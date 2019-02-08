package org.gudy.azureus2.plugins.network;

import java.net.InetSocketAddress;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.gudy.azureus2.plugins.messaging.MessageStreamDecoder;
import org.gudy.azureus2.plugins.messaging.MessageStreamEncoder;

public abstract interface ConnectionManager
{
  public static final int NAT_UNKNOWN = 0;
  public static final int NAT_OK = 1;
  public static final int NAT_PROBABLY_OK = 2;
  public static final int NAT_BAD = 3;
  
  public abstract Connection createConnection(InetSocketAddress paramInetSocketAddress, MessageStreamEncoder paramMessageStreamEncoder, MessageStreamDecoder paramMessageStreamDecoder);
  
  public abstract int getNATStatus();
  
  public abstract TransportCipher createTransportCipher(String paramString, int paramInt, SecretKeySpec paramSecretKeySpec, AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws TransportException;
  
  public abstract TransportFilter createTransportFilter(Connection paramConnection, TransportCipher paramTransportCipher1, TransportCipher paramTransportCipher2)
    throws TransportException;
  
  public abstract RateLimiter createRateLimiter(String paramString, int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/network/ConnectionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */