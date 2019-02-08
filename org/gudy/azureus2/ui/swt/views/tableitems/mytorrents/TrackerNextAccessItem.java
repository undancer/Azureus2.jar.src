/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellDisposeListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellToolTipListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
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
/*     */ public class TrackerNextAccessItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener, TableCellDisposeListener, TableCellToolTipListener
/*     */ {
/*  43 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*     */   public static final String COLUMN_ID = "trackernextaccess";
/*  46 */   HashMap map = new HashMap();
/*     */   
/*     */   public TrackerNextAccessItem(String sTableID) {
/*  49 */     super(DATASOURCE_TYPE, "trackernextaccess", 2, 70, sTableID);
/*  50 */     setRefreshInterval(-2);
/*  51 */     setMinWidthAuto(true);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  55 */     info.addCategories(new String[] { "tracker", "time" });
/*     */     
/*     */ 
/*     */ 
/*  59 */     info.setProficiency((byte)1);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/*  63 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*  64 */     if ((cell.isValid()) && (this.map.containsKey(dm))) {
/*  65 */       long lNextUpdate = ((Long)this.map.get(dm)).longValue();
/*  66 */       if (System.currentTimeMillis() < lNextUpdate)
/*  67 */         return;
/*     */     }
/*  69 */     long value = dm == null ? 0L : dm.getTrackerTime();
/*     */     
/*  71 */     if (value < -1L) {
/*  72 */       value = -1L;
/*     */     }
/*  74 */     long lNextUpdate = System.currentTimeMillis() + (value > 60L ? value % 60L : 1L) * 1000L;
/*     */     
/*  76 */     this.map.put(dm, new Long(lNextUpdate));
/*     */     
/*  78 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/*  79 */       return;
/*     */     }
/*  81 */     String sText = TimeFormatter.formatColon(value);
/*     */     
/*  83 */     if (value > 60L) {
/*  84 */       sText = "< " + sText;
/*     */     }
/*  86 */     TrackerCellUtils.updateColor(cell, dm, false);
/*  87 */     cell.setText(sText);
/*     */   }
/*     */   
/*     */   public void cellHover(TableCell cell) {
/*  91 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*  92 */     cell.setToolTip(TrackerCellUtils.getTooltipText(cell, dm, false));
/*     */   }
/*     */   
/*     */   public void cellHoverComplete(TableCell cell) {
/*  96 */     cell.setToolTip(null);
/*     */   }
/*     */   
/*     */   public void dispose(TableCell cell) {
/* 100 */     this.map.remove(cell.getDataSource());
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/TrackerNextAccessItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */