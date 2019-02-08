/*      */ package com.aelitis.net.upnp.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.proxy.AEProxySelector;
/*      */ import com.aelitis.azureus.core.proxy.AEProxySelectorFactory;
/*      */ import com.aelitis.azureus.core.util.HTTPUtils;
/*      */ import com.aelitis.azureus.core.util.NetUtils;
/*      */ import com.aelitis.net.upnp.UPnP;
/*      */ import com.aelitis.net.upnp.UPnPAdapter;
/*      */ import com.aelitis.net.upnp.UPnPDevice;
/*      */ import com.aelitis.net.upnp.UPnPException;
/*      */ import com.aelitis.net.upnp.UPnPListener;
/*      */ import com.aelitis.net.upnp.UPnPLogListener;
/*      */ import com.aelitis.net.upnp.UPnPRootDevice;
/*      */ import com.aelitis.net.upnp.UPnPSSDP;
/*      */ import com.aelitis.net.upnp.UPnPService;
/*      */ import com.aelitis.net.upnp.impl.device.UPnPDeviceImpl;
/*      */ import com.aelitis.net.upnp.impl.device.UPnPRootDeviceImpl;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.PrintWriter;
/*      */ import java.net.HttpURLConnection;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.NetworkInterface;
/*      */ import java.net.Proxy;
/*      */ import java.net.Socket;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.ThreadPool;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderAdapter;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*      */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocument;
/*      */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class UPnPImpl
/*      */   extends ResourceDownloaderAdapter
/*      */   implements UPnP, SSDPIGDListener
/*      */ {
/*      */   public static final String NL = "\r\n";
/*      */   private static UPnPImpl singleton;
/*   61 */   private static AEMonitor class_mon = new AEMonitor("UPnP:class");
/*      */   
/*      */   private UPnPAdapter adapter;
/*      */   private SSDPIGD ssdp;
/*      */   
/*      */   public static UPnP getSingleton(UPnPAdapter adapter, String[] selected_interfaces)
/*      */     throws UPnPException
/*      */   {
/*      */     try
/*      */     {
/*   71 */       class_mon.enter();
/*      */       
/*   73 */       if (singleton == null)
/*      */       {
/*   75 */         singleton = new UPnPImpl(adapter, selected_interfaces);
/*      */       }
/*      */       
/*   78 */       return singleton;
/*      */     }
/*      */     finally
/*      */     {
/*   82 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   89 */   private Map<String, UPnPRootDeviceImpl> root_locations = new HashMap();
/*      */   
/*   91 */   private List log_listeners = new ArrayList();
/*   92 */   private List log_history = new ArrayList();
/*   93 */   private List log_alert_history = new ArrayList();
/*      */   
/*   95 */   private List<UPnPListener> rd_listeners = new ArrayList();
/*   96 */   private AEMonitor rd_listeners_mon = new AEMonitor("UPnP:L");
/*      */   
/*   98 */   private int http_calls_ok = 0;
/*   99 */   private int direct_calls_ok = 0;
/*      */   
/*  101 */   private int trace_index = 0;
/*      */   
/*  103 */   private AsyncDispatcher async_dispatcher = new AsyncDispatcher();
/*      */   
/*  105 */   private ThreadPool device_dispatcher = new ThreadPool("UPnPDispatcher", 1, true);
/*  106 */   private Set device_dispatcher_pending = new HashSet();
/*      */   
/*  108 */   private Map<String, long[]> failed_urls = new HashMap();
/*      */   
/*  110 */   protected AEMonitor this_mon = new AEMonitor("UPnP");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected UPnPImpl(UPnPAdapter _adapter, String[] _selected_interfaces)
/*      */     throws UPnPException
/*      */   {
/*  119 */     this.adapter = _adapter;
/*      */     
/*  121 */     this.ssdp = SSDPIGDFactory.create(this, _selected_interfaces);
/*      */     
/*  123 */     this.ssdp.addListener(this);
/*      */     
/*  125 */     this.ssdp.start();
/*      */   }
/*      */   
/*      */ 
/*      */   public UPnPSSDP getSSDP()
/*      */   {
/*  131 */     return this.ssdp.getSSDP();
/*      */   }
/*      */   
/*      */ 
/*      */   public void injectDiscoveryCache(Map cache)
/*      */   {
/*      */     try
/*      */     {
/*  139 */       String ni_s = new String((byte[])cache.get("ni"), "UTF-8");
/*  140 */       String la_s = new String((byte[])cache.get("la"), "UTF-8");
/*  141 */       String usn = new String((byte[])cache.get("usn"), "UTF-8");
/*  142 */       String loc_s = new String((byte[])cache.get("loc"), "UTF-8");
/*      */       
/*  144 */       NetworkInterface network_interface = NetUtils.getByName(ni_s);
/*      */       
/*  146 */       if (network_interface == null)
/*      */       {
/*  148 */         return;
/*      */       }
/*      */       
/*  151 */       InetAddress local_address = InetAddress.getByName(la_s);
/*      */       
/*  153 */       URL location = new URL(loc_s);
/*      */       
/*  155 */       rootDiscovered(network_interface, local_address, usn, location);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  159 */       Debug.out(e);
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
/*      */   public void rootDiscovered(final NetworkInterface network_interface, final InetAddress local_address, final String usn, final URL location)
/*      */   {
/*      */     try
/*      */     {
/*  175 */       this.rd_listeners_mon.enter();
/*      */       
/*  177 */       if (this.device_dispatcher_pending.contains(usn)) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  184 */       if (this.device_dispatcher_pending.size() > 512)
/*      */       {
/*  186 */         Debug.out("Device dispatcher queue is full - dropping discovery of " + usn + "/" + location);
/*      */       }
/*      */       
/*  189 */       this.device_dispatcher_pending.add(usn);
/*      */     }
/*      */     finally
/*      */     {
/*  193 */       this.rd_listeners_mon.exit();
/*      */     }
/*      */     
/*  196 */     this.device_dispatcher.run(new AERunnable()
/*      */     {
/*      */       public void runSupport()
/*      */       {
/*      */         UPnPRootDeviceImpl old_root_device;
/*      */         
/*      */ 
/*      */         try
/*      */         {
/*  205 */           UPnPImpl.this.rd_listeners_mon.enter();
/*      */           
/*  207 */           old_root_device = (UPnPRootDeviceImpl)UPnPImpl.this.root_locations.get(usn);
/*      */           
/*  209 */           UPnPImpl.this.device_dispatcher_pending.remove(usn);
/*      */         }
/*      */         finally
/*      */         {
/*  213 */           UPnPImpl.this.rd_listeners_mon.exit();
/*      */         }
/*      */         
/*  216 */         if (old_root_device != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  221 */           if (!old_root_device.getNetworkInterface().getName().equals(network_interface.getName()))
/*      */           {
/*  223 */             if (old_root_device.addAlternativeLocation(location))
/*      */             {
/*  225 */               UPnPImpl.this.log("Adding alternative location " + location + " to " + usn);
/*      */             }
/*      */             
/*  228 */             return;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  233 */           if (old_root_device.getLocation().equals(location))
/*      */           {
/*  235 */             return;
/*      */           }
/*      */         }
/*      */         
/*  239 */         if (old_root_device != null)
/*      */         {
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/*      */ 
/*  247 */             UPnPImpl.this.rd_listeners_mon.enter();
/*      */             
/*  249 */             UPnPImpl.this.root_locations.remove(usn);
/*      */           }
/*      */           finally
/*      */           {
/*  253 */             UPnPImpl.this.rd_listeners_mon.exit();
/*      */           }
/*      */           
/*  256 */           old_root_device.destroy(true);
/*      */         }
/*      */         
/*      */         Object listeners;
/*      */         try
/*      */         {
/*  262 */           UPnPImpl.this.rd_listeners_mon.enter();
/*      */           
/*  264 */           listeners = new ArrayList(UPnPImpl.this.rd_listeners);
/*      */         }
/*      */         finally
/*      */         {
/*  268 */           UPnPImpl.this.rd_listeners_mon.exit();
/*      */         }
/*      */         
/*  271 */         for (int i = 0; i < ((List)listeners).size(); i++) {
/*      */           try
/*      */           {
/*  274 */             if (!((UPnPListener)((List)listeners).get(i)).deviceDiscovered(usn, location))
/*      */             {
/*  276 */               return;
/*      */             }
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  281 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */         
/*  285 */         UPnPImpl.this.log("UPnP: root discovered: usn=" + usn + ", location=" + location + ", ni=" + network_interface.getName() + ",local=" + local_address.toString());
/*      */         try
/*      */         {
/*  288 */           UPnPRootDeviceImpl new_root_device = new UPnPRootDeviceImpl(UPnPImpl.this, network_interface, local_address, usn, location);
/*      */           try
/*      */           {
/*  291 */             UPnPImpl.this.rd_listeners_mon.enter();
/*      */             
/*  293 */             UPnPImpl.this.root_locations.put(usn, new_root_device);
/*      */             
/*  295 */             listeners = new ArrayList(UPnPImpl.this.rd_listeners);
/*      */           }
/*      */           finally
/*      */           {
/*  299 */             UPnPImpl.this.rd_listeners_mon.exit();
/*      */           }
/*      */           
/*  302 */           for (int i = 0; i < ((List)listeners).size(); i++) {
/*      */             try
/*      */             {
/*  305 */               ((UPnPListener)((List)listeners).get(i)).rootDeviceFound(new_root_device);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  309 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (UPnPException e)
/*      */         {
/*  315 */           String message = e.getMessage();
/*      */           
/*  317 */           String msg = message == null ? Debug.getNestedExceptionMessageAndStack(e) : message;
/*      */           
/*  319 */           UPnPImpl.this.adapter.log(msg);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void rootAlive(String usn, URL location)
/*      */   {
/*  330 */     UPnPRootDeviceImpl root_device = (UPnPRootDeviceImpl)this.root_locations.get(usn);
/*      */     
/*  332 */     if (root_device == null)
/*      */     {
/*  334 */       this.ssdp.searchNow();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void rootLost(InetAddress local_address, final String usn)
/*      */   {
/*  345 */     this.device_dispatcher.run(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*  351 */         UPnPRootDeviceImpl root_device = null;
/*      */         try
/*      */         {
/*  354 */           UPnPImpl.this.rd_listeners_mon.enter();
/*      */           
/*  356 */           root_device = (UPnPRootDeviceImpl)UPnPImpl.this.root_locations.remove(usn);
/*      */         }
/*      */         finally
/*      */         {
/*  360 */           UPnPImpl.this.rd_listeners_mon.exit();
/*      */         }
/*      */         
/*  363 */         if (root_device == null)
/*      */         {
/*  365 */           return;
/*      */         }
/*      */         
/*  368 */         UPnPImpl.this.log("UPnP: root lost: usn=" + usn + ", location=" + root_device.getLocation() + ", ni=" + root_device.getNetworkInterface().getName() + ",local=" + root_device.getLocalAddress().toString());
/*      */         
/*  370 */         root_device.destroy(false);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void interfaceChanged(NetworkInterface network_interface)
/*      */   {
/*  379 */     reset();
/*      */   }
/*      */   
/*      */ 
/*      */   public void search()
/*      */   {
/*  385 */     this.ssdp.searchNow();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void search(String[] STs)
/*      */   {
/*  392 */     this.ssdp.searchNow(STs);
/*      */   }
/*      */   
/*      */ 
/*      */   public void reset()
/*      */   {
/*  398 */     log("UPnP: reset");
/*      */     
/*      */     List roots;
/*      */     try
/*      */     {
/*  403 */       this.rd_listeners_mon.enter();
/*      */       
/*  405 */       roots = new ArrayList(this.root_locations.values());
/*      */       
/*  407 */       this.root_locations.clear();
/*      */     }
/*      */     finally
/*      */     {
/*  411 */       this.rd_listeners_mon.exit();
/*      */     }
/*      */     
/*  414 */     for (int i = 0; i < roots.size(); i++)
/*      */     {
/*  416 */       ((UPnPRootDeviceImpl)roots.get(i)).destroy(true);
/*      */     }
/*      */     
/*  419 */     this.ssdp.searchNow();
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public SimpleXMLParserDocument parseXML(InputStream _is)
/*      */     throws SimpleXMLParserDocumentException, IOException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aconst_null
/*      */     //   1: astore_2
/*      */     //   2: new 455	java/io/ByteArrayOutputStream
/*      */     //   5: dup
/*      */     //   6: sipush 1024
/*      */     //   9: invokespecial 826	java/io/ByteArrayOutputStream:<init>	(I)V
/*      */     //   12: astore_2
/*      */     //   13: sipush 8192
/*      */     //   16: newarray <illegal type>
/*      */     //   18: astore_3
/*      */     //   19: aload_1
/*      */     //   20: aload_3
/*      */     //   21: invokevirtual 835	java/io/InputStream:read	([B)I
/*      */     //   24: istore 4
/*      */     //   26: iload 4
/*      */     //   28: ifgt +6 -> 34
/*      */     //   31: goto +14 -> 45
/*      */     //   34: aload_2
/*      */     //   35: aload_3
/*      */     //   36: iconst_0
/*      */     //   37: iload 4
/*      */     //   39: invokevirtual 827	java/io/ByteArrayOutputStream:write	([BII)V
/*      */     //   42: goto -23 -> 19
/*      */     //   45: aload_2
/*      */     //   46: invokevirtual 824	java/io/ByteArrayOutputStream:close	()V
/*      */     //   49: goto +12 -> 61
/*      */     //   52: astore 5
/*      */     //   54: aload_2
/*      */     //   55: invokevirtual 824	java/io/ByteArrayOutputStream:close	()V
/*      */     //   58: aload 5
/*      */     //   60: athrow
/*      */     //   61: aload_2
/*      */     //   62: invokevirtual 825	java/io/ByteArrayOutputStream:toByteArray	()[B
/*      */     //   65: astore_3
/*      */     //   66: new 454	java/io/ByteArrayInputStream
/*      */     //   69: dup
/*      */     //   70: aload_3
/*      */     //   71: invokespecial 823	java/io/ByteArrayInputStream:<init>	([B)V
/*      */     //   74: astore 4
/*      */     //   76: new 471	java/lang/StringBuilder
/*      */     //   79: dup
/*      */     //   80: sipush 1024
/*      */     //   83: invokespecial 860	java/lang/StringBuilder:<init>	(I)V
/*      */     //   86: astore 5
/*      */     //   88: new 461	java/io/LineNumberReader
/*      */     //   91: dup
/*      */     //   92: new 460	java/io/InputStreamReader
/*      */     //   95: dup
/*      */     //   96: aload 4
/*      */     //   98: ldc 48
/*      */     //   100: invokespecial 836	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/lang/String;)V
/*      */     //   103: invokespecial 837	java/io/LineNumberReader:<init>	(Ljava/io/Reader;)V
/*      */     //   106: astore 6
/*      */     //   108: aconst_null
/*      */     //   109: astore 7
/*      */     //   111: aload 6
/*      */     //   113: invokevirtual 838	java/io/LineNumberReader:readLine	()Ljava/lang/String;
/*      */     //   116: astore 8
/*      */     //   118: aload 8
/*      */     //   120: ifnonnull +6 -> 126
/*      */     //   123: goto +163 -> 286
/*      */     //   126: iconst_0
/*      */     //   127: istore 9
/*      */     //   129: iload 9
/*      */     //   131: aload 8
/*      */     //   133: invokevirtual 849	java/lang/String:length	()I
/*      */     //   136: if_icmpge +139 -> 275
/*      */     //   139: aload 8
/*      */     //   141: iload 9
/*      */     //   143: invokevirtual 850	java/lang/String:charAt	(I)C
/*      */     //   146: istore 10
/*      */     //   148: iload 10
/*      */     //   150: bipush 32
/*      */     //   152: if_icmpge +109 -> 261
/*      */     //   155: iload 10
/*      */     //   157: bipush 13
/*      */     //   159: if_icmpeq +102 -> 261
/*      */     //   162: iload 10
/*      */     //   164: bipush 9
/*      */     //   166: if_icmpeq +95 -> 261
/*      */     //   169: aload 5
/*      */     //   171: bipush 32
/*      */     //   173: invokevirtual 862	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
/*      */     //   176: pop
/*      */     //   177: aload 7
/*      */     //   179: ifnonnull +12 -> 191
/*      */     //   182: new 484	java/util/HashSet
/*      */     //   185: dup
/*      */     //   186: invokespecial 894	java/util/HashSet:<init>	()V
/*      */     //   189: astore 7
/*      */     //   191: new 466	java/lang/Character
/*      */     //   194: dup
/*      */     //   195: iload 10
/*      */     //   197: invokespecial 846	java/lang/Character:<init>	(C)V
/*      */     //   200: astore 11
/*      */     //   202: aload 7
/*      */     //   204: aload 11
/*      */     //   206: invokeinterface 948 2 0
/*      */     //   211: ifne +47 -> 258
/*      */     //   214: aload 7
/*      */     //   216: aload 11
/*      */     //   218: invokeinterface 947 2 0
/*      */     //   223: pop
/*      */     //   224: aload_0
/*      */     //   225: getfield 784	com/aelitis/net/upnp/impl/UPnPImpl:adapter	Lcom/aelitis/net/upnp/UPnPAdapter;
/*      */     //   228: new 471	java/lang/StringBuilder
/*      */     //   231: dup
/*      */     //   232: invokespecial 859	java/lang/StringBuilder:<init>	()V
/*      */     //   235: ldc 5
/*      */     //   237: invokevirtual 865	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   240: iload 10
/*      */     //   242: invokevirtual 863	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   245: ldc 8
/*      */     //   247: invokevirtual 865	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   250: invokevirtual 861	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   253: invokeinterface 915 2 0
/*      */     //   258: goto +11 -> 269
/*      */     //   261: aload 5
/*      */     //   263: iload 10
/*      */     //   265: invokevirtual 862	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
/*      */     //   268: pop
/*      */     //   269: iinc 9 1
/*      */     //   272: goto -143 -> 129
/*      */     //   275: aload 5
/*      */     //   277: ldc 2
/*      */     //   279: invokevirtual 865	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   282: pop
/*      */     //   283: goto -172 -> 111
/*      */     //   286: aload 5
/*      */     //   288: invokevirtual 861	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   291: astore 8
/*      */     //   293: aload_0
/*      */     //   294: getfield 784	com/aelitis/net/upnp/impl/UPnPImpl:adapter	Lcom/aelitis/net/upnp/UPnPAdapter;
/*      */     //   297: new 471	java/lang/StringBuilder
/*      */     //   300: dup
/*      */     //   301: invokespecial 859	java/lang/StringBuilder:<init>	()V
/*      */     //   304: ldc 46
/*      */     //   306: invokevirtual 865	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   309: aload 8
/*      */     //   311: invokevirtual 865	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   314: invokevirtual 861	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   317: invokeinterface 915 2 0
/*      */     //   322: aload_0
/*      */     //   323: getfield 784	com/aelitis/net/upnp/impl/UPnPImpl:adapter	Lcom/aelitis/net/upnp/UPnPAdapter;
/*      */     //   326: aload 8
/*      */     //   328: invokeinterface 918 2 0
/*      */     //   333: astore 9
/*      */     //   335: aload 9
/*      */     //   337: areturn
/*      */     //   338: astore 9
/*      */     //   340: aload 8
/*      */     //   342: ldc 19
/*      */     //   344: invokevirtual 851	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
/*      */     //   347: ifeq +26 -> 373
/*      */     //   350: aload 8
/*      */     //   352: ldc 19
/*      */     //   354: ldc 20
/*      */     //   356: invokevirtual 858	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
/*      */     //   359: astore 8
/*      */     //   361: aload_0
/*      */     //   362: getfield 784	com/aelitis/net/upnp/impl/UPnPImpl:adapter	Lcom/aelitis/net/upnp/UPnPAdapter;
/*      */     //   365: aload 8
/*      */     //   367: invokeinterface 918 2 0
/*      */     //   372: areturn
/*      */     //   373: aload 9
/*      */     //   375: athrow
/*      */     //   376: astore 5
/*      */     //   378: new 457	java/io/FileOutputStream
/*      */     //   381: dup
/*      */     //   382: aload_0
/*      */     //   383: invokevirtual 808	com/aelitis/net/upnp/impl/UPnPImpl:getTraceFile	()Ljava/io/File;
/*      */     //   386: invokespecial 831	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
/*      */     //   389: astore 6
/*      */     //   391: aload 6
/*      */     //   393: aload_3
/*      */     //   394: invokevirtual 830	java/io/FileOutputStream:write	([B)V
/*      */     //   397: aload 6
/*      */     //   399: invokevirtual 829	java/io/FileOutputStream:close	()V
/*      */     //   402: goto +13 -> 415
/*      */     //   405: astore 12
/*      */     //   407: aload 6
/*      */     //   409: invokevirtual 829	java/io/FileOutputStream:close	()V
/*      */     //   412: aload 12
/*      */     //   414: athrow
/*      */     //   415: goto +16 -> 431
/*      */     //   418: astore 6
/*      */     //   420: aload_0
/*      */     //   421: getfield 784	com/aelitis/net/upnp/impl/UPnPImpl:adapter	Lcom/aelitis/net/upnp/UPnPAdapter;
/*      */     //   424: aload 6
/*      */     //   426: invokeinterface 916 2 0
/*      */     //   431: aload 5
/*      */     //   433: instanceof 501
/*      */     //   436: ifeq +9 -> 445
/*      */     //   439: aload 5
/*      */     //   441: checkcast 501	org/gudy/azureus2/plugins/utils/xml/simpleparser/SimpleXMLParserDocumentException
/*      */     //   444: athrow
/*      */     //   445: new 501	org/gudy/azureus2/plugins/utils/xml/simpleparser/SimpleXMLParserDocumentException
/*      */     //   448: dup
/*      */     //   449: aload 5
/*      */     //   451: invokespecial 910	org/gudy/azureus2/plugins/utils/xml/simpleparser/SimpleXMLParserDocumentException:<init>	(Ljava/lang/Throwable;)V
/*      */     //   454: athrow
/*      */     // Line number table:
/*      */     //   Java source line #430	-> byte code offset #0
/*      */     //   Java source line #433	-> byte code offset #2
/*      */     //   Java source line #435	-> byte code offset #13
/*      */     //   Java source line #439	-> byte code offset #19
/*      */     //   Java source line #441	-> byte code offset #26
/*      */     //   Java source line #443	-> byte code offset #31
/*      */     //   Java source line #446	-> byte code offset #34
/*      */     //   Java source line #447	-> byte code offset #42
/*      */     //   Java source line #450	-> byte code offset #45
/*      */     //   Java source line #451	-> byte code offset #49
/*      */     //   Java source line #450	-> byte code offset #52
/*      */     //   Java source line #453	-> byte code offset #61
/*      */     //   Java source line #455	-> byte code offset #66
/*      */     //   Java source line #461	-> byte code offset #76
/*      */     //   Java source line #463	-> byte code offset #88
/*      */     //   Java source line #465	-> byte code offset #108
/*      */     //   Java source line #469	-> byte code offset #111
/*      */     //   Java source line #471	-> byte code offset #118
/*      */     //   Java source line #473	-> byte code offset #123
/*      */     //   Java source line #479	-> byte code offset #126
/*      */     //   Java source line #481	-> byte code offset #139
/*      */     //   Java source line #483	-> byte code offset #148
/*      */     //   Java source line #485	-> byte code offset #169
/*      */     //   Java source line #487	-> byte code offset #177
/*      */     //   Java source line #489	-> byte code offset #182
/*      */     //   Java source line #492	-> byte code offset #191
/*      */     //   Java source line #494	-> byte code offset #202
/*      */     //   Java source line #496	-> byte code offset #214
/*      */     //   Java source line #498	-> byte code offset #224
/*      */     //   Java source line #500	-> byte code offset #258
/*      */     //   Java source line #502	-> byte code offset #261
/*      */     //   Java source line #479	-> byte code offset #269
/*      */     //   Java source line #506	-> byte code offset #275
/*      */     //   Java source line #507	-> byte code offset #283
/*      */     //   Java source line #509	-> byte code offset #286
/*      */     //   Java source line #511	-> byte code offset #293
/*      */     //   Java source line #514	-> byte code offset #322
/*      */     //   Java source line #516	-> byte code offset #335
/*      */     //   Java source line #518	-> byte code offset #338
/*      */     //   Java source line #522	-> byte code offset #340
/*      */     //   Java source line #524	-> byte code offset #350
/*      */     //   Java source line #526	-> byte code offset #361
/*      */     //   Java source line #529	-> byte code offset #373
/*      */     //   Java source line #531	-> byte code offset #376
/*      */     //   Java source line #534	-> byte code offset #378
/*      */     //   Java source line #537	-> byte code offset #391
/*      */     //   Java source line #541	-> byte code offset #397
/*      */     //   Java source line #542	-> byte code offset #402
/*      */     //   Java source line #541	-> byte code offset #405
/*      */     //   Java source line #546	-> byte code offset #415
/*      */     //   Java source line #543	-> byte code offset #418
/*      */     //   Java source line #545	-> byte code offset #420
/*      */     //   Java source line #548	-> byte code offset #431
/*      */     //   Java source line #550	-> byte code offset #439
/*      */     //   Java source line #553	-> byte code offset #445
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	455	0	this	UPnPImpl
/*      */     //   0	455	1	_is	InputStream
/*      */     //   1	61	2	baos	java.io.ByteArrayOutputStream
/*      */     //   18	18	3	buffer	byte[]
/*      */     //   65	329	3	bytes_in	byte[]
/*      */     //   24	14	4	len	int
/*      */     //   74	23	4	is	InputStream
/*      */     //   52	7	5	localObject1	Object
/*      */     //   86	201	5	data	StringBuilder
/*      */     //   376	74	5	e	Throwable
/*      */     //   106	6	6	lnr	java.io.LineNumberReader
/*      */     //   389	19	6	trace	java.io.FileOutputStream
/*      */     //   418	7	6	f	Throwable
/*      */     //   109	106	7	ignore_map	Set
/*      */     //   116	24	8	line	String
/*      */     //   291	75	8	data_str	String
/*      */     //   127	143	9	i	int
/*      */     //   338	36	9	e	Throwable
/*      */     //   146	118	10	c	char
/*      */     //   200	17	11	cha	Character
/*      */     //   405	8	12	localObject2	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   2	45	52	finally
/*      */     //   52	54	52	finally
/*      */     //   322	337	338	java/lang/Throwable
/*      */     //   76	337	376	java/lang/Throwable
/*      */     //   338	372	376	java/lang/Throwable
/*      */     //   373	376	376	java/lang/Throwable
/*      */     //   391	397	405	finally
/*      */     //   405	407	405	finally
/*      */     //   378	415	418	java/lang/Throwable
/*      */   }
/*      */   
/*      */   public SimpleXMLParserDocument downloadXML(UPnPRootDeviceImpl root, URL url)
/*      */     throws UPnPException
/*      */   {
/*  564 */     return downloadXMLSupport(null, url);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SimpleXMLParserDocument downloadXML(UPnPDeviceImpl device, URL url)
/*      */     throws UPnPException
/*      */   {
/*      */     try
/*      */     {
/*  577 */       device.restoreRelativeBaseURL();
/*      */       
/*  579 */       return downloadXMLSupport(device.getFriendlyName(), url);
/*      */     }
/*      */     catch (UPnPException e)
/*      */     {
/*  583 */       device.clearRelativeBaseURL();
/*      */     }
/*  585 */     return downloadXMLSupport(device.getFriendlyName(), url);
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected SimpleXMLParserDocument downloadXMLSupport(String friendly_name, URL url)
/*      */     throws UPnPException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_2
/*      */     //   1: invokevirtual 887	java/net/URL:toExternalForm	()Ljava/lang/String;
/*      */     //   4: astore_3
/*      */     //   5: iconst_1
/*      */     //   6: istore 4
/*      */     //   8: new 471	java/lang/StringBuilder
/*      */     //   11: dup
/*      */     //   12: invokespecial 859	java/lang/StringBuilder:<init>	()V
/*      */     //   15: ldc 41
/*      */     //   17: invokevirtual 865	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   20: aload_1
/*      */     //   21: ifnonnull +8 -> 29
/*      */     //   24: ldc 1
/*      */     //   26: goto +22 -> 48
/*      */     //   29: new 471	java/lang/StringBuilder
/*      */     //   32: dup
/*      */     //   33: invokespecial 859	java/lang/StringBuilder:<init>	()V
/*      */     //   36: ldc 17
/*      */     //   38: invokevirtual 865	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   41: aload_1
/*      */     //   42: invokevirtual 865	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   45: invokevirtual 861	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   48: invokevirtual 865	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   51: invokevirtual 861	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   54: invokestatic 908	org/gudy/azureus2/core3/util/TorrentUtils:setTLSDescription	(Ljava/lang/String;)V
/*      */     //   57: aload_0
/*      */     //   58: getfield 784	com/aelitis/net/upnp/impl/UPnPImpl:adapter	Lcom/aelitis/net/upnp/UPnPAdapter;
/*      */     //   61: invokeinterface 917 1 0
/*      */     //   66: astore 5
/*      */     //   68: aload_0
/*      */     //   69: getfield 791	com/aelitis/net/upnp/impl/UPnPImpl:failed_urls	Ljava/util/Map;
/*      */     //   72: dup
/*      */     //   73: astore 7
/*      */     //   75: monitorenter
/*      */     //   76: aload_0
/*      */     //   77: getfield 791	com/aelitis/net/upnp/impl/UPnPImpl:failed_urls	Ljava/util/Map;
/*      */     //   80: aload_3
/*      */     //   81: invokeinterface 943 2 0
/*      */     //   86: checkcast 431	[J
/*      */     //   89: astore 8
/*      */     //   91: aload 8
/*      */     //   93: ifnonnull +9 -> 102
/*      */     //   96: iconst_3
/*      */     //   97: istore 6
/*      */     //   99: goto +92 -> 191
/*      */     //   102: aload 8
/*      */     //   104: iconst_0
/*      */     //   105: laload
/*      */     //   106: lstore 9
/*      */     //   108: aload 8
/*      */     //   110: iconst_1
/*      */     //   111: laload
/*      */     //   112: lstore 11
/*      */     //   114: ldc2_w 427
/*      */     //   117: lstore 13
/*      */     //   119: ldc2_w 425
/*      */     //   122: lstore 15
/*      */     //   124: iconst_0
/*      */     //   125: istore 17
/*      */     //   127: iload 17
/*      */     //   129: i2l
/*      */     //   130: lload 9
/*      */     //   132: lcmp
/*      */     //   133: ifge +30 -> 163
/*      */     //   136: lload 15
/*      */     //   138: iconst_1
/*      */     //   139: lshl
/*      */     //   140: lstore 15
/*      */     //   142: lload 15
/*      */     //   144: lload 13
/*      */     //   146: lcmp
/*      */     //   147: iflt +10 -> 157
/*      */     //   150: lload 13
/*      */     //   152: lstore 15
/*      */     //   154: goto +9 -> 163
/*      */     //   157: iinc 17 1
/*      */     //   160: goto -33 -> 127
/*      */     //   163: invokestatic 905	org/gudy/azureus2/core3/util/SystemTime:getMonotonousTime	()J
/*      */     //   166: lload 11
/*      */     //   168: lsub
/*      */     //   169: lload 15
/*      */     //   171: lcmp
/*      */     //   172: ifge +16 -> 188
/*      */     //   175: iconst_0
/*      */     //   176: istore 4
/*      */     //   178: new 441	com/aelitis/net/upnp/UPnPException
/*      */     //   181: dup
/*      */     //   182: ldc 27
/*      */     //   184: invokespecial 803	com/aelitis/net/upnp/UPnPException:<init>	(Ljava/lang/String;)V
/*      */     //   187: athrow
/*      */     //   188: iconst_1
/*      */     //   189: istore 6
/*      */     //   191: aload 7
/*      */     //   193: monitorexit
/*      */     //   194: goto +11 -> 205
/*      */     //   197: astore 18
/*      */     //   199: aload 7
/*      */     //   201: monitorexit
/*      */     //   202: aload 18
/*      */     //   204: athrow
/*      */     //   205: aload 5
/*      */     //   207: aload 5
/*      */     //   209: aload_2
/*      */     //   210: iconst_1
/*      */     //   211: invokeinterface 951 3 0
/*      */     //   216: iload 6
/*      */     //   218: invokeinterface 952 3 0
/*      */     //   223: astore 7
/*      */     //   225: aload 7
/*      */     //   227: aload_0
/*      */     //   228: invokeinterface 950 2 0
/*      */     //   233: aload 7
/*      */     //   235: invokeinterface 949 1 0
/*      */     //   240: astore 8
/*      */     //   242: aload_0
/*      */     //   243: aload 8
/*      */     //   245: invokevirtual 812	com/aelitis/net/upnp/impl/UPnPImpl:parseXML	(Ljava/io/InputStream;)Lorg/gudy/azureus2/plugins/utils/xml/simpleparser/SimpleXMLParserDocument;
/*      */     //   248: astore 9
/*      */     //   250: aload_0
/*      */     //   251: getfield 791	com/aelitis/net/upnp/impl/UPnPImpl:failed_urls	Ljava/util/Map;
/*      */     //   254: dup
/*      */     //   255: astore 10
/*      */     //   257: monitorenter
/*      */     //   258: aload_0
/*      */     //   259: getfield 791	com/aelitis/net/upnp/impl/UPnPImpl:failed_urls	Ljava/util/Map;
/*      */     //   262: aload_3
/*      */     //   263: invokeinterface 944 2 0
/*      */     //   268: pop
/*      */     //   269: aload 10
/*      */     //   271: monitorexit
/*      */     //   272: goto +11 -> 283
/*      */     //   275: astore 19
/*      */     //   277: aload 10
/*      */     //   279: monitorexit
/*      */     //   280: aload 19
/*      */     //   282: athrow
/*      */     //   283: aload 9
/*      */     //   285: astore 10
/*      */     //   287: aload 8
/*      */     //   289: invokevirtual 834	java/io/InputStream:close	()V
/*      */     //   292: aconst_null
/*      */     //   293: invokestatic 908	org/gudy/azureus2/core3/util/TorrentUtils:setTLSDescription	(Ljava/lang/String;)V
/*      */     //   296: aload 10
/*      */     //   298: areturn
/*      */     //   299: astore 20
/*      */     //   301: aload 8
/*      */     //   303: invokevirtual 834	java/io/InputStream:close	()V
/*      */     //   306: aload 20
/*      */     //   308: athrow
/*      */     //   309: astore 5
/*      */     //   311: iload 4
/*      */     //   313: ifeq +142 -> 455
/*      */     //   316: aload_0
/*      */     //   317: getfield 791	com/aelitis/net/upnp/impl/UPnPImpl:failed_urls	Ljava/util/Map;
/*      */     //   320: dup
/*      */     //   321: astore 6
/*      */     //   323: monitorenter
/*      */     //   324: aload_0
/*      */     //   325: getfield 791	com/aelitis/net/upnp/impl/UPnPImpl:failed_urls	Ljava/util/Map;
/*      */     //   328: invokeinterface 940 1 0
/*      */     //   333: bipush 64
/*      */     //   335: if_icmplt +12 -> 347
/*      */     //   338: aload_0
/*      */     //   339: getfield 791	com/aelitis/net/upnp/impl/UPnPImpl:failed_urls	Ljava/util/Map;
/*      */     //   342: invokeinterface 941 1 0
/*      */     //   347: aload_0
/*      */     //   348: getfield 791	com/aelitis/net/upnp/impl/UPnPImpl:failed_urls	Ljava/util/Map;
/*      */     //   351: aload_3
/*      */     //   352: invokeinterface 943 2 0
/*      */     //   357: checkcast 431	[J
/*      */     //   360: astore 7
/*      */     //   362: aload 7
/*      */     //   364: ifnonnull +21 -> 385
/*      */     //   367: iconst_2
/*      */     //   368: newarray <illegal type>
/*      */     //   370: astore 7
/*      */     //   372: aload_0
/*      */     //   373: getfield 791	com/aelitis/net/upnp/impl/UPnPImpl:failed_urls	Ljava/util/Map;
/*      */     //   376: aload_3
/*      */     //   377: aload 7
/*      */     //   379: invokeinterface 945 3 0
/*      */     //   384: pop
/*      */     //   385: aload 7
/*      */     //   387: iconst_0
/*      */     //   388: dup2
/*      */     //   389: laload
/*      */     //   390: lconst_1
/*      */     //   391: ladd
/*      */     //   392: lastore
/*      */     //   393: aload 7
/*      */     //   395: iconst_1
/*      */     //   396: invokestatic 905	org/gudy/azureus2/core3/util/SystemTime:getMonotonousTime	()J
/*      */     //   399: lastore
/*      */     //   400: aload 6
/*      */     //   402: monitorexit
/*      */     //   403: goto +11 -> 414
/*      */     //   406: astore 21
/*      */     //   408: aload 6
/*      */     //   410: monitorexit
/*      */     //   411: aload 21
/*      */     //   413: athrow
/*      */     //   414: aload_0
/*      */     //   415: getfield 784	com/aelitis/net/upnp/impl/UPnPImpl:adapter	Lcom/aelitis/net/upnp/UPnPAdapter;
/*      */     //   418: new 471	java/lang/StringBuilder
/*      */     //   421: dup
/*      */     //   422: invokespecial 859	java/lang/StringBuilder:<init>	()V
/*      */     //   425: ldc 28
/*      */     //   427: invokevirtual 865	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   430: aload_3
/*      */     //   431: invokevirtual 865	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   434: ldc 17
/*      */     //   436: invokevirtual 865	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   439: aload 5
/*      */     //   441: invokestatic 903	org/gudy/azureus2/core3/util/Debug:getNestedExceptionMessageAndStack	(Ljava/lang/Throwable;)Ljava/lang/String;
/*      */     //   444: invokevirtual 865	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   447: invokevirtual 861	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   450: invokeinterface 914 2 0
/*      */     //   455: aload 5
/*      */     //   457: instanceof 441
/*      */     //   460: ifeq +9 -> 469
/*      */     //   463: aload 5
/*      */     //   465: checkcast 441	com/aelitis/net/upnp/UPnPException
/*      */     //   468: athrow
/*      */     //   469: new 441	com/aelitis/net/upnp/UPnPException
/*      */     //   472: dup
/*      */     //   473: new 471	java/lang/StringBuilder
/*      */     //   476: dup
/*      */     //   477: invokespecial 859	java/lang/StringBuilder:<init>	()V
/*      */     //   480: ldc 36
/*      */     //   482: invokevirtual 865	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   485: aload_2
/*      */     //   486: invokevirtual 864	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*      */     //   489: ldc 11
/*      */     //   491: invokevirtual 865	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   494: invokevirtual 861	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   497: aload 5
/*      */     //   499: invokespecial 804	com/aelitis/net/upnp/UPnPException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   502: athrow
/*      */     //   503: astore 22
/*      */     //   505: aconst_null
/*      */     //   506: invokestatic 908	org/gudy/azureus2/core3/util/TorrentUtils:setTLSDescription	(Ljava/lang/String;)V
/*      */     //   509: aload 22
/*      */     //   511: athrow
/*      */     // Line number table:
/*      */     //   Java source line #596	-> byte code offset #0
/*      */     //   Java source line #598	-> byte code offset #5
/*      */     //   Java source line #601	-> byte code offset #8
/*      */     //   Java source line #603	-> byte code offset #57
/*      */     //   Java source line #607	-> byte code offset #68
/*      */     //   Java source line #609	-> byte code offset #76
/*      */     //   Java source line #611	-> byte code offset #91
/*      */     //   Java source line #613	-> byte code offset #96
/*      */     //   Java source line #617	-> byte code offset #102
/*      */     //   Java source line #618	-> byte code offset #108
/*      */     //   Java source line #620	-> byte code offset #114
/*      */     //   Java source line #621	-> byte code offset #119
/*      */     //   Java source line #623	-> byte code offset #124
/*      */     //   Java source line #625	-> byte code offset #136
/*      */     //   Java source line #627	-> byte code offset #142
/*      */     //   Java source line #629	-> byte code offset #150
/*      */     //   Java source line #631	-> byte code offset #154
/*      */     //   Java source line #623	-> byte code offset #157
/*      */     //   Java source line #635	-> byte code offset #163
/*      */     //   Java source line #637	-> byte code offset #175
/*      */     //   Java source line #639	-> byte code offset #178
/*      */     //   Java source line #642	-> byte code offset #188
/*      */     //   Java source line #644	-> byte code offset #191
/*      */     //   Java source line #646	-> byte code offset #205
/*      */     //   Java source line #648	-> byte code offset #225
/*      */     //   Java source line #650	-> byte code offset #233
/*      */     //   Java source line #654	-> byte code offset #242
/*      */     //   Java source line #656	-> byte code offset #250
/*      */     //   Java source line #658	-> byte code offset #258
/*      */     //   Java source line #659	-> byte code offset #269
/*      */     //   Java source line #661	-> byte code offset #283
/*      */     //   Java source line #665	-> byte code offset #287
/*      */     //   Java source line #704	-> byte code offset #292
/*      */     //   Java source line #665	-> byte code offset #299
/*      */     //   Java source line #667	-> byte code offset #309
/*      */     //   Java source line #669	-> byte code offset #311
/*      */     //   Java source line #671	-> byte code offset #316
/*      */     //   Java source line #673	-> byte code offset #324
/*      */     //   Java source line #675	-> byte code offset #338
/*      */     //   Java source line #678	-> byte code offset #347
/*      */     //   Java source line #680	-> byte code offset #362
/*      */     //   Java source line #682	-> byte code offset #367
/*      */     //   Java source line #684	-> byte code offset #372
/*      */     //   Java source line #687	-> byte code offset #385
/*      */     //   Java source line #689	-> byte code offset #393
/*      */     //   Java source line #690	-> byte code offset #400
/*      */     //   Java source line #692	-> byte code offset #414
/*      */     //   Java source line #695	-> byte code offset #455
/*      */     //   Java source line #697	-> byte code offset #463
/*      */     //   Java source line #700	-> byte code offset #469
/*      */     //   Java source line #704	-> byte code offset #503
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	512	0	this	UPnPImpl
/*      */     //   0	512	1	friendly_name	String
/*      */     //   0	512	2	url	URL
/*      */     //   4	427	3	url_str	String
/*      */     //   6	306	4	record_failure	boolean
/*      */     //   66	142	5	rdf	org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory
/*      */     //   309	189	5	e	Throwable
/*      */     //   97	3	6	retries	int
/*      */     //   189	28	6	retries	int
/*      */     //   223	11	7	rd	ResourceDownloader
/*      */     //   360	34	7	fails	long[]
/*      */     //   89	20	8	fails	long[]
/*      */     //   240	62	8	data	InputStream
/*      */     //   106	25	9	consec_fails	long
/*      */     //   248	36	9	res	SimpleXMLParserDocument
/*      */     //   112	55	11	last_fail	long
/*      */     //   117	34	13	max_period	long
/*      */     //   122	48	15	period	long
/*      */     //   125	33	17	i	int
/*      */     //   197	6	18	localObject1	Object
/*      */     //   275	6	19	localObject2	Object
/*      */     //   299	8	20	localObject3	Object
/*      */     //   406	6	21	localObject4	Object
/*      */     //   503	7	22	localObject5	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   76	194	197	finally
/*      */     //   197	202	197	finally
/*      */     //   258	272	275	finally
/*      */     //   275	280	275	finally
/*      */     //   242	287	299	finally
/*      */     //   299	301	299	finally
/*      */     //   8	292	309	java/lang/Throwable
/*      */     //   299	309	309	java/lang/Throwable
/*      */     //   324	403	406	finally
/*      */     //   406	411	406	finally
/*      */     //   8	292	503	finally
/*      */     //   299	505	503	finally
/*      */   }
/*      */   
/*      */   protected boolean forceDirect()
/*      */   {
/*  711 */     String http_proxy = System.getProperty("http.proxyHost");
/*  712 */     String socks_proxy = System.getProperty("socksProxyHost");
/*      */     
/*      */ 
/*      */ 
/*  716 */     boolean force_direct = ((http_proxy != null) && (http_proxy.trim().length() > 0)) || ((socks_proxy != null) && (socks_proxy.trim().length() > 0));
/*      */     
/*      */ 
/*  719 */     return force_direct;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public SimpleXMLParserDocument performSOAPRequest(UPnPService service, String soap_action, String request)
/*      */     throws SimpleXMLParserDocumentException, UPnPException, IOException
/*      */   {
/*      */     SimpleXMLParserDocument res;
/*      */     
/*      */ 
/*      */     SimpleXMLParserDocument res;
/*      */     
/*      */ 
/*  734 */     if ((service.getDirectInvocations()) || (forceDirect()))
/*      */     {
/*  736 */       res = performSOAPRequest(service, soap_action, request, false);
/*      */     }
/*      */     else {
/*      */       try
/*      */       {
/*  741 */         res = performSOAPRequest(service, soap_action, request, true);
/*      */         
/*  743 */         this.http_calls_ok += 1;
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/*  747 */         res = performSOAPRequest(service, soap_action, request, false);
/*      */         
/*  749 */         this.direct_calls_ok += 1;
/*      */         
/*  751 */         if (this.direct_calls_ok == 1)
/*      */         {
/*  753 */           log("Invocation via http connection failed (" + e.getMessage() + ") but socket connection succeeded");
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  758 */     return res;
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public SimpleXMLParserDocument performSOAPRequest(UPnPService service, String soap_action, String request, boolean use_http_connection)
/*      */     throws SimpleXMLParserDocumentException, UPnPException, IOException
/*      */   {
/*  776 */     List<URL> controls = service.getControlURLs();
/*      */     
/*  778 */     Throwable last_error = null;
/*      */     
/*  780 */     Iterator i$ = controls.iterator(); for (;;) { if (i$.hasNext()) { URL control = (URL)i$.next();
/*      */         
/*  782 */         boolean good_url = true;
/*      */         try
/*      */         {
/*  785 */           this.adapter.trace("UPnP:Request: -> " + control + "," + request);
/*      */           
/*  787 */           if (use_http_connection) {
/*      */             try
/*      */             {
/*  790 */               AEProxySelectorFactory.getSelector().startNoProxy();
/*      */               
/*  792 */               TorrentUtils.setTLSDescription("UPnP Device: " + service.getDevice().getFriendlyName());
/*      */               
/*  794 */               HttpURLConnection con1 = (HttpURLConnection)control.openConnection();
/*      */               
/*  796 */               con1.setRequestProperty("SOAPAction", "\"" + soap_action + "\"");
/*      */               
/*  798 */               con1.setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"");
/*      */               
/*  800 */               con1.setRequestProperty("User-Agent", "Azureus (UPnP/1.0)");
/*      */               
/*  802 */               con1.setRequestMethod("POST");
/*      */               
/*  804 */               con1.setDoInput(true);
/*  805 */               con1.setDoOutput(true);
/*      */               
/*  807 */               OutputStream os = con1.getOutputStream();
/*      */               
/*  809 */               PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
/*      */               
/*  811 */               pw.println(request);
/*      */               
/*  813 */               pw.flush();
/*      */               
/*  815 */               con1.connect();
/*      */               
/*  817 */               if ((con1.getResponseCode() == 405) || (con1.getResponseCode() == 500))
/*      */               {
/*      */                 try
/*      */                 {
/*      */ 
/*  822 */                   HttpURLConnection con2 = (HttpURLConnection)control.openConnection();
/*      */                   
/*  824 */                   con2.setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"");
/*      */                   
/*  826 */                   con2.setRequestMethod("M-POST");
/*      */                   
/*  828 */                   con2.setRequestProperty("MAN", "\"http://schemas.xmlsoap.org/soap/envelope/\"; ns=01");
/*      */                   
/*  830 */                   con2.setRequestProperty("01-SOAPACTION", "\"" + soap_action + "\"");
/*      */                   
/*  832 */                   con2.setDoInput(true);
/*  833 */                   con2.setDoOutput(true);
/*      */                   
/*  835 */                   os = con2.getOutputStream();
/*      */                   
/*  837 */                   pw = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
/*      */                   
/*  839 */                   pw.println(request);
/*      */                   
/*  841 */                   pw.flush();
/*      */                   
/*  843 */                   con2.connect();
/*      */                   
/*  845 */                   SimpleXMLParserDocument localSimpleXMLParserDocument1 = parseXML(con2.getInputStream());
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
/*  876 */                   TorrentUtils.setTLSDescription(null);
/*      */                   
/*  878 */                   AEProxySelectorFactory.getSelector().endNoProxy();
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
/*  938 */                   return localSimpleXMLParserDocument1;
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/*  851 */                   es = con1.getErrorStream();
/*      */                   
/*  853 */                   String info = null;
/*      */                   try
/*      */                   {
/*  856 */                     info = FileUtil.readInputStreamAsString(es, 512);
/*      */                   }
/*      */                   catch (Throwable e) {}
/*      */                   
/*      */ 
/*  861 */                   String error = "SOAP RPC failed: " + con1.getResponseCode() + " " + con1.getResponseMessage();
/*      */                   
/*  863 */                   if (info != null)
/*      */                   {
/*  865 */                     error = error + " - " + info;
/*      */                   }
/*      */                   
/*  868 */                   throw new IOException(error);
/*      */                 }
/*      */               }
/*      */               
/*  872 */               InputStream es = parseXML(con1.getInputStream());
/*      */               
/*      */ 
/*      */ 
/*  876 */               TorrentUtils.setTLSDescription(null);
/*      */               
/*  878 */               AEProxySelectorFactory.getSelector().endNoProxy();
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
/*  938 */               return es;
/*      */             }
/*      */             finally
/*      */             {
/*  876 */               TorrentUtils.setTLSDescription(null);
/*      */               
/*  878 */               AEProxySelectorFactory.getSelector().endNoProxy();
/*      */             }
/*      */           }
/*  881 */           int CONNECT_TIMEOUT = 15000;
/*  882 */           int READ_TIMEOUT = 30000;
/*      */           
/*  884 */           Socket socket = new Socket(Proxy.NO_PROXY);
/*      */           
/*  886 */           socket.connect(new InetSocketAddress(control.getHost(), control.getPort()), 15000);
/*      */           
/*  888 */           socket.setSoTimeout(30000);
/*      */           try
/*      */           {
/*  891 */             PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
/*      */             
/*  893 */             String url_target = control.toString();
/*      */             
/*  895 */             int p1 = url_target.indexOf("://") + 3;
/*  896 */             p1 = url_target.indexOf("/", p1);
/*      */             
/*  898 */             url_target = url_target.substring(p1);
/*      */             
/*  900 */             pw.print("POST " + url_target + " HTTP/1.1" + "\r\n");
/*  901 */             pw.print("Content-Type: text/xml; charset=\"utf-8\"\r\n");
/*  902 */             pw.print("SOAPAction: \"" + soap_action + "\"" + "\r\n");
/*  903 */             pw.print("User-Agent: Azureus (UPnP/1.0)\r\n");
/*  904 */             pw.print("Host: " + control.getHost() + "\r\n");
/*  905 */             pw.print("Content-Length: " + request.getBytes("UTF8").length + "\r\n");
/*  906 */             pw.print("Connection: Keep-Alive\r\n");
/*  907 */             pw.print("Pragma: no-cache\r\n\r\n");
/*      */             
/*  909 */             pw.print(request);
/*      */             
/*  911 */             pw.flush();
/*      */             
/*  913 */             InputStream is = HTTPUtils.decodeChunkedEncoding(socket, true);
/*      */             
/*  915 */             SimpleXMLParserDocument localSimpleXMLParserDocument2 = parseXML(is);
/*      */             
/*      */ 
/*      */             try
/*      */             {
/*  920 */               socket.close();
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  924 */               Debug.printStackTrace(e);
/*      */             }
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
/*  938 */             return localSimpleXMLParserDocument2;
/*      */           }
/*      */           finally
/*      */           {
/*      */             try
/*      */             {
/*  920 */               socket.close();
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  924 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  930 */           last_error = e;
/*      */           
/*  932 */           good_url = false;
/*      */         }
/*      */         finally
/*      */         {
/*  936 */           if (good_url)
/*      */           {
/*  938 */             service.setPreferredControlURL(control);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  944 */     if (last_error == null)
/*      */     {
/*  946 */       throw new UPnPException("inconsistent!");
/*      */     }
/*      */     
/*  949 */     if ((last_error instanceof SimpleXMLParserDocumentException))
/*      */     {
/*  951 */       throw ((SimpleXMLParserDocumentException)last_error);
/*      */     }
/*  953 */     if ((last_error instanceof UPnPException))
/*      */     {
/*  955 */       throw ((UPnPException)last_error);
/*      */     }
/*  957 */     if ((last_error instanceof IOException))
/*      */     {
/*  959 */       throw ((IOException)last_error);
/*      */     }
/*      */     
/*      */ 
/*  963 */     throw ((RuntimeException)last_error);
/*      */   }
/*      */   
/*      */ 
/*      */   protected File getTraceFile()
/*      */   {
/*      */     try
/*      */     {
/*  971 */       this.this_mon.enter();
/*      */       
/*  973 */       this.trace_index += 1;
/*      */       
/*  975 */       if (this.trace_index == 6)
/*      */       {
/*  977 */         this.trace_index = 1;
/*      */       }
/*      */       
/*  980 */       return new File(this.adapter.getTraceDir(), "upnp_trace" + this.trace_index + ".log");
/*      */     }
/*      */     finally {
/*  983 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public UPnPAdapter getAdapter()
/*      */   {
/*  990 */     return this.adapter;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void reportActivity(ResourceDownloader downloader, String activity)
/*      */   {
/*  998 */     log(activity);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void failed(ResourceDownloader downloader, ResourceDownloaderException e)
/*      */   {
/* 1006 */     log(e);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void log(Throwable e)
/*      */   {
/* 1013 */     log(e.toString());
/*      */   }
/*      */   
/*      */ 
/*      */   public void log(String str)
/*      */   {
/*      */     List old_listeners;
/*      */     
/*      */     try
/*      */     {
/* 1023 */       this.this_mon.enter();
/*      */       
/* 1025 */       old_listeners = new ArrayList(this.log_listeners);
/*      */       
/* 1027 */       this.log_history.add(str);
/*      */       
/* 1029 */       if (this.log_history.size() > 32)
/*      */       {
/* 1031 */         this.log_history.remove(0);
/*      */       }
/*      */     }
/*      */     finally {
/* 1035 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1038 */     for (int i = 0; i < old_listeners.size(); i++)
/*      */     {
/* 1040 */       ((UPnPLogListener)old_listeners.get(i)).log(str);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void logAlert(String str, boolean error, int type)
/*      */   {
/*      */     List old_listeners;
/*      */     
/*      */ 
/*      */     try
/*      */     {
/* 1053 */       this.this_mon.enter();
/*      */       
/* 1055 */       old_listeners = new ArrayList(this.log_listeners);
/*      */       
/* 1057 */       this.log_alert_history.add(new Object[] { str, Boolean.valueOf(error), new Integer(type) });
/*      */       
/* 1059 */       if (this.log_alert_history.size() > 32)
/*      */       {
/* 1061 */         this.log_alert_history.remove(0);
/*      */       }
/*      */     }
/*      */     finally {
/* 1065 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1068 */     for (int i = 0; i < old_listeners.size(); i++)
/*      */     {
/* 1070 */       ((UPnPLogListener)old_listeners.get(i)).logAlert(str, error, type);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void addLogListener(UPnPLogListener l)
/*      */   {
/*      */     List old_logs;
/*      */     
/*      */     List old_alerts;
/*      */     try
/*      */     {
/* 1082 */       this.this_mon.enter();
/*      */       
/* 1084 */       old_logs = new ArrayList(this.log_history);
/* 1085 */       old_alerts = new ArrayList(this.log_alert_history);
/*      */       
/* 1087 */       this.log_listeners.add(l);
/*      */     }
/*      */     finally {
/* 1090 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1093 */     for (int i = 0; i < old_logs.size(); i++)
/*      */     {
/* 1095 */       l.log((String)old_logs.get(i));
/*      */     }
/*      */     
/* 1098 */     for (int i = 0; i < old_alerts.size(); i++)
/*      */     {
/* 1100 */       Object[] entry = (Object[])old_alerts.get(i);
/*      */       
/* 1102 */       l.logAlert((String)entry[0], ((Boolean)entry[1]).booleanValue(), ((Integer)entry[2]).intValue());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeLogListener(UPnPLogListener l)
/*      */   {
/* 1110 */     this.log_listeners.remove(l);
/*      */   }
/*      */   
/*      */   public UPnPRootDevice[] getRootDevices()
/*      */   {
/*      */     try
/*      */     {
/* 1117 */       this.this_mon.enter();
/*      */       
/* 1119 */       return (UPnPRootDevice[])this.root_locations.values().toArray(new UPnPRootDevice[this.root_locations.size()]);
/*      */     }
/*      */     finally
/*      */     {
/* 1123 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void addRootDeviceListener(final UPnPListener l)
/*      */   {
/*      */     final List<UPnPRootDeviceImpl> old_locations;
/*      */     
/*      */     try
/*      */     {
/* 1134 */       this.this_mon.enter();
/*      */       
/* 1136 */       old_locations = new ArrayList(this.root_locations.values());
/*      */       
/* 1138 */       this.rd_listeners.add(l);
/*      */     }
/*      */     finally
/*      */     {
/* 1142 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1145 */     if (old_locations.size() > 0)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1152 */       this.async_dispatcher.dispatch(new AERunnable()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/* 1159 */           for (int i = 0; i < old_locations.size(); i++)
/*      */           {
/* 1161 */             UPnPRootDevice device = (UPnPRootDevice)old_locations.get(i);
/*      */             
/*      */             try
/*      */             {
/* 1165 */               if (l.deviceDiscovered(device.getUSN(), device.getLocation()))
/*      */               {
/* 1167 */                 l.rootDeviceFound(device);
/*      */               }
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 1172 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeRootDeviceListener(UPnPListener l)
/*      */   {
/*      */     try
/*      */     {
/* 1185 */       this.this_mon.enter();
/*      */       
/* 1187 */       this.rd_listeners.remove(l);
/*      */     }
/*      */     finally
/*      */     {
/* 1191 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/UPnPImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */