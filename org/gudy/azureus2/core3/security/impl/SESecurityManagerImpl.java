/*      */ package org.gudy.azureus2.core3.security.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.InputStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.net.Authenticator;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.PasswordAuthentication;
/*      */ import java.net.URL;
/*      */ import java.security.Key;
/*      */ import java.security.KeyStore;
/*      */ import java.security.Permission;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.CertificateException;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import javax.net.ssl.HttpsURLConnection;
/*      */ import javax.net.ssl.KeyManagerFactory;
/*      */ import javax.net.ssl.SSLContext;
/*      */ import javax.net.ssl.SSLServerSocketFactory;
/*      */ import javax.net.ssl.SSLSocketFactory;
/*      */ import javax.net.ssl.TrustManager;
/*      */ import javax.net.ssl.TrustManagerFactory;
/*      */ import javax.net.ssl.X509TrustManager;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.security.SECertificateListener;
/*      */ import org.gudy.azureus2.core3.security.SEKeyDetails;
/*      */ import org.gudy.azureus2.core3.security.SEPasswordListener;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
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
/*      */ public class SESecurityManagerImpl
/*      */ {
/*   73 */   private static final LogIDs LOGID = LogIDs.NET;
/*      */   
/*   75 */   protected static final SESecurityManagerImpl singleton = new SESecurityManagerImpl();
/*      */   protected static String KEYSTORE_TYPE;
/*      */   private static boolean auto_install_certs;
/*      */   
/*      */   static {
/*   80 */     String[] types = { "JKS", "GKR", "BKS" };
/*      */     
/*   82 */     for (int i = 0; i < types.length; i++) {
/*      */       try {
/*   84 */         KeyStore.getInstance(types[i]);
/*      */         
/*   86 */         KEYSTORE_TYPE = types[i];
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*   94 */     if (KEYSTORE_TYPE == null)
/*      */     {
/*      */ 
/*      */ 
/*   98 */       KEYSTORE_TYPE = "JKS";
/*      */     }
/*      */     
/*  101 */     Logger.log(new LogEvent(LOGID, "Keystore type is " + KEYSTORE_TYPE));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  108 */     COConfigurationManager.addAndFireParameterListener("security.cert.auto.install", new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameter_name)
/*      */       {
/*      */ 
/*      */ 
/*  116 */         SESecurityManagerImpl.access$002(COConfigurationManager.getBooleanParameter(parameter_name));
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   protected String keystore_name;
/*      */   protected String truststore_name;
/*  123 */   protected final List<SECertificateListener> certificate_listeners = new ArrayList();
/*      */   
/*  125 */   protected final CopyOnWriteList password_listeners = new CopyOnWriteList();
/*      */   
/*      */ 
/*  128 */   private static final ThreadLocal tls = new ThreadLocal()
/*      */   {
/*      */ 
/*      */     public Object initialValue()
/*      */     {
/*      */ 
/*  134 */       return null;
/*      */     }
/*      */   };
/*      */   
/*  138 */   protected final Map password_handlers = new HashMap();
/*  139 */   protected final Map certificate_handlers = new HashMap();
/*      */   
/*  141 */   protected boolean exit_vm_permitted = false;
/*      */   
/*      */   private AzureusSecurityManager my_sec_man;
/*      */   
/*  145 */   protected final AEMonitor this_mon = new AEMonitor("SESecurityManager");
/*      */   
/*      */ 
/*      */   public static SESecurityManagerImpl getSingleton()
/*      */   {
/*  150 */     return singleton;
/*      */   }
/*      */   
/*  153 */   private boolean initialized = false;
/*      */   
/*  155 */   final List stoppable_threads = new ArrayList();
/*      */   private boolean hack_constructor_tried;
/*      */   private Constructor<TrustManager> hack_constructor;
/*      */   
/*      */   public void initialise() {
/*  160 */     synchronized (this)
/*      */     {
/*  162 */       if (this.initialized)
/*  163 */         return;
/*  164 */       this.initialized = true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  175 */     this.keystore_name = FileUtil.getUserFile(".keystore").getAbsolutePath();
/*  176 */     this.truststore_name = FileUtil.getUserFile(".certs").getAbsolutePath();
/*      */     
/*  178 */     System.setProperty("javax.net.ssl.trustStore", this.truststore_name);
/*      */     
/*  180 */     System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
/*      */     
/*      */ 
/*  183 */     installAuthenticator();
/*      */     
/*      */ 
/*  186 */     String[] providers = { "com.sun.net.ssl.internal.ssl.Provider", "org.metastatic.jessie.provider.Jessie", "org.gudy.bouncycastle.jce.provider.BouncyCastleProvider" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  191 */     String provider = null;
/*      */     
/*  193 */     for (int i = 0; i < providers.length; i++) {
/*      */       try
/*      */       {
/*  196 */         Class.forName(providers[i]).newInstance();
/*      */         
/*  198 */         provider = providers[i];
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  206 */     if (provider == null)
/*      */     {
/*  208 */       Debug.out("No SSL provider available");
/*      */     }
/*      */     try
/*      */     {
/*  212 */       SESecurityManagerBC.initialise();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  216 */       Debug.out(e);
/*      */       
/*  218 */       Logger.log(new LogEvent(LOGID, 3, "Bouncy Castle not available"));
/*      */     }
/*      */     
/*      */ 
/*  222 */     installSecurityManager();
/*      */     
/*  224 */     ensureStoreExists(this.keystore_name);
/*      */     
/*  226 */     ensureStoreExists(this.truststore_name);
/*      */     
/*  228 */     initEmptyTrustStore();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void initEmptyTrustStore()
/*      */   {
/*      */     try
/*      */     {
/*  261 */       File target = new File(this.truststore_name);
/*      */       
/*  263 */       if ((target.exists()) && (target.length() > 2048L))
/*      */       {
/*      */ 
/*      */ 
/*  267 */         return;
/*      */       }
/*      */       
/*  270 */       KeyStore keystore = getTrustStore();
/*      */       
/*  272 */       if (keystore.size() == 0)
/*      */       {
/*  274 */         File cacerts = new File(new File(new File(System.getProperty("java.home"), "lib"), "security"), "cacerts");
/*      */         
/*  276 */         if (cacerts.exists())
/*      */         {
/*  278 */           FileUtil.copyFile(cacerts, target);
/*      */           try
/*      */           {
/*  281 */             getTrustStore();
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  285 */             target.delete();
/*      */             
/*  287 */             ensureStoreExists(this.truststore_name);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean resetTrustStore(boolean test_only)
/*      */   {
/*  299 */     return resetTrustStore(test_only, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean resetTrustStore(boolean test_only, boolean recovering)
/*      */   {
/*  307 */     File cacerts = new File(new File(new File(System.getProperty("java.home"), "lib"), "security"), "cacerts");
/*      */     
/*  309 */     if (!cacerts.exists())
/*      */     {
/*  311 */       return false;
/*      */     }
/*      */     
/*  314 */     if (test_only)
/*      */     {
/*  316 */       return true;
/*      */     }
/*      */     
/*  319 */     File target = new File(this.truststore_name);
/*      */     
/*  321 */     if (target.exists())
/*      */     {
/*  323 */       if (!target.delete())
/*      */       {
/*  325 */         Debug.out("Failed to delete " + target);
/*      */         
/*  327 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  331 */     if (!FileUtil.copyFile(cacerts, target))
/*      */     {
/*  333 */       Debug.out("Failed to copy file from " + cacerts + " to " + target);
/*      */       
/*  335 */       return false;
/*      */     }
/*      */     try
/*      */     {
/*  339 */       getTrustStore(!recovering);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  343 */       Debug.out(e);
/*      */       
/*  345 */       target.delete();
/*      */       
/*  347 */       ensureStoreExists(this.truststore_name);
/*      */       
/*  349 */       return false;
/*      */     }
/*      */     
/*  352 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getKeystoreName()
/*      */   {
/*  358 */     return this.keystore_name;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getKeystorePassword()
/*      */   {
/*  364 */     return "changeit";
/*      */   }
/*      */   
/*      */ 
/*      */   protected void installSecurityManager()
/*      */   {
/*  370 */     if (!Constants.isAndroid)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  376 */       String prop = System.getProperty("azureus.security.manager.install", "1");
/*      */       
/*  378 */       if (prop.equals("0"))
/*      */       {
/*  380 */         Debug.outNoStack("Not installing security manager - disabled by system property");
/*      */         
/*  382 */         return;
/*      */       }
/*      */       try
/*      */       {
/*  386 */         SecurityManager old_sec_man = System.getSecurityManager();
/*      */         
/*  388 */         this.my_sec_man = new AzureusSecurityManager(old_sec_man, null);
/*      */         
/*  390 */         System.setSecurityManager(this.my_sec_man);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  394 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void stopThread(Thread t)
/*      */   {
/*  403 */     synchronized (this.stoppable_threads)
/*      */     {
/*  405 */       this.stoppable_threads.add(Thread.currentThread());
/*      */     }
/*      */     
/*      */     try
/*      */     {
/*  410 */       t.stop();
/*      */     }
/*      */     finally
/*      */     {
/*  414 */       synchronized (this.stoppable_threads)
/*      */       {
/*  416 */         this.stoppable_threads.remove(Thread.currentThread());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void exitVM(int status)
/*      */   {
/*      */     try
/*      */     {
/*  426 */       this.exit_vm_permitted = true;
/*      */       try
/*      */       {
/*  429 */         System.exit(status);
/*      */ 
/*      */       }
/*      */       catch (Throwable t) {}
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*  437 */       this.exit_vm_permitted = false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void installAuthenticator()
/*      */   {
/*  444 */     Authenticator.setDefault(new Authenticator()
/*      */     {
/*      */ 
/*  447 */       protected final AEMonitor auth_mon = new AEMonitor("SESecurityManager:auth");
/*      */       
/*      */ 
/*      */       protected PasswordAuthentication getPasswordAuthentication()
/*      */       {
/*      */         try
/*      */         {
/*  454 */           this.auth_mon.enter();
/*      */           
/*  456 */           PasswordAuthentication res = SESecurityManagerImpl.this.getAuthentication(getRequestingPrompt(), getRequestingProtocol(), getRequestingHost(), getRequestingPort());
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
/*  472 */           return res;
/*      */         }
/*      */         finally
/*      */         {
/*  476 */           this.auth_mon.exit();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PasswordAuthentication getAuthentication(String realm, String protocol, String host, int port)
/*      */   {
/*      */     try
/*      */     {
/*  490 */       URL tracker_url = new URL(protocol + "://" + host + ":" + port + "/");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  496 */       if (protocol.toLowerCase().startsWith("socks"))
/*      */       {
/*      */ 
/*      */ 
/*  500 */         SEPasswordListener thread_listener = (SEPasswordListener)tls.get();
/*      */         
/*  502 */         if (thread_listener != null)
/*      */         {
/*  504 */           PasswordAuthentication temp = thread_listener.getAuthentication(realm, tracker_url);
/*      */           
/*  506 */           if (temp != null)
/*      */           {
/*  508 */             return temp;
/*      */           }
/*      */         }
/*      */         
/*  512 */         String socks_user = COConfigurationManager.getStringParameter("Proxy.Username").trim();
/*  513 */         String socks_pw = COConfigurationManager.getStringParameter("Proxy.Password").trim();
/*      */         
/*  515 */         if (socks_user.equalsIgnoreCase("<none>"))
/*      */         {
/*  517 */           return new PasswordAuthentication("", "".toCharArray());
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  524 */         if (socks_user.length() == 0)
/*      */         {
/*  526 */           Logger.log(new LogAlert(false, 1, "Socks server is requesting authentication, please setup user and password in config"));
/*      */         }
/*      */         
/*      */ 
/*  530 */         return new PasswordAuthentication(socks_user, socks_pw.toCharArray());
/*      */       }
/*      */       
/*  533 */       return getPasswordAuthentication(realm, tracker_url);
/*      */     }
/*      */     catch (MalformedURLException e)
/*      */     {
/*  537 */       Debug.printStackTrace(e);
/*      */     }
/*  539 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean checkKeyStoreHasEntry()
/*      */   {
/*  546 */     File f = new File(this.keystore_name);
/*      */     
/*  548 */     if (!f.exists()) {
/*  549 */       Logger.logTextResource(new LogAlert(false, 3, "Security.keystore.empty"), new String[] { this.keystore_name });
/*      */       
/*      */ 
/*      */ 
/*  553 */       return false;
/*      */     }
/*      */     try
/*      */     {
/*  557 */       KeyStore key_store = loadKeyStore();
/*      */       
/*  559 */       Enumeration enumx = key_store.aliases();
/*      */       
/*  561 */       if (!enumx.hasMoreElements()) {
/*  562 */         Logger.logTextResource(new LogAlert(false, 3, "Security.keystore.empty"), new String[] { this.keystore_name });
/*      */         
/*      */ 
/*      */ 
/*  566 */         return false;
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  571 */       Logger.logTextResource(new LogAlert(false, 3, "Security.keystore.corrupt"), new String[] { this.keystore_name });
/*      */       
/*      */ 
/*      */ 
/*  575 */       return false;
/*      */     }
/*      */     
/*  578 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean ensureStoreExists(String name)
/*      */   {
/*      */     try
/*      */     {
/*  586 */       this.this_mon.enter();
/*      */       
/*  588 */       KeyStore keystore = KeyStore.getInstance(KEYSTORE_TYPE);
/*      */       
/*  590 */       if (!new File(name).exists())
/*      */       {
/*  592 */         keystore.load(null, null);
/*      */         
/*  594 */         out = null;
/*      */         try
/*      */         {
/*  597 */           out = new FileOutputStream(name);
/*      */           
/*  599 */           keystore.store(out, "changeit".toCharArray());
/*      */         }
/*      */         finally
/*      */         {
/*  603 */           if (out != null)
/*      */           {
/*  605 */             out.close();
/*      */           }
/*      */         }
/*      */         
/*  609 */         return true;
/*      */       }
/*      */       
/*      */ 
/*  613 */       return 0;
/*      */     }
/*      */     catch (Throwable e) {
/*      */       FileOutputStream out;
/*  617 */       Debug.printStackTrace(e);
/*      */       
/*  619 */       return 0;
/*      */     }
/*      */     finally
/*      */     {
/*  623 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public KeyStore getKeyStore()
/*      */     throws Exception
/*      */   {
/*  632 */     return loadKeyStore();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public KeyStore getTrustStore()
/*      */     throws Exception
/*      */   {
/*  640 */     return getTrustStore(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public KeyStore getTrustStore(boolean attempt_recovery)
/*      */     throws Exception
/*      */   {
/*  649 */     KeyStore keystore = KeyStore.getInstance(KEYSTORE_TYPE);
/*      */     
/*  651 */     File tf_file = new File(this.truststore_name);
/*      */     try
/*      */     {
/*  654 */       if (!tf_file.exists())
/*      */       {
/*  656 */         keystore.load(null, null);
/*      */       }
/*      */       else
/*      */       {
/*  660 */         FileInputStream in = null;
/*      */         try
/*      */         {
/*  663 */           in = new FileInputStream(tf_file);
/*      */           
/*  665 */           keystore.load(in, "changeit".toCharArray());
/*      */         }
/*      */         finally
/*      */         {
/*  669 */           if (in != null)
/*      */           {
/*  671 */             in.close();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  677 */       if (attempt_recovery)
/*      */       {
/*  679 */         Debug.out("Failed to load trust store - resetting", e);
/*      */         try
/*      */         {
/*  682 */           if (tf_file.exists())
/*      */           {
/*  684 */             File bad_file = new File(tf_file.getAbsolutePath() + ".bad");
/*      */             
/*  686 */             bad_file.delete();
/*      */             
/*  688 */             tf_file.renameTo(bad_file);
/*      */           }
/*      */         }
/*      */         catch (Throwable f) {
/*  692 */           Debug.out(f);
/*      */         }
/*      */         
/*  695 */         resetTrustStore(false, true);
/*      */         
/*  697 */         return getTrustStore(false);
/*      */       }
/*      */       
/*      */ 
/*  701 */       if ((e instanceof Exception))
/*      */       {
/*  703 */         throw ((Exception)e);
/*      */       }
/*      */       
/*      */ 
/*  707 */       throw new Exception(e);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  712 */     return keystore;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected KeyStore loadKeyStore()
/*      */     throws Exception
/*      */   {
/*  720 */     KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
/*      */     
/*  722 */     return loadKeyStore(keyManagerFactory);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected KeyStore loadKeyStore(KeyManagerFactory keyManagerFactory)
/*      */     throws Exception
/*      */   {
/*  731 */     KeyStore key_store = KeyStore.getInstance(KEYSTORE_TYPE);
/*      */     
/*  733 */     if (!new File(this.keystore_name).exists())
/*      */     {
/*  735 */       key_store.load(null, null);
/*      */     }
/*      */     else
/*      */     {
/*  739 */       InputStream kis = null;
/*      */       try
/*      */       {
/*  742 */         kis = new FileInputStream(this.keystore_name);
/*      */         
/*  744 */         key_store.load(kis, "changeit".toCharArray());
/*      */       }
/*      */       finally
/*      */       {
/*  748 */         if (kis != null)
/*      */         {
/*  750 */           kis.close();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  755 */     keyManagerFactory.init(key_store, "changeit".toCharArray());
/*      */     
/*  757 */     return key_store;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public SSLServerSocketFactory getSSLServerSocketFactory()
/*      */     throws Exception
/*      */   {
/*  765 */     if (!checkKeyStoreHasEntry())
/*      */     {
/*  767 */       return null;
/*      */     }
/*      */     
/*  770 */     SSLContext context = SSLContext.getInstance("SSL");
/*      */     
/*      */ 
/*      */ 
/*  774 */     KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
/*      */     
/*  776 */     loadKeyStore(keyManagerFactory);
/*      */     
/*      */ 
/*      */ 
/*  780 */     context.init(keyManagerFactory.getKeyManagers(), null, RandomUtils.SECURE_RANDOM);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  785 */     SSLServerSocketFactory factory = context.getServerSocketFactory();
/*      */     
/*  787 */     return factory;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SEKeyDetails getKeyDetails(String alias)
/*      */     throws Exception
/*      */   {
/*  798 */     KeyStore key_store = loadKeyStore();
/*      */     
/*  800 */     final Key key = key_store.getKey(alias, "changeit".toCharArray());
/*      */     
/*  802 */     if (key == null)
/*      */     {
/*  804 */       return null;
/*      */     }
/*      */     
/*  807 */     Certificate[] chain = key_store.getCertificateChain(alias);
/*      */     
/*  809 */     final X509Certificate[] res = new X509Certificate[chain.length];
/*      */     
/*  811 */     for (int i = 0; i < chain.length; i++)
/*      */     {
/*  813 */       if (!(chain[i] instanceof X509Certificate))
/*      */       {
/*  815 */         throw new Exception("Certificate chain must be comprised of X509Certificate entries");
/*      */       }
/*      */       
/*  818 */       res[i] = ((X509Certificate)chain[i]);
/*      */     }
/*      */     
/*  821 */     new SEKeyDetails()
/*      */     {
/*      */ 
/*      */       public Key getKey()
/*      */       {
/*  826 */         return key;
/*      */       }
/*      */       
/*      */ 
/*      */       public X509Certificate[] getCertificateChain()
/*      */       {
/*  832 */         return res;
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Certificate createSelfSignedCertificate(String alias, String cert_dn, int strength)
/*      */     throws Exception
/*      */   {
/*  845 */     return SESecurityManagerBC.createSelfSignedCertificate(this, alias, cert_dn, strength);
/*      */   }
/*      */   
/*      */   public TrustManagerFactory getTrustManagerFactory()
/*      */   {
/*      */     try
/*      */     {
/*  852 */       this.this_mon.enter();
/*      */       
/*  854 */       KeyStore keystore = getTrustStore();
/*      */       
/*  856 */       tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
/*      */       
/*  858 */       tmf.init(keystore);
/*      */       
/*  860 */       return tmf;
/*      */     }
/*      */     catch (Throwable e) {
/*      */       TrustManagerFactory tmf;
/*  864 */       Debug.out(e);
/*      */       
/*  866 */       return null;
/*      */     }
/*      */     finally
/*      */     {
/*  870 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public SSLSocketFactory getSSLSocketFactory()
/*      */   {
/*      */     try
/*      */     {
/*  878 */       this.this_mon.enter();
/*      */       
/*  880 */       KeyStore keystore = getTrustStore();
/*      */       
/*  882 */       tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
/*      */       
/*  884 */       tmf.init(keystore);
/*      */       
/*  886 */       SSLContext ctx = SSLContext.getInstance("SSL");
/*      */       
/*  888 */       ctx.init(null, tmf.getTrustManagers(), null);
/*      */       
/*  890 */       SSLSocketFactory factory = ctx.getSocketFactory();
/*      */       
/*  892 */       return factory;
/*      */     }
/*      */     catch (Throwable e) {
/*      */       TrustManagerFactory tmf;
/*  896 */       Debug.printStackTrace(e);
/*      */       
/*  898 */       return (SSLSocketFactory)SSLSocketFactory.getDefault();
/*      */     }
/*      */     finally
/*      */     {
/*  902 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public SSLSocketFactory installServerCertificates(URL https_url)
/*      */   {
/*  910 */     return installServerCertificates(https_url, false, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TrustManager[] getAllTrustingTrustManager()
/*      */   {
/*  919 */     return getAllTrustingTrustManager(null);
/*      */   }
/*      */   
/*      */ 
/*      */   public TrustManager[] getAllTrustingTrustManager(final X509TrustManager delegate)
/*      */   {
/*      */     try
/*      */     {
/*  927 */       this.this_mon.enter();
/*      */       
/*  929 */       TrustManager[] all_trusting_manager = null;
/*      */       
/*  931 */       if (!this.hack_constructor_tried)
/*      */       {
/*  933 */         this.hack_constructor_tried = true;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/*  940 */           byte[] bytes = Base32.decode("ZL7LVPQAAAADGABRA4AAEAIAG5XXEZZPM52WI6JPMF5HK4TFOVZTEL3DN5ZGKMZPONSWG5LSNF2HSL3JNVYGYL2TIVKHE5LTORUW4Z2NMFXGCZ3FOIDQABABAATGUYLWMF4C63TFOQXXG43MF5MDKMBZIV4HIZLOMRSWIVDSOVZXITLBNZQWOZLSAEAAQZDFNRSWOYLUMUAQAICMNJQXMYLYF5XGK5BPONZWYL2YGUYDSVDSOVZXITLBNZQWOZLSHMAQABR4NFXGS5B6AEACGKCMNJQXMYLYF5XGK5BPONZWYL2YGUYDSVDSOVZXITLBNZQWOZLSHMUVMAIAARBW6ZDFBIAAGAALBQAAOAAMAEAAGKBJKYEQAAIABYGAABIAAYAQAD2MNFXGKTTVNVRGK4SUMFRGYZIBAAJEY33DMFWFMYLSNFQWE3DFKRQWE3DFAEAAI5DINFZQCABZJRXXEZZPM52WI6JPMF5HK4TFOVZTEL3DN5ZGKMZPONSWG5LSNF2HSL3JNVYGYL2TIVKHE5LTORUW4Z2NMFXGCZ3FOI5QCAAJL5SGK3DFM5QXIZIBAAJGG2DFMNVUG3DJMVXHIVDSOVZXIZLEAEADUKC3JRVGC5TBF5ZWKY3VOJUXI6JPMNSXE5BPLA2TAOKDMVZHI2LGNFRWC5DFHNGGUYLWMEXWYYLOM4XVG5DSNFXGOOZJKYAQACSFPBRWK4DUNFXW44YHAAMACABHNJQXMYJPONSWG5LSNF2HSL3DMVZHIL2DMVZHI2LGNFRWC5DFIV4GGZLQORUW63QLAANAAHAHAANQCAA6NJQXMYLYF5XGK5BPONZWYL2YGUYDSVDSOVZXITLBNZQWOZLSBQABIAAVAEAAKY3IMFUW4AIAEVNUY2TBOZQS643FMN2XE2LUPEXWGZLSOQXVQNJQHFBWK4TUNFTGSY3BORSTWAIABBQXK5DIKR4XAZIBAAJEY2TBOZQS63DBNZTS6U3UOJUW4ZZ3AEAA2U3UMFRWWTLBOBKGCYTMMUAQASZILNGGUYLWMEXXGZLDOVZGS5DZF5RWK4TUF5MDKMBZINSXE5DJMZUWGYLUMU5UY2TBOZQS63DBNZTS6U3UOJUW4ZZ3JRVGC5TBF5XGK5BPKNXWG23FOQ5SSVQBAADHG33DNNSXIAIACFGGUYLWMEXW4ZLUF5JW6Y3LMV2DWAIAKMUFWTDKMF3GCL3TMVRXK4TJOR4S6Y3FOJ2C6WBVGA4UGZLSORUWM2LDMF2GKO2MNJQXMYJPNRQW4ZZPKN2HE2LOM45UY2TBOZQXQL3OMV2C643TNQXVGU2MIVXGO2LOMU5SSVQBAADGK3THNFXGKAIADFGGUYLWMF4C63TFOQXXG43MF5JVGTCFNZTWS3TFHMAQAETDNBSWG22TMVZHMZLSKRZHK43UMVSAWAA2AAVAYABIAAKQCAASM5SXIQLDMNSXA5DFMREXG43VMVZHGAIAE4UCSW2MNJQXMYJPONSWG5LSNF2HSL3DMVZHIL2YGUYDSQ3FOJ2GSZTJMNQXIZJ3BMABUABOBQACWABMAEAAUU3POVZGGZKGNFWGKAIACZJUKVDSOVZXI2LOM5GWC3TBM5SXELTKMF3GCABBAAAQAAYAAAAACAACAACQABQAAAAAQAABAADQACAAAEAASAAAABDAAAQAAIAAAAAKFK3QACRKFO2QADNRAAAAAAQAB4AAAAAOAABQAAAAFAAAIABLAAEQALAACAAAAAAWAABAAAAABIABCAASAAAAAAAABIABGAAGAAAQAAIACQABKAACAALAAAAAAQAACAAXAAEQAAAAMIAAGAADAAAAAEZKWQAA3RQABYVLIAANFMWLSAAZAMALCAAAAABQADYAAAAA4AADAAAAAMIAA4ADEAASAA2AAEAAAAACAAADAAAAAEYACEABEAAAAAAAAEYADUAB4AABAAAAAEYAD4ACAAACAAQQAAAAAMAACEQAAEABIABCAABAAFQAAAAAIAABAALQACIAAAAGYAADAACAAAAACMVLIAANYYAA4KVUAAGSWLFZAAMQGAFRAAAAAAYAB4AAAAAOAABQAAAAHAAAOABZAAJAAOYACAAAAABKAACAAAAACMABCAASAAAAAAAACMAB2AA6AAAQAAAACMAB6ABAAABAAAAACMACGABEAABQAIIAAAAAGAABCIAACAAUAASQAAQACYAAAAAEAAAQAFYABEAAAADMAABQABAAAAABGKVUAAG4MAAOFK2AADJLFS4QAGIDACYQAAAAAMAA6AAAAAHAAAYAAAAD6AAHABAAAEQAIIABAAAAAAVAABAAAAABGAARAAJAAAAAAAABGAA5AAPAAAIAAAABGAA7AAQAAAQAAAABGABGAATQAAYAEEAAAAADAAAREAABAAUAAFIAAIABMAAAAACAAAIAC4AASAAAABRAAAYAAMAAAAATFK2AADOGAAHCVNAABUVSZOIAFEBQBMIAAAAAGAAPAAAAADQAAMAAAACGAADQARYACIAESAAQAAAAAIAAAMAAAAATAAIQAEQAAAAAAAATAAOQAHQAAEAAAAATAAPQAIAAAIACCAAAAABQAAISAAAQAKAAEIAAEAAWAAAAABAAAEABOAAJAAAAA3AAAMAAIAAAAAJSVNAABXDAADRKWQAA2KZMXEACSAYAWEAAAAADAAHQAAAABYAAGAAAABGQABYAJYABEACQAAIAAAAAFIAAIAAAAAJQAEIACIAAAAAAAAJQAHIADYAACAAAAAJQAHYAEAAAEAAAAAJQAIYAEQAAGABBAAAAAAYAAEJAAAIAFAACKAACAALAAAAAAQAACAAXAAEQAAAANQAAGAAEAAAAAEZKWQAA3RQABYVLIAANFMWLSABJAMALCAAAAABQADYAAAAA4AADAAAAAVAAA4AFKAASABLQAEAAAAACUAAEAAAAAEYACEABEAAAAAAAAEYADUAB4AABAAAAAEYAD4ACAAACAAAAAEYAEYACOAADAAQQAAAAAMAACEQAAEACWABMAAAQACIAAAAE4AABAAAQAAAACMVLIAANYYAA2KVUAAG3SABNAEALAANQAAAAAAYAB4AAAAAOAABQAAAALIAAOAC3AAIQAXIACAAAAAAMAAAQAAAACMABCAASAAAAAIIAAAAAGAABCEAACABPAAAAAAQAGA");
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
/*  958 */           Class<TrustManager> cla = new ClassLoader()
/*      */           {
/*      */             public Class<TrustManager> loadClass(String name, byte[] bytes)
/*      */             {
/*  950 */               Class<TrustManager> cla = defineClass(name, bytes, 0, bytes.length);
/*      */               
/*  952 */               resolveClass(cla);
/*      */               
/*  954 */               return cla;
/*      */             }
/*      */             
/*      */ 
/*  958 */           }.loadClass("org.gudy.azureus2.core3.security.impl.SETrustingManager", bytes);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  963 */           this.hack_constructor = cla.getConstructor(new Class[] { X509TrustManager.class });
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/*  969 */       if (this.hack_constructor != null) {
/*      */         try
/*      */         {
/*  972 */           all_trusting_manager = new TrustManager[] { (TrustManager)this.hack_constructor.newInstance(new Object[] { delegate }) };
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/*  978 */       if (all_trusting_manager == null)
/*      */       {
/*  980 */         all_trusting_manager = new TrustManager[] { new X509TrustManager()
/*      */         {
/*      */           public X509Certificate[] getAcceptedIssuers() {
/*  983 */             if (delegate != null) {
/*  984 */               return delegate.getAcceptedIssuers();
/*      */             }
/*  986 */             return null;
/*      */           }
/*      */           
/*      */           public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
/*  990 */             if (delegate != null) {
/*  991 */               delegate.checkClientTrusted(chain, authType);
/*      */             }
/*      */           }
/*      */           
/*      */           public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
/*  996 */             if (delegate != null) {
/*  997 */               delegate.checkServerTrusted(chain, authType);
/*      */             }
/*      */           }
/*      */         } };
/*      */       }
/*      */       
/*      */ 
/* 1004 */       return all_trusting_manager;
/*      */     }
/*      */     finally
/*      */     {
/* 1008 */       this.this_mon.exit();
/*      */     }
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
/*      */   public SSLSocketFactory installServerCertificates(String alias, String host, int port)
/*      */   {
/* 1421 */     return installServerCertificates(alias, host, port, false);
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
/*      */   protected void addCertToKeyStore(String alias, Key public_key, Certificate[] certChain)
/*      */     throws Exception
/*      */   {
/*      */     try
/*      */     {
/* 1554 */       this.this_mon.enter();
/*      */       
/* 1556 */       KeyStore key_store = loadKeyStore();
/*      */       
/* 1558 */       if (key_store.containsAlias(alias))
/*      */       {
/* 1560 */         key_store.deleteEntry(alias);
/*      */       }
/*      */       
/* 1563 */       key_store.setKeyEntry(alias, public_key, "changeit".toCharArray(), certChain);
/*      */       
/* 1565 */       FileOutputStream out = null;
/*      */       try
/*      */       {
/* 1568 */         out = new FileOutputStream(this.keystore_name);
/*      */         
/* 1570 */         key_store.store(out, "changeit".toCharArray());
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1574 */         Debug.printStackTrace(e);
/*      */       }
/*      */       finally
/*      */       {
/* 1578 */         if (out != null)
/*      */         {
/* 1580 */           out.close();
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1585 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SSLSocketFactory addCertToTrustStore(String alias, Certificate cert, boolean update_https_factory)
/*      */     throws Exception
/*      */   {
/*      */     try
/*      */     {
/* 1598 */       this.this_mon.enter();
/*      */       
/* 1600 */       KeyStore keystore = getTrustStore();
/*      */       
/* 1602 */       if (cert != null)
/*      */       {
/* 1604 */         if (keystore.containsAlias(alias))
/*      */         {
/* 1606 */           keystore.deleteEntry(alias);
/*      */         }
/*      */         
/* 1609 */         keystore.setCertificateEntry(alias, cert);
/*      */         
/* 1611 */         FileOutputStream out = null;
/*      */         try
/*      */         {
/* 1614 */           out = new FileOutputStream(this.truststore_name);
/*      */           
/* 1616 */           keystore.store(out, "changeit".toCharArray());
/*      */         }
/*      */         finally
/*      */         {
/* 1620 */           if (out != null)
/*      */           {
/* 1622 */             out.close();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1629 */       TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
/*      */       
/* 1631 */       tmf.init(keystore);
/*      */       
/* 1633 */       SSLContext ctx = SSLContext.getInstance("SSL");
/*      */       
/* 1635 */       ctx.init(null, tmf.getTrustManagers(), null);
/*      */       
/* 1637 */       SSLSocketFactory factory = ctx.getSocketFactory();
/*      */       
/* 1639 */       if (update_https_factory)
/*      */       {
/* 1641 */         HttpsURLConnection.setDefaultSSLSocketFactory(factory);
/*      */       }
/*      */       
/* 1644 */       return factory;
/*      */     }
/*      */     finally {
/* 1647 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public PasswordAuthentication getPasswordAuthentication(String realm, URL tracker)
/*      */   {
/* 1656 */     SEPasswordListener thread_listener = (SEPasswordListener)tls.get();
/*      */     
/* 1658 */     if (thread_listener != null)
/*      */     {
/* 1660 */       return thread_listener.getAuthentication(realm, tracker);
/*      */     }
/*      */     
/* 1663 */     Object[] handler = (Object[])this.password_handlers.get(tracker.toString());
/*      */     
/* 1665 */     if (handler != null) {
/*      */       try
/*      */       {
/* 1668 */         return ((SEPasswordListener)handler[0]).getAuthentication(realm, (URL)handler[1]);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1672 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 1676 */     Iterator it = this.password_listeners.iterator();
/*      */     
/* 1678 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/* 1681 */         PasswordAuthentication res = ((SEPasswordListener)it.next()).getAuthentication(realm, tracker);
/*      */         
/* 1683 */         if (res != null)
/*      */         {
/* 1685 */           return res;
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1689 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 1693 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPasswordAuthenticationOutcome(String realm, URL tracker, boolean success)
/*      */   {
/* 1702 */     SEPasswordListener thread_listener = (SEPasswordListener)tls.get();
/*      */     
/* 1704 */     if (thread_listener != null)
/*      */     {
/* 1706 */       thread_listener.setAuthenticationOutcome(realm, tracker, success);
/*      */     }
/*      */     
/* 1709 */     Iterator it = this.password_listeners.iterator();
/*      */     
/* 1711 */     while (it.hasNext())
/*      */     {
/* 1713 */       ((SEPasswordListener)it.next()).setAuthenticationOutcome(realm, tracker, success);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void addPasswordListener(SEPasswordListener l)
/*      */   {
/*      */     try
/*      */     {
/* 1722 */       this.this_mon.enter();
/*      */       
/* 1724 */       this.password_listeners.add(l);
/*      */     }
/*      */     finally
/*      */     {
/* 1728 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removePasswordListener(SEPasswordListener l)
/*      */   {
/*      */     try
/*      */     {
/* 1737 */       this.this_mon.enter();
/*      */       
/* 1739 */       this.password_listeners.remove(l);
/*      */     }
/*      */     finally
/*      */     {
/* 1743 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void clearPasswords()
/*      */   {
/* 1750 */     SEPasswordListener thread_listener = (SEPasswordListener)tls.get();
/*      */     
/* 1752 */     if (thread_listener != null)
/*      */     {
/* 1754 */       thread_listener.clearPasswords();
/*      */     }
/*      */     
/* 1757 */     Iterator it = this.password_listeners.iterator();
/*      */     
/* 1759 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/* 1762 */         ((SEPasswordListener)it.next()).clearPasswords();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1766 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setThreadPasswordHandler(SEPasswordListener l)
/*      */   {
/* 1775 */     tls.set(l);
/*      */   }
/*      */   
/*      */ 
/*      */   public void unsetThreadPasswordHandler()
/*      */   {
/* 1781 */     tls.set(null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPasswordHandler(URL url, SEPasswordListener l)
/*      */   {
/* 1789 */     String url_s = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + "/";
/*      */     
/* 1791 */     if (l == null)
/*      */     {
/* 1793 */       this.password_handlers.remove(url_s);
/*      */     }
/*      */     else
/*      */     {
/* 1797 */       this.password_handlers.put(url_s, new Object[] { l, url });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void addCertificateListener(SECertificateListener l)
/*      */   {
/*      */     try
/*      */     {
/* 1806 */       this.this_mon.enter();
/*      */       
/* 1808 */       this.certificate_listeners.add(l);
/*      */     }
/*      */     finally
/*      */     {
/* 1812 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setCertificateHandler(URL url, SECertificateListener l)
/*      */   {
/* 1821 */     String url_s = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + "/";
/*      */     
/* 1823 */     if (l == null)
/*      */     {
/* 1825 */       this.certificate_handlers.remove(url_s);
/*      */     }
/*      */     else
/*      */     {
/* 1829 */       this.certificate_handlers.put(url_s, new Object[] { l, url });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeCertificateListener(SECertificateListener l)
/*      */   {
/*      */     try
/*      */     {
/* 1838 */       this.this_mon.enter();
/*      */       
/* 1840 */       this.certificate_listeners.remove(l);
/*      */     }
/*      */     finally
/*      */     {
/* 1844 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Class[] getClassContext()
/*      */   {
/* 1851 */     if (this.my_sec_man == null)
/*      */     {
/* 1853 */       return new Class[0];
/*      */     }
/*      */     
/* 1856 */     return this.my_sec_man.getClassContext();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private final class AzureusSecurityManager
/*      */     extends SecurityManager
/*      */   {
/*      */     private final SecurityManager old_sec_man;
/*      */     
/*      */ 
/*      */     private AzureusSecurityManager(SecurityManager _old_sec_man)
/*      */     {
/* 1869 */       this.old_sec_man = _old_sec_man;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void checkAccept(String host, int port) {}
/*      */     
/*      */ 
/*      */ 
/*      */     public void checkRead(String file) {}
/*      */     
/*      */ 
/*      */ 
/*      */     public void checkWrite(String file) {}
/*      */     
/*      */ 
/*      */     public void checkConnect(String host, int port) {}
/*      */     
/*      */ 
/*      */     public void checkExit(int status)
/*      */     {
/* 1890 */       if (this.old_sec_man != null)
/*      */       {
/* 1892 */         this.old_sec_man.checkExit(status);
/*      */       }
/*      */       
/* 1895 */       if (!SESecurityManagerImpl.this.exit_vm_permitted)
/*      */       {
/* 1897 */         String prop = System.getProperty("azureus.security.manager.permitexit", "0");
/*      */         
/* 1899 */         if (prop.equals("0"))
/*      */         {
/* 1901 */           throw new SecurityException("VM exit operation prohibited");
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void checkPermission(Permission perm)
/*      */     {
/* 1910 */       checkPermission(perm, null);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void checkPermission(Permission perm, Object context)
/*      */     {
/* 1918 */       if ((perm instanceof RuntimePermission))
/*      */       {
/* 1920 */         String name = perm.getName();
/*      */         
/* 1922 */         if (name.equals("stopThread"))
/*      */         {
/* 1924 */           synchronized (SESecurityManagerImpl.this.stoppable_threads)
/*      */           {
/* 1926 */             if (SESecurityManagerImpl.this.stoppable_threads.contains(Thread.currentThread()))
/*      */             {
/* 1928 */               return;
/*      */             }
/*      */           }
/*      */           
/* 1932 */           throw new SecurityException("Thread.stop operation prohibited");
/*      */         }
/* 1934 */         if (name.equals("setSecurityManager"))
/*      */         {
/* 1936 */           throw new SecurityException("Permission Denied");
/*      */         }
/*      */       }
/*      */       
/* 1940 */       if (this.old_sec_man != null)
/*      */       {
/* 1942 */         if (context == null)
/*      */         {
/* 1944 */           this.old_sec_man.checkPermission(perm);
/*      */         }
/*      */         else
/*      */         {
/* 1948 */           this.old_sec_man.checkPermission(perm, context);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public Class[] getClassContext()
/*      */     {
/* 1956 */       Class[] res = super.getClassContext();
/*      */       
/* 1958 */       if (res.length <= 3)
/*      */       {
/* 1960 */         return new Class[0];
/*      */       }
/*      */       
/* 1963 */       Class[] trimmed = new Class[res.length - 3];
/*      */       
/* 1965 */       System.arraycopy(res, 3, trimmed, 0, trimmed.length);
/*      */       
/* 1967 */       return trimmed;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/* 1976 */     SESecurityManagerImpl man = getSingleton();
/*      */     
/* 1978 */     man.initialise();
/*      */     try
/*      */     {
/* 1981 */       man.createSelfSignedCertificate("SomeAlias", "CN=fred,OU=wap,O=wip,L=here,ST=there,C=GB", 1000);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1985 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   private SSLSocketFactory installServerCertificates(URL https_url, boolean sni_hack, boolean dh_hack)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 890	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   4: invokevirtual 1047	org/gudy/azureus2/core3/util/AEMonitor:enter	()V
/*      */     //   7: aload_1
/*      */     //   8: invokevirtual 958	java/net/URL:getHost	()Ljava/lang/String;
/*      */     //   11: astore 4
/*      */     //   13: aload_1
/*      */     //   14: invokevirtual 957	java/net/URL:getPort	()I
/*      */     //   17: istore 5
/*      */     //   19: iload 5
/*      */     //   21: iconst_m1
/*      */     //   22: if_icmpne +8 -> 30
/*      */     //   25: sipush 443
/*      */     //   28: istore 5
/*      */     //   30: aconst_null
/*      */     //   31: astore 6
/*      */     //   33: aload_0
/*      */     //   34: invokevirtual 1028	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:getTrustManagerFactory	()Ljavax/net/ssl/TrustManagerFactory;
/*      */     //   37: astore 7
/*      */     //   39: new 495	java/util/ArrayList
/*      */     //   42: dup
/*      */     //   43: invokespecial 979	java/util/ArrayList:<init>	()V
/*      */     //   46: astore 8
/*      */     //   48: aload 7
/*      */     //   50: ifnull +59 -> 109
/*      */     //   53: aload 7
/*      */     //   55: invokevirtual 1003	javax/net/ssl/TrustManagerFactory:getTrustManagers	()[Ljavax/net/ssl/TrustManager;
/*      */     //   58: astore 9
/*      */     //   60: aload 9
/*      */     //   62: arraylength
/*      */     //   63: istore 10
/*      */     //   65: iconst_0
/*      */     //   66: istore 11
/*      */     //   68: iload 11
/*      */     //   70: iload 10
/*      */     //   72: if_icmpge +37 -> 109
/*      */     //   75: aload 9
/*      */     //   77: iload 11
/*      */     //   79: aaload
/*      */     //   80: astore 12
/*      */     //   82: aload 12
/*      */     //   84: instanceof 510
/*      */     //   87: ifeq +16 -> 103
/*      */     //   90: aload 8
/*      */     //   92: aload 12
/*      */     //   94: checkcast 510	javax/net/ssl/X509TrustManager
/*      */     //   97: invokeinterface 1063 2 0
/*      */     //   102: pop
/*      */     //   103: iinc 11 1
/*      */     //   106: goto -38 -> 68
/*      */     //   109: new 495	java/util/ArrayList
/*      */     //   112: dup
/*      */     //   113: invokespecial 979	java/util/ArrayList:<init>	()V
/*      */     //   116: astore 9
/*      */     //   118: new 527	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl$6
/*      */     //   121: dup
/*      */     //   122: aload_0
/*      */     //   123: aload 8
/*      */     //   125: aload 9
/*      */     //   127: invokespecial 1044	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl$6:<init>	(Lorg/gudy/azureus2/core3/security/impl/SESecurityManagerImpl;Ljava/util/List;Ljava/util/List;)V
/*      */     //   130: invokestatic 1014	org/gudy/azureus2/core3/security/SESecurityManager:getAllTrustingTrustManager	(Ljavax/net/ssl/X509TrustManager;)[Ljavax/net/ssl/TrustManager;
/*      */     //   133: astore 10
/*      */     //   135: ldc 23
/*      */     //   137: invokestatic 988	javax/net/ssl/SSLContext:getInstance	(Ljava/lang/String;)Ljavax/net/ssl/SSLContext;
/*      */     //   140: astore 11
/*      */     //   142: aload 11
/*      */     //   144: aconst_null
/*      */     //   145: aload 10
/*      */     //   147: getstatic 892	org/gudy/azureus2/core3/util/RandomUtils:SECURE_RANDOM	Ljava/security/SecureRandom;
/*      */     //   150: invokevirtual 989	javax/net/ssl/SSLContext:init	([Ljavax/net/ssl/KeyManager;[Ljavax/net/ssl/TrustManager;Ljava/security/SecureRandom;)V
/*      */     //   153: aload 11
/*      */     //   155: invokevirtual 987	javax/net/ssl/SSLContext:getSocketFactory	()Ljavax/net/ssl/SSLSocketFactory;
/*      */     //   158: astore 12
/*      */     //   160: new 485	java/net/InetSocketAddress
/*      */     //   163: dup
/*      */     //   164: aload 4
/*      */     //   166: invokestatic 949	java/net/InetAddress:getByName	(Ljava/lang/String;)Ljava/net/InetAddress;
/*      */     //   169: iload 5
/*      */     //   171: invokespecial 951	java/net/InetSocketAddress:<init>	(Ljava/net/InetAddress;I)V
/*      */     //   174: astore 13
/*      */     //   176: invokestatic 893	com/aelitis/azureus/core/networkmanager/admin/NetworkAdmin:getSingleton	()Lcom/aelitis/azureus/core/networkmanager/admin/NetworkAdmin;
/*      */     //   179: aload 13
/*      */     //   181: invokevirtual 950	java/net/InetSocketAddress:getAddress	()Ljava/net/InetAddress;
/*      */     //   184: instanceof 483
/*      */     //   187: ifeq +7 -> 194
/*      */     //   190: iconst_2
/*      */     //   191: goto +4 -> 195
/*      */     //   194: iconst_1
/*      */     //   195: invokevirtual 894	com/aelitis/azureus/core/networkmanager/admin/NetworkAdmin:getSingleHomedServiceBindAddress	(I)Ljava/net/InetAddress;
/*      */     //   198: astore 14
/*      */     //   200: iload_2
/*      */     //   201: ifeq +82 -> 283
/*      */     //   204: new 488	java/net/Socket
/*      */     //   207: dup
/*      */     //   208: invokespecial 954	java/net/Socket:<init>	()V
/*      */     //   211: astore 15
/*      */     //   213: aload 14
/*      */     //   215: ifnull +18 -> 233
/*      */     //   218: aload 15
/*      */     //   220: new 485	java/net/InetSocketAddress
/*      */     //   223: dup
/*      */     //   224: aload 14
/*      */     //   226: iconst_0
/*      */     //   227: invokespecial 951	java/net/InetSocketAddress:<init>	(Ljava/net/InetAddress;I)V
/*      */     //   230: invokevirtual 955	java/net/Socket:bind	(Ljava/net/SocketAddress;)V
/*      */     //   233: aload 15
/*      */     //   235: aload 13
/*      */     //   237: invokevirtual 956	java/net/Socket:connect	(Ljava/net/SocketAddress;)V
/*      */     //   240: aload 12
/*      */     //   242: aload 15
/*      */     //   244: ldc 1
/*      */     //   246: aload 15
/*      */     //   248: invokevirtual 953	java/net/Socket:getPort	()I
/*      */     //   251: iconst_1
/*      */     //   252: invokevirtual 999	javax/net/ssl/SSLSocketFactory:createSocket	(Ljava/net/Socket;Ljava/lang/String;IZ)Ljava/net/Socket;
/*      */     //   255: checkcast 506	javax/net/ssl/SSLSocket
/*      */     //   258: astore 6
/*      */     //   260: aload 6
/*      */     //   262: iconst_1
/*      */     //   263: anewarray 475	java/lang/String
/*      */     //   266: dup
/*      */     //   267: iconst_0
/*      */     //   268: ldc 28
/*      */     //   270: aastore
/*      */     //   271: invokevirtual 995	javax/net/ssl/SSLSocket:setEnabledProtocols	([Ljava/lang/String;)V
/*      */     //   274: aload 6
/*      */     //   276: iconst_1
/*      */     //   277: invokevirtual 992	javax/net/ssl/SSLSocket:setUseClientMode	(Z)V
/*      */     //   280: goto +42 -> 322
/*      */     //   283: aload 14
/*      */     //   285: ifnull +23 -> 308
/*      */     //   288: aload 12
/*      */     //   290: aload 4
/*      */     //   292: iload 5
/*      */     //   294: aload 14
/*      */     //   296: iconst_0
/*      */     //   297: invokevirtual 1000	javax/net/ssl/SSLSocketFactory:createSocket	(Ljava/lang/String;ILjava/net/InetAddress;I)Ljava/net/Socket;
/*      */     //   300: checkcast 506	javax/net/ssl/SSLSocket
/*      */     //   303: astore 6
/*      */     //   305: goto +17 -> 322
/*      */     //   308: aload 12
/*      */     //   310: aload 4
/*      */     //   312: iload 5
/*      */     //   314: invokevirtual 998	javax/net/ssl/SSLSocketFactory:createSocket	(Ljava/lang/String;I)Ljava/net/Socket;
/*      */     //   317: checkcast 506	javax/net/ssl/SSLSocket
/*      */     //   320: astore 6
/*      */     //   322: iload_3
/*      */     //   323: ifeq +109 -> 432
/*      */     //   326: aload 6
/*      */     //   328: invokevirtual 993	javax/net/ssl/SSLSocket:getEnabledCipherSuites	()[Ljava/lang/String;
/*      */     //   331: astore 15
/*      */     //   333: new 495	java/util/ArrayList
/*      */     //   336: dup
/*      */     //   337: invokespecial 979	java/util/ArrayList:<init>	()V
/*      */     //   340: astore 16
/*      */     //   342: aload 15
/*      */     //   344: astore 17
/*      */     //   346: aload 17
/*      */     //   348: arraylength
/*      */     //   349: istore 18
/*      */     //   351: iconst_0
/*      */     //   352: istore 19
/*      */     //   354: iload 19
/*      */     //   356: iload 18
/*      */     //   358: if_icmpge +49 -> 407
/*      */     //   361: aload 17
/*      */     //   363: iload 19
/*      */     //   365: aaload
/*      */     //   366: astore 20
/*      */     //   368: aload 20
/*      */     //   370: ldc 32
/*      */     //   372: invokevirtual 922	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
/*      */     //   375: ifne +26 -> 401
/*      */     //   378: aload 20
/*      */     //   380: ldc 31
/*      */     //   382: invokevirtual 922	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
/*      */     //   385: ifeq +6 -> 391
/*      */     //   388: goto +13 -> 401
/*      */     //   391: aload 16
/*      */     //   393: aload 20
/*      */     //   395: invokeinterface 1063 2 0
/*      */     //   400: pop
/*      */     //   401: iinc 19 1
/*      */     //   404: goto -50 -> 354
/*      */     //   407: aload 6
/*      */     //   409: aload 16
/*      */     //   411: aload 16
/*      */     //   413: invokeinterface 1062 1 0
/*      */     //   418: anewarray 475	java/lang/String
/*      */     //   421: invokeinterface 1066 2 0
/*      */     //   426: checkcast 461	[Ljava/lang/String;
/*      */     //   429: invokevirtual 994	javax/net/ssl/SSLSocket:setEnabledCipherSuites	([Ljava/lang/String;)V
/*      */     //   432: aload 6
/*      */     //   434: invokevirtual 991	javax/net/ssl/SSLSocket:startHandshake	()V
/*      */     //   437: aload 6
/*      */     //   439: invokevirtual 996	javax/net/ssl/SSLSocket:getSession	()Ljavax/net/ssl/SSLSession;
/*      */     //   442: invokeinterface 1070 1 0
/*      */     //   447: astore 15
/*      */     //   449: aload 15
/*      */     //   451: arraylength
/*      */     //   452: anewarray 494	java/security/cert/X509Certificate
/*      */     //   455: astore 16
/*      */     //   457: iconst_0
/*      */     //   458: istore 17
/*      */     //   460: iload 17
/*      */     //   462: aload 15
/*      */     //   464: arraylength
/*      */     //   465: if_icmpge +70 -> 535
/*      */     //   468: aload 15
/*      */     //   470: iload 17
/*      */     //   472: aaload
/*      */     //   473: astore 18
/*      */     //   475: aload 18
/*      */     //   477: instanceof 494
/*      */     //   480: ifeq +13 -> 493
/*      */     //   483: aload 18
/*      */     //   485: checkcast 494	java/security/cert/X509Certificate
/*      */     //   488: astore 19
/*      */     //   490: goto +32 -> 522
/*      */     //   493: ldc 29
/*      */     //   495: invokestatic 976	java/security/cert/CertificateFactory:getInstance	(Ljava/lang/String;)Ljava/security/cert/CertificateFactory;
/*      */     //   498: astore 20
/*      */     //   500: aload 20
/*      */     //   502: new 467	java/io/ByteArrayInputStream
/*      */     //   505: dup
/*      */     //   506: aload 18
/*      */     //   508: invokevirtual 974	java/security/cert/Certificate:getEncoded	()[B
/*      */     //   511: invokespecial 899	java/io/ByteArrayInputStream:<init>	([B)V
/*      */     //   514: invokevirtual 975	java/security/cert/CertificateFactory:generateCertificate	(Ljava/io/InputStream;)Ljava/security/cert/Certificate;
/*      */     //   517: checkcast 494	java/security/cert/X509Certificate
/*      */     //   520: astore 19
/*      */     //   522: aload 16
/*      */     //   524: iload 17
/*      */     //   526: aload 19
/*      */     //   528: aastore
/*      */     //   529: iinc 17 1
/*      */     //   532: goto -72 -> 460
/*      */     //   535: iconst_0
/*      */     //   536: istore 17
/*      */     //   538: aload 9
/*      */     //   540: invokeinterface 1062 1 0
/*      */     //   545: ifle +106 -> 651
/*      */     //   548: aload 9
/*      */     //   550: invokeinterface 1065 1 0
/*      */     //   555: astore 18
/*      */     //   557: aload 18
/*      */     //   559: invokeinterface 1060 1 0
/*      */     //   564: ifeq +87 -> 651
/*      */     //   567: aload 18
/*      */     //   569: invokeinterface 1061 1 0
/*      */     //   574: astore 19
/*      */     //   576: aload 19
/*      */     //   578: checkcast 463	[Ljava/security/cert/X509Certificate;
/*      */     //   581: checkcast 463	[Ljava/security/cert/X509Certificate;
/*      */     //   584: astore 20
/*      */     //   586: aload 20
/*      */     //   588: arraylength
/*      */     //   589: aload 16
/*      */     //   591: arraylength
/*      */     //   592: if_icmpne +56 -> 648
/*      */     //   595: iconst_1
/*      */     //   596: istore 21
/*      */     //   598: iconst_0
/*      */     //   599: istore 22
/*      */     //   601: iload 22
/*      */     //   603: aload 20
/*      */     //   605: arraylength
/*      */     //   606: if_icmpge +31 -> 637
/*      */     //   609: aload 20
/*      */     //   611: iload 22
/*      */     //   613: aaload
/*      */     //   614: aload 16
/*      */     //   616: iload 22
/*      */     //   618: aaload
/*      */     //   619: invokevirtual 978	java/security/cert/X509Certificate:equals	(Ljava/lang/Object;)Z
/*      */     //   622: ifne +9 -> 631
/*      */     //   625: iconst_0
/*      */     //   626: istore 21
/*      */     //   628: goto +9 -> 637
/*      */     //   631: iinc 22 1
/*      */     //   634: goto -33 -> 601
/*      */     //   637: iload 21
/*      */     //   639: ifeq +9 -> 648
/*      */     //   642: iconst_1
/*      */     //   643: istore 17
/*      */     //   645: goto +6 -> 651
/*      */     //   648: goto -91 -> 557
/*      */     //   651: aconst_null
/*      */     //   652: astore 18
/*      */     //   654: iconst_0
/*      */     //   655: istore 19
/*      */     //   657: iload 19
/*      */     //   659: aload 15
/*      */     //   661: arraylength
/*      */     //   662: if_icmpge +423 -> 1085
/*      */     //   665: aload 15
/*      */     //   667: iload 19
/*      */     //   669: aaload
/*      */     //   670: astore 20
/*      */     //   672: aload 16
/*      */     //   674: iload 19
/*      */     //   676: aaload
/*      */     //   677: astore 21
/*      */     //   679: aload_1
/*      */     //   680: invokevirtual 960	java/net/URL:toString	()Ljava/lang/String;
/*      */     //   683: astore 22
/*      */     //   685: aload 22
/*      */     //   687: ldc 12
/*      */     //   689: invokevirtual 928	java/lang/String:indexOf	(Ljava/lang/String;)I
/*      */     //   692: istore 23
/*      */     //   694: iload 23
/*      */     //   696: iconst_m1
/*      */     //   697: if_icmpeq +13 -> 710
/*      */     //   700: aload 22
/*      */     //   702: iconst_0
/*      */     //   703: iload 23
/*      */     //   705: invokevirtual 927	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   708: astore 22
/*      */     //   710: new 476	java/lang/StringBuilder
/*      */     //   713: dup
/*      */     //   714: invokespecial 932	java/lang/StringBuilder:<init>	()V
/*      */     //   717: aload_1
/*      */     //   718: invokevirtual 959	java/net/URL:getProtocol	()Ljava/lang/String;
/*      */     //   721: invokevirtual 936	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   724: ldc 10
/*      */     //   726: invokevirtual 936	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   729: aload_1
/*      */     //   730: invokevirtual 958	java/net/URL:getHost	()Ljava/lang/String;
/*      */     //   733: invokevirtual 936	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   736: ldc 9
/*      */     //   738: invokevirtual 936	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   741: aload_1
/*      */     //   742: invokevirtual 957	java/net/URL:getPort	()I
/*      */     //   745: invokevirtual 934	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   748: ldc 6
/*      */     //   750: invokevirtual 936	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   753: invokevirtual 933	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   756: astore 24
/*      */     //   758: aload_0
/*      */     //   759: getfield 885	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:certificate_handlers	Ljava/util/Map;
/*      */     //   762: aload 24
/*      */     //   764: invokeinterface 1067 2 0
/*      */     //   769: checkcast 460	[Ljava/lang/Object;
/*      */     //   772: checkcast 460	[Ljava/lang/Object;
/*      */     //   775: astore 25
/*      */     //   777: aload 4
/*      */     //   779: ldc 9
/*      */     //   781: invokevirtual 931	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   784: iload 5
/*      */     //   786: invokestatic 926	java/lang/String:valueOf	(I)Ljava/lang/String;
/*      */     //   789: invokevirtual 931	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   792: astore 26
/*      */     //   794: iload 19
/*      */     //   796: ifle +37 -> 833
/*      */     //   799: new 476	java/lang/StringBuilder
/*      */     //   802: dup
/*      */     //   803: invokespecial 932	java/lang/StringBuilder:<init>	()V
/*      */     //   806: aload 26
/*      */     //   808: invokevirtual 936	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   811: ldc_w 455
/*      */     //   814: invokevirtual 936	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   817: iload 19
/*      */     //   819: invokevirtual 934	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   822: ldc_w 456
/*      */     //   825: invokevirtual 936	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   828: invokevirtual 933	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   831: astore 26
/*      */     //   833: aload_0
/*      */     //   834: invokevirtual 1025	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:getTrustStore	()Ljava/security/KeyStore;
/*      */     //   837: astore 27
/*      */     //   839: aload 21
/*      */     //   841: invokevirtual 977	java/security/cert/X509Certificate:getEncoded	()[B
/*      */     //   844: astore 28
/*      */     //   846: iconst_0
/*      */     //   847: istore 29
/*      */     //   849: iconst_0
/*      */     //   850: istore 30
/*      */     //   852: iload 29
/*      */     //   854: sipush 256
/*      */     //   857: if_icmpge +91 -> 948
/*      */     //   860: iload 29
/*      */     //   862: ifne +8 -> 870
/*      */     //   865: aload 26
/*      */     //   867: goto +29 -> 896
/*      */     //   870: new 476	java/lang/StringBuilder
/*      */     //   873: dup
/*      */     //   874: invokespecial 932	java/lang/StringBuilder:<init>	()V
/*      */     //   877: aload 26
/*      */     //   879: invokevirtual 936	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   882: ldc_w 447
/*      */     //   885: invokevirtual 936	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   888: iload 29
/*      */     //   890: invokevirtual 934	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   893: invokevirtual 933	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   896: astore 31
/*      */     //   898: aload 27
/*      */     //   900: aload 31
/*      */     //   902: invokevirtual 970	java/security/KeyStore:getCertificate	(Ljava/lang/String;)Ljava/security/cert/Certificate;
/*      */     //   905: astore 32
/*      */     //   907: aload 32
/*      */     //   909: ifnull +26 -> 935
/*      */     //   912: aload 28
/*      */     //   914: aload 32
/*      */     //   916: invokevirtual 974	java/security/cert/Certificate:getEncoded	()[B
/*      */     //   919: invokestatic 980	java/util/Arrays:equals	([B[B)Z
/*      */     //   922: ifeq +20 -> 942
/*      */     //   925: aload 31
/*      */     //   927: astore 26
/*      */     //   929: iconst_1
/*      */     //   930: istore 30
/*      */     //   932: goto +16 -> 948
/*      */     //   935: aload 31
/*      */     //   937: astore 26
/*      */     //   939: goto +9 -> 948
/*      */     //   942: iinc 29 1
/*      */     //   945: goto -93 -> 852
/*      */     //   948: getstatic 873	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:auto_install_certs	Z
/*      */     //   951: ifne +18 -> 969
/*      */     //   954: iload 17
/*      */     //   956: ifne +13 -> 969
/*      */     //   959: iload 30
/*      */     //   961: ifne +8 -> 969
/*      */     //   964: aload 18
/*      */     //   966: ifnull +17 -> 983
/*      */     //   969: aload_0
/*      */     //   970: aload 26
/*      */     //   972: aload 20
/*      */     //   974: iconst_1
/*      */     //   975: invokevirtual 1036	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:addCertToTrustStore	(Ljava/lang/String;Ljava/security/cert/Certificate;Z)Ljavax/net/ssl/SSLSocketFactory;
/*      */     //   978: astore 18
/*      */     //   980: goto +99 -> 1079
/*      */     //   983: aload 25
/*      */     //   985: ifnull +33 -> 1018
/*      */     //   988: aload 25
/*      */     //   990: iconst_0
/*      */     //   991: aaload
/*      */     //   992: checkcast 516	org/gudy/azureus2/core3/security/SECertificateListener
/*      */     //   995: aload 22
/*      */     //   997: aload 21
/*      */     //   999: invokeinterface 1071 3 0
/*      */     //   1004: ifeq +14 -> 1018
/*      */     //   1007: aload_0
/*      */     //   1008: aload 26
/*      */     //   1010: aload 20
/*      */     //   1012: iconst_1
/*      */     //   1013: invokevirtual 1036	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:addCertToTrustStore	(Ljava/lang/String;Ljava/security/cert/Certificate;Z)Ljavax/net/ssl/SSLSocketFactory;
/*      */     //   1016: astore 18
/*      */     //   1018: aload_0
/*      */     //   1019: getfield 883	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:certificate_listeners	Ljava/util/List;
/*      */     //   1022: invokeinterface 1065 1 0
/*      */     //   1027: astore 31
/*      */     //   1029: aload 31
/*      */     //   1031: invokeinterface 1060 1 0
/*      */     //   1036: ifeq +43 -> 1079
/*      */     //   1039: aload 31
/*      */     //   1041: invokeinterface 1061 1 0
/*      */     //   1046: checkcast 516	org/gudy/azureus2/core3/security/SECertificateListener
/*      */     //   1049: astore 32
/*      */     //   1051: aload 32
/*      */     //   1053: aload 22
/*      */     //   1055: aload 21
/*      */     //   1057: invokeinterface 1071 3 0
/*      */     //   1062: ifeq +14 -> 1076
/*      */     //   1065: aload_0
/*      */     //   1066: aload 26
/*      */     //   1068: aload 20
/*      */     //   1070: iconst_1
/*      */     //   1071: invokevirtual 1036	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:addCertToTrustStore	(Ljava/lang/String;Ljava/security/cert/Certificate;Z)Ljavax/net/ssl/SSLSocketFactory;
/*      */     //   1074: astore 18
/*      */     //   1076: goto -47 -> 1029
/*      */     //   1079: iinc 19 1
/*      */     //   1082: goto -425 -> 657
/*      */     //   1085: aload 18
/*      */     //   1087: astore 19
/*      */     //   1089: aload 6
/*      */     //   1091: ifnull +18 -> 1109
/*      */     //   1094: aload 6
/*      */     //   1096: invokevirtual 990	javax/net/ssl/SSLSocket:close	()V
/*      */     //   1099: goto +10 -> 1109
/*      */     //   1102: astore 20
/*      */     //   1104: aload 20
/*      */     //   1106: invokestatic 1054	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*      */     //   1109: aload_0
/*      */     //   1110: getfield 890	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   1113: invokevirtual 1048	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   1116: aload 19
/*      */     //   1118: areturn
/*      */     //   1119: astore 7
/*      */     //   1121: aload 7
/*      */     //   1123: invokestatic 1055	org/gudy/azureus2/core3/util/Debug:getNestedExceptionMessage	(Ljava/lang/Throwable;)Ljava/lang/String;
/*      */     //   1126: astore 8
/*      */     //   1128: aload 8
/*      */     //   1130: ldc_w 458
/*      */     //   1133: invokevirtual 922	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
/*      */     //   1136: ifeq +46 -> 1182
/*      */     //   1139: iload_2
/*      */     //   1140: ifne +42 -> 1182
/*      */     //   1143: aload_0
/*      */     //   1144: aload_1
/*      */     //   1145: iconst_1
/*      */     //   1146: iload_3
/*      */     //   1147: invokespecial 1031	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:installServerCertificates	(Ljava/net/URL;ZZ)Ljavax/net/ssl/SSLSocketFactory;
/*      */     //   1150: astore 9
/*      */     //   1152: aload 6
/*      */     //   1154: ifnull +18 -> 1172
/*      */     //   1157: aload 6
/*      */     //   1159: invokevirtual 990	javax/net/ssl/SSLSocket:close	()V
/*      */     //   1162: goto +10 -> 1172
/*      */     //   1165: astore 10
/*      */     //   1167: aload 10
/*      */     //   1169: invokestatic 1054	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*      */     //   1172: aload_0
/*      */     //   1173: getfield 890	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   1176: invokevirtual 1048	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   1179: aload 9
/*      */     //   1181: areturn
/*      */     //   1182: aload 8
/*      */     //   1184: ldc_w 450
/*      */     //   1187: invokevirtual 922	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
/*      */     //   1190: ifeq +46 -> 1236
/*      */     //   1193: iload_3
/*      */     //   1194: ifne +42 -> 1236
/*      */     //   1197: aload_0
/*      */     //   1198: aload_1
/*      */     //   1199: iload_2
/*      */     //   1200: iconst_1
/*      */     //   1201: invokespecial 1031	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:installServerCertificates	(Ljava/net/URL;ZZ)Ljavax/net/ssl/SSLSocketFactory;
/*      */     //   1204: astore 9
/*      */     //   1206: aload 6
/*      */     //   1208: ifnull +18 -> 1226
/*      */     //   1211: aload 6
/*      */     //   1213: invokevirtual 990	javax/net/ssl/SSLSocket:close	()V
/*      */     //   1216: goto +10 -> 1226
/*      */     //   1219: astore 10
/*      */     //   1221: aload 10
/*      */     //   1223: invokestatic 1054	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*      */     //   1226: aload_0
/*      */     //   1227: getfield 890	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   1230: invokevirtual 1048	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   1233: aload 9
/*      */     //   1235: areturn
/*      */     //   1236: aload 7
/*      */     //   1238: invokestatic 1053	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/Throwable;)V
/*      */     //   1241: aconst_null
/*      */     //   1242: astore 9
/*      */     //   1244: aload 6
/*      */     //   1246: ifnull +18 -> 1264
/*      */     //   1249: aload 6
/*      */     //   1251: invokevirtual 990	javax/net/ssl/SSLSocket:close	()V
/*      */     //   1254: goto +10 -> 1264
/*      */     //   1257: astore 10
/*      */     //   1259: aload 10
/*      */     //   1261: invokestatic 1054	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*      */     //   1264: aload_0
/*      */     //   1265: getfield 890	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   1268: invokevirtual 1048	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   1271: aload 9
/*      */     //   1273: areturn
/*      */     //   1274: astore 33
/*      */     //   1276: aload 6
/*      */     //   1278: ifnull +18 -> 1296
/*      */     //   1281: aload 6
/*      */     //   1283: invokevirtual 990	javax/net/ssl/SSLSocket:close	()V
/*      */     //   1286: goto +10 -> 1296
/*      */     //   1289: astore 34
/*      */     //   1291: aload 34
/*      */     //   1293: invokestatic 1054	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*      */     //   1296: aload 33
/*      */     //   1298: athrow
/*      */     //   1299: astore 35
/*      */     //   1301: aload_0
/*      */     //   1302: getfield 890	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   1305: invokevirtual 1048	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   1308: aload 35
/*      */     //   1310: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1094	-> byte code offset #0
/*      */     //   Java source line #1096	-> byte code offset #7
/*      */     //   Java source line #1097	-> byte code offset #13
/*      */     //   Java source line #1099	-> byte code offset #19
/*      */     //   Java source line #1101	-> byte code offset #25
/*      */     //   Java source line #1104	-> byte code offset #30
/*      */     //   Java source line #1110	-> byte code offset #33
/*      */     //   Java source line #1112	-> byte code offset #39
/*      */     //   Java source line #1114	-> byte code offset #48
/*      */     //   Java source line #1116	-> byte code offset #53
/*      */     //   Java source line #1118	-> byte code offset #82
/*      */     //   Java source line #1120	-> byte code offset #90
/*      */     //   Java source line #1116	-> byte code offset #103
/*      */     //   Java source line #1125	-> byte code offset #109
/*      */     //   Java source line #1127	-> byte code offset #118
/*      */     //   Java source line #1166	-> byte code offset #135
/*      */     //   Java source line #1168	-> byte code offset #142
/*      */     //   Java source line #1170	-> byte code offset #153
/*      */     //   Java source line #1172	-> byte code offset #160
/*      */     //   Java source line #1174	-> byte code offset #176
/*      */     //   Java source line #1176	-> byte code offset #200
/*      */     //   Java source line #1178	-> byte code offset #204
/*      */     //   Java source line #1180	-> byte code offset #213
/*      */     //   Java source line #1182	-> byte code offset #218
/*      */     //   Java source line #1185	-> byte code offset #233
/*      */     //   Java source line #1187	-> byte code offset #240
/*      */     //   Java source line #1189	-> byte code offset #260
/*      */     //   Java source line #1191	-> byte code offset #274
/*      */     //   Java source line #1193	-> byte code offset #280
/*      */     //   Java source line #1195	-> byte code offset #283
/*      */     //   Java source line #1197	-> byte code offset #288
/*      */     //   Java source line #1201	-> byte code offset #308
/*      */     //   Java source line #1205	-> byte code offset #322
/*      */     //   Java source line #1207	-> byte code offset #326
/*      */     //   Java source line #1209	-> byte code offset #333
/*      */     //   Java source line #1211	-> byte code offset #342
/*      */     //   Java source line #1213	-> byte code offset #368
/*      */     //   Java source line #1217	-> byte code offset #391
/*      */     //   Java source line #1211	-> byte code offset #401
/*      */     //   Java source line #1221	-> byte code offset #407
/*      */     //   Java source line #1224	-> byte code offset #432
/*      */     //   Java source line #1226	-> byte code offset #437
/*      */     //   Java source line #1228	-> byte code offset #449
/*      */     //   Java source line #1230	-> byte code offset #457
/*      */     //   Java source line #1232	-> byte code offset #468
/*      */     //   Java source line #1236	-> byte code offset #475
/*      */     //   Java source line #1238	-> byte code offset #483
/*      */     //   Java source line #1242	-> byte code offset #493
/*      */     //   Java source line #1244	-> byte code offset #500
/*      */     //   Java source line #1247	-> byte code offset #522
/*      */     //   Java source line #1230	-> byte code offset #529
/*      */     //   Java source line #1250	-> byte code offset #535
/*      */     //   Java source line #1252	-> byte code offset #538
/*      */     //   Java source line #1254	-> byte code offset #548
/*      */     //   Java source line #1256	-> byte code offset #576
/*      */     //   Java source line #1258	-> byte code offset #586
/*      */     //   Java source line #1260	-> byte code offset #595
/*      */     //   Java source line #1262	-> byte code offset #598
/*      */     //   Java source line #1264	-> byte code offset #609
/*      */     //   Java source line #1266	-> byte code offset #625
/*      */     //   Java source line #1268	-> byte code offset #628
/*      */     //   Java source line #1262	-> byte code offset #631
/*      */     //   Java source line #1272	-> byte code offset #637
/*      */     //   Java source line #1274	-> byte code offset #642
/*      */     //   Java source line #1276	-> byte code offset #645
/*      */     //   Java source line #1279	-> byte code offset #648
/*      */     //   Java source line #1282	-> byte code offset #651
/*      */     //   Java source line #1284	-> byte code offset #654
/*      */     //   Java source line #1286	-> byte code offset #665
/*      */     //   Java source line #1288	-> byte code offset #672
/*      */     //   Java source line #1290	-> byte code offset #679
/*      */     //   Java source line #1292	-> byte code offset #685
/*      */     //   Java source line #1294	-> byte code offset #694
/*      */     //   Java source line #1296	-> byte code offset #700
/*      */     //   Java source line #1301	-> byte code offset #710
/*      */     //   Java source line #1303	-> byte code offset #758
/*      */     //   Java source line #1305	-> byte code offset #777
/*      */     //   Java source line #1307	-> byte code offset #794
/*      */     //   Java source line #1309	-> byte code offset #799
/*      */     //   Java source line #1312	-> byte code offset #833
/*      */     //   Java source line #1314	-> byte code offset #839
/*      */     //   Java source line #1316	-> byte code offset #846
/*      */     //   Java source line #1318	-> byte code offset #849
/*      */     //   Java source line #1320	-> byte code offset #852
/*      */     //   Java source line #1322	-> byte code offset #860
/*      */     //   Java source line #1324	-> byte code offset #898
/*      */     //   Java source line #1326	-> byte code offset #907
/*      */     //   Java source line #1328	-> byte code offset #912
/*      */     //   Java source line #1330	-> byte code offset #925
/*      */     //   Java source line #1332	-> byte code offset #929
/*      */     //   Java source line #1334	-> byte code offset #932
/*      */     //   Java source line #1338	-> byte code offset #935
/*      */     //   Java source line #1340	-> byte code offset #939
/*      */     //   Java source line #1343	-> byte code offset #942
/*      */     //   Java source line #1344	-> byte code offset #945
/*      */     //   Java source line #1346	-> byte code offset #948
/*      */     //   Java source line #1348	-> byte code offset #969
/*      */     //   Java source line #1352	-> byte code offset #983
/*      */     //   Java source line #1354	-> byte code offset #988
/*      */     //   Java source line #1356	-> byte code offset #1007
/*      */     //   Java source line #1360	-> byte code offset #1018
/*      */     //   Java source line #1362	-> byte code offset #1051
/*      */     //   Java source line #1364	-> byte code offset #1065
/*      */     //   Java source line #1284	-> byte code offset #1079
/*      */     //   Java source line #1370	-> byte code offset #1085
/*      */     //   Java source line #1398	-> byte code offset #1089
/*      */     //   Java source line #1401	-> byte code offset #1094
/*      */     //   Java source line #1406	-> byte code offset #1099
/*      */     //   Java source line #1403	-> byte code offset #1102
/*      */     //   Java source line #1405	-> byte code offset #1104
/*      */     //   Java source line #1411	-> byte code offset #1109
/*      */     //   Java source line #1372	-> byte code offset #1119
/*      */     //   Java source line #1374	-> byte code offset #1121
/*      */     //   Java source line #1376	-> byte code offset #1128
/*      */     //   Java source line #1378	-> byte code offset #1139
/*      */     //   Java source line #1380	-> byte code offset #1143
/*      */     //   Java source line #1398	-> byte code offset #1152
/*      */     //   Java source line #1401	-> byte code offset #1157
/*      */     //   Java source line #1406	-> byte code offset #1162
/*      */     //   Java source line #1403	-> byte code offset #1165
/*      */     //   Java source line #1405	-> byte code offset #1167
/*      */     //   Java source line #1411	-> byte code offset #1172
/*      */     //   Java source line #1384	-> byte code offset #1182
/*      */     //   Java source line #1386	-> byte code offset #1193
/*      */     //   Java source line #1388	-> byte code offset #1197
/*      */     //   Java source line #1398	-> byte code offset #1206
/*      */     //   Java source line #1401	-> byte code offset #1211
/*      */     //   Java source line #1406	-> byte code offset #1216
/*      */     //   Java source line #1403	-> byte code offset #1219
/*      */     //   Java source line #1405	-> byte code offset #1221
/*      */     //   Java source line #1411	-> byte code offset #1226
/*      */     //   Java source line #1392	-> byte code offset #1236
/*      */     //   Java source line #1394	-> byte code offset #1241
/*      */     //   Java source line #1398	-> byte code offset #1244
/*      */     //   Java source line #1401	-> byte code offset #1249
/*      */     //   Java source line #1406	-> byte code offset #1254
/*      */     //   Java source line #1403	-> byte code offset #1257
/*      */     //   Java source line #1405	-> byte code offset #1259
/*      */     //   Java source line #1411	-> byte code offset #1264
/*      */     //   Java source line #1398	-> byte code offset #1274
/*      */     //   Java source line #1401	-> byte code offset #1281
/*      */     //   Java source line #1406	-> byte code offset #1286
/*      */     //   Java source line #1403	-> byte code offset #1289
/*      */     //   Java source line #1405	-> byte code offset #1291
/*      */     //   Java source line #1406	-> byte code offset #1296
/*      */     //   Java source line #1411	-> byte code offset #1299
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	1311	0	this	SESecurityManagerImpl
/*      */     //   0	1311	1	https_url	URL
/*      */     //   0	1311	2	sni_hack	boolean
/*      */     //   0	1311	3	dh_hack	boolean
/*      */     //   11	767	4	host	String
/*      */     //   17	768	5	port	int
/*      */     //   31	1251	6	socket	javax.net.ssl.SSLSocket
/*      */     //   37	17	7	tmf	TrustManagerFactory
/*      */     //   1119	118	7	e	Throwable
/*      */     //   46	78	8	default_tms	List<X509TrustManager>
/*      */     //   1126	57	8	msg	String
/*      */     //   58	18	9	arr$	TrustManager[]
/*      */     //   116	1156	9	trustedChains	List<Object>
/*      */     //   63	8	10	len$	int
/*      */     //   133	13	10	trustAllCerts	TrustManager[]
/*      */     //   1165	3	10	e	Throwable
/*      */     //   1219	3	10	e	Throwable
/*      */     //   1257	3	10	e	Throwable
/*      */     //   66	38	11	i$	int
/*      */     //   140	14	11	sc	SSLContext
/*      */     //   80	13	12	tm	TrustManager
/*      */     //   158	151	12	factory	SSLSocketFactory
/*      */     //   174	62	13	targetSockAddress	java.net.InetSocketAddress
/*      */     //   198	97	14	bindIP	java.net.InetAddress
/*      */     //   211	36	15	base_socket	java.net.Socket
/*      */     //   331	12	15	cs	String[]
/*      */     //   447	219	15	serverCerts	Certificate[]
/*      */     //   340	72	16	new_cs	List<String>
/*      */     //   455	218	16	x509_certs	X509Certificate[]
/*      */     //   344	18	17	arr$	String[]
/*      */     //   458	72	17	i	int
/*      */     //   536	419	17	chain_trusted	boolean
/*      */     //   349	8	18	len$	int
/*      */     //   473	34	18	cert	Certificate
/*      */     //   555	13	18	i$	Iterator
/*      */     //   652	434	18	result	SSLSocketFactory
/*      */     //   352	50	19	i$	int
/*      */     //   488	3	19	x509_cert	X509Certificate
/*      */     //   520	7	19	x509_cert	X509Certificate
/*      */     //   574	3	19	ochain	Object
/*      */     //   655	462	19	i	int
/*      */     //   366	28	20	x	String
/*      */     //   498	3	20	cf	java.security.cert.CertificateFactory
/*      */     //   584	26	20	chain	X509Certificate[]
/*      */     //   670	399	20	cert	Certificate
/*      */     //   1102	3	20	e	Throwable
/*      */     //   596	42	21	match	boolean
/*      */     //   677	379	21	x509_cert	X509Certificate
/*      */     //   599	33	22	i	int
/*      */     //   683	371	22	resource	String
/*      */     //   692	12	23	param_pos	int
/*      */     //   756	7	24	url_s	String
/*      */     //   775	214	25	handler	Object[]
/*      */     //   792	275	26	alias	String
/*      */     //   837	62	27	keystore	KeyStore
/*      */     //   844	69	28	new_encoded	byte[]
/*      */     //   847	96	29	count	int
/*      */     //   850	110	30	already_trusted	boolean
/*      */     //   896	40	31	test_alias	String
/*      */     //   1027	13	31	i$	Iterator
/*      */     //   905	10	32	existing	Certificate
/*      */     //   1049	3	32	listener	SECertificateListener
/*      */     //   1274	23	33	localObject1	Object
/*      */     //   1289	3	34	e	Throwable
/*      */     //   1299	10	35	localObject2	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   1094	1099	1102	java/lang/Throwable
/*      */     //   33	1089	1119	java/lang/Throwable
/*      */     //   1157	1162	1165	java/lang/Throwable
/*      */     //   1211	1216	1219	java/lang/Throwable
/*      */     //   1249	1254	1257	java/lang/Throwable
/*      */     //   33	1089	1274	finally
/*      */     //   1119	1152	1274	finally
/*      */     //   1182	1206	1274	finally
/*      */     //   1236	1244	1274	finally
/*      */     //   1274	1276	1274	finally
/*      */     //   1281	1286	1289	java/lang/Throwable
/*      */     //   0	1109	1299	finally
/*      */     //   1119	1172	1299	finally
/*      */     //   1182	1226	1299	finally
/*      */     //   1236	1264	1299	finally
/*      */     //   1274	1301	1299	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public SSLSocketFactory installServerCertificates(String alias, String host, int port, boolean sni_hack)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 890	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   4: invokevirtual 1047	org/gudy/azureus2/core3/util/AEMonitor:enter	()V
/*      */     //   7: iload_3
/*      */     //   8: iconst_m1
/*      */     //   9: if_icmpne +7 -> 16
/*      */     //   12: sipush 443
/*      */     //   15: istore_3
/*      */     //   16: aconst_null
/*      */     //   17: astore 5
/*      */     //   19: invokestatic 1013	org/gudy/azureus2/core3/security/SESecurityManager:getAllTrustingTrustManager	()[Ljavax/net/ssl/TrustManager;
/*      */     //   22: astore 6
/*      */     //   24: ldc 23
/*      */     //   26: invokestatic 988	javax/net/ssl/SSLContext:getInstance	(Ljava/lang/String;)Ljavax/net/ssl/SSLContext;
/*      */     //   29: astore 7
/*      */     //   31: aload 7
/*      */     //   33: aconst_null
/*      */     //   34: aload 6
/*      */     //   36: getstatic 892	org/gudy/azureus2/core3/util/RandomUtils:SECURE_RANDOM	Ljava/security/SecureRandom;
/*      */     //   39: invokevirtual 989	javax/net/ssl/SSLContext:init	([Ljavax/net/ssl/KeyManager;[Ljavax/net/ssl/TrustManager;Ljava/security/SecureRandom;)V
/*      */     //   42: aload 7
/*      */     //   44: invokevirtual 987	javax/net/ssl/SSLContext:getSocketFactory	()Ljavax/net/ssl/SSLSocketFactory;
/*      */     //   47: astore 8
/*      */     //   49: new 485	java/net/InetSocketAddress
/*      */     //   52: dup
/*      */     //   53: aload_2
/*      */     //   54: invokestatic 949	java/net/InetAddress:getByName	(Ljava/lang/String;)Ljava/net/InetAddress;
/*      */     //   57: iload_3
/*      */     //   58: invokespecial 951	java/net/InetSocketAddress:<init>	(Ljava/net/InetAddress;I)V
/*      */     //   61: astore 9
/*      */     //   63: invokestatic 893	com/aelitis/azureus/core/networkmanager/admin/NetworkAdmin:getSingleton	()Lcom/aelitis/azureus/core/networkmanager/admin/NetworkAdmin;
/*      */     //   66: aload 9
/*      */     //   68: invokevirtual 950	java/net/InetSocketAddress:getAddress	()Ljava/net/InetAddress;
/*      */     //   71: instanceof 483
/*      */     //   74: ifeq +7 -> 81
/*      */     //   77: iconst_2
/*      */     //   78: goto +4 -> 82
/*      */     //   81: iconst_1
/*      */     //   82: invokevirtual 894	com/aelitis/azureus/core/networkmanager/admin/NetworkAdmin:getSingleHomedServiceBindAddress	(I)Ljava/net/InetAddress;
/*      */     //   85: astore 10
/*      */     //   87: iload 4
/*      */     //   89: ifeq +82 -> 171
/*      */     //   92: new 488	java/net/Socket
/*      */     //   95: dup
/*      */     //   96: invokespecial 954	java/net/Socket:<init>	()V
/*      */     //   99: astore 11
/*      */     //   101: aload 10
/*      */     //   103: ifnull +18 -> 121
/*      */     //   106: aload 11
/*      */     //   108: new 485	java/net/InetSocketAddress
/*      */     //   111: dup
/*      */     //   112: aload 10
/*      */     //   114: iconst_0
/*      */     //   115: invokespecial 951	java/net/InetSocketAddress:<init>	(Ljava/net/InetAddress;I)V
/*      */     //   118: invokevirtual 955	java/net/Socket:bind	(Ljava/net/SocketAddress;)V
/*      */     //   121: aload 11
/*      */     //   123: aload 9
/*      */     //   125: invokevirtual 956	java/net/Socket:connect	(Ljava/net/SocketAddress;)V
/*      */     //   128: aload 8
/*      */     //   130: aload 11
/*      */     //   132: ldc 1
/*      */     //   134: aload 11
/*      */     //   136: invokevirtual 953	java/net/Socket:getPort	()I
/*      */     //   139: iconst_1
/*      */     //   140: invokevirtual 999	javax/net/ssl/SSLSocketFactory:createSocket	(Ljava/net/Socket;Ljava/lang/String;IZ)Ljava/net/Socket;
/*      */     //   143: checkcast 506	javax/net/ssl/SSLSocket
/*      */     //   146: astore 5
/*      */     //   148: aload 5
/*      */     //   150: iconst_1
/*      */     //   151: anewarray 475	java/lang/String
/*      */     //   154: dup
/*      */     //   155: iconst_0
/*      */     //   156: ldc 28
/*      */     //   158: aastore
/*      */     //   159: invokevirtual 995	javax/net/ssl/SSLSocket:setEnabledProtocols	([Ljava/lang/String;)V
/*      */     //   162: aload 5
/*      */     //   164: iconst_1
/*      */     //   165: invokevirtual 992	javax/net/ssl/SSLSocket:setUseClientMode	(Z)V
/*      */     //   168: goto +38 -> 206
/*      */     //   171: aload 10
/*      */     //   173: ifnull +21 -> 194
/*      */     //   176: aload 8
/*      */     //   178: aload_2
/*      */     //   179: iload_3
/*      */     //   180: aload 10
/*      */     //   182: iconst_0
/*      */     //   183: invokevirtual 1000	javax/net/ssl/SSLSocketFactory:createSocket	(Ljava/lang/String;ILjava/net/InetAddress;I)Ljava/net/Socket;
/*      */     //   186: checkcast 506	javax/net/ssl/SSLSocket
/*      */     //   189: astore 5
/*      */     //   191: goto +15 -> 206
/*      */     //   194: aload 8
/*      */     //   196: aload_2
/*      */     //   197: iload_3
/*      */     //   198: invokevirtual 998	javax/net/ssl/SSLSocketFactory:createSocket	(Ljava/lang/String;I)Ljava/net/Socket;
/*      */     //   201: checkcast 506	javax/net/ssl/SSLSocket
/*      */     //   204: astore 5
/*      */     //   206: aload 5
/*      */     //   208: invokevirtual 991	javax/net/ssl/SSLSocket:startHandshake	()V
/*      */     //   211: aload 5
/*      */     //   213: invokevirtual 996	javax/net/ssl/SSLSocket:getSession	()Ljavax/net/ssl/SSLSession;
/*      */     //   216: invokeinterface 1070 1 0
/*      */     //   221: astore 11
/*      */     //   223: aconst_null
/*      */     //   224: astore 12
/*      */     //   226: aload 11
/*      */     //   228: astore 13
/*      */     //   230: aload 13
/*      */     //   232: arraylength
/*      */     //   233: istore 14
/*      */     //   235: iconst_0
/*      */     //   236: istore 15
/*      */     //   238: iload 15
/*      */     //   240: iload 14
/*      */     //   242: if_icmpge +73 -> 315
/*      */     //   245: aload 13
/*      */     //   247: iload 15
/*      */     //   249: aaload
/*      */     //   250: astore 16
/*      */     //   252: aload 16
/*      */     //   254: instanceof 494
/*      */     //   257: ifeq +13 -> 270
/*      */     //   260: aload 16
/*      */     //   262: checkcast 494	java/security/cert/X509Certificate
/*      */     //   265: astore 17
/*      */     //   267: goto +32 -> 299
/*      */     //   270: ldc 29
/*      */     //   272: invokestatic 976	java/security/cert/CertificateFactory:getInstance	(Ljava/lang/String;)Ljava/security/cert/CertificateFactory;
/*      */     //   275: astore 18
/*      */     //   277: aload 18
/*      */     //   279: new 467	java/io/ByteArrayInputStream
/*      */     //   282: dup
/*      */     //   283: aload 16
/*      */     //   285: invokevirtual 974	java/security/cert/Certificate:getEncoded	()[B
/*      */     //   288: invokespecial 899	java/io/ByteArrayInputStream:<init>	([B)V
/*      */     //   291: invokevirtual 975	java/security/cert/CertificateFactory:generateCertificate	(Ljava/io/InputStream;)Ljava/security/cert/Certificate;
/*      */     //   294: checkcast 494	java/security/cert/X509Certificate
/*      */     //   297: astore 17
/*      */     //   299: aload_0
/*      */     //   300: aload_1
/*      */     //   301: aload 17
/*      */     //   303: iconst_0
/*      */     //   304: invokevirtual 1036	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:addCertToTrustStore	(Ljava/lang/String;Ljava/security/cert/Certificate;Z)Ljavax/net/ssl/SSLSocketFactory;
/*      */     //   307: astore 12
/*      */     //   309: iinc 15 1
/*      */     //   312: goto -74 -> 238
/*      */     //   315: aload 12
/*      */     //   317: astore 13
/*      */     //   319: aload 5
/*      */     //   321: ifnull +18 -> 339
/*      */     //   324: aload 5
/*      */     //   326: invokevirtual 990	javax/net/ssl/SSLSocket:close	()V
/*      */     //   329: goto +10 -> 339
/*      */     //   332: astore 14
/*      */     //   334: aload 14
/*      */     //   336: invokestatic 1054	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*      */     //   339: aload_0
/*      */     //   340: getfield 890	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   343: invokevirtual 1048	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   346: aload 13
/*      */     //   348: areturn
/*      */     //   349: astore 6
/*      */     //   351: aload 6
/*      */     //   353: invokestatic 1055	org/gudy/azureus2/core3/util/Debug:getNestedExceptionMessage	(Ljava/lang/Throwable;)Ljava/lang/String;
/*      */     //   356: ldc_w 458
/*      */     //   359: invokevirtual 922	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
/*      */     //   362: ifeq +48 -> 410
/*      */     //   365: iload 4
/*      */     //   367: ifne +43 -> 410
/*      */     //   370: aload_0
/*      */     //   371: aload_1
/*      */     //   372: aload_2
/*      */     //   373: iload_3
/*      */     //   374: iconst_1
/*      */     //   375: invokevirtual 1035	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:installServerCertificates	(Ljava/lang/String;Ljava/lang/String;IZ)Ljavax/net/ssl/SSLSocketFactory;
/*      */     //   378: astore 7
/*      */     //   380: aload 5
/*      */     //   382: ifnull +18 -> 400
/*      */     //   385: aload 5
/*      */     //   387: invokevirtual 990	javax/net/ssl/SSLSocket:close	()V
/*      */     //   390: goto +10 -> 400
/*      */     //   393: astore 8
/*      */     //   395: aload 8
/*      */     //   397: invokestatic 1054	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*      */     //   400: aload_0
/*      */     //   401: getfield 890	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   404: invokevirtual 1048	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   407: aload 7
/*      */     //   409: areturn
/*      */     //   410: aload 6
/*      */     //   412: invokestatic 1053	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/Throwable;)V
/*      */     //   415: aconst_null
/*      */     //   416: astore 7
/*      */     //   418: aload 5
/*      */     //   420: ifnull +18 -> 438
/*      */     //   423: aload 5
/*      */     //   425: invokevirtual 990	javax/net/ssl/SSLSocket:close	()V
/*      */     //   428: goto +10 -> 438
/*      */     //   431: astore 8
/*      */     //   433: aload 8
/*      */     //   435: invokestatic 1054	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*      */     //   438: aload_0
/*      */     //   439: getfield 890	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   442: invokevirtual 1048	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   445: aload 7
/*      */     //   447: areturn
/*      */     //   448: astore 19
/*      */     //   450: aload 5
/*      */     //   452: ifnull +18 -> 470
/*      */     //   455: aload 5
/*      */     //   457: invokevirtual 990	javax/net/ssl/SSLSocket:close	()V
/*      */     //   460: goto +10 -> 470
/*      */     //   463: astore 20
/*      */     //   465: aload 20
/*      */     //   467: invokestatic 1054	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*      */     //   470: aload 19
/*      */     //   472: athrow
/*      */     //   473: astore 21
/*      */     //   475: aload_0
/*      */     //   476: getfield 890	org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   479: invokevirtual 1048	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   482: aload 21
/*      */     //   484: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1432	-> byte code offset #0
/*      */     //   Java source line #1434	-> byte code offset #7
/*      */     //   Java source line #1436	-> byte code offset #12
/*      */     //   Java source line #1439	-> byte code offset #16
/*      */     //   Java source line #1445	-> byte code offset #19
/*      */     //   Java source line #1447	-> byte code offset #24
/*      */     //   Java source line #1449	-> byte code offset #31
/*      */     //   Java source line #1451	-> byte code offset #42
/*      */     //   Java source line #1453	-> byte code offset #49
/*      */     //   Java source line #1455	-> byte code offset #63
/*      */     //   Java source line #1457	-> byte code offset #87
/*      */     //   Java source line #1459	-> byte code offset #92
/*      */     //   Java source line #1461	-> byte code offset #101
/*      */     //   Java source line #1463	-> byte code offset #106
/*      */     //   Java source line #1466	-> byte code offset #121
/*      */     //   Java source line #1468	-> byte code offset #128
/*      */     //   Java source line #1470	-> byte code offset #148
/*      */     //   Java source line #1472	-> byte code offset #162
/*      */     //   Java source line #1474	-> byte code offset #168
/*      */     //   Java source line #1476	-> byte code offset #171
/*      */     //   Java source line #1478	-> byte code offset #176
/*      */     //   Java source line #1482	-> byte code offset #194
/*      */     //   Java source line #1486	-> byte code offset #206
/*      */     //   Java source line #1488	-> byte code offset #211
/*      */     //   Java source line #1490	-> byte code offset #223
/*      */     //   Java source line #1492	-> byte code offset #226
/*      */     //   Java source line #1496	-> byte code offset #252
/*      */     //   Java source line #1498	-> byte code offset #260
/*      */     //   Java source line #1502	-> byte code offset #270
/*      */     //   Java source line #1504	-> byte code offset #277
/*      */     //   Java source line #1507	-> byte code offset #299
/*      */     //   Java source line #1492	-> byte code offset #309
/*      */     //   Java source line #1510	-> byte code offset #315
/*      */     //   Java source line #1528	-> byte code offset #319
/*      */     //   Java source line #1531	-> byte code offset #324
/*      */     //   Java source line #1536	-> byte code offset #329
/*      */     //   Java source line #1533	-> byte code offset #332
/*      */     //   Java source line #1535	-> byte code offset #334
/*      */     //   Java source line #1541	-> byte code offset #339
/*      */     //   Java source line #1512	-> byte code offset #349
/*      */     //   Java source line #1514	-> byte code offset #351
/*      */     //   Java source line #1516	-> byte code offset #365
/*      */     //   Java source line #1518	-> byte code offset #370
/*      */     //   Java source line #1528	-> byte code offset #380
/*      */     //   Java source line #1531	-> byte code offset #385
/*      */     //   Java source line #1536	-> byte code offset #390
/*      */     //   Java source line #1533	-> byte code offset #393
/*      */     //   Java source line #1535	-> byte code offset #395
/*      */     //   Java source line #1541	-> byte code offset #400
/*      */     //   Java source line #1522	-> byte code offset #410
/*      */     //   Java source line #1524	-> byte code offset #415
/*      */     //   Java source line #1528	-> byte code offset #418
/*      */     //   Java source line #1531	-> byte code offset #423
/*      */     //   Java source line #1536	-> byte code offset #428
/*      */     //   Java source line #1533	-> byte code offset #431
/*      */     //   Java source line #1535	-> byte code offset #433
/*      */     //   Java source line #1541	-> byte code offset #438
/*      */     //   Java source line #1528	-> byte code offset #448
/*      */     //   Java source line #1531	-> byte code offset #455
/*      */     //   Java source line #1536	-> byte code offset #460
/*      */     //   Java source line #1533	-> byte code offset #463
/*      */     //   Java source line #1535	-> byte code offset #465
/*      */     //   Java source line #1536	-> byte code offset #470
/*      */     //   Java source line #1541	-> byte code offset #473
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	485	0	this	SESecurityManagerImpl
/*      */     //   0	485	1	alias	String
/*      */     //   0	485	2	host	String
/*      */     //   0	485	3	port	int
/*      */     //   0	485	4	sni_hack	boolean
/*      */     //   17	439	5	socket	javax.net.ssl.SSLSocket
/*      */     //   22	13	6	trustAllCerts	TrustManager[]
/*      */     //   349	62	6	e	Throwable
/*      */     //   29	417	7	sc	SSLContext
/*      */     //   47	148	8	factory	SSLSocketFactory
/*      */     //   393	3	8	e	Throwable
/*      */     //   431	3	8	e	Throwable
/*      */     //   61	63	9	targetSockAddress	java.net.InetSocketAddress
/*      */     //   85	96	10	bindIP	java.net.InetAddress
/*      */     //   99	36	11	base_socket	java.net.Socket
/*      */     //   221	6	11	serverCerts	Certificate[]
/*      */     //   224	92	12	result	SSLSocketFactory
/*      */     //   228	119	13	arr$	Certificate[]
/*      */     //   233	8	14	len$	int
/*      */     //   332	3	14	e	Throwable
/*      */     //   236	74	15	i$	int
/*      */     //   250	34	16	cert	Certificate
/*      */     //   265	3	17	x509_cert	X509Certificate
/*      */     //   297	5	17	x509_cert	X509Certificate
/*      */     //   275	3	18	cf	java.security.cert.CertificateFactory
/*      */     //   448	23	19	localObject1	Object
/*      */     //   463	3	20	e	Throwable
/*      */     //   473	10	21	localObject2	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   324	329	332	java/lang/Throwable
/*      */     //   19	319	349	java/lang/Throwable
/*      */     //   385	390	393	java/lang/Throwable
/*      */     //   423	428	431	java/lang/Throwable
/*      */     //   19	319	448	finally
/*      */     //   349	380	448	finally
/*      */     //   410	418	448	finally
/*      */     //   448	450	448	finally
/*      */     //   455	460	463	java/lang/Throwable
/*      */     //   0	339	473	finally
/*      */     //   349	400	473	finally
/*      */     //   410	438	473	finally
/*      */     //   448	475	473	finally
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/security/impl/SESecurityManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */