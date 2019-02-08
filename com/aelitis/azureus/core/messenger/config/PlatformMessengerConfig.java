/*     */ package com.aelitis.azureus.core.messenger.config;
/*     */ 
/*     */ import com.aelitis.azureus.core.messenger.PlatformMessage;
/*     */ import com.aelitis.azureus.core.messenger.PlatformMessenger;
/*     */ import com.aelitis.azureus.core.messenger.PlatformMessengerException;
/*     */ import com.aelitis.azureus.core.messenger.PlatformMessengerListener;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
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
/*     */ 
/*     */ public class PlatformMessengerConfig
/*     */ {
/*     */   private String listener_id;
/*     */   private boolean send_azid;
/*     */   
/*     */   protected PlatformMessengerConfig(String _listener_id, boolean _send_azid)
/*     */   {
/*  43 */     this.listener_id = _listener_id;
/*  44 */     this.send_azid = _send_azid;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Map syncInvoke(String operationID, Map parameters)
/*     */     throws PlatformMessengerException
/*     */   {
/*  54 */     return syncInvoke(operationID, parameters, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Map syncInvoke(String operationID, Map parameters, boolean forceProxy)
/*     */     throws PlatformMessengerException
/*     */   {
/*  65 */     PlatformMessage message = new PlatformMessage("AZMSG", this.listener_id, operationID, parameters, 0L);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  73 */     if (!this.send_azid)
/*     */     {
/*  75 */       message.setSendAZID(false);
/*     */     }
/*     */     
/*  78 */     message.setForceProxy(forceProxy);
/*     */     
/*  80 */     final AESemaphore sem = new AESemaphore("PlatformMessengerConfig:syncInvoke");
/*     */     
/*  82 */     final Object[] result = { null };
/*     */     
/*  84 */     PlatformMessenger.queueMessage(message, new PlatformMessengerListener()
/*     */     {
/*     */       public void messageSent(PlatformMessage message) {}
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
/*     */       public void replyReceived(PlatformMessage message, String replyType, Map reply)
/*     */       {
/*     */         try
/*     */         {
/* 101 */           if (replyType.equals(PlatformMessenger.REPLY_EXCEPTION))
/*     */           {
/* 103 */             String e_message = (String)reply.get("message");
/*     */             
/* 105 */             if (e_message != null)
/*     */             {
/* 107 */               result[0] = new PlatformMessengerException(e_message);
/*     */             }
/*     */             else
/*     */             {
/* 111 */               String text = (String)reply.get("text");
/*     */               
/* 113 */               Throwable e = (Throwable)reply.get("Throwable");
/*     */               
/* 115 */               if ((text == null) && (e == null))
/*     */               {
/* 117 */                 result[0] = new PlatformMessengerException("Unknown error");
/*     */               }
/* 119 */               else if (text == null)
/*     */               {
/* 121 */                 result[0] = new PlatformMessengerException("Failed to send RPC", e);
/*     */               }
/* 123 */               else if (e == null)
/*     */               {
/* 125 */                 result[0] = new PlatformMessengerException(text);
/*     */               }
/*     */               else
/*     */               {
/* 129 */                 result[0] = new PlatformMessengerException(text, e);
/*     */               }
/*     */             }
/*     */           }
/*     */           else {
/* 134 */             result[0] = reply;
/*     */           }
/*     */         }
/*     */         finally {
/* 138 */           sem.release();
/*     */         }
/*     */         
/*     */       }
/* 142 */     });
/* 143 */     sem.reserve();
/*     */     
/* 145 */     if ((result[0] instanceof PlatformMessengerException))
/*     */     {
/* 147 */       throw ((PlatformMessengerException)result[0]);
/*     */     }
/*     */     
/* 150 */     return (Map)result[0];
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/messenger/config/PlatformMessengerConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */