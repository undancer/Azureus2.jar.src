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
/*    */ public class IHDRChunk
/*    */   extends CRCedChunk
/*    */ {
/* 26 */   private static final byte[] type = { 73, 72, 68, 82 };
/*    */   private final int width;
/*    */   private final int height;
/*    */   
/*    */   public IHDRChunk(int width, int height)
/*    */   {
/* 32 */     super(type);
/* 33 */     this.width = width;
/* 34 */     this.height = height;
/*    */   }
/*    */   
/*    */   public byte[] getContentPayload() {
/* 38 */     ByteBuffer buffer = ByteBuffer.allocate(13);
/*    */     
/*    */ 
/* 41 */     buffer.putInt(this.width);
/*    */     
/*    */ 
/* 44 */     buffer.putInt(this.height);
/*    */     
/*    */ 
/* 47 */     buffer.put((byte)8);
/*    */     
/*    */ 
/* 50 */     buffer.put((byte)0);
/*    */     
/*    */ 
/* 53 */     buffer.put((byte)0);
/*    */     
/*    */ 
/* 56 */     buffer.put((byte)0);
/*    */     
/*    */ 
/* 59 */     buffer.put((byte)0);
/*    */     
/* 61 */     return buffer.array();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/png/IHDRChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */