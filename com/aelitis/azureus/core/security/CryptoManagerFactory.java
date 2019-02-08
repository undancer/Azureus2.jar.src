/*    */ package com.aelitis.azureus.core.security;
/*    */ 
/*    */ import com.aelitis.azureus.core.security.impl.CryptoManagerImpl;
/*    */ import org.gudy.azureus2.core3.util.Debug;
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
/*    */ public class CryptoManagerFactory
/*    */ {
/*    */   public static CryptoManager getSingleton()
/*    */   {
/*    */     try
/*    */     {
/* 33 */       return CryptoManagerImpl.getSingleton();
/*    */     }
/*    */     catch (Throwable e)
/*    */     {
/* 37 */       Debug.out("Failed to create crypto manager", e);
/*    */     }
/* 39 */     return null;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/security/CryptoManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */