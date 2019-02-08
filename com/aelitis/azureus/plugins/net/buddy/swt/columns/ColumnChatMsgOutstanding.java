/*    */ package com.aelitis.azureus.plugins.net.buddy.swt.columns;
/*    */ 
/*    */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatInstance;
/*    */ import com.aelitis.azureus.ui.swt.columns.ColumnCheckBox;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ColumnChatMsgOutstanding
/*    */   extends ColumnCheckBox
/*    */ {
/* 36 */   public static String COLUMN_ID = "chat.msg.out";
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnChatMsgOutstanding(TableColumn column)
/*    */   {
/* 42 */     super(column, 80, true);
/*    */     
/* 44 */     column.setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected Boolean getCheckBoxState(Object datasource)
/*    */   {
/* 52 */     BuddyPluginBeta.ChatInstance chat = (BuddyPluginBeta.ChatInstance)datasource;
/*    */     
/* 54 */     if (chat != null)
/*    */     {
/* 56 */       return Boolean.valueOf(chat.getMessageOutstanding());
/*    */     }
/*    */     
/* 59 */     return null;
/*    */   }
/*    */   
/*    */   protected void setCheckBoxState(Object datasource, boolean set) {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/swt/columns/ColumnChatMsgOutstanding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */