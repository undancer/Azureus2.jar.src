/*    */ package org.gudy.azureus2.core3.torrent.impl;
/*    */ 
/*    */ import java.net.URL;
/*    */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*    */ import org.gudy.azureus2.core3.util.StringInterner;
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
/*    */ public class TOTorrentAnnounceURLSetImpl
/*    */   implements TOTorrentAnnounceURLSet
/*    */ {
/*    */   private final TOTorrentImpl torrent;
/*    */   private URL[] urls;
/*    */   
/*    */   protected TOTorrentAnnounceURLSetImpl(TOTorrentImpl _torrent, URL[] _urls)
/*    */   {
/* 42 */     this.torrent = _torrent;
/*    */     
/* 44 */     setAnnounceURLs(_urls);
/*    */   }
/*    */   
/*    */ 
/*    */   public URL[] getAnnounceURLs()
/*    */   {
/* 50 */     return this.urls;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setAnnounceURLs(URL[] _urls)
/*    */   {
/* 58 */     this.urls = new URL[_urls.length];
/*    */     
/* 60 */     for (int i = 0; i < this.urls.length; i++)
/*    */     {
/* 62 */       this.urls[i] = StringInterner.internURL(this.torrent.anonymityTransform(_urls[i]));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/impl/TOTorrentAnnounceURLSetImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */