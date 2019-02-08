package org.gudy.azureus2.core3.tracker.server.impl.tcp.nonblocking;

import java.nio.channels.SocketChannel;
import org.gudy.azureus2.core3.tracker.server.impl.tcp.TRTrackerServerTCP;

public abstract interface TRNonBlockingServerProcessorFactory
{
  public abstract TRNonBlockingServerProcessor create(TRTrackerServerTCP paramTRTrackerServerTCP, SocketChannel paramSocketChannel);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/tcp/nonblocking/TRNonBlockingServerProcessorFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */