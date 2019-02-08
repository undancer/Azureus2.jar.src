/*      */ package com.aelitis.azureus.ui.swt.views.skin.sidebar;
/*      */ 
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*      */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImage;
/*      */ import com.aelitis.azureus.ui.swt.mdi.BaseMDI;
/*      */ import com.aelitis.azureus.ui.swt.mdi.BaseMdiEntry;
/*      */ import com.aelitis.azureus.ui.swt.mdi.MdiEntrySWT;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility.ButtonListenerAdapter;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectSash;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinProperties;
/*      */ import com.aelitis.azureus.ui.swt.utils.FontUtils;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.SkinnedDialog;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Stack;
/*      */ import org.eclipse.swt.dnd.DropTarget;
/*      */ import org.eclipse.swt.dnd.DropTargetEvent;
/*      */ import org.eclipse.swt.events.MenuEvent;
/*      */ import org.eclipse.swt.events.MenuListener;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.events.SelectionListener;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.Font;
/*      */ import org.eclipse.swt.graphics.FontData;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.FormData;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.Tree;
/*      */ import org.eclipse.swt.widgets.TreeItem;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*      */ import org.gudy.azureus2.ui.swt.URLTransfer;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
/*      */ import org.gudy.azureus2.ui.swt.plugins.PluginUISWTSkinObject;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTInstanceImpl;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCore;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewEventListenerHolder;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewImpl;
/*      */ 
/*      */ public class SideBar extends BaseMDI implements org.gudy.azureus2.ui.swt.debug.ObfusticateImage, AEDiagnosticsEvidenceGenerator
/*      */ {
/*   75 */   protected static final boolean isGTK3 = (Utils.isGTK) && (System.getProperty("org.eclipse.swt.internal.gtk.version", "2").startsWith("3"));
/*      */   
/*      */ 
/*   78 */   protected static final boolean END_INDENT = (Constants.isLinux) || (Constants.isWindows2000) || (Constants.isWindows9598ME);
/*      */   
/*      */ 
/*   81 */   private static final boolean USE_PAINTITEM = !Utils.isCarbon;
/*      */   
/*      */ 
/*      */ 
/*   85 */   private static final boolean USE_PAINT = (!Constants.isWindows) && (!Utils.isGTK);
/*      */   
/*   87 */   protected static final boolean USE_NATIVE_EXPANDER = Utils.isGTK;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   93 */   private static final int GAP_BETWEEN_LEVEL_1 = Math.min(5, Math.max(0, COConfigurationManager.getIntParameter("Side Bar Top Level Gap", 1)));
/*      */   
/*      */   protected static final int SIDEBAR_ATTENTION_PERIOD = 500;
/*      */   
/*      */   protected static final int SIDEBAR_ATTENTION_DURATION = 5000;
/*      */   
/*      */   private SWTSkin skin;
/*      */   
/*      */   private SWTSkinObjectContainer soSideBarContents;
/*      */   
/*      */   private SWTSkinObject soSideBarList;
/*      */   
/*      */   private Tree tree;
/*      */   
/*      */   private Font fontHeader;
/*      */   
/*      */   private Font font;
/*      */   
/*      */   private SWTSkinObject soSideBarPopout;
/*      */   
/*      */   private SelectionListener dropDownSelectionListener;
/*      */   
/*      */   private DropTarget dropTarget;
/*      */   
/*      */   protected SideBarEntrySWT draggingOver;
/*      */   
/*      */   private Color fg;
/*      */   
/*      */   private Color bg;
/*      */   
/*  123 */   private List<SideBarEntrySWT> attention_seekers = new ArrayList();
/*      */   
/*      */   private TimerEventPeriodic attention_event;
/*      */   
/*      */   private Composite cPluginsArea;
/*  128 */   public static SideBar instance = null;
/*      */   
/*  130 */   private List<UISWTViewCore> pluginViews = new ArrayList();
/*      */   
/*      */ 
/*      */   public SideBar()
/*      */   {
/*  135 */     if (instance == null) {
/*  136 */       instance = this;
/*      */     }
/*  138 */     org.gudy.azureus2.core3.util.AEDiagnostics.addEvidenceGenerator(this);
/*      */   }
/*      */   
/*      */   public Object skinObjectCreated(SWTSkinObject skinObject, Object params)
/*      */   {
/*  143 */     super.skinObjectCreated(skinObject, params);
/*      */     
/*  145 */     this.skin = skinObject.getSkin();
/*      */     
/*  147 */     this.soSideBarContents = ((SWTSkinObjectContainer)this.skin.getSkinObject("sidebar-contents"));
/*  148 */     this.soSideBarList = this.skin.getSkinObject("sidebar-list");
/*  149 */     this.soSideBarPopout = this.skin.getSkinObject("sidebar-pop");
/*      */     
/*  151 */     SWTSkinObjectContainer soSideBarPluginsArea = (SWTSkinObjectContainer)this.skin.getSkinObject("sidebar-plugins");
/*  152 */     if (soSideBarPluginsArea != null) {
/*  153 */       Composite composite = soSideBarPluginsArea.getComposite();
/*  154 */       this.cPluginsArea = new Composite(composite, 0);
/*  155 */       GridLayout layout = new GridLayout();
/*  156 */       layout.marginHeight = (layout.marginWidth = 0);
/*  157 */       layout.verticalSpacing = (layout.horizontalSpacing = 0);
/*  158 */       this.cPluginsArea.setLayout(layout);
/*  159 */       this.cPluginsArea.setLayoutData(Utils.getFilledFormData());
/*      */     }
/*      */     
/*  162 */     addGeneralMenus();
/*      */     
/*  164 */     createSideBar();
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  169 */       UIUpdater updater = UIFunctionsManager.getUIFunctions().getUIUpdater();
/*      */       
/*  171 */       if (!updater.isAdded(this))
/*      */       {
/*  173 */         updater.addUpdater(this);
/*      */       }
/*      */     } catch (Throwable e) {
/*  176 */       Debug.out(e);
/*      */     }
/*      */     
/*  179 */     Display.getDefault().addFilter(1, new Listener()
/*      */     {
/*      */ 
/*      */       public void handleEvent(Event event)
/*      */       {
/*  184 */         if ((event.keyCode == 16777234) || (event.keyCode == 16777232) || ((event.keyCode == 116) && (event.stateMask == 4259840)))
/*      */         {
/*      */ 
/*  187 */           event.doit = false;
/*  188 */           event.keyCode = 0;
/*  189 */           event.character = '\000';
/*  190 */           SideBar.this.flipSideBarVisibility();
/*  191 */         } else if ((event.keyCode == 16777229) && (event.stateMask == 262144)) {
/*  192 */           MdiEntry entry = SideBar.this.getCurrentEntry();
/*      */           
/*  194 */           if (((entry instanceof SideBarEntrySWT)) && (entry.isCloseable()))
/*      */           {
/*  196 */             ((SideBarEntrySWT)entry).getTreeItem().dispose();
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*  201 */     });
/*  202 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addGeneralMenus()
/*      */   {
/*  213 */     PluginManager pm = com.aelitis.azureus.core.AzureusCoreFactory.getSingleton().getPluginManager();
/*  214 */     PluginInterface pi = pm.getDefaultPluginInterface();
/*  215 */     UIManager uim = pi.getUIManager();
/*  216 */     MenuManager menuManager = uim.getMenuManager();
/*  217 */     org.gudy.azureus2.plugins.ui.menus.MenuItem menuItem = menuManager.addMenuItem("sidebar._end_", "menu.pop.out");
/*      */     
/*  219 */     menuItem.addFillListener(new org.gudy.azureus2.plugins.ui.menus.MenuItemFillListener()
/*      */     {
/*      */       public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data)
/*      */       {
/*  223 */         SideBarEntrySWT sbe = (SideBarEntrySWT)SideBar.this.currentEntry;
/*      */         
/*  225 */         menu.setVisible((sbe != null) && (sbe.canBuildStandAlone()));
/*      */       }
/*      */       
/*  228 */     });
/*  229 */     menuItem.addListener(new org.gudy.azureus2.plugins.ui.menus.MenuItemListener() {
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/*  231 */         SideBarEntrySWT sbe = (SideBarEntrySWT)SideBar.this.currentEntry;
/*      */         
/*  233 */         if (sbe != null) {
/*  234 */           SkinnedDialog skinnedDialog = new SkinnedDialog("skin3_dlg_sidebar_popout", "shell", null, 3184);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  241 */           SWTSkin skin = skinnedDialog.getSkin();
/*      */           
/*  243 */           SWTSkinObjectContainer cont = sbe.buildStandAlone((SWTSkinObjectContainer)skin.getSkinObject("content-area"));
/*      */           
/*  245 */           if (cont != null)
/*      */           {
/*  247 */             skinnedDialog.setTitle(sbe.getTitle());
/*      */             
/*  249 */             skinnedDialog.open();
/*      */           }
/*      */           else
/*      */           {
/*  253 */             skinnedDialog.close();
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void flipSideBarVisibility()
/*      */   {
/*  268 */     final SWTSkinObjectSash soSash = (SWTSkinObjectSash)this.skin.getSkinObject("sidebar-sash");
/*  269 */     if (soSash == null) {
/*  270 */       return;
/*      */     }
/*  272 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*      */       public void runSupport() {
/*  274 */         boolean visible = !soSash.isAboveVisible();
/*      */         
/*  276 */         soSash.setAboveVisible(visible);
/*  277 */         SideBar.this.updateSidebarVisibility();
/*      */         
/*  279 */         COConfigurationManager.setParameter("Show Side Bar", visible);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private void updateSidebarVisibility() {
/*  285 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  287 */         SWTSkinObjectSash soSash = (SWTSkinObjectSash)SideBar.this.skin.getSkinObject("sidebar-sash");
/*  288 */         if (soSash == null) {
/*  289 */           return;
/*      */         }
/*  291 */         if (soSash.isAboveVisible()) {
/*  292 */           if (SideBar.this.soSideBarPopout != null) {
/*  293 */             Object ld = SideBar.this.soSideBarPopout.getControl().getLayoutData();
/*  294 */             if ((ld instanceof FormData)) {
/*  295 */               FormData fd = (FormData)ld;
/*  296 */               fd.width = 0;
/*      */             }
/*  298 */             SideBar.this.soSideBarPopout.setVisible(false);
/*      */             
/*  300 */             Utils.relayout(SideBar.this.soSideBarPopout.getControl());
/*      */           }
/*      */         }
/*  303 */         else if (SideBar.this.soSideBarPopout != null) {
/*  304 */           Object ld = SideBar.this.soSideBarPopout.getControl().getLayoutData();
/*  305 */           if ((ld instanceof FormData)) {
/*  306 */             FormData fd = (FormData)ld;
/*  307 */             fd.width = 24;
/*      */           }
/*  309 */           SideBar.this.soSideBarPopout.setVisible(true);
/*  310 */           SideBar.this.soSideBarPopout.getControl().moveAbove(null);
/*  311 */           Utils.relayout(SideBar.this.soSideBarPopout.getControl());
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public boolean isVisible()
/*      */   {
/*  319 */     SWTSkinObjectSash soSash = (SWTSkinObjectSash)this.skin.getSkinObject("sidebar-sash");
/*  320 */     if (soSash == null) {
/*  321 */       return false;
/*      */     }
/*  323 */     return soSash.isAboveVisible();
/*      */   }
/*      */   
/*      */ 
/*      */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*      */   {
/*  329 */     super.skinObjectInitialShow(skinObject, params);
/*      */     
/*  331 */     COConfigurationManager.addParameterListener("Show Side Bar", new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/*  339 */         boolean visible = COConfigurationManager.getBooleanParameter(name);
/*      */         
/*  341 */         if (visible != SideBar.this.isVisible())
/*      */         {
/*  343 */           SideBar.this.flipSideBarVisibility();
/*      */         }
/*      */         
/*      */       }
/*  347 */     });
/*  348 */     updateSidebarVisibility();
/*      */     
/*  350 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setupPluginViews()
/*      */   {
/*  358 */     super.setupPluginViews();
/*  359 */     createSideBarPluginViews();
/*      */   }
/*      */   
/*      */   public Object skinObjectDestroyed(SWTSkinObject skinObject, Object params) {
/*      */     try {
/*  364 */       UIFunctionsManager.getUIFunctions().getUIUpdater().removeUpdater(this);
/*      */     } catch (Exception e) {
/*  366 */       Debug.out(e);
/*      */     }
/*      */     
/*  369 */     return super.skinObjectDestroyed(skinObject, params);
/*      */   }
/*      */   
/*      */   private void createSideBar() {
/*  373 */     if (this.soSideBarList == null) {
/*  374 */       return;
/*      */     }
/*  376 */     Composite parent = (Composite)this.soSideBarList.getControl();
/*      */     
/*  378 */     this.tree = new Tree(parent, 536936976);
/*      */     
/*  380 */     this.tree.setHeaderVisible(false);
/*      */     
/*  382 */     new SideBarToolTips(this, this.tree);
/*      */     
/*  384 */     this.tree.setLayoutData(Utils.getFilledFormData());
/*      */     
/*  386 */     SWTSkinProperties skinProperties = this.skin.getSkinProperties();
/*  387 */     this.bg = skinProperties.getColor("color.sidebar.bg");
/*  388 */     this.fg = skinProperties.getColor("color.sidebar.fg");
/*      */     
/*  390 */     COConfigurationManager.addParameterListener("config.skin.color.sidebar.bg", new ParameterListener()
/*      */     {
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*  396 */         Utils.execSWTThread(new Runnable()
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/*      */ 
/*  402 */             SideBar.this.swt_updateSideBarColors();
/*      */           }
/*      */           
/*      */         });
/*      */       }
/*  407 */     });
/*  408 */     this.tree.setBackground(this.bg);
/*  409 */     this.tree.setForeground(this.fg);
/*  410 */     FontData[] fontData = this.tree.getFont().getFontData();
/*      */     
/*      */     int fontHeight;
/*      */     int fontHeight;
/*  414 */     if (isGTK3) {
/*  415 */       fontHeight = fontData[0].getHeight();
/*      */     } else {
/*  417 */       fontHeight = (Constants.isOSX ? 11 : 12) + (this.tree.getItemHeight() > 18 ? this.tree.getItemHeight() - 18 : 0);
/*      */       
/*      */ 
/*  420 */       if ((Constants.isLinux) && (this.tree.getItemHeight() >= 38)) {
/*  421 */         fontHeight = 13;
/*      */       }
/*      */     }
/*      */     
/*  425 */     fontData[0].setStyle(1);
/*  426 */     FontUtils.getFontHeightFromPX(this.tree.getDisplay(), fontData, null, fontHeight);
/*  427 */     this.fontHeader = new Font(this.tree.getDisplay(), fontData);
/*  428 */     this.font = FontUtils.getFontWithHeight(this.tree.getFont(), null, fontHeight);
/*      */     
/*  430 */     this.tree.setFont(this.font);
/*      */     
/*      */ 
/*      */ 
/*  434 */     this.tree.getVerticalBar().addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */       public void widgetSelected(SelectionEvent e)
/*      */       {
/*  438 */         if (e.detail == 0) {
/*  439 */           SideBarEntrySWT[] sideBarEntries = (SideBarEntrySWT[])SideBar.this.getEntries(new SideBarEntrySWT[0]);
/*  440 */           SideBar.this.swt_updateSideBarHitAreasY(sideBarEntries);
/*      */         }
/*      */         
/*      */       }
/*  444 */     });
/*  445 */     Listener treeListener = new Listener() {
/*  446 */       TreeItem lastTopItem = null;
/*      */       
/*  448 */       boolean mouseDowned = false;
/*      */       
/*  450 */       Rectangle lastCloseAreaClicked = null;
/*      */       private boolean wasExpanded;
/*      */       
/*      */       public void handleEvent(final Event event)
/*      */       {
/*  455 */         TreeItem treeItem = (TreeItem)event.item;
/*  456 */         Tree tree = SideBar.this.getTree();
/*      */         try
/*      */         {
/*  459 */           switch (event.type) {
/*      */           case 41: 
/*  461 */             int clientWidth = tree.getClientArea().width;
/*  462 */             String text = treeItem.getText(event.index);
/*  463 */             Point size = event.gc.textExtent(text);
/*  464 */             if (event.x + event.width < clientWidth) {
/*  465 */               event.width = (size.x + event.x);
/*  466 */               event.x = 0;
/*      */             }
/*      */             
/*  469 */             if (Constants.isWindows) {
/*  470 */               event.width = (clientWidth - event.x);
/*      */             }
/*      */             
/*  473 */             event.height = 20;
/*      */             
/*  475 */             break;
/*      */           
/*      */           case 42: 
/*  478 */             if (SideBar.USE_PAINTITEM) {
/*  479 */               SideBarEntrySWT entry = (SideBarEntrySWT)treeItem.getData("MdiEntry");
/*      */               
/*  481 */               if (entry != null) {
/*  482 */                 boolean selected = (SideBar.this.currentEntry == entry) && (entry.isSelectable());
/*      */                 
/*      */ 
/*  485 */                 if (!selected) {
/*  486 */                   event.detail &= 0xFFFFFFFD;
/*      */                 } else {
/*  488 */                   event.detail |= 0x2;
/*      */                 }
/*  490 */                 entry.swt_paintSideBar(event);
/*      */               } }
/*  492 */             break;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           case 9: 
/*  498 */             if (!SideBar.USE_PAINT) {
/*  499 */               return;
/*      */             }
/*  501 */             Rectangle bounds = event.getBounds();
/*  502 */             int indent = SideBar.END_INDENT ? tree.getClientArea().width - 1 : 0;
/*  503 */             int y = event.y + 1;
/*  504 */             treeItem = tree.getItem(new Point(indent, y));
/*      */             
/*  506 */             while (treeItem != null) {
/*  507 */               SideBarEntrySWT entry = (SideBarEntrySWT)treeItem.getData("MdiEntry");
/*  508 */               Rectangle itemBounds = entry == null ? null : entry.swt_getBounds();
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  513 */               if ((itemBounds != null) && (entry != null)) {
/*  514 */                 event.item = treeItem;
/*      */                 
/*  516 */                 boolean selected = (SideBar.this.currentEntry == entry) && (entry.isSelectable());
/*      */                 
/*  518 */                 event.detail = (selected ? 2 : 0);
/*      */                 
/*  520 */                 Rectangle newClip = bounds.intersection(itemBounds);
/*      */                 
/*  522 */                 event.setBounds(newClip);
/*  523 */                 Utils.setClipping(event.gc, newClip);
/*      */                 
/*  525 */                 entry.swt_paintSideBar(event);
/*      */                 
/*  527 */                 y = itemBounds.y + itemBounds.height + 1;
/*      */               } else {
/*  529 */                 y += tree.getItemHeight();
/*      */               }
/*      */               
/*  532 */               if (y > bounds.y + bounds.height) {
/*      */                 break;
/*      */               }
/*  535 */               TreeItem oldTreeItem = treeItem;
/*  536 */               treeItem = tree.getItem(new Point(indent, y));
/*  537 */               if (oldTreeItem == treeItem) {
/*      */                 break;
/*      */               }
/*      */             }
/*      */             
/*  542 */             if (tree.getTopItem() != this.lastTopItem) {
/*  543 */               this.lastTopItem = tree.getTopItem();
/*  544 */               SideBarEntrySWT[] sideBarEntries = (SideBarEntrySWT[])SideBar.this.getEntries(new SideBarEntrySWT[0]);
/*  545 */               SideBar.this.swt_updateSideBarHitAreasY(sideBarEntries); }
/*  546 */             break;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           case 40: 
/*  552 */             SideBarEntrySWT entry = (SideBarEntrySWT)treeItem.getData("MdiEntry");
/*  553 */             if (entry == null) {
/*  554 */               event.detail = 0;
/*      */             }
/*      */             
/*      */ 
/*  558 */             event.doit = true;
/*  559 */             break;
/*      */           
/*      */ 
/*      */           case 11: 
/*  563 */             tree.redraw();
/*  564 */             break;
/*      */           
/*      */ 
/*      */           case 13: 
/*  568 */             if (treeItem == null) {
/*  569 */               return;
/*      */             }
/*  571 */             SideBarEntrySWT entry = (SideBarEntrySWT)treeItem.getData("MdiEntry");
/*  572 */             if ((entry != null) && (entry.isSelectable())) {
/*  573 */               Point cursorLocation = tree.toControl(event.display.getCursorLocation());
/*  574 */               if ((this.lastCloseAreaClicked != null) && (this.lastCloseAreaClicked.contains(cursorLocation.x, cursorLocation.y))) {
/*  575 */                 return;
/*      */               }
/*      */               
/*  578 */               SideBar.this.showEntry(entry);
/*  579 */             } else if (SideBar.this.currentEntry != null) {
/*  580 */               TreeItem topItem = tree.getTopItem();
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  585 */               tree.setRedraw(false);
/*  586 */               TreeItem ti = ((SideBarEntrySWT)SideBar.this.currentEntry).getTreeItem();
/*  587 */               if (ti != null) {
/*  588 */                 tree.setSelection(ti);
/*      */               }
/*      */               
/*  591 */               tree.setTopItem(topItem);
/*  592 */               tree.setRedraw(true);
/*      */               
/*  594 */               event.doit = false; }
/*  595 */             break;
/*      */           
/*      */ 
/*      */ 
/*      */           case 5: 
/*  600 */             int indent = SideBar.END_INDENT ? tree.getClientArea().width - 1 : 0;
/*  601 */             treeItem = tree.getItem(new Point(indent, event.y));
/*  602 */             SideBarEntrySWT entry = (SideBarEntrySWT)((treeItem == null) || (treeItem.isDisposed()) ? null : treeItem.getData("MdiEntry"));
/*      */             
/*      */ 
/*  605 */             int cursorNo = 0;
/*  606 */             if (treeItem != null) {
/*  607 */               Rectangle closeArea = (Rectangle)treeItem.getData("closeArea");
/*  608 */               if ((closeArea != null) && (closeArea.contains(event.x, event.y))) {
/*  609 */                 cursorNo = 21;
/*  610 */               } else if ((entry != null) && (!entry.isCollapseDisabled()) && (treeItem.getItemCount() > 0))
/*      */               {
/*  612 */                 cursorNo = 21;
/*      */               }
/*      */             }
/*      */             
/*  616 */             org.eclipse.swt.graphics.Cursor cursor = event.display.getSystemCursor(cursorNo);
/*  617 */             if (tree.getCursor() != cursor) {
/*  618 */               tree.setCursor(cursor);
/*      */             }
/*      */             
/*  621 */             if (treeItem != null) {
/*  622 */               this.wasExpanded = ((entry != null) && (entry.isExpanded()));
/*      */             } else {
/*  624 */               this.wasExpanded = false;
/*      */             }
/*  626 */             break;
/*      */           
/*      */ 
/*      */           case 3: 
/*  630 */             this.mouseDowned = true;
/*  631 */             this.lastCloseAreaClicked = null;
/*  632 */             if ((tree.getItemCount() == 0) || (event.button != 1)) {
/*  633 */               return;
/*      */             }
/*  635 */             int indent = SideBar.END_INDENT ? tree.getClientArea().width - 1 : 0;
/*  636 */             treeItem = tree.getItem(new Point(indent, event.y));
/*  637 */             if (treeItem == null) {
/*  638 */               return;
/*      */             }
/*  640 */             Rectangle closeArea = (Rectangle)treeItem.getData("closeArea");
/*  641 */             if ((closeArea != null) && (closeArea.contains(event.x, event.y))) {
/*  642 */               this.lastCloseAreaClicked = closeArea;
/*  643 */               treeItem.dispose();
/*      */               
/*  645 */               this.mouseDowned = false;
/*      */             }
/*      */             
/*      */ 
/*      */             break;
/*      */           case 4: 
/*  651 */             if (!this.mouseDowned) {
/*  652 */               return;
/*      */             }
/*  654 */             this.mouseDowned = false;
/*  655 */             if ((tree.getItemCount() == 0) || (event.button != 1)) {
/*  656 */               return;
/*      */             }
/*  658 */             int indent = SideBar.END_INDENT ? tree.getClientArea().width - 1 : 0;
/*  659 */             treeItem = tree.getItem(new Point(indent, event.y));
/*  660 */             if (treeItem == null) {
/*  661 */               return;
/*      */             }
/*  663 */             SideBarEntrySWT entry = (SideBarEntrySWT)treeItem.getData("MdiEntry");
/*      */             
/*  665 */             Rectangle closeArea = (Rectangle)treeItem.getData("closeArea");
/*  666 */             if ((closeArea != null) && (closeArea.contains(event.x, event.y)))
/*      */             {
/*  668 */               return; }
/*  669 */             if ((SideBar.this.currentEntry != entry) && (Constants.isOSX)) {
/*  670 */               SideBar.this.showEntry(entry);
/*      */             }
/*      */             
/*  673 */             if (entry != null) {
/*  674 */               MdiEntryVitalityImage[] vitalityImages = entry.getVitalityImages();
/*  675 */               for (int i = 0; i < vitalityImages.length; i++) {
/*  676 */                 SideBarVitalityImageSWT vitalityImage = (SideBarVitalityImageSWT)vitalityImages[i];
/*  677 */                 if ((vitalityImage != null) && (vitalityImage.isVisible()))
/*      */                 {
/*      */ 
/*  680 */                   Rectangle hitArea = vitalityImage.getHitArea();
/*  681 */                   if (hitArea != null)
/*      */                   {
/*      */ 
/*      */ 
/*  685 */                     Rectangle itemBounds = entry.swt_getBounds();
/*  686 */                     int relY = event.y - (itemBounds == null ? 0 : itemBounds.y);
/*      */                     
/*  688 */                     if (hitArea.contains(event.x, relY)) {
/*  689 */                       vitalityImage.triggerClickedListeners(event.x, relY);
/*  690 */                       return;
/*      */                     }
/*      */                   }
/*      */                 } }
/*  694 */               if ((!entry.isCollapseDisabled()) && (treeItem.getItemCount() > 0) && (
/*  695 */                 (!entry.isSelectable()) || (event.x < 20)))
/*      */               {
/*  697 */                 MdiEntry currentEntry = SideBar.this.getCurrentEntry();
/*  698 */                 if ((currentEntry != null) && (entry.getId().equals(currentEntry.getParentID())))
/*      */                 {
/*  700 */                   SideBar.this.showEntryByID("Library");
/*      */                 }
/*  702 */                 entry.setExpanded(!this.wasExpanded);
/*  703 */                 this.wasExpanded = (!this.wasExpanded);
/*      */               }
/*      */             }
/*  706 */             break;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           case 12: 
/*  712 */             SideBar.this.fontHeader.dispose();
/*  713 */             SideBar.this.font.dispose();
/*  714 */             if ((SideBar.this.dropTarget != null) && (!SideBar.this.dropTarget.isDisposed())) {
/*  715 */               SideBar.this.dropTarget.dispose();
/*      */             }
/*  717 */             SideBar.this.saveCloseables();
/*      */             
/*  719 */             break;
/*      */           
/*      */ 
/*      */           case 18: 
/*  723 */             SideBarEntrySWT entry = (SideBarEntrySWT)treeItem.getData("MdiEntry");
/*      */             
/*  725 */             if (entry.isCollapseDisabled()) {
/*  726 */               tree.setRedraw(false);
/*  727 */               Display.getDefault().asyncExec(new Runnable() {
/*      */                 public void run() {
/*  729 */                   ((TreeItem)event.item).setExpanded(true);
/*  730 */                   SideBar.this.getTree().setRedraw(true);
/*      */                 }
/*      */               });
/*      */             } else {
/*  734 */               MdiEntry currentEntry = SideBar.this.getCurrentEntry();
/*  735 */               if ((currentEntry != null) && (entry.getId().equals(currentEntry.getParentID())))
/*      */               {
/*  737 */                 SideBar.this.showEntryByID("Library");
/*      */               }
/*      */             }
/*      */             break;
/*      */           }
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*  745 */           Debug.out(e);
/*      */         }
/*      */       }
/*  748 */     };
/*  749 */     this.tree.addListener(41, treeListener);
/*  750 */     this.tree.addListener(11, treeListener);
/*  751 */     this.tree.addListener(9, treeListener);
/*  752 */     if (USE_PAINTITEM) {
/*  753 */       this.tree.addListener(42, treeListener);
/*  754 */       this.tree.addListener(40, treeListener);
/*      */     }
/*      */     
/*  757 */     this.tree.addListener(13, treeListener);
/*  758 */     this.tree.addListener(12, treeListener);
/*      */     
/*      */ 
/*  761 */     this.tree.addListener(4, treeListener);
/*  762 */     this.tree.addListener(3, treeListener);
/*      */     
/*      */ 
/*  765 */     this.tree.addListener(5, treeListener);
/*      */     
/*      */ 
/*  768 */     this.tree.addListener(18, treeListener);
/*      */     
/*  770 */     this.dropTarget = new DropTarget(this.tree, 1);
/*  771 */     this.dropTarget.setTransfer(new org.eclipse.swt.dnd.Transfer[] { URLTransfer.getInstance(), org.eclipse.swt.dnd.FileTransfer.getInstance(), org.eclipse.swt.dnd.TextTransfer.getInstance() });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  777 */     this.dropTarget.addDropListener(new org.eclipse.swt.dnd.DropTargetAdapter() {
/*      */       public void dropAccept(DropTargetEvent event) {
/*  779 */         event.currentDataType = URLTransfer.pickBestType(event.dataTypes, event.currentDataType);
/*      */       }
/*      */       
/*      */ 
/*      */       public void dragEnter(DropTargetEvent event) {}
/*      */       
/*      */ 
/*      */       public void dragOperationChanged(DropTargetEvent event) {}
/*      */       
/*      */ 
/*      */       public void dragOver(DropTargetEvent event)
/*      */       {
/*  791 */         TreeItem treeItem = (event.item instanceof TreeItem) ? (TreeItem)event.item : null;
/*      */         
/*      */ 
/*  794 */         if (treeItem != null) {
/*  795 */           SideBarEntrySWT entry = (SideBarEntrySWT)treeItem.getData("MdiEntry");
/*      */           
/*  797 */           SideBar.this.draggingOver = entry;
/*      */         } else {
/*  799 */           SideBar.this.draggingOver = null;
/*      */         }
/*  801 */         if ((SideBar.this.draggingOver == null) || (!SideBar.this.draggingOver.hasDropListeners()))
/*      */         {
/*  803 */           boolean isTorrent = TorrentOpener.doesDropHaveTorrents(event);
/*      */           
/*  805 */           if (isTorrent) {
/*  806 */             event.detail = 1;
/*      */           } else {
/*  808 */             event.detail = 0;
/*      */           }
/*  810 */           SideBar.this.draggingOver = null;
/*  811 */         } else if ((event.operations & 0x4) > 0) {
/*  812 */           event.detail = 4;
/*  813 */         } else if ((event.operations & 0x1) > 0) {
/*  814 */           event.detail = 1;
/*  815 */         } else if ((event.operations & 0x10) > 0) {
/*  816 */           event.detail = 1;
/*      */         }
/*  818 */         if (Constants.isOSX) {
/*  819 */           SideBar.this.tree.redraw();
/*      */         }
/*      */         
/*  822 */         event.feedback = 25;
/*      */       }
/*      */       
/*      */       public void dragLeave(DropTargetEvent event)
/*      */       {
/*  827 */         SideBar.this.draggingOver = null;
/*  828 */         SideBar.this.tree.redraw();
/*      */       }
/*      */       
/*      */       public void drop(DropTargetEvent event) {
/*  832 */         SideBar.this.draggingOver = null;
/*  833 */         SideBar.this.tree.redraw();
/*  834 */         if (!(event.item instanceof TreeItem)) {
/*  835 */           SideBar.this.defaultDrop(event);
/*  836 */           return;
/*      */         }
/*  838 */         TreeItem treeItem = (TreeItem)event.item;
/*      */         
/*  840 */         SideBarEntrySWT entry = (SideBarEntrySWT)treeItem.getData("MdiEntry");
/*      */         
/*  842 */         boolean handled = (entry != null) && (entry.triggerDropListeners(event.data));
/*  843 */         if (!handled) {
/*  844 */           SideBar.this.defaultDrop(event);
/*      */         }
/*      */         
/*      */       }
/*  848 */     });
/*  849 */     final Menu menuTree = new Menu(this.tree);
/*  850 */     this.tree.setMenu(menuTree);
/*      */     
/*  852 */     menuTree.addMenuListener(new MenuListener() {
/*  853 */       boolean bShown = false;
/*      */       
/*      */       public void menuHidden(MenuEvent e) {
/*  856 */         this.bShown = false;
/*      */         
/*  858 */         if (Constants.isOSX) {
/*  859 */           return;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  864 */         Utils.execSWTThreadLater(0, new AERunnable() {
/*      */           public void runSupport() {
/*  866 */             if ((SideBar.11.this.bShown) || (SideBar.11.this.val$menuTree.isDisposed())) {
/*  867 */               return;
/*      */             }
/*  869 */             Utils.disposeSWTObjects(SideBar.11.this.val$menuTree.getItems());
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */       public void menuShown(MenuEvent e) {
/*  875 */         Utils.disposeSWTObjects(menuTree.getItems());
/*      */         
/*  877 */         this.bShown = true;
/*      */         
/*  879 */         Point ptMouse = SideBar.this.tree.toControl(e.display.getCursorLocation());
/*      */         
/*  881 */         int indent = SideBar.END_INDENT ? SideBar.this.tree.getClientArea().width - 1 : 0;
/*  882 */         TreeItem treeItem = SideBar.this.tree.getItem(new Point(indent, ptMouse.y));
/*  883 */         if (treeItem == null) {
/*  884 */           return;
/*      */         }
/*  886 */         SideBarEntrySWT entry = (SideBarEntrySWT)treeItem.getData("MdiEntry");
/*      */         
/*  888 */         SideBar.this.fillMenu(menuTree, entry, "sidebar");
/*      */         
/*  890 */         if (menuTree.getItemCount() == 0) {
/*  891 */           Utils.execSWTThreadLater(0, new AERunnable() {
/*      */             public void runSupport() {
/*  893 */               SideBar.11.this.val$menuTree.setVisible(false);
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     });
/*      */     
/*  900 */     if (this.soSideBarPopout != null) {
/*  901 */       SWTSkinObject soDropDown = this.skin.getSkinObject("sidebar-dropdown");
/*  902 */       if (soDropDown != null)
/*      */       {
/*  904 */         final Menu menuDropDown = new Menu(soDropDown.getControl());
/*      */         
/*  906 */         menuDropDown.addMenuListener(new MenuListener() {
/*  907 */           boolean bShown = false;
/*      */           
/*      */           public void menuHidden(MenuEvent e) {
/*  910 */             this.bShown = false;
/*      */             
/*  912 */             if (Constants.isOSX) {
/*  913 */               return;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*  918 */             Utils.execSWTThreadLater(0, new AERunnable() {
/*      */               public void runSupport() {
/*  920 */                 if ((SideBar.12.this.bShown) || (SideBar.12.this.val$menuDropDown.isDisposed())) {
/*  921 */                   return;
/*      */                 }
/*  923 */                 Utils.disposeSWTObjects(SideBar.12.this.val$menuDropDown.getItems());
/*      */               }
/*      */             });
/*      */           }
/*      */           
/*      */           public void menuShown(MenuEvent e) {
/*  929 */             Utils.disposeSWTObjects(menuDropDown.getItems());
/*      */             
/*  931 */             this.bShown = true;
/*      */             
/*  933 */             SideBar.this.fillDropDownMenu(menuDropDown, SideBar.this.tree.getItems(), 0);
/*      */           }
/*      */           
/*  936 */         });
/*  937 */         this.dropDownSelectionListener = new SelectionListener() {
/*      */           public void widgetSelected(SelectionEvent e) {
/*  939 */             String id = (String)e.widget.getData("Plugin.viewID");
/*  940 */             SideBar.this.showEntryByID(id);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*  946 */         };
/*  947 */         SWTSkinButtonUtility btnDropDown = new SWTSkinButtonUtility(soDropDown);
/*  948 */         btnDropDown.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*      */         {
/*      */           public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask) {
/*  951 */             Control c = buttonUtility.getSkinObject().getControl();
/*  952 */             menuDropDown.setLocation(c.getDisplay().getCursorLocation());
/*  953 */             menuDropDown.setVisible(!menuDropDown.getVisible());
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*  958 */       SWTSkinObject soExpand = this.skin.getSkinObject("sidebar-expand");
/*  959 */       if (soExpand != null) {
/*  960 */         SWTSkinButtonUtility btnExpand = new SWTSkinButtonUtility(soExpand);
/*  961 */         btnExpand.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*      */         {
/*      */           public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask) {
/*  964 */             SideBar.this.flipSideBarVisibility();
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void createSideBarPluginViews()
/*      */   {
/*  973 */     if (this.cPluginsArea == null) {
/*  974 */       return;
/*      */     }
/*  976 */     UISWTInstanceImpl uiSWTinstance = (UISWTInstanceImpl)com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT.getUIFunctionsSWT().getUISWTInstance();
/*      */     
/*  978 */     if (uiSWTinstance == null) {
/*  979 */       return;
/*      */     }
/*      */     
/*  982 */     UISWTViewEventListenerHolder[] pluginViews = uiSWTinstance.getViewListeners("SideBarArea");
/*  983 */     for (UISWTViewEventListenerHolder l : pluginViews) {
/*  984 */       if (l != null) {
/*      */         try {
/*  986 */           UISWTViewImpl view = new UISWTViewImpl(l.getViewID(), "SideBarArea", false);
/*  987 */           view.setEventListener(l, true);
/*  988 */           addSideBarView(view, this.cPluginsArea);
/*  989 */           this.cPluginsArea.getParent().getParent().layout(true, true);
/*      */         } catch (Exception e) {
/*  991 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  997 */     uiSWTinstance.addSWTViewListener(new org.gudy.azureus2.ui.swt.pluginsimpl.UISWTInstanceImpl.SWTViewListener()
/*      */     {
/*      */       public void setViewAdded(final String parent, final String id, final UISWTViewEventListener l)
/*      */       {
/* 1001 */         if (!parent.equals("SideBarArea")) {
/* 1002 */           return;
/*      */         }
/* 1004 */         Utils.execSWTThread(new AERunnable()
/*      */         {
/*      */           public void runSupport() {
/*      */             try {
/* 1008 */               UISWTViewImpl view = new UISWTViewImpl(id, parent, false);
/* 1009 */               view.setEventListener(l, true);
/* 1010 */               SideBar.this.addSideBarView(view, SideBar.this.cPluginsArea);
/*      */             } catch (Exception e) {
/* 1012 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*      */       public void setViewRemoved(String parent, String id, final UISWTViewEventListener l)
/*      */       {
/* 1021 */         if (!parent.equals("SideBarArea")) {
/* 1022 */           return;
/*      */         }
/* 1024 */         Utils.execSWTThread(new AERunnable()
/*      */         {
/*      */           public void runSupport() {
/*      */             try {
/* 1028 */               for (UISWTViewCore view : SideBar.this.pluginViews) {
/* 1029 */                 if (l.equals(view.getEventListener())) {
/* 1030 */                   view.closeView();
/*      */                 }
/* 1032 */                 else if ((l instanceof UISWTViewEventListenerHolder)) {
/* 1033 */                   UISWTViewEventListener l2 = ((UISWTViewEventListenerHolder)l).getDelegatedEventListener(view);
/* 1034 */                   if ((l2 != null) && (l2.equals(view.getEventListener()))) {
/* 1035 */                     view.closeView();
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             catch (Exception e) {
/* 1041 */               e.printStackTrace();
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */         });
/*      */       }
/* 1048 */     });
/* 1049 */     this.cPluginsArea.getParent().getParent().layout(true, true);
/*      */   }
/*      */   
/*      */   private void addSideBarView(UISWTViewImpl view, Composite cPluginsArea) {
/* 1053 */     Composite parent = new Composite(cPluginsArea, 0);
/* 1054 */     GridData gridData = new GridData();
/* 1055 */     gridData.grabExcessHorizontalSpace = true;
/* 1056 */     gridData.horizontalAlignment = 4;
/* 1057 */     parent.setLayoutData(gridData);
/* 1058 */     parent.setLayout(new org.eclipse.swt.layout.FormLayout());
/*      */     
/*      */ 
/*      */ 
/* 1062 */     view.initialize(parent);
/* 1063 */     parent.setVisible(true);
/*      */     
/* 1065 */     Control[] children = parent.getChildren();
/* 1066 */     for (int i = 0; i < children.length; i++) {
/* 1067 */       Control control = children[i];
/* 1068 */       Object ld = control.getLayoutData();
/* 1069 */       boolean useGridLayout = (ld != null) && ((ld instanceof GridData));
/* 1070 */       if (useGridLayout) {
/* 1071 */         GridLayout gridLayout = new GridLayout();
/* 1072 */         gridLayout.horizontalSpacing = 0;
/* 1073 */         gridLayout.marginHeight = 0;
/* 1074 */         gridLayout.marginWidth = 0;
/* 1075 */         gridLayout.verticalSpacing = 0;
/* 1076 */         parent.setLayout(gridLayout);
/* 1077 */         break; }
/* 1078 */       if (ld == null) {
/* 1079 */         control.setLayoutData(Utils.getFilledFormData());
/*      */       }
/*      */     }
/*      */     
/* 1083 */     this.pluginViews.add(view);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void defaultDrop(DropTargetEvent event)
/*      */   {
/* 1091 */     TorrentOpener.openDroppedTorrents(event, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void fillDropDownMenu(Menu menuDropDown, TreeItem[] items, int indent)
/*      */   {
/* 1101 */     String s = "";
/* 1102 */     for (int i = 0; i < indent; i++) {
/* 1103 */       s = s + "   ";
/*      */     }
/* 1105 */     for (int i = 0; i < items.length; i++) {
/* 1106 */       TreeItem treeItem = items[i];
/*      */       
/* 1108 */       SideBarEntrySWT entry = (SideBarEntrySWT)treeItem.getData("MdiEntry");
/* 1109 */       if (entry != null)
/*      */       {
/*      */ 
/* 1112 */         org.eclipse.swt.widgets.MenuItem menuItem = new org.eclipse.swt.widgets.MenuItem(menuDropDown, entry.isSelectable() ? 16 : 64);
/*      */         
/*      */ 
/* 1115 */         String id = entry.getId();
/* 1116 */         menuItem.setData("Plugin.viewID", id);
/* 1117 */         ViewTitleInfo titleInfo = entry.getViewTitleInfo();
/* 1118 */         String ind = "";
/* 1119 */         if (titleInfo != null) {
/* 1120 */           String o = (String)titleInfo.getTitleInfoProperty(0);
/* 1121 */           if (o != null) {
/* 1122 */             ind = "  (" + o + ")";
/*      */           }
/*      */         }
/*      */         
/* 1126 */         menuItem.setText(s + entry.getTitle() + ind);
/* 1127 */         menuItem.addSelectionListener(this.dropDownSelectionListener);
/* 1128 */         if ((this.currentEntry != null) && (this.currentEntry.getId().equals(id))) {
/* 1129 */           menuItem.setSelection(true);
/*      */         }
/*      */         
/* 1132 */         TreeItem[] subItems = treeItem.getItems();
/* 1133 */         if (subItems.length > 0) {
/* 1134 */           Menu parent = menuDropDown;
/* 1135 */           if (!entry.isSelectable()) {
/* 1136 */             parent = new Menu(menuDropDown.getParent().getShell(), 4);
/* 1137 */             menuItem.setMenu(parent);
/*      */           }
/*      */           
/*      */ 
/* 1141 */           fillDropDownMenu(parent, subItems, indent + 1);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void swt_updateSideBarHitAreasY(SideBarEntrySWT[] entries)
/*      */   {
/* 1152 */     for (int x = 0; x < entries.length; x++) {
/* 1153 */       SideBarEntrySWT entry = entries[x];
/* 1154 */       TreeItem treeItem = entry.getTreeItem();
/* 1155 */       if ((treeItem != null) && (!treeItem.isDisposed()))
/*      */       {
/*      */ 
/* 1158 */         Rectangle itemBounds = entry.swt_getBounds();
/*      */         
/* 1160 */         if (itemBounds != null) {
/* 1161 */           if (entry.isCloseable()) {
/* 1162 */             Rectangle closeArea = (Rectangle)treeItem.getData("closeArea");
/* 1163 */             if (closeArea != null) {
/* 1164 */               itemBounds.y += (itemBounds.height - closeArea.height) / 2;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 1169 */           MdiEntryVitalityImage[] vitalityImages = entry.getVitalityImages();
/* 1170 */           for (int i = 0; i < vitalityImages.length; i++) {
/* 1171 */             SideBarVitalityImageSWT vitalityImage = (SideBarVitalityImageSWT)vitalityImages[i];
/* 1172 */             if (vitalityImage.isVisible())
/*      */             {
/*      */ 
/* 1175 */               Image image = vitalityImage.getImage();
/* 1176 */               if (image != null) {
/* 1177 */                 Rectangle bounds = vitalityImage.getHitArea();
/* 1178 */                 if (bounds != null)
/*      */                 {
/*      */ 
/* 1181 */                   bounds.y = ((itemBounds.height - bounds.height) / 2); }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void swt_updateSideBarColors() {
/* 1191 */     SWTSkinProperties skinProperties = this.skin.getSkinProperties();
/*      */     
/* 1193 */     skinProperties.clearCache();
/*      */     
/* 1195 */     this.bg = skinProperties.getColor("color.sidebar.bg");
/*      */     
/* 1197 */     this.tree.setBackground(this.bg);
/*      */     
/* 1199 */     this.tree.redraw();
/*      */     
/* 1201 */     swt_updateSideBarColors(this.tree.getItems());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void swt_updateSideBarColors(TreeItem[] items)
/*      */   {
/* 1208 */     for (TreeItem ti : items)
/*      */     {
/* 1210 */       SideBarEntrySWT entry = (SideBarEntrySWT)ti.getData("MdiEntry");
/*      */       
/* 1212 */       if (entry != null)
/*      */       {
/* 1214 */         entry.updateColors();
/*      */         
/* 1216 */         entry.redraw();
/*      */       }
/*      */       
/* 1219 */       swt_updateSideBarColors(ti.getItems());
/*      */     }
/*      */   }
/*      */   
/*      */   protected int indexOf(final MdiEntry entry) {
/* 1224 */     Object o = Utils.execSWTThreadWithObject("indexOf", new org.gudy.azureus2.core3.util.AERunnableObject() {
/*      */       public Object runSupport() {
/* 1226 */         TreeItem treeItem = ((SideBarEntrySWT)entry).getTreeItem();
/* 1227 */         if (treeItem == null) {
/* 1228 */           return Integer.valueOf(-1);
/*      */         }
/* 1230 */         TreeItem parentItem = treeItem.getParentItem();
/* 1231 */         if (parentItem != null) {
/* 1232 */           return Integer.valueOf(parentItem.indexOf(treeItem));
/*      */         }
/* 1234 */         return Integer.valueOf(SideBar.this.tree.indexOf(treeItem)); } }, 500L);
/*      */     
/*      */ 
/* 1237 */     if ((o instanceof Number)) {
/* 1238 */       return ((Number)o).intValue();
/*      */     }
/* 1240 */     return -1;
/*      */   }
/*      */   
/*      */   public MdiEntry createHeader(String id, String titleID, String preferredAfterID) {
/* 1244 */     MdiEntry oldEntry = getEntry(id);
/* 1245 */     if (oldEntry != null) {
/* 1246 */       return oldEntry;
/*      */     }
/*      */     
/* 1249 */     SideBarEntrySWT entry = new SideBarEntrySWT(this, this.skin, id, null);
/* 1250 */     entry.setSelectable(false);
/* 1251 */     entry.setPreferredAfterID(preferredAfterID);
/* 1252 */     entry.setTitleID(titleID);
/*      */     
/* 1254 */     setupNewEntry(entry, id, true, false);
/*      */     
/* 1256 */     return entry;
/*      */   }
/*      */   
/*      */ 
/*      */   private void setupNewEntry(final SideBarEntrySWT entry, final String id, final boolean expandParent, final boolean closeable)
/*      */   {
/* 1262 */     addItem(entry);
/*      */     
/* 1264 */     entry.setCloseable(closeable);
/* 1265 */     entry.setParentSkinObject(this.soSideBarContents);
/* 1266 */     entry.setDestroyOnDeactivate(false);
/*      */     
/* 1268 */     if (("header.plugins".equals(entry.getParentID())) && (entry.getImageLeftID() == null))
/*      */     {
/* 1270 */       entry.setImageLeftID("image.sidebar.plugin");
/*      */     }
/*      */     
/* 1273 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*      */       public void runSupport() {
/* 1275 */         SideBar.this._setupNewEntry(entry, id, expandParent, closeable);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   protected void _setupNewEntry(SideBarEntrySWT entry, String id, boolean expandParent, boolean closeable)
/*      */   {
/* 1282 */     String parentID = entry.getParentID();
/* 1283 */     MdiEntry parent = getEntry(parentID);
/* 1284 */     TreeItem parentTreeItem = null;
/* 1285 */     if ((parent instanceof SideBarEntrySWT)) {
/* 1286 */       SideBarEntrySWT parentSWT = (SideBarEntrySWT)parent;
/* 1287 */       parentTreeItem = parentSWT.getTreeItem();
/* 1288 */       if (expandParent) {
/* 1289 */         parentTreeItem.setExpanded(true);
/*      */       }
/*      */     }
/* 1292 */     int index = -1;
/* 1293 */     String preferredAfterID = entry.getPreferredAfterID();
/* 1294 */     if (preferredAfterID != null) {
/* 1295 */       if (preferredAfterID.length() == 0) {
/* 1296 */         index = 0;
/*      */       } else {
/* 1298 */         boolean hack_it = preferredAfterID.startsWith("~");
/*      */         
/* 1300 */         if (hack_it)
/*      */         {
/*      */ 
/*      */ 
/* 1304 */           preferredAfterID = preferredAfterID.substring(1);
/*      */         }
/*      */         
/* 1307 */         MdiEntry entryAbove = getEntry(preferredAfterID);
/* 1308 */         if (entryAbove != null) {
/* 1309 */           index = indexOf(entryAbove);
/* 1310 */           if (!hack_it)
/*      */           {
/*      */ 
/* 1313 */             if (index >= 0) {
/* 1314 */               index++;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1322 */     if ((index == -1) && (parent == null)) {
/* 1323 */       index = 0;
/* 1324 */       String[] order = getPreferredOrder();
/* 1325 */       for (int i = 0; i < order.length; i++) {
/* 1326 */         String orderID = order[i];
/* 1327 */         if (orderID.equals(id)) {
/*      */           break;
/*      */         }
/* 1330 */         MdiEntry entry2 = getEntry(orderID);
/* 1331 */         if (entry2 != null) {
/* 1332 */           int i2 = indexOf(entry2);
/* 1333 */           if (i2 >= 0) {
/* 1334 */             index = i2 + 1;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1340 */     if ((GAP_BETWEEN_LEVEL_1 > 0) && (parentTreeItem == null) && (this.tree.getItemCount() > 0) && (index != 0))
/*      */     {
/* 1342 */       for (int i = 0; i < GAP_BETWEEN_LEVEL_1; i++) {
/* 1343 */         createTreeItem(null, index);
/* 1344 */         if (index >= 0) {
/* 1345 */           index++;
/*      */         }
/*      */       }
/*      */     }
/* 1349 */     TreeItem treeItem = createTreeItem(parentTreeItem, index);
/* 1350 */     if (treeItem != null) {
/* 1351 */       treeItem.setData("MdiEntry", entry);
/* 1352 */       entry.setTreeItem(treeItem);
/*      */       
/* 1354 */       triggerEntryLoadedListeners(entry);
/*      */     }
/* 1356 */     if ((GAP_BETWEEN_LEVEL_1 > 0) && (parentTreeItem == null) && (this.tree.getItemCount() > 1) && (index == 0))
/*      */     {
/* 1358 */       for (int i = 0; i < GAP_BETWEEN_LEVEL_1; i++) {
/* 1359 */         createTreeItem(null, ++index);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private TreeItem createTreeItem(Object parentSwtItem, int index)
/*      */   {
/* 1367 */     if (parentSwtItem == null)
/* 1368 */       parentSwtItem = this.tree;
/*      */     TreeItem treeItem;
/*      */     TreeItem treeItem;
/* 1371 */     if ((parentSwtItem instanceof Tree)) {
/* 1372 */       Tree tree = (Tree)parentSwtItem;
/* 1373 */       if (tree.isDisposed())
/* 1374 */         return null;
/*      */       TreeItem treeItem;
/* 1376 */       if ((index >= 0) && (index < tree.getItemCount())) {
/* 1377 */         treeItem = new TreeItem(tree, 0, index);
/*      */       } else {
/* 1379 */         treeItem = new TreeItem(tree, 0);
/*      */       }
/*      */     } else {
/* 1382 */       if (((TreeItem)parentSwtItem).isDisposed())
/* 1383 */         return null;
/*      */       TreeItem treeItem;
/* 1385 */       if ((index >= 0) && (index < ((TreeItem)parentSwtItem).getItemCount())) {
/* 1386 */         treeItem = new TreeItem((TreeItem)parentSwtItem, 0, index);
/*      */       } else {
/* 1388 */         treeItem = new TreeItem((TreeItem)parentSwtItem, 0);
/*      */       }
/*      */     }
/*      */     
/* 1392 */     return treeItem;
/*      */   }
/*      */   
/*      */   public void showEntry(MdiEntry newEntry) {
/* 1396 */     if (this.tree.isDisposed()) {
/* 1397 */       return;
/*      */     }
/*      */     
/* 1400 */     if ((newEntry == null) || (!newEntry.isSelectable())) {
/* 1401 */       return;
/*      */     }
/*      */     
/* 1404 */     SideBarEntrySWT oldEntry = (SideBarEntrySWT)this.currentEntry;
/*      */     
/*      */ 
/* 1407 */     if (this.currentEntry == newEntry) {
/* 1408 */       triggerSelectionListener(newEntry, newEntry);
/* 1409 */       return;
/*      */     }
/*      */     
/*      */ 
/* 1413 */     this.currentEntry = ((MdiEntrySWT)newEntry);
/*      */     
/* 1415 */     if ((oldEntry != null) && (oldEntry != newEntry)) {
/* 1416 */       oldEntry.redraw();
/*      */     }
/*      */     
/* 1419 */     if (this.currentEntry != null) {
/* 1420 */       ((BaseMdiEntry)this.currentEntry).show();
/*      */     }
/*      */     
/*      */ 
/* 1424 */     if ((oldEntry != null) && (oldEntry != newEntry)) {
/* 1425 */       oldEntry.hide();
/* 1426 */       oldEntry.redraw();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1433 */     MdiEntrySWT[] entries = getEntriesSWT();
/* 1434 */     for (MdiEntrySWT entry : entries) {
/* 1435 */       if (entry != null)
/*      */       {
/*      */ 
/* 1438 */         if (entry != this.currentEntry)
/*      */         {
/* 1440 */           SWTSkinObject obj = ((SideBarEntrySWT)entry).getSkinObjectMaster();
/*      */           
/* 1442 */           if ((obj != null) && (obj.isVisible()))
/*      */           {
/* 1444 */             entry.hide();
/* 1445 */             entry.redraw();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1450 */     newEntry.redraw();
/*      */     
/* 1452 */     triggerSelectionListener(newEntry, oldEntry);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public MdiEntry createEntryFromEventListener(String parentEntryID, String parentViewID, UISWTViewEventListener l, String id, boolean closeable, Object datasource, String preferredAfterID)
/*      */   {
/* 1461 */     MdiEntry oldEntry = getEntry(id);
/* 1462 */     if (oldEntry != null) {
/* 1463 */       return oldEntry;
/*      */     }
/*      */     
/* 1466 */     SideBarEntrySWT entry = new SideBarEntrySWT(this, this.skin, id, parentViewID);
/*      */     
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1472 */       addItem(entry);
/*      */       
/* 1474 */       entry.setEventListener(l, true);
/* 1475 */       entry.setParentID(parentEntryID);
/* 1476 */       entry.setDatasource(datasource);
/* 1477 */       entry.setPreferredAfterID(preferredAfterID);
/* 1478 */       setupNewEntry(entry, id, false, closeable);
/*      */       
/*      */ 
/* 1481 */       if ((l instanceof org.gudy.azureus2.ui.swt.views.IViewAlwaysInitialize)) {
/* 1482 */         entry.build();
/*      */       }
/*      */     } catch (Exception e) {
/* 1485 */       Debug.out(e);
/* 1486 */       entry.close(true);
/* 1487 */       entry = null;
/*      */     }
/*      */     
/* 1490 */     return entry;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public MdiEntry createEntryFromSkinRef(String parentID, String id, String configID, String title, ViewTitleInfo titleInfo, Object params, boolean closeable, String preferredAfterID)
/*      */   {
/* 1498 */     MdiEntry oldEntry = getEntry(id);
/* 1499 */     if (oldEntry != null) {
/* 1500 */       return oldEntry;
/*      */     }
/*      */     
/* 1503 */     SideBarEntrySWT entry = new SideBarEntrySWT(this, this.skin, id, null);
/*      */     
/* 1505 */     entry.setTitle(title);
/* 1506 */     entry.setSkinRef(configID, params);
/* 1507 */     entry.setParentID(parentID);
/* 1508 */     entry.setViewTitleInfo(titleInfo);
/* 1509 */     entry.setPreferredAfterID(preferredAfterID);
/*      */     
/* 1511 */     setupNewEntry(entry, id, false, closeable);
/*      */     
/* 1513 */     return entry;
/*      */   }
/*      */   
/*      */   public void updateUI()
/*      */   {
/* 1518 */     Object[] views = this.pluginViews.toArray();
/* 1519 */     for (int i = 0; i < views.length; i++) {
/*      */       try {
/* 1521 */         UISWTViewCore view = (UISWTViewCore)views[i];
/* 1522 */         Composite composite = view.getComposite();
/* 1523 */         if (composite != null)
/*      */         {
/*      */ 
/* 1526 */           if (composite.isDisposed()) {
/* 1527 */             this.pluginViews.remove(view);
/*      */ 
/*      */           }
/* 1530 */           else if (composite.isVisible())
/* 1531 */             view.triggerEvent(5, null);
/*      */         }
/*      */       } catch (Exception e) {
/* 1534 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/* 1538 */     if (this.tree.getSelectionCount() == 0) {
/* 1539 */       return;
/*      */     }
/* 1541 */     super.updateUI();
/*      */   }
/*      */   
/*      */   protected boolean wasEntryLoadedOnce(String id)
/*      */   {
/* 1546 */     boolean loadedOnce = COConfigurationManager.getBooleanParameter("sb.once." + id, false);
/*      */     
/* 1548 */     return loadedOnce;
/*      */   }
/*      */   
/*      */   protected void setEntryLoadedOnce(String id) {
/* 1552 */     COConfigurationManager.setParameter("sb.once." + id, true);
/*      */   }
/*      */   
/*      */   public Font getHeaderFont() {
/* 1556 */     return this.fontHeader;
/*      */   }
/*      */   
/*      */   protected Tree getTree() {
/* 1560 */     return this.tree;
/*      */   }
/*      */   
/*      */ 
/*      */   public MdiEntrySWT getEntryFromSkinObject(PluginUISWTSkinObject pluginSkinObject)
/*      */   {
/* 1566 */     if ((pluginSkinObject instanceof SWTSkinObject)) {
/* 1567 */       Control control = ((SWTSkinObject)pluginSkinObject).getControl();
/* 1568 */       while ((control != null) && (!control.isDisposed())) {
/* 1569 */         Object entry = control.getData("BaseMDIEntry");
/* 1570 */         if ((entry instanceof BaseMdiEntry)) {
/* 1571 */           BaseMdiEntry mdiEntry = (BaseMdiEntry)entry;
/* 1572 */           return mdiEntry;
/*      */         }
/* 1574 */         control = control.getParent();
/*      */       }
/*      */     }
/* 1577 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void requestAttention(SideBarEntrySWT entry)
/*      */   {
/* 1584 */     synchronized (this.attention_seekers)
/*      */     {
/* 1586 */       if (!this.attention_seekers.contains(entry))
/*      */       {
/* 1588 */         this.attention_seekers.add(entry);
/*      */       }
/*      */       
/* 1591 */       if (this.attention_event == null)
/*      */       {
/* 1593 */         this.attention_event = org.gudy.azureus2.core3.util.SimpleTimer.addPeriodicEvent("SideBar:attention", 500L, new org.gudy.azureus2.core3.util.TimerEventPerformer()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1599 */           int tick_count = 0;
/*      */           
/*      */ 
/*      */ 
/*      */           public void perform(TimerEvent event)
/*      */           {
/* 1605 */             this.tick_count += 1;
/*      */             
/* 1607 */             final List<SideBarEntrySWT> repaints = new ArrayList();
/*      */             
/* 1609 */             synchronized (SideBar.this.attention_seekers)
/*      */             {
/* 1611 */               Iterator<SideBarEntrySWT> it = SideBar.this.attention_seekers.iterator();
/*      */               
/* 1613 */               while (it.hasNext())
/*      */               {
/* 1615 */                 SideBarEntrySWT entry = (SideBarEntrySWT)it.next();
/*      */                 
/* 1617 */                 if (entry.isDisposed())
/*      */                 {
/* 1619 */                   it.remove();
/*      */                 }
/*      */                 else
/*      */                 {
/* 1623 */                   if (!entry.attentionUpdate(this.tick_count))
/*      */                   {
/* 1625 */                     it.remove();
/*      */                   }
/*      */                   
/* 1628 */                   repaints.add(entry);
/*      */                 }
/*      */               }
/*      */               
/* 1632 */               if (SideBar.this.attention_seekers.size() == 0)
/*      */               {
/* 1634 */                 TimerEventPeriodic ev = SideBar.this.attention_event;
/*      */                 
/* 1636 */                 if (ev != null)
/*      */                 {
/* 1638 */                   ev.cancel();
/*      */                   
/* 1640 */                   SideBar.this.attention_event = null;
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 1645 */             if (repaints.size() > 0)
/*      */             {
/* 1647 */               Utils.execSWTThread(new AERunnable()
/*      */               {
/*      */ 
/*      */ 
/*      */                 public void runSupport()
/*      */                 {
/*      */ 
/* 1654 */                   for (SideBarEntrySWT entry : repaints)
/*      */                   {
/* 1656 */                     entry.redraw();
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/* 1670 */   private Stack<SideBarEntrySWT> stack = new Stack();
/*      */   
/*      */   public void addItem(MdiEntry entry) {
/* 1673 */     super.addItem(entry);
/* 1674 */     if ((entry instanceof SideBarEntrySWT)) {
/* 1675 */       synchronized (this.stack) {
/* 1676 */         this.stack.remove(entry);
/* 1677 */         if (entry.isSelectable()) {
/* 1678 */           this.stack.push((SideBarEntrySWT)entry);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void itemSelected(MdiEntry entry)
/*      */   {
/* 1686 */     super.itemSelected(entry);
/* 1687 */     if ((entry instanceof SideBarEntrySWT)) {
/* 1688 */       synchronized (this.stack) {
/* 1689 */         this.stack.remove(entry);
/* 1690 */         if (entry.isSelectable()) {
/* 1691 */           this.stack.push((SideBarEntrySWT)entry);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeItem(MdiEntry entry) {
/* 1698 */     super.removeItem(entry);
/* 1699 */     if ((entry instanceof SideBarEntrySWT))
/*      */     {
/* 1701 */       MdiEntry current = getCurrentEntry();
/*      */       
/* 1703 */       SideBarEntrySWT next = null;
/*      */       
/* 1705 */       synchronized (this.stack)
/*      */       {
/* 1707 */         this.stack.remove(entry);
/*      */         
/* 1709 */         if ((current == null) || (current == entry))
/*      */         {
/*      */ 
/* 1712 */           while (!this.stack.isEmpty()) {
/* 1713 */             next = (SideBarEntrySWT)this.stack.pop();
/* 1714 */             if (!next.isDisposed()) break;
/* 1715 */             next = null;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1722 */       if (next != null) {
/* 1723 */         showEntry(next);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void generate(IndentWriter writer) {
/* 1729 */     MdiEntrySWT[] entries = getEntriesSWT();
/* 1730 */     for (MdiEntrySWT entry : entries) {
/* 1731 */       if (entry != null)
/*      */       {
/*      */ 
/*      */ 
/* 1735 */         if (!(entry instanceof AEDiagnosticsEvidenceGenerator)) {
/* 1736 */           writer.println("Sidebar View (No Generator): " + entry.getId());
/*      */           try {
/* 1738 */             writer.indent();
/*      */             
/* 1740 */             writer.println("Parent: " + entry.getParentID());
/* 1741 */             writer.println("Title: " + entry.getTitle());
/*      */ 
/*      */           }
/*      */           catch (Exception e) {}finally
/*      */           {
/* 1746 */             writer.exdent();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Image obfusticatedImage(Image image)
/*      */   {
/* 1756 */     Rectangle treeBounds = this.tree.getBounds();
/* 1757 */     SideBarEntrySWT[] sideBarEntries = (SideBarEntrySWT[])getEntries(new SideBarEntrySWT[0]);
/*      */     
/* 1759 */     for (SideBarEntrySWT entry : sideBarEntries) {
/* 1760 */       Rectangle entryBounds = entry.swt_getBounds();
/* 1761 */       if ((entryBounds != null) && (treeBounds.intersects(entryBounds))) {
/* 1762 */         entry.obfusticatedImage(image);
/*      */       }
/*      */     }
/* 1765 */     return image;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/sidebar/SideBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */