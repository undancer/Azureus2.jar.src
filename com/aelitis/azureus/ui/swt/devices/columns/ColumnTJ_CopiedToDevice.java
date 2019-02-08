/*    */ package com.aelitis.azureus.ui.swt.devices.columns;
/*    */ 
/*    */ import com.aelitis.azureus.core.devices.Device;
/*    */ import com.aelitis.azureus.core.devices.DeviceMediaRenderer;
/*    */ import com.aelitis.azureus.core.devices.TranscodeFile;
/*    */ import java.util.Locale;
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.core3.internat.MessageText.MessageTextListener;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
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
/*    */ public class ColumnTJ_CopiedToDevice
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/*    */   public static final String COLUMN_ID = "copied";
/*    */   private String na_text;
/*    */   
/*    */   public ColumnTJ_CopiedToDevice(final TableColumn column)
/*    */   {
/* 47 */     column.initialize(3, -2, 50);
/* 48 */     column.addListeners(this);
/* 49 */     column.setRefreshInterval(-1);
/* 50 */     column.setType(3);
/*    */     
/* 52 */     MessageText.addAndFireListener(new MessageText.MessageTextListener()
/*    */     {
/*    */       public void localeChanged(Locale old_locale, Locale new_locale) {
/* 55 */         ColumnTJ_CopiedToDevice.this.na_text = MessageText.getString("general.na.short");
/*    */         
/* 57 */         column.invalidateCells();
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 63 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 66 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 71 */     TranscodeFile tf = (TranscodeFile)cell.getDataSource();
/* 72 */     if (tf == null) {
/* 73 */       return;
/*    */     }
/*    */     
/* 76 */     Device d = tf.getDevice();
/*    */     
/* 78 */     String value = null;
/*    */     
/* 80 */     if ((d instanceof DeviceMediaRenderer))
/*    */     {
/* 82 */       DeviceMediaRenderer dmr = (DeviceMediaRenderer)d;
/*    */       
/* 84 */       if ((!dmr.canCopyToDevice()) && (!dmr.canCopyToFolder()))
/*    */       {
/* 86 */         value = this.na_text;
/*    */       }
/*    */     }
/*    */     
/* 90 */     if (value == null) {
/* 91 */       value = DisplayFormatters.getYesNo(tf.isCopiedToDevice());
/*    */     }
/*    */     
/* 94 */     if ((cell.setSortValue(value)) || (!cell.isValid())) {
/* 95 */       cell.setText(value);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/columns/ColumnTJ_CopiedToDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */