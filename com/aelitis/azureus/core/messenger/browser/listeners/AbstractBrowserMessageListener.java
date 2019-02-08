/*    */ package com.aelitis.azureus.core.messenger.browser.listeners;
/*    */ 
/*    */ import com.aelitis.azureus.core.messenger.ClientMessageContext;
/*    */ import com.aelitis.azureus.core.messenger.browser.BrowserMessage;
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
/*    */ public abstract class AbstractBrowserMessageListener
/*    */   implements BrowserMessageListener
/*    */ {
/* 34 */   protected ClientMessageContext context = null;
/*    */   
/*    */ 
/*    */ 
/*    */   private String id;
/*    */   
/*    */ 
/*    */ 
/*    */   public AbstractBrowserMessageListener(String id)
/*    */   {
/* 44 */     this.id = id;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected void debug(String message)
/*    */   {
/* 53 */     this.context.debug("[" + this.id + "] " + message);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void debug(String message, Throwable t)
/*    */   {
/* 63 */     this.context.debug("[" + this.id + "] " + message, t);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ClientMessageContext getContext()
/*    */   {
/* 72 */     return this.context;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getId()
/*    */   {
/* 81 */     return this.id;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public abstract void handleMessage(BrowserMessage paramBrowserMessage);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setContext(ClientMessageContext context)
/*    */   {
/* 98 */     if (this.context == null) {
/* 99 */       this.context = context;
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/messenger/browser/listeners/AbstractBrowserMessageListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */