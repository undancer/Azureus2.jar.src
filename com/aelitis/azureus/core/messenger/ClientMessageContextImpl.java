/*    */ package com.aelitis.azureus.core.messenger;
/*    */ 
/*    */ import com.aelitis.azureus.core.messenger.browser.BrowserMessageDispatcher;
/*    */ import com.aelitis.azureus.core.messenger.browser.listeners.BrowserMessageListener;
/*    */ import com.aelitis.azureus.util.ConstantsVuze;
/*    */ import java.io.PrintStream;
/*    */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*    */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
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
/*    */ 
/*    */ public abstract class ClientMessageContextImpl
/*    */   implements ClientMessageContext
/*    */ {
/*    */   private String id;
/*    */   private BrowserMessageDispatcher dispatcher;
/*    */   
/*    */   public ClientMessageContextImpl(String id, BrowserMessageDispatcher dispatcher)
/*    */   {
/* 41 */     this.id = id;
/* 42 */     this.dispatcher = dispatcher;
/*    */   }
/*    */   
/*    */   public void addMessageListener(BrowserMessageListener listener) {
/* 46 */     if (this.dispatcher != null) {
/* 47 */       this.dispatcher.addListener(listener);
/*    */     } else {
/* 49 */       debug("No dispatcher when trying to add MessageListener " + listener.getId() + ";" + Debug.getCompressedStackTrace());
/*    */     }
/*    */   }
/*    */   
/*    */   public void debug(String message)
/*    */   {
/* 55 */     AEDiagnosticsLogger diag_logger = AEDiagnostics.getLogger("v3.CMsgr");
/* 56 */     diag_logger.log("[" + this.id + "] " + message);
/* 57 */     if (ConstantsVuze.DIAG_TO_STDOUT) {
/* 58 */       System.out.println("[" + this.id + "] " + message);
/*    */     }
/*    */   }
/*    */   
/*    */   public void debug(String message, Throwable t) {
/* 63 */     AEDiagnosticsLogger diag_logger = AEDiagnostics.getLogger("v3.CMsgr");
/* 64 */     diag_logger.log("[" + this.id + "] " + message);
/* 65 */     diag_logger.log(t);
/* 66 */     if (ConstantsVuze.DIAG_TO_STDOUT) {
/* 67 */       System.err.println("[" + this.id + "] " + message);
/* 68 */       t.printStackTrace();
/*    */     }
/*    */   }
/*    */   
/*    */   public void removeMessageListener(String listenerId) {
/* 73 */     if (this.dispatcher != null) {
/* 74 */       this.dispatcher.removeListener(listenerId);
/*    */     } else {
/* 76 */       debug("No dispatcher when trying to remove MessageListener " + listenerId + ";" + Debug.getCompressedStackTrace());
/*    */     }
/*    */   }
/*    */   
/*    */   public void removeMessageListener(BrowserMessageListener listener)
/*    */   {
/* 82 */     if (this.dispatcher != null) {
/* 83 */       this.dispatcher.removeListener(listener);
/*    */     } else {
/* 85 */       debug("No dispatcher when trying to remove MessageListener " + listener.getId() + ";" + Debug.getCompressedStackTrace());
/*    */     }
/*    */   }
/*    */   
/*    */   public BrowserMessageDispatcher getDispatcher()
/*    */   {
/* 91 */     return this.dispatcher;
/*    */   }
/*    */   
/*    */   public String getID() {
/* 95 */     return this.id;
/*    */   }
/*    */   
/*    */   public void setMessageDispatcher(BrowserMessageDispatcher dispatcher) {
/* 99 */     this.dispatcher = dispatcher;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/messenger/ClientMessageContextImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */