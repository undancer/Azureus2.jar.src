/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.tracker;
/*    */ 
/*    */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
/*    */ import java.util.Locale;
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.core3.internat.MessageText.MessageTextListener;
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
/*    */ public class TypeItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 40 */   private static final String[] js_resource_keys = { "SpeedView.stats.unknown", "MyTrackerView.tracker", "wizard.webseed.title", "tps.type.dht", "ConfigView.section.transfer.lan", "tps.type.pex", "tps.type.incoming", "tps.type.plugin" };
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
/* 51 */   private static String[] js_resources = new String[js_resource_keys.length];
/*    */   
/*    */ 
/*    */ 
/*    */   public TypeItem(String tableID)
/*    */   {
/* 57 */     super("type", 1, -2, 75, tableID);
/*    */     
/* 59 */     setRefreshInterval(-3);
/*    */     
/* 61 */     MessageText.addAndFireListener(new MessageText.MessageTextListener() {
/*    */       public void localeChanged(Locale old_locale, Locale new_locale) {
/* 63 */         for (int i = 0; i < TypeItem.js_resources.length; i++) {
/* 64 */           TypeItem.js_resources[i] = MessageText.getString(TypeItem.js_resource_keys[i]);
/*    */         }
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 74 */     info.addCategories(new String[] { "essential" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 83 */     TrackerPeerSource ps = (TrackerPeerSource)cell.getDataSource();
/*    */     
/* 85 */     int value = ps == null ? 0 : ps.getType();
/*    */     
/* 87 */     if ((!cell.setSortValue(value)) && (cell.isValid()))
/*    */     {
/* 89 */       return;
/*    */     }
/*    */     
/* 92 */     cell.setText(js_resources[value]);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/tracker/TypeItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */