/*     */ package com.aelitis.azureus.core.devices.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.Device;
/*     */ import com.aelitis.azureus.core.devices.DeviceManagerException;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.tracker.Tracker;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebContext;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageGenerator;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageRequest;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageResponse;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DeviceTivoManager
/*     */ {
/*     */   private static final String LF = "\n";
/*     */   private static final int CONTROL_PORT = 2190;
/*     */   private DeviceManagerImpl device_manager;
/*     */   private PluginInterface plugin_interface;
/*     */   private boolean is_enabled;
/*     */   private String uid;
/*     */   private Searcher current_search;
/*     */   private volatile boolean manager_destroyed;
/*     */   
/*     */   protected DeviceTivoManager(DeviceManagerImpl _dm)
/*     */   {
/*  73 */     this.device_manager = _dm;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void startUp()
/*     */   {
/*  79 */     this.plugin_interface = PluginInitializer.getDefaultInterface();
/*     */     
/*  81 */     if (COConfigurationManager.getStringParameter("ui").equals("az2"))
/*     */     {
/*  83 */       this.is_enabled = false;
/*     */     }
/*     */     else
/*     */     {
/*  87 */       this.is_enabled = COConfigurationManager.getBooleanParameter("devices.tivo.enabled", true);
/*     */     }
/*     */     
/*  90 */     this.uid = COConfigurationManager.getStringParameter("devices.tivo.uid", null);
/*     */     
/*  92 */     if (this.uid == null)
/*     */     {
/*  94 */       byte[] bytes = new byte[8];
/*     */       
/*  96 */       RandomUtils.nextBytes(bytes);
/*     */       
/*  98 */       this.uid = Base32.encode(bytes);
/*     */       
/* 100 */       COConfigurationManager.setParameter("devices.tivo.uid", this.uid);
/*     */     }
/*     */     
/* 103 */     boolean found_tivo = false;
/*     */     
/* 105 */     for (Device device : this.device_manager.getDevices())
/*     */     {
/* 107 */       if ((device instanceof DeviceTivo))
/*     */       {
/* 109 */         found_tivo = true;
/*     */         
/* 111 */         break;
/*     */       }
/*     */     }
/*     */     
/* 115 */     if ((found_tivo) || (this.device_manager.getAutoSearch()))
/*     */     {
/* 117 */       search(found_tivo, false);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isEnabled()
/*     */   {
/* 124 */     return this.is_enabled;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setEnabled(boolean enabled)
/*     */   {
/* 131 */     COConfigurationManager.setParameter("devices.tivo.enabled", enabled);
/*     */     
/* 133 */     if (enabled)
/*     */     {
/* 135 */       search(false, true);
/*     */     }
/*     */     else
/*     */     {
/* 139 */       for (Device device : this.device_manager.getDevices())
/*     */       {
/* 141 */         if ((device instanceof DeviceTivo))
/*     */         {
/* 143 */           device.remove();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void search()
/*     */   {
/* 152 */     search(false, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void search(boolean persistent, boolean async)
/*     */   {
/*     */     try
/*     */     {
/* 161 */       synchronized (this)
/*     */       {
/* 163 */         if (this.current_search == null)
/*     */         {
/* 165 */           this.current_search = new Searcher(persistent, async);
/*     */ 
/*     */ 
/*     */         }
/* 169 */         else if (!this.current_search.wakeup())
/*     */         {
/* 171 */           this.current_search = new Searcher(persistent, async);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 177 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected byte[] encodeBeacon(boolean is_broadcast, int my_port)
/*     */     throws IOException
/*     */   {
/* 188 */     String beacon = "tivoconnect=1\nswversion=1\nmethod=" + (is_broadcast ? "broadcast" : "connected") + "\n" + "identity=" + this.uid + "\n" + "machine=" + this.device_manager.getLocalServiceName() + "\n" + "platform=pc" + "\n" + "services=TiVoMediaServer:" + my_port + "/http";
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 197 */     return beacon.getBytes("ISO-8859-1");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Map<String, String> decodeBeacon(byte[] buffer, int length)
/*     */     throws IOException
/*     */   {
/* 207 */     String str = new String(buffer, 0, length, "ISO-8859-1");
/*     */     
/* 209 */     String[] lines = str.split("\n");
/*     */     
/* 211 */     Map<String, String> map = new HashMap();
/*     */     
/* 213 */     for (String line : lines)
/*     */     {
/* 215 */       int pos = line.indexOf('=');
/*     */       
/* 217 */       if (pos > 0)
/*     */       {
/* 219 */         map.put(line.substring(0, pos).trim().toLowerCase(), line.substring(pos + 1).trim());
/*     */       }
/*     */     }
/*     */     
/* 223 */     return map;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean receiveBeacon(InetAddress sender, byte[] buffer, int length)
/*     */   {
/* 232 */     if (this.is_enabled) {
/*     */       try
/*     */       {
/* 235 */         Map<String, String> map = decodeBeacon(buffer, length);
/*     */         
/* 237 */         String id = (String)map.get("identity");
/*     */         
/* 239 */         if ((id == null) || (id.equals(this.uid)))
/*     */         {
/* 241 */           return false;
/*     */         }
/*     */         
/* 244 */         String platform = (String)map.get("platform");
/*     */         
/* 246 */         if ((platform != null) && (platform.toLowerCase().startsWith("tcd/")))
/*     */         {
/* 248 */           String classification = "tivo." + platform.substring(4).toLowerCase();
/*     */           
/* 250 */           foundTiVo(sender, id, classification, (String)map.get("machine"));
/*     */           
/* 252 */           return true;
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 256 */         log("Failed to decode beacon", e);
/*     */       }
/*     */     }
/*     */     
/* 260 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DeviceTivo foundTiVo(InetAddress address, String uid, String classification, String machine)
/*     */   {
/* 270 */     uid = "tivo:" + uid;
/*     */     
/* 272 */     DeviceImpl[] devices = this.device_manager.getDevices();
/*     */     
/* 274 */     String server_name = this.device_manager.getLocalServiceName();
/*     */     
/* 276 */     for (DeviceImpl device : devices)
/*     */     {
/* 278 */       if ((device instanceof DeviceTivo))
/*     */       {
/* 280 */         DeviceTivo tivo = (DeviceTivo)device;
/*     */         
/* 282 */         if (device.getID().equals(uid))
/*     */         {
/* 284 */           if (classification != null)
/*     */           {
/* 286 */             String existing_classification = device.getClassification();
/*     */             
/* 288 */             if (!classification.equals(existing_classification))
/*     */             {
/* 290 */               device.setPersistentStringProperty("tt_rend_class", classification);
/*     */             }
/*     */           }
/*     */           
/* 294 */           tivo.found(this, address, server_name, machine);
/*     */           
/* 296 */           return tivo;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 304 */     if (classification == null)
/*     */     {
/* 306 */       classification = "tivo.series3";
/*     */     }
/*     */     
/* 309 */     DeviceTivo new_device = new DeviceTivo(this.device_manager, uid, classification);
/*     */     
/* 311 */     DeviceTivo result = (DeviceTivo)this.device_manager.addDevice(new_device);
/*     */     
/*     */ 
/*     */ 
/* 315 */     if (result == new_device)
/*     */     {
/* 317 */       new_device.found(this, address, server_name, machine);
/*     */     }
/*     */     
/* 320 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void log(String str)
/*     */   {
/* 327 */     if (this.device_manager == null)
/*     */     {
/* 329 */       System.out.println(str);
/*     */     }
/*     */     else
/*     */     {
/* 333 */       this.device_manager.log("TiVo: " + str);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void log(String str, Throwable e)
/*     */   {
/* 342 */     if (this.device_manager == null)
/*     */     {
/* 344 */       System.out.println(str);
/*     */       
/* 346 */       e.printStackTrace();
/*     */     }
/*     */     else
/*     */     {
/* 350 */       this.device_manager.log("TiVo: " + str, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected class Searcher
/*     */   {
/*     */     private static final int LIFE_MILLIS = 10000;
/*     */     
/* 359 */     private long start = SystemTime.getMonotonousTime();
/*     */     
/*     */     private int tcp_port;
/*     */     
/*     */     private DatagramSocket control_socket;
/*     */     
/*     */     private TrackerWebContext twc;
/*     */     
/*     */     private TimerEventPeriodic timer_event;
/*     */     
/*     */     private volatile boolean persistent;
/*     */     
/*     */     private volatile boolean search_destroyed;
/*     */     
/*     */ 
/*     */     protected Searcher(boolean _persistent, boolean _async)
/*     */       throws DeviceManagerException
/*     */     {
/*     */       try
/*     */       {
/* 379 */         last_port = COConfigurationManager.getIntParameter("devices.tivo.net.tcp.port", 0);
/*     */         
/* 381 */         if (last_port > 0) {
/*     */           try
/*     */           {
/* 384 */             ServerSocket ss = new ServerSocket(last_port);
/*     */             
/* 386 */             ss.setReuseAddress(true);
/*     */             
/* 388 */             ss.close();
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 392 */             last_port = 0;
/*     */           }
/*     */         }
/*     */         
/* 396 */         this.twc = DeviceTivoManager.this.plugin_interface.getTracker().createWebContext(last_port, 1);
/*     */         
/* 398 */         this.tcp_port = this.twc.getURLs()[0].getPort();
/*     */         
/* 400 */         COConfigurationManager.setParameter("devices.tivo.net.tcp.port", this.tcp_port);
/*     */         
/* 402 */         this.twc.addPageGenerator(new TrackerWebPageGenerator()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public boolean generate(TrackerWebPageRequest request, TrackerWebPageResponse response)
/*     */             throws IOException
/*     */           {
/*     */ 
/*     */ 
/* 412 */             String id = (String)request.getHeaders().get("tsn");
/*     */             
/* 414 */             if (id == null)
/*     */             {
/* 416 */               id = (String)request.getHeaders().get("tivo_tcd_id");
/*     */             }
/*     */             
/* 419 */             if ((id != null) && (DeviceTivoManager.this.is_enabled))
/*     */             {
/* 421 */               DeviceTivoManager.Searcher.this.persistent = true;
/*     */               
/* 423 */               DeviceTivo tivo = DeviceTivoManager.this.foundTiVo(request.getClientAddress2().getAddress(), id, null, null);
/*     */               
/* 425 */               return tivo.generate(request, response);
/*     */             }
/*     */             
/* 428 */             return false;
/*     */           }
/*     */           
/* 431 */         });
/* 432 */         this.control_socket = new DatagramSocket(null);
/*     */         
/* 434 */         this.control_socket.setReuseAddress(true);
/*     */         try
/*     */         {
/* 437 */           this.control_socket.setSoTimeout(60000);
/*     */         }
/*     */         catch (Throwable e) {}
/*     */         
/*     */ 
/* 442 */         InetAddress bind = NetworkAdmin.getSingleton().getSingleHomedServiceBindAddress();
/*     */         
/* 444 */         this.control_socket.bind(new InetSocketAddress(bind, 2190));
/*     */         
/* 446 */         this.timer_event = SimpleTimer.addPeriodicEvent("Tivo:Beacon", 60000L, new TimerEventPerformer()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public void perform(TimerEvent event)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 456 */             if ((!DeviceTivoManager.this.manager_destroyed) && (!DeviceTivoManager.Searcher.this.search_destroyed))
/*     */             {
/* 458 */               DeviceTivoManager.Searcher.this.sendBeacon();
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 463 */             if (!DeviceTivoManager.Searcher.this.persistent)
/*     */             {
/* 465 */               synchronized (DeviceTivoManager.this)
/*     */               {
/* 467 */                 if (SystemTime.getMonotonousTime() - DeviceTivoManager.Searcher.this.start >= 10000L)
/*     */                 {
/* 469 */                   DeviceTivoManager.this.log("Terminating search, no devices found");
/*     */                   
/* 471 */                   DeviceTivoManager.this.current_search = null;
/*     */                   
/* 473 */                   DeviceTivoManager.Searcher.this.destroy();
/*     */                 }
/*     */                 
/*     */               }
/*     */             }
/*     */           }
/* 479 */         });
/* 480 */         final AESemaphore start_sem = new AESemaphore("TiVo:CtrlListener");
/*     */         
/* 482 */         new AEThread2("TiVo:CtrlListener", true)
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/* 487 */             start_sem.release();
/*     */             
/* 489 */             long successful_accepts = 0L;
/* 490 */             long failed_accepts = 0L;
/*     */             
/* 492 */             while ((!DeviceTivoManager.this.manager_destroyed) && (!DeviceTivoManager.Searcher.this.search_destroyed)) {
/*     */               try
/*     */               {
/* 495 */                 byte[] buf = new byte[' '];
/*     */                 
/* 497 */                 DatagramPacket packet = new DatagramPacket(buf, buf.length);
/*     */                 
/* 499 */                 DeviceTivoManager.Searcher.this.control_socket.receive(packet);
/*     */                 
/* 501 */                 successful_accepts += 1L;
/*     */                 
/* 503 */                 failed_accepts = 0L;
/*     */                 
/* 505 */                 if (DeviceTivoManager.this.receiveBeacon(packet.getAddress(), packet.getData(), packet.getLength()))
/*     */                 {
/* 507 */                   DeviceTivoManager.Searcher.this.persistent = true;
/*     */                 }
/*     */                 
/*     */ 
/*     */               }
/*     */               catch (SocketTimeoutException e) {}catch (Throwable e)
/*     */               {
/* 514 */                 if ((DeviceTivoManager.Searcher.this.control_socket != null) && (!DeviceTivoManager.Searcher.this.search_destroyed) && (!DeviceTivoManager.this.manager_destroyed))
/*     */                 {
/* 516 */                   failed_accepts += 1L;
/*     */                   
/* 518 */                   DeviceTivoManager.this.log("UDP receive on port 2190 failed", e);
/*     */                 }
/*     */                 
/* 521 */                 if (((failed_accepts > 100L) && (successful_accepts == 0L)) || (failed_accepts > 1000L))
/*     */                 {
/* 523 */                   DeviceTivoManager.this.log("    too many failures, abandoning");
/*     */                   
/* 525 */                   break;
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }.start();
/*     */         
/* 532 */         if (_async)
/*     */         {
/* 534 */           new DelayedEvent("search:delay", 5000L, new AERunnable()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void runSupport()
/*     */             {
/*     */ 
/*     */ 
/* 542 */               DeviceTivoManager.Searcher.this.sendBeacon();
/*     */             }
/*     */           });
/*     */         }
/*     */         else {
/* 547 */           start_sem.reserve(5000L);
/*     */           
/* 549 */           sendBeacon();
/*     */         }
/*     */         
/* 552 */         DeviceTivoManager.this.log("Initiated device search");
/*     */       }
/*     */       catch (Throwable e) {
/*     */         int last_port;
/* 556 */         DeviceTivoManager.this.log("Failed to initialise search", e);
/*     */         
/* 558 */         destroy();
/*     */         
/* 560 */         throw new DeviceManagerException("Creation failed", e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     protected void sendBeacon()
/*     */     {
/* 567 */       if (DeviceTivoManager.this.is_enabled) {
/*     */         try
/*     */         {
/* 570 */           byte[] bytes = DeviceTivoManager.this.encodeBeacon(true, this.tcp_port);
/*     */           
/* 572 */           this.control_socket.send(new DatagramPacket(bytes, bytes.length, InetAddress.getByName("255.255.255.255"), 2190));
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 576 */           DeviceTivoManager.this.log("Failed to send beacon", e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     protected boolean wakeup()
/*     */     {
/* 584 */       synchronized (DeviceTivoManager.this)
/*     */       {
/* 586 */         if (this.search_destroyed)
/*     */         {
/* 588 */           return false;
/*     */         }
/*     */         
/* 591 */         this.start = SystemTime.getMonotonousTime();
/*     */       }
/*     */       
/* 594 */       sendBeacon();
/*     */       
/* 596 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */     protected void destroy()
/*     */     {
/* 602 */       this.search_destroyed = true;
/*     */       
/* 604 */       if (this.twc != null)
/*     */       {
/* 606 */         this.twc.destroy();
/*     */         
/* 608 */         this.twc = null;
/*     */       }
/*     */       
/* 611 */       if (this.timer_event != null)
/*     */       {
/* 613 */         this.timer_event.cancel();
/*     */         
/* 615 */         this.timer_event = null;
/*     */       }
/*     */       
/* 618 */       if (this.control_socket != null)
/*     */       {
/*     */         try {
/* 621 */           this.control_socket.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */         
/*     */ 
/* 626 */         this.control_socket = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/DeviceTivoManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */