/*    */ package org.gudy.azureus2.ui.swt.views.clientstats;
/*    */ 
/*    */ import com.aelitis.azureus.util.MapUtils;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.core3.util.BEncodableObject;
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
/*    */ public class ClientStatsDataSource
/*    */   implements BEncodableObject
/*    */ {
/*    */   public String client;
/*    */   public int count;
/*    */   public int current;
/*    */   public long bytesReceived;
/*    */   public long bytesDiscarded;
/*    */   public long bytesSent;
/*    */   public Map<String, Map<String, Object>> perNetworkStats;
/*    */   public ClientStatsOverall overall;
/*    */   
/*    */   public ClientStatsDataSource()
/*    */   {
/* 49 */     this.perNetworkStats = new HashMap();
/*    */   }
/*    */   
/*    */   public ClientStatsDataSource(Map loadMap) {
/* 53 */     this.client = MapUtils.getMapString(loadMap, "client", "?");
/* 54 */     this.count = MapUtils.getMapInt(loadMap, "count", 0);
/* 55 */     this.bytesReceived = MapUtils.getMapLong(loadMap, "bytesReceived", 0L);
/* 56 */     this.bytesDiscarded = MapUtils.getMapLong(loadMap, "bytesDiscarded", 0L);
/* 57 */     this.bytesSent = MapUtils.getMapLong(loadMap, "bytesSent", 0L);
/* 58 */     this.perNetworkStats = MapUtils.getMapMap(loadMap, "perNetworkStats", new HashMap());
/*    */   }
/*    */   
/*    */   public Object toBencodeObject()
/*    */   {
/* 63 */     Map<String, Object> map = new HashMap();
/* 64 */     map.put("client", this.client);
/* 65 */     map.put("count", Long.valueOf(this.count));
/* 66 */     map.put("bytesReceived", Long.valueOf(this.bytesReceived));
/* 67 */     map.put("bytesDiscarded", Long.valueOf(this.bytesDiscarded));
/* 68 */     map.put("bytesSent", Long.valueOf(this.bytesSent));
/* 69 */     map.put("perNetworkStats", this.perNetworkStats);
/* 70 */     return map;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/clientstats/ClientStatsDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */