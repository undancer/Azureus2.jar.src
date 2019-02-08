/*    */ package org.gudy.azureus2.core3.ipfilter.impl;
/*    */ 
/*    */ import org.gudy.azureus2.core3.ipfilter.BlockedIp;
/*    */ import org.gudy.azureus2.core3.ipfilter.IpRange;
/*    */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*    */ public class BlockedIpImpl
/*    */   implements BlockedIp
/*    */ {
/*    */   private final String ip;
/*    */   private final long time;
/*    */   private final IpRange range;
/*    */   private final String torrentname;
/*    */   private final boolean loggable;
/*    */   
/*    */   public BlockedIpImpl(String ip, IpRange range, String torrent_name, boolean _loggable)
/*    */   {
/* 41 */     this.ip = ip;
/* 42 */     this.range = range;
/* 43 */     this.time = SystemTime.getCurrentTime();
/* 44 */     this.torrentname = torrent_name;
/* 45 */     this.loggable = _loggable;
/*    */   }
/*    */   
/*    */   public String getBlockedIp() {
/* 49 */     return this.ip;
/*    */   }
/*    */   
/*    */   public IpRange getBlockingRange() {
/* 53 */     return this.range;
/*    */   }
/*    */   
/*    */   public long getBlockedTime() {
/* 57 */     return this.time;
/*    */   }
/*    */   
/*    */   public String getTorrentName() {
/* 61 */     return this.torrentname;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isLoggable()
/*    */   {
/* 67 */     return this.loggable;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/impl/BlockedIpImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */