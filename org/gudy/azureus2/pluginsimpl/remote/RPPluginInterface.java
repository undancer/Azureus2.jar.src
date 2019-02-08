/*     */ package org.gudy.azureus2.pluginsimpl.remote;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import java.util.Random;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.plugins.Plugin;
/*     */ import org.gudy.azureus2.plugins.PluginConfig;
/*     */ import org.gudy.azureus2.plugins.PluginEvent;
/*     */ import org.gudy.azureus2.plugins.PluginEventListener;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginListener;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.plugins.clientid.ClientIDManager;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
/*     */ import org.gudy.azureus2.plugins.dht.mainline.MainlineDHTManager;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*     */ import org.gudy.azureus2.plugins.ipfilter.IPFilter;
/*     */ import org.gudy.azureus2.plugins.logging.Logger;
/*     */ import org.gudy.azureus2.plugins.messaging.MessageManager;
/*     */ import org.gudy.azureus2.plugins.network.ConnectionManager;
/*     */ import org.gudy.azureus2.plugins.platform.PlatformManager;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareException;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareManager;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*     */ import org.gudy.azureus2.plugins.tracker.Tracker;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.config.ConfigSection;
/*     */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.PluginConfigUIFactory;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManager;
/*     */ import org.gudy.azureus2.plugins.utils.ShortCuts;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.download.RPDownloadManager;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.ipfilter.RPIPFilter;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.torrent.RPTorrentManager;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.tracker.RPTracker;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.utils.RPShortCuts;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RPPluginInterface
/*     */   extends RPObject
/*     */   implements PluginInterface
/*     */ {
/*  65 */   protected static transient long connection_id_next = new Random().nextLong();
/*     */   
/*     */ 
/*     */   protected transient PluginInterface delegate;
/*     */   
/*     */   protected transient long request_id_next;
/*     */   
/*  72 */   public String azureus_name = "Azureus";
/*  73 */   public String azureus_version = "5.7.6.0";
/*     */   
/*     */ 
/*     */   public long _connection_id;
/*     */   
/*     */ 
/*     */   public static RPPluginInterface create(PluginInterface _delegate)
/*     */   {
/*  81 */     RPPluginInterface res = (RPPluginInterface)_lookupLocal(_delegate);
/*     */     
/*  83 */     if (res == null)
/*     */     {
/*  85 */       res = new RPPluginInterface(_delegate);
/*     */     }
/*     */     
/*  88 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected RPPluginInterface(PluginInterface _delegate)
/*     */   {
/*  97 */     super(_delegate);
/*     */     
/*  99 */     synchronized (RPPluginInterface.class)
/*     */     {
/* 101 */       this._connection_id = (connection_id_next++);
/*     */       
/*     */ 
/*     */ 
/* 105 */       if (this._connection_id == 0L)
/*     */       {
/* 107 */         this._connection_id = (connection_id_next++);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected long _getConectionId()
/*     */   {
/* 115 */     return this._connection_id;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   protected long _getNextRequestId()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: dup
/*     */     //   6: getfield 295	org/gudy/azureus2/pluginsimpl/remote/RPPluginInterface:request_id_next	J
/*     */     //   9: dup2_x1
/*     */     //   10: lconst_1
/*     */     //   11: ladd
/*     */     //   12: putfield 295	org/gudy/azureus2/pluginsimpl/remote/RPPluginInterface:request_id_next	J
/*     */     //   15: aload_1
/*     */     //   16: monitorexit
/*     */     //   17: lreturn
/*     */     //   18: astore_2
/*     */     //   19: aload_1
/*     */     //   20: monitorexit
/*     */     //   21: aload_2
/*     */     //   22: athrow
/*     */     // Line number table:
/*     */     //   Java source line #121	-> byte code offset #0
/*     */     //   Java source line #123	-> byte code offset #4
/*     */     //   Java source line #124	-> byte code offset #18
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	23	0	this	RPPluginInterface
/*     */     //   2	18	1	Ljava/lang/Object;	Object
/*     */     //   18	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	17	18	finally
/*     */     //   18	21	18	finally
/*     */   }
/*     */   
/*     */   protected void _setDelegate(Object _delegate)
/*     */   {
/* 131 */     this.delegate = ((PluginInterface)_delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object _setLocal()
/*     */     throws RPException
/*     */   {
/* 139 */     return _fixupLocal();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public RPReply _process(RPRequest request)
/*     */   {
/* 147 */     String method = request.getMethod();
/*     */     
/* 149 */     if (method.equals("getPluginProperties"))
/*     */     {
/*     */ 
/*     */ 
/* 153 */       Properties p = new Properties();
/*     */       
/* 155 */       Properties x = this.delegate.getPluginProperties();
/*     */       
/* 157 */       Iterator it = x.keySet().iterator();
/*     */       
/* 159 */       while (it.hasNext())
/*     */       {
/* 161 */         Object key = it.next();
/*     */         
/* 163 */         p.put(key, x.get(key));
/*     */       }
/*     */       
/* 166 */       return new RPReply(p);
/*     */     }
/* 168 */     if (method.equals("getDownloadManager"))
/*     */     {
/* 170 */       return new RPReply(RPDownloadManager.create(this.delegate.getDownloadManager()));
/*     */     }
/* 172 */     if (method.equals("getTorrentManager"))
/*     */     {
/* 174 */       return new RPReply(RPTorrentManager.create(this.delegate.getTorrentManager()));
/*     */     }
/* 176 */     if (method.equals("getPluginconfig"))
/*     */     {
/* 178 */       return new RPReply(RPPluginConfig.create(this.delegate.getPluginconfig()));
/*     */     }
/* 180 */     if (method.equals("getIPFilter"))
/*     */     {
/* 182 */       return new RPReply(RPIPFilter.create(this.delegate.getIPFilter()));
/*     */     }
/* 184 */     if (method.equals("getShortCuts"))
/*     */     {
/* 186 */       return new RPReply(RPShortCuts.create(this.delegate.getShortCuts()));
/*     */     }
/* 188 */     if (method.equals("getTracker"))
/*     */     {
/* 190 */       return new RPReply(RPTracker.create(this.delegate.getTracker()));
/*     */     }
/*     */     
/* 193 */     throw new RPException("Unknown method: " + method);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PluginManager getPluginManager()
/*     */   {
/* 201 */     notSupported();
/*     */     
/* 203 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public Plugin getPlugin()
/*     */   {
/* 209 */     notSupported();
/*     */     
/* 211 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getAzureusName()
/*     */   {
/* 217 */     return this.azureus_name;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getAzureusVersion()
/*     */   {
/* 223 */     return this.azureus_version;
/*     */   }
/*     */   
/*     */   public String getApplicationName() {
/* 227 */     return Constants.APP_NAME;
/*     */   }
/*     */   
/*     */   public void addConfigUIParameters(Parameter[] parameters, String displayName)
/*     */   {
/* 232 */     notSupported();
/*     */   }
/*     */   
/*     */   public void addConfigSection(ConfigSection tab)
/*     */   {
/* 237 */     notSupported();
/*     */   }
/*     */   
/*     */   public void removeConfigSection(ConfigSection tab)
/*     */   {
/* 242 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public Tracker getTracker()
/*     */   {
/* 248 */     RPTracker res = (RPTracker)this._dispatcher.dispatch(new RPRequest(this, "getTracker", null)).getResponse();
/*     */     
/* 250 */     res._setRemote(this._dispatcher);
/*     */     
/* 252 */     return res;
/*     */   }
/*     */   
/*     */   public Logger getLogger()
/*     */   {
/* 257 */     notSupported();
/*     */     
/* 259 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public IPFilter getIPFilter()
/*     */   {
/* 265 */     RPIPFilter res = (RPIPFilter)this._dispatcher.dispatch(new RPRequest(this, "getIPFilter", null)).getResponse();
/*     */     
/* 267 */     res._setRemote(this._dispatcher);
/*     */     
/* 269 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public DownloadManager getDownloadManager()
/*     */   {
/* 275 */     RPDownloadManager res = (RPDownloadManager)this._dispatcher.dispatch(new RPRequest(this, "getDownloadManager", null)).getResponse();
/*     */     
/* 277 */     res._setRemote(this._dispatcher);
/*     */     
/* 279 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ShareManager getShareManager()
/*     */     throws ShareException
/*     */   {
/* 288 */     notSupported();
/*     */     
/* 290 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public Utilities getUtilities()
/*     */   {
/* 296 */     notSupported();
/*     */     
/* 298 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public ShortCuts getShortCuts()
/*     */   {
/* 304 */     RPShortCuts res = (RPShortCuts)this._dispatcher.dispatch(new RPRequest(this, "getShortCuts", null)).getResponse();
/*     */     
/* 306 */     res._setRemote(this._dispatcher);
/*     */     
/* 308 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public UIManager getUIManager()
/*     */   {
/* 314 */     notSupported();
/*     */     
/* 316 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public TorrentManager getTorrentManager()
/*     */   {
/* 322 */     RPTorrentManager res = (RPTorrentManager)this._dispatcher.dispatch(new RPRequest(this, "getTorrentManager", null)).getResponse();
/*     */     
/* 324 */     res._setRemote(this._dispatcher);
/*     */     
/* 326 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public void openTorrentFile(String fileName)
/*     */   {
/* 335 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public void openTorrentURL(String url)
/*     */   {
/* 344 */     notSupported();
/*     */   }
/*     */   
/*     */   public Properties getPluginProperties()
/*     */   {
/* 349 */     return (Properties)this._dispatcher.dispatch(new RPRequest(this, "getPluginProperties", null)).getResponse();
/*     */   }
/*     */   
/*     */   public String getPluginDirectoryName()
/*     */   {
/* 354 */     notSupported();
/*     */     
/* 356 */     return null;
/*     */   }
/*     */   
/*     */   public String getPerUserPluginDirectoryName()
/*     */   {
/* 361 */     notSupported();
/*     */     
/* 363 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isShared()
/*     */   {
/* 369 */     notSupported();
/*     */     
/* 371 */     return false;
/*     */   }
/*     */   
/*     */   public String getPluginName()
/*     */   {
/* 376 */     notSupported();
/*     */     
/* 378 */     return null;
/*     */   }
/*     */   
/*     */   public String getPluginID()
/*     */   {
/* 383 */     notSupported();
/*     */     
/* 385 */     return null;
/*     */   }
/*     */   
/*     */   public boolean isMandatory()
/*     */   {
/* 390 */     notSupported();
/*     */     
/* 392 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isBuiltIn()
/*     */   {
/* 398 */     notSupported();
/*     */     
/* 400 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSigned()
/*     */   {
/* 406 */     notSupported();
/*     */     
/* 408 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isOperational()
/*     */   {
/* 413 */     notSupported();
/*     */     
/* 415 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDisabled(boolean disabled)
/*     */   {
/* 422 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDisabled()
/*     */   {
/* 428 */     notSupported();
/*     */     
/* 430 */     return false;
/*     */   }
/*     */   
/*     */   public String getPluginVersion()
/*     */   {
/* 435 */     notSupported();
/*     */     
/* 437 */     return null;
/*     */   }
/*     */   
/*     */   public PluginConfig getPluginconfig()
/*     */   {
/* 442 */     RPPluginConfig res = (RPPluginConfig)this._dispatcher.dispatch(new RPRequest(this, "getPluginconfig", null)).getResponse();
/*     */     
/* 444 */     res._setRemote(this._dispatcher);
/*     */     
/* 446 */     return res;
/*     */   }
/*     */   
/*     */   public PluginConfigUIFactory getPluginConfigUIFactory()
/*     */   {
/* 451 */     notSupported();
/*     */     
/* 453 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public ClassLoader getPluginClassLoader()
/*     */   {
/* 459 */     notSupported();
/*     */     
/* 461 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PluginInterface getLocalPluginInterface(Class plugin, String id)
/*     */   {
/* 469 */     notSupported();
/*     */     
/* 471 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public IPCInterface getIPC()
/*     */   {
/* 477 */     notSupported();
/*     */     
/* 479 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public UpdateManager getUpdateManager()
/*     */   {
/* 485 */     notSupported();
/*     */     
/* 487 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isUnloadable()
/*     */   {
/* 494 */     notSupported();
/*     */     
/* 496 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void unload()
/*     */     throws PluginException
/*     */   {
/* 504 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void reload()
/*     */     throws PluginException
/*     */   {
/* 512 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void uninstall()
/*     */     throws PluginException
/*     */   {
/* 520 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isInitialisationThread()
/*     */   {
/* 526 */     notSupported();
/*     */     
/* 528 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public ClientIDManager getClientIDManager()
/*     */   {
/* 534 */     notSupported();
/*     */     
/* 536 */     return null;
/*     */   }
/*     */   
/*     */   public ConnectionManager getConnectionManager()
/*     */   {
/* 541 */     notSupported();
/* 542 */     return null;
/*     */   }
/*     */   
/*     */   public MessageManager getMessageManager() {
/* 546 */     notSupported();
/* 547 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DistributedDatabase getDistributedDatabase()
/*     */   {
/* 554 */     notSupported();
/* 555 */     return null;
/*     */   }
/*     */   
/*     */   public PlatformManager getPlatformManager()
/*     */   {
/* 560 */     notSupported();
/* 561 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(PluginListener l)
/*     */   {
/* 568 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(PluginListener l)
/*     */   {
/* 575 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void firePluginEvent(PluginEvent event)
/*     */   {
/* 582 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addEventListener(PluginEventListener l)
/*     */   {
/* 589 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeEventListener(PluginEventListener l)
/*     */   {
/* 596 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 601 */   public ConfigSection[] getConfigSections() { return null; }
/*     */   
/*     */   public MainlineDHTManager getMainlineDHTManager() {
/* 604 */     notSupported();return null; }
/* 605 */   public PluginState getPluginState() { notSupported();return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/RPPluginInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */