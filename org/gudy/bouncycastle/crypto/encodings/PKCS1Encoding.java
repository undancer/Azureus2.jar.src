/*     */ package org.gudy.bouncycastle.crypto.encodings;
/*     */ 
/*     */ import java.security.SecureRandom;
/*     */ import org.gudy.bouncycastle.crypto.AsymmetricBlockCipher;
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.InvalidCipherTextException;
/*     */ import org.gudy.bouncycastle.crypto.params.AsymmetricKeyParameter;
/*     */ import org.gudy.bouncycastle.crypto.params.ParametersWithRandom;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PKCS1Encoding
/*     */   implements AsymmetricBlockCipher
/*     */ {
/*  18 */   private static int HEADER_LENGTH = 10;
/*     */   
/*     */   private SecureRandom random;
/*     */   
/*     */   private AsymmetricBlockCipher engine;
/*     */   private boolean forEncryption;
/*     */   private boolean forPrivateKey;
/*     */   
/*     */   public PKCS1Encoding(AsymmetricBlockCipher cipher)
/*     */   {
/*  28 */     this.engine = cipher;
/*     */   }
/*     */   
/*     */   public AsymmetricBlockCipher getUnderlyingCipher()
/*     */   {
/*  33 */     return this.engine;
/*     */   }
/*     */   
/*     */ 
/*     */   public void init(boolean forEncryption, CipherParameters param)
/*     */   {
/*     */     AsymmetricKeyParameter kParam;
/*     */     
/*     */     AsymmetricKeyParameter kParam;
/*  42 */     if ((param instanceof ParametersWithRandom))
/*     */     {
/*  44 */       ParametersWithRandom rParam = (ParametersWithRandom)param;
/*     */       
/*  46 */       this.random = rParam.getRandom();
/*  47 */       kParam = (AsymmetricKeyParameter)rParam.getParameters();
/*     */     }
/*     */     else
/*     */     {
/*  51 */       this.random = new SecureRandom();
/*  52 */       kParam = (AsymmetricKeyParameter)param;
/*     */     }
/*     */     
/*  55 */     this.engine.init(forEncryption, kParam);
/*     */     
/*  57 */     this.forPrivateKey = kParam.isPrivate();
/*  58 */     this.forEncryption = forEncryption;
/*     */   }
/*     */   
/*     */   public int getInputBlockSize()
/*     */   {
/*  63 */     int baseBlockSize = this.engine.getInputBlockSize();
/*     */     
/*  65 */     if (this.forEncryption)
/*     */     {
/*  67 */       return baseBlockSize - HEADER_LENGTH;
/*     */     }
/*     */     
/*     */ 
/*  71 */     return baseBlockSize;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getOutputBlockSize()
/*     */   {
/*  77 */     int baseBlockSize = this.engine.getOutputBlockSize();
/*     */     
/*  79 */     if (this.forEncryption)
/*     */     {
/*  81 */       return baseBlockSize;
/*     */     }
/*     */     
/*     */ 
/*  85 */     return baseBlockSize - HEADER_LENGTH;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] processBlock(byte[] in, int inOff, int inLen)
/*     */     throws InvalidCipherTextException
/*     */   {
/*  95 */     if (this.forEncryption)
/*     */     {
/*  97 */       return encodeBlock(in, inOff, inLen);
/*     */     }
/*     */     
/*     */ 
/* 101 */     return decodeBlock(in, inOff, inLen);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] encodeBlock(byte[] in, int inOff, int inLen)
/*     */     throws InvalidCipherTextException
/*     */   {
/* 111 */     byte[] block = new byte[this.engine.getInputBlockSize()];
/*     */     
/* 113 */     if (this.forPrivateKey)
/*     */     {
/* 115 */       block[0] = 1;
/*     */       
/* 117 */       for (int i = 1; i != block.length - inLen - 1; i++)
/*     */       {
/* 119 */         block[i] = -1;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 124 */       this.random.nextBytes(block);
/*     */       
/* 126 */       block[0] = 2;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 132 */       for (int i = 1; i != block.length - inLen - 1; i++)
/*     */       {
/* 134 */         while (block[i] == 0)
/*     */         {
/* 136 */           block[i] = ((byte)this.random.nextInt());
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 141 */     block[(block.length - inLen - 1)] = 0;
/* 142 */     System.arraycopy(in, inOff, block, block.length - inLen, inLen);
/*     */     
/* 144 */     return this.engine.processBlock(block, 0, block.length);
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
/* 156 */     byte[] block = this.engine.processBlock(in, inOff, inLen);
/*     */     
/* 158 */     if (block.length < getOutputBlockSize())
/*     */     {
/* 160 */       throw new InvalidCipherTextException("block truncated");
/*     */     }
/*     */     
/* 163 */     if ((block[0] != 1) && (block[0] != 2))
/*     */     {
/* 165 */       throw new InvalidCipherTextException("unknown block type");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 173 */     for (int start = 1; start != block.length; start++)
/*     */     {
/* 175 */       if (block[start] == 0) {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 181 */     start++;
/*     */     
/* 183 */     if ((start >= block.length) || (start < HEADER_LENGTH))
/*     */     {
/* 185 */       throw new InvalidCipherTextException("no data in block");
/*     */     }
/*     */     
/* 188 */     byte[] result = new byte[block.length - start];
/*     */     
/* 190 */     System.arraycopy(block, start, result, 0, result.length);
/*     */     
/* 192 */     return result;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/encodings/PKCS1Encoding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */