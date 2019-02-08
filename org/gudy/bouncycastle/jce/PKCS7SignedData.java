/*     */ package org.gudy.bouncycastle.jce;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.Signature;
/*     */ import java.security.SignatureException;
/*     */ import java.security.cert.CRL;
/*     */ import java.security.cert.CRLException;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509CRL;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Set;
/*     */ import org.gudy.bouncycastle.asn1.DERInputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERNull;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DEROctetString;
/*     */ import org.gudy.bouncycastle.asn1.DEROutputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.DERSet;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.ContentInfo;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.IssuerAndSerialNumber;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.SignedData;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.SignerInfo;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.x509.CertificateList;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509CertificateStructure;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Name;
/*     */ import org.gudy.bouncycastle.jce.provider.BouncyCastleProvider;
/*     */ import org.gudy.bouncycastle.jce.provider.X509CRLObject;
/*     */ import org.gudy.bouncycastle.jce.provider.X509CertificateObject;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PKCS7SignedData
/*     */   implements PKCSObjectIdentifiers
/*     */ {
/*     */   private int version;
/*     */   private int signerversion;
/*     */   private Set digestalgos;
/*     */   private Collection certs;
/*     */   private Collection crls;
/*     */   private X509Certificate signCert;
/*     */   private byte[] digest;
/*     */   private String digestAlgorithm;
/*     */   private String digestEncryptionAlgorithm;
/*     */   private Signature sig;
/*     */   private transient PrivateKey privKey;
/*  69 */   private final String ID_PKCS7_DATA = "1.2.840.113549.1.7.1";
/*  70 */   private final String ID_PKCS7_SIGNED_DATA = "1.2.840.113549.1.7.2";
/*  71 */   private final String ID_MD5 = "1.2.840.113549.2.5";
/*  72 */   private final String ID_MD2 = "1.2.840.113549.2.2";
/*  73 */   private final String ID_SHA1 = "1.3.14.3.2.26";
/*  74 */   private final String ID_RSA = "1.2.840.113549.1.1.1";
/*  75 */   private final String ID_DSA = "1.2.840.10040.4.1";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PKCS7SignedData(byte[] in)
/*     */     throws SecurityException, CRLException, InvalidKeyException, CertificateException, NoSuchProviderException, NoSuchAlgorithmException
/*     */   {
/*  86 */     this(in, BouncyCastleProvider.PROVIDER_NAME);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PKCS7SignedData(byte[] in, String provider)
/*     */     throws SecurityException, CRLException, InvalidKeyException, CertificateException, NoSuchProviderException, NoSuchAlgorithmException
/*     */   {
/*  98 */     DERInputStream din = new DERInputStream(new ByteArrayInputStream(in));
/*     */     
/*     */ 
/*     */ 
/*     */     DERObject pkcs;
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 107 */       pkcs = din.readObject();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 111 */       throw new SecurityException("can't decode PKCS7SignedData object");
/*     */     }
/*     */     
/* 114 */     if (!(pkcs instanceof ASN1Sequence))
/*     */     {
/* 116 */       throw new SecurityException("Not a valid PKCS#7 object - not a sequence");
/*     */     }
/*     */     
/* 119 */     ContentInfo content = ContentInfo.getInstance(pkcs);
/*     */     
/* 121 */     if (!content.getContentType().equals(signedData))
/*     */     {
/* 123 */       throw new SecurityException("Not a valid PKCS#7 signed-data object - wrong header " + content.getContentType().getId());
/*     */     }
/*     */     
/*     */ 
/* 127 */     SignedData data = SignedData.getInstance(content.getContent());
/*     */     
/* 129 */     this.certs = new ArrayList();
/*     */     
/* 131 */     if (data.getCertificates() != null)
/*     */     {
/* 133 */       Enumeration ec = ASN1Set.getInstance(data.getCertificates()).getObjects();
/*     */       
/* 135 */       while (ec.hasMoreElements())
/*     */       {
/* 137 */         this.certs.add(new X509CertificateObject(X509CertificateStructure.getInstance(ec.nextElement())));
/*     */       }
/*     */     }
/*     */     
/* 141 */     this.crls = new ArrayList();
/*     */     
/* 143 */     if (data.getCRLs() != null)
/*     */     {
/* 145 */       Enumeration ec = ASN1Set.getInstance(data.getCRLs()).getObjects();
/* 146 */       while (ec.hasMoreElements())
/*     */       {
/* 148 */         this.crls.add(new X509CRLObject(CertificateList.getInstance(ec.nextElement())));
/*     */       }
/*     */     }
/*     */     
/* 152 */     this.version = data.getVersion().getValue().intValue();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 157 */     this.digestalgos = new HashSet();
/* 158 */     Enumeration e = data.getDigestAlgorithms().getObjects();
/*     */     
/* 160 */     while (e.hasMoreElements())
/*     */     {
/* 162 */       ASN1Sequence s = (ASN1Sequence)e.nextElement();
/* 163 */       DERObjectIdentifier o = (DERObjectIdentifier)s.getObjectAt(0);
/* 164 */       this.digestalgos.add(o.getId());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 170 */     ASN1Set signerinfos = data.getSignerInfos();
/* 171 */     if (signerinfos.size() != 1)
/*     */     {
/* 173 */       throw new SecurityException("This PKCS#7 object has multiple SignerInfos - only one is supported at this time");
/*     */     }
/*     */     
/* 176 */     SignerInfo signerInfo = SignerInfo.getInstance(signerinfos.getObjectAt(0));
/*     */     
/* 178 */     this.signerversion = signerInfo.getVersion().getValue().intValue();
/*     */     
/* 180 */     IssuerAndSerialNumber isAnds = signerInfo.getIssuerAndSerialNumber();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 185 */     BigInteger serialNumber = isAnds.getCertificateSerialNumber().getValue();
/* 186 */     X509Principal issuer = new X509Principal(isAnds.getName());
/*     */     
/* 188 */     for (Iterator i = this.certs.iterator(); i.hasNext();)
/*     */     {
/* 190 */       X509Certificate cert = (X509Certificate)i.next();
/* 191 */       if ((serialNumber.equals(cert.getSerialNumber())) && (issuer.equals(cert.getIssuerDN())))
/*     */       {
/*     */ 
/* 194 */         this.signCert = cert;
/* 195 */         break;
/*     */       }
/*     */     }
/*     */     
/* 199 */     if (this.signCert == null)
/*     */     {
/* 201 */       throw new SecurityException("Can't find signing certificate with serial " + serialNumber.toString(16));
/*     */     }
/*     */     
/* 204 */     this.digestAlgorithm = signerInfo.getDigestAlgorithm().getObjectId().getId();
/*     */     
/* 206 */     this.digest = signerInfo.getEncryptedDigest().getOctets();
/* 207 */     this.digestEncryptionAlgorithm = signerInfo.getDigestEncryptionAlgorithm().getObjectId().getId();
/*     */     
/* 209 */     this.sig = Signature.getInstance(getDigestAlgorithm(), provider);
/*     */     
/* 211 */     this.sig.initVerify(this.signCert.getPublicKey());
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
/*     */ 
/*     */   public PKCS7SignedData(PrivateKey privKey, Certificate[] certChain, String hashAlgorithm)
/*     */     throws SecurityException, InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException
/*     */   {
/* 228 */     this(privKey, certChain, hashAlgorithm, BouncyCastleProvider.PROVIDER_NAME);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public PKCS7SignedData(PrivateKey privKey, Certificate[] certChain, String hashAlgorithm, String provider)
/*     */     throws SecurityException, InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException
/*     */   {
/* 247 */     this(privKey, certChain, null, hashAlgorithm, provider);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PKCS7SignedData(PrivateKey privKey, Certificate[] certChain, CRL[] crlList, String hashAlgorithm, String provider)
/*     */     throws SecurityException, InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException
/*     */   {
/* 268 */     this.privKey = privKey;
/*     */     
/* 270 */     if (hashAlgorithm.equals("MD5"))
/*     */     {
/* 272 */       this.digestAlgorithm = "1.2.840.113549.2.5";
/*     */     }
/* 274 */     else if (hashAlgorithm.equals("MD2"))
/*     */     {
/* 276 */       this.digestAlgorithm = "1.2.840.113549.2.2";
/*     */     }
/* 278 */     else if (hashAlgorithm.equals("SHA"))
/*     */     {
/* 280 */       this.digestAlgorithm = "1.3.14.3.2.26";
/*     */     }
/* 282 */     else if (hashAlgorithm.equals("SHA1"))
/*     */     {
/* 284 */       this.digestAlgorithm = "1.3.14.3.2.26";
/*     */     }
/*     */     else
/*     */     {
/* 288 */       throw new NoSuchAlgorithmException("Unknown Hash Algorithm " + hashAlgorithm);
/*     */     }
/*     */     
/* 291 */     this.version = (this.signerversion = 1);
/* 292 */     this.certs = new ArrayList();
/* 293 */     this.crls = new ArrayList();
/* 294 */     this.digestalgos = new HashSet();
/* 295 */     this.digestalgos.add(this.digestAlgorithm);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 300 */     this.signCert = ((X509Certificate)certChain[0]);
/* 301 */     Collections.addAll(this.certs, certChain);
/*     */     
/* 303 */     if (crlList != null)
/*     */     {
/* 305 */       Collections.addAll(this.crls, crlList);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 311 */     this.digestEncryptionAlgorithm = privKey.getAlgorithm();
/* 312 */     if (this.digestEncryptionAlgorithm.equals("RSA"))
/*     */     {
/* 314 */       this.digestEncryptionAlgorithm = "1.2.840.113549.1.1.1";
/*     */     }
/* 316 */     else if (this.digestEncryptionAlgorithm.equals("DSA"))
/*     */     {
/* 318 */       this.digestEncryptionAlgorithm = "1.2.840.10040.4.1";
/*     */     }
/*     */     else
/*     */     {
/* 322 */       throw new NoSuchAlgorithmException("Unknown Key Algorithm " + this.digestEncryptionAlgorithm);
/*     */     }
/*     */     
/* 325 */     this.sig = Signature.getInstance(getDigestAlgorithm(), provider);
/*     */     
/* 327 */     this.sig.initSign(privKey);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getDigestAlgorithm()
/*     */   {
/* 335 */     String da = this.digestAlgorithm;
/* 336 */     String dea = this.digestEncryptionAlgorithm;
/*     */     
/* 338 */     if (this.digestAlgorithm.equals("1.2.840.113549.2.5"))
/*     */     {
/* 340 */       da = "MD5";
/*     */     }
/* 342 */     else if (this.digestAlgorithm.equals("1.2.840.113549.2.2"))
/*     */     {
/* 344 */       da = "MD2";
/*     */     }
/* 346 */     else if (this.digestAlgorithm.equals("1.3.14.3.2.26"))
/*     */     {
/* 348 */       da = "SHA1";
/*     */     }
/*     */     
/* 351 */     if (this.digestEncryptionAlgorithm.equals("1.2.840.113549.1.1.1"))
/*     */     {
/* 353 */       dea = "RSA";
/*     */     }
/* 355 */     else if (this.digestEncryptionAlgorithm.equals("1.2.840.10040.4.1"))
/*     */     {
/* 357 */       dea = "DSA";
/*     */     }
/*     */     
/* 360 */     return da + "with" + dea;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/*     */     try
/*     */     {
/* 371 */       if (this.privKey == null)
/*     */       {
/* 373 */         this.sig.initVerify(this.signCert.getPublicKey());
/*     */       }
/*     */       else
/*     */       {
/* 377 */         this.sig.initSign(this.privKey);
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 382 */       throw new RuntimeException(e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Certificate[] getCertificates()
/*     */   {
/* 391 */     return (X509Certificate[])this.certs.toArray(new X509Certificate[this.certs.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Collection getCRLs()
/*     */   {
/* 399 */     return this.crls;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Certificate getSigningCertificate()
/*     */   {
/* 407 */     return this.signCert;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getVersion()
/*     */   {
/* 415 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getSigningInfoVersion()
/*     */   {
/* 423 */     return this.signerversion;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(byte buf)
/*     */     throws SignatureException
/*     */   {
/* 432 */     this.sig.update(buf);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(byte[] buf, int off, int len)
/*     */     throws SignatureException
/*     */   {
/* 441 */     this.sig.update(buf, off, len);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean verify()
/*     */     throws SignatureException
/*     */   {
/* 450 */     return this.sig.verify(this.digest);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private DERObject getIssuer(byte[] enc)
/*     */   {
/*     */     try
/*     */     {
/* 460 */       DERInputStream in = new DERInputStream(new ByteArrayInputStream(enc));
/* 461 */       ASN1Sequence seq = (ASN1Sequence)in.readObject();
/* 462 */       return (DERObject)seq.getObjectAt((seq.getObjectAt(0) instanceof DERTaggedObject) ? 3 : 2);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 466 */       throw new Error("IOException reading from ByteArray: " + e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getEncoded()
/*     */   {
/*     */     try
/*     */     {
/* 478 */       this.digest = this.sig.sign();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 484 */       ASN1EncodableVector v = new ASN1EncodableVector();
/* 485 */       for (Iterator i = this.digestalgos.iterator(); i.hasNext();)
/*     */       {
/* 487 */         AlgorithmIdentifier a = new AlgorithmIdentifier(new DERObjectIdentifier((String)i.next()), null);
/*     */         
/*     */ 
/*     */ 
/* 491 */         v.add(a);
/*     */       }
/*     */       
/* 494 */       DERSet algos = new DERSet(v);
/*     */       
/*     */ 
/*     */ 
/* 498 */       DERSequence contentinfo = new DERSequence(new DERObjectIdentifier("1.2.840.113549.1.7.1"));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 503 */       v = new ASN1EncodableVector();
/* 504 */       for (Iterator i = this.certs.iterator(); i.hasNext();)
/*     */       {
/* 506 */         DERInputStream tempstream = new DERInputStream(new ByteArrayInputStream(((X509Certificate)i.next()).getEncoded()));
/* 507 */         v.add(tempstream.readObject());
/*     */       }
/*     */       
/* 510 */       DERSet dercertificates = new DERSet(v);
/*     */       
/*     */ 
/*     */ 
/* 514 */       ASN1EncodableVector signerinfo = new ASN1EncodableVector();
/*     */       
/*     */ 
/*     */ 
/* 518 */       signerinfo.add(new DERInteger(this.signerversion));
/*     */       
/* 520 */       IssuerAndSerialNumber isAnds = new IssuerAndSerialNumber(new X509Name((ASN1Sequence)getIssuer(this.signCert.getTBSCertificate())), new DERInteger(this.signCert.getSerialNumber()));
/*     */       
/*     */ 
/* 523 */       signerinfo.add(isAnds);
/*     */       
/*     */ 
/*     */ 
/* 527 */       signerinfo.add(new AlgorithmIdentifier(new DERObjectIdentifier(this.digestAlgorithm), new DERNull()));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 534 */       signerinfo.add(new AlgorithmIdentifier(new DERObjectIdentifier(this.digestEncryptionAlgorithm), new DERNull()));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 541 */       signerinfo.add(new DEROctetString(this.digest));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 547 */       ASN1EncodableVector body = new ASN1EncodableVector();
/* 548 */       body.add(new DERInteger(this.version));
/* 549 */       body.add(algos);
/* 550 */       body.add(contentinfo);
/* 551 */       body.add(new DERTaggedObject(false, 0, dercertificates));
/*     */       
/* 553 */       if (this.crls.size() > 0) {
/* 554 */         v = new ASN1EncodableVector();
/* 555 */         for (Iterator i = this.crls.iterator(); i.hasNext();) {
/* 556 */           DERInputStream t = new DERInputStream(new ByteArrayInputStream(((X509CRL)i.next()).getEncoded()));
/* 557 */           v.add(t.readObject());
/*     */         }
/* 559 */         DERSet dercrls = new DERSet(v);
/* 560 */         body.add(new DERTaggedObject(false, 1, dercrls));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 565 */       body.add(new DERSet(new DERSequence(signerinfo)));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 570 */       ASN1EncodableVector whole = new ASN1EncodableVector();
/* 571 */       whole.add(new DERObjectIdentifier("1.2.840.113549.1.7.2"));
/* 572 */       whole.add(new DERTaggedObject(0, new DERSequence(body)));
/*     */       
/* 574 */       ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/*     */       
/* 576 */       DEROutputStream dout = new DEROutputStream(bOut);
/* 577 */       dout.writeObject(new DERSequence(whole));
/* 578 */       dout.close();
/*     */       
/* 580 */       return bOut.toByteArray();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 584 */       throw new RuntimeException(e.toString());
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/PKCS7SignedData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */