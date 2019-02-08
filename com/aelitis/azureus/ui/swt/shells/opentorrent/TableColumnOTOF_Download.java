/*    */ package com.aelitis.azureus.ui.swt.shells.opentorrent;
/*    */ 
/*    */ import com.aelitis.azureus.ui.swt.columns.ColumnCheckBox;
/*    */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenFileOptions;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
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
/*    */ public class TableColumnOTOF_Download
/*    */   extends ColumnCheckBox
/*    */ {
/*    */   public static final String COLUMN_ID = "download";
/*    */   
/*    */   public TableColumnOTOF_Download(TableColumn column)
/*    */   {
/* 32 */     super(column, 60);
/* 33 */     column.setPosition(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 37 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 40 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   protected Boolean getCheckBoxState(Object ds)
/*    */   {
/* 45 */     if (!(ds instanceof TorrentOpenFileOptions)) {
/* 46 */       return Boolean.valueOf(false);
/*    */     }
/* 48 */     return Boolean.valueOf(((TorrentOpenFileOptions)ds).isToDownload());
/*    */   }
/*    */   
/*    */   protected void setCheckBoxState(Object ds, boolean set)
/*    */   {
/* 53 */     if (!(ds instanceof TorrentOpenFileOptions)) {
/* 54 */       return;
/*    */     }
/* 56 */     ((TorrentOpenFileOptions)ds).setToDownload(set);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/opentorrent/TableColumnOTOF_Download.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */