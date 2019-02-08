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
/*    */ public class ColumnCS_Received
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "received";
/*    */   
/*    */   public ColumnCS_Received(TableColumn column)
/*    */   {
/* 38 */     column.initialize(2, -2, 80);
/* 39 */     column.addListeners(this);
/* 40 */     column.setType(3);
/* 41 */     String network = column.getUserDataString("network");
/* 42 */     if (network != null) {
/* 43 */       column.setVisible(false);
/* 44 */       column.setNameOverride(network + " " + MessageText.getString("ClientStats.column.received"));
/*    */     }
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 50 */     ClientStatsDataSource ds = (ClientStatsDataSource)cell.getDataSource();
/* 51 */     if (ds == null) {
/* 52 */       return;
/*    */     }
/* 54 */     long val = ds.bytesReceived;
/* 55 */     TableColumn column = cell.getTableColumn();
/* 56 */     if (column != null) {
/* 57 */       String network = column.getUserDataString("network");
/* 58 */       if (network != null) {
/* 59 */         Map<String, Object> map = (Map)ds.perNetworkStats.get(network);
/* 60 */         if (map != null) {
/* 61 */           val = MapUtils.getMapLong(map, "bytesReceived", 0L);
/*    */         } else {
/* 63 */           val = 0L;
/*    */         }
/*    */       }
/*    */     }
/* 67 */     if ((cell.setSortValue(val)) || (!cell.isValid())) {
/* 68 */       cell.setText(DisplayFormatters.formatByteCountToKiBEtc(val));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/clientstats/ColumnCS_Received.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */