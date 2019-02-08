/*    */ package com.aelitis.azureus.ui.swt.columns.tag;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.Tag;
/*    */ import com.aelitis.azureus.core.tag.TagFeatureFileLocation;
/*    */ import java.io.File;
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
/*    */ public class ColumnTagCopyOnComp
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 30 */   public static String COLUMN_ID = "tag.copyoncomp";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 33 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 36 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnTagCopyOnComp(TableColumn column)
/*    */   {
/* 41 */     column.setWidth(200);
/* 42 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 46 */     Tag tag = (Tag)cell.getDataSource();
/* 47 */     if ((tag instanceof TagFeatureFileLocation)) {
/* 48 */       TagFeatureFileLocation fl = (TagFeatureFileLocation)tag;
/*    */       
/* 50 */       if (fl.supportsTagCopyOnComplete())
/*    */       {
/* 52 */         File target_file = fl.getTagCopyOnCompleteFolder();
/*    */         
/*    */         String target;
/*    */         String target;
/* 56 */         if (target_file == null) {
/* 57 */           target = "";
/*    */         } else {
/* 59 */           target = target_file.getAbsolutePath();
/*    */         }
/*    */         
/* 62 */         if ((!cell.setSortValue(target)) && (cell.isValid())) {
/* 63 */           return;
/*    */         }
/*    */         
/* 66 */         if (!cell.isShown()) {
/* 67 */           return;
/*    */         }
/*    */         
/* 70 */         cell.setText(target);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tag/ColumnTagCopyOnComp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */