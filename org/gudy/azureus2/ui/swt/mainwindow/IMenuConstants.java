package org.gudy.azureus2.ui.swt.mainwindow;

public abstract interface IMenuConstants
{
  public static final String KEY_ENABLEMENT = "key.enablement";
  public static final int FOR_AZ2 = 1;
  public static final int FOR_AZ3 = 2;
  public static final String KEY_MENU_ID = "key.menu.id";
  public static final String MENU_ID_MENU_BAR = "menu.bar";
  public static final String MENU_ID_FILE = "MainWindow.menu.file";
  public static final String MENU_ID_TRANSFERS = "MainWindow.menu.transfers";
  public static final String MENU_ID_VIEW = "MainWindow.menu.view";
  public static final String MENU_ID_TORRENT = "MainWindow.menu.torrent";
  public static final String MENU_ID_TOOLS = "MainWindow.menu.tools";
  public static final String MENU_ID_PLUGINS = "MainWindow.menu.view.plugins";
  public static final String MENU_ID_SPEED_LIMITS = "MainWindow.menu.speed_limits";
  public static final String MENU_ID_QUICK_VIEW = "MainWindow.menu.quick_view";
  public static final String MENU_ID_ADVANCED_TOOLS = "MainWindow.menu.advanced_tools";
  public static final String MENU_ID_WINDOW = "MainWindow.menu.window";
  public static final String MENU_ID_HELP = "MainWindow.menu.help";
  public static final String MENU_ID_CREATE = "MainWindow.menu.file.create";
  public static final String MENU_ID_OPEN = "MainWindow.menu.file.open";
  public static final String MENU_ID_LOG_VIEWS = "MainWindow.menu.view.plugins.logViews";
  public static final String MENU_ID_OPEN_TORRENT = "MainWindow.menu.file.open.torrent";
  public static final String MENU_ID_OPEN_URI = "MainWindow.menu.file.open.uri";
  public static final String MENU_ID_OPEN_TORRENT_FOR_TRACKING = "MainWindow.menu.file.open.torrentfortracking";
  public static final String MENU_ID_OPEN_VUZE_FILE = "MainWindow.menu.file.open.vuze";
  public static final String MENU_ID_SHARE = "MainWindow.menu.file.share";
  public static final String MENU_ID_SHARE_FILE = "MainWindow.menu.file.share.file";
  public static final String MENU_ID_SHARE_DIR = "MainWindow.menu.file.share.dir";
  public static final String MENU_ID_SHARE_DIR_CONTENT = "MainWindow.menu.file.share.dircontents";
  public static final String MENU_ID_SHARE_DIR_CONTENT_RECURSE = "MainWindow.menu.file.share.dircontentsrecursive";
  public static final String MENU_ID_IMPORT = "MainWindow.menu.file.import";
  public static final String MENU_ID_EXPORT = "MainWindow.menu.file.export";
  public static final String MENU_ID_WINDOW_CLOSE = "MainWindow.menu.file.closewindow";
  public static final String MENU_ID_CLOSE_TAB = "MainWindow.menu.file.closetab";
  public static final String MENU_ID_CLOSE_ALL_DETAIL = "MainWindow.menu.closealldetails";
  public static final String MENU_ID_CLOSE_ALL_DL_BARS = "MainWindow.menu.closealldownloadbars";
  public static final String MENU_ID_RESTART = "MainWindow.menu.file.restart";
  public static final String MENU_ID_EXIT = "MainWindow.menu.file.exit";
  public static final String MENU_ID_START_ALL_TRANSFERS = "MainWindow.menu.transfers.startalltransfers";
  public static final String MENU_ID_STOP_ALL_TRANSFERS = "MainWindow.menu.transfers.stopalltransfers";
  public static final String MENU_ID_PAUSE_TRANSFERS = "MainWindow.menu.transfers.pausetransfers";
  public static final String MENU_ID_PAUSE_TRANSFERS_FOR = "MainWindow.menu.transfers.pausetransfersfor";
  public static final String MENU_ID_RESUME_TRANSFERS = "MainWindow.menu.transfers.resumetransfers";
  public static final String MENU_ID_MY_TORRENTS = "MainWindow.menu.view.mytorrents";
  public static final String MENU_ID_DETAILED_LIST = "MainWindow.menu.view.detailedlist";
  public static final String MENU_ID_ALL_PEERS = "MainWindow.menu.view.allpeers";
  public static final String MENU_ID_CLIENT_STATS = "MainWindow.menu.view.clientstats";
  public static final String MENU_ID_DEVICEMANAGER = "MainWindow.menu.view.devicemanager";
  public static final String MENU_ID_SUBSCRIPTIONS = "subscriptions.view.title";
  public static final String MENU_ID_MY_TRACKERS = "MainWindow.menu.view.mytracker";
  public static final String MENU_ID_MY_SHARES = "MainWindow.menu.view.myshares";
  public static final String MENU_ID_TOOLBAR = "MainWindow.menu.view.iconbar";
  public static final String MENU_ID_TRANSFER_BAR = "MainWindow.menu.view.open_global_transfer_bar";
  public static final String MENU_ID_IP_FILTER = "MainWindow.menu.view.ipFilter";
  public static final String MENU_ID_CONSOLE = "MainWindow.menu.view.console";
  public static final String MENU_ID_STATS = "MainWindow.menu.view.stats";
  public static final String MENU_ID_NAT_TEST = "MainWindow.menu.tools.nattest";
  public static final String MENU_ID_NET_STATUS = "MainWindow.menu.tools.netstat";
  public static final String MENU_ID_SPEED_TEST = "MainWindow.menu.tools.speedtest";
  public static final String MENU_ID_CONFIGURE = "MainWindow.menu.file.configure";
  public static final String MENU_ID_OPTIONS = "MainWindow.menu.view.configuration";
  public static final String MENU_ID_PAIRING = "MainWindow.menu.pairing";
  public static final String MENU_ID_WINDOW_MINIMIZE = "MainWindow.menu.window.minimize";
  public static final String MENU_ID_WINDOW_ALL_TO_FRONT = "MainWindow.menu.window.alltofront";
  public static final String MENU_ID_WINDOW_ZOOM = "MainWindow.menu.window.zoom";
  public static final String MENU_ID_WINDOW_ZOOM_MAXIMIZE = "MainWindow.menu.window.zoom.maximize";
  public static final String MENU_ID_WINDOW_ZOOM_RESTORE = "MainWindow.menu.window.zoom.restore";
  public static final String MENU_ID_ABOUT = "MainWindow.menu.help.about";
  public static final String MENU_ID_HEALTH = "MyTorrentsView.menu.health";
  public static final String MENU_ID_WHATS_NEW = "MainWindow.menu.help.whatsnew";
  public static final String MENU_ID_RELEASE_NOTES = "MainWindow.menu.help.releasenotes";
  public static final String MENU_ID_PLUGINS_HELP = "MainWindow.menu.help.plugins";
  public static final String MENU_ID_DEBUG_HELP = "MainWindow.menu.help.debug";
  public static final String MENU_ID_UPDATE_CHECK = "MainWindow.menu.help.checkupdate";
  public static final String MENU_ID_BETA_PROG = "MainWindow.menu.beta";
  public static final String MENU_ID_BETA_PROG_BUG = "MainWindow.menu.report.beta.problem";
  public static final String MENU_ID_VOTE = "MainWindow.menu.vote";
  public static final String MENU_ID_PLUGINS_INSTALL = "MainWindow.menu.plugins.installPlugins";
  public static final String MENU_ID_PLUGINS_UNINSTALL = "MainWindow.menu.plugins.uninstallPlugins";
  public static final String MENU_ID_ADVANCED = "v3.MainWindow.tab.advanced";
  public static final String MENU_ID_SEARCH_BAR = "v3.MainWindow.menu.view.searchbar";
  public static final String MENU_ID_COMMUNITY = "MainWindow.menu.community";
  public static final String MENU_ID_COMMUNITY_BLOG = "MainWindow.menu.community.blog";
  public static final String MENU_ID_COMMUNITY_FORUMS = "MainWindow.menu.community.forums";
  public static final String MENU_ID_COMMUNITY_CHAT = "MainWindow.menu.community.chat";
  public static final String MENU_ID_COMMUNITY_WIKI = "MainWindow.menu.community.wiki";
  public static final String MENU_ID_HELP_SUPPORT = "MainWindow.menu.help.support";
  public static final String MENU_ID_DONATE = "MainWindow.menu.help.donate";
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/mainwindow/IMenuConstants.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */