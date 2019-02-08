/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
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
/*     */ public class SubjectDirectoryAttributes
/*     */   extends ASN1Encodable
/*     */ {
/*  34 */   private Vector attributes = new Vector();
/*     */   
/*     */ 
/*     */   public static SubjectDirectoryAttributes getInstance(Object obj)
/*     */   {
/*  39 */     if ((obj == null) || ((obj instanceof SubjectDirectoryAttributes)))
/*     */     {
/*  41 */       return (SubjectDirectoryAttributes)obj;
/*     */     }
/*     */     
/*  44 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  46 */       return new SubjectDirectoryAttributes((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  49 */     throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
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
/*     */   public SubjectDirectoryAttributes(ASN1Sequence seq)
/*     */   {
/*  75 */     Enumeration e = seq.getObjects();
/*     */     
/*  77 */     while (e.hasMoreElements())
/*     */     {
/*  79 */       ASN1Sequence s = ASN1Sequence.getInstance(e.nextElement());
/*  80 */       this.attributes.addElement(new Attribute(s));
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
/*     */   public SubjectDirectoryAttributes(Vector attributes)
/*     */   {
/*  95 */     Enumeration e = attributes.elements();
/*     */     
/*  97 */     while (e.hasMoreElements())
/*     */     {
/*  99 */       this.attributes.addElement(e.nextElement());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERObject toASN1Object()
/*     */   {
/* 125 */     ASN1EncodableVector vec = new ASN1EncodableVector();
/* 126 */     Enumeration e = this.attributes.elements();
/*     */     
/* 128 */     while (e.hasMoreElements())
/*     */     {
/*     */ 
/* 131 */       vec.add((Attribute)e.nextElement());
/*     */     }
/*     */     
/* 134 */     return new DERSequence(vec);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Vector getAttributes()
/*     */   {
/* 142 */     return this.attributes;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/SubjectDirectoryAttributes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */