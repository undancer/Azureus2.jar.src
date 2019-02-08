/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.security.cert.CRL;
/*     */ import java.security.cert.CRLException;
/*     */ import java.security.cert.CertPath;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactorySpi;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Set;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.BERInputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERInputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.SignedData;
/*     */ import org.gudy.bouncycastle.asn1.x509.CertificateList;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509CertificateStructure;
/*     */ import org.gudy.bouncycastle.util.encoders.Base64;
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
/*     */ public class JDKX509CertificateFactory
/*     */   extends CertificateFactorySpi
/*     */ {
/*  43 */   private SignedData sData = null;
/*  44 */   private int sDataObjectCount = 0;
/*     */   
/*     */ 
/*     */ 
/*     */   private String readLine(InputStream in)
/*     */     throws IOException
/*     */   {
/*  51 */     StringBuilder l = new StringBuilder();
/*     */     int c;
/*  53 */     while (((c = in.read()) != 10) && (c >= 0))
/*     */     {
/*  55 */       if (c != 13)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*  60 */         l.append((char)c);
/*     */       }
/*     */     }
/*  63 */     if (c < 0)
/*     */     {
/*  65 */       return null;
/*     */     }
/*     */     
/*  68 */     return l.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   private Certificate readDERCertificate(InputStream in)
/*     */     throws IOException
/*     */   {
/*  75 */     DERInputStream dIn = new DERInputStream(in);
/*  76 */     ASN1Sequence seq = (ASN1Sequence)dIn.readObject();
/*     */     
/*  78 */     if ((seq.size() > 1) && ((seq.getObjectAt(0) instanceof DERObjectIdentifier)))
/*     */     {
/*     */ 
/*  81 */       if (seq.getObjectAt(0).equals(PKCSObjectIdentifiers.signedData))
/*     */       {
/*  83 */         this.sData = new SignedData(ASN1Sequence.getInstance((ASN1TaggedObject)seq.getObjectAt(1), true));
/*     */         
/*     */ 
/*  86 */         return new X509CertificateObject(X509CertificateStructure.getInstance(this.sData.getCertificates().getObjectAt(this.sDataObjectCount++)));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  92 */     return new X509CertificateObject(X509CertificateStructure.getInstance(seq));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Certificate readPKCS7Certificate(InputStream in)
/*     */     throws IOException
/*     */   {
/* 103 */     BERInputStream dIn = new BERInputStream(in);
/* 104 */     ASN1Sequence seq = (ASN1Sequence)dIn.readObject();
/*     */     
/* 106 */     if ((seq.size() > 1) && ((seq.getObjectAt(0) instanceof DERObjectIdentifier)))
/*     */     {
/*     */ 
/* 109 */       if (seq.getObjectAt(0).equals(PKCSObjectIdentifiers.signedData))
/*     */       {
/* 111 */         this.sData = new SignedData(ASN1Sequence.getInstance((ASN1TaggedObject)seq.getObjectAt(1), true));
/*     */         
/*     */ 
/* 114 */         return new X509CertificateObject(X509CertificateStructure.getInstance(this.sData.getCertificates().getObjectAt(this.sDataObjectCount++)));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 120 */     return new X509CertificateObject(X509CertificateStructure.getInstance(seq));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private Certificate readPEMCertificate(InputStream in)
/*     */     throws IOException
/*     */   {
/* 129 */     StringBuilder pemBuf = new StringBuilder();
/*     */     String line;
/* 131 */     while ((line = readLine(in)) != null)
/*     */     {
/* 133 */       if (!line.equals("-----BEGIN CERTIFICATE-----")) { if (line.equals("-----BEGIN X509 CERTIFICATE-----")) {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 140 */     while ((line = readLine(in)) != null)
/*     */     {
/* 142 */       if ((line.equals("-----END CERTIFICATE-----")) || (line.equals("-----END X509 CERTIFICATE-----"))) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 148 */       pemBuf.append(line);
/*     */     }
/*     */     
/* 151 */     if (pemBuf.length() != 0)
/*     */     {
/* 153 */       ByteArrayInputStream bIn = new ByteArrayInputStream(Base64.decode(pemBuf.toString()));
/* 154 */       return readDERCertificate(bIn);
/*     */     }
/*     */     
/* 157 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   private CRL readDERCRL(InputStream in)
/*     */     throws IOException
/*     */   {
/* 164 */     DERInputStream dIn = new DERInputStream(in);
/*     */     
/* 166 */     return new X509CRLObject(new CertificateList((ASN1Sequence)dIn.readObject()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private CRL readPEMCRL(InputStream in)
/*     */     throws IOException
/*     */   {
/* 174 */     StringBuilder pemBuf = new StringBuilder();
/*     */     String line;
/* 176 */     while ((line = readLine(in)) != null)
/*     */     {
/* 178 */       if (!line.equals("-----BEGIN CRL-----")) { if (line.equals("-----BEGIN X509 CRL-----")) {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 185 */     while ((line = readLine(in)) != null)
/*     */     {
/* 187 */       if ((line.equals("-----END CRL-----")) || (line.equals("-----END X509 CRL-----"))) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 193 */       pemBuf.append(line);
/*     */     }
/*     */     
/* 196 */     if (pemBuf.length() != 0)
/*     */     {
/* 198 */       ByteArrayInputStream bIn = new ByteArrayInputStream(Base64.decode(pemBuf.toString()));
/* 199 */       return readDERCRL(bIn);
/*     */     }
/*     */     
/* 202 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Certificate engineGenerateCertificate(InputStream in)
/*     */     throws CertificateException
/*     */   {
/* 213 */     if ((this.sData != null) && (this.sDataObjectCount != this.sData.getCertificates().size()))
/*     */     {
/* 215 */       return new X509CertificateObject(X509CertificateStructure.getInstance(this.sData.getCertificates().getObjectAt(this.sDataObjectCount++)));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 220 */     if (!in.markSupported())
/*     */     {
/* 222 */       in = new BufferedInputStream(in);
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 227 */       in.mark(10);
/* 228 */       int tag = in.read();
/*     */       
/* 230 */       if (tag == -1)
/*     */       {
/* 232 */         return null;
/*     */       }
/*     */       
/* 235 */       if (tag != 48)
/*     */       {
/* 237 */         in.reset();
/* 238 */         return readPEMCertificate(in);
/*     */       }
/* 240 */       if (in.read() == 128)
/*     */       {
/* 242 */         in.reset();
/* 243 */         return readPKCS7Certificate(in);
/*     */       }
/*     */       
/*     */ 
/* 247 */       in.reset();
/* 248 */       return readDERCertificate(in);
/*     */ 
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 253 */       throw new CertificateException(e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Collection engineGenerateCertificates(InputStream inStream)
/*     */     throws CertificateException
/*     */   {
/* 266 */     ArrayList certs = new ArrayList();
/*     */     Certificate cert;
/* 268 */     while ((cert = engineGenerateCertificate(inStream)) != null)
/*     */     {
/* 270 */       certs.add(cert);
/*     */     }
/*     */     
/* 273 */     return certs;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CRL engineGenerateCRL(InputStream inStream)
/*     */     throws CRLException
/*     */   {
/* 284 */     if (!inStream.markSupported())
/*     */     {
/* 286 */       inStream = new BufferedInputStream(inStream);
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 291 */       inStream.mark(10);
/* 292 */       if (inStream.read() != 48)
/*     */       {
/* 294 */         inStream.reset();
/* 295 */         return readPEMCRL(inStream);
/*     */       }
/*     */       
/*     */ 
/* 299 */       inStream.reset();
/* 300 */       return readDERCRL(inStream);
/*     */ 
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 305 */       throw new CRLException(e.toString());
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public Collection engineGenerateCRLs(InputStream inStream)
/*     */     throws CRLException
/*     */   {
/* 322 */     return null;
/*     */   }
/*     */   
/*     */   public Iterator engineGetCertPathEncodings()
/*     */   {
/* 327 */     return PKIXCertPath.certPathEncodings.iterator();
/*     */   }
/*     */   
/*     */ 
/*     */   public CertPath engineGenerateCertPath(InputStream inStream)
/*     */     throws CertificateException
/*     */   {
/* 334 */     return engineGenerateCertPath(inStream, "PkiPath");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public CertPath engineGenerateCertPath(InputStream inStream, String encoding)
/*     */     throws CertificateException
/*     */   {
/* 342 */     return new PKIXCertPath(inStream, encoding);
/*     */   }
/*     */   
/*     */ 
/*     */   public CertPath engineGenerateCertPath(List certificates)
/*     */     throws CertificateException
/*     */   {
/* 349 */     Iterator iter = certificates.iterator();
/*     */     
/* 351 */     while (iter.hasNext())
/*     */     {
/* 353 */       Object obj = iter.next();
/* 354 */       if ((obj != null) && 
/* 355 */         (!(obj instanceof X509Certificate)))
/*     */       {
/* 357 */         throw new CertificateException("list contains none X509Certificate object while creating CertPath\n" + obj.toString());
/*     */       }
/*     */     }
/*     */     
/* 361 */     return new PKIXCertPath(certificates);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/JDKX509CertificateFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */