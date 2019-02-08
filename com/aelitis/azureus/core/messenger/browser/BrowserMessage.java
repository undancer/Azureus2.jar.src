/*     */ package com.aelitis.azureus.core.messenger.browser;
/*     */ 
/*     */ import com.aelitis.azureus.core.messenger.browser.listeners.MessageCompletionListener;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class BrowserMessage
/*     */ {
/*     */   public static final String MESSAGE_PREFIX = "AZMSG";
/*     */   public static final String MESSAGE_DELIM = ";";
/*     */   public static String MESSAGE_DELIM_ENCODED;
/*     */   public static final int NO_PARAM = 0;
/*     */   public static final int OBJECT_PARAM = 1;
/*     */   public static final int ARRAY_PARAM = 2;
/*     */   private String listenerId;
/*     */   private String operationId;
/*     */   private Map decodedParams;
/*     */   
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  58 */       MESSAGE_DELIM_ENCODED = URLEncoder.encode(";", "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  60 */       MESSAGE_DELIM_ENCODED = ";";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  70 */   private ArrayList completionListeners = new ArrayList();
/*     */   
/*     */   private boolean completed;
/*     */   
/*     */   private boolean completeDelayed;
/*     */   
/*     */   private String referer;
/*     */   
/*     */   public BrowserMessage(String listenerId, String operationId, Map<?, ?> params)
/*     */   {
/*  80 */     this.listenerId = listenerId;
/*  81 */     this.operationId = operationId;
/*  82 */     this.decodedParams = params;
/*     */   }
/*     */   
/*     */   public void addCompletionListener(MessageCompletionListener l) {
/*  86 */     this.completionListeners.add(l);
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
/*     */   public void complete(boolean bOnlyNonDelayed, boolean success, Object data)
/*     */   {
/*  99 */     if ((this.completed) || ((bOnlyNonDelayed) && (this.completeDelayed)))
/*     */     {
/* 101 */       return;
/*     */     }
/* 103 */     triggerCompletionListeners(success, data);
/* 104 */     this.completed = true;
/*     */   }
/*     */   
/*     */   public void debug(String message) {
/* 108 */     debug(message, null);
/*     */   }
/*     */   
/*     */   public void debug(String message, Throwable t) {
/*     */     try {
/* 113 */       AEDiagnosticsLogger diag_logger = AEDiagnostics.getLogger("v3.CMsgr");
/* 114 */       String out = "[" + getListenerId() + ":" + getOperationId() + "] " + message;
/*     */       
/* 116 */       diag_logger.log(out);
/* 117 */       if (t != null) {
/* 118 */         diag_logger.log(t);
/*     */       }
/* 120 */       if (ConstantsVuze.DIAG_TO_STDOUT) {
/* 121 */         System.out.println(out);
/* 122 */         if (t != null) {
/* 123 */           t.printStackTrace();
/*     */         }
/*     */       }
/*     */     } catch (Throwable t2) {
/* 127 */       Debug.out(t2);
/*     */     }
/*     */   }
/*     */   
/*     */   public Map getDecodedMap() {
/* 132 */     return this.decodedParams == null ? Collections.EMPTY_MAP : this.decodedParams;
/*     */   }
/*     */   
/*     */   public String getListenerId() {
/* 136 */     return this.listenerId;
/*     */   }
/*     */   
/*     */   public String getOperationId() {
/* 140 */     return this.operationId;
/*     */   }
/*     */   
/*     */   public String getReferer() {
/* 144 */     return this.referer;
/*     */   }
/*     */   
/*     */   public void removeCompletionListener(MessageCompletionListener l) {
/* 148 */     this.completionListeners.remove(l);
/*     */   }
/*     */   
/*     */   public void setCompleteDelayed(boolean bCompleteDelayed) {
/* 152 */     this.completeDelayed = bCompleteDelayed;
/*     */   }
/*     */   
/*     */   public void setReferer(String referer) {
/* 156 */     this.referer = referer;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 160 */     return this.listenerId + "." + this.operationId + "(" + this.decodedParams + ")";
/*     */   }
/*     */   
/*     */   private void triggerCompletionListeners(boolean success, Object data) {
/* 164 */     for (Iterator iterator = this.completionListeners.iterator(); iterator.hasNext();) {
/* 165 */       MessageCompletionListener l = (MessageCompletionListener)iterator.next();
/*     */       try {
/* 167 */         l.completed(success, data);
/*     */       } catch (Throwable e) {
/* 169 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/messenger/browser/BrowserMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */