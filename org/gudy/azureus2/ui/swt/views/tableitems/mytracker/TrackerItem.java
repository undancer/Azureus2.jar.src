/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytracker;
/*    */ 
/*    */ import java.net.URL;
/*    */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*    */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
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
/*    */ 
/*    */ 
/*    */ public class TrackerItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public TrackerItem()
/*    */   {
/* 35 */     super("tracker", -2, 250, "MyTracker");
/* 36 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 42 */     TRHostTorrent item = (TRHostTorrent)cell.getDataSource();
/* 43 */     String sText; String sText; if (item == null) {
/* 44 */       sText = "";
/*    */     } else {
/* 46 */       sText = item.getTorrent().getAnnounceURL().toString();
/*    */     }
/* 48 */     cell.setText(sText);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytracker/TrackerItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */