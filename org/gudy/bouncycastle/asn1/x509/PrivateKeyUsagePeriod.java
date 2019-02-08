/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*    */ import org.gudy.bouncycastle.asn1.DERGeneralizedTime;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PrivateKeyUsagePeriod
/*    */   extends ASN1Encodable
/*    */ {
/*    */   private DERGeneralizedTime _notBefore;
/*    */   private DERGeneralizedTime _notAfter;
/*    */   
/*    */   public static PrivateKeyUsagePeriod getInstance(Object obj)
/*    */   {
/* 27 */     if ((obj instanceof PrivateKeyUsagePeriod))
/*    */     {
/* 29 */       return (PrivateKeyUsagePeriod)obj;
/*    */     }
/*    */     
/* 32 */     if ((obj instanceof ASN1Sequence))
/*    */     {
/* 34 */       return new PrivateKeyUsagePeriod((ASN1Sequence)obj);
/*    */     }
/*    */     
/* 37 */     if ((obj instanceof X509Extension))
/*    */     {
/* 39 */       return getInstance(X509Extension.convertValueToObject((X509Extension)obj));
/*    */     }
/*    */     
/* 42 */     throw new IllegalArgumentException("unknown object in getInstance");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   private PrivateKeyUsagePeriod(ASN1Sequence seq)
/*    */   {
/* 49 */     Enumeration en = seq.getObjects();
/* 50 */     while (en.hasMoreElements())
/*    */     {
/* 52 */       ASN1TaggedObject tObj = (ASN1TaggedObject)en.nextElement();
/*    */       
/* 54 */       if (tObj.getTagNo() == 0)
/*    */       {
/* 56 */         this._notBefore = DERGeneralizedTime.getInstance(tObj, false);
/*    */       }
/* 58 */       else if (tObj.getTagNo() == 1)
/*    */       {
/* 60 */         this._notAfter = DERGeneralizedTime.getInstance(tObj, false);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public DERGeneralizedTime getNotBefore()
/*    */   {
/* 67 */     return this._notBefore;
/*    */   }
/*    */   
/*    */   public DERGeneralizedTime getNotAfter()
/*    */   {
/* 72 */     return this._notAfter;
/*    */   }
/*    */   
/*    */   public DERObject toASN1Object()
/*    */   {
/* 77 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 79 */     if (this._notBefore != null)
/*    */     {
/* 81 */       v.add(new DERTaggedObject(false, 0, this._notBefore));
/*    */     }
/* 83 */     if (this._notAfter != null)
/*    */     {
/* 85 */       v.add(new DERTaggedObject(false, 1, this._notAfter));
/*    */     }
/*    */     
/* 88 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/PrivateKeyUsagePeriod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */