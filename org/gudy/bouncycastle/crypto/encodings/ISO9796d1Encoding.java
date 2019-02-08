/*     */ package org.gudy.bouncycastle.crypto.encodings;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import org.gudy.bouncycastle.crypto.AsymmetricBlockCipher;
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.InvalidCipherTextException;
/*     */ import org.gudy.bouncycastle.crypto.params.ParametersWithRandom;
/*     */ import org.gudy.bouncycastle.crypto.params.RSAKeyParameters;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ISO9796d1Encoding
/*     */   implements AsymmetricBlockCipher
/*     */ {
/*  19 */   private static byte[] shadows = { 14, 3, 5, 8, 9, 4, 2, 15, 0, 13, 11, 6, 7, 10, 12, 1 };
/*     */   
/*  21 */   private static byte[] inverse = { 8, 15, 6, 1, 5, 2, 11, 12, 3, 4, 13, 10, 14, 9, 0, 7 };
/*     */   
/*     */   private AsymmetricBlockCipher engine;
/*     */   
/*     */   private boolean forEncryption;
/*     */   private int bitSize;
/*  27 */   private int padBits = 0;
/*     */   
/*     */ 
/*     */   public ISO9796d1Encoding(AsymmetricBlockCipher cipher)
/*     */   {
/*  32 */     this.engine = cipher;
/*     */   }
/*     */   
/*     */   public AsymmetricBlockCipher getUnderlyingCipher()
/*     */   {
/*  37 */     return this.engine;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void init(boolean forEncryption, CipherParameters param)
/*     */   {
/*  44 */     RSAKeyParameters kParam = null;
/*     */     
/*  46 */     if ((param instanceof ParametersWithRandom))
/*     */     {
/*  48 */       ParametersWithRandom rParam = (ParametersWithRandom)param;
/*     */       
/*  50 */       kParam = (RSAKeyParameters)rParam.getParameters();
/*     */     }
/*     */     else
/*     */     {
/*  54 */       kParam = (RSAKeyParameters)param;
/*     */     }
/*     */     
/*  57 */     this.engine.init(forEncryption, kParam);
/*     */     
/*  59 */     this.bitSize = kParam.getModulus().bitLength();
/*     */     
/*  61 */     this.forEncryption = forEncryption;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getInputBlockSize()
/*     */   {
/*  71 */     int baseBlockSize = this.engine.getInputBlockSize();
/*     */     
/*  73 */     if (this.forEncryption)
/*     */     {
/*  75 */       return (baseBlockSize + 1) / 2;
/*     */     }
/*     */     
/*     */ 
/*  79 */     return baseBlockSize;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getOutputBlockSize()
/*     */   {
/*  88 */     int baseBlockSize = this.engine.getOutputBlockSize();
/*     */     
/*  90 */     if (this.forEncryption)
/*     */     {
/*  92 */       return baseBlockSize;
/*     */     }
/*     */     
/*     */ 
/*  96 */     return (baseBlockSize + 1) / 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPadBits(int padBits)
/*     */   {
/* 107 */     if (padBits > 7)
/*     */     {
/* 109 */       throw new IllegalArgumentException("padBits > 7");
/*     */     }
/*     */     
/* 112 */     this.padBits = padBits;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPadBits()
/*     */   {
/* 120 */     return this.padBits;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] processBlock(byte[] in, int inOff, int inLen)
/*     */     throws InvalidCipherTextException
/*     */   {
/* 129 */     if (this.forEncryption)
/*     */     {
/* 131 */       return encodeBlock(in, inOff, inLen);
/*     */     }
/*     */     
/*     */ 
/* 135 */     return decodeBlock(in, inOff, inLen);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] encodeBlock(byte[] in, int inOff, int inLen)
/*     */     throws InvalidCipherTextException
/*     */   {
/* 145 */     byte[] block = new byte[(this.bitSize + 7) / 8];
/* 146 */     int r = this.padBits + 1;
/* 147 */     int z = inLen;
/* 148 */     int t = (this.bitSize + 13) / 16;
/*     */     
/* 150 */     for (int i = 0; i < t; i += z)
/*     */     {
/* 152 */       if (i > t - z)
/*     */       {
/* 154 */         System.arraycopy(in, inOff + inLen - (t - i), block, block.length - t, t - i);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 159 */         System.arraycopy(in, inOff, block, block.length - (i + z), z);
/*     */       }
/*     */     }
/*     */     
/* 163 */     for (int i = block.length - 2 * t; i != block.length; i += 2)
/*     */     {
/* 165 */       byte val = block[(block.length - t + i / 2)];
/*     */       
/* 167 */       block[i] = ((byte)(shadows[((val & 0xFF) >>> 4)] << 4 | shadows[(val & 0xF)]));
/*     */       
/* 169 */       block[(i + 1)] = val;
/*     */     }
/*     */     
/* 172 */     int tmp203_202 = (block.length - 2 * z); byte[] tmp203_193 = block;tmp203_193[tmp203_202] = ((byte)(tmp203_193[tmp203_202] ^ r));
/* 173 */     block[(block.length - 1)] = ((byte)(block[(block.length - 1)] << 4 | 0x6));
/*     */     
/* 175 */     int maxBit = 8 - (this.bitSize - 1) % 8;
/* 176 */     int offSet = 0;
/*     */     
/* 178 */     if (maxBit != 8)
/*     */     {
/* 180 */       int tmp259_258 = 0; byte[] tmp259_256 = block;tmp259_256[tmp259_258] = ((byte)(tmp259_256[tmp259_258] & 255 >>> maxBit)); int 
/* 181 */         tmp273_272 = 0; byte[] tmp273_270 = block;tmp273_270[tmp273_272] = ((byte)(tmp273_270[tmp273_272] | 128 >>> maxBit));
/*     */     }
/*     */     else
/*     */     {
/* 185 */       block[0] = 0; int 
/* 186 */         tmp295_294 = 1; byte[] tmp295_292 = block;tmp295_292[tmp295_294] = ((byte)(tmp295_292[tmp295_294] | 0x80));
/* 187 */       offSet = 1;
/*     */     }
/*     */     
/* 190 */     return this.engine.processBlock(block, offSet, block.length - offSet);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] decodeBlock(byte[] in, int inOff, int inLen)
/*     */     throws InvalidCipherTextException
/*     */   {
/* 202 */     byte[] block = this.engine.processBlock(in, inOff, inLen);
/* 203 */     int r = 1;
/* 204 */     int t = (this.bitSize + 13) / 16;
/*     */     
/* 206 */     if ((block[(block.length - 1)] & 0xF) != 6)
/*     */     {
/* 208 */       throw new InvalidCipherTextException("invalid forcing byte in block");
/*     */     }
/*     */     
/* 211 */     block[(block.length - 1)] = ((byte)((block[(block.length - 1)] & 0xFF) >>> 4 | inverse[((block[(block.length - 2)] & 0xFF) >> 4)] << 4));
/* 212 */     block[0] = ((byte)(shadows[((block[1] & 0xFF) >>> 4)] << 4 | shadows[(block[1] & 0xF)]));
/*     */     
/*     */ 
/* 215 */     boolean boundaryFound = false;
/* 216 */     int boundary = 0;
/*     */     
/* 218 */     for (int i = block.length - 1; i >= block.length - 2 * t; i -= 2)
/*     */     {
/* 220 */       int val = shadows[((block[i] & 0xFF) >>> 4)] << 4 | shadows[(block[i] & 0xF)];
/*     */       
/*     */ 
/* 223 */       if (((block[(i - 1)] ^ val) & 0xFF) != 0)
/*     */       {
/* 225 */         if (!boundaryFound)
/*     */         {
/* 227 */           boundaryFound = true;
/* 228 */           r = (block[(i - 1)] ^ val) & 0xFF;
/* 229 */           boundary = i - 1;
/*     */         }
/*     */         else
/*     */         {
/* 233 */           throw new InvalidCipherTextException("invalid tsums in block");
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 238 */     block[boundary] = 0;
/*     */     
/* 240 */     byte[] nblock = new byte[(block.length - boundary) / 2];
/*     */     
/* 242 */     for (int i = 0; i < nblock.length; i++)
/*     */     {
/* 244 */       nblock[i] = block[(2 * i + boundary + 1)];
/*     */     }
/*     */     
/* 247 */     this.padBits = (r - 1);
/*     */     
/* 249 */     return nblock;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/encodings/ISO9796d1Encoding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */