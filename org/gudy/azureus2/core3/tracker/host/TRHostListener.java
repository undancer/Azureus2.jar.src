package org.gudy.azureus2.core3.tracker.host;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import org.gudy.azureus2.core3.util.AsyncController;

public abstract interface TRHostListener
{
  public abstract void torrentAdded(TRHostTorrent paramTRHostTorrent);
  
  public abstract void torrentChanged(TRHostTorrent paramTRHostTorrent);
  
  public abstract void torrentRemoved(TRHostTorrent paramTRHostTorrent);
  
  public abstract boolean handleExternalRequest(InetSocketAddress paramInetSocketAddress, String paramString1, String paramString2, URL paramURL, String paramString3, InputStream paramInputStream, OutputStream paramOutputStream, AsyncController paramAsyncController)
    throws IOException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/TRHostListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */