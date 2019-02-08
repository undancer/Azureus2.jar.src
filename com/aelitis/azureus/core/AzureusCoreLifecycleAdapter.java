/*    */ package com.aelitis.azureus.core;
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
/*    */ public class AzureusCoreLifecycleAdapter
/*    */   implements AzureusCoreLifecycleListener
/*    */ {
/*    */   public void componentCreated(AzureusCore core, AzureusCoreComponent component) {}
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
/*    */   public void started(AzureusCore core) {}
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
/*    */   public void stopping(AzureusCore core) {}
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
/*    */   public void stopped(AzureusCore core) {}
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
/*    */   public boolean stopRequested(AzureusCore core)
/*    */     throws AzureusCoreException
/*    */   {
/* 62 */     return true;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean restartRequested(AzureusCore core)
/*    */     throws AzureusCoreException
/*    */   {
/* 71 */     return true;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean syncInvokeRequired()
/*    */   {
/* 77 */     return false;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean requiresPluginInitCompleteBeforeStartedEvent()
/*    */   {
/* 83 */     return true;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/AzureusCoreLifecycleAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */