/*     */ package org.gudy.bouncycastle.crypto.digests;
/*     */ 
/*     */ import org.gudy.bouncycastle.crypto.Digest;
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
/*     */ public abstract class GeneralDigest
/*     */   implements Digest
/*     */ {
/*     */   private byte[] xBuf;
/*     */   private int xBufOff;
/*     */   private long byteCount;
/*     */   
/*     */   protected GeneralDigest()
/*     */   {
/*  23 */     this.xBuf = new byte[4];
/*  24 */     this.xBufOff = 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected GeneralDigest(GeneralDigest t)
/*     */   {
/*  34 */     this.xBuf = new byte[t.xBuf.length];
/*  35 */     System.arraycopy(t.xBuf, 0, this.xBuf, 0, t.xBuf.length);
/*     */     
/*  37 */     this.xBufOff = t.xBufOff;
/*  38 */     this.byteCount = t.byteCount;
/*     */   }
/*     */   
/*     */ 
/*     */   public void update(byte in)
/*     */   {
/*  44 */     this.xBuf[(this.xBufOff++)] = in;
/*     */     
/*  46 */     if (this.xBufOff == this.xBuf.length)
/*     */     {
/*  48 */       processWord(this.xBuf, 0);
/*  49 */       this.xBufOff = 0;
/*     */     }
/*     */     
/*  52 */     this.byteCount += 1L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(byte[] in, int inOff, int len)
/*     */   {
/*  63 */     while ((this.xBufOff != 0) && (len > 0))
/*     */     {
/*  65 */       update(in[inOff]);
/*     */       
/*  67 */       inOff++;
/*  68 */       len--;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  74 */     while (len > this.xBuf.length)
/*     */     {
/*  76 */       processWord(in, inOff);
/*     */       
/*  78 */       inOff += this.xBuf.length;
/*  79 */       len -= this.xBuf.length;
/*  80 */       this.byteCount += this.xBuf.length;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  86 */     while (len > 0)
/*     */     {
/*  88 */       update(in[inOff]);
/*     */       
/*  90 */       inOff++;
/*  91 */       len--;
/*     */     }
/*     */   }
/*     */   
/*     */   public void finish()
/*     */   {
/*  97 */     long bitLength = this.byteCount << 3;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 102 */     update((byte)Byte.MIN_VALUE);
/*     */     
/* 104 */     while (this.xBufOff != 0)
/*     */     {
/* 106 */       update((byte)0);
/*     */     }
/*     */     
/* 109 */     processLength(bitLength);
/*     */     
/* 111 */     processBlock();
/*     */   }
/*     */   
/*     */   public void reset()
/*     */   {
/* 116 */     this.byteCount = 0L;
/*     */     
/* 118 */     this.xBufOff = 0;
/* 119 */     for (int i = 0; i < this.xBuf.length; i++) {
/* 120 */       this.xBuf[i] = 0;
/*     */     }
/*     */   }
/*     */   
/*     */   protected abstract void processWord(byte[] paramArrayOfByte, int paramInt);
/*     */   
/*     */   protected abstract void processLength(long paramLong);
/*     */   
/*     */   protected abstract void processBlock();
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/digests/GeneralDigest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */