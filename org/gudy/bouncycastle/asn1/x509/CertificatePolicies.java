/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CertificatePolicies
/*     */   extends ASN1Encodable
/*     */ {
/*  18 */   static final DERObjectIdentifier anyPolicy = new DERObjectIdentifier("2.5.29.32.0");
/*     */   
/*  20 */   Vector policies = new Vector();
/*     */   
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static CertificatePolicies getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  29 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static CertificatePolicies getInstance(Object obj)
/*     */   {
/*  38 */     if ((obj instanceof CertificatePolicies))
/*     */     {
/*  40 */       return (CertificatePolicies)obj;
/*     */     }
/*  42 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  44 */       return new CertificatePolicies((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  47 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public CertificatePolicies(ASN1Sequence seq)
/*     */   {
/*  56 */     Enumeration e = seq.getObjects();
/*  57 */     while (e.hasMoreElements())
/*     */     {
/*  59 */       ASN1Sequence s = ASN1Sequence.getInstance(e.nextElement());
/*  60 */       this.policies.addElement(s.getObjectAt(0));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public CertificatePolicies(DERObjectIdentifier p)
/*     */   {
/*  72 */     this.policies.addElement(p);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public CertificatePolicies(String p)
/*     */   {
/*  83 */     this(new DERObjectIdentifier(p));
/*     */   }
/*     */   
/*     */ 
/*     */   public void addPolicy(String p)
/*     */   {
/*  89 */     this.policies.addElement(new DERObjectIdentifier(p));
/*     */   }
/*     */   
/*     */   public String getPolicy(int nr)
/*     */   {
/*  94 */     if (this.policies.size() > nr)
/*     */     {
/*  96 */       return ((DERObjectIdentifier)this.policies.elementAt(nr)).getId();
/*     */     }
/*     */     
/*  99 */     return null;
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
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public DERObject toASN1Object()
/*     */   {
/* 124 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/*     */ 
/* 127 */     for (int i = 0; i < this.policies.size(); i++)
/*     */     {
/* 129 */       v.add(new DERSequence((DERObjectIdentifier)this.policies.elementAt(i)));
/*     */     }
/*     */     
/* 132 */     return new DERSequence(v);
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 137 */     String p = null;
/* 138 */     for (int i = 0; i < this.policies.size(); i++)
/*     */     {
/* 140 */       if (p != null)
/*     */       {
/* 142 */         p = p + ", ";
/*     */       }
/* 144 */       p = p + ((DERObjectIdentifier)this.policies.elementAt(i)).getId();
/*     */     }
/* 146 */     return "CertificatePolicies: " + p;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/CertificatePolicies.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */