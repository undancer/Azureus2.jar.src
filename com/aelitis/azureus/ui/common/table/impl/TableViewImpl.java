/*      */ package com.aelitis.azureus.ui.common.table.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.ui.common.table.TableCellCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableCountChangeListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableDataSourceChangedListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableExpansionChangeListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableGroupRowRunner;
/*      */ import com.aelitis.azureus.ui.common.table.TableGroupRowVisibilityRunner;
/*      */ import com.aelitis.azureus.ui.common.table.TableLifeCycleListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableRefreshListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableSelectionListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableView;
/*      */ import com.aelitis.azureus.ui.common.table.TableViewFilterCheck;
/*      */ import com.aelitis.azureus.ui.common.table.TableViewFilterCheck.TableViewFilterCheckEx;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.impl.ConfigurationManager;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableRowRefreshListener;
/*      */ 
/*      */ public abstract class TableViewImpl<DATASOURCETYPE> implements TableView<DATASOURCETYPE>, com.aelitis.azureus.ui.common.table.TableStructureModificationListener<DATASOURCETYPE>
/*      */ {
/*   42 */   private static final LogIDs LOGID = LogIDs.GUI;
/*      */   
/*      */ 
/*      */   private static final boolean DEBUG_SORTER = false;
/*      */   
/*   47 */   public static final boolean DEBUGADDREMOVE = System.getProperty("debug.swt.table.addremove", "0").equals("1");
/*      */   
/*      */ 
/*      */   public static final boolean DEBUG_SELECTION = false;
/*      */   
/*      */ 
/*      */   private static final String CFG_SORTDIRECTION = "config.style.table.defaultSortOrder";
/*      */   
/*   55 */   protected static final ConfigurationManager configMan = ConfigurationManager.getInstance();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String tableID;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String propertiesPrefix;
/*      */   
/*      */ 
/*      */ 
/*      */   private final Class<?> classPluginDataSourceType;
/*      */   
/*      */ 
/*      */ 
/*   73 */   private boolean bReallyAddingDataSources = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private TableColumnCore sortColumn;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private long lLastSortedOn;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   88 */   private AEMonitor listeners_mon = new AEMonitor("tablelisteners");
/*      */   
/*      */ 
/*      */   private ArrayList<TableRowRefreshListener> rowRefreshListeners;
/*      */   
/*   93 */   private CopyOnWriteList<TableDataSourceChangedListener> listenersDataSourceChanged = new CopyOnWriteList();
/*      */   
/*   95 */   private CopyOnWriteList<TableSelectionListener> listenersSelection = new CopyOnWriteList();
/*      */   
/*   97 */   private CopyOnWriteList<TableLifeCycleListener> listenersLifeCycle = new CopyOnWriteList();
/*      */   
/*   99 */   private CopyOnWriteList<TableRefreshListener> listenersRefresh = new CopyOnWriteList();
/*      */   
/*  101 */   private CopyOnWriteList<TableCountChangeListener> listenersCountChange = new CopyOnWriteList(1);
/*      */   
/*  103 */   private CopyOnWriteList<TableExpansionChangeListener> listenersExpansionChange = new CopyOnWriteList(1);
/*      */   
/*      */   private Object parentDataSource;
/*      */   
/*  107 */   private Object rows_sync = new Object();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private List<TableRowCore> sortedRows;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private IdentityHashMap<DATASOURCETYPE, TableRowCore> mapDataSourceToRow;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private IdentityHashMap<DATASOURCETYPE, String> listUnfilteredDataSources;
/*      */   
/*      */ 
/*      */ 
/*  126 */   private IdentityHashMap<DATASOURCETYPE, String> dataSourcesToAdd = new IdentityHashMap(4);
/*      */   
/*      */ 
/*  129 */   private IdentityHashMap<DATASOURCETYPE, String> dataSourcesToRemove = new IdentityHashMap(4);
/*      */   
/*      */   protected filter<DATASOURCETYPE> filter;
/*      */   
/*      */ 
/*      */   public static class filter<DATASOURCETYPE>
/*      */   {
/*      */     public TimerEvent eventUpdate;
/*  137 */     public String text = "";
/*      */     
/*      */     public long lastFilterTime;
/*      */     
/*  141 */     public boolean regex = false;
/*      */     
/*      */     public TableViewFilterCheck<DATASOURCETYPE> checker;
/*      */     
/*  145 */     public String nextText = "";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*  150 */   private DataSourceCallBackUtil.addDataSourceCallback processDataSourceQueueCallback = new DataSourceCallBackUtil.addDataSourceCallback() {
/*      */     public void process() {
/*  152 */       TableViewImpl.this.processDataSourceQueue();
/*      */     }
/*      */     
/*      */     public void debug(String str) {
/*  156 */       TableViewImpl.this.debug(str);
/*      */     }
/*      */   };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private TableColumnCore[] basicItems;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private TableColumnCore[] tableColumns;
/*      */   
/*      */ 
/*      */ 
/*      */   private TableColumnCore[] columnsOrdered;
/*      */   
/*      */ 
/*      */ 
/*  176 */   private List<TableRowCore> selectedRows = new ArrayList(1);
/*      */   
/*      */   private List<Object> listSelectedCoreDataSources;
/*      */   
/*  180 */   private boolean headerVisible = true;
/*      */   
/*  182 */   private boolean menuEnabled = true;
/*      */   
/*  184 */   private boolean provideIndexesOnRemove = false;
/*      */   
/*      */ 
/*      */ 
/*      */   public TableViewImpl(Class<?> pluginDataSourceType, String _sTableID, String _sPropertiesPrefix, TableColumnCore[] _basicItems)
/*      */   {
/*  190 */     this.classPluginDataSourceType = pluginDataSourceType;
/*  191 */     this.propertiesPrefix = _sPropertiesPrefix;
/*  192 */     this.tableID = _sTableID;
/*  193 */     this.basicItems = _basicItems;
/*  194 */     this.mapDataSourceToRow = new IdentityHashMap();
/*  195 */     this.sortedRows = new ArrayList();
/*  196 */     this.listUnfilteredDataSources = new IdentityHashMap();
/*  197 */     initializeColumnDefs();
/*      */   }
/*      */   
/*      */ 
/*      */   private void initializeColumnDefs()
/*      */   {
/*  203 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/*      */     
/*  205 */     if (this.basicItems != null) {
/*  206 */       if (tcManager.getTableColumnCount(this.tableID) != this.basicItems.length) {
/*  207 */         tcManager.addColumns(this.basicItems);
/*      */       }
/*  209 */       this.basicItems = null;
/*      */     }
/*      */     
/*  212 */     this.tableColumns = tcManager.getAllTableColumnCoreAsArray(getDataSourceType(), this.tableID);
/*      */     
/*      */ 
/*      */ 
/*  216 */     tcManager.ensureIntegrety(this.classPluginDataSourceType, this.tableID);
/*      */   }
/*      */   
/*      */ 
/*      */   public void addSelectionListener(TableSelectionListener listener, boolean bFireSelection)
/*      */   {
/*  222 */     this.listenersSelection.add(listener);
/*  223 */     if (bFireSelection) {
/*  224 */       TableRowCore[] rows = getSelectedRows();
/*  225 */       listener.selected(rows);
/*  226 */       listener.focusChanged(getFocusedRow());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void addTableDataSourceChangedListener(TableDataSourceChangedListener l, boolean trigger)
/*      */   {
/*  233 */     this.listenersDataSourceChanged.add(l);
/*  234 */     if (trigger) {
/*  235 */       l.tableDataSourceChanged(this.parentDataSource);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeTableDataSourceChangedListener(TableDataSourceChangedListener l)
/*      */   {
/*  242 */     this.listenersDataSourceChanged.remove(l);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setParentDataSource(Object newDataSource)
/*      */   {
/*  248 */     this.parentDataSource = newDataSource;
/*  249 */     Object[] listeners = this.listenersDataSourceChanged.toArray();
/*  250 */     for (int i = 0; i < listeners.length; i++) {
/*  251 */       TableDataSourceChangedListener l = (TableDataSourceChangedListener)listeners[i];
/*  252 */       l.tableDataSourceChanged(newDataSource);
/*      */     }
/*      */   }
/*      */   
/*      */   public Object getParentDataSource() {
/*  257 */     return this.parentDataSource;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void triggerDefaultSelectedListeners(TableRowCore[] selectedRows, int keyMask)
/*      */   {
/*  265 */     for (Iterator iter = this.listenersSelection.iterator(); iter.hasNext();) {
/*  266 */       TableSelectionListener l = (TableSelectionListener)iter.next();
/*  267 */       l.defaultSelected(selectedRows, keyMask);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void triggerLifeCycleListener(int eventType)
/*      */   {
/*  275 */     Object[] listeners = this.listenersLifeCycle.toArray();
/*  276 */     if (eventType == 0) {
/*  277 */       for (int i = 0; i < listeners.length; i++) {
/*  278 */         TableLifeCycleListener l = (TableLifeCycleListener)listeners[i];
/*      */         try {
/*  280 */           l.tableViewInitialized();
/*      */         } catch (Exception e) {
/*  282 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     } else {
/*  286 */       for (int i = 0; i < listeners.length; i++) {
/*  287 */         TableLifeCycleListener l = (TableLifeCycleListener)listeners[i];
/*      */         try {
/*  289 */           l.tableViewDestroyed();
/*      */         } catch (Exception e) {
/*  291 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void triggerSelectionListeners(TableRowCore[] rows) {
/*  298 */     if ((rows == null) || (rows.length == 0)) {
/*  299 */       return;
/*      */     }
/*  301 */     Object[] listeners = this.listenersSelection.toArray();
/*  302 */     for (int i = 0; i < listeners.length; i++) {
/*  303 */       TableSelectionListener l = (TableSelectionListener)listeners[i];
/*  304 */       l.selected(rows);
/*      */     }
/*      */   }
/*      */   
/*      */   protected void triggerDeselectionListeners(TableRowCore[] rows) {
/*  309 */     if (rows == null) {
/*  310 */       return;
/*      */     }
/*  312 */     Object[] listeners = this.listenersSelection.toArray();
/*  313 */     for (int i = 0; i < listeners.length; i++) {
/*  314 */       TableSelectionListener l = (TableSelectionListener)listeners[i];
/*      */       try {
/*  316 */         l.deselected(rows);
/*      */       } catch (Exception e) {
/*  318 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void triggerMouseEnterExitRow(TableRowCore row, boolean enter) {
/*  324 */     if (row == null) {
/*  325 */       return;
/*      */     }
/*  327 */     Object[] listeners = this.listenersSelection.toArray();
/*  328 */     for (int i = 0; i < listeners.length; i++) {
/*  329 */       TableSelectionListener l = (TableSelectionListener)listeners[i];
/*  330 */       if (enter) {
/*  331 */         l.mouseEnter(row);
/*      */       } else {
/*  333 */         l.mouseExit(row);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void triggerFocusChangedListeners(TableRowCore row) {
/*  339 */     Object[] listeners = this.listenersSelection.toArray();
/*  340 */     for (int i = 0; i < listeners.length; i++) {
/*  341 */       TableSelectionListener l = (TableSelectionListener)listeners[i];
/*  342 */       l.focusChanged(row);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void triggerTableRefreshListeners()
/*      */   {
/*  350 */     Object[] listeners = this.listenersRefresh.toArray();
/*  351 */     for (int i = 0; i < listeners.length; i++) {
/*  352 */       TableRefreshListener l = (TableRefreshListener)listeners[i];
/*  353 */       l.tableRefresh();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addLifeCycleListener(TableLifeCycleListener l)
/*      */   {
/*  359 */     this.listenersLifeCycle.add(l);
/*  360 */     if (!isDisposed()) {
/*  361 */       l.tableViewInitialized();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addRefreshListener(TableRefreshListener l, boolean trigger)
/*      */   {
/*  367 */     this.listenersRefresh.add(l);
/*  368 */     if (trigger) {
/*  369 */       l.tableRefresh();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addCountChangeListener(TableCountChangeListener listener)
/*      */   {
/*  375 */     this.listenersCountChange.add(listener);
/*      */   }
/*      */   
/*      */   public void removeCountChangeListener(TableCountChangeListener listener) {
/*  379 */     this.listenersCountChange.remove(listener);
/*      */   }
/*      */   
/*      */   public void triggerListenerRowAdded(final TableRowCore[] rows) {
/*  383 */     if (this.listenersCountChange.size() == 0) {
/*  384 */       return;
/*      */     }
/*  386 */     getOffUIThread(new AERunnable() {
/*      */       public void runSupport() {
/*  388 */         for (Iterator iter = TableViewImpl.this.listenersCountChange.iterator(); iter.hasNext();) {
/*  389 */           TableCountChangeListener l = (TableCountChangeListener)iter.next();
/*  390 */           for (TableRowCore row : rows) {
/*  391 */             l.rowAdded(row);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   protected void triggerListenerRowRemoved(TableRowCore row) {
/*  399 */     for (Iterator iter = this.listenersCountChange.iterator(); iter.hasNext();) {
/*  400 */       TableCountChangeListener l = (TableCountChangeListener)iter.next();
/*  401 */       l.rowRemoved(row);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void addExpansionChangeListener(TableExpansionChangeListener listener)
/*      */   {
/*  408 */     this.listenersExpansionChange.add(listener);
/*      */   }
/*      */   
/*      */   public void removeExpansionChangeListener(TableExpansionChangeListener listener) {
/*  412 */     this.listenersExpansionChange.remove(listener);
/*      */   }
/*      */   
/*      */   public void invokeExpansionChangeListeners(final TableRowCore row, final boolean expanded) {
/*  416 */     if (this.listenersExpansionChange.size() == 0) {
/*  417 */       return;
/*      */     }
/*  419 */     getOffUIThread(new AERunnable() {
/*      */       public void runSupport() {
/*  421 */         for (Iterator<TableExpansionChangeListener> iter = TableViewImpl.this.listenersExpansionChange.iterator(); iter.hasNext();) {
/*      */           try {
/*  423 */             if (expanded)
/*      */             {
/*  425 */               ((TableExpansionChangeListener)iter.next()).rowExpanded(row);
/*      */             }
/*      */             else
/*      */             {
/*  429 */               ((TableExpansionChangeListener)iter.next()).rowCollapsed(row);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/*  433 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public void addRefreshListener(TableRowRefreshListener listener)
/*      */   {
/*      */     try
/*      */     {
/*  444 */       this.listeners_mon.enter();
/*      */       
/*  446 */       if (this.rowRefreshListeners == null) {
/*  447 */         this.rowRefreshListeners = new ArrayList(1);
/*      */       }
/*      */       
/*  450 */       this.rowRefreshListeners.add(listener);
/*      */     }
/*      */     finally {
/*  453 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeRefreshListener(TableRowRefreshListener listener) {
/*      */     try {
/*  459 */       this.listeners_mon.enter();
/*      */       
/*  461 */       if (this.rowRefreshListeners == null) {
/*      */         return;
/*      */       }
/*      */       
/*  465 */       this.rowRefreshListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  468 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void invokeRefreshListeners(TableRowCore row) {
/*      */     Object[] listeners;
/*      */     try {
/*  475 */       this.listeners_mon.enter();
/*  476 */       if (this.rowRefreshListeners == null) {
/*      */         return;
/*      */       }
/*  479 */       listeners = this.rowRefreshListeners.toArray();
/*      */     }
/*      */     finally {
/*  482 */       this.listeners_mon.exit();
/*      */     }
/*      */     
/*  485 */     for (int i = 0; i < listeners.length; i++) {
/*      */       try {
/*  487 */         TableRowRefreshListener l = (TableRowRefreshListener)listeners[i];
/*      */         
/*  489 */         l.rowRefresh(row);
/*      */       }
/*      */       catch (Throwable e) {
/*  492 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void runForAllRows(TableGroupRowRunner runner)
/*      */   {
/*  500 */     TableRowCore[] rows = getRows();
/*  501 */     if (runner.run(rows)) {
/*  502 */       return;
/*      */     }
/*      */     
/*  505 */     for (int i = 0; i < rows.length; i++) {
/*  506 */       runner.run(rows[i]);
/*      */     }
/*      */   }
/*      */   
/*      */   public void runForAllRows(TableGroupRowVisibilityRunner runner)
/*      */   {
/*  512 */     if (isDisposed()) {
/*  513 */       return;
/*      */     }
/*      */     
/*      */ 
/*  517 */     TableRowCore[] rows = getRows();
/*      */     
/*  519 */     for (int i = 0; i < rows.length; i++) {
/*  520 */       boolean isRowVisible = isRowVisible(rows[i]);
/*  521 */       runner.run(rows[i], isRowVisible);
/*      */       
/*  523 */       int numSubRows = rows[i].getSubItemCount();
/*  524 */       if (numSubRows > 0) {
/*  525 */         TableRowCore[] subRows = rows[i].getSubRowsWithNull();
/*  526 */         for (TableRowCore subRow : subRows) {
/*  527 */           if (subRow != null) {
/*  528 */             runner.run(subRow, isRowVisible(subRow));
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
/*      */   public void runForSelectedRows(TableGroupRowRunner runner)
/*      */   {
/*  542 */     if (isDisposed()) {
/*      */       return;
/*      */     }
/*      */     
/*      */     TableRowCore[] rows;
/*  547 */     synchronized (this.rows_sync) {
/*  548 */       rows = (TableRowCore[])this.selectedRows.toArray(new TableRowCore[0]);
/*      */     }
/*  550 */     boolean ran = runner.run(rows);
/*  551 */     if (!ran) {
/*  552 */       for (int i = 0; i < rows.length; i++) {
/*  553 */         TableRowCore row = rows[i];
/*  554 */         runner.run(row);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public boolean isUnfilteredDataSourceAdded(Object ds)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 1039	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:rows_sync	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_2
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 1045	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:listUnfilteredDataSources	Ljava/util/IdentityHashMap;
/*      */     //   11: aload_1
/*      */     //   12: invokevirtual 1183	java/util/IdentityHashMap:containsKey	(Ljava/lang/Object;)Z
/*      */     //   15: aload_2
/*      */     //   16: monitorexit
/*      */     //   17: ireturn
/*      */     //   18: astore_3
/*      */     //   19: aload_2
/*      */     //   20: monitorexit
/*      */     //   21: aload_3
/*      */     //   22: athrow
/*      */     // Line number table:
/*      */     //   Java source line #560	-> byte code offset #0
/*      */     //   Java source line #561	-> byte code offset #7
/*      */     //   Java source line #562	-> byte code offset #18
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	23	0	this	TableViewImpl<DATASOURCETYPE>
/*      */     //   0	23	1	ds	Object
/*      */     //   5	15	2	Ljava/lang/Object;	Object
/*      */     //   18	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	17	18	finally
/*      */     //   18	21	18	finally
/*      */   }
/*      */   
/*      */   public void refilter()
/*      */   {
/*  567 */     if (this.filter == null) {
/*  568 */       return;
/*      */     }
/*  570 */     if (this.filter.eventUpdate != null) {
/*  571 */       this.filter.eventUpdate.cancel();
/*  572 */       this.filter.text = this.filter.nextText;
/*  573 */       this.filter.checker.filterSet(this.filter.text);
/*      */     }
/*  575 */     this.filter.eventUpdate = null;
/*      */     
/*  577 */     synchronized (this.rows_sync) {
/*  578 */       DATASOURCETYPE[] unfilteredArray = (Object[])this.listUnfilteredDataSources.keySet().toArray();
/*  579 */       if (DEBUGADDREMOVE) {
/*  580 */         debug("filter: unfilteredArray is " + unfilteredArray.length);
/*      */       }
/*      */       
/*  583 */       Set<DATASOURCETYPE> existing = new java.util.HashSet(getDataSources());
/*      */       
/*  585 */       List<DATASOURCETYPE> listRemoves = new ArrayList();
/*  586 */       List<DATASOURCETYPE> listAdds = new ArrayList();
/*      */       
/*  588 */       for (int i = 0; i < unfilteredArray.length; i++) {
/*  589 */         boolean bHave = existing.contains(unfilteredArray[i]);
/*  590 */         boolean isOurs = this.filter.checker.filterCheck(unfilteredArray[i], this.filter.text, this.filter.regex);
/*      */         
/*  592 */         if (!isOurs) {
/*  593 */           if (bHave) {
/*  594 */             listRemoves.add(unfilteredArray[i]);
/*      */           }
/*      */         }
/*  597 */         else if (!bHave) {
/*  598 */           listAdds.add(unfilteredArray[i]);
/*      */         }
/*      */       }
/*      */       
/*  602 */       if (listRemoves.size() > 0) {
/*  603 */         removeDataSources((Object[])listRemoves.toArray());
/*      */       }
/*  605 */       if (listAdds.size() > 0) {
/*  606 */         addDataSources((Object[])listAdds.toArray(), true);
/*      */       }
/*      */       
/*      */ 
/*  610 */       for (DATASOURCETYPE ds : listRemoves) {
/*  611 */         this.listUnfilteredDataSources.put(ds, "");
/*      */       }
/*      */     }
/*  614 */     processDataSourceQueue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isFiltered(DATASOURCETYPE ds)
/*      */   {
/*  621 */     if (this.filter == null) {
/*  622 */       return true;
/*      */     }
/*      */     
/*  625 */     return this.filter.checker.filterCheck(ds, this.filter.text, this.filter.regex);
/*      */   }
/*      */   
/*      */   protected void debug(String s) {
/*  629 */     AEDiagnosticsLogger diag_logger = AEDiagnostics.getLogger("table");
/*  630 */     diag_logger.log(SystemTime.getCurrentTime() + ":" + getTableID() + ": " + s);
/*      */     
/*  632 */     System.out.println(Thread.currentThread().getName() + "." + Integer.toHexString(hashCode()) + "] " + SystemTime.getCurrentTime() + ": " + getTableID() + ": " + s);
/*      */   }
/*      */   
/*      */ 
/*      */   private void _processDataSourceQueue()
/*      */   {
/*  638 */     Object[] dataSourcesAdd = null;
/*  639 */     Object[] dataSourcesRemove = null;
/*      */     
/*  641 */     synchronized (this.rows_sync) {
/*  642 */       if (this.dataSourcesToAdd.size() > 0) {
/*  643 */         boolean removed_something = false;
/*  644 */         for (DATASOURCETYPE ds : this.dataSourcesToRemove.keySet())
/*      */         {
/*  646 */           if (this.dataSourcesToAdd.remove(ds) != null)
/*      */           {
/*  648 */             removed_something = true;
/*      */           }
/*      */         }
/*      */         
/*  652 */         if ((removed_something) && (DEBUGADDREMOVE)) {
/*  653 */           debug("Saved time by not adding a row that was removed");
/*      */         }
/*      */         
/*  656 */         dataSourcesAdd = this.dataSourcesToAdd.keySet().toArray();
/*      */         
/*  658 */         this.dataSourcesToAdd.clear();
/*      */       }
/*      */       
/*  661 */       if (this.dataSourcesToRemove.size() > 0) {
/*  662 */         dataSourcesRemove = this.dataSourcesToRemove.keySet().toArray();
/*  663 */         if ((DEBUGADDREMOVE) && (dataSourcesRemove.length > 1)) {
/*  664 */           debug("Streamlining removing " + dataSourcesRemove.length + " rows");
/*      */         }
/*  666 */         this.dataSourcesToRemove.clear();
/*      */       }
/*      */     }
/*      */     
/*  670 */     boolean hasAdd = (dataSourcesAdd != null) && (dataSourcesAdd.length > 0);
/*  671 */     if (hasAdd) {
/*  672 */       reallyAddDataSources(dataSourcesAdd);
/*  673 */       if ((DEBUGADDREMOVE) && (dataSourcesAdd.length > 1)) {
/*  674 */         debug("Streamlined adding " + dataSourcesAdd.length + " rows");
/*      */       }
/*      */     }
/*      */     
/*  678 */     boolean hasRemove = (dataSourcesRemove != null) && (dataSourcesRemove.length > 0);
/*  679 */     if (hasRemove) {
/*  680 */       reallyRemoveDataSources(dataSourcesRemove);
/*      */     }
/*      */     
/*  683 */     if ((hasAdd) || (hasRemove)) {
/*  684 */       tableMutated();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addDataSource(DATASOURCETYPE dataSource) {
/*  689 */     addDataSource(dataSource, false);
/*      */   }
/*      */   
/*      */   private void addDataSource(DATASOURCETYPE dataSource, boolean skipFilterCheck)
/*      */   {
/*  694 */     if (dataSource == null) {
/*  695 */       return;
/*      */     }
/*      */     
/*  698 */     synchronized (this.rows_sync) {
/*  699 */       this.listUnfilteredDataSources.put(dataSource, "");
/*      */     }
/*  701 */     if (DEBUGADDREMOVE) {
/*  702 */       debug("AddDS: " + dataSource + "; listUnfilteredDS: " + this.listUnfilteredDataSources.size() + " via " + Debug.getCompressedStackTrace(4));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  707 */     if ((!skipFilterCheck) && (this.filter != null) && (!this.filter.checker.filterCheck(dataSource, this.filter.text, this.filter.regex)))
/*      */     {
/*  709 */       return;
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
/*  723 */     synchronized (this.rows_sync) {
/*  724 */       if (this.dataSourcesToRemove.remove(dataSource) != null)
/*      */       {
/*  726 */         if (DEBUGADDREMOVE) {
/*  727 */           debug("AddDS: Removed from toRemove.  Total Removals Queued: " + this.dataSourcesToRemove.size());
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  732 */       if (this.dataSourcesToAdd.containsKey(dataSource))
/*      */       {
/*  734 */         if (DEBUGADDREMOVE) {
/*  735 */           debug("AddDS: Already There.  Total Additions Queued: " + this.dataSourcesToAdd.size());
/*      */         }
/*      */       }
/*      */       else {
/*  739 */         this.dataSourcesToAdd.put(dataSource, "");
/*  740 */         if (DEBUGADDREMOVE) {
/*  741 */           debug("Queued 1 dataSource to add.  Total Additions Queued: " + this.dataSourcesToAdd.size() + "; already=" + this.sortedRows.size());
/*      */         }
/*      */         
/*  744 */         refreshenProcessDataSourcesTimer();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void addDataSources(DATASOURCETYPE[] dataSources)
/*      */   {
/*  751 */     addDataSources(dataSources, false);
/*      */   }
/*      */   
/*      */ 
/*      */   public void addDataSources(DATASOURCETYPE[] dataSources, boolean skipFilterCheck)
/*      */   {
/*  757 */     if (dataSources == null) {
/*  758 */       return;
/*      */     }
/*      */     
/*  761 */     if (DEBUGADDREMOVE) {
/*  762 */       debug("AddDS: " + dataSources.length);
/*      */     }
/*      */     
/*  765 */     synchronized (this.rows_sync) {
/*  766 */       for (DATASOURCETYPE ds : dataSources) {
/*  767 */         if (ds != null)
/*      */         {
/*      */ 
/*  770 */           this.listUnfilteredDataSources.put(ds, null);
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
/*  791 */     synchronized (this.rows_sync) {
/*  792 */       int count = 0;
/*      */       
/*  794 */       for (int i = 0; i < dataSources.length; i++) {
/*  795 */         DATASOURCETYPE dataSource = dataSources[i];
/*  796 */         if (dataSource != null)
/*      */         {
/*      */ 
/*  799 */           if ((skipFilterCheck) || (this.filter == null) || (this.filter.checker.filterCheck(dataSource, this.filter.text, this.filter.regex)))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  805 */             this.dataSourcesToRemove.remove(dataSource);
/*      */             
/*  807 */             if (!this.dataSourcesToAdd.containsKey(dataSource))
/*      */             {
/*  809 */               count++;
/*  810 */               this.dataSourcesToAdd.put(dataSource, "");
/*      */             }
/*      */           } }
/*      */       }
/*  814 */       if (DEBUGADDREMOVE) {
/*  815 */         debug("Queued " + count + " of " + dataSources.length + " dataSources to add.  Total Qd: " + this.dataSourcesToAdd.size() + ";Unfiltered: " + this.listUnfilteredDataSources.size() + "; skipFilterCheck? " + skipFilterCheck + "; via " + Debug.getCompressedStackTrace(5));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  824 */     refreshenProcessDataSourcesTimer();
/*      */   }
/*      */   
/*      */   public boolean dataSourceExists(DATASOURCETYPE dataSource)
/*      */   {
/*  829 */     synchronized (this.rows_sync) {
/*  830 */       return (this.mapDataSourceToRow.containsKey(dataSource)) || (this.dataSourcesToAdd.containsKey(dataSource));
/*      */     }
/*      */   }
/*      */   
/*      */   public void processDataSourceQueue() {
/*  835 */     getOffUIThread(new AERunnable() {
/*      */       public void runSupport() {
/*  837 */         TableViewImpl.this._processDataSourceQueue();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public abstract void getOffUIThread(AERunnable paramAERunnable);
/*      */   
/*      */   public void processDataSourceQueueSync() {
/*  845 */     _processDataSourceQueue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int size(boolean bIncludeQueue)
/*      */   {
/*  852 */     synchronized (this.rows_sync) {
/*  853 */       int size = this.sortedRows.size();
/*      */       
/*  855 */       if (bIncludeQueue) {
/*  856 */         if (this.dataSourcesToAdd != null) {
/*  857 */           size += this.dataSourcesToAdd.size();
/*      */         }
/*  859 */         if (this.dataSourcesToRemove != null) {
/*  860 */           size -= this.dataSourcesToRemove.size();
/*      */         }
/*      */       }
/*  863 */       return size;
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public TableRowCore[] getRows()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 1039	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:rows_sync	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 1049	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:sortedRows	Ljava/util/List;
/*      */     //   11: iconst_0
/*      */     //   12: anewarray 583	com/aelitis/azureus/ui/common/table/TableRowCore
/*      */     //   15: invokeinterface 1254 2 0
/*      */     //   20: checkcast 571	[Lcom/aelitis/azureus/ui/common/table/TableRowCore;
/*      */     //   23: aload_1
/*      */     //   24: monitorexit
/*      */     //   25: areturn
/*      */     //   26: astore_2
/*      */     //   27: aload_1
/*      */     //   28: monitorexit
/*      */     //   29: aload_2
/*      */     //   30: athrow
/*      */     // Line number table:
/*      */     //   Java source line #869	-> byte code offset #0
/*      */     //   Java source line #870	-> byte code offset #7
/*      */     //   Java source line #871	-> byte code offset #26
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	31	0	this	TableViewImpl<DATASOURCETYPE>
/*      */     //   5	23	1	Ljava/lang/Object;	Object
/*      */     //   26	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	25	26	finally
/*      */     //   26	29	26	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public TableRowCore getRow(DATASOURCETYPE dataSource)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 1039	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:rows_sync	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_2
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 1046	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:mapDataSourceToRow	Ljava/util/IdentityHashMap;
/*      */     //   11: aload_1
/*      */     //   12: invokevirtual 1186	java/util/IdentityHashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   15: checkcast 583	com/aelitis/azureus/ui/common/table/TableRowCore
/*      */     //   18: aload_2
/*      */     //   19: monitorexit
/*      */     //   20: areturn
/*      */     //   21: astore_3
/*      */     //   22: aload_2
/*      */     //   23: monitorexit
/*      */     //   24: aload_3
/*      */     //   25: athrow
/*      */     // Line number table:
/*      */     //   Java source line #876	-> byte code offset #0
/*      */     //   Java source line #877	-> byte code offset #7
/*      */     //   Java source line #878	-> byte code offset #21
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	26	0	this	TableViewImpl<DATASOURCETYPE>
/*      */     //   0	26	1	dataSource	DATASOURCETYPE
/*      */     //   5	18	2	Ljava/lang/Object;	Object
/*      */     //   21	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	20	21	finally
/*      */     //   21	24	21	finally
/*      */   }
/*      */   
/*      */   public TableRowCore getRow(int iPos)
/*      */   {
/*  883 */     synchronized (this.rows_sync) {
/*  884 */       if ((iPos >= 0) && (iPos < this.sortedRows.size())) {
/*  885 */         TableRowCore row = (TableRowCore)this.sortedRows.get(iPos);
/*      */         
/*  887 */         if (row.getIndex() != iPos) {
/*  888 */           row.setTableItem(iPos);
/*      */         }
/*  890 */         return row;
/*      */       }
/*      */     }
/*  893 */     return null;
/*      */   }
/*      */   
/*      */   public TableRowCore getRowQuick(int iPos) {
/*      */     try {
/*  898 */       return (TableRowCore)this.sortedRows.get(iPos);
/*      */     } catch (Exception e) {}
/*  900 */     return null;
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public int indexOf(TableRowCore row)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 1039	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:rows_sync	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_2
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 1049	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:sortedRows	Ljava/util/List;
/*      */     //   11: aload_1
/*      */     //   12: invokeinterface 1248 2 0
/*      */     //   17: aload_2
/*      */     //   18: monitorexit
/*      */     //   19: ireturn
/*      */     //   20: astore_3
/*      */     //   21: aload_2
/*      */     //   22: monitorexit
/*      */     //   23: aload_3
/*      */     //   24: athrow
/*      */     // Line number table:
/*      */     //   Java source line #905	-> byte code offset #0
/*      */     //   Java source line #906	-> byte code offset #7
/*      */     //   Java source line #907	-> byte code offset #20
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	25	0	this	TableViewImpl<DATASOURCETYPE>
/*      */     //   0	25	1	row	TableRowCore
/*      */     //   5	17	2	Ljava/lang/Object;	Object
/*      */     //   20	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	19	20	finally
/*      */     //   20	23	20	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public int getRowCount()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 1039	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:rows_sync	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 1046	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:mapDataSourceToRow	Ljava/util/IdentityHashMap;
/*      */     //   11: invokevirtual 1179	java/util/IdentityHashMap:size	()I
/*      */     //   14: aload_1
/*      */     //   15: monitorexit
/*      */     //   16: ireturn
/*      */     //   17: astore_2
/*      */     //   18: aload_1
/*      */     //   19: monitorexit
/*      */     //   20: aload_2
/*      */     //   21: athrow
/*      */     // Line number table:
/*      */     //   Java source line #912	-> byte code offset #0
/*      */     //   Java source line #913	-> byte code offset #7
/*      */     //   Java source line #914	-> byte code offset #17
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	22	0	this	TableViewImpl<DATASOURCETYPE>
/*      */     //   5	14	1	Ljava/lang/Object;	Object
/*      */     //   17	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	16	17	finally
/*      */     //   17	20	17	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public ArrayList<DATASOURCETYPE> getDataSources()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 1039	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:rows_sync	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: new 614	java/util/ArrayList
/*      */     //   10: dup
/*      */     //   11: aload_0
/*      */     //   12: getfield 1046	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:mapDataSourceToRow	Ljava/util/IdentityHashMap;
/*      */     //   15: invokevirtual 1185	java/util/IdentityHashMap:keySet	()Ljava/util/Set;
/*      */     //   18: invokespecial 1172	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
/*      */     //   21: aload_1
/*      */     //   22: monitorexit
/*      */     //   23: areturn
/*      */     //   24: astore_2
/*      */     //   25: aload_1
/*      */     //   26: monitorexit
/*      */     //   27: aload_2
/*      */     //   28: athrow
/*      */     // Line number table:
/*      */     //   Java source line #919	-> byte code offset #0
/*      */     //   Java source line #920	-> byte code offset #7
/*      */     //   Java source line #921	-> byte code offset #24
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	29	0	this	TableViewImpl<DATASOURCETYPE>
/*      */     //   5	21	1	Ljava/lang/Object;	Object
/*      */     //   24	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	23	24	finally
/*      */     //   24	27	24	finally
/*      */   }
/*      */   
/*      */   public ArrayList<DATASOURCETYPE> getDataSources(boolean include_filtered)
/*      */   {
/*  926 */     synchronized (this.rows_sync) {
/*  927 */       if (include_filtered) {
/*  928 */         return new ArrayList(this.listUnfilteredDataSources.keySet());
/*      */       }
/*      */       
/*  931 */       return new ArrayList(this.mapDataSourceToRow.keySet());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeDataSource(DATASOURCETYPE dataSource)
/*      */   {
/*  938 */     if (dataSource == null) {
/*  939 */       return;
/*      */     }
/*      */     
/*  942 */     synchronized (this.rows_sync) {
/*  943 */       this.listUnfilteredDataSources.remove(dataSource);
/*      */     }
/*      */     
/*      */ 
/*  947 */     if (DEBUGADDREMOVE) {
/*  948 */       debug("RemDS: " + dataSource + "; listUnfilteredDS=" + this.listUnfilteredDataSources.size());
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
/*  959 */     synchronized (this.rows_sync) {
/*  960 */       this.dataSourcesToAdd.remove(dataSource);
/*  961 */       this.dataSourcesToRemove.put(dataSource, "");
/*      */       
/*  963 */       if (DEBUGADDREMOVE) {
/*  964 */         debug("Queued 1 dataSource to remove.  Total Queued: " + this.dataSourcesToRemove.size());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  969 */     refreshenProcessDataSourcesTimer();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeDataSources(DATASOURCETYPE[] dataSources)
/*      */   {
/*  978 */     if ((dataSources == null) || (dataSources.length == 0)) {
/*  979 */       return;
/*      */     }
/*      */     
/*  982 */     if (DEBUGADDREMOVE) {
/*  983 */       debug("RemDS: " + dataSources.length);
/*      */     }
/*      */     
/*  986 */     synchronized (this.rows_sync) {
/*  987 */       for (DATASOURCETYPE ds : dataSources) {
/*  988 */         this.listUnfilteredDataSources.remove(ds);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  998 */     synchronized (this.rows_sync) {
/*  999 */       for (int i = 0; i < dataSources.length; i++) {
/* 1000 */         DATASOURCETYPE dataSource = dataSources[i];
/* 1001 */         this.dataSourcesToAdd.remove(dataSource);
/* 1002 */         this.dataSourcesToRemove.put(dataSource, "");
/*      */       }
/*      */       
/* 1005 */       if (DEBUGADDREMOVE) {
/* 1006 */         debug("Queued " + dataSources.length + " dataSources to remove.  Total Qd: " + this.dataSourcesToRemove.size() + "; Unfiltered: " + this.listUnfilteredDataSources.size() + " via " + Debug.getCompressedStackTrace(4));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1013 */     refreshenProcessDataSourcesTimer();
/*      */   }
/*      */   
/*      */   private void refreshenProcessDataSourcesTimer() {
/* 1017 */     if ((this.bReallyAddingDataSources) || (this.processDataSourceQueueCallback == null))
/*      */     {
/* 1019 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1026 */     boolean processQueueImmediately = DataSourceCallBackUtil.addDataSourceAggregated(this.processDataSourceQueueCallback);
/*      */     
/* 1028 */     if (processQueueImmediately) {
/* 1029 */       processDataSourceQueue();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void reallyAddDataSources(Object[] dataSources)
/*      */   {
/* 1037 */     if (isDisposed()) {
/* 1038 */       return;
/*      */     }
/*      */     
/* 1041 */     this.bReallyAddingDataSources = true;
/* 1042 */     if (DEBUGADDREMOVE) {
/* 1043 */       debug(">> Add " + dataSources.length + " rows;");
/*      */     }
/*      */     
/*      */ 
/* 1047 */     synchronized (this.rows_sync)
/*      */     {
/*      */ 
/*      */       try
/*      */       {
/* 1052 */         for (int i = 0; i < dataSources.length; i++) {
/* 1053 */           Object ds = dataSources[i];
/* 1054 */           if (ds == null) {
/* 1055 */             if (DEBUGADDREMOVE) {
/* 1056 */               debug("-- Null DS for " + i);
/*      */             }
/*      */             
/*      */ 
/*      */           }
/* 1061 */           else if (this.mapDataSourceToRow.containsKey(ds)) {
/* 1062 */             if (DEBUGADDREMOVE) {
/* 1063 */               debug("-- " + i + " already added: " + ds.getClass());
/*      */             }
/* 1065 */             dataSources[i] = null;
/*      */           } else {
/* 1067 */             TableRowCore rowCore = createNewRow(ds);
/* 1068 */             this.mapDataSourceToRow.put(ds, rowCore);
/*      */           }
/*      */         }
/*      */       } catch (Exception e) {
/* 1072 */         Logger.log(new LogEvent(LOGID, "Error while added row to Table " + getTableID(), e));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1077 */     if (DEBUGADDREMOVE) {
/* 1078 */       debug("-- Add " + dataSources.length + " rows;");
/*      */     }
/*      */     
/* 1081 */     addSortedDataSource(dataSources);
/*      */     
/* 1083 */     this.bReallyAddingDataSources = false;
/*      */   }
/*      */   
/*      */   public abstract TableRowCore createNewRow(Object paramObject);
/*      */   
/*      */   public void delete() {
/* 1089 */     this.processDataSourceQueueCallback = null;
/*      */   }
/*      */   
/*      */   public Object getRowsSync() {
/* 1093 */     return this.rows_sync;
/*      */   }
/*      */   
/*      */   public void setRowsSync(Object o) {
/* 1097 */     this.rows_sync = o;
/*      */   }
/*      */   
/*      */   public void generate(IndentWriter writer) {
/* 1101 */     writer.println("Diagnostics for " + this + " (" + getTableID() + ")");
/*      */     
/* 1103 */     synchronized (this.rows_sync) {
/* 1104 */       writer.println("DataSources scheduled to Add/Remove: " + this.dataSourcesToAdd.size() + "/" + this.dataSourcesToRemove.size());
/*      */       
/*      */ 
/* 1107 */       writer.println("TableView: " + this.mapDataSourceToRow.size() + " datasources");
/* 1108 */       Iterator<DATASOURCETYPE> it = this.mapDataSourceToRow.keySet().iterator();
/*      */       
/* 1110 */       while (it.hasNext())
/*      */       {
/* 1112 */         Object key = it.next();
/*      */         
/* 1114 */         writer.println("  " + key + " -> " + this.mapDataSourceToRow.get(key));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeAllTableRows()
/*      */   {
/*      */     ArrayList<TableRowCore> itemsToRemove;
/*      */     
/* 1124 */     synchronized (this.rows_sync)
/*      */     {
/* 1126 */       itemsToRemove = new ArrayList(this.mapDataSourceToRow.values());
/* 1127 */       this.mapDataSourceToRow.clear();
/* 1128 */       this.sortedRows.clear();
/*      */       
/* 1130 */       this.dataSourcesToAdd.clear();
/* 1131 */       this.dataSourcesToRemove.clear();
/*      */       
/* 1133 */       this.listUnfilteredDataSources.clear();
/*      */       
/* 1135 */       this.selectedRows.clear();
/* 1136 */       this.listSelectedCoreDataSources = null;
/*      */       
/* 1138 */       if (DEBUGADDREMOVE) {
/* 1139 */         debug("removeAll");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1147 */     for (TableRowCore row : itemsToRemove)
/*      */     {
/* 1149 */       row.delete();
/*      */     }
/*      */   }
/*      */   
/*      */   public void reallyRemoveDataSources(Object[] dataSources)
/*      */   {
/* 1155 */     long lStart = SystemTime.getCurrentTime();
/*      */     
/* 1157 */     int rows_removed = 0;
/*      */     
/* 1159 */     StringBuffer sbWillRemove = null;
/* 1160 */     if (DEBUGADDREMOVE) {
/* 1161 */       debug(">>> Remove rows.  Start w/" + getRowCount() + "ds;" + (SystemTime.getCurrentTime() - lStart) + "ms wait");
/*      */       
/*      */ 
/*      */ 
/* 1165 */       sbWillRemove = new StringBuffer("Will soon remove row #");
/*      */     }
/*      */     
/* 1168 */     ArrayList<TableRowCore> itemsToRemove = new ArrayList();
/* 1169 */     ArrayList<Integer> indexesToRemove = new ArrayList();
/*      */     
/* 1171 */     int numRemovedHavingSelection = 0;
/* 1172 */     synchronized (this.rows_sync) {
/* 1173 */       for (int i = 0; i < dataSources.length; i++)
/* 1174 */         if (dataSources[i] != null)
/*      */         {
/*      */ 
/*      */ 
/* 1178 */           TableRowCore item = (TableRowCore)this.mapDataSourceToRow.get(dataSources[i]);
/* 1179 */           if (item != null) {
/* 1180 */             if (isProvideIndexesOnRemove())
/*      */             {
/*      */ 
/*      */ 
/* 1184 */               int index = this.sortedRows.indexOf(item);
/* 1185 */               indexesToRemove.add(Integer.valueOf(index));
/* 1186 */               if (DEBUGADDREMOVE) {
/* 1187 */                 if (i != 0) {
/* 1188 */                   sbWillRemove.append(", ");
/*      */                 }
/* 1190 */                 sbWillRemove.append(index);
/*      */               }
/*      */             }
/*      */             
/* 1194 */             if (item.isSelected()) {
/* 1195 */               numRemovedHavingSelection++;
/*      */             }
/* 1197 */             itemsToRemove.add(item);
/* 1198 */             this.mapDataSourceToRow.remove(dataSources[i]);
/* 1199 */             triggerListenerRowRemoved(item);
/* 1200 */             this.sortedRows.remove(item);
/* 1201 */             this.selectedRows.remove(item);
/*      */             
/* 1203 */             rows_removed++;
/*      */           }
/*      */         }
/* 1206 */       if (rows_removed > 0) {
/* 1207 */         this.listSelectedCoreDataSources = null;
/*      */       }
/*      */     }
/*      */     
/* 1211 */     if (DEBUGADDREMOVE) {
/* 1212 */       debug(sbWillRemove.toString());
/* 1213 */       debug("#itemsToRemove=" + itemsToRemove.size());
/*      */     }
/*      */     
/* 1216 */     uiRemoveRows((TableRowCore[])itemsToRemove.toArray(new TableRowCore[0]), (Integer[])indexesToRemove.toArray(new Integer[0]));
/*      */     
/*      */ 
/*      */ 
/* 1220 */     for (Iterator<TableRowCore> iter = itemsToRemove.iterator(); iter.hasNext();) {
/* 1221 */       TableRowCore row = (TableRowCore)iter.next();
/* 1222 */       row.delete();
/*      */     }
/*      */     
/* 1225 */     if (DEBUGADDREMOVE) {
/* 1226 */       debug("<< Remove " + itemsToRemove.size() + " rows. now " + this.mapDataSourceToRow.size() + "ds");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void tableMutated()
/*      */   {
/* 1235 */     filter f = this.filter;
/*      */     
/* 1237 */     if (f != null) {
/* 1238 */       TableViewFilterCheck<DATASOURCETYPE> checker = f.checker;
/*      */       
/* 1240 */       if ((checker instanceof TableViewFilterCheck.TableViewFilterCheckEx))
/*      */       {
/* 1242 */         ((TableViewFilterCheck.TableViewFilterCheckEx)checker).viewChanged(this);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void fillRowGaps(boolean bForceDataRefresh)
/*      */   {
/* 1249 */     _sortColumn(bForceDataRefresh, true, false);
/*      */   }
/*      */   
/*      */   public void sortColumn(boolean bForceDataRefresh) {
/* 1253 */     _sortColumn(bForceDataRefresh, false, false);
/*      */   }
/*      */   
/*      */   protected void _sortColumn(boolean bForceDataRefresh, boolean bFillGapsOnly, boolean bFollowSelected)
/*      */   {
/* 1258 */     if (isDisposed()) {
/* 1259 */       return;
/*      */     }
/*      */     
/* 1262 */     if (this.sortColumn != null)
/*      */     {
/* 1264 */       if (!this.sortColumn.isVisible())
/*      */       {
/* 1266 */         this.sortColumn = null;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1271 */     synchronized (this.rows_sync)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1279 */       int iNumMoves = 0;
/*      */       
/*      */ 
/* 1282 */       boolean needsUpdate = false;
/*      */       
/* 1284 */       synchronized (this.rows_sync) { String sColumnID;
/* 1285 */         Iterator<TableRowCore> iter; if ((bForceDataRefresh) && (this.sortColumn != null)) {
/* 1286 */           sColumnID = this.sortColumn.getName();
/* 1287 */           for (iter = this.sortedRows.iterator(); iter.hasNext();) {
/* 1288 */             TableRowCore row = (TableRowCore)iter.next();
/* 1289 */             TableCellCore cell = row.getSortColumnCell(sColumnID);
/* 1290 */             if (cell != null) {
/* 1291 */               cell.refresh(true);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1296 */         if ((!bFillGapsOnly) && 
/* 1297 */           (this.sortColumn != null) && (this.sortColumn.getLastSortValueChange() >= this.lLastSortedOn))
/*      */         {
/* 1299 */           this.lLastSortedOn = SystemTime.getCurrentTime();
/* 1300 */           Collections.sort(this.sortedRows, this.sortColumn);
/*      */         }
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
/* 1314 */         for (int i = 0; i < this.sortedRows.size(); i++) {
/* 1315 */           TableRowCore row = (TableRowCore)this.sortedRows.get(i);
/* 1316 */           boolean visible = row.isVisible();
/* 1317 */           if (row.setTableItem(i, visible)) {
/* 1318 */             if (visible) {
/* 1319 */               needsUpdate = true;
/*      */             }
/* 1321 */             iNumMoves++;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1330 */       if (needsUpdate) {
/* 1331 */         visibleRowsChanged();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public abstract void visibleRowsChanged();
/*      */   
/*      */ 
/*      */ 
/*      */   public abstract void uiRemoveRows(TableRowCore[] paramArrayOfTableRowCore, Integer[] paramArrayOfInteger);
/*      */   
/*      */ 
/*      */ 
/*      */   public abstract int uiGuessMaxVisibleRows();
/*      */   
/*      */ 
/*      */ 
/*      */   public void resetLastSortedOn()
/*      */   {
/* 1352 */     synchronized (this.rows_sync) {
/* 1353 */       this.lLastSortedOn = 0L;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public TableCellCore[] getColumnCells(String sColumnName)
/*      */   {
/* 1360 */     synchronized (this.rows_sync) {
/* 1361 */       TableCellCore[] cells = new TableCellCore[this.sortedRows.size()];
/*      */       
/* 1363 */       int i = 0;
/* 1364 */       for (Iterator<TableRowCore> iter = this.sortedRows.iterator(); iter.hasNext();) {
/* 1365 */         TableRowCore row = (TableRowCore)iter.next();
/* 1366 */         cells[(i++)] = row.getTableCellCore(sColumnName);
/*      */       }
/*      */       
/* 1369 */       return cells;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void addSortedDataSource(Object[] dataSources)
/*      */   {
/* 1376 */     if (isDisposed()) {
/* 1377 */       return;
/*      */     }
/*      */     
/* 1380 */     TableRowCore[] selectedRows = getSelectedRows();
/*      */     
/* 1382 */     boolean bWas0Rows = getRowCount() == 0;
/*      */     try
/*      */     {
/* 1385 */       if (DEBUGADDREMOVE) {
/* 1386 */         debug("-- Add " + dataSources.length + " rows to SWT");
/*      */       }
/*      */       
/* 1389 */       long lStartTime = SystemTime.getCurrentTime();
/*      */       
/* 1391 */       List<TableRowCore> rowsAdded = new ArrayList();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1396 */       for (int i = 0; i < dataSources.length; i++) {
/* 1397 */         Object dataSource = dataSources[i];
/* 1398 */         if (dataSource != null)
/*      */         {
/*      */           TableRowCore row;
/*      */           
/*      */ 
/* 1403 */           synchronized (this.rows_sync) {
/* 1404 */             row = (TableRowCore)this.mapDataSourceToRow.get(dataSource);
/*      */           }
/*      */           
/* 1407 */           if ((row != null) && (!row.isRowDisposed()))
/*      */           {
/*      */ 
/* 1410 */             if (this.sortColumn != null) {
/* 1411 */               TableCellCore cell = row.getSortColumnCell(null);
/* 1412 */               if (cell != null) {
/*      */                 try {
/* 1414 */                   cell.invalidate();
/*      */                   
/*      */ 
/* 1417 */                   cell.refresh(true);
/*      */                 } catch (Exception e) {
/* 1419 */                   Logger.log(new LogEvent(LOGID, "Minor error adding a row to table " + getTableID(), e));
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */ 
/* 1425 */             synchronized (this.rows_sync) {
/*      */               try {
/* 1427 */                 int index = 0;
/* 1428 */                 if (this.sortedRows.size() > 0)
/*      */                 {
/*      */ 
/*      */ 
/* 1432 */                   TableRowCore lastRow = (TableRowCore)this.sortedRows.get(this.sortedRows.size() - 1);
/* 1433 */                   if ((this.sortColumn == null) || (this.sortColumn.compare(row, lastRow) >= 0)) {
/* 1434 */                     index = this.sortedRows.size();
/* 1435 */                     this.sortedRows.add(row);
/* 1436 */                     if (DEBUGADDREMOVE) {
/* 1437 */                       debug("Adding new row to bottom");
/*      */                     }
/*      */                   } else {
/* 1440 */                     index = Collections.binarySearch(this.sortedRows, row, this.sortColumn);
/* 1441 */                     if (index < 0) {
/* 1442 */                       index = -1 * index - 1;
/*      */                     }
/*      */                     
/* 1445 */                     if (index > this.sortedRows.size()) {
/* 1446 */                       index = this.sortedRows.size();
/*      */                     }
/*      */                     
/* 1449 */                     if (DEBUGADDREMOVE) {
/* 1450 */                       debug("Adding new row at position " + index + " of " + (this.sortedRows.size() - 1));
/*      */                     }
/*      */                     
/* 1453 */                     this.sortedRows.add(index, row);
/*      */                   }
/*      */                 } else {
/* 1456 */                   if (DEBUGADDREMOVE) {
/* 1457 */                     debug("Adding new row to bottom (1st Entry)");
/*      */                   }
/* 1459 */                   index = this.sortedRows.size();
/* 1460 */                   this.sortedRows.add(row);
/*      */                 }
/*      */                 
/* 1463 */                 rowsAdded.add(row);
/*      */ 
/*      */ 
/*      */ 
/*      */               }
/*      */               catch (Exception e)
/*      */               {
/*      */ 
/*      */ 
/* 1472 */                 e.printStackTrace();
/* 1473 */                 Logger.log(new LogEvent(LOGID, "Error adding a row to table " + getTableID(), e));
/*      */                 try
/*      */                 {
/* 1476 */                   if (!this.sortedRows.contains(row)) {
/* 1477 */                     this.sortedRows.add(row);
/*      */                   }
/*      */                 } catch (Exception e2) {
/* 1480 */                   Debug.out(e2);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1489 */       triggerListenerRowAdded((TableRowCore[])rowsAdded.toArray(new TableRowCore[0]));
/*      */       
/*      */ 
/* 1492 */       if (DEBUGADDREMOVE) {
/* 1493 */         debug("Adding took " + (SystemTime.getCurrentTime() - lStartTime) + "ms");
/*      */       }
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 1498 */       Logger.log(new LogEvent(LOGID, "Error while adding row to Table " + getTableID(), e));
/*      */     }
/*      */     
/* 1501 */     refreshenProcessDataSourcesTimer();
/*      */     
/* 1503 */     visibleRowsChanged();
/* 1504 */     fillRowGaps(false);
/*      */     
/* 1506 */     if (selectedRows.length > 0) {
/* 1507 */       setSelectedRows(selectedRows);
/*      */     }
/* 1509 */     if (DEBUGADDREMOVE) {
/* 1510 */       debug("<< " + size(false));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void cellInvalidate(TableColumnCore tableColumn, DATASOURCETYPE data_source)
/*      */   {
/* 1518 */     cellInvalidate(tableColumn, data_source, true);
/*      */   }
/*      */   
/*      */   public void cellInvalidate(TableColumnCore tableColumn, final DATASOURCETYPE data_source, final boolean bMustRefresh)
/*      */   {
/* 1523 */     final String sColumnName = tableColumn.getName();
/*      */     
/* 1525 */     runForAllRows(new TableGroupRowRunner() {
/*      */       public void run(TableRowCore row) {
/* 1527 */         TableCellCore cell = row.getTableCellCore(sColumnName);
/* 1528 */         if ((cell != null) && (cell.getDataSource() != null) && (cell.getDataSource().equals(data_source)))
/*      */         {
/* 1530 */           cell.invalidate(bMustRefresh);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public void columnInvalidate(String sColumnName)
/*      */   {
/* 1538 */     TableColumnCore tc = TableColumnManager.getInstance().getTableColumnCore(getTableID(), sColumnName);
/*      */     
/* 1540 */     if (tc != null) {
/* 1541 */       columnInvalidate(tc, tc.getType() == 3);
/*      */     }
/*      */   }
/*      */   
/*      */   public void columnInvalidate(TableColumnCore tableColumn, final boolean bMustRefresh)
/*      */   {
/* 1547 */     final String sColumnName = tableColumn.getName();
/*      */     
/* 1549 */     runForAllRows(new TableGroupRowRunner() {
/*      */       public void run(TableRowCore row) {
/* 1551 */         TableCellCore cell = row.getTableCellCore(sColumnName);
/* 1552 */         if (cell != null) {
/* 1553 */           cell.invalidate(bMustRefresh);
/*      */         }
/*      */       }
/* 1556 */     });
/* 1557 */     resetLastSortedOn();
/* 1558 */     tableColumn.setLastSortValueChange(SystemTime.getCurrentTime());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void columnInvalidate(TableColumnCore tableColumn)
/*      */   {
/* 1565 */     columnInvalidate(tableColumn, true);
/*      */   }
/*      */   
/*      */   public String getPropertiesPrefix()
/*      */   {
/* 1570 */     return this.propertiesPrefix;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getTableID()
/*      */   {
/* 1576 */     return this.tableID;
/*      */   }
/*      */   
/*      */ 
/*      */   public Class<?> getDataSourceType()
/*      */   {
/* 1582 */     return this.classPluginDataSourceType;
/*      */   }
/*      */   
/*      */   public void tableStructureChanged(boolean columnAddedOrRemoved, Class forPluginDataSourceType)
/*      */   {
/* 1587 */     if ((forPluginDataSourceType != null) && (!forPluginDataSourceType.equals(getDataSourceType())))
/*      */     {
/* 1589 */       return;
/*      */     }
/* 1591 */     triggerLifeCycleListener(1);
/*      */     
/*      */     DATASOURCETYPE[] unfilteredDS;
/* 1594 */     synchronized (this.rows_sync) {
/* 1595 */       unfilteredDS = (Object[])this.listUnfilteredDataSources.keySet().toArray();
/*      */     }
/*      */     
/* 1598 */     if (DEBUGADDREMOVE) {
/* 1599 */       debug("TSC: #Unfiltered=" + unfilteredDS.length);
/*      */     }
/* 1601 */     removeAllTableRows();
/* 1602 */     processDataSourceQueueSync();
/*      */     
/* 1604 */     if (columnAddedOrRemoved) {
/* 1605 */       this.tableColumns = TableColumnManager.getInstance().getAllTableColumnCoreAsArray(getDataSourceType(), this.tableID);
/*      */       
/* 1607 */       ArrayList<TableColumnCore> listVisibleColumns = new ArrayList();
/* 1608 */       for (TableColumnCore column : this.tableColumns) {
/* 1609 */         if (column.isVisible()) {
/* 1610 */           listVisibleColumns.add(column);
/*      */         }
/*      */       }
/* 1613 */       Collections.sort(listVisibleColumns, new java.util.Comparator() {
/*      */         public int compare(TableColumnCore o1, TableColumnCore o2) {
/* 1615 */           if (o1 == o2) {
/* 1616 */             return 0;
/*      */           }
/* 1618 */           int diff = o1.getPosition() - o2.getPosition();
/* 1619 */           return diff;
/*      */         }
/* 1621 */       });
/* 1622 */       this.columnsOrdered = ((TableColumnCore[])listVisibleColumns.toArray(new TableColumnCore[0]));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1627 */     refreshTable(false);
/* 1628 */     triggerLifeCycleListener(0);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1633 */     if (this.listUnfilteredDataSources.size() == 0) {
/* 1634 */       addDataSources(unfilteredDS);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public TableColumn getTableColumn(String sColumnName)
/*      */   {
/* 1643 */     for (int i = 0; i < this.tableColumns.length; i++) {
/* 1644 */       TableColumnCore tc = this.tableColumns[i];
/* 1645 */       if (tc.getName().equals(sColumnName)) {
/* 1646 */         return tc;
/*      */       }
/*      */     }
/* 1649 */     return null;
/*      */   }
/*      */   
/*      */   public TableColumnCore[] getVisibleColumns()
/*      */   {
/* 1654 */     return this.columnsOrdered;
/*      */   }
/*      */   
/*      */   public TableColumnCore[] getAllColumns() {
/* 1658 */     return this.tableColumns;
/*      */   }
/*      */   
/*      */   protected void setColumnsOrdered(TableColumnCore[] columnsOrdered) {
/* 1662 */     this.columnsOrdered = columnsOrdered;
/*      */   }
/*      */   
/*      */   public boolean isColumnVisible(TableColumn column)
/*      */   {
/* 1667 */     if (column == null) {
/* 1668 */       return false;
/*      */     }
/* 1670 */     return column.isVisible();
/*      */   }
/*      */   
/*      */   public void refreshTable(boolean bForceSort) {
/* 1674 */     triggerTableRefreshListeners();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List<Object> getSelectedDataSourcesList()
/*      */   {
/* 1681 */     synchronized (this.rows_sync) {
/* 1682 */       if (this.listSelectedCoreDataSources != null) {
/* 1683 */         return this.listSelectedCoreDataSources;
/*      */       }
/*      */       
/* 1686 */       if ((isDisposed()) || (this.selectedRows.size() == 0)) {
/* 1687 */         return Collections.emptyList();
/*      */       }
/*      */       
/* 1690 */       ArrayList<Object> l = new ArrayList(this.selectedRows.size());
/*      */       
/* 1692 */       for (TableRowCore row : this.selectedRows) {
/* 1693 */         if ((row != null) && (!row.isRowDisposed())) {
/* 1694 */           Object ds = row.getDataSource(true);
/* 1695 */           if (ds != null) {
/* 1696 */             l.add(ds);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1701 */       this.listSelectedCoreDataSources = l;
/* 1702 */       return l;
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
/*      */ 
/*      */   public List<Object> getSelectedPluginDataSourcesList()
/*      */   {
/* 1716 */     synchronized (this.rows_sync) {
/* 1717 */       if ((isDisposed()) || (this.selectedRows.size() == 0)) {
/* 1718 */         return Collections.emptyList();
/*      */       }
/*      */       
/* 1721 */       ArrayList<Object> l = new ArrayList(this.selectedRows.size());
/* 1722 */       for (TableRowCore row : this.selectedRows) {
/* 1723 */         if ((row != null) && (!row.isRowDisposed())) {
/* 1724 */           Object ds = row.getDataSource(false);
/* 1725 */           if (ds != null) {
/* 1726 */             l.add(ds);
/*      */           }
/*      */         }
/*      */       }
/* 1730 */       return l;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public List<Object> getSelectedDataSources()
/*      */   {
/* 1742 */     return new ArrayList(getSelectedDataSourcesList());
/*      */   }
/*      */   
/*      */   public Object[] getSelectedDataSources(boolean bCoreDataSource)
/*      */   {
/* 1747 */     if (bCoreDataSource) {
/* 1748 */       return getSelectedDataSourcesList().toArray();
/*      */     }
/* 1750 */     return getSelectedPluginDataSourcesList().toArray();
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public TableRowCore[] getSelectedRows()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 1039	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:rows_sync	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 1048	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:selectedRows	Ljava/util/List;
/*      */     //   11: iconst_0
/*      */     //   12: anewarray 583	com/aelitis/azureus/ui/common/table/TableRowCore
/*      */     //   15: invokeinterface 1254 2 0
/*      */     //   20: checkcast 571	[Lcom/aelitis/azureus/ui/common/table/TableRowCore;
/*      */     //   23: aload_1
/*      */     //   24: monitorexit
/*      */     //   25: areturn
/*      */     //   26: astore_2
/*      */     //   27: aload_1
/*      */     //   28: monitorexit
/*      */     //   29: aload_2
/*      */     //   30: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1755	-> byte code offset #0
/*      */     //   Java source line #1756	-> byte code offset #7
/*      */     //   Java source line #1757	-> byte code offset #26
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	31	0	this	TableViewImpl<DATASOURCETYPE>
/*      */     //   5	23	1	Ljava/lang/Object;	Object
/*      */     //   26	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	25	26	finally
/*      */     //   26	29	26	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public int getSelectedRowsSize()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 1039	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:rows_sync	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 1048	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:selectedRows	Ljava/util/List;
/*      */     //   11: invokeinterface 1243 1 0
/*      */     //   16: aload_1
/*      */     //   17: monitorexit
/*      */     //   18: ireturn
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: aload_2
/*      */     //   23: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1762	-> byte code offset #0
/*      */     //   Java source line #1763	-> byte code offset #7
/*      */     //   Java source line #1764	-> byte code offset #19
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	24	0	this	TableViewImpl<DATASOURCETYPE>
/*      */     //   5	16	1	Ljava/lang/Object;	Object
/*      */     //   19	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	18	19	finally
/*      */     //   19	22	19	finally
/*      */   }
/*      */   
/*      */   public List<TableRowCore> getSelectedRowsList()
/*      */   {
/* 1773 */     synchronized (this.rows_sync) {
/* 1774 */       ArrayList<TableRowCore> l = new ArrayList(this.selectedRows.size());
/*      */       
/* 1776 */       for (TableRowCore row : this.selectedRows) {
/* 1777 */         if ((row != null) && (!row.isRowDisposed())) {
/* 1778 */           l.add(row);
/*      */         }
/*      */       }
/*      */       
/* 1782 */       return l;
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public boolean isSelected(org.gudy.azureus2.plugins.ui.tables.TableRow row)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 1039	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:rows_sync	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_2
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 1048	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:selectedRows	Ljava/util/List;
/*      */     //   11: aload_1
/*      */     //   12: invokeinterface 1250 2 0
/*      */     //   17: aload_2
/*      */     //   18: monitorexit
/*      */     //   19: ireturn
/*      */     //   20: astore_3
/*      */     //   21: aload_2
/*      */     //   22: monitorexit
/*      */     //   23: aload_3
/*      */     //   24: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1787	-> byte code offset #0
/*      */     //   Java source line #1788	-> byte code offset #7
/*      */     //   Java source line #1789	-> byte code offset #20
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	25	0	this	TableViewImpl<DATASOURCETYPE>
/*      */     //   0	25	1	row	org.gudy.azureus2.plugins.ui.tables.TableRow
/*      */     //   5	17	2	Ljava/lang/Object;	Object
/*      */     //   20	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	19	20	finally
/*      */     //   20	23	20	finally
/*      */   }
/*      */   
/*      */   public TableRowCore getFocusedRow()
/*      */   {
/* 1794 */     synchronized (this.rows_sync) {
/* 1795 */       if (this.selectedRows.size() == 0) {
/* 1796 */         return null;
/*      */       }
/* 1798 */       return (TableRowCore)this.selectedRows.get(0);
/*      */     }
/*      */   }
/*      */   
/*      */   public Object getFirstSelectedDataSource()
/*      */   {
/* 1804 */     return getFirstSelectedDataSource(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object getFirstSelectedDataSource(boolean bCoreObject)
/*      */   {
/* 1813 */     synchronized (this.rows_sync) {
/* 1814 */       if (this.selectedRows.size() > 0) {
/* 1815 */         return ((TableRowCore)this.selectedRows.get(0)).getDataSource(bCoreObject);
/*      */       }
/*      */     }
/* 1818 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void tableInvalidate()
/*      */   {
/* 1829 */     runForAllRows(new TableGroupRowVisibilityRunner() {
/*      */       public void run(TableRowCore row, boolean bVisible) {
/* 1831 */         row.invalidate();
/* 1832 */         row.refresh(true, bVisible);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public boolean getHeaderVisible()
/*      */   {
/* 1839 */     return this.headerVisible;
/*      */   }
/*      */   
/*      */   public void setHeaderVisible(boolean visible)
/*      */   {
/* 1844 */     this.headerVisible = visible;
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public TableColumnCore getSortColumn()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 1039	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:rows_sync	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 1031	com/aelitis/azureus/ui/common/table/impl/TableViewImpl:sortColumn	Lcom/aelitis/azureus/ui/common/table/TableColumnCore;
/*      */     //   11: aload_1
/*      */     //   12: monitorexit
/*      */     //   13: areturn
/*      */     //   14: astore_2
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: aload_2
/*      */     //   18: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1850	-> byte code offset #0
/*      */     //   Java source line #1851	-> byte code offset #7
/*      */     //   Java source line #1852	-> byte code offset #14
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	19	0	this	TableViewImpl<DATASOURCETYPE>
/*      */     //   5	11	1	Ljava/lang/Object;	Object
/*      */     //   14	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	13	14	finally
/*      */     //   14	17	14	finally
/*      */   }
/*      */   
/*      */   protected boolean setSortColumn(TableColumnCore newSortColumn, boolean allowOrderChange)
/*      */   {
/* 1856 */     if (newSortColumn == null) {
/* 1857 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1862 */     synchronized (this.rows_sync)
/*      */     {
/* 1864 */       boolean isSameColumn = newSortColumn.equals(this.sortColumn);
/* 1865 */       if (allowOrderChange) {
/* 1866 */         if (!isSameColumn) {
/* 1867 */           this.sortColumn = newSortColumn;
/*      */           
/* 1869 */           int iSortDirection = configMan.getIntParameter("config.style.table.defaultSortOrder");
/* 1870 */           if (iSortDirection == 0) {
/* 1871 */             this.sortColumn.setSortAscending(true);
/* 1872 */           } else if (iSortDirection == 1) {
/* 1873 */             this.sortColumn.setSortAscending(false);
/*      */           } else {
/* 1875 */             this.sortColumn.setSortAscending(!this.sortColumn.isSortAscending());
/*      */           }
/*      */           
/* 1878 */           TableColumnManager.getInstance().setDefaultSortColumnName(this.tableID, this.sortColumn.getName(), true);
/*      */         } else {
/* 1880 */           this.sortColumn.setSortAscending(!this.sortColumn.isSortAscending());
/*      */         }
/*      */       } else
/* 1883 */         this.sortColumn = newSortColumn;
/*      */       String name;
/* 1885 */       Iterator<TableRowCore> iter; if (!isSameColumn) {
/* 1886 */         name = this.sortColumn.getName();
/* 1887 */         for (iter = this.sortedRows.iterator(); iter.hasNext();) {
/* 1888 */           TableRowCore row = (TableRowCore)iter.next();
/* 1889 */           row.setSortColumn(name);
/*      */         }
/*      */       }
/* 1892 */       uiChangeColumnIndicator();
/* 1893 */       resetLastSortedOn();
/* 1894 */       sortColumn(!isSameColumn);
/* 1895 */       return !isSameColumn;
/*      */     }
/*      */   }
/*      */   
/*      */   public void setRowSelected(TableRowCore row, boolean selected, boolean trigger) {
/* 1900 */     if ((row == null) || (row.isRowDisposed())) {
/* 1901 */       return;
/*      */     }
/* 1903 */     if (isSingleSelection()) {
/* 1904 */       setSelectedRows(new TableRowCore[] { row }, trigger);
/*      */     } else {
/* 1906 */       boolean somethingChanged = false;
/*      */       ArrayList<TableRowCore> newSelectedRows;
/* 1908 */       synchronized (this.rows_sync) {
/* 1909 */         newSelectedRows = new ArrayList(this.selectedRows);
/* 1910 */         if (selected) {
/* 1911 */           if (!newSelectedRows.contains(row)) {
/* 1912 */             newSelectedRows.add(row);
/* 1913 */             somethingChanged = true;
/*      */           }
/*      */         } else {
/* 1916 */           somethingChanged = newSelectedRows.remove(row);
/*      */         }
/*      */       }
/*      */       
/* 1920 */       if (somethingChanged) {
/* 1921 */         setSelectedRows((TableRowCore[])newSelectedRows.toArray(new TableRowCore[0]), trigger);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void setSelectedRows(TableRowCore[] newSelectionArray, boolean trigger)
/*      */   {
/* 1928 */     if (isDisposed()) {
/* 1929 */       return;
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
/* 1941 */     List<TableRowCore> oldSelectionList = new ArrayList();
/*      */     
/*      */     List<TableRowCore> listNewlySelected;
/*      */     boolean somethingChanged;
/* 1945 */     synchronized (this.rows_sync) {
/* 1946 */       if ((this.selectedRows.size() == 0) && (newSelectionArray.length == 0)) {
/* 1947 */         return;
/*      */       }
/*      */       
/* 1950 */       oldSelectionList.addAll(this.selectedRows);
/* 1951 */       this.listSelectedCoreDataSources = null;
/* 1952 */       this.selectedRows.clear();
/*      */       
/* 1954 */       listNewlySelected = new ArrayList(1);
/*      */       
/*      */ 
/*      */ 
/* 1958 */       for (TableRowCore row : newSelectionArray) {
/* 1959 */         if ((row != null) && (!row.isRowDisposed()))
/*      */         {
/*      */ 
/*      */ 
/* 1963 */           boolean existed = false;
/* 1964 */           for (TableRowCore oldRow : oldSelectionList) {
/* 1965 */             if (oldRow == row) {
/* 1966 */               existed = true;
/* 1967 */               if (!this.selectedRows.contains(row)) {
/* 1968 */                 this.selectedRows.add(row);
/*      */               }
/* 1970 */               oldSelectionList.remove(row);
/* 1971 */               break;
/*      */             }
/*      */           }
/* 1974 */           if (!existed) {
/* 1975 */             if (!this.selectedRows.contains(row)) {
/* 1976 */               this.selectedRows.add(row);
/*      */             }
/* 1978 */             if (!listNewlySelected.contains(row)) {
/* 1979 */               listNewlySelected.add(row);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1984 */       somethingChanged = (listNewlySelected.size() > 0) || (oldSelectionList.size() > 0);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1993 */     if (somethingChanged) {
/* 1994 */       uiSelectionChanged((TableRowCore[])listNewlySelected.toArray(new TableRowCore[0]), (TableRowCore[])oldSelectionList.toArray(new TableRowCore[0]));
/*      */     }
/*      */     
/* 1997 */     if ((trigger) && (somethingChanged)) {
/* 1998 */       if (listNewlySelected.size() > 0) {
/* 1999 */         triggerSelectionListeners((TableRowCore[])listNewlySelected.toArray(new TableRowCore[0]));
/*      */       }
/* 2001 */       if (oldSelectionList.size() > 0) {
/* 2002 */         triggerDeselectionListeners((TableRowCore[])oldSelectionList.toArray(new TableRowCore[0]));
/*      */       }
/*      */       
/* 2005 */       triggerTabViewsDataSourceChanged();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public abstract boolean isSingleSelection();
/*      */   
/*      */   public abstract void uiSelectionChanged(TableRowCore[] paramArrayOfTableRowCore1, TableRowCore[] paramArrayOfTableRowCore2);
/*      */   
/*      */   public void setSelectedRows(TableRowCore[] rows)
/*      */   {
/* 2016 */     setSelectedRows(rows, true);
/*      */   }
/*      */   
/*      */   public void selectAll() {
/* 2020 */     setSelectedRows(getRows(), true);
/*      */   }
/*      */   
/*      */   public String getFilterText() {
/* 2024 */     return this.filter == null ? "" : this.filter.text;
/*      */   }
/*      */   
/*      */   public boolean isMenuEnabled() {
/* 2028 */     return this.menuEnabled;
/*      */   }
/*      */   
/*      */   public void setMenuEnabled(boolean menuEnabled) {
/* 2032 */     this.menuEnabled = menuEnabled;
/*      */   }
/*      */   
/*      */   protected boolean isLastRow(TableRowCore row) {
/* 2036 */     synchronized (this.rows_sync) {
/* 2037 */       int size = this.sortedRows.size();
/* 2038 */       return size != 0;
/*      */     }
/*      */   }
/*      */   
/*      */   public abstract void triggerTabViewsDataSourceChanged();
/*      */   
/*      */   protected abstract void uiChangeColumnIndicator();
/*      */   
/*      */   public boolean isProvideIndexesOnRemove() {
/* 2047 */     return this.provideIndexesOnRemove;
/*      */   }
/*      */   
/*      */   public void setProvideIndexesOnRemove(boolean provideIndexesOnRemove) {
/* 2051 */     this.provideIndexesOnRemove = provideIndexesOnRemove;
/*      */   }
/*      */   
/*      */   public boolean isTableSelected() {
/* 2055 */     return com.aelitis.azureus.ui.selectedcontent.SelectedContentManager.getCurrentlySelectedTableView() == this;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/table/impl/TableViewImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */