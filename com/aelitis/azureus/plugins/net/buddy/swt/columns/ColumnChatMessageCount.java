/*     */ package com.aelitis.azureus.plugins.net.buddy.swt.columns;
/*     */ 
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatInstance;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginUtils;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ColumnChatMessageCount
/*     */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*     */ {
/*  32 */   public static String COLUMN_ID = "chat.msg.count";
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  35 */     info.addCategories(new String[] { "essential" });
/*     */     
/*     */ 
/*  38 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */   public ColumnChatMessageCount(TableColumn column)
/*     */   {
/*  43 */     column.setWidth(60);
/*  44 */     column.setAlignment(2);
/*  45 */     column.setRefreshInterval(-2);
/*  46 */     column.addListeners(this);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell)
/*     */   {
/*  51 */     Object dataSource = cell.getDataSource();
/*     */     
/*  53 */     int num = -1;
/*     */     
/*  55 */     if ((dataSource instanceof Download))
/*     */     {
/*  57 */       Download dl = (Download)dataSource;
/*     */       
/*  59 */       BuddyPluginBeta beta = BuddyPluginUtils.getBetaPlugin();
/*     */       
/*  61 */       if (beta != null) {
/*  62 */         BuddyPluginBeta.ChatInstance chat = beta.peekChatInstance(dl);
/*     */         
/*  64 */         if (chat != null)
/*     */         {
/*  66 */           num = chat.getMessageCount(true);
/*     */         }
/*     */         else
/*     */         {
/*  70 */           Map<String, Object> peek_data = beta.peekChat(dl, true);
/*     */           
/*  72 */           if (peek_data != null)
/*     */           {
/*  74 */             Number message_count = (Number)peek_data.get("m");
/*     */             
/*  76 */             if (message_count != null)
/*     */             {
/*  78 */               num = message_count.intValue();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/*  85 */       BuddyPluginBeta.ChatInstance chat = (BuddyPluginBeta.ChatInstance)cell.getDataSource();
/*     */       
/*  87 */       if (chat != null)
/*     */       {
/*  89 */         num = chat.getMessageCount(true);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  94 */     if ((!cell.setSortValue(num)) && (cell.isValid())) {
/*  95 */       return;
/*     */     }
/*     */     
/*  98 */     if (!cell.isShown()) {
/*  99 */       return;
/*     */     }
/*     */     
/* 102 */     cell.setText(num < 100 ? String.valueOf(num) : num == -1 ? "" : "100+");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/swt/columns/ColumnChatMessageCount.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */