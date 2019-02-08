/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.tracker;
/*     */ 
/*     */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
/*     */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
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
/*     */ public class IntervalItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener
/*     */ {
/*     */   public IntervalItem(String tableID)
/*     */   {
/*  40 */     super("interval", 3, -2, 75, tableID);
/*     */     
/*  42 */     setRefreshInterval(-1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void fillTableColumnInfo(TableColumnInfo info)
/*     */   {
/*  49 */     info.addCategories(new String[] { "essential" });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void refresh(TableCell cell)
/*     */   {
/*  58 */     TrackerPeerSource ps = (TrackerPeerSource)cell.getDataSource();
/*     */     
/*  60 */     long interval = 0L;
/*  61 */     long min_interval = 0L;
/*     */     
/*  63 */     if (ps != null)
/*     */     {
/*  65 */       interval = ps.getInterval();
/*  66 */       min_interval = ps.getMinInterval();
/*     */     }
/*     */     
/*  69 */     long sort = interval << 31 | min_interval & 0xFFFFFFFF;
/*     */     
/*  71 */     if ((!cell.setSortValue(sort)) && (cell.isValid())) {
/*     */       return;
/*     */     }
/*     */     
/*     */     String str;
/*     */     
/*     */     String str;
/*  78 */     if ((interval <= 0L) && (min_interval <= 0L))
/*     */     {
/*  80 */       str = "";
/*     */     } else { String str;
/*  82 */       if (interval <= 0L)
/*     */       {
/*  84 */         str = "(" + format(min_interval) + ")";
/*     */       } else { String str;
/*  86 */         if (min_interval <= 0L)
/*     */         {
/*  88 */           str = format(interval);
/*     */         }
/*     */         else
/*     */         {
/*  92 */           str = format(interval) + " (" + format(min_interval) + ")"; }
/*     */       }
/*     */     }
/*  95 */     cell.setText(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String format(long secs)
/*     */   {
/* 102 */     return TimeFormatter.format2(secs, (secs < 300L) && (secs % 60L != 0L));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/tracker/IntervalItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */