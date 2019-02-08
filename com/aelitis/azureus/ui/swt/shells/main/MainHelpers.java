/*    */ package com.aelitis.azureus.ui.swt.shells.main;
/*    */ 
/*    */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*    */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
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
/*    */ public class MainHelpers
/*    */ {
/*    */   private static boolean done_xfer_bar;
/*    */   
/*    */   protected static void initTransferBar()
/*    */   {
/* 36 */     UIFunctionsSWT ui_functions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*    */     
/* 38 */     if (ui_functions == null)
/*    */     {
/* 40 */       return;
/*    */     }
/*    */     
/* 43 */     synchronized (MainHelpers.class)
/*    */     {
/* 45 */       if (done_xfer_bar)
/*    */       {
/* 47 */         return;
/*    */       }
/*    */       
/* 50 */       done_xfer_bar = true;
/*    */     }
/*    */     
/* 53 */     if (COConfigurationManager.getBooleanParameter("Open Transfer Bar On Start"))
/*    */     {
/* 55 */       ui_functions.showGlobalTransferBar();
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/main/MainHelpers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */