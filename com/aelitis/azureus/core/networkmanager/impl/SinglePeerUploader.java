/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.EventWaiter;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnectionBase;
/*     */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue;
/*     */ import com.aelitis.azureus.core.networkmanager.RateHandler;
/*     */ import com.aelitis.azureus.core.networkmanager.TransportBase;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import java.io.IOException;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class SinglePeerUploader
/*     */   implements RateControlledEntity
/*     */ {
/*     */   private final NetworkConnectionBase connection;
/*     */   private final RateHandler rate_handler;
/*     */   
/*     */   public SinglePeerUploader(NetworkConnectionBase connection, RateHandler rate_handler)
/*     */   {
/*  42 */     this.connection = connection;
/*  43 */     this.rate_handler = rate_handler;
/*     */   }
/*     */   
/*     */ 
/*     */   public RateHandler getRateHandler()
/*     */   {
/*  49 */     return this.rate_handler;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canProcess(EventWaiter waiter)
/*     */   {
/*  55 */     if (!this.connection.getTransportBase().isReadyForWrite(waiter)) {
/*  56 */       return false;
/*     */     }
/*  58 */     if (this.connection.getOutgoingMessageQueue().getTotalSize() < 1) {
/*  59 */       return false;
/*     */     }
/*  61 */     int[] allowed = this.rate_handler.getCurrentNumBytesAllowed();
/*     */     
/*  63 */     if (allowed[0] < 1)
/*     */     {
/*  65 */       boolean protocol_is_free = allowed[1] > 0;
/*     */       
/*  67 */       if (protocol_is_free)
/*     */       {
/*  69 */         Message first = this.connection.getOutgoingMessageQueue().peekFirstMessage();
/*     */         
/*  71 */         if ((first != null) && (first.getType() == 0))
/*     */         {
/*  73 */           return true;
/*     */         }
/*     */         
/*     */ 
/*  77 */         return false;
/*     */       }
/*     */       
/*     */ 
/*  81 */       return false;
/*     */     }
/*     */     
/*  84 */     return true;
/*     */   }
/*     */   
/*     */   public int doProcessing(EventWaiter waiter, int max_bytes) {
/*  88 */     if (!this.connection.getTransportBase().isReadyForWrite(waiter))
/*     */     {
/*  90 */       return 0;
/*     */     }
/*     */     
/*  93 */     int[] allowed = this.rate_handler.getCurrentNumBytesAllowed();
/*     */     
/*  95 */     int num_bytes_allowed = allowed[0];
/*     */     
/*  97 */     boolean protocol_is_free = allowed[1] > 0;
/*     */     
/*  99 */     if (num_bytes_allowed < 1)
/*     */     {
/* 101 */       if (protocol_is_free)
/*     */       {
/* 103 */         num_bytes_allowed = 0;
/*     */       }
/*     */       else
/*     */       {
/* 107 */         return 0;
/*     */       }
/*     */     }
/*     */     
/* 111 */     if ((max_bytes > 0) && (max_bytes < num_bytes_allowed))
/*     */     {
/* 113 */       num_bytes_allowed = max_bytes;
/*     */     }
/*     */     
/* 116 */     int num_bytes_available = this.connection.getOutgoingMessageQueue().getTotalSize();
/*     */     
/* 118 */     if (num_bytes_available < 1)
/*     */     {
/* 120 */       if (!this.connection.getOutgoingMessageQueue().isDestroyed()) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 125 */       return 0;
/*     */     }
/*     */     
/* 128 */     int num_bytes_to_write = num_bytes_allowed > num_bytes_available ? num_bytes_available : num_bytes_allowed;
/*     */     
/*     */ 
/*     */ 
/*     */     int[] written;
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 137 */       written = this.connection.getOutgoingMessageQueue().deliverToTransport(num_bytes_to_write, protocol_is_free, false);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 141 */       written = new int[2];
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
/* 160 */       if (!(e instanceof IOException))
/*     */       {
/* 162 */         Debug.printStackTrace(e);
/*     */       }
/*     */       
/* 165 */       this.connection.notifyOfException(e);
/* 166 */       return 0;
/*     */     }
/*     */     
/* 169 */     int data_bytes_written = written[0];
/* 170 */     int protocol_bytes_written = written[1];
/*     */     
/* 172 */     int total_written = data_bytes_written + protocol_bytes_written;
/*     */     
/* 174 */     if (total_written < 1)
/*     */     {
/* 176 */       return 0;
/*     */     }
/*     */     
/* 179 */     this.rate_handler.bytesProcessed(data_bytes_written, protocol_bytes_written);
/*     */     
/* 181 */     return total_written;
/*     */   }
/*     */   
/*     */   public int getPriority() {
/* 185 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getPriorityBoost()
/*     */   {
/* 191 */     return this.connection.getOutgoingMessageQueue().getPriorityBoost();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesReadyToWrite()
/*     */   {
/* 197 */     return this.connection.getOutgoingMessageQueue().getTotalSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getConnectionCount(EventWaiter waiter)
/*     */   {
/* 203 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getReadyConnectionCount(EventWaiter waiter)
/*     */   {
/* 210 */     if (this.connection.getTransportBase().isReadyForWrite(waiter))
/*     */     {
/* 212 */       return 1;
/*     */     }
/*     */     
/* 215 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 221 */     return "SPU: bytes_allowed=" + this.rate_handler.getCurrentNumBytesAllowed() + " " + this.connection.getString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/SinglePeerUploader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */