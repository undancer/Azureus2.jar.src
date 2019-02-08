/*    */ package com.aelitis.azureus.ui.swt.columns.tagdiscovery;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.TagDiscovery;
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
/*    */ public class ColumnTagDiscoveryNetwork
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 27 */   public static String COLUMN_ID = "networks";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 30 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 33 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public ColumnTagDiscoveryNetwork(TableColumn column)
/*    */   {
/* 38 */     column.setWidth(60);
/* 39 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 43 */     TagDiscovery discovery = (TagDiscovery)cell.getDataSource();
/* 44 */     cell.setText(discovery.getNetwork());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tagdiscovery/ColumnTagDiscoveryNetwork.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */