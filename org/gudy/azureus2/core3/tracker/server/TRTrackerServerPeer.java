package org.gudy.azureus2.core3.tracker.server;

import java.util.Map;

public abstract interface TRTrackerServerPeer
  extends TRTrackerServerPeerBase
{
  public static final byte NAT_CHECK_UNKNOWN = 0;
  public static final byte NAT_CHECK_DISABLED = 1;
  public static final byte NAT_CHECK_INITIATED = 2;
  public static final byte NAT_CHECK_OK = 3;
  public static final byte NAT_CHECK_FAILED = 4;
  public static final byte NAT_CHECK_FAILED_AND_REPORTED = 5;
  public static final byte CRYPTO_NONE = 0;
  public static final byte CRYPTO_SUPPORTED = 1;
  public static final byte CRYPTO_REQUIRED = 2;
  
  public abstract long getUploaded();
  
  public abstract long getDownloaded();
  
  public abstract long getAmountLeft();
  
  public abstract String getIPRaw();
  
  public abstract byte[] getPeerID();
  
  public abstract byte getNATStatus();
  
  public abstract boolean isBiased();
  
  public abstract void setBiased(boolean paramBoolean);
  
  public abstract void setUserData(Object paramObject1, Object paramObject2);
  
  public abstract Object getUserData(Object paramObject);
  
  public abstract Map export();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/TRTrackerServerPeer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */