/*      */ package com.aelitis.azureus.ui.swt.mdi;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImage;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinFactory;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectTabFolder;
/*      */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.SkinnedDialog;
/*      */ import java.lang.reflect.Field;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.Map;
/*      */ import org.eclipse.swt.custom.CTabFolder;
/*      */ import org.eclipse.swt.custom.CTabFolder2Adapter;
/*      */ import org.eclipse.swt.custom.CTabFolderEvent;
/*      */ import org.eclipse.swt.custom.CTabFolderRenderer;
/*      */ import org.eclipse.swt.custom.CTabItem;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.DisposeListener;
/*      */ import org.eclipse.swt.events.MenuEvent;
/*      */ import org.eclipse.swt.events.MouseAdapter;
/*      */ import org.eclipse.swt.events.MouseEvent;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.RGB;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.Widget;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.config.impl.ConfigurationManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*      */ import org.gudy.azureus2.ui.common.util.MenuItemManager;
/*      */ import org.gudy.azureus2.ui.swt.MenuBuildUtils;
/*      */ import org.gudy.azureus2.ui.swt.MenuBuildUtils.MenuBuilder;
/*      */ import org.gudy.azureus2.ui.swt.MenuBuildUtils.MenuItemPluginMenuControllerImpl;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.debug.ObfusticateImage;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*      */ import org.gudy.azureus2.ui.swt.plugins.PluginUISWTSkinObject;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewEventCancelledException;
/*      */ import org.gudy.azureus2.ui.swt.views.IViewAlwaysInitialize;
/*      */ 
/*      */ public class TabbedMDI extends BaseMDI implements TabbedMdiInterface, AEDiagnosticsEvidenceGenerator, ParameterListener, ObfusticateImage
/*      */ {
/*      */   private CTabFolder tabFolder;
/*   75 */   private LinkedList<MdiEntry> select_history = new LinkedList();
/*      */   
/*      */   protected boolean minimized;
/*      */   
/*      */   private int iFolderHeightAdj;
/*      */   
/*      */   private String props_prefix;
/*      */   
/*      */   private DownloadManager maximizeTo;
/*      */   
/*   85 */   private int minimumCharacters = 25;
/*      */   
/*      */   protected boolean isMainMDI;
/*      */   
/*      */   private Map mapUserClosedTabs;
/*      */   
/*   91 */   private boolean maximizeVisible = false;
/*      */   
/*   93 */   private boolean minimizeVisible = false;
/*      */   
/*      */   private TabbedMdiMaximizeListener maximizeListener;
/*      */   
/*      */   public TabbedMDI()
/*      */   {
/*   99 */     AEDiagnostics.addEvidenceGenerator(this);
/*  100 */     this.mapUserClosedTabs = new HashMap();
/*  101 */     this.isMainMDI = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TabbedMDI(Composite parent, String id)
/*      */   {
/*  108 */     this.props_prefix = id;
/*  109 */     this.minimumCharacters = 0;
/*  110 */     this.isMainMDI = false;
/*  111 */     setCloseableConfigFile(null);
/*      */     
/*  113 */     SWTSkin skin = SWTSkinFactory.getInstance();
/*  114 */     SWTSkinObjectTabFolder soFolder = new SWTSkinObjectTabFolder(skin, skin.getSkinProperties(), id, "tabfolder.fill", parent);
/*      */     
/*  116 */     setMainSkinObject(soFolder);
/*  117 */     soFolder.addListener(this);
/*  118 */     skin.addSkinObject(soFolder);
/*      */     
/*  120 */     String key = this.props_prefix + ".closedtabs";
/*      */     
/*  122 */     this.mapUserClosedTabs = COConfigurationManager.getMapParameter(key, new HashMap());
/*  123 */     COConfigurationManager.addParameterListener(key, this);
/*      */   }
/*      */   
/*      */   public Object skinObjectCreated(SWTSkinObject skinObject, Object params)
/*      */   {
/*  128 */     super.skinObjectCreated(skinObject, params);
/*      */     
/*  130 */     creatMDI();
/*      */     
/*  132 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object skinObjectDestroyed(SWTSkinObject skinObject, Object params)
/*      */   {
/*  140 */     String key = this.props_prefix + ".closedtabs";
/*  141 */     COConfigurationManager.removeParameterListener(key, this);
/*      */     
/*  143 */     return super.skinObjectDestroyed(skinObject, params);
/*      */   }
/*      */   
/*      */   private void creatMDI() {
/*  147 */     if ((this.soMain instanceof SWTSkinObjectTabFolder)) {
/*  148 */       this.tabFolder = ((SWTSkinObjectTabFolder)this.soMain).getTabFolder();
/*      */     } else {
/*  150 */       this.tabFolder = new CTabFolder((Composite)this.soMain.getControl(), 2240);
/*      */     }
/*      */     
/*      */ 
/*  154 */     this.iFolderHeightAdj = this.tabFolder.computeSize(-1, 0).y;
/*      */     
/*  156 */     if (this.isMainMDI) {
/*  157 */       COConfigurationManager.addAndFireParameterListener("GUI_SWT_bFancyTab", new ParameterListener()
/*      */       {
/*      */         public void parameterChanged(String parameterName) {
/*  160 */           Utils.execSWTThread(new AERunnable() {
/*      */             public void runSupport() {
/*  162 */               boolean simple = !COConfigurationManager.getBooleanParameter("GUI_SWT_bFancyTab");
/*  163 */               TabbedMDI.this.tabFolder.setSimple(simple);
/*      */             }
/*      */           });
/*      */         }
/*  167 */       });
/*  168 */       this.tabFolder.setSimple(!COConfigurationManager.getBooleanParameter("GUI_SWT_bFancyTab"));
/*      */     } else {
/*  170 */       this.tabFolder.setSimple(true);
/*  171 */       this.tabFolder.setMaximizeVisible(this.maximizeVisible);
/*  172 */       this.tabFolder.setMinimizeVisible(this.minimizeVisible);
/*  173 */       this.tabFolder.setUnselectedCloseVisible(false);
/*      */     }
/*      */     
/*  176 */     Display display = this.tabFolder.getDisplay();
/*      */     
/*  178 */     float[] hsb = this.tabFolder.getBackground().getRGB().getHSB(); int 
/*  179 */       tmp174_173 = 2; float[] tmp174_172 = hsb;tmp174_172[tmp174_173] = ((float)(tmp174_172[tmp174_173] * (Constants.isOSX ? 0.9D : 0.97D)));
/*  180 */     this.tabFolder.setBackground(ColorCache.getColor(display, hsb));
/*      */     
/*  182 */     hsb = this.tabFolder.getForeground().getRGB().getHSB(); int 
/*  183 */       tmp223_222 = 2; float[] tmp223_221 = hsb;tmp223_221[tmp223_222] = ((float)(tmp223_221[tmp223_222] * (Constants.isOSX ? 1.1D : 0.03D)));
/*  184 */     this.tabFolder.setForeground(ColorCache.getColor(display, hsb));
/*      */     
/*  186 */     this.tabFolder.setSelectionBackground(new Color[] { display.getSystemColor(25), display.getSystemColor(25), display.getSystemColor(22) }, new int[] { 10, 90 }, true);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  194 */     this.tabFolder.setSelectionForeground(display.getSystemColor(24));
/*      */     
/*  196 */     if (this.minimumCharacters > 0) {
/*  197 */       this.tabFolder.setMinimumCharacters(this.minimumCharacters);
/*      */     }
/*      */     
/*      */ 
/*  201 */     this.tabFolder.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  203 */         TabbedEntry entry = (TabbedEntry)event.item.getData("TabbedEntry");
/*  204 */         TabbedMDI.this.showEntry(entry);
/*      */       }
/*      */       
/*  207 */     });
/*  208 */     this.tabFolder.addMouseListener(new MouseAdapter() {
/*      */       public void mouseDown(MouseEvent e) {
/*  210 */         if (TabbedMDI.this.tabFolder.getMinimized()) {
/*  211 */           TabbedMDI.this.restore();
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  218 */           e.button = 0;
/*  219 */           TabbedMDI.this.tabFolder.notifyListeners(7, null);
/*      */         }
/*      */       }
/*      */       
/*  223 */       public void mouseDoubleClick(MouseEvent e) { if ((!TabbedMDI.this.tabFolder.getMinimized()) && (TabbedMDI.this.tabFolder.getMaximizeVisible())) {
/*  224 */           TabbedMDI.this.minimize();
/*      */         }
/*      */         
/*      */       }
/*  228 */     });
/*  229 */     this.tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
/*      */       public void minimize(CTabFolderEvent event) {
/*  231 */         TabbedMDI.this.minimize();
/*      */       }
/*      */       
/*      */       public void maximize(CTabFolderEvent event)
/*      */       {
/*  236 */         if (TabbedMDI.this.maximizeListener != null) {
/*  237 */           TabbedMDI.this.maximizeListener.maximizePressed();
/*      */         }
/*      */       }
/*      */       
/*      */       public void restore(CTabFolderEvent event_maybe_null)
/*      */       {
/*  243 */         TabbedMDI.this.restore();
/*      */       }
/*      */       
/*      */ 
/*      */       public void close(CTabFolderEvent event)
/*      */       {
/*  249 */         final TabbedEntry entry = (TabbedEntry)event.item.getData("TabbedEntry");
/*      */         
/*      */ 
/*  252 */         if (TabbedMDI.this.select_history.remove(entry))
/*      */         {
/*  254 */           if (TabbedMDI.this.select_history.size() > 0)
/*      */           {
/*  256 */             MdiEntry next = (MdiEntry)TabbedMDI.this.select_history.getLast();
/*      */             
/*  258 */             if ((!next.isDisposed()) && (next != entry))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  263 */               CTabItem[] items = TabbedMDI.this.tabFolder.getItems();
/*  264 */               for (int i = 0; i < items.length; i++) {
/*  265 */                 CTabItem item = items[i];
/*  266 */                 TabbedEntry scanEntry = TabbedMDI.this.getEntryFromTabItem(item);
/*  267 */                 if (scanEntry == next) {
/*  268 */                   TabbedMDI.this.tabFolder.setSelection(item);
/*  269 */                   break;
/*      */                 }
/*      */               }
/*      */               
/*  273 */               TabbedMDI.this.showEntry(next);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  282 */         if (TabbedMDI.this.props_prefix != null) {
/*  283 */           Utils.execSWTThreadLater(0, new AERunnable()
/*      */           {
/*      */             public void runSupport()
/*      */             {
/*  287 */               String view_id = entry.getViewID();
/*  288 */               String key = TabbedMDI.this.props_prefix + ".closedtabs";
/*      */               
/*  290 */               Map closedtabs = COConfigurationManager.getMapParameter(key, new HashMap());
/*      */               
/*      */ 
/*  293 */               if (!closedtabs.containsKey(view_id))
/*      */               {
/*  295 */                 closedtabs.put(view_id, entry.getTitle());
/*      */                 
/*      */ 
/*  298 */                 COConfigurationManager.setParameter(key, closedtabs);
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     });
/*      */     
/*  306 */     if (this.isMainMDI) {
/*  307 */       this.tabFolder.getDisplay().addFilter(1, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  309 */           if (TabbedMDI.this.tabFolder.isDisposed()) {
/*  310 */             return;
/*      */           }
/*      */           
/*  313 */           Control focus_control = TabbedMDI.this.tabFolder.getDisplay().getFocusControl();
/*  314 */           if ((focus_control != null) && (focus_control.getShell() != TabbedMDI.this.tabFolder.getShell()))
/*      */           {
/*  316 */             return;
/*      */           }
/*      */           
/*  319 */           int key = event.character;
/*  320 */           if (((event.stateMask & org.eclipse.swt.SWT.MOD1) != 0) && (event.character <= '\032') && (event.character > 0))
/*      */           {
/*  322 */             key += 96;
/*      */           }
/*      */           
/*  325 */           if ((key == 27) || ((event.keyCode == 16777229) && (event.stateMask == 262144)))
/*      */           {
/*  327 */             MdiEntry entry = TabbedMDI.this.getCurrentEntry();
/*  328 */             if (entry != null) {
/*  329 */               entry.close(false);
/*      */             }
/*  331 */             event.doit = false;
/*  332 */           } else if ((event.keyCode == 16777231) || ((event.character == '\t') && ((event.stateMask & 0x40000) != 0)))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  338 */             if ((event.stateMask & 0x20000) == 0) {
/*  339 */               event.doit = false;
/*  340 */               TabbedMDI.this.selectNextTab(true);
/*      */             }
/*  342 */             else if (event.stateMask == 131072) {
/*  343 */               TabbedMDI.this.selectNextTab(false);
/*  344 */               event.doit = false;
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  351 */     this.tabFolder.addDisposeListener(new DisposeListener() {
/*      */       public void widgetDisposed(DisposeEvent e) {
/*  353 */         TabbedMDI.this.saveCloseables();
/*      */       }
/*      */       
/*  356 */     });
/*  357 */     this.tabFolder.getTabHeight();
/*  358 */     final Menu menu = new Menu(this.tabFolder);
/*  359 */     this.tabFolder.setMenu(menu);
/*  360 */     MenuBuildUtils.addMaintenanceListenerForMenu(menu, new MenuBuildUtils.MenuBuilder() {
/*      */       public void buildMenu(Menu root_menu, MenuEvent event) {
/*  362 */         Point cursorLocation = event.display.getCursorLocation();
/*  363 */         Point ptOnControl = TabbedMDI.this.tabFolder.toControl(cursorLocation.x, cursorLocation.y);
/*      */         
/*  365 */         if (ptOnControl.y > TabbedMDI.this.tabFolder.getTabHeight()) {
/*  366 */           return;
/*      */         }
/*      */         
/*  369 */         CTabItem item = TabbedMDI.this.tabFolder.getItem(TabbedMDI.this.tabFolder.toControl(cursorLocation.x, cursorLocation.y));
/*      */         
/*      */ 
/*  372 */         boolean need_sep = false;
/*      */         
/*  374 */         if (item == null)
/*      */         {
/*  376 */           need_sep = TabbedMDI.this.mapUserClosedTabs.size() > 0;
/*  377 */           if (need_sep) {
/*  378 */             for (Object id : TabbedMDI.this.mapUserClosedTabs.keySet()) {
/*  379 */               final String view_id = (String)id;
/*      */               
/*  381 */               org.eclipse.swt.widgets.MenuItem mi = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*      */               
/*      */ 
/*      */ 
/*  385 */               Object oTitle = TabbedMDI.this.mapUserClosedTabs.get(id);
/*  386 */               String title; String title; if (((oTitle instanceof String)) && (((String)oTitle).length() > 0)) {
/*  387 */                 title = (String)oTitle;
/*      */               } else {
/*  389 */                 title = MessageText.getString(TabbedMDI.this.getViewTitleID(view_id));
/*      */               }
/*  391 */               mi.setText(title);
/*      */               
/*  393 */               mi.addListener(13, new Listener() {
/*      */                 public void handleEvent(Event event) {
/*  395 */                   String key = TabbedMDI.this.props_prefix + ".closedtabs";
/*      */                   
/*  397 */                   Map closedtabs = COConfigurationManager.getMapParameter(key, new HashMap());
/*      */                   
/*      */ 
/*  400 */                   if (closedtabs.containsKey(view_id))
/*      */                   {
/*  402 */                     closedtabs.remove(view_id);
/*      */                     
/*  404 */                     COConfigurationManager.setParameter(key, closedtabs);
/*      */                   }
/*      */                   
/*  407 */                   TabbedMDI.this.showEntryByID(view_id);
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  415 */         if (need_sep) {
/*  416 */           new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*      */         }
/*      */         
/*      */ 
/*  420 */         TabbedEntry entry = null;
/*  421 */         if (item != null) {
/*  422 */           entry = TabbedMDI.this.getEntryFromTabItem(item);
/*      */           
/*      */ 
/*  425 */           TabbedMDI.this.showEntry(entry);
/*      */         }
/*      */         
/*  428 */         TabbedMDI.this.fillMenu(menu, entry, TabbedMDI.this.isMainMDI ? "sidebar" : TabbedMDI.this.props_prefix);
/*      */       }
/*      */       
/*      */ 
/*  432 */     });
/*  433 */     CTabFolderRenderer renderer = new CTabFolderRenderer(this.tabFolder)
/*      */     {
/*      */ 
/*      */ 
/*      */       protected Point computeSize(int part, int state, GC gc, int wHint, int hHint)
/*      */       {
/*      */ 
/*  440 */         gc.setAntialias(1);
/*  441 */         Point pt = super.computeSize(part, state, gc, wHint, hHint);
/*  442 */         if (TabbedMDI.this.tabFolder.isDisposed()) {
/*  443 */           return pt;
/*      */         }
/*      */         
/*  446 */         if (part >= 0) {
/*  447 */           TabbedEntry entry = TabbedMDI.this.getEntryFromTabItem(TabbedMDI.this.tabFolder.getItem(part));
/*  448 */           if (entry != null) {
/*  449 */             ViewTitleInfo viewTitleInfo = entry.getViewTitleInfo();
/*  450 */             if (viewTitleInfo != null) {
/*  451 */               Object titleRight = viewTitleInfo.getTitleInfoProperty(0);
/*  452 */               if (titleRight != null) {
/*  453 */                 Point size = gc.textExtent(titleRight.toString(), 0);
/*  454 */                 pt.x += size.x + 10 + 2;
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*  459 */             MdiEntryVitalityImage[] vitalityImages = entry.getVitalityImages();
/*  460 */             ImageLoader imageLoader = ImageLoader.getInstance();
/*  461 */             for (MdiEntryVitalityImage mdiEntryVitalityImage : vitalityImages) {
/*  462 */               if ((mdiEntryVitalityImage != null) && (mdiEntryVitalityImage.isVisible())) {
/*  463 */                 String imageID = mdiEntryVitalityImage.getImageID();
/*  464 */                 Image image = imageLoader.getImage(imageID);
/*  465 */                 if (ImageLoader.isRealImage(image)) {
/*  466 */                   pt.x += image.getBounds().x + 1;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  473 */         return pt;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       protected void draw(int part, int state, Rectangle bounds, GC gc)
/*      */       {
/*      */         try
/*      */         {
/*  483 */           super.draw(part, state, bounds, gc);
/*      */         } catch (Throwable t) {
/*  485 */           Debug.out(t);
/*      */         }
/*  487 */         if (part < 0) {
/*  488 */           return;
/*      */         }
/*      */         try {
/*  491 */           CTabItem item = TabbedMDI.this.getTabFolder().getItem(part);
/*  492 */           TabbedEntry entry = TabbedMDI.this.getEntryFromTabItem(item);
/*  493 */           if (entry == null) {
/*  494 */             return;
/*      */           }
/*      */           
/*  497 */           ViewTitleInfo viewTitleInfo = entry.getViewTitleInfo();
/*  498 */           if (viewTitleInfo != null) {
/*  499 */             Object titleRight = viewTitleInfo.getTitleInfoProperty(0);
/*  500 */             if (titleRight != null) {
/*  501 */               String textIndicator = titleRight.toString();
/*  502 */               int x1IndicatorOfs = 0;
/*  503 */               int SIDEBAR_SPACING = 0;
/*  504 */               int x2 = bounds.x + bounds.width;
/*      */               
/*  506 */               if (item.getShowClose()) {
/*      */                 try {
/*  508 */                   Field fldCloseRect = item.getClass().getDeclaredField("closeRect");
/*  509 */                   fldCloseRect.setAccessible(true);
/*  510 */                   Rectangle closeBounds = (Rectangle)fldCloseRect.get(item);
/*  511 */                   if ((closeBounds != null) && (closeBounds.x > 0)) {
/*  512 */                     x2 = closeBounds.x;
/*      */                   }
/*      */                 } catch (Exception e) {
/*  515 */                   x2 -= 20;
/*      */                 }
/*      */               }
/*  518 */               gc.setAntialias(1);
/*      */               
/*  520 */               Point textSize = gc.textExtent(textIndicator);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  526 */               int width = textSize.x + 10;
/*  527 */               x1IndicatorOfs += width + SIDEBAR_SPACING;
/*  528 */               int startX = x2 - x1IndicatorOfs;
/*      */               
/*  530 */               int textOffsetY = 0;
/*      */               
/*  532 */               int height = textSize.y + 1;
/*  533 */               int startY = bounds.y + (bounds.height - height) / 2 + 1;
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
/*  545 */               Color default_color = ColorCache.getSchemedColor(gc.getDevice(), "#5b6e87");
/*      */               
/*  547 */               Object color = viewTitleInfo.getTitleInfoProperty(8);
/*      */               
/*  549 */               if ((color instanceof int[]))
/*      */               {
/*  551 */                 gc.setBackground(ColorCache.getColor(gc.getDevice(), (int[])color));
/*      */               }
/*      */               else
/*      */               {
/*  555 */                 gc.setBackground(default_color);
/*      */               }
/*      */               
/*      */ 
/*  559 */               Color text_color = Colors.white;
/*      */               
/*  561 */               gc.fillRoundRectangle(startX, startY, width, height, textSize.y * 2 / 3, height * 2 / 3);
/*      */               
/*      */ 
/*  564 */               if (color != null)
/*      */               {
/*  566 */                 Color bg = gc.getBackground();
/*      */                 
/*  568 */                 int red = bg.getRed();
/*  569 */                 int green = bg.getGreen();
/*  570 */                 int blue = bg.getBlue();
/*      */                 
/*  572 */                 double brightness = Math.sqrt(red * red * 0.299D + green * green * 0.587D + blue * blue * 0.114D);
/*      */                 
/*  574 */                 if (brightness >= 130.0D) {
/*  575 */                   text_color = Colors.black;
/*      */                 }
/*      */                 
/*  578 */                 gc.setBackground(default_color);
/*      */                 
/*  580 */                 gc.drawRoundRectangle(startX, startY, width, height, textSize.y * 2 / 3, height * 2 / 3);
/*      */               }
/*      */               
/*  583 */               gc.setForeground(text_color);
/*  584 */               org.gudy.azureus2.ui.swt.shells.GCStringPrinter.printString(gc, textIndicator, new Rectangle(startX, startY + textOffsetY, width, height), true, false, 16777216);
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*  591 */           Debug.out(t);
/*      */         }
/*      */       }
/*  594 */     };
/*  595 */     this.tabFolder.setRenderer(renderer);
/*      */     
/*  597 */     if (this.minimizeVisible) {
/*  598 */       boolean toMinimize = ConfigurationManager.getInstance().getBooleanParameter(this.props_prefix + ".subViews.minimized");
/*  599 */       setMinimized(toMinimize);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String getViewTitleID(String view_id)
/*      */   {
/*  607 */     String history_key = "swt.ui.table.tab.view.namecache." + view_id;
/*      */     
/*  609 */     String id = COConfigurationManager.getStringParameter(history_key, "");
/*      */     
/*  611 */     if (id.length() == 0)
/*      */     {
/*  613 */       String test = view_id + ".title.full";
/*      */       
/*  615 */       if (MessageText.keyExists(test))
/*      */       {
/*  617 */         return test;
/*      */       }
/*      */       
/*  620 */       id = "!" + view_id + "!";
/*      */     }
/*      */     
/*  623 */     return id;
/*      */   }
/*      */   
/*      */ 
/*      */   private void minimize()
/*      */   {
/*  629 */     this.minimized = true;
/*      */     
/*  631 */     this.tabFolder.setMinimized(true);
/*  632 */     CTabItem[] items = this.tabFolder.getItems();
/*  633 */     String tt = MessageText.getString("label.click.to.restore");
/*  634 */     for (int i = 0; i < items.length; i++) {
/*  635 */       CTabItem tabItem = items[i];
/*  636 */       tabItem.setToolTipText(tt);
/*  637 */       Control control = tabItem.getControl();
/*  638 */       if ((control != null) && (!control.isDisposed())) {
/*  639 */         tabItem.getControl().setVisible(false);
/*      */       }
/*      */     }
/*      */     
/*  643 */     this.tabFolder.getParent().notifyListeners(11, null);
/*      */     
/*  645 */     showEntry(null);
/*      */     
/*  647 */     ConfigurationManager configMan = ConfigurationManager.getInstance();
/*  648 */     configMan.setParameter(this.props_prefix + ".subViews.minimized", true);
/*      */   }
/*      */   
/*      */   private void restore()
/*      */   {
/*  653 */     this.minimized = false;
/*  654 */     this.tabFolder.setMinimized(false);
/*  655 */     CTabItem selection = this.tabFolder.getSelection();
/*  656 */     if (selection != null) {
/*  657 */       TabbedEntry tabbedEntry = getEntryFromTabItem(selection);
/*      */       
/*  659 */       showEntry(tabbedEntry);
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
/*  674 */       tabbedEntry.updateUI();
/*      */     }
/*      */     
/*  677 */     if (this.tabFolder.getMaximizeVisible()) {
/*  678 */       CTabItem[] items = this.tabFolder.getItems();
/*  679 */       String tt = MessageText.getString("label.dblclick.to.min");
/*      */       
/*  681 */       for (int i = 0; i < items.length; i++) {
/*  682 */         CTabItem tabItem = items[i];
/*  683 */         tabItem.setToolTipText(tt);
/*      */       }
/*      */     }
/*      */     
/*  687 */     this.tabFolder.getParent().notifyListeners(11, null);
/*      */     
/*  689 */     ConfigurationManager configMan = ConfigurationManager.getInstance();
/*  690 */     configMan.setParameter(this.props_prefix + ".subViews.minimized", false);
/*      */   }
/*      */   
/*      */ 
/*      */   private void selectNextTab(boolean selectNext)
/*      */   {
/*  696 */     if ((this.tabFolder == null) || (this.tabFolder.isDisposed())) {
/*  697 */       return;
/*      */     }
/*      */     
/*  700 */     int nextOrPrevious = selectNext ? 1 : -1;
/*  701 */     int index = this.tabFolder.getSelectionIndex() + nextOrPrevious;
/*  702 */     if (((index == 0) && (selectNext)) || (index == -2) || (this.tabFolder.getItemCount() < 2)) {
/*  703 */       return;
/*      */     }
/*  705 */     if (index == this.tabFolder.getItemCount()) {
/*  706 */       index = 0;
/*  707 */     } else if (index < 0) {
/*  708 */       index = this.tabFolder.getItemCount() - 1;
/*      */     }
/*      */     
/*      */ 
/*  712 */     CTabItem item = this.tabFolder.getItem(index);
/*  713 */     MdiEntry entry = getEntryFromTabItem(item);
/*      */     
/*  715 */     if (entry != null) {
/*  716 */       showEntry(entry);
/*      */     }
/*      */   }
/*      */   
/*      */   protected boolean wasEntryLoadedOnce(String id)
/*      */   {
/*  722 */     boolean loadedOnce = COConfigurationManager.getBooleanParameter("tab.once." + id, false);
/*      */     
/*  724 */     return loadedOnce;
/*      */   }
/*      */   
/*      */   protected void setEntryLoadedOnce(String id) {
/*  728 */     COConfigurationManager.setParameter("tab.once." + id, true);
/*      */   }
/*      */   
/*      */   public void showEntry(MdiEntry newEntry) {
/*  732 */     if (newEntry == null) {
/*  733 */       return;
/*      */     }
/*      */     
/*  736 */     if (newEntry != null) {
/*  737 */       this.select_history.remove(newEntry);
/*      */       
/*  739 */       this.select_history.add(newEntry);
/*      */       
/*  741 */       if (this.select_history.size() > 64)
/*      */       {
/*  743 */         this.select_history.removeFirst();
/*      */       }
/*      */     }
/*      */     
/*  747 */     MdiEntry oldEntry = this.currentEntry;
/*  748 */     if ((newEntry == oldEntry) && (oldEntry != null)) {
/*  749 */       ((BaseMdiEntry)newEntry).show();
/*  750 */       triggerSelectionListener(newEntry, newEntry);
/*  751 */       return;
/*      */     }
/*      */     
/*  754 */     if (oldEntry != null) {
/*  755 */       oldEntry.hide();
/*      */     }
/*      */     
/*  758 */     this.currentEntry = ((MdiEntrySWT)newEntry);
/*      */     
/*  760 */     if ((this.currentEntry instanceof BaseMdiEntry)) {
/*  761 */       ((BaseMdiEntry)newEntry).show();
/*      */     }
/*      */     
/*  764 */     triggerSelectionListener(newEntry, oldEntry);
/*      */   }
/*      */   
/*      */ 
/*      */   private MdiEntry createEntryFromSkinRef(String parentID, String id, String configID, String title, ViewTitleInfo titleInfo, Object params, boolean closeable, int index)
/*      */   {
/*  770 */     MdiEntry oldEntry = getEntry(id);
/*  771 */     if (oldEntry != null) {
/*  772 */       return oldEntry;
/*      */     }
/*      */     
/*  775 */     TabbedEntry entry = new TabbedEntry(this, this.skin, id, null);
/*  776 */     entry.setTitle(title);
/*  777 */     entry.setSkinRef(configID, params);
/*  778 */     entry.setViewTitleInfo(titleInfo);
/*      */     
/*  780 */     setupNewEntry(entry, id, index, closeable);
/*  781 */     return entry;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public MdiEntry createEntryFromSkinRef(String parentID, String id, String configID, String title, ViewTitleInfo titleInfo, Object params, boolean closeable, String preferedAfterID)
/*      */   {
/*  789 */     return createEntryFromSkinRef(parentID, id, configID, title, titleInfo, params, closeable, "".equals(preferedAfterID) ? 0 : -1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public MdiEntry createEntryFromEventListener(String parentEntryID, String parentViewID, UISWTViewEventListener l, String id, boolean closeable, Object datasource, String preferredAfterID)
/*      */   {
/*  796 */     if (isEntryClosedByUser(id)) {
/*  797 */       return null;
/*      */     }
/*  799 */     MdiEntry oldEntry = getEntry(id);
/*  800 */     if (oldEntry != null) {
/*  801 */       return oldEntry;
/*      */     }
/*      */     
/*  804 */     TabbedEntry entry = new TabbedEntry(this, this.skin, id, parentViewID);
/*      */     try
/*      */     {
/*  807 */       entry.setEventListener(l, true);
/*      */     } catch (UISWTViewEventCancelledException e) {
/*  809 */       entry.close(true);
/*  810 */       return null;
/*      */     }
/*  812 */     entry.setDatasource(datasource);
/*  813 */     entry.setPreferredAfterID(preferredAfterID);
/*      */     
/*  815 */     setupNewEntry(entry, id, -1, closeable);
/*      */     
/*  817 */     if ((l instanceof IViewAlwaysInitialize)) {
/*  818 */       entry.build();
/*      */     }
/*      */     
/*  821 */     return entry;
/*      */   }
/*      */   
/*      */   private boolean isEntryClosedByUser(String id)
/*      */   {
/*  826 */     if (this.mapUserClosedTabs.containsKey(id)) {
/*  827 */       return true;
/*      */     }
/*      */     
/*  830 */     return false;
/*      */   }
/*      */   
/*      */   private void setupNewEntry(final TabbedEntry entry, final String id, final int index, boolean closeable)
/*      */   {
/*  835 */     addItem(entry);
/*      */     
/*  837 */     entry.setCloseable(closeable);
/*      */     
/*  839 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*      */       public void runSupport() {
/*  841 */         TabbedMDI.this.swt_setupNewEntry(entry, id, index);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private void swt_setupNewEntry(TabbedEntry entry, String id, int index) {
/*  847 */     if ((this.tabFolder == null) || (this.tabFolder.isDisposed())) {
/*  848 */       return;
/*      */     }
/*  850 */     if ((index < 0) || (index >= this.tabFolder.getItemCount())) {
/*  851 */       index = this.tabFolder.getItemCount();
/*      */     }
/*  853 */     CTabItem cTabItem = new CTabItem(this.tabFolder, 0, index);
/*  854 */     cTabItem.addDisposeListener(new DisposeListener() {
/*      */       public void widgetDisposed(DisposeEvent e) {
/*  856 */         if (TabbedMDI.this.tabFolder.getItemCount() == 0) {
/*  857 */           TabbedMDI.this.currentEntry = null;
/*      */         }
/*      */       }
/*  860 */     });
/*  861 */     cTabItem.setData("TabbedEntry", entry);
/*  862 */     entry.setSwtItem(cTabItem);
/*      */     
/*  864 */     if (this.tabFolder.getItemCount() == 1) {
/*  865 */       Utils.execSWTThreadLater(0, new AERunnable()
/*      */       {
/*      */         public void runSupport()
/*      */         {
/*  869 */           if ((TabbedMDI.this.currentEntry != null) || (TabbedMDI.this.tabFolder.isDisposed())) {
/*  870 */             return;
/*      */           }
/*  872 */           CTabItem selection = TabbedMDI.this.tabFolder.getSelection();
/*  873 */           if (selection == null) {
/*  874 */             return;
/*      */           }
/*  876 */           TabbedEntry entry = TabbedMDI.this.getEntryFromTabItem(selection);
/*  877 */           TabbedMDI.this.showEntry(entry);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */   private TabbedEntry getEntryFromTabItem(CTabItem item) {
/*  884 */     if (item.isDisposed()) {
/*  885 */       return null;
/*      */     }
/*  887 */     return (TabbedEntry)item.getData("TabbedEntry");
/*      */   }
/*      */   
/*      */   public String getUpdateUIName() {
/*  891 */     String name = "MDI";
/*  892 */     MdiEntry entry = getCurrentEntry();
/*  893 */     if (entry != null) {
/*  894 */       name = name + "-" + entry.getId();
/*      */     }
/*  896 */     return name;
/*      */   }
/*      */   
/*      */   public void generate(IndentWriter writer) {
/*  900 */     MdiEntrySWT[] entries = getEntriesSWT();
/*  901 */     for (MdiEntrySWT entry : entries) {
/*  902 */       if (entry != null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  907 */         if (!(entry instanceof AEDiagnosticsEvidenceGenerator)) {
/*  908 */           writer.println("TabbedMdi View (No Generator): " + entry.getId());
/*      */           try {
/*  910 */             writer.indent();
/*      */             
/*  912 */             writer.println("Parent: " + entry.getParentID());
/*  913 */             writer.println("Title: " + entry.getTitle());
/*      */ 
/*      */           }
/*      */           catch (Exception e) {}finally
/*      */           {
/*  918 */             writer.exdent();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public MdiEntrySWT getEntryFromSkinObject(PluginUISWTSkinObject pluginSkinObject) {
/*  926 */     if ((pluginSkinObject instanceof SWTSkinObject)) {
/*  927 */       Control control = ((SWTSkinObject)pluginSkinObject).getControl();
/*  928 */       while ((control != null) && (!control.isDisposed())) {
/*  929 */         Object entry = control.getData("BaseMDIEntry");
/*  930 */         if ((entry instanceof BaseMdiEntry)) {
/*  931 */           BaseMdiEntry mdiEntry = (BaseMdiEntry)entry;
/*  932 */           return mdiEntry;
/*      */         }
/*  934 */         control = control.getParent();
/*      */       }
/*      */     }
/*  937 */     return null;
/*      */   }
/*      */   
/*      */   public MdiEntry createHeader(String id, String title, String preferredAfterID) {
/*  941 */     return null;
/*      */   }
/*      */   
/*      */   public CTabFolder getTabFolder() {
/*  945 */     return this.tabFolder;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMaximizeVisible(final boolean visible)
/*      */   {
/*  952 */     this.maximizeVisible = visible;
/*  953 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/*  956 */         if ((TabbedMDI.this.tabFolder == null) || (TabbedMDI.this.tabFolder.isDisposed())) {
/*  957 */           return;
/*      */         }
/*  959 */         TabbedMDI.this.tabFolder.setMaximizeVisible(visible);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMinimizeVisible(final boolean visible)
/*      */   {
/*  968 */     this.minimizeVisible = visible;
/*  969 */     if (this.minimizeVisible) {
/*  970 */       boolean toMinimize = ConfigurationManager.getInstance().getBooleanParameter(this.props_prefix + ".subViews.minimized");
/*  971 */       setMinimized(toMinimize);
/*      */     }
/*  973 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/*  976 */         if ((TabbedMDI.this.tabFolder == null) || (TabbedMDI.this.tabFolder.isDisposed())) {
/*  977 */           return;
/*      */         }
/*  979 */         TabbedMDI.this.tabFolder.setMinimizeVisible(visible);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean getMinimized()
/*      */   {
/*  988 */     return this.minimized;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMinimized(final boolean minimized)
/*      */   {
/*  995 */     this.minimized = minimized;
/*  996 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/*  999 */         if ((TabbedMDI.this.tabFolder == null) || (TabbedMDI.this.tabFolder.isDisposed())) {
/* 1000 */           return;
/*      */         }
/*      */         
/* 1003 */         if (minimized) {
/* 1004 */           TabbedMDI.this.minimize();
/*      */         } else {
/* 1006 */           TabbedMDI.this.restore();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public int getFolderHeight() {
/* 1013 */     return this.iFolderHeightAdj;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object dataSourceChanged(SWTSkinObject skinObject, final Object ds)
/*      */   {
/* 1022 */     Utils.execSWTThread(new Runnable() {
/*      */       public void run() {
/* 1024 */         if ((TabbedMDI.this.tabFolder == null) || (TabbedMDI.this.tabFolder.isDisposed())) {
/* 1025 */           return;
/*      */         }
/*      */         
/* 1028 */         if ((ds instanceof Object[])) {
/* 1029 */           Object[] temp = (Object[])ds;
/* 1030 */           if (temp.length == 1) {
/* 1031 */             Object obj = temp[0];
/*      */             
/* 1033 */             if ((obj instanceof DownloadManager)) {
/* 1034 */               TabbedMDI.this.maximizeTo = ((DownloadManager)obj);
/* 1035 */             } else if ((obj instanceof Download)) {
/* 1036 */               TabbedMDI.this.maximizeTo = org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils.unwrap((Download)obj);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1041 */         TabbedMDI.this.setMaximizeVisible(TabbedMDI.this.maximizeTo != null);
/*      */       }
/*      */       
/*      */ 
/* 1045 */     });
/* 1046 */     return super.dataSourceChanged(skinObject, ds);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void parameterChanged(String parameterName)
/*      */   {
/* 1053 */     if (isDisposed()) {
/* 1054 */       return;
/*      */     }
/*      */     
/* 1057 */     this.mapUserClosedTabs = COConfigurationManager.getMapParameter(parameterName, new HashMap());
/*      */     
/* 1059 */     for (Object id : this.mapUserClosedTabs.keySet()) {
/* 1060 */       String view_id = (String)id;
/* 1061 */       if (entryExists(view_id)) {
/* 1062 */         closeEntry(view_id);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTabbedMdiMaximizeListener(TabbedMdiMaximizeListener l)
/*      */   {
/* 1071 */     this.maximizeListener = l;
/*      */   }
/*      */   
/*      */   public Image obfusticatedImage(Image image)
/*      */   {
/* 1076 */     MdiEntry[] entries = getEntries();
/* 1077 */     for (MdiEntry entry : entries) {
/* 1078 */       if ((entry instanceof ObfusticateImage)) {
/* 1079 */         ObfusticateImage oi = (ObfusticateImage)entry;
/* 1080 */         image = oi.obfusticatedImage(image);
/*      */       }
/*      */     }
/* 1083 */     return image;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected MdiEntry createEntryByCreationListener(String id, Object ds, Map<?, ?> autoOpenMap)
/*      */   {
/* 1090 */     final TabbedEntry result = (TabbedEntry)super.createEntryByCreationListener(id, ds, autoOpenMap);
/*      */     
/* 1092 */     if (result != null) {
/* 1093 */       PluginManager pm = AzureusCoreFactory.getSingleton().getPluginManager();
/* 1094 */       PluginInterface pi = pm.getDefaultPluginInterface();
/* 1095 */       UIManager uim = pi.getUIManager();
/* 1096 */       MenuManager menuManager = uim.getMenuManager();
/* 1097 */       org.gudy.azureus2.plugins.ui.menus.MenuItem menuItem = menuManager.addMenuItem(id + "._end_", "menu.pop.out");
/*      */       
/* 1099 */       menuItem.addFillListener(new org.gudy.azureus2.plugins.ui.menus.MenuItemFillListener()
/*      */       {
/*      */ 
/*      */         public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data)
/*      */         {
/* 1104 */           menu.setVisible(result.canBuildStandAlone());
/*      */         }
/*      */         
/* 1107 */       });
/* 1108 */       menuItem.addListener(new MenuItemListener()
/*      */       {
/*      */         public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 1111 */           SkinnedDialog skinnedDialog = new SkinnedDialog("skin3_dlg_sidebar_popout", "shell", null, 3184);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1118 */           SWTSkin skin = skinnedDialog.getSkin();
/*      */           
/* 1120 */           SWTSkinObjectContainer cont = result.buildStandAlone((SWTSkinObjectContainer)skin.getSkinObject("content-area"));
/*      */           
/* 1122 */           if (cont != null)
/*      */           {
/* 1124 */             Object ds = result.getDatasource();
/*      */             
/* 1126 */             if ((ds instanceof Object[]))
/*      */             {
/* 1128 */               Object[] temp = (Object[])ds;
/*      */               
/* 1130 */               if (temp.length > 0)
/*      */               {
/* 1132 */                 ds = temp[0];
/*      */               }
/*      */             }
/*      */             
/* 1136 */             String ds_str = "";
/*      */             
/* 1138 */             if ((ds instanceof Download))
/*      */             {
/* 1140 */               ds_str = ((Download)ds).getName();
/*      */             }
/* 1142 */             else if ((ds instanceof DownloadManager))
/*      */             {
/* 1144 */               ds_str = ((DownloadManager)ds).getDisplayName();
/*      */             }
/*      */             
/* 1147 */             skinnedDialog.setTitle(result.getTitle() + (ds_str.length() == 0 ? "" : new StringBuilder().append(" - ").append(ds_str).toString()));
/*      */             
/* 1149 */             skinnedDialog.open();
/*      */           }
/*      */           else
/*      */           {
/* 1153 */             skinnedDialog.close();
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/* 1160 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public void fillMenu(Menu menu, MdiEntry entry, String menuID)
/*      */   {
/* 1166 */     super.fillMenu(menu, entry, menuID);
/*      */     
/* 1168 */     if (entry != null) {
/* 1169 */       org.gudy.azureus2.plugins.ui.menus.MenuItem[] menu_items = MenuItemManager.getInstance().getAllAsArray(entry.getId() + "._end_");
/*      */       
/* 1171 */       if (menu_items.length > 0)
/*      */       {
/* 1173 */         MenuBuildUtils.addPluginMenuItems(menu_items, menu, false, true, new MenuBuildUtils.MenuItemPluginMenuControllerImpl(new Object[] { entry }));
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/mdi/TabbedMDI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */