/*    */ package com.aelitis.azureus.ui.swt.shells.opentorrent;
/*    */ 
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenFileOptions;
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
/*    */ public class TableColumnOTOF_Priority
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/*    */   public static final String COLUMN_ID = "priority";
/*    */   
/*    */   public TableColumnOTOF_Priority(TableColumn column)
/*    */   {
/* 31 */     column.initialize(1, -2, 50);
/* 32 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 36 */     info.addCategories(new String[] { "settings" });
/*    */     
/*    */ 
/* 39 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 43 */     Object ds = cell.getDataSource();
/* 44 */     if (!(ds instanceof TorrentOpenFileOptions)) {
/* 45 */       return;
/*    */     }
/* 47 */     TorrentOpenFileOptions tfi = (TorrentOpenFileOptions)ds;
/*    */     
/*    */     String text;
/*    */     int priority;
/*    */     String text;
/* 52 */     if (tfi.isToDownload())
/*    */     {
/* 54 */       int priority = tfi.getPriority();
/*    */       
/* 56 */       if (priority > 0)
/*    */       {
/* 58 */         String text = MessageText.getString("FileItem.high");
/*    */         
/* 60 */         if (priority > 1)
/*    */         {
/* 62 */           text = text + " (" + priority + ")";
/*    */         }
/* 64 */       } else if (priority < 0)
/*    */       {
/* 66 */         String text = MessageText.getString("FileItem.low");
/*    */         
/* 68 */         if (priority < -1)
/*    */         {
/* 70 */           text = text + " (" + priority + ")";
/*    */         }
/*    */       }
/*    */       else {
/* 74 */         text = MessageText.getString("FileItem.normal");
/*    */       }
/*    */     } else {
/* 77 */       priority = Integer.MIN_VALUE;
/*    */       
/* 79 */       text = "";
/*    */     }
/*    */     
/* 82 */     if (cell.setSortValue(priority))
/*    */     {
/* 84 */       cell.setText(text);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/opentorrent/TableColumnOTOF_Priority.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */