/*    */ package org.gudy.bouncycastle.asn1.pkcs;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.BERSequence;
/*    */ import org.gudy.bouncycastle.asn1.BERTaggedObject;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*    */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ContentInfo
/*    */   implements DEREncodable, PKCSObjectIdentifiers
/*    */ {
/*    */   private DERObjectIdentifier contentType;
/*    */   private DEREncodable content;
/*    */   
/*    */   public static ContentInfo getInstance(Object obj)
/*    */   {
/* 23 */     if ((obj instanceof ContentInfo))
/*    */     {
/* 25 */       return (ContentInfo)obj;
/*    */     }
/* 27 */     if ((obj instanceof ASN1Sequence))
/*    */     {
/* 29 */       return new ContentInfo((ASN1Sequence)obj);
/*    */     }
/*    */     
/* 32 */     throw new IllegalArgumentException("unknown object in factory");
/*    */   }
/*    */   
/*    */ 
/*    */   public ContentInfo(ASN1Sequence seq)
/*    */   {
/* 38 */     Enumeration e = seq.getObjects();
/*    */     
/* 40 */     this.contentType = ((DERObjectIdentifier)e.nextElement());
/*    */     
/* 42 */     if (e.hasMoreElements())
/*    */     {
/* 44 */       this.content = ((DERTaggedObject)e.nextElement()).getObject();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ContentInfo(DERObjectIdentifier contentType, DEREncodable content)
/*    */   {
/* 52 */     this.contentType = contentType;
/* 53 */     this.content = content;
/*    */   }
/*    */   
/*    */   public DERObjectIdentifier getContentType()
/*    */   {
/* 58 */     return this.contentType;
/*    */   }
/*    */   
/*    */   public DEREncodable getContent()
/*    */   {
/* 63 */     return this.content;
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
/*    */   public DERObject getDERObject()
/*    */   {
/* 77 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 79 */     v.add(this.contentType);
/*    */     
/* 81 */     if (this.content != null)
/*    */     {
/* 83 */       v.add(new BERTaggedObject(0, this.content));
/*    */     }
/*    */     
/* 86 */     return new BERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/ContentInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */