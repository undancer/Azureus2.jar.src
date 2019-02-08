/*    */ package com.aelitis.azureus.ui;
/*    */ 
/*    */ import java.lang.reflect.Constructor;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Main
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/*    */     try
/*    */     {
/* 38 */       Class startupClass = Class.forName("org.gudy.azureus2.ui.swt.Main");
/*    */       
/* 40 */       Constructor constructor = startupClass.getConstructor(new Class[] { String[].class });
/*    */       
/*    */ 
/*    */ 
/* 44 */       constructor.newInstance(new Object[] { args });
/*    */ 
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/*    */ 
/* 50 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/Main.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */