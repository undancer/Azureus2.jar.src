package org.gudy.azureus2.plugins.utils.search;

public abstract interface SearchProviderResults
{
  public abstract SearchProvider getProvider();
  
  public abstract SearchResult[] getResults();
  
  public abstract boolean isComplete();
  
  public abstract SearchException getError();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/search/SearchProviderResults.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */