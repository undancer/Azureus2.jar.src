/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*     */ import org.gudy.azureus2.plugins.download.DownloadTypeComplete;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.views.table.utils.TableColumnCreator;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.ColumnDateSizer;
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
/*     */ public class DateCompletedItem
/*     */   extends ColumnDateSizer
/*     */ {
/*  37 */   public static final Class DATASOURCE_TYPE = DownloadTypeComplete.class;
/*     */   
/*     */   public static final String COLUMN_ID = "DateCompleted";
/*     */   private static final long SHOW_ETA_AFTER_MS = 30000L;
/*     */   
/*     */   public DateCompletedItem(String sTableID)
/*     */   {
/*  44 */     super(DATASOURCE_TYPE, "DateCompleted", TableColumnCreator.DATE_COLUMN_WIDTH, sTableID);
/*     */     
/*  46 */     setMultiline(false);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  50 */     info.addCategories(new String[] { "time", "content" });
/*     */     
/*     */ 
/*     */ 
/*  54 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateCompletedItem(String tableID, boolean v)
/*     */   {
/*  62 */     this(tableID);
/*  63 */     setVisible(v);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell, long timestamp) {
/*  67 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*  68 */     long value = 0L;
/*  69 */     if (dm == null) {
/*  70 */       return;
/*     */     }
/*  72 */     if (dm.isDownloadComplete(false)) {
/*  73 */       long completedTime = dm.getDownloadState().getLongParameter("stats.download.completed.time");
/*     */       
/*  75 */       if (completedTime <= 0L) {
/*  76 */         value = dm.getDownloadState().getLongParameter("stats.download.added.time");
/*     */       }
/*     */       else {
/*  79 */         value = completedTime;
/*     */       }
/*     */     } else {
/*  82 */       long diff = SystemTime.getCurrentTime() - dm.getStats().getTimeStarted();
/*  83 */       if (diff > 30000L) {
/*  84 */         long eta = dm.getStats().getSmoothedETA();
/*  85 */         if (eta > 0L) {
/*  86 */           String sETA = TimeFormatter.format(eta);
/*  87 */           value = eta << 42;
/*  88 */           if (value < 0L) {
/*  89 */             value = Long.MAX_VALUE;
/*     */           }
/*  91 */           cell.setText(MessageText.getString("MyTorrents.column.ColumnProgressETA.2ndLine", new String[] { sETA }));
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*  96 */           cell.setText("");
/*     */           
/*  98 */           value = SystemTime.getCurrentTime() / 1000L * 1001L;
/*     */         }
/*     */       } else {
/* 101 */         cell.setText("");
/* 102 */         value = SystemTime.getCurrentTime() / 1000L * 1002L;
/*     */       }
/*     */       
/* 105 */       cell.invalidate();
/*     */       
/* 107 */       cell.setSortValue(value);
/* 108 */       return;
/*     */     }
/*     */     
/* 111 */     super.refresh(cell, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void cellHover(TableCell cell)
/*     */   {
/* 119 */     super.cellHover(cell);
/* 120 */     Object oTooltip = cell.getToolTip();
/* 121 */     String s = (oTooltip instanceof String) ? (String)oTooltip + "\n" : "";
/* 122 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 123 */     long dateAdded = dm == null ? 0L : dm.getDownloadState().getLongParameter("stats.download.added.time");
/*     */     
/* 125 */     if (dateAdded != 0L) {
/* 126 */       s = s + MessageText.getString("TableColumn.header.date_added") + ": " + DisplayFormatters.formatDate(dateAdded) + " (" + DisplayFormatters.formatETA((SystemTime.getCurrentTime() - dateAdded) / 1000L, false) + ")";
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 131 */       cell.setToolTip(s);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/DateCompletedItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */