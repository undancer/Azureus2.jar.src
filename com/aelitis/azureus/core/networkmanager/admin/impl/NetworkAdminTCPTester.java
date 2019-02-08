/*     */ package com.aelitis.azureus.core.networkmanager.admin.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminException;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminProgressListener;
/*     */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
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
/*     */ public class NetworkAdminTCPTester
/*     */   implements NetworkAdminProtocolTester
/*     */ {
/*     */   private final AzureusCore core;
/*     */   private final NetworkAdminProgressListener listener;
/*     */   
/*     */   protected NetworkAdminTCPTester(AzureusCore _core, NetworkAdminProgressListener _listener)
/*     */   {
/*  46 */     this.core = _core;
/*  47 */     this.listener = _listener;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public InetAddress testOutbound(InetAddress bind_ip, int bind_port)
/*     */     throws NetworkAdminException
/*     */   {
/*     */     try
/*     */     {
/*  60 */       return VersionCheckClient.getSingleton().getExternalIpAddressTCP(bind_ip, bind_port, false);
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */       try
/*     */       {
/*  67 */         Socket socket = new Socket();
/*     */         
/*  69 */         if (bind_ip != null)
/*     */         {
/*  71 */           socket.bind(new InetSocketAddress(bind_ip, bind_port));
/*     */         }
/*  73 */         else if (bind_port != 0)
/*     */         {
/*  75 */           socket.bind(new InetSocketAddress(bind_port));
/*     */         }
/*     */         
/*  78 */         socket.setSoTimeout(10000);
/*     */         
/*  80 */         socket.connect(new InetSocketAddress("www.google.com", 80), 10000);
/*     */         
/*  82 */         socket.close();
/*     */         
/*  84 */         return null;
/*     */       }
/*     */       catch (Throwable f)
/*     */       {
/*  88 */         throw new NetworkAdminException("Outbound test failed", e);
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
/* 100 */     NatChecker checker = new NatChecker(this.core, bind_ip, local_port, false);
/*     */     
/* 102 */     if (checker.getResult() == 1)
/*     */     {
/* 104 */       return checker.getExternalAddress();
/*     */     }
/*     */     
/*     */ 
/* 108 */     throw new NetworkAdminException("NAT test failed: " + checker.getAdditionalInfo());
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminTCPTester.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */