/*    */ package com.aelitis.azureus.ui.swt.columns.tagdiscovery;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.TagDiscovery;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.views.table.utils.TableColumnCreator;
/*    */ import org.gudy.azureus2.ui.swt.views.tableitems.ColumnDateSizer;
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
/*    */ public class ColumnTagDiscoveryAddedOn
/*    */   implements TableColumnExtraInfoListener, TableCellRefreshListener
/*    */ {
/* 29 */   public static String COLUMN_ID = "tag.discovery.addedon";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 32 */     info.addCategories(new String[] { "time" });
/*    */     
/*    */ 
/* 35 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnTagDiscoveryAddedOn(TableColumn column)
/*    */   {
/* 40 */     column.setWidth(TableColumnCreator.DATE_COLUMN_WIDTH);
/* 41 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 45 */     TableColumn tc = cell.getTableColumn();
/* 46 */     if ((tc instanceof ColumnDateSizer)) {
/* 47 */       TagDiscovery discovery = (TagDiscovery)cell.getDataSource();
/* 48 */       ((ColumnDateSizer)tc).refresh(cell, discovery.getTimestamp());
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tagdiscovery/ColumnTagDiscoveryAddedOn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */