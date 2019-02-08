package org.gudy.azureus2.plugins.messaging.generic;

import org.gudy.azureus2.plugins.messaging.MessageException;
import org.gudy.azureus2.plugins.utils.PooledByteBuffer;

public abstract interface GenericMessageConnectionListener
{
  public abstract void connected(GenericMessageConnection paramGenericMessageConnection);
  
  public abstract void receive(GenericMessageConnection paramGenericMessageConnection, PooledByteBuffer paramPooledByteBuffer)
    throws MessageException;
  
  public abstract void failed(GenericMessageConnection paramGenericMessageConnection, Throwable paramThrowable)
    throws MessageException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/messaging/generic/GenericMessageConnectionListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */