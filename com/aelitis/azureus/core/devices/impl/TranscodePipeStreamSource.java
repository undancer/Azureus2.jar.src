/*     */ package com.aelitis.azureus.core.devices.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ import java.util.List;
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
/*     */ public class TranscodePipeStreamSource
/*     */   extends TranscodePipe
/*     */ {
/*     */   private String source_host;
/*     */   private int source_port;
/*     */   
/*     */   protected TranscodePipeStreamSource(String _source_host, int _source_port)
/*     */     throws IOException
/*     */   {
/*  41 */     super(null);
/*     */     
/*  43 */     this.source_host = _source_host;
/*  44 */     this.source_port = _source_port;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void handleSocket(Socket socket1)
/*     */   {
/*  52 */     synchronized (this)
/*     */     {
/*  54 */       if (this.destroyed)
/*     */       {
/*     */         try {
/*  57 */           socket1.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */         
/*     */ 
/*  62 */         return;
/*     */       }
/*     */       
/*  65 */       this.sockets.add(socket1);
/*     */     }
/*     */     try
/*     */     {
/*  69 */       Socket socket2 = new Socket(this.source_host, this.source_port);
/*     */       
/*  71 */       synchronized (this)
/*     */       {
/*  73 */         if (this.destroyed)
/*     */         {
/*     */           try {
/*  76 */             socket1.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           
/*     */           try
/*     */           {
/*  82 */             socket2.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           
/*     */ 
/*  87 */           this.sockets.remove(socket1);
/*     */           
/*  89 */           return;
/*     */         }
/*     */         
/*  92 */         this.sockets.add(socket2);
/*     */       }
/*     */       
/*  95 */       handlePipe(socket1.getInputStream(), socket2.getOutputStream());
/*     */       
/*  97 */       handlePipe(socket2.getInputStream(), socket1.getOutputStream());
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */       try {
/* 102 */         socket1.close();
/*     */       }
/*     */       catch (Throwable f) {}
/*     */       
/*     */ 
/* 107 */       synchronized (this)
/*     */       {
/* 109 */         this.sockets.remove(socket1);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/TranscodePipeStreamSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */