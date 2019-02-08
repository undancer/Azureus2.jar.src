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
/*    */ public class TorrentEncodingException
/*    */   extends TorrentException
/*    */ {
/*    */   public String[] valid_charsets;
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
/*    */   public String[] valid_names;
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
/*    */   public TorrentEncodingException(String[] charsets, String[] names)
/*    */   {
/* 41 */     super("Torrent encoding selection required");
/*    */     
/* 43 */     this.valid_charsets = charsets;
/* 44 */     this.valid_names = names;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public TorrentEncodingException(String str, Throwable cause)
/*    */   {
/* 52 */     super(str, cause);
/*    */   }
/*    */   
/*    */ 
/*    */   public String[] getValidCharsets()
/*    */   {
/* 58 */     return this.valid_charsets;
/*    */   }
/*    */   
/*    */ 
/*    */   public String[] getValidTorrentNames()
/*    */   {
/* 64 */     return this.valid_names;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/torrent/TorrentEncodingException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */