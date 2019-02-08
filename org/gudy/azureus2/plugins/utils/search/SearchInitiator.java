package org.gudy.azureus2.plugins.utils.search;

import java.util.Map;

public abstract interface SearchInitiator
{
  public static final String PR_SEARCH_TERM = "search_term";
  public static final String PR_MATURE = "mature";
  
  public abstract SearchProvider[] getProviders();
  
  public abstract Search createSearch(SearchProvider[] paramArrayOfSearchProvider, Map<String, String> paramMap, SearchListener paramSearchListener)
    throws SearchException;
  
  public abstract Search createSearch(String paramString1, String paramString2)
    throws SearchException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/search/SearchInitiator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */