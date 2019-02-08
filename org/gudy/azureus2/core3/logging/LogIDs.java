/*    */ package org.gudy.azureus2.core3.logging;
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
/*    */ public class LogIDs
/*    */   implements Comparable
/*    */ {
/*    */   private final String name;
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
/* 30 */   private static int nextOrdinal = 0;
/*    */   
/*    */ 
/* 33 */   private final int ordinal = nextOrdinal++;
/*    */   
/*    */   private LogIDs(String name) {
/* 36 */     this.name = name;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 40 */     return this.name;
/*    */   }
/*    */   
/*    */   public int compareTo(Object o) {
/* 44 */     return this.ordinal - ((LogIDs)o).ordinal;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/* 49 */   public static final LogIDs LOGGER = new LogIDs("logger");
/*    */   
/* 51 */   public static final LogIDs NWMAN = new LogIDs("nwman");
/*    */   
/* 53 */   public static final LogIDs NET = new LogIDs("net");
/*    */   
/* 55 */   public static final LogIDs PEER = new LogIDs("peer");
/*    */   
/* 57 */   public static final LogIDs CORE = new LogIDs("core");
/*    */   
/* 59 */   public static final LogIDs DISK = new LogIDs("disk");
/*    */   
/* 61 */   public static final LogIDs PLUGIN = new LogIDs("plug");
/*    */   
/* 63 */   public static final LogIDs TRACKER = new LogIDs("tracker");
/*    */   
/* 65 */   public static final LogIDs GUI = new LogIDs("GUI");
/*    */   
/* 67 */   public static final LogIDs STDOUT = new LogIDs("stdout");
/*    */   
/* 69 */   public static final LogIDs STDERR = new LogIDs("stderr");
/*    */   
/* 71 */   public static final LogIDs ALERT = new LogIDs("alert");
/*    */   
/* 73 */   public static final LogIDs CACHE = new LogIDs("cache");
/*    */   
/* 75 */   public static final LogIDs PIECES = new LogIDs("pieces");
/*    */   
/* 77 */   public static final LogIDs UI3 = new LogIDs("UIv3");
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/logging/LogIDs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */