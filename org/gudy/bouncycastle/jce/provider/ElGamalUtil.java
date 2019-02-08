/*    */ package org.gudy.bouncycastle.jce.provider;
/*    */ 
/*    */ import java.security.InvalidKeyException;
/*    */ import java.security.PrivateKey;
/*    */ import java.security.PublicKey;
/*    */ import org.gudy.bouncycastle.crypto.params.AsymmetricKeyParameter;
/*    */ import org.gudy.bouncycastle.crypto.params.ElGamalParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
/*    */ import org.gudy.bouncycastle.jce.interfaces.ElGamalPrivateKey;
/*    */ import org.gudy.bouncycastle.jce.interfaces.ElGamalPublicKey;
/*    */ import org.gudy.bouncycastle.jce.spec.ElGamalParameterSpec;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ElGamalUtil
/*    */ {
/*    */   public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey key)
/*    */     throws InvalidKeyException
/*    */   {
/* 24 */     if ((key instanceof ElGamalPublicKey))
/*    */     {
/* 26 */       ElGamalPublicKey k = (ElGamalPublicKey)key;
/*    */       
/* 28 */       return new ElGamalPublicKeyParameters(k.getY(), new ElGamalParameters(k.getParams().getP(), k.getParams().getG()));
/*    */     }
/*    */     
/*    */ 
/* 32 */     throw new InvalidKeyException("can't identify ElGamal public key.");
/*    */   }
/*    */   
/*    */ 
/*    */   public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey key)
/*    */     throws InvalidKeyException
/*    */   {
/* 39 */     if ((key instanceof ElGamalPrivateKey))
/*    */     {
/* 41 */       ElGamalPrivateKey k = (ElGamalPrivateKey)key;
/*    */       
/* 43 */       return new ElGamalPrivateKeyParameters(k.getX(), new ElGamalParameters(k.getParams().getP(), k.getParams().getG()));
/*    */     }
/*    */     
/*    */ 
/* 47 */     throw new InvalidKeyException("can't identify ElGamal private key.");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/ElGamalUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */