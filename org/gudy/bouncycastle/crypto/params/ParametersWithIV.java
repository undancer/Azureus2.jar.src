/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ParametersWithIV
/*    */   implements CipherParameters
/*    */ {
/*    */   private byte[] iv;
/*    */   private CipherParameters parameters;
/*    */   
/*    */   public ParametersWithIV(CipherParameters parameters, byte[] iv)
/*    */   {
/* 15 */     this(parameters, iv, 0, iv.length);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ParametersWithIV(CipherParameters parameters, byte[] iv, int ivOff, int ivLen)
/*    */   {
/* 24 */     this.iv = new byte[ivLen];
/* 25 */     this.parameters = parameters;
/*    */     
/* 27 */     System.arraycopy(iv, ivOff, this.iv, 0, ivLen);
/*    */   }
/*    */   
/*    */   public byte[] getIV()
/*    */   {
/* 32 */     return this.iv;
/*    */   }
/*    */   
/*    */   public CipherParameters getParameters()
/*    */   {
/* 37 */     return this.parameters;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/ParametersWithIV.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */