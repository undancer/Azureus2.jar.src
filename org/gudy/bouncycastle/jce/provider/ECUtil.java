/*    */ package org.gudy.bouncycastle.jce.provider;
/*    */ 
/*    */ import java.security.InvalidKeyException;
/*    */ import java.security.PrivateKey;
/*    */ import java.security.PublicKey;
/*    */ import org.gudy.bouncycastle.crypto.params.AsymmetricKeyParameter;
/*    */ import org.gudy.bouncycastle.crypto.params.ECDomainParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.ECPrivateKeyParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.ECPublicKeyParameters;
/*    */ import org.gudy.bouncycastle.jce.interfaces.ECPrivateKey;
/*    */ import org.gudy.bouncycastle.jce.interfaces.ECPublicKey;
/*    */ import org.gudy.bouncycastle.jce.spec.ECParameterSpec;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ECUtil
/*    */ {
/*    */   public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey key)
/*    */     throws InvalidKeyException
/*    */   {
/* 25 */     if ((key instanceof ECPublicKey))
/*    */     {
/* 27 */       ECPublicKey k = (ECPublicKey)key;
/* 28 */       ECParameterSpec s = k.getParams();
/*    */       
/* 30 */       return new ECPublicKeyParameters(k.getQ(), new ECDomainParameters(s.getCurve(), s.getG(), s.getN()));
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 35 */     throw new InvalidKeyException("can't identify EC public key.");
/*    */   }
/*    */   
/*    */ 
/*    */   public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey key)
/*    */     throws InvalidKeyException
/*    */   {
/* 42 */     if ((key instanceof ECPrivateKey))
/*    */     {
/* 44 */       ECPrivateKey k = (ECPrivateKey)key;
/* 45 */       ECParameterSpec s = k.getParams();
/*    */       
/* 47 */       return new ECPrivateKeyParameters(k.getD(), new ECDomainParameters(s.getCurve(), s.getG(), s.getN()));
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 52 */     throw new InvalidKeyException("can't identify EC private key.");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/ECUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */