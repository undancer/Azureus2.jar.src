/*    */ package com.aelitis.azureus.ui.swt.utils;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import com.aelitis.net.magneturi.MagnetURIHandler;
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*    */ import org.gudy.azureus2.core3.util.Constants;
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
/*    */ public class UIMagnetHandler
/*    */ {
/*    */   public UIMagnetHandler(AzureusCore core)
/*    */   {
/* 41 */     int val = Integer.parseInt(Constants.getBaseVersion().replaceAll("\\.", ""));
/*    */     
/* 43 */     String ui = COConfigurationManager.getStringParameter("ui");
/* 44 */     if (!"az2".equals(ui)) {
/* 45 */       val += 10000;
/*    */     }
/*    */     
/* 48 */     MagnetURIHandler magnetURIHandler = MagnetURIHandler.getSingleton();
/* 49 */     magnetURIHandler.addInfo("get-version-info", val);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/utils/UIMagnetHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */