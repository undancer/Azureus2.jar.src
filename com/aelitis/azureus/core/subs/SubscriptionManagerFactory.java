/*    */ package com.aelitis.azureus.core.subs;
/*    */ 
/*    */ import java.lang.reflect.Method;
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
/*    */ public class SubscriptionManagerFactory
/*    */ {
/*    */   private static final Class<SubscriptionManager> impl_class;
/*    */   private static SubscriptionManager singleton;
/*    */   
/*    */   static
/*    */   {
/* 33 */     String impl = System.getProperty("az.factory.subscriptionmanager.impl", "com.aelitis.azureus.core.subs.impl.SubscriptionManagerImpl");
/*    */     
/* 35 */     Class<SubscriptionManager> temp = null;
/*    */     
/* 37 */     if (impl.length() > 0) {
/*    */       try
/*    */       {
/* 40 */         temp = SubscriptionManagerFactory.class.getClassLoader().loadClass(impl);
/*    */       }
/*    */       catch (Throwable e)
/*    */       {
/* 44 */         Debug.out("Failed to load SubscriptionManager class: " + impl);
/*    */       }
/*    */     }
/*    */     
/* 48 */     impl_class = temp;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void preInitialise()
/*    */   {
/* 56 */     if (impl_class != null) {
/*    */       try
/*    */       {
/* 59 */         impl_class.getMethod("preInitialise", new Class[0]).invoke(null, (Object[])null);
/*    */       }
/*    */       catch (Throwable e)
/*    */       {
/* 63 */         Debug.out("preInitialise failed", e);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public static SubscriptionManager getSingleton()
/*    */   {
/* 71 */     synchronized (SubscriptionManagerFactory.class)
/*    */     {
/* 73 */       if (singleton != null)
/*    */       {
/* 75 */         return singleton;
/*    */       }
/*    */       
/* 78 */       if (impl_class == null)
/*    */       {
/* 80 */         throw new RuntimeException("No Implementation");
/*    */       }
/*    */       try
/*    */       {
/* 84 */         singleton = (SubscriptionManager)impl_class.getMethod("getSingleton", new Class[] { Boolean.TYPE }).invoke(null, new Object[] { Boolean.valueOf(false) });
/*    */         
/* 86 */         return singleton;
/*    */       }
/*    */       catch (Throwable e)
/*    */       {
/* 90 */         throw new RuntimeException("No Implementation", e);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/SubscriptionManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */