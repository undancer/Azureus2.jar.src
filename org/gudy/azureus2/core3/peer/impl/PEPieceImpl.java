/*     */ package org.gudy.azureus2.core3.peer.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.piecepicker.PiecePicker;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPiece;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class PEPieceImpl
/*     */   implements PEPiece
/*     */ {
/*  45 */   private static final LogIDs LOGID = LogIDs.PIECES;
/*     */   
/*     */   private final DiskManagerPiece dmPiece;
/*     */   
/*     */   private final PEPeerManager manager;
/*     */   
/*     */   private final int nbBlocks;
/*     */   
/*     */   private long creationTime;
/*     */   
/*     */   private final String[] requested;
/*     */   
/*     */   private boolean fully_requested;
/*     */   
/*     */   private final boolean[] downloaded;
/*     */   
/*     */   private boolean fully_downloaded;
/*     */   
/*     */   private long time_last_download;
/*     */   
/*     */   private final String[] writers;
/*     */   
/*     */   private final List writes;
/*     */   
/*     */   private String reservedBy;
/*     */   
/*     */   private int speed;
/*     */   private int resumePriority;
/*     */   private Object real_time_data;
/*  74 */   protected static final AEMonitor class_mon = new AEMonitor("PEPiece:class");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PEPieceImpl(PEPeerManager _manager, DiskManagerPiece _dm_piece, int _pieceSpeed)
/*     */   {
/*  86 */     this.creationTime = SystemTime.getCurrentTime();
/*  87 */     this.manager = _manager;
/*  88 */     this.dmPiece = _dm_piece;
/*  89 */     this.speed = _pieceSpeed;
/*     */     
/*  91 */     this.nbBlocks = this.dmPiece.getNbBlocks();
/*     */     
/*  93 */     this.requested = new String[this.nbBlocks];
/*     */     
/*  95 */     boolean[] written = this.dmPiece.getWritten();
/*  96 */     if (written == null) {
/*  97 */       this.downloaded = new boolean[this.nbBlocks];
/*     */     } else {
/*  99 */       this.downloaded = ((boolean[])written.clone());
/*     */     }
/* 101 */     this.writers = new String[this.nbBlocks];
/* 102 */     this.writes = new ArrayList(0);
/*     */   }
/*     */   
/*     */   public DiskManagerPiece getDMPiece()
/*     */   {
/* 107 */     return this.dmPiece;
/*     */   }
/*     */   
/*     */   public long getCreationTime()
/*     */   {
/* 112 */     long now = SystemTime.getCurrentTime();
/* 113 */     if ((now >= this.creationTime) && (this.creationTime > 0L)) {
/* 114 */       return this.creationTime;
/*     */     }
/* 116 */     this.creationTime = now;
/* 117 */     return now;
/*     */   }
/*     */   
/*     */   public long getTimeSinceLastActivity()
/*     */   {
/* 122 */     long now = SystemTime.getCurrentTime();
/* 123 */     long lastWriteTime = getLastDownloadTime(now);
/* 124 */     if (lastWriteTime > 0L) {
/* 125 */       return now - lastWriteTime;
/*     */     }
/* 127 */     long lastCreateTime = this.creationTime;
/* 128 */     if ((lastCreateTime > 0L) && (now >= lastCreateTime)) {
/* 129 */       return now - lastCreateTime;
/*     */     }
/* 131 */     this.creationTime = now;
/* 132 */     return 0L;
/*     */   }
/*     */   
/*     */   public long getLastDownloadTime(long now)
/*     */   {
/* 137 */     if (this.time_last_download <= now)
/* 138 */       return this.time_last_download;
/* 139 */     return this.time_last_download = now;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isRequested(int blockNumber)
/*     */   {
/* 148 */     return this.requested[blockNumber] != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isDownloaded(int blockNumber)
/*     */   {
/* 157 */     return this.downloaded[blockNumber];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDownloaded(int offset)
/*     */   {
/* 166 */     this.time_last_download = SystemTime.getCurrentTime();
/* 167 */     this.downloaded[(offset / 16384)] = true;
/* 168 */     for (int i = 0; i < this.nbBlocks; i++)
/*     */     {
/* 170 */       if (this.downloaded[i] == 0) {
/* 171 */         return;
/*     */       }
/*     */     }
/* 174 */     this.fully_downloaded = true;
/* 175 */     this.fully_requested = false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void clearDownloaded(int offset)
/*     */   {
/* 184 */     this.downloaded[(offset / 16384)] = false;
/*     */     
/* 186 */     this.fully_downloaded = false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDownloaded()
/*     */   {
/* 192 */     return this.fully_downloaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean[] getDownloaded()
/*     */   {
/* 198 */     return this.downloaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasUndownloadedBlock()
/*     */   {
/* 204 */     for (int i = 0; i < this.nbBlocks; i++)
/*     */     {
/* 206 */       if (this.downloaded[i] == 0)
/*     */       {
/* 208 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 212 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setWritten(String peer, int blockNumber)
/*     */   {
/* 221 */     this.writers[blockNumber] = peer;
/* 222 */     this.dmPiece.setWritten(blockNumber);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void clearRequested(int blockNumber)
/*     */   {
/* 231 */     this.requested[blockNumber] = (this.downloaded[blockNumber] != 0 ? this.writers[blockNumber] : null);
/*     */     
/* 233 */     this.fully_requested = false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isRequested()
/*     */   {
/* 239 */     return this.fully_requested;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setRequested()
/*     */   {
/* 245 */     this.fully_requested = true;
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
/*     */   public void checkRequests()
/*     */   {
/* 308 */     if (getTimeSinceLastActivity() < 30000L)
/*     */     {
/* 310 */       return;
/*     */     }
/*     */     
/* 313 */     int cleared = 0;
/*     */     
/* 315 */     for (int i = 0; i < this.nbBlocks; i++)
/*     */     {
/* 317 */       if ((this.downloaded[i] == 0) && (!this.dmPiece.isWritten(i)))
/*     */       {
/* 319 */         String requester = this.requested[i];
/*     */         
/* 321 */         if (requester != null)
/*     */         {
/* 323 */           if (!this.manager.requestExists(requester, getPieceNumber(), i * 16384, getBlockSize(i)))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 329 */             clearRequested(i);
/*     */             
/* 331 */             cleared++;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 337 */     if (cleared > 0)
/*     */     {
/* 339 */       if (Logger.isEnabled()) {
/* 340 */         Logger.log(new LogEvent(this.dmPiece.getManager().getTorrent(), LOGID, 1, "checkRequests(): piece #" + getPieceNumber() + " cleared " + cleared + " requests"));
/*     */       }
/*     */       
/*     */     }
/* 344 */     else if ((this.fully_requested) && (getNbUnrequested() > 0))
/*     */     {
/* 346 */       if (Logger.isEnabled()) {
/* 347 */         Logger.log(new LogEvent(this.dmPiece.getManager().getTorrent(), LOGID, 1, "checkRequests(): piece #" + getPieceNumber() + " reset fully requested"));
/*     */       }
/*     */       
/* 350 */       this.fully_requested = false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasUnrequestedBlock()
/*     */   {
/* 361 */     boolean[] written = this.dmPiece.getWritten();
/* 362 */     for (int i = 0; i < this.nbBlocks; i++)
/*     */     {
/* 364 */       if ((this.downloaded[i] == 0) && (this.requested[i] == null) && ((written == null) || (written[i] == 0)))
/* 365 */         return true;
/*     */     }
/* 367 */     return false;
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
/*     */   public int[] getAndMarkBlocks(PEPeer peer, int nbWanted, int[] request_hint, boolean reverse_order)
/*     */   {
/* 385 */     String ip = peer.getIp();
/*     */     
/* 387 */     boolean[] written = this.dmPiece.getWritten();
/*     */     
/* 389 */     if (request_hint != null)
/*     */     {
/*     */ 
/*     */ 
/* 393 */       int hint_block_start = request_hint[1] / 16384;
/* 394 */       int hint_block_end = (request_hint[1] + request_hint[2] - 1) / 16384;
/*     */       
/* 396 */       if (reverse_order)
/*     */       {
/* 398 */         for (int i = Math.min(this.nbBlocks - 1, hint_block_end); i >= hint_block_start; i--)
/*     */         {
/* 400 */           int blocksFound = 0;
/* 401 */           int block_index = i;
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 406 */           while ((blocksFound < nbWanted) && (block_index < this.nbBlocks) && (this.downloaded[block_index] == 0) && (this.requested[block_index] == null) && ((written == null) || (written[block_index] == 0)))
/*     */           {
/*     */ 
/* 409 */             this.requested[block_index] = ip;
/* 410 */             blocksFound++;
/* 411 */             block_index--;
/*     */           }
/* 413 */           if (blocksFound > 0) {
/* 414 */             return new int[] { block_index + 1, blocksFound };
/*     */           }
/*     */         }
/*     */       } else {
/* 418 */         for (int i = hint_block_start; (i < this.nbBlocks) && (i <= hint_block_end); i++)
/*     */         {
/* 420 */           int blocksFound = 0;
/* 421 */           int block_index = i;
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 426 */           while ((blocksFound < nbWanted) && (block_index < this.nbBlocks) && (this.downloaded[block_index] == 0) && (this.requested[block_index] == null) && ((written == null) || (written[block_index] == 0)))
/*     */           {
/*     */ 
/* 429 */             this.requested[block_index] = ip;
/* 430 */             blocksFound++;
/* 431 */             block_index++;
/*     */           }
/* 433 */           if (blocksFound > 0) {
/* 434 */             return new int[] { i, blocksFound };
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 442 */     if (reverse_order)
/*     */     {
/* 444 */       for (int i = this.nbBlocks - 1; i >= 0; i--)
/*     */       {
/* 446 */         int blocksFound = 0;
/* 447 */         int block_index = i;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 452 */         while ((blocksFound < nbWanted) && (block_index >= 0) && (this.downloaded[block_index] == 0) && (this.requested[block_index] == null) && ((written == null) || (written[block_index] == 0)))
/*     */         {
/*     */ 
/* 455 */           this.requested[block_index] = ip;
/* 456 */           blocksFound++;
/* 457 */           block_index--;
/*     */         }
/* 459 */         if (blocksFound > 0) {
/* 460 */           return new int[] { block_index + 1, blocksFound };
/*     */         }
/*     */         
/*     */       }
/*     */     } else {
/* 465 */       for (int i = 0; i < this.nbBlocks; i++)
/*     */       {
/* 467 */         int blocksFound = 0;
/* 468 */         int block_index = i;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 473 */         while ((blocksFound < nbWanted) && (block_index < this.nbBlocks) && (this.downloaded[block_index] == 0) && (this.requested[block_index] == null) && ((written == null) || (written[block_index] == 0)))
/*     */         {
/*     */ 
/* 476 */           this.requested[block_index] = ip;
/* 477 */           blocksFound++;
/* 478 */           block_index++;
/*     */         }
/* 480 */         if (blocksFound > 0) {
/* 481 */           return new int[] { i, blocksFound };
/*     */         }
/*     */       }
/*     */     }
/* 485 */     return new int[] { -1, 0 };
/*     */   }
/*     */   
/*     */   public void getAndMarkBlock(PEPeer peer, int index)
/*     */   {
/* 490 */     this.requested[index] = peer.getIp();
/*     */     
/* 492 */     if (getNbUnrequested() <= 0)
/*     */     {
/* 494 */       setRequested();
/*     */     }
/*     */   }
/*     */   
/*     */   public int getNbRequests()
/*     */   {
/* 500 */     int result = 0;
/* 501 */     for (int i = 0; i < this.nbBlocks; i++)
/*     */     {
/* 503 */       if ((this.downloaded[i] == 0) && (this.requested[i] != null))
/* 504 */         result++;
/*     */     }
/* 506 */     return result;
/*     */   }
/*     */   
/*     */   public int getNbUnrequested()
/*     */   {
/* 511 */     int result = 0;
/* 512 */     boolean[] written = this.dmPiece.getWritten();
/* 513 */     for (int i = 0; i < this.nbBlocks; i++)
/*     */     {
/* 515 */       if ((this.downloaded[i] == 0) && (this.requested[i] == null) && ((written == null) || (written[i] == 0)))
/* 516 */         result++;
/*     */     }
/* 518 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean setRequested(PEPeer peer, int blockNumber)
/*     */   {
/* 526 */     if (this.downloaded[blockNumber] == 0)
/*     */     {
/* 528 */       this.requested[blockNumber] = peer.getIp();
/* 529 */       return true;
/*     */     }
/* 531 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isRequestable()
/*     */   {
/* 537 */     return (this.dmPiece.isDownloadable()) && (!this.fully_downloaded) && (!this.fully_requested);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getBlockSize(int blockNumber)
/*     */   {
/* 544 */     if (blockNumber == this.nbBlocks - 1)
/*     */     {
/* 546 */       int length = this.dmPiece.getLength();
/*     */       
/* 548 */       if (length % 16384 != 0)
/*     */       {
/* 550 */         return length % 16384;
/*     */       }
/*     */     }
/*     */     
/* 554 */     return 16384;
/*     */   }
/*     */   
/*     */   public int getBlockNumber(int offset)
/*     */   {
/* 559 */     return offset / 16384;
/*     */   }
/*     */   
/*     */   public int getNbBlocks()
/*     */   {
/* 564 */     return this.nbBlocks;
/*     */   }
/*     */   
/*     */   public List getPieceWrites()
/*     */   {
/*     */     List result;
/*     */     try {
/* 571 */       class_mon.enter();
/*     */       
/* 573 */       result = new ArrayList(this.writes);
/*     */     }
/*     */     finally {
/* 576 */       class_mon.exit();
/*     */     }
/* 578 */     return result;
/*     */   }
/*     */   
/*     */   public List getPieceWrites(int blockNumber)
/*     */   {
/*     */     List result;
/*     */     try {
/* 585 */       class_mon.enter();
/*     */       
/* 587 */       result = new ArrayList(this.writes);
/*     */     }
/*     */     finally
/*     */     {
/* 591 */       class_mon.exit();
/*     */     }
/* 593 */     Iterator iter = result.iterator();
/* 594 */     while (iter.hasNext()) {
/* 595 */       PEPieceWriteImpl write = (PEPieceWriteImpl)iter.next();
/* 596 */       if (write.getBlockNumber() != blockNumber)
/* 597 */         iter.remove();
/*     */     }
/* 599 */     return result;
/*     */   }
/*     */   
/*     */   public List getPieceWrites(PEPeer peer)
/*     */   {
/*     */     List result;
/*     */     try {
/* 606 */       class_mon.enter();
/*     */       
/* 608 */       result = new ArrayList(this.writes);
/*     */     } finally {
/* 610 */       class_mon.exit();
/*     */     }
/* 612 */     Iterator iter = result.iterator();
/* 613 */     while (iter.hasNext()) {
/* 614 */       PEPieceWriteImpl write = (PEPieceWriteImpl)iter.next();
/* 615 */       if ((peer == null) || (!peer.getIp().equals(write.getSender())))
/* 616 */         iter.remove();
/*     */     }
/* 618 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public List getPieceWrites(String ip)
/*     */   {
/*     */     List result;
/*     */     
/*     */     try
/*     */     {
/* 628 */       class_mon.enter();
/*     */       
/* 630 */       result = new ArrayList(this.writes);
/*     */     }
/*     */     finally
/*     */     {
/* 634 */       class_mon.exit();
/*     */     }
/*     */     
/* 637 */     Iterator iter = result.iterator();
/*     */     
/* 639 */     while (iter.hasNext())
/*     */     {
/* 641 */       PEPieceWriteImpl write = (PEPieceWriteImpl)iter.next();
/*     */       
/* 643 */       if (!write.getSender().equals(ip))
/*     */       {
/* 645 */         iter.remove();
/*     */       }
/*     */     }
/*     */     
/* 649 */     return result;
/*     */   }
/*     */   
/*     */   public void reset()
/*     */   {
/* 654 */     this.dmPiece.reset();
/* 655 */     for (int i = 0; i < this.nbBlocks; i++)
/*     */     {
/* 657 */       this.requested[i] = null;
/* 658 */       this.downloaded[i] = false;
/* 659 */       this.writers[i] = null;
/*     */     }
/* 661 */     this.fully_downloaded = false;
/* 662 */     this.time_last_download = 0L;
/* 663 */     this.reservedBy = null;
/* 664 */     this.real_time_data = null;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getRealTimeData()
/*     */   {
/* 670 */     return this.real_time_data;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setRealTimeData(Object o)
/*     */   {
/* 677 */     this.real_time_data = o;
/*     */   }
/*     */   
/*     */   protected void addWrite(PEPieceWriteImpl write) {
/*     */     try {
/* 682 */       class_mon.enter();
/*     */       
/* 684 */       this.writes.add(write);
/*     */     }
/*     */     finally
/*     */     {
/* 688 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addWrite(int blockNumber, String sender, byte[] hash, boolean correct)
/*     */   {
/* 699 */     addWrite(new PEPieceWriteImpl(blockNumber, sender, hash, correct));
/*     */   }
/*     */   
/*     */   public String[] getWriters()
/*     */   {
/* 704 */     return this.writers;
/*     */   }
/*     */   
/*     */   public int getSpeed()
/*     */   {
/* 709 */     return this.speed;
/*     */   }
/*     */   
/*     */   public void setSpeed(int newSpeed)
/*     */   {
/* 714 */     this.speed = newSpeed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLastRequestedPeerSpeed(int peerSpeed)
/*     */   {
/* 722 */     if (peerSpeed > this.speed) {
/* 723 */       this.speed += 1;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PEPeerManager getManager()
/*     */   {
/* 732 */     return this.manager;
/*     */   }
/*     */   
/*     */   public void setReservedBy(String peer)
/*     */   {
/* 737 */     this.reservedBy = peer;
/*     */   }
/*     */   
/*     */   public String getReservedBy()
/*     */   {
/* 742 */     return this.reservedBy;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reDownloadBlock(int blockNumber)
/*     */   {
/* 751 */     this.downloaded[blockNumber] = false;
/* 752 */     this.requested[blockNumber] = null;
/* 753 */     this.fully_downloaded = false;
/* 754 */     this.writers[blockNumber] = null;
/* 755 */     this.dmPiece.reDownloadBlock(blockNumber);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reDownloadBlocks(String address)
/*     */   {
/* 764 */     for (int i = 0; i < this.writers.length; i++)
/*     */     {
/* 766 */       String writer = this.writers[i];
/*     */       
/* 768 */       if ((writer != null) && (writer.equals(address))) {
/* 769 */         reDownloadBlock(i);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void setResumePriority(int p) {
/* 775 */     this.resumePriority = p;
/*     */   }
/*     */   
/*     */   public int getResumePriority()
/*     */   {
/* 780 */     return this.resumePriority;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getAvailability()
/*     */   {
/* 789 */     return this.manager.getAvailability(this.dmPiece.getPieceNumber());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getNbWritten()
/*     */   {
/* 799 */     return this.dmPiece.getNbWritten();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean[] getWritten()
/*     */   {
/* 808 */     return this.dmPiece.getWritten();
/*     */   }
/*     */   
/*     */   public boolean isWritten() {
/* 812 */     return this.dmPiece.isWritten();
/*     */   }
/*     */   
/*     */   public boolean isWritten(int block)
/*     */   {
/* 817 */     return this.dmPiece.isWritten(block);
/*     */   }
/*     */   
/*     */   public int getPieceNumber() {
/* 821 */     return this.dmPiece.getPieceNumber();
/*     */   }
/*     */   
/*     */   public int getLength()
/*     */   {
/* 826 */     return this.dmPiece.getLength();
/*     */   }
/*     */   
/*     */   public void setRequestable()
/*     */   {
/* 831 */     this.fully_downloaded = false;
/* 832 */     this.fully_requested = false;
/*     */     
/* 834 */     this.dmPiece.setDownloadable();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 840 */     String text = "";
/*     */     
/* 842 */     PiecePicker pp = this.manager.getPiecePicker();
/*     */     
/* 844 */     text = text + (isRequestable() ? "reqable," : "");
/* 845 */     text = text + "req=" + getNbRequests() + ",";
/* 846 */     text = text + (isRequested() ? "reqstd," : "");
/* 847 */     text = text + (isDownloaded() ? "downed," : "");
/* 848 */     text = text + (getReservedBy() != null ? "resrv," : "");
/* 849 */     text = text + "speed=" + getSpeed() + ",";
/* 850 */     text = text + (pp == null ? "pri=" + getResumePriority() : pp.getPieceString(this.dmPiece.getPieceNumber()));
/*     */     
/* 852 */     if (text.endsWith(",")) {
/* 853 */       text = text.substring(0, text.length() - 1);
/*     */     }
/*     */     
/* 856 */     return text;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/impl/PEPieceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */