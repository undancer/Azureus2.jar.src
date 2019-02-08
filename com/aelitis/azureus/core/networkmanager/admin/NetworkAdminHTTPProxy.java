package com.aelitis.azureus.core.networkmanager.admin;

public abstract interface NetworkAdminHTTPProxy
{
  public abstract String getName();
  
  public abstract String getHTTPHost();
  
  public abstract String getHTTPPort();
  
  public abstract String getHTTPSHost();
  
  public abstract String getHTTPSPort();
  
  public abstract String getUser();
  
  public abstract String[] getNonProxyHosts();
  
  public abstract Details getDetails()
    throws NetworkAdminException;
  
  public abstract String getString();
  
  public static abstract interface Details
  {
    public abstract String getServerName();
    
    public abstract String getResponse();
    
    public abstract String getAuthenticationType();
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/NetworkAdminHTTPProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */