/*     */ package org.gudy.bouncycastle.asn1.x9;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.math.ec.ECCurve;
/*     */ import org.gudy.bouncycastle.math.ec.ECCurve.Fp;
/*     */ import org.gudy.bouncycastle.math.ec.ECPoint;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class X9ECParameters
/*     */   implements DEREncodable, X9ObjectIdentifiers
/*     */ {
/*  22 */   private static BigInteger ONE = BigInteger.valueOf(1L);
/*     */   
/*     */   private X9FieldID fieldID;
/*     */   
/*     */   private ECCurve curve;
/*     */   private ECPoint g;
/*     */   private BigInteger n;
/*     */   private BigInteger h;
/*     */   private byte[] seed;
/*     */   
/*     */   public X9ECParameters(ASN1Sequence seq)
/*     */   {
/*  34 */     if ((!(seq.getObjectAt(0) instanceof DERInteger)) || (!((DERInteger)seq.getObjectAt(0)).getValue().equals(ONE)))
/*     */     {
/*     */ 
/*  37 */       throw new IllegalArgumentException("bad version in X9ECParameters");
/*     */     }
/*     */     
/*  40 */     X9Curve x9c = new X9Curve(new X9FieldID((ASN1Sequence)seq.getObjectAt(1)), (ASN1Sequence)seq.getObjectAt(2));
/*     */     
/*     */ 
/*     */ 
/*  44 */     this.curve = x9c.getCurve();
/*  45 */     this.g = new X9ECPoint(this.curve, (ASN1OctetString)seq.getObjectAt(3)).getPoint();
/*  46 */     this.n = ((DERInteger)seq.getObjectAt(4)).getValue();
/*  47 */     this.seed = x9c.getSeed();
/*     */     
/*  49 */     if (seq.size() == 6)
/*     */     {
/*  51 */       this.h = ((DERInteger)seq.getObjectAt(5)).getValue();
/*     */     }
/*     */     else
/*     */     {
/*  55 */       this.h = ONE;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public X9ECParameters(ECCurve curve, ECPoint g, BigInteger n)
/*     */   {
/*  64 */     this(curve, g, n, ONE, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X9ECParameters(ECCurve curve, ECPoint g, BigInteger n, BigInteger h)
/*     */   {
/*  73 */     this(curve, g, n, h, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X9ECParameters(ECCurve curve, ECPoint g, BigInteger n, BigInteger h, byte[] seed)
/*     */   {
/*  83 */     this.curve = curve;
/*  84 */     this.g = g;
/*  85 */     this.n = n;
/*  86 */     this.h = h;
/*  87 */     this.seed = seed;
/*     */     
/*  89 */     if ((curve instanceof ECCurve.Fp))
/*     */     {
/*  91 */       this.fieldID = new X9FieldID(prime_field, ((ECCurve.Fp)curve).getQ());
/*     */     }
/*     */     else
/*     */     {
/*  95 */       this.fieldID = new X9FieldID(characteristic_two_field, null);
/*     */     }
/*     */   }
/*     */   
/*     */   public ECCurve getCurve()
/*     */   {
/* 101 */     return this.curve;
/*     */   }
/*     */   
/*     */   public ECPoint getG()
/*     */   {
/* 106 */     return this.g;
/*     */   }
/*     */   
/*     */   public BigInteger getN()
/*     */   {
/* 111 */     return this.n;
/*     */   }
/*     */   
/*     */   public BigInteger getH()
/*     */   {
/* 116 */     return this.h;
/*     */   }
/*     */   
/*     */   public byte[] getSeed()
/*     */   {
/* 121 */     return this.seed;
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
/*     */   public DERObject getDERObject()
/*     */   {
/* 139 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 141 */     v.add(new DERInteger(1));
/* 142 */     v.add(this.fieldID);
/* 143 */     v.add(new X9Curve(this.curve, this.seed));
/* 144 */     v.add(new X9ECPoint(this.g));
/* 145 */     v.add(new DERInteger(this.n));
/*     */     
/* 147 */     if (!this.h.equals(BigInteger.valueOf(1L)))
/*     */     {
/* 149 */       v.add(new DERInteger(this.h));
/*     */     }
/*     */     
/* 152 */     return new DERSequence(v);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x9/X9ECParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */