/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.tables;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.PluginInterface;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*    */ import org.gudy.azureus2.pluginsimpl.local.ui.menus.MenuItemImpl;
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
/*    */ public class TableContextMenuItemImpl
/*    */   extends MenuItemImpl
/*    */   implements TableContextMenuItem
/*    */ {
/*    */   private String sTableID;
/*    */   
/*    */   public TableContextMenuItemImpl(PluginInterface pi, String tableID, String key)
/*    */   {
/* 33 */     super(pi, "table", key);
/* 34 */     this.sTableID = tableID;
/*    */   }
/*    */   
/*    */   public TableContextMenuItemImpl(TableContextMenuItemImpl ti, String key) {
/* 38 */     super(ti, key);
/* 39 */     this.sTableID = ti.getTableID();
/*    */   }
/*    */   
/*    */   public String getTableID() {
/* 43 */     return this.sTableID;
/*    */   }
/*    */   
/*    */   public void remove() {
/* 47 */     removeWithEvents(17, 18);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/tables/TableContextMenuItemImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */