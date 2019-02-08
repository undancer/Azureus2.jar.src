/*    */ package org.gudy.azureus2.pluginsimpl.local.torrent;
/*    */ 
/*    */ import org.gudy.azureus2.core3.peer.PEPeerSource;
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
/*    */ public class TorrentAttributePeerSourcesImpl
/*    */   extends BaseTorrentAttributeImpl
/*    */ {
/*    */   public String getName()
/*    */   {
/* 34 */     return "PeerSources";
/*    */   }
/*    */   
/*    */   public String[] getDefinedValues() {
/* 38 */     return PEPeerSource.PS_SOURCES;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/torrent/TorrentAttributePeerSourcesImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */