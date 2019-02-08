/*      */ package com.aelitis.azureus.ui.swt.mdi;
/*      */ 
/*      */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*      */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo2;
/*      */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfoListener;
/*      */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfoManager;
/*      */ import com.aelitis.azureus.ui.mdi.MdiChildCloseListener;
/*      */ import com.aelitis.azureus.ui.mdi.MdiCloseListener;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntryDatasourceListener;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntryDropListener;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntryLogIdListener;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntryOpenListener;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImage;
/*      */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.impl.ConfigurationDefaults;
/*      */ import org.gudy.azureus2.core3.config.impl.ConfigurationParameterNotFoundException;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.LightHashMap;
/*      */ import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarEnablerBase;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.debug.ObfusticateImage;
/*      */ import org.gudy.azureus2.ui.swt.plugins.PluginUISWTSkinObject;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.BasicPluginViewImpl;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewEventCancelledException;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewEventListenerHolder;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewImpl;
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class BaseMdiEntry
/*      */   extends UISWTViewImpl
/*      */   implements MdiEntrySWT, ViewTitleInfoListener, AEDiagnosticsEvidenceGenerator, ObfusticateImage
/*      */ {
/*      */   protected final MultipleDocumentInterface mdi;
/*      */   protected String logID;
/*      */   private String skinRef;
/*   61 */   private List<MdiCloseListener> listCloseListeners = null;
/*      */   
/*   63 */   private List<MdiChildCloseListener> listChildCloseListeners = null;
/*      */   
/*   65 */   private List<MdiEntryLogIdListener> listLogIDListeners = null;
/*      */   
/*   67 */   private List<MdiEntryOpenListener> listOpenListeners = null;
/*      */   
/*   69 */   private List<MdiEntryDropListener> listDropListeners = null;
/*      */   
/*   71 */   private List<MdiEntryDatasourceListener> listDatasourceListeners = null;
/*      */   
/*      */ 
/*      */   private List<MdiSWTMenuHackListener> listMenuHackListners;
/*      */   
/*      */   protected ViewTitleInfo viewTitleInfo;
/*      */   
/*      */   private String parentEntryID;
/*      */   
/*      */   private boolean closeable;
/*      */   
/*   82 */   private Boolean isExpanded = null;
/*      */   
/*   84 */   private boolean disposed = false;
/*      */   
/*   86 */   private boolean added = false;
/*      */   
/*      */   private String imageLeftID;
/*      */   
/*      */   private Image imageLeft;
/*      */   
/*   92 */   private boolean collapseDisabled = false;
/*      */   
/*      */   private SWTSkinObject soMaster;
/*      */   
/*      */   private String preferredAfterID;
/*      */   private boolean hasBeenOpened;
/*      */   
/*      */   private BaseMdiEntry()
/*      */   {
/*  101 */     super(null, null, false);
/*  102 */     this.mdi = null;
/*  103 */     setDefaultExpanded(false);
/*  104 */     AEDiagnostics.addEvidenceGenerator(this);
/*      */   }
/*      */   
/*      */   public BaseMdiEntry(MultipleDocumentInterface mdi, String id, String parentViewID) {
/*  108 */     super(id, parentViewID, true);
/*  109 */     this.mdi = mdi;
/*  110 */     AEDiagnostics.addEvidenceGenerator(this);
/*      */     
/*  112 */     if (id == null) {
/*  113 */       this.logID = "null";
/*      */     } else {
/*  115 */       int i = id.indexOf('_');
/*  116 */       if (i > 0) {
/*  117 */         this.logID = id.substring(0, i);
/*      */       } else {
/*  119 */         this.logID = id;
/*      */       }
/*      */     }
/*  122 */     setDefaultExpanded(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getId()
/*      */   {
/*  129 */     return this.id;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public MdiEntryVitalityImage addVitalityImage(String imageID)
/*      */   {
/*  136 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean close(boolean forceClose)
/*      */   {
/*  143 */     if ((!forceClose) && 
/*  144 */       (!requestClose())) {
/*  145 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  149 */     setCloseable(this.closeable);
/*  150 */     this.disposed = true;
/*  151 */     ViewTitleInfoManager.removeListener(this);
/*      */     
/*  153 */     return true;
/*      */   }
/*      */   
/*      */   public Object getDatasourceCore() {
/*  157 */     return this.datasource;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getExportableDatasource()
/*      */   {
/*  164 */     if (this.viewTitleInfo != null) {
/*  165 */       Object ds = this.viewTitleInfo.getTitleInfoProperty(10);
/*  166 */       if (ds != null) {
/*  167 */         return ds.toString();
/*      */       }
/*      */     }
/*  170 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object getDatasource()
/*      */   {
/*  177 */     return PluginCoreUtils.convert(this.datasource, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getLogID()
/*      */   {
/*  184 */     return this.logID;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public MultipleDocumentInterface getMDI()
/*      */   {
/*  191 */     return this.mdi;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getParentID()
/*      */   {
/*  198 */     return this.parentEntryID;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setParentID(String id)
/*      */   {
/*  205 */     if ((id == null) || ("Tools".equals(id))) {
/*  206 */       if ((getId().equals("header.dvd")) && (id == null))
/*      */       {
/*  208 */         id = "";
/*      */       } else {
/*  210 */         id = "header.plugins";
/*      */       }
/*      */     }
/*  213 */     if (id.equals(getId())) {
/*  214 */       Debug.out("Setting Parent to same ID as child! " + id);
/*  215 */       return;
/*      */     }
/*  217 */     this.parentEntryID = id;
/*      */     
/*  219 */     if (this.mdi != null) {
/*  220 */       this.mdi.loadEntryByID(this.parentEntryID, false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public MdiEntryVitalityImage[] getVitalityImages()
/*      */   {
/*  228 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isCloseable()
/*      */   {
/*  235 */     return this.closeable;
/*      */   }
/*      */   
/*      */   public boolean isCollapseDisabled() {
/*  239 */     return this.collapseDisabled;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setCollapseDisabled(boolean collapseDisabled)
/*      */   {
/*  246 */     this.collapseDisabled = collapseDisabled;
/*  247 */     setExpanded(true);
/*      */   }
/*      */   
/*      */   public void addListeners(Object objectWithListeners)
/*      */   {
/*  252 */     if ((objectWithListeners instanceof MdiChildCloseListener)) {
/*  253 */       addListener((MdiChildCloseListener)objectWithListeners);
/*      */     }
/*  255 */     if ((objectWithListeners instanceof MdiCloseListener)) {
/*  256 */       addListener((MdiCloseListener)objectWithListeners);
/*      */     }
/*  258 */     if ((objectWithListeners instanceof MdiEntryDatasourceListener)) {
/*  259 */       addListener((MdiEntryDatasourceListener)objectWithListeners);
/*      */     }
/*  261 */     if ((objectWithListeners instanceof MdiEntryDropListener)) {
/*  262 */       addListener((MdiEntryDropListener)objectWithListeners);
/*      */     }
/*  264 */     if ((objectWithListeners instanceof MdiEntryLogIdListener)) {
/*  265 */       addListener((MdiEntryLogIdListener)objectWithListeners);
/*      */     }
/*  267 */     if ((objectWithListeners instanceof MdiEntryOpenListener)) {
/*  268 */       addListener((MdiEntryOpenListener)objectWithListeners);
/*      */     }
/*      */     
/*  271 */     if ((objectWithListeners instanceof MdiSWTMenuHackListener)) {
/*  272 */       addListener((MdiSWTMenuHackListener)objectWithListeners);
/*      */     }
/*      */   }
/*      */   
/*      */   public void addListener(MdiCloseListener l) {
/*  277 */     synchronized (this) {
/*  278 */       if (this.listCloseListeners == null) {
/*  279 */         this.listCloseListeners = new ArrayList(1);
/*      */       }
/*  281 */       this.listCloseListeners.add(l);
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeListener(MdiCloseListener l) {
/*  286 */     synchronized (this) {
/*  287 */       if (this.listCloseListeners != null) {
/*  288 */         this.listCloseListeners.remove(l);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void triggerCloseListeners(boolean user) {
/*  294 */     Object[] list = new Object[0];
/*  295 */     synchronized (this) {
/*  296 */       if (this.listCloseListeners != null) {
/*  297 */         list = this.listCloseListeners.toArray();
/*      */       }
/*      */     }
/*  300 */     for (int i = 0; i < list.length; i++) {
/*  301 */       MdiCloseListener l = (MdiCloseListener)list[i];
/*      */       try {
/*  303 */         l.mdiEntryClosed(this, user);
/*      */       } catch (Exception e) {
/*  305 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/*  309 */     if ((this.parentEntryID != null) && (this.mdi != null)) {
/*  310 */       MdiEntry parentEntry = this.mdi.getEntry(this.parentEntryID);
/*  311 */       if ((parentEntry instanceof BaseMdiEntry)) {
/*  312 */         ((BaseMdiEntry)parentEntry).triggerChildCloseListeners(this, user);
/*      */       }
/*      */     }
/*      */     
/*  316 */     triggerEvent(7, null);
/*      */   }
/*      */   
/*      */   public void addListener(MdiChildCloseListener l) {
/*  320 */     synchronized (this) {
/*  321 */       if (this.listChildCloseListeners == null) {
/*  322 */         this.listChildCloseListeners = new ArrayList(1);
/*      */       }
/*  324 */       this.listChildCloseListeners.add(l);
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeListener(MdiChildCloseListener l) {
/*  329 */     synchronized (this) {
/*  330 */       if (this.listChildCloseListeners != null) {
/*  331 */         this.listChildCloseListeners.remove(l);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void triggerChildCloseListeners(MdiEntry child, boolean user) {
/*      */     Object[] list;
/*  338 */     synchronized (this) {
/*  339 */       if (this.listChildCloseListeners == null) {
/*  340 */         return;
/*      */       }
/*  342 */       list = this.listChildCloseListeners.toArray();
/*      */     }
/*  344 */     for (int i = 0; i < list.length; i++) {
/*  345 */       MdiChildCloseListener l = (MdiChildCloseListener)list[i];
/*      */       try {
/*  347 */         l.mdiChildEntryClosed(this, child, user);
/*      */       } catch (Exception e) {
/*  349 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void addListener(MdiEntryLogIdListener l) {
/*  355 */     synchronized (this) {
/*  356 */       if (this.listLogIDListeners == null) {
/*  357 */         this.listLogIDListeners = new ArrayList(1);
/*      */       }
/*  359 */       this.listLogIDListeners.add(l);
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeListener(MdiEntryLogIdListener sideBarLogIdListener) {
/*  364 */     synchronized (this) {
/*  365 */       if (this.listLogIDListeners != null) {
/*  366 */         this.listLogIDListeners.remove(sideBarLogIdListener);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void triggerLogIDListeners(String oldID) {
/*      */     Object[] list;
/*  373 */     synchronized (this) {
/*  374 */       if (this.listLogIDListeners == null) {
/*  375 */         return;
/*      */       }
/*      */       
/*  378 */       list = this.listLogIDListeners.toArray();
/*      */     }
/*      */     
/*  381 */     for (int i = 0; i < list.length; i++) {
/*  382 */       MdiEntryLogIdListener l = (MdiEntryLogIdListener)list[i];
/*  383 */       l.mdiEntryLogIdChanged(this, oldID, this.logID);
/*      */     }
/*      */   }
/*      */   
/*      */   public void addListener(MdiEntryOpenListener l) {
/*  388 */     synchronized (this) {
/*  389 */       if (this.listOpenListeners == null) {
/*  390 */         this.listOpenListeners = new ArrayList(1);
/*      */       }
/*  392 */       this.listOpenListeners.add(l);
/*      */     }
/*      */     
/*  395 */     if (this.hasBeenOpened) {
/*  396 */       l.mdiEntryOpen(this);
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeListener(MdiEntryOpenListener l) {
/*  401 */     synchronized (this) {
/*  402 */       if (this.listOpenListeners != null) {
/*  403 */         this.listOpenListeners.remove(l);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void triggerOpenListeners()
/*      */   {
/*  410 */     this.hasBeenOpened = true;
/*  411 */     Object[] list; synchronized (this) {
/*  412 */       if (this.listOpenListeners == null) {
/*  413 */         return;
/*      */       }
/*      */       
/*  416 */       list = this.listOpenListeners.toArray();
/*      */     }
/*  418 */     for (int i = 0; i < list.length; i++) {
/*  419 */       MdiEntryOpenListener l = (MdiEntryOpenListener)list[i];
/*      */       try {
/*  421 */         l.mdiEntryOpen(this);
/*      */       } catch (Exception e) {
/*  423 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void addListener(MdiEntryDatasourceListener l)
/*      */   {
/*  430 */     synchronized (this) {
/*  431 */       if (this.listDatasourceListeners == null) {
/*  432 */         this.listDatasourceListeners = new ArrayList(1);
/*      */       }
/*  434 */       this.listDatasourceListeners.add(l);
/*      */     }
/*      */     
/*  437 */     l.mdiEntryDatasourceChanged(this);
/*      */   }
/*      */   
/*      */   public void removeListener(MdiEntryDatasourceListener l) {
/*  441 */     synchronized (this) {
/*  442 */       if (this.listDatasourceListeners != null) {
/*  443 */         this.listDatasourceListeners.remove(l);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void triggerDatasourceListeners() {
/*      */     Object[] list;
/*  450 */     synchronized (this) {
/*  451 */       if (this.listDatasourceListeners == null) {
/*  452 */         return;
/*      */       }
/*      */       
/*  455 */       list = this.listDatasourceListeners.toArray();
/*      */     }
/*  457 */     for (int i = 0; i < list.length; i++) {
/*  458 */       MdiEntryDatasourceListener l = (MdiEntryDatasourceListener)list[i];
/*      */       try {
/*  460 */         l.mdiEntryDatasourceChanged(this);
/*      */       } catch (Exception e) {
/*  462 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void addListener(MdiEntryDropListener l) {
/*  468 */     synchronized (this) {
/*  469 */       if (this.listDropListeners == null) {
/*  470 */         this.listDropListeners = new ArrayList(1);
/*      */       }
/*  472 */       this.listDropListeners.add(l);
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeListener(MdiEntryDropListener l) {
/*  477 */     synchronized (this) {
/*  478 */       if (this.listDropListeners != null) {
/*  479 */         this.listDropListeners.remove(l);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean hasDropListeners() {
/*  485 */     synchronized (this) {
/*  486 */       return (this.listDropListeners != null) && (this.listDropListeners.size() > 0);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean triggerDropListeners(Object o)
/*      */   {
/*  496 */     boolean handled = false;
/*      */     Object[] list;
/*  498 */     synchronized (this) {
/*  499 */       if (this.listDropListeners == null) {
/*  500 */         return handled;
/*      */       }
/*      */       
/*  503 */       list = this.listDropListeners.toArray();
/*      */     }
/*  505 */     for (int i = 0; i < list.length; i++) {
/*  506 */       MdiEntryDropListener l = (MdiEntryDropListener)list[i];
/*  507 */       handled = l.mdiEntryDrop(this, o);
/*  508 */       if (handled) {
/*      */         break;
/*      */       }
/*      */     }
/*  512 */     return handled;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setLogID(String logID)
/*      */   {
/*  519 */     if ((logID == null) || (logID.equals("" + this.logID))) {
/*  520 */       return;
/*      */     }
/*  522 */     String oldID = this.logID;
/*  523 */     this.logID = logID;
/*  524 */     triggerLogIDListeners(oldID);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ViewTitleInfo getViewTitleInfo()
/*      */   {
/*  531 */     return this.viewTitleInfo;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setViewTitleInfo(ViewTitleInfo viewTitleInfo)
/*      */   {
/*  538 */     if (this.viewTitleInfo == viewTitleInfo) {
/*  539 */       return;
/*      */     }
/*  541 */     this.viewTitleInfo = viewTitleInfo;
/*      */     
/*  543 */     if (viewTitleInfo != null) {
/*  544 */       if ((viewTitleInfo instanceof ViewTitleInfo2)) {
/*  545 */         ViewTitleInfo2 vti2 = (ViewTitleInfo2)viewTitleInfo;
/*      */         try {
/*  547 */           vti2.titleInfoLinked(this.mdi, this);
/*      */         } catch (Exception e) {
/*  549 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */       
/*  553 */       String imageID = (String)viewTitleInfo.getTitleInfoProperty(2);
/*  554 */       if (imageID != null) {
/*  555 */         setImageLeftID(imageID.length() == 0 ? null : imageID);
/*      */       }
/*      */       
/*  558 */       ViewTitleInfoManager.addListener(this);
/*      */       
/*  560 */       if ((getEventListener() == null) && ((viewTitleInfo instanceof UISWTViewEventListener))) {
/*      */         try {
/*  562 */           setEventListener((UISWTViewEventListener)viewTitleInfo, true);
/*      */         }
/*      */         catch (UISWTViewEventCancelledException e) {}
/*      */       }
/*      */     }
/*  567 */     redraw();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPluginSkinObject(PluginUISWTSkinObject skinObject)
/*      */   {
/*  576 */     super.setPluginSkinObject(skinObject);
/*  577 */     Object initialDataSource = (this.datasource == null) || (((this.datasource instanceof Object[])) && (((Object[])this.datasource).length == 0)) ? getInitialDataSource() : this.datasource;
/*      */     
/*      */ 
/*      */ 
/*  581 */     if (initialDataSource != null) {
/*  582 */       if ((skinObject instanceof SWTSkinObject)) {
/*  583 */         ((SWTSkinObject)skinObject).triggerListeners(7, initialDataSource);
/*      */       }
/*      */       
/*  586 */       triggerEvent(1, initialDataSource);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setSkinObjectMaster(SWTSkinObject soMaster) {
/*  591 */     this.soMaster = soMaster;
/*      */   }
/*      */   
/*      */   public SWTSkinObject getSkinObject()
/*      */   {
/*  596 */     return (SWTSkinObject)getPluginSkinObject();
/*      */   }
/*      */   
/*      */   public SWTSkinObject getSkinObjectMaster() {
/*  600 */     if (this.soMaster == null) {
/*  601 */       return getSkinObject();
/*      */     }
/*  603 */     return this.soMaster;
/*      */   }
/*      */   
/*      */   public void setSkinRef(String configID, Object params) {
/*  607 */     this.skinRef = configID;
/*  608 */     if (params != null) {
/*  609 */       setDatasource(params);
/*      */     }
/*      */   }
/*      */   
/*      */   public String getSkinRef() {
/*  614 */     return this.skinRef;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getTitle()
/*      */   {
/*  621 */     if (this.viewTitleInfo != null) {
/*  622 */       String viewTitle = (String)this.viewTitleInfo.getTitleInfoProperty(5);
/*  623 */       if ((viewTitle != null) && (viewTitle.length() > 0)) {
/*  624 */         return viewTitle;
/*      */       }
/*      */     }
/*  627 */     return super.getFullTitle();
/*      */   }
/*      */   
/*      */   public void updateLanguage() {
/*  631 */     triggerEvent(6, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void triggerEvent(int eventType, Object data)
/*      */   {
/*  640 */     super.triggerEvent(eventType, data);
/*      */     
/*  642 */     if (eventType == 6)
/*      */     {
/*      */ 
/*      */ 
/*  646 */       if (this.viewTitleInfo != null) {
/*  647 */         viewTitleInfoRefresh(this.viewTitleInfo);
/*      */       }
/*  649 */       updateUI();
/*      */       
/*      */ 
/*  652 */       SWTSkinObject skinObjectMaster = getSkinObjectMaster();
/*  653 */       if (skinObjectMaster != null) {
/*  654 */         skinObjectMaster.triggerListeners(6);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void show()
/*      */   {
/*  662 */     if (this.skinObject == null) {
/*  663 */       return;
/*      */     }
/*      */     
/*  666 */     SelectedContentManager.clearCurrentlySelectedContent();
/*      */     
/*  668 */     UIFunctionsSWT uif = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*  669 */     if (uif != null)
/*      */     {
/*  671 */       uif.refreshTorrentMenu();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  676 */     SWTSkinObject skinObject = getSkinObjectMaster();
/*  677 */     skinObject.setVisible(true);
/*  678 */     if ((skinObject instanceof SWTSkinObjectContainer)) {
/*  679 */       SWTSkinObjectContainer container = (SWTSkinObjectContainer)skinObject;
/*  680 */       Composite composite = container.getComposite();
/*  681 */       if ((composite != null) && (!composite.isDisposed())) {
/*  682 */         composite.setVisible(true);
/*  683 */         composite.moveAbove(null);
/*      */         
/*      */ 
/*  686 */         composite.getParent().layout();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  692 */     Composite c = getComposite();
/*  693 */     if ((c != null) && (!c.isDisposed())) {
/*  694 */       c.setData("BaseMDIEntry", this);
/*  695 */       c.setVisible(true);
/*  696 */       c.getParent().layout();
/*      */     }
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  702 */       triggerEvent(3, null);
/*      */     } catch (Exception e) {
/*  704 */       Debug.out(e);
/*      */     }
/*  706 */     setToolbarVisibility(hasToolbarEnableers());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void hide()
/*      */   {
/*  713 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  715 */         BaseMdiEntry.this.swt_hide();
/*      */       }
/*  717 */     });
/*  718 */     setToolbarVisibility(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void requestAttention() {}
/*      */   
/*      */ 
/*      */ 
/*      */   protected void swt_hide()
/*      */   {
/*  730 */     SWTSkinObject skinObjectMaster = getSkinObjectMaster();
/*  731 */     if ((skinObjectMaster instanceof SWTSkinObjectContainer)) {
/*  732 */       SWTSkinObjectContainer container = (SWTSkinObjectContainer)skinObjectMaster;
/*  733 */       Control oldComposite = container.getControl();
/*      */       
/*  735 */       container.setVisible(false);
/*  736 */       if ((oldComposite != null) && (!oldComposite.isDisposed())) {
/*  737 */         oldComposite.getShell().update();
/*      */       }
/*      */     }
/*      */     
/*  741 */     Composite oldComposite = getComposite();
/*  742 */     if ((oldComposite != null) && (!oldComposite.isDisposed()))
/*      */     {
/*  744 */       oldComposite.setVisible(false);
/*  745 */       oldComposite.getShell().update();
/*      */     }
/*      */     try
/*      */     {
/*  749 */       triggerEvent(4, null);
/*      */     } catch (Exception e) {
/*  751 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void updateUI()
/*      */   {
/*  761 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport()
/*      */       {
/*  765 */         if (!BaseMdiEntry.this.isDisposed()) {
/*  766 */           if (BaseMdiEntry.this.getEventListener() != null)
/*      */           {
/*  768 */             BaseMdiEntry.this.triggerEvent(5, null);
/*      */           }
/*  770 */           BaseMdiEntry.this.refreshTitle();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public boolean isDisposed() {
/*  777 */     return this.disposed;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Map<String, Object> getAutoOpenInfo()
/*      */   {
/*  784 */     Map<String, Object> autoOpenInfo = new LightHashMap();
/*  785 */     if (getParentID() != null) {
/*  786 */       autoOpenInfo.put("parentID", getParentID());
/*      */     }
/*  788 */     autoOpenInfo.put("title", getTitle());
/*  789 */     Object datasource = getDatasourceCore();
/*  790 */     if ((datasource instanceof DownloadManager)) {
/*      */       try {
/*  792 */         autoOpenInfo.put("dm", ((DownloadManager)datasource).getTorrent().getHashWrapper().toBase32String());
/*      */ 
/*      */       }
/*      */       catch (Throwable t) {}
/*      */     }
/*  797 */     else if ((datasource instanceof DownloadManager[])) {
/*  798 */       DownloadManager[] dms = (DownloadManager[])datasource;
/*  799 */       List<String> list = new ArrayList();
/*  800 */       for (DownloadManager dm : dms) {
/*      */         try {
/*  802 */           list.add(dm.getTorrent().getHashWrapper().toBase32String());
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*  806 */       autoOpenInfo.put("dms", list);
/*      */     }
/*      */     
/*  809 */     String eds = getExportableDatasource();
/*  810 */     if (eds != null) {
/*  811 */       autoOpenInfo.put("datasource", eds.toString());
/*      */     }
/*  813 */     return autoOpenInfo;
/*      */   }
/*      */   
/*      */   public void setCloseable(boolean closeable) {
/*  817 */     this.closeable = closeable;
/*      */     
/*  819 */     if (this.mdi != null) {
/*  820 */       if (closeable) {
/*  821 */         this.mdi.informAutoOpenSet(this, getAutoOpenInfo());
/*      */       } else {
/*  823 */         this.mdi.removeEntryAutoOpen(this.id);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void setDefaultExpanded(boolean defaultExpanded)
/*      */   {
/*  830 */     COConfigurationManager.setBooleanDefault("SideBar.Expanded." + this.id, defaultExpanded);
/*      */   }
/*      */   
/*      */   public boolean isExpanded()
/*      */   {
/*  835 */     return this.isExpanded == null ? COConfigurationManager.getBooleanParameter("SideBar.Expanded." + this.id) : this.isExpanded.booleanValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setExpanded(boolean expanded)
/*      */   {
/*  844 */     this.isExpanded = Boolean.valueOf(expanded);
/*  845 */     boolean defExpanded = true;
/*      */     try {
/*  847 */       defExpanded = ConfigurationDefaults.getInstance().getBooleanParameter("SideBar.Expanded." + this.id);
/*      */     }
/*      */     catch (ConfigurationParameterNotFoundException e) {}
/*      */     
/*  851 */     if (this.isExpanded.booleanValue() == defExpanded) {
/*  852 */       COConfigurationManager.removeParameter("SideBar.Expanded." + this.id);
/*      */     } else {
/*  854 */       COConfigurationManager.setParameter("SideBar.Expanded." + this.id, this.isExpanded.booleanValue());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isAdded()
/*      */   {
/*  862 */     return this.added;
/*      */   }
/*      */   
/*      */   public void setDisposed(boolean b) {
/*  866 */     this.disposed = b;
/*  867 */     this.added = (!b);
/*      */     
/*  869 */     if ((this.added) && 
/*  870 */       (getSkinObject() != null)) {
/*  871 */       getSkinObject().triggerListeners(7, this.datasource);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void setImageLeftID(String id)
/*      */   {
/*  878 */     this.imageLeftID = id;
/*  879 */     this.imageLeft = null;
/*  880 */     redraw();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getImageLeftID()
/*      */   {
/*  887 */     return this.imageLeftID;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setImageLeft(Image imageLeft)
/*      */   {
/*  894 */     this.imageLeft = imageLeft;
/*  895 */     this.imageLeftID = null;
/*  896 */     redraw();
/*      */   }
/*      */   
/*      */   public Image getImageLeft(String suffix) {
/*  900 */     if (this.imageLeft != null) {
/*  901 */       return this.imageLeft;
/*      */     }
/*  903 */     if (this.imageLeftID == null) {
/*  904 */       return null;
/*      */     }
/*  906 */     Image img = null;
/*  907 */     if (suffix == null) {
/*  908 */       img = ImageLoader.getInstance().getImage(this.imageLeftID);
/*      */     } else {
/*  910 */       img = ImageLoader.getInstance().getImage(this.imageLeftID + suffix);
/*      */     }
/*  912 */     if (ImageLoader.isRealImage(img))
/*      */     {
/*  914 */       return img;
/*      */     }
/*  916 */     return null;
/*      */   }
/*      */   
/*      */   public void releaseImageLeft(String suffix) {
/*  920 */     if (this.imageLeft != null) {
/*  921 */       ImageLoader.getInstance().releaseImage(this.imageLeftID + (suffix == null ? "" : suffix));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void viewTitleInfoRefresh(ViewTitleInfo titleInfoToRefresh)
/*      */   {
/*  930 */     if ((titleInfoToRefresh == null) || (this.viewTitleInfo != titleInfoToRefresh)) {
/*  931 */       return;
/*      */     }
/*  933 */     if (isDisposed()) {
/*  934 */       return;
/*      */     }
/*      */     
/*  937 */     String imageID = (String)this.viewTitleInfo.getTitleInfoProperty(2);
/*  938 */     if (imageID != null) {
/*  939 */       setImageLeftID(imageID.length() == 0 ? null : imageID);
/*      */     }
/*      */     
/*  942 */     redraw();
/*      */     
/*  944 */     String logID = (String)this.viewTitleInfo.getTitleInfoProperty(7);
/*  945 */     if (logID != null) {
/*  946 */       setLogID(logID);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void build() {}
/*      */   
/*      */ 
/*      */   public void setPreferredAfterID(String preferredAfterID)
/*      */   {
/*  957 */     this.preferredAfterID = preferredAfterID;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getPreferredAfterID()
/*      */   {
/*  964 */     return this.preferredAfterID;
/*      */   }
/*      */   
/*      */   public boolean requestClose() {
/*  968 */     return triggerEventRaw(8, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/*  975 */     writer.println("View: " + this.id + ": " + getTitle());
/*      */     try
/*      */     {
/*  978 */       writer.indent();
/*      */       
/*  980 */       writer.println("Parent: " + getParentID());
/*      */       
/*  982 */       writer.println("Added: " + this.added);
/*  983 */       writer.println("closeable: " + this.closeable);
/*  984 */       writer.println("Disposed: " + this.disposed);
/*  985 */       writer.println("hasBeenOpened: " + this.hasBeenOpened);
/*      */       
/*      */ 
/*  988 */       writer.println("control type: " + getControlType());
/*  989 */       writer.println("hasEventListener: " + (getEventListener() != null));
/*  990 */       writer.println("hasViewTitleInfo: " + (this.viewTitleInfo != null));
/*  991 */       writer.println("skinRef: " + this.skinRef);
/*      */ 
/*      */     }
/*      */     catch (Exception e) {}finally
/*      */     {
/*  996 */       writer.exdent();
/*      */     }
/*      */     
/*  999 */     if ((getEventListener() instanceof AEDiagnosticsEvidenceGenerator)) {
/*      */       try
/*      */       {
/* 1002 */         writer.indent();
/*      */         
/* 1004 */         ((AEDiagnosticsEvidenceGenerator)getEventListener()).generate(writer);
/*      */ 
/*      */       }
/*      */       catch (Exception e) {}finally
/*      */       {
/* 1009 */         writer.exdent();
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
/*      */ 
/*      */   public void closeView()
/*      */   {
/* 1024 */     if (this.mdi != null) {
/* 1025 */       this.mdi.closeEntry(this.id);
/*      */     }
/*      */     
/* 1028 */     super.closeView();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setEventListener(UISWTViewEventListener _eventListener, boolean doCreate)
/*      */     throws UISWTViewEventCancelledException
/*      */   {
/* 1038 */     UISWTViewEventListener eventListener = getEventListener();
/* 1039 */     if ((eventListener instanceof UIToolBarEnablerBase)) {
/* 1040 */       removeToolbarEnabler((UIToolBarEnablerBase)eventListener);
/*      */     }
/* 1042 */     if (((eventListener instanceof ViewTitleInfo)) && (this.viewTitleInfo == eventListener)) {
/* 1043 */       setViewTitleInfo(null);
/*      */     }
/*      */     
/* 1046 */     if ((_eventListener instanceof UISWTViewEventListenerHolder)) {
/* 1047 */       UISWTViewEventListenerHolder h = (UISWTViewEventListenerHolder)_eventListener;
/* 1048 */       UISWTViewEventListener delegatedEventListener = h.getDelegatedEventListener(this);
/* 1049 */       if (delegatedEventListener != null) {
/* 1050 */         _eventListener = delegatedEventListener;
/*      */       }
/*      */     }
/*      */     
/* 1054 */     if ((_eventListener instanceof UIToolBarEnablerBase)) {
/* 1055 */       addToolbarEnabler((UIToolBarEnablerBase)_eventListener);
/*      */     }
/* 1057 */     if (((_eventListener instanceof ViewTitleInfo)) && (this.viewTitleInfo == null)) {
/* 1058 */       setViewTitleInfo((ViewTitleInfo)_eventListener);
/*      */     }
/*      */     
/*      */ 
/* 1062 */     if ((_eventListener instanceof BasicPluginViewImpl)) {
/* 1063 */       String existing_id = getImageLeftID();
/*      */       
/* 1065 */       if ((existing_id == null) || ("image.sidebar.plugin".equals(existing_id))) {
/* 1066 */         setImageLeftID("image.sidebar.logview");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1071 */     super.setEventListener(_eventListener, doCreate);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setDatasource(Object datasource)
/*      */   {
/* 1079 */     super.setDatasource(datasource);
/*      */     
/* 1081 */     triggerDatasourceListeners();
/* 1082 */     if ((isAdded()) && 
/* 1083 */       (getSkinObject() != null)) {
/* 1084 */       getSkinObject().triggerListeners(7, datasource);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTitle(String title)
/*      */   {
/* 1095 */     super.setTitle(title);
/* 1096 */     redraw();
/*      */   }
/*      */   
/*      */   public void addListener(MdiSWTMenuHackListener l) {
/* 1100 */     synchronized (this) {
/* 1101 */       if (this.listMenuHackListners == null) {
/* 1102 */         this.listMenuHackListners = new ArrayList(1);
/*      */       }
/* 1104 */       if (!this.listMenuHackListners.contains(l)) {
/* 1105 */         this.listMenuHackListners.add(l);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeListener(MdiSWTMenuHackListener l) {
/* 1111 */     synchronized (this) {
/* 1112 */       if (this.listMenuHackListners == null) {
/* 1113 */         this.listMenuHackListners = new ArrayList(1);
/*      */       }
/* 1115 */       this.listMenuHackListners.remove(l);
/*      */     }
/*      */   }
/*      */   
/*      */   public MdiSWTMenuHackListener[] getMenuHackListeners() {
/* 1120 */     synchronized (this) {
/* 1121 */       if (this.listMenuHackListners == null) {
/* 1122 */         return new MdiSWTMenuHackListener[0];
/*      */       }
/* 1124 */       return (MdiSWTMenuHackListener[])this.listMenuHackListners.toArray(new MdiSWTMenuHackListener[0]);
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/mdi/BaseMdiEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */