/*     */ package org.gudy.bouncycastle.math.ec;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.util.Random;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ECCurve
/*     */ {
/*     */   ECFieldElement a;
/*     */   ECFieldElement b;
/*     */   
/*     */   public abstract int getFieldSize();
/*     */   
/*     */   public abstract ECFieldElement fromBigInteger(BigInteger paramBigInteger);
/*     */   
/*     */   public abstract ECPoint createPoint(BigInteger paramBigInteger1, BigInteger paramBigInteger2, boolean paramBoolean);
/*     */   
/*     */   public abstract ECPoint decodePoint(byte[] paramArrayOfByte);
/*     */   
/*     */   public abstract ECPoint getInfinity();
/*     */   
/*     */   public ECFieldElement getA()
/*     */   {
/*  30 */     return this.a;
/*     */   }
/*     */   
/*     */   public ECFieldElement getB()
/*     */   {
/*  35 */     return this.b;
/*     */   }
/*     */   
/*     */ 
/*     */   public static class Fp
/*     */     extends ECCurve
/*     */   {
/*     */     BigInteger q;
/*     */     
/*     */     ECPoint.Fp infinity;
/*     */     
/*     */     public Fp(BigInteger q, BigInteger a, BigInteger b)
/*     */     {
/*  48 */       this.q = q;
/*  49 */       this.a = fromBigInteger(a);
/*  50 */       this.b = fromBigInteger(b);
/*  51 */       this.infinity = new ECPoint.Fp(this, null, null);
/*     */     }
/*     */     
/*     */     public BigInteger getQ()
/*     */     {
/*  56 */       return this.q;
/*     */     }
/*     */     
/*     */     public int getFieldSize()
/*     */     {
/*  61 */       return this.q.bitLength();
/*     */     }
/*     */     
/*     */     public ECFieldElement fromBigInteger(BigInteger x)
/*     */     {
/*  66 */       return new ECFieldElement.Fp(this.q, x);
/*     */     }
/*     */     
/*     */     public ECPoint createPoint(BigInteger x, BigInteger y, boolean withCompression)
/*     */     {
/*  71 */       return new ECPoint.Fp(this, fromBigInteger(x), fromBigInteger(y), withCompression);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public ECPoint decodePoint(byte[] encoded)
/*     */     {
/*  82 */       ECPoint p = null;
/*     */       
/*  84 */       switch (encoded[0])
/*     */       {
/*     */ 
/*     */       case 0: 
/*  88 */         p = getInfinity();
/*  89 */         break;
/*     */       
/*     */       case 2: 
/*     */       case 3: 
/*  93 */         int ytilde = encoded[0] & 0x1;
/*  94 */         byte[] i = new byte[encoded.length - 1];
/*     */         
/*  96 */         System.arraycopy(encoded, 1, i, 0, i.length);
/*     */         
/*  98 */         ECFieldElement x = new ECFieldElement.Fp(this.q, new BigInteger(1, i));
/*  99 */         ECFieldElement alpha = x.multiply(x.square().add(this.a)).add(this.b);
/* 100 */         ECFieldElement beta = alpha.sqrt();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 106 */         if (beta == null)
/*     */         {
/* 108 */           throw new RuntimeException("Invalid point compression");
/*     */         }
/*     */         
/* 111 */         int bit0 = beta.toBigInteger().testBit(0) ? 1 : 0;
/*     */         
/* 113 */         if (bit0 == ytilde)
/*     */         {
/* 115 */           p = new ECPoint.Fp(this, x, beta, true);
/*     */         }
/*     */         else
/*     */         {
/* 119 */           p = new ECPoint.Fp(this, x, new ECFieldElement.Fp(this.q, this.q.subtract(beta.toBigInteger())), true);
/*     */         }
/*     */         
/* 122 */         break;
/*     */       
/*     */ 
/*     */       case 4: 
/*     */       case 6: 
/*     */       case 7: 
/* 128 */         byte[] xEnc = new byte[(encoded.length - 1) / 2];
/* 129 */         byte[] yEnc = new byte[(encoded.length - 1) / 2];
/*     */         
/* 131 */         System.arraycopy(encoded, 1, xEnc, 0, xEnc.length);
/* 132 */         System.arraycopy(encoded, xEnc.length + 1, yEnc, 0, yEnc.length);
/*     */         
/* 134 */         p = new ECPoint.Fp(this, new ECFieldElement.Fp(this.q, new BigInteger(1, xEnc)), new ECFieldElement.Fp(this.q, new BigInteger(1, yEnc)));
/*     */         
/*     */ 
/* 137 */         break;
/*     */       case 1: case 5: default: 
/* 139 */         throw new RuntimeException("Invalid point encoding 0x" + Integer.toString(encoded[0], 16));
/*     */       }
/*     */       
/* 142 */       return p;
/*     */     }
/*     */     
/*     */     public ECPoint getInfinity()
/*     */     {
/* 147 */       return this.infinity;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean equals(Object anObject)
/*     */     {
/* 153 */       if (anObject == this)
/*     */       {
/* 155 */         return true;
/*     */       }
/*     */       
/* 158 */       if (!(anObject instanceof Fp))
/*     */       {
/* 160 */         return false;
/*     */       }
/*     */       
/* 163 */       Fp other = (Fp)anObject;
/*     */       
/* 165 */       return (this.q.equals(other.q)) && (this.a.equals(other.a)) && (this.b.equals(other.b));
/*     */     }
/*     */     
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 171 */       return this.a.hashCode() ^ this.b.hashCode() ^ this.q.hashCode();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static class F2m
/*     */     extends ECCurve
/*     */   {
/*     */     private int m;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private int k1;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private int k2;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private int k3;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private BigInteger n;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private BigInteger h;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private ECPoint.F2m infinity;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 231 */     private byte mu = 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 238 */     private BigInteger[] si = null;
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
/*     */     public F2m(int m, int k, BigInteger a, BigInteger b)
/*     */     {
/* 260 */       this(m, k, 0, 0, a, b, null, null);
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
/*     */     public F2m(int m, int k, BigInteger a, BigInteger b, BigInteger n, BigInteger h)
/*     */     {
/* 288 */       this(m, k, 0, 0, a, b, n, h);
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
/*     */     public F2m(int m, int k1, int k2, int k3, BigInteger a, BigInteger b)
/*     */     {
/* 319 */       this(m, k1, k2, k3, a, b, null, null);
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
/*     */     public F2m(int m, int k1, int k2, int k3, BigInteger a, BigInteger b, BigInteger n, BigInteger h)
/*     */     {
/* 355 */       this.m = m;
/* 356 */       this.k1 = k1;
/* 357 */       this.k2 = k2;
/* 358 */       this.k3 = k3;
/* 359 */       this.n = n;
/* 360 */       this.h = h;
/*     */       
/* 362 */       if (k1 == 0)
/*     */       {
/* 364 */         throw new IllegalArgumentException("k1 must be > 0");
/*     */       }
/*     */       
/* 367 */       if (k2 == 0)
/*     */       {
/* 369 */         if (k3 != 0)
/*     */         {
/* 371 */           throw new IllegalArgumentException("k3 must be 0 if k2 == 0");
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 376 */         if (k2 <= k1)
/*     */         {
/* 378 */           throw new IllegalArgumentException("k2 must be > k1");
/*     */         }
/*     */         
/* 381 */         if (k3 <= k2)
/*     */         {
/* 383 */           throw new IllegalArgumentException("k3 must be > k2");
/*     */         }
/*     */       }
/*     */       
/* 387 */       this.a = fromBigInteger(a);
/* 388 */       this.b = fromBigInteger(b);
/* 389 */       this.infinity = new ECPoint.F2m(this, null, null);
/*     */     }
/*     */     
/*     */     public int getFieldSize()
/*     */     {
/* 394 */       return this.m;
/*     */     }
/*     */     
/*     */     public ECFieldElement fromBigInteger(BigInteger x)
/*     */     {
/* 399 */       return new ECFieldElement.F2m(this.m, this.k1, this.k2, this.k3, x);
/*     */     }
/*     */     
/*     */     public ECPoint createPoint(BigInteger x, BigInteger y, boolean withCompression)
/*     */     {
/* 404 */       return new ECPoint.F2m(this, fromBigInteger(x), fromBigInteger(y), withCompression);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public ECPoint decodePoint(byte[] encoded)
/*     */     {
/* 412 */       ECPoint p = null;
/*     */       
/* 414 */       switch (encoded[0])
/*     */       {
/*     */ 
/*     */       case 0: 
/* 418 */         p = getInfinity();
/* 419 */         break;
/*     */       
/*     */       case 2: 
/*     */       case 3: 
/* 423 */         byte[] enc = new byte[encoded.length - 1];
/* 424 */         System.arraycopy(encoded, 1, enc, 0, enc.length);
/* 425 */         if (encoded[0] == 2)
/*     */         {
/* 427 */           p = decompressPoint(enc, 0);
/*     */         }
/*     */         else
/*     */         {
/* 431 */           p = decompressPoint(enc, 1);
/*     */         }
/* 433 */         break;
/*     */       
/*     */ 
/*     */       case 4: 
/*     */       case 6: 
/*     */       case 7: 
/* 439 */         byte[] xEnc = new byte[(encoded.length - 1) / 2];
/* 440 */         byte[] yEnc = new byte[(encoded.length - 1) / 2];
/*     */         
/* 442 */         System.arraycopy(encoded, 1, xEnc, 0, xEnc.length);
/* 443 */         System.arraycopy(encoded, xEnc.length + 1, yEnc, 0, yEnc.length);
/*     */         
/* 445 */         p = new ECPoint.F2m(this, new ECFieldElement.F2m(this.m, this.k1, this.k2, this.k3, new BigInteger(1, xEnc)), new ECFieldElement.F2m(this.m, this.k1, this.k2, this.k3, new BigInteger(1, yEnc)), false);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 450 */         break;
/*     */       case 1: case 5: 
/*     */       default: 
/* 453 */         throw new RuntimeException("Invalid point encoding 0x" + Integer.toString(encoded[0], 16));
/*     */       }
/*     */       
/* 456 */       return p;
/*     */     }
/*     */     
/*     */     public ECPoint getInfinity()
/*     */     {
/* 461 */       return this.infinity;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean isKoblitz()
/*     */     {
/* 470 */       return (this.n != null) && (this.h != null) && ((this.a.toBigInteger().equals(ECConstants.ZERO)) || (this.a.toBigInteger().equals(ECConstants.ONE))) && (this.b.toBigInteger().equals(ECConstants.ONE));
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
/*     */     synchronized byte getMu()
/*     */     {
/* 484 */       if (this.mu == 0)
/*     */       {
/* 486 */         this.mu = Tnaf.getMu(this);
/*     */       }
/* 488 */       return this.mu;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     synchronized BigInteger[] getSi()
/*     */     {
/* 498 */       if (this.si == null)
/*     */       {
/* 500 */         this.si = Tnaf.getSi(this);
/*     */       }
/* 502 */       return this.si;
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
/*     */ 
/*     */ 
/*     */     private ECPoint decompressPoint(byte[] xEnc, int ypBit)
/*     */     {
/* 518 */       ECFieldElement xp = new ECFieldElement.F2m(this.m, this.k1, this.k2, this.k3, new BigInteger(1, xEnc));
/*     */       
/* 520 */       ECFieldElement yp = null;
/* 521 */       if (xp.toBigInteger().equals(ECConstants.ZERO))
/*     */       {
/* 523 */         yp = (ECFieldElement.F2m)this.b;
/* 524 */         for (int i = 0; i < this.m - 1; i++)
/*     */         {
/* 526 */           yp = yp.square();
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 531 */         ECFieldElement beta = xp.add(this.a).add(this.b.multiply(xp.square().invert()));
/*     */         
/* 533 */         ECFieldElement z = solveQuadradicEquation(beta);
/* 534 */         if (z == null)
/*     */         {
/* 536 */           throw new RuntimeException("Invalid point compression");
/*     */         }
/* 538 */         int zBit = 0;
/* 539 */         if (z.toBigInteger().testBit(0))
/*     */         {
/* 541 */           zBit = 1;
/*     */         }
/* 543 */         if (zBit != ypBit)
/*     */         {
/* 545 */           z = z.add(new ECFieldElement.F2m(this.m, this.k1, this.k2, this.k3, ECConstants.ONE));
/*     */         }
/*     */         
/* 548 */         yp = xp.multiply(z);
/*     */       }
/*     */       
/* 551 */       return new ECPoint.F2m(this, xp, yp);
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
/*     */     private ECFieldElement solveQuadradicEquation(ECFieldElement beta)
/*     */     {
/* 565 */       ECFieldElement zeroElement = new ECFieldElement.F2m(this.m, this.k1, this.k2, this.k3, ECConstants.ZERO);
/*     */       
/*     */ 
/* 568 */       if (beta.toBigInteger().equals(ECConstants.ZERO))
/*     */       {
/* 570 */         return zeroElement;
/*     */       }
/*     */       
/* 573 */       ECFieldElement z = null;
/* 574 */       ECFieldElement gamma = zeroElement;
/*     */       
/* 576 */       Random rand = new Random();
/*     */       do
/*     */       {
/* 579 */         ECFieldElement t = new ECFieldElement.F2m(this.m, this.k1, this.k2, this.k3, new BigInteger(this.m, rand));
/*     */         
/* 581 */         z = zeroElement;
/* 582 */         ECFieldElement w = beta;
/* 583 */         for (int i = 1; i <= this.m - 1; i++)
/*     */         {
/* 585 */           ECFieldElement w2 = w.square();
/* 586 */           z = z.square().add(w2.multiply(t));
/* 587 */           w = w2.add(beta);
/*     */         }
/* 589 */         if (!w.toBigInteger().equals(ECConstants.ZERO))
/*     */         {
/* 591 */           return null;
/*     */         }
/* 593 */         gamma = z.square().add(z);
/*     */       }
/* 595 */       while (gamma.toBigInteger().equals(ECConstants.ZERO));
/*     */       
/* 597 */       return z;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean equals(Object anObject)
/*     */     {
/* 603 */       if (anObject == this)
/*     */       {
/* 605 */         return true;
/*     */       }
/*     */       
/* 608 */       if (!(anObject instanceof F2m))
/*     */       {
/* 610 */         return false;
/*     */       }
/*     */       
/* 613 */       F2m other = (F2m)anObject;
/*     */       
/* 615 */       return (this.m == other.m) && (this.k1 == other.k1) && (this.k2 == other.k2) && (this.k3 == other.k3) && (this.a.equals(other.a)) && (this.b.equals(other.b));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 622 */       return this.a.hashCode() ^ this.b.hashCode() ^ this.m ^ this.k1 ^ this.k2 ^ this.k3;
/*     */     }
/*     */     
/*     */     public int getM()
/*     */     {
/* 627 */       return this.m;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean isTrinomial()
/*     */     {
/* 637 */       return (this.k2 == 0) && (this.k3 == 0);
/*     */     }
/*     */     
/*     */     public int getK1()
/*     */     {
/* 642 */       return this.k1;
/*     */     }
/*     */     
/*     */     public int getK2()
/*     */     {
/* 647 */       return this.k2;
/*     */     }
/*     */     
/*     */     public int getK3()
/*     */     {
/* 652 */       return this.k3;
/*     */     }
/*     */     
/*     */     public BigInteger getN()
/*     */     {
/* 657 */       return this.n;
/*     */     }
/*     */     
/*     */     public BigInteger getH()
/*     */     {
/* 662 */       return this.h;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/math/ec/ECCurve.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */