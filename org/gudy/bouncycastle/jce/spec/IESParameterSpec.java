/*    */ package org.gudy.bouncycastle.jce.spec;
/*    */ 
/*    */ import java.security.spec.AlgorithmParameterSpec;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class IESParameterSpec
/*    */   implements AlgorithmParameterSpec
/*    */ {
/*    */   private byte[] derivation;
/*    */   private byte[] encoding;
/*    */   private int macKeySize;
/*    */   
/*    */   public IESParameterSpec(byte[] derivation, byte[] encoding, int macKeySize)
/*    */   {
/* 20 */     this.derivation = new byte[derivation.length];
/* 21 */     System.arraycopy(derivation, 0, this.derivation, 0, derivation.length);
/*    */     
/* 23 */     this.encoding = new byte[encoding.length];
/* 24 */     System.arraycopy(encoding, 0, this.encoding, 0, encoding.length);
/*    */     
/* 26 */     this.macKeySize = macKeySize;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public byte[] getDerivationV()
/*    */   {
/* 34 */     return this.derivation;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public byte[] getEncodingV()
/*    */   {
/* 42 */     return this.encoding;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public int getMacKeySize()
/*    */   {
/* 50 */     return this.macKeySize;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/spec/IESParameterSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */