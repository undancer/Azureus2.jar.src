/*    */ package org.gudy.bouncycastle.crypto.generators;
/*    */ 
/*    */ import org.gudy.bouncycastle.crypto.Digest;
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
/*    */ public class KDF2BytesGenerator
/*    */   extends BaseKDFBytesGenerator
/*    */ {
/*    */   public KDF2BytesGenerator(Digest digest)
/*    */   {
/* 22 */     super(1, digest);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/generators/KDF2BytesGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */