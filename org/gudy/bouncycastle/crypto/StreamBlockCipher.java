/*     */ package org.gudy.bouncycastle.crypto;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StreamBlockCipher
/*     */   implements StreamCipher
/*     */ {
/*     */   private BlockCipher cipher;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  17 */   private byte[] oneByte = new byte[1];
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public StreamBlockCipher(BlockCipher cipher)
/*     */   {
/*  29 */     if (cipher.getBlockSize() != 1)
/*     */     {
/*  31 */       throw new IllegalArgumentException("block cipher block size != 1.");
/*     */     }
/*     */     
/*  34 */     this.cipher = cipher;
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
/*     */   public void init(boolean forEncryption, CipherParameters params)
/*     */   {
/*  47 */     this.cipher.init(forEncryption, params);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAlgorithmName()
/*     */   {
/*  57 */     return this.cipher.getAlgorithmName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte returnByte(byte in)
/*     */   {
/*  69 */     this.oneByte[0] = in;
/*     */     
/*  71 */     this.cipher.processBlock(this.oneByte, 0, this.oneByte, 0);
/*     */     
/*  73 */     return this.oneByte[0];
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
/*     */   public void processBytes(byte[] in, int inOff, int len, byte[] out, int outOff)
/*     */     throws DataLengthException
/*     */   {
/*  94 */     if (outOff + len > out.length)
/*     */     {
/*  96 */       throw new DataLengthException("output buffer too small in processBytes()");
/*     */     }
/*     */     
/*  99 */     for (int i = 0; i != len; i++)
/*     */     {
/* 101 */       this.cipher.processBlock(in, inOff + i, out, outOff + i);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 111 */     this.cipher.reset();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/StreamBlockCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */