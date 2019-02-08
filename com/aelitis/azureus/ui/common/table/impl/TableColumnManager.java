/*     */ package com.aelitis.azureus.ui.common.table.impl;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCoreCreationListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableStructureEventDispatcher;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager.ResetToDefaultsListener;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ import org.gudy.azureus2.core3.util.LightHashMap;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadTypeComplete;
/*     */ import org.gudy.azureus2.plugins.download.DownloadTypeIncomplete;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnCreationListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
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
/*     */ public class TableColumnManager
/*     */ {
/*     */   private static final String CONFIG_FILE = "tables.config";
/*     */   private static TableColumnManager instance;
/*     */   private static AEMonitor class_mon;
/*     */   private Map<String, Map> items;
/*  76 */   private AEMonitor items_mon = new AEMonitor("TableColumnManager:items");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  84 */   private Map autoHideOrder = new LightHashMap();
/*     */   
/*     */ 
/*     */   private Object tablesConfig;
/*     */   
/*     */ 
/*     */   private long lastTableConfigAccess;
/*     */   
/*     */ 
/*     */   private static Comparator<TableColumn> orderComparator;
/*     */   
/*     */ 
/*  96 */   private Map<String, TableColumnCreationListener> mapColumnIDsToListener = new LightHashMap();
/*  97 */   private Map<Class, List> mapDataSourceTypeToColumnIDs = new LightHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 102 */   private Map<String, String[]> mapTableDefaultColumns = new LightHashMap();
/* 103 */   private Map<String, Class[]> mapTableIDsDSTs = new LightHashMap();
/*     */   private static final Map<String, String> mapResetTable_Version;
/*     */   private static final boolean RERESET = false;
/*     */   
/*     */   static
/*     */   {
/*  55 */     COConfigurationManager.addResetToDefaultsListener(new COConfigurationManager.ResetToDefaultsListener()
/*     */     {
/*     */ 
/*     */       public void reset()
/*     */       {
/*     */ 
/*  61 */         TableColumnManager.getInstance().resetAllTables();
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*  66 */     });
/*  67 */     class_mon = new AEMonitor("TableColumnManager");
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
/* 109 */     orderComparator = new Comparator() {
/*     */       public int compare(TableColumn col0, TableColumn col1) {
/* 111 */         if ((col0 == null) || (col1 == null)) {
/* 112 */           return 0;
/*     */         }
/*     */         
/* 115 */         int iPositionA = col0.getPosition();
/* 116 */         if (iPositionA < 0)
/* 117 */           iPositionA = 65535 + iPositionA;
/* 118 */         int iPositionB = col1.getPosition();
/* 119 */         if (iPositionB < 0) {
/* 120 */           iPositionB = 65535 + iPositionB;
/*     */         }
/* 122 */         int i = iPositionA - iPositionB;
/* 123 */         if ((i != 0) || (iPositionA == 65533)) {
/* 124 */           return i;
/*     */         }
/*     */         
/* 127 */         String name0 = col0.getName();
/* 128 */         String name1 = col1.getName();
/*     */         
/* 130 */         String[] names = TableColumnManager.getInstance().getDefaultColumnNames(col0.getTableID());
/* 131 */         if (names != null) {
/* 132 */           for (String name : names) {
/* 133 */             if (name.equals(name0)) {
/* 134 */               return -1;
/*     */             }
/* 136 */             if (name.equals(name1)) {
/* 137 */               return 1;
/*     */             }
/*     */           }
/*     */         }
/* 141 */         return name0.compareTo(name1);
/*     */       }
/*     */       
/* 144 */     };
/* 145 */     mapResetTable_Version = new HashMap();
/* 146 */     mapResetTable_Version.put("DeviceLibrary", "4.4.0.7");
/* 147 */     mapResetTable_Version.put("TranscodeQueue", "4.4.0.7");
/* 148 */     mapResetTable_Version.put("MySeeders.big", "4.4.0.7");
/* 149 */     mapResetTable_Version.put("MyLibrary.big", "4.4.0.7");
/* 150 */     mapResetTable_Version.put("MyTorrents.big", "4.4.0.7");
/* 151 */     mapResetTable_Version.put("Unopened.big", "4.6.0.1");
/* 152 */     mapResetTable_Version.put("Unopened", "4.6.0.1");
/*     */   }
/*     */   
/*     */   private TableColumnManager()
/*     */   {
/* 157 */     this.items = new HashMap();
/*     */   }
/*     */   
/*     */ 
/*     */   public static TableColumnManager getInstance()
/*     */   {
/*     */     try
/*     */     {
/* 165 */       class_mon.enter();
/*     */       
/* 167 */       if (instance == null)
/* 168 */         instance = new TableColumnManager();
/* 169 */       return instance;
/*     */     }
/*     */     finally {
/* 172 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addColumns(TableColumnCore[] itemsToAdd)
/*     */   {
/*     */     try
/*     */     {
/* 181 */       this.items_mon.enter();
/* 182 */       for (int i = 0; i < itemsToAdd.length; i++) {
/* 183 */         TableColumnCore item = itemsToAdd[i];
/* 184 */         if ((item != null) && (!item.isRemoved()))
/*     */         {
/*     */ 
/* 187 */           String name = item.getName();
/* 188 */           String sTableID = item.getTableID();
/* 189 */           Map mTypes = (Map)this.items.get(sTableID);
/* 190 */           if (mTypes == null)
/*     */           {
/* 192 */             mTypes = new LinkedHashMap();
/* 193 */             this.items.put(sTableID, mTypes);
/*     */           }
/* 195 */           if (!mTypes.containsKey(name)) {
/* 196 */             mTypes.put(name, item);
/* 197 */             Map mapColumnConfig = getTableConfigMap(sTableID);
/* 198 */             item.loadSettings(mapColumnConfig);
/*     */           }
/*     */         } }
/* 201 */       for (int i = 0; i < itemsToAdd.length; i++) {
/* 202 */         TableColumnCore item = itemsToAdd[i];
/*     */         
/* 204 */         if ((item != null) && (!item.isRemoved()) && (!item.getColumnAdded())) {
/* 205 */           item.setColumnAdded();
/*     */         }
/*     */       }
/*     */     } catch (Exception e) {
/* 209 */       System.out.println("Error while adding Table Column Extension");
/* 210 */       Debug.printStackTrace(e);
/*     */     } finally {
/* 212 */       this.items_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeColumns(TableColumnCore[] itemsToRemove)
/*     */   {
/*     */     try
/*     */     {
/* 221 */       this.items_mon.enter();
/* 222 */       for (int i = 0; i < itemsToRemove.length; i++) {
/* 223 */         TableColumnCore item = itemsToRemove[i];
/* 224 */         String name = item.getName();
/* 225 */         String sTableID = item.getTableID();
/* 226 */         Map mTypes = (Map)this.items.get(sTableID);
/* 227 */         if ((mTypes == null) || 
/* 228 */           (mTypes.remove(name) == null)) {}
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 233 */       System.out.println("Error while adding Table Column Extension");
/* 234 */       Debug.printStackTrace(e);
/*     */     } finally {
/* 236 */       this.items_mon.exit();
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
/*     */ 
/*     */   public Map<String, TableColumnCore> getTableColumnsAsMap(Class forDataSourceType, String sTableID)
/*     */   {
/*     */     try
/*     */     {
/* 253 */       this.items_mon.enter();
/* 254 */       Map<String, TableColumnCore> mReturn = new LinkedHashMap();
/* 255 */       Map<String, TableColumnCore> mTypes = getAllTableColumnCore(forDataSourceType, sTableID);
/*     */       
/* 257 */       if (mTypes != null) {
/* 258 */         mReturn.putAll(mTypes);
/*     */       }
/*     */       
/* 261 */       return mReturn;
/*     */     }
/*     */     finally {
/* 264 */       this.items_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public int getTableColumnCount(String sTableID) {
/* 269 */     Map mTypes = (Map)this.items.get(sTableID);
/* 270 */     if (mTypes == null) {
/* 271 */       return 0;
/*     */     }
/* 273 */     return mTypes.size();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TableColumnCore[] getAllTableColumnCoreAsArray(Class forDataSourceType, String tableID)
/*     */   {
/* 280 */     Map mTypes = getAllTableColumnCore(forDataSourceType, tableID);
/* 281 */     TableColumnCore[] columns = (TableColumnCore[])mTypes.values().toArray(new TableColumnCore[mTypes.values().size()]);
/*     */     
/* 283 */     return columns;
/*     */   }
/*     */   
/*     */   public String[] getDefaultColumnNames(String tableID) {
/* 287 */     String[] columnNames = (String[])this.mapTableDefaultColumns.get(tableID);
/* 288 */     return columnNames;
/*     */   }
/*     */   
/*     */   public void setDefaultColumnNames(String tableID, TableColumn[] columns) {
/* 292 */     List<String> names = new ArrayList(columns.length);
/* 293 */     for (TableColumn column : columns) {
/* 294 */       if (column.isVisible()) {
/* 295 */         names.add(column.getName());
/*     */       }
/*     */     }
/* 298 */     setDefaultColumnNames(tableID, (String[])names.toArray(new String[names.size()]));
/*     */   }
/*     */   
/*     */   public void setDefaultColumnNames(String tableID, String[] columnNames) {
/* 302 */     this.mapTableDefaultColumns.put(tableID, columnNames);
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
/*     */   private Map<String, TableColumnCore> getAllTableColumnCore(Class forDataSourceType, String tableID)
/*     */   {
/* 385 */     Map mapExisting = null;
/*     */     try {
/* 387 */       this.items_mon.enter();
/*     */       
/* 389 */       mapExisting = (Map)this.items.get(tableID);
/* 390 */       if (mapExisting == null) {
/* 391 */         mapExisting = new LinkedHashMap();
/* 392 */         this.items.put(tableID, mapExisting);
/*     */       }
/*     */       
/* 395 */       if (forDataSourceType != null) {
/* 396 */         Map<Class<?>, List> mapDST = new HashMap();
/* 397 */         List listDST = (List)this.mapDataSourceTypeToColumnIDs.get(forDataSourceType);
/* 398 */         if ((listDST != null) && (listDST.size() > 0)) {
/* 399 */           mapDST.put(forDataSourceType, listDST);
/*     */         }
/* 401 */         if ((forDataSourceType.equals(DownloadTypeComplete.class)) || (forDataSourceType.equals(DownloadTypeIncomplete.class)))
/*     */         {
/* 403 */           listDST = (List)this.mapDataSourceTypeToColumnIDs.get(Download.class);
/* 404 */           if ((listDST != null) && (listDST.size() > 0)) {
/* 405 */             mapDST.put(Download.class, listDST);
/*     */           }
/* 407 */         } else if (Download.class.equals(forDataSourceType)) {
/* 408 */           listDST = (List)this.mapDataSourceTypeToColumnIDs.get(DownloadTypeComplete.class);
/* 409 */           if ((listDST != null) && (listDST.size() > 0)) {
/* 410 */             mapDST.put(DownloadTypeComplete.class, listDST);
/*     */           }
/* 412 */           listDST = (List)this.mapDataSourceTypeToColumnIDs.get(DownloadTypeIncomplete.class);
/* 413 */           if ((listDST != null) && (listDST.size() > 0)) {
/* 414 */             mapDST.put(DownloadTypeIncomplete.class, listDST);
/*     */           }
/*     */         }
/*     */         
/* 418 */         doAddCreate(mapExisting, tableID, mapDST);
/*     */       }
/*     */     } finally {
/* 421 */       this.items_mon.exit();
/*     */     }
/*     */     
/*     */ 
/* 425 */     return mapExisting;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void doAddCreate(Map mTypes, String tableID, Map<Class<?>, List> mapDST)
/*     */   {
/* 437 */     this.mapTableIDsDSTs.put(tableID, mapDST.keySet().toArray(new Class[0]));
/* 438 */     ArrayList<TableColumnCore> listAdded = new ArrayList();
/* 439 */     for (Iterator i$ = mapDST.keySet().iterator(); i$.hasNext();) { forDataSourceType = (Class)i$.next();
/* 440 */       List listDST = (List)mapDST.get(forDataSourceType);
/*     */       
/* 442 */       for (iter = listDST.iterator(); iter.hasNext();) {
/* 443 */         String columnID = (String)iter.next();
/* 444 */         if (!mTypes.containsKey(columnID))
/*     */           try {
/* 446 */             TableColumnCreationListener l = (TableColumnCreationListener)this.mapColumnIDsToListener.get(forDataSourceType + "." + columnID);
/*     */             
/* 448 */             TableColumnCore tc = null;
/* 449 */             if ((l instanceof TableColumnCoreCreationListener)) {
/* 450 */               tc = ((TableColumnCoreCreationListener)l).createTableColumnCore(forDataSourceType, tableID, columnID);
/*     */             }
/*     */             
/* 453 */             if (tc == null) {
/* 454 */               tc = new TableColumnImpl(tableID, columnID);
/* 455 */               tc.addDataSourceType(forDataSourceType);
/*     */             }
/*     */             
/* 458 */             if (l != null) {
/* 459 */               l.tableColumnCreated(tc);
/*     */             }
/*     */             
/* 462 */             listAdded.add(tc);
/*     */           } catch (Exception e) {
/* 464 */             Debug.out(e);
/*     */           }
/*     */       }
/*     */     }
/*     */     Class forDataSourceType;
/*     */     Iterator iter;
/* 470 */     addColumns((TableColumnCore[])listAdded.toArray(new TableColumnCore[0]));
/*     */   }
/*     */   
/*     */   public String[] getTableIDs()
/*     */   {
/*     */     try
/*     */     {
/* 477 */       this.items_mon.enter();
/*     */       
/* 479 */       Set<String> ids = this.items.keySet();
/*     */       
/* 481 */       return (String[])ids.toArray(new String[ids.size()]);
/*     */     }
/*     */     finally {
/* 484 */       this.items_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public String[] appendLists(String[] list1, String[] list2) {
/* 489 */     int size = list1.length + list2.length;
/* 490 */     String[] list = new String[size];
/* 491 */     System.arraycopy(list1, 0, list, 0, list1.length);
/* 492 */     System.arraycopy(list2, 0, list, list1.length, list2.length);
/* 493 */     return list;
/*     */   }
/*     */   
/*     */   public TableColumnCore getTableColumnCore(String sTableID, String sColumnName)
/*     */   {
/* 498 */     Map mTypes = (Map)this.items.get(sTableID);
/* 499 */     if (mTypes == null)
/* 500 */       return null;
/* 501 */     return (TableColumnCore)mTypes.get(sColumnName);
/*     */   }
/*     */   
/*     */   public void ensureIntegrety(Class dataSourceType, String sTableID) {
/* 505 */     Map mTypes = (Map)this.items.get(sTableID);
/* 506 */     if (mTypes == null) {
/* 507 */       return;
/*     */     }
/* 509 */     TableColumnCore[] tableColumns = (TableColumnCore[])mTypes.values().toArray(new TableColumnCore[mTypes.values().size()]);
/*     */     
/*     */ 
/* 512 */     Arrays.sort(tableColumns, getTableColumnOrderComparator());
/*     */     
/* 514 */     int iPos = 0;
/* 515 */     for (int i = 0; i < tableColumns.length; i++) {
/* 516 */       int iCurPos = tableColumns[i].getPosition();
/* 517 */       if (iCurPos == -1) {
/* 518 */         tableColumns[i].setVisible(false);
/*     */       } else {
/* 520 */         tableColumns[i].setPositionNoShift(iPos++);
/*     */       }
/*     */     }
/*     */     
/* 524 */     if (iPos == 0)
/*     */     {
/* 526 */       resetColumns(dataSourceType, sTableID);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getDefaultSortColumnName(String tableID)
/*     */   {
/* 532 */     Map mapTableConfig = getTableConfigMap(tableID);
/* 533 */     Object object = mapTableConfig.get("SortColumn");
/* 534 */     if ((object instanceof byte[])) {
/* 535 */       object = new String((byte[])object);
/*     */     }
/* 537 */     if ((object instanceof String)) {
/* 538 */       return (String)object;
/*     */     }
/*     */     
/* 541 */     String s = COConfigurationManager.getStringParameter(tableID + ".sortColumn");
/* 542 */     if (s != null) {
/* 543 */       COConfigurationManager.removeParameter(tableID + ".sortColumn");
/* 544 */       COConfigurationManager.removeParameter(tableID + ".sortAsc");
/*     */     }
/* 546 */     return s;
/*     */   }
/*     */   
/*     */   public void setDefaultSortColumnName(String tableID, String columnName) {
/* 550 */     setDefaultSortColumnName(tableID, columnName, false);
/*     */   }
/*     */   
/*     */   public void setDefaultSortColumnName(String tableID, String columnName, boolean force) {
/* 554 */     Map mapTableConfig = getTableConfigMap(tableID);
/* 555 */     Object existing = mapTableConfig.get("SortColumn");
/* 556 */     if (existing != null) {
/* 557 */       if (!force) {
/* 558 */         return;
/*     */       }
/* 560 */       String str = (existing instanceof byte[]) ? new String((byte[])existing) : (String)existing;
/* 561 */       if (str.equals(columnName)) {
/* 562 */         return;
/*     */       }
/*     */     }
/* 565 */     mapTableConfig.put("SortColumn", columnName);
/* 566 */     saveTableConfigs();
/*     */   }
/*     */   
/*     */   private void saveTableConfigs() {
/* 570 */     if ((this.tablesConfig instanceof Map)) {
/* 571 */       FileUtil.writeResilientConfigFile("tables.config", (Map)this.tablesConfig);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void saveTableColumns(Class forDataSourceType, String sTableID)
/*     */   {
/*     */     try
/*     */     {
/* 582 */       Map mapTableConfig = getTableConfigMap(sTableID);
/* 583 */       TableColumnCore[] tcs = getAllTableColumnCoreAsArray(forDataSourceType, sTableID);
/*     */       
/* 585 */       for (int i = 0; i < tcs.length; i++) {
/* 586 */         if (tcs[i] != null)
/* 587 */           tcs[i].saveSettings(mapTableConfig);
/*     */       }
/* 589 */       saveTableConfigs();
/*     */     } catch (Exception e) {
/* 591 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean loadTableColumnSettings(Class forDataSourceType, String sTableID) {
/*     */     try {
/* 597 */       Map mapTableConfig = getTableConfigMap(sTableID);
/* 598 */       int size = mapTableConfig.size();
/* 599 */       if (size == 0) {
/* 600 */         return false;
/*     */       }
/* 602 */       boolean hasColumnInfo = false;
/* 603 */       for (Object key : mapTableConfig.keySet()) {
/* 604 */         if (((key instanceof String)) && 
/* 605 */           (((String)key).startsWith("Column."))) {
/* 606 */           hasColumnInfo = true;
/* 607 */           break;
/*     */         }
/*     */       }
/*     */       
/* 611 */       if (!hasColumnInfo) {
/* 612 */         return false;
/*     */       }
/* 614 */       TableColumnCore[] tcs = getAllTableColumnCoreAsArray(forDataSourceType, sTableID);
/*     */       
/* 616 */       for (int i = 0; i < tcs.length; i++) {
/* 617 */         if (tcs[i] != null)
/* 618 */           tcs[i].loadSettings(mapTableConfig);
/*     */       }
/*     */     } catch (Exception e) {
/* 621 */       Debug.out(e);
/*     */     }
/* 623 */     return true;
/*     */   }
/*     */   
/*     */   private Map getTablesConfigMap() {
/* 627 */     this.lastTableConfigAccess = SystemTime.getMonotonousTime();
/*     */     
/* 629 */     if (this.tablesConfig == null) {
/* 630 */       this.tablesConfig = FileUtil.readResilientConfigFile("tables.config");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 640 */       SimpleTimer.addEvent("DisposeTableConfigMap", SystemTime.getOffsetTime(30000L), new TimerEventPerformer()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public void perform(TimerEvent event)
/*     */         {
/*     */ 
/*     */ 
/* 649 */           synchronized (TableColumnManager.this)
/*     */           {
/* 651 */             long now = SystemTime.getMonotonousTime();
/*     */             
/* 653 */             if (now - TableColumnManager.this.lastTableConfigAccess > 25000L)
/*     */             {
/* 655 */               TableColumnManager.this.tablesConfig = null;
/*     */             }
/*     */             else {
/* 658 */               SimpleTimer.addEvent("DisposeTableConfigMap", SystemTime.getOffsetTime(30000L), this);
/*     */             }
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 667 */     return (Map)this.tablesConfig;
/*     */   }
/*     */   
/*     */   public Map getTableConfigMap(String sTableID) {
/* 671 */     synchronized (this) {
/* 672 */       String key = "Table." + sTableID;
/*     */       
/* 674 */       Map mapTablesConfig = getTablesConfigMap();
/*     */       
/* 676 */       Map mapTableConfig = (Map)mapTablesConfig.get(key);
/* 677 */       if (mapTableConfig == null) {
/* 678 */         mapTableConfig = new HashMap();
/* 679 */         mapTablesConfig.put("Table." + sTableID, mapTableConfig);
/*     */       } else {
/* 681 */         String resetIfLastResetBelowVersion = (String)mapResetTable_Version.get(sTableID);
/* 682 */         if (resetIfLastResetBelowVersion != null) {
/* 683 */           String lastReset = MapUtils.getMapString(mapTableConfig, "last.reset", "0.0.0.0");
/*     */           
/* 685 */           if (Constants.compareVersions(lastReset, resetIfLastResetBelowVersion) < 0) {
/* 686 */             mapTableConfig.clear();
/* 687 */             mapTableConfig.put("last.reset", Constants.getBaseVersion());
/* 688 */             saveTableConfigs();
/* 689 */             mapResetTable_Version.remove(sTableID);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 694 */       return mapTableConfig;
/*     */     }
/*     */   }
/*     */   
/*     */   public void setAutoHideOrder(String sTableID, String[] autoHideOrderColumnIDs) {
/* 699 */     ArrayList autoHideOrderList = new ArrayList(autoHideOrderColumnIDs.length);
/* 700 */     for (int i = 0; i < autoHideOrderColumnIDs.length; i++) {
/* 701 */       String sColumnID = autoHideOrderColumnIDs[i];
/* 702 */       TableColumnCore column = getTableColumnCore(sTableID, sColumnID);
/* 703 */       if (column != null) {
/* 704 */         autoHideOrderList.add(column);
/*     */       }
/*     */     }
/*     */     
/* 708 */     this.autoHideOrder.put(sTableID, autoHideOrderList);
/*     */   }
/*     */   
/*     */   public List getAutoHideOrder(String sTableID) {
/* 712 */     List list = (List)this.autoHideOrder.get(sTableID);
/* 713 */     if (list == null) {
/* 714 */       return Collections.EMPTY_LIST;
/*     */     }
/* 716 */     return list;
/*     */   }
/*     */   
/*     */ 
/*     */   public void generateDiagnostics(IndentWriter writer)
/*     */   {
/*     */     try
/*     */     {
/* 724 */       this.items_mon.enter();
/*     */       
/* 726 */       writer.println("TableColumns");
/*     */       
/* 728 */       for (iter = this.items.keySet().iterator(); iter.hasNext();) {
/* 729 */         String sTableID = (String)iter.next();
/* 730 */         Map mTypes = (Map)this.items.get(sTableID);
/*     */         
/* 732 */         writer.indent();
/* 733 */         writer.println(sTableID + ": " + mTypes.size() + " columns:");
/*     */         
/* 735 */         writer.indent();
/* 736 */         for (Iterator iter2 = mTypes.values().iterator(); iter2.hasNext();) {
/* 737 */           TableColumnCore tc = (TableColumnCore)iter2.next();
/* 738 */           tc.generateDiagnostics(writer);
/*     */         }
/* 740 */         writer.exdent();
/*     */         
/* 742 */         writer.exdent();
/*     */       }
/*     */     } catch (Exception e) { Iterator iter;
/* 745 */       e.printStackTrace();
/*     */     } finally {
/* 747 */       this.items_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public static Comparator<TableColumn> getTableColumnOrderComparator()
/*     */   {
/* 753 */     return orderComparator;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerColumn(Class forDataSourceType, String columnID, TableColumnCreationListener listener)
/*     */   {
/* 765 */     if (listener != null) {
/* 766 */       this.mapColumnIDsToListener.put(forDataSourceType + "." + columnID, listener);
/*     */     }
/*     */     try {
/* 769 */       this.items_mon.enter();
/*     */       
/* 771 */       List list = (List)this.mapDataSourceTypeToColumnIDs.get(forDataSourceType);
/* 772 */       if (list == null) {
/* 773 */         list = new ArrayList(1);
/* 774 */         this.mapDataSourceTypeToColumnIDs.put(forDataSourceType, list);
/*     */       }
/* 776 */       if (!list.contains(columnID)) {
/* 777 */         list.add(columnID);
/*     */       }
/*     */     } finally {
/* 780 */       this.items_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void unregisterColumn(Class forDataSourceType, String columnID, TableColumnCreationListener listener)
/*     */   {
/*     */     try {
/* 787 */       this.items_mon.enter();
/*     */       
/* 789 */       this.mapColumnIDsToListener.remove(forDataSourceType + "." + columnID);
/* 790 */       List list = (List)this.mapDataSourceTypeToColumnIDs.get(forDataSourceType);
/* 791 */       if (list != null) {
/* 792 */         list.remove(columnID);
/*     */       }
/*     */     } finally {
/* 795 */       this.items_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public TableColumnInfo getColumnInfo(Class forDataSourceType, String forTableID, String columnID)
/*     */   {
/* 802 */     TableColumnCore column = getTableColumnCore(forTableID, columnID);
/*     */     
/* 804 */     return column == null ? null : getColumnInfo(column);
/*     */   }
/*     */   
/*     */   public TableColumnInfo getColumnInfo(TableColumnCore column)
/*     */   {
/* 809 */     TableColumnInfoImpl columnInfo = new TableColumnInfoImpl(column);
/* 810 */     List<TableColumnExtraInfoListener> listeners = column.getColumnExtraInfoListeners();
/* 811 */     for (TableColumnExtraInfoListener l : listeners) {
/* 812 */       l.fillTableColumnInfo(columnInfo);
/*     */     }
/* 814 */     if ((columnInfo.getCategories() == null) && (!(column instanceof CoreTableColumn))) {
/* 815 */       columnInfo.addCategories(new String[] { "plugin" });
/* 816 */       columnInfo.setProficiency((byte)0);
/*     */     }
/*     */     
/* 819 */     return columnInfo;
/*     */   }
/*     */   
/*     */ 
/*     */   private void resetAllTables()
/*     */   {
/* 825 */     for (String tableID : new ArrayList(this.mapTableDefaultColumns.keySet()))
/*     */     {
/* 827 */       Class[] dataSourceTypes = (Class[])this.mapTableIDsDSTs.get(tableID);
/*     */       
/* 829 */       if ((dataSourceTypes == null) || (dataSourceTypes.length == 0)) {
/* 830 */         resetColumns(null, tableID);
/*     */       } else {
/* 832 */         for (Class dataSourceType : dataSourceTypes) {
/* 833 */           resetColumns(dataSourceType, tableID);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public Object getColumnData(String columnID, String key) {
/* 840 */     Map mapTablesConfig = getTablesConfigMap();
/* 841 */     Map mapColumns = MapUtils.getMapMap(mapTablesConfig, "Columns", null);
/* 842 */     if (mapColumns != null) {
/* 843 */       Map mapConfig = MapUtils.getMapMap(mapColumns, columnID, null);
/* 844 */       if (mapConfig != null) {
/* 845 */         return mapConfig.get(key);
/*     */       }
/*     */     }
/* 848 */     return null;
/*     */   }
/*     */   
/*     */   public void setColumnData(String columnID, String key, Object value) {
/* 852 */     synchronized (this.tablesConfig) {
/* 853 */       Map mapTablesConfig = getTablesConfigMap();
/* 854 */       Map mapColumns = MapUtils.getMapMap(mapTablesConfig, "Columns", null);
/* 855 */       if (mapColumns == null) {
/* 856 */         mapColumns = new LightHashMap(2);
/* 857 */         mapTablesConfig.put("Columns", mapColumns);
/*     */       }
/*     */       
/* 860 */       Map mapConfig = MapUtils.getMapMap(mapColumns, columnID, null);
/* 861 */       if (mapConfig == null) {
/* 862 */         mapConfig = new LightHashMap(2);
/* 863 */         mapColumns.put(columnID, mapConfig);
/*     */       }
/*     */       
/* 866 */       mapConfig.put(key, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeColumnData(String columnID, String key) {
/* 871 */     synchronized (this.tablesConfig) {
/* 872 */       Map mapTablesConfig = getTablesConfigMap();
/* 873 */       Map mapColumns = MapUtils.getMapMap(mapTablesConfig, "Columns", null);
/* 874 */       if (mapColumns != null) {
/* 875 */         Map mapConfig = MapUtils.getMapMap(mapColumns, columnID, null);
/* 876 */         if (mapConfig != null) {
/* 877 */           mapConfig.remove(key);
/* 878 */           if (mapConfig.size() < 1) {
/* 879 */             mapColumns.remove(columnID);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void resetColumns(Class dataSourceType, String tableID) {
/* 887 */     TableColumnCore[] allTableColumns = getAllTableColumnCoreAsArray(dataSourceType, tableID);
/*     */     
/* 889 */     if (allTableColumns != null) {
/* 890 */       for (TableColumnCore column : allTableColumns) {
/* 891 */         if (column != null) {
/* 892 */           column.setVisible(false);
/* 893 */           column.reset();
/*     */         }
/*     */       }
/*     */     }
/* 897 */     String[] defaultColumnNames = getDefaultColumnNames(tableID);
/* 898 */     if (defaultColumnNames != null) {
/* 899 */       int i = 0;
/* 900 */       for (String name : defaultColumnNames) {
/* 901 */         TableColumnCore column = getTableColumnCore(tableID, name);
/* 902 */         if (column != null) {
/* 903 */           column.setVisible(true);
/* 904 */           column.setPositionNoShift(i++);
/*     */         }
/*     */       }
/*     */     }
/* 908 */     saveTableColumns(dataSourceType, tableID);
/* 909 */     TableStructureEventDispatcher.getInstance(tableID).tableStructureChanged(true, dataSourceType);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/table/impl/TableColumnManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */