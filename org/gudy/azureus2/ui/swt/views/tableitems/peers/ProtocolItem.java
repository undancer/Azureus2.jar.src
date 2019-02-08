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
/*    */ public class ProtocolItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "Protocol";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 38 */     info.addCategories(new String[] { "connection" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ProtocolItem(String table_id)
/*    */   {
/* 45 */     super("Protocol", 3, -1, 50, table_id);
/* 46 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 50 */     PEPeer peer = (PEPeer)cell.getDataSource();
/*    */     String value;
/*    */     String value;
/* 53 */     if (peer == null) {
/* 54 */       value = "";
/*    */     } else {
/* 56 */       value = peer.getProtocol();
/*    */       
/* 58 */       String qualifier = peer.getProtocolQualifier();
/*    */       
/* 60 */       if (qualifier != null)
/*    */       {
/* 62 */         value = value + " (" + qualifier + ")";
/*    */       }
/*    */     }
/*    */     
/* 66 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 67 */       return;
/*    */     }
/* 69 */     cell.setText(value);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/ProtocolItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */