/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IssuerSerial
/*     */   extends ASN1Encodable
/*     */ {
/*     */   GeneralNames issuer;
/*     */   DERInteger serial;
/*     */   DERBitString issuerUID;
/*     */   
/*     */   public static IssuerSerial getInstance(Object obj)
/*     */   {
/*  24 */     if ((obj == null) || ((obj instanceof IssuerSerial)))
/*     */     {
/*  26 */       return (IssuerSerial)obj;
/*     */     }
/*     */     
/*  29 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  31 */       return new IssuerSerial((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  34 */     throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static IssuerSerial getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  41 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public IssuerSerial(ASN1Sequence seq)
/*     */   {
/*  47 */     if ((seq.size() != 2) && (seq.size() != 3))
/*     */     {
/*  49 */       throw new IllegalArgumentException("Bad sequence size: " + seq.size());
/*     */     }
/*     */     
/*  52 */     this.issuer = GeneralNames.getInstance(seq.getObjectAt(0));
/*  53 */     this.serial = DERInteger.getInstance(seq.getObjectAt(1));
/*     */     
/*  55 */     if (seq.size() == 3)
/*     */     {
/*  57 */       this.issuerUID = DERBitString.getInstance(seq.getObjectAt(2));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public IssuerSerial(GeneralNames issuer, DERInteger serial)
/*     */   {
/*  65 */     this.issuer = issuer;
/*  66 */     this.serial = serial;
/*     */   }
/*     */   
/*     */   public GeneralNames getIssuer()
/*     */   {
/*  71 */     return this.issuer;
/*     */   }
/*     */   
/*     */   public DERInteger getSerial()
/*     */   {
/*  76 */     return this.serial;
/*     */   }
/*     */   
/*     */   public DERBitString getIssuerUID()
/*     */   {
/*  81 */     return this.issuerUID;
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
/*     */   public DERObject toASN1Object()
/*     */   {
/*  96 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/*  98 */     v.add(this.issuer);
/*  99 */     v.add(this.serial);
/*     */     
/* 101 */     if (this.issuerUID != null)
/*     */     {
/* 103 */       v.add(this.issuerUID);
/*     */     }
/*     */     
/* 106 */     return new DERSequence(v);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/IssuerSerial.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */