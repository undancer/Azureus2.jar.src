/*    */ package com.aelitis.azureus.core.networkmanager.admin.impl;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminException;
/*    */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminProgressListener;
/*    */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
/*    */ import java.net.InetAddress;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NetworkAdminUDPTester
/*    */   implements NetworkAdminProtocolTester
/*    */ {
/*    */   public static final String UDP_SERVER_ADDRESS = "nettest.vuze.com";
/*    */   public static final int UDP_SERVER_PORT = 2081;
/*    */   private final AzureusCore core;
/*    */   private final NetworkAdminProgressListener listener;
/*    */   
/*    */   protected NetworkAdminUDPTester(AzureusCore _core, NetworkAdminProgressListener _listener)
/*    */   {
/* 64 */     this.core = _core;
/* 65 */     this.listener = _listener;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public InetAddress testOutbound(InetAddress bind_ip, int bind_port)
/*    */     throws NetworkAdminException
/*    */   {
/*    */     try
/*    */     {
/* 76 */       return VersionCheckClient.getSingleton().getExternalIpAddressUDP(bind_ip, bind_port, false);
/*    */     }
/*    */     catch (Throwable e)
/*    */     {
/* 80 */       throw new NetworkAdminException("Outbound test failed", e);
/*    */     }
/*    */   }
/*    */   
/*    */   /* Error */
/*    */   public InetAddress testInbound(InetAddress bind_ip, int bind_port)
/*    */     throws NetworkAdminException
/*    */   {
/*    */     // Byte code:
/*    */     //   0: iload_2
/*    */     //   1: invokestatic 274	com/aelitis/net/udp/uc/PRUDPPacketHandlerFactory:getReleasableHandler	(I)Lcom/aelitis/net/udp/uc/PRUDPReleasablePacketHandler;
/*    */     //   4: astore_3
/*    */     //   5: aload_3
/*    */     //   6: invokeinterface 300 1 0
/*    */     //   11: astore 4
/*    */     //   13: new 169	java/util/HashMap
/*    */     //   16: dup
/*    */     //   17: invokespecial 285	java/util/HashMap:<init>	()V
/*    */     //   20: astore 5
/*    */     //   22: aload_0
/*    */     //   23: getfield 260	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminUDPTester:core	Lcom/aelitis/azureus/core/AzureusCore;
/*    */     //   26: invokeinterface 292 1 0
/*    */     //   31: ldc_w 157
/*    */     //   34: invokevirtual 291	org/gudy/azureus2/plugins/PluginManager:getPluginInterfaceByClass	(Ljava/lang/Class;)Lorg/gudy/azureus2/plugins/PluginInterface;
/*    */     //   37: astore 6
/*    */     //   39: aconst_null
/*    */     //   40: astore 7
/*    */     //   42: aload 6
/*    */     //   44: ifnull +96 -> 140
/*    */     //   47: aload 6
/*    */     //   49: invokeinterface 302 1 0
/*    */     //   54: checkcast 157	com/aelitis/azureus/plugins/upnp/UPnPPlugin
/*    */     //   57: astore 8
/*    */     //   59: aload 8
/*    */     //   61: invokevirtual 272	com/aelitis/azureus/plugins/upnp/UPnPPlugin:getServices	()[Lcom/aelitis/azureus/plugins/upnp/UPnPPluginService;
/*    */     //   64: astore 9
/*    */     //   66: aload 9
/*    */     //   68: arraylength
/*    */     //   69: ifle +71 -> 140
/*    */     //   72: ldc 1
/*    */     //   74: astore 7
/*    */     //   76: iconst_0
/*    */     //   77: istore 10
/*    */     //   79: iload 10
/*    */     //   81: aload 9
/*    */     //   83: arraylength
/*    */     //   84: if_icmpge +56 -> 140
/*    */     //   87: aload 9
/*    */     //   89: iload 10
/*    */     //   91: aaload
/*    */     //   92: astore 11
/*    */     //   94: new 165	java/lang/StringBuilder
/*    */     //   97: dup
/*    */     //   98: invokespecial 279	java/lang/StringBuilder:<init>	()V
/*    */     //   101: aload 7
/*    */     //   103: invokevirtual 282	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*    */     //   106: iload 10
/*    */     //   108: ifne +8 -> 116
/*    */     //   111: ldc 1
/*    */     //   113: goto +5 -> 118
/*    */     //   116: ldc 3
/*    */     //   118: invokevirtual 282	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*    */     //   121: aload 11
/*    */     //   123: invokevirtual 273	com/aelitis/azureus/plugins/upnp/UPnPPluginService:getInfo	()Ljava/lang/String;
/*    */     //   126: invokevirtual 282	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*    */     //   129: invokevirtual 280	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*    */     //   132: astore 7
/*    */     //   134: iinc 10 1
/*    */     //   137: goto -58 -> 79
/*    */     //   140: aload 7
/*    */     //   142: ifnull +13 -> 155
/*    */     //   145: aload 5
/*    */     //   147: ldc 19
/*    */     //   149: aload 7
/*    */     //   151: invokevirtual 286	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*    */     //   154: pop
/*    */     //   155: invokestatic 262	com/aelitis/azureus/core/networkmanager/admin/NetworkAdmin:getSingleton	()Lcom/aelitis/azureus/core/networkmanager/admin/NetworkAdmin;
/*    */     //   158: invokevirtual 263	com/aelitis/azureus/core/networkmanager/admin/NetworkAdmin:getCurrentASN	()Lcom/aelitis/azureus/core/networkmanager/admin/NetworkAdminASN;
/*    */     //   161: astore 8
/*    */     //   163: aload 8
/*    */     //   165: invokeinterface 293 1 0
/*    */     //   170: astore 9
/*    */     //   172: aload 8
/*    */     //   174: invokeinterface 294 1 0
/*    */     //   179: astore 10
/*    */     //   181: aload 9
/*    */     //   183: invokevirtual 277	java/lang/String:length	()I
/*    */     //   186: ifle +13 -> 199
/*    */     //   189: aload 5
/*    */     //   191: ldc 11
/*    */     //   193: aload 9
/*    */     //   195: invokevirtual 286	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*    */     //   198: pop
/*    */     //   199: aload 10
/*    */     //   201: invokevirtual 277	java/lang/String:length	()I
/*    */     //   204: ifle +13 -> 217
/*    */     //   207: aload 5
/*    */     //   209: ldc 12
/*    */     //   211: aload 10
/*    */     //   213: invokevirtual 286	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*    */     //   216: pop
/*    */     //   217: aload 5
/*    */     //   219: ldc 15
/*    */     //   221: invokestatic 290	org/gudy/azureus2/core3/internat/MessageText:getCurrentLocale	()Ljava/util/Locale;
/*    */     //   224: invokevirtual 287	java/util/Locale:toString	()Ljava/lang/String;
/*    */     //   227: invokevirtual 286	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*    */     //   230: pop
/*    */     //   231: new 172	java/util/Random
/*    */     //   234: dup
/*    */     //   235: invokespecial 289	java/util/Random:<init>	()V
/*    */     //   238: astore 11
/*    */     //   240: aload 5
/*    */     //   242: ldc 13
/*    */     //   244: new 162	java/lang/Long
/*    */     //   247: dup
/*    */     //   248: aload 11
/*    */     //   250: invokevirtual 288	java/util/Random:nextLong	()J
/*    */     //   253: invokespecial 275	java/lang/Long:<init>	(J)V
/*    */     //   256: invokevirtual 286	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*    */     //   259: pop
/*    */     //   260: aload 4
/*    */     //   262: aload_1
/*    */     //   263: invokeinterface 296 2 0
/*    */     //   268: aconst_null
/*    */     //   269: astore 12
/*    */     //   271: ldc2_w 142
/*    */     //   274: lstore 13
/*    */     //   276: ldc2_w 142
/*    */     //   279: lstore 15
/*    */     //   281: iconst_0
/*    */     //   282: istore 17
/*    */     //   284: iload 17
/*    */     //   286: iconst_3
/*    */     //   287: if_icmpge +336 -> 623
/*    */     //   290: aload 5
/*    */     //   292: ldc 18
/*    */     //   294: new 162	java/lang/Long
/*    */     //   297: dup
/*    */     //   298: iload 17
/*    */     //   300: i2l
/*    */     //   301: invokespecial 275	java/lang/Long:<init>	(J)V
/*    */     //   304: invokevirtual 286	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*    */     //   307: pop
/*    */     //   308: ldc2_w 138
/*    */     //   311: aload 11
/*    */     //   313: invokevirtual 288	java/util/Random:nextLong	()J
/*    */     //   316: lor
/*    */     //   317: lstore 18
/*    */     //   319: new 153	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminNATUDPRequest
/*    */     //   322: dup
/*    */     //   323: lload 18
/*    */     //   325: invokespecial 268	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminNATUDPRequest:<init>	(J)V
/*    */     //   328: astore 20
/*    */     //   330: aload 20
/*    */     //   332: aload 5
/*    */     //   334: invokevirtual 269	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminNATUDPRequest:setPayload	(Ljava/util/Map;)V
/*    */     //   337: aload_0
/*    */     //   338: getfield 261	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminUDPTester:listener	Lcom/aelitis/azureus/core/networkmanager/admin/NetworkAdminProgressListener;
/*    */     //   341: ifnull +37 -> 378
/*    */     //   344: aload_0
/*    */     //   345: getfield 261	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminUDPTester:listener	Lcom/aelitis/azureus/core/networkmanager/admin/NetworkAdminProgressListener;
/*    */     //   348: new 165	java/lang/StringBuilder
/*    */     //   351: dup
/*    */     //   352: invokespecial 279	java/lang/StringBuilder:<init>	()V
/*    */     //   355: ldc 8
/*    */     //   357: invokevirtual 282	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*    */     //   360: lload 13
/*    */     //   362: invokevirtual 281	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
/*    */     //   365: ldc 2
/*    */     //   367: invokevirtual 282	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*    */     //   370: invokevirtual 280	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*    */     //   373: invokeinterface 295 2 0
/*    */     //   378: aload 4
/*    */     //   380: aconst_null
/*    */     //   381: aload 20
/*    */     //   383: new 168	java/net/InetSocketAddress
/*    */     //   386: dup
/*    */     //   387: ldc 16
/*    */     //   389: sipush 2081
/*    */     //   392: invokespecial 284	java/net/InetSocketAddress:<init>	(Ljava/lang/String;I)V
/*    */     //   395: lload 13
/*    */     //   397: bipush 99
/*    */     //   399: invokeinterface 298 7 0
/*    */     //   404: checkcast 152	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminNATUDPReply
/*    */     //   407: astore 21
/*    */     //   409: aload 21
/*    */     //   411: invokevirtual 267	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminNATUDPReply:getPayload	()Ljava/util/Map;
/*    */     //   414: astore 22
/*    */     //   416: aload 22
/*    */     //   418: ldc 14
/*    */     //   420: invokeinterface 301 2 0
/*    */     //   425: checkcast 144	[B
/*    */     //   428: checkcast 144	[B
/*    */     //   431: astore 23
/*    */     //   433: aload 23
/*    */     //   435: ifnonnull +13 -> 448
/*    */     //   438: new 149	com/aelitis/azureus/core/networkmanager/admin/NetworkAdminException
/*    */     //   441: dup
/*    */     //   442: ldc 4
/*    */     //   444: invokespecial 264	com/aelitis/azureus/core/networkmanager/admin/NetworkAdminException:<init>	(Ljava/lang/String;)V
/*    */     //   447: athrow
/*    */     //   448: aload 22
/*    */     //   450: ldc 17
/*    */     //   452: invokeinterface 301 2 0
/*    */     //   457: checkcast 144	[B
/*    */     //   460: checkcast 144	[B
/*    */     //   463: astore 24
/*    */     //   465: aload 24
/*    */     //   467: ifnull +22 -> 489
/*    */     //   470: new 149	com/aelitis/azureus/core/networkmanager/admin/NetworkAdminException
/*    */     //   473: dup
/*    */     //   474: new 164	java/lang/String
/*    */     //   477: dup
/*    */     //   478: aload 24
/*    */     //   480: ldc 10
/*    */     //   482: invokespecial 278	java/lang/String:<init>	([BLjava/lang/String;)V
/*    */     //   485: invokespecial 264	com/aelitis/azureus/core/networkmanager/admin/NetworkAdminException:<init>	(Ljava/lang/String;)V
/*    */     //   488: athrow
/*    */     //   489: aload 23
/*    */     //   491: invokestatic 283	java/net/InetAddress:getByAddress	([B)Ljava/net/InetAddress;
/*    */     //   494: astore 25
/*    */     //   496: aload 5
/*    */     //   498: ldc 18
/*    */     //   500: new 162	java/lang/Long
/*    */     //   503: dup
/*    */     //   504: ldc2_w 140
/*    */     //   507: invokespecial 275	java/lang/Long:<init>	(J)V
/*    */     //   510: invokevirtual 286	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*    */     //   513: pop
/*    */     //   514: ldc2_w 138
/*    */     //   517: aload 11
/*    */     //   519: invokevirtual 288	java/util/Random:nextLong	()J
/*    */     //   522: lor
/*    */     //   523: lstore 26
/*    */     //   525: new 153	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminNATUDPRequest
/*    */     //   528: dup
/*    */     //   529: lload 26
/*    */     //   531: invokespecial 268	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminNATUDPRequest:<init>	(J)V
/*    */     //   534: astore 28
/*    */     //   536: aload 28
/*    */     //   538: aload 5
/*    */     //   540: invokevirtual 269	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminNATUDPRequest:setPayload	(Ljava/util/Map;)V
/*    */     //   543: aload_0
/*    */     //   544: getfield 261	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminUDPTester:listener	Lcom/aelitis/azureus/core/networkmanager/admin/NetworkAdminProgressListener;
/*    */     //   547: ifnull +14 -> 561
/*    */     //   550: aload_0
/*    */     //   551: getfield 261	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminUDPTester:listener	Lcom/aelitis/azureus/core/networkmanager/admin/NetworkAdminProgressListener;
/*    */     //   554: ldc 7
/*    */     //   556: invokeinterface 295 2 0
/*    */     //   561: aload 4
/*    */     //   563: aload 28
/*    */     //   565: new 168	java/net/InetSocketAddress
/*    */     //   568: dup
/*    */     //   569: ldc 16
/*    */     //   571: sipush 2081
/*    */     //   574: invokespecial 284	java/net/InetSocketAddress:<init>	(Ljava/lang/String;I)V
/*    */     //   577: invokeinterface 297 3 0
/*    */     //   582: goto +5 -> 587
/*    */     //   585: astore 26
/*    */     //   587: aload 4
/*    */     //   589: aconst_null
/*    */     //   590: invokeinterface 296 2 0
/*    */     //   595: aload_3
/*    */     //   596: invokeinterface 299 1 0
/*    */     //   601: aload 25
/*    */     //   603: areturn
/*    */     //   604: astore 18
/*    */     //   606: aload 18
/*    */     //   608: astore 12
/*    */     //   610: lload 13
/*    */     //   612: lload 15
/*    */     //   614: ladd
/*    */     //   615: lstore 13
/*    */     //   617: iinc 17 1
/*    */     //   620: goto -336 -> 284
/*    */     //   623: aload 12
/*    */     //   625: ifnull +6 -> 631
/*    */     //   628: aload 12
/*    */     //   630: athrow
/*    */     //   631: new 149	com/aelitis/azureus/core/networkmanager/admin/NetworkAdminException
/*    */     //   634: dup
/*    */     //   635: ldc 9
/*    */     //   637: invokespecial 264	com/aelitis/azureus/core/networkmanager/admin/NetworkAdminException:<init>	(Ljava/lang/String;)V
/*    */     //   640: athrow
/*    */     //   641: astore 29
/*    */     //   643: aload 5
/*    */     //   645: ldc 18
/*    */     //   647: new 162	java/lang/Long
/*    */     //   650: dup
/*    */     //   651: ldc2_w 140
/*    */     //   654: invokespecial 275	java/lang/Long:<init>	(J)V
/*    */     //   657: invokevirtual 286	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*    */     //   660: pop
/*    */     //   661: ldc2_w 138
/*    */     //   664: aload 11
/*    */     //   666: invokevirtual 288	java/util/Random:nextLong	()J
/*    */     //   669: lor
/*    */     //   670: lstore 30
/*    */     //   672: new 153	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminNATUDPRequest
/*    */     //   675: dup
/*    */     //   676: lload 30
/*    */     //   678: invokespecial 268	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminNATUDPRequest:<init>	(J)V
/*    */     //   681: astore 32
/*    */     //   683: aload 32
/*    */     //   685: aload 5
/*    */     //   687: invokevirtual 269	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminNATUDPRequest:setPayload	(Ljava/util/Map;)V
/*    */     //   690: aload_0
/*    */     //   691: getfield 261	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminUDPTester:listener	Lcom/aelitis/azureus/core/networkmanager/admin/NetworkAdminProgressListener;
/*    */     //   694: ifnull +14 -> 708
/*    */     //   697: aload_0
/*    */     //   698: getfield 261	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminUDPTester:listener	Lcom/aelitis/azureus/core/networkmanager/admin/NetworkAdminProgressListener;
/*    */     //   701: ldc 7
/*    */     //   703: invokeinterface 295 2 0
/*    */     //   708: aload 4
/*    */     //   710: aload 32
/*    */     //   712: new 168	java/net/InetSocketAddress
/*    */     //   715: dup
/*    */     //   716: ldc 16
/*    */     //   718: sipush 2081
/*    */     //   721: invokespecial 284	java/net/InetSocketAddress:<init>	(Ljava/lang/String;I)V
/*    */     //   724: invokeinterface 297 3 0
/*    */     //   729: goto +5 -> 734
/*    */     //   732: astore 30
/*    */     //   734: aload 29
/*    */     //   736: athrow
/*    */     //   737: astore 12
/*    */     //   739: aload 12
/*    */     //   741: athrow
/*    */     //   742: astore 12
/*    */     //   744: new 149	com/aelitis/azureus/core/networkmanager/admin/NetworkAdminException
/*    */     //   747: dup
/*    */     //   748: ldc 5
/*    */     //   750: aload 12
/*    */     //   752: invokespecial 265	com/aelitis/azureus/core/networkmanager/admin/NetworkAdminException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*    */     //   755: athrow
/*    */     //   756: astore 33
/*    */     //   758: aload 4
/*    */     //   760: aconst_null
/*    */     //   761: invokeinterface 296 2 0
/*    */     //   766: aload_3
/*    */     //   767: invokeinterface 299 1 0
/*    */     //   772: aload 33
/*    */     //   774: athrow
/*    */     // Line number table:
/*    */     //   Java source line #91	-> byte code offset #0
/*    */     //   Java source line #93	-> byte code offset #5
/*    */     //   Java source line #95	-> byte code offset #13
/*    */     //   Java source line #97	-> byte code offset #22
/*    */     //   Java source line #99	-> byte code offset #39
/*    */     //   Java source line #101	-> byte code offset #42
/*    */     //   Java source line #103	-> byte code offset #47
/*    */     //   Java source line #125	-> byte code offset #59
/*    */     //   Java source line #127	-> byte code offset #66
/*    */     //   Java source line #129	-> byte code offset #72
/*    */     //   Java source line #131	-> byte code offset #76
/*    */     //   Java source line #133	-> byte code offset #87
/*    */     //   Java source line #135	-> byte code offset #94
/*    */     //   Java source line #131	-> byte code offset #134
/*    */     //   Java source line #140	-> byte code offset #140
/*    */     //   Java source line #142	-> byte code offset #145
/*    */     //   Java source line #145	-> byte code offset #155
/*    */     //   Java source line #147	-> byte code offset #163
/*    */     //   Java source line #148	-> byte code offset #172
/*    */     //   Java source line #150	-> byte code offset #181
/*    */     //   Java source line #152	-> byte code offset #189
/*    */     //   Java source line #155	-> byte code offset #199
/*    */     //   Java source line #157	-> byte code offset #207
/*    */     //   Java source line #160	-> byte code offset #217
/*    */     //   Java source line #162	-> byte code offset #231
/*    */     //   Java source line #164	-> byte code offset #240
/*    */     //   Java source line #167	-> byte code offset #260
/*    */     //   Java source line #169	-> byte code offset #268
/*    */     //   Java source line #171	-> byte code offset #271
/*    */     //   Java source line #172	-> byte code offset #276
/*    */     //   Java source line #175	-> byte code offset #281
/*    */     //   Java source line #177	-> byte code offset #290
/*    */     //   Java source line #184	-> byte code offset #308
/*    */     //   Java source line #186	-> byte code offset #319
/*    */     //   Java source line #188	-> byte code offset #330
/*    */     //   Java source line #190	-> byte code offset #337
/*    */     //   Java source line #192	-> byte code offset #344
/*    */     //   Java source line #195	-> byte code offset #378
/*    */     //   Java source line #203	-> byte code offset #409
/*    */     //   Java source line #205	-> byte code offset #416
/*    */     //   Java source line #207	-> byte code offset #433
/*    */     //   Java source line #209	-> byte code offset #438
/*    */     //   Java source line #212	-> byte code offset #448
/*    */     //   Java source line #214	-> byte code offset #465
/*    */     //   Java source line #216	-> byte code offset #470
/*    */     //   Java source line #219	-> byte code offset #489
/*    */     //   Java source line #239	-> byte code offset #496
/*    */     //   Java source line #241	-> byte code offset #514
/*    */     //   Java source line #243	-> byte code offset #525
/*    */     //   Java source line #245	-> byte code offset #536
/*    */     //   Java source line #249	-> byte code offset #543
/*    */     //   Java source line #250	-> byte code offset #550
/*    */     //   Java source line #253	-> byte code offset #561
/*    */     //   Java source line #256	-> byte code offset #582
/*    */     //   Java source line #255	-> byte code offset #585
/*    */     //   Java source line #268	-> byte code offset #587
/*    */     //   Java source line #270	-> byte code offset #595
/*    */     //   Java source line #221	-> byte code offset #604
/*    */     //   Java source line #223	-> byte code offset #606
/*    */     //   Java source line #225	-> byte code offset #610
/*    */     //   Java source line #175	-> byte code offset #617
/*    */     //   Java source line #229	-> byte code offset #623
/*    */     //   Java source line #231	-> byte code offset #628
/*    */     //   Java source line #234	-> byte code offset #631
/*    */     //   Java source line #238	-> byte code offset #641
/*    */     //   Java source line #239	-> byte code offset #643
/*    */     //   Java source line #241	-> byte code offset #661
/*    */     //   Java source line #243	-> byte code offset #672
/*    */     //   Java source line #245	-> byte code offset #683
/*    */     //   Java source line #249	-> byte code offset #690
/*    */     //   Java source line #250	-> byte code offset #697
/*    */     //   Java source line #253	-> byte code offset #708
/*    */     //   Java source line #256	-> byte code offset #729
/*    */     //   Java source line #255	-> byte code offset #732
/*    */     //   Java source line #256	-> byte code offset #734
/*    */     //   Java source line #258	-> byte code offset #737
/*    */     //   Java source line #260	-> byte code offset #739
/*    */     //   Java source line #262	-> byte code offset #742
/*    */     //   Java source line #264	-> byte code offset #744
/*    */     //   Java source line #268	-> byte code offset #756
/*    */     //   Java source line #270	-> byte code offset #766
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	signature
/*    */     //   0	775	0	this	NetworkAdminUDPTester
/*    */     //   0	775	1	bind_ip	InetAddress
/*    */     //   0	775	2	bind_port	int
/*    */     //   4	763	3	handler	com.aelitis.net.udp.uc.PRUDPReleasablePacketHandler
/*    */     //   11	748	4	packet_handler	com.aelitis.net.udp.uc.PRUDPPacketHandler
/*    */     //   20	666	5	data_to_send	java.util.HashMap
/*    */     //   37	11	6	pi_upnp	org.gudy.azureus2.plugins.PluginInterface
/*    */     //   40	110	7	upnp_str	String
/*    */     //   57	3	8	upnp	com.aelitis.azureus.plugins.upnp.UPnPPlugin
/*    */     //   161	12	8	net_asn	com.aelitis.azureus.core.networkmanager.admin.NetworkAdminASN
/*    */     //   64	24	9	services	com.aelitis.azureus.plugins.upnp.UPnPPluginService[]
/*    */     //   170	24	9	as	String
/*    */     //   77	58	10	i	int
/*    */     //   179	33	10	asn	String
/*    */     //   92	30	11	service	com.aelitis.azureus.plugins.upnp.UPnPPluginService
/*    */     //   238	427	11	random	java.util.Random
/*    */     //   269	360	12	last_error	Throwable
/*    */     //   737	3	12	e	NetworkAdminException
/*    */     //   742	9	12	e	Throwable
/*    */     //   274	342	13	timeout	long
/*    */     //   279	334	15	timeout_inc	long
/*    */     //   282	336	17	i	int
/*    */     //   317	7	18	connection_id	long
/*    */     //   604	3	18	e	Throwable
/*    */     //   328	54	20	request_packet	NetworkAdminNATUDPRequest
/*    */     //   407	3	21	reply_packet	NetworkAdminNATUDPReply
/*    */     //   414	35	22	reply	java.util.Map
/*    */     //   431	59	23	ip_bytes	byte[]
/*    */     //   463	16	24	reason	byte[]
/*    */     //   494	108	25	localInetAddress	InetAddress
/*    */     //   523	7	26	connection_id	long
/*    */     //   585	3	26	e	Throwable
/*    */     //   534	30	28	request_packet	NetworkAdminNATUDPRequest
/*    */     //   641	94	29	localObject1	Object
/*    */     //   670	7	30	connection_id	long
/*    */     //   732	3	30	e	Throwable
/*    */     //   681	30	32	request_packet	NetworkAdminNATUDPRequest
/*    */     //   756	17	33	localObject2	Object
/*    */     // Exception table:
/*    */     //   from	to	target	type
/*    */     //   496	582	585	java/lang/Throwable
/*    */     //   308	496	604	java/lang/Throwable
/*    */     //   281	496	641	finally
/*    */     //   604	643	641	finally
/*    */     //   643	729	732	java/lang/Throwable
/*    */     //   260	587	737	com/aelitis/azureus/core/networkmanager/admin/NetworkAdminException
/*    */     //   604	737	737	com/aelitis/azureus/core/networkmanager/admin/NetworkAdminException
/*    */     //   260	587	742	java/lang/Throwable
/*    */     //   604	737	742	java/lang/Throwable
/*    */     //   260	587	756	finally
/*    */     //   604	758	756	finally
/*    */   }
/*    */   
/*    */   static {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminUDPTester.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */