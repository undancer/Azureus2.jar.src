/*      */ package com.aelitis.azureus.core.download;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.devices.DeviceManager;
/*      */ import com.aelitis.azureus.core.devices.DeviceManagerFactory;
/*      */ import com.aelitis.azureus.core.devices.DeviceMediaRenderer;
/*      */ import com.aelitis.azureus.core.devices.TranscodeAnalysisListener;
/*      */ import com.aelitis.azureus.core.devices.TranscodeException;
/*      */ import com.aelitis.azureus.core.devices.TranscodeJob;
/*      */ import com.aelitis.azureus.core.devices.TranscodeManager;
/*      */ import com.aelitis.azureus.core.devices.TranscodeProfile;
/*      */ import com.aelitis.azureus.core.devices.TranscodeProviderAnalysis;
/*      */ import com.aelitis.azureus.core.devices.TranscodeQueue;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctions.actionListener;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import java.io.File;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginListener;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.PluginState;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadStats;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class StreamManager
/*      */ {
/*      */   private static final int BUFFER_SECS_DEFAULT = 30;
/*      */   private static final int BUFFER_MIN_SECS_DEFAULT = 3;
/*      */   private static int config_buffer_secs;
/*      */   private static int config_min_buffer_secs;
/*      */   
/*      */   static
/*      */   {
/*   76 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "streamman.buffer.secs", "streamman.min.buffer.secs" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*   87 */         StreamManager.access$002(COConfigurationManager.getIntParameter("streamman.buffer.secs", 30));
/*   88 */         StreamManager.access$102(COConfigurationManager.getIntParameter("streamman.min.buffer.secs", 3));
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*   94 */   private static StreamManager singleton = new StreamManager();
/*      */   private TorrentAttribute mi_ta;
/*      */   
/*      */   public static StreamManager getSingleton()
/*      */   {
/*   99 */     return singleton;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*  104 */   private AsyncDispatcher dispatcher = new AsyncDispatcher();
/*      */   
/*  106 */   private List<SMDImpl> streamers = new ArrayList();
/*      */   
/*      */ 
/*      */   private StreamManager()
/*      */   {
/*  111 */     PluginInterface default_pi = PluginInitializer.getDefaultInterface();
/*      */     
/*  113 */     this.mi_ta = default_pi.getTorrentManager().getPluginAttribute("sm_metainfo");
/*      */     
/*  115 */     default_pi.addListener(new PluginListener()
/*      */     {
/*      */       public void initializationComplete() {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void closedownInitiated()
/*      */       {
/*  127 */         StreamManager.this.dispatcher.dispatch(new AERunnable()
/*      */         {
/*      */           public void runSupport()
/*      */           {
/*      */             List<StreamManager.SMDImpl> to_cancel;
/*      */             
/*      */ 
/*      */ 
/*  135 */             synchronized (StreamManager.this)
/*      */             {
/*  137 */               to_cancel = new ArrayList(StreamManager.this.streamers);
/*      */               
/*  139 */               StreamManager.this.streamers.clear();
/*      */             }
/*      */             
/*  142 */             for (StreamManager.SMDImpl s : to_cancel)
/*      */             {
/*  144 */               s.cancel();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void closedownComplete() {}
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getBufferSecs()
/*      */   {
/*  161 */     return config_buffer_secs;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setBufferSecs(int secs)
/*      */   {
/*  168 */     COConfigurationManager.setParameter("streamman.buffer.secs", secs);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMinBufferSecs()
/*      */   {
/*  174 */     return config_min_buffer_secs;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMinBufferSecs(int secs)
/*      */   {
/*  181 */     COConfigurationManager.setParameter("streamman.min.buffer.secs", secs);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isStreamingUsable()
/*      */   {
/*  189 */     if ((!Constants.isWindows) && (!Constants.isOSX_10_5_OrHigher))
/*      */     {
/*  191 */       return false;
/*      */     }
/*      */     try
/*      */     {
/*  195 */       PluginManager plug_man = AzureusCoreFactory.getSingleton().getPluginManager();
/*      */       
/*  197 */       PluginInterface xcode_pi = plug_man.getPluginInterfaceByID("vuzexcode", false);
/*      */       
/*  199 */       if ((xcode_pi != null) && (!xcode_pi.getPluginState().isOperational()))
/*      */       {
/*      */ 
/*      */ 
/*  203 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  208 */       PluginInterface emp_pi = plug_man.getPluginInterfaceByID("azemp", false);
/*      */       
/*  210 */       if (emp_pi == null)
/*      */       {
/*      */ 
/*      */ 
/*  214 */         return true;
/*      */       }
/*      */       
/*  217 */       if (!emp_pi.getPluginState().isOperational())
/*      */       {
/*      */ 
/*      */ 
/*  221 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  226 */       Class<?> epwClass = emp_pi.getPlugin().getClass().getClassLoader().loadClass("com.azureus.plugins.azemp.ui.swt.emp.EmbeddedPlayerWindowSWT");
/*      */       
/*  228 */       Method method = epwClass.getMethod("prepareWindow", new Class[] { String.class });
/*      */       
/*  230 */       return method != null;
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*  234 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public StreamManagerDownload stream(DownloadManager dm, int file_index, URL url, boolean preview_mode, StreamManagerDownloadListener listener)
/*      */   {
/*  246 */     SMDImpl result = new SMDImpl(dm, file_index, url, preview_mode, listener, null);
/*      */     
/*  248 */     synchronized (this)
/*      */     {
/*  250 */       this.streamers.add(result);
/*      */     }
/*      */     
/*  253 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   private class SMDImpl
/*      */     extends AERunnable
/*      */     implements StreamManagerDownload
/*      */   {
/*      */     private DownloadManager dm;
/*      */     
/*      */     private int file_index;
/*      */     
/*      */     private URL url;
/*      */     
/*      */     private StreamManagerDownloadListener listener;
/*      */     private int existing_dl_limit;
/*      */     private boolean preview_mode;
/*  270 */     private long preview_mode_last_change = 0L;
/*      */     
/*      */ 
/*      */     private AESemaphore active_sem;
/*      */     
/*      */ 
/*      */     private TranscodeJob active_job;
/*      */     
/*      */ 
/*      */     private EnhancedDownloadManager active_edm;
/*      */     
/*      */     private boolean active_edm_activated;
/*      */     
/*      */     private volatile boolean cancelled;
/*      */     
/*      */ 
/*      */     private SMDImpl(DownloadManager _dm, int _file_index, URL _url, boolean _preview_mode, StreamManagerDownloadListener _listener)
/*      */     {
/*  288 */       this.dm = _dm;
/*  289 */       this.file_index = _file_index;
/*  290 */       this.url = _url;
/*  291 */       this.preview_mode = _preview_mode;
/*  292 */       this.listener = _listener;
/*      */       
/*  294 */       StreamManager.this.dispatcher.dispatch(this);
/*      */     }
/*      */     
/*      */ 
/*      */     public DownloadManager getDownload()
/*      */     {
/*  300 */       return this.dm;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getFileIndex()
/*      */     {
/*  306 */       return this.file_index;
/*      */     }
/*      */     
/*      */ 
/*      */     public URL getURL()
/*      */     {
/*  312 */       return this.url;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean getPreviewMode()
/*      */     {
/*  318 */       return this.preview_mode;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setPreviewMode(boolean _preview_mode)
/*      */     {
/*  325 */       long now = SystemTime.getMonotonousTime();
/*      */       
/*  327 */       if ((this.preview_mode_last_change == 0L) || (now - this.preview_mode_last_change > 500L))
/*      */       {
/*      */ 
/*  330 */         this.preview_mode_last_change = now;
/*      */         
/*  332 */         this.preview_mode = _preview_mode;
/*      */         
/*  334 */         this.listener.updateActivity("Preview mode changed to " + this.preview_mode);
/*      */       }
/*      */     }
/*      */     
/*      */     public void runSupport()
/*      */     {
/*      */       try
/*      */       {
/*  342 */         synchronized (StreamManager.this)
/*      */         {
/*  344 */           if (this.cancelled)
/*      */           {
/*  346 */             throw new Exception("Cancelled");
/*      */           }
/*      */           
/*  349 */           this.active_edm = DownloadManagerEnhancer.getSingleton().getEnhancedDownload(this.dm);
/*      */         }
/*      */         
/*  352 */         final long stream_start = SystemTime.getMonotonousTime();
/*      */         
/*  354 */         final Download download = PluginCoreUtils.wrap(this.dm);
/*      */         
/*  356 */         final DiskManagerFileInfo file = download.getDiskManagerFileInfo(this.file_index);
/*      */         
/*  358 */         PluginInterface emp_pi = checkPlugin("azemp", "media player");
/*      */         
/*  360 */         checkPlugin("vuzexcode", "media analyser");
/*      */         
/*  362 */         Class<?> epwClass = emp_pi.getPlugin().getClass().getClassLoader().loadClass("com.azureus.plugins.azemp.ui.swt.emp.EmbeddedPlayerWindowSWT");
/*      */         
/*  364 */         Method method = epwClass.getMethod("prepareWindow", new Class[] { String.class });
/*      */         
/*  366 */         final Object player = method.invoke(null, new Object[] { file.getFile(true).getName() });
/*      */         
/*  368 */         final Method buffering_method = player.getClass().getMethod("bufferingPlayback", new Class[] { Map.class });
/*  369 */         final Method is_active_method = player.getClass().getMethod("isActive", new Class[0]);
/*      */         
/*  371 */         final StreamManagerDownloadListener original_listener = this.listener;
/*      */         
/*  373 */         this.listener = new StreamManagerDownloadListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void updateActivity(String str)
/*      */           {
/*      */ 
/*  380 */             original_listener.updateActivity(str);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void updateStats(int secs_until_playable, int buffer_secs, long buffer_bytes, int target_buffer_secs)
/*      */           {
/*  390 */             original_listener.updateStats(secs_until_playable, buffer_secs, buffer_bytes, target_buffer_secs);
/*      */           }
/*      */           
/*      */ 
/*      */           public void ready()
/*      */           {
/*  396 */             original_listener.ready();
/*      */           }
/*      */           
/*      */ 
/*      */           public void failed(Throwable error)
/*      */           {
/*      */             try
/*      */             {
/*  404 */               original_listener.failed(error);
/*      */               
/*  406 */               Map<String, Object> b_map = new HashMap();
/*      */               
/*  408 */               b_map.put("state", new Integer(3));
/*  409 */               b_map.put("msg", Debug.getNestedExceptionMessage(error));
/*      */               try
/*      */               {
/*  412 */                 buffering_method.invoke(player, new Object[] { b_map });
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  416 */                 Debug.out(e);
/*      */               }
/*      */             }
/*      */             finally {
/*  420 */               StreamManager.SMDImpl.this.cancel();
/*      */             }
/*      */             
/*      */           }
/*  424 */         };
/*  425 */         Map<String, Map<String, Object>> map = download.getMapAttribute(StreamManager.this.mi_ta);
/*      */         
/*  427 */         Long l_duration = null;
/*  428 */         Long l_video_width = null;
/*  429 */         Long l_video_height = null;
/*      */         
/*  431 */         if (map != null)
/*      */         {
/*  433 */           Map<String, Object> file_map = (Map)map.get(String.valueOf(this.file_index));
/*      */           
/*  435 */           if (file_map != null)
/*      */           {
/*  437 */             l_duration = (Long)file_map.get("duration");
/*  438 */             l_video_width = (Long)file_map.get("video_width");
/*  439 */             l_video_height = (Long)file_map.get("video_height");
/*      */           }
/*      */         }
/*      */         
/*      */         final long duration;
/*      */         
/*      */         long video_width;
/*      */         long video_height;
/*  447 */         if (l_duration == null)
/*      */         {
/*  449 */           this.active_edm.prepareForProgressiveMode(true);
/*      */           try
/*      */           {
/*  452 */             DeviceManager dm = DeviceManagerFactory.getSingleton();
/*      */             
/*  454 */             TranscodeManager tm = dm.getTranscodeManager();
/*      */             
/*  456 */             DeviceMediaRenderer dmr = (DeviceMediaRenderer)dm.addVirtualDevice(3, "18a0b53a-a466-6795-1d0f-cf38c830ca0e", "generic", "Media Analyser");
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  463 */             dmr.setHidden(true);
/*  464 */             dmr.setCanRemove(false);
/*      */             
/*  466 */             TranscodeQueue queue = tm.getQueue();
/*      */             
/*  468 */             TranscodeJob[] jobs = queue.getJobs();
/*      */             
/*  470 */             for (TranscodeJob job : jobs)
/*      */             {
/*  472 */               if (job.getTarget() == dmr)
/*      */               {
/*  474 */                 job.removeForce();
/*      */               }
/*      */             }
/*      */             
/*  478 */             TranscodeProfile[] profiles = dmr.getTranscodeProfiles();
/*      */             
/*  480 */             TranscodeProfile profile = null;
/*      */             
/*  482 */             for (TranscodeProfile p : profiles)
/*      */             {
/*  484 */               if (p.getName().equals("Generic MP4"))
/*      */               {
/*  486 */                 profile = p;
/*      */                 
/*  488 */                 break;
/*      */               }
/*      */             }
/*      */             
/*  492 */             if (profile == null)
/*      */             {
/*  494 */               throw new Exception("Analyser transcode profile not found");
/*      */             }
/*      */             
/*  497 */             this.listener.updateActivity("Analysing media");
/*      */             
/*  499 */             final Map<String, Object> b_map = new HashMap();
/*      */             
/*  501 */             b_map.put("state", new Integer(1));
/*  502 */             b_map.put("msg", MessageText.getString("stream.analysing.media"));
/*      */             
/*  504 */             buffering_method.invoke(player, new Object[] { b_map });
/*      */             
/*  506 */             final TranscodeJob tj = queue.add(dmr, profile, file, true);
/*      */             try
/*      */             {
/*  509 */               final AESemaphore sem = new AESemaphore("analyserWait");
/*      */               
/*  511 */               synchronized (StreamManager.this)
/*      */               {
/*  513 */                 if (this.cancelled)
/*      */                 {
/*  515 */                   throw new Exception("Cancelled");
/*      */                 }
/*      */                 
/*  518 */                 this.active_sem = sem;
/*  519 */                 this.active_job = tj;
/*      */               }
/*      */               
/*  522 */               final long[] properties = new long[3];
/*      */               
/*  524 */               final Throwable[] error = { null };
/*      */               
/*  526 */               tj.analyseNow(new TranscodeAnalysisListener()
/*      */               {
/*      */ 
/*      */                 public void analysisComplete(TranscodeJob file, TranscodeProviderAnalysis analysis)
/*      */                 {
/*      */ 
/*      */                   try
/*      */                   {
/*      */ 
/*  535 */                     properties[0] = analysis.getLongProperty(2);
/*  536 */                     properties[1] = analysis.getLongProperty(3);
/*  537 */                     properties[2] = analysis.getLongProperty(4);
/*      */                     
/*  539 */                     tj.removeForce();
/*      */                   }
/*      */                   finally
/*      */                   {
/*  543 */                     sem.releaseForever();
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */                 public void analysisFailed(TranscodeJob file, TranscodeException e)
/*      */                 {
/*      */                   try
/*      */                   {
/*  553 */                     error[0] = e;
/*      */                     
/*  555 */                     tj.removeForce();
/*      */                   }
/*      */                   finally
/*      */                   {
/*  559 */                     sem.releaseForever();
/*      */                   }
/*      */                   
/*      */                 }
/*  563 */               });
/*  564 */               new AEThread2("SM:anmon")
/*      */               {
/*      */ 
/*      */                 public void run()
/*      */                 {
/*  569 */                   boolean last_preview_mode = StreamManager.SMDImpl.this.preview_mode;
/*      */                   
/*  571 */                   while ((!sem.isReleasedForever()) && (!StreamManager.SMDImpl.this.cancelled))
/*      */                   {
/*  573 */                     if (!sem.reserve(250L))
/*      */                     {
/*  575 */                       if (StreamManager.SMDImpl.this.cancelled)
/*      */                       {
/*  577 */                         return;
/*      */                       }
/*      */                       try
/*      */                       {
/*  581 */                         Boolean b = (Boolean)is_active_method.invoke(player, new Object[0]);
/*      */                         
/*  583 */                         if (!b.booleanValue())
/*      */                         {
/*  585 */                           StreamManager.SMDImpl.this.cancel();
/*      */                           
/*  587 */                           break;
/*      */                         }
/*      */                       }
/*      */                       catch (Throwable e) {}
/*      */                       
/*  592 */                       if (last_preview_mode != StreamManager.SMDImpl.this.preview_mode)
/*      */                       {
/*  594 */                         last_preview_mode = StreamManager.SMDImpl.this.preview_mode;
/*      */                         
/*  596 */                         b_map.put("msg", MessageText.getString(last_preview_mode ? "stream.analysing.media.preview" : "stream.analysing.media"));
/*      */                       }
/*      */                       
/*  599 */                       DownloadStats stats = download.getStats();
/*      */                       
/*  601 */                       b_map.put("dl_rate", Long.valueOf(stats.getDownloadAverage()));
/*  602 */                       b_map.put("dl_size", Long.valueOf(stats.getDownloaded()));
/*  603 */                       b_map.put("dl_time", Long.valueOf(SystemTime.getMonotonousTime() - stream_start));
/*      */                       try
/*      */                       {
/*  606 */                         this.val$buffering_method.invoke(player, new Object[] { b_map });
/*      */ 
/*      */                       }
/*      */                       catch (Throwable e) {}
/*      */                     }
/*      */                     
/*      */                   }
/*      */                   
/*      */                 }
/*  615 */               }.start();
/*  616 */               sem.reserve();
/*      */               
/*  618 */               synchronized (StreamManager.this)
/*      */               {
/*  620 */                 if (this.cancelled)
/*      */                 {
/*  622 */                   throw new Exception("Cancelled");
/*      */                 }
/*      */                 
/*  625 */                 this.active_job = null;
/*  626 */                 this.active_sem = null;
/*      */               }
/*      */               
/*  629 */               if (error[0] != null)
/*      */               {
/*  631 */                 throw error[0];
/*      */               }
/*      */               
/*  634 */               long duration = properties[0];
/*  635 */               long video_width = properties[1];
/*  636 */               long video_height = properties[2];
/*      */               
/*  638 */               if (duration > 0L)
/*      */               {
/*  640 */                 if (map == null)
/*      */                 {
/*  642 */                   map = new HashMap();
/*      */                 }
/*      */                 else
/*      */                 {
/*  646 */                   map = new HashMap(map);
/*      */                 }
/*      */                 
/*  649 */                 Map<String, Object> file_map = (Map)map.get(String.valueOf(this.file_index));
/*      */                 
/*  651 */                 if (file_map == null)
/*      */                 {
/*  653 */                   file_map = new HashMap();
/*      */                   
/*  655 */                   map.put(String.valueOf(this.file_index), file_map);
/*      */                 }
/*      */                 
/*  658 */                 file_map.put("duration", Long.valueOf(duration));
/*  659 */                 file_map.put("video_width", Long.valueOf(video_width));
/*  660 */                 file_map.put("video_height", Long.valueOf(video_height));
/*      */                 
/*  662 */                 download.setMapAttribute(StreamManager.this.mi_ta, map);
/*      */               }
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  667 */               tj.removeForce();
/*      */               
/*  669 */               throw e;
/*      */             }
/*      */           } catch (Throwable e) {
/*  672 */             e = 
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  678 */               e;throw new Exception("Media analysis failed", e);
/*      */           } finally {}
/*      */         } else {
/*  681 */           duration = l_duration.longValue();
/*  682 */           video_width = l_video_width == null ? 0L : l_video_width.longValue();
/*  683 */           video_height = l_video_height == null ? 0L : l_video_height.longValue();
/*      */         }
/*      */         
/*  686 */         if ((video_width == 0L) || (video_height == 0L))
/*      */         {
/*  688 */           throw new Exception("Media analysis failed - video stream not found");
/*      */         }
/*      */         
/*  691 */         if (duration == 0L)
/*      */         {
/*  693 */           throw new Exception("Media analysis failed - duration unknown");
/*      */         }
/*      */         
/*  696 */         this.listener.updateActivity("MetaData read: duration=" + TimeFormatter.formatColon(duration / 1000L) + ", width=" + video_width + ", height=" + video_height);
/*      */         
/*  698 */         Method smd_method = player.getClass().getMethod("setMetaData", new Class[] { Map.class });
/*      */         
/*  700 */         Map<String, Object> md_map = new HashMap();
/*      */         
/*  702 */         md_map.put("duration", Long.valueOf(duration));
/*  703 */         md_map.put("width", Long.valueOf(video_width));
/*  704 */         md_map.put("height", Long.valueOf(video_height));
/*      */         
/*  706 */         smd_method.invoke(player, new Object[] { md_map });
/*      */         
/*  708 */         final long bytes_per_sec = file.getLength() / (duration / 1000L);
/*      */         
/*  710 */         long dl_lim_max = COConfigurationManager.getIntParameter("Plugin.azemp.azemp.config.dl_lim_max") * 1024L;
/*  711 */         long dl_lim_extra = COConfigurationManager.getIntParameter("Plugin.azemp.azemp.config.dl_lim_extra") * 1024L;
/*      */         
/*  713 */         this.existing_dl_limit = download.getDownloadRateLimitBytesPerSecond();
/*      */         
/*  715 */         long required_limit = Math.max(dl_lim_max, bytes_per_sec + dl_lim_extra);
/*      */         
/*  717 */         if (required_limit > 0L)
/*      */         {
/*  719 */           download.setDownloadRateLimitBytesPerSecond((int)required_limit);
/*      */         }
/*      */         
/*  722 */         this.listener.updateActivity("Average rate=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(bytes_per_sec) + ", applied dl limit=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(required_limit));
/*      */         
/*  724 */         synchronized (StreamManager.this)
/*      */         {
/*  726 */           if (this.cancelled)
/*      */           {
/*  728 */             throw new Exception("Cancelled");
/*      */           }
/*      */           
/*  731 */           this.active_edm.setExplicitProgressive(StreamManager.config_buffer_secs, bytes_per_sec, this.file_index);
/*      */           
/*  733 */           if (!this.active_edm.setProgressiveMode(true))
/*      */           {
/*  735 */             throw new Exception("Failed to set download as progressive");
/*      */           }
/*      */           
/*  738 */           this.active_edm_activated = true;
/*      */         }
/*      */         
/*  741 */         new AEThread2("streamMon")
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/*      */ 
/*  747 */             int TIMER_PERIOD = 250;
/*  748 */             int PLAY_STATS_PERIOD = 5000;
/*  749 */             int PLAY_STATS_TICKS = 20;
/*      */             
/*  751 */             int DL_STARTUP_PERIOD = 5000;
/*  752 */             int DL_STARTUP_TICKS = 20;
/*      */             
/*  754 */             boolean playback_started = false;
/*  755 */             boolean playback_paused = false;
/*      */             
/*  757 */             boolean error_reported = false;
/*      */             try
/*      */             {
/*  760 */               Method start_method = player.getClass().getMethod("startPlayback", new Class[] { URL.class });
/*  761 */               Method pause_method = player.getClass().getMethod("pausePlayback", new Class[0]);
/*  762 */               Method resume_method = player.getClass().getMethod("resumePlayback", new Class[0]);
/*  763 */               Method buffering_method = player.getClass().getMethod("bufferingPlayback", new Class[] { Map.class });
/*  764 */               Method play_stats_method = player.getClass().getMethod("playStats", new Class[] { Map.class });
/*      */               
/*  766 */               int tick_count = 0;
/*      */               
/*  768 */               while (!StreamManager.SMDImpl.this.cancelled)
/*      */               {
/*  770 */                 tick_count++;
/*      */                 
/*  772 */                 int dm_state = StreamManager.SMDImpl.this.dm.getState();
/*      */                 
/*  774 */                 boolean complete = file.getLength() == file.getDownloaded();
/*      */                 
/*  776 */                 if (!complete)
/*      */                 {
/*  778 */                   if ((dm_state == 100) || (dm_state == 70) || (dm_state == 75))
/*      */                   {
/*      */ 
/*      */ 
/*  782 */                     if (tick_count >= 20)
/*      */                     {
/*  784 */                       throw new Exception("Streaming abandoned, download isn't running");
/*      */                     }
/*      */                   }
/*      */                   
/*  788 */                   if (!StreamManager.SMDImpl.this.active_edm.getProgressiveMode())
/*      */                   {
/*  790 */                     complete = file.getLength() == file.getDownloaded();
/*      */                     
/*  792 */                     if (!complete)
/*      */                     {
/*  794 */                       throw new Exception("Streaming mode abandoned for download");
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 
/*  799 */                 long[] details = StreamManager.SMDImpl.this.updateETA(StreamManager.SMDImpl.this.active_edm);
/*      */                 
/*  801 */                 int eta = (int)details[0];
/*  802 */                 int buffer_secs = (int)details[1];
/*  803 */                 long buffer = details[2];
/*      */                 
/*  805 */                 StreamManager.SMDImpl.this.listener.updateStats(eta, buffer_secs, buffer, StreamManager.config_buffer_secs);
/*      */                 
/*      */ 
/*      */ 
/*  809 */                 int buffer_to_use = playback_started ? StreamManager.config_min_buffer_secs : StreamManager.config_buffer_secs;
/*      */                 boolean playable;
/*  811 */                 boolean playable; if (complete)
/*      */                 {
/*  813 */                   playable = true;
/*      */                 }
/*      */                 else
/*      */                 {
/*  817 */                   playable = buffer_secs > buffer_to_use;
/*      */                   
/*  819 */                   playable = (playable) && ((eta <= 0) || ((playback_started) && (!playback_paused)) || (StreamManager.SMDImpl.this.preview_mode));
/*      */                 }
/*      */                 
/*  822 */                 if (playback_started)
/*      */                 {
/*  824 */                   if (playable)
/*      */                   {
/*  826 */                     if (playback_paused)
/*      */                     {
/*  828 */                       StreamManager.SMDImpl.this.listener.updateActivity("Resuming playback");
/*      */                       
/*  830 */                       resume_method.invoke(player, new Object[0]);
/*      */                       
/*  832 */                       playback_paused = false;
/*      */                     }
/*      */                     
/*      */                   }
/*  836 */                   else if (!playback_paused)
/*      */                   {
/*  838 */                     StreamManager.SMDImpl.this.listener.updateActivity("Pausing playback to prevent stall");
/*      */                     
/*  840 */                     pause_method.invoke(player, new Object[0]);
/*      */                     
/*  842 */                     playback_paused = true;
/*      */                   }
/*      */                   
/*      */ 
/*      */                 }
/*  847 */                 else if (playable)
/*      */                 {
/*  849 */                   StreamManager.SMDImpl.this.listener.ready();
/*      */                   
/*  851 */                   start_method.invoke(player, new Object[] { StreamManager.SMDImpl.this.url });
/*      */                   
/*  853 */                   playback_started = true;
/*      */                 }
/*      */                 
/*      */ 
/*  857 */                 if (playable)
/*      */                 {
/*  859 */                   if (tick_count % 20 == 0)
/*      */                   {
/*  861 */                     long contiguous_done = StreamManager.SMDImpl.this.active_edm.getContiguousAvailableBytes(StreamManager.SMDImpl.this.file_index >= 0 ? StreamManager.SMDImpl.this.file_index : StreamManager.SMDImpl.this.active_edm.getPrimaryFileIndex(), 0L, 0L);
/*      */                     
/*  863 */                     Map<String, Object> map = new HashMap();
/*      */                     
/*  865 */                     map.put("buffer_min", new Long(StreamManager.config_buffer_secs));
/*  866 */                     map.put("buffer_secs", new Integer(buffer_secs));
/*  867 */                     map.put("buffer_bytes", new Long(buffer));
/*      */                     
/*  869 */                     map.put("stream_rate", Long.valueOf(bytes_per_sec));
/*      */                     
/*  871 */                     DownloadStats stats = stream_start.getStats();
/*      */                     
/*  873 */                     map.put("dl_rate", Long.valueOf(stats.getDownloadAverage()));
/*  874 */                     map.put("dl_size", Long.valueOf(stats.getDownloaded()));
/*  875 */                     map.put("dl_time", Long.valueOf(SystemTime.getMonotonousTime() - duration));
/*      */                     
/*  877 */                     map.put("duration", Long.valueOf(this.val$duration));
/*  878 */                     map.put("file_size", Long.valueOf(file.getLength()));
/*  879 */                     map.put("cont_done", Long.valueOf(contiguous_done));
/*      */                     
/*  881 */                     play_stats_method.invoke(player, new Object[] { map });
/*      */                   }
/*      */                 }
/*      */                 else {
/*  885 */                   DownloadStats stats = stream_start.getStats();
/*      */                   
/*  887 */                   Map<String, Object> map = new HashMap();
/*      */                   
/*  889 */                   map.put("state", new Integer(2));
/*      */                   
/*  891 */                   if ((StreamManager.SMDImpl.this.preview_mode) && (!complete))
/*      */                   {
/*  893 */                     long rate = stats.getDownloadAverage();
/*      */                     
/*      */                     int preview_eta;
/*      */                     int preview_eta;
/*  897 */                     if (rate <= 0L)
/*      */                     {
/*  899 */                       preview_eta = Integer.MAX_VALUE;
/*      */                     }
/*      */                     else
/*      */                     {
/*  903 */                       double secs_per_sec = bytes_per_sec / rate;
/*      */                       
/*  905 */                       preview_eta = (int)((buffer_to_use - buffer_secs) * secs_per_sec);
/*      */                     }
/*      */                     
/*  908 */                     map.put("eta", new Integer(preview_eta));
/*      */                     
/*  910 */                     map.put("preview", Integer.valueOf(1));
/*      */                   }
/*      */                   else
/*      */                   {
/*  914 */                     map.put("eta", new Integer(eta));
/*      */                     
/*  916 */                     map.put("preview", Integer.valueOf(0));
/*      */                   }
/*      */                   
/*  919 */                   map.put("buffer_min", new Long(StreamManager.config_buffer_secs));
/*  920 */                   map.put("buffer_secs", new Integer(buffer_secs));
/*  921 */                   map.put("buffer_bytes", new Long(buffer));
/*      */                   
/*  923 */                   map.put("stream_rate", Long.valueOf(bytes_per_sec));
/*      */                   
/*  925 */                   map.put("dl_rate", Long.valueOf(stats.getDownloadAverage()));
/*  926 */                   map.put("dl_size", Long.valueOf(stats.getDownloaded()));
/*  927 */                   map.put("dl_time", Long.valueOf(SystemTime.getMonotonousTime() - duration));
/*      */                   
/*  929 */                   buffering_method.invoke(player, new Object[] { map });
/*      */                 }
/*      */                 
/*  932 */                 Thread.sleep(250L);
/*      */                 try
/*      */                 {
/*  935 */                   Boolean b = (Boolean)this.val$is_active_method.invoke(player, new Object[0]);
/*      */                   
/*  937 */                   if (!b.booleanValue())
/*      */                   {
/*  939 */                     StreamManager.SMDImpl.this.cancel();
/*      */                     
/*  941 */                     break;
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/*  948 */               error_reported = true;
/*      */               
/*  950 */               StreamManager.SMDImpl.this.listener.failed(e);
/*      */             }
/*      */             finally
/*      */             {
/*  954 */               if ((!error_reported) && (!StreamManager.SMDImpl.this.cancelled))
/*      */               {
/*  956 */                 if (!playback_started)
/*      */                 {
/*  958 */                   StreamManager.SMDImpl.this.listener.failed(new Exception("Streaming failed, reason unknown"));
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }.start();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*      */         try {
/*  968 */           this.listener.failed(e);
/*      */         }
/*      */         finally
/*      */         {
/*  972 */           cancel();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private long[] updateETA(EnhancedDownloadManager edm)
/*      */     {
/*  981 */       long _eta = edm.getProgressivePlayETA();
/*      */       
/*  983 */       int eta = _eta >= 2147483647L ? Integer.MAX_VALUE : (int)_eta;
/*      */       
/*  985 */       EnhancedDownloadManager.progressiveStats stats = edm.getProgressiveStats();
/*      */       
/*  987 */       long provider_pos = stats.getCurrentProviderPosition(false);
/*      */       
/*  989 */       long buffer = edm.getContiguousAvailableBytes(this.file_index >= 0 ? this.file_index : edm.getPrimaryFileIndex(), provider_pos, 0L);
/*      */       
/*  991 */       long bps = stats.getStreamBytesPerSecondMin();
/*      */       
/*  993 */       int buffer_secs = bps <= 0L ? Integer.MAX_VALUE : (int)(buffer / bps);
/*      */       
/*  995 */       return new long[] { eta, buffer_secs, buffer };
/*      */     }
/*      */     
/*      */ 
/*      */     public void cancel()
/*      */     {
/*      */       TranscodeJob job;
/*      */       
/*      */       EnhancedDownloadManager edm;
/*      */       
/*      */       boolean edm_activated;
/* 1006 */       synchronized (StreamManager.this)
/*      */       {
/* 1008 */         this.cancelled = true;
/*      */         
/* 1010 */         job = this.active_job;
/*      */         
/* 1012 */         if (this.active_sem != null)
/*      */         {
/* 1014 */           this.active_sem.releaseForever();
/*      */         }
/*      */         
/* 1017 */         edm = this.active_edm;
/* 1018 */         edm_activated = this.active_edm_activated;
/*      */         
/* 1020 */         StreamManager.this.streamers.remove(this);
/*      */       }
/*      */       
/* 1023 */       if (job != null)
/*      */       {
/* 1025 */         job.removeForce();
/*      */       }
/*      */       
/* 1028 */       if (edm != null)
/*      */       {
/* 1030 */         if (edm_activated)
/*      */         {
/* 1032 */           edm.setProgressiveMode(false);
/*      */         }
/*      */         else
/*      */         {
/* 1036 */           edm.prepareForProgressiveMode(false);
/*      */         }
/*      */       }
/*      */       
/* 1040 */       Download download = PluginCoreUtils.wrap(this.dm);
/*      */       
/* 1042 */       download.setDownloadRateLimitBytesPerSecond(this.existing_dl_limit);
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isCancelled()
/*      */     {
/* 1048 */       return this.cancelled;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private PluginInterface checkPlugin(String id, String name)
/*      */       throws Throwable
/*      */     {
/* 1058 */       PluginManager plug_man = AzureusCoreFactory.getSingleton().getPluginManager();
/*      */       
/* 1060 */       PluginInterface pi = plug_man.getPluginInterfaceByID(id, false);
/*      */       
/* 1062 */       if (pi == null)
/*      */       {
/* 1064 */         UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*      */         
/* 1066 */         if (uif == null)
/*      */         {
/* 1068 */           throw new Exception("UIFunctions unavailable - can't install plugin '" + name + "'");
/*      */         }
/*      */         
/* 1071 */         this.listener.updateActivity("Installing " + name);
/*      */         
/* 1073 */         final AESemaphore sem = new AESemaphore("analyserWait");
/*      */         
/* 1075 */         synchronized (StreamManager.this)
/*      */         {
/* 1077 */           if (this.cancelled)
/*      */           {
/* 1079 */             throw new Exception("Cancelled");
/*      */           }
/*      */           
/* 1082 */           this.active_sem = sem;
/*      */         }
/*      */         
/* 1085 */         final Throwable[] error = { null };
/*      */         
/* 1087 */         uif.installPlugin(id, "dlg.install." + id, new UIFunctions.actionListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void actionComplete(Object result)
/*      */           {
/*      */ 
/*      */             try
/*      */             {
/*      */ 
/* 1097 */               if (!(result instanceof Boolean))
/*      */               {
/*      */ 
/*      */ 
/* 1101 */                 error[0] = ((Throwable)result);
/*      */               }
/*      */             }
/*      */             finally {
/* 1105 */               sem.release();
/*      */             }
/*      */             
/*      */           }
/* 1109 */         });
/* 1110 */         sem.reserve();
/*      */         
/* 1112 */         synchronized (StreamManager.this)
/*      */         {
/* 1114 */           if (this.cancelled)
/*      */           {
/* 1116 */             throw new Exception("Cancelled");
/*      */           }
/*      */           
/* 1119 */           this.active_sem = null;
/*      */         }
/*      */         
/* 1122 */         if (error[0] != null)
/*      */         {
/* 1124 */           throw error[0];
/*      */         }
/*      */         
/* 1127 */         long start = SystemTime.getMonotonousTime();
/*      */         
/* 1129 */         this.listener.updateActivity("Waiting for plugin initialisation");
/*      */         
/*      */         for (;;)
/*      */         {
/* 1133 */           if (this.cancelled)
/*      */           {
/* 1135 */             throw new Exception("Cancelled");
/*      */           }
/*      */           
/* 1138 */           if (SystemTime.getMonotonousTime() - start >= 30000L)
/*      */           {
/* 1140 */             throw new Exception("Timeout waiting for " + name + " to initialise");
/*      */           }
/*      */           
/* 1143 */           pi = plug_man.getPluginInterfaceByID(id, false);
/*      */           
/* 1145 */           if ((pi != null) && (pi.getPluginState().isOperational()))
/*      */           {
/* 1147 */             return pi;
/*      */           }
/*      */           
/* 1150 */           Thread.sleep(250L);
/*      */         } }
/* 1152 */       if (!pi.getPluginState().isOperational())
/*      */       {
/* 1154 */         throw new Exception(name + " not operational");
/*      */       }
/*      */       
/*      */ 
/* 1158 */       return pi;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/download/StreamManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */