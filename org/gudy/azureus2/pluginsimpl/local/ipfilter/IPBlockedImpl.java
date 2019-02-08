/*    */ package org.gudy.azureus2.pluginsimpl.local.ipfilter;
/*    */ 
/*    */ import org.gudy.azureus2.core3.ipfilter.BlockedIp;
/*    */ import org.gudy.azureus2.plugins.ipfilter.IPBlocked;
/*    */ import org.gudy.azureus2.plugins.ipfilter.IPFilter;
/*    */ import org.gudy.azureus2.plugins.ipfilter.IPRange;
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
/*    */ public class IPBlockedImpl
/*    */   implements IPBlocked
/*    */ {
/*    */   protected IPFilter filter;
/*    */   protected BlockedIp blocked;
/*    */   
/*    */   protected IPBlockedImpl(IPFilter _filter, BlockedIp _blocked)
/*    */   {
/* 46 */     this.filter = _filter;
/* 47 */     this.blocked = _blocked;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getBlockedIP()
/*    */   {
/* 53 */     return this.blocked.getBlockedIp();
/*    */   }
/*    */   
/*    */ 
/*    */   public String getBlockedTorrentName()
/*    */   {
/* 59 */     return this.blocked.getTorrentName();
/*    */   }
/*    */   
/*    */ 
/*    */   public long getBlockedTime()
/*    */   {
/* 65 */     return this.blocked.getBlockedTime();
/*    */   }
/*    */   
/*    */ 
/*    */   public IPRange getBlockingRange()
/*    */   {
/* 71 */     return new IPRangeImpl(this.filter, this.blocked.getBlockingRange());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ipfilter/IPBlockedImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */