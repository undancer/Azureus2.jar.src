/*    */ package com.aelitis.azureus.ui.swt.columns.searchsubs;
/*    */ 
/*    */ import com.aelitis.azureus.ui.swt.utils.SearchSubsResultBase;
/*    */ import org.gudy.azureus2.core3.util.ByteFormatter;
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
/*    */ public class ColumnSearchSubResultHash
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 30 */   public static String COLUMN_ID = "hash";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 34 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 37 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnSearchSubResultHash(TableColumn column)
/*    */   {
/* 42 */     column.initialize(1, -1, 200);
/* 43 */     column.setRefreshInterval(-3);
/* 44 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 48 */     SearchSubsResultBase result = (SearchSubsResultBase)cell.getDataSource();
/*    */     
/* 50 */     byte[] hash = result.getHash();
/*    */     
/* 52 */     String str = hash == null ? "" : ByteFormatter.encodeString(hash);
/*    */     
/* 54 */     if ((!cell.setSortValue(str)) && (cell.isValid()))
/*    */     {
/* 56 */       return;
/*    */     }
/*    */     
/* 59 */     if (!cell.isShown())
/*    */     {
/* 61 */       return;
/*    */     }
/*    */     
/* 64 */     cell.setText(str);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/searchsubs/ColumnSearchSubResultHash.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */