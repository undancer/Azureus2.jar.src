/*     */ package com.aelitis.azureus.core.networkmanager.admin.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTestScheduledTest;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTestScheduledTestListener;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTester;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTesterListener;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTesterResult;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.plugins.upnp.UPnPPlugin;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.lang.reflect.Field;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.config.impl.TransferSpeedValidator;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.plugins.PluginConfig;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManagerListener;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginConfigImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader.ResourceDownloaderFactoryImpl;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NetworkAdminSpeedTestScheduledTestImpl
/*     */   implements NetworkAdminSpeedTestScheduledTest
/*     */ {
/*     */   private static final long REQUEST_TEST = 0L;
/*     */   private static final long CHALLENGE_REPLY = 1L;
/*     */   private static final long TEST_RESULT = 2L;
/*     */   private static final int ZERO_DOWNLOAD_SETTING = -1;
/*     */   final PluginInterface plugin;
/*     */   final NetworkAdminSpeedTesterImpl tester;
/*     */   private String detectedRouter;
/*     */   private SpeedTestDownloadState preTestSettings;
/*     */   private byte[] challenge_id;
/*     */   private long delay_millis;
/*     */   private long max_speed;
/*     */   private TOTorrent test_torrent;
/*     */   private volatile boolean aborted;
/*  92 */   private final CopyOnWriteList listeners = new CopyOnWriteList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected NetworkAdminSpeedTestScheduledTestImpl(PluginInterface _plugin, NetworkAdminSpeedTesterImpl _tester)
/*     */   {
/*  99 */     this.plugin = _plugin;
/* 100 */     this.tester = _tester;
/*     */     
/*     */ 
/*     */ 
/* 104 */     PluginInterface upnp = this.plugin.getPluginManager().getPluginInterfaceByClass(UPnPPlugin.class);
/*     */     
/* 106 */     if (upnp != null)
/*     */     {
/* 108 */       this.detectedRouter = upnp.getPluginconfig().getPluginStringParameter("plugin.info");
/*     */     }
/*     */     
/* 111 */     this.tester.addListener(new NetworkAdminSpeedTesterListener()
/*     */     {
/*     */ 
/*     */       public void complete(NetworkAdminSpeedTester tester, NetworkAdminSpeedTesterResult result)
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/* 120 */           NetworkAdminSpeedTestScheduledTestImpl.this.sendResult(result);
/*     */         }
/*     */         finally
/*     */         {
/* 124 */           NetworkAdminSpeedTestScheduledTestImpl.this.reportComplete();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void stage(NetworkAdminSpeedTester tester, String step) {}
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public NetworkAdminSpeedTester getTester()
/*     */   {
/* 140 */     return this.tester;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getMaxUpBytePerSec()
/*     */   {
/* 146 */     return this.max_speed;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getMaxDownBytePerSec()
/*     */   {
/* 152 */     return this.max_speed;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean start()
/*     */   {
/* 158 */     if (schedule())
/*     */     {
/* 160 */       new AEThread("NetworkAdminSpeedTestScheduledTest:delay", true)
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/* 165 */           long delay_ticks = NetworkAdminSpeedTestScheduledTestImpl.this.delay_millis / 1000L;
/*     */           
/* 167 */           for (int i = 0; i < delay_ticks; i++)
/*     */           {
/* 169 */             if (NetworkAdminSpeedTestScheduledTestImpl.this.aborted) {
/*     */               break;
/*     */             }
/*     */             
/*     */ 
/* 174 */             String testScheduledIn = MessageText.getString("SpeedTestWizard.abort.message.scheduled.in", new String[] { "" + (delay_ticks - i) });
/*     */             
/* 176 */             NetworkAdminSpeedTestScheduledTestImpl.this.reportStage(testScheduledIn);
/*     */             try
/*     */             {
/* 179 */               Thread.sleep(1000L);
/*     */             }
/*     */             catch (InterruptedException e)
/*     */             {
/* 183 */               e.printStackTrace();
/*     */             }
/*     */           }
/*     */           
/* 187 */           if (!NetworkAdminSpeedTestScheduledTestImpl.this.aborted)
/*     */           {
/* 189 */             NetworkAdminSpeedTestScheduledTestImpl.this.setSpeedLimits();
/*     */             
/* 191 */             if (NetworkAdminSpeedTestScheduledTestImpl.this.tester.getTestType() == 1)
/*     */             {
/* 193 */               ((NetworkAdminSpeedTesterBTImpl)NetworkAdminSpeedTestScheduledTestImpl.this.tester).start(NetworkAdminSpeedTestScheduledTestImpl.this.test_torrent);
/*     */             }
/*     */             else {
/* 196 */               String unsupportedType = MessageText.getString("SpeedTestWizard.abort.message.unsupported.type");
/* 197 */               NetworkAdminSpeedTestScheduledTestImpl.this.tester.abort(unsupportedType);
/*     */             }
/*     */             
/*     */           }
/*     */         }
/* 202 */       }.start();
/* 203 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 207 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void abort()
/*     */   {
/* 214 */     abort(MessageText.getString("SpeedTestWizard.abort.message.manual.abort"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void abort(String reason)
/*     */   {
/* 221 */     if (!this.aborted)
/*     */     {
/* 223 */       this.aborted = true;
/*     */       
/* 225 */       this.tester.abort(reason);
/*     */     }
/*     */   }
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
/*     */   private boolean schedule()
/*     */   {
/*     */     try
/*     */     {
/* 244 */       Map request = new HashMap();
/* 245 */       request.put("request_type", new Long(0L));
/*     */       
/* 247 */       String id = COConfigurationManager.getStringParameter("ID", "unknown");
/*     */       
/*     */ 
/*     */ 
/* 251 */       File jar_file = null;
/* 252 */       String jar_version = null;
/*     */       
/* 254 */       String explicit_path = System.getProperty("azureus.speed.test.challenge.jar.path", null);
/*     */       
/* 256 */       if (explicit_path != null)
/*     */       {
/* 258 */         File f = new File(explicit_path);
/*     */         
/* 260 */         if (f.exists())
/*     */         {
/* 262 */           String v = getVersionFromJAR(f);
/*     */           
/* 264 */           if (v != null)
/*     */           {
/* 266 */             jar_file = f;
/* 267 */             jar_version = v;
/*     */             
/* 269 */             System.out.println("SpeedTest: using explicit challenge jar " + jar_file.getAbsolutePath() + ", version " + jar_version);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 274 */       if (jar_file == null)
/*     */       {
/* 276 */         String debug = System.getProperty("debug.speed.test.challenge", "n");
/*     */         
/* 278 */         if (!debug.equals("n"))
/*     */         {
/*     */ 
/*     */ 
/* 282 */           File f = new File("C:\\test\\azureus\\Azureus3.0.1.2.jar");
/*     */           
/* 284 */           if (f.exists())
/*     */           {
/* 286 */             jar_file = f;
/* 287 */             jar_version = "3.0.1.2";
/*     */             
/* 289 */             System.out.println("SpeedTest: using old spec challenge jar " + jar_file.getAbsolutePath() + ", version " + jar_version);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 294 */       if (jar_file == null)
/*     */       {
/* 296 */         jar_file = FileUtil.getJarFileFromClass(getClass());
/*     */         
/* 298 */         if (jar_file != null)
/*     */         {
/* 300 */           jar_version = "5.7.6.0";
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 306 */           File f = new File(SystemProperties.getAzureusJarPath());
/*     */           
/* 308 */           if (f.exists())
/*     */           {
/* 310 */             jar_version = "5.7.6.0";
/* 311 */             jar_file = f;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 318 */       if (jar_file == null)
/*     */       {
/* 320 */         throw new Exception("Failed to locate an 'Azureus2.jar' to use for the challenge protocol");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 325 */       request.put("az-id", id);
/* 326 */       request.put("type", "both");
/* 327 */       request.put("jar_ver", jar_version);
/*     */       
/* 329 */       if (this.detectedRouter != null)
/*     */       {
/* 331 */         request.put("router", this.detectedRouter);
/*     */       }
/*     */       
/* 334 */       Map result = sendRequest(request);
/*     */       
/* 336 */       this.challenge_id = ((byte[])result.get("challenge_id"));
/*     */       
/* 338 */       if (this.challenge_id == null)
/*     */       {
/* 340 */         throw new IllegalStateException("No challenge returned from speed test scheduling service");
/*     */       }
/*     */       
/* 343 */       Long responseType = (Long)result.get("reply_type");
/*     */       
/* 345 */       if (responseType.intValue() == 1)
/*     */       {
/* 347 */         result = handleChallengeFromSpeedTestService(jar_file, result);
/*     */         
/* 349 */         responseType = (Long)result.get("reply_type");
/*     */       }
/*     */       
/* 352 */       if (responseType == null) {
/* 353 */         throw new IllegalStateException("No challenge response returned from speed test scheduling service");
/*     */       }
/*     */       
/* 356 */       if (responseType.intValue() == 0)
/*     */       {
/*     */ 
/*     */ 
/* 360 */         Long time = (Long)result.get("time");
/* 361 */         Long limit = (Long)result.get("limit");
/*     */         
/* 363 */         if ((time == null) || (limit == null)) {
/* 364 */           throw new IllegalArgumentException("Returned time or limit parameter is null");
/*     */         }
/*     */         
/* 367 */         this.delay_millis = time.longValue();
/* 368 */         this.max_speed = limit.longValue();
/*     */         
/*     */ 
/*     */ 
/* 372 */         Map torrentMap = (Map)result.get("torrent");
/*     */         
/* 374 */         this.test_torrent = TOTorrentFactory.deserialiseFromMap(torrentMap);
/*     */         
/* 376 */         return true;
/*     */       }
/*     */       
/* 379 */       throw new IllegalStateException("Unrecognized response from speed test scheduling servcie.");
/*     */ 
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 384 */       Debug.printStackTrace(t);
/*     */       
/* 386 */       this.tester.abort(MessageText.getString("SpeedTestWizard.abort.message.scheduling.failed"), t);
/*     */     }
/* 388 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String getVersionFromJAR(File jar_file)
/*     */   {
/*     */     try
/*     */     {
/* 400 */       ClassLoader parent = new ClassLoader()
/*     */       {
/*     */ 
/*     */ 
/*     */         protected synchronized Class loadClass(String name, boolean resolve)
/*     */           throws ClassNotFoundException
/*     */         {
/*     */ 
/*     */ 
/* 409 */           if (name.equals("org.gudy.azureus2.core3.util.Constants"))
/*     */           {
/* 411 */             throw new ClassNotFoundException();
/*     */           }
/*     */           
/* 414 */           return super.loadClass(name, resolve);
/*     */         }
/*     */         
/* 417 */       };
/* 418 */       ClassLoader cl = new URLClassLoader(new URL[] { jar_file.toURI().toURL() }, parent);
/*     */       
/* 420 */       Class c = cl.loadClass("org.gudy.azureus2.core3.util.Constants");
/*     */       
/* 422 */       Field field = c.getField("AZUREUS_VERSION");
/*     */       
/* 424 */       return (String)field.get(null);
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 428 */     return null;
/*     */   }
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
/*     */   private Map handleChallengeFromSpeedTestService(File jar_file, Map result)
/*     */     throws IOException
/*     */   {
/* 451 */     retVal = new HashMap();
/* 452 */     RandomAccessFile raf = null;
/*     */     try
/*     */     {
/* 455 */       Long size = (Long)result.get("size");
/* 456 */       Long offset = (Long)result.get("offset");
/*     */       
/* 458 */       if ((size == null) || (offset == null)) {
/* 459 */         throw new IllegalStateException("scheduleTestWithSpeedTestService had a null parameter.");
/*     */       }
/*     */       
/*     */ 
/* 463 */       raf = new RandomAccessFile(jar_file, "r");
/* 464 */       byte[] jarBytes = new byte[size.intValue()];
/*     */       
/* 466 */       raf.seek(offset.intValue());
/* 467 */       raf.read(jarBytes);
/*     */       
/*     */ 
/*     */ 
/* 471 */       Map request = new HashMap();
/* 472 */       request.put("request_type", new Long(1L));
/* 473 */       request.put("challenge_id", this.challenge_id);
/* 474 */       request.put("data", jarBytes);
/*     */       
/* 476 */       return sendRequest(request);
/*     */     }
/*     */     finally
/*     */     {
/*     */       try {
/* 481 */         if (raf != null)
/* 482 */           raf.close();
/*     */       } catch (Throwable t) {
/* 484 */         Debug.printStackTrace(t);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void sendResult(NetworkAdminSpeedTesterResult result)
/*     */   {
/*     */     try
/*     */     {
/* 497 */       if (this.challenge_id != null)
/*     */       {
/* 499 */         Map request = new HashMap();
/*     */         
/* 501 */         request.put("request_type", new Long(2L));
/*     */         
/* 503 */         request.put("challenge_id", this.challenge_id);
/*     */         
/* 505 */         request.put("type", new Long(this.tester.getTestType()));
/* 506 */         request.put("mode", new Long(this.tester.getMode()));
/* 507 */         request.put("crypto", new Long(this.tester.getUseCrypto() ? 1L : 0L));
/*     */         
/* 509 */         if (result.hadError())
/*     */         {
/* 511 */           request.put("result", new Long(0L));
/*     */           
/* 513 */           request.put("error", result.getLastError());
/*     */         }
/*     */         else
/*     */         {
/* 517 */           request.put("result", new Long(1L));
/*     */           
/* 519 */           request.put("maxup", new Long(result.getUploadSpeed()));
/* 520 */           request.put("maxdown", new Long(result.getDownloadSpeed()));
/*     */         }
/*     */         
/* 523 */         sendRequest(request);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 527 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private Map sendRequest(Map request)
/*     */     throws IOException
/*     */   {
/* 537 */     request.put("ver", new Long(1L));
/* 538 */     request.put("locale", MessageText.getCurrentLocale().toString());
/*     */     
/* 540 */     String speedTestServiceName = System.getProperty("speedtest.service.ip.address", "speedtest.vuze.com");
/*     */     
/* 542 */     URL urlRequestTest = new URL("http://" + speedTestServiceName + ":60000/scheduletest?request=" + URLEncoder.encode(new String(BEncoder.encode(request), "ISO-8859-1"), "ISO-8859-1"));
/*     */     
/*     */ 
/* 545 */     return getBEncodedMapFromRequest(urlRequestTest);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Map getBEncodedMapFromRequest(URL url)
/*     */     throws IOException
/*     */   {
/* 558 */     ResourceDownloader rd = ResourceDownloaderFactoryImpl.getSingleton().create(url);
/*     */     
/* 560 */     InputStream is = null;
/* 561 */     reply = new HashMap();
/*     */     try
/*     */     {
/* 564 */       is = rd.download();
/* 565 */       reply = BDecoder.decode(new BufferedInputStream(is));
/*     */       
/*     */ 
/* 568 */       Long res = (Long)reply.get("result");
/* 569 */       if (res == null)
/* 570 */         throw new IllegalStateException("No result parameter in the response!! reply=" + reply);
/* 571 */       if (res.intValue() == 0) {
/* 572 */         StringBuilder msg = new StringBuilder("Server failed. ");
/* 573 */         String error = new String((byte[])reply.get("error"));
/* 574 */         String errDetail = new String((byte[])reply.get("error_detail"));
/* 575 */         msg.append("Error: ").append(error);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 580 */         Debug.outNoStack("SpeedCheck server returned an error: " + error + ", details=" + errDetail);
/*     */         
/* 582 */         throw new IOException(msg.toString());
/*     */       }
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
/* 598 */       return reply;
/*     */     }
/*     */     catch (IOException ise)
/*     */     {
/* 586 */       throw ise;
/*     */     } catch (Throwable t) {
/* 588 */       Debug.out(t);
/* 589 */       Debug.printStackTrace(t);
/*     */     } finally {
/*     */       try {
/* 592 */         if (is != null)
/* 593 */           is.close();
/*     */       } catch (Throwable e) {
/* 595 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected synchronized void resetSpeedLimits()
/*     */   {
/* 608 */     if (this.preTestSettings != null)
/*     */     {
/* 610 */       this.preTestSettings.restoreLimits();
/*     */       
/* 612 */       this.preTestSettings = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected synchronized void setSpeedLimits()
/*     */   {
/* 623 */     resetSpeedLimits();
/*     */     
/* 625 */     this.preTestSettings = new SpeedTestDownloadState();
/*     */     
/* 627 */     this.preTestSettings.saveLimits();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   class SpeedTestDownloadState
/*     */     implements ParameterListener, DownloadManagerListener
/*     */   {
/* 641 */     private final Map torrentLimits = new HashMap();
/*     */     
/*     */ 
/*     */     public static final String TORRENT_UPLOAD_LIMIT = "u";
/*     */     
/*     */ 
/*     */     public static final String TORRENT_DOWNLOAD_LIMIT = "d";
/*     */     
/*     */     int maxUploadKbs;
/*     */     
/*     */     int maxUploadSeedingKbs;
/*     */     
/*     */     int maxDownloadKbs;
/*     */     
/*     */     boolean autoSpeedEnabled;
/*     */     
/*     */     boolean autoSpeedSeedingEnabled;
/*     */     
/*     */     boolean LANSpeedEnabled;
/*     */     
/*     */ 
/*     */     public SpeedTestDownloadState() {}
/*     */     
/*     */ 
/*     */     public void parameterChanged(String name)
/*     */     {
/* 667 */       String trace = Debug.getCompressedStackTrace();
/*     */       
/* 669 */       NetworkAdminSpeedTestScheduledTestImpl.this.abort("Configuration parameter '" + name + "' changed (new value=" + COConfigurationManager.getParameter(name) + ") during test (" + trace + ")");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void downloadAdded(Download download)
/*     */     {
/* 676 */       if (NetworkAdminSpeedTestScheduledTestImpl.this.test_torrent != null) {
/*     */         try
/*     */         {
/* 679 */           if (Arrays.equals(download.getTorrent().getHash(), NetworkAdminSpeedTestScheduledTestImpl.this.test_torrent.getHash()))
/*     */           {
/* 681 */             return;
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 685 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */       
/* 689 */       String downloadAdded = MessageText.getString("SpeedTestWizard.abort.message.download.added", new String[] { download.getName() });
/*     */       
/* 691 */       NetworkAdminSpeedTestScheduledTestImpl.this.abort(downloadAdded);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void downloadRemoved(Download download) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void saveLimits()
/*     */     {
/* 706 */       PluginConfigImpl.setEnablePluginCoreConfigChange(false);
/*     */       
/* 708 */       NetworkAdminSpeedTestScheduledTestImpl.this.plugin.getDownloadManager().addListener(this, false);
/*     */       
/*     */ 
/* 711 */       Download[] d = NetworkAdminSpeedTestScheduledTestImpl.this.plugin.getDownloadManager().getDownloads();
/* 712 */       if (d != null) {
/* 713 */         int len = d.length;
/* 714 */         for (int i = 0; i < len; i++)
/*     */         {
/* 716 */           NetworkAdminSpeedTestScheduledTestImpl.this.plugin.getDownloadManager().getStats();
/* 717 */           int downloadLimit = d[i].getDownloadRateLimitBytesPerSecond();
/* 718 */           int uploadLimit = d[i].getUploadRateLimitBytesPerSecond();
/*     */           
/* 720 */           setDownloadDetails(d[i], uploadLimit, downloadLimit);
/*     */           
/* 722 */           d[i].setUploadRateLimitBytesPerSecond(-1);
/* 723 */           d[i].setDownloadRateLimitBytesPerSecond(-1);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 729 */       saveGlobalLimits();
/*     */       
/* 731 */       COConfigurationManager.setParameter("LAN Speed Enabled", false);
/*     */       
/* 733 */       COConfigurationManager.setParameter("Auto Upload Speed Enabled", false);
/* 734 */       COConfigurationManager.setParameter("Auto Upload Speed Seeding Enabled", false);
/*     */       
/* 736 */       COConfigurationManager.setParameter("Max Upload Speed KBs", NetworkAdminSpeedTestScheduledTestImpl.this.max_speed);
/* 737 */       COConfigurationManager.setParameter("Max Upload Speed Seeding KBs", NetworkAdminSpeedTestScheduledTestImpl.this.max_speed);
/* 738 */       COConfigurationManager.setParameter("Max Download Speed KBs", NetworkAdminSpeedTestScheduledTestImpl.this.max_speed);
/*     */       
/* 740 */       String[] params = TransferSpeedValidator.CONFIG_PARAMS;
/*     */       
/* 742 */       for (int i = 0; i < params.length; i++) {
/* 743 */         COConfigurationManager.addParameterListener(params[i], this);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void restoreLimits()
/*     */     {
/* 750 */       String[] params = TransferSpeedValidator.CONFIG_PARAMS;
/*     */       
/* 752 */       for (int i = 0; i < params.length; i++) {
/* 753 */         COConfigurationManager.removeParameterListener(params[i], this);
/*     */       }
/*     */       
/* 756 */       NetworkAdminSpeedTestScheduledTestImpl.this.plugin.getDownloadManager().removeListener(this);
/*     */       
/* 758 */       restoreGlobalLimits();
/*     */       
/* 760 */       restoreIndividualLimits();
/*     */       
/* 762 */       PluginConfigImpl.setEnablePluginCoreConfigChange(true);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private void saveGlobalLimits()
/*     */     {
/* 770 */       this.maxUploadKbs = COConfigurationManager.getIntParameter("Max Upload Speed KBs");
/* 771 */       this.maxUploadSeedingKbs = COConfigurationManager.getIntParameter("Max Upload Speed Seeding KBs");
/* 772 */       this.maxDownloadKbs = COConfigurationManager.getIntParameter("Max Download Speed KBs");
/*     */       
/* 774 */       this.autoSpeedEnabled = COConfigurationManager.getBooleanParameter("Auto Upload Speed Enabled");
/* 775 */       this.autoSpeedSeedingEnabled = COConfigurationManager.getBooleanParameter("Auto Upload Speed Seeding Enabled");
/*     */       
/* 777 */       this.LANSpeedEnabled = COConfigurationManager.getBooleanParameter("LAN Speed Enabled");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private void restoreGlobalLimits()
/*     */     {
/* 785 */       COConfigurationManager.setParameter("LAN Speed Enabled", this.LANSpeedEnabled);
/*     */       
/* 787 */       COConfigurationManager.setParameter("Auto Upload Speed Enabled", this.autoSpeedEnabled);
/* 788 */       COConfigurationManager.setParameter("Auto Upload Speed Seeding Enabled", this.autoSpeedSeedingEnabled);
/*     */       
/* 790 */       COConfigurationManager.setParameter("Max Upload Speed KBs", this.maxUploadKbs);
/* 791 */       COConfigurationManager.setParameter("Max Upload Speed Seeding KBs", this.maxUploadSeedingKbs);
/* 792 */       COConfigurationManager.setParameter("Max Download Speed KBs", this.maxDownloadKbs);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private void restoreIndividualLimits()
/*     */     {
/* 800 */       Download[] downloads = getAllDownloads();
/* 801 */       if (downloads != null) {
/* 802 */         int nDownloads = downloads.length;
/*     */         
/* 804 */         for (int i = 0; i < nDownloads; i++) {
/* 805 */           int uploadLimit = getDownloadDetails(downloads[i], "u");
/* 806 */           int downLimit = getDownloadDetails(downloads[i], "d");
/*     */           
/* 808 */           downloads[i].setDownloadRateLimitBytesPerSecond(downLimit);
/* 809 */           downloads[i].setUploadRateLimitBytesPerSecond(uploadLimit);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private void setDownloadDetails(Download d, int uploadLimit, int downloadLimit)
/*     */     {
/* 820 */       if (d == null) {
/* 821 */         throw new IllegalArgumentException("Download should not be null.");
/*     */       }
/* 823 */       Map props = new HashMap();
/*     */       
/* 825 */       props.put("u", new Integer(uploadLimit));
/* 826 */       props.put("d", new Integer(downloadLimit));
/*     */       
/* 828 */       this.torrentLimits.put(d, props);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private int getDownloadDetails(Download d, String param)
/*     */     {
/* 838 */       if ((d == null) || (param == null)) {
/* 839 */         throw new IllegalArgumentException("null inputs.");
/*     */       }
/* 841 */       if ((!param.equals("u")) && (!param.equals("d"))) {
/* 842 */         throw new IllegalArgumentException("invalid param. param=" + param);
/*     */       }
/* 844 */       Map out = (Map)this.torrentLimits.get(d);
/* 845 */       Integer limit = (Integer)out.get(param);
/*     */       
/* 847 */       return limit.intValue();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private Download[] getAllDownloads()
/*     */     {
/* 855 */       Download[] a = new Download[0];
/* 856 */       return (Download[])this.torrentLimits.keySet().toArray(a);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void reportStage(String str)
/*     */   {
/* 865 */     Iterator it = this.listeners.iterator();
/*     */     
/* 867 */     while (it.hasNext()) {
/*     */       try
/*     */       {
/* 870 */         ((NetworkAdminSpeedTestScheduledTestListener)it.next()).stage(this, str);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 874 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void reportComplete()
/*     */   {
/* 882 */     resetSpeedLimits();
/*     */     
/* 884 */     Iterator it = this.listeners.iterator();
/*     */     
/* 886 */     while (it.hasNext()) {
/*     */       try
/*     */       {
/* 889 */         ((NetworkAdminSpeedTestScheduledTestListener)it.next()).complete(this);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 893 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(NetworkAdminSpeedTestScheduledTestListener listener)
/*     */   {
/* 902 */     this.listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(NetworkAdminSpeedTestScheduledTestListener listener)
/*     */   {
/* 909 */     this.listeners.remove(listener);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminSpeedTestScheduledTestImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */