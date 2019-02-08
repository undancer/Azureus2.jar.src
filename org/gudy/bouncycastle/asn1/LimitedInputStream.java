/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ 
/*    */ 
/*    */ abstract class LimitedInputStream
/*    */   extends InputStream
/*    */ {
/*    */   protected final InputStream _in;
/*    */   
/*    */   LimitedInputStream(InputStream in)
/*    */   {
/* 13 */     this._in = in;
/*    */   }
/*    */   
/*    */   InputStream getUnderlyingStream()
/*    */   {
/* 18 */     return this._in;
/*    */   }
/*    */   
/*    */   protected void setParentEofDetect(boolean on)
/*    */   {
/* 23 */     if ((this._in instanceof IndefiniteLengthInputStream))
/*    */     {
/* 25 */       ((IndefiniteLengthInputStream)this._in).setEofOn00(on);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/LimitedInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */