/*    */ package com.aelitis.azureus.ui.swt.shells.main;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import com.aelitis.azureus.ui.IUIIntializer;
/*    */ import org.eclipse.swt.widgets.Display;
/*    */ import org.gudy.azureus2.core3.util.AERunStateHandler;
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
/*    */ public class MainWindowFactory
/*    */ {
/*    */   private static final boolean isImmediate()
/*    */   {
/* 35 */     return !AERunStateHandler.isDelayedUI();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static MainWindow create(AzureusCore core, Display display, IUIIntializer uiInitializer)
/*    */   {
/* 44 */     if (isImmediate())
/*    */     {
/* 46 */       return new MainWindowImpl(core, display, uiInitializer);
/*    */     }
/*    */     
/*    */ 
/* 50 */     return new MainWindowDelayStub(core, display, uiInitializer);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static MainWindowInitStub createAsync(Display display, IUIIntializer uiInitializer)
/*    */   {
/*    */     MainWindow window;
/*    */     
/*    */     MainWindow window;
/*    */     
/* 61 */     if (isImmediate())
/*    */     {
/* 63 */       window = new MainWindowImpl(display, uiInitializer);
/*    */     }
/*    */     else
/*    */     {
/* 67 */       window = new MainWindowDelayStub(display, uiInitializer);
/*    */     }
/*    */     
/* 70 */     new MainWindowInitStub()
/*    */     {
/*    */ 
/*    */ 
/*    */       public void init(AzureusCore core)
/*    */       {
/*    */ 
/* 77 */         this.val$window.init(core);
/*    */       }
/*    */     };
/*    */   }
/*    */   
/*    */   public static abstract interface MainWindowInitStub
/*    */   {
/*    */     public abstract void init(AzureusCore paramAzureusCore);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/main/MainWindowFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */