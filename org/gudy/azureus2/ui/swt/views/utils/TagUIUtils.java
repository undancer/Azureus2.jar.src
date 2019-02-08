/*      */ package org.gudy.azureus2.ui.swt.views.utils;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagDownload;
/*      */ import com.aelitis.azureus.core.tag.TagException;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureExecOnAssign;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureFileLocation;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureProperties;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureProperties.TagProperty;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureRSSFeed;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureRateLimit;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureRunState;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureTranscode;
/*      */ import com.aelitis.azureus.core.tag.TagListener;
/*      */ import com.aelitis.azureus.core.tag.TagManager;
/*      */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*      */ import com.aelitis.azureus.core.tag.TagType;
/*      */ import com.aelitis.azureus.core.tag.Taggable;
/*      */ import com.aelitis.azureus.core.util.AZ3Functions.provider;
/*      */ import com.aelitis.azureus.core.util.AZ3Functions.provider.TranscodeProfile;
/*      */ import com.aelitis.azureus.core.util.AZ3Functions.provider.TranscodeTarget;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPlugin;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBuddy;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctions.TagReturner;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.UIFunctionsUserPrompter;
/*      */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*      */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*      */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*      */ import java.io.File;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.DirectoryDialog;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.TrackersUtil;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuItemFillListener;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.FormattersImpl;
/*      */ import org.gudy.azureus2.ui.common.util.MenuItemManager;
/*      */ import org.gudy.azureus2.ui.swt.MenuBuildUtils;
/*      */ import org.gudy.azureus2.ui.swt.MenuBuildUtils.MenuItemPluginMenuControllerImpl;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.MenuFactory;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
/*      */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*      */ import org.gudy.azureus2.ui.swt.views.FilesView;
/*      */ import org.gudy.azureus2.ui.swt.views.stats.StatsView;
/*      */ 
/*      */ public class TagUIUtils
/*      */ {
/*      */   public static final int MAX_TOP_LEVEL_TAGS_IN_MENU = 20;
/*      */   
/*      */   public static String getChatKey(Tag tag)
/*      */   {
/*   81 */     return "Tag: " + tag.getTagName(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void setupSideBarMenus(MenuManager menuManager)
/*      */   {
/*   88 */     org.gudy.azureus2.plugins.ui.menus.MenuItem menuItem = menuManager.addMenuItem("sidebar.header.transfers", "ConfigView.section.style.TagInSidebar");
/*      */     
/*      */ 
/*      */ 
/*   92 */     menuItem.setStyle(2);
/*      */     
/*   94 */     menuItem.addListener(new MenuItemListener() {
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/*   96 */         boolean b = COConfigurationManager.getBooleanParameter("Library.TagInSideBar");
/*   97 */         COConfigurationManager.setParameter("Library.TagInSideBar", !b);
/*      */       }
/*      */       
/*  100 */     });
/*  101 */     menuItem.addFillListener(new MenuItemFillListener() {
/*      */       public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/*  103 */         menu.setData(Boolean.valueOf(COConfigurationManager.getBooleanParameter("Library.TagInSideBar")));
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  108 */     });
/*  109 */     menuItem = menuManager.addMenuItem("sidebar.header.transfers", "label.tags");
/*      */     
/*      */ 
/*      */ 
/*  113 */     menuItem.setStyle(5);
/*      */     
/*  115 */     menuItem.addFillListener(new MenuItemFillListener() {
/*      */       public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/*  117 */         menu.removeAllChildItems();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  122 */         final TagType manual_tt = TagManagerFactory.getTagManager().getTagType(3);
/*      */         
/*  124 */         org.gudy.azureus2.plugins.ui.menus.MenuItem menuItem = this.val$menuManager.addMenuItem(menu, manual_tt.getTagTypeName(false));
/*      */         
/*  126 */         menuItem.setStyle(5);
/*      */         
/*  128 */         menuItem.addFillListener(new MenuItemFillListener() {
/*      */           public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/*  130 */             menu.removeAllChildItems();
/*      */             
/*  132 */             final List<Tag> all_tags = manual_tt.getTags();
/*      */             
/*  134 */             List<String> menu_names = new ArrayList();
/*  135 */             Map<String, Tag> menu_name_map = new IdentityHashMap();
/*      */             
/*  137 */             boolean all_visible = true;
/*  138 */             boolean all_invisible = true;
/*      */             
/*  140 */             boolean has_ut = false;
/*      */             
/*  142 */             for (Tag t : all_tags)
/*      */             {
/*  144 */               String name = t.getTagName(true);
/*      */               
/*  146 */               menu_names.add(name);
/*  147 */               menu_name_map.put(name, t);
/*      */               
/*  149 */               if (t.isVisible()) {
/*  150 */                 all_invisible = false;
/*      */               } else {
/*  152 */                 all_visible = false;
/*      */               }
/*      */               
/*  155 */               TagFeatureProperties props = (TagFeatureProperties)t;
/*      */               
/*  157 */               TagFeatureProperties.TagProperty prop = props.getProperty("untagged");
/*      */               
/*  159 */               if (prop != null)
/*      */               {
/*  161 */                 Boolean b = prop.getBoolean();
/*      */                 
/*  163 */                 if ((b != null) && (b.booleanValue()))
/*      */                 {
/*  165 */                   has_ut = true;
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*  170 */             org.gudy.azureus2.plugins.ui.menus.MenuItem showAllItem = TagUIUtils.3.this.val$menuManager.addMenuItem(menu, "label.show.all");
/*  171 */             showAllItem.setStyle(1);
/*      */             
/*  173 */             showAllItem.addListener(new MenuItemListener() {
/*      */               public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/*  175 */                 for (Tag t : all_tags) {
/*  176 */                   t.setVisible(true);
/*      */                 }
/*      */                 
/*      */               }
/*  180 */             });
/*  181 */             org.gudy.azureus2.plugins.ui.menus.MenuItem hideAllItem = TagUIUtils.3.this.val$menuManager.addMenuItem(menu, "popup.error.hideall");
/*  182 */             hideAllItem.setStyle(1);
/*      */             
/*  184 */             hideAllItem.addListener(new MenuItemListener() {
/*      */               public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/*  186 */                 for (Tag t : all_tags) {
/*  187 */                   t.setVisible(false);
/*      */                 }
/*      */                 
/*      */               }
/*  191 */             });
/*  192 */             org.gudy.azureus2.plugins.ui.menus.MenuItem sepItem = TagUIUtils.3.this.val$menuManager.addMenuItem(menu, "sepm");
/*      */             
/*  194 */             sepItem.setStyle(4);
/*      */             
/*  196 */             showAllItem.setEnabled(!all_visible);
/*  197 */             hideAllItem.setEnabled(!all_invisible);
/*      */             
/*  199 */             List<Object> menu_structure = MenuBuildUtils.splitLongMenuListIntoHierarchy(menu_names, 20);
/*      */             
/*  201 */             for (Object obj : menu_structure)
/*      */             {
/*  203 */               List<Tag> bucket_tags = new ArrayList();
/*      */               
/*      */ 
/*      */ 
/*  207 */               if ((obj instanceof String))
/*      */               {
/*  209 */                 org.gudy.azureus2.plugins.ui.menus.MenuItem parent_menu = menu;
/*      */                 
/*  211 */                 bucket_tags.add(menu_name_map.get((String)obj));
/*      */               }
/*      */               else
/*      */               {
/*  215 */                 Object[] entry = (Object[])obj;
/*      */                 
/*  217 */                 List<String> tag_names = (List)entry[1];
/*      */                 
/*  219 */                 boolean sub_all_visible = true;
/*  220 */                 boolean sub_some_visible = false;
/*      */                 
/*  222 */                 for (String name : tag_names)
/*      */                 {
/*  224 */                   Tag tag = (Tag)menu_name_map.get(name);
/*      */                   
/*  226 */                   if (tag.isVisible())
/*      */                   {
/*  228 */                     sub_some_visible = true;
/*      */                   }
/*      */                   else
/*      */                   {
/*  232 */                     sub_all_visible = false;
/*      */                   }
/*      */                   
/*  235 */                   bucket_tags.add(tag);
/*      */                 }
/*      */                 
/*      */                 String mod;
/*      */                 String mod;
/*  240 */                 if (sub_all_visible)
/*      */                 {
/*  242 */                   mod = " (*)";
/*      */                 } else { String mod;
/*  244 */                   if (sub_some_visible)
/*      */                   {
/*  246 */                     mod = " (+)";
/*      */                   }
/*      */                   else
/*      */                   {
/*  250 */                     mod = "";
/*      */                   }
/*      */                 }
/*  253 */                 parent_menu = TagUIUtils.3.this.val$menuManager.addMenuItem(menu, "!" + (String)entry[0] + mod + "!");
/*      */                 
/*  255 */                 parent_menu.setStyle(5);
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*  260 */               for (final Tag tag : bucket_tags)
/*      */               {
/*  262 */                 org.gudy.azureus2.plugins.ui.menus.MenuItem m = TagUIUtils.3.this.val$menuManager.addMenuItem(parent_menu, tag.getTagName(false));
/*      */                 
/*  264 */                 m.setStyle(2);
/*      */                 
/*  266 */                 m.setData(Boolean.valueOf(tag.isVisible()));
/*      */                 
/*  268 */                 m.addListener(new MenuItemListener()
/*      */                 {
/*      */ 
/*      */ 
/*      */                   public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */                   {
/*      */ 
/*      */ 
/*  276 */                     tag.setVisible(!tag.isVisible());
/*      */                   }
/*      */                 });
/*      */               }
/*      */             }
/*      */             org.gudy.azureus2.plugins.ui.menus.MenuItem parent_menu;
/*  282 */             if (!has_ut)
/*      */             {
/*  284 */               sepItem = TagUIUtils.3.this.val$menuManager.addMenuItem(menu, "sepu");
/*      */               
/*  286 */               sepItem.setStyle(4);
/*      */               
/*      */ 
/*  289 */               org.gudy.azureus2.plugins.ui.menus.MenuItem m = TagUIUtils.3.this.val$menuManager.addMenuItem(menu, "label.untagged");
/*      */               
/*  291 */               m.setStyle(1);
/*      */               
/*  293 */               m.addListener(new MenuItemListener()
/*      */               {
/*      */ 
/*      */                 public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */                 {
/*      */ 
/*      */                   try
/*      */                   {
/*      */ 
/*  302 */                     String tag_name = MessageText.getString("label.untagged");
/*      */                     
/*  304 */                     Tag ut_tag = TagUIUtils.3.1.this.val$manual_tt.getTag(tag_name, true);
/*      */                     
/*  306 */                     if (ut_tag == null)
/*      */                     {
/*      */ 
/*  309 */                       ut_tag = TagUIUtils.3.1.this.val$manual_tt.createTag(tag_name, true);
/*      */                     }
/*      */                     
/*  312 */                     TagFeatureProperties tp = (TagFeatureProperties)ut_tag;
/*      */                     
/*  314 */                     tp.getProperty("untagged").setBoolean(Boolean.valueOf(true));
/*      */                   }
/*      */                   catch (TagException e)
/*      */                   {
/*  318 */                     Debug.out(e);
/*      */                   }
/*      */                   
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*  325 */         });
/*  326 */         menuItem = this.val$menuManager.addMenuItem(menu, "label.add.tag");
/*      */         
/*  328 */         menuItem.addListener(new MenuItemListener() {
/*      */           public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/*  330 */             TagUIUtils.createManualTag(null);
/*      */           }
/*      */           
/*  333 */         });
/*  334 */         org.gudy.azureus2.plugins.ui.menus.MenuItem sepItem = this.val$menuManager.addMenuItem(menu, "sep1");
/*      */         
/*  336 */         sepItem.setStyle(4);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  341 */         menuItem = this.val$menuManager.addMenuItem(menu, "wizard.maketorrent.auto");
/*      */         
/*  343 */         menuItem.setStyle(5);
/*      */         
/*  345 */         menuItem.addFillListener(new MenuItemFillListener() {
/*      */           public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/*  347 */             menu.removeAllChildItems();
/*      */             
/*      */ 
/*      */ 
/*  351 */             org.gudy.azureus2.plugins.ui.menus.MenuItem menuItem = TagUIUtils.3.this.val$menuManager.addMenuItem(menu, "label.content");
/*      */             
/*  353 */             menuItem.setStyle(5);
/*      */             
/*  355 */             menuItem.addFillListener(new MenuItemFillListener() {
/*      */               public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/*  357 */                 menu.removeAllChildItems();
/*      */                 
/*  359 */                 String[] tag_ids = { "tag.type.man.vhdn", "tag.type.man.featcon" };
/*      */                 
/*  361 */                 for (String id : tag_ids)
/*      */                 {
/*  363 */                   final String c_id = id + ".enabled";
/*      */                   
/*  365 */                   org.gudy.azureus2.plugins.ui.menus.MenuItem menuItem = TagUIUtils.3.this.val$menuManager.addMenuItem(menu, id);
/*      */                   
/*  367 */                   menuItem.setStyle(2);
/*      */                   
/*  369 */                   menuItem.addListener(new MenuItemListener() {
/*      */                     public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/*  371 */                       COConfigurationManager.setParameter(c_id, menu.isSelected());
/*      */                     }
/*  373 */                   });
/*  374 */                   menuItem.addFillListener(new MenuItemFillListener() {
/*      */                     public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/*  376 */                       menu.setData(Boolean.valueOf(COConfigurationManager.getBooleanParameter(c_id, true)));
/*      */                     }
/*      */                     
/*      */ 
/*      */                   });
/*      */                 }
/*      */                 
/*      */               }
/*      */               
/*  385 */             });
/*  386 */             List<TagType> tag_types = TagManagerFactory.getTagManager().getTagTypes();
/*      */             
/*  388 */             for (final TagType tag_type : tag_types)
/*      */             {
/*  390 */               if ((tag_type.getTagType() != 1) && 
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  395 */                 (tag_type.isTagTypeAuto()) && 
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*  400 */                 (tag_type.getTags().size() != 0))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  405 */                 menuItem = TagUIUtils.3.this.val$menuManager.addMenuItem(menu, tag_type.getTagTypeName(false));
/*      */                 
/*  407 */                 menuItem.setStyle(5);
/*      */                 
/*  409 */                 menuItem.addFillListener(new MenuItemFillListener() {
/*      */                   public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/*  411 */                     menu.removeAllChildItems();
/*      */                     
/*  413 */                     final List<Tag> tags = tag_type.getTags();
/*      */                     
/*  415 */                     org.gudy.azureus2.plugins.ui.menus.MenuItem showAllItem = TagUIUtils.3.this.val$menuManager.addMenuItem(menu, "label.show.all");
/*  416 */                     showAllItem.setStyle(1);
/*      */                     
/*  418 */                     showAllItem.addListener(new MenuItemListener() {
/*      */                       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/*  420 */                         for (Tag t : tags) {
/*  421 */                           t.setVisible(true);
/*      */                         }
/*      */                         
/*      */                       }
/*  425 */                     });
/*  426 */                     org.gudy.azureus2.plugins.ui.menus.MenuItem hideAllItem = TagUIUtils.3.this.val$menuManager.addMenuItem(menu, "popup.error.hideall");
/*  427 */                     hideAllItem.setStyle(1);
/*      */                     
/*  429 */                     hideAllItem.addListener(new MenuItemListener() {
/*      */                       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/*  431 */                         for (Tag t : tags) {
/*  432 */                           t.setVisible(false);
/*      */                         }
/*      */                         
/*      */                       }
/*  436 */                     });
/*  437 */                     boolean all_visible = true;
/*  438 */                     boolean all_invisible = true;
/*      */                     
/*  440 */                     for (Tag t : tags) {
/*  441 */                       if (t.isVisible()) {
/*  442 */                         all_invisible = false;
/*      */                       } else {
/*  444 */                         all_visible = false;
/*      */                       }
/*      */                     }
/*      */                     
/*  448 */                     showAllItem.setEnabled(!all_visible);
/*  449 */                     hideAllItem.setEnabled(!all_invisible);
/*      */                     
/*  451 */                     org.gudy.azureus2.plugins.ui.menus.MenuItem sepItem = TagUIUtils.3.this.val$menuManager.addMenuItem(menu, "sep2");
/*      */                     
/*  453 */                     sepItem.setStyle(4);
/*      */                     
/*  455 */                     for (final Tag t : tags)
/*      */                     {
/*  457 */                       org.gudy.azureus2.plugins.ui.menus.MenuItem menuItem = TagUIUtils.3.this.val$menuManager.addMenuItem(menu, t.getTagName(false));
/*      */                       
/*  459 */                       menuItem.setStyle(2);
/*      */                       
/*  461 */                       menuItem.addListener(new MenuItemListener() {
/*      */                         public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/*  463 */                           t.setVisible(menu.isSelected());
/*      */                         }
/*  465 */                       });
/*  466 */                       menuItem.addFillListener(new MenuItemFillListener() {
/*      */                         public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/*  468 */                           menu.setData(Boolean.valueOf(t.isVisible()));
/*      */                         }
/*      */                       });
/*      */                     }
/*      */                   }
/*      */                 });
/*      */               }
/*      */             }
/*      */           }
/*  477 */         });
/*  478 */         sepItem = this.val$menuManager.addMenuItem(menu, "sep3");
/*      */         
/*  480 */         sepItem.setStyle(4);
/*      */         
/*      */ 
/*  483 */         menuItem = this.val$menuManager.addMenuItem(menu, "tag.show.stats");
/*      */         
/*  485 */         menuItem.addListener(new MenuItemListener() {
/*      */           public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/*  487 */             UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*  488 */             uiFunctions.getMDI().showEntryByID(StatsView.VIEW_ID, "TagStatsView");
/*      */           }
/*      */           
/*      */ 
/*  492 */         });
/*  493 */         menuItem = this.val$menuManager.addMenuItem(menu, "tag.show.overview");
/*      */         
/*  495 */         menuItem.addListener(new MenuItemListener() {
/*      */           public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/*  497 */             UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*  498 */             uiFunctions.getMDI().showEntryByID("TagsOverview");
/*      */           }
/*      */           
/*      */         });
/*      */       }
/*  503 */     });
/*  504 */     com.aelitis.azureus.core.AzureusCoreFactory.addCoreRunningListener(new com.aelitis.azureus.core.AzureusCoreRunningListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void azureusCoreRunning(AzureusCore core)
/*      */       {
/*      */ 
/*  511 */         TagUIUtils.checkTagSharing(true);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void checkTagSharing(boolean start_of_day)
/*      */   {
/*  521 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*      */     
/*  523 */     if (uiFunctions != null)
/*      */     {
/*  525 */       TagManager tm = TagManagerFactory.getTagManager();
/*      */       
/*  527 */       if (start_of_day)
/*      */       {
/*  529 */         if (COConfigurationManager.getBooleanParameter("tag.sharing.default.checked", false))
/*      */         {
/*  531 */           return;
/*      */         }
/*      */         
/*  534 */         COConfigurationManager.setParameter("tag.sharing.default.checked", true);
/*      */         
/*  536 */         List<TagType> tag_types = tm.getTagTypes();
/*      */         
/*  538 */         boolean prompt_required = false;
/*      */         
/*  540 */         for (TagType tag_type : tag_types)
/*      */         {
/*  542 */           List<Tag> tags = tag_type.getTags();
/*      */           
/*  544 */           for (Tag tag : tags)
/*      */           {
/*  546 */             if (tag.isPublic())
/*      */             {
/*  548 */               prompt_required = true;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  553 */         if (!prompt_required)
/*      */         {
/*  555 */           return;
/*      */         }
/*      */       }
/*      */       
/*  559 */       String title = MessageText.getString("tag.sharing.enable.title");
/*      */       
/*  561 */       String text = MessageText.getString("tag.sharing.enable.text");
/*      */       
/*  563 */       UIFunctionsUserPrompter prompter = uiFunctions.getUserPrompter(title, text, new String[] { MessageText.getString("Button.yes"), MessageText.getString("Button.no") }, 0);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  568 */       prompter.setRemember("tag.share.default", true, MessageText.getString("MessageBoxWindow.nomoreprompting"));
/*      */       
/*      */ 
/*  571 */       prompter.setAutoCloseInMS(0);
/*      */       
/*  573 */       prompter.open(null);
/*      */       
/*  575 */       boolean share = prompter.waitUntilClosed() == 0;
/*      */       
/*  577 */       tm.setTagPublicDefault(share);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void createManualTag(UIFunctions.TagReturner tagReturner)
/*      */   {
/*  585 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*  586 */     if (uiFunctions != null) {
/*  587 */       uiFunctions.showCreateTagDialog(tagReturner);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void createSideBarMenuItems(Menu menu, Tag tag)
/*      */   {
/*  595 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*      */     
/*  597 */     TagType tag_type = tag.getTagType();
/*      */     
/*  599 */     boolean needs_separator_next = false;
/*      */     
/*  601 */     int countBefore = menu.getItemCount();
/*      */     
/*  603 */     if (tag_type.hasTagTypeFeature(1L)) {
/*  604 */       createTF_RateLimitMenuItems(menu, tag, tag_type, userMode);
/*      */     }
/*      */     
/*  607 */     if (tag_type.hasTagTypeFeature(4L)) {
/*  608 */       createTF_RunState(menu, tag);
/*      */     }
/*      */     
/*  611 */     if (tag_type.hasTagTypeFeature(16L)) {
/*  612 */       createTF_FileLocationMenuItems(menu, tag);
/*      */     }
/*      */     
/*  615 */     if (tag_type.hasTagTypeFeature(64L))
/*      */     {
/*  617 */       TagFeatureExecOnAssign tf_eoa = (TagFeatureExecOnAssign)tag;
/*      */       
/*  619 */       int supported_actions = tf_eoa.getSupportedActions();
/*      */       
/*  621 */       if (supported_actions != 0)
/*      */       {
/*  623 */         Menu eoa_menu = new Menu(menu.getShell(), 4);
/*      */         
/*  625 */         org.eclipse.swt.widgets.MenuItem eoa_item = new org.eclipse.swt.widgets.MenuItem(menu, 64);
/*      */         
/*  627 */         Messages.setLanguageText(eoa_item, "label.exec.on.assign");
/*      */         
/*  629 */         eoa_item.setMenu(eoa_menu);
/*      */         
/*  631 */         int[] action_ids = { 1, 2, 8, 16, 4, 64, 128, 32 };
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
/*  642 */         String[] action_keys = { "v3.MainWindow.button.delete", "v3.MainWindow.button.start", "v3.MainWindow.button.forcestart", "v3.MainWindow.button.notforcestart", "v3.MainWindow.button.stop", "v3.MainWindow.button.pause", "v3.MainWindow.button.resume", "label.script" };
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  652 */         for (int i = 0; i < action_ids.length; i++)
/*      */         {
/*  654 */           final int action_id = action_ids[i];
/*      */           
/*  656 */           if (tf_eoa.supportsAction(action_id))
/*      */           {
/*  658 */             if (action_id == 32)
/*      */             {
/*  660 */               org.eclipse.swt.widgets.MenuItem action_item = new org.eclipse.swt.widgets.MenuItem(eoa_menu, 8);
/*      */               
/*  662 */               String script = tf_eoa.getActionScript();
/*      */               
/*  664 */               if (script.length() > 23) {
/*  665 */                 script = script.substring(0, 20) + "...";
/*      */               }
/*      */               
/*  668 */               String msg = MessageText.getString(action_keys[i]);
/*      */               
/*  670 */               if (script.length() > 0)
/*      */               {
/*  672 */                 msg = msg + ": " + script;
/*      */               }
/*      */               
/*  675 */               msg = msg + "...";
/*      */               
/*  677 */               action_item.setText(msg);
/*      */               
/*  679 */               action_item.addListener(13, new Listener()
/*      */               {
/*      */                 public void handleEvent(Event event)
/*      */                 {
/*  683 */                   String msg = MessageText.getString("UpdateScript.message");
/*      */                   
/*  685 */                   SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("UpdateScript.title", "!" + msg + "!");
/*      */                   
/*  687 */                   entryWindow.setPreenteredText(this.val$tf_eoa.getActionScript(), false);
/*  688 */                   entryWindow.selectPreenteredText(true);
/*      */                   
/*  690 */                   entryWindow.prompt();
/*      */                   
/*  692 */                   if (entryWindow.hasSubmittedInput())
/*      */                   {
/*  694 */                     String text = entryWindow.getSubmittedInput().trim();
/*      */                     
/*  696 */                     this.val$tf_eoa.setActionScript(text);
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */             else
/*      */             {
/*  703 */               final org.eclipse.swt.widgets.MenuItem action_item = new org.eclipse.swt.widgets.MenuItem(eoa_menu, 32);
/*      */               
/*  705 */               Messages.setLanguageText(action_item, action_keys[i]);
/*      */               
/*  707 */               action_item.setSelection(tf_eoa.isActionEnabled(action_id));
/*      */               
/*  709 */               action_item.addListener(13, new Listener()
/*      */               {
/*      */                 public void handleEvent(Event event)
/*      */                 {
/*  713 */                   this.val$tf_eoa.setActionEnabled(action_id, action_item.getSelection());
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  724 */     if ((tag instanceof TagDownload))
/*      */     {
/*  726 */       needs_separator_next = true;
/*      */       
/*  728 */       org.eclipse.swt.widgets.MenuItem itemOptions = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*      */       
/*  730 */       Set<DownloadManager> dms = ((TagDownload)tag).getTaggedDownloads();
/*      */       
/*  732 */       Messages.setLanguageText(itemOptions, "cat.options");
/*  733 */       itemOptions.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  735 */           UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*  736 */           if (uiFunctions != null) {
/*  737 */             uiFunctions.getMDI().showEntryByID("TorrentOptionsView", this.val$dms.toArray(new DownloadManager[this.val$dms.size()]));
/*      */           }
/*      */         }
/*      */       });
/*      */       
/*      */ 
/*      */ 
/*  744 */       if (dms.size() == 0)
/*      */       {
/*  746 */         itemOptions.setEnabled(false);
/*      */       }
/*      */     }
/*      */     
/*  750 */     if (userMode > 0)
/*      */     {
/*  752 */       if (tag_type.hasTagTypeFeature(32L))
/*      */       {
/*  754 */         createTFProperitesMenuItems(menu, tag);
/*      */       }
/*      */     }
/*      */     
/*  758 */     if (menu.getItemCount() > countBefore) {
/*  759 */       needs_separator_next = true;
/*      */     }
/*      */     
/*  762 */     if (needs_separator_next)
/*      */     {
/*  764 */       new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*      */       
/*  766 */       needs_separator_next = false;
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
/*  790 */     if (tag_type.getTagType() == 3)
/*      */     {
/*  792 */       needs_separator_next = true;
/*      */       
/*  794 */       org.eclipse.swt.widgets.MenuItem search = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  795 */       Messages.setLanguageText(search, "tag.search");
/*  796 */       search.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  798 */           UIFunctionsManager.getUIFunctions().doSearch("tag:" + this.val$tag.getTagName(true));
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  803 */     addShareWithFriendsMenuItems(menu, tag, tag_type);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  808 */     if (tag_type.hasTagTypeFeature(2L))
/*      */     {
/*  810 */       final TagFeatureRSSFeed tfrss = (TagFeatureRSSFeed)tag;
/*      */       
/*      */ 
/*      */ 
/*  814 */       org.eclipse.swt.widgets.MenuItem rssOption = new org.eclipse.swt.widgets.MenuItem(menu, 32);
/*      */       
/*  816 */       rssOption.setSelection(tfrss.isTagRSSFeedEnabled());
/*      */       
/*  818 */       Messages.setLanguageText(rssOption, "cat.rss.gen");
/*  819 */       rssOption.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  821 */           boolean set = this.val$rssOption.getSelection();
/*  822 */           tfrss.setTagRSSFeedEnabled(set);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  827 */     if (tag_type.hasTagTypeFeature(8L)) {
/*  828 */       createXCodeMenuItems(menu, tag);
/*      */     }
/*      */     
/*  831 */     needs_separator_next = true;
/*      */     
/*  833 */     if (tag_type.getTagType() == 3)
/*      */     {
/*  835 */       MenuBuildUtils.addChatMenu(menu, "label.chat", getChatKey(tag));
/*      */     }
/*      */     
/*  838 */     org.eclipse.swt.widgets.MenuItem itemShowStats = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*      */     
/*  840 */     Messages.setLanguageText(itemShowStats, "tag.show.stats");
/*  841 */     itemShowStats.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  843 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*  844 */         uiFunctions.getMDI().loadEntryByID(StatsView.VIEW_ID, true, false, "TagStatsView");
/*      */       }
/*      */     });
/*      */     
/*  848 */     if (tag.getTaggableTypes() == 2)
/*      */     {
/*  850 */       org.eclipse.swt.widgets.MenuItem itemShowFiles = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*      */       
/*  852 */       Messages.setLanguageText(itemShowFiles, "menu.show.files");
/*  853 */       itemShowFiles.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  855 */           TagUIUtils.showFilesView((TagDownload)this.val$tag);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  860 */     if (needs_separator_next)
/*      */     {
/*  862 */       new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*      */       
/*  864 */       needs_separator_next = false;
/*      */     }
/*      */     
/*  867 */     boolean auto = tag_type.isTagTypeAuto();
/*      */     
/*  869 */     boolean closable = auto;
/*      */     
/*  871 */     if (tag.getTaggableTypes() == 2)
/*      */     {
/*  873 */       closable = true;
/*      */     }
/*      */     
/*  876 */     Menu[] menuShowHide = { null };
/*      */     
/*  878 */     if (closable) {
/*  879 */       createCloseableMenuItems(menu, tag, tag_type, menuShowHide, needs_separator_next);
/*      */     }
/*      */     
/*  882 */     if (!auto) {
/*  883 */       createNonAutoMenuItems(menu, tag, tag_type, menuShowHide);
/*      */     }
/*      */     
/*  886 */     org.eclipse.swt.widgets.MenuItem menuSettings = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  887 */     Messages.setLanguageText(menuSettings, "TagSettingsView.title");
/*  888 */     menuSettings.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  890 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*  891 */         uiFunctions.getMDI().showEntryByID("TagsOverview", this.val$tag);
/*      */       }
/*      */       
/*  894 */     });
/*  895 */     org.gudy.azureus2.plugins.ui.menus.MenuItem[] items = MenuItemManager.getInstance().getAllAsArray("tag_content");
/*      */     
/*      */ 
/*  898 */     if (items.length > 0) {
/*  899 */       MenuFactory.addSeparatorMenuItem(menu);
/*      */       
/*      */ 
/*  902 */       MenuBuildUtils.addPluginMenuItems(items, menu, true, true, new MenuBuildUtils.MenuItemPluginMenuControllerImpl(new Tag[] { tag }));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static void createTF_RunState(Menu menu, Tag tag)
/*      */   {
/*  909 */     TagFeatureRunState tf_run_state = (TagFeatureRunState)tag;
/*      */     
/*  911 */     int caps = tf_run_state.getRunStateCapabilities();
/*      */     
/*  913 */     int[] op_set = { 8, 1, 2, 4 };
/*      */     
/*      */ 
/*      */ 
/*  917 */     boolean[] can_ops_set = tf_run_state.getPerformableOperations(op_set);
/*      */     
/*  919 */     if ((caps & 0x8) != 0)
/*      */     {
/*  921 */       org.eclipse.swt.widgets.MenuItem itemOp = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  922 */       Messages.setLanguageText(itemOp, "MyTorrentsView.menu.queue");
/*  923 */       Utils.setMenuItemImage(itemOp, "start");
/*  924 */       itemOp.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  926 */           this.val$tf_run_state.performOperation(8);
/*      */         }
/*  928 */       });
/*  929 */       itemOp.setEnabled(can_ops_set[0]);
/*      */     }
/*      */     
/*  932 */     if ((caps & 0x1) != 0)
/*      */     {
/*  934 */       org.eclipse.swt.widgets.MenuItem itemOp = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  935 */       Messages.setLanguageText(itemOp, "MyTorrentsView.menu.stop");
/*  936 */       Utils.setMenuItemImage(itemOp, "stop");
/*  937 */       itemOp.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  939 */           this.val$tf_run_state.performOperation(1);
/*      */         }
/*  941 */       });
/*  942 */       itemOp.setEnabled(can_ops_set[1]);
/*      */     }
/*      */     
/*  945 */     if ((caps & 0x2) != 0)
/*      */     {
/*  947 */       org.eclipse.swt.widgets.MenuItem itemOp = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  948 */       Messages.setLanguageText(itemOp, "v3.MainWindow.button.pause");
/*  949 */       Utils.setMenuItemImage(itemOp, "pause");
/*  950 */       itemOp.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  952 */           this.val$tf_run_state.performOperation(2);
/*      */         }
/*  954 */       });
/*  955 */       itemOp.setEnabled(can_ops_set[2]);
/*      */     }
/*      */     
/*  958 */     if ((caps & 0x4) != 0)
/*      */     {
/*  960 */       org.eclipse.swt.widgets.MenuItem itemOp = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  961 */       Messages.setLanguageText(itemOp, "v3.MainWindow.button.resume");
/*  962 */       Utils.setMenuItemImage(itemOp, "start");
/*  963 */       itemOp.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  965 */           this.val$tf_run_state.performOperation(4);
/*      */         }
/*  967 */       });
/*  968 */       itemOp.setEnabled(can_ops_set[3]);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void createTF_RateLimitMenuItems(Menu menu, Tag tag, TagType tag_type, int userMode)
/*      */   {
/*  980 */     final TagFeatureRateLimit tf_rate_limit = (TagFeatureRateLimit)tag;
/*      */     
/*  982 */     boolean has_up = tf_rate_limit.supportsTagUploadLimit();
/*  983 */     boolean has_down = tf_rate_limit.supportsTagDownloadLimit();
/*      */     
/*  985 */     if ((has_up) || (has_down))
/*      */     {
/*  987 */       long kInB = DisplayFormatters.getKinB();
/*      */       
/*  989 */       long maxDownload = COConfigurationManager.getIntParameter("Max Download Speed KBs", 0) * kInB;
/*      */       
/*  991 */       long maxUpload = COConfigurationManager.getIntParameter("Max Upload Speed KBs", 0) * kInB;
/*      */       
/*      */ 
/*  994 */       int down_speed = tf_rate_limit.getTagDownloadLimit();
/*  995 */       int up_speed = tf_rate_limit.getTagUploadLimit();
/*      */       
/*  997 */       Map<String, Object> menu_properties = new HashMap();
/*      */       
/*  999 */       if ((tag_type.getTagType() == 4) || (tag_type.getTagType() == 3))
/*      */       {
/* 1001 */         if (has_up) {
/* 1002 */           menu_properties.put("enable_upload_disable", Boolean.valueOf(true));
/*      */         }
/* 1004 */         if (has_down) {
/* 1005 */           menu_properties.put("enable_download_disable", Boolean.valueOf(true));
/*      */         }
/*      */       }
/*      */       
/* 1009 */       org.gudy.azureus2.ui.swt.views.ViewUtils.addSpeedMenu(menu.getShell(), menu, has_up, has_down, true, true, down_speed == -1, down_speed == 0, down_speed, down_speed, maxDownload, up_speed == -1, up_speed == 0, up_speed, up_speed, maxUpload, 1, menu_properties, new org.gudy.azureus2.ui.swt.views.ViewUtils.SpeedAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void setDownSpeed(int val)
/*      */         {
/*      */ 
/* 1016 */           this.val$tf_rate_limit.setTagDownloadLimit(val);
/*      */         }
/*      */         
/*      */         public void setUpSpeed(int val) {
/* 1020 */           this.val$tf_rate_limit.setTagUploadLimit(val);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 1025 */     if (userMode > 0)
/*      */     {
/* 1027 */       if (tf_rate_limit.getTagUploadPriority() >= 0)
/*      */       {
/*      */ 
/* 1030 */         org.eclipse.swt.widgets.MenuItem upPriority = new org.eclipse.swt.widgets.MenuItem(menu, 32);
/*      */         
/* 1032 */         upPriority.setSelection(tf_rate_limit.getTagUploadPriority() > 0);
/*      */         
/* 1034 */         Messages.setLanguageText(upPriority, "cat.upload.priority");
/* 1035 */         upPriority.addListener(13, new Listener() {
/*      */           public void handleEvent(Event event) {
/* 1037 */             boolean set = this.val$upPriority.getSelection();
/* 1038 */             tf_rate_limit.setTagUploadPriority(set ? 1 : 0);
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/* 1044 */       if (tf_rate_limit.getTagMinShareRatio() >= 0)
/*      */       {
/* 1046 */         org.eclipse.swt.widgets.MenuItem itemSR = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*      */         
/* 1048 */         String existing = String.valueOf(tf_rate_limit.getTagMinShareRatio() / 1000.0F);
/*      */         
/* 1050 */         Messages.setLanguageText(itemSR, "menu.min.share.ratio", new String[] { existing });
/*      */         
/* 1052 */         itemSR.addListener(13, new Listener() {
/*      */           public void handleEvent(Event event) {
/* 1054 */             SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("min.sr.window.title", "min.sr.window.message");
/*      */             
/*      */ 
/* 1057 */             entryWindow.setPreenteredText(this.val$existing, false);
/* 1058 */             entryWindow.selectPreenteredText(true);
/*      */             
/* 1060 */             entryWindow.prompt();
/*      */             
/* 1062 */             if (entryWindow.hasSubmittedInput()) {
/*      */               try
/*      */               {
/* 1065 */                 String text = entryWindow.getSubmittedInput().trim();
/*      */                 
/* 1067 */                 int sr = 0;
/*      */                 
/* 1069 */                 if (text.length() > 0)
/*      */                 {
/*      */                   try {
/* 1072 */                     float f = Float.parseFloat(text);
/*      */                     
/* 1074 */                     sr = (int)(f * 1000.0F);
/*      */                     
/* 1076 */                     if (sr < 0)
/*      */                     {
/* 1078 */                       sr = 0;
/*      */                     }
/* 1080 */                     else if ((sr == 0) && (f > 0.0F))
/*      */                     {
/* 1082 */                       sr = 1;
/*      */                     }
/*      */                   }
/*      */                   catch (Throwable e)
/*      */                   {
/* 1087 */                     Debug.out(e);
/*      */                   }
/*      */                   
/* 1090 */                   tf_rate_limit.setTagMinShareRatio(sr);
/*      */                 }
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 1095 */                 Debug.out(e);
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 1102 */       if (tf_rate_limit.getTagMaxShareRatio() >= 0)
/*      */       {
/* 1104 */         org.eclipse.swt.widgets.MenuItem itemSR = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*      */         
/* 1106 */         String existing = String.valueOf(tf_rate_limit.getTagMaxShareRatio() / 1000.0F);
/*      */         
/* 1108 */         Messages.setLanguageText(itemSR, "menu.max.share.ratio", new String[] { existing });
/*      */         
/* 1110 */         itemSR.addListener(13, new Listener() {
/*      */           public void handleEvent(Event event) {
/* 1112 */             SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("max.sr.window.title", "max.sr.window.message");
/*      */             
/*      */ 
/* 1115 */             entryWindow.setPreenteredText(this.val$existing, false);
/* 1116 */             entryWindow.selectPreenteredText(true);
/*      */             
/* 1118 */             entryWindow.prompt();
/*      */             
/* 1120 */             if (entryWindow.hasSubmittedInput()) {
/*      */               try
/*      */               {
/* 1123 */                 String text = entryWindow.getSubmittedInput().trim();
/*      */                 
/* 1125 */                 int sr = 0;
/*      */                 
/* 1127 */                 if (text.length() > 0)
/*      */                 {
/*      */                   try {
/* 1130 */                     float f = Float.parseFloat(text);
/*      */                     
/* 1132 */                     sr = (int)(f * 1000.0F);
/*      */                     
/* 1134 */                     if (sr < 0)
/*      */                     {
/* 1136 */                       sr = 0;
/*      */                     }
/* 1138 */                     else if ((sr == 0) && (f > 0.0F))
/*      */                     {
/* 1140 */                       sr = 1;
/*      */                     }
/*      */                   }
/*      */                   catch (Throwable e)
/*      */                   {
/* 1145 */                     Debug.out(e);
/*      */                   }
/*      */                   
/* 1148 */                   tf_rate_limit.setTagMaxShareRatio(sr);
/*      */                 }
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 1153 */                 Debug.out(e);
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static void createTF_FileLocationMenuItems(Menu menu, Tag tag)
/*      */   {
/* 1165 */     final TagFeatureFileLocation fl = (TagFeatureFileLocation)tag;
/*      */     
/* 1167 */     if ((fl.supportsTagInitialSaveFolder()) || (fl.supportsTagMoveOnComplete()) || (fl.supportsTagCopyOnComplete()))
/*      */     {
/* 1169 */       Menu files_menu = new Menu(menu.getShell(), 4);
/*      */       
/* 1171 */       org.eclipse.swt.widgets.MenuItem files_item = new org.eclipse.swt.widgets.MenuItem(menu, 64);
/*      */       
/* 1173 */       Messages.setLanguageText(files_item, "ConfigView.section.files");
/*      */       
/* 1175 */       files_item.setMenu(files_menu);
/*      */       
/* 1177 */       if (fl.supportsTagInitialSaveFolder())
/*      */       {
/* 1179 */         Menu moc_menu = new Menu(files_menu.getShell(), 4);
/*      */         
/* 1181 */         org.eclipse.swt.widgets.MenuItem isl_item = new org.eclipse.swt.widgets.MenuItem(files_menu, 64);
/*      */         
/* 1183 */         Messages.setLanguageText(isl_item, "label.init.save.loc");
/*      */         
/* 1185 */         isl_item.setMenu(moc_menu);
/*      */         
/* 1187 */         org.eclipse.swt.widgets.MenuItem clear_item = new org.eclipse.swt.widgets.MenuItem(moc_menu, 64);
/*      */         
/* 1189 */         Messages.setLanguageText(clear_item, "Button.clear");
/*      */         
/* 1191 */         clear_item.addListener(13, new Listener() {
/*      */           public void handleEvent(Event event) {
/* 1193 */             this.val$fl.setTagInitialSaveFolder(null);
/*      */           }
/*      */           
/*      */ 
/* 1197 */         });
/* 1198 */         final File existing = fl.getTagInitialSaveFolder();
/*      */         
/* 1200 */         org.eclipse.swt.widgets.MenuItem apply_item = new org.eclipse.swt.widgets.MenuItem(moc_menu, 64);
/*      */         
/* 1202 */         Messages.setLanguageText(apply_item, "apply.to.current");
/*      */         
/* 1204 */         apply_item.addListener(13, new Listener() {
/*      */           public void handleEvent(Event event) {
/* 1206 */             TagUIUtils.applyLocationToCurrent(this.val$tag, existing, false);
/*      */           }
/* 1208 */         });
/* 1209 */         new org.eclipse.swt.widgets.MenuItem(moc_menu, 2);
/*      */         
/* 1211 */         if (existing != null)
/*      */         {
/* 1213 */           org.eclipse.swt.widgets.MenuItem current_item = new org.eclipse.swt.widgets.MenuItem(moc_menu, 16);
/* 1214 */           current_item.setSelection(true);
/*      */           
/* 1216 */           current_item.setText(existing.getAbsolutePath());
/*      */           
/* 1218 */           new org.eclipse.swt.widgets.MenuItem(moc_menu, 2);
/*      */         }
/*      */         else
/*      */         {
/* 1222 */           apply_item.setEnabled(false);
/* 1223 */           clear_item.setEnabled(false);
/*      */         }
/*      */         
/* 1226 */         org.eclipse.swt.widgets.MenuItem set_item = new org.eclipse.swt.widgets.MenuItem(moc_menu, 64);
/*      */         
/* 1228 */         Messages.setLanguageText(set_item, "label.set");
/*      */         
/* 1230 */         set_item.addListener(13, new Listener() {
/*      */           public void handleEvent(Event event) {
/* 1232 */             DirectoryDialog dd = new DirectoryDialog(this.val$moc_menu.getShell());
/*      */             
/* 1234 */             dd.setFilterPath(TorrentOpener.getFilterPathData());
/*      */             
/* 1236 */             dd.setText(MessageText.getString("MyTorrentsView.menu.movedata.dialog"));
/*      */             
/* 1238 */             String path = dd.open();
/*      */             
/* 1240 */             if (path != null)
/*      */             {
/* 1242 */               TorrentOpener.setFilterPathData(path);
/*      */               
/* 1244 */               fl.setTagInitialSaveFolder(new File(path));
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/* 1249 */       if (fl.supportsTagMoveOnComplete())
/*      */       {
/* 1251 */         Menu moc_menu = new Menu(files_menu.getShell(), 4);
/*      */         
/* 1253 */         org.eclipse.swt.widgets.MenuItem moc_item = new org.eclipse.swt.widgets.MenuItem(files_menu, 64);
/*      */         
/* 1255 */         Messages.setLanguageText(moc_item, "label.move.on.comp");
/*      */         
/* 1257 */         moc_item.setMenu(moc_menu);
/*      */         
/* 1259 */         org.eclipse.swt.widgets.MenuItem clear_item = new org.eclipse.swt.widgets.MenuItem(moc_menu, 64);
/*      */         
/* 1261 */         Messages.setLanguageText(clear_item, "Button.clear");
/*      */         
/* 1263 */         clear_item.addListener(13, new Listener() {
/*      */           public void handleEvent(Event event) {
/* 1265 */             this.val$fl.setTagMoveOnCompleteFolder(null);
/*      */ 
/*      */           }
/*      */           
/*      */ 
/* 1270 */         });
/* 1271 */         final File existing = fl.getTagMoveOnCompleteFolder();
/*      */         
/* 1273 */         org.eclipse.swt.widgets.MenuItem apply_item = new org.eclipse.swt.widgets.MenuItem(moc_menu, 64);
/*      */         
/* 1275 */         Messages.setLanguageText(apply_item, "apply.to.current");
/*      */         
/* 1277 */         apply_item.addListener(13, new Listener() {
/*      */           public void handleEvent(Event event) {
/* 1279 */             TagUIUtils.applyLocationToCurrent(this.val$tag, existing, true);
/*      */           }
/* 1281 */         });
/* 1282 */         new org.eclipse.swt.widgets.MenuItem(moc_menu, 2);
/*      */         
/* 1284 */         if (existing != null)
/*      */         {
/* 1286 */           org.eclipse.swt.widgets.MenuItem current_item = new org.eclipse.swt.widgets.MenuItem(moc_menu, 16);
/* 1287 */           current_item.setSelection(true);
/*      */           
/* 1289 */           current_item.setText(existing.getAbsolutePath());
/*      */           
/* 1291 */           new org.eclipse.swt.widgets.MenuItem(moc_menu, 2);
/*      */         }
/*      */         else
/*      */         {
/* 1295 */           apply_item.setEnabled(false);
/* 1296 */           clear_item.setEnabled(false);
/*      */         }
/*      */         
/* 1299 */         org.eclipse.swt.widgets.MenuItem set_item = new org.eclipse.swt.widgets.MenuItem(moc_menu, 64);
/*      */         
/* 1301 */         Messages.setLanguageText(set_item, "label.set");
/*      */         
/* 1303 */         set_item.addListener(13, new Listener() {
/*      */           public void handleEvent(Event event) {
/* 1305 */             DirectoryDialog dd = new DirectoryDialog(this.val$moc_menu.getShell());
/*      */             
/* 1307 */             dd.setFilterPath(TorrentOpener.getFilterPathData());
/*      */             
/* 1309 */             dd.setText(MessageText.getString("MyTorrentsView.menu.movedata.dialog"));
/*      */             
/* 1311 */             String path = dd.open();
/*      */             
/* 1313 */             if (path != null)
/*      */             {
/* 1315 */               TorrentOpener.setFilterPathData(path);
/*      */               
/* 1317 */               fl.setTagMoveOnCompleteFolder(new File(path));
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/* 1322 */       if (fl.supportsTagCopyOnComplete())
/*      */       {
/* 1324 */         Menu moc_menu = new Menu(files_menu.getShell(), 4);
/*      */         
/* 1326 */         org.eclipse.swt.widgets.MenuItem moc_item = new org.eclipse.swt.widgets.MenuItem(files_menu, 64);
/*      */         
/* 1328 */         Messages.setLanguageText(moc_item, "label.copy.on.comp");
/*      */         
/* 1330 */         moc_item.setMenu(moc_menu);
/*      */         
/* 1332 */         org.eclipse.swt.widgets.MenuItem clear_item = new org.eclipse.swt.widgets.MenuItem(moc_menu, 64);
/*      */         
/* 1334 */         Messages.setLanguageText(clear_item, "Button.clear");
/*      */         
/* 1336 */         clear_item.addListener(13, new Listener() {
/*      */           public void handleEvent(Event event) {
/* 1338 */             this.val$fl.setTagCopyOnCompleteFolder(null);
/*      */           }
/* 1340 */         });
/* 1341 */         new org.eclipse.swt.widgets.MenuItem(moc_menu, 2);
/*      */         
/* 1343 */         File existing = fl.getTagCopyOnCompleteFolder();
/*      */         
/* 1345 */         if (existing != null)
/*      */         {
/* 1347 */           org.eclipse.swt.widgets.MenuItem current_item = new org.eclipse.swt.widgets.MenuItem(moc_menu, 16);
/* 1348 */           current_item.setSelection(true);
/*      */           
/* 1350 */           current_item.setText(existing.getAbsolutePath());
/*      */           
/* 1352 */           new org.eclipse.swt.widgets.MenuItem(moc_menu, 2);
/*      */         }
/*      */         else
/*      */         {
/* 1356 */           clear_item.setEnabled(false);
/*      */         }
/*      */         
/* 1359 */         org.eclipse.swt.widgets.MenuItem set_item = new org.eclipse.swt.widgets.MenuItem(moc_menu, 64);
/*      */         
/* 1361 */         Messages.setLanguageText(set_item, "label.set");
/*      */         
/* 1363 */         set_item.addListener(13, new Listener() {
/*      */           public void handleEvent(Event event) {
/* 1365 */             DirectoryDialog dd = new DirectoryDialog(this.val$moc_menu.getShell());
/*      */             
/* 1367 */             dd.setFilterPath(TorrentOpener.getFilterPathData());
/*      */             
/* 1369 */             dd.setText(MessageText.getString("MyTorrentsView.menu.movedata.dialog"));
/*      */             
/* 1371 */             String path = dd.open();
/*      */             
/* 1373 */             if (path != null)
/*      */             {
/* 1375 */               TorrentOpener.setFilterPathData(path);
/*      */               
/* 1377 */               fl.setTagCopyOnCompleteFolder(new File(path));
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private static void createTFProperitesMenuItems(Menu menu, Tag tag) {
/* 1386 */     TagFeatureProperties props = (TagFeatureProperties)tag;
/*      */     
/* 1388 */     TagFeatureProperties.TagProperty[] tps = props.getSupportedProperties();
/*      */     
/* 1390 */     if (tps.length > 0)
/*      */     {
/* 1392 */       Menu props_menu = new Menu(menu.getShell(), 4);
/*      */       
/* 1394 */       org.eclipse.swt.widgets.MenuItem props_item = new org.eclipse.swt.widgets.MenuItem(menu, 64);
/*      */       
/* 1396 */       Messages.setLanguageText(props_item, "label.properties");
/*      */       
/* 1398 */       props_item.setMenu(props_menu);
/*      */       
/* 1400 */       for (final TagFeatureProperties.TagProperty tp : tps)
/*      */       {
/* 1402 */         if (tp.getType() == 1)
/*      */         {
/* 1404 */           String tp_name = tp.getName(false);
/*      */           
/* 1406 */           if (tp_name.equals("constraint"))
/*      */           {
/* 1408 */             org.eclipse.swt.widgets.MenuItem const_item = new org.eclipse.swt.widgets.MenuItem(props_menu, 8);
/*      */             
/* 1410 */             Messages.setLanguageText(const_item, "label.contraints");
/*      */             
/* 1412 */             const_item.addListener(13, new Listener()
/*      */             {
/*      */               public void handleEvent(Event event)
/*      */               {
/* 1416 */                 String[] old_value = this.val$tp.getStringList();
/*      */                 
/*      */                 String def_val;
/*      */                 String def_val;
/* 1420 */                 if ((old_value != null) && (old_value.length > 0))
/*      */                 {
/* 1422 */                   def_val = old_value[0];
/*      */                 }
/*      */                 else
/*      */                 {
/* 1426 */                   def_val = "";
/*      */                 }
/*      */                 
/* 1429 */                 String msg = MessageText.getString("UpdateConstraint.message");
/*      */                 
/* 1431 */                 SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("UpdateConstraint.title", "!" + msg + "!");
/*      */                 
/* 1433 */                 entryWindow.setPreenteredText(def_val, false);
/* 1434 */                 entryWindow.selectPreenteredText(true);
/*      */                 
/* 1436 */                 entryWindow.prompt();
/*      */                 
/* 1438 */                 if (entryWindow.hasSubmittedInput())
/*      */                   try
/*      */                   {
/* 1441 */                     String text = entryWindow.getSubmittedInput().trim();
/*      */                     
/* 1443 */                     if (text.length() == 0)
/*      */                     {
/* 1445 */                       this.val$tp.setStringList(null);
/*      */                     }
/*      */                     else
/*      */                     {
/* 1449 */                       String old_options = old_value.length > 1 ? old_value[1] : "";
/*      */                       
/* 1451 */                       this.val$tp.setStringList(new String[] { text, old_options });
/*      */                     }
/*      */                   }
/*      */                   catch (Throwable e) {
/* 1455 */                     Debug.out(e); } } }); } else { final TrackersUtil tut;
/*      */             String str_merge;
/*      */             String str_replace;
/*      */             String str_remove;
/*      */             final List<String> selected;
/* 1460 */             Menu ttemp_menu; if (tp_name.equals("tracker_templates"))
/*      */             {
/* 1462 */               tut = TrackersUtil.getInstance();
/*      */               
/* 1464 */               List<String> templates = new ArrayList(tut.getMultiTrackers().keySet());
/*      */               
/* 1466 */               str_merge = MessageText.getString("label.merge");
/* 1467 */               str_replace = MessageText.getString("label.replace");
/* 1468 */               str_remove = MessageText.getString("Button.remove");
/*      */               
/* 1470 */               String[] val = tp.getStringList();
/*      */               
/*      */ 
/*      */ 
/* 1474 */               selected = new ArrayList();
/*      */               String def_str;
/* 1476 */               String def_str; if ((val == null) || (val.length == 0))
/*      */               {
/* 1478 */                 def_str = "";
/*      */               }
/*      */               else
/*      */               {
/* 1482 */                 def_str = "";
/*      */                 
/* 1484 */                 for (String v : val)
/*      */                 {
/* 1486 */                   String[] bits = v.split(":");
/*      */                   
/* 1488 */                   if (bits.length == 2)
/*      */                   {
/* 1490 */                     String tn = bits[1];
/*      */                     
/* 1492 */                     if (templates.contains(tn))
/*      */                     {
/* 1494 */                       String type = bits[0];
/*      */                       
/* 1496 */                       if (type.equals("m"))
/*      */                       {
/* 1498 */                         tn = tn + ": " + str_merge;
/*      */                       }
/* 1500 */                       else if (type.equals("r"))
/*      */                       {
/* 1502 */                         tn = tn + ": " + str_replace;
/*      */                       }
/*      */                       else {
/* 1505 */                         tn = tn + ": " + str_remove;
/*      */                       }
/*      */                       
/* 1508 */                       selected.add(v);
/*      */                       
/* 1510 */                       def_str = def_str + (def_str.length() == 0 ? "" : ", ") + tn;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/* 1516 */               Collections.sort(templates);
/*      */               
/*      */ 
/*      */ 
/* 1520 */               ttemp_menu = new Menu(menu.getShell(), 4);
/*      */               
/* 1522 */               org.eclipse.swt.widgets.MenuItem ttemp_item = new org.eclipse.swt.widgets.MenuItem(menu, 64);
/*      */               
/* 1524 */               ttemp_item.setText(MessageText.getString("label.tracker.templates") + (def_str.length() == 0 ? "" : new StringBuilder().append(" (").append(def_str).append(")  ").toString()));
/*      */               
/* 1526 */               ttemp_item.setMenu(ttemp_menu);
/*      */               
/* 1528 */               org.eclipse.swt.widgets.MenuItem new_item = new org.eclipse.swt.widgets.MenuItem(ttemp_menu, 8);
/*      */               
/* 1530 */               Messages.setLanguageText(new_item, "wizard.multitracker.new");
/*      */               
/* 1532 */               new_item.addListener(13, new Listener()
/*      */               {
/*      */                 public void handleEvent(Event event)
/*      */                 {
/* 1536 */                   List<List<String>> group = new ArrayList();
/* 1537 */                   List<String> tracker = new ArrayList();
/* 1538 */                   group.add(tracker);
/*      */                   
/* 1540 */                   new org.gudy.azureus2.ui.swt.maketorrent.MultiTrackerEditor(this.val$props_menu.getShell(), null, group, new org.gudy.azureus2.ui.swt.maketorrent.TrackerEditorListener()
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                     public void trackersChanged(String oldName, String newName, List<List<String>> trackers)
/*      */                     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1552 */                       if (trackers != null)
/*      */                       {
/* 1554 */                         TagUIUtils.30.this.val$tut.addMultiTracker(newName, trackers);
/*      */                       }
/*      */                     }
/*      */                   });
/*      */                 }
/* 1559 */               });
/* 1560 */               org.eclipse.swt.widgets.MenuItem reapply_item = new org.eclipse.swt.widgets.MenuItem(ttemp_menu, 8);
/*      */               
/* 1562 */               Messages.setLanguageText(reapply_item, "label.reapply");
/*      */               
/* 1564 */               reapply_item.addListener(13, new Listener()
/*      */               {
/*      */                 public void handleEvent(Event event)
/*      */                 {
/* 1568 */                   this.val$tp.syncListeners();
/*      */                 }
/* 1570 */               });
/* 1571 */               reapply_item.setEnabled(def_str.length() > 0);
/*      */               
/* 1573 */               if (templates.size() > 0)
/*      */               {
/* 1575 */                 new org.eclipse.swt.widgets.MenuItem(ttemp_menu, 2);
/*      */                 
/* 1577 */                 for (final String template_name : templates)
/*      */                 {
/* 1579 */                   Menu t_menu = new Menu(ttemp_menu.getShell(), 4);
/*      */                   
/* 1581 */                   org.eclipse.swt.widgets.MenuItem t_item = new org.eclipse.swt.widgets.MenuItem(ttemp_menu, 64);
/*      */                   
/* 1583 */                   t_item.setText(template_name);
/*      */                   
/* 1585 */                   t_item.setMenu(t_menu);
/*      */                   
/* 1587 */                   boolean r_selected = false;
/*      */                   
/* 1589 */                   for (int i = 0; i < 3; i++)
/*      */                   {
/* 1591 */                     org.eclipse.swt.widgets.MenuItem sel_item = new org.eclipse.swt.widgets.MenuItem(t_menu, 32);
/*      */                     
/* 1593 */                     final String key = (i == 1 ? "r" : i == 0 ? "m" : "x") + ":" + template_name;
/*      */                     
/* 1595 */                     sel_item.setText(i == 1 ? str_replace : i == 0 ? str_merge : str_remove);
/*      */                     
/* 1597 */                     boolean is_sel = selected.contains(key);
/*      */                     
/* 1599 */                     r_selected |= is_sel;
/*      */                     
/* 1601 */                     sel_item.setSelection(is_sel);
/*      */                     
/* 1603 */                     sel_item.addListener(13, new Listener()
/*      */                     {
/*      */                       public void handleEvent(Event event)
/*      */                       {
/* 1607 */                         if (this.val$sel_item.getSelection())
/*      */                         {
/* 1609 */                           selected.add(key);
/*      */                         }
/*      */                         else
/*      */                         {
/* 1613 */                           selected.remove(key);
/*      */                         }
/*      */                         
/* 1616 */                         Utils.getOffOfSWTThread(new AERunnable()
/*      */                         {
/*      */                           public void runSupport()
/*      */                           {
/* 1620 */                             TagUIUtils.32.this.val$tp.setStringList((String[])TagUIUtils.32.this.val$selected.toArray(new String[TagUIUtils.32.this.val$selected.size()]));
/*      */                           }
/*      */                         });
/*      */                       }
/*      */                     });
/*      */                   }
/*      */                   
/* 1627 */                   if (r_selected)
/*      */                   {
/* 1629 */                     Utils.setMenuItemImage(t_item, "graytick");
/*      */                   }
/*      */                   
/* 1632 */                   new org.eclipse.swt.widgets.MenuItem(t_menu, 2);
/*      */                   
/* 1634 */                   org.eclipse.swt.widgets.MenuItem edit_item = new org.eclipse.swt.widgets.MenuItem(t_menu, 8);
/*      */                   
/* 1636 */                   Messages.setLanguageText(edit_item, "wizard.multitracker.edit");
/*      */                   
/* 1638 */                   edit_item.addListener(13, new Listener()
/*      */                   {
/*      */                     public void handleEvent(Event event)
/*      */                     {
/* 1642 */                       new org.gudy.azureus2.ui.swt.maketorrent.MultiTrackerEditor(this.val$props_menu.getShell(), template_name, (List)tut.getMultiTrackers().get(template_name), new org.gudy.azureus2.ui.swt.maketorrent.TrackerEditorListener()
/*      */                       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                         public void trackersChanged(String oldName, String newName, List<List<String>> trackers)
/*      */                         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1654 */                           if ((oldName != null) && (!oldName.equals(newName)))
/*      */                           {
/* 1656 */                             TagUIUtils.33.this.val$tut.removeMultiTracker(oldName);
/*      */                           }
/*      */                           
/* 1659 */                           TagUIUtils.33.this.val$tut.addMultiTracker(newName, trackers);
/*      */                         }
/*      */                       });
/*      */                     }
/* 1663 */                   });
/* 1664 */                   org.eclipse.swt.widgets.MenuItem del_item = new org.eclipse.swt.widgets.MenuItem(t_menu, 8);
/*      */                   
/* 1666 */                   Messages.setLanguageText(del_item, "FileItem.delete");
/*      */                   
/* 1668 */                   Utils.setMenuItemImage(del_item, "delete");
/*      */                   
/* 1670 */                   del_item.addListener(13, new Listener()
/*      */                   {
/*      */                     public void handleEvent(Event event)
/*      */                     {
/* 1674 */                       MessageBoxShell mb = new MessageBoxShell(MessageText.getString("message.confirm.delete.title"), MessageText.getString("message.confirm.delete.text", new String[] { this.val$template_name }), new String[] { MessageText.getString("Button.yes"), MessageText.getString("Button.no") }, 1);
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
/* 1685 */                       mb.open(new com.aelitis.azureus.ui.UserPrompterResultListener() {
/*      */                         public void prompterClosed(int result) {
/* 1687 */                           if (result == 0)
/* 1688 */                             TagUIUtils.34.this.val$tut.removeMultiTracker(TagUIUtils.34.this.val$template_name);
/*      */                         }
/*      */                       });
/*      */                     }
/*      */                   });
/*      */                 }
/*      */               }
/*      */             } else {
/* 1696 */               String[] val = tp.getStringList();
/*      */               
/*      */               String def_str;
/*      */               String def_str;
/* 1700 */               if ((val == null) || (val.length == 0))
/*      */               {
/* 1702 */                 def_str = "";
/*      */               }
/*      */               else
/*      */               {
/* 1706 */                 def_str = "";
/*      */                 
/* 1708 */                 for (String v : val)
/*      */                 {
/* 1710 */                   def_str = def_str + (def_str.length() == 0 ? "" : ", ") + v;
/*      */                 }
/*      */               }
/*      */               
/* 1714 */               org.eclipse.swt.widgets.MenuItem set_item = new org.eclipse.swt.widgets.MenuItem(props_menu, 8);
/*      */               
/* 1716 */               set_item.setText(tp.getName(true) + (def_str.length() == 0 ? "" : new StringBuilder().append(" (").append(def_str).append(") ").toString()) + "...");
/*      */               
/* 1718 */               final String f_def_str = def_str;
/*      */               
/* 1720 */               set_item.addListener(13, new Listener()
/*      */               {
/*      */                 public void handleEvent(Event event) {
/* 1723 */                   String msg = MessageText.getString("UpdateProperty.list.message", new String[] { this.val$tp.getName(true) });
/*      */                   
/* 1725 */                   SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("UpdateProperty.title", "!" + msg + "!");
/*      */                   
/* 1727 */                   entryWindow.setPreenteredText(f_def_str, false);
/* 1728 */                   entryWindow.selectPreenteredText(true);
/*      */                   
/* 1730 */                   entryWindow.prompt();
/*      */                   
/* 1732 */                   if (entryWindow.hasSubmittedInput())
/*      */                     try
/*      */                     {
/* 1735 */                       String text = entryWindow.getSubmittedInput().trim();
/*      */                       
/* 1737 */                       if (text.length() == 0)
/*      */                       {
/* 1739 */                         this.val$tp.setStringList(null);
/*      */                       }
/*      */                       else {
/* 1742 */                         text = text.replace(';', ',');
/* 1743 */                         text = text.replace(' ', ',');
/* 1744 */                         text = text.replaceAll("[,]+", ",");
/*      */                         
/* 1746 */                         String[] bits = text.split(",");
/*      */                         
/* 1748 */                         List<String> vals = new ArrayList();
/*      */                         
/* 1750 */                         for (String bit : bits)
/*      */                         {
/* 1752 */                           bit = bit.trim();
/*      */                           
/* 1754 */                           if (bit.length() > 0)
/*      */                           {
/* 1756 */                             vals.add(bit);
/*      */                           }
/*      */                         }
/*      */                         
/* 1760 */                         if (vals.size() == 0)
/*      */                         {
/* 1762 */                           this.val$tp.setStringList(null);
/*      */                         }
/*      */                         else {
/* 1765 */                           this.val$tp.setStringList((String[])vals.toArray(new String[vals.size()]));
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                     catch (Throwable e) {
/* 1770 */                       Debug.out(e);
/*      */                     }
/*      */                 }
/*      */               });
/*      */             }
/* 1775 */           } } else if (tp.getType() == 2)
/*      */         {
/* 1777 */           final org.eclipse.swt.widgets.MenuItem set_item = new org.eclipse.swt.widgets.MenuItem(props_menu, 32);
/*      */           
/* 1779 */           set_item.setText(tp.getName(true));
/*      */           
/* 1781 */           Boolean val = tp.getBoolean();
/*      */           
/* 1783 */           set_item.setSelection((val != null) && (val.booleanValue()));
/*      */           
/* 1785 */           set_item.addListener(13, new Listener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void handleEvent(Event event)
/*      */             {
/*      */ 
/*      */ 
/* 1793 */               this.val$tp.setBoolean(Boolean.valueOf(set_item.getSelection()));
/*      */             }
/*      */           });
/*      */         }
/*      */         else
/*      */         {
/* 1799 */           Debug.out("Unknown property");
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private static void addShareWithFriendsMenuItems(Menu menu, Tag tag, TagType tag_type)
/*      */   {
/* 1807 */     PluginInterface bpi = org.gudy.azureus2.pluginsimpl.local.PluginInitializer.getDefaultInterface().getPluginManager().getPluginInterfaceByClass(BuddyPlugin.class);
/*      */     Menu share_menu;
/*      */     final String tag_name;
/* 1810 */     boolean is_public; if ((tag_type.getTagType() == 3) && (bpi != null))
/*      */     {
/* 1812 */       TagFeatureProperties props = (TagFeatureProperties)tag;
/*      */       
/* 1814 */       TagFeatureProperties.TagProperty tp = props.getProperty("untagged");
/*      */       
/* 1816 */       Boolean is_ut = tp == null ? null : tp.getBoolean();
/*      */       
/* 1818 */       if ((is_ut == null) || (!is_ut.booleanValue()))
/*      */       {
/* 1820 */         final BuddyPlugin buddy_plugin = (BuddyPlugin)bpi.getPlugin();
/*      */         
/* 1822 */         if (buddy_plugin.isClassicEnabled())
/*      */         {
/* 1824 */           share_menu = new Menu(menu.getShell(), 4);
/* 1825 */           org.eclipse.swt.widgets.MenuItem share_item = new org.eclipse.swt.widgets.MenuItem(menu, 64);
/* 1826 */           Messages.setLanguageText(share_item, "azbuddy.ui.menu.cat.share");
/* 1827 */           share_item.setText(share_item.getText() + "  ");
/* 1828 */           share_item.setMenu(share_menu);
/*      */           
/* 1830 */           List<BuddyPluginBuddy> buddies = buddy_plugin.getBuddies();
/*      */           
/* 1832 */           if (buddies.size() == 0)
/*      */           {
/* 1834 */             org.eclipse.swt.widgets.MenuItem item = new org.eclipse.swt.widgets.MenuItem(share_menu, 32);
/*      */             
/* 1836 */             item.setText(MessageText.getString("general.add.friends"));
/*      */             
/* 1838 */             item.setEnabled(false);
/*      */           }
/*      */           else {
/* 1841 */             tag_name = tag.getTagName(true);
/*      */             
/* 1843 */             is_public = buddy_plugin.isPublicTagOrCategory(tag_name);
/*      */             
/* 1845 */             org.eclipse.swt.widgets.MenuItem itemPubCat = new org.eclipse.swt.widgets.MenuItem(share_menu, 32);
/*      */             
/* 1847 */             Messages.setLanguageText(itemPubCat, "general.all.friends");
/*      */             
/* 1849 */             itemPubCat.setSelection(is_public);
/*      */             
/* 1851 */             itemPubCat.addListener(13, new Listener() {
/*      */               public void handleEvent(Event event) {
/* 1853 */                 if (this.val$is_public)
/*      */                 {
/* 1855 */                   buddy_plugin.removePublicTagOrCategory(tag_name);
/*      */                 }
/*      */                 else
/*      */                 {
/* 1859 */                   buddy_plugin.addPublicTagOrCategory(tag_name);
/*      */                 }
/*      */                 
/*      */               }
/* 1863 */             });
/* 1864 */             new org.eclipse.swt.widgets.MenuItem(share_menu, 2);
/*      */             
/* 1866 */             for (final BuddyPluginBuddy buddy : buddies)
/*      */             {
/* 1868 */               if (buddy.getNickName() != null)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 1873 */                 boolean auth = buddy.isLocalRSSTagOrCategoryAuthorised(tag_name);
/*      */                 
/* 1875 */                 org.eclipse.swt.widgets.MenuItem itemShare = new org.eclipse.swt.widgets.MenuItem(share_menu, 32);
/*      */                 
/* 1877 */                 itemShare.setText(buddy.getName());
/*      */                 
/* 1879 */                 itemShare.setSelection((auth) || (is_public));
/*      */                 
/* 1881 */                 if (is_public)
/*      */                 {
/* 1883 */                   itemShare.setEnabled(false);
/*      */                 }
/*      */                 
/* 1886 */                 itemShare.addListener(13, new Listener() {
/*      */                   public void handleEvent(Event event) {
/* 1888 */                     if (this.val$auth)
/*      */                     {
/* 1890 */                       buddy.removeLocalAuthorisedRSSTagOrCategory(tag_name);
/*      */                     }
/*      */                     else
/*      */                     {
/* 1894 */                       buddy.addLocalAuthorisedRSSTagOrCategory(tag_name);
/*      */                     }
/*      */                   }
/*      */                 });
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static void createXCodeMenuItems(Menu menu, Tag tag)
/*      */   {
/* 1909 */     final TagFeatureTranscode tf_xcode = (TagFeatureTranscode)tag;
/*      */     
/* 1911 */     if (tf_xcode.supportsTagTranscode())
/*      */     {
/* 1913 */       AZ3Functions.provider provider = com.aelitis.azureus.core.util.AZ3Functions.getProvider();
/*      */       
/* 1915 */       if (provider != null)
/*      */       {
/* 1917 */         AZ3Functions.provider.TranscodeTarget[] tts = provider.getTranscodeTargets();
/*      */         
/* 1919 */         if (tts.length > 0)
/*      */         {
/* 1921 */           Menu t_menu = new Menu(menu.getShell(), 4);
/*      */           
/* 1923 */           org.eclipse.swt.widgets.MenuItem t_item = new org.eclipse.swt.widgets.MenuItem(menu, 64);
/*      */           
/* 1925 */           Messages.setLanguageText(t_item, "cat.autoxcode");
/*      */           
/* 1927 */           t_item.setMenu(t_menu);
/*      */           
/* 1929 */           String[] existing = tf_xcode.getTagTranscodeTarget();
/*      */           
/* 1931 */           for (AZ3Functions.provider.TranscodeTarget tt : tts)
/*      */           {
/* 1933 */             AZ3Functions.provider.TranscodeProfile[] profiles = tt.getProfiles();
/*      */             
/* 1935 */             if (profiles.length > 0)
/*      */             {
/* 1937 */               Menu tt_menu = new Menu(t_menu.getShell(), 4);
/*      */               
/* 1939 */               org.eclipse.swt.widgets.MenuItem tt_item = new org.eclipse.swt.widgets.MenuItem(t_menu, 64);
/*      */               
/* 1941 */               tt_item.setText(tt.getName());
/*      */               
/* 1943 */               tt_item.setMenu(tt_menu);
/*      */               
/* 1945 */               for (final AZ3Functions.provider.TranscodeProfile tp : profiles)
/*      */               {
/* 1947 */                 final org.eclipse.swt.widgets.MenuItem p_item = new org.eclipse.swt.widgets.MenuItem(tt_menu, 32);
/*      */                 
/* 1949 */                 p_item.setText(tp.getName());
/*      */                 
/* 1951 */                 boolean selected = (existing != null) && (existing[0].equals(tp.getUID()));
/*      */                 
/* 1953 */                 if (selected)
/*      */                 {
/* 1955 */                   Utils.setMenuItemImage(tt_item, "graytick");
/*      */                 }
/*      */                 
/* 1958 */                 p_item.setSelection(selected);
/*      */                 
/* 1960 */                 p_item.addListener(13, new Listener()
/*      */                 {
/*      */                   public void handleEvent(Event event) {
/* 1963 */                     String name = this.val$tt.getName() + " - " + tp.getName();
/*      */                     
/* 1965 */                     if (p_item.getSelection())
/*      */                     {
/* 1967 */                       tf_xcode.setTagTranscodeTarget(tp.getUID(), name);
/*      */                     }
/*      */                     else
/*      */                     {
/* 1971 */                       tf_xcode.setTagTranscodeTarget(null, null);
/*      */                     }
/*      */                   }
/*      */                 });
/*      */               }
/*      */               
/* 1977 */               new org.eclipse.swt.widgets.MenuItem(tt_menu, 2);
/*      */               
/* 1979 */               final org.eclipse.swt.widgets.MenuItem no_xcode_item = new org.eclipse.swt.widgets.MenuItem(tt_menu, 32);
/*      */               
/* 1981 */               final String never_str = MessageText.getString("v3.menu.device.defaultprofile.never");
/*      */               
/* 1983 */               no_xcode_item.setText(never_str);
/*      */               
/* 1985 */               final String never_uid = tt.getID() + "/blank";
/*      */               
/* 1987 */               boolean selected = (existing != null) && (existing[0].equals(never_uid));
/*      */               
/* 1989 */               if (selected)
/*      */               {
/* 1991 */                 Utils.setMenuItemImage(tt_item, "graytick");
/*      */               }
/*      */               
/* 1994 */               no_xcode_item.setSelection(selected);
/*      */               
/* 1996 */               no_xcode_item.addListener(13, new Listener()
/*      */               {
/*      */                 public void handleEvent(Event event)
/*      */                 {
/* 2000 */                   String name = this.val$tt.getName() + " - " + never_str;
/*      */                   
/* 2002 */                   if (no_xcode_item.getSelection())
/*      */                   {
/* 2004 */                     tf_xcode.setTagTranscodeTarget(never_uid, name);
/*      */                   }
/*      */                   else
/*      */                   {
/* 2008 */                     tf_xcode.setTagTranscodeTarget(null, null);
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         }
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
/*      */   private static void createCloseableMenuItems(Menu menu, Tag tag, TagType tag_type, Menu[] menuShowHide, boolean needs_separator_next)
/*      */   {
/* 2028 */     List<Tag> tags = tag_type.getTags();
/*      */     
/* 2030 */     int visible_count = 0;
/* 2031 */     int invisible_count = 0;
/*      */     
/* 2033 */     for (Tag t : tags)
/*      */     {
/* 2035 */       if (t.isVisible()) {
/* 2036 */         visible_count++;
/*      */       } else {
/* 2038 */         invisible_count++;
/*      */       }
/*      */     }
/*      */     
/* 2042 */     menuShowHide[0] = new Menu(menu.getShell(), 4);
/*      */     
/* 2044 */     org.eclipse.swt.widgets.MenuItem showhideitem = new org.eclipse.swt.widgets.MenuItem(menu, 64);
/* 2045 */     showhideitem.setText(MessageText.getString("label.showhide.tag"));
/* 2046 */     showhideitem.setMenu(menuShowHide[0]);
/*      */     
/* 2048 */     org.eclipse.swt.widgets.MenuItem title = new org.eclipse.swt.widgets.MenuItem(menuShowHide[0], 8);
/* 2049 */     title.setText("[" + tag_type.getTagTypeName(true) + "]");
/* 2050 */     title.setEnabled(false);
/* 2051 */     new org.eclipse.swt.widgets.MenuItem(menuShowHide[0], 2);
/*      */     
/* 2053 */     if (invisible_count > 0) {
/* 2054 */       org.eclipse.swt.widgets.MenuItem showAll = new org.eclipse.swt.widgets.MenuItem(menuShowHide[0], 8);
/* 2055 */       Messages.setLanguageText(showAll, "label.show.all");
/* 2056 */       showAll.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/* 2058 */           for (Tag t : this.val$tags)
/*      */           {
/* 2060 */             if (!t.isVisible()) {
/* 2061 */               t.setVisible(true);
/*      */             }
/*      */           }
/*      */         }
/* 2065 */       });
/* 2066 */       needs_separator_next = true;
/*      */     }
/*      */     
/* 2069 */     if (visible_count > 0) {
/* 2070 */       org.eclipse.swt.widgets.MenuItem hideAll = new org.eclipse.swt.widgets.MenuItem(menuShowHide[0], 8);
/* 2071 */       Messages.setLanguageText(hideAll, "popup.error.hideall");
/* 2072 */       hideAll.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/* 2074 */           for (Tag t : this.val$tags)
/*      */           {
/* 2076 */             if (t.isVisible()) {
/* 2077 */               t.setVisible(false);
/*      */             }
/*      */           }
/*      */         }
/* 2081 */       });
/* 2082 */       needs_separator_next = true;
/*      */     }
/*      */     Map<String, Tag> menu_name_map;
/* 2085 */     if (tags.size() > 0)
/*      */     {
/* 2087 */       if (needs_separator_next)
/*      */       {
/* 2089 */         new org.eclipse.swt.widgets.MenuItem(menuShowHide[0], 2);
/*      */         
/* 2091 */         needs_separator_next = false;
/*      */       }
/*      */       
/* 2094 */       List<String> menu_names = new ArrayList();
/* 2095 */       menu_name_map = new IdentityHashMap();
/*      */       
/* 2097 */       for (Tag t : tags)
/*      */       {
/* 2099 */         String name = t.getTagName(true);
/*      */         
/* 2101 */         menu_names.add(name);
/* 2102 */         menu_name_map.put(name, t);
/*      */       }
/*      */       
/* 2105 */       List<Object> menu_structure = MenuBuildUtils.splitLongMenuListIntoHierarchy(menu_names, 20);
/*      */       
/* 2107 */       for (Object obj : menu_structure)
/*      */       {
/* 2109 */         List<Tag> bucket_tags = new ArrayList();
/*      */         
/*      */ 
/*      */ 
/* 2113 */         if ((obj instanceof String))
/*      */         {
/* 2115 */           Menu parent_menu = menuShowHide[0];
/*      */           
/* 2117 */           bucket_tags.add(menu_name_map.get((String)obj));
/*      */         }
/*      */         else
/*      */         {
/* 2121 */           Object[] entry = (Object[])obj;
/*      */           
/* 2123 */           List<String> tag_names = (List)entry[1];
/*      */           
/* 2125 */           boolean sub_all_visible = true;
/* 2126 */           boolean sub_some_visible = false;
/*      */           
/* 2128 */           for (String name : tag_names)
/*      */           {
/* 2130 */             Tag sub_tag = (Tag)menu_name_map.get(name);
/*      */             
/* 2132 */             if (sub_tag.isVisible())
/*      */             {
/* 2134 */               sub_some_visible = true;
/*      */             }
/*      */             else
/*      */             {
/* 2138 */               sub_all_visible = false;
/*      */             }
/*      */             
/* 2141 */             bucket_tags.add(sub_tag);
/*      */           }
/*      */           
/*      */           String mod;
/*      */           String mod;
/* 2146 */           if (sub_all_visible)
/*      */           {
/* 2148 */             mod = " (*)";
/*      */           } else { String mod;
/* 2150 */             if (sub_some_visible)
/*      */             {
/* 2152 */               mod = " (+)";
/*      */             }
/*      */             else
/*      */             {
/* 2156 */               mod = "";
/*      */             }
/*      */           }
/* 2159 */           Menu menu_bucket = new Menu(menuShowHide[0].getShell(), 4);
/*      */           
/* 2161 */           org.eclipse.swt.widgets.MenuItem bucket_item = new org.eclipse.swt.widgets.MenuItem(menuShowHide[0], 64);
/*      */           
/* 2163 */           bucket_item.setText((String)entry[0] + mod);
/*      */           
/* 2165 */           bucket_item.setMenu(menu_bucket);
/*      */           
/* 2167 */           parent_menu = menu_bucket;
/*      */         }
/*      */         
/* 2170 */         for (Tag t : bucket_tags)
/*      */         {
/* 2172 */           org.eclipse.swt.widgets.MenuItem showTag = new org.eclipse.swt.widgets.MenuItem(parent_menu, 32);
/*      */           
/* 2174 */           showTag.setSelection(t.isVisible());
/*      */           
/* 2176 */           Messages.setLanguageText(showTag, t.getTagName(false));
/*      */           
/* 2178 */           showTag.addListener(13, new Listener()
/*      */           {
/* 2180 */             public void handleEvent(Event event) { this.val$t.setVisible(!this.val$t.isVisible()); }
/*      */           });
/*      */         }
/*      */       }
/*      */     }
/*      */     Menu parent_menu;
/* 2186 */     showhideitem.setEnabled(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void createNonAutoMenuItems(Menu menu, Tag tag, TagType tag_type, Menu[] menuShowHide)
/*      */   {
/* 2197 */     if (tag_type.hasTagTypeFeature(32L))
/*      */     {
/* 2199 */       TagFeatureProperties props = (TagFeatureProperties)tag;
/*      */       
/* 2201 */       boolean has_ut = props.getProperty("untagged") != null;
/*      */       
/* 2203 */       if (has_ut)
/*      */       {
/* 2205 */         has_ut = false;
/*      */         
/* 2207 */         for (Tag t : tag_type.getTags())
/*      */         {
/* 2209 */           props = (TagFeatureProperties)t;
/*      */           
/* 2211 */           TagFeatureProperties.TagProperty prop = props.getProperty("untagged");
/*      */           
/* 2213 */           if (prop != null)
/*      */           {
/* 2215 */             Boolean b = prop.getBoolean();
/*      */             
/* 2217 */             if ((b != null) && (b.booleanValue()))
/*      */             {
/* 2219 */               has_ut = true;
/*      */               
/* 2221 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 2226 */         if (!has_ut)
/*      */         {
/* 2228 */           if (menuShowHide[0] == null)
/*      */           {
/* 2230 */             menuShowHide[0] = new Menu(menu.getShell(), 4);
/*      */             
/* 2232 */             org.eclipse.swt.widgets.MenuItem showhideitem = new org.eclipse.swt.widgets.MenuItem(menu, 64);
/* 2233 */             showhideitem.setText(MessageText.getString("label.showhide.tag"));
/* 2234 */             showhideitem.setMenu(menuShowHide[0]);
/*      */           }
/*      */           else
/*      */           {
/* 2238 */             new org.eclipse.swt.widgets.MenuItem(menuShowHide[0], 2);
/*      */           }
/*      */           
/* 2241 */           org.eclipse.swt.widgets.MenuItem showAll = new org.eclipse.swt.widgets.MenuItem(menuShowHide[0], 8);
/* 2242 */           Messages.setLanguageText(showAll, "label.untagged");
/* 2243 */           showAll.addListener(13, new Listener() {
/*      */             public void handleEvent(Event event) {
/*      */               try {
/* 2246 */                 String tag_name = MessageText.getString("label.untagged");
/*      */                 
/* 2248 */                 Tag ut_tag = this.val$tag_type.getTag(tag_name, true);
/*      */                 
/* 2250 */                 if (ut_tag == null)
/*      */                 {
/*      */ 
/* 2253 */                   ut_tag = this.val$tag_type.createTag(tag_name, true);
/*      */                 }
/*      */                 
/* 2256 */                 TagFeatureProperties tp = (TagFeatureProperties)ut_tag;
/*      */                 
/* 2258 */                 tp.getProperty("untagged").setBoolean(Boolean.valueOf(true));
/*      */               }
/*      */               catch (TagException e)
/*      */               {
/* 2262 */                 Debug.out(e);
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2303 */     org.eclipse.swt.widgets.MenuItem itemGroup = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*      */     
/* 2305 */     Messages.setLanguageText(itemGroup, "MyTorrentsView.menu.group");
/* 2306 */     itemGroup.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/* 2308 */         SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("TagGroupWindow.title", "TagGroupWindow.message");
/*      */         
/*      */ 
/* 2311 */         String group = this.val$tag.getGroup();
/*      */         
/* 2313 */         if (group == null) {
/* 2314 */           group = "";
/*      */         }
/* 2316 */         entryWindow.setPreenteredText(group, false);
/* 2317 */         entryWindow.selectPreenteredText(true);
/*      */         
/* 2319 */         entryWindow.prompt();
/*      */         
/* 2321 */         if (entryWindow.hasSubmittedInput()) {
/*      */           try
/*      */           {
/* 2324 */             group = entryWindow.getSubmittedInput().trim();
/*      */             
/* 2326 */             if (group.length() == 0) {
/* 2327 */               group = null;
/*      */             }
/* 2329 */             this.val$tag.setGroup(group);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2333 */             Debug.out(e);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2366 */     });
/* 2367 */     org.eclipse.swt.widgets.MenuItem itemDelete = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*      */     
/* 2369 */     Utils.setMenuItemImage(itemDelete, "delete");
/*      */     
/* 2371 */     Messages.setLanguageText(itemDelete, "FileItem.delete");
/* 2372 */     itemDelete.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/* 2374 */         this.val$tag.removeTag();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void createSideBarMenuItems(Menu menu, List<Tag> _tags)
/*      */   {
/* 2384 */     List<Tag> tags = new ArrayList(_tags);
/*      */     
/* 2386 */     Iterator<Tag> it = tags.iterator();
/*      */     
/* 2388 */     boolean can_show = false;
/* 2389 */     boolean can_hide = false;
/*      */     
/* 2391 */     while (it.hasNext())
/*      */     {
/* 2393 */       Tag tag = (Tag)it.next();
/*      */       
/* 2395 */       if (tag.getTagType().getTagType() != 3)
/*      */       {
/* 2397 */         it.remove();
/*      */ 
/*      */       }
/* 2400 */       else if (tag.isVisible())
/*      */       {
/* 2402 */         can_hide = true;
/*      */       }
/*      */       else
/*      */       {
/* 2406 */         can_show = true;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2411 */     if (tags.size() == 0)
/*      */     {
/* 2413 */       return;
/*      */     }
/*      */     
/* 2416 */     org.eclipse.swt.widgets.MenuItem itemShow = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*      */     
/* 2418 */     Messages.setLanguageText(itemShow, "Button.bar.show");
/* 2419 */     itemShow.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/* 2421 */         for (Tag tag : this.val$tags) {
/* 2422 */           tag.setVisible(true);
/*      */         }
/*      */         
/*      */       }
/* 2426 */     });
/* 2427 */     itemShow.setEnabled(can_show);
/*      */     
/* 2429 */     org.eclipse.swt.widgets.MenuItem itemHide = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*      */     
/* 2431 */     Messages.setLanguageText(itemHide, "Button.bar.hide");
/* 2432 */     itemHide.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/* 2434 */         for (Tag tag : this.val$tags) {
/* 2435 */           tag.setVisible(false);
/*      */         }
/*      */         
/*      */       }
/* 2439 */     });
/* 2440 */     itemHide.setEnabled(can_hide);
/*      */     
/* 2442 */     org.eclipse.swt.widgets.MenuItem itemGroup = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*      */     
/* 2444 */     Messages.setLanguageText(itemGroup, "MyTorrentsView.menu.group");
/* 2445 */     itemGroup.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/* 2447 */         SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("TagGroupWindow.title", "TagGroupWindow.message");
/*      */         
/*      */ 
/* 2450 */         String group = "";
/*      */         
/* 2452 */         entryWindow.setPreenteredText(group, false);
/* 2453 */         entryWindow.selectPreenteredText(true);
/*      */         
/* 2455 */         entryWindow.prompt();
/*      */         
/* 2457 */         if (entryWindow.hasSubmittedInput()) {
/*      */           try
/*      */           {
/* 2460 */             group = entryWindow.getSubmittedInput().trim();
/*      */             
/* 2462 */             if (group.length() == 0) {
/* 2463 */               group = null;
/*      */             }
/*      */             
/* 2466 */             for (Tag tag : this.val$tags)
/*      */             {
/* 2468 */               tag.setGroup(group);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 2472 */             Debug.out(e);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/* 2477 */     });
/* 2478 */     org.gudy.azureus2.plugins.ui.menus.MenuItem[] items = MenuItemManager.getInstance().getAllAsArray("tag_content");
/*      */     
/*      */ 
/* 2481 */     if (items.length > 0) {
/* 2482 */       MenuFactory.addSeparatorMenuItem(menu);
/*      */       
/*      */ 
/* 2485 */       MenuBuildUtils.addPluginMenuItems(items, menu, true, true, new MenuBuildUtils.MenuItemPluginMenuControllerImpl(tags.toArray(new Tag[0])));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/* 2490 */   private static final AsyncDispatcher move_dispatcher = new AsyncDispatcher("tag:applytocurrent");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void applyLocationToCurrent(Tag tag, final File location, final boolean complete_only)
/*      */   {
/* 2498 */     move_dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/* 2504 */         Set<DownloadManager> downloads = ((TagDownload)this.val$tag).getTaggedDownloads();
/*      */         
/* 2506 */         for (DownloadManager download : downloads)
/*      */         {
/* 2508 */           boolean dl_is_complete = download.isDownloadComplete(false);
/*      */           
/* 2510 */           if (((!complete_only) || (dl_is_complete)) && (
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2517 */             (!dl_is_complete) || (complete_only) || 
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 2522 */             (!download.getDownloadState().getFlag(8L))))
/*      */           {
/*      */ 
/*      */             try
/*      */             {
/*      */ 
/*      */ 
/* 2529 */               download.moveDataFilesLive(location);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 2533 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void addLibraryViewTagsSubMenu(final DownloadManager[] dms, Menu menu_tags, Composite composite)
/*      */   {
/* 2546 */     org.eclipse.swt.widgets.MenuItem[] items = menu_tags.getItems();
/*      */     
/* 2548 */     for (org.eclipse.swt.widgets.MenuItem item : items)
/*      */     {
/* 2550 */       item.dispose();
/*      */     }
/*      */     
/* 2553 */     TagManager tm = TagManagerFactory.getTagManager();
/*      */     
/* 2555 */     Map<TagType, List<Tag>> auto_map = new HashMap();
/*      */     
/* 2557 */     TagType manual_tt = tm.getTagType(3);
/*      */     
/* 2559 */     Map<Tag, Integer> manual_map = new HashMap();
/*      */     
/* 2561 */     for (DownloadManager dm : dms)
/*      */     {
/* 2563 */       List<Tag> tags = tm.getTagsForTaggable(dm);
/*      */       
/* 2565 */       for (Tag t : tags)
/*      */       {
/* 2567 */         TagType tt = t.getTagType();
/*      */         
/* 2569 */         if ((tt.isTagTypeAuto()) || (t.isTagAuto()[0] != 0) || (t.isTagAuto()[1] != 0))
/*      */         {
/* 2571 */           List<Tag> x = (List)auto_map.get(tt);
/*      */           
/* 2573 */           if (x == null)
/*      */           {
/* 2575 */             x = new ArrayList();
/*      */             
/* 2577 */             auto_map.put(tt, x);
/*      */           }
/*      */           
/* 2580 */           x.add(t);
/*      */         }
/* 2582 */         else if (tt == manual_tt)
/*      */         {
/* 2584 */           Integer i = (Integer)manual_map.get(t);
/*      */           
/* 2586 */           manual_map.put(t, Integer.valueOf(i == null ? 1 : i.intValue() + 1));
/*      */         }
/*      */       }
/*      */     }
/*      */     Menu menuAuto;
/* 2591 */     if (auto_map.size() > 0)
/*      */     {
/* 2593 */       menuAuto = new Menu(menu_tags.getShell(), 4);
/* 2594 */       org.eclipse.swt.widgets.MenuItem autoItem = new org.eclipse.swt.widgets.MenuItem(menu_tags, 64);
/* 2595 */       Messages.setLanguageText(autoItem, "wizard.maketorrent.auto");
/* 2596 */       autoItem.setMenu(menuAuto);
/*      */       
/* 2598 */       List<TagType> auto_tags = sortTagTypes(auto_map.keySet());
/*      */       
/* 2600 */       for (TagType tt : auto_tags)
/*      */       {
/* 2602 */         org.eclipse.swt.widgets.MenuItem tt_i = new org.eclipse.swt.widgets.MenuItem(menuAuto, org.gudy.azureus2.core3.util.Constants.isOSX ? 32 : 8);
/*      */         
/* 2604 */         String tt_str = tt.getTagTypeName(true) + ": ";
/*      */         
/* 2606 */         List<Tag> tags = (List)auto_map.get(tt);
/*      */         
/* 2608 */         Map<Tag, Integer> tag_counts = new HashMap();
/*      */         
/* 2610 */         for (Tag t : tags)
/*      */         {
/* 2612 */           Integer i = (Integer)tag_counts.get(t);
/*      */           
/* 2614 */           tag_counts.put(t, Integer.valueOf(i == null ? 1 : i.intValue() + 1));
/*      */         }
/*      */         
/* 2617 */         tags = sortTags(tag_counts.keySet());
/*      */         
/* 2619 */         int num = 0;
/*      */         
/* 2621 */         for (Tag t : tags)
/*      */         {
/* 2623 */           tt_str = tt_str + (num == 0 ? "" : ", ") + t.getTagName(true);
/*      */           
/* 2625 */           num++;
/*      */           
/* 2627 */           if (dms.length > 1)
/*      */           {
/* 2629 */             tt_str = tt_str + " (" + tag_counts.get(t) + ")";
/*      */           }
/*      */         }
/*      */         
/* 2633 */         tt_i.setText(tt_str);
/* 2634 */         if (org.gudy.azureus2.core3.util.Constants.isOSX) {
/* 2635 */           tt_i.setSelection(true);
/*      */         } else {
/* 2637 */           Utils.setMenuItemImage(tt_i, "graytick");
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2644 */     List<Tag> manual_t = manual_tt.getTags();
/*      */     Map<String, Tag> menu_name_map;
/* 2646 */     if (manual_t.size() > 0)
/*      */     {
/* 2648 */       if (auto_map.size() > 0)
/*      */       {
/* 2650 */         new org.eclipse.swt.widgets.MenuItem(menu_tags, 2);
/*      */       }
/*      */       
/* 2653 */       List<String> menu_names = new ArrayList();
/* 2654 */       menu_name_map = new IdentityHashMap();
/*      */       
/* 2656 */       for (Tag t : manual_t)
/*      */       {
/*      */ 
/*      */ 
/* 2660 */         if (t.isTagAuto()[0] == 0)
/*      */         {
/* 2662 */           String name = t.getTagName(true);
/*      */           
/* 2664 */           menu_names.add(name);
/* 2665 */           menu_name_map.put(name, t);
/*      */         }
/*      */       }
/*      */       
/* 2669 */       List<Object> menu_structure = MenuBuildUtils.splitLongMenuListIntoHierarchy(menu_names, 20);
/*      */       
/* 2671 */       for (Object obj : menu_structure)
/*      */       {
/* 2673 */         List<Tag> bucket_tags = new ArrayList();
/*      */         
/*      */ 
/*      */ 
/* 2677 */         if ((obj instanceof String))
/*      */         {
/* 2679 */           Menu parent_menu = menu_tags;
/*      */           
/* 2681 */           bucket_tags.add(menu_name_map.get((String)obj));
/*      */         }
/*      */         else
/*      */         {
/* 2685 */           Object[] entry = (Object[])obj;
/*      */           
/* 2687 */           List<String> tag_names = (List)entry[1];
/*      */           
/* 2689 */           boolean sub_all_selected = true;
/* 2690 */           boolean sub_some_selected = false;
/*      */           
/* 2692 */           for (String name : tag_names)
/*      */           {
/* 2694 */             Tag sub_tag = (Tag)menu_name_map.get(name);
/*      */             
/* 2696 */             Integer c = (Integer)manual_map.get(sub_tag);
/*      */             
/* 2698 */             if ((c != null) && (c.intValue() == dms.length))
/*      */             {
/* 2700 */               sub_some_selected = true;
/*      */             }
/*      */             else
/*      */             {
/* 2704 */               sub_all_selected = false;
/*      */             }
/*      */             
/* 2707 */             bucket_tags.add(sub_tag);
/*      */           }
/*      */           
/*      */           String mod;
/*      */           String mod;
/* 2712 */           if (sub_all_selected)
/*      */           {
/* 2714 */             mod = " (*)";
/*      */           } else { String mod;
/* 2716 */             if (sub_some_selected)
/*      */             {
/* 2718 */               mod = " (+)";
/*      */             }
/*      */             else
/*      */             {
/* 2722 */               mod = "";
/*      */             }
/*      */           }
/* 2725 */           Menu menu_bucket = new Menu(menu_tags.getShell(), 4);
/*      */           
/* 2727 */           org.eclipse.swt.widgets.MenuItem bucket_item = new org.eclipse.swt.widgets.MenuItem(menu_tags, 64);
/*      */           
/* 2729 */           bucket_item.setText((String)entry[0] + mod);
/*      */           
/* 2731 */           bucket_item.setMenu(menu_bucket);
/*      */           
/* 2733 */           parent_menu = menu_bucket;
/*      */         }
/*      */         
/* 2736 */         for (final Tag t : bucket_tags)
/*      */         {
/* 2738 */           org.eclipse.swt.widgets.MenuItem t_i = new org.eclipse.swt.widgets.MenuItem(parent_menu, 32);
/*      */           
/* 2740 */           String tag_name = t.getTagName(true);
/*      */           
/* 2742 */           Integer c = (Integer)manual_map.get(t);
/*      */           
/* 2744 */           if (c != null)
/*      */           {
/* 2746 */             if (c.intValue() == dms.length)
/*      */             {
/* 2748 */               t_i.setSelection(true);
/*      */               
/* 2750 */               t_i.setText(tag_name);
/*      */             }
/*      */             else
/*      */             {
/* 2754 */               t_i.setText(tag_name + " (" + c + ")");
/*      */             }
/*      */           }
/*      */           else {
/* 2758 */             t_i.setText(tag_name);
/*      */           }
/*      */           
/* 2761 */           t_i.addListener(13, new Listener()
/*      */           {
/*      */             public void handleEvent(Event event) {
/* 2764 */               boolean selected = this.val$t_i.getSelection();
/*      */               
/* 2766 */               for (DownloadManager dm : dms)
/*      */               {
/* 2768 */                 if (selected)
/*      */                 {
/* 2770 */                   t.addTaggable(dm);
/*      */                 }
/*      */                 else
/*      */                 {
/* 2774 */                   t.removeTaggable(dm);
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     }
/*      */     Menu parent_menu;
/* 2783 */     new org.eclipse.swt.widgets.MenuItem(menu_tags, 2);
/*      */     
/* 2785 */     org.eclipse.swt.widgets.MenuItem item_create = new org.eclipse.swt.widgets.MenuItem(menu_tags, 8);
/*      */     
/* 2787 */     Messages.setLanguageText(item_create, "label.add.tag");
/* 2788 */     item_create.addListener(13, new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {
/* 2791 */         TagUIUtils.createManualTag(new UIFunctions.TagReturner() {
/*      */           public void returnedTags(Tag[] tags) {
/* 2793 */             if (tags != null) {
/* 2794 */               for (Tag new_tag : tags) {
/* 2795 */                 for (DownloadManager dm : TagUIUtils.52.this.val$dms)
/*      */                 {
/* 2797 */                   new_tag.addTaggable(dm);
/*      */                 }
/*      */                 
/* 2800 */                 COConfigurationManager.setParameter("Library.TagInSideBar", true);
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static List<TagType> sortTagTypes(Collection<TagType> _tag_types)
/*      */   {
/* 2813 */     List<TagType> tag_types = new ArrayList(_tag_types);
/*      */     
/* 2815 */     Collections.sort(tag_types, new Comparator()
/*      */     {
/*      */ 
/*      */ 
/* 2819 */       final Comparator<String> comp = new FormattersImpl().getAlphanumericComparator(true);
/*      */       
/*      */ 
/*      */ 
/*      */       public int compare(TagType o1, TagType o2)
/*      */       {
/* 2825 */         return this.comp.compare(o1.getTagTypeName(true), o2.getTagTypeName(true));
/*      */       }
/*      */       
/* 2828 */     });
/* 2829 */     return tag_types;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static List<Tag> sortTags(Collection<Tag> _tags)
/*      */   {
/* 2836 */     List<Tag> tags = new ArrayList(_tags);
/*      */     
/* 2838 */     if (tags.size() < 2)
/*      */     {
/* 2840 */       return tags;
/*      */     }
/*      */     
/* 2843 */     Collections.sort(tags, getTagComparator());
/*      */     
/* 2845 */     return tags;
/*      */   }
/*      */   
/*      */ 
/*      */   public static Comparator<Tag> getTagComparator()
/*      */   {
/* 2851 */     new Comparator()
/*      */     {
/* 2853 */       final Comparator<String> comp = new FormattersImpl().getAlphanumericComparator(true);
/*      */       
/*      */ 
/*      */ 
/*      */       public int compare(Tag o1, Tag o2)
/*      */       {
/* 2859 */         String g1 = o1.getGroup();
/* 2860 */         String g2 = o2.getGroup();
/*      */         
/* 2862 */         if (g1 != g2) {
/* 2863 */           if (g1 == null)
/* 2864 */             return 1;
/* 2865 */           if (g2 == null) {
/* 2866 */             return -1;
/*      */           }
/*      */           
/* 2869 */           int res = this.comp.compare(g1, g2);
/*      */           
/* 2871 */           if (res != 0) {
/* 2872 */             return res;
/*      */           }
/*      */         }
/*      */         
/* 2876 */         return this.comp.compare(o1.getTagName(true), o2.getTagName(true));
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String getTagTooltip(Tag tag)
/*      */   {
/* 2885 */     return getTagTooltip(tag, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getTagTooltip(Tag tag, boolean skip_name)
/*      */   {
/* 2893 */     TagType tag_type = tag.getTagType();
/*      */     
/* 2895 */     String str = tag_type.getTagTypeName(true) + ": " + tag.getTagName(true);
/*      */     
/* 2897 */     String desc = tag.getDescription();
/*      */     
/* 2899 */     if (desc != null)
/*      */     {
/* 2901 */       if (str.length() > 0)
/*      */       {
/* 2903 */         str = str + "\r\n";
/*      */       }
/*      */       
/* 2906 */       str = str + desc;
/*      */     }
/*      */     
/* 2909 */     if (tag_type.hasTagTypeFeature(1L))
/*      */     {
/* 2911 */       TagFeatureRateLimit rl = (TagFeatureRateLimit)tag;
/*      */       
/* 2913 */       String up_str = "";
/* 2914 */       String down_str = "";
/*      */       
/* 2916 */       int limit_up = rl.getTagUploadLimit();
/*      */       
/* 2918 */       if (limit_up > 0)
/*      */       {
/* 2920 */         up_str = up_str + MessageText.getString("label.limit") + "=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(limit_up);
/*      */       }
/*      */       
/* 2923 */       int current_up = rl.getTagCurrentUploadRate();
/*      */       
/* 2925 */       if (current_up >= 0)
/*      */       {
/* 2927 */         up_str = up_str + (up_str.length() == 0 ? "" : ", ") + MessageText.getString("label.current") + "=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(current_up);
/*      */       }
/*      */       
/* 2930 */       int limit_down = rl.getTagDownloadLimit();
/*      */       
/* 2932 */       if (limit_down > 0)
/*      */       {
/* 2934 */         down_str = down_str + MessageText.getString("label.limit") + "=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(limit_down);
/*      */       }
/*      */       
/* 2937 */       int current_down = rl.getTagCurrentDownloadRate();
/*      */       
/* 2939 */       if (current_down >= 0)
/*      */       {
/* 2941 */         down_str = down_str + (down_str.length() == 0 ? "" : ", ") + MessageText.getString("label.current") + "=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(current_down);
/*      */       }
/*      */       
/*      */ 
/* 2945 */       if (up_str.length() > 0)
/*      */       {
/* 2947 */         str = str + "\r\n    " + MessageText.getString("iconBar.up") + ": " + up_str;
/*      */       }
/*      */       
/* 2950 */       if (down_str.length() > 0)
/*      */       {
/* 2952 */         str = str + "\r\n    " + MessageText.getString("iconBar.down") + ": " + down_str;
/*      */       }
/*      */       
/*      */ 
/* 2956 */       int up_pri = rl.getTagUploadPriority();
/*      */       
/* 2958 */       if (up_pri > 0)
/*      */       {
/* 2960 */         str = str + "\r\n    " + MessageText.getString("cat.upload.priority");
/*      */       }
/*      */     }
/*      */     
/* 2964 */     if (tag_type.hasTagTypeFeature(16L))
/*      */     {
/* 2966 */       TagFeatureFileLocation fl = (TagFeatureFileLocation)tag;
/*      */       
/* 2968 */       if (fl.supportsTagInitialSaveFolder())
/*      */       {
/* 2970 */         File init_loc = fl.getTagInitialSaveFolder();
/*      */         
/* 2972 */         if (init_loc != null)
/*      */         {
/* 2974 */           str = str + "\r\n    " + MessageText.getString("label.init.save.loc") + "=" + init_loc.getAbsolutePath();
/*      */         }
/*      */       }
/*      */       
/* 2978 */       if (fl.supportsTagMoveOnComplete())
/*      */       {
/* 2980 */         File move_on_comp = fl.getTagMoveOnCompleteFolder();
/*      */         
/* 2982 */         if (move_on_comp != null)
/*      */         {
/* 2984 */           str = str + "\r\n    " + MessageText.getString("label.move.on.comp") + "=" + move_on_comp.getAbsolutePath();
/*      */         }
/*      */       }
/* 2987 */       if (fl.supportsTagCopyOnComplete())
/*      */       {
/* 2989 */         File copy_on_comp = fl.getTagCopyOnCompleteFolder();
/*      */         
/* 2991 */         if (copy_on_comp != null)
/*      */         {
/* 2993 */           str = str + "\r\n    " + MessageText.getString("label.copy.on.comp") + "=" + copy_on_comp.getAbsolutePath();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2998 */     if (str.startsWith("\r\n"))
/*      */     {
/* 3000 */       str = str.substring(2);
/*      */     }
/*      */     
/* 3003 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static void showFilesView(TagDownload tag)
/*      */   {
/* 3010 */     Shell shell = org.gudy.azureus2.ui.swt.components.shell.ShellFactory.createShell(Utils.findAnyShell(), 1264);
/*      */     
/* 3012 */     org.eclipse.swt.layout.FillLayout fillLayout = new org.eclipse.swt.layout.FillLayout();
/* 3013 */     fillLayout.marginHeight = 2;
/* 3014 */     fillLayout.marginWidth = 2;
/* 3015 */     shell.setLayout(fillLayout);
/*      */     
/* 3017 */     final FilesView view = new FilesView(false);
/*      */     
/* 3019 */     view.setDisableWhenEmpty(false);
/*      */     
/* 3021 */     Set<DownloadManager> dms = tag.getTaggedDownloads();
/*      */     
/* 3023 */     view.dataSourceChanged(dms.toArray());
/*      */     
/* 3025 */     view.initialize(shell);
/*      */     
/* 3027 */     view.viewActivated();
/* 3028 */     view.refresh();
/*      */     
/* 3030 */     final UIUpdatable viewUpdater = new UIUpdatable() {
/*      */       public void updateUI() {
/* 3032 */         this.val$view.refresh();
/*      */       }
/*      */       
/*      */       public String getUpdateUIName() {
/* 3036 */         return this.val$view.getFullTitle();
/*      */       }
/*      */       
/* 3039 */     };
/* 3040 */     com.aelitis.azureus.ui.swt.uiupdater.UIUpdaterSWT.getInstance().addUpdater(viewUpdater);
/*      */     
/* 3042 */     final TagListener tag_listener = new TagListener()
/*      */     {
/*      */       public void taggableSync(Tag tag) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void taggableRemoved(Tag t, Taggable tagged)
/*      */       {
/* 3052 */         Set<DownloadManager> dms = this.val$tag.getTaggedDownloads();
/*      */         
/* 3054 */         view.dataSourceChanged(dms.toArray());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void taggableAdded(Tag t, Taggable tagged)
/*      */       {
/* 3061 */         Set<DownloadManager> dms = this.val$tag.getTaggedDownloads();
/*      */         
/* 3063 */         view.dataSourceChanged(dms.toArray());
/*      */       }
/*      */       
/* 3066 */     };
/* 3067 */     tag.addTagListener(tag_listener, false);
/*      */     
/* 3069 */     shell.addDisposeListener(new org.eclipse.swt.events.DisposeListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetDisposed(DisposeEvent e)
/*      */       {
/*      */ 
/* 3076 */         this.val$tag.removeTagListener(tag_listener);
/*      */         
/* 3078 */         com.aelitis.azureus.ui.swt.uiupdater.UIUpdaterSWT.getInstance().removeUpdater(viewUpdater);
/* 3079 */         view.delete();
/*      */       }
/*      */       
/* 3082 */     });
/* 3083 */     shell.layout(true, true);
/*      */     
/*      */ 
/* 3086 */     shell.setText(tag.getTagName(true));
/*      */     
/* 3088 */     shell.open();
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/utils/TagUIUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */