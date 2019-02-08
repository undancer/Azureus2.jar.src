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
/*    */ public class IENDChunk
/*    */   extends CRCedChunk
/*    */ {
/* 24 */   private static final byte[] type = { 73, 69, 78, 68 };
/*    */   
/*    */   public IENDChunk() {
/* 27 */     super(type);
/*    */   }
/*    */   
/*    */   public byte[] getContentPayload() {
/* 31 */     return new byte[0];
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/png/IENDChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */