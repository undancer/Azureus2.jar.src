/*     */ package org.gudy.bouncycastle.jce;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.Signature;
/*     */ import java.security.SignatureException;
/*     */ import java.security.cert.X509CRL;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Hashtable;
/*     */ import java.util.SimpleTimeZone;
/*     */ import java.util.Vector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DEROctetString;
/*     */ import org.gudy.bouncycastle.asn1.DEROutputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.DERUTCTime;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.x509.CertificateList;
/*     */ import org.gudy.bouncycastle.asn1.x509.TBSCertList;
/*     */ import org.gudy.bouncycastle.asn1.x509.V2TBSCertListGenerator;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Extension;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Extensions;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Name;
/*     */ import org.gudy.bouncycastle.jce.provider.BouncyCastleProvider;
/*     */ import org.gudy.bouncycastle.jce.provider.X509CRLObject;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class X509V2CRLGenerator
/*     */ {
/*  46 */   private SimpleDateFormat dateF = new SimpleDateFormat("yyMMddHHmmss");
/*  47 */   private SimpleTimeZone tz = new SimpleTimeZone(0, "Z");
/*     */   private V2TBSCertListGenerator tbsGen;
/*     */   private DERObjectIdentifier sigOID;
/*     */   private AlgorithmIdentifier sigAlgId;
/*     */   private String signatureAlgorithm;
/*  52 */   private Hashtable extensions = null;
/*  53 */   private Vector extOrdering = null;
/*     */   
/*  55 */   private static Hashtable algorithms = new Hashtable();
/*     */   
/*     */   static
/*     */   {
/*  59 */     algorithms.put("MD2WITHRSAENCRYPTION", new DERObjectIdentifier("1.2.840.113549.1.1.2"));
/*  60 */     algorithms.put("MD2WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.2"));
/*  61 */     algorithms.put("MD5WITHRSAENCRYPTION", new DERObjectIdentifier("1.2.840.113549.1.1.4"));
/*  62 */     algorithms.put("MD5WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.4"));
/*  63 */     algorithms.put("SHA1WITHRSAENCRYPTION", new DERObjectIdentifier("1.2.840.113549.1.1.5"));
/*  64 */     algorithms.put("SHA1WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.5"));
/*  65 */     algorithms.put("RIPEMD160WITHRSAENCRYPTION", new DERObjectIdentifier("1.3.36.3.3.1.2"));
/*  66 */     algorithms.put("RIPEMD160WITHRSA", new DERObjectIdentifier("1.3.36.3.3.1.2"));
/*  67 */     algorithms.put("SHA1WITHDSA", new DERObjectIdentifier("1.2.840.10040.4.3"));
/*  68 */     algorithms.put("DSAWITHSHA1", new DERObjectIdentifier("1.2.840.10040.4.3"));
/*  69 */     algorithms.put("SHA1WITHECDSA", new DERObjectIdentifier("1.2.840.10045.4.1"));
/*  70 */     algorithms.put("ECDSAWITHSHA1", new DERObjectIdentifier("1.2.840.10045.4.1"));
/*     */   }
/*     */   
/*     */   public X509V2CRLGenerator()
/*     */   {
/*  75 */     this.dateF.setTimeZone(this.tz);
/*     */     
/*  77 */     this.tbsGen = new V2TBSCertListGenerator();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/*  85 */     this.tbsGen = new V2TBSCertListGenerator();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setIssuerDN(X509Name issuer)
/*     */   {
/*  96 */     this.tbsGen.setIssuer(issuer);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setThisUpdate(Date date)
/*     */   {
/* 102 */     this.tbsGen.setThisUpdate(new DERUTCTime(this.dateF.format(date) + "Z"));
/*     */   }
/*     */   
/*     */ 
/*     */   public void setNextUpdate(Date date)
/*     */   {
/* 108 */     this.tbsGen.setNextUpdate(new DERUTCTime(this.dateF.format(date) + "Z"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addCRLEntry(BigInteger userCertificate, Date revocationDate, int reason)
/*     */   {
/* 117 */     this.tbsGen.addCRLEntry(new DERInteger(userCertificate), new DERUTCTime(this.dateF.format(revocationDate) + "Z"), reason);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSignatureAlgorithm(String signatureAlgorithm)
/*     */   {
/* 123 */     this.signatureAlgorithm = signatureAlgorithm;
/*     */     
/* 125 */     this.sigOID = ((DERObjectIdentifier)algorithms.get(signatureAlgorithm.toUpperCase()));
/*     */     
/* 127 */     if (this.sigOID == null)
/*     */     {
/* 129 */       throw new IllegalArgumentException("Unknown signature type requested");
/*     */     }
/*     */     
/* 132 */     this.sigAlgId = new AlgorithmIdentifier(this.sigOID, null);
/*     */     
/* 134 */     this.tbsGen.setSignature(this.sigAlgId);
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
/* 145 */     addExtension(new DERObjectIdentifier(OID), critical, value);
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
/* 156 */     if (this.extensions == null)
/*     */     {
/* 158 */       this.extensions = new Hashtable();
/* 159 */       this.extOrdering = new Vector();
/*     */     }
/*     */     
/* 162 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 163 */     DEROutputStream dOut = new DEROutputStream(bOut);
/*     */     
/*     */     try
/*     */     {
/* 167 */       dOut.writeObject(value);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 171 */       throw new IllegalArgumentException("error encoding value: " + e);
/*     */     }
/*     */     
/* 174 */     addExtension(OID, critical, bOut.toByteArray());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addExtension(String OID, boolean critical, byte[] value)
/*     */   {
/* 185 */     addExtension(new DERObjectIdentifier(OID), critical, value);
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
/* 196 */     if (this.extensions == null)
/*     */     {
/* 198 */       this.extensions = new Hashtable();
/* 199 */       this.extOrdering = new Vector();
/*     */     }
/*     */     
/* 202 */     this.extensions.put(OID, new X509Extension(critical, new DEROctetString(value)));
/* 203 */     this.extOrdering.addElement(OID);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509CRL generateX509CRL(PrivateKey key)
/*     */     throws SecurityException, SignatureException, InvalidKeyException
/*     */   {
/*     */     try
/*     */     {
/* 216 */       return generateX509CRL(key, BouncyCastleProvider.PROVIDER_NAME, null);
/*     */     }
/*     */     catch (NoSuchProviderException e)
/*     */     {
/* 220 */       throw new SecurityException("BC provider not installed!");
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
/*     */   public X509CRL generateX509CRL(PrivateKey key, SecureRandom random)
/*     */     throws SecurityException, SignatureException, InvalidKeyException
/*     */   {
/*     */     try
/*     */     {
/* 236 */       return generateX509CRL(key, BouncyCastleProvider.PROVIDER_NAME, random);
/*     */     }
/*     */     catch (NoSuchProviderException e)
/*     */     {
/* 240 */       throw new SecurityException("BC provider not installed!");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509CRL generateX509CRL(PrivateKey key, String provider)
/*     */     throws NoSuchProviderException, SecurityException, SignatureException, InvalidKeyException
/*     */   {
/* 253 */     return generateX509CRL(key, provider, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509CRL generateX509CRL(PrivateKey key, String provider, SecureRandom random)
/*     */     throws NoSuchProviderException, SecurityException, SignatureException, InvalidKeyException
/*     */   {
/* 266 */     Signature sig = null;
/*     */     
/*     */     try
/*     */     {
/* 270 */       sig = Signature.getInstance(this.sigOID.getId(), provider);
/*     */     }
/*     */     catch (NoSuchAlgorithmException ex)
/*     */     {
/*     */       try
/*     */       {
/* 276 */         sig = Signature.getInstance(this.signatureAlgorithm, provider);
/*     */       }
/*     */       catch (NoSuchAlgorithmException e)
/*     */       {
/* 280 */         throw new SecurityException("exception creating signature: " + e.toString());
/*     */       }
/*     */     }
/*     */     
/* 284 */     if (random != null)
/*     */     {
/* 286 */       sig.initSign(key, random);
/*     */     }
/*     */     else
/*     */     {
/* 290 */       sig.initSign(key);
/*     */     }
/*     */     
/* 293 */     if (this.extensions != null)
/*     */     {
/* 295 */       this.tbsGen.setExtensions(new X509Extensions(this.extOrdering, this.extensions));
/*     */     }
/*     */     
/* 298 */     TBSCertList tbsCrl = this.tbsGen.generateTBSCertList();
/*     */     
/*     */     try
/*     */     {
/* 302 */       ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 303 */       DEROutputStream dOut = new DEROutputStream(bOut);
/*     */       
/* 305 */       dOut.writeObject(tbsCrl);
/*     */       
/* 307 */       sig.update(bOut.toByteArray());
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 311 */       throw new SecurityException("exception encoding TBS cert - " + e);
/*     */     }
/*     */     
/*     */ 
/* 315 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 317 */     v.add(tbsCrl);
/* 318 */     v.add(this.sigAlgId);
/* 319 */     v.add(new DERBitString(sig.sign()));
/*     */     
/* 321 */     return new X509CRLObject(new CertificateList(new DERSequence(v)));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/X509V2CRLGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */