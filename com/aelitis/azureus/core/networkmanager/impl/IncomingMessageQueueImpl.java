/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.IncomingMessageQueue;
/*     */ import com.aelitis.azureus.core.networkmanager.IncomingMessageQueue.MessageQueueListener;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamDecoder;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
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
/*     */   implements IncomingMessageQueue
/*     */ {
/*  39 */   private volatile ArrayList<IncomingMessageQueue.MessageQueueListener> listeners = new ArrayList();
/*  40 */   private final AEMonitor listeners_mon = new AEMonitor("IncomingMessageQueue:listeners");
/*     */   
/*     */ 
/*     */   private MessageStreamDecoder stream_decoder;
/*     */   
/*     */ 
/*     */   private final NetworkConnection connection;
/*     */   
/*     */ 
/*     */ 
/*     */   public IncomingMessageQueueImpl(MessageStreamDecoder stream_decoder, NetworkConnection connection)
/*     */   {
/*  52 */     if (stream_decoder == null) {
/*  53 */       throw new NullPointerException("stream_decoder is null");
/*     */     }
/*  55 */     this.connection = connection;
/*  56 */     this.stream_decoder = stream_decoder;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDecoder(MessageStreamDecoder new_stream_decoder)
/*     */   {
/*  65 */     ByteBuffer already_read = this.stream_decoder.destroy();
/*  66 */     this.connection.getTransport().setAlreadyRead(already_read);
/*  67 */     this.stream_decoder = new_stream_decoder;
/*  68 */     this.stream_decoder.resumeDecoding();
/*     */   }
/*     */   
/*     */ 
/*     */   public MessageStreamDecoder getDecoder()
/*     */   {
/*  74 */     return this.stream_decoder;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPercentDoneOfCurrentMessage()
/*     */   {
/*  82 */     return this.stream_decoder.getPercentDoneOfCurrentMessage();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int[] receiveFromTransport(int max_bytes, boolean protocol_is_free)
/*     */     throws IOException
/*     */   {
/*  94 */     if (max_bytes < 1)
/*     */     {
/*     */ 
/*     */ 
/*  98 */       if (!protocol_is_free)
/*     */       {
/* 100 */         Debug.out("max_bytes < 1: " + max_bytes);
/*     */       }
/*     */       
/* 103 */       return new int[2];
/*     */     }
/*     */     
/* 106 */     if (this.listeners.isEmpty()) {
/* 107 */       Debug.out("no queue listeners registered!");
/* 108 */       throw new IOException("no queue listeners registered!");
/*     */     }
/*     */     
/*     */ 
/*     */     int bytes_read;
/*     */     
/*     */     try
/*     */     {
/* 116 */       bytes_read = this.stream_decoder.performStreamDecode(this.connection.getTransport(), max_bytes);
/*     */     }
/*     */     catch (RuntimeException e)
/*     */     {
/* 120 */       Debug.out("Stream decode for " + this.connection.getString() + " failed: " + Debug.getNestedExceptionMessageAndStack(e));
/*     */       
/* 122 */       throw e;
/*     */     }
/*     */     
/*     */ 
/* 126 */     Message[] messages = this.stream_decoder.removeDecodedMessages();
/* 127 */     if (messages != null) {
/* 128 */       for (int i = 0; i < messages.length; i++) {
/* 129 */         Message msg = messages[i];
/*     */         
/* 131 */         if (msg == null) {
/* 132 */           System.out.println("received msg == null [messages.length=" + messages.length + ", #" + i + "]: " + this.connection.getTransport().getDescription());
/*     */         }
/*     */         else
/*     */         {
/* 136 */           ArrayList listeners_ref = this.listeners;
/* 137 */           boolean handled = false;
/*     */           
/* 139 */           for (int x = 0; x < listeners_ref.size(); x++) {
/* 140 */             IncomingMessageQueue.MessageQueueListener mql = (IncomingMessageQueue.MessageQueueListener)listeners_ref.get(x);
/* 141 */             if (mql.messageReceived(msg)) {
/* 142 */               handled = true;
/*     */             }
/*     */           }
/*     */           
/* 146 */           if (!handled) {
/* 147 */             if (listeners_ref.size() > 0) {
/* 148 */               System.out.println("no registered listeners [out of " + listeners_ref.size() + "] handled decoded message [" + msg.getDescription() + "]");
/*     */             }
/*     */             
/* 151 */             DirectByteBuffer[] buffs = msg.getData();
/* 152 */             for (int x = 0; x < buffs.length; x++) {
/* 153 */               buffs[x].returnToPool();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 159 */     int protocol_read = this.stream_decoder.getProtocolBytesDecoded();
/* 160 */     if (protocol_read > 0) {
/* 161 */       ArrayList listeners_ref = this.listeners;
/* 162 */       for (int i = 0; i < listeners_ref.size(); i++) {
/* 163 */         IncomingMessageQueue.MessageQueueListener mql = (IncomingMessageQueue.MessageQueueListener)listeners_ref.get(i);
/* 164 */         mql.protocolBytesReceived(protocol_read);
/*     */       }
/*     */     }
/*     */     
/* 168 */     int data_read = this.stream_decoder.getDataBytesDecoded();
/* 169 */     if (data_read > 0) {
/* 170 */       ArrayList listeners_ref = this.listeners;
/* 171 */       for (int i = 0; i < listeners_ref.size(); i++) {
/* 172 */         IncomingMessageQueue.MessageQueueListener mql = (IncomingMessageQueue.MessageQueueListener)listeners_ref.get(i);
/* 173 */         mql.dataBytesReceived(data_read);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 180 */     data_read = bytes_read - protocol_read;
/*     */     
/* 182 */     if (data_read < 0)
/*     */     {
/* 184 */       protocol_read = bytes_read;
/*     */       
/* 186 */       data_read = 0;
/*     */     }
/*     */     
/* 189 */     return new int[] { data_read, protocol_read };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void notifyOfExternallyReceivedMessage(Message message)
/*     */     throws IOException
/*     */   {
/* 200 */     ArrayList listeners_ref = this.listeners;
/* 201 */     boolean handled = false;
/*     */     
/* 203 */     DirectByteBuffer[] dbbs = message.getData();
/* 204 */     int size = 0;
/* 205 */     for (int i = 0; i < dbbs.length; i++) {
/* 206 */       size += dbbs[i].remaining((byte)5);
/*     */     }
/*     */     
/*     */ 
/* 210 */     for (int x = 0; x < listeners_ref.size(); x++) {
/* 211 */       IncomingMessageQueue.MessageQueueListener mql = (IncomingMessageQueue.MessageQueueListener)listeners_ref.get(x);
/* 212 */       if (mql.messageReceived(message)) {
/* 213 */         handled = true;
/*     */       }
/*     */       
/* 216 */       if (message.getType() == 1) {
/* 217 */         mql.dataBytesReceived(size);
/*     */       }
/*     */       else {
/* 220 */         mql.protocolBytesReceived(size);
/*     */       }
/*     */     }
/*     */     
/* 224 */     if (!handled) {
/* 225 */       if (listeners_ref.size() > 0) {
/* 226 */         System.out.println("no registered listeners [out of " + listeners_ref.size() + "] handled decoded message [" + message.getDescription() + "]");
/*     */       }
/*     */       
/* 229 */       DirectByteBuffer[] buffs = message.getData();
/* 230 */       for (int x = 0; x < buffs.length; x++) {
/* 231 */         buffs[x].returnToPool();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void resumeQueueProcessing()
/*     */   {
/* 243 */     this.stream_decoder.resumeDecoding();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerQueueListener(IncomingMessageQueue.MessageQueueListener listener)
/*     */   {
/*     */     try
/*     */     {
/* 253 */       this.listeners_mon.enter();
/*     */       
/* 255 */       ArrayList<IncomingMessageQueue.MessageQueueListener> new_list = new ArrayList(this.listeners.size() + 1);
/*     */       
/* 257 */       if (listener.isPriority()) {
/* 258 */         boolean added = false;
/* 259 */         for (int i = 0; i < this.listeners.size(); i++) {
/* 260 */           IncomingMessageQueue.MessageQueueListener existing = (IncomingMessageQueue.MessageQueueListener)this.listeners.get(i);
/* 261 */           if ((!added) && (!existing.isPriority()))
/*     */           {
/* 263 */             new_list.add(listener);
/* 264 */             added = true;
/*     */           }
/* 266 */           new_list.add(existing);
/*     */         }
/* 268 */         if (!added) {
/* 269 */           new_list.add(listener);
/*     */         }
/*     */       } else {
/* 272 */         new_list.addAll(this.listeners);
/* 273 */         new_list.add(listener);
/*     */       }
/* 275 */       this.listeners = new_list;
/*     */     } finally {
/* 277 */       this.listeners_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void cancelQueueListener(IncomingMessageQueue.MessageQueueListener listener)
/*     */   {
/*     */     try
/*     */     {
/* 286 */       this.listeners_mon.enter();
/*     */       
/* 288 */       ArrayList new_list = new ArrayList(this.listeners);
/* 289 */       new_list.remove(listener);
/* 290 */       this.listeners = new_list;
/*     */     } finally {
/* 292 */       this.listeners_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 302 */     this.stream_decoder.destroy();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/IncomingMessageQueueImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */