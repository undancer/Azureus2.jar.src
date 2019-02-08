/*     */ package com.aelitis.azureus.core.clientmessageservice.secure.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.clientmessageservice.ClientMessageService;
/*     */ import com.aelitis.azureus.core.clientmessageservice.secure.SecureMessageServiceClient;
/*     */ import com.aelitis.azureus.core.clientmessageservice.secure.SecureMessageServiceClientAdapter;
/*     */ import com.aelitis.azureus.core.clientmessageservice.secure.SecureMessageServiceClientListener;
/*     */ import com.aelitis.azureus.core.clientmessageservice.secure.SecureMessageServiceClientMessage;
/*     */ import java.security.interfaces.RSAPublicKey;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SecureMessageServiceClientImpl
/*     */   implements SecureMessageServiceClient
/*     */ {
/*     */   public static final int STATUS_OK = 0;
/*     */   public static final int STATUS_LOGON_FAIL = 1;
/*     */   public static final int STATUS_INVALID_SEQUENCE = 2;
/*     */   public static final int STATUS_FAILED = 3;
/*     */   public static final int STATUS_ABORT = 4;
/*     */   public static final String SERVICE_NAME = "SecureMsgServ";
/*     */   private static final long MIN_RETRY_PERIOD = 300000L;
/*     */   private static final long MAX_RETRY_PERIOD = 7200000L;
/*     */   private final String host;
/*     */   private final int port;
/*     */   private final int timeout_secs;
/*     */   private final RSAPublicKey public_key;
/*     */   final SecureMessageServiceClientAdapter adapter;
/*  60 */   private long retry_millis = 300000L;
/*  61 */   private int connect_failure_count = 0;
/*     */   
/*     */   private final AEMonitor message_mon;
/*     */   
/*     */   final AESemaphore message_sem;
/*     */   
/*  67 */   private String last_failed_user_pw = "";
/*     */   
/*     */   private long last_failed_user_pw_time;
/*  70 */   private final List messages = new ArrayList();
/*  71 */   private final List listeners = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SecureMessageServiceClientImpl(String _host, int _port, int _timeout_secs, RSAPublicKey _key, SecureMessageServiceClientAdapter _adapter)
/*     */   {
/*  81 */     this.host = _host;
/*  82 */     this.port = _port;
/*  83 */     this.timeout_secs = _timeout_secs;
/*  84 */     this.public_key = _key;
/*  85 */     this.adapter = _adapter;
/*     */     
/*  87 */     this.message_mon = new AEMonitor("SecureService:messages");
/*     */     
/*  89 */     this.message_sem = new AESemaphore("SecureService:messages");
/*     */     
/*  91 */     new AEThread("SecureService::messageSender", true)
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */         for (;;)
/*     */         {
/*  98 */           long time = SecureMessageServiceClientImpl.this.retry_millis;
/*     */           
/* 100 */           if (SecureMessageServiceClientImpl.this.connect_failure_count > 0)
/*     */           {
/* 102 */             for (int i = 0; i < SecureMessageServiceClientImpl.this.connect_failure_count; i++)
/*     */             {
/* 104 */               time += time;
/*     */               
/* 106 */               if (time > 7200000L)
/*     */               {
/* 108 */                 time = 7200000L;
/*     */                 
/* 110 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 115 */           SecureMessageServiceClientImpl.this.message_sem.reserve(time);
/*     */           try
/*     */           {
/* 118 */             SecureMessageServiceClientImpl.this.sendMessagesSupport();
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 122 */             SecureMessageServiceClientImpl.this.adapter.log("Request processing failed", e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */   public void sendMessages()
/*     */   {
/* 132 */     this.message_sem.release();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void sendMessagesSupport()
/*     */   {
/* 138 */     String user = this.adapter.getUser();
/* 139 */     byte[] password = this.adapter.getPassword();
/*     */     
/* 141 */     String user_password = user + "/" + new String(password);
/*     */     
/*     */ 
/*     */ 
/* 145 */     if (user.length() == 0)
/*     */     {
/* 147 */       this.adapter.authenticationFailed();
/*     */       
/* 149 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 155 */     if (user_password.equals(this.last_failed_user_pw))
/*     */     {
/* 157 */       long now = SystemTime.getCurrentTime();
/*     */       
/* 159 */       if ((now > this.last_failed_user_pw_time) && (now - this.last_failed_user_pw_time < 60000L))
/*     */       {
/* 161 */         this.adapter.authenticationFailed(); return;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     List outstanding_messages;
/*     */     
/*     */     try
/*     */     {
/* 170 */       this.message_mon.enter();
/*     */       
/* 172 */       outstanding_messages = new ArrayList(this.messages);
/*     */     }
/*     */     finally
/*     */     {
/* 176 */       this.message_mon.exit();
/*     */     }
/*     */     
/* 179 */     if (outstanding_messages.size() == 0)
/*     */     {
/* 181 */       return;
/*     */     }
/*     */     
/* 184 */     List complete_messages = new ArrayList();
/*     */     
/* 186 */     boolean failed = false;
/*     */     try
/*     */     {
/* 189 */       Iterator it = outstanding_messages.iterator();
/*     */       
/* 191 */       while ((it.hasNext()) && (!failed))
/*     */       {
/* 193 */         SecureMessageServiceClientMessageImpl message = (SecureMessageServiceClientMessageImpl)it.next();
/*     */         
/* 195 */         boolean retry = true;
/* 196 */         int retry_count = 0;
/*     */         
/* 198 */         while ((retry) && (!failed))
/*     */         {
/* 200 */           retry = false;
/*     */           
/* 202 */           ClientMessageService message_service = null;
/*     */           
/* 204 */           boolean got_reply = false;
/*     */           try
/*     */           {
/* 207 */             Map content = new HashMap();
/*     */             
/* 209 */             long sequence = this.adapter.getMessageSequence();
/*     */             
/* 211 */             content.put("user", user);
/* 212 */             content.put("password", password);
/* 213 */             content.put("seq", new Long(sequence));
/* 214 */             content.put("request", message.getRequest());
/*     */             
/* 216 */             this.last_failed_user_pw = "";
/*     */             
/* 218 */             message_service = SecureMessageServiceClientHelper.getServerService(this.host, this.port, this.timeout_secs, "SecureMsgServ", this.public_key);
/*     */             
/* 220 */             message_service.sendMessage(content);
/*     */             
/* 222 */             Map reply = message_service.receiveMessage();
/*     */             
/* 224 */             got_reply = true;
/*     */             
/* 226 */             long status = ((Long)reply.get("status")).longValue();
/*     */             
/* 228 */             Long new_retry = (Long)reply.get("retry");
/*     */             
/* 230 */             if (new_retry != null)
/*     */             {
/* 232 */               this.retry_millis = new_retry.longValue();
/*     */               
/* 234 */               if (this.retry_millis < 300000L)
/*     */               {
/* 236 */                 this.retry_millis = 300000L;
/*     */               }
/*     */               
/* 239 */               this.adapter.log("Server requested retry period of " + this.retry_millis / 1000L + " seconds");
/*     */             }
/*     */             else
/*     */             {
/* 243 */               this.retry_millis = 300000L;
/*     */             }
/*     */             
/* 246 */             if (status == 0L)
/*     */             {
/* 248 */               message.setReply((Map)reply.get("reply"));
/*     */               
/* 250 */               this.adapter.log("Request successfully sent: " + message.getRequest() + "->" + message.getReply());
/*     */               
/* 252 */               this.adapter.setMessageSequence(sequence + 1L);
/*     */               
/* 254 */               this.adapter.serverOK();
/*     */               
/* 256 */               for (Iterator l_it = this.listeners.iterator(); l_it.hasNext();) {
/*     */                 try
/*     */                 {
/* 259 */                   ((SecureMessageServiceClientListener)l_it.next()).complete(message);
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 263 */                   e.printStackTrace();
/*     */                 }
/*     */               }
/*     */               
/* 267 */               complete_messages.add(message);
/*     */             }
/* 269 */             else if (status == 1L)
/*     */             {
/* 271 */               this.last_failed_user_pw = user_password;
/* 272 */               this.last_failed_user_pw_time = SystemTime.getCurrentTime();
/*     */               
/* 274 */               this.adapter.serverOK();
/*     */               
/* 276 */               this.adapter.authenticationFailed();
/*     */               
/* 278 */               failed = true;
/*     */             }
/* 280 */             else if (status == 2L)
/*     */             {
/* 282 */               if (retry_count == 1)
/*     */               {
/* 284 */                 this.adapter.serverFailed(new Exception("Sequence resynchronisation failed"));
/*     */                 
/* 286 */                 failed = true;
/*     */               }
/*     */               else
/*     */               {
/* 290 */                 retry_count++;
/*     */                 
/* 292 */                 retry = true;
/*     */                 
/* 294 */                 long expected_sequence = ((Long)reply.get("seq")).longValue();
/*     */                 
/* 296 */                 this.adapter.log("Sequence resynchronise: local = " + sequence + ", remote = " + expected_sequence);
/*     */                 
/* 298 */                 this.adapter.setMessageSequence(expected_sequence);
/*     */               }
/*     */             }
/* 301 */             else if (status == 3L)
/*     */             {
/* 303 */               this.adapter.serverFailed(new Exception(new String((byte[])reply.get("error"))));
/*     */               
/* 305 */               failed = true;
/*     */ 
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/*     */ 
/* 312 */               this.adapter.serverFailed(new Exception("Server requested abort"));
/*     */               
/* 314 */               for (Iterator l_it = this.listeners.iterator(); l_it.hasNext();) {
/*     */                 try
/*     */                 {
/* 317 */                   ((SecureMessageServiceClientListener)l_it.next()).aborted(message, new String((byte[])reply.get("error")));
/*     */ 
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/*     */ 
/* 323 */                   e.printStackTrace();
/*     */                 }
/*     */               }
/*     */               
/* 327 */               complete_messages.add(message);
/*     */             }
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 332 */             this.adapter.serverFailed(e);
/*     */             
/* 334 */             failed = true;
/*     */           }
/*     */           finally
/*     */           {
/* 338 */             if (got_reply)
/*     */             {
/* 340 */               this.connect_failure_count = 0;
/*     */             }
/*     */             else
/*     */             {
/* 344 */               this.connect_failure_count += 1;
/*     */               
/* 346 */               if (this.connect_failure_count > 1) {
/*     */                 try
/*     */                 {
/* 349 */                   this.adapter.log("Failed to contact server " + this.connect_failure_count + " times in a row");
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 353 */                   e.printStackTrace();
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 358 */             if (message_service != null)
/*     */             {
/* 360 */               message_service.close();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 367 */       this.adapter.serverFailed(e);
/*     */     }
/*     */     finally
/*     */     {
/*     */       try {
/* 372 */         this.message_mon.enter();
/*     */         
/* 374 */         this.messages.removeAll(complete_messages);
/*     */       }
/*     */       finally
/*     */       {
/* 378 */         this.message_mon.exit();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public SecureMessageServiceClientMessage sendMessage(Map request, Object data, String description)
/*     */   {
/*     */     try
/*     */     {
/* 390 */       this.message_mon.enter();
/*     */       
/* 392 */       SecureMessageServiceClientMessage res = new SecureMessageServiceClientMessageImpl(this, request, data, description);
/*     */       
/* 394 */       this.messages.add(res);
/*     */       
/* 396 */       this.message_sem.release();
/*     */       
/* 398 */       return res;
/*     */     }
/*     */     finally
/*     */     {
/* 402 */       this.message_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void cancel(SecureMessageServiceClientMessage message)
/*     */   {
/* 410 */     boolean inform = false;
/*     */     try
/*     */     {
/* 413 */       this.message_mon.enter();
/*     */       
/* 415 */       if (this.messages.remove(message))
/*     */       {
/* 417 */         inform = true;
/*     */       }
/*     */     }
/*     */     finally {
/* 421 */       this.message_mon.exit();
/*     */     }
/*     */     Iterator it;
/* 424 */     if (inform)
/*     */     {
/* 426 */       for (it = this.listeners.iterator(); it.hasNext();) {
/*     */         try
/*     */         {
/* 429 */           ((SecureMessageServiceClientListener)it.next()).cancelled(message);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 433 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public SecureMessageServiceClientMessage[] getMessages()
/*     */   {
/*     */     try
/*     */     {
/* 443 */       this.message_mon.enter();
/*     */       
/* 445 */       return (SecureMessageServiceClientMessage[])this.messages.toArray(new SecureMessageServiceClientMessage[this.messages.size()]);
/*     */     }
/*     */     finally
/*     */     {
/* 449 */       this.message_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(SecureMessageServiceClientListener l)
/*     */   {
/* 457 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(SecureMessageServiceClientListener l)
/*     */   {
/* 464 */     this.listeners.remove(l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/clientmessageservice/secure/impl/SecureMessageServiceClientImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */