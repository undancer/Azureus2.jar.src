package org.gudy.azureus2.plugins.utils.search;

public abstract interface SearchObserver
{
  public static final int PR_MAX_RESULTS_WANTED = 1;
  public static final int PR_SUPPORTS_DUPLICATES = 2;
  
  public abstract void resultReceived(SearchInstance paramSearchInstance, SearchResult paramSearchResult);
  
  public abstract void complete();
  
  public abstract void cancelled();
  
  public abstract Object getProperty(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/search/SearchObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */