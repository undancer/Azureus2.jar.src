/*    */ package org.gudy.azureus2.ui.swt.views.clientstats;
/*    */ 
/*    */ import com.aelitis.azureus.util.MapUtils;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
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
/*    */ public class ColumnCS_Sent
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "sent";
/*    */   
/*    */   public ColumnCS_Sent(TableColumn column)
/*    */   {
/* 38 */     column.initialize(2, -2, 80);
/* 39 */     column.addListeners(this);
/* 40 */     column.setType(3);
/* 41 */     String network = column.getUserDataString("network");
/* 42 */     if (network != null) {
/* 43 */       column.setVisible(false);
/* 44 */       column.setNameOverride(network + " " + MessageText.getString("ClientStats.column.sent"));
/*    */     }
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 50 */     ClientStatsDataSource ds = (ClientStatsDataSource)cell.getDataSource();
/* 51 */     if (ds == null) {
/* 52 */       return;
/*    */     }
/* 54 */     long val = ds.bytesSent;
/*    */     
/* 56 */     TableColumn column = cell.getTableColumn();
/* 57 */     if (column != null) {
/* 58 */       String network = column.getUserDataString("network");
/* 59 */       if (network != null) {
/* 60 */         Map<String, Object> map = (Map)ds.perNetworkStats.get(network);
/* 61 */         if (map != null) {
/* 62 */           val = MapUtils.getMapLong(map, "bytesSent", 0L);
/*    */         } else {
/* 64 */           val = 0L;
/*    */         }
/*    */       }
/*    */     }
/* 68 */     if ((cell.setSortValue(val)) || (!cell.isValid())) {
/* 69 */       cell.setText(DisplayFormatters.formatByteCountToKiBEtc(val));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/clientstats/ColumnCS_Sent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */