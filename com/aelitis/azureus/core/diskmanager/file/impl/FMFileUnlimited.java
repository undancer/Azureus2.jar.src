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
/*     */ public class FMFileUnlimited
/*     */   extends FMFileImpl
/*     */ {
/*     */   protected FMFileUnlimited(FMFileOwner _owner, FMFileManagerImpl _manager, File _file, int _type)
/*     */     throws FMFileManagerException
/*     */   {
/*  49 */     super(_owner, _manager, _file, _type);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected FMFileUnlimited(FMFileUnlimited basis)
/*     */     throws FMFileManagerException
/*     */   {
/*  58 */     super(basis);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public FMFile createClone()
/*     */     throws FMFileManagerException
/*     */   {
/*  66 */     return new FMFileUnlimited(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAccessMode(int mode)
/*     */     throws FMFileManagerException
/*     */   {
/*     */     try
/*     */     {
/*  76 */       this.this_mon.enter();
/*     */       
/*  78 */       if ((mode == getAccessMode()) && (isOpen())) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/*  83 */       setAccessModeSupport(mode);
/*     */       
/*  85 */       if (isOpen())
/*     */       {
/*  87 */         closeSupport(false);
/*     */       }
/*     */       
/*  90 */       openSupport("FMFileUnlimited:setAccessMode");
/*     */     }
/*     */     finally
/*     */     {
/*  94 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLength()
/*     */     throws FMFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 104 */       this.this_mon.enter();
/*     */       
/* 106 */       ensureOpen("FMFileUnlimited:getLength");
/*     */       
/* 108 */       return getLengthSupport();
/*     */     }
/*     */     finally
/*     */     {
/* 112 */       this.this_mon.exit();
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
/* 123 */       this.this_mon.enter();
/*     */       
/* 125 */       ensureOpen("FMFileUnlimited:setLength");
/*     */       
/* 127 */       setLengthSupport(length);
/*     */     }
/*     */     finally
/*     */     {
/* 131 */       this.this_mon.exit();
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
/* 143 */       this.this_mon.enter();
/*     */       
/* 145 */       if (isPieceCompleteProcessingNeeded(piece_number))
/*     */       {
/* 147 */         ensureOpen("FMFileUnlimited:setPieceComplete");
/*     */         
/* 149 */         boolean switched_mode = false;
/*     */         
/* 151 */         if (getAccessMode() != 2)
/*     */         {
/* 153 */           setAccessMode(2);
/*     */           
/* 155 */           switched_mode = true;
/*     */           
/*     */ 
/*     */ 
/* 159 */           ensureOpen("FMFileUnlimited:setPieceComplete2");
/*     */         }
/*     */         
/*     */         try
/*     */         {
/* 164 */           setPieceCompleteSupport(piece_number, piece_data);
/*     */         }
/*     */         finally
/*     */         {
/* 168 */           if (switched_mode)
/*     */           {
/* 170 */             setAccessMode(1);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 176 */       this.this_mon.exit();
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
/* 188 */       this.this_mon.enter();
/*     */       
/* 190 */       ensureOpen("FMFileUnlimited:read");
/*     */       
/* 192 */       readSupport(buffer, offset);
/*     */     }
/*     */     finally
/*     */     {
/* 196 */       this.this_mon.exit();
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
/* 208 */       this.this_mon.enter();
/*     */       
/* 210 */       ensureOpen("FMFileUnlimited:read");
/*     */       
/* 212 */       readSupport(buffers, offset);
/*     */     }
/*     */     finally
/*     */     {
/* 216 */       this.this_mon.exit();
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
/* 229 */       this.this_mon.enter();
/*     */       
/* 231 */       ensureOpen("FMFileUnlimited:write");
/*     */       
/* 233 */       writeSupport(buffer, position);
/*     */     }
/*     */     finally
/*     */     {
/* 237 */       this.this_mon.exit();
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
/* 249 */       this.this_mon.enter();
/*     */       
/* 251 */       ensureOpen("FMFileUnlimited:write");
/*     */       
/* 253 */       writeSupport(buffers, position);
/*     */     }
/*     */     finally
/*     */     {
/* 257 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */     throws FMFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 267 */       this.this_mon.enter();
/*     */       
/* 269 */       closeSupport(true);
/*     */     }
/*     */     finally
/*     */     {
/* 273 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/file/impl/FMFileUnlimited.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */