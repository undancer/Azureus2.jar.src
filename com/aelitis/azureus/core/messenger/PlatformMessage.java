/*     */ package com.aelitis.azureus.core.messenger;
/*     */ 
/*     */ import com.aelitis.azureus.util.JSONUtils;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class PlatformMessage
/*     */ {
/*     */   private final String messageID;
/*     */   private final String listenerID;
/*     */   private final String operationID;
/*     */   private final Map<?, ?> parameters;
/*     */   private final long fireBeforeDate;
/*     */   private final long messageCreatedOn;
/*  46 */   private long lSequenceNo = -1L;
/*     */   
/*  48 */   private boolean sendAZID = true;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean forceProxy;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PlatformMessage(String messageID, String listenerID, String operationID, Map<?, ?> parameters, long maxDelayMS)
/*     */   {
/*  62 */     this.messageID = messageID;
/*  63 */     this.listenerID = listenerID;
/*  64 */     this.operationID = operationID;
/*  65 */     this.parameters = JSONUtils.encodeToJSONObject(parameters);
/*     */     
/*  67 */     this.messageCreatedOn = SystemTime.getCurrentTime();
/*  68 */     this.fireBeforeDate = (this.messageCreatedOn + maxDelayMS);
/*     */   }
/*     */   
/*     */ 
/*     */   public PlatformMessage(String messageID, String listenerID, String operationID, Object[] parameters, long maxDelayMS)
/*     */   {
/*  74 */     this.messageID = messageID;
/*  75 */     this.listenerID = listenerID;
/*  76 */     this.operationID = operationID;
/*     */     
/*  78 */     this.parameters = JSONUtils.encodeToJSONObject(parseParams(parameters));
/*     */     
/*  80 */     this.messageCreatedOn = SystemTime.getCurrentTime();
/*  81 */     this.fireBeforeDate = (this.messageCreatedOn + maxDelayMS);
/*     */   }
/*     */   
/*     */   public static Map<String, Object> parseParams(Object[] parameters) {
/*  85 */     Map<String, Object> result = new HashMap();
/*  86 */     for (int i = 0; i < parameters.length - 1; i += 2) {
/*     */       try {
/*  88 */         if ((parameters[i] instanceof String)) {
/*  89 */           if ((parameters[(i + 1)] instanceof String[])) {
/*  90 */             List<String> list = Arrays.asList((String[])parameters[(i + 1)]);
/*  91 */             result.put((String)parameters[i], list);
/*  92 */           } else if ((parameters[(i + 1)] instanceof Object[])) {
/*  93 */             result.put((String)parameters[i], parseParams((Object[])parameters[(i + 1)]));
/*     */           }
/*  95 */           else if ((parameters[(i + 1)] instanceof Map)) {
/*  96 */             result.put((String)parameters[i], (Map)parameters[(i + 1)]);
/*     */           } else {
/*  98 */             result.put((String)parameters[i], parameters[(i + 1)]);
/*     */           }
/*     */         }
/*     */       } catch (Exception e) {
/* 102 */         Debug.out("making JSONObject out of parsedParams", e);
/*     */       }
/*     */     }
/*     */     
/* 106 */     return result;
/*     */   }
/*     */   
/*     */   public boolean isForceProxy() {
/* 110 */     return this.forceProxy;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setForceProxy(boolean fp)
/*     */   {
/* 117 */     this.forceProxy = fp;
/*     */   }
/*     */   
/*     */   public long getFireBefore() {
/* 121 */     return this.fireBeforeDate;
/*     */   }
/*     */   
/*     */   public long getMessageCreated() {
/* 125 */     return this.messageCreatedOn;
/*     */   }
/*     */   
/*     */   public Map<?, ?> getParameters() {
/* 129 */     return this.parameters;
/*     */   }
/*     */   
/*     */   public String getListenerID() {
/* 133 */     return this.listenerID;
/*     */   }
/*     */   
/*     */   public String getMessageID() {
/* 137 */     return this.messageID;
/*     */   }
/*     */   
/*     */   public String getOperationID() {
/* 141 */     return this.operationID;
/*     */   }
/*     */   
/*     */   protected long getSequenceNo() {
/* 145 */     return this.lSequenceNo;
/*     */   }
/*     */   
/*     */   protected void setSequenceNo(long sequenceNo) {
/* 149 */     this.lSequenceNo = sequenceNo;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 153 */     String paramString = this.parameters.toString();
/* 154 */     return "PlaformMessage {" + this.lSequenceNo + ", " + this.messageID + ", " + this.listenerID + ", " + this.operationID + ", " + (paramString.length() > 32767 ? paramString.substring(0, 32767) : paramString) + "}";
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
/*     */   public String toShortString()
/*     */   {
/* 168 */     return getMessageID() + "." + getListenerID() + "." + getOperationID();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean sendAZID()
/*     */   {
/* 178 */     return this.sendAZID;
/*     */   }
/*     */   
/*     */   public void setSendAZID(boolean send) {
/* 182 */     this.sendAZID = send;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/messenger/PlatformMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */