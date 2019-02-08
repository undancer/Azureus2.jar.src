package com.aelitis.azureus.core.networkmanager;

import com.aelitis.azureus.core.peermanager.messaging.Message;
import org.gudy.azureus2.core3.util.DirectByteBuffer;

public abstract interface RawMessage
  extends Message
{
  public static final int PRIORITY_LOW = 0;
  public static final int PRIORITY_NORMAL = 1;
  public static final int PRIORITY_HIGH = 2;
  
  public abstract DirectByteBuffer[] getRawData();
  
  public abstract int getPriority();
  
  public abstract boolean isNoDelay();
  
  public abstract void setNoDelay();
  
  public abstract Message[] messagesToRemove();
  
  public abstract Message getBaseMessage();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/RawMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */