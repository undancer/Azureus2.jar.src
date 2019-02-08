/*    */ package com.aelitis.azureus.core.messenger.config;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.core3.torrent.TOTorrent;
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
/*    */ public class PlatformTorrentMessenger
/*    */ {
/* 31 */   public static String LISTENER_ID = "torrent";
/*    */   
/* 33 */   public static String OP_STREAMCOMPLETE = "stream-complete";
/*    */   
/*    */   public static void streamComplete(TOTorrent torrent, long waitTime, int maxSeekAheadSecs, int numRebuffers, int numHardRebuffers) {}
/*    */   
/*    */   public static void streamComplete(TOTorrent torrent, Map info) {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/messenger/config/PlatformTorrentMessenger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */