/*    */ package com.aelitis.azureus.core.util.png;
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
/*    */ public class PngSignatureChunk
/*    */   extends Chunk
/*    */ {
/* 24 */   private static final byte[] signature = { -119, 80, 78, 71, 13, 10, 26, 10 };
/*    */   
/*    */   public byte[] getChunkPayload() {
/* 27 */     return signature;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/png/PngSignatureChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */