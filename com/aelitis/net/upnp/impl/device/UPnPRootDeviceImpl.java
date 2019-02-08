/*     */ package com.aelitis.net.upnp.impl.device;
/*     */ 
/*     */ import com.aelitis.net.upnp.UPnP;
/*     */ import com.aelitis.net.upnp.UPnPAdapter;
/*     */ import com.aelitis.net.upnp.UPnPDevice;
/*     */ import com.aelitis.net.upnp.UPnPException;
/*     */ import com.aelitis.net.upnp.UPnPRootDevice;
/*     */ import com.aelitis.net.upnp.UPnPRootDeviceListener;
/*     */ import com.aelitis.net.upnp.impl.UPnPImpl;
/*     */ import java.net.InetAddress;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocument;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentNode;
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
/*     */ public class UPnPRootDeviceImpl
/*     */   implements UPnPRootDevice
/*     */ {
/*  42 */   public static final String[] ROUTERS = { "3Com ADSL 11g" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  47 */   public static final String[] BAD_ROUTER_VERSIONS = { "2.05" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  52 */   public static final boolean[] BAD_ROUTER_REPORT_FAIL = { true };
/*     */   
/*     */ 
/*     */   private final UPnPImpl upnp;
/*     */   
/*     */ 
/*     */   private final NetworkInterface network_interface;
/*     */   
/*     */   private final InetAddress local_address;
/*     */   
/*     */   private final String usn;
/*     */   
/*     */   private final URL location;
/*     */   
/*  66 */   private final List<URL> alt_locations = new ArrayList();
/*     */   
/*     */   private URL url_base_for_relative_urls;
/*     */   
/*     */   private URL saved_url_base_for_relative_urls;
/*     */   
/*     */   private String info;
/*     */   
/*     */   private UPnPDeviceImpl root_device;
/*     */   
/*     */   private boolean port_mapping_result_received;
/*     */   
/*     */   private boolean destroyed;
/*  79 */   private List listeners = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public UPnPRootDeviceImpl(UPnPImpl _upnp, NetworkInterface _network_interface, InetAddress _local_address, String _usn, URL _location)
/*     */     throws UPnPException
/*     */   {
/*  91 */     this.upnp = _upnp;
/*  92 */     this.network_interface = _network_interface;
/*  93 */     this.local_address = _local_address;
/*  94 */     this.usn = _usn;
/*  95 */     this.location = _location;
/*     */     
/*  97 */     SimpleXMLParserDocument doc = this.upnp.downloadXML(this, this.location);
/*     */     
/*  99 */     SimpleXMLParserDocumentNode url_base_node = doc.getChild("URLBase");
/*     */     try
/*     */     {
/* 102 */       if (url_base_node != null)
/*     */       {
/* 104 */         String url_str = url_base_node.getValue().trim();
/*     */         
/*     */ 
/*     */ 
/* 108 */         if (url_str.length() > 0)
/*     */         {
/* 110 */           this.url_base_for_relative_urls = new URL(url_str);
/*     */         }
/*     */       }
/*     */       
/* 114 */       this.upnp.log("Relative URL base is " + (this.url_base_for_relative_urls == null ? "unspecified" : this.url_base_for_relative_urls.toString()));
/*     */     }
/*     */     catch (MalformedURLException e)
/*     */     {
/* 118 */       this.upnp.log("Invalid URLBase - " + (url_base_node == null ? "mill" : url_base_node.getValue()));
/*     */       
/* 120 */       this.upnp.log(e);
/*     */       
/* 122 */       Debug.printStackTrace(e);
/*     */     }
/*     */     
/* 125 */     SimpleXMLParserDocumentNode device = doc.getChild("Device");
/*     */     
/* 127 */     if (device == null)
/*     */     {
/* 129 */       throw new UPnPException("Root device '" + this.usn + "(" + this.location + ") is missing the device description");
/*     */     }
/*     */     
/* 132 */     this.root_device = new UPnPDeviceImpl(this, "", device);
/*     */     
/* 134 */     this.info = this.root_device.getFriendlyName();
/*     */     
/* 136 */     String version = this.root_device.getModelNumber();
/*     */     
/* 138 */     if (version != null)
/*     */     {
/* 140 */       this.info = (this.info + "/" + version);
/*     */     }
/*     */   }
/*     */   
/*     */   public Map getDiscoveryCache()
/*     */   {
/*     */     try
/*     */     {
/* 148 */       Map cache = new HashMap();
/*     */       
/* 150 */       cache.put("ni", this.network_interface.getName().getBytes("UTF-8"));
/* 151 */       cache.put("la", this.local_address.getHostAddress().getBytes("UTF-8"));
/* 152 */       cache.put("usn", this.usn.getBytes("UTF-8"));
/* 153 */       cache.put("loc", this.location.toExternalForm().getBytes("UTF-8"));
/*     */       
/* 155 */       return cache;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 159 */       Debug.printStackTrace(e);
/*     */     }
/* 161 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void portMappingResult(boolean ok)
/*     */   {
/* 169 */     if (this.port_mapping_result_received)
/*     */     {
/* 171 */       return;
/*     */     }
/*     */     
/* 174 */     this.port_mapping_result_received = true;
/*     */     
/* 176 */     if (ok)
/*     */     {
/* 178 */       this.info += "/OK";
/*     */     }
/*     */     else
/*     */     {
/* 182 */       this.info += "/Failed";
/*     */     }
/*     */     
/* 185 */     String model = this.root_device.getModelName();
/* 186 */     String version = this.root_device.getModelNumber();
/*     */     
/* 188 */     if ((model == null) || (version == null))
/*     */     {
/* 190 */       return;
/*     */     }
/*     */     
/* 193 */     for (int i = 0; i < ROUTERS.length; i++)
/*     */     {
/* 195 */       if (ROUTERS[i].equals(model))
/*     */       {
/* 197 */         if (isBadVersion(version, BAD_ROUTER_VERSIONS[i]))
/*     */         {
/* 199 */           boolean report_on_fail = BAD_ROUTER_REPORT_FAIL[i];
/*     */           
/* 201 */           if ((report_on_fail) && (ok)) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 207 */           String url = this.root_device.getModelURL();
/*     */           
/* 209 */           this.upnp.logAlert("Device '" + model + "', version '" + version + "' has known problems with UPnP. Please update to the latest software version (see " + (url == null ? "the manufacturer's web site" : url) + ") and refer to http://wiki.vuze.com/w/UPnP", false, 3);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 217 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getInfo()
/*     */   {
/* 226 */     return this.info;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String getAbsoluteURL(String url)
/*     */   {
/* 233 */     String lc_url = url.toLowerCase().trim();
/*     */     
/* 235 */     if ((lc_url.startsWith("http://")) || (lc_url.startsWith("https://")))
/*     */     {
/*     */ 
/*     */ 
/* 239 */       return url;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 244 */     if (this.url_base_for_relative_urls != null)
/*     */     {
/* 246 */       String abs_url = this.url_base_for_relative_urls.toString();
/*     */       
/* 248 */       if (!abs_url.endsWith("/"))
/*     */       {
/* 250 */         abs_url = abs_url + "/";
/*     */       }
/*     */       
/* 253 */       if (url.startsWith("/"))
/*     */       {
/* 255 */         abs_url = abs_url + url.substring(1);
/*     */       }
/*     */       else
/*     */       {
/* 259 */         abs_url = abs_url + url;
/*     */       }
/*     */       
/* 262 */       return abs_url;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 268 */     String abs_url = this.location.toString();
/*     */     
/* 270 */     int p1 = abs_url.indexOf("://") + 3;
/*     */     
/* 272 */     p1 = abs_url.indexOf("/", p1);
/*     */     
/* 274 */     if (p1 != -1)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 280 */       abs_url = abs_url.substring(0, p1);
/*     */     }
/*     */     
/* 283 */     return abs_url + (url.startsWith("/") ? "" : "/") + url;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected synchronized void clearRelativeBaseURL()
/*     */   {
/* 290 */     if (this.url_base_for_relative_urls != null)
/*     */     {
/* 292 */       this.saved_url_base_for_relative_urls = this.url_base_for_relative_urls;
/* 293 */       this.url_base_for_relative_urls = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected synchronized void restoreRelativeBaseURL()
/*     */   {
/* 300 */     if (this.saved_url_base_for_relative_urls != null)
/*     */     {
/* 302 */       this.url_base_for_relative_urls = this.saved_url_base_for_relative_urls;
/* 303 */       this.saved_url_base_for_relative_urls = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public UPnP getUPnP()
/*     */   {
/* 310 */     return this.upnp;
/*     */   }
/*     */   
/*     */ 
/*     */   public NetworkInterface getNetworkInterface()
/*     */   {
/* 316 */     return this.network_interface;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getLocalAddress()
/*     */   {
/* 322 */     return this.local_address;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUSN()
/*     */   {
/* 328 */     return this.usn;
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getLocation()
/*     */   {
/* 334 */     return this.location;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean addAlternativeLocation(URL alt_location)
/*     */   {
/* 341 */     synchronized (this.alt_locations)
/*     */     {
/* 343 */       if (!this.alt_locations.contains(alt_location))
/*     */       {
/* 345 */         this.alt_locations.add(alt_location);
/*     */         
/* 347 */         if (this.alt_locations.size() > 10)
/*     */         {
/* 349 */           this.alt_locations.remove(0);
/*     */         }
/*     */         
/* 352 */         return true;
/*     */       }
/*     */       
/*     */ 
/* 356 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public List<URL> getAlternativeLocations()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 379	com/aelitis/net/upnp/impl/device/UPnPRootDeviceImpl:alt_locations	Ljava/util/List;
/*     */     //   4: dup
/*     */     //   5: astore_1
/*     */     //   6: monitorenter
/*     */     //   7: new 230	java/util/ArrayList
/*     */     //   10: dup
/*     */     //   11: aload_0
/*     */     //   12: getfield 379	com/aelitis/net/upnp/impl/device/UPnPRootDeviceImpl:alt_locations	Ljava/util/List;
/*     */     //   15: invokespecial 420	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
/*     */     //   18: aload_1
/*     */     //   19: monitorexit
/*     */     //   20: areturn
/*     */     //   21: astore_2
/*     */     //   22: aload_1
/*     */     //   23: monitorexit
/*     */     //   24: aload_2
/*     */     //   25: athrow
/*     */     // Line number table:
/*     */     //   Java source line #364	-> byte code offset #0
/*     */     //   Java source line #366	-> byte code offset #7
/*     */     //   Java source line #367	-> byte code offset #21
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	26	0	this	UPnPRootDeviceImpl
/*     */     //   5	18	1	Ljava/lang/Object;	Object
/*     */     //   21	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	20	21	finally
/*     */     //   21	24	21	finally
/*     */   }
/*     */   
/*     */   public UPnPDevice getDevice()
/*     */   {
/* 373 */     return this.root_device;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void destroy(boolean replaced)
/*     */   {
/* 380 */     this.destroyed = true;
/*     */     
/* 382 */     for (int i = 0; i < this.listeners.size(); i++)
/*     */     {
/* 384 */       ((UPnPRootDeviceListener)this.listeners.get(i)).lost(this, replaced);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDestroyed()
/*     */   {
/* 391 */     return this.destroyed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(UPnPRootDeviceListener l)
/*     */   {
/* 398 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(UPnPRootDeviceListener l)
/*     */   {
/* 405 */     this.listeners.remove(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean isBadVersion(String current, String bad)
/*     */   {
/* 413 */     if (bad.equals("any"))
/*     */     {
/* 415 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 419 */     Comparator comp = this.upnp.getAdapter().getAlphanumericComparator();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 425 */     Set delimiters = new HashSet();
/*     */     
/* 427 */     char current_delim = '1';
/* 428 */     char bad_delim = '1';
/*     */     
/* 430 */     for (int i = 0; i < current.length(); i++)
/*     */     {
/* 432 */       char c = current.charAt(i);
/*     */       
/* 434 */       if (!Character.isLetterOrDigit(c))
/*     */       {
/* 436 */         delimiters.add(new Character(c));
/*     */         
/* 438 */         current_delim = c;
/*     */       }
/*     */     }
/*     */     
/* 442 */     for (int i = 0; i < bad.length(); i++)
/*     */     {
/* 444 */       char c = bad.charAt(i);
/*     */       
/* 446 */       if (!Character.isLetterOrDigit(c))
/*     */       {
/* 448 */         delimiters.add(new Character(c));
/*     */         
/* 450 */         bad_delim = c;
/*     */       }
/*     */     }
/*     */     
/* 454 */     if ((delimiters.size() != 1) || (current_delim != bad_delim))
/*     */     {
/*     */ 
/* 457 */       return comp.compare(current, bad) <= 0;
/*     */     }
/*     */     
/* 460 */     StringTokenizer current_tk = new StringTokenizer(current, "" + current_delim);
/* 461 */     StringTokenizer bad_tk = new StringTokenizer(bad, "" + bad_delim);
/*     */     
/* 463 */     int num_current = current_tk.countTokens();
/* 464 */     int num_bad = bad_tk.countTokens();
/*     */     
/* 466 */     for (int i = 0; i < Math.min(num_current, num_bad); i++)
/*     */     {
/* 468 */       String current_token = current_tk.nextToken();
/* 469 */       String bad_token = bad_tk.nextToken();
/*     */       
/* 471 */       int res = comp.compare(current_token, bad_token);
/*     */       
/* 473 */       if (res != 0)
/*     */       {
/* 475 */         return res < 0;
/*     */       }
/*     */     }
/*     */     
/* 479 */     return num_current <= num_bad;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/device/UPnPRootDeviceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */