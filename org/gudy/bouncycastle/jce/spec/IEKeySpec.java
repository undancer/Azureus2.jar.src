/*    */ package org.gudy.bouncycastle.jce.spec;
/*    */ 
/*    */ import java.security.PrivateKey;
/*    */ import java.security.PublicKey;
/*    */ import java.security.spec.KeySpec;
/*    */ import org.gudy.bouncycastle.jce.interfaces.IESKey;
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
/*    */ public class IEKeySpec
/*    */   implements KeySpec, IESKey
/*    */ {
/*    */   private PublicKey pubKey;
/*    */   private PrivateKey privKey;
/*    */   
/*    */   public IEKeySpec(PrivateKey privKey, PublicKey pubKey)
/*    */   {
/* 27 */     this.privKey = privKey;
/* 28 */     this.pubKey = pubKey;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public PublicKey getPublic()
/*    */   {
/* 36 */     return this.pubKey;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public PrivateKey getPrivate()
/*    */   {
/* 44 */     return this.privKey;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getAlgorithm()
/*    */   {
/* 52 */     return "IES";
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getFormat()
/*    */   {
/* 60 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public byte[] getEncoded()
/*    */   {
/* 68 */     return null;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/spec/IEKeySpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */