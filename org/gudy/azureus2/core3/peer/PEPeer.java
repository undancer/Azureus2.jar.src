/*    */ package org.gudy.azureus2.core3.peer;
/*    */ 
/*    */ import com.aelitis.azureus.core.networkmanager.LimitedRateGroup;
/*    */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*    */ import com.aelitis.azureus.core.peermanager.piecepicker.util.BitFlags;
/*    */ import com.aelitis.azureus.core.tag.Taggable;
/*    */ import java.net.InetAddress;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
/*    */ import org.gudy.azureus2.plugins.network.Connection;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract interface PEPeer
/*    */   extends Taggable
/*    */ {
/*    */   public static final int CONNECTING = 10;
/*    */   public static final int HANDSHAKING = 20;
/*    */   public static final int TRANSFERING = 30;
/*    */   public static final int CLOSING = 40;
/*    */   public static final int DISCONNECTED = 50;
/* 62 */   public static final String[] StateNames = { "Twinkle", "Connecting", "Handshaking", "Transfering", "Closing", "Disconnected" };
/*    */   public static final int MESSAGING_BT_ONLY = 1;
/*    */   public static final int MESSAGING_AZMP = 2;
/*    */   public static final int MESSAGING_LTEP = 3;
/*    */   public static final int MESSAGING_EXTERN = 4;
/*    */   
/*    */   public abstract void addListener(PEPeerListener paramPEPeerListener);
/*    */   
/*    */   public abstract void removeListener(PEPeerListener paramPEPeerListener);
/*    */   
/*    */   public abstract int getPeerState();
/*    */   
/*    */   public abstract PEPeerManager getManager();
/*    */   
/*    */   public abstract String getPeerSource();
/*    */   
/*    */   public abstract byte[] getId();
/*    */   
/*    */   public abstract String getIp();
/*    */   
/*    */   public abstract InetAddress getAlternativeIPv6();
/*    */   
/*    */   public abstract int getPort();
/*    */   
/*    */   public abstract String getIPHostName();
/*    */   
/*    */   public abstract int getTCPListenPort();
/*    */   
/*    */   public abstract int getUDPListenPort();
/*    */   
/*    */   public abstract int getUDPNonDataListenPort();
/*    */   
/*    */   public abstract BitFlags getAvailable();
/*    */   
/*    */   public abstract boolean isPieceAvailable(int paramInt);
/*    */   
/*    */   public abstract boolean transferAvailable();
/*    */   
/*    */   public abstract void setSnubbed(boolean paramBoolean);
/*    */   
/*    */   public abstract boolean isChokingMe();
/*    */   
/*    */   public abstract boolean isUnchokeOverride();
/*    */   
/*    */   public abstract boolean isChokedByMe();
/*    */   
/*    */   public abstract void sendChoke();
/*    */   
/*    */   public abstract void sendUnChoke();
/*    */   
/*    */   public abstract boolean isInteresting();
/*    */   
/*    */   public abstract boolean isInterested();
/*    */   
/*    */   public abstract boolean isDownloadPossible();
/*    */   
/*    */   public abstract boolean isSeed();
/*    */   
/*    */   public abstract boolean isRelativeSeed();
/*    */   
/*    */   public abstract boolean isSnubbed();
/*    */   
/*    */   public abstract long getSnubbedTime();
/*    */   
/*    */   public abstract PEPeerStats getStats();
/*    */   
/*    */   public abstract boolean isIncoming();
/*    */   
/*    */   public abstract boolean hasReceivedBitField();
/*    */   
/*    */   public abstract int getPercentDoneInThousandNotation();
/*    */   
/*    */   public abstract String getClient();
/*    */   
/*    */   public abstract boolean isOptimisticUnchoke();
/*    */   
/*    */   public abstract void setOptimisticUnchoke(boolean paramBoolean);
/*    */   
/*    */   public abstract void setUploadHint(int paramInt);
/*    */   
/*    */   public abstract int getUploadHint();
/*    */   
/*    */   public abstract void setUniqueAnnounce(int paramInt);
/*    */   
/*    */   public abstract int getUniqueAnnounce();
/*    */   
/*    */   public abstract int getConsecutiveNoRequestCount();
/*    */   
/*    */   public abstract void setConsecutiveNoRequestCount(int paramInt);
/*    */   
/*    */   public abstract void setUploadRateLimitBytesPerSecond(int paramInt);
/*    */   
/*    */   public abstract void setDownloadRateLimitBytesPerSecond(int paramInt);
/*    */   
/*    */   public abstract int getUploadRateLimitBytesPerSecond();
/*    */   
/*    */   public abstract int getDownloadRateLimitBytesPerSecond();
/*    */   
/*    */   public abstract void addRateLimiter(LimitedRateGroup paramLimitedRateGroup, boolean paramBoolean);
/*    */   
/*    */   public abstract LimitedRateGroup[] getRateLimiters(boolean paramBoolean);
/*    */   
/*    */   public abstract void removeRateLimiter(LimitedRateGroup paramLimitedRateGroup, boolean paramBoolean);
/*    */   
/*    */   public abstract void setUploadDisabled(Object paramObject, boolean paramBoolean);
/*    */   
/*    */   public abstract void setDownloadDisabled(Object paramObject, boolean paramBoolean);
/*    */   
/*    */   public abstract boolean isUploadDisabled();
/*    */   
/*    */   public abstract boolean isDownloadDisabled();
/*    */   
/*    */   public abstract void updateAutoUploadPriority(Object paramObject, boolean paramBoolean);
/*    */   
/*    */   public abstract Object getData(String paramString);
/*    */   
/*    */   public abstract void setData(String paramString, Object paramObject);
/*    */   
/*    */   public abstract Object getUserData(Object paramObject);
/*    */   
/*    */   public abstract void setUserData(Object paramObject1, Object paramObject2);
/*    */   
/*    */   public abstract Connection getPluginConnection();
/*    */   
/*    */   public abstract boolean supportsMessaging();
/*    */   
/*    */   public abstract int getMessagingMode();
/*    */   
/*    */   public abstract String getEncryption();
/*    */   
/*    */   public abstract String getProtocol();
/*    */   
/*    */   public abstract String getProtocolQualifier();
/*    */   
/*    */   public abstract Message[] getSupportedMessages();
/*    */   
/*    */   public abstract void addReservedPieceNumber(int paramInt);
/*    */   
/*    */   public abstract void removeReservedPieceNumber(int paramInt);
/*    */   
/*    */   public abstract int[] getReservedPieceNumbers();
/*    */   
/*    */   public abstract int getIncomingRequestCount();
/*    */   
/*    */   public abstract int getOutgoingRequestCount();
/*    */   
/*    */   public abstract int getOutboundDataQueueSize();
/*    */   
/*    */   public abstract int[] getIncomingRequestedPieceNumbers();
/*    */   
/*    */   public abstract int[] getOutgoingRequestedPieceNumbers();
/*    */   
/*    */   public abstract int getPercentDoneOfCurrentIncomingRequest();
/*    */   
/*    */   public abstract int getPercentDoneOfCurrentOutgoingRequest();
/*    */   
/*    */   public abstract long getBytesRemaining();
/*    */   
/*    */   public abstract void setSuspendedLazyBitFieldEnabled(boolean paramBoolean);
/*    */   
/*    */   public abstract long getTimeSinceConnectionEstablished();
/*    */   
/*    */   public abstract void setLastPiece(int paramInt);
/*    */   
/*    */   public abstract int getLastPiece();
/*    */   
/*    */   public abstract boolean isLANLocal();
/*    */   
/*    */   public abstract boolean sendRequestHint(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*    */   
/*    */   public abstract int[] getRequestHint();
/*    */   
/*    */   public abstract void clearRequestHint();
/*    */   
/*    */   public abstract void sendStatsRequest(Map paramMap);
/*    */   
/*    */   public abstract void sendRejectRequest(DiskManagerReadRequest paramDiskManagerReadRequest);
/*    */   
/*    */   public abstract void setHaveAggregationEnabled(boolean paramBoolean);
/*    */   
/*    */   public abstract byte[] getHandshakeReservedBytes();
/*    */   
/*    */   public abstract String getClientNameFromPeerID();
/*    */   
/*    */   public abstract String getClientNameFromExtensionHandshake();
/*    */   
/*    */   public abstract boolean isPriorityConnection();
/*    */   
/*    */   public abstract void setPriorityConnection(boolean paramBoolean);
/*    */   
/*    */   public abstract boolean isClosed();
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/PEPeer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */