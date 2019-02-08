/*    */ package com.aelitis.azureus.ui.swt.columns.searchsubs;
/*    */ 
/*    */ import com.aelitis.azureus.ui.swt.utils.SearchSubsResultBase;
/*    */ import com.aelitis.azureus.ui.swt.utils.SearchSubsUtils;
/*    */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
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
/*    */ 
/*    */ 
/*    */ public class ColumnSearchSubResultExisting
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 33 */   public static String COLUMN_ID = "in";
/*    */   
/* 35 */   private static final String[] messages = new String[5];
/*    */   
/*    */   static {
/* 38 */     COConfigurationManager.addAndFireListener(new COConfigurationListener()
/*    */     {
/*    */ 
/*    */       public void configurationSaved()
/*    */       {
/*    */ 
/* 44 */         ColumnSearchSubResultExisting.messages[0] = "";
/* 45 */         ColumnSearchSubResultExisting.messages[1] = MessageText.getString("label.library");
/* 46 */         ColumnSearchSubResultExisting.messages[2] = MessageText.getString("label.archive");
/* 47 */         ColumnSearchSubResultExisting.messages[3] = MessageText.getString("label.history");
/* 48 */         ColumnSearchSubResultExisting.messages[4] = "?";
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 54 */     info.addCategories(new String[] { "content" });
/*    */     
/*    */ 
/* 57 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnSearchSubResultExisting(TableColumn column)
/*    */   {
/* 62 */     column.initialize(3, -1, 60);
/* 63 */     column.setRefreshInterval(-1);
/* 64 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 68 */     SearchSubsResultBase result = (SearchSubsResultBase)cell.getDataSource();
/*    */     
/* 70 */     int status = SearchSubsUtils.getHashStatus(result);
/*    */     
/* 72 */     if ((!cell.setSortValue(status)) && (cell.isValid()))
/*    */     {
/* 74 */       return;
/*    */     }
/*    */     
/* 77 */     if (!cell.isShown())
/*    */     {
/* 79 */       return;
/*    */     }
/*    */     
/* 82 */     cell.setText(messages[status]);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/searchsubs/ColumnSearchSubResultExisting.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */