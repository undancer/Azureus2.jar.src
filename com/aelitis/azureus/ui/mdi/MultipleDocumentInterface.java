package com.aelitis.azureus.ui.mdi;

import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
import java.util.List;
import java.util.Map;

public abstract interface MultipleDocumentInterface
{
  public static final String SIDEBAR_POS_FIRST = "";
  public static final String SIDEBAR_HEADER_VUZE = "header.vuze";
  public static final String SIDEBAR_HEADER_TRANSFERS = "header.transfers";
  public static final String SIDEBAR_HEADER_DISCOVERY = "header.discovery";
  public static final String SIDEBAR_HEADER_DEVICES = "header.devices";
  public static final String SIDEBAR_HEADER_DVD = "header.dvd";
  public static final String SIDEBAR_HEADER_PLUGINS = "header.plugins";
  public static final String SIDEBAR_SECTION_PLUGINS = "Plugins";
  public static final String SIDEBAR_SECTION_ABOUTPLUGINS = "About.Plugins";
  public static final String SIDEBAR_SECTION_LIBRARY = "Library";
  public static final String SIDEBAR_SECTION_GAMES = "Games";
  public static final String SIDEBAR_SECTION_BETAPROGRAM = "BetaProgramme";
  public static final String SIDEBAR_SECTION_LIBRARY_DL = "LibraryDL";
  public static final String SIDEBAR_SECTION_LIBRARY_CD = "LibraryCD";
  public static final String SIDEBAR_SECTION_TAGS = "TagsOverview";
  public static final String SIDEBAR_SECTION_TAG_DISCOVERY = "TagDiscovery";
  public static final String SIDEBAR_SECTION_CHAT = "ChatOverview";
  public static final String SIDEBAR_SECTION_LIBRARY_UNOPENED = "LibraryUnopened";
  public static final String SIDEBAR_SECTION_TORRENT_DETAILS = "DMDetails";
  public static final String SIDEBAR_SECTION_WELCOME = "Welcome";
  public static final String SIDEBAR_SECTION_PLUS = "Plus";
  public static final String SIDEBAR_SECTION_SUBSCRIPTIONS = "Subscriptions";
  public static final String SIDEBAR_SECTION_DEVICES = "Devices";
  public static final String SIDEBAR_SECTION_BURN_INFO = "BurnInfo";
  public static final String SIDEBAR_SECTION_ACTIVITIES = "Activity";
  public static final String SIDEBAR_SECTION_SEARCH = "Search";
  public static final String SIDEBAR_SECTION_ALLPEERS = "AllPeersView";
  public static final String SIDEBAR_SECTION_TORRENT_OPTIONS = "TorrentOptionsView";
  public static final String SIDEBAR_SECTION_MY_SHARES = "MySharesView";
  public static final String SIDEBAR_SECTION_MY_TRACKER = "MyTrackerView";
  public static final String SIDEBAR_SECTION_CLIENT_STATS = "ClientStatsView";
  public static final String SIDEBAR_SECTION_LOGGER = "LoggerView";
  public static final String SIDEBAR_SECTION_CONFIG = "ConfigView";
  public static final String SIDEBAR_SECTION_ARCHIVED_DOWNLOADS = "ArchivedDownloads";
  public static final String SIDEBAR_SECTION_DOWNLOAD_HISTORY = "DownloadHistory";
  
  public abstract boolean showEntryByID(String paramString);
  
  public abstract boolean showEntryByID(String paramString, Object paramObject);
  
  public abstract MdiEntry createEntryFromSkinRef(String paramString1, String paramString2, String paramString3, String paramString4, ViewTitleInfo paramViewTitleInfo, Object paramObject, boolean paramBoolean, String paramString5);
  
  public abstract MdiEntry getCurrentEntry();
  
  public abstract MdiEntry getEntry(String paramString);
  
  public abstract void addListener(MdiListener paramMdiListener);
  
  public abstract void removeListener(MdiListener paramMdiListener);
  
  public abstract void addListener(MdiEntryLoadedListener paramMdiEntryLoadedListener);
  
  public abstract void removeListener(MdiEntryLoadedListener paramMdiEntryLoadedListener);
  
  public abstract boolean isVisible();
  
  public abstract void closeEntry(String paramString);
  
  public abstract MdiEntry[] getEntries();
  
  public abstract void registerEntry(String paramString, MdiEntryCreationListener paramMdiEntryCreationListener);
  
  public abstract void registerEntry(String paramString, MdiEntryCreationListener2 paramMdiEntryCreationListener2);
  
  public abstract void deregisterEntry(String paramString, MdiEntryCreationListener paramMdiEntryCreationListener);
  
  public abstract void deregisterEntry(String paramString, MdiEntryCreationListener2 paramMdiEntryCreationListener2);
  
  public abstract boolean entryExists(String paramString);
  
  public abstract void removeItem(MdiEntry paramMdiEntry);
  
  public abstract void setEntryAutoOpen(String paramString, Object paramObject);
  
  public abstract void removeEntryAutoOpen(String paramString);
  
  public abstract void showEntry(MdiEntry paramMdiEntry);
  
  public abstract void informAutoOpenSet(MdiEntry paramMdiEntry, Map<String, Object> paramMap);
  
  public abstract boolean loadEntryByID(String paramString, boolean paramBoolean);
  
  public abstract void setPreferredOrder(String[] paramArrayOfString);
  
  public abstract String[] getPreferredOrder();
  
  public abstract MdiEntry createHeader(String paramString1, String paramString2, String paramString3);
  
  public abstract List<MdiEntry> getChildrenOf(String paramString);
  
  public abstract boolean loadEntryByID(String paramString, boolean paramBoolean1, boolean paramBoolean2, Object paramObject);
  
  public abstract int getEntriesCount();
  
  public abstract boolean isDisposed();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/mdi/MultipleDocumentInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */