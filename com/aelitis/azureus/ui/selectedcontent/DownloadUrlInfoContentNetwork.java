/*    */ package com.aelitis.azureus.ui.selectedcontent;
/*    */ 
/*    */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
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
/*    */ public class DownloadUrlInfoContentNetwork
/*    */   extends DownloadUrlInfo
/*    */ {
/*    */   private ContentNetwork cn;
/*    */   
/*    */   public DownloadUrlInfoContentNetwork(String url, ContentNetwork cn)
/*    */   {
/* 41 */     super(url);
/* 42 */     this.cn = cn;
/*    */   }
/*    */   
/*    */   public ContentNetwork getContentNetwork() {
/* 46 */     return this.cn;
/*    */   }
/*    */   
/*    */   public void setContentNetwork(ContentNetwork cn)
/*    */   {
/* 51 */     this.cn = cn;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/selectedcontent/DownloadUrlInfoContentNetwork.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */