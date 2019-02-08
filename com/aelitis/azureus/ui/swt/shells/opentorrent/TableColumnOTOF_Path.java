/*    */ package com.aelitis.azureus.ui.swt.shells.opentorrent;
/*    */ 
/*    */ import java.io.File;
/*    */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenFileOptions;
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
/*    */ public class TableColumnOTOF_Path
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/*    */   public static final String COLUMN_ID = "path";
/*    */   
/*    */   public TableColumnOTOF_Path(TableColumn column)
/*    */   {
/* 32 */     column.initialize(1, -2, 290);
/* 33 */     column.setRefreshInterval(-2);
/* 34 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 38 */     info.addCategories(new String[] { "content" });
/*    */     
/*    */ 
/* 41 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 45 */     Object ds = cell.getDataSource();
/* 46 */     if (!(ds instanceof TorrentOpenFileOptions)) {
/* 47 */       return;
/*    */     }
/* 49 */     TorrentOpenFileOptions tfi = (TorrentOpenFileOptions)ds;
/* 50 */     String s = tfi.getDestPathName();
/* 51 */     String parentDir = tfi.parent.getParentDir();
/*    */     
/* 53 */     if ((s.startsWith(parentDir + File.separator)) && (!parentDir.endsWith(File.separator)) && (s.length() > parentDir.length()))
/*    */     {
/*    */ 
/*    */ 
/* 57 */       s = s.substring(parentDir.length() + 1);
/*    */     }
/* 59 */     cell.setText(s);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/opentorrent/TableColumnOTOF_Path.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */