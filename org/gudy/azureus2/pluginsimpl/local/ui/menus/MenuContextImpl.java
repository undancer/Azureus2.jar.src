/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.menus;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.ui.menus.MenuContext;
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
/*    */ public class MenuContextImpl
/*    */   implements MenuContext
/*    */ {
/* 26 */   private static int count = 0;
/*    */   public final String context;
/*    */   public boolean is_dirty;
/*    */   
/*    */   private MenuContextImpl(String identifier) {
/* 31 */     this.context = identifier;
/*    */   }
/*    */   
/* 34 */   public String toString() { return super.toString() + " - " + this.context; }
/*    */   
/*    */   public void dirty() {
/* 37 */     this.is_dirty = true;
/*    */   }
/*    */   
/*    */   public static synchronized MenuContextImpl create(String identifier) {
/* 41 */     return new MenuContextImpl("MENU_CONTEXT_" + ++count + "_" + identifier);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/menus/MenuContextImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */