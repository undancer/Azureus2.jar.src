/*     */ package com.aelitis.azureus.ui.swt.columns.utils;
/*     */ 
/*     */ import com.aelitis.azureus.activities.VuzeActivitiesEntry;
/*     */ import com.aelitis.azureus.plugins.net.buddy.swt.columns.ColumnChatMessageCount;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCoreCreationListener;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import com.aelitis.azureus.ui.swt.columns.torrent.ColumnProgressETA;
/*     */ import com.aelitis.azureus.ui.swt.columns.torrent.ColumnStream;
/*     */ import com.aelitis.azureus.ui.swt.columns.torrent.ColumnThumbAndName;
/*     */ import com.aelitis.azureus.ui.swt.columns.torrent.ColumnThumbnail;
/*     */ import com.aelitis.azureus.ui.swt.columns.torrent.ColumnUnopened;
/*     */ import com.aelitis.azureus.ui.swt.columns.vuzeactivity.ColumnActivityActions;
/*     */ import com.aelitis.azureus.ui.swt.columns.vuzeactivity.ColumnActivityDate;
/*     */ import com.aelitis.azureus.ui.swt.columns.vuzeactivity.ColumnActivityNew;
/*     */ import com.aelitis.azureus.ui.swt.columns.vuzeactivity.ColumnActivityText;
/*     */ import com.aelitis.azureus.ui.swt.columns.vuzeactivity.ColumnActivityType;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.LightHashMap;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadTypeComplete;
/*     */ import org.gudy.azureus2.plugins.download.DownloadTypeIncomplete;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.ui.swt.views.table.utils.TableColumnCreator;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.DateAddedItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.DateCompletedItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.NameItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.RankItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.ShareRatioItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.StatusItem;
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
/*     */ public class TableColumnCreatorV3
/*     */ {
/*     */   public static TableColumnCore[] createAllDM(String tableID, boolean big)
/*     */   {
/*  57 */     String[] oldVisibleOrder = { "unopened", "name", "TorrentStream", "size", "ProgressETA", "azsubs.ui.column.subs", "status", "torrentspeed", "seeds", "peers", "shareRatio" };
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
/*  70 */     String[] defaultVisibleOrder = { "#", "name", "TorrentStream", "ProgressETA", "size", "torrentspeed", "eta", "RatingColumn", "azsubs.ui.column.subs", "date_added" };
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
/*  84 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/*  85 */     Map<String, TableColumnCore> mapTCs = tcManager.getTableColumnsAsMap(Download.class, tableID);
/*     */     
/*     */ 
/*  88 */     tcManager.setDefaultColumnNames(tableID, defaultVisibleOrder);
/*     */     
/*  90 */     if ((!tcManager.loadTableColumnSettings(Download.class, tableID)) || (areNoneVisible(mapTCs)))
/*     */     {
/*  92 */       setVisibility(mapTCs, defaultVisibleOrder, true);
/*  93 */       RankItem tc = (RankItem)mapTCs.get("#");
/*  94 */       if (tc != null) {
/*  95 */         tcManager.setDefaultSortColumnName(tableID, "#");
/*  96 */         tc.setSortAscending(true);
/*     */       }
/*     */     } else {
/*  99 */       upgradeColumns(oldVisibleOrder, defaultVisibleOrder, mapTCs);
/*     */     }
/*     */     
/*     */ 
/* 103 */     StatusItem tcStatusItem = (StatusItem)mapTCs.get("status");
/* 104 */     if (tcStatusItem != null) {
/* 105 */       tcStatusItem.setChangeRowFG(false);
/* 106 */       if (big) {
/* 107 */         tcStatusItem.setChangeCellFG(false);
/* 108 */         tcStatusItem.setShowTrackerErrors(true);
/*     */       }
/*     */     }
/* 111 */     if (big) {
/* 112 */       ShareRatioItem tcShareRatioItem = (ShareRatioItem)mapTCs.get("shareRatio");
/* 113 */       if (tcShareRatioItem != null) {
/* 114 */         tcShareRatioItem.setChangeFG(false);
/* 115 */         tcShareRatioItem.setWidth(80);
/*     */       }
/*     */     }
/*     */     
/* 119 */     return (TableColumnCore[])mapTCs.values().toArray(new TableColumnCore[0]);
/*     */   }
/*     */   
/*     */   private static void upgradeColumns(String[] oldOrder, String[] newOrder, Map<String, TableColumnCore> mapTCs)
/*     */   {
/* 124 */     List<String> listCurrentOrder = new ArrayList();
/*     */     
/* 126 */     for (TableColumnCore tc : mapTCs.values()) {
/* 127 */       if (tc.isVisible()) {
/* 128 */         listCurrentOrder.add(tc.getName());
/*     */       }
/*     */     }
/*     */     
/* 132 */     if (oldOrder.length == listCurrentOrder.size()) {
/* 133 */       List<String> listOldOrder = Arrays.asList(oldOrder);
/* 134 */       if (listOldOrder.containsAll(listCurrentOrder))
/*     */       {
/* 136 */         System.out.println("upgradeColumns: SAME -> UPGRADING!");
/* 137 */         setVisibility(mapTCs, newOrder, true);
/*     */       }
/* 139 */     } else if (listCurrentOrder.size() > oldOrder.length) {
/* 140 */       List<String> listNewOrder = Arrays.asList(newOrder);
/* 141 */       if (listCurrentOrder.containsAll(listNewOrder)) {
/* 142 */         System.out.println("upgradeColumns: has all old plus -> UPGRADING!");
/*     */         
/*     */ 
/* 145 */         for (String id : newOrder) {
/* 146 */           TableColumnCore tc = (TableColumnCore)mapTCs.get(id);
/* 147 */           if (tc != null) {
/* 148 */             tc.setVisible(true);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static TableColumnCore[] createIncompleteDM(String tableID, boolean big) {
/* 156 */     String[] oldVisibleOrder = { "name", "TorrentStream", "size", "filecount", "ProgressETA", "seeds", "peers", "azsubs.ui.column.subs" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 166 */     String[] defaultVisibleOrder = { "#", "name", "TorrentStream", "ProgressETA", "size", "torrentspeed", "eta", "RatingColumn", "azsubs.ui.column.subs", "date_added" };
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
/* 179 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/* 180 */     Map<String, TableColumnCore> mapTCs = tcManager.getTableColumnsAsMap(DownloadTypeIncomplete.class, tableID);
/*     */     
/*     */ 
/* 183 */     tcManager.setDefaultColumnNames(tableID, defaultVisibleOrder);
/*     */     
/* 185 */     if ((!tcManager.loadTableColumnSettings(DownloadTypeIncomplete.class, tableID)) || (areNoneVisible(mapTCs)))
/*     */     {
/* 187 */       setVisibility(mapTCs, defaultVisibleOrder, true);
/* 188 */       RankItem tc = (RankItem)mapTCs.get("#");
/* 189 */       if (tc != null) {
/* 190 */         tcManager.setDefaultSortColumnName(tableID, "#");
/* 191 */         tc.setSortAscending(true);
/*     */       }
/*     */     } else {
/* 194 */       upgradeColumns(oldVisibleOrder, defaultVisibleOrder, mapTCs);
/*     */     }
/*     */     
/*     */ 
/* 198 */     StatusItem tcStatusItem = (StatusItem)mapTCs.get("status");
/* 199 */     if (tcStatusItem != null) {
/* 200 */       tcStatusItem.setChangeRowFG(false);
/* 201 */       if (big) {
/* 202 */         tcStatusItem.setChangeCellFG(false);
/*     */       }
/*     */     }
/*     */     
/* 206 */     if (big) {
/* 207 */       ShareRatioItem tcShareRatioItem = (ShareRatioItem)mapTCs.get("shareRatio");
/* 208 */       if (tcShareRatioItem != null) {
/* 209 */         tcShareRatioItem.setChangeFG(false);
/* 210 */         tcShareRatioItem.setWidth(80);
/*     */       }
/*     */     }
/*     */     
/* 214 */     return (TableColumnCore[])mapTCs.values().toArray(new TableColumnCore[0]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void setVisibility(Map mapTCs, String[] defaultVisibleOrder, boolean reorder)
/*     */   {
/* 223 */     for (Iterator iter = mapTCs.values().iterator(); iter.hasNext();) {
/* 224 */       TableColumnCore tc = (TableColumnCore)iter.next();
/* 225 */       Long force_visible = (Long)tc.getUserData("ud_fv");
/* 226 */       if ((force_visible == null) || (force_visible.longValue() == 0L))
/*     */       {
/* 228 */         tc.setVisible(false);
/*     */       }
/*     */     }
/*     */     
/* 232 */     for (int i = 0; i < defaultVisibleOrder.length; i++) {
/* 233 */       String id = defaultVisibleOrder[i];
/* 234 */       TableColumnCore tc = (TableColumnCore)mapTCs.get(id);
/* 235 */       if (tc != null) {
/* 236 */         tc.setVisible(true);
/* 237 */         if (reorder) {
/* 238 */           tc.setPositionNoShift(i);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static TableColumnCore[] createCompleteDM(String tableID, boolean big) {
/* 245 */     String[] oldVisibleOrder = { "unopened", "name", "RatingColumn", "azsubs.ui.column.subs", "size", "status", "shareRatio", "DateCompleted" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 255 */     String[] defaultVisibleOrder = { "#", "name", "TorrentStream", "status", "size", "torrentspeed", "RatingColumn", "azsubs.ui.column.subs", "DateCompleted" };
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
/* 267 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/* 268 */     Map mapTCs = tcManager.getTableColumnsAsMap(DownloadTypeComplete.class, tableID);
/*     */     
/*     */ 
/* 271 */     tcManager.setDefaultColumnNames(tableID, defaultVisibleOrder);
/*     */     
/* 273 */     if ((!tcManager.loadTableColumnSettings(DownloadTypeComplete.class, tableID)) || (areNoneVisible(mapTCs)))
/*     */     {
/* 275 */       setVisibility(mapTCs, defaultVisibleOrder, true);
/* 276 */       DateCompletedItem tc = (DateCompletedItem)mapTCs.get("DateCompleted");
/* 277 */       if (tc != null) {
/* 278 */         tcManager.setDefaultSortColumnName(tableID, "DateCompleted");
/* 279 */         tc.setSortAscending(false);
/*     */       }
/*     */     } else {
/* 282 */       upgradeColumns(oldVisibleOrder, defaultVisibleOrder, mapTCs);
/*     */     }
/*     */     
/*     */ 
/* 286 */     StatusItem tcStatusItem = (StatusItem)mapTCs.get("status");
/* 287 */     if (tcStatusItem != null) {
/* 288 */       tcStatusItem.setChangeRowFG(false);
/* 289 */       if (big) {
/* 290 */         tcStatusItem.setChangeCellFG(false);
/*     */       }
/*     */     }
/* 293 */     if (big) {
/* 294 */       ShareRatioItem tcShareRatioItem = (ShareRatioItem)mapTCs.get("shareRatio");
/* 295 */       if (tcShareRatioItem != null) {
/* 296 */         tcShareRatioItem.setChangeFG(false);
/* 297 */         tcShareRatioItem.setWidth(80);
/*     */       }
/*     */     }
/*     */     
/* 301 */     return (TableColumnCore[])mapTCs.values().toArray(new TableColumnCore[0]);
/*     */   }
/*     */   
/*     */   public static TableColumnCore[] createUnopenedDM(String tableID, boolean big) {
/* 305 */     String[] oldVisibleOrder = { "unopened", "name", "azsubs.ui.column.subs", "size", "ProgressETA", "status", "DateCompleted" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 314 */     String[] defaultVisibleOrder = { "unopened", "name", "TorrentStream", "size", "RatingColumn", "azsubs.ui.column.subs", "DateCompleted" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 324 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/* 325 */     Map mapTCs = tcManager.getTableColumnsAsMap(DownloadTypeComplete.class, tableID);
/*     */     
/*     */ 
/* 328 */     tcManager.setDefaultColumnNames(tableID, defaultVisibleOrder);
/*     */     
/* 330 */     if ((!tcManager.loadTableColumnSettings(DownloadTypeComplete.class, tableID)) || (areNoneVisible(mapTCs)))
/*     */     {
/* 332 */       setVisibility(mapTCs, defaultVisibleOrder, true);
/* 333 */       DateCompletedItem tc = (DateCompletedItem)mapTCs.get("DateCompleted");
/* 334 */       if (tc != null) {
/* 335 */         tcManager.setDefaultSortColumnName(tableID, "DateCompleted");
/* 336 */         tc.setSortAscending(false);
/*     */       }
/*     */     } else {
/* 339 */       upgradeColumns(oldVisibleOrder, defaultVisibleOrder, mapTCs);
/*     */     }
/*     */     
/*     */ 
/* 343 */     StatusItem tcStatusItem = (StatusItem)mapTCs.get("status");
/* 344 */     if (tcStatusItem != null) {
/* 345 */       tcStatusItem.setChangeRowFG(false);
/* 346 */       if (big) {
/* 347 */         tcStatusItem.setChangeCellFG(false);
/*     */       }
/*     */     }
/* 350 */     if (big) {
/* 351 */       ShareRatioItem tcShareRatioItem = (ShareRatioItem)mapTCs.get("shareRatio");
/* 352 */       if (tcShareRatioItem != null) {
/* 353 */         tcShareRatioItem.setChangeFG(false);
/* 354 */         tcShareRatioItem.setWidth(80);
/*     */       }
/*     */     }
/*     */     
/* 358 */     return (TableColumnCore[])mapTCs.values().toArray(new TableColumnCore[0]);
/*     */   }
/*     */   
/*     */   public static TableColumnCore[] createActivitySmall(String tableID) {
/* 362 */     String[] defaultVisibleOrder = { "activityNew", "activityType", "activityText", "activityActions", "activityDate" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 369 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/* 370 */     Map mapTCs = tcManager.getTableColumnsAsMap(VuzeActivitiesEntry.class, tableID);
/*     */     
/*     */ 
/* 373 */     tcManager.setDefaultColumnNames(tableID, defaultVisibleOrder);
/*     */     
/* 375 */     if ((!tcManager.loadTableColumnSettings(VuzeActivitiesEntry.class, tableID)) || (areNoneVisible(mapTCs)))
/*     */     {
/* 377 */       setVisibility(mapTCs, defaultVisibleOrder, true);
/* 378 */       ColumnActivityDate tc = (ColumnActivityDate)mapTCs.get("activityDate");
/* 379 */       if (tc != null) {
/* 380 */         tcManager.setDefaultSortColumnName(tableID, "activityDate");
/*     */         
/* 382 */         tc.setSortAscending(false);
/*     */       }
/* 384 */       ColumnActivityText tcText = (ColumnActivityText)mapTCs.get("activityText");
/* 385 */       if (tcText != null) {
/* 386 */         tcText.setWidth(445);
/*     */       }
/*     */     }
/*     */     
/* 390 */     return (TableColumnCore[])mapTCs.values().toArray(new TableColumnCore[0]);
/*     */   }
/*     */   
/*     */   public static TableColumnCore[] createActivityBig(String tableID) {
/* 394 */     String[] defaultVisibleOrder = { "activityNew", "activityType", "activityText", "Thumbnail", "activityActions", "activityDate" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 402 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/* 403 */     Map mapTCs = tcManager.getTableColumnsAsMap(VuzeActivitiesEntry.class, tableID);
/*     */     
/*     */ 
/* 406 */     tcManager.setDefaultColumnNames(tableID, defaultVisibleOrder);
/*     */     
/* 408 */     if ((!tcManager.loadTableColumnSettings(VuzeActivitiesEntry.class, tableID)) || (areNoneVisible(mapTCs)))
/*     */     {
/* 410 */       setVisibility(mapTCs, defaultVisibleOrder, true);
/*     */       
/* 412 */       ColumnActivityText tcText = (ColumnActivityText)mapTCs.get("activityText");
/* 413 */       if (tcText != null) {
/* 414 */         tcText.setWidth(350);
/*     */       }
/* 416 */       ColumnActivityDate tc = (ColumnActivityDate)mapTCs.get("activityDate");
/* 417 */       if (tc != null) {
/* 418 */         tcManager.setDefaultSortColumnName(tableID, "activityDate");
/*     */         
/* 420 */         tc.setSortAscending(false);
/*     */       }
/*     */     }
/*     */     
/* 424 */     return (TableColumnCore[])mapTCs.values().toArray(new TableColumnCore[0]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean areNoneVisible(Map mapTCs)
/*     */   {
/* 433 */     boolean noneVisible = true;
/* 434 */     for (Iterator iter = mapTCs.values().iterator(); iter.hasNext();) {
/* 435 */       TableColumn tc = (TableColumn)iter.next();
/* 436 */       if (tc.isVisible()) {
/* 437 */         noneVisible = false;
/* 438 */         break;
/*     */       }
/*     */     }
/* 441 */     return noneVisible;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void initCoreColumns()
/*     */   {
/* 451 */     TableColumnCreator.initCoreColumns();
/*     */     
/*     */ 
/* 454 */     Map<String, cInfo> c = new LightHashMap(7);
/*     */     
/* 456 */     c.put("unopened", new cInfo(ColumnUnopened.class, ColumnUnopened.DATASOURCE_TYPE));
/*     */     
/* 458 */     c.put("name", new cInfo(ColumnThumbAndName.class, ColumnThumbAndName.DATASOURCE_TYPES));
/*     */     
/* 460 */     c.put("TorrentStream", new cInfo(ColumnStream.class, ColumnStream.DATASOURCE_TYPES));
/*     */     
/* 462 */     c.put("date_added", new cInfo(DateAddedItem.class, DateAddedItem.DATASOURCE_TYPE));
/*     */     
/* 464 */     c.put("DateCompleted", new cInfo(DateCompletedItem.class, DateCompletedItem.DATASOURCE_TYPE));
/*     */     
/* 466 */     c.put("ProgressETA", new cInfo(ColumnProgressETA.class, ColumnProgressETA.DATASOURCE_TYPE));
/*     */     
/* 468 */     c.put(ColumnChatMessageCount.COLUMN_ID, new cInfo(ColumnChatMessageCount.class, Download.class));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 473 */     Class ac = VuzeActivitiesEntry.class;
/*     */     
/* 475 */     c.put("activityNew", new cInfo(ColumnActivityNew.class, ac));
/* 476 */     c.put("activityType", new cInfo(ColumnActivityType.class, ac));
/* 477 */     c.put("activityText", new cInfo(ColumnActivityText.class, ac));
/* 478 */     c.put("activityActions", new cInfo(ColumnActivityActions.class, ac));
/*     */     
/* 480 */     c.put("activityDate", new cInfo(ColumnActivityDate.class, ac));
/*     */     
/* 482 */     c.put("Thumbnail", new cInfo(ColumnThumbnail.class, new Class[] { ac }));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 491 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/*     */     
/* 493 */     TableColumnCoreCreationListener tcCreator = new TableColumnCoreCreationListener()
/*     */     {
/*     */       public TableColumnCore createTableColumnCore(Class forDataSourceType, String tableID, String columnID)
/*     */       {
/* 497 */         TableColumnCreatorV3.cInfo info = (TableColumnCreatorV3.cInfo)this.val$c.get(columnID);
/*     */         
/* 499 */         if (info.cla.isAssignableFrom(TableColumnCore.class)) {
/* 500 */           return null;
/*     */         }
/*     */         try
/*     */         {
/* 504 */           Constructor<TableColumnCore> constructor = info.cla.getDeclaredConstructor(new Class[] { String.class });
/*     */           
/*     */ 
/*     */ 
/* 508 */           return (TableColumnCore)constructor.newInstance(new Object[] { tableID });
/*     */ 
/*     */         }
/*     */         catch (NoSuchMethodException e) {}catch (Exception e)
/*     */         {
/*     */ 
/* 514 */           Debug.out(e);
/*     */         }
/*     */         
/* 517 */         return null;
/*     */       }
/*     */       
/*     */       public void tableColumnCreated(TableColumn column) {
/* 521 */         TableColumnCreatorV3.cInfo info = (TableColumnCreatorV3.cInfo)this.val$c.get(column.getName());
/* 522 */         if (column.getClass().equals(info.cla)) {
/* 523 */           return;
/*     */         }
/*     */         try
/*     */         {
/* 527 */           Constructor constructor = info.cla.getDeclaredConstructor(new Class[] { TableColumn.class });
/*     */           
/*     */ 
/* 530 */           if (constructor != null) {
/* 531 */             constructor.newInstance(new Object[] { column });
/*     */           }
/*     */           
/*     */         }
/*     */         catch (NoSuchMethodException e) {}catch (Exception e)
/*     */         {
/* 537 */           Debug.out(e);
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 542 */     };
/* 543 */     tcManager.unregisterColumn(NameItem.DATASOURCE_TYPE, "name", null);
/*     */     
/*     */ 
/* 546 */     for (Iterator<String> iter = c.keySet().iterator(); iter.hasNext();) {
/* 547 */       String id = (String)iter.next();
/* 548 */       cInfo info = (cInfo)c.get(id);
/*     */       
/* 550 */       for (int i = 0; i < info.forDataSourceTypes.length; i++) {
/* 551 */         Class cla = info.forDataSourceTypes[i];
/*     */         
/* 553 */         tcManager.registerColumn(cla, id, tcCreator);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class cInfo
/*     */   {
/*     */     public Class cla;
/*     */     public Class[] forDataSourceTypes;
/*     */     
/*     */     public cInfo(Class cla, Class forDataSourceType)
/*     */     {
/* 566 */       this.cla = cla;
/* 567 */       this.forDataSourceTypes = new Class[] { forDataSourceType };
/*     */     }
/*     */     
/*     */ 
/*     */     public cInfo(Class cla, Class[] forDataSourceTypes)
/*     */     {
/* 573 */       this.cla = cla;
/* 574 */       this.forDataSourceTypes = forDataSourceTypes;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/utils/TableColumnCreatorV3.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */