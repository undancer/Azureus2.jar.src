/*    */ package com.aelitis.azureus.core.dht.router;
/*    */ 
/*    */ import com.aelitis.azureus.core.dht.DHTLogger;
/*    */ import com.aelitis.azureus.core.dht.router.impl.DHTRouterImpl;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.gudy.azureus2.core3.util.Debug;
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
/*    */ public class DHTRouterFactory
/*    */ {
/* 37 */   private static final List observers = new ArrayList();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static DHTRouter create(int K, int B, int max_rep_per_node, byte[] id, DHTRouterContactAttachment attachment, DHTLogger logger)
/*    */   {
/* 48 */     DHTRouterImpl res = new DHTRouterImpl(K, B, max_rep_per_node, id, attachment, logger);
/*    */     
/* 50 */     for (int i = 0; i < observers.size(); i++) {
/*    */       try
/*    */       {
/* 53 */         ((DHTRouterFactoryObserver)observers.get(i)).routerCreated(res);
/*    */       }
/*    */       catch (Throwable e)
/*    */       {
/* 57 */         Debug.printStackTrace(e);
/*    */       }
/*    */     }
/*    */     
/* 61 */     return res;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static void addObserver(DHTRouterFactoryObserver observer)
/*    */   {
/* 68 */     observers.add(observer);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static void removeObserver(DHTRouterFactoryObserver observer)
/*    */   {
/* 75 */     observers.remove(observer);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/router/DHTRouterFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */