/*      */ package com.aelitis.azureus.core.devices.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*      */ import com.aelitis.azureus.core.content.AzureusContentDownload;
/*      */ import com.aelitis.azureus.core.content.AzureusContentFile;
/*      */ import com.aelitis.azureus.core.content.AzureusPlatformContentDirectory;
/*      */ import com.aelitis.azureus.core.devices.Device.browseLocation;
/*      */ import com.aelitis.azureus.core.devices.DeviceManager.UnassociatedDevice;
/*      */ import com.aelitis.azureus.core.devices.DeviceUPnP;
/*      */ import com.aelitis.azureus.core.devices.TranscodeException;
/*      */ import com.aelitis.azureus.core.devices.TranscodeFile;
/*      */ import com.aelitis.azureus.core.devices.TranscodeJob;
/*      */ import com.aelitis.azureus.core.devices.TranscodeProfile;
/*      */ import com.aelitis.azureus.core.devices.TranscodeTarget;
/*      */ import com.aelitis.azureus.core.devices.TranscodeTargetListener;
/*      */ import com.aelitis.azureus.core.download.DiskManagerFileInfoStream;
/*      */ import com.aelitis.azureus.core.download.DiskManagerFileInfoStream.StreamFactory;
/*      */ import com.aelitis.azureus.core.download.DiskManagerFileInfoStream.StreamFactory.StreamDetails;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagListener;
/*      */ import com.aelitis.azureus.core.tag.TagManager;
/*      */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*      */ import com.aelitis.azureus.core.tag.TagType;
/*      */ import com.aelitis.azureus.core.tag.Taggable;
/*      */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*      */ import com.aelitis.azureus.core.util.UUIDGenerator;
/*      */ import com.aelitis.azureus.util.PlayUtils;
/*      */ import com.aelitis.net.upnp.UPnPDevice;
/*      */ import com.aelitis.net.upnp.UPnPDeviceImage;
/*      */ import com.aelitis.net.upnp.UPnPRootDevice;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.net.InetAddress;
/*      */ import java.net.URL;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.WeakHashMap;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadAttributeListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*      */ import org.gudy.azureus2.plugins.download.DownloadManagerListener;
/*      */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
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
/*      */ public abstract class DeviceUPnPImpl
/*      */   extends DeviceImpl
/*      */   implements DeviceUPnP, TranscodeTargetListener, DownloadManagerListener
/*      */ {
/*   77 */   private static final Object UPNPAV_FILE_KEY = new Object();
/*      */   
/*   79 */   private static final Map<String, AzureusContentFile> acf_map = new WeakHashMap();
/*      */   private final String MY_ACF_KEY;
/*      */   private final DeviceManagerUPnPImpl upnp_manager;
/*      */   private volatile UPnPDevice device_may_be_null;
/*      */   
/*      */   protected static String getDisplayName(UPnPDevice device) {
/*   85 */     UPnPDevice root = device.getRootDevice().getDevice();
/*      */     
/*   87 */     String fn = root.getFriendlyName();
/*      */     
/*   89 */     if ((fn == null) || (fn.length() == 0))
/*      */     {
/*   91 */       fn = device.getFriendlyName();
/*      */     }
/*      */     
/*   94 */     String dn = root.getModelName();
/*      */     
/*   96 */     if ((dn == null) || (dn.length() == 0))
/*      */     {
/*   98 */       dn = device.getModelName();
/*      */     }
/*      */     
/*  101 */     if ((dn != null) && (dn.length() > 0))
/*      */     {
/*  103 */       if ((!fn.contains(dn)) && ((!dn.contains("Azureus")) || (dn.contains("Vuze"))))
/*      */       {
/*  105 */         fn = fn + " (" + dn + ")";
/*      */       }
/*      */     }
/*      */     
/*  109 */     return fn;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private IPCInterface upnpav_ipc;
/*      */   
/*      */ 
/*      */ 
/*      */   private TranscodeProfile dynamic_transcode_profile;
/*      */   
/*      */ 
/*      */ 
/*      */   private Map<String, AzureusContentFile> dynamic_xcode_map;
/*      */   
/*      */ 
/*      */ 
/*      */   protected DeviceUPnPImpl(DeviceManagerImpl _manager, UPnPDevice _device, int _type)
/*      */   {
/*  129 */     super(_manager, _type, _type + "/" + _device.getRootDevice().getUSN(), getDisplayName(_device), false);
/*      */     
/*  131 */     this.upnp_manager = _manager.getUPnPManager();
/*  132 */     setUPnPDevice(_device);
/*      */     
/*  134 */     this.MY_ACF_KEY = getACFKey();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DeviceUPnPImpl(DeviceManagerImpl _manager, int _type, String _classification)
/*      */   {
/*  144 */     super(_manager, _type, UUIDGenerator.generateUUIDString(), _classification, true);
/*      */     
/*  146 */     this.upnp_manager = _manager.getUPnPManager();
/*      */     
/*  148 */     this.MY_ACF_KEY = getACFKey();
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
/*      */   protected DeviceUPnPImpl(DeviceManagerImpl _manager, int _type, String _uuid, String _classification, boolean _manual, String _name)
/*      */   {
/*  161 */     super(_manager, _type, _uuid == null ? UUIDGenerator.generateUUIDString() : _uuid, _classification, _manual, _name);
/*      */     
/*  163 */     this.upnp_manager = _manager.getUPnPManager();
/*      */     
/*  165 */     this.MY_ACF_KEY = getACFKey();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DeviceUPnPImpl(DeviceManagerImpl _manager, int _type, String _uuid, String _classification, boolean _manual)
/*      */   {
/*  177 */     super(_manager, _type, _uuid, _classification, _manual);
/*      */     
/*  179 */     this.upnp_manager = _manager.getUPnPManager();
/*      */     
/*  181 */     this.MY_ACF_KEY = getACFKey();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DeviceUPnPImpl(DeviceManagerImpl _manager, Map _map)
/*      */     throws IOException
/*      */   {
/*  191 */     super(_manager, _map);
/*      */     
/*  193 */     this.upnp_manager = _manager.getUPnPManager();
/*      */     
/*  195 */     this.MY_ACF_KEY = getACFKey();
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getACFKey()
/*      */   {
/*  201 */     return "DeviceUPnPImpl:device:" + getID();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean updateFrom(DeviceImpl _other, boolean _is_alive)
/*      */   {
/*  210 */     if (!super.updateFrom(_other, _is_alive))
/*      */     {
/*  212 */       return false;
/*      */     }
/*      */     
/*  215 */     if (!(_other instanceof DeviceUPnPImpl))
/*      */     {
/*  217 */       Debug.out("Inconsistent");
/*      */       
/*  219 */       return false;
/*      */     }
/*      */     
/*  222 */     DeviceUPnPImpl other = (DeviceUPnPImpl)_other;
/*      */     
/*  224 */     setUPnPDevice(other.device_may_be_null);
/*      */     
/*  226 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void initialise()
/*      */   {
/*  233 */     super.initialise();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void UPnPInitialised() {}
/*      */   
/*      */ 
/*      */ 
/*      */   protected void destroy()
/*      */   {
/*  245 */     super.destroy();
/*      */   }
/*      */   
/*      */ 
/*      */   protected DeviceManagerUPnPImpl getUPnPDeviceManager()
/*      */   {
/*  251 */     return this.upnp_manager;
/*      */   }
/*      */   
/*      */ 
/*      */   public UPnPDevice getUPnPDevice()
/*      */   {
/*  257 */     return this.device_may_be_null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setUPnPDevice(UPnPDevice device)
/*      */   {
/*  264 */     this.device_may_be_null = device;
/*  265 */     if (device != null)
/*      */     {
/*  267 */       setAddress(getAddress());
/*      */     }
/*  269 */     setDirty(false);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isBrowsable()
/*      */   {
/*  275 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public Device.browseLocation[] getBrowseLocations()
/*      */   {
/*  281 */     List<Device.browseLocation> locs = new ArrayList();
/*      */     
/*  283 */     UPnPDevice device = this.device_may_be_null;
/*      */     
/*  285 */     if (device != null)
/*      */     {
/*  287 */       URL presentation = getPresentationURL(device);
/*      */       
/*  289 */       if (presentation != null)
/*      */       {
/*  291 */         locs.add(new DeviceImpl.browseLocationImpl("device.upnp.present_url", presentation));
/*      */       }
/*      */       
/*  294 */       int userMode = COConfigurationManager.getIntParameter("User Mode");
/*      */       
/*  296 */       if (userMode > 1)
/*      */       {
/*  298 */         locs.add(new DeviceImpl.browseLocationImpl("device.upnp.desc_url", device.getRootDevice().getLocation()));
/*      */       }
/*      */     }
/*      */     
/*  302 */     return (Device.browseLocation[])locs.toArray(new Device.browseLocation[locs.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canFilterFilesView()
/*      */   {
/*  308 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setFilterFilesView(boolean filter)
/*      */   {
/*  315 */     boolean existing = getFilterFilesView();
/*      */     
/*  317 */     if (existing != filter)
/*      */     {
/*  319 */       setPersistentBooleanProperty("rend_filter", filter);
/*      */       
/*  321 */       IPCInterface ipc = this.upnpav_ipc;
/*      */       
/*  323 */       if (ipc != null) {
/*      */         try
/*      */         {
/*  326 */           ipc.invoke("invalidateDirectory", new Object[0]);
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean getFilterFilesView()
/*      */   {
/*  337 */     return getPersistentBooleanProperty("rend_filter", true);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isLivenessDetectable()
/*      */   {
/*  343 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   protected URL getLocation()
/*      */   {
/*  349 */     UPnPDevice device = this.device_may_be_null;
/*      */     
/*  351 */     if (device != null)
/*      */     {
/*  353 */       UPnPRootDevice root = device.getRootDevice();
/*      */       
/*  355 */       return root.getLocation();
/*      */     }
/*      */     
/*  358 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canAssociate()
/*      */   {
/*  364 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void associate(DeviceManager.UnassociatedDevice assoc)
/*      */   {
/*  371 */     if (isAlive())
/*      */     {
/*  373 */       return;
/*      */     }
/*      */     
/*  376 */     setAddress(assoc.getAddress());
/*      */     
/*  378 */     alive();
/*      */   }
/*      */   
/*      */ 
/*      */   public InetAddress getAddress()
/*      */   {
/*      */     try
/*      */     {
/*  386 */       UPnPDevice device = this.device_may_be_null;
/*      */       
/*  388 */       if (device != null)
/*      */       {
/*  390 */         UPnPRootDevice root = device.getRootDevice();
/*      */         
/*  392 */         URL location = root.getLocation();
/*      */         
/*  394 */         return InetAddress.getByName(location.getHost());
/*      */       }
/*      */       
/*      */ 
/*  398 */       InetAddress address = (InetAddress)getTransientProperty("DeviceUPnPImpl:ip");
/*      */       
/*  400 */       if (address != null)
/*      */       {
/*  402 */         return address;
/*      */       }
/*      */       
/*  405 */       String last = getPersistentStringProperty("rend_ip");
/*      */       
/*  407 */       if ((last != null) && (last.length() > 0))
/*      */       {
/*  409 */         return InetAddress.getByName(last);
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  414 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/*      */ 
/*  418 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAddress(InetAddress address)
/*      */   {
/*  425 */     setTransientProperty("DeviceUPnPImpl:ip", address);
/*      */     
/*  427 */     setPersistentStringProperty("rend_ip", address.getHostAddress());
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canRestrictAccess()
/*      */   {
/*  433 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getAccessRestriction()
/*      */   {
/*  439 */     return getPersistentStringProperty("restrict_access", "");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAccessRestriction(String str)
/*      */   {
/*  446 */     setPersistentStringProperty("restrict_access", str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected URL getStreamURL(TranscodeFileImpl file)
/*      */   {
/*  453 */     return getStreamURL(file, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected URL getStreamURL(TranscodeFileImpl file, String host)
/*      */   {
/*  461 */     browseReceived();
/*      */     
/*  463 */     return super.getStreamURL(file, host);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String getMimeType(TranscodeFileImpl file)
/*      */   {
/*  470 */     browseReceived();
/*      */     
/*  472 */     return super.getMimeType(file);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void browseReceived()
/*      */   {
/*  478 */     IPCInterface ipc = this.upnp_manager.getUPnPAVIPC();
/*      */     
/*  480 */     if (ipc == null)
/*      */     {
/*  482 */       return;
/*      */     }
/*      */     
/*  485 */     TranscodeProfile default_profile = getDefaultTranscodeProfile();
/*      */     
/*  487 */     if (default_profile == null)
/*      */     {
/*  489 */       TranscodeProfile[] profiles = getTranscodeProfiles();
/*      */       
/*  491 */       for (TranscodeProfile p : profiles)
/*      */       {
/*  493 */         if (p.isStreamable())
/*      */         {
/*  495 */           default_profile = p;
/*      */           
/*  497 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  502 */     synchronized (this)
/*      */     {
/*  504 */       if (this.upnpav_ipc != null)
/*      */       {
/*  506 */         return;
/*      */       }
/*      */       
/*  509 */       this.upnpav_ipc = ipc;
/*      */       
/*  511 */       if ((default_profile != null) && (default_profile.isStreamable()))
/*      */       {
/*  513 */         this.dynamic_transcode_profile = default_profile;
/*      */       }
/*      */     }
/*      */     
/*  517 */     if ((this.dynamic_transcode_profile != null) && ((this instanceof TranscodeTarget)))
/*      */     {
/*      */ 
/*      */ 
/*  521 */       AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*      */       {
/*      */         public void azureusCoreRunning(AzureusCore core)
/*      */         {
/*  525 */           DownloadManager dm = PluginInitializer.getDefaultInterface().getDownloadManager();
/*      */           
/*  527 */           dm.addListener(DeviceUPnPImpl.this, true);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  532 */     addListener(this);
/*      */     
/*  534 */     TranscodeFile[] transcode_files = getFiles();
/*      */     
/*  536 */     for (TranscodeFile file : transcode_files)
/*      */     {
/*  538 */       fileAdded(file, false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void resetUPNPAV()
/*      */   {
/*  545 */     Set<String> to_remove = new HashSet();
/*      */     
/*  547 */     synchronized (this)
/*      */     {
/*  549 */       if (this.upnpav_ipc == null)
/*      */       {
/*  551 */         return;
/*      */       }
/*      */       
/*  554 */       this.upnpav_ipc = null;
/*      */       
/*  556 */       this.dynamic_transcode_profile = null;
/*      */       
/*  558 */       this.dynamic_xcode_map = null;
/*      */       
/*  560 */       DownloadManager dm = PluginInitializer.getDefaultInterface().getDownloadManager();
/*      */       
/*  562 */       dm.removeListener(this);
/*      */       
/*  564 */       removeListener(this);
/*      */       
/*  566 */       TranscodeFileImpl[] transcode_files = getFiles();
/*      */       
/*  568 */       for (TranscodeFileImpl file : transcode_files)
/*      */       {
/*  570 */         file.setTransientProperty(UPNPAV_FILE_KEY, null);
/*      */         
/*  572 */         to_remove.add(file.getKey());
/*      */       }
/*      */     }
/*      */     
/*  576 */     synchronized (acf_map)
/*      */     {
/*  578 */       for (String key : to_remove)
/*      */       {
/*  580 */         acf_map.remove(key);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void downloadAdded(Download download)
/*      */   {
/*  589 */     Torrent torrent = download.getTorrent();
/*      */     
/*  591 */     if ((torrent != null) && (PlatformTorrentUtils.isContent(torrent, false)))
/*      */     {
/*  593 */       addDynamicXCode(download.getDiskManagerFileInfo()[0]);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void downloadRemoved(Download download)
/*      */   {
/*  601 */     Torrent torrent = download.getTorrent();
/*      */     
/*  603 */     if ((torrent != null) && (PlatformTorrentUtils.isContent(torrent, false)))
/*      */     {
/*  605 */       removeDynamicXCode(download.getDiskManagerFileInfo()[0]);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void addDynamicXCode(final DiskManagerFileInfo source)
/*      */   {
/*  613 */     final TranscodeProfile profile = this.dynamic_transcode_profile;
/*      */     
/*  615 */     IPCInterface ipc = this.upnpav_ipc;
/*      */     
/*  617 */     if ((profile == null) || (ipc == null))
/*      */     {
/*  619 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  623 */       TranscodeFileImpl transcode_file = allocateFile(profile, false, source, false);
/*      */       
/*  625 */       AzureusContentFile acf = (AzureusContentFile)transcode_file.getTransientProperty(UPNPAV_FILE_KEY);
/*      */       
/*  627 */       if (acf != null)
/*      */       {
/*  629 */         return;
/*      */       }
/*      */       
/*  632 */       final String tf_key = transcode_file.getKey();
/*      */       
/*  634 */       synchronized (acf_map)
/*      */       {
/*  636 */         acf = (AzureusContentFile)acf_map.get(tf_key);
/*      */       }
/*      */       
/*  639 */       if (acf != null)
/*      */       {
/*  641 */         return;
/*      */       }
/*      */       
/*  644 */       final DiskManagerFileInfo stream_file = new DiskManagerFileInfoStream(new DiskManagerFileInfoStream.StreamFactory()
/*      */       {
/*      */ 
/*      */ 
/*  648 */         private List<Object> current_requests = new ArrayList();
/*      */         
/*      */ 
/*      */ 
/*      */         public DiskManagerFileInfoStream.StreamFactory.StreamDetails getStream(Object request)
/*      */           throws IOException
/*      */         {
/*      */           try
/*      */           {
/*  657 */             TranscodeJobImpl job = DeviceUPnPImpl.this.getManager().getTranscodeManager().getQueue().add((TranscodeTarget)DeviceUPnPImpl.this, profile, source, false, true, -1);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  665 */             synchronized (this)
/*      */             {
/*  667 */               this.current_requests.add(request);
/*      */             }
/*      */             
/*      */             for (;;)
/*      */             {
/*  672 */               InputStream is = job.getStream(1000);
/*      */               
/*  674 */               if (is != null)
/*      */               {
/*  676 */                 return new DeviceUPnPImpl.StreamWrapper(is, job);
/*      */               }
/*      */               
/*  679 */               int state = job.getState();
/*      */               
/*  681 */               if (state == 5)
/*      */               {
/*  683 */                 throw new IOException("Transcode failed: " + job.getError());
/*      */               }
/*  685 */               if (state == 4)
/*      */               {
/*  687 */                 throw new IOException("Transcode failed: job cancelled");
/*      */               }
/*  689 */               if (state == 3)
/*      */               {
/*  691 */                 throw new IOException("Job complete but no stream!");
/*      */               }
/*      */               
/*  694 */               synchronized (this)
/*      */               {
/*  696 */                 if (!this.current_requests.contains(request)) {
/*      */                   break;
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*  702 */               System.out.println("waiting for stream");
/*      */             }
/*      */             
/*      */ 
/*  706 */             IOException error = new IOException("Stream request cancelled");
/*      */             
/*  708 */             job.failed(error);
/*      */             
/*  710 */             throw error;
/*      */           }
/*      */           catch (IOException e)
/*      */           {
/*  714 */             throw e;
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  718 */             throw new IOException("Failed to add transcode job: " + Debug.getNestedExceptionMessage(e));
/*      */           }
/*      */           finally
/*      */           {
/*  722 */             synchronized (this)
/*      */             {
/*  724 */               this.current_requests.remove(request);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void destroyed(Object request)
/*      */         {
/*  733 */           synchronized (this)
/*      */           {
/*  735 */             this.current_requests.remove(request); } } }, transcode_file.getCacheFile());
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  741 */       acf = new AzureusContentFile()
/*      */       {
/*      */ 
/*      */         public DiskManagerFileInfo getFile()
/*      */         {
/*  746 */           return stream_file;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public Object getProperty(String name)
/*      */         {
/*  755 */           if (name.equals(DeviceUPnPImpl.this.MY_ACF_KEY))
/*      */           {
/*  757 */             return new Object[] { DeviceUPnPImpl.this, tf_key };
/*      */           }
/*  759 */           if (name.equals("percent"))
/*      */           {
/*  761 */             return new Long(1000L);
/*      */           }
/*  763 */           if (name.equals("eta"))
/*      */           {
/*  765 */             return new Long(0L);
/*      */           }
/*      */           
/*  768 */           return null;
/*      */         }
/*      */       };
/*      */       
/*  772 */       synchronized (acf_map)
/*      */       {
/*  774 */         acf_map.put(tf_key, acf);
/*      */       }
/*      */       
/*  777 */       transcode_file.setTransientProperty(UPNPAV_FILE_KEY, acf);
/*      */       
/*  779 */       syncCategoriesAndTags(transcode_file, true);
/*      */       
/*  781 */       synchronized (this)
/*      */       {
/*  783 */         if (this.dynamic_xcode_map == null)
/*      */         {
/*  785 */           this.dynamic_xcode_map = new HashMap();
/*      */         }
/*      */         
/*  788 */         this.dynamic_xcode_map.put(tf_key, acf);
/*      */       }
/*      */       
/*  791 */       ipc.invoke("addContent", new Object[] { acf });
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  795 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void removeDynamicXCode(DiskManagerFileInfo source)
/*      */   {
/*  803 */     TranscodeProfile profile = this.dynamic_transcode_profile;
/*      */     
/*  805 */     IPCInterface ipc = this.upnpav_ipc;
/*      */     
/*  807 */     if ((profile == null) || (ipc == null))
/*      */     {
/*  809 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  813 */       TranscodeFileImpl transcode_file = lookupFile(profile, source);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  818 */       if ((transcode_file != null) && (!transcode_file.isComplete()))
/*      */       {
/*  820 */         AzureusContentFile acf = null;
/*      */         
/*  822 */         synchronized (this)
/*      */         {
/*  824 */           if (this.dynamic_xcode_map != null)
/*      */           {
/*  826 */             acf = (AzureusContentFile)this.dynamic_xcode_map.get(transcode_file.getKey());
/*      */           }
/*      */         }
/*      */         
/*  830 */         transcode_file.delete(true);
/*      */         
/*  832 */         if (acf != null)
/*      */         {
/*  834 */           ipc.invoke("removeContent", new Object[] { acf });
/*      */         }
/*      */         
/*  837 */         synchronized (acf_map)
/*      */         {
/*  839 */           acf_map.remove(transcode_file.getKey());
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  844 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean setupStreamXCode(TranscodeFileImpl transcode_file)
/*      */   {
/*  852 */     TranscodeJobImpl job = transcode_file.getJob();
/*      */     
/*  854 */     if (job == null)
/*      */     {
/*      */ 
/*      */ 
/*  858 */       return transcode_file.isComplete();
/*      */     }
/*      */     
/*  861 */     final String tf_key = transcode_file.getKey();
/*      */     
/*      */     AzureusContentFile acf;
/*      */     
/*  865 */     synchronized (acf_map)
/*      */     {
/*  867 */       acf = (AzureusContentFile)acf_map.get(tf_key);
/*      */     }
/*      */     
/*  870 */     if (acf != null)
/*      */     {
/*  872 */       return true;
/*      */     }
/*      */     
/*  875 */     IPCInterface ipc = this.upnpav_ipc;
/*      */     
/*  877 */     if (ipc == null)
/*      */     {
/*  879 */       return false;
/*      */     }
/*      */     
/*  882 */     if (transcode_file.getDurationMillis() == 0L)
/*      */     {
/*  884 */       return false;
/*      */     }
/*      */     try
/*      */     {
/*  888 */       final Object stream_file = new TranscodeJobOutputLeecher(job, transcode_file);
/*      */       
/*      */ 
/*  891 */       acf = new AzureusContentFile()
/*      */       {
/*      */ 
/*      */         public DiskManagerFileInfo getFile()
/*      */         {
/*  896 */           return stream_file;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public Object getProperty(String name)
/*      */         {
/*  905 */           if (name.equals(DeviceUPnPImpl.this.MY_ACF_KEY))
/*      */           {
/*  907 */             return new Object[] { DeviceUPnPImpl.this, tf_key };
/*      */           }
/*  909 */           if (name.equals("percent"))
/*      */           {
/*  911 */             return new Long(1000L);
/*      */           }
/*  913 */           if (name.equals("eta"))
/*      */           {
/*  915 */             return new Long(0L);
/*      */           }
/*      */           
/*  918 */           return null;
/*      */         }
/*      */       };
/*      */       
/*  922 */       synchronized (acf_map)
/*      */       {
/*  924 */         acf_map.put(tf_key, acf);
/*      */       }
/*      */       
/*  927 */       ipc.invoke("addContent", new Object[] { acf });
/*      */       
/*  929 */       log("Set up stream-xcode for " + transcode_file.getName());
/*      */       
/*  931 */       return true;
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*  935 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean isVisible(AzureusContentDownload file)
/*      */   {
/*  943 */     if ((getFilterFilesView()) || (file == null))
/*      */     {
/*  945 */       return false;
/*      */     }
/*      */     
/*  948 */     Download download = file.getDownload();
/*      */     
/*  950 */     if (download == null)
/*      */     {
/*  952 */       return false;
/*      */     }
/*      */     
/*  955 */     if (download.isComplete())
/*      */     {
/*  957 */       return true;
/*      */     }
/*      */     
/*  960 */     int numFiles = download.getDiskManagerFileCount();
/*      */     
/*  962 */     for (int i = 0; i < numFiles; i++)
/*      */     {
/*  964 */       DiskManagerFileInfo fileInfo = download.getDiskManagerFileInfo(i);
/*      */       
/*  966 */       if ((fileInfo != null) && (!fileInfo.isDeleted()) && (!fileInfo.isSkipped()))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  971 */         if (fileInfo.getLength() == fileInfo.getDownloaded())
/*      */         {
/*  973 */           return true;
/*      */         }
/*  975 */         if (PlayUtils.canUseEMP(fileInfo))
/*      */         {
/*  977 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*  981 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean isVisible(AzureusContentFile file)
/*      */   {
/*  988 */     if (getFilterFilesView())
/*      */     {
/*  990 */       Object[] x = (Object[])file.getProperty(this.MY_ACF_KEY);
/*      */       
/*  992 */       if ((x != null) && (x[0] == this))
/*      */       {
/*  994 */         String tf_key = (String)x[1];
/*      */         
/*  996 */         return getTranscodeFile(tf_key) != null;
/*      */       }
/*      */       
/*      */ 
/* 1000 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 1004 */     if (file == null)
/*      */     {
/* 1006 */       return false;
/*      */     }
/*      */     
/* 1009 */     DiskManagerFileInfo fileInfo = file.getFile();
/*      */     
/* 1011 */     if ((fileInfo == null) || (fileInfo.isDeleted()) || (fileInfo.isSkipped()))
/*      */     {
/* 1013 */       return false;
/*      */     }
/*      */     
/* 1016 */     if (fileInfo.getLength() == fileInfo.getDownloaded())
/*      */     {
/* 1018 */       return true;
/*      */     }
/* 1020 */     if (PlayUtils.canUseEMP(fileInfo))
/*      */     {
/* 1022 */       return true;
/*      */     }
/*      */     
/*      */ 
/* 1026 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void fileAdded(TranscodeFile _transcode_file)
/*      */   {
/* 1033 */     fileAdded(_transcode_file, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void fileAdded(TranscodeFile _transcode_file, boolean _new_file)
/*      */   {
/* 1041 */     TranscodeFileImpl transcode_file = (TranscodeFileImpl)_transcode_file;
/*      */     
/* 1043 */     IPCInterface ipc = this.upnpav_ipc;
/*      */     
/* 1045 */     synchronized (this)
/*      */     {
/* 1047 */       if (ipc == null)
/*      */       {
/* 1049 */         return;
/*      */       }
/*      */       
/* 1052 */       if (!transcode_file.isComplete())
/*      */       {
/* 1054 */         syncCategoriesAndTags(transcode_file, _new_file);
/*      */         
/* 1056 */         return;
/*      */       }
/*      */       
/* 1059 */       AzureusContentFile acf = (AzureusContentFile)transcode_file.getTransientProperty(UPNPAV_FILE_KEY);
/*      */       
/* 1061 */       if (acf != null)
/*      */       {
/* 1063 */         return;
/*      */       }
/*      */       
/* 1066 */       final String tf_key = transcode_file.getKey();
/*      */       
/* 1068 */       synchronized (acf_map)
/*      */       {
/* 1070 */         acf = (AzureusContentFile)acf_map.get(tf_key);
/*      */       }
/*      */       
/* 1073 */       if (acf != null)
/*      */       {
/* 1075 */         return;
/*      */       }
/*      */       try
/*      */       {
/* 1079 */         final DiskManagerFileInfo f = transcode_file.getTargetFile();
/*      */         
/* 1081 */         acf = new AzureusContentFile()
/*      */         {
/*      */ 
/*      */           public DiskManagerFileInfo getFile()
/*      */           {
/*      */ 
/* 1087 */             return f;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public Object getProperty(String name)
/*      */           {
/* 1094 */             if (name.equals(DeviceUPnPImpl.this.MY_ACF_KEY))
/*      */             {
/* 1096 */               return new Object[] { DeviceUPnPImpl.this, tf_key };
/*      */             }
/* 1098 */             if (name.equals("cats"))
/*      */             {
/* 1100 */               TranscodeFileImpl tf = DeviceUPnPImpl.this.getTranscodeFile(tf_key);
/*      */               
/* 1102 */               if (tf != null)
/*      */               {
/* 1104 */                 return tf.getCategories();
/*      */               }
/*      */               
/* 1107 */               return new String[0];
/*      */             }
/* 1109 */             if (name.equals("tags"))
/*      */             {
/* 1111 */               TranscodeFileImpl tf = DeviceUPnPImpl.this.getTranscodeFile(tf_key);
/*      */               
/* 1113 */               if (tf != null)
/*      */               {
/* 1115 */                 return tf.getTags(true);
/*      */               }
/*      */               
/* 1118 */               return new String[0];
/*      */             }
/* 1120 */             if (name.equals("title"))
/*      */             {
/* 1122 */               TranscodeFileImpl tf = DeviceUPnPImpl.this.getTranscodeFile(tf_key);
/*      */               
/* 1124 */               if (tf != null)
/*      */               {
/* 1126 */                 return tf.getName();
/*      */               }
/*      */             }
/*      */             else {
/* 1130 */               TranscodeFileImpl tf = DeviceUPnPImpl.this.getTranscodeFile(tf_key);
/*      */               
/* 1132 */               if (tf != null)
/*      */               {
/* 1134 */                 long res = 0L;
/*      */                 
/* 1136 */                 if (name.equals("duration"))
/*      */                 {
/* 1138 */                   res = tf.getDurationMillis();
/*      */                 }
/* 1140 */                 else if (name.equals("video_width"))
/*      */                 {
/* 1142 */                   res = tf.getVideoWidth();
/*      */                 }
/* 1144 */                 else if (name.equals("video_height"))
/*      */                 {
/* 1146 */                   res = tf.getVideoHeight();
/*      */                 }
/* 1148 */                 else if (name.equals("date"))
/*      */                 {
/* 1150 */                   res = tf.getCreationDateMillis();
/*      */                 } else {
/* 1152 */                   if (name.equals("percent"))
/*      */                   {
/* 1154 */                     if (tf.isComplete())
/*      */                     {
/* 1156 */                       res = 1000L;
/*      */                     }
/*      */                     else
/*      */                     {
/* 1160 */                       TranscodeJob job = tf.getJob();
/*      */                       
/* 1162 */                       if (job == null)
/*      */                       {
/* 1164 */                         res = 0L;
/*      */                       }
/*      */                       else
/*      */                       {
/* 1168 */                         res = 10 * job.getPercentComplete();
/*      */                       }
/*      */                     }
/*      */                     
/* 1172 */                     return Long.valueOf(res);
/*      */                   }
/* 1174 */                   if (name.equals("eta"))
/*      */                   {
/* 1176 */                     if (tf.isComplete())
/*      */                     {
/* 1178 */                       res = 0L;
/*      */                     }
/*      */                     else
/*      */                     {
/* 1182 */                       TranscodeJob job = tf.getJob();
/*      */                       
/* 1184 */                       if (job == null)
/*      */                       {
/* 1186 */                         res = Long.MAX_VALUE;
/*      */                       }
/*      */                       else
/*      */                       {
/* 1190 */                         res = job.getETASecs();
/*      */                       }
/*      */                     }
/*      */                     
/* 1194 */                     return Long.valueOf(res);
/*      */                   }
/*      */                 }
/* 1197 */                 if (res > 0L)
/*      */                 {
/* 1199 */                   return new Long(res);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 1204 */             return null;
/*      */           }
/*      */           
/* 1207 */         };
/* 1208 */         transcode_file.setTransientProperty(UPNPAV_FILE_KEY, acf);
/*      */         
/* 1210 */         synchronized (acf_map)
/*      */         {
/* 1212 */           acf_map.put(tf_key, acf);
/*      */         }
/*      */         
/* 1215 */         syncCategoriesAndTags(transcode_file, _new_file);
/*      */         try
/*      */         {
/* 1218 */           ipc.invoke("addContent", new Object[] { acf });
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1222 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */       catch (TranscodeException e) {}
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void syncCategoriesAndTags(TranscodeFileImpl tf, boolean inherit_from_download)
/*      */   {
/*      */     try
/*      */     {
/* 1237 */       final Download dl = tf.getSourceFile().getDownload();
/*      */       
/* 1239 */       if (dl != null)
/*      */       {
/*      */ 
/*      */ 
/* 1243 */         if (inherit_from_download)
/*      */         {
/* 1245 */           setCategories(tf, dl);
/* 1246 */           setTags(tf, dl);
/*      */         }
/*      */         
/* 1249 */         final String tf_key = tf.getKey();
/*      */         
/* 1251 */         dl.addAttributeListener(new DownloadAttributeListener()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           public void attributeEventOccurred(Download download, TorrentAttribute attribute, int eventType)
/*      */           {
/*      */ 
/*      */ 
/* 1260 */             TranscodeFileImpl tf = DeviceUPnPImpl.this.getTranscodeFile(tf_key);
/*      */             
/* 1262 */             if (tf != null)
/*      */             {
/* 1264 */               DeviceUPnPImpl.this.setCategories(tf, download);
/*      */             }
/*      */             else
/*      */             {
/* 1268 */               dl.removeAttributeListener(this, DeviceUPnPImpl.this.upnp_manager.getCategoryAttibute(), 1); } } }, this.upnp_manager.getCategoryAttibute(), 1);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1275 */         TagManagerFactory.getTagManager().getTagType(3).addTagListener(PluginCoreUtils.unwrap(dl), new TagListener()
/*      */         {
/*      */           public void taggableSync(Tag tag) {}
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
/*      */           public void taggableRemoved(Tag tag, Taggable tagged)
/*      */           {
/* 1290 */             update(tagged);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void taggableAdded(Tag tag, Taggable tagged)
/*      */           {
/* 1298 */             update(tagged);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           private void update(Taggable tagged)
/*      */           {
/* 1305 */             TranscodeFileImpl tf = DeviceUPnPImpl.this.getTranscodeFile(tf_key);
/*      */             
/* 1307 */             if (tf != null)
/*      */             {
/* 1309 */               DeviceUPnPImpl.this.setTags(tf, dl);
/*      */             }
/*      */             else
/*      */             {
/* 1313 */               TagManagerFactory.getTagManager().getTagType(3).removeTagListener(tagged, this);
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setCategories(TranscodeFileImpl tf, Download dl)
/*      */   {
/* 1328 */     String cat = dl.getCategoryName();
/*      */     
/* 1330 */     if ((cat != null) && (cat.length() > 0) && (!cat.equals("Categories.uncategorized")))
/*      */     {
/* 1332 */       tf.setCategories(new String[] { cat });
/*      */     }
/*      */     else
/*      */     {
/* 1336 */       tf.setCategories(new String[0]);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setTags(TranscodeFileImpl tf, Download dl)
/*      */   {
/* 1345 */     List<Tag> tags = TagManagerFactory.getTagManager().getTagsForTaggable(PluginCoreUtils.unwrap(dl));
/*      */     
/* 1347 */     List<String> tag_names = new ArrayList();
/*      */     
/* 1349 */     for (Tag tag : tags)
/*      */     {
/* 1351 */       if (tag.getTagType().getTagType() == 3)
/*      */       {
/* 1353 */         tag_names.add(String.valueOf(tag.getTagUID()));
/*      */       }
/*      */     }
/*      */     
/* 1357 */     tf.setTags((String[])tag_names.toArray(new String[tag_names.size()]));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void fileChanged(TranscodeFile file, int type, Object data)
/*      */   {
/* 1366 */     if (file.isComplete())
/*      */     {
/* 1368 */       fileAdded(file, false);
/*      */     }
/*      */     
/* 1371 */     if (type == 1)
/*      */     {
/* 1373 */       if ((data == "cat") || (data == "tags"))
/*      */       {
/*      */         AzureusContentFile acf;
/*      */         
/* 1377 */         synchronized (acf_map)
/*      */         {
/* 1379 */           acf = (AzureusContentFile)acf_map.get(((TranscodeFileImpl)file).getKey());
/*      */         }
/*      */         
/* 1382 */         if (acf != null)
/*      */         {
/* 1384 */           if (data == "tags")
/*      */           {
/* 1386 */             AzureusPlatformContentDirectory.fireTagsChanged(acf);
/*      */           }
/*      */           else
/*      */           {
/* 1390 */             AzureusPlatformContentDirectory.fireCatsChanged(acf);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void fileRemoved(TranscodeFile file)
/*      */   {
/* 1401 */     IPCInterface ipc = this.upnp_manager.getUPnPAVIPC();
/*      */     
/* 1403 */     if (ipc == null)
/*      */     {
/* 1405 */       return;
/*      */     }
/*      */     
/* 1408 */     synchronized (this)
/*      */     {
/* 1410 */       AzureusContentFile acf = (AzureusContentFile)file.getTransientProperty(UPNPAV_FILE_KEY);
/*      */       
/* 1412 */       if (acf == null)
/*      */       {
/* 1414 */         return;
/*      */       }
/*      */       
/* 1417 */       file.setTransientProperty(UPNPAV_FILE_KEY, null);
/*      */       try
/*      */       {
/* 1420 */         ipc.invoke("removeContent", new Object[] { acf });
/*      */ 
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1425 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/* 1429 */     synchronized (acf_map)
/*      */     {
/* 1431 */       acf_map.remove(((TranscodeFileImpl)file).getKey());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected URL getPresentationURL(UPnPDevice device)
/*      */   {
/* 1439 */     String presentation = device.getRootDevice().getDevice().getPresentation();
/*      */     
/* 1441 */     if (presentation != null) {
/*      */       try
/*      */       {
/* 1444 */         return new URL(presentation);
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1452 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void getDisplayProperties(List<String[]> dp)
/*      */   {
/* 1459 */     super.getDisplayProperties(dp);
/*      */     
/* 1461 */     UPnPDevice device = this.device_may_be_null;
/*      */     
/* 1463 */     if (device != null)
/*      */     {
/* 1465 */       UPnPRootDevice root = device.getRootDevice();
/*      */       
/* 1467 */       URL location = root.getLocation();
/*      */       
/* 1469 */       addDP(dp, "dht.reseed.ip", location.getHost() + ":" + location.getPort());
/*      */       
/* 1471 */       String model_details = device.getModelName();
/* 1472 */       String model_url = device.getModelURL();
/*      */       
/* 1474 */       if ((model_url != null) && (model_url.length() > 0)) {
/* 1475 */         model_details = model_details + " (" + model_url + ")";
/*      */       }
/*      */       
/* 1478 */       String manu_details = device.getManufacturer();
/* 1479 */       String manu_url = device.getManufacturerURL();
/*      */       
/* 1481 */       if ((manu_url != null) && (manu_url.length() > 0)) {
/* 1482 */         manu_details = manu_details + " (" + manu_url + ")";
/*      */       }
/*      */       
/* 1485 */       addDP(dp, "device.model.desc", device.getModelDescription());
/* 1486 */       addDP(dp, "device.model.name", model_details);
/* 1487 */       addDP(dp, "device.model.num", device.getModelNumber());
/* 1488 */       addDP(dp, "device.manu.desc", manu_details);
/*      */     }
/*      */     else
/*      */     {
/* 1492 */       InetAddress ia = getAddress();
/*      */       
/* 1494 */       if (ia != null)
/*      */       {
/* 1496 */         addDP(dp, "dht.reseed.ip", ia.getHostAddress());
/*      */       }
/*      */     }
/* 1499 */     addDP(dp, "!Is Liveness Detectable!", isLivenessDetectable());
/* 1500 */     if (isManual())
/*      */     {
/* 1502 */       addDP(dp, "azbuddy.ui.table.online", isAlive());
/*      */       
/* 1504 */       addDP(dp, "device.lastseen", getLastSeen() == 0L ? "" : new SimpleDateFormat().format(new Date(getLastSeen())));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 1512 */     super.generate(writer);
/*      */     try
/*      */     {
/* 1515 */       writer.indent();
/*      */       
/* 1517 */       UPnPDevice device = this.device_may_be_null;
/*      */       
/* 1519 */       if (device == null)
/*      */       {
/* 1521 */         writer.println("upnp_device=null");
/*      */       }
/*      */       else
/*      */       {
/* 1525 */         writer.println("upnp_device=" + device.getFriendlyName());
/*      */       }
/*      */       
/* 1528 */       writer.println("dyn_xcode=" + (this.dynamic_transcode_profile == null ? "null" : this.dynamic_transcode_profile.getName()));
/*      */     }
/*      */     finally {
/* 1531 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static class StreamWrapper
/*      */     implements DiskManagerFileInfoStream.StreamFactory.StreamDetails
/*      */   {
/*      */     private InputStream is;
/*      */     
/*      */     private TranscodeJob job;
/*      */     
/*      */ 
/*      */     protected StreamWrapper(InputStream _is, TranscodeJob _job)
/*      */     {
/* 1547 */       this.is = _is;
/* 1548 */       this.job = _job;
/*      */     }
/*      */     
/*      */ 
/*      */     public InputStream getStream()
/*      */     {
/* 1554 */       return this.is;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean hasFailed()
/*      */     {
/* 1560 */       long start = SystemTime.getMonotonousTime();
/*      */       
/*      */       for (;;)
/*      */       {
/* 1564 */         int state = this.job.getState();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1569 */         if (state == 1)
/*      */         {
/* 1571 */           if (SystemTime.getMonotonousTime() - start > 5000L)
/*      */           {
/* 1573 */             return true;
/*      */           }
/*      */           
/*      */           try
/*      */           {
/* 1578 */             Thread.sleep(250L);
/*      */ 
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*      */ 
/* 1584 */             return true;
/*      */           }
/*      */           
/*      */ 
/*      */         }
/* 1589 */         else if ((state == 5) || (state == 4) || (state == 7) || (state == 6))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1596 */           TranscodeFile tf = this.job.getTranscodeFile();
/*      */           
/* 1598 */           if ((tf != null) && (tf.isComplete()))
/*      */           {
/* 1600 */             return false;
/*      */           }
/*      */           
/* 1603 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public String getImageID()
/*      */   {
/* 1611 */     String imageID = super.getImageID();
/*      */     
/* 1613 */     if ((this.device_may_be_null != null) && (isAlive())) {
/* 1614 */       UPnPDeviceImage[] images = this.device_may_be_null.getImages();
/* 1615 */       if (images.length > 0) {
/* 1616 */         URL location = getLocation();
/* 1617 */         if (location != null) {
/* 1618 */           String url = "http://" + location.getHost() + ":" + location.getPort();
/* 1619 */           String imageUrl = images[0].getLocation();
/* 1620 */           for (UPnPDeviceImage imageInfo : images) {
/* 1621 */             String mime = imageInfo.getLocation();
/* 1622 */             if ((mime != null) && (mime.contains("png"))) {
/* 1623 */               imageUrl = imageInfo.getLocation();
/* 1624 */               break;
/*      */             }
/*      */           }
/* 1627 */           if (!imageUrl.startsWith("/")) {
/* 1628 */             url = url + "/";
/*      */           }
/* 1630 */           url = url + imageUrl;
/* 1631 */           return url;
/*      */         }
/*      */       }
/*      */     }
/* 1635 */     return imageID;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/DeviceUPnPImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */