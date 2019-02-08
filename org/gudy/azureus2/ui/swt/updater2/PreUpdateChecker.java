/*    */ package org.gudy.azureus2.ui.swt.updater2;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
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
/*    */ 
/*    */ 
/*    */ public class PreUpdateChecker
/*    */ {
/*    */   public static void initialize(AzureusCore core, String ui)
/*    */   {
/* 40 */     if ((ui.equals("az3")) && (!"0".equals(System.getProperty("azureus.loadplugins")))) {}
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/updater2/PreUpdateChecker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */