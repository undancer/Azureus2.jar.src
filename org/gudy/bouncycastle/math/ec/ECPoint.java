/*     */ package org.gudy.bouncycastle.math.ec;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import org.gudy.bouncycastle.asn1.x9.X9IntegerConverter;
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
/*     */ public abstract class ECPoint
/*     */ {
/*     */   ECCurve curve;
/*     */   ECFieldElement x;
/*     */   ECFieldElement y;
/*     */   protected boolean withCompression;
/*  22 */   protected ECMultiplier multiplier = null;
/*     */   
/*  24 */   protected PreCompInfo preCompInfo = null;
/*     */   
/*  26 */   private static X9IntegerConverter converter = new X9IntegerConverter();
/*     */   
/*     */   protected ECPoint(ECCurve curve, ECFieldElement x, ECFieldElement y)
/*     */   {
/*  30 */     this.curve = curve;
/*  31 */     this.x = x;
/*  32 */     this.y = y;
/*     */   }
/*     */   
/*     */   public ECCurve getCurve()
/*     */   {
/*  37 */     return this.curve;
/*     */   }
/*     */   
/*     */   public ECFieldElement getX()
/*     */   {
/*  42 */     return this.x;
/*     */   }
/*     */   
/*     */   public ECFieldElement getY()
/*     */   {
/*  47 */     return this.y;
/*     */   }
/*     */   
/*     */   public boolean isInfinity()
/*     */   {
/*  52 */     return (this.x == null) && (this.y == null);
/*     */   }
/*     */   
/*     */   public boolean isCompressed()
/*     */   {
/*  57 */     return this.withCompression;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/*  63 */     if (other == this)
/*     */     {
/*  65 */       return true;
/*     */     }
/*     */     
/*  68 */     if (!(other instanceof ECPoint))
/*     */     {
/*  70 */       return false;
/*     */     }
/*     */     
/*  73 */     ECPoint o = (ECPoint)other;
/*     */     
/*  75 */     if (isInfinity())
/*     */     {
/*  77 */       return o.isInfinity();
/*     */     }
/*     */     
/*  80 */     return (this.x.equals(o.x)) && (this.y.equals(o.y));
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/*  85 */     if (isInfinity())
/*     */     {
/*  87 */       return 0;
/*     */     }
/*     */     
/*  90 */     return this.x.hashCode() ^ this.y.hashCode();
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
/*     */   void setPreCompInfo(PreCompInfo preCompInfo)
/*     */   {
/* 112 */     this.preCompInfo = preCompInfo;
/*     */   }
/*     */   
/*     */   public abstract byte[] getEncoded();
/*     */   
/*     */   public abstract ECPoint add(ECPoint paramECPoint);
/*     */   
/*     */   public abstract ECPoint subtract(ECPoint paramECPoint);
/*     */   
/*     */   public abstract ECPoint negate();
/*     */   
/*     */   public abstract ECPoint twice();
/*     */   
/*     */   synchronized void assertECMultiplier()
/*     */   {
/* 127 */     if (this.multiplier == null)
/*     */     {
/* 129 */       this.multiplier = new FpNafMultiplier();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ECPoint multiply(BigInteger k)
/*     */   {
/* 140 */     if (isInfinity())
/*     */     {
/* 142 */       return this;
/*     */     }
/*     */     
/* 145 */     if (k.signum() == 0)
/*     */     {
/* 147 */       return this.curve.getInfinity();
/*     */     }
/*     */     
/* 150 */     assertECMultiplier();
/* 151 */     return this.multiplier.multiply(this, k, this.preCompInfo);
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
/*     */   public static class Fp
/*     */     extends ECPoint
/*     */   {
/*     */     public Fp(ECCurve curve, ECFieldElement x, ECFieldElement y)
/*     */     {
/* 169 */       this(curve, x, y, false);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Fp(ECCurve curve, ECFieldElement x, ECFieldElement y, boolean withCompression)
/*     */     {
/* 182 */       super(x, y);
/*     */       
/* 184 */       if (((x != null) && (y == null)) || ((x == null) && (y != null)))
/*     */       {
/* 186 */         throw new IllegalArgumentException("Exactly one of the field elements is null");
/*     */       }
/*     */       
/* 189 */       this.withCompression = withCompression;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public byte[] getEncoded()
/*     */     {
/* 197 */       if (isInfinity())
/*     */       {
/* 199 */         return new byte[1];
/*     */       }
/*     */       
/* 202 */       int qLength = ECPoint.converter.getByteLength(this.x);
/*     */       
/* 204 */       if (this.withCompression)
/*     */       {
/*     */         byte PC;
/*     */         byte PC;
/* 208 */         if (getY().toBigInteger().testBit(0))
/*     */         {
/* 210 */           PC = 3;
/*     */         }
/*     */         else
/*     */         {
/* 214 */           PC = 2;
/*     */         }
/*     */         
/* 217 */         byte[] X = ECPoint.converter.integerToBytes(getX().toBigInteger(), qLength);
/* 218 */         byte[] PO = new byte[X.length + 1];
/*     */         
/* 220 */         PO[0] = PC;
/* 221 */         System.arraycopy(X, 0, PO, 1, X.length);
/*     */         
/* 223 */         return PO;
/*     */       }
/*     */       
/*     */ 
/* 227 */       byte[] X = ECPoint.converter.integerToBytes(getX().toBigInteger(), qLength);
/* 228 */       byte[] Y = ECPoint.converter.integerToBytes(getY().toBigInteger(), qLength);
/* 229 */       byte[] PO = new byte[X.length + Y.length + 1];
/*     */       
/* 231 */       PO[0] = 4;
/* 232 */       System.arraycopy(X, 0, PO, 1, X.length);
/* 233 */       System.arraycopy(Y, 0, PO, X.length + 1, Y.length);
/*     */       
/* 235 */       return PO;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public ECPoint add(ECPoint b)
/*     */     {
/* 242 */       if (isInfinity())
/*     */       {
/* 244 */         return b;
/*     */       }
/*     */       
/* 247 */       if (b.isInfinity())
/*     */       {
/* 249 */         return this;
/*     */       }
/*     */       
/*     */ 
/* 253 */       if (this.x.equals(b.x))
/*     */       {
/* 255 */         if (this.y.equals(b.y))
/*     */         {
/*     */ 
/* 258 */           return twice();
/*     */         }
/*     */         
/*     */ 
/* 262 */         return this.curve.getInfinity();
/*     */       }
/*     */       
/* 265 */       ECFieldElement gamma = b.y.subtract(this.y).divide(b.x.subtract(this.x));
/*     */       
/* 267 */       ECFieldElement x3 = gamma.square().subtract(this.x).subtract(b.x);
/* 268 */       ECFieldElement y3 = gamma.multiply(this.x.subtract(x3)).subtract(this.y);
/*     */       
/* 270 */       return new Fp(this.curve, x3, y3);
/*     */     }
/*     */     
/*     */ 
/*     */     public ECPoint twice()
/*     */     {
/* 276 */       if (isInfinity())
/*     */       {
/*     */ 
/* 279 */         return this;
/*     */       }
/*     */       
/* 282 */       if (this.y.toBigInteger().signum() == 0)
/*     */       {
/*     */ 
/*     */ 
/* 286 */         return this.curve.getInfinity();
/*     */       }
/*     */       
/* 289 */       ECFieldElement TWO = this.curve.fromBigInteger(BigInteger.valueOf(2L));
/* 290 */       ECFieldElement THREE = this.curve.fromBigInteger(BigInteger.valueOf(3L));
/* 291 */       ECFieldElement gamma = this.x.square().multiply(THREE).add(this.curve.a).divide(this.y.multiply(TWO));
/*     */       
/* 293 */       ECFieldElement x3 = gamma.square().subtract(this.x.multiply(TWO));
/* 294 */       ECFieldElement y3 = gamma.multiply(this.x.subtract(x3)).subtract(this.y);
/*     */       
/* 296 */       return new Fp(this.curve, x3, y3, this.withCompression);
/*     */     }
/*     */     
/*     */ 
/*     */     public ECPoint subtract(ECPoint b)
/*     */     {
/* 302 */       if (b.isInfinity())
/*     */       {
/* 304 */         return this;
/*     */       }
/*     */       
/*     */ 
/* 308 */       return add(b.negate());
/*     */     }
/*     */     
/*     */     public ECPoint negate()
/*     */     {
/* 313 */       return new Fp(this.curve, this.x, this.y.negate(), this.withCompression);
/*     */     }
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
/*     */   public static class F2m
/*     */     extends ECPoint
/*     */   {
/*     */     public F2m(ECCurve curve, ECFieldElement x, ECFieldElement y)
/*     */     {
/* 341 */       this(curve, x, y, false);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public F2m(ECCurve curve, ECFieldElement x, ECFieldElement y, boolean withCompression)
/*     */     {
/* 352 */       super(x, y);
/*     */       
/* 354 */       if (((x != null) && (y == null)) || ((x == null) && (y != null)))
/*     */       {
/* 356 */         throw new IllegalArgumentException("Exactly one of the field elements is null");
/*     */       }
/*     */       
/* 359 */       if (x != null)
/*     */       {
/*     */ 
/* 362 */         ECFieldElement.F2m.checkFieldElements(this.x, this.y);
/*     */         
/*     */ 
/* 365 */         if (curve != null)
/*     */         {
/* 367 */           ECFieldElement.F2m.checkFieldElements(this.x, this.curve.getA());
/*     */         }
/*     */       }
/*     */       
/* 371 */       this.withCompression = withCompression;
/*     */     }
/*     */     
/*     */ 
/*     */     /**
/*     */      * @deprecated
/*     */      */
/*     */     public F2m(ECCurve curve)
/*     */     {
/* 380 */       super(null, null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public byte[] getEncoded()
/*     */     {
/* 388 */       if (isInfinity())
/*     */       {
/* 390 */         return new byte[1];
/*     */       }
/*     */       
/* 393 */       int byteCount = ECPoint.converter.getByteLength(this.x);
/* 394 */       byte[] X = ECPoint.converter.integerToBytes(getX().toBigInteger(), byteCount);
/*     */       
/*     */       byte[] PO;
/* 397 */       if (this.withCompression)
/*     */       {
/*     */ 
/* 400 */         byte[] PO = new byte[byteCount + 1];
/*     */         
/* 402 */         PO[0] = 2;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 408 */         if (!getX().toBigInteger().equals(ECConstants.ZERO))
/*     */         {
/* 410 */           if (getY().multiply(getX().invert()).toBigInteger().testBit(0))
/*     */           {
/*     */ 
/*     */ 
/* 414 */             PO[0] = 3;
/*     */           }
/*     */         }
/*     */         
/* 418 */         System.arraycopy(X, 0, PO, 1, byteCount);
/*     */       }
/*     */       else
/*     */       {
/* 422 */         byte[] Y = ECPoint.converter.integerToBytes(getY().toBigInteger(), byteCount);
/*     */         
/* 424 */         PO = new byte[byteCount + byteCount + 1];
/*     */         
/* 426 */         PO[0] = 4;
/* 427 */         System.arraycopy(X, 0, PO, 1, byteCount);
/* 428 */         System.arraycopy(Y, 0, PO, byteCount + 1, byteCount);
/*     */       }
/*     */       
/* 431 */       return PO;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private static void checkPoints(ECPoint a, ECPoint b)
/*     */     {
/* 444 */       if (!a.curve.equals(b.curve))
/*     */       {
/* 446 */         throw new IllegalArgumentException("Only points on the same curve can be added or subtracted");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public ECPoint add(ECPoint b)
/*     */     {
/* 458 */       checkPoints(this, b);
/* 459 */       return addSimple((F2m)b);
/*     */     }
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
/*     */     public F2m addSimple(F2m b)
/*     */     {
/* 473 */       F2m other = b;
/* 474 */       if (isInfinity())
/*     */       {
/* 476 */         return other;
/*     */       }
/*     */       
/* 479 */       if (other.isInfinity())
/*     */       {
/* 481 */         return this;
/*     */       }
/*     */       
/* 484 */       ECFieldElement.F2m x2 = (ECFieldElement.F2m)other.getX();
/* 485 */       ECFieldElement.F2m y2 = (ECFieldElement.F2m)other.getY();
/*     */       
/*     */ 
/* 488 */       if (this.x.equals(x2))
/*     */       {
/* 490 */         if (this.y.equals(y2))
/*     */         {
/*     */ 
/* 493 */           return (F2m)twice();
/*     */         }
/*     */         
/*     */ 
/* 497 */         return (F2m)this.curve.getInfinity();
/*     */       }
/*     */       
/* 500 */       ECFieldElement.F2m lambda = (ECFieldElement.F2m)this.y.add(y2).divide(this.x.add(x2));
/*     */       
/*     */ 
/* 503 */       ECFieldElement.F2m x3 = (ECFieldElement.F2m)lambda.square().add(lambda).add(this.x).add(x2).add(this.curve.getA());
/*     */       
/*     */ 
/* 506 */       ECFieldElement.F2m y3 = (ECFieldElement.F2m)lambda.multiply(this.x.add(x3)).add(x3).add(this.y);
/*     */       
/*     */ 
/* 509 */       return new F2m(this.curve, x3, y3, this.withCompression);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public ECPoint subtract(ECPoint b)
/*     */     {
/* 517 */       checkPoints(this, b);
/* 518 */       return subtractSimple((F2m)b);
/*     */     }
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
/*     */     public F2m subtractSimple(F2m b)
/*     */     {
/* 532 */       if (b.isInfinity())
/*     */       {
/* 534 */         return this;
/*     */       }
/*     */       
/*     */ 
/* 538 */       return addSimple((F2m)b.negate());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public ECPoint twice()
/*     */     {
/* 546 */       if (isInfinity())
/*     */       {
/*     */ 
/* 549 */         return this;
/*     */       }
/*     */       
/* 552 */       if (this.x.toBigInteger().signum() == 0)
/*     */       {
/*     */ 
/*     */ 
/* 556 */         return this.curve.getInfinity();
/*     */       }
/*     */       
/* 559 */       ECFieldElement.F2m lambda = (ECFieldElement.F2m)this.x.add(this.y.divide(this.x));
/*     */       
/*     */ 
/* 562 */       ECFieldElement.F2m x3 = (ECFieldElement.F2m)lambda.square().add(lambda).add(this.curve.getA());
/*     */       
/*     */ 
/*     */ 
/* 566 */       ECFieldElement ONE = this.curve.fromBigInteger(ECConstants.ONE);
/* 567 */       ECFieldElement.F2m y3 = (ECFieldElement.F2m)this.x.square().add(x3.multiply(lambda.add(ONE)));
/*     */       
/*     */ 
/*     */ 
/* 571 */       return new F2m(this.curve, x3, y3, this.withCompression);
/*     */     }
/*     */     
/*     */     public ECPoint negate()
/*     */     {
/* 576 */       return new F2m(this.curve, getX(), getY().add(getX()), this.withCompression);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/math/ec/ECPoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */