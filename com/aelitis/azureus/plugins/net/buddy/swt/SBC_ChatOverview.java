/*     */ package com.aelitis.azureus.plugins.net.buddy.swt;
/*     */ 
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatInstance;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatManagerListener;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginUtils;
/*     */ import com.aelitis.azureus.plugins.net.buddy.swt.columns.ColumnChatFavorite;
/*     */ import com.aelitis.azureus.plugins.net.buddy.swt.columns.ColumnChatMessageCount;
/*     */ import com.aelitis.azureus.plugins.net.buddy.swt.columns.ColumnChatMsgOutstanding;
/*     */ import com.aelitis.azureus.plugins.net.buddy.swt.columns.ColumnChatName;
/*     */ import com.aelitis.azureus.plugins.net.buddy.swt.columns.ColumnChatStatus;
/*     */ import com.aelitis.azureus.plugins.net.buddy.swt.columns.ColumnChatUserCount;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableSelectionListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableViewFilterCheck;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryCreationListener2;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.InfoBarUtil;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinView;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerListener;
/*     */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnCreationListener;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewEventListenerHolder;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener;
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
/*     */ public class SBC_ChatOverview
/*     */   extends SkinView
/*     */   implements UIUpdatable, UIPluginViewToolBarListener, TableViewFilterCheck<BuddyPluginBeta.ChatInstance>, BuddyPluginBeta.ChatManagerListener, TableViewSWTMenuFillListener, TableSelectionListener
/*     */ {
/*  88 */   public static final int[] COLOR_MESSAGE_WITH_NICK = { 132, 16, 58 };
/*     */   
/*     */   private static final String TABLE_CHAT = "ChatsView";
/*     */   
/*  92 */   protected static final Object MDI_KEY = new Object();
/*     */   TableViewSWT<BuddyPluginBeta.ChatInstance> tv;
/*     */   private Text txtFilter;
/*     */   private Composite table_parent;
/*     */   
/*  97 */   public static void preInitialize() { UIManager ui_manager = PluginInitializer.getDefaultInterface().getUIManager();
/*     */     
/*  99 */     ui_manager.addUIListener(new UIManagerListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void UIAttached(UIInstance instance)
/*     */       {
/*     */ 
/* 106 */         MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*     */         
/* 108 */         if (mdi == null)
/*     */         {
/* 110 */           return;
/*     */         }
/*     */         
/* 113 */         mdi.registerEntry("Chat_.*", new MdiEntryCreationListener2()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           public MdiEntry createMDiEntry(MultipleDocumentInterface mdi, String id, Object datasource, Map<?, ?> params)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 124 */             BuddyPluginBeta.ChatInstance chat = null;
/*     */             
/* 126 */             if ((datasource instanceof BuddyPluginBeta.ChatInstance))
/*     */             {
/* 128 */               chat = (BuddyPluginBeta.ChatInstance)datasource;
/*     */               try
/*     */               {
/* 131 */                 chat = chat.getClone();
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 135 */                 chat = null;
/*     */                 
/* 137 */                 Debug.out(e);
/*     */               }
/*     */             }
/* 140 */             else if (id.length() > 7)
/*     */             {
/* 142 */               BuddyPluginBeta beta = BuddyPluginUtils.getBetaPlugin();
/*     */               
/* 144 */               if (beta != null) {
/*     */                 try
/*     */                 {
/* 147 */                   String[] bits = id.substring(5).split(":");
/*     */                   
/* 149 */                   String network = AENetworkClassifier.internalise(bits[0]);
/* 150 */                   String key = new String(Base32.decode(bits[1]), "UTF-8");
/*     */                   
/* 152 */                   chat = beta.getChat(network, key);
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 156 */                   Debug.out(e);
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 161 */             if (chat != null)
/*     */             {
/* 163 */               chat.setAutoNotify(true);
/*     */               
/* 165 */               return SBC_ChatOverview.createChatMdiEntry(chat);
/*     */             }
/*     */             
/* 168 */             return null;
/*     */           }
/*     */         });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void UIDetached(UIInstance instance) {}
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void openChat(String network, String key)
/*     */   {
/* 186 */     BuddyPluginBeta beta = BuddyPluginUtils.getBetaPlugin();
/*     */     
/* 188 */     if (beta != null) {
/*     */       try
/*     */       {
/* 191 */         BuddyPluginBeta.ChatInstance chat = beta.getChat(network, key);
/*     */         
/* 193 */         chat.setAutoNotify(true);
/*     */         
/* 195 */         MdiEntry mdi_entry = createChatMdiEntry(chat);
/*     */         
/* 197 */         MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*     */         
/* 199 */         if (mdi != null)
/*     */         {
/* 201 */           mdi.showEntry(mdi_entry);
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 205 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static MdiEntry createChatMdiEntry(BuddyPluginBeta.ChatInstance chat)
/*     */   {
/* 214 */     MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/*     */     
/* 216 */     if (mdi == null)
/*     */     {
/*     */ 
/*     */ 
/* 220 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 224 */       String key = "Chat_" + chat.getNetwork() + ":" + Base32.encode(chat.getKey().getBytes("UTF-8"));
/*     */       
/* 226 */       MdiEntry existing = mdi.getEntry(key);
/*     */       
/* 228 */       if (existing != null)
/*     */       {
/* 230 */         chat.destroy();
/*     */         
/* 232 */         return existing;
/*     */       }
/*     */       
/* 235 */       BuddyPluginBeta bp = BuddyPluginUtils.getBetaPlugin();
/*     */       
/* 237 */       TreeMap<BuddyPluginBeta.ChatInstance, String> name_map = new TreeMap(new Comparator()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public int compare(BuddyPluginBeta.ChatInstance o1, BuddyPluginBeta.ChatInstance o2)
/*     */         {
/*     */ 
/*     */ 
/* 246 */           return o1.getName().compareTo(o2.getName());
/*     */         }
/*     */         
/* 249 */       });
/* 250 */       name_map.put(chat, key);
/*     */       
/* 252 */       List<BuddyPluginBeta.ChatInstance> all_chats = bp.getChats();
/*     */       
/* 254 */       for (BuddyPluginBeta.ChatInstance c : all_chats) {
/*     */         try
/*     */         {
/* 257 */           String k = "Chat_" + c.getNetwork() + ":" + Base32.encode(c.getKey().getBytes("UTF-8"));
/*     */           
/* 259 */           if (mdi.getEntry(k) != null)
/*     */           {
/* 261 */             name_map.put(c, k);
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */       
/*     */ 
/* 268 */       String prev_id = null;
/*     */       
/* 270 */       for (String this_id : name_map.values())
/*     */       {
/* 272 */         if (this_id == key) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/* 277 */         prev_id = this_id;
/*     */       }
/*     */       
/* 280 */       if ((prev_id == null) && (name_map.size() > 1))
/*     */       {
/* 282 */         Iterator<String> it = name_map.values().iterator();
/*     */         
/* 284 */         it.next();
/*     */         
/* 286 */         prev_id = "~" + (String)it.next();
/*     */       }
/*     */       
/* 289 */       MdiEntry entry = mdi.createEntryFromEventListener("ChatOverview", new UISWTViewEventListenerHolder(key, ChatView.class, chat, null), key, true, chat, prev_id);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 298 */       ChatMDIEntry entry_info = new ChatMDIEntry(chat, entry);
/*     */       
/* 300 */       chat.setUserData(MDI_KEY, entry_info);
/*     */       
/* 302 */       return entry;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 306 */       Debug.out(e);
/*     */     }
/* 308 */     return null;
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
/* 319 */   private boolean columnsAdded = false;
/*     */   
/*     */   private boolean listener_added;
/*     */   
/*     */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*     */   {
/* 325 */     if ((this.tv == null) || (!this.tv.isVisible())) {
/* 326 */       return false;
/*     */     }
/* 328 */     if (item.getID().equals("remove"))
/*     */     {
/* 330 */       Object[] datasources = this.tv.getSelectedDataSources().toArray();
/*     */       
/* 332 */       if (datasources.length > 0)
/*     */       {
/* 334 */         for (Object object : datasources) {
/* 335 */           if ((object instanceof BuddyPluginBeta.ChatInstance)) {
/* 336 */             BuddyPluginBeta.ChatInstance chat = (BuddyPluginBeta.ChatInstance)object;
/* 337 */             chat.destroy();
/*     */           }
/*     */         }
/*     */         
/* 341 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 345 */     return false;
/*     */   }
/*     */   
/*     */   public void filterSet(String filter) {}
/*     */   
/*     */   public void refreshToolBarItems(Map<String, Long> list)
/*     */   {
/* 352 */     if ((this.tv == null) || (!this.tv.isVisible())) {
/* 353 */       return;
/*     */     }
/*     */     
/* 356 */     boolean canEnable = false;
/* 357 */     Object[] datasources = this.tv.getSelectedDataSources().toArray();
/*     */     
/* 359 */     if (datasources.length > 0)
/*     */     {
/* 361 */       for (Object object : datasources) {
/* 362 */         if (!(object instanceof BuddyPluginBeta.ChatInstance)) {}
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 368 */     list.put("remove", Long.valueOf(canEnable ? 1L : 0L));
/*     */   }
/*     */   
/*     */   public void updateUI() {
/* 372 */     if (this.tv != null) {
/* 373 */       this.tv.refreshTable(false);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getUpdateUIName() {
/* 378 */     return "ChatsView";
/*     */   }
/*     */   
/*     */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params) {
/* 382 */     initColumns();
/*     */     
/* 384 */     new InfoBarUtil(skinObject, "chatsview.infobar", false, "chats.infobar", "chats.view.infobar")
/*     */     {
/*     */       public boolean allowShow() {
/* 387 */         return true;
/*     */       }
/*     */       
/* 390 */     };
/* 391 */     return null;
/*     */   }
/*     */   
/*     */   protected void initColumns() {
/* 395 */     synchronized (SBC_ChatOverview.class)
/*     */     {
/* 397 */       if (this.columnsAdded)
/*     */       {
/* 399 */         return;
/*     */       }
/*     */       
/* 402 */       this.columnsAdded = true;
/*     */     }
/*     */     
/* 405 */     TableColumnManager tableManager = TableColumnManager.getInstance();
/*     */     
/* 407 */     tableManager.registerColumn(BuddyPluginBeta.ChatInstance.class, ColumnChatName.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 410 */         new ColumnChatName(column);
/*     */       }
/*     */       
/* 413 */     });
/* 414 */     tableManager.registerColumn(BuddyPluginBeta.ChatInstance.class, ColumnChatMessageCount.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 417 */         new ColumnChatMessageCount(column);
/*     */       }
/*     */       
/* 420 */     });
/* 421 */     tableManager.registerColumn(BuddyPluginBeta.ChatInstance.class, ColumnChatUserCount.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 424 */         new ColumnChatUserCount(column);
/*     */       }
/*     */       
/* 427 */     });
/* 428 */     tableManager.registerColumn(BuddyPluginBeta.ChatInstance.class, ColumnChatFavorite.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 431 */         new ColumnChatFavorite(column);
/*     */       }
/*     */       
/* 434 */     });
/* 435 */     tableManager.registerColumn(BuddyPluginBeta.ChatInstance.class, ColumnChatMsgOutstanding.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 438 */         new ColumnChatMsgOutstanding(column);
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 444 */     });
/* 445 */     tableManager.registerColumn(BuddyPluginBeta.ChatInstance.class, ColumnChatStatus.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 448 */         new ColumnChatStatus(column);
/*     */       }
/*     */       
/* 451 */     });
/* 452 */     tableManager.setDefaultColumnNames("ChatsView", new String[] { ColumnChatName.COLUMN_ID, ColumnChatMessageCount.COLUMN_ID, ColumnChatUserCount.COLUMN_ID, ColumnChatFavorite.COLUMN_ID, ColumnChatMsgOutstanding.COLUMN_ID, ColumnChatStatus.COLUMN_ID });
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
/* 465 */     tableManager.setDefaultSortColumnName("ChatsView", ColumnChatName.COLUMN_ID);
/*     */   }
/*     */   
/*     */   public Object skinObjectHidden(SWTSkinObject skinObject, Object params)
/*     */   {
/* 470 */     if (this.tv != null)
/*     */     {
/* 472 */       this.tv.delete();
/*     */       
/* 474 */       this.tv = null;
/*     */     }
/*     */     
/* 477 */     Utils.disposeSWTObjects(new Object[] { this.table_parent });
/*     */     
/*     */ 
/*     */ 
/* 481 */     BuddyPluginBeta beta = BuddyPluginUtils.getBetaPlugin();
/*     */     
/* 483 */     if (beta != null)
/*     */     {
/* 485 */       beta.removeListener(this);
/*     */       
/* 487 */       this.listener_added = false;
/*     */     }
/*     */     
/*     */ 
/* 491 */     return super.skinObjectHidden(skinObject, params);
/*     */   }
/*     */   
/*     */   public Object skinObjectShown(SWTSkinObject skinObject, Object params) {
/* 495 */     super.skinObjectShown(skinObject, params);
/* 496 */     SWTSkinObject so_list = getSkinObject("chats-list");
/*     */     
/* 498 */     if (so_list != null) {
/* 499 */       initTable((Composite)so_list.getControl());
/*     */     } else {
/* 501 */       System.out.println("NO chats-list");
/* 502 */       return null;
/*     */     }
/*     */     
/* 505 */     if (this.tv == null) {
/* 506 */       return null;
/*     */     }
/*     */     
/* 509 */     BuddyPluginBeta beta = BuddyPluginUtils.getBetaPlugin();
/*     */     
/* 511 */     if (beta != null)
/*     */     {
/* 513 */       if (!this.listener_added)
/*     */       {
/* 515 */         this.listener_added = true;
/*     */         
/* 517 */         beta.addListener(this, true);
/*     */       }
/*     */     }
/*     */     
/* 521 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object skinObjectDestroyed(SWTSkinObject skinObject, Object params)
/*     */   {
/* 530 */     if (this.listener_added)
/*     */     {
/* 532 */       this.listener_added = false;
/*     */       
/* 534 */       BuddyPluginBeta beta = BuddyPluginUtils.getBetaPlugin();
/*     */       
/* 536 */       if (beta != null)
/*     */       {
/* 538 */         beta.removeListener(this);
/*     */       }
/*     */     }
/*     */     
/* 542 */     return super.skinObjectDestroyed(skinObject, params);
/*     */   }
/*     */   
/*     */   private void initTable(Composite control)
/*     */   {
/* 547 */     if (this.tv == null)
/*     */     {
/* 549 */       this.tv = TableViewFactory.createTableViewSWT(BuddyPluginBeta.ChatInstance.class, "ChatsView", "ChatsView", new TableColumnCore[0], ColumnChatName.COLUMN_ID, 268500994);
/*     */       
/*     */ 
/* 552 */       if (this.txtFilter != null) {
/* 553 */         this.tv.enableFilterCheck(this.txtFilter, this);
/*     */       }
/* 555 */       this.tv.setRowDefaultHeightEM(1.0F);
/*     */       
/* 557 */       this.table_parent = new Composite(control, 2048);
/* 558 */       this.table_parent.setLayoutData(Utils.getFilledFormData());
/* 559 */       GridLayout layout = new GridLayout();
/* 560 */       layout.marginHeight = (layout.marginWidth = layout.verticalSpacing = layout.horizontalSpacing = 0);
/* 561 */       this.table_parent.setLayout(layout);
/*     */       
/* 563 */       this.tv.addMenuFillListener(this);
/* 564 */       this.tv.addSelectionListener(this, false);
/*     */       
/* 566 */       this.tv.initialize(this.table_parent);
/*     */     }
/*     */     
/* 569 */     control.layout(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fillMenu(String sColumnName, Menu menu)
/*     */   {
/* 577 */     List<Object> ds = this.tv.getSelectedDataSources();
/*     */     
/* 579 */     final List<BuddyPluginBeta.ChatInstance> chats = new ArrayList();
/*     */     
/* 581 */     for (Object obj : ds)
/*     */     {
/* 583 */       if ((obj instanceof BuddyPluginBeta.ChatInstance))
/*     */       {
/* 585 */         chats.add((BuddyPluginBeta.ChatInstance)obj);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 591 */     MenuItem itemSiS = new MenuItem(menu, 8);
/*     */     
/* 593 */     Messages.setLanguageText(itemSiS, Utils.isAZ2UI() ? "label.show.in.tab" : "label.show.in.sidebar");
/*     */     
/* 595 */     itemSiS.setEnabled(chats.size() > 0);
/*     */     
/* 597 */     itemSiS.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/* 602 */         MdiEntry first_entry = null;
/*     */         
/* 604 */         for (BuddyPluginBeta.ChatInstance chat : chats) {
/*     */           try
/*     */           {
/* 607 */             MdiEntry entry = SBC_ChatOverview.createChatMdiEntry(chat.getClone());
/*     */             
/* 609 */             if (first_entry == null)
/*     */             {
/* 611 */               first_entry = entry;
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 615 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */         
/* 619 */         if (first_entry != null)
/*     */         {
/* 621 */           MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*     */           
/* 623 */           if (mdi != null)
/*     */           {
/* 625 */             mdi.showEntry(first_entry);
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 632 */     });
/* 633 */     MenuItem itemRemove = new MenuItem(menu, 8);
/*     */     
/* 635 */     Messages.setLanguageText(itemRemove, "MySharesView.menu.remove");
/*     */     
/* 637 */     Utils.setMenuItemImage(itemRemove, "delete");
/*     */     
/* 639 */     itemRemove.setEnabled(chats.size() > 0);
/*     */     
/* 641 */     itemRemove.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event e)
/*     */       {
/* 646 */         for (BuddyPluginBeta.ChatInstance chat : chats)
/*     */         {
/* 648 */           chat.remove();
/*     */         }
/*     */         
/*     */       }
/* 652 */     });
/* 653 */     new MenuItem(menu, 2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addThisColumnSubMenu(String sColumnName, Menu menuThisColumn) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void selected(TableRowCore[] row)
/*     */   {
/* 668 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 669 */     if (uiFunctions != null) {
/* 670 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void deselected(TableRowCore[] rows)
/*     */   {
/* 678 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 679 */     if (uiFunctions != null) {
/* 680 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void focusChanged(TableRowCore focus)
/*     */   {
/* 688 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 689 */     if (uiFunctions != null) {
/* 690 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void defaultSelected(TableRowCore[] rows, int stateMask)
/*     */   {
/* 699 */     if (rows.length == 1)
/*     */     {
/* 701 */       Object obj = rows[0].getDataSource();
/*     */       
/* 703 */       if ((obj instanceof BuddyPluginBeta.ChatInstance))
/*     */       {
/* 705 */         BuddyPluginBeta.ChatInstance chat = (BuddyPluginBeta.ChatInstance)obj;
/*     */         
/* 707 */         BuddyPluginBeta beta = BuddyPluginUtils.getBetaPlugin();
/*     */         
/* 709 */         if (beta != null) {
/*     */           try
/*     */           {
/* 712 */             beta.showChat(chat.getClone());
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 716 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void chatAdded(BuddyPluginBeta.ChatInstance chat) {
/* 724 */     if (!chat.isInvisible()) {
/* 725 */       this.tv.addDataSource(chat);
/*     */     }
/*     */   }
/*     */   
/*     */   public void chatChanged(BuddyPluginBeta.ChatInstance chat) {
/* 730 */     if ((this.tv == null) || (this.tv.isDisposed())) {
/* 731 */       return;
/*     */     }
/* 733 */     TableRowCore row = this.tv.getRow(chat);
/* 734 */     if (row != null) {
/* 735 */       row.invalidate(true);
/*     */     }
/*     */   }
/*     */   
/*     */   public void chatRemoved(BuddyPluginBeta.ChatInstance chat) {
/* 740 */     this.tv.removeDataSource(chat);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void mouseEnter(TableRowCore row) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void mouseExit(TableRowCore row) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean filterCheck(BuddyPluginBeta.ChatInstance ds, String filter, boolean regex)
/*     */   {
/* 756 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/swt/SBC_ChatOverview.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */