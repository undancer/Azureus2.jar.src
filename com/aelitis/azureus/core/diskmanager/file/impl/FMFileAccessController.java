/*     */ package com.aelitis.azureus.core.diskmanager.file.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.file.FMFileManagerException;
/*     */ import com.aelitis.azureus.core.diskmanager.file.FMFileOwner;
/*     */ import java.io.File;
/*     */ import java.io.RandomAccessFile;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
/*     */ import org.gudy.azureus2.core3.util.StringInterner;
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
/*     */ public class FMFileAccessController
/*     */   implements FMFileAccess
/*     */ {
/*     */   private static final String REORDER_SUFFIX = ".2";
/*  41 */   private static final boolean TEST_PIECE_REORDER = System.getProperty("azureus.file.piece.reorder.force", "0").equals("1");
/*     */   private final FMFileImpl owner;
/*     */   
/*  44 */   static { if (TEST_PIECE_REORDER)
/*     */     {
/*  46 */       Debug.out("*** Piece reordering storage forced ***");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*  51 */   private int type = 1;
/*     */   
/*     */ 
/*     */   private File control_dir;
/*     */   
/*     */ 
/*     */   private String controlFileName;
/*     */   
/*     */   private FMFileAccess file_access;
/*     */   
/*     */ 
/*     */   protected FMFileAccessController(FMFileImpl _file, int _target_type)
/*     */     throws FMFileManagerException
/*     */   {
/*  65 */     if (TEST_PIECE_REORDER)
/*     */     {
/*  67 */       _target_type = 3;
/*     */     }
/*     */     
/*  70 */     this.owner = _file;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  79 */     setControlFile();
/*     */     
/*  81 */     if (this.control_dir == null)
/*     */     {
/*     */ 
/*     */ 
/*  85 */       if (_target_type == 1)
/*     */       {
/*  87 */         this.file_access = new FMFileAccessLinear(this.owner);
/*     */       }
/*     */       else
/*     */       {
/*  91 */         throw new FMFileManagerException("Compact storage not supported: no control file available");
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  96 */       if (new File(this.control_dir, this.controlFileName).exists())
/*     */       {
/*  98 */         this.type = 2;
/*     */       }
/* 100 */       else if (new File(this.control_dir, this.controlFileName + ".2").exists())
/*     */       {
/* 102 */         this.type = (_target_type == 3 ? 3 : 4);
/*     */ 
/*     */ 
/*     */       }
/* 106 */       else if ((_target_type == 3) || (_target_type == 4))
/*     */       {
/* 108 */         File target_file = this.owner.getLinkedFile();
/*     */         
/* 110 */         if (target_file.exists())
/*     */         {
/* 112 */           FMFileAccessPieceReorderer.recoverConfig(this.owner.getOwner().getTorrentFile(), target_file, new File(this.control_dir, this.controlFileName + ".2"), _target_type);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 118 */         this.type = _target_type;
/*     */       }
/*     */       else
/*     */       {
/* 122 */         this.type = 1;
/*     */       }
/*     */       
/*     */ 
/* 126 */       if (this.type == 1)
/*     */       {
/* 128 */         this.file_access = new FMFileAccessLinear(this.owner);
/*     */       }
/* 130 */       else if (this.type == 2)
/*     */       {
/* 132 */         this.file_access = new FMFileAccessCompact(this.owner.getOwner().getTorrentFile(), this.control_dir, this.controlFileName, new FMFileAccessLinear(this.owner));
/*     */ 
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/*     */ 
/* 140 */         this.file_access = new FMFileAccessPieceReorderer(this.owner.getOwner().getTorrentFile(), this.control_dir, this.controlFileName + ".2", this.type, new FMFileAccessLinear(this.owner));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 149 */       convert(_target_type);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void convert(int target_type)
/*     */     throws FMFileManagerException
/*     */   {
/* 159 */     if (this.type == target_type)
/*     */     {
/* 161 */       return;
/*     */     }
/*     */     
/* 164 */     if ((this.type == 3) || (target_type == 3))
/*     */     {
/* 166 */       if ((target_type == 4) || (this.type == 4))
/*     */       {
/*     */ 
/*     */ 
/* 170 */         this.type = target_type;
/*     */         
/* 172 */         return;
/*     */       }
/*     */       
/* 175 */       throw new FMFileManagerException("Conversion to/from piece-reorder not supported");
/*     */     }
/*     */     
/* 178 */     File file = this.owner.getLinkedFile();
/*     */     
/* 180 */     RandomAccessFile raf = null;
/*     */     
/* 182 */     boolean ok = false;
/*     */     try
/*     */     {
/*     */       FMFileAccess target_access;
/*     */       FMFileAccess target_access;
/* 187 */       if (target_type == 1)
/*     */       {
/* 189 */         target_access = new FMFileAccessLinear(this.owner);
/*     */       }
/*     */       else
/*     */       {
/* 193 */         target_access = new FMFileAccessCompact(this.owner.getOwner().getTorrentFile(), this.control_dir, this.controlFileName, new FMFileAccessLinear(this.owner));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 199 */       if (file.exists())
/*     */       {
/* 201 */         raf = new RandomAccessFile(file, "rw");
/*     */         
/*     */ 
/*     */         FMFileAccessCompact compact_access;
/*     */         
/*     */         FMFileAccessCompact compact_access;
/*     */         
/* 208 */         if (target_type == 1)
/*     */         {
/* 210 */           compact_access = (FMFileAccessCompact)this.file_access;
/*     */         }
/*     */         else
/*     */         {
/* 214 */           compact_access = (FMFileAccessCompact)target_access;
/*     */         }
/*     */         
/* 217 */         long length = this.file_access.getLength(raf);
/*     */         
/* 219 */         long last_piece_start = compact_access.getLastPieceStart();
/* 220 */         long last_piece_length = compact_access.getLastPieceLength();
/*     */         
/*     */ 
/*     */ 
/* 224 */         if ((last_piece_length > 0L) && (length > last_piece_start))
/*     */         {
/* 226 */           long data_length = length - last_piece_start;
/*     */           
/* 228 */           if (data_length > last_piece_length)
/*     */           {
/* 230 */             Debug.out("data length inconsistent: len=" + data_length + ",limit=" + last_piece_length);
/*     */             
/* 232 */             data_length = last_piece_length;
/*     */           }
/*     */           
/* 235 */           DirectByteBuffer buffer = DirectByteBufferPool.getBuffer((byte)25, (int)data_length);
/*     */           
/*     */ 
/*     */           try
/*     */           {
/* 240 */             this.file_access.read(raf, new DirectByteBuffer[] { buffer }, last_piece_start);
/*     */             
/*     */ 
/*     */ 
/* 244 */             if (target_type == 2)
/*     */             {
/* 246 */               long first_piece_length = compact_access.getFirstPieceLength();
/*     */               
/* 248 */               long physical_length = raf.length();
/*     */               
/* 250 */               if (physical_length > first_piece_length)
/*     */               {
/* 252 */                 raf.setLength(first_piece_length);
/*     */               }
/*     */             }
/*     */             
/* 256 */             buffer.flip((byte)25);
/*     */             
/* 258 */             target_access.write(raf, new DirectByteBuffer[] { buffer }, last_piece_start);
/*     */           }
/*     */           finally
/*     */           {
/* 262 */             buffer.returnToPool();
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */         }
/* 268 */         else if (target_type == 2)
/*     */         {
/* 270 */           long first_piece_length = compact_access.getFirstPieceLength();
/*     */           
/* 272 */           long physical_length = raf.length();
/*     */           
/* 274 */           if (physical_length > first_piece_length)
/*     */           {
/* 276 */             raf.setLength(first_piece_length);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 281 */         target_access.setLength(raf, length);
/*     */         
/* 283 */         target_access.flush();
/*     */       }
/*     */       
/* 286 */       this.type = target_type;
/* 287 */       this.file_access = target_access;
/*     */       
/* 289 */       ok = true;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 293 */       Debug.printStackTrace(e);
/*     */       
/* 295 */       throw new FMFileManagerException("convert fails", e);
/*     */     }
/*     */     finally
/*     */     {
/*     */       try {
/* 300 */         if (raf != null) {
/*     */           try
/*     */           {
/* 303 */             raf.close();
/*     */ 
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/*     */ 
/* 309 */             if (ok)
/*     */             {
/* 311 */               ok = false;
/*     */               
/* 313 */               throw new FMFileManagerException("convert fails", e);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       finally {
/* 319 */         if (!ok)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 324 */           this.type = 1;
/* 325 */           this.file_access = new FMFileAccessLinear(this.owner);
/*     */         }
/*     */         
/* 328 */         if (this.type == 1)
/*     */         {
/* 330 */           new File(this.control_dir, this.controlFileName).delete();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setControlFile()
/*     */   {
/* 339 */     TOTorrentFile tf = this.owner.getOwner().getTorrentFile();
/*     */     
/* 341 */     if (tf == null)
/*     */     {
/* 343 */       this.controlFileName = null;
/* 344 */       this.control_dir = null;
/*     */     }
/*     */     else
/*     */     {
/* 348 */       TOTorrent torrent = tf.getTorrent();
/*     */       
/* 350 */       TOTorrentFile[] files = torrent.getFiles();
/*     */       
/* 352 */       int file_index = -1;
/*     */       
/* 354 */       for (int i = 0; i < files.length; i++)
/*     */       {
/* 356 */         if (files[i] == tf)
/*     */         {
/* 358 */           file_index = i;
/*     */           
/* 360 */           break;
/*     */         }
/*     */       }
/*     */       
/* 364 */       if (file_index == -1)
/*     */       {
/* 366 */         Debug.out("File '" + this.owner.getName() + "' not found in torrent!");
/*     */         
/* 368 */         this.controlFileName = null;
/* 369 */         this.control_dir = null;
/*     */       }
/*     */       else
/*     */       {
/* 373 */         this.control_dir = this.owner.getOwner().getControlFileDir();
/* 374 */         this.controlFileName = StringInterner.intern("fmfile" + file_index + ".dat");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setStorageType(int new_type)
/*     */     throws FMFileManagerException
/*     */   {
/* 386 */     convert(new_type);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getStorageType()
/*     */   {
/* 392 */     return this.type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void aboutToOpen()
/*     */     throws FMFileManagerException
/*     */   {
/* 403 */     this.file_access.aboutToOpen();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getLength(RandomAccessFile raf)
/*     */     throws FMFileManagerException
/*     */   {
/* 412 */     return this.file_access.getLength(raf);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLength(RandomAccessFile raf, long length)
/*     */     throws FMFileManagerException
/*     */   {
/* 422 */     this.file_access.setLength(raf, length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isPieceCompleteProcessingNeeded(int piece_number)
/*     */   {
/* 429 */     return this.file_access.isPieceCompleteProcessingNeeded(piece_number);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPieceComplete(RandomAccessFile raf, int piece_number, DirectByteBuffer piece_data)
/*     */     throws FMFileManagerException
/*     */   {
/* 440 */     this.file_access.setPieceComplete(raf, piece_number, piece_data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void read(RandomAccessFile raf, DirectByteBuffer[] buffers, long offset)
/*     */     throws FMFileManagerException
/*     */   {
/* 451 */     this.file_access.read(raf, buffers, offset);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void write(RandomAccessFile raf, DirectByteBuffer[] buffers, long position)
/*     */     throws FMFileManagerException
/*     */   {
/* 462 */     this.file_access.write(raf, buffers, position);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void flush()
/*     */     throws FMFileManagerException
/*     */   {
/* 470 */     this.file_access.flush();
/*     */   }
/*     */   
/*     */ 
/*     */   public FMFileImpl getFile()
/*     */   {
/* 476 */     return this.owner;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 482 */     return "type=" + this.type + ",acc=" + this.file_access.getString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/file/impl/FMFileAccessController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */