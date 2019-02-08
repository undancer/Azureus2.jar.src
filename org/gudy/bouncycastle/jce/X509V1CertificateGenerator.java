/*     */ package org.gudy.bouncycastle.jce;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.Signature;
/*     */ import java.security.SignatureException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Date;
/*     */ import java.util.Hashtable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DERInputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERNull;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DEROutputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
/*     */ import org.gudy.bouncycastle.asn1.x509.TBSCertificateStructure;
/*     */ import org.gudy.bouncycastle.asn1.x509.Time;
/*     */ import org.gudy.bouncycastle.asn1.x509.V1TBSCertificateGenerator;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509CertificateStructure;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Name;
/*     */ import org.gudy.bouncycastle.jce.provider.BouncyCastleProvider;
/*     */ import org.gudy.bouncycastle.jce.provider.X509CertificateObject;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class X509V1CertificateGenerator
/*     */ {
/*     */   private V1TBSCertificateGenerator tbsGen;
/*     */   private DERObjectIdentifier sigOID;
/*     */   private AlgorithmIdentifier sigAlgId;
/*     */   private String signatureAlgorithm;
/*  47 */   private static Hashtable algorithms = new Hashtable();
/*     */   
/*     */   static
/*     */   {
/*  51 */     algorithms.put("MD2WITHRSAENCRYPTION", new DERObjectIdentifier("1.2.840.113549.1.1.2"));
/*  52 */     algorithms.put("MD2WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.2"));
/*  53 */     algorithms.put("MD5WITHRSAENCRYPTION", new DERObjectIdentifier("1.2.840.113549.1.1.4"));
/*  54 */     algorithms.put("MD5WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.4"));
/*  55 */     algorithms.put("SHA1WITHRSAENCRYPTION", new DERObjectIdentifier("1.2.840.113549.1.1.5"));
/*  56 */     algorithms.put("SHA1WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.5"));
/*  57 */     algorithms.put("RIPEMD160WITHRSAENCRYPTION", new DERObjectIdentifier("1.3.36.3.3.1.2"));
/*  58 */     algorithms.put("RIPEMD160WITHRSA", new DERObjectIdentifier("1.3.36.3.3.1.2"));
/*  59 */     algorithms.put("SHA1WITHDSA", new DERObjectIdentifier("1.2.840.10040.4.3"));
/*  60 */     algorithms.put("DSAWITHSHA1", new DERObjectIdentifier("1.2.840.10040.4.3"));
/*  61 */     algorithms.put("SHA1WITHECDSA", new DERObjectIdentifier("1.2.840.10045.4.1"));
/*  62 */     algorithms.put("ECDSAWITHSHA1", new DERObjectIdentifier("1.2.840.10045.4.1"));
/*     */   }
/*     */   
/*     */   public X509V1CertificateGenerator()
/*     */   {
/*  67 */     this.tbsGen = new V1TBSCertificateGenerator();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/*  75 */     this.tbsGen = new V1TBSCertificateGenerator();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setSerialNumber(BigInteger serialNumber)
/*     */   {
/*  84 */     this.tbsGen.setSerialNumber(new DERInteger(serialNumber));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setIssuerDN(X509Name issuer)
/*     */   {
/*  94 */     this.tbsGen.setIssuer(issuer);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setNotBefore(Date date)
/*     */   {
/* 100 */     this.tbsGen.setStartDate(new Time(date));
/*     */   }
/*     */   
/*     */ 
/*     */   public void setNotAfter(Date date)
/*     */   {
/* 106 */     this.tbsGen.setEndDate(new Time(date));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setSubjectDN(X509Name subject)
/*     */   {
/* 115 */     this.tbsGen.setSubject(subject);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setPublicKey(PublicKey key)
/*     */   {
/*     */     try
/*     */     {
/* 123 */       this.tbsGen.setSubjectPublicKeyInfo(new SubjectPublicKeyInfo((ASN1Sequence)new DERInputStream(new ByteArrayInputStream(key.getEncoded())).readObject()));
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 128 */       throw new IllegalArgumentException("unable to process key - " + e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSignatureAlgorithm(String signatureAlgorithm)
/*     */   {
/* 135 */     this.signatureAlgorithm = signatureAlgorithm;
/*     */     
/* 137 */     this.sigOID = ((DERObjectIdentifier)algorithms.get(signatureAlgorithm.toUpperCase()));
/*     */     
/* 139 */     if (this.sigOID == null)
/*     */     {
/* 141 */       throw new IllegalArgumentException("Unknown signature type requested");
/*     */     }
/*     */     
/* 144 */     this.sigAlgId = new AlgorithmIdentifier(this.sigOID, new DERNull());
/*     */     
/* 146 */     this.tbsGen.setSignature(this.sigAlgId);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Certificate generateX509Certificate(PrivateKey key)
/*     */     throws SecurityException, SignatureException, InvalidKeyException
/*     */   {
/*     */     try
/*     */     {
/* 159 */       return generateX509Certificate(key, BouncyCastleProvider.PROVIDER_NAME, null);
/*     */     }
/*     */     catch (NoSuchProviderException e)
/*     */     {
/* 163 */       throw new SecurityException("BC provider not installed!");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Certificate generateX509Certificate(PrivateKey key, SecureRandom random)
/*     */     throws SecurityException, SignatureException, InvalidKeyException
/*     */   {
/*     */     try
/*     */     {
/* 178 */       return generateX509Certificate(key, BouncyCastleProvider.PROVIDER_NAME, random);
/*     */     }
/*     */     catch (NoSuchProviderException e)
/*     */     {
/* 182 */       throw new SecurityException("BC provider not installed!");
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
/*     */   public X509Certificate generateX509Certificate(PrivateKey key, String provider)
/*     */     throws NoSuchProviderException, SecurityException, SignatureException, InvalidKeyException
/*     */   {
/* 196 */     return generateX509Certificate(key, provider, null);
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
/*     */   public X509Certificate generateX509Certificate(PrivateKey key, String provider, SecureRandom random)
/*     */     throws NoSuchProviderException, SecurityException, SignatureException, InvalidKeyException
/*     */   {
/* 210 */     Signature sig = null;
/*     */     
/*     */     try
/*     */     {
/* 214 */       sig = Signature.getInstance(this.sigOID.getId(), provider);
/*     */     }
/*     */     catch (NoSuchAlgorithmException ex)
/*     */     {
/*     */       try
/*     */       {
/* 220 */         sig = Signature.getInstance(this.signatureAlgorithm, provider);
/*     */       }
/*     */       catch (NoSuchAlgorithmException e)
/*     */       {
/* 224 */         throw new SecurityException("exception creating signature: " + e.toString());
/*     */       }
/*     */     }
/*     */     
/* 228 */     if (random != null)
/*     */     {
/* 230 */       sig.initSign(key, random);
/*     */     }
/*     */     else
/*     */     {
/* 234 */       sig.initSign(key);
/*     */     }
/*     */     
/* 237 */     TBSCertificateStructure tbsCert = this.tbsGen.generateTBSCertificate();
/*     */     
/*     */     try
/*     */     {
/* 241 */       ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 242 */       DEROutputStream dOut = new DEROutputStream(bOut);
/*     */       
/* 244 */       dOut.writeObject(tbsCert);
/*     */       
/* 246 */       sig.update(bOut.toByteArray());
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 250 */       throw new SecurityException("exception encoding TBS cert - " + e);
/*     */     }
/*     */     
/* 253 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 255 */     v.add(tbsCert);
/* 256 */     v.add(this.sigAlgId);
/* 257 */     v.add(new DERBitString(sig.sign()));
/*     */     
/* 259 */     return new X509CertificateObject(new X509CertificateStructure(new DERSequence(v)));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/X509V1CertificateGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */