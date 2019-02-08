/*    */ package com.aelitis.azureus.core.util.png;
/*    */ 
/*    */ import java.nio.ByteBuffer;
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
/*    */ public class PNG
/*    */ {
/*    */   public static byte[] getPNGBytesForWidth(int width)
/*    */   {
/* 27 */     return getPNGBytesForSize(width, 1);
/*    */   }
/*    */   
/*    */   public static byte[] getPNGBytesForSize(int width, int height) {
/* 31 */     byte[] signature = new PngSignatureChunk().getChunkPayload();
/* 32 */     byte[] ihdr = new IHDRChunk(width, height).getChunkPayload();
/* 33 */     byte[] idat = new IDATChunk(width, height).getChunkPayload();
/* 34 */     byte[] iend = new IENDChunk().getChunkPayload();
/*    */     
/* 36 */     ByteBuffer buffer = ByteBuffer.allocate(signature.length + ihdr.length + idat.length + iend.length);
/* 37 */     buffer.put(signature);
/* 38 */     buffer.put(ihdr);
/* 39 */     buffer.put(idat);
/* 40 */     buffer.put(iend);
/*    */     
/* 42 */     buffer.position(0);
/* 43 */     return buffer.array();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/png/PNG.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */