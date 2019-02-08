/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.Principal;
/*     */ import java.security.Provider;
/*     */ import java.security.PublicKey;
/*     */ import java.security.Security;
/*     */ import java.security.Signature;
/*     */ import java.security.SignatureException;
/*     */ import java.security.cert.CRLException;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.X509CRL;
/*     */ import java.security.cert.X509CRLEntry;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Date;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import org.gudy.bouncycastle.asn1.ASN1OutputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DEROutputStream;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.x509.CertificateList;
/*     */ import org.gudy.bouncycastle.asn1.x509.TBSCertList;
/*     */ import org.gudy.bouncycastle.asn1.x509.TBSCertList.CRLEntry;
/*     */ import org.gudy.bouncycastle.asn1.x509.Time;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Extension;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Extensions;
/*     */ import org.gudy.bouncycastle.jce.X509Principal;
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
/*     */ public class X509CRLObject
/*     */   extends X509CRL
/*     */ {
/*     */   private CertificateList c;
/*     */   
/*     */   public X509CRLObject(CertificateList c)
/*     */   {
/*  55 */     this.c = c;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasUnsupportedCriticalExtension()
/*     */   {
/*  64 */     Set extns = getCriticalExtensionOIDs();
/*  65 */     if ((extns != null) && (!extns.isEmpty()))
/*     */     {
/*  67 */       return true;
/*     */     }
/*     */     
/*  70 */     return false;
/*     */   }
/*     */   
/*     */   private Set getExtensionOIDs(boolean critical)
/*     */   {
/*  75 */     if (getVersion() == 2)
/*     */     {
/*  77 */       HashSet set = new HashSet();
/*  78 */       X509Extensions extensions = this.c.getTBSCertList().getExtensions();
/*  79 */       Enumeration e = extensions.oids();
/*     */       
/*  81 */       while (e.hasMoreElements())
/*     */       {
/*  83 */         DERObjectIdentifier oid = (DERObjectIdentifier)e.nextElement();
/*  84 */         X509Extension ext = extensions.getExtension(oid);
/*     */         
/*  86 */         if (critical == ext.isCritical())
/*     */         {
/*  88 */           set.add(oid.getId());
/*     */         }
/*     */       }
/*     */       
/*  92 */       return set;
/*     */     }
/*     */     
/*  95 */     return null;
/*     */   }
/*     */   
/*     */   public Set getCriticalExtensionOIDs()
/*     */   {
/* 100 */     return getExtensionOIDs(true);
/*     */   }
/*     */   
/*     */   public Set getNonCriticalExtensionOIDs()
/*     */   {
/* 105 */     return getExtensionOIDs(false);
/*     */   }
/*     */   
/*     */   public byte[] getExtensionValue(String oid)
/*     */   {
/* 110 */     X509Extensions exts = this.c.getTBSCertList().getExtensions();
/*     */     
/* 112 */     if (exts != null)
/*     */     {
/* 114 */       X509Extension ext = exts.getExtension(new DERObjectIdentifier(oid));
/*     */       
/* 116 */       if (ext != null)
/*     */       {
/* 118 */         ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 119 */         DEROutputStream dOut = new DEROutputStream(bOut);
/*     */         
/*     */         try
/*     */         {
/* 123 */           dOut.writeObject(ext.getValue());
/*     */           
/* 125 */           return bOut.toByteArray();
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 129 */           throw new RuntimeException("error encoding " + e.toString());
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 134 */     return null;
/*     */   }
/*     */   
/*     */   public byte[] getEncoded()
/*     */     throws CRLException
/*     */   {
/* 140 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 141 */     DEROutputStream dOut = new DEROutputStream(bOut);
/*     */     
/*     */     try
/*     */     {
/* 145 */       dOut.writeObject(this.c);
/*     */       
/* 147 */       return bOut.toByteArray();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 151 */       throw new CRLException(e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void verify(PublicKey key)
/*     */     throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
/*     */   {
/* 160 */     verify(key, BouncyCastleProvider.PROVIDER_NAME);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void verify(PublicKey key, String sigProvider)
/*     */     throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
/*     */   {
/* 168 */     if (!this.c.getSignatureAlgorithm().equals(this.c.getTBSCertList().getSignature()))
/*     */     {
/* 170 */       throw new CRLException("Signature algorithm on CertifcateList does not match TBSCertList.");
/*     */     }
/*     */     
/* 173 */     Signature sig = Signature.getInstance(getSigAlgName(), sigProvider);
/*     */     
/* 175 */     sig.initVerify(key);
/* 176 */     sig.update(getTBSCertList());
/* 177 */     if (!sig.verify(getSignature()))
/*     */     {
/* 179 */       throw new SignatureException("CRL does not verify with supplied public key.");
/*     */     }
/*     */   }
/*     */   
/*     */   public int getVersion()
/*     */   {
/* 185 */     return this.c.getVersion();
/*     */   }
/*     */   
/*     */   public Principal getIssuerDN()
/*     */   {
/* 190 */     return new X509Principal(this.c.getIssuer());
/*     */   }
/*     */   
/*     */   public X500Principal getIssuerX500Principal()
/*     */   {
/*     */     try
/*     */     {
/* 197 */       ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 198 */       ASN1OutputStream aOut = new ASN1OutputStream(bOut);
/*     */       
/* 200 */       aOut.writeObject(this.c.getIssuer());
/*     */       
/* 202 */       return new X500Principal(bOut.toByteArray());
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 206 */       throw new IllegalStateException("can't encode issuer DN");
/*     */     }
/*     */   }
/*     */   
/*     */   public Date getThisUpdate()
/*     */   {
/* 212 */     return this.c.getThisUpdate().getDate();
/*     */   }
/*     */   
/*     */   public Date getNextUpdate()
/*     */   {
/* 217 */     if (this.c.getNextUpdate() != null)
/*     */     {
/* 219 */       return this.c.getNextUpdate().getDate();
/*     */     }
/*     */     
/* 222 */     return null;
/*     */   }
/*     */   
/*     */   public X509CRLEntry getRevokedCertificate(BigInteger serialNumber)
/*     */   {
/* 227 */     TBSCertList.CRLEntry[] certs = this.c.getRevokedCertificates();
/*     */     
/* 229 */     if (certs != null)
/*     */     {
/* 231 */       for (int i = 0; i < certs.length; i++)
/*     */       {
/* 233 */         if (certs[i].getUserCertificate().getValue().equals(serialNumber)) {
/* 234 */           return new X509CRLEntryObject(certs[i]);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 239 */     return null;
/*     */   }
/*     */   
/*     */   public Set getRevokedCertificates()
/*     */   {
/* 244 */     TBSCertList.CRLEntry[] certs = this.c.getRevokedCertificates();
/*     */     
/* 246 */     if (certs != null)
/*     */     {
/* 248 */       HashSet set = new HashSet();
/* 249 */       for (int i = 0; i < certs.length; i++)
/*     */       {
/* 251 */         set.add(new X509CRLEntryObject(certs[i]));
/*     */       }
/*     */       
/*     */ 
/* 255 */       return set;
/*     */     }
/*     */     
/* 258 */     return null;
/*     */   }
/*     */   
/*     */   public byte[] getTBSCertList()
/*     */     throws CRLException
/*     */   {
/* 264 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 265 */     DEROutputStream dOut = new DEROutputStream(bOut);
/*     */     
/*     */     try
/*     */     {
/* 269 */       dOut.writeObject(this.c.getTBSCertList());
/*     */       
/* 271 */       return bOut.toByteArray();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 275 */       throw new CRLException(e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */   public byte[] getSignature()
/*     */   {
/* 281 */     return this.c.getSignature().getBytes();
/*     */   }
/*     */   
/*     */   public String getSigAlgName()
/*     */   {
/* 286 */     Provider prov = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
/* 287 */     String algName = prov.getProperty("Alg.Alias.Signature." + getSigAlgOID());
/*     */     
/* 289 */     if (algName != null)
/*     */     {
/* 291 */       return algName;
/*     */     }
/*     */     
/* 294 */     Provider[] provs = Security.getProviders();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 299 */     for (int i = 0; i != provs.length; i++)
/*     */     {
/* 301 */       algName = provs[i].getProperty("Alg.Alias.Signature." + getSigAlgOID());
/* 302 */       if (algName != null)
/*     */       {
/* 304 */         return algName;
/*     */       }
/*     */     }
/*     */     
/* 308 */     return getSigAlgOID();
/*     */   }
/*     */   
/*     */   public String getSigAlgOID()
/*     */   {
/* 313 */     return this.c.getSignatureAlgorithm().getObjectId().getId();
/*     */   }
/*     */   
/*     */   public byte[] getSigAlgParams()
/*     */   {
/* 318 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/*     */     
/* 320 */     if (this.c.getSignatureAlgorithm().getParameters() != null)
/*     */     {
/*     */       try
/*     */       {
/* 324 */         DEROutputStream dOut = new DEROutputStream(bOut);
/*     */         
/* 326 */         dOut.writeObject(this.c.getSignatureAlgorithm().getParameters());
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 330 */         throw new RuntimeException("exception getting sig parameters " + e);
/*     */       }
/*     */       
/* 333 */       return bOut.toByteArray();
/*     */     }
/*     */     
/* 336 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 346 */     return "X.509 CRL";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isRevoked(Certificate cert)
/*     */   {
/* 358 */     if (!cert.getType().equals("X.509"))
/*     */     {
/* 360 */       throw new RuntimeException("X.509 CRL used with non X.509 Cert");
/*     */     }
/*     */     
/* 363 */     TBSCertList.CRLEntry[] certs = this.c.getRevokedCertificates();
/*     */     
/* 365 */     if (certs != null)
/*     */     {
/* 367 */       BigInteger serial = ((X509Certificate)cert).getSerialNumber();
/*     */       
/* 369 */       for (int i = 0; i < certs.length; i++)
/*     */       {
/* 371 */         if (certs[i].getUserCertificate().getValue().equals(serial))
/*     */         {
/* 373 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 378 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/X509CRLObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */