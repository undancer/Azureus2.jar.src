/*     */ package org.gudy.bouncycastle.x509;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.PublicKey;
/*     */ import java.security.Signature;
/*     */ import java.security.SignatureException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateExpiredException;
/*     */ import java.security.cert.CertificateNotYetValidException;
/*     */ import java.text.ParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1InputStream;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DERGeneralizedTime;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DEROutputStream;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.x509.AttCertValidityPeriod;
/*     */ import org.gudy.bouncycastle.asn1.x509.AttributeCertificate;
/*     */ import org.gudy.bouncycastle.asn1.x509.AttributeCertificateInfo;
/*     */ import org.gudy.bouncycastle.asn1.x509.Holder;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Extension;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Extensions;
/*     */ import org.gudy.bouncycastle.util.Arrays;
/*     */ 
/*     */ public class X509V2AttributeCertificate
/*     */   implements X509AttributeCertificate
/*     */ {
/*     */   private AttributeCertificate cert;
/*     */   private Date notBefore;
/*     */   private Date notAfter;
/*     */   
/*     */   public X509V2AttributeCertificate(InputStream encIn) throws IOException
/*     */   {
/*  50 */     this(AttributeCertificate.getInstance(new ASN1InputStream(encIn).readObject()));
/*     */   }
/*     */   
/*     */ 
/*     */   public X509V2AttributeCertificate(byte[] encoded)
/*     */     throws IOException
/*     */   {
/*  57 */     this(new ByteArrayInputStream(encoded));
/*     */   }
/*     */   
/*     */ 
/*     */   X509V2AttributeCertificate(AttributeCertificate cert)
/*     */     throws IOException
/*     */   {
/*  64 */     this.cert = cert;
/*     */     
/*     */     try
/*     */     {
/*  68 */       this.notAfter = cert.getAcinfo().getAttrCertValidityPeriod().getNotAfterTime().getDate();
/*  69 */       this.notBefore = cert.getAcinfo().getAttrCertValidityPeriod().getNotBeforeTime().getDate();
/*     */     }
/*     */     catch (ParseException e)
/*     */     {
/*  73 */       throw new IOException("invalid data structure in certificate!");
/*     */     }
/*     */   }
/*     */   
/*     */   public int getVersion()
/*     */   {
/*  79 */     return this.cert.getAcinfo().getVersion().getValue().intValue();
/*     */   }
/*     */   
/*     */   public BigInteger getSerialNumber()
/*     */   {
/*  84 */     return this.cert.getAcinfo().getSerialNumber().getValue();
/*     */   }
/*     */   
/*     */   public AttributeCertificateHolder getHolder()
/*     */   {
/*  89 */     return new AttributeCertificateHolder((ASN1Sequence)this.cert.getAcinfo().getHolder().toASN1Object());
/*     */   }
/*     */   
/*     */   public AttributeCertificateIssuer getIssuer()
/*     */   {
/*  94 */     return new AttributeCertificateIssuer(this.cert.getAcinfo().getIssuer());
/*     */   }
/*     */   
/*     */   public Date getNotBefore()
/*     */   {
/*  99 */     return this.notBefore;
/*     */   }
/*     */   
/*     */   public Date getNotAfter()
/*     */   {
/* 104 */     return this.notAfter;
/*     */   }
/*     */   
/*     */   public boolean[] getIssuerUniqueID()
/*     */   {
/* 109 */     DERBitString id = this.cert.getAcinfo().getIssuerUniqueID();
/*     */     
/* 111 */     if (id != null)
/*     */     {
/* 113 */       byte[] bytes = id.getBytes();
/* 114 */       boolean[] boolId = new boolean[bytes.length * 8 - id.getPadBits()];
/*     */       
/* 116 */       for (int i = 0; i != boolId.length; i++)
/*     */       {
/* 118 */         boolId[i] = ((bytes[(i / 8)] & 128 >>> i % 8) != 0 ? 1 : false);
/*     */       }
/*     */       
/* 121 */       return boolId;
/*     */     }
/*     */     
/* 124 */     return null;
/*     */   }
/*     */   
/*     */   public void checkValidity()
/*     */     throws CertificateExpiredException, CertificateNotYetValidException
/*     */   {
/* 130 */     checkValidity(new Date());
/*     */   }
/*     */   
/*     */ 
/*     */   public void checkValidity(Date date)
/*     */     throws CertificateExpiredException, CertificateNotYetValidException
/*     */   {
/* 137 */     if (date.after(getNotAfter()))
/*     */     {
/* 139 */       throw new CertificateExpiredException("certificate expired on " + getNotAfter());
/*     */     }
/*     */     
/* 142 */     if (date.before(getNotBefore()))
/*     */     {
/* 144 */       throw new CertificateNotYetValidException("certificate not valid till " + getNotBefore());
/*     */     }
/*     */   }
/*     */   
/*     */   public byte[] getSignature()
/*     */   {
/* 150 */     return this.cert.getSignatureValue().getBytes();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void verify(PublicKey key, String provider)
/*     */     throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
/*     */   {
/* 159 */     Signature signature = null;
/*     */     
/* 161 */     if (!this.cert.getSignatureAlgorithm().equals(this.cert.getAcinfo().getSignature()))
/*     */     {
/* 163 */       throw new CertificateException("Signature algorithm in certificate info not same as outer certificate");
/*     */     }
/*     */     
/* 166 */     signature = Signature.getInstance(this.cert.getSignatureAlgorithm().getObjectId().getId(), provider);
/*     */     
/* 168 */     signature.initVerify(key);
/*     */     
/*     */     try
/*     */     {
/* 172 */       signature.update(this.cert.getAcinfo().getEncoded());
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 176 */       throw new SignatureException("Exception encoding certificate info object");
/*     */     }
/*     */     
/* 179 */     if (!signature.verify(getSignature()))
/*     */     {
/* 181 */       throw new InvalidKeyException("Public key presented not for certificate signature");
/*     */     }
/*     */   }
/*     */   
/*     */   public byte[] getEncoded()
/*     */     throws IOException
/*     */   {
/* 188 */     return this.cert.getEncoded();
/*     */   }
/*     */   
/*     */   public byte[] getExtensionValue(String oid)
/*     */   {
/* 193 */     X509Extensions extensions = this.cert.getAcinfo().getExtensions();
/*     */     
/* 195 */     if (extensions != null)
/*     */     {
/* 197 */       X509Extension ext = extensions.getExtension(new DERObjectIdentifier(oid));
/*     */       
/* 199 */       if (ext != null)
/*     */       {
/* 201 */         ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 202 */         DEROutputStream dOut = new DEROutputStream(bOut);
/*     */         
/*     */         try
/*     */         {
/* 206 */           dOut.writeObject(ext.getValue());
/*     */           
/* 208 */           return bOut.toByteArray();
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 212 */           throw new RuntimeException("error encoding " + e.toString());
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 217 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   private Set getExtensionOIDs(boolean critical)
/*     */   {
/* 223 */     X509Extensions extensions = this.cert.getAcinfo().getExtensions();
/*     */     
/* 225 */     if (extensions != null)
/*     */     {
/* 227 */       Set set = new HashSet();
/* 228 */       Enumeration e = extensions.oids();
/*     */       
/* 230 */       while (e.hasMoreElements())
/*     */       {
/* 232 */         DERObjectIdentifier oid = (DERObjectIdentifier)e.nextElement();
/* 233 */         X509Extension ext = extensions.getExtension(oid);
/*     */         
/* 235 */         if (ext.isCritical() == critical)
/*     */         {
/* 237 */           set.add(oid.getId());
/*     */         }
/*     */       }
/*     */       
/* 241 */       return set;
/*     */     }
/*     */     
/* 244 */     return null;
/*     */   }
/*     */   
/*     */   public Set getNonCriticalExtensionOIDs()
/*     */   {
/* 249 */     return getExtensionOIDs(false);
/*     */   }
/*     */   
/*     */   public Set getCriticalExtensionOIDs()
/*     */   {
/* 254 */     return getExtensionOIDs(true);
/*     */   }
/*     */   
/*     */   public boolean hasUnsupportedCriticalExtension()
/*     */   {
/* 259 */     Set extensions = getCriticalExtensionOIDs();
/*     */     
/* 261 */     return (extensions != null) && (!extensions.isEmpty());
/*     */   }
/*     */   
/*     */   public X509Attribute[] getAttributes()
/*     */   {
/* 266 */     ASN1Sequence seq = this.cert.getAcinfo().getAttributes();
/* 267 */     X509Attribute[] attrs = new X509Attribute[seq.size()];
/*     */     
/* 269 */     for (int i = 0; i != seq.size(); i++)
/*     */     {
/* 271 */       attrs[i] = new X509Attribute((ASN1Encodable)seq.getObjectAt(i));
/*     */     }
/*     */     
/* 274 */     return attrs;
/*     */   }
/*     */   
/*     */   public X509Attribute[] getAttributes(String oid)
/*     */   {
/* 279 */     ASN1Sequence seq = this.cert.getAcinfo().getAttributes();
/* 280 */     List list = new ArrayList();
/*     */     
/* 282 */     for (int i = 0; i != seq.size(); i++)
/*     */     {
/* 284 */       X509Attribute attr = new X509Attribute((ASN1Encodable)seq.getObjectAt(i));
/* 285 */       if (attr.getOID().equals(oid))
/*     */       {
/* 287 */         list.add(attr);
/*     */       }
/*     */     }
/*     */     
/* 291 */     if (list.size() == 0)
/*     */     {
/* 293 */       return null;
/*     */     }
/*     */     
/* 296 */     return (X509Attribute[])list.toArray(new X509Attribute[list.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 302 */     if (o == this)
/*     */     {
/* 304 */       return true;
/*     */     }
/*     */     
/* 307 */     if (!(o instanceof X509AttributeCertificate))
/*     */     {
/* 309 */       return false;
/*     */     }
/*     */     
/* 312 */     X509AttributeCertificate other = (X509AttributeCertificate)o;
/*     */     
/*     */     try
/*     */     {
/* 316 */       byte[] b1 = getEncoded();
/* 317 */       byte[] b2 = other.getEncoded();
/*     */       
/* 319 */       return Arrays.areEqual(b1, b2);
/*     */     }
/*     */     catch (IOException e) {}
/*     */     
/* 323 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*     */     try
/*     */     {
/* 331 */       byte[] b = getEncoded();
/* 332 */       int value = 0;
/*     */       
/* 334 */       for (int i = 0; i != b.length; i++)
/*     */       {
/* 336 */         value ^= (b[i] & 0xFF) << i % 4;
/*     */       }
/*     */       
/* 339 */       return value;
/*     */     }
/*     */     catch (IOException e) {}
/*     */     
/* 343 */     return 0;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/x509/X509V2AttributeCertificate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */