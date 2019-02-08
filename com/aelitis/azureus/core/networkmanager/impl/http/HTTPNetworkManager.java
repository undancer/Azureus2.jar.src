/*     */ package com.aelitis.azureus.core.networkmanager.impl.http;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.ConnectionEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager.ByteMatcher;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager.RoutingListener;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelper;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelper.selectListener;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.IncomingSocketChannelManager;
/*     */ import com.aelitis.azureus.core.peermanager.PeerManager;
/*     */ import com.aelitis.azureus.core.peermanager.PeerManagerRegistration;
/*     */ import com.aelitis.azureus.core.peermanager.PeerManagerRoutingListener;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamDecoder;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamEncoder;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamFactory;
/*     */ import com.aelitis.azureus.core.stats.AzureusCoreStats;
/*     */ import com.aelitis.azureus.core.stats.AzureusCoreStatsProvider;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.InetAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HTTPNetworkManager
/*     */ {
/*     */   private static final String NL = "\r\n";
/*  62 */   private static final LogIDs LOGID = LogIDs.NWMAN;
/*     */   
/*  64 */   private static final HTTPNetworkManager instance = new HTTPNetworkManager();
/*     */   
/*  66 */   public static HTTPNetworkManager getSingleton() { return instance; }
/*     */   
/*     */ 
/*     */   private final IncomingSocketChannelManager http_incoming_manager;
/*     */   
/*     */   private long total_requests;
/*     */   
/*     */   private long total_webseed_requests;
/*     */   private long total_getright_requests;
/*     */   private long total_invalid_requests;
/*     */   private long total_ok_requests;
/*  77 */   final CopyOnWriteList<URLHandler> url_handlers = new CopyOnWriteList();
/*     */   
/*     */ 
/*     */   private HTTPNetworkManager()
/*     */   {
/*  82 */     Set types = new HashSet();
/*     */     
/*  84 */     types.add("net.http.inbound.request.count");
/*  85 */     types.add("net.http.inbound.request.ok.count");
/*  86 */     types.add("net.http.inbound.request.invalid.count");
/*  87 */     types.add("net.http.inbound.request.webseed.count");
/*  88 */     types.add("net.http.inbound.request.getright.count");
/*     */     
/*  90 */     AzureusCoreStats.registerProvider(types, new AzureusCoreStatsProvider()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void updateStats(Set types, Map values)
/*     */       {
/*     */ 
/*     */ 
/*  99 */         if (types.contains("net.http.inbound.request.count"))
/*     */         {
/* 101 */           values.put("net.http.inbound.request.count", new Long(HTTPNetworkManager.this.total_requests));
/*     */         }
/*     */         
/* 104 */         if (types.contains("net.http.inbound.request.ok.count"))
/*     */         {
/* 106 */           values.put("net.http.inbound.request.ok.count", new Long(HTTPNetworkManager.this.total_ok_requests));
/*     */         }
/*     */         
/* 109 */         if (types.contains("net.http.inbound.request.invalid.count"))
/*     */         {
/* 111 */           values.put("net.http.inbound.request.invalid.count", new Long(HTTPNetworkManager.this.total_invalid_requests));
/*     */         }
/*     */         
/* 114 */         if (types.contains("net.http.inbound.request.webseed.count"))
/*     */         {
/* 116 */           values.put("net.http.inbound.request.webseed.count", new Long(HTTPNetworkManager.this.total_webseed_requests));
/*     */         }
/*     */         
/* 119 */         if (types.contains("net.http.inbound.request.getright.count"))
/*     */         {
/* 121 */           values.put("net.http.inbound.request.getright.count", new Long(HTTPNetworkManager.this.total_getright_requests));
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 134 */     });
/* 135 */     this.http_incoming_manager = new IncomingSocketChannelManager("HTTP.Data.Listen.Port", "HTTP.Data.Listen.Port.Enable");
/*     */     
/* 137 */     NetworkManager.ByteMatcher matcher = new NetworkManager.ByteMatcher()
/*     */     {
/*     */ 
/* 140 */       public int matchThisSizeOrBigger() { return 16; }
/*     */       
/* 142 */       public int maxSize() { return 256; }
/* 143 */       public int minSize() { return 3;
/*     */       }
/*     */       
/*     */       /* Error */
/*     */       public Object matches(TransportHelper transport, ByteBuffer to_compare, int port)
/*     */       {
/*     */         // Byte code:
/*     */         //   0: aload_0
/*     */         //   1: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   4: invokestatic 259	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$008	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   7: pop2
/*     */         //   8: aload_1
/*     */         //   9: invokeinterface 300 1 0
/*     */         //   14: astore 4
/*     */         //   16: aload_2
/*     */         //   17: invokevirtual 291	java/nio/ByteBuffer:limit	()I
/*     */         //   20: istore 5
/*     */         //   22: aload_2
/*     */         //   23: invokevirtual 292	java/nio/ByteBuffer:position	()I
/*     */         //   26: istore 6
/*     */         //   28: iconst_0
/*     */         //   29: istore 7
/*     */         //   31: iconst_3
/*     */         //   32: newarray <illegal type>
/*     */         //   34: astore 8
/*     */         //   36: aload_2
/*     */         //   37: aload 8
/*     */         //   39: invokevirtual 296	java/nio/ByteBuffer:get	([B)Ljava/nio/ByteBuffer;
/*     */         //   42: pop
/*     */         //   43: aload 8
/*     */         //   45: iconst_0
/*     */         //   46: baload
/*     */         //   47: bipush 71
/*     */         //   49: if_icmpne +21 -> 70
/*     */         //   52: aload 8
/*     */         //   54: iconst_1
/*     */         //   55: baload
/*     */         //   56: bipush 69
/*     */         //   58: if_icmpne +12 -> 70
/*     */         //   61: aload 8
/*     */         //   63: iconst_2
/*     */         //   64: baload
/*     */         //   65: bipush 84
/*     */         //   67: if_icmpeq +47 -> 114
/*     */         //   70: aconst_null
/*     */         //   71: astore 9
/*     */         //   73: iload 7
/*     */         //   75: ifeq +14 -> 89
/*     */         //   78: aload_0
/*     */         //   79: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   82: invokestatic 260	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$108	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   85: pop2
/*     */         //   86: goto +11 -> 97
/*     */         //   89: aload_0
/*     */         //   90: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   93: invokestatic 261	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$208	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   96: pop2
/*     */         //   97: aload_2
/*     */         //   98: iload 5
/*     */         //   100: invokevirtual 294	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
/*     */         //   103: pop
/*     */         //   104: aload_2
/*     */         //   105: iload 6
/*     */         //   107: invokevirtual 295	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
/*     */         //   110: pop
/*     */         //   111: aload 9
/*     */         //   113: areturn
/*     */         //   114: aload_2
/*     */         //   115: invokevirtual 293	java/nio/ByteBuffer:remaining	()I
/*     */         //   118: newarray <illegal type>
/*     */         //   120: astore 9
/*     */         //   122: aload_2
/*     */         //   123: aload 9
/*     */         //   125: invokevirtual 296	java/nio/ByteBuffer:get	([B)Ljava/nio/ByteBuffer;
/*     */         //   128: pop
/*     */         //   129: new 153	java/lang/String
/*     */         //   132: dup
/*     */         //   133: aload 9
/*     */         //   135: ldc 13
/*     */         //   137: invokespecial 282	java/lang/String:<init>	([BLjava/lang/String;)V
/*     */         //   140: astore 10
/*     */         //   142: aload 10
/*     */         //   144: bipush 32
/*     */         //   146: invokevirtual 274	java/lang/String:indexOf	(I)I
/*     */         //   149: istore 11
/*     */         //   151: iload 11
/*     */         //   153: iconst_m1
/*     */         //   154: if_icmpne +47 -> 201
/*     */         //   157: aconst_null
/*     */         //   158: astore 12
/*     */         //   160: iload 7
/*     */         //   162: ifeq +14 -> 176
/*     */         //   165: aload_0
/*     */         //   166: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   169: invokestatic 260	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$108	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   172: pop2
/*     */         //   173: goto +11 -> 184
/*     */         //   176: aload_0
/*     */         //   177: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   180: invokestatic 261	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$208	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   183: pop2
/*     */         //   184: aload_2
/*     */         //   185: iload 5
/*     */         //   187: invokevirtual 294	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
/*     */         //   190: pop
/*     */         //   191: aload_2
/*     */         //   192: iload 6
/*     */         //   194: invokevirtual 295	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
/*     */         //   197: pop
/*     */         //   198: aload 12
/*     */         //   200: areturn
/*     */         //   201: aload 10
/*     */         //   203: iload 11
/*     */         //   205: iconst_1
/*     */         //   206: iadd
/*     */         //   207: invokevirtual 278	java/lang/String:substring	(I)Ljava/lang/String;
/*     */         //   210: invokevirtual 277	java/lang/String:trim	()Ljava/lang/String;
/*     */         //   213: astore 10
/*     */         //   215: aload 10
/*     */         //   217: ldc 6
/*     */         //   219: invokevirtual 276	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
/*     */         //   222: ifeq +67 -> 289
/*     */         //   225: iconst_1
/*     */         //   226: istore 7
/*     */         //   228: iconst_2
/*     */         //   229: anewarray 152	java/lang/Object
/*     */         //   232: dup
/*     */         //   233: iconst_0
/*     */         //   234: aload_1
/*     */         //   235: aastore
/*     */         //   236: dup
/*     */         //   237: iconst_1
/*     */         //   238: aload_0
/*     */         //   239: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   242: invokevirtual 262	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:getIndexPage	()Ljava/lang/String;
/*     */         //   245: aastore
/*     */         //   246: astore 12
/*     */         //   248: iload 7
/*     */         //   250: ifeq +14 -> 264
/*     */         //   253: aload_0
/*     */         //   254: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   257: invokestatic 260	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$108	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   260: pop2
/*     */         //   261: goto +11 -> 272
/*     */         //   264: aload_0
/*     */         //   265: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   268: invokestatic 261	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$208	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   271: pop2
/*     */         //   272: aload_2
/*     */         //   273: iload 5
/*     */         //   275: invokevirtual 294	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
/*     */         //   278: pop
/*     */         //   279: aload_2
/*     */         //   280: iload 6
/*     */         //   282: invokevirtual 295	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
/*     */         //   285: pop
/*     */         //   286: aload 12
/*     */         //   288: areturn
/*     */         //   289: aload 10
/*     */         //   291: ldc 8
/*     */         //   293: invokevirtual 276	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
/*     */         //   296: ifeq +69 -> 365
/*     */         //   299: iconst_1
/*     */         //   300: istore 7
/*     */         //   302: iconst_2
/*     */         //   303: anewarray 152	java/lang/Object
/*     */         //   306: dup
/*     */         //   307: iconst_0
/*     */         //   308: aload_1
/*     */         //   309: aastore
/*     */         //   310: dup
/*     */         //   311: iconst_1
/*     */         //   312: aload_0
/*     */         //   313: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   316: aload 10
/*     */         //   318: invokevirtual 267	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:getPingPage	(Ljava/lang/String;)Ljava/lang/String;
/*     */         //   321: aastore
/*     */         //   322: astore 12
/*     */         //   324: iload 7
/*     */         //   326: ifeq +14 -> 340
/*     */         //   329: aload_0
/*     */         //   330: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   333: invokestatic 260	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$108	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   336: pop2
/*     */         //   337: goto +11 -> 348
/*     */         //   340: aload_0
/*     */         //   341: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   344: invokestatic 261	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$208	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   347: pop2
/*     */         //   348: aload_2
/*     */         //   349: iload 5
/*     */         //   351: invokevirtual 294	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
/*     */         //   354: pop
/*     */         //   355: aload_2
/*     */         //   356: iload 6
/*     */         //   358: invokevirtual 295	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
/*     */         //   361: pop
/*     */         //   362: aload 12
/*     */         //   364: areturn
/*     */         //   365: aload 10
/*     */         //   367: ldc 9
/*     */         //   369: invokevirtual 276	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
/*     */         //   372: ifeq +67 -> 439
/*     */         //   375: iconst_1
/*     */         //   376: istore 7
/*     */         //   378: iconst_2
/*     */         //   379: anewarray 152	java/lang/Object
/*     */         //   382: dup
/*     */         //   383: iconst_0
/*     */         //   384: aload_1
/*     */         //   385: aastore
/*     */         //   386: dup
/*     */         //   387: iconst_1
/*     */         //   388: aload_0
/*     */         //   389: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   392: invokevirtual 264	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:getTest503	()Ljava/lang/String;
/*     */         //   395: aastore
/*     */         //   396: astore 12
/*     */         //   398: iload 7
/*     */         //   400: ifeq +14 -> 414
/*     */         //   403: aload_0
/*     */         //   404: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   407: invokestatic 260	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$108	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   410: pop2
/*     */         //   411: goto +11 -> 422
/*     */         //   414: aload_0
/*     */         //   415: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   418: invokestatic 261	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$208	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   421: pop2
/*     */         //   422: aload_2
/*     */         //   423: iload 5
/*     */         //   425: invokevirtual 294	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
/*     */         //   428: pop
/*     */         //   429: aload_2
/*     */         //   430: iload 6
/*     */         //   432: invokevirtual 295	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
/*     */         //   435: pop
/*     */         //   436: aload 12
/*     */         //   438: areturn
/*     */         //   439: aconst_null
/*     */         //   440: astore 12
/*     */         //   442: aload 10
/*     */         //   444: ldc 10
/*     */         //   446: invokevirtual 280	java/lang/String:indexOf	(Ljava/lang/String;)I
/*     */         //   449: istore 13
/*     */         //   451: iload 13
/*     */         //   453: iconst_m1
/*     */         //   454: if_icmpeq +85 -> 539
/*     */         //   457: iload 13
/*     */         //   459: bipush 11
/*     */         //   461: iadd
/*     */         //   462: istore 14
/*     */         //   464: aload 10
/*     */         //   466: bipush 38
/*     */         //   468: iload 13
/*     */         //   470: invokevirtual 275	java/lang/String:indexOf	(II)I
/*     */         //   473: istore 15
/*     */         //   475: iload 15
/*     */         //   477: iconst_m1
/*     */         //   478: if_icmpne +47 -> 525
/*     */         //   481: aconst_null
/*     */         //   482: astore 16
/*     */         //   484: iload 7
/*     */         //   486: ifeq +14 -> 500
/*     */         //   489: aload_0
/*     */         //   490: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   493: invokestatic 260	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$108	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   496: pop2
/*     */         //   497: goto +11 -> 508
/*     */         //   500: aload_0
/*     */         //   501: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   504: invokestatic 261	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$208	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   507: pop2
/*     */         //   508: aload_2
/*     */         //   509: iload 5
/*     */         //   511: invokevirtual 294	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
/*     */         //   514: pop
/*     */         //   515: aload_2
/*     */         //   516: iload 6
/*     */         //   518: invokevirtual 295	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
/*     */         //   521: pop
/*     */         //   522: aload 16
/*     */         //   524: areturn
/*     */         //   525: aload 10
/*     */         //   527: iload 14
/*     */         //   529: iload 15
/*     */         //   531: invokevirtual 279	java/lang/String:substring	(II)Ljava/lang/String;
/*     */         //   534: astore 12
/*     */         //   536: goto +97 -> 633
/*     */         //   539: aload 10
/*     */         //   541: ldc 5
/*     */         //   543: invokevirtual 280	java/lang/String:indexOf	(Ljava/lang/String;)I
/*     */         //   546: istore 13
/*     */         //   548: iload 13
/*     */         //   550: iconst_m1
/*     */         //   551: if_icmpeq +82 -> 633
/*     */         //   554: iload 13
/*     */         //   556: bipush 7
/*     */         //   558: iadd
/*     */         //   559: istore 14
/*     */         //   561: aload 10
/*     */         //   563: bipush 47
/*     */         //   565: iload 14
/*     */         //   567: invokevirtual 275	java/lang/String:indexOf	(II)I
/*     */         //   570: istore 15
/*     */         //   572: iload 15
/*     */         //   574: iconst_m1
/*     */         //   575: if_icmpne +47 -> 622
/*     */         //   578: aconst_null
/*     */         //   579: astore 16
/*     */         //   581: iload 7
/*     */         //   583: ifeq +14 -> 597
/*     */         //   586: aload_0
/*     */         //   587: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   590: invokestatic 260	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$108	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   593: pop2
/*     */         //   594: goto +11 -> 605
/*     */         //   597: aload_0
/*     */         //   598: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   601: invokestatic 261	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$208	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   604: pop2
/*     */         //   605: aload_2
/*     */         //   606: iload 5
/*     */         //   608: invokevirtual 294	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
/*     */         //   611: pop
/*     */         //   612: aload_2
/*     */         //   613: iload 6
/*     */         //   615: invokevirtual 295	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
/*     */         //   618: pop
/*     */         //   619: aload 16
/*     */         //   621: areturn
/*     */         //   622: aload 10
/*     */         //   624: iload 14
/*     */         //   626: iload 15
/*     */         //   628: invokevirtual 279	java/lang/String:substring	(II)Ljava/lang/String;
/*     */         //   631: astore 12
/*     */         //   633: aload 12
/*     */         //   635: ifnull +127 -> 762
/*     */         //   638: aload 12
/*     */         //   640: ldc 13
/*     */         //   642: invokestatic 289	java/net/URLDecoder:decode	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
/*     */         //   645: ldc 13
/*     */         //   647: invokevirtual 281	java/lang/String:getBytes	(Ljava/lang/String;)[B
/*     */         //   650: astore 14
/*     */         //   652: invokestatic 269	com/aelitis/azureus/core/peermanager/PeerManager:getSingleton	()Lcom/aelitis/azureus/core/peermanager/PeerManager;
/*     */         //   655: aload 4
/*     */         //   657: aload 14
/*     */         //   659: invokevirtual 270	com/aelitis/azureus/core/peermanager/PeerManager:manualMatchHash	(Ljava/net/InetSocketAddress;[B)Lcom/aelitis/azureus/core/peermanager/PeerManagerRegistration;
/*     */         //   662: astore 15
/*     */         //   664: aload 15
/*     */         //   666: ifnull +93 -> 759
/*     */         //   669: aload 10
/*     */         //   671: bipush 32
/*     */         //   673: invokevirtual 274	java/lang/String:indexOf	(I)I
/*     */         //   676: istore 16
/*     */         //   678: iload 16
/*     */         //   680: iconst_m1
/*     */         //   681: if_icmpne +8 -> 689
/*     */         //   684: aload 10
/*     */         //   686: goto +11 -> 697
/*     */         //   689: aload 10
/*     */         //   691: iconst_0
/*     */         //   692: iload 16
/*     */         //   694: invokevirtual 279	java/lang/String:substring	(II)Ljava/lang/String;
/*     */         //   697: astore 17
/*     */         //   699: iconst_1
/*     */         //   700: istore 7
/*     */         //   702: iconst_2
/*     */         //   703: anewarray 152	java/lang/Object
/*     */         //   706: dup
/*     */         //   707: iconst_0
/*     */         //   708: aload 17
/*     */         //   710: aastore
/*     */         //   711: dup
/*     */         //   712: iconst_1
/*     */         //   713: aload 15
/*     */         //   715: aastore
/*     */         //   716: astore 18
/*     */         //   718: iload 7
/*     */         //   720: ifeq +14 -> 734
/*     */         //   723: aload_0
/*     */         //   724: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   727: invokestatic 260	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$108	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   730: pop2
/*     */         //   731: goto +11 -> 742
/*     */         //   734: aload_0
/*     */         //   735: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   738: invokestatic 261	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$208	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   741: pop2
/*     */         //   742: aload_2
/*     */         //   743: iload 5
/*     */         //   745: invokevirtual 294	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
/*     */         //   748: pop
/*     */         //   749: aload_2
/*     */         //   750: iload 6
/*     */         //   752: invokevirtual 295	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
/*     */         //   755: pop
/*     */         //   756: aload 18
/*     */         //   758: areturn
/*     */         //   759: goto +313 -> 1072
/*     */         //   762: aload 10
/*     */         //   764: ldc 7
/*     */         //   766: invokevirtual 280	java/lang/String:indexOf	(Ljava/lang/String;)I
/*     */         //   769: istore 14
/*     */         //   771: iload 14
/*     */         //   773: iconst_m1
/*     */         //   774: if_icmpeq +298 -> 1072
/*     */         //   777: aload 10
/*     */         //   779: bipush 32
/*     */         //   781: iload 14
/*     */         //   783: invokevirtual 275	java/lang/String:indexOf	(II)I
/*     */         //   786: istore 15
/*     */         //   788: iload 15
/*     */         //   790: iconst_m1
/*     */         //   791: if_icmpne +47 -> 838
/*     */         //   794: aconst_null
/*     */         //   795: astore 16
/*     */         //   797: iload 7
/*     */         //   799: ifeq +14 -> 813
/*     */         //   802: aload_0
/*     */         //   803: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   806: invokestatic 260	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$108	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   809: pop2
/*     */         //   810: goto +11 -> 821
/*     */         //   813: aload_0
/*     */         //   814: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   817: invokestatic 261	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$208	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   820: pop2
/*     */         //   821: aload_2
/*     */         //   822: iload 5
/*     */         //   824: invokevirtual 294	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
/*     */         //   827: pop
/*     */         //   828: aload_2
/*     */         //   829: iload 6
/*     */         //   831: invokevirtual 295	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
/*     */         //   834: pop
/*     */         //   835: aload 16
/*     */         //   837: areturn
/*     */         //   838: aload 10
/*     */         //   840: iconst_0
/*     */         //   841: iload 15
/*     */         //   843: invokevirtual 279	java/lang/String:substring	(II)Ljava/lang/String;
/*     */         //   846: iload 14
/*     */         //   848: bipush 7
/*     */         //   850: iadd
/*     */         //   851: invokevirtual 278	java/lang/String:substring	(I)Ljava/lang/String;
/*     */         //   854: astore 16
/*     */         //   856: aload 16
/*     */         //   858: ldc 14
/*     */         //   860: invokestatic 289	java/net/URLDecoder:decode	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
/*     */         //   863: astore 16
/*     */         //   865: invokestatic 269	com/aelitis/azureus/core/peermanager/PeerManager:getSingleton	()Lcom/aelitis/azureus/core/peermanager/PeerManager;
/*     */         //   868: aload 4
/*     */         //   870: aload 16
/*     */         //   872: invokevirtual 271	com/aelitis/azureus/core/peermanager/PeerManager:manualMatchLink	(Ljava/net/InetSocketAddress;Ljava/lang/String;)Lcom/aelitis/azureus/core/peermanager/PeerManagerRegistration;
/*     */         //   875: astore 17
/*     */         //   877: aload 17
/*     */         //   879: ifnull +193 -> 1072
/*     */         //   882: aload 17
/*     */         //   884: aload 16
/*     */         //   886: invokeinterface 302 2 0
/*     */         //   891: astore 18
/*     */         //   893: aload 18
/*     */         //   895: ifnull +177 -> 1072
/*     */         //   898: new 154	java/lang/StringBuilder
/*     */         //   901: dup
/*     */         //   902: sipush 512
/*     */         //   905: invokespecial 284	java/lang/StringBuilder:<init>	(I)V
/*     */         //   908: astore 19
/*     */         //   910: aload 19
/*     */         //   912: ldc 5
/*     */         //   914: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */         //   917: pop
/*     */         //   918: aload 19
/*     */         //   920: new 153	java/lang/String
/*     */         //   923: dup
/*     */         //   924: aload 18
/*     */         //   926: invokeinterface 307 1 0
/*     */         //   931: invokeinterface 305 1 0
/*     */         //   936: ldc 13
/*     */         //   938: invokespecial 282	java/lang/String:<init>	([BLjava/lang/String;)V
/*     */         //   941: ldc 13
/*     */         //   943: invokestatic 290	java/net/URLEncoder:encode	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
/*     */         //   946: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */         //   949: pop
/*     */         //   950: aload 18
/*     */         //   952: invokeinterface 306 1 0
/*     */         //   957: astore 20
/*     */         //   959: iconst_0
/*     */         //   960: istore 21
/*     */         //   962: iload 21
/*     */         //   964: aload 20
/*     */         //   966: arraylength
/*     */         //   967: if_icmpge +42 -> 1009
/*     */         //   970: aload 19
/*     */         //   972: ldc 4
/*     */         //   974: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */         //   977: pop
/*     */         //   978: aload 19
/*     */         //   980: new 153	java/lang/String
/*     */         //   983: dup
/*     */         //   984: aload 20
/*     */         //   986: iload 21
/*     */         //   988: aaload
/*     */         //   989: ldc 13
/*     */         //   991: invokespecial 282	java/lang/String:<init>	([BLjava/lang/String;)V
/*     */         //   994: ldc 13
/*     */         //   996: invokestatic 290	java/net/URLEncoder:encode	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
/*     */         //   999: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */         //   1002: pop
/*     */         //   1003: iinc 21 1
/*     */         //   1006: goto -44 -> 962
/*     */         //   1009: iconst_1
/*     */         //   1010: istore 7
/*     */         //   1012: iconst_2
/*     */         //   1013: anewarray 152	java/lang/Object
/*     */         //   1016: dup
/*     */         //   1017: iconst_0
/*     */         //   1018: aload 19
/*     */         //   1020: invokevirtual 285	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */         //   1023: aastore
/*     */         //   1024: dup
/*     */         //   1025: iconst_1
/*     */         //   1026: aload 17
/*     */         //   1028: aastore
/*     */         //   1029: astore 21
/*     */         //   1031: iload 7
/*     */         //   1033: ifeq +14 -> 1047
/*     */         //   1036: aload_0
/*     */         //   1037: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   1040: invokestatic 260	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$108	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   1043: pop2
/*     */         //   1044: goto +11 -> 1055
/*     */         //   1047: aload_0
/*     */         //   1048: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   1051: invokestatic 261	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$208	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   1054: pop2
/*     */         //   1055: aload_2
/*     */         //   1056: iload 5
/*     */         //   1058: invokevirtual 294	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
/*     */         //   1061: pop
/*     */         //   1062: aload_2
/*     */         //   1063: iload 6
/*     */         //   1065: invokevirtual 295	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
/*     */         //   1068: pop
/*     */         //   1069: aload 21
/*     */         //   1071: areturn
/*     */         //   1072: aload 10
/*     */         //   1074: astore 14
/*     */         //   1076: aload 14
/*     */         //   1078: bipush 32
/*     */         //   1080: invokevirtual 274	java/lang/String:indexOf	(I)I
/*     */         //   1083: istore 15
/*     */         //   1085: iload 15
/*     */         //   1087: iconst_m1
/*     */         //   1088: if_icmpeq +13 -> 1101
/*     */         //   1091: aload 14
/*     */         //   1093: iconst_0
/*     */         //   1094: iload 15
/*     */         //   1096: invokevirtual 279	java/lang/String:substring	(II)Ljava/lang/String;
/*     */         //   1099: astore 14
/*     */         //   1101: aload_0
/*     */         //   1102: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   1105: getfield 257	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:url_handlers	Lcom/aelitis/azureus/core/util/CopyOnWriteList;
/*     */         //   1108: invokevirtual 272	com/aelitis/azureus/core/util/CopyOnWriteList:iterator	()Ljava/util/Iterator;
/*     */         //   1111: astore 16
/*     */         //   1113: aload 16
/*     */         //   1115: invokeinterface 303 1 0
/*     */         //   1120: ifeq +112 -> 1232
/*     */         //   1123: aload 16
/*     */         //   1125: invokeinterface 304 1 0
/*     */         //   1130: checkcast 147	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$URLHandler
/*     */         //   1133: astore 17
/*     */         //   1135: aload 17
/*     */         //   1137: aload 14
/*     */         //   1139: invokeinterface 301 2 0
/*     */         //   1144: ifeq +85 -> 1229
/*     */         //   1147: iconst_1
/*     */         //   1148: istore 7
/*     */         //   1150: iconst_3
/*     */         //   1151: anewarray 152	java/lang/Object
/*     */         //   1154: dup
/*     */         //   1155: iconst_0
/*     */         //   1156: aload 17
/*     */         //   1158: aastore
/*     */         //   1159: dup
/*     */         //   1160: iconst_1
/*     */         //   1161: aload_1
/*     */         //   1162: aastore
/*     */         //   1163: dup
/*     */         //   1164: iconst_2
/*     */         //   1165: new 154	java/lang/StringBuilder
/*     */         //   1168: dup
/*     */         //   1169: invokespecial 283	java/lang/StringBuilder:<init>	()V
/*     */         //   1172: ldc 11
/*     */         //   1174: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */         //   1177: aload 10
/*     */         //   1179: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */         //   1182: invokevirtual 285	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */         //   1185: aastore
/*     */         //   1186: astore 18
/*     */         //   1188: iload 7
/*     */         //   1190: ifeq +14 -> 1204
/*     */         //   1193: aload_0
/*     */         //   1194: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   1197: invokestatic 260	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$108	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   1200: pop2
/*     */         //   1201: goto +11 -> 1212
/*     */         //   1204: aload_0
/*     */         //   1205: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   1208: invokestatic 261	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$208	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   1211: pop2
/*     */         //   1212: aload_2
/*     */         //   1213: iload 5
/*     */         //   1215: invokevirtual 294	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
/*     */         //   1218: pop
/*     */         //   1219: aload_2
/*     */         //   1220: iload 6
/*     */         //   1222: invokevirtual 295	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
/*     */         //   1225: pop
/*     */         //   1226: aload 18
/*     */         //   1228: areturn
/*     */         //   1229: goto -116 -> 1113
/*     */         //   1232: invokestatic 298	org/gudy/azureus2/core3/logging/Logger:isEnabled	()Z
/*     */         //   1235: ifeq +46 -> 1281
/*     */         //   1238: new 161	org/gudy/azureus2/core3/logging/LogEvent
/*     */         //   1241: dup
/*     */         //   1242: invokestatic 265	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$500	()Lorg/gudy/azureus2/core3/logging/LogIDs;
/*     */         //   1245: new 154	java/lang/StringBuilder
/*     */         //   1248: dup
/*     */         //   1249: invokespecial 283	java/lang/StringBuilder:<init>	()V
/*     */         //   1252: ldc 12
/*     */         //   1254: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */         //   1257: aload 4
/*     */         //   1259: invokevirtual 286	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*     */         //   1262: ldc 3
/*     */         //   1264: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */         //   1267: aload 10
/*     */         //   1269: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */         //   1272: invokevirtual 285	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */         //   1275: invokespecial 297	org/gudy/azureus2/core3/logging/LogEvent:<init>	(Lorg/gudy/azureus2/core3/logging/LogIDs;Ljava/lang/String;)V
/*     */         //   1278: invokestatic 299	org/gudy/azureus2/core3/logging/Logger:log	(Lorg/gudy/azureus2/core3/logging/LogEvent;)V
/*     */         //   1281: iconst_2
/*     */         //   1282: anewarray 152	java/lang/Object
/*     */         //   1285: dup
/*     */         //   1286: iconst_0
/*     */         //   1287: aload_1
/*     */         //   1288: aastore
/*     */         //   1289: dup
/*     */         //   1290: iconst_1
/*     */         //   1291: aload_0
/*     */         //   1292: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   1295: invokevirtual 263	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:getNotFound	()Ljava/lang/String;
/*     */         //   1298: aastore
/*     */         //   1299: astore 16
/*     */         //   1301: iload 7
/*     */         //   1303: ifeq +14 -> 1317
/*     */         //   1306: aload_0
/*     */         //   1307: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   1310: invokestatic 260	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$108	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   1313: pop2
/*     */         //   1314: goto +11 -> 1325
/*     */         //   1317: aload_0
/*     */         //   1318: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   1321: invokestatic 261	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$208	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   1324: pop2
/*     */         //   1325: aload_2
/*     */         //   1326: iload 5
/*     */         //   1328: invokevirtual 294	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
/*     */         //   1331: pop
/*     */         //   1332: aload_2
/*     */         //   1333: iload 6
/*     */         //   1335: invokevirtual 295	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
/*     */         //   1338: pop
/*     */         //   1339: aload 16
/*     */         //   1341: areturn
/*     */         //   1342: astore 10
/*     */         //   1344: invokestatic 298	org/gudy/azureus2/core3/logging/Logger:isEnabled	()Z
/*     */         //   1347: ifeq +49 -> 1396
/*     */         //   1350: new 161	org/gudy/azureus2/core3/logging/LogEvent
/*     */         //   1353: dup
/*     */         //   1354: invokestatic 265	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$500	()Lorg/gudy/azureus2/core3/logging/LogIDs;
/*     */         //   1357: new 154	java/lang/StringBuilder
/*     */         //   1360: dup
/*     */         //   1361: invokespecial 283	java/lang/StringBuilder:<init>	()V
/*     */         //   1364: ldc 12
/*     */         //   1366: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */         //   1369: aload 4
/*     */         //   1371: invokevirtual 286	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*     */         //   1374: ldc 2
/*     */         //   1376: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */         //   1379: aload 10
/*     */         //   1381: invokevirtual 288	java/lang/Throwable:getMessage	()Ljava/lang/String;
/*     */         //   1384: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */         //   1387: invokevirtual 285	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */         //   1390: invokespecial 297	org/gudy/azureus2/core3/logging/LogEvent:<init>	(Lorg/gudy/azureus2/core3/logging/LogIDs;Ljava/lang/String;)V
/*     */         //   1393: invokestatic 299	org/gudy/azureus2/core3/logging/Logger:log	(Lorg/gudy/azureus2/core3/logging/LogEvent;)V
/*     */         //   1396: aconst_null
/*     */         //   1397: astore 11
/*     */         //   1399: iload 7
/*     */         //   1401: ifeq +14 -> 1415
/*     */         //   1404: aload_0
/*     */         //   1405: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   1408: invokestatic 260	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$108	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   1411: pop2
/*     */         //   1412: goto +11 -> 1423
/*     */         //   1415: aload_0
/*     */         //   1416: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   1419: invokestatic 261	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$208	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   1422: pop2
/*     */         //   1423: aload_2
/*     */         //   1424: iload 5
/*     */         //   1426: invokevirtual 294	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
/*     */         //   1429: pop
/*     */         //   1430: aload_2
/*     */         //   1431: iload 6
/*     */         //   1433: invokevirtual 295	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
/*     */         //   1436: pop
/*     */         //   1437: aload 11
/*     */         //   1439: areturn
/*     */         //   1440: astore 22
/*     */         //   1442: iload 7
/*     */         //   1444: ifeq +14 -> 1458
/*     */         //   1447: aload_0
/*     */         //   1448: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   1451: invokestatic 260	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$108	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   1454: pop2
/*     */         //   1455: goto +11 -> 1466
/*     */         //   1458: aload_0
/*     */         //   1459: getfield 258	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager$2:this$0	Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;
/*     */         //   1462: invokestatic 261	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager:access$208	(Lcom/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager;)J
/*     */         //   1465: pop2
/*     */         //   1466: aload_2
/*     */         //   1467: iload 5
/*     */         //   1469: invokevirtual 294	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
/*     */         //   1472: pop
/*     */         //   1473: aload_2
/*     */         //   1474: iload 6
/*     */         //   1476: invokevirtual 295	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
/*     */         //   1479: pop
/*     */         //   1480: aload 22
/*     */         //   1482: athrow
/*     */         // Line number table:
/*     */         //   Java source line #151	-> byte code offset #0
/*     */         //   Java source line #153	-> byte code offset #8
/*     */         //   Java source line #155	-> byte code offset #16
/*     */         //   Java source line #156	-> byte code offset #22
/*     */         //   Java source line #158	-> byte code offset #28
/*     */         //   Java source line #161	-> byte code offset #31
/*     */         //   Java source line #163	-> byte code offset #36
/*     */         //   Java source line #167	-> byte code offset #43
/*     */         //   Java source line #169	-> byte code offset #70
/*     */         //   Java source line #356	-> byte code offset #73
/*     */         //   Java source line #358	-> byte code offset #78
/*     */         //   Java source line #362	-> byte code offset #89
/*     */         //   Java source line #366	-> byte code offset #97
/*     */         //   Java source line #367	-> byte code offset #104
/*     */         //   Java source line #172	-> byte code offset #114
/*     */         //   Java source line #174	-> byte code offset #122
/*     */         //   Java source line #179	-> byte code offset #129
/*     */         //   Java source line #181	-> byte code offset #142
/*     */         //   Java source line #183	-> byte code offset #151
/*     */         //   Java source line #185	-> byte code offset #157
/*     */         //   Java source line #356	-> byte code offset #160
/*     */         //   Java source line #358	-> byte code offset #165
/*     */         //   Java source line #362	-> byte code offset #176
/*     */         //   Java source line #366	-> byte code offset #184
/*     */         //   Java source line #367	-> byte code offset #191
/*     */         //   Java source line #190	-> byte code offset #201
/*     */         //   Java source line #192	-> byte code offset #215
/*     */         //   Java source line #194	-> byte code offset #225
/*     */         //   Java source line #196	-> byte code offset #228
/*     */         //   Java source line #356	-> byte code offset #248
/*     */         //   Java source line #358	-> byte code offset #253
/*     */         //   Java source line #362	-> byte code offset #264
/*     */         //   Java source line #366	-> byte code offset #272
/*     */         //   Java source line #367	-> byte code offset #279
/*     */         //   Java source line #198	-> byte code offset #289
/*     */         //   Java source line #202	-> byte code offset #299
/*     */         //   Java source line #204	-> byte code offset #302
/*     */         //   Java source line #356	-> byte code offset #324
/*     */         //   Java source line #358	-> byte code offset #329
/*     */         //   Java source line #362	-> byte code offset #340
/*     */         //   Java source line #366	-> byte code offset #348
/*     */         //   Java source line #367	-> byte code offset #355
/*     */         //   Java source line #206	-> byte code offset #365
/*     */         //   Java source line #208	-> byte code offset #375
/*     */         //   Java source line #210	-> byte code offset #378
/*     */         //   Java source line #356	-> byte code offset #398
/*     */         //   Java source line #358	-> byte code offset #403
/*     */         //   Java source line #362	-> byte code offset #414
/*     */         //   Java source line #366	-> byte code offset #422
/*     */         //   Java source line #367	-> byte code offset #429
/*     */         //   Java source line #213	-> byte code offset #439
/*     */         //   Java source line #215	-> byte code offset #442
/*     */         //   Java source line #217	-> byte code offset #451
/*     */         //   Java source line #219	-> byte code offset #457
/*     */         //   Java source line #221	-> byte code offset #464
/*     */         //   Java source line #223	-> byte code offset #475
/*     */         //   Java source line #227	-> byte code offset #481
/*     */         //   Java source line #356	-> byte code offset #484
/*     */         //   Java source line #358	-> byte code offset #489
/*     */         //   Java source line #362	-> byte code offset #500
/*     */         //   Java source line #366	-> byte code offset #508
/*     */         //   Java source line #367	-> byte code offset #515
/*     */         //   Java source line #231	-> byte code offset #525
/*     */         //   Java source line #233	-> byte code offset #536
/*     */         //   Java source line #235	-> byte code offset #539
/*     */         //   Java source line #237	-> byte code offset #548
/*     */         //   Java source line #239	-> byte code offset #554
/*     */         //   Java source line #241	-> byte code offset #561
/*     */         //   Java source line #243	-> byte code offset #572
/*     */         //   Java source line #247	-> byte code offset #578
/*     */         //   Java source line #356	-> byte code offset #581
/*     */         //   Java source line #358	-> byte code offset #586
/*     */         //   Java source line #362	-> byte code offset #597
/*     */         //   Java source line #366	-> byte code offset #605
/*     */         //   Java source line #367	-> byte code offset #612
/*     */         //   Java source line #251	-> byte code offset #622
/*     */         //   Java source line #256	-> byte code offset #633
/*     */         //   Java source line #258	-> byte code offset #638
/*     */         //   Java source line #260	-> byte code offset #652
/*     */         //   Java source line #262	-> byte code offset #664
/*     */         //   Java source line #266	-> byte code offset #669
/*     */         //   Java source line #268	-> byte code offset #678
/*     */         //   Java source line #270	-> byte code offset #699
/*     */         //   Java source line #272	-> byte code offset #702
/*     */         //   Java source line #356	-> byte code offset #718
/*     */         //   Java source line #358	-> byte code offset #723
/*     */         //   Java source line #362	-> byte code offset #734
/*     */         //   Java source line #366	-> byte code offset #742
/*     */         //   Java source line #367	-> byte code offset #749
/*     */         //   Java source line #274	-> byte code offset #759
/*     */         //   Java source line #276	-> byte code offset #762
/*     */         //   Java source line #278	-> byte code offset #771
/*     */         //   Java source line #280	-> byte code offset #777
/*     */         //   Java source line #282	-> byte code offset #788
/*     */         //   Java source line #284	-> byte code offset #794
/*     */         //   Java source line #356	-> byte code offset #797
/*     */         //   Java source line #358	-> byte code offset #802
/*     */         //   Java source line #362	-> byte code offset #813
/*     */         //   Java source line #366	-> byte code offset #821
/*     */         //   Java source line #367	-> byte code offset #828
/*     */         //   Java source line #287	-> byte code offset #838
/*     */         //   Java source line #289	-> byte code offset #856
/*     */         //   Java source line #291	-> byte code offset #865
/*     */         //   Java source line #293	-> byte code offset #877
/*     */         //   Java source line #295	-> byte code offset #882
/*     */         //   Java source line #297	-> byte code offset #893
/*     */         //   Java source line #299	-> byte code offset #898
/*     */         //   Java source line #301	-> byte code offset #910
/*     */         //   Java source line #303	-> byte code offset #918
/*     */         //   Java source line #305	-> byte code offset #950
/*     */         //   Java source line #307	-> byte code offset #959
/*     */         //   Java source line #309	-> byte code offset #970
/*     */         //   Java source line #311	-> byte code offset #978
/*     */         //   Java source line #307	-> byte code offset #1003
/*     */         //   Java source line #314	-> byte code offset #1009
/*     */         //   Java source line #316	-> byte code offset #1012
/*     */         //   Java source line #356	-> byte code offset #1031
/*     */         //   Java source line #358	-> byte code offset #1036
/*     */         //   Java source line #362	-> byte code offset #1047
/*     */         //   Java source line #366	-> byte code offset #1055
/*     */         //   Java source line #367	-> byte code offset #1062
/*     */         //   Java source line #322	-> byte code offset #1072
/*     */         //   Java source line #324	-> byte code offset #1076
/*     */         //   Java source line #326	-> byte code offset #1085
/*     */         //   Java source line #328	-> byte code offset #1091
/*     */         //   Java source line #331	-> byte code offset #1101
/*     */         //   Java source line #333	-> byte code offset #1135
/*     */         //   Java source line #335	-> byte code offset #1147
/*     */         //   Java source line #337	-> byte code offset #1150
/*     */         //   Java source line #356	-> byte code offset #1188
/*     */         //   Java source line #358	-> byte code offset #1193
/*     */         //   Java source line #362	-> byte code offset #1204
/*     */         //   Java source line #366	-> byte code offset #1212
/*     */         //   Java source line #367	-> byte code offset #1219
/*     */         //   Java source line #340	-> byte code offset #1232
/*     */         //   Java source line #341	-> byte code offset #1238
/*     */         //   Java source line #344	-> byte code offset #1281
/*     */         //   Java source line #356	-> byte code offset #1301
/*     */         //   Java source line #358	-> byte code offset #1306
/*     */         //   Java source line #362	-> byte code offset #1317
/*     */         //   Java source line #366	-> byte code offset #1325
/*     */         //   Java source line #367	-> byte code offset #1332
/*     */         //   Java source line #346	-> byte code offset #1342
/*     */         //   Java source line #348	-> byte code offset #1344
/*     */         //   Java source line #349	-> byte code offset #1350
/*     */         //   Java source line #352	-> byte code offset #1396
/*     */         //   Java source line #356	-> byte code offset #1399
/*     */         //   Java source line #358	-> byte code offset #1404
/*     */         //   Java source line #362	-> byte code offset #1415
/*     */         //   Java source line #366	-> byte code offset #1423
/*     */         //   Java source line #367	-> byte code offset #1430
/*     */         //   Java source line #356	-> byte code offset #1440
/*     */         //   Java source line #358	-> byte code offset #1447
/*     */         //   Java source line #362	-> byte code offset #1458
/*     */         //   Java source line #366	-> byte code offset #1466
/*     */         //   Java source line #367	-> byte code offset #1473
/*     */         // Local variable table:
/*     */         //   start	length	slot	name	signature
/*     */         //   0	1483	0	this	2
/*     */         //   0	1483	1	transport	TransportHelper
/*     */         //   0	1483	2	to_compare	ByteBuffer
/*     */         //   0	1483	3	port	int
/*     */         //   14	1356	4	address	java.net.InetSocketAddress
/*     */         //   20	1448	5	old_limit	int
/*     */         //   26	1449	6	old_position	int
/*     */         //   29	1414	7	ok	boolean
/*     */         //   34	28	8	head	byte[]
/*     */         //   71	41	9	localObject1	Object
/*     */         //   120	14	9	line_bytes	byte[]
/*     */         //   140	1128	10	url	String
/*     */         //   1342	38	10	e	Throwable
/*     */         //   149	1289	11	space	int
/*     */         //   158	279	12	localObject2	Object
/*     */         //   440	199	12	hash_str	String
/*     */         //   449	106	13	hash_pos	int
/*     */         //   462	66	14	hash_start	int
/*     */         //   559	66	14	hash_start	int
/*     */         //   650	8	14	hash	byte[]
/*     */         //   769	78	14	link_pos	int
/*     */         //   1074	64	14	trimmed	String
/*     */         //   473	57	15	hash_end	int
/*     */         //   570	57	15	hash_end	int
/*     */         //   662	52	15	reg_data	PeerManagerRegistration
/*     */         //   786	56	15	pos	int
/*     */         //   1083	12	15	pos	int
/*     */         //   482	138	16	localObject3	Object
/*     */         //   676	160	16	pos	int
/*     */         //   795	41	16	localObject4	Object
/*     */         //   854	31	16	link	String
/*     */         //   1111	229	16	i$	Object
/*     */         //   697	12	17	trimmed	String
/*     */         //   875	152	17	reg_data	PeerManagerRegistration
/*     */         //   1133	24	17	handler	HTTPNetworkManager.URLHandler
/*     */         //   716	41	18	arrayOfObject	Object[]
/*     */         //   891	336	18	file	Object
/*     */         //   908	111	19	target_url	StringBuilder
/*     */         //   957	28	20	bits	byte[][]
/*     */         //   960	110	21	i	int
/*     */         //   1440	41	22	localObject5	Object
/*     */         // Exception table:
/*     */         //   from	to	target	type
/*     */         //   129	160	1342	java/lang/Throwable
/*     */         //   201	248	1342	java/lang/Throwable
/*     */         //   289	324	1342	java/lang/Throwable
/*     */         //   365	398	1342	java/lang/Throwable
/*     */         //   439	484	1342	java/lang/Throwable
/*     */         //   525	581	1342	java/lang/Throwable
/*     */         //   622	718	1342	java/lang/Throwable
/*     */         //   759	797	1342	java/lang/Throwable
/*     */         //   838	1031	1342	java/lang/Throwable
/*     */         //   1072	1188	1342	java/lang/Throwable
/*     */         //   1229	1301	1342	java/lang/Throwable
/*     */         //   31	73	1440	finally
/*     */         //   114	160	1440	finally
/*     */         //   201	248	1440	finally
/*     */         //   289	324	1440	finally
/*     */         //   365	398	1440	finally
/*     */         //   439	484	1440	finally
/*     */         //   525	581	1440	finally
/*     */         //   622	718	1440	finally
/*     */         //   759	797	1440	finally
/*     */         //   838	1031	1440	finally
/*     */         //   1072	1188	1440	finally
/*     */         //   1229	1301	1440	finally
/*     */         //   1342	1399	1440	finally
/*     */         //   1440	1442	1440	finally
/*     */       }
/*     */       
/*     */       public Object minMatches(TransportHelper transport, ByteBuffer to_compare, int port)
/*     */       {
/* 377 */         byte[] head = new byte[3];
/*     */         
/* 379 */         to_compare.get(head);
/*     */         
/* 381 */         if ((head[0] != 71) || (head[1] != 69) || (head[2] != 84))
/*     */         {
/* 383 */           return null;
/*     */         }
/*     */         
/* 386 */         return "";
/*     */       }
/*     */       
/*     */ 
/*     */       public byte[][] getSharedSecrets()
/*     */       {
/* 392 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */       public int getSpecificPort()
/*     */       {
/* 398 */         return HTTPNetworkManager.this.http_incoming_manager.getTCPListeningPortNumber();
/*     */       }
/*     */       
/*     */ 
/* 402 */     };
/* 403 */     NetworkManager.getSingleton().requestIncomingConnectionRouting(matcher, new NetworkManager.RoutingListener()
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 474 */       new MessageStreamFactory
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public void connectionRouted(final NetworkConnection connection, Object _routing_data)
/*     */         {
/*     */ 
/*     */ 
/* 412 */           Object[] x = (Object[])_routing_data;
/*     */           
/* 414 */           Object entry1 = x[0];
/*     */           
/* 416 */           if ((entry1 instanceof TransportHelper))
/*     */           {
/*     */ 
/*     */ 
/* 420 */             HTTPNetworkManager.this.writeReply(connection, (TransportHelper)x[0], (String)x[1]);
/*     */             
/* 422 */             return;
/*     */           }
/* 424 */           if ((entry1 instanceof HTTPNetworkManager.URLHandler))
/*     */           {
/* 426 */             ((HTTPNetworkManager.URLHandler)entry1).handle((TransportHelper)x[1], (String)x[2]);
/*     */             
/*     */ 
/*     */ 
/* 430 */             return;
/*     */           }
/*     */           
/* 433 */           final String url = (String)entry1;
/* 434 */           PeerManagerRegistration routing_data = (PeerManagerRegistration)x[1];
/*     */           
/* 436 */           if (Logger.isEnabled()) {
/* 437 */             Logger.log(new LogEvent(HTTPNetworkManager.LOGID, "HTTP connection from " + connection.getEndpoint().getNotionalAddress() + " routed successfully on '" + url + "'"));
/*     */           }
/*     */           
/* 440 */           PeerManager.getSingleton().manualRoute(routing_data, connection, new PeerManagerRoutingListener()
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */             public boolean routed(PEPeerTransport peer)
/*     */             {
/*     */ 
/*     */ 
/* 449 */               if (url.contains("/webseed"))
/*     */               {
/* 451 */                 HTTPNetworkManager.access$308(HTTPNetworkManager.this);
/*     */                 
/* 453 */                 new HTTPNetworkConnectionWebSeed(HTTPNetworkManager.this, connection, peer);
/*     */                 
/* 455 */                 return true;
/*     */               }
/* 457 */               if (url.contains("/files/"))
/*     */               {
/* 459 */                 HTTPNetworkManager.access$408(HTTPNetworkManager.this);
/*     */                 
/* 461 */                 new HTTPNetworkConnectionFile(HTTPNetworkManager.this, connection, peer);
/*     */                 
/* 463 */                 return true;
/*     */               }
/*     */               
/* 466 */               return false;
/*     */             }
/*     */           });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 474 */         public boolean autoCryptoFallback() { return false; } }, new MessageStreamFactory()
/*     */       {
/*     */ 
/*     */         public MessageStreamEncoder createEncoder() {
/* 478 */           return new HTTPMessageEncoder(); }
/* 479 */         public MessageStreamDecoder createDecoder() { return new HTTPMessageDecoder(); }
/*     */       });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void reRoute(HTTPNetworkConnection old_http_connection, byte[] old_hash, byte[] new_hash, final String header)
/*     */   {
/* 491 */     NetworkConnection old_connection = old_http_connection.getConnection();
/*     */     
/* 493 */     PeerManagerRegistration reg_data = PeerManager.getSingleton().manualMatchHash(old_connection.getEndpoint().getNotionalAddress(), new_hash);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 498 */     if (reg_data == null)
/*     */     {
/* 500 */       old_http_connection.close("Re-routing failed - registration not found");
/*     */       
/* 502 */       return;
/*     */     }
/*     */     
/* 505 */     Transport transport = old_connection.detachTransport();
/*     */     
/* 507 */     old_http_connection.close("Switching torrents");
/*     */     
/* 509 */     final NetworkConnection new_connection = NetworkManager.getSingleton().bindTransport(transport, new HTTPMessageEncoder(), new HTTPMessageDecoder(header));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 515 */     PeerManager.getSingleton().manualRoute(reg_data, new_connection, new PeerManagerRoutingListener()
/*     */     {
/*     */       public boolean routed(PEPeerTransport peer)
/*     */       {
/*     */         HTTPNetworkConnection new_http_connection;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 526 */         if (header.contains("/webseed"))
/*     */         {
/* 528 */           new_http_connection = new HTTPNetworkConnectionWebSeed(HTTPNetworkManager.this, new_connection, peer);
/*     */         } else { HTTPNetworkConnection new_http_connection;
/* 530 */           if (header.contains("/files/"))
/*     */           {
/* 532 */             new_http_connection = new HTTPNetworkConnectionFile(HTTPNetworkManager.this, new_connection, peer);
/*     */           }
/*     */           else
/*     */           {
/* 536 */             return false;
/*     */           }
/*     */         }
/*     */         
/*     */         HTTPNetworkConnection new_http_connection;
/* 541 */         new_http_connection.readWakeup();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 550 */         return true;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isHTTPListenerEnabled()
/*     */   {
/* 558 */     return this.http_incoming_manager.isEnabled();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getHTTPListeningPortNumber()
/*     */   {
/* 564 */     return this.http_incoming_manager.getTCPListeningPortNumber();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setExplicitBindAddress(InetAddress address)
/*     */   {
/* 571 */     this.http_incoming_manager.setExplicitBindAddress(address);
/*     */   }
/*     */   
/*     */ 
/*     */   public void clearExplicitBindAddress()
/*     */   {
/* 577 */     this.http_incoming_manager.clearExplicitBindAddress();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isEffectiveBindAddress(InetAddress address)
/*     */   {
/* 584 */     return this.http_incoming_manager.isEffectiveBindAddress(address);
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getIndexPage()
/*     */   {
/* 590 */     return "HTTP/1.1 200 OK\r\nConnection: Close\r\nContent-Length: 0\r\n\r\n";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String getPingPage(String url)
/*     */   {
/* 600 */     int pos = url.indexOf(' ');
/*     */     
/* 602 */     if (pos != -1)
/*     */     {
/* 604 */       url = url.substring(0, pos);
/*     */     }
/*     */     
/* 607 */     pos = url.indexOf('?');
/*     */     
/* 609 */     Map response = new HashMap();
/*     */     
/* 611 */     boolean ok = false;
/*     */     
/* 613 */     if (pos != -1)
/*     */     {
/* 615 */       StringTokenizer tok = new StringTokenizer(url.substring(pos + 1), "&");
/*     */       
/* 617 */       while (tok.hasMoreTokens())
/*     */       {
/* 619 */         String token = tok.nextToken();
/*     */         
/* 621 */         pos = token.indexOf('=');
/*     */         
/* 623 */         if (pos != -1)
/*     */         {
/* 625 */           String lhs = token.substring(0, pos);
/* 626 */           String rhs = token.substring(pos + 1);
/*     */           
/* 628 */           if (lhs.equals("check"))
/*     */           {
/* 630 */             response.put("check", rhs);
/*     */             
/* 632 */             ok = true;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 638 */     if (ok) {
/*     */       try
/*     */       {
/* 641 */         byte[] bytes = BEncoder.encode(response);
/*     */         
/* 643 */         byte[] length = new byte[4];
/*     */         
/* 645 */         ByteBuffer.wrap(length).putInt(bytes.length);
/*     */         
/* 647 */         return "HTTP/1.1 200 OK\r\nConnection: Close\r\nContent-Length: " + (bytes.length + 4) + "\r\n" + "\r\n" + new String(length, "ISO-8859-1") + new String(bytes, "ISO-8859-1");
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 657 */     return getNotFound();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String getTest503()
/*     */   {
/* 664 */     return "HTTP/1.1 503 Service Unavailable\r\nConnection: Close\r\nContent-Length: 4\r\n\r\n1234";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String getNotFound()
/*     */   {
/* 674 */     return "HTTP/1.1 404 Not Found\r\nConnection: Close\r\nContent-Length: 0\r\n\r\n";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String getRangeNotSatisfiable()
/*     */   {
/* 683 */     return "HTTP/1.1 416 Not Satisfiable\r\nConnection: Close\r\nContent-Length: 0\r\n\r\n";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writeReply(final NetworkConnection connection, TransportHelper transport, final String data)
/*     */   {
/*     */     byte[] bytes;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 699 */       bytes = data.getBytes("ISO-8859-1");
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/* 703 */       bytes = data.getBytes();
/*     */     }
/*     */     
/* 706 */     final ByteBuffer bb = ByteBuffer.wrap(bytes);
/*     */     try
/*     */     {
/* 709 */       transport.write(bb, false);
/*     */       
/* 711 */       if (bb.remaining() > 0)
/*     */       {
/* 713 */         transport.registerForWriteSelects(new TransportHelper.selectListener()
/*     */         {
/*     */ 
/*     */           public boolean selectSuccess(TransportHelper helper, Object attachment)
/*     */           {
/*     */ 
/*     */             try
/*     */             {
/*     */ 
/* 722 */               int written = helper.write(bb, false);
/*     */               
/* 724 */               if (bb.remaining() > 0)
/*     */               {
/* 726 */                 helper.registerForWriteSelects(this, null);
/*     */               }
/*     */               else
/*     */               {
/* 730 */                 if (Logger.isEnabled()) {
/* 731 */                   Logger.log(new LogEvent(HTTPNetworkManager.LOGID, "HTTP connection from " + connection.getEndpoint().getNotionalAddress() + " closed"));
/*     */                 }
/*     */                 
/* 734 */                 connection.close(null);
/*     */               }
/*     */               
/* 737 */               return written > 0;
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 741 */               helper.cancelWriteSelects();
/*     */               
/* 743 */               if (Logger.isEnabled()) {
/* 744 */                 Logger.log(new LogEvent(HTTPNetworkManager.LOGID, "HTTP connection from " + connection.getEndpoint().getNotionalAddress() + " failed to write error '" + data + "'"));
/*     */               }
/*     */               
/* 747 */               connection.close(e == null ? null : Debug.getNestedExceptionMessage(e));
/*     */             }
/* 749 */             return false;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           public void selectFailure(TransportHelper helper, Object attachment, Throwable msg)
/*     */           {
/* 759 */             helper.cancelWriteSelects();
/*     */             
/* 761 */             if (Logger.isEnabled()) {
/* 762 */               Logger.log(new LogEvent(HTTPNetworkManager.LOGID, "HTTP connection from " + connection.getEndpoint().getNotionalAddress() + " failed to write error '" + data + "'"));
/*     */             }
/*     */             
/* 765 */             connection.close(msg == null ? null : Debug.getNestedExceptionMessage(msg)); } }, null);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 771 */         if (Logger.isEnabled()) {
/* 772 */           Logger.log(new LogEvent(LOGID, "HTTP connection from " + connection.getEndpoint().getNotionalAddress() + " closed"));
/*     */         }
/*     */         
/* 775 */         connection.close(null);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 779 */       if (Logger.isEnabled()) {
/* 780 */         Logger.log(new LogEvent(LOGID, "HTTP connection from " + connection.getEndpoint().getNotionalAddress() + " failed to write error '" + data + "'"));
/*     */       }
/*     */       
/* 783 */       connection.close(e == null ? null : Debug.getNestedExceptionMessage(e));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addURLHandler(URLHandler handler)
/*     */   {
/* 791 */     this.url_handlers.add(handler);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeURLHandler(URLHandler handler)
/*     */   {
/* 798 */     this.url_handlers.remove(handler);
/*     */   }
/*     */   
/*     */   public static abstract interface URLHandler
/*     */   {
/*     */     public abstract boolean matches(String paramString);
/*     */     
/*     */     public abstract void handle(TransportHelper paramTransportHelper, String paramString);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */