/*     */ package org.gudy.bouncycastle.jce;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.KeyFactory;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.Signature;
/*     */ import java.security.SignatureException;
/*     */ import java.security.spec.InvalidKeySpecException;
/*     */ import java.security.spec.X509EncodedKeySpec;
/*     */ import java.util.Hashtable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Set;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DERInputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DEROutputStream;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.CertificationRequest;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.CertificationRequestInfo;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Name;
/*     */ import org.gudy.bouncycastle.jce.provider.BouncyCastleProvider;
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
/*     */ public class PKCS10CertificationRequest
/*     */   extends CertificationRequest
/*     */ {
/*  58 */   private static Hashtable algorithms = new Hashtable();
/*  59 */   private static Hashtable oids = new Hashtable();
/*     */   
/*     */   static
/*     */   {
/*  63 */     algorithms.put("MD2WITHRSAENCRYPTION", new DERObjectIdentifier("1.2.840.113549.1.1.2"));
/*  64 */     algorithms.put("MD2WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.2"));
/*  65 */     algorithms.put("MD5WITHRSAENCRYPTION", new DERObjectIdentifier("1.2.840.113549.1.1.4"));
/*  66 */     algorithms.put("MD5WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.4"));
/*  67 */     algorithms.put("RSAWITHMD5", new DERObjectIdentifier("1.2.840.113549.1.1.4"));
/*  68 */     algorithms.put("SHA1WITHRSAENCRYPTION", new DERObjectIdentifier("1.2.840.113549.1.1.5"));
/*  69 */     algorithms.put("SHA1WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.5"));
/*  70 */     algorithms.put("RSAWITHSHA1", new DERObjectIdentifier("1.2.840.113549.1.1.5"));
/*  71 */     algorithms.put("RIPEMD160WITHRSAENCRYPTION", new DERObjectIdentifier("1.3.36.3.3.1.2"));
/*  72 */     algorithms.put("RIPEMD160WITHRSA", new DERObjectIdentifier("1.3.36.3.3.1.2"));
/*  73 */     algorithms.put("SHA1WITHDSA", new DERObjectIdentifier("1.2.840.10040.4.3"));
/*  74 */     algorithms.put("DSAWITHSHA1", new DERObjectIdentifier("1.2.840.10040.4.3"));
/*  75 */     algorithms.put("SHA1WITHECDSA", new DERObjectIdentifier("1.2.840.10045.4.1"));
/*  76 */     algorithms.put("ECDSAWITHSHA1", new DERObjectIdentifier("1.2.840.10045.4.1"));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  81 */     oids.put(new DERObjectIdentifier("1.2.840.113549.1.1.5"), "SHA1WITHRSA");
/*  82 */     oids.put(new DERObjectIdentifier("1.2.840.113549.1.1.4"), "MD5WITHRSA");
/*  83 */     oids.put(new DERObjectIdentifier("1.2.840.113549.1.1.2"), "MD2WITHRSA");
/*  84 */     oids.put(new DERObjectIdentifier("1.2.840.10040.4.3"), "DSAWITHSHA1");
/*     */   }
/*     */   
/*     */ 
/*     */   private static ASN1Sequence toDERSequence(byte[] bytes)
/*     */   {
/*     */     try
/*     */     {
/*  92 */       ByteArrayInputStream bIn = new ByteArrayInputStream(bytes);
/*  93 */       DERInputStream dIn = new DERInputStream(bIn);
/*     */       
/*  95 */       return (ASN1Sequence)dIn.readObject();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  99 */       throw new IllegalArgumentException("badly encoded request");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PKCS10CertificationRequest(byte[] bytes)
/*     */   {
/* 110 */     super(toDERSequence(bytes));
/*     */   }
/*     */   
/*     */ 
/*     */   public PKCS10CertificationRequest(ASN1Sequence sequence)
/*     */   {
/* 116 */     super(sequence);
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
/*     */   public PKCS10CertificationRequest(String signatureAlgorithm, X509Name subject, PublicKey key, ASN1Set attributes, PrivateKey signingKey)
/*     */     throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException
/*     */   {
/* 131 */     this(signatureAlgorithm, subject, key, attributes, signingKey, BouncyCastleProvider.PROVIDER_NAME);
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
/*     */   public PKCS10CertificationRequest(String signatureAlgorithm, X509Name subject, PublicKey key, ASN1Set attributes, PrivateKey signingKey, String provider)
/*     */     throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException
/*     */   {
/* 147 */     DERObjectIdentifier sigOID = (DERObjectIdentifier)algorithms.get(signatureAlgorithm.toUpperCase());
/*     */     
/* 149 */     if (sigOID == null)
/*     */     {
/* 151 */       throw new IllegalArgumentException("Unknown signature type requested");
/*     */     }
/*     */     
/* 154 */     if (subject == null)
/*     */     {
/* 156 */       throw new IllegalArgumentException("subject must not be null");
/*     */     }
/*     */     
/* 159 */     if (key == null)
/*     */     {
/* 161 */       throw new IllegalArgumentException("public key must not be null");
/*     */     }
/*     */     
/* 164 */     this.sigAlgId = new AlgorithmIdentifier(sigOID, null);
/*     */     
/* 166 */     byte[] bytes = key.getEncoded();
/* 167 */     ByteArrayInputStream bIn = new ByteArrayInputStream(bytes);
/* 168 */     DERInputStream dIn = new DERInputStream(bIn);
/*     */     
/*     */     try
/*     */     {
/* 172 */       this.reqInfo = new CertificationRequestInfo(subject, new SubjectPublicKeyInfo((ASN1Sequence)dIn.readObject()), attributes);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 176 */       throw new IllegalArgumentException("can't encode public key");
/*     */     }
/*     */     
/* 179 */     Signature sig = null;
/*     */     
/*     */     try
/*     */     {
/* 183 */       sig = Signature.getInstance(this.sigAlgId.getObjectId().getId(), provider);
/*     */     }
/*     */     catch (NoSuchAlgorithmException e)
/*     */     {
/* 187 */       sig = Signature.getInstance(signatureAlgorithm, provider);
/*     */     }
/*     */     
/* 190 */     sig.initSign(signingKey);
/*     */     
/*     */     try
/*     */     {
/* 194 */       ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 195 */       DEROutputStream dOut = new DEROutputStream(bOut);
/*     */       
/* 197 */       dOut.writeObject(this.reqInfo);
/*     */       
/* 199 */       sig.update(bOut.toByteArray());
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 203 */       throw new SecurityException("exception encoding TBS cert request - " + e);
/*     */     }
/*     */     
/* 206 */     this.sigBits = new DERBitString(sig.sign());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PublicKey getPublicKey()
/*     */     throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException
/*     */   {
/* 216 */     return getPublicKey(BouncyCastleProvider.PROVIDER_NAME);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PublicKey getPublicKey(String provider)
/*     */     throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException
/*     */   {
/* 224 */     SubjectPublicKeyInfo subjectPKInfo = this.reqInfo.getSubjectPublicKeyInfo();
/*     */     
/*     */     try
/*     */     {
/* 228 */       X509EncodedKeySpec xspec = new X509EncodedKeySpec(new DERBitString(subjectPKInfo).getBytes());
/* 229 */       AlgorithmIdentifier keyAlg = subjectPKInfo.getAlgorithmId();
/*     */       
/* 231 */       return KeyFactory.getInstance(keyAlg.getObjectId().getId(), provider).generatePublic(xspec);
/*     */     }
/*     */     catch (InvalidKeySpecException e)
/*     */     {
/* 235 */       throw new InvalidKeyException("error encoding public key");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean verify()
/*     */     throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException
/*     */   {
/* 246 */     return verify(BouncyCastleProvider.PROVIDER_NAME);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean verify(String provider)
/*     */     throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException
/*     */   {
/* 254 */     Signature sig = null;
/*     */     
/*     */     try
/*     */     {
/* 258 */       sig = Signature.getInstance(this.sigAlgId.getObjectId().getId(), provider);
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (NoSuchAlgorithmException e)
/*     */     {
/*     */ 
/* 265 */       if (oids.get(this.sigAlgId.getObjectId().getId()) != null)
/*     */       {
/* 267 */         String signatureAlgorithm = (String)oids.get(this.sigAlgId.getObjectId().getId());
/*     */         
/* 269 */         sig = Signature.getInstance(signatureAlgorithm, provider);
/*     */       }
/*     */     }
/*     */     
/* 273 */     sig.initVerify(getPublicKey(provider));
/*     */     
/*     */     try
/*     */     {
/* 277 */       ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 278 */       DEROutputStream dOut = new DEROutputStream(bOut);
/*     */       
/* 280 */       dOut.writeObject(this.reqInfo);
/*     */       
/* 282 */       sig.update(bOut.toByteArray());
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 286 */       throw new SecurityException("exception encoding TBS cert request - " + e);
/*     */     }
/*     */     
/* 289 */     return sig.verify(this.sigBits.getBytes());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getEncoded()
/*     */   {
/* 297 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 298 */     DEROutputStream dOut = new DEROutputStream(bOut);
/*     */     
/*     */     try
/*     */     {
/* 302 */       dOut.writeObject(this);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 306 */       throw new RuntimeException(e.toString());
/*     */     }
/*     */     
/* 309 */     return bOut.toByteArray();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/PKCS10CertificationRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */