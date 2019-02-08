/*     */ package org.gudy.azureus2.ui.swt.views.utils;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions.provider;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions.provider.TranscodeProfile;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions.provider.TranscodeTarget;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPlugin;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBuddy;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.events.MenuEvent;
/*     */ import org.eclipse.swt.events.MenuListener;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Widget;
/*     */ import org.gudy.azureus2.core3.category.Category;
/*     */ import org.gudy.azureus2.core3.category.CategoryManager;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.TorrentUtil;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.views.ViewUtils;
/*     */ import org.gudy.azureus2.ui.swt.views.ViewUtils.SpeedAdapter;
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
/*     */ public class CategoryUIUtils
/*     */ {
/*     */   public static void setupCategoryMenu(Menu menu, final Category category)
/*     */   {
/*  62 */     menu.addMenuListener(new MenuListener() {
/*  63 */       boolean bShown = false;
/*     */       
/*     */       public void menuHidden(MenuEvent e) {
/*  66 */         this.bShown = false;
/*     */         
/*  68 */         if (Constants.isOSX) {
/*  69 */           return;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*  74 */         e.widget.getDisplay().asyncExec(new AERunnable() {
/*     */           public void runSupport() {
/*  76 */             if ((CategoryUIUtils.1.this.bShown) || (CategoryUIUtils.1.this.val$menu.isDisposed()))
/*  77 */               return;
/*  78 */             MenuItem[] items = CategoryUIUtils.1.this.val$menu.getItems();
/*  79 */             for (int i = 0; i < items.length; i++) {
/*  80 */               items[i].dispose();
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */       
/*     */       public void menuShown(MenuEvent e) {
/*  87 */         MenuItem[] items = this.val$menu.getItems();
/*  88 */         for (int i = 0; i < items.length; i++) {
/*  89 */           items[i].dispose();
/*     */         }
/*  91 */         this.bShown = true;
/*     */         
/*  93 */         CategoryUIUtils.createMenuItems(this.val$menu, category);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static void createMenuItems(Menu menu, final Category category) {
/*  99 */     if (category.getType() == 0)
/*     */     {
/* 101 */       MenuItem itemDelete = new MenuItem(menu, 8);
/*     */       
/* 103 */       Messages.setLanguageText(itemDelete, "MyTorrentsView.menu.category.delete");
/*     */       
/*     */ 
/* 106 */       itemDelete.addListener(13, new Listener() {
/*     */         public void handleEvent(Event event) {
/* 108 */           GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/* 109 */           List<?> managers = this.val$category.getDownloadManagers(gm.getDownloadManagers());
/*     */           
/*     */ 
/* 112 */           DownloadManager[] dms = (DownloadManager[])managers.toArray(new DownloadManager[managers.size()]);
/* 113 */           for (int i = 0; i < dms.length; i++) {
/* 114 */             dms[i].getDownloadState().setCategory(null);
/*     */           }
/* 116 */           CategoryManager.removeCategory(this.val$category);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 121 */     if (category.getType() != 1)
/*     */     {
/* 123 */       long kInB = DisplayFormatters.getKinB();
/*     */       
/* 125 */       long maxDownload = COConfigurationManager.getIntParameter("Max Download Speed KBs", 0) * kInB;
/*     */       
/* 127 */       long maxUpload = COConfigurationManager.getIntParameter("Max Upload Speed KBs", 0) * kInB;
/*     */       
/*     */ 
/* 130 */       int down_speed = category.getDownloadSpeed();
/* 131 */       int up_speed = category.getUploadSpeed();
/*     */       
/* 133 */       ViewUtils.addSpeedMenu(menu.getShell(), menu, true, true, true, true, false, down_speed == 0, down_speed, down_speed, maxDownload, false, up_speed == 0, up_speed, up_speed, maxUpload, 1, null, new ViewUtils.SpeedAdapter()
/*     */       {
/*     */ 
/*     */         public void setDownSpeed(int val)
/*     */         {
/* 138 */           this.val$category.setDownloadSpeed(val);
/*     */         }
/*     */         
/*     */         public void setUpSpeed(int val) {
/* 142 */           this.val$category.setUploadSpeed(val);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/* 148 */     GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/* 149 */     List<?> managers = category.getDownloadManagers(gm.getDownloadManagers());
/*     */     
/* 151 */     DownloadManager[] dms = (DownloadManager[])managers.toArray(new DownloadManager[managers.size()]);
/*     */     
/* 153 */     boolean start = false;
/* 154 */     boolean stop = false;
/*     */     
/* 156 */     for (int i = 0; i < dms.length; i++)
/*     */     {
/* 158 */       DownloadManager dm = dms[i];
/*     */       
/* 160 */       stop = (stop) || (ManagerUtils.isStopable(dm));
/*     */       
/* 162 */       start = (start) || (ManagerUtils.isStartable(dm));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 168 */     MenuItem itemQueue = new MenuItem(menu, 8);
/* 169 */     Messages.setLanguageText(itemQueue, "MyTorrentsView.menu.queue");
/* 170 */     Utils.setMenuItemImage(itemQueue, "start");
/* 171 */     itemQueue.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 173 */         GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/* 174 */         List<?> managers = this.val$category.getDownloadManagers(gm.getDownloadManagers());
/*     */         
/* 176 */         Object[] dms = managers.toArray();
/* 177 */         TorrentUtil.queueDataSources(dms, true);
/*     */       }
/* 179 */     });
/* 180 */     itemQueue.setEnabled(start);
/*     */     
/*     */ 
/*     */ 
/* 184 */     MenuItem itemStop = new MenuItem(menu, 8);
/* 185 */     Messages.setLanguageText(itemStop, "MyTorrentsView.menu.stop");
/* 186 */     Utils.setMenuItemImage(itemStop, "stop");
/* 187 */     itemStop.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 189 */         GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/* 190 */         List<?> managers = this.val$category.getDownloadManagers(gm.getDownloadManagers());
/*     */         
/* 192 */         Object[] dms = managers.toArray();
/* 193 */         TorrentUtil.stopDataSources(dms);
/*     */       }
/* 195 */     });
/* 196 */     itemStop.setEnabled(stop);
/*     */     
/* 198 */     if (category.canBePublic())
/*     */     {
/* 200 */       new MenuItem(menu, 2);
/*     */       
/* 202 */       final MenuItem itemPublic = new MenuItem(menu, 32);
/*     */       
/* 204 */       itemPublic.setSelection(category.isPublic());
/*     */       
/* 206 */       Messages.setLanguageText(itemPublic, "cat.share");
/*     */       
/* 208 */       itemPublic.addListener(13, new Listener()
/*     */       {
/*     */         public void handleEvent(Event event) {
/* 211 */           this.val$category.setPublic(itemPublic.getSelection());
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/* 217 */     PluginInterface bpi = PluginInitializer.getDefaultInterface().getPluginManager().getPluginInterfaceByClass(BuddyPlugin.class);
/*     */     
/*     */ 
/* 220 */     int cat_type = category.getType();
/*     */     Menu share_menu;
/* 222 */     final String cname; boolean is_public; if ((bpi != null) && (cat_type != 2))
/*     */     {
/* 224 */       final BuddyPlugin buddy_plugin = (BuddyPlugin)bpi.getPlugin();
/*     */       
/* 226 */       if (buddy_plugin.isClassicEnabled())
/*     */       {
/* 228 */         share_menu = new Menu(menu.getShell(), 4);
/* 229 */         MenuItem share_item = new MenuItem(menu, 64);
/* 230 */         Messages.setLanguageText(share_item, "azbuddy.ui.menu.cat.share");
/* 231 */         share_item.setMenu(share_menu);
/*     */         
/* 233 */         List<BuddyPluginBuddy> buddies = buddy_plugin.getBuddies();
/*     */         
/* 235 */         if (buddies.size() == 0)
/*     */         {
/* 237 */           MenuItem item = new MenuItem(share_menu, 32);
/*     */           
/* 239 */           item.setText(MessageText.getString("general.add.friends"));
/*     */           
/* 241 */           item.setEnabled(false);
/*     */         }
/*     */         else
/*     */         {
/*     */           String cname;
/* 246 */           if (cat_type == 1)
/*     */           {
/* 248 */             cname = "All";
/*     */           }
/*     */           else
/*     */           {
/* 252 */             cname = category.getName();
/*     */           }
/*     */           
/* 255 */           is_public = buddy_plugin.isPublicTagOrCategory(cname);
/*     */           
/* 257 */           MenuItem itemPubCat = new MenuItem(share_menu, 32);
/*     */           
/* 259 */           Messages.setLanguageText(itemPubCat, "general.all.friends");
/*     */           
/* 261 */           itemPubCat.setSelection(is_public);
/*     */           
/* 263 */           itemPubCat.addListener(13, new Listener() {
/*     */             public void handleEvent(Event event) {
/* 265 */               if (this.val$is_public)
/*     */               {
/* 267 */                 buddy_plugin.removePublicTagOrCategory(cname);
/*     */               }
/*     */               else
/*     */               {
/* 271 */                 buddy_plugin.addPublicTagOrCategory(cname);
/*     */               }
/*     */               
/*     */             }
/* 275 */           });
/* 276 */           new MenuItem(share_menu, 2);
/*     */           
/* 278 */           for (final BuddyPluginBuddy buddy : buddies)
/*     */           {
/* 280 */             if (buddy.getNickName() != null)
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 285 */               boolean auth = buddy.isLocalRSSTagOrCategoryAuthorised(cname);
/*     */               
/* 287 */               MenuItem itemShare = new MenuItem(share_menu, 32);
/*     */               
/* 289 */               itemShare.setText(buddy.getName());
/*     */               
/* 291 */               itemShare.setSelection((auth) || (is_public));
/*     */               
/* 293 */               if (is_public)
/*     */               {
/* 295 */                 itemShare.setEnabled(false);
/*     */               }
/*     */               
/* 298 */               itemShare.addListener(13, new Listener() {
/*     */                 public void handleEvent(Event event) {
/* 300 */                   if (this.val$auth)
/*     */                   {
/* 302 */                     buddy.removeLocalAuthorisedRSSTagOrCategory(cname);
/*     */                   }
/*     */                   else
/*     */                   {
/* 306 */                     buddy.addLocalAuthorisedRSSTagOrCategory(cname);
/*     */                   }
/*     */                 }
/*     */               });
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 318 */     AZ3Functions.provider provider = AZ3Functions.getProvider();
/*     */     
/* 320 */     if ((provider != null) && (category.getType() != 1))
/*     */     {
/* 322 */       AZ3Functions.provider.TranscodeTarget[] tts = provider.getTranscodeTargets();
/*     */       
/* 324 */       if (tts.length > 0)
/*     */       {
/* 326 */         Menu t_menu = new Menu(menu.getShell(), 4);
/* 327 */         MenuItem t_item = new MenuItem(menu, 64);
/* 328 */         Messages.setLanguageText(t_item, "cat.autoxcode");
/* 329 */         t_item.setMenu(t_menu);
/*     */         
/* 331 */         String existing = category.getStringAttribute("at_att");
/*     */         
/* 333 */         for (AZ3Functions.provider.TranscodeTarget tt : tts)
/*     */         {
/* 335 */           AZ3Functions.provider.TranscodeProfile[] profiles = tt.getProfiles();
/*     */           
/* 337 */           if (profiles.length > 0)
/*     */           {
/* 339 */             Menu tt_menu = new Menu(t_menu.getShell(), 4);
/* 340 */             MenuItem tt_item = new MenuItem(t_menu, 64);
/* 341 */             tt_item.setText(tt.getName());
/* 342 */             tt_item.setMenu(tt_menu);
/*     */             
/* 344 */             for (final AZ3Functions.provider.TranscodeProfile tp : profiles)
/*     */             {
/* 346 */               final MenuItem p_item = new MenuItem(tt_menu, 32);
/*     */               
/* 348 */               p_item.setText(tp.getName());
/*     */               
/* 350 */               boolean selected = (existing != null) && (existing.equals(tp.getUID()));
/*     */               
/* 352 */               if (selected)
/*     */               {
/* 354 */                 Utils.setMenuItemImage(tt_item, "blacktick");
/*     */               }
/* 356 */               p_item.setSelection(selected);
/*     */               
/* 358 */               p_item.addListener(13, new Listener() {
/*     */                 public void handleEvent(Event event) {
/* 360 */                   this.val$category.setStringAttribute("at_att", p_item.getSelection() ? tp.getUID() : null);
/*     */                 }
/*     */               });
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 373 */     MenuItem rssOption = new MenuItem(menu, 32);
/*     */     
/* 375 */     rssOption.setSelection(category.getBooleanAttribute("at_rss_gen"));
/*     */     
/* 377 */     Messages.setLanguageText(rssOption, "cat.rss.gen");
/* 378 */     rssOption.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 380 */         boolean set = this.val$rssOption.getSelection();
/* 381 */         category.setBooleanAttribute("at_rss_gen", set);
/*     */       }
/*     */     });
/*     */     
/*     */ 
/*     */ 
/* 387 */     if ((cat_type != 2) && (cat_type != 1))
/*     */     {
/*     */ 
/* 390 */       MenuItem upPriority = new MenuItem(menu, 32);
/*     */       
/* 392 */       upPriority.setSelection(category.getIntAttribute("at_up_pri") > 0);
/*     */       
/* 394 */       Messages.setLanguageText(upPriority, "cat.upload.priority");
/* 395 */       upPriority.addListener(13, new Listener() {
/*     */         public void handleEvent(Event event) {
/* 397 */           boolean set = this.val$upPriority.getSelection();
/* 398 */           category.setIntAttribute("at_up_pri", set ? 1 : 0);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 405 */     MenuItem itemOptions = new MenuItem(menu, 8);
/*     */     
/* 407 */     Messages.setLanguageText(itemOptions, "cat.options");
/* 408 */     itemOptions.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 410 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 411 */         if (uiFunctions != null) {
/* 412 */           uiFunctions.getMDI().showEntryByID("TorrentOptionsView", this.val$dms);
/*     */         }
/*     */       }
/*     */     });
/*     */     
/*     */ 
/* 418 */     if (dms.length == 0)
/*     */     {
/* 420 */       itemOptions.setEnabled(false);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/utils/CategoryUIUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */