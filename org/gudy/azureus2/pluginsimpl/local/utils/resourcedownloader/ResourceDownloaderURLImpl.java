/*      */ package org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader;
/*      */ 
/*      */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*      */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.net.PasswordAuthentication;
/*      */ import java.net.Proxy;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.security.cert.CertificateException;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import javax.net.ssl.HostnameVerifier;
/*      */ import javax.net.ssl.SSLSession;
/*      */ import javax.net.ssl.X509TrustManager;
/*      */ import org.gudy.azureus2.core3.security.SEPasswordListener;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderCancelledException;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ResourceDownloaderURLImpl
/*      */   extends ResourceDownloaderBaseImpl
/*      */   implements SEPasswordListener
/*      */ {
/*      */   private static final int BUFFER_SIZE = 32768;
/*      */   private static final int MAX_IN_MEM_READ_SIZE = 262144;
/*      */   private URL original_url;
/*      */   private boolean auth_supplied;
/*      */   private String user_name;
/*      */   private String password;
/*      */   private InputStream input_stream;
/*   85 */   private boolean cancel_download = false;
/*      */   
/*      */   private boolean download_initiated;
/*   88 */   private long size = -2L;
/*      */   
/*   90 */   private boolean force_no_proxy = false;
/*      */   
/*      */   private Proxy force_proxy;
/*      */   
/*      */   private boolean auto_plugin_proxy;
/*      */   
/*      */   private final byte[] post_data;
/*      */   
/*      */ 
/*      */   public ResourceDownloaderURLImpl(ResourceDownloaderBaseImpl _parent, URL _url)
/*      */   {
/*  101 */     this(_parent, _url, false, null, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResourceDownloaderURLImpl(ResourceDownloaderBaseImpl _parent, URL _url, String _user_name, String _password)
/*      */   {
/*  111 */     this(_parent, _url, true, _user_name, _password);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResourceDownloaderURLImpl(ResourceDownloaderBaseImpl _parent, URL _url, boolean _auth_supplied, String _user_name, String _password)
/*      */   {
/*  122 */     this(_parent, _url, null, _auth_supplied, _user_name, _password);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResourceDownloaderURLImpl(ResourceDownloaderBaseImpl _parent, URL _url, byte[] _data, boolean _auth_supplied, String _user_name, String _password)
/*      */   {
/*  144 */     super(_parent);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  157 */     this.original_url = _url;
/*  158 */     this.post_data = _data;
/*  159 */     this.auth_supplied = _auth_supplied;
/*  160 */     this.user_name = _user_name;
/*  161 */     this.password = _password;
/*      */   }
/*      */   
/*      */   protected void setForceNoProxy(boolean force_no_proxy) {
/*  165 */     this.force_no_proxy = force_no_proxy;
/*      */   }
/*      */   
/*      */   protected void setForceProxy(Proxy proxy) {
/*  169 */     this.force_proxy = proxy;
/*      */   }
/*      */   
/*      */   protected void setAutoPluginProxy() {
/*  173 */     this.auto_plugin_proxy = true;
/*      */   }
/*      */   
/*      */ 
/*      */   protected URL getURL()
/*      */   {
/*  179 */     return this.original_url;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getName()
/*      */   {
/*  185 */     return this.original_url.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getSize()
/*      */     throws ResourceDownloaderException
/*      */   {
/*  195 */     if (this.size == -2L) {
/*      */       try
/*      */       {
/*  198 */         ResourceDownloaderURLImpl c = (ResourceDownloaderURLImpl)getClone(this);
/*      */         
/*  200 */         addReportListener(c);
/*      */         
/*  202 */         this.size = c.getSizeSupport();
/*      */         
/*  204 */         setProperties(c);
/*      */       }
/*      */       finally
/*      */       {
/*  208 */         if (this.size == -2L)
/*      */         {
/*  210 */           this.size = -1L;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  215 */     return this.size;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setSize(long l)
/*      */   {
/*  222 */     this.size = l;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setProperty(String name, Object value)
/*      */     throws ResourceDownloaderException
/*      */   {
/*  232 */     setPropertySupport(name, value);
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected long getSizeSupport()
/*      */     throws ResourceDownloaderException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 869	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:original_url	Ljava/net/URL;
/*      */     //   4: invokevirtual 932	java/net/URL:getProtocol	()Ljava/lang/String;
/*      */     //   7: invokevirtual 904	java/lang/String:toLowerCase	()Ljava/lang/String;
/*      */     //   10: astore_1
/*      */     //   11: aload_1
/*      */     //   12: ldc 59
/*      */     //   14: invokevirtual 903	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   17: ifne +48 -> 65
/*      */     //   20: aload_1
/*      */     //   21: ldc 58
/*      */     //   23: invokevirtual 903	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   26: ifne +39 -> 65
/*      */     //   29: aload_1
/*      */     //   30: ldc 49
/*      */     //   32: invokevirtual 903	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   35: ifne +30 -> 65
/*      */     //   38: aload_1
/*      */     //   39: ldc 64
/*      */     //   41: invokevirtual 903	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   44: ifne +21 -> 65
/*      */     //   47: aload_1
/*      */     //   48: ldc 45
/*      */     //   50: invokevirtual 903	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   53: ifne +12 -> 65
/*      */     //   56: aload_1
/*      */     //   57: ldc 52
/*      */     //   59: invokevirtual 903	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   62: ifeq +7 -> 69
/*      */     //   65: ldc2_w 475
/*      */     //   68: lreturn
/*      */     //   69: aload_1
/*      */     //   70: ldc 51
/*      */     //   72: invokevirtual 903	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   75: ifeq +21 -> 96
/*      */     //   78: new 502	java/io/File
/*      */     //   81: dup
/*      */     //   82: aload_0
/*      */     //   83: getfield 869	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:original_url	Ljava/net/URL;
/*      */     //   86: invokevirtual 936	java/net/URL:toURI	()Ljava/net/URI;
/*      */     //   89: invokespecial 883	java/io/File:<init>	(Ljava/net/URI;)V
/*      */     //   92: invokevirtual 881	java/io/File:length	()J
/*      */     //   95: lreturn
/*      */     //   96: aload_0
/*      */     //   97: aload_0
/*      */     //   98: new 513	java/lang/StringBuilder
/*      */     //   101: dup
/*      */     //   102: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   105: ldc 24
/*      */     //   107: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   110: aload_0
/*      */     //   111: getfield 869	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:original_url	Ljava/net/URL;
/*      */     //   114: invokevirtual 919	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*      */     //   117: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   120: invokevirtual 1015	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:reportActivity	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;)V
/*      */     //   123: new 519	java/net/URL
/*      */     //   126: dup
/*      */     //   127: aload_0
/*      */     //   128: getfield 869	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:original_url	Ljava/net/URL;
/*      */     //   131: invokevirtual 934	java/net/URL:toString	()Ljava/lang/String;
/*      */     //   134: ldc 4
/*      */     //   136: ldc 5
/*      */     //   138: invokevirtual 913	java/lang/String:replaceAll	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
/*      */     //   141: invokespecial 935	java/net/URL:<init>	(Ljava/lang/String;)V
/*      */     //   144: astore_2
/*      */     //   145: aload_2
/*      */     //   146: invokestatic 973	org/gudy/azureus2/core3/util/AddressUtils:adjustURL	(Ljava/net/URL;)Ljava/net/URL;
/*      */     //   149: astore_2
/*      */     //   150: aload_2
/*      */     //   151: astore_3
/*      */     //   152: iconst_0
/*      */     //   153: istore 5
/*      */     //   155: aload_0
/*      */     //   156: getfield 860	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:auto_plugin_proxy	Z
/*      */     //   159: ifne +10 -> 169
/*      */     //   162: aload_0
/*      */     //   163: invokevirtual 995	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:isAnonymous	()Z
/*      */     //   166: ifeq +49 -> 215
/*      */     //   169: ldc 50
/*      */     //   171: aload_2
/*      */     //   172: invokestatic 872	com/aelitis/azureus/core/proxy/AEProxyFactory:getPluginProxy	(Ljava/lang/String;Ljava/net/URL;)Lcom/aelitis/azureus/core/proxy/AEProxyFactory$PluginProxy;
/*      */     //   175: astore 4
/*      */     //   177: aload 4
/*      */     //   179: ifnonnull +14 -> 193
/*      */     //   182: new 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   185: dup
/*      */     //   186: aload_0
/*      */     //   187: ldc 29
/*      */     //   189: invokespecial 986	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException:<init>	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;)V
/*      */     //   192: athrow
/*      */     //   193: aload 4
/*      */     //   195: invokeinterface 1028 1 0
/*      */     //   200: astore_2
/*      */     //   201: aload_0
/*      */     //   202: aload 4
/*      */     //   204: invokeinterface 1027 1 0
/*      */     //   209: putfield 868	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:force_proxy	Ljava/net/Proxy;
/*      */     //   212: goto +6 -> 218
/*      */     //   215: aconst_null
/*      */     //   216: astore 4
/*      */     //   218: aload_0
/*      */     //   219: getfield 863	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:force_no_proxy	Z
/*      */     //   222: ifeq +11 -> 233
/*      */     //   225: invokestatic 874	com/aelitis/azureus/core/proxy/AEProxySelectorFactory:getSelector	()Lcom/aelitis/azureus/core/proxy/AEProxySelector;
/*      */     //   228: invokeinterface 1031 1 0
/*      */     //   233: aload_0
/*      */     //   234: getfield 859	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:auth_supplied	Z
/*      */     //   237: ifeq +8 -> 245
/*      */     //   240: aload_2
/*      */     //   241: aload_0
/*      */     //   242: invokestatic 968	org/gudy/azureus2/core3/security/SESecurityManager:setPasswordHandler	(Ljava/net/URL;Lorg/gudy/azureus2/core3/security/SEPasswordListener;)V
/*      */     //   245: iconst_0
/*      */     //   246: istore 6
/*      */     //   248: iconst_0
/*      */     //   249: istore 7
/*      */     //   251: iconst_0
/*      */     //   252: istore 8
/*      */     //   254: iload 8
/*      */     //   256: iconst_2
/*      */     //   257: if_icmpge +660 -> 917
/*      */     //   260: aload_2
/*      */     //   261: invokevirtual 932	java/net/URL:getProtocol	()Ljava/lang/String;
/*      */     //   264: ldc 55
/*      */     //   266: invokevirtual 908	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
/*      */     //   269: ifeq +78 -> 347
/*      */     //   272: aload_0
/*      */     //   273: aload_0
/*      */     //   274: getfield 868	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:force_proxy	Ljava/net/Proxy;
/*      */     //   277: aload_2
/*      */     //   278: invokespecial 1017	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:openConnection	(Ljava/net/Proxy;Ljava/net/URL;)Ljava/net/URLConnection;
/*      */     //   281: checkcast 533	javax/net/ssl/HttpsURLConnection
/*      */     //   284: astore 10
/*      */     //   286: iload 7
/*      */     //   288: ifne +16 -> 304
/*      */     //   291: aload 10
/*      */     //   293: new 557	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl$1
/*      */     //   296: dup
/*      */     //   297: aload_0
/*      */     //   298: invokespecial 1020	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl$1:<init>	(Lorg/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl;)V
/*      */     //   301: invokevirtual 959	javax/net/ssl/HttpsURLConnection:setHostnameVerifier	(Ljavax/net/ssl/HostnameVerifier;)V
/*      */     //   304: iload 6
/*      */     //   306: ifeq +8 -> 314
/*      */     //   309: aload 10
/*      */     //   311: invokestatic 981	org/gudy/azureus2/core3/util/UrlUtils:DHHackIt	(Ljavax/net/ssl/HttpsURLConnection;)V
/*      */     //   314: iload 7
/*      */     //   316: ifeq +24 -> 340
/*      */     //   319: aload 4
/*      */     //   321: ifnull +19 -> 340
/*      */     //   324: aload 4
/*      */     //   326: invokeinterface 1026 1 0
/*      */     //   331: astore 11
/*      */     //   333: aload 11
/*      */     //   335: aload 10
/*      */     //   337: invokestatic 983	org/gudy/azureus2/core3/util/UrlUtils:HTTPSURLConnectionSNIHack	(Ljava/lang/String;Ljavax/net/ssl/HttpsURLConnection;)V
/*      */     //   340: aload 10
/*      */     //   342: astore 9
/*      */     //   344: goto +17 -> 361
/*      */     //   347: aload_0
/*      */     //   348: aload_0
/*      */     //   349: getfield 868	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:force_proxy	Ljava/net/Proxy;
/*      */     //   352: aload_2
/*      */     //   353: invokespecial 1017	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:openConnection	(Ljava/net/Proxy;Ljava/net/URL;)Ljava/net/URLConnection;
/*      */     //   356: checkcast 515	java/net/HttpURLConnection
/*      */     //   359: astore 9
/*      */     //   361: aload 9
/*      */     //   363: aload 4
/*      */     //   365: ifnonnull +7 -> 372
/*      */     //   368: iconst_1
/*      */     //   369: goto +4 -> 373
/*      */     //   372: iconst_0
/*      */     //   373: invokevirtual 923	java/net/HttpURLConnection:setInstanceFollowRedirects	(Z)V
/*      */     //   376: aload 4
/*      */     //   378: ifnull +68 -> 446
/*      */     //   381: aload 9
/*      */     //   383: ldc 26
/*      */     //   385: new 513	java/lang/StringBuilder
/*      */     //   388: dup
/*      */     //   389: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   392: aload 4
/*      */     //   394: invokeinterface 1026 1 0
/*      */     //   399: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   402: aload_3
/*      */     //   403: invokevirtual 930	java/net/URL:getPort	()I
/*      */     //   406: iconst_m1
/*      */     //   407: if_icmpne +8 -> 415
/*      */     //   410: ldc 3
/*      */     //   412: goto +25 -> 437
/*      */     //   415: new 513	java/lang/StringBuilder
/*      */     //   418: dup
/*      */     //   419: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   422: ldc 11
/*      */     //   424: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   427: aload_3
/*      */     //   428: invokevirtual 930	java/net/URL:getPort	()I
/*      */     //   431: invokevirtual 917	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   434: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   437: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   440: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   443: invokevirtual 927	java/net/HttpURLConnection:setRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
/*      */     //   446: aload 9
/*      */     //   448: ldc 25
/*      */     //   450: invokevirtual 926	java/net/HttpURLConnection:setRequestMethod	(Ljava/lang/String;)V
/*      */     //   453: invokestatic 989	org/gudy/azureus2/pluginsimpl/local/clientid/ClientIDManagerImpl:getSingleton	()Lorg/gudy/azureus2/pluginsimpl/local/clientid/ClientIDManagerImpl;
/*      */     //   456: invokevirtual 988	org/gudy/azureus2/pluginsimpl/local/clientid/ClientIDManagerImpl:getGenerator	()Lorg/gudy/azureus2/plugins/clientid/ClientIDGenerator;
/*      */     //   459: astore 10
/*      */     //   461: aload 10
/*      */     //   463: ifnull +40 -> 503
/*      */     //   466: new 528	java/util/Properties
/*      */     //   469: dup
/*      */     //   470: invokespecial 955	java/util/Properties:<init>	()V
/*      */     //   473: astore 11
/*      */     //   475: aload 10
/*      */     //   477: aconst_null
/*      */     //   478: aload 11
/*      */     //   480: invokeinterface 1046 3 0
/*      */     //   485: aload 11
/*      */     //   487: ldc 44
/*      */     //   489: invokevirtual 956	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   492: astore 12
/*      */     //   494: aload 9
/*      */     //   496: ldc 44
/*      */     //   498: aload 12
/*      */     //   500: invokevirtual 927	java/net/HttpURLConnection:setRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
/*      */     //   503: aload_0
/*      */     //   504: aload 9
/*      */     //   506: iconst_0
/*      */     //   507: invokevirtual 1006	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:setRequestProperties	(Ljava/net/URLConnection;Z)V
/*      */     //   510: aload 9
/*      */     //   512: invokevirtual 922	java/net/HttpURLConnection:connect	()V
/*      */     //   515: goto +18 -> 533
/*      */     //   518: astore 11
/*      */     //   520: new 521	java/net/UnknownHostException
/*      */     //   523: dup
/*      */     //   524: aload 11
/*      */     //   526: invokevirtual 873	com/aelitis/azureus/core/proxy/AEProxyFactory$UnknownHostException:getMessage	()Ljava/lang/String;
/*      */     //   529: invokespecial 952	java/net/UnknownHostException:<init>	(Ljava/lang/String;)V
/*      */     //   532: athrow
/*      */     //   533: aload 9
/*      */     //   535: invokevirtual 921	java/net/HttpURLConnection:getResponseCode	()I
/*      */     //   538: istore 11
/*      */     //   540: aload_0
/*      */     //   541: ldc 39
/*      */     //   543: new 510	java/lang/Long
/*      */     //   546: dup
/*      */     //   547: iload 11
/*      */     //   549: i2l
/*      */     //   550: invokespecial 897	java/lang/Long:<init>	(J)V
/*      */     //   553: invokevirtual 1011	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:setProperty	(Ljava/lang/String;Ljava/lang/Object;)V
/*      */     //   556: iload 11
/*      */     //   558: sipush 202
/*      */     //   561: if_icmpeq +169 -> 730
/*      */     //   564: iload 11
/*      */     //   566: sipush 200
/*      */     //   569: if_icmpeq +161 -> 730
/*      */     //   572: iload 11
/*      */     //   574: sipush 302
/*      */     //   577: if_icmpeq +11 -> 588
/*      */     //   580: iload 11
/*      */     //   582: sipush 301
/*      */     //   585: if_icmpne +57 -> 642
/*      */     //   588: ldc2_w 475
/*      */     //   591: lstore 12
/*      */     //   593: aload_0
/*      */     //   594: getfield 859	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:auth_supplied	Z
/*      */     //   597: ifeq +8 -> 605
/*      */     //   600: aload_2
/*      */     //   601: aconst_null
/*      */     //   602: invokestatic 968	org/gudy/azureus2/core3/security/SESecurityManager:setPasswordHandler	(Ljava/net/URL;Lorg/gudy/azureus2/core3/security/SEPasswordListener;)V
/*      */     //   605: aload_0
/*      */     //   606: getfield 863	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:force_no_proxy	Z
/*      */     //   609: ifeq +11 -> 620
/*      */     //   612: invokestatic 874	com/aelitis/azureus/core/proxy/AEProxySelectorFactory:getSelector	()Lcom/aelitis/azureus/core/proxy/AEProxySelector;
/*      */     //   615: invokeinterface 1030 1 0
/*      */     //   620: aload 4
/*      */     //   622: ifnull +17 -> 639
/*      */     //   625: aload 4
/*      */     //   627: iload 5
/*      */     //   629: invokeinterface 1024 2 0
/*      */     //   634: aload_0
/*      */     //   635: aconst_null
/*      */     //   636: putfield 868	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:force_proxy	Ljava/net/Proxy;
/*      */     //   639: lload 12
/*      */     //   641: lreturn
/*      */     //   642: aload_2
/*      */     //   643: astore 12
/*      */     //   645: aload 4
/*      */     //   647: ifnull +24 -> 671
/*      */     //   650: new 519	java/net/URL
/*      */     //   653: dup
/*      */     //   654: aload 4
/*      */     //   656: invokeinterface 1025 1 0
/*      */     //   661: invokespecial 935	java/net/URL:<init>	(Ljava/lang/String;)V
/*      */     //   664: astore 12
/*      */     //   666: goto +5 -> 671
/*      */     //   669: astore 13
/*      */     //   671: new 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   674: dup
/*      */     //   675: aload_0
/*      */     //   676: new 513	java/lang/StringBuilder
/*      */     //   679: dup
/*      */     //   680: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   683: ldc 21
/*      */     //   685: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   688: aload_0
/*      */     //   689: aload 12
/*      */     //   691: invokevirtual 1014	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:trimForDisplay	(Ljava/net/URL;)Ljava/lang/String;
/*      */     //   694: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   697: ldc 8
/*      */     //   699: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   702: iload 11
/*      */     //   704: invokestatic 896	java/lang/Integer:toString	(I)Ljava/lang/String;
/*      */     //   707: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   710: ldc 4
/*      */     //   712: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   715: aload 9
/*      */     //   717: invokevirtual 925	java/net/HttpURLConnection:getResponseMessage	()Ljava/lang/String;
/*      */     //   720: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   723: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   726: invokespecial 986	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException:<init>	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;)V
/*      */     //   729: athrow
/*      */     //   730: aload_0
/*      */     //   731: aload 9
/*      */     //   733: invokevirtual 1005	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:getRequestProperties	(Ljava/net/URLConnection;)V
/*      */     //   736: iconst_1
/*      */     //   737: istore 5
/*      */     //   739: aload 9
/*      */     //   741: invokestatic 980	org/gudy/azureus2/core3/util/UrlUtils:getContentLength	(Ljava/net/URLConnection;)J
/*      */     //   744: lstore 12
/*      */     //   746: aload_0
/*      */     //   747: getfield 859	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:auth_supplied	Z
/*      */     //   750: ifeq +8 -> 758
/*      */     //   753: aload_2
/*      */     //   754: aconst_null
/*      */     //   755: invokestatic 968	org/gudy/azureus2/core3/security/SESecurityManager:setPasswordHandler	(Ljava/net/URL;Lorg/gudy/azureus2/core3/security/SEPasswordListener;)V
/*      */     //   758: aload_0
/*      */     //   759: getfield 863	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:force_no_proxy	Z
/*      */     //   762: ifeq +11 -> 773
/*      */     //   765: invokestatic 874	com/aelitis/azureus/core/proxy/AEProxySelectorFactory:getSelector	()Lcom/aelitis/azureus/core/proxy/AEProxySelector;
/*      */     //   768: invokeinterface 1030 1 0
/*      */     //   773: aload 4
/*      */     //   775: ifnull +17 -> 792
/*      */     //   778: aload 4
/*      */     //   780: iload 5
/*      */     //   782: invokeinterface 1024 2 0
/*      */     //   787: aload_0
/*      */     //   788: aconst_null
/*      */     //   789: putfield 868	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:force_proxy	Ljava/net/Proxy;
/*      */     //   792: lload 12
/*      */     //   794: lreturn
/*      */     //   795: astore 9
/*      */     //   797: aload 9
/*      */     //   799: invokestatic 977	org/gudy/azureus2/core3/util/Debug:getNestedExceptionMessage	(Ljava/lang/Throwable;)Ljava/lang/String;
/*      */     //   802: astore 10
/*      */     //   804: iload 8
/*      */     //   806: iconst_3
/*      */     //   807: if_icmpge +74 -> 881
/*      */     //   810: iconst_0
/*      */     //   811: istore 11
/*      */     //   813: aload 10
/*      */     //   815: ldc 18
/*      */     //   817: invokevirtual 902	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
/*      */     //   820: ifeq +17 -> 837
/*      */     //   823: iload 6
/*      */     //   825: ifne +33 -> 858
/*      */     //   828: iconst_1
/*      */     //   829: istore 6
/*      */     //   831: iconst_1
/*      */     //   832: istore 11
/*      */     //   834: goto +24 -> 858
/*      */     //   837: aload 10
/*      */     //   839: ldc 56
/*      */     //   841: invokevirtual 902	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
/*      */     //   844: ifeq +14 -> 858
/*      */     //   847: iload 7
/*      */     //   849: ifne +9 -> 858
/*      */     //   852: iconst_1
/*      */     //   853: istore 7
/*      */     //   855: iconst_1
/*      */     //   856: istore 11
/*      */     //   858: aload 4
/*      */     //   860: ifnull +13 -> 873
/*      */     //   863: aload_2
/*      */     //   864: invokestatic 966	org/gudy/azureus2/core3/security/SESecurityManager:installServerCertificates	(Ljava/net/URL;)Ljavax/net/ssl/SSLSocketFactory;
/*      */     //   867: ifnull +6 -> 873
/*      */     //   870: iconst_1
/*      */     //   871: istore 11
/*      */     //   873: iload 11
/*      */     //   875: ifeq +6 -> 881
/*      */     //   878: goto +33 -> 911
/*      */     //   881: aload 9
/*      */     //   883: athrow
/*      */     //   884: astore 9
/*      */     //   886: iload 8
/*      */     //   888: ifne +20 -> 908
/*      */     //   891: aload_2
/*      */     //   892: invokestatic 982	org/gudy/azureus2/core3/util/UrlUtils:getIPV4Fallback	(Ljava/net/URL;)Ljava/net/URL;
/*      */     //   895: astore 10
/*      */     //   897: aload 10
/*      */     //   899: ifnull +9 -> 908
/*      */     //   902: aload 10
/*      */     //   904: astore_2
/*      */     //   905: goto +6 -> 911
/*      */     //   908: aload 9
/*      */     //   910: athrow
/*      */     //   911: iinc 8 1
/*      */     //   914: goto -660 -> 254
/*      */     //   917: new 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   920: dup
/*      */     //   921: aload_0
/*      */     //   922: ldc 34
/*      */     //   924: invokespecial 986	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException:<init>	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;)V
/*      */     //   927: athrow
/*      */     //   928: astore 14
/*      */     //   930: aload_0
/*      */     //   931: getfield 859	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:auth_supplied	Z
/*      */     //   934: ifeq +8 -> 942
/*      */     //   937: aload_2
/*      */     //   938: aconst_null
/*      */     //   939: invokestatic 968	org/gudy/azureus2/core3/security/SESecurityManager:setPasswordHandler	(Ljava/net/URL;Lorg/gudy/azureus2/core3/security/SEPasswordListener;)V
/*      */     //   942: aload_0
/*      */     //   943: getfield 863	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:force_no_proxy	Z
/*      */     //   946: ifeq +11 -> 957
/*      */     //   949: invokestatic 874	com/aelitis/azureus/core/proxy/AEProxySelectorFactory:getSelector	()Lcom/aelitis/azureus/core/proxy/AEProxySelector;
/*      */     //   952: invokeinterface 1030 1 0
/*      */     //   957: aload 4
/*      */     //   959: ifnull +17 -> 976
/*      */     //   962: aload 4
/*      */     //   964: iload 5
/*      */     //   966: invokeinterface 1024 2 0
/*      */     //   971: aload_0
/*      */     //   972: aconst_null
/*      */     //   973: putfield 868	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:force_proxy	Ljava/net/Proxy;
/*      */     //   976: aload 14
/*      */     //   978: athrow
/*      */     //   979: astore_2
/*      */     //   980: new 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   983: dup
/*      */     //   984: aload_0
/*      */     //   985: new 513	java/lang/StringBuilder
/*      */     //   988: dup
/*      */     //   989: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   992: ldc 23
/*      */     //   994: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   997: aload_0
/*      */     //   998: getfield 869	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:original_url	Ljava/net/URL;
/*      */     //   1001: invokevirtual 919	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*      */     //   1004: ldc 7
/*      */     //   1006: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1009: aload_2
/*      */     //   1010: invokevirtual 928	java/net/MalformedURLException:getMessage	()Ljava/lang/String;
/*      */     //   1013: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1016: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1019: aload_2
/*      */     //   1020: invokespecial 987	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException:<init>	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   1023: athrow
/*      */     //   1024: astore_2
/*      */     //   1025: new 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   1028: dup
/*      */     //   1029: aload_0
/*      */     //   1030: new 513	java/lang/StringBuilder
/*      */     //   1033: dup
/*      */     //   1034: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   1037: ldc 22
/*      */     //   1039: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1042: aload_0
/*      */     //   1043: aload_0
/*      */     //   1044: getfield 869	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:original_url	Ljava/net/URL;
/*      */     //   1047: invokevirtual 1014	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:trimForDisplay	(Ljava/net/URL;)Ljava/lang/String;
/*      */     //   1050: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1053: ldc 9
/*      */     //   1055: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1058: aload_2
/*      */     //   1059: invokevirtual 951	java/net/UnknownHostException:getMessage	()Ljava/lang/String;
/*      */     //   1062: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1065: ldc 6
/*      */     //   1067: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1070: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1073: aload_2
/*      */     //   1074: invokespecial 987	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException:<init>	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   1077: athrow
/*      */     //   1078: astore_2
/*      */     //   1079: new 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   1082: dup
/*      */     //   1083: aload_0
/*      */     //   1084: new 513	java/lang/StringBuilder
/*      */     //   1087: dup
/*      */     //   1088: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   1091: ldc 27
/*      */     //   1093: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1096: aload_0
/*      */     //   1097: aload_0
/*      */     //   1098: getfield 869	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:original_url	Ljava/net/URL;
/*      */     //   1101: invokevirtual 1014	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:trimForDisplay	(Ljava/net/URL;)Ljava/lang/String;
/*      */     //   1104: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1107: ldc 6
/*      */     //   1109: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1112: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1115: aload_2
/*      */     //   1116: invokespecial 987	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException:<init>	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   1119: athrow
/*      */     //   1120: astore_1
/*      */     //   1121: aload_1
/*      */     //   1122: instanceof 553
/*      */     //   1125: ifeq +11 -> 1136
/*      */     //   1128: aload_1
/*      */     //   1129: checkcast 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   1132: astore_2
/*      */     //   1133: goto +19 -> 1152
/*      */     //   1136: aload_1
/*      */     //   1137: invokestatic 975	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/Throwable;)V
/*      */     //   1140: new 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   1143: dup
/*      */     //   1144: aload_0
/*      */     //   1145: ldc 43
/*      */     //   1147: aload_1
/*      */     //   1148: invokespecial 987	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException:<init>	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   1151: astore_2
/*      */     //   1152: aload_2
/*      */     //   1153: athrow
/*      */     // Line number table:
/*      */     //   Java source line #243	-> byte code offset #0
/*      */     //   Java source line #245	-> byte code offset #11
/*      */     //   Java source line #252	-> byte code offset #65
/*      */     //   Java source line #254	-> byte code offset #69
/*      */     //   Java source line #256	-> byte code offset #78
/*      */     //   Java source line #259	-> byte code offset #96
/*      */     //   Java source line #262	-> byte code offset #123
/*      */     //   Java source line #264	-> byte code offset #145
/*      */     //   Java source line #266	-> byte code offset #150
/*      */     //   Java source line #270	-> byte code offset #152
/*      */     //   Java source line #272	-> byte code offset #155
/*      */     //   Java source line #274	-> byte code offset #169
/*      */     //   Java source line #276	-> byte code offset #177
/*      */     //   Java source line #278	-> byte code offset #182
/*      */     //   Java source line #281	-> byte code offset #193
/*      */     //   Java source line #282	-> byte code offset #201
/*      */     //   Java source line #286	-> byte code offset #215
/*      */     //   Java source line #290	-> byte code offset #218
/*      */     //   Java source line #292	-> byte code offset #225
/*      */     //   Java source line #295	-> byte code offset #233
/*      */     //   Java source line #297	-> byte code offset #240
/*      */     //   Java source line #300	-> byte code offset #245
/*      */     //   Java source line #301	-> byte code offset #248
/*      */     //   Java source line #303	-> byte code offset #251
/*      */     //   Java source line #308	-> byte code offset #260
/*      */     //   Java source line #312	-> byte code offset #272
/*      */     //   Java source line #316	-> byte code offset #286
/*      */     //   Java source line #318	-> byte code offset #291
/*      */     //   Java source line #331	-> byte code offset #304
/*      */     //   Java source line #333	-> byte code offset #309
/*      */     //   Java source line #336	-> byte code offset #314
/*      */     //   Java source line #338	-> byte code offset #324
/*      */     //   Java source line #340	-> byte code offset #333
/*      */     //   Java source line #343	-> byte code offset #340
/*      */     //   Java source line #345	-> byte code offset #344
/*      */     //   Java source line #347	-> byte code offset #347
/*      */     //   Java source line #351	-> byte code offset #361
/*      */     //   Java source line #353	-> byte code offset #376
/*      */     //   Java source line #355	-> byte code offset #381
/*      */     //   Java source line #358	-> byte code offset #446
/*      */     //   Java source line #360	-> byte code offset #453
/*      */     //   Java source line #362	-> byte code offset #461
/*      */     //   Java source line #364	-> byte code offset #466
/*      */     //   Java source line #366	-> byte code offset #475
/*      */     //   Java source line #368	-> byte code offset #485
/*      */     //   Java source line #370	-> byte code offset #494
/*      */     //   Java source line #373	-> byte code offset #503
/*      */     //   Java source line #376	-> byte code offset #510
/*      */     //   Java source line #381	-> byte code offset #515
/*      */     //   Java source line #378	-> byte code offset #518
/*      */     //   Java source line #380	-> byte code offset #520
/*      */     //   Java source line #383	-> byte code offset #533
/*      */     //   Java source line #385	-> byte code offset #540
/*      */     //   Java source line #387	-> byte code offset #556
/*      */     //   Java source line #389	-> byte code offset #572
/*      */     //   Java source line #394	-> byte code offset #588
/*      */     //   Java source line #481	-> byte code offset #593
/*      */     //   Java source line #483	-> byte code offset #600
/*      */     //   Java source line #486	-> byte code offset #605
/*      */     //   Java source line #488	-> byte code offset #612
/*      */     //   Java source line #491	-> byte code offset #620
/*      */     //   Java source line #493	-> byte code offset #625
/*      */     //   Java source line #495	-> byte code offset #634
/*      */     //   Java source line #397	-> byte code offset #642
/*      */     //   Java source line #399	-> byte code offset #645
/*      */     //   Java source line #402	-> byte code offset #650
/*      */     //   Java source line #405	-> byte code offset #666
/*      */     //   Java source line #404	-> byte code offset #669
/*      */     //   Java source line #408	-> byte code offset #671
/*      */     //   Java source line #411	-> byte code offset #730
/*      */     //   Java source line #413	-> byte code offset #736
/*      */     //   Java source line #415	-> byte code offset #739
/*      */     //   Java source line #481	-> byte code offset #746
/*      */     //   Java source line #483	-> byte code offset #753
/*      */     //   Java source line #486	-> byte code offset #758
/*      */     //   Java source line #488	-> byte code offset #765
/*      */     //   Java source line #491	-> byte code offset #773
/*      */     //   Java source line #493	-> byte code offset #778
/*      */     //   Java source line #495	-> byte code offset #787
/*      */     //   Java source line #417	-> byte code offset #795
/*      */     //   Java source line #419	-> byte code offset #797
/*      */     //   Java source line #421	-> byte code offset #804
/*      */     //   Java source line #423	-> byte code offset #810
/*      */     //   Java source line #425	-> byte code offset #813
/*      */     //   Java source line #427	-> byte code offset #823
/*      */     //   Java source line #429	-> byte code offset #828
/*      */     //   Java source line #431	-> byte code offset #831
/*      */     //   Java source line #433	-> byte code offset #837
/*      */     //   Java source line #435	-> byte code offset #847
/*      */     //   Java source line #437	-> byte code offset #852
/*      */     //   Java source line #439	-> byte code offset #855
/*      */     //   Java source line #443	-> byte code offset #858
/*      */     //   Java source line #448	-> byte code offset #870
/*      */     //   Java source line #451	-> byte code offset #873
/*      */     //   Java source line #453	-> byte code offset #878
/*      */     //   Java source line #457	-> byte code offset #881
/*      */     //   Java source line #459	-> byte code offset #884
/*      */     //   Java source line #461	-> byte code offset #886
/*      */     //   Java source line #463	-> byte code offset #891
/*      */     //   Java source line #465	-> byte code offset #897
/*      */     //   Java source line #467	-> byte code offset #902
/*      */     //   Java source line #469	-> byte code offset #905
/*      */     //   Java source line #473	-> byte code offset #908
/*      */     //   Java source line #303	-> byte code offset #911
/*      */     //   Java source line #477	-> byte code offset #917
/*      */     //   Java source line #481	-> byte code offset #928
/*      */     //   Java source line #483	-> byte code offset #937
/*      */     //   Java source line #486	-> byte code offset #942
/*      */     //   Java source line #488	-> byte code offset #949
/*      */     //   Java source line #491	-> byte code offset #957
/*      */     //   Java source line #493	-> byte code offset #962
/*      */     //   Java source line #495	-> byte code offset #971
/*      */     //   Java source line #498	-> byte code offset #979
/*      */     //   Java source line #500	-> byte code offset #980
/*      */     //   Java source line #502	-> byte code offset #1024
/*      */     //   Java source line #504	-> byte code offset #1025
/*      */     //   Java source line #506	-> byte code offset #1078
/*      */     //   Java source line #508	-> byte code offset #1079
/*      */     //   Java source line #510	-> byte code offset #1120
/*      */     //   Java source line #514	-> byte code offset #1121
/*      */     //   Java source line #516	-> byte code offset #1128
/*      */     //   Java source line #520	-> byte code offset #1136
/*      */     //   Java source line #522	-> byte code offset #1140
/*      */     //   Java source line #525	-> byte code offset #1152
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	1154	0	this	ResourceDownloaderURLImpl
/*      */     //   10	60	1	protocol	String
/*      */     //   1120	28	1	e	Throwable
/*      */     //   144	794	2	url	URL
/*      */     //   979	41	2	e	java.net.MalformedURLException
/*      */     //   1024	50	2	e	java.net.UnknownHostException
/*      */     //   1078	38	2	e	IOException
/*      */     //   1132	2	2	rde	ResourceDownloaderException
/*      */     //   1151	2	2	rde	ResourceDownloaderException
/*      */     //   151	277	3	initial_url	URL
/*      */     //   175	28	4	plugin_proxy	AEProxyFactory.PluginProxy
/*      */     //   216	747	4	plugin_proxy	AEProxyFactory.PluginProxy
/*      */     //   153	812	5	ok	boolean
/*      */     //   246	584	6	dh_hack	boolean
/*      */     //   249	605	7	internal_error_hack	boolean
/*      */     //   252	660	8	connect_loop	int
/*      */     //   342	3	9	con	java.net.HttpURLConnection
/*      */     //   359	381	9	con	java.net.HttpURLConnection
/*      */     //   795	87	9	e	javax.net.ssl.SSLException
/*      */     //   884	25	9	e	IOException
/*      */     //   284	57	10	ssl_con	javax.net.ssl.HttpsURLConnection
/*      */     //   459	17	10	cidg	org.gudy.azureus2.plugins.clientid.ClientIDGenerator
/*      */     //   802	36	10	msg	String
/*      */     //   895	8	10	retry_url	URL
/*      */     //   331	3	11	host	String
/*      */     //   473	13	11	props	java.util.Properties
/*      */     //   518	7	11	e	com.aelitis.azureus.core.proxy.AEProxyFactory.UnknownHostException
/*      */     //   538	165	11	response	int
/*      */     //   811	63	11	try_again	boolean
/*      */     //   492	148	12	ua	String
/*      */     //   643	150	12	dest	URL
/*      */     //   669	3	13	e	Throwable
/*      */     //   928	49	14	localObject	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   510	515	518	com/aelitis/azureus/core/proxy/AEProxyFactory$UnknownHostException
/*      */     //   650	666	669	java/lang/Throwable
/*      */     //   260	593	795	javax/net/ssl/SSLException
/*      */     //   642	746	795	javax/net/ssl/SSLException
/*      */     //   260	593	884	java/io/IOException
/*      */     //   642	746	884	java/io/IOException
/*      */     //   218	593	928	finally
/*      */     //   642	746	928	finally
/*      */     //   795	930	928	finally
/*      */     //   123	639	979	java/net/MalformedURLException
/*      */     //   642	792	979	java/net/MalformedURLException
/*      */     //   795	979	979	java/net/MalformedURLException
/*      */     //   123	639	1024	java/net/UnknownHostException
/*      */     //   642	792	1024	java/net/UnknownHostException
/*      */     //   795	979	1024	java/net/UnknownHostException
/*      */     //   123	639	1078	java/io/IOException
/*      */     //   642	792	1078	java/io/IOException
/*      */     //   795	979	1078	java/io/IOException
/*      */     //   0	68	1120	java/lang/Throwable
/*      */     //   69	95	1120	java/lang/Throwable
/*      */     //   96	639	1120	java/lang/Throwable
/*      */     //   642	792	1120	java/lang/Throwable
/*      */     //   795	1120	1120	java/lang/Throwable
/*      */   }
/*      */   
/*      */   public ResourceDownloaderBaseImpl getClone(ResourceDownloaderBaseImpl parent)
/*      */   {
/*  533 */     ResourceDownloaderURLImpl c = new ResourceDownloaderURLImpl(parent, this.original_url, this.post_data, this.auth_supplied, this.user_name, this.password);
/*      */     
/*  535 */     c.setSize(this.size);
/*      */     
/*  537 */     c.setProperties(this);
/*  538 */     c.setForceNoProxy(this.force_no_proxy);
/*  539 */     if (this.force_proxy != null) {
/*  540 */       c.setForceProxy(this.force_proxy);
/*      */     }
/*  542 */     if (this.auto_plugin_proxy) {
/*  543 */       c.setAutoPluginProxy();
/*      */     }
/*  545 */     return c;
/*      */   }
/*      */   
/*      */ 
/*      */   public void asyncDownload()
/*      */   {
/*  551 */     final Object parent_tls = TorrentUtils.getTLS();
/*      */     
/*  553 */     AEThread2 t = new AEThread2("ResourceDownloader:asyncDownload - " + trimForDisplay(this.original_url), true)
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/*  559 */         Object child_tls = TorrentUtils.getTLS();
/*      */         
/*  561 */         TorrentUtils.setTLS(parent_tls);
/*      */         try
/*      */         {
/*  564 */           ResourceDownloaderURLImpl.this.download();
/*      */ 
/*      */         }
/*      */         catch (ResourceDownloaderException e) {}finally
/*      */         {
/*      */ 
/*  570 */           TorrentUtils.setTLS(child_tls);
/*      */         }
/*      */         
/*      */       }
/*      */       
/*  575 */     };
/*  576 */     t.start();
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public InputStream download()
/*      */     throws ResourceDownloaderException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: aload_0
/*      */     //   2: new 513	java/lang/StringBuilder
/*      */     //   5: dup
/*      */     //   6: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   9: aload_0
/*      */     //   10: invokevirtual 1001	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:getLogIndent	()Ljava/lang/String;
/*      */     //   13: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   16: ldc 20
/*      */     //   18: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   21: aload_0
/*      */     //   22: aload_0
/*      */     //   23: getfield 869	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:original_url	Ljava/net/URL;
/*      */     //   26: invokevirtual 1014	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:trimForDisplay	(Ljava/net/URL;)Ljava/lang/String;
/*      */     //   29: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   32: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   35: invokevirtual 1015	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:reportActivity	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;)V
/*      */     //   38: aload_0
/*      */     //   39: getfield 870	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   42: invokevirtual 969	org/gudy/azureus2/core3/util/AEMonitor:enter	()V
/*      */     //   45: aload_0
/*      */     //   46: getfield 862	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:download_initiated	Z
/*      */     //   49: ifeq +14 -> 63
/*      */     //   52: new 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   55: dup
/*      */     //   56: aload_0
/*      */     //   57: ldc 19
/*      */     //   59: invokespecial 986	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException:<init>	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;)V
/*      */     //   62: athrow
/*      */     //   63: aload_0
/*      */     //   64: iconst_1
/*      */     //   65: putfield 862	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:download_initiated	Z
/*      */     //   68: aload_0
/*      */     //   69: getfield 870	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   72: invokevirtual 970	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   75: goto +13 -> 88
/*      */     //   78: astore_1
/*      */     //   79: aload_0
/*      */     //   80: getfield 870	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   83: invokevirtual 970	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   86: aload_1
/*      */     //   87: athrow
/*      */     //   88: new 519	java/net/URL
/*      */     //   91: dup
/*      */     //   92: aload_0
/*      */     //   93: getfield 869	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:original_url	Ljava/net/URL;
/*      */     //   96: invokevirtual 934	java/net/URL:toString	()Ljava/lang/String;
/*      */     //   99: ldc 4
/*      */     //   101: ldc 5
/*      */     //   103: invokevirtual 913	java/lang/String:replaceAll	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
/*      */     //   106: invokespecial 935	java/net/URL:<init>	(Ljava/lang/String;)V
/*      */     //   109: astore_1
/*      */     //   110: aload_1
/*      */     //   111: invokevirtual 932	java/net/URL:getProtocol	()Ljava/lang/String;
/*      */     //   114: invokevirtual 904	java/lang/String:toLowerCase	()Ljava/lang/String;
/*      */     //   117: astore_2
/*      */     //   118: aload_2
/*      */     //   119: ldc 64
/*      */     //   121: invokevirtual 903	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   124: ifeq +11 -> 135
/*      */     //   127: aload_0
/*      */     //   128: getfield 869	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:original_url	Ljava/net/URL;
/*      */     //   131: astore_1
/*      */     //   132: goto +248 -> 380
/*      */     //   135: aload_2
/*      */     //   136: ldc 51
/*      */     //   138: invokevirtual 903	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   141: ifeq +52 -> 193
/*      */     //   144: new 502	java/io/File
/*      */     //   147: dup
/*      */     //   148: aload_0
/*      */     //   149: getfield 869	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:original_url	Ljava/net/URL;
/*      */     //   152: invokevirtual 936	java/net/URL:toURI	()Ljava/net/URI;
/*      */     //   155: invokespecial 883	java/io/File:<init>	(Ljava/net/URI;)V
/*      */     //   158: astore_3
/*      */     //   159: new 503	java/io/FileInputStream
/*      */     //   162: dup
/*      */     //   163: aload_3
/*      */     //   164: invokespecial 884	java/io/FileInputStream:<init>	(Ljava/io/File;)V
/*      */     //   167: astore 4
/*      */     //   169: aload_0
/*      */     //   170: aload_3
/*      */     //   171: invokevirtual 881	java/io/File:length	()J
/*      */     //   174: invokevirtual 997	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:informAmountComplete	(J)V
/*      */     //   177: aload_0
/*      */     //   178: bipush 100
/*      */     //   180: invokevirtual 996	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:informPercentDone	(I)V
/*      */     //   183: aload_0
/*      */     //   184: aload 4
/*      */     //   186: invokevirtual 1000	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:informComplete	(Ljava/io/InputStream;)Z
/*      */     //   189: pop
/*      */     //   190: aload 4
/*      */     //   192: areturn
/*      */     //   193: aload_1
/*      */     //   194: invokevirtual 930	java/net/URL:getPort	()I
/*      */     //   197: iconst_m1
/*      */     //   198: if_icmpne +182 -> 380
/*      */     //   201: aload_2
/*      */     //   202: ldc 54
/*      */     //   204: invokevirtual 903	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   207: ifne +12 -> 219
/*      */     //   210: aload_2
/*      */     //   211: ldc 55
/*      */     //   213: invokevirtual 903	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   216: ifeq +164 -> 380
/*      */     //   219: aload_2
/*      */     //   220: ldc 54
/*      */     //   222: invokevirtual 903	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   225: ifeq +9 -> 234
/*      */     //   228: bipush 80
/*      */     //   230: istore_3
/*      */     //   231: goto +7 -> 238
/*      */     //   234: sipush 443
/*      */     //   237: istore_3
/*      */     //   238: aload_0
/*      */     //   239: getfield 869	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:original_url	Ljava/net/URL;
/*      */     //   242: invokevirtual 934	java/net/URL:toString	()Ljava/lang/String;
/*      */     //   245: ldc 4
/*      */     //   247: ldc 5
/*      */     //   249: invokevirtual 913	java/lang/String:replaceAll	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
/*      */     //   252: astore 4
/*      */     //   254: aload 4
/*      */     //   256: ldc 13
/*      */     //   258: invokevirtual 907	java/lang/String:indexOf	(Ljava/lang/String;)I
/*      */     //   261: istore 5
/*      */     //   263: aload 4
/*      */     //   265: ldc 10
/*      */     //   267: iload 5
/*      */     //   269: iconst_4
/*      */     //   270: iadd
/*      */     //   271: invokevirtual 910	java/lang/String:indexOf	(Ljava/lang/String;I)I
/*      */     //   274: istore 5
/*      */     //   276: iload 5
/*      */     //   278: iconst_m1
/*      */     //   279: if_icmpne +43 -> 322
/*      */     //   282: new 519	java/net/URL
/*      */     //   285: dup
/*      */     //   286: new 513	java/lang/StringBuilder
/*      */     //   289: dup
/*      */     //   290: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   293: aload 4
/*      */     //   295: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   298: ldc 11
/*      */     //   300: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   303: iload_3
/*      */     //   304: invokevirtual 917	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   307: ldc 10
/*      */     //   309: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   312: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   315: invokespecial 935	java/net/URL:<init>	(Ljava/lang/String;)V
/*      */     //   318: astore_1
/*      */     //   319: goto +51 -> 370
/*      */     //   322: new 519	java/net/URL
/*      */     //   325: dup
/*      */     //   326: new 513	java/lang/StringBuilder
/*      */     //   329: dup
/*      */     //   330: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   333: aload 4
/*      */     //   335: iconst_0
/*      */     //   336: iload 5
/*      */     //   338: invokevirtual 906	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   341: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   344: ldc 11
/*      */     //   346: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   349: iload_3
/*      */     //   350: invokevirtual 917	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   353: aload 4
/*      */     //   355: iload 5
/*      */     //   357: invokevirtual 905	java/lang/String:substring	(I)Ljava/lang/String;
/*      */     //   360: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   363: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   366: invokespecial 935	java/net/URL:<init>	(Ljava/lang/String;)V
/*      */     //   369: astore_1
/*      */     //   370: goto +10 -> 380
/*      */     //   373: astore 4
/*      */     //   375: aload 4
/*      */     //   377: invokestatic 976	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*      */     //   380: aload_1
/*      */     //   381: invokestatic 973	org/gudy/azureus2/core3/util/AddressUtils:adjustURL	(Ljava/net/URL;)Ljava/net/URL;
/*      */     //   384: astore_1
/*      */     //   385: aload_0
/*      */     //   386: getfield 863	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:force_no_proxy	Z
/*      */     //   389: ifeq +11 -> 400
/*      */     //   392: invokestatic 874	com/aelitis/azureus/core/proxy/AEProxySelectorFactory:getSelector	()Lcom/aelitis/azureus/core/proxy/AEProxySelector;
/*      */     //   395: invokeinterface 1031 1 0
/*      */     //   400: aload_0
/*      */     //   401: getfield 859	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:auth_supplied	Z
/*      */     //   404: ifeq +8 -> 412
/*      */     //   407: aload_1
/*      */     //   408: aload_0
/*      */     //   409: invokestatic 968	org/gudy/azureus2/core3/security/SESecurityManager:setPasswordHandler	(Ljava/net/URL;Lorg/gudy/azureus2/core3/security/SEPasswordListener;)V
/*      */     //   412: iconst_1
/*      */     //   413: istore_3
/*      */     //   414: iconst_1
/*      */     //   415: istore 4
/*      */     //   417: iconst_0
/*      */     //   418: istore 5
/*      */     //   420: iconst_0
/*      */     //   421: istore 6
/*      */     //   423: new 523	java/util/HashSet
/*      */     //   426: dup
/*      */     //   427: invokespecial 954	java/util/HashSet:<init>	()V
/*      */     //   430: astore 7
/*      */     //   432: aload_1
/*      */     //   433: astore 8
/*      */     //   435: aload_0
/*      */     //   436: getfield 868	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:force_proxy	Ljava/net/Proxy;
/*      */     //   439: astore 9
/*      */     //   441: aconst_null
/*      */     //   442: astore 10
/*      */     //   444: aload 8
/*      */     //   446: astore 11
/*      */     //   448: iload 4
/*      */     //   450: ifeq +2525 -> 2975
/*      */     //   453: iconst_0
/*      */     //   454: istore 4
/*      */     //   456: iconst_0
/*      */     //   457: istore 13
/*      */     //   459: aload_0
/*      */     //   460: getfield 860	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:auto_plugin_proxy	Z
/*      */     //   463: ifne +10 -> 473
/*      */     //   466: aload_0
/*      */     //   467: invokevirtual 995	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:isAnonymous	()Z
/*      */     //   470: ifeq +49 -> 519
/*      */     //   473: ldc 50
/*      */     //   475: aload 8
/*      */     //   477: invokestatic 872	com/aelitis/azureus/core/proxy/AEProxyFactory:getPluginProxy	(Ljava/lang/String;Ljava/net/URL;)Lcom/aelitis/azureus/core/proxy/AEProxyFactory$PluginProxy;
/*      */     //   480: astore 12
/*      */     //   482: aload 12
/*      */     //   484: ifnonnull +14 -> 498
/*      */     //   487: new 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   490: dup
/*      */     //   491: aload_0
/*      */     //   492: ldc 29
/*      */     //   494: invokespecial 986	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException:<init>	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;)V
/*      */     //   497: athrow
/*      */     //   498: aload 12
/*      */     //   500: invokeinterface 1028 1 0
/*      */     //   505: astore 8
/*      */     //   507: aload 12
/*      */     //   509: invokeinterface 1027 1 0
/*      */     //   514: astore 9
/*      */     //   516: goto +6 -> 522
/*      */     //   519: aconst_null
/*      */     //   520: astore 12
/*      */     //   522: iconst_0
/*      */     //   523: istore 14
/*      */     //   525: iload 14
/*      */     //   527: iconst_3
/*      */     //   528: if_icmpge +2408 -> 2936
/*      */     //   531: aconst_null
/*      */     //   532: astore 15
/*      */     //   534: aload 12
/*      */     //   536: ifnonnull +13 -> 549
/*      */     //   539: aload_0
/*      */     //   540: getfield 868	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:force_proxy	Ljava/net/Proxy;
/*      */     //   543: invokestatic 871	com/aelitis/azureus/core/proxy/AEProxyFactory:getPluginProxy	(Ljava/net/Proxy;)Lcom/aelitis/azureus/core/proxy/AEProxyFactory$PluginProxy;
/*      */     //   546: goto +5 -> 551
/*      */     //   549: aload 12
/*      */     //   551: astore 10
/*      */     //   553: aload 8
/*      */     //   555: invokevirtual 932	java/net/URL:getProtocol	()Ljava/lang/String;
/*      */     //   558: ldc 55
/*      */     //   560: invokevirtual 908	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
/*      */     //   563: ifeq +204 -> 767
/*      */     //   566: aload_0
/*      */     //   567: aload 9
/*      */     //   569: aload 8
/*      */     //   571: invokespecial 1017	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:openConnection	(Ljava/net/Proxy;Ljava/net/URL;)Ljava/net/URLConnection;
/*      */     //   574: checkcast 533	javax/net/ssl/HttpsURLConnection
/*      */     //   577: astore 17
/*      */     //   579: iload 6
/*      */     //   581: ifne +16 -> 597
/*      */     //   584: aload 17
/*      */     //   586: new 559	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl$3
/*      */     //   589: dup
/*      */     //   590: aload_0
/*      */     //   591: invokespecial 1022	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl$3:<init>	(Lorg/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl;)V
/*      */     //   594: invokevirtual 959	javax/net/ssl/HttpsURLConnection:setHostnameVerifier	(Ljavax/net/ssl/HostnameVerifier;)V
/*      */     //   597: aload 10
/*      */     //   599: ifnull +125 -> 724
/*      */     //   602: invokestatic 965	org/gudy/azureus2/core3/security/SESecurityManager:getTrustManagerFactory	()Ljavax/net/ssl/TrustManagerFactory;
/*      */     //   605: astore 18
/*      */     //   607: new 522	java/util/ArrayList
/*      */     //   610: dup
/*      */     //   611: invokespecial 953	java/util/ArrayList:<init>	()V
/*      */     //   614: astore 19
/*      */     //   616: aload 18
/*      */     //   618: ifnull +59 -> 677
/*      */     //   621: aload 18
/*      */     //   623: invokevirtual 964	javax/net/ssl/TrustManagerFactory:getTrustManagers	()[Ljavax/net/ssl/TrustManager;
/*      */     //   626: astore 20
/*      */     //   628: aload 20
/*      */     //   630: arraylength
/*      */     //   631: istore 21
/*      */     //   633: iconst_0
/*      */     //   634: istore 22
/*      */     //   636: iload 22
/*      */     //   638: iload 21
/*      */     //   640: if_icmpge +37 -> 677
/*      */     //   643: aload 20
/*      */     //   645: iload 22
/*      */     //   647: aaload
/*      */     //   648: astore 23
/*      */     //   650: aload 23
/*      */     //   652: instanceof 537
/*      */     //   655: ifeq +16 -> 671
/*      */     //   658: aload 19
/*      */     //   660: aload 23
/*      */     //   662: checkcast 537	javax/net/ssl/X509TrustManager
/*      */     //   665: invokeinterface 1036 2 0
/*      */     //   670: pop
/*      */     //   671: iinc 22 1
/*      */     //   674: goto -38 -> 636
/*      */     //   677: new 560	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl$4
/*      */     //   680: dup
/*      */     //   681: aload_0
/*      */     //   682: aload 19
/*      */     //   684: invokespecial 1023	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl$4:<init>	(Lorg/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl;Ljava/util/List;)V
/*      */     //   687: invokestatic 967	org/gudy/azureus2/core3/security/SESecurityManager:getAllTrustingTrustManager	(Ljavax/net/ssl/X509TrustManager;)[Ljavax/net/ssl/TrustManager;
/*      */     //   690: astore 20
/*      */     //   692: ldc 32
/*      */     //   694: invokestatic 962	javax/net/ssl/SSLContext:getInstance	(Ljava/lang/String;)Ljavax/net/ssl/SSLContext;
/*      */     //   697: astore 21
/*      */     //   699: aload 21
/*      */     //   701: aconst_null
/*      */     //   702: aload 20
/*      */     //   704: getstatic 857	org/gudy/azureus2/core3/util/RandomUtils:SECURE_RANDOM	Ljava/security/SecureRandom;
/*      */     //   707: invokevirtual 963	javax/net/ssl/SSLContext:init	([Ljavax/net/ssl/KeyManager;[Ljavax/net/ssl/TrustManager;Ljava/security/SecureRandom;)V
/*      */     //   710: aload 21
/*      */     //   712: invokevirtual 961	javax/net/ssl/SSLContext:getSocketFactory	()Ljavax/net/ssl/SSLSocketFactory;
/*      */     //   715: astore 22
/*      */     //   717: aload 17
/*      */     //   719: aload 22
/*      */     //   721: invokevirtual 960	javax/net/ssl/HttpsURLConnection:setSSLSocketFactory	(Ljavax/net/ssl/SSLSocketFactory;)V
/*      */     //   724: iload 5
/*      */     //   726: ifeq +8 -> 734
/*      */     //   729: aload 17
/*      */     //   731: invokestatic 981	org/gudy/azureus2/core3/util/UrlUtils:DHHackIt	(Ljavax/net/ssl/HttpsURLConnection;)V
/*      */     //   734: iload 6
/*      */     //   736: ifeq +24 -> 760
/*      */     //   739: aload 10
/*      */     //   741: ifnull +19 -> 760
/*      */     //   744: aload 10
/*      */     //   746: invokeinterface 1026 1 0
/*      */     //   751: astore 18
/*      */     //   753: aload 18
/*      */     //   755: aload 17
/*      */     //   757: invokestatic 983	org/gudy/azureus2/core3/util/UrlUtils:HTTPSURLConnectionSNIHack	(Ljava/lang/String;Ljavax/net/ssl/HttpsURLConnection;)V
/*      */     //   760: aload 17
/*      */     //   762: astore 16
/*      */     //   764: goto +13 -> 777
/*      */     //   767: aload_0
/*      */     //   768: aload 9
/*      */     //   770: aload 8
/*      */     //   772: invokespecial 1017	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:openConnection	(Ljava/net/Proxy;Ljava/net/URL;)Ljava/net/URLConnection;
/*      */     //   775: astore 16
/*      */     //   777: aload 16
/*      */     //   779: instanceof 515
/*      */     //   782: ifeq +29 -> 811
/*      */     //   785: aload 10
/*      */     //   787: ifnull +15 -> 802
/*      */     //   790: aload 16
/*      */     //   792: checkcast 515	java/net/HttpURLConnection
/*      */     //   795: iconst_0
/*      */     //   796: invokevirtual 923	java/net/HttpURLConnection:setInstanceFollowRedirects	(Z)V
/*      */     //   799: goto +12 -> 811
/*      */     //   802: aload 16
/*      */     //   804: checkcast 515	java/net/HttpURLConnection
/*      */     //   807: iconst_1
/*      */     //   808: invokevirtual 923	java/net/HttpURLConnection:setInstanceFollowRedirects	(Z)V
/*      */     //   811: aload 10
/*      */     //   813: ifnull +70 -> 883
/*      */     //   816: aload 16
/*      */     //   818: ldc 26
/*      */     //   820: new 513	java/lang/StringBuilder
/*      */     //   823: dup
/*      */     //   824: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   827: aload 10
/*      */     //   829: invokeinterface 1026 1 0
/*      */     //   834: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   837: aload 11
/*      */     //   839: invokevirtual 930	java/net/URL:getPort	()I
/*      */     //   842: iconst_m1
/*      */     //   843: if_icmpne +8 -> 851
/*      */     //   846: ldc 3
/*      */     //   848: goto +26 -> 874
/*      */     //   851: new 513	java/lang/StringBuilder
/*      */     //   854: dup
/*      */     //   855: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   858: ldc 11
/*      */     //   860: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   863: aload 11
/*      */     //   865: invokevirtual 930	java/net/URL:getPort	()I
/*      */     //   868: invokevirtual 917	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   871: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   874: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   877: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   880: invokevirtual 950	java/net/URLConnection:setRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
/*      */     //   883: invokestatic 989	org/gudy/azureus2/pluginsimpl/local/clientid/ClientIDManagerImpl:getSingleton	()Lorg/gudy/azureus2/pluginsimpl/local/clientid/ClientIDManagerImpl;
/*      */     //   886: invokevirtual 988	org/gudy/azureus2/pluginsimpl/local/clientid/ClientIDManagerImpl:getGenerator	()Lorg/gudy/azureus2/plugins/clientid/ClientIDGenerator;
/*      */     //   889: astore 17
/*      */     //   891: aload 17
/*      */     //   893: ifnull +40 -> 933
/*      */     //   896: new 528	java/util/Properties
/*      */     //   899: dup
/*      */     //   900: invokespecial 955	java/util/Properties:<init>	()V
/*      */     //   903: astore 18
/*      */     //   905: aload 17
/*      */     //   907: aconst_null
/*      */     //   908: aload 18
/*      */     //   910: invokeinterface 1046 3 0
/*      */     //   915: aload 18
/*      */     //   917: ldc 44
/*      */     //   919: invokevirtual 956	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   922: astore 19
/*      */     //   924: aload 16
/*      */     //   926: ldc 44
/*      */     //   928: aload 19
/*      */     //   930: invokevirtual 950	java/net/URLConnection:setRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
/*      */     //   933: aload_0
/*      */     //   934: ldc 36
/*      */     //   936: invokevirtual 1013	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:getStringProperty	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   939: astore 18
/*      */     //   941: aload 18
/*      */     //   943: ifnull +25 -> 968
/*      */     //   946: aload 18
/*      */     //   948: ldc 28
/*      */     //   950: invokevirtual 908	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
/*      */     //   953: ifeq +15 -> 968
/*      */     //   956: aload 16
/*      */     //   958: ldc 17
/*      */     //   960: ldc 28
/*      */     //   962: invokevirtual 950	java/net/URLConnection:setRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
/*      */     //   965: goto +27 -> 992
/*      */     //   968: aload 18
/*      */     //   970: ifnull +13 -> 983
/*      */     //   973: aload 18
/*      */     //   975: ldc 62
/*      */     //   977: invokevirtual 903	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   980: ifne +12 -> 992
/*      */     //   983: aload 16
/*      */     //   985: ldc 17
/*      */     //   987: ldc 46
/*      */     //   989: invokevirtual 950	java/net/URLConnection:setRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
/*      */     //   992: iload_3
/*      */     //   993: ifeq +12 -> 1005
/*      */     //   996: aload 16
/*      */     //   998: ldc 16
/*      */     //   1000: ldc 53
/*      */     //   1002: invokevirtual 949	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
/*      */     //   1005: aload_0
/*      */     //   1006: aload 16
/*      */     //   1008: iload_3
/*      */     //   1009: invokevirtual 1006	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:setRequestProperties	(Ljava/net/URLConnection;Z)V
/*      */     //   1012: aload_0
/*      */     //   1013: getfield 864	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:post_data	[B
/*      */     //   1016: ifnull +73 -> 1089
/*      */     //   1019: aload 16
/*      */     //   1021: instanceof 515
/*      */     //   1024: ifeq +65 -> 1089
/*      */     //   1027: aload 16
/*      */     //   1029: iconst_1
/*      */     //   1030: invokevirtual 942	java/net/URLConnection:setDoOutput	(Z)V
/*      */     //   1033: aload_0
/*      */     //   1034: ldc 40
/*      */     //   1036: invokevirtual 1013	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:getStringProperty	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   1039: astore 19
/*      */     //   1041: aload 19
/*      */     //   1043: ifnonnull +7 -> 1050
/*      */     //   1046: ldc 30
/*      */     //   1048: astore 19
/*      */     //   1050: aload 16
/*      */     //   1052: checkcast 515	java/net/HttpURLConnection
/*      */     //   1055: aload 19
/*      */     //   1057: invokevirtual 926	java/net/HttpURLConnection:setRequestMethod	(Ljava/lang/String;)V
/*      */     //   1060: aload_0
/*      */     //   1061: getfield 864	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:post_data	[B
/*      */     //   1064: arraylength
/*      */     //   1065: ifle +24 -> 1089
/*      */     //   1068: aload 16
/*      */     //   1070: invokevirtual 944	java/net/URLConnection:getOutputStream	()Ljava/io/OutputStream;
/*      */     //   1073: astore 20
/*      */     //   1075: aload 20
/*      */     //   1077: aload_0
/*      */     //   1078: getfield 864	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:post_data	[B
/*      */     //   1081: invokevirtual 894	java/io/OutputStream:write	([B)V
/*      */     //   1084: aload 20
/*      */     //   1086: invokevirtual 893	java/io/OutputStream:flush	()V
/*      */     //   1089: aload_0
/*      */     //   1090: ldc 35
/*      */     //   1092: invokevirtual 1002	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:getLongProperty	(Ljava/lang/String;)J
/*      */     //   1095: lstore 19
/*      */     //   1097: lload 19
/*      */     //   1099: lconst_0
/*      */     //   1100: lcmp
/*      */     //   1101: iflt +11 -> 1112
/*      */     //   1104: aload 16
/*      */     //   1106: lload 19
/*      */     //   1108: l2i
/*      */     //   1109: invokevirtual 940	java/net/URLConnection:setConnectTimeout	(I)V
/*      */     //   1112: aload_0
/*      */     //   1113: ldc 41
/*      */     //   1115: invokevirtual 1002	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:getLongProperty	(Ljava/lang/String;)J
/*      */     //   1118: lstore 21
/*      */     //   1120: lload 21
/*      */     //   1122: lconst_0
/*      */     //   1123: lcmp
/*      */     //   1124: iflt +11 -> 1135
/*      */     //   1127: aload 16
/*      */     //   1129: lload 21
/*      */     //   1131: l2i
/*      */     //   1132: invokevirtual 941	java/net/URLConnection:setReadTimeout	(I)V
/*      */     //   1135: aload_0
/*      */     //   1136: ldc 42
/*      */     //   1138: invokevirtual 1003	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:getBooleanProperty	(Ljava/lang/String;)Z
/*      */     //   1141: istore 23
/*      */     //   1143: aload 16
/*      */     //   1145: invokevirtual 939	java/net/URLConnection:connect	()V
/*      */     //   1148: goto +18 -> 1166
/*      */     //   1151: astore 24
/*      */     //   1153: new 521	java/net/UnknownHostException
/*      */     //   1156: dup
/*      */     //   1157: aload 24
/*      */     //   1159: invokevirtual 873	com/aelitis/azureus/core/proxy/AEProxyFactory$UnknownHostException:getMessage	()Ljava/lang/String;
/*      */     //   1162: invokespecial 952	java/net/UnknownHostException:<init>	(Ljava/lang/String;)V
/*      */     //   1165: athrow
/*      */     //   1166: aload 16
/*      */     //   1168: instanceof 515
/*      */     //   1171: ifeq +14 -> 1185
/*      */     //   1174: aload 16
/*      */     //   1176: checkcast 515	java/net/HttpURLConnection
/*      */     //   1179: invokevirtual 921	java/net/HttpURLConnection:getResponseCode	()I
/*      */     //   1182: goto +6 -> 1188
/*      */     //   1185: sipush 200
/*      */     //   1188: istore 24
/*      */     //   1190: iconst_1
/*      */     //   1191: istore 13
/*      */     //   1193: iload 24
/*      */     //   1195: sipush 302
/*      */     //   1198: if_icmpeq +11 -> 1209
/*      */     //   1201: iload 24
/*      */     //   1203: sipush 301
/*      */     //   1206: if_icmpne +490 -> 1696
/*      */     //   1209: aload 16
/*      */     //   1211: ldc 57
/*      */     //   1213: invokevirtual 948	java/net/URLConnection:getHeaderField	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   1216: astore 25
/*      */     //   1218: aload 25
/*      */     //   1220: ifnull +476 -> 1696
/*      */     //   1223: aload 7
/*      */     //   1225: aload 25
/*      */     //   1227: invokeinterface 1044 2 0
/*      */     //   1232: ifne +15 -> 1247
/*      */     //   1235: aload 7
/*      */     //   1237: invokeinterface 1042 1 0
/*      */     //   1242: bipush 32
/*      */     //   1244: if_icmple +14 -> 1258
/*      */     //   1247: new 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   1250: dup
/*      */     //   1251: aload_0
/*      */     //   1252: ldc 61
/*      */     //   1254: invokespecial 986	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException:<init>	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;)V
/*      */     //   1257: athrow
/*      */     //   1258: aload 7
/*      */     //   1260: aload 25
/*      */     //   1262: invokeinterface 1043 2 0
/*      */     //   1267: pop
/*      */     //   1268: new 519	java/net/URL
/*      */     //   1271: dup
/*      */     //   1272: aload 25
/*      */     //   1274: invokespecial 935	java/net/URL:<init>	(Ljava/lang/String;)V
/*      */     //   1277: astore 26
/*      */     //   1279: iconst_0
/*      */     //   1280: istore 27
/*      */     //   1282: aload 10
/*      */     //   1284: ifnull +110 -> 1394
/*      */     //   1287: aload 10
/*      */     //   1289: ldc 60
/*      */     //   1291: aload 26
/*      */     //   1293: invokeinterface 1029 3 0
/*      */     //   1298: astore 28
/*      */     //   1300: aload 28
/*      */     //   1302: ifnull +92 -> 1394
/*      */     //   1305: aload 26
/*      */     //   1307: astore 11
/*      */     //   1309: aload_0
/*      */     //   1310: ldc 38
/*      */     //   1312: new 513	java/lang/StringBuilder
/*      */     //   1315: dup
/*      */     //   1316: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   1319: aload 11
/*      */     //   1321: invokevirtual 931	java/net/URL:getHost	()Ljava/lang/String;
/*      */     //   1324: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1327: aload 11
/*      */     //   1329: invokevirtual 930	java/net/URL:getPort	()I
/*      */     //   1332: iconst_m1
/*      */     //   1333: if_icmpne +8 -> 1341
/*      */     //   1336: ldc 3
/*      */     //   1338: goto +26 -> 1364
/*      */     //   1341: new 513	java/lang/StringBuilder
/*      */     //   1344: dup
/*      */     //   1345: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   1348: ldc 11
/*      */     //   1350: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1353: aload 11
/*      */     //   1355: invokevirtual 930	java/net/URL:getPort	()I
/*      */     //   1358: invokevirtual 917	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   1361: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1364: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1367: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1370: invokevirtual 1011	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:setProperty	(Ljava/lang/String;Ljava/lang/Object;)V
/*      */     //   1373: aload 28
/*      */     //   1375: invokeinterface 1027 1 0
/*      */     //   1380: astore 9
/*      */     //   1382: aload 28
/*      */     //   1384: invokeinterface 1028 1 0
/*      */     //   1389: astore 26
/*      */     //   1391: iconst_1
/*      */     //   1392: istore 27
/*      */     //   1394: aload 8
/*      */     //   1396: invokevirtual 932	java/net/URL:getProtocol	()Ljava/lang/String;
/*      */     //   1399: invokevirtual 904	java/lang/String:toLowerCase	()Ljava/lang/String;
/*      */     //   1402: astore 28
/*      */     //   1404: aload 26
/*      */     //   1406: invokevirtual 932	java/net/URL:getProtocol	()Ljava/lang/String;
/*      */     //   1409: invokevirtual 904	java/lang/String:toLowerCase	()Ljava/lang/String;
/*      */     //   1412: astore 29
/*      */     //   1414: iload 27
/*      */     //   1416: ifne +13 -> 1429
/*      */     //   1419: aload 28
/*      */     //   1421: aload 29
/*      */     //   1423: invokevirtual 903	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   1426: ifne +265 -> 1691
/*      */     //   1429: aload 26
/*      */     //   1431: astore 8
/*      */     //   1433: aload 16
/*      */     //   1435: invokevirtual 947	java/net/URLConnection:getHeaderFields	()Ljava/util/Map;
/*      */     //   1438: ldc 33
/*      */     //   1440: invokeinterface 1039 2 0
/*      */     //   1445: checkcast 525	java/util/List
/*      */     //   1448: astore 30
/*      */     //   1450: new 522	java/util/ArrayList
/*      */     //   1453: dup
/*      */     //   1454: invokespecial 953	java/util/ArrayList:<init>	()V
/*      */     //   1457: astore 31
/*      */     //   1459: aload 30
/*      */     //   1461: ifnull +61 -> 1522
/*      */     //   1464: iconst_0
/*      */     //   1465: istore 32
/*      */     //   1467: iload 32
/*      */     //   1469: aload 30
/*      */     //   1471: invokeinterface 1034 1 0
/*      */     //   1476: if_icmpge +46 -> 1522
/*      */     //   1479: aload 30
/*      */     //   1481: iload 32
/*      */     //   1483: invokeinterface 1035 2 0
/*      */     //   1488: checkcast 512	java/lang/String
/*      */     //   1491: ldc 14
/*      */     //   1493: invokevirtual 911	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
/*      */     //   1496: astore 33
/*      */     //   1498: aload 33
/*      */     //   1500: arraylength
/*      */     //   1501: ifle +15 -> 1516
/*      */     //   1504: aload 31
/*      */     //   1506: aload 33
/*      */     //   1508: iconst_0
/*      */     //   1509: aaload
/*      */     //   1510: invokeinterface 1036 2 0
/*      */     //   1515: pop
/*      */     //   1516: iinc 32 1
/*      */     //   1519: goto -52 -> 1467
/*      */     //   1522: aload 31
/*      */     //   1524: invokeinterface 1034 1 0
/*      */     //   1529: ifle +121 -> 1650
/*      */     //   1532: ldc 3
/*      */     //   1534: astore 32
/*      */     //   1536: aload_0
/*      */     //   1537: invokevirtual 1007	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:getLCKeyProperties	()Ljava/util/Map;
/*      */     //   1540: astore 33
/*      */     //   1542: aload 33
/*      */     //   1544: ldc 63
/*      */     //   1546: invokeinterface 1039 2 0
/*      */     //   1551: astore 34
/*      */     //   1553: aload 34
/*      */     //   1555: instanceof 512
/*      */     //   1558: ifeq +10 -> 1568
/*      */     //   1561: aload 34
/*      */     //   1563: checkcast 512	java/lang/String
/*      */     //   1566: astore 32
/*      */     //   1568: aload 31
/*      */     //   1570: invokeinterface 1037 1 0
/*      */     //   1575: astore 35
/*      */     //   1577: aload 35
/*      */     //   1579: invokeinterface 1032 1 0
/*      */     //   1584: ifeq +58 -> 1642
/*      */     //   1587: aload 35
/*      */     //   1589: invokeinterface 1033 1 0
/*      */     //   1594: checkcast 512	java/lang/String
/*      */     //   1597: astore 36
/*      */     //   1599: new 513	java/lang/StringBuilder
/*      */     //   1602: dup
/*      */     //   1603: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   1606: aload 32
/*      */     //   1608: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1611: aload 32
/*      */     //   1613: invokevirtual 899	java/lang/String:length	()I
/*      */     //   1616: ifne +8 -> 1624
/*      */     //   1619: ldc 3
/*      */     //   1621: goto +5 -> 1626
/*      */     //   1624: ldc 15
/*      */     //   1626: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1629: aload 36
/*      */     //   1631: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1634: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1637: astore 32
/*      */     //   1639: goto -62 -> 1577
/*      */     //   1642: aload_0
/*      */     //   1643: ldc 37
/*      */     //   1645: aload 32
/*      */     //   1647: invokevirtual 1011	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:setProperty	(Ljava/lang/String;Ljava/lang/Object;)V
/*      */     //   1650: goto +10 -> 1660
/*      */     //   1653: astore 30
/*      */     //   1655: aload 30
/*      */     //   1657: invokestatic 975	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/Throwable;)V
/*      */     //   1660: iconst_1
/*      */     //   1661: istore 4
/*      */     //   1663: aload 15
/*      */     //   1665: ifnull +9 -> 1674
/*      */     //   1668: aload 15
/*      */     //   1670: invokevirtual 882	java/io/File:delete	()Z
/*      */     //   1673: pop
/*      */     //   1674: aload 12
/*      */     //   1676: ifnull -1228 -> 448
/*      */     //   1679: aload 12
/*      */     //   1681: iload 13
/*      */     //   1683: invokeinterface 1024 2 0
/*      */     //   1688: goto -1240 -> 448
/*      */     //   1691: goto +5 -> 1696
/*      */     //   1694: astore 26
/*      */     //   1696: aload_0
/*      */     //   1697: ldc 39
/*      */     //   1699: new 510	java/lang/Long
/*      */     //   1702: dup
/*      */     //   1703: iload 24
/*      */     //   1705: i2l
/*      */     //   1706: invokespecial 897	java/lang/Long:<init>	(J)V
/*      */     //   1709: invokevirtual 1011	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:setProperty	(Ljava/lang/String;Ljava/lang/Object;)V
/*      */     //   1712: iload 24
/*      */     //   1714: sipush 201
/*      */     //   1717: if_icmpeq +246 -> 1963
/*      */     //   1720: iload 24
/*      */     //   1722: sipush 202
/*      */     //   1725: if_icmpeq +238 -> 1963
/*      */     //   1728: iload 24
/*      */     //   1730: sipush 204
/*      */     //   1733: if_icmpeq +230 -> 1963
/*      */     //   1736: iload 24
/*      */     //   1738: sipush 200
/*      */     //   1741: if_icmpeq +222 -> 1963
/*      */     //   1744: aload 16
/*      */     //   1746: checkcast 515	java/net/HttpURLConnection
/*      */     //   1749: astore 25
/*      */     //   1751: aload 25
/*      */     //   1753: invokevirtual 924	java/net/HttpURLConnection:getErrorStream	()Ljava/io/InputStream;
/*      */     //   1756: astore 26
/*      */     //   1758: aconst_null
/*      */     //   1759: astore 27
/*      */     //   1761: aload 26
/*      */     //   1763: ifnull +72 -> 1835
/*      */     //   1766: aload 16
/*      */     //   1768: ldc 47
/*      */     //   1770: invokevirtual 948	java/net/URLConnection:getHeaderField	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   1773: astore 28
/*      */     //   1775: aload 28
/*      */     //   1777: ifnull +48 -> 1825
/*      */     //   1780: aload 28
/*      */     //   1782: ldc 53
/*      */     //   1784: invokevirtual 908	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
/*      */     //   1787: ifeq +17 -> 1804
/*      */     //   1790: new 530	java/util/zip/GZIPInputStream
/*      */     //   1793: dup
/*      */     //   1794: aload 26
/*      */     //   1796: invokespecial 957	java/util/zip/GZIPInputStream:<init>	(Ljava/io/InputStream;)V
/*      */     //   1799: astore 26
/*      */     //   1801: goto +24 -> 1825
/*      */     //   1804: aload 28
/*      */     //   1806: ldc 48
/*      */     //   1808: invokevirtual 908	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
/*      */     //   1811: ifeq +14 -> 1825
/*      */     //   1814: new 531	java/util/zip/InflaterInputStream
/*      */     //   1817: dup
/*      */     //   1818: aload 26
/*      */     //   1820: invokespecial 958	java/util/zip/InflaterInputStream:<init>	(Ljava/io/InputStream;)V
/*      */     //   1823: astore 26
/*      */     //   1825: aload 26
/*      */     //   1827: sipush 512
/*      */     //   1830: invokestatic 978	org/gudy/azureus2/core3/util/FileUtil:readInputStreamAsString	(Ljava/io/InputStream;I)Ljava/lang/String;
/*      */     //   1833: astore 27
/*      */     //   1835: aload_0
/*      */     //   1836: aload 16
/*      */     //   1838: invokevirtual 1005	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:getRequestProperties	(Ljava/net/URLConnection;)V
/*      */     //   1841: aload 8
/*      */     //   1843: astore 28
/*      */     //   1845: aload 10
/*      */     //   1847: ifnull +24 -> 1871
/*      */     //   1850: new 519	java/net/URL
/*      */     //   1853: dup
/*      */     //   1854: aload 10
/*      */     //   1856: invokeinterface 1025 1 0
/*      */     //   1861: invokespecial 935	java/net/URL:<init>	(Ljava/lang/String;)V
/*      */     //   1864: astore 28
/*      */     //   1866: goto +5 -> 1871
/*      */     //   1869: astore 29
/*      */     //   1871: new 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   1874: dup
/*      */     //   1875: aload_0
/*      */     //   1876: new 513	java/lang/StringBuilder
/*      */     //   1879: dup
/*      */     //   1880: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   1883: ldc 21
/*      */     //   1885: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1888: aload_0
/*      */     //   1889: aload 28
/*      */     //   1891: invokevirtual 1014	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:trimForDisplay	(Ljava/net/URL;)Ljava/lang/String;
/*      */     //   1894: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1897: ldc 8
/*      */     //   1899: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1902: iload 24
/*      */     //   1904: invokestatic 896	java/lang/Integer:toString	(I)Ljava/lang/String;
/*      */     //   1907: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1910: ldc 4
/*      */     //   1912: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1915: aload 25
/*      */     //   1917: invokevirtual 925	java/net/HttpURLConnection:getResponseMessage	()Ljava/lang/String;
/*      */     //   1920: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1923: aload 27
/*      */     //   1925: ifnonnull +8 -> 1933
/*      */     //   1928: ldc 3
/*      */     //   1930: goto +23 -> 1953
/*      */     //   1933: new 513	java/lang/StringBuilder
/*      */     //   1936: dup
/*      */     //   1937: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   1940: ldc 12
/*      */     //   1942: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1945: aload 27
/*      */     //   1947: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1950: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1953: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1956: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1959: invokespecial 986	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException:<init>	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;)V
/*      */     //   1962: athrow
/*      */     //   1963: aload_0
/*      */     //   1964: aload 16
/*      */     //   1966: invokevirtual 1005	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:getRequestProperties	(Ljava/net/URLConnection;)V
/*      */     //   1969: iconst_0
/*      */     //   1970: istore 25
/*      */     //   1972: aload_0
/*      */     //   1973: getfield 870	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   1976: invokevirtual 969	org/gudy/azureus2/core3/util/AEMonitor:enter	()V
/*      */     //   1979: aload_0
/*      */     //   1980: aload 16
/*      */     //   1982: invokevirtual 943	java/net/URLConnection:getInputStream	()Ljava/io/InputStream;
/*      */     //   1985: putfield 865	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:input_stream	Ljava/io/InputStream;
/*      */     //   1988: aload 16
/*      */     //   1990: ldc 47
/*      */     //   1992: invokevirtual 948	java/net/URLConnection:getHeaderField	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   1995: astore 26
/*      */     //   1997: aload 26
/*      */     //   1999: ifnull +62 -> 2061
/*      */     //   2002: aload 26
/*      */     //   2004: ldc 53
/*      */     //   2006: invokevirtual 908	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
/*      */     //   2009: ifeq +24 -> 2033
/*      */     //   2012: iconst_1
/*      */     //   2013: istore 25
/*      */     //   2015: aload_0
/*      */     //   2016: new 530	java/util/zip/GZIPInputStream
/*      */     //   2019: dup
/*      */     //   2020: aload_0
/*      */     //   2021: getfield 865	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:input_stream	Ljava/io/InputStream;
/*      */     //   2024: invokespecial 957	java/util/zip/GZIPInputStream:<init>	(Ljava/io/InputStream;)V
/*      */     //   2027: putfield 865	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:input_stream	Ljava/io/InputStream;
/*      */     //   2030: goto +31 -> 2061
/*      */     //   2033: aload 26
/*      */     //   2035: ldc 48
/*      */     //   2037: invokevirtual 908	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
/*      */     //   2040: ifeq +21 -> 2061
/*      */     //   2043: iconst_1
/*      */     //   2044: istore 25
/*      */     //   2046: aload_0
/*      */     //   2047: new 531	java/util/zip/InflaterInputStream
/*      */     //   2050: dup
/*      */     //   2051: aload_0
/*      */     //   2052: getfield 865	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:input_stream	Ljava/io/InputStream;
/*      */     //   2055: invokespecial 958	java/util/zip/InflaterInputStream:<init>	(Ljava/io/InputStream;)V
/*      */     //   2058: putfield 865	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:input_stream	Ljava/io/InputStream;
/*      */     //   2061: aload_0
/*      */     //   2062: getfield 870	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   2065: invokevirtual 970	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   2068: goto +15 -> 2083
/*      */     //   2071: astore 37
/*      */     //   2073: aload_0
/*      */     //   2074: getfield 870	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   2077: invokevirtual 970	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   2080: aload 37
/*      */     //   2082: athrow
/*      */     //   2083: aload 16
/*      */     //   2085: instanceof 550
/*      */     //   2088: ifeq +44 -> 2132
/*      */     //   2091: aload 16
/*      */     //   2093: checkcast 550	org/gudy/azureus2/core3/util/protocol/magnet/MagnetConnection2
/*      */     //   2096: iconst_1
/*      */     //   2097: invokevirtual 984	org/gudy/azureus2/core3/util/protocol/magnet/MagnetConnection2:getResponseMessages	(Z)Ljava/util/List;
/*      */     //   2100: astore 26
/*      */     //   2102: aload 26
/*      */     //   2104: invokeinterface 1034 1 0
/*      */     //   2109: ifle +23 -> 2132
/*      */     //   2112: new 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   2115: dup
/*      */     //   2116: aload_0
/*      */     //   2117: aload 26
/*      */     //   2119: iconst_0
/*      */     //   2120: invokeinterface 1035 2 0
/*      */     //   2125: checkcast 512	java/lang/String
/*      */     //   2128: invokespecial 986	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException:<init>	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;)V
/*      */     //   2131: athrow
/*      */     //   2132: aconst_null
/*      */     //   2133: astore 26
/*      */     //   2135: aconst_null
/*      */     //   2136: astore 27
/*      */     //   2138: ldc 1
/*      */     //   2140: newarray <illegal type>
/*      */     //   2142: astore 28
/*      */     //   2144: lconst_0
/*      */     //   2145: lstore 29
/*      */     //   2147: iload 25
/*      */     //   2149: ifeq +9 -> 2158
/*      */     //   2152: ldc2_w 475
/*      */     //   2155: goto +8 -> 2163
/*      */     //   2158: aload 16
/*      */     //   2160: invokestatic 980	org/gudy/azureus2/core3/util/UrlUtils:getContentLength	(Ljava/net/URLConnection;)J
/*      */     //   2163: lstore 31
/*      */     //   2165: lload 31
/*      */     //   2167: lconst_0
/*      */     //   2168: lcmp
/*      */     //   2169: ifle +30 -> 2199
/*      */     //   2172: new 501	java/io/ByteArrayOutputStream
/*      */     //   2175: dup
/*      */     //   2176: lload 31
/*      */     //   2178: ldc2_w 479
/*      */     //   2181: lcmp
/*      */     //   2182: ifle +8 -> 2190
/*      */     //   2185: ldc 2
/*      */     //   2187: goto +6 -> 2193
/*      */     //   2190: lload 31
/*      */     //   2192: l2i
/*      */     //   2193: invokespecial 879	java/io/ByteArrayOutputStream:<init>	(I)V
/*      */     //   2196: goto +10 -> 2206
/*      */     //   2199: new 501	java/io/ByteArrayOutputStream
/*      */     //   2202: dup
/*      */     //   2203: invokespecial 877	java/io/ByteArrayOutputStream:<init>	()V
/*      */     //   2206: astore 26
/*      */     //   2208: aload_0
/*      */     //   2209: getfield 861	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:cancel_download	Z
/*      */     //   2212: ifne +146 -> 2358
/*      */     //   2215: lload 31
/*      */     //   2217: lconst_0
/*      */     //   2218: lcmp
/*      */     //   2219: iflt +19 -> 2238
/*      */     //   2222: lload 29
/*      */     //   2224: lload 31
/*      */     //   2226: lcmp
/*      */     //   2227: iflt +11 -> 2238
/*      */     //   2230: iload 23
/*      */     //   2232: ifeq +6 -> 2238
/*      */     //   2235: goto +123 -> 2358
/*      */     //   2238: aload_0
/*      */     //   2239: getfield 865	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:input_stream	Ljava/io/InputStream;
/*      */     //   2242: aload 28
/*      */     //   2244: invokevirtual 892	java/io/InputStream:read	([B)I
/*      */     //   2247: istore 33
/*      */     //   2249: iload 33
/*      */     //   2251: ifle +107 -> 2358
/*      */     //   2254: lload 29
/*      */     //   2256: ldc2_w 479
/*      */     //   2259: lcmp
/*      */     //   2260: ifle +50 -> 2310
/*      */     //   2263: aload 27
/*      */     //   2265: ifnonnull +32 -> 2297
/*      */     //   2268: invokestatic 971	org/gudy/azureus2/core3/util/AETemporaryFileHandler:createTempFile	()Ljava/io/File;
/*      */     //   2271: astore 15
/*      */     //   2273: new 504	java/io/FileOutputStream
/*      */     //   2276: dup
/*      */     //   2277: aload 15
/*      */     //   2279: invokespecial 888	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
/*      */     //   2282: astore 27
/*      */     //   2284: aload 27
/*      */     //   2286: aload 26
/*      */     //   2288: invokevirtual 878	java/io/ByteArrayOutputStream:toByteArray	()[B
/*      */     //   2291: invokevirtual 886	java/io/FileOutputStream:write	([B)V
/*      */     //   2294: aconst_null
/*      */     //   2295: astore 26
/*      */     //   2297: aload 27
/*      */     //   2299: aload 28
/*      */     //   2301: iconst_0
/*      */     //   2302: iload 33
/*      */     //   2304: invokevirtual 887	java/io/FileOutputStream:write	([BII)V
/*      */     //   2307: goto +13 -> 2320
/*      */     //   2310: aload 26
/*      */     //   2312: aload 28
/*      */     //   2314: iconst_0
/*      */     //   2315: iload 33
/*      */     //   2317: invokevirtual 880	java/io/ByteArrayOutputStream:write	([BII)V
/*      */     //   2320: lload 29
/*      */     //   2322: iload 33
/*      */     //   2324: i2l
/*      */     //   2325: ladd
/*      */     //   2326: lstore 29
/*      */     //   2328: aload_0
/*      */     //   2329: lload 29
/*      */     //   2331: invokevirtual 997	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:informAmountComplete	(J)V
/*      */     //   2334: lload 31
/*      */     //   2336: lconst_0
/*      */     //   2337: lcmp
/*      */     //   2338: ifle +17 -> 2355
/*      */     //   2341: aload_0
/*      */     //   2342: ldc2_w 477
/*      */     //   2345: lload 29
/*      */     //   2347: lmul
/*      */     //   2348: lload 31
/*      */     //   2350: ldiv
/*      */     //   2351: l2i
/*      */     //   2352: invokevirtual 996	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:informPercentDone	(I)V
/*      */     //   2355: goto -147 -> 2208
/*      */     //   2358: lload 31
/*      */     //   2360: lconst_0
/*      */     //   2361: lcmp
/*      */     //   2362: ifle +85 -> 2447
/*      */     //   2365: lload 29
/*      */     //   2367: lload 31
/*      */     //   2369: lcmp
/*      */     //   2370: ifeq +77 -> 2447
/*      */     //   2373: lload 29
/*      */     //   2375: lload 31
/*      */     //   2377: lcmp
/*      */     //   2378: ifle +58 -> 2436
/*      */     //   2381: new 513	java/lang/StringBuilder
/*      */     //   2384: dup
/*      */     //   2385: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   2388: ldc_w 485
/*      */     //   2391: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2394: aload_0
/*      */     //   2395: aload_0
/*      */     //   2396: getfield 869	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:original_url	Ljava/net/URL;
/*      */     //   2399: invokevirtual 1014	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:trimForDisplay	(Ljava/net/URL;)Ljava/lang/String;
/*      */     //   2402: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2405: ldc_w 481
/*      */     //   2408: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2411: lload 31
/*      */     //   2413: invokevirtual 918	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
/*      */     //   2416: ldc_w 482
/*      */     //   2419: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2422: lload 29
/*      */     //   2424: invokevirtual 918	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
/*      */     //   2427: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2430: invokestatic 974	org/gudy/azureus2/core3/util/Debug:outNoStack	(Ljava/lang/String;)V
/*      */     //   2433: goto +14 -> 2447
/*      */     //   2436: new 505	java/io/IOException
/*      */     //   2439: dup
/*      */     //   2440: ldc_w 486
/*      */     //   2443: invokespecial 890	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   2446: athrow
/*      */     //   2447: aload 27
/*      */     //   2449: ifnull +13 -> 2462
/*      */     //   2452: aload 27
/*      */     //   2454: invokevirtual 885	java/io/FileOutputStream:close	()V
/*      */     //   2457: goto +5 -> 2462
/*      */     //   2460: astore 28
/*      */     //   2462: aload_0
/*      */     //   2463: getfield 865	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:input_stream	Ljava/io/InputStream;
/*      */     //   2466: invokevirtual 891	java/io/InputStream:close	()V
/*      */     //   2469: goto +30 -> 2499
/*      */     //   2472: astore 38
/*      */     //   2474: aload 27
/*      */     //   2476: ifnull +13 -> 2489
/*      */     //   2479: aload 27
/*      */     //   2481: invokevirtual 885	java/io/FileOutputStream:close	()V
/*      */     //   2484: goto +5 -> 2489
/*      */     //   2487: astore 39
/*      */     //   2489: aload_0
/*      */     //   2490: getfield 865	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:input_stream	Ljava/io/InputStream;
/*      */     //   2493: invokevirtual 891	java/io/InputStream:close	()V
/*      */     //   2496: aload 38
/*      */     //   2498: athrow
/*      */     //   2499: aload 15
/*      */     //   2501: ifnull +20 -> 2521
/*      */     //   2504: new 499	com/aelitis/azureus/core/util/DeleteFileOnCloseInputStream
/*      */     //   2507: dup
/*      */     //   2508: aload 15
/*      */     //   2510: invokespecial 875	com/aelitis/azureus/core/util/DeleteFileOnCloseInputStream:<init>	(Ljava/io/File;)V
/*      */     //   2513: astore 28
/*      */     //   2515: aconst_null
/*      */     //   2516: astore 15
/*      */     //   2518: goto +17 -> 2535
/*      */     //   2521: new 500	java/io/ByteArrayInputStream
/*      */     //   2524: dup
/*      */     //   2525: aload 26
/*      */     //   2527: invokevirtual 878	java/io/ByteArrayOutputStream:toByteArray	()[B
/*      */     //   2530: invokespecial 876	java/io/ByteArrayInputStream:<init>	([B)V
/*      */     //   2533: astore 28
/*      */     //   2535: iconst_0
/*      */     //   2536: istore 29
/*      */     //   2538: aload_0
/*      */     //   2539: aload 28
/*      */     //   2541: invokevirtual 1000	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:informComplete	(Ljava/io/InputStream;)Z
/*      */     //   2544: ifeq +75 -> 2619
/*      */     //   2547: iconst_1
/*      */     //   2548: istore 29
/*      */     //   2550: aload 28
/*      */     //   2552: astore 30
/*      */     //   2554: iload 29
/*      */     //   2556: ifne +8 -> 2564
/*      */     //   2559: aload 28
/*      */     //   2561: invokevirtual 891	java/io/InputStream:close	()V
/*      */     //   2564: aload 15
/*      */     //   2566: ifnull +9 -> 2575
/*      */     //   2569: aload 15
/*      */     //   2571: invokevirtual 882	java/io/File:delete	()Z
/*      */     //   2574: pop
/*      */     //   2575: aload 12
/*      */     //   2577: ifnull +12 -> 2589
/*      */     //   2580: aload 12
/*      */     //   2582: iload 13
/*      */     //   2584: invokeinterface 1024 2 0
/*      */     //   2589: aload_0
/*      */     //   2590: getfield 859	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:auth_supplied	Z
/*      */     //   2593: ifeq +8 -> 2601
/*      */     //   2596: aload_1
/*      */     //   2597: aconst_null
/*      */     //   2598: invokestatic 968	org/gudy/azureus2/core3/security/SESecurityManager:setPasswordHandler	(Ljava/net/URL;Lorg/gudy/azureus2/core3/security/SEPasswordListener;)V
/*      */     //   2601: aload_0
/*      */     //   2602: getfield 863	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:force_no_proxy	Z
/*      */     //   2605: ifeq +11 -> 2616
/*      */     //   2608: invokestatic 874	com/aelitis/azureus/core/proxy/AEProxySelectorFactory:getSelector	()Lcom/aelitis/azureus/core/proxy/AEProxySelector;
/*      */     //   2611: invokeinterface 1030 1 0
/*      */     //   2616: aload 30
/*      */     //   2618: areturn
/*      */     //   2619: iload 29
/*      */     //   2621: ifne +26 -> 2647
/*      */     //   2624: aload 28
/*      */     //   2626: invokevirtual 891	java/io/InputStream:close	()V
/*      */     //   2629: goto +18 -> 2647
/*      */     //   2632: astore 40
/*      */     //   2634: iload 29
/*      */     //   2636: ifne +8 -> 2644
/*      */     //   2639: aload 28
/*      */     //   2641: invokevirtual 891	java/io/InputStream:close	()V
/*      */     //   2644: aload 40
/*      */     //   2646: athrow
/*      */     //   2647: new 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   2650: dup
/*      */     //   2651: aload_0
/*      */     //   2652: new 513	java/lang/StringBuilder
/*      */     //   2655: dup
/*      */     //   2656: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   2659: ldc_w 484
/*      */     //   2662: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2665: aload_0
/*      */     //   2666: aload_0
/*      */     //   2667: getfield 869	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:original_url	Ljava/net/URL;
/*      */     //   2670: invokevirtual 1014	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:trimForDisplay	(Ljava/net/URL;)Ljava/lang/String;
/*      */     //   2673: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2676: ldc 6
/*      */     //   2678: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2681: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2684: invokespecial 986	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException:<init>	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;)V
/*      */     //   2687: athrow
/*      */     //   2688: astore 16
/*      */     //   2690: aload 16
/*      */     //   2692: invokestatic 977	org/gudy/azureus2/core3/util/Debug:getNestedExceptionMessage	(Ljava/lang/Throwable;)Ljava/lang/String;
/*      */     //   2695: astore 17
/*      */     //   2697: iload 14
/*      */     //   2699: iconst_3
/*      */     //   2700: if_icmpge +86 -> 2786
/*      */     //   2703: iconst_0
/*      */     //   2704: istore 18
/*      */     //   2706: aload 17
/*      */     //   2708: ldc 18
/*      */     //   2710: invokevirtual 902	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
/*      */     //   2713: ifeq +17 -> 2730
/*      */     //   2716: iload 5
/*      */     //   2718: ifne +33 -> 2751
/*      */     //   2721: iconst_1
/*      */     //   2722: istore 5
/*      */     //   2724: iconst_1
/*      */     //   2725: istore 18
/*      */     //   2727: goto +24 -> 2751
/*      */     //   2730: aload 17
/*      */     //   2732: ldc 56
/*      */     //   2734: invokevirtual 902	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
/*      */     //   2737: ifeq +14 -> 2751
/*      */     //   2740: iload 6
/*      */     //   2742: ifne +9 -> 2751
/*      */     //   2745: iconst_1
/*      */     //   2746: istore 6
/*      */     //   2748: iconst_1
/*      */     //   2749: istore 18
/*      */     //   2751: aload 10
/*      */     //   2753: ifnonnull +14 -> 2767
/*      */     //   2756: aload 8
/*      */     //   2758: invokestatic 966	org/gudy/azureus2/core3/security/SESecurityManager:installServerCertificates	(Ljava/net/URL;)Ljavax/net/ssl/SSLSocketFactory;
/*      */     //   2761: ifnull +6 -> 2767
/*      */     //   2764: iconst_1
/*      */     //   2765: istore 18
/*      */     //   2767: iload 18
/*      */     //   2769: ifeq +17 -> 2786
/*      */     //   2772: aload 15
/*      */     //   2774: ifnull +156 -> 2930
/*      */     //   2777: aload 15
/*      */     //   2779: invokevirtual 882	java/io/File:delete	()Z
/*      */     //   2782: pop
/*      */     //   2783: goto +147 -> 2930
/*      */     //   2786: aload 16
/*      */     //   2788: athrow
/*      */     //   2789: astore 16
/*      */     //   2791: iload 14
/*      */     //   2793: ifne +19 -> 2812
/*      */     //   2796: iconst_0
/*      */     //   2797: istore_3
/*      */     //   2798: aload 15
/*      */     //   2800: ifnull +130 -> 2930
/*      */     //   2803: aload 15
/*      */     //   2805: invokevirtual 882	java/io/File:delete	()Z
/*      */     //   2808: pop
/*      */     //   2809: goto +121 -> 2930
/*      */     //   2812: aload 15
/*      */     //   2814: ifnull +116 -> 2930
/*      */     //   2817: aload 15
/*      */     //   2819: invokevirtual 882	java/io/File:delete	()Z
/*      */     //   2822: pop
/*      */     //   2823: goto +107 -> 2930
/*      */     //   2826: astore 16
/*      */     //   2828: iload 14
/*      */     //   2830: ifne +81 -> 2911
/*      */     //   2833: aload 16
/*      */     //   2835: invokevirtual 889	java/io/IOException:getMessage	()Ljava/lang/String;
/*      */     //   2838: astore 17
/*      */     //   2840: aload 17
/*      */     //   2842: ifnull +39 -> 2881
/*      */     //   2845: aload 17
/*      */     //   2847: getstatic 856	org/gudy/azureus2/core3/internat/MessageText:LOCALE_ENGLISH	Ljava/util/Locale;
/*      */     //   2850: invokevirtual 912	java/lang/String:toLowerCase	(Ljava/util/Locale;)Ljava/lang/String;
/*      */     //   2853: astore 17
/*      */     //   2855: aload 17
/*      */     //   2857: ldc 53
/*      */     //   2859: invokevirtual 902	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
/*      */     //   2862: ifeq +19 -> 2881
/*      */     //   2865: iconst_0
/*      */     //   2866: istore_3
/*      */     //   2867: aload 15
/*      */     //   2869: ifnull +61 -> 2930
/*      */     //   2872: aload 15
/*      */     //   2874: invokevirtual 882	java/io/File:delete	()Z
/*      */     //   2877: pop
/*      */     //   2878: goto +52 -> 2930
/*      */     //   2881: aload 8
/*      */     //   2883: invokestatic 982	org/gudy/azureus2/core3/util/UrlUtils:getIPV4Fallback	(Ljava/net/URL;)Ljava/net/URL;
/*      */     //   2886: astore 18
/*      */     //   2888: aload 18
/*      */     //   2890: ifnull +21 -> 2911
/*      */     //   2893: aload 18
/*      */     //   2895: astore 8
/*      */     //   2897: aload 15
/*      */     //   2899: ifnull +31 -> 2930
/*      */     //   2902: aload 15
/*      */     //   2904: invokevirtual 882	java/io/File:delete	()Z
/*      */     //   2907: pop
/*      */     //   2908: goto +22 -> 2930
/*      */     //   2911: aload 16
/*      */     //   2913: athrow
/*      */     //   2914: astore 41
/*      */     //   2916: aload 15
/*      */     //   2918: ifnull +9 -> 2927
/*      */     //   2921: aload 15
/*      */     //   2923: invokevirtual 882	java/io/File:delete	()Z
/*      */     //   2926: pop
/*      */     //   2927: aload 41
/*      */     //   2929: athrow
/*      */     //   2930: iinc 14 1
/*      */     //   2933: goto -2408 -> 525
/*      */     //   2936: aload 12
/*      */     //   2938: ifnull +34 -> 2972
/*      */     //   2941: aload 12
/*      */     //   2943: iload 13
/*      */     //   2945: invokeinterface 1024 2 0
/*      */     //   2950: goto +22 -> 2972
/*      */     //   2953: astore 42
/*      */     //   2955: aload 12
/*      */     //   2957: ifnull +12 -> 2969
/*      */     //   2960: aload 12
/*      */     //   2962: iload 13
/*      */     //   2964: invokeinterface 1024 2 0
/*      */     //   2969: aload 42
/*      */     //   2971: athrow
/*      */     //   2972: goto -2524 -> 448
/*      */     //   2975: new 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   2978: dup
/*      */     //   2979: aload_0
/*      */     //   2980: ldc 34
/*      */     //   2982: invokespecial 986	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException:<init>	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;)V
/*      */     //   2985: athrow
/*      */     //   2986: astore 43
/*      */     //   2988: aload_0
/*      */     //   2989: getfield 859	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:auth_supplied	Z
/*      */     //   2992: ifeq +8 -> 3000
/*      */     //   2995: aload_1
/*      */     //   2996: aconst_null
/*      */     //   2997: invokestatic 968	org/gudy/azureus2/core3/security/SESecurityManager:setPasswordHandler	(Ljava/net/URL;Lorg/gudy/azureus2/core3/security/SEPasswordListener;)V
/*      */     //   3000: aload_0
/*      */     //   3001: getfield 863	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:force_no_proxy	Z
/*      */     //   3004: ifeq +11 -> 3015
/*      */     //   3007: invokestatic 874	com/aelitis/azureus/core/proxy/AEProxySelectorFactory:getSelector	()Lcom/aelitis/azureus/core/proxy/AEProxySelector;
/*      */     //   3010: invokeinterface 1030 1 0
/*      */     //   3015: aload 43
/*      */     //   3017: athrow
/*      */     //   3018: astore_1
/*      */     //   3019: new 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   3022: dup
/*      */     //   3023: aload_0
/*      */     //   3024: new 513	java/lang/StringBuilder
/*      */     //   3027: dup
/*      */     //   3028: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   3031: ldc 23
/*      */     //   3033: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3036: aload_0
/*      */     //   3037: aload_0
/*      */     //   3038: getfield 869	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:original_url	Ljava/net/URL;
/*      */     //   3041: invokevirtual 1014	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:trimForDisplay	(Ljava/net/URL;)Ljava/lang/String;
/*      */     //   3044: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3047: ldc 7
/*      */     //   3049: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3052: aload_1
/*      */     //   3053: invokevirtual 928	java/net/MalformedURLException:getMessage	()Ljava/lang/String;
/*      */     //   3056: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3059: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3062: aload_1
/*      */     //   3063: invokespecial 987	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException:<init>	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   3066: athrow
/*      */     //   3067: astore_1
/*      */     //   3068: new 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   3071: dup
/*      */     //   3072: aload_0
/*      */     //   3073: new 513	java/lang/StringBuilder
/*      */     //   3076: dup
/*      */     //   3077: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   3080: ldc 22
/*      */     //   3082: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3085: aload_0
/*      */     //   3086: aload_0
/*      */     //   3087: getfield 869	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:original_url	Ljava/net/URL;
/*      */     //   3090: invokevirtual 1014	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:trimForDisplay	(Ljava/net/URL;)Ljava/lang/String;
/*      */     //   3093: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3096: ldc 9
/*      */     //   3098: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3101: aload_1
/*      */     //   3102: invokevirtual 951	java/net/UnknownHostException:getMessage	()Ljava/lang/String;
/*      */     //   3105: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3108: ldc 6
/*      */     //   3110: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3113: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3116: aload_1
/*      */     //   3117: invokespecial 987	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException:<init>	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   3120: athrow
/*      */     //   3121: astore_1
/*      */     //   3122: new 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   3125: dup
/*      */     //   3126: aload_0
/*      */     //   3127: new 513	java/lang/StringBuilder
/*      */     //   3130: dup
/*      */     //   3131: invokespecial 914	java/lang/StringBuilder:<init>	()V
/*      */     //   3134: ldc 27
/*      */     //   3136: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3139: aload_0
/*      */     //   3140: aload_0
/*      */     //   3141: getfield 869	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:original_url	Ljava/net/URL;
/*      */     //   3144: invokevirtual 1014	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:trimForDisplay	(Ljava/net/URL;)Ljava/lang/String;
/*      */     //   3147: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3150: ldc 6
/*      */     //   3152: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3155: invokevirtual 915	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3158: aload_1
/*      */     //   3159: invokespecial 987	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException:<init>	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   3162: athrow
/*      */     //   3163: astore_1
/*      */     //   3164: aload_1
/*      */     //   3165: instanceof 553
/*      */     //   3168: ifeq +11 -> 3179
/*      */     //   3171: aload_1
/*      */     //   3172: checkcast 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   3175: astore_2
/*      */     //   3176: goto +19 -> 3195
/*      */     //   3179: aload_1
/*      */     //   3180: invokestatic 975	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/Throwable;)V
/*      */     //   3183: new 553	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   3186: dup
/*      */     //   3187: aload_0
/*      */     //   3188: ldc 43
/*      */     //   3190: aload_1
/*      */     //   3191: invokespecial 987	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException:<init>	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   3194: astore_2
/*      */     //   3195: aload_0
/*      */     //   3196: aload_2
/*      */     //   3197: invokevirtual 1009	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl:informFailed	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException;)V
/*      */     //   3200: aload_2
/*      */     //   3201: athrow
/*      */     // Line number table:
/*      */     //   Java source line #587	-> byte code offset #0
/*      */     //   Java source line #590	-> byte code offset #38
/*      */     //   Java source line #592	-> byte code offset #45
/*      */     //   Java source line #594	-> byte code offset #52
/*      */     //   Java source line #597	-> byte code offset #63
/*      */     //   Java source line #601	-> byte code offset #68
/*      */     //   Java source line #602	-> byte code offset #75
/*      */     //   Java source line #601	-> byte code offset #78
/*      */     //   Java source line #605	-> byte code offset #88
/*      */     //   Java source line #609	-> byte code offset #110
/*      */     //   Java source line #611	-> byte code offset #118
/*      */     //   Java source line #613	-> byte code offset #127
/*      */     //   Java source line #615	-> byte code offset #135
/*      */     //   Java source line #617	-> byte code offset #144
/*      */     //   Java source line #619	-> byte code offset #159
/*      */     //   Java source line #621	-> byte code offset #169
/*      */     //   Java source line #623	-> byte code offset #177
/*      */     //   Java source line #625	-> byte code offset #183
/*      */     //   Java source line #627	-> byte code offset #190
/*      */     //   Java source line #629	-> byte code offset #193
/*      */     //   Java source line #634	-> byte code offset #219
/*      */     //   Java source line #636	-> byte code offset #228
/*      */     //   Java source line #640	-> byte code offset #234
/*      */     //   Java source line #644	-> byte code offset #238
/*      */     //   Java source line #646	-> byte code offset #254
/*      */     //   Java source line #648	-> byte code offset #263
/*      */     //   Java source line #652	-> byte code offset #276
/*      */     //   Java source line #654	-> byte code offset #282
/*      */     //   Java source line #658	-> byte code offset #322
/*      */     //   Java source line #664	-> byte code offset #370
/*      */     //   Java source line #661	-> byte code offset #373
/*      */     //   Java source line #663	-> byte code offset #375
/*      */     //   Java source line #667	-> byte code offset #380
/*      */     //   Java source line #670	-> byte code offset #385
/*      */     //   Java source line #672	-> byte code offset #392
/*      */     //   Java source line #675	-> byte code offset #400
/*      */     //   Java source line #677	-> byte code offset #407
/*      */     //   Java source line #680	-> byte code offset #412
/*      */     //   Java source line #682	-> byte code offset #414
/*      */     //   Java source line #684	-> byte code offset #417
/*      */     //   Java source line #685	-> byte code offset #420
/*      */     //   Java source line #687	-> byte code offset #423
/*      */     //   Java source line #689	-> byte code offset #432
/*      */     //   Java source line #690	-> byte code offset #435
/*      */     //   Java source line #692	-> byte code offset #441
/*      */     //   Java source line #694	-> byte code offset #444
/*      */     //   Java source line #697	-> byte code offset #448
/*      */     //   Java source line #699	-> byte code offset #453
/*      */     //   Java source line #703	-> byte code offset #456
/*      */     //   Java source line #705	-> byte code offset #459
/*      */     //   Java source line #707	-> byte code offset #473
/*      */     //   Java source line #709	-> byte code offset #482
/*      */     //   Java source line #711	-> byte code offset #487
/*      */     //   Java source line #714	-> byte code offset #498
/*      */     //   Java source line #715	-> byte code offset #507
/*      */     //   Java source line #719	-> byte code offset #519
/*      */     //   Java source line #724	-> byte code offset #522
/*      */     //   Java source line #726	-> byte code offset #531
/*      */     //   Java source line #731	-> byte code offset #534
/*      */     //   Java source line #733	-> byte code offset #553
/*      */     //   Java source line #737	-> byte code offset #566
/*      */     //   Java source line #739	-> byte code offset #579
/*      */     //   Java source line #746	-> byte code offset #584
/*      */     //   Java source line #759	-> byte code offset #597
/*      */     //   Java source line #768	-> byte code offset #602
/*      */     //   Java source line #770	-> byte code offset #607
/*      */     //   Java source line #772	-> byte code offset #616
/*      */     //   Java source line #774	-> byte code offset #621
/*      */     //   Java source line #776	-> byte code offset #650
/*      */     //   Java source line #778	-> byte code offset #658
/*      */     //   Java source line #774	-> byte code offset #671
/*      */     //   Java source line #783	-> byte code offset #677
/*      */     //   Java source line #826	-> byte code offset #692
/*      */     //   Java source line #828	-> byte code offset #699
/*      */     //   Java source line #830	-> byte code offset #710
/*      */     //   Java source line #832	-> byte code offset #717
/*      */     //   Java source line #835	-> byte code offset #724
/*      */     //   Java source line #837	-> byte code offset #729
/*      */     //   Java source line #840	-> byte code offset #734
/*      */     //   Java source line #842	-> byte code offset #744
/*      */     //   Java source line #844	-> byte code offset #753
/*      */     //   Java source line #847	-> byte code offset #760
/*      */     //   Java source line #849	-> byte code offset #764
/*      */     //   Java source line #851	-> byte code offset #767
/*      */     //   Java source line #856	-> byte code offset #777
/*      */     //   Java source line #861	-> byte code offset #785
/*      */     //   Java source line #865	-> byte code offset #790
/*      */     //   Java source line #869	-> byte code offset #802
/*      */     //   Java source line #876	-> byte code offset #811
/*      */     //   Java source line #878	-> byte code offset #816
/*      */     //   Java source line #881	-> byte code offset #883
/*      */     //   Java source line #883	-> byte code offset #891
/*      */     //   Java source line #885	-> byte code offset #896
/*      */     //   Java source line #887	-> byte code offset #905
/*      */     //   Java source line #889	-> byte code offset #915
/*      */     //   Java source line #891	-> byte code offset #924
/*      */     //   Java source line #894	-> byte code offset #933
/*      */     //   Java source line #896	-> byte code offset #941
/*      */     //   Java source line #898	-> byte code offset #956
/*      */     //   Java source line #902	-> byte code offset #968
/*      */     //   Java source line #906	-> byte code offset #983
/*      */     //   Java source line #909	-> byte code offset #992
/*      */     //   Java source line #911	-> byte code offset #996
/*      */     //   Java source line #914	-> byte code offset #1005
/*      */     //   Java source line #916	-> byte code offset #1012
/*      */     //   Java source line #918	-> byte code offset #1027
/*      */     //   Java source line #920	-> byte code offset #1033
/*      */     //   Java source line #922	-> byte code offset #1041
/*      */     //   Java source line #924	-> byte code offset #1046
/*      */     //   Java source line #927	-> byte code offset #1050
/*      */     //   Java source line #929	-> byte code offset #1060
/*      */     //   Java source line #931	-> byte code offset #1068
/*      */     //   Java source line #933	-> byte code offset #1075
/*      */     //   Java source line #935	-> byte code offset #1084
/*      */     //   Java source line #939	-> byte code offset #1089
/*      */     //   Java source line #941	-> byte code offset #1097
/*      */     //   Java source line #943	-> byte code offset #1104
/*      */     //   Java source line #946	-> byte code offset #1112
/*      */     //   Java source line #948	-> byte code offset #1120
/*      */     //   Java source line #950	-> byte code offset #1127
/*      */     //   Java source line #953	-> byte code offset #1135
/*      */     //   Java source line #956	-> byte code offset #1143
/*      */     //   Java source line #961	-> byte code offset #1148
/*      */     //   Java source line #958	-> byte code offset #1151
/*      */     //   Java source line #960	-> byte code offset #1153
/*      */     //   Java source line #963	-> byte code offset #1166
/*      */     //   Java source line #965	-> byte code offset #1190
/*      */     //   Java source line #967	-> byte code offset #1193
/*      */     //   Java source line #972	-> byte code offset #1209
/*      */     //   Java source line #974	-> byte code offset #1218
/*      */     //   Java source line #976	-> byte code offset #1223
/*      */     //   Java source line #978	-> byte code offset #1247
/*      */     //   Java source line #981	-> byte code offset #1258
/*      */     //   Java source line #986	-> byte code offset #1268
/*      */     //   Java source line #988	-> byte code offset #1279
/*      */     //   Java source line #990	-> byte code offset #1282
/*      */     //   Java source line #992	-> byte code offset #1287
/*      */     //   Java source line #994	-> byte code offset #1300
/*      */     //   Java source line #996	-> byte code offset #1305
/*      */     //   Java source line #1000	-> byte code offset #1309
/*      */     //   Java source line #1002	-> byte code offset #1373
/*      */     //   Java source line #1003	-> byte code offset #1382
/*      */     //   Java source line #1005	-> byte code offset #1391
/*      */     //   Java source line #1009	-> byte code offset #1394
/*      */     //   Java source line #1010	-> byte code offset #1404
/*      */     //   Java source line #1012	-> byte code offset #1414
/*      */     //   Java source line #1014	-> byte code offset #1429
/*      */     //   Java source line #1017	-> byte code offset #1433
/*      */     //   Java source line #1019	-> byte code offset #1450
/*      */     //   Java source line #1021	-> byte code offset #1459
/*      */     //   Java source line #1023	-> byte code offset #1464
/*      */     //   Java source line #1025	-> byte code offset #1479
/*      */     //   Java source line #1027	-> byte code offset #1498
/*      */     //   Java source line #1029	-> byte code offset #1504
/*      */     //   Java source line #1023	-> byte code offset #1516
/*      */     //   Java source line #1034	-> byte code offset #1522
/*      */     //   Java source line #1036	-> byte code offset #1532
/*      */     //   Java source line #1038	-> byte code offset #1536
/*      */     //   Java source line #1040	-> byte code offset #1542
/*      */     //   Java source line #1042	-> byte code offset #1553
/*      */     //   Java source line #1044	-> byte code offset #1561
/*      */     //   Java source line #1047	-> byte code offset #1568
/*      */     //   Java source line #1049	-> byte code offset #1599
/*      */     //   Java source line #1052	-> byte code offset #1642
/*      */     //   Java source line #1057	-> byte code offset #1650
/*      */     //   Java source line #1054	-> byte code offset #1653
/*      */     //   Java source line #1056	-> byte code offset #1655
/*      */     //   Java source line #1059	-> byte code offset #1660
/*      */     //   Java source line #1374	-> byte code offset #1663
/*      */     //   Java source line #1376	-> byte code offset #1668
/*      */     //   Java source line #1382	-> byte code offset #1674
/*      */     //   Java source line #1384	-> byte code offset #1679
/*      */     //   Java source line #1065	-> byte code offset #1691
/*      */     //   Java source line #1063	-> byte code offset #1694
/*      */     //   Java source line #1069	-> byte code offset #1696
/*      */     //   Java source line #1071	-> byte code offset #1712
/*      */     //   Java source line #1076	-> byte code offset #1744
/*      */     //   Java source line #1078	-> byte code offset #1751
/*      */     //   Java source line #1080	-> byte code offset #1758
/*      */     //   Java source line #1082	-> byte code offset #1761
/*      */     //   Java source line #1084	-> byte code offset #1766
/*      */     //   Java source line #1086	-> byte code offset #1775
/*      */     //   Java source line #1088	-> byte code offset #1780
/*      */     //   Java source line #1090	-> byte code offset #1790
/*      */     //   Java source line #1092	-> byte code offset #1804
/*      */     //   Java source line #1094	-> byte code offset #1814
/*      */     //   Java source line #1098	-> byte code offset #1825
/*      */     //   Java source line #1103	-> byte code offset #1835
/*      */     //   Java source line #1105	-> byte code offset #1841
/*      */     //   Java source line #1107	-> byte code offset #1845
/*      */     //   Java source line #1110	-> byte code offset #1850
/*      */     //   Java source line #1113	-> byte code offset #1866
/*      */     //   Java source line #1112	-> byte code offset #1869
/*      */     //   Java source line #1116	-> byte code offset #1871
/*      */     //   Java source line #1119	-> byte code offset #1963
/*      */     //   Java source line #1121	-> byte code offset #1969
/*      */     //   Java source line #1124	-> byte code offset #1972
/*      */     //   Java source line #1126	-> byte code offset #1979
/*      */     //   Java source line #1128	-> byte code offset #1988
/*      */     //   Java source line #1130	-> byte code offset #1997
/*      */     //   Java source line #1132	-> byte code offset #2002
/*      */     //   Java source line #1134	-> byte code offset #2012
/*      */     //   Java source line #1136	-> byte code offset #2015
/*      */     //   Java source line #1138	-> byte code offset #2033
/*      */     //   Java source line #1140	-> byte code offset #2043
/*      */     //   Java source line #1142	-> byte code offset #2046
/*      */     //   Java source line #1147	-> byte code offset #2061
/*      */     //   Java source line #1148	-> byte code offset #2068
/*      */     //   Java source line #1147	-> byte code offset #2071
/*      */     //   Java source line #1150	-> byte code offset #2083
/*      */     //   Java source line #1154	-> byte code offset #2091
/*      */     //   Java source line #1156	-> byte code offset #2102
/*      */     //   Java source line #1158	-> byte code offset #2112
/*      */     //   Java source line #1162	-> byte code offset #2132
/*      */     //   Java source line #1163	-> byte code offset #2135
/*      */     //   Java source line #1166	-> byte code offset #2138
/*      */     //   Java source line #1168	-> byte code offset #2144
/*      */     //   Java source line #1181	-> byte code offset #2147
/*      */     //   Java source line #1183	-> byte code offset #2165
/*      */     //   Java source line #1185	-> byte code offset #2208
/*      */     //   Java source line #1187	-> byte code offset #2215
/*      */     //   Java source line #1189	-> byte code offset #2235
/*      */     //   Java source line #1192	-> byte code offset #2238
/*      */     //   Java source line #1194	-> byte code offset #2249
/*      */     //   Java source line #1196	-> byte code offset #2254
/*      */     //   Java source line #1198	-> byte code offset #2263
/*      */     //   Java source line #1200	-> byte code offset #2268
/*      */     //   Java source line #1202	-> byte code offset #2273
/*      */     //   Java source line #1204	-> byte code offset #2284
/*      */     //   Java source line #1206	-> byte code offset #2294
/*      */     //   Java source line #1209	-> byte code offset #2297
/*      */     //   Java source line #1213	-> byte code offset #2310
/*      */     //   Java source line #1216	-> byte code offset #2320
/*      */     //   Java source line #1218	-> byte code offset #2328
/*      */     //   Java source line #1220	-> byte code offset #2334
/*      */     //   Java source line #1222	-> byte code offset #2341
/*      */     //   Java source line #1228	-> byte code offset #2355
/*      */     //   Java source line #1232	-> byte code offset #2358
/*      */     //   Java source line #1234	-> byte code offset #2373
/*      */     //   Java source line #1239	-> byte code offset #2381
/*      */     //   Java source line #1243	-> byte code offset #2436
/*      */     //   Java source line #1248	-> byte code offset #2447
/*      */     //   Java source line #1251	-> byte code offset #2452
/*      */     //   Java source line #1254	-> byte code offset #2457
/*      */     //   Java source line #1253	-> byte code offset #2460
/*      */     //   Java source line #1257	-> byte code offset #2462
/*      */     //   Java source line #1258	-> byte code offset #2469
/*      */     //   Java source line #1248	-> byte code offset #2472
/*      */     //   Java source line #1251	-> byte code offset #2479
/*      */     //   Java source line #1254	-> byte code offset #2484
/*      */     //   Java source line #1253	-> byte code offset #2487
/*      */     //   Java source line #1257	-> byte code offset #2489
/*      */     //   Java source line #1262	-> byte code offset #2499
/*      */     //   Java source line #1264	-> byte code offset #2504
/*      */     //   Java source line #1266	-> byte code offset #2515
/*      */     //   Java source line #1270	-> byte code offset #2521
/*      */     //   Java source line #1273	-> byte code offset #2535
/*      */     //   Java source line #1276	-> byte code offset #2538
/*      */     //   Java source line #1278	-> byte code offset #2547
/*      */     //   Java source line #1280	-> byte code offset #2550
/*      */     //   Java source line #1284	-> byte code offset #2554
/*      */     //   Java source line #1286	-> byte code offset #2559
/*      */     //   Java source line #1374	-> byte code offset #2564
/*      */     //   Java source line #1376	-> byte code offset #2569
/*      */     //   Java source line #1382	-> byte code offset #2575
/*      */     //   Java source line #1384	-> byte code offset #2580
/*      */     //   Java source line #1393	-> byte code offset #2589
/*      */     //   Java source line #1395	-> byte code offset #2596
/*      */     //   Java source line #1398	-> byte code offset #2601
/*      */     //   Java source line #1400	-> byte code offset #2608
/*      */     //   Java source line #1284	-> byte code offset #2619
/*      */     //   Java source line #1286	-> byte code offset #2624
/*      */     //   Java source line #1284	-> byte code offset #2632
/*      */     //   Java source line #1286	-> byte code offset #2639
/*      */     //   Java source line #1290	-> byte code offset #2647
/*      */     //   Java source line #1292	-> byte code offset #2688
/*      */     //   Java source line #1294	-> byte code offset #2690
/*      */     //   Java source line #1296	-> byte code offset #2697
/*      */     //   Java source line #1298	-> byte code offset #2703
/*      */     //   Java source line #1300	-> byte code offset #2706
/*      */     //   Java source line #1302	-> byte code offset #2716
/*      */     //   Java source line #1304	-> byte code offset #2721
/*      */     //   Java source line #1306	-> byte code offset #2724
/*      */     //   Java source line #1308	-> byte code offset #2730
/*      */     //   Java source line #1310	-> byte code offset #2740
/*      */     //   Java source line #1312	-> byte code offset #2745
/*      */     //   Java source line #1314	-> byte code offset #2748
/*      */     //   Java source line #1318	-> byte code offset #2751
/*      */     //   Java source line #1323	-> byte code offset #2764
/*      */     //   Java source line #1326	-> byte code offset #2767
/*      */     //   Java source line #1374	-> byte code offset #2772
/*      */     //   Java source line #1376	-> byte code offset #2777
/*      */     //   Java source line #1332	-> byte code offset #2786
/*      */     //   Java source line #1334	-> byte code offset #2789
/*      */     //   Java source line #1336	-> byte code offset #2791
/*      */     //   Java source line #1338	-> byte code offset #2796
/*      */     //   Java source line #1374	-> byte code offset #2798
/*      */     //   Java source line #1376	-> byte code offset #2803
/*      */     //   Java source line #1374	-> byte code offset #2812
/*      */     //   Java source line #1376	-> byte code offset #2817
/*      */     //   Java source line #1342	-> byte code offset #2826
/*      */     //   Java source line #1344	-> byte code offset #2828
/*      */     //   Java source line #1346	-> byte code offset #2833
/*      */     //   Java source line #1348	-> byte code offset #2840
/*      */     //   Java source line #1350	-> byte code offset #2845
/*      */     //   Java source line #1352	-> byte code offset #2855
/*      */     //   Java source line #1354	-> byte code offset #2865
/*      */     //   Java source line #1374	-> byte code offset #2867
/*      */     //   Java source line #1376	-> byte code offset #2872
/*      */     //   Java source line #1360	-> byte code offset #2881
/*      */     //   Java source line #1362	-> byte code offset #2888
/*      */     //   Java source line #1364	-> byte code offset #2893
/*      */     //   Java source line #1374	-> byte code offset #2897
/*      */     //   Java source line #1376	-> byte code offset #2902
/*      */     //   Java source line #1370	-> byte code offset #2911
/*      */     //   Java source line #1374	-> byte code offset #2914
/*      */     //   Java source line #1376	-> byte code offset #2921
/*      */     //   Java source line #724	-> byte code offset #2930
/*      */     //   Java source line #1382	-> byte code offset #2936
/*      */     //   Java source line #1384	-> byte code offset #2941
/*      */     //   Java source line #1382	-> byte code offset #2953
/*      */     //   Java source line #1384	-> byte code offset #2960
/*      */     //   Java source line #1387	-> byte code offset #2972
/*      */     //   Java source line #1389	-> byte code offset #2975
/*      */     //   Java source line #1393	-> byte code offset #2986
/*      */     //   Java source line #1395	-> byte code offset #2995
/*      */     //   Java source line #1398	-> byte code offset #3000
/*      */     //   Java source line #1400	-> byte code offset #3007
/*      */     //   Java source line #1403	-> byte code offset #3018
/*      */     //   Java source line #1405	-> byte code offset #3019
/*      */     //   Java source line #1407	-> byte code offset #3067
/*      */     //   Java source line #1409	-> byte code offset #3068
/*      */     //   Java source line #1411	-> byte code offset #3121
/*      */     //   Java source line #1413	-> byte code offset #3122
/*      */     //   Java source line #1415	-> byte code offset #3163
/*      */     //   Java source line #1419	-> byte code offset #3164
/*      */     //   Java source line #1421	-> byte code offset #3171
/*      */     //   Java source line #1424	-> byte code offset #3179
/*      */     //   Java source line #1426	-> byte code offset #3183
/*      */     //   Java source line #1429	-> byte code offset #3195
/*      */     //   Java source line #1431	-> byte code offset #3200
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	3202	0	this	ResourceDownloaderURLImpl
/*      */     //   78	9	1	localObject1	Object
/*      */     //   109	2887	1	outer_url	URL
/*      */     //   3018	45	1	e	java.net.MalformedURLException
/*      */     //   3067	50	1	e	java.net.UnknownHostException
/*      */     //   3121	38	1	e	IOException
/*      */     //   3163	28	1	e	Throwable
/*      */     //   117	103	2	protocol	String
/*      */     //   3175	2	2	rde	ResourceDownloaderException
/*      */     //   3194	7	2	rde	ResourceDownloaderException
/*      */     //   158	13	3	file	java.io.File
/*      */     //   230	2	3	target_port	int
/*      */     //   237	113	3	target_port	int
/*      */     //   413	2454	3	use_compression	boolean
/*      */     //   167	24	4	fis	java.io.FileInputStream
/*      */     //   252	102	4	str	String
/*      */     //   373	3	4	e	Throwable
/*      */     //   415	1247	4	follow_redirect	boolean
/*      */     //   261	95	5	pos	int
/*      */     //   418	2305	5	dh_hack	boolean
/*      */     //   421	2326	6	internal_error_hack	boolean
/*      */     //   430	829	7	redirect_urls	Set<String>
/*      */     //   433	2463	8	current_url	URL
/*      */     //   439	942	9	current_proxy	Proxy
/*      */     //   442	2310	10	current_plugin_proxy	AEProxyFactory.PluginProxy
/*      */     //   446	908	11	initial_url	URL
/*      */     //   480	28	12	plugin_proxy_auto	AEProxyFactory.PluginProxy
/*      */     //   520	2441	12	plugin_proxy_auto	AEProxyFactory.PluginProxy
/*      */     //   457	2506	13	ok	boolean
/*      */     //   523	2408	14	connect_loop	int
/*      */     //   532	2390	15	temp_file	java.io.File
/*      */     //   762	3	16	con	URLConnection
/*      */     //   775	1384	16	con	URLConnection
/*      */     //   2688	99	16	e	javax.net.ssl.SSLException
/*      */     //   2789	3	16	e	java.util.zip.ZipException
/*      */     //   2826	86	16	e	IOException
/*      */     //   577	184	17	ssl_con	javax.net.ssl.HttpsURLConnection
/*      */     //   889	17	17	cidg	org.gudy.azureus2.plugins.clientid.ClientIDGenerator
/*      */     //   2695	36	17	msg	String
/*      */     //   2838	18	17	msg	String
/*      */     //   605	17	18	tmf	javax.net.ssl.TrustManagerFactory
/*      */     //   751	3	18	host	String
/*      */     //   903	13	18	props	java.util.Properties
/*      */     //   939	35	18	connection	String
/*      */     //   2704	64	18	try_again	boolean
/*      */     //   2886	8	18	retry_url	URL
/*      */     //   614	69	19	default_tms	List<X509TrustManager>
/*      */     //   922	7	19	ua	String
/*      */     //   1039	17	19	verb	String
/*      */     //   1095	12	19	connect_timeout	long
/*      */     //   626	18	20	arr$	javax.net.ssl.TrustManager[]
/*      */     //   690	13	20	tms_delegate	javax.net.ssl.TrustManager[]
/*      */     //   1073	12	20	os	java.io.OutputStream
/*      */     //   631	8	21	len$	int
/*      */     //   697	14	21	sc	javax.net.ssl.SSLContext
/*      */     //   1118	12	21	read_timeout	long
/*      */     //   634	38	22	i$	int
/*      */     //   715	5	22	factory	javax.net.ssl.SSLSocketFactory
/*      */     //   648	13	23	tm	javax.net.ssl.TrustManager
/*      */     //   1141	1090	23	trust_content_length	boolean
/*      */     //   1151	7	24	e	com.aelitis.azureus.core.proxy.AEProxyFactory.UnknownHostException
/*      */     //   1188	715	24	response	int
/*      */     //   1216	57	25	move_to	String
/*      */     //   1749	167	25	http_con	java.net.HttpURLConnection
/*      */     //   1970	178	25	compressed	boolean
/*      */     //   1277	153	26	move_to_url	URL
/*      */     //   1694	3	26	e	Throwable
/*      */     //   1756	70	26	error_stream	InputStream
/*      */     //   1995	39	26	encoding	String
/*      */     //   2100	18	26	errors	List<String>
/*      */     //   2133	393	26	baos	java.io.ByteArrayOutputStream
/*      */     //   1280	135	27	follow	boolean
/*      */     //   1759	187	27	error_str	String
/*      */     //   2136	344	27	fos	java.io.FileOutputStream
/*      */     //   1298	85	28	child	AEProxyFactory.PluginProxy
/*      */     //   1402	18	28	original_protocol	String
/*      */     //   1773	32	28	encoding	String
/*      */     //   1843	47	28	dest	URL
/*      */     //   2142	171	28	buf	byte[]
/*      */     //   2460	3	28	e	Throwable
/*      */     //   2513	3	28	res	InputStream
/*      */     //   2533	107	28	res	InputStream
/*      */     //   1412	10	29	new_protocol	String
/*      */     //   1869	3	29	e	Throwable
/*      */     //   2145	278	29	total_read	long
/*      */     //   2536	99	29	handed_over	boolean
/*      */     //   1448	32	30	cookies_list	List<String>
/*      */     //   1653	964	30	e	Throwable
/*      */     //   1457	112	31	cookies_set	List<String>
/*      */     //   2163	249	31	size	long
/*      */     //   1465	52	32	i	int
/*      */     //   1534	112	32	new_cookies	String
/*      */     //   1496	11	33	cookie_bits	String[]
/*      */     //   1540	3	33	properties	Map
/*      */     //   2247	76	33	read	int
/*      */     //   1551	11	34	obj	Object
/*      */     //   1575	13	35	i$	Iterator
/*      */     //   1597	33	36	s	String
/*      */     //   2071	10	37	localObject2	Object
/*      */     //   2472	25	38	localObject3	Object
/*      */     //   2487	3	39	e	Throwable
/*      */     //   2632	13	40	localObject4	Object
/*      */     //   2914	14	41	localObject5	Object
/*      */     //   2953	17	42	localObject6	Object
/*      */     //   2986	30	43	localObject7	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   38	68	78	finally
/*      */     //   78	79	78	finally
/*      */     //   238	370	373	java/lang/Throwable
/*      */     //   1143	1148	1151	com/aelitis/azureus/core/proxy/AEProxyFactory$UnknownHostException
/*      */     //   1433	1650	1653	java/lang/Throwable
/*      */     //   1268	1663	1694	java/lang/Throwable
/*      */     //   1850	1866	1869	java/lang/Throwable
/*      */     //   1972	2061	2071	finally
/*      */     //   2071	2073	2071	finally
/*      */     //   2452	2457	2460	java/lang/Throwable
/*      */     //   2138	2447	2472	finally
/*      */     //   2472	2474	2472	finally
/*      */     //   2479	2484	2487	java/lang/Throwable
/*      */     //   2538	2554	2632	finally
/*      */     //   2632	2634	2632	finally
/*      */     //   534	1663	2688	javax/net/ssl/SSLException
/*      */     //   1691	2564	2688	javax/net/ssl/SSLException
/*      */     //   2619	2688	2688	javax/net/ssl/SSLException
/*      */     //   534	1663	2789	java/util/zip/ZipException
/*      */     //   1691	2564	2789	java/util/zip/ZipException
/*      */     //   2619	2688	2789	java/util/zip/ZipException
/*      */     //   534	1663	2826	java/io/IOException
/*      */     //   1691	2564	2826	java/io/IOException
/*      */     //   2619	2688	2826	java/io/IOException
/*      */     //   534	1663	2914	finally
/*      */     //   1691	2564	2914	finally
/*      */     //   2619	2772	2914	finally
/*      */     //   2786	2798	2914	finally
/*      */     //   2826	2867	2914	finally
/*      */     //   2881	2897	2914	finally
/*      */     //   2911	2916	2914	finally
/*      */     //   522	1674	2953	finally
/*      */     //   1691	2575	2953	finally
/*      */     //   2619	2936	2953	finally
/*      */     //   2953	2955	2953	finally
/*      */     //   385	2589	2986	finally
/*      */     //   2619	2988	2986	finally
/*      */     //   88	192	3018	java/net/MalformedURLException
/*      */     //   193	2616	3018	java/net/MalformedURLException
/*      */     //   2619	3018	3018	java/net/MalformedURLException
/*      */     //   88	192	3067	java/net/UnknownHostException
/*      */     //   193	2616	3067	java/net/UnknownHostException
/*      */     //   2619	3018	3067	java/net/UnknownHostException
/*      */     //   88	192	3121	java/io/IOException
/*      */     //   193	2616	3121	java/io/IOException
/*      */     //   2619	3018	3121	java/io/IOException
/*      */     //   0	192	3163	java/lang/Throwable
/*      */     //   193	2616	3163	java/lang/Throwable
/*      */     //   2619	3163	3163	java/lang/Throwable
/*      */   }
/*      */   
/*      */   public void cancel()
/*      */   {
/* 1438 */     setCancelled();
/*      */     
/* 1440 */     this.cancel_download = true;
/*      */     try
/*      */     {
/* 1443 */       this.this_mon.enter();
/*      */       
/* 1445 */       if (this.input_stream != null) {
/*      */         try
/*      */         {
/* 1448 */           this.input_stream.close();
/*      */ 
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1456 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1459 */     informFailed(new ResourceDownloaderCancelledException(this));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setRequestProperties(URLConnection con, boolean use_compression)
/*      */   {
/* 1467 */     Map properties = getLCKeyProperties();
/*      */     
/* 1469 */     Iterator it = properties.entrySet().iterator();
/*      */     
/* 1471 */     while (it.hasNext())
/*      */     {
/* 1473 */       Map.Entry entry = (Map.Entry)it.next();
/*      */       
/* 1475 */       String key = (String)entry.getKey();
/* 1476 */       Object value = entry.getValue();
/*      */       
/* 1478 */       if ((key.startsWith("url_")) && ((value instanceof String)))
/*      */       {
/* 1480 */         if ((!value.equals("skip")) && 
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1485 */           (!key.equalsIgnoreCase("URL_HTTP_VERB")))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1490 */           key = key.substring(4);
/*      */           
/* 1492 */           if ((!key.equals("accept-encoding")) || (use_compression))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1498 */             String nice_key = "";
/*      */             
/* 1500 */             boolean upper = true;
/*      */             
/* 1502 */             for (char c : key.toCharArray())
/*      */             {
/* 1504 */               if (upper) {
/* 1505 */                 c = Character.toUpperCase(c);
/* 1506 */                 upper = false;
/* 1507 */               } else if (c == '-') {
/* 1508 */                 upper = true;
/*      */               }
/*      */               
/* 1511 */               nice_key = nice_key + c;
/*      */             }
/*      */             
/* 1514 */             con.setRequestProperty(nice_key, (String)value);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void getRequestProperties(URLConnection con)
/*      */   {
/*      */     try
/*      */     {
/* 1525 */       setProperty("ContentType", con.getContentType());
/*      */       
/* 1527 */       setProperty("URL_URL", con.getURL());
/*      */       
/* 1529 */       Map headers = con.getHeaderFields();
/*      */       
/* 1531 */       Iterator it = headers.entrySet().iterator();
/*      */       
/* 1533 */       while (it.hasNext())
/*      */       {
/* 1535 */         Map.Entry entry = (Map.Entry)it.next();
/*      */         
/* 1537 */         String key = (String)entry.getKey();
/* 1538 */         Object val = entry.getValue();
/*      */         
/* 1540 */         if (key != null)
/*      */         {
/* 1542 */           setProperty("URL_" + key, val);
/*      */         }
/*      */       }
/*      */       
/* 1546 */       setPropertiesSet();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1550 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public PasswordAuthentication getAuthentication(String realm, URL tracker)
/*      */   {
/* 1559 */     if ((this.user_name == null) || (this.password == null))
/*      */     {
/* 1561 */       String user_info = tracker.getUserInfo();
/*      */       
/* 1563 */       if (user_info == null)
/*      */       {
/* 1565 */         return null;
/*      */       }
/*      */       
/* 1568 */       String user_bit = user_info;
/* 1569 */       String pw_bit = "";
/*      */       
/* 1571 */       int pos = user_info.indexOf(':');
/*      */       
/* 1573 */       if (pos != -1)
/*      */       {
/* 1575 */         user_bit = user_info.substring(0, pos);
/* 1576 */         pw_bit = user_info.substring(pos + 1);
/*      */       }
/*      */       
/* 1579 */       return new PasswordAuthentication(user_bit, pw_bit.toCharArray());
/*      */     }
/*      */     
/* 1582 */     return new PasswordAuthentication(this.user_name, this.password.toCharArray());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setAuthenticationOutcome(String realm, URL tracker, boolean success) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void clearPasswords() {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private URLConnection openConnection(Proxy proxy, URL url)
/*      */     throws IOException
/*      */   {
/* 1605 */     if (this.force_no_proxy)
/*      */     {
/* 1607 */       return url.openConnection(Proxy.NO_PROXY);
/*      */     }
/* 1609 */     if (proxy != null)
/*      */     {
/* 1611 */       return url.openConnection(proxy);
/*      */     }
/*      */     
/*      */ 
/* 1615 */     return url.openConnection();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String trimForDisplay(URL url)
/*      */   {
/* 1623 */     if (this.force_proxy != null)
/*      */     {
/* 1625 */       AEProxyFactory.PluginProxy pp = AEProxyFactory.getPluginProxy(this.force_proxy);
/*      */       
/* 1627 */       if (pp != null) {
/*      */         try
/*      */         {
/* 1630 */           url = new URL(pp.getTarget());
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */     
/* 1636 */     String str = url.toString();
/*      */     
/* 1638 */     int pos = str.indexOf('?');
/*      */     
/* 1640 */     if (pos != -1)
/*      */     {
/* 1642 */       str = str.substring(0, pos);
/*      */     }
/*      */     
/* 1645 */     return str;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderURLImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */