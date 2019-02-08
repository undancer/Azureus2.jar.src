/*      */ package org.gudy.bouncycastle.math.ec;
/*      */ 
/*      */ import java.math.BigInteger;
/*      */ 
/*      */ public abstract class ECFieldElement implements ECConstants { public abstract BigInteger toBigInteger();
/*      */   
/*      */   public abstract String getFieldName();
/*      */   
/*      */   public abstract int getFieldSize();
/*      */   
/*      */   public abstract ECFieldElement add(ECFieldElement paramECFieldElement);
/*      */   
/*      */   public abstract ECFieldElement subtract(ECFieldElement paramECFieldElement);
/*      */   
/*      */   public abstract ECFieldElement multiply(ECFieldElement paramECFieldElement);
/*      */   
/*      */   public abstract ECFieldElement divide(ECFieldElement paramECFieldElement);
/*      */   
/*      */   public abstract ECFieldElement negate();
/*      */   
/*      */   public abstract ECFieldElement square();
/*      */   
/*      */   public abstract ECFieldElement invert();
/*      */   
/*      */   public abstract ECFieldElement sqrt();
/*      */   
/*   27 */   public String toString() { return toBigInteger().toString(2); }
/*      */   
/*      */ 
/*      */   public static class Fp
/*      */     extends ECFieldElement
/*      */   {
/*      */     BigInteger x;
/*      */     BigInteger q;
/*      */     
/*      */     public Fp(BigInteger q, BigInteger x)
/*      */     {
/*   38 */       this.x = x;
/*      */       
/*   40 */       if (x.compareTo(q) >= 0)
/*      */       {
/*   42 */         throw new IllegalArgumentException("x value too large in field element");
/*      */       }
/*      */       
/*   45 */       this.q = q;
/*      */     }
/*      */     
/*      */     public BigInteger toBigInteger()
/*      */     {
/*   50 */       return this.x;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public String getFieldName()
/*      */     {
/*   60 */       return "Fp";
/*      */     }
/*      */     
/*      */     public int getFieldSize()
/*      */     {
/*   65 */       return this.q.bitLength();
/*      */     }
/*      */     
/*      */     public BigInteger getQ()
/*      */     {
/*   70 */       return this.q;
/*      */     }
/*      */     
/*      */     public ECFieldElement add(ECFieldElement b)
/*      */     {
/*   75 */       return new Fp(this.q, this.x.add(b.toBigInteger()).mod(this.q));
/*      */     }
/*      */     
/*      */     public ECFieldElement subtract(ECFieldElement b)
/*      */     {
/*   80 */       return new Fp(this.q, this.x.subtract(b.toBigInteger()).mod(this.q));
/*      */     }
/*      */     
/*      */     public ECFieldElement multiply(ECFieldElement b)
/*      */     {
/*   85 */       return new Fp(this.q, this.x.multiply(b.toBigInteger()).mod(this.q));
/*      */     }
/*      */     
/*      */     public ECFieldElement divide(ECFieldElement b)
/*      */     {
/*   90 */       return new Fp(this.q, this.x.multiply(b.toBigInteger().modInverse(this.q)).mod(this.q));
/*      */     }
/*      */     
/*      */     public ECFieldElement negate()
/*      */     {
/*   95 */       return new Fp(this.q, this.x.negate().mod(this.q));
/*      */     }
/*      */     
/*      */     public ECFieldElement square()
/*      */     {
/*  100 */       return new Fp(this.q, this.x.multiply(this.x).mod(this.q));
/*      */     }
/*      */     
/*      */     public ECFieldElement invert()
/*      */     {
/*  105 */       return new Fp(this.q, this.x.modInverse(this.q));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public ECFieldElement sqrt()
/*      */     {
/*  115 */       if (!this.q.testBit(0))
/*      */       {
/*  117 */         throw new RuntimeException("not done yet");
/*      */       }
/*      */       
/*      */ 
/*  121 */       if (this.q.testBit(1))
/*      */       {
/*      */ 
/*  124 */         ECFieldElement z = new Fp(this.q, this.x.modPow(this.q.shiftRight(2).add(ONE), this.q));
/*      */         
/*  126 */         return z.square().equals(this) ? z : null;
/*      */       }
/*      */       
/*      */ 
/*  130 */       BigInteger qMinusOne = this.q.subtract(ECConstants.ONE);
/*      */       
/*  132 */       BigInteger legendreExponent = qMinusOne.shiftRight(1);
/*  133 */       if (!this.x.modPow(legendreExponent, this.q).equals(ECConstants.ONE))
/*      */       {
/*  135 */         return null;
/*      */       }
/*      */       
/*  138 */       BigInteger u = qMinusOne.shiftRight(2);
/*  139 */       BigInteger k = u.shiftLeft(1).add(ECConstants.ONE);
/*      */       
/*  141 */       BigInteger Q = this.x;
/*  142 */       BigInteger fourQ = Q.shiftLeft(2).mod(this.q);
/*      */       
/*      */ 
/*  145 */       java.util.Random rand = new java.util.Random();
/*      */       BigInteger U;
/*      */       do
/*      */       {
/*      */         BigInteger P;
/*      */         do {
/*  151 */           P = new BigInteger(this.q.bitLength(), rand);
/*      */ 
/*      */         }
/*  154 */         while ((P.compareTo(this.q) >= 0) || (!P.multiply(P).subtract(fourQ).modPow(legendreExponent, this.q).equals(qMinusOne)));
/*      */         
/*  156 */         BigInteger[] result = lucasSequence(this.q, P, Q, k);
/*  157 */         U = result[0];
/*  158 */         BigInteger V = result[1];
/*      */         
/*  160 */         if (V.multiply(V).mod(this.q).equals(fourQ))
/*      */         {
/*      */ 
/*  163 */           if (V.testBit(0))
/*      */           {
/*  165 */             V = V.add(this.q);
/*      */           }
/*      */           
/*  168 */           V = V.shiftRight(1);
/*      */           
/*      */ 
/*      */ 
/*  172 */           return new Fp(this.q, V);
/*      */         }
/*      */         
/*  175 */       } while ((U.equals(ECConstants.ONE)) || (U.equals(qMinusOne)));
/*      */       
/*  177 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private static BigInteger[] lucasSequence(BigInteger p, BigInteger P, BigInteger Q, BigInteger k)
/*      */     {
/*  240 */       int n = k.bitLength();
/*  241 */       int s = k.getLowestSetBit();
/*      */       
/*  243 */       BigInteger Uh = ECConstants.ONE;
/*  244 */       BigInteger Vl = ECConstants.TWO;
/*  245 */       BigInteger Vh = P;
/*  246 */       BigInteger Ql = ECConstants.ONE;
/*  247 */       BigInteger Qh = ECConstants.ONE;
/*      */       
/*  249 */       for (int j = n - 1; j >= s + 1; j--)
/*      */       {
/*  251 */         Ql = Ql.multiply(Qh).mod(p);
/*      */         
/*  253 */         if (k.testBit(j))
/*      */         {
/*  255 */           Qh = Ql.multiply(Q).mod(p);
/*  256 */           Uh = Uh.multiply(Vh).mod(p);
/*  257 */           Vl = Vh.multiply(Vl).subtract(P.multiply(Ql)).mod(p);
/*  258 */           Vh = Vh.multiply(Vh).subtract(Qh.shiftLeft(1)).mod(p);
/*      */         }
/*      */         else
/*      */         {
/*  262 */           Qh = Ql;
/*  263 */           Uh = Uh.multiply(Vl).subtract(Ql).mod(p);
/*  264 */           Vh = Vh.multiply(Vl).subtract(P.multiply(Ql)).mod(p);
/*  265 */           Vl = Vl.multiply(Vl).subtract(Ql.shiftLeft(1)).mod(p);
/*      */         }
/*      */       }
/*      */       
/*  269 */       Ql = Ql.multiply(Qh).mod(p);
/*  270 */       Qh = Ql.multiply(Q).mod(p);
/*  271 */       Uh = Uh.multiply(Vl).subtract(Ql).mod(p);
/*  272 */       Vl = Vh.multiply(Vl).subtract(P.multiply(Ql)).mod(p);
/*  273 */       Ql = Ql.multiply(Qh).mod(p);
/*      */       
/*  275 */       for (int j = 1; j <= s; j++)
/*      */       {
/*  277 */         Uh = Uh.multiply(Vl).mod(p);
/*  278 */         Vl = Vl.multiply(Vl).subtract(Ql.shiftLeft(1)).mod(p);
/*  279 */         Ql = Ql.multiply(Ql).mod(p);
/*      */       }
/*      */       
/*  282 */       return new BigInteger[] { Uh, Vl };
/*      */     }
/*      */     
/*      */     public boolean equals(Object other)
/*      */     {
/*  287 */       if (other == this)
/*      */       {
/*  289 */         return true;
/*      */       }
/*      */       
/*  292 */       if (!(other instanceof Fp))
/*      */       {
/*  294 */         return false;
/*      */       }
/*      */       
/*  297 */       Fp o = (Fp)other;
/*  298 */       return (this.q.equals(o.q)) && (this.x.equals(o.x));
/*      */     }
/*      */     
/*      */     public int hashCode()
/*      */     {
/*  303 */       return this.q.hashCode() ^ this.x.hashCode();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static class F2m
/*      */     extends ECFieldElement
/*      */   {
/*      */     public static final int GNB = 1;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public static final int TPB = 2;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public static final int PPB = 3;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private int representation;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private int m;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private int k1;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private int k2;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private int k3;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private IntArray x;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private int t;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public F2m(int m, int k1, int k2, int k3, BigInteger x)
/*      */     {
/*  864 */       this.t = (m + 31 >> 5);
/*  865 */       this.x = new IntArray(x, this.t);
/*      */       
/*  867 */       if ((k2 == 0) && (k3 == 0))
/*      */       {
/*  869 */         this.representation = 2;
/*      */       }
/*      */       else
/*      */       {
/*  873 */         if (k2 >= k3)
/*      */         {
/*  875 */           throw new IllegalArgumentException("k2 must be smaller than k3");
/*      */         }
/*      */         
/*  878 */         if (k2 <= 0)
/*      */         {
/*  880 */           throw new IllegalArgumentException("k2 must be larger than 0");
/*      */         }
/*      */         
/*  883 */         this.representation = 3;
/*      */       }
/*      */       
/*  886 */       if (x.signum() < 0)
/*      */       {
/*  888 */         throw new IllegalArgumentException("x value cannot be negative");
/*      */       }
/*      */       
/*  891 */       this.m = m;
/*  892 */       this.k1 = k1;
/*  893 */       this.k2 = k2;
/*  894 */       this.k3 = k3;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public F2m(int m, int k, BigInteger x)
/*      */     {
/*  909 */       this(m, k, 0, 0, x);
/*      */     }
/*      */     
/*      */     private F2m(int m, int k1, int k2, int k3, IntArray x)
/*      */     {
/*  914 */       this.t = (m + 31 >> 5);
/*  915 */       this.x = x;
/*  916 */       this.m = m;
/*  917 */       this.k1 = k1;
/*  918 */       this.k2 = k2;
/*  919 */       this.k3 = k3;
/*      */       
/*  921 */       if ((k2 == 0) && (k3 == 0))
/*      */       {
/*  923 */         this.representation = 2;
/*      */       }
/*      */       else
/*      */       {
/*  927 */         this.representation = 3;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public BigInteger toBigInteger()
/*      */     {
/*  934 */       return this.x.toBigInteger();
/*      */     }
/*      */     
/*      */     public String getFieldName()
/*      */     {
/*  939 */       return "F2m";
/*      */     }
/*      */     
/*      */     public int getFieldSize()
/*      */     {
/*  944 */       return this.m;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public static void checkFieldElements(ECFieldElement a, ECFieldElement b)
/*      */     {
/*  962 */       if ((!(a instanceof F2m)) || (!(b instanceof F2m)))
/*      */       {
/*  964 */         throw new IllegalArgumentException("Field elements are not both instances of ECFieldElement.F2m");
/*      */       }
/*      */       
/*      */ 
/*  968 */       F2m aF2m = (F2m)a;
/*  969 */       F2m bF2m = (F2m)b;
/*      */       
/*  971 */       if ((aF2m.m != bF2m.m) || (aF2m.k1 != bF2m.k1) || (aF2m.k2 != bF2m.k2) || (aF2m.k3 != bF2m.k3))
/*      */       {
/*      */ 
/*  974 */         throw new IllegalArgumentException("Field elements are not elements of the same field F2m");
/*      */       }
/*      */       
/*      */ 
/*  978 */       if (aF2m.representation != bF2m.representation)
/*      */       {
/*      */ 
/*  981 */         throw new IllegalArgumentException("One of the field elements are not elements has incorrect representation");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public ECFieldElement add(ECFieldElement b)
/*      */     {
/*  992 */       IntArray iarrClone = (IntArray)this.x.clone();
/*  993 */       F2m bF2m = (F2m)b;
/*  994 */       iarrClone.addShifted(bF2m.x, 0);
/*  995 */       return new F2m(this.m, this.k1, this.k2, this.k3, iarrClone);
/*      */     }
/*      */     
/*      */ 
/*      */     public ECFieldElement subtract(ECFieldElement b)
/*      */     {
/* 1001 */       return add(b);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public ECFieldElement multiply(ECFieldElement b)
/*      */     {
/* 1013 */       F2m bF2m = (F2m)b;
/* 1014 */       IntArray mult = this.x.multiply(bF2m.x, this.m);
/* 1015 */       mult.reduce(this.m, new int[] { this.k1, this.k2, this.k3 });
/* 1016 */       return new F2m(this.m, this.k1, this.k2, this.k3, mult);
/*      */     }
/*      */     
/*      */ 
/*      */     public ECFieldElement divide(ECFieldElement b)
/*      */     {
/* 1022 */       ECFieldElement bInv = b.invert();
/* 1023 */       return multiply(bInv);
/*      */     }
/*      */     
/*      */ 
/*      */     public ECFieldElement negate()
/*      */     {
/* 1029 */       return this;
/*      */     }
/*      */     
/*      */     public ECFieldElement square()
/*      */     {
/* 1034 */       IntArray squared = this.x.square(this.m);
/* 1035 */       squared.reduce(this.m, new int[] { this.k1, this.k2, this.k3 });
/* 1036 */       return new F2m(this.m, this.k1, this.k2, this.k3, squared);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public ECFieldElement invert()
/*      */     {
/* 1047 */       IntArray uz = (IntArray)this.x.clone();
/*      */       
/*      */ 
/* 1050 */       IntArray vz = new IntArray(this.t);
/* 1051 */       vz.setBit(this.m);
/* 1052 */       vz.setBit(0);
/* 1053 */       vz.setBit(this.k1);
/* 1054 */       if (this.representation == 3)
/*      */       {
/* 1056 */         vz.setBit(this.k2);
/* 1057 */         vz.setBit(this.k3);
/*      */       }
/*      */       
/*      */ 
/* 1061 */       IntArray g1z = new IntArray(this.t);
/* 1062 */       g1z.setBit(0);
/* 1063 */       IntArray g2z = new IntArray(this.t);
/*      */       
/*      */ 
/* 1066 */       while (!uz.isZero())
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1071 */         int j = uz.bitLength() - vz.bitLength();
/*      */         
/*      */ 
/* 1074 */         if (j < 0)
/*      */         {
/* 1076 */           IntArray uzCopy = uz;
/* 1077 */           uz = vz;
/* 1078 */           vz = uzCopy;
/*      */           
/* 1080 */           IntArray g1zCopy = g1z;
/* 1081 */           g1z = g2z;
/* 1082 */           g2z = g1zCopy;
/*      */           
/* 1084 */           j = -j;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1094 */         int jInt = j >> 5;
/*      */         
/* 1096 */         int jBit = j & 0x1F;
/* 1097 */         IntArray vzShift = vz.shiftLeft(jBit);
/* 1098 */         uz.addShifted(vzShift, jInt);
/*      */         
/*      */ 
/*      */ 
/* 1102 */         IntArray g2zShift = g2z.shiftLeft(jBit);
/* 1103 */         g1z.addShifted(g2zShift, jInt);
/*      */       }
/*      */       
/* 1106 */       return new F2m(this.m, this.k1, this.k2, this.k3, g2z);
/*      */     }
/*      */     
/*      */ 
/*      */     public ECFieldElement sqrt()
/*      */     {
/* 1112 */       throw new RuntimeException("Not implemented");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public int getRepresentation()
/*      */     {
/* 1125 */       return this.representation;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public int getM()
/*      */     {
/* 1134 */       return this.m;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public int getK1()
/*      */     {
/* 1147 */       return this.k1;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public int getK2()
/*      */     {
/* 1158 */       return this.k2;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public int getK3()
/*      */     {
/* 1169 */       return this.k3;
/*      */     }
/*      */     
/*      */     public boolean equals(Object anObject)
/*      */     {
/* 1174 */       if (anObject == this)
/*      */       {
/* 1176 */         return true;
/*      */       }
/*      */       
/* 1179 */       if (!(anObject instanceof F2m))
/*      */       {
/* 1181 */         return false;
/*      */       }
/*      */       
/* 1184 */       F2m b = (F2m)anObject;
/*      */       
/* 1186 */       return (this.m == b.m) && (this.k1 == b.k1) && (this.k2 == b.k2) && (this.k3 == b.k3) && (this.representation == b.representation) && (this.x.equals(b.x));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 1194 */       return this.x.hashCode() ^ this.m ^ this.k1 ^ this.k2 ^ this.k3;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/math/ec/ECFieldElement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */