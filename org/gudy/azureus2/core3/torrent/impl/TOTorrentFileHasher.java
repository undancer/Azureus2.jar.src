/*     */ package org.gudy.azureus2.core3.torrent.impl;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.util.Vector;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.util.ED2KHasher;
/*     */ import org.gudy.azureus2.core3.util.SHA1Hasher;
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
/*     */ public class TOTorrentFileHasher
/*     */ {
/*     */   protected final boolean do_other_per_file_hash;
/*     */   protected final int piece_length;
/*  36 */   protected final Vector pieces = new Vector();
/*     */   
/*     */   protected final byte[] buffer;
/*     */   
/*     */   protected int buffer_pos;
/*     */   
/*     */   protected SHA1Hasher overall_sha1_hash;
/*     */   
/*     */   protected ED2KHasher overall_ed2k_hash;
/*     */   
/*     */   protected byte[] sha1_digest;
/*     */   
/*     */   protected byte[] ed2k_digest;
/*     */   
/*     */   protected byte[] per_file_sha1_digest;
/*     */   
/*     */   protected byte[] per_file_ed2k_digest;
/*     */   
/*     */   protected final TOTorrentFileHasherListener listener;
/*     */   
/*     */   protected boolean cancelled;
/*     */   
/*     */ 
/*     */   protected TOTorrentFileHasher(boolean _do_other_overall_hashes, boolean _do_other_per_file_hash, int _piece_length, TOTorrentFileHasherListener _listener)
/*     */   {
/*  61 */     if (_do_other_overall_hashes) {
/*  62 */       this.overall_sha1_hash = new SHA1Hasher();
/*     */       
/*  64 */       this.overall_ed2k_hash = new ED2KHasher();
/*     */     }
/*     */     
/*  67 */     this.do_other_per_file_hash = _do_other_per_file_hash;
/*  68 */     this.piece_length = _piece_length;
/*  69 */     this.listener = _listener;
/*     */     
/*  71 */     this.buffer = new byte[this.piece_length];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   long add(File _file)
/*     */     throws TOTorrentException
/*     */   {
/*  80 */     file_length = 0L;
/*     */     
/*  82 */     InputStream is = null;
/*     */     
/*  84 */     SHA1Hasher sha1_hash = null;
/*  85 */     ED2KHasher ed2k_hash = null;
/*     */     try
/*     */     {
/*  88 */       if (this.do_other_per_file_hash)
/*     */       {
/*  90 */         sha1_hash = new SHA1Hasher();
/*  91 */         ed2k_hash = new ED2KHasher();
/*     */       }
/*     */       
/*  94 */       is = new BufferedInputStream(new FileInputStream(_file), 65536);
/*     */       
/*     */       for (;;)
/*     */       {
/*  98 */         if (this.cancelled)
/*     */         {
/* 100 */           throw new TOTorrentException("TOTorrentCreate: operation cancelled", 9);
/*     */         }
/*     */         
/*     */ 
/* 104 */         int len = is.read(this.buffer, this.buffer_pos, this.piece_length - this.buffer_pos);
/*     */         
/* 106 */         if (len <= 0)
/*     */           break;
/* 108 */         if (this.do_other_per_file_hash)
/*     */         {
/* 110 */           sha1_hash.update(this.buffer, this.buffer_pos, len);
/* 111 */           ed2k_hash.update(this.buffer, this.buffer_pos, len);
/*     */         }
/*     */         
/*     */ 
/* 115 */         file_length += len;
/*     */         
/* 117 */         this.buffer_pos += len;
/*     */         
/* 119 */         if (this.buffer_pos == this.piece_length)
/*     */         {
/*     */ 
/*     */ 
/* 123 */           byte[] hash = new SHA1Hasher().calculateHash(this.buffer);
/*     */           
/* 125 */           if (this.overall_sha1_hash != null)
/*     */           {
/* 127 */             this.overall_sha1_hash.update(this.buffer);
/* 128 */             this.overall_ed2k_hash.update(this.buffer);
/*     */           }
/*     */           
/* 131 */           this.pieces.add(hash);
/*     */           
/* 133 */           if (this.listener != null)
/*     */           {
/* 135 */             this.listener.pieceHashed(this.pieces.size());
/*     */           }
/*     */           
/* 138 */           this.buffer_pos = 0;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 146 */       if (this.do_other_per_file_hash)
/*     */       {
/* 148 */         this.per_file_sha1_digest = sha1_hash.getDigest();
/* 149 */         this.per_file_ed2k_digest = ed2k_hash.getDigest();
/*     */       }
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
/* 170 */       return file_length;
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 154 */       throw e;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 158 */       throw new TOTorrentException("TOTorrentFileHasher: file read fails '" + e.toString() + "'", 4);
/*     */     }
/*     */     finally {
/* 161 */       if (is != null) {
/*     */         try {
/* 163 */           is.close();
/*     */         }
/*     */         catch (Exception e) {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected byte[] getPerFileSHA1Digest()
/*     */   {
/* 176 */     return this.per_file_sha1_digest;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte[] getPerFileED2KDigest()
/*     */   {
/* 182 */     return this.per_file_ed2k_digest;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte[][] getPieces()
/*     */     throws TOTorrentException
/*     */   {
/*     */     try
/*     */     {
/* 191 */       if (this.buffer_pos > 0)
/*     */       {
/* 193 */         byte[] rem = new byte[this.buffer_pos];
/*     */         
/* 195 */         System.arraycopy(this.buffer, 0, rem, 0, this.buffer_pos);
/*     */         
/* 197 */         this.pieces.addElement(new SHA1Hasher().calculateHash(rem));
/*     */         
/* 199 */         if (this.overall_sha1_hash != null)
/*     */         {
/* 201 */           this.overall_sha1_hash.update(rem);
/* 202 */           this.overall_ed2k_hash.update(rem);
/*     */         }
/*     */         
/* 205 */         if (this.listener != null)
/*     */         {
/* 207 */           this.listener.pieceHashed(this.pieces.size());
/*     */         }
/*     */         
/* 210 */         this.buffer_pos = 0;
/*     */       }
/*     */       
/* 213 */       if ((this.overall_sha1_hash != null) && (this.sha1_digest == null))
/*     */       {
/* 215 */         this.sha1_digest = this.overall_sha1_hash.getDigest();
/* 216 */         this.ed2k_digest = this.overall_ed2k_hash.getDigest();
/*     */       }
/*     */       
/* 219 */       byte[][] res = new byte[this.pieces.size()][];
/*     */       
/* 221 */       this.pieces.copyInto(res);
/*     */       
/* 223 */       return res;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 227 */       throw new TOTorrentException("TOTorrentFileHasher: file read fails '" + e.toString() + "'", 4);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected byte[] getED2KDigest()
/*     */     throws TOTorrentException
/*     */   {
/* 237 */     if (this.ed2k_digest == null)
/*     */     {
/* 239 */       getPieces();
/*     */     }
/*     */     
/* 242 */     return this.ed2k_digest;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected byte[] getSHA1Digest()
/*     */     throws TOTorrentException
/*     */   {
/* 250 */     if (this.sha1_digest == null)
/*     */     {
/* 252 */       getPieces();
/*     */     }
/*     */     
/* 255 */     return this.sha1_digest;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void cancel()
/*     */   {
/* 261 */     this.cancelled = true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/impl/TOTorrentFileHasher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */