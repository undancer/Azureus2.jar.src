/*     */ package org.gudy.bouncycastle.math.ec;
/*     */ 
/*     */ import java.math.BigInteger;
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
/*     */ class Tnaf
/*     */ {
/*  19 */   private static final BigInteger MINUS_ONE = ECConstants.ONE.negate();
/*  20 */   private static final BigInteger MINUS_TWO = ECConstants.TWO.negate();
/*  21 */   private static final BigInteger MINUS_THREE = ECConstants.THREE.negate();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final byte WIDTH = 4;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final byte POW_2_WIDTH = 16;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  44 */   public static final ZTauElement[] alpha0 = { null, new ZTauElement(ECConstants.ONE, ECConstants.ZERO), null, new ZTauElement(MINUS_THREE, MINUS_ONE), null, new ZTauElement(MINUS_ONE, MINUS_ONE), null, new ZTauElement(ECConstants.ONE, MINUS_ONE), null };
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
/*  56 */   public static final byte[][] alpha0Tnaf = { null, { 1 }, null, { -1, 0, 1 }, null, { 1, 0, 1 }, null, { -1, 0, 0, 1 } };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  64 */   public static final ZTauElement[] alpha1 = { null, new ZTauElement(ECConstants.ONE, ECConstants.ZERO), null, new ZTauElement(MINUS_THREE, ECConstants.ONE), null, new ZTauElement(MINUS_ONE, ECConstants.ONE), null, new ZTauElement(ECConstants.ONE, ECConstants.ONE), null };
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
/*  75 */   public static final byte[][] alpha1Tnaf = { null, { 1 }, null, { -1, 0, 1 }, null, { 1, 0, 1 }, null, { -1, 0, 0, -1 } };
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
/*     */   public static BigInteger norm(byte mu, ZTauElement lambda)
/*     */   {
/*  92 */     BigInteger s1 = lambda.u.multiply(lambda.u);
/*     */     
/*     */ 
/*  95 */     BigInteger s2 = lambda.u.multiply(lambda.v);
/*     */     
/*     */ 
/*  98 */     BigInteger s3 = lambda.v.multiply(lambda.v).shiftLeft(1);
/*     */     BigInteger norm;
/* 100 */     if (mu == 1)
/*     */     {
/* 102 */       norm = s1.add(s2).add(s3);
/*     */     } else { BigInteger norm;
/* 104 */       if (mu == -1)
/*     */       {
/* 106 */         norm = s1.subtract(s2).add(s3);
/*     */       }
/*     */       else
/*     */       {
/* 110 */         throw new IllegalArgumentException("mu must be 1 or -1"); }
/*     */     }
/*     */     BigInteger norm;
/* 113 */     return norm;
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
/*     */   public static SimpleBigDecimal norm(byte mu, SimpleBigDecimal u, SimpleBigDecimal v)
/*     */   {
/* 134 */     SimpleBigDecimal s1 = u.multiply(u);
/*     */     
/*     */ 
/* 137 */     SimpleBigDecimal s2 = u.multiply(v);
/*     */     
/*     */ 
/* 140 */     SimpleBigDecimal s3 = v.multiply(v).shiftLeft(1);
/*     */     SimpleBigDecimal norm;
/* 142 */     if (mu == 1)
/*     */     {
/* 144 */       norm = s1.add(s2).add(s3);
/*     */     } else { SimpleBigDecimal norm;
/* 146 */       if (mu == -1)
/*     */       {
/* 148 */         norm = s1.subtract(s2).add(s3);
/*     */       }
/*     */       else
/*     */       {
/* 152 */         throw new IllegalArgumentException("mu must be 1 or -1"); }
/*     */     }
/*     */     SimpleBigDecimal norm;
/* 155 */     return norm;
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
/*     */   public static ZTauElement round(SimpleBigDecimal lambda0, SimpleBigDecimal lambda1, byte mu)
/*     */   {
/* 174 */     int scale = lambda0.getScale();
/* 175 */     if (lambda1.getScale() != scale)
/*     */     {
/* 177 */       throw new IllegalArgumentException("lambda0 and lambda1 do not have same scale");
/*     */     }
/*     */     
/*     */ 
/* 181 */     if ((mu != 1) && (mu != -1))
/*     */     {
/* 183 */       throw new IllegalArgumentException("mu must be 1 or -1");
/*     */     }
/*     */     
/* 186 */     BigInteger f0 = lambda0.round();
/* 187 */     BigInteger f1 = lambda1.round();
/*     */     
/* 189 */     SimpleBigDecimal eta0 = lambda0.subtract(f0);
/* 190 */     SimpleBigDecimal eta1 = lambda1.subtract(f1);
/*     */     
/*     */ 
/* 193 */     SimpleBigDecimal eta = eta0.add(eta0);
/* 194 */     if (mu == 1)
/*     */     {
/* 196 */       eta = eta.add(eta1);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 201 */       eta = eta.subtract(eta1);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 206 */     SimpleBigDecimal threeEta1 = eta1.add(eta1).add(eta1);
/* 207 */     SimpleBigDecimal fourEta1 = threeEta1.add(eta1);
/*     */     SimpleBigDecimal check2;
/*     */     SimpleBigDecimal check1;
/* 210 */     SimpleBigDecimal check2; if (mu == 1)
/*     */     {
/* 212 */       SimpleBigDecimal check1 = eta0.subtract(threeEta1);
/* 213 */       check2 = eta0.add(fourEta1);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 218 */       check1 = eta0.add(threeEta1);
/* 219 */       check2 = eta0.subtract(fourEta1);
/*     */     }
/*     */     
/* 222 */     byte h0 = 0;
/* 223 */     byte h1 = 0;
/*     */     
/*     */ 
/* 226 */     if (eta.compareTo(ECConstants.ONE) >= 0)
/*     */     {
/* 228 */       if (check1.compareTo(MINUS_ONE) < 0)
/*     */       {
/* 230 */         h1 = mu;
/*     */       }
/*     */       else
/*     */       {
/* 234 */         h0 = 1;
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */     }
/* 240 */     else if (check2.compareTo(ECConstants.TWO) >= 0)
/*     */     {
/* 242 */       h1 = mu;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 247 */     if (eta.compareTo(MINUS_ONE) < 0)
/*     */     {
/* 249 */       if (check1.compareTo(ECConstants.ONE) >= 0)
/*     */       {
/* 251 */         h1 = (byte)-mu;
/*     */       }
/*     */       else
/*     */       {
/* 255 */         h0 = -1;
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */     }
/* 261 */     else if (check2.compareTo(MINUS_TWO) < 0)
/*     */     {
/* 263 */       h1 = (byte)-mu;
/*     */     }
/*     */     
/*     */ 
/* 267 */     BigInteger q0 = f0.add(BigInteger.valueOf(h0));
/* 268 */     BigInteger q1 = f1.add(BigInteger.valueOf(h1));
/* 269 */     return new ZTauElement(q0, q1);
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
/*     */   public static SimpleBigDecimal approximateDivisionByN(BigInteger k, BigInteger s, BigInteger vm, byte a, int m, int c)
/*     */   {
/* 291 */     int _k = (m + 5) / 2 + c;
/* 292 */     BigInteger ns = k.shiftRight(m - _k - 2 + a);
/*     */     
/* 294 */     BigInteger gs = s.multiply(ns);
/*     */     
/* 296 */     BigInteger hs = gs.shiftRight(m);
/*     */     
/* 298 */     BigInteger js = vm.multiply(hs);
/*     */     
/* 300 */     BigInteger gsPlusJs = gs.add(js);
/* 301 */     BigInteger ls = gsPlusJs.shiftRight(_k - c);
/* 302 */     if (gsPlusJs.testBit(_k - c - 1))
/*     */     {
/*     */ 
/* 305 */       ls = ls.add(ECConstants.ONE);
/*     */     }
/*     */     
/* 308 */     return new SimpleBigDecimal(ls, c);
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
/*     */   public static byte[] tauAdicNaf(byte mu, ZTauElement lambda)
/*     */   {
/* 321 */     if ((mu != 1) && (mu != -1))
/*     */     {
/* 323 */       throw new IllegalArgumentException("mu must be 1 or -1");
/*     */     }
/*     */     
/* 326 */     BigInteger norm = norm(mu, lambda);
/*     */     
/*     */ 
/* 329 */     int log2Norm = norm.bitLength();
/*     */     
/*     */ 
/* 332 */     int maxLength = log2Norm > 30 ? log2Norm + 4 : 34;
/*     */     
/*     */ 
/* 335 */     byte[] u = new byte[maxLength];
/* 336 */     int i = 0;
/*     */     
/*     */ 
/* 339 */     int length = 0;
/*     */     
/* 341 */     BigInteger r0 = lambda.u;
/* 342 */     BigInteger r1 = lambda.v;
/*     */     
/* 344 */     while ((!r0.equals(ECConstants.ZERO)) || (!r1.equals(ECConstants.ZERO)))
/*     */     {
/*     */ 
/* 347 */       if (r0.testBit(0))
/*     */       {
/* 349 */         u[i] = ((byte)ECConstants.TWO.subtract(r0.subtract(r1.shiftLeft(1)).mod(ECConstants.FOUR)).intValue());
/*     */         
/*     */ 
/* 352 */         if (u[i] == 1)
/*     */         {
/* 354 */           r0 = r0.clearBit(0);
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 359 */           r0 = r0.add(ECConstants.ONE);
/*     */         }
/* 361 */         length = i;
/*     */       }
/*     */       else
/*     */       {
/* 365 */         u[i] = 0;
/*     */       }
/*     */       
/* 368 */       BigInteger t = r0;
/* 369 */       BigInteger s = r0.shiftRight(1);
/* 370 */       if (mu == 1)
/*     */       {
/* 372 */         r0 = r1.add(s);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 377 */         r0 = r1.subtract(s);
/*     */       }
/*     */       
/* 380 */       r1 = t.shiftRight(1).negate();
/* 381 */       i++;
/*     */     }
/*     */     
/* 384 */     length++;
/*     */     
/*     */ 
/* 387 */     byte[] tnaf = new byte[length];
/* 388 */     System.arraycopy(u, 0, tnaf, 0, length);
/* 389 */     return tnaf;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ECPoint.F2m tau(ECPoint.F2m p)
/*     */   {
/* 400 */     if (p.isInfinity())
/*     */     {
/* 402 */       return p;
/*     */     }
/*     */     
/* 405 */     ECFieldElement x = p.getX();
/* 406 */     ECFieldElement y = p.getY();
/*     */     
/* 408 */     return new ECPoint.F2m(p.getCurve(), x.square(), y.square(), p.isCompressed());
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
/*     */   public static byte getMu(ECCurve.F2m curve)
/*     */   {
/* 423 */     BigInteger a = curve.getA().toBigInteger();
/*     */     
/*     */     byte mu;
/* 426 */     if (a.equals(ECConstants.ZERO))
/*     */     {
/* 428 */       mu = -1;
/*     */     } else { byte mu;
/* 430 */       if (a.equals(ECConstants.ONE))
/*     */       {
/* 432 */         mu = 1;
/*     */       }
/*     */       else
/*     */       {
/* 436 */         throw new IllegalArgumentException("No Koblitz curve (ABC), TNAF multiplication not possible"); }
/*     */     }
/*     */     byte mu;
/* 439 */     return mu;
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
/*     */   public static BigInteger[] getLucas(byte mu, int k, boolean doV)
/*     */   {
/* 458 */     if ((mu != 1) && (mu != -1))
/*     */     {
/* 460 */       throw new IllegalArgumentException("mu must be 1 or -1");
/*     */     }
/*     */     
/*     */     BigInteger u1;
/*     */     
/*     */     BigInteger u0;
/*     */     BigInteger u1;
/* 467 */     if (doV)
/*     */     {
/* 469 */       BigInteger u0 = ECConstants.TWO;
/* 470 */       u1 = BigInteger.valueOf(mu);
/*     */     }
/*     */     else
/*     */     {
/* 474 */       u0 = ECConstants.ZERO;
/* 475 */       u1 = ECConstants.ONE;
/*     */     }
/*     */     
/* 478 */     for (int i = 1; i < k; i++)
/*     */     {
/*     */ 
/* 481 */       BigInteger s = null;
/* 482 */       if (mu == 1)
/*     */       {
/* 484 */         s = u1;
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 489 */         s = u1.negate();
/*     */       }
/*     */       
/* 492 */       BigInteger u2 = s.subtract(u0.shiftLeft(1));
/* 493 */       u0 = u1;
/* 494 */       u1 = u2;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 499 */     BigInteger[] retVal = { u0, u1 };
/* 500 */     return retVal;
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
/*     */   public static BigInteger getTw(byte mu, int w)
/*     */   {
/* 513 */     if (w == 4)
/*     */     {
/* 515 */       if (mu == 1)
/*     */       {
/* 517 */         return BigInteger.valueOf(6L);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 522 */       return BigInteger.valueOf(10L);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 528 */     BigInteger[] us = getLucas(mu, w, false);
/* 529 */     BigInteger twoToW = ECConstants.ZERO.setBit(w);
/* 530 */     BigInteger u1invert = us[1].modInverse(twoToW);
/*     */     
/* 532 */     BigInteger tw = ECConstants.TWO.multiply(us[0]).multiply(u1invert).mod(twoToW);
/*     */     
/*     */ 
/* 535 */     return tw;
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
/*     */   public static BigInteger[] getSi(ECCurve.F2m curve)
/*     */   {
/* 549 */     if (!curve.isKoblitz())
/*     */     {
/* 551 */       throw new IllegalArgumentException("si is defined for Koblitz curves only");
/*     */     }
/*     */     
/* 554 */     int m = curve.getM();
/* 555 */     int a = curve.getA().toBigInteger().intValue();
/* 556 */     byte mu = curve.getMu();
/* 557 */     int h = curve.getH().intValue();
/* 558 */     int index = m + 3 - a;
/* 559 */     BigInteger[] ui = getLucas(mu, index, false);
/*     */     
/*     */     BigInteger dividend1;
/*     */     
/* 563 */     if (mu == 1)
/*     */     {
/* 565 */       BigInteger dividend0 = ECConstants.ONE.subtract(ui[1]);
/* 566 */       dividend1 = ECConstants.ONE.subtract(ui[0]);
/*     */     } else { BigInteger dividend1;
/* 568 */       if (mu == -1)
/*     */       {
/* 570 */         BigInteger dividend0 = ECConstants.ONE.add(ui[1]);
/* 571 */         dividend1 = ECConstants.ONE.add(ui[0]);
/*     */       }
/*     */       else
/*     */       {
/* 575 */         throw new IllegalArgumentException("mu must be 1 or -1"); } }
/*     */     BigInteger dividend1;
/*     */     BigInteger dividend0;
/* 578 */     BigInteger[] si = new BigInteger[2];
/*     */     
/* 580 */     if (h == 2)
/*     */     {
/* 582 */       si[0] = dividend0.shiftRight(1);
/* 583 */       si[1] = dividend1.shiftRight(1).negate();
/*     */     }
/* 585 */     else if (h == 4)
/*     */     {
/* 587 */       si[0] = dividend0.shiftRight(2);
/* 588 */       si[1] = dividend1.shiftRight(2).negate();
/*     */     }
/*     */     else
/*     */     {
/* 592 */       throw new IllegalArgumentException("h (Cofactor) must be 2 or 4");
/*     */     }
/*     */     
/* 595 */     return si;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ZTauElement partModReduction(BigInteger k, int m, byte a, BigInteger[] s, byte mu, byte c)
/*     */   {
/*     */     BigInteger d0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     BigInteger d0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 616 */     if (mu == 1)
/*     */     {
/* 618 */       d0 = s[0].add(s[1]);
/*     */     }
/*     */     else
/*     */     {
/* 622 */       d0 = s[0].subtract(s[1]);
/*     */     }
/*     */     
/* 625 */     BigInteger[] v = getLucas(mu, m, true);
/* 626 */     BigInteger vm = v[1];
/*     */     
/* 628 */     SimpleBigDecimal lambda0 = approximateDivisionByN(k, s[0], vm, a, m, c);
/*     */     
/*     */ 
/* 631 */     SimpleBigDecimal lambda1 = approximateDivisionByN(k, s[1], vm, a, m, c);
/*     */     
/*     */ 
/* 634 */     ZTauElement q = round(lambda0, lambda1, mu);
/*     */     
/*     */ 
/* 637 */     BigInteger r0 = k.subtract(d0.multiply(q.u)).subtract(BigInteger.valueOf(2L).multiply(s[1]).multiply(q.v));
/*     */     
/*     */ 
/*     */ 
/* 641 */     BigInteger r1 = s[1].multiply(q.u).subtract(s[0].multiply(q.v));
/*     */     
/* 643 */     return new ZTauElement(r0, r1);
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
/*     */   public static ECPoint.F2m multiplyRTnaf(ECPoint.F2m p, BigInteger k)
/*     */   {
/* 656 */     ECCurve.F2m curve = (ECCurve.F2m)p.getCurve();
/* 657 */     int m = curve.getM();
/* 658 */     byte a = (byte)curve.getA().toBigInteger().intValue();
/* 659 */     byte mu = curve.getMu();
/* 660 */     BigInteger[] s = curve.getSi();
/* 661 */     ZTauElement rho = partModReduction(k, m, a, s, mu, (byte)10);
/*     */     
/* 663 */     return multiplyTnaf(p, rho);
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
/*     */   public static ECPoint.F2m multiplyTnaf(ECPoint.F2m p, ZTauElement lambda)
/*     */   {
/* 677 */     ECCurve.F2m curve = (ECCurve.F2m)p.getCurve();
/* 678 */     byte mu = curve.getMu();
/* 679 */     byte[] u = tauAdicNaf(mu, lambda);
/*     */     
/* 681 */     ECPoint.F2m q = multiplyFromTnaf(p, u);
/*     */     
/* 683 */     return q;
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
/*     */   public static ECPoint.F2m multiplyFromTnaf(ECPoint.F2m p, byte[] u)
/*     */   {
/* 697 */     ECCurve.F2m curve = (ECCurve.F2m)p.getCurve();
/* 698 */     ECPoint.F2m q = (ECPoint.F2m)curve.getInfinity();
/* 699 */     for (int i = u.length - 1; i >= 0; i--)
/*     */     {
/* 701 */       q = tau(q);
/* 702 */       if (u[i] == 1)
/*     */       {
/* 704 */         q = q.addSimple(p);
/*     */       }
/* 706 */       else if (u[i] == -1)
/*     */       {
/* 708 */         q = q.subtractSimple(p);
/*     */       }
/*     */     }
/* 711 */     return q;
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
/*     */   public static byte[] tauAdicWNaf(byte mu, ZTauElement lambda, byte width, BigInteger pow2w, BigInteger tw, ZTauElement[] alpha)
/*     */   {
/* 731 */     if ((mu != 1) && (mu != -1))
/*     */     {
/* 733 */       throw new IllegalArgumentException("mu must be 1 or -1");
/*     */     }
/*     */     
/* 736 */     BigInteger norm = norm(mu, lambda);
/*     */     
/*     */ 
/* 739 */     int log2Norm = norm.bitLength();
/*     */     
/*     */ 
/* 742 */     int maxLength = log2Norm > 30 ? log2Norm + 4 + width : 34 + width;
/*     */     
/*     */ 
/* 745 */     byte[] u = new byte[maxLength];
/*     */     
/*     */ 
/* 748 */     BigInteger pow2wMin1 = pow2w.shiftRight(1);
/*     */     
/*     */ 
/* 751 */     BigInteger r0 = lambda.u;
/* 752 */     BigInteger r1 = lambda.v;
/* 753 */     int i = 0;
/*     */     
/*     */ 
/* 756 */     while ((!r0.equals(ECConstants.ZERO)) || (!r1.equals(ECConstants.ZERO)))
/*     */     {
/*     */ 
/* 759 */       if (r0.testBit(0))
/*     */       {
/*     */ 
/* 762 */         BigInteger uUnMod = r0.add(r1.multiply(tw)).mod(pow2w);
/*     */         
/*     */         byte uLocal;
/*     */         
/*     */         byte uLocal;
/* 767 */         if (uUnMod.compareTo(pow2wMin1) >= 0)
/*     */         {
/* 769 */           uLocal = (byte)uUnMod.subtract(pow2w).intValue();
/*     */         }
/*     */         else
/*     */         {
/* 773 */           uLocal = (byte)uUnMod.intValue();
/*     */         }
/*     */         
/*     */ 
/* 777 */         u[i] = uLocal;
/* 778 */         boolean s = true;
/* 779 */         if (uLocal < 0)
/*     */         {
/* 781 */           s = false;
/* 782 */           uLocal = (byte)-uLocal;
/*     */         }
/*     */         
/*     */ 
/* 786 */         if (s)
/*     */         {
/* 788 */           r0 = r0.subtract(alpha[uLocal].u);
/* 789 */           r1 = r1.subtract(alpha[uLocal].v);
/*     */         }
/*     */         else
/*     */         {
/* 793 */           r0 = r0.add(alpha[uLocal].u);
/* 794 */           r1 = r1.add(alpha[uLocal].v);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 799 */         u[i] = 0;
/*     */       }
/*     */       
/* 802 */       BigInteger t = r0;
/*     */       
/* 804 */       if (mu == 1)
/*     */       {
/* 806 */         r0 = r1.add(r0.shiftRight(1));
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 811 */         r0 = r1.subtract(r0.shiftRight(1));
/*     */       }
/* 813 */       r1 = t.shiftRight(1).negate();
/* 814 */       i++;
/*     */     }
/* 816 */     return u;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ECPoint.F2m[] getPreComp(ECPoint.F2m p, byte a)
/*     */   {
/* 828 */     ECPoint.F2m[] pu = new ECPoint.F2m[16];
/* 829 */     pu[1] = p;
/*     */     byte[][] alphaTnaf;
/* 831 */     byte[][] alphaTnaf; if (a == 0)
/*     */     {
/* 833 */       alphaTnaf = alpha0Tnaf;
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 838 */       alphaTnaf = alpha1Tnaf;
/*     */     }
/*     */     
/* 841 */     int precompLen = alphaTnaf.length;
/* 842 */     for (int i = 3; i < precompLen; i += 2)
/*     */     {
/* 844 */       pu[i] = multiplyFromTnaf(p, alphaTnaf[i]);
/*     */     }
/*     */     
/* 847 */     return pu;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/math/ec/Tnaf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */