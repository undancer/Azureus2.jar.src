package org.gudy.azureus2.core3.security;

import java.net.PasswordAuthentication;
import java.net.URL;

public abstract interface SEPasswordListener
{
  public abstract PasswordAuthentication getAuthentication(String paramString, URL paramURL);
  
  public abstract void setAuthenticationOutcome(String paramString, URL paramURL, boolean paramBoolean);
  
  public abstract void clearPasswords();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/security/SEPasswordListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */