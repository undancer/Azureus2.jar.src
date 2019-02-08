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
/*    */ public class ColumnChatUserCount
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 27 */   public static String COLUMN_ID = "chat.user.count";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 30 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 33 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnChatUserCount(TableColumn column)
/*    */   {
/* 38 */     column.setWidth(40);
/* 39 */     column.setAlignment(3);
/* 40 */     column.setRefreshInterval(-2);
/* 41 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 45 */     BuddyPluginBeta.ChatInstance chat = (BuddyPluginBeta.ChatInstance)cell.getDataSource();
/* 46 */     int num = -1;
/* 47 */     if (chat != null) {
/* 48 */       num = chat.getEstimatedNodes();
/*    */     }
/*    */     
/* 51 */     if ((!cell.setSortValue(num)) && (cell.isValid())) {
/* 52 */       return;
/*    */     }
/*    */     
/* 55 */     if (!cell.isShown()) {
/* 56 */       return;
/*    */     }
/*    */     
/* 59 */     cell.setText(num < 100 ? String.valueOf(num) : num == -1 ? "" : "100+");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/swt/columns/ColumnChatUserCount.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */