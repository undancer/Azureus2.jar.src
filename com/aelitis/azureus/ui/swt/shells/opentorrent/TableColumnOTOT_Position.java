/*    */ package com.aelitis.azureus.ui.swt.shells.opentorrent;
/*    */ 
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
/*    */ public class TableColumnOTOT_Position
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/*    */   public static final String COLUMN_ID = "#";
/*    */   
/*    */   public TableColumnOTOT_Position(TableColumn column)
/*    */   {
/* 29 */     column.initialize(2, -2, 40, -2);
/* 30 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 34 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 37 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 41 */     Object ds = cell.getDataSource();
/* 42 */     if (!(ds instanceof OpenTorrentOptionsWindow.OpenTorrentInstance)) {
/* 43 */       return;
/*    */     }
/* 45 */     OpenTorrentOptionsWindow.OpenTorrentInstance instance = (OpenTorrentOptionsWindow.OpenTorrentInstance)ds;
/* 46 */     int index = instance.getIndex() + 1;
/* 47 */     if (index < 1) {
/* 48 */       return;
/*    */     }
/* 50 */     cell.setSortValue(-index);
/* 51 */     cell.setText("" + index);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/opentorrent/TableColumnOTOT_Position.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */