/*     */ package com.aelitis.azureus.core.devices.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.DeviceManagerException;
/*     */ import com.aelitis.azureus.core.devices.DeviceMediaRenderer;
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.core.tag.Taggable;
/*     */ import com.aelitis.net.upnp.UPnPDevice;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareItem;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareManager;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceFile;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*     */ import org.gudy.azureus2.plugins.utils.ShortCuts;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
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
/*     */ 
/*     */ public class DeviceMediaRendererImpl
/*     */   extends DeviceUPnPImpl
/*     */   implements DeviceMediaRenderer
/*     */ {
/*     */   private static final int INSTALL_CHECK_PERIOD = 60000;
/*     */   private static final int TAG_SHARE_CHECK_TICKS = 12;
/*     */   private static TorrentAttribute share_ta;
/*     */   
/*     */   public DeviceMediaRendererImpl(DeviceManagerImpl _manager, UPnPDevice _device)
/*     */   {
/*  70 */     super(_manager, _device, 3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DeviceMediaRendererImpl(DeviceManagerImpl _manager, String _classification)
/*     */   {
/*  78 */     super(_manager, 3, _classification);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DeviceMediaRendererImpl(DeviceManagerImpl _manager, String _uuid, String _classification, boolean _manual, String _name)
/*     */   {
/*  89 */     super(_manager, 3, _uuid, _classification, _manual, _name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DeviceMediaRendererImpl(DeviceManagerImpl _manager, String _uuid, String _classification, boolean _manual)
/*     */   {
/*  99 */     super(_manager, 3, _uuid, _classification, _manual);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DeviceMediaRendererImpl(DeviceManagerImpl _manager, Map _map)
/*     */     throws IOException
/*     */   {
/* 109 */     super(_manager, _map);
/*     */   }
/*     */   
/*     */   public void setAddress(InetAddress address) {
/* 113 */     super.setAddress(address);
/*     */     
/* 115 */     if (getType() == 3)
/*     */     {
/*     */ 
/* 118 */       boolean hasUPnPDevice = getUPnPDevice() != null;
/* 119 */       DeviceImpl[] devices = getManager().getDevices();
/* 120 */       for (DeviceImpl device : devices) {
/* 121 */         if ((device != this) && (!device.getID().equals(getID())) && ((device instanceof DeviceUPnPImpl)))
/*     */         {
/*     */ 
/*     */ 
/* 125 */           DeviceUPnPImpl deviceUPnP = (DeviceUPnPImpl)device;
/* 126 */           if ((address.equals(device.getAddress())) && (device.isAlive()))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 131 */             if (hasUPnPDevice) {
/* 132 */               boolean no_auto_hide = device.getPersistentBooleanProperty("rend_no_ah", false);
/*     */               
/* 134 */               if ((device.getType() == 3) && (!no_auto_hide))
/*     */               {
/* 136 */                 if (deviceUPnP.getUPnPDevice() != null) {
/* 137 */                   int fileCount = deviceUPnP.getFileCount();
/* 138 */                   if ((fileCount == 0) && (!device.isHidden())) {
/* 139 */                     log("Hiding " + device.getName() + "/" + device.getClassification() + "/" + device.getID() + " due to " + getName() + "/" + getClassification() + "/" + getID());
/*     */                     
/*     */ 
/*     */ 
/* 143 */                     device.setHidden(true);
/*     */                   }
/*     */                 }
/*     */               }
/* 147 */               break; }
/* 148 */             if (device.getType() == 3) {
/* 149 */               boolean no_auto_hide = getPersistentBooleanProperty("rend_no_ah", false);
/*     */               
/* 151 */               if (!no_auto_hide) {
/* 152 */                 int fileCount = getFileCount();
/*     */                 
/* 154 */                 if ((fileCount == 0) && (!isHidden())) {
/* 155 */                   log("hiding " + getName() + "/" + getClassification() + "/" + getID() + " due to " + device.getName() + "/" + device.getClassification() + "/" + device.getID());
/*     */                   
/*     */ 
/* 158 */                   setHidden(true);
/* 159 */                 } else if ((fileCount > 0) && (Constants.IS_CVS_VERSION) && (isHidden()))
/*     */                 {
/* 161 */                   setHidden(false);
/*     */                 }
/*     */               }
/* 164 */               break;
/*     */             }
/*     */             
/* 167 */             UPnPDevice upnpDevice = deviceUPnP.getUPnPDevice();
/* 168 */             if (upnpDevice != null) {
/* 169 */               String manufacturer = upnpDevice.getManufacturer();
/* 170 */               if ((manufacturer == null) || (!manufacturer.startsWith("Vuze"))) {
/* 171 */                 log("Linked " + getName() + " to UPnP Device " + device.getName());
/* 172 */                 setUPnPDevice(upnpDevice);
/* 173 */                 setDirty();
/*     */               }
/*     */             }
/*     */             
/*     */ 
/* 178 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean updateFrom(DeviceImpl _other, boolean _is_alive)
/*     */   {
/* 189 */     if (!super.updateFrom(_other, _is_alive))
/*     */     {
/* 191 */       return false;
/*     */     }
/*     */     
/* 194 */     if (!(_other instanceof DeviceMediaRendererImpl))
/*     */     {
/* 196 */       Debug.out("Inconsistent");
/*     */       
/* 198 */       return false;
/*     */     }
/*     */     
/* 201 */     DeviceMediaRendererImpl other = (DeviceMediaRendererImpl)_other;
/*     */     
/* 203 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void initialise()
/*     */   {
/* 210 */     super.initialise();
/*     */   }
/*     */   
/*     */ 
/* 214 */   private static List<Object[]> share_requests = new ArrayList();
/* 215 */   private static AsyncDispatcher share_dispatcher = new AsyncDispatcher();
/*     */   
/*     */ 
/*     */ 
/*     */   protected void updateStatus(int tick_count)
/*     */   {
/* 221 */     super.updateStatus(tick_count);
/*     */     
/* 223 */     if ((tick_count > 0) && (tick_count % 12 == 0))
/*     */     {
/*     */ 
/* 226 */       long tag_id = getAutoShareToTagID();
/*     */       
/* 228 */       if (tag_id != -1L)
/*     */       {
/* 230 */         synchronized (DeviceMediaRendererImpl.class)
/*     */         {
/* 232 */           if (share_ta == null)
/*     */           {
/* 234 */             share_ta = PluginInitializer.getDefaultInterface().getTorrentManager().getPluginAttribute("DeviceMediaRendererImpl:tag_share");
/*     */           }
/*     */         }
/*     */         
/* 238 */         TagManager tm = TagManagerFactory.getTagManager();
/*     */         
/* 240 */         Tag assigned_tag = tm.lookupTagByUID(tag_id);
/*     */         
/* 242 */         if (assigned_tag != null)
/*     */         {
/* 244 */           assigned_tag.setPublic(false);
/*     */           
/* 246 */           synchronized (share_requests)
/*     */           {
/* 248 */             if (share_requests.size() == 0)
/*     */             {
/* 250 */               Set<Taggable> taggables = assigned_tag.getTagged();
/*     */               
/* 252 */               Set<String> done_files = new HashSet();
/*     */               
/* 254 */               for (Taggable temp : taggables)
/*     */               {
/* 256 */                 if ((temp instanceof DownloadManager))
/*     */                 {
/*     */ 
/*     */ 
/*     */ 
/* 261 */                   DownloadManager dm = (DownloadManager)temp;
/*     */                   
/* 263 */                   Download download = PluginCoreUtils.wrap(dm);
/*     */                   
/* 265 */                   String attr = download.getAttribute(share_ta);
/*     */                   
/* 267 */                   if (attr != null)
/*     */                   {
/* 269 */                     done_files.add(attr);
/*     */                   }
/*     */                 }
/*     */               }
/* 273 */               TranscodeFileImpl[] files = getFiles();
/*     */               
/* 275 */               for (TranscodeFileImpl file : files)
/*     */               {
/* 277 */                 if (file.isComplete()) {
/*     */                   try
/*     */                   {
/* 280 */                     File target_file = file.getTargetFile().getFile(true);
/*     */                     
/* 282 */                     long size = target_file.length();
/*     */                     
/* 284 */                     if ((target_file.exists()) && (size > 0L))
/*     */                     {
/* 286 */                       String suffix = " (" + file.getProfileName() + " - " + DisplayFormatters.formatByteCountToKiBEtc(size) + ")";
/*     */                       
/* 288 */                       String share_name = file.getName() + suffix;
/* 289 */                       String key = target_file.getName() + suffix;
/*     */                       
/* 291 */                       if (!done_files.contains(key))
/*     */                       {
/* 293 */                         share_requests.add(new Object[] { key, target_file, share_name, assigned_tag });
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                   catch (Throwable e) {}
/*     */                 }
/*     */               }
/*     */               
/*     */ 
/* 302 */               if (share_requests.size() > 0)
/*     */               {
/* 304 */                 shareRequestAdded();
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void shareRequestAdded()
/*     */   {
/* 316 */     share_dispatcher.dispatch(new AERunnable()
/*     */     {
/*     */       public void runSupport()
/*     */       {
/*     */         List<Object[]> to_process;
/*     */         
/*     */ 
/*     */ 
/* 324 */         synchronized (DeviceMediaRendererImpl.share_requests)
/*     */         {
/* 326 */           to_process = new ArrayList(DeviceMediaRendererImpl.share_requests);
/*     */         }
/*     */         
/* 329 */         for (Object[] entry : to_process) {
/*     */           try
/*     */           {
/* 332 */             String key = (String)entry[0];
/* 333 */             File file = (File)entry[1];
/* 334 */             String name = (String)entry[2];
/* 335 */             Tag tag = (Tag)entry[3];
/*     */             
/* 337 */             DeviceMediaRendererImpl.this.log("Auto sharing " + name + " (" + file + ") to tag " + tag.getTagName(true));
/*     */             
/* 339 */             Map<String, String> properties = new HashMap();
/*     */             
/* 341 */             properties.put("user_data", "device:autoshare");
/*     */             
/*     */ 
/*     */ 
/* 345 */             String[] networks = AENetworkClassifier.getDefaultNetworks();
/*     */             
/* 347 */             String networks_str = "";
/*     */             
/* 349 */             for (String net : networks)
/*     */             {
/* 351 */               networks_str = networks_str + (networks_str.length() == 0 ? "" : ",") + net;
/*     */             }
/*     */             
/* 354 */             properties.put("networks", networks_str);
/*     */             
/* 356 */             properties.put("tags", String.valueOf(tag.getTagUID()));
/*     */             
/* 358 */             PluginInterface pi = PluginInitializer.getDefaultInterface();
/*     */             
/* 360 */             ShareResourceFile srf = pi.getShareManager().addFile(file, properties);
/*     */             
/* 362 */             Torrent torrent = srf.getItem().getTorrent();
/*     */             
/* 364 */             Download download = pi.getPluginManager().getDefaultPluginInterface().getShortCuts().getDownload(torrent.getHash());
/*     */             
/* 366 */             if (download == null)
/*     */             {
/* 368 */               throw new Exception("Download no longer exists");
/*     */             }
/*     */             
/* 371 */             DownloadManager dm = PluginCoreUtils.unwrap(download);
/*     */             
/* 373 */             dm.getDownloadState().setDisplayName(name);
/*     */             
/* 375 */             download.setAttribute(DeviceMediaRendererImpl.share_ta, key);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 379 */             DeviceMediaRendererImpl.this.log("Auto sharing failed", e);
/*     */           }
/*     */         }
/*     */         
/* 383 */         synchronized (DeviceMediaRendererImpl.share_requests)
/*     */         {
/* 385 */           DeviceMediaRendererImpl.share_requests.removeAll(to_process);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void destroy()
/*     */   {
/* 395 */     super.destroy();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canCopyToDevice()
/*     */   {
/* 401 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getAutoCopyToDevice()
/*     */   {
/* 407 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAutoCopyToDevice(boolean auto) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public int getCopyToDevicePending()
/*     */   {
/* 419 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canAutoStartDevice()
/*     */   {
/* 425 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getAutoStartDevice()
/*     */   {
/* 431 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAutoStartDevice(boolean auto) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean canCopyToFolder()
/*     */   {
/* 443 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCanCopyToFolder(boolean can) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public File getCopyToFolder()
/*     */   {
/* 456 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCopyToFolder(File file) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public int getCopyToFolderPending()
/*     */   {
/* 468 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getAutoCopyToFolder()
/*     */   {
/* 474 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAutoCopyToFolder(boolean auto) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void manualCopy()
/*     */     throws DeviceManagerException
/*     */   {
/* 488 */     throw new DeviceManagerException("Unsupported");
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canShowCategories()
/*     */   {
/* 494 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setShowCategories(boolean b)
/*     */   {
/* 501 */     setPersistentBooleanProperty("tt_show_cat", b);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getShowCategories()
/*     */   {
/* 507 */     return getPersistentBooleanProperty("tt_show_cat", getShowCategoriesDefault());
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean getShowCategoriesDefault()
/*     */   {
/* 513 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void getDisplayProperties(List<String[]> dp)
/*     */   {
/* 521 */     super.getDisplayProperties(dp);
/*     */     
/* 523 */     if (canCopyToFolder())
/*     */     {
/* 525 */       addDP(dp, "devices.copy.folder.auto", getAutoCopyToFolder());
/* 526 */       addDP(dp, "devices.copy.folder.dest", getCopyToFolder());
/*     */     }
/*     */     
/* 529 */     if (canCopyToDevice()) {
/* 530 */       addDP(dp, "devices.copy.device.auto", getAutoCopyToDevice());
/*     */     }
/*     */     
/* 533 */     if (canShowCategories())
/*     */     {
/* 535 */       addDP(dp, "devices.cat.show", getShowCategories());
/*     */     }
/*     */     
/* 538 */     super.getTTDisplayProperties(dp);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void generate(IndentWriter writer)
/*     */   {
/* 546 */     super.generate(writer);
/*     */     try
/*     */     {
/* 549 */       writer.indent();
/*     */       
/* 551 */       generateTT(writer);
/*     */     }
/*     */     finally
/*     */     {
/* 555 */       writer.exdent();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/DeviceMediaRendererImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */