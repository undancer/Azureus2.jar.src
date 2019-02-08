/*    */ package org.gudy.azureus2.core3.util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RealTimeInfo
/*    */ {
/*    */   private static volatile int realtime_task_count;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private static volatile long progressive_bytes_per_sec;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static synchronized void addRealTimeTask()
/*    */   {
/* 33 */     realtime_task_count += 1;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static synchronized void removeRealTimeTask()
/*    */   {
/* 41 */     realtime_task_count -= 1;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static boolean isRealTimeTaskActive()
/*    */   {
/* 49 */     return realtime_task_count > 0;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static synchronized void setProgressiveActive(long bytes_per_sec)
/*    */   {
/* 56 */     progressive_bytes_per_sec = bytes_per_sec;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static synchronized void setProgressiveInactive()
/*    */   {
/* 64 */     progressive_bytes_per_sec = 0L;
/*    */   }
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
/*    */   public static long getProgressiveActiveBytesPerSec()
/*    */   {
/* 78 */     return progressive_bytes_per_sec;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/RealTimeInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */