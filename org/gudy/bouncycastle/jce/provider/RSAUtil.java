/*    */ package org.gudy.bouncycastle.jce.provider;
/*    */ 
/*    */ import java.security.interfaces.RSAPrivateCrtKey;
/*    */ import java.security.interfaces.RSAPrivateKey;
/*    */ import java.security.interfaces.RSAPublicKey;
/*    */ import org.gudy.bouncycastle.crypto.params.RSAKeyParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RSAUtil
/*    */ {
/*    */   public static RSAKeyParameters generatePublicKeyParameter(RSAPublicKey key)
/*    */   {
/* 19 */     return new RSAKeyParameters(false, key.getModulus(), key.getPublicExponent());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static RSAKeyParameters generatePrivateKeyParameter(RSAPrivateKey key)
/*    */   {
/* 26 */     if ((key instanceof RSAPrivateCrtKey))
/*    */     {
/* 28 */       RSAPrivateCrtKey k = (RSAPrivateCrtKey)key;
/*    */       
/* 30 */       return new RSAPrivateCrtKeyParameters(k.getModulus(), k.getPublicExponent(), k.getPrivateExponent(), k.getPrimeP(), k.getPrimeQ(), k.getPrimeExponentP(), k.getPrimeExponentQ(), k.getCrtCoefficient());
/*    */     }
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 36 */     RSAPrivateKey k = key;
/*    */     
/* 38 */     return new RSAKeyParameters(true, k.getModulus(), k.getPrivateExponent());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/RSAUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */