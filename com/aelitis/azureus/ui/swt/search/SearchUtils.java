/*     */ package com.aelitis.azureus.ui.swt.search;
/*     */ 
/*     */ import com.aelitis.azureus.core.metasearch.Engine;
/*     */ import com.aelitis.azureus.core.metasearch.MetaSearch;
/*     */ import com.aelitis.azureus.core.metasearch.MetaSearchManager;
/*     */ import com.aelitis.azureus.core.metasearch.MetaSearchManagerFactory;
/*     */ import com.aelitis.azureus.core.metasearch.impl.plugin.PluginEngine;
/*     */ import com.aelitis.azureus.core.metasearch.impl.web.WebEngine;
/*     */ import com.aelitis.azureus.core.subs.Subscription;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionException;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionHistory;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionManager;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionManagerFactory;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectCheckbox;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectCombo;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectTextbox;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinnedDialog;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.StandardButtonsArea;
/*     */ import com.aelitis.azureus.util.JSONUtils;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.dnd.Clipboard;
/*     */ import org.eclipse.swt.dnd.TextTransfer;
/*     */ import org.eclipse.swt.events.MenuAdapter;
/*     */ import org.eclipse.swt.events.MenuEvent;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.FileDialog;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemFillListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils.ChatKeyResolver;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.PropertiesWindow;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
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
/*     */ public class SearchUtils
/*     */ {
/*     */   public static void addMenus(Menu menu)
/*     */   {
/*  78 */     Menu template_menu = new Menu(menu.getShell(), 4);
/*     */     
/*  80 */     org.eclipse.swt.widgets.MenuItem template_menu_item = new org.eclipse.swt.widgets.MenuItem(menu, 64);
/*     */     
/*  82 */     template_menu_item.setMenu(template_menu);
/*     */     
/*  84 */     Messages.setLanguageText(template_menu_item, "Search.menu.engines");
/*     */     
/*  86 */     template_menu.addMenuListener(new MenuAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void menuShown(MenuEvent e)
/*     */       {
/*     */ 
/*  93 */         for (org.eclipse.swt.widgets.MenuItem mi : this.val$template_menu.getItems())
/*     */         {
/*  95 */           mi.dispose();
/*     */         }
/*     */         
/*  98 */         org.eclipse.swt.widgets.MenuItem import_mi = new org.eclipse.swt.widgets.MenuItem(this.val$template_menu, 8);
/*     */         
/* 100 */         Messages.setLanguageText(import_mi, "menu.import.json.from.clipboard");
/*     */         
/* 102 */         import_mi.addSelectionListener(new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {}
/*     */ 
/* 106 */         });
/* 107 */         new org.eclipse.swt.widgets.MenuItem(this.val$template_menu, 2);
/*     */         
/* 109 */         Engine[] engines = MetaSearchManagerFactory.getSingleton().getMetaSearch().getEngines(true, false);
/*     */         
/* 111 */         Arrays.sort(engines, new Comparator()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public int compare(Engine o1, Engine o2)
/*     */           {
/*     */ 
/*     */ 
/* 120 */             return o1.getName().compareToIgnoreCase(o2.getName());
/*     */           }
/*     */         });
/*     */         
/* 124 */         for (int i = 0; i < engines.length; i++)
/*     */         {
/* 126 */           Engine engine = engines[i];
/*     */           
/* 128 */           Menu engine_menu = new Menu(this.val$template_menu.getShell(), 4);
/*     */           
/* 130 */           org.eclipse.swt.widgets.MenuItem engine_menu_item = new org.eclipse.swt.widgets.MenuItem(this.val$template_menu, 64);
/*     */           
/* 132 */           engine_menu_item.setMenu(engine_menu);
/*     */           
/* 134 */           engine_menu_item.setText(engine.getName());
/*     */           
/* 136 */           SearchUtils.addMenus(engine_menu, engine, false);
/*     */         }
/*     */         
/*     */       }
/* 140 */     });
/* 141 */     MenuBuildUtils.addChatMenu(menu, "label.chat", "Search Templates");
/*     */     
/* 143 */     org.eclipse.swt.widgets.MenuItem itemExport = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*     */     
/* 145 */     Messages.setLanguageText(itemExport, "search.export.all");
/*     */     
/* 147 */     itemExport.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */       public void widgetSelected(SelectionEvent e) {}
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addMenus(Menu engine_menu, Engine engine, boolean separator_required)
/*     */   {
/* 160 */     if (separator_required)
/*     */     {
/* 162 */       new org.eclipse.swt.widgets.MenuItem(engine_menu, 2);
/*     */       
/* 164 */       separator_required = false;
/*     */     }
/*     */     
/* 167 */     if (!(engine instanceof PluginEngine))
/*     */     {
/* 169 */       org.eclipse.swt.widgets.MenuItem export_json = new org.eclipse.swt.widgets.MenuItem(engine_menu, 8);
/*     */       
/* 171 */       Messages.setLanguageText(export_json, "menu.export.json.to.clipboard");
/*     */       
/* 173 */       export_json.addSelectionListener(new SelectionAdapter() {
/*     */         public void widgetSelected(SelectionEvent e) {
/* 175 */           Shell shell = Utils.findAnyShell();
/*     */           
/* 177 */           shell.getDisplay().asyncExec(new AERunnable()
/*     */           {
/*     */ 
/*     */             public void runSupport()
/*     */             {
/*     */               try
/*     */               {
/* 184 */                 ClipboardCopy.copyToClipBoard(SearchUtils.3.this.val$engine.exportToVuzeFile().exportToJSON());
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 188 */                 Debug.out(e);
/*     */               }
/*     */               
/*     */             }
/*     */           });
/*     */         }
/* 194 */       });
/* 195 */       Subscription subs = engine.getSubscription();
/*     */       
/* 197 */       if (subs != null)
/*     */       {
/* 199 */         org.eclipse.swt.widgets.MenuItem export_uri = new org.eclipse.swt.widgets.MenuItem(engine_menu, 8);
/*     */         
/* 201 */         Messages.setLanguageText(export_uri, "label.copy.uri.to.clip");
/*     */         
/* 203 */         export_uri.addSelectionListener(new SelectionAdapter() {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 205 */             Shell shell = Utils.findAnyShell();
/*     */             
/* 207 */             shell.getDisplay().asyncExec(new AERunnable()
/*     */             {
/*     */ 
/*     */               public void runSupport()
/*     */               {
/*     */                 try
/*     */                 {
/* 214 */                   ClipboardCopy.copyToClipBoard(SearchUtils.4.this.val$subs.getURI());
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 218 */                   Debug.out(e);
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/*     */         });
/*     */       }
/* 225 */       new org.eclipse.swt.widgets.MenuItem(engine_menu, 2);
/*     */       
/* 227 */       org.eclipse.swt.widgets.MenuItem remove_item = new org.eclipse.swt.widgets.MenuItem(engine_menu, 8);
/*     */       
/* 229 */       Messages.setLanguageText(remove_item, "Button.remove");
/*     */       
/* 231 */       Utils.setMenuItemImage(remove_item, "delete");
/*     */       
/* 233 */       remove_item.addSelectionListener(new SelectionAdapter() {
/*     */         public void widgetSelected(SelectionEvent e) {
/* 235 */           this.val$engine.setSelectionState(3);
/*     */         }
/*     */         
/* 238 */       });
/* 239 */       separator_required = true;
/*     */     }
/*     */     
/* 242 */     if (separator_required)
/*     */     {
/* 244 */       new org.eclipse.swt.widgets.MenuItem(engine_menu, 2);
/*     */       
/* 246 */       separator_required = false;
/*     */     }
/*     */     
/* 249 */     org.eclipse.swt.widgets.MenuItem show_props = new org.eclipse.swt.widgets.MenuItem(engine_menu, 8);
/*     */     
/* 251 */     Messages.setLanguageText(show_props, "Subscription.menu.properties");
/*     */     
/* 253 */     show_props.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 255 */         SearchUtils.showProperties(this.val$engine);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public static void addMenus(final MenuManager menuManager)
/*     */   {
/* 263 */     org.gudy.azureus2.plugins.ui.menus.MenuItem template_menu = menuManager.addMenuItem("sidebar.Search", "Search.menu.engines");
/*     */     
/* 265 */     template_menu.setStyle(5);
/*     */     
/* 267 */     template_menu.addFillListener(new MenuItemFillListener()
/*     */     {
/*     */ 
/*     */       public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data)
/*     */       {
/* 272 */         this.val$template_menu.removeAllChildItems();
/*     */         
/* 274 */         Engine[] engines = MetaSearchManagerFactory.getSingleton().getMetaSearch().getEngines(true, false);
/*     */         
/* 276 */         Arrays.sort(engines, new Comparator()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public int compare(Engine o1, Engine o2)
/*     */           {
/*     */ 
/*     */ 
/* 285 */             return o1.getName().compareToIgnoreCase(o2.getName());
/*     */           }
/*     */           
/* 288 */         });
/* 289 */         org.gudy.azureus2.plugins.ui.menus.MenuItem import_menu = menuManager.addMenuItem(this.val$template_menu, "menu.import.json.from.clipboard");
/*     */         
/* 291 */         import_menu.addListener(new MenuItemListener()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 302 */         });
/* 303 */         org.gudy.azureus2.plugins.ui.menus.MenuItem sep = menuManager.addMenuItem(this.val$template_menu, "!sep!");
/*     */         
/* 305 */         sep.setStyle(4);
/*     */         
/* 307 */         for (int i = 0; i < engines.length; i++)
/*     */         {
/* 309 */           final Engine engine = engines[i];
/*     */           
/* 311 */           org.gudy.azureus2.plugins.ui.menus.MenuItem engine_menu = menuManager.addMenuItem(this.val$template_menu, "!" + engine.getName() + "!");
/*     */           
/* 313 */           engine_menu.setStyle(5);
/*     */           
/* 315 */           if (!(engine instanceof PluginEngine))
/*     */           {
/* 317 */             org.gudy.azureus2.plugins.ui.menus.MenuItem mi = menuManager.addMenuItem(engine_menu, "MyTorrentsView.menu.exportmenu");
/*     */             
/* 319 */             mi.addListener(new MenuItemListener()
/*     */             {
/*     */ 
/*     */ 
/*     */               public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*     */               {
/*     */ 
/*     */ 
/* 327 */                 final Shell shell = Utils.findAnyShell();
/*     */                 
/* 329 */                 shell.getDisplay().asyncExec(new AERunnable()
/*     */                 {
/*     */ 
/*     */                   public void runSupport()
/*     */                   {
/*     */ 
/* 335 */                     FileDialog dialog = new FileDialog(shell, 139264);
/*     */                     
/*     */ 
/* 338 */                     dialog.setFilterPath(TorrentOpener.getFilterPathData());
/*     */                     
/* 340 */                     dialog.setText(MessageText.getString("metasearch.export.select.template.file"));
/*     */                     
/* 342 */                     dialog.setFilterExtensions(new String[] { "*.vuze", "*.vuz", Constants.FILE_WILDCARD });
/*     */                     
/*     */ 
/*     */ 
/*     */ 
/* 347 */                     dialog.setFilterNames(new String[] { "*.vuze", "*.vuz", Constants.FILE_WILDCARD });
/*     */                     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 353 */                     String path = TorrentOpener.setFilterPathData(dialog.open());
/*     */                     
/* 355 */                     if (path != null)
/*     */                     {
/* 357 */                       String lc = path.toLowerCase();
/*     */                       
/* 359 */                       if ((!lc.endsWith(".vuze")) && (!lc.endsWith(".vuz")))
/*     */                       {
/* 361 */                         path = path + ".vuze";
/*     */                       }
/*     */                       try
/*     */                       {
/* 365 */                         SearchUtils.7.3.this.val$engine.exportToVuzeFile(new File(path));
/*     */                       }
/*     */                       catch (Throwable e)
/*     */                       {
/* 369 */                         Debug.out(e);
/*     */                       }
/*     */                       
/*     */                     }
/*     */                   }
/*     */                 });
/*     */               }
/* 376 */             });
/* 377 */             org.gudy.azureus2.plugins.ui.menus.MenuItem copy_mi = menuManager.addMenuItem(engine_menu, "menu.export.json.to.clipboard");
/*     */             
/* 379 */             copy_mi.addListener(new MenuItemListener()
/*     */             {
/*     */ 
/*     */ 
/*     */               public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*     */               {
/*     */ 
/*     */ 
/* 387 */                 Shell shell = Utils.findAnyShell();
/*     */                 
/* 389 */                 shell.getDisplay().asyncExec(new AERunnable()
/*     */                 {
/*     */ 
/*     */                   public void runSupport()
/*     */                   {
/*     */                     try
/*     */                     {
/* 396 */                       ClipboardCopy.copyToClipBoard(SearchUtils.7.4.this.val$engine.exportToVuzeFile().exportToJSON());
/*     */                     }
/*     */                     catch (Throwable e)
/*     */                     {
/* 400 */                       Debug.out(e);
/*     */                     }
/*     */                     
/*     */                   }
/*     */                 });
/*     */               }
/* 406 */             });
/* 407 */             final Subscription subs = engine.getSubscription();
/*     */             
/* 409 */             if (subs != null)
/*     */             {
/* 411 */               org.gudy.azureus2.plugins.ui.menus.MenuItem copy_uri = menuManager.addMenuItem(engine_menu, "label.copy.uri.to.clip");
/*     */               
/* 413 */               copy_uri.addListener(new MenuItemListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*     */                 {
/*     */ 
/*     */ 
/* 421 */                   Shell shell = Utils.findAnyShell();
/*     */                   
/* 423 */                   shell.getDisplay().asyncExec(new AERunnable()
/*     */                   {
/*     */ 
/*     */                     public void runSupport()
/*     */                     {
/*     */                       try
/*     */                       {
/* 430 */                         ClipboardCopy.copyToClipBoard(SearchUtils.7.5.this.val$subs.getURI());
/*     */                       }
/*     */                       catch (Throwable e)
/*     */                       {
/* 434 */                         Debug.out(e);
/*     */                       }
/*     */                     }
/*     */                   });
/*     */                 }
/*     */               });
/*     */             }
/*     */             
/* 442 */             if ((engine instanceof WebEngine))
/*     */             {
/* 444 */               final WebEngine we = (WebEngine)engine;
/*     */               
/* 446 */               if (we.isNeedsAuth())
/*     */               {
/* 448 */                 String cookies = we.getCookies();
/*     */                 
/* 450 */                 if ((cookies != null) && (cookies.length() > 0))
/*     */                 {
/* 452 */                   mi = menuManager.addMenuItem(engine_menu, "Subscription.menu.resetauth");
/*     */                   
/* 454 */                   mi.addListener(new MenuItemListener()
/*     */                   {
/*     */ 
/*     */ 
/*     */                     public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*     */                     {
/*     */ 
/*     */ 
/* 462 */                       we.setCookies(null);
/*     */                     }
/*     */                   });
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 470 */           if (!(engine instanceof PluginEngine))
/*     */           {
/* 472 */             if (engine_menu.getItems().length > 0)
/*     */             {
/* 474 */               org.gudy.azureus2.plugins.ui.menus.MenuItem mi = menuManager.addMenuItem(engine_menu, "Subscription.menu.sep");
/*     */               
/* 476 */               mi.setStyle(4);
/*     */             }
/*     */             
/* 479 */             org.gudy.azureus2.plugins.ui.menus.MenuItem mi = menuManager.addMenuItem(engine_menu, "Button.remove");
/*     */             
/* 481 */             mi.addListener(new MenuItemListener()
/*     */             {
/*     */ 
/*     */ 
/*     */               public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*     */               {
/*     */ 
/*     */ 
/* 489 */                 engine.setSelectionState(3);
/*     */               }
/*     */               
/* 492 */             });
/* 493 */             mi = menuManager.addMenuItem(engine_menu, "Subscription.menu.sep2");
/*     */             
/* 495 */             mi.setStyle(4);
/*     */           }
/*     */           
/* 498 */           if (engine_menu.getItems().length > 0)
/*     */           {
/* 500 */             org.gudy.azureus2.plugins.ui.menus.MenuItem mi = menuManager.addMenuItem(engine_menu, "Subscription.menu.sep2");
/*     */             
/* 502 */             mi.setStyle(4);
/*     */           }
/*     */           
/* 505 */           org.gudy.azureus2.plugins.ui.menus.MenuItem mi = menuManager.addMenuItem(engine_menu, "Subscription.menu.properties");
/*     */           
/* 507 */           mi.addListener(new MenuItemListener()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*     */             {
/*     */ 
/*     */ 
/* 515 */               SearchUtils.showProperties(engine);
/*     */             }
/*     */             
/*     */           });
/*     */         }
/*     */       }
/* 521 */     });
/* 522 */     org.gudy.azureus2.plugins.ui.menus.MenuItem chat_menu = menuManager.addMenuItem("sidebar.Search", "label.chat");
/*     */     
/* 524 */     MenuBuildUtils.addChatMenu(menuManager, chat_menu, new MenuBuildUtils.ChatKeyResolver()
/*     */     {
/*     */ 
/*     */ 
/*     */       public String getChatKey(Object object)
/*     */       {
/*     */ 
/* 531 */         return "Search Templates";
/*     */       }
/*     */       
/* 534 */     });
/* 535 */     org.gudy.azureus2.plugins.ui.menus.MenuItem export_menu = menuManager.addMenuItem("sidebar.Search", "search.export.all");
/*     */     
/* 537 */     export_menu.setStyle(1);
/*     */     
/* 539 */     export_menu.addListener(new MenuItemListener()
/*     */     {
/*     */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {}
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void showCreateSubscriptionDialog(final long engineID, final String searchTerm, final Map optionalFilters)
/*     */   {
/* 554 */     final SkinnedDialog dialog = new SkinnedDialog("skin3_dlg_create_search_subscription", "shell", 2144);
/*     */     
/* 556 */     SWTSkin skin = dialog.getSkin();
/*     */     
/* 558 */     SWTSkinObjectTextbox tb = (SWTSkinObjectTextbox)skin.getSkinObject("sub-name");
/*     */     
/* 560 */     final SWTSkinObjectCheckbox cbShare = (SWTSkinObjectCheckbox)skin.getSkinObject("sub-share");
/*     */     
/*     */ 
/* 563 */     final SWTSkinObjectCheckbox cbAutoDL = (SWTSkinObjectCheckbox)skin.getSkinObject("sub-autodl");
/*     */     
/*     */ 
/* 566 */     SWTSkinObject soEngineArea = skin.getSkinObject("sub-engine-area");
/* 567 */     SWTSkinObjectCombo soEngines = (SWTSkinObjectCombo)skin.getSkinObject("sub-engine");
/*     */     
/* 569 */     if ((tb == null) || (cbShare == null) || (cbAutoDL == null)) {
/* 570 */       return;
/*     */     }
/*     */     
/* 573 */     boolean hasEngineID = engineID >= 0L;
/* 574 */     soEngineArea.setVisible(!hasEngineID);
/*     */     
/* 576 */     final Map<Integer, Engine> mapEngines = new HashMap();
/* 577 */     if (!hasEngineID) {
/* 578 */       Engine[] engines = MetaSearchManagerFactory.getSingleton().getMetaSearch().getEngines(true, false);
/* 579 */       List<String> list = new ArrayList();
/* 580 */       int pos = 0;
/*     */       
/* 582 */       for (Engine engine : engines) {
/* 583 */         mapEngines.put(Integer.valueOf(pos++), engine);
/* 584 */         list.add(engine.getName());
/*     */       }
/* 586 */       soEngines.setList((String[])list.toArray(new String[list.size()]));
/*     */     }
/*     */     
/* 589 */     cbShare.setChecked(COConfigurationManager.getBooleanParameter("sub.sharing.default.checked"));
/*     */     
/* 591 */     cbAutoDL.setChecked(COConfigurationManager.getBooleanParameter("sub.autodl.default.checked"));
/*     */     
/*     */ 
/* 594 */     SWTSkinObject soButtonArea = skin.getSkinObject("bottom-area");
/* 595 */     if ((soButtonArea instanceof SWTSkinObjectContainer)) {
/* 596 */       StandardButtonsArea buttonsArea = new StandardButtonsArea()
/*     */       {
/*     */         protected void clicked(int buttonValue) {
/* 599 */           if (buttonValue == 32)
/*     */           {
/* 601 */             String name = this.val$tb.getText().trim();
/* 602 */             boolean isShared = cbShare.isChecked();
/* 603 */             boolean autoDL = cbAutoDL.isChecked();
/*     */             
/* 605 */             long realEngineID = engineID;
/* 606 */             if (engineID <= 0L) {
/* 607 */               int engineIndex = mapEngines.getComboControl().getSelectionIndex();
/* 608 */               if (engineIndex < 0)
/*     */               {
/* 610 */                 return;
/*     */               }
/* 612 */               realEngineID = ((Engine)searchTerm.get(Integer.valueOf(engineIndex))).getId();
/*     */             }
/*     */             
/* 615 */             Map<String, Object> payload = new HashMap();
/* 616 */             payload.put("engine_id", Long.valueOf(realEngineID));
/* 617 */             payload.put("search_term", optionalFilters);
/*     */             
/* 619 */             Map<String, Object> mapSchedule = new HashMap();
/* 620 */             mapSchedule.put("days", Collections.EMPTY_LIST);
/* 621 */             mapSchedule.put("interval", Integer.valueOf(120));
/* 622 */             payload.put("schedule", mapSchedule);
/*     */             
/* 624 */             Map<String, Object> mapOptions = new HashMap();
/* 625 */             mapOptions.put("auto_dl", Boolean.valueOf(autoDL));
/* 626 */             payload.put("options", mapOptions);
/*     */             
/* 628 */             Map<String, Object> mapFilters = new HashMap();
/* 629 */             if (dialog != null) {
/* 630 */               mapFilters.putAll(dialog);
/*     */             }
/*     */             
/* 633 */             payload.put("filters", mapFilters);
/*     */             
/*     */             try
/*     */             {
/* 637 */               Subscription subs = SubscriptionManagerFactory.getSingleton().create(name, isShared, JSONUtils.encodeToJSON(payload));
/*     */               
/*     */ 
/* 640 */               subs.getHistory().setDetails(true, autoDL);
/*     */               
/* 642 */               subs.requestAttention();
/*     */             }
/*     */             catch (SubscriptionException e) {}
/*     */           }
/*     */           
/*     */ 
/* 648 */           this.val$dialog.close();
/*     */         }
/* 650 */       };
/* 651 */       buttonsArea.setButtonIDs(new String[] { MessageText.getString("Button.add"), MessageText.getString("Button.cancel") });
/*     */       
/*     */ 
/*     */ 
/* 655 */       buttonsArea.setButtonVals(new Integer[] { Integer.valueOf(32), Integer.valueOf(256) });
/*     */       
/*     */ 
/*     */ 
/* 659 */       buttonsArea.swt_createButtons(((SWTSkinObjectContainer)soButtonArea).getComposite());
/*     */     }
/*     */     
/*     */ 
/* 663 */     dialog.open();
/*     */   }
/*     */   
/*     */ 
/*     */   private static void importFromClipboard()
/*     */   {
/* 669 */     Shell shell = Utils.findAnyShell();
/*     */     
/* 671 */     shell.getDisplay().asyncExec(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */         try
/*     */         {
/* 678 */           Clipboard clipboard = new Clipboard(Display.getDefault());
/*     */           
/* 680 */           String text = (String)clipboard.getContents(TextTransfer.getInstance());
/*     */           
/* 682 */           clipboard.dispose();
/*     */           
/* 684 */           if (text != null)
/*     */           {
/* 686 */             InputStream is = new ByteArrayInputStream(text.getBytes("UTF-8"));
/*     */             try
/*     */             {
/* 689 */               VuzeFileHandler vfh = VuzeFileHandler.getSingleton();
/*     */               
/* 691 */               VuzeFile vf = vfh.loadVuzeFile(is);
/*     */               
/* 693 */               if (vf != null)
/*     */               {
/* 695 */                 vfh.handleFiles(new VuzeFile[] { vf }, 0);
/*     */               }
/*     */             }
/*     */             finally {
/* 699 */               is.close();
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 704 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private static void exportAll()
/*     */   {
/* 713 */     Shell shell = Utils.findAnyShell();
/*     */     
/* 715 */     shell.getDisplay().asyncExec(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 721 */         FileDialog dialog = new FileDialog(this.val$shell, 139264);
/*     */         
/*     */ 
/* 724 */         dialog.setFilterPath(TorrentOpener.getFilterPathData());
/*     */         
/* 726 */         dialog.setText(MessageText.getString("metasearch.export.select.template.file"));
/*     */         
/* 728 */         dialog.setFilterExtensions(new String[] { "*.vuze", "*.vuz", Constants.FILE_WILDCARD });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 733 */         dialog.setFilterNames(new String[] { "*.vuze", "*.vuz", Constants.FILE_WILDCARD });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 739 */         String path = TorrentOpener.setFilterPathData(dialog.open());
/*     */         
/* 741 */         if (path != null)
/*     */         {
/* 743 */           String lc = path.toLowerCase();
/*     */           
/* 745 */           if ((!lc.endsWith(".vuze")) && (!lc.endsWith(".vuz")))
/*     */           {
/* 747 */             path = path + ".vuze";
/*     */           }
/*     */           try
/*     */           {
/* 751 */             MetaSearchManagerFactory.getSingleton().getMetaSearch().exportEngines(new File(path));
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 755 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void showProperties(Engine engine)
/*     */   {
/* 767 */     String auth_str = String.valueOf(false);
/*     */     
/* 769 */     String engine_str = engine.getNameEx();
/*     */     
/* 771 */     String url_str = null;
/*     */     
/* 773 */     if ((engine instanceof WebEngine))
/*     */     {
/* 775 */       WebEngine web_engine = (WebEngine)engine;
/*     */       
/* 777 */       if (web_engine.isNeedsAuth())
/*     */       {
/* 779 */         auth_str = String.valueOf(true) + ": cookies=" + toString(web_engine.getRequiredCookies());
/*     */       }
/*     */       
/* 782 */       url_str = web_engine.getSearchUrl();
/*     */     }
/*     */     
/* 785 */     String[] keys = { "subs.prop.template", "subs.prop.auth", "subs.prop.query", "label.anon", "subs.prop.version" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 793 */     String[] values = { engine_str, auth_str, url_str, String.valueOf(engine.isAnonymous()), String.valueOf(engine.getVersion()) };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 801 */     new PropertiesWindow(engine.getName(), keys, values);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String toString(String[] strs)
/*     */   {
/* 809 */     String res = "";
/*     */     
/* 811 */     for (int i = 0; i < strs.length; i++) {
/* 812 */       res = res + (i == 0 ? "" : ",") + strs[i];
/*     */     }
/*     */     
/* 815 */     return res;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/search/SearchUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */