/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExtendedKeyUsage
/*     */   extends ASN1Encodable
/*     */ {
/*  27 */   Hashtable usageTable = new Hashtable();
/*     */   
/*     */   ASN1Sequence seq;
/*     */   
/*     */ 
/*     */   public static ExtendedKeyUsage getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  34 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public static ExtendedKeyUsage getInstance(Object obj)
/*     */   {
/*  40 */     if ((obj instanceof ExtendedKeyUsage))
/*     */     {
/*  42 */       return (ExtendedKeyUsage)obj;
/*     */     }
/*     */     
/*  45 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  47 */       return new ExtendedKeyUsage((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  50 */     if ((obj instanceof X509Extension))
/*     */     {
/*  52 */       return getInstance(X509Extension.convertValueToObject((X509Extension)obj));
/*     */     }
/*     */     
/*  55 */     throw new IllegalArgumentException("Invalid ExtendedKeyUsage: " + obj.getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */   public ExtendedKeyUsage(KeyPurposeId usage)
/*     */   {
/*  61 */     this.seq = new DERSequence(usage);
/*     */     
/*  63 */     this.usageTable.put(usage, usage);
/*     */   }
/*     */   
/*     */ 
/*     */   public ExtendedKeyUsage(ASN1Sequence seq)
/*     */   {
/*  69 */     this.seq = seq;
/*     */     
/*  71 */     Enumeration e = seq.getObjects();
/*     */     
/*  73 */     while (e.hasMoreElements())
/*     */     {
/*  75 */       Object o = e.nextElement();
/*  76 */       if (!(o instanceof DERObjectIdentifier))
/*     */       {
/*  78 */         throw new IllegalArgumentException("Only DERObjectIdentifiers allowed in ExtendedKeyUsage.");
/*     */       }
/*  80 */       this.usageTable.put(o, o);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public ExtendedKeyUsage(Vector usages)
/*     */   {
/*  87 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*  88 */     Enumeration e = usages.elements();
/*     */     
/*  90 */     while (e.hasMoreElements())
/*     */     {
/*  92 */       DERObject o = (DERObject)e.nextElement();
/*     */       
/*  94 */       v.add(o);
/*  95 */       this.usageTable.put(o, o);
/*     */     }
/*     */     
/*  98 */     this.seq = new DERSequence(v);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasKeyPurposeId(KeyPurposeId keyPurposeId)
/*     */   {
/* 104 */     return this.usageTable.get(keyPurposeId) != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Vector getUsages()
/*     */   {
/* 114 */     Vector temp = new Vector();
/* 115 */     for (Enumeration it = this.usageTable.elements(); it.hasMoreElements();)
/*     */     {
/* 117 */       temp.addElement(it.nextElement());
/*     */     }
/* 119 */     return temp;
/*     */   }
/*     */   
/*     */   public int size()
/*     */   {
/* 124 */     return this.usageTable.size();
/*     */   }
/*     */   
/*     */   public DERObject toASN1Object()
/*     */   {
/* 129 */     return this.seq;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/ExtendedKeyUsage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */