/*    */ package org.gudy.azureus2.core3.util;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DebugLight
/*    */ {
/*    */   public static void printStackTrace(Throwable e)
/*    */   {
/*    */     try
/*    */     {
/* 35 */       Debug.printStackTrace(e);
/*    */     }
/*    */     catch (Throwable f)
/*    */     {
/* 39 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public static void out(String str)
/*    */   {
/*    */     try
/*    */     {
/* 48 */       Debug.out(str);
/*    */     }
/*    */     catch (Throwable f)
/*    */     {
/* 52 */       System.out.println(str);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/DebugLight.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */