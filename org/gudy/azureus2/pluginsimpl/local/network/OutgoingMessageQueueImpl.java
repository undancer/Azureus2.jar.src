/*     */ package org.gudy.azureus2.pluginsimpl.local.network;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue.MessageQueueListener;
/*     */ import java.util.HashMap;
/*     */ import org.gudy.azureus2.plugins.messaging.MessageStreamEncoder;
/*     */ import org.gudy.azureus2.plugins.network.OutgoingMessageQueueListener;
/*     */ import org.gudy.azureus2.pluginsimpl.local.messaging.MessageAdapter;
/*     */ import org.gudy.azureus2.pluginsimpl.local.messaging.MessageStreamEncoderAdapter;
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
/*     */ public class OutgoingMessageQueueImpl
/*     */   implements org.gudy.azureus2.plugins.network.OutgoingMessageQueue
/*     */ {
/*     */   private final com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue core_queue;
/*  37 */   private final HashMap registrations = new HashMap();
/*     */   
/*     */   protected OutgoingMessageQueueImpl(com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue core_queue)
/*     */   {
/*  41 */     this.core_queue = core_queue;
/*     */   }
/*     */   
/*     */   public void setEncoder(MessageStreamEncoder encoder)
/*     */   {
/*  46 */     this.core_queue.setEncoder(new MessageStreamEncoderAdapter(encoder));
/*     */   }
/*     */   
/*     */   public void sendMessage(org.gudy.azureus2.plugins.messaging.Message message)
/*     */   {
/*  51 */     if ((message instanceof MessageAdapter))
/*     */     {
/*     */ 
/*  54 */       this.core_queue.addMessage(((MessageAdapter)message).getCoreMessage(), false);
/*  55 */       return;
/*     */     }
/*     */     
/*     */ 
/*  59 */     this.core_queue.addMessage(new MessageAdapter(message), false);
/*     */   }
/*     */   
/*     */   public void registerListener(final OutgoingMessageQueueListener listener)
/*     */   {
/*  64 */     OutgoingMessageQueue.MessageQueueListener core_listener = new OutgoingMessageQueue.MessageQueueListener()
/*     */     {
/*     */       public boolean messageAdded(com.aelitis.azureus.core.peermanager.messaging.Message message)
/*     */       {
/*  68 */         if ((message instanceof MessageAdapter))
/*     */         {
/*     */ 
/*  71 */           return listener.messageAdded(((MessageAdapter)message).getPluginMessage());
/*     */         }
/*     */         
/*     */ 
/*  75 */         return listener.messageAdded(new MessageAdapter(message));
/*     */       }
/*     */       
/*     */       public void messageQueued(com.aelitis.azureus.core.peermanager.messaging.Message message) {}
/*     */       
/*     */       public void messageRemoved(com.aelitis.azureus.core.peermanager.messaging.Message message) {}
/*     */       
/*  82 */       public void messageSent(com.aelitis.azureus.core.peermanager.messaging.Message message) { if ((message instanceof MessageAdapter))
/*     */         {
/*     */ 
/*  85 */           listener.messageSent(((MessageAdapter)message).getPluginMessage());
/*  86 */           return;
/*     */         }
/*     */         
/*     */ 
/*  90 */         listener.messageSent(new MessageAdapter(message));
/*     */       }
/*     */       
/*  93 */       public void protocolBytesSent(int byte_count) { listener.bytesSent(byte_count); }
/*     */       
/*  95 */       public void dataBytesSent(int byte_count) { listener.bytesSent(byte_count); }
/*     */       
/*     */       public void flush() {}
/*  98 */     };
/*  99 */     this.registrations.put(listener, core_listener);
/*     */     
/* 101 */     this.core_queue.registerQueueListener(core_listener);
/*     */   }
/*     */   
/*     */ 
/*     */   public void deregisterListener(OutgoingMessageQueueListener listener)
/*     */   {
/* 107 */     OutgoingMessageQueue.MessageQueueListener core_listener = (OutgoingMessageQueue.MessageQueueListener)this.registrations.remove(listener);
/*     */     
/*     */ 
/* 110 */     if (core_listener != null) {
/* 111 */       this.core_queue.cancelQueueListener(core_listener);
/*     */     }
/*     */   }
/*     */   
/*     */   public void notifyOfExternalSend(org.gudy.azureus2.plugins.messaging.Message message)
/*     */   {
/* 117 */     if ((message instanceof MessageAdapter))
/*     */     {
/*     */ 
/* 120 */       this.core_queue.notifyOfExternallySentMessage(((MessageAdapter)message).getCoreMessage());
/* 121 */       return;
/*     */     }
/*     */     
/*     */ 
/* 125 */     this.core_queue.notifyOfExternallySentMessage(new MessageAdapter(message));
/*     */   }
/*     */   
/*     */   public int getPercentDoneOfCurrentMessage() {
/* 129 */     return this.core_queue.getPercentDoneOfCurrentMessage();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getDataQueuedBytes()
/*     */   {
/* 135 */     return this.core_queue.getDataQueuedBytes();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getProtocolQueuedBytes()
/*     */   {
/* 141 */     return this.core_queue.getProtocolQueuedBytes();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isBlocked()
/*     */   {
/* 147 */     return this.core_queue.isBlocked();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/network/OutgoingMessageQueueImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */