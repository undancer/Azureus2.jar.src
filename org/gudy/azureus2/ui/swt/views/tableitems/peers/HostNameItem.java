/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import org.gudy.azureus2.core3.peer.PEPeer;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HostNameItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "host";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 38 */     info.addCategories(new String[] { "identification" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public HostNameItem(String table_id)
/*    */   {
/* 45 */     super("host", -1, 100, table_id);
/* 46 */     setRefreshInterval(-2);
/* 47 */     setObfustication(true);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 51 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 52 */     String addr = peer == null ? "" : peer.getIPHostName();
/* 53 */     if (cell.setText(addr)) if (!addr.equals(peer == null ? "" : peer.getIp()))
/*    */       {
/* 55 */         String[] l = addr.split("\\.");
/* 56 */         StringBuilder buf = new StringBuilder();
/* 57 */         for (int i = l.length - 1; i >= 0; i--)
/*    */         {
/* 59 */           buf.append(l[i]);
/* 60 */           buf.append('.');
/*    */         }
/* 62 */         cell.setSortValue(buf.toString());
/*    */       }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/HostNameItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */