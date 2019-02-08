/*    */ package com.aelitis.azureus.ui.swt.shells.opentorrent;
/*    */ 
/*    */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*    */ import org.eclipse.swt.graphics.GC;
/*    */ import org.eclipse.swt.graphics.Rectangle;
/*    */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*    */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenFileOptions;
/*    */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenOptions;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
/*    */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWTPaintListener;
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
/*    */ public class TableColumnOTOF_Size
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener, TableCellSWTPaintListener
/*    */ {
/*    */   public static final String COLUMN_ID = "size";
/*    */   
/*    */   public TableColumnOTOF_Size(TableColumn column)
/*    */   {
/* 38 */     column.initialize(2, -2, 80);
/* 39 */     column.addListeners(this);
/* 40 */     if ((column instanceof TableColumnCore)) {
/* 41 */       ((TableColumnCore)column).addCellOtherListener("SWTPaint", this);
/*    */     }
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 46 */     info.addCategories(new String[] { "content" });
/*    */     
/*    */ 
/* 49 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 53 */     Object ds = cell.getDataSource();
/* 54 */     if (!(ds instanceof TorrentOpenFileOptions)) {
/* 55 */       return;
/*    */     }
/* 57 */     TorrentOpenFileOptions tfi = (TorrentOpenFileOptions)ds;
/* 58 */     cell.setSortValue(tfi.lSize);
/* 59 */     cell.setText(DisplayFormatters.formatByteCountToKiBEtc(tfi.lSize));
/*    */   }
/*    */   
/*    */   public void cellPaint(GC gc, TableCellSWT cell) {
/* 63 */     Object ds = cell.getDataSource();
/* 64 */     if (!(ds instanceof TorrentOpenFileOptions)) {
/* 65 */       return;
/*    */     }
/* 67 */     TorrentOpenFileOptions tfi = (TorrentOpenFileOptions)ds;
/*    */     
/* 69 */     float pct = (float)tfi.lSize / (float)tfi.parent.getTorrent().getSize();
/*    */     
/* 71 */     Rectangle bounds = cell.getBounds();
/*    */     
/* 73 */     bounds.width = ((int)(bounds.width * pct));
/* 74 */     if (bounds.width > 2) {
/* 75 */       bounds.x += 1;
/* 76 */       bounds.y += 1;
/* 77 */       bounds.height -= 2;
/* 78 */       bounds.width -= 2;
/* 79 */       gc.setBackground(gc.getForeground());
/* 80 */       int alpha = gc.getAlpha();
/* 81 */       gc.setAlpha(10);
/* 82 */       gc.fillRectangle(bounds);
/* 83 */       gc.setAlpha(alpha);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/opentorrent/TableColumnOTOF_Size.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */