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
/*    */ public class MessagingItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "Messaging";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 38 */     info.addCategories(new String[] { "protocol" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public MessagingItem(String table_id)
/*    */   {
/* 45 */     super("Messaging", 3, -1, 40, table_id);
/* 46 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 50 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 51 */     int value = peer == null ? -1 : peer.getMessagingMode();
/*    */     
/* 53 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/*    */       return;
/*    */     }
/*    */     
/*    */     String text;
/* 58 */     switch (value) {
/*    */     case 1: 
/* 60 */       text = "";
/* 61 */       break;
/*    */     case 3: 
/* 63 */       text = "LT";
/* 64 */       break;
/*    */     case 2: 
/* 66 */       text = "AZ";
/* 67 */       break;
/*    */     case 4: 
/* 69 */       text = "Plugin";
/* 70 */       break;
/*    */     default: 
/* 72 */       text = "";
/*    */     }
/*    */     
/*    */     
/* 76 */     cell.setText(text);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/MessagingItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */