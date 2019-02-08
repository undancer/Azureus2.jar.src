/*     */ package com.aelitis.azureus.util;
/*     */ 
/*     */ import com.aelitis.azureus.activities.LocalActivityManager;
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManagerFactory;
/*     */ import com.aelitis.azureus.core.content.AzureusPlatformContentDirectory;
/*     */ import com.aelitis.azureus.core.content.RelatedContentManager;
/*     */ import com.aelitis.azureus.core.devices.Device;
/*     */ import com.aelitis.azureus.core.devices.DeviceManager;
/*     */ import com.aelitis.azureus.core.devices.DeviceManagerFactory;
/*     */ import com.aelitis.azureus.core.devices.DeviceMediaRenderer;
/*     */ import com.aelitis.azureus.core.devices.TranscodeProfile;
/*     */ import com.aelitis.azureus.core.download.DownloadManagerEnhancer;
/*     */ import com.aelitis.azureus.core.metasearch.Engine;
/*     */ import com.aelitis.azureus.core.metasearch.MetaSearchManager;
/*     */ import com.aelitis.azureus.core.metasearch.MetaSearchManagerFactory;
/*     */ import com.aelitis.azureus.core.metasearch.MetaSearchManagerListener;
/*     */ import com.aelitis.azureus.core.peer.cache.CacheDiscovery;
/*     */ import com.aelitis.azureus.core.subs.Subscription;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionManager;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionManagerFactory;
/*     */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions.provider;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions.provider.LocalActivityCallback;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions.provider.TranscodeProfile;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions.provider.TranscodeTarget;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileComponent;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginUI;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager.UIFCallback;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStateAttributeListener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManagerListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadWillBeAddedListener;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.utils.DelayedTask;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
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
/*     */ public class InitialisationFunctions
/*     */ {
/*     */   private static final String EXTENSION_PREFIX = "azid";
/*     */   
/*     */   public static void earlyInitialisation(AzureusCore core)
/*     */   {
/*  77 */     DownloadUtils.initialise();
/*     */     
/*  79 */     DownloadManagerEnhancer dme = DownloadManagerEnhancer.initialise(core);
/*     */     
/*  81 */     hookDownloadAddition();
/*     */     
/*  83 */     AzureusPlatformContentDirectory.register();
/*     */     
/*  85 */     CacheDiscovery.initialise(dme);
/*     */     
/*  87 */     ContentNetworkManagerFactory.preInitialise();
/*     */     
/*  89 */     MetaSearchManagerFactory.preInitialise();
/*     */     
/*  91 */     SubscriptionManagerFactory.preInitialise();
/*     */     
/*  93 */     DeviceManagerFactory.preInitialise();
/*     */     
/*  95 */     NavigationHelper.initialise();
/*     */     
/*  97 */     RelatedContentManager.preInitialise(core);
/*     */     
/*  99 */     earlySWTInitialise();
/*     */     
/* 101 */     AZ3Functions.setProvider(new AZ3Functions.provider()
/*     */     {
/*     */       public String getDefaultContentNetworkURL(int type, Object[] params)
/*     */       {
/* 105 */         return ConstantsVuze.getDefaultContentNetwork().getServiceURL(type, params);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void subscribeToRSS(String name, URL url, int interval, boolean is_public, String creator_ref)
/*     */         throws Exception
/*     */       {
/* 118 */         Subscription subs = SubscriptionManagerFactory.getSingleton().createSingletonRSS(name, url, interval, false);
/*     */         
/*     */ 
/*     */ 
/* 122 */         if (!subs.getName(false).equals(name))
/*     */         {
/* 124 */           subs.setName(name);
/*     */         }
/*     */         
/* 127 */         if (subs.isPublic() != is_public)
/*     */         {
/* 129 */           subs.setPublic(is_public);
/*     */         }
/*     */         
/* 132 */         if (!subs.isSubscribed())
/*     */         {
/* 134 */           subs.setSubscribed(true);
/*     */         }
/* 136 */         if (creator_ref != null)
/*     */         {
/* 138 */           subs.setCreatorRef(creator_ref);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void subscribeToSubscription(String uri)
/*     */         throws Exception
/*     */       {
/* 148 */         SubscriptionManager manager = SubscriptionManagerFactory.getSingleton();
/*     */         
/* 150 */         Subscription subs = manager.createFromURI(uri);
/*     */         
/* 152 */         if (!subs.isSubscribed())
/*     */         {
/* 154 */           subs.setSubscribed(true);
/*     */         }
/*     */         
/* 157 */         if (subs.isSearchTemplate()) {
/*     */           try
/*     */           {
/* 160 */             VuzeFile vf = subs.getSearchTemplateVuzeFile();
/*     */             
/* 162 */             if (vf != null)
/*     */             {
/* 164 */               VuzeFileHandler.getSingleton().handleFiles(new VuzeFile[] { vf }, 0);
/*     */               
/* 166 */               for (VuzeFileComponent comp : vf.getComponents())
/*     */               {
/* 168 */                 Engine engine = (Engine)comp.getData(Engine.VUZE_FILE_COMPONENT_ENGINE_KEY);
/*     */                 
/* 170 */                 if ((engine != null) && ((engine.getSelectionState() == 0) || (engine.getSelectionState() == 3)))
/*     */                 {
/*     */ 
/*     */ 
/* 174 */                   engine.setSelectionState(2);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 180 */             Debug.out(e);
/*     */           }
/*     */           
/*     */         } else {
/* 184 */           subs.requestAttention();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public void openRemotePairingWindow()
/*     */       {
/* 191 */         UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */         
/* 193 */         if (uif == null)
/*     */         {
/* 195 */           Debug.out("UIFunctions not available, can't open remote pairing window");
/*     */         }
/*     */         else
/*     */         {
/* 199 */           uif.openRemotePairingWindow();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void setOpened(org.gudy.azureus2.core3.download.DownloadManager dm, boolean opened)
/*     */       {
/* 208 */         PlatformTorrentUtils.setHasBeenOpened(dm, opened);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean canPlay(org.gudy.azureus2.core3.download.DownloadManager dm, int file_index)
/*     */       {
/* 216 */         return (PlayUtils.canPlayDS(dm, file_index, true)) || (PlayUtils.canStreamDS(dm, file_index, true));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void play(org.gudy.azureus2.core3.download.DownloadManager dm, int file_index)
/*     */       {
/* 224 */         Object ds = dm;
/* 225 */         if (file_index >= 0) {
/* 226 */           DiskManagerFileInfo[] files = dm.getDiskManagerFileInfoSet().getFiles();
/* 227 */           if (file_index < files.length) {
/* 228 */             ds = files[file_index];
/*     */           }
/*     */         }
/*     */         
/* 232 */         UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */         
/* 234 */         if (uif == null)
/*     */         {
/* 236 */           Debug.out("UIFunctions not available, can't open play/stream content");
/*     */ 
/*     */         }
/* 239 */         else if (PlayUtils.canPlayDS(dm, file_index, true))
/*     */         {
/* 241 */           uif.playOrStreamDataSource(ds, "playdownloadmanager", false, true);
/*     */         }
/* 243 */         else if (PlayUtils.canStreamDS(dm, file_index, true))
/*     */         {
/* 245 */           uif.playOrStreamDataSource(ds, "playdownloadmanager", true, false);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean openChat(String network, String key)
/*     */       {
/* 255 */         return BuddyPluginUI.openChat(network, key);
/*     */       }
/*     */       
/*     */ 
/*     */       public AZ3Functions.provider.TranscodeTarget[] getTranscodeTargets()
/*     */       {
/* 261 */         List<AZ3Functions.provider.TranscodeTarget> result = new ArrayList();
/*     */         
/* 263 */         if (!COConfigurationManager.getStringParameter("ui").equals("az2")) {
/*     */           try
/*     */           {
/* 266 */             DeviceManager dm = DeviceManagerFactory.getSingleton();
/*     */             
/* 268 */             Device[] devices = dm.getDevices();
/*     */             
/* 270 */             for (final Device d : devices)
/*     */             {
/* 272 */               if ((d instanceof DeviceMediaRenderer))
/*     */               {
/* 274 */                 final DeviceMediaRenderer dmr = (DeviceMediaRenderer)d;
/*     */                 
/* 276 */                 boolean hide_device = d.isHidden();
/*     */                 
/* 278 */                 if (COConfigurationManager.getBooleanParameter("device.sidebar.ui.rend.hidegeneric", true))
/*     */                 {
/* 280 */                   if (dmr.isNonSimple())
/*     */                   {
/* 282 */                     hide_device = true;
/*     */                   }
/*     */                 }
/*     */                 
/* 286 */                 if (!hide_device)
/*     */                 {
/*     */ 
/*     */ 
/*     */ 
/* 291 */                   result.add(new AZ3Functions.provider.TranscodeTarget()
/*     */                   {
/*     */ 
/*     */                     public String getName()
/*     */                     {
/*     */ 
/* 297 */                       return d.getName();
/*     */                     }
/*     */                     
/*     */ 
/*     */                     public String getID()
/*     */                     {
/* 303 */                       return d.getID();
/*     */                     }
/*     */                     
/*     */ 
/*     */                     public AZ3Functions.provider.TranscodeProfile[] getProfiles()
/*     */                     {
/* 309 */                       List<AZ3Functions.provider.TranscodeProfile> ps = new ArrayList();
/*     */                       
/* 311 */                       TranscodeProfile[] profs = dmr.getTranscodeProfiles();
/*     */                       
/* 313 */                       if (profs.length == 0)
/*     */                       {
/* 315 */                         if (dmr.getTranscodeRequirement() == 1)
/*     */                         {
/* 317 */                           ps.add(new AZ3Functions.provider.TranscodeProfile()
/*     */                           {
/*     */ 
/*     */                             public String getUID()
/*     */                             {
/*     */ 
/* 323 */                               return InitialisationFunctions.1.1.this.val$dmr.getID() + "/" + InitialisationFunctions.1.1.this.val$dmr.getBlankProfile().getName();
/*     */                             }
/*     */                             
/*     */ 
/*     */                             public String getName()
/*     */                             {
/* 329 */                               return MessageText.getString("devices.profile.direct");
/*     */                             }
/*     */                           });
/*     */                         }
/*     */                       } else {
/* 334 */                         for (final TranscodeProfile prof : profs)
/*     */                         {
/* 336 */                           ps.add(new AZ3Functions.provider.TranscodeProfile()
/*     */                           {
/*     */ 
/*     */                             public String getUID()
/*     */                             {
/*     */ 
/* 342 */                               return prof.getUID();
/*     */                             }
/*     */                             
/*     */ 
/*     */                             public String getName()
/*     */                             {
/* 348 */                               return prof.getName();
/*     */                             }
/*     */                           });
/*     */                         }
/*     */                       }
/*     */                       
/* 354 */                       return (AZ3Functions.provider.TranscodeProfile[])ps.toArray(new AZ3Functions.provider.TranscodeProfile[ps.size()]);
/*     */                     }
/*     */                   });
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 362 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */         
/* 366 */         Collections.sort(result, new Comparator()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public int compare(AZ3Functions.provider.TranscodeTarget o1, AZ3Functions.provider.TranscodeTarget o2)
/*     */           {
/*     */ 
/*     */ 
/* 375 */             return o1.getName().compareTo(o2.getName());
/*     */           }
/*     */           
/* 378 */         });
/* 379 */         return (AZ3Functions.provider.TranscodeTarget[])result.toArray(new AZ3Functions.provider.TranscodeTarget[result.size()]);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void addLocalActivity(String uid, String icon_id, String name, String[] actions, Class<? extends AZ3Functions.provider.LocalActivityCallback> callback, Map<String, String> callback_data)
/*     */       {
/* 389 */         LocalActivityManager.addLocalActivity(uid, icon_id, name, actions, callback, callback_data);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void earlySWTInitialise()
/*     */   {
/* 400 */     UIFunctionsManager.execWithUIFunctions(new UIFunctionsManager.UIFCallback()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void run(UIFunctions uif)
/*     */       {
/*     */ 
/* 407 */         if (uif.getUIType() == 1)
/*     */         {
/* 409 */           BuddyPluginUI.preInitialize();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void lateInitialisation(AzureusCore core)
/*     */   {
/* 419 */     ExternalStimulusHandler.initialise(core);
/*     */     
/* 421 */     PluginInitializer.getDefaultInterface().getUtilities().createDelayedTask(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 427 */         MetaSearchManagerFactory.getSingleton();
/*     */         
/* 429 */         SubscriptionManagerFactory.getSingleton();
/*     */         try
/*     */         {
/* 432 */           RelatedContentManager.getSingleton();
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 436 */           Debug.out(e);
/*     */         }
/*     */         try
/*     */         {
/* 440 */           MetaSearchManagerFactory.getSingleton().addListener(new MetaSearchManagerListener()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void searchRequest(String term)
/*     */             {
/*     */ 
/* 447 */               UIFunctionsManager.getUIFunctions().doSearch(term);
/*     */             }
/*     */           });
/*     */         }
/*     */         catch (Throwable e) {
/* 452 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }).queue();
/*     */   }
/*     */   
/*     */ 
/*     */   protected static void hookDownloadAddition()
/*     */   {
/* 461 */     PluginInterface pi = PluginInitializer.getDefaultInterface();
/*     */     
/* 463 */     org.gudy.azureus2.plugins.download.DownloadManager dm = pi.getDownloadManager();
/*     */     
/*     */ 
/*     */ 
/* 467 */     dm.addDownloadWillBeAddedListener(new DownloadWillBeAddedListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void initialised(Download download)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 478 */         org.gudy.azureus2.core3.download.DownloadManager dm = PluginCoreUtils.unwrap(download);
/*     */         
/* 480 */         if (PlatformTorrentUtils.getHasBeenOpened(dm))
/*     */         {
/* 482 */           PlatformTorrentUtils.setHasBeenOpened(dm, false);
/*     */         }
/*     */         
/* 485 */         InitialisationFunctions.register(download);
/*     */       }
/*     */       
/* 488 */     });
/* 489 */     dm.addListener(new DownloadManagerListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void downloadAdded(Download download)
/*     */       {
/*     */ 
/* 496 */         InitialisationFunctions.register(download);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void downloadRemoved(Download download) {}
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static void register(Download download)
/*     */   {
/* 510 */     DownloadManagerStateAttributeListener dmsal = new DownloadManagerStateAttributeListener() {
/*     */       public void attributeEventOccurred(org.gudy.azureus2.core3.download.DownloadManager dm, String attribute_name, int event_type) {
/*     */         try {
/* 513 */           Torrent t = this.val$download.getTorrent();
/* 514 */           if (t == null) return;
/* 515 */           if (!PlatformTorrentUtils.isContent(t, true)) return;
/* 516 */           DownloadUtils.addTrackerExtension(this.val$download, "azid", ConstantsVuze.AZID);
/*     */           
/*     */ 
/* 519 */           this.val$download.setFlag(32L, true);
/*     */         }
/*     */         finally {
/* 522 */           dm.getDownloadState().removeListener(this, "trackerclientextensions", 2);
/*     */         }
/*     */         
/*     */       }
/* 526 */     };
/* 527 */     PluginCoreUtils.unwrap(download).getDownloadState().addListener(dmsal, "trackerclientextensions", 2);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/util/InitialisationFunctions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */