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
/*    */ public class ColumnChatName
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 27 */   public static String COLUMN_ID = "chat.name";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 30 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 33 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnChatName(TableColumn column)
/*    */   {
/* 38 */     column.setWidth(220);
/* 39 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 43 */     BuddyPluginBeta.ChatInstance chat = (BuddyPluginBeta.ChatInstance)cell.getDataSource();
/* 44 */     String chat_name = null;
/* 45 */     if (chat != null) {
/* 46 */       chat_name = chat.getName();
/*    */     }
/*    */     
/* 49 */     if (chat_name == null) {
/* 50 */       chat_name = "";
/*    */     }
/*    */     
/* 53 */     if ((!cell.setSortValue(chat_name)) && (cell.isValid())) {
/* 54 */       return;
/*    */     }
/*    */     
/* 57 */     if (!cell.isShown()) {
/* 58 */       return;
/*    */     }
/*    */     
/* 61 */     cell.setText(chat_name);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/swt/columns/ColumnChatName.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */