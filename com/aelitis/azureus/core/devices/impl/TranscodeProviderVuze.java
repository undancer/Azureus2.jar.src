/*     */ package com.aelitis.azureus.core.devices.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.TranscodeException;
/*     */ import com.aelitis.azureus.core.devices.TranscodeProfile;
/*     */ import com.aelitis.azureus.core.devices.TranscodeProvider;
/*     */ import com.aelitis.azureus.core.devices.TranscodeProviderAdapter;
/*     */ import com.aelitis.azureus.core.devices.TranscodeProviderAnalysis;
/*     */ import com.aelitis.azureus.core.devices.TranscodeProviderJob;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginConfig;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCException;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TranscodeProviderVuze
/*     */   implements TranscodeProvider
/*     */ {
/*     */   private static final String PROFILE_PREFIX = "vuzexcode:";
/*     */   private TranscodeManagerImpl manager;
/*     */   private PluginInterface plugin_interface;
/*     */   private volatile TranscodeProfile[] profiles;
/*  51 */   private Map<String, TranscodeProfile[]> profile_classification_map = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TranscodeProviderVuze(TranscodeManagerImpl _manager, PluginInterface _plugin_interface)
/*     */   {
/*  58 */     this.manager = _manager;
/*     */     
/*  60 */     update(_plugin_interface);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getID()
/*     */   {
/*  66 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public PluginInterface getPluginInterface()
/*     */   {
/*  72 */     return this.plugin_interface;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void update(PluginInterface pi)
/*     */   {
/*  79 */     this.plugin_interface = pi;
/*     */     try
/*     */     {
/*  82 */       this.plugin_interface.getIPC().invoke("addProfileListChangedListener", new Object[] { new AERunnable()
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*  87 */           TranscodeProviderVuze.this.resetProfiles();
/*     */         }
/*     */       } });
/*     */     }
/*     */     catch (IPCException e) {}
/*     */     
/*     */ 
/*  94 */     resetProfiles();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 100 */     return this.plugin_interface.getPluginName() + ": version=" + this.plugin_interface.getPluginVersion();
/*     */   }
/*     */   
/*     */ 
/*     */   private void resetProfiles()
/*     */   {
/* 106 */     synchronized (this.profile_classification_map)
/*     */     {
/* 108 */       this.profile_classification_map.clear();
/*     */       
/* 110 */       this.profiles = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public TranscodeProfile[] getProfiles()
/*     */   {
/* 117 */     if (this.profiles != null)
/*     */     {
/* 119 */       return this.profiles;
/*     */     }
/*     */     try
/*     */     {
/* 123 */       Map<String, Map<String, Object>> profiles_map = (Map)this.plugin_interface.getIPC().invoke("getProfiles", new Object[0]);
/*     */       
/* 125 */       TranscodeProfile[] res = new TranscodeProfile[profiles_map.size()];
/*     */       
/* 127 */       int index = 0;
/*     */       
/* 129 */       for (Map.Entry<String, Map<String, Object>> entry : profiles_map.entrySet())
/*     */       {
/* 131 */         res[(index++)] = new TranscodeProfileImpl(this.manager, 1, "vuzexcode:" + (String)entry.getKey(), (String)entry.getKey(), (Map)entry.getValue());
/*     */       }
/*     */       
/* 134 */       this.profiles = res;
/*     */       
/* 136 */       return res;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 140 */       e.printStackTrace();
/*     */     }
/* 142 */     return new TranscodeProfile[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public TranscodeProfile[] getProfiles(String classification_prefix)
/*     */   {
/* 150 */     classification_prefix = classification_prefix.toLowerCase();
/*     */     
/* 152 */     TranscodeProfile[] profs = getProfiles();
/*     */     
/* 154 */     synchronized (this.profile_classification_map)
/*     */     {
/* 156 */       TranscodeProfile[] res = (TranscodeProfile[])this.profile_classification_map.get(classification_prefix);
/*     */       
/* 158 */       if (res != null)
/*     */       {
/* 160 */         return res;
/*     */       }
/*     */     }
/*     */     
/* 164 */     List<TranscodeProfile> c_profiles = new ArrayList();
/*     */     
/* 166 */     for (TranscodeProfile p : profs)
/*     */     {
/* 168 */       String c = p.getDeviceClassification();
/*     */       
/* 170 */       if (c == null)
/*     */       {
/* 172 */         this.manager.log("Device classification missing for " + p.getName());
/*     */ 
/*     */ 
/*     */       }
/* 176 */       else if (c.toLowerCase().startsWith(classification_prefix))
/*     */       {
/* 178 */         c_profiles.add(p);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 183 */     TranscodeProfile[] res = (TranscodeProfile[])c_profiles.toArray(new TranscodeProfile[c_profiles.size()]);
/*     */     
/* 185 */     synchronized (this.profile_classification_map)
/*     */     {
/* 187 */       if (profs == this.profiles)
/*     */       {
/* 189 */         this.profile_classification_map.put(classification_prefix, res);
/*     */       }
/*     */     }
/*     */     
/* 193 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TranscodeProfile getProfile(String UID)
/*     */   {
/* 200 */     TranscodeProfile[] profiles = getProfiles();
/*     */     
/* 202 */     for (TranscodeProfile profile : profiles)
/*     */     {
/* 204 */       if (profile.getUID().equals(UID))
/*     */       {
/* 206 */         return profile;
/*     */       }
/*     */     }
/*     */     
/* 210 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TranscodeProfile addProfile(File file)
/*     */     throws TranscodeException
/*     */   {
/*     */     try
/*     */     {
/* 220 */       String uid = "vuzexcode:" + (String)this.plugin_interface.getIPC().invoke("addProfile", new Object[] { file });
/*     */       
/* 222 */       resetProfiles();
/*     */       
/* 224 */       return getProfile(uid);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 228 */       throw new TranscodeException("Failed to add profile", e);
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public TranscodeProviderAnalysis analyse(final TranscodeProviderAdapter _adapter, org.gudy.azureus2.plugins.disk.DiskManagerFileInfo input, TranscodeProfile profile)
/*     */     throws TranscodeException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aconst_null
/*     */     //   1: astore 4
/*     */     //   3: aconst_null
/*     */     //   4: astore 5
/*     */     //   6: aload_2
/*     */     //   7: invokeinterface 505 1 0
/*     */     //   12: lstore 6
/*     */     //   14: lload 6
/*     */     //   16: lconst_0
/*     */     //   17: lcmp
/*     */     //   18: ifle +46 -> 64
/*     */     //   21: lload 6
/*     */     //   23: aload_2
/*     */     //   24: invokeinterface 504 1 0
/*     */     //   29: lcmp
/*     */     //   30: ifne +34 -> 64
/*     */     //   33: aload_2
/*     */     //   34: invokeinterface 506 1 0
/*     */     //   39: astore 8
/*     */     //   41: aload 8
/*     */     //   43: invokevirtual 452	java/io/File:exists	()Z
/*     */     //   46: ifeq +18 -> 64
/*     */     //   49: aload 8
/*     */     //   51: invokevirtual 450	java/io/File:length	()J
/*     */     //   54: lload 6
/*     */     //   56: lcmp
/*     */     //   57: ifne +7 -> 64
/*     */     //   60: aload 8
/*     */     //   62: astore 5
/*     */     //   64: aconst_null
/*     */     //   65: astore 8
/*     */     //   67: aload 5
/*     */     //   69: ifnonnull +186 -> 255
/*     */     //   72: aload_2
/*     */     //   73: instanceof 250
/*     */     //   76: ifeq +15 -> 91
/*     */     //   79: aload_2
/*     */     //   80: checkcast 250	com/aelitis/azureus/core/download/DiskManagerFileInfoURL
/*     */     //   83: invokevirtual 449	com/aelitis/azureus/core/download/DiskManagerFileInfoURL:getURL	()Ljava/net/URL;
/*     */     //   86: astore 4
/*     */     //   88: goto +167 -> 255
/*     */     //   91: iconst_0
/*     */     //   92: istore 9
/*     */     //   94: iload 9
/*     */     //   96: bipush 10
/*     */     //   98: if_icmpge +157 -> 255
/*     */     //   101: aload_0
/*     */     //   102: getfield 426	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze:plugin_interface	Lorg/gudy/azureus2/plugins/PluginInterface;
/*     */     //   105: invokeinterface 502 1 0
/*     */     //   110: ldc 20
/*     */     //   112: invokevirtual 480	org/gudy/azureus2/plugins/PluginManager:getPluginInterfaceByID	(Ljava/lang/String;)Lorg/gudy/azureus2/plugins/PluginInterface;
/*     */     //   115: astore 10
/*     */     //   117: aload 10
/*     */     //   119: ifnonnull +13 -> 132
/*     */     //   122: new 229	com/aelitis/azureus/core/devices/TranscodeException
/*     */     //   125: dup
/*     */     //   126: ldc 12
/*     */     //   128: invokespecial 427	com/aelitis/azureus/core/devices/TranscodeException:<init>	(Ljava/lang/String;)V
/*     */     //   131: athrow
/*     */     //   132: aload 10
/*     */     //   134: invokeinterface 503 1 0
/*     */     //   139: astore 11
/*     */     //   141: aload 11
/*     */     //   143: ldc 21
/*     */     //   145: iconst_1
/*     */     //   146: anewarray 253	java/lang/Object
/*     */     //   149: dup
/*     */     //   150: iconst_0
/*     */     //   151: aload_2
/*     */     //   152: aastore
/*     */     //   153: invokeinterface 507 3 0
/*     */     //   158: checkcast 254	java/lang/String
/*     */     //   161: astore 12
/*     */     //   163: aload 12
/*     */     //   165: ifnull +62 -> 227
/*     */     //   168: aload 12
/*     */     //   170: invokevirtual 460	java/lang/String:length	()I
/*     */     //   173: ifle +54 -> 227
/*     */     //   176: new 259	java/net/URL
/*     */     //   179: dup
/*     */     //   180: aload 12
/*     */     //   182: invokespecial 474	java/net/URL:<init>	(Ljava/lang/String;)V
/*     */     //   185: astore 4
/*     */     //   187: new 239	com/aelitis/azureus/core/devices/impl/TranscodePipeStreamSource
/*     */     //   190: dup
/*     */     //   191: aload 4
/*     */     //   193: invokevirtual 472	java/net/URL:getHost	()Ljava/lang/String;
/*     */     //   196: aload 4
/*     */     //   198: invokevirtual 471	java/net/URL:getPort	()I
/*     */     //   201: invokespecial 433	com/aelitis/azureus/core/devices/impl/TranscodePipeStreamSource:<init>	(Ljava/lang/String;I)V
/*     */     //   204: astore 8
/*     */     //   206: aload 4
/*     */     //   208: ldc 4
/*     */     //   210: invokestatic 479	org/gudy/azureus2/core3/util/UrlUtils:setHost	(Ljava/net/URL;Ljava/lang/String;)Ljava/net/URL;
/*     */     //   213: astore 4
/*     */     //   215: aload 4
/*     */     //   217: aload 8
/*     */     //   219: invokevirtual 430	com/aelitis/azureus/core/devices/impl/TranscodePipe:getPort	()I
/*     */     //   222: invokestatic 478	org/gudy/azureus2/core3/util/UrlUtils:setPort	(Ljava/net/URL;I)Ljava/net/URL;
/*     */     //   225: astore 4
/*     */     //   227: aload 4
/*     */     //   229: ifnull +6 -> 235
/*     */     //   232: goto +23 -> 255
/*     */     //   235: ldc2_w 225
/*     */     //   238: invokestatic 468	java/lang/Thread:sleep	(J)V
/*     */     //   241: goto +8 -> 249
/*     */     //   244: astore 13
/*     */     //   246: goto +9 -> 255
/*     */     //   249: iinc 9 1
/*     */     //   252: goto -158 -> 94
/*     */     //   255: aload 5
/*     */     //   257: ifnonnull +18 -> 275
/*     */     //   260: aload 4
/*     */     //   262: ifnonnull +13 -> 275
/*     */     //   265: new 229	com/aelitis/azureus/core/devices/TranscodeException
/*     */     //   268: dup
/*     */     //   269: ldc 10
/*     */     //   271: invokespecial 427	com/aelitis/azureus/core/devices/TranscodeException:<init>	(Ljava/lang/String;)V
/*     */     //   274: athrow
/*     */     //   275: aload 8
/*     */     //   277: astore 9
/*     */     //   279: aload_0
/*     */     //   280: getfield 426	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze:plugin_interface	Lorg/gudy/azureus2/plugins/PluginInterface;
/*     */     //   283: invokeinterface 503 1 0
/*     */     //   288: astore 10
/*     */     //   290: aload 4
/*     */     //   292: ifnull +35 -> 327
/*     */     //   295: aload 10
/*     */     //   297: ldc 16
/*     */     //   299: iconst_2
/*     */     //   300: anewarray 253	java/lang/Object
/*     */     //   303: dup
/*     */     //   304: iconst_0
/*     */     //   305: aload 4
/*     */     //   307: aastore
/*     */     //   308: dup
/*     */     //   309: iconst_1
/*     */     //   310: aload_3
/*     */     //   311: invokeinterface 482 1 0
/*     */     //   316: aastore
/*     */     //   317: invokeinterface 507 3 0
/*     */     //   322: astore 11
/*     */     //   324: goto +32 -> 356
/*     */     //   327: aload 10
/*     */     //   329: ldc 16
/*     */     //   331: iconst_2
/*     */     //   332: anewarray 253	java/lang/Object
/*     */     //   335: dup
/*     */     //   336: iconst_0
/*     */     //   337: aload 5
/*     */     //   339: aastore
/*     */     //   340: dup
/*     */     //   341: iconst_1
/*     */     //   342: aload_3
/*     */     //   343: invokeinterface 482 1 0
/*     */     //   348: aastore
/*     */     //   349: invokeinterface 507 3 0
/*     */     //   354: astore 11
/*     */     //   356: new 261	java/util/HashMap
/*     */     //   359: dup
/*     */     //   360: invokespecial 477	java/util/HashMap:<init>	()V
/*     */     //   363: astore 12
/*     */     //   365: new 243	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze$2
/*     */     //   368: dup
/*     */     //   369: aload_0
/*     */     //   370: aload 10
/*     */     //   372: aload 11
/*     */     //   374: aload 12
/*     */     //   376: invokespecial 440	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze$2:<init>	(Lcom/aelitis/azureus/core/devices/impl/TranscodeProviderVuze;Lorg/gudy/azureus2/plugins/ipc/IPCInterface;Ljava/lang/Object;Ljava/util/Map;)V
/*     */     //   379: astore 13
/*     */     //   381: new 244	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze$3
/*     */     //   384: dup
/*     */     //   385: aload_0
/*     */     //   386: ldc 18
/*     */     //   388: iconst_1
/*     */     //   389: aload 10
/*     */     //   391: aload 11
/*     */     //   393: aload_1
/*     */     //   394: aload 12
/*     */     //   396: aload 9
/*     */     //   398: invokespecial 442	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze$3:<init>	(Lcom/aelitis/azureus/core/devices/impl/TranscodeProviderVuze;Ljava/lang/String;ZLorg/gudy/azureus2/plugins/ipc/IPCInterface;Ljava/lang/Object;Lcom/aelitis/azureus/core/devices/TranscodeProviderAdapter;Ljava/util/Map;Lcom/aelitis/azureus/core/devices/impl/TranscodePipe;)V
/*     */     //   401: invokevirtual 441	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze$3:start	()V
/*     */     //   404: aload 13
/*     */     //   406: areturn
/*     */     //   407: astore 10
/*     */     //   409: aload 8
/*     */     //   411: ifnull +9 -> 420
/*     */     //   414: aload 8
/*     */     //   416: invokevirtual 431	com/aelitis/azureus/core/devices/impl/TranscodePipe:destroy	()Z
/*     */     //   419: pop
/*     */     //   420: aload 10
/*     */     //   422: athrow
/*     */     //   423: astore 4
/*     */     //   425: aload 4
/*     */     //   427: athrow
/*     */     //   428: astore 4
/*     */     //   430: new 229	com/aelitis/azureus/core/devices/TranscodeException
/*     */     //   433: dup
/*     */     //   434: ldc 17
/*     */     //   436: aload 4
/*     */     //   438: invokespecial 428	com/aelitis/azureus/core/devices/TranscodeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   441: athrow
/*     */     // Line number table:
/*     */     //   Java source line #242	-> byte code offset #0
/*     */     //   Java source line #243	-> byte code offset #3
/*     */     //   Java source line #245	-> byte code offset #6
/*     */     //   Java source line #247	-> byte code offset #14
/*     */     //   Java source line #249	-> byte code offset #33
/*     */     //   Java source line #251	-> byte code offset #41
/*     */     //   Java source line #253	-> byte code offset #60
/*     */     //   Java source line #257	-> byte code offset #64
/*     */     //   Java source line #259	-> byte code offset #67
/*     */     //   Java source line #261	-> byte code offset #72
/*     */     //   Java source line #263	-> byte code offset #79
/*     */     //   Java source line #269	-> byte code offset #91
/*     */     //   Java source line #271	-> byte code offset #101
/*     */     //   Java source line #273	-> byte code offset #117
/*     */     //   Java source line #275	-> byte code offset #122
/*     */     //   Java source line #278	-> byte code offset #132
/*     */     //   Java source line #280	-> byte code offset #141
/*     */     //   Java source line #282	-> byte code offset #163
/*     */     //   Java source line #284	-> byte code offset #176
/*     */     //   Java source line #286	-> byte code offset #187
/*     */     //   Java source line #288	-> byte code offset #206
/*     */     //   Java source line #290	-> byte code offset #215
/*     */     //   Java source line #293	-> byte code offset #227
/*     */     //   Java source line #295	-> byte code offset #232
/*     */     //   Java source line #300	-> byte code offset #235
/*     */     //   Java source line #305	-> byte code offset #241
/*     */     //   Java source line #302	-> byte code offset #244
/*     */     //   Java source line #304	-> byte code offset #246
/*     */     //   Java source line #269	-> byte code offset #249
/*     */     //   Java source line #311	-> byte code offset #255
/*     */     //   Java source line #313	-> byte code offset #265
/*     */     //   Java source line #316	-> byte code offset #275
/*     */     //   Java source line #319	-> byte code offset #279
/*     */     //   Java source line #323	-> byte code offset #290
/*     */     //   Java source line #325	-> byte code offset #295
/*     */     //   Java source line #332	-> byte code offset #327
/*     */     //   Java source line #339	-> byte code offset #356
/*     */     //   Java source line #341	-> byte code offset #365
/*     */     //   Java source line #476	-> byte code offset #381
/*     */     //   Java source line #532	-> byte code offset #404
/*     */     //   Java source line #535	-> byte code offset #407
/*     */     //   Java source line #537	-> byte code offset #409
/*     */     //   Java source line #539	-> byte code offset #414
/*     */     //   Java source line #542	-> byte code offset #420
/*     */     //   Java source line #544	-> byte code offset #423
/*     */     //   Java source line #546	-> byte code offset #425
/*     */     //   Java source line #548	-> byte code offset #428
/*     */     //   Java source line #550	-> byte code offset #430
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	442	0	this	TranscodeProviderVuze
/*     */     //   0	442	1	_adapter	TranscodeProviderAdapter
/*     */     //   0	442	2	input	org.gudy.azureus2.plugins.disk.DiskManagerFileInfo
/*     */     //   0	442	3	profile	TranscodeProfile
/*     */     //   1	305	4	source_url	java.net.URL
/*     */     //   423	3	4	e	TranscodeException
/*     */     //   428	9	4	e	Throwable
/*     */     //   4	334	5	source_file	File
/*     */     //   12	43	6	input_length	long
/*     */     //   39	22	8	file	File
/*     */     //   65	350	8	pipe	TranscodePipe
/*     */     //   92	158	9	i	int
/*     */     //   277	120	9	f_pipe	TranscodePipe
/*     */     //   115	18	10	av_pi	PluginInterface
/*     */     //   288	102	10	ipc	IPCInterface
/*     */     //   407	14	10	e	Throwable
/*     */     //   139	3	11	av_ipc	IPCInterface
/*     */     //   322	3	11	analysis_context	Object
/*     */     //   354	38	11	analysis_context	Object
/*     */     //   161	20	12	url_str	String
/*     */     //   363	32	12	result	Map<String, Object>
/*     */     //   244	3	13	e	Throwable
/*     */     //   379	26	13	analysis	TranscodeProviderAnalysisImpl
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   235	241	244	java/lang/Throwable
/*     */     //   279	406	407	java/lang/Throwable
/*     */     //   0	406	423	com/aelitis/azureus/core/devices/TranscodeException
/*     */     //   407	423	423	com/aelitis/azureus/core/devices/TranscodeException
/*     */     //   0	406	428	java/lang/Throwable
/*     */     //   407	423	428	java/lang/Throwable
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public TranscodeProviderJob transcode(final TranscodeProviderAdapter _adapter, TranscodeProviderAnalysis analysis, boolean direct_input, org.gudy.azureus2.plugins.disk.DiskManagerFileInfo input, TranscodeProfile profile, java.net.URL output)
/*     */     throws TranscodeException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 426	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze:plugin_interface	Lorg/gudy/azureus2/plugins/PluginInterface;
/*     */     //   4: invokeinterface 502 1 0
/*     */     //   9: ldc 20
/*     */     //   11: invokevirtual 480	org/gudy/azureus2/plugins/PluginManager:getPluginInterfaceByID	(Ljava/lang/String;)Lorg/gudy/azureus2/plugins/PluginInterface;
/*     */     //   14: astore 7
/*     */     //   16: aload 7
/*     */     //   18: ifnonnull +13 -> 31
/*     */     //   21: new 229	com/aelitis/azureus/core/devices/TranscodeException
/*     */     //   24: dup
/*     */     //   25: ldc 12
/*     */     //   27: invokespecial 427	com/aelitis/azureus/core/devices/TranscodeException:<init>	(Ljava/lang/String;)V
/*     */     //   30: athrow
/*     */     //   31: iconst_1
/*     */     //   32: anewarray 234	com/aelitis/azureus/core/devices/TranscodeProviderJob
/*     */     //   35: dup
/*     */     //   36: iconst_0
/*     */     //   37: aconst_null
/*     */     //   38: aastore
/*     */     //   39: astore 8
/*     */     //   41: aconst_null
/*     */     //   42: astore 9
/*     */     //   44: aconst_null
/*     */     //   45: astore 10
/*     */     //   47: iload_3
/*     */     //   48: ifeq +94 -> 142
/*     */     //   51: aload 4
/*     */     //   53: instanceof 250
/*     */     //   56: ifeq +11 -> 67
/*     */     //   59: aload 4
/*     */     //   61: checkcast 250	com/aelitis/azureus/core/download/DiskManagerFileInfoURL
/*     */     //   64: invokevirtual 448	com/aelitis/azureus/core/download/DiskManagerFileInfoURL:download	()V
/*     */     //   67: aload 4
/*     */     //   69: invokeinterface 504 1 0
/*     */     //   74: aload 4
/*     */     //   76: invokeinterface 505 1 0
/*     */     //   81: lcmp
/*     */     //   82: ifne +46 -> 128
/*     */     //   85: aload 4
/*     */     //   87: invokeinterface 506 1 0
/*     */     //   92: astore 11
/*     */     //   94: aload 11
/*     */     //   96: invokevirtual 452	java/io/File:exists	()Z
/*     */     //   99: ifeq +29 -> 128
/*     */     //   102: aload 11
/*     */     //   104: invokevirtual 450	java/io/File:length	()J
/*     */     //   107: aload 4
/*     */     //   109: invokeinterface 505 1 0
/*     */     //   114: lcmp
/*     */     //   115: ifne +13 -> 128
/*     */     //   118: aload 11
/*     */     //   120: invokevirtual 456	java/io/File:toURI	()Ljava/net/URI;
/*     */     //   123: invokevirtual 470	java/net/URI:toURL	()Ljava/net/URL;
/*     */     //   126: astore 9
/*     */     //   128: aload 9
/*     */     //   130: ifnonnull +12 -> 142
/*     */     //   133: aload_0
/*     */     //   134: getfield 424	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze:manager	Lcom/aelitis/azureus/core/devices/impl/TranscodeManagerImpl;
/*     */     //   137: ldc 9
/*     */     //   139: invokevirtual 429	com/aelitis/azureus/core/devices/impl/TranscodeManagerImpl:log	(Ljava/lang/String;)V
/*     */     //   142: aload 9
/*     */     //   144: ifnonnull +212 -> 356
/*     */     //   147: aload 4
/*     */     //   149: instanceof 250
/*     */     //   152: ifeq +16 -> 168
/*     */     //   155: aload 4
/*     */     //   157: checkcast 250	com/aelitis/azureus/core/download/DiskManagerFileInfoURL
/*     */     //   160: invokevirtual 449	com/aelitis/azureus/core/download/DiskManagerFileInfoURL:getURL	()Ljava/net/URL;
/*     */     //   163: astore 9
/*     */     //   165: goto +191 -> 356
/*     */     //   168: aload 7
/*     */     //   170: invokeinterface 503 1 0
/*     */     //   175: astore 11
/*     */     //   177: aload 11
/*     */     //   179: ldc 21
/*     */     //   181: iconst_1
/*     */     //   182: anewarray 253	java/lang/Object
/*     */     //   185: dup
/*     */     //   186: iconst_0
/*     */     //   187: aload 4
/*     */     //   189: aastore
/*     */     //   190: invokeinterface 507 3 0
/*     */     //   195: checkcast 254	java/lang/String
/*     */     //   198: astore 12
/*     */     //   200: aload 12
/*     */     //   202: ifnull +11 -> 213
/*     */     //   205: aload 12
/*     */     //   207: invokevirtual 460	java/lang/String:length	()I
/*     */     //   210: ifne +95 -> 305
/*     */     //   213: aload 4
/*     */     //   215: invokeinterface 506 1 0
/*     */     //   220: astore 13
/*     */     //   222: aload 13
/*     */     //   224: invokevirtual 452	java/io/File:exists	()Z
/*     */     //   227: ifeq +65 -> 292
/*     */     //   230: new 238	com/aelitis/azureus/core/devices/impl/TranscodePipeFileSource
/*     */     //   233: dup
/*     */     //   234: aload 13
/*     */     //   236: new 245	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze$4
/*     */     //   239: dup
/*     */     //   240: aload_0
/*     */     //   241: aload_1
/*     */     //   242: aload 8
/*     */     //   244: invokespecial 443	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze$4:<init>	(Lcom/aelitis/azureus/core/devices/impl/TranscodeProviderVuze;Lcom/aelitis/azureus/core/devices/TranscodeProviderAdapter;[Lcom/aelitis/azureus/core/devices/TranscodeProviderJob;)V
/*     */     //   247: invokespecial 432	com/aelitis/azureus/core/devices/impl/TranscodePipeFileSource:<init>	(Ljava/io/File;Lcom/aelitis/azureus/core/devices/impl/TranscodePipe$errorListener;)V
/*     */     //   250: astore 10
/*     */     //   252: new 259	java/net/URL
/*     */     //   255: dup
/*     */     //   256: new 255	java/lang/StringBuilder
/*     */     //   259: dup
/*     */     //   260: invokespecial 464	java/lang/StringBuilder:<init>	()V
/*     */     //   263: ldc 23
/*     */     //   265: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   268: aload 10
/*     */     //   270: invokevirtual 430	com/aelitis/azureus/core/devices/impl/TranscodePipe:getPort	()I
/*     */     //   273: invokevirtual 466	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   276: ldc 3
/*     */     //   278: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   281: invokevirtual 465	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   284: invokespecial 474	java/net/URL:<init>	(Ljava/lang/String;)V
/*     */     //   287: astore 9
/*     */     //   289: goto +13 -> 302
/*     */     //   292: new 229	com/aelitis/azureus/core/devices/TranscodeException
/*     */     //   295: dup
/*     */     //   296: ldc 13
/*     */     //   298: invokespecial 427	com/aelitis/azureus/core/devices/TranscodeException:<init>	(Ljava/lang/String;)V
/*     */     //   301: athrow
/*     */     //   302: goto +54 -> 356
/*     */     //   305: new 259	java/net/URL
/*     */     //   308: dup
/*     */     //   309: aload 12
/*     */     //   311: invokespecial 474	java/net/URL:<init>	(Ljava/lang/String;)V
/*     */     //   314: astore 9
/*     */     //   316: new 239	com/aelitis/azureus/core/devices/impl/TranscodePipeStreamSource
/*     */     //   319: dup
/*     */     //   320: aload 9
/*     */     //   322: invokevirtual 472	java/net/URL:getHost	()Ljava/lang/String;
/*     */     //   325: aload 9
/*     */     //   327: invokevirtual 471	java/net/URL:getPort	()I
/*     */     //   330: invokespecial 433	com/aelitis/azureus/core/devices/impl/TranscodePipeStreamSource:<init>	(Ljava/lang/String;I)V
/*     */     //   333: astore 10
/*     */     //   335: aload 9
/*     */     //   337: ldc 4
/*     */     //   339: invokestatic 479	org/gudy/azureus2/core3/util/UrlUtils:setHost	(Ljava/net/URL;Ljava/lang/String;)Ljava/net/URL;
/*     */     //   342: astore 9
/*     */     //   344: aload 9
/*     */     //   346: aload 10
/*     */     //   348: invokevirtual 430	com/aelitis/azureus/core/devices/impl/TranscodePipe:getPort	()I
/*     */     //   351: invokestatic 478	org/gudy/azureus2/core3/util/UrlUtils:setPort	(Ljava/net/URL;I)Ljava/net/URL;
/*     */     //   354: astore 9
/*     */     //   356: aload 10
/*     */     //   358: astore 11
/*     */     //   360: aload_0
/*     */     //   361: getfield 426	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze:plugin_interface	Lorg/gudy/azureus2/plugins/PluginInterface;
/*     */     //   364: invokeinterface 503 1 0
/*     */     //   369: astore 12
/*     */     //   371: aload 6
/*     */     //   373: invokevirtual 473	java/net/URL:getProtocol	()Ljava/lang/String;
/*     */     //   376: ldc 24
/*     */     //   378: invokevirtual 461	java/lang/String:equals	(Ljava/lang/Object;)Z
/*     */     //   381: ifeq +62 -> 443
/*     */     //   384: aload_1
/*     */     //   385: astore 14
/*     */     //   387: aload 12
/*     */     //   389: ldc 27
/*     */     //   391: iconst_4
/*     */     //   392: anewarray 253	java/lang/Object
/*     */     //   395: dup
/*     */     //   396: iconst_0
/*     */     //   397: aload_2
/*     */     //   398: checkcast 249	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze$TranscodeProviderAnalysisImpl
/*     */     //   401: invokeinterface 484 1 0
/*     */     //   406: aastore
/*     */     //   407: dup
/*     */     //   408: iconst_1
/*     */     //   409: aload 9
/*     */     //   411: aastore
/*     */     //   412: dup
/*     */     //   413: iconst_2
/*     */     //   414: aload 5
/*     */     //   416: invokeinterface 482 1 0
/*     */     //   421: aastore
/*     */     //   422: dup
/*     */     //   423: iconst_3
/*     */     //   424: aload 6
/*     */     //   426: invokevirtual 471	java/net/URL:getPort	()I
/*     */     //   429: invokestatic 458	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*     */     //   432: aastore
/*     */     //   433: invokeinterface 507 3 0
/*     */     //   438: astore 13
/*     */     //   440: goto +180 -> 620
/*     */     //   443: new 251	java/io/File
/*     */     //   446: dup
/*     */     //   447: aload 6
/*     */     //   449: invokevirtual 475	java/net/URL:toURI	()Ljava/net/URI;
/*     */     //   452: invokespecial 457	java/io/File:<init>	(Ljava/net/URI;)V
/*     */     //   455: astore 15
/*     */     //   457: aload 15
/*     */     //   459: invokevirtual 454	java/io/File:getParentFile	()Ljava/io/File;
/*     */     //   462: astore 16
/*     */     //   464: aload 16
/*     */     //   466: invokevirtual 452	java/io/File:exists	()Z
/*     */     //   469: ifeq +47 -> 516
/*     */     //   472: aload 16
/*     */     //   474: invokevirtual 451	java/io/File:canWrite	()Z
/*     */     //   477: ifne +83 -> 560
/*     */     //   480: new 229	com/aelitis/azureus/core/devices/TranscodeException
/*     */     //   483: dup
/*     */     //   484: new 255	java/lang/StringBuilder
/*     */     //   487: dup
/*     */     //   488: invokespecial 464	java/lang/StringBuilder:<init>	()V
/*     */     //   491: ldc 11
/*     */     //   493: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   496: aload 16
/*     */     //   498: invokevirtual 455	java/io/File:getAbsolutePath	()Ljava/lang/String;
/*     */     //   501: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   504: ldc 2
/*     */     //   506: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   509: invokevirtual 465	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   512: invokespecial 427	com/aelitis/azureus/core/devices/TranscodeException:<init>	(Ljava/lang/String;)V
/*     */     //   515: athrow
/*     */     //   516: aload 16
/*     */     //   518: invokevirtual 453	java/io/File:mkdirs	()Z
/*     */     //   521: ifne +39 -> 560
/*     */     //   524: new 229	com/aelitis/azureus/core/devices/TranscodeException
/*     */     //   527: dup
/*     */     //   528: new 255	java/lang/StringBuilder
/*     */     //   531: dup
/*     */     //   532: invokespecial 464	java/lang/StringBuilder:<init>	()V
/*     */     //   535: ldc 8
/*     */     //   537: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   540: aload 16
/*     */     //   542: invokevirtual 455	java/io/File:getAbsolutePath	()Ljava/lang/String;
/*     */     //   545: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   548: ldc 1
/*     */     //   550: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   553: invokevirtual 465	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   556: invokespecial 427	com/aelitis/azureus/core/devices/TranscodeException:<init>	(Ljava/lang/String;)V
/*     */     //   559: athrow
/*     */     //   560: new 246	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze$5
/*     */     //   563: dup
/*     */     //   564: aload_0
/*     */     //   565: aload_1
/*     */     //   566: aload 15
/*     */     //   568: invokespecial 444	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze$5:<init>	(Lcom/aelitis/azureus/core/devices/impl/TranscodeProviderVuze;Lcom/aelitis/azureus/core/devices/TranscodeProviderAdapter;Ljava/io/File;)V
/*     */     //   571: astore 14
/*     */     //   573: aload 12
/*     */     //   575: ldc 26
/*     */     //   577: iconst_4
/*     */     //   578: anewarray 253	java/lang/Object
/*     */     //   581: dup
/*     */     //   582: iconst_0
/*     */     //   583: aload_2
/*     */     //   584: checkcast 249	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze$TranscodeProviderAnalysisImpl
/*     */     //   587: invokeinterface 484 1 0
/*     */     //   592: aastore
/*     */     //   593: dup
/*     */     //   594: iconst_1
/*     */     //   595: aload 9
/*     */     //   597: aastore
/*     */     //   598: dup
/*     */     //   599: iconst_2
/*     */     //   600: aload 5
/*     */     //   602: invokeinterface 482 1 0
/*     */     //   607: aastore
/*     */     //   608: dup
/*     */     //   609: iconst_3
/*     */     //   610: aload 15
/*     */     //   612: aastore
/*     */     //   613: invokeinterface 507 3 0
/*     */     //   618: astore 13
/*     */     //   620: new 247	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze$6
/*     */     //   623: dup
/*     */     //   624: aload_0
/*     */     //   625: ldc 29
/*     */     //   627: iconst_1
/*     */     //   628: aload 11
/*     */     //   630: aload 14
/*     */     //   632: aload 12
/*     */     //   634: aload 13
/*     */     //   636: invokespecial 446	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze$6:<init>	(Lcom/aelitis/azureus/core/devices/impl/TranscodeProviderVuze;Ljava/lang/String;ZLcom/aelitis/azureus/core/devices/impl/TranscodePipe;Lcom/aelitis/azureus/core/devices/TranscodeProviderAdapter;Lorg/gudy/azureus2/plugins/ipc/IPCInterface;Ljava/lang/Object;)V
/*     */     //   639: invokevirtual 445	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze$6:start	()V
/*     */     //   642: aload 8
/*     */     //   644: iconst_0
/*     */     //   645: new 248	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze$7
/*     */     //   648: dup
/*     */     //   649: aload_0
/*     */     //   650: aload 11
/*     */     //   652: aload 12
/*     */     //   654: aload 13
/*     */     //   656: invokespecial 447	com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze$7:<init>	(Lcom/aelitis/azureus/core/devices/impl/TranscodeProviderVuze;Lcom/aelitis/azureus/core/devices/impl/TranscodePipe;Lorg/gudy/azureus2/plugins/ipc/IPCInterface;Ljava/lang/Object;)V
/*     */     //   659: aastore
/*     */     //   660: aload 8
/*     */     //   662: iconst_0
/*     */     //   663: aaload
/*     */     //   664: areturn
/*     */     //   665: astore 12
/*     */     //   667: aload 10
/*     */     //   669: ifnull +9 -> 678
/*     */     //   672: aload 10
/*     */     //   674: invokevirtual 431	com/aelitis/azureus/core/devices/impl/TranscodePipe:destroy	()Z
/*     */     //   677: pop
/*     */     //   678: aload 12
/*     */     //   680: athrow
/*     */     //   681: astore 7
/*     */     //   683: aload 7
/*     */     //   685: athrow
/*     */     //   686: astore 7
/*     */     //   688: new 229	com/aelitis/azureus/core/devices/TranscodeException
/*     */     //   691: dup
/*     */     //   692: ldc 25
/*     */     //   694: aload 7
/*     */     //   696: invokespecial 428	com/aelitis/azureus/core/devices/TranscodeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   699: athrow
/*     */     // Line number table:
/*     */     //   Java source line #566	-> byte code offset #0
/*     */     //   Java source line #568	-> byte code offset #16
/*     */     //   Java source line #570	-> byte code offset #21
/*     */     //   Java source line #573	-> byte code offset #31
/*     */     //   Java source line #575	-> byte code offset #41
/*     */     //   Java source line #576	-> byte code offset #44
/*     */     //   Java source line #578	-> byte code offset #47
/*     */     //   Java source line #580	-> byte code offset #51
/*     */     //   Java source line #582	-> byte code offset #59
/*     */     //   Java source line #585	-> byte code offset #67
/*     */     //   Java source line #587	-> byte code offset #85
/*     */     //   Java source line #589	-> byte code offset #94
/*     */     //   Java source line #591	-> byte code offset #118
/*     */     //   Java source line #595	-> byte code offset #128
/*     */     //   Java source line #597	-> byte code offset #133
/*     */     //   Java source line #601	-> byte code offset #142
/*     */     //   Java source line #603	-> byte code offset #147
/*     */     //   Java source line #605	-> byte code offset #155
/*     */     //   Java source line #609	-> byte code offset #168
/*     */     //   Java source line #611	-> byte code offset #177
/*     */     //   Java source line #614	-> byte code offset #200
/*     */     //   Java source line #618	-> byte code offset #213
/*     */     //   Java source line #620	-> byte code offset #222
/*     */     //   Java source line #622	-> byte code offset #230
/*     */     //   Java source line #641	-> byte code offset #252
/*     */     //   Java source line #645	-> byte code offset #292
/*     */     //   Java source line #647	-> byte code offset #302
/*     */     //   Java source line #649	-> byte code offset #305
/*     */     //   Java source line #651	-> byte code offset #316
/*     */     //   Java source line #653	-> byte code offset #335
/*     */     //   Java source line #655	-> byte code offset #344
/*     */     //   Java source line #660	-> byte code offset #356
/*     */     //   Java source line #663	-> byte code offset #360
/*     */     //   Java source line #669	-> byte code offset #371
/*     */     //   Java source line #671	-> byte code offset #384
/*     */     //   Java source line #673	-> byte code offset #387
/*     */     //   Java source line #683	-> byte code offset #443
/*     */     //   Java source line #685	-> byte code offset #457
/*     */     //   Java source line #687	-> byte code offset #464
/*     */     //   Java source line #689	-> byte code offset #472
/*     */     //   Java source line #691	-> byte code offset #480
/*     */     //   Java source line #695	-> byte code offset #516
/*     */     //   Java source line #697	-> byte code offset #524
/*     */     //   Java source line #701	-> byte code offset #560
/*     */     //   Java source line #742	-> byte code offset #573
/*     */     //   Java source line #752	-> byte code offset #620
/*     */     //   Java source line #835	-> byte code offset #642
/*     */     //   Java source line #879	-> byte code offset #660
/*     */     //   Java source line #881	-> byte code offset #665
/*     */     //   Java source line #883	-> byte code offset #667
/*     */     //   Java source line #885	-> byte code offset #672
/*     */     //   Java source line #888	-> byte code offset #678
/*     */     //   Java source line #890	-> byte code offset #681
/*     */     //   Java source line #892	-> byte code offset #683
/*     */     //   Java source line #894	-> byte code offset #686
/*     */     //   Java source line #896	-> byte code offset #688
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	700	0	this	TranscodeProviderVuze
/*     */     //   0	700	1	_adapter	TranscodeProviderAdapter
/*     */     //   0	700	2	analysis	TranscodeProviderAnalysis
/*     */     //   0	700	3	direct_input	boolean
/*     */     //   0	700	4	input	org.gudy.azureus2.plugins.disk.DiskManagerFileInfo
/*     */     //   0	700	5	profile	TranscodeProfile
/*     */     //   0	700	6	output	java.net.URL
/*     */     //   14	155	7	av_pi	PluginInterface
/*     */     //   681	3	7	e	TranscodeException
/*     */     //   686	9	7	e	Throwable
/*     */     //   39	622	8	xcode_job	TranscodeProviderJob[]
/*     */     //   42	554	9	source_url	java.net.URL
/*     */     //   45	628	10	pipe	TranscodePipe
/*     */     //   92	27	11	file	File
/*     */     //   175	3	11	av_ipc	IPCInterface
/*     */     //   358	293	11	f_pipe	TranscodePipe
/*     */     //   198	112	12	url_str	String
/*     */     //   369	284	12	ipc	IPCInterface
/*     */     //   665	14	12	e	Throwable
/*     */     //   220	15	13	source_file	File
/*     */     //   438	3	13	context	Object
/*     */     //   618	37	13	context	Object
/*     */     //   385	3	14	adapter	TranscodeProviderAdapter
/*     */     //   571	60	14	adapter	TranscodeProviderAdapter
/*     */     //   455	156	15	file	File
/*     */     //   462	79	16	parent_dir	File
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   360	664	665	java/lang/Throwable
/*     */     //   0	664	681	com/aelitis/azureus/core/devices/TranscodeException
/*     */     //   665	681	681	com/aelitis/azureus/core/devices/TranscodeException
/*     */     //   0	664	686	java/lang/Throwable
/*     */     //   665	681	686	java/lang/Throwable
/*     */   }
/*     */   
/*     */   public File getAssetDirectory()
/*     */   {
/* 903 */     File file = this.plugin_interface.getPluginconfig().getPluginUserFile("assets");
/*     */     
/* 905 */     if (!file.exists())
/*     */     {
/* 907 */       file.mkdirs();
/*     */     }
/*     */     
/* 910 */     return file;
/*     */   }
/*     */   
/*     */   protected void destroy() {}
/*     */   
/*     */   protected static abstract interface TranscodeProviderAnalysisImpl
/*     */     extends TranscodeProviderAnalysis
/*     */   {
/*     */     public abstract Map<String, Object> getResult();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/TranscodeProviderVuze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */