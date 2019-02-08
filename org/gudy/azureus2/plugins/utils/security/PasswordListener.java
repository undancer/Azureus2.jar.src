package org.gudy.azureus2.plugins.utils.security;

import java.net.PasswordAuthentication;
import java.net.URL;

public abstract interface PasswordListener
{
  public abstract PasswordAuthentication getAuthentication(String paramString, URL paramURL);
  
  public abstract void setAuthenticationOutcome(String paramString, URL paramURL, boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/security/PasswordListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */