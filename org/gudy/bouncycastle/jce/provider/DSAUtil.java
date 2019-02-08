/*    */ package org.gudy.bouncycastle.jce.provider;
/*    */ 
/*    */ import java.security.InvalidKeyException;
/*    */ import java.security.PrivateKey;
/*    */ import java.security.PublicKey;
/*    */ import java.security.interfaces.DSAParams;
/*    */ import java.security.interfaces.DSAPrivateKey;
/*    */ import java.security.interfaces.DSAPublicKey;
/*    */ import org.gudy.bouncycastle.crypto.params.AsymmetricKeyParameter;
/*    */ import org.gudy.bouncycastle.crypto.params.DSAParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.DSAPrivateKeyParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.DSAPublicKeyParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DSAUtil
/*    */ {
/*    */   public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey key)
/*    */     throws InvalidKeyException
/*    */   {
/* 24 */     if ((key instanceof DSAPublicKey))
/*    */     {
/* 26 */       DSAPublicKey k = (DSAPublicKey)key;
/*    */       
/* 28 */       return new DSAPublicKeyParameters(k.getY(), new DSAParameters(k.getParams().getP(), k.getParams().getQ(), k.getParams().getG()));
/*    */     }
/*    */     
/*    */ 
/* 32 */     throw new InvalidKeyException("can't identify DSA public key: " + key.getClass().getName());
/*    */   }
/*    */   
/*    */ 
/*    */   public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey key)
/*    */     throws InvalidKeyException
/*    */   {
/* 39 */     if ((key instanceof DSAPrivateKey))
/*    */     {
/* 41 */       DSAPrivateKey k = (DSAPrivateKey)key;
/*    */       
/* 43 */       return new DSAPrivateKeyParameters(k.getX(), new DSAParameters(k.getParams().getP(), k.getParams().getQ(), k.getParams().getG()));
/*    */     }
/*    */     
/*    */ 
/* 47 */     throw new InvalidKeyException("can't identify DSA private key.");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/DSAUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */