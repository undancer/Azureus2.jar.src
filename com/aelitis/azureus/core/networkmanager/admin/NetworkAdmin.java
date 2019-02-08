/*     */ package com.aelitis.azureus.core.networkmanager.admin;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.impl.NetworkAdminImpl;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.nio.channels.UnsupportedAddressTypeException;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class NetworkAdmin
/*     */ {
/*     */   private static NetworkAdmin singleton;
/*     */   public static final String PR_NETWORK_INTERFACES = "Network Interfaces";
/*     */   public static final String PR_DEFAULT_BIND_ADDRESS = "Default Bind IP";
/*     */   public static final String PR_AS = "AS";
/*     */   public static final int IP_PROTOCOL_VERSION_AUTO = 0;
/*     */   public static final int IP_PROTOCOL_VERSION_REQUIRE_V4 = 1;
/*     */   public static final int IP_PROTOCOL_VERSION_REQUIRE_V6 = 2;
/*  45 */   public static final String[] PR_NAMES = { "Network Interfaces", "Default Bind IP", "AS" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized NetworkAdmin getSingleton()
/*     */   {
/*  54 */     if (singleton == null)
/*     */     {
/*  56 */       singleton = new NetworkAdminImpl();
/*     */     }
/*     */     
/*  59 */     return singleton;
/*     */   }
/*     */   
/*  62 */   public InetAddress getSingleHomedServiceBindAddress() { return getSingleHomedServiceBindAddress(0); }
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract InetAddress getSingleHomedServiceBindAddress(int paramInt)
/*     */     throws UnsupportedAddressTypeException;
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract InetAddress[] getMultiHomedServiceBindAddresses(boolean paramBoolean);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract InetAddress getMultiHomedOutgoingRoundRobinBindAddress(InetAddress paramInetAddress);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract String getNetworkInterfacesAsString();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract InetAddress[] getAllBindAddresses(boolean paramBoolean);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract InetAddress[] resolveBindAddresses(String paramString);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract InetAddress guessRoutableBindAddress();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract InetAddress[] getBindableAddresses();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract int getBindablePort(int paramInt)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract boolean mustBind();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract boolean hasMissingForcedBind();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract String getBindStatus();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract NetworkAdminNetworkInterface[] getInterfaces();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract boolean hasIPV4Potential();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract boolean isIPV6Enabled();
/*     */   
/*     */ 
/*     */   public boolean hasIPV6Potential()
/*     */   {
/* 129 */     return hasIPV6Potential(false);
/*     */   }
/*     */   
/*     */   public abstract boolean hasIPV6Potential(boolean paramBoolean);
/*     */   
/*     */   public abstract NetworkAdminProtocol[] getOutboundProtocols(AzureusCore paramAzureusCore);
/*     */   
/*     */   public abstract NetworkAdminProtocol[] getInboundProtocols(AzureusCore paramAzureusCore);
/*     */   
/*     */   public abstract NetworkAdminProtocol createInboundProtocol(AzureusCore paramAzureusCore, int paramInt1, int paramInt2);
/*     */   
/*     */   public abstract InetAddress testProtocol(NetworkAdminProtocol paramNetworkAdminProtocol)
/*     */     throws NetworkAdminException;
/*     */   
/*     */   public abstract NetworkAdminSocksProxy createSocksProxy(String paramString1, int paramInt, String paramString2, String paramString3);
/*     */   
/*     */   public abstract boolean isSocksActive();
/*     */   
/*     */   public abstract NetworkAdminSocksProxy[] getSocksProxies();
/*     */   
/*     */   public abstract NetworkAdminHTTPProxy getHTTPProxy();
/*     */   
/*     */   public abstract NetworkAdminNATDevice[] getNATDevices(AzureusCore paramAzureusCore);
/*     */   
/*     */   public abstract NetworkAdminASN lookupCurrentASN(InetAddress paramInetAddress)
/*     */     throws NetworkAdminException;
/*     */   
/*     */   public abstract NetworkAdminASN getCurrentASN();
/*     */   
/*     */   public abstract NetworkAdminASN lookupASN(InetAddress paramInetAddress)
/*     */     throws NetworkAdminException;
/*     */   
/*     */   public abstract void lookupASN(InetAddress paramInetAddress, NetworkAdminASNListener paramNetworkAdminASNListener);
/*     */   
/*     */   public abstract String classifyRoute(InetAddress paramInetAddress);
/*     */   
/*     */   public abstract boolean canTraceRoute();
/*     */   
/*     */   public abstract void getRoutes(InetAddress paramInetAddress, int paramInt, NetworkAdminRoutesListener paramNetworkAdminRoutesListener)
/*     */     throws NetworkAdminException;
/*     */   
/*     */   public abstract boolean canPing();
/*     */   
/*     */   public abstract void pingTargets(InetAddress paramInetAddress, int paramInt, NetworkAdminRoutesListener paramNetworkAdminRoutesListener)
/*     */     throws NetworkAdminException;
/*     */   
/*     */   public abstract InetAddress getDefaultPublicAddress();
/*     */   
/*     */   public abstract InetAddress getDefaultPublicAddress(boolean paramBoolean);
/*     */   
/*     */   public abstract InetAddress getDefaultPublicAddressV6();
/*     */   
/*     */   public abstract InetAddress getDefaultPublicAddressV6(boolean paramBoolean);
/*     */   
/*     */   public abstract boolean hasDHTIPV6();
/*     */   
/*     */   public abstract void addPropertyChangeListener(NetworkAdminPropertyChangeListener paramNetworkAdminPropertyChangeListener);
/*     */   
/*     */   public abstract void addAndFirePropertyChangeListener(NetworkAdminPropertyChangeListener paramNetworkAdminPropertyChangeListener);
/*     */   
/*     */   public abstract void removePropertyChangeListener(NetworkAdminPropertyChangeListener paramNetworkAdminPropertyChangeListener);
/*     */   
/*     */   public abstract void runInitialChecks(AzureusCore paramAzureusCore);
/*     */   
/*     */   public abstract void logNATStatus(IndentWriter paramIndentWriter);
/*     */   
/*     */   public abstract void generateDiagnostics(IndentWriter paramIndentWriter);
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/NetworkAdmin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */