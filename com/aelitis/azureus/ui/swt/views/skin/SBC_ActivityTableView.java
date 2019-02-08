/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.activities.VuzeActivitiesEntry;
/*     */ import com.aelitis.azureus.activities.VuzeActivitiesListener;
/*     */ import com.aelitis.azureus.activities.VuzeActivitiesManager;
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableLifeCycleListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableSelectionAdapter;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfoManager;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryCreationListener;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*     */ import com.aelitis.azureus.ui.swt.columns.utils.TableColumnCreatorV3;
/*     */ import com.aelitis.azureus.ui.swt.feature.FeatureManagerUIListener;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectListener;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.events.KeyEvent;
/*     */ import org.eclipse.swt.events.KeyListener;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public class SBC_ActivityTableView
/*     */   extends SkinView
/*     */   implements UIUpdatable, UIPluginViewToolBarListener, VuzeActivitiesListener
/*     */ {
/*  76 */   private static int[] COLOR_UNVIEWED_ENTRIES = { 132, 16, 58 };
/*     */   
/*     */   private TableViewSWT<VuzeActivitiesEntry> view;
/*     */   
/*     */   private String tableID;
/*     */   
/*     */   private Composite viewComposite;
/*     */   
/*  84 */   private int viewMode = 0;
/*     */   
/*     */ 
/*     */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*     */   {
/*  89 */     skinObject.addListener(new SWTSkinObjectListener()
/*     */     {
/*     */       public Object eventOccured(SWTSkinObject skinObject, int eventType, Object params) {
/*  92 */         if (eventType == 0) {
/*  93 */           SelectedContentManager.changeCurrentlySelectedContent(SBC_ActivityTableView.this.tableID, SBC_ActivityTableView.this.getCurrentlySelectedContent(), SBC_ActivityTableView.this.view);
/*     */         }
/*  95 */         else if (eventType == 1) {
/*  96 */           SelectedContentManager.changeCurrentlySelectedContent(SBC_ActivityTableView.this.tableID, null, SBC_ActivityTableView.this.view);
/*     */         }
/*     */         
/*  99 */         return null;
/*     */       }
/*     */       
/* 102 */     });
/* 103 */     SWTSkinObject soParent = skinObject.getParent();
/*     */     
/* 105 */     Object data = soParent.getControl().getData("ViewMode");
/* 106 */     if ((data instanceof Long)) {
/* 107 */       this.viewMode = ((int)((Long)data).longValue());
/*     */     }
/*     */     
/* 110 */     boolean big = this.viewMode == -1;
/*     */     
/* 112 */     this.tableID = (big ? "Activity.big" : "Activity");
/*     */     
/* 114 */     TableColumnCore[] columns = big ? TableColumnCreatorV3.createActivityBig(this.tableID) : TableColumnCreatorV3.createActivitySmall(this.tableID);
/*     */     
/*     */ 
/*     */ 
/* 118 */     this.view = TableViewFactory.createTableViewSWT(VuzeActivitiesEntry.class, this.tableID, this.tableID, columns, "name", 268500994);
/*     */     
/*     */ 
/*     */ 
/* 122 */     this.view.setRowDefaultHeightEM(big ? 3.0F : 2.0F);
/*     */     
/* 124 */     this.view.addKeyListener(new KeyListener()
/*     */     {
/*     */       public void keyReleased(KeyEvent e) {}
/*     */       
/*     */       public void keyPressed(KeyEvent e) {
/* 129 */         if (e.keyCode == 127) {
/* 130 */           SBC_ActivityTableView.this.removeSelected();
/* 131 */         } else if (e.keyCode == 16777230) {
/* 132 */           if ((e.stateMask & 0x20000) != 0) {
/* 133 */             VuzeActivitiesManager.resetRemovedEntries();
/*     */           }
/* 135 */           if ((e.stateMask & 0x40000) != 0) {
/* 136 */             System.out.println("pull all vuze news entries");
/* 137 */             VuzeActivitiesManager.clearLastPullTimes();
/* 138 */             VuzeActivitiesManager.pullActivitiesNow(0L, "^F5", true);
/*     */           } else {
/* 140 */             System.out.println("pull latest vuze news entries");
/* 141 */             VuzeActivitiesManager.pullActivitiesNow(0L, "F5", true);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 146 */     });
/* 147 */     this.view.addSelectionListener(new TableSelectionAdapter()
/*     */     {
/*     */       public void selected(TableRowCore[] rows) {
/* 150 */         selectionChanged();
/* 151 */         for (int i = 0; i < rows.length; i++) {
/* 152 */           VuzeActivitiesEntry entry = (VuzeActivitiesEntry)rows[i].getDataSource(true);
/* 153 */           if ((entry != null) && (!entry.isRead()) && (entry.canFlipRead())) {
/* 154 */             entry.setRead(true);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */       public void defaultSelected(TableRowCore[] rows, int stateMask) {
/* 160 */         if (rows.length == 1)
/*     */         {
/* 162 */           VuzeActivitiesEntry ds = (VuzeActivitiesEntry)rows[0].getDataSource();
/*     */           
/* 164 */           if (ds.getTypeID().equals("LOCAL_NEWS_ITEM"))
/*     */           {
/* 166 */             String[] actions = ds.getActions();
/*     */             
/* 168 */             if (actions.length == 1)
/*     */             {
/* 170 */               ds.invokeCallback(actions[0]);
/*     */             }
/*     */           }
/*     */           else {
/* 174 */             TorrentListViewsUtils.playOrStreamDataSource(ds, false);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */       public void deselected(TableRowCore[] rows) {
/* 180 */         selectionChanged();
/*     */       }
/*     */       
/*     */       public void selectionChanged() {
/* 184 */         Utils.execSWTThread(new AERunnable() {
/*     */           public void runSupport() {
/* 186 */             ISelectedContent[] contents = SBC_ActivityTableView.this.getCurrentlySelectedContent();
/* 187 */             if (SBC_ActivityTableView.this.soMain.isVisible()) {
/* 188 */               SelectedContentManager.changeCurrentlySelectedContent(SBC_ActivityTableView.this.tableID, contents, SBC_ActivityTableView.this.view);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/* 184 */     }, false);
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
/* 197 */     this.view.addLifeCycleListener(new TableLifeCycleListener() {
/*     */       public void tableViewInitialized() {
/* 199 */         SBC_ActivityTableView.this.view.addDataSources(VuzeActivitiesManager.getAllEntries().toArray(new VuzeActivitiesEntry[0]));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void tableViewDestroyed() {}
/* 205 */     });
/* 206 */     SWTSkinObjectContainer soContents = new SWTSkinObjectContainer(this.skin, this.skin.getSkinProperties(), getUpdateUIName(), "", this.soMain);
/*     */     
/*     */ 
/* 209 */     this.skin.layout();
/*     */     
/* 211 */     this.viewComposite = soContents.getComposite();
/* 212 */     this.viewComposite.setBackground(this.viewComposite.getDisplay().getSystemColor(22));
/*     */     
/* 214 */     this.viewComposite.setForeground(this.viewComposite.getDisplay().getSystemColor(21));
/*     */     
/* 216 */     this.viewComposite.setLayoutData(Utils.getFilledFormData());
/* 217 */     GridLayout gridLayout = new GridLayout();
/* 218 */     gridLayout.horizontalSpacing = (gridLayout.verticalSpacing = gridLayout.marginHeight = gridLayout.marginWidth = 0);
/* 219 */     this.viewComposite.setLayout(gridLayout);
/*     */     
/* 221 */     this.view.initialize(this.viewComposite);
/*     */     
/* 223 */     VuzeActivitiesManager.addListener(this);
/*     */     
/* 225 */     return null;
/*     */   }
/*     */   
/*     */   public Object skinObjectDestroyed(SWTSkinObject skinObject, Object params)
/*     */   {
/* 230 */     if (this.view != null) {
/* 231 */       this.view.delete();
/*     */     }
/* 233 */     return super.skinObjectDestroyed(skinObject, params);
/*     */   }
/*     */   
/*     */   public String getUpdateUIName()
/*     */   {
/* 238 */     return this.tableID;
/*     */   }
/*     */   
/*     */   public void updateUI()
/*     */   {
/* 243 */     if (this.view != null) {
/* 244 */       this.view.refreshTable(false);
/*     */     }
/*     */   }
/*     */   
/*     */   public void refreshToolBarItems(Map<String, Long> list) {
/* 249 */     list.put("remove", Long.valueOf((isVisible()) && (this.view != null) && (this.view.getSelectedRowsSize() > 0) ? 1L : 0L));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*     */   {
/* 256 */     if (item.getID().equals("remove")) {
/* 257 */       removeSelected();
/* 258 */       return true;
/*     */     }
/*     */     
/* 261 */     return false;
/*     */   }
/*     */   
/*     */   public ISelectedContent[] getCurrentlySelectedContent() {
/* 265 */     if (this.view == null) {
/* 266 */       return null;
/*     */     }
/* 268 */     List listContent = new ArrayList();
/* 269 */     Object[] selectedDataSources = this.view.getSelectedDataSources(true);
/* 270 */     for (int i = 0; i < selectedDataSources.length; i++)
/*     */     {
/* 272 */       VuzeActivitiesEntry ds = (VuzeActivitiesEntry)selectedDataSources[i];
/* 273 */       if (ds != null) {
/*     */         try
/*     */         {
/* 276 */           ISelectedContent currentContent = ds.createSelectedContentObject();
/* 277 */           if (currentContent != null) {
/* 278 */             listContent.add(currentContent);
/*     */           }
/*     */         } catch (Exception e) {
/* 281 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/* 285 */     return (ISelectedContent[])listContent.toArray(new ISelectedContent[listContent.size()]);
/*     */   }
/*     */   
/*     */   public void vuzeNewsEntriesAdded(VuzeActivitiesEntry[] entries)
/*     */   {
/* 290 */     if (this.view != null) {
/* 291 */       this.view.addDataSources(entries);
/*     */     }
/*     */   }
/*     */   
/*     */   public void vuzeNewsEntriesRemoved(VuzeActivitiesEntry[] entries)
/*     */   {
/* 297 */     if (this.view != null) {
/* 298 */       this.view.removeDataSources(entries);
/* 299 */       this.view.processDataSourceQueue();
/*     */     }
/*     */   }
/*     */   
/*     */   public void vuzeNewsEntryChanged(VuzeActivitiesEntry entry)
/*     */   {
/* 305 */     if (this.view == null) {
/* 306 */       return;
/*     */     }
/* 308 */     TableRowCore row = this.view.getRow(entry);
/* 309 */     if (row != null) {
/* 310 */       row.invalidate();
/*     */     }
/*     */   }
/*     */   
/*     */   private void removeEntries(final VuzeActivitiesEntry[] toRemove, final int startIndex)
/*     */   {
/* 316 */     final VuzeActivitiesEntry entry = toRemove[startIndex];
/* 317 */     if ((entry == null) || ("Header".equals(entry.getTypeID())))
/*     */     {
/* 319 */       int nextIndex = startIndex + 1;
/* 320 */       if (nextIndex < toRemove.length) {
/* 321 */         removeEntries(toRemove, nextIndex);
/*     */       }
/* 323 */       return;
/*     */     }
/*     */     
/* 326 */     MessageBoxShell mb = new MessageBoxShell(MessageText.getString("v3.activity.remove.title"), MessageText.getString("v3.activity.remove.text", new String[] { entry.getText() }));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 331 */     mb.setRemember(this.tableID + "-Remove", false, MessageText.getString("MessageBoxWindow.nomoreprompting"));
/*     */     
/*     */ 
/* 334 */     if (startIndex == toRemove.length - 1) {
/* 335 */       mb.setButtons(0, new String[] { MessageText.getString("Button.yes"), MessageText.getString("Button.no") }, new Integer[] { Integer.valueOf(0), Integer.valueOf(1) });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 342 */       mb.setRememberOnlyIfButton(0);
/*     */     } else {
/* 344 */       mb.setButtons(1, new String[] { MessageText.getString("Button.removeAll"), MessageText.getString("Button.yes"), MessageText.getString("Button.no") }, new Integer[] { Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1) });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 353 */       mb.setRememberOnlyIfButton(1);
/*     */     }
/*     */     
/* 356 */     mb.setHandleHTML(false);
/* 357 */     mb.open(new UserPrompterResultListener() {
/*     */       public void prompterClosed(int result) {
/* 359 */         if (result == 2) {
/* 360 */           int numToRemove = toRemove.length - startIndex;
/* 361 */           VuzeActivitiesEntry[] toGroupRemove = new VuzeActivitiesEntry[numToRemove];
/* 362 */           System.arraycopy(toRemove, startIndex, toGroupRemove, 0, numToRemove);
/* 363 */           VuzeActivitiesManager.removeEntries(toGroupRemove);
/* 364 */           return; }
/* 365 */         if (result == 0) {
/* 366 */           VuzeActivitiesManager.removeEntries(new VuzeActivitiesEntry[] { entry });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 371 */         int nextIndex = startIndex + 1;
/* 372 */         if (nextIndex < toRemove.length) {
/* 373 */           SBC_ActivityTableView.this.removeEntries(toRemove, nextIndex);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   protected void removeSelected() {
/* 380 */     if (this.view == null) {
/* 381 */       return;
/*     */     }
/* 383 */     VuzeActivitiesEntry[] selectedEntries = (VuzeActivitiesEntry[])this.view.getSelectedDataSources().toArray(new VuzeActivitiesEntry[0]);
/*     */     
/*     */ 
/* 386 */     if (selectedEntries.length > 0)
/*     */     {
/* 388 */       removeEntries(selectedEntries, 0);
/*     */     }
/*     */   }
/*     */   
/*     */   public TableViewSWT getView() {
/* 393 */     return this.view;
/*     */   }
/*     */   
/*     */   public static void setupSidebarEntry(MultipleDocumentInterface mdi)
/*     */   {
/* 398 */     final ViewTitleInfo titleInfoActivityView = new ViewTitleInfo() {
/* 399 */       boolean had_unviewed = false;
/*     */       
/* 401 */       public Object getTitleInfoProperty(int propertyID) { if (propertyID == 0) {
/* 402 */           int num_unread = 0;
/* 403 */           int num_unviewed = 0;
/* 404 */           List<VuzeActivitiesEntry> allEntries = VuzeActivitiesManager.getAllEntries();
/*     */           
/* 406 */           for (VuzeActivitiesEntry entry : allEntries)
/*     */           {
/* 408 */             if (!entry.isRead())
/*     */             {
/* 410 */               num_unread++;
/*     */             }
/*     */             
/* 413 */             if (!entry.getViewed())
/*     */             {
/* 415 */               num_unviewed++;
/*     */             }
/*     */           }
/*     */           
/* 419 */           if (num_unread == 0)
/*     */           {
/* 421 */             num_unviewed = 0;
/*     */           }
/*     */           
/* 424 */           boolean has_unviewed = num_unviewed > 0;
/*     */           
/* 426 */           if (has_unviewed != this.had_unviewed)
/*     */           {
/* 428 */             if (has_unviewed)
/*     */             {
/* 430 */               MdiEntry parent = this.val$mdi.getEntry("header.vuze");
/*     */               
/* 432 */               if ((parent != null) && (!parent.isExpanded()))
/*     */               {
/* 434 */                 parent.setExpanded(true);
/*     */               }
/*     */             }
/*     */             
/* 438 */             this.had_unviewed = has_unviewed;
/*     */           }
/*     */           
/* 441 */           if (num_unviewed > 0)
/*     */           {
/* 443 */             return String.valueOf(num_unviewed) + (num_unread == 0 ? "" : new StringBuilder().append(":").append(num_unread).toString());
/*     */           }
/* 445 */           if (num_unread > 0)
/*     */           {
/* 447 */             return String.valueOf(num_unread);
/*     */           }
/*     */           
/* 450 */           return null;
/*     */         }
/* 452 */         if (propertyID == 2)
/*     */         {
/* 454 */           return "image.sidebar.activity";
/*     */         }
/* 456 */         if (propertyID == 8)
/*     */         {
/* 458 */           boolean has_unread = false;
/* 459 */           boolean has_unviewed = false;
/*     */           
/* 461 */           List<VuzeActivitiesEntry> allEntries = VuzeActivitiesManager.getAllEntries();
/*     */           
/* 463 */           for (VuzeActivitiesEntry entry : allEntries)
/*     */           {
/* 465 */             if (!entry.isRead())
/*     */             {
/* 467 */               has_unread = true;
/*     */             }
/*     */             
/* 470 */             if (!entry.getViewed())
/*     */             {
/* 472 */               has_unviewed = true;
/*     */             }
/*     */           }
/*     */           
/* 476 */           if ((has_unread) && (has_unviewed))
/*     */           {
/* 478 */             return SBC_ActivityTableView.COLOR_UNVIEWED_ENTRIES;
/*     */           }
/*     */         }
/*     */         
/* 482 */         return null;
/*     */       }
/* 484 */     };
/* 485 */     VuzeActivitiesManager.addListener(new VuzeActivitiesListener() {
/*     */       public void vuzeNewsEntryChanged(VuzeActivitiesEntry entry) {
/* 487 */         ViewTitleInfoManager.refreshTitleInfo(this.val$titleInfoActivityView);
/*     */       }
/*     */       
/*     */       public void vuzeNewsEntriesRemoved(VuzeActivitiesEntry[] entries) {
/* 491 */         ViewTitleInfoManager.refreshTitleInfo(this.val$titleInfoActivityView);
/*     */       }
/*     */       
/*     */       public void vuzeNewsEntriesAdded(VuzeActivitiesEntry[] entries) {
/* 495 */         ViewTitleInfoManager.refreshTitleInfo(this.val$titleInfoActivityView);
/*     */       }
/*     */       
/* 498 */     });
/* 499 */     MdiEntryCreationListener creationListener = new MdiEntryCreationListener() {
/*     */       public MdiEntry createMDiEntry(String id) {
/* 501 */         return this.val$mdi.createEntryFromSkinRef("header.vuze", "Activity", "activity", "{sidebar.Activity}", titleInfoActivityView, null, false, null);
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 506 */     };
/* 507 */     mdi.registerEntry("Activity", creationListener);
/* 508 */     mdi.registerEntry("activities", creationListener);
/*     */     
/* 510 */     PluginInterface pi = PluginInitializer.getDefaultInterface();
/* 511 */     UIManager uim = pi.getUIManager();
/* 512 */     MenuManager menuManager = uim.getMenuManager();
/*     */     
/* 514 */     MenuItem menuItem = menuManager.addMenuItem("sidebar.Activity", "v3.activity.button.readall");
/*     */     
/*     */ 
/* 517 */     menuItem.addListener(new MenuItemListener() {
/*     */       public void selected(MenuItem menu, Object target) {
/* 519 */         List<VuzeActivitiesEntry> allEntries = VuzeActivitiesManager.getAllEntries();
/* 520 */         for (VuzeActivitiesEntry entry : allEntries) {
/* 521 */           entry.setRead(true);
/*     */         }
/*     */       }
/*     */     });
/*     */     
/* 526 */     if (Constants.isCVSVersion()) {
/* 527 */       menuItem = menuManager.addMenuItem("sidebar.Activity", "!test update expiry!");
/*     */       
/*     */ 
/* 530 */       menuItem.addListener(new MenuItemListener()
/*     */       {
/*     */         public void selected(MenuItem menu, Object target) {}
/*     */       });
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SBC_ActivityTableView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */