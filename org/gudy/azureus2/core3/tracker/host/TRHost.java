package org.gudy.azureus2.core3.tracker.host;

import java.net.InetAddress;
import org.gudy.azureus2.core3.torrent.TOTorrent;

public abstract interface TRHost
{
  public static final int DEFAULT_MIN_RETRY_DELAY = 120;
  public static final int DEFAULT_MAX_RETRY_DELAY = 3600;
  public static final int DEFAULT_INC_BY = 60;
  public static final int DEFAULT_INC_PER = 10;
  public static final int DEFAULT_SCRAPE_RETRY_PERCENTAGE = 200;
  public static final int DEFAULT_SCRAPE_CACHE_PERIOD = 5000;
  public static final int DEFAULT_ANNOUNCE_CACHE_PERIOD = 500;
  public static final int DEFAULT_ANNOUNCE_CACHE_PEER_THRESHOLD = 500;
  public static final int DEFAULT_PORT = 6969;
  public static final int DEFAULT_PORT_SSL = 7000;
  
  public abstract void initialise(TRHostTorrentFinder paramTRHostTorrentFinder);
  
  public abstract String getName();
  
  public abstract InetAddress getBindIP();
  
  public abstract TRHostTorrent hostTorrent(TOTorrent paramTOTorrent, boolean paramBoolean1, boolean paramBoolean2)
    throws TRHostException;
  
  public abstract TRHostTorrent publishTorrent(TOTorrent paramTOTorrent)
    throws TRHostException;
  
  public abstract TRHostTorrent[] getTorrents();
  
  public abstract TRHostTorrent getHostTorrent(TOTorrent paramTOTorrent);
  
  public abstract void addListener(TRHostListener paramTRHostListener);
  
  public abstract void removeListener(TRHostListener paramTRHostListener);
  
  public abstract void addListener2(TRHostListener2 paramTRHostListener2);
  
  public abstract void removeListener2(TRHostListener2 paramTRHostListener2);
  
  public abstract void addAuthenticationListener(TRHostAuthenticationListener paramTRHostAuthenticationListener);
  
  public abstract void removeAuthenticationListener(TRHostAuthenticationListener paramTRHostAuthenticationListener);
  
  public abstract void close();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/TRHost.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */