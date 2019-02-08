/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import java.text.NumberFormat;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellToolTipListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
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
/*     */ public class SizeItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener, TableCellToolTipListener
/*     */ {
/*  49 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*     */   public static final String COLUMN_ID = "size";
/*     */   
/*  53 */   private static boolean DO_MULTILINE = true;
/*     */   
/*     */   public SizeItem(String sTableID)
/*     */   {
/*  57 */     super(DATASOURCE_TYPE, "size", 2, 70, sTableID);
/*  58 */     addDataSourceType(DiskManagerFileInfo.class);
/*  59 */     setRefreshInterval(-1);
/*  60 */     setMinWidthAuto(true);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  64 */     info.addCategories(new String[] { "essential", "content", "bytes" });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  69 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell)
/*     */   {
/*  74 */     Object ds = cell.getDataSource();
/*  75 */     sizeitemsort value; if ((ds instanceof DownloadManager)) {
/*  76 */       DownloadManager dm = (DownloadManager)ds;
/*     */       
/*  78 */       value = new sizeitemsort(dm.getStats().getSizeExcludingDND(), dm.getStats().getRemainingExcludingDND());
/*     */     } else { sizeitemsort value;
/*  80 */       if ((ds instanceof DiskManagerFileInfo)) {
/*  81 */         DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)ds;
/*  82 */         value = new sizeitemsort(fileInfo.getLength(), fileInfo.getLength() - fileInfo.getDownloaded());
/*     */       }
/*     */       else
/*     */       {
/*     */         return;
/*     */       }
/*     */     }
/*     */     sizeitemsort value;
/*  90 */     if ((value.compareTo(cell.getSortValue()) == 0) && (cell.isValid())) {
/*  91 */       return;
/*     */     }
/*  93 */     cell.setSortValue(value);
/*     */     
/*  95 */     String s = DisplayFormatters.formatCustomSize("column.size", value.size);
/*     */     
/*  97 */     if (s == null)
/*     */     {
/*  99 */       s = DisplayFormatters.formatByteCountToKiBEtc(value.size);
/*     */     }
/*     */     
/* 102 */     if ((DO_MULTILINE) && (cell.getMaxLines() > 1) && (value.remaining > 0L)) {
/* 103 */       s = s + "\n" + DisplayFormatters.formatByteCountToKiBEtc(value.remaining, false, false, 0) + " " + MessageText.getString("TableColumn.header.remaining");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 108 */     cell.setText(s);
/*     */     
/* 110 */     if ((Utils.getUserMode() > 0) && ((cell instanceof TableCellSWT))) {
/* 111 */       if (value.size >= 1073741824L) {
/* 112 */         ((TableCellSWT)cell).setTextAlpha(456);
/* 113 */       } else if (value.size < 1048576L) {
/* 114 */         ((TableCellSWT)cell).setTextAlpha(180);
/*     */       } else {
/* 116 */         ((TableCellSWT)cell).setTextAlpha(255);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static class sizeitemsort
/*     */     implements Comparable
/*     */   {
/*     */     private final long size;
/*     */     private final long remaining;
/*     */     
/*     */     public sizeitemsort(long size, long remaining)
/*     */     {
/* 129 */       this.size = size;
/* 130 */       this.remaining = remaining;
/*     */     }
/*     */     
/*     */     public int compareTo(Object arg0) {
/* 134 */       if (!(arg0 instanceof sizeitemsort)) {
/* 135 */         return 1;
/*     */       }
/*     */       
/* 138 */       sizeitemsort otherObj = (sizeitemsort)arg0;
/* 139 */       if (this.size == otherObj.size) {
/* 140 */         return this.remaining > otherObj.remaining ? 1 : this.remaining == otherObj.remaining ? 0 : -1;
/*     */       }
/*     */       
/* 143 */       return this.size > otherObj.size ? 1 : -1;
/*     */     }
/*     */   }
/*     */   
/*     */   public void cellHover(TableCell cell) {
/* 148 */     Comparable sortValue = cell.getSortValue();
/* 149 */     if (!(sortValue instanceof sizeitemsort)) {
/* 150 */       return;
/*     */     }
/* 152 */     sizeitemsort value = (sizeitemsort)sortValue;
/* 153 */     String tooltip = NumberFormat.getInstance().format(value.size) + " " + MessageText.getString("DHTView.transport.bytes");
/*     */     
/* 155 */     if (value.remaining > 0L) {
/* 156 */       tooltip = tooltip + "\n" + DisplayFormatters.formatByteCountToKiBEtc(value.remaining, false, false) + " " + MessageText.getString("TableColumn.header.remaining");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 161 */     Object ds = cell.getDataSource();
/* 162 */     if ((ds instanceof DownloadManager)) {
/* 163 */       DownloadManager dm = (DownloadManager)ds;
/* 164 */       long fullSize = dm.getSize();
/* 165 */       if (fullSize > value.size) {
/* 166 */         tooltip = tooltip + "\n" + DisplayFormatters.formatByteCountToKiBEtc(fullSize - value.size, false, false) + " " + MessageText.getString("FileView.BlockView.Skipped");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 173 */     cell.setToolTip(tooltip);
/*     */   }
/*     */   
/*     */   public void cellHoverComplete(TableCell cell) {
/* 177 */     cell.setToolTip(null);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/SizeItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */