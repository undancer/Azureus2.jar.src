/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagFeatureRateLimit;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import com.aelitis.azureus.core.tag.TagTypeListener.TagEvent;
/*     */ import com.aelitis.azureus.core.util.RegExUtil;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableSelectionListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableViewFilterCheck;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableViewImpl;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagAggregateSR;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagAggregateSRMax;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagColor;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagCopyOnComp;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagCount;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagDownLimit;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagDownRate;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagDownSession;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagDownTotal;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagGroup;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagInitialSaveLocation;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagLimits;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagMaxSR;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagMinSR;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagMoveOnComp;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagName;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagProperties;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagPublic;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagRSSFeed;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagType;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagUpLimit;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagUpRate;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagUpSession;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagUpTotal;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagUploadPriority;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagVisible;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagXCode;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MdiEntrySWT;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility.ButtonListenerAdapter;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectButton;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectTextbox;
/*     */ import com.aelitis.azureus.ui.swt.utils.TagUIUtilsV3;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnCreationListener;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.MenuFactory;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCore;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*     */ import org.gudy.azureus2.ui.swt.views.MyTorrentsSubView;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewSWT_TabsCommon;
/*     */ import org.gudy.azureus2.ui.swt.views.utils.TagUIUtils;
/*     */ 
/*     */ public class SBC_TagsOverview extends SkinView implements UIUpdatable, UIPluginViewToolBarListener, TableViewFilterCheck<Tag>, com.aelitis.azureus.core.tag.TagManagerListener, com.aelitis.azureus.core.tag.TagTypeListener, org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener, TableSelectionListener
/*     */ {
/*     */   private static final String TABLE_TAGS = "TagsView";
/*     */   TableViewSWT<Tag> tv;
/*     */   private org.eclipse.swt.widgets.Text txtFilter;
/*     */   private Composite table_parent;
/*  93 */   private boolean columnsAdded = false;
/*     */   
/*     */ 
/*     */   private boolean tm_listener_added;
/*     */   
/*     */ 
/*     */   private boolean registeredCoreSubViews;
/*     */   
/*     */   private Object datasource;
/*     */   
/*     */ 
/*     */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*     */   {
/* 106 */     boolean isTableSelected = false;
/* 107 */     if ((this.tv instanceof TableViewImpl)) {
/* 108 */       isTableSelected = ((TableViewImpl)this.tv).isTableSelected();
/*     */     }
/* 110 */     if (!isTableSelected) {
/* 111 */       UISWTViewCore active_view = getActiveView();
/* 112 */       if (active_view != null) {
/* 113 */         UIPluginViewToolBarListener l = active_view.getToolBarListener();
/* 114 */         if ((l != null) && (l.toolBarItemActivated(item, activationType, datasource))) {
/* 115 */           return true;
/*     */         }
/*     */       }
/* 118 */       return false;
/*     */     }
/*     */     
/* 121 */     if ((this.tv == null) || (!this.tv.isVisible())) {
/* 122 */       return false;
/*     */     }
/* 124 */     if (item.getID().equals("remove"))
/*     */     {
/*     */ 
/* 127 */       Object[] datasources = this.tv.getSelectedDataSources().toArray();
/*     */       
/* 129 */       if (datasources.length > 0)
/*     */       {
/* 131 */         for (Object object : datasources) {
/* 132 */           if ((object instanceof Tag)) {
/* 133 */             final Tag tag = (Tag)object;
/* 134 */             if (tag.getTagType().getTagType() == 3)
/*     */             {
/*     */ 
/*     */ 
/* 138 */               MessageBoxShell mb = new MessageBoxShell(MessageText.getString("message.confirm.delete.title"), MessageText.getString("message.confirm.delete.text", new String[] { tag.getTagName(true) }), new String[] { MessageText.getString("Button.yes"), MessageText.getString("Button.no") }, 1);
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
/* 151 */               mb.open(new com.aelitis.azureus.ui.UserPrompterResultListener() {
/*     */                 public void prompterClosed(int result) {
/* 153 */                   if (result == 0) {
/* 154 */                     tag.removeTag();
/*     */                   }
/*     */                 }
/*     */               });
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 162 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 166 */     return false;
/*     */   }
/*     */   
/*     */   private MdiEntrySWT getActiveView() {
/* 170 */     TableViewSWT_TabsCommon tabsCommon = this.tv.getTabsCommon();
/* 171 */     if (tabsCommon != null) {
/* 172 */       return tabsCommon.getActiveSubView();
/*     */     }
/* 174 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void filterSet(String filter) {}
/*     */   
/*     */ 
/*     */   public void refreshToolBarItems(Map<String, Long> list)
/*     */   {
/* 184 */     if ((this.tv == null) || (!this.tv.isVisible())) {
/* 185 */       return;
/*     */     }
/*     */     
/* 188 */     boolean canEnable = false;
/* 189 */     Object[] datasources = this.tv.getSelectedDataSources().toArray();
/*     */     
/* 191 */     if (datasources.length > 0)
/*     */     {
/* 193 */       for (Object object : datasources) {
/* 194 */         if ((object instanceof Tag)) {
/* 195 */           Tag tag = (Tag)object;
/* 196 */           if (tag.getTagType().getTagType() == 3) {
/* 197 */             canEnable = true;
/* 198 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 204 */     list.put("remove", Long.valueOf(canEnable ? 1L : 0L));
/*     */   }
/*     */   
/*     */   public void updateUI()
/*     */   {
/* 209 */     if (this.tv != null) {
/* 210 */       this.tv.refreshTable(false);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getUpdateUIName()
/*     */   {
/* 216 */     return "TagsView";
/*     */   }
/*     */   
/*     */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*     */   {
/* 221 */     initColumns();
/*     */     
/* 223 */     SWTSkinObjectButton soAddTagButton = (SWTSkinObjectButton)getSkinObject("add-tag");
/* 224 */     if (soAddTagButton != null) {
/* 225 */       soAddTagButton.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*     */       {
/*     */         public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask)
/*     */         {
/* 229 */           TagUIUtilsV3.showCreateTagDialog(null);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 234 */     new InfoBarUtil(skinObject, "tagsview.infobar", false, "tags.infobar", "tags.view.infobar")
/*     */     {
/*     */       public boolean allowShow() {
/* 237 */         return true;
/*     */       }
/*     */       
/* 240 */     };
/* 241 */     return null;
/*     */   }
/*     */   
/*     */   protected void initColumns() {
/* 245 */     synchronized (SBC_TagsOverview.class)
/*     */     {
/* 247 */       if (this.columnsAdded)
/*     */       {
/* 249 */         return;
/*     */       }
/*     */       
/* 252 */       this.columnsAdded = true;
/*     */     }
/*     */     
/* 255 */     TableColumnManager tableManager = TableColumnManager.getInstance();
/*     */     
/* 257 */     tableManager.registerColumn(Tag.class, ColumnTagCount.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 260 */         new ColumnTagCount(column);
/*     */       }
/* 262 */     });
/* 263 */     tableManager.registerColumn(Tag.class, ColumnTagColor.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 266 */         new ColumnTagColor(column);
/*     */       }
/* 268 */     });
/* 269 */     tableManager.registerColumn(Tag.class, ColumnTagName.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 272 */         new ColumnTagName(column);
/*     */       }
/* 274 */     });
/* 275 */     tableManager.registerColumn(Tag.class, ColumnTagType.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 278 */         new ColumnTagType(column);
/*     */       }
/*     */       
/* 281 */     });
/* 282 */     tableManager.registerColumn(Tag.class, ColumnTagPublic.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 285 */         new ColumnTagPublic(column);
/*     */       }
/*     */       
/* 288 */     });
/* 289 */     tableManager.registerColumn(Tag.class, ColumnTagUpRate.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 292 */         new ColumnTagUpRate(column);
/*     */       }
/*     */       
/* 295 */     });
/* 296 */     tableManager.registerColumn(Tag.class, ColumnTagDownRate.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 299 */         new ColumnTagDownRate(column);
/*     */       }
/*     */       
/* 302 */     });
/* 303 */     tableManager.registerColumn(Tag.class, ColumnTagUpLimit.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 306 */         new ColumnTagUpLimit(column);
/*     */       }
/*     */       
/* 309 */     });
/* 310 */     tableManager.registerColumn(Tag.class, ColumnTagDownLimit.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 313 */         new ColumnTagDownLimit(column);
/*     */       }
/*     */       
/* 316 */     });
/* 317 */     tableManager.registerColumn(Tag.class, ColumnTagUpSession.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 320 */         new ColumnTagUpSession(column);
/*     */       }
/*     */       
/* 323 */     });
/* 324 */     tableManager.registerColumn(Tag.class, ColumnTagDownSession.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 327 */         new ColumnTagDownSession(column);
/*     */       }
/*     */       
/* 330 */     });
/* 331 */     tableManager.registerColumn(Tag.class, ColumnTagUpTotal.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 334 */         new ColumnTagUpTotal(column);
/*     */       }
/*     */       
/* 337 */     });
/* 338 */     tableManager.registerColumn(Tag.class, ColumnTagDownTotal.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 341 */         new ColumnTagDownTotal(column);
/*     */       }
/*     */       
/* 344 */     });
/* 345 */     tableManager.registerColumn(Tag.class, ColumnTagRSSFeed.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 348 */         new ColumnTagRSSFeed(column);
/*     */       }
/*     */       
/* 351 */     });
/* 352 */     tableManager.registerColumn(Tag.class, ColumnTagUploadPriority.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 355 */         new ColumnTagUploadPriority(column);
/*     */       }
/*     */       
/* 358 */     });
/* 359 */     tableManager.registerColumn(Tag.class, ColumnTagMinSR.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 362 */         new ColumnTagMinSR(column);
/*     */       }
/*     */       
/* 365 */     });
/* 366 */     tableManager.registerColumn(Tag.class, ColumnTagMaxSR.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 369 */         new ColumnTagMaxSR(column);
/*     */       }
/*     */       
/* 372 */     });
/* 373 */     tableManager.registerColumn(Tag.class, ColumnTagAggregateSR.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 376 */         new ColumnTagAggregateSR(column);
/*     */       }
/*     */       
/* 379 */     });
/* 380 */     tableManager.registerColumn(Tag.class, ColumnTagAggregateSRMax.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 383 */         new ColumnTagAggregateSRMax(column);
/*     */       }
/*     */       
/* 386 */     });
/* 387 */     tableManager.registerColumn(Tag.class, ColumnTagXCode.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 390 */         new ColumnTagXCode(column);
/*     */       }
/*     */       
/* 393 */     });
/* 394 */     tableManager.registerColumn(Tag.class, ColumnTagInitialSaveLocation.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 397 */         new ColumnTagInitialSaveLocation(column);
/*     */       }
/*     */       
/* 400 */     });
/* 401 */     tableManager.registerColumn(Tag.class, ColumnTagMoveOnComp.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 404 */         new ColumnTagMoveOnComp(column);
/*     */       }
/*     */       
/* 407 */     });
/* 408 */     tableManager.registerColumn(Tag.class, ColumnTagCopyOnComp.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 411 */         new ColumnTagCopyOnComp(column);
/*     */       }
/*     */       
/* 414 */     });
/* 415 */     tableManager.registerColumn(Tag.class, ColumnTagProperties.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 418 */         new ColumnTagProperties(column);
/*     */       }
/*     */       
/* 421 */     });
/* 422 */     tableManager.registerColumn(Tag.class, ColumnTagVisible.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 425 */         new ColumnTagVisible(column);
/*     */       }
/*     */       
/* 428 */     });
/* 429 */     tableManager.registerColumn(Tag.class, ColumnTagGroup.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 432 */         new ColumnTagGroup(column);
/*     */       }
/*     */       
/* 435 */     });
/* 436 */     tableManager.registerColumn(Tag.class, ColumnTagLimits.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 439 */         new ColumnTagLimits(column);
/*     */       }
/*     */       
/* 442 */     });
/* 443 */     tableManager.setDefaultColumnNames("TagsView", new String[] { ColumnTagColor.COLUMN_ID, ColumnTagName.COLUMN_ID, ColumnTagCount.COLUMN_ID, ColumnTagType.COLUMN_ID, ColumnTagPublic.COLUMN_ID, ColumnTagUpRate.COLUMN_ID, ColumnTagDownRate.COLUMN_ID, ColumnTagUpLimit.COLUMN_ID, ColumnTagDownLimit.COLUMN_ID });
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
/* 456 */     tableManager.setDefaultSortColumnName("TagsView", ColumnTagName.COLUMN_ID);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object skinObjectHidden(SWTSkinObject skinObject, Object params)
/*     */   {
/* 462 */     if (this.tv != null)
/*     */     {
/* 464 */       this.tv.delete();
/*     */       
/* 466 */       this.tv = null;
/*     */     }
/*     */     
/* 469 */     Utils.disposeSWTObjects(new Object[] { this.table_parent });
/*     */     
/*     */ 
/*     */ 
/* 473 */     TagManager tagManager = TagManagerFactory.getTagManager();
/* 474 */     if (tagManager != null) {
/* 475 */       List<TagType> tagTypes = tagManager.getTagTypes();
/* 476 */       for (TagType tagType : tagTypes) {
/* 477 */         tagType.removeTagTypeListener(this);
/*     */       }
/* 479 */       tagManager.removeTagManagerListener(this);
/*     */       
/* 481 */       this.tm_listener_added = false;
/*     */     }
/*     */     
/*     */ 
/* 485 */     return super.skinObjectHidden(skinObject, params);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object skinObjectShown(SWTSkinObject skinObject, Object params)
/*     */   {
/* 491 */     super.skinObjectShown(skinObject, params);
/*     */     
/* 493 */     SWTSkinObjectTextbox soFilter = (SWTSkinObjectTextbox)getSkinObject("filterbox");
/*     */     
/* 495 */     if (soFilter != null)
/*     */     {
/* 497 */       this.txtFilter = soFilter.getTextControl();
/*     */     }
/*     */     
/* 500 */     SWTSkinObject so_list = getSkinObject("tags-list");
/*     */     
/* 502 */     if (so_list != null)
/*     */     {
/* 504 */       initTable((Composite)so_list.getControl());
/*     */     }
/*     */     else
/*     */     {
/* 508 */       return null;
/*     */     }
/*     */     
/* 511 */     if (this.tv == null)
/*     */     {
/* 513 */       return null;
/*     */     }
/*     */     
/* 516 */     TagManager tagManager = TagManagerFactory.getTagManager();
/*     */     
/* 518 */     if (tagManager != null)
/*     */     {
/* 520 */       if (!this.tm_listener_added)
/*     */       {
/* 522 */         this.tm_listener_added = true;
/*     */         
/* 524 */         tagManager.addTagManagerListener(this, true);
/*     */       }
/*     */     }
/*     */     
/* 528 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object skinObjectDestroyed(SWTSkinObject skinObject, Object params)
/*     */   {
/* 538 */     if (this.tm_listener_added)
/*     */     {
/* 540 */       this.tm_listener_added = false;
/*     */       
/* 542 */       TagManager tagManager = TagManagerFactory.getTagManager();
/*     */       
/* 544 */       tagManager.removeTagManagerListener(this);
/*     */       
/* 546 */       for (TagType tt : tagManager.getTagTypes())
/*     */       {
/* 548 */         tt.removeTagTypeListener(this);
/*     */       }
/*     */     }
/*     */     
/* 552 */     return super.skinObjectDestroyed(skinObject, params);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initTable(Composite control)
/*     */   {
/* 562 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 563 */     if (uiFunctions != null) {
/* 564 */       UISWTInstance pluginUI = uiFunctions.getUISWTInstance();
/*     */       
/* 566 */       registerPluginViews(pluginUI);
/*     */     }
/*     */     
/* 569 */     if (this.tv == null)
/*     */     {
/* 571 */       this.tv = TableViewFactory.createTableViewSWT(Tag.class, "TagsView", "TagsView", new TableColumnCore[0], ColumnTagName.COLUMN_ID, 268500994);
/*     */       
/*     */ 
/*     */ 
/* 575 */       if (this.txtFilter != null) {
/* 576 */         this.tv.enableFilterCheck(this.txtFilter, this);
/*     */       }
/* 578 */       this.tv.setRowDefaultHeightEM(1.0F);
/* 579 */       this.tv.setEnableTabViews(true, true, null);
/*     */       
/* 581 */       this.table_parent = new Composite(control, 2048);
/* 582 */       this.table_parent.setLayoutData(Utils.getFilledFormData());
/* 583 */       GridLayout layout = new GridLayout();
/* 584 */       layout.marginHeight = (layout.marginWidth = layout.verticalSpacing = layout.horizontalSpacing = 0);
/* 585 */       this.table_parent.setLayout(layout);
/*     */       
/* 587 */       this.table_parent.addListener(26, new Listener()
/*     */       {
/*     */         public void handleEvent(Event event) {
/* 590 */           SBC_TagsOverview.this.updateSelectedContent();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 603 */       });
/* 604 */       this.tv.addMenuFillListener(this);
/* 605 */       this.tv.addSelectionListener(this, false);
/*     */       
/* 607 */       this.tv.initialize(this.table_parent);
/*     */       
/* 609 */       this.tv.addCountChangeListener(new com.aelitis.azureus.ui.common.table.TableCountChangeListener()
/*     */       {
/*     */         public void rowRemoved(TableRowCore row) {}
/*     */         
/*     */         public void rowAdded(TableRowCore row)
/*     */         {
/* 615 */           if (SBC_TagsOverview.this.datasource == row.getDataSource()) {
/* 616 */             SBC_TagsOverview.this.tv.setSelectedRows(new TableRowCore[] { row });
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 622 */     control.layout(true);
/*     */   }
/*     */   
/*     */   private void registerPluginViews(UISWTInstance pluginUI) {
/* 626 */     if (this.registeredCoreSubViews) {
/* 627 */       return;
/*     */     }
/*     */     
/* 630 */     pluginUI.addView("TagsView", "TagSettingsView", org.gudy.azureus2.ui.swt.views.TagSettingsView.class, null);
/*     */     
/* 632 */     pluginUI.addView("TagsView", "MyTorrentsSubView", MyTorrentsSubView.class, null);
/*     */     
/*     */ 
/* 635 */     this.registeredCoreSubViews = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fillMenu(String sColumnName, Menu menu)
/*     */   {
/* 643 */     List<Object> ds = this.tv.getSelectedDataSources();
/*     */     
/* 645 */     List<Tag> tags = new ArrayList();
/*     */     
/* 647 */     final List<TagFeatureRateLimit> tags_su = new ArrayList();
/* 648 */     final List<TagFeatureRateLimit> tags_sd = new ArrayList();
/*     */     
/* 650 */     for (Object obj : ds)
/*     */     {
/* 652 */       if ((obj instanceof Tag))
/*     */       {
/* 654 */         Tag tag = (Tag)obj;
/*     */         
/* 656 */         tags.add(tag);
/*     */         
/* 658 */         if ((tag instanceof TagFeatureRateLimit))
/*     */         {
/* 660 */           TagFeatureRateLimit rl = (TagFeatureRateLimit)tag;
/*     */           
/* 662 */           if (rl.supportsTagRates())
/*     */           {
/* 664 */             long[] up = rl.getTagSessionUploadTotal();
/*     */             
/* 666 */             if (up != null)
/*     */             {
/* 668 */               tags_su.add(rl);
/*     */             }
/*     */             
/* 671 */             long[] down = rl.getTagSessionDownloadTotal();
/*     */             
/* 673 */             if (down != null)
/*     */             {
/* 675 */               tags_sd.add(rl);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 682 */     if (((sColumnName.equals(ColumnTagUpSession.COLUMN_ID)) && (tags_su.size() > 0)) || ((sColumnName.equals(ColumnTagDownSession.COLUMN_ID)) && (tags_sd.size() > 0)))
/*     */     {
/*     */ 
/* 685 */       final boolean is_up = sColumnName.equals(ColumnTagUpSession.COLUMN_ID);
/*     */       
/* 687 */       MenuItem mi = new MenuItem(menu, 8);
/*     */       
/* 689 */       Messages.setLanguageText(mi, "menu.reset.session.stats");
/*     */       
/* 691 */       mi.addListener(13, new Listener() {
/*     */         public void handleEvent(Event event) {
/* 693 */           for (TagFeatureRateLimit rl : is_up ? tags_su : tags_sd)
/*     */           {
/* 695 */             if (is_up) {
/* 696 */               rl.resetTagSessionUploadTotal();
/*     */             } else {
/* 698 */               rl.resetTagSessionDownloadTotal();
/*     */             }
/*     */             
/*     */           }
/*     */         }
/* 703 */       });
/* 704 */       MenuFactory.addSeparatorMenuItem(menu);
/*     */     }
/*     */     
/* 707 */     if (tags.size() == 1) {
/* 708 */       TagUIUtils.createSideBarMenuItems(menu, (Tag)tags.get(0));
/*     */     } else {
/* 710 */       TagUIUtils.createSideBarMenuItems(menu, tags);
/*     */     }
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
/*     */   public void selected(TableRowCore[] row)
/*     */   {
/* 725 */     updateSelectedContent();
/* 726 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 727 */     if (uiFunctions != null) {
/* 728 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void deselected(TableRowCore[] rows)
/*     */   {
/* 736 */     updateSelectedContent();
/* 737 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 738 */     if (uiFunctions != null) {
/* 739 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void focusChanged(TableRowCore focus)
/*     */   {
/* 747 */     updateSelectedContent();
/* 748 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 749 */     if (uiFunctions != null) {
/* 750 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void defaultSelected(TableRowCore[] rows, int stateMask)
/*     */   {
/* 759 */     if (rows.length == 1)
/*     */     {
/* 761 */       Object obj = rows[0].getDataSource();
/*     */       
/* 763 */       if ((obj instanceof Tag))
/*     */       {
/* 765 */         Tag tag = (Tag)obj;
/*     */         
/* 767 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*     */         
/* 769 */         if (uiFunctions != null)
/*     */         {
/* 771 */           if (!COConfigurationManager.getBooleanParameter("Library.TagInSideBar"))
/*     */           {
/* 773 */             COConfigurationManager.setParameter("Library.TagInSideBar", true);
/*     */           }
/*     */           
/* 776 */           if (!tag.isVisible())
/*     */           {
/* 778 */             tag.setVisible(true);
/*     */           }
/*     */           
/* 781 */           String id = "Tag." + tag.getTagType().getTagType() + "." + tag.getTagID();
/* 782 */           uiFunctions.getMDI().showEntryByID(id, tag);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void updateSelectedContent() {
/* 789 */     updateSelectedContent(false);
/*     */   }
/*     */   
/*     */   public void updateSelectedContent(boolean force) {
/* 793 */     if ((this.table_parent == null) || (this.table_parent.isDisposed())) {
/* 794 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 800 */     if ((!isVisible()) && 
/* 801 */       (!force)) {
/* 802 */       return;
/*     */     }
/*     */     
/* 805 */     SelectedContentManager.clearCurrentlySelectedContent();
/* 806 */     SelectedContentManager.changeCurrentlySelectedContent(this.tv.getTableID(), null, this.tv);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void mouseEnter(TableRowCore row) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void mouseExit(TableRowCore row) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean filterCheck(Tag ds, String filter, boolean regex)
/*     */   {
/* 823 */     String name = ds.getTagName(true);
/*     */     
/* 825 */     String s = "\\Q" + filter.replaceAll("\\s*[|;]\\s*", "\\\\E|\\\\Q") + "\\E";
/*     */     
/* 827 */     boolean match_result = true;
/*     */     
/* 829 */     if ((regex) && (s.startsWith("!")))
/*     */     {
/* 831 */       s = s.substring(1);
/*     */       
/* 833 */       match_result = false;
/*     */     }
/*     */     
/* 836 */     Pattern pattern = RegExUtil.getCachedPattern("tagsoverview:search", s, 2);
/*     */     
/* 838 */     return pattern.matcher(name).find() == match_result;
/*     */   }
/*     */   
/*     */   public void tagTypeAdded(TagManager manager, TagType tag_type)
/*     */   {
/* 843 */     tag_type.addTagTypeListener(this, true);
/*     */   }
/*     */   
/*     */   public void tagTypeRemoved(TagManager manager, TagType tag_type)
/*     */   {
/* 848 */     tag_type.removeTagTypeListener(this);
/*     */   }
/*     */   
/*     */   public void tagTypeChanged(TagType tag_type)
/*     */   {
/* 853 */     this.tv.tableInvalidate();
/*     */   }
/*     */   
/*     */   public void tagEventOccurred(TagTypeListener.TagEvent event)
/*     */   {
/* 858 */     int type = event.getEventType();
/* 859 */     Tag tag = event.getTag();
/* 860 */     if (type == 0) {
/* 861 */       tagAdded(tag);
/* 862 */     } else if (type == 1) {
/* 863 */       tagChanged(tag);
/* 864 */     } else if (type == 2) {
/* 865 */       tagRemoved(tag);
/*     */     }
/*     */   }
/*     */   
/*     */   public void tagAdded(Tag tag) {
/* 870 */     this.tv.addDataSource(tag);
/*     */     
/* 872 */     handleProps(tag);
/*     */   }
/*     */   
/*     */   public void tagChanged(Tag tag) {
/* 876 */     if ((this.tv == null) || (this.tv.isDisposed())) {
/* 877 */       return;
/*     */     }
/* 879 */     TableRowCore row = this.tv.getRow(tag);
/* 880 */     if (row != null) {
/* 881 */       row.invalidate(true);
/*     */     }
/*     */     
/* 884 */     handleProps(tag);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void handleProps(Tag tag)
/*     */   {
/* 891 */     Boolean b = (Boolean)tag.getTransientProperty("Settings Requested");
/*     */     
/* 893 */     if ((b != null) && (b.booleanValue()))
/*     */     {
/* 895 */       tag.setTransientProperty("Settings Requested", null);
/*     */       
/* 897 */       this.tv.processDataSourceQueueSync();
/*     */       
/* 899 */       TableRowCore row = this.tv.getRow(tag);
/*     */       
/* 901 */       if (row == null)
/*     */       {
/* 903 */         Debug.out("Can't select settings view for " + tag.getTagName(true) + " as row not found");
/*     */       }
/*     */       else
/*     */       {
/* 907 */         this.tv.setSelectedRows(new TableRowCore[] { row });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void tagRemoved(Tag tag)
/*     */   {
/* 914 */     this.tv.removeDataSource(tag);
/*     */   }
/*     */   
/*     */   public Object dataSourceChanged(SWTSkinObject skinObject, Object params)
/*     */   {
/* 919 */     if (((params instanceof Tag)) && 
/* 920 */       (this.tv != null)) {
/* 921 */       TableRowCore row = this.tv.getRow((Tag)params);
/* 922 */       if (row != null) {
/* 923 */         this.tv.setSelectedRows(new TableRowCore[] { row });
/*     */       }
/*     */     }
/*     */     
/* 927 */     this.datasource = params;
/* 928 */     return null;
/*     */   }
/*     */   
/*     */   public Object skinObjectSelected(SWTSkinObject skinObject, Object params)
/*     */   {
/* 933 */     updateSelectedContent();
/* 934 */     return super.skinObjectSelected(skinObject, params);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SBC_TagsOverview.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */