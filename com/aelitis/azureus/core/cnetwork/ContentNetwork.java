package com.aelitis.azureus.core.cnetwork;

import com.aelitis.azureus.core.vuzefile.VuzeFile;

public abstract interface ContentNetwork
{
  public static final long CONTENT_NETWORK_UNKNOWN = -1L;
  public static final long CONTENT_NETWORK_VUZE = 1L;
  public static final long CONTENT_NETWORK_RFN = 2L;
  public static final long CONTENT_NETWORK_VHDNL = 3L;
  public static final int SERVICE_XSEARCH = 2;
  public static final int SERVICE_RPC = 3;
  public static final int SERVICE_RELAY_RPC = 4;
  public static final int SERVICE_AUTH_RPC = 5;
  public static final int SERVICE_BIG_BROWSE = 6;
  public static final int SERVICE_PUBLISH = 7;
  public static final int SERVICE_WELCOME = 8;
  public static final int SERVICE_PUBLISH_NEW = 9;
  public static final int SERVICE_PUBLISH_ABOUT = 10;
  public static final int SERVICE_CONTENT_DETAILS = 11;
  public static final int SERVICE_COMMENT = 12;
  public static final int SERVICE_PROFILE = 13;
  public static final int SERVICE_TORRENT_DOWNLOAD = 14;
  public static final int SERVICE_SITE = 15;
  public static final int SERVICE_SUPPORT = 16;
  public static final int SERVICE_FAQ = 17;
  public static final int SERVICE_FAQ_TOPIC = 18;
  public static final int SERVICE_BLOG = 19;
  public static final int SERVICE_FORUMS = 20;
  public static final int SERVICE_WIKI = 21;
  public static final int SERVICE_LOGIN = 22;
  public static final int SERVICE_LOGOUT = 23;
  public static final int SERVICE_REGISTER = 24;
  public static final int SERVICE_MY_PROFILE = 25;
  public static final int SERVICE_MY_ACCOUNT = 26;
  public static final int SERVICE_SITE_RELATIVE = 27;
  public static final int SERVICE_ADD_FRIEND = 28;
  public static final int SERVICE_SUBSCRIPTION = 29;
  public static final int SERVICE_GET_ICON = 30;
  public static final int SERVICE_AUTHORIZE = 31;
  public static final int SERVICE_PREPLAYBACK = 32;
  public static final int SERVICE_POSTPLAYBACK = 33;
  public static final int SERVICE_SIDEBAR_CLOSE = 34;
  public static final int SERVICE_ABOUT = 35;
  public static final int SERVICE_IDENTIFY = 36;
  public static final int SERVICE_EXT_SITE_RELATIVE = 37;
  public static final int PROPERTY_SITE_HOST = 1;
  public static final int PROPERTY_REMOVEABLE = 2;
  public static final int PROPERTY_ORDER = 3;
  public static final String PP_AUTH_PAGE_SHOWN = "auth_shown";
  public static final String PP_IS_CUSTOMIZATION = "is_cust";
  public static final String PP_ACTIVE = "active";
  public static final String PP_SHOW_IN_MENU = "in_menu";
  public static final String PP_SOURCE_REF = "source_ref";
  
  public abstract long getID();
  
  public abstract String getName();
  
  public abstract Object getProperty(int paramInt);
  
  public abstract boolean isStartupNetwork();
  
  public abstract void setStartupNetwork(boolean paramBoolean);
  
  public abstract boolean isServiceSupported(int paramInt);
  
  public abstract String getServiceURL(int paramInt);
  
  public abstract String getServiceURL(int paramInt, Object[] paramArrayOfObject);
  
  public abstract String getXSearchService(String paramString, boolean paramBoolean);
  
  public abstract String getContentDetailsService(String paramString1, String paramString2);
  
  public abstract String getCommentService(String paramString);
  
  public abstract String getProfileService(String paramString1, String paramString2);
  
  public abstract String getTorrentDownloadService(String paramString1, String paramString2);
  
  public abstract String getFAQTopicService(String paramString);
  
  public abstract String getLoginService(String paramString);
  
  public abstract String getSiteRelativeURL(String paramString, boolean paramBoolean);
  
  public abstract String getExternalSiteRelativeURL(String paramString, boolean paramBoolean);
  
  public abstract String getAddFriendURL(String paramString);
  
  public abstract String getSubscriptionURL(String paramString);
  
  public abstract String appendURLSuffix(String paramString, boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract VuzeFile getVuzeFile();
  
  public abstract void setPersistentProperty(String paramString, Object paramObject);
  
  public abstract Object getPersistentProperty(String paramString);
  
  public abstract void addPersistentPropertyChangeListener(ContentNetworkPropertyChangeListener paramContentNetworkPropertyChangeListener);
  
  public abstract void removePersistentPropertyChangeListener(ContentNetworkPropertyChangeListener paramContentNetworkPropertyChangeListener);
  
  public abstract void setTransientProperty(Object paramObject1, Object paramObject2);
  
  public abstract Object getTransientProperty(Object paramObject);
  
  public abstract void remove();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/cnetwork/ContentNetwork.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */