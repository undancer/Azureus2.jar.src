/*      */ package com.aelitis.azureus.core.devices.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.devices.Device;
/*      */ import com.aelitis.azureus.core.devices.Device.browseLocation;
/*      */ import com.aelitis.azureus.core.devices.DeviceListener;
/*      */ import com.aelitis.azureus.core.devices.TranscodeException;
/*      */ import com.aelitis.azureus.core.devices.TranscodeFile;
/*      */ import com.aelitis.azureus.core.devices.TranscodeProfile;
/*      */ import com.aelitis.azureus.core.devices.TranscodeProvider;
/*      */ import com.aelitis.azureus.core.devices.TranscodeTargetListener;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*      */ import com.aelitis.azureus.util.ImportExportUtils;
/*      */ import com.aelitis.azureus.util.StringCompareUtils;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.net.URL;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.LightHashMap;
/*      */ import org.gudy.azureus2.core3.util.SystemProperties;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class DeviceImpl
/*      */   implements Device
/*      */ {
/*      */   private static final String MY_PACKAGE = "com.aelitis.azureus.core.devices.impl";
/*   57 */   private static final TranscodeProfile blank_profile = new TranscodeProfile()
/*      */   {
/*      */ 
/*      */     public String getUID()
/*      */     {
/*      */ 
/*   63 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getName()
/*      */     {
/*   69 */       return "blank";
/*      */     }
/*      */     
/*      */ 
/*      */     public String getDescription()
/*      */     {
/*   75 */       return "blank";
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isStreamable()
/*      */     {
/*   81 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getIconURL()
/*      */     {
/*   87 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getIconIndex()
/*      */     {
/*   93 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getFileExtension()
/*      */     {
/*   99 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getDeviceClassification()
/*      */     {
/*  105 */       return "blank";
/*      */     }
/*      */     
/*      */ 
/*      */     public TranscodeProvider getProvider()
/*      */     {
/*  111 */       return null;
/*      */     }
/*      */   };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static DeviceImpl importFromBEncodedMapStatic(DeviceManagerImpl manager, Map map)
/*      */     throws IOException
/*      */   {
/*  122 */     String impl = ImportExportUtils.importString(map, "_impl");
/*      */     
/*  124 */     if (impl.startsWith("."))
/*      */     {
/*  126 */       impl = "com.aelitis.azureus.core.devices.impl" + impl;
/*      */     }
/*      */     try
/*      */     {
/*  130 */       Class<DeviceImpl> cla = Class.forName(impl);
/*      */       
/*  132 */       Constructor<DeviceImpl> cons = cla.getDeclaredConstructor(new Class[] { DeviceManagerImpl.class, Map.class });
/*      */       
/*  134 */       cons.setAccessible(true);
/*      */       
/*  136 */       return (DeviceImpl)cons.newInstance(new Object[] { manager, map });
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  140 */       Debug.out("Can't construct device for " + impl, e);
/*      */       
/*  142 */       throw new IOException("Construction failed: " + Debug.getNestedExceptionMessage(e));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*  147 */   private static List<Pattern> device_renames = new ArrayList();
/*      */   private static final String PP_REND_WORK_DIR = "tt_work_dir";
/*      */   
/*      */   static {
/*  151 */     try { device_renames.add(Pattern.compile("TV\\s*+\\(([^\\)]*)\\)", 2));
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  156 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static String modifyDeviceDisplayName(String name)
/*      */   {
/*  164 */     for (Pattern p : device_renames)
/*      */     {
/*  166 */       Matcher m = p.matcher(name);
/*      */       
/*  168 */       if (m.find())
/*      */       {
/*  170 */         String new_name = m.group(1);
/*      */         
/*  172 */         return new_name;
/*      */       }
/*      */     }
/*      */     
/*  176 */     if (name.startsWith("WDTVLIVE"))
/*      */     {
/*  178 */       name = "WD TV Live";
/*      */     }
/*      */     
/*  181 */     return name;
/*      */   }
/*      */   
/*      */ 
/*      */   private static final String PP_REND_DEF_TRANS_PROF = "tt_def_trans_prof";
/*      */   
/*      */   private static final String PP_REND_TRANS_REQ = "tt_req";
/*      */   
/*      */   private static final String PP_REND_TRANS_CACHE = "tt_always_cache";
/*      */   
/*      */   private static final String PP_REND_RSS_PUB = "tt_rss_pub";
/*      */   
/*      */   private static final String PP_REND_TAG_SHARE = "tt_tag_share";
/*      */   
/*      */   protected static final String PP_REND_SHOW_CAT = "tt_show_cat";
/*      */   
/*      */   protected static final String PP_REND_CLASSIFICATION = "tt_rend_class";
/*      */   
/*      */   protected static final String PP_IP_ADDRESS = "rend_ip";
/*      */   
/*      */   protected static final String PP_DONT_AUTO_HIDE = "rend_no_ah";
/*      */   
/*      */   protected static final String TP_IP_ADDRESS = "DeviceUPnPImpl:ip";
/*      */   
/*      */   protected static final String PP_FILTER_FILES = "rend_filter";
/*      */   
/*      */   protected static final String PP_RESTRICT_ACCESS = "restrict_access";
/*      */   
/*      */   protected static final String PP_COPY_OUTSTANDING = "copy_outstanding";
/*      */   
/*      */   protected static final String PP_AUTO_START = "auto_start";
/*      */   protected static final String PP_COPY_TO_FOLDER = "copy_to_folder";
/*      */   protected static final String PP_AUTO_COPY = "auto_copy";
/*      */   protected static final String PP_EXPORTABLE = "exportable";
/*      */   protected static final String PP_LIVENESS_DETECTABLE = "live_det";
/*      */   protected static final String PP_TIVO_MACHINE = "tivo_machine";
/*      */   protected static final String PP_OD_ENABLED = "od_enabled";
/*      */   protected static final String PP_OD_SHOWN_FTUX = "od_shown_ftux";
/*      */   protected static final String PP_OD_MANUFACTURER = "od_manufacturer";
/*      */   protected static final String PP_OD_STATE_CACHE = "od_state_cache";
/*      */   protected static final String PP_OD_XFER_CACHE = "od_xfer_cache";
/*      */   protected static final String PP_OD_UPNP_DISC_CACHE = "od_upnp_cache";
/*      */   protected static final boolean PR_AUTO_START_DEFAULT = true;
/*      */   protected static final boolean PP_AUTO_COPY_DEFAULT = false;
/*      */   private static final String GENERIC = "generic";
/*  226 */   private static final Object KEY_FILE_ALLOC_ERROR = new Object();
/*      */   
/*      */   private DeviceManagerImpl manager;
/*      */   
/*      */   private int type;
/*      */   private String uid;
/*      */   private String secondary_uid;
/*      */   private String classification;
/*      */   private String name;
/*      */   private boolean manual;
/*      */   private boolean hidden;
/*      */   private boolean auto_hidden;
/*      */   private boolean isGenericUSB;
/*      */   private long last_seen;
/*  240 */   private boolean can_remove = true;
/*      */   
/*      */   private boolean tagged;
/*      */   
/*      */   private int busy_count;
/*      */   
/*      */   private boolean online;
/*      */   private boolean transcoding;
/*  248 */   private Map<String, Object> persistent_properties = new LightHashMap(1);
/*      */   
/*  250 */   private Map<Object, Object> transient_properties = new LightHashMap(1);
/*      */   
/*      */   private long device_files_last_mod;
/*      */   
/*      */   private boolean device_files_dirty;
/*      */   
/*      */   private Map<String, Map<String, ?>> device_files;
/*      */   private WeakReference<Map<String, Map<String, ?>>> device_files_ref;
/*  258 */   private CopyOnWriteList<TranscodeTargetListener> listeners = new CopyOnWriteList();
/*      */   
/*  260 */   private Map<Object, String> errors = new HashMap();
/*  261 */   private Map<Object, String> infos = new HashMap();
/*      */   
/*      */ 
/*      */ 
/*      */   private CopyOnWriteList<DeviceListener> device_listeners;
/*      */   
/*      */ 
/*      */   private String image_id;
/*      */   
/*      */ 
/*      */   private boolean isNameAutomatic;
/*      */   
/*      */ 
/*      */ 
/*      */   protected DeviceImpl(DeviceManagerImpl _manager, int _type, String _uid, String _classification, boolean _manual)
/*      */   {
/*  277 */     this(_manager, _type, _uid, _classification, _manual, _classification);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DeviceImpl(DeviceManagerImpl _manager, int _type, String _uid, String _classification, boolean _manual, String _name)
/*      */   {
/*  289 */     this.manager = _manager;
/*  290 */     this.type = _type;
/*  291 */     this.uid = _uid;
/*  292 */     this.classification = _classification;
/*  293 */     this.name = modifyDeviceDisplayName(_name);
/*  294 */     this.manual = _manual;
/*  295 */     this.isNameAutomatic = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DeviceImpl(DeviceManagerImpl _manager, Map map)
/*      */     throws IOException
/*      */   {
/*  305 */     this.manager = _manager;
/*      */     
/*  307 */     this.type = ((int)ImportExportUtils.importLong(map, "_type"));
/*  308 */     this.uid = ImportExportUtils.importString(map, "_uid");
/*  309 */     this.classification = ImportExportUtils.importString(map, "_name");
/*  310 */     this.name = ImportExportUtils.importString(map, "_lname");
/*  311 */     this.isNameAutomatic = ImportExportUtils.importBoolean(map, "_autoname", true);
/*  312 */     this.image_id = ImportExportUtils.importString(map, "_image_id");
/*      */     
/*  314 */     if (this.name == null)
/*      */     {
/*  316 */       this.name = this.classification;
/*      */     }
/*      */     
/*  319 */     if (ImportExportUtils.importLong(map, "_rn", 0L) == 0L)
/*      */     {
/*  321 */       this.name = modifyDeviceDisplayName(this.name);
/*      */     }
/*      */     
/*  324 */     this.secondary_uid = ImportExportUtils.importString(map, "_suid");
/*      */     
/*  326 */     this.last_seen = ImportExportUtils.importLong(map, "_ls");
/*  327 */     this.hidden = ImportExportUtils.importBoolean(map, "_hide");
/*  328 */     this.auto_hidden = ImportExportUtils.importBoolean(map, "_ahide");
/*  329 */     this.can_remove = ImportExportUtils.importBoolean(map, "_rm", true);
/*  330 */     this.isGenericUSB = ImportExportUtils.importBoolean(map, "_genericUSB");
/*  331 */     this.manual = ImportExportUtils.importBoolean(map, "_man");
/*  332 */     this.tagged = ImportExportUtils.importBoolean(map, "_tag", false);
/*      */     
/*  334 */     if (map.containsKey("_pprops"))
/*      */     {
/*  336 */       this.persistent_properties = ((Map)map.get("_pprops"));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void exportToBEncodedMap(Map map, boolean for_export)
/*      */     throws IOException
/*      */   {
/*  347 */     String cla = getClass().getName();
/*      */     
/*  349 */     if (cla.startsWith("com.aelitis.azureus.core.devices.impl"))
/*      */     {
/*  351 */       cla = cla.substring("com.aelitis.azureus.core.devices.impl".length());
/*      */     }
/*      */     
/*  354 */     ImportExportUtils.exportString(map, "_impl", cla);
/*  355 */     ImportExportUtils.exportLong(map, "_type", this.type);
/*  356 */     ImportExportUtils.exportString(map, "_uid", this.uid);
/*  357 */     ImportExportUtils.exportString(map, "_name", this.classification);
/*  358 */     ImportExportUtils.exportBoolean(map, "_autoname", this.isNameAutomatic);
/*  359 */     ImportExportUtils.exportLong(map, "_rn", 1L);
/*  360 */     ImportExportUtils.exportString(map, "_lname", this.name);
/*  361 */     ImportExportUtils.exportString(map, "_image_id", this.image_id);
/*      */     
/*  363 */     if (this.secondary_uid != null)
/*      */     {
/*  365 */       ImportExportUtils.exportString(map, "_suid", this.secondary_uid);
/*      */     }
/*      */     
/*  368 */     if (!for_export) {
/*  369 */       ImportExportUtils.exportLong(map, "_ls", this.last_seen);
/*  370 */       ImportExportUtils.exportBoolean(map, "_hide", this.hidden);
/*  371 */       ImportExportUtils.exportBoolean(map, "_ahide", this.auto_hidden);
/*      */     }
/*      */     
/*  374 */     ImportExportUtils.exportBoolean(map, "_rm", this.can_remove);
/*  375 */     ImportExportUtils.exportBoolean(map, "_genericUSB", this.isGenericUSB);
/*  376 */     ImportExportUtils.exportBoolean(map, "_man", this.manual);
/*      */     
/*  378 */     if (this.tagged) {
/*  379 */       ImportExportUtils.exportBoolean(map, "_tag", this.tagged);
/*      */     }
/*      */     
/*      */     Map<String, Object> pp_copy;
/*      */     
/*  384 */     synchronized (this.persistent_properties)
/*      */     {
/*  386 */       pp_copy = new HashMap(this.persistent_properties);
/*      */     }
/*      */     
/*  389 */     if (for_export)
/*      */     {
/*  391 */       pp_copy.remove("rend_ip");
/*  392 */       pp_copy.remove("copy_outstanding");
/*  393 */       pp_copy.remove("copy_to_folder");
/*  394 */       pp_copy.remove("tt_work_dir");
/*      */       
/*  396 */       map.put("_pprops", pp_copy);
/*      */     }
/*      */     else
/*      */     {
/*  400 */       map.put("_pprops", pp_copy);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean updateFrom(DeviceImpl other, boolean is_alive)
/*      */   {
/*  409 */     if (this.type != other.type)
/*      */     {
/*  411 */       Debug.out("Inconsistent update operation (type)");
/*      */       
/*  413 */       return false;
/*      */     }
/*      */     
/*  416 */     String o_uid = other.uid;
/*      */     
/*  418 */     if (!this.uid.equals(o_uid))
/*      */     {
/*  420 */       String o_suid = other.secondary_uid;
/*      */       
/*  422 */       boolean borked = false;
/*      */       
/*  424 */       if ((this.secondary_uid == null) && (o_suid == null))
/*      */       {
/*  426 */         borked = true;
/*      */       }
/*  428 */       else if (((this.secondary_uid != null) || (!this.uid.equals(o_suid))) && ((o_suid != null) || (!o_uid.equals(this.secondary_uid))) && ((o_suid == null) || (!o_suid.equals(this.secondary_uid))))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  434 */         borked = true;
/*      */       }
/*      */       
/*  437 */       if (borked)
/*      */       {
/*  439 */         Debug.out("Inconsistent update operation (uids)");
/*      */         
/*  441 */         return false;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  446 */     if (!this.classification.equals(other.classification))
/*      */     {
/*  448 */       this.classification = other.classification;
/*      */       
/*  450 */       setDirty();
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
/*      */ 
/*  462 */     if (this.manual != other.manual)
/*      */     {
/*  464 */       this.manual = other.manual;
/*      */       
/*  466 */       setDirty();
/*      */     }
/*      */     
/*  469 */     if (is_alive)
/*      */     {
/*  471 */       alive();
/*      */     }
/*      */     
/*  474 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setExportable(boolean b)
/*      */   {
/*  481 */     setPersistentBooleanProperty("exportable", b);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isExportable()
/*      */   {
/*  487 */     return getPersistentBooleanProperty("exportable", false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public VuzeFile getVuzeFile()
/*      */     throws IOException
/*      */   {
/*  495 */     return this.manager.exportVuzeFile(this);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void initialise()
/*      */   {
/*  501 */     updateStatus(0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getType()
/*      */   {
/*  512 */     return this.type;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getID()
/*      */   {
/*  518 */     return this.uid;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setSecondaryID(String str)
/*      */   {
/*  525 */     this.secondary_uid = str;
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getSecondaryID()
/*      */   {
/*  531 */     return this.secondary_uid;
/*      */   }
/*      */   
/*      */   public String getImageID() {
/*  535 */     return this.image_id;
/*      */   }
/*      */   
/*      */   public void setImageID(String id) {
/*  539 */     if (!StringCompareUtils.equals(id, this.image_id)) {
/*  540 */       this.image_id = id;
/*      */       
/*  542 */       setDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Device getDevice()
/*      */   {
/*  549 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getName()
/*      */   {
/*  555 */     return this.name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setName(String _name, boolean isAutomaticName)
/*      */   {
/*  563 */     if ((!this.name.equals(_name)) || (this.isNameAutomatic != isAutomaticName))
/*      */     {
/*  565 */       this.name = _name;
/*  566 */       this.isNameAutomatic = isAutomaticName;
/*      */       
/*  568 */       setDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isNameAutomatic()
/*      */   {
/*  575 */     return this.isNameAutomatic;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getClassification()
/*      */   {
/*  581 */     String explicit_classification = getPersistentStringProperty("tt_rend_class", null);
/*      */     
/*  583 */     if (explicit_classification != null)
/*      */     {
/*  585 */       return explicit_classification;
/*      */     }
/*      */     
/*  588 */     return this.classification;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getShortDescription()
/*      */   {
/*  594 */     if (getRendererSpecies() == 3)
/*      */     {
/*  596 */       return "iPad, iPhone, iPod, Apple TV";
/*      */     }
/*      */     
/*  599 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getRendererSpecies()
/*      */   {
/*  607 */     if (this.classification.equalsIgnoreCase("PS3"))
/*      */     {
/*  609 */       return 1;
/*      */     }
/*  611 */     if (this.classification.equalsIgnoreCase("XBox 360"))
/*      */     {
/*  613 */       return 2;
/*      */     }
/*  615 */     if (this.classification.equalsIgnoreCase("Wii"))
/*      */     {
/*  617 */       return 4;
/*      */     }
/*  619 */     if (this.classification.equalsIgnoreCase("Browser"))
/*      */     {
/*  621 */       return 5;
/*      */     }
/*      */     
/*      */ 
/*  625 */     return 6;
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
/*      */   protected String getDeviceClassification()
/*      */   {
/*  642 */     switch (getRendererSpecies())
/*      */     {
/*      */ 
/*      */     case 1: 
/*  646 */       return "sony.PS3";
/*      */     
/*      */ 
/*      */     case 2: 
/*  650 */       return "microsoft.XBox";
/*      */     
/*      */ 
/*      */     case 4: 
/*  654 */       return "nintendo.Wii";
/*      */     
/*      */ 
/*      */     case 5: 
/*  658 */       return "browser.generic";
/*      */     
/*      */ 
/*      */     case 6: 
/*  662 */       if (isManual())
/*      */       {
/*  664 */         return this.classification;
/*      */       }
/*      */       
/*  667 */       if ((this.classification.equals("sony.PSP")) || (this.classification.startsWith("tivo.")))
/*      */       {
/*      */ 
/*  670 */         return this.classification;
/*      */       }
/*      */       
/*  673 */       String str = getPersistentStringProperty("tt_rend_class", null);
/*      */       
/*  675 */       if (str != null)
/*      */       {
/*  677 */         return str;
/*      */       }
/*      */       
/*  680 */       return "generic";
/*      */     }
/*      */     
/*  683 */     Debug.out("Unknown classification");
/*      */     
/*  685 */     return "generic";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isNonSimple()
/*      */   {
/*  695 */     return (getClassification().startsWith("ms_wmp.")) || (isGenericUSB());
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isManual()
/*      */   {
/*  701 */     return this.manual;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isHidden()
/*      */   {
/*  707 */     return this.hidden;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setHidden(boolean h)
/*      */   {
/*  714 */     if (h != this.hidden)
/*      */     {
/*  716 */       this.hidden = h;
/*      */       
/*  718 */       setDirty();
/*      */     }
/*      */     
/*  721 */     if (this.auto_hidden)
/*      */     {
/*  723 */       this.auto_hidden = false;
/*      */       
/*  725 */       setDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isAutoHidden()
/*      */   {
/*  732 */     return this.auto_hidden;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAutoHidden(boolean h)
/*      */   {
/*  739 */     if (h != this.auto_hidden)
/*      */     {
/*  741 */       this.auto_hidden = h;
/*      */       
/*  743 */       setDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isTagged()
/*      */   {
/*  750 */     return this.tagged;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagged(boolean t)
/*      */   {
/*  757 */     if (t != this.tagged)
/*      */     {
/*  759 */       this.tagged = t;
/*      */       
/*  761 */       setDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isGenericUSB()
/*      */   {
/*  768 */     return this.isGenericUSB;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setGenericUSB(boolean is)
/*      */   {
/*  775 */     if (is != this.isGenericUSB)
/*      */     {
/*  777 */       this.isGenericUSB = is;
/*      */       
/*  779 */       setDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   public long getLastSeen()
/*      */   {
/*  785 */     return this.last_seen;
/*      */   }
/*      */   
/*      */ 
/*      */   public void alive()
/*      */   {
/*  791 */     this.last_seen = SystemTime.getCurrentTime();
/*      */     
/*  793 */     if (!this.online)
/*      */     {
/*  795 */       this.online = true;
/*      */       
/*  797 */       setDirty(false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isLivenessDetectable()
/*      */   {
/*  804 */     return !this.manual;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isAlive()
/*      */   {
/*  810 */     return this.online;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void dead()
/*      */   {
/*  816 */     if (this.online)
/*      */     {
/*  818 */       this.online = false;
/*      */       
/*  820 */       setDirty(false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public URL getWikiURL()
/*      */   {
/*  827 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setDirty()
/*      */   {
/*  833 */     setDirty(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setDirty(boolean save_changes)
/*      */   {
/*  840 */     this.manager.configDirty(this, save_changes);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void requestAttention()
/*      */   {
/*  852 */     this.manager.requestAttention(this);
/*      */   }
/*      */   
/*      */   public int getFileCount()
/*      */   {
/*      */     try
/*      */     {
/*  859 */       synchronized (this)
/*      */       {
/*  861 */         if (this.device_files == null)
/*      */         {
/*  863 */           loadDeviceFile();
/*      */         }
/*      */         
/*      */ 
/*  867 */         return this.device_files.size();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  875 */       return 0;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  872 */       Debug.out("Failed to load device file", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TranscodeFileImpl[] getFiles()
/*      */   {
/*      */     try
/*      */     {
/*  882 */       synchronized (this)
/*      */       {
/*  884 */         if (this.device_files == null)
/*      */         {
/*  886 */           loadDeviceFile();
/*      */         }
/*      */         
/*  889 */         List<TranscodeFile> result = new ArrayList();
/*      */         
/*  891 */         Iterator<Map.Entry<String, Map<String, ?>>> it = this.device_files.entrySet().iterator();
/*      */         
/*  893 */         while (it.hasNext())
/*      */         {
/*  895 */           Map.Entry<String, Map<String, ?>> entry = (Map.Entry)it.next();
/*      */           try
/*      */           {
/*  898 */             TranscodeFileImpl tf = new TranscodeFileImpl(this, (String)entry.getKey(), this.device_files);
/*      */             
/*  900 */             result.add(tf);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  904 */             it.remove();
/*      */             
/*  906 */             log("Failed to deserialise transcode file", e);
/*      */           }
/*      */         }
/*      */         
/*  910 */         return (TranscodeFileImpl[])result.toArray(new TranscodeFileImpl[result.size()]);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  916 */       return new TranscodeFileImpl[0];
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  914 */       Debug.out(e);
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
/*      */   public TranscodeFileImpl allocateFile(TranscodeProfile profile, boolean no_xcode, DiskManagerFileInfo file, boolean for_job)
/*      */     throws TranscodeException
/*      */   {
/*  929 */     TranscodeFileImpl result = null;
/*      */     
/*  931 */     setError(KEY_FILE_ALLOC_ERROR, null);
/*      */     try
/*      */     {
/*  934 */       synchronized (this)
/*      */       {
/*  936 */         if (this.device_files == null)
/*      */         {
/*  938 */           loadDeviceFile();
/*      */         }
/*      */         
/*  941 */         String key = ByteFormatter.encodeString(file.getDownloadHash()) + ":" + file.getIndex() + ":" + profile.getUID();
/*      */         
/*  943 */         if (this.device_files.containsKey(key)) {
/*      */           try
/*      */           {
/*  946 */             result = new TranscodeFileImpl(this, key, this.device_files);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  950 */             this.device_files.remove(key);
/*      */             
/*  952 */             log("Failed to deserialise transcode file", e);
/*      */           }
/*      */         }
/*      */         
/*  956 */         if (result == null)
/*      */         {
/*  958 */           String ext = profile.getFileExtension();
/*      */           
/*  960 */           String target_file = file.getFile(true).getName();
/*      */           
/*  962 */           if ((ext != null) && (!no_xcode))
/*      */           {
/*  964 */             int pos = target_file.lastIndexOf('.');
/*      */             
/*  966 */             if (pos != -1)
/*      */             {
/*  968 */               target_file = target_file.substring(0, pos);
/*      */             }
/*      */             
/*  971 */             target_file = target_file + ext;
/*      */           }
/*      */           
/*  974 */           target_file = allocateUniqueFileName(target_file);
/*      */           
/*  976 */           File output_file = getWorkingDirectory(true);
/*      */           
/*  978 */           if (!output_file.canWrite())
/*      */           {
/*  980 */             throw new TranscodeException("Can't write to transcode folder '" + output_file.getAbsolutePath() + "'");
/*      */           }
/*      */           
/*  983 */           output_file = new File(output_file.getAbsoluteFile(), target_file);
/*      */           
/*  985 */           result = new TranscodeFileImpl(this, key, profile.getName(), this.device_files, output_file, for_job);
/*      */           
/*  987 */           result.setSourceFile(file);
/*      */           
/*  989 */           this.device_files_last_mod = SystemTime.getMonotonousTime();
/*      */           
/*  991 */           this.device_files_dirty = true;
/*      */         }
/*      */         else
/*      */         {
/*  995 */           result.setSourceFile(file);
/*      */           
/*  997 */           result.setProfileName(profile.getName());
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1002 */       setError(KEY_FILE_ALLOC_ERROR, Debug.getNestedExceptionMessage(e));
/*      */       
/* 1004 */       throw new TranscodeException("File allocation failed", e);
/*      */     }
/*      */     
/* 1007 */     for (TranscodeTargetListener l : this.listeners) {
/*      */       try
/*      */       {
/* 1010 */         l.fileAdded(result);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1014 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/* 1018 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String allocateUniqueFileName(String str)
/*      */   {
/* 1025 */     Set<String> name_set = new HashSet();
/*      */     
/* 1027 */     for (Map<String, ?> entry : this.device_files.values()) {
/*      */       try
/*      */       {
/* 1030 */         name_set.add(new File(ImportExportUtils.importString(entry, "file")).getName());
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1034 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/* 1038 */     for (int i = 0; i < 1024; i++)
/*      */     {
/* 1040 */       String test_name = i + "_" + str;
/*      */       
/* 1042 */       if (!name_set.contains(test_name))
/*      */       {
/* 1044 */         str = test_name;
/*      */         
/* 1046 */         break;
/*      */       }
/*      */     }
/*      */     
/* 1050 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void revertFileName(TranscodeFileImpl tf)
/*      */     throws TranscodeException
/*      */   {
/* 1059 */     File cache_file = tf.getCacheFile();
/*      */     
/* 1061 */     if (cache_file.exists())
/*      */     {
/* 1063 */       Debug.out("Cache file already allocated, can't rename");
/*      */       
/* 1065 */       return;
/*      */     }
/*      */     
/* 1068 */     File source_file = tf.getSourceFile().getFile(true);
/*      */     
/* 1070 */     String original_name = source_file.getName();
/*      */     
/* 1072 */     int pos = original_name.indexOf('.');
/*      */     
/* 1074 */     if (pos == -1)
/*      */     {
/* 1076 */       return;
/*      */     }
/*      */     
/* 1079 */     String cf_name = cache_file.getName();
/*      */     
/* 1081 */     if (cf_name.endsWith(original_name.substring(pos)))
/*      */     {
/* 1083 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 1087 */       synchronized (this)
/*      */       {
/* 1089 */         if (this.device_files == null)
/*      */         {
/* 1091 */           loadDeviceFile();
/*      */         }
/*      */         
/* 1094 */         String reverted_name = allocateUniqueFileName(original_name);
/*      */         
/* 1096 */         tf.setCacheFile(new File(cache_file.getParentFile(), reverted_name));
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1100 */       throw new TranscodeException("File name revertion failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TranscodeFileImpl lookupFile(TranscodeProfile profile, DiskManagerFileInfo file)
/*      */   {
/*      */     try
/*      */     {
/* 1110 */       synchronized (this)
/*      */       {
/* 1112 */         if (this.device_files == null)
/*      */         {
/* 1114 */           loadDeviceFile();
/*      */         }
/*      */         
/* 1117 */         String key = ByteFormatter.encodeString(file.getDownloadHash()) + ":" + file.getIndex() + ":" + profile.getUID();
/*      */         
/* 1119 */         if (this.device_files.containsKey(key)) {
/*      */           try
/*      */           {
/* 1122 */             return new TranscodeFileImpl(this, key, this.device_files);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1126 */             this.device_files.remove(key);
/*      */             
/* 1128 */             log("Failed to deserialise transcode file", e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 1135 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   protected TranscodeFileImpl getTranscodeFile(String key)
/*      */   {
/*      */     try
/*      */     {
/* 1143 */       synchronized (this)
/*      */       {
/* 1145 */         if (this.device_files == null)
/*      */         {
/* 1147 */           loadDeviceFile();
/*      */         }
/*      */         
/* 1150 */         if (this.device_files.containsKey(key))
/*      */         {
/*      */           try
/*      */           {
/* 1154 */             return new TranscodeFileImpl(this, key, this.device_files);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1158 */             this.device_files.remove(key);
/*      */             
/* 1160 */             log("Failed to deserialise transcode file", e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 1167 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public File getWorkingDirectory()
/*      */   {
/* 1173 */     return getWorkingDirectory(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public File getWorkingDirectory(boolean persist)
/*      */   {
/* 1180 */     String result = getPersistentStringProperty("tt_work_dir");
/*      */     
/* 1182 */     if (result.length() == 0)
/*      */     {
/* 1184 */       File f = this.manager.getDefaultWorkingDirectory(persist);
/*      */       
/* 1186 */       if (persist)
/*      */       {
/* 1188 */         f.mkdirs();
/*      */       }
/*      */       
/* 1191 */       String name = FileUtil.convertOSSpecificChars(getName(), true);
/*      */       
/* 1193 */       for (int i = 0; i < 1024; i++)
/*      */       {
/* 1195 */         String test_name = name + (i == 0 ? "" : new StringBuilder().append("_").append(i).toString());
/*      */         
/* 1197 */         File test_file = new File(f, test_name);
/*      */         
/* 1199 */         if (!test_file.exists())
/*      */         {
/* 1201 */           f = test_file;
/*      */           
/* 1203 */           break;
/*      */         }
/*      */       }
/*      */       
/* 1207 */       result = f.getAbsolutePath();
/*      */       
/* 1209 */       if (persist)
/*      */       {
/* 1211 */         setPersistentStringProperty("tt_work_dir", result);
/*      */       }
/*      */     }
/*      */     
/* 1215 */     File f_result = new File(result);
/*      */     
/* 1217 */     if (!f_result.exists())
/*      */     {
/* 1219 */       if (persist)
/*      */       {
/* 1221 */         f_result.mkdirs();
/*      */       }
/*      */     }
/*      */     
/* 1225 */     return f_result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setWorkingDirectory(File directory)
/*      */   {
/* 1232 */     setPersistentStringProperty("tt_work_dir", directory.getAbsolutePath());
/*      */   }
/*      */   
/*      */ 
/*      */   protected void resetWorkingDirectory()
/*      */   {
/* 1238 */     setPersistentStringProperty("tt_work_dir", "");
/*      */   }
/*      */   
/*      */ 
/*      */   public TranscodeProfile[] getTranscodeProfiles()
/*      */   {
/* 1244 */     return getTranscodeProfiles(true);
/*      */   }
/*      */   
/*      */ 
/*      */   public TranscodeProfile[] getDirectTranscodeProfiles()
/*      */   {
/* 1250 */     return getTranscodeProfiles(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TranscodeProfile[] getTranscodeProfiles(boolean walkup)
/*      */   {
/* 1257 */     String classification = getDeviceClassification();
/*      */     
/* 1259 */     TranscodeProfile[] result = getTranscodeProfiles(classification);
/*      */     
/* 1261 */     if ((!walkup) || (result.length > 0))
/*      */     {
/* 1263 */       return result;
/*      */     }
/*      */     try
/*      */     {
/* 1267 */       String[] bits = Constants.PAT_SPLIT_DOT.split(classification);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1273 */       for (int i = bits.length - 1; i >= 1; i--)
/*      */       {
/* 1275 */         String c = "";
/*      */         
/* 1277 */         for (int j = 0; j < i; j++)
/*      */         {
/* 1279 */           c = c + (c.length() == 0 ? "" : ".") + bits[j];
/*      */         }
/*      */         
/* 1282 */         c = c + (c.length() == 0 ? "" : ".") + "generic";
/*      */         
/* 1284 */         result = getTranscodeProfiles(c);
/*      */         
/* 1286 */         if (result.length > 0)
/*      */         {
/* 1288 */           return result;
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1293 */       Debug.out(e);
/*      */     }
/*      */     
/* 1296 */     return new TranscodeProfile[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private TranscodeProfile[] getTranscodeProfiles(String classification)
/*      */   {
/* 1303 */     List<TranscodeProfile> profiles = new ArrayList();
/*      */     
/* 1305 */     DeviceManagerImpl dm = getManager();
/*      */     
/* 1307 */     TranscodeProvider[] providers = dm.getProviders();
/*      */     
/* 1309 */     for (TranscodeProvider provider : providers)
/*      */     {
/* 1311 */       TranscodeProfile[] ps = provider.getProfiles(classification);
/*      */       
/* 1313 */       if (providers.length == 1)
/*      */       {
/* 1315 */         return ps;
/*      */       }
/*      */       
/* 1318 */       profiles.addAll(Arrays.asList(ps));
/*      */     }
/*      */     
/* 1321 */     return (TranscodeProfile[])profiles.toArray(new TranscodeProfile[profiles.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */   public TranscodeProfile getDefaultTranscodeProfile()
/*      */   {
/* 1327 */     String uid = getPersistentStringProperty("tt_def_trans_prof");
/*      */     
/* 1329 */     DeviceManagerImpl dm = getManager();
/*      */     
/* 1331 */     TranscodeManagerImpl tm = dm.getTranscodeManager();
/*      */     
/* 1333 */     TranscodeProfile profile = tm.getProfileFromUID(uid);
/*      */     
/* 1335 */     if (profile != null)
/*      */     {
/* 1337 */       return profile;
/*      */     }
/*      */     
/* 1340 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDefaultTranscodeProfile(TranscodeProfile profile)
/*      */   {
/* 1347 */     if (profile == null)
/*      */     {
/* 1349 */       removePersistentProperty("tt_def_trans_prof");
/*      */     }
/*      */     else
/*      */     {
/* 1353 */       setPersistentStringProperty("tt_def_trans_prof", profile.getUID());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public TranscodeProfile getBlankProfile()
/*      */   {
/* 1360 */     return blank_profile;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setTranscoding(boolean _transcoding)
/*      */   {
/* 1367 */     this.transcoding = _transcoding;
/*      */     
/* 1369 */     this.manager.deviceChanged(this, false);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isTranscoding()
/*      */   {
/* 1375 */     return this.transcoding;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTranscodeRequirement()
/*      */   {
/* 1381 */     return getPersistentIntProperty("tt_req", 2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTranscodeRequirement(int req)
/*      */   {
/* 1388 */     setPersistentIntProperty("tt_req", req);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isAudioCompatible(TranscodeFile file)
/*      */   {
/* 1395 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getAlwaysCacheFiles()
/*      */   {
/* 1401 */     return getPersistentBooleanProperty("tt_always_cache", false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAlwaysCacheFiles(boolean always_cache)
/*      */   {
/* 1408 */     setPersistentBooleanProperty("tt_always_cache", always_cache);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isRSSPublishEnabled()
/*      */   {
/* 1414 */     return getPersistentBooleanProperty("tt_rss_pub", true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setRSSPublishEnabled(boolean enabled)
/*      */   {
/* 1421 */     setPersistentBooleanProperty("tt_rss_pub", enabled);
/*      */   }
/*      */   
/*      */ 
/*      */   public long getAutoShareToTagID()
/*      */   {
/* 1427 */     return getPersistentLongProperty("tt_tag_share", -1L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAutoShareToTagID(long id)
/*      */   {
/* 1434 */     setPersistentLongProperty("tt_tag_share", id);
/*      */   }
/*      */   
/*      */ 
/*      */   public String[][] getDisplayProperties()
/*      */   {
/* 1440 */     List<String[]> dp = new ArrayList();
/*      */     
/* 1442 */     getDisplayProperties(dp);
/*      */     
/* 1444 */     String[][] res = new String[2][dp.size()];
/*      */     
/* 1446 */     int pos = 0;
/*      */     
/* 1448 */     for (String[] entry : dp)
/*      */     {
/* 1450 */       res[0][pos] = entry[0];
/* 1451 */       res[1][pos] = entry[1];
/*      */       
/* 1453 */       pos++;
/*      */     }
/*      */     
/* 1456 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void getDisplayProperties(List<String[]> dp)
/*      */   {
/* 1463 */     if (!this.name.equals(this.classification))
/*      */     {
/* 1465 */       addDP(dp, "TableColumn.header.name", this.name);
/*      */     }
/*      */     
/* 1468 */     addDP(dp, "TableColumn.header.class", getClassification().toLowerCase());
/*      */     
/* 1470 */     addDP(dp, "!UID!", getID());
/*      */     
/* 1472 */     if (!this.manual)
/*      */     {
/* 1474 */       addDP(dp, "azbuddy.ui.table.online", this.online);
/*      */       
/* 1476 */       addDP(dp, "device.lastseen", this.last_seen == 0L ? "" : new SimpleDateFormat().format(new Date(this.last_seen)));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void getTTDisplayProperties(List<String[]> dp)
/*      */   {
/* 1484 */     addDP(dp, "devices.xcode.working_dir", getWorkingDirectory(false).getAbsolutePath());
/*      */     
/* 1486 */     addDP(dp, "devices.xcode.prof_def", getDefaultTranscodeProfile());
/*      */     
/* 1488 */     addDP(dp, "devices.xcode.profs", getTranscodeProfiles());
/*      */     
/* 1490 */     int tran_req = getTranscodeRequirement();
/*      */     
/*      */     String tran_req_str;
/*      */     String tran_req_str;
/* 1494 */     if (tran_req == 3)
/*      */     {
/* 1496 */       tran_req_str = "device.xcode.always";
/*      */     } else { String tran_req_str;
/* 1498 */       if (tran_req == 1)
/*      */       {
/* 1500 */         tran_req_str = "device.xcode.never";
/*      */       }
/*      */       else {
/* 1503 */         tran_req_str = "device.xcode.whenreq";
/*      */       }
/*      */     }
/* 1506 */     addDP(dp, "device.xcode", MessageText.getString(tran_req_str));
/*      */     String key;
/* 1508 */     if (this.errors.size() > 0)
/*      */     {
/* 1510 */       key = "ManagerItem.error";
/*      */       
/* 1512 */       for (String error : this.errors.values())
/*      */       {
/* 1514 */         addDP(dp, key, error);
/*      */         
/* 1516 */         key = "";
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addDP(List<String[]> dp, String name, String value)
/*      */   {
/* 1527 */     dp.add(new String[] { name, value });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addDP(List<String[]> dp, String name, File value)
/*      */   {
/* 1536 */     dp.add(new String[] { name, value == null ? "" : value.getAbsolutePath() });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addDP(List<String[]> dp, String name, String[] values)
/*      */   {
/* 1544 */     String value = "";
/*      */     
/* 1546 */     for (String v : values)
/*      */     {
/* 1548 */       value = value + (value.length() == 0 ? "" : ",") + v;
/*      */     }
/*      */     
/* 1551 */     dp.add(new String[] { name, value });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addDP(List<String[]> dp, String name, boolean value)
/*      */   {
/* 1560 */     dp.add(new String[] { name, MessageText.getString(value ? "GeneralView.yes" : "GeneralView.no") });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addDP(List<String[]> dp, String name, TranscodeProfile value)
/*      */   {
/* 1570 */     addDP(dp, name, value == null ? "" : value.getName());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addDP(List<String[]> dp, String name, TranscodeProfile[] values)
/*      */   {
/* 1579 */     String[] names = new String[values.length];
/*      */     
/* 1581 */     for (int i = 0; i < values.length; i++)
/*      */     {
/* 1583 */       names[i] = values[i].getName();
/*      */     }
/*      */     
/* 1586 */     addDP(dp, name, names);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setCanRemove(boolean can)
/*      */   {
/* 1593 */     if (this.can_remove != can)
/*      */     {
/* 1595 */       this.can_remove = can;
/*      */       
/* 1597 */       setDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canRemove()
/*      */   {
/* 1604 */     return this.can_remove;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isBusy()
/*      */   {
/* 1610 */     if (isTranscoding())
/*      */     {
/* 1612 */       return true;
/*      */     }
/*      */     
/* 1615 */     synchronized (this)
/*      */     {
/* 1617 */       return this.busy_count > 0;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setBusy(boolean busy)
/*      */   {
/* 1625 */     boolean changed = false;
/*      */     
/* 1627 */     synchronized (this)
/*      */     {
/* 1629 */       if (busy)
/*      */       {
/* 1631 */         changed = this.busy_count++ == 0;
/*      */       }
/*      */       else
/*      */       {
/* 1635 */         changed = this.busy_count-- == 1;
/*      */       }
/*      */     }
/*      */     
/* 1639 */     if (changed)
/*      */     {
/* 1641 */       this.manager.deviceChanged(this, false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void remove()
/*      */   {
/* 1648 */     this.manager.removeDevice(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getPersistentStringProperty(String prop)
/*      */   {
/* 1655 */     return getPersistentStringProperty(prop, "");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getPersistentStringProperty(String prop, String def)
/*      */   {
/* 1663 */     synchronized (this.persistent_properties)
/*      */     {
/*      */       try {
/* 1666 */         byte[] value = (byte[])this.persistent_properties.get(prop);
/*      */         
/* 1668 */         if (value == null)
/*      */         {
/* 1670 */           return def;
/*      */         }
/*      */         
/* 1673 */         return new String(value, "UTF-8");
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1677 */         Debug.printStackTrace(e);
/*      */         
/* 1679 */         return def;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPersistentStringProperty(String prop, String value)
/*      */   {
/* 1689 */     boolean dirty = false;
/*      */     
/* 1691 */     synchronized (this.persistent_properties)
/*      */     {
/* 1693 */       String existing = getPersistentStringProperty(prop);
/*      */       
/* 1695 */       if (!existing.equals(value)) {
/*      */         try
/*      */         {
/* 1698 */           if (value == null)
/*      */           {
/* 1700 */             this.persistent_properties.remove(prop);
/*      */           }
/*      */           else
/*      */           {
/* 1704 */             this.persistent_properties.put(prop, value.getBytes("UTF-8"));
/*      */           }
/*      */           
/* 1707 */           dirty = true;
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1711 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1716 */     if (dirty)
/*      */     {
/* 1718 */       setDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> Map<String, T> getPersistentMapProperty(String prop, Map<String, T> def)
/*      */   {
/* 1727 */     synchronized (this.persistent_properties)
/*      */     {
/*      */       try {
/* 1730 */         Map<String, T> value = (Map)this.persistent_properties.get(prop);
/*      */         
/* 1732 */         if (value == null)
/*      */         {
/* 1734 */           return def;
/*      */         }
/*      */         
/* 1737 */         return value;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1741 */         Debug.printStackTrace(e);
/*      */         
/* 1743 */         return def;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> void setPersistentMapProperty(String prop, Map<String, T> value)
/*      */   {
/* 1753 */     boolean dirty = false;
/*      */     
/* 1755 */     synchronized (this.persistent_properties)
/*      */     {
/* 1757 */       Map<String, T> existing = getPersistentMapProperty(prop, null);
/*      */       
/* 1759 */       if (!BEncoder.mapsAreIdentical(value, existing)) {
/*      */         try
/*      */         {
/* 1762 */           if (value == null)
/*      */           {
/* 1764 */             this.persistent_properties.remove(prop);
/*      */           }
/*      */           else
/*      */           {
/* 1768 */             this.persistent_properties.put(prop, value);
/*      */           }
/*      */           
/* 1771 */           dirty = true;
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1775 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1780 */     if (dirty)
/*      */     {
/* 1782 */       setDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removePersistentProperty(String prop)
/*      */   {
/* 1790 */     boolean dirty = false;
/*      */     
/* 1792 */     synchronized (this.persistent_properties)
/*      */     {
/* 1794 */       String existing = getPersistentStringProperty(prop);
/*      */       
/* 1796 */       if (existing != null) {
/*      */         try
/*      */         {
/* 1799 */           this.persistent_properties.remove(prop);
/*      */           
/* 1801 */           dirty = true;
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1805 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1810 */     if (dirty)
/*      */     {
/* 1812 */       setDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   public String getError()
/*      */   {
/* 1818 */     synchronized (this.errors)
/*      */     {
/* 1820 */       if (this.errors.size() == 0)
/*      */       {
/* 1822 */         return null;
/*      */       }
/*      */       
/* 1825 */       String res = "";
/*      */       
/* 1827 */       for (String s : this.errors.values())
/*      */       {
/* 1829 */         res = res + (res.length() == 0 ? "" : "; ") + s;
/*      */       }
/*      */       
/* 1832 */       return res;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setError(Object key, String error)
/*      */   {
/* 1841 */     boolean changed = false;
/*      */     
/* 1843 */     if ((error == null) || (error.length() == 0))
/*      */     {
/* 1845 */       synchronized (this.errors)
/*      */       {
/* 1847 */         changed = this.errors.remove(key) != null;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*      */       String existing;
/* 1853 */       synchronized (this.errors)
/*      */       {
/* 1855 */         existing = (String)this.errors.put(key, error);
/*      */       }
/*      */       
/* 1858 */       changed = (existing == null) || (!existing.equals(error));
/*      */     }
/*      */     
/* 1861 */     if (changed)
/*      */     {
/* 1863 */       this.manager.deviceChanged(this, false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getInfo()
/*      */   {
/* 1870 */     synchronized (this.infos)
/*      */     {
/* 1872 */       if (this.infos.size() == 0)
/*      */       {
/* 1874 */         return null;
/*      */       }
/*      */       
/* 1877 */       String res = "";
/*      */       
/* 1879 */       for (String s : this.infos.values())
/*      */       {
/* 1881 */         res = res + (res.length() == 0 ? "" : "; ") + s;
/*      */       }
/*      */       
/* 1884 */       return res;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setInfo(Object key, String info)
/*      */   {
/* 1893 */     boolean changed = false;
/*      */     
/* 1895 */     if ((info == null) || (info.length() == 0))
/*      */     {
/* 1897 */       synchronized (this.infos)
/*      */       {
/* 1899 */         changed = this.infos.remove(key) != null;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*      */       String existing;
/* 1905 */       synchronized (this.infos)
/*      */       {
/* 1907 */         existing = (String)this.infos.put(key, info);
/*      */       }
/*      */       
/* 1910 */       changed = (existing == null) || (!existing.equals(info));
/*      */     }
/*      */     
/* 1913 */     if (changed)
/*      */     {
/* 1915 */       this.manager.deviceChanged(this, false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getStatus()
/*      */   {
/* 1922 */     if (isLivenessDetectable())
/*      */     {
/* 1924 */       if (isAlive())
/*      */       {
/* 1926 */         return MessageText.getString("device.status.online");
/*      */       }
/*      */       
/*      */ 
/* 1930 */       return MessageText.getString("device.od.error.notfound");
/*      */     }
/*      */     
/*      */ 
/* 1934 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getPersistentBooleanProperty(String prop, boolean def)
/*      */   {
/* 1942 */     return getPersistentStringProperty(prop, def ? "true" : "false").equals("true");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPersistentBooleanProperty(String prop, boolean value)
/*      */   {
/* 1950 */     setPersistentStringProperty(prop, value ? "true" : "false");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getPersistentLongProperty(String prop, long def)
/*      */   {
/* 1958 */     return Long.parseLong(getPersistentStringProperty(prop, String.valueOf(def)));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPersistentLongProperty(String prop, long value)
/*      */   {
/* 1966 */     setPersistentStringProperty(prop, String.valueOf(value));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getPersistentIntProperty(String prop, int def)
/*      */   {
/* 1974 */     return Integer.parseInt(getPersistentStringProperty(prop, String.valueOf(def)));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPersistentIntProperty(String prop, int value)
/*      */   {
/* 1982 */     setPersistentStringProperty(prop, String.valueOf(value));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String[] getPersistentStringListProperty(String prop)
/*      */   {
/* 1989 */     synchronized (this.persistent_properties)
/*      */     {
/*      */       try {
/* 1992 */         List<byte[]> values = (List)this.persistent_properties.get(prop);
/*      */         
/* 1994 */         if (values == null)
/*      */         {
/* 1996 */           return new String[0];
/*      */         }
/*      */         
/* 1999 */         String[] res = new String[values.size()];
/*      */         
/* 2001 */         int pos = 0;
/*      */         
/* 2003 */         for (byte[] value : values)
/*      */         {
/* 2005 */           res[(pos++)] = new String(value, "UTF-8");
/*      */         }
/*      */         
/* 2008 */         return res;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2012 */         Debug.printStackTrace(e);
/*      */         
/* 2014 */         return new String[0];
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPersistentStringListProperty(String prop, String[] values)
/*      */   {
/* 2024 */     boolean dirty = false;
/*      */     
/* 2026 */     synchronized (this.persistent_properties)
/*      */     {
/*      */       try {
/* 2029 */         List<byte[]> values_list = new ArrayList();
/*      */         
/* 2031 */         for (String value : values)
/*      */         {
/* 2033 */           values_list.add(value.getBytes("UTF-8"));
/*      */         }
/*      */         
/* 2036 */         this.persistent_properties.put(prop, values_list);
/*      */         
/* 2038 */         dirty = true;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2042 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 2046 */     if (dirty)
/*      */     {
/* 2048 */       setDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTransientProperty(Object key, Object value)
/*      */   {
/* 2057 */     synchronized (this.transient_properties)
/*      */     {
/* 2059 */       if (value == null)
/*      */       {
/* 2061 */         this.transient_properties.remove(key);
/*      */       }
/*      */       else
/*      */       {
/* 2065 */         this.transient_properties.put(key, value);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTransientProperty(Object key1, Object key2, Object value)
/*      */   {
/* 2086 */     synchronized (this.transient_properties)
/*      */     {
/* 2088 */       Map<Object, Object> l1 = (Map)this.transient_properties.get(key1);
/*      */       
/* 2090 */       if (l1 == null)
/*      */       {
/* 2092 */         if (value == null)
/*      */         {
/* 2094 */           return;
/*      */         }
/*      */         
/* 2097 */         l1 = new HashMap();
/*      */         
/* 2099 */         this.transient_properties.put(key1, l1);
/*      */       }
/*      */       
/* 2102 */       if (value == null)
/*      */       {
/* 2104 */         l1.remove(key2);
/*      */         
/* 2106 */         if (l1.size() == 0)
/*      */         {
/* 2108 */           this.transient_properties.remove(key1);
/*      */         }
/*      */       }
/*      */       else {
/* 2112 */         l1.put(key2, value);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object getTransientProperty(Object key1, Object key2)
/*      */   {
/* 2122 */     synchronized (this.transient_properties)
/*      */     {
/* 2124 */       Map<Object, Object> l1 = (Map)this.transient_properties.get(key1);
/*      */       
/* 2126 */       if (l1 == null)
/*      */       {
/* 2128 */         return null;
/*      */       }
/*      */       
/* 2131 */       return l1.get(key2);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void close()
/*      */   {
/* 2138 */     synchronized (this)
/*      */     {
/* 2140 */       if (this.device_files_dirty)
/*      */       {
/* 2142 */         saveDeviceFile();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void loadDeviceFile()
/*      */     throws IOException
/*      */   {
/* 2152 */     this.device_files_last_mod = SystemTime.getMonotonousTime();
/*      */     
/* 2154 */     if (this.device_files_ref != null)
/*      */     {
/* 2156 */       this.device_files = ((Map)this.device_files_ref.get());
/*      */     }
/*      */     
/* 2159 */     if (this.device_files == null)
/*      */     {
/* 2161 */       Map map = FileUtil.readResilientFile(getDeviceFile());
/*      */       
/* 2163 */       this.device_files = ((Map)map.get("files"));
/*      */       
/* 2165 */       if (this.device_files == null)
/*      */       {
/* 2167 */         this.device_files = new HashMap();
/*      */       }
/*      */       
/* 2170 */       this.device_files_ref = new WeakReference(this.device_files);
/*      */       
/* 2172 */       log("Loaded device file for " + getName() + ": files=" + this.device_files.size());
/*      */     }
/*      */     
/* 2175 */     int GC_TIME = 15000;
/*      */     
/* 2177 */     new DelayedEvent("Device:gc", 15000L, new AERunnable()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*      */ 
/* 2185 */         synchronized (DeviceImpl.this)
/*      */         {
/* 2187 */           if (SystemTime.getMonotonousTime() - DeviceImpl.this.device_files_last_mod >= 15000L)
/*      */           {
/* 2189 */             if (DeviceImpl.this.device_files_dirty)
/*      */             {
/* 2191 */               DeviceImpl.this.saveDeviceFile();
/*      */             }
/*      */             
/* 2194 */             DeviceImpl.this.device_files = null;
/*      */           }
/*      */           else
/*      */           {
/* 2198 */             new DelayedEvent("Device:gc2", 15000L, this);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected URL getStreamURL(TranscodeFileImpl file, String host)
/*      */   {
/* 2210 */     return this.manager.getStreamURL(file, host);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String getMimeType(TranscodeFileImpl file)
/*      */   {
/* 2217 */     return this.manager.getMimeType(file);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void deleteFile(TranscodeFileImpl file, boolean delete_contents, boolean remove)
/*      */     throws TranscodeException
/*      */   {
/* 2228 */     if (file.isDeleted())
/*      */     {
/* 2230 */       return;
/*      */     }
/*      */     
/* 2233 */     if (delete_contents)
/*      */     {
/* 2235 */       File f = file.getCacheFile();
/*      */       
/* 2237 */       int time = 0;
/*      */       
/* 2239 */       while ((f.exists()) && (!f.delete()))
/*      */       {
/* 2241 */         if (time > 3000)
/*      */         {
/* 2243 */           log("Failed to remove file '" + f.getAbsolutePath() + "'");
/*      */           
/* 2245 */           break;
/*      */         }
/*      */         
/*      */         try
/*      */         {
/* 2250 */           Thread.sleep(500L);
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/*      */ 
/*      */ 
/* 2256 */         time += 500;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2261 */     if (remove)
/*      */     {
/*      */ 
/*      */       try
/*      */       {
/*      */ 
/*      */ 
/* 2268 */         for (TranscodeTargetListener l : this.listeners) {
/*      */           try
/*      */           {
/* 2271 */             l.fileRemoved(file);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2275 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */         
/* 2279 */         synchronized (this)
/*      */         {
/* 2281 */           if (this.device_files == null)
/*      */           {
/* 2283 */             loadDeviceFile();
/*      */           }
/*      */           else
/*      */           {
/* 2287 */             this.device_files_last_mod = SystemTime.getMonotonousTime();
/*      */           }
/*      */           
/* 2290 */           this.device_files.remove(file.getKey());
/*      */           
/* 2292 */           this.device_files_dirty = true;
/*      */         }
/*      */         
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2298 */         throw new TranscodeException("Delete failed", e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void fileDirty(TranscodeFileImpl file, int type, Object data)
/*      */   {
/*      */     try
/*      */     {
/* 2310 */       synchronized (this)
/*      */       {
/* 2312 */         if (this.device_files == null)
/*      */         {
/* 2314 */           loadDeviceFile();
/*      */         }
/*      */         else
/*      */         {
/* 2318 */           this.device_files_last_mod = SystemTime.getMonotonousTime();
/*      */         }
/*      */       }
/*      */       
/* 2322 */       this.device_files_dirty = true;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2326 */       Debug.out("Failed to load device file", e);
/*      */     }
/*      */     
/* 2329 */     for (TranscodeTargetListener l : this.listeners) {
/*      */       try
/*      */       {
/* 2332 */         l.fileChanged(file, type, data);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2336 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void saveDeviceFile()
/*      */   {
/* 2344 */     this.device_files_dirty = false;
/*      */     try
/*      */     {
/* 2347 */       loadDeviceFile();
/*      */       
/* 2349 */       if ((this.device_files == null) || (this.device_files.size() == 0))
/*      */       {
/* 2351 */         FileUtil.deleteResilientFile(getDeviceFile());
/*      */       }
/*      */       else {
/* 2354 */         Map map = new HashMap();
/*      */         
/* 2356 */         map.put("files", this.device_files);
/*      */         
/* 2358 */         FileUtil.writeResilientFile(getDeviceFile(), map);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 2362 */       Debug.out("Failed to save device file", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected File getDeviceFile()
/*      */     throws IOException
/*      */   {
/* 2371 */     File dir = getDevicesDir();
/*      */     
/* 2373 */     return new File(dir, FileUtil.convertOSSpecificChars(getID(), false) + ".dat");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected File getDevicesDir()
/*      */     throws IOException
/*      */   {
/* 2381 */     File dir = new File(SystemProperties.getUserPath());
/*      */     
/* 2383 */     dir = new File(dir, "devices");
/*      */     
/* 2385 */     if (!dir.exists())
/*      */     {
/* 2387 */       if (!dir.mkdirs())
/*      */       {
/* 2389 */         throw new IOException("Failed to create '" + dir + "'");
/*      */       }
/*      */     }
/*      */     
/* 2393 */     return dir;
/*      */   }
/*      */   
/*      */ 
/*      */   protected DeviceManagerImpl getManager()
/*      */   {
/* 2399 */     return this.manager;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(TranscodeTargetListener listener)
/*      */   {
/* 2406 */     if (!this.listeners.contains(listener)) {
/* 2407 */       this.listeners.add(listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(TranscodeTargetListener listener)
/*      */   {
/* 2415 */     this.listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void fireChanged()
/*      */   {
/*      */     List<DeviceListener> l;
/*      */     
/* 2423 */     synchronized (this) {
/*      */       List<DeviceListener> l;
/* 2425 */       if (this.device_listeners != null)
/*      */       {
/* 2427 */         l = this.device_listeners.getList();
/*      */       }
/*      */       else
/*      */       {
/* 2431 */         return;
/*      */       }
/*      */     }
/*      */     
/* 2435 */     for (DeviceListener listener : l) {
/*      */       try
/*      */       {
/* 2438 */         listener.deviceChanged(this);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2442 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(DeviceListener listener)
/*      */   {
/* 2451 */     synchronized (this)
/*      */     {
/* 2453 */       if (this.device_listeners == null)
/*      */       {
/* 2455 */         this.device_listeners = new CopyOnWriteList();
/*      */       }
/*      */       
/* 2458 */       this.device_listeners.add(listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(DeviceListener listener)
/*      */   {
/* 2466 */     synchronized (this)
/*      */     {
/* 2468 */       if (this.device_listeners != null)
/*      */       {
/* 2470 */         this.device_listeners.remove(listener);
/*      */         
/* 2472 */         if (this.device_listeners.size() == 0)
/*      */         {
/* 2474 */           this.device_listeners = null;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/* 2484 */     this.manager.log(str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(String str, Throwable e)
/*      */   {
/* 2492 */     this.manager.log(str, e);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getString()
/*      */   {
/* 2498 */     return "type=" + this.type + ",uid=" + this.uid + ",class=" + this.classification;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 2505 */     writer.println(getName() + "/" + getID() + "/" + this.type);
/*      */     try
/*      */     {
/* 2508 */       writer.indent();
/*      */       
/* 2510 */       writer.println("hidden=" + this.hidden + ", last_seen=" + new SimpleDateFormat().format(new Date(this.last_seen)) + ", online=" + this.online + ", transcoding=" + this.transcoding);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2516 */       writer.println("p_props=" + this.persistent_properties);
/* 2517 */       writer.println("t_props=" + this.transient_properties);
/*      */       
/* 2519 */       writer.println("errors=" + this.errors);
/* 2520 */       writer.println("infos=" + this.infos);
/*      */     }
/*      */     finally {
/* 2523 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generateTT(IndentWriter writer)
/*      */   {
/* 2531 */     TranscodeFileImpl[] files = getFiles();
/*      */     
/* 2533 */     int complete = 0;
/* 2534 */     int copied = 0;
/* 2535 */     int deleted = 0;
/* 2536 */     int template = 0;
/*      */     
/* 2538 */     for (TranscodeFileImpl f : files)
/*      */     {
/* 2540 */       if (f.isComplete())
/*      */       {
/* 2542 */         complete++;
/*      */       }
/*      */       
/* 2545 */       if (f.isCopiedToDevice())
/*      */       {
/* 2547 */         copied++;
/*      */       }
/*      */       
/* 2550 */       if (f.isDeleted())
/*      */       {
/* 2552 */         deleted++;
/*      */       }
/*      */       
/* 2555 */       if (f.isTemplate())
/*      */       {
/* 2557 */         template++;
/*      */       }
/*      */     }
/*      */     
/* 2561 */     writer.println("files=" + files.length + ", comp=" + complete + ", copied=" + copied + ", deleted=" + deleted + ", template=" + template);
/*      */   }
/*      */   
/*      */   protected void destroy() {}
/*      */   
/*      */   protected void updateStatus(int tick_count) {}
/*      */   
/*      */   /* Error */
/*      */   public Object getTransientProperty(Object key)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 1197	com/aelitis/azureus/core/devices/impl/DeviceImpl:transient_properties	Ljava/util/Map;
/*      */     //   4: dup
/*      */     //   5: astore_2
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 1197	com/aelitis/azureus/core/devices/impl/DeviceImpl:transient_properties	Ljava/util/Map;
/*      */     //   11: aload_1
/*      */     //   12: invokeinterface 1390 2 0
/*      */     //   17: aload_2
/*      */     //   18: monitorexit
/*      */     //   19: areturn
/*      */     //   20: astore_3
/*      */     //   21: aload_2
/*      */     //   22: monitorexit
/*      */     //   23: aload_3
/*      */     //   24: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2074	-> byte code offset #0
/*      */     //   Java source line #2076	-> byte code offset #7
/*      */     //   Java source line #2077	-> byte code offset #20
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	25	0	this	DeviceImpl
/*      */     //   0	25	1	key	Object
/*      */     //   5	17	2	Ljava/lang/Object;	Object
/*      */     //   20	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	19	20	finally
/*      */     //   20	23	20	finally
/*      */   }
/*      */   
/*      */   protected static class browseLocationImpl
/*      */     implements Device.browseLocation
/*      */   {
/*      */     private String name;
/*      */     private URL url;
/*      */     
/*      */     protected browseLocationImpl(String _name, URL _url)
/*      */     {
/* 2576 */       this.name = _name;
/* 2577 */       this.url = _url;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getName()
/*      */     {
/* 2583 */       return this.name;
/*      */     }
/*      */     
/*      */ 
/*      */     public URL getURL()
/*      */     {
/* 2589 */       return this.url;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/DeviceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */