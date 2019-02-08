package org.gudy.azureus2.plugins.tracker.web;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Map;
import org.gudy.azureus2.plugins.tracker.Tracker;

public abstract interface TrackerWebPageRequest
{
  public abstract Tracker getTracker();
  
  public abstract String getClientAddress();
  
  public abstract InetSocketAddress getClientAddress2();
  
  public abstract InetSocketAddress getLocalAddress();
  
  public abstract String getUser();
  
  public abstract String getURL();
  
  public abstract String getHeader();
  
  public abstract Map getHeaders();
  
  public abstract InputStream getInputStream();
  
  public abstract URL getAbsoluteURL();
  
  public abstract TrackerWebContext getContext();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/tracker/web/TrackerWebPageRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */