/*    */ package org.gudy.bouncycastle.asn1.x9;
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
/*    */ public abstract interface X9ObjectIdentifiers
/*    */ {
/*    */   public static final String ansi_X9_62 = "1.2.840.10045";
/*    */   public static final String id_fieldType = "1.2.840.10045.1";
/* 16 */   public static final DERObjectIdentifier prime_field = new DERObjectIdentifier("1.2.840.10045.1.1");
/*    */   
/*    */ 
/* 19 */   public static final DERObjectIdentifier characteristic_two_field = new DERObjectIdentifier("1.2.840.10045.1.2");
/*    */   
/*    */ 
/* 22 */   public static final DERObjectIdentifier gnBasis = new DERObjectIdentifier("1.2.840.10045.1.2.3.1");
/*    */   
/*    */ 
/* 25 */   public static final DERObjectIdentifier tpBasis = new DERObjectIdentifier("1.2.840.10045.1.2.3.2");
/*    */   
/*    */ 
/* 28 */   public static final DERObjectIdentifier ppBasis = new DERObjectIdentifier("1.2.840.10045.1.2.3.3");
/*    */   
/*    */ 
/*    */   public static final String id_ecSigType = "1.2.840.10045.4";
/*    */   
/* 33 */   public static final DERObjectIdentifier ecdsa_with_SHA1 = new DERObjectIdentifier("1.2.840.10045.4.1");
/*    */   
/*    */ 
/*    */   public static final String id_publicKeyType = "1.2.840.10045.2";
/*    */   
/* 38 */   public static final DERObjectIdentifier id_ecPublicKey = new DERObjectIdentifier("1.2.840.10045.2.1");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final String ellipticCurve = "1.2.840.10045.3";
/*    */   
/*    */ 
/*    */ 
/*    */   public static final String primeCurve = "1.2.840.10045.3.1";
/*    */   
/*    */ 
/*    */ 
/* 51 */   public static final DERObjectIdentifier prime192v1 = new DERObjectIdentifier("1.2.840.10045.3.1.1");
/*    */   
/* 53 */   public static final DERObjectIdentifier prime192v2 = new DERObjectIdentifier("1.2.840.10045.3.1.2");
/*    */   
/* 55 */   public static final DERObjectIdentifier prime192v3 = new DERObjectIdentifier("1.2.840.10045.3.1.3");
/*    */   
/* 57 */   public static final DERObjectIdentifier prime239v1 = new DERObjectIdentifier("1.2.840.10045.3.1.4");
/*    */   
/* 59 */   public static final DERObjectIdentifier prime239v2 = new DERObjectIdentifier("1.2.840.10045.3.1.5");
/*    */   
/* 61 */   public static final DERObjectIdentifier prime239v3 = new DERObjectIdentifier("1.2.840.10045.3.1.6");
/*    */   
/* 63 */   public static final DERObjectIdentifier prime256v1 = new DERObjectIdentifier("1.2.840.10045.3.1.7");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 72 */   public static final DERObjectIdentifier dhpublicnumber = new DERObjectIdentifier("1.2.840.10046.2.1");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 79 */   public static final DERObjectIdentifier id_dsa = new DERObjectIdentifier("1.2.840.10040.4.1");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 85 */   public static final DERObjectIdentifier id_dsa_with_sha1 = new DERObjectIdentifier("1.2.840.10040.4.3");
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x9/X9ObjectIdentifiers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */