/*    */ package org.gudy.azureus2.ui.common.util;
/*    */ 
/*    */ import org.apache.log4j.Level;
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
/*    */ public class SLevel
/*    */   extends Level
/*    */ {
/*    */   public static final int INT_CORE_WARNING = 11100;
/*    */   public static final int INT_CORE_INFO = 11000;
/*    */   public static final int INT_THREAD = 10001;
/*    */   public static final int INT_HTTP = 12000;
/*    */   public static final int INT_ACCESS_VIOLATION = 35000;
/* 26 */   public static final Level CORE_WARNING = new SLevel(11100, "CORE WARNING", 6);
/* 27 */   public static final Level CORE_INFO = new SLevel(11000, "CORE INFO", 6);
/* 28 */   public static final Level THREAD = new SLevel(10001, "THREAD", 6);
/* 29 */   public static final Level HTTP = new SLevel(12000, "HTTP", 6);
/* 30 */   public static final Level ACCESS_VIOLATION = new SLevel(35000, "ACCESS VIOLATION", 6);
/*    */   
/* 32 */   SLevel(int c, String a, int b) { super(c, a, b); }
/*    */   
/*    */   public static Level toLevel(int val) {
/* 35 */     return toLevel(val, Level.DEBUG);
/*    */   }
/*    */   
/*    */   public static Level toLevel(int val, Level defaultLevel) {
/* 39 */     switch (val) {
/* 40 */     case 11100:  return CORE_WARNING;
/* 41 */     case 11000:  return CORE_INFO;
/* 42 */     case 10001:  return THREAD;
/* 43 */     case 12000:  return HTTP;
/* 44 */     case 35000:  return ACCESS_VIOLATION; }
/* 45 */     return Level.toLevel(val, defaultLevel);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/common/util/SLevel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */