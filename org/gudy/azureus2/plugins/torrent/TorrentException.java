/*    */ package org.gudy.azureus2.plugins.torrent;
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
/*    */ public class TorrentException
/*    */   extends Exception
/*    */ {
/*    */   public TorrentException() {}
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
/*    */   public TorrentException(String str)
/*    */   {
/* 43 */     super(str);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public TorrentException(Throwable cause)
/*    */   {
/* 50 */     super(cause);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public TorrentException(String str, Throwable cause)
/*    */   {
/* 58 */     super(str, cause);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/torrent/TorrentException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */