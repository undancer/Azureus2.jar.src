package com.aelitis.azureus.core.tracker;

public abstract interface TrackerPeerSource
{
  public static final int TP_UNKNOWN = 0;
  public static final int TP_TRACKER = 1;
  public static final int TP_HTTP_SEED = 2;
  public static final int TP_DHT = 3;
  public static final int TP_LAN = 4;
  public static final int TP_PEX = 5;
  public static final int TP_INCOMING = 6;
  public static final int TP_PLUGIN = 7;
  public static final int ST_UNKNOWN = 0;
  public static final int ST_DISABLED = 1;
  public static final int ST_STOPPED = 2;
  public static final int ST_QUEUED = 3;
  public static final int ST_UPDATING = 4;
  public static final int ST_ONLINE = 5;
  public static final int ST_ERROR = 6;
  public static final int ST_AVAILABLE = 7;
  public static final int ST_UNAVAILABLE = 8;
  public static final int ST_INITIALISING = 9;
  
  public abstract int getType();
  
  public abstract String getName();
  
  public abstract int getStatus();
  
  public abstract String getStatusString();
  
  public abstract int getSeedCount();
  
  public abstract int getLeecherCount();
  
  public abstract int getPeers();
  
  public abstract int getCompletedCount();
  
  public abstract int getLastUpdate();
  
  public abstract int getSecondsToUpdate();
  
  public abstract int getInterval();
  
  public abstract int getMinInterval();
  
  public abstract boolean isUpdating();
  
  public abstract boolean canManuallyUpdate();
  
  public abstract void manualUpdate();
  
  public abstract boolean canDelete();
  
  public abstract void delete();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tracker/TrackerPeerSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */