/*    */ package com.aelitis.azureus.ui.swt.browser.listener;
/*    */ 
/*    */ import com.aelitis.azureus.core.messenger.ClientMessageContext;
/*    */ import com.aelitis.azureus.ui.selectedcontent.DownloadUrlInfo;
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
/*    */ 
/*    */ 
/*    */ public class DownloadUrlInfoSWT
/*    */   extends DownloadUrlInfo
/*    */ {
/*    */   private final ClientMessageContext context;
/*    */   private final String callback;
/*    */   private final String hash;
/*    */   
/*    */   public DownloadUrlInfoSWT(ClientMessageContext context, String callback, String hash)
/*    */   {
/* 47 */     super(null);
/* 48 */     this.context = context;
/* 49 */     this.callback = callback;
/* 50 */     this.hash = hash;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ClientMessageContext getContext()
/*    */   {
/* 57 */     return this.context;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String getCallback()
/*    */   {
/* 64 */     return this.callback;
/*    */   }
/*    */   
/*    */   public void invoke(String reason) {
/* 68 */     this.context.executeInBrowser(this.callback + "('" + reason + "','" + this.hash + "','" + getDownloadURL() + "')");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/browser/listener/DownloadUrlInfoSWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */