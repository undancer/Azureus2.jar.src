package org.gudy.azureus2.core3.peer;

import com.aelitis.azureus.core.peermanager.PeerManagerRegistration;
import java.util.Map;
import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
import org.gudy.azureus2.core3.disk.DiskManagerReadRequestListener;
import org.gudy.azureus2.core3.logging.LogRelation;
import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;

public abstract interface PEPeerManagerAdapter
{
  public abstract String getDisplayName();
  
  public abstract int getUploadRateLimitBytesPerSecond();
  
  public abstract int getDownloadRateLimitBytesPerSecond();
  
  public abstract int getMaxUploads();
  
  public abstract int[] getMaxConnections();
  
  public abstract int[] getMaxSeedConnections();
  
  public abstract int getExtendedMessagingMode();
  
  public abstract boolean isPeerExchangeEnabled();
  
  public abstract boolean isMetadataDownload();
  
  public abstract int getUploadPriority();
  
  public abstract int getTorrentInfoDictSize();
  
  public abstract byte[] getTorrentInfoDict(PEPeer paramPEPeer);
  
  public abstract int getCryptoLevel();
  
  public abstract long getRandomSeed();
  
  public abstract boolean isPeriodicRescanEnabled();
  
  public abstract void setStateFinishing();
  
  public abstract void setStateSeeding(boolean paramBoolean);
  
  public abstract void restartDownload(boolean paramBoolean);
  
  public abstract TRTrackerScraperResponse getTrackerScrapeResponse();
  
  public abstract String getTrackerClientExtensions();
  
  public abstract void setTrackerRefreshDelayOverrides(int paramInt);
  
  public abstract boolean isNATHealthy();
  
  public abstract void addPeer(PEPeer paramPEPeer);
  
  public abstract void removePeer(PEPeer paramPEPeer);
  
  public abstract void addPiece(PEPiece paramPEPiece);
  
  public abstract void removePiece(PEPiece paramPEPiece);
  
  public abstract void discarded(PEPeer paramPEPeer, int paramInt);
  
  public abstract void protocolBytesReceived(PEPeer paramPEPeer, int paramInt);
  
  public abstract void dataBytesReceived(PEPeer paramPEPeer, int paramInt);
  
  public abstract void protocolBytesSent(PEPeer paramPEPeer, int paramInt);
  
  public abstract void dataBytesSent(PEPeer paramPEPeer, int paramInt);
  
  public abstract void statsRequest(PEPeer paramPEPeer, Map paramMap1, Map paramMap2);
  
  public abstract PeerManagerRegistration getPeerManagerRegistration();
  
  public abstract void addHTTPSeed(String paramString, int paramInt);
  
  public abstract byte[][] getSecrets(int paramInt);
  
  public abstract void enqueueReadRequest(PEPeer paramPEPeer, DiskManagerReadRequest paramDiskManagerReadRequest, DiskManagerReadRequestListener paramDiskManagerReadRequestListener);
  
  public abstract LogRelation getLogRelation();
  
  public abstract int getPosition();
  
  public abstract boolean isPeerSourceEnabled(String paramString);
  
  public abstract boolean isNetworkEnabled(String paramString);
  
  public abstract String[] getEnabledNetworks();
  
  public abstract void priorityConnectionChanged(boolean paramBoolean);
  
  public abstract boolean hasPriorityConnection();
  
  public abstract int getPermittedBytesToReceive();
  
  public abstract void permittedReceiveBytesUsed(int paramInt);
  
  public abstract int getPermittedBytesToSend();
  
  public abstract void permittedSendBytesUsed(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/PEPeerManagerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */