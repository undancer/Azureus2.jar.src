/*      */ package com.aelitis.azureus.plugins.magnet;
/*      */ 
/*      */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*      */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagManager;
/*      */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.util.FeatureAvailability;
/*      */ import com.aelitis.net.magneturi.MagnetURIHandler;
/*      */ import com.aelitis.net.magneturi.MagnetURIHandlerException;
/*      */ import com.aelitis.net.magneturi.MagnetURIHandlerListener;
/*      */ import com.aelitis.net.magneturi.MagnetURIHandlerProgressListener;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.InputStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.NoRouteToHostException;
/*      */ import java.net.Proxy;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.ByteArrayHashMap;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.plugins.Plugin;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginListener;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseContact;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseEvent;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseListener;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseProgressListener;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseValue;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadException;
/*      */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareException;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareItem;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareResourceDir;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareResourceFile;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.UIManagerListener;
/*      */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.IntParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*      */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableManager;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*      */ import org.gudy.azureus2.plugins.utils.LocaleUtilities;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderAdapter;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class MagnetPlugin
/*      */   implements Plugin
/*      */ {
/*      */   public static final int FL_NONE = 0;
/*      */   public static final int FL_DISABLE_MD_LOOKUP = 1;
/*      */   private static final String SECONDARY_LOOKUP = "http://magnet.vuze.com/";
/*      */   private static final int SECONDARY_LOOKUP_DELAY = 20000;
/*      */   private static final int SECONDARY_LOOKUP_MAX_TIME = 120000;
/*      */   private static final int MD_LOOKUP_DELAY_SECS_DEFAULT = 20;
/*      */   private static final String PLUGIN_NAME = "Magnet URI Handler";
/*      */   private static final String PLUGIN_CONFIGSECTION_ID = "plugins.magnetplugin";
/*      */   private PluginInterface plugin_interface;
/*      */   private CopyOnWriteList listeners;
/*      */   private boolean first_download;
/*      */   private static final int PLUGIN_DOWNLOAD_TIMEOUT_SECS_DEFAULT = 600;
/*      */   private BooleanParameter secondary_lookup;
/*      */   private BooleanParameter md_lookup;
/*      */   private IntParameter md_lookup_delay;
/*      */   private IntParameter timeout_param;
/*      */   private Map<String, BooleanParameter> net_params;
/*      */   
/*      */   public MagnetPlugin()
/*      */   {
/*  124 */     this.listeners = new CopyOnWriteList();
/*      */     
/*  126 */     this.first_download = true;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  135 */     this.net_params = new HashMap();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void load(PluginInterface plugin_interface)
/*      */   {
/*  142 */     plugin_interface.getPluginProperties().setProperty("plugin.version", "1.0");
/*  143 */     plugin_interface.getPluginProperties().setProperty("plugin.name", "Magnet URI Handler");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void initialize(PluginInterface _plugin_interface)
/*      */   {
/*  150 */     this.plugin_interface = _plugin_interface;
/*      */     
/*  152 */     MagnetURIHandler uri_handler = MagnetURIHandler.getSingleton();
/*      */     
/*  154 */     BasicPluginConfigModel config = this.plugin_interface.getUIManager().createBasicPluginConfigModel("plugins", "plugins.magnetplugin");
/*      */     
/*      */ 
/*      */ 
/*  158 */     config.addInfoParameter2("MagnetPlugin.current.port", String.valueOf(uri_handler.getPort()));
/*      */     
/*  160 */     this.secondary_lookup = config.addBooleanParameter2("MagnetPlugin.use.lookup.service", "MagnetPlugin.use.lookup.service", true);
/*  161 */     this.md_lookup = config.addBooleanParameter2("MagnetPlugin.use.md.download", "MagnetPlugin.use.md.download", true);
/*  162 */     this.md_lookup_delay = config.addIntParameter2("MagnetPlugin.use.md.download.delay", "MagnetPlugin.use.md.download.delay", 20);
/*      */     
/*  164 */     this.md_lookup.addEnabledOnSelection(this.md_lookup_delay);
/*      */     
/*  166 */     this.timeout_param = config.addIntParameter2("MagnetPlugin.timeout.secs", "MagnetPlugin.timeout.secs", 600);
/*      */     
/*  168 */     Parameter[] nps = new Parameter[AENetworkClassifier.AT_NETWORKS.length];
/*      */     
/*  170 */     for (int i = 0; i < nps.length; i++)
/*      */     {
/*  172 */       String nn = AENetworkClassifier.AT_NETWORKS[i];
/*      */       
/*  174 */       String config_name = "Network Selection Default." + nn;
/*      */       
/*  176 */       String msg_text = "ConfigView.section.connection.networks." + nn;
/*      */       
/*  178 */       final BooleanParameter param = config.addBooleanParameter2(config_name, msg_text, COConfigurationManager.getBooleanParameter(config_name));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  184 */       COConfigurationManager.addParameterListener(config_name, new ParameterListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void parameterChanged(String name)
/*      */         {
/*      */ 
/*      */ 
/*  192 */           param.setDefaultValue(COConfigurationManager.getBooleanParameter(name));
/*      */         }
/*      */         
/*  195 */       });
/*  196 */       nps[i] = param;
/*      */       
/*  198 */       this.net_params.put(nn, param);
/*      */     }
/*      */     
/*  201 */     config.createGroup("label.default.nets", nps);
/*      */     
/*  203 */     MenuItemListener listener = new MenuItemListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void selected(MenuItem _menu, Object _target)
/*      */       {
/*      */ 
/*      */ 
/*  211 */         TableRow[] rows = (TableRow[])_target;
/*      */         
/*  213 */         String cb_all_data = "";
/*      */         
/*  215 */         for (TableRow row : rows)
/*      */         {
/*      */ 
/*  218 */           Object ds = row.getDataSource();
/*      */           
/*  220 */           Download download = null;
/*      */           String name;
/*  222 */           Torrent torrent; String name; if ((ds instanceof ShareResourceFile)) {
/*      */             try {
/*  224 */               torrent = ((ShareResourceFile)ds).getItem().getTorrent();
/*      */             } catch (ShareException e) { Torrent torrent;
/*  226 */               continue;
/*      */             }
/*  228 */             name = ((ShareResourceFile)ds).getName(); } else { String name;
/*  229 */             if ((ds instanceof ShareResourceDir)) {
/*      */               try {
/*  231 */                 torrent = ((ShareResourceDir)ds).getItem().getTorrent();
/*      */               } catch (ShareException e) { Torrent torrent;
/*  233 */                 continue;
/*      */               }
/*  235 */               name = ((ShareResourceDir)ds).getName();
/*  236 */             } else { if (!(ds instanceof Download)) continue;
/*  237 */               download = (Download)ds;
/*  238 */               torrent = download.getTorrent();
/*  239 */               name = download.getName();
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  245 */           String cb_data = download == null ? UrlUtils.getMagnetURI(name, torrent) : UrlUtils.getMagnetURI(download);
/*      */           
/*  247 */           if (download != null)
/*      */           {
/*  249 */             List<Tag> tags = TagManagerFactory.getTagManager().getTagsForTaggable(3, PluginCoreUtils.unwrap(download));
/*      */             
/*  251 */             for (Tag tag : tags)
/*      */             {
/*  253 */               if (tag.isPublic())
/*      */               {
/*  255 */                 cb_data = cb_data + "&tag=" + UrlUtils.encode(tag.getTagName(true));
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  306 */           cb_all_data = cb_all_data + (cb_all_data.length() == 0 ? "" : "\n") + cb_data;
/*      */         }
/*      */         try
/*      */         {
/*  310 */           MagnetPlugin.this.plugin_interface.getUIManager().copyToClipBoard(cb_all_data);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  314 */           e.printStackTrace();
/*      */         }
/*      */         
/*      */       }
/*  318 */     };
/*  319 */     final TableContextMenuItem menu1 = this.plugin_interface.getUIManager().getTableManager().addContextMenuItem("MyTorrents", "MagnetPlugin.contextmenu.exporturi");
/*  320 */     final TableContextMenuItem menu2 = this.plugin_interface.getUIManager().getTableManager().addContextMenuItem("MySeeders", "MagnetPlugin.contextmenu.exporturi");
/*  321 */     final TableContextMenuItem menu3 = this.plugin_interface.getUIManager().getTableManager().addContextMenuItem("MyShares", "MagnetPlugin.contextmenu.exporturi");
/*      */     
/*  323 */     menu1.addMultiListener(listener);
/*  324 */     menu2.addMultiListener(listener);
/*  325 */     menu3.addMultiListener(listener);
/*      */     
/*  327 */     uri_handler.addListener(new MagnetURIHandlerListener()
/*      */     {
/*      */ 
/*      */       public byte[] badge()
/*      */       {
/*      */ 
/*  333 */         InputStream is = getClass().getClassLoader().getResourceAsStream("com/aelitis/azureus/plugins/magnet/Magnet.gif");
/*      */         
/*  335 */         if (is == null)
/*      */         {
/*  337 */           return null;
/*      */         }
/*      */         try
/*      */         {
/*  341 */           ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*      */           try
/*      */           {
/*  344 */             byte[] buffer = new byte[' '];
/*      */             
/*      */             for (;;)
/*      */             {
/*  348 */               int len = is.read(buffer);
/*      */               
/*  350 */               if (len <= 0) {
/*      */                 break;
/*      */               }
/*      */               
/*      */ 
/*  355 */               baos.write(buffer, 0, len);
/*      */             }
/*      */           }
/*      */           finally {
/*  359 */             is.close();
/*      */           }
/*      */           
/*  362 */           return baos.toByteArray();
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  366 */           Debug.printStackTrace(e);
/*      */         }
/*  368 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public byte[] download(final MagnetURIHandlerProgressListener muh_listener, byte[] hash, String args, InetSocketAddress[] sources, long timeout)
/*      */         throws MagnetURIHandlerException
/*      */       {
/*      */         try
/*      */         {
/*  385 */           Download dl = MagnetPlugin.this.plugin_interface.getDownloadManager().getDownload(hash);
/*      */           
/*  387 */           if (dl != null)
/*      */           {
/*  389 */             Torrent torrent = dl.getTorrent();
/*      */             
/*  391 */             if (torrent != null)
/*      */             {
/*  393 */               byte[] torrent_data = torrent.writeToBEncodedData();
/*      */               
/*  395 */               return MagnetPlugin.this.addTrackersAndWebSeedsEtc(torrent_data, args, new HashSet());
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  402 */           Debug.printStackTrace(e);
/*      */         }
/*      */         
/*  405 */         MagnetPlugin.this.download(muh_listener == null ? null : new MagnetPluginProgressListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void reportSize(long size)
/*      */           {
/*      */ 
/*  412 */             muh_listener.reportSize(size);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public void reportActivity(String str)
/*      */           {
/*  419 */             muh_listener.reportActivity(str);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public void reportCompleteness(int percent)
/*      */           {
/*  426 */             muh_listener.reportCompleteness(percent);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void reportContributor(InetSocketAddress address) {}
/*      */           
/*      */ 
/*      */ 
/*      */           public boolean cancelled()
/*      */           {
/*  438 */             return muh_listener.cancelled();
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  444 */           public boolean verbose() { return muh_listener.verbose(); } }, hash, args, sources, timeout, 0);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean download(URL url)
/*      */         throws MagnetURIHandlerException
/*      */       {
/*      */         try
/*      */         {
/*  462 */           MagnetPlugin.this.plugin_interface.getDownloadManager().addDownload(url, false);
/*      */           
/*  464 */           return true;
/*      */         }
/*      */         catch (DownloadException e)
/*      */         {
/*  468 */           throw new MagnetURIHandlerException("Operation failed", e);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean set(String name, Map values)
/*      */       {
/*  477 */         List l = MagnetPlugin.this.listeners.getList();
/*      */         
/*  479 */         for (int i = 0; i < l.size(); i++)
/*      */         {
/*  481 */           if (((MagnetPluginListener)l.get(i)).set(name, values))
/*      */           {
/*  483 */             return true;
/*      */           }
/*      */         }
/*      */         
/*  487 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public int get(String name, Map values)
/*      */       {
/*  495 */         List l = MagnetPlugin.this.listeners.getList();
/*      */         
/*  497 */         for (int i = 0; i < l.size(); i++)
/*      */         {
/*  499 */           int res = ((MagnetPluginListener)l.get(i)).get(name, values);
/*      */           
/*  501 */           if (res != Integer.MIN_VALUE)
/*      */           {
/*  503 */             return res;
/*      */           }
/*      */         }
/*      */         
/*  507 */         return Integer.MIN_VALUE;
/*      */       }
/*      */       
/*  510 */     });
/*  511 */     this.plugin_interface.addListener(new PluginListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void initializationComplete()
/*      */       {
/*      */ 
/*      */ 
/*  520 */         AEThread2 t = new AEThread2("MagnetPlugin:init", true)
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/*      */ 
/*  526 */             MagnetPlugin.this.plugin_interface.getDistributedDatabase();
/*      */           }
/*      */           
/*  529 */         };
/*  530 */         t.start();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void closedownInitiated() {}
/*      */       
/*      */ 
/*      */       public void closedownComplete() {}
/*  539 */     });
/*  540 */     this.plugin_interface.getUIManager().addUIListener(new UIManagerListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void UIAttached(UIInstance instance)
/*      */       {
/*      */ 
/*  547 */         if (instance.getUIType() == 1) {
/*      */           try
/*      */           {
/*  550 */             Class.forName("com.aelitis.azureus.plugins.magnet.swt.MagnetPluginUISWT").getConstructor(new Class[] { UIInstance.class, TableContextMenuItem[].class }).newInstance(new Object[] { instance, { menu1, menu2, menu3 } });
/*      */ 
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*      */ 
/*  556 */             e.printStackTrace();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void UIDetached(UIInstance instance) {}
/*  568 */     });
/*  569 */     final List<Download> to_delete = new ArrayList();
/*      */     
/*  571 */     Download[] downloads = this.plugin_interface.getDownloadManager().getDownloads();
/*      */     
/*  573 */     for (Download download : downloads)
/*      */     {
/*  575 */       if (download.getFlag(512L))
/*      */       {
/*  577 */         to_delete.add(download);
/*      */       }
/*      */     }
/*      */     
/*  581 */     if (to_delete.size() > 0)
/*      */     {
/*  583 */       AEThread2 t = new AEThread2("MagnetPlugin:delmds", true)
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/*  589 */           for (Download download : to_delete)
/*      */           {
/*      */             try {
/*  592 */               download.stop();
/*      */             }
/*      */             catch (Throwable e) {}
/*      */             
/*      */             try
/*      */             {
/*  598 */               download.remove(true, true);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  602 */               Debug.out(e);
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*  607 */       };
/*  608 */       t.start();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isNetworkEnabled(String net)
/*      */   {
/*  616 */     return ((BooleanParameter)this.net_params.get(net)).getValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public URL getMagnetURL(Download d)
/*      */   {
/*  623 */     Torrent torrent = d.getTorrent();
/*      */     
/*  625 */     if (torrent == null)
/*      */     {
/*  627 */       return null;
/*      */     }
/*      */     
/*  630 */     return getMagnetURL(torrent.getHash());
/*      */   }
/*      */   
/*      */ 
/*      */   public URL getMagnetURL(byte[] hash)
/*      */   {
/*      */     try
/*      */     {
/*  638 */       return new URL("magnet:?xt=urn:btih:" + Base32.encode(hash));
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  642 */       Debug.printStackTrace(e);
/*      */     }
/*  644 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public byte[] badge()
/*      */   {
/*  651 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] download(MagnetPluginProgressListener listener, byte[] hash, String args, InetSocketAddress[] sources, long timeout, int flags)
/*      */     throws MagnetURIHandlerException
/*      */   {
/*  665 */     DownloadResult result = downloadSupport(listener, hash, args, sources, timeout, flags);
/*      */     
/*  667 */     if (result == null)
/*      */     {
/*  669 */       return null;
/*      */     }
/*      */     
/*  672 */     return addTrackersAndWebSeedsEtc(result, args);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] addTrackersAndWebSeedsEtc(DownloadResult result, String args)
/*      */   {
/*  680 */     byte[] torrent_data = result.getTorrentData();
/*  681 */     Set<String> networks = result.getNetworks();
/*      */     
/*  683 */     return addTrackersAndWebSeedsEtc(torrent_data, args, networks);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] addTrackersAndWebSeedsEtc(byte[] torrent_data, String args, Set<String> networks)
/*      */   {
/*  692 */     List<String> new_web_seeds = new ArrayList();
/*  693 */     List<String> new_trackers = new ArrayList();
/*      */     
/*  695 */     Set<String> tags = new HashSet();
/*      */     
/*  697 */     if (args != null)
/*      */     {
/*  699 */       String[] bits = args.split("&");
/*      */       
/*      */ 
/*  702 */       for (String bit : bits)
/*      */       {
/*  704 */         String[] x = bit.split("=");
/*      */         
/*  706 */         if (x.length == 2)
/*      */         {
/*  708 */           String lhs = x[0].toLowerCase();
/*      */           
/*  710 */           if (lhs.equals("ws")) {
/*      */             try
/*      */             {
/*  713 */               new_web_seeds.add(new URL(UrlUtils.decode(x[1])).toExternalForm());
/*      */ 
/*      */             }
/*      */             catch (Throwable e) {}
/*  717 */           } else if (lhs.equals("tr")) {
/*      */             try
/*      */             {
/*  720 */               new_trackers.add(new URL(UrlUtils.decode(x[1])).toExternalForm());
/*      */ 
/*      */             }
/*      */             catch (Throwable e) {}
/*  724 */           } else if (lhs.equals("tag"))
/*      */           {
/*  726 */             tags.add(UrlUtils.decode(x[1]));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  732 */     if ((new_web_seeds.size() > 0) || (new_trackers.size() > 0) || (networks.size() > 0)) {
/*      */       try
/*      */       {
/*  735 */         TOTorrent torrent = TOTorrentFactory.deserialiseFromBEncodedByteArray(torrent_data);
/*      */         
/*  737 */         boolean update_torrent = false;
/*      */         
/*  739 */         if (new_web_seeds.size() > 0)
/*      */         {
/*  741 */           Object obj = torrent.getAdditionalProperty("url-list");
/*      */           
/*  743 */           List<String> existing = new ArrayList();
/*      */           
/*  745 */           if ((obj instanceof byte[]))
/*      */           {
/*      */             try {
/*  748 */               new_web_seeds.remove(new URL(new String((byte[])obj, "UTF-8")).toExternalForm());
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/*  752 */           else if ((obj instanceof List))
/*      */           {
/*  754 */             List<byte[]> l = (List)obj;
/*      */             
/*  756 */             for (byte[] b : l) {
/*      */               try
/*      */               {
/*  759 */                 existing.add(new URL(new String((byte[])b, "UTF-8")).toExternalForm());
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*  766 */           boolean update_ws = false;
/*      */           
/*  768 */           for (String e : new_web_seeds)
/*      */           {
/*  770 */             if (!existing.contains(e))
/*      */             {
/*  772 */               existing.add(e);
/*      */               
/*  774 */               update_ws = true;
/*      */             }
/*      */           }
/*      */           
/*  778 */           if (update_ws)
/*      */           {
/*  780 */             List<byte[]> l = new ArrayList();
/*      */             
/*  782 */             for (String s : existing)
/*      */             {
/*  784 */               l.add(s.getBytes("UTF-8"));
/*      */             }
/*      */             
/*  787 */             torrent.setAdditionalProperty("url-list", l);
/*      */             
/*  789 */             update_torrent = true;
/*      */           }
/*      */         }
/*      */         
/*  793 */         if (new_trackers.size() > 0)
/*      */         {
/*  795 */           URL announce_url = torrent.getAnnounceURL();
/*      */           
/*  797 */           new_trackers.remove(announce_url.toExternalForm());
/*      */           
/*  799 */           TOTorrentAnnounceURLGroup group = torrent.getAnnounceURLGroup();
/*      */           
/*  801 */           TOTorrentAnnounceURLSet[] sets = group.getAnnounceURLSets();
/*      */           
/*  803 */           for (TOTorrentAnnounceURLSet set : sets)
/*      */           {
/*  805 */             URL[] set_urls = set.getAnnounceURLs();
/*      */             
/*  807 */             for (URL set_url : set_urls)
/*      */             {
/*  809 */               new_trackers.remove(set_url.toExternalForm());
/*      */             }
/*      */           }
/*      */           
/*  813 */           if (new_trackers.size() > 0)
/*      */           {
/*  815 */             TOTorrentAnnounceURLSet[] new_sets = new TOTorrentAnnounceURLSet[sets.length + new_trackers.size()];
/*      */             
/*  817 */             System.arraycopy(sets, 0, new_sets, 0, sets.length);
/*      */             
/*  819 */             for (int i = 0; i < new_trackers.size(); i++)
/*      */             {
/*  821 */               TOTorrentAnnounceURLSet new_set = group.createAnnounceURLSet(new URL[] { new URL((String)new_trackers.get(i)) });
/*      */               
/*  823 */               new_sets[(i + sets.length)] = new_set;
/*      */             }
/*      */             
/*  826 */             group.setAnnounceURLSets(new_sets);
/*      */             
/*  828 */             update_torrent = true;
/*      */           }
/*      */         }
/*      */         
/*  832 */         if (networks.size() > 0)
/*      */         {
/*  834 */           TorrentUtils.setNetworkCache(torrent, new ArrayList(networks));
/*      */           
/*  836 */           update_torrent = true;
/*      */         }
/*      */         
/*  839 */         if (tags.size() > 0)
/*      */         {
/*  841 */           TorrentUtils.setTagCache(torrent, new ArrayList(tags));
/*      */           
/*  843 */           update_torrent = true;
/*      */         }
/*      */         
/*  846 */         if (update_torrent)
/*      */         {
/*  848 */           torrent_data = BEncoder.encode(torrent.serialiseToMap());
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*  854 */     return torrent_data;
/*      */   }
/*      */   
/*  857 */   private static ByteArrayHashMap<DownloadActivity> download_activities = new ByteArrayHashMap();
/*      */   
/*      */ 
/*      */   private static class DownloadActivity
/*      */   {
/*      */     private volatile MagnetPlugin.DownloadResult result;
/*      */     
/*      */     private volatile MagnetURIHandlerException error;
/*  865 */     private AESemaphore sem = new AESemaphore("MP:DA");
/*      */     
/*      */ 
/*      */ 
/*      */     public void setResult(MagnetPlugin.DownloadResult _result)
/*      */     {
/*  871 */       this.result = _result;
/*      */       
/*  873 */       this.sem.releaseForever();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setResult(Throwable _error)
/*      */     {
/*  880 */       if ((_error instanceof MagnetURIHandlerException))
/*      */       {
/*  882 */         this.error = ((MagnetURIHandlerException)_error);
/*      */       }
/*      */       else
/*      */       {
/*  886 */         this.error = new MagnetURIHandlerException("Download failed", _error);
/*      */       }
/*      */       
/*  889 */       this.sem.releaseForever();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public MagnetPlugin.DownloadResult getResult()
/*      */       throws MagnetURIHandlerException
/*      */     {
/*  897 */       this.sem.reserve();
/*      */       
/*  899 */       if (this.error != null)
/*      */       {
/*  901 */         throw this.error;
/*      */       }
/*      */       
/*  904 */       return this.result;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private DownloadResult downloadSupport(MagnetPluginProgressListener listener, byte[] hash, String args, InetSocketAddress[] sources, long timeout, int flags)
/*      */     throws MagnetURIHandlerException
/*      */   {
/*  920 */     boolean new_activity = false;
/*      */     DownloadActivity activity;
/*  922 */     synchronized (download_activities)
/*      */     {
/*      */ 
/*      */ 
/*  926 */       activity = (DownloadActivity)download_activities.get(hash);
/*      */       
/*  928 */       if (activity == null)
/*      */       {
/*  930 */         activity = new DownloadActivity(null);
/*      */         
/*  932 */         download_activities.put(hash, activity);
/*      */         
/*  934 */         new_activity = true;
/*      */       }
/*      */     }
/*      */     
/*  938 */     if (new_activity)
/*      */     {
/*      */       try
/*      */       {
/*  942 */         activity.setResult(_downloadSupport(listener, hash, args, sources, timeout, flags));
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  946 */         activity.setResult(e);
/*      */       }
/*      */       finally
/*      */       {
/*  950 */         synchronized (download_activities)
/*      */         {
/*  952 */           download_activities.remove(hash);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  957 */     return activity.getResult();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private DownloadResult _downloadSupport(final MagnetPluginProgressListener listener, final byte[] hash, final String args, final InetSocketAddress[] sources, long _timeout, int flags)
/*      */     throws MagnetURIHandlerException
/*      */   {
/*      */     long timeout;
/*      */     
/*      */ 
/*      */ 
/*      */     final long timeout;
/*      */     
/*      */ 
/*      */ 
/*  974 */     if (_timeout < 0L)
/*      */     {
/*      */ 
/*      */ 
/*  978 */       int secs = this.timeout_param.getValue();
/*      */       long timeout;
/*  980 */       if (secs <= 0)
/*      */       {
/*  982 */         timeout = 2147483647L;
/*      */       }
/*      */       else
/*      */       {
/*  986 */         timeout = secs * 1000L;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  991 */       timeout = _timeout;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  996 */     boolean dummy_hash = Arrays.equals(hash, new byte[20]);
/*      */     boolean md_enabled;
/*  998 */     boolean md_enabled; if ((flags & 0x1) != 0)
/*      */     {
/* 1000 */       md_enabled = false;
/*      */     }
/*      */     else
/*      */     {
/* 1004 */       md_enabled = (this.md_lookup.getValue()) && (FeatureAvailability.isMagnetMDEnabled());
/*      */     }
/*      */     
/* 1007 */     final byte[][] result_holder = { null };
/* 1008 */     final Throwable[] result_error = { null };
/*      */     
/* 1010 */     TimerEvent md_delay_event = null;
/* 1011 */     final MagnetPluginMDDownloader[] md_downloader = { null };
/*      */     
/* 1013 */     boolean net_pub_default = isNetworkEnabled("Public");
/*      */     
/*      */ 
/*      */ 
/* 1017 */     final Set<String> additional_networks = new HashSet();
/*      */     final Set<String> networks_enabled;
/* 1019 */     if (args != null)
/*      */     {
/* 1021 */       String[] bits = args.split("&");
/*      */       
/* 1023 */       List<URL> fl_args = new ArrayList();
/*      */       
/* 1025 */       Set<String> tr_networks = new HashSet();
/* 1026 */       Set<String> explicit_networks = new HashSet();
/*      */       
/* 1028 */       for (String bit : bits)
/*      */       {
/* 1030 */         if (bit.startsWith("maggot_sha1"))
/*      */         {
/* 1032 */           tr_networks.clear();
/*      */           
/* 1034 */           explicit_networks.clear();
/*      */           
/* 1036 */           fl_args.clear();
/*      */           
/* 1038 */           explicit_networks.add("I2P");
/*      */           
/* 1040 */           break;
/*      */         }
/*      */         
/* 1043 */         String[] x = bit.split("=");
/*      */         
/* 1045 */         if (x.length == 2)
/*      */         {
/* 1047 */           String lhs = x[0].toLowerCase();
/*      */           
/* 1049 */           if ((lhs.equals("fl")) || (lhs.equals("xs")) || (lhs.equals("as")))
/*      */           {
/*      */             try {
/* 1052 */               URL url = new URL(UrlUtils.decode(x[1]));
/*      */               
/* 1054 */               fl_args.add(url);
/*      */               
/* 1056 */               tr_networks.add(AENetworkClassifier.categoriseAddress(url.getHost()));
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/* 1060 */           else if (lhs.equals("tr"))
/*      */           {
/*      */             try {
/* 1063 */               tr_networks.add(AENetworkClassifier.categoriseAddress(new URL(UrlUtils.decode(x[1])).getHost()));
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/* 1067 */           else if (lhs.equals("net"))
/*      */           {
/* 1069 */             String network = AENetworkClassifier.internalise(x[1]);
/*      */             
/* 1071 */             if (network != null)
/*      */             {
/* 1073 */               explicit_networks.add(network); }
/*      */           }
/*      */         }
/*      */       }
/*      */       Set<String> networks_enabled;
/*      */       Set<String> networks_enabled;
/* 1079 */       if (explicit_networks.size() > 0)
/*      */       {
/* 1081 */         networks_enabled = explicit_networks;
/*      */       }
/*      */       else
/*      */       {
/* 1085 */         networks_enabled = tr_networks;
/*      */         
/* 1087 */         if (net_pub_default)
/*      */         {
/* 1089 */           if (networks_enabled.size() == 0)
/*      */           {
/* 1091 */             networks_enabled.add("Public");
/*      */           }
/*      */         }
/*      */         else {
/* 1095 */           networks_enabled.remove("Public");
/*      */         }
/*      */       }
/*      */       
/* 1099 */       if (fl_args.size() > 0)
/*      */       {
/* 1101 */         final AESemaphore fl_sem = new AESemaphore("fl_sem");
/*      */         
/* 1103 */         int fl_run = 0;
/*      */         
/* 1105 */         for (int i = 0; (i < fl_args.size()) && (i < 3); i++)
/*      */         {
/* 1107 */           final URL fl_url = (URL)fl_args.get(i);
/*      */           
/* 1109 */           String url_net = AENetworkClassifier.categoriseAddress(fl_url.getHost());
/*      */           
/* 1111 */           if (networks_enabled.contains(url_net))
/*      */           {
/* 1113 */             new AEThread2("Magnet:fldl", true)
/*      */             {
/*      */               public void run()
/*      */               {
/*      */                 try
/*      */                 {
/* 1119 */                   TOTorrent torrent = TorrentUtils.download(fl_url, timeout);
/*      */                   
/* 1121 */                   if (torrent != null)
/*      */                   {
/* 1123 */                     if ((hash) || (Arrays.equals(torrent.getHash(), result_holder)))
/*      */                     {
/* 1125 */                       synchronized (fl_sem)
/*      */                       {
/* 1127 */                         fl_sem[0] = BEncoder.encode(torrent.serialiseToMap());
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable e) {
/* 1133 */                   Debug.out(e);
/*      */                 }
/*      */                 finally
/*      */                 {
/* 1137 */                   this.val$fl_sem.release();
/*      */                 }
/*      */                 
/*      */               }
/* 1141 */             }.start();
/* 1142 */             fl_run++;
/*      */           }
/*      */         }
/*      */         
/* 1146 */         if (dummy_hash)
/*      */         {
/* 1148 */           long remaining = timeout;
/*      */           
/* 1150 */           for (int i = 0; (i < fl_run) && (remaining > 0L); i++)
/*      */           {
/* 1152 */             long start = SystemTime.getMonotonousTime();
/*      */             
/* 1154 */             if (!fl_sem.reserve(remaining)) {
/*      */               break;
/*      */             }
/*      */             
/*      */ 
/* 1159 */             remaining -= SystemTime.getMonotonousTime() - start;
/*      */             
/* 1161 */             synchronized (result_holder)
/*      */             {
/* 1163 */               if (result_holder[0] != null)
/*      */               {
/* 1165 */                 return new DownloadResult(result_holder[0], networks_enabled, additional_networks, null);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 1173 */       networks_enabled = new HashSet();
/*      */       
/* 1175 */       if (net_pub_default)
/*      */       {
/* 1177 */         networks_enabled.add("Public");
/*      */       }
/*      */     }
/*      */     
/* 1181 */     if (dummy_hash)
/*      */     {
/* 1183 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1189 */     if (md_enabled)
/*      */     {
/* 1191 */       int delay_millis = this.md_lookup_delay.getValue() * 1000;
/*      */       
/* 1193 */       md_delay_event = SimpleTimer.addEvent("MagnetPlugin:md_delay", delay_millis <= 0 ? 0L : SystemTime.getCurrentTime() + delay_millis, new TimerEventPerformer()
/*      */       {
/*      */         public void perform(TimerEvent event)
/*      */         {
/*      */           MagnetPluginMDDownloader mdd;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1205 */           synchronized (md_downloader)
/*      */           {
/* 1207 */             if (event.isCancelled())
/*      */             {
/* 1209 */               return;
/*      */             }
/*      */             
/* 1212 */             md_downloader[0] = (mdd = new MagnetPluginMDDownloader(MagnetPlugin.this, MagnetPlugin.this.plugin_interface, hash, networks_enabled, sources, args));
/*      */           }
/*      */           
/* 1215 */           if (listener != null) {
/* 1216 */             listener.reportActivity(MagnetPlugin.this.getMessageText("report.md.starts", new String[0]));
/*      */           }
/*      */           
/* 1219 */           mdd.start(new MagnetPluginMDDownloader.DownloadListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void reportProgress(int downloaded, int total_size)
/*      */             {
/*      */ 
/*      */ 
/* 1227 */               if (MagnetPlugin.8.this.val$listener != null) {
/* 1228 */                 MagnetPlugin.8.this.val$listener.reportActivity(MagnetPlugin.this.getMessageText("report.md.progress", new String[] { String.valueOf(downloaded + "/" + total_size) }));
/*      */                 
/* 1230 */                 MagnetPlugin.8.this.val$listener.reportCompleteness(100 * downloaded / total_size);
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public void complete(TOTorrent torrent, Set<String> peer_networks)
/*      */             {
/* 1239 */               if (MagnetPlugin.8.this.val$listener != null) {
/* 1240 */                 MagnetPlugin.8.this.val$listener.reportActivity(MagnetPlugin.this.getMessageText("report.md.done", new String[0]));
/*      */               }
/*      */               
/* 1243 */               synchronized (MagnetPlugin.8.this.val$result_holder)
/*      */               {
/* 1245 */                 MagnetPlugin.8.this.val$additional_networks.addAll(peer_networks);
/*      */                 try
/*      */                 {
/* 1248 */                   MagnetPlugin.8.this.val$result_holder[0] = BEncoder.encode(torrent.serialiseToMap());
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 1252 */                   Debug.out(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */             public void failed(Throwable e)
/*      */             {
/* 1261 */               if (MagnetPlugin.8.this.val$listener != null) {
/* 1262 */                 MagnetPlugin.8.this.val$listener.reportActivity(MagnetPlugin.this.getMessageText("report.error", new String[] { Debug.getNestedExceptionMessage(e) }));
/*      */               }
/*      */               
/* 1265 */               synchronized (MagnetPlugin.8.this.val$result_holder)
/*      */               {
/* 1267 */                 MagnetPlugin.8.this.val$result_error[0] = e;
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */     try
/*      */     {
/* 1278 */       long remaining = timeout;
/*      */       
/* 1280 */       boolean sl_enabled = (this.secondary_lookup.getValue()) && (FeatureAvailability.isMagnetSLEnabled());
/* 1281 */       boolean sl_failed = false;
/* 1282 */       long secondary_lookup_time = -1L;
/*      */       
/* 1284 */       Object[] secondary_result = { null };
/*      */       
/*      */       boolean is_first_download;
/*      */       
/* 1288 */       if (networks_enabled.contains("Public"))
/*      */       {
/* 1290 */         is_first_download = this.first_download;
/*      */         
/* 1292 */         if (is_first_download)
/*      */         {
/* 1294 */           if (listener != null) {
/* 1295 */             listener.reportActivity(getMessageText("report.waiting_ddb", new String[0]));
/*      */           }
/*      */           
/* 1298 */           this.first_download = false;
/*      */         }
/*      */         
/* 1301 */         final DistributedDatabase db = this.plugin_interface.getDistributedDatabase();
/*      */         
/* 1303 */         if (db.isAvailable())
/*      */         {
/* 1305 */           final List potential_contacts = new ArrayList();
/* 1306 */           final AESemaphore potential_contacts_sem = new AESemaphore("MagnetPlugin:liveones");
/* 1307 */           final AEMonitor potential_contacts_mon = new AEMonitor("MagnetPlugin:liveones");
/*      */           
/* 1309 */           final int[] outstanding = { 0 };
/* 1310 */           final boolean[] lookup_complete = { false };
/*      */           
/* 1312 */           if (listener != null) {
/* 1313 */             listener.reportActivity(getMessageText("report.searching", new String[0]));
/*      */           }
/*      */           
/* 1316 */           DistributedDatabaseListener ddb_listener = new DistributedDatabaseListener()
/*      */           {
/*      */ 
/* 1319 */             private Set found_set = new HashSet();
/*      */             
/*      */ 
/*      */ 
/*      */             public void event(DistributedDatabaseEvent event)
/*      */             {
/* 1325 */               int type = event.getType();
/*      */               
/* 1327 */               if (type == 7)
/*      */               {
/*      */ 
/*      */ 
/* 1331 */                 if (sources.length > 0)
/*      */                 {
/* 1333 */                   new DelayedEvent("MP:sourceAdd", 10000L, new AERunnable()
/*      */                   {
/*      */ 
/*      */ 
/*      */                     public void runSupport()
/*      */                     {
/*      */ 
/*      */ 
/* 1341 */                       MagnetPlugin.9.this.addExplicitSources();
/*      */                     }
/*      */                   });
/*      */                 }
/*      */               }
/* 1346 */               else if (type == 2)
/*      */               {
/* 1348 */                 contactFound(event.getValue().getContact());
/*      */               }
/* 1350 */               else if ((type == 4) || (type == 5))
/*      */               {
/*      */ 
/* 1353 */                 if (listener != null) {
/* 1354 */                   listener.reportActivity(MagnetPlugin.this.getMessageText("report.found", new String[] { String.valueOf(this.found_set.size()) }));
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/* 1359 */                 addExplicitSources();
/*      */                 try
/*      */                 {
/* 1362 */                   potential_contacts_mon.enter();
/*      */                   
/* 1364 */                   lookup_complete[0] = true;
/*      */                 }
/*      */                 finally
/*      */                 {
/* 1368 */                   potential_contacts_mon.exit();
/*      */                 }
/*      */                 
/* 1371 */                 potential_contacts_sem.release();
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */             protected void addExplicitSources()
/*      */             {
/* 1378 */               for (int i = 0; i < sources.length; i++) {
/*      */                 try
/*      */                 {
/* 1381 */                   contactFound(db.importContact(sources[i]));
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 1385 */                   Debug.printStackTrace(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */             public void contactFound(final DistributedDatabaseContact contact)
/*      */             {
/* 1394 */               String key = contact.getAddress().toString();
/*      */               
/* 1396 */               synchronized (this.found_set)
/*      */               {
/* 1398 */                 if (this.found_set.contains(key))
/*      */                 {
/* 1400 */                   return;
/*      */                 }
/*      */                 
/* 1403 */                 this.found_set.add(key);
/*      */               }
/*      */               
/* 1406 */               if ((listener != null) && (listener.verbose()))
/*      */               {
/* 1408 */                 listener.reportActivity(MagnetPlugin.this.getMessageText("report.found", new String[] { contact.getName() }));
/*      */               }
/*      */               try
/*      */               {
/* 1412 */                 potential_contacts_mon.enter();
/*      */                 
/* 1414 */                 outstanding[0] += 1;
/*      */               }
/*      */               finally
/*      */               {
/* 1418 */                 potential_contacts_mon.exit();
/*      */               }
/*      */               
/* 1421 */               contact.isAlive(20000L, new DistributedDatabaseListener()
/*      */               {
/*      */ 
/*      */                 public void event(DistributedDatabaseEvent event)
/*      */                 {
/*      */ 
/*      */                   try
/*      */                   {
/*      */ 
/* 1430 */                     boolean alive = event.getType() == 4;
/*      */                     
/* 1432 */                     if ((MagnetPlugin.9.this.val$listener != null) && (MagnetPlugin.9.this.val$listener.verbose()))
/*      */                     {
/* 1434 */                       MagnetPlugin.9.this.val$listener.reportActivity(MagnetPlugin.this.getMessageText(alive ? "report.alive" : "report.dead", new String[] { contact.getName() }));
/*      */                     }
/*      */                     
/*      */                     try
/*      */                     {
/* 1439 */                       MagnetPlugin.9.this.val$potential_contacts_mon.enter();
/*      */                       
/* 1441 */                       Object[] entry = { Boolean.valueOf(alive), contact };
/*      */                       
/* 1443 */                       boolean added = false;
/*      */                       
/* 1445 */                       if (alive)
/*      */                       {
/*      */ 
/*      */ 
/* 1449 */                         for (int i = 0; i < MagnetPlugin.9.this.val$potential_contacts.size(); i++)
/*      */                         {
/* 1451 */                           if (!((Boolean)((Object[])(Object[])MagnetPlugin.9.this.val$potential_contacts.get(i))[0]).booleanValue())
/*      */                           {
/* 1453 */                             MagnetPlugin.9.this.val$potential_contacts.add(i, entry);
/*      */                             
/* 1455 */                             added = true;
/*      */                             
/* 1457 */                             break;
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                       
/* 1462 */                       if (!added)
/*      */                       {
/* 1464 */                         MagnetPlugin.9.this.val$potential_contacts.add(entry);
/*      */                       }
/*      */                     }
/*      */                     finally
/*      */                     {
/* 1469 */                       MagnetPlugin.9.this.val$potential_contacts_mon.exit();
/*      */                     }
/*      */                   }
/*      */                   finally {
/*      */                     try {
/* 1474 */                       MagnetPlugin.9.this.val$potential_contacts_mon.enter();
/*      */                       
/* 1476 */                       MagnetPlugin.9.this.val$outstanding[0] -= 1;
/*      */                     }
/*      */                     finally
/*      */                     {
/* 1480 */                       MagnetPlugin.9.this.val$potential_contacts_mon.exit();
/*      */                     }
/*      */                     
/* 1483 */                     MagnetPlugin.9.this.val$potential_contacts_sem.release();
/*      */                   }
/*      */                   
/*      */                 }
/*      */               });
/*      */             }
/* 1489 */           };
/* 1490 */           db.read(ddb_listener, db.createKey(hash, "Torrent download lookup for '" + ByteFormatter.encodeString(hash) + "'"), timeout, 3);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1496 */           long overall_start = SystemTime.getMonotonousTime();
/* 1497 */           long last_found = -1L;
/*      */           
/* 1499 */           AsyncDispatcher dispatcher = new AsyncDispatcher();
/*      */           
/* 1501 */           while (remaining > 0L)
/*      */           {
/*      */             try {
/* 1504 */               potential_contacts_mon.enter();
/*      */               
/* 1506 */               if ((lookup_complete[0] != 0) && (potential_contacts.size() == 0) && (outstanding[0] == 0))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1514 */                 potential_contacts_mon.exit(); break; } } finally { potential_contacts_mon.exit();
/*      */             }
/*      */             
/*      */             long now;
/* 1518 */             while (remaining > 0L)
/*      */             {
/* 1520 */               if ((listener != null) && (listener.cancelled()))
/*      */               {
/* 1522 */                 return null;
/*      */               }
/*      */               
/* 1525 */               synchronized (result_holder)
/*      */               {
/* 1527 */                 if (result_holder[0] != null)
/*      */                 {
/* 1529 */                   return new DownloadResult(result_holder[0], networks_enabled, additional_networks, null);
/*      */                 }
/*      */               }
/*      */               
/* 1533 */               long wait_start = SystemTime.getMonotonousTime();
/*      */               
/* 1535 */               boolean got_sem = potential_contacts_sem.reserve(1000L);
/*      */               
/* 1537 */               now = SystemTime.getMonotonousTime();
/*      */               
/* 1539 */               remaining -= now - wait_start;
/*      */               
/* 1541 */               if (got_sem)
/*      */               {
/* 1543 */                 last_found = now;
/*      */                 
/* 1545 */                 break;
/*      */               }
/*      */               
/*      */ 
/* 1549 */               if (sl_enabled)
/*      */               {
/* 1551 */                 if (secondary_lookup_time == -1L)
/*      */                 {
/*      */                   long base_time;
/*      */                   long base_time;
/* 1555 */                   if ((last_found == -1L) || (now - overall_start > 60000L))
/*      */                   {
/* 1557 */                     base_time = overall_start;
/*      */                   }
/*      */                   else
/*      */                   {
/* 1561 */                     base_time = last_found;
/*      */                   }
/*      */                   
/* 1564 */                   long time_so_far = now - base_time;
/*      */                   
/* 1566 */                   if (time_so_far > 20000L)
/*      */                   {
/* 1568 */                     secondary_lookup_time = SystemTime.getMonotonousTime();
/*      */                     
/* 1570 */                     doSecondaryLookup(listener, secondary_result, hash, networks_enabled, args);
/*      */                   }
/*      */                 }
/*      */                 else {
/*      */                   try {
/* 1575 */                     byte[] torrent = getSecondaryLookupResult(secondary_result);
/*      */                     
/* 1577 */                     if (torrent != null)
/*      */                     {
/* 1579 */                       return new DownloadResult(torrent, networks_enabled, additional_networks, null);
/*      */                     }
/*      */                   }
/*      */                   catch (ResourceDownloaderException e) {
/* 1583 */                     sl_failed = true;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1594 */             if (sl_enabled)
/*      */             {
/*      */               try
/*      */               {
/*      */ 
/* 1599 */                 byte[] torrent = getSecondaryLookupResult(secondary_result);
/*      */                 
/* 1601 */                 if (torrent != null)
/*      */                 {
/* 1603 */                   return new DownloadResult(torrent, networks_enabled, additional_networks, null);
/*      */                 }
/*      */               }
/*      */               catch (ResourceDownloaderException e) {
/* 1607 */                 sl_failed = true;
/*      */               }
/*      */             }
/*      */             
/*      */             final boolean live_contact;
/*      */             final DistributedDatabaseContact contact;
/*      */             try
/*      */             {
/* 1615 */               potential_contacts_mon.enter();
/*      */               
/*      */ 
/*      */ 
/* 1619 */               if (potential_contacts.size() == 0)
/*      */               {
/* 1621 */                 if (outstanding[0] == 0)
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1639 */                   potential_contacts_mon.exit(); break; } potential_contacts_mon.exit(); continue;
/*      */               }
/* 1631 */               Object[] entry = (Object[])potential_contacts.remove(0);
/*      */               
/* 1633 */               live_contact = ((Boolean)entry[0]).booleanValue();
/* 1634 */               contact = (DistributedDatabaseContact)entry[1];
/*      */ 
/*      */             }
/*      */             finally
/*      */             {
/* 1639 */               potential_contacts_mon.exit();
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 1644 */             final AESemaphore contact_sem = new AESemaphore("MD:contact");
/*      */             
/* 1646 */             dispatcher.dispatch(new AERunnable()
/*      */             {
/*      */ 
/*      */               public void runSupport()
/*      */               {
/*      */                 try
/*      */                 {
/* 1653 */                   if (!live_contact)
/*      */                   {
/* 1655 */                     if (listener != null) {
/* 1656 */                       listener.reportActivity(MagnetPlugin.this.getMessageText("report.tunnel", new String[] { contact.getName() }));
/*      */                     }
/*      */                     
/* 1659 */                     contact.openTunnel();
/*      */                   }
/*      */                   try
/*      */                   {
/* 1663 */                     if (listener != null) {
/* 1664 */                       listener.reportActivity(MagnetPlugin.this.getMessageText("report.downloading", new String[] { contact.getName() }));
/*      */                     }
/*      */                     
/* 1667 */                     DistributedDatabaseValue value = contact.read(listener == null ? null : new DistributedDatabaseProgressListener()
/*      */                     {
/*      */ 
/*      */ 
/*      */                       public void reportSize(long size)
/*      */                       {
/*      */ 
/*      */ 
/* 1675 */                         MagnetPlugin.10.this.val$listener.reportSize(size);
/*      */                       }
/*      */                       
/*      */ 
/*      */                       public void reportActivity(String str)
/*      */                       {
/* 1681 */                         MagnetPlugin.10.this.val$listener.reportActivity(str);
/*      */                       }
/*      */                       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1688 */                       public void reportCompleteness(int percent) { MagnetPlugin.10.this.val$listener.reportCompleteness(percent); } }, db.getStandardTransferType(1), db.createKey(hash, "Torrent download content for '" + ByteFormatter.encodeString(hash) + "'"), timeout);
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1695 */                     if (value != null)
/*      */                     {
/*      */ 
/*      */ 
/* 1699 */                       byte[] data = (byte[])value.getValue(byte[].class);
/*      */                       try
/*      */                       {
/* 1702 */                         TOTorrent torrent = TOTorrentFactory.deserialiseFromBEncodedByteArray(data);
/*      */                         
/* 1704 */                         if (Arrays.equals(hash, torrent.getHash()))
/*      */                         {
/* 1706 */                           if (listener != null) {
/* 1707 */                             listener.reportContributor(contact.getAddress());
/*      */                           }
/*      */                           
/* 1710 */                           synchronized (contact_sem)
/*      */                           {
/* 1712 */                             contact_sem[0] = data;
/*      */                           }
/*      */                           
/*      */                         }
/* 1716 */                         else if (listener != null) {
/* 1717 */                           listener.reportActivity(MagnetPlugin.this.getMessageText("report.error", new String[] { "torrent invalid (hash mismatch)" }));
/*      */                         }
/*      */                       }
/*      */                       catch (Throwable e)
/*      */                       {
/* 1722 */                         if (listener != null) {
/* 1723 */                           listener.reportActivity(MagnetPlugin.this.getMessageText("report.error", new String[] { "torrent invalid (decode failed)" }));
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                   catch (Throwable e) {
/* 1729 */                     if (listener != null) {
/* 1730 */                       listener.reportActivity(MagnetPlugin.this.getMessageText("report.error", new String[] { Debug.getNestedExceptionMessage(e) }));
/*      */                     }
/*      */                     
/* 1733 */                     Debug.printStackTrace(e);
/*      */                   }
/*      */                 }
/*      */                 finally {
/* 1737 */                   this.val$contact_sem.release();
/*      */                 }
/*      */               }
/*      */             });
/*      */             
/*      */             for (;;)
/*      */             {
/* 1744 */               if ((listener != null) && (listener.cancelled()))
/*      */               {
/* 1746 */                 return null;
/*      */               }
/*      */               
/* 1749 */               boolean got_sem = contact_sem.reserve(500L);
/*      */               
/* 1751 */               synchronized (result_holder)
/*      */               {
/* 1753 */                 if (result_holder[0] != null)
/*      */                 {
/* 1755 */                   return new DownloadResult(result_holder[0], networks_enabled, additional_networks, null);
/*      */                 }
/*      */               }
/*      */               
/* 1759 */               if (got_sem) {
/*      */                 break;
/*      */               }
/*      */               
/*      */             }
/*      */             
/*      */           }
/*      */         }
/* 1767 */         else if (is_first_download)
/*      */         {
/* 1769 */           if (listener != null) {
/* 1770 */             listener.reportActivity(getMessageText("report.ddb_disabled", new String[0]));
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1779 */       if ((sl_enabled) && (!sl_failed))
/*      */       {
/* 1781 */         if (secondary_lookup_time == -1L)
/*      */         {
/* 1783 */           secondary_lookup_time = SystemTime.getMonotonousTime();
/*      */           
/* 1785 */           doSecondaryLookup(listener, secondary_result, hash, networks_enabled, args);
/*      */         }
/*      */         for (;;) {
/* 1788 */           if (SystemTime.getMonotonousTime() - secondary_lookup_time < 120000L)
/*      */           {
/* 1790 */             if ((listener != null) && (listener.cancelled()))
/*      */             {
/* 1792 */               return null;
/*      */             }
/*      */             try
/*      */             {
/* 1796 */               byte[] torrent = getSecondaryLookupResult(secondary_result);
/*      */               
/* 1798 */               if (torrent != null)
/*      */               {
/* 1800 */                 return new DownloadResult(torrent, networks_enabled, additional_networks, null);
/*      */               }
/*      */               
/* 1803 */               synchronized (result_holder)
/*      */               {
/* 1805 */                 if (result_holder[0] != null)
/*      */                 {
/* 1807 */                   return new DownloadResult(result_holder[0], networks_enabled, additional_networks, null);
/*      */                 }
/*      */               }
/*      */               
/* 1811 */               Thread.sleep(500L);
/*      */ 
/*      */             }
/*      */             catch (ResourceDownloaderException e)
/*      */             {
/*      */ 
/* 1817 */               sl_failed = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1826 */       if (md_enabled)
/*      */       {
/* 1828 */         while (remaining > 0L)
/*      */         {
/* 1830 */           if ((listener != null) && (listener.cancelled()))
/*      */           {
/* 1832 */             return null;
/*      */           }
/*      */           
/* 1835 */           Thread.sleep(500L);
/*      */           
/* 1837 */           remaining -= 500L;
/*      */           
/* 1839 */           if (!sl_failed) {
/*      */             try
/*      */             {
/* 1842 */               byte[] torrent = getSecondaryLookupResult(secondary_result);
/*      */               
/* 1844 */               if (torrent != null)
/*      */               {
/* 1846 */                 return new DownloadResult(torrent, networks_enabled, additional_networks, null);
/*      */               }
/*      */               
/*      */             }
/*      */             catch (ResourceDownloaderException e)
/*      */             {
/* 1852 */               sl_failed = true;
/*      */             }
/*      */           }
/*      */           
/* 1856 */           synchronized (result_holder)
/*      */           {
/* 1858 */             if (result_holder[0] != null)
/*      */             {
/* 1860 */               return new DownloadResult(result_holder[0], networks_enabled, additional_networks, null);
/*      */             }
/*      */             
/* 1863 */             if (result_error[0] != null) {
/*      */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1871 */       return null;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1875 */       Debug.printStackTrace(e);
/*      */       
/* 1877 */       if (listener != null) {
/* 1878 */         listener.reportActivity(getMessageText("report.error", new String[] { Debug.getNestedExceptionMessage(e) }));
/*      */       }
/*      */       
/* 1881 */       throw new MagnetURIHandlerException("MagnetURIHandler failed", e);
/*      */     }
/*      */     finally
/*      */     {
/* 1885 */       synchronized (md_downloader)
/*      */       {
/* 1887 */         if (md_delay_event != null)
/*      */         {
/* 1889 */           md_delay_event.cancel();
/*      */           
/* 1891 */           if (md_downloader[0] != null)
/*      */           {
/* 1893 */             md_downloader[0].cancel();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void doSecondaryLookup(final MagnetPluginProgressListener listener, final Object[] result, byte[] hash, Set<String> networks_enabled, String args)
/*      */   {
/* 1908 */     if (listener != null) {
/* 1909 */       listener.reportActivity(getMessageText("report.secondarylookup", null));
/*      */     }
/*      */     
/* 1912 */     AEProxyFactory.PluginProxy plugin_proxy = null;
/*      */     try
/*      */     {
/* 1915 */       URL original_sl_url = new URL("http://magnet.vuze.com/magnetLookup?hash=" + Base32.encode(hash) + (args.length() == 0 ? "" : new StringBuilder().append("&args=").append(UrlUtils.encode(args)).toString()));
/*      */       
/* 1917 */       URL sl_url = original_sl_url;
/* 1918 */       Proxy proxy = null;
/*      */       
/* 1920 */       if (!networks_enabled.contains("Public"))
/*      */       {
/* 1922 */         plugin_proxy = AEProxyFactory.getPluginProxy("secondary magnet lookup", sl_url);
/*      */         
/* 1924 */         if (plugin_proxy == null)
/*      */         {
/* 1926 */           throw new NoRouteToHostException("plugin proxy unavailable");
/*      */         }
/*      */         
/*      */ 
/* 1930 */         proxy = plugin_proxy.getProxy();
/* 1931 */         sl_url = plugin_proxy.getURL();
/*      */       }
/*      */       
/*      */ 
/* 1935 */       ResourceDownloaderFactory rdf = this.plugin_interface.getUtilities().getResourceDownloaderFactory();
/*      */       
/*      */       ResourceDownloader rd;
/*      */       ResourceDownloader rd;
/* 1939 */       if (proxy == null)
/*      */       {
/* 1941 */         rd = rdf.create(sl_url);
/*      */       }
/*      */       else
/*      */       {
/* 1945 */         rd = rdf.create(sl_url, proxy);
/*      */         
/* 1947 */         rd.setProperty("URL_HOST", original_sl_url.getHost());
/*      */       }
/*      */       
/* 1950 */       final AEProxyFactory.PluginProxy f_pp = plugin_proxy;
/*      */       
/* 1952 */       rd.addListener(new ResourceDownloaderAdapter()
/*      */       {
/*      */ 
/*      */         public boolean completed(ResourceDownloader downloader, InputStream data)
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/* 1961 */             if (listener != null) {
/* 1962 */               listener.reportActivity(MagnetPlugin.this.getMessageText("report.secondarylookup.ok", null));
/*      */             }
/*      */             
/* 1965 */             synchronized (result)
/*      */             {
/* 1967 */               result[0] = data;
/*      */             }
/*      */             
/* 1970 */             return (boolean)1;
/*      */           }
/*      */           finally
/*      */           {
/* 1974 */             complete();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void failed(ResourceDownloader downloader, ResourceDownloaderException e)
/*      */         {
/*      */           try
/*      */           {
/* 1984 */             synchronized (result)
/*      */             {
/* 1986 */               result[0] = e;
/*      */             }
/*      */             
/* 1989 */             if (listener != null) {
/* 1990 */               listener.reportActivity(MagnetPlugin.this.getMessageText("report.secondarylookup.fail", new String[0]));
/*      */             }
/*      */           }
/*      */           finally
/*      */           {
/* 1995 */             complete();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */         private void complete()
/*      */         {
/* 2002 */           if (f_pp != null)
/*      */           {
/* 2004 */             f_pp.setOK(true);
/*      */           }
/*      */           
/*      */         }
/* 2008 */       });
/* 2009 */       rd.asyncDownload();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2013 */       if (plugin_proxy != null)
/*      */       {
/* 2015 */         plugin_proxy.setOK(true);
/*      */       }
/*      */       
/* 2018 */       if (listener != null) {
/* 2019 */         listener.reportActivity(getMessageText("report.secondarylookup.fail", new String[] { Debug.getNestedExceptionMessage(e) }));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[] getSecondaryLookupResult(Object[] result)
/*      */     throws ResourceDownloaderException
/*      */   {
/* 2030 */     if (result == null)
/*      */     {
/* 2032 */       return null;
/*      */     }
/*      */     
/*      */     Object x;
/*      */     
/* 2037 */     synchronized (result)
/*      */     {
/* 2039 */       x = result[0];
/*      */       
/* 2041 */       result[0] = null;
/*      */     }
/*      */     
/* 2044 */     if ((x instanceof InputStream))
/*      */     {
/* 2046 */       InputStream is = (InputStream)x;
/*      */       try
/*      */       {
/* 2049 */         TOTorrent t = TOTorrentFactory.deserialiseFromBEncodedInputStream(is);
/*      */         
/* 2051 */         TorrentUtils.setPeerCacheValid(t);
/*      */         
/* 2053 */         return BEncoder.encode(t.serialiseToMap());
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/* 2057 */     else if ((x instanceof ResourceDownloaderException))
/*      */     {
/* 2059 */       throw ((ResourceDownloaderException)x);
/*      */     }
/*      */     
/* 2062 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String getMessageText(String resource, String... params)
/*      */   {
/* 2070 */     return this.plugin_interface.getUtilities().getLocaleUtilities().getLocalisedMessageText("MagnetPlugin." + resource, params);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addListener(MagnetPluginListener listener)
/*      */   {
/* 2078 */     this.listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(MagnetPluginListener listener)
/*      */   {
/* 2085 */     this.listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class DownloadResult
/*      */   {
/*      */     private byte[] data;
/*      */     
/*      */ 
/*      */     private Set<String> networks;
/*      */     
/*      */ 
/*      */     private DownloadResult(byte[] torrent_data, Set<String> networks_enabled, Set<String> additional_networks)
/*      */     {
/* 2100 */       this.data = torrent_data;
/*      */       
/* 2102 */       this.networks = new HashSet();
/*      */       
/* 2104 */       this.networks.addAll(networks_enabled);
/* 2105 */       this.networks.addAll(additional_networks);
/*      */     }
/*      */     
/*      */ 
/*      */     private byte[] getTorrentData()
/*      */     {
/* 2111 */       return this.data;
/*      */     }
/*      */     
/*      */ 
/*      */     private Set<String> getNetworks()
/*      */     {
/* 2117 */       return this.networks;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/magnet/MagnetPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */