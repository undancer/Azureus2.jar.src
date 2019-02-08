package com.aelitis.azureus.core.proxy;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Map;

public abstract interface AEProxyAddressMapper
{
  public static final String MAP_PROPERTY_DISABLE_AZ_MESSAGING = "AEProxyAddressMapper.disable.az.msg";
  public static final String MAP_PROPERTY_PROTOCOL_QUALIFIER = "AEProxyAddressMapper.prot.qual";
  
  public abstract String internalise(String paramString);
  
  public abstract String externalise(String paramString);
  
  public abstract URL internalise(URL paramURL);
  
  public abstract URL externalise(URL paramURL);
  
  public abstract PortMapping registerPortMapping(int paramInt, String paramString);
  
  public abstract PortMapping registerPortMapping(int paramInt, String paramString, Map<String, Object> paramMap);
  
  public abstract AppliedPortMapping applyPortMapping(InetAddress paramInetAddress, int paramInt);
  
  public static abstract interface AppliedPortMapping
  {
    public abstract InetSocketAddress getAddress();
    
    public abstract Map<String, Object> getProperties();
  }
  
  public static abstract interface PortMapping
  {
    public abstract void unregister();
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/AEProxyAddressMapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */