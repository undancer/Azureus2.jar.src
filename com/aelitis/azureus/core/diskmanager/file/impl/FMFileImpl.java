/*      */ package com.aelitis.azureus.core.diskmanager.file.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.diskmanager.file.FMFile;
/*      */ import com.aelitis.azureus.core.diskmanager.file.FMFileManagerException;
/*      */ import com.aelitis.azureus.core.diskmanager.file.FMFileOwner;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.io.RandomAccessFile;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
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
/*      */ public abstract class FMFileImpl
/*      */   implements FMFile
/*      */ {
/*      */   protected static final String READ_ACCESS_MODE = "r";
/*      */   protected static final String WRITE_ACCESS_MODE = "rw";
/*   50 */   private static final Map file_map = new HashMap();
/*   51 */   private static final AEMonitor file_map_mon = new AEMonitor("FMFile:map");
/*      */   private static final boolean OUTPUT_REOPEN_RELATED_ERRORS = true;
/*      */   private final FMFileManagerImpl manager;
/*      */   private final FMFileOwner owner;
/*      */   
/*      */   static
/*      */   {
/*   58 */     AEDiagnostics.addEvidenceGenerator(new AEDiagnosticsEvidenceGenerator()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void generate(IndentWriter writer)
/*      */       {
/*      */ 
/*   65 */         FMFileImpl.generateEvidence(writer);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*   72 */   private int access_mode = 1;
/*      */   
/*      */   private File linked_file;
/*      */   
/*      */   private String canonical_path;
/*      */   
/*      */   private RandomAccessFile raf;
/*      */   
/*      */   private FMFileAccessController file_access;
/*      */   private File created_dirs_leaf;
/*      */   private List created_dirs;
/*   83 */   protected final AEMonitor this_mon = new AEMonitor("FMFile");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean clone;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected FMFileImpl(FMFileOwner _owner, FMFileManagerImpl _manager, File _file, int _type)
/*      */     throws FMFileManagerException
/*      */   {
/*   96 */     this.owner = _owner;
/*   97 */     this.manager = _manager;
/*      */     
/*   99 */     TOTorrentFile tf = this.owner.getTorrentFile();
/*      */     
/*  101 */     this.linked_file = this.manager.getFileLink(tf.getTorrent(), tf.getIndex(), _file);
/*      */     
/*  103 */     boolean file_was_created = false;
/*  104 */     boolean file_reserved = false;
/*  105 */     boolean ok = false;
/*      */     
/*      */     try
/*      */     {
/*      */       try
/*      */       {
/*  111 */         this.canonical_path = this.linked_file.getCanonicalPath();
/*  112 */         if (this.canonical_path.equals(this.linked_file.getPath())) {
/*  113 */           this.canonical_path = this.linked_file.getPath();
/*      */         }
/*      */       }
/*      */       catch (IOException ioe) {
/*  117 */         String msg = ioe.getMessage();
/*      */         
/*  119 */         if ((msg != null) && (msg.contains("There are no more files")))
/*      */         {
/*  121 */           String abs_path = this.linked_file.getAbsolutePath();
/*      */           
/*  123 */           String error = "Caught 'There are no more files' exception during file.getCanonicalPath(). os=[" + Constants.OSName + "], file.getPath()=[" + this.linked_file.getPath() + "], file.getAbsolutePath()=[" + abs_path + "]. ";
/*      */           
/*      */ 
/*  126 */           Debug.out(error, ioe);
/*      */         }
/*      */         
/*  129 */         throw ioe;
/*      */       }
/*      */       
/*  132 */       createDirs(this.linked_file);
/*      */       
/*  134 */       reserveFile();
/*      */       
/*  136 */       file_reserved = true;
/*      */       
/*  138 */       this.file_access = new FMFileAccessController(this, _type);
/*      */       
/*  140 */       ok = true;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  144 */       if (file_was_created)
/*      */       {
/*  146 */         this.linked_file.delete();
/*      */       }
/*      */       
/*  149 */       deleteDirs();
/*      */       
/*  151 */       if ((e instanceof FMFileManagerException))
/*      */       {
/*  153 */         throw ((FMFileManagerException)e);
/*      */       }
/*      */       
/*  156 */       throw new FMFileManagerException("initialisation failed", e);
/*      */     }
/*      */     finally
/*      */     {
/*  160 */       if ((file_reserved) && (!ok))
/*      */       {
/*  162 */         releaseFile();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected FMFileImpl(FMFileImpl basis)
/*      */     throws FMFileManagerException
/*      */   {
/*  173 */     this.owner = basis.owner;
/*  174 */     this.manager = basis.manager;
/*  175 */     this.linked_file = basis.linked_file;
/*  176 */     this.canonical_path = basis.canonical_path;
/*      */     
/*  178 */     this.clone = true;
/*      */     try
/*      */     {
/*  181 */       this.file_access = new FMFileAccessController(this, basis.file_access.getStorageType());
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  185 */       if ((e instanceof FMFileManagerException))
/*      */       {
/*  187 */         throw ((FMFileManagerException)e);
/*      */       }
/*      */       
/*  190 */       throw new FMFileManagerException("initialisation failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected FMFileManagerImpl getManager()
/*      */   {
/*  197 */     return this.manager;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getName()
/*      */   {
/*  203 */     return this.linked_file.toString();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean exists()
/*      */   {
/*  209 */     return this.linked_file.exists();
/*      */   }
/*      */   
/*      */ 
/*      */   public FMFileOwner getOwner()
/*      */   {
/*  215 */     return this.owner;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isClone()
/*      */   {
/*  221 */     return this.clone;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setStorageType(int new_type)
/*      */     throws FMFileManagerException
/*      */   {
/*      */     try
/*      */     {
/*  231 */       this.this_mon.enter();
/*      */       
/*  233 */       boolean was_open = isOpen();
/*      */       
/*  235 */       if (was_open)
/*      */       {
/*  237 */         closeSupport(false);
/*      */       }
/*      */       try
/*      */       {
/*  241 */         this.file_access.setStorageType(new_type);
/*      */       }
/*      */       finally
/*      */       {
/*  245 */         if (was_open)
/*      */         {
/*  247 */           openSupport("Re-open after storage type change");
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  253 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getStorageType()
/*      */   {
/*  260 */     return this.file_access.getStorageType();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getAccessMode()
/*      */   {
/*  266 */     return this.access_mode;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setAccessModeSupport(int mode)
/*      */   {
/*  273 */     this.access_mode = mode;
/*      */   }
/*      */   
/*      */ 
/*      */   protected File getLinkedFile()
/*      */   {
/*  279 */     return this.linked_file;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void moveFile(File new_unlinked_file)
/*      */     throws FMFileManagerException
/*      */   {
/*      */     try
/*      */     {
/*  289 */       this.this_mon.enter();
/*      */       
/*  291 */       TOTorrentFile tf = this.owner.getTorrentFile();
/*      */       
/*      */ 
/*      */ 
/*  295 */       File new_linked_file = this.manager.getFileLink(tf.getTorrent(), tf.getIndex(), new_unlinked_file);
/*      */       String new_canonical_path;
/*      */       try
/*      */       {
/*      */         try
/*      */         {
/*  301 */           new_canonical_path = new_linked_file.getCanonicalPath();
/*      */ 
/*      */         }
/*      */         catch (IOException ioe)
/*      */         {
/*  306 */           String msg = ioe.getMessage();
/*      */           
/*  308 */           if ((msg != null) && (msg.contains("There are no more files"))) {
/*  309 */             String abs_path = new_linked_file.getAbsolutePath();
/*  310 */             String error = "Caught 'There are no more files' exception during new_file.getCanonicalPath(). os=[" + Constants.OSName + "], new_file.getPath()=[" + new_linked_file.getPath() + "], new_file.getAbsolutePath()=[" + abs_path + "]. ";
/*      */             
/*      */ 
/*  313 */             Debug.out(error, ioe);
/*      */           }
/*  315 */           throw ioe;
/*      */         }
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  320 */         throw new FMFileManagerException("getCanonicalPath fails", e);
/*      */       }
/*      */       
/*  323 */       if (new_linked_file.exists())
/*      */       {
/*  325 */         throw new FMFileManagerException("moveFile fails - file '" + new_canonical_path + "' already exists");
/*      */       }
/*      */       
/*  328 */       boolean was_open = isOpen();
/*      */       
/*  330 */       close();
/*      */       
/*  332 */       createDirs(new_linked_file);
/*      */       
/*  334 */       if ((!this.linked_file.exists()) || (FileUtil.renameFile(this.linked_file, new_linked_file)))
/*      */       {
/*  336 */         this.linked_file = new_linked_file;
/*  337 */         this.canonical_path = new_canonical_path;
/*      */         
/*  339 */         reserveFile();
/*      */         
/*  341 */         if (was_open)
/*      */         {
/*  343 */           ensureOpen("moveFile target");
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*      */         try {
/*  349 */           reserveFile();
/*      */         }
/*      */         catch (FMFileManagerException e)
/*      */         {
/*  353 */           Debug.printStackTrace(e);
/*      */         }
/*      */         
/*  356 */         if (was_open) {
/*      */           try
/*      */           {
/*  359 */             ensureOpen("moveFile recovery");
/*      */           }
/*      */           catch (FMFileManagerException e)
/*      */           {
/*  363 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */         
/*  367 */         throw new FMFileManagerException("moveFile fails");
/*      */       }
/*      */     }
/*      */     finally {
/*  371 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void renameFile(String new_name)
/*      */     throws FMFileManagerException
/*      */   {
/*      */     try
/*      */     {
/*  382 */       this.this_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*  386 */       File new_linked_file = new File(this.linked_file.getParentFile(), new_name);
/*      */       String new_canonical_path;
/*      */       try
/*      */       {
/*      */         try
/*      */         {
/*  392 */           new_canonical_path = new_linked_file.getCanonicalPath();
/*      */ 
/*      */         }
/*      */         catch (IOException ioe)
/*      */         {
/*  397 */           String msg = ioe.getMessage();
/*      */           
/*  399 */           if ((msg != null) && (msg.contains("There are no more files"))) {
/*  400 */             String abs_path = new_linked_file.getAbsolutePath();
/*  401 */             String error = "Caught 'There are no more files' exception during new_file.getCanonicalPath(). os=[" + Constants.OSName + "], new_file.getPath()=[" + new_linked_file.getPath() + "], new_file.getAbsolutePath()=[" + abs_path + "]. ";
/*      */             
/*      */ 
/*  404 */             Debug.out(error, ioe);
/*      */           }
/*  406 */           throw ioe;
/*      */         }
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  411 */         throw new FMFileManagerException("getCanonicalPath fails", e);
/*      */       }
/*      */       
/*  414 */       if (new_linked_file.exists())
/*      */       {
/*  416 */         throw new FMFileManagerException("renameFile fails - file '" + new_canonical_path + "' already exists");
/*      */       }
/*      */       
/*  419 */       boolean was_open = isOpen();
/*      */       
/*  421 */       close();
/*      */       
/*  423 */       if ((!this.linked_file.exists()) || (this.linked_file.renameTo(new_linked_file)))
/*      */       {
/*  425 */         this.linked_file = new_linked_file;
/*  426 */         this.canonical_path = new_canonical_path;
/*      */         
/*  428 */         reserveFile();
/*      */         
/*  430 */         if (was_open)
/*      */         {
/*  432 */           ensureOpen("renameFile target");
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*      */         try {
/*  438 */           reserveFile();
/*      */         }
/*      */         catch (FMFileManagerException e)
/*      */         {
/*  442 */           Debug.printStackTrace(e);
/*      */         }
/*      */         
/*  445 */         if (was_open) {
/*      */           try
/*      */           {
/*  448 */             ensureOpen("renameFile recovery");
/*      */           }
/*      */           catch (FMFileManagerException e)
/*      */           {
/*  452 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */         
/*  456 */         throw new FMFileManagerException("renameFile fails");
/*      */       }
/*      */     }
/*      */     finally {
/*  460 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void ensureOpen(String reason)
/*      */     throws FMFileManagerException
/*      */   {
/*      */     try
/*      */     {
/*  471 */       this.this_mon.enter();
/*      */       
/*  473 */       if (isOpen()) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/*  478 */       openSupport(reason);
/*      */     }
/*      */     finally
/*      */     {
/*  482 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected long getLengthSupport()
/*      */     throws FMFileManagerException
/*      */   {
/*      */     try
/*      */     {
/*  492 */       return this.file_access.getLength(this.raf);
/*      */     }
/*      */     catch (FMFileManagerException e)
/*      */     {
/*  496 */       Debug.printStackTrace(e);
/*      */       try
/*      */       {
/*  499 */         reopen(e);
/*      */         
/*  501 */         return this.file_access.getLength(this.raf);
/*      */       }
/*      */       catch (Throwable e2)
/*      */       {
/*  505 */         throw e;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setLengthSupport(long length)
/*      */     throws FMFileManagerException
/*      */   {
/*      */     try
/*      */     {
/*  517 */       this.file_access.setLength(this.raf, length);
/*      */     }
/*      */     catch (FMFileManagerException e)
/*      */     {
/*  521 */       Debug.printStackTrace(e);
/*      */       try
/*      */       {
/*  524 */         reopen(e);
/*      */         
/*  526 */         this.file_access.setLength(this.raf, length);
/*      */       }
/*      */       catch (Throwable e2)
/*      */       {
/*  530 */         throw e;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void reopen(FMFileManagerException cause)
/*      */     throws Throwable
/*      */   {
/*  541 */     if (!cause.isRecoverable())
/*      */     {
/*  543 */       throw cause;
/*      */     }
/*      */     
/*  546 */     if (this.raf != null)
/*      */     {
/*      */       try
/*      */       {
/*  550 */         this.raf.close();
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  561 */     this.file_access.aboutToOpen();
/*      */     
/*  563 */     this.raf = new RandomAccessFile(this.linked_file, this.access_mode == 1 ? "r" : "rw");
/*      */     
/*  565 */     Debug.outNoStack("Recovered connection to " + getName() + " after access failure");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void openSupport(String reason)
/*      */     throws FMFileManagerException
/*      */   {
/*  574 */     if (this.raf != null)
/*      */     {
/*  576 */       throw new FMFileManagerException("file already open");
/*      */     }
/*      */     
/*  579 */     reserveAccess(reason);
/*      */     try
/*      */     {
/*  582 */       this.file_access.aboutToOpen();
/*      */       
/*  584 */       this.raf = new RandomAccessFile(this.linked_file, this.access_mode == 1 ? "r" : "rw");
/*      */     }
/*      */     catch (FileNotFoundException e)
/*      */     {
/*  588 */       int st = this.file_access.getStorageType();
/*      */       
/*  590 */       boolean ok = false;
/*      */       
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/*  596 */         this.linked_file.getParentFile().mkdirs();
/*      */         
/*  598 */         this.linked_file.createNewFile();
/*      */         
/*  600 */         this.raf = new RandomAccessFile(this.linked_file, this.access_mode == 1 ? "r" : "rw");
/*      */         
/*  602 */         ok = true;
/*      */       }
/*      */       catch (Throwable f) {}
/*      */       
/*      */ 
/*  607 */       if (!ok)
/*      */       {
/*  609 */         Debug.printStackTrace(e);
/*      */         
/*  611 */         throw new FMFileManagerException("open fails", e);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  615 */       Debug.printStackTrace(e);
/*      */       
/*  617 */       throw new FMFileManagerException("open fails", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void closeSupport(boolean explicit)
/*      */     throws FMFileManagerException
/*      */   {
/*  627 */     FMFileManagerException flush_exception = null;
/*      */     try
/*      */     {
/*  630 */       flush();
/*      */     }
/*      */     catch (FMFileManagerException e)
/*      */     {
/*  634 */       flush_exception = e;
/*      */     }
/*      */     
/*  637 */     if (this.raf == null)
/*      */     {
/*      */ 
/*      */ 
/*  641 */       if (explicit)
/*      */       {
/*  643 */         releaseFile();
/*      */         
/*  645 */         deleteDirs();
/*      */       }
/*      */     }
/*      */     else {
/*      */       try {
/*  650 */         this.raf.close();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  654 */         throw new FMFileManagerException("close fails", e);
/*      */       }
/*      */       finally
/*      */       {
/*  658 */         this.raf = null;
/*      */         
/*  660 */         if (explicit)
/*      */         {
/*  662 */           releaseFile();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  667 */     if (flush_exception != null)
/*      */     {
/*  669 */       throw flush_exception;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void flush()
/*      */     throws FMFileManagerException
/*      */   {
/*  678 */     this.file_access.flush();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean isPieceCompleteProcessingNeeded(int piece_number)
/*      */     throws FMFileManagerException
/*      */   {
/*  687 */     return this.file_access.isPieceCompleteProcessingNeeded(piece_number);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setPieceCompleteSupport(int piece_number, DirectByteBuffer piece_data)
/*      */     throws FMFileManagerException
/*      */   {
/*  697 */     this.file_access.setPieceComplete(this.raf, piece_number, piece_data);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void delete()
/*      */     throws FMFileManagerException
/*      */   {
/*  705 */     close();
/*      */     
/*  707 */     if (this.linked_file.exists())
/*      */     {
/*  709 */       if (!this.linked_file.delete())
/*      */       {
/*  711 */         throw new FMFileManagerException("Failed to delete '" + this.linked_file + "'");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void readSupport(DirectByteBuffer buffer, long position)
/*      */     throws FMFileManagerException
/*      */   {
/*  723 */     readSupport(new DirectByteBuffer[] { buffer }, position);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void readSupport(DirectByteBuffer[] buffers, long position)
/*      */     throws FMFileManagerException
/*      */   {
/*      */     try
/*      */     {
/*  735 */       this.file_access.read(this.raf, buffers, position);
/*      */     }
/*      */     catch (FMFileManagerException e)
/*      */     {
/*  739 */       Debug.printStackTrace(e);
/*      */       try
/*      */       {
/*  742 */         reopen(e);
/*      */         
/*  744 */         this.file_access.read(this.raf, buffers, position);
/*      */       }
/*      */       catch (Throwable e2)
/*      */       {
/*  748 */         throw e;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void writeSupport(DirectByteBuffer buffer, long position)
/*      */     throws FMFileManagerException
/*      */   {
/*  760 */     writeSupport(new DirectByteBuffer[] { buffer }, position);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void writeSupport(DirectByteBuffer[] buffers, long position)
/*      */     throws FMFileManagerException
/*      */   {
/*      */     try
/*      */     {
/*  772 */       this.file_access.write(this.raf, buffers, position);
/*      */     }
/*      */     catch (FMFileManagerException e)
/*      */     {
/*  776 */       Debug.printStackTrace(e);
/*      */       try
/*      */       {
/*  779 */         reopen(e);
/*      */         
/*  781 */         this.file_access.write(this.raf, buffers, position);
/*      */       }
/*      */       catch (Throwable e2)
/*      */       {
/*  785 */         throw e;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isOpen()
/*      */   {
/*  793 */     return this.raf != null;
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
/*      */ 
/*      */ 
/*      */   private void reserveFile()
/*      */     throws FMFileManagerException
/*      */   {
/*  810 */     if (this.clone)
/*      */     {
/*  812 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  816 */       file_map_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*  820 */       List owners = (List)file_map.get(this.canonical_path);
/*      */       
/*  822 */       if (owners == null)
/*      */       {
/*  824 */         owners = new ArrayList();
/*      */         
/*      */ 
/*      */ 
/*  828 */         file_map.put(this.canonical_path, owners);
/*      */       }
/*      */       
/*  831 */       for (Iterator it = owners.iterator(); it.hasNext();)
/*      */       {
/*  833 */         Object[] entry = (Object[])it.next();
/*      */         
/*  835 */         String entry_name = ((FMFileOwner)entry[0]).getName();
/*      */         
/*      */ 
/*      */ 
/*  839 */         if (this.owner.getName().equals(entry_name))
/*      */         {
/*      */ 
/*      */ 
/*  843 */           Debug.out("reserve file - entry already present");
/*      */           
/*  845 */           entry[1] = Boolean.FALSE; return;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  851 */       owners.add(new Object[] { this.owner, Boolean.FALSE, "<reservation>" });
/*      */     }
/*      */     finally
/*      */     {
/*  855 */       file_map_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void reserveAccess(String reason)
/*      */     throws FMFileManagerException
/*      */   {
/*  865 */     if (this.clone)
/*      */     {
/*  867 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  871 */       file_map_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*  875 */       List owners = (List)file_map.get(this.canonical_path);
/*      */       
/*  877 */       Object[] my_entry = null;
/*      */       
/*  879 */       if (owners == null)
/*      */       {
/*  881 */         Debug.out("reserveAccess fail");
/*      */         
/*  883 */         throw new FMFileManagerException("File '" + this.canonical_path + "' has not been reserved (no entries), '" + this.owner.getName() + "'");
/*      */       }
/*      */       
/*  886 */       for (Iterator it = owners.iterator(); it.hasNext();)
/*      */       {
/*  888 */         Object[] entry = (Object[])it.next();
/*      */         
/*  890 */         String entry_name = ((FMFileOwner)entry[0]).getName();
/*      */         
/*      */ 
/*      */ 
/*  894 */         if (this.owner.getName().equals(entry_name))
/*      */         {
/*  896 */           my_entry = entry;
/*      */         }
/*      */       }
/*      */       
/*  900 */       if (my_entry == null)
/*      */       {
/*  902 */         Debug.out("reserveAccess fail");
/*      */         
/*  904 */         throw new FMFileManagerException("File '" + this.canonical_path + "' has not been reserved (not found), '" + this.owner.getName() + "'");
/*      */       }
/*      */       
/*  907 */       my_entry[1] = Boolean.valueOf(this.access_mode == 2 ? 1 : false);
/*  908 */       my_entry[2] = reason;
/*      */       
/*  910 */       int read_access = 0;
/*  911 */       int write_access = 0;
/*  912 */       int write_access_lax = 0;
/*      */       
/*  914 */       TOTorrentFile my_torrent_file = this.owner.getTorrentFile();
/*      */       
/*  916 */       StringBuilder users_sb = owners.size() == 1 ? null : new StringBuilder(128);
/*      */       
/*  918 */       for (Iterator it = owners.iterator(); it.hasNext();)
/*      */       {
/*  920 */         Object[] entry = (Object[])it.next();
/*      */         
/*  922 */         FMFileOwner this_owner = (FMFileOwner)entry[0];
/*      */         
/*  924 */         if (((Boolean)entry[1]).booleanValue())
/*      */         {
/*  926 */           write_access++;
/*      */           
/*  928 */           TOTorrentFile this_tf = this_owner.getTorrentFile();
/*      */           
/*  930 */           if ((my_torrent_file != null) && (this_tf != null) && (my_torrent_file.getLength() == this_tf.getLength()))
/*      */           {
/*  932 */             write_access_lax++;
/*      */           }
/*      */           
/*  935 */           if (users_sb != null) {
/*  936 */             if (users_sb.length() > 0) {
/*  937 */               users_sb.append(",");
/*      */             }
/*  939 */             users_sb.append(this_owner.getName());
/*  940 */             users_sb.append(" [write]");
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  945 */           read_access++;
/*      */           
/*  947 */           if (users_sb != null) {
/*  948 */             if (users_sb.length() > 0) {
/*  949 */               users_sb.append(",");
/*      */             }
/*  951 */             users_sb.append(this_owner.getName());
/*  952 */             users_sb.append(" [read]");
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  957 */       if ((write_access > 1) || ((write_access == 1) && (read_access > 0)))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  963 */         if (!COConfigurationManager.getBooleanParameter("File.strict.locking"))
/*      */         {
/*  965 */           if (write_access_lax == write_access) {
/*      */             return;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  971 */         Debug.out("reserveAccess fail");
/*      */         
/*  973 */         throw new FMFileManagerException("File '" + this.canonical_path + "' is in use by '" + (users_sb == null ? "eh?" : users_sb.toString()) + "'");
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  978 */       file_map_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void releaseFile()
/*      */   {
/*  985 */     if (this.clone)
/*      */     {
/*  987 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  991 */       file_map_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*  995 */       List owners = (List)file_map.get(this.canonical_path);
/*      */       
/*  997 */       if (owners != null)
/*      */       {
/*  999 */         for (Iterator it = owners.iterator(); it.hasNext();)
/*      */         {
/* 1001 */           Object[] entry = (Object[])it.next();
/*      */           
/* 1003 */           if (this.owner.getName().equals(((FMFileOwner)entry[0]).getName()))
/*      */           {
/* 1005 */             it.remove();
/*      */             
/* 1007 */             break;
/*      */           }
/*      */         }
/*      */         
/* 1011 */         if (owners.size() == 0)
/*      */         {
/* 1013 */           file_map.remove(this.canonical_path);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1018 */       file_map_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void createDirs(File target)
/*      */     throws FMFileManagerException
/*      */   {
/* 1028 */     if (this.clone)
/*      */     {
/* 1030 */       return;
/*      */     }
/*      */     
/* 1033 */     deleteDirs();
/*      */     
/* 1035 */     File parent = target.getParentFile();
/*      */     
/* 1037 */     if (!parent.exists())
/*      */     {
/* 1039 */       List new_dirs = new ArrayList();
/*      */       
/* 1041 */       File current = parent;
/*      */       
/* 1043 */       while ((current != null) && (!current.exists()))
/*      */       {
/* 1045 */         new_dirs.add(current);
/*      */         
/* 1047 */         current = current.getParentFile();
/*      */       }
/*      */       
/* 1050 */       this.created_dirs_leaf = target;
/* 1051 */       this.created_dirs = new ArrayList();
/*      */       
/* 1053 */       if (FileUtil.mkdirs(parent))
/*      */       {
/* 1055 */         this.created_dirs = new_dirs;
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/*      */ 
/*      */ 
/* 1068 */           Thread.sleep(RandomUtils.nextInt(1000));
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/*      */ 
/* 1073 */         FileUtil.mkdirs(parent);
/*      */         
/* 1075 */         if (parent.isDirectory())
/*      */         {
/* 1077 */           this.created_dirs = new_dirs;
/*      */         }
/*      */         else
/*      */         {
/* 1081 */           throw new FMFileManagerException("Failed to create parent directory '" + parent + "'");
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void deleteDirs()
/*      */   {
/* 1090 */     if (this.clone)
/*      */     {
/* 1092 */       return;
/*      */     }
/*      */     
/* 1095 */     if (this.created_dirs_leaf != null)
/*      */     {
/*      */ 
/*      */ 
/* 1099 */       if (!this.created_dirs_leaf.exists())
/*      */       {
/* 1101 */         Iterator it = this.created_dirs.iterator();
/*      */         
/* 1103 */         while (it.hasNext())
/*      */         {
/* 1105 */           File dir = (File)it.next();
/*      */           
/* 1107 */           if ((!dir.exists()) || (!dir.isDirectory()))
/*      */             break;
/* 1109 */           File[] entries = dir.listFiles();
/*      */           
/* 1111 */           if ((entries != null) && (entries.length != 0)) {
/*      */             break;
/*      */           }
/*      */           
/* 1115 */           dir.delete();
/*      */         }
/*      */       }
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
/* 1128 */       this.created_dirs_leaf = null;
/* 1129 */       this.created_dirs = null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getString()
/*      */   {
/* 1136 */     File cPath = new File(this.canonical_path);
/*      */     String sPaths;
/* 1138 */     String sPaths; if (cPath.equals(this.linked_file)) {
/* 1139 */       sPaths = "can/link=" + Debug.secretFileName(this.canonical_path);
/*      */     } else {
/* 1141 */       sPaths = "can=" + Debug.secretFileName(this.canonical_path) + ",link=" + Debug.secretFileName(this.linked_file.toString());
/*      */     }
/* 1143 */     return sPaths + ",raf=" + this.raf + ",acc=" + this.access_mode + ",ctrl = " + this.file_access.getString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void generateEvidence(IndentWriter writer)
/*      */   {
/* 1151 */     writer.println(file_map.size() + " FMFile Reservations");
/*      */     try
/*      */     {
/* 1154 */       writer.indent();
/*      */       try
/*      */       {
/* 1157 */         file_map_mon.enter();
/*      */         
/* 1159 */         Iterator it = file_map.keySet().iterator();
/*      */         
/* 1161 */         while (it.hasNext())
/*      */         {
/* 1163 */           String key = (String)it.next();
/*      */           
/* 1165 */           List owners = (List)file_map.get(key);
/*      */           
/* 1167 */           Iterator it2 = owners.iterator();
/*      */           
/* 1169 */           String str = "";
/*      */           
/* 1171 */           while (it2.hasNext())
/*      */           {
/* 1173 */             Object[] entry = (Object[])it2.next();
/*      */             
/* 1175 */             FMFileOwner owner = (FMFileOwner)entry[0];
/* 1176 */             Boolean write = (Boolean)entry[1];
/* 1177 */             String reason = (String)entry[2];
/*      */             
/*      */ 
/* 1180 */             str = str + (str.length() == 0 ? "" : ", ") + owner.getName() + "[" + (write.booleanValue() ? "write" : "read") + "/" + reason + "]";
/*      */           }
/*      */           
/*      */ 
/* 1184 */           writer.println(Debug.secretFileName(key) + " -> " + str);
/*      */         }
/*      */       }
/*      */       finally {
/* 1188 */         file_map_mon.exit();
/*      */       }
/*      */       
/* 1191 */       FMFileManagerImpl.generateEvidence(writer);
/*      */     }
/*      */     finally
/*      */     {
/* 1195 */       writer.exdent();
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/file/impl/FMFileImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */