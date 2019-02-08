/*    */ package com.aelitis.azureus.launcher;
/*    */ 
/*    */ import com.aelitis.azureus.launcher.classloading.PeeringClassloader;
/*    */ import java.io.PrintStream;
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
/*    */ public class LauncherTest
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/* 29 */     System.out.println("current loader\t" + LauncherTest.class.getClassLoader());
/* 30 */     System.out.println("classloader's loader\t" + LauncherTest.class.getClassLoader().getClass().getClassLoader());
/* 31 */     System.out.println("classloader interface's loader\t" + PeeringClassloader.class.getClassLoader());
/*    */     
/* 33 */     if ((LauncherTest.class.getClassLoader() instanceof PeeringClassloader)) {
/* 34 */       System.out.println("success");
/*    */     }
/*    */     else {
/* 37 */       System.out.println("wrong classloader, invoking launcher");
/* 38 */       Launcher.launch(LauncherTest.class, args);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/launcher/LauncherTest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */