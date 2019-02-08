/*     */ package com.aelitis.azureus.ui.swt.columns.torrent;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableCellCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.TorrentListViewsUtils;
/*     */ import com.aelitis.azureus.util.PlayUtils;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.plugins.download.DownloadTypeIncomplete;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellToolTipListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWTPaintListener;
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
/*     */ public class ColumnStream
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellSWTPaintListener, TableCellAddedListener, TableCellRefreshListener, TableCellMouseListener, TableCellToolTipListener
/*     */ {
/*     */   public static final String COLUMN_ID = "TorrentStream";
/*  55 */   public static final Class[] DATASOURCE_TYPES = { DownloadTypeIncomplete.class, DiskManagerFileInfo.class };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  60 */   private static int WIDTH = 62;
/*     */   
/*     */   private static Image imgGreen;
/*     */   
/*     */   private static Image imgDisabled;
/*     */   
/*     */   private static Image imgBlue;
/*     */   
/*     */   private static Image imgGreenSmall;
/*     */   
/*     */   private static Image imgDisabledSmall;
/*     */   
/*     */   private static Image imgBlueSmall;
/*     */   
/*  74 */   private static Object firstLock = new Object();
/*     */   
/*  76 */   private static boolean first = true;
/*     */   
/*  78 */   private static boolean skipPaint = true;
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  81 */     info.addCategories(new String[] { "essential", "content" });
/*     */     
/*     */ 
/*     */ 
/*  85 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ColumnStream(String tableID)
/*     */   {
/*  93 */     super("TorrentStream", tableID);
/*  94 */     addDataSourceTypes(DATASOURCE_TYPES);
/*     */     
/*  96 */     initializeAsGraphic(WIDTH);
/*  97 */     setAlignment(3);
/*     */     
/*  99 */     synchronized (ColumnStream.class) {
/* 100 */       if (imgGreen == null) {
/* 101 */         imgGreen = ImageLoader.getInstance().getImage("column.image.play.green");
/* 102 */         imgDisabled = ImageLoader.getInstance().getImage("column.image.play.off");
/* 103 */         imgBlue = ImageLoader.getInstance().getImage("column.image.play.blue");
/* 104 */         imgGreenSmall = ImageLoader.getInstance().getImage("column.image.play.green.small");
/* 105 */         imgDisabledSmall = ImageLoader.getInstance().getImage("column.image.play.off.small");
/* 106 */         imgBlueSmall = ImageLoader.getInstance().getImage("column.image.play.blue.small");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void preAdd()
/*     */   {
/* 113 */     if ((!isFirstLoad()) || (getPosition() >= 0) || (getColumnAdded())) {
/* 114 */       return;
/*     */     }
/* 116 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/* 117 */     TableColumnInfo columnInfoTAN = tcManager.getColumnInfo(null, getTableID(), "name");
/*     */     
/* 119 */     if (columnInfoTAN != null) {
/* 120 */       TableColumn column = columnInfoTAN.getColumn();
/* 121 */       if (column != null) {
/* 122 */         int position = column.getPosition();
/* 123 */         if (position >= 0) {
/* 124 */           setPosition(position + 1);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean noIconForYou(Object ds, TableCell cell) {
/* 131 */     if (!(ds instanceof DownloadManager)) {
/* 132 */       return false;
/*     */     }
/* 134 */     if (!(cell instanceof TableCellCore)) {
/* 135 */       return false;
/*     */     }
/* 137 */     DownloadManager dm = (DownloadManager)ds;
/* 138 */     TableRowCore rowCore = ((TableCellCore)cell).getTableRowCore();
/* 139 */     if (rowCore == null) {
/* 140 */       return false;
/*     */     }
/*     */     
/* 143 */     if ((dm.getNumFileInfos() > 1) && (rowCore.isExpanded())) {
/* 144 */       return true;
/*     */     }
/* 146 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void cellPaint(GC gc, TableCellSWT cell)
/*     */   {
/* 152 */     Object ds = cell.getDataSource();
/* 153 */     if (noIconForYou(ds, cell)) {
/* 154 */       return;
/*     */     }
/*     */     
/* 157 */     Comparable sortValue = cell.getSortValue();
/* 158 */     if (!(sortValue instanceof Number)) {
/* 159 */       return;
/*     */     }
/* 161 */     int sortVal = ((Number)sortValue).intValue();
/* 162 */     boolean canStream = (sortVal & 0x2) > 0;
/* 163 */     boolean canPlay = (sortVal & 0x1) > 0;
/*     */     
/* 165 */     Image img = canPlay ? imgGreenSmall : canStream ? imgBlueSmall : cell.getHeight() > 18 ? imgDisabled : canPlay ? imgGreen : canStream ? imgBlue : imgDisabledSmall;
/*     */     
/*     */ 
/* 168 */     Rectangle cellBounds = cell.getBounds();
/*     */     
/* 170 */     if ((img != null) && (!img.isDisposed())) {
/* 171 */       Rectangle imgBounds = img.getBounds();
/* 172 */       gc.drawImage(img, cellBounds.x + (cellBounds.width - imgBounds.width) / 2, cellBounds.y + (cellBounds.height - imgBounds.height) / 2);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void cellAdded(final TableCell cell)
/*     */   {
/* 180 */     cell.setMarginWidth(0);
/* 181 */     cell.setMarginHeight(0);
/*     */     
/* 183 */     synchronized (firstLock) {
/* 184 */       if (first) {
/* 185 */         first = false;
/* 186 */         new AEThread2("WaitForMS", true) {
/*     */           public void run() {
/* 188 */             Object ds = cell.getDataSource();
/*     */             
/* 190 */             PlayUtils.canStreamDS(ds, -1, true);
/* 191 */             ColumnStream.access$002(false);
/*     */           }
/*     */         };
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void refresh(TableCell cell)
/*     */   {
/* 201 */     Object ds = cell.getDataSource();
/* 202 */     int sortVal; int sortVal; if (noIconForYou(ds, cell)) {
/* 203 */       sortVal = 0;
/*     */     } else {
/* 205 */       boolean canStream = PlayUtils.canStreamDS(ds, -1, false);
/* 206 */       boolean canPlay = PlayUtils.canPlayDS(ds, -1, false);
/* 207 */       sortVal = (canStream ? 2 : 0) + (canPlay ? 1 : 0);
/*     */     }
/*     */     
/* 210 */     if (cell.setSortValue(sortVal)) {
/* 211 */       cell.invalidate();
/*     */     }
/*     */   }
/*     */   
/*     */   public void cellMouseTrigger(TableCellMouseEvent event)
/*     */   {
/* 217 */     if ((event.eventType == 0) && (event.button == 1))
/*     */     {
/* 219 */       Object ds = event.cell.getDataSource();
/* 220 */       if ((PlayUtils.canStreamDS(ds, -1, true)) || (PlayUtils.canPlayDS(ds, -1, true))) {
/* 221 */         TorrentListViewsUtils.playOrStreamDataSource(ds, "column", true, false);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void cellHover(TableCell cell)
/*     */   {
/* 228 */     Object ds = cell.getDataSource();
/* 229 */     if (noIconForYou(ds, cell)) {
/* 230 */       cell.setToolTip(null);
/* 231 */       return;
/*     */     }
/* 233 */     if ((PlayUtils.canStreamDS(ds, -1, false)) || (PlayUtils.canPlayDS(ds, -1, false))) {
/* 234 */       cell.setToolTip(null);
/* 235 */       return;
/*     */     }
/* 237 */     String id = "TableColumn.TorrentStream.tooltip.disabled";
/* 238 */     if (((ds instanceof DownloadManager)) && (((DownloadManager)ds).getNumFileInfos() > 1)) {
/* 239 */       id = "TableColumn.TorrentStream.tooltip.expand";
/*     */     }
/*     */     
/* 242 */     cell.setToolTip(MessageText.getString(id));
/*     */   }
/*     */   
/*     */   public void cellHoverComplete(TableCell cell)
/*     */   {
/* 247 */     cell.setToolTip(null);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/torrent/ColumnStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */