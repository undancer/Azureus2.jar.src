/*    */ package org.gudy.bouncycastle.crypto.digests;
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
/*    */ public class SHA512Digest
/*    */   extends LongDigest
/*    */ {
/*    */   private static final int DIGEST_LENGTH = 64;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public SHA512Digest() {}
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public SHA512Digest(SHA512Digest t)
/*    */   {
/* 37 */     super(t);
/*    */   }
/*    */   
/*    */   public String getAlgorithmName()
/*    */   {
/* 42 */     return "SHA-512";
/*    */   }
/*    */   
/*    */   public int getDigestSize()
/*    */   {
/* 47 */     return 64;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public int doFinal(byte[] out, int outOff)
/*    */   {
/* 54 */     finish();
/*    */     
/* 56 */     unpackWord(this.H1, out, outOff);
/* 57 */     unpackWord(this.H2, out, outOff + 8);
/* 58 */     unpackWord(this.H3, out, outOff + 16);
/* 59 */     unpackWord(this.H4, out, outOff + 24);
/* 60 */     unpackWord(this.H5, out, outOff + 32);
/* 61 */     unpackWord(this.H6, out, outOff + 40);
/* 62 */     unpackWord(this.H7, out, outOff + 48);
/* 63 */     unpackWord(this.H8, out, outOff + 56);
/*    */     
/* 65 */     reset();
/*    */     
/* 67 */     return 64;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void reset()
/*    */   {
/* 75 */     super.reset();
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 81 */     this.H1 = 7640891576956012808L;
/* 82 */     this.H2 = -4942790177534073029L;
/* 83 */     this.H3 = 4354685564936845355L;
/* 84 */     this.H4 = -6534734903238641935L;
/* 85 */     this.H5 = 5840696475078001361L;
/* 86 */     this.H6 = -7276294671716946913L;
/* 87 */     this.H7 = 2270897969802886507L;
/* 88 */     this.H8 = 6620516959819538809L;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/digests/SHA512Digest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */