/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.tracker;
/*    */ 
/*    */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
/*    */ import org.gudy.azureus2.core3.util.TimeFormatter;
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
/*    */ public class UpdateInItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public UpdateInItem(String tableID)
/*    */   {
/* 39 */     super("updatein", 3, -2, 75, tableID);
/*    */     
/* 41 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 48 */     info.addCategories(new String[] { "essential" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 57 */     TrackerPeerSource ps = (TrackerPeerSource)cell.getDataSource();
/*    */     
/*    */     int secs;
/*    */     int secs;
/* 61 */     if (ps == null)
/*    */     {
/* 63 */       secs = -1;
/*    */     }
/*    */     else
/*    */     {
/* 67 */       int state = ps.getStatus();
/*    */       int secs;
/* 69 */       if (((state == 5) || (state == 6)) && (!ps.isUpdating()))
/*    */       {
/*    */ 
/*    */ 
/* 73 */         secs = ps.getSecondsToUpdate();
/*    */       }
/*    */       else
/*    */       {
/* 77 */         secs = -1;
/*    */       }
/*    */     }
/*    */     
/* 81 */     if ((!cell.setSortValue(secs)) && (cell.isValid()))
/*    */     {
/* 83 */       return;
/*    */     }
/*    */     
/* 86 */     cell.setText(TimeFormatter.formatColon(secs));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/tracker/UpdateInItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */