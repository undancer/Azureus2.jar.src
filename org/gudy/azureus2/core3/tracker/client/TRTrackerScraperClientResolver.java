/*    */ package org.gudy.azureus2.core3.tracker.client;
/*    */ 
/*    */ import java.net.URL;
/*    */ import org.gudy.azureus2.core3.util.HashWrapper;
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
/*    */ public abstract interface TRTrackerScraperClientResolver
/*    */ {
/* 34 */   public static final Character FL_NONE = new Character('n');
/* 35 */   public static final Character FL_INCOMPLETE_STOPPED = new Character('s');
/* 36 */   public static final Character FL_INCOMPLETE_QUEUED = new Character('q');
/* 37 */   public static final Character FL_INCOMPLETE_RUNNING = new Character('r');
/* 38 */   public static final Character FL_COMPLETE_STOPPED = new Character('S');
/* 39 */   public static final Character FL_COMPLETE_QUEUED = new Character('Q');
/* 40 */   public static final Character FL_COMPLETE_RUNNING = new Character('R');
/*    */   
/*    */   public abstract boolean isScrapable(HashWrapper paramHashWrapper);
/*    */   
/*    */   public abstract int[] getCachedScrape(HashWrapper paramHashWrapper);
/*    */   
/*    */   public abstract boolean isNetworkEnabled(HashWrapper paramHashWrapper, URL paramURL);
/*    */   
/*    */   public abstract String[] getEnabledNetworks(HashWrapper paramHashWrapper);
/*    */   
/*    */   public abstract Object[] getExtensions(HashWrapper paramHashWrapper);
/*    */   
/*    */   public abstract boolean redirectTrackerUrl(HashWrapper paramHashWrapper, URL paramURL1, URL paramURL2);
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/TRTrackerScraperClientResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */