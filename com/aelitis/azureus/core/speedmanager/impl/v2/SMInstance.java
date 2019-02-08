/*    */ package com.aelitis.azureus.core.speedmanager.impl.v2;
/*    */ 
/*    */ import com.aelitis.azureus.core.speedmanager.impl.SpeedManagerAlgorithmProviderAdapter;
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
/*    */ public class SMInstance
/*    */ {
/* 26 */   private static final SMInstance instance = new SMInstance();
/*    */   
/*    */   private static SpeedManagerAlgorithmProviderAdapter adapter;
/*    */   private static SMConfigurationAdapter conf;
/*    */   
/*    */   public static void init(SpeedManagerAlgorithmProviderAdapter _adapter)
/*    */   {
/* 33 */     adapter = _adapter;
/* 34 */     conf = new SMConfigurationAdapterImpl();
/*    */   }
/*    */   
/*    */   public static SMInstance getInstance() {
/* 38 */     return instance;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public SpeedManagerAlgorithmProviderAdapter getAdapter()
/*    */   {
/* 45 */     return adapter;
/*    */   }
/*    */   
/*    */   public SMConfigurationAdapter getConfigManager() {
/* 49 */     return conf;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/SMInstance.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */