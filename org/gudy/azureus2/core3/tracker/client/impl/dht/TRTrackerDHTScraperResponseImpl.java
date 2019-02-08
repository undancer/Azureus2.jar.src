/*    */ package org.gudy.azureus2.core3.tracker.client.impl.dht;
/*    */ 
/*    */ import java.net.URL;
/*    */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerScraperResponseImpl;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TRTrackerDHTScraperResponseImpl
/*    */   extends TRTrackerScraperResponseImpl
/*    */ {
/*    */   private final URL url;
/*    */   
/*    */   protected TRTrackerDHTScraperResponseImpl(HashWrapper hash, URL _url)
/*    */   {
/* 43 */     super(hash);
/*    */     
/* 45 */     this.url = _url;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setSeedsPeers(int s, int p)
/*    */   {
/* 53 */     setSeeds(s);
/* 54 */     setPeers(p);
/*    */   }
/*    */   
/*    */ 
/*    */   public URL getURL()
/*    */   {
/* 60 */     return this.url;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setDHTBackup(boolean is_backup) {}
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean isDHTBackup()
/*    */   {
/* 73 */     return false;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/impl/dht/TRTrackerDHTScraperResponseImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */