/*    */ package com.aelitis.azureus.core.proxy;
/*    */ 
/*    */ import com.aelitis.azureus.core.proxy.impl.AEProxySelectorImpl;
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
/*    */ public class AEProxySelectorFactory
/*    */ {
/*    */   public static AEProxySelector getSelector()
/*    */   {
/* 31 */     return AEProxySelectorImpl.getSingleton();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/AEProxySelectorFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */