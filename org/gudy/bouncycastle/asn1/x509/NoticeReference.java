/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NoticeReference
/*     */   extends ASN1Encodable
/*     */ {
/*     */   private DisplayText organization;
/*     */   private ASN1Sequence noticeNumbers;
/*     */   
/*     */   public NoticeReference(String orgName, Vector numbers)
/*     */   {
/*  46 */     this.organization = new DisplayText(orgName);
/*     */     
/*  48 */     Object o = numbers.elementAt(0);
/*     */     
/*  50 */     ASN1EncodableVector av = new ASN1EncodableVector();
/*  51 */     if ((o instanceof Integer))
/*     */     {
/*  53 */       Enumeration it = numbers.elements();
/*     */       
/*  55 */       while (it.hasMoreElements())
/*     */       {
/*  57 */         Integer nm = (Integer)it.nextElement();
/*  58 */         DERInteger di = new DERInteger(nm.intValue());
/*  59 */         av.add(di);
/*     */       }
/*     */     }
/*     */     
/*  63 */     this.noticeNumbers = new DERSequence(av);
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
/*     */   public NoticeReference(String orgName, ASN1Sequence numbers)
/*     */   {
/*  76 */     this.organization = new DisplayText(orgName);
/*  77 */     this.noticeNumbers = numbers;
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
/*     */   public NoticeReference(int displayTextType, String orgName, ASN1Sequence numbers)
/*     */   {
/*  92 */     this.organization = new DisplayText(displayTextType, orgName);
/*     */     
/*  94 */     this.noticeNumbers = numbers;
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
/*     */   public NoticeReference(ASN1Sequence as)
/*     */   {
/* 109 */     if (as.size() != 2)
/*     */     {
/* 111 */       throw new IllegalArgumentException("Bad sequence size: " + as.size());
/*     */     }
/*     */     
/*     */ 
/* 115 */     this.organization = DisplayText.getInstance(as.getObjectAt(0));
/* 116 */     this.noticeNumbers = ASN1Sequence.getInstance(as.getObjectAt(1));
/*     */   }
/*     */   
/*     */ 
/*     */   public static NoticeReference getInstance(Object as)
/*     */   {
/* 122 */     if ((as instanceof NoticeReference))
/*     */     {
/* 124 */       return (NoticeReference)as;
/*     */     }
/* 126 */     if ((as instanceof ASN1Sequence))
/*     */     {
/* 128 */       return new NoticeReference((ASN1Sequence)as);
/*     */     }
/*     */     
/* 131 */     throw new IllegalArgumentException("unknown object in getInstance.");
/*     */   }
/*     */   
/*     */   public DisplayText getOrganization()
/*     */   {
/* 136 */     return this.organization;
/*     */   }
/*     */   
/*     */   public ASN1Sequence getNoticeNumbers()
/*     */   {
/* 141 */     return this.noticeNumbers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERObject toASN1Object()
/*     */   {
/* 151 */     ASN1EncodableVector av = new ASN1EncodableVector();
/* 152 */     av.add(this.organization);
/* 153 */     av.add(this.noticeNumbers);
/* 154 */     return new DERSequence(av);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/NoticeReference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */