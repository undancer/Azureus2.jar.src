/*      */ package com.aelitis.azureus.core.instancemanager.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstance;
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstanceManager;
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstanceManagerAdapter;
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstanceManagerAdapter.StateListener;
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstanceManagerListener;
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstanceTracked;
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstanceTracked.TrackTarget;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteSet;
/*      */ import com.aelitis.azureus.core.util.NetUtils;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPlugin;
/*      */ import com.aelitis.net.udp.mc.MCGroup;
/*      */ import com.aelitis.net.udp.mc.MCGroupAdapter;
/*      */ import com.aelitis.net.udp.mc.MCGroupException;
/*      */ import com.aelitis.net.udp.mc.MCGroupFactory;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.NetworkInterface;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import java.util.regex.PatternSyntaxException;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class AZInstanceManagerImpl
/*      */   implements AZInstanceManager, MCGroupAdapter
/*      */ {
/*      */   private static final boolean DISABLE_LAN_LOCAL_STUFF = false;
/*   61 */   private static final LogIDs LOGID = LogIDs.NET;
/*      */   
/*      */   private static final String MC_GROUP_ADDRESS = "239.255.067.250";
/*      */   
/*      */   private static final int MC_GROUP_PORT = 16680;
/*      */   
/*      */   private static final int MC_CONTROL_PORT = 0;
/*      */   
/*      */   private static final int MT_VERSION = 1;
/*      */   
/*      */   private static final int MT_ALIVE = 1;
/*      */   
/*      */   private static final int MT_BYE = 2;
/*      */   
/*      */   private static final int MT_REQUEST = 3;
/*      */   private static final int MT_REPLY = 4;
/*      */   private static final int MT_REQUEST_SEARCH = 1;
/*      */   private static final int MT_REQUEST_TRACK = 2;
/*      */   private static final long ALIVE_PERIOD = 1800000L;
/*      */   private static AZInstanceManagerImpl singleton;
/*   81 */   private final List listeners = new ArrayList();
/*      */   
/*   83 */   private static final AEMonitor class_mon = new AEMonitor("AZInstanceManager:class");
/*      */   
/*   85 */   private static String socks_proxy = null;
/*      */   private final AZInstanceManagerAdapter adapter;
/*      */   
/*   88 */   static { COConfigurationManager.addAndFireParameterListeners(new String[] { "Proxy.Data.Enable", "Proxy.Host", "Proxy.Data.Same", "Proxy.Data.Host" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*      */ 
/*   96 */         if (!COConfigurationManager.getBooleanParameter("Proxy.Data.Enable"))
/*      */         {
/*   98 */           AZInstanceManagerImpl.access$002(null);
/*      */           
/*  100 */           return;
/*      */         }
/*      */         
/*  103 */         if (COConfigurationManager.getBooleanParameter("Proxy.Data.Same"))
/*      */         {
/*  105 */           AZInstanceManagerImpl.access$002(COConfigurationManager.getStringParameter("Proxy.Host"));
/*      */         }
/*      */         else
/*      */         {
/*  109 */           AZInstanceManagerImpl.access$002(COConfigurationManager.getStringParameter("Proxy.Data.Host"));
/*      */         }
/*      */         
/*      */ 
/*  113 */         if (AZInstanceManagerImpl.socks_proxy != null)
/*      */         {
/*  115 */           AZInstanceManagerImpl.access$002(AZInstanceManagerImpl.socks_proxy.trim());
/*      */         }
/*      */       }
/*      */     }); }
/*      */   
/*      */ 
/*      */ 
/*      */   public static AZInstanceManager getSingleton(AZInstanceManagerAdapter core)
/*      */   {
/*      */     try
/*      */     {
/*  126 */       class_mon.enter();
/*      */       
/*  128 */       if (singleton == null)
/*      */       {
/*  130 */         singleton = new AZInstanceManagerImpl(core);
/*      */       }
/*      */     }
/*      */     finally {
/*  134 */       class_mon.exit();
/*      */     }
/*      */     
/*  137 */     return singleton;
/*      */   }
/*      */   
/*      */ 
/*      */   private MCGroup mc_group;
/*      */   
/*      */   private long search_id_next;
/*      */   
/*  145 */   final List<Request> requests = new ArrayList();
/*      */   
/*      */   final AZMyInstanceImpl my_instance;
/*      */   
/*  149 */   private final Map<String, AZOtherInstanceImpl> other_instances = new HashMap();
/*      */   
/*      */   private volatile boolean initialised;
/*      */   
/*  153 */   private volatile Map<InetSocketAddress, InetSocketAddress> tcp_lan_to_ext = new HashMap();
/*  154 */   private volatile Map<InetSocketAddress, InetSocketAddress> udp_lan_to_ext = new HashMap();
/*  155 */   private volatile Map<InetSocketAddress, InetSocketAddress> udp2_lan_to_ext = new HashMap();
/*  156 */   private volatile Map<InetSocketAddress, InetSocketAddress> tcp_ext_to_lan = new HashMap();
/*  157 */   private volatile Map<InetSocketAddress, InetSocketAddress> udp_ext_to_lan = new HashMap();
/*  158 */   private volatile Map<InetSocketAddress, InetSocketAddress> udp2_ext_to_lan = new HashMap();
/*      */   
/*  160 */   private volatile Set<InetAddress> lan_addresses = new HashSet();
/*  161 */   private volatile Set<InetAddress> ext_addresses = new HashSet();
/*      */   
/*  163 */   private volatile List<Pattern> lan_subnets = new ArrayList();
/*  164 */   private volatile List<InetSocketAddress> explicit_peers = new ArrayList();
/*      */   
/*  166 */   private CopyOnWriteSet<InetAddress> explicit_addresses = new CopyOnWriteSet(false);
/*      */   
/*  168 */   private volatile boolean include_well_known_lans = true;
/*      */   
/*  170 */   final AESemaphore initial_search_sem = new AESemaphore("AZInstanceManager:initialSearch");
/*      */   
/*      */   private boolean init_wait_abandoned;
/*  173 */   final AEMonitor this_mon = new AEMonitor("AZInstanceManager");
/*      */   
/*      */ 
/*      */   private boolean closing;
/*      */   
/*      */ 
/*      */   protected AZInstanceManagerImpl(AZInstanceManagerAdapter _adapter)
/*      */   {
/*  181 */     this.adapter = _adapter;
/*      */     
/*  183 */     this.my_instance = new AZMyInstanceImpl(this.adapter, this);
/*      */     
/*  185 */     new AZPortClashHandler(this);
/*      */   }
/*      */   
/*      */   public void initialize()
/*      */   {
/*      */     try
/*      */     {
/*  192 */       this.initialised = true;
/*      */       
/*  194 */       boolean enable = System.getProperty("az.instance.manager.enable", "1").equals("1");
/*      */       
/*  196 */       if (enable)
/*      */       {
/*  198 */         this.mc_group = MCGroupFactory.getSingleton(this, "239.255.067.250", 16680, 0, null);
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*      */ 
/*  207 */         this.mc_group = getDummyMCGroup();
/*      */       }
/*      */       
/*  210 */       this.adapter.addListener(new AZInstanceManagerAdapter.StateListener()
/*      */       {
/*      */         public void started() {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void stopped()
/*      */         {
/*  221 */           AZInstanceManagerImpl.this.closing = true;
/*      */           
/*  223 */           AZInstanceManagerImpl.this.sendByeBye();
/*      */         }
/*      */         
/*  226 */       });
/*  227 */       SimpleTimer.addPeriodicEvent("InstManager:timeouts", 1800000L, new TimerEventPerformer()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void perform(TimerEvent event)
/*      */         {
/*      */ 
/*      */ 
/*  236 */           AZInstanceManagerImpl.this.checkTimeouts();
/*      */           
/*  238 */           AZInstanceManagerImpl.this.sendAlive();
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  244 */       if (this.mc_group == null)
/*      */       {
/*  246 */         this.mc_group = getDummyMCGroup();
/*      */       }
/*      */       
/*  249 */       this.initial_search_sem.releaseForever();
/*      */       
/*  251 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/*  254 */     new AEThread2("AZInstanceManager:initialSearch", true)
/*      */     {
/*      */       public void run()
/*      */       {
/*      */         try
/*      */         {
/*  260 */           AZInstanceManagerImpl.this.search();
/*      */           
/*      */ 
/*      */ 
/*  264 */           AZInstanceManagerImpl.this.addAddresses(AZInstanceManagerImpl.this.my_instance);
/*      */         }
/*      */         finally
/*      */         {
/*  268 */           AZInstanceManagerImpl.this.initial_search_sem.releaseForever();
/*      */         }
/*      */       }
/*      */     }.start();
/*      */   }
/*      */   
/*      */ 
/*      */   private MCGroup getDummyMCGroup()
/*      */   {
/*  277 */     new MCGroup()
/*      */     {
/*      */ 
/*      */       public int getControlPort()
/*      */       {
/*      */ 
/*  283 */         return 0;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void sendToGroup(byte[] data) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void sendToGroup(String param_data) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void sendToMember(InetSocketAddress address, byte[] data)
/*      */         throws MCGroupException
/*      */       {}
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getClockSkew()
/*      */   {
/*      */     try
/*      */     {
/*  313 */       DHTPlugin dht = this.adapter.getDHTPlugin();
/*      */       
/*  315 */       if (dht != null)
/*      */       {
/*  317 */         return dht.getClockSkew();
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  321 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/*  324 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void trace(String str)
/*      */   {
/*  331 */     if (Logger.isEnabled())
/*      */     {
/*  333 */       Logger.log(new LogEvent(LOGID, str));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void log(Throwable e)
/*      */   {
/*  341 */     Debug.printStackTrace(e);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isInitialized()
/*      */   {
/*  347 */     return this.initial_search_sem.isReleasedForever();
/*      */   }
/*      */   
/*      */ 
/*      */   public void updateNow()
/*      */   {
/*  353 */     sendAlive();
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean isClosing()
/*      */   {
/*  359 */     return this.closing;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void sendAlive()
/*      */   {
/*  365 */     sendMessage(1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void sendAlive(InetSocketAddress target)
/*      */   {
/*  372 */     sendMessage(1, target);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void sendByeBye()
/*      */   {
/*  378 */     sendMessage(2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void sendByeBye(InetSocketAddress target)
/*      */   {
/*  385 */     sendMessage(2, target);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void sendMessage(int type)
/*      */   {
/*  392 */     sendMessage(type, (Map)null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void sendMessage(int type, InetSocketAddress target)
/*      */   {
/*  400 */     sendMessage(type, null, target);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void sendMessage(int type, Map body)
/*      */   {
/*  408 */     sendMessage(type, body, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void sendMessage(int type, Map body, InetSocketAddress member)
/*      */   {
/*  417 */     Map map = new HashMap();
/*      */     
/*  419 */     map.put("ver", new Long(1L));
/*  420 */     map.put("type", new Long(type));
/*      */     
/*  422 */     Map originator = new HashMap();
/*      */     
/*  424 */     map.put("orig", originator);
/*      */     
/*  426 */     this.my_instance.encode(originator);
/*      */     
/*  428 */     if (body != null)
/*      */     {
/*  430 */       map.put("body", body);
/*      */     }
/*      */     
/*      */     try
/*      */     {
/*  435 */       if (member == null)
/*      */       {
/*  437 */         byte[] data = BEncoder.encode(map);
/*      */         
/*  439 */         this.mc_group.sendToGroup(data);
/*      */         
/*  441 */         if (this.explicit_peers.size() > 0)
/*      */         {
/*  443 */           map.put("explicit", new Long(1L));
/*      */           
/*  445 */           byte[] explicit_data = BEncoder.encode(map);
/*      */           
/*  447 */           Iterator it = this.explicit_peers.iterator();
/*      */           
/*  449 */           while (it.hasNext())
/*      */           {
/*  451 */             this.mc_group.sendToMember((InetSocketAddress)it.next(), explicit_data);
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/*  456 */         if (this.explicit_peers.contains(member))
/*      */         {
/*  458 */           map.put("explicit", new Long(1L));
/*      */         }
/*      */         
/*  461 */         byte[] explicit_data = BEncoder.encode(map);
/*      */         
/*  463 */         this.mc_group.sendToMember(member, explicit_data);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void received(NetworkInterface network_interface, InetAddress local_address, InetSocketAddress originator, byte[] data, int length)
/*      */   {
/*      */     try
/*      */     {
/*  479 */       Map map = BDecoder.decode(data, 0, length);
/*      */       
/*  481 */       long version = ((Long)map.get("ver")).longValue();
/*  482 */       long type = ((Long)map.get("type")).longValue();
/*      */       
/*  484 */       InetAddress originator_address = originator.getAddress();
/*      */       
/*  486 */       if (map.get("explicit") != null)
/*      */       {
/*  488 */         addInstanceSupport(originator_address, false);
/*      */       }
/*      */       
/*  491 */       AZOtherInstanceImpl instance = AZOtherInstanceImpl.decode(originator_address, (Map)map.get("orig"));
/*      */       
/*  493 */       if (instance != null)
/*      */       {
/*  495 */         if (type == 1L)
/*      */         {
/*  497 */           checkAdd(instance);
/*      */         }
/*  499 */         else if (type == 2L)
/*      */         {
/*  501 */           checkRemove(instance);
/*      */         }
/*      */         else
/*      */         {
/*  505 */           checkAdd(instance);
/*      */           
/*  507 */           Map body = (Map)map.get("body");
/*      */           
/*  509 */           if (type == 3L)
/*      */           {
/*  511 */             String originator_id = instance.getID();
/*      */             
/*  513 */             if (!originator_id.equals(this.my_instance.getID()))
/*      */             {
/*  515 */               Map reply = requestReceived(instance, body);
/*      */               
/*  517 */               if (reply != null)
/*      */               {
/*  519 */                 reply.put("oid", originator_id.getBytes());
/*  520 */                 reply.put("rid", body.get("rid"));
/*      */                 
/*  522 */                 sendMessage(4, reply, originator);
/*      */               }
/*      */             }
/*  525 */           } else if (type == 4L)
/*      */           {
/*  527 */             String originator_id = new String((byte[])body.get("oid"));
/*      */             
/*  529 */             if (originator_id.equals(this.my_instance.getID()))
/*      */             {
/*  531 */               long req_id = ((Long)body.get("rid")).longValue();
/*      */               try
/*      */               {
/*  534 */                 this.this_mon.enter();
/*      */                 
/*  536 */                 for (int i = 0; i < this.requests.size(); i++)
/*      */                 {
/*  538 */                   Request req = (Request)this.requests.get(i);
/*      */                   
/*  540 */                   if (req.getID() == req_id)
/*      */                   {
/*  542 */                     req.addReply(instance, body);
/*      */                   }
/*      */                 }
/*      */               }
/*      */               finally {
/*  547 */                 this.this_mon.exit();
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  555 */       Debug.out("Invalid packet received from " + originator, e);
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
/*      */   protected Map requestReceived(AZInstance instance, Map body)
/*      */   {
/*  568 */     long type = ((Long)body.get("type")).longValue();
/*      */     
/*  570 */     if (type == 1L)
/*      */     {
/*  572 */       return new HashMap();
/*      */     }
/*  574 */     if (type == 2L)
/*      */     {
/*  576 */       byte[] hash = (byte[])body.get("hash");
/*      */       
/*  578 */       boolean seed = ((Long)body.get("seed")).intValue() == 1;
/*      */       
/*  580 */       AZInstanceTracked.TrackTarget target = this.adapter.track(hash);
/*      */       
/*  582 */       if (target != null)
/*      */       {
/*      */         try {
/*  585 */           informTracked(new trackedInstance(instance, target, seed));
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  589 */           Debug.printStackTrace(e);
/*      */         }
/*      */         
/*  592 */         Map reply = new HashMap();
/*      */         
/*  594 */         reply.put("seed", new Long(target.isSeed() ? 1L : 0L));
/*      */         
/*  596 */         return reply;
/*      */       }
/*      */       
/*      */ 
/*  600 */       return null;
/*      */     }
/*      */     
/*      */ 
/*  604 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void interfaceChanged(NetworkInterface network_interface)
/*      */   {
/*  612 */     sendAlive();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected AZOtherInstanceImpl checkAdd(AZOtherInstanceImpl inst)
/*      */   {
/*  619 */     if (inst.getID().equals(this.my_instance.getID()))
/*      */     {
/*  621 */       return inst;
/*      */     }
/*      */     
/*  624 */     boolean added = false;
/*  625 */     boolean changed = false;
/*      */     try
/*      */     {
/*  628 */       this.this_mon.enter();
/*      */       
/*  630 */       AZOtherInstanceImpl existing = (AZOtherInstanceImpl)this.other_instances.get(inst.getID());
/*      */       
/*  632 */       if (existing == null)
/*      */       {
/*  634 */         added = true;
/*      */         
/*  636 */         this.other_instances.put(inst.getID(), inst);
/*      */       }
/*      */       else
/*      */       {
/*  640 */         changed = existing.update(inst);
/*      */         
/*  642 */         inst = existing;
/*      */       }
/*      */     }
/*      */     finally {
/*  646 */       this.this_mon.exit();
/*      */     }
/*      */     
/*  649 */     if (added)
/*      */     {
/*  651 */       informAdded(inst);
/*      */     }
/*  653 */     else if (changed)
/*      */     {
/*  655 */       informChanged(inst);
/*      */     }
/*      */     
/*  658 */     return inst;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkRemove(AZOtherInstanceImpl inst)
/*      */   {
/*  665 */     if (inst.getID().equals(this.my_instance.getID()))
/*      */     {
/*  667 */       return;
/*      */     }
/*      */     
/*  670 */     boolean removed = false;
/*      */     try
/*      */     {
/*  673 */       this.this_mon.enter();
/*      */       
/*  675 */       removed = this.other_instances.remove(inst.getID()) != null;
/*      */     }
/*      */     finally
/*      */     {
/*  679 */       this.this_mon.exit();
/*      */     }
/*      */     
/*  682 */     if (removed)
/*      */     {
/*  684 */       informRemoved(inst);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public AZInstance getMyInstance()
/*      */   {
/*  691 */     return this.my_instance;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void search()
/*      */   {
/*  697 */     sendRequest(1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getOtherInstanceCount(boolean block_if_needed)
/*      */   {
/*  704 */     if (!block_if_needed)
/*      */     {
/*  706 */       if (!this.initial_search_sem.isReleasedForever())
/*      */       {
/*  708 */         return 0;
/*      */       }
/*      */     }
/*      */     
/*  712 */     waitForInit();
/*      */     try
/*      */     {
/*  715 */       this.this_mon.enter();
/*      */       
/*  717 */       return this.other_instances.size();
/*      */     }
/*      */     finally
/*      */     {
/*  721 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public AZInstance[] getOtherInstances()
/*      */   {
/*  728 */     waitForInit();
/*      */     try
/*      */     {
/*  731 */       this.this_mon.enter();
/*      */       
/*  733 */       return (AZInstance[])this.other_instances.values().toArray(new AZInstance[this.other_instances.size()]);
/*      */     }
/*      */     finally
/*      */     {
/*  737 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void waitForInit()
/*      */   {
/*  744 */     if (this.init_wait_abandoned)
/*      */     {
/*  746 */       return;
/*      */     }
/*      */     
/*  749 */     if (!this.initial_search_sem.reserve(2500L)) {
/*  750 */       Debug.out("Instance manager - timeout waiting for initial search");
/*      */       
/*  752 */       this.init_wait_abandoned = true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void addAddresses(AZInstance inst)
/*      */   {
/*  760 */     InetAddress internal_address = inst.getInternalAddress();
/*  761 */     InetAddress external_address = inst.getExternalAddress();
/*  762 */     int tcp = inst.getTCPListenPort();
/*  763 */     int udp = inst.getUDPListenPort();
/*  764 */     int udp2 = inst.getUDPNonDataListenPort();
/*      */     
/*  766 */     modifyAddresses(internal_address, external_address, tcp, udp, udp2, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void removeAddresses(AZOtherInstanceImpl inst)
/*      */   {
/*  773 */     List internal_addresses = inst.getInternalAddresses();
/*  774 */     InetAddress external_address = inst.getExternalAddress();
/*  775 */     int tcp = inst.getTCPListenPort();
/*  776 */     int udp = inst.getUDPListenPort();
/*  777 */     int udp2 = inst.getUDPNonDataListenPort();
/*      */     
/*  779 */     for (int i = 0; i < internal_addresses.size(); i++)
/*      */     {
/*  781 */       modifyAddresses((InetAddress)internal_addresses.get(i), external_address, tcp, udp, udp2, false);
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
/*      */   protected void modifyAddresses(InetAddress internal_address, InetAddress external_address, int tcp, int udp, int udp2, boolean add)
/*      */   {
/*  794 */     if (internal_address.isAnyLocalAddress()) {
/*      */       try
/*      */       {
/*  797 */         internal_address = NetUtils.getLocalHost();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  801 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  806 */       this.this_mon.enter();
/*      */       
/*  808 */       InetSocketAddress int_tcp = new InetSocketAddress(internal_address, tcp);
/*  809 */       InetSocketAddress ext_tcp = new InetSocketAddress(external_address, tcp);
/*  810 */       InetSocketAddress int_udp = new InetSocketAddress(internal_address, udp);
/*  811 */       InetSocketAddress ext_udp = new InetSocketAddress(external_address, udp);
/*  812 */       InetSocketAddress int_udp2 = new InetSocketAddress(internal_address, udp2);
/*  813 */       InetSocketAddress ext_udp2 = new InetSocketAddress(external_address, udp2);
/*      */       
/*      */ 
/*      */ 
/*  817 */       this.tcp_ext_to_lan = modifyAddress(this.tcp_ext_to_lan, ext_tcp, int_tcp, add);
/*  818 */       this.tcp_lan_to_ext = modifyAddress(this.tcp_lan_to_ext, int_tcp, ext_tcp, add);
/*  819 */       this.udp_ext_to_lan = modifyAddress(this.udp_ext_to_lan, ext_udp, int_udp, add);
/*  820 */       this.udp_lan_to_ext = modifyAddress(this.udp_lan_to_ext, int_udp, ext_udp, add);
/*  821 */       this.udp2_ext_to_lan = modifyAddress(this.udp2_ext_to_lan, ext_udp2, int_udp2, add);
/*  822 */       this.udp2_lan_to_ext = modifyAddress(this.udp2_lan_to_ext, int_udp2, ext_udp2, add);
/*      */       
/*  824 */       if (!this.lan_addresses.contains(internal_address))
/*      */       {
/*  826 */         Set new_lan_addresses = new HashSet(this.lan_addresses);
/*      */         
/*  828 */         new_lan_addresses.add(internal_address);
/*      */         
/*  830 */         this.lan_addresses = new_lan_addresses;
/*      */       }
/*      */       
/*  833 */       if (!this.ext_addresses.contains(external_address))
/*      */       {
/*  835 */         Set new_ext_addresses = new HashSet(this.ext_addresses);
/*      */         
/*  837 */         new_ext_addresses.add(external_address);
/*      */         
/*  839 */         this.ext_addresses = new_ext_addresses;
/*      */       }
/*      */     }
/*      */     finally {
/*  843 */       this.this_mon.exit();
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
/*      */   protected Map<InetSocketAddress, InetSocketAddress> modifyAddress(Map<InetSocketAddress, InetSocketAddress> map, InetSocketAddress key, InetSocketAddress value, boolean add)
/*      */   {
/*  856 */     InetSocketAddress old_value = (InetSocketAddress)map.get(key);
/*      */     
/*  858 */     boolean same = (old_value != null) && (old_value.equals(value));
/*      */     
/*  860 */     Map<InetSocketAddress, InetSocketAddress> new_map = map;
/*      */     
/*  862 */     if (add)
/*      */     {
/*  864 */       if (!same)
/*      */       {
/*  866 */         new_map = new HashMap(map);
/*      */         
/*  868 */         new_map.put(key, value);
/*      */       }
/*      */       
/*      */     }
/*  872 */     else if (same)
/*      */     {
/*  874 */       new_map = new HashMap(map);
/*      */       
/*  876 */       new_map.remove(key);
/*      */     }
/*      */     
/*      */ 
/*  880 */     return new_map;
/*      */   }
/*      */   
/*      */ 
/*      */   public InetSocketAddress getLANAddress(InetSocketAddress external_address, int address_type)
/*      */   {
/*      */     Map map;
/*      */     
/*      */     Map map;
/*      */     
/*  890 */     if (address_type == 1) {
/*  891 */       map = this.tcp_ext_to_lan; } else { Map map;
/*  892 */       if (address_type == 2) {
/*  893 */         map = this.udp_ext_to_lan;
/*      */       } else {
/*  895 */         map = this.udp2_ext_to_lan;
/*      */       }
/*      */     }
/*  898 */     if (map.size() == 0)
/*      */     {
/*  900 */       return null;
/*      */     }
/*      */     
/*  903 */     return (InetSocketAddress)map.get(external_address);
/*      */   }
/*      */   
/*      */ 
/*      */   public InetSocketAddress getExternalAddress(InetSocketAddress lan_address, int address_type)
/*      */   {
/*      */     Map map;
/*      */     
/*      */     Map map;
/*      */     
/*  913 */     if (address_type == 1) {
/*  914 */       map = this.tcp_lan_to_ext; } else { Map map;
/*  915 */       if (address_type == 2) {
/*  916 */         map = this.udp_lan_to_ext;
/*      */       } else {
/*  918 */         map = this.udp2_lan_to_ext;
/*      */       }
/*      */     }
/*  921 */     if (map.size() == 0)
/*      */     {
/*  923 */       return null;
/*      */     }
/*      */     
/*  926 */     return (InetSocketAddress)map.get(lan_address);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isLANAddress(InetAddress address)
/*      */   {
/*  938 */     if (address == null)
/*      */     {
/*  940 */       return false;
/*      */     }
/*      */     
/*  943 */     String sp = socks_proxy;
/*      */     
/*  945 */     if (sp != null)
/*      */     {
/*  947 */       if (sp.equals(address.getHostAddress()))
/*      */       {
/*  949 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  953 */     if (this.include_well_known_lans)
/*      */     {
/*  955 */       if ((address.isLoopbackAddress()) || (address.isLinkLocalAddress()) || (address.isSiteLocalAddress()))
/*      */       {
/*      */ 
/*      */ 
/*  959 */         return true;
/*      */       }
/*      */     }
/*      */     
/*  963 */     String host_address = address.getHostAddress();
/*      */     
/*  965 */     for (int i = 0; i < this.lan_subnets.size(); i++)
/*      */     {
/*  967 */       Pattern p = (Pattern)this.lan_subnets.get(i);
/*      */       
/*  969 */       if (p.matcher(host_address).matches())
/*      */       {
/*  971 */         return true;
/*      */       }
/*      */     }
/*      */     
/*  975 */     if (this.lan_addresses.contains(address))
/*      */     {
/*  977 */       return true;
/*      */     }
/*      */     
/*  980 */     if (this.explicit_peers.size() > 0)
/*      */     {
/*  982 */       Iterator it = this.explicit_peers.iterator();
/*      */       
/*  984 */       while (it.hasNext())
/*      */       {
/*  986 */         if (((InetSocketAddress)it.next()).getAddress().equals(address))
/*      */         {
/*  988 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  993 */     return this.explicit_addresses.contains(address);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addLANAddress(InetAddress address)
/*      */   {
/* 1000 */     this.explicit_addresses.add(address);
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeLANAddress(InetAddress address)
/*      */   {
/* 1006 */     this.explicit_addresses.remove(address);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean addLANSubnet(String subnet)
/*      */     throws PatternSyntaxException
/*      */   {
/* 1015 */     String str = "";
/*      */     
/* 1017 */     for (int i = 0; i < subnet.length(); i++)
/*      */     {
/* 1019 */       char c = subnet.charAt(i);
/*      */       
/* 1021 */       if (c == '*')
/*      */       {
/* 1023 */         str = str + ".*?";
/*      */       }
/* 1025 */       else if (c == '.')
/*      */       {
/* 1027 */         str = str + "\\.";
/*      */       }
/*      */       else
/*      */       {
/* 1031 */         str = str + c;
/*      */       }
/*      */     }
/*      */     
/* 1035 */     Pattern pattern = Pattern.compile(str);
/*      */     
/* 1037 */     for (int i = 0; i < this.lan_subnets.size(); i++)
/*      */     {
/* 1039 */       if (pattern.pattern().equals(((Pattern)this.lan_subnets.get(i)).pattern()))
/*      */       {
/* 1041 */         return false;
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1046 */       this.this_mon.enter();
/*      */       
/* 1048 */       List new_nets = new ArrayList(this.lan_subnets);
/*      */       
/* 1050 */       new_nets.add(pattern);
/*      */       
/* 1052 */       this.lan_subnets = new_nets;
/*      */     }
/*      */     finally
/*      */     {
/* 1056 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1059 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setIncludeWellKnownLANs(boolean include)
/*      */   {
/* 1066 */     this.include_well_known_lans = include;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getIncludeWellKnownLANs()
/*      */   {
/* 1072 */     return this.include_well_known_lans;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean addInstance(InetAddress explicit_address)
/*      */   {
/* 1079 */     return addInstanceSupport(explicit_address, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean addInstanceSupport(InetAddress explicit_address, boolean force_send_alive)
/*      */   {
/* 1087 */     final InetSocketAddress sad = new InetSocketAddress(explicit_address, 16680);
/*      */     
/* 1089 */     boolean new_peer = false;
/*      */     
/* 1091 */     if (!this.explicit_peers.contains(sad))
/*      */     {
/*      */       try {
/* 1094 */         this.this_mon.enter();
/*      */         
/* 1096 */         List new_peers = new ArrayList(this.explicit_peers);
/*      */         
/* 1098 */         new_peers.add(sad);
/*      */         
/* 1100 */         this.explicit_peers = new_peers;
/*      */       }
/*      */       finally
/*      */       {
/* 1104 */         this.this_mon.exit();
/*      */       }
/*      */       
/* 1107 */       new_peer = true;
/*      */     }
/*      */     
/*      */ 
/* 1111 */     if ((force_send_alive) || (new_peer))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1117 */       if (this.initialised)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1124 */         new DelayedEvent("AZInstanceManagerImpl:delaySendAlive", 0L, new AERunnable()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/*      */ 
/* 1132 */             AZInstanceManagerImpl.this.sendAlive(sad);
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/* 1138 */     return new_peer;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isExternalAddress(InetAddress address)
/*      */   {
/* 1145 */     return this.ext_addresses.contains(address);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public AZInstanceTracked[] track(byte[] hash, AZInstanceTracked.TrackTarget target)
/*      */   {
/* 1153 */     if ((this.mc_group == null) || (getOtherInstances().length == 0))
/*      */     {
/* 1155 */       return new AZInstanceTracked[0];
/*      */     }
/*      */     
/* 1158 */     Map body = new HashMap();
/*      */     
/* 1160 */     body.put("hash", hash);
/*      */     
/* 1162 */     body.put("seed", new Long(target.isSeed() ? 1L : 0L));
/*      */     
/* 1164 */     Map replies = sendRequest(2, body);
/*      */     
/* 1166 */     AZInstanceTracked[] res = new AZInstanceTracked[replies.size()];
/*      */     
/* 1168 */     Iterator it = replies.entrySet().iterator();
/*      */     
/* 1170 */     int pos = 0;
/*      */     
/* 1172 */     while (it.hasNext())
/*      */     {
/* 1174 */       Map.Entry entry = (Map.Entry)it.next();
/*      */       
/* 1176 */       AZInstance inst = (AZInstance)entry.getKey();
/* 1177 */       Map reply = (Map)entry.getValue();
/*      */       
/* 1179 */       boolean seed = ((Long)reply.get("seed")).intValue() == 1;
/*      */       
/* 1181 */       res[(pos++)] = new trackedInstance(inst, target, seed);
/*      */     }
/*      */     
/* 1184 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkTimeouts()
/*      */   {
/* 1190 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 1192 */     List removed = new ArrayList();
/*      */     try
/*      */     {
/* 1195 */       this.this_mon.enter();
/*      */       
/* 1197 */       Iterator it = this.other_instances.values().iterator();
/*      */       
/* 1199 */       while (it.hasNext())
/*      */       {
/* 1201 */         AZOtherInstanceImpl inst = (AZOtherInstanceImpl)it.next();
/*      */         
/* 1203 */         if (now - inst.getAliveTime() > 4500000.0D)
/*      */         {
/* 1205 */           removed.add(inst);
/*      */           
/* 1207 */           it.remove();
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1212 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1215 */     for (int i = 0; i < removed.size(); i++)
/*      */     {
/* 1217 */       AZOtherInstanceImpl inst = (AZOtherInstanceImpl)removed.get(i);
/*      */       
/* 1219 */       informRemoved(inst);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void informRemoved(AZOtherInstanceImpl inst)
/*      */   {
/* 1227 */     removeAddresses(inst);
/*      */     
/* 1229 */     for (int i = 0; i < this.listeners.size(); i++) {
/*      */       try
/*      */       {
/* 1232 */         ((AZInstanceManagerListener)this.listeners.get(i)).instanceLost(inst);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1236 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void informAdded(AZInstance inst)
/*      */   {
/* 1245 */     addAddresses(inst);
/*      */     
/* 1247 */     for (int i = 0; i < this.listeners.size(); i++) {
/*      */       try
/*      */       {
/* 1250 */         ((AZInstanceManagerListener)this.listeners.get(i)).instanceFound(inst);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1254 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void informChanged(AZInstance inst)
/*      */   {
/* 1263 */     addAddresses(inst);
/*      */     
/* 1265 */     if (inst == this.my_instance)
/*      */     {
/* 1267 */       sendAlive();
/*      */     }
/*      */     
/* 1270 */     for (int i = 0; i < this.listeners.size(); i++) {
/*      */       try
/*      */       {
/* 1273 */         ((AZInstanceManagerListener)this.listeners.get(i)).instanceChanged(inst);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1277 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void informTracked(AZInstanceTracked inst)
/*      */   {
/* 1286 */     for (int i = 0; i < this.listeners.size(); i++) {
/*      */       try
/*      */       {
/* 1289 */         ((AZInstanceManagerListener)this.listeners.get(i)).instanceTracked(inst);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1293 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected Map sendRequest(int type)
/*      */   {
/* 1302 */     return new Request(type, new HashMap()).getReplies();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Map sendRequest(int type, Map body)
/*      */   {
/* 1310 */     return new Request(type, body).getReplies();
/*      */   }
/*      */   
/*      */ 
/*      */   protected class Request
/*      */   {
/*      */     private long id;
/*      */     
/* 1318 */     private final Set reply_instances = new HashSet();
/*      */     
/* 1320 */     private final Map replies = new HashMap();
/*      */     
/*      */ 
/*      */ 
/*      */     protected Request(int type, Map body)
/*      */     {
/*      */       try
/*      */       {
/* 1328 */         AZInstanceManagerImpl.this.this_mon.enter();
/*      */         
/* 1330 */         this.id = AZInstanceManagerImpl.access$208(AZInstanceManagerImpl.this);
/*      */         
/* 1332 */         AZInstanceManagerImpl.this.requests.add(this);
/*      */       }
/*      */       finally
/*      */       {
/* 1336 */         AZInstanceManagerImpl.this.this_mon.exit();
/*      */       }
/*      */       
/* 1339 */       body.put("type", new Long(type));
/*      */       
/* 1341 */       body.put("rid", new Long(this.id));
/*      */       
/* 1343 */       AZInstanceManagerImpl.this.sendMessage(3, body);
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getID()
/*      */     {
/* 1349 */       return this.id;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void addReply(AZInstance instance, Map body)
/*      */     {
/*      */       try
/*      */       {
/* 1358 */         AZInstanceManagerImpl.this.this_mon.enter();
/*      */         
/* 1360 */         if (!this.reply_instances.contains(instance.getID()))
/*      */         {
/* 1362 */           this.reply_instances.add(instance.getID());
/*      */           
/* 1364 */           this.replies.put(instance, body);
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 1369 */         AZInstanceManagerImpl.this.this_mon.exit();
/*      */       }
/*      */     }
/*      */     
/*      */     protected Map getReplies()
/*      */     {
/*      */       try
/*      */       {
/* 1377 */         Thread.sleep(2500L);
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/*      */       try
/*      */       {
/* 1384 */         AZInstanceManagerImpl.this.this_mon.enter();
/*      */         
/* 1386 */         AZInstanceManagerImpl.this.requests.remove(this);
/*      */         
/* 1388 */         return this.replies;
/*      */       }
/*      */       finally
/*      */       {
/* 1392 */         AZInstanceManagerImpl.this.this_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(AZInstanceManagerListener l)
/*      */   {
/* 1401 */     this.listeners.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(AZInstanceManagerListener l)
/*      */   {
/* 1408 */     this.listeners.remove(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static class trackedInstance
/*      */     implements AZInstanceTracked
/*      */   {
/*      */     private final AZInstance instance;
/*      */     
/*      */     private final AZInstanceTracked.TrackTarget target;
/*      */     
/*      */     private final boolean seed;
/*      */     
/*      */ 
/*      */     protected trackedInstance(AZInstance _instance, AZInstanceTracked.TrackTarget _target, boolean _seed)
/*      */     {
/* 1425 */       this.instance = _instance;
/* 1426 */       this.target = _target;
/* 1427 */       this.seed = _seed;
/*      */     }
/*      */     
/*      */     public AZInstance getInstance()
/*      */     {
/* 1432 */       return this.instance;
/*      */     }
/*      */     
/*      */ 
/*      */     public AZInstanceTracked.TrackTarget getTarget()
/*      */     {
/* 1438 */       return this.target;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isSeed()
/*      */     {
/* 1444 */       return this.seed;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/instancemanager/impl/AZInstanceManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */