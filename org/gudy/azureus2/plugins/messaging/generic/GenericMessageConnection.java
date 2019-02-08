package org.gudy.azureus2.plugins.messaging.generic;

import org.gudy.azureus2.plugins.messaging.MessageException;
import org.gudy.azureus2.plugins.network.RateLimiter;
import org.gudy.azureus2.plugins.utils.PooledByteBuffer;

public abstract interface GenericMessageConnection
{
  public static final int TT_NONE = 0;
  public static final int TT_TCP = 0;
  public static final int TT_UDP = 0;
  public static final int TT_INDIRECT = 0;
  
  public abstract GenericMessageEndpoint getEndpoint();
  
  public abstract void connect()
    throws MessageException;
  
  public abstract void send(PooledByteBuffer paramPooledByteBuffer)
    throws MessageException;
  
  public abstract void close()
    throws MessageException;
  
  public abstract int getMaximumMessageSize();
  
  public abstract String getType();
  
  public abstract int getTransportType();
  
  public abstract void addInboundRateLimiter(RateLimiter paramRateLimiter);
  
  public abstract void removeInboundRateLimiter(RateLimiter paramRateLimiter);
  
  public abstract void addOutboundRateLimiter(RateLimiter paramRateLimiter);
  
  public abstract void removeOutboundRateLimiter(RateLimiter paramRateLimiter);
  
  public abstract void addListener(GenericMessageConnectionListener paramGenericMessageConnectionListener);
  
  public abstract void removeListener(GenericMessageConnectionListener paramGenericMessageConnectionListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/messaging/generic/GenericMessageConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */