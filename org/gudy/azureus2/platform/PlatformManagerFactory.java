/*     */ package org.gudy.azureus2.platform;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.platform.dummy.PlatformManagerImpl;
/*     */ import org.gudy.azureus2.plugins.platform.PlatformManagerException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PlatformManagerFactory
/*     */ {
/*     */   protected static PlatformManager platform_manager;
/*  38 */   protected static AEMonitor class_mon = new AEMonitor("PlatformManagerFactory");
/*     */   
/*     */   public static PlatformManager getPlatformManager()
/*     */   {
/*     */     try
/*     */     {
/*  44 */       boolean force_dummy = System.getProperty("azureus.platform.manager.disable", "false").equals("true");
/*     */       
/*  46 */       class_mon.enter();
/*     */       
/*  48 */       if ((platform_manager == null) && (!force_dummy)) {
/*     */         try
/*     */         {
/*  51 */           String cla = System.getProperty("az.factory.platformmanager.impl", "");
/*     */           
/*  53 */           boolean explicit_class = cla.length() > 0;
/*     */           
/*  55 */           if (!explicit_class) {
/*  56 */             int platformType = getPlatformType();
/*  57 */             switch (platformType) {
/*     */             case 1: 
/*  59 */               cla = "org.gudy.azureus2.platform.win32.PlatformManagerImpl";
/*  60 */               break;
/*     */             case 3: 
/*  62 */               cla = "org.gudy.azureus2.platform.macosx.PlatformManagerImpl";
/*  63 */               break;
/*     */             case 4: 
/*  65 */               cla = "org.gudy.azureus2.platform.unix.PlatformManagerImpl";
/*  66 */               break;
/*     */             case 2: default: 
/*  68 */               cla = "org.gudy.azureus2.platform.dummy.PlatformManagerImpl";
/*     */             }
/*     */             
/*     */           }
/*     */           
/*  73 */           Class<?> platform_manager_class = Class.forName(cla);
/*     */           try {
/*  75 */             Method methGetSingleton = platform_manager_class.getMethod("getSingleton", new Class[0]);
/*  76 */             platform_manager = (PlatformManager)methGetSingleton.invoke(null, new Object[0]);
/*     */           }
/*     */           catch (NoSuchMethodException e) {}catch (SecurityException e) {}catch (IllegalAccessException e) {}catch (IllegalArgumentException e) {}catch (InvocationTargetException e) {}
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  84 */           if (explicit_class)
/*     */           {
/*     */ 
/*     */ 
/*  88 */             if (platform_manager == null) {
/*  89 */               platform_manager = (PlatformManager)Class.forName(cla).newInstance();
/*     */             }
/*     */             
/*     */           }
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/*  96 */           if (!(e instanceof PlatformManagerException)) {
/*  97 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 102 */       if (platform_manager == null)
/*     */       {
/* 104 */         platform_manager = PlatformManagerImpl.getSingleton();
/*     */       }
/*     */       
/* 107 */       return platform_manager;
/*     */     }
/*     */     finally
/*     */     {
/* 111 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static int getPlatformType()
/*     */   {
/* 118 */     if (Constants.isWindows)
/*     */     {
/* 120 */       return 1;
/*     */     }
/* 122 */     if (Constants.isOSX)
/*     */     {
/* 124 */       return 3;
/*     */     }
/* 126 */     if (Constants.isUnix)
/*     */     {
/* 128 */       return 4;
/*     */     }
/*     */     
/* 131 */     return 2;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/PlatformManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */