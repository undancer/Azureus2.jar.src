package org.gudy.azureus2.core3.tracker.host;

import java.io.IOException;
import org.gudy.azureus2.core3.tracker.server.TRTrackerServerListener2.ExternalRequest;

public abstract interface TRHostListener2
{
  public abstract boolean handleExternalRequest(TRTrackerServerListener2.ExternalRequest paramExternalRequest)
    throws IOException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/TRHostListener2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */