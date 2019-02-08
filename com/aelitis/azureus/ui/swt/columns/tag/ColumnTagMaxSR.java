/*    */ package com.aelitis.azureus.ui.swt.columns.tag;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.Tag;
/*    */ import com.aelitis.azureus.core.tag.TagFeatureRateLimit;
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
/*    */ public class ColumnTagMaxSR
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 40 */   public static String COLUMN_ID = "max_sr";
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 46 */     info.addCategories(new String[] { "sharing" });
/*    */     
/*    */ 
/*    */ 
/* 50 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnTagMaxSR(TableColumn column)
/*    */   {
/* 57 */     column.setWidth(60);
/* 58 */     column.setRefreshInterval(-2);
/* 59 */     column.setAlignment(2);
/* 60 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 64 */     Tag tag = (Tag)cell.getDataSource();
/* 65 */     if ((tag instanceof TagFeatureRateLimit)) {
/* 66 */       TagFeatureRateLimit rl = (TagFeatureRateLimit)tag;
/*    */       
/* 68 */       int sr = rl.getTagMaxShareRatio();
/*    */       
/* 70 */       if (sr >= 0)
/*    */       {
/* 72 */         if ((!cell.setSortValue(sr)) && (cell.isValid())) {
/* 73 */           return;
/*    */         }
/*    */         
/* 76 */         if (!cell.isShown()) {
/* 77 */           return;
/*    */         }
/*    */         
/* 80 */         cell.setText(sr == 0 ? "" : String.valueOf(sr / 1000.0F));
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tag/ColumnTagMaxSR.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */