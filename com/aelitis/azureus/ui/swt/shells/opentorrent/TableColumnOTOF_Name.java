/*    */ package com.aelitis.azureus.ui.swt.shells.opentorrent;
/*    */ 
/*    */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*    */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenFileOptions;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellInplaceEditorListener;
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
/*    */ public class TableColumnOTOF_Name
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/*    */   public static final String COLUMN_ID = "name";
/*    */   
/*    */   public TableColumnOTOF_Name(TableColumn column)
/*    */   {
/* 32 */     column.initialize(1, -2, 260);
/* 33 */     column.setRefreshInterval(-2);
/* 34 */     column.addListeners(this);
/* 35 */     if ((column instanceof TableColumnCore)) {
/* 36 */       ((TableColumnCore)column).setInplaceEditorListener(new TableCellInplaceEditorListener() {
/*    */         public boolean inplaceValueSet(TableCell cell, String value, boolean finalEdit) {
/* 38 */           Object ds = cell.getDataSource();
/* 39 */           if (!(ds instanceof TorrentOpenFileOptions)) {
/* 40 */             return false;
/*    */           }
/* 42 */           TorrentOpenFileOptions tfi = (TorrentOpenFileOptions)ds;
/*    */           
/* 44 */           if ((value.equalsIgnoreCase(cell.getText())) || ("".equals(value)) || ("".equals(cell.getText())))
/*    */           {
/* 46 */             return true;
/*    */           }
/* 48 */           if (finalEdit) {
/* 49 */             tfi.setDestFileName(value, true);
/*    */           }
/*    */           
/* 52 */           return true;
/*    */         }
/*    */       });
/*    */     }
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 59 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 62 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 66 */     Object ds = cell.getDataSource();
/* 67 */     if (!(ds instanceof TorrentOpenFileOptions)) {
/* 68 */       return;
/*    */     }
/* 70 */     TorrentOpenFileOptions tfi = (TorrentOpenFileOptions)ds;
/* 71 */     cell.setText(tfi.getDestFileName());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/opentorrent/TableColumnOTOF_Name.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */