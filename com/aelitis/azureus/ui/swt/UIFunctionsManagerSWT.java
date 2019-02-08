/*    */ package com.aelitis.azureus.ui.swt;
/*    */ 
/*    */ import com.aelitis.azureus.ui.UIFunctions;
/*    */ import com.aelitis.azureus.ui.UIFunctionsManager;
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
/*    */ public class UIFunctionsManagerSWT
/*    */   extends UIFunctionsManager
/*    */ {
/*    */   public static UIFunctionsSWT getUIFunctionsSWT()
/*    */   {
/* 32 */     UIFunctions uiFunctions = getUIFunctions();
/*    */     
/* 34 */     if ((uiFunctions instanceof UIFunctionsSWT))
/*    */     {
/* 36 */       return (UIFunctionsSWT)uiFunctions;
/*    */     }
/*    */     
/* 39 */     return null;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/UIFunctionsManagerSWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */