/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.EventWaiter;
/*     */ import com.aelitis.azureus.core.networkmanager.IncomingMessageQueue;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnectionBase;
/*     */ import com.aelitis.azureus.core.networkmanager.RateHandler;
/*     */ import com.aelitis.azureus.core.networkmanager.TransportBase;
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
/*     */ public class SinglePeerDownloader
/*     */   implements RateControlledEntity
/*     */ {
/*     */   private final NetworkConnectionBase connection;
/*     */   private final RateHandler rate_handler;
/*     */   
/*     */   public SinglePeerDownloader(NetworkConnectionBase connection, RateHandler rate_handler)
/*     */   {
/*  40 */     this.connection = connection;
/*  41 */     this.rate_handler = rate_handler;
/*     */   }
/*     */   
/*     */ 
/*     */   public RateHandler getRateHandler()
/*     */   {
/*  47 */     return this.rate_handler;
/*     */   }
/*     */   
/*     */   public boolean canProcess(EventWaiter waiter)
/*     */   {
/*  52 */     if (this.connection.getTransportBase().isReadyForRead(waiter) != 0L) {
/*  53 */       return false;
/*     */     }
/*     */     
/*  56 */     int[] allowed = this.rate_handler.getCurrentNumBytesAllowed();
/*     */     
/*  58 */     if (allowed[0] < 1)
/*     */     {
/*  60 */       return false;
/*     */     }
/*     */     
/*  63 */     return true;
/*     */   }
/*     */   
/*     */   public int doProcessing(EventWaiter waiter, int max_bytes) {
/*  67 */     if (this.connection.getTransportBase().isReadyForRead(waiter) != 0L) {
/*  68 */       return 0;
/*     */     }
/*     */     
/*  71 */     int[] allowed = this.rate_handler.getCurrentNumBytesAllowed();
/*     */     
/*  73 */     int num_bytes_allowed = allowed[0];
/*     */     
/*  75 */     boolean protocol_is_free = allowed[1] > 0;
/*     */     
/*  77 */     if (num_bytes_allowed < 1)
/*     */     {
/*     */ 
/*     */ 
/*  81 */       return 0;
/*     */     }
/*     */     
/*  84 */     if ((max_bytes > 0) && (max_bytes < num_bytes_allowed)) {
/*  85 */       num_bytes_allowed = max_bytes;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  91 */     int bytes_read = 0;
/*     */     
/*  93 */     int data_bytes_read = 0;
/*  94 */     int protocol_bytes_read = 0;
/*     */     try
/*     */     {
/*  97 */       int[] read = this.connection.getIncomingMessageQueue().receiveFromTransport(num_bytes_allowed, protocol_is_free);
/*     */       
/*  99 */       data_bytes_read = read[0];
/* 100 */       protocol_bytes_read = read[1];
/*     */       
/* 102 */       bytes_read = data_bytes_read + protocol_bytes_read;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 123 */       if (!(e instanceof IOException))
/*     */       {
/* 125 */         Debug.printStackTrace(e);
/*     */       }
/*     */       
/* 128 */       this.connection.notifyOfException(e);
/* 129 */       return 0;
/*     */     }
/*     */     
/* 132 */     if (bytes_read < 1) {
/* 133 */       return 0;
/*     */     }
/*     */     
/* 136 */     this.rate_handler.bytesProcessed(data_bytes_read, protocol_bytes_read);
/*     */     
/* 138 */     return bytes_read;
/*     */   }
/*     */   
/*     */   public int getPriority()
/*     */   {
/* 143 */     return 0;
/*     */   }
/*     */   
/* 146 */   public boolean getPriorityBoost() { return false; }
/*     */   
/*     */ 
/*     */   public long getBytesReadyToWrite()
/*     */   {
/* 151 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getConnectionCount(EventWaiter waiter)
/*     */   {
/* 157 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getReadyConnectionCount(EventWaiter waiter)
/*     */   {
/* 164 */     if (this.connection.getTransportBase().isReadyForRead(waiter) == 0L)
/*     */     {
/* 166 */       return 1;
/*     */     }
/*     */     
/* 169 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 175 */     return "SPD: " + this.connection.getString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/SinglePeerDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */