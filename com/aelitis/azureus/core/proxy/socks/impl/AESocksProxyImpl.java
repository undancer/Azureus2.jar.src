/*     */ package com.aelitis.azureus.core.proxy.socks.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.proxy.AEProxy;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyConnection;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyException;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyHandler;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyState;
/*     */ import com.aelitis.azureus.core.proxy.socks.AESocksProxy;
/*     */ import com.aelitis.azureus.core.proxy.socks.AESocksProxyConnection;
/*     */ import com.aelitis.azureus.core.proxy.socks.AESocksProxyPlugableConnection;
/*     */ import com.aelitis.azureus.core.proxy.socks.AESocksProxyPlugableConnectionFactory;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AESocksProxyImpl
/*     */   implements AESocksProxy, AEProxyHandler
/*     */ {
/*     */   private AEProxy proxy;
/*     */   private final AESocksProxyPlugableConnectionFactory connection_factory;
/*     */   private String proxy_chain_host;
/*     */   private int proxy_chain_port;
/*     */   private String proxy_chain_version;
/*     */   
/*     */   public AESocksProxyImpl(int _port, long _ct, long _rt, AESocksProxyPlugableConnectionFactory _connection_factory)
/*     */     throws AEProxyException
/*     */   {
/*  51 */     this.connection_factory = _connection_factory;
/*     */     
/*  53 */     this.proxy = AEProxyFactory.create(_port, _ct, _rt, this);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPort()
/*     */   {
/*  59 */     return this.proxy.getPort();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public AESocksProxyPlugableConnection getDefaultPlugableConnection(AESocksProxyConnection basis)
/*     */   {
/*  66 */     return new AESocksProxyPlugableConnectionDefault(basis);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public AEProxyState getInitialState(AEProxyConnection connection)
/*     */     throws IOException
/*     */   {
/*  75 */     return new AESocksProxyConnectionImpl(this, this.connection_factory, connection).getInitialState();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setNextSOCKSProxy(String host, int port, String proxy_version)
/*     */   {
/*  84 */     this.proxy_chain_host = host;
/*  85 */     this.proxy_chain_port = port;
/*  86 */     this.proxy_chain_version = proxy_version;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getNextSOCKSProxyHost()
/*     */   {
/*  92 */     return this.proxy_chain_host;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNextSOCKSProxyPort()
/*     */   {
/*  98 */     return this.proxy_chain_port;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getNextSOCKSProxyVersion()
/*     */   {
/* 104 */     return this.proxy_chain_version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAllowExternalConnections(boolean permit)
/*     */   {
/* 111 */     this.proxy.setAllowExternalConnections(permit);
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 117 */     this.proxy.destroy();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/socks/impl/AESocksProxyImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */