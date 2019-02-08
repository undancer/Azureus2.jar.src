package org.gudy.azureus2.core3.peer;

import com.aelitis.azureus.core.networkmanager.LimitedRateGroup;
import com.aelitis.azureus.core.peermanager.peerdb.PeerExchangerItem;
import com.aelitis.azureus.core.peermanager.piecepicker.PiecePicker;
import com.aelitis.azureus.core.tracker.TrackerPeerSource;
import java.util.List;
import java.util.Map;
import org.gudy.azureus2.core3.disk.DiskManager;
import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
import org.gudy.azureus2.core3.peer.util.PeerIdentityDataID;
import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse;
import org.gudy.azureus2.core3.util.DirectByteBuffer;
import org.gudy.azureus2.core3.util.IndentWriter;
import org.gudy.azureus2.plugins.peers.PeerDescriptor;

public abstract interface PEPeerManager
{
  public abstract DiskManager getDiskManager();
  
  public abstract PiecePicker getPiecePicker();
  
  public abstract PEPeerManagerAdapter getAdapter();
  
  public abstract void start();
  
  public abstract void stopAll();
  
  public abstract byte[] getHash();
  
  public abstract String getDisplayName();
  
  public abstract PeerIdentityDataID getPeerIdentityDataID();
  
  public abstract byte[] getPeerId();
  
  public abstract int[] getAvailability();
  
  public abstract int getAvailability(int paramInt);
  
  public abstract float getAvgAvail();
  
  public abstract float getMinAvailability();
  
  public abstract float getMinAvailability(int paramInt);
  
  public abstract long getAvailWentBadTime();
  
  public abstract long getBytesUnavailable();
  
  public abstract boolean hasDownloadablePiece();
  
  public abstract int getBytesQueuedForUpload();
  
  public abstract int getNbPeersWithUploadQueued();
  
  public abstract int getNbPeersWithUploadBlocked();
  
  public abstract int getNbPeersUnchoked();
  
  public abstract PEPiece[] getPieces();
  
  public abstract PEPiece getPiece(int paramInt);
  
  public abstract PEPeerManagerStats getStats();
  
  public abstract void processTrackerResponse(TRTrackerAnnouncerResponse paramTRTrackerAnnouncerResponse);
  
  public abstract int getNbPeers();
  
  public abstract int getNbSeeds();
  
  public abstract int getPieceLength(int paramInt);
  
  public abstract long getRemaining();
  
  public abstract long getHiddenBytes();
  
  public abstract long getETA(boolean paramBoolean);
  
  public abstract String getElapsedTime();
  
  public abstract long getTimeStarted(boolean paramBoolean);
  
  public abstract long getTimeStartedSeeding(boolean paramBoolean);
  
  public abstract void addListener(PEPeerManagerListener paramPEPeerManagerListener);
  
  public abstract void removeListener(PEPeerManagerListener paramPEPeerManagerListener);
  
  public abstract void addPiece(PEPiece paramPEPiece, int paramInt, PEPeer paramPEPeer);
  
  public abstract boolean needsMD5CheckOnCompletion(int paramInt);
  
  public abstract boolean isSeeding();
  
  public abstract boolean isMetadataDownload();
  
  public abstract int getTorrentInfoDictSize();
  
  public abstract void setTorrentInfoDictSize(int paramInt);
  
  public abstract boolean isSuperSeedMode();
  
  public abstract boolean canToggleSuperSeedMode();
  
  public abstract void setSuperSeedMode(boolean paramBoolean);
  
  public abstract boolean seedPieceRecheck();
  
  public abstract int getNbRemoteTCPConnections();
  
  public abstract int getNbRemoteUDPConnections();
  
  public abstract int getNbRemoteUTPConnections();
  
  public abstract long getLastRemoteConnectionTime();
  
  public abstract int getMaxNewConnectionsAllowed(String paramString);
  
  public abstract boolean hasPotentialConnections();
  
  public abstract void dataBytesReceived(PEPeer paramPEPeer, int paramInt);
  
  public abstract void dataBytesSent(PEPeer paramPEPeer, int paramInt);
  
  public abstract void protocolBytesSent(PEPeer paramPEPeer, int paramInt);
  
  public abstract void protocolBytesReceived(PEPeer paramPEPeer, int paramInt);
  
  public abstract void discarded(PEPeer paramPEPeer, int paramInt);
  
  public abstract PEPeerStats createPeerStats(PEPeer paramPEPeer);
  
  public abstract List<PEPeer> getPeers();
  
  public abstract List<PEPeer> getPeers(String paramString);
  
  public abstract int getPendingPeerCount();
  
  public abstract PeerDescriptor[] getPendingPeers();
  
  public abstract PeerDescriptor[] getPendingPeers(String paramString);
  
  public abstract void addPeer(PEPeer paramPEPeer);
  
  public abstract void addPeer(String paramString, int paramInt1, int paramInt2, boolean paramBoolean, Map paramMap);
  
  public abstract void peerDiscovered(String paramString1, String paramString2, int paramInt1, int paramInt2, boolean paramBoolean);
  
  public abstract void removePeer(PEPeer paramPEPeer);
  
  public abstract void removePeer(PEPeer paramPEPeer, String paramString);
  
  public abstract void peerAdded(PEPeer paramPEPeer);
  
  public abstract void peerRemoved(PEPeer paramPEPeer);
  
  public abstract DiskManagerReadRequest createDiskManagerRequest(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract void requestCanceled(DiskManagerReadRequest paramDiskManagerReadRequest);
  
  public abstract boolean requestExists(String paramString, int paramInt1, int paramInt2, int paramInt3);
  
  public abstract boolean validatePieceReply(PEPeerTransport paramPEPeerTransport, int paramInt1, int paramInt2, DirectByteBuffer paramDirectByteBuffer);
  
  public abstract void writeBlock(int paramInt1, int paramInt2, DirectByteBuffer paramDirectByteBuffer, Object paramObject, boolean paramBoolean);
  
  public abstract boolean isWritten(int paramInt1, int paramInt2);
  
  public abstract boolean isInEndGameMode();
  
  public abstract void peerConnectionClosed(PEPeerTransport paramPEPeerTransport, boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract PeerExchangerItem createPeerExchangeConnection(PEPeerTransport paramPEPeerTransport);
  
  public abstract void peerVerifiedAsSelf(PEPeerTransport paramPEPeerTransport);
  
  public abstract LimitedRateGroup getUploadLimitedRateGroup();
  
  public abstract LimitedRateGroup getDownloadLimitedRateGroup();
  
  public abstract int getUploadRateLimitBytesPerSecond();
  
  public abstract int getDownloadRateLimitBytesPerSecond();
  
  public abstract Object getData(String paramString);
  
  public abstract void setData(String paramString, Object paramObject);
  
  public abstract int getAverageCompletionInThousandNotation();
  
  public abstract PEPeerTransport getTransportFromIdentity(byte[] paramArrayOfByte);
  
  public abstract PEPeerTransport getTransportFromAddress(String paramString);
  
  public abstract boolean getPreferUDP();
  
  public abstract void setPreferUDP(boolean paramBoolean);
  
  public abstract void addRateLimiter(LimitedRateGroup paramLimitedRateGroup, boolean paramBoolean);
  
  public abstract void removeRateLimiter(LimitedRateGroup paramLimitedRateGroup, boolean paramBoolean);
  
  public abstract TrackerPeerSource getTrackerPeerSource();
  
  public abstract boolean isPeerSourceEnabled(String paramString);
  
  public abstract boolean isNetworkEnabled(String paramString);
  
  public abstract int getPartitionID();
  
  public abstract boolean isDestroyed();
  
  public abstract void generateEvidence(IndentWriter paramIndentWriter);
  
  public abstract void setStatsReceiver(StatsReceiver paramStatsReceiver);
  
  public static abstract interface StatsReceiver
  {
    public abstract void receiveStats(PEPeer paramPEPeer, Map paramMap);
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/PEPeerManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */