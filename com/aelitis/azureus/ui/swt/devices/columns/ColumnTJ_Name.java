/*    */ package com.aelitis.azureus.ui.swt.devices.columns;
/*    */ 
/*    */ import com.aelitis.azureus.core.devices.TranscodeFile;
/*    */ import com.aelitis.azureus.util.DataSourceUtils;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellDisposeListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.debug.ObfusticateCellText;
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
/*    */ public class ColumnTJ_Name
/*    */   implements TableCellRefreshListener, ObfusticateCellText, TableCellDisposeListener, TableColumnExtraInfoListener
/*    */ {
/*    */   public static final String COLUMN_ID = "transcode_name";
/*    */   
/*    */   public ColumnTJ_Name(TableColumn column)
/*    */   {
/* 47 */     column.initialize(1, -2, 215);
/* 48 */     column.addListeners(this);
/* 49 */     column.setObfustication(true);
/* 50 */     column.setRefreshInterval(-1);
/* 51 */     column.setType(3);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 55 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 58 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 62 */     TranscodeFile tf = (TranscodeFile)cell.getDataSource();
/* 63 */     if (tf == null) {
/* 64 */       return;
/*    */     }
/*    */     
/* 67 */     String text = tf.getName();
/*    */     
/* 69 */     if ((text == null) || (text.length() == 0))
/*    */     {
/* 71 */       return;
/*    */     }
/*    */     
/* 74 */     cell.setText(text);
/*    */   }
/*    */   
/*    */   public String getObfusticatedText(TableCell cell) {
/* 78 */     String name = null;
/* 79 */     DownloadManager dm = DataSourceUtils.getDM(cell.getDataSource());
/* 80 */     if (dm != null) {
/* 81 */       name = dm.toString();
/* 82 */       int i = name.indexOf('#');
/* 83 */       if (i > 0) {
/* 84 */         name = name.substring(i + 1);
/*    */       }
/*    */     }
/*    */     
/* 88 */     if (name == null)
/* 89 */       name = "";
/* 90 */     return name;
/*    */   }
/*    */   
/*    */   public void dispose(TableCell cell) {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/columns/ColumnTJ_Name.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */