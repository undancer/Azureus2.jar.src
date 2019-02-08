/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DEREncodable;
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
/*     */ public class UserNotice
/*     */   extends ASN1Encodable
/*     */ {
/*     */   private NoticeReference noticeRef;
/*     */   private DisplayText explicitText;
/*     */   
/*     */   public UserNotice(NoticeReference noticeRef, DisplayText explicitText)
/*     */   {
/*  40 */     this.noticeRef = noticeRef;
/*  41 */     this.explicitText = explicitText;
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
/*     */   public UserNotice(NoticeReference noticeRef, String str)
/*     */   {
/*  54 */     this.noticeRef = noticeRef;
/*  55 */     this.explicitText = new DisplayText(str);
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
/*     */   public UserNotice(ASN1Sequence as)
/*     */   {
/*  70 */     if (as.size() == 2)
/*     */     {
/*  72 */       this.noticeRef = NoticeReference.getInstance(as.getObjectAt(0));
/*  73 */       this.explicitText = DisplayText.getInstance(as.getObjectAt(1));
/*     */     }
/*  75 */     else if (as.size() == 1)
/*     */     {
/*  77 */       if ((as.getObjectAt(0).getDERObject() instanceof ASN1Sequence))
/*     */       {
/*  79 */         this.noticeRef = NoticeReference.getInstance(as.getObjectAt(0));
/*     */       }
/*     */       else
/*     */       {
/*  83 */         this.explicitText = DisplayText.getInstance(as.getObjectAt(0));
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  88 */       throw new IllegalArgumentException("Bad sequence size: " + as.size());
/*     */     }
/*     */   }
/*     */   
/*     */   public NoticeReference getNoticeRef()
/*     */   {
/*  94 */     return this.noticeRef;
/*     */   }
/*     */   
/*     */   public DisplayText getExplicitText()
/*     */   {
/*  99 */     return this.explicitText;
/*     */   }
/*     */   
/*     */   public DERObject toASN1Object()
/*     */   {
/* 104 */     ASN1EncodableVector av = new ASN1EncodableVector();
/*     */     
/* 106 */     if (this.noticeRef != null)
/*     */     {
/* 108 */       av.add(this.noticeRef);
/*     */     }
/*     */     
/* 111 */     if (this.explicitText != null)
/*     */     {
/* 113 */       av.add(this.explicitText);
/*     */     }
/*     */     
/* 116 */     return new DERSequence(av);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/UserNotice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */