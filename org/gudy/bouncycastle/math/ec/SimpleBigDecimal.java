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
/*     */ class SimpleBigDecimal
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final BigInteger bigInt;
/*     */   private final int scale;
/*     */   
/*     */   public static SimpleBigDecimal getInstance(BigInteger value, int scale)
/*     */   {
/*  38 */     return new SimpleBigDecimal(value.shiftLeft(scale), scale);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SimpleBigDecimal(BigInteger bigInt, int scale)
/*     */   {
/*  50 */     if (scale < 0)
/*     */     {
/*  52 */       throw new IllegalArgumentException("scale may not be negative");
/*     */     }
/*     */     
/*  55 */     this.bigInt = bigInt;
/*  56 */     this.scale = scale;
/*     */   }
/*     */   
/*     */   private SimpleBigDecimal(SimpleBigDecimal limBigDec)
/*     */   {
/*  61 */     this.bigInt = limBigDec.bigInt;
/*  62 */     this.scale = limBigDec.scale;
/*     */   }
/*     */   
/*     */   private void checkScale(SimpleBigDecimal b)
/*     */   {
/*  67 */     if (this.scale != b.scale)
/*     */     {
/*  69 */       throw new IllegalArgumentException("Only SimpleBigDecimal of same scale allowed in arithmetic operations");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public SimpleBigDecimal adjustScale(int newScale)
/*     */   {
/*  76 */     if (newScale < 0)
/*     */     {
/*  78 */       throw new IllegalArgumentException("scale may not be negative");
/*     */     }
/*     */     
/*  81 */     if (newScale == this.scale)
/*     */     {
/*  83 */       return new SimpleBigDecimal(this);
/*     */     }
/*     */     
/*  86 */     return new SimpleBigDecimal(this.bigInt.shiftLeft(newScale - this.scale), newScale);
/*     */   }
/*     */   
/*     */ 
/*     */   public SimpleBigDecimal add(SimpleBigDecimal b)
/*     */   {
/*  92 */     checkScale(b);
/*  93 */     return new SimpleBigDecimal(this.bigInt.add(b.bigInt), this.scale);
/*     */   }
/*     */   
/*     */   public SimpleBigDecimal add(BigInteger b)
/*     */   {
/*  98 */     return new SimpleBigDecimal(this.bigInt.add(b.shiftLeft(this.scale)), this.scale);
/*     */   }
/*     */   
/*     */   public SimpleBigDecimal negate()
/*     */   {
/* 103 */     return new SimpleBigDecimal(this.bigInt.negate(), this.scale);
/*     */   }
/*     */   
/*     */   public SimpleBigDecimal subtract(SimpleBigDecimal b)
/*     */   {
/* 108 */     return add(b.negate());
/*     */   }
/*     */   
/*     */   public SimpleBigDecimal subtract(BigInteger b)
/*     */   {
/* 113 */     return new SimpleBigDecimal(this.bigInt.subtract(b.shiftLeft(this.scale)), this.scale);
/*     */   }
/*     */   
/*     */ 
/*     */   public SimpleBigDecimal multiply(SimpleBigDecimal b)
/*     */   {
/* 119 */     checkScale(b);
/* 120 */     return new SimpleBigDecimal(this.bigInt.multiply(b.bigInt), this.scale + this.scale);
/*     */   }
/*     */   
/*     */   public SimpleBigDecimal multiply(BigInteger b)
/*     */   {
/* 125 */     return new SimpleBigDecimal(this.bigInt.multiply(b), this.scale);
/*     */   }
/*     */   
/*     */   public SimpleBigDecimal divide(SimpleBigDecimal b)
/*     */   {
/* 130 */     checkScale(b);
/* 131 */     BigInteger dividend = this.bigInt.shiftLeft(this.scale);
/* 132 */     return new SimpleBigDecimal(dividend.divide(b.bigInt), this.scale);
/*     */   }
/*     */   
/*     */   public SimpleBigDecimal divide(BigInteger b)
/*     */   {
/* 137 */     return new SimpleBigDecimal(this.bigInt.divide(b), this.scale);
/*     */   }
/*     */   
/*     */   public SimpleBigDecimal shiftLeft(int n)
/*     */   {
/* 142 */     return new SimpleBigDecimal(this.bigInt.shiftLeft(n), this.scale);
/*     */   }
/*     */   
/*     */   public int compareTo(SimpleBigDecimal val)
/*     */   {
/* 147 */     checkScale(val);
/* 148 */     return this.bigInt.compareTo(val.bigInt);
/*     */   }
/*     */   
/*     */   public int compareTo(BigInteger val)
/*     */   {
/* 153 */     return this.bigInt.compareTo(val.shiftLeft(this.scale));
/*     */   }
/*     */   
/*     */   public BigInteger floor()
/*     */   {
/* 158 */     return this.bigInt.shiftRight(this.scale);
/*     */   }
/*     */   
/*     */   public BigInteger round()
/*     */   {
/* 163 */     SimpleBigDecimal oneHalf = new SimpleBigDecimal(ECConstants.ONE, 1);
/* 164 */     return add(oneHalf.adjustScale(this.scale)).floor();
/*     */   }
/*     */   
/*     */   public int intValue()
/*     */   {
/* 169 */     return floor().intValue();
/*     */   }
/*     */   
/*     */   public long longValue()
/*     */   {
/* 174 */     return floor().longValue();
/*     */   }
/*     */   
/*     */   public double doubleValue()
/*     */   {
/* 179 */     return Double.valueOf(toString()).doubleValue();
/*     */   }
/*     */   
/*     */   public float floatValue()
/*     */   {
/* 184 */     return Float.valueOf(toString()).floatValue();
/*     */   }
/*     */   
/*     */   public int getScale()
/*     */   {
/* 189 */     return this.scale;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 194 */     if (this.scale == 0)
/*     */     {
/* 196 */       return this.bigInt.toString();
/*     */     }
/*     */     
/* 199 */     BigInteger floorBigInt = floor();
/*     */     
/* 201 */     BigInteger fract = this.bigInt.subtract(floorBigInt.shiftLeft(this.scale));
/* 202 */     if (this.bigInt.signum() == -1)
/*     */     {
/* 204 */       fract = ECConstants.ONE.shiftLeft(this.scale).subtract(fract);
/*     */     }
/*     */     
/* 207 */     if ((floorBigInt.signum() == -1) && (!fract.equals(ECConstants.ZERO)))
/*     */     {
/* 209 */       floorBigInt = floorBigInt.add(ECConstants.ONE);
/*     */     }
/* 211 */     String leftOfPoint = floorBigInt.toString();
/*     */     
/* 213 */     char[] fractCharArr = new char[this.scale];
/* 214 */     String fractStr = fract.toString(2);
/* 215 */     int fractLen = fractStr.length();
/* 216 */     int zeroes = this.scale - fractLen;
/* 217 */     for (int i = 0; i < zeroes; i++)
/*     */     {
/* 219 */       fractCharArr[i] = '0';
/*     */     }
/* 221 */     for (int j = 0; j < fractLen; j++)
/*     */     {
/* 223 */       fractCharArr[(zeroes + j)] = fractStr.charAt(j);
/*     */     }
/* 225 */     String rightOfPoint = new String(fractCharArr);
/*     */     
/* 227 */     StringBuilder sb = new StringBuilder(leftOfPoint);
/* 228 */     sb.append(".");
/* 229 */     sb.append(rightOfPoint);
/*     */     
/* 231 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public boolean equals(Object o)
/*     */   {
/* 236 */     if (this == o)
/*     */     {
/* 238 */       return true;
/*     */     }
/*     */     
/* 241 */     if (!(o instanceof SimpleBigDecimal))
/*     */     {
/* 243 */       return false;
/*     */     }
/*     */     
/* 246 */     SimpleBigDecimal other = (SimpleBigDecimal)o;
/* 247 */     return (this.bigInt.equals(other.bigInt)) && (this.scale == other.scale);
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 252 */     return this.bigInt.hashCode() ^ this.scale;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/math/ec/SimpleBigDecimal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */