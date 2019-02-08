package com.aelitis.azureus.core.peermanager.messaging.azureus;

import org.gudy.azureus2.core3.util.DirectByteBuffer;

public abstract interface AZUTMetaData
{
  public static final int MSG_TYPE_REQUEST = 0;
  public static final int MSG_TYPE_DATA = 1;
  public static final int MSG_TYPE_REJECT = 2;
  
  public abstract int getMessageType();
  
  public abstract int getPiece();
  
  public abstract DirectByteBuffer getMetadata();
  
  public abstract void setMetadata(DirectByteBuffer paramDirectByteBuffer);
  
  public abstract void destroy();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/azureus/AZUTMetaData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */