/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
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
/*     */ public class GeneralSubtree
/*     */   extends ASN1Encodable
/*     */ {
/*  36 */   private static final BigInteger ZERO = BigInteger.valueOf(0L);
/*     */   
/*     */   private GeneralName base;
/*     */   
/*     */   private DERInteger minimum;
/*     */   
/*     */   private DERInteger maximum;
/*     */   
/*     */ 
/*     */   public GeneralSubtree(ASN1Sequence seq)
/*     */   {
/*  47 */     this.base = GeneralName.getInstance(seq.getObjectAt(0));
/*     */     
/*  49 */     switch (seq.size())
/*     */     {
/*     */     case 1: 
/*     */       break;
/*     */     case 2: 
/*  54 */       ASN1TaggedObject o = ASN1TaggedObject.getInstance(seq.getObjectAt(1));
/*  55 */       switch (o.getTagNo())
/*     */       {
/*     */       case 0: 
/*  58 */         this.minimum = DERInteger.getInstance(o, false);
/*  59 */         break;
/*     */       case 1: 
/*  61 */         this.maximum = DERInteger.getInstance(o, false);
/*  62 */         break;
/*     */       default: 
/*  64 */         throw new IllegalArgumentException("Bad tag number: " + o.getTagNo());
/*     */       }
/*     */       
/*     */       break;
/*     */     case 3: 
/*  69 */       this.minimum = DERInteger.getInstance(ASN1TaggedObject.getInstance(seq.getObjectAt(1)));
/*  70 */       this.maximum = DERInteger.getInstance(ASN1TaggedObject.getInstance(seq.getObjectAt(2)));
/*  71 */       break;
/*     */     default: 
/*  73 */       throw new IllegalArgumentException("Bad sequence size: " + seq.size());
/*     */     }
/*     */     
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
/*     */   public GeneralSubtree(GeneralName base, BigInteger minimum, BigInteger maximum)
/*     */   {
/* 100 */     this.base = base;
/* 101 */     if (maximum != null)
/*     */     {
/* 103 */       this.maximum = new DERInteger(maximum);
/*     */     }
/* 105 */     if (minimum == null)
/*     */     {
/* 107 */       this.minimum = null;
/*     */     }
/*     */     else
/*     */     {
/* 111 */       this.minimum = new DERInteger(minimum);
/*     */     }
/*     */   }
/*     */   
/*     */   public GeneralSubtree(GeneralName base)
/*     */   {
/* 117 */     this(base, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static GeneralSubtree getInstance(ASN1TaggedObject o, boolean explicit)
/*     */   {
/* 124 */     return new GeneralSubtree(ASN1Sequence.getInstance(o, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public static GeneralSubtree getInstance(Object obj)
/*     */   {
/* 130 */     if (obj == null)
/*     */     {
/* 132 */       return null;
/*     */     }
/*     */     
/* 135 */     if ((obj instanceof GeneralSubtree))
/*     */     {
/* 137 */       return (GeneralSubtree)obj;
/*     */     }
/*     */     
/* 140 */     return new GeneralSubtree(ASN1Sequence.getInstance(obj));
/*     */   }
/*     */   
/*     */   public GeneralName getBase()
/*     */   {
/* 145 */     return this.base;
/*     */   }
/*     */   
/*     */   public BigInteger getMinimum()
/*     */   {
/* 150 */     if (this.minimum == null)
/*     */     {
/* 152 */       return ZERO;
/*     */     }
/*     */     
/* 155 */     return this.minimum.getValue();
/*     */   }
/*     */   
/*     */   public BigInteger getMaximum()
/*     */   {
/* 160 */     if (this.maximum == null)
/*     */     {
/* 162 */       return null;
/*     */     }
/*     */     
/* 165 */     return this.maximum.getValue();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERObject toASN1Object()
/*     */   {
/* 186 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 188 */     v.add(this.base);
/*     */     
/* 190 */     if ((this.minimum != null) && (!this.minimum.getValue().equals(ZERO)))
/*     */     {
/* 192 */       v.add(new DERTaggedObject(false, 0, this.minimum));
/*     */     }
/*     */     
/* 195 */     if (this.maximum != null)
/*     */     {
/* 197 */       v.add(new DERTaggedObject(false, 1, this.maximum));
/*     */     }
/*     */     
/* 200 */     return new DERSequence(v);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/GeneralSubtree.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */