/*    */ package org.gudy.azureus2.core3.torrent.impl;
/*    */ 
/*    */ import java.net.URL;
/*    */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*    */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
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
/*    */ public class TOTorrentAnnounceURLGroupImpl
/*    */   implements TOTorrentAnnounceURLGroup
/*    */ {
/*    */   private final TOTorrentImpl torrent;
/*    */   private TOTorrentAnnounceURLSet[] sets;
/*    */   
/*    */   protected TOTorrentAnnounceURLGroupImpl(TOTorrentImpl _torrent)
/*    */   {
/* 40 */     this.torrent = _torrent;
/*    */     
/* 42 */     this.sets = new TOTorrentAnnounceURLSet[0];
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected void addSet(TOTorrentAnnounceURLSet set)
/*    */   {
/* 49 */     TOTorrentAnnounceURLSet[] new_sets = new TOTorrentAnnounceURLSet[this.sets.length + 1];
/*    */     
/* 51 */     System.arraycopy(this.sets, 0, new_sets, 0, this.sets.length);
/*    */     
/* 53 */     new_sets[(new_sets.length - 1)] = set;
/*    */     
/* 55 */     this.sets = new_sets;
/*    */     
/* 57 */     this.torrent.fireChanged(1);
/*    */   }
/*    */   
/*    */ 
/*    */   public TOTorrentAnnounceURLSet[] getAnnounceURLSets()
/*    */   {
/* 63 */     return this.sets;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setAnnounceURLSets(TOTorrentAnnounceURLSet[] _sets)
/*    */   {
/* 70 */     this.sets = _sets;
/*    */     
/* 72 */     this.torrent.fireChanged(1);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public TOTorrentAnnounceURLSet createAnnounceURLSet(URL[] urls)
/*    */   {
/* 80 */     return new TOTorrentAnnounceURLSetImpl(this.torrent, urls);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/impl/TOTorrentAnnounceURLGroupImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */