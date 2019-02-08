package org.gudy.azureus2.plugins.utils.search;

public abstract interface Search
{
  public abstract SearchProviderResults[] getResults();
  
  public abstract boolean isComplete();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/search/Search.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */