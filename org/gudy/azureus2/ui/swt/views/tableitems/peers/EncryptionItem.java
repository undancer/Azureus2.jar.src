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
/*    */ 
/*    */ public class EncryptionItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "Encryption";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 39 */     info.addCategories(new String[] { "connection" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public EncryptionItem(String table_id)
/*    */   {
/* 46 */     super("Encryption", 3, -1, 50, table_id);
/* 47 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 51 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 52 */     String value = peer == null ? "" : peer.getEncryption();
/*    */     
/* 54 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 55 */       return;
/*    */     }
/* 57 */     cell.setText(value);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/EncryptionItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */