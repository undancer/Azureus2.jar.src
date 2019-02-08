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
/*    */ public class ColumnChatFavorite
/*    */   extends ColumnCheckBox
/*    */ {
/* 36 */   public static String COLUMN_ID = "chat.fave";
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnChatFavorite(TableColumn column)
/*    */   {
/* 42 */     super(column, 60);
/* 43 */     column.setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected Boolean getCheckBoxState(Object datasource)
/*    */   {
/* 51 */     BuddyPluginBeta.ChatInstance chat = (BuddyPluginBeta.ChatInstance)datasource;
/*    */     
/* 53 */     if (chat != null)
/*    */     {
/* 55 */       return Boolean.valueOf(chat.isFavourite());
/*    */     }
/*    */     
/* 58 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected void setCheckBoxState(Object datasource, boolean set)
/*    */   {
/* 67 */     BuddyPluginBeta.ChatInstance chat = (BuddyPluginBeta.ChatInstance)datasource;
/*    */     
/* 69 */     if (chat != null)
/*    */     {
/* 71 */       chat.setFavourite(set);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/swt/columns/ColumnChatFavorite.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */