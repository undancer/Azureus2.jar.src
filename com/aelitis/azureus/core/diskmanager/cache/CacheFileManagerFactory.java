/*    */ package com.aelitis.azureus.core.diskmanager.cache;
/*    */ 
/*    */ import org.gudy.azureus2.core3.util.AEMonitor;
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
/*    */ public class CacheFileManagerFactory
/*    */ {
/*    */   public static final String DEFAULT_MANAGER = "com.aelitis.azureus.core.diskmanager.cache.impl.CacheFileManagerImpl";
/*    */   private static CacheFileManager manager;
/* 35 */   private static final AEMonitor class_mon = new AEMonitor("CacheFileManagerFactory");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static CacheFileManager getSingleton()
/*    */     throws CacheFileManagerException
/*    */   {
/* 43 */     return getSingleton(null);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static CacheFileManager getSingleton(String explicit_implementation)
/*    */     throws CacheFileManagerException
/*    */   {
/*    */     try
/*    */     {
/* 53 */       class_mon.enter();
/*    */       String impl;
/* 55 */       if (manager == null)
/*    */       {
/* 57 */         impl = explicit_implementation;
/*    */         
/* 59 */         if (impl == null)
/*    */         {
/* 61 */           impl = System.getProperty("com.aelitis.azureus.core.diskmanager.cache.manager");
/*    */         }
/*    */         
/* 64 */         if (impl == null)
/*    */         {
/* 66 */           impl = "com.aelitis.azureus.core.diskmanager.cache.impl.CacheFileManagerImpl";
/*    */         }
/*    */         try
/*    */         {
/* 70 */           Class impl_class = CacheFileManagerFactory.class.getClassLoader().loadClass(impl);
/*    */           
/* 72 */           manager = (CacheFileManager)impl_class.newInstance();
/*    */         }
/*    */         catch (Throwable e)
/*    */         {
/* 76 */           throw new CacheFileManagerException(null, "Failed to instantiate manager '" + impl + "'", e);
/*    */         }
/*    */       }
/*    */       
/* 80 */       return manager;
/*    */     }
/*    */     finally
/*    */     {
/* 84 */       class_mon.exit();
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/cache/CacheFileManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */