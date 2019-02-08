/*    */ package com.aelitis.azureus.launcher.classloading;
/*    */ 
/*    */ import java.net.URL;
/*    */ import java.net.URLClassLoader;
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
/*    */ public class SecondaryClassLoader
/*    */   extends URLClassLoader
/*    */   implements PeeringClassloader
/*    */ {
/*    */   public SecondaryClassLoader(URL[] urls, PrimaryClassloader parent)
/*    */   {
/* 30 */     super(urls, parent);
/* 31 */     parent.registerSecondaryClassloader(this);
/*    */   }
/*    */   
/*    */   Class findLoadedClassHelper(String name)
/*    */   {
/* 36 */     return findLoadedClass(name);
/*    */   }
/*    */   
/*    */   Class findClassHelper(String name)
/*    */   {
/*    */     try
/*    */     {
/* 43 */       return findClass(name);
/*    */     }
/*    */     catch (ClassNotFoundException e) {}
/* 46 */     return null;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/launcher/classloading/SecondaryClassLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */