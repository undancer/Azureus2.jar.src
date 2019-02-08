/*    */ package com.aelitis.azureus.core.metasearch;
/*    */ 
/*    */ import com.aelitis.azureus.core.metasearch.impl.MetaSearchManagerImpl;
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
/*    */ public class MetaSearchManagerFactory
/*    */ {
/*    */   public static void preInitialise() {}
/*    */   
/*    */   public static MetaSearchManager getSingleton()
/*    */   {
/* 37 */     return MetaSearchManagerImpl.getSingleton();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/MetaSearchManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */