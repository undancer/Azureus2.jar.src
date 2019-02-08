/*     */ package org.gudy.azureus2.pluginsimpl.local.update;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*     */ import org.gudy.azureus2.plugins.update.UpdatableComponent;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstanceListener;
/*     */ import org.gudy.azureus2.plugins.update.UpdateException;
/*     */ import org.gudy.azureus2.plugins.update.UpdateInstaller;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManager;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManagerListener;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManagerVerificationListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UpdateManagerImpl
/*     */   implements UpdateManager, UpdateCheckInstanceListener
/*     */ {
/*     */   private static UpdateManagerImpl singleton;
/*     */   private AzureusCore azureus_core;
/*     */   
/*     */   public static UpdateManager getSingleton(AzureusCore core)
/*     */   {
/*  55 */     if (singleton == null)
/*     */     {
/*  57 */       singleton = new UpdateManagerImpl(core);
/*     */     }
/*     */     
/*  60 */     return singleton;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  65 */   private List<UpdateCheckInstanceImpl> checkers = new ArrayList();
/*     */   
/*  67 */   private List<UpdatableComponentImpl> components = new ArrayList();
/*  68 */   private List listeners = new ArrayList();
/*  69 */   private List verification_listeners = new ArrayList();
/*     */   
/*  71 */   private List<UpdateInstaller> installers = new ArrayList();
/*     */   
/*  73 */   protected AEMonitor this_mon = new AEMonitor("UpdateManager");
/*     */   
/*     */ 
/*     */ 
/*     */   protected UpdateManagerImpl(AzureusCore _azureus_core)
/*     */   {
/*  79 */     this.azureus_core = _azureus_core;
/*     */     
/*  81 */     UpdateInstallerImpl.checkForFailedInstalls(this);
/*     */     
/*     */ 
/*     */     try
/*     */     {
/*  86 */       PlatformManagerFactory.getPlatformManager();
/*     */     }
/*     */     catch (Throwable e) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected AzureusCore getCore()
/*     */   {
/*  96 */     return this.azureus_core;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void registerUpdatableComponent(UpdatableComponent component, boolean mandatory)
/*     */   {
/*     */     try
/*     */     {
/* 105 */       this.this_mon.enter();
/*     */       
/* 107 */       this.components.add(new UpdatableComponentImpl(component, mandatory));
/*     */     }
/*     */     finally {
/* 110 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public UpdateCheckInstance[] getCheckInstances()
/*     */   {
/*     */     try
/*     */     {
/* 118 */       this.this_mon.enter();
/*     */       
/* 120 */       return (UpdateCheckInstance[])this.checkers.toArray(new UpdateCheckInstance[this.checkers.size()]);
/*     */     }
/*     */     finally
/*     */     {
/* 124 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public UpdateCheckInstance createUpdateCheckInstance()
/*     */   {
/* 131 */     return createUpdateCheckInstance(2, "");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public UpdateCheckInstance createUpdateCheckInstance(int type, String name)
/*     */   {
/*     */     try
/*     */     {
/* 140 */       this.this_mon.enter();
/*     */       
/* 142 */       UpdatableComponentImpl[] comps = new UpdatableComponentImpl[this.components.size()];
/*     */       
/* 144 */       this.components.toArray(comps);
/*     */       
/* 146 */       UpdateCheckInstanceImpl res = new UpdateCheckInstanceImpl(this, type, name, comps);
/*     */       
/* 148 */       this.checkers.add(res);
/*     */       
/* 150 */       res.addListener(this);
/*     */       
/* 152 */       for (int i = 0; i < this.listeners.size(); i++)
/*     */       {
/* 154 */         ((UpdateManagerListener)this.listeners.get(i)).checkInstanceCreated(res);
/*     */       }
/*     */       
/* 157 */       return res;
/*     */     }
/*     */     finally
/*     */     {
/* 161 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public UpdateCheckInstanceImpl createEmptyUpdateCheckInstance(int type, String name)
/*     */   {
/* 170 */     return createEmptyUpdateCheckInstance(type, name, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public UpdateCheckInstanceImpl createEmptyUpdateCheckInstance(int type, String name, boolean low_noise)
/*     */   {
/*     */     try
/*     */     {
/* 180 */       this.this_mon.enter();
/*     */       
/* 182 */       UpdatableComponentImpl[] comps = new UpdatableComponentImpl[0];
/*     */       
/* 184 */       UpdateCheckInstanceImpl res = new UpdateCheckInstanceImpl(this, type, name, comps);
/*     */       
/* 186 */       res.setLowNoise(low_noise);
/*     */       
/* 188 */       this.checkers.add(res);
/*     */       
/* 190 */       res.addListener(this);
/*     */       
/* 192 */       for (int i = 0; i < this.listeners.size(); i++)
/*     */       {
/* 194 */         ((UpdateManagerListener)this.listeners.get(i)).checkInstanceCreated(res);
/*     */       }
/*     */       
/* 197 */       return res;
/*     */     }
/*     */     finally
/*     */     {
/* 201 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public UpdateInstaller createInstaller()
/*     */     throws UpdateException
/*     */   {
/* 210 */     UpdateInstaller installer = new UpdateInstallerImpl(this);
/*     */     
/* 212 */     this.installers.add(installer);
/*     */     
/* 214 */     return installer;
/*     */   }
/*     */   
/*     */ 
/*     */   public UpdateInstaller[] getInstallers()
/*     */   {
/* 220 */     UpdateInstaller[] res = new UpdateInstaller[this.installers.size()];
/*     */     
/* 222 */     this.installers.toArray(res);
/*     */     
/* 224 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void cancelled(UpdateCheckInstance instance)
/*     */   {
/* 231 */     complete(instance);
/*     */   }
/*     */   
/*     */ 
/*     */   public void complete(UpdateCheckInstance instance)
/*     */   {
/*     */     try
/*     */     {
/* 239 */       this.this_mon.enter();
/*     */       
/* 241 */       this.checkers.remove(instance);
/*     */     }
/*     */     finally
/*     */     {
/* 245 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void removeInstaller(UpdateInstaller installer)
/*     */   {
/* 253 */     this.installers.remove(installer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getInstallDir()
/*     */   {
/* 262 */     String str = SystemProperties.getApplicationPath();
/*     */     
/* 264 */     if (str.endsWith(File.separator))
/*     */     {
/* 266 */       str = str.substring(0, str.length() - 1);
/*     */     }
/*     */     
/* 269 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUserDir()
/*     */   {
/* 275 */     String str = SystemProperties.getUserPath();
/*     */     
/* 277 */     if (str.endsWith(File.separator))
/*     */     {
/* 279 */       str = str.substring(0, str.length() - 1);
/*     */     }
/*     */     
/* 282 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void restart()
/*     */     throws UpdateException
/*     */   {
/* 290 */     applyUpdates(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void applyUpdates(boolean restart_after)
/*     */     throws UpdateException
/*     */   {
/*     */     try
/*     */     {
/* 300 */       if (restart_after)
/*     */       {
/* 302 */         this.azureus_core.requestRestart();
/*     */       }
/*     */       else
/*     */       {
/* 306 */         this.azureus_core.requestStop();
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 310 */       throw new UpdateException("UpdateManager:applyUpdates fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public java.io.InputStream verifyData(org.gudy.azureus2.plugins.update.Update update, java.io.InputStream is, boolean force)
/*     */     throws UpdateException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: iconst_0
/*     */     //   1: istore 4
/*     */     //   3: iconst_0
/*     */     //   4: istore 5
/*     */     //   6: aconst_null
/*     */     //   7: astore 6
/*     */     //   9: invokestatic 278	org/gudy/azureus2/core3/util/AETemporaryFileHandler:createTempFile	()Ljava/io/File;
/*     */     //   12: astore 7
/*     */     //   14: aload_2
/*     */     //   15: aload 7
/*     */     //   17: invokestatic 282	org/gudy/azureus2/core3/util/FileUtil:copyFile	(Ljava/io/InputStream;Ljava/io/File;)V
/*     */     //   20: aload 7
/*     */     //   22: invokestatic 279	org/gudy/azureus2/core3/util/AEVerifier:verifyData	(Ljava/io/File;)V
/*     */     //   25: iconst_1
/*     */     //   26: istore 5
/*     */     //   28: new 131	java/io/FileInputStream
/*     */     //   31: dup
/*     */     //   32: aload 7
/*     */     //   34: invokespecial 269	java/io/FileInputStream:<init>	(Ljava/io/File;)V
/*     */     //   37: astore 8
/*     */     //   39: iload 4
/*     */     //   41: ifne +79 -> 120
/*     */     //   44: iload 5
/*     */     //   46: ifne +74 -> 120
/*     */     //   49: aload 6
/*     */     //   51: ifnonnull +14 -> 65
/*     */     //   54: new 149	org/gudy/azureus2/plugins/update/UpdateException
/*     */     //   57: dup
/*     */     //   58: ldc 4
/*     */     //   60: invokespecial 286	org/gudy/azureus2/plugins/update/UpdateException:<init>	(Ljava/lang/String;)V
/*     */     //   63: astore 6
/*     */     //   65: iconst_0
/*     */     //   66: istore 9
/*     */     //   68: iload 9
/*     */     //   70: aload_0
/*     */     //   71: getfield 266	org/gudy/azureus2/pluginsimpl/local/update/UpdateManagerImpl:verification_listeners	Ljava/util/List;
/*     */     //   74: invokeinterface 302 1 0
/*     */     //   79: if_icmpge +41 -> 120
/*     */     //   82: aload_0
/*     */     //   83: getfield 266	org/gudy/azureus2/pluginsimpl/local/update/UpdateManagerImpl:verification_listeners	Ljava/util/List;
/*     */     //   86: iload 9
/*     */     //   88: invokeinterface 303 2 0
/*     */     //   93: checkcast 153	org/gudy/azureus2/plugins/update/UpdateManagerVerificationListener
/*     */     //   96: aload_1
/*     */     //   97: aload 6
/*     */     //   99: invokeinterface 309 3 0
/*     */     //   104: goto +10 -> 114
/*     */     //   107: astore 10
/*     */     //   109: aload 10
/*     */     //   111: invokestatic 281	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*     */     //   114: iinc 9 1
/*     */     //   117: goto -49 -> 68
/*     */     //   120: aload 8
/*     */     //   122: areturn
/*     */     //   123: astore 8
/*     */     //   125: iload_3
/*     */     //   126: ifne +169 -> 295
/*     */     //   129: aload 8
/*     */     //   131: invokevirtual 280	org/gudy/azureus2/core3/util/AEVerifierException:getFailureType	()I
/*     */     //   134: iconst_1
/*     */     //   135: if_icmpne +160 -> 295
/*     */     //   138: iconst_0
/*     */     //   139: istore 9
/*     */     //   141: iload 9
/*     */     //   143: aload_0
/*     */     //   144: getfield 266	org/gudy/azureus2/pluginsimpl/local/update/UpdateManagerImpl:verification_listeners	Ljava/util/List;
/*     */     //   147: invokeinterface 302 1 0
/*     */     //   152: if_icmpge +143 -> 295
/*     */     //   155: iconst_1
/*     */     //   156: istore 4
/*     */     //   158: aload_0
/*     */     //   159: getfield 266	org/gudy/azureus2/pluginsimpl/local/update/UpdateManagerImpl:verification_listeners	Ljava/util/List;
/*     */     //   162: iload 9
/*     */     //   164: invokeinterface 303 2 0
/*     */     //   169: checkcast 153	org/gudy/azureus2/plugins/update/UpdateManagerVerificationListener
/*     */     //   172: aload_1
/*     */     //   173: invokeinterface 308 2 0
/*     */     //   178: ifeq +101 -> 279
/*     */     //   181: iconst_1
/*     */     //   182: istore 5
/*     */     //   184: new 131	java/io/FileInputStream
/*     */     //   187: dup
/*     */     //   188: aload 7
/*     */     //   190: invokespecial 269	java/io/FileInputStream:<init>	(Ljava/io/File;)V
/*     */     //   193: astore 10
/*     */     //   195: iload 4
/*     */     //   197: ifne +79 -> 276
/*     */     //   200: iload 5
/*     */     //   202: ifne +74 -> 276
/*     */     //   205: aload 6
/*     */     //   207: ifnonnull +14 -> 221
/*     */     //   210: new 149	org/gudy/azureus2/plugins/update/UpdateException
/*     */     //   213: dup
/*     */     //   214: ldc 4
/*     */     //   216: invokespecial 286	org/gudy/azureus2/plugins/update/UpdateException:<init>	(Ljava/lang/String;)V
/*     */     //   219: astore 6
/*     */     //   221: iconst_0
/*     */     //   222: istore 11
/*     */     //   224: iload 11
/*     */     //   226: aload_0
/*     */     //   227: getfield 266	org/gudy/azureus2/pluginsimpl/local/update/UpdateManagerImpl:verification_listeners	Ljava/util/List;
/*     */     //   230: invokeinterface 302 1 0
/*     */     //   235: if_icmpge +41 -> 276
/*     */     //   238: aload_0
/*     */     //   239: getfield 266	org/gudy/azureus2/pluginsimpl/local/update/UpdateManagerImpl:verification_listeners	Ljava/util/List;
/*     */     //   242: iload 11
/*     */     //   244: invokeinterface 303 2 0
/*     */     //   249: checkcast 153	org/gudy/azureus2/plugins/update/UpdateManagerVerificationListener
/*     */     //   252: aload_1
/*     */     //   253: aload 6
/*     */     //   255: invokeinterface 309 3 0
/*     */     //   260: goto +10 -> 270
/*     */     //   263: astore 12
/*     */     //   265: aload 12
/*     */     //   267: invokestatic 281	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*     */     //   270: iinc 11 1
/*     */     //   273: goto -49 -> 224
/*     */     //   276: aload 10
/*     */     //   278: areturn
/*     */     //   279: goto +10 -> 289
/*     */     //   282: astore 10
/*     */     //   284: aload 10
/*     */     //   286: invokestatic 281	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*     */     //   289: iinc 9 1
/*     */     //   292: goto -151 -> 141
/*     */     //   295: aload 8
/*     */     //   297: astore 6
/*     */     //   299: aload 8
/*     */     //   301: athrow
/*     */     //   302: astore 7
/*     */     //   304: aload 7
/*     */     //   306: astore 6
/*     */     //   308: aload 7
/*     */     //   310: athrow
/*     */     //   311: astore 7
/*     */     //   313: aload 7
/*     */     //   315: astore 6
/*     */     //   317: new 149	org/gudy/azureus2/plugins/update/UpdateException
/*     */     //   320: dup
/*     */     //   321: ldc 4
/*     */     //   323: aload 7
/*     */     //   325: invokespecial 287	org/gudy/azureus2/plugins/update/UpdateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   328: athrow
/*     */     //   329: astore 13
/*     */     //   331: iload 4
/*     */     //   333: ifne +79 -> 412
/*     */     //   336: iload 5
/*     */     //   338: ifne +74 -> 412
/*     */     //   341: aload 6
/*     */     //   343: ifnonnull +14 -> 357
/*     */     //   346: new 149	org/gudy/azureus2/plugins/update/UpdateException
/*     */     //   349: dup
/*     */     //   350: ldc 4
/*     */     //   352: invokespecial 286	org/gudy/azureus2/plugins/update/UpdateException:<init>	(Ljava/lang/String;)V
/*     */     //   355: astore 6
/*     */     //   357: iconst_0
/*     */     //   358: istore 14
/*     */     //   360: iload 14
/*     */     //   362: aload_0
/*     */     //   363: getfield 266	org/gudy/azureus2/pluginsimpl/local/update/UpdateManagerImpl:verification_listeners	Ljava/util/List;
/*     */     //   366: invokeinterface 302 1 0
/*     */     //   371: if_icmpge +41 -> 412
/*     */     //   374: aload_0
/*     */     //   375: getfield 266	org/gudy/azureus2/pluginsimpl/local/update/UpdateManagerImpl:verification_listeners	Ljava/util/List;
/*     */     //   378: iload 14
/*     */     //   380: invokeinterface 303 2 0
/*     */     //   385: checkcast 153	org/gudy/azureus2/plugins/update/UpdateManagerVerificationListener
/*     */     //   388: aload_1
/*     */     //   389: aload 6
/*     */     //   391: invokeinterface 309 3 0
/*     */     //   396: goto +10 -> 406
/*     */     //   399: astore 15
/*     */     //   401: aload 15
/*     */     //   403: invokestatic 281	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*     */     //   406: iinc 14 1
/*     */     //   409: goto -49 -> 360
/*     */     //   412: aload 13
/*     */     //   414: athrow
/*     */     // Line number table:
/*     */     //   Java source line #322	-> byte code offset #0
/*     */     //   Java source line #323	-> byte code offset #3
/*     */     //   Java source line #324	-> byte code offset #6
/*     */     //   Java source line #327	-> byte code offset #9
/*     */     //   Java source line #329	-> byte code offset #14
/*     */     //   Java source line #332	-> byte code offset #20
/*     */     //   Java source line #334	-> byte code offset #25
/*     */     //   Java source line #336	-> byte code offset #28
/*     */     //   Java source line #379	-> byte code offset #39
/*     */     //   Java source line #381	-> byte code offset #49
/*     */     //   Java source line #383	-> byte code offset #54
/*     */     //   Java source line #386	-> byte code offset #65
/*     */     //   Java source line #389	-> byte code offset #82
/*     */     //   Java source line #394	-> byte code offset #104
/*     */     //   Java source line #391	-> byte code offset #107
/*     */     //   Java source line #393	-> byte code offset #109
/*     */     //   Java source line #386	-> byte code offset #114
/*     */     //   Java source line #338	-> byte code offset #123
/*     */     //   Java source line #340	-> byte code offset #125
/*     */     //   Java source line #342	-> byte code offset #138
/*     */     //   Java source line #345	-> byte code offset #155
/*     */     //   Java source line #347	-> byte code offset #158
/*     */     //   Java source line #350	-> byte code offset #181
/*     */     //   Java source line #352	-> byte code offset #184
/*     */     //   Java source line #379	-> byte code offset #195
/*     */     //   Java source line #381	-> byte code offset #205
/*     */     //   Java source line #383	-> byte code offset #210
/*     */     //   Java source line #386	-> byte code offset #221
/*     */     //   Java source line #389	-> byte code offset #238
/*     */     //   Java source line #394	-> byte code offset #260
/*     */     //   Java source line #391	-> byte code offset #263
/*     */     //   Java source line #393	-> byte code offset #265
/*     */     //   Java source line #386	-> byte code offset #270
/*     */     //   Java source line #357	-> byte code offset #279
/*     */     //   Java source line #354	-> byte code offset #282
/*     */     //   Java source line #356	-> byte code offset #284
/*     */     //   Java source line #342	-> byte code offset #289
/*     */     //   Java source line #361	-> byte code offset #295
/*     */     //   Java source line #363	-> byte code offset #299
/*     */     //   Java source line #365	-> byte code offset #302
/*     */     //   Java source line #367	-> byte code offset #304
/*     */     //   Java source line #369	-> byte code offset #308
/*     */     //   Java source line #371	-> byte code offset #311
/*     */     //   Java source line #373	-> byte code offset #313
/*     */     //   Java source line #375	-> byte code offset #317
/*     */     //   Java source line #379	-> byte code offset #329
/*     */     //   Java source line #381	-> byte code offset #341
/*     */     //   Java source line #383	-> byte code offset #346
/*     */     //   Java source line #386	-> byte code offset #357
/*     */     //   Java source line #389	-> byte code offset #374
/*     */     //   Java source line #394	-> byte code offset #396
/*     */     //   Java source line #391	-> byte code offset #399
/*     */     //   Java source line #393	-> byte code offset #401
/*     */     //   Java source line #386	-> byte code offset #406
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	415	0	this	UpdateManagerImpl
/*     */     //   0	415	1	update	org.gudy.azureus2.plugins.update.Update
/*     */     //   0	415	2	is	java.io.InputStream
/*     */     //   0	415	3	force	boolean
/*     */     //   1	331	4	queried	boolean
/*     */     //   4	333	5	ok	boolean
/*     */     //   7	383	6	failure	Throwable
/*     */     //   12	177	7	temp	File
/*     */     //   302	7	7	e	UpdateException
/*     */     //   311	13	7	e	Throwable
/*     */     //   37	84	8	localFileInputStream	java.io.FileInputStream
/*     */     //   123	177	8	e	org.gudy.azureus2.core3.util.AEVerifierException
/*     */     //   66	49	9	i	int
/*     */     //   139	151	9	i	int
/*     */     //   107	170	10	f	Throwable
/*     */     //   282	3	10	f	Throwable
/*     */     //   222	49	11	i	int
/*     */     //   263	3	12	f	Throwable
/*     */     //   329	84	13	localObject	Object
/*     */     //   358	49	14	i	int
/*     */     //   399	3	15	f	Throwable
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   82	104	107	java/lang/Throwable
/*     */     //   20	39	123	org/gudy/azureus2/core3/util/AEVerifierException
/*     */     //   238	260	263	java/lang/Throwable
/*     */     //   155	195	282	java/lang/Throwable
/*     */     //   9	39	302	org/gudy/azureus2/plugins/update/UpdateException
/*     */     //   123	195	302	org/gudy/azureus2/plugins/update/UpdateException
/*     */     //   279	302	302	org/gudy/azureus2/plugins/update/UpdateException
/*     */     //   9	39	311	java/lang/Throwable
/*     */     //   123	195	311	java/lang/Throwable
/*     */     //   279	302	311	java/lang/Throwable
/*     */     //   9	39	329	finally
/*     */     //   123	195	329	finally
/*     */     //   279	331	329	finally
/*     */     //   374	396	399	java/lang/Throwable
/*     */   }
/*     */   
/*     */   public void addVerificationListener(UpdateManagerVerificationListener l)
/*     */   {
/* 405 */     this.verification_listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeVerificationListener(UpdateManagerVerificationListener l)
/*     */   {
/* 412 */     this.verification_listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(UpdateManagerListener l)
/*     */   {
/* 419 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(UpdateManagerListener l)
/*     */   {
/* 426 */     this.listeners.remove(l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/update/UpdateManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */