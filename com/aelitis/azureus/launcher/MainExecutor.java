/*    */ package com.aelitis.azureus.launcher;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.lang.reflect.Method;
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
/*    */ public class MainExecutor
/*    */ {
/*    */   static void load(ClassLoader loader, final String mainClass, final String[] args)
/*    */   {
/* 29 */     Thread t = new Thread(new Runnable()
/*    */     {
/*    */       public void run() {
/*    */         try {
/* 33 */           Method main = this.val$loader.loadClass(mainClass).getMethod("main", new Class[] { String[].class });
/* 34 */           main.invoke(null, new Object[] { args });
/*    */         }
/*    */         catch (Exception e) {
/* 37 */           System.err.println("Invoking main failed");
/* 38 */           e.printStackTrace();
/* 39 */           System.exit(1); } } }, "MainRunner");
/*    */     
/*    */ 
/*    */ 
/* 43 */     t.setContextClassLoader(loader);
/* 44 */     t.start();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/launcher/MainExecutor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */