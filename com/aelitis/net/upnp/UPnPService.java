package com.aelitis.net.upnp;

import com.aelitis.net.upnp.services.UPnPSpecificService;
import java.net.URL;
import java.util.List;

public abstract interface UPnPService
{
  public abstract UPnPDevice getDevice();
  
  public abstract String getServiceType();
  
  public abstract List<URL> getControlURLs()
    throws UPnPException;
  
  public abstract void setPreferredControlURL(URL paramURL);
  
  public abstract boolean isConnectable();
  
  public abstract UPnPAction[] getActions()
    throws UPnPException;
  
  public abstract UPnPAction getAction(String paramString)
    throws UPnPException;
  
  public abstract UPnPStateVariable[] getStateVariables()
    throws UPnPException;
  
  public abstract UPnPStateVariable getStateVariable(String paramString)
    throws UPnPException;
  
  public abstract UPnPSpecificService getSpecificService();
  
  public abstract boolean getDirectInvocations();
  
  public abstract void setDirectInvocations(boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/UPnPService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */