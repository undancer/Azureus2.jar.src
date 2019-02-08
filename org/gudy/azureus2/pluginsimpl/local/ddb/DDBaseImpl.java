/*      */ package org.gudy.azureus2.pluginsimpl.local.ddb;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPlugin;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginContact;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginInterface;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginKeyStats;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginListener;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginOperationListener;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginProgressListener;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginTransferHandler;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginValue;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseContact;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseEvent;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseException;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseKey;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseKeyStats;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseListener;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseProgressListener;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseTransferHandler;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseTransferType;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseValue;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DDBaseImpl
/*      */   implements DistributedDatabase
/*      */ {
/*      */   private static DDBaseImpl singleton;
/*   62 */   protected static AEMonitor class_mon = new AEMonitor("DDBaseImpl:class");
/*      */   
/*   64 */   private Map<HashWrapper, DistributedDatabaseTransferHandler> transfer_map = new HashMap();
/*      */   
/*      */ 
/*      */   public static DDBaseImpl getSingleton(AzureusCore azureus_core)
/*      */   {
/*      */     try
/*      */     {
/*   71 */       class_mon.enter();
/*      */       
/*   73 */       if (singleton == null)
/*      */       {
/*   75 */         singleton = new DDBaseImpl(azureus_core);
/*      */       }
/*      */     }
/*      */     finally {
/*   79 */       class_mon.exit();
/*      */     }
/*      */     
/*   82 */     return singleton;
/*      */   }
/*      */   
/*   85 */   private static Map<DHTPluginInterface, DistributedDatabase> dht_pi_map = new HashMap();
/*      */   private final AzureusCore azureus_core;
/*      */   private final DDBaseTTTorrent torrent_transfer;
/*      */   private final String network;
/*      */   private DHTPluginInterface dht_use_accessor;
/*      */   
/*   91 */   public static List<DistributedDatabase> getDDBs(Download download) { List<DistributedDatabase> result = new ArrayList();
/*      */     
/*   93 */     String[] networks = PluginCoreUtils.unwrap(download).getDownloadState().getNetworks();
/*      */     
/*   95 */     for (String net : networks)
/*      */     {
/*   97 */       if (net == "Public")
/*      */       {
/*   99 */         DistributedDatabase ddb = getSingleton(AzureusCoreFactory.getSingleton());
/*      */         
/*  101 */         if (ddb.isAvailable())
/*      */         {
/*  103 */           result.add(ddb);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  108 */         Map<String, Object> options = new HashMap();
/*      */         
/*  110 */         options.put("download", download);
/*      */         
/*  112 */         DHTPluginInterface dpi = AEProxyFactory.getPluginDHTProxy("ddb", net, options);
/*      */         
/*  114 */         if (dpi != null)
/*      */         {
/*      */           DistributedDatabase ddb;
/*      */           
/*  118 */           synchronized (dht_pi_map)
/*      */           {
/*  120 */             ddb = (DistributedDatabase)dht_pi_map.get(dpi);
/*      */             
/*  122 */             if (ddb == null)
/*      */             {
/*  124 */               ddb = new DDBaseImpl(net, dpi);
/*      */               
/*  126 */               dht_pi_map.put(dpi, ddb);
/*      */             }
/*      */           }
/*      */           
/*  130 */           if (ddb.isAvailable())
/*      */           {
/*  132 */             result.add(ddb);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  138 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static List<DistributedDatabase> getDDBs(String[] networks)
/*      */   {
/*  145 */     return getDDBs(networks, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static List<DistributedDatabase> getDDBs(String[] networks, Map<String, Object> _options)
/*      */   {
/*  153 */     List<DistributedDatabase> result = new ArrayList();
/*      */     
/*  155 */     for (String net : networks)
/*      */     {
/*  157 */       if (net == "Public")
/*      */       {
/*  159 */         DistributedDatabase ddb = getSingleton(AzureusCoreFactory.getSingleton());
/*      */         
/*  161 */         if (ddb.isAvailable())
/*      */         {
/*  163 */           result.add(ddb);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  168 */         Map<String, Object> options = new HashMap();
/*      */         
/*  170 */         options.put("networks", networks);
/*      */         
/*  172 */         if (_options != null)
/*      */         {
/*  174 */           options.putAll(_options);
/*      */         }
/*      */         
/*  177 */         DHTPluginInterface dpi = AEProxyFactory.getPluginDHTProxy("ddb", net, options);
/*      */         
/*  179 */         if (dpi != null)
/*      */         {
/*      */           DistributedDatabase ddb;
/*      */           
/*  183 */           synchronized (dht_pi_map)
/*      */           {
/*  185 */             ddb = (DistributedDatabase)dht_pi_map.get(dpi);
/*      */             
/*  187 */             if (ddb == null)
/*      */             {
/*  189 */               ddb = new DDBaseImpl(net, dpi);
/*      */               
/*  191 */               dht_pi_map.put(dpi, ddb);
/*      */             }
/*      */           }
/*      */           
/*  195 */           if (ddb.isAvailable())
/*      */           {
/*  197 */             result.add(ddb);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  203 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  213 */   private CopyOnWriteList<DistributedDatabaseListener> listeners = new CopyOnWriteList();
/*      */   
/*      */ 
/*      */ 
/*      */   protected DDBaseImpl(AzureusCore _azureus_core)
/*      */   {
/*  219 */     this.azureus_core = _azureus_core;
/*      */     
/*  221 */     this.torrent_transfer = new DDBaseTTTorrent(this);
/*      */     
/*  223 */     this.network = "Public";
/*      */     
/*  225 */     grabDHT();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DDBaseImpl(String _net, DHTPluginInterface _dht)
/*      */   {
/*  233 */     this.network = _net;
/*  234 */     this.dht_use_accessor = _dht;
/*      */     
/*  236 */     this.azureus_core = null;
/*  237 */     this.torrent_transfer = new DDBaseTTTorrent(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getNetwork()
/*      */   {
/*  243 */     return this.network;
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTPluginInterface getDHTPlugin()
/*      */   {
/*  249 */     return this.dht_use_accessor;
/*      */   }
/*      */   
/*      */ 
/*      */   public DDBaseTTTorrent getTTTorrent()
/*      */   {
/*  255 */     return this.torrent_transfer;
/*      */   }
/*      */   
/*      */ 
/*      */   protected DHTPluginInterface grabDHT()
/*      */   {
/*  261 */     if (this.dht_use_accessor != null)
/*      */     {
/*  263 */       return this.dht_use_accessor;
/*      */     }
/*      */     try
/*      */     {
/*  267 */       class_mon.enter();
/*      */       
/*  269 */       if (this.dht_use_accessor == null)
/*      */       {
/*  271 */         PluginInterface dht_pi = this.azureus_core.getPluginManager().getPluginInterfaceByClass(DHTPlugin.class);
/*      */         
/*      */ 
/*      */ 
/*  275 */         if (dht_pi != null)
/*      */         {
/*  277 */           this.dht_use_accessor = ((DHTPluginInterface)dht_pi.getPlugin());
/*      */           
/*  279 */           if (this.dht_use_accessor.isEnabled())
/*      */           {
/*  281 */             this.dht_use_accessor.addListener(new DHTPluginListener()
/*      */             {
/*      */ 
/*      */ 
/*      */               public void localAddressChanged(DHTPluginContact local_contact)
/*      */               {
/*      */ 
/*  288 */                 List<DistributedDatabaseListener> list = DDBaseImpl.this.listeners.getList();
/*      */                 
/*  290 */                 DDBaseImpl.dbEvent ev = new DDBaseImpl.dbEvent(DDBaseImpl.this, 10);
/*      */                 
/*  292 */                 for (DistributedDatabaseListener l : list) {
/*      */                   try
/*      */                   {
/*  295 */                     l.event(ev);
/*      */                   }
/*      */                   catch (Throwable e)
/*      */                   {
/*  299 */                     Debug.out(e);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             });
/*      */             try
/*      */             {
/*  306 */               addTransferHandler(this.torrent_transfer, this.torrent_transfer);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  310 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/*  317 */       class_mon.exit();
/*      */     }
/*      */     
/*  320 */     return this.dht_use_accessor;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isAvailable()
/*      */   {
/*  326 */     DHTPluginInterface dht = grabDHT();
/*      */     
/*  328 */     if (dht == null)
/*      */     {
/*  330 */       return false;
/*      */     }
/*      */     
/*  333 */     return dht.isEnabled();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isInitialized()
/*      */   {
/*  339 */     DHTPluginInterface dht = grabDHT();
/*      */     
/*  341 */     if (dht == null)
/*      */     {
/*  343 */       return false;
/*      */     }
/*      */     
/*  346 */     return !dht.isInitialising();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isExtendedUseAllowed()
/*      */   {
/*  353 */     DHTPluginInterface dht = grabDHT();
/*      */     
/*  355 */     if (dht == null)
/*      */     {
/*  357 */       return false;
/*      */     }
/*      */     
/*  360 */     return dht.isExtendedUseAllowed();
/*      */   }
/*      */   
/*      */ 
/*      */   public DistributedDatabaseContact getLocalContact()
/*      */   {
/*  366 */     DHTPluginInterface dht = grabDHT();
/*      */     
/*  368 */     if (dht == null)
/*      */     {
/*  370 */       return null;
/*      */     }
/*      */     
/*  373 */     return new DDBaseContactImpl(this, dht.getLocalAddress());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void throwIfNotAvailable()
/*      */     throws DistributedDatabaseException
/*      */   {
/*  381 */     if (!isAvailable())
/*      */     {
/*  383 */       throw new DistributedDatabaseException("DHT not available");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected DHTPluginInterface getDHT()
/*      */     throws DistributedDatabaseException
/*      */   {
/*  392 */     throwIfNotAvailable();
/*      */     
/*  394 */     return grabDHT();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/*  401 */     DHTPluginInterface dht = grabDHT();
/*      */     
/*  403 */     if (dht != null)
/*      */     {
/*  405 */       dht.log(str);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public DistributedDatabaseKey createKey(Object key)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  415 */     throwIfNotAvailable();
/*      */     
/*  417 */     return new DDBaseKeyImpl(key);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DistributedDatabaseKey createKey(Object key, String description)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  427 */     throwIfNotAvailable();
/*      */     
/*  429 */     return new DDBaseKeyImpl(key, description);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public DistributedDatabaseValue createValue(Object value)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  438 */     throwIfNotAvailable();
/*      */     
/*  440 */     return new DDBaseValueImpl(new DDBaseContactImpl(this, getDHT().getLocalAddress()), value, SystemTime.getCurrentTime(), -1L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public DistributedDatabaseContact importContact(InetSocketAddress address)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  449 */     throwIfNotAvailable();
/*      */     
/*  451 */     DHTPluginContact contact = getDHT().importContact(address);
/*      */     
/*  453 */     if (contact == null)
/*      */     {
/*  455 */       throw new DistributedDatabaseException("import of '" + address + "' failed");
/*      */     }
/*      */     
/*  458 */     return new DDBaseContactImpl(this, contact);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DistributedDatabaseContact importContact(InetSocketAddress address, byte version)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  468 */     throwIfNotAvailable();
/*      */     
/*  470 */     DHTPluginContact contact = getDHT().importContact(address, version);
/*      */     
/*  472 */     if (contact == null)
/*      */     {
/*  474 */       throw new DistributedDatabaseException("import of '" + address + "' failed");
/*      */     }
/*      */     
/*  477 */     return new DDBaseContactImpl(this, contact);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DistributedDatabaseContact importContact(InetSocketAddress address, byte version, int preferred_dht)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  488 */     throwIfNotAvailable();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  496 */     DHTPluginContact contact = getDHT().importContact(address, version, preferred_dht == 2);
/*      */     
/*  498 */     if (contact == null)
/*      */     {
/*  500 */       throw new DistributedDatabaseException("import of '" + address + "' failed");
/*      */     }
/*      */     
/*  503 */     return new DDBaseContactImpl(this, contact);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public DistributedDatabaseContact importContact(Map<String, Object> map)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  512 */     throwIfNotAvailable();
/*      */     
/*  514 */     DHTPluginContact contact = getDHT().importContact(map);
/*      */     
/*  516 */     if (contact == null)
/*      */     {
/*  518 */       throw new DistributedDatabaseException("import of '" + map + "' failed");
/*      */     }
/*      */     
/*  521 */     return new DDBaseContactImpl(this, contact);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void write(DistributedDatabaseListener listener, DistributedDatabaseKey key, DistributedDatabaseValue value)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  532 */     write(listener, key, new DistributedDatabaseValue[] { value });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void write(DistributedDatabaseListener listener, DistributedDatabaseKey key, DistributedDatabaseValue[] values)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  543 */     throwIfNotAvailable();
/*      */     
/*  545 */     for (int i = 0; i < values.length; i++)
/*      */     {
/*  547 */       if (((DDBaseValueImpl)values[i]).getBytes().length > DDBaseValueImpl.MAX_VALUE_SIZE)
/*      */       {
/*  549 */         throw new DistributedDatabaseException("Value size limited to " + DDBaseValueImpl.MAX_VALUE_SIZE + " bytes");
/*      */       }
/*      */     }
/*      */     
/*  553 */     byte extra_flags = 0;
/*      */     
/*  555 */     int key_flags = key.getFlags();
/*      */     
/*  557 */     if ((key_flags & 0x1) != 0)
/*      */     {
/*  559 */       extra_flags = (byte)(extra_flags | 0x10);
/*      */     }
/*      */     
/*  562 */     if ((key_flags & 0x2) != 0)
/*      */     {
/*  564 */       extra_flags = (byte)(extra_flags | 0x40);
/*      */     }
/*      */     
/*  567 */     if (values.length == 0)
/*      */     {
/*  569 */       delete(listener, key);
/*      */     }
/*  571 */     else if (values.length == 1)
/*      */     {
/*  573 */       getDHT().put(((DDBaseKeyImpl)key).getBytes(), key.getDescription(), ((DDBaseValueImpl)values[0]).getBytes(), (byte)(0x0 | extra_flags), new listenerMapper(listener, 1, key, 0L, false, false));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  603 */       byte[] current_key = ((DDBaseKeyImpl)key).getBytes();
/*      */       
/*      */ 
/*      */ 
/*  607 */       byte[] payload = new byte['Ȁ'];
/*  608 */       int payload_length = 1;
/*      */       
/*  610 */       int pos = 0;
/*      */       
/*  612 */       while (pos < values.length)
/*      */       {
/*  614 */         DDBaseValueImpl value = (DDBaseValueImpl)values[pos];
/*      */         
/*  616 */         byte[] bytes = value.getBytes();
/*      */         
/*  618 */         int len = bytes.length;
/*      */         
/*  620 */         if (payload_length + len < payload.length - 2)
/*      */         {
/*  622 */           payload[(payload_length++)] = ((byte)((len & 0xFF00) >> 8));
/*  623 */           payload[(payload_length++)] = ((byte)(len & 0xFF));
/*      */           
/*  625 */           System.arraycopy(bytes, 0, payload, payload_length, len);
/*      */           
/*  627 */           payload_length += len;
/*      */           
/*  629 */           pos++;
/*      */         }
/*      */         else
/*      */         {
/*  633 */           payload[0] = 1;
/*      */           
/*  635 */           byte[] copy = new byte[payload_length];
/*      */           
/*  637 */           System.arraycopy(payload, 0, copy, 0, copy.length);
/*      */           
/*  639 */           byte[] f_current_key = current_key;
/*      */           
/*  641 */           getDHT().put(f_current_key, key.getDescription(), copy, (byte)(0x4 | extra_flags), new listenerMapper(listener, 1, key, 0L, false, false));
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  648 */           payload_length = 1;
/*      */           
/*  650 */           current_key = new SHA1Simple().calculateHash(current_key);
/*      */         }
/*      */       }
/*      */       
/*  654 */       if (payload_length > 1)
/*      */       {
/*  656 */         payload[0] = 0;
/*      */         
/*  658 */         byte[] copy = new byte[payload_length];
/*      */         
/*  660 */         System.arraycopy(payload, 0, copy, 0, copy.length);
/*      */         
/*  662 */         byte[] f_current_key = current_key;
/*      */         
/*  664 */         getDHT().put(f_current_key, key.getDescription(), copy, (byte)(0x4 | extra_flags), new listenerMapper(listener, 1, key, 0L, false, false));
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
/*      */ 
/*      */   public void read(DistributedDatabaseListener listener, DistributedDatabaseKey key, long timeout)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  682 */     read(listener, key, timeout, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void read(DistributedDatabaseListener listener, DistributedDatabaseKey key, long timeout, int options)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  694 */     throwIfNotAvailable();
/*      */     
/*  696 */     boolean exhaustive = (options & 0x1) != 0;
/*  697 */     boolean high_priority = (options & 0x2) != 0;
/*      */     
/*      */ 
/*      */ 
/*  701 */     getDHT().get(((DDBaseKeyImpl)key).getBytes(), key.getDescription(), (byte)0, 256, timeout, exhaustive, high_priority, new listenerMapper(listener, 2, key, timeout, exhaustive, high_priority));
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
/*      */   public void readKeyStats(DistributedDatabaseListener listener, DistributedDatabaseKey key, long timeout)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  720 */     throwIfNotAvailable();
/*      */     
/*  722 */     getDHT().get(((DDBaseKeyImpl)key).getBytes(), key.getDescription(), (byte)8, 256, timeout, false, false, new listenerMapper(listener, 6, key, timeout, false, false));
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
/*      */   public List<DistributedDatabaseValue> getValues(DistributedDatabaseKey key)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  740 */     List<DHTPluginValue> values = getDHT().getValues(((DDBaseKeyImpl)key).getBytes());
/*      */     
/*  742 */     List<DistributedDatabaseValue> result = new ArrayList(values.size());
/*      */     
/*  744 */     for (DHTPluginValue v : values)
/*      */     {
/*  746 */       DDBaseContactImpl originator = null;
/*      */       
/*  748 */       DDBaseValueImpl value = new DDBaseValueImpl(originator, v.getValue(), v.getCreationTime(), v.getVersion());
/*      */       
/*  750 */       result.add(value);
/*      */     }
/*      */     
/*  753 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void delete(DistributedDatabaseListener listener, DistributedDatabaseKey key)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  764 */     throwIfNotAvailable();
/*      */     
/*  766 */     getDHT().remove(((DDBaseKeyImpl)key).getBytes(), key.getDescription(), new listenerMapper(listener, 3, key, 0L, false, false));
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
/*      */   public void delete(DistributedDatabaseListener listener, DistributedDatabaseKey key, DistributedDatabaseContact[] targets)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  780 */     throwIfNotAvailable();
/*      */     
/*  782 */     DHTPluginContact[] plugin_targets = new DHTPluginContact[targets.length];
/*      */     
/*  784 */     for (int i = 0; i < targets.length; i++)
/*      */     {
/*  786 */       plugin_targets[i] = ((DDBaseContactImpl)targets[i]).getContact();
/*      */     }
/*      */     
/*  789 */     getDHT().remove(plugin_targets, ((DDBaseKeyImpl)key).getBytes(), key.getDescription(), new listenerMapper(listener, 3, key, 0L, false, false));
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
/*      */   public void addTransferHandler(final DistributedDatabaseTransferType type, final DistributedDatabaseTransferHandler handler)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  803 */     throwIfNotAvailable();
/*      */     
/*  805 */     HashWrapper type_key = DDBaseHelpers.getKey(type.getClass());
/*      */     
/*  807 */     if (this.transfer_map.get(type_key) != null)
/*      */     {
/*  809 */       throw new DistributedDatabaseException("Handler for class '" + type.getClass().getName() + "' already defined");
/*      */     }
/*      */     
/*  812 */     this.transfer_map.put(type_key, handler);
/*      */     
/*      */     String handler_name;
/*      */     final String handler_name;
/*  816 */     if (type == this.torrent_transfer)
/*      */     {
/*  818 */       handler_name = "Torrent Transfer";
/*      */     }
/*      */     else
/*      */     {
/*  822 */       String class_name = type.getClass().getName();
/*      */       
/*  824 */       int pos = class_name.indexOf('$');
/*      */       
/*  826 */       if (pos != -1)
/*      */       {
/*  828 */         class_name = class_name.substring(pos + 1);
/*      */       }
/*      */       else
/*      */       {
/*  832 */         pos = class_name.lastIndexOf('.');
/*      */         
/*  834 */         if (pos != -1)
/*      */         {
/*  836 */           class_name = class_name.substring(pos + 1);
/*      */         }
/*      */       }
/*      */       
/*  840 */       handler_name = "Plugin Defined (" + class_name + ")";
/*      */     }
/*      */     
/*  843 */     getDHT().registerHandler(type_key.getHash(), new DHTPluginTransferHandler()
/*      */     {
/*      */ 
/*      */ 
/*      */       public String getName()
/*      */       {
/*      */ 
/*  850 */         return handler_name;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public byte[] handleRead(DHTPluginContact originator, byte[] xfer_key)
/*      */       {
/*      */         try
/*      */         {
/*  859 */           DDBaseValueImpl res = (DDBaseValueImpl)handler.read(new DDBaseContactImpl(DDBaseImpl.this, originator), type, new DDBaseKeyImpl(xfer_key));
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  865 */           if (res == null)
/*      */           {
/*  867 */             return null;
/*      */           }
/*      */           
/*  870 */           return res.getBytes();
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  874 */           Debug.printStackTrace(e);
/*      */         }
/*  876 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public byte[] handleWrite(DHTPluginContact originator, byte[] xfer_key, byte[] value)
/*      */       {
/*      */         try
/*      */         {
/*  887 */           DDBaseContactImpl contact = new DDBaseContactImpl(DDBaseImpl.this, originator);
/*      */           
/*  889 */           DDBaseValueImpl res = (DDBaseValueImpl)handler.write(contact, type, new DDBaseKeyImpl(xfer_key), new DDBaseValueImpl(contact, value, SystemTime.getCurrentTime(), -1L));
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  896 */           if (res == null)
/*      */           {
/*  898 */             return null;
/*      */           }
/*      */           
/*  901 */           return res.getBytes();
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  905 */           Debug.printStackTrace(e);
/*      */         }
/*  907 */         return null; } }, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DistributedDatabaseTransferType getStandardTransferType(int standard_type)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  919 */     if (standard_type == 1)
/*      */     {
/*  921 */       return this.torrent_transfer;
/*      */     }
/*      */     
/*  924 */     throw new DistributedDatabaseException("unknown type");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DistributedDatabaseValue read(DDBaseContactImpl contact, final DistributedDatabaseProgressListener listener, DistributedDatabaseTransferType type, DistributedDatabaseKey key, long timeout)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  937 */     if (type == this.torrent_transfer)
/*      */     {
/*  939 */       return this.torrent_transfer.read(contact, listener, type, key, timeout);
/*      */     }
/*      */     
/*      */ 
/*  943 */     DHTPluginContact plugin_contact = contact.getContact();
/*      */     
/*  945 */     byte[] data = plugin_contact.read(listener == null ? null : new DHTPluginProgressListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void reportSize(long size)
/*      */       {
/*      */ 
/*  952 */         listener.reportSize(size);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void reportActivity(String str)
/*      */       {
/*  959 */         listener.reportActivity(str);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  966 */       public void reportCompleteness(int percent) { listener.reportCompleteness(percent); } }, DDBaseHelpers.getKey(type.getClass()).getHash(), ((DDBaseKeyImpl)key).getBytes(), timeout);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  973 */     if (data == null)
/*      */     {
/*  975 */       return null;
/*      */     }
/*      */     
/*  978 */     return new DDBaseValueImpl(contact, data, SystemTime.getCurrentTime(), -1L);
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
/*      */   protected void write(DDBaseContactImpl contact, final DistributedDatabaseProgressListener listener, DistributedDatabaseTransferType type, DistributedDatabaseKey key, DistributedDatabaseValue value, long timeout)
/*      */     throws DistributedDatabaseException
/*      */   {
/*  993 */     DHTPluginContact plugin_contact = contact.getContact();
/*      */     
/*  995 */     plugin_contact.write(listener == null ? null : new DHTPluginProgressListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void reportSize(long size)
/*      */       {
/*      */ 
/*      */ 
/* 1003 */         listener.reportSize(size);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void reportActivity(String str)
/*      */       {
/* 1010 */         listener.reportActivity(str);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1017 */       public void reportCompleteness(int percent) { listener.reportCompleteness(percent); } }, DDBaseHelpers.getKey(type.getClass()).getHash(), ((DDBaseKeyImpl)key).getBytes(), ((DDBaseValueImpl)value).getBytes(), timeout);
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
/*      */   protected DistributedDatabaseValue call(DDBaseContactImpl contact, final DistributedDatabaseProgressListener listener, DistributedDatabaseTransferType type, DistributedDatabaseValue value, long timeout)
/*      */     throws DistributedDatabaseException
/*      */   {
/* 1036 */     DHTPluginContact plugin_contact = contact.getContact();
/*      */     
/* 1038 */     byte[] data = plugin_contact.call(listener == null ? null : new DHTPluginProgressListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void reportSize(long size)
/*      */       {
/*      */ 
/*      */ 
/* 1046 */         listener.reportSize(size);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void reportActivity(String str)
/*      */       {
/* 1053 */         listener.reportActivity(str);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1060 */       public void reportCompleteness(int percent) { listener.reportCompleteness(percent); } }, DDBaseHelpers.getKey(type.getClass()).getHash(), ((DDBaseValueImpl)value).getBytes(), timeout);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1067 */     if (data == null)
/*      */     {
/* 1069 */       return null;
/*      */     }
/*      */     
/* 1072 */     return new DDBaseValueImpl(contact, data, SystemTime.getCurrentTime(), -1L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(DistributedDatabaseListener l)
/*      */   {
/* 1079 */     this.listeners.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(DistributedDatabaseListener l)
/*      */   {
/* 1086 */     this.listeners.remove(l);
/*      */   }
/*      */   
/*      */ 
/*      */   protected class listenerMapper
/*      */     implements DHTPluginOperationListener
/*      */   {
/*      */     private DistributedDatabaseListener listener;
/*      */     
/*      */     private int type;
/*      */     
/*      */     private DistributedDatabaseKey key;
/*      */     
/*      */     private byte[] key_bytes;
/*      */     
/*      */     private long timeout;
/*      */     
/*      */     private boolean complete_disabled;
/*      */     
/*      */     private boolean exhaustive;
/*      */     
/*      */     private boolean high_priority;
/*      */     
/*      */     private int continuation_num;
/*      */     
/*      */     protected listenerMapper(DistributedDatabaseListener _listener, int _type, DistributedDatabaseKey _key, long _timeout, boolean _exhaustive, boolean _high_priority)
/*      */     {
/* 1113 */       this.listener = _listener;
/* 1114 */       this.type = _type;
/* 1115 */       this.key = _key;
/* 1116 */       this.key_bytes = ((DDBaseKeyImpl)this.key).getBytes();
/* 1117 */       this.timeout = _timeout;
/* 1118 */       this.exhaustive = _exhaustive;
/* 1119 */       this.high_priority = _high_priority;
/*      */       
/* 1121 */       this.continuation_num = 1;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private listenerMapper(DistributedDatabaseListener _listener, int _type, DistributedDatabaseKey _key, byte[] _key_bytes, long _timeout, int _continuation_num)
/*      */     {
/* 1133 */       this.listener = _listener;
/* 1134 */       this.type = _type;
/* 1135 */       this.key = _key;
/* 1136 */       this.key_bytes = _key_bytes;
/* 1137 */       this.timeout = _timeout;
/*      */       
/* 1139 */       this.continuation_num = _continuation_num;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean diversified()
/*      */     {
/* 1145 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void starts(byte[] _key)
/*      */     {
/* 1152 */       this.listener.event(new DDBaseImpl.dbEvent(DDBaseImpl.this, 7, this.key));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void valueRead(DHTPluginContact originator, DHTPluginValue _value)
/*      */     {
/* 1160 */       if (this.type == 6)
/*      */       {
/* 1162 */         if ((_value.getFlags() & 0x8) == 0)
/*      */         {
/*      */ 
/*      */ 
/* 1166 */           return;
/*      */         }
/*      */         try
/*      */         {
/* 1170 */           final DHTPluginKeyStats stats = DDBaseImpl.this.getDHT().decodeStats(_value);
/*      */           
/* 1172 */           if (stats != null)
/*      */           {
/* 1174 */             DistributedDatabaseKeyStats ddb_stats = new DistributedDatabaseKeyStats()
/*      */             {
/*      */ 
/*      */               public int getEntryCount()
/*      */               {
/*      */ 
/* 1180 */                 return stats.getEntryCount();
/*      */               }
/*      */               
/*      */ 
/*      */               public int getSize()
/*      */               {
/* 1186 */                 return stats.getSize();
/*      */               }
/*      */               
/*      */ 
/*      */               public int getReadsPerMinute()
/*      */               {
/* 1192 */                 return stats.getReadsPerMinute();
/*      */               }
/*      */               
/*      */ 
/*      */               public byte getDiversification()
/*      */               {
/* 1198 */                 return stats.getDiversification();
/*      */               }
/*      */               
/* 1201 */             };
/* 1202 */             this.listener.event(new DDBaseImpl.dbEvent(DDBaseImpl.this, this.type, this.key, originator, ddb_stats));
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/* 1206 */           Debug.printStackTrace(e);
/*      */         }
/*      */       } else {
/* 1209 */         byte[] value = _value.getValue();
/*      */         
/* 1211 */         if (_value.getFlags() == 4)
/*      */         {
/* 1213 */           int pos = 1;
/*      */           
/* 1215 */           while (pos < value.length)
/*      */           {
/* 1217 */             int len = (value[(pos++)] << 8 & 0xFF00) + (value[(pos++)] & 0xFF);
/*      */             
/*      */ 
/* 1220 */             if (len > value.length - pos)
/*      */             {
/* 1222 */               Debug.out("Invalid length: len = " + len + ", remaining = " + (value.length - pos));
/*      */               
/* 1224 */               break;
/*      */             }
/*      */             
/* 1227 */             byte[] d = new byte[len];
/*      */             
/* 1229 */             System.arraycopy(value, pos, d, 0, len);
/*      */             
/* 1231 */             this.listener.event(new DDBaseImpl.dbEvent(DDBaseImpl.this, this.type, this.key, originator, d, _value.getCreationTime(), _value.getVersion()));
/*      */             
/* 1233 */             pos += len;
/*      */           }
/*      */           
/* 1236 */           if (value[0] == 1)
/*      */           {
/*      */ 
/*      */ 
/* 1240 */             byte[] next_key_bytes = new SHA1Simple().calculateHash(this.key_bytes);
/*      */             
/* 1242 */             this.complete_disabled = true;
/*      */             
/* 1244 */             DDBaseImpl.this.grabDHT().get(next_key_bytes, this.key.getDescription() + " [continuation " + this.continuation_num + "]", (byte)0, 256, this.timeout, this.exhaustive, this.high_priority, new listenerMapper(DDBaseImpl.this, this.listener, 2, this.key, next_key_bytes, this.timeout, this.continuation_num + 1));
/*      */ 
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*      */ 
/* 1256 */           this.listener.event(new DDBaseImpl.dbEvent(DDBaseImpl.this, this.type, this.key, originator, _value));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void valueWritten(DHTPluginContact target, DHTPluginValue value)
/*      */     {
/* 1266 */       this.listener.event(new DDBaseImpl.dbEvent(DDBaseImpl.this, this.type, this.key, target, value));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void complete(byte[] timeout_key, boolean timeout_occurred)
/*      */     {
/* 1274 */       if (!this.complete_disabled)
/*      */       {
/* 1276 */         this.listener.event(new DDBaseImpl.dbEvent(DDBaseImpl.this, timeout_occurred ? 5 : 4, this.key));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected class dbEvent
/*      */     implements DistributedDatabaseEvent
/*      */   {
/*      */     private int type;
/*      */     
/*      */     private DistributedDatabaseKey key;
/*      */     
/*      */     private DistributedDatabaseKeyStats key_stats;
/*      */     
/*      */     private DistributedDatabaseValue value;
/*      */     
/*      */     private DDBaseContactImpl contact;
/*      */     
/*      */ 
/*      */     protected dbEvent(int _type)
/*      */     {
/* 1298 */       this.type = _type;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected dbEvent(int _type, DistributedDatabaseKey _key)
/*      */     {
/* 1306 */       this.type = _type;
/* 1307 */       this.key = _key;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected dbEvent(int _type, DistributedDatabaseKey _key, DHTPluginContact _contact, DHTPluginValue _value)
/*      */     {
/* 1317 */       this.type = _type;
/* 1318 */       this.key = _key;
/*      */       
/* 1320 */       this.contact = new DDBaseContactImpl(DDBaseImpl.this, _contact);
/*      */       
/* 1322 */       this.value = new DDBaseValueImpl(this.contact, _value.getValue(), _value.getCreationTime(), _value.getVersion());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected dbEvent(int _type, DistributedDatabaseKey _key, DHTPluginContact _contact, DistributedDatabaseKeyStats _key_stats)
/*      */     {
/* 1332 */       this.type = _type;
/* 1333 */       this.key = _key;
/*      */       
/* 1335 */       this.contact = new DDBaseContactImpl(DDBaseImpl.this, _contact);
/*      */       
/* 1337 */       this.key_stats = _key_stats;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected dbEvent(int _type, DistributedDatabaseKey _key, DHTPluginContact _contact, byte[] _value, long _ct, long _v)
/*      */     {
/* 1349 */       this.type = _type;
/* 1350 */       this.key = _key;
/*      */       
/* 1352 */       this.contact = new DDBaseContactImpl(DDBaseImpl.this, _contact);
/*      */       
/* 1354 */       this.value = new DDBaseValueImpl(this.contact, _value, _ct, _v);
/*      */     }
/*      */     
/*      */ 
/*      */     public int getType()
/*      */     {
/* 1360 */       return this.type;
/*      */     }
/*      */     
/*      */ 
/*      */     public DistributedDatabaseKey getKey()
/*      */     {
/* 1366 */       return this.key;
/*      */     }
/*      */     
/*      */ 
/*      */     public DistributedDatabaseKeyStats getKeyStats()
/*      */     {
/* 1372 */       return this.key_stats;
/*      */     }
/*      */     
/*      */ 
/*      */     public DistributedDatabaseValue getValue()
/*      */     {
/* 1378 */       return this.value;
/*      */     }
/*      */     
/*      */ 
/*      */     public DistributedDatabaseContact getContact()
/*      */     {
/* 1384 */       return this.contact;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ddb/DDBaseImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */