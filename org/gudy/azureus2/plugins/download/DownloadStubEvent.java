package org.gudy.azureus2.plugins.download;

import java.util.List;

public abstract interface DownloadStubEvent
{
  public static final int DSE_STUB_ADDED = 1;
  public static final int DSE_STUB_REMOVED = 2;
  public static final int DSE_STUB_WILL_BE_ADDED = 3;
  public static final int DSE_STUB_WILL_BE_REMOVED = 4;
  
  public abstract int getEventType();
  
  public abstract List<DownloadStub> getDownloadStubs();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/DownloadStubEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */