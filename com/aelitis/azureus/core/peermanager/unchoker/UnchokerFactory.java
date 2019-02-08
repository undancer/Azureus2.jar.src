/*    */ package com.aelitis.azureus.core.peermanager.unchoker;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ public class UnchokerFactory
/*    */ {
/*    */   public static final String DEFAULT_MANAGER = "com.aelitis.azureus.core.peermanager.unchoker.UnchokerFactory";
/* 31 */   private static UnchokerFactory factory = getSingleton(null);
/*    */   
/*    */ 
/*    */ 
/*    */   public static UnchokerFactory getSingleton()
/*    */   {
/* 37 */     return factory;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   private static UnchokerFactory getSingleton(String explicit_implementation)
/*    */   {
/* 44 */     String impl = explicit_implementation;
/*    */     
/* 46 */     if (impl == null)
/*    */     {
/* 48 */       impl = System.getProperty("com.aelitis.azureus.core.peermanager.unchoker.UnchokerFactory");
/*    */     }
/*    */     
/* 51 */     if (impl == null)
/*    */     {
/* 53 */       impl = "com.aelitis.azureus.core.peermanager.unchoker.UnchokerFactory";
/*    */     }
/*    */     try
/*    */     {
/* 57 */       Class impl_class = UnchokerFactory.class.getClassLoader().loadClass(impl);
/*    */       
/* 59 */       factory = (UnchokerFactory)impl_class.newInstance();
/*    */     }
/*    */     catch (Throwable e)
/*    */     {
/* 63 */       Debug.out("Failed to instantiate unchoker factory '" + impl + "'", e);
/*    */       
/* 65 */       factory = new UnchokerFactory();
/*    */     }
/*    */     
/* 68 */     return factory;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Unchoker getUnchoker(boolean seeding)
/*    */   {
/* 75 */     if (seeding)
/*    */     {
/* 77 */       return new SeedingUnchoker();
/*    */     }
/*    */     
/*    */ 
/* 81 */     return new DownloadingUnchoker();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/unchoker/UnchokerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */