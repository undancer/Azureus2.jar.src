/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import org.gudy.azureus2.core3.peer.PEPeer;
/*    */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
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
/*    */ public class DownloadNameItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "name";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 35 */     info.addCategories(new String[] { "content" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public DownloadNameItem(String table_id)
/*    */   {
/* 42 */     super("name", 250, table_id);
/* 43 */     setPosition(0);
/*    */     
/* 45 */     setRefreshInterval(-2);
/* 46 */     setType(1);
/* 47 */     setMinWidth(100);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 55 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 56 */     if (peer == null) { cell.setText("");return; }
/* 57 */     PEPeerManager manager = peer.getManager();
/* 58 */     if (manager == null) { cell.setText("");return; }
/* 59 */     cell.setText(manager.getDisplayName());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/DownloadNameItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */