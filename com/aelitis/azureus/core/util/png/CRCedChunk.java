/*     */ package com.aelitis.azureus.core.util.png;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.InvalidParameterException;
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
/*     */ public abstract class CRCedChunk
/*     */   extends Chunk
/*     */ {
/*     */   private byte[] type;
/*     */   
/*     */   public CRCedChunk(byte[] type)
/*     */     throws InvalidParameterException
/*     */   {
/*  32 */     if (type.length != 4) {
/*  33 */       throw new InvalidParameterException("type must be of length 4, provided : " + type.length);
/*     */     }
/*  35 */     this.type = type;
/*     */   }
/*     */   
/*     */   public byte[] getChunkPayload()
/*     */   {
/*  40 */     byte[] contentPayload = getContentPayload();
/*  41 */     int length = contentPayload.length;
/*  42 */     ByteBuffer buffer = ByteBuffer.allocate(length + 12);
/*  43 */     buffer.putInt(length);
/*  44 */     buffer.put(this.type);
/*  45 */     buffer.put(contentPayload);
/*     */     
/*  47 */     buffer.position(4);
/*  48 */     buffer.limit(length + 8);
/*     */     
/*  50 */     long crc = crc(buffer);
/*  51 */     buffer.limit(length + 12);
/*  52 */     buffer.putInt((int)crc);
/*     */     
/*  54 */     buffer.position(0);
/*  55 */     return buffer.array();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  63 */   private static final long[] crc_table = new long['Ā'];
/*     */   
/*     */ 
/*  66 */   private static boolean crc_table_computed = false;
/*     */   
/*     */ 
/*     */   public abstract byte[] getContentPayload();
/*     */   
/*     */ 
/*     */   private static synchronized void make_crc_table()
/*     */   {
/*  74 */     for (int n = 0; n < 256; n++) {
/*  75 */       long c = n;
/*  76 */       for (int k = 0; k < 8; k++) {
/*  77 */         if ((c & 1L) != 0L) {
/*  78 */           c = 0xEDB88320 ^ c >> 1 & 0xFFFFFFFFFFFFFFFF;
/*     */         } else {
/*  80 */           c >>= 1;
/*     */         }
/*  82 */         c &= 0xFFFFFFFFFFFFFFFF;
/*     */       }
/*  84 */       crc_table[n] = c;
/*     */     }
/*  86 */     crc_table_computed = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static long update_crc(long crc, ByteBuffer buf)
/*     */   {
/*  96 */     long c = crc;
/*     */     
/*  98 */     if (!crc_table_computed) {
/*  99 */       make_crc_table();
/*     */     }
/* 101 */     while (buf.hasRemaining()) {
/* 102 */       c = crc_table[((int)((c ^ buf.get()) & 0xFF))] ^ c >> 8;
/*     */     }
/* 104 */     return c;
/*     */   }
/*     */   
/*     */ 
/*     */   private static long crc(ByteBuffer buf)
/*     */   {
/* 110 */     return update_crc(4294967295L, buf) ^ 0xFFFFFFFF;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/png/CRCedChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */