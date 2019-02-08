/*    */ package org.gudy.azureus2.core3.tracker.server;
/*    */ 
/*    */ import java.net.InetAddress;
/*    */ import java.util.Set;
/*    */ import org.gudy.azureus2.core3.util.Constants;
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
/*    */ public abstract interface TRTrackerServer
/*    */ {
/* 33 */   public static final String DEFAULT_NAME = Constants.APP_NAME;
/*    */   public static final int DEFAULT_MIN_RETRY_DELAY = 120;
/*    */   public static final int DEFAULT_MAX_RETRY_DELAY = 3600;
/*    */   public static final int DEFAULT_INC_BY = 60;
/*    */   public static final int DEFAULT_INC_PER = 10;
/*    */   public static final int DEFAULT_SCRAPE_RETRY_PERCENTAGE = 200;
/*    */   public static final int DEFAULT_SCRAPE_CACHE_PERIOD = 5000;
/*    */   public static final int DEFAULT_ANNOUNCE_CACHE_PERIOD = 500;
/*    */   public static final int DEFAULT_ANNOUNCE_CACHE_PEER_THRESHOLD = 500;
/*    */   public static final int DEFAULT_TRACKER_PORT = 6969;
/*    */   public static final int DEFAULT_TRACKER_PORT_SSL = 7000;
/*    */   public static final int DEFAULT_NAT_CHECK_SECS = 15;
/*    */   
/*    */   public abstract String getName();
/*    */   
/*    */   public abstract int getPort();
/*    */   
/*    */   public abstract String getHost();
/*    */   
/*    */   public abstract InetAddress getBindIP();
/*    */   
/*    */   public abstract void setReady();
/*    */   
/*    */   public abstract void setEnabled(boolean paramBoolean);
/*    */   
/*    */   public abstract boolean isSSL();
/*    */   
/*    */   public abstract void setEnableKeepAlive(boolean paramBoolean);
/*    */   
/*    */   public abstract TRTrackerServerTorrent permit(String paramString, byte[] paramArrayOfByte, boolean paramBoolean)
/*    */     throws TRTrackerServerException;
/*    */   
/*    */   public abstract TRTrackerServerTorrent permit(String paramString, byte[] paramArrayOfByte, boolean paramBoolean1, boolean paramBoolean2)
/*    */     throws TRTrackerServerException;
/*    */   
/*    */   public abstract void deny(byte[] paramArrayOfByte, boolean paramBoolean)
/*    */     throws TRTrackerServerException;
/*    */   
/*    */   public abstract TRTrackerServerTorrentStats getStats(byte[] paramArrayOfByte);
/*    */   
/*    */   public abstract TRTrackerServerPeer[] getPeers(byte[] paramArrayOfByte);
/*    */   
/*    */   public abstract TRTrackerServerStats getStats();
/*    */   
/*    */   public abstract void setBiasedPeers(Set paramSet);
/*    */   
/*    */   public abstract void addListener(TRTrackerServerListener paramTRTrackerServerListener);
/*    */   
/*    */   public abstract void removeListener(TRTrackerServerListener paramTRTrackerServerListener);
/*    */   
/*    */   public abstract void addListener2(TRTrackerServerListener2 paramTRTrackerServerListener2);
/*    */   
/*    */   public abstract void removeListener2(TRTrackerServerListener2 paramTRTrackerServerListener2);
/*    */   
/*    */   public abstract void addRequestListener(TRTrackerServerRequestListener paramTRTrackerServerRequestListener);
/*    */   
/*    */   public abstract void removeRequestListener(TRTrackerServerRequestListener paramTRTrackerServerRequestListener);
/*    */   
/*    */   public abstract void addAuthenticationListener(TRTrackerServerAuthenticationListener paramTRTrackerServerAuthenticationListener);
/*    */   
/*    */   public abstract void removeAuthenticationListener(TRTrackerServerAuthenticationListener paramTRTrackerServerAuthenticationListener);
/*    */   
/*    */   public abstract void close();
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/TRTrackerServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */