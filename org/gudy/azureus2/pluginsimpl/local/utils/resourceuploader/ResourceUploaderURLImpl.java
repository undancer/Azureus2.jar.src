/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.resourceuploader;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.PasswordAuthentication;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import org.gudy.azureus2.core3.security.SEPasswordListener;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*     */ import org.gudy.azureus2.plugins.utils.resourceuploader.ResourceUploader;
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
/*     */ public class ResourceUploaderURLImpl
/*     */   implements ResourceUploader, SEPasswordListener
/*     */ {
/*     */   private URL target;
/*     */   private InputStream data;
/*     */   private String user_name;
/*     */   private String password;
/*  51 */   private Map properties = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ResourceUploaderURLImpl(URL _target, InputStream _data, String _user_name, String _password)
/*     */   {
/*  61 */     this.target = _target;
/*  62 */     this.data = _data;
/*  63 */     this.user_name = _user_name;
/*  64 */     this.password = _password;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setProperty(String name, Object value)
/*     */     throws ResourceDownloaderException
/*     */   {
/*  74 */     this.properties.put(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object getProperty(String name)
/*     */     throws ResourceDownloaderException
/*     */   {
/*  83 */     return this.properties.get(name);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public InputStream upload()
/*     */     throws org.gudy.azureus2.plugins.utils.resourceuploader.ResourceUploaderException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: new 201	java/net/URL
/*     */     //   3: dup
/*     */     //   4: aload_0
/*     */     //   5: getfield 352	org/gudy/azureus2/pluginsimpl/local/utils/resourceuploader/ResourceUploaderURLImpl:target	Ljava/net/URL;
/*     */     //   8: invokevirtual 393	java/net/URL:toString	()Ljava/lang/String;
/*     */     //   11: ldc 3
/*     */     //   13: ldc 4
/*     */     //   15: invokevirtual 370	java/lang/String:replaceAll	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
/*     */     //   18: invokespecial 394	java/net/URL:<init>	(Ljava/lang/String;)V
/*     */     //   21: astore_1
/*     */     //   22: aload_1
/*     */     //   23: invokevirtual 391	java/net/URL:getProtocol	()Ljava/lang/String;
/*     */     //   26: invokevirtual 363	java/lang/String:toLowerCase	()Ljava/lang/String;
/*     */     //   29: astore_2
/*     */     //   30: aload_1
/*     */     //   31: invokevirtual 390	java/net/URL:getPort	()I
/*     */     //   34: iconst_m1
/*     */     //   35: if_icmpne +164 -> 199
/*     */     //   38: aload_2
/*     */     //   39: ldc 23
/*     */     //   41: invokevirtual 362	java/lang/String:equals	(Ljava/lang/Object;)Z
/*     */     //   44: ifeq +9 -> 53
/*     */     //   47: bipush 80
/*     */     //   49: istore_3
/*     */     //   50: goto +7 -> 57
/*     */     //   53: sipush 443
/*     */     //   56: istore_3
/*     */     //   57: aload_0
/*     */     //   58: getfield 352	org/gudy/azureus2/pluginsimpl/local/utils/resourceuploader/ResourceUploaderURLImpl:target	Ljava/net/URL;
/*     */     //   61: invokevirtual 393	java/net/URL:toString	()Ljava/lang/String;
/*     */     //   64: ldc 3
/*     */     //   66: ldc 4
/*     */     //   68: invokevirtual 370	java/lang/String:replaceAll	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
/*     */     //   71: astore 4
/*     */     //   73: aload 4
/*     */     //   75: ldc 11
/*     */     //   77: invokevirtual 366	java/lang/String:indexOf	(Ljava/lang/String;)I
/*     */     //   80: istore 5
/*     */     //   82: aload 4
/*     */     //   84: ldc 9
/*     */     //   86: iload 5
/*     */     //   88: iconst_4
/*     */     //   89: iadd
/*     */     //   90: invokevirtual 369	java/lang/String:indexOf	(Ljava/lang/String;I)I
/*     */     //   93: istore 5
/*     */     //   95: iload 5
/*     */     //   97: iconst_m1
/*     */     //   98: if_icmpne +43 -> 141
/*     */     //   101: new 201	java/net/URL
/*     */     //   104: dup
/*     */     //   105: new 196	java/lang/StringBuilder
/*     */     //   108: dup
/*     */     //   109: invokespecial 371	java/lang/StringBuilder:<init>	()V
/*     */     //   112: aload 4
/*     */     //   114: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   117: ldc 10
/*     */     //   119: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   122: iload_3
/*     */     //   123: invokevirtual 373	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   126: ldc 9
/*     */     //   128: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   131: invokevirtual 372	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   134: invokespecial 394	java/net/URL:<init>	(Ljava/lang/String;)V
/*     */     //   137: astore_1
/*     */     //   138: goto +51 -> 189
/*     */     //   141: new 201	java/net/URL
/*     */     //   144: dup
/*     */     //   145: new 196	java/lang/StringBuilder
/*     */     //   148: dup
/*     */     //   149: invokespecial 371	java/lang/StringBuilder:<init>	()V
/*     */     //   152: aload 4
/*     */     //   154: iconst_0
/*     */     //   155: iload 5
/*     */     //   157: invokevirtual 365	java/lang/String:substring	(II)Ljava/lang/String;
/*     */     //   160: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   163: ldc 10
/*     */     //   165: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   168: iload_3
/*     */     //   169: invokevirtual 373	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   172: aload 4
/*     */     //   174: iload 5
/*     */     //   176: invokevirtual 364	java/lang/String:substring	(I)Ljava/lang/String;
/*     */     //   179: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   182: invokevirtual 372	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   185: invokespecial 394	java/net/URL:<init>	(Ljava/lang/String;)V
/*     */     //   188: astore_1
/*     */     //   189: goto +10 -> 199
/*     */     //   192: astore 4
/*     */     //   194: aload 4
/*     */     //   196: invokestatic 404	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*     */     //   199: aload_1
/*     */     //   200: invokestatic 403	org/gudy/azureus2/core3/util/AddressUtils:adjustURL	(Ljava/net/URL;)Ljava/net/URL;
/*     */     //   203: astore_1
/*     */     //   204: aload_0
/*     */     //   205: getfield 351	org/gudy/azureus2/pluginsimpl/local/utils/resourceuploader/ResourceUploaderURLImpl:user_name	Ljava/lang/String;
/*     */     //   208: ifnull +8 -> 216
/*     */     //   211: aload_1
/*     */     //   212: aload_0
/*     */     //   213: invokestatic 402	org/gudy/azureus2/core3/security/SESecurityManager:setPasswordHandler	(Ljava/net/URL;Lorg/gudy/azureus2/core3/security/SEPasswordListener;)V
/*     */     //   216: iconst_0
/*     */     //   217: istore_3
/*     */     //   218: iload_3
/*     */     //   219: iconst_2
/*     */     //   220: if_icmpge +349 -> 569
/*     */     //   223: aload_1
/*     */     //   224: invokevirtual 391	java/net/URL:getProtocol	()Ljava/lang/String;
/*     */     //   227: ldc 24
/*     */     //   229: invokevirtual 367	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
/*     */     //   232: ifeq +32 -> 264
/*     */     //   235: aload_1
/*     */     //   236: invokevirtual 395	java/net/URL:openConnection	()Ljava/net/URLConnection;
/*     */     //   239: checkcast 209	javax/net/ssl/HttpsURLConnection
/*     */     //   242: astore 5
/*     */     //   244: aload 5
/*     */     //   246: new 222	org/gudy/azureus2/pluginsimpl/local/utils/resourceuploader/ResourceUploaderURLImpl$1
/*     */     //   249: dup
/*     */     //   250: aload_0
/*     */     //   251: invokespecial 413	org/gudy/azureus2/pluginsimpl/local/utils/resourceuploader/ResourceUploaderURLImpl$1:<init>	(Lorg/gudy/azureus2/pluginsimpl/local/utils/resourceuploader/ResourceUploaderURLImpl;)V
/*     */     //   254: invokevirtual 400	javax/net/ssl/HttpsURLConnection:setHostnameVerifier	(Ljavax/net/ssl/HostnameVerifier;)V
/*     */     //   257: aload 5
/*     */     //   259: astore 4
/*     */     //   261: goto +12 -> 273
/*     */     //   264: aload_1
/*     */     //   265: invokevirtual 395	java/net/URL:openConnection	()Ljava/net/URLConnection;
/*     */     //   268: checkcast 198	java/net/HttpURLConnection
/*     */     //   271: astore 4
/*     */     //   273: aload 4
/*     */     //   275: ldc 18
/*     */     //   277: invokevirtual 385	java/net/HttpURLConnection:setRequestMethod	(Ljava/lang/String;)V
/*     */     //   280: new 207	java/util/Properties
/*     */     //   283: dup
/*     */     //   284: invokespecial 398	java/util/Properties:<init>	()V
/*     */     //   287: astore 5
/*     */     //   289: invokestatic 409	org/gudy/azureus2/pluginsimpl/local/clientid/ClientIDManagerImpl:getSingleton	()Lorg/gudy/azureus2/pluginsimpl/local/clientid/ClientIDManagerImpl;
/*     */     //   292: invokevirtual 408	org/gudy/azureus2/pluginsimpl/local/clientid/ClientIDManagerImpl:getGenerator	()Lorg/gudy/azureus2/plugins/clientid/ClientIDGenerator;
/*     */     //   295: aconst_null
/*     */     //   296: aload 5
/*     */     //   298: invokeinterface 422 3 0
/*     */     //   303: aload 5
/*     */     //   305: ldc 22
/*     */     //   307: invokevirtual 399	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   310: astore 6
/*     */     //   312: aload 4
/*     */     //   314: ldc 22
/*     */     //   316: aload 6
/*     */     //   318: invokevirtual 387	java/net/HttpURLConnection:setRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
/*     */     //   321: aload_0
/*     */     //   322: aload 4
/*     */     //   324: iconst_0
/*     */     //   325: invokevirtual 411	org/gudy/azureus2/pluginsimpl/local/utils/resourceuploader/ResourceUploaderURLImpl:setRequestProperties	(Ljava/net/HttpURLConnection;Z)V
/*     */     //   328: aload 4
/*     */     //   330: iconst_1
/*     */     //   331: invokevirtual 380	java/net/HttpURLConnection:setDoOutput	(Z)V
/*     */     //   334: aload 4
/*     */     //   336: iconst_1
/*     */     //   337: invokevirtual 379	java/net/HttpURLConnection:setDoInput	(Z)V
/*     */     //   340: aload 4
/*     */     //   342: invokevirtual 382	java/net/HttpURLConnection:getOutputStream	()Ljava/io/OutputStream;
/*     */     //   345: astore 7
/*     */     //   347: ldc 1
/*     */     //   349: newarray <illegal type>
/*     */     //   351: astore 8
/*     */     //   353: aload_0
/*     */     //   354: getfield 349	org/gudy/azureus2/pluginsimpl/local/utils/resourceuploader/ResourceUploaderURLImpl:data	Ljava/io/InputStream;
/*     */     //   357: aload 8
/*     */     //   359: invokevirtual 356	java/io/InputStream:read	([B)I
/*     */     //   362: istore 9
/*     */     //   364: iload 9
/*     */     //   366: ifgt +6 -> 372
/*     */     //   369: goto +16 -> 385
/*     */     //   372: aload 7
/*     */     //   374: aload 8
/*     */     //   376: iconst_0
/*     */     //   377: iload 9
/*     */     //   379: invokevirtual 357	java/io/OutputStream:write	([BII)V
/*     */     //   382: goto -29 -> 353
/*     */     //   385: aload 4
/*     */     //   387: invokevirtual 378	java/net/HttpURLConnection:connect	()V
/*     */     //   390: aload 4
/*     */     //   392: invokevirtual 377	java/net/HttpURLConnection:getResponseCode	()I
/*     */     //   395: istore 9
/*     */     //   397: iload 9
/*     */     //   399: sipush 202
/*     */     //   402: if_icmpeq +67 -> 469
/*     */     //   405: iload 9
/*     */     //   407: sipush 200
/*     */     //   410: if_icmpeq +59 -> 469
/*     */     //   413: new 219	org/gudy/azureus2/plugins/utils/resourceuploader/ResourceUploaderException
/*     */     //   416: dup
/*     */     //   417: new 196	java/lang/StringBuilder
/*     */     //   420: dup
/*     */     //   421: invokespecial 371	java/lang/StringBuilder:<init>	()V
/*     */     //   424: ldc 14
/*     */     //   426: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   429: aload_1
/*     */     //   430: invokevirtual 393	java/net/URL:toString	()Ljava/lang/String;
/*     */     //   433: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   436: ldc 7
/*     */     //   438: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   441: iload 9
/*     */     //   443: invokestatic 358	java/lang/Integer:toString	(I)Ljava/lang/String;
/*     */     //   446: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   449: ldc 3
/*     */     //   451: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   454: aload 4
/*     */     //   456: invokevirtual 384	java/net/HttpURLConnection:getResponseMessage	()Ljava/lang/String;
/*     */     //   459: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   462: invokevirtual 372	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   465: invokespecial 406	org/gudy/azureus2/plugins/utils/resourceuploader/ResourceUploaderException:<init>	(Ljava/lang/String;)V
/*     */     //   468: athrow
/*     */     //   469: aload 4
/*     */     //   471: invokevirtual 381	java/net/HttpURLConnection:getInputStream	()Ljava/io/InputStream;
/*     */     //   474: astore 10
/*     */     //   476: aload_0
/*     */     //   477: aload 4
/*     */     //   479: invokevirtual 410	org/gudy/azureus2/pluginsimpl/local/utils/resourceuploader/ResourceUploaderURLImpl:getRequestProperties	(Ljava/net/HttpURLConnection;)V
/*     */     //   482: aload 10
/*     */     //   484: astore 11
/*     */     //   486: aload_0
/*     */     //   487: getfield 351	org/gudy/azureus2/pluginsimpl/local/utils/resourceuploader/ResourceUploaderURLImpl:user_name	Ljava/lang/String;
/*     */     //   490: ifnull +8 -> 498
/*     */     //   493: aload_1
/*     */     //   494: aconst_null
/*     */     //   495: invokestatic 402	org/gudy/azureus2/core3/security/SESecurityManager:setPasswordHandler	(Ljava/net/URL;Lorg/gudy/azureus2/core3/security/SEPasswordListener;)V
/*     */     //   498: aload_0
/*     */     //   499: getfield 349	org/gudy/azureus2/pluginsimpl/local/utils/resourceuploader/ResourceUploaderURLImpl:data	Ljava/io/InputStream;
/*     */     //   502: invokevirtual 355	java/io/InputStream:close	()V
/*     */     //   505: goto +10 -> 515
/*     */     //   508: astore 12
/*     */     //   510: aload 12
/*     */     //   512: invokevirtual 376	java/lang/Throwable:printStackTrace	()V
/*     */     //   515: aload 11
/*     */     //   517: areturn
/*     */     //   518: astore 4
/*     */     //   520: iload_3
/*     */     //   521: ifne +13 -> 534
/*     */     //   524: aload_1
/*     */     //   525: invokestatic 401	org/gudy/azureus2/core3/security/SESecurityManager:installServerCertificates	(Ljava/net/URL;)Ljavax/net/ssl/SSLSocketFactory;
/*     */     //   528: ifnull +6 -> 534
/*     */     //   531: goto +32 -> 563
/*     */     //   534: aload 4
/*     */     //   536: athrow
/*     */     //   537: astore 4
/*     */     //   539: iload_3
/*     */     //   540: ifne +20 -> 560
/*     */     //   543: aload_1
/*     */     //   544: invokestatic 405	org/gudy/azureus2/core3/util/UrlUtils:getIPV4Fallback	(Ljava/net/URL;)Ljava/net/URL;
/*     */     //   547: astore 5
/*     */     //   549: aload 5
/*     */     //   551: ifnull +9 -> 560
/*     */     //   554: aload 5
/*     */     //   556: astore_1
/*     */     //   557: goto +6 -> 563
/*     */     //   560: aload 4
/*     */     //   562: athrow
/*     */     //   563: iinc 3 1
/*     */     //   566: goto -348 -> 218
/*     */     //   569: new 219	org/gudy/azureus2/plugins/utils/resourceuploader/ResourceUploaderException
/*     */     //   572: dup
/*     */     //   573: ldc 19
/*     */     //   575: invokespecial 406	org/gudy/azureus2/plugins/utils/resourceuploader/ResourceUploaderException:<init>	(Ljava/lang/String;)V
/*     */     //   578: athrow
/*     */     //   579: astore 13
/*     */     //   581: aload_0
/*     */     //   582: getfield 351	org/gudy/azureus2/pluginsimpl/local/utils/resourceuploader/ResourceUploaderURLImpl:user_name	Ljava/lang/String;
/*     */     //   585: ifnull +8 -> 593
/*     */     //   588: aload_1
/*     */     //   589: aconst_null
/*     */     //   590: invokestatic 402	org/gudy/azureus2/core3/security/SESecurityManager:setPasswordHandler	(Ljava/net/URL;Lorg/gudy/azureus2/core3/security/SEPasswordListener;)V
/*     */     //   593: aload 13
/*     */     //   595: athrow
/*     */     //   596: astore_1
/*     */     //   597: new 219	org/gudy/azureus2/plugins/utils/resourceuploader/ResourceUploaderException
/*     */     //   600: dup
/*     */     //   601: new 196	java/lang/StringBuilder
/*     */     //   604: dup
/*     */     //   605: invokespecial 371	java/lang/StringBuilder:<init>	()V
/*     */     //   608: ldc 16
/*     */     //   610: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   613: aload_0
/*     */     //   614: getfield 352	org/gudy/azureus2/pluginsimpl/local/utils/resourceuploader/ResourceUploaderURLImpl:target	Ljava/net/URL;
/*     */     //   617: invokevirtual 374	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*     */     //   620: ldc 6
/*     */     //   622: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   625: aload_1
/*     */     //   626: invokevirtual 388	java/net/MalformedURLException:getMessage	()Ljava/lang/String;
/*     */     //   629: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   632: invokevirtual 372	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   635: aload_1
/*     */     //   636: invokespecial 407	org/gudy/azureus2/plugins/utils/resourceuploader/ResourceUploaderException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   639: athrow
/*     */     //   640: astore_1
/*     */     //   641: new 219	org/gudy/azureus2/plugins/utils/resourceuploader/ResourceUploaderException
/*     */     //   644: dup
/*     */     //   645: new 196	java/lang/StringBuilder
/*     */     //   648: dup
/*     */     //   649: invokespecial 371	java/lang/StringBuilder:<init>	()V
/*     */     //   652: ldc 15
/*     */     //   654: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   657: aload_0
/*     */     //   658: getfield 352	org/gudy/azureus2/pluginsimpl/local/utils/resourceuploader/ResourceUploaderURLImpl:target	Ljava/net/URL;
/*     */     //   661: invokevirtual 374	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*     */     //   664: ldc 8
/*     */     //   666: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   669: aload_1
/*     */     //   670: invokevirtual 396	java/net/UnknownHostException:getMessage	()Ljava/lang/String;
/*     */     //   673: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   676: ldc 5
/*     */     //   678: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   681: invokevirtual 372	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   684: aload_1
/*     */     //   685: invokespecial 407	org/gudy/azureus2/plugins/utils/resourceuploader/ResourceUploaderException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   688: athrow
/*     */     //   689: astore_1
/*     */     //   690: new 219	org/gudy/azureus2/plugins/utils/resourceuploader/ResourceUploaderException
/*     */     //   693: dup
/*     */     //   694: new 196	java/lang/StringBuilder
/*     */     //   697: dup
/*     */     //   698: invokespecial 371	java/lang/StringBuilder:<init>	()V
/*     */     //   701: ldc 17
/*     */     //   703: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   706: aload_0
/*     */     //   707: getfield 352	org/gudy/azureus2/pluginsimpl/local/utils/resourceuploader/ResourceUploaderURLImpl:target	Ljava/net/URL;
/*     */     //   710: invokevirtual 374	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*     */     //   713: ldc 6
/*     */     //   715: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   718: aload_1
/*     */     //   719: invokevirtual 354	java/io/IOException:toString	()Ljava/lang/String;
/*     */     //   722: invokevirtual 375	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   725: invokevirtual 372	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   728: aload_1
/*     */     //   729: invokespecial 407	org/gudy/azureus2/plugins/utils/resourceuploader/ResourceUploaderException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   732: athrow
/*     */     //   733: astore_1
/*     */     //   734: aload_1
/*     */     //   735: instanceof 219
/*     */     //   738: ifeq +11 -> 749
/*     */     //   741: aload_1
/*     */     //   742: checkcast 219	org/gudy/azureus2/plugins/utils/resourceuploader/ResourceUploaderException
/*     */     //   745: astore_2
/*     */     //   746: goto +14 -> 760
/*     */     //   749: new 219	org/gudy/azureus2/plugins/utils/resourceuploader/ResourceUploaderException
/*     */     //   752: dup
/*     */     //   753: ldc 21
/*     */     //   755: aload_1
/*     */     //   756: invokespecial 407	org/gudy/azureus2/plugins/utils/resourceuploader/ResourceUploaderException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   759: astore_2
/*     */     //   760: aload_2
/*     */     //   761: athrow
/*     */     //   762: astore 14
/*     */     //   764: aload_0
/*     */     //   765: getfield 349	org/gudy/azureus2/pluginsimpl/local/utils/resourceuploader/ResourceUploaderURLImpl:data	Ljava/io/InputStream;
/*     */     //   768: invokevirtual 355	java/io/InputStream:close	()V
/*     */     //   771: goto +10 -> 781
/*     */     //   774: astore 15
/*     */     //   776: aload 15
/*     */     //   778: invokevirtual 376	java/lang/Throwable:printStackTrace	()V
/*     */     //   781: aload 14
/*     */     //   783: athrow
/*     */     // Line number table:
/*     */     //   Java source line #94	-> byte code offset #0
/*     */     //   Java source line #98	-> byte code offset #22
/*     */     //   Java source line #100	-> byte code offset #30
/*     */     //   Java source line #104	-> byte code offset #38
/*     */     //   Java source line #106	-> byte code offset #47
/*     */     //   Java source line #110	-> byte code offset #53
/*     */     //   Java source line #114	-> byte code offset #57
/*     */     //   Java source line #116	-> byte code offset #73
/*     */     //   Java source line #118	-> byte code offset #82
/*     */     //   Java source line #122	-> byte code offset #95
/*     */     //   Java source line #124	-> byte code offset #101
/*     */     //   Java source line #128	-> byte code offset #141
/*     */     //   Java source line #134	-> byte code offset #189
/*     */     //   Java source line #131	-> byte code offset #192
/*     */     //   Java source line #133	-> byte code offset #194
/*     */     //   Java source line #137	-> byte code offset #199
/*     */     //   Java source line #140	-> byte code offset #204
/*     */     //   Java source line #142	-> byte code offset #211
/*     */     //   Java source line #145	-> byte code offset #216
/*     */     //   Java source line #150	-> byte code offset #223
/*     */     //   Java source line #154	-> byte code offset #235
/*     */     //   Java source line #158	-> byte code offset #244
/*     */     //   Java source line #170	-> byte code offset #257
/*     */     //   Java source line #172	-> byte code offset #261
/*     */     //   Java source line #174	-> byte code offset #264
/*     */     //   Java source line #178	-> byte code offset #273
/*     */     //   Java source line #180	-> byte code offset #280
/*     */     //   Java source line #182	-> byte code offset #289
/*     */     //   Java source line #184	-> byte code offset #303
/*     */     //   Java source line #186	-> byte code offset #312
/*     */     //   Java source line #188	-> byte code offset #321
/*     */     //   Java source line #190	-> byte code offset #328
/*     */     //   Java source line #191	-> byte code offset #334
/*     */     //   Java source line #193	-> byte code offset #340
/*     */     //   Java source line #195	-> byte code offset #347
/*     */     //   Java source line #199	-> byte code offset #353
/*     */     //   Java source line #201	-> byte code offset #364
/*     */     //   Java source line #203	-> byte code offset #369
/*     */     //   Java source line #206	-> byte code offset #372
/*     */     //   Java source line #207	-> byte code offset #382
/*     */     //   Java source line #209	-> byte code offset #385
/*     */     //   Java source line #211	-> byte code offset #390
/*     */     //   Java source line #213	-> byte code offset #397
/*     */     //   Java source line #215	-> byte code offset #413
/*     */     //   Java source line #218	-> byte code offset #469
/*     */     //   Java source line #220	-> byte code offset #476
/*     */     //   Java source line #222	-> byte code offset #482
/*     */     //   Java source line #260	-> byte code offset #486
/*     */     //   Java source line #262	-> byte code offset #493
/*     */     //   Java source line #295	-> byte code offset #498
/*     */     //   Java source line #300	-> byte code offset #505
/*     */     //   Java source line #297	-> byte code offset #508
/*     */     //   Java source line #299	-> byte code offset #510
/*     */     //   Java source line #300	-> byte code offset #515
/*     */     //   Java source line #224	-> byte code offset #518
/*     */     //   Java source line #226	-> byte code offset #520
/*     */     //   Java source line #228	-> byte code offset #524
/*     */     //   Java source line #232	-> byte code offset #531
/*     */     //   Java source line #236	-> byte code offset #534
/*     */     //   Java source line #238	-> byte code offset #537
/*     */     //   Java source line #240	-> byte code offset #539
/*     */     //   Java source line #242	-> byte code offset #543
/*     */     //   Java source line #244	-> byte code offset #549
/*     */     //   Java source line #246	-> byte code offset #554
/*     */     //   Java source line #248	-> byte code offset #557
/*     */     //   Java source line #252	-> byte code offset #560
/*     */     //   Java source line #145	-> byte code offset #563
/*     */     //   Java source line #256	-> byte code offset #569
/*     */     //   Java source line #260	-> byte code offset #579
/*     */     //   Java source line #262	-> byte code offset #588
/*     */     //   Java source line #265	-> byte code offset #596
/*     */     //   Java source line #267	-> byte code offset #597
/*     */     //   Java source line #269	-> byte code offset #640
/*     */     //   Java source line #271	-> byte code offset #641
/*     */     //   Java source line #273	-> byte code offset #689
/*     */     //   Java source line #275	-> byte code offset #690
/*     */     //   Java source line #277	-> byte code offset #733
/*     */     //   Java source line #281	-> byte code offset #734
/*     */     //   Java source line #283	-> byte code offset #741
/*     */     //   Java source line #287	-> byte code offset #749
/*     */     //   Java source line #290	-> byte code offset #760
/*     */     //   Java source line #294	-> byte code offset #762
/*     */     //   Java source line #295	-> byte code offset #764
/*     */     //   Java source line #300	-> byte code offset #771
/*     */     //   Java source line #297	-> byte code offset #774
/*     */     //   Java source line #299	-> byte code offset #776
/*     */     //   Java source line #300	-> byte code offset #781
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	784	0	this	ResourceUploaderURLImpl
/*     */     //   21	568	1	url	URL
/*     */     //   596	40	1	e	java.net.MalformedURLException
/*     */     //   640	45	1	e	java.net.UnknownHostException
/*     */     //   689	40	1	e	java.io.IOException
/*     */     //   733	23	1	e	Throwable
/*     */     //   29	10	2	protocol	String
/*     */     //   745	2	2	rde	org.gudy.azureus2.plugins.utils.resourceuploader.ResourceUploaderException
/*     */     //   759	2	2	rde	org.gudy.azureus2.plugins.utils.resourceuploader.ResourceUploaderException
/*     */     //   49	2	3	target_port	int
/*     */     //   56	113	3	target_port	int
/*     */     //   217	347	3	i	int
/*     */     //   71	102	4	str	String
/*     */     //   192	3	4	e	Throwable
/*     */     //   259	3	4	con	HttpURLConnection
/*     */     //   271	207	4	con	HttpURLConnection
/*     */     //   518	17	4	e	javax.net.ssl.SSLException
/*     */     //   537	24	4	e	java.io.IOException
/*     */     //   80	95	5	pos	int
/*     */     //   242	16	5	ssl_con	javax.net.ssl.HttpsURLConnection
/*     */     //   287	17	5	props	java.util.Properties
/*     */     //   547	8	5	retry_url	URL
/*     */     //   310	7	6	ua	String
/*     */     //   345	28	7	os	java.io.OutputStream
/*     */     //   351	24	8	buffer	byte[]
/*     */     //   362	16	9	len	int
/*     */     //   395	47	9	response	int
/*     */     //   474	9	10	is	InputStream
/*     */     //   484	32	11	localInputStream1	InputStream
/*     */     //   508	3	12	e	Throwable
/*     */     //   579	15	13	localObject1	Object
/*     */     //   762	20	14	localObject2	Object
/*     */     //   774	3	15	e	Throwable
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   57	189	192	java/lang/Throwable
/*     */     //   498	505	508	java/lang/Throwable
/*     */     //   223	486	518	javax/net/ssl/SSLException
/*     */     //   223	486	537	java/io/IOException
/*     */     //   204	486	579	finally
/*     */     //   518	581	579	finally
/*     */     //   0	498	596	java/net/MalformedURLException
/*     */     //   518	596	596	java/net/MalformedURLException
/*     */     //   0	498	640	java/net/UnknownHostException
/*     */     //   518	596	640	java/net/UnknownHostException
/*     */     //   0	498	689	java/io/IOException
/*     */     //   518	596	689	java/io/IOException
/*     */     //   0	498	733	java/lang/Throwable
/*     */     //   518	733	733	java/lang/Throwable
/*     */     //   0	498	762	finally
/*     */     //   518	764	762	finally
/*     */     //   764	771	774	java/lang/Throwable
/*     */   }
/*     */   
/*     */   protected void setRequestProperties(HttpURLConnection con, boolean use_compression)
/*     */   {
/* 310 */     Iterator it = this.properties.entrySet().iterator();
/*     */     
/* 312 */     while (it.hasNext())
/*     */     {
/* 314 */       Map.Entry entry = (Map.Entry)it.next();
/*     */       
/* 316 */       String key = (String)entry.getKey();
/* 317 */       Object value = entry.getValue();
/*     */       
/* 319 */       if ((key.startsWith("URL_")) && ((value instanceof String)))
/*     */       {
/* 321 */         key = key.substring(4);
/*     */         
/* 323 */         if ((!key.equalsIgnoreCase("Accept-Encoding")) || (use_compression))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 329 */           con.setRequestProperty(key, (String)value);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void getRequestProperties(HttpURLConnection con)
/*     */   {
/*     */     try
/*     */     {
/* 340 */       setProperty("ContentType", con.getContentType());
/*     */       
/* 342 */       Map headers = con.getHeaderFields();
/*     */       
/* 344 */       Iterator it = headers.entrySet().iterator();
/*     */       
/* 346 */       while (it.hasNext())
/*     */       {
/* 348 */         Map.Entry entry = (Map.Entry)it.next();
/*     */         
/* 350 */         String key = (String)entry.getKey();
/* 351 */         Object val = entry.getValue();
/*     */         
/* 353 */         if (key != null)
/*     */         {
/* 355 */           setProperty("URL_" + key, val);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 360 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PasswordAuthentication getAuthentication(String realm, URL tracker)
/*     */   {
/* 369 */     if ((this.user_name == null) || (this.password == null))
/*     */     {
/* 371 */       String user_info = tracker.getUserInfo();
/*     */       
/* 373 */       if (user_info == null)
/*     */       {
/* 375 */         return null;
/*     */       }
/*     */       
/* 378 */       String user_bit = user_info;
/* 379 */       String pw_bit = "";
/*     */       
/* 381 */       int pos = user_info.indexOf(':');
/*     */       
/* 383 */       if (pos != -1)
/*     */       {
/* 385 */         user_bit = user_info.substring(0, pos);
/* 386 */         pw_bit = user_info.substring(pos + 1);
/*     */       }
/*     */       
/* 389 */       return new PasswordAuthentication(user_bit, pw_bit.toCharArray());
/*     */     }
/*     */     
/* 392 */     return new PasswordAuthentication(this.user_name, this.password.toCharArray());
/*     */   }
/*     */   
/*     */   public void setAuthenticationOutcome(String realm, URL tracker, boolean success) {}
/*     */   
/*     */   public void clearPasswords() {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/resourceuploader/ResourceUploaderURLImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */