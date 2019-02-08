/*    */ package org.gudy.azureus2.ui.swt.views.clientstats;
/*    */ 
/*    */ import com.aelitis.azureus.util.MapUtils;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
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
/*    */ public class ColumnCS_Count
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "count";
/*    */   
/*    */   public ColumnCS_Count(TableColumn column)
/*    */   {
/* 35 */     column.initialize(2, -2, 50);
/* 36 */     column.addListeners(this);
/* 37 */     column.setType(3);
/*    */     
/* 39 */     Object network = column.getUserDataString("network");
/* 40 */     if (network != null) {
/* 41 */       column.setVisible(false);
/* 42 */       column.setNameOverride(network + " " + MessageText.getString("ClientStats.column.count"));
/*    */     }
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 48 */     ClientStatsDataSource ds = (ClientStatsDataSource)cell.getDataSource();
/* 49 */     if (ds == null) {
/* 50 */       return;
/*    */     }
/* 52 */     long val = ds.count;
/* 53 */     TableColumn column = cell.getTableColumn();
/* 54 */     if (column != null) {
/* 55 */       Object network = column.getUserDataString("network");
/* 56 */       if (network != null) {
/* 57 */         Map<String, Object> map = (Map)ds.perNetworkStats.get(network);
/* 58 */         if (map != null) {
/* 59 */           val = MapUtils.getMapLong(map, "count", 0L);
/*    */         } else {
/* 61 */           val = 0L;
/*    */         }
/*    */       }
/*    */     }
/* 65 */     if ((cell.setSortValue(val)) || (!cell.isValid())) {
/* 66 */       cell.setText(Long.toString(val));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/clientstats/ColumnCS_Count.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */