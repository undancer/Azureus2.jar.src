/*     */ package org.gudy.azureus2.core3.util;
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
/*     */ public class ED2KHasher
/*     */ {
/*     */   public static final int BLOCK_SIZE = 9728000;
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
/*  36 */   protected MD4Hasher current_hasher = new MD4Hasher();
/*     */   
/*     */ 
/*     */ 
/*     */   protected MD4Hasher block_hasher;
/*     */   
/*     */ 
/*     */ 
/*     */   protected int current_bytes;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(byte[] data)
/*     */   {
/*  51 */     update(data, 0, data.length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(byte[] data, int pos, int len)
/*     */   {
/*  60 */     int rem = len;
/*     */     
/*  62 */     while (rem > 0)
/*     */     {
/*  64 */       int space = 9728000 - this.current_bytes;
/*     */       
/*  66 */       if (rem <= space)
/*     */       {
/*  68 */         this.current_hasher.update(data, pos, rem);
/*     */         
/*  70 */         this.current_bytes += rem;
/*     */         
/*  72 */         break;
/*     */       }
/*     */       
/*     */ 
/*  76 */       if (this.block_hasher == null)
/*     */       {
/*  78 */         this.block_hasher = new MD4Hasher();
/*     */       }
/*     */       
/*  81 */       if (space == 0)
/*     */       {
/*  83 */         this.block_hasher.update(this.current_hasher.getDigest());
/*     */         
/*  85 */         this.current_hasher = new MD4Hasher();
/*     */         
/*  87 */         this.current_bytes = 0;
/*     */       }
/*     */       else
/*     */       {
/*  91 */         this.current_hasher.update(data, pos, space);
/*     */         
/*  93 */         pos += space;
/*  94 */         rem -= space;
/*  95 */         this.current_bytes += space;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getDigest()
/*     */   {
/* 106 */     if (this.current_bytes == 9728000)
/*     */     {
/* 108 */       if (this.block_hasher == null)
/*     */       {
/* 110 */         this.block_hasher = new MD4Hasher();
/*     */       }
/*     */       
/* 113 */       this.block_hasher.update(this.current_hasher.getDigest());
/*     */       
/* 115 */       this.current_hasher = new MD4Hasher();
/*     */     }
/*     */     
/* 118 */     if (this.block_hasher == null)
/*     */     {
/* 120 */       return this.current_hasher.getDigest();
/*     */     }
/*     */     
/*     */ 
/* 124 */     if (this.current_bytes > 0)
/*     */     {
/* 126 */       this.block_hasher.update(this.current_hasher.getDigest());
/*     */     }
/*     */     
/* 129 */     return this.block_hasher.getDigest();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/ED2KHasher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */