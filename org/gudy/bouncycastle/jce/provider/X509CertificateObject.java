/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
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
/*     */ import java.security.cert.CertificateEncodingException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateExpiredException;
/*     */ import java.security.cert.CertificateNotYetValidException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Date;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*     */ import org.gudy.bouncycastle.asn1.ASN1OutputStream;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DERBoolean;
/*     */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*     */ import org.gudy.bouncycastle.asn1.DERIA5String;
/*     */ import org.gudy.bouncycastle.asn1.DERInputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DEROutputStream;
/*     */ import org.gudy.bouncycastle.asn1.misc.MiscObjectIdentifiers;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.x509.TBSCertificateStructure;
/*     */ import org.gudy.bouncycastle.asn1.x509.Time;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509CertificateStructure;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Extension;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Extensions;
/*     */ import org.gudy.bouncycastle.jce.X509Principal;
/*     */ import org.gudy.bouncycastle.util.encoders.Hex;
/*     */ 
/*     */ public class X509CertificateObject extends X509Certificate implements org.gudy.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier
/*     */ {
/*     */   private X509CertificateStructure c;
/*  52 */   private Hashtable pkcs12Attributes = new Hashtable();
/*  53 */   private Vector pkcs12Ordering = new Vector();
/*     */   
/*     */ 
/*     */   public X509CertificateObject(X509CertificateStructure c)
/*     */   {
/*  58 */     this.c = c;
/*     */   }
/*     */   
/*     */   public void checkValidity()
/*     */     throws CertificateExpiredException, CertificateNotYetValidException
/*     */   {
/*  64 */     checkValidity(new Date());
/*     */   }
/*     */   
/*     */ 
/*     */   public void checkValidity(Date date)
/*     */     throws CertificateExpiredException, CertificateNotYetValidException
/*     */   {
/*  71 */     if (date.after(getNotAfter()))
/*     */     {
/*  73 */       throw new CertificateExpiredException("certificate expired on " + this.c.getEndDate().getTime());
/*     */     }
/*     */     
/*  76 */     if (date.before(getNotBefore()))
/*     */     {
/*  78 */       throw new CertificateNotYetValidException("certificate not valid till " + this.c.getStartDate().getTime());
/*     */     }
/*     */   }
/*     */   
/*     */   public int getVersion()
/*     */   {
/*  84 */     return this.c.getVersion();
/*     */   }
/*     */   
/*     */   public BigInteger getSerialNumber()
/*     */   {
/*  89 */     return this.c.getSerialNumber().getValue();
/*     */   }
/*     */   
/*     */   public Principal getIssuerDN()
/*     */   {
/*  94 */     return new X509Principal(this.c.getIssuer());
/*     */   }
/*     */   
/*     */   public X500Principal getIssuerX500Principal()
/*     */   {
/*     */     try
/*     */     {
/* 101 */       ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 102 */       ASN1OutputStream aOut = new ASN1OutputStream(bOut);
/*     */       
/* 104 */       aOut.writeObject(this.c.getIssuer());
/*     */       
/* 106 */       return new X500Principal(bOut.toByteArray());
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 110 */       throw new IllegalStateException("can't encode issuer DN");
/*     */     }
/*     */   }
/*     */   
/*     */   public Principal getSubjectDN()
/*     */   {
/* 116 */     return new X509Principal(this.c.getSubject());
/*     */   }
/*     */   
/*     */   public X500Principal getSubjectX500Principal()
/*     */   {
/*     */     try
/*     */     {
/* 123 */       ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 124 */       ASN1OutputStream aOut = new ASN1OutputStream(bOut);
/*     */       
/* 126 */       aOut.writeObject(this.c.getSubject());
/*     */       
/* 128 */       return new X500Principal(bOut.toByteArray());
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 132 */       throw new IllegalStateException("can't encode issuer DN");
/*     */     }
/*     */   }
/*     */   
/*     */   public Date getNotBefore()
/*     */   {
/* 138 */     return this.c.getStartDate().getDate();
/*     */   }
/*     */   
/*     */   public Date getNotAfter()
/*     */   {
/* 143 */     return this.c.getEndDate().getDate();
/*     */   }
/*     */   
/*     */   public byte[] getTBSCertificate()
/*     */     throws CertificateEncodingException
/*     */   {
/* 149 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 150 */     DEROutputStream dOut = new DEROutputStream(bOut);
/*     */     
/*     */     try
/*     */     {
/* 154 */       dOut.writeObject(this.c.getTBSCertificate());
/*     */       
/* 156 */       return bOut.toByteArray();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 160 */       throw new CertificateEncodingException(e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */   public byte[] getSignature()
/*     */   {
/* 166 */     return this.c.getSignature().getBytes();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getSigAlgName()
/*     */   {
/* 175 */     Provider prov = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
/* 176 */     String algName = prov.getProperty("Alg.Alias.Signature." + getSigAlgOID());
/*     */     
/* 178 */     if (algName != null)
/*     */     {
/* 180 */       return algName;
/*     */     }
/*     */     
/* 183 */     Provider[] provs = Security.getProviders();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 188 */     for (int i = 0; i != provs.length; i++)
/*     */     {
/* 190 */       algName = provs[i].getProperty("Alg.Alias.Signature." + getSigAlgOID());
/* 191 */       if (algName != null)
/*     */       {
/* 193 */         return algName;
/*     */       }
/*     */     }
/*     */     
/* 197 */     return getSigAlgOID();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getSigAlgOID()
/*     */   {
/* 205 */     return this.c.getSignatureAlgorithm().getObjectId().getId();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getSigAlgParams()
/*     */   {
/* 213 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/*     */     
/* 215 */     if (this.c.getSignatureAlgorithm().getParameters() != null)
/*     */     {
/*     */       try
/*     */       {
/* 219 */         DEROutputStream dOut = new DEROutputStream(bOut);
/*     */         
/* 221 */         dOut.writeObject(this.c.getSignatureAlgorithm().getParameters());
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 225 */         throw new RuntimeException("exception getting sig parameters " + e);
/*     */       }
/*     */       
/* 228 */       return bOut.toByteArray();
/*     */     }
/*     */     
/*     */ 
/* 232 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean[] getIssuerUniqueID()
/*     */   {
/* 238 */     DERBitString id = this.c.getTBSCertificate().getIssuerUniqueId();
/*     */     
/* 240 */     if (id != null)
/*     */     {
/* 242 */       byte[] bytes = id.getBytes();
/* 243 */       boolean[] boolId = new boolean[bytes.length * 8 - id.getPadBits()];
/*     */       
/* 245 */       for (int i = 0; i != boolId.length; i++)
/*     */       {
/* 247 */         boolId[i] = ((bytes[(i / 8)] & 128 >>> i % 8) != 0 ? 1 : false);
/*     */       }
/*     */       
/* 250 */       return boolId;
/*     */     }
/*     */     
/* 253 */     return null;
/*     */   }
/*     */   
/*     */   public boolean[] getSubjectUniqueID()
/*     */   {
/* 258 */     DERBitString id = this.c.getTBSCertificate().getSubjectUniqueId();
/*     */     
/* 260 */     if (id != null)
/*     */     {
/* 262 */       byte[] bytes = id.getBytes();
/* 263 */       boolean[] boolId = new boolean[bytes.length * 8 - id.getPadBits()];
/*     */       
/* 265 */       for (int i = 0; i != boolId.length; i++)
/*     */       {
/* 267 */         boolId[i] = ((bytes[(i / 8)] & 128 >>> i % 8) != 0 ? 1 : false);
/*     */       }
/*     */       
/* 270 */       return boolId;
/*     */     }
/*     */     
/* 273 */     return null;
/*     */   }
/*     */   
/*     */   public boolean[] getKeyUsage()
/*     */   {
/* 278 */     byte[] bytes = getExtensionBytes("2.5.29.15");
/* 279 */     int length = 0;
/*     */     
/* 281 */     if (bytes != null)
/*     */     {
/*     */       try
/*     */       {
/* 285 */         DERInputStream dIn = new DERInputStream(new ByteArrayInputStream(bytes));
/* 286 */         DERBitString bits = (DERBitString)dIn.readObject();
/*     */         
/* 288 */         bytes = bits.getBytes();
/* 289 */         length = bytes.length * 8 - bits.getPadBits();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 293 */         throw new RuntimeException("error processing key usage extension");
/*     */       }
/*     */       
/* 296 */       boolean[] keyUsage = new boolean[length < 9 ? 9 : length];
/*     */       
/* 298 */       for (int i = 0; i != length; i++)
/*     */       {
/* 300 */         keyUsage[i] = ((bytes[(i / 8)] & 128 >>> i % 8) != 0 ? 1 : false);
/*     */       }
/*     */       
/* 303 */       return keyUsage;
/*     */     }
/*     */     
/* 306 */     return null;
/*     */   }
/*     */   
/*     */   public int getBasicConstraints()
/*     */   {
/* 311 */     byte[] bytes = getExtensionBytes("2.5.29.19");
/*     */     
/* 313 */     if (bytes != null)
/*     */     {
/*     */       try
/*     */       {
/* 317 */         DERInputStream dIn = new DERInputStream(new ByteArrayInputStream(bytes));
/* 318 */         ASN1Sequence seq = (ASN1Sequence)dIn.readObject();
/*     */         
/* 320 */         if (seq.size() == 2)
/*     */         {
/* 322 */           if (((DERBoolean)seq.getObjectAt(0)).isTrue())
/*     */           {
/* 324 */             return ((DERInteger)seq.getObjectAt(1)).getValue().intValue();
/*     */           }
/*     */           
/*     */ 
/* 328 */           return -1;
/*     */         }
/*     */         
/* 331 */         if (seq.size() == 1)
/*     */         {
/* 333 */           if ((seq.getObjectAt(0) instanceof DERBoolean))
/*     */           {
/* 335 */             if (((DERBoolean)seq.getObjectAt(0)).isTrue())
/*     */             {
/* 337 */               return Integer.MAX_VALUE;
/*     */             }
/*     */             
/*     */ 
/* 341 */             return -1;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 346 */           return -1;
/*     */         }
/*     */         
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 352 */         throw new RuntimeException("error processing key usage extension");
/*     */       }
/*     */     }
/*     */     
/* 356 */     return -1;
/*     */   }
/*     */   
/*     */   public Set getCriticalExtensionOIDs()
/*     */   {
/* 361 */     if (getVersion() == 3)
/*     */     {
/* 363 */       HashSet set = new HashSet();
/* 364 */       X509Extensions extensions = this.c.getTBSCertificate().getExtensions();
/*     */       
/* 366 */       if (extensions != null)
/*     */       {
/* 368 */         Enumeration e = extensions.oids();
/*     */         
/* 370 */         while (e.hasMoreElements())
/*     */         {
/* 372 */           DERObjectIdentifier oid = (DERObjectIdentifier)e.nextElement();
/* 373 */           X509Extension ext = extensions.getExtension(oid);
/*     */           
/* 375 */           if (ext.isCritical())
/*     */           {
/* 377 */             set.add(oid.getId());
/*     */           }
/*     */         }
/*     */         
/* 381 */         return set;
/*     */       }
/*     */     }
/*     */     
/* 385 */     return null;
/*     */   }
/*     */   
/*     */   private byte[] getExtensionBytes(String oid)
/*     */   {
/* 390 */     X509Extensions exts = this.c.getTBSCertificate().getExtensions();
/*     */     
/* 392 */     if (exts != null)
/*     */     {
/* 394 */       X509Extension ext = exts.getExtension(new DERObjectIdentifier(oid));
/* 395 */       if (ext != null)
/*     */       {
/* 397 */         return ext.getValue().getOctets();
/*     */       }
/*     */     }
/*     */     
/* 401 */     return null;
/*     */   }
/*     */   
/*     */   public byte[] getExtensionValue(String oid)
/*     */   {
/* 406 */     X509Extensions exts = this.c.getTBSCertificate().getExtensions();
/*     */     
/* 408 */     if (exts != null)
/*     */     {
/* 410 */       X509Extension ext = exts.getExtension(new DERObjectIdentifier(oid));
/*     */       
/* 412 */       if (ext != null)
/*     */       {
/* 414 */         ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 415 */         DEROutputStream dOut = new DEROutputStream(bOut);
/*     */         
/*     */         try
/*     */         {
/* 419 */           dOut.writeObject(ext.getValue());
/*     */           
/* 421 */           return bOut.toByteArray();
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 425 */           throw new RuntimeException("error encoding " + e.toString());
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 430 */     return null;
/*     */   }
/*     */   
/*     */   public Set getNonCriticalExtensionOIDs()
/*     */   {
/* 435 */     if (getVersion() == 3)
/*     */     {
/* 437 */       HashSet set = new HashSet();
/* 438 */       X509Extensions extensions = this.c.getTBSCertificate().getExtensions();
/*     */       
/* 440 */       if (extensions != null)
/*     */       {
/* 442 */         Enumeration e = extensions.oids();
/*     */         
/* 444 */         while (e.hasMoreElements())
/*     */         {
/* 446 */           DERObjectIdentifier oid = (DERObjectIdentifier)e.nextElement();
/* 447 */           X509Extension ext = extensions.getExtension(oid);
/*     */           
/* 449 */           if (!ext.isCritical())
/*     */           {
/* 451 */             set.add(oid.getId());
/*     */           }
/*     */         }
/*     */         
/* 455 */         return set;
/*     */       }
/*     */     }
/*     */     
/* 459 */     return null;
/*     */   }
/*     */   
/*     */   public boolean hasUnsupportedCriticalExtension()
/*     */   {
/* 464 */     if (getVersion() == 3)
/*     */     {
/* 466 */       X509Extensions extensions = this.c.getTBSCertificate().getExtensions();
/*     */       
/* 468 */       if (extensions != null)
/*     */       {
/* 470 */         Enumeration e = extensions.oids();
/*     */         
/* 472 */         while (e.hasMoreElements())
/*     */         {
/* 474 */           DERObjectIdentifier oid = (DERObjectIdentifier)e.nextElement();
/* 475 */           if ((!oid.getId().equals("2.5.29.15")) && (!oid.getId().equals("2.5.29.19")))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 481 */             X509Extension ext = extensions.getExtension(oid);
/*     */             
/* 483 */             if (ext.isCritical())
/*     */             {
/* 485 */               return true;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 491 */     return false;
/*     */   }
/*     */   
/*     */   public PublicKey getPublicKey()
/*     */   {
/* 496 */     return JDKKeyFactory.createPublicKeyFromPublicKeyInfo(this.c.getSubjectPublicKeyInfo());
/*     */   }
/*     */   
/*     */   public byte[] getEncoded()
/*     */     throws CertificateEncodingException
/*     */   {
/* 502 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 503 */     DEROutputStream dOut = new DEROutputStream(bOut);
/*     */     
/*     */     try
/*     */     {
/* 507 */       dOut.writeObject(this.c);
/*     */       
/* 509 */       return bOut.toByteArray();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 513 */       throw new CertificateEncodingException(e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setBagAttribute(DERObjectIdentifier oid, DEREncodable attribute)
/*     */   {
/* 521 */     this.pkcs12Attributes.put(oid, attribute);
/* 522 */     this.pkcs12Ordering.addElement(oid);
/*     */   }
/*     */   
/*     */ 
/*     */   public DEREncodable getBagAttribute(DERObjectIdentifier oid)
/*     */   {
/* 528 */     return (DEREncodable)this.pkcs12Attributes.get(oid);
/*     */   }
/*     */   
/*     */   public Enumeration getBagAttributeKeys()
/*     */   {
/* 533 */     return this.pkcs12Ordering.elements();
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 538 */     StringBuilder buf = new StringBuilder();
/* 539 */     String nl = System.getProperty("line.separator");
/*     */     
/* 541 */     buf.append("  [0]         Version: ").append(getVersion()).append(nl);
/* 542 */     buf.append("         SerialNumber: ").append(getSerialNumber()).append(nl);
/* 543 */     buf.append("             IssuerDN: ").append(getIssuerDN()).append(nl);
/* 544 */     buf.append("           Start Date: ").append(getNotBefore()).append(nl);
/* 545 */     buf.append("           Final Date: ").append(getNotAfter()).append(nl);
/* 546 */     buf.append("            SubjectDN: ").append(getSubjectDN()).append(nl);
/* 547 */     buf.append("           Public Key: ").append(getPublicKey()).append(nl);
/* 548 */     buf.append("  Signature Algorithm: ").append(getSigAlgName()).append(nl);
/*     */     
/* 550 */     byte[] sig = getSignature();
/*     */     
/* 552 */     buf.append("            Signature: ").append(new String(Hex.encode(sig, 0, 20))).append(nl);
/* 553 */     for (int i = 20; i < sig.length; i += 20)
/*     */     {
/* 555 */       if (i < sig.length - 20)
/*     */       {
/* 557 */         buf.append("                       ").append(new String(Hex.encode(sig, i, 20))).append(nl);
/*     */       }
/*     */       else
/*     */       {
/* 561 */         buf.append("                       ").append(new String(Hex.encode(sig, i, sig.length - i))).append(nl);
/*     */       }
/*     */     }
/*     */     
/* 565 */     X509Extensions extensions = this.c.getTBSCertificate().getExtensions();
/*     */     
/* 567 */     if (extensions != null)
/*     */     {
/* 569 */       Enumeration e = extensions.oids();
/*     */       
/* 571 */       if (e.hasMoreElements())
/*     */       {
/* 573 */         buf.append("       Extensions: \n");
/*     */       }
/*     */       
/* 576 */       while (e.hasMoreElements())
/*     */       {
/* 578 */         DERObjectIdentifier oid = (DERObjectIdentifier)e.nextElement();
/* 579 */         X509Extension ext = extensions.getExtension(oid);
/*     */         
/* 581 */         if (ext.getValue() != null)
/*     */         {
/* 583 */           byte[] octs = ext.getValue().getOctets();
/* 584 */           ByteArrayInputStream bIn = new ByteArrayInputStream(octs);
/* 585 */           DERInputStream dIn = new DERInputStream(bIn);
/* 586 */           buf.append("                       critical(").append(ext.isCritical()).append(") ");
/*     */           try
/*     */           {
/* 589 */             if (oid.equals(X509Extensions.BasicConstraints))
/*     */             {
/* 591 */               buf.append(new org.gudy.bouncycastle.asn1.x509.BasicConstraints((ASN1Sequence)dIn.readObject())).append(nl);
/*     */             }
/* 593 */             else if (oid.equals(X509Extensions.KeyUsage))
/*     */             {
/* 595 */               buf.append(new org.gudy.bouncycastle.asn1.x509.KeyUsage((DERBitString)dIn.readObject())).append(nl);
/*     */             }
/* 597 */             else if (oid.equals(MiscObjectIdentifiers.netscapeCertType))
/*     */             {
/* 599 */               buf.append(new org.gudy.bouncycastle.asn1.misc.NetscapeCertType((DERBitString)dIn.readObject())).append(nl);
/*     */             }
/* 601 */             else if (oid.equals(MiscObjectIdentifiers.netscapeRevocationURL))
/*     */             {
/* 603 */               buf.append(new org.gudy.bouncycastle.asn1.misc.NetscapeRevocationURL((DERIA5String)dIn.readObject())).append(nl);
/*     */             }
/* 605 */             else if (oid.equals(MiscObjectIdentifiers.verisignCzagExtension))
/*     */             {
/* 607 */               buf.append(new org.gudy.bouncycastle.asn1.misc.VerisignCzagExtension((DERIA5String)dIn.readObject())).append(nl);
/*     */             }
/*     */             else
/*     */             {
/* 611 */               buf.append(oid.getId());
/* 612 */               buf.append(" value = ").append(org.gudy.bouncycastle.asn1.util.ASN1Dump.dumpAsString(dIn.readObject())).append(nl);
/*     */             }
/*     */             
/*     */           }
/*     */           catch (Exception ex)
/*     */           {
/* 618 */             buf.append(oid.getId());
/*     */             
/* 620 */             buf.append(" value = *****").append(nl);
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 625 */           buf.append(nl);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 630 */     return buf.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final void verify(PublicKey key)
/*     */     throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
/*     */   {
/* 638 */     Signature signature = null;
/*     */     
/* 640 */     if (!this.c.getSignatureAlgorithm().equals(this.c.getTBSCertificate().getSignature()))
/*     */     {
/* 642 */       throw new CertificateException("signature algorithm in TBS cert not same as outer cert");
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 647 */       signature = Signature.getInstance(this.c.getSignatureAlgorithm().getObjectId().getId(), BouncyCastleProvider.PROVIDER_NAME);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 651 */       signature = Signature.getInstance(this.c.getSignatureAlgorithm().getObjectId().getId());
/*     */     }
/*     */     
/* 654 */     signature.initVerify(key);
/*     */     
/* 656 */     signature.update(getTBSCertificate());
/*     */     
/* 658 */     if (!signature.verify(getSignature()))
/*     */     {
/* 660 */       throw new InvalidKeyException("Public key presented not for certificate signature");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void verify(PublicKey key, String sigProvider)
/*     */     throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
/*     */   {
/* 670 */     Signature signature = Signature.getInstance(this.c.getSignatureAlgorithm().getObjectId().getId(), sigProvider);
/*     */     
/* 672 */     signature.initVerify(key);
/*     */     
/* 674 */     signature.update(getTBSCertificate());
/*     */     
/* 676 */     if (!signature.verify(getSignature()))
/*     */     {
/* 678 */       throw new InvalidKeyException("Public key presented not for certificate signature");
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/X509CertificateObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */