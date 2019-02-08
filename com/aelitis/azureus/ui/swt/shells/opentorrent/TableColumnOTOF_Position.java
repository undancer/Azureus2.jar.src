/*    */ package com.aelitis.azureus.ui.swt.shells.opentorrent;
/*    */ 
/*    */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*    */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenFileOptions;
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
/*    */ public class TableColumnOTOF_Position
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/*    */   public static final String COLUMN_ID = "#";
/*    */   
/*    */   public TableColumnOTOF_Position(TableColumn column)
/*    */   {
/* 32 */     column.initialize(2, -2, 40);
/* 33 */     column.addListeners(this);
/* 34 */     if ((column instanceof TableColumnCore)) {
/* 35 */       ((TableColumnCore)column).setDefaultSortAscending(true);
/*    */     }
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 40 */     info.addCategories(new String[] { "protocol" });
/*    */     
/*    */ 
/* 43 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 47 */     Object ds = cell.getDataSource();
/* 48 */     if (!(ds instanceof TorrentOpenFileOptions)) {
/* 49 */       return;
/*    */     }
/* 51 */     TorrentOpenFileOptions tfi = (TorrentOpenFileOptions)ds;
/* 52 */     int index = tfi.getIndex();
/* 53 */     cell.setSortValue(index);
/* 54 */     cell.setText("" + index);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/opentorrent/TableColumnOTOF_Position.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */