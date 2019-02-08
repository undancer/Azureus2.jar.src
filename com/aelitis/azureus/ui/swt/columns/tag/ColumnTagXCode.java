/*    */ package com.aelitis.azureus.ui.swt.columns.tag;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.Tag;
/*    */ import com.aelitis.azureus.core.tag.TagFeatureTranscode;
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
/*    */ public class ColumnTagXCode
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 28 */   public static String COLUMN_ID = "tag.xcode";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 31 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 34 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnTagXCode(TableColumn column)
/*    */   {
/* 39 */     column.setWidth(200);
/* 40 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 44 */     Tag tag = (Tag)cell.getDataSource();
/* 45 */     if ((tag instanceof TagFeatureTranscode)) {
/* 46 */       TagFeatureTranscode xcode = (TagFeatureTranscode)tag;
/*    */       
/* 48 */       if (xcode.supportsTagTranscode())
/*    */       {
/* 50 */         String[] target_details = xcode.getTagTranscodeTarget();
/*    */         
/*    */         String target;
/*    */         String target;
/* 54 */         if ((target_details == null) || (target_details.length < 2)) {
/* 55 */           target = "";
/*    */         } else {
/* 57 */           target = target_details[1];
/*    */         }
/*    */         
/* 60 */         if ((!cell.setSortValue(target)) && (cell.isValid())) {
/* 61 */           return;
/*    */         }
/*    */         
/* 64 */         if (!cell.isShown()) {
/* 65 */           return;
/*    */         }
/*    */         
/* 68 */         cell.setText(target);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tag/ColumnTagXCode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */