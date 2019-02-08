/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract interface X509ObjectIdentifiers
/*    */ {
/*    */   public static final String id = "2.5.4";
/* 12 */   public static final DERObjectIdentifier commonName = new DERObjectIdentifier("2.5.4.3");
/* 13 */   public static final DERObjectIdentifier countryName = new DERObjectIdentifier("2.5.4.6");
/* 14 */   public static final DERObjectIdentifier localityName = new DERObjectIdentifier("2.5.4.7");
/* 15 */   public static final DERObjectIdentifier stateOrProvinceName = new DERObjectIdentifier("2.5.4.8");
/* 16 */   public static final DERObjectIdentifier organization = new DERObjectIdentifier("2.5.4.10");
/* 17 */   public static final DERObjectIdentifier organizationalUnitName = new DERObjectIdentifier("2.5.4.11");
/*    */   
/*    */ 
/*    */ 
/* 21 */   public static final DERObjectIdentifier id_SHA1 = new DERObjectIdentifier("1.3.14.3.2.26");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 27 */   public static final DERObjectIdentifier ripemd160 = new DERObjectIdentifier("1.3.36.3.2.1");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 33 */   public static final DERObjectIdentifier ripemd160WithRSAEncryption = new DERObjectIdentifier("1.3.36.3.3.1.2");
/*    */   
/*    */ 
/* 36 */   public static final DERObjectIdentifier id_ea_rsa = new DERObjectIdentifier("2.5.8.1.1");
/*    */   
/*    */ 
/* 39 */   public static final DERObjectIdentifier id_pkix = new DERObjectIdentifier("1.3.6.1.5.5.7");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 44 */   public static final DERObjectIdentifier id_pe = new DERObjectIdentifier(id_pkix + ".1");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 49 */   public static final DERObjectIdentifier id_ad = new DERObjectIdentifier(id_pkix + ".48");
/* 50 */   public static final DERObjectIdentifier id_ad_caIssuers = new DERObjectIdentifier(id_ad + ".2");
/* 51 */   public static final DERObjectIdentifier id_ad_ocsp = new DERObjectIdentifier(id_ad + ".1");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 56 */   public static final DERObjectIdentifier ocspAccessMethod = id_ad_ocsp;
/* 57 */   public static final DERObjectIdentifier crlAccessMethod = id_ad_caIssuers;
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/X509ObjectIdentifiers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */