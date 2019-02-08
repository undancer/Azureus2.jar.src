package org.gudy.azureus2.plugins.utils.search;

public abstract interface SearchResult
{
  public static final int PR_NAME = 1;
  public static final int PR_PUB_DATE = 2;
  public static final int PR_SIZE = 3;
  public static final int PR_LEECHER_COUNT = 4;
  public static final int PR_SEED_COUNT = 5;
  public static final int PR_SUPER_SEED_COUNT = 6;
  public static final int PR_CATEGORY = 7;
  public static final int PR_COMMENTS = 8;
  public static final int PR_VOTES = 9;
  public static final int PR_CONTENT_TYPE = 10;
  public static final int PR_DETAILS_LINK = 11;
  public static final int PR_DOWNLOAD_LINK = 12;
  public static final int PR_PLAY_LINK = 13;
  public static final int PR_PRIVATE = 14;
  public static final int PR_DRM_KEY = 15;
  public static final int PR_DOWNLOAD_BUTTON_LINK = 16;
  public static final int PR_RANK = 17;
  public static final int PR_ACCURACY = 18;
  public static final int PR_VOTES_DOWN = 19;
  public static final int PR_UID = 20;
  public static final int PR_HASH = 21;
  public static final int PR_VERSION = 22;
  public static final int PR_TORRENT_LINK = 23;
  
  public abstract Object getProperty(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/search/SearchResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */