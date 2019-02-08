package org.gudy.azureus2.core3.tracker.client;

import org.gudy.azureus2.plugins.download.DownloadAnnounceResultPeer;

public abstract interface TRTrackerAnnouncerResponsePeer
  extends DownloadAnnounceResultPeer
{
  public abstract int getHTTPPort();
  
  public abstract byte getAZVersion();
  
  public abstract int getUploadSpeed();
  
  public abstract int compareTo(TRTrackerAnnouncerResponsePeer paramTRTrackerAnnouncerResponsePeer);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/TRTrackerAnnouncerResponsePeer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */