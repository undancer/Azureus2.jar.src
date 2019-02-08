/*    */ package org.gudy.azureus2.plugins.peers;
/*    */ 
/*    */ import java.util.List;
/*    */ import org.gudy.azureus2.plugins.messaging.Message;
/*    */ import org.gudy.azureus2.plugins.network.Connection;
/*    */ import org.gudy.azureus2.plugins.network.ConnectionStub;
/*    */ import org.gudy.azureus2.plugins.network.RateLimiter;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract interface Peer
/*    */ {
/*    */   public static final int CONNECTING = 10;
/*    */   public static final int HANDSHAKING = 20;
/*    */   public static final int TRANSFERING = 30;
/*    */   public static final int CLOSING = 40;
/*    */   public static final int DISCONNECTED = 50;
/* 49 */   public static final Object PR_PRIORITY_CONNECTION = new Object();
/* 50 */   public static final Object PR_PROTOCOL = new Object();
/* 51 */   public static final Object PR_PROTOCOL_QUALIFIER = new Object();
/*    */   
/*    */   public abstract void bindConnection(ConnectionStub paramConnectionStub);
/*    */   
/*    */   public abstract PeerManager getManager();
/*    */   
/*    */   public abstract int getState();
/*    */   
/*    */   public abstract byte[] getId();
/*    */   
/*    */   public abstract String getIp();
/*    */   
/*    */   public abstract int getTCPListenPort();
/*    */   
/*    */   public abstract int getUDPListenPort();
/*    */   
/*    */   public abstract int getUDPNonDataListenPort();
/*    */   
/*    */   public abstract int getPort();
/*    */   
/*    */   public abstract boolean isLANLocal();
/*    */   
/*    */   public abstract boolean[] getAvailable();
/*    */   
/*    */   public abstract boolean isPieceAvailable(int paramInt);
/*    */   
/*    */   public abstract boolean isTransferAvailable();
/*    */   
/*    */   public abstract int readBytes(int paramInt);
/*    */   
/*    */   public abstract int writeBytes(int paramInt);
/*    */   
/*    */   public abstract boolean isDownloadPossible();
/*    */   
/*    */   public abstract boolean isChoked();
/*    */   
/*    */   public abstract boolean isChoking();
/*    */   
/*    */   public abstract boolean isInterested();
/*    */   
/*    */   public abstract boolean isInteresting();
/*    */   
/*    */   public abstract boolean isSeed();
/*    */   
/*    */   public abstract boolean isSnubbed();
/*    */   
/*    */   public abstract long getSnubbedTime();
/*    */   
/*    */   public abstract void setSnubbed(boolean paramBoolean);
/*    */   
/*    */   public abstract PeerStats getStats();
/*    */   
/*    */   public abstract boolean isIncoming();
/*    */   
/*    */   /**
/*    */    * @deprecated
/*    */    */
/*    */   public abstract int getPercentDone();
/*    */   
/*    */   public abstract int getPercentDoneInThousandNotation();
/*    */   
/*    */   public abstract String getClient();
/*    */   
/*    */   public abstract boolean isOptimisticUnchoke();
/*    */   
/*    */   public abstract void setOptimisticUnchoke(boolean paramBoolean);
/*    */   
/*    */   public abstract List getExpiredRequests();
/*    */   
/*    */   public abstract List getRequests();
/*    */   
/*    */   public abstract int getMaximumNumberOfRequests();
/*    */   
/*    */   public abstract int getNumberOfRequests();
/*    */   
/*    */   public abstract void cancelRequest(PeerReadRequest paramPeerReadRequest);
/*    */   
/*    */   public abstract boolean requestAllocationStarts(int[] paramArrayOfInt);
/*    */   
/*    */   public abstract int[] getPriorityOffsets();
/*    */   
/*    */   public abstract void requestAllocationComplete();
/*    */   
/*    */   public abstract boolean addRequest(PeerReadRequest paramPeerReadRequest);
/*    */   
/*    */   public abstract void close(String paramString, boolean paramBoolean1, boolean paramBoolean2);
/*    */   
/*    */   public abstract int getPercentDoneOfCurrentIncomingRequest();
/*    */   
/*    */   public abstract int[] getOutgoingRequestedPieceNumbers();
/*    */   
/*    */   public abstract int getOutgoingRequestCount();
/*    */   
/*    */   public abstract int getPercentDoneOfCurrentOutgoingRequest();
/*    */   
/*    */   /**
/*    */    * @deprecated
/*    */    */
/*    */   public abstract void addListener(PeerListener paramPeerListener);
/*    */   
/*    */   /**
/*    */    * @deprecated
/*    */    */
/*    */   public abstract void removeListener(PeerListener paramPeerListener);
/*    */   
/*    */   public abstract void addListener(PeerListener2 paramPeerListener2);
/*    */   
/*    */   public abstract void removeListener(PeerListener2 paramPeerListener2);
/*    */   
/*    */   public abstract Connection getConnection();
/*    */   
/*    */   public abstract boolean supportsMessaging();
/*    */   
/*    */   public abstract Message[] getSupportedMessages();
/*    */   
/*    */   public abstract void setUserData(Object paramObject1, Object paramObject2);
/*    */   
/*    */   public abstract Object getUserData(Object paramObject);
/*    */   
/*    */   public abstract byte[] getHandshakeReservedBytes();
/*    */   
/*    */   public abstract boolean isPriorityConnection();
/*    */   
/*    */   public abstract void setPriorityConnection(boolean paramBoolean);
/*    */   
/*    */   public abstract void addRateLimiter(RateLimiter paramRateLimiter, boolean paramBoolean);
/*    */   
/*    */   public abstract void removeRateLimiter(RateLimiter paramRateLimiter, boolean paramBoolean);
/*    */   
/*    */   public abstract RateLimiter[] getRateLimiters(boolean paramBoolean);
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/peers/Peer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */