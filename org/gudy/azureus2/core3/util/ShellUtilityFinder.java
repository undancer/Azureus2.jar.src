/*    */ package org.gudy.azureus2.core3.util;
/*    */ 
/*    */ import java.io.File;
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
/*    */ public class ShellUtilityFinder
/*    */ {
/*    */   public static String getChMod()
/*    */   {
/* 27 */     return findCommand("chmod");
/*    */   }
/*    */   
/*    */   public static String getNice() {
/* 31 */     return findCommand("nice");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static String findCommand(String name)
/*    */   {
/* 38 */     String[] locations = { "/bin", "/usr/bin" };
/* 39 */     for (String s : locations) {
/* 40 */       File f = new File(s, name);
/* 41 */       if ((f.exists()) && (f.canRead())) {
/* 42 */         return f.getAbsolutePath();
/*    */       }
/*    */     }
/* 45 */     return name;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/ShellUtilityFinder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */