package org.gudy.azureus2.plugins.network;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface Transport
{
  public abstract long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract void setFilter(TransportFilter paramTransportFilter)
    throws IOException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/network/Transport.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */