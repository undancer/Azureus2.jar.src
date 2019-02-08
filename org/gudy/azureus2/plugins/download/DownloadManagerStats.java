package org.gudy.azureus2.plugins.download;

public abstract interface DownloadManagerStats
{
  public abstract long getOverallDataBytesReceived();
  
  public abstract long getOverallDataBytesSent();
  
  public abstract long getSessionUptimeSeconds();
  
  public abstract int getDataReceiveRate();
  
  public abstract int getProtocolReceiveRate();
  
  public abstract int getDataAndProtocolReceiveRate();
  
  public abstract int getDataSendRate();
  
  public abstract int getProtocolSendRate();
  
  public abstract int getDataAndProtocolSendRate();
  
  public abstract long getDataBytesReceived();
  
  public abstract long getProtocolBytesReceived();
  
  public abstract long getDataBytesSent();
  
  public abstract long getProtocolBytesSent();
  
  public abstract long getSmoothedReceiveRate();
  
  public abstract long getSmoothedSendRate();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/DownloadManagerStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */