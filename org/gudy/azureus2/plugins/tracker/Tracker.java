package org.gudy.azureus2.plugins.tracker;

import java.net.InetAddress;
import java.util.Map;
import org.gudy.azureus2.plugins.torrent.Torrent;
import org.gudy.azureus2.plugins.tracker.web.TrackerWebContext;

public abstract interface Tracker
  extends TrackerWebContext
{
  public static final int PR_HTTP = 1;
  public static final int PR_HTTPS = 2;
  public static final String PR_NON_BLOCKING = "nonblocking";
  
  public abstract TrackerTorrent host(Torrent paramTorrent, boolean paramBoolean)
    throws TrackerException;
  
  public abstract TrackerTorrent host(Torrent paramTorrent, boolean paramBoolean1, boolean paramBoolean2)
    throws TrackerException;
  
  public abstract TrackerTorrent publish(Torrent paramTorrent)
    throws TrackerException;
  
  public abstract TrackerTorrent getTorrent(Torrent paramTorrent);
  
  public abstract TrackerTorrent[] getTorrents();
  
  public abstract TrackerWebContext createWebContext(int paramInt1, int paramInt2)
    throws TrackerException;
  
  public abstract TrackerWebContext createWebContext(String paramString, int paramInt1, int paramInt2)
    throws TrackerException;
  
  public abstract TrackerWebContext createWebContext(String paramString, int paramInt1, int paramInt2, InetAddress paramInetAddress)
    throws TrackerException;
  
  public abstract TrackerWebContext createWebContext(String paramString, int paramInt1, int paramInt2, InetAddress paramInetAddress, Map<String, Object> paramMap)
    throws TrackerException;
  
  public abstract void addListener(TrackerListener paramTrackerListener);
  
  public abstract void removeListener(TrackerListener paramTrackerListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/tracker/Tracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */