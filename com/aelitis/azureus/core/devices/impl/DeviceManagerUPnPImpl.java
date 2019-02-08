/*      */ package com.aelitis.azureus.core.devices.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.content.AzureusContentDownload;
/*      */ import com.aelitis.azureus.core.content.AzureusContentFile;
/*      */ import com.aelitis.azureus.core.content.AzureusContentFilter;
/*      */ import com.aelitis.azureus.core.devices.DeviceManager.UnassociatedDevice;
/*      */ import com.aelitis.azureus.core.devices.DeviceMediaRenderer;
/*      */ import com.aelitis.azureus.core.util.UUIDGenerator;
/*      */ import com.aelitis.net.upnp.UPnP;
/*      */ import com.aelitis.net.upnp.UPnPAdapter;
/*      */ import com.aelitis.net.upnp.UPnPDevice;
/*      */ import com.aelitis.net.upnp.UPnPFactory;
/*      */ import com.aelitis.net.upnp.UPnPListener;
/*      */ import com.aelitis.net.upnp.UPnPRootDevice;
/*      */ import com.aelitis.net.upnp.UPnPRootDeviceListener;
/*      */ import com.aelitis.net.upnp.UPnPSSDP;
/*      */ import com.aelitis.net.upnp.UPnPSSDPListener;
/*      */ import com.aelitis.net.upnp.UPnPService;
/*      */ import com.aelitis.net.upnp.services.UPnPOfflineDownloader;
/*      */ import com.aelitis.net.upnp.services.UPnPWANConnection;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.NetworkInterface;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.plugins.PluginEvent;
/*      */ import org.gudy.azureus2.plugins.PluginEventListener;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginListener;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*      */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageRequest;
/*      */ import org.gudy.azureus2.plugins.utils.Formatters;
/*      */ import org.gudy.azureus2.plugins.utils.UTTimer;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*      */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocument;
/*      */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentException;
/*      */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentFactory;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ipc.IPCInterfaceImpl;
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
/*      */ public class DeviceManagerUPnPImpl
/*      */ {
/*   67 */   private static final Object KEY_ROOT_DEVICE = new Object();
/*      */   
/*      */   private DeviceManagerImpl manager;
/*      */   
/*      */   private PluginInterface plugin_interface;
/*      */   
/*      */   private UPnP upnp;
/*      */   private TorrentAttribute ta_category;
/*      */   private volatile IPCInterface upnpav_ipc;
/*   76 */   private Map<InetAddress, String> unassociated_devices = new HashMap();
/*      */   
/*   78 */   private Set<String> access_logs = new HashSet();
/*      */   
/*      */ 
/*      */ 
/*      */   protected DeviceManagerUPnPImpl(DeviceManagerImpl _manager)
/*      */   {
/*   84 */     this.manager = _manager;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void initialise()
/*      */   {
/*   90 */     this.plugin_interface = PluginInitializer.getDefaultInterface();
/*      */     
/*   92 */     this.ta_category = this.plugin_interface.getTorrentManager().getAttribute("Category");
/*      */     
/*   94 */     this.plugin_interface.addListener(new PluginListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void initializationComplete()
/*      */       {
/*      */ 
/*      */ 
/*  103 */         new AEThread2("DMUPnPAsyncStart", true)
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/*  108 */             DeviceManagerUPnPImpl.this.startUp();
/*      */           }
/*      */         }.start();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void closedownInitiated() {}
/*      */       
/*      */ 
/*      */ 
/*      */       public void closedownComplete() {}
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected DeviceManagerImpl getManager()
/*      */   {
/*  128 */     return this.manager;
/*      */   }
/*      */   
/*      */ 
/*      */   protected TorrentAttribute getCategoryAttibute()
/*      */   {
/*  134 */     return this.ta_category;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void startUp()
/*      */   {
/*  140 */     UPnPAdapter adapter = new UPnPAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public SimpleXMLParserDocument parseXML(String data)
/*      */         throws SimpleXMLParserDocumentException
/*      */       {
/*      */ 
/*      */ 
/*  149 */         return DeviceManagerUPnPImpl.this.plugin_interface.getUtilities().getSimpleXMLParserDocumentFactory().create(data);
/*      */       }
/*      */       
/*      */ 
/*      */       public ResourceDownloaderFactory getResourceDownloaderFactory()
/*      */       {
/*  155 */         return DeviceManagerUPnPImpl.this.plugin_interface.getUtilities().getResourceDownloaderFactory();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public UTTimer createTimer(String name)
/*      */       {
/*  162 */         return DeviceManagerUPnPImpl.this.plugin_interface.getUtilities().createTimer(name);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void createThread(String name, Runnable runnable)
/*      */       {
/*  170 */         DeviceManagerUPnPImpl.this.plugin_interface.getUtilities().createThread(name, runnable);
/*      */       }
/*      */       
/*      */ 
/*      */       public Comparator getAlphanumericComparator()
/*      */       {
/*  176 */         return DeviceManagerUPnPImpl.this.plugin_interface.getUtilities().getFormatters().getAlphanumericComparator(true);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void log(Throwable e)
/*      */       {
/*  183 */         Debug.printStackTrace(e);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void trace(String str) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void log(String str) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public String getTraceDir()
/*      */       {
/*  203 */         return DeviceManagerUPnPImpl.this.plugin_interface.getPluginDirectoryName();
/*      */       }
/*      */     };
/*      */     try
/*      */     {
/*  208 */       this.upnp = UPnPFactory.getSingleton(adapter, null);
/*      */       
/*      */ 
/*  211 */       this.upnp.addRootDeviceListener(new UPnPListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public boolean deviceDiscovered(String USN, URL location)
/*      */         {
/*      */ 
/*      */ 
/*  219 */           return true;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void rootDeviceFound(UPnPRootDevice device)
/*      */         {
/*  226 */           DeviceManagerUPnPImpl.this.handleDevice(device, true);
/*      */         }
/*      */         
/*  229 */       });
/*  230 */       this.upnp.getSSDP().addListener(new UPnPSSDPListener()
/*      */       {
/*      */ 
/*  233 */         private Map<InetAddress, Boolean> liveness_map = new HashMap();
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
/*      */         public void receivedResult(NetworkInterface network_interface, InetAddress local_address, InetAddress originator, String USN, URL location, String ST, String AL) {}
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
/*      */         public void receivedNotify(NetworkInterface network_interface, InetAddress local_address, InetAddress originator, String USN, URL location, String NT, String NTS)
/*      */         {
/*  257 */           alive(originator, !NTS.contains("byebye"));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public String[] receivedSearch(NetworkInterface network_interface, InetAddress local_address, InetAddress originator, String ST)
/*      */         {
/*  267 */           alive(originator, true);
/*      */           
/*  269 */           return null;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void interfaceChanged(NetworkInterface network_interface) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         private void alive(InetAddress address, boolean alive)
/*      */         {
/*  283 */           synchronized (this.liveness_map)
/*      */           {
/*  285 */             Boolean b = (Boolean)this.liveness_map.get(address);
/*      */             
/*  287 */             if ((b != null) && (b.booleanValue() == alive))
/*      */             {
/*  289 */               return;
/*      */             }
/*      */             
/*  292 */             this.liveness_map.put(address, Boolean.valueOf(alive));
/*      */           }
/*      */           
/*  295 */           DeviceImpl[] devices = DeviceManagerUPnPImpl.this.manager.getDevices();
/*      */           
/*  297 */           for (DeviceImpl d : devices)
/*      */           {
/*  299 */             if ((d instanceof DeviceMediaRendererImpl))
/*      */             {
/*  301 */               DeviceMediaRendererImpl r = (DeviceMediaRendererImpl)d;
/*      */               
/*  303 */               InetAddress device_address = r.getAddress();
/*      */               
/*  305 */               if ((device_address != null) && (device_address.equals(address)))
/*      */               {
/*  307 */                 if (r.isAlive() != alive)
/*      */                 {
/*  309 */                   if (alive)
/*      */                   {
/*  311 */                     r.alive();
/*      */                   }
/*      */                   else
/*      */                   {
/*  315 */                     r.dead();
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  326 */       this.manager.log("UPnP device manager failed", e);
/*      */     }
/*      */     try
/*      */     {
/*  330 */       this.plugin_interface.addEventListener(new PluginEventListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void handleEvent(PluginEvent ev)
/*      */         {
/*      */ 
/*  337 */           int type = ev.getType();
/*      */           
/*  339 */           if ((type == 8) || (type == 9))
/*      */           {
/*      */ 
/*  342 */             PluginInterface pi = (PluginInterface)ev.getValue();
/*      */             
/*  344 */             if (pi.getPluginID().equals("azupnpav"))
/*      */             {
/*  346 */               if (type == 8)
/*      */               {
/*  348 */                 DeviceManagerUPnPImpl.this.upnpav_ipc = pi.getIPC();
/*      */                 
/*  350 */                 DeviceManagerUPnPImpl.this.addListener(pi);
/*      */               }
/*      */               else
/*      */               {
/*  354 */                 DeviceManagerUPnPImpl.this.upnpav_ipc = null;
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*      */         }
/*  360 */       });
/*  361 */       PluginInterface pi = this.plugin_interface.getPluginManager().getPluginInterfaceByID("azupnpav");
/*      */       
/*  363 */       if (pi == null)
/*      */       {
/*  365 */         this.manager.log("No UPnPAV plugin found");
/*      */       }
/*      */       else
/*      */       {
/*  369 */         this.upnpav_ipc = pi.getIPC();
/*      */         
/*  371 */         addListener(pi);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  375 */       this.manager.log("Failed to hook into UPnPAV", e);
/*      */     }
/*      */     
/*  378 */     this.manager.UPnPManagerStarted();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void addListener(PluginInterface pi)
/*      */   {
/*      */     try
/*      */     {
/*  386 */       IPCInterface my_ipc = new IPCInterfaceImpl(new Object()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public Map<String, Object> browseReceived(TrackerWebPageRequest request, Map<String, Object> browser_args)
/*      */         {
/*      */ 
/*      */ 
/*  395 */           Map headers = request.getHeaders();
/*      */           
/*  397 */           String user_agent = (String)headers.get("user-agent");
/*  398 */           String client_info = (String)headers.get("x-av-client-info");
/*      */           
/*  400 */           InetSocketAddress client_address = request.getClientAddress2();
/*      */           
/*  402 */           DeviceMediaRenderer explicit_renderer = null;
/*      */           
/*  404 */           boolean handled = false;
/*      */           
/*  406 */           if (user_agent != null)
/*      */           {
/*  408 */             String lc_agent = user_agent.toLowerCase();
/*      */             
/*  410 */             if (lc_agent.contains("playstation 3"))
/*      */             {
/*  412 */               DeviceManagerUPnPImpl.this.handlePS3(client_address);
/*      */               
/*  414 */               handled = true;
/*      */             }
/*  416 */             else if (lc_agent.contains("xbox"))
/*      */             {
/*  418 */               DeviceManagerUPnPImpl.this.handleXBox(client_address);
/*      */               
/*  420 */               handled = true;
/*      */             }
/*  422 */             else if (lc_agent.contains("nintendo wii"))
/*      */             {
/*  424 */               DeviceManagerUPnPImpl.this.handleWii(client_address);
/*      */               
/*  426 */               handled = true;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*  431 */           if (client_info != null)
/*      */           {
/*  433 */             String lc_info = client_info.toLowerCase();
/*      */             
/*  435 */             if (lc_info.contains("playstation 3"))
/*      */             {
/*  437 */               DeviceManagerUPnPImpl.this.handlePS3(client_address);
/*      */               
/*  439 */               handled = true;
/*      */             }
/*  441 */             else if ((lc_info.contains("azureus")) || (lc_info.contains("vuze")))
/*      */             {
/*  443 */               explicit_renderer = DeviceManagerUPnPImpl.this.handleVuzeMSBrowser(client_address, client_info);
/*      */               
/*  445 */               handled = true;
/*      */             }
/*      */           }
/*      */           
/*  449 */           if (!handled)
/*      */           {
/*  451 */             handled = DeviceManagerUPnPImpl.this.manager.browseReceived(request, browser_args);
/*      */           }
/*      */           
/*  454 */           if (!handled)
/*      */           {
/*  456 */             String source = (String)browser_args.get("source");
/*      */             
/*  458 */             if ((source != null) && (source.equalsIgnoreCase("http")))
/*      */             {
/*  460 */               DeviceManagerUPnPImpl.this.handleBrowser(client_address);
/*      */               
/*  462 */               handled = true;
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
/*  476 */           DeviceImpl[] devices = DeviceManagerUPnPImpl.this.manager.getDevices();
/*      */           
/*  478 */           final List<DeviceMediaRendererImpl> browse_devices = new ArrayList();
/*      */           
/*  480 */           boolean restrict_access = false;
/*      */           
/*  482 */           for (DeviceImpl device : devices)
/*      */           {
/*  484 */             if ((device instanceof DeviceMediaRendererImpl))
/*      */             {
/*  486 */               DeviceMediaRendererImpl renderer = (DeviceMediaRendererImpl)device;
/*      */               
/*  488 */               if ((explicit_renderer == null) || 
/*      */               
/*  490 */                 (renderer == explicit_renderer))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  496 */                 InetAddress device_address = renderer.getAddress();
/*      */                 try
/*      */                 {
/*  499 */                   if (device_address != null)
/*      */                   {
/*      */ 
/*      */ 
/*  503 */                     if (device_address.equals(client_address.getAddress()))
/*      */                     {
/*  505 */                       if (renderer.canFilterFilesView())
/*      */                       {
/*  507 */                         boolean skip = false;
/*      */                         
/*  509 */                         if (renderer.canRestrictAccess())
/*      */                         {
/*  511 */                           String restriction = renderer.getAccessRestriction().trim();
/*      */                           
/*  513 */                           if (restriction.length() > 0)
/*      */                           {
/*  515 */                             String x = client_address.getAddress().getHostAddress();
/*      */                             
/*  517 */                             skip = true;
/*      */                             
/*  519 */                             String[] ips = restriction.split(",");
/*      */                             
/*  521 */                             for (String ip : ips)
/*      */                             {
/*  523 */                               if (ip.startsWith("-"))
/*      */                               {
/*  525 */                                 ip = ip.substring(1);
/*      */                                 
/*  527 */                                 if (ip.equals(x)) {
/*      */                                   break;
/*      */                                 }
/*      */                               }
/*      */                               else
/*      */                               {
/*  533 */                                 if (ip.startsWith("+"))
/*      */                                 {
/*  535 */                                   ip = ip.substring(1);
/*      */                                 }
/*      */                                 
/*  538 */                                 if (ip.equals(x))
/*      */                                 {
/*  540 */                                   skip = false;
/*      */                                   
/*  542 */                                   break;
/*      */                                 }
/*      */                               }
/*      */                             }
/*      */                           }
/*      */                         }
/*      */                         
/*  549 */                         if (skip)
/*      */                         {
/*  551 */                           restrict_access = true;
/*      */                           
/*  553 */                           String host = client_address.getAddress().getHostAddress();
/*      */                           
/*  555 */                           synchronized (DeviceManagerUPnPImpl.this.access_logs)
/*      */                           {
/*  557 */                             if (!DeviceManagerUPnPImpl.this.access_logs.contains(host))
/*      */                             {
/*  559 */                               DeviceManagerUPnPImpl.this.access_logs.add(host);
/*      */                               
/*  561 */                               DeviceManagerUPnPImpl.this.manager.log("Ignoring browse from " + host + " due to access restriction for '" + renderer.getName() + "'");
/*      */                             }
/*      */                           }
/*      */                         }
/*      */                         
/*  566 */                         browse_devices.add(renderer);
/*      */                         
/*  568 */                         renderer.browseReceived();
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable e) {
/*  574 */                   Debug.out(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*  579 */           Map<String, Object> result = new HashMap();
/*      */           
/*  581 */           if (browse_devices.size() > 0)
/*      */           {
/*  583 */             synchronized (DeviceManagerUPnPImpl.this.unassociated_devices)
/*      */             {
/*  585 */               DeviceManagerUPnPImpl.this.unassociated_devices.remove(client_address.getAddress());
/*      */             }
/*      */             
/*  588 */             final boolean f_restrict_access = restrict_access;
/*      */             
/*  590 */             result.put("filter", new AzureusContentFilter()
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */               public boolean isVisible(AzureusContentDownload download, Map<String, Object> browse_args)
/*      */               {
/*      */ 
/*      */ 
/*  599 */                 if (f_restrict_access)
/*      */                 {
/*  601 */                   return false;
/*      */                 }
/*      */                 
/*  604 */                 boolean visible = false;
/*      */                 
/*  606 */                 for (DeviceUPnPImpl device : browse_devices)
/*      */                 {
/*  608 */                   if (device.isVisible(download))
/*      */                   {
/*  610 */                     visible = true;
/*      */                   }
/*      */                 }
/*      */                 
/*  614 */                 return visible;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */               public boolean isVisible(AzureusContentFile file, Map<String, Object> browse_args)
/*      */               {
/*  622 */                 if (f_restrict_access)
/*      */                 {
/*  624 */                   return false;
/*      */                 }
/*      */                 
/*  627 */                 boolean visible = false;
/*      */                 
/*  629 */                 for (DeviceUPnPImpl device : browse_devices)
/*      */                 {
/*  631 */                   if (device.isVisible(file))
/*      */                   {
/*  633 */                     visible = true;
/*      */                   }
/*      */                 }
/*      */                 
/*  637 */                 return visible;
/*      */               }
/*      */               
/*      */             });
/*      */           }
/*  642 */           else if (request.getHeader().substring(0, 4).equalsIgnoreCase("POST"))
/*      */           {
/*  644 */             synchronized (DeviceManagerUPnPImpl.this.unassociated_devices)
/*      */             {
/*  646 */               DeviceManagerUPnPImpl.this.unassociated_devices.put(client_address.getAddress(), user_agent);
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*  651 */           return result;
/*      */         }
/*      */       });
/*      */       
/*  655 */       if (this.upnpav_ipc.canInvoke("addBrowseListener", new Object[] { my_ipc }))
/*      */       {
/*  657 */         DeviceImpl[] devices = this.manager.getDevices();
/*      */         
/*  659 */         for (DeviceImpl device : devices)
/*      */         {
/*  661 */           if ((device instanceof DeviceUPnPImpl))
/*      */           {
/*  663 */             DeviceUPnPImpl u_d = (DeviceUPnPImpl)device;
/*      */             
/*  665 */             u_d.resetUPNPAV();
/*      */           }
/*      */         }
/*      */         
/*  669 */         this.upnpav_ipc.invoke("addBrowseListener", new Object[] { my_ipc });
/*      */       }
/*      */       else
/*      */       {
/*  673 */         this.manager.log("UPnPAV plugin needs upgrading");
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  677 */       this.manager.log("Failed to hook into UPnPAV", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void injectDiscoveryCache(Map cache)
/*      */   {
/*      */     try
/*      */     {
/*  686 */       this.upnp.injectDiscoveryCache(cache);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  690 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public DeviceManager.UnassociatedDevice[] getUnassociatedDevices()
/*      */   {
/*  697 */     List<DeviceManager.UnassociatedDevice> result = new ArrayList();
/*      */     
/*      */     Map<InetAddress, String> ud;
/*      */     
/*  701 */     synchronized (this.unassociated_devices)
/*      */     {
/*  703 */       ud = new HashMap(this.unassociated_devices);
/*      */     }
/*      */     
/*  706 */     DeviceImpl[] devices = this.manager.getDevices();
/*      */     
/*  708 */     for (final Map.Entry<InetAddress, String> entry : ud.entrySet())
/*      */     {
/*  710 */       InetAddress address = (InetAddress)entry.getKey();
/*      */       
/*  712 */       boolean already_assoc = false;
/*      */       
/*  714 */       for (DeviceImpl d : devices)
/*      */       {
/*  716 */         if ((d instanceof DeviceMediaRendererImpl))
/*      */         {
/*  718 */           DeviceMediaRendererImpl r = (DeviceMediaRendererImpl)d;
/*      */           
/*  720 */           InetAddress device_address = r.getAddress();
/*      */           
/*  722 */           if ((d.isAlive()) && (device_address != null) && (device_address.equals(address)))
/*      */           {
/*  724 */             already_assoc = true;
/*      */             
/*  726 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  731 */       if (!already_assoc)
/*      */       {
/*  733 */         result.add(new DeviceManager.UnassociatedDevice()
/*      */         {
/*      */ 
/*      */           public InetAddress getAddress()
/*      */           {
/*      */ 
/*  739 */             return (InetAddress)entry.getKey();
/*      */           }
/*      */           
/*      */ 
/*      */           public String getDescription()
/*      */           {
/*  745 */             return (String)entry.getValue();
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/*  751 */     return (DeviceManager.UnassociatedDevice[])result.toArray(new DeviceManager.UnassociatedDevice[result.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */   public PluginInterface getPluginInterface()
/*      */   {
/*  757 */     return this.plugin_interface;
/*      */   }
/*      */   
/*      */ 
/*      */   protected IPCInterface getUPnPAVIPC()
/*      */   {
/*  763 */     return this.upnpav_ipc;
/*      */   }
/*      */   
/*      */ 
/*      */   public void search()
/*      */   {
/*  769 */     if (this.upnp != null)
/*      */     {
/*      */ 
/*      */ 
/*  773 */       UPnPRootDevice[] devices = this.upnp.getRootDevices();
/*      */       
/*  775 */       for (UPnPRootDevice device : devices)
/*      */       {
/*  777 */         handleDevice(device, false);
/*      */       }
/*      */       
/*  780 */       String[] STs = { "upnp:rootdevice", "urn:schemas-upnp-org:device:MediaRenderer:1", "urn:schemas-upnp-org:service:ContentDirectory:1" };
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  786 */       this.upnp.search(STs);
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
/*      */   protected void handleXBox(InetSocketAddress address)
/*      */   {
/*  799 */     DeviceImpl[] devices = this.manager.getDevices();
/*      */     
/*  801 */     boolean found = false;
/*      */     
/*  803 */     for (DeviceImpl device : devices)
/*      */     {
/*  805 */       if ((device instanceof DeviceMediaRendererImpl))
/*      */       {
/*  807 */         DeviceMediaRendererImpl renderer = (DeviceMediaRendererImpl)device;
/*      */         
/*  809 */         if (device.getRendererSpecies() == 2)
/*      */         {
/*  811 */           found = true;
/*      */           
/*  813 */           if (!device.isAlive())
/*      */           {
/*  815 */             renderer.setAddress(address.getAddress());
/*      */             
/*  817 */             device.alive();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  823 */     if (!found)
/*      */     {
/*  825 */       this.manager.addDevice(new DeviceMediaRendererImpl(this.manager, "Xbox 360"));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void handlePS3(InetSocketAddress address)
/*      */   {
/*  833 */     handleGeneric(address, "ps3", "PS3");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void handleWii(InetSocketAddress address)
/*      */   {
/*  840 */     handleGeneric(address, "wii", "Wii");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void handleBrowser(InetSocketAddress address)
/*      */   {
/*  847 */     handleGeneric(address, "browser", "Browser");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DeviceMediaRenderer handleVuzeMSBrowser(InetSocketAddress address, String info)
/*      */   {
/*  855 */     String[] bits = info.split(";");
/*      */     
/*  857 */     String client = "";
/*      */     
/*  859 */     for (String bit : bits)
/*      */     {
/*  861 */       String[] temp = bit.split("=");
/*      */       
/*  863 */       if ((temp.length == 2) && (temp[0].trim().equalsIgnoreCase("mn")))
/*      */       {
/*  865 */         client = temp[1].trim();
/*      */         
/*  867 */         if (client.startsWith("\""))
/*      */         {
/*  869 */           client = client.substring(1);
/*      */         }
/*      */         
/*  872 */         if (client.endsWith("\""))
/*      */         {
/*  874 */           client = client.substring(0, client.length() - 1);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  879 */     if (client.length() == 0)
/*      */     {
/*  881 */       client = "Vuze on " + address.getAddress().getHostAddress();
/*      */     }
/*      */     
/*  884 */     DeviceMediaRenderer result = handleGeneric(address, "vuze-ms-browser." + client, client);
/*      */     
/*  886 */     result.setTranscodeRequirement(1);
/*      */     
/*  888 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DeviceMediaRenderer handleGeneric(InetSocketAddress address, String unique_name, String display_name)
/*      */   {
/*      */     String uid;
/*      */     
/*      */ 
/*  899 */     synchronized (this)
/*      */     {
/*      */       String un_key;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/*  908 */         un_key = Base32.encode(unique_name.getBytes("UTF-8"));
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  912 */         un_key = Base32.encode(unique_name.getBytes());
/*      */       }
/*      */       
/*  915 */       String new_key = "devices.upnp.uid2." + un_key;
/*      */       
/*  917 */       uid = COConfigurationManager.getStringParameter(new_key, "");
/*      */       
/*  919 */       if (uid.length() == 0)
/*      */       {
/*  921 */         String old_key = "devices.upnp.uid." + unique_name;
/*      */         
/*  923 */         uid = COConfigurationManager.getStringParameter(old_key, "");
/*      */         
/*  925 */         if (uid.length() > 0)
/*      */         {
/*  927 */           COConfigurationManager.setParameter(new_key, uid);
/*      */           
/*  929 */           COConfigurationManager.removeParameter(old_key);
/*      */         }
/*      */         else
/*      */         {
/*  933 */           uid = UUIDGenerator.generateUUIDString();
/*      */           
/*  935 */           COConfigurationManager.setParameter(new_key, uid);
/*      */           
/*  937 */           COConfigurationManager.save();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  942 */     DeviceMediaRendererImpl newDevice = new DeviceMediaRendererImpl(this.manager, uid, unique_name, false, display_name);
/*      */     
/*  944 */     DeviceMediaRendererImpl device = (DeviceMediaRendererImpl)this.manager.addDevice(newDevice);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  952 */     device.setPersistentBooleanProperty("rend_no_ah", true);
/*      */     
/*  954 */     device.setAddress(address.getAddress());
/*      */     
/*  956 */     device.alive();
/*      */     
/*  958 */     return device;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void handleDevice(UPnPRootDevice root_device, boolean update_if_found)
/*      */   {
/*  966 */     if (!this.manager.getAutoSearch())
/*      */     {
/*  968 */       if (!this.manager.isExplicitSearch())
/*      */       {
/*  970 */         return;
/*      */       }
/*      */     }
/*      */     
/*  974 */     handleDevice(root_device.getDevice(), update_if_found);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void handleDevice(UPnPDevice device, boolean update_if_found)
/*      */   {
/*  982 */     UPnPService[] services = device.getServices();
/*      */     
/*  984 */     List<DeviceUPnPImpl> new_devices = new ArrayList();
/*      */     
/*  986 */     List<UPnPWANConnection> igd_services = new ArrayList();
/*      */     
/*  988 */     for (UPnPService service : services)
/*      */     {
/*  990 */       String service_type = service.getServiceType();
/*      */       
/*  992 */       if ((service_type.equalsIgnoreCase("urn:schemas-upnp-org:service:WANIPConnection:1")) || (service_type.equalsIgnoreCase("urn:schemas-upnp-org:service:WANPPPConnection:1")))
/*      */       {
/*      */ 
/*  995 */         UPnPWANConnection wan_service = (UPnPWANConnection)service.getSpecificService();
/*      */         
/*  997 */         igd_services.add(wan_service);
/*      */       }
/*  999 */       else if (service_type.equals("urn:schemas-upnp-org:service:ContentDirectory:1"))
/*      */       {
/* 1001 */         new_devices.add(new DeviceContentDirectoryImpl(this.manager, device, service));
/*      */       }
/* 1003 */       else if (service_type.equals("urn:schemas-upnp-org:service:VuzeOfflineDownloaderService:1"))
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/* 1008 */           PluginInterface od_pi = this.plugin_interface.getPluginManager().getPluginInterfaceByID("azofflinedownloader");
/*      */           
/* 1010 */           if (od_pi != null)
/*      */           {
/* 1012 */             String local_usn = (String)od_pi.getIPC().invoke("getUSN", new Object[0]);
/*      */             
/* 1014 */             String od_usn = device.getRootDevice().getUSN();
/*      */             
/*      */ 
/*      */ 
/* 1018 */             int pos = od_usn.indexOf("::upnp:rootdevice");
/*      */             
/* 1020 */             if (pos > 0)
/*      */             {
/* 1022 */               od_usn = od_usn.substring(0, pos);
/*      */             }
/*      */             
/* 1025 */             if (od_usn.equals(local_usn)) {
/*      */               continue;
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1032 */           Debug.out(e);
/*      */         }
/*      */         
/* 1035 */         UPnPOfflineDownloader downloader = (UPnPOfflineDownloader)service.getSpecificService();
/*      */         
/* 1037 */         if (downloader != null)
/*      */         {
/* 1039 */           new_devices.add(new DeviceOfflineDownloaderImpl(this.manager, device, downloader));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1044 */     if (igd_services.size() > 0)
/*      */     {
/* 1046 */       new_devices.add(new DeviceInternetGatewayImpl(this.manager, device, igd_services));
/*      */     }
/*      */     
/* 1049 */     if (device.getDeviceType().equals("urn:schemas-upnp-org:device:MediaRenderer:1"))
/*      */     {
/* 1051 */       new_devices.add(new DeviceMediaRendererImpl(this.manager, device));
/*      */     }
/*      */     
/* 1054 */     for (DeviceUPnPImpl new_device : new_devices)
/*      */     {
/*      */ 
/*      */ 
/* 1058 */       DeviceImpl existing = this.manager.getDevice(new_device.getID());
/*      */       DeviceImpl actual_device;
/* 1060 */       final DeviceImpl actual_device; if ((!update_if_found) && (existing != null))
/*      */       {
/* 1062 */         actual_device = existing;
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/* 1069 */         actual_device = this.manager.addDevice(new_device);
/*      */       }
/*      */       
/* 1072 */       UPnPRootDevice current_root = device.getRootDevice();
/*      */       
/* 1074 */       UPnPRootDevice existing_root = (UPnPRootDevice)actual_device.getTransientProperty(KEY_ROOT_DEVICE);
/*      */       
/* 1076 */       if (current_root != existing_root)
/*      */       {
/* 1078 */         actual_device.setTransientProperty(KEY_ROOT_DEVICE, current_root);
/*      */         
/* 1080 */         current_root.addListener(new UPnPRootDeviceListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void lost(UPnPRootDevice root, boolean replaced)
/*      */           {
/*      */ 
/*      */ 
/* 1088 */             if (!replaced)
/*      */             {
/* 1090 */               actual_device.dead();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/* 1097 */     for (UPnPDevice d : device.getSubDevices())
/*      */     {
/* 1099 */       handleDevice(d, update_if_found);
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/DeviceManagerUPnPImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */