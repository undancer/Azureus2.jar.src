/*    */ package org.gudy.bouncycastle.crypto.agreement;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import org.gudy.bouncycastle.crypto.BasicAgreement;
/*    */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.ECPrivateKeyParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.ECPublicKeyParameters;
/*    */ import org.gudy.bouncycastle.math.ec.ECFieldElement;
/*    */ import org.gudy.bouncycastle.math.ec.ECPoint;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ECDHBasicAgreement
/*    */   implements BasicAgreement
/*    */ {
/*    */   private ECPrivateKeyParameters key;
/*    */   
/*    */   public void init(CipherParameters key)
/*    */   {
/* 33 */     this.key = ((ECPrivateKeyParameters)key);
/*    */   }
/*    */   
/*    */ 
/*    */   public BigInteger calculateAgreement(CipherParameters pubKey)
/*    */   {
/* 39 */     ECPublicKeyParameters pub = (ECPublicKeyParameters)pubKey;
/* 40 */     ECPoint P = pub.getQ().multiply(this.key.getD());
/*    */     
/*    */ 
/*    */ 
/* 44 */     return P.getX().toBigInteger();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/agreement/ECDHBasicAgreement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */