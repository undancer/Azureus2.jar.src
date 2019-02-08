package com.aelitis.azureus.core.pairing;

import java.io.IOException;
import java.net.InetAddress;

public abstract interface PairedServiceRequestHandler
{
  public abstract byte[] handleRequest(InetAddress paramInetAddress, String paramString, byte[] paramArrayOfByte)
    throws IOException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/pairing/PairedServiceRequestHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */