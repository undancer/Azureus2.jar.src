/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERBoolean;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class X509Extensions
/*     */   extends ASN1Encodable
/*     */ {
/*  25 */   public static final DERObjectIdentifier SubjectDirectoryAttributes = new DERObjectIdentifier("2.5.29.9");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  30 */   public static final DERObjectIdentifier SubjectKeyIdentifier = new DERObjectIdentifier("2.5.29.14");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  35 */   public static final DERObjectIdentifier KeyUsage = new DERObjectIdentifier("2.5.29.15");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  40 */   public static final DERObjectIdentifier PrivateKeyUsagePeriod = new DERObjectIdentifier("2.5.29.16");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  45 */   public static final DERObjectIdentifier SubjectAlternativeName = new DERObjectIdentifier("2.5.29.17");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  50 */   public static final DERObjectIdentifier IssuerAlternativeName = new DERObjectIdentifier("2.5.29.18");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  55 */   public static final DERObjectIdentifier BasicConstraints = new DERObjectIdentifier("2.5.29.19");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  60 */   public static final DERObjectIdentifier CRLNumber = new DERObjectIdentifier("2.5.29.20");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  65 */   public static final DERObjectIdentifier ReasonCode = new DERObjectIdentifier("2.5.29.21");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  70 */   public static final DERObjectIdentifier InstructionCode = new DERObjectIdentifier("2.5.29.23");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  75 */   public static final DERObjectIdentifier InvalidityDate = new DERObjectIdentifier("2.5.29.24");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  80 */   public static final DERObjectIdentifier DeltaCRLIndicator = new DERObjectIdentifier("2.5.29.27");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  85 */   public static final DERObjectIdentifier IssuingDistributionPoint = new DERObjectIdentifier("2.5.29.28");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  90 */   public static final DERObjectIdentifier CertificateIssuer = new DERObjectIdentifier("2.5.29.29");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  95 */   public static final DERObjectIdentifier NameConstraints = new DERObjectIdentifier("2.5.29.30");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 100 */   public static final DERObjectIdentifier CRLDistributionPoints = new DERObjectIdentifier("2.5.29.31");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 105 */   public static final DERObjectIdentifier CertificatePolicies = new DERObjectIdentifier("2.5.29.32");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 110 */   public static final DERObjectIdentifier PolicyMappings = new DERObjectIdentifier("2.5.29.33");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 115 */   public static final DERObjectIdentifier AuthorityKeyIdentifier = new DERObjectIdentifier("2.5.29.35");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 120 */   public static final DERObjectIdentifier PolicyConstraints = new DERObjectIdentifier("2.5.29.36");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 125 */   public static final DERObjectIdentifier ExtendedKeyUsage = new DERObjectIdentifier("2.5.29.37");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 130 */   public static final DERObjectIdentifier FreshestCRL = new DERObjectIdentifier("2.5.29.46");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 135 */   public static final DERObjectIdentifier InhibitAnyPolicy = new DERObjectIdentifier("2.5.29.54");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 140 */   public static final DERObjectIdentifier AuthorityInfoAccess = new DERObjectIdentifier("1.3.6.1.5.5.7.1.1");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 145 */   public static final DERObjectIdentifier SubjectInfoAccess = new DERObjectIdentifier("1.3.6.1.5.5.7.1.11");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 150 */   public static final DERObjectIdentifier LogoType = new DERObjectIdentifier("1.3.6.1.5.5.7.1.12");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 155 */   public static final DERObjectIdentifier BiometricInfo = new DERObjectIdentifier("1.3.6.1.5.5.7.1.2");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 160 */   public static final DERObjectIdentifier QCStatements = new DERObjectIdentifier("1.3.6.1.5.5.7.1.3");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 165 */   public static final DERObjectIdentifier AuditIdentity = new DERObjectIdentifier("1.3.6.1.5.5.7.1.4");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 170 */   public static final DERObjectIdentifier NoRevAvail = new DERObjectIdentifier("2.5.29.56");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 175 */   public static final DERObjectIdentifier TargetInformation = new DERObjectIdentifier("2.5.29.55");
/*     */   
/* 177 */   private Hashtable extensions = new Hashtable();
/* 178 */   private Vector ordering = new Vector();
/*     */   
/*     */ 
/*     */ 
/*     */   public static X509Extensions getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/* 184 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public static X509Extensions getInstance(Object obj)
/*     */   {
/* 190 */     if ((obj == null) || ((obj instanceof X509Extensions)))
/*     */     {
/* 192 */       return (X509Extensions)obj;
/*     */     }
/*     */     
/* 195 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/* 197 */       return new X509Extensions((ASN1Sequence)obj);
/*     */     }
/*     */     
/* 200 */     if ((obj instanceof ASN1TaggedObject))
/*     */     {
/* 202 */       return getInstance(((ASN1TaggedObject)obj).getObject());
/*     */     }
/*     */     
/* 205 */     throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Extensions(ASN1Sequence seq)
/*     */   {
/* 216 */     Enumeration e = seq.getObjects();
/*     */     
/* 218 */     while (e.hasMoreElements())
/*     */     {
/* 220 */       ASN1Sequence s = ASN1Sequence.getInstance(e.nextElement());
/*     */       
/* 222 */       if (s.size() == 3)
/*     */       {
/* 224 */         this.extensions.put(s.getObjectAt(0), new X509Extension(DERBoolean.getInstance(s.getObjectAt(1)), ASN1OctetString.getInstance(s.getObjectAt(2))));
/*     */       }
/* 226 */       else if (s.size() == 2)
/*     */       {
/* 228 */         this.extensions.put(s.getObjectAt(0), new X509Extension(false, ASN1OctetString.getInstance(s.getObjectAt(1))));
/*     */       }
/*     */       else
/*     */       {
/* 232 */         throw new IllegalArgumentException("Bad sequence size: " + s.size());
/*     */       }
/*     */       
/* 235 */       this.ordering.addElement(s.getObjectAt(0));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Extensions(Hashtable extensions)
/*     */   {
/* 247 */     this(null, extensions);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Extensions(Vector ordering, Hashtable extensions)
/*     */   {
/*     */     Enumeration e;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 261 */     if (ordering == null)
/*     */     {
/* 263 */       e = extensions.keys();
/*     */     }
/*     */     else
/*     */     {
/* 267 */       e = ordering.elements();
/*     */     }
/*     */     
/* 270 */     while (e.hasMoreElements())
/*     */     {
/* 272 */       this.ordering.addElement(e.nextElement());
/*     */     }
/*     */     
/* 275 */     Enumeration e = this.ordering.elements();
/*     */     
/* 277 */     while (e.hasMoreElements())
/*     */     {
/* 279 */       DERObjectIdentifier oid = (DERObjectIdentifier)e.nextElement();
/* 280 */       X509Extension ext = (X509Extension)extensions.get(oid);
/*     */       
/* 282 */       this.extensions.put(oid, ext);
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
/*     */   public X509Extensions(Vector objectIDs, Vector values)
/*     */   {
/* 296 */     Enumeration e = objectIDs.elements();
/*     */     
/* 298 */     while (e.hasMoreElements())
/*     */     {
/* 300 */       this.ordering.addElement(e.nextElement());
/*     */     }
/*     */     
/* 303 */     int count = 0;
/*     */     
/* 305 */     e = this.ordering.elements();
/*     */     
/* 307 */     while (e.hasMoreElements())
/*     */     {
/* 309 */       DERObjectIdentifier oid = (DERObjectIdentifier)e.nextElement();
/* 310 */       X509Extension ext = (X509Extension)values.elementAt(count);
/*     */       
/* 312 */       this.extensions.put(oid, ext);
/* 313 */       count++;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration oids()
/*     */   {
/* 322 */     return this.ordering.elements();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Extension getExtension(DERObjectIdentifier oid)
/*     */   {
/* 334 */     return (X509Extension)this.extensions.get(oid);
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
/*     */   public DERObject toASN1Object()
/*     */   {
/* 349 */     ASN1EncodableVector vec = new ASN1EncodableVector();
/* 350 */     Enumeration e = this.ordering.elements();
/*     */     
/* 352 */     while (e.hasMoreElements())
/*     */     {
/* 354 */       DERObjectIdentifier oid = (DERObjectIdentifier)e.nextElement();
/* 355 */       X509Extension ext = (X509Extension)this.extensions.get(oid);
/* 356 */       ASN1EncodableVector v = new ASN1EncodableVector();
/*     */       
/* 358 */       v.add(oid);
/*     */       
/* 360 */       if (ext.isCritical())
/*     */       {
/* 362 */         v.add(new DERBoolean(true));
/*     */       }
/*     */       
/* 365 */       v.add(ext.getValue());
/*     */       
/* 367 */       vec.add(new DERSequence(v));
/*     */     }
/*     */     
/* 370 */     return new DERSequence(vec);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equivalent(X509Extensions other)
/*     */   {
/* 376 */     if (this.extensions.size() != other.extensions.size())
/*     */     {
/* 378 */       return false;
/*     */     }
/*     */     
/* 381 */     Enumeration e1 = this.extensions.keys();
/*     */     
/* 383 */     while (e1.hasMoreElements())
/*     */     {
/* 385 */       Object key = e1.nextElement();
/*     */       
/* 387 */       if (!this.extensions.get(key).equals(other.extensions.get(key)))
/*     */       {
/* 389 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 393 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/X509Extensions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */