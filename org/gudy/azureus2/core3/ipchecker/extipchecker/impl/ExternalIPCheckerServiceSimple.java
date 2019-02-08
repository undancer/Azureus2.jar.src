/*    */ package org.gudy.azureus2.core3.ipchecker.extipchecker.impl;
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
/*    */ public class ExternalIPCheckerServiceSimple
/*    */   extends ExternalIPCheckerServiceImpl
/*    */ {
/*    */   protected final String url;
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
/*    */   protected ExternalIPCheckerServiceSimple(String _key, String _url)
/*    */   {
/* 40 */     super(_key);
/*    */     
/* 42 */     this.url = _url;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean supportsCheck()
/*    */   {
/* 48 */     return true;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void initiateCheck(long timeout)
/*    */   {
/* 55 */     super.initiateCheck(timeout);
/*    */   }
/*    */   
/*    */ 
/*    */   protected void initiateCheckSupport()
/*    */   {
/* 61 */     reportProgress("loadingwebpage", this.url);
/*    */     
/* 63 */     String page = loadPage(this.url);
/*    */     
/* 65 */     if (page != null)
/*    */     {
/* 67 */       reportProgress("analysingresponse");
/*    */       
/* 69 */       String IP = extractIPAddress(page);
/*    */       
/* 71 */       if (IP != null)
/*    */       {
/* 73 */         reportProgress("addressextracted", IP);
/*    */         
/* 75 */         informSuccess(IP);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipchecker/extipchecker/impl/ExternalIPCheckerServiceSimple.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */