/*    */ package org.gudy.bouncycastle.crypto;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AsymmetricCipherKeyPair
/*    */ {
/*    */   private CipherParameters publicParam;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   private CipherParameters privateParam;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public AsymmetricCipherKeyPair(CipherParameters publicParam, CipherParameters privateParam)
/*    */   {
/* 23 */     this.publicParam = publicParam;
/* 24 */     this.privateParam = privateParam;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public CipherParameters getPublic()
/*    */   {
/* 34 */     return this.publicParam;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public CipherParameters getPrivate()
/*    */   {
/* 44 */     return this.privateParam;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/AsymmetricCipherKeyPair.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */