package org.gudy.azureus2.plugins.utils.search;

public abstract interface SearchListener
{
  public abstract void receivedResults(SearchProviderResults[] paramArrayOfSearchProviderResults);
  
  public abstract void completed();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/search/SearchListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */