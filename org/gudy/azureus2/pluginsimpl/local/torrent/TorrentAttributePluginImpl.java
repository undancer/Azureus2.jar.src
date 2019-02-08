/*    */ package org.gudy.azureus2.pluginsimpl.local.torrent;
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
/*    */ public class TorrentAttributePluginImpl
/*    */   extends BaseTorrentAttributeImpl
/*    */ {
/*    */   private String name;
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
/*    */   protected TorrentAttributePluginImpl(String _name)
/*    */   {
/* 32 */     this.name = _name;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 36 */     return this.name;
/*    */   }
/*    */   
/*    */   public String[] getDefinedValues() {
/* 40 */     throw new RuntimeException("not supported");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/torrent/TorrentAttributePluginImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */