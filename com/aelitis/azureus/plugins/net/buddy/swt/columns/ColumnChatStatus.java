/*    */ package com.aelitis.azureus.plugins.net.buddy.swt.columns;
/*    */ 
/*    */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatInstance;
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
/*    */ 
/*    */ public class ColumnChatStatus
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 27 */   public static String COLUMN_ID = "chat.status";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 30 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 33 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnChatStatus(TableColumn column)
/*    */   {
/* 38 */     column.setWidth(400);
/* 39 */     column.setRefreshInterval(-2);
/* 40 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 44 */     BuddyPluginBeta.ChatInstance chat = (BuddyPluginBeta.ChatInstance)cell.getDataSource();
/* 45 */     String status = null;
/* 46 */     if (chat != null) {
/* 47 */       status = chat.getStatus();
/*    */     }
/*    */     
/* 50 */     if (status == null) {
/* 51 */       status = "";
/*    */     }
/*    */     
/* 54 */     if ((!cell.setSortValue(status)) && (cell.isValid())) {
/* 55 */       return;
/*    */     }
/*    */     
/* 58 */     if (!cell.isShown()) {
/* 59 */       return;
/*    */     }
/*    */     
/* 62 */     cell.setText(status);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/swt/columns/ColumnChatStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */