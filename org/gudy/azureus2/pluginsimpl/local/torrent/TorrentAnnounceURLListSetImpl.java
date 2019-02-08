/*    */ package org.gudy.azureus2.pluginsimpl.local.torrent;
/*    */ 
/*    */ import java.net.URL;
/*    */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*    */ import org.gudy.azureus2.plugins.torrent.TorrentAnnounceURLListSet;
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
/*    */ public class TorrentAnnounceURLListSetImpl
/*    */   implements TorrentAnnounceURLListSet
/*    */ {
/*    */   protected TorrentAnnounceURLListImpl list;
/*    */   protected TOTorrentAnnounceURLSet set;
/*    */   
/*    */   protected TorrentAnnounceURLListSetImpl(TorrentAnnounceURLListImpl _list, TOTorrentAnnounceURLSet _set)
/*    */   {
/* 47 */     this.list = _list;
/* 48 */     this.set = _set;
/*    */   }
/*    */   
/*    */ 
/*    */   protected TOTorrentAnnounceURLSet getSet()
/*    */   {
/* 54 */     return this.set;
/*    */   }
/*    */   
/*    */ 
/*    */   public URL[] getURLs()
/*    */   {
/* 60 */     return this.set.getAnnounceURLs();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setURLs(URL[] urls)
/*    */   {
/* 67 */     this.set.setAnnounceURLs(urls);
/*    */     
/* 69 */     this.list.updated();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/torrent/TorrentAnnounceURLListSetImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */