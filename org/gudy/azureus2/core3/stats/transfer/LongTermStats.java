/*    */ package org.gudy.azureus2.core3.stats.transfer;
/*    */ 
/*    */ import java.util.Date;
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
/*    */ public abstract interface LongTermStats
/*    */ {
/*    */   public static final int ST_PROTOCOL_UPLOAD = 0;
/*    */   public static final int ST_DATA_UPLOAD = 1;
/*    */   public static final int ST_PROTOCOL_DOWNLOAD = 2;
/*    */   public static final int ST_DATA_DOWNLOAD = 3;
/*    */   public static final int ST_DHT_UPLOAD = 4;
/*    */   public static final int ST_DHT_DOWNLOAD = 5;
/*    */   public static final int PT_CURRENT_HOUR = 0;
/*    */   public static final int PT_CURRENT_DAY = 1;
/*    */   public static final int PT_CURRENT_WEEK = 2;
/*    */   public static final int PT_CURRENT_MONTH = 3;
/*    */   public static final int PT_SLIDING_HOUR = 10;
/*    */   public static final int PT_SLIDING_DAY = 11;
/*    */   public static final int PT_SLIDING_WEEK = 12;
/* 44 */   public static final String[] PT_NAMES = { "hour", "day", "week", "month", "", "", "", "", "", "", "sliding hour", "sliding day", "sliding week" };
/*    */   
/*    */   public abstract boolean isEnabled();
/*    */   
/*    */   public abstract long[] getCurrentRateBytesPerSecond();
/*    */   
/*    */   public abstract long[] getTotalUsageInPeriod(Date paramDate1, Date paramDate2);
/*    */   
/*    */   public abstract long[] getTotalUsageInPeriod(int paramInt, double paramDouble);
/*    */   
/*    */   public abstract long[] getTotalUsageInPeriod(int paramInt, double paramDouble, RecordAccepter paramRecordAccepter);
/*    */   
/*    */   public abstract void addListener(long paramLong, LongTermStatsListener paramLongTermStatsListener);
/*    */   
/*    */   public abstract void removeListener(LongTermStatsListener paramLongTermStatsListener);
/*    */   
/*    */   public abstract void reset();
/*    */   
/*    */   public static abstract interface GenericStatsSource
/*    */   {
/*    */     public abstract int getEntryCount();
/*    */     
/*    */     public abstract long[] getStats(String paramString);
/*    */   }
/*    */   
/*    */   public static abstract interface RecordAccepter
/*    */   {
/*    */     public abstract boolean acceptRecord(long paramLong);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/stats/transfer/LongTermStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */