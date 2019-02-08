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
/*    */ public class KeyPurposeId
/*    */   extends DERObjectIdentifier
/*    */ {
/*    */   private static final String id_kp = "1.3.6.1.5.5.7.3";
/*    */   
/*    */   private KeyPurposeId(String id)
/*    */   {
/* 21 */     super(id);
/*    */   }
/*    */   
/* 24 */   public static final KeyPurposeId anyExtendedKeyUsage = new KeyPurposeId(X509Extensions.ExtendedKeyUsage.getId() + ".0");
/* 25 */   public static final KeyPurposeId id_kp_serverAuth = new KeyPurposeId("1.3.6.1.5.5.7.3.1");
/* 26 */   public static final KeyPurposeId id_kp_clientAuth = new KeyPurposeId("1.3.6.1.5.5.7.3.2");
/* 27 */   public static final KeyPurposeId id_kp_codeSigning = new KeyPurposeId("1.3.6.1.5.5.7.3.3");
/* 28 */   public static final KeyPurposeId id_kp_emailProtection = new KeyPurposeId("1.3.6.1.5.5.7.3.4");
/* 29 */   public static final KeyPurposeId id_kp_ipsecEndSystem = new KeyPurposeId("1.3.6.1.5.5.7.3.5");
/* 30 */   public static final KeyPurposeId id_kp_ipsecTunnel = new KeyPurposeId("1.3.6.1.5.5.7.3.6");
/* 31 */   public static final KeyPurposeId id_kp_ipsecUser = new KeyPurposeId("1.3.6.1.5.5.7.3.7");
/* 32 */   public static final KeyPurposeId id_kp_timeStamping = new KeyPurposeId("1.3.6.1.5.5.7.3.8");
/* 33 */   public static final KeyPurposeId id_kp_OCSPSigning = new KeyPurposeId("1.3.6.1.5.5.7.3.9");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 38 */   public static final KeyPurposeId id_kp_smartcardlogon = new KeyPurposeId("1.3.6.1.4.1.311.20.2.2");
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/KeyPurposeId.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */