/*    */ package com.aelitis.azureus.core.util.png;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.util.zip.Deflater;
/*    */ import java.util.zip.DeflaterOutputStream;
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
/*    */ public class IDATChunk
/*    */   extends CRCedChunk
/*    */ {
/* 27 */   private static final byte[] type = { 73, 68, 65, 84 };
/*    */   private final int width;
/*    */   private final int height;
/*    */   
/*    */   public IDATChunk(int width, int height)
/*    */   {
/* 33 */     super(type);
/* 34 */     this.width = width;
/* 35 */     this.height = height;
/*    */   }
/*    */   
/*    */   public byte[] getContentPayload()
/*    */   {
/* 40 */     byte[] payload = new byte[(this.width + 1) * this.height];
/* 41 */     for (int i = 0; i < this.height; i++) {
/* 42 */       int offset = i * (this.width + 1);
/*    */       
/* 44 */       payload[(offset++)] = 0;
/* 45 */       for (int j = 0; j < this.width; j++) {
/* 46 */         payload[(offset + j)] = Byte.MAX_VALUE;
/*    */       }
/*    */     }
/*    */     
/* 50 */     Deflater deflater = new Deflater(-1);
/* 51 */     ByteArrayOutputStream outBytes = new ByteArrayOutputStream((this.width + 1) * this.height);
/*    */     
/* 53 */     DeflaterOutputStream compBytes = new DeflaterOutputStream(outBytes, deflater);
/*    */     try {
/* 55 */       compBytes.write(payload);
/* 56 */       compBytes.close();
/*    */     } catch (Exception e) {
/* 58 */       e.printStackTrace();
/*    */     }
/* 60 */     byte[] compPayload = outBytes.toByteArray();
/*    */     
/* 62 */     return compPayload;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/png/IDATChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */