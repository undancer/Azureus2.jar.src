package org.gudy.azureus2.core3.tracker.host;

import java.net.URL;

public abstract interface TRHostAuthenticationListener
{
  public abstract boolean authenticate(String paramString1, URL paramURL, String paramString2, String paramString3);
  
  public abstract byte[] authenticate(URL paramURL, String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/TRHostAuthenticationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */