package org.gudy.azureus2.core3.peer;

public abstract interface PEPeerManagerStats
{
  public abstract void discarded(PEPeer paramPEPeer, int paramInt);
  
  public abstract void hashFailed(int paramInt);
  
  public abstract void dataBytesReceived(PEPeer paramPEPeer, int paramInt);
  
  public abstract void protocolBytesReceived(PEPeer paramPEPeer, int paramInt);
  
  public abstract void dataBytesSent(PEPeer paramPEPeer, int paramInt);
  
  public abstract void protocolBytesSent(PEPeer paramPEPeer, int paramInt);
  
  public abstract void haveNewPiece(int paramInt);
  
  public abstract void haveNewConnection(boolean paramBoolean);
  
  public abstract long getDataReceiveRate();
  
  public abstract long getProtocolReceiveRate();
  
  public abstract long getDataSendRate();
  
  public abstract long getProtocolSendRate();
  
  public abstract long getPeakDataReceiveRate();
  
  public abstract long getPeakDataSendRate();
  
  public abstract long getSmoothedDataReceiveRate();
  
  public abstract long getSmoothedDataSendRate();
  
  public abstract long getTotalDataBytesSent();
  
  public abstract long getTotalProtocolBytesSent();
  
  public abstract long getTotalDataBytesReceived();
  
  public abstract long getTotalProtocolBytesReceived();
  
  public abstract long getTotalDataBytesSentNoLan();
  
  public abstract long getTotalProtocolBytesSentNoLan();
  
  public abstract long getTotalDataBytesReceivedNoLan();
  
  public abstract long getTotalProtocolBytesReceivedNoLan();
  
  public abstract long getTotalAverage();
  
  public abstract long getTotalHashFailBytes();
  
  public abstract long getTotalDiscarded();
  
  public abstract int getTimeSinceLastDataReceivedInSeconds();
  
  public abstract int getTimeSinceLastDataSentInSeconds();
  
  public abstract int getTotalIncomingConnections();
  
  public abstract int getTotalOutgoingConnections();
  
  public abstract int getPermittedBytesToReceive();
  
  public abstract void permittedReceiveBytesUsed(int paramInt);
  
  public abstract int getPermittedBytesToSend();
  
  public abstract void permittedSendBytesUsed(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/PEPeerManagerStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */