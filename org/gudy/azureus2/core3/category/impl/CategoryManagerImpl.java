/*     */ package org.gudy.azureus2.core3.category.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.rssgen.RSSGeneratorPlugin;
/*     */ import com.aelitis.azureus.core.rssgen.RSSGeneratorPlugin.Provider;
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.impl.TagTypeBase;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URL;
/*     */ import java.net.URLDecoder;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import org.gudy.azureus2.core3.category.Category;
/*     */ import org.gudy.azureus2.core3.category.CategoryManagerListener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.ListenerManager;
/*     */ import org.gudy.azureus2.core3.util.ListenerManagerDispatcher;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.core3.xml.util.XMLEscapeWriter;
/*     */ import org.gudy.azureus2.core3.xml.util.XUXmlWriter;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadScrapeResult;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageRequest;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageResponse;
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
/*     */ 
/*     */ 
/*     */ public class CategoryManagerImpl
/*     */   extends TagTypeBase
/*     */   implements RSSGeneratorPlugin.Provider
/*     */ {
/*  74 */   private static final int[] color_default = { 189, 178, 57 };
/*     */   
/*     */   private static final String PROVIDER = "categories";
/*     */   
/*     */   private static final String UNCAT_NAME = "__uncategorised__";
/*     */   
/*     */   private static final String ALL_NAME = "__all__";
/*     */   private static CategoryManagerImpl catMan;
/*  82 */   private static CategoryImpl catAll = null;
/*  83 */   private static CategoryImpl catUncategorized = null;
/*  84 */   private static boolean doneLoading = false;
/*  85 */   private static final AEMonitor class_mon = new AEMonitor("CategoryManager:class");
/*     */   
/*  87 */   private final Map<String, CategoryImpl> categories = new HashMap();
/*  88 */   private final AEMonitor categories_mon = new AEMonitor("Categories");
/*     */   
/*     */   private static final int LDT_CATEGORY_ADDED = 1;
/*     */   private static final int LDT_CATEGORY_REMOVED = 2;
/*     */   private static final int LDT_CATEGORY_CHANGED = 3;
/*  93 */   private final ListenerManager category_listeners = ListenerManager.createManager("CatListenDispatcher", new ListenerManagerDispatcher()
/*     */   {
/*     */ 
/*     */ 
/*     */ 
/*     */     public void dispatch(Object _listener, int type, Object value)
/*     */     {
/*     */ 
/*     */ 
/* 102 */       CategoryManagerListener target = (CategoryManagerListener)_listener;
/*     */       
/* 104 */       if (type == 1) {
/* 105 */         target.categoryAdded((Category)value);
/* 106 */       } else if (type == 2) {
/* 107 */         target.categoryRemoved((Category)value);
/* 108 */       } else if (type == 3) {
/* 109 */         target.categoryChanged((Category)value);
/*     */       }
/*     */     }
/*  93 */   });
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected CategoryManagerImpl()
/*     */   {
/* 116 */     super(1, 511, "Category");
/*     */     
/* 118 */     addTagType();
/*     */     
/* 120 */     loadCategories();
/*     */   }
/*     */   
/*     */   public void addCategoryManagerListener(CategoryManagerListener l) {
/* 124 */     this.category_listeners.addListener(l);
/*     */   }
/*     */   
/*     */   public void removeCategoryManagerListener(CategoryManagerListener l) {
/* 128 */     this.category_listeners.removeListener(l);
/*     */   }
/*     */   
/*     */   public static CategoryManagerImpl getInstance() {
/*     */     try {
/* 133 */       class_mon.enter();
/* 134 */       if (catMan == null)
/* 135 */         catMan = new CategoryManagerImpl();
/* 136 */       return catMan;
/*     */     }
/*     */     finally {
/* 139 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void loadCategories() {
/* 144 */     if (doneLoading)
/* 145 */       return;
/* 146 */     doneLoading = true;
/*     */     
/* 148 */     FileInputStream fin = null;
/* 149 */     BufferedInputStream bin = null;
/*     */     
/* 151 */     makeSpecialCategories();
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 156 */       File configFile = FileUtil.getUserFile("categories.config");
/* 157 */       fin = new FileInputStream(configFile);
/* 158 */       bin = new BufferedInputStream(fin, 8192);
/*     */       
/* 160 */       Map map = BDecoder.decode(bin);
/*     */       
/* 162 */       List catList = (List)map.get("categories");
/* 163 */       for (int i = 0; i < catList.size(); i++) {
/* 164 */         Map mCategory = (Map)catList.get(i);
/*     */         try {
/* 166 */           String catName = new String((byte[])mCategory.get("name"), "UTF8");
/*     */           
/* 168 */           Long l_maxup = (Long)mCategory.get("maxup");
/* 169 */           Long l_maxdown = (Long)mCategory.get("maxdown");
/* 170 */           Map<String, String> attributes = BDecoder.decodeStrings((Map)mCategory.get("attr"));
/*     */           
/* 172 */           if (attributes == null)
/*     */           {
/* 174 */             attributes = new HashMap();
/*     */           }
/*     */           
/* 177 */           if (catName.equals("__uncategorised__"))
/*     */           {
/* 179 */             catUncategorized.setUploadSpeed(l_maxup == null ? 0 : l_maxup.intValue());
/* 180 */             catUncategorized.setDownloadSpeed(l_maxdown == null ? 0 : l_maxdown.intValue());
/* 181 */             catUncategorized.setAttributes(attributes);
/*     */           }
/* 183 */           else if (catName.equals("__all__"))
/*     */           {
/* 185 */             catAll.setAttributes(attributes);
/*     */           }
/*     */           else {
/* 188 */             this.categories.put(catName, new CategoryImpl(this, catName, l_maxup == null ? 0 : l_maxup.intValue(), l_maxdown == null ? 0 : l_maxdown.intValue(), attributes));
/*     */ 
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */         }
/*     */         catch (UnsupportedEncodingException e1) {}
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (FileNotFoundException e) {}catch (Exception e)
/*     */     {
/*     */ 
/*     */ 
/* 207 */       Debug.printStackTrace(e);
/*     */     }
/*     */     finally {
/*     */       try {
/* 211 */         if (bin != null) {
/* 212 */           bin.close();
/*     */         }
/*     */       } catch (Exception e) {}
/*     */       try {
/* 216 */         if (fin != null) {
/* 217 */           fin.close();
/*     */         }
/*     */       }
/*     */       catch (Exception e) {}
/* 221 */       checkConfig();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void saveCategories(Category category) {
/* 226 */     saveCategories();
/*     */     
/* 228 */     this.category_listeners.dispatch(3, category);
/*     */   }
/*     */   
/*     */   protected void saveCategories() {
/* 232 */     try { this.categories_mon.enter();
/*     */       
/* 234 */       Map map = new HashMap();
/* 235 */       List list = new ArrayList(this.categories.size());
/*     */       
/* 237 */       Iterator<CategoryImpl> iter = this.categories.values().iterator();
/* 238 */       while (iter.hasNext()) {
/* 239 */         CategoryImpl cat = (CategoryImpl)iter.next();
/*     */         
/* 241 */         if (cat.getType() == 0) {
/* 242 */           Map catMap = new HashMap();
/* 243 */           catMap.put("name", cat.getName());
/* 244 */           catMap.put("maxup", new Long(cat.getUploadSpeed()));
/* 245 */           catMap.put("maxdown", new Long(cat.getDownloadSpeed()));
/* 246 */           catMap.put("attr", cat.getAttributes());
/* 247 */           list.add(catMap);
/*     */         }
/*     */       }
/*     */       
/* 251 */       Map uncat = new HashMap();
/* 252 */       uncat.put("name", "__uncategorised__");
/* 253 */       uncat.put("maxup", new Long(catUncategorized.getUploadSpeed()));
/* 254 */       uncat.put("maxdown", new Long(catUncategorized.getDownloadSpeed()));
/* 255 */       uncat.put("attr", catUncategorized.getAttributes());
/* 256 */       list.add(uncat);
/*     */       
/* 258 */       Map allcat = new HashMap();
/* 259 */       allcat.put("name", "__all__");
/* 260 */       allcat.put("attr", catAll.getAttributes());
/* 261 */       list.add(allcat);
/*     */       
/* 263 */       map.put("categories", list);
/*     */       
/*     */ 
/* 266 */       FileOutputStream fos = null;
/*     */       
/*     */       try
/*     */       {
/* 270 */         byte[] torrentData = BEncoder.encode(map);
/*     */         
/* 272 */         File oldFile = FileUtil.getUserFile("categories.config");
/* 273 */         File newFile = FileUtil.getUserFile("categories.config.new");
/*     */         
/*     */ 
/* 276 */         fos = new FileOutputStream(newFile);
/* 277 */         fos.write(torrentData);
/* 278 */         fos.flush();
/* 279 */         fos.getFD().sync();
/*     */         
/*     */ 
/* 282 */         fos.close();
/* 283 */         fos = null;
/*     */         
/*     */ 
/* 286 */         if ((!oldFile.exists()) || (oldFile.delete()))
/*     */         {
/* 288 */           newFile.renameTo(oldFile);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         try
/*     */         {
/* 297 */           if (fos != null) {
/* 298 */             fos.close();
/*     */           }
/*     */         }
/*     */         catch (Exception e) {}
/*     */         
/*     */ 
/* 304 */         checkConfig();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 293 */         Debug.printStackTrace(e);
/*     */       }
/*     */       finally {
/*     */         try {
/* 297 */           if (fos != null) {
/* 298 */             fos.close();
/*     */           }
/*     */         }
/*     */         catch (Exception e) {}
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 306 */       this.categories_mon.exit();
/*     */     }
/*     */     finally
/*     */     {
/* 304 */       checkConfig();
/*     */       
/* 306 */       this.categories_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public Category createCategory(String name) {
/* 311 */     makeSpecialCategories();
/* 312 */     CategoryImpl newCategory = getCategory(name);
/* 313 */     if (newCategory == null) {
/* 314 */       newCategory = new CategoryImpl(this, name, 0, 0, new HashMap());
/* 315 */       this.categories.put(name, newCategory);
/* 316 */       saveCategories();
/*     */       
/* 318 */       this.category_listeners.dispatch(1, newCategory);
/* 319 */       return (Category)this.categories.get(name);
/*     */     }
/* 321 */     return newCategory;
/*     */   }
/*     */   
/*     */   public void removeCategory(Category category) {
/* 325 */     if (this.categories.containsKey(category.getName())) {
/* 326 */       CategoryImpl old = (CategoryImpl)this.categories.remove(category.getName());
/* 327 */       saveCategories();
/* 328 */       this.category_listeners.dispatch(2, category);
/*     */       
/* 330 */       if (old != null) {
/* 331 */         old.destroy();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public Category[] getCategories() {
/* 337 */     if (this.categories.size() > 0)
/* 338 */       return (Category[])this.categories.values().toArray(new Category[this.categories.size()]);
/* 339 */     return new Category[0];
/*     */   }
/*     */   
/*     */   public CategoryImpl getCategory(String name) {
/* 343 */     return (CategoryImpl)this.categories.get(name);
/*     */   }
/*     */   
/*     */   public Category getCategory(int type) {
/* 347 */     if (type == 1)
/* 348 */       return catAll;
/* 349 */     if (type == 2)
/* 350 */       return catUncategorized;
/* 351 */     return null;
/*     */   }
/*     */   
/*     */   private void makeSpecialCategories() {
/* 355 */     if (catAll == null) {
/* 356 */       catAll = new CategoryImpl(this, "Categories.all", 1, new HashMap());
/* 357 */       this.categories.put("Categories.all", catAll);
/*     */     }
/*     */     
/* 360 */     if (catUncategorized == null) {
/* 361 */       catUncategorized = new CategoryImpl(this, "Categories.uncategorized", 2, new HashMap());
/* 362 */       this.categories.put("Categories.uncategorized", catUncategorized);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int[] getColorDefault()
/*     */   {
/* 370 */     return color_default;
/*     */   }
/*     */   
/*     */ 
/*     */   public List<Tag> getTags()
/*     */   {
/* 376 */     return new ArrayList(this.categories.values());
/*     */   }
/*     */   
/*     */ 
/*     */   private void checkConfig()
/*     */   {
/* 382 */     boolean gen_enabled = false;
/*     */     
/* 384 */     for (CategoryImpl cat : this.categories.values())
/*     */     {
/* 386 */       if (cat.getBooleanAttribute("at_rss_gen"))
/*     */       {
/* 388 */         gen_enabled = true;
/*     */         
/* 390 */         break;
/*     */       }
/*     */     }
/*     */     
/* 394 */     if (gen_enabled)
/*     */     {
/* 396 */       RSSGeneratorPlugin.registerProvider("categories", this);
/*     */     }
/*     */     else
/*     */     {
/* 400 */       RSSGeneratorPlugin.unregisterProvider("categories");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 407 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean generate(TrackerWebPageRequest request, TrackerWebPageResponse response)
/*     */     throws IOException
/*     */   {
/* 417 */     URL url = request.getAbsoluteURL();
/*     */     
/* 419 */     String path = url.getPath();
/*     */     
/* 421 */     int pos = path.indexOf('?');
/*     */     
/* 423 */     if (pos != -1)
/*     */     {
/* 425 */       path = path.substring(0, pos);
/*     */     }
/*     */     
/* 428 */     path = path.substring("categories".length() + 1);
/*     */     
/* 430 */     XMLEscapeWriter pw = new XMLEscapeWriter(new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8")));
/*     */     
/* 432 */     pw.setEnabled(false);
/*     */     
/* 434 */     if (path.length() <= 1)
/*     */     {
/* 436 */       response.setContentType("text/html; charset=UTF-8");
/*     */       
/* 438 */       pw.println("<HTML><HEAD><TITLE>Vuze Category Feeds</TITLE></HEAD><BODY>");
/*     */       
/* 440 */       Map<String, String> lines = new TreeMap();
/*     */       
/*     */       List<CategoryImpl> cats;
/*     */       try
/*     */       {
/* 445 */         this.categories_mon.enter();
/*     */         
/* 447 */         cats = new ArrayList(this.categories.values());
/*     */       }
/*     */       finally
/*     */       {
/* 451 */         this.categories_mon.exit();
/*     */       }
/*     */       
/* 454 */       for (CategoryImpl c : cats)
/*     */       {
/* 456 */         if (c.getBooleanAttribute("at_rss_gen"))
/*     */         {
/* 458 */           String name = getDisplayName(c);
/*     */           
/* 460 */           String cat_url = "categories/" + URLEncoder.encode(c.getName(), "UTF-8");
/*     */           
/* 462 */           lines.put(name, "<LI><A href=\"" + cat_url + "\">" + name + "</A></LI>");
/*     */         }
/*     */       }
/*     */       
/* 466 */       for (String line : lines.values())
/*     */       {
/* 468 */         pw.println(line);
/*     */       }
/*     */       
/* 471 */       pw.println("</BODY></HTML>");
/*     */     }
/*     */     else
/*     */     {
/* 475 */       String cat_name = URLDecoder.decode(path.substring(1), "UTF-8");
/*     */       
/*     */       CategoryImpl cat;
/*     */       try
/*     */       {
/* 480 */         this.categories_mon.enter();
/*     */         
/* 482 */         cat = (CategoryImpl)this.categories.get(cat_name);
/*     */       }
/*     */       finally
/*     */       {
/* 486 */         this.categories_mon.exit();
/*     */       }
/*     */       
/* 489 */       if (cat == null)
/*     */       {
/* 491 */         response.setReplyStatus(404);
/*     */         
/* 493 */         return true;
/*     */       }
/*     */       
/* 496 */       Object dms = cat.getDownloadManagers(AzureusCoreFactory.getSingleton().getGlobalManager().getDownloadManagers());
/*     */       
/* 498 */       List<Download> downloads = new ArrayList(((List)dms).size());
/*     */       
/* 500 */       long dl_marker = 0L;
/*     */       
/* 502 */       for (DownloadManager dm : (List)dms)
/*     */       {
/* 504 */         TOTorrent torrent = dm.getTorrent();
/*     */         
/* 506 */         if (torrent != null)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 511 */           if (!TorrentUtils.isReallyPrivate(torrent))
/*     */           {
/* 513 */             dl_marker += dm.getDownloadState().getLongParameter("stats.download.added.time");
/*     */             
/* 515 */             downloads.add(PluginCoreUtils.wrap(dm));
/*     */           }
/*     */         }
/*     */       }
/* 519 */       String config_key = "cat.rss.config." + Base32.encode(cat.getName().getBytes("UTF-8"));
/*     */       
/* 521 */       long old_marker = COConfigurationManager.getLongParameter(config_key + ".marker", 0L);
/*     */       
/* 523 */       long last_modified = COConfigurationManager.getLongParameter(config_key + ".last_mod", 0L);
/*     */       
/* 525 */       long now = SystemTime.getCurrentTime();
/*     */       
/* 527 */       if (old_marker == dl_marker)
/*     */       {
/* 529 */         if (last_modified == 0L)
/*     */         {
/* 531 */           last_modified = now;
/*     */         }
/*     */       }
/*     */       else {
/* 535 */         COConfigurationManager.setParameter(config_key + ".marker", dl_marker);
/*     */         
/* 537 */         last_modified = now;
/*     */       }
/*     */       
/* 540 */       if (last_modified == now)
/*     */       {
/* 542 */         COConfigurationManager.setParameter(config_key + ".last_mod", last_modified);
/*     */       }
/*     */       
/* 545 */       response.setContentType("application/xml; charset=UTF-8");
/*     */       
/* 547 */       pw.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
/*     */       
/* 549 */       pw.println("<rss version=\"2.0\" xmlns:vuze=\"http://www.vuze.com\">");
/*     */       
/* 551 */       pw.println("<channel>");
/*     */       
/* 553 */       pw.println("<title>" + escape(getDisplayName(cat)) + "</title>");
/*     */       
/* 555 */       Collections.sort(downloads, new Comparator()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public int compare(Download d1, Download d2)
/*     */         {
/*     */ 
/*     */ 
/* 564 */           long added1 = CategoryManagerImpl.this.getAddedTime(d1) / 1000L;
/* 565 */           long added2 = CategoryManagerImpl.this.getAddedTime(d2) / 1000L;
/*     */           
/* 567 */           return (int)(added2 - added1);
/*     */         }
/*     */         
/*     */ 
/* 571 */       });
/* 572 */       pw.println("<pubDate>" + TimeFormatter.getHTTPDate(last_modified) + "</pubDate>");
/*     */       
/* 574 */       for (int i = 0; i < downloads.size(); i++)
/*     */       {
/* 576 */         Download download = (Download)downloads.get(i);
/*     */         
/* 578 */         DownloadManager core_download = PluginCoreUtils.unwrap(download);
/*     */         
/* 580 */         Torrent torrent = download.getTorrent();
/*     */         
/* 582 */         byte[] hash = torrent.getHash();
/*     */         
/* 584 */         String hash_str = Base32.encode(hash);
/*     */         
/* 586 */         pw.println("<item>");
/*     */         
/* 588 */         pw.println("<title>" + escape(download.getName()) + "</title>");
/*     */         
/* 590 */         pw.println("<guid>" + hash_str + "</guid>");
/*     */         
/* 592 */         String magnet_url = escape(UrlUtils.getMagnetURI(download));
/*     */         
/* 594 */         pw.println("<link>" + magnet_url + "</link>");
/*     */         
/* 596 */         long added = core_download.getDownloadState().getLongParameter("stats.download.added.time");
/*     */         
/* 598 */         pw.println("<pubDate>" + TimeFormatter.getHTTPDate(added) + "</pubDate>");
/*     */         
/* 600 */         pw.println("<vuze:size>" + torrent.getSize() + "</vuze:size>");
/* 601 */         pw.println("<vuze:assethash>" + hash_str + "</vuze:assethash>");
/*     */         
/* 603 */         pw.println("<vuze:downloadurl>" + magnet_url + "</vuze:downloadurl>");
/*     */         
/* 605 */         DownloadScrapeResult scrape = download.getLastScrapeResult();
/*     */         
/* 607 */         if ((scrape != null) && (scrape.getResponseType() == 1))
/*     */         {
/* 609 */           pw.println("<vuze:seeds>" + scrape.getSeedCount() + "</vuze:seeds>");
/* 610 */           pw.println("<vuze:peers>" + scrape.getNonSeedCount() + "</vuze:peers>");
/*     */         }
/*     */         
/* 613 */         pw.println("</item>");
/*     */       }
/*     */       
/* 616 */       pw.println("</channel>");
/*     */       
/* 618 */       pw.println("</rss>");
/*     */     }
/*     */     
/* 621 */     pw.flush();
/*     */     
/* 623 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String getDisplayName(CategoryImpl c)
/*     */   {
/* 630 */     if (c == catAll)
/*     */     {
/* 632 */       return MessageText.getString("Categories.all");
/*     */     }
/* 634 */     if (c == catUncategorized)
/*     */     {
/* 636 */       return MessageText.getString("Categories.uncategorized");
/*     */     }
/*     */     
/*     */ 
/* 640 */     return c.getName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected long getAddedTime(Download download)
/*     */   {
/* 648 */     DownloadManager core_download = PluginCoreUtils.unwrap(download);
/*     */     
/* 650 */     return core_download.getDownloadState().getLongParameter("stats.download.added.time");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String escape(String str)
/*     */   {
/* 657 */     return XUXmlWriter.escapeXML(str);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/category/impl/CategoryManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */