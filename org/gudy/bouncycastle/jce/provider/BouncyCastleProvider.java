/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import java.security.Provider;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class BouncyCastleProvider
/*     */   extends Provider
/*     */ {
/*  31 */   private static String info = "BouncyCastle Security Provider v1.23";
/*     */   
/*  33 */   public static String PROVIDER_NAME = "BC_VUZE";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BouncyCastleProvider()
/*     */   {
/*  42 */     super(PROVIDER_NAME, 1.23D, info);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  47 */     put("KeyStore.BKS", "org.gudy.bouncycastle.jce.provider.JDKKeyStore");
/*  48 */     put("KeyStore.BouncyCastle", "org.gudy.bouncycastle.jce.provider.JDKKeyStore$BouncyCastleStore");
/*  49 */     put("KeyStore.PKCS12", "org.gudy.bouncycastle.jce.provider.JDKPKCS12KeyStore$BCPKCS12KeyStore");
/*  50 */     put("KeyStore.BCPKCS12", "org.gudy.bouncycastle.jce.provider.JDKPKCS12KeyStore$BCPKCS12KeyStore");
/*  51 */     put("KeyStore.PKCS12-DEF", "org.gudy.bouncycastle.jce.provider.JDKPKCS12KeyStore$DefPKCS12KeyStore");
/*  52 */     put("Alg.Alias.KeyStore.UBER", "BouncyCastle");
/*  53 */     put("Alg.Alias.KeyStore.BOUNCYCASTLE", "BouncyCastle");
/*  54 */     put("Alg.Alias.KeyStore.bouncycastle", "BouncyCastle");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  59 */     put("CertificateFactory.X.509", "org.gudy.bouncycastle.jce.provider.JDKX509CertificateFactory");
/*  60 */     put("Alg.Alias.CertificateFactory.X509", "X.509");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  65 */     put("AlgorithmParameterGenerator.DH", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameterGenerator$DH");
/*  66 */     put("AlgorithmParameterGenerator.DSA", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameterGenerator$DSA");
/*  67 */     put("AlgorithmParameterGenerator.ELGAMAL", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameterGenerator$ElGamal");
/*  68 */     put("AlgorithmParameterGenerator.DES", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameterGenerator$DES");
/*  69 */     put("AlgorithmParameterGenerator.DESEDE", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameterGenerator$DES");
/*  70 */     put("AlgorithmParameterGenerator.1.2.840.113549.3.7", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameterGenerator$DES");
/*  71 */     put("AlgorithmParameterGenerator.1.3.14.3.2.7", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameterGenerator$DES");
/*  72 */     put("AlgorithmParameterGenerator.IDEA", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameterGenerator$IDEA");
/*  73 */     put("AlgorithmParameterGenerator.1.3.6.1.4.1.188.7.1.1.2", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameterGenerator$IDEA");
/*  74 */     put("AlgorithmParameterGenerator.RC2", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameterGenerator$RC2");
/*  75 */     put("AlgorithmParameterGenerator.1.2.840.113549.3.2", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameterGenerator$RC2");
/*  76 */     put("AlgorithmParameterGenerator.CAST5", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameterGenerator$CAST5");
/*  77 */     put("AlgorithmParameterGenerator.1.2.840.113533.7.66.10", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameterGenerator$CAST5");
/*  78 */     put("AlgorithmParameterGenerator.AES", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameterGenerator$AES");
/*  79 */     put("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.2", "AES");
/*  80 */     put("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.22", "AES");
/*  81 */     put("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.42", "AES");
/*  82 */     put("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.1.2", "AES");
/*  83 */     put("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.1.22", "AES");
/*  84 */     put("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.1.42", "AES");
/*     */     
/*     */ 
/*     */ 
/*  88 */     put("AlgorithmParameters.DH", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$DH");
/*  89 */     put("AlgorithmParameters.DSA", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$DSA");
/*  90 */     put("AlgorithmParameters.ELGAMAL", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$ElGamal");
/*  91 */     put("AlgorithmParameters.IES", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$IES");
/*  92 */     put("AlgorithmParameters.PKCS12PBE", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$PKCS12PBE");
/*  93 */     put("AlgorithmParameters.1.2.840.113549.3.7", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$IVAlgorithmParameters");
/*  94 */     put("AlgorithmParameters.IDEA", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$IDEAAlgorithmParameters");
/*  95 */     put("AlgorithmParameters.1.3.6.1.4.1.188.7.1.1.2", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$IDEAAlgorithmParameters");
/*  96 */     put("AlgorithmParameters.CAST5", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$CAST5AlgorithmParameters");
/*  97 */     put("AlgorithmParameters.1.2.840.113533.7.66.10", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$CAST5AlgorithmParameters");
/*  98 */     put("Alg.Alias.AlgorithmParameters.PBEWITHSHA1ANDRC2", "PKCS12PBE");
/*  99 */     put("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND3-KEYTRIPLEDES", "PKCS12PBE");
/* 100 */     put("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND2-KEYTRIPLEDES", "PKCS12PBE");
/* 101 */     put("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDRC2", "PKCS12PBE");
/* 102 */     put("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDRC4", "PKCS12PBE");
/* 103 */     put("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDTWOFISH", "PKCS12PBE");
/* 104 */     put("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDIDEA", "PKCS12PBE");
/* 105 */     put("Alg.Alias.AlgorithmParameters.1.2.840.113549.1.12.1.1", "PKCS12PBE");
/* 106 */     put("Alg.Alias.AlgorithmParameters.1.2.840.113549.1.12.1.2", "PKCS12PBE");
/* 107 */     put("Alg.Alias.AlgorithmParameters.1.2.840.113549.1.12.1.3", "PKCS12PBE");
/* 108 */     put("Alg.Alias.AlgorithmParameters.1.2.840.113549.1.12.1.4", "PKCS12PBE");
/* 109 */     put("Alg.Alias.AlgorithmParameters.1.2.840.113549.1.12.1.5", "PKCS12PBE");
/* 110 */     put("Alg.Alias.AlgorithmParameters.1.2.840.113549.1.12.1.6", "PKCS12PBE");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 115 */     put("KeyAgreement.DH", "org.gudy.bouncycastle.jce.provider.JCEDHKeyAgreement");
/* 116 */     put("KeyAgreement.ECDH", "org.gudy.bouncycastle.jce.provider.JCEECDHKeyAgreement$DH");
/* 117 */     put("KeyAgreement.ECDHC", "org.gudy.bouncycastle.jce.provider.JCEECDHKeyAgreement$DHC");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 122 */     put("Cipher.DES", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$DES");
/* 123 */     put("Cipher.DESEDE", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$DESede");
/* 124 */     put("Cipher.1.2.840.113549.3.7", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$DESedeCBC");
/* 125 */     put("Cipher.1.3.14.3.2.7", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$DESCBC");
/* 126 */     put("Cipher.DESEDEWRAP", "org.gudy.bouncycastle.jce.provider.WrapCipherSpi$DESEDEWrap");
/* 127 */     put("Cipher.1.2.840.113549.1.9.16.3.6", "org.gudy.bouncycastle.jce.provider.WrapCipherSpi$DESEDEWrap");
/* 128 */     put("Cipher.SKIPJACK", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$Skipjack");
/* 129 */     put("Cipher.BLOWFISH", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$Blowfish");
/* 130 */     put("Cipher.TWOFISH", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$Twofish");
/* 131 */     put("Cipher.RC2", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$RC2");
/* 132 */     put("Cipher.RC2WRAP", "org.gudy.bouncycastle.jce.provider.WrapCipherSpi$RC2Wrap");
/* 133 */     put("Cipher.1.2.840.113549.1.9.16.3.7", "org.gudy.bouncycastle.jce.provider.WrapCipherSpi$RC2Wrap");
/* 134 */     put("Cipher.ARC4", "org.gudy.bouncycastle.jce.provider.JCEStreamCipher$RC4");
/* 135 */     put("Cipher.RC4", "org.gudy.bouncycastle.jce.provider.JCEStreamCipher$RC4");
/* 136 */     put("Alg.Alias.Cipher.1.2.840.113549.3.4", "RC4");
/* 137 */     put("Cipher.RC5", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$RC5");
/* 138 */     put("Cipher.1.2.840.113549.3.2", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$RC2CBC");
/* 139 */     put("Alg.Alias.Cipher.RC5-32", "RC5");
/* 140 */     put("Cipher.RC5-64", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$RC564");
/* 141 */     put("Cipher.RC6", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$RC6");
/* 142 */     put("Cipher.RIJNDAEL", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$Rijndael");
/* 143 */     put("Cipher.AES", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$AES");
/* 144 */     put("Alg.Alias.Cipher.2.16.840.1.101.3.4.2", "AES");
/* 145 */     put("Alg.Alias.Cipher.2.16.840.1.101.3.4.22", "AES");
/* 146 */     put("Alg.Alias.Cipher.2.16.840.1.101.3.4.42", "AES");
/* 147 */     put("Cipher.2.16.840.1.101.3.4.1.2", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$AESCBC");
/* 148 */     put("Cipher.2.16.840.1.101.3.4.1.22", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$AESCBC");
/* 149 */     put("Cipher.2.16.840.1.101.3.4.1.42", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$AESCBC");
/* 150 */     put("Cipher.AESWRAP", "org.gudy.bouncycastle.jce.provider.WrapCipherSpi$AESWrap");
/* 151 */     put("Cipher.SERPENT", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$Serpent");
/* 152 */     put("Cipher.CAST5", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$CAST5");
/* 153 */     put("Cipher.1.2.840.113533.7.66.10", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$CAST5CBC");
/* 154 */     put("Cipher.CAST6", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$CAST6");
/* 155 */     put("Cipher.IDEA", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$IDEA");
/* 156 */     put("Cipher.1.3.6.1.4.1.188.7.1.1.2", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$IDEACBC");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 188 */     put("Cipher.RSA", "org.gudy.bouncycastle.jce.provider.JCERSACipher$NoPadding");
/* 189 */     put("Cipher.RSA/RAW", "org.gudy.bouncycastle.jce.provider.JCERSACipher$NoPadding");
/* 190 */     put("Cipher.RSA/PKCS1", "org.gudy.bouncycastle.jce.provider.JCERSACipher$PKCS1v1_5Padding");
/* 191 */     put("Cipher.1.2.840.113549.1.1.1", "org.gudy.bouncycastle.jce.provider.JCERSACipher$PKCS1v1_5Padding");
/* 192 */     put("Cipher.2.5.8.1.1", "org.gudy.bouncycastle.jce.provider.JCERSACipher$PKCS1v1_5Padding");
/* 193 */     put("Cipher.RSA/1", "org.gudy.bouncycastle.jce.provider.JCERSACipher$PKCS1v1_5Padding_PrivateOnly");
/* 194 */     put("Cipher.RSA/2", "org.gudy.bouncycastle.jce.provider.JCERSACipher$PKCS1v1_5Padding_PublicOnly");
/* 195 */     put("Cipher.RSA/OAEP", "org.gudy.bouncycastle.jce.provider.JCERSACipher$OAEPPadding");
/* 196 */     put("Cipher.1.2.840.113549.1.1.7", "org.gudy.bouncycastle.jce.provider.JCERSACipher$OAEPPadding");
/* 197 */     put("Cipher.RSA/ISO9796-1", "org.gudy.bouncycastle.jce.provider.JCERSACipher$ISO9796d1Padding");
/*     */     
/* 199 */     put("Cipher.ECIES", "org.gudy.bouncycastle.jce.provider.JCEIESCipher$ECIES");
/* 200 */     put("Cipher.ELGAMAL", "org.gudy.bouncycastle.jce.provider.JCEElGamalCipher$NoPadding");
/* 201 */     put("Cipher.ELGAMAL/PKCS1", "org.gudy.bouncycastle.jce.provider.JCEElGamalCipher$PKCS1v1_5Padding");
/*     */     
/* 203 */     put("Alg.Alias.Cipher.RSA//RAW", "RSA");
/* 204 */     put("Alg.Alias.Cipher.RSA//NOPADDING", "RSA");
/* 205 */     put("Alg.Alias.Cipher.RSA//PKCS1PADDING", "RSA/PKCS1");
/* 206 */     put("Alg.Alias.Cipher.RSA//OAEPPADDING", "RSA/OAEP");
/* 207 */     put("Alg.Alias.Cipher.RSA//ISO9796-1PADDING", "RSA/ISO9796-1");
/* 208 */     put("Alg.Alias.Cipher.RSA/ECB/NOPADDING", "RSA");
/* 209 */     put("Alg.Alias.Cipher.RSA/ECB/PKCS1PADDING", "RSA/PKCS1");
/* 210 */     put("Alg.Alias.Cipher.RSA/ECB/OAEPPADDING", "RSA/OAEP");
/* 211 */     put("Alg.Alias.Cipher.RSA/ECB/ISO9796-1PADDING", "RSA/ISO9796-1");
/* 212 */     put("Alg.Alias.Cipher.RSA/NONE/NOPADDING", "RSA");
/* 213 */     put("Alg.Alias.Cipher.RSA/NONE/PKCS1PADDING", "RSA/PKCS1");
/* 214 */     put("Alg.Alias.Cipher.RSA/NONE/OAEPPADDING", "RSA/OAEP");
/* 215 */     put("Alg.Alias.Cipher.RSA/NONE/ISO9796-1PADDING", "RSA/ISO9796-1");
/* 216 */     put("Alg.Alias.Cipher.RSA/1/PCKS1PADDING", "RSA/1");
/* 217 */     put("Alg.Alias.Cipher.RSA/2/PCKS1PADDING", "RSA/2");
/*     */     
/* 219 */     put("Alg.Alias.Cipher.ELGAMAL/ECB/PKCS1PADDING", "ELGAMAL/PKCS1");
/* 220 */     put("Alg.Alias.Cipher.ELGAMAL/NONE/PKCS1PADDING", "ELGAMAL/PKCS1");
/*     */     
/* 222 */     put("Cipher.PBEWITHMD5ANDDES", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$PBEWithMD5AndDES");
/* 223 */     put("Cipher.BROKENPBEWITHMD5ANDDES", "org.gudy.bouncycastle.jce.provider.BrokenJCEBlockCipher$BrokePBEWithMD5AndDES");
/* 224 */     put("Cipher.PBEWITHMD5ANDRC2", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$PBEWithMD5AndRC2");
/* 225 */     put("Cipher.PBEWITHSHA1ANDDES", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$PBEWithSHA1AndDES");
/* 226 */     put("Cipher.BROKENPBEWITHSHA1ANDDES", "org.gudy.bouncycastle.jce.provider.BrokenJCEBlockCipher$BrokePBEWithSHA1AndDES");
/* 227 */     put("Cipher.PBEWITHSHA1ANDRC2", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$PBEWithSHA1AndRC2");
/* 228 */     put("Cipher.PBEWITHSHAAND3-KEYTRIPLEDES-CBC", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$PBEWithSHAAndDES3Key");
/* 229 */     put("Cipher.BROKENPBEWITHSHAAND3-KEYTRIPLEDES-CBC", "org.gudy.bouncycastle.jce.provider.BrokenJCEBlockCipher$BrokePBEWithSHAAndDES3Key");
/* 230 */     put("Cipher.OLDPBEWITHSHAAND3-KEYTRIPLEDES-CBC", "org.gudy.bouncycastle.jce.provider.BrokenJCEBlockCipher$OldPBEWithSHAAndDES3Key");
/* 231 */     put("Cipher.PBEWITHSHAAND2-KEYTRIPLEDES-CBC", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$PBEWithSHAAndDES2Key");
/* 232 */     put("Cipher.BROKENPBEWITHSHAAND2-KEYTRIPLEDES-CBC", "org.gudy.bouncycastle.jce.provider.BrokenJCEBlockCipher$BrokePBEWithSHAAndDES2Key");
/* 233 */     put("Cipher.PBEWITHSHAAND128BITRC2-CBC", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$PBEWithSHAAnd128BitRC2");
/* 234 */     put("Cipher.PBEWITHSHAAND40BITRC2-CBC", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$PBEWithSHAAnd40BitRC2");
/* 235 */     put("Cipher.PBEWITHSHAAND128BITRC4", "org.gudy.bouncycastle.jce.provider.JCEStreamCipher$PBEWithSHAAnd128BitRC4");
/* 236 */     put("Cipher.PBEWITHSHAAND40BITRC4", "org.gudy.bouncycastle.jce.provider.JCEStreamCipher$PBEWithSHAAnd40BitRC4");
/*     */     
/* 238 */     put("Cipher.PBEWITHSHAANDTWOFISH-CBC", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$PBEWithSHAAndTwofish");
/* 239 */     put("Cipher.OLDPBEWITHSHAANDTWOFISH-CBC", "org.gudy.bouncycastle.jce.provider.BrokenJCEBlockCipher$OldPBEWithSHAAndTwofish");
/* 240 */     put("Cipher.PBEWITHSHAANDIDEA-CBC", "org.gudy.bouncycastle.jce.provider.JCEBlockCipher$PBEWithSHAAndIDEA");
/*     */     
/* 242 */     put("Alg.Alias.Cipher.1.2.840.113549.1.12.1.1", "PBEWITHSHAAND128BITRC4");
/* 243 */     put("Alg.Alias.Cipher.1.2.840.113549.1.12.1.2", "PBEWITHSHAAND40BITRC4");
/* 244 */     put("Alg.Alias.Cipher.1.2.840.113549.1.12.1.3", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
/* 245 */     put("Alg.Alias.Cipher.1.2.840.113549.1.12.1.4", "PBEWITHSHAAND2-KEYTRIPLEDES-CBC");
/* 246 */     put("Alg.Alias.Cipher.1.2.840.113549.1.12.1.5", "PBEWITHSHAAND128BITRC2-CBC");
/* 247 */     put("Alg.Alias.Cipher.1.2.840.113549.1.12.1.6", "PBEWITHSHAAND40BITRC2-CBC");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 252 */     put("KeyGenerator.DES", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$DES");
/* 253 */     put("Alg.Alias.KeyGenerator.1.3.14.3.2.7", "DES");
/* 254 */     put("KeyGenerator.DESEDE", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$DESede");
/* 255 */     put("KeyGenerator.1.2.840.113549.3.7", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$DESede3");
/* 256 */     put("KeyGenerator.DESEDEWRAP", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$DESede");
/* 257 */     put("KeyGenerator.SKIPJACK", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$Skipjack");
/* 258 */     put("KeyGenerator.BLOWFISH", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$Blowfish");
/* 259 */     put("KeyGenerator.TWOFISH", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$Twofish");
/* 260 */     put("KeyGenerator.RC2", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$RC2");
/* 261 */     put("KeyGenerator.1.2.840.113549.3.2", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$RC2");
/* 262 */     put("KeyGenerator.RC4", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$RC4");
/* 263 */     put("Alg.Alias.KeyGenerator.ARC4", "RC4");
/* 264 */     put("Alg.Alias.KeyGenerator.1.2.840.113549.3.4", "RC4");
/* 265 */     put("KeyGenerator.RC5", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$RC5");
/* 266 */     put("Alg.Alias.KeyGenerator.RC5-32", "RC5");
/* 267 */     put("KeyGenerator.RC5-64", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$RC564");
/* 268 */     put("KeyGenerator.RC6", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$RC6");
/* 269 */     put("KeyGenerator.RIJNDAEL", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$Rijndael");
/* 270 */     put("KeyGenerator.AES", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$AES");
/* 271 */     put("KeyGenerator.2.16.840.1.101.3.4.2", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$AES128");
/* 272 */     put("KeyGenerator.2.16.840.1.101.3.4.22", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$AES192");
/* 273 */     put("KeyGenerator.2.16.840.1.101.3.4.42", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$AES256");
/* 274 */     put("KeyGenerator.2.16.840.1.101.3.4.1.2", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$AES128");
/* 275 */     put("KeyGenerator.2.16.840.1.101.3.4.1.22", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$AES192");
/* 276 */     put("KeyGenerator.2.16.840.1.101.3.4.1.42", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$AES256");
/* 277 */     put("KeyGenerator.AESWRAP", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$AES");
/* 278 */     put("KeyGenerator.SERPENT", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$Serpent");
/* 279 */     put("KeyGenerator.CAST5", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$CAST5");
/* 280 */     put("KeyGenerator.1.2.840.113533.7.66.10", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$CAST5");
/* 281 */     put("KeyGenerator.CAST6", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$CAST6");
/* 282 */     put("KeyGenerator.IDEA", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$IDEA");
/* 283 */     put("KeyGenerator.1.3.6.1.4.1.188.7.1.1.2", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$IDEA");
/* 284 */     put("KeyGenerator.HMACMD2", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$MD2HMAC");
/* 285 */     put("KeyGenerator.HMACMD4", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$MD4HMAC");
/* 286 */     put("KeyGenerator.HMACMD5", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$MD5HMAC");
/* 287 */     put("KeyGenerator.HMACRIPEMD128", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$RIPEMD128HMAC");
/* 288 */     put("KeyGenerator.HMACRIPEMD160", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$RIPEMD160HMAC");
/* 289 */     put("KeyGenerator.HMACSHA1", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$HMACSHA1");
/* 290 */     put("KeyGenerator.HMACTIGER", "org.gudy.bouncycastle.jce.provider.JCEKeyGenerator$HMACTIGER");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 295 */     put("KeyPairGenerator.RSA", "org.gudy.bouncycastle.jce.provider.JDKKeyPairGenerator$RSA");
/* 296 */     put("KeyPairGenerator.DH", "org.gudy.bouncycastle.jce.provider.JDKKeyPairGenerator$DH");
/* 297 */     put("KeyPairGenerator.DSA", "org.gudy.bouncycastle.jce.provider.JDKKeyPairGenerator$DSA");
/* 298 */     put("KeyPairGenerator.ELGAMAL", "org.gudy.bouncycastle.jce.provider.JDKKeyPairGenerator$ElGamal");
/* 299 */     put("KeyPairGenerator.ECDSA", "org.gudy.bouncycastle.jce.provider.JDKKeyPairGenerator$ECDSA");
/* 300 */     put("KeyPairGenerator.ECDH", "org.gudy.bouncycastle.jce.provider.JDKKeyPairGenerator$ECDH");
/* 301 */     put("KeyPairGenerator.ECDHC", "org.gudy.bouncycastle.jce.provider.JDKKeyPairGenerator$ECDHC");
/* 302 */     put("KeyPairGenerator.ECIES", "org.gudy.bouncycastle.jce.provider.JDKKeyPairGenerator$ECDH");
/*     */     
/* 304 */     put("Alg.Alias.KeyPairGenerator.1.2.840.113549.1.1.1", "RSA");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 309 */     put("KeyFactory.RSA", "org.gudy.bouncycastle.jce.provider.JDKKeyFactory$RSA");
/* 310 */     put("KeyFactory.DH", "org.gudy.bouncycastle.jce.provider.JDKKeyFactory$DH");
/* 311 */     put("KeyFactory.DSA", "org.gudy.bouncycastle.jce.provider.JDKKeyFactory$DSA");
/* 312 */     put("KeyFactory.ELGAMAL", "org.gudy.bouncycastle.jce.provider.JDKKeyFactory$ElGamal");
/* 313 */     put("KeyFactory.ElGamal", "org.gudy.bouncycastle.jce.provider.JDKKeyFactory$ElGamal");
/* 314 */     put("KeyFactory.EC", "org.gudy.bouncycastle.jce.provider.JDKKeyFactory$EC");
/* 315 */     put("KeyFactory.ECDSA", "org.gudy.bouncycastle.jce.provider.JDKKeyFactory$ECDSA");
/* 316 */     put("KeyFactory.ECDH", "org.gudy.bouncycastle.jce.provider.JDKKeyFactory$ECDH");
/* 317 */     put("KeyFactory.ECDHC", "org.gudy.bouncycastle.jce.provider.JDKKeyFactory$ECDHC");
/*     */     
/* 319 */     put("Alg.Alias.KeyFactory.1.2.840.113549.1.1.1", "RSA");
/* 320 */     put("Alg.Alias.KeyFactory.1.2.840.10040.4.1", "DSA");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 325 */     put("AlgorithmParameters.DES", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$IVAlgorithmParameters");
/* 326 */     put("Alg.Alias.AlgorithmParameters.1.3.14.3.2.7", "DES");
/* 327 */     put("AlgorithmParameters.DESEDE", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$IVAlgorithmParameters");
/* 328 */     put("AlgorithmParameters.1.2.840.113549.3.7", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$IVAlgorithmParameters");
/* 329 */     put("AlgorithmParameters.RC2", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$RC2AlgorithmParameters");
/* 330 */     put("AlgorithmParameters.1.2.840.113549.3.2", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$RC2AlgorithmParameters");
/* 331 */     put("AlgorithmParameters.RC5", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$IVAlgorithmParameters");
/* 332 */     put("AlgorithmParameters.RC6", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$IVAlgorithmParameters");
/* 333 */     put("AlgorithmParameters.IDEA", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$IDEAAlgorithmParameters");
/* 334 */     put("AlgorithmParameters.BLOWFISH", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$IVAlgorithmParameters");
/* 335 */     put("AlgorithmParameters.TWOFISH", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$IVAlgorithmParameters");
/* 336 */     put("AlgorithmParameters.SKIPJACK", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$IVAlgorithmParameters");
/* 337 */     put("AlgorithmParameters.RIJNDAEL", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$IVAlgorithmParameters");
/* 338 */     put("AlgorithmParameters.AES", "org.gudy.bouncycastle.jce.provider.JDKAlgorithmParameters$IVAlgorithmParameters");
/* 339 */     put("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.2", "AES");
/* 340 */     put("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.22", "AES");
/* 341 */     put("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.42", "AES");
/* 342 */     put("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.1.2", "AES");
/* 343 */     put("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.1.22", "AES");
/* 344 */     put("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.1.42", "AES");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 349 */     put("SecretKeyFactory.PBE/PKCS5", "org.gudy.bouncycastle.jce.provider.JCESecretKeyFactory$PBE_PKCS5");
/* 350 */     put("SecretKeyFactory.PBE/PKCS12", "org.gudy.bouncycastle.jce.provider.JCESecretKeyFactory$PBE_PKCS12");
/* 351 */     put("SecretKeyFactory.DES", "org.gudy.bouncycastle.jce.provider.JCESecretKeyFactory$DES");
/* 352 */     put("SecretKeyFactory.DESEDE", "org.gudy.bouncycastle.jce.provider.JCESecretKeyFactory$DESede");
/* 353 */     put("SecretKeyFactory.DESEDE", "org.gudy.bouncycastle.jce.provider.JCESecretKeyFactory$DESede");
/*     */     
/* 355 */     put("Alg.Alias.SecretKeyFactory.PBE", "PBE/PKCS5");
/* 356 */     put("Alg.Alias.SecretKeyFactory.PBEWITHMD5ANDDES", "PBE/PKCS5");
/* 357 */     put("Alg.Alias.SecretKeyFactory.BROKENPBEWITHMD5ANDDES", "PBE/PKCS5");
/* 358 */     put("Alg.Alias.SecretKeyFactory.PBEWITHMD5ANDRC2", "PBE/PKCS5");
/* 359 */     put("Alg.Alias.SecretKeyFactory.PBEWITHSHA1ANDDES", "PBE/PKCS5");
/* 360 */     put("Alg.Alias.SecretKeyFactory.BROKENPBEWITHSHA1ANDDES", "PBE/PKCS5");
/* 361 */     put("Alg.Alias.SecretKeyFactory.PBEWITHSHA1ANDRC2", "PBE/PKCS5");
/* 362 */     put("Alg.Alias.SecretKeyFactory.PBEWITHSHAAND3-KEYTRIPLEDES-CBC", "PBE/PKCS12");
/* 363 */     put("Alg.Alias.SecretKeyFactory.OLDPBEWITHSHAAND3-KEYTRIPLEDES-CBC", "PBE/PKCS12");
/* 364 */     put("Alg.Alias.SecretKeyFactory.BROKENPBEWITHSHAAND3-KEYTRIPLEDES-CBC", "PBE/PKCS12");
/* 365 */     put("Alg.Alias.SecretKeyFactory.PBEWITHSHAAND2-KEYTRIPLEDES-CBC", "PBE/PKCS12");
/* 366 */     put("Alg.Alias.SecretKeyFactory.BROKENPBEWITHSHAAND2-KEYTRIPLEDES-CBC", "PBE/PKCS12");
/* 367 */     put("Alg.Alias.SecretKeyFactory.PBEWITHSHAAND128BITRC4", "PBE/PKCS12");
/* 368 */     put("Alg.Alias.SecretKeyFactory.PBEWITHSHAAND40BITRC4", "PBE/PKCS12");
/* 369 */     put("Alg.Alias.SecretKeyFactory.PBEWITHSHAAND128BITRC2-CBC", "PBE/PKCS12");
/* 370 */     put("Alg.Alias.SecretKeyFactory.PBEWITHSHAAND40BITRC2-CBC", "PBE/PKCS12");
/* 371 */     put("Alg.Alias.SecretKeyFactory.PBEWITHSHAANDTWOFISH-CBC", "PBE/PKCS12");
/* 372 */     put("Alg.Alias.SecretKeyFactory.OLDPBEWITHSHAANDTWOFISH-CBC", "PBE/PKCS12");
/* 373 */     put("Alg.Alias.SecretKeyFactory.PBEWITHSHAANDIDEA-CBC", "PBE/PKCS12");
/* 374 */     put("Alg.Alias.SecretKeyFactory.PBEWITHHMACSHA", "PBE/PKCS12");
/* 375 */     put("Alg.Alias.SecretKeyFactory.PBEWITHHMACRIPEMD160", "PBE/PKCS12");
/*     */     
/* 377 */     put("Alg.Alias.SecretKeyFactory.1.2.840.113549.1.12.1.1", "PBE/PKCS12");
/* 378 */     put("Alg.Alias.SecretKeyFactory.1.2.840.113549.1.12.1.2", "PBE/PKCS12");
/* 379 */     put("Alg.Alias.SecretKeyFactory.1.2.840.113549.1.12.1.3", "PBE/PKCS12");
/* 380 */     put("Alg.Alias.SecretKeyFactory.1.2.840.113549.1.12.1.4", "PBE/PKCS12");
/* 381 */     put("Alg.Alias.SecretKeyFactory.1.2.840.113549.1.12.1.5", "PBE/PKCS12");
/* 382 */     put("Alg.Alias.SecretKeyFactory.1.2.840.113549.1.12.1.6", "PBE/PKCS12");
/* 383 */     put("Alg.Alias.SecretKeyFactory.1.3.14.3.2.26", "PBE/PKCS12");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 388 */     put("Mac.DESMAC", "org.gudy.bouncycastle.jce.provider.JCEMac$DES");
/* 389 */     put("Alg.Alias.Mac.DES", "DESMAC");
/* 390 */     put("Mac.DESMAC/CFB8", "org.gudy.bouncycastle.jce.provider.JCEMac$DESCFB8");
/* 391 */     put("Alg.Alias.Mac.DES/CFB8", "DESMAC/CFB8");
/*     */     
/* 393 */     put("Mac.DESEDEMAC", "org.gudy.bouncycastle.jce.provider.JCEMac$DESede");
/* 394 */     put("Alg.Alias.Mac.DESEDE", "DESEDEMAC");
/* 395 */     put("Mac.DESEDEMAC/CFB8", "org.gudy.bouncycastle.jce.provider.JCEMac$DESedeCFB8");
/* 396 */     put("Alg.Alias.Mac.DESEDE/CFB8", "DESEDEMAC/CFB8");
/*     */     
/* 398 */     put("Mac.SKIPJACKMAC", "org.gudy.bouncycastle.jce.provider.JCEMac$Skipjack");
/* 399 */     put("Alg.Alias.Mac.SKIPJACK", "SKIPJACKMAC");
/* 400 */     put("Mac.SKIPJACKMAC/CFB8", "org.gudy.bouncycastle.jce.provider.JCEMac$SkipjackCFB8");
/* 401 */     put("Alg.Alias.Mac.SKIPJACK/CFB8", "SKIPJACKMAC/CFB8");
/*     */     
/* 403 */     put("Mac.IDEAMAC", "org.gudy.bouncycastle.jce.provider.JCEMac$IDEA");
/* 404 */     put("Alg.Alias.Mac.IDEA", "IDEAMAC");
/* 405 */     put("Mac.IDEAMAC/CFB8", "org.gudy.bouncycastle.jce.provider.JCEMac$IDEACFB8");
/* 406 */     put("Alg.Alias.Mac.IDEA/CFB8", "IDEAMAC/CFB8");
/*     */     
/* 408 */     put("Mac.RC2MAC", "org.gudy.bouncycastle.jce.provider.JCEMac$RC2");
/* 409 */     put("Alg.Alias.Mac.RC2", "RC2MAC");
/* 410 */     put("Mac.RC2MAC/CFB8", "org.gudy.bouncycastle.jce.provider.JCEMac$RC2CFB8");
/* 411 */     put("Alg.Alias.Mac.RC2/CFB8", "RC2MAC/CFB8");
/*     */     
/* 413 */     put("Mac.RC5MAC", "org.gudy.bouncycastle.jce.provider.JCEMac$RC5");
/* 414 */     put("Alg.Alias.Mac.RC5", "RC5MAC");
/* 415 */     put("Mac.RC5MAC/CFB8", "org.gudy.bouncycastle.jce.provider.JCEMac$RC5CFB8");
/* 416 */     put("Alg.Alias.Mac.RC5/CFB8", "RC5MAC/CFB8");
/*     */     
/* 418 */     put("Mac.HMACMD2", "org.gudy.bouncycastle.jce.provider.JCEMac$MD2");
/* 419 */     put("Alg.Alias.Mac.HMAC-MD2", "HMACMD2");
/* 420 */     put("Alg.Alias.Mac.HMAC/MD2", "HMACMD2");
/*     */     
/* 422 */     put("Mac.HMACMD4", "org.gudy.bouncycastle.jce.provider.JCEMac$MD4");
/* 423 */     put("Alg.Alias.Mac.HMAC-MD4", "HMACMD4");
/* 424 */     put("Alg.Alias.Mac.HMAC/MD4", "HMACMD4");
/*     */     
/* 426 */     put("Mac.HMACMD5", "org.gudy.bouncycastle.jce.provider.JCEMac$MD5");
/* 427 */     put("Alg.Alias.Mac.HMAC-MD5", "HMACMD5");
/* 428 */     put("Alg.Alias.Mac.HMAC/MD5", "HMACMD5");
/*     */     
/* 430 */     put("Mac.HMACRIPEMD128", "org.gudy.bouncycastle.jce.provider.JCEMac$RIPEMD128");
/* 431 */     put("Alg.Alias.Mac.HMAC-RIPEMD128", "HMACRIPEMD128");
/* 432 */     put("Alg.Alias.Mac.HMAC/RIPEMD128", "HMACRIPEMD128");
/*     */     
/* 434 */     put("Mac.HMACRIPEMD160", "org.gudy.bouncycastle.jce.provider.JCEMac$RIPEMD160");
/* 435 */     put("Alg.Alias.Mac.HMAC-RIPEMD160", "HMACRIPEMD160");
/* 436 */     put("Alg.Alias.Mac.HMAC/RIPEMD160", "HMACRIPEMD160");
/*     */     
/* 438 */     put("Mac.HMACSHA1", "org.gudy.bouncycastle.jce.provider.JCEMac$SHA1");
/* 439 */     put("Alg.Alias.Mac.HMAC-SHA1", "HMACSHA1");
/* 440 */     put("Alg.Alias.Mac.HMAC/SHA1", "HMACSHA1");
/*     */     
/* 442 */     put("Mac.HMACSHA256", "org.gudy.bouncycastle.jce.provider.JCEMac$SHA256");
/* 443 */     put("Alg.Alias.Mac.HMAC-SHA256", "HMACSHA256");
/* 444 */     put("Alg.Alias.Mac.HMAC/SHA256", "HMACSHA256");
/*     */     
/* 446 */     put("Mac.HMACSHA384", "org.gudy.bouncycastle.jce.provider.JCEMac$SHA384");
/* 447 */     put("Alg.Alias.Mac.HMAC-SHA384", "HMACSHA384");
/* 448 */     put("Alg.Alias.Mac.HMAC/SHA384", "HMACSHA384");
/*     */     
/* 450 */     put("Mac.HMACSHA512", "org.gudy.bouncycastle.jce.provider.JCEMac$SHA512");
/* 451 */     put("Alg.Alias.Mac.HMAC-SHA512", "HMACSHA512");
/* 452 */     put("Alg.Alias.Mac.HMAC/SHA512", "HMACSHA512");
/*     */     
/* 454 */     put("Mac.HMACTiger", "org.gudy.bouncycastle.jce.provider.JCEMac$Tiger");
/* 455 */     put("Alg.Alias.Mac.HMAC-Tiger", "HMACTiger");
/* 456 */     put("Alg.Alias.Mac.HMAC/Tiger", "HMACTiger");
/*     */     
/* 458 */     put("Mac.PBEWITHHMACSHA", "org.gudy.bouncycastle.jce.provider.JCEMac$PBEWithSHA");
/* 459 */     put("Mac.PBEWITHHMACRIPEMD160", "org.gudy.bouncycastle.jce.provider.JCEMac$PBEWithRIPEMD160");
/* 460 */     put("Alg.Alias.Mac.1.3.14.3.2.26", "PBEWITHHMACSHA");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 465 */     put("MessageDigest.SHA-1", "org.gudy.bouncycastle.jce.provider.JDKMessageDigest$SHA1");
/* 466 */     put("Alg.Alias.MessageDigest.SHA1", "SHA-1");
/* 467 */     put("Alg.Alias.MessageDigest.SHA", "SHA-1");
/* 468 */     put("Alg.Alias.MessageDigest.1.3.14.3.2.26", "SHA-1");
/* 469 */     put("MessageDigest.SHA-256", "org.gudy.bouncycastle.jce.provider.JDKMessageDigest$SHA256");
/* 470 */     put("MessageDigest.SHA-384", "org.gudy.bouncycastle.jce.provider.JDKMessageDigest$SHA384");
/* 471 */     put("MessageDigest.SHA-512", "org.gudy.bouncycastle.jce.provider.JDKMessageDigest$SHA512");
/* 472 */     put("MessageDigest.MD2", "org.gudy.bouncycastle.jce.provider.JDKMessageDigest$MD2");
/* 473 */     put("MessageDigest.MD4", "org.gudy.bouncycastle.jce.provider.JDKMessageDigest$MD4");
/* 474 */     put("MessageDigest.MD5", "org.gudy.bouncycastle.jce.provider.JDKMessageDigest$MD5");
/* 475 */     put("MessageDigest.1.2.840.113549.2.5", "org.gudy.bouncycastle.jce.provider.JDKMessageDigest$MD5");
/* 476 */     put("MessageDigest.RIPEMD128", "org.gudy.bouncycastle.jce.provider.JDKMessageDigest$RIPEMD128");
/* 477 */     put("MessageDigest.RIPEMD160", "org.gudy.bouncycastle.jce.provider.JDKMessageDigest$RIPEMD160");
/* 478 */     put("MessageDigest.RIPEMD256", "org.gudy.bouncycastle.jce.provider.JDKMessageDigest$RIPEMD256");
/* 479 */     put("MessageDigest.RIPEMD320", "org.gudy.bouncycastle.jce.provider.JDKMessageDigest$RIPEMD320");
/* 480 */     put("MessageDigest.Tiger", "org.gudy.bouncycastle.jce.provider.JDKMessageDigest$Tiger");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 485 */     put("Signature.MD2WithRSAEncryption", "org.gudy.bouncycastle.jce.provider.JDKDigestSignature$MD2WithRSAEncryption");
/* 486 */     put("Signature.MD5WithRSAEncryption", "org.gudy.bouncycastle.jce.provider.JDKDigestSignature$MD5WithRSAEncryption");
/* 487 */     put("Signature.SHA1WithRSAEncryption", "org.gudy.bouncycastle.jce.provider.JDKDigestSignature$SHA1WithRSAEncryption");
/* 488 */     put("Signature.RIPEMD160WithRSAEncryption", "org.gudy.bouncycastle.jce.provider.JDKDigestSignature$RIPEMD160WithRSAEncryption");
/* 489 */     put("Signature.RIPEMD128WithRSAEncryption", "org.gudy.bouncycastle.jce.provider.JDKDigestSignature$RIPEMD128WithRSAEncryption");
/* 490 */     put("Signature.RIPEMD256WithRSAEncryption", "org.gudy.bouncycastle.jce.provider.JDKDigestSignature$RIPEMD256WithRSAEncryption");
/* 491 */     put("Signature.DSA", "org.gudy.bouncycastle.jce.provider.JDKDSASigner$stdDSA");
/* 492 */     put("Signature.ECDSA", "org.gudy.bouncycastle.jce.provider.JDKDSASigner$ecDSA");
/* 493 */     put("Signature.SHA1withRSA/ISO9796-2", "org.gudy.bouncycastle.jce.provider.JDKISOSignature$SHA1WithRSAEncryption");
/* 494 */     put("Signature.MD5withRSA/ISO9796-2", "org.gudy.bouncycastle.jce.provider.JDKISOSignature$MD5WithRSAEncryption");
/* 495 */     put("Signature.RIPEMD160withRSA/ISO9796-2", "org.gudy.bouncycastle.jce.provider.JDKISOSignature$RIPEMD160WithRSAEncryption");
/*     */     
/* 497 */     put("Signature.SHA1withRSA/PSS", "org.gudy.bouncycastle.jce.provider.JDKPSSSigner$SHA1withRSA");
/* 498 */     put("Signature.SHA256withRSA/PSS", "org.gudy.bouncycastle.jce.provider.JDKPSSSigner$SHA256withRSA");
/* 499 */     put("Signature.SHA384withRSA/PSS", "org.gudy.bouncycastle.jce.provider.JDKPSSSigner$SHA384withRSA");
/* 500 */     put("Signature.SHA512withRSA/PSS", "org.gudy.bouncycastle.jce.provider.JDKPSSSigner$SHA512withRSA");
/*     */     
/* 502 */     put("Alg.Alias.Signature.MD2withRSAEncryption", "MD2WithRSAEncryption");
/* 503 */     put("Alg.Alias.Signature.MD5withRSAEncryption", "MD5WithRSAEncryption");
/* 504 */     put("Alg.Alias.Signature.SHA1withRSAEncryption", "SHA1WithRSAEncryption");
/*     */     
/* 506 */     put("Alg.Alias.Signature.SHA256withRSAEncryption", "SHA256withRSA/PSS");
/* 507 */     put("Alg.Alias.Signature.SHA384withRSAEncryption", "SHA384withRSA/PSS");
/* 508 */     put("Alg.Alias.Signature.SHA512withRSAEncryption", "SHA512withRSA/PSS");
/*     */     
/* 510 */     put("Alg.Alias.Signature.SHA256WithRSAEncryption", "SHA256withRSA/PSS");
/* 511 */     put("Alg.Alias.Signature.SHA384WithRSAEncryption", "SHA384withRSA/PSS");
/* 512 */     put("Alg.Alias.Signature.SHA512WithRSAEncryption", "SHA512withRSA/PSS");
/*     */     
/* 514 */     put("Alg.Alias.Signature.SHA256WITHRSAENCRYPTION", "SHA256withRSA/PSS");
/* 515 */     put("Alg.Alias.Signature.SHA384WITHRSAENCRYPTION", "SHA384withRSA/PSS");
/* 516 */     put("Alg.Alias.Signature.SHA512WITHRSAENCRYPTION", "SHA512withRSA/PSS");
/*     */     
/* 518 */     put("Alg.Alias.Signature.RIPEMD160withRSAEncryption", "RIPEMD160WithRSAEncryption");
/*     */     
/* 520 */     put("Alg.Alias.Signature.1.2.840.113549.1.1.2", "MD2WithRSAEncryption");
/* 521 */     put("Alg.Alias.Signature.MD2WithRSA", "MD2WithRSAEncryption");
/* 522 */     put("Alg.Alias.Signature.MD2withRSA", "MD2WithRSAEncryption");
/* 523 */     put("Alg.Alias.Signature.MD2/RSA", "MD2WithRSAEncryption");
/* 524 */     put("Alg.Alias.Signature.MD5WithRSA", "MD5WithRSAEncryption");
/* 525 */     put("Alg.Alias.Signature.MD5withRSA", "MD5WithRSAEncryption");
/* 526 */     put("Alg.Alias.Signature.MD5/RSA", "MD5WithRSAEncryption");
/* 527 */     put("Alg.Alias.Signature.1.2.840.113549.1.1.4", "MD5WithRSAEncryption");
/* 528 */     put("Alg.Alias.Signature.SHA1WithRSA", "SHA1WithRSAEncryption");
/* 529 */     put("Alg.Alias.Signature.SHA1withRSA", "SHA1WithRSAEncryption");
/* 530 */     put("Alg.Alias.Signature.SHA1/RSA", "SHA1WithRSAEncryption");
/* 531 */     put("Alg.Alias.Signature.SHA-1/RSA", "SHA1WithRSAEncryption");
/* 532 */     put("Alg.Alias.Signature.1.2.840.113549.1.1.5", "SHA1WithRSAEncryption");
/* 533 */     put("Alg.Alias.Signature.1.3.14.3.2.26with1.2.840.113549.1.1.1", "SHA1WithRSAEncryption");
/* 534 */     put("Alg.Alias.Signature.1.3.14.3.2.26with1.2.840.113549.1.1.5", "SHA1WithRSAEncryption");
/* 535 */     put("Alg.Alias.Signature.1.2.840.113549.2.5with1.2.840.113549.1.1.1", "MD5WithRSAEncryption");
/* 536 */     put("Alg.Alias.Signature.RIPEMD160WithRSA", "RIPEMD160WithRSAEncryption");
/* 537 */     put("Alg.Alias.Signature.RIPEMD160withRSA", "RIPEMD160WithRSAEncryption");
/* 538 */     put("Alg.Alias.Signature.RIPEMD128WithRSA", "RIPEMD128WithRSAEncryption");
/* 539 */     put("Alg.Alias.Signature.RIPEMD128withRSA", "RIPEMD128WithRSAEncryption");
/* 540 */     put("Alg.Alias.Signature.RIPEMD256WithRSA", "RIPEMD256WithRSAEncryption");
/* 541 */     put("Alg.Alias.Signature.RIPEMD256withRSA", "RIPEMD256WithRSAEncryption");
/* 542 */     put("Alg.Alias.Signature.RIPEMD-160/RSA", "RIPEMD160WithRSAEncryption");
/* 543 */     put("Alg.Alias.Signature.RMD160withRSA", "RIPEMD160WithRSAEncryption");
/* 544 */     put("Alg.Alias.Signature.RMD160/RSA", "RIPEMD160WithRSAEncryption");
/* 545 */     put("Alg.Alias.Signature.1.3.36.3.3.1.2", "RIPEMD160WithRSAEncryption");
/* 546 */     put("Alg.Alias.Signature.1.3.36.3.3.1.3", "RIPEMD128WithRSAEncryption");
/* 547 */     put("Alg.Alias.Signature.1.3.36.3.3.1.4", "RIPEMD256WithRSAEncryption");
/*     */     
/* 549 */     put("Alg.Alias.Signature.MD2WITHRSAENCRYPTION", "MD2WithRSAEncryption");
/* 550 */     put("Alg.Alias.Signature.MD5WITHRSAENCRYPTION", "MD5WithRSAEncryption");
/* 551 */     put("Alg.Alias.Signature.SHA1WITHRSAENCRYPTION", "SHA1WithRSAEncryption");
/* 552 */     put("Alg.Alias.Signature.RIPEMD160WITHRSAENCRYPTION", "RIPEMD160WithRSAEncryption");
/*     */     
/* 554 */     put("Alg.Alias.Signature.MD5WITHRSA", "MD5WithRSAEncryption");
/* 555 */     put("Alg.Alias.Signature.SHA1WITHRSA", "SHA1WithRSAEncryption");
/* 556 */     put("Alg.Alias.Signature.RIPEMD160WITHRSA", "RIPEMD160WithRSAEncryption");
/* 557 */     put("Alg.Alias.Signature.RMD160WITHRSA", "RIPEMD160WithRSAEncryption");
/* 558 */     put("Alg.Alias.Signature.RIPEMD160WITHRSA", "RIPEMD160WithRSAEncryption");
/*     */     
/* 560 */     put("Alg.Alias.Signature.SHA1withECDSA", "ECDSA");
/* 561 */     put("Alg.Alias.Signature.ECDSAwithSHA1", "ECDSA");
/* 562 */     put("Alg.Alias.Signature.SHA1WITHECDSA", "ECDSA");
/* 563 */     put("Alg.Alias.Signature.ECDSAWITHSHA1", "ECDSA");
/* 564 */     put("Alg.Alias.Signature.SHA1WithECDSA", "ECDSA");
/* 565 */     put("Alg.Alias.Signature.ECDSAWithSHA1", "ECDSA");
/* 566 */     put("Alg.Alias.Signature.1.2.840.10045.4.1", "ECDSA");
/* 567 */     put("Alg.Alias.Signature.SHA/DSA", "DSA");
/* 568 */     put("Alg.Alias.Signature.SHA1withDSA", "DSA");
/* 569 */     put("Alg.Alias.Signature.SHA1WITHDSA", "DSA");
/* 570 */     put("Alg.Alias.Signature.1.3.14.3.2.26with1.2.840.10040.4.1", "DSA");
/* 571 */     put("Alg.Alias.Signature.1.3.14.3.2.26with1.2.840.10040.4.3", "DSA");
/* 572 */     put("Alg.Alias.Signature.DSAwithSHA1", "DSA");
/* 573 */     put("Alg.Alias.Signature.DSAWITHSHA1", "DSA");
/* 574 */     put("Alg.Alias.Signature.SHA1WithDSA", "DSA");
/* 575 */     put("Alg.Alias.Signature.DSAWithSHA1", "DSA");
/* 576 */     put("Alg.Alias.Signature.1.2.840.10040.4.3", "DSA");
/* 577 */     put("Alg.Alias.Signature.MD5WithRSA/ISO9796-2", "MD5withRSA/ISO9796-2");
/* 578 */     put("Alg.Alias.Signature.SHA1WithRSA/ISO9796-2", "SHA1withRSA/ISO9796-2");
/* 579 */     put("Alg.Alias.Signature.RIPEMD160WithRSA/ISO9796-2", "RIPEMD160withRSA/ISO9796-2");
/*     */     
/*     */ 
/* 582 */     put("CertPathValidator.PKIX", "org.gudy.bouncycastle.jce.provider.PKIXCertPathValidatorSpi");
/* 583 */     put("CertPathValidator.PKIX ValidationAlgorithm", "RFC2459");
/* 584 */     put("CertPathBuilder.PKIX", "org.gudy.bouncycastle.jce.provider.PKIXCertPathBuilderSpi");
/* 585 */     put("CertPathBuilder.PKIX ValidationAlgorithm", "RFC2459");
/* 586 */     put("CertStore.Collection", "org.gudy.bouncycastle.jce.provider.CertStoreCollectionSpi");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/BouncyCastleProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */