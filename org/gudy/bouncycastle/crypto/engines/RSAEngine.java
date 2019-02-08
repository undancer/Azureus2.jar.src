/*     */ package org.gudy.bouncycastle.crypto.engines;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import org.gudy.bouncycastle.crypto.AsymmetricBlockCipher;
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.DataLengthException;
/*     */ import org.gudy.bouncycastle.crypto.params.RSAKeyParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
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
/*     */ public class RSAEngine
/*     */   implements AsymmetricBlockCipher
/*     */ {
/*     */   private RSAKeyParameters key;
/*     */   private boolean forEncryption;
/*     */   
/*     */   public void init(boolean forEncryption, CipherParameters param)
/*     */   {
/*  30 */     this.key = ((RSAKeyParameters)param);
/*  31 */     this.forEncryption = forEncryption;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getInputBlockSize()
/*     */   {
/*  43 */     int bitSize = this.key.getModulus().bitLength();
/*     */     
/*  45 */     if (this.forEncryption)
/*     */     {
/*  47 */       return (bitSize + 7) / 8 - 1;
/*     */     }
/*     */     
/*     */ 
/*  51 */     return (bitSize + 7) / 8;
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
/*     */   public int getOutputBlockSize()
/*     */   {
/*  64 */     int bitSize = this.key.getModulus().bitLength();
/*     */     
/*  66 */     if (this.forEncryption)
/*     */     {
/*  68 */       return (bitSize + 7) / 8;
/*     */     }
/*     */     
/*     */ 
/*  72 */     return (bitSize + 7) / 8 - 1;
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
/*     */   public byte[] processBlock(byte[] in, int inOff, int inLen)
/*     */   {
/*  90 */     if (inLen > getInputBlockSize() + 1)
/*     */     {
/*  92 */       throw new DataLengthException("input too large for RSA cipher.\n");
/*     */     }
/*  94 */     if ((inLen == getInputBlockSize() + 1) && ((in[inOff] & 0x80) != 0))
/*     */     {
/*  96 */       throw new DataLengthException("input too large for RSA cipher.\n");
/*     */     }
/*     */     
/*     */     byte[] block;
/*     */     
/* 101 */     if ((inOff != 0) || (inLen != in.length))
/*     */     {
/* 103 */       byte[] block = new byte[inLen];
/*     */       
/* 105 */       System.arraycopy(in, inOff, block, 0, inLen);
/*     */     }
/*     */     else
/*     */     {
/* 109 */       block = in;
/*     */     }
/*     */     
/* 112 */     BigInteger input = new BigInteger(1, block);
/*     */     byte[] output;
/*     */     byte[] output;
/* 115 */     if ((this.key instanceof RSAPrivateCrtKeyParameters))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 122 */       RSAPrivateCrtKeyParameters crtKey = (RSAPrivateCrtKeyParameters)this.key;
/*     */       
/* 124 */       BigInteger p = crtKey.getP();
/* 125 */       BigInteger q = crtKey.getQ();
/* 126 */       BigInteger dP = crtKey.getDP();
/* 127 */       BigInteger dQ = crtKey.getDQ();
/* 128 */       BigInteger qInv = crtKey.getQInv();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 133 */       BigInteger mP = input.remainder(p).modPow(dP, p);
/*     */       
/*     */ 
/* 136 */       BigInteger mQ = input.remainder(q).modPow(dQ, q);
/*     */       
/*     */ 
/* 139 */       BigInteger h = mP.subtract(mQ);
/* 140 */       h = h.multiply(qInv);
/* 141 */       h = h.mod(p);
/*     */       
/*     */ 
/* 144 */       BigInteger m = h.multiply(q);
/* 145 */       m = m.add(mQ);
/*     */       
/* 147 */       output = m.toByteArray();
/*     */     }
/*     */     else
/*     */     {
/* 151 */       output = input.modPow(this.key.getExponent(), this.key.getModulus()).toByteArray();
/*     */     }
/*     */     
/*     */ 
/* 155 */     if (this.forEncryption)
/*     */     {
/* 157 */       if ((output[0] == 0) && (output.length > getOutputBlockSize()))
/*     */       {
/* 159 */         byte[] tmp = new byte[output.length - 1];
/*     */         
/* 161 */         System.arraycopy(output, 1, tmp, 0, tmp.length);
/*     */         
/* 163 */         return tmp;
/*     */       }
/*     */       
/* 166 */       if (output.length < getOutputBlockSize())
/*     */       {
/* 168 */         byte[] tmp = new byte[getOutputBlockSize()];
/*     */         
/* 170 */         System.arraycopy(output, 0, tmp, tmp.length - output.length, output.length);
/*     */         
/* 172 */         return tmp;
/*     */       }
/*     */       
/*     */ 
/*     */     }
/* 177 */     else if (output[0] == 0)
/*     */     {
/* 179 */       byte[] tmp = new byte[output.length - 1];
/*     */       
/* 181 */       System.arraycopy(output, 1, tmp, 0, tmp.length);
/*     */       
/* 183 */       return tmp;
/*     */     }
/*     */     
/* 186 */     return output;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/engines/RSAEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */