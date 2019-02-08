package org.gudy.azureus2.plugins.tracker.web;

import java.io.IOException;

public abstract interface TrackerWebPageGenerator
{
  public abstract boolean generate(TrackerWebPageRequest paramTrackerWebPageRequest, TrackerWebPageResponse paramTrackerWebPageResponse)
    throws IOException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/tracker/web/TrackerWebPageGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */