/*      */ package com.aelitis.azureus.plugins.dht.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.dht.DHT;
/*      */ import com.aelitis.azureus.core.dht.DHTLogger;
/*      */ import com.aelitis.azureus.core.dht.DHTStorageAdapter;
/*      */ import com.aelitis.azureus.core.dht.DHTStorageBlock;
/*      */ import com.aelitis.azureus.core.dht.DHTStorageKey;
/*      */ import com.aelitis.azureus.core.dht.DHTStorageKeyStats;
/*      */ import com.aelitis.azureus.core.dht.impl.DHTLog;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileDescriptor;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.math.BigInteger;
/*      */ import java.security.KeyFactory;
/*      */ import java.security.Signature;
/*      */ import java.security.interfaces.RSAPublicKey;
/*      */ import java.security.spec.RSAPublicKeySpec;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.ByteArrayHashMap;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DHTPluginStorageManager
/*      */   implements DHTStorageAdapter
/*      */ {
/*      */   private static final String pub_exp = "10001";
/*      */   private static final String modulus = "b8a440c76405b2175a24c86d70f2c71929673a31045791d8bd84220a48729998900d227b560e88357074fa534ccccc6944729bfdda5413622f068e7926176a8afc8b75d4ba6cde760096624415b544f73677e8093ddba46723cb973b4d55f61c2003b73f52582894c018e141e8d010bb615cdbbfaeb97a7af6ce1a5a20a62994da81bde6487e8a39e66c8df0cfd9d763c2da4729cbf54278ea4912169edb0a33";
/*      */   private static final long ADDRESS_EXPIRY = 604800000L;
/*      */   private static final int DIV_WIDTH = 10;
/*      */   private static final int DIV_FRAG_GET_SIZE = 2;
/*      */   private static final long DIV_EXPIRY_MIN = 172800000L;
/*      */   private static final long DIV_EXPIRY_RAND = 86400000L;
/*      */   private static final long KEY_BLOCK_TIMEOUT_SECS = 604800L;
/*      */   public static final int LOCAL_DIVERSIFICATION_SIZE_LIMIT = 32768;
/*      */   public static final int LOCAL_DIVERSIFICATION_ENTRIES_LIMIT = 2048;
/*      */   public static final int LOCAL_DIVERSIFICATION_READS_PER_MIN_SAMPLES = 3;
/*      */   public static final int LOCAL_DIVERSIFICATION_READS_PER_MIN = 30;
/*      */   public static final int MAX_STORAGE_KEYS = 65536;
/*      */   private int network;
/*      */   private DHTLogger log;
/*      */   private File data_dir;
/*   86 */   private AEMonitor address_mon = new AEMonitor("DHTPluginStorageManager:address");
/*   87 */   private AEMonitor contact_mon = new AEMonitor("DHTPluginStorageManager:contact");
/*   88 */   private AEMonitor storage_mon = new AEMonitor("DHTPluginStorageManager:storage");
/*   89 */   private AEMonitor version_mon = new AEMonitor("DHTPluginStorageManager:version");
/*   90 */   private AEMonitor key_block_mon = new AEMonitor("DHTPluginStorageManager:block");
/*      */   
/*   92 */   private Map version_map = new HashMap();
/*   93 */   private Map recent_addresses = new HashMap();
/*      */   
/*   95 */   private Map remote_diversifications = new HashMap();
/*   96 */   private Map local_storage_keys = new HashMap();
/*      */   
/*      */   private int remote_freq_div_count;
/*      */   
/*      */   private int remote_size_div_count;
/*  101 */   private volatile ByteArrayHashMap key_block_map_cow = new ByteArrayHashMap();
/*  102 */   private volatile DHTStorageBlock[] key_blocks_direct_cow = new DHTStorageBlock[0];
/*      */   private BloomFilter kb_verify_fail_bloom;
/*      */   private long kb_verify_fail_bloom_create_time;
/*      */   private long suspend_divs_until;
/*      */   private static RSAPublicKey key_block_public_key;
/*      */   
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/*  112 */       KeyFactory key_factory = KeyFactory.getInstance("RSA");
/*      */       
/*  114 */       RSAPublicKeySpec public_key_spec = new RSAPublicKeySpec(new BigInteger("b8a440c76405b2175a24c86d70f2c71929673a31045791d8bd84220a48729998900d227b560e88357074fa534ccccc6944729bfdda5413622f068e7926176a8afc8b75d4ba6cde760096624415b544f73677e8093ddba46723cb973b4d55f61c2003b73f52582894c018e141e8d010bb615cdbbfaeb97a7af6ce1a5a20a62994da81bde6487e8a39e66c8df0cfd9d763c2da4729cbf54278ea4912169edb0a33", 16), new BigInteger("10001", 16));
/*      */       
/*      */ 
/*  117 */       key_block_public_key = (RSAPublicKey)key_factory.generatePublic(public_key_spec);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  121 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTPluginStorageManager(int _network, DHTLogger _log, File _data_dir)
/*      */   {
/*  131 */     this.network = _network;
/*  132 */     this.log = _log;
/*  133 */     this.data_dir = _data_dir;
/*      */     
/*  135 */     if (this.network == 1)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  140 */       String key_ver = "dht.plugin.sm.hack.kill.div.2.v";
/*  141 */       String key = "dht.plugin.sm.hack.kill.div.2";
/*      */       
/*  143 */       int HACK_VER = 6;
/*  144 */       long HACK_PERIOD = 259200000L;
/*      */       
/*  146 */       long suspend_ver = COConfigurationManager.getLongParameter(key_ver, 0L);
/*      */       
/*      */       long suspend_start;
/*      */       
/*  150 */       if (suspend_ver < 6L)
/*      */       {
/*  152 */         long suspend_start = 0L;
/*      */         
/*  154 */         COConfigurationManager.setParameter(key_ver, 6);
/*      */       }
/*      */       else
/*      */       {
/*  158 */         suspend_start = COConfigurationManager.getLongParameter(key, 0L);
/*      */       }
/*      */       
/*  161 */       long now = SystemTime.getCurrentTime();
/*      */       
/*  163 */       if (suspend_start == 0L)
/*      */       {
/*  165 */         suspend_start = now;
/*      */         
/*  167 */         COConfigurationManager.setParameter(key, suspend_start);
/*      */       }
/*      */       
/*  170 */       this.suspend_divs_until = (suspend_start + 259200000L);
/*      */       
/*  172 */       if (suspendDivs())
/*      */       {
/*  174 */         writeMapToFile(new HashMap(), "diverse");
/*      */       }
/*      */       else
/*      */       {
/*  178 */         this.suspend_divs_until = 0L;
/*      */       }
/*      */     }
/*      */     
/*  182 */     FileUtil.mkdirs(this.data_dir);
/*      */     
/*  184 */     readRecentAddresses();
/*      */     
/*  186 */     readDiversifications();
/*      */     
/*  188 */     readVersionData();
/*      */     
/*  190 */     readKeyBlocks();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNetwork()
/*      */   {
/*  196 */     return this.network;
/*      */   }
/*      */   
/*      */ 
/*      */   public void importContacts(DHT dht)
/*      */   {
/*      */     try
/*      */     {
/*  204 */       this.contact_mon.enter();
/*      */       
/*  206 */       File target = new File(this.data_dir, "contacts.dat");
/*      */       
/*  208 */       if (!target.exists())
/*      */       {
/*  210 */         target = new File(this.data_dir, "contacts.saving");
/*      */       }
/*      */       
/*  213 */       if (target.exists())
/*      */       {
/*  215 */         DataInputStream dis = new DataInputStream(new FileInputStream(target));
/*      */         
/*      */         try
/*      */         {
/*  219 */           dht.importState(dis);
/*      */         }
/*      */         finally
/*      */         {
/*  223 */           dis.close();
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  228 */       Debug.printStackTrace(e);
/*      */     }
/*      */     finally
/*      */     {
/*  232 */       this.contact_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void exportContacts(DHT dht)
/*      */   {
/*      */     try
/*      */     {
/*  241 */       this.contact_mon.enter();
/*      */       
/*  243 */       File saving = new File(this.data_dir, "contacts.saving");
/*  244 */       File target = new File(this.data_dir, "contacts.dat");
/*      */       
/*  246 */       saving.delete();
/*      */       
/*  248 */       DataOutputStream dos = null;
/*      */       
/*  250 */       boolean ok = false;
/*      */       try
/*      */       {
/*  253 */         FileOutputStream fos = new FileOutputStream(saving);
/*      */         
/*  255 */         dos = new DataOutputStream(fos);
/*      */         
/*  257 */         dht.exportState(dos, 32);
/*      */         
/*  259 */         dos.flush();
/*      */         
/*  261 */         fos.getFD().sync();
/*      */         
/*  263 */         ok = true;
/*      */       }
/*      */       finally
/*      */       {
/*  267 */         if (dos != null)
/*      */         {
/*  269 */           dos.close();
/*      */           
/*  271 */           if (ok)
/*      */           {
/*  273 */             target.delete();
/*      */             
/*  275 */             saving.renameTo(target);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  281 */       Debug.printStackTrace(e);
/*      */     }
/*      */     finally
/*      */     {
/*  285 */       this.contact_mon.exit();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  291 */     writeDiversifications();
/*      */   }
/*      */   
/*      */   protected void readRecentAddresses()
/*      */   {
/*      */     try
/*      */     {
/*  298 */       this.address_mon.enter();
/*      */       
/*  300 */       this.recent_addresses = readMapFromFile("addresses");
/*      */     }
/*      */     finally
/*      */     {
/*  304 */       this.address_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   protected void writeRecentAddresses()
/*      */   {
/*      */     try
/*      */     {
/*  312 */       this.address_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*  316 */       Iterator it = this.recent_addresses.keySet().iterator();
/*      */       
/*  318 */       while (it.hasNext())
/*      */       {
/*  320 */         String key = (String)it.next();
/*      */         
/*  322 */         if (!key.equals("most_recent"))
/*      */         {
/*  324 */           Long time = (Long)this.recent_addresses.get(key);
/*      */           
/*  326 */           if (SystemTime.getCurrentTime() - time.longValue() > 604800000L)
/*      */           {
/*  328 */             it.remove();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  333 */       writeMapToFile(this.recent_addresses, "addresses");
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  337 */       Debug.printStackTrace(e);
/*      */     }
/*      */     finally
/*      */     {
/*  341 */       this.address_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void recordCurrentAddress(String address)
/*      */   {
/*      */     try
/*      */     {
/*  350 */       this.address_mon.enter();
/*      */       
/*  352 */       this.recent_addresses.put(address, new Long(SystemTime.getCurrentTime()));
/*      */       
/*  354 */       this.recent_addresses.put("most_recent", address.getBytes());
/*      */       
/*  356 */       writeRecentAddresses();
/*      */     }
/*      */     finally
/*      */     {
/*  360 */       this.address_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getMostRecentAddress()
/*      */   {
/*  367 */     byte[] addr = (byte[])this.recent_addresses.get("most_recent");
/*      */     
/*  369 */     if (addr == null)
/*      */     {
/*  371 */       return null;
/*      */     }
/*      */     
/*  374 */     return new String(addr);
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean isRecentAddress(String address)
/*      */   {
/*      */     try
/*      */     {
/*  382 */       this.address_mon.enter();
/*      */       
/*  384 */       if (this.recent_addresses.containsKey(address))
/*      */       {
/*  386 */         return true;
/*      */       }
/*      */       
/*  389 */       String most_recent = getMostRecentAddress();
/*      */       
/*  391 */       return (most_recent != null) && (most_recent.equals(address));
/*      */     }
/*      */     finally
/*      */     {
/*  395 */       this.address_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void localContactChanged(DHTTransportContact contact)
/*      */   {
/*  403 */     purgeDirectKeyBlocks();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected Map readMapFromFile(String file_prefix)
/*      */   {
/*      */     try
/*      */     {
/*  412 */       File target = new File(this.data_dir, file_prefix + ".dat");
/*      */       
/*  414 */       if (!target.exists())
/*      */       {
/*  416 */         target = new File(this.data_dir, file_prefix + ".saving");
/*      */       }
/*      */       
/*  419 */       if (target.exists())
/*      */       {
/*  421 */         BufferedInputStream is = new BufferedInputStream(new FileInputStream(target));
/*      */         try
/*      */         {
/*  424 */           return BDecoder.decode(is);
/*      */         }
/*      */         finally
/*      */         {
/*  428 */           is.close();
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  433 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/*  436 */     return new HashMap();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void writeMapToFile(Map map, String file_prefix)
/*      */   {
/*      */     try
/*      */     {
/*  445 */       File saving = new File(this.data_dir, file_prefix + ".saving");
/*  446 */       File target = new File(this.data_dir, file_prefix + ".dat");
/*      */       
/*  448 */       saving.delete();
/*      */       
/*  450 */       if (map.size() == 0)
/*      */       {
/*  452 */         target.delete();
/*      */       }
/*      */       else
/*      */       {
/*  456 */         FileOutputStream os = null;
/*      */         
/*  458 */         boolean ok = false;
/*      */         try
/*      */         {
/*  461 */           byte[] data = BEncoder.encode(map);
/*      */           
/*  463 */           os = new FileOutputStream(saving);
/*      */           
/*  465 */           os.write(data);
/*      */           
/*  467 */           os.flush();
/*      */           
/*  469 */           os.getFD().sync();
/*      */           
/*  471 */           os.close();
/*      */           
/*  473 */           ok = true;
/*      */         }
/*      */         finally
/*      */         {
/*  477 */           if (os != null)
/*      */           {
/*  479 */             os.close();
/*      */             
/*  481 */             if (ok)
/*      */             {
/*  483 */               target.delete();
/*      */               
/*  485 */               saving.renameTo(target);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  492 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */   protected void readVersionData()
/*      */   {
/*      */     try
/*      */     {
/*  500 */       this.version_mon.enter();
/*      */       
/*  502 */       this.version_map = readMapFromFile("version");
/*      */     }
/*      */     finally
/*      */     {
/*  506 */       this.version_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   protected void writeVersionData()
/*      */   {
/*      */     try
/*      */     {
/*  514 */       this.version_mon.enter();
/*      */       
/*  516 */       writeMapToFile(this.version_map, "version");
/*      */     }
/*      */     finally
/*      */     {
/*  520 */       this.version_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public int getNextValueVersions(int num)
/*      */   {
/*      */     try
/*      */     {
/*  528 */       this.version_mon.enter();
/*      */       
/*  530 */       Long l_next = (Long)this.version_map.get("next");
/*      */       
/*  532 */       int now = (int)(SystemTime.getCurrentTime() / 1000L);
/*      */       
/*      */       int next;
/*      */       int next;
/*  536 */       if (l_next == null)
/*      */       {
/*  538 */         next = now;
/*      */       }
/*      */       else
/*      */       {
/*  542 */         next = l_next.intValue();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  547 */         if (next < now)
/*      */         {
/*  549 */           next = now;
/*      */         }
/*      */       }
/*      */       
/*  553 */       this.version_map.put("next", new Long(next + num));
/*      */       
/*  555 */       writeVersionData();
/*      */       
/*  557 */       return next;
/*      */     }
/*      */     finally
/*      */     {
/*  561 */       this.version_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTStorageKey keyCreated(HashWrapper key, boolean local)
/*      */   {
/*      */     try
/*      */     {
/*  575 */       this.storage_mon.enter();
/*      */       
/*  577 */       return getStorageKey(key);
/*      */     }
/*      */     finally
/*      */     {
/*  581 */       this.storage_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void keyDeleted(DHTStorageKey key)
/*      */   {
/*      */     try
/*      */     {
/*  592 */       this.storage_mon.enter();
/*      */       
/*  594 */       deleteStorageKey((storageKey)key);
/*      */     }
/*      */     finally
/*      */     {
/*  598 */       this.storage_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public int getKeyCount()
/*      */   {
/*      */     try
/*      */     {
/*  606 */       this.storage_mon.enter();
/*      */       
/*  608 */       return this.local_storage_keys.size();
/*      */     }
/*      */     finally
/*      */     {
/*  612 */       this.storage_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void keyRead(DHTStorageKey key, DHTTransportContact contact)
/*      */   {
/*      */     try
/*      */     {
/*  624 */       this.storage_mon.enter();
/*      */       
/*  626 */       ((storageKey)key).read(contact);
/*      */     }
/*      */     finally
/*      */     {
/*  630 */       this.storage_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void serialiseStats(storageKey key, DataOutputStream dos)
/*      */     throws IOException
/*      */   {
/*  641 */     dos.writeByte(0);
/*  642 */     dos.writeInt(key.getEntryCount());
/*  643 */     dos.writeInt(key.getSize());
/*  644 */     dos.writeInt(key.getReadsPerMinute());
/*  645 */     dos.writeByte(key.getDiversificationType());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTStorageKeyStats deserialiseStats(DataInputStream is)
/*      */     throws IOException
/*      */   {
/*  654 */     return decodeStats(is);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DHTStorageKeyStats decodeStats(DataInputStream is)
/*      */     throws IOException
/*      */   {
/*  663 */     byte version = is.readByte();
/*      */     
/*  665 */     int entry_count = is.readInt();
/*  666 */     final int size = is.readInt();
/*  667 */     final int reads = is.readInt();
/*  668 */     final byte div = is.readByte();
/*      */     
/*  670 */     new DHTStorageKeyStats()
/*      */     {
/*      */ 
/*      */       public int getEntryCount()
/*      */       {
/*      */ 
/*  676 */         return this.val$entry_count;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getSize()
/*      */       {
/*  682 */         return size;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getReadsPerMinute()
/*      */       {
/*  688 */         return reads;
/*      */       }
/*      */       
/*      */ 
/*      */       public byte getDiversification()
/*      */       {
/*  694 */         return div;
/*      */       }
/*      */       
/*      */ 
/*      */       public String getString()
/*      */       {
/*  700 */         return "entries=" + getEntryCount() + ",size=" + getSize() + ",rpm=" + getReadsPerMinute() + ",div=" + getDiversification();
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void valueAdded(DHTStorageKey key, DHTTransportValue value)
/*      */   {
/*      */     try
/*      */     {
/*  714 */       this.storage_mon.enter();
/*      */       
/*  716 */       ((storageKey)key).valueChanged(1, value.getValue().length);
/*      */     }
/*      */     finally
/*      */     {
/*  720 */       this.storage_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void valueUpdated(DHTStorageKey key, DHTTransportValue old_value, DHTTransportValue new_value)
/*      */   {
/*      */     try
/*      */     {
/*  733 */       this.storage_mon.enter();
/*      */       
/*  735 */       ((storageKey)key).valueChanged(0, new_value.getValue().length - old_value.getValue().length);
/*      */     }
/*      */     finally
/*      */     {
/*  739 */       this.storage_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void valueDeleted(DHTStorageKey key, DHTTransportValue value)
/*      */   {
/*      */     try
/*      */     {
/*  751 */       this.storage_mon.enter();
/*      */       
/*  753 */       ((storageKey)key).valueChanged(-1, -value.getValue().length);
/*      */     }
/*      */     finally
/*      */     {
/*  757 */       this.storage_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isDiversified(byte[] key)
/*      */   {
/*  765 */     HashWrapper wrapper = new HashWrapper(key);
/*      */     try
/*      */     {
/*  768 */       this.storage_mon.enter();
/*      */       
/*  770 */       return lookupDiversification(wrapper) != null;
/*      */     }
/*      */     finally
/*      */     {
/*  774 */       this.storage_mon.exit();
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
/*      */   public byte[][] getExistingDiversification(byte[] key, boolean put_operation, boolean exhaustive, int max_depth)
/*      */   {
/*  788 */     if (suspendDivs())
/*      */     {
/*  790 */       return new byte[][] { key };
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  795 */     HashWrapper wrapper = new HashWrapper(key);
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  800 */       this.storage_mon.enter();
/*      */       
/*  802 */       byte[][] res = followDivChain(wrapper, put_operation, exhaustive, max_depth);
/*      */       String trace;
/*  804 */       if ((res.length > 0) && (!Arrays.equals(res[0], key)))
/*      */       {
/*  806 */         trace = "";
/*      */         
/*  808 */         for (int i = 0; i < res.length; i++) {
/*  809 */           trace = trace + (i == 0 ? "" : ",") + DHTLog.getString2(res[i]);
/*      */         }
/*      */         
/*  812 */         this.log.log("SM: get div: " + DHTLog.getString2(key) + ", put = " + put_operation + ", exh = " + exhaustive + " -> " + trace);
/*      */       }
/*      */       
/*  815 */       return res;
/*      */     }
/*      */     finally
/*      */     {
/*  819 */       this.storage_mon.exit();
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
/*      */   public byte[][] createNewDiversification(String description, DHTTransportContact cause, byte[] key, boolean put_operation, byte diversification_type, boolean exhaustive, int max_depth)
/*      */   {
/*  833 */     if (suspendDivs())
/*      */     {
/*  835 */       if (put_operation)
/*      */       {
/*  837 */         return new byte[0][];
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  843 */     HashWrapper wrapper = new HashWrapper(key);
/*      */     try
/*      */     {
/*  846 */       this.storage_mon.enter();
/*      */       
/*  848 */       diversification div = lookupDiversification(wrapper);
/*      */       
/*  850 */       boolean created = false;
/*      */       
/*  852 */       if (div == null)
/*      */       {
/*  854 */         div = createDiversification(wrapper, diversification_type);
/*      */         
/*  856 */         created = true;
/*      */       }
/*      */       
/*  859 */       byte[][] res = followDivChain(wrapper, put_operation, exhaustive, max_depth);
/*      */       
/*  861 */       String trace = "";
/*      */       
/*  863 */       for (int i = 0; i < res.length; i++)
/*      */       {
/*  865 */         trace = trace + (i == 0 ? "" : ",") + DHTLog.getString2(res[i]);
/*      */       }
/*      */       
/*  868 */       this.log.log("SM: create div: " + DHTLog.getString2(key) + ", new=" + created + ", put = " + put_operation + ", exh=" + exhaustive + ", type=" + DHT.DT_STRINGS[diversification_type] + " -> " + trace + ", cause=" + (cause == null ? "<unknown>" : cause.getString()) + ", desc=" + description);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  900 */       return res;
/*      */     }
/*      */     finally
/*      */     {
/*  904 */       this.storage_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[][] followDivChain(HashWrapper wrapper, boolean put_operation, boolean exhaustive, int max_depth)
/*      */   {
/*  915 */     List list = new ArrayList();
/*      */     
/*  917 */     list.add(wrapper);
/*      */     
/*  919 */     list = followDivChainSupport(list, put_operation, 0, exhaustive, new ArrayList(), max_depth);
/*      */     
/*  921 */     byte[][] res = new byte[list.size()][];
/*      */     
/*  923 */     for (int i = 0; i < list.size(); i++)
/*      */     {
/*  925 */       res[i] = ((HashWrapper)list.get(i)).getBytes();
/*      */     }
/*      */     
/*  928 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected List followDivChainSupport(List list_in, boolean put_operation, int depth, boolean exhaustive, List keys_done, int max_depth)
/*      */   {
/*  940 */     List list_out = new ArrayList();
/*      */     
/*  942 */     if (depth < max_depth)
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
/*  956 */       for (int i = 0; i < list_in.size(); i++)
/*      */       {
/*  958 */         HashWrapper wrapper = (HashWrapper)list_in.get(i);
/*      */         
/*  960 */         diversification div = lookupDiversification(wrapper);
/*      */         
/*  962 */         if (div == null)
/*      */         {
/*  964 */           if (!list_out.contains(wrapper))
/*      */           {
/*  966 */             list_out.add(wrapper);
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*  971 */         else if (keys_done.contains(wrapper))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  976 */           if (!list_out.contains(wrapper))
/*      */           {
/*  978 */             list_out.add(wrapper);
/*      */           }
/*      */           
/*      */         }
/*      */         else
/*      */         {
/*  984 */           keys_done.add(wrapper);
/*      */           
/*      */ 
/*      */ 
/*  988 */           List new_list = followDivChainSupport(div.getKeys(put_operation, exhaustive), put_operation, depth + 1, exhaustive, keys_done, max_depth);
/*      */           
/*  990 */           for (int j = 0; j < new_list.size(); j++)
/*      */           {
/*  992 */             Object entry = new_list.get(j);
/*      */             
/*  994 */             if (!list_out.contains(entry))
/*      */             {
/*  996 */               list_out.add(entry);
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/* 1004 */     } else if (Constants.isCVSVersion())
/*      */     {
/* 1006 */       Debug.out("Terminated div chain lookup (max depth=" + max_depth + ") - net=" + this.network);
/*      */     }
/*      */     
/*      */ 
/* 1010 */     return list_out;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected storageKey getStorageKey(HashWrapper key)
/*      */   {
/* 1017 */     storageKey res = (storageKey)this.local_storage_keys.get(key);
/*      */     
/* 1019 */     if (res == null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1024 */       if (this.local_storage_keys.size() >= 65536)
/*      */       {
/* 1026 */         res = new storageKey(this, (byte)(suspendDivs() ? 1 : 3), key);
/*      */         
/* 1028 */         Debug.out("DHTStorageManager: max key limit exceeded");
/*      */         
/* 1030 */         this.log.log("SM: max storage key limit exceeded - " + DHTLog.getString2(key.getBytes()));
/*      */       }
/*      */       else
/*      */       {
/* 1034 */         res = new storageKey(this, (byte)1, key);
/*      */         
/* 1036 */         this.local_storage_keys.put(key, res);
/*      */       }
/*      */     }
/*      */     
/* 1040 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void deleteStorageKey(storageKey key)
/*      */   {
/* 1047 */     if (this.local_storage_keys.remove(key.getKey()) != null)
/*      */     {
/* 1049 */       if (key.getDiversificationType() != 1)
/*      */       {
/* 1051 */         writeDiversifications();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean suspendDivs()
/*      */   {
/* 1059 */     return (this.suspend_divs_until > 0L) && (this.suspend_divs_until > SystemTime.getCurrentTime());
/*      */   }
/*      */   
/*      */ 
/*      */   protected void readDiversifications()
/*      */   {
/* 1065 */     if (suspendDivs())
/*      */     {
/* 1067 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 1071 */       this.storage_mon.enter();
/*      */       
/* 1073 */       Map map = readMapFromFile("diverse");
/*      */       
/* 1075 */       List keys = (List)map.get("local");
/*      */       
/* 1077 */       if (keys != null)
/*      */       {
/* 1079 */         long now = SystemTime.getCurrentTime();
/*      */         
/* 1081 */         for (int i = 0; i < keys.size(); i++)
/*      */         {
/* 1083 */           storageKey d = storageKey.deserialise(this, (Map)keys.get(i));
/*      */           
/* 1085 */           long time_left = d.getExpiry() - now;
/*      */           
/* 1087 */           if (time_left > 0L)
/*      */           {
/* 1089 */             this.local_storage_keys.put(d.getKey(), d);
/*      */           }
/*      */           else
/*      */           {
/* 1093 */             this.log.log("SM: serialised sk: " + DHTLog.getString2(d.getKey().getBytes()) + " expired");
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1098 */       List divs = (List)map.get("remote");
/*      */       
/* 1100 */       if (divs != null)
/*      */       {
/* 1102 */         long now = SystemTime.getCurrentTime();
/*      */         
/* 1104 */         for (int i = 0; i < divs.size(); i++)
/*      */         {
/* 1106 */           diversification d = diversification.deserialise(this, (Map)divs.get(i));
/*      */           
/* 1108 */           long time_left = d.getExpiry() - now;
/*      */           
/* 1110 */           if (time_left > 0L)
/*      */           {
/* 1112 */             diversification existing = (diversification)this.remote_diversifications.put(d.getKey(), d);
/*      */             
/* 1114 */             if (existing != null)
/*      */             {
/* 1116 */               divRemoved(existing);
/*      */             }
/*      */             
/* 1119 */             divAdded(d);
/*      */           }
/*      */           else
/*      */           {
/* 1123 */             this.log.log("SM: serialised div: " + DHTLog.getString2(d.getKey().getBytes()) + " expired");
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1130 */       this.storage_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void writeDiversifications()
/*      */   {
/* 1137 */     if (suspendDivs())
/*      */     {
/* 1139 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 1143 */       this.storage_mon.enter();
/*      */       
/* 1145 */       Map map = new HashMap();
/*      */       
/* 1147 */       List keys = new ArrayList();
/*      */       
/* 1149 */       map.put("local", keys);
/*      */       
/* 1151 */       Iterator it = this.local_storage_keys.values().iterator();
/*      */       
/* 1153 */       while (it.hasNext())
/*      */       {
/* 1155 */         storageKey key = (storageKey)it.next();
/*      */         
/* 1157 */         if (key.getDiversificationType() != 1)
/*      */         {
/* 1159 */           keys.add(key.serialise());
/*      */         }
/*      */       }
/*      */       
/* 1163 */       List divs = new ArrayList();
/*      */       
/* 1165 */       map.put("remote", divs);
/*      */       
/* 1167 */       it = this.remote_diversifications.values().iterator();
/*      */       
/* 1169 */       while (it.hasNext())
/*      */       {
/* 1171 */         divs.add(((diversification)it.next()).serialise());
/*      */       }
/*      */       
/* 1174 */       writeMapToFile(map, "diverse");
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1178 */       Debug.printStackTrace(e);
/*      */     }
/*      */     finally
/*      */     {
/* 1182 */       this.storage_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected diversification lookupDiversification(HashWrapper wrapper)
/*      */   {
/* 1190 */     diversification div = (diversification)this.remote_diversifications.get(wrapper);
/*      */     
/* 1192 */     if (div != null)
/*      */     {
/* 1194 */       if (div.getExpiry() < SystemTime.getCurrentTime())
/*      */       {
/* 1196 */         this.log.log("SM: div: " + DHTLog.getString2(div.getKey().getBytes()) + " expired");
/*      */         
/* 1198 */         this.remote_diversifications.remove(wrapper);
/*      */         
/* 1200 */         divRemoved(div);
/*      */         
/* 1202 */         div = null;
/*      */       }
/*      */     }
/*      */     
/* 1206 */     return div;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected diversification createDiversification(HashWrapper wrapper, byte type)
/*      */   {
/* 1214 */     diversification div = new diversification(this, wrapper, type);
/*      */     
/* 1216 */     diversification existing = (diversification)this.remote_diversifications.put(wrapper, div);
/*      */     
/* 1218 */     if (existing != null)
/*      */     {
/* 1220 */       divRemoved(existing);
/*      */     }
/*      */     
/* 1223 */     divAdded(div);
/*      */     
/* 1225 */     writeDiversifications();
/*      */     
/* 1227 */     return div;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void divAdded(diversification div)
/*      */   {
/* 1234 */     if (div.getType() == 2)
/*      */     {
/* 1236 */       this.remote_freq_div_count += 1;
/*      */     }
/*      */     else
/*      */     {
/* 1240 */       this.remote_size_div_count += 1;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void divRemoved(diversification div)
/*      */   {
/* 1248 */     if (div.getType() == 2)
/*      */     {
/* 1250 */       this.remote_freq_div_count -= 1;
/*      */     }
/*      */     else
/*      */     {
/* 1254 */       this.remote_size_div_count -= 1;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getRemoteFreqDivCount()
/*      */   {
/* 1261 */     return this.remote_freq_div_count;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getRemoteSizeDivCount()
/*      */   {
/* 1267 */     return this.remote_size_div_count;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static String formatExpiry(long l)
/*      */   {
/* 1274 */     long diff = l - SystemTime.getCurrentTime();
/*      */     
/* 1276 */     return (diff < 0L ? "-" : "") + DisplayFormatters.formatTime(Math.abs(diff));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void readKeyBlocks()
/*      */   {
/*      */     try
/*      */     {
/* 1286 */       this.key_block_mon.enter();
/*      */       
/* 1288 */       Map map = readMapFromFile("block");
/*      */       
/* 1290 */       List entries = (List)map.get("entries");
/*      */       
/* 1292 */       int now_secs = (int)(SystemTime.getCurrentTime() / 1000L);
/*      */       
/* 1294 */       ByteArrayHashMap new_map = new ByteArrayHashMap();
/*      */       
/* 1296 */       if (entries != null)
/*      */       {
/* 1298 */         for (int i = 0; i < entries.size(); i++) {
/*      */           try
/*      */           {
/* 1301 */             Map m = (Map)entries.get(i);
/*      */             
/* 1303 */             byte[] request = (byte[])m.get("req");
/* 1304 */             byte[] cert = (byte[])m.get("cert");
/* 1305 */             int recv = ((Long)m.get("received")).intValue();
/* 1306 */             boolean direct = ((Long)m.get("direct")).longValue() == 1L;
/*      */             
/* 1308 */             if (recv > now_secs)
/*      */             {
/* 1310 */               recv = now_secs;
/*      */             }
/*      */             
/* 1313 */             keyBlock kb = new keyBlock(request, cert, recv, direct);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 1318 */             if (((direct) && (kb.isAdd())) || (now_secs - recv < 604800L))
/*      */             {
/* 1320 */               if (verifyKeyBlock(request, cert))
/*      */               {
/* 1322 */                 this.log.log("KB: deserialised " + DHTLog.getString2(kb.getKey()) + ",add=" + kb.isAdd() + ",dir=" + kb.isDirect());
/*      */                 
/* 1324 */                 new_map.put(kb.getKey(), kb);
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1330 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1335 */       this.key_block_map_cow = new_map;
/* 1336 */       this.key_blocks_direct_cow = buildKeyBlockDetails(new_map);
/*      */     }
/*      */     finally
/*      */     {
/* 1340 */       this.key_block_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected DHTStorageBlock[] buildKeyBlockDetails(ByteArrayHashMap map)
/*      */   {
/* 1348 */     List kbs = map.values();
/*      */     
/* 1350 */     Iterator it = kbs.iterator();
/*      */     
/* 1352 */     while (it.hasNext())
/*      */     {
/* 1354 */       keyBlock kb = (keyBlock)it.next();
/*      */       
/* 1356 */       if (!kb.isDirect())
/*      */       {
/* 1358 */         it.remove();
/*      */       }
/*      */     }
/*      */     
/* 1362 */     DHTStorageBlock[] new_blocks = new DHTStorageBlock[kbs.size()];
/*      */     
/* 1364 */     kbs.toArray(new_blocks);
/*      */     
/* 1366 */     return new_blocks;
/*      */   }
/*      */   
/*      */   protected void writeKeyBlocks()
/*      */   {
/*      */     try
/*      */     {
/* 1373 */       this.key_block_mon.enter();
/*      */       
/* 1375 */       Map map = new HashMap();
/*      */       
/* 1377 */       List entries = new ArrayList();
/*      */       
/* 1379 */       map.put("entries", entries);
/*      */       
/* 1381 */       List kbs = this.key_block_map_cow.values();
/*      */       
/* 1383 */       for (int i = 0; i < kbs.size(); i++)
/*      */       {
/* 1385 */         keyBlock kb = (keyBlock)kbs.get(i);
/*      */         
/* 1387 */         Map m = new HashMap();
/*      */         
/* 1389 */         m.put("req", kb.getRequest());
/* 1390 */         m.put("cert", kb.getCertificate());
/* 1391 */         m.put("received", new Long(kb.getReceived()));
/* 1392 */         m.put("direct", new Long(kb.isDirect() ? 1L : 0L));
/*      */         
/* 1394 */         entries.add(m);
/*      */       }
/*      */       
/* 1397 */       writeMapToFile(map, "block");
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1401 */       Debug.printStackTrace(e);
/*      */     }
/*      */     finally
/*      */     {
/* 1405 */       this.key_block_mon.exit();
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
/*      */   protected boolean verifyKeyBlock(keyBlock kb, DHTTransportContact originator)
/*      */   {
/* 1516 */     byte[] id = originator == null ? new byte[20] : originator.getID();
/*      */     
/* 1518 */     BloomFilter filter = this.kb_verify_fail_bloom;
/*      */     
/* 1520 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 1522 */     if ((filter == null) || (this.kb_verify_fail_bloom_create_time > now) || (now - this.kb_verify_fail_bloom_create_time > 1800000L))
/*      */     {
/*      */ 
/*      */ 
/* 1526 */       this.kb_verify_fail_bloom_create_time = now;
/*      */       
/* 1528 */       filter = BloomFilterFactory.createAddOnly(4000);
/*      */       
/* 1530 */       this.kb_verify_fail_bloom = filter;
/*      */     }
/*      */     
/* 1533 */     if (filter.contains(id))
/*      */     {
/* 1535 */       this.log.log("KB: request verify denied");
/*      */       
/* 1537 */       return false;
/*      */     }
/*      */     try
/*      */     {
/* 1541 */       Signature verifier = Signature.getInstance("MD5withRSA");
/*      */       
/* 1543 */       verifier.initVerify(key_block_public_key);
/*      */       
/* 1545 */       verifier.update(kb.getRequest());
/*      */       
/* 1547 */       if (!verifier.verify(kb.getCertificate()))
/*      */       {
/* 1549 */         this.log.log("KB: request verify failed for " + DHTLog.getString2(kb.getKey()));
/*      */         
/* 1551 */         filter.add(id);
/*      */         
/* 1553 */         return false;
/*      */       }
/*      */       
/* 1556 */       this.log.log("KB: request verify ok " + DHTLog.getString2(kb.getKey()) + ", add = " + kb.isAdd() + ", direct = " + kb.isDirect());
/*      */       
/* 1558 */       return true;
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 1562 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean verifyKeyBlock(byte[] request, byte[] signature)
/*      */   {
/*      */     try
/*      */     {
/* 1572 */       Signature verifier = Signature.getInstance("MD5withRSA");
/*      */       
/* 1574 */       verifier.initVerify(key_block_public_key);
/*      */       
/* 1576 */       verifier.update(request);
/*      */       
/* 1578 */       if (!verifier.verify(signature))
/*      */       {
/* 1580 */         return false;
/*      */       }
/*      */       
/* 1583 */       return true;
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 1587 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTStorageBlock getKeyBlockDetails(byte[] key)
/*      */   {
/* 1595 */     keyBlock kb = (keyBlock)this.key_block_map_cow.get(key);
/*      */     
/* 1597 */     if ((kb == null) || (!kb.isAdd()))
/*      */     {
/* 1599 */       return null;
/*      */     }
/*      */     
/* 1602 */     if (!kb.getLogged())
/*      */     {
/* 1604 */       kb.setLogged();
/*      */       
/* 1606 */       this.log.log("KB: Access to key '" + DHTLog.getFullString(kb.getKey()) + "' denied as it is blocked");
/*      */     }
/*      */     
/* 1609 */     return kb;
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTStorageBlock[] getDirectKeyBlocks()
/*      */   {
/* 1615 */     return this.key_blocks_direct_cow;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public byte[] getKeyForKeyBlock(byte[] request)
/*      */   {
/* 1622 */     if (request.length <= 8)
/*      */     {
/* 1624 */       return new byte[0];
/*      */     }
/*      */     
/* 1627 */     byte[] key = new byte[request.length - 8];
/*      */     
/* 1629 */     System.arraycopy(request, 8, key, 0, key.length);
/*      */     
/* 1631 */     return key;
/*      */   }
/*      */   
/*      */   protected void purgeDirectKeyBlocks()
/*      */   {
/*      */     try
/*      */     {
/* 1638 */       this.key_block_mon.enter();
/*      */       
/* 1640 */       ByteArrayHashMap new_map = new ByteArrayHashMap();
/*      */       
/* 1642 */       Iterator it = this.key_block_map_cow.values().iterator();
/*      */       
/* 1644 */       boolean changed = false;
/*      */       
/* 1646 */       while (it.hasNext())
/*      */       {
/* 1648 */         keyBlock kb = (keyBlock)it.next();
/*      */         
/* 1650 */         if (kb.isDirect())
/*      */         {
/* 1652 */           changed = true;
/*      */         }
/*      */         else
/*      */         {
/* 1656 */           new_map.put(kb.getKey(), kb);
/*      */         }
/*      */       }
/*      */       
/* 1660 */       if (changed)
/*      */       {
/* 1662 */         this.log.log("KB: Purged direct entries on ID change");
/*      */         
/* 1664 */         this.key_block_map_cow = new_map;
/* 1665 */         this.key_blocks_direct_cow = buildKeyBlockDetails(this.key_block_map_cow);
/*      */         
/* 1667 */         writeKeyBlocks();
/*      */       }
/*      */     }
/*      */     finally {
/* 1671 */       this.key_block_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setStorageForKey(String key, byte[] data)
/*      */   {
/*      */     try
/*      */     {
/* 1681 */       this.storage_mon.enter();
/*      */       
/* 1683 */       Map map = readMapFromFile("general");
/*      */       
/* 1685 */       map.put(key, data);
/*      */       
/* 1687 */       writeMapToFile(map, "general");
/*      */     }
/*      */     finally
/*      */     {
/* 1691 */       this.storage_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] getStorageForKey(String key)
/*      */   {
/*      */     try
/*      */     {
/* 1700 */       this.storage_mon.enter();
/*      */       
/* 1702 */       Map map = readMapFromFile("general");
/*      */       
/* 1704 */       return (byte[])map.get(key);
/*      */     }
/*      */     finally
/*      */     {
/* 1708 */       this.storage_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected static class keyBlock
/*      */     implements DHTStorageBlock
/*      */   {
/*      */     private byte[] request;
/*      */     
/*      */     private byte[] cert;
/*      */     
/*      */     private int received;
/*      */     
/*      */     private boolean direct;
/*      */     
/*      */     private BloomFilter sent_to_bloom;
/*      */     
/*      */     private boolean logged;
/*      */     
/*      */ 
/*      */     protected keyBlock(byte[] _request, byte[] _cert, int _received, boolean _direct)
/*      */     {
/* 1731 */       this.request = _request;
/* 1732 */       this.cert = _cert;
/* 1733 */       this.received = _received;
/* 1734 */       this.direct = _direct;
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getRequest()
/*      */     {
/* 1740 */       return this.request;
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getCertificate()
/*      */     {
/* 1746 */       return this.cert;
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getKey()
/*      */     {
/* 1752 */       byte[] key = new byte[this.request.length - 8];
/*      */       
/* 1754 */       System.arraycopy(this.request, 8, key, 0, key.length);
/*      */       
/* 1756 */       return key;
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isAdd()
/*      */     {
/* 1762 */       return this.request[0] == 1;
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean getLogged()
/*      */     {
/* 1768 */       return this.logged;
/*      */     }
/*      */     
/*      */ 
/*      */     protected void setLogged()
/*      */     {
/* 1774 */       this.logged = true;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getCreated()
/*      */     {
/* 1780 */       int created = this.request[4] << 24 & 0xFF000000 | this.request[5] << 16 & 0xFF0000 | this.request[6] << 8 & 0xFF00 | this.request[7] & 0xFF;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1786 */       return created;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getReceived()
/*      */     {
/* 1792 */       return this.received;
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isDirect()
/*      */     {
/* 1798 */       return this.direct;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public boolean hasBeenSentTo(DHTTransportContact contact)
/*      */     {
/* 1805 */       BloomFilter filter = this.sent_to_bloom;
/*      */       
/* 1807 */       if (filter == null)
/*      */       {
/* 1809 */         return false;
/*      */       }
/*      */       
/* 1812 */       return filter.contains(contact.getID());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void sentTo(DHTTransportContact contact)
/*      */     {
/* 1819 */       BloomFilter filter = this.sent_to_bloom;
/*      */       
/* 1821 */       if ((filter == null) || (filter.getEntryCount() > 100))
/*      */       {
/* 1823 */         filter = BloomFilterFactory.createAddOnly(500);
/*      */         
/* 1825 */         this.sent_to_bloom = filter;
/*      */       }
/*      */       
/* 1828 */       filter.add(contact.getID());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static class diversification
/*      */   {
/*      */     private DHTPluginStorageManager manager;
/*      */     
/*      */ 
/*      */     private HashWrapper key;
/*      */     
/*      */ 
/*      */     private byte type;
/*      */     
/*      */     private long expiry;
/*      */     
/*      */     private int[] fixed_put_offsets;
/*      */     
/*      */ 
/*      */     protected diversification(DHTPluginStorageManager _manager, HashWrapper _key, byte _type)
/*      */     {
/* 1851 */       this.manager = _manager;
/* 1852 */       this.key = _key;
/* 1853 */       this.type = _type;
/*      */       
/* 1855 */       this.expiry = (SystemTime.getCurrentTime() + 172800000L + RandomUtils.nextLong(86400000L));
/*      */       
/* 1857 */       this.fixed_put_offsets = new int[2];
/*      */       
/* 1859 */       int pos = 0;
/*      */       
/* 1861 */       while (pos < 2)
/*      */       {
/* 1863 */         int i = RandomUtils.nextInt(10);
/*      */         
/* 1865 */         boolean found = false;
/*      */         
/* 1867 */         for (int j = 0; j < pos; j++)
/*      */         {
/* 1869 */           if (i == this.fixed_put_offsets[j])
/*      */           {
/* 1871 */             found = true;
/*      */             
/* 1873 */             break;
/*      */           }
/*      */         }
/*      */         
/* 1877 */         if (!found)
/*      */         {
/* 1879 */           this.fixed_put_offsets[(pos++)] = i;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected diversification(DHTPluginStorageManager _manager, HashWrapper _key, byte _type, long _expiry, int[] _fixed_put_offsets)
/*      */     {
/* 1892 */       this.manager = _manager;
/* 1893 */       this.key = _key;
/* 1894 */       this.type = _type;
/* 1895 */       this.expiry = _expiry;
/* 1896 */       this.fixed_put_offsets = _fixed_put_offsets;
/*      */     }
/*      */     
/*      */ 
/*      */     protected Map serialise()
/*      */     {
/* 1902 */       Map map = new HashMap();
/*      */       
/* 1904 */       map.put("key", this.key.getBytes());
/* 1905 */       map.put("type", new Long(this.type));
/* 1906 */       map.put("exp", new Long(this.expiry));
/*      */       
/* 1908 */       List offsets = new ArrayList();
/*      */       
/* 1910 */       for (int i = 0; i < this.fixed_put_offsets.length; i++)
/*      */       {
/* 1912 */         offsets.add(new Long(this.fixed_put_offsets[i]));
/*      */       }
/*      */       
/* 1915 */       map.put("fpo", offsets);
/*      */       
/* 1917 */       if (Constants.isCVSVersion())
/*      */       {
/* 1919 */         this.manager.log.log("SM: serialised div: " + DHTLog.getString2(this.key.getBytes()) + ", " + DHT.DT_STRINGS[this.type] + ", " + DHTPluginStorageManager.formatExpiry(this.expiry));
/*      */       }
/*      */       
/* 1922 */       return map;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected static diversification deserialise(DHTPluginStorageManager _manager, Map _map)
/*      */     {
/* 1930 */       HashWrapper key = new HashWrapper((byte[])_map.get("key"));
/* 1931 */       int type = ((Long)_map.get("type")).intValue();
/* 1932 */       long exp = ((Long)_map.get("exp")).longValue();
/*      */       
/* 1934 */       List offsets = (List)_map.get("fpo");
/*      */       
/* 1936 */       int[] fops = new int[offsets.size()];
/*      */       
/* 1938 */       for (int i = 0; i < fops.length; i++)
/*      */       {
/* 1940 */         fops[i] = ((Long)offsets.get(i)).intValue();
/*      */       }
/*      */       
/* 1943 */       _manager.log.log("SM: deserialised div: " + DHTLog.getString2(key.getBytes()) + ", " + DHT.DT_STRINGS[type] + ", " + DHTPluginStorageManager.formatExpiry(exp));
/*      */       
/* 1945 */       return new diversification(_manager, key, (byte)type, exp, fops);
/*      */     }
/*      */     
/*      */ 
/*      */     protected HashWrapper getKey()
/*      */     {
/* 1951 */       return this.key;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getExpiry()
/*      */     {
/* 1957 */       return this.expiry;
/*      */     }
/*      */     
/*      */ 
/*      */     protected byte getType()
/*      */     {
/* 1963 */       return this.type;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected List getKeys(boolean put, boolean exhaustive)
/*      */     {
/* 1971 */       List keys = new ArrayList();
/*      */       
/* 1973 */       if (put)
/*      */       {
/* 1975 */         if (this.type == 2)
/*      */         {
/*      */ 
/*      */ 
/* 1979 */           for (int i = 0; i < 10; i++)
/*      */           {
/* 1981 */             keys.add(DHTPluginStorageManager.diversifyKey(this.key, i));
/*      */           }
/*      */           
/* 1984 */           if (exhaustive)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1990 */             keys.add(this.key);
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/* 1998 */           for (int i = 0; i < this.fixed_put_offsets.length; i++)
/*      */           {
/* 2000 */             keys.add(DHTPluginStorageManager.diversifyKey(this.key, this.fixed_put_offsets[i]));
/*      */           }
/*      */           
/* 2003 */           if (exhaustive)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2009 */             keys.add(this.key);
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */ 
/*      */       }
/* 2016 */       else if (this.type == 2)
/*      */       {
/*      */ 
/*      */ 
/* 2020 */         keys.add(DHTPluginStorageManager.diversifyKey(this.key, RandomUtils.nextInt(10)));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/* 2027 */       else if (exhaustive)
/*      */       {
/* 2029 */         for (int i = 0; i < 10; i++)
/*      */         {
/* 2031 */           keys.add(DHTPluginStorageManager.diversifyKey(this.key, i));
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 2038 */         List randoms = new ArrayList();
/*      */         
/* 2040 */         while (randoms.size() < 2)
/*      */         {
/* 2042 */           Integer i = new Integer(RandomUtils.nextInt(10));
/*      */           
/* 2044 */           if (!randoms.contains(i))
/*      */           {
/* 2046 */             randoms.add(i);
/*      */           }
/*      */         }
/*      */         
/* 2050 */         for (int i = 0; i < 2; i++)
/*      */         {
/* 2052 */           keys.add(DHTPluginStorageManager.diversifyKey(this.key, ((Integer)randoms.get(i)).intValue()));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2058 */       return keys;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static HashWrapper diversifyKey(HashWrapper key_in, int offset)
/*      */   {
/* 2067 */     return new HashWrapper(diversifyKey(key_in.getBytes(), offset));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static byte[] diversifyKey(byte[] key_in, int offset)
/*      */   {
/* 2075 */     return new SHA1Simple().calculateHash(diversifyKeyLocal(key_in, offset));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static byte[] diversifyKeyLocal(byte[] key_in, int offset)
/*      */   {
/* 2083 */     byte[] key_out = new byte[key_in.length + 1];
/*      */     
/* 2085 */     System.arraycopy(key_in, 0, key_out, 0, key_in.length);
/*      */     
/* 2087 */     key_out[key_in.length] = ((byte)offset);
/*      */     
/* 2089 */     return key_out;
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public DHTStorageBlock keyBlockRequest(DHTTransportContact originating_contact, byte[] request, byte[] signature)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_2
/*      */     //   1: arraylength
/*      */     //   2: bipush 8
/*      */     //   4: if_icmpgt +5 -> 9
/*      */     //   7: aconst_null
/*      */     //   8: areturn
/*      */     //   9: new 502	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock
/*      */     //   12: dup
/*      */     //   13: aload_2
/*      */     //   14: aload_3
/*      */     //   15: invokestatic 960	org/gudy/azureus2/core3/util/SystemTime:getCurrentTime	()J
/*      */     //   18: ldc2_w 466
/*      */     //   21: ldiv
/*      */     //   22: l2i
/*      */     //   23: aload_1
/*      */     //   24: ifnull +7 -> 31
/*      */     //   27: iconst_1
/*      */     //   28: goto +4 -> 32
/*      */     //   31: iconst_0
/*      */     //   32: invokespecial 878	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:<init>	([B[BIZ)V
/*      */     //   35: astore 4
/*      */     //   37: aload_0
/*      */     //   38: getfield 827	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   41: invokevirtual 941	org/gudy/azureus2/core3/util/AEMonitor:enter	()V
/*      */     //   44: iconst_0
/*      */     //   45: istore 5
/*      */     //   47: aload_0
/*      */     //   48: getfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   51: aload 4
/*      */     //   53: invokevirtual 876	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:getKey	()[B
/*      */     //   56: invokevirtual 947	org/gudy/azureus2/core3/util/ByteArrayHashMap:get	([B)Ljava/lang/Object;
/*      */     //   59: checkcast 502	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock
/*      */     //   62: astore 6
/*      */     //   64: aload 6
/*      */     //   66: ifnull +176 -> 242
/*      */     //   69: aload 6
/*      */     //   71: invokevirtual 874	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:isDirect	()Z
/*      */     //   74: ifeq +83 -> 157
/*      */     //   77: aload 4
/*      */     //   79: invokevirtual 874	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:isDirect	()Z
/*      */     //   82: ifne +75 -> 157
/*      */     //   85: aconst_null
/*      */     //   86: astore 7
/*      */     //   88: iload 5
/*      */     //   90: ifeq +57 -> 147
/*      */     //   93: aload_0
/*      */     //   94: getfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   97: invokevirtual 949	org/gudy/azureus2/core3/util/ByteArrayHashMap:duplicate	()Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   100: astore 8
/*      */     //   102: aload 8
/*      */     //   104: aload 4
/*      */     //   106: invokevirtual 876	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:getKey	()[B
/*      */     //   109: aload 4
/*      */     //   111: invokevirtual 950	org/gudy/azureus2/core3/util/ByteArrayHashMap:put	([BLjava/lang/Object;)Ljava/lang/Object;
/*      */     //   114: pop
/*      */     //   115: aload_1
/*      */     //   116: ifnull +9 -> 125
/*      */     //   119: aload 4
/*      */     //   121: aload_1
/*      */     //   122: invokevirtual 879	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:sentTo	(Lcom/aelitis/azureus/core/dht/transport/DHTTransportContact;)V
/*      */     //   125: aload_0
/*      */     //   126: aload 8
/*      */     //   128: putfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   131: aload_0
/*      */     //   132: aload_0
/*      */     //   133: aload_0
/*      */     //   134: getfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   137: invokevirtual 852	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:buildKeyBlockDetails	(Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;)[Lcom/aelitis/azureus/core/dht/DHTStorageBlock;
/*      */     //   140: putfield 817	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_blocks_direct_cow	[Lcom/aelitis/azureus/core/dht/DHTStorageBlock;
/*      */     //   143: aload_0
/*      */     //   144: invokevirtual 840	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:writeKeyBlocks	()V
/*      */     //   147: aload_0
/*      */     //   148: getfield 827	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   151: invokevirtual 942	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   154: aload 7
/*      */     //   156: areturn
/*      */     //   157: aload 6
/*      */     //   159: invokevirtual 869	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:getCreated	()I
/*      */     //   162: aload 4
/*      */     //   164: invokevirtual 869	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:getCreated	()I
/*      */     //   167: if_icmple +75 -> 242
/*      */     //   170: aconst_null
/*      */     //   171: astore 7
/*      */     //   173: iload 5
/*      */     //   175: ifeq +57 -> 232
/*      */     //   178: aload_0
/*      */     //   179: getfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   182: invokevirtual 949	org/gudy/azureus2/core3/util/ByteArrayHashMap:duplicate	()Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   185: astore 8
/*      */     //   187: aload 8
/*      */     //   189: aload 4
/*      */     //   191: invokevirtual 876	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:getKey	()[B
/*      */     //   194: aload 4
/*      */     //   196: invokevirtual 950	org/gudy/azureus2/core3/util/ByteArrayHashMap:put	([BLjava/lang/Object;)Ljava/lang/Object;
/*      */     //   199: pop
/*      */     //   200: aload_1
/*      */     //   201: ifnull +9 -> 210
/*      */     //   204: aload 4
/*      */     //   206: aload_1
/*      */     //   207: invokevirtual 879	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:sentTo	(Lcom/aelitis/azureus/core/dht/transport/DHTTransportContact;)V
/*      */     //   210: aload_0
/*      */     //   211: aload 8
/*      */     //   213: putfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   216: aload_0
/*      */     //   217: aload_0
/*      */     //   218: aload_0
/*      */     //   219: getfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   222: invokevirtual 852	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:buildKeyBlockDetails	(Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;)[Lcom/aelitis/azureus/core/dht/DHTStorageBlock;
/*      */     //   225: putfield 817	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_blocks_direct_cow	[Lcom/aelitis/azureus/core/dht/DHTStorageBlock;
/*      */     //   228: aload_0
/*      */     //   229: invokevirtual 840	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:writeKeyBlocks	()V
/*      */     //   232: aload_0
/*      */     //   233: getfield 827	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   236: invokevirtual 942	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   239: aload 7
/*      */     //   241: areturn
/*      */     //   242: aload 4
/*      */     //   244: invokevirtual 873	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:isAdd	()Z
/*      */     //   247: ifeq +174 -> 421
/*      */     //   250: aload 6
/*      */     //   252: ifnull +11 -> 263
/*      */     //   255: aload 6
/*      */     //   257: invokevirtual 873	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:isAdd	()Z
/*      */     //   260: ifne +88 -> 348
/*      */     //   263: aload_0
/*      */     //   264: aload 4
/*      */     //   266: aload_1
/*      */     //   267: invokevirtual 854	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:verifyKeyBlock	(Lcom/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock;Lcom/aelitis/azureus/core/dht/transport/DHTTransportContact;)Z
/*      */     //   270: ifne +75 -> 345
/*      */     //   273: aconst_null
/*      */     //   274: astore 7
/*      */     //   276: iload 5
/*      */     //   278: ifeq +57 -> 335
/*      */     //   281: aload_0
/*      */     //   282: getfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   285: invokevirtual 949	org/gudy/azureus2/core3/util/ByteArrayHashMap:duplicate	()Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   288: astore 8
/*      */     //   290: aload 8
/*      */     //   292: aload 4
/*      */     //   294: invokevirtual 876	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:getKey	()[B
/*      */     //   297: aload 4
/*      */     //   299: invokevirtual 950	org/gudy/azureus2/core3/util/ByteArrayHashMap:put	([BLjava/lang/Object;)Ljava/lang/Object;
/*      */     //   302: pop
/*      */     //   303: aload_1
/*      */     //   304: ifnull +9 -> 313
/*      */     //   307: aload 4
/*      */     //   309: aload_1
/*      */     //   310: invokevirtual 879	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:sentTo	(Lcom/aelitis/azureus/core/dht/transport/DHTTransportContact;)V
/*      */     //   313: aload_0
/*      */     //   314: aload 8
/*      */     //   316: putfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   319: aload_0
/*      */     //   320: aload_0
/*      */     //   321: aload_0
/*      */     //   322: getfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   325: invokevirtual 852	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:buildKeyBlockDetails	(Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;)[Lcom/aelitis/azureus/core/dht/DHTStorageBlock;
/*      */     //   328: putfield 817	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_blocks_direct_cow	[Lcom/aelitis/azureus/core/dht/DHTStorageBlock;
/*      */     //   331: aload_0
/*      */     //   332: invokevirtual 840	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:writeKeyBlocks	()V
/*      */     //   335: aload_0
/*      */     //   336: getfield 827	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   339: invokevirtual 942	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   342: aload 7
/*      */     //   344: areturn
/*      */     //   345: iconst_1
/*      */     //   346: istore 5
/*      */     //   348: aload 4
/*      */     //   350: astore 7
/*      */     //   352: iload 5
/*      */     //   354: ifeq +57 -> 411
/*      */     //   357: aload_0
/*      */     //   358: getfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   361: invokevirtual 949	org/gudy/azureus2/core3/util/ByteArrayHashMap:duplicate	()Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   364: astore 8
/*      */     //   366: aload 8
/*      */     //   368: aload 4
/*      */     //   370: invokevirtual 876	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:getKey	()[B
/*      */     //   373: aload 4
/*      */     //   375: invokevirtual 950	org/gudy/azureus2/core3/util/ByteArrayHashMap:put	([BLjava/lang/Object;)Ljava/lang/Object;
/*      */     //   378: pop
/*      */     //   379: aload_1
/*      */     //   380: ifnull +9 -> 389
/*      */     //   383: aload 4
/*      */     //   385: aload_1
/*      */     //   386: invokevirtual 879	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:sentTo	(Lcom/aelitis/azureus/core/dht/transport/DHTTransportContact;)V
/*      */     //   389: aload_0
/*      */     //   390: aload 8
/*      */     //   392: putfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   395: aload_0
/*      */     //   396: aload_0
/*      */     //   397: aload_0
/*      */     //   398: getfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   401: invokevirtual 852	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:buildKeyBlockDetails	(Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;)[Lcom/aelitis/azureus/core/dht/DHTStorageBlock;
/*      */     //   404: putfield 817	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_blocks_direct_cow	[Lcom/aelitis/azureus/core/dht/DHTStorageBlock;
/*      */     //   407: aload_0
/*      */     //   408: invokevirtual 840	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:writeKeyBlocks	()V
/*      */     //   411: aload_0
/*      */     //   412: getfield 827	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   415: invokevirtual 942	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   418: aload 7
/*      */     //   420: areturn
/*      */     //   421: aload 4
/*      */     //   423: invokevirtual 874	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:isDirect	()Z
/*      */     //   426: ifeq +101 -> 527
/*      */     //   429: aload 6
/*      */     //   431: ifnull +11 -> 442
/*      */     //   434: aload 6
/*      */     //   436: invokevirtual 873	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:isAdd	()Z
/*      */     //   439: ifeq +88 -> 527
/*      */     //   442: aload_0
/*      */     //   443: aload 4
/*      */     //   445: aload_1
/*      */     //   446: invokevirtual 854	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:verifyKeyBlock	(Lcom/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock;Lcom/aelitis/azureus/core/dht/transport/DHTTransportContact;)Z
/*      */     //   449: ifne +75 -> 524
/*      */     //   452: aconst_null
/*      */     //   453: astore 7
/*      */     //   455: iload 5
/*      */     //   457: ifeq +57 -> 514
/*      */     //   460: aload_0
/*      */     //   461: getfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   464: invokevirtual 949	org/gudy/azureus2/core3/util/ByteArrayHashMap:duplicate	()Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   467: astore 8
/*      */     //   469: aload 8
/*      */     //   471: aload 4
/*      */     //   473: invokevirtual 876	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:getKey	()[B
/*      */     //   476: aload 4
/*      */     //   478: invokevirtual 950	org/gudy/azureus2/core3/util/ByteArrayHashMap:put	([BLjava/lang/Object;)Ljava/lang/Object;
/*      */     //   481: pop
/*      */     //   482: aload_1
/*      */     //   483: ifnull +9 -> 492
/*      */     //   486: aload 4
/*      */     //   488: aload_1
/*      */     //   489: invokevirtual 879	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:sentTo	(Lcom/aelitis/azureus/core/dht/transport/DHTTransportContact;)V
/*      */     //   492: aload_0
/*      */     //   493: aload 8
/*      */     //   495: putfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   498: aload_0
/*      */     //   499: aload_0
/*      */     //   500: aload_0
/*      */     //   501: getfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   504: invokevirtual 852	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:buildKeyBlockDetails	(Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;)[Lcom/aelitis/azureus/core/dht/DHTStorageBlock;
/*      */     //   507: putfield 817	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_blocks_direct_cow	[Lcom/aelitis/azureus/core/dht/DHTStorageBlock;
/*      */     //   510: aload_0
/*      */     //   511: invokevirtual 840	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:writeKeyBlocks	()V
/*      */     //   514: aload_0
/*      */     //   515: getfield 827	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   518: invokevirtual 942	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   521: aload 7
/*      */     //   523: areturn
/*      */     //   524: iconst_1
/*      */     //   525: istore 5
/*      */     //   527: aconst_null
/*      */     //   528: astore 7
/*      */     //   530: iload 5
/*      */     //   532: ifeq +57 -> 589
/*      */     //   535: aload_0
/*      */     //   536: getfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   539: invokevirtual 949	org/gudy/azureus2/core3/util/ByteArrayHashMap:duplicate	()Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   542: astore 8
/*      */     //   544: aload 8
/*      */     //   546: aload 4
/*      */     //   548: invokevirtual 876	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:getKey	()[B
/*      */     //   551: aload 4
/*      */     //   553: invokevirtual 950	org/gudy/azureus2/core3/util/ByteArrayHashMap:put	([BLjava/lang/Object;)Ljava/lang/Object;
/*      */     //   556: pop
/*      */     //   557: aload_1
/*      */     //   558: ifnull +9 -> 567
/*      */     //   561: aload 4
/*      */     //   563: aload_1
/*      */     //   564: invokevirtual 879	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:sentTo	(Lcom/aelitis/azureus/core/dht/transport/DHTTransportContact;)V
/*      */     //   567: aload_0
/*      */     //   568: aload 8
/*      */     //   570: putfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   573: aload_0
/*      */     //   574: aload_0
/*      */     //   575: aload_0
/*      */     //   576: getfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   579: invokevirtual 852	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:buildKeyBlockDetails	(Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;)[Lcom/aelitis/azureus/core/dht/DHTStorageBlock;
/*      */     //   582: putfield 817	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_blocks_direct_cow	[Lcom/aelitis/azureus/core/dht/DHTStorageBlock;
/*      */     //   585: aload_0
/*      */     //   586: invokevirtual 840	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:writeKeyBlocks	()V
/*      */     //   589: aload_0
/*      */     //   590: getfield 827	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   593: invokevirtual 942	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   596: aload 7
/*      */     //   598: areturn
/*      */     //   599: astore 9
/*      */     //   601: iload 5
/*      */     //   603: ifeq +57 -> 660
/*      */     //   606: aload_0
/*      */     //   607: getfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   610: invokevirtual 949	org/gudy/azureus2/core3/util/ByteArrayHashMap:duplicate	()Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   613: astore 10
/*      */     //   615: aload 10
/*      */     //   617: aload 4
/*      */     //   619: invokevirtual 876	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:getKey	()[B
/*      */     //   622: aload 4
/*      */     //   624: invokevirtual 950	org/gudy/azureus2/core3/util/ByteArrayHashMap:put	([BLjava/lang/Object;)Ljava/lang/Object;
/*      */     //   627: pop
/*      */     //   628: aload_1
/*      */     //   629: ifnull +9 -> 638
/*      */     //   632: aload 4
/*      */     //   634: aload_1
/*      */     //   635: invokevirtual 879	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager$keyBlock:sentTo	(Lcom/aelitis/azureus/core/dht/transport/DHTTransportContact;)V
/*      */     //   638: aload_0
/*      */     //   639: aload 10
/*      */     //   641: putfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   644: aload_0
/*      */     //   645: aload_0
/*      */     //   646: aload_0
/*      */     //   647: getfield 830	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_map_cow	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   650: invokevirtual 852	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:buildKeyBlockDetails	(Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;)[Lcom/aelitis/azureus/core/dht/DHTStorageBlock;
/*      */     //   653: putfield 817	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_blocks_direct_cow	[Lcom/aelitis/azureus/core/dht/DHTStorageBlock;
/*      */     //   656: aload_0
/*      */     //   657: invokevirtual 840	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:writeKeyBlocks	()V
/*      */     //   660: aload 9
/*      */     //   662: athrow
/*      */     //   663: astore 11
/*      */     //   665: aload_0
/*      */     //   666: getfield 827	com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager:key_block_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   669: invokevirtual 942	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   672: aload 11
/*      */     //   674: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1419	-> byte code offset #0
/*      */     //   Java source line #1421	-> byte code offset #7
/*      */     //   Java source line #1424	-> byte code offset #9
/*      */     //   Java source line #1428	-> byte code offset #37
/*      */     //   Java source line #1430	-> byte code offset #44
/*      */     //   Java source line #1433	-> byte code offset #47
/*      */     //   Java source line #1435	-> byte code offset #64
/*      */     //   Java source line #1440	-> byte code offset #69
/*      */     //   Java source line #1442	-> byte code offset #85
/*      */     //   Java source line #1485	-> byte code offset #88
/*      */     //   Java source line #1487	-> byte code offset #93
/*      */     //   Java source line #1489	-> byte code offset #102
/*      */     //   Java source line #1494	-> byte code offset #115
/*      */     //   Java source line #1496	-> byte code offset #119
/*      */     //   Java source line #1499	-> byte code offset #125
/*      */     //   Java source line #1500	-> byte code offset #131
/*      */     //   Java source line #1502	-> byte code offset #143
/*      */     //   Java source line #1507	-> byte code offset #147
/*      */     //   Java source line #1447	-> byte code offset #157
/*      */     //   Java source line #1449	-> byte code offset #170
/*      */     //   Java source line #1485	-> byte code offset #173
/*      */     //   Java source line #1487	-> byte code offset #178
/*      */     //   Java source line #1489	-> byte code offset #187
/*      */     //   Java source line #1494	-> byte code offset #200
/*      */     //   Java source line #1496	-> byte code offset #204
/*      */     //   Java source line #1499	-> byte code offset #210
/*      */     //   Java source line #1500	-> byte code offset #216
/*      */     //   Java source line #1502	-> byte code offset #228
/*      */     //   Java source line #1507	-> byte code offset #232
/*      */     //   Java source line #1453	-> byte code offset #242
/*      */     //   Java source line #1455	-> byte code offset #250
/*      */     //   Java source line #1457	-> byte code offset #263
/*      */     //   Java source line #1459	-> byte code offset #273
/*      */     //   Java source line #1485	-> byte code offset #276
/*      */     //   Java source line #1487	-> byte code offset #281
/*      */     //   Java source line #1489	-> byte code offset #290
/*      */     //   Java source line #1494	-> byte code offset #303
/*      */     //   Java source line #1496	-> byte code offset #307
/*      */     //   Java source line #1499	-> byte code offset #313
/*      */     //   Java source line #1500	-> byte code offset #319
/*      */     //   Java source line #1502	-> byte code offset #331
/*      */     //   Java source line #1507	-> byte code offset #335
/*      */     //   Java source line #1462	-> byte code offset #345
/*      */     //   Java source line #1465	-> byte code offset #348
/*      */     //   Java source line #1485	-> byte code offset #352
/*      */     //   Java source line #1487	-> byte code offset #357
/*      */     //   Java source line #1489	-> byte code offset #366
/*      */     //   Java source line #1494	-> byte code offset #379
/*      */     //   Java source line #1496	-> byte code offset #383
/*      */     //   Java source line #1499	-> byte code offset #389
/*      */     //   Java source line #1500	-> byte code offset #395
/*      */     //   Java source line #1502	-> byte code offset #407
/*      */     //   Java source line #1507	-> byte code offset #411
/*      */     //   Java source line #1471	-> byte code offset #421
/*      */     //   Java source line #1473	-> byte code offset #442
/*      */     //   Java source line #1475	-> byte code offset #452
/*      */     //   Java source line #1485	-> byte code offset #455
/*      */     //   Java source line #1487	-> byte code offset #460
/*      */     //   Java source line #1489	-> byte code offset #469
/*      */     //   Java source line #1494	-> byte code offset #482
/*      */     //   Java source line #1496	-> byte code offset #486
/*      */     //   Java source line #1499	-> byte code offset #492
/*      */     //   Java source line #1500	-> byte code offset #498
/*      */     //   Java source line #1502	-> byte code offset #510
/*      */     //   Java source line #1507	-> byte code offset #514
/*      */     //   Java source line #1478	-> byte code offset #524
/*      */     //   Java source line #1481	-> byte code offset #527
/*      */     //   Java source line #1485	-> byte code offset #530
/*      */     //   Java source line #1487	-> byte code offset #535
/*      */     //   Java source line #1489	-> byte code offset #544
/*      */     //   Java source line #1494	-> byte code offset #557
/*      */     //   Java source line #1496	-> byte code offset #561
/*      */     //   Java source line #1499	-> byte code offset #567
/*      */     //   Java source line #1500	-> byte code offset #573
/*      */     //   Java source line #1502	-> byte code offset #585
/*      */     //   Java source line #1507	-> byte code offset #589
/*      */     //   Java source line #1485	-> byte code offset #599
/*      */     //   Java source line #1487	-> byte code offset #606
/*      */     //   Java source line #1489	-> byte code offset #615
/*      */     //   Java source line #1494	-> byte code offset #628
/*      */     //   Java source line #1496	-> byte code offset #632
/*      */     //   Java source line #1499	-> byte code offset #638
/*      */     //   Java source line #1500	-> byte code offset #644
/*      */     //   Java source line #1502	-> byte code offset #656
/*      */     //   Java source line #1503	-> byte code offset #660
/*      */     //   Java source line #1507	-> byte code offset #663
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	675	0	this	DHTPluginStorageManager
/*      */     //   0	675	1	originating_contact	DHTTransportContact
/*      */     //   0	675	2	request	byte[]
/*      */     //   0	675	3	signature	byte[]
/*      */     //   35	598	4	kb	keyBlock
/*      */     //   45	557	5	add_it	boolean
/*      */     //   62	373	6	old	keyBlock
/*      */     //   86	511	7	localObject1	Object
/*      */     //   100	27	8	new_map	ByteArrayHashMap
/*      */     //   185	27	8	new_map	ByteArrayHashMap
/*      */     //   288	27	8	new_map	ByteArrayHashMap
/*      */     //   364	27	8	new_map	ByteArrayHashMap
/*      */     //   467	27	8	new_map	ByteArrayHashMap
/*      */     //   542	27	8	new_map	ByteArrayHashMap
/*      */     //   599	62	9	localObject2	Object
/*      */     //   613	27	10	new_map	ByteArrayHashMap
/*      */     //   663	10	11	localObject3	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   47	88	599	finally
/*      */     //   157	173	599	finally
/*      */     //   242	276	599	finally
/*      */     //   345	352	599	finally
/*      */     //   421	455	599	finally
/*      */     //   524	530	599	finally
/*      */     //   599	601	599	finally
/*      */     //   37	147	663	finally
/*      */     //   157	232	663	finally
/*      */     //   242	335	663	finally
/*      */     //   345	411	663	finally
/*      */     //   421	514	663	finally
/*      */     //   524	589	663	finally
/*      */     //   599	665	663	finally
/*      */   }
/*      */   
/*      */   protected static class storageKey
/*      */     implements DHTStorageKey
/*      */   {
/*      */     private DHTPluginStorageManager manager;
/*      */     private HashWrapper key;
/*      */     private byte type;
/*      */     private int size;
/*      */     private int entries;
/*      */     private long expiry;
/*      */     private long read_count_start;
/*      */     private short reads_per_min;
/*      */     private BloomFilter ip_bloom_filter;
/*      */     
/*      */     protected storageKey(DHTPluginStorageManager _manager, byte _type, HashWrapper _key)
/*      */     {
/* 2117 */       this.manager = _manager;
/* 2118 */       this.type = _type;
/* 2119 */       this.key = _key;
/*      */       
/* 2121 */       this.expiry = (SystemTime.getCurrentTime() + 172800000L + RandomUtils.nextLong(86400000L));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected storageKey(DHTPluginStorageManager _manager, byte _type, HashWrapper _key, long _expiry)
/*      */     {
/* 2131 */       this.manager = _manager;
/* 2132 */       this.type = _type;
/* 2133 */       this.key = _key;
/* 2134 */       this.expiry = _expiry;
/*      */     }
/*      */     
/*      */ 
/*      */     protected Map serialise()
/*      */     {
/* 2140 */       Map map = new HashMap();
/*      */       
/* 2142 */       map.put("key", this.key.getBytes());
/* 2143 */       map.put("type", new Long(this.type));
/* 2144 */       map.put("exp", new Long(this.expiry));
/*      */       
/* 2146 */       this.manager.log.log("SM: serialised sk: " + DHTLog.getString2(this.key.getBytes()) + ", " + DHT.DT_STRINGS[this.type] + ", " + DHTPluginStorageManager.formatExpiry(this.expiry));
/*      */       
/* 2148 */       return map;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected static storageKey deserialise(DHTPluginStorageManager _manager, Map map)
/*      */     {
/* 2156 */       HashWrapper key = new HashWrapper((byte[])map.get("key"));
/* 2157 */       int type = ((Long)map.get("type")).intValue();
/* 2158 */       long exp = ((Long)map.get("exp")).longValue();
/*      */       
/* 2160 */       _manager.log.log("SM: deserialised sk: " + DHTLog.getString2(key.getBytes()) + ", " + DHT.DT_STRINGS[type] + ", " + DHTPluginStorageManager.formatExpiry(exp));
/*      */       
/* 2162 */       return new storageKey(_manager, (byte)type, key, exp);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void serialiseStats(DataOutputStream dos)
/*      */       throws IOException
/*      */     {
/* 2171 */       this.manager.serialiseStats(this, dos);
/*      */     }
/*      */     
/*      */ 
/*      */     protected HashWrapper getKey()
/*      */     {
/* 2177 */       return this.key;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getExpiry()
/*      */     {
/* 2183 */       return this.expiry;
/*      */     }
/*      */     
/*      */ 
/*      */     public byte getDiversificationType()
/*      */     {
/* 2189 */       if (this.type != 1)
/*      */       {
/*      */ 
/*      */ 
/* 2193 */         if (this.expiry < SystemTime.getCurrentTime())
/*      */         {
/* 2195 */           this.type = 1;
/*      */           
/* 2197 */           this.manager.log.log("SM: sk: " + DHTLog.getString2(getKey().getBytes()) + " expired");
/*      */           
/* 2199 */           this.manager.writeDiversifications();
/*      */         }
/*      */       }
/*      */       
/* 2203 */       return this.type;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getReadsPerMinute()
/*      */     {
/* 2209 */       return this.reads_per_min;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getSize()
/*      */     {
/* 2215 */       return this.size;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getEntryCount()
/*      */     {
/* 2221 */       return this.entries;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void read(DHTTransportContact contact)
/*      */     {
/* 2230 */       if (this.type == 1)
/*      */       {
/* 2232 */         long now = SystemTime.getCurrentTime();
/*      */         
/* 2234 */         long diff = now - this.read_count_start;
/*      */         
/* 2236 */         if (diff > 180000L)
/*      */         {
/* 2238 */           if (this.ip_bloom_filter != null)
/*      */           {
/* 2240 */             int ip_entries = this.ip_bloom_filter.getEntryCount();
/*      */             
/* 2242 */             this.reads_per_min = ((short)(ip_entries / 3));
/*      */             
/* 2244 */             if ((this.reads_per_min == 0) && (ip_entries > 0))
/*      */             {
/*      */ 
/*      */ 
/* 2248 */               this.reads_per_min = 1;
/*      */             }
/*      */             
/* 2251 */             if (ip_entries > 90)
/*      */             {
/* 2253 */               if (!this.manager.suspendDivs())
/*      */               {
/* 2255 */                 this.type = 2;
/*      */                 
/* 2257 */                 this.manager.log.log("SM: sk freq created (" + ip_entries + "reads ) - " + DHTLog.getString2(this.key.getBytes()));
/*      */                 
/* 2259 */                 this.manager.writeDiversifications();
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 2264 */           this.read_count_start = now;
/*      */           
/* 2266 */           this.ip_bloom_filter = null;
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/* 2272 */           if (this.ip_bloom_filter == null)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2278 */             this.ip_bloom_filter = BloomFilterFactory.createAddOnly(300);
/*      */           }
/*      */           
/*      */ 
/* 2282 */           byte[] bloom_key = contact.getBloomKey();
/*      */           
/* 2284 */           this.ip_bloom_filter.add(bloom_key);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void valueChanged(int entries_diff, int size_diff)
/*      */     {
/* 2294 */       this.entries += entries_diff;
/* 2295 */       this.size += size_diff;
/*      */       
/* 2297 */       if (this.entries < 0) {
/* 2298 */         Debug.out("entries negative");
/* 2299 */         this.entries = 0;
/*      */       }
/*      */       
/* 2302 */       if (this.size < 0) {
/* 2303 */         Debug.out("size negative");
/* 2304 */         this.size = 0;
/*      */       }
/*      */       
/* 2307 */       if (this.type == 1)
/*      */       {
/* 2309 */         if (!this.manager.suspendDivs())
/*      */         {
/* 2311 */           if (this.size > 32768)
/*      */           {
/* 2313 */             this.type = 3;
/*      */             
/* 2315 */             this.manager.log.log("SM: sk size total created (size " + this.size + ") - " + DHTLog.getString2(this.key.getBytes()));
/*      */             
/* 2317 */             this.manager.writeDiversifications();
/*      */           }
/* 2319 */           else if (this.entries > 2048)
/*      */           {
/* 2321 */             this.type = 3;
/*      */             
/* 2323 */             this.manager.log.log("SM: sk size entries created (" + this.entries + " entries) - " + DHTLog.getString2(this.key.getBytes()));
/*      */             
/* 2325 */             this.manager.writeDiversifications();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/dht/impl/DHTPluginStorageManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */