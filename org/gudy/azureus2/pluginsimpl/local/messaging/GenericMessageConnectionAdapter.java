package org.gudy.azureus2.pluginsimpl.local.messaging;

import java.nio.ByteBuffer;
import org.gudy.azureus2.plugins.messaging.MessageException;
import org.gudy.azureus2.plugins.messaging.generic.GenericMessageEndpoint;
import org.gudy.azureus2.plugins.network.RateLimiter;
import org.gudy.azureus2.plugins.utils.PooledByteBuffer;

public abstract interface GenericMessageConnectionAdapter
{
  public abstract void setOwner(GenericMessageConnectionImpl paramGenericMessageConnectionImpl);
  
  public abstract GenericMessageEndpoint getEndpoint();
  
  public abstract int getMaximumMessageSize();
  
  public abstract String getType();
  
  public abstract int getTransportType();
  
  public abstract void connect(ByteBuffer paramByteBuffer, ConnectionListener paramConnectionListener);
  
  public abstract void accepted();
  
  public abstract void send(PooledByteBuffer paramPooledByteBuffer)
    throws MessageException;
  
  public abstract void addInboundRateLimiter(RateLimiter paramRateLimiter);
  
  public abstract void removeInboundRateLimiter(RateLimiter paramRateLimiter);
  
  public abstract void addOutboundRateLimiter(RateLimiter paramRateLimiter);
  
  public abstract void removeOutboundRateLimiter(RateLimiter paramRateLimiter);
  
  public abstract void close()
    throws MessageException;
  
  public static abstract interface ConnectionListener
  {
    public abstract void connectSuccess();
    
    public abstract void connectFailure(Throwable paramThrowable);
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/messaging/GenericMessageConnectionAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */