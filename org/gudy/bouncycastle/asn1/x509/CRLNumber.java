/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import org.gudy.bouncycastle.asn1.DERInteger;
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
/*    */ public class CRLNumber
/*    */   extends DERInteger
/*    */ {
/*    */   public CRLNumber(BigInteger number)
/*    */   {
/* 20 */     super(number);
/*    */   }
/*    */   
/*    */   public BigInteger getCRLNumber()
/*    */   {
/* 25 */     return getPositiveValue();
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 30 */     return "CRLNumber: " + getCRLNumber();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/CRLNumber.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */