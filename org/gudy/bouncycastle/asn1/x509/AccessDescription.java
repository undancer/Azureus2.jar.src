/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
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
/*    */ public class AccessDescription
/*    */   extends ASN1Encodable
/*    */ {
/* 22 */   public static final DERObjectIdentifier id_ad_caIssuers = new DERObjectIdentifier("1.3.6.1.5.5.7.48.2");
/*    */   
/* 24 */   public static final DERObjectIdentifier id_ad_ocsp = new DERObjectIdentifier("1.3.6.1.5.5.7.48.1");
/*    */   
/* 26 */   DERObjectIdentifier accessMethod = null;
/* 27 */   GeneralName accessLocation = null;
/*    */   
/*    */ 
/*    */   public static AccessDescription getInstance(Object obj)
/*    */   {
/* 32 */     if ((obj instanceof AccessDescription))
/*    */     {
/* 34 */       return (AccessDescription)obj;
/*    */     }
/* 36 */     if ((obj instanceof ASN1Sequence))
/*    */     {
/* 38 */       return new AccessDescription((ASN1Sequence)obj);
/*    */     }
/*    */     
/* 41 */     throw new IllegalArgumentException("unknown object in factory");
/*    */   }
/*    */   
/*    */ 
/*    */   public AccessDescription(ASN1Sequence seq)
/*    */   {
/* 47 */     if (seq.size() != 2)
/*    */     {
/* 49 */       throw new IllegalArgumentException("wrong number of elements in inner sequence");
/*    */     }
/*    */     
/* 52 */     this.accessMethod = DERObjectIdentifier.getInstance(seq.getObjectAt(0));
/* 53 */     this.accessLocation = GeneralName.getInstance(seq.getObjectAt(1));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public AccessDescription(DERObjectIdentifier oid, GeneralName location)
/*    */   {
/* 63 */     this.accessMethod = oid;
/* 64 */     this.accessLocation = location;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public DERObjectIdentifier getAccessMethod()
/*    */   {
/* 73 */     return this.accessMethod;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public GeneralName getAccessLocation()
/*    */   {
/* 82 */     return this.accessLocation;
/*    */   }
/*    */   
/*    */   public DERObject toASN1Object()
/*    */   {
/* 87 */     ASN1EncodableVector accessDescription = new ASN1EncodableVector();
/*    */     
/* 89 */     accessDescription.add(this.accessMethod);
/* 90 */     accessDescription.add(this.accessLocation);
/*    */     
/* 92 */     return new DERSequence(accessDescription);
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 97 */     return "AccessDescription: Oid(" + this.accessMethod.getId() + ")";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/AccessDescription.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */