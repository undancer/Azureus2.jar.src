/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import java.util.Hashtable;
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
/*    */ 
/*    */ 
/*    */ public class PolicyMappings
/*    */   extends ASN1Encodable
/*    */ {
/* 26 */   ASN1Sequence seq = null;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public PolicyMappings(ASN1Sequence seq)
/*    */   {
/* 36 */     this.seq = seq;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public PolicyMappings(Hashtable mappings)
/*    */   {
/* 48 */     ASN1EncodableVector dev = new ASN1EncodableVector();
/* 49 */     Enumeration it = mappings.keys();
/*    */     
/* 51 */     while (it.hasMoreElements())
/*    */     {
/* 53 */       String idp = (String)it.nextElement();
/* 54 */       String sdp = (String)mappings.get(idp);
/* 55 */       ASN1EncodableVector dv = new ASN1EncodableVector();
/* 56 */       dv.add(new DERObjectIdentifier(idp));
/* 57 */       dv.add(new DERObjectIdentifier(sdp));
/* 58 */       dev.add(new DERSequence(dv));
/*    */     }
/*    */     
/* 61 */     this.seq = new DERSequence(dev);
/*    */   }
/*    */   
/*    */   public DERObject toASN1Object()
/*    */   {
/* 66 */     return this.seq;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/PolicyMappings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */