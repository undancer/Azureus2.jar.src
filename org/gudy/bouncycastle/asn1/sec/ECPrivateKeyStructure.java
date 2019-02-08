/*     */ package org.gudy.bouncycastle.asn1.sec;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.util.Enumeration;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Object;
/*     */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DEROctetString;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ECPrivateKeyStructure
/*     */   extends ASN1Encodable
/*     */ {
/*     */   private ASN1Sequence seq;
/*     */   
/*     */   public ECPrivateKeyStructure(ASN1Sequence seq)
/*     */   {
/*  30 */     this.seq = seq;
/*     */   }
/*     */   
/*     */ 
/*     */   public ECPrivateKeyStructure(BigInteger key)
/*     */   {
/*  36 */     byte[] bytes = key.toByteArray();
/*     */     
/*  38 */     if (bytes[0] == 0)
/*     */     {
/*  40 */       byte[] tmp = new byte[bytes.length - 1];
/*     */       
/*  42 */       System.arraycopy(bytes, 1, tmp, 0, tmp.length);
/*  43 */       bytes = tmp;
/*     */     }
/*     */     
/*  46 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/*  48 */     v.add(new DERInteger(1));
/*  49 */     v.add(new DEROctetString(bytes));
/*     */     
/*  51 */     this.seq = new DERSequence(v);
/*     */   }
/*     */   
/*     */   public BigInteger getKey()
/*     */   {
/*  56 */     ASN1OctetString octs = (ASN1OctetString)this.seq.getObjectAt(1);
/*     */     
/*  58 */     return new BigInteger(1, octs.getOctets());
/*     */   }
/*     */   
/*     */   public DERBitString getPublicKey()
/*     */   {
/*  63 */     return (DERBitString)getObjectInTag(1);
/*     */   }
/*     */   
/*     */   public ASN1Object getParameters()
/*     */   {
/*  68 */     return getObjectInTag(0);
/*     */   }
/*     */   
/*     */   private ASN1Object getObjectInTag(int tagNo)
/*     */   {
/*  73 */     Enumeration e = this.seq.getObjects();
/*     */     
/*  75 */     while (e.hasMoreElements())
/*     */     {
/*  77 */       DEREncodable obj = (DEREncodable)e.nextElement();
/*     */       
/*  79 */       if ((obj instanceof ASN1TaggedObject))
/*     */       {
/*  81 */         ASN1TaggedObject tag = (ASN1TaggedObject)obj;
/*  82 */         if (tag.getTagNo() == tagNo)
/*     */         {
/*  84 */           return (ASN1Object)tag.getObject().getDERObject();
/*     */         }
/*     */       }
/*     */     }
/*  88 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERObject toASN1Object()
/*     */   {
/* 100 */     return this.seq;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/sec/ECPrivateKeyStructure.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */