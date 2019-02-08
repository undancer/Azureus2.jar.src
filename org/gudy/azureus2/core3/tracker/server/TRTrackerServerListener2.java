package org.gudy.azureus2.core3.tracker.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import org.gudy.azureus2.core3.util.AsyncController;

public abstract interface TRTrackerServerListener2
{
  public abstract boolean handleExternalRequest(ExternalRequest paramExternalRequest)
    throws IOException;
  
  public static abstract interface ExternalRequest
  {
    public abstract InetSocketAddress getClientAddress();
    
    public abstract InetSocketAddress getLocalAddress();
    
    public abstract String getUser();
    
    public abstract String getURL();
    
    public abstract URL getAbsoluteURL();
    
    public abstract String getHeader();
    
    public abstract InputStream getInputStream();
    
    public abstract OutputStream getOutputStream();
    
    public abstract boolean isActive();
    
    public abstract AsyncController getAsyncController();
    
    public abstract boolean canKeepAlive();
    
    public abstract void setKeepAlive(boolean paramBoolean);
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/TRTrackerServerListener2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */