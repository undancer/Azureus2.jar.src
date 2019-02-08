/*    */ package com.aelitis.azureus.ui.swt.shells.opentorrent;
/*    */ 
/*    */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenOptions;
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
/*    */ public class TableColumnOTOT_Name
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/*    */   public static final String COLUMN_ID = "torrentname";
/*    */   
/*    */   public TableColumnOTOT_Name(TableColumn column)
/*    */   {
/* 30 */     column.initialize(1, -2, 260);
/* 31 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 36 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 39 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 43 */     Object ds = cell.getDataSource();
/* 44 */     if (!(ds instanceof OpenTorrentOptionsWindow.OpenTorrentInstance)) {
/* 45 */       return;
/*    */     }
/* 47 */     OpenTorrentOptionsWindow.OpenTorrentInstance instance = (OpenTorrentOptionsWindow.OpenTorrentInstance)ds;
/*    */     
/* 49 */     cell.setText(instance.getOptions().getTorrentName());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/opentorrent/TableColumnOTOT_Name.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */