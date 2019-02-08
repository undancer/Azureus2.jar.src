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
/*    */ public class SHA384Digest
/*    */   extends LongDigest
/*    */ {
/*    */   private static final int DIGEST_LENGTH = 48;
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
/*    */   public SHA384Digest() {}
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public SHA384Digest(SHA384Digest t)
/*    */   {
/* 38 */     super(t);
/*    */   }
/*    */   
/*    */   public String getAlgorithmName()
/*    */   {
/* 43 */     return "SHA-384";
/*    */   }
/*    */   
/*    */   public int getDigestSize()
/*    */   {
/* 48 */     return 48;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public int doFinal(byte[] out, int outOff)
/*    */   {
/* 55 */     finish();
/*    */     
/* 57 */     unpackWord(this.H1, out, outOff);
/* 58 */     unpackWord(this.H2, out, outOff + 8);
/* 59 */     unpackWord(this.H3, out, outOff + 16);
/* 60 */     unpackWord(this.H4, out, outOff + 24);
/* 61 */     unpackWord(this.H5, out, outOff + 32);
/* 62 */     unpackWord(this.H6, out, outOff + 40);
/*    */     
/* 64 */     reset();
/*    */     
/* 66 */     return 48;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void reset()
/*    */   {
/* 74 */     super.reset();
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 80 */     this.H1 = -3766243637369397544L;
/* 81 */     this.H2 = 7105036623409894663L;
/* 82 */     this.H3 = -7973340178411365097L;
/* 83 */     this.H4 = 1526699215303891257L;
/* 84 */     this.H5 = 7436329637833083697L;
/* 85 */     this.H6 = -8163818279084223215L;
/* 86 */     this.H7 = -2662702644619276377L;
/* 87 */     this.H8 = 5167115440072839076L;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/digests/SHA384Digest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */