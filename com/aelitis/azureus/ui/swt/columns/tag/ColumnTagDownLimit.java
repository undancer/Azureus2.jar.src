/*    */ package com.aelitis.azureus.ui.swt.columns.tag;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.Tag;
/*    */ import com.aelitis.azureus.core.tag.TagFeatureRateLimit;
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ColumnTagDownLimit
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 31 */   public static String COLUMN_ID = "tag.downlimit";
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 37 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/*    */ 
/* 41 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnTagDownLimit(TableColumn column)
/*    */   {
/* 48 */     column.setWidth(60);
/* 49 */     column.setRefreshInterval(-2);
/* 50 */     column.setAlignment(2);
/* 51 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 55 */     Tag tag = (Tag)cell.getDataSource();
/* 56 */     if ((tag instanceof TagFeatureRateLimit)) {
/* 57 */       TagFeatureRateLimit rl = (TagFeatureRateLimit)tag;
/*    */       
/* 59 */       if (rl.supportsTagDownloadLimit())
/*    */       {
/* 61 */         int sortVal = rl.getTagDownloadLimit();
/*    */         
/* 63 */         if ((!cell.setSortValue(sortVal)) && (cell.isValid())) {
/* 64 */           return;
/*    */         }
/*    */         
/* 67 */         if (!cell.isShown()) {
/* 68 */           return;
/*    */         }
/*    */         
/* 71 */         cell.setText(sortVal == 0 ? "∞" : sortVal == -1 ? MessageText.getString("MyTorrentsView.menu.setSpeed.disabled") : DisplayFormatters.formatByteCountToKiBEtcPerSec(sortVal));
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tag/ColumnTagDownLimit.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */