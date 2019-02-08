package com.aelitis.net.upnpms;

import java.util.List;

public abstract interface UPNPMSContainer
  extends UPNPMSNode
{
  public abstract List<UPNPMSNode> getChildren()
    throws UPnPMSException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnpms/UPNPMSContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */