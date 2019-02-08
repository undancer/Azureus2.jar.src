/*      */ package com.aelitis.azureus.core.diskmanager.file.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.diskmanager.file.FMFileManagerException;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.RandomAccessFile;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class FMFileAccessPieceReorderer
/*      */   implements FMFileAccess
/*      */ {
/*      */   private static final boolean TRACE = false;
/*      */   private static final int MIN_PIECES_REORDERABLE = 3;
/*      */   private static final byte SS_FILE = 4;
/*      */   private static final int DIRT_CLEAN = 0;
/*      */   private static final int DIRT_DIRTY = 1;
/*      */   private static final int DIRT_NEVER_WRITTEN = 2;
/*      */   private static final long DIRT_FLUSH_MILLIS = 30000L;
/*      */   private final FMFileAccess delegate;
/*      */   private final File control_dir;
/*      */   private final String control_file;
/*      */   private final int storage_type;
/*      */   private int piece_size;
/*      */   private int first_piece_length;
/*      */   private int first_piece_number;
/*      */   private int last_piece_length;
/*      */   private int num_pieces;
/*  104 */   private int previous_storage_type = -1;
/*      */   
/*      */   private long current_length;
/*      */   
/*      */   private int[] piece_map;
/*      */   private int[] piece_reverse_map;
/*      */   private int next_piece_index;
/*      */   private int dirt_state;
/*  112 */   private long dirt_time = -1L;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected FMFileAccessPieceReorderer(TOTorrentFile _torrent_file, File _control_dir, String _control_file, int _storage_type, FMFileAccess _delegate)
/*      */     throws FMFileManagerException
/*      */   {
/*  124 */     this.delegate = _delegate;
/*  125 */     this.control_dir = _control_dir;
/*  126 */     this.control_file = _control_file;
/*  127 */     this.storage_type = _storage_type;
/*      */     try
/*      */     {
/*  130 */       this.first_piece_number = _torrent_file.getFirstPieceNumber();
/*      */       
/*  132 */       this.num_pieces = (_torrent_file.getLastPieceNumber() - this.first_piece_number + 1);
/*      */       
/*  134 */       if (this.num_pieces >= 3)
/*      */       {
/*  136 */         this.piece_size = ((int)_torrent_file.getTorrent().getPieceLength());
/*      */         
/*  138 */         TOTorrent torrent = _torrent_file.getTorrent();
/*      */         
/*  140 */         long file_length = _torrent_file.getLength();
/*      */         
/*  142 */         long file_offset_in_torrent = 0L;
/*      */         
/*  144 */         TOTorrentFile[] files = torrent.getFiles();
/*      */         
/*  146 */         for (int i = 0; i < files.length; i++)
/*      */         {
/*  148 */           TOTorrentFile f = files[i];
/*      */           
/*  150 */           if (f == _torrent_file) {
/*      */             break;
/*      */           }
/*      */           
/*      */ 
/*  155 */           file_offset_in_torrent += f.getLength();
/*      */         }
/*      */         
/*  158 */         int first_piece_offset = (int)(file_offset_in_torrent % this.piece_size);
/*      */         
/*  160 */         this.first_piece_length = (this.piece_size - first_piece_offset);
/*      */         
/*  162 */         long file_end = file_offset_in_torrent + file_length;
/*      */         
/*      */ 
/*  165 */         this.last_piece_length = ((int)(file_end - file_end / this.piece_size * this.piece_size));
/*      */         
/*  167 */         if (this.last_piece_length == 0)
/*      */         {
/*  169 */           this.last_piece_length = this.piece_size;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  174 */       this.dirt_state = (new File(this.control_dir, this.control_file).exists() ? 0 : 2);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  178 */       throw new FMFileManagerException("Piece-reorder file init fail", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void aboutToOpen()
/*      */     throws FMFileManagerException
/*      */   {
/*  191 */     if (this.dirt_state == 2)
/*      */     {
/*  193 */       writeConfig();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getLength(RandomAccessFile raf)
/*      */     throws FMFileManagerException
/*      */   {
/*  203 */     if (this.num_pieces >= 3)
/*      */     {
/*  205 */       if (this.piece_map == null)
/*      */       {
/*  207 */         readConfig();
/*      */       }
/*      */       try
/*      */       {
/*  211 */         boolean attempt_recovery = false;
/*      */         
/*  213 */         long max_length = this.first_piece_length + (this.num_pieces - 2) * this.piece_size + this.last_piece_length;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  219 */         if ((this.current_length == 0L) && (this.next_piece_index == 1))
/*      */         {
/*  221 */           attempt_recovery = true;
/*      */         }
/*  223 */         else if ((this.storage_type == 3) && (this.previous_storage_type == 4))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  230 */           if (this.current_length == max_length)
/*      */           {
/*  232 */             long physical_length = raf.length();
/*      */             
/*  234 */             if (physical_length == this.current_length)
/*      */             {
/*  236 */               this.current_length = 0L;
/*      */               
/*  238 */               attempt_recovery = true;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  243 */         if (attempt_recovery)
/*      */         {
/*  245 */           long physical_length = raf.length();
/*      */           
/*  247 */           if (physical_length > this.current_length)
/*      */           {
/*  249 */             physical_length = Math.min(physical_length, max_length);
/*      */             
/*  251 */             if (physical_length > this.current_length)
/*      */             {
/*  253 */               this.current_length = physical_length;
/*      */               
/*  255 */               int piece_count = (int)((this.current_length + this.piece_size - 1L) / this.piece_size) + 1;
/*      */               
/*  257 */               if (piece_count > this.num_pieces)
/*      */               {
/*  259 */                 piece_count = this.num_pieces;
/*      */               }
/*      */               
/*  262 */               for (int i = 1; i < piece_count; i++)
/*      */               {
/*  264 */                 this.piece_map[i] = i;
/*  265 */                 this.piece_reverse_map[i] = i;
/*      */               }
/*      */               
/*  268 */               this.next_piece_index = piece_count;
/*      */               
/*  270 */               setDirty();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (IOException e) {}
/*      */       
/*  277 */       return this.current_length;
/*      */     }
/*      */     
/*      */ 
/*  281 */     return this.delegate.getLength(raf);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setLength(RandomAccessFile raf, long length)
/*      */     throws FMFileManagerException
/*      */   {
/*  292 */     if (this.num_pieces >= 3)
/*      */     {
/*  294 */       if (this.piece_map == null)
/*      */       {
/*  296 */         readConfig();
/*      */       }
/*      */       
/*  299 */       if (this.current_length != length)
/*      */       {
/*  301 */         this.current_length = length;
/*      */         
/*  303 */         setDirty();
/*      */       }
/*      */     }
/*      */     else {
/*  307 */       this.delegate.setLength(raf, length);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected long getPieceOffset(RandomAccessFile raf, int piece_number, boolean allocate_if_needed)
/*      */     throws FMFileManagerException
/*      */   {
/*  319 */     if (this.piece_map == null)
/*      */     {
/*  321 */       readConfig();
/*      */     }
/*      */     
/*  324 */     int index = getPieceIndex(raf, piece_number, allocate_if_needed);
/*      */     
/*  326 */     if (index < 0)
/*      */     {
/*  328 */       return index;
/*      */     }
/*  330 */     if (index == 0)
/*      */     {
/*  332 */       return 0L;
/*      */     }
/*  334 */     if (index == 1)
/*      */     {
/*  336 */       return this.first_piece_length;
/*      */     }
/*      */     
/*      */ 
/*  340 */     return this.first_piece_length + (index - 1) * this.piece_size;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int readWritePiece(RandomAccessFile raf, DirectByteBuffer[] buffers, int piece_number, int piece_offset, boolean is_read)
/*      */     throws FMFileManagerException
/*      */   {
/*  354 */     String str = is_read ? "read" : "write";
/*      */     
/*  356 */     if (piece_number >= this.num_pieces)
/*      */     {
/*  358 */       throw new FMFileManagerException("Attempt to " + str + " piece " + piece_number + ": last=" + this.num_pieces);
/*      */     }
/*      */     
/*  361 */     int this_piece_size = piece_number == this.num_pieces - 1 ? this.last_piece_length : piece_number == 0 ? this.first_piece_length : this.piece_size;
/*      */     
/*  363 */     int piece_space = this_piece_size - piece_offset;
/*      */     
/*  365 */     if (piece_space <= 0)
/*      */     {
/*  367 */       throw new FMFileManagerException("Attempt to " + str + " piece " + piece_number + ", offset " + piece_offset + " - no space in piece");
/*      */     }
/*      */     
/*  370 */     int rem_space = piece_space;
/*      */     
/*  372 */     int[] limits = new int[buffers.length];
/*      */     int rem;
/*  374 */     for (int i = 0; i < buffers.length; i++)
/*      */     {
/*  376 */       DirectByteBuffer buffer = buffers[i];
/*      */       
/*  378 */       limits[i] = buffer.limit(4);
/*      */       
/*  380 */       rem = buffer.remaining((byte)4);
/*      */       
/*  382 */       if (rem > rem_space)
/*      */       {
/*  384 */         buffer.limit((byte)4, buffer.position((byte)4) + rem_space);
/*      */         
/*  386 */         rem_space = 0;
/*      */       }
/*      */       else
/*      */       {
/*  390 */         rem_space -= rem;
/*      */       }
/*      */     }
/*      */     
/*      */     try
/*      */     {
/*  396 */       long piece_start = getPieceOffset(raf, piece_number, !is_read);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  402 */       if (piece_start == -1L) {
/*      */         int i;
/*  404 */         return 0;
/*      */       }
/*      */       
/*  407 */       long piece_io_position = piece_start + piece_offset;
/*      */       
/*  409 */       if (is_read)
/*      */       {
/*  411 */         this.delegate.read(raf, buffers, piece_io_position);
/*      */       }
/*      */       else
/*      */       {
/*  415 */         this.delegate.write(raf, buffers, piece_io_position);
/*      */       }
/*      */       int i;
/*  418 */       return piece_space - rem_space;
/*      */     }
/*      */     finally
/*      */     {
/*  422 */       for (int i = 0; i < buffers.length; i++)
/*      */       {
/*  424 */         buffers[i].limit((byte)4, limits[i]);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void readWrite(RandomAccessFile raf, DirectByteBuffer[] buffers, long position, boolean is_read)
/*      */     throws FMFileManagerException
/*      */   {
/*  438 */     long total_length = 0L;
/*      */     
/*  440 */     for (DirectByteBuffer buffer : buffers)
/*      */     {
/*  442 */       total_length += buffer.remaining((byte)4);
/*      */     }
/*      */     
/*  445 */     if ((!is_read) && (position + total_length > this.current_length))
/*      */     {
/*  447 */       this.current_length = (position + total_length);
/*      */       
/*  449 */       setDirty();
/*      */     }
/*      */     
/*  452 */     long current_position = position;
/*      */     
/*  454 */     while (total_length > 0L)
/*      */     {
/*      */       int piece_offset;
/*      */       int piece_number;
/*      */       int piece_offset;
/*  459 */       if (current_position < this.first_piece_length)
/*      */       {
/*  461 */         int piece_number = 0;
/*  462 */         piece_offset = (int)current_position;
/*      */       }
/*      */       else
/*      */       {
/*  466 */         long offset = current_position - this.first_piece_length;
/*      */         
/*  468 */         piece_number = (int)(offset / this.piece_size) + 1;
/*      */         
/*  470 */         piece_offset = (int)(offset % this.piece_size);
/*      */       }
/*      */       
/*  473 */       int count = readWritePiece(raf, buffers, piece_number, piece_offset, is_read);
/*      */       
/*  475 */       if (count == 0)
/*      */       {
/*  477 */         if (is_read)
/*      */         {
/*      */ 
/*      */ 
/*  481 */           for (DirectByteBuffer buffer : buffers)
/*      */           {
/*  483 */             ByteBuffer bb = buffer.getBuffer((byte)4);
/*      */             
/*  485 */             int rem = bb.remaining();
/*      */             
/*  487 */             if (rem > 0)
/*      */             {
/*  489 */               bb.put(new byte[rem]);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  501 */               buffer.setFlag((byte)1);
/*      */             }
/*      */             
/*      */           }
/*      */         } else {
/*  506 */           throw new FMFileManagerException("partial write operation");
/*      */         }
/*      */         
/*  509 */         return;
/*      */       }
/*      */       
/*  512 */       total_length -= count;
/*  513 */       current_position += count;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void read(RandomAccessFile raf, DirectByteBuffer[] buffers, long position)
/*      */     throws FMFileManagerException
/*      */   {
/*  525 */     if (this.num_pieces >= 3)
/*      */     {
/*  527 */       readWrite(raf, buffers, position, true);
/*      */     }
/*      */     else
/*      */     {
/*  531 */       this.delegate.read(raf, buffers, position);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void write(RandomAccessFile raf, DirectByteBuffer[] buffers, long position)
/*      */     throws FMFileManagerException
/*      */   {
/*  543 */     if (this.num_pieces >= 3)
/*      */     {
/*  545 */       readWrite(raf, buffers, position, false);
/*      */     }
/*      */     else
/*      */     {
/*  549 */       this.delegate.write(raf, buffers, position);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void flush()
/*      */     throws FMFileManagerException
/*      */   {
/*  558 */     if (this.num_pieces >= 3)
/*      */     {
/*  560 */       if (this.dirt_state != 0)
/*      */       {
/*  562 */         writeConfig();
/*      */       }
/*      */     }
/*      */     else {
/*  566 */       this.delegate.flush();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isPieceCompleteProcessingNeeded(int piece_number)
/*      */   {
/*  574 */     if (this.num_pieces >= 3)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  582 */       piece_number -= this.first_piece_number;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  588 */       if (piece_number >= this.next_piece_index)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  596 */         return false;
/*      */       }
/*      */       
/*  599 */       int store_index = this.piece_map[piece_number];
/*      */       
/*  601 */       if (store_index == -1)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  609 */         return true;
/*      */       }
/*      */       
/*  612 */       if (piece_number == store_index)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  620 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  627 */       return true;
/*      */     }
/*      */     
/*      */ 
/*  631 */     return this.delegate.isPieceCompleteProcessingNeeded(piece_number);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPieceComplete(RandomAccessFile raf, int piece_number, DirectByteBuffer piece_data)
/*      */     throws FMFileManagerException
/*      */   {
/*  645 */     if (this.num_pieces >= 3)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  653 */       piece_number -= this.first_piece_number;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  659 */       if (piece_number >= this.next_piece_index)
/*      */       {
/*      */ 
/*      */ 
/*  663 */         return;
/*      */       }
/*      */       
/*  666 */       int store_index = getPieceIndex(raf, piece_number, false);
/*      */       
/*  668 */       if (store_index == -1)
/*      */       {
/*  670 */         throw new FMFileManagerException("piece marked as complete but not yet allocated");
/*      */       }
/*      */       
/*  673 */       if (piece_number == store_index)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  681 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  686 */       int swap_piece_number = this.piece_reverse_map[piece_number];
/*      */       
/*  688 */       if (swap_piece_number < 1)
/*      */       {
/*  690 */         throw new FMFileManagerException("Inconsistent: failed to find piece to swap");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  697 */       DirectByteBuffer temp_buffer = DirectByteBufferPool.getBuffer((byte)4, this.piece_size);
/*      */       
/*  699 */       DirectByteBuffer[] temp_buffers = { temp_buffer };
/*      */       try
/*      */       {
/*  702 */         long store_offset = this.first_piece_length + (store_index - 1) * this.piece_size;
/*  703 */         long swap_offset = this.first_piece_length + (piece_number - 1) * this.piece_size;
/*      */         
/*  705 */         this.delegate.read(raf, temp_buffers, swap_offset);
/*      */         
/*  707 */         piece_data.position((byte)4, 0);
/*      */         
/*  709 */         this.delegate.write(raf, new DirectByteBuffer[] { piece_data }, swap_offset);
/*      */         
/*  711 */         temp_buffer.position((byte)4, 0);
/*      */         
/*  713 */         this.delegate.write(raf, temp_buffers, store_offset);
/*      */         
/*  715 */         this.piece_map[piece_number] = piece_number;
/*  716 */         this.piece_reverse_map[piece_number] = piece_number;
/*      */         
/*  718 */         this.piece_map[swap_piece_number] = store_index;
/*  719 */         this.piece_reverse_map[store_index] = swap_piece_number;
/*      */         
/*  721 */         setDirty();
/*      */         
/*  723 */         if (piece_number == this.num_pieces - 1)
/*      */         {
/*  725 */           long file_length = swap_offset + this.last_piece_length;
/*      */           
/*  727 */           if (this.delegate.getLength(raf) > file_length)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  733 */             this.delegate.setLength(raf, file_length);
/*      */           }
/*      */         }
/*      */       }
/*      */       finally {
/*  738 */         temp_buffer.returnToPool();
/*      */       }
/*      */     }
/*      */     else {
/*  742 */       this.delegate.setPieceComplete(raf, piece_number, piece_data);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int getPieceIndex(RandomAccessFile raf, int piece_number, boolean allocate_if_needed)
/*      */     throws FMFileManagerException
/*      */   {
/*  754 */     int store_index = this.piece_map[piece_number];
/*      */     
/*  756 */     if ((store_index == -1) && (allocate_if_needed))
/*      */     {
/*  758 */       store_index = this.next_piece_index++;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  764 */       this.piece_map[piece_number] = store_index;
/*  765 */       this.piece_reverse_map[store_index] = piece_number;
/*      */       
/*  767 */       if (piece_number != store_index)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  772 */         int swap_index = this.piece_map[store_index];
/*      */         
/*  774 */         if (swap_index > 0)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  780 */           DirectByteBuffer temp_buffer = DirectByteBufferPool.getBuffer((byte)4, this.piece_size);
/*      */           
/*  782 */           DirectByteBuffer[] temp_buffers = { temp_buffer };
/*      */           try
/*      */           {
/*  785 */             long store_offset = this.first_piece_length + (store_index - 1) * this.piece_size;
/*  786 */             long swap_offset = this.first_piece_length + (swap_index - 1) * this.piece_size;
/*      */             
/*  788 */             this.delegate.read(raf, temp_buffers, swap_offset);
/*      */             
/*  790 */             temp_buffer.position((byte)4, 0);
/*      */             
/*  792 */             this.delegate.write(raf, temp_buffers, store_offset);
/*      */             
/*  794 */             this.piece_map[store_index] = store_index;
/*  795 */             this.piece_reverse_map[store_index] = store_index;
/*      */             
/*  797 */             this.piece_map[piece_number] = swap_index;
/*  798 */             this.piece_reverse_map[swap_index] = piece_number;
/*      */             
/*  800 */             if (store_index == this.num_pieces - 1)
/*      */             {
/*  802 */               long file_length = store_offset + this.last_piece_length;
/*      */               
/*  804 */               if (this.delegate.getLength(raf) > file_length)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  810 */                 this.delegate.setLength(raf, file_length);
/*      */               }
/*      */             }
/*      */             
/*  814 */             store_index = swap_index;
/*      */           }
/*      */           finally
/*      */           {
/*  818 */             temp_buffer.returnToPool();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  823 */       setDirty();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  828 */     return store_index;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void readConfig()
/*      */     throws FMFileManagerException
/*      */   {
/*  836 */     this.piece_map = new int[this.num_pieces];
/*  837 */     this.piece_reverse_map = new int[this.num_pieces];
/*      */     
/*  839 */     if (this.dirt_state == 2)
/*      */     {
/*  841 */       Arrays.fill(this.piece_map, -1);
/*      */       
/*  843 */       this.piece_map[0] = 0;
/*  844 */       this.piece_reverse_map[0] = 0;
/*  845 */       this.next_piece_index = 1;
/*  846 */       this.current_length = 0L;
/*      */     }
/*      */     else
/*      */     {
/*  850 */       Map map = FileUtil.readResilientFile(this.control_dir, this.control_file, false);
/*      */       
/*  852 */       Long l_st = (Long)map.get("st");
/*      */       
/*  854 */       if (l_st != null) {
/*  855 */         this.previous_storage_type = l_st.intValue();
/*      */       }
/*      */       
/*  858 */       Long l_len = (Long)map.get("len");
/*  859 */       Long l_next = (Long)map.get("next");
/*  860 */       byte[] piece_bytes = (byte[])map.get("pieces");
/*      */       
/*  862 */       if ((l_len == null) || (l_next == null) || (piece_bytes == null))
/*      */       {
/*  864 */         configBorked("Failed to read control file " + new File(this.control_dir, this.control_file).getAbsolutePath() + ": map invalid - " + map);
/*      */         
/*  866 */         return;
/*      */       }
/*      */       
/*  869 */       this.current_length = l_len.longValue();
/*  870 */       this.next_piece_index = l_next.intValue();
/*      */       
/*  872 */       if (piece_bytes.length != this.num_pieces * 4)
/*      */       {
/*  874 */         configBorked("Failed to read control file " + new File(this.control_dir, this.control_file).getAbsolutePath() + ": piece bytes invalid");
/*      */         
/*  876 */         return;
/*      */       }
/*      */       
/*  879 */       int pos = 0;
/*      */       
/*  881 */       for (int i = 0; i < this.num_pieces; i++)
/*      */       {
/*  883 */         int index = (piece_bytes[(pos++)] << 24) + ((piece_bytes[(pos++)] & 0xFF) << 16) + ((piece_bytes[(pos++)] & 0xFF) << 8) + (piece_bytes[(pos++)] & 0xFF);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  889 */         this.piece_map[i] = index;
/*      */         
/*  891 */         if (index != -1)
/*      */         {
/*  893 */           this.piece_reverse_map[index] = i;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void configBorked(String error)
/*      */     throws FMFileManagerException
/*      */   {
/*  909 */     this.piece_map = new int[this.num_pieces];
/*  910 */     this.piece_reverse_map = new int[this.num_pieces];
/*      */     
/*  912 */     Arrays.fill(this.piece_map, -1);
/*      */     
/*  914 */     this.piece_map[0] = 0;
/*  915 */     this.piece_reverse_map[0] = 0;
/*  916 */     this.current_length = getFile().getLinkedFile().length();
/*      */     
/*  918 */     int piece_count = (int)((this.current_length + this.piece_size - 1L) / this.piece_size) + 1;
/*      */     
/*  920 */     if (piece_count > this.num_pieces)
/*      */     {
/*  922 */       piece_count = this.num_pieces;
/*      */     }
/*      */     
/*  925 */     for (int i = 1; i < piece_count; i++)
/*      */     {
/*  927 */       this.piece_map[i] = i;
/*  928 */       this.piece_reverse_map[i] = i;
/*      */     }
/*      */     
/*  931 */     this.next_piece_index = piece_count;
/*      */     
/*  933 */     writeConfig();
/*      */     
/*  935 */     FMFileManagerException e = new FMFileManagerException(error);
/*      */     
/*  937 */     e.setRecoverable(false);
/*      */     
/*  939 */     throw e;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setDirty()
/*      */     throws FMFileManagerException
/*      */   {
/*  947 */     if (this.dirt_state == 2)
/*      */     {
/*  949 */       Debug.out("shouldn't get here");
/*      */       
/*  951 */       writeConfig();
/*      */     }
/*      */     else {
/*  954 */       long now = SystemTime.getMonotonousTime();
/*      */       
/*  956 */       if (this.dirt_state == 0)
/*      */       {
/*  958 */         this.dirt_state = 1;
/*  959 */         this.dirt_time = now;
/*      */ 
/*      */ 
/*      */       }
/*  963 */       else if ((this.dirt_time >= 0L) && (now - this.dirt_time >= 30000L))
/*      */       {
/*  965 */         writeConfig();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static Map encodeConfig(int storage_type, long current_length, long next_piece_index, int[] piece_map)
/*      */   {
/*  978 */     Map map = new HashMap();
/*      */     
/*  980 */     map.put("st", new Long(storage_type));
/*  981 */     map.put("len", new Long(current_length));
/*  982 */     map.put("next", new Long(next_piece_index));
/*      */     
/*  984 */     byte[] pieces_bytes = new byte[piece_map.length * 4];
/*      */     
/*  986 */     int pos = 0;
/*      */     
/*  988 */     for (int i = 0; i < piece_map.length; i++)
/*      */     {
/*  990 */       int value = piece_map[i];
/*      */       
/*  992 */       if (value == -1)
/*      */       {
/*  994 */         pieces_bytes[(pos++)] = (pieces_bytes[(pos++)] = pieces_bytes[(pos++)] = pieces_bytes[(pos++)] = -1);
/*      */       }
/*      */       else
/*      */       {
/*  998 */         pieces_bytes[(pos++)] = ((byte)(value >> 24));
/*  999 */         pieces_bytes[(pos++)] = ((byte)(value >> 16));
/* 1000 */         pieces_bytes[(pos++)] = ((byte)(value >> 8));
/* 1001 */         pieces_bytes[(pos++)] = ((byte)value);
/*      */       }
/*      */     }
/*      */     
/* 1005 */     map.put("pieces", pieces_bytes);
/*      */     
/* 1007 */     return map;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void recoverConfig(TOTorrentFile torrent_file, File data_file, File config_file, int storage_type)
/*      */     throws FMFileManagerException
/*      */   {
/* 1022 */     int first_piece_number = torrent_file.getFirstPieceNumber();
/*      */     
/* 1024 */     int num_pieces = torrent_file.getLastPieceNumber() - first_piece_number + 1;
/*      */     
/* 1026 */     int piece_size = (int)torrent_file.getTorrent().getPieceLength();
/*      */     
/* 1028 */     int[] piece_map = new int[num_pieces];
/*      */     
/* 1030 */     Arrays.fill(piece_map, -1);
/*      */     
/* 1032 */     piece_map[0] = 0;
/*      */     
/* 1034 */     long current_length = data_file.length();
/*      */     
/* 1036 */     int piece_count = (int)((current_length + piece_size - 1L) / piece_size) + 1;
/*      */     
/* 1038 */     if (piece_count > num_pieces)
/*      */     {
/* 1040 */       piece_count = num_pieces;
/*      */     }
/*      */     
/* 1043 */     for (int i = 1; i < piece_count; i++)
/*      */     {
/* 1045 */       piece_map[i] = i;
/*      */     }
/*      */     
/* 1048 */     int next_piece_index = piece_count;
/*      */     
/* 1050 */     Map map = encodeConfig(storage_type, current_length, next_piece_index, piece_map);
/*      */     
/* 1052 */     File control_dir = config_file.getParentFile();
/*      */     
/* 1054 */     if (!control_dir.exists())
/*      */     {
/* 1056 */       control_dir.mkdirs();
/*      */     }
/*      */     
/* 1059 */     if (!FileUtil.writeResilientFileWithResult(control_dir, config_file.getName(), map))
/*      */     {
/* 1061 */       throw new FMFileManagerException("Failed to write control file " + config_file.getAbsolutePath());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void writeConfig()
/*      */     throws FMFileManagerException
/*      */   {
/* 1070 */     if (this.piece_map == null)
/*      */     {
/* 1072 */       readConfig();
/*      */     }
/*      */     
/* 1075 */     Map map = encodeConfig(this.storage_type, this.current_length, this.next_piece_index, this.piece_map);
/*      */     
/* 1077 */     if (!this.control_dir.exists())
/*      */     {
/* 1079 */       this.control_dir.mkdirs();
/*      */     }
/*      */     
/* 1082 */     if (!FileUtil.writeResilientFileWithResult(this.control_dir, this.control_file, map))
/*      */     {
/* 1084 */       throw new FMFileManagerException("Failed to write control file " + new File(this.control_dir, this.control_file).getAbsolutePath());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1091 */     this.dirt_state = 0;
/* 1092 */     this.dirt_time = -1L;
/*      */   }
/*      */   
/*      */ 
/*      */   public FMFileImpl getFile()
/*      */   {
/* 1098 */     return this.delegate.getFile();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getString()
/*      */   {
/* 1104 */     return "reorderer";
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/file/impl/FMFileAccessPieceReorderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */