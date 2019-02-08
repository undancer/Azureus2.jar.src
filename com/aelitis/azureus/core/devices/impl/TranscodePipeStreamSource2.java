/*    */ package com.aelitis.azureus.core.devices.impl;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.Socket;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TranscodePipeStreamSource2
/*    */   extends TranscodePipe
/*    */ {
/*    */   private streamListener adapter;
/*    */   
/*    */   protected TranscodePipeStreamSource2(streamListener _adapter)
/*    */     throws IOException
/*    */   {
/* 40 */     super(null);
/*    */     
/* 42 */     this.adapter = _adapter;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected void handleSocket(Socket socket1)
/*    */   {
/* 50 */     synchronized (this)
/*    */     {
/* 52 */       if (this.destroyed)
/*    */       {
/*    */         try {
/* 55 */           socket1.close();
/*    */         }
/*    */         catch (Throwable e) {}
/*    */         
/*    */ 
/* 60 */         return;
/*    */       }
/*    */       
/* 63 */       this.sockets.add(socket1);
/*    */     }
/*    */     
/*    */     try
/*    */     {
/* 68 */       this.adapter.gotStream(socket1.getInputStream());
/*    */     }
/*    */     catch (Throwable e)
/*    */     {
/* 72 */       synchronized (this)
/*    */       {
/*    */         try {
/* 75 */           socket1.close();
/*    */         }
/*    */         catch (Throwable f) {}
/*    */         
/*    */ 
/*    */ 
/* 81 */         this.sockets.remove(socket1);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   static abstract interface streamListener
/*    */   {
/*    */     public abstract void gotStream(InputStream paramInputStream);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/TranscodePipeStreamSource2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */