/*    */ package org.gudy.azureus2.platform.macosx;
/*    */ 
/*    */ import java.io.File;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class NativeInvocationBridge
/*    */ {
/*    */   private static NativeInvocationBridge instance;
/*    */   
/*    */   protected static final NativeInvocationBridge sharedInstance()
/*    */   {
/* 42 */     if (instance == null) {
/*    */       try {
/* 44 */         Object newInstance = Class.forName("org.gudy.azureus2.platform.macosx.access.cocoa.CocoaJavaBridge").getConstructor(new Class[0]).newInstance(new Object[0]);
/*    */         
/* 46 */         instance = (NativeInvocationBridge)newInstance;
/*    */       }
/*    */       catch (Throwable e) {
/* 49 */         instance = new DummyBridge(null);
/*    */       }
/*    */     }
/* 52 */     return instance;
/*    */   }
/*    */   
/*    */   protected static final boolean hasSharedInstance() {
/* 56 */     return instance != null;
/*    */   }
/*    */   
/*    */ 
/*    */   protected boolean performRecoverableFileDelete(File path)
/*    */   {
/* 62 */     return false;
/*    */   }
/*    */   
/*    */   protected boolean showInFinder(File path, String fb)
/*    */   {
/* 67 */     return false;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected abstract boolean isEnabled();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected void dispose() {}
/*    */   
/*    */ 
/*    */ 
/*    */   private static class DummyBridge
/*    */     extends NativeInvocationBridge
/*    */   {
/*    */     public boolean isEnabled()
/*    */     {
/* 87 */       return false;
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/macosx/NativeInvocationBridge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */