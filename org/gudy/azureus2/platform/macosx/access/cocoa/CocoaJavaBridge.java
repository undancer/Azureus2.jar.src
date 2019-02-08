/*     */ package org.gudy.azureus2.platform.macosx.access.cocoa;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.text.MessageFormat;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.platform.macosx.NativeInvocationBridge;
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
/*     */ public final class CocoaJavaBridge
/*     */   extends NativeInvocationBridge
/*     */ {
/*     */   protected static final String CLASS_PATH = "/system/library/java";
/*     */   private static final String REVEAL_SCRIPT_FORMAT = "tell application \"System Events\"\ntell application \"{0}\"\nactivate\nreveal (posix file \"{1}\" as alias)\nend tell\nend tell";
/*     */   private static final String DEL_SCRIPT_FORMAT = "tell application \"Finder\" to move (posix file \"{0}\" as alias) to the trash";
/*     */   private int mainPool;
/*  60 */   protected AEMonitor classMon = new AEMonitor("CocoaJavaBridge:C");
/*  61 */   private AEMonitor scriptMon = new AEMonitor("CocoaJavaBridge:S");
/*     */   
/*  63 */   protected boolean isDisposed = false;
/*     */   
/*     */   protected RunnableDispatcher scriptDispatcher;
/*     */   
/*     */   private Class claNSAppleEventDescriptor;
/*     */   
/*     */   private Class<?> claNSAutoreleasePool;
/*     */   
/*     */   private Method methPush;
/*     */   
/*     */   private Method methPop;
/*     */   
/*     */   private Method methNSAppleEventDescriptor_descriptorWithBoolean;
/*     */   
/*     */   private Class<?> claNSAppleScript;
/*     */   
/*     */   private Class<?> claNSMutableDictionary;
/*     */   
/*     */   private Method methNSAppleScript_execute;
/*     */   
/*     */   private String NSAppleScript_AppleScriptErrorMessage;
/*     */   private Method methNSMutableDictionary_objectForKey;
/*     */   
/*     */   public CocoaJavaBridge()
/*     */     throws Throwable
/*     */   {
/*     */     try
/*     */     {
/*  91 */       this.classMon.enter();
/*     */       
/*  93 */       this.claNSMutableDictionary = Class.forName("com.apple.cocoa.foundation.NSMutableDictionary");
/*  94 */       this.methNSMutableDictionary_objectForKey = this.claNSMutableDictionary.getMethod("objectForKey", new Class[] { Object.class });
/*     */       
/*  96 */       this.claNSAppleEventDescriptor = Class.forName("com.apple.cocoa.foundation.NSAppleEventDescriptor");
/*  97 */       this.methNSAppleEventDescriptor_descriptorWithBoolean = this.claNSAppleEventDescriptor.getMethod("descriptorWithBoolean", new Class[] { Boolean.TYPE });
/*     */       
/*  99 */       this.claNSAutoreleasePool = Class.forName("com.apple.cocoa.foundation.NSAutoreleasePool");
/* 100 */       this.methPush = this.claNSAutoreleasePool.getMethod("push", new Class[0]);
/* 101 */       this.methPop = this.claNSAutoreleasePool.getMethod("pop", new Class[] { Integer.TYPE });
/*     */       
/* 103 */       this.claNSAppleScript = Class.forName("com.apple.cocoa.foundation.NSAppleScript");
/* 104 */       this.methNSAppleScript_execute = this.claNSAppleScript.getMethod("execute", new Class[] { this.claNSMutableDictionary });
/* 105 */       this.NSAppleScript_AppleScriptErrorMessage = ((String)this.claNSAppleScript.getField("AppleScriptErrorMessage").get(null));
/*     */       
/*     */ 
/*     */ 
/* 109 */       this.mainPool = NSAutoreleasePool_push();
/*     */       
/* 111 */       this.scriptDispatcher = new RunnableDispatcher(null);
/*     */     }
/*     */     finally
/*     */     {
/* 115 */       this.classMon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   private int NSAutoreleasePool_push() throws Throwable
/*     */   {
/* 121 */     return ((Number)this.methPush.invoke(null, new Object[0])).intValue();
/*     */   }
/*     */   
/*     */   private void NSAutoreleasePool_pop(int i) throws Throwable
/*     */   {
/* 126 */     this.methPop.invoke(null, new Object[] { Integer.valueOf(i) });
/*     */   }
/*     */   
/*     */   private Object new_NSAppleScript(String s) throws Throwable {
/* 130 */     return this.claNSAppleScript.getConstructor(new Class[] { String.class }).newInstance(new Object[] { s });
/*     */   }
/*     */   
/*     */   private Object NSAppleScript_execute(Object NSAppleScript, Object NSMutableDictionary) throws Throwable {
/* 134 */     return this.methNSAppleScript_execute.invoke(NSAppleScript, new Object[] { NSMutableDictionary });
/*     */   }
/*     */   
/*     */   private Object new_NSMutableDictionary() throws Throwable {
/* 138 */     return this.claNSMutableDictionary.newInstance();
/*     */   }
/*     */   
/*     */   private Object NSMutableDictionary_objectForKey(Object NSMutableDictionary, String s) throws Throwable {
/* 142 */     return this.methNSMutableDictionary_objectForKey.invoke(NSMutableDictionary, new Object[] { s });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean performRecoverableFileDelete(File path)
/*     */   {
/* 152 */     if (!path.exists()) {
/* 153 */       return false;
/*     */     }
/*     */     Object result;
/*     */     try
/*     */     {
/* 158 */       result = executeScriptWithAsync("tell application \"Finder\" to move (posix file \"{0}\" as alias) to the trash", new Object[] { path.getAbsolutePath() });
/*     */     } catch (Throwable t) {
/* 160 */       Debug.out(t);
/* 161 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 167 */     if (result != null)
/*     */     {
/* 169 */       int sleep = 25;
/*     */       
/* 171 */       int sleep_to_go = 2500;
/*     */       for (;;) {
/* 173 */         if (path.exists())
/*     */         {
/* 175 */           if (sleep_to_go > 0)
/*     */           {
/*     */ 
/*     */             try
/*     */             {
/*     */ 
/* 181 */               Thread.sleep(25L);
/*     */               
/* 183 */               sleep_to_go -= 25;
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 190 */       if (path.exists())
/*     */       {
/* 192 */         Debug.outNoStack("Gave up waiting for delete to complete for " + path);
/*     */       }
/*     */     }
/*     */     
/* 196 */     return result != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean showInFinder(File path, String fileBrowserApp)
/*     */   {
/* 203 */     if (!path.exists()) {
/* 204 */       return false;
/*     */     }
/* 206 */     Object result = null;
/*     */     try {
/* 208 */       int pool = NSAutoreleasePool_push();
/*     */       try {
/* 210 */         result = executeScriptWithAsync("tell application \"System Events\"\ntell application \"{0}\"\nactivate\nreveal (posix file \"{1}\" as alias)\nend tell\nend tell", new Object[] { fileBrowserApp, path.getAbsolutePath() });
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/* 215 */         NSAutoreleasePool_pop(pool);
/*     */       }
/*     */     } catch (Throwable t) {
/* 218 */       return false;
/*     */     }
/* 220 */     return result != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean isEnabled()
/*     */   {
/* 228 */     return this.claNSAutoreleasePool != null;
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
/*     */   protected final Object executeScript(String scriptFormat, Object[] params)
/*     */     throws Throwable
/*     */   {
/*     */     try
/*     */     {
/* 245 */       this.scriptMon.enter();
/*     */       
/* 247 */       int pool = NSAutoreleasePool_push();
/* 248 */       long start = System.currentTimeMillis();
/*     */       String src;
/*     */       String src;
/* 251 */       if ((params == null) || (params.length == 0))
/*     */       {
/* 253 */         src = scriptFormat;
/*     */       }
/*     */       else
/*     */       {
/* 257 */         src = MessageFormat.format(scriptFormat, params);
/*     */       }
/*     */       
/* 260 */       Debug.outNoStack("Executing: \n" + src);
/*     */       
/* 262 */       Object scp = new_NSAppleScript(src);
/* 263 */       Object result = NSAppleScript_execute(scp, new_NSMutableDictionary());
/*     */       
/* 265 */       Debug.outNoStack(MessageFormat.format("Elapsed time: {0}ms\n", new Object[] { new Long(System.currentTimeMillis() - start) }));
/* 266 */       NSAutoreleasePool_pop(pool);
/* 267 */       return result;
/*     */     }
/*     */     finally
/*     */     {
/* 271 */       this.scriptMon.exit();
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
/*     */   protected final Object executeScriptWithNewThread(final String scriptFormat, final Object[] params)
/*     */     throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
/*     */   {
/* 293 */     Thread worker = new AEThread("ScriptObject", true) {
/*     */       public void runSupport() {
/*     */         try {
/* 296 */           int pool = CocoaJavaBridge.this.NSAutoreleasePool_push();
/* 297 */           long start = System.currentTimeMillis();
/*     */           String src;
/*     */           String src;
/* 300 */           if ((params == null) || (params.length == 0)) {
/* 301 */             src = scriptFormat;
/*     */           } else {
/* 303 */             src = MessageFormat.format(scriptFormat, params);
/*     */           }
/*     */           
/* 306 */           Debug.outNoStack("Executing: \n" + src);
/*     */           
/* 308 */           Object errorInfo = CocoaJavaBridge.this.new_NSMutableDictionary();
/* 309 */           if (CocoaJavaBridge.this.NSAppleScript_execute(CocoaJavaBridge.access$300(CocoaJavaBridge.this, src), errorInfo) == null) {
/* 310 */             Debug.out(String.valueOf(CocoaJavaBridge.this.NSMutableDictionary_objectForKey(errorInfo, CocoaJavaBridge.this.NSAppleScript_AppleScriptErrorMessage)));
/*     */           }
/*     */           
/*     */ 
/* 314 */           Debug.outNoStack(MessageFormat.format("Elapsed time: {0}ms\n", new Object[] { new Long(System.currentTimeMillis() - start) }));
/*     */           
/*     */ 
/*     */ 
/* 318 */           CocoaJavaBridge.this.NSAutoreleasePool_pop(pool);
/*     */         } catch (Throwable e) {
/* 320 */           Debug.out(e);
/*     */         }
/*     */         
/*     */       }
/* 324 */     };
/* 325 */     worker.setPriority(4);
/* 326 */     worker.start();
/*     */     
/* 328 */     return this.methNSAppleEventDescriptor_descriptorWithBoolean.invoke(null, new Object[] { Boolean.valueOf(true) });
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
/*     */   protected final Object executeScriptWithAsync(final String scriptFormat, final Object[] params)
/*     */     throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
/*     */   {
/* 349 */     final AERunnable worker = new AERunnable()
/*     */     {
/*     */       public void runSupport() {
/*     */         try {
/* 353 */           int pool = CocoaJavaBridge.this.NSAutoreleasePool_push();
/* 354 */           long start = System.currentTimeMillis();
/*     */           String src;
/*     */           String src;
/* 357 */           if ((params == null) || (params.length == 0)) {
/* 358 */             src = scriptFormat;
/*     */           } else {
/* 360 */             src = MessageFormat.format(scriptFormat, params);
/*     */           }
/*     */           
/* 363 */           Debug.outNoStack("Executing: \n" + src);
/*     */           
/* 365 */           Object errorInfo = CocoaJavaBridge.this.new_NSMutableDictionary();
/* 366 */           if (CocoaJavaBridge.this.NSAppleScript_execute(CocoaJavaBridge.access$300(CocoaJavaBridge.this, src), errorInfo) == null) {
/* 367 */             Debug.out(String.valueOf(CocoaJavaBridge.this.NSMutableDictionary_objectForKey(errorInfo, CocoaJavaBridge.this.NSAppleScript_AppleScriptErrorMessage)));
/*     */           }
/*     */           
/*     */ 
/* 371 */           Debug.outNoStack(MessageFormat.format("Elapsed time: {0}ms\n", new Object[] { new Long(System.currentTimeMillis() - start) }));
/*     */           
/*     */ 
/*     */ 
/* 375 */           CocoaJavaBridge.this.NSAutoreleasePool_pop(pool);
/*     */         } catch (Throwable t) {
/* 377 */           Debug.out(t);
/*     */         }
/*     */         
/*     */       }
/* 381 */     };
/* 382 */     AEThread t = new AEThread("ScriptObject", true)
/*     */     {
/*     */       public void runSupport()
/*     */       {
/* 386 */         CocoaJavaBridge.RunnableDispatcher.access$800(CocoaJavaBridge.this.scriptDispatcher, worker);
/*     */       }
/* 388 */     };
/* 389 */     t.setPriority(4);
/* 390 */     t.start();
/*     */     
/* 392 */     return this.methNSAppleEventDescriptor_descriptorWithBoolean.invoke(null, new Object[] { Boolean.valueOf(true) });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void logWarning(String message)
/*     */   {
/*     */     try
/*     */     {
/* 404 */       this.classMon.enter();
/* 405 */       Logger.log(new LogAlert(false, 1, message));
/*     */     }
/*     */     finally
/*     */     {
/* 409 */       this.classMon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void dispose()
/*     */   {
/*     */     try
/*     */     {
/* 422 */       this.classMon.enter();
/* 423 */       if (!this.isDisposed)
/*     */       {
/* 425 */         Debug.outNoStack("Disposing Native PlatformManager...");
/*     */         try {
/* 427 */           NSAutoreleasePool_pop(this.mainPool);
/*     */         }
/*     */         catch (Throwable e) {}
/* 430 */         this.isDisposed = true;
/* 431 */         Debug.outNoStack("Done");
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 436 */       this.classMon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void finalize()
/*     */     throws Throwable
/*     */   {
/* 445 */     dispose();
/* 446 */     super.finalize();
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
/*     */   private static class RunnableDispatcher
/*     */   {
/*     */     private void exec(Runnable runnable)
/*     */     {
/* 461 */       synchronized (this)
/*     */       {
/* 463 */         runnable.run();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/macosx/access/cocoa/CocoaJavaBridge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */