/*     */ package org.gudy.bouncycastle.openssl;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.security.KeyFactory;
/*     */ import java.security.KeyPair;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.PublicKey;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.security.cert.X509CRL;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.security.spec.DSAPrivateKeySpec;
/*     */ import java.security.spec.DSAPublicKeySpec;
/*     */ import java.security.spec.InvalidKeySpecException;
/*     */ import java.security.spec.KeySpec;
/*     */ import java.security.spec.PKCS8EncodedKeySpec;
/*     */ import java.security.spec.RSAPrivateCrtKeySpec;
/*     */ import java.security.spec.RSAPublicKeySpec;
/*     */ import java.security.spec.X509EncodedKeySpec;
/*     */ import java.util.StringTokenizer;
/*     */ import org.gudy.bouncycastle.asn1.ASN1InputStream;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Object;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.cms.ContentInfo;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.PrivateKeyInfo;
/*     */ import org.gudy.bouncycastle.asn1.sec.ECPrivateKeyStructure;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.x509.RSAPublicKeyStructure;
/*     */ import org.gudy.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
/*     */ import org.gudy.bouncycastle.asn1.x9.X9ObjectIdentifiers;
/*     */ import org.gudy.bouncycastle.jce.ECNamedCurveTable;
/*     */ import org.gudy.bouncycastle.jce.PKCS10CertificationRequest;
/*     */ import org.gudy.bouncycastle.jce.provider.BouncyCastleProvider;
/*     */ import org.gudy.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
/*     */ import org.gudy.bouncycastle.util.encoders.Base64;
/*     */ import org.gudy.bouncycastle.util.encoders.Hex;
/*     */ import org.gudy.bouncycastle.x509.X509AttributeCertificate;
/*     */ import org.gudy.bouncycastle.x509.X509V2AttributeCertificate;
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
/*     */ public class PEMReader
/*     */   extends BufferedReader
/*     */ {
/*     */   private final PasswordFinder pFinder;
/*     */   private final String provider;
/*     */   
/*     */   public PEMReader(Reader reader)
/*     */   {
/*  67 */     this(reader, null, BouncyCastleProvider.PROVIDER_NAME);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PEMReader(Reader reader, PasswordFinder pFinder)
/*     */   {
/*  80 */     this(reader, pFinder, BouncyCastleProvider.PROVIDER_NAME);
/*     */   }
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
/*     */   public PEMReader(Reader reader, PasswordFinder pFinder, String provider)
/*     */   {
/*  95 */     super(reader);
/*     */     
/*  97 */     this.pFinder = pFinder;
/*  98 */     this.provider = provider;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object readObject()
/*     */     throws IOException
/*     */   {
/*     */     String line;
/* 106 */     while ((line = readLine()) != null)
/*     */     {
/* 108 */       if (line.contains("-----BEGIN PUBLIC KEY"))
/*     */       {
/* 110 */         return readPublicKey("-----END PUBLIC KEY");
/*     */       }
/* 112 */       if (line.contains("-----BEGIN RSA PUBLIC KEY"))
/*     */       {
/* 114 */         return readRSAPublicKey("-----END RSA PUBLIC KEY");
/*     */       }
/* 116 */       if (line.contains("-----BEGIN CERTIFICATE REQUEST"))
/*     */       {
/* 118 */         return readCertificateRequest("-----END CERTIFICATE REQUEST");
/*     */       }
/* 120 */       if (line.contains("-----BEGIN NEW CERTIFICATE REQUEST"))
/*     */       {
/* 122 */         return readCertificateRequest("-----END NEW CERTIFICATE REQUEST");
/*     */       }
/* 124 */       if (line.contains("-----BEGIN CERTIFICATE"))
/*     */       {
/* 126 */         return readCertificate("-----END CERTIFICATE");
/*     */       }
/* 128 */       if (line.contains("-----BEGIN PKCS7"))
/*     */       {
/* 130 */         return readPKCS7("-----END PKCS7");
/*     */       }
/* 132 */       if (line.contains("-----BEGIN X509 CERTIFICATE"))
/*     */       {
/* 134 */         return readCertificate("-----END X509 CERTIFICATE");
/*     */       }
/* 136 */       if (line.contains("-----BEGIN X509 CRL"))
/*     */       {
/* 138 */         return readCRL("-----END X509 CRL");
/*     */       }
/* 140 */       if (line.contains("-----BEGIN ATTRIBUTE CERTIFICATE"))
/*     */       {
/* 142 */         return readAttributeCertificate("-----END ATTRIBUTE CERTIFICATE");
/*     */       }
/* 144 */       if (line.contains("-----BEGIN RSA PRIVATE KEY"))
/*     */       {
/*     */         try
/*     */         {
/* 148 */           return readKeyPair("RSA", "-----END RSA PRIVATE KEY");
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 152 */           throw new IOException("problem creating RSA private key: " + e.toString());
/*     */         }
/*     */       }
/*     */       
/* 156 */       if (line.contains("-----BEGIN DSA PRIVATE KEY"))
/*     */       {
/*     */         try
/*     */         {
/* 160 */           return readKeyPair("DSA", "-----END DSA PRIVATE KEY");
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 164 */           throw new IOException("problem creating DSA private key: " + e.toString());
/*     */         }
/*     */       }
/*     */       
/* 168 */       if (line.contains("-----BEGIN EC PARAMETERS-----"))
/*     */       {
/* 170 */         return readECParameters("-----END EC PARAMETERS-----");
/*     */       }
/* 172 */       if (line.contains("-----BEGIN EC PRIVATE KEY-----"))
/*     */       {
/* 174 */         return readECPrivateKey("-----END EC PRIVATE KEY-----");
/*     */       }
/*     */     }
/*     */     
/* 178 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   private byte[] readBytes(String endMarker)
/*     */     throws IOException
/*     */   {
/* 185 */     StringBuilder buf = new StringBuilder();
/*     */     String line;
/* 187 */     while ((line = readLine()) != null)
/*     */     {
/* 189 */       if (line.contains(endMarker)) {
/*     */         break;
/*     */       }
/*     */       
/* 193 */       buf.append(line.trim());
/*     */     }
/*     */     
/* 196 */     if (line == null)
/*     */     {
/* 198 */       throw new IOException(endMarker + " not found");
/*     */     }
/*     */     
/* 201 */     return Base64.decode(buf.toString());
/*     */   }
/*     */   
/*     */   private PublicKey readRSAPublicKey(String endMarker)
/*     */     throws IOException
/*     */   {
/* 207 */     ByteArrayInputStream bAIS = new ByteArrayInputStream(readBytes(endMarker));
/* 208 */     ASN1InputStream ais = new ASN1InputStream(bAIS);
/* 209 */     Object asnObject = ais.readObject();
/* 210 */     ASN1Sequence sequence = (ASN1Sequence)asnObject;
/* 211 */     RSAPublicKeyStructure rsaPubStructure = new RSAPublicKeyStructure(sequence);
/* 212 */     RSAPublicKeySpec keySpec = new RSAPublicKeySpec(rsaPubStructure.getModulus(), rsaPubStructure.getPublicExponent());
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 218 */       KeyFactory keyFact = KeyFactory.getInstance("RSA", this.provider);
/*     */       
/* 220 */       return keyFact.generatePublic(keySpec);
/*     */     }
/*     */     catch (NoSuchProviderException e)
/*     */     {
/* 224 */       throw new IOException("can't find provider " + this.provider);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 228 */       throw new IOException("problem extracting key: " + e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */   private PublicKey readPublicKey(String endMarker)
/*     */     throws IOException
/*     */   {
/* 235 */     KeySpec keySpec = new X509EncodedKeySpec(readBytes(endMarker));
/* 236 */     String[] algorithms = { "DSA", "RSA" };
/* 237 */     for (int i = 0; i < algorithms.length; i++)
/*     */     {
/*     */       try
/*     */       {
/* 241 */         KeyFactory keyFact = KeyFactory.getInstance(algorithms[i], this.provider);
/*     */         
/* 243 */         return keyFact.generatePublic(keySpec);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       catch (NoSuchAlgorithmException e) {}catch (InvalidKeySpecException e) {}catch (NoSuchProviderException e)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 257 */         throw new RuntimeException("can't find provider " + this.provider);
/*     */       }
/*     */     }
/*     */     
/* 261 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private X509Certificate readCertificate(String endMarker)
/*     */     throws IOException
/*     */   {
/* 274 */     ByteArrayInputStream bIn = new ByteArrayInputStream(readBytes(endMarker));
/*     */     
/*     */     try
/*     */     {
/* 278 */       CertificateFactory certFact = CertificateFactory.getInstance("X.509", this.provider);
/*     */       
/*     */ 
/* 281 */       return (X509Certificate)certFact.generateCertificate(bIn);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 285 */       throw new IOException("problem parsing cert: " + e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private X509CRL readCRL(String endMarker)
/*     */     throws IOException
/*     */   {
/* 299 */     ByteArrayInputStream bIn = new ByteArrayInputStream(readBytes(endMarker));
/*     */     
/*     */     try
/*     */     {
/* 303 */       CertificateFactory certFact = CertificateFactory.getInstance("X.509", this.provider);
/*     */       
/*     */ 
/* 306 */       return (X509CRL)certFact.generateCRL(bIn);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 310 */       throw new IOException("problem parsing cert: " + e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private PKCS10CertificationRequest readCertificateRequest(String endMarker)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 326 */       return new PKCS10CertificationRequest(readBytes(endMarker));
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 330 */       throw new IOException("problem parsing cert: " + e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private X509AttributeCertificate readAttributeCertificate(String endMarker)
/*     */     throws IOException
/*     */   {
/* 344 */     return new X509V2AttributeCertificate(readBytes(endMarker));
/*     */   }
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
/*     */   private ContentInfo readPKCS7(String endMarker)
/*     */     throws IOException
/*     */   {
/* 359 */     StringBuilder buf = new StringBuilder();
/* 360 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/*     */     String line;
/* 362 */     while ((line = readLine()) != null)
/*     */     {
/* 364 */       if (line.contains(endMarker)) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/* 369 */       line = line.trim();
/*     */       
/* 371 */       buf.append(line.trim());
/*     */       
/* 373 */       Base64.decode(buf.substring(0, buf.length() / 4 * 4), bOut);
/*     */       
/* 375 */       buf.delete(0, buf.length() / 4 * 4);
/*     */     }
/*     */     
/* 378 */     if (buf.length() != 0)
/*     */     {
/* 380 */       throw new RuntimeException("base64 data appears to be truncated");
/*     */     }
/*     */     
/* 383 */     if (line == null)
/*     */     {
/* 385 */       throw new IOException(endMarker + " not found");
/*     */     }
/*     */     
/* 388 */     ByteArrayInputStream bIn = new ByteArrayInputStream(bOut.toByteArray());
/*     */     
/*     */     try
/*     */     {
/* 392 */       ASN1InputStream aIn = new ASN1InputStream(bIn);
/*     */       
/* 394 */       return ContentInfo.getInstance(aIn.readObject());
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 398 */       throw new IOException("problem parsing PKCS7 object: " + e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private KeyPair readKeyPair(String type, String endMarker)
/*     */     throws Exception
/*     */   {
/* 410 */     boolean isEncrypted = false;
/* 411 */     String line = null;
/* 412 */     String dekInfo = null;
/* 413 */     StringBuilder buf = new StringBuilder();
/*     */     
/* 415 */     while ((line = readLine()) != null)
/*     */     {
/* 417 */       if (line.startsWith("Proc-Type: 4,ENCRYPTED"))
/*     */       {
/* 419 */         isEncrypted = true;
/*     */       }
/* 421 */       else if (line.startsWith("DEK-Info:"))
/*     */       {
/* 423 */         dekInfo = line.substring(10);
/*     */       } else {
/* 425 */         if (line.contains(endMarker)) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 431 */         buf.append(line.trim());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 438 */     byte[] keyBytes = Base64.decode(buf.toString());
/*     */     
/* 440 */     if (isEncrypted)
/*     */     {
/* 442 */       if (this.pFinder == null)
/*     */       {
/* 444 */         throw new IOException("No password finder specified, but a password is required");
/*     */       }
/*     */       
/* 447 */       char[] password = this.pFinder.getPassword();
/*     */       
/* 449 */       if (password == null)
/*     */       {
/* 451 */         throw new IOException("Password is null, but a password is required");
/*     */       }
/*     */       
/* 454 */       StringTokenizer tknz = new StringTokenizer(dekInfo, ",");
/* 455 */       String dekAlgName = tknz.nextToken();
/* 456 */       byte[] iv = Hex.decode(tknz.nextToken());
/*     */       
/* 458 */       keyBytes = PEMUtilities.crypt(false, this.provider, keyBytes, password, dekAlgName, iv);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 463 */     ByteArrayInputStream bIn = new ByteArrayInputStream(keyBytes);
/* 464 */     ASN1InputStream aIn = new ASN1InputStream(bIn);
/* 465 */     ASN1Sequence seq = (ASN1Sequence)aIn.readObject();
/*     */     KeySpec privSpec;
/* 467 */     KeySpec privSpec; KeySpec pubSpec; if (type.equals("RSA"))
/*     */     {
/*     */ 
/* 470 */       DERInteger mod = (DERInteger)seq.getObjectAt(1);
/* 471 */       DERInteger pubExp = (DERInteger)seq.getObjectAt(2);
/* 472 */       DERInteger privExp = (DERInteger)seq.getObjectAt(3);
/* 473 */       DERInteger p1 = (DERInteger)seq.getObjectAt(4);
/* 474 */       DERInteger p2 = (DERInteger)seq.getObjectAt(5);
/* 475 */       DERInteger exp1 = (DERInteger)seq.getObjectAt(6);
/* 476 */       DERInteger exp2 = (DERInteger)seq.getObjectAt(7);
/* 477 */       DERInteger crtCoef = (DERInteger)seq.getObjectAt(8);
/*     */       
/* 479 */       KeySpec pubSpec = new RSAPublicKeySpec(mod.getValue(), pubExp.getValue());
/*     */       
/* 481 */       privSpec = new RSAPrivateCrtKeySpec(mod.getValue(), pubExp.getValue(), privExp.getValue(), p1.getValue(), p2.getValue(), exp1.getValue(), exp2.getValue(), crtCoef.getValue());
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/*     */ 
/* 490 */       DERInteger p = (DERInteger)seq.getObjectAt(1);
/* 491 */       DERInteger q = (DERInteger)seq.getObjectAt(2);
/* 492 */       DERInteger g = (DERInteger)seq.getObjectAt(3);
/* 493 */       DERInteger y = (DERInteger)seq.getObjectAt(4);
/* 494 */       DERInteger x = (DERInteger)seq.getObjectAt(5);
/*     */       
/* 496 */       privSpec = new DSAPrivateKeySpec(x.getValue(), p.getValue(), q.getValue(), g.getValue());
/*     */       
/*     */ 
/* 499 */       pubSpec = new DSAPublicKeySpec(y.getValue(), p.getValue(), q.getValue(), g.getValue());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 504 */     KeyFactory fact = KeyFactory.getInstance(type, this.provider);
/*     */     
/* 506 */     return new KeyPair(fact.generatePublic(pubSpec), fact.generatePrivate(privSpec));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private ECNamedCurveParameterSpec readECParameters(String endMarker)
/*     */     throws IOException
/*     */   {
/* 514 */     DERObjectIdentifier oid = (DERObjectIdentifier)ASN1Object.fromByteArray(readBytes(endMarker));
/*     */     
/* 516 */     return ECNamedCurveTable.getParameterSpec(oid.getId());
/*     */   }
/*     */   
/*     */   private KeyPair readECPrivateKey(String endMarker)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 524 */       ECPrivateKeyStructure pKey = new ECPrivateKeyStructure((ASN1Sequence)ASN1Object.fromByteArray(readBytes(endMarker)));
/* 525 */       AlgorithmIdentifier algId = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, pKey.getParameters());
/* 526 */       PrivateKeyInfo privInfo = new PrivateKeyInfo(algId, pKey.getDERObject());
/* 527 */       SubjectPublicKeyInfo pubInfo = new SubjectPublicKeyInfo(algId, pKey.getPublicKey().getBytes());
/* 528 */       PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privInfo.getEncoded());
/* 529 */       X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubInfo.getEncoded());
/* 530 */       KeyFactory fact = KeyFactory.getInstance("ECDSA", this.provider);
/*     */       
/* 532 */       return new KeyPair(fact.generatePublic(pubSpec), fact.generatePrivate(privSpec));
/*     */     }
/*     */     catch (ClassCastException e)
/*     */     {
/* 536 */       throw new IOException("wrong ASN.1 object found in stream");
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 540 */       throw new IOException("problem parsing EC private key: " + e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/openssl/PEMReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */