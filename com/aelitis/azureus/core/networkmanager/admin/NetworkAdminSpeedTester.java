/*    */ package com.aelitis.azureus.core.networkmanager.admin;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract interface NetworkAdminSpeedTester
/*    */ {
/*    */   public static final int TEST_TYPE_UPLOAD_ONLY = 0;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int TEST_TYPE_DOWNLOAD_ONLY = 1;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 30 */   public static final int[] TEST_TYPES = { 0, 1 };
/*    */   
/*    */   public abstract int getTestType();
/*    */   
/*    */   public abstract void setMode(int paramInt);
/*    */   
/*    */   public abstract int getMode();
/*    */   
/*    */   public abstract void setUseCrypto(boolean paramBoolean);
/*    */   
/*    */   public abstract boolean getUseCrypto();
/*    */   
/*    */   public abstract void addListener(NetworkAdminSpeedTesterListener paramNetworkAdminSpeedTesterListener);
/*    */   
/*    */   public abstract void removeListener(NetworkAdminSpeedTesterListener paramNetworkAdminSpeedTesterListener);
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/NetworkAdminSpeedTester.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */