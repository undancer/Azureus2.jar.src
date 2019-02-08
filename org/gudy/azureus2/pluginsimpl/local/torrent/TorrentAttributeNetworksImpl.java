/*    */ package org.gudy.azureus2.pluginsimpl.local.torrent;
/*    */ 
/*    */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
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
/*    */ public class TorrentAttributeNetworksImpl
/*    */   extends BaseTorrentAttributeImpl
/*    */ {
/*    */   public String getName()
/*    */   {
/* 34 */     return "Networks";
/*    */   }
/*    */   
/*    */   public String[] getDefinedValues() {
/* 38 */     return AENetworkClassifier.AT_NETWORKS;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/torrent/TorrentAttributeNetworksImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */