/*     */ package com.aelitis.azureus.core.networkmanager.admin.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminException;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminProgressListener;
/*     */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
/*     */ import java.net.InetAddress;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import org.gudy.azureus2.core3.ipchecker.natchecker.NatChecker;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NetworkAdminHTTPTester
/*     */   implements NetworkAdminProtocolTester
/*     */ {
/*     */   private final AzureusCore core;
/*     */   private final NetworkAdminProgressListener listener;
/*     */   
/*     */   protected NetworkAdminHTTPTester(AzureusCore _core, NetworkAdminProgressListener _listener)
/*     */   {
/*  46 */     this.core = _core;
/*  47 */     this.listener = _listener;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public InetAddress testOutbound(InetAddress bind_ip, int bind_port)
/*     */     throws NetworkAdminException
/*     */   {
/*  57 */     if ((bind_ip != null) || (bind_port != 0))
/*     */     {
/*  59 */       throw new NetworkAdminException("HTTP tester doesn't support local bind options");
/*     */     }
/*     */     
/*     */ 
/*     */     try
/*     */     {
/*  65 */       return VersionCheckClient.getSingleton().getExternalIpAddressHTTP(false);
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */       try
/*     */       {
/*  72 */         URL url = new URL("http://www.google.com/");
/*     */         
/*  74 */         URLConnection connection = url.openConnection();
/*     */         
/*  76 */         connection.setConnectTimeout(10000);
/*     */         
/*  78 */         connection.connect();
/*     */         
/*  80 */         return null;
/*     */       }
/*     */       catch (Throwable f)
/*     */       {
/*  84 */         throw new NetworkAdminException("Outbound test failed", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public InetAddress testInbound(InetAddress bind_ip, int local_port)
/*     */     throws NetworkAdminException
/*     */   {
/*  96 */     NatChecker checker = new NatChecker(this.core, bind_ip, local_port, true);
/*     */     
/*  98 */     if (checker.getResult() == 1)
/*     */     {
/* 100 */       return checker.getExternalAddress();
/*     */     }
/*     */     
/*     */ 
/* 104 */     throw new NetworkAdminException("NAT check failed: " + checker.getAdditionalInfo());
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminHTTPTester.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */