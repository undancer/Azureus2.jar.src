/*    */ package com.aelitis.azureus.core.cnetwork;
/*    */ 
/*    */ import com.aelitis.azureus.core.cnetwork.impl.ContentNetworkManagerImpl;
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
/*    */ public class ContentNetworkManagerFactory
/*    */ {
/*    */   public static void preInitialise() {}
/*    */   
/*    */   public static ContentNetworkManager getSingleton()
/*    */   {
/* 37 */     return ContentNetworkManagerImpl.getSingleton();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/cnetwork/ContentNetworkManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */