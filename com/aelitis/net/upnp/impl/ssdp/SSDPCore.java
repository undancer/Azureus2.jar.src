/*     */ package com.aelitis.net.upnp.impl.ssdp;
/*     */ 
/*     */ import com.aelitis.net.udp.mc.MCGroup;
/*     */ import com.aelitis.net.udp.mc.MCGroupAdapter;
/*     */ import com.aelitis.net.udp.mc.MCGroupFactory;
/*     */ import com.aelitis.net.upnp.UPnPException;
/*     */ import com.aelitis.net.upnp.UPnPSSDP;
/*     */ import com.aelitis.net.upnp.UPnPSSDPAdapter;
/*     */ import com.aelitis.net.upnp.UPnPSSDPListener;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*     */ import org.gudy.azureus2.plugins.utils.UTTimer;
/*     */ import org.gudy.azureus2.plugins.utils.UTTimerEvent;
/*     */ import org.gudy.azureus2.plugins.utils.UTTimerEventPerformer;
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
/*     */ public class SSDPCore
/*     */   implements UPnPSSDP, MCGroupAdapter
/*     */ {
/*     */   private static final String HTTP_VERSION = "1.1";
/*     */   private static final String NL = "\r\n";
/*  53 */   private static Map singletons = new HashMap();
/*  54 */   private static AEMonitor class_mon = new AEMonitor("SSDPCore:class");
/*     */   
/*     */   private MCGroup mc_group;
/*     */   
/*     */   private UPnPSSDPAdapter adapter;
/*     */   private String group_address_str;
/*     */   private int group_port;
/*     */   
/*     */   public static SSDPCore getSingleton(UPnPSSDPAdapter adapter, String group_address, int group_port, int control_port, String[] selected_interfaces)
/*     */     throws UPnPException
/*     */   {
/*     */     try
/*     */     {
/*  67 */       class_mon.enter();
/*     */       
/*  69 */       String key = group_address + ":" + group_port + ":" + control_port;
/*     */       
/*  71 */       SSDPCore singleton = (SSDPCore)singletons.get(key);
/*     */       
/*  73 */       if (singleton == null)
/*     */       {
/*  75 */         singleton = new SSDPCore(adapter, group_address, group_port, control_port, selected_interfaces);
/*     */         
/*  77 */         singletons.put(key, singleton);
/*     */       }
/*     */       
/*  80 */       return singleton;
/*     */     }
/*     */     finally
/*     */     {
/*  84 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  94 */   private boolean first_response = true;
/*     */   
/*  96 */   private List listeners = new ArrayList();
/*     */   
/*     */   private UTTimer timer;
/*  99 */   private List timer_queue = new ArrayList();
/*     */   
/*     */   private long time_event_next;
/* 102 */   protected AEMonitor this_mon = new AEMonitor("SSDP");
/*     */   
/* 104 */   private Set<String> ignore_mx = new HashSet();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private SSDPCore(UPnPSSDPAdapter _adapter, String _group_address, int _group_port, int _control_port, String[] _selected_interfaces)
/*     */     throws UPnPException
/*     */   {
/* 116 */     this.adapter = _adapter;
/*     */     
/* 118 */     this.group_address_str = _group_address;
/* 119 */     this.group_port = _group_port;
/*     */     try
/*     */     {
/* 122 */       this.mc_group = MCGroupFactory.getSingleton(this, _group_address, this.group_port, _control_port, _selected_interfaces);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 126 */       throw new UPnPException("Failed to initialise SSDP", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getControlPort()
/*     */   {
/* 133 */     return this.mc_group.getControlPort();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void trace(String str)
/*     */   {
/* 140 */     this.adapter.log(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void log(Throwable e)
/*     */   {
/* 147 */     this.adapter.log(e);
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
/*     */   public void notify(String NT, String NTS, String UUID, String url)
/*     */   {
/* 168 */     if (url.startsWith("/"))
/*     */     {
/* 170 */       url = url.substring(1);
/*     */     }
/*     */     
/* 173 */     String str = "NOTIFY * HTTP/1.1\r\nHOST: " + this.group_address_str + ":" + this.group_port + "\r\n" + "CACHE-CONTROL: max-age=3600" + "\r\n" + "LOCATION: http://%AZINTERFACE%:" + this.mc_group.getControlPort() + "/" + url + "\r\n" + "NT: " + NT + "\r\n" + "NTS: " + NTS + "\r\n" + "SERVER: " + getServerName() + "\r\n" + "USN: " + (UUID == null ? "" : new StringBuilder().append(UUID).append("::").toString()) + NT + "\r\n" + "\r\n";
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 185 */       this.mc_group.sendToGroup(str);
/*     */     }
/*     */     catch (Throwable e) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String getServerName()
/*     */   {
/* 194 */     return System.getProperty("os.name") + "/" + System.getProperty("os.version") + " UPnP/1.0 " + "Azureus" + "/" + "5.7.6.0";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void search(String[] STs)
/*     */   {
/* 202 */     for (String ST : STs)
/*     */     {
/* 204 */       String str = "M-SEARCH * HTTP/1.1\r\nST: " + ST + "\r\n" + "MX: 3" + "\r\n" + "MAN: \"ssdp:discover\"" + "\r\n" + "HOST: " + this.group_address_str + ":" + this.group_port + "\r\n" + "\r\n";
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 211 */       sendMC(str);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void sendMC(String str)
/*     */   {
/* 219 */     byte[] data = str.getBytes();
/*     */     
/*     */     try
/*     */     {
/* 223 */       this.mc_group.sendToGroup(data);
/*     */     }
/*     */     catch (Throwable e) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void interfaceChanged(NetworkInterface network_interface)
/*     */   {
/* 233 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try
/*     */       {
/* 236 */         ((UPnPSSDPListener)this.listeners.get(i)).interfaceChanged(network_interface);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 240 */         this.adapter.log(e);
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
/*     */   public void received(NetworkInterface network_interface, InetAddress local_address, final InetSocketAddress originator, byte[] packet_data, int length)
/*     */   {
/* 253 */     String str = new String(packet_data, 0, length);
/*     */     
/* 255 */     if (this.first_response)
/*     */     {
/* 257 */       this.first_response = false;
/*     */       
/* 259 */       this.adapter.trace("UPnP:SSDP: first response:\n" + str);
/*     */     }
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
/* 278 */     List<String> lines = new ArrayList();
/*     */     
/* 280 */     int pos = 0;
/*     */     
/*     */     for (;;)
/*     */     {
/* 284 */       int p1 = str.indexOf("\r\n", pos);
/*     */       
/*     */       String line;
/*     */       String line;
/* 288 */       if (p1 == -1)
/*     */       {
/* 290 */         line = str.substring(pos);
/*     */       }
/*     */       else {
/* 293 */         line = str.substring(pos, p1);
/*     */         
/* 295 */         pos = p1 + 1;
/*     */       }
/*     */       
/* 298 */       lines.add(line.trim());
/*     */       
/* 300 */       if (p1 == -1) {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 306 */     if (lines.size() == 0)
/*     */     {
/* 308 */       this.adapter.trace("SSDP::receive packet - 0 line reply");
/*     */       
/* 310 */       return;
/*     */     }
/*     */     
/* 313 */     String header = (String)lines.get(0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 318 */     URL location = null;
/* 319 */     String usn = null;
/* 320 */     String nt = null;
/* 321 */     String nts = null;
/* 322 */     String st = null;
/* 323 */     String al = null;
/* 324 */     String mx = null;
/* 325 */     String server = null;
/*     */     
/* 327 */     for (int i = 1; i < lines.size(); i++)
/*     */     {
/* 329 */       String line = (String)lines.get(i);
/*     */       
/* 331 */       int c_pos = line.indexOf(":");
/*     */       
/* 333 */       if (c_pos != -1)
/*     */       {
/*     */ 
/*     */ 
/* 337 */         String key = line.substring(0, c_pos).trim().toUpperCase();
/* 338 */         String val = line.substring(c_pos + 1).trim();
/*     */         
/* 340 */         if (key.equals("LOCATION"))
/*     */         {
/*     */           try
/*     */           {
/*     */ 
/* 345 */             if (!val.equals("*"))
/*     */             {
/* 347 */               location = new URL(val);
/*     */             }
/*     */           }
/*     */           catch (MalformedURLException e) {
/* 351 */             if (!val.contains("//"))
/*     */             {
/*     */ 
/*     */ 
/* 355 */               val = "http://" + val;
/*     */               try
/*     */               {
/* 358 */                 location = new URL(val);
/*     */               }
/*     */               catch (Throwable f) {}
/*     */             }
/*     */             
/*     */ 
/* 364 */             if (location == null)
/*     */             {
/* 366 */               this.adapter.log(e);
/*     */             }
/*     */           }
/* 369 */         } else if (key.equals("NT"))
/*     */         {
/* 371 */           nt = val;
/*     */         }
/* 373 */         else if (key.equals("USN"))
/*     */         {
/* 375 */           usn = val;
/*     */         }
/* 377 */         else if (key.equals("NTS"))
/*     */         {
/* 379 */           nts = val;
/*     */         }
/* 381 */         else if (key.equals("ST"))
/*     */         {
/* 383 */           st = val;
/*     */         }
/* 385 */         else if (key.equals("AL"))
/*     */         {
/* 387 */           al = val;
/*     */         }
/* 389 */         else if (key.equals("MX"))
/*     */         {
/* 391 */           mx = val;
/*     */         }
/* 393 */         else if (key.equals("SERVER"))
/*     */         {
/* 395 */           server = val;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 404 */     if (server != null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 409 */       if (server.toLowerCase().startsWith("xbox"))
/*     */       {
/* 411 */         String host = originator.getAddress().getHostAddress();
/*     */         
/* 413 */         synchronized (this.ignore_mx)
/*     */         {
/* 415 */           this.ignore_mx.add(host);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 420 */     if (mx != null)
/*     */     {
/* 422 */       String host = originator.getAddress().getHostAddress();
/*     */       
/* 424 */       synchronized (this.ignore_mx)
/*     */       {
/* 426 */         if (this.ignore_mx.contains(host))
/*     */         {
/* 428 */           mx = null;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 433 */     if (header.startsWith("M-SEARCH"))
/*     */     {
/* 435 */       if (st != null)
/*     */       {
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
/* 448 */         String[] response = informSearch(network_interface, local_address, originator.getAddress(), st);
/*     */         
/* 450 */         if (response != null)
/*     */         {
/* 452 */           String UUID = response[0];
/* 453 */           String url = response[1];
/*     */           
/* 455 */           if (url.startsWith("/")) {
/* 456 */             url = url.substring(1);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 461 */           String data = "HTTP/1.1 200 OK\r\nUSN: " + UUID + "::" + st + "\r\n" + "ST: " + st + "\r\n" + "EXT:" + "\r\n" + "Location: http://" + local_address.getHostAddress() + ":" + this.mc_group.getControlPort() + "/" + url + "\r\n" + "Server: Azureus/" + "5.7.6.0" + " UPnP/1.0 Azureus/" + "5.7.6.0" + "\r\n" + "Cache-Control: max-age=3600" + "\r\n" + "Date: " + TimeFormatter.getHTTPDate(SystemTime.getCurrentTime()) + "\r\n" + "Content-Length: 0" + "\r\n" + "\r\n";
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
/* 472 */           final byte[] data_bytes = data.getBytes();
/*     */           
/* 474 */           if (this.timer == null)
/*     */           {
/* 476 */             this.timer = this.adapter.createTimer("SSDPCore:MX");
/*     */           }
/*     */           
/* 479 */           int delay = 0;
/*     */           
/* 481 */           if (mx != null)
/*     */           {
/*     */             try
/*     */             {
/* 485 */               delay = Integer.parseInt(mx) * 1000;
/*     */               
/* 487 */               delay = RandomUtils.nextInt(delay);
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */           
/*     */ 
/* 493 */           Runnable task = new Runnable()
/*     */           {
/*     */ 
/*     */             public void run()
/*     */             {
/*     */               try
/*     */               {
/* 500 */                 SSDPCore.this.mc_group.sendToMember(originator, data_bytes);
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 504 */                 SSDPCore.this.adapter.log(e);
/*     */               }
/*     */             }
/*     */           };
/*     */           
/* 509 */           if (delay == 0)
/*     */           {
/* 511 */             task.run();
/*     */           }
/*     */           else
/*     */           {
/* 515 */             long target_time = SystemTime.getCurrentTime() + delay;
/*     */             
/*     */             boolean schedule_event;
/*     */             
/* 519 */             synchronized (this.timer_queue)
/*     */             {
/* 521 */               this.timer_queue.add(task);
/*     */               
/* 523 */               schedule_event = (this.time_event_next == 0L) || (target_time < this.time_event_next);
/*     */               
/* 525 */               if (schedule_event)
/*     */               {
/* 527 */                 this.time_event_next = target_time;
/*     */               }
/*     */             }
/*     */             
/* 531 */             if (schedule_event)
/*     */             {
/* 533 */               this.timer.addEvent(target_time, new UTTimerEventPerformer()
/*     */               {
/*     */                 public void perform(UTTimerEvent event)
/*     */                 {
/*     */                   for (;;)
/*     */                   {
/*     */                     Runnable t;
/*     */                     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 548 */                     synchronized (SSDPCore.this.timer_queue) {
/*     */                       Runnable t;
/* 550 */                       if (SSDPCore.this.timer_queue.size() > 0)
/*     */                       {
/* 552 */                         t = (Runnable)SSDPCore.this.timer_queue.remove(0);
/*     */                       }
/*     */                       else
/*     */                       {
/* 556 */                         SSDPCore.this.time_event_next = 0L;
/*     */                         
/* 558 */                         return;
/*     */                       }
/*     */                     }
/*     */                     try
/*     */                     {
/* 563 */                       t.run();
/*     */                     }
/*     */                     catch (Throwable e)
/*     */                     {
/* 567 */                       Debug.printStackTrace(e);
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               });
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/* 577 */         this.adapter.trace("SSDP::receive M-SEARCH - bad header:" + header);
/*     */       }
/* 579 */     } else if (header.startsWith("NOTIFY"))
/*     */     {
/*     */ 
/*     */ 
/* 583 */       if ((nt != null) && (nts != null))
/*     */       {
/* 585 */         informNotify(network_interface, local_address, originator.getAddress(), usn, location, nt, nts);
/*     */       }
/*     */       else
/*     */       {
/* 589 */         this.adapter.trace("SSDP::receive NOTIFY - bad header:" + header);
/*     */       }
/* 591 */     } else if ((header.startsWith("HTTP")) && (header.contains("200")))
/*     */     {
/* 593 */       if ((location != null) && (st != null))
/*     */       {
/* 595 */         informResult(network_interface, local_address, originator.getAddress(), usn, location, st, al);
/*     */       }
/*     */       else
/*     */       {
/* 599 */         this.adapter.trace("SSDP::receive HTTP - bad header:" + header);
/*     */       }
/*     */     }
/*     */     else {
/* 603 */       this.adapter.trace("SSDP::receive packet - bad header:" + header);
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
/*     */   protected void informResult(NetworkInterface network_interface, InetAddress local_address, InetAddress originator, String usn, URL location, String st, String al)
/*     */   {
/* 618 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try
/*     */       {
/* 621 */         ((UPnPSSDPListener)this.listeners.get(i)).receivedResult(network_interface, local_address, originator, usn, location, st, al);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 625 */         this.adapter.log(e);
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
/*     */ 
/*     */ 
/*     */   protected void informNotify(NetworkInterface network_interface, InetAddress local_address, InetAddress originator, String usn, URL location, String nt, String nts)
/*     */   {
/* 640 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try
/*     */       {
/* 643 */         ((UPnPSSDPListener)this.listeners.get(i)).receivedNotify(network_interface, local_address, originator, usn, location, nt, nts);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 647 */         this.adapter.log(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String[] informSearch(NetworkInterface network_interface, InetAddress local_address, InetAddress originator, String st)
/*     */   {
/* 659 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try
/*     */       {
/* 662 */         String[] res = ((UPnPSSDPListener)this.listeners.get(i)).receivedSearch(network_interface, local_address, originator, st);
/*     */         
/* 664 */         if (res != null)
/*     */         {
/* 666 */           return res;
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 670 */         this.adapter.log(e);
/*     */       }
/*     */     }
/*     */     
/* 674 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(UPnPSSDPListener l)
/*     */   {
/* 681 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(UPnPSSDPListener l)
/*     */   {
/* 688 */     this.listeners.remove(l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/ssdp/SSDPCore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */