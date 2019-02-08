/*    */ package org.gudy.azureus2.core3.ipfilter.impl;
/*    */ 
/*    */ import org.gudy.azureus2.core3.ipfilter.BadIp;
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
/*    */ public class BadIpImpl
/*    */   implements BadIp
/*    */ {
/*    */   protected final String ip;
/*    */   protected int warning_count;
/*    */   protected long last_time;
/*    */   
/*    */   protected BadIpImpl(String _ip)
/*    */   {
/* 42 */     this.ip = _ip;
/*    */   }
/*    */   
/*    */ 
/*    */   protected int incrementWarnings()
/*    */   {
/* 48 */     this.last_time = SystemTime.getCurrentTime();
/*    */     
/* 50 */     return ++this.warning_count;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getIp()
/*    */   {
/* 56 */     return this.ip;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getNumberOfWarnings()
/*    */   {
/* 62 */     return this.warning_count;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getLastTime()
/*    */   {
/* 68 */     return this.last_time;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/impl/BadIpImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */