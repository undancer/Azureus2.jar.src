/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DEROctetString;
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
/*    */ 
/*    */ 
/*    */ public class DigestInfo
/*    */   extends ASN1Encodable
/*    */ {
/*    */   private byte[] digest;
/*    */   private AlgorithmIdentifier algId;
/*    */   
/*    */   public static DigestInfo getInstance(ASN1TaggedObject obj, boolean explicit)
/*    */   {
/* 34 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*    */   }
/*    */   
/*    */ 
/*    */   public static DigestInfo getInstance(Object obj)
/*    */   {
/* 40 */     if ((obj instanceof DigestInfo))
/*    */     {
/* 42 */       return (DigestInfo)obj;
/*    */     }
/* 44 */     if ((obj instanceof ASN1Sequence))
/*    */     {
/* 46 */       return new DigestInfo((ASN1Sequence)obj);
/*    */     }
/*    */     
/* 49 */     throw new IllegalArgumentException("unknown object in factory");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public DigestInfo(AlgorithmIdentifier algId, byte[] digest)
/*    */   {
/* 56 */     this.digest = digest;
/* 57 */     this.algId = algId;
/*    */   }
/*    */   
/*    */ 
/*    */   public DigestInfo(ASN1Sequence obj)
/*    */   {
/* 63 */     Enumeration e = obj.getObjects();
/*    */     
/* 65 */     this.algId = AlgorithmIdentifier.getInstance(e.nextElement());
/* 66 */     this.digest = ASN1OctetString.getInstance(e.nextElement()).getOctets();
/*    */   }
/*    */   
/*    */   public AlgorithmIdentifier getAlgorithmId()
/*    */   {
/* 71 */     return this.algId;
/*    */   }
/*    */   
/*    */   public byte[] getDigest()
/*    */   {
/* 76 */     return this.digest;
/*    */   }
/*    */   
/*    */   public DERObject toASN1Object()
/*    */   {
/* 81 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 83 */     v.add(this.algId);
/* 84 */     v.add(new DEROctetString(this.digest));
/*    */     
/* 86 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/DigestInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */