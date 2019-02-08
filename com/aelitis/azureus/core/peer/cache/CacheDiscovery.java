/*     */ package com.aelitis.azureus.core.peer.cache;
/*     */ 
/*     */ import com.aelitis.azureus.core.download.DownloadManagerEnhancer;
/*     */ import com.aelitis.azureus.core.download.EnhancedDownloadManager;
/*     */ import java.net.InetAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.ipfilter.BannedIp;
/*     */ import org.gudy.azureus2.core3.ipfilter.IPFilterListener;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilter;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilterManager;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilterManagerFactory;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
/*     */ import org.gudy.azureus2.core3.util.IPToHostNameResolver;
/*     */ import org.gudy.azureus2.core3.util.IPToHostNameResolverListener;
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
/*     */ 
/*     */ 
/*     */ public class CacheDiscovery
/*     */ {
/*  46 */   private static final IpFilter ip_filter = IpFilterManagerFactory.getSingleton().getIPFilter();
/*     */   
/*  48 */   private static final CacheDiscoverer[] discoverers = new CacheDiscoverer[0];
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  53 */   private static Set<String> cache_ips = Collections.synchronizedSet(new HashSet());
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void initialise(DownloadManagerEnhancer dme)
/*     */   {
/*  60 */     ip_filter.addListener(new IPFilterListener()
/*     */     {
/*     */       public void IPFilterEnabledChanged(boolean is_enabled) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean canIPBeBanned(String ip)
/*     */       {
/*  73 */         return CacheDiscovery.canBan(ip);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void IPBanned(BannedIp ip) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void IPBlockedListChanged(IpFilter filter) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean canIPBeBlocked(String ip, byte[] torrent_hash)
/*     */       {
/*  93 */         EnhancedDownloadManager dm = this.val$dme.getEnhancedDownload(torrent_hash);
/*     */         
/*  95 */         if (dm == null)
/*     */         {
/*  97 */           return true;
/*     */         }
/*     */         
/* 100 */         if (dm.isPlatform())
/*     */         {
/* 102 */           return CacheDiscovery.canBan(ip);
/*     */         }
/*     */         
/* 105 */         return true;
/*     */       }
/*     */       
/* 108 */     });
/* 109 */     new AEThread2("CacheDiscovery:ban checker", true)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 114 */         BannedIp[] bans = CacheDiscovery.ip_filter.getBannedIps();
/*     */         
/* 116 */         for (int i = 0; i < bans.length; i++)
/*     */         {
/* 118 */           String ip = bans[i].getIp();
/*     */           
/* 120 */           if (!CacheDiscovery.canBan(ip))
/*     */           {
/* 122 */             CacheDiscovery.ip_filter.unban(ip);
/*     */           }
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static boolean canBan(String ip)
/*     */   {
/* 133 */     if (cache_ips.contains(ip))
/*     */     {
/* 135 */       return false;
/*     */     }
/*     */     try
/*     */     {
/* 139 */       InetAddress address = HostNameToIPResolver.syncResolve(ip);
/*     */       
/* 141 */       String host_address = address.getHostAddress();
/*     */       
/* 143 */       if (cache_ips.contains(host_address))
/*     */       {
/* 145 */         return false;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 150 */       IPToHostNameResolver.addResolverRequest(ip, new IPToHostNameResolverListener()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public void IPResolutionComplete(String result, boolean succeeded)
/*     */         {
/*     */ 
/*     */ 
/* 159 */           if (Constants.isAzureusDomain(result))
/*     */           {
/* 161 */             CacheDiscovery.cache_ips.add(this.val$host_address);
/*     */             
/* 163 */             CacheDiscovery.ip_filter.unban(this.val$host_address, true);
/*     */           }
/*     */           
/*     */         }
/* 167 */       });
/* 168 */       return true;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 172 */       Debug.printStackTrace(e);
/*     */     }
/* 174 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public static CachePeer[] lookup(TOTorrent torrent)
/*     */   {
/*     */     CachePeer[] res;
/*     */     
/*     */     CachePeer[] res;
/*     */     
/* 184 */     if (discoverers.length == 0)
/*     */     {
/* 186 */       res = new CachePeer[0];
/*     */     } else { CachePeer[] res;
/* 188 */       if (discoverers.length == 1)
/*     */       {
/* 190 */         res = discoverers[0].lookup(torrent);
/*     */       }
/*     */       else
/*     */       {
/* 194 */         List<CachePeer> result = new ArrayList();
/*     */         
/* 196 */         for (int i = 0; i < discoverers.length; i++)
/*     */         {
/* 198 */           CachePeer[] peers = discoverers[i].lookup(torrent);
/*     */           
/* 200 */           for (int j = 0; j < peers.length; j++)
/*     */           {
/* 202 */             result.add(peers[i]);
/*     */           }
/*     */         }
/*     */         
/* 206 */         res = (CachePeer[])result.toArray(new CachePeer[result.size()]);
/*     */       }
/*     */     }
/* 209 */     for (int i = 0; i < res.length; i++)
/*     */     {
/* 211 */       String ip = res[i].getAddress().getHostAddress();
/*     */       
/* 213 */       cache_ips.add(ip);
/*     */       
/* 215 */       ip_filter.unban(ip);
/*     */     }
/*     */     
/* 218 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static CachePeer categorisePeer(byte[] peer_id, InetAddress ip, int port)
/*     */   {
/* 227 */     for (int i = 0; i < discoverers.length; i++)
/*     */     {
/* 229 */       CachePeer cp = discoverers[i].lookup(peer_id, ip, port);
/*     */       
/* 231 */       if (cp != null)
/*     */       {
/* 233 */         return cp;
/*     */       }
/*     */     }
/*     */     
/* 237 */     return new CachePeerImpl(1, ip, port);
/*     */   }
/*     */   
/*     */ 
/*     */   public static class CachePeerImpl
/*     */     implements CachePeer
/*     */   {
/*     */     private int type;
/*     */     private InetAddress address;
/*     */     private int port;
/*     */     private long create_time;
/*     */     private long inject_time;
/*     */     private long speed_change_time;
/* 250 */     private boolean auto_reconnect = true;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public CachePeerImpl(int _type, InetAddress _address, int _port)
/*     */     {
/* 258 */       this.type = _type;
/* 259 */       this.address = _address;
/* 260 */       this.port = _port;
/*     */       
/* 262 */       this.create_time = SystemTime.getCurrentTime();
/*     */     }
/*     */     
/*     */ 
/*     */     public int getType()
/*     */     {
/* 268 */       return this.type;
/*     */     }
/*     */     
/*     */ 
/*     */     public InetAddress getAddress()
/*     */     {
/* 274 */       return this.address;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getPort()
/*     */     {
/* 280 */       return this.port;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public long getCreateTime(long now)
/*     */     {
/* 287 */       if (this.create_time > now)
/*     */       {
/* 289 */         this.create_time = now;
/*     */       }
/*     */       
/* 292 */       return this.create_time;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public long getInjectTime(long now)
/*     */     {
/* 299 */       if (this.inject_time > now)
/*     */       {
/* 301 */         this.inject_time = now;
/*     */       }
/*     */       
/* 304 */       return this.inject_time;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void setInjectTime(long time)
/*     */     {
/* 311 */       this.inject_time = time;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public long getSpeedChangeTime(long now)
/*     */     {
/* 318 */       if (this.speed_change_time > now)
/*     */       {
/* 320 */         this.speed_change_time = now;
/*     */       }
/*     */       
/* 323 */       return this.speed_change_time;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void setSpeedChangeTime(long time)
/*     */     {
/* 330 */       this.speed_change_time = time;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean getAutoReconnect()
/*     */     {
/* 336 */       return this.auto_reconnect;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void setAutoReconnect(boolean auto)
/*     */     {
/* 343 */       this.auto_reconnect = auto;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean sameAs(CachePeer other)
/*     */     {
/* 350 */       return (getType() == other.getType()) && (getAddress().getHostAddress().equals(other.getAddress().getHostAddress())) && (getPort() == other.getPort());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getString()
/*     */     {
/* 359 */       return "type=" + getType() + ",address=" + getAddress() + ",port=" + getPort();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peer/cache/CacheDiscovery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */