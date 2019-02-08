/*    */ package com.aelitis.azureus.core.instancemanager;
/*    */ 
/*    */ import com.aelitis.azureus.core.instancemanager.impl.AZInstanceManagerImpl;
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
/*    */ public class AZInstanceManagerFactory
/*    */ {
/*    */   public static AZInstanceManager getSingleton(AZInstanceManagerAdapter adapter)
/*    */   {
/* 31 */     return AZInstanceManagerImpl.getSingleton(adapter);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/instancemanager/AZInstanceManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */