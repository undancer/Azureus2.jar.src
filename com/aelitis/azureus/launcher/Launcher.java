/*     */ package com.aelitis.azureus.launcher;
/*     */ 
/*     */ import com.aelitis.azureus.launcher.classloading.PeeringClassloader;
/*     */ import com.aelitis.azureus.launcher.classloading.PrimaryClassloader;
/*     */ import com.aelitis.azureus.launcher.classloading.SecondaryClassLoader;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
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
/*     */ public class Launcher
/*     */ {
/*  33 */   private static final String OSName = System.getProperty("os.name");
/*  34 */   private static final boolean isOSX = OSName.toLowerCase().startsWith("mac os");
/*  35 */   private static final boolean LOADER_ENABLED = !isOSX;
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
/*     */   public static void launch(Class MainClass, String[] args)
/*     */   {
/*  50 */     ClassLoader primaryloader = PrimaryClassloader.getBootstrappedLoader();
/*     */     try
/*     */     {
/*  53 */       Method mainWrapper = primaryloader.loadClass(MainExecutor.class.getName()).getDeclaredMethod("load", new Class[] { ClassLoader.class, String.class, String[].class });
/*  54 */       mainWrapper.setAccessible(true);
/*  55 */       mainWrapper.invoke(null, new Object[] { primaryloader, MainClass.getName(), args });
/*     */     }
/*     */     catch (Exception e) {
/*  58 */       System.err.println("Bootstrapping failed");
/*  59 */       e.printStackTrace();
/*  60 */       System.exit(1);
/*     */     }
/*     */   }
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
/*     */   public static boolean checkAndLaunch(Class MainClass, String[] args)
/*     */   {
/*  87 */     if (isBootStrapped())
/*  88 */       return false;
/*  89 */     launch(MainClass, args);
/*  90 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isBootStrapped()
/*     */   {
/*  99 */     if ((!LOADER_ENABLED) || ((ClassLoaderWitness.class.getClassLoader() instanceof PeeringClassloader)))
/* 100 */       return true;
/* 101 */     return false;
/*     */   }
/*     */   
/*     */   public static SecondaryClassLoader getComponentLoader(URL[] urls)
/*     */   {
/* 106 */     if (!isBootStrapped())
/* 107 */       throw new IllegalStateException("Current Classloader is not part of the peering hierarchy!");
/* 108 */     ClassLoader primary = ClassLoaderWitness.class.getClassLoader();
/* 109 */     while (!(primary instanceof PrimaryClassloader))
/* 110 */       primary = primary.getParent();
/* 111 */     return new SecondaryClassLoader(urls, (PrimaryClassloader)primary);
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/launcher/Launcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */