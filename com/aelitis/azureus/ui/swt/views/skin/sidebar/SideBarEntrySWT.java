/*      */ package com.aelitis.azureus.ui.swt.views.skin.sidebar;
/*      */ 
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*      */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*      */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImage;
/*      */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import com.aelitis.azureus.ui.swt.mdi.BaseMdiEntry;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinProperties;
/*      */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.InfoBarUtil;
/*      */ import com.aelitis.azureus.util.MapUtils;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.DisposeListener;
/*      */ import org.eclipse.swt.events.TreeEvent;
/*      */ import org.eclipse.swt.events.TreeListener;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.Cursor;
/*      */ import org.eclipse.swt.graphics.Font;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.eclipse.swt.widgets.Tree;
/*      */ import org.eclipse.swt.widgets.TreeItem;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.debug.ObfusticateImage;
/*      */ import org.gudy.azureus2.ui.swt.debug.ObfusticateTab;
/*      */ import org.gudy.azureus2.ui.swt.debug.UIDebugGenerator;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListenerEx;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewImpl;
/*      */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
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
/*      */ public class SideBarEntrySWT
/*      */   extends BaseMdiEntry
/*      */   implements DisposeListener
/*      */ {
/*      */   private static final boolean DO_OUR_OWN_TREE_INDENT = true;
/*      */   private static final int SIDEBAR_SPACING = 2;
/*   77 */   private int IMAGELEFT_SIZE = 20;
/*      */   
/*   79 */   private int IMAGELEFT_GAP = 5;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final boolean ALWAYS_IMAGE_GAP = true;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String SO_ID_ENTRY_WRAPPER = "mdi.content.item";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String SO_ID_TOOLBAR = "mdientry.toolbar.full";
/*      */   
/*      */ 
/*      */ 
/*   96 */   private static long uniqueNumber = 0L;
/*      */   
/*      */   private TreeItem swtItem;
/*      */   
/*  100 */   private List<SideBarVitalityImageSWT> listVitalityImages = Collections.EMPTY_LIST;
/*      */   
/*      */ 
/*      */   private final SideBar sidebar;
/*      */   
/*      */ 
/*      */   private int maxIndicatorWidth;
/*      */   
/*      */ 
/*      */   private Image imgClose;
/*      */   
/*      */   private Image imgCloseSelected;
/*      */   
/*      */   private Color bg;
/*      */   
/*      */   private Color fg;
/*      */   
/*      */   private Color bgSel;
/*      */   
/*      */   private Color fgSel;
/*      */   
/*      */   private boolean showonSWTItemSet;
/*      */   
/*      */   private final SWTSkin skin;
/*      */   
/*      */   private SWTSkinObjectContainer soParent;
/*      */   
/*      */   private boolean buildonSWTItemSet;
/*      */   
/*  129 */   private boolean selectable = true;
/*      */   
/*  131 */   private boolean neverPainted = true;
/*      */   
/*  133 */   private long attention_start = -1L;
/*      */   
/*      */   private boolean attention_flash_on;
/*      */   
/*      */   public SideBarEntrySWT(SideBar sidebar, SWTSkin _skin, String id, String parentViewID)
/*      */   {
/*  139 */     super(sidebar, id, parentViewID);
/*  140 */     this.skin = _skin;
/*      */     
/*  142 */     if (id == null) {
/*  143 */       this.logID = "null";
/*      */     } else {
/*  145 */       int i = id.indexOf('_');
/*  146 */       if (i > 0) {
/*  147 */         this.logID = id.substring(0, i);
/*      */       } else {
/*  149 */         this.logID = id;
/*      */       }
/*      */     }
/*      */     
/*  153 */     this.sidebar = sidebar;
/*      */     
/*  155 */     this.IMAGELEFT_GAP = Utils.adjustPXForDPI(this.IMAGELEFT_GAP);
/*  156 */     this.IMAGELEFT_SIZE = Utils.adjustPXForDPI(this.IMAGELEFT_SIZE);
/*  157 */     updateColors();
/*      */   }
/*      */   
/*      */   protected void updateColors() {
/*  161 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  163 */         SWTSkinProperties skinProperties = SideBarEntrySWT.this.skin.getSkinProperties();
/*  164 */         SideBarEntrySWT.this.bg = skinProperties.getColor("color.sidebar.bg");
/*  165 */         SideBarEntrySWT.this.fg = skinProperties.getColor("color.sidebar." + (SideBarEntrySWT.this.isSelectable() ? "text" : "header"));
/*      */         
/*  167 */         SideBarEntrySWT.this.bgSel = skinProperties.getColor("color.sidebar.selected.bg");
/*  168 */         SideBarEntrySWT.this.fgSel = skinProperties.getColor("color.sidebar.selected.fg");
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public TreeItem getTreeItem()
/*      */   {
/*  175 */     return this.swtItem;
/*      */   }
/*      */   
/*      */   public void setTreeItem(TreeItem treeItem) {
/*  179 */     if ((this.swtItem != null) && (treeItem != null)) {
/*  180 */       Debug.out("Warning: Sidebar " + this.id + " already has a treeitem");
/*  181 */       return;
/*      */     }
/*  183 */     this.swtItem = treeItem;
/*  184 */     setDisposed(false);
/*      */     
/*  186 */     if (treeItem != null) {
/*  187 */       ImageLoader imageLoader = ImageLoader.getInstance();
/*  188 */       this.imgClose = imageLoader.getImage("image.sidebar.closeitem");
/*  189 */       this.imgCloseSelected = imageLoader.getImage("image.sidebar.closeitem-selected");
/*      */       
/*  191 */       treeItem.addDisposeListener(this);
/*      */       
/*  193 */       treeItem.getParent().addTreeListener(new TreeListener() {
/*      */         public void treeExpanded(TreeEvent e) {
/*  195 */           if (e.item == SideBarEntrySWT.this.swtItem) {
/*  196 */             SideBarEntrySWT.this.setExpanded(true);
/*      */           }
/*      */         }
/*      */         
/*      */         public void treeCollapsed(TreeEvent e) {
/*  201 */           if (e.item == SideBarEntrySWT.this.swtItem) {
/*  202 */             SideBarEntrySWT.this.setExpanded(false);
/*      */ 
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */ 
/*  209 */       });
/*  210 */       TreeItem parentItem = treeItem.getParentItem();
/*  211 */       if (parentItem != null) {
/*  212 */         MdiEntry parentEntry = (MdiEntry)parentItem.getData("MdiEntry");
/*  213 */         if (parentEntry.isExpanded()) {
/*  214 */           parentItem.setExpanded(true);
/*      */         }
/*      */       }
/*      */       
/*  218 */       setExpanded(isExpanded());
/*      */     }
/*  220 */     if (this.buildonSWTItemSet) {
/*  221 */       build();
/*      */     }
/*  223 */     if (this.showonSWTItemSet) {
/*  224 */       show();
/*      */     }
/*      */   }
/*      */   
/*      */   public MdiEntryVitalityImage addVitalityImage(String imageID)
/*      */   {
/*  230 */     synchronized (this) {
/*  231 */       SideBarVitalityImageSWT vitalityImage = new SideBarVitalityImageSWT(this, imageID);
/*      */       
/*  233 */       if (this.listVitalityImages == Collections.EMPTY_LIST) {
/*  234 */         this.listVitalityImages = new ArrayList(1);
/*      */       }
/*  236 */       this.listVitalityImages.add(vitalityImage);
/*  237 */       return vitalityImage;
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public MdiEntryVitalityImage[] getVitalityImages()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_1
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: getfield 1004	com/aelitis/azureus/ui/swt/views/skin/sidebar/SideBarEntrySWT:listVitalityImages	Ljava/util/List;
/*      */     //   8: iconst_0
/*      */     //   9: anewarray 531	com/aelitis/azureus/ui/mdi/MdiEntryVitalityImage
/*      */     //   12: invokeinterface 1248 2 0
/*      */     //   17: checkcast 524	[Lcom/aelitis/azureus/ui/mdi/MdiEntryVitalityImage;
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: areturn
/*      */     //   23: astore_2
/*      */     //   24: aload_1
/*      */     //   25: monitorexit
/*      */     //   26: aload_2
/*      */     //   27: athrow
/*      */     // Line number table:
/*      */     //   Java source line #242	-> byte code offset #0
/*      */     //   Java source line #243	-> byte code offset #4
/*      */     //   Java source line #244	-> byte code offset #23
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	28	0	this	SideBarEntrySWT
/*      */     //   2	23	1	Ljava/lang/Object;	Object
/*      */     //   23	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	22	23	finally
/*      */     //   23	26	23	finally
/*      */   }
/*      */   
/*      */   public MdiEntryVitalityImage getVitalityImage(int hitX, int hitY)
/*      */   {
/*  248 */     MdiEntryVitalityImage[] vitalityImages = getVitalityImages();
/*  249 */     for (int i = 0; i < vitalityImages.length; i++) {
/*  250 */       SideBarVitalityImageSWT vitalityImage = (SideBarVitalityImageSWT)vitalityImages[i];
/*  251 */       if (vitalityImage.isVisible())
/*      */       {
/*      */ 
/*  254 */         Rectangle hitArea = vitalityImage.getHitArea();
/*  255 */         if ((hitArea != null) && (hitArea.contains(hitX, hitY)))
/*  256 */           return vitalityImage;
/*      */       }
/*      */     }
/*  259 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public void requestAttention()
/*      */   {
/*  265 */     this.attention_start = SystemTime.getMonotonousTime();
/*      */     
/*  267 */     this.sidebar.requestAttention(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean attentionUpdate(int ticks)
/*      */   {
/*  274 */     if ((this.attention_start == -1L) || (SystemTime.getMonotonousTime() - this.attention_start > 5000L))
/*      */     {
/*      */ 
/*  277 */       this.attention_start = -1L;
/*      */       
/*  279 */       return false;
/*      */     }
/*      */     
/*  282 */     this.attention_flash_on = (ticks % 2 == 0);
/*      */     
/*  284 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  290 */   boolean isRedrawQueued = false;
/*      */   private InfoBarUtil toolBarInfoBar;
/*      */   
/*      */   public void redraw() {
/*  294 */     if (this.neverPainted) {
/*  295 */       return;
/*      */     }
/*  297 */     synchronized (this) {
/*  298 */       if (this.isRedrawQueued) {
/*  299 */         return;
/*      */       }
/*  301 */       this.isRedrawQueued = true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  306 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*      */       public void runSupport() {
/*      */         try {
/*  309 */           if ((SideBarEntrySWT.this.swtItem == null) || (SideBarEntrySWT.this.swtItem.isDisposed())) {
/*      */             return;
/*      */           }
/*  312 */           Tree tree = SideBarEntrySWT.this.swtItem.getParent();
/*  313 */           if (!tree.isVisible()) {
/*      */             return;
/*      */           }
/*      */           try {
/*  317 */             Rectangle bounds = SideBarEntrySWT.this.swtItem.getBounds();
/*  318 */             Rectangle treeBounds = tree.getBounds();
/*  319 */             tree.redraw(0, bounds.y, treeBounds.width, bounds.height, true);
/*      */ 
/*      */           }
/*      */           catch (NullPointerException npe) {}
/*      */         }
/*      */         finally
/*      */         {
/*  326 */           synchronized (SideBarEntrySWT.this) {
/*  327 */             SideBarEntrySWT.this.isRedrawQueued = false;
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   protected Rectangle swt_getBounds() {
/*  335 */     if ((this.swtItem == null) || (this.swtItem.isDisposed())) {
/*  336 */       return null;
/*      */     }
/*      */     try {
/*  339 */       Tree tree = this.swtItem.getParent();
/*  340 */       Rectangle bounds = this.swtItem.getBounds();
/*  341 */       Rectangle treeBounds = tree.getClientArea();
/*  342 */       return new Rectangle(0, bounds.y, treeBounds.width, bounds.height);
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (NullPointerException e)
/*      */     {
/*      */ 
/*      */ 
/*  351 */       Debug.outNoStack("NPE @ " + Debug.getCompressedStackTrace(), true);
/*      */     } catch (Exception e) {
/*  353 */       Debug.out(e);
/*      */     }
/*  355 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setExpanded(final boolean expanded)
/*      */   {
/*  362 */     super.setExpanded(expanded);
/*  363 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  365 */         if ((SideBarEntrySWT.this.swtItem != null) && (!SideBarEntrySWT.this.isDisposed())) {
/*  366 */           SideBarEntrySWT.this.swtItem.setExpanded(expanded);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public void expandTo() {
/*  373 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  375 */         if ((SideBarEntrySWT.this.swtItem == null) || (SideBarEntrySWT.this.isDisposed())) {
/*  376 */           return;
/*      */         }
/*      */         
/*  379 */         TreeItem item = SideBarEntrySWT.this.swtItem.getParentItem();
/*  380 */         while (item != null) {
/*  381 */           item.setExpanded(true);
/*      */           
/*  383 */           item = item.getParentItem();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean close(boolean force)
/*      */   {
/*  393 */     if (!super.close(force)) {
/*  394 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  398 */     this.mdi.removeItem(this);
/*      */     
/*      */ 
/*  401 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  403 */         if ((SideBarEntrySWT.this.swtItem != null) && (!SideBarEntrySWT.this.swtItem.isDisposed())) {
/*      */           try {
/*  405 */             SideBarEntrySWT.this.swtItem.setFont(null);
/*  406 */             SideBarEntrySWT.this.swtItem.dispose();
/*      */ 
/*      */           }
/*      */           catch (Exception e)
/*      */           {
/*  411 */             Debug.outNoStack("Warning on SidebarEntry dispose: " + e.toString(), false);
/*      */           }
/*      */           finally {
/*  414 */             SideBarEntrySWT.this.swtItem = null;
/*      */           }
/*      */         }
/*      */       }
/*  418 */     });
/*  419 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canBuildStandAlone()
/*      */   {
/*  425 */     String skinRef = getSkinRef();
/*      */     
/*  427 */     if (skinRef != null)
/*      */     {
/*  429 */       return true;
/*      */     }
/*      */     
/*      */ 
/*  433 */     UISWTViewEventListener event_listener = getEventListener();
/*      */     
/*  435 */     if (((event_listener instanceof UISWTViewCoreEventListenerEx)) && (((UISWTViewCoreEventListenerEx)event_listener).isCloneable()))
/*      */     {
/*  437 */       return true;
/*      */     }
/*      */     
/*      */ 
/*  441 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public SWTSkinObjectContainer buildStandAlone(SWTSkinObjectContainer soParent)
/*      */   {
/*  448 */     Control control = null;
/*      */     
/*      */ 
/*      */ 
/*  452 */     Composite parent = soParent.getComposite();
/*      */     
/*  454 */     String skinRef = getSkinRef();
/*      */     
/*  456 */     if (skinRef != null)
/*      */     {
/*  458 */       Shell shell = parent.getShell();
/*  459 */       Cursor cursor = shell.getCursor();
/*      */       try {
/*  461 */         shell.setCursor(shell.getDisplay().getSystemCursor(1));
/*      */         
/*      */ 
/*      */ 
/*  465 */         SWTSkinObjectContainer soContents = (SWTSkinObjectContainer)this.skin.createSkinObject("MdiContents." + uniqueNumber++, "mdi.content.item", soParent, null);
/*      */         
/*      */ 
/*      */ 
/*  469 */         SWTSkinObject skinObject = this.skin.createSkinObject(this.id, skinRef, soContents, getDatasourceCore());
/*      */         
/*      */ 
/*  472 */         control = skinObject.getControl();
/*  473 */         control.setLayoutData(Utils.getFilledFormData());
/*  474 */         control.getParent().layout(true, true);
/*      */         
/*  476 */         soContents.setVisible(true);
/*      */         
/*  478 */         return soContents;
/*      */       }
/*      */       finally {
/*  481 */         shell.setCursor(cursor);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  486 */     UISWTViewEventListener event_listener = getEventListener();
/*      */     
/*  488 */     if (((event_listener instanceof UISWTViewCoreEventListenerEx)) && (((UISWTViewCoreEventListenerEx)event_listener).isCloneable()))
/*      */     {
/*  490 */       final UISWTViewImpl view = new UISWTViewImpl(getParentID(), this.id, true);
/*      */       try
/*      */       {
/*  493 */         view.setEventListener(((UISWTViewCoreEventListenerEx)event_listener).getClone(), false);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  497 */         Debug.out(e);
/*      */       }
/*      */       
/*  500 */       view.setDatasource(this.datasource);
/*      */       try
/*      */       {
/*  503 */         SWTSkinObjectContainer soContents = (SWTSkinObjectContainer)this.skin.createSkinObject("MdiIView." + uniqueNumber++, "mdi.content.item", soParent);
/*      */         
/*      */ 
/*      */ 
/*  507 */         parent.setBackgroundMode(0);
/*      */         
/*  509 */         final Composite viewComposite = soContents.getComposite();
/*  510 */         boolean doGridLayout = true;
/*  511 */         if (getControlType() == 257) {
/*  512 */           doGridLayout = false;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  518 */         if (doGridLayout) {
/*  519 */           GridLayout gridLayout = new GridLayout();
/*  520 */           gridLayout.horizontalSpacing = (gridLayout.verticalSpacing = gridLayout.marginHeight = gridLayout.marginWidth = 0);
/*  521 */           viewComposite.setLayout(gridLayout);
/*  522 */           viewComposite.setLayoutData(Utils.getFilledFormData());
/*      */         }
/*      */         
/*  525 */         view.setPluginSkinObject(soContents);
/*  526 */         view.initialize(viewComposite);
/*      */         
/*      */ 
/*      */ 
/*  530 */         Composite iviewComposite = view.getComposite();
/*  531 */         control = iviewComposite;
/*      */         
/*      */ 
/*      */ 
/*  535 */         if (doGridLayout) {
/*  536 */           Object existingLayoutData = iviewComposite.getLayoutData();
/*  537 */           Object existingParentLayoutData = iviewComposite.getParent().getLayoutData();
/*  538 */           if ((existingLayoutData == null) || ((!(existingLayoutData instanceof GridData)) && ((existingParentLayoutData instanceof GridLayout))))
/*      */           {
/*      */ 
/*  541 */             GridData gridData = new GridData(1808);
/*  542 */             iviewComposite.setLayoutData(gridData);
/*      */           }
/*      */         }
/*      */         
/*  546 */         parent.layout(true, true);
/*      */         
/*  548 */         final UIUpdater updater = UIFunctionsManager.getUIFunctions().getUIUpdater();
/*      */         
/*  550 */         updater.addUpdater(new UIUpdatable()
/*      */         {
/*      */           public void updateUI()
/*      */           {
/*  554 */             if (viewComposite.isDisposed()) {
/*  555 */               updater.removeUpdater(this);
/*      */             } else {
/*  557 */               view.triggerEvent(5, null);
/*      */             }
/*      */           }
/*      */           
/*      */           public String getUpdateUIName() {
/*  562 */             return "popout";
/*      */           }
/*      */           
/*  565 */         });
/*  566 */         soContents.setVisible(true);
/*      */         
/*  568 */         view.triggerEvent(3, null);
/*      */         
/*  570 */         return soContents;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  574 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  579 */     return null;
/*      */   }
/*      */   
/*      */   public void build() {
/*  583 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  585 */         SideBarEntrySWT.this.swt_build();
/*  586 */         SideBarEntrySWT.this.build();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public boolean swt_build() {
/*  592 */     if (this.swtItem == null) {
/*  593 */       this.buildonSWTItemSet = true;
/*  594 */       return true;
/*      */     }
/*  596 */     this.buildonSWTItemSet = false;
/*      */     
/*  598 */     if (getSkinObject() == null) {
/*  599 */       Control control = null;
/*      */       
/*  601 */       Composite parent = this.soParent == null ? Utils.findAnyShell() : this.soParent.getComposite();
/*      */       
/*      */ 
/*  604 */       String skinRef = getSkinRef();
/*  605 */       if (skinRef != null) {
/*  606 */         Shell shell = parent.getShell();
/*  607 */         Cursor cursor = shell.getCursor();
/*      */         try {
/*  609 */           shell.setCursor(shell.getDisplay().getSystemCursor(1));
/*      */           
/*      */ 
/*      */ 
/*  613 */           SWTSkinObjectContainer soContents = (SWTSkinObjectContainer)this.skin.createSkinObject("MdiContents." + uniqueNumber++, "mdi.content.item", getParentSkinObject(), null);
/*      */           
/*      */ 
/*      */ 
/*  617 */           SWTSkinObject skinObject = this.skin.createSkinObject(this.id, skinRef, soContents, getDatasourceCore());
/*      */           
/*      */ 
/*  620 */           control = skinObject.getControl();
/*  621 */           control.setLayoutData(Utils.getFilledFormData());
/*  622 */           control.getParent().layout(true, true);
/*  623 */           setPluginSkinObject(skinObject);
/*  624 */           initialize((Composite)control);
/*  625 */           setSkinObjectMaster(soContents);
/*      */         } finally {
/*  627 */           shell.setCursor(cursor);
/*      */         }
/*      */       }
/*      */       else {
/*      */         try {
/*  632 */           SWTSkinObjectContainer soContents = (SWTSkinObjectContainer)this.skin.createSkinObject("MdiIView." + uniqueNumber++, "mdi.content.item", getParentSkinObject());
/*      */           
/*      */ 
/*      */ 
/*  636 */           parent.setBackgroundMode(0);
/*      */           
/*  638 */           Composite viewComposite = soContents.getComposite();
/*  639 */           boolean doGridLayout = true;
/*  640 */           if (getControlType() == 257) {
/*  641 */             doGridLayout = false;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  647 */           if (doGridLayout) {
/*  648 */             GridLayout gridLayout = new GridLayout();
/*  649 */             gridLayout.horizontalSpacing = (gridLayout.verticalSpacing = gridLayout.marginHeight = gridLayout.marginWidth = 0);
/*  650 */             viewComposite.setLayout(gridLayout);
/*  651 */             viewComposite.setLayoutData(Utils.getFilledFormData());
/*      */           }
/*      */           
/*  654 */           setPluginSkinObject(soContents);
/*  655 */           initialize(viewComposite);
/*  656 */           String fullTitle = getFullTitle();
/*  657 */           if (fullTitle != null) {
/*  658 */             this.swtItem.setText(getFullTitle());
/*      */           }
/*      */           
/*  661 */           Composite iviewComposite = getComposite();
/*  662 */           control = iviewComposite;
/*      */           
/*      */ 
/*      */ 
/*  666 */           if (doGridLayout) {
/*  667 */             Object existingLayoutData = iviewComposite.getLayoutData();
/*  668 */             Object existingParentLayoutData = iviewComposite.getParent().getLayoutData();
/*  669 */             if ((existingLayoutData == null) || ((!(existingLayoutData instanceof GridData)) && ((existingParentLayoutData instanceof GridLayout))))
/*      */             {
/*      */ 
/*  672 */               GridData gridData = new GridData(1808);
/*  673 */               iviewComposite.setLayoutData(gridData);
/*      */             }
/*      */           }
/*      */           
/*  677 */           parent.layout(true, true);
/*      */           
/*  679 */           setSkinObjectMaster(soContents);
/*      */         } catch (Exception e) {
/*  681 */           Debug.out("Error creating sidebar content area for " + this.id, e);
/*  682 */           close(true);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  687 */       if ((control != null) && (!control.isDisposed())) {
/*  688 */         control.setData("BaseMDIEntry", this);
/*  689 */         control.addDisposeListener(new DisposeListener() {
/*      */           public void widgetDisposed(DisposeEvent e) {
/*  691 */             SideBarEntrySWT.this.close(true);
/*      */           }
/*      */         });
/*      */       } else {
/*  695 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  699 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void show()
/*      */   {
/*  710 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*      */       public void runSupport() {
/*  712 */         SideBarEntrySWT.this.swt_show();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private void swt_show() {
/*  718 */     if (this.swtItem == null) {
/*  719 */       this.showonSWTItemSet = true;
/*  720 */       return;
/*      */     }
/*  722 */     this.showonSWTItemSet = false;
/*  723 */     if (!swt_build()) {
/*  724 */       return;
/*      */     }
/*      */     
/*  727 */     triggerOpenListeners();
/*      */     
/*  729 */     this.swtItem.getParent().select(this.swtItem);
/*  730 */     this.swtItem.getParent().showItem(this.swtItem);
/*      */     
/*  732 */     super.show();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void hide()
/*      */   {
/*  739 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*      */       public void runSupport() {
/*  741 */         SideBarEntrySWT.this.hide();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   protected void swt_paintSideBar(Event event) {
/*  747 */     this.neverPainted = false;
/*      */     
/*  749 */     TreeItem treeItem = (TreeItem)event.item;
/*  750 */     if ((treeItem.isDisposed()) || (isDisposed())) {
/*  751 */       return;
/*      */     }
/*  753 */     Rectangle itemBounds = treeItem.getBounds();
/*  754 */     Rectangle drawBounds = event.gc.getClipping();
/*  755 */     if (drawBounds.isEmpty()) {
/*  756 */       drawBounds = event.getBounds();
/*      */     }
/*  758 */     Rectangle treeArea = treeItem.getParent().getClientArea();
/*  759 */     if (SideBar.isGTK3)
/*      */     {
/*  761 */       if (treeArea.width > itemBounds.width) {
/*  762 */         itemBounds.width = treeArea.width;
/*      */       }
/*  764 */       if (treeArea.x < itemBounds.x) {
/*  765 */         itemBounds.x = treeArea.x;
/*      */       }
/*  767 */       drawBounds = itemBounds;
/*      */     }
/*      */     
/*  770 */     String text = getTitle();
/*  771 */     if (text == null) {
/*  772 */       text = "";
/*      */     }
/*      */     
/*      */ 
/*  776 */     GC gc = event.gc;
/*      */     
/*  778 */     gc.setAntialias(1);
/*  779 */     gc.setAdvanced(true);
/*  780 */     Utils.setClipping(gc, null);
/*      */     
/*  782 */     boolean selected = (event.detail & 0x2) > 0;
/*  783 */     Color fgText = swt_paintEntryBG(event.detail, gc, drawBounds);
/*      */     
/*  785 */     Tree tree = (Tree)event.widget;
/*      */     
/*  787 */     Font font = tree.getFont();
/*  788 */     if ((font != null) && (!font.isDisposed())) {
/*  789 */       gc.setFont(font);
/*      */     }
/*      */     
/*  792 */     if ((SideBar.USE_NATIVE_EXPANDER) && (Utils.isGTK3)) {
/*  793 */       itemBounds.x = treeItem.getBounds().x;
/*      */     } else {
/*  795 */       TreeItem tempItem = treeItem.getParentItem();
/*      */       int indent;
/*  797 */       int indent; if ((!isCollapseDisabled()) && (tempItem == null) && (!Utils.isGTK)) {
/*  798 */         indent = 22;
/*      */       } else {
/*  800 */         indent = 10;
/*      */       }
/*  802 */       while (tempItem != null) {
/*  803 */         indent += 10;
/*  804 */         tempItem = tempItem.getParentItem();
/*      */       }
/*  806 */       if ((SideBar.USE_NATIVE_EXPANDER) && (Utils.isGTK)) {
/*  807 */         indent += 5;
/*      */       }
/*  809 */       itemBounds.x = indent;
/*      */     }
/*  811 */     int x1IndicatorOfs = 2;
/*  812 */     int x0IndicatorOfs = itemBounds.x;
/*      */     
/*      */ 
/*  815 */     if (this.viewTitleInfo != null) {
/*  816 */       String textIndicator = null;
/*      */       try {
/*  818 */         textIndicator = (String)this.viewTitleInfo.getTitleInfoProperty(0);
/*      */       } catch (Exception e) {
/*  820 */         Debug.out(e);
/*      */       }
/*  822 */       if (textIndicator != null)
/*      */       {
/*  824 */         Point textSize = gc.textExtent(textIndicator);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  830 */         int width = textSize.x + Utils.adjustPXForDPI(10);
/*  831 */         x1IndicatorOfs += width + 2;
/*  832 */         int startX = treeArea.width - x1IndicatorOfs;
/*      */         
/*  834 */         int textOffsetY = 0;
/*      */         
/*  836 */         int height = textSize.y + 1;
/*  837 */         int startY = itemBounds.y + (itemBounds.height - height) / 2;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  845 */         Color default_color = ColorCache.getSchemedColor(gc.getDevice(), "#5b6e87");
/*      */         
/*  847 */         Object color = this.viewTitleInfo.getTitleInfoProperty(8);
/*      */         
/*  849 */         if ((color instanceof int[]))
/*      */         {
/*  851 */           gc.setBackground(ColorCache.getColor(gc.getDevice(), (int[])color));
/*      */         }
/*      */         else
/*      */         {
/*  855 */           gc.setBackground(default_color);
/*      */         }
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
/*  874 */         Color text_color = Colors.white;
/*      */         
/*  876 */         gc.fillRoundRectangle(startX, startY, width, height, textSize.y * 2 / 3, height * 2 / 3);
/*      */         
/*      */ 
/*  879 */         if (color != null)
/*      */         {
/*  881 */           Color bg = gc.getBackground();
/*      */           
/*  883 */           int red = bg.getRed();
/*  884 */           int green = bg.getGreen();
/*  885 */           int blue = bg.getBlue();
/*      */           
/*  887 */           double brightness = Math.sqrt(red * red * 0.299D + green * green * 0.587D + blue * blue * 0.114D);
/*      */           
/*  889 */           if (brightness >= 130.0D) {
/*  890 */             text_color = Colors.black;
/*      */           }
/*      */           
/*  893 */           gc.setBackground(default_color);
/*      */           
/*  895 */           gc.drawRoundRectangle(startX, startY, width, height, textSize.y * 2 / 3, height * 2 / 3);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  900 */         if (this.maxIndicatorWidth > width) {
/*  901 */           this.maxIndicatorWidth = width;
/*      */         }
/*  903 */         gc.setForeground(text_color);
/*  904 */         GCStringPrinter.printString(gc, textIndicator, new Rectangle(startX, startY + textOffsetY, width, height), true, false, 16777216);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  913 */     if (isCloseable()) {
/*  914 */       Image img = selected ? this.imgCloseSelected : this.imgClose;
/*  915 */       Rectangle closeArea = img.getBounds();
/*  916 */       closeArea.x = (treeArea.width - closeArea.width - 2 - x1IndicatorOfs);
/*      */       
/*  918 */       itemBounds.y += (itemBounds.height - closeArea.height) / 2;
/*  919 */       x1IndicatorOfs += closeArea.width + 2;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  924 */       gc.drawImage(img, closeArea.x, closeArea.y);
/*  925 */       treeItem.setData("closeArea", closeArea);
/*      */     }
/*      */     
/*  928 */     MdiEntryVitalityImage[] vitalityImages = getVitalityImages();
/*  929 */     for (int i = 0; i < vitalityImages.length; i++) {
/*  930 */       SideBarVitalityImageSWT vitalityImage = (SideBarVitalityImageSWT)vitalityImages[i];
/*  931 */       if ((vitalityImage != null) && (vitalityImage.isVisible()) && (vitalityImage.getAlignment() == 131072))
/*      */       {
/*      */ 
/*      */ 
/*  935 */         vitalityImage.switchSuffix(selected ? "-selected" : "");
/*  936 */         Image image = vitalityImage.getImage();
/*  937 */         if ((image != null) && (!image.isDisposed())) {
/*  938 */           Rectangle bounds = image.getBounds();
/*  939 */           bounds.x = (treeArea.width - bounds.width - 2 - x1IndicatorOfs);
/*      */           
/*  941 */           itemBounds.y += (itemBounds.height - bounds.height) / 2;
/*  942 */           x1IndicatorOfs += bounds.width + 2;
/*      */           
/*  944 */           gc.drawImage(image, bounds.x, bounds.y);
/*      */           
/*  946 */           bounds.y -= itemBounds.y;
/*  947 */           vitalityImage.setHitArea(bounds);
/*      */         }
/*      */       }
/*      */     }
/*  951 */     boolean greyScale = false;
/*      */     
/*  953 */     if (this.viewTitleInfo != null)
/*      */     {
/*  955 */       Object active_state = this.viewTitleInfo.getTitleInfoProperty(9);
/*      */       
/*  957 */       if ((active_state instanceof Long))
/*      */       {
/*  959 */         greyScale = ((Long)active_state).longValue() == 2L;
/*      */       }
/*      */     }
/*      */     
/*  963 */     String suffix = selected ? "-selected" : null;
/*  964 */     Image imageLeft = getImageLeft(suffix);
/*  965 */     if ((imageLeft == null) && (selected)) {
/*  966 */       releaseImageLeft(suffix);
/*  967 */       suffix = null;
/*  968 */       imageLeft = getImageLeft(null);
/*      */     }
/*  970 */     if (imageLeft != null) {
/*  971 */       Rectangle clipping = gc.getClipping();
/*  972 */       Utils.setClipping(gc, new Rectangle(x0IndicatorOfs, itemBounds.y, this.IMAGELEFT_SIZE, itemBounds.height));
/*      */       
/*      */ 
/*  975 */       if (greyScale) {
/*  976 */         greyScale = false;
/*  977 */         String imageLeftID = getImageLeftID();
/*  978 */         if (imageLeftID != null) {
/*  979 */           Image grey = ImageLoader.getInstance().getImage(imageLeftID + "-gray");
/*      */           
/*  981 */           if (grey != null) {
/*  982 */             imageLeft = grey;
/*  983 */             gc.setAlpha(160);
/*  984 */             greyScale = true;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  989 */       Rectangle bounds = imageLeft.getBounds();
/*  990 */       int w = bounds.width;
/*  991 */       int h = bounds.height;
/*  992 */       if (w > this.IMAGELEFT_SIZE) {
/*  993 */         float pct = this.IMAGELEFT_SIZE / w;
/*  994 */         w = this.IMAGELEFT_SIZE;
/*  995 */         h = (int)(h * pct);
/*      */       }
/*  997 */       int x = x0IndicatorOfs + (this.IMAGELEFT_SIZE - w) / 2;
/*  998 */       int y = itemBounds.y + (itemBounds.height - h) / 2;
/*      */       
/* 1000 */       gc.setAdvanced(true);
/* 1001 */       gc.setInterpolation(2);
/* 1002 */       gc.drawImage(imageLeft, 0, 0, bounds.width, bounds.height, x, y, w, h);
/*      */       
/* 1004 */       if (greyScale) {
/* 1005 */         String imageLeftID = getImageLeftID();
/* 1006 */         gc.setAlpha(255);
/* 1007 */         ImageLoader.getInstance().releaseImage(imageLeftID + "-gray");
/*      */       }
/*      */       
/* 1010 */       releaseImageLeft(suffix);
/* 1011 */       Utils.setClipping(gc, clipping);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1017 */       x0IndicatorOfs += this.IMAGELEFT_SIZE + this.IMAGELEFT_GAP;
/*      */       
/* 1019 */       releaseImageLeft(suffix);
/*      */     }
/* 1021 */     else if (isSelectable()) {
/* 1022 */       x0IndicatorOfs += this.IMAGELEFT_SIZE + this.IMAGELEFT_GAP;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1033 */     Rectangle clipping = new Rectangle(x0IndicatorOfs, itemBounds.y, treeArea.width - x1IndicatorOfs - 2 - x0IndicatorOfs, itemBounds.height);
/*      */     
/*      */ 
/*      */ 
/* 1037 */     if (drawBounds.intersects(clipping)) {
/* 1038 */       int style = 0;
/* 1039 */       if (!isSelectable()) {
/* 1040 */         Font headerFont = this.sidebar.getHeaderFont();
/* 1041 */         if ((headerFont != null) && (!headerFont.isDisposed())) {
/* 1042 */           gc.setFont(headerFont);
/*      */         }
/* 1044 */         text = text.toUpperCase();
/*      */         
/* 1046 */         gc.setForeground(ColorCache.getColor(gc.getDevice(), 255, 255, 255));
/* 1047 */         gc.setAlpha(100);
/* 1048 */         clipping.x += 1;
/* 1049 */         clipping.y += 1;
/*      */         
/* 1051 */         GCStringPrinter sp = new GCStringPrinter(gc, text, clipping, true, false, style);
/*      */         
/* 1053 */         sp.printString();
/* 1054 */         gc.setAlpha(255);
/*      */         
/* 1056 */         clipping.x -= 1;
/* 1057 */         clipping.y -= 1;
/* 1058 */         gc.setForeground(fgText);
/*      */       } else {
/* 1060 */         if (treeItem.getItemCount() > 0) {
/* 1061 */           Font headerFont = this.sidebar.getHeaderFont();
/* 1062 */           if ((headerFont != null) && (!headerFont.isDisposed())) {
/* 1063 */             gc.setFont(headerFont);
/*      */           }
/*      */         }
/* 1066 */         gc.setForeground(fgText);
/*      */       }
/*      */       
/*      */ 
/* 1070 */       GCStringPrinter sp = new GCStringPrinter(gc, text, clipping, true, false, style);
/*      */       
/* 1072 */       sp.printString();
/* 1073 */       clipping.x += sp.getCalculatedSize().x + 5;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1079 */     for (int i = 0; i < vitalityImages.length; i++) {
/* 1080 */       SideBarVitalityImageSWT vitalityImage = (SideBarVitalityImageSWT)vitalityImages[i];
/* 1081 */       if ((vitalityImage.isVisible()) && (vitalityImage.getAlignment() == 16384))
/*      */       {
/*      */ 
/*      */ 
/* 1085 */         vitalityImage.switchSuffix(selected ? "-selected" : "");
/* 1086 */         Image image = vitalityImage.getImage();
/* 1087 */         if ((image != null) && (!image.isDisposed())) {
/* 1088 */           Rectangle bounds = image.getBounds();
/* 1089 */           bounds.x = clipping.x;
/* 1090 */           itemBounds.y += (itemBounds.height - bounds.height) / 2;
/* 1091 */           clipping.x += bounds.width + 2;
/*      */           
/* 1093 */           if (clipping.x > treeArea.width - x1IndicatorOfs) {
/* 1094 */             vitalityImage.setHitArea(null);
/*      */           }
/*      */           else {
/* 1097 */             gc.drawImage(image, bounds.x, bounds.y);
/* 1098 */             vitalityImage.setHitArea(bounds);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1106 */     if ((treeItem.getItemCount() > 0) && (!isCollapseDisabled()) && (!SideBar.USE_NATIVE_EXPANDER))
/*      */     {
/* 1108 */       gc.setAntialias(1);
/* 1109 */       Color oldBG = gc.getBackground();
/* 1110 */       gc.setBackground(event.display.getSystemColor(24));
/* 1111 */       int baseX = 22;
/* 1112 */       if (treeItem.getExpanded()) {
/* 1113 */         int xStart = 12;
/* 1114 */         int arrowSize = 8;
/* 1115 */         int yStart = itemBounds.height - (itemBounds.height + arrowSize) / 2;
/* 1116 */         gc.fillPolygon(new int[] { baseX - xStart, itemBounds.y + yStart, baseX - xStart + arrowSize, itemBounds.y + yStart, baseX - xStart + arrowSize / 2, itemBounds.y + yStart + arrowSize });
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*      */ 
/* 1125 */         int xStart = 12;
/* 1126 */         int arrowSize = 8;
/* 1127 */         int yStart = itemBounds.height - (itemBounds.height + arrowSize) / 2;
/* 1128 */         gc.fillPolygon(new int[] { baseX - xStart, itemBounds.y + yStart, baseX - xStart + arrowSize, itemBounds.y + yStart + 4, baseX - xStart, itemBounds.y + yStart + 8 });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1137 */       gc.setBackground(oldBG);
/* 1138 */       Font headerFont = this.sidebar.getHeaderFont();
/* 1139 */       if ((headerFont != null) && (!headerFont.isDisposed())) {
/* 1140 */         gc.setFont(headerFont);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected Color swt_paintEntryBG(int detail, GC gc, Rectangle drawBounds) {
/* 1146 */     this.neverPainted = false;
/* 1147 */     Color fgText = Colors.black;
/* 1148 */     boolean selected = (detail & 0x2) > 0;
/*      */     
/* 1150 */     boolean hot = (detail & 0x20) > 0;
/* 1151 */     if (selected) {
/* 1152 */       this.attention_start = -1L;
/*      */     }
/* 1154 */     else if ((this.attention_start != -1L) && (this.attention_flash_on)) {
/* 1155 */       selected = true;
/*      */     }
/*      */     
/* 1158 */     if (selected) {
/* 1159 */       if (Constants.isLinux) {
/* 1160 */         gc.fillRectangle(drawBounds.x, drawBounds.y, drawBounds.width, drawBounds.height);
/* 1161 */         fgText = gc.getForeground();
/*      */       }
/*      */       else {
/* 1164 */         Utils.setClipping(gc, (Rectangle)null);
/* 1165 */         if (this.fgSel != null) {
/* 1166 */           fgText = this.fgSel;
/*      */         }
/* 1168 */         if (this.bgSel != null)
/* 1169 */           gc.setBackground(this.bgSel);
/*      */         Color color2;
/*      */         Color color1;
/*      */         Color color2;
/* 1173 */         if (this.sidebar.getTree().isFocusControl()) {
/* 1174 */           Color color1 = ColorCache.getSchemedColor(gc.getDevice(), "#166688");
/* 1175 */           color2 = ColorCache.getSchemedColor(gc.getDevice(), "#1c2458");
/*      */         } else {
/* 1177 */           color1 = ColorCache.getSchemedColor(gc.getDevice(), "#447281");
/* 1178 */           color2 = ColorCache.getSchemedColor(gc.getDevice(), "#393e58");
/*      */         }
/*      */         
/* 1181 */         gc.setBackground(color1);
/* 1182 */         gc.fillRectangle(drawBounds.x, drawBounds.y, drawBounds.width, 4);
/*      */         
/* 1184 */         gc.setForeground(color1);
/* 1185 */         gc.setBackground(color2);
/* 1186 */         Rectangle itemBounds = swt_getBounds();
/* 1187 */         if (itemBounds == null) {
/* 1188 */           return fgText;
/*      */         }
/*      */         
/*      */ 
/* 1192 */         gc.fillGradientRectangle(drawBounds.x, itemBounds.y + 3, drawBounds.width, itemBounds.height - 3, true);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1197 */       if (this.fg != null) {
/* 1198 */         fgText = this.fg;
/*      */       }
/* 1200 */       if (this.bg != null) {
/* 1201 */         gc.setBackground(this.bg);
/*      */       }
/*      */       
/* 1204 */       if ((this == this.sidebar.draggingOver) || (hot)) {
/* 1205 */         Color c = this.skin.getSkinProperties().getColor("color.sidebar.drag.bg");
/* 1206 */         gc.setBackground(c);
/*      */       }
/*      */       
/* 1209 */       gc.fillRectangle(drawBounds);
/*      */       
/* 1211 */       if (this == this.sidebar.draggingOver) {
/* 1212 */         Color c = this.skin.getSkinProperties().getColor("color.sidebar.drag.fg");
/* 1213 */         gc.setForeground(c);
/* 1214 */         gc.setLineWidth(5);
/* 1215 */         gc.drawRectangle(drawBounds);
/*      */       }
/*      */     }
/* 1218 */     return fgText;
/*      */   }
/*      */   
/*      */   public void widgetDisposed(DisposeEvent e) {
/* 1222 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 1223 */     imageLoader.releaseImage("image.sidebar.closeitem");
/* 1224 */     imageLoader.releaseImage("image.sidebar.closeitem-selected");
/*      */     
/* 1226 */     setDisposed(true);
/*      */     
/* 1228 */     TreeItem treeItem = (TreeItem)e.widget;
/* 1229 */     if (treeItem != this.swtItem) {
/* 1230 */       Debug.out("Warning: TreeItem changed for sidebar " + this.id);
/* 1231 */       return;
/*      */     }
/*      */     
/* 1234 */     if (this.swtItem == null) {
/* 1235 */       return;
/*      */     }
/*      */     
/* 1238 */     if ((this.swtItem != null) && (!Constants.isOSX))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1243 */       TreeItem[] children = this.swtItem.getItems();
/* 1244 */       for (TreeItem child : children) {
/* 1245 */         if (!child.isDisposed())
/*      */         {
/*      */ 
/* 1248 */           MdiEntry entry = (MdiEntry)child.getData("MdiEntry");
/* 1249 */           if (entry != null) {
/* 1250 */             entry.close(true);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1255 */     final Tree tree = this.sidebar.getTree();
/*      */     
/*      */ 
/*      */ 
/* 1259 */     if ((tree.isDisposed()) || ((this.swtItem != null) && (this.swtItem.isDisposed())) || (tree.getShell().isDisposed())) {
/* 1260 */       return;
/*      */     }
/*      */     
/* 1263 */     setTreeItem(null);
/*      */     
/* 1265 */     this.mdi.removeItem(this);
/*      */     
/* 1267 */     boolean user = !SWTThread.getInstance().isTerminated();
/* 1268 */     if (user)
/*      */     {
/*      */ 
/* 1271 */       String parentID = getParentID();
/* 1272 */       if (parentID != null) {
/* 1273 */         MdiEntry entry = this.mdi.getEntry(parentID);
/* 1274 */         if ((entry != null) && (entry.isDisposed())) {
/* 1275 */           user = false;
/*      */         }
/*      */       }
/*      */     }
/* 1279 */     triggerCloseListeners(user);
/*      */     
/* 1281 */     SWTSkinObject so = getSkinObject();
/* 1282 */     if (so != null) {
/* 1283 */       setSkinObjectMaster(null);
/* 1284 */       so.getSkin().removeSkinObject(so);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1290 */     Utils.execSWTThreadLater(0, new AERunnable()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/* 1297 */         if (tree.isDisposed()) {
/* 1298 */           return;
/*      */         }
/*      */         try
/*      */         {
/* 1302 */           COConfigurationManager.removeParameter("SideBar.AutoOpen." + SideBarEntrySWT.this.id);
/*      */           
/*      */ 
/*      */ 
/* 1306 */           if ((Constants.isOSX) && (!tree.isDisposed()) && (tree.getSelectionCount() == 0))
/*      */           {
/*      */ 
/* 1309 */             String parentid = SideBarEntrySWT.this.getParentID();
/* 1310 */             if ((parentid != null) && (SideBarEntrySWT.this.mdi.getEntry(parentid) != null)) {
/* 1311 */               SideBarEntrySWT.this.mdi.showEntryByID(parentid);
/*      */             } else {
/* 1313 */               SideBarEntrySWT.this.mdi.showEntryByID("Library");
/*      */             }
/*      */           }
/*      */         } catch (Exception e2) {
/* 1317 */           Debug.out(e2);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1324 */         boolean replaced = false;
/*      */         
/* 1326 */         String my_id = SideBarEntrySWT.this.getId();
/*      */         
/* 1328 */         if (my_id != null)
/*      */         {
/* 1330 */           MdiEntry entry = SideBarEntrySWT.this.mdi.getEntry(my_id);
/*      */           
/* 1332 */           if ((entry != null) && (entry != SideBarEntrySWT.this))
/*      */           {
/* 1334 */             replaced = true;
/*      */           }
/*      */         }
/*      */         
/* 1338 */         if (!replaced)
/*      */         {
/* 1340 */           SideBarEntrySWT.this.mdi.removeEntryAutoOpen(SideBarEntrySWT.this.id);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public void setParentSkinObject(SWTSkinObjectContainer soParent) {
/* 1347 */     this.soParent = soParent;
/*      */   }
/*      */   
/*      */   public SWTSkinObjectContainer getParentSkinObject() {
/* 1351 */     return this.soParent;
/*      */   }
/*      */   
/*      */   public void setSelectable(boolean selectable) {
/* 1355 */     this.selectable = selectable;
/* 1356 */     updateColors();
/*      */   }
/*      */   
/*      */   public boolean isSelectable() {
/* 1360 */     return this.selectable;
/*      */   }
/*      */   
/*      */   public boolean swt_isVisible() {
/* 1364 */     TreeItem parentItem = this.swtItem.getParentItem();
/* 1365 */     if (parentItem != null) {
/* 1366 */       MdiEntry parentEntry = (MdiEntry)parentItem.getData("MdiEntry");
/* 1367 */       if (!parentEntry.isExpanded()) {
/* 1368 */         return false;
/*      */       }
/*      */     }
/* 1371 */     return true;
/*      */   }
/*      */   
/*      */   public Image obfusticatedImage(Image image)
/*      */   {
/* 1376 */     Rectangle bounds = swt_getBounds();
/* 1377 */     if (bounds != null) {
/* 1378 */       TreeItem treeItem = getTreeItem();
/* 1379 */       Point location = Utils.getLocationRelativeToShell(treeItem.getParent());
/*      */       
/* 1381 */       bounds.x += location.x;
/* 1382 */       bounds.y += location.y;
/*      */       
/* 1384 */       Map<String, Object> map = new HashMap();
/* 1385 */       map.put("image", image);
/* 1386 */       map.put("obfuscateTitle", Boolean.valueOf(false));
/* 1387 */       triggerEvent(9, map);
/*      */       
/* 1389 */       if ((this.viewTitleInfo instanceof ObfusticateImage)) {
/* 1390 */         ((ObfusticateImage)this.viewTitleInfo).obfusticatedImage(image);
/*      */       }
/*      */       
/* 1393 */       int ofs = this.IMAGELEFT_GAP + this.IMAGELEFT_SIZE;
/* 1394 */       if (treeItem.getParentItem() != null) {
/* 1395 */         ofs += 12;
/*      */       }
/* 1397 */       bounds.x += ofs;
/* 1398 */       bounds.width -= ofs + 2 + 1;
/* 1399 */       bounds.height -= 1;
/*      */       
/* 1401 */       if ((this.viewTitleInfo instanceof ObfusticateTab)) {
/* 1402 */         String header = ((ObfusticateTab)this.viewTitleInfo).getObfusticatedHeader();
/* 1403 */         if (header != null) {
/* 1404 */           UIDebugGenerator.obfusticateArea(image, bounds, header);
/*      */         }
/*      */       }
/*      */       
/* 1408 */       if (MapUtils.getMapBoolean(map, "obfuscateTitle", false)) {
/* 1409 */         UIDebugGenerator.obfusticateArea(image, bounds);
/*      */       }
/*      */     }
/*      */     
/* 1413 */     return image;
/*      */   }
/*      */   
/*      */   protected void setToolbarVisibility(boolean visible)
/*      */   {
/* 1418 */     if (this.toolBarInfoBar != null) {
/* 1419 */       if (visible) {
/* 1420 */         this.toolBarInfoBar.show();
/*      */       } else {
/* 1422 */         this.toolBarInfoBar.hide(false);
/*      */       }
/* 1424 */       return;
/*      */     }
/* 1426 */     SWTSkinObject soMaster = getSkinObjectMaster();
/* 1427 */     if (soMaster == null) {
/* 1428 */       return;
/*      */     }
/* 1430 */     SWTSkinObject so = getSkinObject();
/* 1431 */     if (so == null) {
/* 1432 */       return;
/*      */     }
/* 1434 */     SWTSkinObject soToolbar = this.skin.getSkinObject("view-toolbar", soMaster);
/* 1435 */     if ((soToolbar == null) && (visible)) {
/* 1436 */       this.toolBarInfoBar = new InfoBarUtil(so, "mdientry.toolbar.full", true, "", "") {
/*      */         public boolean allowShow() {
/* 1438 */           return true;
/*      */         }
/*      */       };
/* 1441 */     } else if (soToolbar != null) {
/* 1442 */       soToolbar.setVisible(visible);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTitle(String title)
/*      */   {
/* 1451 */     super.setTitle(title);
/*      */     
/* 1453 */     refreshTitle();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void refreshTitle()
/*      */   {
/* 1460 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport()
/*      */       {
/* 1464 */         if ((SideBarEntrySWT.this.swtItem == null) || (SideBarEntrySWT.this.swtItem.isDisposed())) {
/* 1465 */           return;
/*      */         }
/* 1467 */         String title = SideBarEntrySWT.this.getTitle();
/*      */         
/* 1469 */         if (!SideBarEntrySWT.this.swtItem.getText().equals(title))
/*      */         {
/* 1471 */           SideBarEntrySWT.this.swtItem.setText(title);
/*      */           
/* 1473 */           SideBarEntrySWT.this.redraw();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/sidebar/SideBarEntrySWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */