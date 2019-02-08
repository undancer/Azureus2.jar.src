/*      */ package com.aelitis.azureus.ui.common.table.impl;
/*      */ 
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.common.table.TableCellCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableStructureEventDispatcher;
/*      */ import java.io.PrintStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.LightHashMap;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadTypeComplete;
/*      */ import org.gudy.azureus2.plugins.download.DownloadTypeIncomplete;
/*      */ import org.gudy.azureus2.plugins.peers.Peer;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareResource;
/*      */ import org.gudy.azureus2.plugins.tracker.TrackerTorrent;
/*      */ import org.gudy.azureus2.plugins.ui.UIRuntimeException;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellClipboardListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellDisposeListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellInplaceEditorListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellLightRefreshListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseMoveListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellToolTipListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellVisibilityListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ui.tables.TableContextMenuItemImpl;
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
/*      */ public class TableColumnImpl
/*      */   implements TableColumnCore
/*      */ {
/*      */   private static final String CFG_SORTDIRECTION = "config.style.table.defaultSortOrder";
/*      */   private static final String ATTRIBUTE_NAME_OVERIDE = "tablecolumn.nameoverride";
/*   65 */   private static UIFunctions uiFunctions = ;
/*      */   
/*      */   private String sName;
/*      */   
/*      */   private static int adjustPXForDPI(int px)
/*      */   {
/*   71 */     if (uiFunctions == null)
/*      */     {
/*   73 */       return px;
/*      */     }
/*      */     
/*   76 */     return uiFunctions.adjustPXForDPI(px);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   83 */   private String sTitleLanguageKey = null;
/*      */   
/*      */   private int iAlignment;
/*      */   
/*   87 */   private int iDefaultAlignment = -1;
/*      */   
/*      */ 
/*      */   private int iType;
/*      */   
/*      */   private int iPosition;
/*      */   
/*      */   private int iWidth;
/*      */   
/*      */   private int iDefaultWidth;
/*      */   
/*      */   private int iInterval;
/*      */   
/*      */   private long lLastSortValueChange;
/*      */   
/*      */   private String sTableID;
/*      */   
/*      */   private boolean bColumnAdded;
/*      */   
/*      */   private boolean bCoreDataSource;
/*      */   
/*      */   private TableCellInplaceEditorListener cellEditorListener;
/*      */   
/*      */   private ArrayList<TableCellRefreshListener> cellRefreshListeners;
/*      */   
/*      */   private ArrayList<TableCellAddedListener> cellAddedListeners;
/*      */   
/*      */   private ArrayList<TableCellDisposeListener> cellDisposeListeners;
/*      */   
/*      */   private ArrayList<TableCellToolTipListener> cellToolTipListeners;
/*      */   
/*      */   private ArrayList<TableCellMouseListener> cellMouseListeners;
/*      */   
/*      */   private ArrayList<TableCellMouseMoveListener> cellMouseMoveListeners;
/*      */   
/*      */   private ArrayList<TableCellVisibilityListener> cellVisibilityListeners;
/*      */   
/*      */   private ArrayList<TableCellClipboardListener> cellClipboardListeners;
/*      */   
/*      */   private ArrayList<TableColumnExtraInfoListener> columnExtraInfoListeners;
/*      */   
/*      */   private Map<String, List<Object>> mapOtherCellListeners;
/*      */   
/*      */   private int iConsecutiveErrCount;
/*      */   
/*      */   private ArrayList<TableContextMenuItem> menuItemsHeader;
/*      */   
/*      */   private ArrayList<TableContextMenuItem> menuItemsColumn;
/*      */   
/*      */   private boolean bObfusticateData;
/*      */   
/*  138 */   protected AEMonitor this_mon = new AEMonitor("TableColumn");
/*      */   
/*      */   private boolean bSortValueLive;
/*      */   
/*      */   private long lStatsRefreshTotalTime;
/*      */   
/*  144 */   private long lStatsRefreshCount = 0L;
/*      */   
/*  146 */   private long lStatsRefreshZeroCount = 0L;
/*      */   
/*      */   private boolean bSortAscending;
/*      */   
/*      */   private boolean bDefaultSortAscending;
/*  151 */   private int iMinWidth = -1;
/*      */   
/*  153 */   private int iMaxWidth = -1;
/*      */   
/*      */   private boolean bVisible;
/*      */   
/*  157 */   private boolean bMaxWidthAuto = false;
/*      */   
/*      */   private boolean bWidthAuto;
/*      */   
/*      */   private int iPreferredWidth;
/*      */   
/*  163 */   private boolean bPreferredWidthAuto = true;
/*      */   
/*  165 */   private int iPreferredWidthMax = -1;
/*      */   
/*  167 */   private boolean auto_tooltip = false;
/*      */   
/*      */   private Map userData;
/*      */   
/*      */   private boolean removed;
/*      */   
/*  173 */   private List<Class<?>> forPluginDataSourceTypes = new ArrayList();
/*      */   
/*      */   private String iconID;
/*      */   
/*      */   private boolean firstLoad;
/*      */   private boolean showOnlyImage;
/*      */   
/*      */   public TableColumnImpl(String tableID, String columnID)
/*      */   {
/*  182 */     init(tableID, columnID);
/*      */   }
/*      */   
/*      */ 
/*      */   private void init(String tableID, String columnID)
/*      */   {
/*  188 */     this.sTableID = tableID;
/*  189 */     this.sName = columnID;
/*  190 */     this.iType = 3;
/*  191 */     this.iWidth = 50;
/*  192 */     this.iAlignment = 1;
/*  193 */     this.bColumnAdded = false;
/*  194 */     this.bCoreDataSource = false;
/*  195 */     this.iInterval = -3;
/*  196 */     this.iConsecutiveErrCount = 0;
/*  197 */     this.lLastSortValueChange = 0L;
/*  198 */     this.bVisible = false;
/*  199 */     this.iMinWidth = adjustPXForDPI(16);
/*  200 */     this.iPosition = -1;
/*  201 */     int iSortDirection = COConfigurationManager.getIntParameter("config.style.table.defaultSortOrder");
/*  202 */     this.bSortAscending = (iSortDirection != 1);
/*      */   }
/*      */   
/*      */   public void initialize(int iAlignment, int iPosition, int iWidth, int iInterval)
/*      */   {
/*  207 */     if (this.bColumnAdded) {
/*  208 */       throw new UIRuntimeException("Can't set properties. Column '" + this.sName + " already added");
/*      */     }
/*      */     
/*      */ 
/*  212 */     this.iAlignment = (this.iDefaultAlignment = iAlignment);
/*  213 */     setPosition(iPosition);
/*  214 */     this.iWidth = (this.iDefaultWidth = adjustPXForDPI(iWidth));
/*  215 */     this.iMinWidth = adjustPXForDPI(16);
/*  216 */     this.iInterval = iInterval;
/*      */   }
/*      */   
/*      */   public void initialize(int iAlignment, int iPosition, int iWidth) {
/*  220 */     if (this.bColumnAdded) {
/*  221 */       throw new UIRuntimeException("Can't set properties. Column '" + this.sName + " already added");
/*      */     }
/*      */     
/*      */ 
/*  225 */     this.iAlignment = (this.iDefaultAlignment = iAlignment);
/*  226 */     setPosition(iPosition);
/*  227 */     this.iWidth = (this.iDefaultWidth = adjustPXForDPI(iWidth));
/*  228 */     this.iMinWidth = adjustPXForDPI(16);
/*      */   }
/*      */   
/*      */   public String getName() {
/*  232 */     return this.sName;
/*      */   }
/*      */   
/*      */   public String getNameOverride()
/*      */   {
/*  237 */     return getUserDataString("tablecolumn.nameoverride");
/*      */   }
/*      */   
/*      */   public void setNameOverride(String name)
/*      */   {
/*  242 */     setUserData("tablecolumn.nameoverride", name);
/*      */   }
/*      */   
/*      */   public String getTableID() {
/*  246 */     return this.sTableID;
/*      */   }
/*      */   
/*      */   public void setType(int type) {
/*  250 */     if (this.bColumnAdded) {
/*  251 */       throw new UIRuntimeException("Can't set properties. Column '" + this.sName + " already added");
/*      */     }
/*      */     
/*      */ 
/*  255 */     this.iType = type;
/*      */   }
/*      */   
/*      */   public int getType() {
/*  259 */     return this.iType;
/*      */   }
/*      */   
/*      */   public void setWidth(int realPXWidth) {
/*  263 */     setWidthPX(adjustPXForDPI(realPXWidth));
/*      */   }
/*      */   
/*      */   public void setWidthPX(int width) {
/*  267 */     if ((width == this.iWidth) || (width < 0)) {
/*  268 */       return;
/*      */     }
/*      */     
/*  271 */     if ((this.iMinWidth > 0) && (width < this.iMinWidth)) {
/*  272 */       return;
/*      */     }
/*      */     
/*  275 */     if ((this.iMaxWidth > 0) && (width > this.iMaxWidth)) {
/*  276 */       if (width == this.iMaxWidth) {
/*  277 */         return;
/*      */       }
/*  279 */       width = this.iMaxWidth;
/*      */     }
/*      */     
/*  282 */     if (this.iMinWidth < 0) {
/*  283 */       this.iMinWidth = width;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  290 */     int diff = width - this.iWidth;
/*  291 */     this.iWidth = width;
/*  292 */     if (this.iDefaultWidth == 0) {
/*  293 */       this.iDefaultWidth = width;
/*      */     }
/*      */     
/*  296 */     if ((this.bColumnAdded) && (this.bVisible)) {
/*  297 */       triggerColumnSizeChange(diff);
/*      */     }
/*      */   }
/*      */   
/*      */   public void triggerColumnSizeChange(int diff) {
/*  302 */     TableStructureEventDispatcher tsed = TableStructureEventDispatcher.getInstance(this.sTableID);
/*  303 */     tsed.columnSizeChanged(this, diff);
/*  304 */     if (this.iType == 2) {
/*  305 */       invalidateCells();
/*      */     }
/*      */   }
/*      */   
/*      */   public int getWidth() {
/*  310 */     return this.iWidth;
/*      */   }
/*      */   
/*      */   public void setPosition(int position) {
/*  314 */     if (this.bColumnAdded) {
/*  315 */       throw new UIRuntimeException("Can't set properties. Column '" + this.sName + " already added");
/*      */     }
/*      */     
/*      */ 
/*  319 */     if ((this.iPosition == -1) && (position != -1)) {
/*  320 */       setVisible(true);
/*      */     }
/*  322 */     this.iPosition = position;
/*  323 */     if (position == -1) {
/*  324 */       setVisible(false);
/*      */     }
/*      */   }
/*      */   
/*      */   public int getPosition() {
/*  329 */     return this.iPosition;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setAlignment(int alignment)
/*      */   {
/*  340 */     if (alignment == -1) {
/*  341 */       if (this.iDefaultAlignment != -1) {
/*  342 */         this.iAlignment = this.iDefaultAlignment;
/*      */       }
/*      */     } else {
/*  345 */       this.iAlignment = alignment;
/*      */       
/*  347 */       if (this.iDefaultAlignment == -1) {
/*  348 */         this.iDefaultAlignment = alignment;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getAlignment()
/*      */   {
/*  359 */     return this.iAlignment;
/*      */   }
/*      */   
/*      */   public void addCellRefreshListener(TableCellRefreshListener listener) {
/*      */     try {
/*  364 */       this.this_mon.enter();
/*      */       
/*  366 */       if (this.cellRefreshListeners == null) {
/*  367 */         this.cellRefreshListeners = new ArrayList(1);
/*      */       }
/*      */       
/*  370 */       this.cellRefreshListeners.add(listener);
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*  375 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public List<TableCellRefreshListener> getCellRefreshListeners() {
/*      */     try {
/*  381 */       this.this_mon.enter();
/*      */       ArrayList localArrayList;
/*  383 */       if (this.cellRefreshListeners == null) {
/*  384 */         return new ArrayList(0);
/*      */       }
/*      */       
/*  387 */       return new ArrayList(this.cellRefreshListeners);
/*      */     }
/*      */     finally
/*      */     {
/*  391 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeCellRefreshListener(TableCellRefreshListener listener) {
/*      */     try {
/*  397 */       this.this_mon.enter();
/*      */       
/*  399 */       if (this.cellRefreshListeners == null) {
/*      */         return;
/*      */       }
/*      */       
/*  403 */       this.cellRefreshListeners.remove(listener);
/*      */     } finally {
/*  405 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean hasCellRefreshListener() {
/*  410 */     return (this.cellRefreshListeners != null) && (this.cellRefreshListeners.size() > 0);
/*      */   }
/*      */   
/*      */   public void setRefreshInterval(int interval) {
/*  414 */     this.iInterval = interval;
/*      */   }
/*      */   
/*      */   public int getRefreshInterval() {
/*  418 */     return this.iInterval;
/*      */   }
/*      */   
/*      */   public void addCellAddedListener(TableCellAddedListener listener) {
/*      */     try {
/*  423 */       this.this_mon.enter();
/*      */       
/*  425 */       if (this.cellAddedListeners == null) {
/*  426 */         this.cellAddedListeners = new ArrayList(1);
/*      */       }
/*      */       
/*  429 */       this.cellAddedListeners.add(listener);
/*      */     }
/*      */     finally
/*      */     {
/*  433 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addCellOtherListener(String listenerID, Object listener) {
/*      */     try {
/*  439 */       this.this_mon.enter();
/*      */       
/*  441 */       if (this.mapOtherCellListeners == null) {
/*  442 */         this.mapOtherCellListeners = new HashMap(1);
/*      */       }
/*      */       
/*  445 */       List<Object> list = (List)this.mapOtherCellListeners.get(listenerID);
/*  446 */       if (list == null) {
/*  447 */         list = new ArrayList(1);
/*  448 */         this.mapOtherCellListeners.put(listenerID, list);
/*      */       }
/*      */       
/*  451 */       list.add(listener);
/*      */     }
/*      */     finally
/*      */     {
/*  455 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeCellOtherListener(String listenerID, Object l) {
/*      */     try {
/*  461 */       this.this_mon.enter();
/*      */       
/*  463 */       if (this.mapOtherCellListeners == null) {
/*      */         return;
/*      */       }
/*      */       
/*  467 */       this.mapOtherCellListeners.remove(listenerID);
/*      */     }
/*      */     finally
/*      */     {
/*  471 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public Object[] getCellOtherListeners(String listenerID)
/*      */   {
/*  477 */     if (this.mapOtherCellListeners == null) {
/*  478 */       return null;
/*      */     }
/*      */     
/*  481 */     List<Object> list = (List)this.mapOtherCellListeners.get(listenerID);
/*  482 */     if (list == null) {
/*  483 */       return null;
/*      */     }
/*  485 */     return list.toArray();
/*      */   }
/*      */   
/*      */   public boolean hasCellOtherListeners(String listenerID)
/*      */   {
/*  490 */     return (this.mapOtherCellListeners != null) && (this.mapOtherCellListeners.get(listenerID) != null);
/*      */   }
/*      */   
/*      */   public List getCellAddedListeners()
/*      */   {
/*      */     try {
/*  496 */       this.this_mon.enter();
/*      */       Object localObject1;
/*  498 */       if (this.cellAddedListeners == null) {
/*  499 */         return Collections.emptyList();
/*      */       }
/*      */       
/*  502 */       return new ArrayList(this.cellAddedListeners);
/*      */     }
/*      */     finally
/*      */     {
/*  506 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeCellAddedListener(TableCellAddedListener listener) {
/*      */     try {
/*  512 */       this.this_mon.enter();
/*      */       
/*  514 */       if (this.cellAddedListeners == null) {
/*      */         return;
/*      */       }
/*      */       
/*  518 */       this.cellAddedListeners.remove(listener);
/*      */     }
/*      */     finally
/*      */     {
/*  522 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addCellDisposeListener(TableCellDisposeListener listener) {
/*      */     try {
/*  528 */       this.this_mon.enter();
/*      */       
/*  530 */       if (this.cellDisposeListeners == null) {
/*  531 */         this.cellDisposeListeners = new ArrayList(1);
/*      */       }
/*      */       
/*  534 */       this.cellDisposeListeners.add(listener);
/*      */     }
/*      */     finally {
/*  537 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeCellDisposeListener(TableCellDisposeListener listener) {
/*      */     try {
/*  543 */       this.this_mon.enter();
/*      */       
/*  545 */       if (this.cellDisposeListeners == null) {
/*      */         return;
/*      */       }
/*      */       
/*  549 */       this.cellDisposeListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  552 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addCellToolTipListener(TableCellToolTipListener listener) {
/*      */     try {
/*  558 */       this.this_mon.enter();
/*      */       
/*  560 */       if (this.cellToolTipListeners == null) {
/*  561 */         this.cellToolTipListeners = new ArrayList(1);
/*      */       }
/*      */       
/*  564 */       this.cellToolTipListeners.add(listener);
/*      */     }
/*      */     finally {
/*  567 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeCellToolTipListener(TableCellToolTipListener listener) {
/*      */     try {
/*  573 */       this.this_mon.enter();
/*      */       
/*  575 */       if (this.cellToolTipListeners == null) {
/*      */         return;
/*      */       }
/*      */       
/*  579 */       this.cellToolTipListeners.remove(listener);
/*      */     } finally {
/*  581 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addCellMouseListener(TableCellMouseListener listener) {
/*      */     try {
/*  587 */       this.this_mon.enter();
/*      */       
/*  589 */       if (this.cellMouseListeners == null) {
/*  590 */         this.cellMouseListeners = new ArrayList(1);
/*      */       }
/*      */       
/*  593 */       this.cellMouseListeners.add(listener);
/*      */     }
/*      */     finally {
/*  596 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeCellMouseListener(TableCellMouseListener listener) {
/*      */     try {
/*  602 */       this.this_mon.enter();
/*      */       
/*  604 */       if (this.cellMouseListeners == null) {
/*      */         return;
/*      */       }
/*      */       
/*  608 */       this.cellMouseListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  611 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean hasCellMouseMoveListener() {
/*  616 */     return (this.cellMouseMoveListeners != null) && (this.cellMouseMoveListeners.size() > 0);
/*      */   }
/*      */   
/*      */   public void addCellMouseMoveListener(TableCellMouseMoveListener listener) {
/*      */     try {
/*  621 */       this.this_mon.enter();
/*      */       
/*  623 */       if (this.cellMouseMoveListeners == null) {
/*  624 */         this.cellMouseMoveListeners = new ArrayList(1);
/*      */       }
/*      */       
/*  627 */       this.cellMouseMoveListeners.add(listener);
/*      */     }
/*      */     finally {
/*  630 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeCellMouseMoveListener(TableCellMouseMoveListener listener) {
/*      */     try {
/*  636 */       this.this_mon.enter();
/*      */       
/*  638 */       if (this.cellMouseMoveListeners == null) {
/*      */         return;
/*      */       }
/*      */       
/*  642 */       this.cellMouseMoveListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  645 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addCellClipboardListener(TableCellClipboardListener listener) {
/*      */     try {
/*  651 */       this.this_mon.enter();
/*      */       
/*  653 */       if (this.cellClipboardListeners == null) {
/*  654 */         this.cellClipboardListeners = new ArrayList(1);
/*      */       }
/*      */       
/*  657 */       this.cellClipboardListeners.add(listener);
/*      */     }
/*      */     finally {
/*  660 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeCellClipboardListener(TableCellClipboardListener listener) {
/*      */     try {
/*  666 */       this.this_mon.enter();
/*      */       
/*  668 */       if (this.cellClipboardListeners == null) {
/*      */         return;
/*      */       }
/*      */       
/*  672 */       this.cellClipboardListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  675 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addCellVisibilityListener(TableCellVisibilityListener listener) {
/*      */     try {
/*  681 */       this.this_mon.enter();
/*      */       
/*  683 */       if (this.cellVisibilityListeners == null) {
/*  684 */         this.cellVisibilityListeners = new ArrayList(1);
/*      */       }
/*      */       
/*  687 */       this.cellVisibilityListeners.add(listener);
/*      */     }
/*      */     finally {
/*  690 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeCellVisibilityListener(TableCellVisibilityListener listener) {
/*      */     try {
/*  696 */       this.this_mon.enter();
/*      */       
/*  698 */       if (this.cellVisibilityListeners == null) {
/*      */         return;
/*      */       }
/*      */       
/*  702 */       this.cellVisibilityListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  705 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public List<TableColumnExtraInfoListener> getColumnExtraInfoListeners() {
/*      */     try {
/*  711 */       this.this_mon.enter();
/*      */       ArrayList localArrayList;
/*  713 */       if (this.columnExtraInfoListeners == null) {
/*  714 */         return new ArrayList(0);
/*      */       }
/*      */       
/*  717 */       return new ArrayList(this.columnExtraInfoListeners);
/*      */     }
/*      */     finally
/*      */     {
/*  721 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addColumnExtraInfoListener(TableColumnExtraInfoListener listener) {
/*      */     try {
/*  727 */       this.this_mon.enter();
/*      */       
/*  729 */       if (this.columnExtraInfoListeners == null) {
/*  730 */         this.columnExtraInfoListeners = new ArrayList(1);
/*      */       }
/*      */       
/*  733 */       this.columnExtraInfoListeners.add(listener);
/*      */     }
/*      */     finally {
/*  736 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeColumnExtraInfoListener(TableColumnExtraInfoListener listener) {
/*      */     try {
/*  742 */       this.this_mon.enter();
/*      */       
/*  744 */       if (this.columnExtraInfoListeners == null) {
/*      */         return;
/*      */       }
/*      */       
/*  748 */       this.columnExtraInfoListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  751 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void invalidateCells() {
/*  756 */     TableStructureEventDispatcher tsed = TableStructureEventDispatcher.getInstance(this.sTableID);
/*  757 */     tsed.columnInvalidate(this);
/*      */   }
/*      */   
/*      */   public void invalidateCell(Object data_source) {
/*  761 */     TableStructureEventDispatcher tsed = TableStructureEventDispatcher.getInstance(this.sTableID);
/*  762 */     tsed.cellInvalidate(this, data_source);
/*      */   }
/*      */   
/*      */   public void addListeners(Object listenerObject) {
/*  766 */     if ((listenerObject instanceof TableCellDisposeListener)) {
/*  767 */       addCellDisposeListener((TableCellDisposeListener)listenerObject);
/*      */     }
/*      */     
/*  770 */     if ((listenerObject instanceof TableCellRefreshListener)) {
/*  771 */       addCellRefreshListener((TableCellRefreshListener)listenerObject);
/*      */     }
/*      */     
/*  774 */     if ((listenerObject instanceof TableCellToolTipListener)) {
/*  775 */       addCellToolTipListener((TableCellToolTipListener)listenerObject);
/*      */     }
/*      */     
/*  778 */     if ((listenerObject instanceof TableCellAddedListener)) {
/*  779 */       addCellAddedListener((TableCellAddedListener)listenerObject);
/*      */     }
/*      */     
/*  782 */     if ((listenerObject instanceof TableCellMouseMoveListener)) {
/*  783 */       addCellMouseMoveListener((TableCellMouseMoveListener)listenerObject);
/*      */     }
/*      */     
/*  786 */     if ((listenerObject instanceof TableCellMouseListener)) {
/*  787 */       addCellMouseListener((TableCellMouseListener)listenerObject);
/*      */     }
/*      */     
/*  790 */     if ((listenerObject instanceof TableCellVisibilityListener)) {
/*  791 */       addCellVisibilityListener((TableCellVisibilityListener)listenerObject);
/*      */     }
/*      */     
/*  794 */     if ((listenerObject instanceof TableColumnExtraInfoListener)) {
/*  795 */       addColumnExtraInfoListener((TableColumnExtraInfoListener)listenerObject);
/*      */     }
/*      */     
/*  798 */     if ((listenerObject instanceof TableCellClipboardListener)) {
/*  799 */       addCellClipboardListener((TableCellClipboardListener)listenerObject);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void setColumnAdded()
/*      */   {
/*  806 */     if (this.bColumnAdded) {
/*  807 */       return;
/*      */     }
/*  809 */     preAdd();
/*  810 */     this.bColumnAdded = true;
/*      */   }
/*      */   
/*      */   public boolean getColumnAdded() {
/*  814 */     return this.bColumnAdded;
/*      */   }
/*      */   
/*      */   public void setUseCoreDataSource(boolean bCoreDataSource) {
/*  818 */     this.bCoreDataSource = bCoreDataSource;
/*      */   }
/*      */   
/*      */   public boolean getUseCoreDataSource() {
/*  822 */     return this.bCoreDataSource;
/*      */   }
/*      */   
/*      */   public void invokeCellRefreshListeners(TableCell cell, boolean fastRefresh) throws Throwable
/*      */   {
/*  827 */     if ((this.cellRefreshListeners == null) || (cell == null) || (cell.isDisposed())) {
/*  828 */       return;
/*      */     }
/*      */     
/*  831 */     Throwable firstError = null;
/*      */     
/*      */ 
/*  834 */     for (int i = 0; i < this.cellRefreshListeners.size(); i++)
/*      */     {
/*  836 */       TableCellRefreshListener l = (TableCellRefreshListener)this.cellRefreshListeners.get(i);
/*      */       try
/*      */       {
/*  839 */         if ((l instanceof TableCellLightRefreshListener)) {
/*  840 */           ((TableCellLightRefreshListener)l).refresh(cell, fastRefresh);
/*      */         } else {
/*  842 */           l.refresh(cell);
/*      */         }
/*      */       } catch (Throwable e) {
/*  845 */         if (firstError == null) {
/*  846 */           firstError = e;
/*      */         }
/*  848 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  852 */     if (firstError != null) {
/*  853 */       throw firstError;
/*      */     }
/*      */   }
/*      */   
/*      */   public void invokeCellAddedListeners(TableCell cell) {
/*  858 */     if (this.cellAddedListeners == null) {
/*  859 */       return;
/*      */     }
/*  861 */     for (int i = 0; i < this.cellAddedListeners.size(); i++) {
/*      */       try
/*      */       {
/*  864 */         ((TableCellAddedListener)this.cellAddedListeners.get(i)).cellAdded(cell);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  868 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void invokeCellDisposeListeners(TableCell cell) {
/*  874 */     if (this.cellDisposeListeners == null) {
/*  875 */       return;
/*      */     }
/*  877 */     for (int i = 0; i < this.cellDisposeListeners.size(); i++) {
/*      */       try {
/*  879 */         ((TableCellDisposeListener)this.cellDisposeListeners.get(i)).dispose(cell);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  883 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public String getClipboardText(TableCell cell) {
/*  889 */     if (this.cellClipboardListeners == null) {
/*  890 */       return null;
/*      */     }
/*  892 */     for (int i = 0; i < this.cellClipboardListeners.size(); i++) {
/*      */       try {
/*  894 */         String text = ((TableCellClipboardListener)this.cellClipboardListeners.get(i)).getClipboardText(cell);
/*      */         
/*  896 */         if (text != null) {
/*  897 */           return text;
/*      */         }
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  902 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  906 */     return null;
/*      */   }
/*      */   
/*      */   public void invokeCellToolTipListeners(TableCellCore cell, int type) {
/*  910 */     if (this.cellToolTipListeners == null) {
/*  911 */       return;
/*      */     }
/*  913 */     if (type == 0) {
/*  914 */       for (int i = 0; i < this.cellToolTipListeners.size(); i++) {
/*      */         try {
/*  916 */           ((TableCellToolTipListener)this.cellToolTipListeners.get(i)).cellHover(cell);
/*      */         }
/*      */         catch (Throwable e) {
/*  919 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     } else {
/*  923 */       for (int i = 0; i < this.cellToolTipListeners.size(); i++) {
/*      */         try
/*      */         {
/*  926 */           ((TableCellToolTipListener)this.cellToolTipListeners.get(i)).cellHoverComplete(cell);
/*      */         }
/*      */         catch (Throwable e) {
/*  929 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void invokeCellMouseListeners(TableCellMouseEvent event) {
/*  936 */     ArrayList<?> listeners = event.eventType == 3 ? this.cellMouseMoveListeners : this.cellMouseListeners;
/*      */     
/*  938 */     if (listeners == null) {
/*  939 */       return;
/*      */     }
/*      */     
/*  942 */     for (int i = 0; i < listeners.size(); i++) {
/*      */       try {
/*  944 */         TableCellMouseListener l = (TableCellMouseListener)listeners.get(i);
/*      */         
/*  946 */         l.cellMouseTrigger(event);
/*      */       }
/*      */       catch (Throwable e) {
/*  949 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void invokeCellVisibilityListeners(TableCellCore cell, int visibility) {
/*  955 */     if (this.cellVisibilityListeners == null) {
/*  956 */       return;
/*      */     }
/*      */     
/*  959 */     for (int i = 0; i < this.cellVisibilityListeners.size(); i++) {
/*      */       try {
/*  961 */         TableCellVisibilityListener l = (TableCellVisibilityListener)this.cellVisibilityListeners.get(i);
/*      */         
/*  963 */         l.cellVisibilityChanged(cell, visibility);
/*      */       }
/*      */       catch (Throwable e) {
/*  966 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPositionNoShift(int position)
/*      */   {
/*  975 */     this.iPosition = position;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object getUserData(String key)
/*      */   {
/*  982 */     if (this.userData != null)
/*  983 */       return this.userData.get(key);
/*  984 */     return null;
/*      */   }
/*      */   
/*      */   public String getUserDataString(String key) {
/*  988 */     if (this.userData != null) {
/*  989 */       Object o = this.userData.get(key);
/*  990 */       if ((o instanceof String))
/*  991 */         return (String)o;
/*  992 */       if ((o instanceof byte[])) {
/*      */         try {
/*  994 */           String s = new String((byte[])o, "utf-8");
/*      */           
/*  996 */           this.userData.put(key, s);
/*  997 */           return s;
/*      */         }
/*      */         catch (UnsupportedEncodingException e) {}
/*      */       }
/*      */     }
/* 1002 */     return null;
/*      */   }
/*      */   
/*      */   public void setUserData(String key, Object value) {
/* 1006 */     if (this.userData == null)
/* 1007 */       this.userData = new LightHashMap(2);
/* 1008 */     this.userData.put(key, value);
/*      */   }
/*      */   
/*      */   public void removeUserData(String key) {
/* 1012 */     if (this.userData == null)
/* 1013 */       return;
/* 1014 */     this.userData.remove(key);
/* 1015 */     if (this.userData.size() < 1) {
/* 1016 */       this.userData = null;
/*      */     }
/*      */   }
/*      */   
/*      */   public void remove()
/*      */   {
/* 1022 */     this.removed = true;
/*      */     
/* 1024 */     TableColumnManager.getInstance().removeColumns(new TableColumnCore[] { this });
/*      */     
/* 1026 */     TableStructureEventDispatcher tsed = TableStructureEventDispatcher.getInstance(this.sTableID);
/*      */     
/* 1028 */     for (Class<?> cla : this.forPluginDataSourceTypes) {
/* 1029 */       tsed.tableStructureChanged(true, cla);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isRemoved()
/*      */   {
/* 1036 */     return this.removed;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final void loadSettings(Map mapSettings)
/*      */   {
/* 1043 */     String itemPrefix = "Column." + this.sName;
/* 1044 */     String oldItemPrefix = "Table." + this.sTableID + "." + this.sName;
/* 1045 */     Object object = mapSettings.get(itemPrefix);
/*      */     Object[] list;
/* 1047 */     Object[] list; if ((object instanceof List)) {
/* 1048 */       list = ((List)object).toArray();
/*      */     } else {
/* 1050 */       list = new String[0];
/*      */     }
/*      */     
/* 1053 */     int pos = 0;
/* 1054 */     if ((list.length >= pos + 1) && ((list[pos] instanceof Number))) {
/* 1055 */       boolean vis = ((Number)list[pos]).intValue() == 1;
/* 1056 */       setVisible(vis);
/*      */     }
/*      */     
/* 1059 */     pos++;
/* 1060 */     if ((list.length >= pos + 1) && ((list[pos] instanceof Number))) {
/* 1061 */       int position = ((Number)list[pos]).intValue();
/* 1062 */       setPositionNoShift(position);
/*      */     } else {
/* 1064 */       int position = COConfigurationManager.getIntParameter(oldItemPrefix + ".position", this.iPosition);
/*      */       
/* 1066 */       if ((this.iPosition == -1) && (position != -1)) {
/* 1067 */         setVisible(true);
/*      */       }
/* 1069 */       setPositionNoShift(position);
/* 1070 */       if (position == -1) {
/* 1071 */         setVisible(false);
/*      */       }
/*      */     }
/*      */     
/* 1075 */     pos++;
/* 1076 */     if ((list.length >= pos + 1) && ((list[pos] instanceof Number))) {
/* 1077 */       int width = ((Number)list[pos]).intValue();
/* 1078 */       setWidthPX(width);
/*      */     } else {
/* 1080 */       String key = oldItemPrefix + ".width";
/* 1081 */       if (COConfigurationManager.hasParameter(key, true)) {
/* 1082 */         setWidth(COConfigurationManager.getIntParameter(key));
/*      */       }
/*      */     }
/*      */     
/* 1086 */     pos++;
/* 1087 */     if ((list.length >= pos + 1) && ((list[pos] instanceof Number))) {
/* 1088 */       boolean autoTooltip = ((Number)list[pos]).intValue() == 1;
/* 1089 */       setAutoTooltip(autoTooltip);
/*      */     } else {
/* 1091 */       setAutoTooltip(COConfigurationManager.getBooleanParameter(oldItemPrefix + ".auto_tooltip", this.auto_tooltip));
/*      */     }
/*      */     
/*      */ 
/* 1095 */     pos++;
/* 1096 */     if ((list.length >= pos + 1) && ((list[pos] instanceof Number))) {
/* 1097 */       int sortOrder = ((Number)list[pos]).intValue();
/* 1098 */       if (sortOrder >= 0)
/*      */       {
/*      */ 
/* 1101 */         this.bSortAscending = (sortOrder == 1);
/*      */       }
/*      */     } else {
/* 1104 */       this.bSortAscending = this.bDefaultSortAscending;
/*      */     }
/*      */     
/* 1107 */     pos++;
/* 1108 */     Map loadedUserData; if ((list.length >= pos + 1) && ((list[pos] instanceof Map))) {
/* 1109 */       loadedUserData = (Map)list[pos];
/* 1110 */       if ((this.userData == null) || (this.userData.size() == 0)) {
/* 1111 */         this.userData = loadedUserData;
/*      */       } else {
/* 1113 */         for (Object key : loadedUserData.keySet()) {
/* 1114 */           this.userData.put(key, loadedUserData.get(key));
/*      */         }
/*      */       }
/*      */     }
/* 1118 */     pos++;
/* 1119 */     if ((list.length >= pos + 1) && ((list[pos] instanceof Number))) {
/* 1120 */       int align = ((Number)list[pos]).intValue();
/* 1121 */       setAlignment(align);
/*      */     }
/* 1123 */     this.firstLoad = (list.length == 0);
/* 1124 */     postConfigLoad();
/*      */   }
/*      */   
/*      */   public boolean isFirstLoad() {
/* 1128 */     return this.firstLoad;
/*      */   }
/*      */   
/*      */ 
/*      */   public void postConfigLoad() {}
/*      */   
/*      */ 
/*      */   public void preAdd() {}
/*      */   
/*      */   public void preConfigSave() {}
/*      */   
/*      */   public final void saveSettings(Map mapSettings)
/*      */   {
/* 1141 */     preConfigSave();
/*      */     
/* 1143 */     if (mapSettings == null) {
/* 1144 */       mapSettings = TableColumnManager.getInstance().getTableConfigMap(this.sTableID);
/* 1145 */       if (mapSettings == null) {
/* 1146 */         return;
/*      */       }
/*      */     }
/* 1149 */     String sItemPrefix = "Column." + this.sName;
/* 1150 */     mapSettings.put(sItemPrefix, Arrays.asList(new Object[] { new Integer(this.bVisible ? 1 : 0), new Integer(this.iPosition), new Integer(this.iWidth), new Integer(this.auto_tooltip ? 1 : 0), new Integer(this.bSortAscending ? 1 : this.lLastSortValueChange == 0L ? -1 : 0), this.userData != null ? this.userData : Collections.EMPTY_MAP, new Integer(this.iAlignment == this.iDefaultAlignment ? -1 : this.iAlignment) }));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1160 */     sItemPrefix = "Table." + this.sTableID + "." + this.sName;
/* 1161 */     if (COConfigurationManager.hasParameter(sItemPrefix + ".width", true)) {
/* 1162 */       COConfigurationManager.removeParameter(sItemPrefix + ".position");
/* 1163 */       COConfigurationManager.removeParameter(sItemPrefix + ".width");
/* 1164 */       COConfigurationManager.removeParameter(sItemPrefix + ".auto_tooltip");
/*      */     }
/*      */   }
/*      */   
/*      */   public String getTitleLanguageKey() {
/* 1169 */     return getTitleLanguageKey(true);
/*      */   }
/*      */   
/*      */   public String getTitleLanguageKey(boolean with_renames) {
/*      */     try {
/* 1174 */       this.this_mon.enter();
/*      */       String name_override;
/* 1176 */       String str1; if (with_renames) {
/* 1177 */         name_override = getNameOverride();
/*      */         
/* 1179 */         if (name_override != null)
/*      */         {
/* 1181 */           return "!" + name_override + "!";
/*      */         }
/*      */       }
/*      */       String sKeyPrefix;
/* 1185 */       if (this.sTitleLanguageKey == null) {
/* 1186 */         if (MessageText.keyExists(this.sTableID + ".column." + this.sName)) {
/* 1187 */           this.sTitleLanguageKey = (this.sTableID + ".column." + this.sName);
/* 1188 */           return this.sTitleLanguageKey;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1194 */         sKeyPrefix = (this.sTableID.equals("MySeeders") ? "MyTorrents" : this.sTableID) + "View.";
/*      */         
/*      */ 
/* 1197 */         if (MessageText.keyExists(sKeyPrefix + this.sName)) {
/* 1198 */           this.sTitleLanguageKey = (sKeyPrefix + this.sName);
/* 1199 */           return this.sTitleLanguageKey;
/*      */         }
/*      */         
/*      */ 
/* 1203 */         if (this.sTableID.equals("AllPeers")) {
/* 1204 */           sKeyPrefix = "Peers.column.";
/* 1205 */           if (MessageText.keyExists(sKeyPrefix + this.sName)) {
/* 1206 */             this.sTitleLanguageKey = (sKeyPrefix + this.sName);
/* 1207 */             return this.sTitleLanguageKey;
/*      */           }
/*      */           
/*      */ 
/* 1211 */           sKeyPrefix = "PeersView.";
/* 1212 */           if (MessageText.keyExists(sKeyPrefix + this.sName)) {
/* 1213 */             this.sTitleLanguageKey = (sKeyPrefix + this.sName);
/* 1214 */             return this.sTitleLanguageKey;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 1219 */         sKeyPrefix = "TableColumn.header.";
/* 1220 */         if (MessageText.keyExists(sKeyPrefix + this.sName)) {
/* 1221 */           this.sTitleLanguageKey = (sKeyPrefix + this.sName);
/* 1222 */           return this.sTitleLanguageKey;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1227 */         sKeyPrefix = "MyTorrentsView." + this.sName;
/*      */         
/* 1229 */         if (MessageText.keyExists(sKeyPrefix)) {
/* 1230 */           this.sTitleLanguageKey = sKeyPrefix;
/* 1231 */           return this.sTitleLanguageKey;
/*      */         }
/*      */         
/* 1234 */         this.sTitleLanguageKey = ("!" + this.sName + "!");
/*      */       }
/* 1236 */       return this.sTitleLanguageKey;
/*      */     }
/*      */     finally {
/* 1239 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public int getConsecutiveErrCount() {
/* 1244 */     return this.iConsecutiveErrCount;
/*      */   }
/*      */   
/*      */   public void setConsecutiveErrCount(int iCount) {
/* 1248 */     this.iConsecutiveErrCount = iCount;
/*      */   }
/*      */   
/*      */   public void removeContextMenuItem(TableContextMenuItem menuItem) {
/* 1252 */     if (this.menuItemsColumn != null) {
/* 1253 */       this.menuItemsColumn.remove(menuItem);
/*      */     }
/* 1255 */     if (this.menuItemsHeader != null) {
/* 1256 */       this.menuItemsHeader.remove(menuItem);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/* 1262 */   public TableContextMenuItem addContextMenuItem(String key) { return addContextMenuItem(key, 2); }
/*      */   
/*      */   public TableContextMenuItem addContextMenuItem(String key, int menuStyle) {
/*      */     ArrayList<TableContextMenuItem> menuItems;
/*      */     ArrayList<TableContextMenuItem> menuItems;
/* 1267 */     if (menuStyle == 2) {
/* 1268 */       if (this.menuItemsColumn == null) {
/* 1269 */         this.menuItemsColumn = new ArrayList();
/*      */       }
/* 1271 */       menuItems = this.menuItemsColumn;
/*      */     } else {
/* 1273 */       if (this.menuItemsHeader == null) {
/* 1274 */         this.menuItemsHeader = new ArrayList();
/*      */       }
/* 1276 */       menuItems = this.menuItemsHeader;
/*      */     }
/*      */     
/*      */ 
/* 1280 */     TableContextMenuItemImpl item = new TableContextMenuItemImpl(null, "", key);
/* 1281 */     menuItems.add(item);
/* 1282 */     return item;
/*      */   }
/*      */   
/*      */   public TableContextMenuItem[] getContextMenuItems(int menuStyle) { ArrayList<TableContextMenuItem> menuItems;
/*      */     ArrayList<TableContextMenuItem> menuItems;
/* 1287 */     if (menuStyle == 2) {
/* 1288 */       menuItems = this.menuItemsColumn;
/*      */     } else {
/* 1290 */       menuItems = this.menuItemsHeader;
/*      */     }
/*      */     
/* 1293 */     if (menuItems == null) {
/* 1294 */       return new TableContextMenuItem[0];
/*      */     }
/*      */     
/* 1297 */     return (TableContextMenuItem[])menuItems.toArray(new TableContextMenuItem[0]);
/*      */   }
/*      */   
/*      */   public boolean isObfusticated() {
/* 1301 */     return this.bObfusticateData;
/*      */   }
/*      */   
/*      */   public void setObfustication(boolean hideData) {
/* 1305 */     this.bObfusticateData = hideData;
/*      */   }
/*      */   
/*      */   public long getLastSortValueChange() {
/* 1309 */     if (this.bSortValueLive) {
/* 1310 */       return SystemTime.getCurrentTime();
/*      */     }
/* 1312 */     return this.lLastSortValueChange;
/*      */   }
/*      */   
/*      */   public void setLastSortValueChange(long lastSortValueChange)
/*      */   {
/* 1317 */     this.lLastSortValueChange = lastSortValueChange;
/*      */   }
/*      */   
/*      */   public boolean isSortValueLive() {
/* 1321 */     return this.bSortValueLive;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSortValueLive(boolean live)
/*      */   {
/* 1328 */     this.bSortValueLive = live;
/*      */   }
/*      */   
/*      */   public void addRefreshTime(long ms) {
/* 1332 */     if (ms == 0L) {
/* 1333 */       this.lStatsRefreshZeroCount += 1L;
/*      */     } else {
/* 1335 */       this.lStatsRefreshTotalTime += ms;
/* 1336 */       this.lStatsRefreshCount += 1L;
/*      */     }
/*      */   }
/*      */   
/*      */   public void generateDiagnostics(IndentWriter writer) {
/* 1341 */     writer.println("Column " + this.sTableID + ":" + this.sName + (this.bSortValueLive ? " (Live Sort)" : ""));
/*      */     try
/*      */     {
/* 1344 */       writer.indent();
/*      */       
/* 1346 */       if (this.lStatsRefreshCount > 0L) {
/* 1347 */         writer.println("Avg refresh time (" + this.lStatsRefreshCount + " samples): " + this.lStatsRefreshTotalTime / this.lStatsRefreshCount + " (" + this.lStatsRefreshZeroCount + " zero ms refreshes not included)");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1352 */       writer.println("Listeners: refresh=" + getListCountString(this.cellRefreshListeners) + "; dispose=" + getListCountString(this.cellDisposeListeners) + "; mouse=" + getListCountString(this.cellMouseListeners) + "; mm=" + getListCountString(this.cellMouseMoveListeners) + "; vis=" + getListCountString(this.cellVisibilityListeners) + "; added=" + getListCountString(this.cellAddedListeners) + "; tooltip=" + getListCountString(this.cellToolTipListeners));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1361 */       writer.println("lLastSortValueChange=" + this.lLastSortValueChange);
/*      */     }
/*      */     catch (Exception e) {}finally {
/* 1364 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */   private String getListCountString(List<?> l) {
/* 1369 */     if (l == null) {
/* 1370 */       return "-0";
/*      */     }
/* 1372 */     return "" + l.size();
/*      */   }
/*      */   
/*      */   public void setTableID(String tableID) {
/* 1376 */     this.sTableID = tableID;
/*      */   }
/*      */   
/*      */   public int compare(Object arg0, Object arg1)
/*      */   {
/* 1381 */     TableCellCore cell0 = ((TableRowCore)arg0).getTableCellCore(this.sName);
/* 1382 */     TableCellCore cell1 = ((TableRowCore)arg1).getTableCellCore(this.sName);
/*      */     
/* 1384 */     Comparable c0 = cell0 == null ? "" : cell0.getSortValue();
/* 1385 */     Comparable c1 = cell1 == null ? "" : cell1.getSortValue();
/*      */     
/*      */ 
/* 1388 */     boolean c0_is_null = (c0 == null) || (c0.equals(""));
/* 1389 */     boolean c1_is_null = (c1 == null) || (c1.equals(""));
/* 1390 */     if (c1_is_null) {
/* 1391 */       return c0_is_null ? 0 : -1;
/*      */     }
/* 1393 */     if (c0_is_null) {
/* 1394 */       return 1;
/*      */     }
/*      */     try
/*      */     {
/* 1398 */       boolean c0isString = c0 instanceof String;
/* 1399 */       boolean c1isString = c1 instanceof String;
/* 1400 */       if ((c0isString) && (c1isString)) {
/* 1401 */         if (this.bSortAscending) {
/* 1402 */           return ((String)c0).compareToIgnoreCase((String)c1);
/*      */         }
/*      */         
/* 1405 */         return ((String)c1).compareToIgnoreCase((String)c0);
/*      */       }
/*      */       int val;
/*      */       int val;
/* 1409 */       if ((c0isString) && (!c1isString)) {
/* 1410 */         val = -1; } else { int val;
/* 1411 */         if ((c1isString) && (!c0isString)) {
/* 1412 */           val = 1;
/*      */         } else
/* 1414 */           val = c1.compareTo(c0);
/*      */       }
/* 1416 */       return this.bSortAscending ? -val : val;
/*      */     } catch (ClassCastException e) {
/* 1418 */       int c0_index = cell0 == null ? 64537 : cell0.getTableRowCore().getIndex();
/* 1419 */       int c1_index = cell1 == null ? 64537 : cell1.getTableRowCore().getIndex();
/* 1420 */       System.err.println("Can't compare " + c0.getClass().getName() + "(" + c0.toString() + ") from row #" + c0_index + " to " + c1.getClass().getName() + "(" + c1.toString() + ") from row #" + c1_index + " while sorting column " + this.sName);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1425 */       e.printStackTrace(); }
/* 1426 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setSortAscending(boolean bAscending)
/*      */   {
/* 1434 */     if (this.bSortAscending == bAscending) {
/* 1435 */       return;
/*      */     }
/* 1437 */     setLastSortValueChange(SystemTime.getCurrentTime());
/*      */     
/* 1439 */     this.bSortAscending = bAscending;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isSortAscending()
/*      */   {
/* 1446 */     return this.bSortAscending;
/*      */   }
/*      */   
/*      */   public void setDefaultSortAscending(boolean bAscending) {
/* 1450 */     this.bDefaultSortAscending = bAscending;
/* 1451 */     if (isFirstLoad()) {
/* 1452 */       this.bSortAscending = bAscending;
/*      */     }
/*      */   }
/*      */   
/*      */   public int getMinWidth()
/*      */   {
/* 1458 */     if (this.iMinWidth < 0) {
/* 1459 */       return this.iWidth;
/*      */     }
/*      */     
/* 1462 */     return this.iMinWidth;
/*      */   }
/*      */   
/*      */ 
/*      */   public void setMinWidth(int minwidth)
/*      */   {
/* 1468 */     minwidth = adjustPXForDPI(minwidth);
/* 1469 */     if ((minwidth > this.iMaxWidth) && (this.iMaxWidth >= 0)) {
/* 1470 */       this.iMaxWidth = minwidth;
/*      */     }
/* 1472 */     if ((this.iPreferredWidth > 0) && (this.iPreferredWidth < minwidth)) {
/* 1473 */       this.iPreferredWidth = minwidth;
/*      */     }
/* 1475 */     this.iMinWidth = minwidth;
/* 1476 */     if (this.iWidth < minwidth) {
/* 1477 */       setWidthPX(minwidth);
/*      */     }
/*      */   }
/*      */   
/*      */   public int getMaxWidth()
/*      */   {
/* 1483 */     return this.iMaxWidth;
/*      */   }
/*      */   
/*      */ 
/*      */   public void setMaxWidth(int maxwidth)
/*      */   {
/* 1489 */     maxwidth = adjustPXForDPI(maxwidth);
/* 1490 */     if ((maxwidth >= 0) && (maxwidth < this.iMinWidth)) {
/* 1491 */       this.iMinWidth = maxwidth;
/*      */     }
/* 1493 */     if (this.iPreferredWidth > maxwidth) {
/* 1494 */       this.iPreferredWidth = maxwidth;
/*      */     }
/* 1496 */     this.iMaxWidth = maxwidth;
/* 1497 */     if ((maxwidth >= 0) && (this.iWidth > this.iMaxWidth)) {
/* 1498 */       setWidthPX(maxwidth);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setWidthLimits(int min, int max)
/*      */   {
/* 1504 */     setMinWidth(min);
/* 1505 */     setMaxWidth(max);
/*      */   }
/*      */   
/*      */   public boolean isVisible()
/*      */   {
/* 1510 */     return this.bVisible;
/*      */   }
/*      */   
/*      */   public void setVisible(boolean visible)
/*      */   {
/* 1515 */     if (this.bVisible == visible) {
/* 1516 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1522 */     this.bVisible = visible;
/* 1523 */     if ((this.bVisible) && (this.iPosition == -1)) {
/* 1524 */       TableColumnCore[] allColumns = TableColumnManager.getInstance().getAllTableColumnCoreAsArray(null, this.sTableID);
/*      */       
/* 1526 */       this.iPosition = 0;
/* 1527 */       for (TableColumnCore tableColumnCore : allColumns) {
/* 1528 */         if (tableColumnCore.getPosition() > this.iPosition) {
/* 1529 */           this.iPosition = (tableColumnCore.getPosition() + 1);
/*      */         }
/*      */       }
/*      */     }
/* 1533 */     invalidateCells();
/*      */   }
/*      */   
/*      */   public boolean isMaxWidthAuto()
/*      */   {
/* 1538 */     return this.bMaxWidthAuto;
/*      */   }
/*      */   
/*      */   public void setMaxWidthAuto(boolean automaxwidth)
/*      */   {
/* 1543 */     this.bMaxWidthAuto = automaxwidth;
/*      */   }
/*      */   
/*      */   public boolean isMinWidthAuto()
/*      */   {
/* 1548 */     return this.bWidthAuto;
/*      */   }
/*      */   
/*      */   public void setMinWidthAuto(boolean autominwidth)
/*      */   {
/* 1553 */     this.bWidthAuto = autominwidth;
/*      */   }
/*      */   
/*      */   public int getPreferredWidth()
/*      */   {
/* 1558 */     return this.iPreferredWidth;
/*      */   }
/*      */   
/*      */   public void setPreferredWidthAuto(boolean auto)
/*      */   {
/* 1563 */     this.bPreferredWidthAuto = auto;
/*      */   }
/*      */   
/*      */   public boolean isPreferredWidthAuto()
/*      */   {
/* 1568 */     return this.bPreferredWidthAuto;
/*      */   }
/*      */   
/*      */   public void setPreferredWidthMax(int maxprefwidth)
/*      */   {
/* 1573 */     this.iPreferredWidthMax = maxprefwidth;
/* 1574 */     if (this.iPreferredWidth > this.iPreferredWidthMax) {
/* 1575 */       setPreferredWidth(maxprefwidth);
/*      */     }
/*      */   }
/*      */   
/*      */   public int getPreferredWidthMax()
/*      */   {
/* 1581 */     return this.iPreferredWidthMax;
/*      */   }
/*      */   
/*      */   public void setPreferredWidth(int width)
/*      */   {
/* 1586 */     if ((this.iPreferredWidthMax > 0) && (width > this.iPreferredWidthMax)) {
/* 1587 */       width = this.iPreferredWidthMax;
/*      */     }
/*      */     
/* 1590 */     if (width < this.iMinWidth) {
/* 1591 */       this.iPreferredWidth = this.iMinWidth;
/* 1592 */     } else if ((this.iMaxWidth > 0) && (width > this.iMaxWidth)) {
/* 1593 */       this.iPreferredWidth = this.iMaxWidth;
/*      */     } else {
/* 1595 */       this.iPreferredWidth = width;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setAutoTooltip(boolean auto_tooltip)
/*      */   {
/* 1605 */     this.auto_tooltip = auto_tooltip;
/*      */   }
/*      */   
/*      */   public boolean doesAutoTooltip() {
/* 1609 */     return this.auto_tooltip;
/*      */   }
/*      */   
/*      */   public void setInplaceEditorListener(TableCellInplaceEditorListener l) {
/* 1613 */     this.cellEditorListener = l;
/*      */   }
/*      */   
/*      */   public boolean hasInplaceEditorListener() {
/* 1617 */     return this.cellEditorListener != null;
/*      */   }
/*      */   
/*      */   public TableCellInplaceEditorListener getInplaceEditorListener() {
/* 1621 */     return this.cellEditorListener;
/*      */   }
/*      */   
/*      */   public Class[] getForDataSourceTypes() {
/* 1625 */     if (this.forPluginDataSourceTypes.isEmpty())
/*      */     {
/* 1627 */       Class<?> forPluginDataSourceType = null;
/* 1628 */       if (("MyLibrary.big".equals(this.sTableID)) || ("Unopened".equals(this.sTableID)) || ("Unopened.big".equals(this.sTableID)))
/*      */       {
/*      */ 
/* 1631 */         forPluginDataSourceType = Download.class;
/* 1632 */       } else if (("MyTorrents.big".equals(this.sTableID)) || ("MyTorrents".equals(this.sTableID)))
/*      */       {
/* 1634 */         forPluginDataSourceType = DownloadTypeIncomplete.class;
/* 1635 */       } else if (("MySeeders".equals(this.sTableID)) || ("MySeeders.big".equals(this.sTableID)))
/*      */       {
/* 1637 */         forPluginDataSourceType = DownloadTypeComplete.class;
/* 1638 */       } else if ("Peers".equals(this.sTableID)) {
/* 1639 */         forPluginDataSourceType = Peer.class;
/* 1640 */       } else if ("Files".equals(this.sTableID)) {
/* 1641 */         forPluginDataSourceType = org.gudy.azureus2.plugins.disk.DiskManagerFileInfo.class;
/* 1642 */       } else if ("MyTracker".equals(this.sTableID)) {
/* 1643 */         forPluginDataSourceType = TrackerTorrent.class;
/* 1644 */       } else if ("MyShares".equals(this.sTableID)) {
/* 1645 */         forPluginDataSourceType = ShareResource.class;
/*      */       }
/* 1647 */       if (forPluginDataSourceType != null) {
/* 1648 */         this.forPluginDataSourceTypes.add(forPluginDataSourceType);
/*      */       }
/*      */     }
/* 1651 */     return (Class[])this.forPluginDataSourceTypes.toArray(new Class[0]);
/*      */   }
/*      */   
/*      */   public void reset() {
/* 1655 */     if (this.iDefaultWidth != 0) {
/* 1656 */       setWidthPX(this.iDefaultWidth);
/*      */     }
/* 1658 */     if (this.iDefaultAlignment != -1) {
/* 1659 */       setAlignment(this.iDefaultAlignment);
/*      */     }
/* 1661 */     setNameOverride(null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addDataSourceType(Class<?> cla)
/*      */   {
/* 1668 */     if (cla == null) {
/* 1669 */       return;
/*      */     }
/* 1671 */     if (cla == org.gudy.azureus2.core3.disk.DiskManagerFileInfo.class) {
/* 1672 */       cla = org.gudy.azureus2.plugins.disk.DiskManagerFileInfo.class;
/*      */     }
/* 1674 */     this.forPluginDataSourceTypes.add(cla);
/*      */   }
/*      */   
/*      */   public void addDataSourceTypes(Class[] datasourceTypes) {
/* 1678 */     if (datasourceTypes == null) {
/* 1679 */       return;
/*      */     }
/* 1681 */     for (Class cla : datasourceTypes) {
/* 1682 */       addDataSourceType(cla);
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean handlesDataSourceType(Class<?> cla) {
/* 1687 */     Class[] forPluginDataSourceTypes = getForDataSourceTypes();
/* 1688 */     for (Class<?> forClass : forPluginDataSourceTypes) {
/* 1689 */       if (forClass.isAssignableFrom(cla)) {
/* 1690 */         return true;
/*      */       }
/*      */     }
/* 1693 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Class getForDataSourceType()
/*      */   {
/* 1700 */     Class[] forPluginDataSourceTypes = getForDataSourceTypes();
/* 1701 */     return forPluginDataSourceTypes.length > 0 ? forPluginDataSourceTypes[0] : null;
/*      */   }
/*      */   
/*      */   public void setIconReference(String iconID, boolean showOnlyIcon) {
/* 1705 */     this.iconID = iconID;
/* 1706 */     this.showOnlyImage = showOnlyIcon;
/*      */   }
/*      */   
/*      */   public String getIconReference() {
/* 1710 */     return this.iconID;
/*      */   }
/*      */   
/*      */   public void setMinimumRequiredUserMode(int mode)
/*      */   {
/* 1715 */     TableColumnInfo info = TableColumnManager.getInstance().getColumnInfo(this);
/*      */     
/* 1717 */     if (info != null) { byte prof;
/*      */       byte prof;
/* 1719 */       if (mode == 0) {
/* 1720 */         prof = 0; } else { byte prof;
/* 1721 */         if (mode == 1) {
/* 1722 */           prof = 1;
/*      */         } else
/* 1724 */           prof = 2;
/*      */       }
/* 1726 */       info.setProficiency(prof);
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean showOnlyImage()
/*      */   {
/* 1732 */     return this.showOnlyImage;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/table/impl/TableColumnImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */