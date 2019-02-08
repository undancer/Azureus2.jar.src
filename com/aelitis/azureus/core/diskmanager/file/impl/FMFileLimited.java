/*     */ package com.aelitis.azureus.core.diskmanager.file.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.file.FMFile;
/*     */ import com.aelitis.azureus.core.diskmanager.file.FMFileManagerException;
/*     */ import com.aelitis.azureus.core.diskmanager.file.FMFileOwner;
/*     */ import java.io.File;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
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
/*     */ public class FMFileLimited
/*     */   extends FMFileImpl
/*     */ {
/*     */   protected FMFileLimited(FMFileOwner _owner, FMFileManagerImpl _manager, File _file, int _type)
/*     */     throws FMFileManagerException
/*     */   {
/*  50 */     super(_owner, _manager, _file, _type);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected FMFileLimited(FMFileLimited basis)
/*     */     throws FMFileManagerException
/*     */   {
/*  59 */     super(basis);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public FMFile createClone()
/*     */     throws FMFileManagerException
/*     */   {
/*  67 */     return new FMFileLimited(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void ensureOpen(String reason)
/*     */     throws FMFileManagerException
/*     */   {
/*     */     try
/*     */     {
/*  77 */       this.this_mon.enter();
/*     */       
/*  79 */       if (isOpen())
/*     */       {
/*  81 */         usedSlot();
/*     */       }
/*     */       else
/*     */       {
/*  85 */         getSlot();
/*     */         
/*     */         try
/*     */         {
/*  89 */           super.ensureOpen(reason);
/*     */         }
/*     */         finally
/*     */         {
/*  93 */           if (!isOpen())
/*     */           {
/*  95 */             releaseSlot();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 101 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void getSlot()
/*     */   {
/* 108 */     getManager().getSlot(this);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void releaseSlot()
/*     */   {
/* 114 */     getManager().releaseSlot(this);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void usedSlot()
/*     */   {
/* 120 */     getManager().usedSlot(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAccessMode(int mode)
/*     */     throws FMFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 130 */       this.this_mon.enter();
/*     */       
/* 132 */       if (mode != getAccessMode())
/*     */       {
/* 134 */         close(false);
/*     */       }
/*     */       
/* 137 */       setAccessModeSupport(mode);
/*     */     }
/*     */     finally
/*     */     {
/* 141 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLength()
/*     */     throws FMFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 151 */       this.this_mon.enter();
/*     */       
/* 153 */       ensureOpen("FMFileLimited:getLength");
/*     */       
/* 155 */       return getLengthSupport();
/*     */     }
/*     */     finally
/*     */     {
/* 159 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLength(long length)
/*     */     throws FMFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 170 */       this.this_mon.enter();
/*     */       
/* 172 */       ensureOpen("FMFileLimited:setLength");
/*     */       
/* 174 */       setLengthSupport(length);
/*     */     }
/*     */     finally
/*     */     {
/* 178 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPieceComplete(int piece_number, DirectByteBuffer piece_data)
/*     */     throws FMFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 190 */       this.this_mon.enter();
/*     */       
/* 192 */       if (isPieceCompleteProcessingNeeded(piece_number))
/*     */       {
/* 194 */         ensureOpen("FMFileLimited:setPieceComplete");
/*     */         
/* 196 */         boolean switched_mode = false;
/*     */         
/* 198 */         if (getAccessMode() != 2)
/*     */         {
/* 200 */           setAccessMode(2);
/*     */           
/* 202 */           switched_mode = true;
/*     */           
/*     */ 
/*     */ 
/* 206 */           ensureOpen("FMFileLimited:setPieceComplete2");
/*     */         }
/*     */         
/*     */         try
/*     */         {
/* 211 */           setPieceCompleteSupport(piece_number, piece_data);
/*     */         }
/*     */         finally
/*     */         {
/* 215 */           if (switched_mode)
/*     */           {
/* 217 */             setAccessMode(1);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 223 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void read(DirectByteBuffer[] buffers, long offset)
/*     */     throws FMFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 235 */       this.this_mon.enter();
/*     */       
/* 237 */       ensureOpen("FMFileLimited:read");
/*     */       
/* 239 */       readSupport(buffers, offset);
/*     */     }
/*     */     finally
/*     */     {
/* 243 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void read(DirectByteBuffer buffer, long offset)
/*     */     throws FMFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 255 */       this.this_mon.enter();
/*     */       
/* 257 */       ensureOpen("FMFileLimited:read");
/*     */       
/* 259 */       readSupport(buffer, offset);
/*     */     }
/*     */     finally
/*     */     {
/* 263 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void write(DirectByteBuffer buffer, long position)
/*     */     throws FMFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 276 */       this.this_mon.enter();
/*     */       
/* 278 */       ensureOpen("FMFileLimited:write");
/*     */       
/* 280 */       writeSupport(buffer, position);
/*     */     }
/*     */     finally
/*     */     {
/* 284 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void write(DirectByteBuffer[] buffers, long position)
/*     */     throws FMFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 296 */       this.this_mon.enter();
/*     */       
/* 298 */       ensureOpen("FMFileLimited:write");
/*     */       
/* 300 */       writeSupport(buffers, position);
/*     */     }
/*     */     finally
/*     */     {
/* 304 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */     throws FMFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 314 */       this.this_mon.enter();
/*     */       
/* 316 */       close(true);
/*     */     }
/*     */     finally
/*     */     {
/* 320 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void close(boolean explicit)
/*     */     throws FMFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 331 */       this.this_mon.enter();
/*     */       
/* 333 */       boolean was_open = isOpen();
/*     */       try
/*     */       {
/* 336 */         closeSupport(explicit);
/*     */       }
/*     */       finally
/*     */       {
/* 340 */         if (was_open)
/*     */         {
/* 342 */           releaseSlot();
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 347 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/file/impl/FMFileLimited.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */