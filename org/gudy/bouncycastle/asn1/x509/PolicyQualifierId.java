/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
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
/*    */ public class PolicyQualifierId
/*    */   extends DERObjectIdentifier
/*    */ {
/*    */   private static final String id_qt = "1.3.6.1.5.5.7.2";
/*    */   
/*    */   private PolicyQualifierId(String id)
/*    */   {
/* 24 */     super(id);
/*    */   }
/*    */   
/* 27 */   public static final PolicyQualifierId id_qt_cps = new PolicyQualifierId("1.3.6.1.5.5.7.2.1");
/*    */   
/* 29 */   public static final PolicyQualifierId id_qt_unotice = new PolicyQualifierId("1.3.6.1.5.5.7.2.2");
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/PolicyQualifierId.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */