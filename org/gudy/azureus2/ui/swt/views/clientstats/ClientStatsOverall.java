/*    */ package org.gudy.azureus2.ui.swt.views.clientstats;
/*    */ 
/*    */ import com.aelitis.azureus.util.MapUtils;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
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
/*    */ public class ClientStatsOverall
/*    */ {
/*    */   long count;
/*    */   
/*    */   public ClientStatsOverall() {}
/*    */   
/*    */   public ClientStatsOverall(Map loadMap)
/*    */   {
/* 41 */     if (loadMap == null) {
/* 42 */       return;
/*    */     }
/* 44 */     this.count = MapUtils.getMapLong(loadMap, "count", 0L);
/*    */   }
/*    */   
/*    */   public Map<String, Object> toMap() {
/* 48 */     Map<String, Object> map = new HashMap();
/* 49 */     map.put("count", Long.valueOf(this.count));
/* 50 */     return map;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/clientstats/ClientStatsOverall.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */