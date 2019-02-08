/*      */ package com.aelitis.azureus.core.pairing.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.dht.DHT;
/*      */ import com.aelitis.azureus.core.dht.nat.DHTNATPuncher;
/*      */ import com.aelitis.azureus.core.dht.nat.DHTNATPuncherListener;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*      */ import com.aelitis.azureus.core.nat.NATTraversalHandler;
/*      */ import com.aelitis.azureus.core.nat.NATTraverser;
/*      */ import com.aelitis.azureus.core.pairing.PairedServiceRequestHandler;
/*      */ import com.aelitis.azureus.core.security.CryptoManager;
/*      */ import com.aelitis.azureus.core.security.CryptoManager.SRPParameters;
/*      */ import com.aelitis.azureus.core.security.CryptoManagerFactory;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPlugin;
/*      */ import com.aelitis.azureus.util.JSONUtils;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.math.BigInteger;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.URL;
/*      */ import java.security.AlgorithmParameters;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import javax.crypto.BadPaddingException;
/*      */ import javax.crypto.Cipher;
/*      */ import javax.crypto.IllegalBlockSizeException;
/*      */ import javax.crypto.spec.IvParameterSpec;
/*      */ import javax.crypto.spec.SecretKeySpec;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageRequest;
/*      */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageResponse;
/*      */ import org.gudy.bouncycastle.crypto.agreement.srp.SRP6Server;
/*      */ import org.gudy.bouncycastle.crypto.agreement.srp.SRP6VerifierGenerator;
/*      */ import org.gudy.bouncycastle.crypto.digests.SHA256Digest;
/*      */ import org.json.simple.JSONObject;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class PairingManagerTunnelHandler
/*      */ {
/*      */   private static final String DEFAULT_IDENTITY = "vuze";
/*      */   private BigInteger N_3072;
/*      */   private BigInteger G_3072;
/*      */   private byte[] SRP_SALT;
/*      */   private BigInteger SRP_VERIFIER;
/*      */   final PairingManagerImpl manager;
/*      */   private final AzureusCore core;
/*   89 */   private boolean started = false;
/*   90 */   private boolean active = false;
/*      */   
/*   92 */   private final List<DHTNATPuncher> nat_punchers_ipv4 = new ArrayList();
/*   93 */   private final List<DHTNATPuncher> nat_punchers_ipv6 = new ArrayList();
/*      */   
/*   95 */   private int last_punchers_registered = 0;
/*      */   
/*      */ 
/*      */   private TimerEvent update_event;
/*      */   
/*  100 */   private final Map<String, Object[]> local_server_map = new LinkedHashMap(10, 0.75F, true)
/*      */   {
/*      */ 
/*      */ 
/*      */     protected boolean removeEldestEntry(Map.Entry<String, Object[]> eldest)
/*      */     {
/*      */ 
/*  107 */       return size() > 10;
/*      */     }
/*      */   };
/*      */   
/*      */   private long last_server_create_time;
/*      */   
/*      */   private long last_server_agree_time;
/*      */   
/*      */   private int total_servers;
/*      */   
/*      */   private long last_local_server_create_time;
/*      */   private long last_local_server_agree_time;
/*      */   private int total_local_servers;
/*      */   private static final int MAX_TUNNELS = 10;
/*  121 */   final Map<String, PairManagerTunnel> tunnels = new HashMap();
/*      */   
/*      */ 
/*      */ 
/*      */   private String init_fail;
/*      */   
/*      */ 
/*      */ 
/*      */   protected PairingManagerTunnelHandler(PairingManagerImpl _manager, AzureusCore _core)
/*      */   {
/*  131 */     this.manager = _manager;
/*  132 */     this.core = _core;
/*      */     
/*  134 */     CryptoManager.SRPParameters params = CryptoManagerFactory.getSingleton().getSRPParameters();
/*      */     
/*  136 */     if (params != null)
/*      */     {
/*  138 */       this.SRP_SALT = params.getSalt();
/*  139 */       this.SRP_VERIFIER = params.getVerifier();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSRPPassword(char[] password)
/*      */   {
/*  147 */     if ((password == null) || (password.length == 0))
/*      */     {
/*  149 */       this.SRP_SALT = null;
/*  150 */       this.SRP_VERIFIER = null;
/*      */       
/*  152 */       CryptoManagerFactory.getSingleton().setSRPParameters(null, null);
/*      */     }
/*      */     else
/*      */     {
/*  156 */       start();
/*      */       try
/*      */       {
/*  159 */         byte[] I = "vuze".getBytes("UTF-8");
/*  160 */         byte[] P = new String(password).getBytes("UTF-8");
/*      */         
/*  162 */         byte[] salt = new byte[16];
/*      */         
/*  164 */         RandomUtils.nextSecureBytes(salt);
/*      */         
/*  166 */         SRP6VerifierGenerator gen = new SRP6VerifierGenerator();
/*      */         
/*  168 */         gen.init(this.N_3072, this.G_3072, new SHA256Digest());
/*      */         
/*  170 */         BigInteger verifier = gen.generateVerifier(salt, I, P);
/*      */         
/*  172 */         CryptoManagerFactory.getSingleton().setSRPParameters(salt, verifier);
/*      */         
/*  174 */         this.SRP_SALT = salt;
/*  175 */         this.SRP_VERIFIER = verifier;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  179 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/*  183 */     updateActive();
/*      */   }
/*      */   
/*      */ 
/*      */   private void start()
/*      */   {
/*  189 */     synchronized (this)
/*      */     {
/*  191 */       if (this.started)
/*      */       {
/*  193 */         return;
/*      */       }
/*      */       
/*  196 */       this.started = true;
/*      */     }
/*      */     
/*  199 */     this.N_3072 = fromHex("FFFFFFFF FFFFFFFF C90FDAA2 2168C234 C4C6628B 80DC1CD1 29024E088A67CC74 020BBEA6 3B139B22 514A0879 8E3404DD EF9519B3 CD3A431B302B0A6D F25F1437 4FE1356D 6D51C245 E485B576 625E7EC6 F44C42E9A637ED6B 0BFF5CB6 F406B7ED EE386BFB 5A899FA5 AE9F2411 7C4B1FE649286651 ECE45B3D C2007CB8 A163BF05 98DA4836 1C55D39A 69163FA8FD24CF5F 83655D23 DCA3AD96 1C62F356 208552BB 9ED52907 7096966D670C354E 4ABC9804 F1746C08 CA18217C 32905E46 2E36CE3B E39E772C180E8603 9B2783A2 EC07A28F B5C55DF0 6F4C52C9 DE2BCBF6 955817183995497C EA956AE5 15D22618 98FA0510 15728E5A 8AAAC42D AD33170D04507A33 A85521AB DF1CBA64 ECFB8504 58DBEF0A 8AEA7157 5D060C7DB3970F85 A6E1E4C7 ABF5AE8C DB0933D7 1E8C94E0 4A25619D CEE3D2261AD2EE6B F12FFA06 D98A0864 D8760273 3EC86A64 521F2B18 177B200CBBE11757 7A615D6C 770988C0 BAD946E2 08E24FA0 74E5AB31 43DB5BFCE0FD108E 4B82D120 A93AD2CA FFFFFFFF FFFFFFFF");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  215 */     this.G_3072 = BigInteger.valueOf(5L);
/*      */     try
/*      */     {
/*  218 */       PluginInterface dht_pi = this.core.getPluginManager().getPluginInterfaceByClass(DHTPlugin.class);
/*      */       
/*  220 */       if (dht_pi == null)
/*      */       {
/*  222 */         throw new Exception("DHT Plugin not found");
/*      */       }
/*      */       
/*  225 */       DHTPlugin dht_plugin = (DHTPlugin)dht_pi.getPlugin();
/*      */       
/*  227 */       if (!dht_plugin.isEnabled())
/*      */       {
/*  229 */         throw new Exception("DHT Plugin is disabled");
/*      */       }
/*      */       
/*      */ 
/*  233 */       DHT[] dhts = dht_plugin.getDHTs();
/*      */       
/*  235 */       List<DHTNATPuncher> punchers = new ArrayList();
/*      */       
/*  237 */       for (DHT dht : dhts)
/*      */       {
/*  239 */         int net = dht.getTransport().getNetwork();
/*      */         
/*  241 */         if (net == 0)
/*      */         {
/*  243 */           DHTNATPuncher primary_puncher = dht.getNATPuncher();
/*      */           
/*  245 */           if (primary_puncher != null)
/*      */           {
/*  247 */             punchers.add(primary_puncher);
/*      */             
/*  249 */             this.nat_punchers_ipv4.add(primary_puncher);
/*      */             
/*  251 */             for (int i = 1; i <= 2; i++)
/*      */             {
/*  253 */               DHTNATPuncher puncher = primary_puncher.getSecondaryPuncher();
/*      */               
/*  255 */               punchers.add(puncher);
/*      */               
/*  257 */               this.nat_punchers_ipv4.add(puncher);
/*      */             }
/*      */           }
/*  260 */         } else if (net != 3) {}
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  283 */       if (punchers.size() == 0)
/*      */       {
/*  285 */         throw new Exception("No suitable DHT instances available");
/*      */       }
/*      */       
/*  288 */       for (DHTNATPuncher p : punchers)
/*      */       {
/*  290 */         p.forceActive(true);
/*      */         
/*  292 */         p.addListener(new DHTNATPuncherListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void rendezvousChanged(DHTTransportContact rendezvous)
/*      */           {
/*      */ 
/*  299 */             System.out.println("active: " + rendezvous.getString());
/*      */             
/*  301 */             synchronized (PairingManagerTunnelHandler.this)
/*      */             {
/*  303 */               if (PairingManagerTunnelHandler.this.update_event == null)
/*      */               {
/*  305 */                 PairingManagerTunnelHandler.this.update_event = SimpleTimer.addEvent("PMT:defer", SystemTime.getOffsetTime(15000L), new TimerEventPerformer()
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */                   public void perform(TimerEvent event)
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*  315 */                     synchronized (PairingManagerTunnelHandler.this)
/*      */                     {
/*  317 */                       PairingManagerTunnelHandler.this.update_event = null;
/*      */                     }
/*      */                     
/*  320 */                     System.out.println("    updating");
/*      */                     
/*  322 */                     PairingManagerTunnelHandler.this.manager.updateNeeded();
/*      */                   }
/*      */                 });
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*  331 */       this.core.getNATTraverser().registerHandler(new NATTraversalHandler()
/*      */       {
/*      */ 
/*  334 */         private final Map<Long, Object[]> server_map = new LinkedHashMap(10, 0.75F, true)
/*      */         {
/*      */ 
/*      */ 
/*      */           protected boolean removeEldestEntry(Map.Entry<Long, Object[]> eldest)
/*      */           {
/*      */ 
/*  341 */             return size() > 10;
/*      */           }
/*      */         };
/*      */         
/*      */ 
/*      */         public int getType()
/*      */         {
/*  348 */           return 3;
/*      */         }
/*      */         
/*      */ 
/*      */         public String getName()
/*      */         {
/*  354 */           return "Pairing Tunnel";
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public Map process(InetSocketAddress originator, Map data)
/*      */         {
/*  362 */           if ((PairingManagerTunnelHandler.this.SRP_VERIFIER == null) || (!PairingManagerTunnelHandler.this.active))
/*      */           {
/*  364 */             return null;
/*      */           }
/*      */           
/*  367 */           boolean good_request = false;
/*      */           
/*      */           try
/*      */           {
/*  371 */             Map result = new HashMap();
/*      */             
/*  373 */             Long session = (Long)data.get("sid");
/*      */             
/*  375 */             if (session == null)
/*      */             {
/*  377 */               return null;
/*      */             }
/*      */             
/*      */             InetAddress tunnel_originator;
/*      */             try
/*      */             {
/*  383 */               tunnel_originator = InetAddress.getByAddress((byte[])data.get("origin"));
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  387 */               Debug.out("originator decode failed: " + data);
/*      */               
/*  389 */               return null;
/*      */             }
/*      */             
/*  392 */             System.out.println("PairManagerTunnelHander: incoming message - session=" + session + ", payload=" + data + " from " + tunnel_originator + " via " + originator);
/*      */             
/*      */             SRP6Server server;
/*      */             
/*      */             BigInteger B;
/*  397 */             synchronized (this.server_map)
/*      */             {
/*  399 */               Object[] entry = (Object[])this.server_map.get(session);
/*      */               
/*  401 */               if (entry == null)
/*      */               {
/*  403 */                 long diff = SystemTime.getMonotonousTime() - PairingManagerTunnelHandler.this.last_server_create_time;
/*      */                 
/*  405 */                 if (diff < 5000L) {
/*      */                   try
/*      */                   {
/*  408 */                     long sleep = 5000L - diff;
/*      */                     
/*  410 */                     System.out.println("Sleeping for " + sleep + " before starting srp");
/*      */                     
/*  412 */                     Thread.sleep(sleep);
/*      */                   }
/*      */                   catch (Throwable e) {}
/*      */                 }
/*      */                 
/*      */ 
/*  418 */                 SRP6Server server = new SRP6Server();
/*      */                 
/*  420 */                 server.init(PairingManagerTunnelHandler.this.N_3072, PairingManagerTunnelHandler.this.G_3072, PairingManagerTunnelHandler.this.SRP_VERIFIER, new SHA256Digest(), RandomUtils.SECURE_RANDOM);
/*      */                 
/*  422 */                 BigInteger B = server.generateServerCredentials();
/*      */                 
/*  424 */                 this.server_map.put(session, new Object[] { server, B });
/*      */                 
/*  426 */                 PairingManagerTunnelHandler.this.last_server_create_time = SystemTime.getMonotonousTime();
/*      */                 
/*  428 */                 PairingManagerTunnelHandler.access$608(PairingManagerTunnelHandler.this);
/*      */               }
/*      */               else
/*      */               {
/*  432 */                 server = (SRP6Server)entry[0];
/*  433 */                 B = (BigInteger)entry[1];
/*      */               }
/*      */             }
/*      */             
/*  437 */             Long op = (Long)data.get("op");
/*      */             boolean log_error;
/*  439 */             if (op.longValue() == 1L)
/*      */             {
/*  441 */               result.put("op", Integer.valueOf(2));
/*      */               
/*  443 */               result.put("s", PairingManagerTunnelHandler.this.SRP_SALT);
/*      */               
/*  445 */               result.put("b", B.toByteArray());
/*      */               
/*  447 */               good_request = true;
/*      */               
/*  449 */               if (data.containsKey("test"))
/*      */               {
/*  451 */                 PairingManagerTunnelHandler.this.manager.recordRequest("SRP Test", originator.getAddress().getHostAddress(), true);
/*      */               }
/*      */             }
/*  454 */             else if (op.longValue() == 3L)
/*      */             {
/*  456 */               log_error = true;
/*      */               try
/*      */               {
/*  459 */                 long diff = SystemTime.getMonotonousTime() - PairingManagerTunnelHandler.this.last_server_agree_time;
/*      */                 
/*  461 */                 if (diff < 5000L) {
/*      */                   try
/*      */                   {
/*  464 */                     long sleep = 5000L - diff;
/*      */                     
/*  466 */                     System.out.println("Sleeping for " + sleep + " before completing srp");
/*      */                     
/*  468 */                     Thread.sleep(sleep);
/*      */                   }
/*      */                   catch (Throwable e) {}
/*      */                 }
/*      */                 
/*      */ 
/*  474 */                 BigInteger A = new BigInteger((byte[])data.get("a"));
/*      */                 
/*  476 */                 BigInteger serverS = server.calculateSecret(A);
/*      */                 
/*  478 */                 byte[] shared_secret = serverS.toByteArray();
/*      */                 
/*  480 */                 Cipher decipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
/*      */                 
/*  482 */                 byte[] key = new byte[16];
/*      */                 
/*  484 */                 System.arraycopy(shared_secret, 0, key, 0, 16);
/*      */                 
/*  486 */                 SecretKeySpec secret = new SecretKeySpec(key, "AES");
/*      */                 
/*  488 */                 decipher.init(2, secret, new IvParameterSpec((byte[])data.get("enc_iv")));
/*      */                 
/*  490 */                 byte[] dec = decipher.doFinal((byte[])data.get("enc_data"));
/*      */                 
/*  492 */                 String json_str = new String(dec, "UTF-8");
/*      */                 
/*  494 */                 if (!json_str.startsWith("{"))
/*      */                 {
/*  496 */                   log_error = false;
/*      */                   
/*  498 */                   throw new Exception("decode failed");
/*      */                 }
/*      */                 
/*  501 */                 JSONObject dec_json = (JSONObject)JSONUtils.decodeJSON(json_str);
/*      */                 
/*  503 */                 String tunnel_url = (String)dec_json.get("url");
/*      */                 
/*  505 */                 String service_id = new String((byte[])data.get("service"), "UTF-8");
/*      */                 
/*  507 */                 String endpoint_url = (String)dec_json.get("endpoint");
/*      */                 
/*  509 */                 boolean ok = PairingManagerTunnelHandler.this.createTunnel(tunnel_originator, session.longValue(), service_id, secret, tunnel_url, endpoint_url);
/*      */                 
/*  511 */                 result.put("op", Integer.valueOf(4));
/*  512 */                 result.put("status", ok ? "ok" : "failed");
/*      */                 
/*  514 */                 good_request = true;
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  518 */                 result.put("op", Integer.valueOf(4));
/*  519 */                 result.put("status", "failed");
/*      */                 
/*      */ 
/*      */ 
/*  523 */                 if (((e instanceof BadPaddingException)) || ((e instanceof IllegalBlockSizeException)))
/*      */                 {
/*      */ 
/*  526 */                   log_error = false;
/*      */                 }
/*      */                 
/*  529 */                 if (log_error)
/*      */                 {
/*  531 */                   e.printStackTrace();
/*      */                 }
/*      */               }
/*      */               finally {
/*  535 */                 PairingManagerTunnelHandler.this.last_server_agree_time = SystemTime.getMonotonousTime();
/*      */               }
/*      */             }
/*      */             
/*  539 */             return result;
/*      */           }
/*      */           finally
/*      */           {
/*  543 */             if (!good_request)
/*      */             {
/*  545 */               PairingManagerTunnelHandler.this.manager.recordRequest("SRP", originator.getAddress().getHostAddress(), false);
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*  550 */       });
/*  551 */       SimpleTimer.addPeriodicEvent("pm:tunnel:stats", 30000L, new TimerEventPerformer()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void perform(TimerEvent event)
/*      */         {
/*      */ 
/*      */ 
/*  560 */           synchronized (PairingManagerTunnelHandler.this.tunnels)
/*      */           {
/*  562 */             if (PairingManagerTunnelHandler.this.tunnels.size() > 0)
/*      */             {
/*  564 */               System.out.println("PairTunnels: " + PairingManagerTunnelHandler.this.tunnels.size());
/*      */               
/*  566 */               for (PairManagerTunnel t : PairingManagerTunnelHandler.this.tunnels.values())
/*      */               {
/*  568 */                 System.out.println("\t" + t.getString());
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  577 */       Debug.out(e);
/*      */       
/*  579 */       this.init_fail = Debug.getNestedExceptionMessage(e);
/*      */       
/*  581 */       this.manager.updateSRPState();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getStatus()
/*      */   {
/*  588 */     if (this.init_fail != null)
/*      */     {
/*  590 */       return MessageText.getString("MyTorrentsView.menu.setSpeed.disabled") + ": " + this.init_fail;
/*      */     }
/*  592 */     if (!this.active)
/*      */     {
/*  594 */       return MessageText.getString("pairing.status.initialising") + "...";
/*      */     }
/*  596 */     if (this.SRP_SALT == null)
/*      */     {
/*  598 */       return MessageText.getString("pairing.srp.pw.req");
/*      */     }
/*  600 */     if (this.last_punchers_registered == 0)
/*      */     {
/*  602 */       return MessageText.getString("pairing.srp.registering");
/*      */     }
/*      */     
/*      */ 
/*  606 */     return MessageText.getString("tps.status.available");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setActive(boolean a)
/*      */   {
/*  614 */     synchronized (this)
/*      */     {
/*  616 */       if (this.active == a)
/*      */       {
/*  618 */         return;
/*      */       }
/*      */       
/*  621 */       this.active = a;
/*      */     }
/*      */     
/*  624 */     updateActive();
/*      */   }
/*      */   
/*      */ 
/*      */   private void updateActive()
/*      */   {
/*  630 */     this.manager.updateSRPState();
/*      */     
/*  632 */     if ((this.active) && (this.SRP_VERIFIER != null))
/*      */     {
/*  634 */       start();
/*      */     }
/*      */     else
/*      */     {
/*  638 */       synchronized (this.tunnels)
/*      */       {
/*  640 */         for (PairManagerTunnel t : new ArrayList(this.tunnels.values()))
/*      */         {
/*  642 */           t.destroy();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  647 */       synchronized (this.local_server_map)
/*      */       {
/*  649 */         this.local_server_map.clear();
/*      */       }
/*      */     }
/*      */     
/*  653 */     List<DHTNATPuncher> punchers = new ArrayList();
/*      */     
/*  655 */     punchers.addAll(this.nat_punchers_ipv4);
/*  656 */     punchers.addAll(this.nat_punchers_ipv6);
/*      */     
/*  658 */     for (DHTNATPuncher p : punchers)
/*      */     {
/*  660 */       p.forceActive(this.active);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updateRegistrationData(Map<String, Object> payload)
/*      */   {
/*  668 */     int puncher_num = 0;
/*      */     
/*  670 */     int num_registered = 0;
/*      */     
/*  672 */     for (DHTNATPuncher nat_ipv4 : this.nat_punchers_ipv4)
/*      */     {
/*  674 */       DHTTransportContact rend = nat_ipv4.getRendezvous();
/*  675 */       DHTTransportContact lc = nat_ipv4.getLocalContact();
/*      */       
/*  677 */       if ((rend != null) && (lc != null))
/*      */       {
/*  679 */         puncher_num++;
/*      */         
/*  681 */         InetSocketAddress rend_address = rend.getTransportAddress();
/*      */         
/*  683 */         num_registered++;
/*      */         
/*  685 */         payload.put("rc_v4-" + puncher_num, rend_address.getAddress().getHostAddress() + ":" + rend_address.getPort());
/*      */         
/*  687 */         if (puncher_num == 1)
/*      */         {
/*  689 */           payload.put("rl_v4", lc.getExternalAddress().getAddress().getHostAddress() + ":" + lc.getAddress().getPort());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  694 */     puncher_num = 0;
/*      */     
/*  696 */     for (DHTNATPuncher nat_ipv6 : this.nat_punchers_ipv6)
/*      */     {
/*  698 */       DHTTransportContact rend = nat_ipv6.getRendezvous();
/*  699 */       DHTTransportContact lc = nat_ipv6.getLocalContact();
/*      */       
/*  701 */       if ((rend != null) && (lc != null))
/*      */       {
/*  703 */         puncher_num++;
/*      */         
/*  705 */         InetSocketAddress rend_address = rend.getTransportAddress();
/*      */         
/*  707 */         num_registered++;
/*      */         
/*  709 */         payload.put("rc_v6-" + puncher_num, rend_address.getAddress().getHostAddress() + ":" + rend_address.getPort());
/*      */         
/*  711 */         if (puncher_num == 1)
/*      */         {
/*  713 */           payload.put("rl_v6", lc.getExternalAddress().getAddress().getHostAddress() + ":" + lc.getAddress().getPort());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  718 */     if (num_registered != this.last_punchers_registered)
/*      */     {
/*  720 */       this.last_punchers_registered = num_registered;
/*      */       
/*  722 */       this.manager.updateSRPState();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private BigInteger fromHex(String hex)
/*      */   {
/*  730 */     return new BigInteger(1, ByteFormatter.decodeString(hex.replaceAll(" ", "")));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean handleLocalTunnel(TrackerWebPageRequest request, TrackerWebPageResponse response)
/*      */     throws IOException
/*      */   {
/*  740 */     start();
/*      */     
/*  742 */     if ((this.SRP_VERIFIER == null) || (!this.active))
/*      */     {
/*  744 */       throw new IOException("Secure pairing is not enabled");
/*      */     }
/*      */     
/*  747 */     boolean good_request = false;
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  752 */       String url = request.getURL().substring(16);
/*      */       
/*  754 */       int q_pos = url.indexOf('?');
/*      */       
/*  756 */       Map<String, String> args = new HashMap();
/*      */       
/*  758 */       if (q_pos != -1)
/*      */       {
/*  760 */         String args_str = url.substring(q_pos + 1);
/*      */         
/*  762 */         String[] bits = args_str.split("&");
/*      */         
/*  764 */         for (String arg : bits)
/*      */         {
/*  766 */           String[] x = arg.split("=");
/*      */           
/*  768 */           if (x.length == 2)
/*      */           {
/*  770 */             args.put(x[0].toLowerCase(), x[1]);
/*      */           }
/*      */         }
/*      */         
/*  774 */         url = url.substring(0, q_pos);
/*      */       }
/*      */       String abs_url;
/*  777 */       if (url.startsWith("create"))
/*      */       {
/*  779 */         String ac = (String)args.get("ac");
/*  780 */         String sid = (String)args.get("sid");
/*      */         
/*  782 */         if ((ac == null) || (sid == null))
/*      */         {
/*  784 */           throw new IOException("Access code or service id missing");
/*      */         }
/*      */         
/*  787 */         if (!ac.equals(this.manager.peekAccessCode()))
/*      */         {
/*  789 */           throw new IOException("Invalid access code");
/*      */         }
/*      */         
/*  792 */         PairingManagerImpl.PairedServiceImpl ps = this.manager.getService(sid);
/*      */         
/*  794 */         if (ps == null)
/*      */         {
/*  796 */           good_request = true;
/*      */           
/*  798 */           throw new IOException("Service '" + sid + "' not registered");
/*      */         }
/*      */         
/*  801 */         PairedServiceRequestHandler handler = ps.getHandler();
/*      */         
/*  803 */         if (handler == null)
/*      */         {
/*  805 */           good_request = true;
/*      */           
/*  807 */           throw new IOException("Service '" + sid + "' has no handler registered");
/*      */         }
/*      */         
/*  810 */         JSONObject json = new JSONObject();
/*      */         
/*  812 */         JSONObject result = new JSONObject();
/*      */         
/*  814 */         json.put("result", result);
/*      */         
/*  816 */         byte[] ss = { this.SRP_SALT[0], this.SRP_SALT[1], this.SRP_SALT[2], this.SRP_SALT[3] };
/*      */         
/*  818 */         long tunnel_id = RandomUtils.nextSecureAbsoluteLong();
/*      */         
/*  820 */         String tunnel_name = Base32.encode(ss) + "_" + tunnel_id;
/*      */         
/*  822 */         synchronized (this.local_server_map)
/*      */         {
/*  824 */           long diff = SystemTime.getMonotonousTime() - this.last_local_server_create_time;
/*      */           
/*  826 */           if (diff < 5000L) {
/*      */             try
/*      */             {
/*  829 */               long sleep = 5000L - diff;
/*      */               
/*  831 */               System.out.println("Sleeping for " + sleep + " before starting srp");
/*      */               
/*  833 */               Thread.sleep(sleep);
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/*      */           
/*      */ 
/*  839 */           SRP6Server server = new SRP6Server();
/*      */           
/*  841 */           server.init(this.N_3072, this.G_3072, this.SRP_VERIFIER, new SHA256Digest(), RandomUtils.SECURE_RANDOM);
/*      */           
/*  843 */           BigInteger B = server.generateServerCredentials();
/*      */           
/*  845 */           this.local_server_map.put(tunnel_name, new Object[] { server, handler, null, null });
/*      */           
/*  847 */           this.last_local_server_create_time = SystemTime.getMonotonousTime();
/*      */           
/*  849 */           this.total_local_servers += 1;
/*      */           
/*  851 */           result.put("srp_salt", Base32.encode(this.SRP_SALT));
/*      */           
/*  853 */           result.put("srp_b", Base32.encode(B.toByteArray()));
/*      */           
/*  855 */           Map<String, String> headers = request.getHeaders();
/*      */           
/*  857 */           String host = (String)headers.get("host");
/*      */           
/*      */ 
/*      */ 
/*  861 */           int pos = host.lastIndexOf("]");
/*      */           
/*  863 */           if (pos != -1)
/*      */           {
/*      */ 
/*      */ 
/*  867 */             host = host.substring(0, pos + 1);
/*      */           }
/*      */           else
/*      */           {
/*  871 */             pos = host.indexOf(':');
/*      */             
/*  873 */             if (pos != -1)
/*      */             {
/*  875 */               host = host.substring(0, pos);
/*      */             }
/*      */           }
/*      */           
/*  879 */           abs_url = request.getAbsoluteURL().toString();
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  884 */           abs_url = UrlUtils.setHost(new URL(abs_url), host).toExternalForm();
/*      */           
/*  886 */           pos = abs_url.indexOf("/create");
/*      */           
/*  888 */           String tunnel_url = abs_url.substring(0, pos) + "/id/" + tunnel_name;
/*      */           
/*  890 */           result.put("url", tunnel_url);
/*      */         }
/*      */         
/*  893 */         response.getOutputStream().write(JSONUtils.encodeToJSON(json).getBytes("UTF-8"));
/*      */         
/*  895 */         response.setContentType("application/json; charset=UTF-8");
/*      */         
/*  897 */         response.setGZIP(true);
/*      */         
/*  899 */         good_request = true;
/*      */         
/*  901 */         return (boolean)1;
/*      */       }
/*  903 */       if (url.startsWith("id/"))
/*      */       {
/*  905 */         String tunnel_name = url.substring(3);
/*      */         
/*      */         Object[] entry;
/*      */         
/*  909 */         synchronized (this.local_server_map)
/*      */         {
/*  911 */           entry = (Object[])this.local_server_map.get(tunnel_name);
/*      */           
/*  913 */           if (entry == null)
/*      */           {
/*  915 */             good_request = true;
/*      */             
/*  917 */             throw new IOException("Unknown tunnel id");
/*      */           }
/*      */         }
/*      */         
/*  921 */         String srp_a = (String)args.get("srp_a");
/*  922 */         String enc_data = (String)args.get("enc_data");
/*  923 */         String enc_iv = (String)args.get("enc_iv");
/*      */         String tunnel_url;
/*  925 */         if ((srp_a != null) && (enc_data != null) && (enc_iv != null))
/*      */           try
/*      */           {
/*  928 */             synchronized (this.local_server_map)
/*      */             {
/*  930 */               long diff = SystemTime.getMonotonousTime() - this.last_local_server_agree_time;
/*      */               
/*  932 */               if (diff < 5000L) {
/*      */                 try
/*      */                 {
/*  935 */                   long sleep = 5000L - diff;
/*      */                   
/*  937 */                   System.out.println("Sleeping for " + sleep + " before completing srp");
/*      */                   
/*  939 */                   Thread.sleep(sleep);
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*  946 */             JSONObject json = new JSONObject();
/*      */             
/*  948 */             JSONObject result = new JSONObject();
/*      */             
/*  950 */             json.put("result", result);
/*      */             
/*  952 */             SRP6Server server = (SRP6Server)entry[0];
/*      */             
/*  954 */             BigInteger A = new BigInteger(Base32.decode(srp_a));
/*      */             
/*  956 */             BigInteger serverS = server.calculateSecret(A);
/*      */             
/*  958 */             byte[] shared_secret = serverS.toByteArray();
/*      */             
/*  960 */             Cipher decipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
/*      */             
/*  962 */             byte[] key = new byte[16];
/*      */             
/*  964 */             System.arraycopy(shared_secret, 0, key, 0, 16);
/*      */             
/*  966 */             SecretKeySpec secret = new SecretKeySpec(key, "AES");
/*      */             
/*  968 */             decipher.init(2, secret, new IvParameterSpec(Base32.decode(enc_iv)));
/*      */             
/*  970 */             byte[] dec = decipher.doFinal(Base32.decode(enc_data));
/*      */             
/*  972 */             JSONObject dec_json = (JSONObject)JSONUtils.decodeJSON(new String(dec, "UTF-8"));
/*      */             
/*  974 */             tunnel_url = (String)dec_json.get("url");
/*      */             
/*  976 */             if (!tunnel_url.contains(tunnel_name))
/*      */             {
/*  978 */               throw new IOException("Invalid tunnel url");
/*      */             }
/*      */             
/*  981 */             String endpoint_url = (String)dec_json.get("endpoint");
/*      */             
/*  983 */             entry[2] = secret;
/*  984 */             entry[3] = endpoint_url;
/*      */             
/*  986 */             result.put("state", "activated");
/*      */             
/*  988 */             response.getOutputStream().write(JSONUtils.encodeToJSON(json).getBytes("UTF-8"));
/*      */             
/*  990 */             response.setContentType("application/json; charset=UTF-8");
/*      */             
/*  992 */             response.setGZIP(true);
/*      */             
/*  994 */             good_request = true;
/*      */             
/*  996 */             abs_url = 1;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1004 */             this.last_local_server_agree_time = SystemTime.getMonotonousTime();
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1086 */             return abs_url;
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1000 */             throw new IOException(Debug.getNestedExceptionMessage(e));
/*      */           }
/*      */           finally
/*      */           {
/* 1004 */             this.last_local_server_agree_time = SystemTime.getMonotonousTime();
/*      */           }
/* 1006 */         if (args.containsKey("close"))
/*      */         {
/* 1008 */           synchronized (this.local_server_map)
/*      */           {
/* 1010 */             this.local_server_map.remove(tunnel_name);
/*      */           }
/*      */           
/* 1013 */           good_request = true;
/*      */           
/* 1015 */           return (boolean)1;
/*      */         }
/*      */         
/*      */ 
/* 1019 */         PairedServiceRequestHandler request_handler = (PairedServiceRequestHandler)entry[1];
/*      */         
/* 1021 */         SecretKeySpec secret = (SecretKeySpec)entry[2];
/*      */         
/* 1023 */         String endpoint_url = (String)entry[3];
/*      */         
/* 1025 */         if (secret == null)
/*      */         {
/* 1027 */           throw new IOException("auth not completed");
/*      */         }
/*      */         
/* 1030 */         byte[] request_data = FileUtil.readInputStreamAsByteArray(request.getInputStream());
/*      */         
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/* 1036 */           byte[] IV = new byte[16];
/*      */           
/* 1038 */           System.arraycopy(request_data, 0, IV, 0, IV.length);
/*      */           
/* 1040 */           Cipher decipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
/*      */           
/* 1042 */           decipher.init(2, secret, new IvParameterSpec(IV));
/*      */           
/* 1044 */           byte[] decrypted = decipher.doFinal(request_data, 16, request_data.length - 16);
/*      */           
/*      */ 
/* 1047 */           byte[] reply_bytes = request_handler.handleRequest(request.getClientAddress2().getAddress(), endpoint_url, decrypted);
/*      */           
/*      */ 
/* 1050 */           Cipher encipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
/*      */           
/* 1052 */           encipher.init(1, secret);
/*      */           
/* 1054 */           AlgorithmParameters params = encipher.getParameters();
/*      */           
/* 1056 */           byte[] IV = ((IvParameterSpec)params.getParameterSpec(IvParameterSpec.class)).getIV();
/*      */           
/* 1058 */           byte[] enc = encipher.doFinal(reply_bytes);
/*      */           
/* 1060 */           byte[] rep_bytes = new byte[IV.length + enc.length];
/*      */           
/* 1062 */           System.arraycopy(IV, 0, rep_bytes, 0, IV.length);
/* 1063 */           System.arraycopy(enc, 0, rep_bytes, IV.length, enc.length);
/*      */           
/* 1065 */           response.getOutputStream().write(rep_bytes);
/*      */           
/* 1067 */           response.setContentType("application/octet-stream");
/*      */           
/* 1069 */           good_request = true;
/*      */           
/* 1071 */           return 1;
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1075 */           throw new IOException(Debug.getNestedExceptionMessage(e));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1080 */       throw new IOException("Unknown tunnel operation");
/*      */     }
/*      */     finally
/*      */     {
/* 1084 */       if (!good_request)
/*      */       {
/* 1086 */         this.manager.recordRequest("SRP", request.getClientAddress2().getAddress().getHostAddress(), false);
/*      */       }
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
/*      */   private boolean createTunnel(InetAddress originator, long session, String sid, SecretKeySpec secret, String tunnel_url, String endpoint_url)
/*      */   {
/* 1100 */     PairingManagerImpl.PairedServiceImpl ps = this.manager.getService(sid);
/*      */     
/* 1102 */     if (ps == null)
/*      */     {
/* 1104 */       Debug.out("Service '" + sid + "' not registered");
/*      */       
/* 1106 */       return false;
/*      */     }
/*      */     
/* 1109 */     PairedServiceRequestHandler handler = ps.getHandler();
/*      */     
/* 1111 */     if (handler == null)
/*      */     {
/* 1113 */       Debug.out("Service '" + sid + "' has no handler registered");
/*      */       
/* 1115 */       return false;
/*      */     }
/*      */     
/* 1118 */     String key = originator.getHostAddress() + ":" + session + ":" + sid;
/*      */     
/* 1120 */     synchronized (this.tunnels)
/*      */     {
/* 1122 */       PairManagerTunnel existing = (PairManagerTunnel)this.tunnels.get(key);
/*      */       
/* 1124 */       if (existing != null)
/*      */       {
/* 1126 */         return true;
/*      */       }
/*      */       
/* 1129 */       if (this.tunnels.size() > 10)
/*      */       {
/* 1131 */         long oldest_active = Long.MAX_VALUE;
/* 1132 */         PairManagerTunnel oldest_tunnel = null;
/*      */         
/* 1134 */         for (PairManagerTunnel t : this.tunnels.values())
/*      */         {
/* 1136 */           long at = t.getLastActive();
/*      */           
/* 1138 */           if (at < oldest_active)
/*      */           {
/* 1140 */             oldest_active = at;
/* 1141 */             oldest_tunnel = t;
/*      */           }
/*      */         }
/*      */         
/* 1145 */         oldest_tunnel.destroy();
/*      */         
/* 1147 */         this.tunnels.remove(oldest_tunnel.getKey());
/*      */       }
/*      */       
/* 1150 */       PairManagerTunnel tunnel = new PairManagerTunnel(this, key, originator, sid, handler, secret, tunnel_url, endpoint_url);
/*      */       
/* 1152 */       this.tunnels.put(key, tunnel);
/*      */       
/* 1154 */       System.out.println("Created pair manager tunnel: " + tunnel.getString());
/*      */     }
/*      */     
/*      */ 
/* 1158 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void closeTunnel(PairManagerTunnel tunnel)
/*      */   {
/* 1166 */     System.out.println("Destroyed pair manager tunnel: " + tunnel.getString());
/*      */     
/* 1168 */     synchronized (this.tunnels)
/*      */     {
/* 1170 */       this.tunnels.remove(tunnel.getKey());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void generateEvidence(IndentWriter writer)
/*      */   {
/* 1178 */     writer.println("Tunnel Handler");
/*      */     
/* 1180 */     writer.indent();
/*      */     
/* 1182 */     writer.println("started=" + this.started + ", active=" + this.active);
/*      */     
/* 1184 */     if (this.init_fail != null)
/*      */     {
/* 1186 */       writer.println("Init fail: " + this.init_fail);
/*      */     }
/*      */     
/* 1189 */     long now = SystemTime.getMonotonousTime();
/*      */     
/* 1191 */     writer.println("total local=" + this.total_local_servers);
/* 1192 */     writer.println("last local create=" + (this.last_local_server_create_time == 0L ? "<never>" : String.valueOf(now - this.last_local_server_create_time)));
/* 1193 */     writer.println("last local agree=" + (this.last_local_server_agree_time == 0L ? "<never>" : String.valueOf(now - this.last_local_server_agree_time)));
/*      */     
/* 1195 */     writer.println("total remote=" + this.total_servers);
/* 1196 */     writer.println("last remote create=" + (this.last_server_create_time == 0L ? "<never>" : String.valueOf(now - this.last_server_create_time)));
/* 1197 */     writer.println("last remote agree=" + (this.last_server_agree_time == 0L ? "<never>" : String.valueOf(now - this.last_server_agree_time)));
/*      */     
/* 1199 */     synchronized (this.tunnels)
/*      */     {
/* 1201 */       writer.println("tunnels=" + this.tunnels.size());
/*      */       
/* 1203 */       for (PairManagerTunnel tunnel : this.tunnels.values())
/*      */       {
/* 1205 */         writer.println("    " + tunnel.getString());
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1210 */       writer.println("IPv4 punchers: " + this.nat_punchers_ipv4.size());
/*      */       
/* 1212 */       for (DHTNATPuncher p : this.nat_punchers_ipv4)
/*      */       {
/* 1214 */         writer.println("    " + p.getStats());
/*      */       }
/*      */       
/* 1217 */       writer.println("IPv6 punchers: " + this.nat_punchers_ipv6.size());
/*      */       
/* 1219 */       for (DHTNATPuncher p : this.nat_punchers_ipv6)
/*      */       {
/* 1221 */         writer.println("    " + p.getStats());
/*      */       }
/*      */     }
/*      */     finally {
/* 1225 */       writer.exdent();
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/pairing/impl/PairingManagerTunnelHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */