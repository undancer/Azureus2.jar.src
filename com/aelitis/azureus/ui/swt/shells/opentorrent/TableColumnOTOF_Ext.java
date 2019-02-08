/*    */ package com.aelitis.azureus.ui.swt.shells.opentorrent;
/*    */ 
/*    */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenFileOptions;
/*    */ import org.gudy.azureus2.core3.util.FileUtil;
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
/*    */ public class TableColumnOTOF_Ext
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/*    */   public static final String COLUMN_ID = "fileext";
/*    */   
/*    */   public TableColumnOTOF_Ext(TableColumn column)
/*    */   {
/* 31 */     column.initialize(1, -1, 90);
/* 32 */     column.setRefreshInterval(-2);
/* 33 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 37 */     info.addCategories(new String[] { "content" });
/*    */     
/*    */ 
/* 40 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 44 */     Object ds = cell.getDataSource();
/* 45 */     if (!(ds instanceof TorrentOpenFileOptions)) {
/* 46 */       return;
/*    */     }
/* 48 */     TorrentOpenFileOptions tfi = (TorrentOpenFileOptions)ds;
/* 49 */     String s = FileUtil.getExtension(tfi.orgFileName);
/* 50 */     cell.setText(s);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/opentorrent/TableColumnOTOF_Ext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */