package com.aelitis.azureus.core.pairing;

public abstract interface PairingConnectionData
{
  public static final String ATTR_IP_V4 = "ip4";
  public static final String ATTR_IP_V6 = "ip6";
  public static final String ATTR_PORT = "port";
  public static final String ATTR_PORT_OVERRIDE = "port_or";
  public static final String ATTR_PROTOCOL = "protocol";
  public static final String ATTR_HOST = "host";
  public static final String ATTR_I2P = "I2P";
  public static final String ATTR_TOR = "Tor";
  
  public abstract void setAttribute(String paramString1, String paramString2);
  
  public abstract String getAttribute(String paramString);
  
  public abstract void sync();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/pairing/PairingConnectionData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */