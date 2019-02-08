/*     */ package com.aelitis.azureus.ui.swt.subscriptions;
/*     */ 
/*     */ import com.aelitis.azureus.core.metasearch.Engine;
/*     */ import com.aelitis.azureus.core.subs.Subscription;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionManager;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionManagerFactory;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionManagerListener;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileComponent;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableLifeCycleListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableSelectionAdapter;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*     */ import com.aelitis.azureus.ui.swt.columns.subscriptions.ColumnSubscriptionAutoDownload;
/*     */ import com.aelitis.azureus.ui.swt.columns.subscriptions.ColumnSubscriptionCategory;
/*     */ import com.aelitis.azureus.ui.swt.columns.subscriptions.ColumnSubscriptionEnabled;
/*     */ import com.aelitis.azureus.ui.swt.columns.subscriptions.ColumnSubscriptionError;
/*     */ import com.aelitis.azureus.ui.swt.columns.subscriptions.ColumnSubscriptionLastChecked;
/*     */ import com.aelitis.azureus.ui.swt.columns.subscriptions.ColumnSubscriptionMaxResults;
/*     */ import com.aelitis.azureus.ui.swt.columns.subscriptions.ColumnSubscriptionName;
/*     */ import com.aelitis.azureus.ui.swt.columns.subscriptions.ColumnSubscriptionNbNewResults;
/*     */ import com.aelitis.azureus.ui.swt.columns.subscriptions.ColumnSubscriptionNbResults;
/*     */ import com.aelitis.azureus.ui.swt.columns.subscriptions.ColumnSubscriptionNew;
/*     */ import com.aelitis.azureus.ui.swt.columns.subscriptions.ColumnSubscriptionParent;
/*     */ import com.aelitis.azureus.ui.swt.columns.subscriptions.ColumnSubscriptionSubscribers;
/*     */ import com.aelitis.azureus.ui.swt.columns.subscriptions.ColumnSubscriptionTag;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.events.KeyEvent;
/*     */ import org.eclipse.swt.events.KeyListener;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Link;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SubscriptionsView
/*     */   implements SubscriptionManagerListener, UIPluginViewToolBarListener, UISWTViewCoreEventListener
/*     */ {
/*     */   protected static final String TABLE_ID = "subscriptions";
/*     */   private TableViewSWT view;
/*     */   private Composite viewComposite;
/*     */   private Font textFont1;
/*     */   private Font textFont2;
/*     */   private UISWTView swtView;
/*     */   
/*     */   public void associationsChanged(byte[] association_hash) {}
/*     */   
/*     */   public void subscriptionSelected(Subscription subscription) {}
/*     */   
/*     */   public void subscriptionRequested(URL url, Map<String, Object> options) {}
/*     */   
/*     */   public void subscriptionAdded(Subscription subscription)
/*     */   {
/* 125 */     if (subscription.isSubscribed()) {
/* 126 */       this.view.addDataSource(subscription);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void subscriptionRemoved(Subscription subscription)
/*     */   {
/* 134 */     this.view.removeDataSource(subscription);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void subscriptionChanged(Subscription subscription)
/*     */   {
/* 142 */     if (!subscription.isSubscribed()) {
/* 143 */       subscriptionRemoved(subscription);
/* 144 */     } else if (this.view.getRow(subscription) == null) {
/* 145 */       subscriptionAdded(subscription);
/*     */     } else {
/* 147 */       this.view.refreshTable(true);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void refreshToolBarItems(Map<String, Long> list)
/*     */   {
/* 155 */     if (this.view == null) {
/* 156 */       return;
/*     */     }
/* 158 */     int numRows = this.view.getSelectedRowsSize();
/* 159 */     list.put("remove", Long.valueOf(numRows > 0 ? 1L : 0L));
/* 160 */     list.put("share", Long.valueOf(numRows == 1 ? 1L : 0L));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*     */   {
/* 168 */     if ("remove".equals(item.getID())) {
/* 169 */       removeSelected();
/* 170 */       return true;
/*     */     }
/* 172 */     return false;
/*     */   }
/*     */   
/*     */   private void removeSelected()
/*     */   {
/* 177 */     TableRowCore[] rows = this.view.getSelectedRows();
/* 178 */     Subscription[] subs = new Subscription[rows.length];
/* 179 */     int i = 0;
/* 180 */     for (Subscription subscription : subs) {
/* 181 */       subs[i] = ((Subscription)rows[(i++)].getDataSource());
/*     */     }
/* 183 */     removeSubs(subs, 0);
/*     */   }
/*     */   
/*     */   private void removeSubs(final Subscription[] toRemove, final int startIndex) {
/* 187 */     if ((toRemove == null) || (startIndex >= toRemove.length)) {
/* 188 */       return;
/*     */     }
/*     */     
/* 191 */     if (toRemove[startIndex] == null) {
/* 192 */       int nextIndex = startIndex + 1;
/* 193 */       if (nextIndex < toRemove.length) {
/* 194 */         removeSubs(toRemove, nextIndex);
/*     */       }
/* 196 */       return;
/*     */     }
/*     */     
/* 199 */     MessageBoxShell mb = new MessageBoxShell(MessageText.getString("message.confirm.delete.title"), MessageText.getString("message.confirm.delete.text", new String[] { toRemove[startIndex].getName() }));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 205 */     if (startIndex == toRemove.length - 1) {
/* 206 */       mb.setButtons(0, new String[] { MessageText.getString("Button.yes"), MessageText.getString("Button.no") }, new Integer[] { Integer.valueOf(0), Integer.valueOf(1) });
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 211 */       mb.setButtons(1, new String[] { MessageText.getString("Button.removeAll"), MessageText.getString("Button.yes"), MessageText.getString("Button.no") }, new Integer[] { Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1) });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 218 */     mb.open(new UserPrompterResultListener() {
/*     */       public void prompterClosed(int result) {
/* 220 */         if (result == 0) {
/* 221 */           toRemove[startIndex].remove();
/* 222 */         } else if (result == 2) {
/* 223 */           for (int i = startIndex; i < toRemove.length; i++) {
/* 224 */             if (toRemove[i] != null) {
/* 225 */               toRemove[i].remove();
/*     */             }
/*     */           }
/* 228 */           return;
/*     */         }
/*     */         
/* 231 */         int nextIndex = startIndex + 1;
/* 232 */         if (nextIndex < toRemove.length) {
/* 233 */           SubscriptionsView.this.removeSubs(toRemove, nextIndex);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void delete()
/*     */   {
/* 241 */     if ((this.viewComposite != null) && (!this.viewComposite.isDisposed())) {
/* 242 */       this.viewComposite.dispose();
/*     */     }
/* 244 */     if ((this.textFont1 != null) && (!this.textFont1.isDisposed())) {
/* 245 */       this.textFont1.dispose();
/*     */     }
/* 247 */     if ((this.textFont2 != null) && (!this.textFont2.isDisposed())) {
/* 248 */       this.textFont2.dispose();
/*     */     }
/*     */   }
/*     */   
/*     */   private Composite getComposite() {
/* 253 */     return this.viewComposite;
/*     */   }
/*     */   
/*     */   private String getFullTitle() {
/* 257 */     return MessageText.getString("subscriptions.overview");
/*     */   }
/*     */   
/*     */   private void initialize(Composite parent)
/*     */   {
/* 262 */     this.viewComposite = new Composite(parent, 0);
/* 263 */     this.viewComposite.setLayout(new FormLayout());
/*     */     
/* 265 */     TableColumnCore[] columns = { new ColumnSubscriptionNew("subscriptions"), new ColumnSubscriptionName("subscriptions"), new ColumnSubscriptionNbNewResults("subscriptions"), new ColumnSubscriptionNbResults("subscriptions"), new ColumnSubscriptionMaxResults("subscriptions"), new ColumnSubscriptionLastChecked("subscriptions"), new ColumnSubscriptionSubscribers("subscriptions"), new ColumnSubscriptionEnabled("subscriptions"), new ColumnSubscriptionAutoDownload("subscriptions"), new ColumnSubscriptionCategory("subscriptions"), new ColumnSubscriptionTag("subscriptions"), new ColumnSubscriptionParent("subscriptions"), new ColumnSubscriptionError("subscriptions") };
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
/* 282 */     TableColumnManager tcm = TableColumnManager.getInstance();
/* 283 */     tcm.setDefaultColumnNames("subscriptions", new String[] { "new", ColumnSubscriptionName.COLUMN_ID, ColumnSubscriptionNbNewResults.COLUMN_ID, ColumnSubscriptionNbResults.COLUMN_ID, ColumnSubscriptionAutoDownload.COLUMN_ID });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 291 */     this.view = TableViewFactory.createTableViewSWT(Subscription.class, "subscriptions", "subscriptions", columns, "name", 268500994);
/*     */     
/*     */ 
/* 294 */     this.view.addLifeCycleListener(new TableLifeCycleListener() {
/*     */       public void tableViewInitialized() {
/* 296 */         SubscriptionManagerFactory.getSingleton().addListener(SubscriptionsView.this);
/*     */         
/* 298 */         SubscriptionsView.this.view.addDataSources(SubscriptionManagerFactory.getSingleton().getSubscriptions(true));
/*     */       }
/*     */       
/*     */       public void tableViewDestroyed() {
/* 302 */         SubscriptionManagerFactory.getSingleton().removeListener(SubscriptionsView.this);
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 307 */     });
/* 308 */     this.view.addSelectionListener(new TableSelectionAdapter()
/*     */     {
/* 310 */       PluginInterface pi = PluginInitializer.getDefaultInterface();
/* 311 */       UIManager uim = this.pi.getUIManager();
/*     */       
/* 313 */       MenuManager menu_manager = this.uim.getMenuManager();
/* 314 */       TableManager table_manager = this.uim.getTableManager();
/*     */       
/* 316 */       ArrayList<TableContextMenuItem> menu_items = new ArrayList();
/*     */       
/* 318 */       SubscriptionManagerUI.MenuCreator menu_creator = new SubscriptionManagerUI.MenuCreator()
/*     */       {
/*     */ 
/*     */ 
/*     */         public MenuItem createMenu(String resource_id)
/*     */         {
/*     */ 
/* 325 */           TableContextMenuItem menu = SubscriptionsView.3.this.table_manager.addContextMenuItem("subscriptions", resource_id);
/*     */           
/*     */ 
/* 328 */           SubscriptionsView.3.this.menu_items.add(menu);
/*     */           
/* 330 */           return menu;
/*     */         }
/*     */         
/*     */ 
/*     */         public void refreshView() {}
/*     */       };
/*     */       
/*     */ 
/*     */       public void defaultSelected(TableRowCore[] rows, int stateMask)
/*     */       {
/* 340 */         if (rows.length == 1) {
/* 341 */           TableRowCore row = rows[0];
/*     */           
/* 343 */           Subscription sub = (Subscription)row.getDataSource();
/* 344 */           if (sub == null) {
/* 345 */             return;
/*     */           }
/*     */           
/* 348 */           if (sub.isSearchTemplate())
/*     */           {
/*     */             try {
/* 351 */               VuzeFile vf = sub.getSearchTemplateVuzeFile();
/*     */               
/* 353 */               if (vf != null)
/*     */               {
/* 355 */                 sub.setSubscribed(true);
/*     */                 
/* 357 */                 VuzeFileHandler.getSingleton().handleFiles(new VuzeFile[] { vf }, 0);
/*     */                 
/*     */ 
/* 360 */                 for (VuzeFileComponent comp : vf.getComponents())
/*     */                 {
/* 362 */                   Engine engine = (Engine)comp.getData(Engine.VUZE_FILE_COMPONENT_ENGINE_KEY);
/*     */                   
/* 364 */                   if ((engine != null) && ((engine.getSelectionState() == 0) || (engine.getSelectionState() == 3)))
/*     */                   {
/*     */ 
/*     */ 
/* 368 */                     engine.setSelectionState(2);
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */             catch (Throwable e) {
/* 374 */               Debug.out(e);
/*     */             }
/*     */           }
/*     */           else {
/* 378 */             String key = "Subscription_" + ByteFormatter.encodeString(sub.getPublicKey());
/* 379 */             MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 380 */             if (mdi != null) {
/* 381 */               mdi.showEntryByID(key);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */       public void selected(TableRowCore[] rows)
/*     */       {
/* 389 */         rows = SubscriptionsView.this.view.getSelectedRows();
/* 390 */         ISelectedContent[] sels = new ISelectedContent[rows.length];
/*     */         
/* 392 */         List<Subscription> subs = new ArrayList();
/*     */         
/* 394 */         for (int i = 0; i < rows.length; i++)
/*     */         {
/* 396 */           Subscription sub = (Subscription)rows[i].getDataSource();
/*     */           
/* 398 */           sels[i] = new SubscriptionSelectedContent(sub);
/*     */           
/* 400 */           if (sub != null)
/*     */           {
/* 402 */             subs.add(sub);
/*     */           }
/*     */         }
/*     */         
/* 406 */         SelectedContentManager.changeCurrentlySelectedContent(SubscriptionsView.this.view.getTableID(), sels, SubscriptionsView.this.view);
/*     */         
/* 408 */         for (TableContextMenuItem mi : this.menu_items)
/*     */         {
/* 410 */           mi.remove();
/*     */         }
/*     */         
/* 413 */         if (subs.size() > 0)
/*     */         {
/* 415 */           SubscriptionManagerUI.createMenus(this.menu_manager, this.menu_creator, (Subscription[])subs.toArray(new Subscription[0])); } } }, false);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 421 */     this.view.addKeyListener(new KeyListener()
/*     */     {
/*     */       public void keyPressed(KeyEvent event) {}
/*     */       
/*     */       public void keyReleased(KeyEvent event)
/*     */       {
/* 427 */         if (event.keyCode == 127) {
/* 428 */           SubscriptionsView.this.removeSelected();
/*     */         }
/*     */         
/*     */       }
/* 432 */     });
/* 433 */     this.view.setRowDefaultHeightEM(1.4F);
/*     */     
/* 435 */     this.view.initialize(this.viewComposite);
/*     */     
/* 437 */     final Composite composite = new Composite(this.viewComposite, 2048);
/* 438 */     composite.setBackgroundMode(1);
/* 439 */     composite.setBackground(ColorCache.getColor(composite.getDisplay(), "#F1F9F8"));
/*     */     
/* 441 */     Font font = composite.getFont();
/* 442 */     FontData[] fDatas = font.getFontData();
/* 443 */     for (int i = 0; i < fDatas.length; i++) {
/* 444 */       fDatas[i].setHeight(150 * fDatas[i].getHeight() / 100);
/* 445 */       if (Constants.isWindows) {
/* 446 */         fDatas[i].setStyle(1);
/*     */       }
/*     */     }
/*     */     
/* 450 */     this.textFont1 = new Font(composite.getDisplay(), fDatas);
/*     */     
/* 452 */     fDatas = font.getFontData();
/* 453 */     for (int i = 0; i < fDatas.length; i++) {
/* 454 */       fDatas[i].setHeight(120 * fDatas[i].getHeight() / 100);
/*     */     }
/*     */     
/* 457 */     this.textFont2 = new Font(composite.getDisplay(), fDatas);
/*     */     
/* 459 */     Label preText = new Label(composite, 0);
/* 460 */     preText.setForeground(ColorCache.getColor(composite.getDisplay(), "#6D6F6E"));
/* 461 */     preText.setFont(this.textFont1);
/* 462 */     preText.setText(MessageText.getString("subscriptions.view.help.1"));
/*     */     
/* 464 */     Label image = new Label(composite, 0);
/* 465 */     ImageLoader.getInstance().setLabelImage(image, "btn_rss_add");
/*     */     
/* 467 */     Link postText = new Link(composite, 0);
/* 468 */     postText.setForeground(ColorCache.getColor(composite.getDisplay(), "#6D6F6E"));
/* 469 */     postText.setFont(this.textFont2);
/* 470 */     postText.setText(MessageText.getString("subscriptions.view.help.2"));
/*     */     
/* 472 */     postText.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 474 */         if ((event.text != null) && ((event.text.startsWith("http://")) || (event.text.startsWith("https://")))) {
/* 475 */           Utils.launch(event.text);
/*     */         }
/*     */         
/*     */       }
/* 479 */     });
/* 480 */     Label close = new Label(composite, 0);
/* 481 */     ImageLoader.getInstance().setLabelImage(close, "image.dismissX");
/* 482 */     close.setCursor(composite.getDisplay().getSystemCursor(21));
/* 483 */     close.addListener(4, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 485 */         COConfigurationManager.setParameter("subscriptions.view.showhelp", false);
/* 486 */         composite.setVisible(false);
/* 487 */         FormData data = (FormData)SubscriptionsView.this.view.getComposite().getLayoutData();
/* 488 */         data.bottom = new FormAttachment(100, 0);
/* 489 */         SubscriptionsView.this.viewComposite.layout(true);
/*     */       }
/*     */       
/* 492 */     });
/* 493 */     FormLayout layout = new FormLayout();
/* 494 */     composite.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/* 498 */     FormData data = new FormData();
/* 499 */     data.left = new FormAttachment(0, 15);
/* 500 */     data.top = new FormAttachment(0, 20);
/* 501 */     data.bottom = new FormAttachment(postText, -5);
/* 502 */     preText.setLayoutData(data);
/*     */     
/* 504 */     data = new FormData();
/* 505 */     data.left = new FormAttachment(preText, 5);
/* 506 */     data.top = new FormAttachment(preText, 0, 16777216);
/* 507 */     image.setLayoutData(data);
/*     */     
/* 509 */     data = new FormData();
/* 510 */     data.left = new FormAttachment(preText, 0, 16384);
/*     */     
/* 512 */     data.bottom = new FormAttachment(100, -20);
/* 513 */     postText.setLayoutData(data);
/*     */     
/* 515 */     data = new FormData();
/* 516 */     data.right = new FormAttachment(100, -10);
/* 517 */     data.top = new FormAttachment(0, 10);
/* 518 */     close.setLayoutData(data);
/*     */     
/* 520 */     data = new FormData();
/* 521 */     data.left = new FormAttachment(0, 0);
/* 522 */     data.right = new FormAttachment(100, 0);
/* 523 */     data.top = new FormAttachment(0, 0);
/* 524 */     data.bottom = new FormAttachment(composite, 0);
/* 525 */     this.viewComposite.setLayoutData(data);
/*     */     
/* 527 */     data = new FormData();
/* 528 */     data.left = new FormAttachment(0, 0);
/* 529 */     data.right = new FormAttachment(100, 0);
/* 530 */     data.bottom = new FormAttachment(100, 0);
/* 531 */     composite.setLayoutData(data);
/*     */     
/* 533 */     COConfigurationManager.setBooleanDefault("subscriptions.view.showhelp", true);
/* 534 */     if (!COConfigurationManager.getBooleanParameter("subscriptions.view.showhelp")) {
/* 535 */       composite.setVisible(false);
/* 536 */       data = (FormData)this.viewComposite.getLayoutData();
/* 537 */       data.bottom = new FormAttachment(100, 0);
/* 538 */       this.viewComposite.layout(true);
/*     */     }
/*     */   }
/*     */   
/*     */   private void refresh() {
/* 543 */     if (this.view != null) {
/* 544 */       this.view.refreshTable(false);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 549 */     switch (event.getType()) {
/*     */     case 0: 
/* 551 */       this.swtView = ((UISWTView)event.getData());
/* 552 */       this.swtView.setTitle(getFullTitle());
/* 553 */       break;
/*     */     
/*     */     case 7: 
/* 556 */       delete();
/* 557 */       break;
/*     */     
/*     */     case 2: 
/* 560 */       initialize((Composite)event.getData());
/* 561 */       break;
/*     */     
/*     */     case 6: 
/* 564 */       Messages.updateLanguageForControl(getComposite());
/* 565 */       this.swtView.setTitle(getFullTitle());
/* 566 */       break;
/*     */     
/*     */     case 1: 
/*     */       break;
/*     */     
/*     */     case 3: 
/*     */       break;
/*     */     
/*     */ 
/*     */     case 5: 
/* 576 */       refresh();
/*     */     }
/*     */     
/*     */     
/* 580 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/subscriptions/SubscriptionsView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */