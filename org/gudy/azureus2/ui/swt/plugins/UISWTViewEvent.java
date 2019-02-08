/*    */ package org.gudy.azureus2.ui.swt.plugins;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract interface UISWTViewEvent
/*    */ {
/* 34 */   public static final String[] DEBUG_TYPES = { "Create", "DS", "Init", "FocusG", "FocusL", "Refr", "Lang", "Destr", "Close", "Obfus" };
/*    */   public static final int TYPE_CREATE = 0;
/*    */   public static final int TYPE_DATASOURCE_CHANGED = 1;
/*    */   public static final int TYPE_INITIALIZE = 2;
/*    */   public static final int TYPE_FOCUSGAINED = 3;
/*    */   public static final int TYPE_FOCUSLOST = 4;
/*    */   public static final int TYPE_REFRESH = 5;
/*    */   public static final int TYPE_LANGUAGEUPDATE = 6;
/*    */   public static final int TYPE_DESTROY = 7;
/*    */   public static final int TYPE_CLOSE = 8;
/*    */   public static final int TYPE_OBFUSCATE = 9;
/*    */   
/*    */   public abstract int getType();
/*    */   
/*    */   public abstract Object getData();
/*    */   
/*    */   public abstract UISWTView getView();
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/plugins/UISWTViewEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */