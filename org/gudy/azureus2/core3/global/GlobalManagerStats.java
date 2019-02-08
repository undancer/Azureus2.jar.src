package org.gudy.azureus2.core3.global;

public abstract interface GlobalManagerStats
{
  public abstract int getDataReceiveRate();
  
  public abstract int getDataReceiveRateNoLAN();
  
  public abstract int getDataReceiveRateNoLAN(int paramInt);
  
  public abstract int getProtocolReceiveRate();
  
  public abstract int getProtocolReceiveRateNoLAN();
  
  public abstract int getProtocolReceiveRateNoLAN(int paramInt);
  
  public abstract int getDataAndProtocolReceiveRate();
  
  public abstract int getDataSendRate();
  
  public abstract int getDataSendRateNoLAN();
  
  public abstract int getDataSendRateNoLAN(int paramInt);
  
  public abstract int getProtocolSendRate();
  
  public abstract int getProtocolSendRateNoLAN();
  
  public abstract int getProtocolSendRateNoLAN(int paramInt);
  
  public abstract int getDataAndProtocolSendRate();
  
  public abstract long getSmoothedSendRate();
  
  public abstract long getSmoothedReceiveRate();
  
  public abstract int getDataSendRateAtClose();
  
  public abstract long getTotalDataBytesReceived();
  
  public abstract long getTotalProtocolBytesReceived();
  
  public abstract long getTotalDataBytesSent();
  
  public abstract long getTotalProtocolBytesSent();
  
  public abstract long getTotalSwarmsPeerRate(boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract void dataBytesSent(int paramInt, boolean paramBoolean);
  
  public abstract void protocolBytesSent(int paramInt, boolean paramBoolean);
  
  public abstract void dataBytesReceived(int paramInt, boolean paramBoolean);
  
  public abstract void protocolBytesReceived(int paramInt, boolean paramBoolean);
  
  public abstract void discarded(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/global/GlobalManagerStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */