/*      */ package com.aelitis.azureus.plugins.net.buddy;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import java.io.File;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class BuddyPluginBuddyMessageHandler
/*      */ {
/*      */   private BuddyPluginBuddy buddy;
/*      */   private File store;
/*      */   private Map config_map;
/*      */   private int message_count;
/*      */   private int pending_deletes;
/*      */   private int next_message_id;
/*   45 */   private CopyOnWriteList listeners = new CopyOnWriteList();
/*      */   
/*      */ 
/*      */   private BuddyPluginBuddyMessage active_message;
/*      */   
/*      */ 
/*      */   private long last_failure;
/*      */   
/*      */   private long last_pending_success;
/*      */   
/*      */ 
/*      */   protected BuddyPluginBuddyMessageHandler(BuddyPluginBuddy _buddy, File _store)
/*      */   {
/*   58 */     this.buddy = _buddy;
/*   59 */     this.store = _store;
/*      */     
/*   61 */     loadConfig();
/*      */     
/*   63 */     if (this.message_count > 0)
/*      */     {
/*   65 */       this.buddy.persistentDispatchPending();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public BuddyPluginBuddy getBuddy()
/*      */   {
/*   72 */     return this.buddy;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public BuddyPluginBuddyMessage queueMessage(int subsystem, Map content, int timeout_millis)
/*      */     throws BuddyPluginException
/*      */   {
/*      */     BuddyPluginBuddyMessage message;
/*      */     
/*      */ 
/*      */     boolean dispatch_pending;
/*      */     
/*      */ 
/*   87 */     synchronized (this)
/*      */     {
/*   89 */       int id = this.next_message_id++;
/*      */       
/*   91 */       message = new BuddyPluginBuddyMessage(this, id, subsystem, content, timeout_millis, SystemTime.getCurrentTime());
/*      */       
/*      */ 
/*      */ 
/*   95 */       storeMessage(message);
/*      */       
/*   97 */       dispatch_pending = this.message_count == 1;
/*      */     }
/*      */     
/*  100 */     Iterator it = this.listeners.iterator();
/*      */     
/*  102 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/*  105 */         ((BuddyPluginBuddyMessageListener)it.next()).messageQueued(message);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  109 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  113 */     if (dispatch_pending)
/*      */     {
/*  115 */       this.buddy.persistentDispatchPending();
/*      */     }
/*      */     
/*  118 */     return message;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkPersistentDispatch()
/*      */   {
/*  124 */     boolean request_dispatch = false;
/*      */     
/*  126 */     synchronized (this)
/*      */     {
/*  128 */       long now = SystemTime.getCurrentTime();
/*      */       
/*  130 */       if (now < this.last_failure)
/*      */       {
/*  132 */         this.last_failure = now;
/*      */       }
/*      */       
/*  135 */       if (now < this.last_pending_success)
/*      */       {
/*  137 */         this.last_pending_success = now;
/*      */       }
/*      */       
/*  140 */       if ((this.last_pending_success > 0L) && (now - this.last_pending_success >= 300000L))
/*      */       {
/*  142 */         request_dispatch = true;
/*      */       }
/*  144 */       else if ((this.active_message == null) && (this.message_count != 0) && (this.last_failure != 0L))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  150 */         request_dispatch = now - this.last_failure >= 300000L;
/*      */       }
/*      */     }
/*      */     
/*  154 */     if (request_dispatch)
/*      */     {
/*  156 */       this.buddy.persistentDispatchPending();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void persistentDispatch()
/*      */   {
/*  163 */     checkPendingSuccess();
/*      */     
/*  165 */     synchronized (this)
/*      */     {
/*  167 */       if ((this.active_message != null) || (this.message_count == 0))
/*      */       {
/*  169 */         return;
/*      */       }
/*      */       
/*  172 */       List messages = (List)this.config_map.get("messages");
/*      */       
/*  174 */       Map map = (Map)messages.get(0);
/*      */       try
/*      */       {
/*  177 */         this.active_message = restoreMessage(map);
/*      */ 
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*      */ 
/*  183 */         Debug.out("Failed to restore message, deleting it", e);
/*      */         
/*  185 */         messages.remove(0);
/*      */         try
/*      */         {
/*  188 */           saveConfig();
/*      */         }
/*      */         catch (Throwable f)
/*      */         {
/*  192 */           this.buddy.log("Config save failed during delete of bad message", f);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  197 */     boolean request_ok = false;
/*      */     try
/*      */     {
/*  200 */       Map request = this.active_message.getRequest();
/*      */       
/*  202 */       request_ok = true;
/*      */       
/*  204 */       this.buddy.sendMessage(this.active_message.getSubsystem(), request, this.active_message.getTimeout(), new BuddyPluginBuddyReplyListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void replyReceived(BuddyPluginBuddy from_buddy, Map reply)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  215 */           BuddyPluginBuddyMessage message = BuddyPluginBuddyMessageHandler.this.active_message;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  220 */           Iterator it = BuddyPluginBuddyMessageHandler.this.listeners.iterator();
/*      */           
/*  222 */           boolean processing_ok = true;
/*      */           
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/*  228 */             synchronized (BuddyPluginBuddyMessageHandler.this)
/*      */             {
/*  230 */               BuddyPluginBuddyMessageHandler.access$208(BuddyPluginBuddyMessageHandler.this);
/*      */             }
/*      */             
/*  233 */             while (it.hasNext()) {
/*      */               try
/*      */               {
/*  236 */                 if (!((BuddyPluginBuddyMessageListener)it.next()).deliverySucceeded(message, reply))
/*      */                 {
/*  238 */                   processing_ok = false;
/*      */                 }
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  243 */                 Debug.printStackTrace(e);
/*      */               }
/*      */             }
/*      */           }
/*      */           finally {
/*  248 */             synchronized (BuddyPluginBuddyMessageHandler.this)
/*      */             {
/*  250 */               BuddyPluginBuddyMessageHandler.access$210(BuddyPluginBuddyMessageHandler.this);
/*      */             }
/*      */           }
/*  253 */           if (processing_ok)
/*      */           {
/*  255 */             message.delete();
/*      */           }
/*      */           else {
/*  258 */             synchronized (BuddyPluginBuddyMessageHandler.this)
/*      */             {
/*  260 */               boolean found = false;
/*      */               
/*  262 */               List messages = (List)BuddyPluginBuddyMessageHandler.this.config_map.get("messages");
/*      */               
/*  264 */               if (messages != null)
/*      */               {
/*  266 */                 for (int i = 0; i < messages.size(); i++)
/*      */                 {
/*  268 */                   Map msg = (Map)messages.get(i);
/*      */                   
/*  270 */                   if (message.getID() == ((Long)msg.get("id")).intValue())
/*      */                   {
/*  272 */                     found = true;
/*      */                     
/*  274 */                     messages.remove(i);
/*      */                     try
/*      */                     {
/*  277 */                       BuddyPluginBuddyMessageHandler.this.writeReply(message, reply);
/*      */                       
/*  279 */                       Object pending_success = (List)BuddyPluginBuddyMessageHandler.this.config_map.get("pending_success");
/*      */                       
/*  281 */                       if (pending_success == null)
/*      */                       {
/*  283 */                         pending_success = new ArrayList();
/*      */                         
/*  285 */                         BuddyPluginBuddyMessageHandler.this.config_map.put("pending_success", pending_success);
/*      */                       }
/*      */                       
/*  288 */                       ((List)pending_success).add(msg);
/*      */                       
/*  290 */                       BuddyPluginBuddyMessageHandler.this.last_pending_success = SystemTime.getCurrentTime();
/*      */                       
/*  292 */                       BuddyPluginBuddyMessageHandler.this.buddy.log("Message moved to pending success queue after listener failed");
/*      */                       
/*  294 */                       BuddyPluginBuddyMessageHandler.this.saveConfig();
/*      */                     }
/*      */                     catch (Throwable e)
/*      */                     {
/*  298 */                       BuddyPluginBuddyMessageHandler.this.buddy.log("Config save failed during message pending queueing", e);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*  306 */               if (!found)
/*      */               {
/*  308 */                 BuddyPluginBuddyMessageHandler.this.buddy.log("Failed to find message " + message.getID());
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */           boolean messages_queued;
/*      */           
/*  315 */           synchronized (BuddyPluginBuddyMessageHandler.this)
/*      */           {
/*  317 */             BuddyPluginBuddyMessageHandler.this.active_message = null;
/*      */             
/*  319 */             messages_queued = BuddyPluginBuddyMessageHandler.this.message_count > 0;
/*      */             
/*  321 */             BuddyPluginBuddyMessageHandler.this.last_failure = 0L;
/*      */           }
/*      */           
/*  324 */           if (messages_queued)
/*      */           {
/*  326 */             BuddyPluginBuddyMessageHandler.this.buddy.persistentDispatchPending();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void sendFailed(BuddyPluginBuddy to_buddy, BuddyPluginException cause)
/*      */         {
/*  335 */           BuddyPluginBuddyMessage message = BuddyPluginBuddyMessageHandler.this.active_message;
/*      */           
/*  337 */           synchronized (BuddyPluginBuddyMessageHandler.this)
/*      */           {
/*  339 */             BuddyPluginBuddyMessageHandler.this.active_message = null;
/*      */             
/*  341 */             BuddyPluginBuddyMessageHandler.this.last_failure = SystemTime.getCurrentTime();
/*      */           }
/*      */           
/*  344 */           BuddyPluginBuddyMessageHandler.this.reportFailed(message, cause, true);
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Throwable cause)
/*      */     {
/*  350 */       BuddyPluginBuddyMessage message = this.active_message;
/*      */       
/*  352 */       synchronized (this)
/*      */       {
/*  354 */         this.active_message = null;
/*      */         
/*  356 */         this.last_failure = SystemTime.getCurrentTime();
/*      */       }
/*      */       
/*  359 */       boolean do_subsequent = true;
/*      */       
/*  361 */       if ((!request_ok) && (!(cause instanceof BuddyPluginPasswordException)))
/*      */       {
/*  363 */         this.buddy.logMessage("Message request unavailable, deleting message");
/*      */         
/*  365 */         message.delete();
/*      */         
/*  367 */         boolean messages_queued = false;
/*      */         
/*  369 */         synchronized (this)
/*      */         {
/*  371 */           this.last_failure = 0L;
/*      */           
/*  373 */           messages_queued = this.message_count > 0;
/*      */         }
/*      */         
/*  376 */         if (messages_queued)
/*      */         {
/*  378 */           do_subsequent = false;
/*      */           
/*  380 */           this.buddy.persistentDispatchPending();
/*      */         }
/*      */       }
/*      */       
/*  384 */       reportFailed(message, cause, do_subsequent);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void reportFailed(BuddyPluginBuddyMessage message, Throwable cause, boolean do_subsequent)
/*      */   {
/*      */     BuddyPluginException b_cause;
/*      */     
/*      */     BuddyPluginException b_cause;
/*      */     
/*  396 */     if ((cause instanceof BuddyPluginException))
/*      */     {
/*  398 */       b_cause = (BuddyPluginException)cause;
/*      */     }
/*      */     else
/*      */     {
/*  402 */       b_cause = new BuddyPluginException("Failed to send message", cause);
/*      */     }
/*      */     
/*  405 */     reportFailedSupport(message, b_cause);
/*      */     
/*  407 */     if (do_subsequent)
/*      */     {
/*  409 */       List other_messages = new ArrayList();
/*      */       
/*  411 */       synchronized (this)
/*      */       {
/*  413 */         List messages = (List)this.config_map.get("messages");
/*      */         
/*  415 */         for (int i = 0; i < messages.size(); i++) {
/*      */           try
/*      */           {
/*  418 */             BuddyPluginBuddyMessage msg = restoreMessage((Map)messages.get(i));
/*      */             
/*  420 */             if (msg.getID() != message.getID())
/*      */             {
/*  422 */               other_messages.add(msg);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  430 */       if (other_messages.size() > 0)
/*      */       {
/*  432 */         BuddyPluginException o_cause = new BuddyPluginException("Reporting probable failure to subsequent messages");
/*      */         
/*  434 */         for (int i = 0; i < other_messages.size(); i++)
/*      */         {
/*  436 */           reportFailedSupport((BuddyPluginBuddyMessage)other_messages.get(i), o_cause);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void reportFailedSupport(BuddyPluginBuddyMessage message, BuddyPluginException cause)
/*      */   {
/*  447 */     Iterator it = this.listeners.iterator();
/*      */     
/*  449 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/*  452 */         ((BuddyPluginBuddyMessageListener)it.next()).deliveryFailed(message, cause);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  456 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkPendingSuccess()
/*      */   {
/*  464 */     List pending_messages = new ArrayList();
/*      */     
/*  466 */     boolean save_pending = false;
/*      */     
/*  468 */     synchronized (this)
/*      */     {
/*  470 */       this.last_pending_success = 0L;
/*      */       
/*  472 */       List pending_success = (List)this.config_map.get("pending_success");
/*      */       
/*  474 */       if ((pending_success == null) || (pending_success.size() == 0))
/*      */       {
/*  476 */         return;
/*      */       }
/*      */       
/*  479 */       Iterator it = pending_success.iterator();
/*      */       
/*  481 */       while (it.hasNext())
/*      */       {
/*  483 */         Map map = (Map)it.next();
/*      */         try
/*      */         {
/*  486 */           pending_messages.add(restoreMessage(map));
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  490 */           this.buddy.log("Failed to restore message from pending success queue", e);
/*      */           
/*  492 */           it.remove();
/*      */           
/*  494 */           save_pending = true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  499 */     for (int i = 0; i < pending_messages.size(); i++)
/*      */     {
/*  501 */       BuddyPluginBuddyMessage message = (BuddyPluginBuddyMessage)pending_messages.get(i);
/*      */       try
/*      */       {
/*  504 */         Map reply = message.getReply();
/*      */         
/*  506 */         Iterator it = this.listeners.iterator();
/*      */         
/*  508 */         boolean processing_ok = true;
/*      */         
/*  510 */         while (it.hasNext()) {
/*      */           try
/*      */           {
/*  513 */             if (!((BuddyPluginBuddyMessageListener)it.next()).deliverySucceeded(message, reply))
/*      */             {
/*  515 */               processing_ok = false;
/*      */             }
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  520 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */         
/*  524 */         if (processing_ok)
/*      */         {
/*  526 */           message.delete();
/*      */         }
/*      */         else {
/*  529 */           synchronized (this)
/*      */           {
/*  531 */             this.last_pending_success = SystemTime.getCurrentTime();
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (BuddyPluginPasswordException e) {
/*  536 */         this.buddy.log("Failed to restore message reply", e);
/*      */ 
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*      */ 
/*  542 */         this.buddy.log("Failed to restore message reply - deleting message", e);
/*      */         
/*  544 */         message.delete();
/*      */       }
/*      */     }
/*      */     
/*  548 */     if (save_pending) {
/*      */       try
/*      */       {
/*  551 */         saveConfig();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  555 */         this.buddy.log("Save failed during pending success processing", e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public int getMessageCount()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_1
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: getfield 434	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddyMessageHandler:message_count	I
/*      */     //   8: aload_0
/*      */     //   9: getfield 436	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddyMessageHandler:pending_deletes	I
/*      */     //   12: isub
/*      */     //   13: aload_1
/*      */     //   14: monitorexit
/*      */     //   15: ireturn
/*      */     //   16: astore_2
/*      */     //   17: aload_1
/*      */     //   18: monitorexit
/*      */     //   19: aload_2
/*      */     //   20: athrow
/*      */     // Line number table:
/*      */     //   Java source line #563	-> byte code offset #0
/*      */     //   Java source line #565	-> byte code offset #4
/*      */     //   Java source line #566	-> byte code offset #16
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	21	0	this	BuddyPluginBuddyMessageHandler
/*      */     //   2	16	1	Ljava/lang/Object;	Object
/*      */     //   16	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	15	16	finally
/*      */     //   16	19	16	finally
/*      */   }
/*      */   
/*      */   protected void deleteMessage(BuddyPluginBuddyMessage message)
/*      */   {
/*  573 */     Iterator it = this.listeners.iterator();
/*      */     
/*  575 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/*  578 */         ((BuddyPluginBuddyMessageListener)it.next()).messageDeleted(message);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  582 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  586 */     synchronized (this)
/*      */     {
/*  588 */       String[] keys = { "messages", "pending_success", "explicit" };
/*      */       
/*  590 */       for (int i = 0; i < keys.length; i++)
/*      */       {
/*  592 */         List messages = (List)this.config_map.get(keys[i]);
/*      */         
/*  594 */         if (messages != null)
/*      */         {
/*  596 */           boolean found = false;
/*      */           
/*  598 */           for (int j = 0; j < messages.size(); j++)
/*      */           {
/*  600 */             Map msg = (Map)messages.get(j);
/*      */             
/*  602 */             if (message.getID() == ((Long)msg.get("id")).intValue())
/*      */             {
/*  604 */               messages.remove(j);
/*      */               
/*  606 */               found = true;
/*      */               
/*  608 */               break;
/*      */             }
/*      */           }
/*      */           
/*  612 */           if (found)
/*      */           {
/*  614 */             deleteRequest(message);
/*      */             
/*  616 */             deleteReply(message);
/*      */             try
/*      */             {
/*  619 */               saveConfig();
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  623 */               this.buddy.log("Config save failed during message delete", e);
/*      */             }
/*      */             
/*  626 */             return;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void destroy()
/*      */   {
/*  636 */     synchronized (this)
/*      */     {
/*  638 */       this.config_map.clear();
/*      */       try
/*      */       {
/*  641 */         saveConfig();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  645 */         this.buddy.log("Config save failed during destroy", e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void writeRequest(BuddyPluginBuddyMessage message, Map content)
/*      */     throws BuddyPluginException
/*      */   {
/*  657 */     writeContent(message.getID() + ".req.dat", content);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Map readRequest(BuddyPluginBuddyMessage message)
/*      */     throws BuddyPluginException
/*      */   {
/*  666 */     return readContent(message.getID() + ".req.dat");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void writeReply(BuddyPluginBuddyMessage message, Map content)
/*      */     throws BuddyPluginException
/*      */   {
/*  676 */     writeContent(message.getID() + ".rep.dat", content);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Map readReply(BuddyPluginBuddyMessage message)
/*      */     throws BuddyPluginException
/*      */   {
/*  685 */     return readContent(message.getID() + ".rep.dat");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void writeContent(String target_str, Map content)
/*      */     throws BuddyPluginException
/*      */   {
/*  695 */     if (!this.store.exists())
/*      */     {
/*  697 */       if (!this.store.mkdirs())
/*      */       {
/*  699 */         throw new BuddyPluginException("Failed to create " + this.store);
/*      */       }
/*      */     }
/*      */     
/*  703 */     File target = new File(this.store, target_str);
/*      */     
/*      */     try
/*      */     {
/*  707 */       BuddyPlugin.cryptoResult result = this.buddy.encrypt(BEncoder.encode(content));
/*      */       
/*  709 */       Map store_map = new HashMap();
/*      */       
/*  711 */       store_map.put("pk", this.buddy.getPlugin().getPublicKey());
/*  712 */       store_map.put("data", result.getPayload());
/*      */       
/*  714 */       if (!this.buddy.writeConfigFile(target, store_map))
/*      */       {
/*  716 */         throw new BuddyPluginException("failed to write " + target);
/*      */       }
/*      */     }
/*      */     catch (BuddyPluginException e)
/*      */     {
/*  721 */       throw e;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  725 */       throw new BuddyPluginException("Failed to write message", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Map readContent(String target_str)
/*      */     throws BuddyPluginException
/*      */   {
/*  735 */     File target = new File(this.store, target_str);
/*      */     
/*  737 */     if (!target.exists())
/*      */     {
/*  739 */       throw new BuddyPluginException("Failed to read persisted message - " + target + " doesn't exist");
/*      */     }
/*      */     
/*  742 */     Map map = this.buddy.readConfigFile(target);
/*      */     
/*  744 */     if (map.size() == 0)
/*      */     {
/*  746 */       throw new BuddyPluginException("Failed to read persisted message file " + target);
/*      */     }
/*      */     try
/*      */     {
/*  750 */       String pk = new String((byte[])map.get("pk"));
/*      */       
/*  752 */       if (!pk.equals(this.buddy.getPlugin().getPublicKey()))
/*      */       {
/*  754 */         throw new BuddyPluginException("Can't decrypt message as key changed");
/*      */       }
/*      */       
/*  757 */       byte[] data = (byte[])map.get("data");
/*      */       
/*  759 */       return BDecoder.decode(this.buddy.decrypt(data).getPayload());
/*      */     }
/*      */     catch (BuddyPluginException e)
/*      */     {
/*  763 */       throw e;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  767 */       throw new BuddyPluginException("Failed to read message", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void deleteRequest(BuddyPluginBuddyMessage message)
/*      */   {
/*  775 */     deleteRequest(message.getID());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void deleteRequest(int id)
/*      */   {
/*  782 */     File target = new File(this.store, id + ".req.dat");
/*      */     
/*  784 */     if (target.exists())
/*      */     {
/*  786 */       if (!target.delete())
/*      */       {
/*  788 */         Debug.out("Failed to delete " + target);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void deleteReply(BuddyPluginBuddyMessage message)
/*      */   {
/*  797 */     deleteReply(message.getID());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void deleteReply(int id)
/*      */   {
/*  804 */     File target = new File(this.store, id + ".rep.dat");
/*      */     
/*  806 */     if (target.exists())
/*      */     {
/*  808 */       if (!target.delete())
/*      */       {
/*  810 */         Debug.out("Failed to delete " + target);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public BuddyPluginBuddyMessage storeExplicitMessage(int type, Map msg)
/*      */   {
/*      */     BuddyPluginBuddyMessage message;
/*      */     
/*      */ 
/*  822 */     synchronized (this)
/*      */     {
/*  824 */       int id = this.next_message_id++;
/*      */       try
/*      */       {
/*  827 */         message = new BuddyPluginBuddyMessage(this, id, 1024 + type, msg, 0, SystemTime.getCurrentTime());
/*      */         
/*      */ 
/*      */ 
/*  831 */         storeExplicitMessage(message);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  835 */         this.buddy.log("Failed to store explicit message", e);
/*      */         
/*  837 */         return null;
/*      */       }
/*      */     }
/*      */     
/*  841 */     return message;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List<BuddyPluginBuddyMessage> retrieveExplicitMessages(int type)
/*      */   {
/*  848 */     List<BuddyPluginBuddyMessage> result = new ArrayList();
/*      */     
/*  850 */     synchronized (this)
/*      */     {
/*  852 */       List<Map<String, Object>> messages = (List)this.config_map.get("explicit");
/*      */       
/*  854 */       if (messages != null)
/*      */       {
/*  856 */         for (int i = 0; i < messages.size(); i++) {
/*      */           try
/*      */           {
/*  859 */             BuddyPluginBuddyMessage msg = restoreMessage((Map)messages.get(i));
/*      */             
/*  861 */             if (msg.getSubsystem() == 1024 + type)
/*      */             {
/*  863 */               result.add(msg);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/*  867 */             this.buddy.log("Failed to restore message", e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  873 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void storeExplicitMessage(BuddyPluginBuddyMessage msg)
/*      */     throws BuddyPluginException
/*      */   {
/*  882 */     storeMessageSupport(msg, "explicit");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void storeMessage(BuddyPluginBuddyMessage msg)
/*      */     throws BuddyPluginException
/*      */   {
/*  891 */     storeMessageSupport(msg, "messages");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void storeMessageSupport(BuddyPluginBuddyMessage msg, String key)
/*      */     throws BuddyPluginException
/*      */   {
/*  901 */     List messages = (List)this.config_map.get(key);
/*      */     
/*  903 */     if (messages == null)
/*      */     {
/*  905 */       messages = new ArrayList();
/*      */       
/*  907 */       this.config_map.put(key, messages);
/*      */     }
/*      */     
/*  910 */     Map map = new HashMap();
/*      */     
/*  912 */     map.put("id", new Long(msg.getID()));
/*  913 */     map.put("ss", new Long(msg.getSubsystem()));
/*  914 */     map.put("to", new Long(msg.getTimeout()));
/*  915 */     map.put("cr", new Long(msg.getCreateTime()));
/*      */     
/*  917 */     messages.add(map);
/*      */     
/*  919 */     saveConfig();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected BuddyPluginBuddyMessage restoreMessage(Map map)
/*      */     throws BuddyPluginException
/*      */   {
/*  928 */     int id = ((Long)map.get("id")).intValue();
/*  929 */     int ss = ((Long)map.get("ss")).intValue();
/*  930 */     int to = ((Long)map.get("to")).intValue();
/*      */     
/*  932 */     long cr = ((Long)map.get("cr")).longValue();
/*      */     
/*  934 */     return new BuddyPluginBuddyMessage(this, id, ss, null, to, cr);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void loadConfig()
/*      */   {
/*  940 */     File config_file = new File(this.store, "messages.dat");
/*      */     
/*  942 */     if (config_file.exists())
/*      */     {
/*  944 */       this.config_map = this.buddy.readConfigFile(config_file);
/*      */     }
/*      */     else
/*      */     {
/*  948 */       this.config_map = new HashMap();
/*      */     }
/*      */     
/*  951 */     List messages = (List)this.config_map.get("messages");
/*      */     
/*  953 */     if (messages != null)
/*      */     {
/*  955 */       this.message_count = messages.size();
/*      */       
/*  957 */       if (this.message_count > 0)
/*      */       {
/*  959 */         Map last_msg = (Map)messages.get(this.message_count - 1);
/*      */         
/*  961 */         this.next_message_id = (((Long)last_msg.get("id")).intValue() + 1);
/*      */       }
/*      */     }
/*      */     
/*  965 */     List pending_success = (List)this.config_map.get("pending_success");
/*      */     
/*  967 */     if (pending_success != null)
/*      */     {
/*  969 */       int ps_count = pending_success.size();
/*      */       
/*  971 */       if (ps_count > 0)
/*      */       {
/*  973 */         Map last_msg = (Map)pending_success.get(ps_count - 1);
/*      */         
/*  975 */         this.next_message_id = Math.max(this.next_message_id, ((Long)last_msg.get("id")).intValue() + 1);
/*      */         
/*  977 */         synchronized (this)
/*      */         {
/*  979 */           this.last_pending_success = SystemTime.getCurrentTime();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  984 */     List explicit = (List)this.config_map.get("explicit");
/*      */     
/*  986 */     if (explicit != null)
/*      */     {
/*  988 */       int exp_count = explicit.size();
/*      */       
/*  990 */       if (exp_count > 0)
/*      */       {
/*  992 */         Map last_msg = (Map)explicit.get(exp_count - 1);
/*      */         
/*  994 */         this.next_message_id = Math.max(this.next_message_id, ((Long)last_msg.get("id")).intValue() + 1);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void saveConfig()
/*      */     throws BuddyPluginException
/*      */   {
/* 1004 */     File config_file = new File(this.store, "messages.dat");
/*      */     
/* 1006 */     List messages = (List)this.config_map.get("messages");
/* 1007 */     List pending = (List)this.config_map.get("pending_success");
/* 1008 */     List explicit = (List)this.config_map.get("explicit");
/*      */     
/* 1010 */     if (((messages == null) || (messages.size() == 0)) && ((pending == null) || (pending.size() == 0)) && ((explicit == null) || (explicit.size() == 0)))
/*      */     {
/*      */ 
/*      */ 
/* 1014 */       if (this.store.exists())
/*      */       {
/* 1016 */         File[] files = this.store.listFiles();
/*      */         
/* 1018 */         for (int i = 0; i < files.length; i++)
/*      */         {
/* 1020 */           files[i].delete();
/*      */         }
/*      */         
/* 1023 */         this.store.delete();
/*      */       }
/*      */       
/* 1026 */       this.message_count = 0;
/*      */       
/* 1028 */       this.next_message_id = 0;
/*      */     }
/*      */     else
/*      */     {
/* 1032 */       if (!this.store.exists())
/*      */       {
/* 1034 */         if (!this.store.mkdirs())
/*      */         {
/* 1036 */           throw new BuddyPluginException("Failed to create " + this.store);
/*      */         }
/*      */       }
/*      */       
/* 1040 */       if (!this.buddy.writeConfigFile(config_file, this.config_map))
/*      */       {
/* 1042 */         throw new BuddyPluginException("Failed to write" + config_file);
/*      */       }
/*      */       
/* 1045 */       this.message_count = (messages == null ? 0 : messages.size());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(BuddyPluginBuddyMessageListener listener)
/*      */   {
/* 1053 */     this.listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(BuddyPluginBuddyMessageListener listener)
/*      */   {
/* 1060 */     this.listeners.remove(listener);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddyMessageHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */