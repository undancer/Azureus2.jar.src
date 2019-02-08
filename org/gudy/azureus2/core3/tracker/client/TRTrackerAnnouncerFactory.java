/*    */ package org.gudy.azureus2.core3.tracker.client;
/*    */ 
/*    */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*    */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerAnnouncerFactoryImpl;
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
/*    */ public class TRTrackerAnnouncerFactory
/*    */ {
/*    */   public static TRTrackerAnnouncer create(TOTorrent torrent)
/*    */     throws TRTrackerAnnouncerException
/*    */   {
/* 38 */     return create(torrent, null);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static TRTrackerAnnouncer create(TOTorrent torrent, boolean manual)
/*    */     throws TRTrackerAnnouncerException
/*    */   {
/* 48 */     return TRTrackerAnnouncerFactoryImpl.create(torrent, null, manual);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static TRTrackerAnnouncer create(TOTorrent torrent, DataProvider provider)
/*    */     throws TRTrackerAnnouncerException
/*    */   {
/* 58 */     return TRTrackerAnnouncerFactoryImpl.create(torrent, provider, false);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static void addListener(TRTrackerAnnouncerFactoryListener l)
/*    */   {
/* 65 */     TRTrackerAnnouncerFactoryImpl.addListener(l);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static void removeListener(TRTrackerAnnouncerFactoryListener l)
/*    */   {
/* 72 */     TRTrackerAnnouncerFactoryImpl.removeListener(l);
/*    */   }
/*    */   
/*    */   public static abstract interface DataProvider
/*    */   {
/*    */     public abstract String[] getNetworks();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/TRTrackerAnnouncerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */