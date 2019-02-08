package org.gudy.azureus2.core3.html;

import java.net.URL;

public abstract interface HTMLPage
  extends HTMLChunk
{
  public abstract URL getMetaRefreshURL();
  
  public abstract URL getMetaRefreshURL(URL paramURL);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/html/HTMLPage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */