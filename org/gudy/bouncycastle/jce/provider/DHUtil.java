/*    */ package org.gudy.bouncycastle.jce.provider;
/*    */ 
/*    */ import java.security.InvalidKeyException;
/*    */ import java.security.PrivateKey;
/*    */ import java.security.PublicKey;
/*    */ import javax.crypto.interfaces.DHPrivateKey;
/*    */ import javax.crypto.interfaces.DHPublicKey;
/*    */ import javax.crypto.spec.DHParameterSpec;
/*    */ import org.gudy.bouncycastle.crypto.params.AsymmetricKeyParameter;
/*    */ import org.gudy.bouncycastle.crypto.params.DHParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.DHPrivateKeyParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.DHPublicKeyParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DHUtil
/*    */ {
/*    */   public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey key)
/*    */     throws InvalidKeyException
/*    */   {
/* 25 */     if ((key instanceof DHPublicKey))
/*    */     {
/* 27 */       DHPublicKey k = (DHPublicKey)key;
/*    */       
/* 29 */       return new DHPublicKeyParameters(k.getY(), new DHParameters(k.getParams().getP(), k.getParams().getG(), null, k.getParams().getL()));
/*    */     }
/*    */     
/*    */ 
/* 33 */     throw new InvalidKeyException("can't identify DH public key.");
/*    */   }
/*    */   
/*    */ 
/*    */   public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey key)
/*    */     throws InvalidKeyException
/*    */   {
/* 40 */     if ((key instanceof DHPrivateKey))
/*    */     {
/* 42 */       DHPrivateKey k = (DHPrivateKey)key;
/*    */       
/* 44 */       return new DHPrivateKeyParameters(k.getX(), new DHParameters(k.getParams().getP(), k.getParams().getG(), null, k.getParams().getL()));
/*    */     }
/*    */     
/*    */ 
/* 48 */     throw new InvalidKeyException("can't identify DH private key.");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/DHUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */