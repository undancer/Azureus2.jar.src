/*    */ package com.aelitis.azureus.ui.swt.devices.columns;
/*    */ 
/*    */ import com.aelitis.azureus.core.devices.DeviceOfflineDownload;
/*    */ import org.gudy.azureus2.plugins.download.Download;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
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
/*    */ public class ColumnOD_Name
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener, ObfusticateCellText
/*    */ {
/*    */   public static final String COLUMN_ID = "od_name";
/*    */   
/*    */   public ColumnOD_Name(TableColumn column)
/*    */   {
/* 40 */     column.initialize(1, -2, 300);
/* 41 */     column.addListeners(this);
/* 42 */     column.setRefreshInterval(-1);
/* 43 */     column.setType(3);
/* 44 */     column.setObfustication(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 48 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 51 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 55 */     DeviceOfflineDownload od = (DeviceOfflineDownload)cell.getDataSource();
/* 56 */     if (od == null) {
/* 57 */       return;
/*    */     }
/*    */     
/* 60 */     String text = od.getDownload().getName();
/*    */     
/* 62 */     if ((text == null) || (text.length() == 0))
/*    */     {
/* 64 */       return;
/*    */     }
/*    */     
/* 67 */     cell.setText(text);
/*    */   }
/*    */   
/*    */   public String getObfusticatedText(TableCell cell) {
/* 71 */     DeviceOfflineDownload od = (DeviceOfflineDownload)cell.getDataSource();
/* 72 */     if (od == null) {
/* 73 */       return null;
/*    */     }
/* 75 */     String name = od.getDownload().toString();
/* 76 */     int i = name.indexOf('#');
/* 77 */     if (i > 0) {
/* 78 */       name = name.substring(i + 1);
/*    */     }
/* 80 */     return name;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/columns/ColumnOD_Name.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */