/*     */ package org.gudy.bouncycastle.math.ec;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import org.gudy.bouncycastle.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class IntArray
/*     */ {
/*     */   private int[] m_ints;
/*     */   
/*     */   public IntArray(int intLen)
/*     */   {
/*  16 */     this.m_ints = new int[intLen];
/*     */   }
/*     */   
/*     */   public IntArray(int[] ints)
/*     */   {
/*  21 */     this.m_ints = ints;
/*     */   }
/*     */   
/*     */   public IntArray(BigInteger bigInt)
/*     */   {
/*  26 */     this(bigInt, 0);
/*     */   }
/*     */   
/*     */   public IntArray(BigInteger bigInt, int minIntLen)
/*     */   {
/*  31 */     if (bigInt.signum() == -1)
/*     */     {
/*  33 */       throw new IllegalArgumentException("Only positive Integers allowed");
/*     */     }
/*  35 */     if (bigInt.equals(ECConstants.ZERO))
/*     */     {
/*  37 */       this.m_ints = new int[] { 0 };
/*  38 */       return;
/*     */     }
/*     */     
/*  41 */     byte[] barr = bigInt.toByteArray();
/*  42 */     int barrLen = barr.length;
/*  43 */     int barrStart = 0;
/*  44 */     if (barr[0] == 0)
/*     */     {
/*     */ 
/*     */ 
/*  48 */       barrLen--;
/*  49 */       barrStart = 1;
/*     */     }
/*  51 */     int intLen = (barrLen + 3) / 4;
/*  52 */     if (intLen < minIntLen)
/*     */     {
/*  54 */       this.m_ints = new int[minIntLen];
/*     */     }
/*     */     else
/*     */     {
/*  58 */       this.m_ints = new int[intLen];
/*     */     }
/*     */     
/*  61 */     int iarrJ = intLen - 1;
/*  62 */     int rem = barrLen % 4 + barrStart;
/*  63 */     int temp = 0;
/*  64 */     int barrI = barrStart;
/*  65 */     if (barrStart < rem)
/*     */     {
/*  67 */       for (; barrI < rem; barrI++)
/*     */       {
/*  69 */         temp <<= 8;
/*  70 */         int barrBarrI = barr[barrI];
/*  71 */         if (barrBarrI < 0)
/*     */         {
/*  73 */           barrBarrI += 256;
/*     */         }
/*  75 */         temp |= barrBarrI;
/*     */       }
/*  77 */       this.m_ints[(iarrJ--)] = temp;
/*     */     }
/*  80 */     for (; 
/*  80 */         iarrJ >= 0; iarrJ--)
/*     */     {
/*  82 */       temp = 0;
/*  83 */       for (int i = 0; i < 4; i++)
/*     */       {
/*  85 */         temp <<= 8;
/*  86 */         int barrBarrI = barr[(barrI++)];
/*  87 */         if (barrBarrI < 0)
/*     */         {
/*  89 */           barrBarrI += 256;
/*     */         }
/*  91 */         temp |= barrBarrI;
/*     */       }
/*  93 */       this.m_ints[iarrJ] = temp;
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isZero()
/*     */   {
/*  99 */     return (this.m_ints.length == 0) || ((this.m_ints[0] == 0) && (getUsedLength() == 0));
/*     */   }
/*     */   
/*     */ 
/*     */   public int getUsedLength()
/*     */   {
/* 105 */     int highestIntPos = this.m_ints.length;
/*     */     
/* 107 */     if (highestIntPos < 1)
/*     */     {
/* 109 */       return 0;
/*     */     }
/*     */     
/*     */ 
/* 113 */     if (this.m_ints[0] != 0)
/*     */     {
/* 115 */       while (this.m_ints[(--highestIntPos)] == 0) {}
/*     */       
/*     */ 
/* 118 */       return highestIntPos + 1;
/*     */     }
/*     */     
/*     */     do
/*     */     {
/* 123 */       if (this.m_ints[(--highestIntPos)] != 0)
/*     */       {
/* 125 */         return highestIntPos + 1;
/*     */       }
/*     */       
/* 128 */     } while (highestIntPos > 0);
/*     */     
/* 130 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int bitLength()
/*     */   {
/* 136 */     int intLen = getUsedLength();
/* 137 */     if (intLen == 0)
/*     */     {
/* 139 */       return 0;
/*     */     }
/*     */     
/* 142 */     int last = intLen - 1;
/* 143 */     int highest = this.m_ints[last];
/* 144 */     int bits = (last << 5) + 1;
/*     */     
/*     */ 
/* 147 */     if ((highest & 0xFFFF0000) != 0)
/*     */     {
/* 149 */       if ((highest & 0xFF000000) != 0)
/*     */       {
/* 151 */         bits += 24;
/* 152 */         highest >>>= 24;
/*     */       }
/*     */       else
/*     */       {
/* 156 */         bits += 16;
/* 157 */         highest >>>= 16;
/*     */       }
/*     */     }
/* 160 */     else if (highest > 255)
/*     */     {
/* 162 */       bits += 8;
/* 163 */       highest >>>= 8;
/*     */     }
/*     */     
/* 166 */     while (highest != 1)
/*     */     {
/* 168 */       bits++;
/* 169 */       highest >>>= 1;
/*     */     }
/*     */     
/* 172 */     return bits;
/*     */   }
/*     */   
/*     */   private int[] resizedInts(int newLen)
/*     */   {
/* 177 */     int[] newInts = new int[newLen];
/* 178 */     int oldLen = this.m_ints.length;
/* 179 */     int copyLen = oldLen < newLen ? oldLen : newLen;
/* 180 */     System.arraycopy(this.m_ints, 0, newInts, 0, copyLen);
/* 181 */     return newInts;
/*     */   }
/*     */   
/*     */   public BigInteger toBigInteger()
/*     */   {
/* 186 */     int usedLen = getUsedLength();
/* 187 */     if (usedLen == 0)
/*     */     {
/* 189 */       return ECConstants.ZERO;
/*     */     }
/*     */     
/* 192 */     int highestInt = this.m_ints[(usedLen - 1)];
/* 193 */     byte[] temp = new byte[4];
/* 194 */     int barrI = 0;
/* 195 */     boolean trailingZeroBytesDone = false;
/* 196 */     for (int j = 3; j >= 0; j--)
/*     */     {
/* 198 */       byte thisByte = (byte)(highestInt >>> 8 * j);
/* 199 */       if ((trailingZeroBytesDone) || (thisByte != 0))
/*     */       {
/* 201 */         trailingZeroBytesDone = true;
/* 202 */         temp[(barrI++)] = thisByte;
/*     */       }
/*     */     }
/*     */     
/* 206 */     int barrLen = 4 * (usedLen - 1) + barrI;
/* 207 */     byte[] barr = new byte[barrLen];
/* 208 */     System.arraycopy(temp, 0, barr, 0, barrI);
/*     */     
/*     */ 
/* 211 */     for (int iarrJ = usedLen - 2; iarrJ >= 0; iarrJ--)
/*     */     {
/* 213 */       for (int j = 3; j >= 0; j--)
/*     */       {
/* 215 */         barr[(barrI++)] = ((byte)(this.m_ints[iarrJ] >>> 8 * j));
/*     */       }
/*     */     }
/* 218 */     return new BigInteger(1, barr);
/*     */   }
/*     */   
/*     */   public void shiftLeft()
/*     */   {
/* 223 */     int usedLen = getUsedLength();
/* 224 */     if (usedLen == 0)
/*     */     {
/* 226 */       return;
/*     */     }
/* 228 */     if (this.m_ints[(usedLen - 1)] < 0)
/*     */     {
/*     */ 
/*     */ 
/* 232 */       usedLen++;
/* 233 */       if (usedLen > this.m_ints.length)
/*     */       {
/*     */ 
/*     */ 
/* 237 */         this.m_ints = resizedInts(this.m_ints.length + 1);
/*     */       }
/*     */     }
/*     */     
/* 241 */     boolean carry = false;
/* 242 */     for (int i = 0; i < usedLen; i++)
/*     */     {
/*     */ 
/* 245 */       boolean nextCarry = this.m_ints[i] < 0;
/* 246 */       this.m_ints[i] <<= 1;
/* 247 */       if (carry)
/*     */       {
/*     */ 
/* 250 */         this.m_ints[i] |= 0x1;
/*     */       }
/* 252 */       carry = nextCarry;
/*     */     }
/*     */   }
/*     */   
/*     */   public IntArray shiftLeft(int n)
/*     */   {
/* 258 */     int usedLen = getUsedLength();
/* 259 */     if (usedLen == 0)
/*     */     {
/* 261 */       return this;
/*     */     }
/*     */     
/* 264 */     if (n == 0)
/*     */     {
/* 266 */       return this;
/*     */     }
/*     */     
/* 269 */     if (n > 31)
/*     */     {
/* 271 */       throw new IllegalArgumentException("shiftLeft() for max 31 bits , " + n + "bit shift is not possible");
/*     */     }
/*     */     
/*     */ 
/* 275 */     int[] newInts = new int[usedLen + 1];
/*     */     
/* 277 */     int nm32 = 32 - n;
/* 278 */     newInts[0] = (this.m_ints[0] << n);
/* 279 */     for (int i = 1; i < usedLen; i++)
/*     */     {
/* 281 */       newInts[i] = (this.m_ints[i] << n | this.m_ints[(i - 1)] >>> nm32);
/*     */     }
/* 283 */     newInts[usedLen] = (this.m_ints[(usedLen - 1)] >>> nm32);
/*     */     
/* 285 */     return new IntArray(newInts);
/*     */   }
/*     */   
/*     */   public void addShifted(IntArray other, int shift)
/*     */   {
/* 290 */     int usedLenOther = other.getUsedLength();
/* 291 */     int newMinUsedLen = usedLenOther + shift;
/* 292 */     if (newMinUsedLen > this.m_ints.length)
/*     */     {
/* 294 */       this.m_ints = resizedInts(newMinUsedLen);
/*     */     }
/*     */     
/*     */ 
/* 298 */     for (int i = 0; i < usedLenOther; i++)
/*     */     {
/* 300 */       this.m_ints[(i + shift)] ^= other.m_ints[i];
/*     */     }
/*     */   }
/*     */   
/*     */   public int getLength()
/*     */   {
/* 306 */     return this.m_ints.length;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean testBit(int n)
/*     */   {
/* 312 */     int theInt = n >> 5;
/*     */     
/* 314 */     int theBit = n & 0x1F;
/* 315 */     int tester = 1 << theBit;
/* 316 */     return (this.m_ints[theInt] & tester) != 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public void flipBit(int n)
/*     */   {
/* 322 */     int theInt = n >> 5;
/*     */     
/* 324 */     int theBit = n & 0x1F;
/* 325 */     int flipper = 1 << theBit;
/* 326 */     this.m_ints[theInt] ^= flipper;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setBit(int n)
/*     */   {
/* 332 */     int theInt = n >> 5;
/*     */     
/* 334 */     int theBit = n & 0x1F;
/* 335 */     int setter = 1 << theBit;
/* 336 */     this.m_ints[theInt] |= setter;
/*     */   }
/*     */   
/*     */ 
/*     */   public IntArray multiply(IntArray other, int m)
/*     */   {
/* 342 */     int t = m + 31 >> 5;
/* 343 */     if (this.m_ints.length < t)
/*     */     {
/* 345 */       this.m_ints = resizedInts(t);
/*     */     }
/*     */     
/* 348 */     IntArray b = new IntArray(other.resizedInts(other.getLength() + 1));
/* 349 */     IntArray c = new IntArray(m + m + 31 >> 5);
/*     */     
/* 351 */     int testBit = 1;
/* 352 */     for (int k = 0; k < 32; k++)
/*     */     {
/* 354 */       for (int j = 0; j < t; j++)
/*     */       {
/* 356 */         if ((this.m_ints[j] & testBit) != 0)
/*     */         {
/*     */ 
/* 359 */           c.addShifted(b, j);
/*     */         }
/*     */       }
/* 362 */       testBit <<= 1;
/* 363 */       b.shiftLeft();
/*     */     }
/* 365 */     return c;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reduce(int m, int[] redPol)
/*     */   {
/* 397 */     for (int i = m + m - 2; i >= m; i--)
/*     */     {
/* 399 */       if (testBit(i))
/*     */       {
/* 401 */         int bit = i - m;
/* 402 */         flipBit(bit);
/* 403 */         flipBit(i);
/* 404 */         int l = redPol.length;
/* 405 */         for (;;) { l--; if (l < 0)
/*     */             break;
/* 407 */           flipBit(redPol[l] + bit);
/*     */         }
/*     */       }
/*     */     }
/* 411 */     this.m_ints = resizedInts(m + 31 >> 5);
/*     */   }
/*     */   
/*     */ 
/*     */   public IntArray square(int m)
/*     */   {
/* 417 */     int[] table = { 0, 1, 4, 5, 16, 17, 20, 21, 64, 65, 68, 69, 80, 81, 84, 85 };
/*     */     
/*     */ 
/* 420 */     int t = m + 31 >> 5;
/* 421 */     if (this.m_ints.length < t)
/*     */     {
/* 423 */       this.m_ints = resizedInts(t);
/*     */     }
/*     */     
/* 426 */     IntArray c = new IntArray(t + t);
/*     */     
/*     */ 
/* 429 */     for (int i = 0; i < t; i++)
/*     */     {
/* 431 */       int v0 = 0;
/* 432 */       for (int j = 0; j < 4; j++)
/*     */       {
/* 434 */         v0 >>>= 8;
/* 435 */         int u = this.m_ints[i] >>> j * 4 & 0xF;
/* 436 */         int w = table[u] << 24;
/* 437 */         v0 |= w;
/*     */       }
/* 439 */       c.m_ints[(i + i)] = v0;
/*     */       
/* 441 */       v0 = 0;
/* 442 */       int upper = this.m_ints[i] >>> 16;
/* 443 */       for (int j = 0; j < 4; j++)
/*     */       {
/* 445 */         v0 >>>= 8;
/* 446 */         int u = upper >>> j * 4 & 0xF;
/* 447 */         int w = table[u] << 24;
/* 448 */         v0 |= w;
/*     */       }
/* 450 */       c.m_ints[(i + i + 1)] = v0;
/*     */     }
/* 452 */     return c;
/*     */   }
/*     */   
/*     */   public boolean equals(Object o)
/*     */   {
/* 457 */     if (!(o instanceof IntArray))
/*     */     {
/* 459 */       return false;
/*     */     }
/* 461 */     IntArray other = (IntArray)o;
/* 462 */     int usedLen = getUsedLength();
/* 463 */     if (other.getUsedLength() != usedLen)
/*     */     {
/* 465 */       return false;
/*     */     }
/* 467 */     for (int i = 0; i < usedLen; i++)
/*     */     {
/* 469 */       if (this.m_ints[i] != other.m_ints[i])
/*     */       {
/* 471 */         return false;
/*     */       }
/*     */     }
/* 474 */     return true;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 479 */     int usedLen = getUsedLength();
/* 480 */     int hash = 0;
/* 481 */     for (int i = 0; i < usedLen; i++)
/*     */     {
/* 483 */       hash ^= this.m_ints[i];
/*     */     }
/* 485 */     return hash;
/*     */   }
/*     */   
/*     */   public Object clone()
/*     */   {
/* 490 */     return new IntArray(Arrays.clone(this.m_ints));
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 495 */     int usedLen = getUsedLength();
/* 496 */     if (usedLen == 0)
/*     */     {
/* 498 */       return "0";
/*     */     }
/*     */     
/* 501 */     StringBuilder sb = new StringBuilder(Integer.toBinaryString(this.m_ints[(usedLen - 1)]));
/*     */     
/* 503 */     for (int iarrJ = usedLen - 2; iarrJ >= 0; iarrJ--)
/*     */     {
/* 505 */       String hexString = Integer.toBinaryString(this.m_ints[iarrJ]);
/*     */       
/*     */ 
/* 508 */       for (int i = hexString.length(); i < 8; i++)
/*     */       {
/* 510 */         hexString = "0" + hexString;
/*     */       }
/* 512 */       sb.append(hexString);
/*     */     }
/* 514 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/math/ec/IntArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */