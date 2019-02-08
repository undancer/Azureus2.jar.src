/*      */ package org.gudy.azureus2.core3.ipfilter.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.net.Inet6Address;
/*      */ import java.net.InetAddress;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.ipfilter.BannedIp;
/*      */ import org.gudy.azureus2.core3.ipfilter.BlockedIp;
/*      */ import org.gudy.azureus2.core3.ipfilter.IPFilterListener;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilter;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilterExternalHandler;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpRange;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.tracker.protocol.PRHelpers;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor2;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AddressUtils;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.ByteArrayHashMap;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.FrequencyLimitedDispatcher;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ 
/*      */ public class IpFilterImpl implements IpFilter
/*      */ {
/*   50 */   private static final LogIDs LOGID = LogIDs.CORE;
/*      */   
/*      */   private static final long BAN_IP_PERSIST_TIME = 604800000L;
/*      */   
/*      */   private static final int MAX_BLOCKS_TO_REMEMBER = 500;
/*      */   
/*      */   private static IpFilterImpl ipFilter;
/*   57 */   static final AEMonitor2 class_mon = new AEMonitor2("IpFilter:class");
/*      */   
/*   59 */   private final IPAddressRangeManager range_manager = new IPAddressRangeManager();
/*      */   
/*      */ 
/*      */   private final Map<Integer, BannedIpImpl> bannedIps;
/*      */   
/*      */ 
/*      */   private final LinkedList ipsBlocked;
/*      */   
/*   67 */   private int num_ips_blocked = 0;
/*   68 */   private int num_ips_blocked_loggable = 0;
/*      */   
/*      */ 
/*      */   private long last_update_time;
/*      */   
/*   73 */   final CopyOnWriteList<IPFilterListener> listenerz = new CopyOnWriteList(true);
/*      */   
/*   75 */   private final CopyOnWriteList<IpFilterExternalHandler> external_handlers = new CopyOnWriteList();
/*      */   
/*      */   final FrequencyLimitedDispatcher blockedListChangedDispatcher;
/*      */   
/*      */   private final IpFilterAutoLoaderImpl ipFilterAutoLoader;
/*      */   
/*      */   private boolean ip_filter_enabled;
/*      */   
/*      */   private boolean ip_filter_allow;
/*   84 */   private ByteArrayHashMap<String> excluded_hashes = new ByteArrayHashMap();
/*      */   private TimerEventPeriodic unban_timer;
/*      */   
/*      */   private IpFilterImpl() {
/*   88 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Ip Filter Allow", "Ip Filter Enabled" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*   98 */         IpFilterImpl.this.ip_filter_enabled = COConfigurationManager.getBooleanParameter("Ip Filter Enabled");
/*   99 */         IpFilterImpl.this.ip_filter_allow = COConfigurationManager.getBooleanParameter("Ip Filter Allow");
/*      */         
/*  101 */         if (parameterName != null)
/*      */         {
/*  103 */           if (parameterName.equals("Ip Filter Enabled"))
/*      */           {
/*  105 */             for (IPFilterListener listener : IpFilterImpl.this.listenerz)
/*      */             {
/*  107 */               listener.IPFilterEnabledChanged(IpFilterImpl.this.ip_filter_enabled);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1132 */     });
/* 1133 */     this.unban_map = new TreeMap();
/* 1134 */     this.unban_map_reverse = new HashMap();ipFilter = this;this.bannedIps = new HashMap();this.ipsBlocked = new LinkedList();this.blockedListChangedDispatcher = new FrequencyLimitedDispatcher(new AERunnable()
/*      */     {
/*      */       public void runSupport()
/*      */       {
/*  126 */         for (IPFilterListener listener : IpFilterImpl.this.listenerz)
/*      */           try {
/*  128 */             listener.IPBlockedListChanged(IpFilterImpl.this);
/*      */           } catch (Exception e) {
/*  130 */             Debug.out(e); } } }, 10000);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  136 */     this.ipFilterAutoLoader = new IpFilterAutoLoaderImpl(this);
/*      */     try
/*      */     {
/*  139 */       loadBannedIPs();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  143 */       Debug.printStackTrace(e);
/*      */     }
/*      */     try
/*      */     {
/*  147 */       loadFilters(true, true);
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  151 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/*  154 */     COConfigurationManager.addParameterListener(new String[] { "Ip Filter Allow", "Ip Filter Enabled" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*  159 */         IpFilterImpl.this.markAsUpToDate();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static IpFilter getInstance() {
/*      */     try {
/*  166 */       class_mon.enter();
/*      */       
/*  168 */       if (ipFilter == null) {
/*  169 */         ipFilter = new IpFilterImpl();
/*      */       }
/*  171 */       return ipFilter;
/*      */     }
/*      */     finally {
/*  174 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public File getFile()
/*      */   {
/*  181 */     return FileUtil.getUserFile("filters.config");
/*      */   }
/*      */   
/*      */ 
/*      */   public void reload()
/*      */     throws Exception
/*      */   {
/*  188 */     reload(true);
/*      */   }
/*      */   
/*      */ 
/*      */   public void reloadSync()
/*      */     throws Exception
/*      */   {
/*  195 */     reload(false);
/*      */   }
/*      */   
/*      */ 
/*      */   public void reload(boolean allowAsyncDownloading)
/*      */     throws Exception
/*      */   {
/*  202 */     if (COConfigurationManager.getBooleanParameter("Ip Filter Clear On Reload")) {
/*  203 */       this.range_manager.clearAllEntries();
/*      */     }
/*  205 */     markAsUpToDate();
/*  206 */     loadFilters(allowAsyncDownloading, false);
/*      */   }
/*      */   
/*      */ 
/*      */   public void save()
/*      */     throws Exception
/*      */   {
/*      */     try
/*      */     {
/*  215 */       class_mon.enter();
/*      */       
/*  217 */       Map map = new HashMap();
/*      */       
/*      */ 
/*  220 */       List filters = new ArrayList();
/*  221 */       map.put("ranges", filters);
/*  222 */       List entries = this.range_manager.getEntries();
/*  223 */       Iterator iter = entries.iterator();
/*  224 */       while (iter.hasNext()) {
/*  225 */         IpRange range = (IpRange)iter.next();
/*  226 */         if ((range.isValid()) && (!range.isSessionOnly())) {
/*  227 */           String description = range.getDescription();
/*  228 */           String startIp = range.getStartIp();
/*  229 */           String endIp = range.getEndIp();
/*  230 */           Map mapRange = new HashMap();
/*  231 */           mapRange.put("description", description.getBytes("UTF-8"));
/*  232 */           mapRange.put("start", startIp);
/*  233 */           mapRange.put("end", endIp);
/*  234 */           filters.add(mapRange);
/*      */         }
/*      */       }
/*      */       
/*  238 */       FileOutputStream fos = null;
/*      */       
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/*  244 */         File filtersFile = FileUtil.getUserFile("filters.config");
/*      */         
/*  246 */         fos = new FileOutputStream(filtersFile);
/*      */         
/*  248 */         fos.write(BEncoder.encode(map));
/*      */       }
/*      */       finally
/*      */       {
/*  252 */         if (fos != null)
/*      */         {
/*  254 */           fos.close();
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/*  259 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void loadFilters(boolean allowAsyncDownloading, boolean loadOldWhileAsyncDownloading)
/*      */     throws Exception
/*      */   {
/*  267 */     long startTime = System.currentTimeMillis();
/*  268 */     this.ipFilterAutoLoader.loadOtherFilters(allowAsyncDownloading, loadOldWhileAsyncDownloading);
/*      */     
/*  270 */     if (getNbRanges() > 0) {
/*  271 */       Logger.log(new LogEvent(LOGID, System.currentTimeMillis() - startTime + "ms for " + getNbRanges() + ". now loading norm"));
/*      */     }
/*      */     
/*      */     try
/*      */     {
/*  276 */       class_mon.enter();
/*      */       
/*  278 */       List new_ipRanges = new ArrayList(1024);
/*      */       
/*  280 */       FileInputStream fin = null;
/*  281 */       BufferedInputStream bin = null;
/*      */       try
/*      */       {
/*  284 */         File filtersFile = FileUtil.getUserFile("filters.config");
/*  285 */         if (filtersFile.exists()) {
/*  286 */           fin = new FileInputStream(filtersFile);
/*  287 */           bin = new BufferedInputStream(fin, 16384);
/*  288 */           Map map = org.gudy.azureus2.core3.util.BDecoder.decode(bin);
/*  289 */           List list = (List)map.get("ranges");
/*  290 */           Iterator iter = list.listIterator();
/*  291 */           while (iter.hasNext()) {
/*  292 */             Map range = (Map)iter.next();
/*  293 */             String description = new String((byte[])range.get("description"), "UTF-8");
/*  294 */             String startIp = new String((byte[])range.get("start"));
/*  295 */             String endIp = new String((byte[])range.get("end"));
/*      */             
/*  297 */             IpRangeImpl ipRange = new IpRangeImpl(description, startIp, endIp, false);
/*      */             
/*  299 */             ipRange.setAddedToRangeList(true);
/*      */             
/*  301 */             new_ipRanges.add(ipRange);
/*      */           }
/*      */         }
/*      */       } finally {
/*      */         Iterator it;
/*  306 */         if (bin != null) {
/*      */           try {
/*  308 */             bin.close();
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*  312 */         if (fin != null) {
/*      */           try {
/*  314 */             fin.close();
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */         
/*      */ 
/*  320 */         Iterator it = new_ipRanges.iterator();
/*      */         
/*  322 */         while (it.hasNext())
/*      */         {
/*  324 */           ((IpRange)it.next()).checkValid();
/*      */         }
/*      */         
/*  327 */         markAsUpToDate();
/*      */       }
/*      */     }
/*      */     finally {
/*  331 */       class_mon.exit();
/*      */     }
/*  333 */     Logger.log(new LogEvent(LOGID, System.currentTimeMillis() - startTime + "ms to load all IP Filters"));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void loadBannedIPs()
/*      */   {
/*  340 */     if (!COConfigurationManager.getBooleanParameter("Ip Filter Banning Persistent"))
/*      */     {
/*  342 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  346 */       class_mon.enter();
/*      */       
/*  348 */       Map map = FileUtil.readResilientConfigFile("banips.config");
/*      */       
/*  350 */       List ips = (List)map.get("ips");
/*      */       
/*  352 */       if (ips != null)
/*      */       {
/*  354 */         long now = SystemTime.getCurrentTime();
/*      */         
/*  356 */         for (int i = 0; i < ips.size(); i++)
/*      */         {
/*  358 */           Map entry = (Map)ips.get(i);
/*      */           
/*  360 */           String ip = new String((byte[])entry.get("ip"));
/*  361 */           String desc = new String((byte[])entry.get("desc"), "UTF-8");
/*  362 */           Long ltime = (Long)entry.get("time");
/*      */           
/*  364 */           long time = ltime.longValue();
/*      */           
/*  366 */           boolean drop = false;
/*      */           
/*  368 */           if (time > now)
/*      */           {
/*  370 */             time = now;
/*      */           }
/*  372 */           else if (now - time >= 604800000L)
/*      */           {
/*  374 */             drop = true;
/*      */             
/*  376 */             if (Logger.isEnabled())
/*      */             {
/*  378 */               Logger.log(new LogEvent(LOGID, 0, "Persistent ban dropped as too old : " + ip + ", " + desc));
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  386 */           if (!drop)
/*      */           {
/*  388 */             int int_ip = this.range_manager.addressToInt(ip);
/*      */             
/*  390 */             this.bannedIps.put(new Integer(int_ip), new BannedIpImpl(ip, desc, time));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  396 */       Debug.printStackTrace(e);
/*      */     }
/*      */     finally
/*      */     {
/*  400 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void saveBannedIPs()
/*      */   {
/*  407 */     if (!COConfigurationManager.getBooleanParameter("Ip Filter Banning Persistent"))
/*      */     {
/*  409 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  413 */       class_mon.enter();
/*      */       
/*  415 */       Map map = new HashMap();
/*      */       
/*  417 */       List ips = new ArrayList();
/*      */       
/*  419 */       Iterator it = this.bannedIps.values().iterator();
/*      */       
/*  421 */       while (it.hasNext())
/*      */       {
/*  423 */         BannedIpImpl bip = (BannedIpImpl)it.next();
/*      */         
/*  425 */         if (!bip.isTemporary())
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  430 */           Map entry = new HashMap();
/*      */           
/*  432 */           entry.put("ip", bip.getIp());
/*  433 */           entry.put("desc", bip.getTorrentName().getBytes("UTF-8"));
/*  434 */           entry.put("time", new Long(bip.getBanningTime()));
/*      */           
/*  436 */           ips.add(entry);
/*      */         }
/*      */       }
/*  439 */       map.put("ips", ips);
/*      */       
/*  441 */       FileUtil.writeResilientConfigFile("banips.config", map);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  445 */       Debug.printStackTrace(e);
/*      */     }
/*      */     finally
/*      */     {
/*  449 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isInRange(String ipAddress)
/*      */   {
/*  457 */     return isInRange(ipAddress, "", null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isInRange(String ipAddress, String torrent_name, byte[] torrent_hash)
/*      */   {
/*  467 */     return isInRange(ipAddress, torrent_name, torrent_hash, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isInRange(String ipAddress, String torrent_name, byte[] torrent_hash, boolean loggable)
/*      */   {
/*  479 */     if (isBanned(ipAddress))
/*      */     {
/*  481 */       return true;
/*      */     }
/*      */     
/*      */ 
/*  485 */     if (!isEnabled())
/*      */     {
/*  487 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  492 */     if (ipAddress.equals("127.0.0.1"))
/*      */     {
/*  494 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  499 */     if (ipAddress.contains(":"))
/*      */     {
/*  501 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  506 */     if (AddressUtils.isLANLocalAddress(ipAddress) == 1)
/*      */     {
/*  508 */       return false;
/*      */     }
/*      */     
/*  511 */     if (torrent_hash != null)
/*      */     {
/*  513 */       if (this.excluded_hashes.containsKey(torrent_hash))
/*      */       {
/*  515 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  519 */     boolean allow = this.ip_filter_allow;
/*      */     
/*  521 */     IpRange match = (IpRange)this.range_manager.isInRange(ipAddress);
/*      */     
/*  523 */     if ((match == null) || (allow))
/*      */     {
/*  525 */       IpRange explict_deny = checkExternalHandlers(torrent_hash, ipAddress);
/*      */       
/*  527 */       if (explict_deny != null)
/*      */       {
/*  529 */         match = explict_deny;
/*      */         
/*  531 */         allow = false;
/*      */       }
/*      */     }
/*      */     
/*  535 */     if (match != null) {
/*  536 */       if (!allow)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  541 */         if (AENetworkClassifier.categoriseAddress(ipAddress) != "Public")
/*      */         {
/*  543 */           return false;
/*      */         }
/*      */         
/*  546 */         if (addBlockedIP(new BlockedIpImpl(ipAddress, match, torrent_name, loggable), torrent_hash, loggable))
/*      */         {
/*  548 */           if (Logger.isEnabled()) {
/*  549 */             Logger.log(new LogEvent(LOGID, 1, "Ip Blocked : " + ipAddress + ", in range : " + match));
/*      */           }
/*      */           
/*  552 */           return true;
/*      */         }
/*      */         
/*      */ 
/*  556 */         if (Logger.isEnabled()) {
/*  557 */           Logger.log(new LogEvent(LOGID, 1, "Ip Blocking Denied : " + ipAddress + ", in range : " + match));
/*      */         }
/*      */         
/*  560 */         return false;
/*      */       }
/*      */       
/*      */ 
/*  564 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  568 */     if (allow)
/*      */     {
/*  570 */       if (AENetworkClassifier.categoriseAddress(ipAddress) != "Public")
/*      */       {
/*  572 */         return false;
/*      */       }
/*      */       
/*  575 */       if (addBlockedIP(new BlockedIpImpl(ipAddress, null, torrent_name, loggable), torrent_hash, loggable))
/*      */       {
/*  577 */         if (Logger.isEnabled()) {
/*  578 */           Logger.log(new LogEvent(LOGID, 1, "Ip Blocked : " + ipAddress + ", not in any range"));
/*      */         }
/*      */         
/*  581 */         return true;
/*      */       }
/*      */       
/*      */ 
/*  585 */       if (Logger.isEnabled()) {
/*  586 */         Logger.log(new LogEvent(LOGID, 1, "Ip Blocking Denied : " + ipAddress + ", not in any range"));
/*      */       }
/*      */       
/*  589 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  593 */     return false;
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
/*      */   public boolean isInRange(InetAddress ipAddress, String torrent_name, byte[] torrent_hash, boolean loggable)
/*      */   {
/*  606 */     if (isBanned(ipAddress))
/*      */     {
/*  608 */       return true;
/*      */     }
/*      */     
/*  611 */     if (!isEnabled())
/*      */     {
/*  613 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  618 */     if ((ipAddress.isLoopbackAddress()) || (ipAddress.isLinkLocalAddress()) || (ipAddress.isSiteLocalAddress()))
/*      */     {
/*  620 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  625 */     if ((ipAddress instanceof Inet6Address))
/*      */     {
/*  627 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  632 */     if (AddressUtils.isLANLocalAddress(ipAddress) == 1)
/*      */     {
/*  634 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  638 */     if (torrent_hash != null)
/*      */     {
/*  640 */       if (this.excluded_hashes.containsKey(torrent_hash))
/*      */       {
/*  642 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  646 */     boolean allow = this.ip_filter_allow;
/*      */     
/*  648 */     IpRange match = (IpRange)this.range_manager.isInRange(ipAddress);
/*      */     
/*  650 */     if ((match == null) || (allow))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  656 */       IpRange explicit_deny = checkExternalHandlers(torrent_hash, ipAddress);
/*      */       
/*  658 */       if (explicit_deny != null)
/*      */       {
/*      */ 
/*      */ 
/*  662 */         match = explicit_deny;
/*      */         
/*  664 */         allow = false;
/*      */       }
/*      */     }
/*      */     
/*  668 */     if (match != null)
/*      */     {
/*  670 */       if (!allow)
/*      */       {
/*  672 */         if (addBlockedIP(new BlockedIpImpl(ipAddress.getHostAddress(), match, torrent_name, loggable), torrent_hash, loggable))
/*      */         {
/*  674 */           if (Logger.isEnabled()) {
/*  675 */             Logger.log(new LogEvent(LOGID, 1, "Ip Blocked : " + ipAddress + ", in range : " + match));
/*      */           }
/*      */           
/*  678 */           return true;
/*      */         }
/*      */         
/*      */ 
/*  682 */         if (Logger.isEnabled()) {
/*  683 */           Logger.log(new LogEvent(LOGID, 1, "Ip Blocking Denied: " + ipAddress + ", in range : " + match));
/*      */         }
/*      */         
/*  686 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  691 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  695 */     if (allow)
/*      */     {
/*  697 */       if (addBlockedIP(new BlockedIpImpl(ipAddress.getHostAddress(), null, torrent_name, loggable), torrent_hash, loggable))
/*      */       {
/*  699 */         if (Logger.isEnabled()) {
/*  700 */           Logger.log(new LogEvent(LOGID, 1, "Ip Blocked : " + ipAddress + ", not in any range"));
/*      */         }
/*      */         
/*  703 */         return true;
/*      */       }
/*      */       
/*  706 */       if (Logger.isEnabled()) {
/*  707 */         Logger.log(new LogEvent(LOGID, 1, "Ip Blocking Denied : " + ipAddress + ", not in any range"));
/*      */       }
/*      */       
/*  710 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  714 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected IpRange checkExternalHandlers(byte[] torrent_hash, String address)
/*      */   {
/*  722 */     if (this.external_handlers.size() > 0)
/*      */     {
/*  724 */       Iterator it = this.external_handlers.iterator();
/*      */       
/*  726 */       while (it.hasNext())
/*      */       {
/*  728 */         if (((IpFilterExternalHandler)it.next()).isBlocked(torrent_hash, address))
/*      */         {
/*  730 */           return new IpRangeImpl("External handler", address, address, true);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  735 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected IpRange checkExternalHandlers(byte[] torrent_hash, InetAddress address)
/*      */   {
/*  743 */     if (this.external_handlers.size() > 0)
/*      */     {
/*  745 */       Iterator it = this.external_handlers.iterator();
/*      */       
/*  747 */       while (it.hasNext())
/*      */       {
/*  749 */         if (((IpFilterExternalHandler)it.next()).isBlocked(torrent_hash, address))
/*      */         {
/*  751 */           String ip = address.getHostAddress();
/*      */           
/*  753 */           return new IpRangeImpl("External handler", ip, ip, true);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  758 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean addBlockedIP(BlockedIp ip, byte[] torrent_hash, boolean loggable)
/*      */   {
/*  767 */     if (torrent_hash != null)
/*      */     {
/*  769 */       for (IPFilterListener listener : this.listenerz) {
/*      */         try
/*      */         {
/*  772 */           if (!listener.canIPBeBlocked(ip.getBlockedIp(), torrent_hash))
/*      */           {
/*  774 */             return false;
/*      */           }
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  779 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  785 */       class_mon.enter();
/*      */       
/*  787 */       this.ipsBlocked.addLast(ip);
/*      */       
/*  789 */       this.num_ips_blocked += 1;
/*      */       
/*  791 */       if (loggable)
/*      */       {
/*  793 */         this.num_ips_blocked_loggable += 1;
/*      */       }
/*      */       
/*  796 */       if (this.ipsBlocked.size() > 500)
/*      */       {
/*  798 */         this.ipsBlocked.removeFirst();
/*      */       }
/*      */     }
/*      */     finally {
/*  802 */       class_mon.exit();
/*      */     }
/*      */     
/*  805 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean isBanned(InetAddress ipAddress)
/*      */   {
/*      */     try
/*      */     {
/*  815 */       class_mon.enter();
/*      */       
/*  817 */       int address = this.range_manager.addressToInt(ipAddress);
/*      */       
/*  819 */       Integer i_address = new Integer(address);
/*      */       
/*  821 */       return this.bannedIps.get(i_address) != null;
/*      */     }
/*      */     finally
/*      */     {
/*  825 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean isBanned(String ipAddress)
/*      */   {
/*      */     try
/*      */     {
/*  834 */       class_mon.enter();
/*      */       
/*  836 */       int address = this.range_manager.addressToInt(ipAddress);
/*      */       
/*  838 */       Integer i_address = new Integer(address);
/*      */       
/*  840 */       return this.bannedIps.get(i_address) != null;
/*      */     }
/*      */     finally
/*      */     {
/*  844 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getInRangeAddressesAreAllowed()
/*      */   {
/*  851 */     return this.ip_filter_allow;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setInRangeAddressesAreAllowed(boolean b)
/*      */   {
/*  858 */     COConfigurationManager.setParameter("Ip Filter Allow", b);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public List getIpRanges()
/*      */   {
/*      */     try
/*      */     {
/*  870 */       class_mon.enter();
/*      */       
/*  872 */       return new ArrayList(this.range_manager.getEntries());
/*      */     }
/*      */     finally
/*      */     {
/*  876 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public IpRange[] getRanges()
/*      */   {
/*      */     try
/*      */     {
/*  884 */       class_mon.enter();
/*      */       
/*  886 */       List entries = this.range_manager.getEntries();
/*  887 */       IpRange[] res = new IpRange[entries.size()];
/*      */       
/*  889 */       entries.toArray(res);
/*      */       
/*  891 */       return res;
/*      */     }
/*      */     finally
/*      */     {
/*  895 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public IpRange createRange(boolean sessionOnly)
/*      */   {
/*  902 */     return new IpRangeImpl("", "", "", sessionOnly);
/*      */   }
/*      */   
/*      */ 
/*      */   public void addRange(IpRange range)
/*      */   {
/*      */     try
/*      */     {
/*  910 */       class_mon.enter();
/*      */       
/*  912 */       ((IpRangeImpl)range).setAddedToRangeList(true);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  918 */       range.checkValid();
/*      */     }
/*      */     finally
/*      */     {
/*  922 */       class_mon.exit();
/*      */     }
/*      */     
/*  925 */     markAsUpToDate();
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeRange(IpRange range)
/*      */   {
/*      */     try
/*      */     {
/*  933 */       class_mon.enter();
/*      */       
/*  935 */       ((IpRangeImpl)range).setAddedToRangeList(false);
/*      */       
/*  937 */       this.range_manager.removeRange(range);
/*      */     }
/*      */     finally
/*      */     {
/*  941 */       class_mon.exit();
/*      */     }
/*      */     
/*  944 */     markAsUpToDate();
/*      */   }
/*      */   
/*      */   public int getNbRanges() {
/*  948 */     List entries = this.range_manager.getEntries();
/*      */     
/*  950 */     return entries.size();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setValidOrNot(IpRange range, boolean valid)
/*      */   {
/*      */     try
/*      */     {
/*  959 */       class_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  964 */       if (!range.getAddedToRangeList()) {
/*      */         return;
/*      */       }
/*      */       
/*      */     }
/*      */     finally
/*      */     {
/*  971 */       class_mon.exit();
/*      */     }
/*      */     
/*  974 */     if (valid)
/*      */     {
/*  976 */       this.range_manager.addRange(range);
/*      */     }
/*      */     else
/*      */     {
/*  980 */       this.range_manager.removeRange(range);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNbIpsBlocked()
/*      */   {
/*  987 */     return this.num_ips_blocked;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNbIpsBlockedAndLoggable()
/*      */   {
/*  993 */     return this.num_ips_blocked_loggable;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean ban(String ipAddress, String torrent_name, boolean manual)
/*      */   {
/* 1002 */     return ban(ipAddress, torrent_name, manual, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean ban(String ipAddress, String torrent_name, boolean manual, int for_mins)
/*      */   {
/* 1014 */     if (!manual)
/*      */     {
/* 1016 */       for (IPFilterListener listener : this.listenerz) {
/*      */         try
/*      */         {
/* 1019 */           if (!listener.canIPBeBanned(ipAddress))
/*      */           {
/* 1021 */             return false;
/*      */           }
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1026 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1031 */     boolean block_ban = false;
/*      */     
/* 1033 */     List new_bans = new ArrayList();
/*      */     
/* 1035 */     boolean temporary = for_mins > 0;
/*      */     try
/*      */     {
/* 1038 */       class_mon.enter();
/*      */       
/* 1040 */       int address = this.range_manager.addressToInt(ipAddress);
/*      */       
/* 1042 */       Integer i_address = new Integer(address);
/*      */       
/* 1044 */       if (this.bannedIps.get(i_address) == null)
/*      */       {
/* 1046 */         BannedIpImpl new_ban = new BannedIpImpl(ipAddress, torrent_name, temporary);
/*      */         
/* 1048 */         new_bans.add(new_ban);
/*      */         
/* 1050 */         this.bannedIps.put(i_address, new_ban);
/*      */         
/* 1052 */         if (temporary)
/*      */         {
/* 1054 */           addTemporaryBan(new_ban, for_mins);
/*      */         }
/*      */         
/*      */ 
/* 1058 */         if (!org.gudy.azureus2.core3.util.UnresolvableHostManager.isPseudoAddress(ipAddress))
/*      */         {
/* 1060 */           long l_address = address;
/*      */           
/* 1062 */           if (l_address < 0L)
/*      */           {
/* 1064 */             l_address += 4294967296L;
/*      */           }
/*      */           
/* 1067 */           long start = l_address & 0xFFFFFFFFFFFFFF00;
/* 1068 */           long end = start + 256L;
/*      */           
/* 1070 */           int hits = 0;
/*      */           
/* 1072 */           for (long i = start; i < end; i += 1L)
/*      */           {
/* 1074 */             Integer a = new Integer((int)i);
/*      */             
/* 1076 */             if (this.bannedIps.get(a) != null)
/*      */             {
/* 1078 */               hits++;
/*      */             }
/*      */           }
/*      */           
/* 1082 */           int hit_limit = COConfigurationManager.getIntParameter("Ip Filter Ban Block Limit");
/*      */           
/* 1084 */           if (hits >= hit_limit)
/*      */           {
/* 1086 */             block_ban = true;
/*      */             
/* 1088 */             for (long i = start; i < end; i += 1L)
/*      */             {
/* 1090 */               Integer a = new Integer((int)i);
/*      */               
/* 1092 */               if (this.bannedIps.get(a) == null)
/*      */               {
/* 1094 */                 BannedIpImpl new_block_ban = new BannedIpImpl(PRHelpers.intToAddress((int)i), torrent_name + " [block ban]", temporary);
/*      */                 
/* 1096 */                 new_bans.add(new_block_ban);
/*      */                 
/* 1098 */                 this.bannedIps.put(a, new_block_ban);
/*      */                 
/* 1100 */                 addTemporaryBan(new_block_ban, for_mins);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1106 */         saveBannedIPs();
/*      */       }
/*      */     }
/*      */     finally {
/* 1110 */       class_mon.exit();
/*      */     }
/*      */     BannedIp entry;
/* 1113 */     for (int i = 0; i < new_bans.size(); i++)
/*      */     {
/* 1115 */       entry = (BannedIp)new_bans.get(i);
/*      */       
/* 1117 */       for (IPFilterListener listener : this.listenerz) {
/*      */         try
/*      */         {
/* 1120 */           listener.IPBanned(entry);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1124 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1129 */     return block_ban;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   final Map<Long, List<String>> unban_map;
/*      */   
/*      */ 
/*      */ 
/*      */   final Map<String, Long> unban_map_reverse;
/*      */   
/*      */ 
/*      */   private void addTemporaryBan(BannedIpImpl ban, int mins)
/*      */   {
/* 1144 */     if (this.unban_timer == null)
/*      */     {
/* 1146 */       this.unban_timer = SimpleTimer.addPeriodicEvent("Unbanner", 30000L, new TimerEventPerformer()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void perform(TimerEvent event)
/*      */         {
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/* 1157 */             IpFilterImpl.class_mon.enter();
/*      */             
/* 1159 */             long now = SystemTime.getMonotonousTime();
/*      */             
/* 1161 */             Iterator<Map.Entry<Long, List<String>>> it = IpFilterImpl.this.unban_map.entrySet().iterator();
/*      */             
/* 1163 */             while (it.hasNext())
/*      */             {
/* 1165 */               Map.Entry<Long, List<String>> entry = (Map.Entry)it.next();
/*      */               
/* 1167 */               if (((Long)entry.getKey()).longValue() > now)
/*      */                 break;
/* 1169 */               it.remove();
/*      */               
/* 1171 */               for (String ip : (List)entry.getValue())
/*      */               {
/* 1173 */                 IpFilterImpl.this.unban_map_reverse.remove(ip);
/*      */                 
/* 1175 */                 IpFilterImpl.this.unban(ip);
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1183 */             if (IpFilterImpl.this.unban_map.size() == 0)
/*      */             {
/* 1185 */               IpFilterImpl.this.unban_timer.cancel();
/*      */               
/* 1187 */               IpFilterImpl.this.unban_timer = null;
/*      */             }
/*      */           }
/*      */           finally {
/* 1191 */             IpFilterImpl.class_mon.exit();
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 1197 */     String ip = ban.getIp();
/*      */     
/* 1199 */     long expiry = SystemTime.getMonotonousTime() + mins * 60 * 1000L;
/*      */     
/* 1201 */     expiry = (expiry + 29999L) / 30000L * 30000L;
/*      */     
/* 1203 */     Long old_expiry = (Long)this.unban_map_reverse.get(ip);
/*      */     
/* 1205 */     if (old_expiry != null)
/*      */     {
/* 1207 */       List<String> list = (List)this.unban_map.get(old_expiry);
/*      */       
/* 1209 */       if (list != null)
/*      */       {
/* 1211 */         list.remove(ip);
/*      */         
/* 1213 */         if (list.size() == 0)
/*      */         {
/* 1215 */           this.unban_map.remove(old_expiry);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1220 */     this.unban_map_reverse.put(ip, Long.valueOf(expiry));
/*      */     
/* 1222 */     List<String> list = (List)this.unban_map.get(Long.valueOf(expiry));
/*      */     
/* 1224 */     if (list == null)
/*      */     {
/* 1226 */       list = new ArrayList(1);
/*      */       
/* 1228 */       this.unban_map.put(Long.valueOf(expiry), list);
/*      */     }
/*      */     
/* 1231 */     list.add(ip);
/*      */   }
/*      */   
/*      */   public BannedIp[] getBannedIps()
/*      */   {
/*      */     try
/*      */     {
/* 1238 */       class_mon.enter();
/*      */       
/* 1240 */       BannedIp[] res = new BannedIp[this.bannedIps.size()];
/*      */       
/* 1242 */       this.bannedIps.values().toArray(res);
/*      */       
/* 1244 */       return res;
/*      */     }
/*      */     finally
/*      */     {
/* 1248 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNbBannedIps()
/*      */   {
/* 1255 */     return this.bannedIps.size();
/*      */   }
/*      */   
/*      */   public void clearBannedIps()
/*      */   {
/*      */     try
/*      */     {
/* 1262 */       class_mon.enter();
/*      */       
/* 1264 */       this.bannedIps.clear();
/*      */       
/* 1266 */       this.unban_map.clear();
/*      */       
/* 1268 */       this.unban_map_reverse.clear();
/*      */       
/* 1270 */       saveBannedIPs();
/*      */     }
/*      */     finally
/*      */     {
/* 1274 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void unban(String ipAddress)
/*      */   {
/*      */     try
/*      */     {
/* 1282 */       class_mon.enter();
/*      */       
/* 1284 */       int address = this.range_manager.addressToInt(ipAddress);
/*      */       
/* 1286 */       Integer i_address = new Integer(address);
/*      */       
/* 1288 */       BannedIpImpl entry = (BannedIpImpl)this.bannedIps.remove(i_address);
/*      */       
/* 1290 */       if (entry != null)
/*      */       {
/* 1292 */         if (!entry.isTemporary())
/*      */         {
/* 1294 */           saveBannedIPs();
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1300 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void unban(String ipAddress, boolean block)
/*      */   {
/* 1307 */     if (block)
/*      */     {
/* 1309 */       int address = this.range_manager.addressToInt(ipAddress);
/*      */       
/* 1311 */       long l_address = address;
/*      */       
/* 1313 */       if (l_address < 0L)
/*      */       {
/* 1315 */         l_address += 4294967296L;
/*      */       }
/*      */       
/* 1318 */       long start = l_address & 0xFFFFFFFFFFFFFF00;
/* 1319 */       long end = start + 256L;
/*      */       
/* 1321 */       boolean hit = false;
/*      */       try
/*      */       {
/* 1324 */         class_mon.enter();
/*      */         
/* 1326 */         for (long i = start; i < end; i += 1L)
/*      */         {
/* 1328 */           Integer a = new Integer((int)i);
/*      */           
/* 1330 */           if (this.bannedIps.remove(a) != null)
/*      */           {
/* 1332 */             hit = true;
/*      */           }
/*      */         }
/*      */         
/* 1336 */         if (hit)
/*      */         {
/* 1338 */           saveBannedIPs();
/*      */         }
/*      */       }
/*      */       finally {
/* 1342 */         class_mon.exit();
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*      */       try {
/* 1348 */         class_mon.enter();
/*      */         
/* 1350 */         int address = this.range_manager.addressToInt(ipAddress);
/*      */         
/* 1352 */         Integer i_address = new Integer(address);
/*      */         
/* 1354 */         if (this.bannedIps.remove(i_address) != null)
/*      */         {
/* 1356 */           saveBannedIPs();
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 1361 */         class_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public BlockedIp[] getBlockedIps()
/*      */   {
/*      */     try
/*      */     {
/* 1371 */       class_mon.enter();
/*      */       
/* 1373 */       BlockedIp[] res = new BlockedIp[this.ipsBlocked.size()];
/*      */       
/* 1375 */       this.ipsBlocked.toArray(res);
/*      */       
/* 1377 */       return res;
/*      */     }
/*      */     finally {
/* 1380 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void clearBlockedIPs()
/*      */   {
/*      */     try
/*      */     {
/* 1388 */       class_mon.enter();
/*      */       
/* 1390 */       this.ipsBlocked.clear();
/*      */       
/* 1392 */       this.num_ips_blocked = 0;
/* 1393 */       this.num_ips_blocked_loggable = 0;
/*      */     }
/*      */     finally
/*      */     {
/* 1397 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addExcludedHash(byte[] hash)
/*      */   {
/* 1405 */     synchronized (this)
/*      */     {
/* 1407 */       if (this.excluded_hashes.containsKey(hash))
/*      */       {
/* 1409 */         return;
/*      */       }
/*      */       
/* 1412 */       ByteArrayHashMap<String> copy = new ByteArrayHashMap();
/*      */       
/* 1414 */       for (byte[] k : this.excluded_hashes.keys())
/*      */       {
/* 1416 */         copy.put(k, "");
/*      */       }
/*      */       
/* 1419 */       copy.put(hash, "");
/*      */       
/* 1421 */       this.excluded_hashes = copy;
/*      */     }
/*      */     
/* 1424 */     markAsUpToDate();
/*      */     
/* 1426 */     Logger.log(new LogEvent(LOGID, "Added " + ByteFormatter.encodeString(hash) + " to excluded set"));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeExcludedHash(byte[] hash)
/*      */   {
/* 1434 */     synchronized (this)
/*      */     {
/* 1436 */       if (!this.excluded_hashes.containsKey(hash))
/*      */       {
/* 1438 */         return;
/*      */       }
/*      */       
/* 1441 */       ByteArrayHashMap<String> copy = new ByteArrayHashMap();
/*      */       
/* 1443 */       for (byte[] k : this.excluded_hashes.keys())
/*      */       {
/* 1445 */         copy.put(k, "");
/*      */       }
/*      */       
/* 1448 */       copy.remove(hash);
/*      */       
/* 1450 */       this.excluded_hashes = copy;
/*      */     }
/*      */     
/* 1453 */     markAsUpToDate();
/*      */     
/* 1455 */     Logger.log(new LogEvent(LOGID, "Removed " + ByteFormatter.encodeString(hash) + " from excluded set"));
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isEnabled()
/*      */   {
/* 1461 */     return this.ip_filter_enabled;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setEnabled(boolean enabled)
/*      */   {
/* 1468 */     COConfigurationManager.setParameter("Ip Filter Enabled", enabled);
/*      */   }
/*      */   
/*      */ 
/*      */   public void markAsUpToDate()
/*      */   {
/* 1474 */     this.last_update_time = SystemTime.getCurrentTime();
/*      */     
/* 1476 */     this.blockedListChangedDispatcher.dispatch();
/*      */   }
/*      */   
/*      */ 
/*      */   public long getLastUpdateTime()
/*      */   {
/* 1482 */     return this.last_update_time;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTotalAddressesInRange()
/*      */   {
/* 1488 */     return this.range_manager.getTotalSpan();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(IPFilterListener l)
/*      */   {
/* 1495 */     this.listenerz.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(IPFilterListener l)
/*      */   {
/* 1502 */     this.listenerz.remove(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addExternalHandler(IpFilterExternalHandler h)
/*      */   {
/* 1509 */     this.external_handlers.add(h);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeExternalHandler(IpFilterExternalHandler h)
/*      */   {
/* 1516 */     this.external_handlers.remove(h);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/impl/IpFilterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */