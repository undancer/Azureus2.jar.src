/*     */ package org.gudy.azureus2.pluginsimpl.local.network;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.IncomingMessageQueue.MessageQueueListener;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTMessage;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import org.gudy.azureus2.plugins.messaging.bittorrent.BTMessageManager;
/*     */ import org.gudy.azureus2.plugins.network.IncomingMessageQueueListener;
/*     */ import org.gudy.azureus2.pluginsimpl.local.messaging.MessageAdapter;
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
/*     */ public class IncomingMessageQueueImpl
/*     */   implements org.gudy.azureus2.plugins.network.IncomingMessageQueue
/*     */ {
/*     */   private final com.aelitis.azureus.core.networkmanager.IncomingMessageQueue core_queue;
/*  39 */   private final HashMap registrations = new HashMap();
/*     */   
/*     */   protected IncomingMessageQueueImpl(com.aelitis.azureus.core.networkmanager.IncomingMessageQueue core_queue)
/*     */   {
/*  43 */     this.core_queue = core_queue;
/*     */   }
/*     */   
/*     */   public void registerListener(IncomingMessageQueueListener listener) {
/*  47 */     registerListenerSupport(listener, false);
/*     */   }
/*     */   
/*     */   public void registerPriorityListener(IncomingMessageQueueListener listener) {
/*  51 */     registerListenerSupport(listener, true);
/*     */   }
/*     */   
/*     */   private void registerListenerSupport(final IncomingMessageQueueListener listener, final boolean is_priority) {
/*  55 */     IncomingMessageQueue.MessageQueueListener core_listener = new IncomingMessageQueue.MessageQueueListener()
/*     */     {
/*     */       public boolean messageReceived(com.aelitis.azureus.core.peermanager.messaging.Message message) {
/*  58 */         if ((message instanceof MessageAdapter))
/*     */         {
/*     */ 
/*  61 */           return listener.messageReceived(((MessageAdapter)message).getPluginMessage());
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*  66 */         if ((message instanceof BTMessage))
/*     */         {
/*  68 */           return listener.messageReceived(BTMessageManager.wrapCoreMessage((BTMessage)message));
/*     */         }
/*     */         
/*     */ 
/*  72 */         return listener.messageReceived(new MessageAdapter(message));
/*     */       }
/*     */       
/*     */ 
/*  76 */       public void protocolBytesReceived(int byte_count) { listener.bytesReceived(byte_count); }
/*     */       
/*  78 */       public void dataBytesReceived(int byte_count) { listener.bytesReceived(byte_count); }
/*     */       
/*     */ 
/*     */       public boolean isPriority()
/*     */       {
/*  83 */         return is_priority;
/*     */       }
/*     */       
/*  86 */     };
/*  87 */     this.registrations.put(listener, core_listener);
/*     */     
/*  89 */     this.core_queue.registerQueueListener(core_listener);
/*     */   }
/*     */   
/*     */ 
/*     */   public void deregisterListener(IncomingMessageQueueListener listener)
/*     */   {
/*  95 */     IncomingMessageQueue.MessageQueueListener core_listener = (IncomingMessageQueue.MessageQueueListener)this.registrations.remove(listener);
/*     */     
/*     */ 
/*  98 */     if (core_listener != null) {
/*  99 */       this.core_queue.cancelQueueListener(core_listener);
/*     */     }
/*     */   }
/*     */   
/*     */   public void notifyOfExternalReceive(org.gudy.azureus2.plugins.messaging.Message message) throws IOException
/*     */   {
/* 105 */     if ((message instanceof MessageAdapter))
/*     */     {
/*     */ 
/* 108 */       this.core_queue.notifyOfExternallyReceivedMessage(((MessageAdapter)message).getCoreMessage());
/* 109 */       return;
/*     */     }
/*     */     
/*     */ 
/* 113 */     this.core_queue.notifyOfExternallyReceivedMessage(new MessageAdapter(message));
/*     */   }
/*     */   
/*     */   public int getPercentDoneOfCurrentMessage() {
/* 117 */     return this.core_queue.getPercentDoneOfCurrentMessage();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/network/IncomingMessageQueueImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */