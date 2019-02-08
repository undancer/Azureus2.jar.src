/*      */ package com.aelitis.azureus.plugins.net.buddy;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.AZ3Functions;
/*      */ import com.aelitis.azureus.core.util.AZ3Functions.provider;
/*      */ import java.io.File;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.URL;
/*      */ import java.net.URLEncoder;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Random;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AddressUtils;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*      */ import org.gudy.azureus2.core3.util.LightHashMap;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.messaging.MessageException;
/*      */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageConnection;
/*      */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageConnectionListener;
/*      */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageEndpoint;
/*      */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageRegistration;
/*      */ import org.gudy.azureus2.plugins.utils.PooledByteBuffer;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.plugins.utils.security.SEPublicKey;
/*      */ import org.gudy.azureus2.plugins.utils.security.SEPublicKeyLocator;
/*      */ import org.gudy.azureus2.plugins.utils.security.SESecurityManager;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class BuddyPluginBuddy
/*      */ {
/*      */   private static final boolean TRACE = false;
/*      */   private static final int CONNECTION_IDLE_TIMEOUT = 300000;
/*      */   private static final int CONNECTION_KEEP_ALIVE = 60000;
/*      */   private static final int MAX_ACTIVE_CONNECTIONS = 5;
/*      */   private static final int MAX_QUEUED_MESSAGES = 256;
/*      */   private static final int RT_REQUEST_DATA = 1;
/*      */   private static final int RT_REPLY_DATA = 2;
/*      */   private static final int RT_REPLY_ERROR = 99;
/*      */   private BuddyPlugin plugin;
/*      */   private long created_time;
/*      */   private int subsystem;
/*      */   private boolean authorised;
/*      */   private String public_key;
/*      */   private String nick_name;
/*      */   private List<Long> recent_ygm;
/*      */   private int last_status_seq;
/*      */   private long post_time;
/*      */   private InetAddress current_ip;
/*      */   private int tcp_port;
/*      */   private int udp_port;
/*   86 */   private int online_status = 0;
/*      */   
/*   88 */   private int version = 2;
/*      */   
/*      */   private boolean online;
/*      */   
/*      */   private long last_time_online;
/*      */   
/*      */   private long status_check_count;
/*      */   
/*      */   private long last_status_check_time;
/*      */   private boolean check_active;
/*   98 */   private List<buddyConnection> connections = new ArrayList();
/*   99 */   private List<buddyMessage> messages = new ArrayList();
/*      */   
/*      */   private buddyMessage current_message;
/*      */   
/*      */   private int next_connection_id;
/*      */   
/*      */   private int next_message_id;
/*      */   
/*      */   private boolean ygm_active;
/*      */   
/*      */   private boolean ygm_pending;
/*      */   
/*      */   private long latest_ygm_time;
/*      */   private String last_message_received;
/*      */   private Set<Long> offline_seq_set;
/*      */   private int message_out_count;
/*      */   private int message_in_count;
/*      */   private int message_out_bytes;
/*      */   private int message_in_bytes;
/*  118 */   private String received_frag_details = "";
/*      */   
/*      */   private BuddyPluginBuddyMessageHandler persistent_msg_handler;
/*      */   
/*  122 */   private Map<Object, Object> user_data = new LightHashMap();
/*      */   
/*      */   private boolean keep_alive_outstanding;
/*  125 */   private volatile long last_connect_attempt = SystemTime.getCurrentTime();
/*      */   
/*      */   private volatile int consec_connect_fails;
/*  128 */   private long last_auto_reconnect = -1L;
/*      */   
/*  130 */   private Object rss_lock = new Object();
/*      */   
/*      */   private Set<String> rss_local_cats;
/*      */   
/*      */   private Set<String> rss_remote_cats;
/*      */   private Set<String> rss_cats_read;
/*  136 */   private AESemaphore outgoing_connect_sem = new AESemaphore("BPB:outcon", 1);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private volatile boolean closing;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private volatile boolean destroyed;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected BuddyPluginBuddy(BuddyPlugin _plugin, long _created_time, int _subsystem, boolean _authorised, String _pk, String _nick_name, int _version, String _rss_local_cats, String _rss_remote_cats, int _last_status_seq, long _last_time_online, List<Long> _recent_ygm)
/*      */   {
/*  156 */     this.plugin = _plugin;
/*  157 */     this.created_time = _created_time;
/*  158 */     this.subsystem = _subsystem;
/*  159 */     this.authorised = _authorised;
/*  160 */     this.public_key = _pk;
/*  161 */     this.nick_name = _nick_name;
/*  162 */     this.version = Math.max(this.version, _version);
/*  163 */     this.rss_local_cats = stringToCats(_rss_local_cats);
/*  164 */     this.rss_remote_cats = stringToCats(_rss_remote_cats);
/*  165 */     this.last_status_seq = _last_status_seq;
/*  166 */     this.last_time_online = _last_time_online;
/*  167 */     this.recent_ygm = _recent_ygm;
/*      */     
/*  169 */     this.persistent_msg_handler = new BuddyPluginBuddyMessageHandler(this, new File(this.plugin.getBuddyConfigDir(), this.public_key));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setInitialStatus(long now, int num_buddies)
/*      */   {
/*  180 */     if ((this.last_time_online == 0L) && (now - this.created_time > 604800000L))
/*      */     {
/*      */ 
/*  183 */       this.last_status_check_time = (now + RandomUtils.nextInt(300000 * num_buddies));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected BuddyPlugin getPlugin()
/*      */   {
/*  190 */     return this.plugin;
/*      */   }
/*      */   
/*      */ 
/*      */   public BuddyPluginBuddyMessageHandler getMessageHandler()
/*      */   {
/*  196 */     return this.persistent_msg_handler;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void persistentDispatchPending()
/*      */   {
/*  202 */     this.plugin.persistentDispatchPending(this);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkPersistentDispatch()
/*      */   {
/*  208 */     this.persistent_msg_handler.checkPersistentDispatch();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void persistentDispatch()
/*      */   {
/*  214 */     this.persistent_msg_handler.persistentDispatch();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Map readConfigFile(File name)
/*      */   {
/*  221 */     return this.plugin.readConfigFile(name);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean writeConfigFile(File name, Map data)
/*      */   {
/*  229 */     return this.plugin.writeConfigFile(name, data);
/*      */   }
/*      */   
/*      */ 
/*      */   protected long getCreatedTime()
/*      */   {
/*  235 */     return this.created_time;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getSubsystem()
/*      */   {
/*  241 */     return this.subsystem;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setSubsystem(int _s)
/*      */   {
/*  248 */     this.subsystem = _s;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isAuthorised()
/*      */   {
/*  254 */     return this.authorised;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setAuthorised(boolean _a)
/*      */   {
/*  261 */     this.authorised = _a;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getPublicKey()
/*      */   {
/*  267 */     return this.public_key;
/*      */   }
/*      */   
/*      */ 
/*      */   protected byte[] getRawPublicKey()
/*      */   {
/*  273 */     return Base32.decode(this.public_key);
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getShortString()
/*      */   {
/*  279 */     return this.public_key.substring(0, 16) + "...";
/*      */   }
/*      */   
/*      */ 
/*      */   public String getNickName()
/*      */   {
/*  285 */     return this.nick_name;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getVersion()
/*      */   {
/*  291 */     return this.version;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setVersion(int v)
/*      */   {
/*  298 */     if (this.version < v)
/*      */     {
/*  300 */       this.version = v;
/*      */       
/*  302 */       this.plugin.fireDetailsChanged(this);
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public String getLocalAuthorisedRSSTagsOrCategoriesAsString()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 939	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddy:rss_lock	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: aload_0
/*      */     //   9: getfield 951	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddy:rss_local_cats	Ljava/util/Set;
/*      */     //   12: invokevirtual 999	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddy:catsToString	(Ljava/util/Set;)Ljava/lang/String;
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: areturn
/*      */     //   18: astore_2
/*      */     //   19: aload_1
/*      */     //   20: monitorexit
/*      */     //   21: aload_2
/*      */     //   22: athrow
/*      */     // Line number table:
/*      */     //   Java source line #309	-> byte code offset #0
/*      */     //   Java source line #311	-> byte code offset #7
/*      */     //   Java source line #312	-> byte code offset #18
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	23	0	this	BuddyPluginBuddy
/*      */     //   5	15	1	Ljava/lang/Object;	Object
/*      */     //   18	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	17	18	finally
/*      */     //   18	21	18	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public Set<String> getLocalAuthorisedRSSTagsOrCategories()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 939	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddy:rss_lock	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 951	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddy:rss_local_cats	Ljava/util/Set;
/*      */     //   11: aload_1
/*      */     //   12: monitorexit
/*      */     //   13: areturn
/*      */     //   14: astore_2
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: aload_2
/*      */     //   18: athrow
/*      */     // Line number table:
/*      */     //   Java source line #318	-> byte code offset #0
/*      */     //   Java source line #320	-> byte code offset #7
/*      */     //   Java source line #321	-> byte code offset #14
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	19	0	this	BuddyPluginBuddy
/*      */     //   5	11	1	Ljava/lang/Object;	Object
/*      */     //   14	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	13	14	finally
/*      */     //   14	17	14	finally
/*      */   }
/*      */   
/*      */   public void addLocalAuthorisedRSSTagOrCategory(String category)
/*      */   {
/*  328 */     category = this.plugin.normaliseCat(category);
/*      */     
/*      */     boolean dirty;
/*      */     
/*  332 */     synchronized (this.rss_lock)
/*      */     {
/*  334 */       if (this.rss_local_cats == null)
/*      */       {
/*  336 */         this.rss_local_cats = new HashSet();
/*      */       }
/*      */       
/*  339 */       if ((dirty = !this.rss_local_cats.contains(category) ? 1 : 0) != 0)
/*      */       {
/*  341 */         this.rss_local_cats.add(category);
/*      */       }
/*      */     }
/*      */     
/*  345 */     if (dirty)
/*      */     {
/*  347 */       this.plugin.setConfigDirty();
/*      */       
/*  349 */       this.plugin.fireDetailsChanged(this);
/*      */       
/*      */ 
/*      */ 
/*  353 */       if (isConnected())
/*      */       {
/*  355 */         sendKeepAlive();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeLocalAuthorisedRSSTagOrCategory(String category)
/*      */   {
/*  364 */     category = this.plugin.normaliseCat(category);
/*      */     
/*      */     boolean dirty;
/*      */     
/*  368 */     synchronized (this.rss_lock)
/*      */     {
/*  370 */       if (this.rss_local_cats == null)
/*      */       {
/*  372 */         return;
/*      */       }
/*      */       
/*      */ 
/*  376 */       dirty = this.rss_local_cats.remove(category);
/*      */     }
/*      */     
/*      */ 
/*  380 */     if (dirty)
/*      */     {
/*  382 */       this.plugin.setConfigDirty();
/*      */       
/*  384 */       this.plugin.fireDetailsChanged(this);
/*      */       
/*      */ 
/*      */ 
/*  388 */       if (isConnected())
/*      */       {
/*  390 */         sendKeepAlive();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setLocalAuthorisedRSSTagsOrCategories(String new_cats)
/*      */   {
/*  399 */     setLocalAuthorisedRSSTagsOrCategories(stringToCats(new_cats));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setLocalAuthorisedRSSTagsOrCategories(Set<String> new_cats)
/*      */   {
/*  406 */     this.plugin.normaliseCats(new_cats);
/*      */     
/*      */     boolean dirty;
/*      */     
/*  410 */     synchronized (this.rss_lock)
/*      */     {
/*  412 */       if ((dirty = !catsIdentical(new_cats, this.rss_local_cats) ? 1 : 0) != 0)
/*      */       {
/*  414 */         this.rss_local_cats = new_cats;
/*      */       }
/*      */     }
/*      */     
/*  418 */     if (dirty)
/*      */     {
/*  420 */       this.plugin.setConfigDirty();
/*      */       
/*  422 */       this.plugin.fireDetailsChanged(this);
/*      */       
/*      */ 
/*      */ 
/*  426 */       if (isConnected())
/*      */       {
/*  428 */         sendKeepAlive();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Set<String> getRemoteAuthorisedRSSTagsOrCategories()
/*      */   {
/*  436 */     return this.rss_remote_cats;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getRemoteAuthorisedRSSTagsOrCategoriesAsString()
/*      */   {
/*  442 */     return catsToString(this.rss_remote_cats);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setRemoteAuthorisedRSSTagsOrCategories(Set<String> new_cats)
/*      */   {
/*  449 */     this.plugin.normaliseCats(new_cats);
/*      */     
/*      */     boolean dirty;
/*      */     
/*  453 */     synchronized (this.rss_lock)
/*      */     {
/*  455 */       if ((dirty = !catsIdentical(new_cats, this.rss_remote_cats) ? 1 : 0) != 0)
/*      */       {
/*  457 */         this.rss_remote_cats = new_cats;
/*      */       }
/*      */     }
/*      */     
/*  461 */     if (dirty)
/*      */     {
/*  463 */       this.plugin.setConfigDirty();
/*      */       
/*  465 */       this.plugin.fireDetailsChanged(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isLocalRSSTagOrCategoryAuthorised(String category)
/*      */   {
/*  473 */     category = this.plugin.normaliseCat(category);
/*      */     
/*  475 */     synchronized (this.rss_lock)
/*      */     {
/*  477 */       if (this.rss_local_cats != null)
/*      */       {
/*  479 */         return this.rss_local_cats.contains(category);
/*      */       }
/*      */       
/*  482 */       return false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isRemoteRSSTagOrCategoryAuthorised(String category)
/*      */   {
/*  490 */     category = this.plugin.normaliseCat(category);
/*      */     
/*  492 */     synchronized (this.rss_lock)
/*      */     {
/*  494 */       if (this.rss_remote_cats != null)
/*      */       {
/*  496 */         return this.rss_remote_cats.contains(category);
/*      */       }
/*      */       
/*  499 */       return false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void localRSSTagOrCategoryRead(String str)
/*      */   {
/*      */     boolean dirty;
/*      */     
/*  509 */     synchronized (this.rss_lock)
/*      */     {
/*  511 */       if (this.rss_cats_read == null)
/*      */       {
/*  513 */         this.rss_cats_read = new HashSet();
/*      */       }
/*      */       
/*  516 */       dirty = this.rss_cats_read.add(str);
/*      */     }
/*      */     
/*  519 */     if (dirty)
/*      */     {
/*      */ 
/*      */ 
/*  523 */       this.plugin.fireDetailsChanged(this);
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public String getLocalReadTagsOrCategoriesAsString()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 939	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddy:rss_lock	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: aload_0
/*      */     //   9: getfield 950	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddy:rss_cats_read	Ljava/util/Set;
/*      */     //   12: invokevirtual 999	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddy:catsToString	(Ljava/util/Set;)Ljava/lang/String;
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: areturn
/*      */     //   18: astore_2
/*      */     //   19: aload_1
/*      */     //   20: monitorexit
/*      */     //   21: aload_2
/*      */     //   22: athrow
/*      */     // Line number table:
/*      */     //   Java source line #530	-> byte code offset #0
/*      */     //   Java source line #532	-> byte code offset #7
/*      */     //   Java source line #533	-> byte code offset #18
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	23	0	this	BuddyPluginBuddy
/*      */     //   5	15	1	Ljava/lang/Object;	Object
/*      */     //   18	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	17	18	finally
/*      */     //   18	21	18	finally
/*      */   }
/*      */   
/*      */   public URL getSubscriptionURL(String cat)
/*      */   {
/*  540 */     String url = "azplug:?id=azbuddy&name=Friends&arg=";
/*      */     
/*  542 */     String arg = "pk=" + getPublicKey() + "&cat=" + cat;
/*      */     try
/*      */     {
/*  545 */       url = url + URLEncoder.encode(arg, "UTF-8");
/*      */       
/*  547 */       return new URL(url);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  551 */       Debug.out(e);
/*      */     }
/*  553 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void subscribeToCategory(String cat)
/*      */     throws BuddyPluginException
/*      */   {
/*  563 */     AZ3Functions.provider az3 = AZ3Functions.getProvider();
/*      */     
/*  565 */     if (az3 == null)
/*      */     {
/*  567 */       throw new BuddyPluginException("AZ3 subsystem not available");
/*      */     }
/*      */     try
/*      */     {
/*  571 */       az3.subscribeToRSS(getName() + ": " + cat, getSubscriptionURL(cat), 15, false, getPublicKey() + ":" + cat);
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/*      */ 
/*  580 */       throw new BuddyPluginException("Failed to add subscription", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isSubscribedToTagOrCategory(String cat, String creator_ref)
/*      */   {
/*  589 */     if (creator_ref == null)
/*      */     {
/*  591 */       return false;
/*      */     }
/*      */     
/*  594 */     return creator_ref.equals(getPublicKey() + ":" + cat);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String catsToString(Set<String> cats)
/*      */   {
/*  601 */     if ((cats == null) || (cats.size() == 0))
/*      */     {
/*  603 */       return null;
/*      */     }
/*      */     
/*  606 */     String str = "";
/*      */     
/*  608 */     for (String s : cats)
/*      */     {
/*  610 */       str = str + (str.length() == 0 ? "" : ",") + s;
/*      */     }
/*      */     
/*  613 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean catsIdentical(Set<String> c1, Set<String> c2)
/*      */   {
/*  621 */     if ((c1 == null) && (c2 == null))
/*      */     {
/*  623 */       return true;
/*      */     }
/*  625 */     if ((c1 == null) || (c2 == null))
/*      */     {
/*  627 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  631 */     return c1.equals(c2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Set<String> stringToCats(String str)
/*      */   {
/*  639 */     if (str == null)
/*      */     {
/*  641 */       return null;
/*      */     }
/*      */     
/*  644 */     String[] bits = str.split(",");
/*      */     
/*  646 */     Set<String> res = new HashSet(bits.length);
/*      */     
/*  648 */     for (String b : bits)
/*      */     {
/*  650 */       b = b.trim();
/*      */       
/*  652 */       if (b.length() > 0)
/*      */       {
/*  654 */         res.add(b);
/*      */       }
/*      */     }
/*      */     
/*  658 */     if (res.size() == 0)
/*      */     {
/*  660 */       return null;
/*      */     }
/*      */     
/*  663 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getOnlineStatus()
/*      */   {
/*  669 */     return this.online_status;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setOnlineStatus(int s)
/*      */   {
/*  676 */     if (this.online_status != s)
/*      */     {
/*  678 */       this.online_status = s;
/*      */       
/*  680 */       this.plugin.fireDetailsChanged(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getName()
/*      */   {
/*  687 */     if (this.nick_name != null)
/*      */     {
/*  689 */       return this.nick_name;
/*      */     }
/*      */     
/*  692 */     return getShortString();
/*      */   }
/*      */   
/*      */ 
/*      */   public void remove()
/*      */   {
/*  698 */     this.persistent_msg_handler.destroy();
/*      */     
/*  700 */     this.plugin.removeBuddy(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public InetAddress getIP()
/*      */   {
/*  706 */     return this.current_ip;
/*      */   }
/*      */   
/*      */ 
/*      */   public InetAddress getAdjustedIP()
/*      */   {
/*  712 */     if (this.current_ip == null)
/*      */     {
/*  714 */       return null;
/*      */     }
/*      */     
/*  717 */     InetSocketAddress address = new InetSocketAddress(this.current_ip, this.tcp_port);
/*      */     
/*  719 */     InetSocketAddress adjusted_address = AddressUtils.adjustTCPAddress(address, true);
/*      */     
/*  721 */     if (adjusted_address != address)
/*      */     {
/*  723 */       return adjusted_address.getAddress();
/*      */     }
/*      */     
/*  726 */     address = new InetSocketAddress(this.current_ip, this.udp_port);
/*      */     
/*  728 */     adjusted_address = AddressUtils.adjustUDPAddress(address, true);
/*      */     
/*  730 */     if (adjusted_address != address)
/*      */     {
/*  732 */       return adjusted_address.getAddress();
/*      */     }
/*      */     
/*  735 */     return this.current_ip;
/*      */   }
/*      */   
/*      */ 
/*      */   public List<InetAddress> getAdjustedIPs()
/*      */   {
/*  741 */     List<InetAddress> result = new ArrayList();
/*      */     
/*  743 */     if (this.current_ip == null)
/*      */     {
/*  745 */       return result;
/*      */     }
/*      */     
/*  748 */     InetAddress adjusted = getAdjustedIP();
/*      */     
/*  750 */     if (adjusted == this.current_ip)
/*      */     {
/*  752 */       result.add(this.current_ip);
/*      */     }
/*      */     else
/*      */     {
/*  756 */       List l = AddressUtils.getLANAddresses(adjusted.getHostAddress());
/*      */       
/*  758 */       for (int i = 0; i < l.size(); i++) {
/*      */         try
/*      */         {
/*  761 */           result.add(InetAddress.getByName((String)l.get(i)));
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  769 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getTCPPort()
/*      */   {
/*  776 */     return this.tcp_port;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getUDPPort()
/*      */   {
/*  782 */     return this.udp_port;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isOnline(boolean is_connected)
/*      */   {
/*  789 */     boolean connected = isConnected();
/*      */     
/*      */ 
/*      */ 
/*  793 */     if (connected)
/*      */     {
/*  795 */       return true;
/*      */     }
/*      */     
/*  798 */     if (!this.online)
/*      */     {
/*  800 */       return false;
/*      */     }
/*      */     
/*  803 */     if (is_connected)
/*      */     {
/*  805 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  809 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean isIdle()
/*      */   {
/*  816 */     synchronized (this)
/*      */     {
/*  818 */       return this.connections.size() == 0;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public long getLastTimeOnline()
/*      */   {
/*  825 */     return this.last_time_online;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public BuddyPlugin.cryptoResult encrypt(byte[] payload)
/*      */     throws BuddyPluginException
/*      */   {
/*  834 */     return this.plugin.encrypt(this, payload);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public BuddyPlugin.cryptoResult decrypt(byte[] payload)
/*      */     throws BuddyPluginException
/*      */   {
/*  843 */     return this.plugin.decrypt(this, payload, getName());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean verify(byte[] payload, byte[] signature)
/*      */     throws BuddyPluginException
/*      */   {
/*  854 */     return this.plugin.verify(this, payload, signature);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public BuddyPluginBuddyMessage storeMessage(int type, Map msg)
/*      */   {
/*  862 */     return this.persistent_msg_handler.storeExplicitMessage(type, msg);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List<BuddyPluginBuddyMessage> retrieveMessages(int type)
/*      */   {
/*  869 */     return this.persistent_msg_handler.retrieveExplicitMessages(type);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMessagePending()
/*      */     throws BuddyPluginException
/*      */   {
/*  877 */     synchronized (this)
/*      */     {
/*  879 */       if (this.ygm_active)
/*      */       {
/*  881 */         this.ygm_pending = true;
/*      */         
/*  883 */         return;
/*      */       }
/*      */       
/*  886 */       this.ygm_active = true;
/*      */     }
/*      */     
/*  889 */     this.plugin.setMessagePending(this, new BuddyPlugin.operationListener()
/*      */     {
/*      */       public void complete()
/*      */       {
/*      */         boolean retry;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  898 */         synchronized (BuddyPluginBuddy.this)
/*      */         {
/*  900 */           BuddyPluginBuddy.this.ygm_active = false;
/*      */           
/*  902 */           retry = BuddyPluginBuddy.this.ygm_pending;
/*      */           
/*  904 */           BuddyPluginBuddy.this.ygm_pending = false;
/*      */         }
/*      */         
/*  907 */         if (retry) {
/*      */           try
/*      */           {
/*  910 */             BuddyPluginBuddy.this.setMessagePending();
/*      */           }
/*      */           catch (BuddyPluginException e)
/*      */           {
/*  914 */             BuddyPluginBuddy.this.log("Failed to send YGM", e);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public long getLastMessagePending()
/*      */   {
/*  924 */     return this.latest_ygm_time;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean addYGMMarker(long marker)
/*      */   {
/*  931 */     Long l = new Long(marker);
/*      */     
/*  933 */     synchronized (this)
/*      */     {
/*  935 */       if (this.recent_ygm == null)
/*      */       {
/*  937 */         this.recent_ygm = new ArrayList();
/*      */       }
/*      */       
/*  940 */       if (this.recent_ygm.contains(l))
/*      */       {
/*  942 */         return false;
/*      */       }
/*      */       
/*  945 */       this.recent_ygm.add(l);
/*      */       
/*  947 */       if (this.recent_ygm.size() > 16)
/*      */       {
/*  949 */         this.recent_ygm.remove(0);
/*      */       }
/*      */       
/*  952 */       this.latest_ygm_time = SystemTime.getCurrentTime();
/*      */     }
/*      */     
/*  955 */     this.plugin.setConfigDirty();
/*      */     
/*  957 */     this.plugin.fireDetailsChanged(this);
/*      */     
/*  959 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setLastMessageReceived(String str)
/*      */   {
/*  966 */     this.last_message_received = str;
/*      */     
/*  968 */     this.plugin.fireDetailsChanged(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getLastMessageReceived()
/*      */   {
/*  974 */     return this.last_message_received == null ? "" : this.last_message_received;
/*      */   }
/*      */   
/*      */ 
/*      */   protected List<Long> getYGMMarkers()
/*      */   {
/*  980 */     synchronized (this)
/*      */     {
/*  982 */       return this.recent_ygm == null ? null : new ArrayList(this.recent_ygm);
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected int getLastStatusSeq()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_1
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: getfield 908	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddy:last_status_seq	I
/*      */     //   8: aload_1
/*      */     //   9: monitorexit
/*      */     //   10: ireturn
/*      */     //   11: astore_2
/*      */     //   12: aload_1
/*      */     //   13: monitorexit
/*      */     //   14: aload_2
/*      */     //   15: athrow
/*      */     // Line number table:
/*      */     //   Java source line #989	-> byte code offset #0
/*      */     //   Java source line #991	-> byte code offset #4
/*      */     //   Java source line #992	-> byte code offset #11
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	16	0	this	BuddyPluginBuddy
/*      */     //   2	11	1	Ljava/lang/Object;	Object
/*      */     //   11	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	10	11	finally
/*      */     //   11	14	11	finally
/*      */   }
/*      */   
/*      */   protected void buddyConnectionEstablished(boolean outgoing)
/*      */   {
/*  999 */     buddyActive();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void buddyMessageSent(int size, boolean record_active)
/*      */   {
/* 1007 */     this.message_out_count += 1;
/* 1008 */     this.message_out_bytes += size;
/*      */     
/* 1010 */     if (record_active)
/*      */     {
/* 1012 */       buddyActive();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void buddyMessageReceived(int size)
/*      */   {
/* 1020 */     this.message_in_count += 1;
/* 1021 */     this.message_in_bytes += size;
/*      */     
/* 1023 */     this.received_frag_details = "";
/*      */     
/* 1025 */     buddyActive();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void buddyMessageFragmentReceived(int num_received, int total)
/*      */   {
/* 1033 */     this.received_frag_details = (num_received + "/" + total);
/*      */     
/* 1035 */     this.plugin.fireDetailsChanged(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getMessageInFragmentDetails()
/*      */   {
/* 1041 */     return this.received_frag_details;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMessageInCount()
/*      */   {
/* 1047 */     return this.message_in_count;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMessageOutCount()
/*      */   {
/* 1053 */     return this.message_out_count;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getBytesInCount()
/*      */   {
/* 1059 */     return this.message_in_bytes;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getBytesOutCount()
/*      */   {
/* 1065 */     return this.message_out_bytes;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isConnected()
/*      */   {
/* 1071 */     boolean connected = false;
/*      */     
/* 1073 */     synchronized (this)
/*      */     {
/* 1075 */       for (int i = 0; i < this.connections.size(); i++)
/*      */       {
/* 1077 */         buddyConnection c = (buddyConnection)this.connections.get(i);
/*      */         
/* 1079 */         if ((c.isConnected()) && (!c.hasFailed()))
/*      */         {
/* 1081 */           connected = true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1086 */     return connected;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void buddyActive()
/*      */   {
/* 1092 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 1094 */     synchronized (this)
/*      */     {
/* 1096 */       this.last_time_online = now;
/* 1097 */       this.online = true;
/*      */     }
/*      */     
/* 1100 */     persistentDispatchPending();
/*      */     
/* 1102 */     this.plugin.fireDetailsChanged(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public void ping()
/*      */     throws BuddyPluginException
/*      */   {
/* 1109 */     this.plugin.checkAvailable();
/*      */     try
/*      */     {
/* 1112 */       Map ping_request = new HashMap();
/*      */       
/* 1114 */       ping_request.put("type", new Long(1L));
/*      */       
/* 1116 */       sendMessage(0, ping_request, 60000, new BuddyPluginBuddyReplyListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void replyReceived(BuddyPluginBuddy from_buddy, Map reply)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1127 */           BuddyPluginBuddy.this.log("Ping reply received:" + reply);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void sendFailed(BuddyPluginBuddy to_buddy, BuddyPluginException cause)
/*      */         {
/* 1135 */           BuddyPluginBuddy.this.log("Ping failed to " + BuddyPluginBuddy.this.getString(), cause);
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1141 */       throw new BuddyPluginException("Ping failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void sendCloseRequest(boolean restarting)
/*      */   {
/* 1149 */     List to_send = new ArrayList();
/*      */     
/* 1151 */     synchronized (this)
/*      */     {
/* 1153 */       this.closing = true;
/*      */       
/* 1155 */       for (int i = 0; i < this.connections.size(); i++)
/*      */       {
/* 1157 */         buddyConnection c = (buddyConnection)this.connections.get(i);
/*      */         
/* 1159 */         if ((c.isConnected()) && (!c.hasFailed()) && (!c.isActive()))
/*      */         {
/* 1161 */           to_send.add(c);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1166 */     for (int i = 0; i < to_send.size(); i++)
/*      */     {
/* 1168 */       buddyConnection c = (buddyConnection)to_send.get(i);
/*      */       try
/*      */       {
/* 1171 */         Map close_request = new HashMap();
/*      */         
/* 1173 */         close_request.put("type", new Long(3L));
/*      */         
/* 1175 */         close_request.put("r", new Long(restarting ? 1L : 0L));
/*      */         
/* 1177 */         close_request.put("os", new Long(this.plugin.getCurrentStatusSeq()));
/*      */         
/* 1179 */         buddyMessage message = new buddyMessage(0, close_request, 60000);
/*      */         
/*      */ 
/* 1182 */         message.setListener(new BuddyPluginBuddyReplyListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void replyReceived(BuddyPluginBuddy from_buddy, Map reply)
/*      */           {
/*      */ 
/*      */ 
/* 1190 */             BuddyPluginBuddy.this.log("Close reply received:" + reply);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void sendFailed(BuddyPluginBuddy to_buddy, BuddyPluginException cause)
/*      */           {
/* 1198 */             BuddyPluginBuddy.this.log("Close failed to " + BuddyPluginBuddy.this.getString(), cause);
/*      */           }
/*      */           
/* 1201 */         });
/* 1202 */         c.sendCloseMessage(message);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1206 */         log("Close request failed", e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void receivedCloseRequest(Map request)
/*      */   {
/* 1215 */     List<buddyConnection> closing = new ArrayList();
/*      */     
/* 1217 */     synchronized (this)
/*      */     {
/* 1219 */       closing.addAll(this.connections);
/*      */     }
/*      */     
/* 1222 */     for (int i = 0; i < closing.size(); i++)
/*      */     {
/* 1224 */       ((buddyConnection)closing.get(i)).remoteClosing();
/*      */     }
/*      */     try
/*      */     {
/* 1228 */       boolean restarting = ((Long)request.get("r")).longValue() == 1L;
/*      */       
/* 1230 */       if (restarting)
/*      */       {
/* 1232 */         logMessage("restarting");
/*      */       }
/*      */       else
/*      */       {
/* 1236 */         logMessage("going offline");
/*      */         
/* 1238 */         boolean details_change = false;
/*      */         
/* 1240 */         synchronized (this)
/*      */         {
/* 1242 */           if (this.offline_seq_set == null)
/*      */           {
/* 1244 */             this.offline_seq_set = new HashSet();
/*      */           }
/*      */           
/* 1247 */           this.offline_seq_set.add(new Long(this.last_status_seq));
/*      */           
/* 1249 */           this.offline_seq_set.add((Long)request.get("os"));
/*      */           
/* 1251 */           if (this.online)
/*      */           {
/* 1253 */             this.online = false;
/* 1254 */             this.consec_connect_fails = 0;
/*      */             
/* 1256 */             details_change = true;
/*      */           }
/*      */         }
/*      */         
/* 1260 */         if (details_change)
/*      */         {
/* 1262 */           this.plugin.fireDetailsChanged(this);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1267 */       Debug.out("Failed to decode close request", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void sendMessage(final int subsystem, final Map content, final int timeout_millis, final BuddyPluginBuddyReplyListener listener)
/*      */     throws BuddyPluginException
/*      */   {
/* 1280 */     this.plugin.checkAvailable();
/*      */     
/* 1282 */     boolean wait = false;
/*      */     
/* 1284 */     if (this.current_ip == null)
/*      */     {
/* 1286 */       synchronized (this)
/*      */       {
/* 1288 */         wait = this.check_active;
/*      */       }
/*      */       
/* 1291 */       if (!wait)
/*      */       {
/* 1293 */         if (SystemTime.getCurrentTime() - this.last_status_check_time > 30000L)
/*      */         {
/* 1295 */           this.plugin.updateBuddyStatus(this);
/*      */           
/* 1297 */           wait = true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1302 */     if (wait)
/*      */     {
/* 1304 */       new AEThread2("BuddyPluginBuddy:sendWait", true)
/*      */       {
/*      */         public void run()
/*      */         {
/*      */           try
/*      */           {
/* 1310 */             long start = SystemTime.getCurrentTime();
/*      */             
/* 1312 */             for (int i = 0; i < 20; i++)
/*      */             {
/* 1314 */               if (BuddyPluginBuddy.this.current_ip != null) {
/*      */                 break;
/*      */               }
/*      */               
/*      */ 
/* 1319 */               Thread.sleep(1000L);
/*      */             }
/*      */             
/* 1322 */             long elapsed = SystemTime.getCurrentTime() - start;
/*      */             
/* 1324 */             int new_tm = timeout_millis;
/*      */             
/* 1326 */             if ((elapsed > 0L) && (timeout_millis > 0))
/*      */             {
/* 1328 */               new_tm = (int)(new_tm - elapsed);
/*      */               
/* 1330 */               if (new_tm <= 0)
/*      */               {
/* 1332 */                 listener.sendFailed(BuddyPluginBuddy.this, new BuddyPluginException("Timeout"));
/*      */                 
/* 1334 */                 return;
/*      */               }
/*      */             }
/*      */             
/* 1338 */             BuddyPluginBuddy.this.sendMessageSupport(content, subsystem, new_tm, listener);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1342 */             if ((e instanceof BuddyPluginException))
/*      */             {
/* 1344 */               listener.sendFailed(BuddyPluginBuddy.this, (BuddyPluginException)e);
/*      */             }
/*      */             else {
/* 1347 */               listener.sendFailed(BuddyPluginBuddy.this, new BuddyPluginException("Send failed", e));
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */         }
/*      */       }.start();
/*      */     } else {
/* 1355 */       sendMessageSupport(content, subsystem, timeout_millis, listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void sendMessageSupport(Map content, int subsystem, int timeout_millis, final BuddyPluginBuddyReplyListener original_listener)
/*      */     throws BuddyPluginException
/*      */   {
/* 1368 */     boolean too_many_messages = false;
/*      */     
/* 1370 */     synchronized (this)
/*      */     {
/* 1372 */       too_many_messages = this.messages.size() >= 256;
/*      */     }
/*      */     
/* 1375 */     if (too_many_messages)
/*      */     {
/* 1377 */       throw new BuddyPluginException("Too many messages queued");
/*      */     }
/*      */     
/* 1380 */     final buddyMessage message = new buddyMessage(subsystem, content, timeout_millis);
/*      */     
/* 1382 */     Object listener_delegate = new BuddyPluginBuddyReplyListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void replyReceived(BuddyPluginBuddy from_buddy, Map reply)
/*      */       {
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/*      */ 
/* 1393 */           synchronized (BuddyPluginBuddy.this)
/*      */           {
/* 1395 */             if (BuddyPluginBuddy.this.current_message != message)
/*      */             {
/* 1397 */               Debug.out("Inconsistent: reply received not for current message");
/*      */             }
/*      */             
/* 1400 */             BuddyPluginBuddy.this.current_message = null;
/*      */           }
/*      */           
/* 1403 */           original_listener.replyReceived(from_buddy, reply);
/*      */         }
/*      */         finally
/*      */         {
/* 1407 */           BuddyPluginBuddy.this.dispatchMessage();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void sendFailed(BuddyPluginBuddy to_buddy, BuddyPluginException cause)
/*      */       {
/* 1416 */         BuddyPluginBuddy.this.logMessage("Msg " + message.getString() + " failed: " + Debug.getNestedExceptionMessage(cause));
/*      */         
/*      */         try
/*      */         {
/*      */           boolean was_active;
/*      */           
/*      */           boolean was_active;
/*      */           
/* 1424 */           if ((cause instanceof BuddyPluginTimeoutException))
/*      */           {
/* 1426 */             was_active = ((BuddyPluginTimeoutException)cause).wasActive();
/*      */           }
/*      */           else
/*      */           {
/* 1430 */             was_active = true;
/*      */           }
/*      */           
/* 1433 */           if (was_active)
/*      */           {
/* 1435 */             synchronized (BuddyPluginBuddy.this)
/*      */             {
/* 1437 */               if (BuddyPluginBuddy.this.current_message != message)
/*      */               {
/* 1439 */                 Debug.out("Inconsistent: error received not for current message");
/*      */               }
/*      */               
/* 1442 */               BuddyPluginBuddy.this.current_message = null;
/*      */             }
/*      */           }
/*      */           
/* 1446 */           long now = SystemTime.getCurrentTime();
/*      */           
/* 1448 */           int retry_count = message.getRetryCount();
/*      */           
/* 1450 */           if ((retry_count < 1) && (!message.timedOut(now)))
/*      */           {
/* 1452 */             message.setRetry();
/*      */             
/*      */ 
/*      */ 
/* 1456 */             synchronized (BuddyPluginBuddy.this)
/*      */             {
/* 1458 */               BuddyPluginBuddy.this.messages.add(0, message);
/*      */             }
/*      */           }
/*      */           else {
/* 1462 */             original_listener.sendFailed(to_buddy, cause);
/*      */           }
/*      */         }
/*      */         finally {
/* 1466 */           BuddyPluginBuddy.this.dispatchMessage();
/*      */         }
/*      */         
/*      */       }
/* 1470 */     };
/* 1471 */     message.setListener((BuddyPluginBuddyReplyListener)listener_delegate);
/*      */     
/*      */     int size;
/*      */     
/* 1475 */     synchronized (this)
/*      */     {
/* 1477 */       this.messages.add(message);
/*      */       
/* 1479 */       size = this.messages.size();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1484 */     dispatchMessage();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void dispatchMessage()
/*      */   {
/* 1490 */     buddyConnection bc = null;
/*      */     
/* 1492 */     buddyMessage allocated_message = null;
/* 1493 */     Throwable failed_msg_error = null;
/*      */     
/* 1495 */     boolean inform_dirty = false;
/*      */     
/* 1497 */     synchronized (this)
/*      */     {
/* 1499 */       if ((this.current_message != null) || (this.messages.size() == 0) || (this.closing))
/*      */       {
/* 1501 */         return;
/*      */       }
/*      */       
/* 1504 */       allocated_message = this.current_message = (buddyMessage)this.messages.remove(0);
/*      */       
/* 1506 */       for (int i = 0; i < this.connections.size(); i++)
/*      */       {
/* 1508 */         buddyConnection c = (buddyConnection)this.connections.get(i);
/*      */         
/* 1510 */         if (!c.hasFailed())
/*      */         {
/* 1512 */           bc = c;
/*      */         }
/*      */       }
/*      */       
/* 1516 */       if (bc == null)
/*      */       {
/* 1518 */         if (this.destroyed)
/*      */         {
/* 1520 */           failed_msg_error = new BuddyPluginException("Friend destroyed");
/*      */         }
/* 1522 */         else if (this.connections.size() >= 5)
/*      */         {
/* 1524 */           failed_msg_error = new BuddyPluginException("Too many active connections");
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1529 */     if (failed_msg_error != null)
/*      */     {
/* 1531 */       allocated_message.reportFailed(failed_msg_error);
/*      */       
/* 1533 */       return;
/*      */     }
/*      */     
/* 1536 */     if (bc == null)
/*      */     {
/*      */       try
/*      */       {
/*      */ 
/* 1541 */         this.outgoing_connect_sem.reserve();
/*      */         
/* 1543 */         synchronized (this)
/*      */         {
/* 1545 */           if (this.current_message != allocated_message)
/*      */           {
/* 1547 */             failed_msg_error = new BuddyPluginException("current message no longer active");
/*      */           }
/* 1549 */           else if (this.closing) {
/*      */             return;
/*      */           }
/*      */           
/*      */ 
/* 1554 */           if (failed_msg_error == null)
/*      */           {
/* 1556 */             for (int i = 0; i < this.connections.size(); i++)
/*      */             {
/* 1558 */               buddyConnection c = (buddyConnection)this.connections.get(i);
/*      */               
/* 1560 */               if (!c.hasFailed())
/*      */               {
/* 1562 */                 bc = c;
/*      */               }
/*      */             }
/*      */             
/* 1566 */             if (bc == null)
/*      */             {
/* 1568 */               if (this.destroyed)
/*      */               {
/* 1570 */                 failed_msg_error = new BuddyPluginException("Friend destroyed");
/*      */               }
/* 1572 */               else if (this.connections.size() >= 5)
/*      */               {
/* 1574 */                 failed_msg_error = new BuddyPluginException("Too many active connections");
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1580 */         if ((bc == null) && (failed_msg_error == null))
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/* 1586 */             GenericMessageConnection generic_connection = outgoingConnection();
/*      */             
/* 1588 */             synchronized (this)
/*      */             {
/* 1590 */               if (this.current_message != allocated_message)
/*      */               {
/* 1592 */                 failed_msg_error = new BuddyPluginException("current message no longer active");
/*      */                 
/* 1594 */                 generic_connection.close();
/*      */               }
/*      */               else
/*      */               {
/* 1598 */                 bc = new buddyConnection(generic_connection, true);
/*      */                 
/* 1600 */                 inform_dirty = this.connections.size() == 0;
/*      */                 
/* 1602 */                 this.connections.add(bc);
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1609 */             failed_msg_error = e;
/*      */           }
/*      */         }
/*      */       }
/*      */       finally {
/* 1614 */         this.outgoing_connect_sem.release();
/*      */       }
/*      */     }
/*      */     
/* 1618 */     if (failed_msg_error != null)
/*      */     {
/* 1620 */       allocated_message.reportFailed(failed_msg_error);
/*      */       
/* 1622 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */     try
/*      */     {
/* 1628 */       bc.sendMessage(allocated_message);
/*      */     }
/*      */     catch (BuddyPluginException e)
/*      */     {
/* 1632 */       allocated_message.reportFailed(e);
/*      */     }
/*      */     
/* 1635 */     if (inform_dirty)
/*      */     {
/* 1637 */       this.plugin.setConfigDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void removeConnection(buddyConnection bc)
/*      */   {
/*      */     int size;
/*      */     
/* 1647 */     synchronized (this)
/*      */     {
/* 1649 */       this.connections.remove(bc);
/*      */       
/* 1651 */       size = this.connections.size();
/*      */     }
/*      */     
/* 1654 */     if (size == 0)
/*      */     {
/* 1656 */       this.plugin.setConfigDirty();
/*      */     }
/*      */     
/* 1659 */     if ((size == 0) && (bc.isConnected()) && (!bc.isClosing()) && (!bc.isRemoteClosing()))
/*      */     {
/*      */ 
/*      */ 
/* 1663 */       if (this.consec_connect_fails < 3)
/*      */       {
/* 1665 */         if (this.consec_connect_fails == 0)
/*      */         {
/* 1667 */           long now = SystemTime.getMonotonousTime();
/*      */           
/* 1669 */           boolean do_it = false;
/*      */           
/* 1671 */           synchronized (this)
/*      */           {
/* 1673 */             if ((this.last_auto_reconnect == -1L) || (now - this.last_auto_reconnect > 30000L))
/*      */             {
/*      */ 
/* 1676 */               this.last_auto_reconnect = now;
/*      */               
/* 1678 */               do_it = true;
/*      */             }
/*      */           }
/*      */           
/* 1682 */           if (do_it)
/*      */           {
/*      */ 
/*      */ 
/* 1686 */             new DelayedEvent("BuddyPluginBuddy:recon", new Random().nextInt(3000), new AERunnable()
/*      */             {
/*      */               public void runSupport()
/*      */               {
/*      */                 int size;
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1696 */                 synchronized (BuddyPluginBuddy.this)
/*      */                 {
/* 1698 */                   size = BuddyPluginBuddy.this.connections.size();
/*      */                 }
/*      */                 
/* 1701 */                 if ((BuddyPluginBuddy.this.consec_connect_fails == 0) && (size == 0))
/*      */                 {
/* 1703 */                   BuddyPluginBuddy.this.log("Attempting reconnect after dropped connection");
/*      */                   
/* 1705 */                   BuddyPluginBuddy.this.sendKeepAlive();
/*      */                 }
/*      */               }
/*      */             });
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1713 */           long delay = 60000L;
/*      */           
/* 1715 */           delay <<= Math.min(3, this.consec_connect_fails);
/*      */           
/* 1717 */           if (SystemTime.getCurrentTime() - this.last_connect_attempt >= delay)
/*      */           {
/* 1719 */             sendKeepAlive();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1729 */     this.plugin.fireDetailsChanged(this);
/*      */     
/* 1731 */     dispatchMessage();
/*      */   }
/*      */   
/*      */ 
/*      */   protected long getLastStatusCheckTime()
/*      */   {
/* 1737 */     return this.last_status_check_time;
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected boolean statusCheckActive()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_1
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: getfield 929	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddy:check_active	Z
/*      */     //   8: aload_1
/*      */     //   9: monitorexit
/*      */     //   10: ireturn
/*      */     //   11: astore_2
/*      */     //   12: aload_1
/*      */     //   13: monitorexit
/*      */     //   14: aload_2
/*      */     //   15: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1743	-> byte code offset #0
/*      */     //   Java source line #1745	-> byte code offset #4
/*      */     //   Java source line #1746	-> byte code offset #11
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	16	0	this	BuddyPluginBuddy
/*      */     //   2	11	1	Ljava/lang/Object;	Object
/*      */     //   11	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	10	11	finally
/*      */     //   11	14	11	finally
/*      */   }
/*      */   
/*      */   protected boolean statusCheckStarts()
/*      */   {
/* 1752 */     synchronized (this)
/*      */     {
/* 1754 */       if (this.check_active)
/*      */       {
/* 1756 */         return false;
/*      */       }
/*      */       
/* 1759 */       this.last_status_check_time = SystemTime.getCurrentTime();
/*      */       
/* 1761 */       this.check_active = true;
/*      */     }
/*      */     
/* 1764 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void statusCheckFailed()
/*      */   {
/* 1770 */     boolean details_change = false;
/*      */     
/* 1772 */     synchronized (this)
/*      */     {
/*      */       try {
/* 1775 */         if (this.online)
/*      */         {
/* 1777 */           this.online = false;
/* 1778 */           this.consec_connect_fails = 0;
/*      */           
/* 1780 */           details_change = true;
/*      */         }
/*      */       }
/*      */       finally {
/* 1784 */         this.status_check_count += 1L;
/*      */         
/* 1786 */         this.check_active = false;
/*      */       }
/*      */     }
/*      */     
/* 1790 */     if (details_change)
/*      */     {
/* 1792 */       this.plugin.fireDetailsChanged(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setAddress(InetAddress address)
/*      */   {
/* 1800 */     if (this.plugin.getPeersAreLANLocal())
/*      */     {
/* 1802 */       AddressUtils.addLANRateLimitAddress(address);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setCachedStatus(InetAddress _ip, int _tcp_port, int _udp_port)
/*      */   {
/* 1812 */     setAddress(_ip);
/*      */     
/* 1814 */     synchronized (this)
/*      */     {
/* 1816 */       if (this.current_ip == null)
/*      */       {
/* 1818 */         this.current_ip = _ip;
/* 1819 */         this.tcp_port = _tcp_port;
/* 1820 */         this.udp_port = _udp_port;
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
/*      */ 
/*      */ 
/*      */   protected void statusCheckComplete(long _post_time, InetAddress _ip, int _tcp_port, int _udp_port, String _nick_name, int _online_status, int _status_seq, int _version)
/*      */   {
/* 1836 */     boolean details_change = false;
/* 1837 */     boolean config_dirty = false;
/*      */     
/* 1839 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 1841 */     if (now < this.last_time_online)
/*      */     {
/* 1843 */       this.last_time_online = now;
/*      */     }
/*      */     
/* 1846 */     boolean is_connected = isConnected();
/*      */     
/* 1848 */     synchronized (this)
/*      */     {
/*      */ 
/*      */       try
/*      */       {
/* 1853 */         if (this.offline_seq_set != null)
/*      */         {
/* 1855 */           if (this.offline_seq_set.contains(new Long(_status_seq)))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1943 */             this.status_check_count += 1L;
/*      */             
/* 1945 */             this.check_active = false; return;
/*      */           }
/* 1861 */           this.offline_seq_set = null;
/*      */         }
/*      */         
/*      */ 
/* 1865 */         boolean seq_change = _status_seq != this.last_status_seq;
/*      */         
/*      */ 
/*      */         boolean timed_out;
/*      */         
/*      */ 
/* 1871 */         if (seq_change)
/*      */         {
/* 1873 */           this.last_status_seq = _status_seq;
/* 1874 */           this.last_time_online = now;
/*      */           
/* 1876 */           boolean timed_out = false;
/* 1877 */           details_change = true;
/*      */         }
/*      */         else
/*      */         {
/* 1881 */           timed_out = now - this.last_time_online >= 1800000L;
/*      */         }
/*      */         
/* 1884 */         if (this.online)
/*      */         {
/* 1886 */           if (timed_out)
/*      */           {
/* 1888 */             this.online = false;
/* 1889 */             this.consec_connect_fails = 0;
/*      */             
/* 1891 */             details_change = true;
/*      */           }
/*      */           
/*      */         }
/* 1895 */         else if ((seq_change) || (!timed_out))
/*      */         {
/* 1897 */           this.online = true;
/* 1898 */           details_change = true;
/*      */         }
/*      */         
/*      */ 
/* 1902 */         this.post_time = _post_time;
/*      */         
/* 1904 */         if ((!addressesEqual(this.current_ip, _ip)) || (this.tcp_port != _tcp_port) || (this.udp_port != _udp_port) || (this.version < _version))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1909 */           setAddress(_ip);
/*      */           
/* 1911 */           this.current_ip = _ip;
/* 1912 */           this.tcp_port = _tcp_port;
/* 1913 */           this.udp_port = _udp_port;
/*      */           
/* 1915 */           if (this.version < _version)
/*      */           {
/* 1917 */             this.version = _version;
/*      */           }
/*      */           
/* 1920 */           details_change = true;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1926 */         if ((!is_connected) && (this.online_status != _online_status))
/*      */         {
/*      */ 
/* 1929 */           this.online_status = _online_status;
/*      */           
/* 1931 */           details_change = true;
/*      */         }
/*      */         
/* 1934 */         if (!this.plugin.stringsEqual(this.nick_name, _nick_name))
/*      */         {
/* 1936 */           this.nick_name = _nick_name;
/*      */           
/* 1938 */           config_dirty = true;
/* 1939 */           details_change = true;
/*      */         }
/*      */       }
/*      */       finally {
/* 1943 */         this.status_check_count += 1L;
/*      */         
/* 1945 */         this.check_active = false;
/*      */       }
/*      */     }
/*      */     
/* 1949 */     if (config_dirty)
/*      */     {
/* 1951 */       this.plugin.setConfigDirty();
/*      */     }
/*      */     
/* 1954 */     if (details_change)
/*      */     {
/* 1956 */       if (this.online)
/*      */       {
/* 1958 */         persistentDispatchPending();
/*      */       }
/*      */       
/* 1961 */       this.plugin.fireDetailsChanged(this);
/*      */     }
/*      */     
/* 1964 */     this.plugin.logMessage(getString());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean addressesEqual(InetAddress ip1, InetAddress ip2)
/*      */   {
/* 1972 */     if ((ip1 == null) && (ip2 == null))
/*      */     {
/* 1974 */       return true;
/*      */     }
/* 1976 */     if ((ip1 == null) || (ip2 == null))
/*      */     {
/* 1978 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 1982 */     return ip1.equals(ip2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkTimeouts()
/*      */   {
/* 1989 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 1991 */     List failed = null;
/*      */     
/* 1993 */     List connections_to_check = null;
/*      */     
/*      */     boolean messages_queued;
/*      */     
/* 1997 */     synchronized (this)
/*      */     {
/* 1999 */       messages_queued = this.messages.size() > 0;
/*      */       
/* 2001 */       if (messages_queued)
/*      */       {
/* 2003 */         Iterator it = this.messages.iterator();
/*      */         
/* 2005 */         while (it.hasNext())
/*      */         {
/* 2007 */           buddyMessage message = (buddyMessage)it.next();
/*      */           
/* 2009 */           if (message.timedOut(now))
/*      */           {
/* 2011 */             it.remove();
/*      */             
/* 2013 */             if (failed == null)
/*      */             {
/* 2015 */               failed = new ArrayList();
/*      */             }
/*      */             
/* 2018 */             failed.add(message);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2023 */       if (this.connections.size() > 0)
/*      */       {
/* 2025 */         connections_to_check = new ArrayList(this.connections);
/*      */       }
/*      */     }
/*      */     
/* 2029 */     boolean send_keep_alive = false;
/*      */     
/* 2031 */     if (connections_to_check == null)
/*      */     {
/*      */ 
/*      */ 
/* 2035 */       if ((this.online) && (this.current_ip != null) && (!messages_queued))
/*      */       {
/*      */ 
/*      */ 
/* 2039 */         if (this.consec_connect_fails < 3)
/*      */         {
/* 2041 */           long delay = 60000L;
/*      */           
/* 2043 */           delay <<= Math.min(3, this.consec_connect_fails);
/*      */           
/* 2045 */           send_keep_alive = now - this.last_connect_attempt >= delay;
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 2050 */       for (int i = 0; i < connections_to_check.size(); i++)
/*      */       {
/* 2052 */         buddyConnection connection = (buddyConnection)connections_to_check.get(i);
/*      */         
/* 2054 */         boolean closed = connection.checkTimeout(now);
/*      */         
/* 2056 */         if ((this.current_ip != null) && (!closed) && (!messages_queued) && (connection.isConnected()) && (!connection.isActive()))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2062 */           if (now - connection.getLastActive(now) > 60000L)
/*      */           {
/* 2064 */             send_keep_alive = true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2070 */     if (send_keep_alive)
/*      */     {
/* 2072 */       sendKeepAlive();
/*      */     }
/*      */     
/* 2075 */     if (failed != null)
/*      */     {
/* 2077 */       for (int i = 0; i < failed.size(); i++)
/*      */       {
/* 2079 */         ((buddyMessage)failed.get(i)).reportFailed(new BuddyPluginTimeoutException("Timeout", false));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void sendKeepAlive()
/*      */   {
/* 2087 */     boolean send_keep_alive = true;
/*      */     
/* 2089 */     synchronized (this)
/*      */     {
/* 2091 */       if (this.keep_alive_outstanding)
/*      */       {
/* 2093 */         send_keep_alive = false;
/*      */       }
/*      */       else
/*      */       {
/* 2097 */         this.keep_alive_outstanding = true;
/*      */       }
/*      */     }
/*      */     
/* 2101 */     if (send_keep_alive) {
/*      */       try
/*      */       {
/* 2104 */         Map ping_request = new HashMap();
/*      */         
/* 2106 */         ping_request.put("type", new Long(1L));
/*      */         
/* 2108 */         sendMessageSupport(ping_request, 0, 60000, new BuddyPluginBuddyReplyListener()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void replyReceived(BuddyPluginBuddy from_buddy, Map reply)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 2119 */             synchronized (BuddyPluginBuddy.this)
/*      */             {
/* 2121 */               BuddyPluginBuddy.this.keep_alive_outstanding = false;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void sendFailed(BuddyPluginBuddy to_buddy, BuddyPluginException cause)
/*      */           {
/* 2130 */             synchronized (BuddyPluginBuddy.this)
/*      */             {
/* 2132 */               BuddyPluginBuddy.this.keep_alive_outstanding = false;
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2139 */         synchronized (this)
/*      */         {
/* 2141 */           this.keep_alive_outstanding = false;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getConnectionsString()
/*      */   {
/* 2150 */     synchronized (this)
/*      */     {
/* 2152 */       String str = "";
/*      */       
/* 2154 */       for (int i = 0; i < this.connections.size(); i++)
/*      */       {
/* 2156 */         str = str + (str.length() == 0 ? "" : ",") + ((buddyConnection)this.connections.get(i)).getString(true);
/*      */       }
/*      */       
/* 2159 */       return str;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void disconnect()
/*      */   {
/* 2166 */     List to_disconnect = new ArrayList();
/*      */     
/* 2168 */     synchronized (this)
/*      */     {
/* 2170 */       to_disconnect.addAll(this.connections);
/*      */     }
/*      */     
/* 2173 */     for (int i = 0; i < to_disconnect.size(); i++)
/*      */     {
/* 2175 */       ((buddyConnection)to_disconnect.get(i)).disconnect();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean isClosing()
/*      */   {
/* 2182 */     return this.closing;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void destroy()
/*      */   {
/* 2188 */     List<buddyConnection> to_close = new ArrayList();
/*      */     
/* 2190 */     synchronized (this)
/*      */     {
/* 2192 */       this.destroyed = true;
/*      */       
/* 2194 */       to_close.addAll(this.connections);
/*      */     }
/*      */     
/* 2197 */     for (int i = 0; i < to_close.size(); i++)
/*      */     {
/* 2199 */       ((buddyConnection)to_close.get(i)).close();
/*      */     }
/*      */     
/* 2202 */     InetAddress ip = this.current_ip;
/*      */     
/* 2204 */     if (ip != null)
/*      */     {
/* 2206 */       AddressUtils.removeLANRateLimitAddress(ip);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void logMessage(String str)
/*      */   {
/* 2214 */     this.plugin.logMessage(getShortString() + ": " + str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected GenericMessageConnection outgoingConnection()
/*      */     throws BuddyPluginException
/*      */   {
/* 2222 */     GenericMessageRegistration msg_registration = this.plugin.getMessageRegistration();
/*      */     
/* 2224 */     if (msg_registration == null)
/*      */     {
/* 2226 */       throw new BuddyPluginException("Messaging system unavailable");
/*      */     }
/*      */     
/* 2229 */     InetAddress ip = getIP();
/*      */     
/* 2231 */     if (ip == null)
/*      */     {
/* 2233 */       throw new BuddyPluginException("Friend offline (no usable IP address)");
/*      */     }
/*      */     
/* 2236 */     InetSocketAddress tcp_target = null;
/* 2237 */     InetSocketAddress udp_target = null;
/*      */     
/* 2239 */     int tcp_port = getTCPPort();
/*      */     
/* 2241 */     if (tcp_port > 0)
/*      */     {
/* 2243 */       tcp_target = new InetSocketAddress(ip, tcp_port);
/*      */     }
/*      */     
/* 2246 */     int udp_port = getUDPPort();
/*      */     
/* 2248 */     if (udp_port > 0)
/*      */     {
/* 2250 */       udp_target = new InetSocketAddress(ip, udp_port);
/*      */     }
/*      */     
/* 2253 */     InetSocketAddress notional_target = tcp_target;
/*      */     
/* 2255 */     if (notional_target == null)
/*      */     {
/* 2257 */       notional_target = udp_target;
/*      */     }
/*      */     
/* 2260 */     if (notional_target == null)
/*      */     {
/* 2262 */       throw new BuddyPluginException("Friend offline (no usable protocols)");
/*      */     }
/*      */     
/* 2265 */     GenericMessageEndpoint endpoint = msg_registration.createEndpoint(notional_target);
/*      */     
/* 2267 */     if (tcp_target != null)
/*      */     {
/* 2269 */       endpoint.addTCP(tcp_target);
/*      */     }
/*      */     
/* 2272 */     if (udp_target != null)
/*      */     {
/* 2274 */       endpoint.addUDP(udp_target);
/*      */     }
/*      */     
/* 2277 */     GenericMessageConnection con = null;
/*      */     try
/*      */     {
/* 2280 */       this.last_connect_attempt = SystemTime.getCurrentTime();
/*      */       
/* 2282 */       con = msg_registration.createConnection(endpoint);
/*      */       
/* 2284 */       this.plugin.addRateLimiters(con);
/*      */       
/* 2286 */       String reason = "Friend: Outgoing connection establishment";
/*      */       
/* 2288 */       SESecurityManager sec_man = this.plugin.getSecurityManager();
/*      */       
/* 2290 */       con = sec_man.getSTSConnection(con, sec_man.getPublicKey(1, reason), new SEPublicKeyLocator()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public boolean accept(Object context, SEPublicKey other_key)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 2301 */           String other_key_str = Base32.encode(other_key.encodeRawPublicKey());
/*      */           
/* 2303 */           if (other_key_str.equals(BuddyPluginBuddy.this.public_key))
/*      */           {
/* 2305 */             BuddyPluginBuddy.this.consec_connect_fails = 0;
/*      */             
/* 2307 */             return true;
/*      */           }
/*      */           
/*      */ 
/* 2311 */           BuddyPluginBuddy.this.log(BuddyPluginBuddy.this.getString() + ": connection failed due to pk mismatch");
/*      */           
/* 2313 */           return false; } }, reason, 2);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2320 */       con.connect();
/*      */       
/* 2322 */       return con;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2326 */       if (con != null)
/*      */       {
/* 2328 */         this.consec_connect_fails += 1;
/*      */         try
/*      */         {
/* 2331 */           con.close();
/*      */         }
/*      */         catch (Throwable f)
/*      */         {
/* 2335 */           log("Failed to close connection", f);
/*      */         }
/*      */       }
/*      */       
/* 2339 */       throw new BuddyPluginException("Failed to send message", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void incomingConnection(GenericMessageConnection _connection)
/*      */     throws BuddyPluginException
/*      */   {
/* 2349 */     addConnection(_connection);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addConnection(GenericMessageConnection _connection)
/*      */     throws BuddyPluginException
/*      */   {
/* 2360 */     buddyConnection bc = new buddyConnection(_connection, false);
/*      */     
/* 2362 */     boolean inform_dirty = false;
/*      */     
/* 2364 */     synchronized (this)
/*      */     {
/* 2366 */       if (this.destroyed)
/*      */       {
/* 2368 */         throw new BuddyPluginException("Friend has been destroyed");
/*      */       }
/*      */       
/* 2371 */       inform_dirty = this.connections.size() == 0;
/*      */       
/* 2373 */       this.connections.add(bc);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2380 */     if (inform_dirty)
/*      */     {
/* 2382 */       this.plugin.setConfigDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setUserData(Object key, Object value)
/*      */   {
/* 2391 */     synchronized (this.user_data)
/*      */     {
/* 2393 */       this.user_data.put(key, value);
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public Object getUserData(Object key)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 948	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddy:user_data	Ljava/util/Map;
/*      */     //   4: dup
/*      */     //   5: astore_2
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 948	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddy:user_data	Ljava/util/Map;
/*      */     //   11: aload_1
/*      */     //   12: invokeinterface 1102 2 0
/*      */     //   17: aload_2
/*      */     //   18: monitorexit
/*      */     //   19: areturn
/*      */     //   20: astore_3
/*      */     //   21: aload_2
/*      */     //   22: monitorexit
/*      */     //   23: aload_3
/*      */     //   24: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2401	-> byte code offset #0
/*      */     //   Java source line #2403	-> byte code offset #7
/*      */     //   Java source line #2404	-> byte code offset #20
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	25	0	this	BuddyPluginBuddy
/*      */     //   0	25	1	key	Object
/*      */     //   5	17	2	Ljava/lang/Object;	Object
/*      */     //   20	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	19	20	finally
/*      */     //   20	23	20	finally
/*      */   }
/*      */   
/*      */   protected void log(String str)
/*      */   {
/* 2411 */     this.plugin.log(str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(String str, Throwable e)
/*      */   {
/* 2419 */     this.plugin.log(str, e);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getString()
/*      */   {
/* 2425 */     return "pk=" + getShortString() + (this.nick_name == null ? "" : new StringBuilder().append(",nick=").append(this.nick_name).toString()) + ",ip=" + this.current_ip + ",tcp=" + this.tcp_port + ",udp=" + this.udp_port + ",online=" + this.online + ",age=" + (SystemTime.getCurrentTime() - this.post_time);
/*      */   }
/*      */   
/*      */ 
/*      */   protected class buddyMessage
/*      */   {
/*      */     private int message_id;
/*      */     
/*      */     private Map request;
/*      */     
/*      */     private int subsystem;
/*      */     private BuddyPluginBuddyReplyListener listener;
/*      */     private int timeout_millis;
/* 2438 */     private long queue_time = SystemTime.getCurrentTime();
/*      */     
/*      */ 
/*      */     private boolean timed_out;
/*      */     
/*      */     private int retry_count;
/*      */     
/*      */     private boolean complete;
/*      */     
/*      */ 
/*      */     protected buddyMessage(int _subsystem, Map _request, int _timeout)
/*      */     {
/* 2450 */       synchronized (BuddyPluginBuddy.this)
/*      */       {
/* 2452 */         this.message_id = BuddyPluginBuddy.access$908(BuddyPluginBuddy.this);
/*      */       }
/*      */       
/* 2455 */       this.request = _request;
/* 2456 */       this.subsystem = _subsystem;
/* 2457 */       this.timeout_millis = _timeout;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setListener(BuddyPluginBuddyReplyListener _listener)
/*      */     {
/* 2464 */       this.listener = _listener;
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     protected int getRetryCount()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: dup
/*      */       //   2: astore_1
/*      */       //   3: monitorenter
/*      */       //   4: aload_0
/*      */       //   5: getfield 114	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddy$buddyMessage:retry_count	I
/*      */       //   8: aload_1
/*      */       //   9: monitorexit
/*      */       //   10: ireturn
/*      */       //   11: astore_2
/*      */       //   12: aload_1
/*      */       //   13: monitorexit
/*      */       //   14: aload_2
/*      */       //   15: athrow
/*      */       // Line number table:
/*      */       //   Java source line #2470	-> byte code offset #0
/*      */       //   Java source line #2472	-> byte code offset #4
/*      */       //   Java source line #2473	-> byte code offset #11
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	16	0	this	buddyMessage
/*      */       //   2	11	1	Ljava/lang/Object;	Object
/*      */       //   11	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   4	10	11	finally
/*      */       //   11	14	11	finally
/*      */     }
/*      */     
/*      */     protected void setDontRetry()
/*      */     {
/* 2479 */       this.retry_count = 99;
/*      */     }
/*      */     
/*      */ 
/*      */     protected void setRetry()
/*      */     {
/* 2485 */       synchronized (this)
/*      */       {
/* 2487 */         this.retry_count += 1;
/*      */         
/* 2489 */         this.complete = false;
/* 2490 */         this.timed_out = false;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean timedOut(long now)
/*      */     {
/* 2499 */       if (this.timed_out)
/*      */       {
/* 2501 */         return true;
/*      */       }
/*      */       
/* 2504 */       if (now < this.queue_time)
/*      */       {
/* 2506 */         this.queue_time = now;
/*      */         
/* 2508 */         return false;
/*      */       }
/*      */       
/*      */ 
/* 2512 */       this.timed_out = (now - this.queue_time >= this.timeout_millis);
/*      */       
/* 2514 */       return this.timed_out;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected Map getRequest()
/*      */     {
/* 2521 */       return this.request;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getSubsystem()
/*      */     {
/* 2527 */       return this.subsystem;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getID()
/*      */     {
/* 2533 */       return this.message_id;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void reportComplete(Map reply)
/*      */     {
/* 2540 */       synchronized (this)
/*      */       {
/* 2542 */         if (this.complete)
/*      */         {
/* 2544 */           return;
/*      */         }
/*      */         
/* 2547 */         this.complete = true;
/*      */       }
/*      */       try
/*      */       {
/* 2551 */         this.listener.replyReceived(BuddyPluginBuddy.this, reply);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2555 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void reportFailed(Throwable error)
/*      */     {
/* 2563 */       synchronized (this)
/*      */       {
/* 2565 */         if (this.complete)
/*      */         {
/* 2567 */           return;
/*      */         }
/*      */         
/* 2570 */         this.complete = true;
/*      */       }
/*      */       try
/*      */       {
/* 2574 */         if ((error instanceof BuddyPluginException))
/*      */         {
/* 2576 */           this.listener.sendFailed(BuddyPluginBuddy.this, (BuddyPluginException)error);
/*      */         }
/*      */         else
/*      */         {
/* 2580 */           this.listener.sendFailed(BuddyPluginBuddy.this, new BuddyPluginException("", error));
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 2584 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getString()
/*      */     {
/* 2591 */       return "id=" + this.message_id + ",ss=" + this.subsystem + (this.retry_count == 0 ? "" : new StringBuilder().append(",retry=").append(this.retry_count).toString());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected class buddyConnection
/*      */     implements BuddyPluginBuddy.fragmentHandlerReceiver
/*      */   {
/*      */     private BuddyPluginBuddy.fragmentHandler fragment_handler;
/*      */     
/*      */     private int connection_id;
/*      */     
/*      */     private boolean outgoing;
/*      */     
/*      */     private String dir_str;
/*      */     
/*      */     private volatile BuddyPluginBuddy.buddyMessage active_message;
/*      */     
/*      */     private volatile boolean connected;
/*      */     private volatile boolean closing;
/*      */     private volatile boolean remote_closing;
/*      */     private volatile boolean failed;
/* 2613 */     private long last_active = SystemTime.getCurrentTime();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected buddyConnection(GenericMessageConnection _connection, boolean _outgoing)
/*      */     {
/* 2620 */       this.fragment_handler = new BuddyPluginBuddy.fragmentHandler(BuddyPluginBuddy.this, _connection, this);
/*      */       
/* 2622 */       this.outgoing = _outgoing;
/*      */       
/* 2624 */       synchronized (BuddyPluginBuddy.this)
/*      */       {
/* 2626 */         this.connection_id = BuddyPluginBuddy.access$1008(BuddyPluginBuddy.this);
/*      */       }
/*      */       
/* 2629 */       this.dir_str = (this.outgoing ? "Outgoing" : "Incoming");
/*      */       
/* 2631 */       if (!this.outgoing)
/*      */       {
/* 2633 */         this.connected = true;
/*      */         
/* 2635 */         BuddyPluginBuddy.this.buddyConnectionEstablished(false);
/*      */       }
/*      */       
/* 2638 */       this.fragment_handler.start();
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isConnected()
/*      */     {
/* 2644 */       return this.connected;
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean hasFailed()
/*      */     {
/* 2650 */       return this.failed;
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isOutgoing()
/*      */     {
/* 2656 */       return this.outgoing;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected long getLastActive(long now)
/*      */     {
/* 2663 */       if (now < this.last_active)
/*      */       {
/* 2665 */         this.last_active = now;
/*      */       }
/*      */       
/* 2668 */       return this.last_active;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void sendMessage(BuddyPluginBuddy.buddyMessage message)
/*      */       throws BuddyPluginException
/*      */     {
/* 2677 */       BuddyPluginException failed_error = null;
/*      */       
/* 2679 */       BuddyPluginBuddy.buddyMessage msg_to_send = null;
/*      */       
/* 2681 */       synchronized (this)
/*      */       {
/* 2683 */         if (BuddyPluginBuddy.this.isClosing())
/*      */         {
/* 2685 */           throw new BuddyPluginException("Close in progress");
/*      */         }
/*      */         
/* 2688 */         if (this.active_message != null)
/*      */         {
/* 2690 */           Debug.out("Inconsistent: active message already set");
/*      */           
/* 2692 */           failed_error = new BuddyPluginException("Inconsistent state");
/*      */         } else {
/* 2694 */           if ((this.failed) || (this.closing))
/*      */           {
/* 2696 */             throw new BuddyPluginException("Connection failed");
/*      */           }
/*      */           
/*      */ 
/* 2700 */           this.active_message = message;
/*      */           
/* 2702 */           if (this.connected)
/*      */           {
/* 2704 */             msg_to_send = this.active_message;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2709 */       if (failed_error != null)
/*      */       {
/* 2711 */         failed(failed_error);
/*      */         
/* 2713 */         throw failed_error;
/*      */       }
/*      */       
/* 2716 */       if (msg_to_send != null)
/*      */       {
/* 2718 */         send(msg_to_send);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void sendCloseMessage(BuddyPluginBuddy.buddyMessage message)
/*      */     {
/*      */       boolean ok_to_send;
/*      */       
/* 2728 */       synchronized (this)
/*      */       {
/* 2730 */         ok_to_send = (this.active_message == null) && (this.connected) && (!this.failed) && (!this.closing);
/*      */       }
/*      */       
/* 2733 */       if (ok_to_send)
/*      */       {
/* 2735 */         send(message);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isActive()
/*      */     {
/* 2742 */       return this.active_message != null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void connected()
/*      */     {
/* 2752 */       BuddyPluginBuddy.buddyMessage msg_to_send = null;
/*      */       
/* 2754 */       synchronized (this)
/*      */       {
/* 2756 */         this.last_active = SystemTime.getCurrentTime();
/*      */         
/* 2758 */         this.connected = true;
/*      */         
/* 2760 */         msg_to_send = this.active_message;
/*      */       }
/*      */       
/* 2763 */       BuddyPluginBuddy.this.buddyConnectionEstablished(true);
/*      */       
/* 2765 */       if (msg_to_send != null)
/*      */       {
/* 2767 */         send(msg_to_send);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected boolean checkTimeout(long now)
/*      */     {
/* 2775 */       BuddyPluginBuddy.buddyMessage bm = null;
/*      */       
/* 2777 */       boolean close = false;
/*      */       
/* 2779 */       synchronized (this)
/*      */       {
/* 2781 */         if (this.active_message != null)
/*      */         {
/* 2783 */           if (this.active_message.timedOut(now))
/*      */           {
/* 2785 */             bm = this.active_message;
/*      */             
/* 2787 */             this.active_message = null;
/*      */           }
/*      */         }
/*      */         
/* 2791 */         if (now < this.last_active)
/*      */         {
/* 2793 */           this.last_active = now;
/*      */         }
/*      */         
/* 2796 */         if (now - this.last_active > 300000L)
/*      */         {
/* 2798 */           close = true;
/*      */         }
/*      */       }
/*      */       
/* 2802 */       if (bm != null)
/*      */       {
/* 2804 */         bm.reportFailed(new BuddyPluginTimeoutException("Timeout", true));
/*      */       }
/*      */       
/* 2807 */       if (close)
/*      */       {
/* 2809 */         close();
/*      */       }
/*      */       
/* 2812 */       return close;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void send(BuddyPluginBuddy.buddyMessage msg)
/*      */     {
/* 2819 */       Map request = msg.getRequest();
/*      */       
/* 2821 */       Map send_map = new HashMap();
/*      */       
/* 2823 */       send_map.put("type", new Long(1L));
/* 2824 */       send_map.put("req", request);
/* 2825 */       send_map.put("ss", new Long(msg.getSubsystem()));
/* 2826 */       send_map.put("id", new Long(msg.getID()));
/* 2827 */       send_map.put("oz", new Long(BuddyPluginBuddy.this.plugin.getOnlineStatus()));
/* 2828 */       send_map.put("v", new Long(2L));
/*      */       
/* 2830 */       String loc_cat = BuddyPluginBuddy.this.getLocalAuthorisedRSSTagsOrCategoriesAsString();
/*      */       
/* 2832 */       if (loc_cat != null) {
/* 2833 */         send_map.put("cat", loc_cat);
/*      */       }
/*      */       
/*      */ 
/*      */       try
/*      */       {
/* 2839 */         this.fragment_handler.send(send_map, true, true);
/*      */         
/* 2841 */         synchronized (this)
/*      */         {
/* 2843 */           this.last_active = SystemTime.getCurrentTime();
/*      */         }
/*      */       }
/*      */       catch (BuddyPluginException e) {
/*      */         try {
/* 2848 */           failed(e);
/*      */         }
/*      */         catch (Throwable f)
/*      */         {
/* 2852 */           Debug.printStackTrace(f);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void receive(Map data_map)
/*      */     {
/* 2861 */       synchronized (this)
/*      */       {
/* 2863 */         this.last_active = SystemTime.getCurrentTime();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/* 2871 */         int type = ((Long)data_map.get("type")).intValue();
/*      */         
/* 2873 */         Long l_os = (Long)data_map.get("oz");
/*      */         
/* 2875 */         if (l_os != null)
/*      */         {
/* 2877 */           BuddyPluginBuddy.this.setOnlineStatus(l_os.intValue());
/*      */         }
/*      */         
/* 2880 */         Long l_ver = (Long)data_map.get("v");
/*      */         
/* 2882 */         if (l_ver != null)
/*      */         {
/* 2884 */           BuddyPluginBuddy.this.setVersion(l_ver.intValue());
/*      */         }
/*      */         
/* 2887 */         byte[] b_rem_cat = (byte[])data_map.get("cat");
/*      */         
/* 2889 */         if (b_rem_cat == null)
/*      */         {
/* 2891 */           BuddyPluginBuddy.this.setRemoteAuthorisedRSSTagsOrCategories(null);
/*      */         }
/*      */         else
/*      */         {
/* 2895 */           BuddyPluginBuddy.this.setRemoteAuthorisedRSSTagsOrCategories(BuddyPluginBuddy.this.stringToCats(new String(b_rem_cat, "UTF-8")));
/*      */         }
/*      */         
/* 2898 */         if (type == 1)
/*      */         {
/*      */ 
/*      */ 
/* 2902 */           Long subsystem = (Long)data_map.get("ss");
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2908 */           Map request = (Map)data_map.get("req");
/*      */           
/* 2910 */           String error = null;
/*      */           Map reply;
/* 2912 */           Map reply; if ((request == null) || (subsystem == null))
/*      */           {
/* 2914 */             reply = null;
/*      */           }
/*      */           else
/*      */           {
/*      */             try
/*      */             {
/* 2920 */               reply = BuddyPluginBuddy.this.plugin.requestReceived(BuddyPluginBuddy.this, subsystem.intValue(), request);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 2924 */               error = Debug.getNestedExceptionMessage(e);
/*      */               
/* 2926 */               reply = null;
/*      */             }
/*      */           }
/*      */           int reply_type;
/* 2930 */           if (reply == null)
/*      */           {
/* 2932 */             int reply_type = 99;
/*      */             
/* 2934 */             reply = new HashMap();
/*      */             
/* 2936 */             reply.put("error", error == null ? "No handlers available to process request" : error);
/*      */           }
/*      */           else
/*      */           {
/* 2940 */             reply_type = 2;
/*      */           }
/*      */           
/* 2943 */           Map reply_map = new HashMap();
/*      */           
/* 2945 */           reply_map.put("ss", subsystem);
/* 2946 */           reply_map.put("type", new Long(reply_type));
/* 2947 */           reply_map.put("id", data_map.get("id"));
/* 2948 */           reply_map.put("oz", new Long(BuddyPluginBuddy.this.plugin.getOnlineStatus()));
/*      */           
/* 2950 */           String loc_cat = BuddyPluginBuddy.this.getLocalAuthorisedRSSTagsOrCategoriesAsString();
/*      */           
/* 2952 */           if (loc_cat != null) {
/* 2953 */             reply_map.put("cat", loc_cat);
/*      */           }
/*      */           
/* 2956 */           reply_map.put("rep", reply);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2962 */           this.fragment_handler.send(reply_map, false, false);
/*      */         }
/* 2964 */         else if ((type == 2) || (type == 99))
/*      */         {
/* 2966 */           long id = ((Long)data_map.get("id")).longValue();
/*      */           
/*      */           BuddyPluginBuddy.buddyMessage bm;
/*      */           
/* 2970 */           synchronized (this)
/*      */           {
/* 2972 */             if ((this.active_message != null) && (this.active_message.getID() == id))
/*      */             {
/*      */ 
/* 2975 */               BuddyPluginBuddy.buddyMessage bm = this.active_message;
/*      */               
/* 2977 */               this.active_message = null;
/*      */             }
/*      */             else
/*      */             {
/* 2981 */               bm = null;
/*      */             }
/*      */           }
/*      */           
/* 2985 */           Map reply = (Map)data_map.get("rep");
/*      */           
/* 2987 */           if (bm == null)
/*      */           {
/* 2989 */             BuddyPluginBuddy.this.logMessage("reply discarded as no matching request: " + reply);
/*      */ 
/*      */ 
/*      */           }
/* 2993 */           else if (type == 99)
/*      */           {
/* 2995 */             bm.setDontRetry();
/*      */             
/* 2997 */             bm.reportFailed(new BuddyPluginException(new String((byte[])reply.get("error"))));
/*      */           }
/*      */           else
/*      */           {
/* 3001 */             bm.reportComplete(reply);
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 3010 */         failed(e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected void close()
/*      */     {
/* 3017 */       this.closing = true;
/*      */       
/* 3019 */       failed(new BuddyPluginException("Closing"));
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isClosing()
/*      */     {
/* 3025 */       return this.closing;
/*      */     }
/*      */     
/*      */ 
/*      */     protected void remoteClosing()
/*      */     {
/* 3031 */       this.remote_closing = true;
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isRemoteClosing()
/*      */     {
/* 3037 */       return this.remote_closing;
/*      */     }
/*      */     
/*      */ 
/*      */     protected void disconnect()
/*      */     {
/* 3043 */       this.fragment_handler.close();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void failed(Throwable error)
/*      */     {
/* 3050 */       BuddyPluginBuddy.buddyMessage bm = null;
/*      */       
/* 3052 */       if ((!this.connected) && (this.outgoing))
/*      */       {
/* 3054 */         BuddyPluginBuddy.access$608(BuddyPluginBuddy.this);
/*      */       }
/*      */       
/* 3057 */       synchronized (this)
/*      */       {
/* 3059 */         if (this.failed)
/*      */         {
/* 3061 */           return;
/*      */         }
/*      */         
/* 3064 */         this.failed = true;
/*      */         
/* 3066 */         bm = this.active_message;
/*      */         
/* 3068 */         this.active_message = null;
/*      */       }
/*      */       
/* 3071 */       BuddyPluginBuddy.this.logMessage("Con " + getString() + " failed: " + Debug.getNestedExceptionMessage(error));
/*      */       try
/*      */       {
/* 3074 */         if (!this.closing) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3081 */         this.fragment_handler.close();
/*      */       }
/*      */       finally
/*      */       {
/* 3085 */         BuddyPluginBuddy.this.removeConnection(this);
/*      */         
/* 3087 */         if (bm != null)
/*      */         {
/* 3089 */           bm.reportFailed(error);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getString()
/*      */     {
/* 3097 */       return getString(false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected String getString(boolean short_form)
/*      */     {
/* 3104 */       if (short_form)
/*      */       {
/* 3106 */         return this.fragment_handler.getString();
/*      */       }
/*      */       
/*      */ 
/* 3110 */       return "id=" + this.connection_id + ",dir=" + (this.outgoing ? "out" : "in");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected class fragmentHandler
/*      */     implements GenericMessageConnectionListener
/*      */   {
/*      */     private GenericMessageConnection connection;
/*      */     
/*      */     private BuddyPluginBuddy.fragmentHandlerReceiver receiver;
/*      */     
/* 3122 */     private int next_fragment_id = 0;
/*      */     
/*      */     private fragmentAssembly current_request_frag;
/*      */     
/*      */     private fragmentAssembly current_reply_frag;
/*      */     
/*      */     private int send_count;
/*      */     
/*      */     private int recv_count;
/*      */     
/*      */ 
/*      */     protected fragmentHandler(GenericMessageConnection _connection, BuddyPluginBuddy.fragmentHandlerReceiver _receiver)
/*      */     {
/* 3135 */       this.connection = _connection;
/* 3136 */       this.receiver = _receiver;
/*      */     }
/*      */     
/*      */ 
/*      */     public void start()
/*      */     {
/* 3142 */       this.connection.addListener(this);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void connected(GenericMessageConnection connection)
/*      */     {
/* 3149 */       this.receiver.connected();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void failed(GenericMessageConnection connection, Throwable error)
/*      */       throws MessageException
/*      */     {
/* 3159 */       this.receiver.failed(error);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void send(Map data_map, boolean is_request, boolean record_active)
/*      */       throws BuddyPluginException
/*      */     {
/*      */       try
/*      */       {
/* 3171 */         byte[] data = BEncoder.encode(data_map);
/*      */         
/* 3173 */         int data_length = data.length;
/*      */         
/* 3175 */         BuddyPluginBuddy.this.plugin.checkMaxMessageSize(data_length);
/*      */         
/* 3177 */         int max_chunk = this.connection.getMaximumMessageSize() - 1024;
/*      */         
/* 3179 */         if (data_length > max_chunk)
/*      */         {
/*      */           int fragment_id;
/*      */           
/* 3183 */           synchronized (this)
/*      */           {
/* 3185 */             fragment_id = this.next_fragment_id++;
/*      */           }
/*      */           
/* 3188 */           int chunk_num = 0;
/*      */           
/* 3190 */           for (int i = 0; i < data_length; i += max_chunk)
/*      */           {
/* 3192 */             int end = Math.min(data_length, i + max_chunk);
/*      */             
/* 3194 */             if (end > i)
/*      */             {
/* 3196 */               byte[] chunk = new byte[end - i];
/*      */               
/* 3198 */               System.arraycopy(data, i, chunk, 0, chunk.length);
/*      */               
/* 3200 */               Map chunk_map = new HashMap();
/*      */               
/* 3202 */               chunk_map.put("type", new Long(5L));
/* 3203 */               chunk_map.put("f", new Long(fragment_id));
/* 3204 */               chunk_map.put("l", new Long(data_length));
/* 3205 */               chunk_map.put("c", new Long(max_chunk));
/* 3206 */               chunk_map.put("i", new Long(chunk_num));
/* 3207 */               chunk_map.put("q", new Long(is_request ? 1L : 0L));
/* 3208 */               chunk_map.put("d", chunk);
/*      */               
/* 3210 */               byte[] chunk_data = BEncoder.encode(chunk_map);
/*      */               
/* 3212 */               PooledByteBuffer chunk_buffer = BuddyPluginBuddy.this.plugin.getPluginInterface().getUtilities().allocatePooledByteBuffer(chunk_data);
/*      */               
/*      */               try
/*      */               {
/* 3216 */                 this.connection.send(chunk_buffer);
/*      */                 
/* 3218 */                 chunk_buffer = null;
/*      */               }
/*      */               finally
/*      */               {
/* 3222 */                 if (chunk_buffer != null)
/*      */                 {
/* 3224 */                   chunk_buffer.returnToPool();
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 3229 */             chunk_num++;
/*      */           }
/*      */         }
/*      */         else {
/* 3233 */           PooledByteBuffer buffer = BuddyPluginBuddy.this.plugin.getPluginInterface().getUtilities().allocatePooledByteBuffer(data);
/*      */           
/*      */ 
/*      */           try
/*      */           {
/* 3238 */             this.connection.send(buffer);
/*      */             
/* 3240 */             buffer = null;
/*      */           }
/*      */           finally
/*      */           {
/* 3244 */             if (buffer != null)
/*      */             {
/* 3246 */               buffer.returnToPool();
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 3251 */         BuddyPluginBuddy.this.buddyMessageSent(data.length, record_active);
/*      */         
/* 3253 */         this.send_count += 1;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 3257 */         throw new BuddyPluginException("Send failed", e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void receive(GenericMessageConnection connection, PooledByteBuffer message)
/*      */       throws MessageException
/*      */     {
/*      */       try
/*      */       {
/* 3272 */         if ((this.recv_count >= 4) && (!BuddyPluginBuddy.this.isAuthorised()))
/*      */         {
/* 3274 */           throw new MessageException("Too many messages received while in unauthorised state");
/*      */         }
/*      */         
/* 3277 */         byte[] content = message.toByteArray();
/*      */         
/* 3279 */         Map data_map = BDecoder.decode(content);
/*      */         
/* 3281 */         if (((Long)data_map.get("type")).intValue() == 5)
/*      */         {
/* 3283 */           Map chunk_map = data_map;
/*      */           
/* 3285 */           int fragment_id = ((Long)chunk_map.get("f")).intValue();
/* 3286 */           int data_length = ((Long)chunk_map.get("l")).intValue();
/* 3287 */           int chunk_size = ((Long)chunk_map.get("c")).intValue();
/* 3288 */           int chunk_num = ((Long)chunk_map.get("i")).intValue();
/*      */           
/* 3290 */           boolean is_request = ((Long)chunk_map.get("q")).intValue() == 1;
/*      */           
/* 3292 */           byte[] chunk_data = (byte[])chunk_map.get("d");
/*      */           
/* 3294 */           BuddyPluginBuddy.this.plugin.checkMaxMessageSize(data_length);
/*      */           
/*      */           fragmentAssembly assembly;
/*      */           fragmentAssembly assembly;
/* 3298 */           if (is_request)
/*      */           {
/* 3300 */             if (this.current_request_frag == null)
/*      */             {
/* 3302 */               this.current_request_frag = new fragmentAssembly(fragment_id, data_length, chunk_size);
/*      */             }
/*      */             
/* 3305 */             assembly = this.current_request_frag;
/*      */           }
/*      */           else
/*      */           {
/* 3309 */             if (this.current_reply_frag == null)
/*      */             {
/* 3311 */               this.current_reply_frag = new fragmentAssembly(fragment_id, data_length, chunk_size);
/*      */             }
/*      */             
/* 3314 */             assembly = this.current_reply_frag;
/*      */           }
/*      */           
/* 3317 */           if (assembly.getID() != fragment_id)
/*      */           {
/* 3319 */             throw new BuddyPluginException("Fragment receive error: concurrent decode not supported");
/*      */           }
/*      */           
/* 3322 */           if (assembly.receive(chunk_num, chunk_data))
/*      */           {
/* 3324 */             if (is_request)
/*      */             {
/* 3326 */               this.current_request_frag = null;
/*      */             }
/*      */             else
/*      */             {
/* 3330 */               this.current_reply_frag = null;
/*      */             }
/*      */             
/* 3333 */             BuddyPluginBuddy.this.buddyMessageReceived(data_length);
/*      */             
/* 3335 */             this.recv_count += 1;
/*      */             
/* 3337 */             this.receiver.receive(BDecoder.decode(assembly.getData()));
/*      */           }
/*      */           else
/*      */           {
/* 3341 */             BuddyPluginBuddy.this.buddyMessageFragmentReceived(assembly.getChunksReceived(), assembly.getTotalChunks());
/*      */           }
/*      */         }
/*      */         else {
/* 3345 */           BuddyPluginBuddy.this.buddyMessageReceived(content.length);
/*      */           
/* 3347 */           this.recv_count += 1;
/*      */           
/* 3349 */           this.receiver.receive(data_map);
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 3353 */         this.receiver.failed(e);
/*      */       }
/*      */       finally
/*      */       {
/* 3357 */         message.returnToPool();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected void close()
/*      */     {
/*      */       try
/*      */       {
/* 3366 */         this.connection.close();
/*      */ 
/*      */ 
/*      */       }
/*      */       catch (Throwable e) {}finally
/*      */       {
/*      */ 
/*      */ 
/* 3374 */         this.receiver.failed(new Exception("Connection closed"));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getString()
/*      */     {
/* 3381 */       return this.connection.getType();
/*      */     }
/*      */     
/*      */ 
/*      */     protected class fragmentAssembly
/*      */     {
/*      */       private int id;
/*      */       
/*      */       private byte[] data;
/*      */       private int chunk_size;
/*      */       private int num_chunks;
/* 3392 */       private Set chunks_received = new HashSet();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       protected fragmentAssembly(int _id, int _length, int _chunk_size)
/*      */       {
/* 3400 */         this.id = _id;
/* 3401 */         this.chunk_size = _chunk_size;
/*      */         
/* 3403 */         this.data = new byte[_length];
/*      */         
/* 3405 */         this.num_chunks = ((_length + this.chunk_size - 1) / this.chunk_size);
/*      */       }
/*      */       
/*      */ 
/*      */       protected int getID()
/*      */       {
/* 3411 */         return this.id;
/*      */       }
/*      */       
/*      */ 
/*      */       protected int getChunksReceived()
/*      */       {
/* 3417 */         return this.chunks_received.size();
/*      */       }
/*      */       
/*      */ 
/*      */       protected int getTotalChunks()
/*      */       {
/* 3423 */         return this.num_chunks;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       protected boolean receive(int chunk_num, byte[] chunk)
/*      */       {
/* 3433 */         Integer i = new Integer(chunk_num);
/*      */         
/* 3435 */         if (this.chunks_received.contains(i))
/*      */         {
/* 3437 */           return false;
/*      */         }
/*      */         
/* 3440 */         this.chunks_received.add(i);
/*      */         
/* 3442 */         System.arraycopy(chunk, 0, this.data, chunk_num * this.chunk_size, chunk.length);
/*      */         
/* 3444 */         return this.chunks_received.size() == this.num_chunks;
/*      */       }
/*      */       
/*      */ 
/*      */       protected byte[] getData()
/*      */       {
/* 3450 */         return this.data;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   static abstract interface fragmentHandlerReceiver
/*      */   {
/*      */     public abstract void connected();
/*      */     
/*      */     public abstract void receive(Map paramMap);
/*      */     
/*      */     public abstract void failed(Throwable paramThrowable);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */