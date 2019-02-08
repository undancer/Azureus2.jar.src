/*    */ package com.aelitis.azureus.core.tag;
/*    */ 
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
/*    */ public class TagDiscovery
/*    */ {
/* 32 */   public static int DISCOVERY_TYPE_RCM = 0;
/* 33 */   public static int DISCOVERY_TYPE_META_PARSE = 1;
/*    */   
/*    */   private final String name;
/*    */   
/*    */   private final String torrentName;
/*    */   
/*    */   private final String network;
/*    */   
/*    */   private final byte[] hash;
/*    */   private final long timestamp;
/*    */   private int discoveryType;
/*    */   
/*    */   public TagDiscovery(String name, String network, String torrentName, byte[] hash)
/*    */   {
/* 47 */     this.name = name;
/* 48 */     this.network = network;
/* 49 */     this.torrentName = torrentName;
/* 50 */     this.hash = hash;
/* 51 */     this.timestamp = SystemTime.getCurrentTime();
/*    */   }
/*    */   
/*    */   public String getName() {
/* 55 */     return this.name;
/*    */   }
/*    */   
/*    */   public String getNetwork() {
/* 59 */     return this.network;
/*    */   }
/*    */   
/*    */   public String getTorrentName() {
/* 63 */     return this.torrentName;
/*    */   }
/*    */   
/*    */   public byte[] getHash() {
/* 67 */     return this.hash;
/*    */   }
/*    */   
/*    */   public long getTimestamp() {
/* 71 */     return this.timestamp;
/*    */   }
/*    */   
/*    */   public long getDiscoveryType() {
/* 75 */     return this.discoveryType;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TagDiscovery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */