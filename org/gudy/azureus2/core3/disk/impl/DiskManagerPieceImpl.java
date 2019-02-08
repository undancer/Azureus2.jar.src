/*     */ package org.gudy.azureus2.core3.disk.impl;
/*     */ 
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceList;
/*     */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMapEntry;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DiskManagerPieceImpl
/*     */   implements DiskManagerPiece
/*     */ {
/*     */   private static final byte PIECE_STATUS_NEEDED = 1;
/*     */   private static final byte PIECE_STATUS_WRITTEN = 32;
/*     */   private static final byte PIECE_STATUS_CHECKING = 64;
/*     */   private static final byte PIECE_STATUS_MASK_DOWNLOADABLE = 97;
/*     */   private static final byte PIECE_STATUS_MASK_NEEDS_CHECK = 96;
/*     */   private final DiskManagerHelper diskManager;
/*     */   private final int pieceNumber;
/*     */   private final short nbBlocks;
/*     */   protected volatile boolean[] written;
/*     */   private byte statusFlags;
/*     */   private short read_count;
/*     */   private boolean done;
/*     */   
/*     */   public DiskManagerPieceImpl(DiskManagerHelper _disk_manager, int pieceIndex, int length)
/*     */   {
/*  81 */     this.diskManager = _disk_manager;
/*  82 */     this.pieceNumber = pieceIndex;
/*     */     
/*  84 */     this.nbBlocks = ((short)((length + 16384 - 1) / 16384));
/*     */     
/*  86 */     this.statusFlags = 1;
/*     */   }
/*     */   
/*     */   public DiskManager getManager()
/*     */   {
/*  91 */     return this.diskManager;
/*     */   }
/*     */   
/*     */   public int getPieceNumber()
/*     */   {
/*  96 */     return this.pieceNumber;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getLength()
/*     */   {
/* 104 */     return this.diskManager.getPieceLength(this.pieceNumber);
/*     */   }
/*     */   
/*     */   public int getNbBlocks()
/*     */   {
/* 109 */     return this.nbBlocks;
/*     */   }
/*     */   
/*     */ 
/*     */   public short getReadCount()
/*     */   {
/* 115 */     return this.read_count;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setReadCount(short c)
/*     */   {
/* 122 */     this.read_count = c;
/*     */   }
/*     */   
/*     */   public int getBlockSize(int blockNumber)
/*     */   {
/* 127 */     if (blockNumber == this.nbBlocks - 1)
/*     */     {
/* 129 */       int len = getLength() % 16384;
/*     */       
/* 131 */       if (len != 0)
/*     */       {
/* 133 */         return len;
/*     */       }
/*     */     }
/*     */     
/* 137 */     return 16384;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSkipped()
/*     */   {
/* 143 */     DMPieceList pieceList = this.diskManager.getPieceList(this.pieceNumber);
/* 144 */     for (int i = 0; i < pieceList.size(); i++) {
/* 145 */       DiskManagerFileInfoImpl file = pieceList.get(i).getFile();
/* 146 */       if (file == null) {
/* 147 */         return false;
/*     */       }
/* 149 */       if (!file.isSkipped()) {
/* 150 */         return false;
/*     */       }
/*     */     }
/* 153 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isNeeded()
/*     */   {
/* 158 */     return (this.statusFlags & 0x1) != 0;
/*     */   }
/*     */   
/*     */   public boolean calcNeeded()
/*     */   {
/* 163 */     boolean filesNeeded = false;
/* 164 */     DMPieceList pieceList = this.diskManager.getPieceList(this.pieceNumber);
/* 165 */     for (int i = 0; i < pieceList.size(); i++)
/*     */     {
/* 167 */       DiskManagerFileInfoImpl file = pieceList.get(i).getFile();
/* 168 */       long fileLength = file.getLength();
/* 169 */       filesNeeded |= ((fileLength > 0L) && (file.getDownloaded() < fileLength) && (!file.isSkipped()));
/*     */     }
/* 171 */     if (filesNeeded)
/*     */     {
/* 173 */       this.statusFlags = ((byte)(this.statusFlags | 0x1));
/* 174 */       return true;
/*     */     }
/* 176 */     this.statusFlags = ((byte)(this.statusFlags & 0xFFFFFFFE));
/* 177 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean spansFiles()
/*     */   {
/* 183 */     DMPieceList pieceList = this.diskManager.getPieceList(this.pieceNumber);
/*     */     
/* 185 */     return pieceList.size() > 1;
/*     */   }
/*     */   
/*     */   public void clearNeeded()
/*     */   {
/* 190 */     this.statusFlags = ((byte)(this.statusFlags & 0xFFFFFFFE));
/*     */   }
/*     */   
/*     */   public void setNeeded()
/*     */   {
/* 195 */     this.statusFlags = ((byte)(this.statusFlags | 0x1));
/*     */   }
/*     */   
/*     */   public void setNeeded(boolean b)
/*     */   {
/* 200 */     if (b) {
/* 201 */       this.statusFlags = ((byte)(this.statusFlags | 0x1));
/*     */     } else {
/* 203 */       this.statusFlags = ((byte)(this.statusFlags & 0xFFFFFFFE));
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isWritten() {
/* 208 */     return (this.statusFlags & 0x20) != 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean[] getWritten()
/*     */   {
/* 217 */     return this.written;
/*     */   }
/*     */   
/*     */   public boolean isWritten(int blockNumber)
/*     */   {
/* 222 */     if (this.done)
/* 223 */       return true;
/* 224 */     boolean[] writtenRef = this.written;
/* 225 */     if (writtenRef == null)
/* 226 */       return false;
/* 227 */     return writtenRef[blockNumber];
/*     */   }
/*     */   
/*     */   public int getNbWritten()
/*     */   {
/* 232 */     if (this.done)
/* 233 */       return this.nbBlocks;
/* 234 */     boolean[] writtenRef = this.written;
/* 235 */     if (writtenRef == null)
/* 236 */       return 0;
/* 237 */     int res = 0;
/* 238 */     for (int i = 0; i < this.nbBlocks; i++)
/*     */     {
/* 240 */       if (writtenRef[i] != 0)
/* 241 */         res++;
/*     */     }
/* 243 */     return res;
/*     */   }
/*     */   
/*     */   public void setWritten(int blockNumber)
/*     */   {
/* 248 */     if (this.written == null)
/* 249 */       this.written = new boolean[this.nbBlocks];
/* 250 */     boolean[] written_ref = this.written;
/*     */     
/* 252 */     written_ref[blockNumber] = true;
/* 253 */     for (int i = 0; i < this.nbBlocks; i++)
/*     */     {
/* 255 */       if (written_ref[i] == 0)
/* 256 */         return;
/*     */     }
/* 258 */     this.statusFlags = ((byte)(this.statusFlags | 0x20));
/*     */   }
/*     */   
/*     */   public boolean isChecking()
/*     */   {
/* 263 */     return (this.statusFlags & 0x40) != 0;
/*     */   }
/*     */   
/*     */   public void setChecking()
/*     */   {
/* 268 */     this.statusFlags = ((byte)(this.statusFlags | 0x40));
/*     */   }
/*     */   
/*     */   public boolean isNeedsCheck()
/*     */   {
/* 273 */     return (!this.done) && ((this.statusFlags & 0x60) == 32);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean calcDone()
/*     */   {
/* 282 */     return this.done;
/*     */   }
/*     */   
/*     */   public boolean isDone()
/*     */   {
/* 287 */     return this.done;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setDone(boolean b)
/*     */   {
/* 293 */     if (b != this.done)
/*     */     {
/* 295 */       this.diskManager.setPieceDone(this, b);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDoneSupport(boolean b)
/*     */   {
/* 306 */     this.done = b;
/* 307 */     if (this.done) {
/* 308 */       this.written = null;
/*     */     }
/*     */   }
/*     */   
/*     */   public void setDownloadable() {
/* 313 */     setDone(false);
/* 314 */     this.statusFlags = ((byte)(this.statusFlags & 0xFFFFFF9E));
/* 315 */     calcNeeded();
/*     */   }
/*     */   
/*     */   public boolean isDownloadable()
/*     */   {
/* 320 */     return (!this.done) && ((this.statusFlags & 0x61) == 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isInteresting()
/*     */   {
/* 328 */     return (!this.done) && ((this.statusFlags & 0x1) != 0);
/*     */   }
/*     */   
/*     */   public void reset()
/*     */   {
/* 333 */     setDownloadable();
/* 334 */     this.written = null;
/*     */   }
/*     */   
/*     */   public void reDownloadBlock(int blockNumber)
/*     */   {
/* 339 */     boolean[] written_ref = this.written;
/* 340 */     if (written_ref != null)
/*     */     {
/* 342 */       written_ref[blockNumber] = false;
/* 343 */       setDownloadable();
/*     */     }
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
/*     */   public String getString()
/*     */   {
/* 380 */     String text = "";
/*     */     
/* 382 */     text = text + (isNeeded() ? "needed," : "");
/* 383 */     text = text + (isDone() ? "done," : "");
/*     */     
/* 385 */     if (!isDone()) {
/* 386 */       text = text + (isDownloadable() ? "downable," : "");
/* 387 */       text = text + (isWritten() ? "written" : new StringBuilder().append("written ").append(getNbWritten()).toString()) + ",";
/* 388 */       text = text + (isChecking() ? "checking" : "");
/*     */     }
/*     */     
/* 391 */     if (text.endsWith(",")) {
/* 392 */       text = text.substring(0, text.length() - 1);
/*     */     }
/* 394 */     return text;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/DiskManagerPieceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */