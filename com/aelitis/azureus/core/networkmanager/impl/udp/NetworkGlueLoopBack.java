/*     */ package com.aelitis.azureus.core.networkmanager.impl.udp;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Random;
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
/*     */ public class NetworkGlueLoopBack
/*     */   implements NetworkGlue
/*     */ {
/*     */   private static final int latency = 0;
/*     */   final NetworkGlueListener listener;
/*  39 */   private final List message_queue = new ArrayList();
/*     */   
/*  41 */   private final Random random = new Random();
/*     */   
/*     */ 
/*     */ 
/*     */   protected NetworkGlueLoopBack(NetworkGlueListener _listener)
/*     */   {
/*  47 */     this.listener = _listener;
/*     */     
/*  49 */     new AEThread("NetworkGlueLoopBack", true)
/*     */     {
/*     */       public void runSupport()
/*     */       {
/*     */         for (;;)
/*     */         {
/*     */           try
/*     */           {
/*  57 */             Thread.sleep(1L);
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           
/*     */ 
/*     */ 
/*  63 */           InetSocketAddress target_address = null;
/*  64 */           InetSocketAddress source_address = null;
/*  65 */           byte[] data = null;
/*     */           
/*  67 */           long now = SystemTime.getCurrentTime();
/*     */           
/*  69 */           synchronized (NetworkGlueLoopBack.this.message_queue)
/*     */           {
/*  71 */             if (NetworkGlueLoopBack.this.message_queue.size() > 0)
/*     */             {
/*  73 */               Object[] entry = (Object[])NetworkGlueLoopBack.this.message_queue.get(0);
/*     */               
/*  75 */               if (((Long)entry[0]).longValue() < now)
/*     */               {
/*  77 */                 NetworkGlueLoopBack.this.message_queue.remove(0);
/*     */                 
/*  79 */                 source_address = (InetSocketAddress)entry[1];
/*  80 */                 target_address = (InetSocketAddress)entry[2];
/*  81 */                 data = (byte[])entry[3];
/*     */               }
/*     */             }
/*     */           }
/*     */           
/*  86 */           if (source_address != null)
/*     */           {
/*  88 */             NetworkGlueLoopBack.this.listener.receive(target_address.getPort(), source_address, data, data.length);
/*     */           }
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int send(int local_port, InetSocketAddress target, byte[] data)
/*     */     throws IOException
/*     */   {
/* 103 */     Long expires = new Long(SystemTime.getCurrentTime() + 0L);
/*     */     
/* 105 */     InetSocketAddress local_address = new InetSocketAddress(target.getAddress(), local_port);
/*     */     
/* 107 */     synchronized (this.message_queue)
/*     */     {
/* 109 */       if (this.random.nextInt(4) != 9)
/*     */       {
/* 111 */         this.message_queue.add(new Object[] { expires, local_address, target, data });
/*     */       }
/*     */     }
/*     */     
/* 115 */     return data.length;
/*     */   }
/*     */   
/*     */ 
/*     */   public long[] getStats()
/*     */   {
/* 121 */     return new long[] { 0L, 0L, 0L, 0L };
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/udp/NetworkGlueLoopBack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */