package org.gudy.azureus2.core3.tracker.server;

import java.net.URL;

public abstract interface TRTrackerServerAuthenticationListener
{
  public abstract boolean authenticate(String paramString1, URL paramURL, String paramString2, String paramString3);
  
  public abstract byte[] authenticate(URL paramURL, String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/TRTrackerServerAuthenticationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */