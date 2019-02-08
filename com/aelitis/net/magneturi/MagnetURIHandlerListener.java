package com.aelitis.net.magneturi;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Map;

public abstract interface MagnetURIHandlerListener
{
  public abstract byte[] badge();
  
  public abstract byte[] download(MagnetURIHandlerProgressListener paramMagnetURIHandlerProgressListener, byte[] paramArrayOfByte, String paramString, InetSocketAddress[] paramArrayOfInetSocketAddress, long paramLong)
    throws MagnetURIHandlerException;
  
  public abstract boolean download(URL paramURL)
    throws MagnetURIHandlerException;
  
  public abstract boolean set(String paramString, Map paramMap);
  
  public abstract int get(String paramString, Map paramMap);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/magneturi/MagnetURIHandlerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */