/*    */ package org.gudy.azureus2.ui.common;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import com.aelitis.azureus.core.AzureusCoreException;
/*    */ import java.util.Date;
/*    */ import java.util.HashMap;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.gudy.azureus2.core3.global.GlobalManager;
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
/*    */ public class UIConst
/*    */ {
/*    */   public static Date startTime;
/*    */   public static HashMap UIS;
/*    */   private static AzureusCore azureus_core;
/*    */   private static boolean must_init_core;
/*    */   
/*    */   public static synchronized void setAzureusCore(AzureusCore _azureus_core)
/*    */   {
/* 49 */     azureus_core = _azureus_core;
/* 50 */     must_init_core = !azureus_core.isStarted();
/*    */   }
/*    */   
/*    */ 
/*    */   public static synchronized AzureusCore getAzureusCore()
/*    */   {
/* 56 */     if (must_init_core) {
/* 57 */       try { azureus_core.start();
/*    */       } catch (AzureusCoreException e) {
/* 59 */         Logger.getLogger("azureus2").error("Start fails", e);
/*    */       }
/* 61 */       must_init_core = false;
/*    */     }
/* 63 */     return azureus_core;
/*    */   }
/*    */   
/*    */ 
/*    */   public static synchronized GlobalManager getGlobalManager()
/*    */   {
/* 69 */     return azureus_core.getGlobalManager();
/*    */   }
/*    */   
/*    */ 
/*    */   public static void shutdown() {}
/*    */   
/*    */ 
/*    */   public static synchronized boolean startUI(String ui, String[] args)
/*    */   {
/* 78 */     if (UIS.containsKey(ui))
/* 79 */       return false;
/* 80 */     IUserInterface uif = UserInterfaceFactory.getUI(ui);
/* 81 */     uif.init(false, true);
/* 82 */     if (args != null)
/* 83 */       uif.processArgs(args);
/* 84 */     uif.startUI();
/* 85 */     UIS.put(ui, uif);
/* 86 */     return true;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/common/UIConst.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */