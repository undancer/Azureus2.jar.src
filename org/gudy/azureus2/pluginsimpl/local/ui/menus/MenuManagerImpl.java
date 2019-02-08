/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.menus;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.PluginInterface;
/*    */ import org.gudy.azureus2.plugins.ui.UIRuntimeException;
/*    */ import org.gudy.azureus2.plugins.ui.menus.MenuContext;
/*    */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*    */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*    */ import org.gudy.azureus2.pluginsimpl.local.ui.UIManagerImpl;
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
/*    */ public class MenuManagerImpl
/*    */   implements MenuManager
/*    */ {
/*    */   private UIManagerImpl ui_manager;
/*    */   
/* 33 */   public MenuManagerImpl(UIManagerImpl _ui_manager) { this.ui_manager = _ui_manager; }
/*    */   
/*    */   public MenuItem addMenuItem(String menuID, String resource_key) {
/* 36 */     PluginInterface pi = this.ui_manager.getPluginInterface();
/* 37 */     MenuItemImpl item = new MenuItemImpl(pi, menuID, resource_key);
/* 38 */     UIManagerImpl.fireEvent(pi, 15, item);
/* 39 */     return item;
/*    */   }
/*    */   
/*    */   public MenuItem addMenuItem(MenuContext context, String resource_key) {
/* 43 */     MenuContextImpl context_impl = (MenuContextImpl)context;
/* 44 */     MenuItemImpl result = (MenuItemImpl)addMenuItem(context_impl.context, resource_key);
/* 45 */     result.setContext(context_impl);
/* 46 */     context_impl.dirty();
/* 47 */     return result;
/*    */   }
/*    */   
/*    */   public MenuItem addMenuItem(MenuItem parent, String resource_key)
/*    */   {
/* 52 */     if (!(parent instanceof MenuItemImpl)) {
/* 53 */       throw new UIRuntimeException("parent must have been created by addMenuItem");
/*    */     }
/*    */     
/* 56 */     if (parent.getStyle() != 5) {
/* 57 */       throw new UIRuntimeException("parent menu item must have the menu style associated");
/*    */     }
/*    */     
/* 60 */     MenuItemImpl item = new MenuItemImpl((MenuItemImpl)parent, resource_key);
/* 61 */     UIManagerImpl.fireEvent(this.ui_manager.getPluginInterface(), 16, new Object[] { item, parent });
/* 62 */     return item;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/menus/MenuManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */