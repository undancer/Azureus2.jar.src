/*     */ package org.gudy.bouncycastle.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.Principal;
/*     */ import java.security.PublicKey;
/*     */ import java.security.cert.CertSelector;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateEncodingException;
/*     */ import java.security.cert.CertificateParsingException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DEREnumerated;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.x509.GeneralName;
/*     */ import org.gudy.bouncycastle.asn1.x509.GeneralNames;
/*     */ import org.gudy.bouncycastle.asn1.x509.Holder;
/*     */ import org.gudy.bouncycastle.asn1.x509.IssuerSerial;
/*     */ import org.gudy.bouncycastle.asn1.x509.ObjectDigestInfo;
/*     */ import org.gudy.bouncycastle.jce.PrincipalUtil;
/*     */ import org.gudy.bouncycastle.jce.X509Principal;
/*     */ import org.gudy.bouncycastle.jce.provider.BouncyCastleProvider;
/*     */ import org.gudy.bouncycastle.util.Arrays;
/*     */ import org.gudy.bouncycastle.util.Selector;
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
/*     */ public class AttributeCertificateHolder
/*     */   implements CertSelector, Selector
/*     */ {
/*     */   final Holder holder;
/*     */   
/*     */   AttributeCertificateHolder(ASN1Sequence seq)
/*     */   {
/*  57 */     this.holder = Holder.getInstance(seq);
/*     */   }
/*     */   
/*     */ 
/*     */   public AttributeCertificateHolder(X509Principal issuerName, BigInteger serialNumber)
/*     */   {
/*  63 */     this.holder = new Holder(new IssuerSerial(new GeneralNames(new DERSequence(new GeneralName(issuerName))), new DERInteger(serialNumber)));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public AttributeCertificateHolder(X500Principal issuerName, BigInteger serialNumber)
/*     */   {
/*  71 */     this(X509Util.convertPrincipal(issuerName), serialNumber);
/*     */   }
/*     */   
/*     */ 
/*     */   public AttributeCertificateHolder(X509Certificate cert)
/*     */     throws CertificateParsingException
/*     */   {
/*     */     X509Principal name;
/*     */     try
/*     */     {
/*  81 */       name = PrincipalUtil.getIssuerX509Principal(cert);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  85 */       throw new CertificateParsingException(e.getMessage());
/*     */     }
/*     */     
/*  88 */     this.holder = new Holder(new IssuerSerial(generateGeneralNames(name), new DERInteger(cert.getSerialNumber())));
/*     */   }
/*     */   
/*     */ 
/*     */   public AttributeCertificateHolder(X509Principal principal)
/*     */   {
/*  94 */     this.holder = new Holder(generateGeneralNames(principal));
/*     */   }
/*     */   
/*     */   public AttributeCertificateHolder(X500Principal principal)
/*     */   {
/*  99 */     this(X509Util.convertPrincipal(principal));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AttributeCertificateHolder(int digestedObjectType, String digestAlgorithm, String otherObjectTypeID, byte[] objectDigest)
/*     */   {
/* 128 */     this.holder = new Holder(new ObjectDigestInfo(digestedObjectType, otherObjectTypeID, new AlgorithmIdentifier(digestAlgorithm), Arrays.clone(objectDigest)));
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
/*     */ 
/*     */   public int getDigestedObjectType()
/*     */   {
/* 149 */     if (this.holder.getObjectDigestInfo() != null)
/*     */     {
/* 151 */       return this.holder.getObjectDigestInfo().getDigestedObjectType().getValue().intValue();
/*     */     }
/*     */     
/* 154 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getDigestAlgorithm()
/*     */   {
/* 165 */     if (this.holder.getObjectDigestInfo() != null)
/*     */     {
/* 167 */       this.holder.getObjectDigestInfo().getDigestAlgorithm().getObjectId().getId();
/*     */     }
/*     */     
/* 170 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getObjectDigest()
/*     */   {
/* 180 */     if (this.holder.getObjectDigestInfo() != null)
/*     */     {
/* 182 */       this.holder.getObjectDigestInfo().getObjectDigest().getBytes();
/*     */     }
/* 184 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getOtherObjectTypeID()
/*     */   {
/* 195 */     if (this.holder.getObjectDigestInfo() != null)
/*     */     {
/* 197 */       this.holder.getObjectDigestInfo().getOtherObjectTypeID().getId();
/*     */     }
/* 199 */     return null;
/*     */   }
/*     */   
/*     */   private GeneralNames generateGeneralNames(X509Principal principal)
/*     */   {
/* 204 */     return new GeneralNames(new DERSequence(new GeneralName(principal)));
/*     */   }
/*     */   
/*     */   private boolean matchesDN(X509Principal subject, GeneralNames targets)
/*     */   {
/* 209 */     GeneralName[] names = targets.getNames();
/*     */     
/* 211 */     for (int i = 0; i != names.length; i++)
/*     */     {
/* 213 */       GeneralName gn = names[i];
/*     */       
/* 215 */       if (gn.getTagNo() == 4)
/*     */       {
/*     */         try
/*     */         {
/* 219 */           if (new X509Principal(((ASN1Encodable)gn.getName()).getEncoded()).equals(subject))
/*     */           {
/*     */ 
/* 222 */             return true;
/*     */           }
/*     */         }
/*     */         catch (IOException e) {}
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 231 */     return false;
/*     */   }
/*     */   
/*     */   private Object[] getNames(GeneralName[] names)
/*     */   {
/* 236 */     List l = new ArrayList(names.length);
/*     */     
/* 238 */     for (int i = 0; i != names.length; i++)
/*     */     {
/* 240 */       if (names[i].getTagNo() == 4)
/*     */       {
/*     */         try
/*     */         {
/* 244 */           l.add(new X500Principal(((ASN1Encodable)names[i].getName()).getEncoded()));
/*     */ 
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/* 249 */           throw new RuntimeException("badly formed Name object");
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 254 */     return l.toArray(new Object[l.size()]);
/*     */   }
/*     */   
/*     */   private Principal[] getPrincipals(GeneralNames names)
/*     */   {
/* 259 */     Object[] p = getNames(names.getNames());
/* 260 */     List l = new ArrayList();
/*     */     
/* 262 */     for (int i = 0; i != p.length; i++)
/*     */     {
/* 264 */       if ((p[i] instanceof Principal))
/*     */       {
/* 266 */         l.add(p[i]);
/*     */       }
/*     */     }
/*     */     
/* 270 */     return (Principal[])l.toArray(new Principal[l.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Principal[] getEntityNames()
/*     */   {
/* 282 */     if (this.holder.getEntityName() != null)
/*     */     {
/* 284 */       return getPrincipals(this.holder.getEntityName());
/*     */     }
/*     */     
/* 287 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Principal[] getIssuer()
/*     */   {
/* 297 */     if (this.holder.getBaseCertificateID() != null)
/*     */     {
/* 299 */       return getPrincipals(this.holder.getBaseCertificateID().getIssuer());
/*     */     }
/*     */     
/* 302 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BigInteger getSerialNumber()
/*     */   {
/* 314 */     if (this.holder.getBaseCertificateID() != null)
/*     */     {
/* 316 */       return this.holder.getBaseCertificateID().getSerial().getValue();
/*     */     }
/*     */     
/* 319 */     return null;
/*     */   }
/*     */   
/*     */   public Object clone()
/*     */   {
/* 324 */     return new AttributeCertificateHolder((ASN1Sequence)this.holder.toASN1Object());
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean match(Certificate cert)
/*     */   {
/* 330 */     if (!(cert instanceof X509Certificate))
/*     */     {
/* 332 */       return false;
/*     */     }
/*     */     
/* 335 */     X509Certificate x509Cert = (X509Certificate)cert;
/*     */     
/*     */     try
/*     */     {
/* 339 */       if (this.holder.getBaseCertificateID() != null)
/*     */       {
/* 341 */         return (this.holder.getBaseCertificateID().getSerial().getValue().equals(x509Cert.getSerialNumber())) && (matchesDN(PrincipalUtil.getIssuerX509Principal(x509Cert), this.holder.getBaseCertificateID().getIssuer()));
/*     */       }
/*     */       
/*     */ 
/* 345 */       if (this.holder.getEntityName() != null)
/*     */       {
/* 347 */         if (matchesDN(PrincipalUtil.getSubjectX509Principal(x509Cert), this.holder.getEntityName()))
/*     */         {
/*     */ 
/* 350 */           return true;
/*     */         }
/*     */       }
/* 353 */       if (this.holder.getObjectDigestInfo() != null)
/*     */       {
/* 355 */         MessageDigest md = null;
/*     */         try
/*     */         {
/* 358 */           md = MessageDigest.getInstance(getDigestAlgorithm(), BouncyCastleProvider.PROVIDER_NAME);
/*     */ 
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 363 */           return false;
/*     */         }
/* 365 */         switch (getDigestedObjectType())
/*     */         {
/*     */ 
/*     */         case 0: 
/* 369 */           md.update(cert.getPublicKey().getEncoded());
/* 370 */           break;
/*     */         case 1: 
/* 372 */           md.update(cert.getEncoded());
/*     */         }
/*     */         
/* 375 */         if (!Arrays.areEqual(md.digest(), getObjectDigest()))
/*     */         {
/* 377 */           return false;
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (CertificateEncodingException e)
/*     */     {
/* 383 */       return false;
/*     */     }
/*     */     
/* 386 */     return false;
/*     */   }
/*     */   
/*     */   public boolean equals(Object obj)
/*     */   {
/* 391 */     if (obj == this)
/*     */     {
/* 393 */       return true;
/*     */     }
/*     */     
/* 396 */     if (!(obj instanceof AttributeCertificateHolder))
/*     */     {
/* 398 */       return false;
/*     */     }
/*     */     
/* 401 */     AttributeCertificateHolder other = (AttributeCertificateHolder)obj;
/*     */     
/* 403 */     return this.holder.equals(other.holder);
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 408 */     return this.holder.hashCode();
/*     */   }
/*     */   
/*     */   public boolean match(Object obj)
/*     */   {
/* 413 */     if (!(obj instanceof X509Certificate))
/*     */     {
/* 415 */       return false;
/*     */     }
/*     */     
/* 418 */     return match((Certificate)obj);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/x509/AttributeCertificateHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */