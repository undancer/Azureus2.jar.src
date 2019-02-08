/*    */ package com.aelitis.azureus.ui.swt.shells.opentorrent;
/*    */ 
/*    */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenOptions;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
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
/*    */ public class TableColumnOTOT_Size
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/*    */   public static final String COLUMN_ID = "size";
/*    */   
/*    */   public TableColumnOTOT_Size(TableColumn column)
/*    */   {
/* 31 */     column.initialize(1, -2, 80, -2);
/* 32 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 37 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 40 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 44 */     Object ds = cell.getDataSource();
/* 45 */     if (!(ds instanceof OpenTorrentOptionsWindow.OpenTorrentInstance)) {
/* 46 */       return;
/*    */     }
/* 48 */     OpenTorrentOptionsWindow.OpenTorrentInstance instance = (OpenTorrentOptionsWindow.OpenTorrentInstance)ds;
/*    */     
/* 50 */     long total_size = instance.getOptions().getTotalSize();
/* 51 */     long selected_size = instance.getSelectedDataSize();
/*    */     
/* 53 */     if (cell.setSortValue(selected_size))
/*    */     {
/* 55 */       String total_str = DisplayFormatters.formatByteCountToKiBEtc(total_size);
/* 56 */       String sel_str = total_size == selected_size ? total_str : DisplayFormatters.formatByteCountToKiBEtc(selected_size);
/*    */       
/* 58 */       if (total_str.equals(sel_str))
/*    */       {
/* 60 */         cell.setText(total_str);
/*    */       }
/*    */       else
/*    */       {
/* 64 */         cell.setText(sel_str + " / " + total_str);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/opentorrent/TableColumnOTOT_Size.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */