/*     */ package com.aelitis.net.upnp.impl.ssdp;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.NetUtils;
/*     */ import com.aelitis.net.upnp.UPnPAdapter;
/*     */ import com.aelitis.net.upnp.UPnPException;
/*     */ import com.aelitis.net.upnp.UPnPSSDPListener;
/*     */ import com.aelitis.net.upnp.impl.SSDPIGD;
/*     */ import com.aelitis.net.upnp.impl.SSDPIGDListener;
/*     */ import com.aelitis.net.upnp.impl.UPnPImpl;
/*     */ import java.net.InetAddress;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class SSDPIGDImpl
/*     */   implements SSDPIGD, UPnPSSDPListener
/*     */ {
/*     */   private UPnPImpl upnp;
/*     */   private SSDPCore ssdp_core;
/*  43 */   private boolean first_result = true;
/*  44 */   private long last_explicit_search = 0L;
/*     */   
/*  46 */   private List listeners = new ArrayList();
/*     */   
/*  48 */   protected AEMonitor this_mon = new AEMonitor("SSDP");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SSDPIGDImpl(UPnPImpl _upnp, String[] _selected_interfaces)
/*     */     throws UPnPException
/*     */   {
/*  58 */     this.upnp = _upnp;
/*     */     
/*  60 */     this.ssdp_core = SSDPCore.getSingleton(this.upnp.getAdapter(), "239.255.255.250", 1900, 0, _selected_interfaces);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  68 */     this.ssdp_core.addListener(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public SSDPCore getSSDP()
/*     */   {
/*  74 */     return this.ssdp_core;
/*     */   }
/*     */   
/*     */ 
/*     */   public void start()
/*     */     throws UPnPException
/*     */   {
/*     */     try
/*     */     {
/*  83 */       this.upnp.getAdapter().createThread("SSDP:queryLoop", new AERunnable()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/*  90 */           SSDPIGDImpl.this.queryLoop();
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  96 */       Debug.printStackTrace(e);
/*     */       
/*  98 */       throw new UPnPException("Failed to initialise SSDP", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void searchNow()
/*     */   {
/* 107 */     long now = SystemTime.getCurrentTime();
/*     */     
/* 109 */     if (now - this.last_explicit_search < 10000L)
/*     */     {
/* 111 */       return;
/*     */     }
/*     */     
/* 114 */     this.last_explicit_search = now;
/*     */     
/* 116 */     search();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void searchNow(String[] STs)
/*     */   {
/* 123 */     this.ssdp_core.search(STs);
/*     */   }
/*     */   
/*     */   protected void queryLoop()
/*     */   {
/*     */     try
/*     */     {
/*     */       for (;;)
/*     */       {
/* 132 */         search();
/*     */         
/* 134 */         Thread.sleep(60000L);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 138 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void search()
/*     */   {
/* 147 */     this.ssdp_core.search(new String[] { "upnp:rootdevice" });
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
/*     */   public void receivedResult(NetworkInterface network_interface, InetAddress local_address, InetAddress originator, String usn, URL location, String st, String al)
/*     */   {
/*     */     try
/*     */     {
/* 162 */       this.this_mon.enter();
/*     */       
/* 164 */       if (st.equalsIgnoreCase("upnp:rootdevice"))
/*     */       {
/* 166 */         gotRoot(network_interface, local_address, usn, location);
/*     */       }
/*     */     }
/*     */     finally {
/* 170 */       this.first_result = false;
/*     */       
/* 172 */       this.this_mon.exit();
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
/*     */   public void receivedNotify(NetworkInterface network_interface, InetAddress local_address, InetAddress originator, String usn, URL location, String nt, String nts)
/*     */   {
/*     */     try
/*     */     {
/* 187 */       this.this_mon.enter();
/*     */       
/* 189 */       if (nt.contains("upnp:rootdevice"))
/*     */       {
/* 191 */         if (nts.contains("alive"))
/*     */         {
/*     */ 
/*     */           try
/*     */           {
/*     */ 
/* 197 */             InetAddress dev = InetAddress.getByName(location.getHost());
/*     */             
/* 199 */             byte[] dev_bytes = dev.getAddress();
/*     */             
/* 201 */             boolean[] dev_bits = bytesToBits(dev_bytes);
/*     */             
/*     */ 
/*     */ 
/* 205 */             NetworkInterface best_ni = null;
/* 206 */             InetAddress best_addr = null;
/*     */             
/* 208 */             int best_prefix = 0;
/*     */             
/* 210 */             List<NetworkInterface> x = NetUtils.getNetworkInterfaces();
/*     */             
/* 212 */             for (NetworkInterface this_ni : x)
/*     */             {
/* 214 */               Enumeration<InetAddress> ni_addresses = this_ni.getInetAddresses();
/*     */               
/* 216 */               while (ni_addresses.hasMoreElements())
/*     */               {
/* 218 */                 InetAddress this_address = (InetAddress)ni_addresses.nextElement();
/*     */                 
/* 220 */                 byte[] this_bytes = this_address.getAddress();
/*     */                 
/* 222 */                 if (dev_bytes.length == this_bytes.length)
/*     */                 {
/* 224 */                   boolean[] this_bits = bytesToBits(this_bytes);
/*     */                   
/* 226 */                   for (int i = 0; i < this_bits.length; i++)
/*     */                   {
/* 228 */                     if (dev_bits[i] != this_bits[i]) {
/*     */                       break;
/*     */                     }
/*     */                     
/*     */ 
/* 233 */                     if (i > best_prefix)
/*     */                     {
/* 235 */                       best_prefix = i;
/*     */                       
/* 237 */                       best_ni = this_ni;
/* 238 */                       best_addr = this_address;
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 245 */             if (best_ni != null)
/*     */             {
/* 247 */               if (this.first_result)
/*     */               {
/* 249 */                 this.upnp.log(location + " -> " + best_ni.getDisplayName() + "/" + best_addr + " (prefix=" + (best_prefix + 1) + ")");
/*     */               }
/*     */               
/* 252 */               gotRoot(best_ni, best_addr, usn, location);
/*     */             }
/*     */             else
/*     */             {
/* 256 */               gotAlive(usn, location);
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 260 */             gotAlive(usn, location);
/*     */           }
/* 262 */         } else if (nts.contains("byebye"))
/*     */         {
/* 264 */           lostRoot(local_address, usn);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 269 */       this.first_result = false;
/*     */       
/* 271 */       this.this_mon.exit();
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
/*     */   public String[] receivedSearch(NetworkInterface network_interface, InetAddress local_address, InetAddress originator, String ST)
/*     */   {
/* 284 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean[] bytesToBits(byte[] bytes)
/*     */   {
/* 292 */     boolean[] res = new boolean[bytes.length * 8];
/*     */     
/* 294 */     for (int i = 0; i < bytes.length; i++)
/*     */     {
/* 296 */       byte b = bytes[i];
/*     */       
/* 298 */       for (int j = 0; j < 8; j++)
/*     */       {
/* 300 */         res[(i * 8 + j)] = ((b & (byte)(1 << 7 - j)) != 0 ? 1 : false);
/*     */       }
/*     */     }
/*     */     
/* 304 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void gotRoot(NetworkInterface network_interface, InetAddress local_address, String usn, URL location)
/*     */   {
/* 314 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try
/*     */       {
/* 317 */         ((SSDPIGDListener)this.listeners.get(i)).rootDiscovered(network_interface, local_address, usn, location);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 321 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void gotAlive(String usn, URL location)
/*     */   {
/* 331 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try
/*     */       {
/* 334 */         ((SSDPIGDListener)this.listeners.get(i)).rootAlive(usn, location);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 338 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void lostRoot(InetAddress local_address, String usn)
/*     */   {
/* 348 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try
/*     */       {
/* 351 */         ((SSDPIGDListener)this.listeners.get(i)).rootLost(local_address, usn);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 355 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void interfaceChanged(NetworkInterface network_interface)
/*     */   {
/* 364 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try
/*     */       {
/* 367 */         ((SSDPIGDListener)this.listeners.get(i)).interfaceChanged(network_interface);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 371 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(SSDPIGDListener l)
/*     */   {
/* 380 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(SSDPIGDListener l)
/*     */   {
/* 387 */     this.listeners.remove(l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/ssdp/SSDPIGDImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */