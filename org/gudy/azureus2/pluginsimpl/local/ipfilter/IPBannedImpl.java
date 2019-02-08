/*    */ package org.gudy.azureus2.pluginsimpl.local.ipfilter;
/*    */ 
/*    */ import org.gudy.azureus2.core3.ipfilter.BannedIp;
/*    */ import org.gudy.azureus2.plugins.ipfilter.IPBanned;
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
/*    */ public class IPBannedImpl
/*    */   implements IPBanned
/*    */ {
/*    */   protected BannedIp banned;
/*    */   
/*    */   protected IPBannedImpl(BannedIp _blocked)
/*    */   {
/* 44 */     this.banned = _blocked;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getBannedIP()
/*    */   {
/* 50 */     return this.banned.getIp();
/*    */   }
/*    */   
/*    */ 
/*    */   public String getBannedTorrentName()
/*    */   {
/* 56 */     return this.banned.getTorrentName();
/*    */   }
/*    */   
/*    */ 
/*    */   public long getBannedTime()
/*    */   {
/* 62 */     return this.banned.getBanningTime();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ipfilter/IPBannedImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */