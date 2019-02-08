/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.plugins.I2PHelpers;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatInstance;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginUtils;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginUtils.CreateChatCallback;
/*     */ import java.net.URI;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.eclipse.swt.events.MenuAdapter;
/*     */ import org.eclipse.swt.events.MenuEvent;
/*     */ import org.eclipse.swt.events.MenuListener;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.Widget;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.ui.Graphic;
/*     */ import org.gudy.azureus2.plugins.ui.GraphicURI;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuBuilder;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemFillListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.menus.MenuItemImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.FormattersImpl;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTGraphic;
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
/*     */ public class MenuBuildUtils
/*     */ {
/*     */   public static void addMaintenanceListenerForMenu(Menu menu, final MenuBuilder builder)
/*     */   {
/*  94 */     if (Constants.isLinux) {
/*  95 */       new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*     */     }
/*     */     
/*  98 */     menu.addMenuListener(new MenuListener() {
/*  99 */       boolean bShown = false;
/*     */       
/*     */       public void menuHidden(MenuEvent e) {
/* 102 */         this.bShown = false;
/*     */         
/* 104 */         if (Constants.isOSX) {
/* 105 */           return;
/*     */         }
/*     */         
/*     */ 
/* 109 */         e.widget.getDisplay().asyncExec(new AERunnable() {
/*     */           public void runSupport() {
/* 111 */             if ((MenuBuildUtils.1.this.bShown) || (MenuBuildUtils.1.this.val$menu.isDisposed()))
/* 112 */               return;
/* 113 */             org.eclipse.swt.widgets.MenuItem[] items = MenuBuildUtils.1.this.val$menu.getItems();
/*     */             
/* 115 */             for (int i = 0; i < items.length; i++) {
/* 116 */               items[i].dispose();
/*     */             }
/* 118 */             if (Constants.isLinux) {
/* 119 */               new org.eclipse.swt.widgets.MenuItem(MenuBuildUtils.1.this.val$menu, 2);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */       
/*     */       public void menuShown(MenuEvent e) {
/*     */         try {
/* 127 */           org.eclipse.swt.widgets.MenuItem[] items = this.val$menu.getItems();
/* 128 */           for (int i = 0; i < items.length; i++) {
/* 129 */             items[i].dispose();
/*     */           }
/*     */         }
/*     */         catch (Throwable f) {}
/*     */         
/*     */ 
/* 135 */         this.bShown = true;
/* 136 */         builder.buildMenu(this.val$menu, e);
/* 137 */         if ((Constants.isLinux) && 
/* 138 */           (this.val$menu.getItemCount() == 0)) {
/* 139 */           new org.eclipse.swt.widgets.MenuItem(this.val$menu, 2);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static abstract interface ChatKeyResolver
/*     */   {
/*     */     public abstract String getChatKey(Object paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static abstract interface MenuBuilder
/*     */   {
/*     */     public abstract void buildMenu(Menu paramMenu, MenuEvent paramMenuEvent);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static class MenuItemPluginMenuControllerImpl
/*     */     implements MenuBuildUtils.PluginMenuController
/*     */   {
/*     */     private Object[] objects;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public MenuItemPluginMenuControllerImpl(Object[] o)
/*     */     {
/* 180 */       this.objects = o;
/*     */     }
/*     */     
/*     */     public Listener makeSelectionListener(org.gudy.azureus2.plugins.ui.menus.MenuItem menu_item) {
/* 184 */       final MenuItemImpl mii = (MenuItemImpl)menu_item;
/* 185 */       new Listener() {
/*     */         public void handleEvent(Event e) {
/* 187 */           mii.invokeListenersMulti(MenuBuildUtils.MenuItemPluginMenuControllerImpl.this.objects);
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*     */     public void notifyFillListeners(org.gudy.azureus2.plugins.ui.menus.MenuItem menu_item) {
/* 193 */       ((MenuItemImpl)menu_item).invokeMenuWillBeShownListeners(this.objects);
/*     */     }
/*     */     
/*     */     public void buildSubmenu(org.gudy.azureus2.plugins.ui.menus.MenuItem parent)
/*     */     {
/* 198 */       MenuBuilder submenuBuilder = ((MenuItemImpl)parent).getSubmenuBuilder();
/* 199 */       if (submenuBuilder != null) {
/*     */         try {
/* 201 */           parent.removeAllChildItems();
/* 202 */           submenuBuilder.buildSubmenu(parent, this.objects);
/*     */         } catch (Throwable t) {
/* 204 */           Debug.out(t);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 215 */   public static final PluginMenuController BASIC_MENU_ITEM_CONTROLLER = new MenuItemPluginMenuControllerImpl(null);
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
/*     */   public static void addPluginMenuItems(org.gudy.azureus2.plugins.ui.menus.MenuItem[] items, Menu parent, boolean prev_was_separator, final boolean enable_items, PluginMenuController controller)
/*     */   {
/* 242 */     for (int i = 0; i < items.length; i++) {
/* 243 */       final MenuItemImpl az_menuitem = (MenuItemImpl)items[i];
/*     */       
/* 245 */       controller.notifyFillListeners(az_menuitem);
/* 246 */       if (az_menuitem.isVisible())
/*     */       {
/* 248 */         int style = az_menuitem.getStyle();
/*     */         
/*     */ 
/* 251 */         boolean this_is_separator = false;
/*     */         
/*     */ 
/*     */ 
/* 255 */         boolean is_container = false;
/*     */         
/*     */         int swt_style;
/* 258 */         if (style == 5) {
/* 259 */           int swt_style = 64;
/* 260 */           is_container = true; } else { int swt_style;
/* 261 */           if (style == 1) {
/* 262 */             swt_style = 8; } else { int swt_style;
/* 263 */             if (style == 2) {
/* 264 */               swt_style = 32; } else { int swt_style;
/* 265 */               if (style == 3) {
/* 266 */                 swt_style = 16; } else { int swt_style;
/* 267 */                 if (style == 4) {
/* 268 */                   this_is_separator = true;
/* 269 */                   swt_style = 2;
/*     */                 } else {
/* 271 */                   swt_style = 8;
/*     */                 }
/*     */               } } } }
/* 274 */         if (((!prev_was_separator) || (!this_is_separator)) && (
/* 275 */           (!this_is_separator) || (i != items.length - 1)))
/*     */         {
/* 277 */           prev_was_separator = this_is_separator;
/*     */           
/* 279 */           final org.eclipse.swt.widgets.MenuItem menuItem = new org.eclipse.swt.widgets.MenuItem(parent, swt_style);
/*     */           
/*     */ 
/* 282 */           if (swt_style != 2)
/*     */           {
/* 284 */             if (enable_items)
/*     */             {
/* 286 */               if ((style == 2) || (style == 3))
/*     */               {
/*     */ 
/* 289 */                 Boolean selection_value = (Boolean)az_menuitem.getData();
/* 290 */                 if (selection_value == null) {
/* 291 */                   throw new RuntimeException("MenuItem with resource name \"" + az_menuitem.getResourceKey() + "\" needs to have a boolean value entered via setData before being used!");
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/* 296 */                 menuItem.setSelection(selection_value.booleanValue());
/*     */               }
/*     */             }
/*     */             
/* 300 */             final Listener main_listener = controller.makeSelectionListener(az_menuitem);
/* 301 */             menuItem.addListener(13, new Listener() {
/*     */               public void handleEvent(Event e) {
/* 303 */                 if ((this.val$az_menuitem.getStyle() == 2) || (this.val$az_menuitem.getStyle() == 3))
/*     */                 {
/* 305 */                   if (!menuItem.isDisposed()) {
/* 306 */                     this.val$az_menuitem.setData(Boolean.valueOf(menuItem.getSelection()));
/*     */                   }
/*     */                 }
/* 309 */                 main_listener.handleEvent(e);
/*     */               }
/*     */             });
/*     */             
/* 313 */             if (is_container) {
/* 314 */               Menu this_menu = new Menu(parent);
/* 315 */               menuItem.setMenu(this_menu);
/*     */               
/* 317 */               addMaintenanceListenerForMenu(this_menu, new MenuBuilder() {
/*     */                 public void buildMenu(Menu root_menu, MenuEvent menuEvent) {
/* 319 */                   this.val$controller.buildSubmenu(az_menuitem);
/* 320 */                   MenuBuildUtils.addPluginMenuItems(az_menuitem.getItems(), root_menu, false, enable_items, this.val$controller);
/*     */                 }
/*     */               });
/*     */             }
/*     */             
/*     */ 
/* 326 */             String custom_title = az_menuitem.getText();
/* 327 */             menuItem.setText(custom_title);
/*     */             
/* 329 */             Graphic g = az_menuitem.getGraphic();
/* 330 */             if ((g instanceof UISWTGraphic)) {
/* 331 */               Utils.setMenuItemImage(menuItem, ((UISWTGraphic)g).getImage());
/* 332 */             } else if ((g instanceof GraphicURI)) {
/* 333 */               Utils.setMenuItemImage(menuItem, ((GraphicURI)g).getURI().toString());
/*     */             }
/*     */             
/* 336 */             menuItem.setEnabled((enable_items) && (az_menuitem.isEnabled()));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
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
/*     */   public static List<Object> splitLongMenuListIntoHierarchy(List<String> flat_entries, int split_after)
/*     */   {
/* 353 */     List<Object> result = new ArrayList();
/*     */     
/* 355 */     int flat_entry_count = flat_entries.size();
/*     */     
/* 357 */     if (flat_entry_count == 0)
/*     */     {
/* 359 */       return result;
/*     */     }
/*     */     
/* 362 */     Collections.sort(flat_entries, new Comparator()
/*     */     {
/*     */ 
/*     */ 
/* 366 */       final Comparator<String> comp = new FormattersImpl().getAlphanumericComparator(true);
/*     */       
/*     */ 
/*     */ 
/*     */       public int compare(String o1, String o2)
/*     */       {
/* 372 */         return this.comp.compare(o1, o2);
/*     */       }
/*     */       
/* 375 */     });
/* 376 */     int[] buckets = new int[split_after];
/*     */     
/* 378 */     for (int i = 0; i < flat_entry_count; i++)
/*     */     {
/* 380 */       buckets[(i % buckets.length)] += 1;
/*     */     }
/*     */     
/* 383 */     List<char[]> edges = new ArrayList();
/*     */     
/* 385 */     int pos = 0;
/*     */     
/* 387 */     for (int i = 0; i < buckets.length; i++)
/*     */     {
/* 389 */       int entries = buckets[i];
/*     */       
/* 391 */       edges.add(((String)flat_entries.get(pos)).toCharArray());
/*     */       
/* 393 */       if (entries <= 1)
/*     */         break;
/* 395 */       edges.add(((String)flat_entries.get(pos + entries - 1)).toCharArray());
/*     */       
/* 397 */       pos += entries;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 405 */     int[] edge_lens = new int[edges.size()];
/*     */     
/* 407 */     for (int i = 0; i < edges.size() - 1; i++)
/*     */     {
/* 409 */       char[] c1 = (char[])edges.get(i);
/* 410 */       char[] c2 = (char[])edges.get(i + 1);
/*     */       
/*     */ 
/*     */ 
/* 414 */       for (int j = 0; j < Math.min(Math.min(c1.length, c2.length), 5); j++)
/*     */       {
/* 416 */         if (c1[j] != c2[j]) {
/*     */           break;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 422 */       j++;
/*     */       
/* 424 */       edge_lens[i] = Math.min(c1.length, Math.max(edge_lens[i], j));
/* 425 */       edge_lens[(i + 1)] = j;
/*     */     }
/*     */     
/* 428 */     int bucket_pos = 0;
/* 429 */     int edge_pos = 0;
/*     */     
/* 431 */     Iterator<String> tag_it = flat_entries.iterator();
/*     */     
/* 433 */     while (tag_it.hasNext())
/*     */     {
/* 435 */       int bucket_entries = buckets[(bucket_pos++)];
/*     */       
/* 437 */       List<String> bucket_tags = new ArrayList();
/*     */       
/* 439 */       for (int i = 0; i < bucket_entries; i++)
/*     */       {
/* 441 */         bucket_tags.add(tag_it.next());
/*     */       }
/*     */       
/* 444 */       if (bucket_entries == 1)
/*     */       {
/* 446 */         result.add(bucket_tags.get(0));
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 451 */         String level_name = new String((char[])edges.get(edge_pos), 0, edge_lens[(edge_pos++)]) + " - " + new String((char[])edges.get(edge_pos), 0, edge_lens[(edge_pos++)]);
/*     */         
/* 453 */         result.add(new Object[] { level_name, bucket_tags });
/*     */       }
/*     */     }
/*     */     
/* 457 */     return result;
/*     */   }
/*     */   
/* 460 */   private static AtomicBoolean pub_chat_pending = new AtomicBoolean();
/* 461 */   private static AtomicBoolean anon_chat_pending = new AtomicBoolean();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addChatMenu(Menu menu, String menu_resource_key, final String chat_key)
/*     */   {
/* 469 */     Menu chat_menu = new Menu(menu.getShell(), 4);
/*     */     
/* 471 */     org.eclipse.swt.widgets.MenuItem chat_item = new org.eclipse.swt.widgets.MenuItem(menu, 64);
/*     */     
/* 473 */     Messages.setLanguageText(chat_item, menu_resource_key);
/*     */     
/* 475 */     chat_item.setMenu(chat_menu);
/*     */     
/* 477 */     chat_menu.addMenuListener(new MenuAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void menuShown(MenuEvent e)
/*     */       {
/*     */ 
/* 484 */         for (org.eclipse.swt.widgets.MenuItem mi : this.val$chat_menu.getItems())
/*     */         {
/* 486 */           mi.dispose();
/*     */         }
/*     */         
/* 489 */         if (!BuddyPluginUtils.isBetaChatAvailable())
/*     */         {
/* 491 */           return;
/*     */         }
/*     */         
/* 494 */         org.eclipse.swt.widgets.MenuItem chat_pub = new org.eclipse.swt.widgets.MenuItem(this.val$chat_menu, 8);
/*     */         
/* 496 */         Messages.setLanguageText(chat_pub, "label.public");
/*     */         
/* 498 */         chat_pub.addListener(13, new Listener()
/*     */         {
/*     */           public void handleEvent(Event event) {
/* 501 */             MenuBuildUtils.pub_chat_pending.set(true);
/*     */             
/* 503 */             BuddyPluginUtils.createBetaChat("Public", MenuBuildUtils.5.this.val$chat_key, new BuddyPluginUtils.CreateChatCallback()
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */               public void complete(BuddyPluginBeta.ChatInstance chat)
/*     */               {
/*     */ 
/*     */ 
/* 512 */                 MenuBuildUtils.pub_chat_pending.set(false);
/*     */               }
/*     */             });
/*     */           }
/*     */         });
/* 517 */         if (MenuBuildUtils.pub_chat_pending.get())
/*     */         {
/* 519 */           chat_pub.setEnabled(false);
/* 520 */           chat_pub.setText(chat_pub.getText() + " (" + MessageText.getString("PeersView.state.pending") + ")");
/*     */         }
/*     */         
/* 523 */         if (BuddyPluginUtils.isBetaChatAnonAvailable())
/*     */         {
/* 525 */           org.eclipse.swt.widgets.MenuItem chat_priv = new org.eclipse.swt.widgets.MenuItem(this.val$chat_menu, 8);
/*     */           
/* 527 */           Messages.setLanguageText(chat_priv, "label.anon");
/*     */           
/* 529 */           chat_priv.addListener(13, new Listener()
/*     */           {
/*     */             public void handleEvent(Event event) {
/* 532 */               MenuBuildUtils.anon_chat_pending.set(true);
/*     */               
/* 534 */               BuddyPluginUtils.createBetaChat("I2P", MenuBuildUtils.5.this.val$chat_key, new BuddyPluginUtils.CreateChatCallback()
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/*     */                 public void complete(BuddyPluginBeta.ChatInstance chat)
/*     */                 {
/*     */ 
/*     */ 
/* 543 */                   MenuBuildUtils.anon_chat_pending.set(false);
/*     */                 }
/*     */               });
/*     */             }
/*     */           });
/* 548 */           if (MenuBuildUtils.anon_chat_pending.get())
/*     */           {
/* 550 */             chat_priv.setEnabled(false);
/* 551 */             chat_priv.setText(chat_priv.getText() + " (" + MessageText.getString("PeersView.state.pending") + ")");
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 556 */           org.eclipse.swt.widgets.MenuItem chat_priv = new org.eclipse.swt.widgets.MenuItem(this.val$chat_menu, 8);
/*     */           
/* 558 */           chat_priv.setText(MessageText.getString("label.anon") + "...");
/*     */           
/* 560 */           chat_priv.addListener(13, new Listener()
/*     */           {
/*     */             public void handleEvent(Event event) {
/* 563 */               I2PHelpers.installI2PHelper(null, null, null);
/*     */             }
/*     */           });
/* 566 */           if (I2PHelpers.isInstallingI2PHelper())
/*     */           {
/* 568 */             chat_priv.setEnabled(false);
/* 569 */             chat_priv.setText(chat_priv.getText() + " (" + MessageText.getString("PeersView.state.pending") + ")");
/*     */           }
/*     */         }
/*     */       }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static org.gudy.azureus2.plugins.ui.menus.MenuItem addChatMenu(MenuManager menu_manager, final org.gudy.azureus2.plugins.ui.menus.MenuItem chat_item, final ChatKeyResolver chat_key_resolver)
/*     */   {
/* 591 */     chat_item.setStyle(5);
/*     */     
/* 593 */     chat_item.addFillListener(new MenuItemFillListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data)
/*     */       {
/*     */ 
/*     */ 
/* 602 */         menu.removeAllChildItems();
/*     */         
/*     */ 
/* 605 */         org.gudy.azureus2.plugins.ui.menus.MenuItem chat_pub = this.val$menu_manager.addMenuItem(chat_item, "label.public");
/*     */         
/* 607 */         chat_pub.addMultiListener(new MenuItemListener()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*     */           {
/*     */ 
/*     */ 
/* 615 */             Object[] rows = (Object[])target;
/*     */             
/* 617 */             if (rows.length > 0)
/*     */             {
/* 619 */               final AtomicInteger count = new AtomicInteger(rows.length);
/*     */               
/* 621 */               MenuBuildUtils.pub_chat_pending.set(true);
/*     */               
/* 623 */               for (Object obj : rows)
/*     */               {
/* 625 */                 String chat_key = MenuBuildUtils.6.this.val$chat_key_resolver.getChatKey(obj);
/*     */                 
/* 627 */                 if (chat_key != null)
/*     */                 {
/* 629 */                   BuddyPluginUtils.createBetaChat("Public", chat_key, new BuddyPluginUtils.CreateChatCallback()
/*     */                   {
/*     */ 
/*     */ 
/*     */ 
/*     */                     public void complete(BuddyPluginBeta.ChatInstance chat)
/*     */                     {
/*     */ 
/*     */ 
/* 638 */                       if (count.decrementAndGet() == 0)
/*     */                       {
/* 640 */                         MenuBuildUtils.pub_chat_pending.set(false);
/*     */                       }
/*     */                     }
/*     */                   });
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */         
/* 650 */         if (MenuBuildUtils.pub_chat_pending.get())
/*     */         {
/* 652 */           chat_pub.setEnabled(false);
/* 653 */           chat_pub.setText(chat_pub.getText() + " (" + MessageText.getString("PeersView.state.pending") + ")");
/*     */         }
/*     */         
/*     */ 
/* 657 */         if (BuddyPluginUtils.isBetaChatAnonAvailable())
/*     */         {
/* 659 */           org.gudy.azureus2.plugins.ui.menus.MenuItem chat_priv = this.val$menu_manager.addMenuItem(chat_item, "label.anon");
/*     */           
/* 661 */           chat_priv.addMultiListener(new MenuItemListener()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*     */             {
/*     */ 
/*     */ 
/* 669 */               Object[] rows = (Object[])target;
/*     */               
/* 671 */               if (rows.length > 0)
/*     */               {
/* 673 */                 final AtomicInteger count = new AtomicInteger(rows.length);
/*     */                 
/* 675 */                 MenuBuildUtils.anon_chat_pending.set(true);
/*     */                 
/* 677 */                 for (Object obj : rows)
/*     */                 {
/* 679 */                   String chat_key = MenuBuildUtils.6.this.val$chat_key_resolver.getChatKey(obj);
/*     */                   
/* 681 */                   if (chat_key != null)
/*     */                   {
/* 683 */                     BuddyPluginUtils.createBetaChat("I2P", chat_key, new BuddyPluginUtils.CreateChatCallback()
/*     */                     {
/*     */ 
/*     */ 
/*     */ 
/*     */                       public void complete(BuddyPluginBeta.ChatInstance chat)
/*     */                       {
/*     */ 
/*     */ 
/* 692 */                         if (count.decrementAndGet() == 0)
/*     */                         {
/* 694 */                           MenuBuildUtils.anon_chat_pending.set(false);
/*     */                         }
/*     */                       }
/*     */                     });
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           });
/*     */           
/* 704 */           if (MenuBuildUtils.anon_chat_pending.get())
/*     */           {
/* 706 */             chat_priv.setEnabled(false);
/* 707 */             chat_priv.setText(chat_priv.getText() + " (" + MessageText.getString("PeersView.state.pending") + ")");
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 712 */           org.gudy.azureus2.plugins.ui.menus.MenuItem chat_priv = this.val$menu_manager.addMenuItem(chat_item, "label.anon");
/*     */           
/* 714 */           chat_priv.setText(MessageText.getString("label.anon") + "...");
/*     */           
/* 716 */           chat_priv.addMultiListener(new MenuItemListener()
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */             public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*     */             {
/*     */ 
/*     */ 
/* 725 */               I2PHelpers.installI2PHelper(null, null, null);
/*     */             }
/*     */           });
/*     */           
/* 729 */           if (I2PHelpers.isInstallingI2PHelper())
/*     */           {
/* 731 */             chat_priv.setEnabled(false);
/* 732 */             chat_priv.setText(chat_priv.getText() + " (" + MessageText.getString("PeersView.state.pending") + ")");
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/* 738 */     });
/* 739 */     return chat_item;
/*     */   }
/*     */   
/*     */   public static abstract interface PluginMenuController
/*     */   {
/*     */     public abstract Listener makeSelectionListener(org.gudy.azureus2.plugins.ui.menus.MenuItem paramMenuItem);
/*     */     
/*     */     public abstract void notifyFillListeners(org.gudy.azureus2.plugins.ui.menus.MenuItem paramMenuItem);
/*     */     
/*     */     public abstract void buildSubmenu(org.gudy.azureus2.plugins.ui.menus.MenuItem paramMenuItem);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/MenuBuildUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */