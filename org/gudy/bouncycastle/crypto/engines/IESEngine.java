/*     */ package org.gudy.bouncycastle.crypto.engines;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import org.gudy.bouncycastle.crypto.BasicAgreement;
/*     */ import org.gudy.bouncycastle.crypto.BufferedBlockCipher;
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.DerivationFunction;
/*     */ import org.gudy.bouncycastle.crypto.InvalidCipherTextException;
/*     */ import org.gudy.bouncycastle.crypto.Mac;
/*     */ import org.gudy.bouncycastle.crypto.params.IESParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.IESWithCipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.KDFParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.KeyParameter;
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
/*     */ public class IESEngine
/*     */ {
/*     */   BasicAgreement agree;
/*     */   DerivationFunction kdf;
/*     */   Mac mac;
/*     */   BufferedBlockCipher cipher;
/*     */   byte[] macBuf;
/*     */   boolean forEncryption;
/*     */   CipherParameters privParam;
/*     */   CipherParameters pubParam;
/*     */   IESParameters param;
/*     */   
/*     */   public IESEngine(BasicAgreement agree, DerivationFunction kdf, Mac mac)
/*     */   {
/*  45 */     this.agree = agree;
/*  46 */     this.kdf = kdf;
/*  47 */     this.mac = mac;
/*  48 */     this.macBuf = new byte[mac.getMacSize()];
/*  49 */     this.cipher = null;
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
/*     */   public IESEngine(BasicAgreement agree, DerivationFunction kdf, Mac mac, BufferedBlockCipher cipher)
/*     */   {
/*  67 */     this.agree = agree;
/*  68 */     this.kdf = kdf;
/*  69 */     this.mac = mac;
/*  70 */     this.macBuf = new byte[mac.getMacSize()];
/*  71 */     this.cipher = cipher;
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
/*     */   public void init(boolean forEncryption, CipherParameters privParam, CipherParameters pubParam, CipherParameters param)
/*     */   {
/*  88 */     this.forEncryption = forEncryption;
/*  89 */     this.privParam = privParam;
/*  90 */     this.pubParam = pubParam;
/*  91 */     this.param = ((IESParameters)param);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] decryptBlock(byte[] in_enc, int inOff, int inLen, byte[] z)
/*     */     throws InvalidCipherTextException
/*     */   {
/* 101 */     byte[] M = null;
/* 102 */     KeyParameter macKey = null;
/* 103 */     KDFParameters kParam = new KDFParameters(z, this.param.getDerivationV());
/* 104 */     int macKeySize = this.param.getMacKeySize();
/*     */     
/* 106 */     this.kdf.init(kParam);
/*     */     
/* 108 */     inLen -= this.mac.getMacSize();
/*     */     
/* 110 */     if (this.cipher == null)
/*     */     {
/* 112 */       byte[] buf = new byte[inLen + macKeySize / 8];
/*     */       
/* 114 */       M = new byte[inLen];
/*     */       
/* 116 */       this.kdf.generateBytes(buf, 0, buf.length);
/*     */       
/* 118 */       for (int i = 0; i != inLen; i++)
/*     */       {
/* 120 */         M[i] = ((byte)(in_enc[(inOff + i)] ^ buf[i]));
/*     */       }
/*     */       
/* 123 */       macKey = new KeyParameter(buf, inLen, macKeySize / 8);
/*     */     }
/*     */     else
/*     */     {
/* 127 */       int cipherKeySize = ((IESWithCipherParameters)this.param).getCipherKeySize();
/* 128 */       byte[] buf = new byte[cipherKeySize / 8 + macKeySize / 8];
/*     */       
/* 130 */       this.cipher.init(false, new KeyParameter(buf, 0, cipherKeySize / 8));
/*     */       
/* 132 */       byte[] tmp = new byte[this.cipher.getOutputSize(inLen)];
/*     */       
/* 134 */       int off = this.cipher.processBytes(in_enc, inOff, inLen, tmp, 0);
/*     */       
/* 136 */       off += this.cipher.doFinal(tmp, off);
/*     */       
/* 138 */       M = new byte[off];
/*     */       
/* 140 */       System.arraycopy(tmp, 0, M, 0, off);
/*     */       
/* 142 */       macKey = new KeyParameter(buf, cipherKeySize / 8, macKeySize / 8);
/*     */     }
/*     */     
/* 145 */     byte[] macIV = this.param.getEncodingV();
/*     */     
/* 147 */     this.mac.init(macKey);
/* 148 */     this.mac.update(in_enc, inOff, inLen);
/* 149 */     this.mac.update(macIV, 0, macIV.length);
/* 150 */     this.mac.doFinal(this.macBuf, 0);
/*     */     
/* 152 */     inOff += inLen;
/*     */     
/* 154 */     for (int t = 0; t < this.macBuf.length; t++)
/*     */     {
/* 156 */       if (this.macBuf[t] != in_enc[(inOff + t)])
/*     */       {
/* 158 */         throw new InvalidCipherTextException("Mac codes failed to equal.");
/*     */       }
/*     */     }
/*     */     
/* 162 */     return M;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] encryptBlock(byte[] in, int inOff, int inLen, byte[] z)
/*     */     throws InvalidCipherTextException
/*     */   {
/* 172 */     byte[] C = null;
/* 173 */     KeyParameter macKey = null;
/* 174 */     KDFParameters kParam = new KDFParameters(z, this.param.getDerivationV());
/* 175 */     int c_text_length = 0;
/* 176 */     int macKeySize = this.param.getMacKeySize();
/*     */     
/* 178 */     this.kdf.init(kParam);
/*     */     
/* 180 */     if (this.cipher == null)
/*     */     {
/* 182 */       byte[] buf = new byte[inLen + macKeySize / 8];
/*     */       
/* 184 */       C = new byte[inLen + this.mac.getMacSize()];
/* 185 */       c_text_length = inLen;
/*     */       
/* 187 */       this.kdf.generateBytes(buf, 0, buf.length);
/*     */       
/* 189 */       for (int i = 0; i != inLen; i++)
/*     */       {
/* 191 */         C[i] = ((byte)(in[(inOff + i)] ^ buf[i]));
/*     */       }
/*     */       
/* 194 */       macKey = new KeyParameter(buf, inLen, macKeySize / 8);
/*     */     }
/*     */     else
/*     */     {
/* 198 */       int cipherKeySize = ((IESWithCipherParameters)this.param).getCipherKeySize();
/* 199 */       byte[] buf = new byte[cipherKeySize / 8 + macKeySize / 8];
/*     */       
/* 201 */       this.cipher.init(true, new KeyParameter(buf, 0, cipherKeySize / 8));
/*     */       
/* 203 */       c_text_length = this.cipher.getOutputSize(inLen);
/*     */       
/* 205 */       C = new byte[c_text_length + this.mac.getMacSize()];
/*     */       
/* 207 */       int off = this.cipher.processBytes(in, inOff, inLen, C, 0);
/*     */       
/* 209 */       this.cipher.doFinal(C, off);
/*     */       
/* 211 */       macKey = new KeyParameter(buf, cipherKeySize / 8, macKeySize / 8);
/*     */     }
/*     */     
/* 214 */     byte[] macIV = this.param.getEncodingV();
/*     */     
/* 216 */     this.mac.init(macKey);
/* 217 */     this.mac.update(C, 0, c_text_length);
/* 218 */     this.mac.update(macIV, 0, macIV.length);
/*     */     
/*     */ 
/*     */ 
/* 222 */     this.mac.doFinal(C, c_text_length);
/* 223 */     return C;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] processBlock(byte[] in, int inOff, int inLen)
/*     */     throws InvalidCipherTextException
/*     */   {
/* 232 */     this.agree.init(this.privParam);
/*     */     
/* 234 */     BigInteger z = this.agree.calculateAgreement(this.pubParam);
/*     */     
/* 236 */     if (this.forEncryption)
/*     */     {
/* 238 */       return encryptBlock(in, inOff, inLen, z.toByteArray());
/*     */     }
/*     */     
/*     */ 
/* 242 */     return decryptBlock(in, inOff, inLen, z.toByteArray());
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/engines/IESEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */