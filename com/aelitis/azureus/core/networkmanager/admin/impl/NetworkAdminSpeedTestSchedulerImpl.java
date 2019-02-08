/*     */ package com.aelitis.azureus.core.networkmanager.admin.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminException;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTestScheduledTest;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTestScheduledTestListener;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTestScheduler;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTester;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTesterResult;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NetworkAdminSpeedTestSchedulerImpl
/*     */   implements NetworkAdminSpeedTestScheduler
/*     */ {
/*  33 */   private static NetworkAdminSpeedTestSchedulerImpl instance = null;
/*  34 */   private NetworkAdminSpeedTestScheduledTestImpl currentTest = null;
/*     */   
/*     */   public static synchronized NetworkAdminSpeedTestScheduler getInstance() {
/*  37 */     if (instance == null) {
/*  38 */       instance = new NetworkAdminSpeedTestSchedulerImpl();
/*     */     }
/*  40 */     return instance;
/*     */   }
/*     */   
/*     */ 
/*     */   private NetworkAdminSpeedTestSchedulerImpl()
/*     */   {
/*  46 */     NetworkAdminSpeedTesterBTImpl.initialise();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void initialise() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized NetworkAdminSpeedTestScheduledTest getCurrentTest()
/*     */   {
/*  58 */     return this.currentTest;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized NetworkAdminSpeedTestScheduledTest scheduleTest(int type)
/*     */     throws NetworkAdminException
/*     */   {
/*  65 */     if (this.currentTest != null)
/*     */     {
/*  67 */       throw new NetworkAdminException("Test already scheduled");
/*     */     }
/*     */     
/*  70 */     if (type == 1)
/*     */     {
/*  72 */       PluginInterface plugin = PluginInitializer.getDefaultInterface();
/*     */       
/*  74 */       this.currentTest = new NetworkAdminSpeedTestScheduledTestImpl(plugin, new NetworkAdminSpeedTesterBTImpl(plugin));
/*  75 */       this.currentTest.getTester().setMode(type);
/*     */       
/*  77 */       this.currentTest.addListener(new NetworkAdminSpeedTestScheduledTestListener()
/*     */       {
/*     */         public void stage(NetworkAdminSpeedTestScheduledTest test, String step) {}
/*     */         
/*     */ 
/*     */ 
/*     */         public void complete(NetworkAdminSpeedTestScheduledTest test)
/*     */         {
/*  85 */           synchronized (NetworkAdminSpeedTestSchedulerImpl.this)
/*     */           {
/*  87 */             NetworkAdminSpeedTestSchedulerImpl.this.currentTest = null;
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     else
/*     */     {
/*  94 */       throw new NetworkAdminException("Unknown test type");
/*     */     }
/*     */     
/*  97 */     return this.currentTest;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public NetworkAdminSpeedTesterResult getLastResult(int type)
/*     */   {
/* 107 */     if (type == 1)
/*     */     {
/* 109 */       return NetworkAdminSpeedTesterBTImpl.getLastResult();
/*     */     }
/*     */     
/*     */ 
/* 113 */     Debug.out("Unknown test type");
/*     */     
/* 115 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminSpeedTestSchedulerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */