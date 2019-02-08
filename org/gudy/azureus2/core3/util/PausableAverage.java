/*    */ package org.gudy.azureus2.core3.util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PausableAverage
/*    */   extends Average
/*    */ {
/*    */   private long offset;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private long pause_time;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static PausableAverage getPausableInstance(int refreshRate, int period)
/*    */   {
/* 31 */     if (refreshRate < 100)
/*    */     {
/* 33 */       return null;
/*    */     }
/*    */     
/* 36 */     if (period * 1000 < refreshRate)
/*    */     {
/* 38 */       return null;
/*    */     }
/*    */     
/* 41 */     return new PausableAverage(refreshRate, period);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private PausableAverage(int _refreshRate, int _period)
/*    */   {
/* 52 */     super(_refreshRate, _period);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void addValue(long value)
/*    */   {
/* 59 */     super.addValue(value);
/*    */   }
/*    */   
/*    */ 
/*    */   public long getAverage()
/*    */   {
/* 65 */     long average = super.getAverage();
/*    */     
/* 67 */     return average;
/*    */   }
/*    */   
/*    */ 
/*    */   protected long getEffectiveTime()
/*    */   {
/* 73 */     return SystemTime.getCurrentTime() - this.offset;
/*    */   }
/*    */   
/*    */ 
/*    */   public void pause()
/*    */   {
/* 79 */     if (this.pause_time == 0L)
/*    */     {
/* 81 */       this.pause_time = SystemTime.getCurrentTime();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public void resume()
/*    */   {
/* 88 */     if (this.pause_time != 0L)
/*    */     {
/* 90 */       long now = SystemTime.getCurrentTime();
/*    */       
/* 92 */       if (now > this.pause_time)
/*    */       {
/* 94 */         this.offset += now - this.pause_time;
/*    */       }
/*    */       
/* 97 */       this.pause_time = 0L;
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/PausableAverage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */