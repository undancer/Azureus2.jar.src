/*     */ package org.gudy.bouncycastle.asn1.pkcs;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.util.Enumeration;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ 
/*     */ 
/*     */ public class RSAPrivateKeyStructure
/*     */   extends ASN1Encodable
/*     */ {
/*     */   private int version;
/*     */   private BigInteger modulus;
/*     */   private BigInteger publicExponent;
/*     */   private BigInteger privateExponent;
/*     */   private BigInteger prime1;
/*     */   private BigInteger prime2;
/*     */   private BigInteger exponent1;
/*     */   private BigInteger exponent2;
/*     */   private BigInteger coefficient;
/*  26 */   private ASN1Sequence otherPrimeInfos = null;
/*     */   
/*     */ 
/*     */ 
/*     */   public static RSAPrivateKeyStructure getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  32 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public static RSAPrivateKeyStructure getInstance(Object obj)
/*     */   {
/*  38 */     if ((obj instanceof RSAPrivateKeyStructure))
/*     */     {
/*  40 */       return (RSAPrivateKeyStructure)obj;
/*     */     }
/*  42 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  44 */       return new RSAPrivateKeyStructure((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  47 */     throw new IllegalArgumentException("unknown object in factory");
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
/*     */   public RSAPrivateKeyStructure(BigInteger modulus, BigInteger publicExponent, BigInteger privateExponent, BigInteger prime1, BigInteger prime2, BigInteger exponent1, BigInteger exponent2, BigInteger coefficient)
/*     */   {
/*  60 */     this.version = 0;
/*  61 */     this.modulus = modulus;
/*  62 */     this.publicExponent = publicExponent;
/*  63 */     this.privateExponent = privateExponent;
/*  64 */     this.prime1 = prime1;
/*  65 */     this.prime2 = prime2;
/*  66 */     this.exponent1 = exponent1;
/*  67 */     this.exponent2 = exponent2;
/*  68 */     this.coefficient = coefficient;
/*     */   }
/*     */   
/*     */ 
/*     */   public RSAPrivateKeyStructure(ASN1Sequence seq)
/*     */   {
/*  74 */     Enumeration e = seq.getObjects();
/*     */     
/*  76 */     BigInteger v = ((DERInteger)e.nextElement()).getValue();
/*  77 */     if ((v.intValue() != 0) && (v.intValue() != 1))
/*     */     {
/*  79 */       throw new IllegalArgumentException("wrong version for RSA private key");
/*     */     }
/*     */     
/*  82 */     this.version = v.intValue();
/*  83 */     this.modulus = ((DERInteger)e.nextElement()).getValue();
/*  84 */     this.publicExponent = ((DERInteger)e.nextElement()).getValue();
/*  85 */     this.privateExponent = ((DERInteger)e.nextElement()).getValue();
/*  86 */     this.prime1 = ((DERInteger)e.nextElement()).getValue();
/*  87 */     this.prime2 = ((DERInteger)e.nextElement()).getValue();
/*  88 */     this.exponent1 = ((DERInteger)e.nextElement()).getValue();
/*  89 */     this.exponent2 = ((DERInteger)e.nextElement()).getValue();
/*  90 */     this.coefficient = ((DERInteger)e.nextElement()).getValue();
/*     */     
/*  92 */     if (e.hasMoreElements())
/*     */     {
/*  94 */       this.otherPrimeInfos = ((ASN1Sequence)e.nextElement());
/*     */     }
/*     */   }
/*     */   
/*     */   public int getVersion()
/*     */   {
/* 100 */     return this.version;
/*     */   }
/*     */   
/*     */   public BigInteger getModulus()
/*     */   {
/* 105 */     return this.modulus;
/*     */   }
/*     */   
/*     */   public BigInteger getPublicExponent()
/*     */   {
/* 110 */     return this.publicExponent;
/*     */   }
/*     */   
/*     */   public BigInteger getPrivateExponent()
/*     */   {
/* 115 */     return this.privateExponent;
/*     */   }
/*     */   
/*     */   public BigInteger getPrime1()
/*     */   {
/* 120 */     return this.prime1;
/*     */   }
/*     */   
/*     */   public BigInteger getPrime2()
/*     */   {
/* 125 */     return this.prime2;
/*     */   }
/*     */   
/*     */   public BigInteger getExponent1()
/*     */   {
/* 130 */     return this.exponent1;
/*     */   }
/*     */   
/*     */   public BigInteger getExponent2()
/*     */   {
/* 135 */     return this.exponent2;
/*     */   }
/*     */   
/*     */   public BigInteger getCoefficient()
/*     */   {
/* 140 */     return this.coefficient;
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
/*     */ 
/*     */ 
/*     */   public DERObject toASN1Object()
/*     */   {
/* 167 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 169 */     v.add(new DERInteger(this.version));
/* 170 */     v.add(new DERInteger(getModulus()));
/* 171 */     v.add(new DERInteger(getPublicExponent()));
/* 172 */     v.add(new DERInteger(getPrivateExponent()));
/* 173 */     v.add(new DERInteger(getPrime1()));
/* 174 */     v.add(new DERInteger(getPrime2()));
/* 175 */     v.add(new DERInteger(getExponent1()));
/* 176 */     v.add(new DERInteger(getExponent2()));
/* 177 */     v.add(new DERInteger(getCoefficient()));
/*     */     
/* 179 */     if (this.otherPrimeInfos != null)
/*     */     {
/* 181 */       v.add(this.otherPrimeInfos);
/*     */     }
/*     */     
/* 184 */     return new DERSequence(v);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/RSAPrivateKeyStructure.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */