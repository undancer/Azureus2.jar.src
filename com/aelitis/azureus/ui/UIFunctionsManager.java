/*    */ package com.aelitis.azureus.ui;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ 
/*    */ public class UIFunctionsManager
/*    */ {
/* 30 */   private static UIFunctions instance = null;
/*    */   
/* 32 */   private static List<UIFCallback> callbacks = null;
/*    */   
/*    */ 
/*    */ 
/*    */   public static void execWithUIFunctions(UIFCallback cb)
/*    */   {
/*    */     UIFunctions current_instance;
/*    */     
/* 40 */     synchronized (UIFunctionsManager.class)
/*    */     {
/* 42 */       current_instance = instance;
/*    */       
/* 44 */       if (current_instance == null)
/*    */       {
/* 46 */         if (callbacks == null)
/*    */         {
/* 48 */           callbacks = new ArrayList();
/*    */         }
/*    */         
/* 51 */         callbacks.add(cb);
/*    */         
/* 53 */         return;
/*    */       }
/*    */     }
/*    */     
/* 57 */     cb.run(current_instance);
/*    */   }
/*    */   
/*    */ 
/*    */   public static UIFunctions getUIFunctions()
/*    */   {
/* 63 */     UIFunctions result = instance;
/*    */     
/* 65 */     return result;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static void setUIFunctions(UIFunctions uiFunctions)
/*    */   {
/* 72 */     List<UIFCallback> pending = null;
/*    */     
/* 74 */     synchronized (UIFunctionsManager.class)
/*    */     {
/* 76 */       instance = uiFunctions;
/*    */       
/* 78 */       if (callbacks != null)
/*    */       {
/* 80 */         pending = new ArrayList(callbacks);
/*    */         
/* 82 */         callbacks = null;
/*    */       }
/*    */     }
/*    */     
/* 86 */     if (pending != null)
/*    */     {
/* 88 */       for (UIFCallback cb : pending) {
/*    */         try
/*    */         {
/* 91 */           cb.run(uiFunctions);
/*    */         }
/*    */         catch (Throwable e)
/*    */         {
/* 95 */           Debug.out(e);
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public static abstract interface UIFCallback
/*    */   {
/*    */     public abstract void run(UIFunctions paramUIFunctions);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/UIFunctionsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */