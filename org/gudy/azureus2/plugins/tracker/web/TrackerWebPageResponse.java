package org.gudy.azureus2.plugins.tracker.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.gudy.azureus2.plugins.tracker.TrackerTorrent;

public abstract interface TrackerWebPageResponse
{
  public abstract OutputStream getOutputStream();
  
  public abstract void setReplyStatus(int paramInt);
  
  public abstract void setContentType(String paramString);
  
  public abstract void setLastModified(long paramLong);
  
  public abstract void setExpires(long paramLong);
  
  public abstract void setHeader(String paramString1, String paramString2);
  
  public abstract void setGZIP(boolean paramBoolean);
  
  public abstract boolean useFile(String paramString1, String paramString2)
    throws IOException;
  
  public abstract void useStream(String paramString, InputStream paramInputStream)
    throws IOException;
  
  public abstract void writeTorrent(TrackerTorrent paramTrackerTorrent)
    throws IOException;
  
  public abstract void setAsynchronous(boolean paramBoolean)
    throws IOException;
  
  public abstract boolean getAsynchronous();
  
  public abstract OutputStream getRawOutputStream()
    throws IOException;
  
  public abstract boolean isActive();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/tracker/web/TrackerWebPageResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */