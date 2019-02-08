/*     */ package com.aelitis.azureus.core.diskmanager.file.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.file.FMFile;
/*     */ import com.aelitis.azureus.core.diskmanager.file.FMFileManager;
/*     */ import com.aelitis.azureus.core.diskmanager.file.FMFileManagerException;
/*     */ import com.aelitis.azureus.core.diskmanager.file.FMFileOwner;
/*     */ import com.aelitis.azureus.core.util.LinkFileMap;
/*     */ import com.aelitis.azureus.core.util.LinkFileMap.Entry;
/*     */ import java.io.File;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
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
/*     */ public class FMFileManagerImpl
/*     */   implements FMFileManager
/*     */ {
/*     */   public static final boolean DEBUG = false;
/*     */   protected static FMFileManagerImpl singleton;
/*  47 */   protected static final AEMonitor class_mon = new AEMonitor("FMFileManager:class");
/*     */   protected final LinkedHashMap map;
/*     */   
/*     */   public static FMFileManager getSingleton()
/*     */   {
/*     */     try
/*     */     {
/*  54 */       class_mon.enter();
/*     */       
/*  56 */       if (singleton == null)
/*     */       {
/*  58 */         singleton = new FMFileManagerImpl();
/*     */       }
/*     */       
/*  61 */       return singleton;
/*     */     }
/*     */     finally
/*     */     {
/*  65 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*  70 */   protected final AEMonitor map_mon = new AEMonitor("FMFileManager:Map");
/*     */   
/*  72 */   protected final HashMap<Object, LinkFileMap> links = new HashMap();
/*  73 */   protected final AEMonitor links_mon = new AEMonitor("FMFileManager:Links");
/*     */   
/*     */   protected final boolean limited;
/*     */   
/*     */   protected final int limit_size;
/*     */   protected AESemaphore close_queue_sem;
/*     */   protected List close_queue;
/*  80 */   protected final AEMonitor close_queue_mon = new AEMonitor("FMFileManager:CQ");
/*     */   
/*     */   protected List files;
/*  83 */   protected final AEMonitor files_mon = new AEMonitor("FMFileManager:File");
/*     */   
/*     */ 
/*     */   protected FMFileManagerImpl()
/*     */   {
/*  88 */     this.limit_size = COConfigurationManager.getIntParameter("File Max Open");
/*     */     
/*  90 */     this.limited = (this.limit_size > 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  99 */     this.map = new LinkedHashMap(this.limit_size, 0.75F, true);
/*     */     
/* 101 */     if (this.limited)
/*     */     {
/* 103 */       this.close_queue_sem = new AESemaphore("FMFileManager::closeqsem");
/*     */       
/* 105 */       this.close_queue = new LinkedList();
/*     */       
/* 107 */       Thread t = new AEThread("FMFileManager::closeQueueDispatcher")
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/* 112 */           FMFileManagerImpl.this.closeQueueDispatch();
/*     */         }
/*     */         
/* 115 */       };
/* 116 */       t.setDaemon(true);
/*     */       
/* 118 */       t.start();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected LinkFileMap getLinksEntry(TOTorrent torrent)
/*     */   {
/*     */     Object links_key;
/*     */     
/*     */     try
/*     */     {
/* 130 */       links_key = torrent.getHashWrapper();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 134 */       Debug.printStackTrace(e);
/*     */       
/* 136 */       links_key = "";
/*     */     }
/*     */     
/* 139 */     LinkFileMap links_entry = (LinkFileMap)this.links.get(links_key);
/*     */     
/* 141 */     if (links_entry == null)
/*     */     {
/* 143 */       links_entry = new LinkFileMap();
/*     */       
/* 145 */       this.links.put(links_key, links_entry);
/*     */     }
/*     */     
/* 148 */     return links_entry;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setFileLinks(TOTorrent torrent, LinkFileMap new_links)
/*     */   {
/*     */     try
/*     */     {
/* 157 */       this.links_mon.enter();
/*     */       
/* 159 */       LinkFileMap links_entry = getLinksEntry(torrent);
/*     */       
/* 161 */       Iterator<LinkFileMap.Entry> it = new_links.entryIterator();
/*     */       
/* 163 */       while (it.hasNext())
/*     */       {
/* 165 */         LinkFileMap.Entry entry = (LinkFileMap.Entry)it.next();
/*     */         
/* 167 */         int index = entry.getIndex();
/*     */         
/* 169 */         File source = entry.getFromFile();
/* 170 */         File target = entry.getToFile();
/*     */         
/*     */ 
/*     */ 
/* 174 */         if ((target != null) && (!source.equals(target)))
/*     */         {
/* 176 */           if (index >= 0)
/*     */           {
/* 178 */             links_entry.put(index, source, target);
/*     */           }
/*     */           else
/*     */           {
/* 182 */             links_entry.putMigration(source, target);
/*     */           }
/*     */         }
/*     */         else {
/* 186 */           links_entry.remove(index, source);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 191 */       this.links_mon.exit();
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
/*     */   public File getFileLink(TOTorrent torrent, int file_index, File file)
/*     */   {
/*     */     try
/*     */     {
/* 213 */       this.links_mon.enter();
/*     */       
/* 215 */       LinkFileMap links_entry = getLinksEntry(torrent);
/*     */       
/* 217 */       LinkFileMap.Entry entry = links_entry.getEntry(file_index, file);
/*     */       
/* 219 */       File res = null;
/*     */       
/* 221 */       if (entry == null)
/*     */       {
/* 223 */         res = file;
/*     */ 
/*     */ 
/*     */       }
/* 227 */       else if (file.equals(entry.getFromFile()))
/*     */       {
/* 229 */         res = entry.getToFile();
/*     */       }
/*     */       else
/*     */       {
/* 233 */         res = file;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 239 */       return res;
/*     */     }
/*     */     finally
/*     */     {
/* 243 */       this.links_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public FMFile createFile(FMFileOwner owner, File file, int type)
/*     */     throws FMFileManagerException
/*     */   {
/*     */     FMFile res;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     FMFile res;
/*     */     
/*     */ 
/*     */ 
/* 263 */     if (this.limited)
/*     */     {
/* 265 */       res = new FMFileLimited(owner, this, file, type);
/*     */     }
/*     */     else
/*     */     {
/* 269 */       res = new FMFileUnlimited(owner, this, file, type);
/*     */     }
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
/* 286 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void getSlot(FMFileLimited file)
/*     */   {
/* 296 */     FMFileLimited oldest_file = null;
/*     */     try
/*     */     {
/* 299 */       this.map_mon.enter();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 305 */       if (this.map.size() >= this.limit_size)
/*     */       {
/* 307 */         Iterator it = this.map.keySet().iterator();
/*     */         
/* 309 */         oldest_file = (FMFileLimited)it.next();
/*     */         
/* 311 */         it.remove();
/*     */       }
/*     */       
/* 314 */       this.map.put(file, file);
/*     */     }
/*     */     finally
/*     */     {
/* 318 */       this.map_mon.exit();
/*     */     }
/*     */     
/* 321 */     if (oldest_file != null)
/*     */     {
/* 323 */       closeFile(oldest_file);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void releaseSlot(FMFileLimited file)
/*     */   {
/*     */     try
/*     */     {
/* 337 */       this.map_mon.enter();
/*     */       
/* 339 */       this.map.remove(file);
/*     */     }
/*     */     finally
/*     */     {
/* 343 */       this.map_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void usedSlot(FMFileLimited file)
/*     */   {
/*     */     try
/*     */     {
/* 356 */       this.map_mon.enter();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 361 */       if (this.map.containsKey(file))
/*     */       {
/* 363 */         this.map.put(file, file);
/*     */       }
/*     */     }
/*     */     finally {
/* 367 */       this.map_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void closeFile(FMFileLimited file)
/*     */   {
/*     */     try
/*     */     {
/* 380 */       this.close_queue_mon.enter();
/*     */       
/* 382 */       this.close_queue.add(file);
/*     */     }
/*     */     finally
/*     */     {
/* 386 */       this.close_queue_mon.exit();
/*     */     }
/*     */     
/* 389 */     this.close_queue_sem.release();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void closeQueueDispatch()
/*     */   {
/*     */     for (;;)
/*     */     {
/* 403 */       this.close_queue_sem.reserve();
/*     */       
/*     */ 
/* 406 */       FMFileLimited file = null;
/*     */       try
/*     */       {
/* 409 */         this.close_queue_mon.enter();
/*     */         
/* 411 */         if (this.close_queue.size() > 0)
/*     */         {
/* 413 */           file = (FMFileLimited)this.close_queue.remove(0);
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/*     */ 
/* 422 */         this.close_queue_mon.exit();
/*     */       }
/*     */       
/* 425 */       if (file != null) {
/*     */         try
/*     */         {
/* 428 */           file.close(false);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 432 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
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
/*     */   protected void generate(IndentWriter writer)
/*     */   {
/* 467 */     writer.println("FMFileManager slots");
/*     */     try
/*     */     {
/* 470 */       writer.indent();
/*     */       try
/*     */       {
/* 473 */         this.map_mon.enter();
/*     */         
/* 475 */         Iterator it = this.map.keySet().iterator();
/*     */         
/* 477 */         while (it.hasNext())
/*     */         {
/* 479 */           FMFileLimited file = (FMFileLimited)it.next();
/*     */           
/* 481 */           writer.println(file.getString());
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 486 */         this.map_mon.exit();
/*     */       }
/*     */     }
/*     */     finally {
/* 490 */       writer.exdent();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected static void generateEvidence(IndentWriter writer)
/*     */   {
/* 497 */     getSingleton();
/*     */     
/* 499 */     singleton.generate(writer);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/file/impl/FMFileManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */