package org.gudy.azureus2.plugins.utils.search;

import java.util.Map;

public abstract interface SearchProvider
{
  public static final int PR_ID = 0;
  public static final int PR_NAME = 1;
  public static final int PR_ICON_URL = 2;
  public static final int PR_DOWNLOAD_LINK_LOCATOR = 3;
  public static final int PR_REFERER = 4;
  public static final int PR_SUPPORTS_RESULT_FIELDS = 5;
  public static final int PR_USE_ACCURACY_FOR_RANK = 6;
  public static final String SP_SEARCH_NAME = "t";
  public static final String SP_SEARCH_TERM = "s";
  public static final String SP_MATURE = "m";
  public static final String SP_NETWORKS = "n";
  public static final String SP_MIN_SEEDS = "z";
  public static final String SP_MIN_LEECHERS = "l";
  
  public abstract SearchInstance search(Map<String, Object> paramMap, SearchObserver paramSearchObserver)
    throws SearchException;
  
  public abstract Object getProperty(int paramInt);
  
  public abstract void setProperty(int paramInt, Object paramObject);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/search/SearchProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */