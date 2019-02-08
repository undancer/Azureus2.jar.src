package org.gudy.azureus2.core3.tracker.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import org.gudy.azureus2.core3.util.AsyncController;

public abstract interface TRTrackerServerListener
{
  public abstract boolean handleExternalRequest(InetSocketAddress paramInetSocketAddress, String paramString1, String paramString2, URL paramURL, String paramString3, InputStream paramInputStream, OutputStream paramOutputStream, AsyncController paramAsyncController)
    throws IOException;
  
  public abstract boolean permitted(String paramString, byte[] paramArrayOfByte, boolean paramBoolean);
  
  public abstract boolean denied(byte[] paramArrayOfByte, boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/TRTrackerServerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */