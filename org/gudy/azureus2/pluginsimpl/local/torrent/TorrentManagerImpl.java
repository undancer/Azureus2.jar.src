/*     */ package org.gudy.azureus2.pluginsimpl.local.torrent;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.internat.LocaleTorrentUtil;
/*     */ import org.gudy.azureus2.core3.internat.LocaleUtilEncodingException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentCreator;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentProgressListener;
/*     */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenOptions;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentCreator;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentCreatorListener;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentDownloader;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentEncodingException;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentException;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentManagerEvent;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentManagerListener;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentOptions;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TorrentManagerImpl
/*     */   implements TorrentManager, TOTorrentProgressListener
/*     */ {
/*     */   private static TorrentManagerImpl singleton;
/*  55 */   private static AEMonitor class_mon = new AEMonitor("TorrentManager");
/*     */   
/*  57 */   private static TorrentAttribute category_attribute = new TorrentAttributeCategoryImpl();
/*  58 */   private static TorrentAttribute share_properties_attribute = new TorrentAttributeSharePropertiesImpl();
/*  59 */   private static TorrentAttribute networks_attribute = new TorrentAttributeNetworksImpl();
/*  60 */   private static TorrentAttribute peer_sources_attribute = new TorrentAttributePeerSourcesImpl();
/*  61 */   private static TorrentAttribute tr_ext_attribute = new TorrentAttributeTrackerClientExtImpl();
/*  62 */   private static TorrentAttribute disp_name_attribute = new TorrentAttributeDisplayNameImpl();
/*  63 */   private static TorrentAttribute comment_attribute = new TorrentAttributeUserCommentImpl();
/*  64 */   private static TorrentAttribute relative_save_path_attribute = new TorrentAttributeRelativeSavePathImpl();
/*     */   
/*  66 */   private static Map<String, TorrentAttribute> attribute_map = new HashMap();
/*     */   
/*     */   static {
/*  69 */     attribute_map.put("Category", category_attribute);
/*  70 */     attribute_map.put("ShareProperties", share_properties_attribute);
/*  71 */     attribute_map.put("Networks", networks_attribute);
/*  72 */     attribute_map.put("PeerSources", peer_sources_attribute);
/*  73 */     attribute_map.put("TrackerClientExtensions", tr_ext_attribute);
/*  74 */     attribute_map.put("DisplayName", disp_name_attribute);
/*  75 */     attribute_map.put("UserComment", comment_attribute);
/*  76 */     attribute_map.put("RelativePath", relative_save_path_attribute);
/*     */   }
/*     */   
/*     */   public static TorrentManagerImpl getSingleton()
/*     */   {
/*     */     try
/*     */     {
/*  83 */       class_mon.enter();
/*     */       
/*  85 */       if (singleton == null)
/*     */       {
/*     */ 
/*     */ 
/*  89 */         singleton = new TorrentManagerImpl(null);
/*     */       }
/*     */       
/*  92 */       return singleton;
/*     */     }
/*     */     finally
/*     */     {
/*  96 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/* 100 */   protected static CopyOnWriteList<TorrentManagerListener> listeners = new CopyOnWriteList();
/*     */   
/*     */ 
/*     */   protected PluginInterface plugin_interface;
/*     */   
/*     */ 
/*     */   protected TorrentManagerImpl(PluginInterface _pi)
/*     */   {
/* 108 */     this.plugin_interface = _pi;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TorrentManager specialise(PluginInterface _pi)
/*     */   {
/* 117 */     return new TorrentManagerImpl(_pi);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public TorrentDownloader getURLDownloader(URL url)
/*     */     throws TorrentException
/*     */   {
/* 126 */     return new TorrentDownloaderImpl(this, url);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TorrentDownloader getURLDownloader(URL url, String user_name, String password)
/*     */     throws TorrentException
/*     */   {
/* 137 */     return new TorrentDownloaderImpl(this, url, user_name, password);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Torrent createFromBEncodedFile(File file)
/*     */     throws TorrentException
/*     */   {
/* 146 */     return createFromBEncodedFile(file, false);
/*     */   }
/*     */   
/*     */ 
/*     */   public Torrent createFromBEncodedFile(File file, boolean for_seeding)
/*     */     throws TorrentException
/*     */   {
/*     */     try
/*     */     {
/*     */       TOTorrent torrent;
/*     */       
/*     */       TOTorrent torrent;
/*     */       
/* 159 */       if (for_seeding)
/*     */       {
/* 161 */         torrent = TorrentUtils.readFromFile(file, true, true);
/*     */       }
/*     */       else
/*     */       {
/* 165 */         torrent = TorrentUtils.readFromFile(file, false);
/*     */       }
/*     */       
/* 168 */       return new TorrentImpl(this.plugin_interface, torrent);
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 172 */       throw new TorrentException("TorrentManager::createFromBEncodedFile Fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Torrent createFromBEncodedInputStream(InputStream data)
/*     */     throws TorrentException
/*     */   {
/*     */     try
/*     */     {
/* 183 */       return new TorrentImpl(this.plugin_interface, TorrentUtils.readFromBEncodedInputStream(data));
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 187 */       throw new TorrentException("TorrentManager::createFromBEncodedFile Fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Torrent createFromBEncodedData(byte[] data)
/*     */     throws TorrentException
/*     */   {
/* 197 */     ByteArrayInputStream is = null;
/*     */     try
/*     */     {
/* 200 */       is = new ByteArrayInputStream(data);
/*     */       
/* 202 */       return new TorrentImpl(this.plugin_interface, TorrentUtils.readFromBEncodedInputStream(is));
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 206 */       throw new TorrentException("TorrentManager::createFromBEncodedData Fails", e);
/*     */     }
/*     */     finally
/*     */     {
/*     */       try {
/* 211 */         is.close();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 215 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Torrent createFromDataFile(File data, URL announce_url)
/*     */     throws TorrentException
/*     */   {
/* 227 */     return createFromDataFile(data, announce_url, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Torrent createFromDataFile(File data, URL announce_url, boolean include_other_hashes)
/*     */     throws TorrentException
/*     */   {
/*     */     try
/*     */     {
/* 239 */       TOTorrentCreator c = TOTorrentFactory.createFromFileOrDirWithComputedPieceLength(data, announce_url, include_other_hashes);
/*     */       
/* 241 */       c.addListener(this);
/*     */       
/* 243 */       return new TorrentImpl(this.plugin_interface, c.create());
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 247 */       throw new TorrentException("TorrentManager::createFromDataFile Fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TorrentCreator createFromDataFileEx(File data, URL announce_url, boolean include_other_hashes)
/*     */     throws TorrentException
/*     */   {
/*     */     try
/*     */     {
/* 260 */       final TOTorrentCreator c = TOTorrentFactory.createFromFileOrDirWithComputedPieceLength(data, announce_url, include_other_hashes);
/*     */       
/* 262 */       new TorrentCreator()
/*     */       {
/*     */ 
/* 265 */         private CopyOnWriteList<TorrentCreatorListener> listeners = new CopyOnWriteList();
/*     */         
/*     */ 
/*     */         public void start()
/*     */         {
/* 270 */           c.addListener(new TOTorrentProgressListener()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void reportProgress(int percent_complete)
/*     */             {
/*     */ 
/* 277 */               for (Iterator<TorrentCreatorListener> it = TorrentManagerImpl.1.this.listeners.iterator(); it.hasNext();)
/*     */               {
/* 279 */                 ((TorrentCreatorListener)it.next()).reportPercentageDone(percent_complete);
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */             public void reportCurrentTask(String task_description)
/*     */             {
/* 287 */               for (Iterator<TorrentCreatorListener> it = TorrentManagerImpl.1.this.listeners.iterator(); it.hasNext();)
/*     */               {
/* 289 */                 ((TorrentCreatorListener)it.next()).reportActivity(task_description);
/*     */               }
/*     */               
/*     */             }
/* 293 */           });
/* 294 */           new AEThread2("TorrentManager::create")
/*     */           {
/*     */             public void run()
/*     */             {
/*     */               Iterator<TorrentCreatorListener> it;
/*     */               try {
/* 300 */                 TOTorrent t = TorrentManagerImpl.1.this.val$c.create();
/*     */                 
/* 302 */                 torrent = new TorrentImpl(TorrentManagerImpl.this.plugin_interface, t);
/*     */                 
/* 304 */                 for (it = TorrentManagerImpl.1.this.listeners.iterator(); it.hasNext();)
/*     */                 {
/* 306 */                   ((TorrentCreatorListener)it.next()).complete(torrent);
/*     */                 }
/*     */               } catch (TOTorrentException e) {
/*     */                 Torrent torrent;
/*     */                 Iterator<TorrentCreatorListener> it;
/* 311 */                 for (it = TorrentManagerImpl.1.this.listeners.iterator(); it.hasNext();)
/*     */                 {
/* 313 */                   ((TorrentCreatorListener)it.next()).failed(new TorrentException(e));
/*     */                 }
/*     */               }
/*     */             }
/*     */           }.start();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void cancel()
/*     */         {
/* 324 */           c.cancel();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void addListener(TorrentCreatorListener listener)
/*     */         {
/* 331 */           this.listeners.add(listener);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void removeListener(TorrentCreatorListener listener)
/*     */         {
/* 338 */           this.listeners.remove(listener);
/*     */         }
/*     */       };
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 344 */       throw new TorrentException("TorrentManager::createFromDataFile Fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */   public TorrentAttribute[] getDefinedAttributes()
/*     */   {
/*     */     try
/*     */     {
/* 352 */       class_mon.enter();
/*     */       
/* 354 */       Collection<TorrentAttribute> entries = attribute_map.values();
/*     */       
/* 356 */       TorrentAttribute[] res = new TorrentAttribute[entries.size()];
/*     */       
/* 358 */       entries.toArray(res);
/*     */       
/* 360 */       return res;
/*     */     }
/*     */     finally
/*     */     {
/* 364 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public TorrentAttribute getAttribute(String name)
/*     */   {
/*     */     try
/*     */     {
/* 373 */       class_mon.enter();
/*     */       
/* 375 */       TorrentAttribute res = (TorrentAttribute)attribute_map.get(name);
/*     */       
/* 377 */       if ((res == null) && (name.startsWith("Plugin.")))
/*     */       {
/* 379 */         res = new TorrentAttributePluginImpl(name);
/*     */         
/* 381 */         attribute_map.put(name, res);
/*     */       }
/*     */       
/* 384 */       if (res == null) throw new IllegalArgumentException("No such attribute: \"" + name + "\"");
/* 385 */       return res;
/*     */     }
/*     */     finally
/*     */     {
/* 389 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TorrentAttribute getPluginAttribute(String name)
/*     */   {
/* 399 */     name = "Plugin." + this.plugin_interface.getPluginID() + "." + name;
/*     */     try
/*     */     {
/* 402 */       class_mon.enter();
/*     */       
/* 404 */       TorrentAttribute res = (TorrentAttribute)attribute_map.get(name);
/*     */       TorrentAttribute localTorrentAttribute1;
/* 406 */       if (res != null)
/*     */       {
/* 408 */         return res;
/*     */       }
/*     */       
/* 411 */       res = new TorrentAttributePluginImpl(name);
/*     */       
/* 413 */       attribute_map.put(name, res);
/*     */       
/* 415 */       return res;
/*     */     }
/*     */     finally
/*     */     {
/* 419 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Torrent createFromBEncodedData(byte[] data, int preserve)
/*     */     throws TorrentException
/*     */   {
/* 430 */     ByteArrayInputStream bais = new ByteArrayInputStream(data);
/*     */     try {
/* 432 */       TOTorrent torrent = TOTorrentFactory.deserialiseFromBEncodedInputStream(bais);
/* 433 */       return new TorrentImpl(this.plugin_interface, preserveFields(torrent, preserve));
/*     */     } catch (TOTorrentException e) {
/* 435 */       throw new TorrentException("Failed to read TorrentData", e);
/*     */     } finally {
/*     */       try {
/* 438 */         bais.close();
/*     */       }
/*     */       catch (IOException e) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Torrent createFromBEncodedFile(File file, int preserve)
/*     */     throws TorrentException
/*     */   {
/* 451 */     FileInputStream fis = null;
/*     */     try {
/* 453 */       fis = new FileInputStream(file);
/* 454 */       TOTorrent torrent = TOTorrentFactory.deserialiseFromBEncodedInputStream(fis);
/* 455 */       return new TorrentImpl(this.plugin_interface, preserveFields(torrent, preserve));
/*     */     } catch (FileNotFoundException e) {
/* 457 */       throw new TorrentException("Failed to read from TorrentFile", e);
/*     */     } catch (TOTorrentException e) {
/* 459 */       throw new TorrentException("Failed to read TorrentData", e);
/*     */     } finally {
/* 461 */       if (fis != null) {
/*     */         try {
/* 463 */           fis.close();
/*     */         }
/*     */         catch (IOException e) {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public Torrent createFromBEncodedInputStream(InputStream data, int preserve) throws TorrentException {
/*     */     try {
/* 472 */       TOTorrent torrent = TOTorrentFactory.deserialiseFromBEncodedInputStream(data);
/* 473 */       return new TorrentImpl(this.plugin_interface, preserveFields(torrent, preserve));
/*     */     } catch (TOTorrentException e) {
/* 475 */       throw new TorrentException("Failed to read TorrentData", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private TOTorrent preserveFields(TOTorrent torrent, int preserve)
/*     */   {
/* 485 */     if (preserve == -1)
/* 486 */       return torrent;
/* 487 */     if ((preserve & 0x1) > 0) {
/* 488 */       String encoding = torrent.getAdditionalStringProperty("encoding");
/* 489 */       torrent.removeAdditionalProperties();
/* 490 */       if (encoding != null)
/* 491 */         torrent.setAdditionalStringProperty("encoding", encoding);
/* 492 */     } else if (preserve == 0) {
/* 493 */       torrent.removeAdditionalProperties();
/*     */     }
/* 495 */     return torrent;
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
/*     */   public void reportCurrentTask(final String task_description)
/*     */   {
/* 508 */     for (Iterator<TorrentManagerListener> it = listeners.iterator(); it.hasNext();)
/*     */     {
/* 510 */       ((TorrentManagerListener)it.next()).event(new TorrentManagerEvent()
/*     */       {
/*     */ 
/*     */         public int getType()
/*     */         {
/*     */ 
/* 516 */           return 1;
/*     */         }
/*     */         
/*     */ 
/*     */         public Object getData()
/*     */         {
/* 522 */           return task_description;
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void tryToSetTorrentEncoding(TOTorrent torrent, String encoding)
/*     */     throws TorrentEncodingException
/*     */   {
/*     */     try
/*     */     {
/* 536 */       LocaleTorrentUtil.setTorrentEncoding(torrent, encoding);
/*     */     }
/*     */     catch (LocaleUtilEncodingException e)
/*     */     {
/* 540 */       String[] charsets = e.getValidCharsets();
/*     */       
/* 542 */       if (charsets == null)
/*     */       {
/* 544 */         throw new TorrentEncodingException("Failed to set requested encoding", e);
/*     */       }
/*     */       
/*     */ 
/* 548 */       throw new TorrentEncodingException(charsets, e.getValidTorrentNames());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void tryToSetDefaultTorrentEncoding(TOTorrent torrent)
/*     */     throws TorrentException
/*     */   {
/*     */     try
/*     */     {
/* 560 */       LocaleTorrentUtil.setDefaultTorrentEncoding(torrent);
/*     */     }
/*     */     catch (LocaleUtilEncodingException e)
/*     */     {
/* 564 */       String[] charsets = e.getValidCharsets();
/*     */       
/* 566 */       if (charsets == null)
/*     */       {
/* 568 */         throw new TorrentEncodingException("Failed to set default encoding", e);
/*     */       }
/*     */       
/*     */ 
/* 572 */       throw new TorrentEncodingException(charsets, e.getValidTorrentNames());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 577 */   private Map<TorrentOpenOptions, TorrentOptionsImpl> too_state = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void fireEvent(final int type, final Object data)
/*     */   {
/* 584 */     TorrentManagerEvent ev = new TorrentManagerEvent()
/*     */     {
/*     */       public int getType()
/*     */       {
/* 588 */         return type;
/*     */       }
/*     */       
/*     */       public Object getData() {
/* 592 */         return data;
/*     */       }
/*     */     };
/*     */     
/* 596 */     for (TorrentManagerListener l : listeners) {
/*     */       try
/*     */       {
/* 599 */         l.event(ev);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 603 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class TorrentOptionsImpl
/*     */     implements TorrentOptions
/*     */   {
/*     */     private TorrentOpenOptions options;
/*     */     
/*     */ 
/*     */     private TorrentOptionsImpl(TorrentOpenOptions _options)
/*     */     {
/* 618 */       this.options = _options;
/*     */     }
/*     */     
/*     */ 
/*     */     public Torrent getTorrent()
/*     */     {
/* 624 */       return PluginCoreUtils.wrap(this.options.getTorrent());
/*     */     }
/*     */     
/*     */ 
/*     */     public void accept()
/*     */     {
/* 630 */       this.options.setCompleteAction(1);
/*     */     }
/*     */     
/*     */ 
/*     */     public void cancel()
/*     */     {
/* 636 */       this.options.setCompleteAction(2);
/*     */     }
/*     */     
/*     */ 
/*     */     public List<org.gudy.azureus2.plugins.tag.Tag> getTags()
/*     */     {
/* 642 */       List<org.gudy.azureus2.plugins.tag.Tag> tags = new ArrayList(this.options.getInitialTags());
/*     */       
/* 644 */       return tags;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void addTag(org.gudy.azureus2.plugins.tag.Tag tag)
/*     */     {
/* 651 */       List<com.aelitis.azureus.core.tag.Tag> tags = this.options.getInitialTags();
/*     */       
/* 653 */       if (!tags.contains(tag))
/*     */       {
/* 655 */         tags.add((com.aelitis.azureus.core.tag.Tag)tag);
/*     */         
/* 657 */         this.options.setInitialTags(tags);
/*     */         
/* 659 */         this.options.setDirty();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void removeTag(org.gudy.azureus2.plugins.tag.Tag tag)
/*     */     {
/* 667 */       List<com.aelitis.azureus.core.tag.Tag> tags = this.options.getInitialTags();
/*     */       
/* 669 */       if (tags.contains(tag))
/*     */       {
/* 671 */         tags.remove((com.aelitis.azureus.core.tag.Tag)tag);
/*     */         
/* 673 */         this.options.setInitialTags(tags);
/*     */         
/* 675 */         this.options.setDirty();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void optionsAdded(TorrentOpenOptions options)
/*     */   {
/* 684 */     TorrentOptionsImpl my_options = new TorrentOptionsImpl(options, null);
/*     */     
/* 686 */     synchronized (this.too_state)
/*     */     {
/* 688 */       this.too_state.put(options, my_options);
/*     */       
/* 690 */       fireEvent(2, my_options);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void optionsAccepted(TorrentOpenOptions options)
/*     */   {
/* 698 */     synchronized (this.too_state)
/*     */     {
/* 700 */       TorrentOptionsImpl my_options = (TorrentOptionsImpl)this.too_state.remove(options);
/*     */       
/* 702 */       if (my_options != null)
/*     */       {
/* 704 */         fireEvent(3, my_options);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void optionsRemoved(TorrentOpenOptions options)
/*     */   {
/* 713 */     synchronized (this.too_state)
/*     */     {
/* 715 */       TorrentOptionsImpl my_options = (TorrentOptionsImpl)this.too_state.remove(options);
/*     */       
/* 717 */       if (my_options != null)
/*     */       {
/* 719 */         fireEvent(4, my_options);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(TorrentManagerListener l)
/*     */   {
/* 728 */     listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(TorrentManagerListener l)
/*     */   {
/* 735 */     listeners.remove(l);
/*     */   }
/*     */   
/*     */   public void reportProgress(int percent_complete) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/torrent/TorrentManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */