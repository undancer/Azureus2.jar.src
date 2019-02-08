/*     */ package org.gudy.bouncycastle.crypto.macs;
/*     */ 
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.Digest;
/*     */ import org.gudy.bouncycastle.crypto.Mac;
/*     */ import org.gudy.bouncycastle.crypto.params.KeyParameter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HMac
/*     */   implements Mac
/*     */ {
/*     */   private static final int BLOCK_LENGTH = 64;
/*     */   private static final byte IPAD = 54;
/*     */   private static final byte OPAD = 92;
/*     */   private Digest digest;
/*     */   private int digestSize;
/*  23 */   private byte[] inputPad = new byte[64];
/*  24 */   private byte[] outputPad = new byte[64];
/*     */   
/*     */ 
/*     */   public HMac(Digest digest)
/*     */   {
/*  29 */     this.digest = digest;
/*  30 */     this.digestSize = digest.getDigestSize();
/*     */   }
/*     */   
/*     */   public String getAlgorithmName()
/*     */   {
/*  35 */     return this.digest.getAlgorithmName() + "/HMAC";
/*     */   }
/*     */   
/*     */   public Digest getUnderlyingDigest()
/*     */   {
/*  40 */     return this.digest;
/*     */   }
/*     */   
/*     */ 
/*     */   public void init(CipherParameters params)
/*     */   {
/*  46 */     this.digest.reset();
/*     */     
/*  48 */     byte[] key = ((KeyParameter)params).getKey();
/*     */     
/*  50 */     if (key.length > 64)
/*     */     {
/*  52 */       this.digest.update(key, 0, key.length);
/*  53 */       this.digest.doFinal(this.inputPad, 0);
/*  54 */       for (int i = this.digestSize; i < this.inputPad.length; i++)
/*     */       {
/*  56 */         this.inputPad[i] = 0;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  61 */       System.arraycopy(key, 0, this.inputPad, 0, key.length);
/*  62 */       for (int i = key.length; i < this.inputPad.length; i++)
/*     */       {
/*  64 */         this.inputPad[i] = 0;
/*     */       }
/*     */     }
/*     */     
/*  68 */     this.outputPad = new byte[this.inputPad.length];
/*  69 */     System.arraycopy(this.inputPad, 0, this.outputPad, 0, this.inputPad.length);
/*     */     
/*  71 */     for (int i = 0; i < this.inputPad.length; i++)
/*     */     {
/*  73 */       int tmp164_163 = i; byte[] tmp164_160 = this.inputPad;tmp164_160[tmp164_163] = ((byte)(tmp164_160[tmp164_163] ^ 0x36));
/*     */     }
/*     */     
/*  76 */     for (int i = 0; i < this.outputPad.length; i++)
/*     */     {
/*  78 */       int tmp193_192 = i; byte[] tmp193_189 = this.outputPad;tmp193_189[tmp193_192] = ((byte)(tmp193_189[tmp193_192] ^ 0x5C));
/*     */     }
/*     */     
/*  81 */     this.digest.update(this.inputPad, 0, this.inputPad.length);
/*     */   }
/*     */   
/*     */   public int getMacSize()
/*     */   {
/*  86 */     return this.digestSize;
/*     */   }
/*     */   
/*     */ 
/*     */   public void update(byte in)
/*     */   {
/*  92 */     this.digest.update(in);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(byte[] in, int inOff, int len)
/*     */   {
/* 100 */     this.digest.update(in, inOff, len);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int doFinal(byte[] out, int outOff)
/*     */   {
/* 107 */     byte[] tmp = new byte[this.digestSize];
/* 108 */     this.digest.doFinal(tmp, 0);
/*     */     
/* 110 */     this.digest.update(this.outputPad, 0, this.outputPad.length);
/* 111 */     this.digest.update(tmp, 0, tmp.length);
/*     */     
/* 113 */     int len = this.digest.doFinal(out, outOff);
/*     */     
/* 115 */     reset();
/*     */     
/* 117 */     return len;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 128 */     this.digest.reset();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 133 */     this.digest.update(this.inputPad, 0, this.inputPad.length);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/macs/HMac.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */