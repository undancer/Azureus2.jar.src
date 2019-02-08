/*    */ package org.gudy.azureus2.pluginsimpl.local.torrent;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.torrent.TorrentFile;
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
/*    */ public class TorrentFileImpl
/*    */   implements TorrentFile
/*    */ {
/*    */   protected String name;
/*    */   protected long size;
/*    */   
/*    */   protected TorrentFileImpl(String _name, long _size)
/*    */   {
/* 44 */     this.name = _name;
/* 45 */     this.size = _size;
/*    */   }
/*    */   
/*    */   public String getName()
/*    */   {
/* 50 */     return this.name;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getSize()
/*    */   {
/* 56 */     return this.size;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/torrent/TorrentFileImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */