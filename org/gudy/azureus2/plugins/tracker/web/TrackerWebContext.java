package org.gudy.azureus2.plugins.tracker.web;

import java.net.InetAddress;
import java.net.URL;

public abstract interface TrackerWebContext
{
  public abstract String getName();
  
  public abstract URL[] getURLs();
  
  public abstract InetAddress getBindIP();
  
  public abstract void setEnableKeepAlive(boolean paramBoolean);
  
  public abstract void addPageGenerator(TrackerWebPageGenerator paramTrackerWebPageGenerator);
  
  public abstract void removePageGenerator(TrackerWebPageGenerator paramTrackerWebPageGenerator);
  
  public abstract TrackerWebPageGenerator[] getPageGenerators();
  
  public abstract void addAuthenticationListener(TrackerAuthenticationListener paramTrackerAuthenticationListener);
  
  public abstract void removeAuthenticationListener(TrackerAuthenticationListener paramTrackerAuthenticationListener);
  
  public abstract void destroy();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/tracker/web/TrackerWebContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */