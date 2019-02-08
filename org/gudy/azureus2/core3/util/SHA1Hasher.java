/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.nio.ByteBuffer;
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
/*     */ public final class SHA1Hasher
/*     */ {
/*     */   private final SHA1 sha1;
/*     */   
/*     */   public SHA1Hasher()
/*     */   {
/*  36 */     this.sha1 = new SHA1();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] calculateHash(byte[] bytes)
/*     */   {
/*  46 */     ByteBuffer buff = ByteBuffer.wrap(bytes);
/*  47 */     return calculateHash(buff);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] calculateHash(ByteBuffer buffer)
/*     */   {
/*  57 */     this.sha1.reset();
/*  58 */     return this.sha1.digest(buffer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(byte[] data)
/*     */   {
/*  67 */     update(ByteBuffer.wrap(data));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(byte[] data, int pos, int len)
/*     */   {
/*  79 */     update(ByteBuffer.wrap(data, pos, len));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(ByteBuffer buffer)
/*     */   {
/*  88 */     this.sha1.update(buffer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getDigest()
/*     */   {
/*  97 */     return this.sha1.digest();
/*     */   }
/*     */   
/*     */   public HashWrapper getHash() {
/* 101 */     return new HashWrapper(this.sha1.digest());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 108 */     this.sha1.reset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void saveHashState()
/*     */   {
/* 116 */     this.sha1.saveState();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void restoreHashState()
/*     */   {
/* 124 */     this.sha1.restoreState();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/SHA1Hasher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */