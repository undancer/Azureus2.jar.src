/*     */ package com.aelitis.azureus.core.devices;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DeviceManagerFactory
/*     */ {
/*     */   private static final Class<DeviceManager> impl_class;
/*     */   private static DeviceManager singleton;
/*     */   
/*     */   static
/*     */   {
/*  34 */     String impl = System.getProperty("az.factory.devicemanager.impl", "com.aelitis.azureus.core.devices.impl.DeviceManagerImpl");
/*     */     
/*  36 */     Class<DeviceManager> temp = null;
/*     */     
/*  38 */     if (impl.length() > 0) {
/*     */       try
/*     */       {
/*  41 */         temp = DeviceManagerFactory.class.getClassLoader().loadClass(impl);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*  45 */         Debug.out("Failed to load DeviceManagerFactory class: " + impl);
/*     */       }
/*     */     }
/*     */     
/*  49 */     impl_class = temp;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void preInitialise()
/*     */   {
/*  57 */     if (impl_class != null) {
/*     */       try
/*     */       {
/*  60 */         impl_class.getMethod("preInitialise", new Class[0]).invoke(null, (Object[])null);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*  64 */         Debug.out("preInitialise failed", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static DeviceManager getSingleton()
/*     */   {
/*  72 */     synchronized (DeviceManagerFactory.class)
/*     */     {
/*  74 */       if (singleton != null)
/*     */       {
/*  76 */         return singleton;
/*     */       }
/*     */       
/*  79 */       if (impl_class == null)
/*     */       {
/*  81 */         throw new RuntimeException("No Implementation");
/*     */       }
/*     */       
/*  84 */       boolean isAZ3 = COConfigurationManager.getStringParameter("ui").equals("az3");
/*     */       
/*  86 */       if (!isAZ3)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  92 */         Debug.out("DeviceManager is only fully functional with Vuze UI - some features unavailable");
/*     */         
/*  94 */         return null;
/*     */       }
/*     */       try
/*     */       {
/*  98 */         singleton = (DeviceManager)impl_class.getMethod("getSingleton", new Class[0]).invoke(null, (Object[])null);
/*     */         
/* 100 */         return singleton;
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 104 */         throw new RuntimeException("No Implementation", e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/DeviceManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */