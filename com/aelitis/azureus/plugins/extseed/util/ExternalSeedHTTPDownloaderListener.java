package com.aelitis.azureus.plugins.extseed.util;

import com.aelitis.azureus.plugins.extseed.ExternalSeedException;

public abstract interface ExternalSeedHTTPDownloaderListener
{
  public abstract byte[] getBuffer()
    throws ExternalSeedException;
  
  public abstract void setBufferPosition(int paramInt);
  
  public abstract int getBufferPosition();
  
  public abstract int getBufferLength();
  
  public abstract int getPermittedBytes()
    throws ExternalSeedException;
  
  public abstract int getPermittedTime();
  
  public abstract void reportBytesRead(int paramInt);
  
  public abstract boolean isCancelled();
  
  public abstract void done();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/extseed/util/ExternalSeedHTTPDownloaderListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */