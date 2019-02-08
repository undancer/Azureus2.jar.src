/*    */ package org.gudy.azureus2.core3.ipfilter.impl;
/*    */ 
/*    */ import org.gudy.azureus2.core3.ipfilter.BannedIp;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BannedIpImpl
/*    */   implements BannedIp
/*    */ {
/*    */   protected long time;
/*    */   protected final String torrent_name;
/*    */   protected final String ip;
/*    */   
/*    */   protected BannedIpImpl(String _ip, String _torrent_name, boolean _temporary)
/*    */   {
/* 43 */     this.ip = _ip;
/* 44 */     this.torrent_name = _torrent_name;
/*    */     
/* 46 */     this.time = SystemTime.getCurrentTime();
/*    */     
/* 48 */     if (_temporary)
/*    */     {
/* 50 */       this.time = (-this.time);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected BannedIpImpl(String _ip, String _torrent_name, long _time)
/*    */   {
/* 60 */     this.ip = _ip;
/* 61 */     this.torrent_name = _torrent_name;
/* 62 */     this.time = _time;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getIp()
/*    */   {
/* 68 */     return this.ip;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isTemporary()
/*    */   {
/* 74 */     return this.time < 0L;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getBanningTime()
/*    */   {
/* 80 */     return Math.abs(this.time);
/*    */   }
/*    */   
/*    */ 
/*    */   public String getTorrentName()
/*    */   {
/* 86 */     return this.torrent_name;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/impl/BannedIpImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */