/*    */ package com.aelitis.azureus.plugins.magnet.swt;
/*    */ 
/*    */ import org.eclipse.swt.graphics.Image;
/*    */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*    */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
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
/*    */ public class MagnetPluginUISWT
/*    */ {
/*    */   public MagnetPluginUISWT(UIInstance instance, TableContextMenuItem[] menus)
/*    */   {
/* 37 */     UISWTInstance swt = (UISWTInstance)instance;
/*    */     
/* 39 */     Image image = swt.loadImage("com/aelitis/azureus/plugins/magnet/icons/magnet.gif");
/*    */     
/* 41 */     for (TableContextMenuItem menu : menus)
/*    */     {
/* 43 */       menu.setGraphic(swt.createGraphic(image));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/magnet/swt/MagnetPluginUISWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */