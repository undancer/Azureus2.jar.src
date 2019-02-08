/*    */ package org.gudy.azureus2.core3.tracker.client;
/*    */ 
/*    */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerScraperImpl;
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
/*    */ 
/*    */ public class TRTrackerScraperFactory
/*    */ {
/*    */   public static TRTrackerScraper getSingleton()
/*    */   {
/* 38 */     return TRTrackerScraperImpl.create();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/TRTrackerScraperFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */