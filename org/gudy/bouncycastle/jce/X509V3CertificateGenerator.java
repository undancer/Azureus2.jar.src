/*     */ package org.gudy.bouncycastle.jce;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
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
/*     */ import java.util.Vector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*     */ import org.gudy.bouncycastle.asn1.DERInputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERNull;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DEROutputStream;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
/*     */ import org.gudy.bouncycastle.asn1.x509.Time;
/*     */ import org.gudy.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509CertificateStructure;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Extension;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Extensions;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Name;
/*     */ import org.gudy.bouncycastle.jce.provider.BouncyCastleProvider;
/*     */ import org.gudy.bouncycastle.jce.provider.X509CertificateObject;
/*     */ 
/*     */ public class X509V3CertificateGenerator
/*     */ {
/*     */   private V3TBSCertificateGenerator tbsGen;
/*     */   private DERObjectIdentifier sigOID;
/*     */   private AlgorithmIdentifier sigAlgId;
/*     */   private String signatureAlgorithm;
/*  42 */   private Hashtable extensions = null;
/*  43 */   private Vector extOrdering = null;
/*     */   
/*  45 */   private static Hashtable algorithms = new Hashtable();
/*     */   
/*     */   static
/*     */   {
/*  49 */     algorithms.put("MD2WITHRSAENCRYPTION", new DERObjectIdentifier("1.2.840.113549.1.1.2"));
/*  50 */     algorithms.put("MD2WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.2"));
/*  51 */     algorithms.put("MD5WITHRSAENCRYPTION", new DERObjectIdentifier("1.2.840.113549.1.1.4"));
/*  52 */     algorithms.put("MD5WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.4"));
/*  53 */     algorithms.put("SHA1WITHRSAENCRYPTION", new DERObjectIdentifier("1.2.840.113549.1.1.5"));
/*  54 */     algorithms.put("SHA1WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.5"));
/*  55 */     algorithms.put("RIPEMD160WITHRSAENCRYPTION", new DERObjectIdentifier("1.3.36.3.3.1.2"));
/*  56 */     algorithms.put("RIPEMD160WITHRSA", new DERObjectIdentifier("1.3.36.3.3.1.2"));
/*  57 */     algorithms.put("SHA1WITHDSA", new DERObjectIdentifier("1.2.840.10040.4.3"));
/*  58 */     algorithms.put("DSAWITHSHA1", new DERObjectIdentifier("1.2.840.10040.4.3"));
/*  59 */     algorithms.put("SHA1WITHECDSA", new DERObjectIdentifier("1.2.840.10045.4.1"));
/*  60 */     algorithms.put("ECDSAWITHSHA1", new DERObjectIdentifier("1.2.840.10045.4.1"));
/*     */   }
/*     */   
/*     */   public X509V3CertificateGenerator()
/*     */   {
/*  65 */     this.tbsGen = new V3TBSCertificateGenerator();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/*  73 */     this.tbsGen = new V3TBSCertificateGenerator();
/*  74 */     this.extensions = null;
/*  75 */     this.extOrdering = null;
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
/* 123 */       this.tbsGen.setSubjectPublicKeyInfo(new SubjectPublicKeyInfo((org.gudy.bouncycastle.asn1.ASN1Sequence)new DERInputStream(new java.io.ByteArrayInputStream(key.getEncoded())).readObject()));
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
/*     */ 
/*     */   public void addExtension(String OID, boolean critical, DEREncodable value)
/*     */   {
/* 157 */     addExtension(new DERObjectIdentifier(OID), critical, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addExtension(DERObjectIdentifier OID, boolean critical, DEREncodable value)
/*     */   {
/* 168 */     if (this.extensions == null)
/*     */     {
/* 170 */       this.extensions = new Hashtable();
/* 171 */       this.extOrdering = new Vector();
/*     */     }
/*     */     
/* 174 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 175 */     DEROutputStream dOut = new DEROutputStream(bOut);
/*     */     
/*     */     try
/*     */     {
/* 179 */       dOut.writeObject(value);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 183 */       throw new IllegalArgumentException("error encoding value: " + e);
/*     */     }
/*     */     
/* 186 */     addExtension(OID, critical, bOut.toByteArray());
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
/*     */   public void addExtension(String OID, boolean critical, byte[] value)
/*     */   {
/* 199 */     addExtension(new DERObjectIdentifier(OID), critical, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addExtension(DERObjectIdentifier OID, boolean critical, byte[] value)
/*     */   {
/* 210 */     if (this.extensions == null)
/*     */     {
/* 212 */       this.extensions = new Hashtable();
/* 213 */       this.extOrdering = new Vector();
/*     */     }
/*     */     
/* 216 */     this.extensions.put(OID, new X509Extension(critical, new org.gudy.bouncycastle.asn1.DEROctetString(value)));
/* 217 */     this.extOrdering.addElement(OID);
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
/* 230 */       return generateX509Certificate(key, BouncyCastleProvider.PROVIDER_NAME, null);
/*     */     }
/*     */     catch (NoSuchProviderException e)
/*     */     {
/* 234 */       throw new SecurityException("BC provider not installed!");
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
/*     */   public X509Certificate generateX509Certificate(PrivateKey key, SecureRandom random)
/*     */     throws SecurityException, SignatureException, InvalidKeyException
/*     */   {
/*     */     try
/*     */     {
/* 250 */       return generateX509Certificate(key, BouncyCastleProvider.PROVIDER_NAME, random);
/*     */     }
/*     */     catch (NoSuchProviderException e)
/*     */     {
/* 254 */       throw new SecurityException("BC provider not installed!");
/*     */     }
/*     */   }
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
/* 267 */     return generateX509Certificate(key, provider, null);
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
/* 281 */     Signature sig = null;
/*     */     
/* 283 */     if (this.sigOID == null)
/*     */     {
/* 285 */       throw new IllegalStateException("no signature algorithm specified");
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 290 */       sig = Signature.getInstance(this.sigOID.getId(), provider);
/*     */     }
/*     */     catch (NoSuchAlgorithmException ex)
/*     */     {
/*     */       try
/*     */       {
/* 296 */         sig = Signature.getInstance(this.signatureAlgorithm, provider);
/*     */       }
/*     */       catch (NoSuchAlgorithmException e)
/*     */       {
/* 300 */         throw new SecurityException("exception creating signature: " + e.toString());
/*     */       }
/*     */     }
/*     */     
/* 304 */     if (random != null)
/*     */     {
/* 306 */       sig.initSign(key, random);
/*     */     }
/*     */     else
/*     */     {
/* 310 */       sig.initSign(key);
/*     */     }
/*     */     
/* 313 */     if (this.extensions != null)
/*     */     {
/* 315 */       this.tbsGen.setExtensions(new X509Extensions(this.extOrdering, this.extensions));
/*     */     }
/*     */     
/* 318 */     org.gudy.bouncycastle.asn1.x509.TBSCertificateStructure tbsCert = this.tbsGen.generateTBSCertificate();
/*     */     
/*     */     try
/*     */     {
/* 322 */       ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 323 */       DEROutputStream dOut = new DEROutputStream(bOut);
/*     */       
/* 325 */       dOut.writeObject(tbsCert);
/*     */       
/* 327 */       sig.update(bOut.toByteArray());
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 331 */       throw new SecurityException("exception encoding TBS cert - " + e);
/*     */     }
/*     */     
/* 334 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 336 */     v.add(tbsCert);
/* 337 */     v.add(this.sigAlgId);
/* 338 */     v.add(new org.gudy.bouncycastle.asn1.DERBitString(sig.sign()));
/*     */     
/* 340 */     return new X509CertificateObject(new X509CertificateStructure(new org.gudy.bouncycastle.asn1.DERSequence(v)));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/X509V3CertificateGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */