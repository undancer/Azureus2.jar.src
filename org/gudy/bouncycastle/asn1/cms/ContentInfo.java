/*    */ package org.gudy.bouncycastle.asn1.cms;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*    */ import org.gudy.bouncycastle.asn1.BERSequence;
/*    */ import org.gudy.bouncycastle.asn1.BERTaggedObject;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ContentInfo
/*    */   extends ASN1Encodable
/*    */   implements CMSObjectIdentifiers
/*    */ {
/*    */   private DERObjectIdentifier contentType;
/*    */   private DEREncodable content;
/*    */   
/*    */   public static ContentInfo getInstance(Object obj)
/*    */   {
/* 25 */     if ((obj instanceof ContentInfo))
/*    */     {
/* 27 */       return (ContentInfo)obj;
/*    */     }
/* 29 */     if ((obj instanceof ASN1Sequence))
/*    */     {
/* 31 */       return new ContentInfo((ASN1Sequence)obj);
/*    */     }
/*    */     
/* 34 */     throw new IllegalArgumentException("unknown object in factory: " + obj.getClass().getName());
/*    */   }
/*    */   
/*    */ 
/*    */   public ContentInfo(ASN1Sequence seq)
/*    */   {
/* 40 */     Enumeration e = seq.getObjects();
/*    */     
/* 42 */     this.contentType = ((DERObjectIdentifier)e.nextElement());
/*    */     
/* 44 */     if (e.hasMoreElements())
/*    */     {
/* 46 */       this.content = ((ASN1TaggedObject)e.nextElement()).getObject();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ContentInfo(DERObjectIdentifier contentType, DEREncodable content)
/*    */   {
/* 54 */     this.contentType = contentType;
/* 55 */     this.content = content;
/*    */   }
/*    */   
/*    */   public DERObjectIdentifier getContentType()
/*    */   {
/* 60 */     return this.contentType;
/*    */   }
/*    */   
/*    */   public DEREncodable getContent()
/*    */   {
/* 65 */     return this.content;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public DERObject toASN1Object()
/*    */   {
/* 79 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 81 */     v.add(this.contentType);
/*    */     
/* 83 */     if (this.content != null)
/*    */     {
/* 85 */       v.add(new BERTaggedObject(0, this.content));
/*    */     }
/*    */     
/* 88 */     return new BERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/cms/ContentInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */