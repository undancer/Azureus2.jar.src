/*    */ package com.aelitis.azureus.ui.swt.devices.columns;
/*    */ 
/*    */ import com.aelitis.azureus.core.devices.DeviceOfflineDownload;
/*    */ import java.util.Locale;
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.core3.internat.MessageText.MessageTextListener;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ColumnOD_Status
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/*    */   public static final String COLUMN_ID = "od_status";
/* 42 */   private static final String[] js_resource_keys = { "devices.od.idle", "devices.od.xfering" };
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 47 */   private static String[] js_resources = new String[js_resource_keys.length];
/*    */   
/*    */   public ColumnOD_Status(final TableColumn column) {
/* 50 */     column.initialize(3, -2, 80);
/* 51 */     column.addListeners(this);
/* 52 */     column.setRefreshInterval(-1);
/* 53 */     column.setType(3);
/*    */     
/* 55 */     MessageText.addAndFireListener(new MessageText.MessageTextListener() {
/*    */       public void localeChanged(Locale old_locale, Locale new_locale) {
/* 57 */         for (int i = 0; i < ColumnOD_Status.js_resources.length; i++) {
/* 58 */           ColumnOD_Status.js_resources[i] = MessageText.getString(ColumnOD_Status.js_resource_keys[i]);
/*    */         }
/*    */         
/* 61 */         column.invalidateCells();
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 67 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 70 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 74 */     DeviceOfflineDownload od = (DeviceOfflineDownload)cell.getDataSource();
/* 75 */     if (od == null) {
/* 76 */       return;
/*    */     }
/*    */     
/* 79 */     String text = od.isTransfering() ? js_resources[1] : js_resources[0];
/*    */     
/* 81 */     if ((text == null) || (text.length() == 0))
/*    */     {
/* 83 */       return;
/*    */     }
/*    */     
/* 86 */     cell.setText(text);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/columns/ColumnOD_Status.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */