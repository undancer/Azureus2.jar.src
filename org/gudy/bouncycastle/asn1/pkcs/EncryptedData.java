/*     */ package org.gudy.bouncycastle.asn1.pkcs;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.BERSequence;
/*     */ import org.gudy.bouncycastle.asn1.BERTaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
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
/*     */ public class EncryptedData
/*     */   implements DEREncodable
/*     */ {
/*     */   ASN1Sequence data;
/*     */   DERObjectIdentifier bagId;
/*     */   DERObject bagValue;
/*     */   
/*     */   public static EncryptedData getInstance(Object obj)
/*     */   {
/*  34 */     if ((obj instanceof EncryptedData))
/*     */     {
/*  36 */       return (EncryptedData)obj;
/*     */     }
/*  38 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  40 */       return new EncryptedData((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  43 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */   public EncryptedData(ASN1Sequence seq)
/*     */   {
/*  49 */     int version = ((DERInteger)seq.getObjectAt(0)).getValue().intValue();
/*     */     
/*  51 */     if (version != 0)
/*     */     {
/*  53 */       throw new IllegalArgumentException("sequence not version 0");
/*     */     }
/*     */     
/*  56 */     this.data = ((ASN1Sequence)seq.getObjectAt(1));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public EncryptedData(DERObjectIdentifier contentType, AlgorithmIdentifier encryptionAlgorithm, DEREncodable content)
/*     */   {
/*  64 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/*  66 */     v.add(contentType);
/*  67 */     v.add(encryptionAlgorithm.getDERObject());
/*  68 */     v.add(new BERTaggedObject(false, 0, content));
/*     */     
/*  70 */     this.data = new BERSequence(v);
/*     */   }
/*     */   
/*     */   public DERObjectIdentifier getContentType()
/*     */   {
/*  75 */     return (DERObjectIdentifier)this.data.getObjectAt(0);
/*     */   }
/*     */   
/*     */   public AlgorithmIdentifier getEncryptionAlgorithm()
/*     */   {
/*  80 */     return AlgorithmIdentifier.getInstance(this.data.getObjectAt(1));
/*     */   }
/*     */   
/*     */   public ASN1OctetString getContent()
/*     */   {
/*  85 */     if (this.data.size() == 3)
/*     */     {
/*  87 */       DERTaggedObject o = (DERTaggedObject)this.data.getObjectAt(2);
/*     */       
/*  89 */       return ASN1OctetString.getInstance(o.getObject());
/*     */     }
/*     */     
/*  92 */     return null;
/*     */   }
/*     */   
/*     */   public DERObject getDERObject()
/*     */   {
/*  97 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/*  99 */     v.add(new DERInteger(0));
/* 100 */     v.add(this.data);
/*     */     
/* 102 */     return new BERSequence(v);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/EncryptedData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */