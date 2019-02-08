/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*     */ 
/*     */ 
/*     */ public class NameConstraints
/*     */   extends ASN1Encodable
/*     */ {
/*     */   private ASN1Sequence permitted;
/*     */   private ASN1Sequence excluded;
/*     */   
/*     */   public NameConstraints(ASN1Sequence seq)
/*     */   {
/*  22 */     Enumeration e = seq.getObjects();
/*  23 */     while (e.hasMoreElements())
/*     */     {
/*  25 */       ASN1TaggedObject o = ASN1TaggedObject.getInstance(e.nextElement());
/*  26 */       switch (o.getTagNo())
/*     */       {
/*     */       case 0: 
/*  29 */         this.permitted = ASN1Sequence.getInstance(o, false);
/*  30 */         break;
/*     */       case 1: 
/*  32 */         this.excluded = ASN1Sequence.getInstance(o, false);
/*     */       }
/*     */       
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public NameConstraints(Vector permitted, Vector excluded)
/*     */   {
/*  53 */     if (permitted != null)
/*     */     {
/*  55 */       this.permitted = createSequence(permitted);
/*     */     }
/*  57 */     if (excluded != null)
/*     */     {
/*  59 */       this.excluded = createSequence(excluded);
/*     */     }
/*     */   }
/*     */   
/*     */   private DERSequence createSequence(Vector subtree)
/*     */   {
/*  65 */     ASN1EncodableVector vec = new ASN1EncodableVector();
/*  66 */     Enumeration e = subtree.elements();
/*  67 */     while (e.hasMoreElements())
/*     */     {
/*  69 */       vec.add((GeneralSubtree)e.nextElement());
/*     */     }
/*     */     
/*  72 */     return new DERSequence(vec);
/*     */   }
/*     */   
/*     */   public ASN1Sequence getPermittedSubtrees()
/*     */   {
/*  77 */     return this.permitted;
/*     */   }
/*     */   
/*     */   public ASN1Sequence getExcludedSubtrees()
/*     */   {
/*  82 */     return this.excluded;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERObject toASN1Object()
/*     */   {
/*  91 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/*  93 */     if (this.permitted != null)
/*     */     {
/*  95 */       v.add(new DERTaggedObject(false, 0, this.permitted));
/*     */     }
/*     */     
/*  98 */     if (this.excluded != null)
/*     */     {
/* 100 */       v.add(new DERTaggedObject(false, 1, this.excluded));
/*     */     }
/*     */     
/* 103 */     return new DERSequence(v);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/NameConstraints.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */