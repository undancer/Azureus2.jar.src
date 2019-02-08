/*     */ package org.gudy.bouncycastle.asn1.pkcs;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract interface PKCSObjectIdentifiers
/*     */ {
/*     */   public static final String pkcs_1 = "1.2.840.113549.1.1";
/*  12 */   public static final DERObjectIdentifier rsaEncryption = new DERObjectIdentifier("1.2.840.113549.1.1.1");
/*  13 */   public static final DERObjectIdentifier md2WithRSAEncryption = new DERObjectIdentifier("1.2.840.113549.1.1.2");
/*  14 */   public static final DERObjectIdentifier md4WithRSAEncryption = new DERObjectIdentifier("1.2.840.113549.1.1.3");
/*  15 */   public static final DERObjectIdentifier md5WithRSAEncryption = new DERObjectIdentifier("1.2.840.113549.1.1.4");
/*  16 */   public static final DERObjectIdentifier sha1WithRSAEncryption = new DERObjectIdentifier("1.2.840.113549.1.1.5");
/*  17 */   public static final DERObjectIdentifier srsaOAEPEncryptionSET = new DERObjectIdentifier("1.2.840.113549.1.1.6");
/*  18 */   public static final DERObjectIdentifier sha256WithRSAEncryption = new DERObjectIdentifier("1.2.840.113549.1.1.11");
/*  19 */   public static final DERObjectIdentifier sha384WithRSAEncryption = new DERObjectIdentifier("1.2.840.113549.1.1.12");
/*  20 */   public static final DERObjectIdentifier sha512WithRSAEncryption = new DERObjectIdentifier("1.2.840.113549.1.1.13");
/*     */   
/*     */ 
/*     */ 
/*     */   public static final String pkcs_3 = "1.2.840.113549.1.3";
/*     */   
/*     */ 
/*  27 */   public static final DERObjectIdentifier dhKeyAgreement = new DERObjectIdentifier("1.2.840.113549.1.3.1");
/*     */   
/*     */ 
/*     */ 
/*     */   public static final String pkcs_5 = "1.2.840.113549.1.5";
/*     */   
/*     */ 
/*     */ 
/*  35 */   public static final DERObjectIdentifier id_PBES2 = new DERObjectIdentifier("1.2.840.113549.1.5.13");
/*     */   
/*  37 */   public static final DERObjectIdentifier id_PBKDF2 = new DERObjectIdentifier("1.2.840.113549.1.5.12");
/*     */   
/*     */ 
/*     */ 
/*     */   public static final String encryptionAlgorithm = "1.2.840.113549.3";
/*     */   
/*     */ 
/*     */ 
/*  45 */   public static final DERObjectIdentifier des_EDE3_CBC = new DERObjectIdentifier("1.2.840.113549.3.7");
/*  46 */   public static final DERObjectIdentifier RC2_CBC = new DERObjectIdentifier("1.2.840.113549.3.2");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  56 */   public static final DERObjectIdentifier md2 = new DERObjectIdentifier("1.2.840.113549.2.2");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  62 */   public static final DERObjectIdentifier md5 = new DERObjectIdentifier("1.2.840.113549.2.5");
/*     */   
/*     */ 
/*     */ 
/*     */   public static final String pkcs_7 = "1.2.840.113549.1.7";
/*     */   
/*     */ 
/*  69 */   public static final DERObjectIdentifier data = new DERObjectIdentifier("1.2.840.113549.1.7.1");
/*  70 */   public static final DERObjectIdentifier signedData = new DERObjectIdentifier("1.2.840.113549.1.7.2");
/*  71 */   public static final DERObjectIdentifier envelopedData = new DERObjectIdentifier("1.2.840.113549.1.7.3");
/*  72 */   public static final DERObjectIdentifier signedAndEnvelopedData = new DERObjectIdentifier("1.2.840.113549.1.7.4");
/*  73 */   public static final DERObjectIdentifier digestedData = new DERObjectIdentifier("1.2.840.113549.1.7.5");
/*  74 */   public static final DERObjectIdentifier encryptedData = new DERObjectIdentifier("1.2.840.113549.1.7.6");
/*     */   
/*     */ 
/*     */ 
/*     */   public static final String pkcs_9 = "1.2.840.113549.1.9";
/*     */   
/*     */ 
/*     */ 
/*  82 */   public static final DERObjectIdentifier pkcs_9_at_emailAddress = new DERObjectIdentifier("1.2.840.113549.1.9.1");
/*  83 */   public static final DERObjectIdentifier pkcs_9_at_unstructuredName = new DERObjectIdentifier("1.2.840.113549.1.9.2");
/*  84 */   public static final DERObjectIdentifier pkcs_9_at_contentType = new DERObjectIdentifier("1.2.840.113549.1.9.3");
/*  85 */   public static final DERObjectIdentifier pkcs_9_at_messageDigest = new DERObjectIdentifier("1.2.840.113549.1.9.4");
/*  86 */   public static final DERObjectIdentifier pkcs_9_at_signingTime = new DERObjectIdentifier("1.2.840.113549.1.9.5");
/*  87 */   public static final DERObjectIdentifier pkcs_9_at_counterSignature = new DERObjectIdentifier("1.2.840.113549.1.9.6");
/*  88 */   public static final DERObjectIdentifier pkcs_9_at_challengePassword = new DERObjectIdentifier("1.2.840.113549.1.9.7");
/*  89 */   public static final DERObjectIdentifier pkcs_9_at_unstructuredAddress = new DERObjectIdentifier("1.2.840.113549.1.9.8");
/*  90 */   public static final DERObjectIdentifier pkcs_9_at_extendedCertificateAttributes = new DERObjectIdentifier("1.2.840.113549.1.9.9");
/*     */   
/*  92 */   public static final DERObjectIdentifier pkcs_9_at_signingDescription = new DERObjectIdentifier("1.2.840.113549.1.9.13");
/*  93 */   public static final DERObjectIdentifier pkcs_9_at_extensionRequest = new DERObjectIdentifier("1.2.840.113549.1.9.14");
/*  94 */   public static final DERObjectIdentifier pkcs_9_at_smimeCapabilities = new DERObjectIdentifier("1.2.840.113549.1.9.15");
/*     */   
/*  96 */   public static final DERObjectIdentifier pkcs_9_at_friendlyName = new DERObjectIdentifier("1.2.840.113549.1.9.20");
/*  97 */   public static final DERObjectIdentifier pkcs_9_at_localKeyId = new DERObjectIdentifier("1.2.840.113549.1.9.21");
/*     */   
/*  99 */   public static final DERObjectIdentifier x509certType = new DERObjectIdentifier("1.2.840.113549.1.9.22.1");
/*     */   
/* 101 */   public static final DERObjectIdentifier id_ct_compressedData = new DERObjectIdentifier("1.2.840.113549.1.9.16.1.9");
/*     */   
/* 103 */   public static final DERObjectIdentifier id_alg_PWRI_KEK = new DERObjectIdentifier("1.2.840.113549.1.9.16.3.9");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 108 */   public static final DERObjectIdentifier preferSignedData = new DERObjectIdentifier("1.2.840.113549.1.9.15.1");
/* 109 */   public static final DERObjectIdentifier canNotDecryptAny = new DERObjectIdentifier("1.2.840.113549.1.9.15.2");
/* 110 */   public static final DERObjectIdentifier sMIMECapabilitiesVersions = new DERObjectIdentifier("1.2.840.113549.1.9.15.3");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final String id_aa = "1.2.840.113549.1.9.16.2";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 126 */   public static final DERObjectIdentifier id_aa_encrypKeyPref = new DERObjectIdentifier("1.2.840.113549.1.9.16.2.11");
/*     */   
/*     */ 
/*     */   public static final String pkcs_12 = "1.2.840.113549.1.12";
/*     */   
/*     */ 
/*     */   public static final String bagtypes = "1.2.840.113549.1.12.10.1";
/*     */   
/*     */ 
/* 135 */   public static final DERObjectIdentifier keyBag = new DERObjectIdentifier("1.2.840.113549.1.12.10.1.1");
/* 136 */   public static final DERObjectIdentifier pkcs8ShroudedKeyBag = new DERObjectIdentifier("1.2.840.113549.1.12.10.1.2");
/* 137 */   public static final DERObjectIdentifier certBag = new DERObjectIdentifier("1.2.840.113549.1.12.10.1.3");
/* 138 */   public static final DERObjectIdentifier crlBag = new DERObjectIdentifier("1.2.840.113549.1.12.10.1.4");
/* 139 */   public static final DERObjectIdentifier secretBag = new DERObjectIdentifier("1.2.840.113549.1.12.10.1.5");
/* 140 */   public static final DERObjectIdentifier safeContentsBag = new DERObjectIdentifier("1.2.840.113549.1.12.10.1.6");
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/PKCSObjectIdentifiers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */