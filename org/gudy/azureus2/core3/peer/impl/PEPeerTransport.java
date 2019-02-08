package org.gudy.azureus2.core3.peer.impl;

import com.aelitis.azureus.core.peermanager.peerdb.PeerItem;
import java.util.List;
import java.util.Map;
import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
import org.gudy.azureus2.core3.peer.PEPeer;
import org.gudy.azureus2.core3.util.IndentWriter;

public abstract interface PEPeerTransport
  extends PEPeer
{
  public static final int CONNECTION_PENDING = 0;
  public static final int CONNECTION_CONNECTING = 1;
  public static final int CONNECTION_WAITING_FOR_HANDSHAKE = 2;
  public static final int CONNECTION_FULLY_ESTABLISHED = 4;
  
  public abstract void start();
  
  public abstract void sendChoke();
  
  public abstract void sendUnChoke();
  
  public abstract void sendHave(int paramInt);
  
  public abstract void sendCancel(DiskManagerReadRequest paramDiskManagerReadRequest);
  
  public abstract void sendBadPiece(int paramInt);
  
  public abstract void sendStatsRequest(Map paramMap);
  
  public abstract void sendStatsReply(Map paramMap);
  
  public abstract boolean requestAllocationStarts(int[] paramArrayOfInt);
  
  public abstract void requestAllocationComplete();
  
  public abstract DiskManagerReadRequest request(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean);
  
  public abstract int getRequestIndex(DiskManagerReadRequest paramDiskManagerReadRequest);
  
  public abstract void closeConnection(String paramString);
  
  public abstract boolean transferAvailable();
  
  public abstract long getLastMessageSentTime();
  
  public abstract List getExpiredRequests();
  
  public abstract int getMaxNbRequests();
  
  public abstract int getNbRequests();
  
  public abstract PEPeerControl getControl();
  
  public abstract int[] getPriorityOffsets();
  
  public abstract void doKeepAliveCheck();
  
  public abstract boolean doTimeoutChecks();
  
  public abstract void doPerformanceTuningCheck();
  
  public abstract int getConnectionState();
  
  public abstract long getTimeSinceLastDataMessageReceived();
  
  public abstract long getTimeSinceGoodDataReceived();
  
  public abstract long getTimeSinceLastDataMessageSent();
  
  public abstract long getUnchokedForMillis();
  
  public abstract long getLatency();
  
  public abstract void updatePeerExchange();
  
  public abstract PeerItem getPeerItemIdentity();
  
  public abstract boolean isStalledPendingLoad();
  
  public abstract boolean isLANLocal();
  
  public abstract boolean isTCP();
  
  public abstract void checkInterested();
  
  public abstract PEPeerTransport reconnect(boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract boolean isSafeForReconnect();
  
  public abstract String getNetwork();
  
  public abstract void generateEvidence(IndentWriter paramIndentWriter);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/impl/PEPeerTransport.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */