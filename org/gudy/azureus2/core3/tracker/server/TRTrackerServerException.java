/*     */ package org.gudy.azureus2.core3.tracker.server;
/*     */ 
/*     */ import java.util.Map;
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
/*     */ public class TRTrackerServerException
/*     */   extends Exception
/*     */ {
/*  31 */   private int response_code = -1;
/*     */   
/*     */   private String response_text;
/*     */   
/*     */   private Map response_headers;
/*     */   
/*     */   private boolean user_message;
/*     */   
/*     */   private Map error_map;
/*     */   
/*     */ 
/*     */   public TRTrackerServerException(int _response_code, String _response_text, Map _response_headers)
/*     */   {
/*  44 */     this.response_code = _response_code;
/*  45 */     this.response_text = _response_text;
/*  46 */     this.response_headers = _response_headers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TRTrackerServerException(String str)
/*     */   {
/*  53 */     super(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public TRTrackerServerException(String str, Throwable e)
/*     */   {
/*  61 */     super(str, e);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getResponseCode()
/*     */   {
/*  67 */     return this.response_code;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getResponseText()
/*     */   {
/*  73 */     return this.response_text;
/*     */   }
/*     */   
/*     */ 
/*     */   public Map getResponseHeaders()
/*     */   {
/*  79 */     return this.response_headers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setUserMessage(boolean b)
/*     */   {
/*  87 */     this.user_message = b;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isUserMessage()
/*     */   {
/*  93 */     return this.user_message;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setErrorEntries(Map map)
/*     */   {
/* 100 */     this.error_map = map;
/*     */   }
/*     */   
/*     */ 
/*     */   public Map getErrorEntries()
/*     */   {
/* 106 */     return this.error_map;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/TRTrackerServerException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */