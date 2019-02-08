/*     */ package com.aelitis.azureus.ui.swt.browser.msg;
/*     */ 
/*     */ import com.aelitis.azureus.core.messenger.ClientMessageContext;
/*     */ import com.aelitis.azureus.core.messenger.browser.BrowserMessage;
/*     */ import com.aelitis.azureus.core.messenger.browser.BrowserMessageDispatcher;
/*     */ import com.aelitis.azureus.core.messenger.browser.listeners.BrowserMessageListener;
/*     */ import com.aelitis.azureus.util.JSONUtils;
/*     */ import com.aelitis.azureus.util.UrlFilter;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.BrowserWrapper;
/*     */ import org.gudy.azureus2.ui.swt.BrowserWrapper.BrowserFunction;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public class MessageDispatcherSWT
/*     */   implements BrowserMessageDispatcher
/*     */ {
/*     */   private ClientMessageContext context;
/*  45 */   private Map<String, BrowserMessageListener> listeners = new HashMap();
/*     */   
/*     */ 
/*     */   private BrowserWrapper browser;
/*     */   
/*     */   private BrowserWrapper.BrowserFunction browserFunction;
/*     */   
/*     */ 
/*     */   public MessageDispatcherSWT(ClientMessageContext context)
/*     */   {
/*  55 */     this.context = context;
/*     */   }
/*     */   
/*     */   public void registerBrowser(final BrowserWrapper browser) {
/*  59 */     this.browser = browser;
/*     */     try
/*     */     {
/*  62 */       this.browserFunction = browser.addBrowserFunction("sendMessageToAZ", new BrowserWrapper.BrowserFunction()
/*     */       {
/*     */ 
/*     */         public Object function(Object[] args)
/*     */         {
/*  67 */           if (args == null) {
/*  68 */             MessageDispatcherSWT.this.context.debug("sendMessageToAZ: arguments null on " + browser.getUrl());
/*  69 */             return null;
/*     */           }
/*  71 */           if ((args.length != 3) && (args.length != 2)) {
/*  72 */             MessageDispatcherSWT.this.context.debug("sendMessageToAZ: # arguments not 2 or 3 (" + args.length + ") on " + browser.getUrl());
/*  73 */             return null;
/*     */           }
/*     */           
/*  76 */           if (!(args[0] instanceof String)) {
/*  77 */             MessageDispatcherSWT.this.context.debug("sendMessageToAZ: Param 1 not String");
/*  78 */             return null;
/*     */           }
/*  80 */           if (!(args[1] instanceof String)) {
/*  81 */             MessageDispatcherSWT.this.context.debug("sendMessageToAZ: Param 2 not String");
/*  82 */             return null;
/*     */           }
/*  84 */           Map<?, ?> params = Collections.EMPTY_MAP;
/*  85 */           if (args.length == 3) {
/*  86 */             if (!(args[2] instanceof String)) {
/*  87 */               MessageDispatcherSWT.this.context.debug("sendMessageToAZ: Param 3 not String");
/*  88 */               return null;
/*     */             }
/*     */             
/*  91 */             params = JSONUtils.decodeJSON((String)args[2]);
/*     */           }
/*     */           
/*     */ 
/*  95 */           BrowserMessage message = new BrowserMessage((String)args[0], (String)args[1], params);
/*  96 */           message.setReferer(browser.getUrl());
/*  97 */           MessageDispatcherSWT.this.dispatch(message);
/*  98 */           return null;
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Throwable t) {
/* 103 */       Debug.out(t);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void deregisterBrowser(BrowserWrapper browser)
/*     */   {
/* 115 */     if ((this.browserFunction != null) && (!this.browserFunction.isDisposed())) {
/* 116 */       this.browserFunction.dispose();
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
/*     */   public synchronized void addListener(BrowserMessageListener listener)
/*     */   {
/* 130 */     String id = listener.getId();
/* 131 */     BrowserMessageListener registered = (BrowserMessageListener)this.listeners.get(id);
/* 132 */     if (registered != null) {
/* 133 */       if (registered != listener) {
/* 134 */         throw new IllegalStateException("Listener " + registered.getClass().getName() + " already registered for ID " + id);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 139 */       listener.setContext(this.context);
/* 140 */       this.listeners.put(id, listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void removeListener(BrowserMessageListener listener)
/*     */   {
/* 150 */     removeListener(listener.getId());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void removeListener(String id)
/*     */   {
/*     */     BrowserMessageListener removed;
/*     */     
/*     */ 
/* 161 */     synchronized (this) {
/* 162 */       removed = (BrowserMessageListener)this.listeners.remove(id);
/*     */     }
/*     */     
/* 165 */     if (removed != null)
/*     */     {
/*     */ 
/* 168 */       removed.setContext(null);
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public BrowserMessageListener getListener(String id)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_2
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 173	com/aelitis/azureus/ui/swt/browser/msg/MessageDispatcherSWT:listeners	Ljava/util/Map;
/*     */     //   8: aload_1
/*     */     //   9: invokeinterface 205 2 0
/*     */     //   14: checkcast 92	com/aelitis/azureus/core/messenger/browser/listeners/BrowserMessageListener
/*     */     //   17: aload_2
/*     */     //   18: monitorexit
/*     */     //   19: areturn
/*     */     //   20: astore_3
/*     */     //   21: aload_2
/*     */     //   22: monitorexit
/*     */     //   23: aload_3
/*     */     //   24: athrow
/*     */     // Line number table:
/*     */     //   Java source line #174	-> byte code offset #0
/*     */     //   Java source line #175	-> byte code offset #4
/*     */     //   Java source line #176	-> byte code offset #20
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	25	0	this	MessageDispatcherSWT
/*     */     //   0	25	1	id	String
/*     */     //   2	20	2	Ljava/lang/Object;	Object
/*     */     //   20	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	19	20	finally
/*     */     //   20	23	20	finally
/*     */   }
/*     */   
/*     */   public void dispatch(final BrowserMessage message)
/*     */   {
/* 181 */     if (message == null) {
/* 182 */       return;
/*     */     }
/* 184 */     String referer = message.getReferer();
/* 185 */     if ((referer != null) && (!UrlFilter.getInstance().urlCanRPC(referer))) {
/* 186 */       this.context.debug("blocked " + message + "\n  " + referer);
/* 187 */       return;
/*     */     }
/*     */     
/*     */ 
/* 191 */     this.context.debug("Received " + message);
/* 192 */     if ((this.browser != null) && (!this.browser.isDisposed()) && (Utils.isThisThreadSWT())) {
/* 193 */       this.context.debug("   browser url: " + this.browser.getUrl());
/*     */     }
/*     */     
/*     */ 
/* 197 */     String listenerId = message.getListenerId();
/* 198 */     if ("lightbox-browser".equals(listenerId)) {
/* 199 */       listenerId = "display";
/*     */     }
/*     */     
/* 202 */     final BrowserMessageListener listener = getListener(listenerId);
/* 203 */     if (listener == null) {
/* 204 */       this.context.debug("No listener registered with ID " + listenerId);
/*     */     } else {
/* 206 */       new AEThread2("dispatch for " + listenerId, true) {
/*     */         public void run() {
/* 208 */           listener.handleMessage(message);
/* 209 */           message.complete(true, true, null);
/*     */         }
/*     */       }.start();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/browser/msg/MessageDispatcherSWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */