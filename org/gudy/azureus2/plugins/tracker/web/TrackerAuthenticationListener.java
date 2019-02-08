package org.gudy.azureus2.plugins.tracker.web;

import java.net.URL;

public abstract interface TrackerAuthenticationListener
{
  public abstract boolean authenticate(URL paramURL, String paramString1, String paramString2);
  
  public abstract byte[] authenticate(URL paramURL, String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/tracker/web/TrackerAuthenticationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */