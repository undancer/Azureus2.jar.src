/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.tracker;
/*    */ 
/*    */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
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
/*    */ public class NameItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public NameItem(String tableID)
/*    */   {
/* 40 */     super("name", 1, -2, 300, tableID);
/*    */     
/* 42 */     setRefreshInterval(-1);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 49 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 52 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 59 */     TrackerPeerSource ps = (TrackerPeerSource)cell.getDataSource();
/*    */     
/* 61 */     String name = ps == null ? "" : ps.getName();
/*    */     
/* 63 */     if ((!cell.setSortValue(name)) && (cell.isValid()))
/*    */     {
/* 65 */       return;
/*    */     }
/*    */     
/* 68 */     cell.setText(name);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/tracker/NameItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */