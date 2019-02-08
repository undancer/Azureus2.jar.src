/*      */ package com.aelitis.azureus.plugins.net.netstatus.swt;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminASN;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminHTTPProxy;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminNATDevice;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminProgressListener;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminProtocol;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSocksProxy;
/*      */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*      */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy;
/*      */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
/*      */ import com.aelitis.azureus.plugins.net.netstatus.NetStatusPlugin;
/*      */ import com.aelitis.azureus.plugins.net.netstatus.NetStatusProtocolTester;
/*      */ import com.aelitis.azureus.plugins.net.netstatus.NetStatusProtocolTesterBT;
/*      */ import com.aelitis.azureus.plugins.net.netstatus.NetStatusProtocolTesterBT.Session;
/*      */ import com.aelitis.azureus.plugins.net.netstatus.NetStatusProtocolTesterListener;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.net.HttpURLConnection;
/*      */ import java.net.InetAddress;
/*      */ import java.net.URL;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.CertificateFactory;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import javax.net.ssl.HostnameVerifier;
/*      */ import javax.net.ssl.HttpsURLConnection;
/*      */ import javax.net.ssl.SSLSession;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class NetStatusPluginTester
/*      */ {
/*      */   public static final int TEST_NAT_PROXIES = 2;
/*      */   public static final int TEST_OUTBOUND = 4;
/*      */   public static final int TEST_INBOUND = 8;
/*      */   public static final int TEST_BT_CONNECT = 16;
/*      */   public static final int TEST_IPV6 = 32;
/*      */   public static final int TEST_VUZE_SERVICES = 64;
/*      */   public static final int TEST_PROXY_CONNECT = 128;
/*      */   private static final int ROUTE_TIMEOUT = 120000;
/*      */   private NetStatusPlugin plugin;
/*      */   private int test_types;
/*      */   private loggerProvider logger;
/*      */   private volatile boolean test_cancelled;
/*      */   
/*      */   public NetStatusPluginTester(NetStatusPlugin _plugin, int _test_types, loggerProvider _logger)
/*      */   {
/*   76 */     this.plugin = _plugin;
/*   77 */     this.test_types = _test_types;
/*   78 */     this.logger = _logger;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean doTest(int type)
/*      */   {
/*   85 */     if (this.test_cancelled)
/*      */     {
/*   87 */       return false;
/*      */     }
/*      */     
/*   90 */     return (this.test_types & type) != 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public void run(AzureusCore core)
/*      */   {
/*   96 */     NetworkAdmin admin = NetworkAdmin.getSingleton();
/*      */     
/*   98 */     boolean checked_public = false;
/*      */     
/*  100 */     Set<InetAddress> public_addresses = new HashSet();
/*      */     
/*  102 */     InetAddress def_pa = admin.getDefaultPublicAddress();
/*      */     
/*  104 */     if (def_pa != null)
/*      */     {
/*  106 */       log("Default public address is " + def_pa.getHostAddress());
/*      */       
/*  108 */       addPublicAddress(public_addresses, def_pa);
/*      */       
/*  110 */       checked_public = true;
/*      */     }
/*      */     
/*  113 */     InetAddress[] bindable = admin.getBindableAddresses();
/*      */     
/*  115 */     String bindable_str = "";
/*      */     
/*  117 */     for (InetAddress b : bindable)
/*      */     {
/*  119 */       bindable_str = bindable_str + (bindable_str.length() == 0 ? "" : ", ") + b.getHostAddress();
/*      */     }
/*      */     
/*  122 */     log("Bindable addresses: " + bindable_str);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  420 */     if (doTest(2))
/*      */     {
/*  422 */       checked_public = true;
/*      */       
/*  424 */       NetworkAdminNATDevice[] nat_devices = admin.getNATDevices(core);
/*      */       
/*  426 */       log(nat_devices.length + " NAT device" + (nat_devices.length == 1 ? "" : "s") + " found");
/*      */       
/*  428 */       for (int i = 0; i < nat_devices.length; i++)
/*      */       {
/*  430 */         NetworkAdminNATDevice device = nat_devices[i];
/*      */         
/*  432 */         InetAddress ext_address = device.getExternalAddress();
/*      */         
/*  434 */         addPublicAddress(public_addresses, ext_address);
/*      */         
/*  436 */         log("    " + device.getString());
/*      */       }
/*      */       
/*  439 */       NetworkAdminSocksProxy[] socks_proxies = admin.getSocksProxies();
/*      */       
/*  441 */       if (socks_proxies.length == 0)
/*      */       {
/*  443 */         log("No SOCKS proxy found");
/*      */       }
/*  445 */       else if (socks_proxies.length == 1)
/*      */       {
/*  447 */         log("One SOCKS proxy found");
/*      */       }
/*      */       else
/*      */       {
/*  451 */         log(socks_proxies.length + " SOCKS proxies found");
/*      */       }
/*      */       
/*  454 */       for (int i = 0; i < socks_proxies.length; i++)
/*      */       {
/*  456 */         NetworkAdminSocksProxy proxy = socks_proxies[i];
/*      */         
/*  458 */         log("    " + proxy.getString());
/*      */       }
/*      */       
/*  461 */       NetworkAdminHTTPProxy http_proxy = admin.getHTTPProxy();
/*      */       
/*  463 */       if (http_proxy == null)
/*      */       {
/*  465 */         log("No HTTP proxy found");
/*      */       }
/*      */       else
/*      */       {
/*  469 */         log("HTTP proxy found");
/*      */         
/*  471 */         log("    " + http_proxy.getString());
/*      */       }
/*      */     }
/*      */     
/*  475 */     InetAddress[] bind_addresses = admin.getAllBindAddresses(false);
/*      */     
/*  477 */     int num_binds = 0;
/*      */     
/*  479 */     for (int i = 0; i < bind_addresses.length; i++)
/*      */     {
/*  481 */       if (bind_addresses[i] != null)
/*      */       {
/*  483 */         num_binds++;
/*      */       }
/*      */     }
/*      */     
/*  487 */     if (num_binds == 0)
/*      */     {
/*  489 */       log("No explicit bind address set");
/*      */     }
/*      */     else
/*      */     {
/*  493 */       log(num_binds + " bind addresses");
/*      */       
/*  495 */       for (int i = 0; i < bind_addresses.length; i++)
/*      */       {
/*  497 */         if (bind_addresses[i] != null)
/*      */         {
/*  499 */           log("    " + bind_addresses[i].getHostAddress());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  504 */     if (doTest(4))
/*      */     {
/*  506 */       checked_public = true;
/*      */       
/*  508 */       NetworkAdminProtocol[] outbound_protocols = admin.getOutboundProtocols(core);
/*      */       
/*  510 */       if (outbound_protocols.length == 0)
/*      */       {
/*  512 */         log("No outbound protocols");
/*      */       }
/*      */       else
/*      */       {
/*  516 */         for (int i = 0; i < outbound_protocols.length; i++)
/*      */         {
/*  518 */           if (this.test_cancelled)
/*      */           {
/*  520 */             return;
/*      */           }
/*      */           
/*  523 */           NetworkAdminProtocol protocol = outbound_protocols[i];
/*      */           
/*  525 */           log("Testing " + protocol.getName());
/*      */           try
/*      */           {
/*  528 */             InetAddress public_address = protocol.test(null, new NetworkAdminProgressListener()
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */               public void reportProgress(String task)
/*      */               {
/*      */ 
/*      */ 
/*  537 */                 NetStatusPluginTester.this.log("    " + task);
/*      */               }
/*      */               
/*  540 */             });
/*  541 */             logSuccess("    Test successful");
/*      */             
/*  543 */             addPublicAddress(public_addresses, public_address);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  547 */             logError("    Test failed", e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  553 */     if (doTest(8))
/*      */     {
/*  555 */       checked_public = true;
/*      */       
/*  557 */       NetworkAdminProtocol[] inbound_protocols = admin.getInboundProtocols(core);
/*      */       
/*  559 */       if (inbound_protocols.length == 0)
/*      */       {
/*  561 */         log("No inbound protocols");
/*      */       }
/*      */       else
/*      */       {
/*  565 */         for (int i = 0; i < inbound_protocols.length; i++)
/*      */         {
/*  567 */           if (this.test_cancelled)
/*      */           {
/*  569 */             return;
/*      */           }
/*      */           
/*  572 */           NetworkAdminProtocol protocol = inbound_protocols[i];
/*      */           
/*  574 */           log("Testing " + protocol.getName());
/*      */           try
/*      */           {
/*  577 */             InetAddress public_address = protocol.test(null, new NetworkAdminProgressListener()
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */               public void reportProgress(String task)
/*      */               {
/*      */ 
/*      */ 
/*  586 */                 NetStatusPluginTester.this.log("    " + task);
/*      */               }
/*      */               
/*  589 */             });
/*  590 */             logSuccess("    Test successful");
/*      */             
/*  592 */             addPublicAddress(public_addresses, public_address);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  596 */             logError("    Test failed", e);
/*  597 */             logInfo("    Check your port forwarding for " + protocol.getTypeString() + " " + protocol.getPort());
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  603 */     if (checked_public)
/*      */     {
/*  605 */       if (public_addresses.size() == 0)
/*      */       {
/*  607 */         log("No public addresses found");
/*      */       }
/*      */       else
/*      */       {
/*  611 */         Iterator<InetAddress> it = public_addresses.iterator();
/*      */         
/*  613 */         log(public_addresses.size() + " public/external addresses found");
/*      */         
/*  615 */         while (it.hasNext())
/*      */         {
/*  617 */           InetAddress pub_address = (InetAddress)it.next();
/*      */           
/*  619 */           log("    " + pub_address.getHostAddress());
/*      */           try
/*      */           {
/*  622 */             NetworkAdminASN asn = admin.lookupASN(pub_address);
/*      */             
/*  624 */             log("    AS details: " + asn.getString());
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  628 */             logError("    failed to lookup AS", e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  634 */     if (doTest(64))
/*      */     {
/*  636 */       log("Vuze Services test");
/*      */       
/*  638 */       String[][] services = { { "Vuze Website", "https://www.vuze.com/" }, { "Client Website", "https://client.vuze.com/" }, { "Version Server", "http://version.vuze.com/?dee" }, { "Pairing Server", "https://pair.vuze.com/pairing/web/view?" }, { "License Server", "https://license.vuze.com/licence" }, { "Plugins Website", "https://plugins.vuze.com/" } };
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  647 */       for (String[] service : services)
/*      */       {
/*  649 */         if (this.test_cancelled)
/*      */         {
/*  651 */           return;
/*      */         }
/*      */         try
/*      */         {
/*  655 */           URL url = new URL(service[1]);
/*      */           
/*  657 */           log("    " + service[0] + " - " + url.getHost());
/*      */           
/*  659 */           boolean is_https = url.getProtocol().equals("https");
/*      */           
/*  661 */           if (is_https)
/*      */           {
/*  663 */             String[] host_bits = url.getHost().split("\\.");
/*      */             
/*  665 */             String host_match = "." + host_bits[(host_bits.length - 2)] + "." + host_bits[(host_bits.length - 1)];
/*      */             
/*  667 */             HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
/*      */             
/*  669 */             con.setHostnameVerifier(new HostnameVerifier()
/*      */             {
/*      */ 
/*      */ 
/*      */               public boolean verify(String host, SSLSession session)
/*      */               {
/*      */ 
/*      */ 
/*  677 */                 return true;
/*      */               }
/*      */               
/*      */ 
/*  681 */             });
/*  682 */             con.setInstanceFollowRedirects(false);
/*      */             
/*  684 */             con.setConnectTimeout(30000);
/*  685 */             con.setReadTimeout(30000);
/*      */             
/*  687 */             con.getResponseCode();
/*      */             
/*  689 */             con.getInputStream();
/*      */             
/*  691 */             Certificate[] certs = con.getServerCertificates();
/*      */             
/*  693 */             if ((certs == null) || (certs.length == 0))
/*      */             {
/*  695 */               logError("        No certificates returned");
/*      */             }
/*      */             else
/*      */             {
/*  699 */               Certificate cert = certs[0];
/*      */               
/*      */               X509Certificate x509_cert;
/*      */               X509Certificate x509_cert;
/*  703 */               if ((cert instanceof X509Certificate))
/*      */               {
/*  705 */                 x509_cert = (X509Certificate)cert;
/*      */               }
/*      */               else
/*      */               {
/*  709 */                 CertificateFactory cf = CertificateFactory.getInstance("X.509");
/*      */                 
/*  711 */                 x509_cert = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(cert.getEncoded()));
/*      */               }
/*      */               
/*  714 */               log("        Certificate: " + x509_cert.getSubjectDN());
/*      */               
/*  716 */               Collection<List<?>> alt_names = x509_cert.getSubjectAlternativeNames();
/*      */               
/*  718 */               boolean match = false;
/*      */               
/*  720 */               for (List<?> alt_name : alt_names)
/*      */               {
/*  722 */                 int type = ((Number)alt_name.get(0)).intValue();
/*      */                 
/*  724 */                 if (type == 2)
/*      */                 {
/*  726 */                   String dns_name = (String)alt_name.get(1);
/*      */                   
/*  728 */                   if (dns_name.endsWith(host_match))
/*      */                   {
/*  730 */                     match = true;
/*      */                     
/*  732 */                     break;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*  737 */               if (!match)
/*      */               {
/*  739 */                 logError("        Failed: Host '" + host_match + "' not found in certificate");
/*      */               }
/*      */               else
/*      */               {
/*  743 */                 logSuccess("        Connection result: " + con.getResponseCode() + "/" + con.getResponseMessage());
/*      */               }
/*      */             }
/*      */           }
/*      */           else {
/*  748 */             HttpURLConnection con = (HttpURLConnection)url.openConnection();
/*      */             
/*  750 */             con.setInstanceFollowRedirects(false);
/*      */             
/*  752 */             con.setConnectTimeout(30000);
/*  753 */             con.setReadTimeout(30000);
/*      */             
/*  755 */             if (con.getResponseCode() != 200)
/*      */             {
/*  757 */               throw new Exception("Connection failed: " + con.getResponseCode() + "/" + con.getResponseMessage());
/*      */             }
/*      */             
/*  760 */             Map resp = BDecoder.decode(new BufferedInputStream(con.getInputStream(), 16384));
/*      */             
/*  762 */             if ((resp != null) && (resp.containsKey("version")))
/*      */             {
/*  764 */               logSuccess("        Connection result: " + con.getResponseCode() + "/" + con.getResponseMessage());
/*      */             }
/*      */             else
/*      */             {
/*  768 */               logError("        Unexpected reply from server: " + resp);
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/*  773 */           logError("        Failed: " + Debug.getNestedExceptionMessage(e));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  778 */     if (doTest(128))
/*      */     {
/*  780 */       log("Indirect Connect test");
/*      */       try
/*      */       {
/*  783 */         URL target = new URL("https://www.vuze.com");
/*      */         
/*  785 */         AEProxyFactory.PluginProxy proxy = AEProxyFactory.getPluginProxy("Network Status test", target);
/*      */         
/*  787 */         if (proxy == null)
/*      */         {
/*  789 */           String url_str = "http://azureus.sourceforge.net/plugin_detailssf.php?plugin=aznettor&os=";
/*      */           
/*  791 */           if (Constants.isWindows)
/*      */           {
/*  793 */             url_str = url_str + "Windows";
/*      */           }
/*      */           else
/*      */           {
/*  797 */             url_str = url_str + "Mac%20OSX";
/*      */           }
/*      */           
/*  800 */           URL url = new URL(url_str);
/*      */           
/*  802 */           logError("    No plugin proxy available");
/*  803 */           logInfo("    For the plugin installer see " + url.toExternalForm());
/*      */         }
/*      */         else
/*      */         {
/*  807 */           log("    Connecting to " + target.toExternalForm());
/*      */           
/*  809 */           HttpURLConnection con = (HttpURLConnection)proxy.getURL().openConnection(proxy.getProxy());
/*      */           
/*  811 */           if ((con instanceof HttpsURLConnection))
/*      */           {
/*  813 */             ((HttpsURLConnection)con).setHostnameVerifier(new HostnameVerifier()
/*      */             {
/*      */ 
/*      */ 
/*      */               public boolean verify(String host, SSLSession session)
/*      */               {
/*      */ 
/*      */ 
/*  821 */                 return true;
/*      */               }
/*      */             });
/*      */           }
/*      */           
/*  826 */           con.setRequestProperty("HOST", proxy.getURLHostRewrite());
/*      */           
/*  828 */           con.setInstanceFollowRedirects(false);
/*      */           
/*  830 */           con.setConnectTimeout(60000);
/*  831 */           con.setReadTimeout(30000);
/*      */           try
/*      */           {
/*  834 */             int resp = con.getResponseCode();
/*      */             
/*  836 */             if ((con instanceof HttpsURLConnection))
/*      */             {
/*  838 */               Certificate[] certs = ((HttpsURLConnection)con).getServerCertificates();
/*      */               
/*  840 */               if ((certs == null) || (certs.length == 0))
/*      */               {
/*  842 */                 logError("    No certificates returned");
/*      */               }
/*      */               else {
/*  845 */                 Certificate cert = certs[0];
/*      */                 
/*      */                 X509Certificate x509_cert;
/*      */                 X509Certificate x509_cert;
/*  849 */                 if ((cert instanceof X509Certificate))
/*      */                 {
/*  851 */                   x509_cert = (X509Certificate)cert;
/*      */                 }
/*      */                 else
/*      */                 {
/*  855 */                   CertificateFactory cf = CertificateFactory.getInstance("X.509");
/*      */                   
/*  857 */                   x509_cert = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(cert.getEncoded()));
/*      */                 }
/*      */                 
/*  860 */                 log("    Certificate: " + x509_cert.getSubjectDN());
/*      */               }
/*      */             }
/*      */             
/*  864 */             if (resp == 200)
/*      */             {
/*  866 */               logSuccess("    Connection result: " + con.getResponseCode() + "/" + con.getResponseMessage());
/*      */             }
/*      */             else
/*      */             {
/*  870 */               log("    Connection result: " + con.getResponseCode() + "/" + con.getResponseMessage());
/*      */             }
/*      */           }
/*      */           finally {
/*  874 */             proxy.setOK(true);
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/*  879 */         logError("    Failed: " + Debug.getNestedExceptionMessage(e));
/*  880 */         logError("    Check the logs for the 'Tor Helper Plugin' (Tools->Plugins->Log Views)");
/*      */       }
/*      */     }
/*      */     
/*  884 */     if (doTest(16))
/*      */     {
/*  886 */       log("Distributed protocol test");
/*      */       
/*  888 */       NetStatusProtocolTesterBT bt_test = this.plugin.getProtocolTester().runTest(new NetStatusProtocolTesterListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  893 */         private List sessions = new ArrayList();
/*      */         
/*      */ 
/*      */ 
/*      */         public void complete(NetStatusProtocolTesterBT tester)
/*      */         {
/*  899 */           log("Results", false);
/*      */           
/*  901 */           if (tester.getOutboundConnects() < 4)
/*      */           {
/*  903 */             log("    insufficient outbound connects for analysis", false);
/*      */             
/*  905 */             return;
/*      */           }
/*      */           
/*  908 */           int outgoing_seed_ok = 0;
/*  909 */           int outgoing_leecher_ok = 0;
/*  910 */           int outgoing_seed_bad = 0;
/*  911 */           int outgoing_leecher_bad = 0;
/*      */           
/*  913 */           int incoming_connect_ok = 0;
/*      */           
/*  915 */           for (int i = 0; i < this.sessions.size(); i++)
/*      */           {
/*  917 */             NetStatusProtocolTesterBT.Session session = (NetStatusProtocolTesterBT.Session)this.sessions.get(i);
/*      */             
/*  919 */             if (session.isOK())
/*      */             {
/*  921 */               if (session.isInitiator())
/*      */               {
/*  923 */                 if (session.isSeed())
/*      */                 {
/*  925 */                   outgoing_seed_ok++;
/*      */                 }
/*      */                 else
/*      */                 {
/*  929 */                   outgoing_leecher_ok++;
/*      */                 }
/*      */               }
/*      */               else {
/*  933 */                 incoming_connect_ok++;
/*      */               }
/*      */               
/*      */             }
/*  937 */             else if (session.isConnected())
/*      */             {
/*  939 */               if (session.isInitiator())
/*      */               {
/*  941 */                 if (session.isSeed())
/*      */                 {
/*  943 */                   outgoing_seed_bad++;
/*      */                 }
/*      */                 else
/*      */                 {
/*  947 */                   outgoing_leecher_bad++;
/*      */                 }
/*      */               }
/*      */               else {
/*  951 */                 incoming_connect_ok++;
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*  956 */             log("  " + (session.isInitiator() ? "Outbound" : "Inbound") + "," + (session.isSeed() ? "Seed" : "Leecher") + "," + session.getProtocolString(), false);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  962 */           boolean good = true;
/*      */           
/*  964 */           if (incoming_connect_ok == 0)
/*      */           {
/*  966 */             logError("  No incoming connections received, likely NAT problems");
/*      */             
/*  968 */             good = false;
/*      */           }
/*      */           
/*  971 */           if ((outgoing_leecher_ok > 0) && (outgoing_seed_ok == 0) && (outgoing_seed_bad > 0))
/*      */           {
/*      */ 
/*      */ 
/*  975 */             logError("  Outgoing seed connects appear to be failing while non-seeds succeed");
/*      */             
/*  977 */             good = false;
/*      */           }
/*      */           
/*  980 */           if (good)
/*      */           {
/*  982 */             NetStatusPluginTester.this.logSuccess("    Test successful");
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void sessionAdded(NetStatusProtocolTesterBT.Session session)
/*      */         {
/*  990 */           synchronized (this.sessions)
/*      */           {
/*  992 */             this.sessions.add(session);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void log(String str, boolean detailed)
/*      */         {
/* 1001 */           NetStatusPluginTester.this.log("  " + str, detailed);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void logError(String str)
/*      */         {
/* 1008 */           NetStatusPluginTester.this.logError("  " + str);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void logError(String str, Throwable e)
/*      */         {
/* 1016 */           NetStatusPluginTester.this.logError("  " + str, e);
/*      */         }
/*      */       });
/*      */       
/* 1020 */       while (!bt_test.waitForCompletion(5000L))
/*      */       {
/* 1022 */         if (isCancelled())
/*      */         {
/* 1024 */           bt_test.destroy();
/*      */           
/* 1026 */           break;
/*      */         }
/*      */         
/* 1029 */         log("    Status: " + bt_test.getStatus());
/*      */       }
/*      */     }
/*      */     
/* 1033 */     if (doTest(32))
/*      */     {
/* 1035 */       log("IPv6 test");
/*      */       
/* 1037 */       InetAddress ipv6_address = admin.getDefaultPublicAddressV6();
/*      */       
/* 1039 */       if (ipv6_address == null)
/*      */       {
/* 1041 */         log("    No default public IPv6 address found");
/*      */       }
/*      */       else
/*      */       {
/* 1045 */         log("    Default public IPv6 address: " + ipv6_address.getHostAddress());
/*      */         
/* 1047 */         log("    Testing connectivity...");
/*      */         
/* 1049 */         String res = VersionCheckClient.getSingleton().getExternalIpAddress(false, true, true);
/*      */         
/* 1051 */         if ((res != null) && (res.length() > 0))
/*      */         {
/* 1053 */           logSuccess("        Connect succeeded, reported IPv6 address: " + res);
/*      */         }
/*      */         else
/*      */         {
/* 1057 */           logError("        Connect failed");
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addPublicAddress(Set<InetAddress> addresses, InetAddress address)
/*      */   {
/* 1068 */     if (address == null)
/*      */     {
/* 1070 */       return;
/*      */     }
/*      */     
/* 1073 */     if ((address.isAnyLocalAddress()) || (address.isLoopbackAddress()) || (address.isLinkLocalAddress()) || (address.isSiteLocalAddress()))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1078 */       return;
/*      */     }
/*      */     
/* 1081 */     addresses.add(address);
/*      */   }
/*      */   
/*      */ 
/*      */   public void cancel()
/*      */   {
/* 1087 */     this.test_cancelled = true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isCancelled()
/*      */   {
/* 1093 */     return this.test_cancelled;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/* 1100 */     log(str, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(String str, boolean detailed)
/*      */   {
/* 1108 */     this.logger.log(str, detailed);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void logSuccess(String str)
/*      */   {
/* 1115 */     this.logger.logSuccess(str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void logInfo(String str)
/*      */   {
/* 1122 */     this.logger.logInfo(str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(String str, Throwable e)
/*      */   {
/* 1130 */     this.logger.log(str + ": " + e.getLocalizedMessage(), false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void logError(String str)
/*      */   {
/* 1137 */     this.logger.logFailure(str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void logError(String str, Throwable e)
/*      */   {
/* 1145 */     this.logger.logFailure(str + ": " + e.getLocalizedMessage());
/*      */   }
/*      */   
/*      */   public static abstract interface loggerProvider
/*      */   {
/*      */     public abstract void log(String paramString, boolean paramBoolean);
/*      */     
/*      */     public abstract void logSuccess(String paramString);
/*      */     
/*      */     public abstract void logInfo(String paramString);
/*      */     
/*      */     public abstract void logFailure(String paramString);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/netstatus/swt/NetStatusPluginTester.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */