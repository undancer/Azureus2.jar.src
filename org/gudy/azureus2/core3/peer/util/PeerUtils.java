/*     */ package org.gudy.azureus2.core3.peer.util;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import java.net.InetAddress;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.CRC32C;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.peers.Peer;
/*     */ import org.gudy.azureus2.plugins.utils.LocationProvider;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
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
/*     */ public class PeerUtils
/*     */ {
/*     */   private static final String CONFIG_MAX_CONN_PER_TORRENT = "Max.Peer.Connections.Per.Torrent";
/*     */   private static final String CONFIG_MAX_CONN_TOTAL = "Max.Peer.Connections.Total";
/*     */   public static int MAX_CONNECTIONS_PER_TORRENT;
/*     */   public static int MAX_CONNECTIONS_TOTAL;
/*     */   private static final NetworkAdmin network_admin;
/*     */   private static volatile long na_last_ip4_time;
/*     */   private static volatile long na_last_ip6_time;
/*     */   private static volatile byte[] na_last_ip4;
/*     */   private static volatile byte[] na_last_ip6;
/*     */   private static int na_tcp_port;
/*     */   private static final Set<Integer> ignore_peer_ports;
/*     */   static final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
/*     */   private static volatile LocationProvider country_provider;
/*     */   private static long country_provider_last_check;
/*     */   
/*     */   public static int getPeerPriority(String address, int port)
/*     */   {
/* 120 */     if (network_admin == null)
/*     */     {
/* 122 */       return 0;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 127 */       InetAddress ia = HostNameToIPResolver.syncResolve(address);
/*     */       
/* 129 */       if (ia != null)
/*     */       {
/* 131 */         return getPeerPriority(ia, port);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/* 137 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int getPeerPriority(InetAddress address, int peer_port)
/*     */   {
/* 147 */     return getPeerPriority(address.getAddress(), peer_port);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int getPeerPriority(byte[] peer_address, short peer_port)
/*     */   {
/* 155 */     return getPeerPriority(peer_address, peer_port & 0xFFFF);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int getPeerPriority(byte[] peer_address, int peer_port)
/*     */   {
/* 163 */     if (network_admin == null)
/*     */     {
/* 165 */       return 0;
/*     */     }
/*     */     
/* 168 */     if (peer_address == null)
/*     */     {
/* 170 */       return 0;
/*     */     }
/*     */     
/* 173 */     byte[] my_address = null;
/*     */     
/* 175 */     long now = SystemTime.getMonotonousTime();
/*     */     
/* 177 */     if (peer_address.length == 4)
/*     */     {
/* 179 */       if ((na_last_ip4 != null) && (now - na_last_ip4_time < 120000L))
/*     */       {
/* 181 */         my_address = na_last_ip4;
/*     */ 
/*     */ 
/*     */       }
/* 185 */       else if ((na_last_ip4_time == 0L) || (now - na_last_ip4_time > 10000L))
/*     */       {
/* 187 */         na_last_ip4_time = now;
/*     */         
/* 189 */         InetAddress ia = network_admin.getDefaultPublicAddress(true);
/*     */         
/* 191 */         if (ia != null)
/*     */         {
/* 193 */           byte[] iab = ia.getAddress();
/*     */           
/* 195 */           if (iab != null)
/*     */           {
/* 197 */             na_last_ip4 = my_address = ia.getAddress();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 203 */       if (my_address == null)
/*     */       {
/* 205 */         my_address = na_last_ip4;
/*     */       }
/* 207 */     } else if (peer_address.length == 16)
/*     */     {
/* 209 */       if ((na_last_ip6 != null) && (now - na_last_ip6_time < 120000L))
/*     */       {
/* 211 */         my_address = na_last_ip6;
/*     */ 
/*     */ 
/*     */       }
/* 215 */       else if ((na_last_ip6_time == 0L) || (now - na_last_ip6_time > 10000L))
/*     */       {
/* 217 */         na_last_ip6_time = now;
/*     */         
/* 219 */         InetAddress ia = network_admin.getDefaultPublicAddressV6(true);
/*     */         
/* 221 */         if (ia != null)
/*     */         {
/* 223 */           byte[] iab = ia.getAddress();
/*     */           
/* 225 */           if (iab != null)
/*     */           {
/* 227 */             na_last_ip6 = my_address = ia.getAddress();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 233 */       if (my_address == null)
/*     */       {
/* 235 */         my_address = na_last_ip6;
/*     */       }
/*     */     }
/*     */     else {
/* 239 */       return 0;
/*     */     }
/*     */     
/* 242 */     if ((my_address != null) && (my_address.length == peer_address.length))
/*     */     {
/* 244 */       return getPeerPriority(my_address, na_tcp_port, peer_address, peer_port);
/*     */     }
/*     */     
/*     */ 
/* 248 */     return 0;
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
/*     */   private static int getPeerPriority(byte[] a1, int port1, byte[] a2, int port2)
/*     */   {
/* 261 */     byte[] a1_masked = new byte[a1.length];
/* 262 */     byte[] a2_masked = new byte[a2.length];
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
/* 279 */     int x = a1_masked.length == 4 ? 1 : 5;
/*     */     
/* 281 */     boolean same = true;
/*     */     
/* 283 */     int order = 0;
/*     */     
/* 285 */     for (int i = 0; i < a1_masked.length; i++) {
/* 286 */       byte a1_byte = a1[i];
/* 287 */       byte a2_byte = a2[i];
/*     */       
/* 289 */       if ((i < x) || (same)) {
/* 290 */         a1_masked[i] = a1_byte;
/* 291 */         a2_masked[i] = a2_byte;
/*     */       } else {
/* 293 */         a1_masked[i] = ((byte)(a1_byte & 0x55));
/* 294 */         a2_masked[i] = ((byte)(a2_byte & 0x55));
/*     */       }
/*     */       
/* 297 */       if ((i >= x) && (same)) {
/* 298 */         same = a1_byte == a2_byte;
/*     */       }
/*     */       
/* 301 */       if (order == 0)
/*     */       {
/* 303 */         order = (a1_masked[i] & 0xFF) - (a2_masked[i] & 0xFF);
/*     */       }
/*     */     }
/*     */     
/* 307 */     if (same)
/*     */     {
/* 309 */       a1_masked = new byte[] { (byte)(port1 >> 8), (byte)port1 };
/* 310 */       a2_masked = new byte[] { (byte)(port2 >> 8), (byte)port2 };
/*     */       
/* 312 */       order = port1 - port2;
/*     */     }
/*     */     
/* 315 */     CRC32C crc32 = new CRC32C();
/*     */     
/* 317 */     if (order < 0)
/*     */     {
/* 319 */       crc32.updateWord(a1_masked, true);
/* 320 */       crc32.updateWord(a2_masked, true);
/*     */     }
/*     */     else
/*     */     {
/* 324 */       crc32.updateWord(a2_masked, true);
/* 325 */       crc32.updateWord(a1_masked, true);
/*     */     }
/*     */     
/* 328 */     long res = crc32.getValue();
/*     */     
/* 330 */     return (int)res;
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
/*     */   public static int numNewConnectionsAllowed(PeerIdentityDataID data_id, int specific_max)
/*     */   {
/* 346 */     int curConnPerTorrent = PeerIdentityManager.getIdentityCount(data_id);
/*     */     
/* 348 */     int curConnTotal = PeerIdentityManager.getTotalIdentityCount();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 353 */     int PER_TORRENT_LIMIT = specific_max;
/*     */     
/* 355 */     int perTorrentAllowed = -1;
/* 356 */     if (PER_TORRENT_LIMIT != 0) {
/* 357 */       int allowed = PER_TORRENT_LIMIT - curConnPerTorrent;
/* 358 */       if (allowed < 0) allowed = 0;
/* 359 */       perTorrentAllowed = allowed;
/*     */     }
/*     */     
/* 362 */     int totalAllowed = -1;
/* 363 */     if (MAX_CONNECTIONS_TOTAL != 0) {
/* 364 */       int allowed = MAX_CONNECTIONS_TOTAL - curConnTotal;
/* 365 */       if (allowed < 0) allowed = 0;
/* 366 */       totalAllowed = allowed;
/*     */     }
/*     */     
/* 369 */     int allowed = -1;
/* 370 */     if ((perTorrentAllowed > -1) && (totalAllowed > -1)) {
/* 371 */       allowed = Math.min(perTorrentAllowed, totalAllowed);
/*     */     }
/* 373 */     else if ((perTorrentAllowed == -1) || (totalAllowed == -1)) {
/* 374 */       allowed = Math.max(perTorrentAllowed, totalAllowed);
/*     */     }
/*     */     
/* 377 */     return allowed;
/*     */   }
/*     */   
/*     */   static
/*     */   {
/*  53 */     COConfigurationManager.addParameterListener("Max.Peer.Connections.Per.Torrent", new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/*     */ 
/*     */ 
/*  61 */         PeerUtils.MAX_CONNECTIONS_PER_TORRENT = COConfigurationManager.getIntParameter("Max.Peer.Connections.Per.Torrent");
/*     */       }
/*     */       
/*  64 */     });
/*  65 */     MAX_CONNECTIONS_PER_TORRENT = COConfigurationManager.getIntParameter("Max.Peer.Connections.Per.Torrent");
/*     */     
/*  67 */     COConfigurationManager.addParameterListener("Max.Peer.Connections.Total", new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/*     */ 
/*     */ 
/*  75 */         PeerUtils.MAX_CONNECTIONS_TOTAL = COConfigurationManager.getIntParameter("Max.Peer.Connections.Total");
/*     */       }
/*     */       
/*  78 */     });
/*  79 */     MAX_CONNECTIONS_TOTAL = COConfigurationManager.getIntParameter("Max.Peer.Connections.Total");
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
/*  92 */     NetworkAdmin temp = null;
/*     */     try
/*     */     {
/*  95 */       temp = NetworkAdmin.getSingleton();
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/* 100 */     network_admin = temp;
/*     */     
/* 102 */     COConfigurationManager.addAndFireParameterListener("TCP.Listen.Port", new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/*     */ 
/*     */ 
/* 110 */         PeerUtils.access$002(COConfigurationManager.getIntParameter(parameterName));
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
/*     */ 
/*     */ 
/*     */ 
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
/* 380 */     });
/* 381 */     ignore_peer_ports = new HashSet();
/*     */     
/*     */ 
/* 384 */     COConfigurationManager.addParameterListener("Ignore.peer.ports", new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String parameterName) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 395 */     });
/* 396 */     readIgnorePeerPorts();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void readIgnorePeerPorts()
/*     */   {
/* 403 */     String str = COConfigurationManager.getStringParameter("Ignore.peer.ports").trim();
/*     */     
/* 405 */     ignore_peer_ports.clear();
/*     */     
/* 407 */     if (str.length() > 0)
/*     */     {
/* 409 */       String[] ports = str.split("\\;");
/* 410 */       if ((ports != null) && (ports.length > 0)) {
/* 411 */         for (int i = 0; i < ports.length; i++) {
/* 412 */           String port = ports[i];
/* 413 */           int spreadPos = port.indexOf('-');
/* 414 */           if ((spreadPos > 0) && (spreadPos < port.length() - 1)) {
/*     */             try {
/* 416 */               int iMin = Integer.parseInt(port.substring(0, spreadPos).trim());
/* 417 */               int iMax = Integer.parseInt(port.substring(spreadPos + 1).trim());
/*     */               
/* 419 */               iMin = Math.max(0, iMin);
/* 420 */               iMax = Math.min(65535, iMax);
/*     */               
/* 422 */               for (int j = iMin; j <= iMax; j++) {
/* 423 */                 ignore_peer_ports.add(Integer.valueOf(j));
/*     */               }
/*     */             } catch (Throwable e) {
/* 426 */               Debug.out("Invalid ignore-port entry: " + port);
/*     */             }
/*     */           } else {
/*     */             try {
/* 430 */               ignore_peer_ports.add(Integer.valueOf(Integer.parseInt(port.trim())));
/*     */             } catch (Throwable e) {
/* 432 */               Debug.out("Invalid ignore-port entry: " + port);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean ignorePeerPort(int port)
/*     */   {
/* 444 */     return ignore_peer_ports.contains(Integer.valueOf(port));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] createPeerID()
/*     */   {
/* 452 */     byte[] peerId = new byte[20];
/*     */     
/* 454 */     byte[] version = Constants.VERSION_ID;
/*     */     
/* 456 */     System.arraycopy(version, 0, peerId, 0, 8);
/*     */     
/* 458 */     for (int i = 8; i < 20; i++) {
/* 459 */       int pos = (int)(Math.random() * "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length());
/* 460 */       peerId[i] = ((byte)"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(pos));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 465 */     return peerId;
/*     */   }
/*     */   
/*     */ 
/*     */   public static byte[] createWebSeedPeerID()
/*     */   {
/* 471 */     byte[] peerId = new byte[20];
/*     */     
/* 473 */     peerId[0] = 45;
/* 474 */     peerId[1] = 87;
/* 475 */     peerId[2] = 83;
/*     */     
/* 477 */     for (int i = 3; i < 20; i++) {
/* 478 */       int pos = (int)(Math.random() * "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length());
/* 479 */       peerId[i] = ((byte)"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(pos));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 484 */     return peerId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 491 */   private static final Object country_key = new Object();
/* 492 */   private static final Object net_key = new Object();
/*     */   
/*     */ 
/*     */   private static LocationProvider getCountryProvider()
/*     */   {
/* 497 */     if (country_provider != null)
/*     */     {
/* 499 */       if (country_provider.isDestroyed())
/*     */       {
/* 501 */         country_provider = null;
/* 502 */         country_provider_last_check = 0L;
/*     */       }
/*     */     }
/*     */     
/* 506 */     if (country_provider == null)
/*     */     {
/* 508 */       long now = SystemTime.getMonotonousTime();
/*     */       
/* 510 */       if ((country_provider_last_check == 0L) || (now - country_provider_last_check > 20000L))
/*     */       {
/* 512 */         country_provider_last_check = now;
/*     */         
/* 514 */         List<LocationProvider> providers = AzureusCoreFactory.getSingleton().getPluginManager().getDefaultPluginInterface().getUtilities().getLocationProviders();
/*     */         
/* 516 */         for (LocationProvider provider : providers)
/*     */         {
/* 518 */           if (provider.hasCapabilities(3L))
/*     */           {
/*     */ 
/*     */ 
/* 522 */             country_provider = provider;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 528 */     return country_provider;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String[] getCountryDetails(Peer peer)
/*     */   {
/* 535 */     return getCountryDetails(PluginCoreUtils.unwrap(peer));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String[] getCountryDetails(PEPeer peer)
/*     */   {
/* 542 */     if (peer == null)
/*     */     {
/* 544 */       return null;
/*     */     }
/*     */     
/* 547 */     String[] details = (String[])peer.getUserData(country_key);
/*     */     
/* 549 */     if (details == null)
/*     */     {
/* 551 */       LocationProvider lp = getCountryProvider();
/*     */       
/* 553 */       if (lp != null) {
/*     */         try
/*     */         {
/* 556 */           String ip = peer.getIp();
/*     */           
/* 558 */           if (HostNameToIPResolver.isDNSName(ip))
/*     */           {
/* 560 */             InetAddress peer_address = HostNameToIPResolver.syncResolve(ip);
/*     */             
/* 562 */             String code = lp.getISO3166CodeForIP(peer_address);
/* 563 */             String name = lp.getCountryNameForIP(peer_address, Locale.getDefault());
/*     */             
/* 565 */             if ((code != null) && (name != null))
/*     */             {
/* 567 */               details = new String[] { code, name };
/*     */             }
/*     */             else
/*     */             {
/* 571 */               details = new String[0];
/*     */             }
/*     */           }
/*     */           else {
/* 575 */             String cat = AENetworkClassifier.categoriseAddress(ip);
/*     */             
/* 577 */             if (cat != "Public")
/*     */             {
/* 579 */               details = new String[] { cat, cat };
/*     */             }
/*     */             else
/*     */             {
/* 583 */               details = new String[0];
/*     */             }
/*     */           }
/*     */           
/* 587 */           peer.setUserData(country_key, details);
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 594 */     return details;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String[] getCountryDetails(InetAddress address)
/*     */   {
/* 601 */     if (address == null)
/*     */     {
/* 603 */       return null;
/*     */     }
/*     */     
/* 606 */     String[] details = null;
/*     */     
/* 608 */     LocationProvider lp = getCountryProvider();
/*     */     
/* 610 */     if (lp != null)
/*     */     {
/*     */       try
/*     */       {
/* 614 */         String code = lp.getISO3166CodeForIP(address);
/* 615 */         String name = lp.getCountryNameForIP(address, Locale.getDefault());
/*     */         
/* 617 */         if ((code != null) && (name != null))
/*     */         {
/* 619 */           details = new String[] { code, name };
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/* 625 */     return details;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getNetwork(PEPeer peer)
/*     */   {
/* 632 */     if (peer == null)
/*     */     {
/* 634 */       return null;
/*     */     }
/*     */     
/* 637 */     String net = (String)peer.getUserData(net_key);
/*     */     
/* 639 */     if (net == null)
/*     */     {
/* 641 */       net = AENetworkClassifier.categoriseAddress(peer.getIp());
/*     */       
/* 643 */       peer.setUserData(net_key, net);
/*     */     }
/*     */     
/* 646 */     return net;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/util/PeerUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */