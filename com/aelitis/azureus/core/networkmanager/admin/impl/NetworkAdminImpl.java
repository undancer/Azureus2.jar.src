/*      */ package com.aelitis.azureus.core.networkmanager.admin.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstance;
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstanceManager;
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstanceTracked;
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkConnectionBase;
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*      */ import com.aelitis.azureus.core.networkmanager.ProtocolStartpoint;
/*      */ import com.aelitis.azureus.core.networkmanager.Transport;
/*      */ import com.aelitis.azureus.core.networkmanager.TransportBase;
/*      */ import com.aelitis.azureus.core.networkmanager.TransportStartpoint;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminASN;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminASNListener;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminException;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminHTTPProxy;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminHTTPProxy.Details;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminNATDevice;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminNetworkInterface;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminNetworkInterfaceAddress;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminNode;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminPropertyChangeListener;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminProtocol;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminRouteListener;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminRoutesListener;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSocksProxy;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTestScheduler;
/*      */ import com.aelitis.azureus.core.networkmanager.impl.http.HTTPNetworkManager;
/*      */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPNetworkManager;
/*      */ import com.aelitis.azureus.core.networkmanager.impl.udp.UDPNetworkManager;
/*      */ import com.aelitis.azureus.core.proxy.socks.AESocksProxy;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.util.NetUtils;
/*      */ import com.aelitis.azureus.plugins.upnp.UPnPPlugin;
/*      */ import java.io.IOException;
/*      */ import java.net.Inet4Address;
/*      */ import java.net.Inet6Address;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.NetworkInterface;
/*      */ import java.net.Proxy;
/*      */ import java.net.ServerSocket;
/*      */ import java.net.Socket;
/*      */ import java.net.UnknownHostException;
/*      */ import java.nio.channels.ServerSocketChannel;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Random;
/*      */ import java.util.Set;
/*      */ import java.util.regex.Pattern;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AddressUtils;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.platform.PlatformManager;
/*      */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.platform.PlatformManagerException;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ 
/*      */ public class NetworkAdminImpl extends NetworkAdmin implements org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator
/*      */ {
/*   80 */   private static final org.gudy.azureus2.core3.logging.LogIDs LOGID = org.gudy.azureus2.core3.logging.LogIDs.NWMAN;
/*      */   private static final boolean FULL_INTF_PROBE = false;
/*      */   private static InetAddress anyLocalAddress;
/*      */   private static InetAddress anyLocalAddressIPv4;
/*      */   private static InetAddress anyLocalAddressIPv6;
/*      */   private static InetAddress localhostV4;
/*      */   private static InetAddress localhostV6;
/*      */   private static final int INTERFACE_CHECK_MILLIS = 15000;
/*      */   private static final int ROUTE_CHECK_MILLIS = 60000;
/*      */   private static final int ROUTE_CHECK_TICKS = 4;
/*      */   private Set<NetworkInterface> old_network_interfaces;
/*      */   
/*      */   static {
/*      */     try {
/*   94 */       anyLocalAddressIPv4 = InetAddress.getByAddress(new byte[] { 0, 0, 0, 0 });
/*   95 */       anyLocalAddressIPv6 = InetAddress.getByAddress(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
/*   96 */       anyLocalAddress = new InetSocketAddress(0).getAddress();
/*   97 */       localhostV4 = InetAddress.getByAddress(new byte[] { Byte.MAX_VALUE, 0, 0, 1 });
/*   98 */       localhostV6 = InetAddress.getByAddress(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 });
/*      */     }
/*      */     catch (UnknownHostException e) {
/*  101 */       e.printStackTrace();
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
/*  112 */   private final Map<String, AddressHistoryRecord> address_history = new HashMap();
/*      */   
/*      */   private long address_history_update_time;
/*  115 */   private InetAddress[] currentBindIPs = { null };
/*  116 */   private boolean forceBind = false;
/*  117 */   private boolean supportsIPv6withNIO = true;
/*  118 */   private boolean supportsIPv6 = true;
/*  119 */   private boolean supportsIPv4 = true;
/*      */   private boolean IPv6_enabled;
/*      */   private int roundRobinCounterV4;
/*      */   
/*      */   public NetworkAdminImpl() {
/*  124 */     COConfigurationManager.addAndFireParameterListener("IPV6 Enable Support", new org.gudy.azureus2.core3.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*      */ 
/*  132 */         NetworkAdminImpl.this.setIPv6Enabled(COConfigurationManager.getBooleanParameter("IPV6 Enable Support"));
/*      */       }
/*      */       
/*  135 */     });
/*  136 */     COConfigurationManager.addResetToDefaultsListener(new org.gudy.azureus2.core3.config.COConfigurationManager.ResetToDefaultsListener()
/*      */     {
/*      */ 
/*      */       public void reset()
/*      */       {
/*      */ 
/*  142 */         NetworkAdminImpl.this.clearMaybeVPNs();
/*      */       }
/*      */       
/*      */ 
/*  146 */     });
/*  147 */     this.roundRobinCounterV4 = 0;
/*  148 */     this.roundRobinCounterV6 = 0;
/*      */     
/*      */ 
/*      */ 
/*  152 */     this.listeners = new CopyOnWriteList();
/*      */     
/*      */ 
/*  155 */     this.trace_route_listener = new NetworkAdminRouteListener()
/*      */     {
/*      */ 
/*  158 */       private int node_count = 0;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean foundNode(NetworkAdminNode node, int distance, int rtt)
/*      */       {
/*  166 */         this.node_count += 1;
/*      */         
/*  168 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public boolean timeout(int distance)
/*      */       {
/*  175 */         if ((distance == 3) && (this.node_count == 0))
/*      */         {
/*  177 */           return false;
/*      */         }
/*      */         
/*  180 */         return true;
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  187 */     };
/*  188 */     this.asn_ips_checked = new ArrayList(0);
/*      */     
/*  190 */     this.as_history = new ArrayList();
/*      */     
/*  192 */     this.async_asn_dispacher = new AsyncDispatcher();
/*      */     
/*      */ 
/*  195 */     this.async_asn_history = new LinkedHashMap(256, 0.75F, true)
/*      */     {
/*      */ 
/*      */ 
/*      */       protected boolean removeEldestEntry(Map.Entry<InetAddress, NetworkAdminASN> eldest)
/*      */       {
/*      */ 
/*  202 */         return size() > 256;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  336 */     };
/*  337 */     this.getni_lock = new Object();
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
/* 2676 */     this.bs_last_calc = 0L;
/* 2677 */     this.bs_last_value = null;COConfigurationManager.addParameterListener(new String[] { "Bind IP", "Enforce Bind IP" }, new org.gudy.azureus2.core3.config.ParameterListener()
/*      */     {
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*  219 */         NetworkAdminImpl.this.checkDefaultBindAddress(false);
/*      */       }
/*      */       
/*  222 */     });
/*  223 */     org.gudy.azureus2.core3.util.SimpleTimer.addPeriodicEvent("NetworkAdmin:checker", 15000L, new org.gudy.azureus2.core3.util.TimerEventPerformer()
/*      */     {
/*      */       private int tick_count;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void perform(org.gudy.azureus2.core3.util.TimerEvent event)
/*      */       {
/*  234 */         this.tick_count += 1;
/*      */         
/*  236 */         boolean changed = NetworkAdminImpl.this.checkNetworkInterfaces(false, false);
/*      */         
/*  238 */         if ((changed) || (this.tick_count % 4 == 0))
/*      */         {
/*      */ 
/*  241 */           NetworkAdminImpl.this.checkConnectionRoutes();
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/*  247 */     });
/*  248 */     checkNetworkInterfaces(true, true);
/*      */     
/*  250 */     checkDefaultBindAddress(true);
/*      */     
/*  252 */     org.gudy.azureus2.core3.util.AEDiagnostics.addEvidenceGenerator(this);
/*      */     
/*  254 */     if (System.getProperty("skip.dns.spi.test", "0").equals("0")) {
/*  255 */       checkDNSSPI();
/*      */     }
/*      */     
/*  258 */     AzureusCoreFactory.addCoreRunningListener(new com.aelitis.azureus.core.AzureusCoreRunningListener()
/*      */     {
/*      */ 
/*      */       public void azureusCoreRunning(AzureusCore core)
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/*  266 */           Class.forName("com.aelitis.azureus.core.networkmanager.admin.impl.swt.NetworkAdminSWTImpl").getConstructor(new Class[] { AzureusCore.class, NetworkAdminImpl.class }).newInstance(new Object[] { core, NetworkAdminImpl.this });
/*      */ 
/*      */ 
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/*  274 */     });
/*  275 */     this.initialised = true; }
/*      */   
/*      */   private int roundRobinCounterV6;
/*      */   private boolean logged_bind_force_issue;
/*      */   private final CopyOnWriteList listeners;
/*      */   
/*  281 */   private void checkDNSSPI() { String error_str = null;
/*      */     try
/*      */     {
/*  284 */       InetAddress ia = InetAddress.getByName("dns.test.client.vuze.com");
/*      */       
/*  286 */       if (!ia.isLoopbackAddress())
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  292 */         error_str = "Loopback address expected, got " + ia;
/*      */       }
/*      */     }
/*      */     catch (UnknownHostException e) {
/*  296 */       error_str = "DNS SPI not loaded";
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  300 */       error_str = "Test lookup failed: " + Debug.getNestedExceptionMessage(e);
/*      */     }
/*      */     
/*  303 */     if (error_str != null)
/*      */     {
/*  305 */       Logger.log(new LogAlert(true, 1, MessageText.getString("network.admin.dns.spi.fail", new String[] { error_str })));
/*      */     }
/*      */   }
/*      */   
/*      */   final NetworkAdminRouteListener trace_route_listener;
/*      */   private static final int ASN_MIN_CHECK = 1800000;
/*      */   private long last_asn_lookup_time;
/*      */   private final List asn_ips_checked;
/*      */   private final List as_history;
/*      */   
/*      */   protected void setIPv6Enabled(boolean enabled)
/*      */   {
/*  317 */     this.IPv6_enabled = enabled;
/*      */     
/*  319 */     this.supportsIPv6withNIO = enabled;
/*  320 */     this.supportsIPv6 = enabled;
/*      */     
/*  322 */     if (this.initialised)
/*      */     {
/*  324 */       checkNetworkInterfaces(false, true);
/*      */       
/*  326 */       checkDefaultBindAddress(false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isIPV6Enabled()
/*      */   {
/*  333 */     return this.IPv6_enabled;
/*      */   }
/*      */   
/*      */   private final AsyncDispatcher async_asn_dispacher;
/*      */   private static final int MAX_ASYNC_ASN_LOOKUPS = 1024;
/*      */   final Map<InetAddress, NetworkAdminASN> async_asn_history;
/*      */   private final boolean initialised;
/*      */   private List<NetworkInterface> last_getni_result;
/*      */   private final Object getni_lock;
/*      */   protected boolean checkNetworkInterfaces(boolean first_time, boolean force)
/*      */   {
/*  344 */     boolean changed = false;
/*      */     try
/*      */     {
/*  347 */       List<NetworkInterface> x = NetUtils.getNetworkInterfaces();
/*      */       
/*  349 */       boolean fire_stuff = false;
/*      */       
/*  351 */       synchronized (this.getni_lock)
/*      */       {
/*  353 */         if (this.last_getni_result != x)
/*      */         {
/*  355 */           this.last_getni_result = x;
/*      */           
/*  357 */           if ((x.size() != 0) || (this.old_network_interfaces != null))
/*      */           {
/*  359 */             if (x.size() == 0)
/*      */             {
/*  361 */               this.old_network_interfaces = null;
/*      */               
/*  363 */               changed = true;
/*      */             }
/*  365 */             else if (this.old_network_interfaces == null)
/*      */             {
/*  367 */               Set<NetworkInterface> new_network_interfaces = new HashSet();
/*      */               
/*  369 */               new_network_interfaces.addAll(x);
/*      */               
/*  371 */               this.old_network_interfaces = new_network_interfaces;
/*      */               
/*  373 */               changed = true;
/*      */             }
/*      */             else
/*      */             {
/*  377 */               Set<NetworkInterface> new_network_interfaces = new HashSet();
/*      */               
/*  379 */               for (NetworkInterface ni : x)
/*      */               {
/*      */ 
/*      */ 
/*  383 */                 if (!this.old_network_interfaces.contains(ni))
/*      */                 {
/*  385 */                   changed = true;
/*      */                 }
/*      */                 
/*  388 */                 new_network_interfaces.add(ni);
/*      */               }
/*      */               
/*  391 */               if (this.old_network_interfaces.size() != new_network_interfaces.size())
/*      */               {
/*  393 */                 changed = true;
/*      */               }
/*      */               
/*  396 */               this.old_network_interfaces = new_network_interfaces;
/*      */             }
/*      */           }
/*  399 */           if ((changed) || (force))
/*      */           {
/*  401 */             boolean newV6 = false;
/*  402 */             boolean newV4 = false;
/*      */             
/*  404 */             Set<NetworkInterface> interfaces = this.old_network_interfaces;
/*      */             
/*  406 */             long now = SystemTime.getMonotonousTime();
/*      */             
/*  408 */             List<AddressHistoryRecord> a_history = new ArrayList();
/*      */             
/*  410 */             if (interfaces != null)
/*      */             {
/*  412 */               Iterator<NetworkInterface> it = interfaces.iterator();
/*  413 */               while (it.hasNext())
/*      */               {
/*  415 */                 NetworkInterface ni = (NetworkInterface)it.next();
/*  416 */                 Enumeration addresses = ni.getInetAddresses();
/*  417 */                 while (addresses.hasMoreElements())
/*      */                 {
/*  419 */                   InetAddress ia = (InetAddress)addresses.nextElement();
/*      */                   
/*  421 */                   a_history.add(new AddressHistoryRecord(ni, ia, now, null));
/*      */                   
/*  423 */                   if (!ia.isLoopbackAddress())
/*      */                   {
/*      */ 
/*  426 */                     if (((ia instanceof Inet6Address)) && (!ia.isLinkLocalAddress())) {
/*  427 */                       if (this.IPv6_enabled) {
/*  428 */                         newV6 = true;
/*      */                       }
/*  430 */                     } else if ((ia instanceof Inet4Address)) {
/*  431 */                       newV4 = true;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*  438 */             synchronized (this.address_history)
/*      */             {
/*  440 */               this.address_history_update_time = now;
/*      */               
/*  442 */               for (AddressHistoryRecord entry : a_history)
/*      */               {
/*  444 */                 String name = entry.getAddress().getHostAddress();
/*      */                 
/*  446 */                 AddressHistoryRecord existing = (AddressHistoryRecord)this.address_history.get(name);
/*      */                 
/*  448 */                 if (existing == null)
/*      */                 {
/*  450 */                   this.address_history.put(name, entry);
/*      */                 }
/*      */                 else
/*      */                 {
/*  454 */                   existing.setLastSeen(now);
/*      */                 }
/*      */               }
/*      */               
/*  458 */               Iterator<AddressHistoryRecord> it = this.address_history.values().iterator();
/*      */               
/*  460 */               while (it.hasNext())
/*      */               {
/*  462 */                 AddressHistoryRecord entry = (AddressHistoryRecord)it.next();
/*      */                 
/*  464 */                 long age = now - entry.getLastSeen();
/*      */                 
/*  466 */                 if (age > 600000L)
/*      */                 {
/*  468 */                   it.remove();
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*  473 */             this.supportsIPv4 = newV4;
/*  474 */             this.supportsIPv6 = newV6;
/*      */             
/*  476 */             Logger.log(new LogEvent(LOGID, "NetworkAdmin: ipv4 supported: " + this.supportsIPv4 + "; ipv6: " + this.supportsIPv6 + "; probing v6+nio functionality"));
/*      */             
/*  478 */             if (newV6)
/*      */             {
/*  480 */               ServerSocketChannel channel = ServerSocketChannel.open();
/*      */               
/*      */               try
/*      */               {
/*  484 */                 channel.configureBlocking(false);
/*  485 */                 channel.socket().bind(new InetSocketAddress(anyLocalAddressIPv6, 0));
/*  486 */                 Logger.log(new LogEvent(LOGID, "NetworkAdmin: testing nio + ipv6 bind successful"));
/*      */                 
/*  488 */                 this.supportsIPv6withNIO = true;
/*      */               }
/*      */               catch (Exception e) {
/*  491 */                 Logger.log(new LogEvent(LOGID, 1, "nio + ipv6 test failed", e));
/*  492 */                 this.supportsIPv6withNIO = false;
/*      */               }
/*      */               
/*  495 */               channel.close();
/*      */             } else {
/*  497 */               this.supportsIPv6withNIO = false;
/*      */             }
/*  499 */             if (!first_time)
/*      */             {
/*  501 */               Logger.log(new LogEvent(LOGID, "NetworkAdmin: network interfaces have changed"));
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*  506 */             fire_stuff = true;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  511 */       if (fire_stuff)
/*      */       {
/*  513 */         firePropertyChange("Network Interfaces");
/*      */         
/*  515 */         checkDefaultBindAddress(first_time);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*  520 */     return changed;
/*      */   }
/*      */   
/*      */   public InetAddress getMultiHomedOutgoingRoundRobinBindAddress(InetAddress target)
/*      */   {
/*  525 */     InetAddress[] addresses = this.currentBindIPs;
/*  526 */     boolean v6 = target instanceof Inet6Address;
/*  527 */     int previous = (v6 ? this.roundRobinCounterV6 : this.roundRobinCounterV4) % addresses.length;
/*  528 */     InetAddress toReturn = null;
/*      */     
/*  530 */     int i = previous;
/*      */     
/*      */     do
/*      */     {
/*  534 */       i++;i %= addresses.length;
/*  535 */       if ((target == null) || ((v6) && ((addresses[i] instanceof Inet6Address))) || ((!v6) && ((addresses[i] instanceof Inet4Address))))
/*      */       {
/*  537 */         toReturn = addresses[i];
/*  538 */         break; }
/*  539 */       if ((!v6) && (addresses[i].isAnyLocalAddress()))
/*      */       {
/*  541 */         toReturn = anyLocalAddressIPv4;
/*  542 */         break;
/*      */       }
/*  544 */     } while (i != previous);
/*      */     
/*  546 */     if (v6) {
/*  547 */       this.roundRobinCounterV6 = i;
/*      */     } else
/*  549 */       this.roundRobinCounterV4 = i;
/*  550 */     return v6 ? localhostV6 : toReturn != null ? toReturn : localhostV4;
/*      */   }
/*      */   
/*      */   public InetAddress[] getMultiHomedServiceBindAddresses(boolean nio)
/*      */   {
/*  555 */     InetAddress[] bindIPs = this.currentBindIPs;
/*  556 */     for (int i = 0; i < bindIPs.length; i++)
/*      */     {
/*  558 */       if (bindIPs[i].isAnyLocalAddress())
/*  559 */         return new InetAddress[] { (nio) && (!this.supportsIPv6withNIO) && ((bindIPs[i] instanceof Inet6Address)) ? anyLocalAddressIPv4 : bindIPs[i] };
/*      */     }
/*  561 */     return bindIPs;
/*      */   }
/*      */   
/*      */   public InetAddress getSingleHomedServiceBindAddress(int proto)
/*      */   {
/*  566 */     InetAddress[] addrs = this.currentBindIPs;
/*  567 */     if (proto == 0) {
/*  568 */       return addrs[0];
/*      */     }
/*  570 */     for (InetAddress addr : addrs)
/*      */     {
/*  572 */       if (((proto == 1) && ((addr instanceof Inet4Address))) || (addr.isAnyLocalAddress()) || ((proto == 2) && ((addr instanceof Inet6Address))))
/*      */       {
/*      */ 
/*  575 */         if (addr.isAnyLocalAddress())
/*      */         {
/*  577 */           if (proto == 1)
/*      */           {
/*  579 */             return anyLocalAddressIPv4;
/*      */           }
/*      */           
/*      */ 
/*  583 */           return anyLocalAddressIPv6;
/*      */         }
/*      */         
/*      */ 
/*  587 */         return addr;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  593 */     throw new java.nio.channels.UnsupportedAddressTypeException();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public InetAddress[] getAllBindAddresses(boolean include_wildcard)
/*      */   {
/*  600 */     if (include_wildcard)
/*      */     {
/*  602 */       return this.currentBindIPs;
/*      */     }
/*      */     
/*      */ 
/*  606 */     List<InetAddress> res = new ArrayList();
/*      */     
/*  608 */     InetAddress[] bind_ips = this.currentBindIPs;
/*      */     
/*  610 */     for (InetAddress ip : bind_ips)
/*      */     {
/*  612 */       if (!ip.isAnyLocalAddress())
/*      */       {
/*  614 */         res.add(ip);
/*      */       }
/*      */     }
/*      */     
/*  618 */     return (InetAddress[])res.toArray(new InetAddress[res.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public InetAddress[] resolveBindAddresses(String bind_to)
/*      */   {
/*  626 */     return calcBindAddresses(bind_to, false);
/*      */   }
/*      */   
/*      */   private InetAddress[] calcBindAddresses(String addressString, boolean enforceBind)
/*      */   {
/*  631 */     ArrayList<InetAddress> addrs = new ArrayList();
/*      */     
/*  633 */     Pattern addressSplitter = Pattern.compile(";");
/*  634 */     Pattern interfaceSplitter = Pattern.compile("[\\]\\[]");
/*      */     
/*  636 */     String[] tokens = addressSplitter.split(addressString);
/*      */     
/*      */ 
/*  639 */     for (int i = 0; i < tokens.length; i++)
/*      */     {
/*  641 */       String currentAddress = tokens[i];
/*      */       
/*  643 */       currentAddress = currentAddress.trim();
/*      */       
/*  645 */       if (currentAddress.length() != 0)
/*      */       {
/*      */ 
/*      */ 
/*  649 */         InetAddress parsedAddress = null;
/*      */         
/*      */         try
/*      */         {
/*  653 */           if ((currentAddress.indexOf('.') != -1) || (currentAddress.indexOf(':') != -1)) {
/*  654 */             parsedAddress = InetAddress.getByName(currentAddress);
/*      */           }
/*      */         }
/*      */         catch (Exception e) {}
/*      */         
/*  659 */         if (parsedAddress != null)
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/*  664 */             if (((!parsedAddress.isAnyLocalAddress()) || (addrs.size() > 0)) && (NetUtils.getByInetAddress(parsedAddress) == null)) {
/*      */               continue;
/*      */             }
/*      */           } catch (Throwable e) {
/*  668 */             Debug.printStackTrace(e);
/*  669 */             continue;
/*      */           }
/*  671 */           addrs.add(parsedAddress);
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  676 */           String[] ifaces = interfaceSplitter.split(currentAddress);
/*      */           
/*  678 */           NetworkInterface netInterface = null;
/*      */           try
/*      */           {
/*  681 */             netInterface = NetUtils.getByName(ifaces[0]);
/*      */           }
/*      */           catch (Throwable e) {
/*  684 */             e.printStackTrace();
/*      */           }
/*  686 */           if (netInterface != null)
/*      */           {
/*      */ 
/*  689 */             Enumeration interfaceAddresses = netInterface.getInetAddresses();
/*  690 */             if (ifaces.length != 2) {
/*  691 */               while (interfaceAddresses.hasMoreElements()) {
/*  692 */                 addrs.add((InetAddress)interfaceAddresses.nextElement());
/*      */               }
/*      */             }
/*  695 */             int selectedAddress = 0;
/*  696 */             try { selectedAddress = Integer.parseInt(ifaces[1]);
/*      */             } catch (NumberFormatException e) {}
/*  698 */             for (int j = 0; interfaceAddresses.hasMoreElements(); interfaceAddresses.nextElement()) {
/*  699 */               if (j == selectedAddress)
/*      */               {
/*  701 */                 addrs.add((InetAddress)interfaceAddresses.nextElement());
/*  702 */                 break;
/*      */               }
/*  698 */               j++;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  707 */     if (!this.IPv6_enabled)
/*      */     {
/*  709 */       Iterator<InetAddress> it = addrs.iterator();
/*      */       
/*  711 */       while (it.hasNext())
/*      */       {
/*  713 */         if ((it.next() instanceof Inet6Address))
/*      */         {
/*  715 */           it.remove();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  720 */     if (addrs.size() < 1) {
/*  721 */       return new InetAddress[] { hasIPV6Potential() ? anyLocalAddressIPv6 : enforceBind ? localhostV4 : anyLocalAddressIPv4 };
/*      */     }
/*      */     
/*  724 */     return (InetAddress[])addrs.toArray(new InetAddress[addrs.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private String checkBindAddresses(boolean log_alerts)
/*      */   {
/*  732 */     Pattern addressSplitter = Pattern.compile(";");
/*  733 */     Pattern interfaceSplitter = Pattern.compile("[\\]\\[]");
/*      */     
/*  735 */     String bind_ips = COConfigurationManager.getStringParameter("Bind IP", "").trim();
/*  736 */     boolean enforceBind = COConfigurationManager.getBooleanParameter("Enforce Bind IP");
/*      */     
/*  738 */     if ((enforceBind) && (bind_ips.length() == 0))
/*      */     {
/*  740 */       if (log_alerts)
/*      */       {
/*  742 */         Logger.log(new LogAlert(true, 1, MessageText.getString("network.admin.bind.enforce.fail")));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  750 */     String[] tokens = addressSplitter.split(bind_ips);
/*      */     
/*  752 */     String failed_entries = "";
/*      */     
/*  754 */     for (int i = 0; i < tokens.length; i++)
/*      */     {
/*  756 */       String currentAddress = tokens[i];
/*      */       
/*  758 */       currentAddress = currentAddress.trim();
/*      */       
/*  760 */       if (currentAddress.length() != 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  765 */         boolean ok = false;
/*      */         
/*  767 */         InetAddress parsedAddress = null;
/*      */         try
/*      */         {
/*  770 */           if ((currentAddress.indexOf('.') != -1) || (currentAddress.indexOf(':') != -1))
/*      */           {
/*  772 */             parsedAddress = InetAddress.getByName(currentAddress);
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/*  777 */         if (parsedAddress != null)
/*      */         {
/*      */           try {
/*  780 */             if ((parsedAddress.isAnyLocalAddress()) || (NetUtils.getByInetAddress(parsedAddress) != null))
/*      */             {
/*      */ 
/*  783 */               ok = true;
/*      */             }
/*      */             
/*      */ 
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */         else
/*      */         {
/*  792 */           String[] ifaces = interfaceSplitter.split(currentAddress);
/*      */           
/*  794 */           NetworkInterface netInterface = null;
/*      */           try
/*      */           {
/*  797 */             netInterface = NetUtils.getByName(ifaces[0]);
/*      */           }
/*      */           catch (Throwable e) {}
/*      */           
/*      */ 
/*  802 */           if (netInterface != null)
/*      */           {
/*  804 */             Enumeration interfaceAddresses = netInterface.getInetAddresses();
/*      */             
/*  806 */             if (ifaces.length != 2)
/*      */             {
/*  808 */               ok = interfaceAddresses.hasMoreElements();
/*      */             }
/*      */             else {
/*      */               try
/*      */               {
/*  813 */                 int selectedAddress = Integer.parseInt(ifaces[1]);
/*      */                 
/*  815 */                 for (int j = 0; interfaceAddresses.hasMoreElements(); interfaceAddresses.nextElement())
/*      */                 {
/*  817 */                   if (j == selectedAddress)
/*      */                   {
/*  819 */                     ok = true;
/*      */                     
/*  821 */                     break;
/*      */                   }
/*  815 */                   j++;
/*      */                 }
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  830 */         if (!ok)
/*      */         {
/*  832 */           failed_entries = failed_entries + (failed_entries.length() == 0 ? "" : ", ") + currentAddress;
/*      */         }
/*      */       }
/*      */     }
/*  836 */     if (failed_entries.length() > 0)
/*      */     {
/*  838 */       if (log_alerts)
/*      */       {
/*  840 */         Logger.log(new LogAlert(true, 1, "Bind IPs not resolved: " + failed_entries + "\n\nSee Tools->Options->Connection->Advanced Network Settings"));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  847 */       return failed_entries;
/*      */     }
/*      */     
/*  850 */     return null;
/*      */   }
/*      */   
/*      */   protected void checkDefaultBindAddress(boolean first_time)
/*      */   {
/*  855 */     boolean changed = false;
/*  856 */     String bind_ip = COConfigurationManager.getStringParameter("Bind IP", "").trim();
/*      */     
/*  858 */     boolean enforceBind = COConfigurationManager.getBooleanParameter("Enforce Bind IP");
/*      */     
/*  860 */     if (enforceBind)
/*      */     {
/*  862 */       if (bind_ip.length() == 0)
/*      */       {
/*  864 */         if (!this.logged_bind_force_issue)
/*      */         {
/*  866 */           this.logged_bind_force_issue = true;
/*      */           
/*  868 */           Debug.out("'Enforce IP Bindings' is selected but no bindings have been specified - ignoring force request!");
/*      */         }
/*      */         
/*  871 */         enforceBind = false;
/*      */       }
/*      */       else
/*      */       {
/*  875 */         this.logged_bind_force_issue = false;
/*      */       }
/*      */     }
/*      */     
/*  879 */     this.forceBind = enforceBind;
/*      */     
/*  881 */     InetAddress[] addrs = calcBindAddresses(bind_ip, enforceBind);
/*  882 */     changed = !java.util.Arrays.equals(this.currentBindIPs, addrs);
/*  883 */     if (changed) {
/*  884 */       this.currentBindIPs = addrs;
/*  885 */       if (!first_time)
/*      */       {
/*  887 */         String logmsg = "NetworkAdmin: default bind ip has changed to '";
/*  888 */         for (int i = 0; i < addrs.length; i++)
/*  889 */           logmsg = logmsg + (addrs[i] == null ? "none" : addrs[i].getHostAddress()) + (i < addrs.length ? ";" : "");
/*  890 */         logmsg = logmsg + "'";
/*  891 */         Logger.log(new LogEvent(LOGID, logmsg));
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  896 */         if (bind_ip.length() == 0)
/*      */         {
/*  898 */           clearMaybeVPNs();
/*      */         }
/*      */       }
/*  901 */       firePropertyChange("Default Bind IP");
/*      */     }
/*      */   }
/*      */   
/*      */   public String getNetworkInterfacesAsString()
/*      */   {
/*  907 */     Set interfaces = this.old_network_interfaces;
/*      */     
/*  909 */     if (interfaces == null)
/*      */     {
/*  911 */       return "";
/*      */     }
/*      */     
/*  914 */     Iterator it = interfaces.iterator();
/*  915 */     StringBuilder sb = new StringBuilder(1024);
/*  916 */     while (it.hasNext())
/*      */     {
/*  918 */       NetworkInterface ni = (NetworkInterface)it.next();
/*  919 */       Enumeration addresses = ni.getInetAddresses();
/*  920 */       sb.append(ni.getName());
/*  921 */       sb.append("\t(");
/*  922 */       sb.append(ni.getDisplayName());
/*  923 */       sb.append(")\n");
/*  924 */       int i = 0;
/*  925 */       while (addresses.hasMoreElements()) {
/*  926 */         InetAddress address = (InetAddress)addresses.nextElement();
/*  927 */         sb.append("\t");
/*  928 */         sb.append(ni.getName());
/*  929 */         sb.append("[");
/*  930 */         sb.append(i++);
/*  931 */         sb.append("]\t");
/*  932 */         sb.append(address.getHostAddress());
/*  933 */         sb.append("\n");
/*      */       }
/*      */     }
/*  936 */     return sb.toString();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasIPV4Potential()
/*      */   {
/*  942 */     return this.supportsIPv4;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasIPV6Potential(boolean nio)
/*      */   {
/*  948 */     return nio ? this.supportsIPv6withNIO : this.supportsIPv6;
/*      */   }
/*      */   
/*      */ 
/*      */   public InetAddress[] getBindableAddresses()
/*      */   {
/*  954 */     return getBindableAddresses(false, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private InetAddress[] getBindableAddresses(boolean ignore_loopback, boolean ignore_link_local)
/*      */   {
/*  962 */     List<InetAddress> bindable = new ArrayList();
/*      */     
/*  964 */     NetworkAdminNetworkInterface[] interfaces = NetworkAdmin.getSingleton().getInterfaces();
/*      */     
/*  966 */     for (NetworkAdminNetworkInterface intf : interfaces)
/*      */     {
/*  968 */       NetworkAdminNetworkInterfaceAddress[] addresses = intf.getAddresses();
/*      */       
/*  970 */       for (NetworkAdminNetworkInterfaceAddress address : addresses)
/*      */       {
/*  972 */         InetAddress a = address.getAddress();
/*      */         
/*  974 */         if (((!ignore_loopback) || (!a.isLoopbackAddress())) && ((!ignore_link_local) || (!a.isLinkLocalAddress())))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  979 */           if (canBind(a))
/*      */           {
/*  981 */             bindable.add(a);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  987 */     return (InetAddress[])bindable.toArray(new InetAddress[bindable.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean canBind(InetAddress bind_ip)
/*      */   {
/*  994 */     ServerSocketChannel ssc = null;
/*      */     try
/*      */     {
/*  997 */       ssc = ServerSocketChannel.open();
/*      */       
/*  999 */       ssc.socket().bind(new InetSocketAddress(bind_ip, 0), 16);
/*      */       
/* 1001 */       return true;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1005 */       return 0;
/*      */     }
/*      */     finally
/*      */     {
/* 1009 */       if (ssc != null) {
/*      */         try
/*      */         {
/* 1012 */           ssc.close();
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1016 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getBindablePort(int prefer_port)
/*      */     throws IOException
/*      */   {
/* 1028 */     int tries = 1024;
/*      */     
/* 1030 */     Random random = new Random();
/*      */     
/* 1032 */     for (int i = 1; i <= 1024; i++)
/*      */     {
/*      */       int port;
/*      */       int port;
/* 1036 */       if ((i == 1) && (prefer_port != 0))
/*      */       {
/* 1038 */         port = prefer_port;
/*      */       }
/*      */       else
/*      */       {
/* 1042 */         port = i == 1024 ? 0 : random.nextInt(20000) + 40000;
/*      */       }
/*      */       
/* 1045 */       ServerSocketChannel ssc = null;
/*      */       try
/*      */       {
/* 1048 */         ssc = ServerSocketChannel.open();
/*      */         
/* 1050 */         ssc.socket().setReuseAddress(true);
/*      */         
/* 1052 */         bind(ssc, null, port);
/*      */         
/* 1054 */         port = ssc.socket().getLocalPort();
/*      */         
/* 1056 */         ssc.close();
/*      */         
/* 1058 */         return port;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1062 */         if (ssc != null)
/*      */         {
/*      */           try {
/* 1065 */             ssc.close();
/*      */           }
/*      */           catch (Throwable f)
/*      */           {
/* 1069 */             Debug.printStackTrace(e);
/*      */           }
/*      */           
/* 1072 */           ssc = null;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1077 */     throw new IOException("No bindable ports found");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void bind(ServerSocketChannel ssc, InetAddress address, int port)
/*      */     throws IOException
/*      */   {
/* 1088 */     if (address == null)
/*      */     {
/* 1090 */       ssc.socket().bind(new InetSocketAddress(port), 1024);
/*      */     }
/*      */     else
/*      */     {
/* 1094 */       ssc.socket().bind(new InetSocketAddress(address, port), 1024);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public InetAddress guessRoutableBindAddress()
/*      */   {
/*      */     try
/*      */     {
/* 1104 */       List local_addresses = new ArrayList();
/* 1105 */       List non_local_addresses = new ArrayList();
/*      */       try
/*      */       {
/* 1108 */         NetworkAdminNetworkInterface[] interfaces = getInterfaces();
/*      */         
/* 1110 */         List possible = new ArrayList();
/*      */         
/* 1112 */         for (int i = 0; i < interfaces.length; i++)
/*      */         {
/* 1114 */           NetworkAdminNetworkInterface intf = interfaces[i];
/*      */           
/* 1116 */           NetworkAdminNetworkInterfaceAddress[] addresses = intf.getAddresses();
/*      */           
/* 1118 */           for (int j = 0; j < addresses.length; j++)
/*      */           {
/* 1120 */             NetworkAdminNetworkInterfaceAddress address = addresses[j];
/*      */             
/* 1122 */             InetAddress ia = address.getAddress();
/*      */             
/* 1124 */             if (!ia.isLoopbackAddress())
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 1129 */               if ((ia.isLinkLocalAddress()) || (ia.isSiteLocalAddress()))
/*      */               {
/* 1131 */                 local_addresses.add(ia);
/*      */               }
/*      */               else
/*      */               {
/* 1135 */                 non_local_addresses.add(ia);
/*      */               }
/*      */               
/* 1138 */               if (((hasIPV4Potential()) && ((ia instanceof Inet4Address))) || ((hasIPV6Potential()) && ((ia instanceof Inet6Address))))
/*      */               {
/*      */ 
/* 1141 */                 possible.add(ia);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 1146 */         if (possible.size() == 1)
/*      */         {
/* 1148 */           return (InetAddress)possible.get(0);
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/*      */       try
/*      */       {
/* 1156 */         NetworkAdminSocksProxy[] socks = getSocksProxies();
/*      */         
/* 1158 */         if (socks.length > 0)
/*      */         {
/* 1160 */           return mapAddressToBindIP(InetAddress.getByName(socks[0].getHost()));
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/*      */       try
/*      */       {
/* 1168 */         NetworkAdminNATDevice[] nat = getNATDevices(AzureusCoreFactory.getSingleton());
/*      */         
/* 1170 */         if (nat.length > 0)
/*      */         {
/* 1172 */           return mapAddressToBindIP(nat[0].getAddress());
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       try
/*      */       {
/* 1178 */         final AESemaphore sem = new AESemaphore("NA:conTest");
/* 1179 */         final InetAddress[] can_connect = { null };
/*      */         
/* 1181 */         int timeout = 10000;
/*      */         
/* 1183 */         for (int i = 0; i < local_addresses.size(); i++)
/*      */         {
/* 1185 */           final InetAddress address = (InetAddress)local_addresses.get(i);
/*      */           
/* 1187 */           new AEThread2("NA:conTest", true)
/*      */           {
/*      */ 
/*      */             public void run()
/*      */             {
/* 1192 */               if (NetworkAdminImpl.this.canConnectWithBind(address, 10000))
/*      */               {
/* 1194 */                 can_connect[0] = address;
/*      */                 
/* 1196 */                 sem.release();
/*      */               }
/*      */             }
/*      */           }.start();
/*      */         }
/*      */         
/* 1202 */         if (sem.reserve(10000L))
/*      */         {
/* 1204 */           return can_connect[0];
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1212 */       if (non_local_addresses.size() > 0)
/*      */       {
/* 1214 */         return guessAddress(non_local_addresses);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1219 */       if (local_addresses.size() > 0)
/*      */       {
/* 1221 */         return guessAddress(local_addresses);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1226 */       return null;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1230 */       Debug.printStackTrace(e);
/*      */     }
/* 1232 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean canConnectWithBind(InetAddress bind_address, int timeout)
/*      */   {
/* 1241 */     Socket socket = null;
/*      */     try
/*      */     {
/* 1244 */       socket = new Socket();
/*      */       
/* 1246 */       socket.bind(new InetSocketAddress(bind_address, 0));
/*      */       
/* 1248 */       socket.setSoTimeout(timeout);
/*      */       
/* 1250 */       socket.connect(new InetSocketAddress("www.google.com", 80), timeout);
/*      */       
/* 1252 */       return true;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1256 */       return 0;
/*      */     }
/*      */     finally
/*      */     {
/* 1260 */       if (socket != null) {
/*      */         try
/*      */         {
/* 1263 */           socket.close();
/*      */         }
/*      */         catch (Throwable f) {}
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected InetAddress mapAddressToBindIP(InetAddress address)
/*      */   {
/* 1275 */     boolean[] address_bits = bytesToBits(address.getAddress());
/*      */     
/* 1277 */     NetworkAdminNetworkInterface[] interfaces = getInterfaces();
/*      */     
/* 1279 */     InetAddress best_bind_address = null;
/* 1280 */     int best_prefix = 0;
/*      */     
/* 1282 */     for (int i = 0; i < interfaces.length; i++)
/*      */     {
/* 1284 */       NetworkAdminNetworkInterface intf = interfaces[i];
/*      */       
/* 1286 */       NetworkAdminNetworkInterfaceAddress[] addresses = intf.getAddresses();
/*      */       
/* 1288 */       for (int j = 0; j < addresses.length; j++)
/*      */       {
/* 1290 */         NetworkAdminNetworkInterfaceAddress bind_address = addresses[j];
/*      */         
/* 1292 */         InetAddress ba = bind_address.getAddress();
/*      */         
/* 1294 */         byte[] bind_bytes = ba.getAddress();
/*      */         
/* 1296 */         if (address_bits.length == bind_bytes.length)
/*      */         {
/* 1298 */           boolean[] bind_bits = bytesToBits(bind_bytes);
/*      */           
/* 1300 */           for (int k = 0; k < bind_bits.length; k++)
/*      */           {
/* 1302 */             if (address_bits[k] != bind_bits[k]) {
/*      */               break;
/*      */             }
/*      */             
/*      */ 
/* 1307 */             if (k > best_prefix)
/*      */             {
/* 1309 */               best_prefix = k;
/*      */               
/* 1311 */               best_bind_address = ba;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1318 */     return best_bind_address;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean[] bytesToBits(byte[] bytes)
/*      */   {
/* 1325 */     boolean[] res = new boolean[bytes.length * 8];
/*      */     
/* 1327 */     for (int i = 0; i < bytes.length; i++)
/*      */     {
/* 1329 */       byte b = bytes[i];
/*      */       
/* 1331 */       for (int j = 0; j < 8; j++)
/*      */       {
/* 1333 */         res[(i * 8 + j)] = ((b & (byte)(1 << 7 - j)) != 0 ? 1 : false);
/*      */       }
/*      */     }
/*      */     
/* 1337 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected InetAddress guessAddress(List addresses)
/*      */   {
/* 1347 */     for (int i = 0; i < addresses.size(); i++)
/*      */     {
/* 1349 */       InetAddress address = (InetAddress)addresses.get(i);
/*      */       
/* 1351 */       String str = address.getHostAddress();
/*      */       
/* 1353 */       if ((str.startsWith("192.168.0.")) || (str.startsWith("192.168.1.")))
/*      */       {
/* 1355 */         return address;
/*      */       }
/*      */     }
/*      */     
/* 1359 */     for (int i = 0; i < addresses.size(); i++)
/*      */     {
/* 1361 */       InetAddress address = (InetAddress)addresses.get(i);
/*      */       
/* 1363 */       if ((address instanceof Inet4Address))
/*      */       {
/* 1365 */         return address;
/*      */       }
/*      */     }
/*      */     
/* 1369 */     for (int i = 0; i < addresses.size(); i++)
/*      */     {
/* 1371 */       InetAddress address = (InetAddress)addresses.get(i);
/*      */       
/* 1373 */       if ((address instanceof Inet6Address))
/*      */       {
/* 1375 */         return address;
/*      */       }
/*      */     }
/*      */     
/* 1379 */     if (addresses.size() > 0)
/*      */     {
/* 1381 */       return (InetAddress)addresses.get(0);
/*      */     }
/*      */     
/* 1384 */     return null;
/*      */   }
/*      */   
/* 1387 */   static final InetAddress[] gdpa_lock = { null };
/*      */   private static AESemaphore gdpa_sem;
/*      */   private static long gdpa_last_fail;
/*      */   private static long gdpa_last_lookup;
/* 1391 */   static final AESemaphore gdpa_initial_sem = new AESemaphore("gdpa:init");
/*      */   public static final int BS_INACTIVE = 0;
/*      */   public static final int BS_OK = 1;
/*      */   public static final int BS_WARNING = 2;
/*      */   
/* 1396 */   public InetAddress getDefaultPublicAddress() { return getDefaultPublicAddress(false); }
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
/*      */   public static final int BS_ERROR = 3;
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
/*      */   private long bs_last_calc;
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
/*      */   private Object[] bs_last_value;
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
/*      */   public InetAddress getDefaultPublicAddressV6()
/*      */   {
/* 1512 */     return getDefaultPublicAddressV6(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public InetAddress getDefaultPublicAddressV6(boolean peek)
/*      */   {
/* 1520 */     if (!this.supportsIPv6) {
/* 1521 */       return null;
/*      */     }
/*      */     
/* 1524 */     for (InetAddress addr : this.currentBindIPs)
/*      */     {
/*      */ 
/* 1527 */       if (AddressUtils.isGlobalAddressV6(addr)) {
/* 1528 */         return addr;
/*      */       }
/*      */     }
/*      */     
/* 1532 */     for (InetAddress addr : this.currentBindIPs)
/*      */     {
/* 1534 */       if (((addr instanceof Inet6Address)) && (addr.isAnyLocalAddress()))
/*      */       {
/* 1536 */         ArrayList<InetAddress> addrs = new ArrayList();
/* 1537 */         for (NetworkInterface iface : this.old_network_interfaces) {
/* 1538 */           addrs.addAll(java.util.Collections.list(iface.getInetAddresses()));
/*      */         }
/* 1540 */         return AddressUtils.pickBestGlobalV6Address(addrs);
/*      */       }
/*      */     }
/*      */     
/* 1544 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasDHTIPV6()
/*      */   {
/* 1550 */     if (hasIPV6Potential(false))
/*      */     {
/* 1552 */       InetAddress v6 = getDefaultPublicAddressV6();
/*      */       
/* 1554 */       if (v6 == null)
/*      */       {
/* 1556 */         return false;
/*      */       }
/*      */       
/* 1559 */       if (org.gudy.azureus2.core3.util.Constants.IS_CVS_VERSION)
/*      */       {
/* 1561 */         return true;
/*      */       }
/*      */       
/*      */ 
/* 1565 */       return !AddressUtils.isTeredo(v6);
/*      */     }
/*      */     
/*      */ 
/* 1569 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void firePropertyChange(String property)
/*      */   {
/* 1576 */     Iterator it = this.listeners.iterator();
/*      */     
/* 1578 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/* 1581 */         ((NetworkAdminPropertyChangeListener)it.next()).propertyChanged(property);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1585 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public NetworkAdminNetworkInterface[] getInterfaces()
/*      */   {
/* 1593 */     Set interfaces = this.old_network_interfaces;
/*      */     
/* 1595 */     if (interfaces == null)
/*      */     {
/* 1597 */       return new NetworkAdminNetworkInterface[0];
/*      */     }
/*      */     
/* 1600 */     NetworkAdminNetworkInterface[] res = new NetworkAdminNetworkInterface[interfaces.size()];
/*      */     
/* 1602 */     Iterator it = interfaces.iterator();
/*      */     
/* 1604 */     int pos = 0;
/*      */     
/* 1606 */     while (it.hasNext())
/*      */     {
/* 1608 */       NetworkInterface ni = (NetworkInterface)it.next();
/*      */       
/* 1610 */       res[(pos++)] = new networkInterface(ni);
/*      */     }
/*      */     
/* 1613 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public NetworkAdminProtocol[] getOutboundProtocols(AzureusCore azureus_core)
/*      */   {
/* 1620 */     NetworkAdminProtocol[] res = { new NetworkAdminProtocolImpl(azureus_core, 1), new NetworkAdminProtocolImpl(azureus_core, 2), new NetworkAdminProtocolImpl(azureus_core, 3) };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1627 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public NetworkAdminProtocol createInboundProtocol(AzureusCore azureus_core, int type, int port)
/*      */   {
/* 1636 */     return new NetworkAdminProtocolImpl(azureus_core, type, port);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public NetworkAdminProtocol[] getInboundProtocols(AzureusCore azureus_core)
/*      */   {
/* 1647 */     List protocols = new ArrayList();
/*      */     
/* 1649 */     TCPNetworkManager tcp_manager = TCPNetworkManager.getSingleton();
/*      */     
/* 1651 */     if (tcp_manager.isTCPListenerEnabled())
/*      */     {
/* 1653 */       protocols.add(new NetworkAdminProtocolImpl(azureus_core, 2, tcp_manager.getTCPListeningPortNumber()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1660 */     UDPNetworkManager udp_manager = UDPNetworkManager.getSingleton();
/*      */     
/* 1662 */     int done_udp = -1;
/*      */     
/* 1664 */     if (udp_manager.isUDPListenerEnabled())
/*      */     {
/* 1666 */       protocols.add(new NetworkAdminProtocolImpl(azureus_core, 3, done_udp = udp_manager.getUDPListeningPortNumber()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1673 */     if (udp_manager.isUDPNonDataListenerEnabled())
/*      */     {
/* 1675 */       int port = udp_manager.getUDPNonDataListeningPortNumber();
/*      */       
/* 1677 */       if (port != done_udp)
/*      */       {
/* 1679 */         protocols.add(new NetworkAdminProtocolImpl(azureus_core, 3, done_udp = udp_manager.getUDPNonDataListeningPortNumber()));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1688 */     HTTPNetworkManager http_manager = HTTPNetworkManager.getSingleton();
/*      */     
/* 1690 */     if (http_manager.isHTTPListenerEnabled())
/*      */     {
/* 1692 */       protocols.add(new NetworkAdminProtocolImpl(azureus_core, 1, http_manager.getHTTPListeningPortNumber()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1699 */     return (NetworkAdminProtocol[])protocols.toArray(new NetworkAdminProtocol[protocols.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public InetAddress testProtocol(NetworkAdminProtocol protocol)
/*      */     throws NetworkAdminException
/*      */   {
/* 1708 */     return protocol.test(null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isSocksActive()
/*      */   {
/* 1715 */     Proxy proxy = com.aelitis.azureus.core.proxy.AEProxySelectorFactory.getSelector().getActiveProxy();
/*      */     
/* 1717 */     return (proxy != null) && (proxy.type() == java.net.Proxy.Type.SOCKS);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public NetworkAdminSocksProxy createSocksProxy(String host, int port, String username, String password)
/*      */   {
/* 1727 */     return new NetworkAdminSocksProxyImpl(host, "" + port, username, password);
/*      */   }
/*      */   
/*      */ 
/*      */   public NetworkAdminSocksProxy[] getSocksProxies()
/*      */   {
/* 1733 */     String host = System.getProperty("socksProxyHost", "").trim();
/* 1734 */     String port = System.getProperty("socksProxyPort", "").trim();
/*      */     
/* 1736 */     String user = System.getProperty("java.net.socks.username", "").trim();
/* 1737 */     String password = System.getProperty("java.net.socks.password", "").trim();
/*      */     
/* 1739 */     List res = new ArrayList();
/*      */     
/* 1741 */     NetworkAdminSocksProxyImpl p1 = new NetworkAdminSocksProxyImpl(host, port, user, password);
/*      */     
/* 1743 */     if (p1.isConfigured())
/*      */     {
/* 1745 */       res.add(p1);
/*      */     }
/*      */     
/* 1748 */     if ((COConfigurationManager.getBooleanParameter("Proxy.Data.Enable")) && (!COConfigurationManager.getBooleanParameter("Proxy.Data.Same")))
/*      */     {
/*      */ 
/* 1751 */       host = COConfigurationManager.getStringParameter("Proxy.Data.Host");
/* 1752 */       port = COConfigurationManager.getStringParameter("Proxy.Data.Port");
/* 1753 */       user = COConfigurationManager.getStringParameter("Proxy.Data.Username");
/*      */       
/* 1755 */       if (user.trim().equalsIgnoreCase("<none>")) {
/* 1756 */         user = "";
/*      */       }
/* 1758 */       password = COConfigurationManager.getStringParameter("Proxy.Data.Password");
/*      */       
/* 1760 */       NetworkAdminSocksProxyImpl p2 = new NetworkAdminSocksProxyImpl(host, port, user, password);
/*      */       
/* 1762 */       if (p2.isConfigured())
/*      */       {
/* 1764 */         res.add(p2);
/*      */       }
/*      */     }
/*      */     
/* 1768 */     return (NetworkAdminSocksProxy[])res.toArray(new NetworkAdminSocksProxy[res.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */   public NetworkAdminHTTPProxy getHTTPProxy()
/*      */   {
/* 1774 */     NetworkAdminHTTPProxyImpl res = new NetworkAdminHTTPProxyImpl();
/*      */     
/* 1776 */     if (!res.isConfigured())
/*      */     {
/* 1778 */       res = null;
/*      */     }
/*      */     
/* 1781 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public NetworkAdminNATDevice[] getNATDevices(AzureusCore azureus_core)
/*      */   {
/* 1788 */     List<NetworkAdminNATDeviceImpl> devices = new ArrayList();
/*      */     
/*      */     try
/*      */     {
/* 1792 */       PluginInterface upnp_pi = azureus_core.getPluginManager().getPluginInterfaceByClass(UPnPPlugin.class);
/*      */       
/* 1794 */       if (upnp_pi != null)
/*      */       {
/* 1796 */         UPnPPlugin upnp = (UPnPPlugin)upnp_pi.getPlugin();
/*      */         
/* 1798 */         com.aelitis.azureus.plugins.upnp.UPnPPluginService[] services = upnp.getServices();
/*      */         
/* 1800 */         for (com.aelitis.azureus.plugins.upnp.UPnPPluginService service : services)
/*      */         {
/* 1802 */           NetworkAdminNATDeviceImpl dev = new NetworkAdminNATDeviceImpl(service);
/*      */           
/* 1804 */           boolean same = false;
/*      */           
/* 1806 */           for (NetworkAdminNATDeviceImpl d : devices)
/*      */           {
/* 1808 */             if (d.sameAs(dev))
/*      */             {
/* 1810 */               same = true;
/*      */               
/* 1812 */               break;
/*      */             }
/*      */           }
/*      */           
/* 1816 */           if (!same)
/*      */           {
/* 1818 */             devices.add(dev);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1824 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/* 1827 */     return (NetworkAdminNATDevice[])devices.toArray(new NetworkAdminNATDevice[devices.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */   public NetworkAdminASN getCurrentASN()
/*      */   {
/* 1833 */     List asns = COConfigurationManager.getListParameter("ASN Details", new ArrayList());
/*      */     
/* 1835 */     if (asns.size() == 0)
/*      */     {
/*      */ 
/*      */ 
/* 1839 */       String as = "";
/* 1840 */       String asn = "";
/* 1841 */       String bgp = "";
/*      */       try
/*      */       {
/* 1844 */         as = COConfigurationManager.getStringParameter("ASN AS");
/* 1845 */         asn = COConfigurationManager.getStringParameter("ASN ASN");
/* 1846 */         bgp = COConfigurationManager.getStringParameter("ASN BGP");
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1850 */         Debug.printStackTrace(e);
/*      */       }
/*      */       
/* 1853 */       COConfigurationManager.removeParameter("ASN AS");
/* 1854 */       COConfigurationManager.removeParameter("ASN ASN");
/* 1855 */       COConfigurationManager.removeParameter("ASN BGP");
/* 1856 */       COConfigurationManager.removeParameter("ASN Autocheck Performed Time");
/*      */       
/* 1858 */       asns.add(ASNToMap(new NetworkAdminASNImpl(as, asn, bgp)));
/*      */       
/* 1860 */       COConfigurationManager.setParameter("ASN Details", asns);
/*      */     }
/*      */     
/* 1863 */     if (asns.size() > 0)
/*      */     {
/* 1865 */       Map m = (Map)asns.get(0);
/*      */       
/* 1867 */       return ASNFromMap(m);
/*      */     }
/*      */     
/* 1870 */     return new NetworkAdminASNImpl("", "", "");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected Map ASNToMap(NetworkAdminASNImpl x)
/*      */   {
/* 1877 */     Map m = new HashMap();
/*      */     
/* 1879 */     byte[] as = new byte[0];
/* 1880 */     byte[] asn = new byte[0];
/* 1881 */     byte[] bgp = new byte[0];
/*      */     try
/*      */     {
/* 1884 */       as = x.getAS().getBytes("UTF-8");
/* 1885 */       asn = x.getASName().getBytes("UTF-8");
/* 1886 */       bgp = x.getBGPPrefix().getBytes("UTF-8");
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1890 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/* 1893 */     m.put("as", as);
/* 1894 */     m.put("name", asn);
/* 1895 */     m.put("bgp", bgp);
/*      */     
/* 1897 */     return m;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected NetworkAdminASNImpl ASNFromMap(Map m)
/*      */   {
/* 1904 */     String as = "";
/* 1905 */     String asn = "";
/* 1906 */     String bgp = "";
/*      */     try
/*      */     {
/* 1909 */       as = new String((byte[])m.get("as"), "UTF-8");
/* 1910 */       asn = new String((byte[])m.get("name"), "UTF-8");
/* 1911 */       bgp = new String((byte[])m.get("bgp"), "UTF-8");
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1915 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/* 1918 */     return new NetworkAdminASNImpl(as, asn, bgp);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public NetworkAdminASN lookupCurrentASN(InetAddress address)
/*      */     throws NetworkAdminException
/*      */   {
/* 1927 */     NetworkAdminASN current = getCurrentASN();
/*      */     
/* 1929 */     if (current.matchesCIDR(address))
/*      */     {
/* 1931 */       return current;
/*      */     }
/*      */     
/* 1934 */     List asns = COConfigurationManager.getListParameter("ASN Details", new ArrayList());
/*      */     
/* 1936 */     for (int i = 0; i < asns.size(); i++)
/*      */     {
/* 1938 */       Map m = (Map)asns.get(i);
/*      */       
/* 1940 */       NetworkAdminASN x = ASNFromMap(m);
/*      */       
/* 1942 */       if (x.matchesCIDR(address))
/*      */       {
/* 1944 */         asns.remove(i);
/*      */         
/* 1946 */         asns.add(0, m);
/*      */         
/* 1948 */         firePropertyChange("AS");
/*      */         
/* 1950 */         return x;
/*      */       }
/*      */     }
/*      */     
/* 1954 */     if (this.asn_ips_checked.contains(address))
/*      */     {
/* 1956 */       return current;
/*      */     }
/*      */     
/* 1959 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 1961 */     if ((now < this.last_asn_lookup_time) || (now - this.last_asn_lookup_time > 1800000L))
/*      */     {
/* 1963 */       this.last_asn_lookup_time = now;
/*      */       
/* 1965 */       NetworkAdminASNLookupImpl lookup = new NetworkAdminASNLookupImpl(address);
/*      */       
/* 1967 */       NetworkAdminASNImpl x = lookup.lookup();
/*      */       
/* 1969 */       this.asn_ips_checked.add(address);
/*      */       
/* 1971 */       asns.add(0, ASNToMap(x));
/*      */       
/* 1973 */       firePropertyChange("AS");
/*      */       
/* 1975 */       return x;
/*      */     }
/*      */     
/* 1978 */     return current;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public NetworkAdminASN lookupASN(InetAddress address)
/*      */     throws NetworkAdminException
/*      */   {
/* 1987 */     NetworkAdminASN existing = getFromASHistory(address);
/*      */     
/* 1989 */     if (existing != null)
/*      */     {
/* 1991 */       return existing;
/*      */     }
/*      */     
/* 1994 */     NetworkAdminASNLookupImpl lookup = new NetworkAdminASNLookupImpl(address);
/*      */     
/* 1996 */     NetworkAdminASNImpl result = lookup.lookup();
/*      */     
/* 1998 */     addToASHistory(result);
/*      */     
/* 2000 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void lookupASN(final InetAddress address, final NetworkAdminASNListener listener)
/*      */   {
/* 2008 */     synchronized (this.async_asn_history)
/*      */     {
/* 2010 */       NetworkAdminASN existing = (NetworkAdminASN)this.async_asn_history.get(address);
/*      */       
/* 2012 */       if (existing != null)
/*      */       {
/* 2014 */         listener.success(existing);
/*      */       }
/*      */     }
/*      */     
/* 2018 */     int queue_size = this.async_asn_dispacher.getQueueSize();
/*      */     
/* 2020 */     if (queue_size >= 1024)
/*      */     {
/* 2022 */       listener.failed(new NetworkAdminException("Too many outstanding lookups"));
/*      */     }
/*      */     else
/*      */     {
/* 2026 */       this.async_asn_dispacher.dispatch(new org.gudy.azureus2.core3.util.AERunnable()
/*      */       {
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/* 2032 */           synchronized (NetworkAdminImpl.this.async_asn_history)
/*      */           {
/* 2034 */             NetworkAdminASN existing = (NetworkAdminASN)NetworkAdminImpl.this.async_asn_history.get(address);
/*      */             
/* 2036 */             if (existing != null)
/*      */             {
/* 2038 */               listener.success(existing);
/*      */               
/* 2040 */               return;
/*      */             }
/*      */           }
/*      */           try
/*      */           {
/* 2045 */             NetworkAdminASNLookupImpl lookup = new NetworkAdminASNLookupImpl(address);
/*      */             
/* 2047 */             NetworkAdminASNImpl result = lookup.lookup();
/*      */             
/* 2049 */             synchronized (NetworkAdminImpl.this.async_asn_history)
/*      */             {
/* 2051 */               NetworkAdminImpl.this.async_asn_history.put(address, result);
/*      */             }
/*      */             
/* 2054 */             listener.success(result);
/*      */           }
/*      */           catch (NetworkAdminException e)
/*      */           {
/* 2058 */             listener.failed(e);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2062 */             listener.failed(new NetworkAdminException("lookup failed", e));
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void addToASHistory(NetworkAdminASN asn)
/*      */   {
/* 2073 */     synchronized (this.as_history)
/*      */     {
/* 2075 */       boolean found = false;
/*      */       
/* 2077 */       for (int i = 0; i < this.as_history.size(); i++)
/*      */       {
/* 2079 */         NetworkAdminASN x = (NetworkAdminASN)this.as_history.get(i);
/*      */         
/* 2081 */         if (asn.getAS().equals(x.getAS()))
/*      */         {
/* 2083 */           found = true;
/*      */           
/* 2085 */           break;
/*      */         }
/*      */       }
/*      */       
/* 2089 */       if (!found)
/*      */       {
/* 2091 */         this.as_history.add(asn);
/*      */         
/* 2093 */         if (this.as_history.size() > 256)
/*      */         {
/* 2095 */           this.as_history.remove(0);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected NetworkAdminASN getFromASHistory(InetAddress address)
/*      */   {
/* 2105 */     synchronized (this.as_history)
/*      */     {
/* 2107 */       for (int i = 0; i < this.as_history.size(); i++)
/*      */       {
/* 2109 */         NetworkAdminASN x = (NetworkAdminASN)this.as_history.get(i);
/*      */         
/* 2111 */         if (x.matchesCIDR(address))
/*      */         {
/* 2113 */           return x;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2118 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void runInitialChecks(AzureusCore azureus_core)
/*      */   {
/* 2125 */     AZInstanceManager i_man = azureus_core.getInstanceManager();
/*      */     
/* 2127 */     final AZInstance my_instance = i_man.getMyInstance();
/*      */     
/* 2129 */     i_man.addListener(new com.aelitis.azureus.core.instancemanager.AZInstanceManagerListener()
/*      */     {
/*      */       private InetAddress external_address;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void instanceFound(AZInstance instance) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void instanceChanged(AZInstance instance)
/*      */       {
/* 2144 */         if (instance == my_instance)
/*      */         {
/* 2146 */           InetAddress address = instance.getExternalAddress();
/*      */           
/* 2148 */           if ((this.external_address == null) || (!this.external_address.equals(address)))
/*      */           {
/* 2150 */             this.external_address = address;
/*      */             try
/*      */             {
/* 2153 */               NetworkAdminImpl.this.lookupCurrentASN(address);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 2157 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void instanceLost(AZInstance instance) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void instanceTracked(AZInstanceTracked instance) {}
/*      */     });
/*      */     
/*      */ 
/*      */ 
/* 2176 */     if (COConfigurationManager.getBooleanParameter("Proxy.Check.On.Start"))
/*      */     {
/* 2178 */       NetworkAdminSocksProxy[] socks = getSocksProxies();
/*      */       
/* 2180 */       for (int i = 0; i < socks.length; i++)
/*      */       {
/* 2182 */         NetworkAdminSocksProxy sock = socks[i];
/*      */         try
/*      */         {
/* 2185 */           sock.getVersionsSupported();
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 2189 */           Debug.printStackTrace(e);
/*      */           
/* 2191 */           Logger.log(new LogAlert(true, 1, "Socks proxy " + sock.getName() + " check failed: " + Debug.getNestedExceptionMessage(e)));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2199 */       NetworkAdminHTTPProxy http_proxy = getHTTPProxy();
/*      */       
/* 2201 */       if (http_proxy != null)
/*      */       {
/*      */         try
/*      */         {
/* 2205 */           http_proxy.getDetails();
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 2209 */           Debug.printStackTrace(e);
/*      */           
/* 2211 */           Logger.log(new LogAlert(true, 1, "HTTP proxy " + http_proxy.getName() + " check failed: " + Debug.getNestedExceptionMessage(e)));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2220 */     if (COConfigurationManager.getBooleanParameter("Check Bind IP On Start"))
/*      */     {
/* 2222 */       checkBindAddresses(true);
/*      */     }
/*      */     
/* 2225 */     NetworkAdminSpeedTestScheduler nast = NetworkAdminSpeedTestSchedulerImpl.getInstance();
/*      */     
/* 2227 */     nast.initialise();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canTraceRoute()
/*      */   {
/* 2233 */     PlatformManager pm = PlatformManagerFactory.getPlatformManager();
/*      */     
/* 2235 */     return pm.hasCapability(org.gudy.azureus2.platform.PlatformManagerCapabilities.TraceRouteAvailability);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public NetworkAdminNode[] getRoute(InetAddress interface_address, InetAddress target, final int max_millis, final NetworkAdminRouteListener listener)
/*      */     throws NetworkAdminException
/*      */   {
/* 2247 */     PlatformManager pm = PlatformManagerFactory.getPlatformManager();
/*      */     
/* 2249 */     if (!canTraceRoute())
/*      */     {
/* 2251 */       throw new NetworkAdminException("No trace-route capability on platform");
/*      */     }
/*      */     
/* 2254 */     final List nodes = new ArrayList();
/*      */     try
/*      */     {
/* 2257 */       pm.traceRoute(interface_address, target, new org.gudy.azureus2.platform.PlatformManagerPingCallback()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 2262 */         private long start_time = SystemTime.getCurrentTime();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public boolean reportNode(int distance, InetAddress address, int millis)
/*      */         {
/* 2270 */           boolean timeout = false;
/*      */           
/* 2272 */           if (max_millis >= 0)
/*      */           {
/* 2274 */             long now = SystemTime.getCurrentTime();
/*      */             
/* 2276 */             if (now < this.start_time)
/*      */             {
/* 2278 */               this.start_time = now;
/*      */             }
/*      */             
/* 2281 */             if (now - this.start_time >= max_millis)
/*      */             {
/* 2283 */               timeout = true;
/*      */             }
/*      */           }
/*      */           
/* 2287 */           NetworkAdminNode node = null;
/*      */           
/* 2289 */           if (address != null)
/*      */           {
/* 2291 */             node = new NetworkAdminImpl.networkNode(address, distance, millis);
/*      */             
/* 2293 */             nodes.add(node);
/*      */           }
/*      */           
/*      */           boolean result;
/*      */           boolean result;
/* 2298 */           if (listener == null)
/*      */           {
/* 2300 */             result = true;
/*      */           }
/*      */           else {
/*      */             boolean result;
/* 2304 */             if (node == null)
/*      */             {
/* 2306 */               result = listener.timeout(distance);
/*      */             }
/*      */             else
/*      */             {
/* 2310 */               result = listener.foundNode(node, distance, millis);
/*      */             }
/*      */           }
/*      */           
/* 2314 */           return (result) && (!timeout);
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (PlatformManagerException e) {
/* 2319 */       throw new NetworkAdminException("trace-route failed", e);
/*      */     }
/*      */     
/* 2322 */     return (NetworkAdminNode[])nodes.toArray(new NetworkAdminNode[nodes.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canPing()
/*      */   {
/* 2328 */     PlatformManager pm = PlatformManagerFactory.getPlatformManager();
/*      */     
/* 2330 */     return pm.hasCapability(org.gudy.azureus2.platform.PlatformManagerCapabilities.PingAvailability);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public NetworkAdminNode pingTarget(InetAddress interface_address, InetAddress target, final int max_millis, final NetworkAdminRouteListener listener)
/*      */     throws NetworkAdminException
/*      */   {
/* 2342 */     PlatformManager pm = PlatformManagerFactory.getPlatformManager();
/*      */     
/* 2344 */     if (!canPing())
/*      */     {
/* 2346 */       throw new NetworkAdminException("No ping capability on platform");
/*      */     }
/*      */     
/* 2349 */     final NetworkAdminNode[] nodes = { null };
/*      */     try
/*      */     {
/* 2352 */       pm.ping(interface_address, target, new org.gudy.azureus2.platform.PlatformManagerPingCallback()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 2357 */         private long start_time = SystemTime.getCurrentTime();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public boolean reportNode(int distance, InetAddress address, int millis)
/*      */         {
/* 2365 */           boolean timeout = false;
/*      */           
/* 2367 */           if (max_millis >= 0)
/*      */           {
/* 2369 */             long now = SystemTime.getCurrentTime();
/*      */             
/* 2371 */             if (now < this.start_time)
/*      */             {
/* 2373 */               this.start_time = now;
/*      */             }
/*      */             
/* 2376 */             if (now - this.start_time >= max_millis)
/*      */             {
/* 2378 */               timeout = true;
/*      */             }
/*      */           }
/*      */           
/* 2382 */           NetworkAdminNode node = null;
/*      */           
/* 2384 */           if (address != null)
/*      */           {
/* 2386 */             node = new NetworkAdminImpl.networkNode(address, distance, millis);
/*      */             
/* 2388 */             nodes[0] = node;
/*      */           }
/*      */           
/*      */           boolean result;
/*      */           boolean result;
/* 2393 */           if (listener == null)
/*      */           {
/* 2395 */             result = false;
/*      */           }
/*      */           else {
/*      */             boolean result;
/* 2399 */             if (node == null)
/*      */             {
/* 2401 */               result = listener.timeout(distance);
/*      */             }
/*      */             else
/*      */             {
/* 2405 */               result = listener.foundNode(node, distance, millis);
/*      */             }
/*      */           }
/*      */           
/* 2409 */           return (result) && (!timeout);
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (PlatformManagerException e) {
/* 2414 */       throw new NetworkAdminException("ping failed", e);
/*      */     }
/*      */     
/* 2417 */     return nodes[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void getRoutes(final InetAddress target, int max_millis, final NetworkAdminRoutesListener listener)
/*      */     throws NetworkAdminException
/*      */   {
/* 2429 */     List sems = new ArrayList();
/* 2430 */     List traces = new ArrayList();
/*      */     
/* 2432 */     NetworkAdminNetworkInterface[] interfaces = getInterfaces();
/*      */     
/* 2434 */     for (int i = 0; i < interfaces.length; i++)
/*      */     {
/* 2436 */       NetworkAdminNetworkInterface interf = interfaces[i];
/*      */       
/* 2438 */       NetworkAdminNetworkInterfaceAddress[] addresses = interf.getAddresses();
/*      */       
/* 2440 */       for (int j = 0; j < addresses.length; j++)
/*      */       {
/* 2442 */         final NetworkAdminNetworkInterfaceAddress address = addresses[j];
/*      */         
/* 2444 */         InetAddress ia = address.getAddress();
/*      */         
/* 2446 */         if ((!ia.isLoopbackAddress()) && (!(ia instanceof Inet6Address)))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2452 */           final AESemaphore sem = new AESemaphore("parallelRouter");
/*      */           
/* 2454 */           final List trace = new ArrayList();
/*      */           
/* 2456 */           sems.add(sem);
/*      */           
/* 2458 */           traces.add(trace);
/*      */           
/* 2460 */           new AEThread2("parallelRouter", true)
/*      */           {
/*      */             public void run()
/*      */             {
/*      */               try
/*      */               {
/* 2466 */                 address.getRoute(target, 30000, new NetworkAdminRouteListener()
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                   public boolean foundNode(NetworkAdminNode node, int distance, int rtt)
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/* 2477 */                     NetworkAdminImpl.14.this.val$trace.add(node);
/*      */                     
/* 2479 */                     NetworkAdminNode[] route = new NetworkAdminNode[NetworkAdminImpl.14.this.val$trace.size()];
/*      */                     
/* 2481 */                     NetworkAdminImpl.14.this.val$trace.toArray(route);
/*      */                     
/* 2483 */                     return NetworkAdminImpl.14.this.val$listener.foundNode(NetworkAdminImpl.14.this.val$address, route, distance, rtt);
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */                   public boolean timeout(int distance)
/*      */                   {
/* 2490 */                     NetworkAdminNode[] route = new NetworkAdminNode[NetworkAdminImpl.14.this.val$trace.size()];
/*      */                     
/* 2492 */                     NetworkAdminImpl.14.this.val$trace.toArray(route);
/*      */                     
/* 2494 */                     return NetworkAdminImpl.14.this.val$listener.timeout(NetworkAdminImpl.14.this.val$address, route, distance);
/*      */                   }
/*      */                 });
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 2500 */                 e.printStackTrace();
/*      */               }
/*      */               finally
/*      */               {
/* 2504 */                 sem.release();
/*      */               }
/*      */             }
/*      */           }.start();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2512 */     for (int i = 0; i < sems.size(); i++)
/*      */     {
/* 2514 */       ((AESemaphore)sems.get(i)).reserve();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void pingTargets(final InetAddress target, int max_millis, final NetworkAdminRoutesListener listener)
/*      */     throws NetworkAdminException
/*      */   {
/* 2526 */     List sems = new ArrayList();
/* 2527 */     List traces = new ArrayList();
/*      */     
/* 2529 */     NetworkAdminNetworkInterface[] interfaces = getInterfaces();
/*      */     
/* 2531 */     for (int i = 0; i < interfaces.length; i++)
/*      */     {
/* 2533 */       NetworkAdminNetworkInterface interf = interfaces[i];
/*      */       
/* 2535 */       NetworkAdminNetworkInterfaceAddress[] addresses = interf.getAddresses();
/*      */       
/* 2537 */       for (int j = 0; j < addresses.length; j++)
/*      */       {
/* 2539 */         final NetworkAdminNetworkInterfaceAddress address = addresses[j];
/*      */         
/* 2541 */         InetAddress ia = address.getAddress();
/*      */         
/* 2543 */         if ((!ia.isLoopbackAddress()) && (!(ia instanceof Inet6Address)))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2549 */           final AESemaphore sem = new AESemaphore("parallelPinger");
/*      */           
/* 2551 */           final List trace = new ArrayList();
/*      */           
/* 2553 */           sems.add(sem);
/*      */           
/* 2555 */           traces.add(trace);
/*      */           
/* 2557 */           new AEThread2("parallelPinger", true)
/*      */           {
/*      */             public void run()
/*      */             {
/*      */               try
/*      */               {
/* 2563 */                 address.pingTarget(target, 30000, new NetworkAdminRouteListener()
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                   public boolean foundNode(NetworkAdminNode node, int distance, int rtt)
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/* 2574 */                     NetworkAdminImpl.15.this.val$trace.add(node);
/*      */                     
/* 2576 */                     NetworkAdminNode[] route = new NetworkAdminNode[NetworkAdminImpl.15.this.val$trace.size()];
/*      */                     
/* 2578 */                     NetworkAdminImpl.15.this.val$trace.toArray(route);
/*      */                     
/* 2580 */                     return NetworkAdminImpl.15.this.val$listener.foundNode(NetworkAdminImpl.15.this.val$address, route, distance, rtt);
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */                   public boolean timeout(int distance)
/*      */                   {
/* 2587 */                     NetworkAdminNode[] route = new NetworkAdminNode[NetworkAdminImpl.15.this.val$trace.size()];
/*      */                     
/* 2589 */                     NetworkAdminImpl.15.this.val$trace.toArray(route);
/*      */                     
/* 2591 */                     return NetworkAdminImpl.15.this.val$listener.timeout(NetworkAdminImpl.15.this.val$address, route, distance);
/*      */                   }
/*      */                 });
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 2597 */                 e.printStackTrace();
/*      */               }
/*      */               finally
/*      */               {
/* 2601 */                 sem.release();
/*      */               }
/*      */             }
/*      */           }.start();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2609 */     for (int i = 0; i < sems.size(); i++)
/*      */     {
/* 2611 */       ((AESemaphore)sems.get(i)).reserve();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean mustBind()
/*      */   {
/* 2619 */     return this.forceBind;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasMissingForcedBind()
/*      */   {
/* 2625 */     Object[] status = getBindingStatus();
/*      */     
/* 2627 */     return ((Integer)status[0]).intValue() == 3;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getBindStatus()
/*      */   {
/* 2633 */     Object[] status = getBindingStatus();
/*      */     
/* 2635 */     int state = ((Integer)status[0]).intValue();
/*      */     
/* 2637 */     if (state == 0)
/*      */     {
/* 2639 */       return "No binding configured";
/*      */     }
/*      */     
/*      */ 
/* 2643 */     String str = "";
/*      */     
/* 2645 */     if (state == 1)
/*      */     {
/* 2647 */       str = "Binding OK";
/*      */     }
/* 2649 */     else if (state == 2)
/*      */     {
/* 2651 */       str = "Binding warning";
/*      */     }
/*      */     else
/*      */     {
/* 2655 */       str = "Binding error";
/*      */     }
/*      */     
/* 2658 */     String status_str = (String)status[1];
/*      */     
/* 2660 */     if (status_str.length() > 0)
/*      */     {
/* 2662 */       str = str + ": " + status_str;
/*      */     }
/*      */     
/* 2665 */     return str;
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
/*      */   public Object[] getBindingStatus()
/*      */   {
/* 2682 */     long now = SystemTime.getMonotonousTime();
/*      */     
/* 2684 */     if ((this.bs_last_value != null) && (now - this.bs_last_calc < 30000L))
/*      */     {
/* 2686 */       return this.bs_last_value;
/*      */     }
/*      */     
/* 2689 */     String bind_ips = COConfigurationManager.getStringParameter("Bind IP", "").trim();
/*      */     
/* 2691 */     if (bind_ips.length() == 0)
/*      */     {
/* 2693 */       return new Object[] { Integer.valueOf(0), "" };
/*      */     }
/*      */     
/* 2696 */     boolean enforceBind = COConfigurationManager.getBooleanParameter("Enforce Bind IP");
/*      */     
/* 2698 */     String missing = checkBindAddresses(false);
/*      */     
/* 2700 */     InetAddress[] binds = getAllBindAddresses(false);
/*      */     
/* 2702 */     List<InetAddress> bindable = new ArrayList();
/* 2703 */     List<InetAddress> unbindable = new ArrayList();
/*      */     
/* 2705 */     for (InetAddress b : binds)
/*      */     {
/* 2707 */       if (canBind(b))
/*      */       {
/* 2709 */         bindable.add(b);
/*      */ 
/*      */ 
/*      */       }
/* 2713 */       else if ((!b.isLoopbackAddress()) && (!b.isLinkLocalAddress()))
/*      */       {
/* 2715 */         unbindable.add(b);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2720 */     Set<NetworkConnectionBase> connections = NetworkManager.getSingleton().getConnections();
/*      */     
/* 2722 */     Map<InetAddress, int[]> lookup_map = new HashMap();
/*      */     
/* 2724 */     for (NetworkConnectionBase connection : connections)
/*      */     {
/* 2726 */       TransportBase tb = connection.getTransportBase();
/*      */       
/* 2728 */       if ((tb instanceof Transport))
/*      */       {
/* 2730 */         Transport transport = (Transport)tb;
/*      */         
/* 2732 */         TransportStartpoint start = transport.getTransportStartpoint();
/*      */         
/* 2734 */         if (start != null)
/*      */         {
/* 2736 */           InetSocketAddress socket_address = start.getProtocolStartpoint().getAddress();
/*      */           
/* 2738 */           if (socket_address != null)
/*      */           {
/* 2740 */             InetAddress address = socket_address.getAddress();
/*      */             
/* 2742 */             int[] counts = (int[])lookup_map.get(address);
/*      */             
/* 2744 */             if (counts == null)
/*      */             {
/* 2746 */               counts = new int[2];
/*      */               
/* 2748 */               lookup_map.put(address, counts);
/*      */             }
/*      */             
/* 2751 */             if (connection.isIncoming())
/*      */             {
/* 2753 */               counts[0] += 1;
/*      */             }
/*      */             else
/*      */             {
/* 2757 */               counts[1] += 1;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2764 */     int status = 1;
/*      */     
/* 2766 */     if ((unbindable.size() > 0) || (missing != null))
/*      */     {
/* 2768 */       status = enforceBind ? 3 : 2;
/*      */     }
/*      */     
/* 2771 */     String str = MessageText.getString("network.admin.binding.state", new String[] { getString(bindable), MessageText.getString(enforceBind ? "GeneralView.yes" : "GeneralView.no").toLowerCase() });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2776 */     if (unbindable.size() > 0) {
/* 2777 */       str = str + "\nUnbindable: " + getString(unbindable);
/*      */     }
/*      */     
/* 2780 */     if (missing != null) {
/* 2781 */       str = str + "\n" + MessageText.getString("label.missing") + ": " + missing;
/*      */     }
/*      */     
/* 2784 */     boolean unbound_connections = false;
/*      */     
/* 2786 */     if (lookup_map.size() == 0)
/*      */     {
/* 2788 */       str = str + "\n" + MessageText.getString("label.no.connections");
/*      */     }
/*      */     else
/*      */     {
/* 2792 */       String con_str = "";
/*      */       
/* 2794 */       for (Map.Entry<InetAddress, int[]> entry : lookup_map.entrySet())
/*      */       {
/* 2796 */         InetAddress address = (InetAddress)entry.getKey();
/* 2797 */         int[] counts = (int[])entry.getValue();
/*      */         
/*      */ 
/*      */ 
/* 2801 */         if (address.isAnyLocalAddress())
/*      */         {
/* 2803 */           String s = "*";
/*      */           
/* 2805 */           unbound_connections = true;
/*      */         }
/*      */         else
/*      */         {
/* 2809 */           s = address.getHostAddress();
/*      */           
/* 2811 */           if (!bindable.contains(address))
/*      */           {
/* 2813 */             unbound_connections = true;
/*      */           }
/*      */         }
/*      */         
/* 2817 */         String s = s + " - " + MessageText.getString("label.in") + "=" + counts[0] + ", " + MessageText.getString("label.out") + "=" + counts[1];
/*      */         
/* 2819 */         con_str = con_str + (con_str.length() == 0 ? "" : "; ") + s.toLowerCase();
/*      */       }
/*      */       
/* 2822 */       str = str + "\n" + MessageText.getString("label.connections") + ": " + con_str;
/*      */     }
/*      */     
/* 2825 */     if (unbound_connections)
/*      */     {
/* 2827 */       status = enforceBind ? 3 : 2;
/*      */     }
/*      */     
/* 2830 */     this.bs_last_value = new Object[] { Integer.valueOf(status), str };
/* 2831 */     this.bs_last_calc = now;
/*      */     
/* 2833 */     return this.bs_last_value;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String getString(List<InetAddress> addresses)
/*      */   {
/* 2840 */     if (addresses.size() == 0)
/*      */     {
/* 2842 */       return "<none>";
/*      */     }
/*      */     
/* 2845 */     String str = "";
/*      */     
/* 2847 */     for (InetAddress address : addresses)
/*      */     {
/* 2849 */       str = str + (str.length() == 0 ? "" : ", ") + address.getHostAddress();
/*      */     }
/*      */     
/* 2852 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkConnectionRoutes()
/*      */   {
/* 2860 */     if (getAllBindAddresses(false).length > 0)
/*      */     {
/*      */ 
/*      */ 
/* 2864 */       return;
/*      */     }
/*      */     
/* 2867 */     Set<NetworkConnectionBase> connections = NetworkManager.getSingleton().getConnections();
/*      */     
/*      */ 
/*      */ 
/* 2871 */     boolean found_wildcard = false;
/* 2872 */     int tcp_found = 0;
/*      */     
/* 2874 */     Map<InetAddress, Object[]> lookup_map = new HashMap();
/* 2875 */     Map<String, Object[]> bind_map = new HashMap();
/*      */     
/* 2877 */     for (NetworkConnectionBase connection : connections)
/*      */     {
/* 2879 */       if (!connection.isIncoming())
/*      */       {
/* 2881 */         TransportBase tb = connection.getTransportBase();
/*      */         
/* 2883 */         if ((tb instanceof Transport))
/*      */         {
/* 2885 */           Transport transport = (Transport)tb;
/*      */           
/* 2887 */           if (transport.isTCP())
/*      */           {
/* 2889 */             TransportStartpoint start = transport.getTransportStartpoint();
/*      */             
/* 2891 */             if (start != null)
/*      */             {
/* 2893 */               InetSocketAddress socket_address = start.getProtocolStartpoint().getAddress();
/*      */               
/* 2895 */               if (socket_address != null)
/*      */               {
/* 2897 */                 tcp_found++;
/*      */                 
/* 2899 */                 InetAddress address = socket_address.getAddress();
/*      */                 
/* 2901 */                 if (address.isAnyLocalAddress())
/*      */                 {
/* 2903 */                   found_wildcard = true;
/*      */                 }
/*      */                 else
/*      */                 {
/* 2907 */                   Object[] details = (Object[])lookup_map.get(address);
/*      */                   
/* 2909 */                   if (details == null)
/*      */                   {
/* 2911 */                     if (!lookup_map.containsKey(address))
/*      */                     {
/* 2913 */                       details = getInterfaceForAddress(address);
/*      */                       
/* 2915 */                       lookup_map.put(address, details);
/*      */                     }
/*      */                   }
/*      */                   
/* 2919 */                   if ((details != null) && ((details[0] instanceof NetworkInterface)))
/*      */                   {
/* 2921 */                     NetworkInterface intf = (NetworkInterface)details[0];
/*      */                     
/* 2923 */                     InetAddress intf_address = details.length == 1 ? null : (InetAddress)details[1];
/*      */                     
/* 2925 */                     String key = intf.getName() + "/" + intf_address;
/*      */                     
/* 2927 */                     Object[] entry = (Object[])bind_map.get(key);
/*      */                     
/* 2929 */                     if (entry == null)
/*      */                     {
/* 2931 */                       entry = new Object[] { new int[1], details };
/*      */                       
/* 2933 */                       bind_map.put(key, entry);
/*      */                     }
/*      */                     
/* 2936 */                     ((int[])entry[0])[0] += 1;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2946 */     if (tcp_found > 8)
/*      */     {
/* 2948 */       if ((found_wildcard) && (bind_map.size() == 0))
/*      */       {
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
/* 2960 */         InetAddress[] bindable_addresses = getBindableAddresses(true, true);
/*      */         
/*      */ 
/*      */ 
/* 2964 */         if (bindable_addresses.length > 1)
/*      */         {
/* 2966 */           Map<String, NetworkInterface> intf_map = new HashMap();
/*      */           
/* 2968 */           for (InetAddress address : bindable_addresses)
/*      */           {
/* 2970 */             Object[] details = getInterfaceForAddress(address);
/*      */             
/* 2972 */             if ((details != null) && ((details[0] instanceof NetworkInterface)))
/*      */             {
/* 2974 */               NetworkInterface intf = (NetworkInterface)details[0];
/*      */               
/* 2976 */               intf_map.put(intf.getName(), intf);
/*      */             }
/*      */           }
/*      */           
/* 2980 */           if (intf_map.size() > 1)
/*      */           {
/* 2982 */             int eth_like = 0;
/*      */             
/* 2984 */             Map<String, NetworkInterface> vpn_like = new HashMap();
/*      */             
/* 2986 */             for (Map.Entry<String, NetworkInterface> entry : intf_map.entrySet())
/*      */             {
/* 2988 */               int type = categoriseIntf((NetworkInterface)entry.getValue());
/*      */               
/* 2990 */               if (type == 1)
/*      */               {
/* 2992 */                 eth_like++;
/*      */               }
/* 2994 */               else if (type == 2)
/*      */               {
/* 2996 */                 vpn_like.put(entry.getKey(), entry.getValue());
/*      */               }
/*      */             }
/*      */             
/* 3000 */             if ((vpn_like.size() == 1) && (eth_like > 0))
/*      */             {
/* 3002 */               maybeVPN((NetworkInterface)vpn_like.values().iterator().next());
/*      */             }
/*      */           }
/*      */         }
/* 3006 */       } else if ((!found_wildcard) && (bind_map.size() == 1))
/*      */       {
/* 3008 */         Object[] bound_details = (Object[])((Object[])bind_map.values().iterator().next())[1];
/*      */         
/* 3010 */         NetworkInterface bound_intf = (NetworkInterface)bound_details[0];
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3016 */         int bound_type = categoriseIntf(bound_intf);
/*      */         
/* 3018 */         if (bound_type == 2)
/*      */         {
/* 3020 */           if (!maybeVPNDone(bound_intf))
/*      */           {
/* 3022 */             InetAddress[] bindable_addresses = getBindableAddresses(true, true);
/*      */             
/* 3024 */             if (bindable_addresses.length > 1)
/*      */             {
/* 3026 */               int eth_like = 0;
/* 3027 */               int vpn_like = 0;
/*      */               
/* 3029 */               for (InetAddress address : bindable_addresses)
/*      */               {
/* 3031 */                 Object[] details = getInterfaceForAddress(address);
/*      */                 
/* 3033 */                 if ((details != null) && ((details[0] instanceof NetworkInterface)))
/*      */                 {
/* 3035 */                   NetworkInterface intf = (NetworkInterface)details[0];
/*      */                   
/* 3037 */                   if (intf != bound_intf)
/*      */                   {
/* 3039 */                     int type = categoriseIntf(intf);
/*      */                     
/* 3041 */                     if (type == 1)
/*      */                     {
/* 3043 */                       eth_like++;
/*      */                     }
/* 3045 */                     else if (type == 2)
/*      */                     {
/* 3047 */                       vpn_like++;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/* 3053 */               if ((vpn_like == 0) && (eth_like > 0))
/*      */               {
/* 3055 */                 maybeVPN(bound_intf);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void clearMaybeVPNs()
/*      */   {
/* 3067 */     Set<String> keys = COConfigurationManager.getDefinedParameters();
/*      */     
/* 3069 */     for (String key : keys)
/*      */     {
/* 3071 */       if (key.startsWith("network.admin.maybe.vpn.done."))
/*      */       {
/* 3073 */         COConfigurationManager.removeParameter(key);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean maybeVPNDone(NetworkInterface intf)
/*      */   {
/* 3082 */     if (COConfigurationManager.getBooleanParameter("network.admin.maybe.vpn.enable"))
/*      */     {
/* 3084 */       return COConfigurationManager.getBooleanParameter("network.admin.maybe.vpn.done." + getConfigKey(intf), false);
/*      */     }
/*      */     
/*      */ 
/* 3088 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void maybeVPN(final NetworkInterface intf)
/*      */   {
/* 3096 */     final UIManager ui_manager = org.gudy.azureus2.plugins.utils.StaticUtilities.getUIManager(5000L);
/*      */     
/* 3098 */     if (ui_manager == null)
/*      */     {
/* 3100 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 3105 */     if (maybeVPNDone(intf))
/*      */     {
/* 3107 */       return;
/*      */     }
/*      */     
/* 3110 */     COConfigurationManager.setParameter("network.admin.maybe.vpn.done." + getConfigKey(intf), true);
/*      */     
/* 3112 */     new AEThread2("NetworkAdmin:vpn?")
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/* 3117 */         String msg_details = MessageText.getString("network.admin.maybe.vpn.msg", new String[] { intf.getName() + " - " + intf.getDisplayName() });
/*      */         
/*      */ 
/*      */ 
/* 3121 */         long res = ui_manager.showMessageBox("network.admin.maybe.vpn.title", "!" + msg_details + "!", 36L);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 3126 */         if (res == 4L)
/*      */         {
/* 3128 */           COConfigurationManager.setParameter("User Mode", 2);
/* 3129 */           COConfigurationManager.setParameter("Bind IP", intf.getName());
/* 3130 */           COConfigurationManager.setParameter("Enforce Bind IP", true);
/* 3131 */           COConfigurationManager.setParameter("Check Bind IP On Start", true);
/* 3132 */           COConfigurationManager.save();
/*      */           try
/*      */           {
/* 3135 */             Set<NetworkConnectionBase> connections = NetworkManager.getSingleton().getConnections();
/*      */             
/* 3137 */             lookup_map = new HashMap();
/*      */             
/* 3139 */             for (NetworkConnectionBase connection : connections)
/*      */             {
/* 3141 */               TransportBase tb = connection.getTransportBase();
/*      */               
/* 3143 */               if ((tb instanceof Transport))
/*      */               {
/* 3145 */                 boolean ok = false;
/*      */                 
/* 3147 */                 Transport transport = (Transport)tb;
/*      */                 
/* 3149 */                 if (transport.isTCP())
/*      */                 {
/* 3151 */                   TransportStartpoint start = transport.getTransportStartpoint();
/*      */                   
/* 3153 */                   if (start != null)
/*      */                   {
/* 3155 */                     InetSocketAddress socket_address = start.getProtocolStartpoint().getAddress();
/*      */                     
/* 3157 */                     if (socket_address != null)
/*      */                     {
/* 3159 */                       InetAddress address = socket_address.getAddress();
/*      */                       
/* 3161 */                       Object[] details = (Object[])lookup_map.get(address);
/*      */                       
/* 3163 */                       if (details == null)
/*      */                       {
/* 3165 */                         if (!lookup_map.containsKey(address))
/*      */                         {
/* 3167 */                           details = NetworkAdminImpl.this.getInterfaceForAddress(address);
/*      */                           
/* 3169 */                           lookup_map.put(address, details);
/*      */                         }
/*      */                         
/* 3172 */                         if (details[0] == intf)
/*      */                         {
/* 3174 */                           ok = true;
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 
/* 3181 */                 if (!ok)
/*      */                 {
/* 3183 */                   transport.close("Explicit bind IP set, disconnecting incompatible connections");
/*      */                 }
/*      */               }
/*      */             }
/*      */           } catch (Throwable e) {
/*      */             Map<InetAddress, Object[]> lookup_map;
/* 3189 */             Debug.out(e);
/*      */           }
/*      */           
/* 3192 */           NetworkAdminImpl.this.bs_last_calc = 0L;
/*      */           
/* 3194 */           ui_manager.showMessageBox("settings.updated.title", "settings.updated.msg", 1L);
/*      */         }
/*      */       }
/*      */     }.start();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String getConfigKey(NetworkInterface intf)
/*      */   {
/*      */     try
/*      */     {
/* 3208 */       return org.gudy.azureus2.core3.util.Base32.encode(intf.getName().getBytes("UTF-8"));
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 3212 */       Debug.out(e);
/*      */     }
/* 3214 */     return "derp";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private int categoriseIntf(NetworkInterface intf)
/*      */   {
/* 3221 */     String name = intf.getName().toLowerCase();
/* 3222 */     String desc = intf.getDisplayName().toLowerCase();
/*      */     
/* 3224 */     if ((desc.startsWith("tap-")) || (desc.contains("vpn")))
/*      */     {
/* 3226 */       return 2;
/*      */     }
/* 3228 */     if (name.startsWith("ppp"))
/*      */     {
/* 3230 */       return 2;
/*      */     }
/* 3232 */     if (name.startsWith("tun"))
/*      */     {
/* 3234 */       return 2;
/*      */     }
/* 3236 */     if ((name.startsWith("eth")) || (name.startsWith("en")))
/*      */     {
/* 3238 */       return 1;
/*      */     }
/*      */     
/*      */ 
/* 3242 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String classifyRoute(InetAddress address)
/*      */   {
/* 3250 */     synchronized (this.address_history)
/*      */     {
/* 3252 */       if (this.address_history_update_time == 0L)
/*      */       {
/* 3254 */         return "Initializing";
/*      */       }
/*      */       
/* 3257 */       byte[] address_bytes = address.getAddress();
/*      */       
/* 3259 */       AddressHistoryRecord best_entry = null;
/* 3260 */       int best_prefix = 0;
/*      */       
/* 3262 */       for (AddressHistoryRecord entry : this.address_history.values())
/*      */       {
/* 3264 */         InetAddress other_address = entry.getAddress();
/* 3265 */         byte[] other_bytes = other_address.getAddress();
/*      */         
/* 3267 */         if (other_bytes.length == address_bytes.length)
/*      */         {
/* 3269 */           int prefix_len = 0;
/*      */           
/* 3271 */           for (int i = 0; i < other_bytes.length; i++)
/*      */           {
/* 3273 */             byte b1 = address_bytes[i];
/* 3274 */             byte b2 = other_bytes[i];
/*      */             
/* 3276 */             if (b1 == b2)
/*      */             {
/* 3278 */               prefix_len += 8;
/*      */             }
/*      */             else
/*      */             {
/* 3282 */               for (int j = 7; j >= 1; j--)
/*      */               {
/* 3284 */                 if ((b1 >> j & 0x1) != (b2 >> j & 0x1))
/*      */                   break;
/* 3286 */                 prefix_len++;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3294 */               break;
/*      */             }
/*      */           }
/*      */           
/* 3298 */           if (prefix_len > best_prefix)
/*      */           {
/* 3300 */             best_prefix = prefix_len;
/*      */             
/* 3302 */             best_entry = entry;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 3307 */       if (best_entry == null)
/*      */       {
/* 3309 */         return "Unknown";
/*      */       }
/*      */       
/* 3312 */       return best_entry.getName(this.address_history_update_time);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object[] getInterfaceForAddress(InetAddress address)
/*      */   {
/* 3320 */     byte[] address_bytes = address.getAddress();
/*      */     
/* 3322 */     Set<NetworkInterface> interfaces = this.old_network_interfaces;
/*      */     
/* 3324 */     if (interfaces == null)
/*      */     {
/* 3326 */       return null;
/*      */     }
/*      */     
/* 3329 */     NetworkInterface best_intf = null;
/* 3330 */     InetAddress best_addr = null;
/* 3331 */     int best_prefix = 0;
/*      */     
/* 3333 */     for (NetworkInterface intf : interfaces)
/*      */     {
/* 3335 */       Enumeration<InetAddress> addresses = intf.getInetAddresses();
/*      */       
/* 3337 */       int num_addresses = 0;
/*      */       
/* 3339 */       InetAddress derp = null;
/*      */       
/* 3341 */       while (addresses.hasMoreElements())
/*      */       {
/* 3343 */         InetAddress other_address = (InetAddress)addresses.nextElement();
/* 3344 */         byte[] other_bytes = other_address.getAddress();
/*      */         
/* 3346 */         if (other_bytes.length == address_bytes.length)
/*      */         {
/* 3348 */           num_addresses++;
/*      */           
/* 3350 */           int prefix_len = 0;
/*      */           
/* 3352 */           for (int i = 0; i < other_bytes.length; i++)
/*      */           {
/* 3354 */             byte b1 = address_bytes[i];
/* 3355 */             byte b2 = other_bytes[i];
/*      */             
/* 3357 */             if (b1 == b2)
/*      */             {
/* 3359 */               prefix_len += 8;
/*      */             }
/*      */             else
/*      */             {
/* 3363 */               for (int j = 7; j >= 1; j--)
/*      */               {
/* 3365 */                 if ((b1 >> j & 0x1) != (b2 >> j & 0x1))
/*      */                   break;
/* 3367 */                 prefix_len++;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3375 */               break;
/*      */             }
/*      */           }
/*      */           
/* 3379 */           if (prefix_len > best_prefix)
/*      */           {
/* 3381 */             best_prefix = prefix_len;
/*      */             
/* 3383 */             best_intf = intf;
/*      */             
/* 3385 */             best_addr = null;
/* 3386 */             derp = other_address;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 3391 */       if ((derp != null) && (num_addresses > 1))
/*      */       {
/* 3393 */         best_addr = derp;
/*      */       }
/*      */     }
/*      */     
/* 3397 */     if (best_addr != null)
/*      */     {
/* 3399 */       return new Object[] { best_intf, best_addr };
/*      */     }
/* 3401 */     if (best_intf != null)
/*      */     {
/* 3403 */       return new Object[] { best_intf };
/*      */     }
/*      */     
/*      */ 
/* 3407 */     return new Object[] { address };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addPropertyChangeListener(NetworkAdminPropertyChangeListener listener)
/*      */   {
/* 3415 */     this.listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addAndFirePropertyChangeListener(NetworkAdminPropertyChangeListener listener)
/*      */   {
/* 3422 */     this.listeners.add(listener);
/*      */     
/* 3424 */     for (int i = 0; i < NetworkAdmin.PR_NAMES.length; i++) {
/*      */       try
/*      */       {
/* 3427 */         listener.propertyChanged(PR_NAMES[i]);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 3431 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removePropertyChangeListener(NetworkAdminPropertyChangeListener listener)
/*      */   {
/* 3440 */     this.listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 3447 */     writer.println("Network Admin");
/*      */     try
/*      */     {
/* 3450 */       writer.indent();
/*      */       try
/*      */       {
/* 3453 */         writer.println("Binding Details");
/*      */         
/* 3455 */         writer.indent();
/*      */         
/* 3457 */         boolean enforceBind = COConfigurationManager.getBooleanParameter("Enforce Bind IP");
/*      */         
/* 3459 */         writer.println("bind to: " + getString(getAllBindAddresses(false)) + ", enforce=" + enforceBind);
/*      */         
/* 3461 */         writer.println("bindable: " + getString(getBindableAddresses()));
/*      */         
/* 3463 */         writer.println("ipv6_enabled=" + this.IPv6_enabled);
/*      */         
/* 3465 */         writer.println("ipv4_potential=" + hasIPV4Potential());
/* 3466 */         writer.println("ipv6_potential=" + hasIPV6Potential(false) + "/" + hasIPV6Potential(true));
/*      */         try
/*      */         {
/* 3469 */           writer.println("single homed: " + getSingleHomedServiceBindAddress());
/*      */         } catch (Throwable e) {
/* 3471 */           writer.println("single homed: none");
/*      */         }
/*      */         try
/*      */         {
/* 3475 */           writer.println("single homed (4): " + getSingleHomedServiceBindAddress(1));
/*      */         } catch (Throwable e) {
/* 3477 */           writer.println("single homed (4): none");
/*      */         }
/*      */         try
/*      */         {
/* 3481 */           writer.println("single homed (6): " + getSingleHomedServiceBindAddress(2));
/*      */         } catch (Throwable e) {
/* 3483 */           writer.println("single homed (6): none");
/*      */         }
/*      */         
/* 3486 */         writer.println("multi homed, nio=false: " + getString(getMultiHomedServiceBindAddresses(false)));
/* 3487 */         writer.println("multi homed, nio=true:  " + getString(getMultiHomedServiceBindAddresses(true)));
/*      */       }
/*      */       finally {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 3494 */       NetworkAdminHTTPProxy proxy = getHTTPProxy();
/*      */       
/* 3496 */       if (proxy == null)
/*      */       {
/* 3498 */         writer.println("HTTP proxy: none");
/*      */       }
/*      */       else
/*      */       {
/* 3502 */         writer.println("HTTP proxy: " + proxy.getName());
/*      */         
/*      */         try
/*      */         {
/* 3506 */           NetworkAdminHTTPProxy.Details details = proxy.getDetails();
/*      */           
/* 3508 */           writer.println("    name: " + details.getServerName());
/* 3509 */           writer.println("    resp: " + details.getResponse());
/* 3510 */           writer.println("    auth: " + details.getAuthenticationType());
/*      */         }
/*      */         catch (NetworkAdminException e)
/*      */         {
/* 3514 */           writer.println("    failed: " + e.getLocalizedMessage());
/*      */         }
/*      */       }
/*      */       
/* 3518 */       NetworkAdminSocksProxy[] socks = getSocksProxies();
/*      */       
/* 3520 */       if (socks.length == 0)
/*      */       {
/* 3522 */         writer.println("Socks proxy: none");
/*      */       }
/*      */       else
/*      */       {
/* 3526 */         for (int i = 0; i < socks.length; i++)
/*      */         {
/* 3528 */           NetworkAdminSocksProxy sock = socks[i];
/*      */           
/* 3530 */           writer.println("Socks proxy: " + sock.getName());
/*      */           try
/*      */           {
/* 3533 */             String[] versions = sock.getVersionsSupported();
/*      */             
/* 3535 */             String str = "";
/*      */             
/* 3537 */             for (int j = 0; j < versions.length; j++)
/*      */             {
/* 3539 */               str = str + (j == 0 ? "" : ",") + versions[j];
/*      */             }
/*      */             
/* 3542 */             writer.println("   version: " + str);
/*      */           }
/*      */           catch (NetworkAdminException e)
/*      */           {
/* 3546 */             writer.println("    failed: " + e.getLocalizedMessage());
/*      */           }
/*      */         }
/*      */       }
/*      */       try
/*      */       {
/* 3552 */         NetworkAdminNATDevice[] nat_devices = getNATDevices(AzureusCoreFactory.getSingleton());
/*      */         
/* 3554 */         writer.println("NAT Devices: " + nat_devices.length);
/*      */         
/* 3556 */         for (int i = 0; i < nat_devices.length; i++)
/*      */         {
/* 3558 */           NetworkAdminNATDevice device = nat_devices[i];
/*      */           
/* 3560 */           writer.println("    " + device.getName() + ",address=" + device.getAddress().getHostAddress() + ":" + device.getPort() + ",ext=" + device.getExternalAddress());
/*      */         }
/*      */       }
/*      */       catch (Exception e) {
/* 3564 */         writer.println("Nat Devices: Can't get -> " + e.toString());
/*      */       }
/*      */       
/* 3567 */       writer.println("Interfaces");
/*      */       
/* 3569 */       writer.println("   " + getNetworkInterfacesAsString());
/*      */     }
/*      */     finally
/*      */     {
/* 3573 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String getString(InetAddress[] addresses)
/*      */   {
/* 3581 */     String str = "";
/*      */     
/* 3583 */     for (InetAddress address : addresses)
/*      */     {
/* 3585 */       str = str + (str.length() == 0 ? "" : ", ") + address.getHostAddress();
/*      */     }
/*      */     
/* 3588 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generateDiagnostics(final IndentWriter iw)
/*      */   {
/* 3595 */     Set public_addresses = new HashSet();
/*      */     
/* 3597 */     NetworkAdminHTTPProxy proxy = getHTTPProxy();
/*      */     
/* 3599 */     if (proxy == null)
/*      */     {
/* 3601 */       iw.println("HTTP proxy: none");
/*      */     }
/*      */     else
/*      */     {
/* 3605 */       iw.println("HTTP proxy: " + proxy.getName());
/*      */       
/*      */       try
/*      */       {
/* 3609 */         NetworkAdminHTTPProxy.Details details = proxy.getDetails();
/*      */         
/* 3611 */         iw.println("    name: " + details.getServerName());
/* 3612 */         iw.println("    resp: " + details.getResponse());
/* 3613 */         iw.println("    auth: " + details.getAuthenticationType());
/*      */       }
/*      */       catch (NetworkAdminException e)
/*      */       {
/* 3617 */         iw.println("    failed: " + e.getLocalizedMessage());
/*      */       }
/*      */     }
/*      */     
/* 3621 */     NetworkAdminSocksProxy[] socks = getSocksProxies();
/*      */     
/* 3623 */     if (socks.length == 0)
/*      */     {
/* 3625 */       iw.println("Socks proxy: none");
/*      */     }
/*      */     else
/*      */     {
/* 3629 */       for (int i = 0; i < socks.length; i++)
/*      */       {
/* 3631 */         NetworkAdminSocksProxy sock = socks[i];
/*      */         
/* 3633 */         iw.println("Socks proxy: " + sock.getName());
/*      */         try
/*      */         {
/* 3636 */           String[] versions = sock.getVersionsSupported();
/*      */           
/* 3638 */           String str = "";
/*      */           
/* 3640 */           for (int j = 0; j < versions.length; j++)
/*      */           {
/* 3642 */             str = str + (j == 0 ? "" : ",") + versions[j];
/*      */           }
/*      */           
/* 3645 */           iw.println("   version: " + str);
/*      */         }
/*      */         catch (NetworkAdminException e)
/*      */         {
/* 3649 */           iw.println("    failed: " + e.getLocalizedMessage());
/*      */         }
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 3655 */       NetworkAdminNATDevice[] nat_devices = getNATDevices(AzureusCoreFactory.getSingleton());
/*      */       
/* 3657 */       iw.println("NAT Devices: " + nat_devices.length);
/*      */       
/* 3659 */       for (int i = 0; i < nat_devices.length; i++)
/*      */       {
/* 3661 */         NetworkAdminNATDevice device = nat_devices[i];
/*      */         
/* 3663 */         iw.println("    " + device.getName() + ",address=" + device.getAddress().getHostAddress() + ":" + device.getPort() + ",ext=" + device.getExternalAddress());
/*      */         
/* 3665 */         public_addresses.add(device.getExternalAddress());
/*      */       }
/*      */     } catch (Exception e) {
/* 3668 */       iw.println("Nat Devices: Can't get -> " + e.toString());
/*      */     }
/*      */     
/* 3671 */     iw.println("Interfaces");
/*      */     
/* 3673 */     NetworkAdminNetworkInterface[] interfaces = getInterfaces();
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
/*      */     try
/*      */     {
/* 3721 */       pingTargets(InetAddress.getByName("www.google.com"), 30000, new NetworkAdminRoutesListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 3726 */         private int timeouts = 0;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public boolean foundNode(NetworkAdminNetworkInterfaceAddress intf, NetworkAdminNode[] route, int distance, int rtt)
/*      */         {
/* 3735 */           iw.println(intf.getAddress().getHostAddress() + ": " + route[(route.length - 1)].getAddress().getHostAddress() + " (" + distance + ")");
/*      */           
/* 3737 */           return false;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public boolean timeout(NetworkAdminNetworkInterfaceAddress intf, NetworkAdminNode[] route, int distance)
/*      */         {
/* 3746 */           iw.println(intf.getAddress().getHostAddress() + ": timeout (dist=" + distance + ")");
/*      */           
/* 3748 */           this.timeouts += 1;
/*      */           
/* 3750 */           return this.timeouts < 3;
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 3756 */       iw.println("getRoutes failed: " + Debug.getNestedExceptionMessage(e));
/*      */     }
/*      */     
/*      */ 
/* 3760 */     iw.println("Inbound protocols: default routing");
/*      */     
/*      */ 
/* 3763 */     if (AzureusCoreFactory.isCoreRunning()) {
/* 3764 */       AzureusCore azureus_core = AzureusCoreFactory.getSingleton();
/*      */       
/* 3766 */       NetworkAdminProtocol[] protocols = getInboundProtocols(azureus_core);
/*      */       
/* 3768 */       for (int i = 0; i < protocols.length; i++)
/*      */       {
/* 3770 */         NetworkAdminProtocol protocol = protocols[i];
/*      */         try
/*      */         {
/* 3773 */           InetAddress ext_addr = testProtocol(protocol);
/*      */           
/* 3775 */           if (ext_addr != null)
/*      */           {
/* 3777 */             public_addresses.add(ext_addr);
/*      */           }
/*      */           
/* 3780 */           iw.println("    " + protocol.getName() + " - " + ext_addr);
/*      */         }
/*      */         catch (NetworkAdminException e)
/*      */         {
/* 3784 */           iw.println("    " + protocol.getName() + " - " + Debug.getNestedExceptionMessage(e));
/*      */         }
/*      */       }
/*      */       
/* 3788 */       iw.println("Outbound protocols: default routing");
/*      */       
/* 3790 */       protocols = getOutboundProtocols(azureus_core);
/*      */       
/* 3792 */       for (int i = 0; i < protocols.length; i++)
/*      */       {
/* 3794 */         NetworkAdminProtocol protocol = protocols[i];
/*      */         
/*      */         try
/*      */         {
/* 3798 */           InetAddress ext_addr = testProtocol(protocol);
/*      */           
/* 3800 */           if (ext_addr != null)
/*      */           {
/* 3802 */             public_addresses.add(ext_addr);
/*      */           }
/*      */           
/* 3805 */           iw.println("    " + protocol.getName() + " - " + ext_addr);
/*      */         }
/*      */         catch (NetworkAdminException e)
/*      */         {
/* 3809 */           iw.println("    " + protocol.getName() + " - " + Debug.getNestedExceptionMessage(e));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3814 */     Iterator it = public_addresses.iterator();
/*      */     
/* 3816 */     iw.println("Public Addresses");
/*      */     
/* 3818 */     while (it.hasNext())
/*      */     {
/* 3820 */       InetAddress pub_address = (InetAddress)it.next();
/*      */       try
/*      */       {
/* 3823 */         NetworkAdminASN res = lookupCurrentASN(pub_address);
/*      */         
/* 3825 */         iw.println("    " + pub_address.getHostAddress() + " -> " + res.getAS() + "/" + res.getASName());
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 3829 */         iw.println("    " + pub_address.getHostAddress() + " -> " + Debug.getNestedExceptionMessage(e));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected class networkInterface
/*      */     implements NetworkAdminNetworkInterface
/*      */   {
/*      */     private final NetworkInterface ni;
/*      */     
/*      */ 
/*      */     protected networkInterface(NetworkInterface _ni)
/*      */     {
/* 3844 */       this.ni = _ni;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getDisplayName()
/*      */     {
/* 3850 */       return this.ni.getDisplayName();
/*      */     }
/*      */     
/*      */ 
/*      */     public String getName()
/*      */     {
/* 3856 */       return this.ni.getName();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public NetworkAdminNetworkInterfaceAddress[] getAddresses()
/*      */     {
/* 3864 */       Enumeration e = this.ni.getInetAddresses();
/*      */       
/* 3866 */       List addresses = new ArrayList();
/*      */       
/* 3868 */       while (e.hasMoreElements())
/*      */       {
/* 3870 */         InetAddress address = (InetAddress)e.nextElement();
/*      */         
/* 3872 */         if ((!(address instanceof Inet6Address)) || (NetworkAdminImpl.this.IPv6_enabled))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 3877 */           addresses.add(new networkAddress(address));
/*      */         }
/*      */       }
/* 3880 */       return (NetworkAdminNetworkInterfaceAddress[])addresses.toArray(new NetworkAdminNetworkInterfaceAddress[addresses.size()]);
/*      */     }
/*      */     
/*      */ 
/*      */     public String getString()
/*      */     {
/* 3886 */       String str = getDisplayName() + "/" + getName() + " [";
/*      */       
/* 3888 */       NetworkAdminNetworkInterfaceAddress[] addresses = getAddresses();
/*      */       
/* 3890 */       for (int i = 0; i < addresses.length; i++)
/*      */       {
/* 3892 */         networkAddress addr = (networkAddress)addresses[i];
/*      */         
/* 3894 */         str = str + (i == 0 ? "" : ",") + addr.getAddress().getHostAddress();
/*      */       }
/*      */       
/* 3897 */       return str + "]";
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void generateDiagnostics(IndentWriter iw, Set public_addresses)
/*      */     {
/* 3905 */       iw.println(getDisplayName() + "/" + getName());
/*      */       
/* 3907 */       NetworkAdminNetworkInterfaceAddress[] addresses = getAddresses();
/*      */       
/* 3909 */       for (int i = 0; i < addresses.length; i++)
/*      */       {
/* 3911 */         networkAddress addr = (networkAddress)addresses[i];
/*      */         
/* 3913 */         iw.indent();
/*      */         
/*      */         try
/*      */         {
/* 3917 */           addr.generateDiagnostics(iw, public_addresses);
/*      */         }
/*      */         finally
/*      */         {
/* 3921 */           iw.exdent();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected class networkAddress
/*      */       implements NetworkAdminNetworkInterfaceAddress
/*      */     {
/*      */       private final InetAddress address;
/*      */       
/*      */ 
/*      */ 
/*      */       protected networkAddress(InetAddress _address)
/*      */       {
/* 3938 */         this.address = _address;
/*      */       }
/*      */       
/*      */ 
/*      */       public NetworkAdminNetworkInterface getInterface()
/*      */       {
/* 3944 */         return NetworkAdminImpl.networkInterface.this;
/*      */       }
/*      */       
/*      */ 
/*      */       public InetAddress getAddress()
/*      */       {
/* 3950 */         return this.address;
/*      */       }
/*      */       
/*      */ 
/*      */       public boolean isLoopback()
/*      */       {
/* 3956 */         return this.address.isLoopbackAddress();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public NetworkAdminNode[] getRoute(InetAddress target, int max_millis, NetworkAdminRouteListener listener)
/*      */         throws NetworkAdminException
/*      */       {
/* 3967 */         return NetworkAdminImpl.this.getRoute(this.address, target, max_millis, listener);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public NetworkAdminNode pingTarget(InetAddress target, int max_millis, NetworkAdminRouteListener listener)
/*      */         throws NetworkAdminException
/*      */       {
/* 3978 */         return NetworkAdminImpl.this.pingTarget(this.address, target, max_millis, listener);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public InetAddress testProtocol(NetworkAdminProtocol protocol)
/*      */         throws NetworkAdminException
/*      */       {
/* 3987 */         return protocol.test(this);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void generateDiagnostics(IndentWriter iw, Set public_addresses)
/*      */       {
/* 3995 */         iw.println("" + getAddress());
/*      */         try
/*      */         {
/* 3998 */           iw.println("  Trace route");
/*      */           
/* 4000 */           iw.indent();
/*      */           
/* 4002 */           if (isLoopback())
/*      */           {
/* 4004 */             iw.println("Loopback - ignoring");
/*      */           }
/*      */           else
/*      */           {
/*      */             try {
/* 4009 */               NetworkAdminNode[] nodes = getRoute(InetAddress.getByName("www.google.com"), 30000, NetworkAdminImpl.this.trace_route_listener);
/*      */               
/* 4011 */               for (int i = 0; i < nodes.length; i++)
/*      */               {
/* 4013 */                 NetworkAdminImpl.networkNode node = (NetworkAdminImpl.networkNode)nodes[i];
/*      */                 
/* 4015 */                 iw.println(node.getString());
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/* 4019 */               iw.println("Can't resolve host for route trace - " + e.getMessage());
/*      */             }
/*      */             
/* 4022 */             iw.println("Outbound protocols: bound");
/*      */             
/* 4024 */             AzureusCore azureus_core = AzureusCoreFactory.getSingleton();
/*      */             
/* 4026 */             NetworkAdminProtocol[] protocols = NetworkAdminImpl.this.getOutboundProtocols(azureus_core);
/*      */             
/* 4028 */             for (int i = 0; i < protocols.length; i++)
/*      */             {
/* 4030 */               NetworkAdminProtocol protocol = protocols[i];
/*      */               try
/*      */               {
/* 4033 */                 InetAddress res = testProtocol(protocol);
/*      */                 
/* 4035 */                 if (res != null)
/*      */                 {
/* 4037 */                   public_addresses.add(res);
/*      */                 }
/*      */                 
/* 4040 */                 iw.println("    " + protocol.getName() + " - " + res);
/*      */               }
/*      */               catch (NetworkAdminException e)
/*      */               {
/* 4044 */                 iw.println("    " + protocol.getName() + " - " + Debug.getNestedExceptionMessage(e));
/*      */               }
/*      */             }
/*      */             
/* 4048 */             iw.println("Inbound protocols: bound");
/*      */             
/* 4050 */             protocols = NetworkAdminImpl.this.getInboundProtocols(azureus_core);
/*      */             
/* 4052 */             for (int i = 0; i < protocols.length; i++)
/*      */             {
/* 4054 */               NetworkAdminProtocol protocol = protocols[i];
/*      */               try
/*      */               {
/* 4057 */                 InetAddress res = testProtocol(protocol);
/*      */                 
/* 4059 */                 if (res != null)
/*      */                 {
/* 4061 */                   public_addresses.add(res);
/*      */                 }
/*      */                 
/* 4064 */                 iw.println("    " + protocol.getName() + " - " + res);
/*      */               }
/*      */               catch (NetworkAdminException e)
/*      */               {
/* 4068 */                 iw.println("    " + protocol.getName() + " - " + Debug.getNestedExceptionMessage(e));
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         finally {
/* 4074 */           iw.exdent();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static class networkNode
/*      */     implements NetworkAdminNode
/*      */   {
/*      */     private final InetAddress address;
/*      */     
/*      */     private final int distance;
/*      */     
/*      */     private final int rtt;
/*      */     
/*      */ 
/*      */     protected networkNode(InetAddress _address, int _distance, int _millis)
/*      */     {
/* 4094 */       this.address = _address;
/* 4095 */       this.distance = _distance;
/* 4096 */       this.rtt = _millis;
/*      */     }
/*      */     
/*      */ 
/*      */     public InetAddress getAddress()
/*      */     {
/* 4102 */       return this.address;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isLocalAddress()
/*      */     {
/* 4108 */       return (this.address.isLinkLocalAddress()) || (this.address.isSiteLocalAddress());
/*      */     }
/*      */     
/*      */ 
/*      */     public int getDistance()
/*      */     {
/* 4114 */       return this.distance;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getRTT()
/*      */     {
/* 4120 */       return this.rtt;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getString()
/*      */     {
/* 4126 */       if (this.address == null)
/*      */       {
/* 4128 */         return "" + this.distance;
/*      */       }
/*      */       
/*      */ 
/* 4132 */       return this.distance + "," + this.address + "[local=" + isLocalAddress() + "]," + this.rtt;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void generateDiagnostics(IndentWriter iw, NetworkAdminProtocol[] protocols)
/*      */   {
/* 4142 */     for (int i = 0; i < protocols.length; i++)
/*      */     {
/* 4144 */       NetworkAdminProtocol protocol = protocols[i];
/*      */       
/* 4146 */       iw.println("Testing " + protocol.getName());
/*      */       try
/*      */       {
/* 4149 */         InetAddress ext_addr = testProtocol(protocol);
/*      */         
/* 4151 */         iw.println("    -> OK, public address=" + ext_addr);
/*      */       }
/*      */       catch (NetworkAdminException e)
/*      */       {
/* 4155 */         iw.println("    -> Failed: " + Debug.getNestedExceptionMessage(e));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void logNATStatus(IndentWriter iw)
/*      */   {
/* 4164 */     if (AzureusCoreFactory.isCoreRunning()) {
/* 4165 */       generateDiagnostics(iw, getInboundProtocols(AzureusCoreFactory.getSingleton()));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class AddressHistoryRecord
/*      */   {
/*      */     private final String ni_name;
/*      */     
/*      */     private final boolean ni_has_multiple_addresses;
/*      */     
/*      */     private final InetAddress address;
/*      */     
/*      */     private long last_seen;
/*      */     
/*      */ 
/*      */     private AddressHistoryRecord(NetworkInterface _ni, InetAddress _a, long _now)
/*      */     {
/* 4183 */       this.ni_name = _ni.getName();
/* 4184 */       this.address = _a;
/* 4185 */       this.last_seen = _now;
/*      */       
/* 4187 */       Enumeration<InetAddress> addresses = _ni.getInetAddresses();
/*      */       
/* 4189 */       int hits = 0;
/*      */       
/* 4191 */       int len = this.address.getAddress().length;
/*      */       
/* 4193 */       while (addresses.hasMoreElements())
/*      */       {
/* 4195 */         if (((InetAddress)addresses.nextElement()).getAddress().length == len)
/*      */         {
/* 4197 */           hits++;
/*      */         }
/*      */       }
/*      */       
/* 4201 */       this.ni_has_multiple_addresses = (hits > 1);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setLastSeen(long t)
/*      */     {
/* 4208 */       this.last_seen = t;
/*      */     }
/*      */     
/*      */ 
/*      */     private long getLastSeen()
/*      */     {
/* 4214 */       return this.last_seen;
/*      */     }
/*      */     
/*      */ 
/*      */     private InetAddress getAddress()
/*      */     {
/* 4220 */       return this.address;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private String getName(long last_update)
/*      */     {
/* 4227 */       String result = this.ni_name;
/*      */       
/* 4229 */       if (this.ni_has_multiple_addresses)
/*      */       {
/* 4231 */         result = result + "/" + this.address.getHostAddress();
/*      */       }
/*      */       
/* 4234 */       if (last_update > this.last_seen)
/*      */       {
/* 4236 */         result = result + " (disconnected)";
/*      */       }
/*      */       
/* 4239 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/* 4247 */     boolean TEST_SOCKS_PROXY = false;
/* 4248 */     boolean TEST_HTTP_PROXY = false;
/*      */     try
/*      */     {
/* 4251 */       if (TEST_SOCKS_PROXY)
/*      */       {
/* 4253 */         AESocksProxy proxy = com.aelitis.azureus.core.proxy.socks.AESocksProxyFactory.create(4567, 10000L, 10000L);
/*      */         
/* 4255 */         proxy.setAllowExternalConnections(true);
/*      */         
/* 4257 */         System.setProperty("socksProxyHost", "localhost");
/* 4258 */         System.setProperty("socksProxyPort", "4567");
/*      */       }
/*      */       
/* 4261 */       if (TEST_HTTP_PROXY)
/*      */       {
/* 4263 */         System.setProperty("http.proxyHost", "localhost");
/* 4264 */         System.setProperty("http.proxyPort", "3128");
/* 4265 */         System.setProperty("https.proxyHost", "localhost");
/* 4266 */         System.setProperty("https.proxyPort", "3128");
/*      */         
/* 4268 */         java.net.Authenticator.setDefault(new java.net.Authenticator()
/*      */         {
/*      */ 
/*      */           protected java.net.PasswordAuthentication getPasswordAuthentication()
/*      */           {
/*      */ 
/* 4274 */             return new java.net.PasswordAuthentication("fred", "bill".toCharArray());
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/* 4280 */       IndentWriter iw = new IndentWriter(new java.io.PrintWriter(System.out));
/*      */       
/* 4282 */       iw.setForce(true);
/*      */       
/* 4284 */       COConfigurationManager.initialise();
/*      */       
/* 4286 */       AzureusCoreFactory.create();
/*      */       
/* 4288 */       NetworkAdmin admin = getSingleton();
/*      */       
/*      */ 
/* 4291 */       admin.generateDiagnostics(iw);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 4295 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public InetAddress getDefaultPublicAddress(boolean peek)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: getstatic 1710	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl:gdpa_lock	[Ljava/net/InetAddress;
/*      */     //   3: dup
/*      */     //   4: astore_3
/*      */     //   5: monitorenter
/*      */     //   6: invokestatic 1974	org/gudy/azureus2/core3/util/SystemTime:getMonotonousTime	()J
/*      */     //   9: lstore 4
/*      */     //   11: getstatic 1719	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl:gdpa_sem	Lorg/gudy/azureus2/core3/util/AESemaphore;
/*      */     //   14: ifnonnull +82 -> 96
/*      */     //   17: iconst_1
/*      */     //   18: istore 6
/*      */     //   20: iload_1
/*      */     //   21: ifeq +27 -> 48
/*      */     //   24: getstatic 1690	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl:gdpa_last_lookup	J
/*      */     //   27: lconst_0
/*      */     //   28: lcmp
/*      */     //   29: ifeq +19 -> 48
/*      */     //   32: lload 4
/*      */     //   34: getstatic 1690	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl:gdpa_last_lookup	J
/*      */     //   37: lsub
/*      */     //   38: ldc2_w 852
/*      */     //   41: lcmp
/*      */     //   42: ifgt +6 -> 48
/*      */     //   45: iconst_0
/*      */     //   46: istore 6
/*      */     //   48: iload 6
/*      */     //   50: ifeq +41 -> 91
/*      */     //   53: lload 4
/*      */     //   55: putstatic 1690	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl:gdpa_last_lookup	J
/*      */     //   58: new 1100	org/gudy/azureus2/core3/util/AESemaphore
/*      */     //   61: dup
/*      */     //   62: ldc_w 936
/*      */     //   65: invokespecial 1955	org/gudy/azureus2/core3/util/AESemaphore:<init>	(Ljava/lang/String;)V
/*      */     //   68: dup
/*      */     //   69: astore_2
/*      */     //   70: putstatic 1719	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl:gdpa_sem	Lorg/gudy/azureus2/core3/util/AESemaphore;
/*      */     //   73: new 1035	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl$9
/*      */     //   76: dup
/*      */     //   77: aload_0
/*      */     //   78: ldc_w 936
/*      */     //   81: aload_2
/*      */     //   82: invokespecial 1822	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl$9:<init>	(Lcom/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl;Ljava/lang/String;Lorg/gudy/azureus2/core3/util/AESemaphore;)V
/*      */     //   85: invokevirtual 1821	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl$9:start	()V
/*      */     //   88: goto +5 -> 93
/*      */     //   91: aconst_null
/*      */     //   92: astore_2
/*      */     //   93: goto +7 -> 100
/*      */     //   96: getstatic 1719	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl:gdpa_sem	Lorg/gudy/azureus2/core3/util/AESemaphore;
/*      */     //   99: astore_2
/*      */     //   100: getstatic 1689	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl:gdpa_last_fail	J
/*      */     //   103: lconst_0
/*      */     //   104: lcmp
/*      */     //   105: ifeq +25 -> 130
/*      */     //   108: invokestatic 1974	org/gudy/azureus2/core3/util/SystemTime:getMonotonousTime	()J
/*      */     //   111: getstatic 1689	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl:gdpa_last_fail	J
/*      */     //   114: lsub
/*      */     //   115: ldc2_w 854
/*      */     //   118: lcmp
/*      */     //   119: ifge +11 -> 130
/*      */     //   122: getstatic 1710	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl:gdpa_lock	[Ljava/net/InetAddress;
/*      */     //   125: iconst_0
/*      */     //   126: aaload
/*      */     //   127: aload_3
/*      */     //   128: monitorexit
/*      */     //   129: areturn
/*      */     //   130: aload_3
/*      */     //   131: monitorexit
/*      */     //   132: goto +10 -> 142
/*      */     //   135: astore 7
/*      */     //   137: aload_3
/*      */     //   138: monitorexit
/*      */     //   139: aload 7
/*      */     //   141: athrow
/*      */     //   142: iload_1
/*      */     //   143: ifeq +34 -> 177
/*      */     //   146: getstatic 1718	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl:gdpa_initial_sem	Lorg/gudy/azureus2/core3/util/AESemaphore;
/*      */     //   149: ldc2_w 846
/*      */     //   152: invokevirtual 1954	org/gudy/azureus2/core3/util/AESemaphore:reserve	(J)Z
/*      */     //   155: pop
/*      */     //   156: getstatic 1710	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl:gdpa_lock	[Ljava/net/InetAddress;
/*      */     //   159: dup
/*      */     //   160: astore_3
/*      */     //   161: monitorenter
/*      */     //   162: getstatic 1710	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl:gdpa_lock	[Ljava/net/InetAddress;
/*      */     //   165: iconst_0
/*      */     //   166: aaload
/*      */     //   167: aload_3
/*      */     //   168: monitorexit
/*      */     //   169: areturn
/*      */     //   170: astore 8
/*      */     //   172: aload_3
/*      */     //   173: monitorexit
/*      */     //   174: aload 8
/*      */     //   176: athrow
/*      */     //   177: aload_2
/*      */     //   178: ldc2_w 846
/*      */     //   181: invokevirtual 1954	org/gudy/azureus2/core3/util/AESemaphore:reserve	(J)Z
/*      */     //   184: istore_3
/*      */     //   185: getstatic 1710	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl:gdpa_lock	[Ljava/net/InetAddress;
/*      */     //   188: dup
/*      */     //   189: astore 4
/*      */     //   191: monitorenter
/*      */     //   192: iload_3
/*      */     //   193: ifeq +10 -> 203
/*      */     //   196: lconst_0
/*      */     //   197: putstatic 1689	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl:gdpa_last_fail	J
/*      */     //   200: goto +15 -> 215
/*      */     //   203: getstatic 1718	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl:gdpa_initial_sem	Lorg/gudy/azureus2/core3/util/AESemaphore;
/*      */     //   206: invokevirtual 1952	org/gudy/azureus2/core3/util/AESemaphore:releaseForever	()V
/*      */     //   209: invokestatic 1974	org/gudy/azureus2/core3/util/SystemTime:getMonotonousTime	()J
/*      */     //   212: putstatic 1689	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl:gdpa_last_fail	J
/*      */     //   215: getstatic 1710	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl:gdpa_lock	[Ljava/net/InetAddress;
/*      */     //   218: iconst_0
/*      */     //   219: aaload
/*      */     //   220: aload 4
/*      */     //   222: monitorexit
/*      */     //   223: areturn
/*      */     //   224: astore 9
/*      */     //   226: aload 4
/*      */     //   228: monitorexit
/*      */     //   229: aload 9
/*      */     //   231: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1405	-> byte code offset #0
/*      */     //   Java source line #1407	-> byte code offset #6
/*      */     //   Java source line #1409	-> byte code offset #11
/*      */     //   Java source line #1411	-> byte code offset #17
/*      */     //   Java source line #1413	-> byte code offset #20
/*      */     //   Java source line #1415	-> byte code offset #24
/*      */     //   Java source line #1417	-> byte code offset #45
/*      */     //   Java source line #1421	-> byte code offset #48
/*      */     //   Java source line #1423	-> byte code offset #53
/*      */     //   Java source line #1425	-> byte code offset #58
/*      */     //   Java source line #1427	-> byte code offset #73
/*      */     //   Java source line #1462	-> byte code offset #91
/*      */     //   Java source line #1464	-> byte code offset #93
/*      */     //   Java source line #1466	-> byte code offset #96
/*      */     //   Java source line #1469	-> byte code offset #100
/*      */     //   Java source line #1471	-> byte code offset #122
/*      */     //   Java source line #1473	-> byte code offset #130
/*      */     //   Java source line #1475	-> byte code offset #142
/*      */     //   Java source line #1479	-> byte code offset #146
/*      */     //   Java source line #1481	-> byte code offset #156
/*      */     //   Java source line #1483	-> byte code offset #162
/*      */     //   Java source line #1484	-> byte code offset #170
/*      */     //   Java source line #1488	-> byte code offset #177
/*      */     //   Java source line #1490	-> byte code offset #185
/*      */     //   Java source line #1492	-> byte code offset #192
/*      */     //   Java source line #1494	-> byte code offset #196
/*      */     //   Java source line #1498	-> byte code offset #203
/*      */     //   Java source line #1500	-> byte code offset #209
/*      */     //   Java source line #1503	-> byte code offset #215
/*      */     //   Java source line #1504	-> byte code offset #224
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	232	0	this	NetworkAdminImpl
/*      */     //   0	232	1	peek	boolean
/*      */     //   69	13	2	sem	AESemaphore
/*      */     //   92	2	2	sem	AESemaphore
/*      */     //   99	79	2	sem	AESemaphore
/*      */     //   4	134	3	Ljava/lang/Object;	Object
/*      */     //   160	13	3	Ljava/lang/Object;	Object
/*      */     //   184	9	3	worked	boolean
/*      */     //   9	45	4	now	long
/*      */     //   18	31	6	do_lookup	boolean
/*      */     //   135	5	7	localObject1	Object
/*      */     //   170	5	8	localObject2	Object
/*      */     //   224	6	9	localObject3	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   6	129	135	finally
/*      */     //   130	132	135	finally
/*      */     //   135	139	135	finally
/*      */     //   162	169	170	finally
/*      */     //   170	174	170	finally
/*      */     //   192	223	224	finally
/*      */     //   224	229	224	finally
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */