/*    */ package com.aelitis.azureus.ui.swt.columns.tag;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.Tag;
/*    */ import com.aelitis.azureus.core.tag.TagFeatureRateLimit;
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
/*    */ public class ColumnTagDownRate
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 29 */   public static String COLUMN_ID = "tag.downrate";
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 35 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/*    */ 
/* 39 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnTagDownRate(TableColumn column)
/*    */   {
/* 46 */     column.setWidth(60);
/* 47 */     column.setRefreshInterval(-2);
/* 48 */     column.setAlignment(2);
/* 49 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 53 */     Tag tag = (Tag)cell.getDataSource();
/* 54 */     if ((tag instanceof TagFeatureRateLimit)) {
/* 55 */       TagFeatureRateLimit rl = (TagFeatureRateLimit)tag;
/*    */       
/* 57 */       if (rl.supportsTagRates())
/*    */       {
/* 59 */         int sortVal = rl.getTagCurrentDownloadRate();
/*    */         
/* 61 */         if ((!cell.setSortValue(sortVal)) && (cell.isValid())) {
/* 62 */           return;
/*    */         }
/*    */         
/* 65 */         if (!cell.isShown()) {
/* 66 */           return;
/*    */         }
/*    */         
/* 69 */         cell.setText(sortVal < 0 ? "-" : DisplayFormatters.formatByteCountToKiBEtcPerSec(sortVal));
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tag/ColumnTagDownRate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */