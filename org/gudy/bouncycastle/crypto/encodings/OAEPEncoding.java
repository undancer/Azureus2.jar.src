/*     */ package org.gudy.bouncycastle.crypto.encodings;
/*     */ 
/*     */ import java.security.SecureRandom;
/*     */ import org.gudy.bouncycastle.crypto.AsymmetricBlockCipher;
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.Digest;
/*     */ import org.gudy.bouncycastle.crypto.InvalidCipherTextException;
/*     */ import org.gudy.bouncycastle.crypto.digests.SHA1Digest;
/*     */ import org.gudy.bouncycastle.crypto.params.AsymmetricKeyParameter;
/*     */ import org.gudy.bouncycastle.crypto.params.ParametersWithRandom;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OAEPEncoding
/*     */   implements AsymmetricBlockCipher
/*     */ {
/*     */   private byte[] defHash;
/*     */   private Digest hash;
/*     */   private AsymmetricBlockCipher engine;
/*     */   private SecureRandom random;
/*     */   private boolean forEncryption;
/*     */   
/*     */   public OAEPEncoding(AsymmetricBlockCipher cipher)
/*     */   {
/*  29 */     this(cipher, new SHA1Digest(), null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public OAEPEncoding(AsymmetricBlockCipher cipher, Digest hash)
/*     */   {
/*  36 */     this(cipher, hash, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public OAEPEncoding(AsymmetricBlockCipher cipher, Digest hash, byte[] encodingParams)
/*     */   {
/*  44 */     this.engine = cipher;
/*  45 */     this.hash = hash;
/*  46 */     this.defHash = new byte[hash.getDigestSize()];
/*     */     
/*  48 */     if (encodingParams != null)
/*     */     {
/*  50 */       hash.update(encodingParams, 0, encodingParams.length);
/*     */     }
/*     */     
/*  53 */     hash.doFinal(this.defHash, 0);
/*     */   }
/*     */   
/*     */   public AsymmetricBlockCipher getUnderlyingCipher()
/*     */   {
/*  58 */     return this.engine;
/*     */   }
/*     */   
/*     */ 
/*     */   public void init(boolean forEncryption, CipherParameters param)
/*     */   {
/*     */     AsymmetricKeyParameter kParam;
/*     */     
/*     */     AsymmetricKeyParameter kParam;
/*  67 */     if ((param instanceof ParametersWithRandom))
/*     */     {
/*  69 */       ParametersWithRandom rParam = (ParametersWithRandom)param;
/*     */       
/*  71 */       this.random = rParam.getRandom();
/*  72 */       kParam = (AsymmetricKeyParameter)rParam.getParameters();
/*     */     }
/*     */     else
/*     */     {
/*  76 */       this.random = new SecureRandom();
/*  77 */       kParam = (AsymmetricKeyParameter)param;
/*     */     }
/*     */     
/*  80 */     this.engine.init(forEncryption, kParam);
/*     */     
/*  82 */     this.forEncryption = forEncryption;
/*     */   }
/*     */   
/*     */   public int getInputBlockSize()
/*     */   {
/*  87 */     int baseBlockSize = this.engine.getInputBlockSize();
/*     */     
/*  89 */     if (this.forEncryption)
/*     */     {
/*  91 */       return baseBlockSize - 1 - 2 * this.defHash.length;
/*     */     }
/*     */     
/*     */ 
/*  95 */     return baseBlockSize;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getOutputBlockSize()
/*     */   {
/* 101 */     int baseBlockSize = this.engine.getOutputBlockSize();
/*     */     
/* 103 */     if (this.forEncryption)
/*     */     {
/* 105 */       return baseBlockSize;
/*     */     }
/*     */     
/*     */ 
/* 109 */     return baseBlockSize - 1 - 2 * this.defHash.length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] processBlock(byte[] in, int inOff, int inLen)
/*     */     throws InvalidCipherTextException
/*     */   {
/* 119 */     if (this.forEncryption)
/*     */     {
/* 121 */       return encodeBlock(in, inOff, inLen);
/*     */     }
/*     */     
/*     */ 
/* 125 */     return decodeBlock(in, inOff, inLen);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] encodeBlock(byte[] in, int inOff, int inLen)
/*     */     throws InvalidCipherTextException
/*     */   {
/* 135 */     byte[] block = new byte[getInputBlockSize() + 1 + 2 * this.defHash.length];
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 140 */     System.arraycopy(in, inOff, block, block.length - inLen, inLen);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 145 */     block[(block.length - inLen - 1)] = 1;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 154 */     System.arraycopy(this.defHash, 0, block, this.defHash.length, this.defHash.length);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 159 */     byte[] seed = new byte[this.defHash.length];
/*     */     
/* 161 */     this.random.nextBytes(seed);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 166 */     byte[] mask = maskGeneratorFunction1(seed, 0, seed.length, block.length - this.defHash.length);
/*     */     
/* 168 */     for (int i = this.defHash.length; i != block.length; i++)
/*     */     {
/* 170 */       int tmp120_118 = i; byte[] tmp120_116 = block;tmp120_116[tmp120_118] = ((byte)(tmp120_116[tmp120_118] ^ mask[(i - this.defHash.length)]));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 176 */     System.arraycopy(seed, 0, block, 0, this.defHash.length);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 181 */     mask = maskGeneratorFunction1(block, this.defHash.length, block.length - this.defHash.length, this.defHash.length);
/*     */     
/*     */ 
/* 184 */     for (int i = 0; i != this.defHash.length; i++)
/*     */     {
/* 186 */       int tmp200_198 = i; byte[] tmp200_196 = block;tmp200_196[tmp200_198] = ((byte)(tmp200_196[tmp200_198] ^ mask[i]));
/*     */     }
/*     */     
/* 189 */     return this.engine.processBlock(block, 0, block.length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] decodeBlock(byte[] in, int inOff, int inLen)
/*     */     throws InvalidCipherTextException
/*     */   {
/* 202 */     byte[] data = this.engine.processBlock(in, inOff, inLen);
/* 203 */     byte[] block = null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 210 */     if (data.length < this.engine.getOutputBlockSize())
/*     */     {
/* 212 */       block = new byte[this.engine.getOutputBlockSize()];
/*     */       
/* 214 */       System.arraycopy(data, 0, block, block.length - data.length, data.length);
/*     */     }
/*     */     else
/*     */     {
/* 218 */       block = data;
/*     */     }
/*     */     
/* 221 */     if (block.length < 2 * this.defHash.length + 1)
/*     */     {
/* 223 */       throw new InvalidCipherTextException("data too short");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 229 */     byte[] mask = maskGeneratorFunction1(block, this.defHash.length, block.length - this.defHash.length, this.defHash.length);
/*     */     
/*     */ 
/* 232 */     for (int i = 0; i != this.defHash.length; i++)
/*     */     {
/* 234 */       int tmp139_137 = i; byte[] tmp139_135 = block;tmp139_135[tmp139_137] = ((byte)(tmp139_135[tmp139_137] ^ mask[i]));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 240 */     mask = maskGeneratorFunction1(block, 0, this.defHash.length, block.length - this.defHash.length);
/*     */     
/* 242 */     for (int i = this.defHash.length; i != block.length; i++)
/*     */     {
/* 244 */       int tmp197_195 = i; byte[] tmp197_193 = block;tmp197_193[tmp197_195] = ((byte)(tmp197_193[tmp197_195] ^ mask[(i - this.defHash.length)]));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 250 */     for (int i = 0; i != this.defHash.length; i++)
/*     */     {
/* 252 */       if (this.defHash[i] != block[(this.defHash.length + i)])
/*     */       {
/* 254 */         throw new InvalidCipherTextException("data hash wrong");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 263 */     for (int start = 2 * this.defHash.length; start != block.length; start++)
/*     */     {
/* 265 */       if ((block[start] == 1) || (block[start] != 0)) {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 271 */     if ((start >= block.length - 1) || (block[start] != 1))
/*     */     {
/* 273 */       throw new InvalidCipherTextException("data start wrong " + start);
/*     */     }
/*     */     
/* 276 */     start++;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 281 */     byte[] output = new byte[block.length - start];
/*     */     
/* 283 */     System.arraycopy(block, start, output, 0, output.length);
/*     */     
/* 285 */     return output;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void ItoOSP(int i, byte[] sp)
/*     */   {
/* 295 */     sp[0] = ((byte)(i >>> 24));
/* 296 */     sp[1] = ((byte)(i >>> 16));
/* 297 */     sp[2] = ((byte)(i >>> 8));
/* 298 */     sp[3] = ((byte)(i >>> 0));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] maskGeneratorFunction1(byte[] Z, int zOff, int zLen, int length)
/*     */   {
/* 310 */     byte[] mask = new byte[length];
/* 311 */     byte[] hashBuf = new byte[this.defHash.length];
/* 312 */     byte[] C = new byte[4];
/* 313 */     int counter = 0;
/*     */     
/* 315 */     this.hash.reset();
/*     */     
/*     */     do
/*     */     {
/* 319 */       ItoOSP(counter, C);
/*     */       
/* 321 */       this.hash.update(Z, zOff, zLen);
/* 322 */       this.hash.update(C, 0, C.length);
/* 323 */       this.hash.doFinal(hashBuf, 0);
/*     */       
/* 325 */       System.arraycopy(hashBuf, 0, mask, counter * this.defHash.length, this.defHash.length);
/*     */       
/* 327 */       counter++; } while (counter < length / this.defHash.length);
/*     */     
/* 329 */     if (counter * this.defHash.length < length)
/*     */     {
/* 331 */       ItoOSP(counter, C);
/*     */       
/* 333 */       this.hash.update(Z, zOff, zLen);
/* 334 */       this.hash.update(C, 0, C.length);
/* 335 */       this.hash.doFinal(hashBuf, 0);
/*     */       
/* 337 */       System.arraycopy(hashBuf, 0, mask, counter * this.defHash.length, mask.length - counter * this.defHash.length);
/*     */     }
/*     */     
/* 340 */     return mask;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/encodings/OAEPEncoding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */