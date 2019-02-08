/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.ASN1Choice;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AttCertIssuer
/*    */   extends ASN1Encodable
/*    */   implements ASN1Choice
/*    */ {
/*    */   ASN1Encodable obj;
/*    */   DERObject choiceObj;
/*    */   
/*    */   public static AttCertIssuer getInstance(Object obj)
/*    */   {
/* 23 */     if ((obj instanceof AttCertIssuer))
/*    */     {
/* 25 */       return (AttCertIssuer)obj;
/*    */     }
/* 27 */     if ((obj instanceof V2Form))
/*    */     {
/* 29 */       return new AttCertIssuer(V2Form.getInstance(obj));
/*    */     }
/* 31 */     if ((obj instanceof GeneralNames))
/*    */     {
/* 33 */       return new AttCertIssuer((GeneralNames)obj);
/*    */     }
/* 35 */     if ((obj instanceof ASN1TaggedObject))
/*    */     {
/* 37 */       return new AttCertIssuer(V2Form.getInstance((ASN1TaggedObject)obj, false));
/*    */     }
/* 39 */     if ((obj instanceof ASN1Sequence))
/*    */     {
/* 41 */       return new AttCertIssuer(GeneralNames.getInstance(obj));
/*    */     }
/*    */     
/* 44 */     throw new IllegalArgumentException("unknown object in factory: " + obj.getClass());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static AttCertIssuer getInstance(ASN1TaggedObject obj, boolean explicit)
/*    */   {
/* 51 */     return getInstance(obj.getObject());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public AttCertIssuer(GeneralNames names)
/*    */   {
/* 63 */     this.obj = names;
/* 64 */     this.choiceObj = this.obj.getDERObject();
/*    */   }
/*    */   
/*    */ 
/*    */   public AttCertIssuer(V2Form v2Form)
/*    */   {
/* 70 */     this.obj = v2Form;
/* 71 */     this.choiceObj = new DERTaggedObject(false, 0, this.obj);
/*    */   }
/*    */   
/*    */   public ASN1Encodable getIssuer()
/*    */   {
/* 76 */     return this.obj;
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
/*    */ 
/*    */   public DERObject toASN1Object()
/*    */   {
/* 91 */     return this.choiceObj;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/AttCertIssuer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */