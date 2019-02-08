package org.gudy.azureus2.plugins.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

public abstract interface PooledByteBuffer
{
  public abstract byte[] toByteArray();
  
  public abstract ByteBuffer toByteBuffer();
  
  public abstract Map toMap()
    throws IOException;
  
  public abstract void returnToPool();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/PooledByteBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */