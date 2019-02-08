/*    */ package org.gudy.azureus2.core3.tracker.protocol.udp;
/*    */ 
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
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
/*    */ public class PRUDPPacketTracker
/*    */ {
/* 35 */   public static int VERSION = COConfigurationManager.getIntParameter("Tracker Port UDP Version", 2);
/*    */   public static final int DEFAULT_RETRY_COUNT = 1;
/*    */   public static final int ACT_REQUEST_CONNECT = 0;
/*    */   public static final int ACT_REQUEST_ANNOUNCE = 1;
/*    */   public static final int ACT_REQUEST_SCRAPE = 2;
/*    */   public static final int ACT_REPLY_CONNECT = 0;
/*    */   public static final int ACT_REPLY_ANNOUNCE = 1;
/*    */   public static final int ACT_REPLY_SCRAPE = 2;
/*    */   public static final int ACT_REPLY_ERROR = 3;
/*    */   public static final long INITIAL_CONNECTION_ID = 4497486125440L;
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/protocol/udp/PRUDPPacketTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */