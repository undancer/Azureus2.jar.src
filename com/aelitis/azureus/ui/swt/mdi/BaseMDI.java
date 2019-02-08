/*     */ package com.aelitis.azureus.ui.swt.mdi;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryCreationListener;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryCreationListener2;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryLoadedListener;
/*     */ import com.aelitis.azureus.ui.mdi.MdiListener;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinView;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.sidebar.SideBarEntrySWT;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import com.aelitis.azureus.util.ContentNetworkUtils;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Pattern;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.LightHashMap;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ConfigSectionHolder;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ConfigSectionRepository;
/*     */ import org.gudy.azureus2.ui.common.util.MenuItemManager;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils.MenuItemPluginMenuControllerImpl;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.PluginsMenuHelper;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.PluginsMenuHelper.IViewInfo;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.PluginsMenuHelper.PluginAddedViewListener;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCore;
/*     */ 
/*     */ public abstract class BaseMDI extends SkinView implements MultipleDocumentInterfaceSWT, UIUpdatable
/*     */ {
/*     */   protected MdiEntrySWT currentEntry;
/*  73 */   private Map<String, MdiEntryCreationListener> mapIdToCreationListener = new LightHashMap();
/*  74 */   private Map<String, MdiEntryCreationListener2> mapIdToCreationListener2 = new LightHashMap();
/*     */   
/*     */ 
/*  77 */   private Map<String, MdiEntry> mapIdToEntry = new LinkedHashMap(8);
/*     */   
/*  79 */   private List<MdiListener> listeners = new ArrayList();
/*     */   
/*  81 */   private List<MdiEntryLoadedListener> listLoadListeners = new ArrayList();
/*     */   
/*     */   private List<MdiSWTMenuHackListener> listMenuHackListners;
/*     */   
/*  85 */   private LinkedHashMap<String, Object> mapAutoOpen = new LinkedHashMap();
/*     */   
/*     */   private String[] preferredOrder;
/*     */   
/*  89 */   private boolean mapAutoOpenLoaded = false;
/*     */   
/*  91 */   private String closeableConfigFile = "sidebarauto.config";
/*     */   
/*     */   public void addListener(MdiListener l) {
/*  94 */     synchronized (this.listeners) {
/*  95 */       if (this.listeners.contains(l)) {
/*  96 */         return;
/*     */       }
/*  98 */       this.listeners.add(l);
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeListener(MdiListener l) {
/* 103 */     synchronized (this.listeners) {
/* 104 */       this.listeners.remove(l);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addListener(MdiEntryLoadedListener l) {
/* 109 */     synchronized (this.listLoadListeners) {
/* 110 */       if (this.listLoadListeners.contains(l)) {
/* 111 */         return;
/*     */       }
/* 113 */       this.listLoadListeners.add(l);
/*     */     }
/*     */     
/*     */ 
/* 117 */     MdiEntry[] entries = getEntries();
/* 118 */     for (MdiEntry entry : entries) {
/* 119 */       if (entry.isAdded()) {
/* 120 */         l.mdiEntryLoaded(entry);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeListener(MdiEntryLoadedListener l) {
/* 126 */     synchronized (this.listLoadListeners) {
/* 127 */       this.listLoadListeners.remove(l);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void triggerSelectionListener(MdiEntry newEntry, MdiEntry oldEntry) {
/*     */     MdiListener[] array;
/* 133 */     synchronized (this.listeners) {
/* 134 */       array = (MdiListener[])this.listeners.toArray(new MdiListener[0]);
/*     */     }
/* 136 */     for (MdiListener l : array) {
/*     */       try {
/* 138 */         l.mdiEntrySelected(newEntry, oldEntry);
/*     */       } catch (Exception e) {
/* 140 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/* 144 */     itemSelected(newEntry);
/*     */   }
/*     */   
/*     */   public void triggerEntryLoadedListeners(MdiEntry entry) {
/*     */     MdiEntryLoadedListener[] array;
/* 149 */     synchronized (this.listLoadListeners) {
/* 150 */       array = (MdiEntryLoadedListener[])this.listLoadListeners.toArray(new MdiEntryLoadedListener[0]);
/*     */     }
/* 152 */     for (MdiEntryLoadedListener l : array) {
/*     */       try {
/* 154 */         l.mdiEntryLoaded(entry);
/*     */       } catch (Exception e) {
/* 156 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void closeEntry(String id) {
/* 162 */     MdiEntry entry = getEntry(id);
/* 163 */     if (entry != null) {
/* 164 */       entry.close(false);
/*     */     } else {
/* 166 */       removeEntryAutoOpen(id);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public final MdiEntry createEntryFromEventListener(String parentID, UISWTViewEventListener l, String id, boolean closeable, Object datasource, String preferedAfterID)
/*     */   {
/* 173 */     return createEntryFromEventListener(parentID, null, l, id, closeable, datasource, preferedAfterID);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract MdiEntry createEntryFromEventListener(String paramString1, String paramString2, UISWTViewEventListener paramUISWTViewEventListener, String paramString3, boolean paramBoolean, Object paramObject, String paramString4);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract MdiEntry createEntryFromSkinRef(String paramString1, String paramString2, String paramString3, String paramString4, ViewTitleInfo paramViewTitleInfo, Object paramObject, boolean paramBoolean, String paramString5);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public MdiEntry createEntryFromEventListener(String parentID, Class<? extends UISWTViewEventListener> cla, String id, boolean closeable, Object data, String preferedAfterID)
/*     */   {
/* 192 */     MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/* 193 */     if (mdi == null) {
/* 194 */       return null;
/*     */     }
/*     */     
/* 197 */     if (id == null) {
/* 198 */       id = cla.getName();
/* 199 */       int i = id.lastIndexOf('.');
/* 200 */       if (i > 0) {
/* 201 */         id = id.substring(i + 1);
/*     */       }
/*     */     }
/*     */     
/* 205 */     MdiEntry entry = mdi.getEntry(id);
/* 206 */     if (entry != null) {
/* 207 */       if (data != null) {
/* 208 */         entry.setDatasource(data);
/*     */       }
/* 210 */       return entry;
/*     */     }
/* 212 */     UISWTViewEventListener l = null;
/* 213 */     if (data != null) {
/*     */       try {
/* 215 */         Constructor<?> constructor = cla.getConstructor(new Class[] { data.getClass() });
/*     */         
/*     */ 
/* 218 */         l = (UISWTViewEventListener)constructor.newInstance(new Object[] { data });
/*     */       }
/*     */       catch (Exception e) {}
/*     */     }
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 226 */       if (l == null) {
/* 227 */         l = (UISWTViewEventListener)cla.newInstance();
/*     */       }
/* 229 */       return mdi.createEntryFromEventListener(parentID, l, id, closeable, data, preferedAfterID);
/*     */     }
/*     */     catch (Exception e) {
/* 232 */       Debug.out(e);
/*     */     }
/*     */     
/* 235 */     return null;
/*     */   }
/*     */   
/*     */   public MdiEntry getCurrentEntry() {
/* 239 */     return this.currentEntry;
/*     */   }
/*     */   
/*     */   public MdiEntrySWT getCurrentEntrySWT() {
/* 243 */     return this.currentEntry;
/*     */   }
/*     */   
/*     */   public MdiEntry[] getEntries() {
/* 247 */     return getEntries(new MdiEntry[0]);
/*     */   }
/*     */   
/*     */   public MdiEntrySWT[] getEntriesSWT() {
/* 251 */     return (MdiEntrySWT[])getEntries(new MdiEntrySWT[0]);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public <T extends MdiEntry> T[] getEntries(T[] array)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 680	com/aelitis/azureus/ui/swt/mdi/BaseMDI:mapIdToEntry	Ljava/util/Map;
/*     */     //   4: dup
/*     */     //   5: astore_2
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: getfield 680	com/aelitis/azureus/ui/swt/mdi/BaseMDI:mapIdToEntry	Ljava/util/Map;
/*     */     //   11: invokeinterface 801 1 0
/*     */     //   16: aload_1
/*     */     //   17: invokeinterface 789 2 0
/*     */     //   22: checkcast 328	[Lcom/aelitis/azureus/ui/mdi/MdiEntry;
/*     */     //   25: aload_2
/*     */     //   26: monitorexit
/*     */     //   27: areturn
/*     */     //   28: astore_3
/*     */     //   29: aload_2
/*     */     //   30: monitorexit
/*     */     //   31: aload_3
/*     */     //   32: athrow
/*     */     // Line number table:
/*     */     //   Java source line #255	-> byte code offset #0
/*     */     //   Java source line #256	-> byte code offset #7
/*     */     //   Java source line #257	-> byte code offset #28
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	33	0	this	BaseMDI
/*     */     //   0	33	1	array	T[]
/*     */     //   5	25	2	Ljava/lang/Object;	Object
/*     */     //   28	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	27	28	finally
/*     */     //   28	31	28	finally
/*     */   }
/*     */   
/*     */   public MdiEntry getEntry(String id)
/*     */   {
/* 261 */     if ("browse".equalsIgnoreCase(id)) {
/* 262 */       id = ContentNetworkUtils.getTarget(ConstantsVuze.getDefaultContentNetwork());
/*     */     }
/* 264 */     synchronized (this.mapIdToEntry) {
/* 265 */       MdiEntry entry = (MdiEntry)this.mapIdToEntry.get(id);
/* 266 */       return entry;
/*     */     }
/*     */   }
/*     */   
/*     */   public MdiEntrySWT getEntrySWT(String id) {
/* 271 */     if ("browse".equalsIgnoreCase(id)) {
/* 272 */       id = ContentNetworkUtils.getTarget(ConstantsVuze.getDefaultContentNetwork());
/*     */     }
/* 274 */     synchronized (this.mapIdToEntry) {
/* 275 */       MdiEntrySWT entry = (MdiEntrySWT)this.mapIdToEntry.get(id);
/* 276 */       return entry;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MdiEntry getEntryBySkinView(Object skinView)
/*     */   {
/* 287 */     SWTSkinObject so = ((SkinView)skinView).getMainSkinObject();
/* 288 */     BaseMdiEntry[] sideBarEntries = (BaseMdiEntry[])getEntries(new BaseMdiEntry[0]);
/* 289 */     for (int i = 0; i < sideBarEntries.length; i++) {
/* 290 */       BaseMdiEntry entry = sideBarEntries[i];
/* 291 */       SWTSkinObject entrySO = entry.getSkinObject();
/* 292 */       SWTSkinObject entrySOParent = entrySO == null ? entrySO : entrySO.getParent();
/*     */       
/* 294 */       if ((entrySO == so) || (entrySO == so.getParent()) || (entrySOParent == so)) {
/* 295 */         return entry;
/*     */       }
/*     */     }
/* 298 */     return null;
/*     */   }
/*     */   
/*     */   public UISWTViewCore getCoreViewFromID(String id) {
/* 302 */     if (id == null) {
/* 303 */       return null;
/*     */     }
/* 305 */     MdiEntrySWT entry = getEntrySWT(id);
/* 306 */     if ((entry instanceof UISWTViewCore)) {
/* 307 */       return entry;
/*     */     }
/* 309 */     return null;
/*     */   }
/*     */   
/*     */   public String getUpdateUIName() {
/* 313 */     if (this.currentEntry == null) {
/* 314 */       return "MDI";
/*     */     }
/* 316 */     return this.currentEntry.getId();
/*     */   }
/*     */   
/*     */   public void registerEntry(String id, MdiEntryCreationListener2 l)
/*     */   {
/* 321 */     if (this.mapIdToCreationListener.containsKey(id)) {
/* 322 */       System.err.println("Warning: MDIEntry " + id + " Creation Listener being registered twice. " + Debug.getCompressedStackTrace());
/*     */     }
/*     */     
/*     */ 
/* 326 */     this.mapIdToCreationListener2.put(id, l);
/*     */     
/* 328 */     createIfAutoOpen(id);
/*     */   }
/*     */   
/*     */   public void deregisterEntry(String id, MdiEntryCreationListener2 l)
/*     */   {
/* 333 */     MdiEntryCreationListener2 l2 = (MdiEntryCreationListener2)this.mapIdToCreationListener2.get(id);
/* 334 */     if (l == l2) {
/* 335 */       this.mapIdToCreationListener2.remove(id);
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean createIfAutoOpen(String id) {
/* 340 */     Object o = this.mapAutoOpen.get(id);
/* 341 */     if ((o instanceof Map)) {
/* 342 */       Map<?, ?> autoOpenMap = (Map)o;
/*     */       
/* 344 */       return createEntryByCreationListener(id, autoOpenMap.get("datasource"), autoOpenMap) != null;
/*     */     }
/*     */     
/*     */ 
/* 348 */     boolean created = false;
/* 349 */     String[] autoOpenIDs = (String[])this.mapAutoOpen.keySet().toArray(new String[0]);
/* 350 */     for (String autoOpenID : autoOpenIDs) {
/* 351 */       if (Pattern.matches(id, autoOpenID)) {
/* 352 */         Map<?, ?> autoOpenMap = (Map)this.mapAutoOpen.get(autoOpenID);
/* 353 */         created |= createEntryByCreationListener(autoOpenID, autoOpenMap.get("datasource"), autoOpenMap) != null;
/*     */       }
/*     */     }
/*     */     
/* 357 */     return created;
/*     */   }
/*     */   
/*     */ 
/*     */   protected MdiEntry createEntryByCreationListener(String id, Object ds, Map<?, ?> autoOpenMap)
/*     */   {
/* 363 */     MdiEntryCreationListener mdiEntryCreationListener = null;
/* 364 */     for (String key : this.mapIdToCreationListener.keySet()) {
/* 365 */       if (Pattern.matches(key, id)) {
/* 366 */         mdiEntryCreationListener = (MdiEntryCreationListener)this.mapIdToCreationListener.get(key);
/* 367 */         break;
/*     */       }
/*     */     }
/* 370 */     if (mdiEntryCreationListener != null) {
/*     */       try {
/* 372 */         MdiEntry mdiEntry = mdiEntryCreationListener.createMDiEntry(id);
/*     */         
/* 374 */         if ((mdiEntry != null) && (ds != null)) {
/* 375 */           mdiEntry.setDatasource(ds);
/*     */         }
/* 377 */         return mdiEntry;
/*     */       } catch (Exception e) {
/* 379 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/* 383 */     MdiEntryCreationListener2 mdiEntryCreationListener2 = null;
/* 384 */     for (String key : this.mapIdToCreationListener2.keySet()) {
/* 385 */       if (Pattern.matches(key, id)) {
/* 386 */         mdiEntryCreationListener2 = (MdiEntryCreationListener2)this.mapIdToCreationListener2.get(key);
/* 387 */         break;
/*     */       }
/*     */     }
/* 390 */     if (mdiEntryCreationListener2 != null) {
/*     */       try {
/* 392 */         MdiEntry mdiEntry = mdiEntryCreationListener2.createMDiEntry(this, id, ds, autoOpenMap);
/* 393 */         if (mdiEntry == null) {
/* 394 */           removeEntryAutoOpen(id);
/*     */         }
/* 396 */         return mdiEntry;
/*     */       } catch (Exception e) {
/* 398 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/* 402 */     setEntryAutoOpen(id, ds);
/*     */     
/* 404 */     return null;
/*     */   }
/*     */   
/*     */   public void registerEntry(String id, MdiEntryCreationListener l)
/*     */   {
/* 409 */     if ((this.mapIdToCreationListener.containsKey(id)) || (this.mapIdToCreationListener2.containsKey(id)))
/*     */     {
/* 411 */       System.err.println("Warning: MDIEntry " + id + " Creation Listener being registered twice. " + Debug.getCompressedStackTrace());
/*     */     }
/*     */     
/*     */ 
/* 415 */     this.mapIdToCreationListener.put(id, l);
/*     */     
/* 417 */     createIfAutoOpen(id);
/*     */   }
/*     */   
/*     */   public void deregisterEntry(String id, MdiEntryCreationListener l)
/*     */   {
/* 422 */     MdiEntryCreationListener l2 = (MdiEntryCreationListener)this.mapIdToCreationListener.get(id);
/* 423 */     if (l == l2) {
/* 424 */       this.mapIdToCreationListener.remove(id);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean showEntryByID(String id) {
/* 429 */     return loadEntryByID(id, true);
/*     */   }
/*     */   
/*     */   public boolean showEntryByID(String id, Object datasource) {
/* 433 */     return loadEntryByID(id, true, false, datasource);
/*     */   }
/*     */   
/*     */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*     */   {
/* 438 */     UIManager ui_manager = PluginInitializer.getDefaultInterface().getUIManager();
/* 439 */     ui_manager.addUIListener(new UIManagerListener()
/*     */     {
/*     */       public void UIDetached(UIInstance instance) {}
/*     */       
/*     */       public void UIAttached(UIInstance instance) {
/* 444 */         if ((instance instanceof UISWTInstance)) {
/* 445 */           final AESemaphore wait_sem = new AESemaphore("SideBar:wait");
/*     */           
/* 447 */           Utils.execSWTThread(new AERunnable() {
/*     */             public void runSupport() {
/*     */               try {
/*     */                 try {
/* 451 */                   BaseMDI.this.loadCloseables();
/*     */                 } catch (Throwable t) {
/* 453 */                   Debug.out(t);
/*     */                 }
/*     */                 
/* 456 */                 BaseMDI.this.setupPluginViews();
/*     */               }
/*     */               finally
/*     */               {
/* 460 */                 wait_sem.release();
/*     */               }
/*     */             }
/*     */           });
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 468 */           if (!wait_sem.reserve(10000L))
/*     */           {
/* 470 */             Debug.out("eh?");
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 475 */     });
/* 476 */     return null;
/*     */   }
/*     */   
/*     */   public Object skinObjectDestroyed(SWTSkinObject skinObject, Object params)
/*     */   {
/* 481 */     MdiEntry entry = getCurrentEntry();
/* 482 */     if (entry != null) {
/* 483 */       COConfigurationManager.setParameter("v3.StartTab", entry.getId());
/*     */       
/* 485 */       String ds = entry.getExportableDatasource();
/* 486 */       COConfigurationManager.setParameter("v3.StartTab.ds", ds == null ? null : ds.toString());
/*     */     }
/*     */     
/* 489 */     return super.skinObjectDestroyed(skinObject, params);
/*     */   }
/*     */   
/*     */   public void updateUI() {
/* 493 */     MdiEntry currentEntry = getCurrentEntry();
/* 494 */     if (currentEntry != null) {
/* 495 */       currentEntry.updateUI();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean loadEntryByID(String id, boolean activate)
/*     */   {
/* 501 */     return loadEntryByID(id, activate, false, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean loadEntryByID(String id, boolean activate, boolean onlyLoadOnce, Object datasource)
/*     */   {
/* 509 */     if (id == null) {
/* 510 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 514 */     boolean loadedOnce = wasEntryLoadedOnce(id);
/* 515 */     if ((loadedOnce) && (onlyLoadOnce)) {
/* 516 */       return false;
/*     */     }
/*     */     
/* 519 */     MdiEntry entry = getEntry(id);
/* 520 */     if (entry != null) {
/* 521 */       if (datasource != null) {
/* 522 */         entry.setDatasource(datasource);
/*     */       }
/* 524 */       if (activate) {
/* 525 */         showEntry(entry);
/*     */       }
/* 527 */       return true;
/*     */     }
/*     */     
/* 530 */     MdiEntry mdiEntry = createEntryByCreationListener(id, datasource, null);
/* 531 */     if (mdiEntry != null) {
/* 532 */       if (onlyLoadOnce) {
/* 533 */         setEntryLoadedOnce(id);
/*     */       }
/* 535 */       if (activate) {
/* 536 */         showEntry(mdiEntry);
/*     */       }
/* 538 */       return true;
/*     */     }
/*     */     
/* 541 */     return false;
/*     */   }
/*     */   
/*     */   protected abstract void setEntryLoadedOnce(String paramString);
/*     */   
/*     */   protected abstract boolean wasEntryLoadedOnce(String paramString);
/*     */   
/*     */   public boolean entryExists(String id)
/*     */   {
/* 550 */     if ("browse".equalsIgnoreCase(id)) {
/* 551 */       id = ContentNetworkUtils.getTarget(ConstantsVuze.getDefaultContentNetwork());
/*     */     }
/* 553 */     synchronized (this.mapIdToEntry) {
/* 554 */       MdiEntry entry = (MdiEntry)this.mapIdToEntry.get(id);
/* 555 */       if (entry == null) {
/* 556 */         return false;
/*     */       }
/* 558 */       return entry.isAdded();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setEntryAutoOpen(String id, Object datasource)
/*     */   {
/* 564 */     Map<String, Object> map = (Map)this.mapAutoOpen.get(id);
/* 565 */     if (map == null) {
/* 566 */       map = new LightHashMap(1);
/*     */     }
/* 568 */     map.put("datasource", datasource);
/* 569 */     this.mapAutoOpen.put(id, map);
/*     */   }
/*     */   
/*     */   public void removeEntryAutoOpen(String id)
/*     */   {
/* 574 */     this.mapAutoOpen.remove(id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setupPluginViews()
/*     */   {
/* 581 */     PluginsMenuHelper.getInstance().addPluginAddedViewListener(new PluginsMenuHelper.PluginAddedViewListener()
/*     */     {
/*     */ 
/*     */       public void pluginViewAdded(PluginsMenuHelper.IViewInfo viewInfo)
/*     */       {
/* 586 */         Object o = BaseMDI.this.mapAutoOpen.get(viewInfo.viewID);
/* 587 */         if ((o instanceof Map)) {
/* 588 */           BaseMDI.this.processAutoOpenMap(viewInfo.viewID, (Map)o, viewInfo);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void informAutoOpenSet(MdiEntry entry, Map<String, Object> autoOpenInfo) {
/* 595 */     this.mapAutoOpen.put(entry.getId(), autoOpenInfo);
/*     */   }
/*     */   
/*     */   public void loadCloseables() {
/* 599 */     if (this.closeableConfigFile == null) {
/* 600 */       return;
/*     */     }
/*     */     try {
/* 603 */       Map<?, ?> loadedMap = FileUtil.readResilientConfigFile(this.closeableConfigFile, true);
/* 604 */       if (loadedMap.isEmpty()) {
/*     */         return;
/*     */       }
/* 607 */       BDecoder.decodeStrings(loadedMap);
/*     */       
/* 609 */       List<Map> orderedEntries = (List)loadedMap.get("_entries_");
/*     */       Iterator<?> iter;
/* 611 */       if (orderedEntries == null)
/*     */       {
/* 613 */         for (iter = loadedMap.keySet().iterator(); iter.hasNext();) {
/* 614 */           String id = (String)iter.next();
/* 615 */           Object o = loadedMap.get(id);
/*     */           
/* 617 */           if (((o instanceof Map)) && 
/* 618 */             (!processAutoOpenMap(id, (Map)o, null))) {
/* 619 */             this.mapAutoOpen.put(id, o);
/*     */           }
/*     */           
/*     */         }
/*     */       } else {
/* 624 */         for (Map map : orderedEntries) {
/* 625 */           String id = (String)map.get("id");
/*     */           
/*     */ 
/* 628 */           Object o = map.get("value");
/* 629 */           if (((o instanceof Map)) && 
/* 630 */             (!processAutoOpenMap(id, (Map)o, null))) {
/* 631 */             this.mapAutoOpen.put(id, o);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 638 */       Debug.out(e);
/*     */     }
/*     */     finally
/*     */     {
/* 642 */       this.mapAutoOpenLoaded = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void saveCloseables()
/*     */   {
/* 651 */     if (!this.mapAutoOpenLoaded) {
/* 652 */       return;
/*     */     }
/* 654 */     if (this.closeableConfigFile == null) {
/* 655 */       return;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 660 */       for (Iterator<String> iter = new ArrayList(this.mapAutoOpen.keySet()).iterator(); iter.hasNext();) {
/* 661 */         String id = (String)iter.next();
/*     */         
/* 663 */         MdiEntry entry = getEntry(id);
/*     */         
/* 665 */         if ((entry != null) && (entry.isAdded()))
/*     */         {
/* 667 */           this.mapAutoOpen.put(id, entry.getAutoOpenInfo());
/*     */         }
/*     */         else
/*     */         {
/* 671 */           this.mapAutoOpen.remove(id);
/*     */         }
/*     */       }
/*     */       
/* 675 */       Map map = new HashMap();
/*     */       
/* 677 */       List<Map> list = new ArrayList(this.mapAutoOpen.size());
/*     */       
/* 679 */       map.put("_entries_", list);
/*     */       
/* 681 */       for (Map.Entry<String, Object> entry : this.mapAutoOpen.entrySet())
/*     */       {
/* 683 */         Map m = new HashMap();
/*     */         
/* 685 */         list.add(m);
/*     */         
/* 687 */         String id = (String)entry.getKey();
/*     */         
/* 689 */         m.put("id", id);
/* 690 */         m.put("value", entry.getValue());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 695 */       FileUtil.writeResilientConfigFile(this.closeableConfigFile, map);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 699 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean processAutoOpenMap(String id, Map<?, ?> autoOpenInfo, PluginsMenuHelper.IViewInfo viewInfo)
/*     */   {
/*     */     try {
/* 706 */       MdiEntry entry = getEntry(id);
/* 707 */       if (entry != null) {
/* 708 */         return true;
/*     */       }
/*     */       
/* 711 */       Object datasource = autoOpenInfo.get("datasource");
/* 712 */       String title = MapUtils.getMapString(autoOpenInfo, "title", id);
/*     */       
/* 714 */       MdiEntry mdiEntry = createEntryByCreationListener(id, datasource, autoOpenInfo);
/* 715 */       if (mdiEntry != null) {
/* 716 */         if (mdiEntry.getTitle().equals("")) {
/* 717 */           mdiEntry.setTitle(title);
/*     */         }
/* 719 */         return true;
/*     */       }
/*     */       
/* 722 */       String parentID = MapUtils.getMapString(autoOpenInfo, "parentID", "header.plugins");
/*     */       
/* 724 */       if ((viewInfo != null) && 
/* 725 */         (viewInfo.event_listener != null)) {
/* 726 */         entry = createEntryFromEventListener(parentID, viewInfo.event_listener, id, true, datasource, null);
/*     */         
/* 728 */         if (entry != null) {
/* 729 */           entry.setTitle(title);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 734 */       if ((entry != null) && (datasource == null)) {
/* 735 */         final MdiEntry fEntry = entry;
/* 736 */         final String dmHash = MapUtils.getMapString(autoOpenInfo, "dm", null);
/* 737 */         if (dmHash != null) {
/* 738 */           AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */             public void azureusCoreRunning(AzureusCore core) {
/* 740 */               GlobalManager gm = core.getGlobalManager();
/* 741 */               HashWrapper hw = new HashWrapper(Base32.decode(dmHash));
/* 742 */               DownloadManager dm = gm.getDownloadManager(hw);
/* 743 */               if (dm != null) {
/* 744 */                 fEntry.setDatasource(dm);
/*     */               }
/*     */             }
/*     */           });
/*     */         } else {
/* 749 */           final List<?> listHashes = MapUtils.getMapList(autoOpenInfo, "dms", null);
/*     */           
/* 751 */           if (listHashes != null) {
/* 752 */             AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */               public void azureusCoreRunning(AzureusCore core) {
/* 754 */                 List<DownloadManager> listDMS = new ArrayList(1);
/*     */                 
/* 756 */                 GlobalManager gm = core.getGlobalManager();
/* 757 */                 for (Object oDM : listHashes) {
/* 758 */                   if ((oDM instanceof String)) {
/* 759 */                     String hash = (String)oDM;
/* 760 */                     DownloadManager dm = gm.getDownloadManager(new HashWrapper(Base32.decode(hash)));
/*     */                     
/* 762 */                     if (dm != null) {
/* 763 */                       listDMS.add(dm);
/*     */                     }
/*     */                   }
/* 766 */                   fEntry.setDatasource(listDMS.toArray(new DownloadManager[0]));
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 774 */       return entry != null;
/*     */     } catch (Throwable e) {
/* 776 */       Debug.out(e);
/*     */     }
/* 778 */     return false;
/*     */   }
/*     */   
/*     */   public void addItem(MdiEntry entry) {
/* 782 */     String id = entry.getId();
/* 783 */     synchronized (this.mapIdToEntry) {
/* 784 */       this.mapIdToEntry.put(id, entry);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void itemSelected(MdiEntry entry) {}
/*     */   
/*     */   public void removeItem(MdiEntry entry)
/*     */   {
/* 793 */     String id = entry.getId();
/* 794 */     synchronized (this.mapIdToEntry) {
/* 795 */       this.mapIdToEntry.remove(id);
/*     */       
/* 797 */       removeChildrenOf(id);
/*     */     }
/*     */   }
/*     */   
/*     */   private void removeChildrenOf(String id) {
/* 802 */     if (id == null) {
/* 803 */       return;
/*     */     }
/* 805 */     synchronized (this.mapIdToEntry) {
/* 806 */       MdiEntrySWT[] entriesSWT = getEntriesSWT();
/* 807 */       for (MdiEntrySWT entry : entriesSWT) {
/* 808 */         if (id.equals(entry.getParentID())) {
/* 809 */           String kid_id = entry.getId();
/* 810 */           this.mapIdToEntry.remove(kid_id);
/* 811 */           removeChildrenOf(kid_id);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public List<MdiEntry> getChildrenOf(String id) {
/* 818 */     if (id == null) {
/* 819 */       return Collections.emptyList();
/*     */     }
/* 821 */     List<MdiEntry> list = new ArrayList(1);
/* 822 */     synchronized (this.mapIdToEntry) {
/* 823 */       MdiEntrySWT[] entriesSWT = getEntriesSWT();
/* 824 */       for (MdiEntrySWT entry : entriesSWT) {
/* 825 */         if (id.equals(entry.getParentID())) {
/* 826 */           list.add(entry);
/*     */         }
/*     */       }
/*     */     }
/* 830 */     return list;
/*     */   }
/*     */   
/*     */   public Object updateLanguage(SWTSkinObject skinObject, Object params) {
/* 834 */     MdiEntry[] entries = getEntries();
/*     */     
/* 836 */     for (MdiEntry entry : entries) {
/* 837 */       if ((entry instanceof BaseMdiEntry)) {
/* 838 */         BaseMdiEntry baseEntry = (BaseMdiEntry)entry;
/* 839 */         baseEntry.updateLanguage();
/*     */       }
/*     */     }
/*     */     
/* 843 */     return null;
/*     */   }
/*     */   
/*     */   public void setPreferredOrder(String[] preferredOrder) {
/* 847 */     this.preferredOrder = preferredOrder;
/*     */   }
/*     */   
/*     */   public String[] getPreferredOrder() {
/* 851 */     return this.preferredOrder == null ? new String[0] : this.preferredOrder;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public int getEntriesCount()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 680	com/aelitis/azureus/ui/swt/mdi/BaseMDI:mapIdToEntry	Ljava/util/Map;
/*     */     //   4: dup
/*     */     //   5: astore_1
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: getfield 680	com/aelitis/azureus/ui/swt/mdi/BaseMDI:mapIdToEntry	Ljava/util/Map;
/*     */     //   11: invokeinterface 798 1 0
/*     */     //   16: aload_1
/*     */     //   17: monitorexit
/*     */     //   18: ireturn
/*     */     //   19: astore_2
/*     */     //   20: aload_1
/*     */     //   21: monitorexit
/*     */     //   22: aload_2
/*     */     //   23: athrow
/*     */     // Line number table:
/*     */     //   Java source line #855	-> byte code offset #0
/*     */     //   Java source line #856	-> byte code offset #7
/*     */     //   Java source line #857	-> byte code offset #19
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	24	0	this	BaseMDI
/*     */     //   5	16	1	Ljava/lang/Object;	Object
/*     */     //   19	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	18	19	finally
/*     */     //   19	22	19	finally
/*     */   }
/*     */   
/*     */   public void setCloseableConfigFile(String closeableConfigFile)
/*     */   {
/* 861 */     this.closeableConfigFile = closeableConfigFile;
/*     */   }
/*     */   
/*     */   public void addListener(MdiSWTMenuHackListener l) {
/* 865 */     synchronized (this) {
/* 866 */       if (this.listMenuHackListners == null) {
/* 867 */         this.listMenuHackListners = new ArrayList(1);
/*     */       }
/* 869 */       if (!this.listMenuHackListners.contains(l)) {
/* 870 */         this.listMenuHackListners.add(l);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeListener(MdiSWTMenuHackListener l) {
/* 876 */     synchronized (this) {
/* 877 */       if (this.listMenuHackListners == null) {
/* 878 */         this.listMenuHackListners = new ArrayList(1);
/*     */       }
/* 880 */       this.listMenuHackListners.remove(l);
/*     */     }
/*     */   }
/*     */   
/*     */   public MdiSWTMenuHackListener[] getMenuHackListeners() {
/* 885 */     synchronized (this) {
/* 886 */       if (this.listMenuHackListners == null) {
/* 887 */         return new MdiSWTMenuHackListener[0];
/*     */       }
/* 889 */       return (MdiSWTMenuHackListener[])this.listMenuHackListners.toArray(new MdiSWTMenuHackListener[0]);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void fillMenu(Menu menu, MdiEntry entry, String menuID)
/*     */   {
/* 897 */     MenuItem[] menu_items = MenuItemManager.getInstance().getAllAsArray(menuID);
/*     */     
/* 899 */     MenuBuildUtils.addPluginMenuItems(menu_items, menu, false, true, new MenuBuildUtils.MenuItemPluginMenuControllerImpl(new Object[] { entry }));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 904 */     if (entry != null)
/*     */     {
/* 906 */       menu_items = MenuItemManager.getInstance().getAllAsArray("sidebar." + entry.getId());
/*     */       
/*     */ 
/* 909 */       if (menu_items.length == 0)
/*     */       {
/* 911 */         if ((entry instanceof UISWTView))
/*     */         {
/* 913 */           PluginInterface pi = ((UISWTView)entry).getPluginInterface();
/*     */           
/* 915 */           if (pi != null)
/*     */           {
/* 917 */             final List<String> relevant_sections = new ArrayList();
/*     */             
/* 919 */             List<ConfigSectionHolder> sections = ConfigSectionRepository.getInstance().getHolderList();
/*     */             
/* 921 */             for (ConfigSectionHolder cs : sections)
/*     */             {
/* 923 */               if (pi == cs.getPluginInterface())
/*     */               {
/* 925 */                 relevant_sections.add(cs.configSectionGetName());
/*     */               }
/*     */             }
/*     */             
/* 929 */             if (relevant_sections.size() > 0)
/*     */             {
/* 931 */               MenuItem mi = pi.getUIManager().getMenuManager().addMenuItem("sidebar." + entry.getId(), "MainWindow.menu.view.configuration");
/*     */               
/*     */ 
/*     */ 
/* 935 */               mi.addListener(new MenuItemListener() {
/*     */                 public void selected(MenuItem menu, Object target) {
/* 937 */                   UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */                   
/* 939 */                   if (uif != null)
/*     */                   {
/* 941 */                     for (String s : relevant_sections)
/*     */                     {
/* 943 */                       uif.getMDI().showEntryByID("ConfigView", s);
/*     */                     }
/*     */                     
/*     */                   }
/*     */                   
/*     */                 }
/* 949 */               });
/* 950 */               menu_items = MenuItemManager.getInstance().getAllAsArray("sidebar." + entry.getId());
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 957 */       MenuBuildUtils.addPluginMenuItems(menu_items, menu, false, true, new MenuBuildUtils.MenuItemPluginMenuControllerImpl(new Object[] { entry }));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 962 */       MdiSWTMenuHackListener[] menuHackListeners = getMenuHackListeners();
/* 963 */       for (MdiSWTMenuHackListener l : menuHackListeners) {
/*     */         try {
/* 965 */           l.menuWillBeShown(entry, menu);
/*     */         } catch (Exception e) {
/* 967 */           Debug.out(e);
/*     */         }
/*     */       }
/* 970 */       if ((this.currentEntry instanceof SideBarEntrySWT)) {
/* 971 */         menuHackListeners = ((SideBarEntrySWT)entry).getMenuHackListeners();
/* 972 */         for (MdiSWTMenuHackListener l : menuHackListeners) {
/*     */           try {
/* 974 */             l.menuWillBeShown(entry, menu);
/*     */           } catch (Exception e) {
/* 976 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 982 */     menu_items = MenuItemManager.getInstance().getAllAsArray(menuID + "._end_");
/*     */     
/* 984 */     if (menu_items.length > 0)
/*     */     {
/* 986 */       MenuBuildUtils.addPluginMenuItems(menu_items, menu, false, true, new MenuBuildUtils.MenuItemPluginMenuControllerImpl(new Object[] { entry }));
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/mdi/BaseMDI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */