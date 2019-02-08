/*      */ package org.gudy.azureus2.core3.config.impl;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.LineNumberReader;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
/*      */ import java.util.TreeSet;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager.ParameterVerifier;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager.ResetToDefaultsListener;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.config.StringList;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.ConcurrentHashMapWrapper;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.FrequencyLimitedDispatcher;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SystemProperties;
/*      */ 
/*      */ public class ConfigurationManager implements org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator
/*      */ {
/*      */   private static final boolean DEBUG_PARAMETER_LISTENERS = false;
/*   42 */   private static ConfigurationManager config_temp = null;
/*   43 */   private static ConfigurationManager config = null;
/*   44 */   private static final AEMonitor class_mon = new AEMonitor("ConfigMan:class");
/*      */   
/*      */   private ConcurrentHashMapWrapper<String, Object> propertiesMap;
/*      */   
/*   48 */   private final List transient_properties = new ArrayList();
/*      */   
/*   50 */   private final List<COConfigurationListener> listenerz = new ArrayList();
/*   51 */   private final Map<String, ParameterListener[]> parameterListenerz = new HashMap();
/*      */   
/*   53 */   private final List<COConfigurationManager.ResetToDefaultsListener> reset_to_def_listeners = new ArrayList();
/*      */   
/*   55 */   private static final FrequencyLimitedDispatcher dirty_dispatcher = new FrequencyLimitedDispatcher(new AERunnable() { public void runSupport() {} }, 30000);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   67 */   private final ParameterListener exportable_parameter_listener = new ParameterListener()
/*      */   {
/*      */ 
/*      */ 
/*      */     public void parameterChanged(String key)
/*      */     {
/*      */ 
/*      */ 
/*   75 */       ConfigurationManager.this.updateExportableParameter(key);
/*      */     }
/*      */   };
/*      */   
/*   79 */   private final Map<String, String[]> exported_parameters = new HashMap();
/*   80 */   private final Map<String, String> imported_parameters = new HashMap();
/*      */   private volatile boolean exported_parameters_dirty;
/*      */   
/*      */   public static ConfigurationManager getInstance()
/*      */   {
/*      */     try {
/*   86 */       class_mon.enter();
/*      */       ConfigurationManager localConfigurationManager;
/*   88 */       if (config == null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   96 */         if (config_temp == null)
/*      */         {
/*   98 */           config_temp = new ConfigurationManager();
/*      */           
/*  100 */           config_temp.load();
/*      */           
/*  102 */           config_temp.initialise();
/*      */           
/*  104 */           config = config_temp;
/*      */         }
/*      */         else
/*      */         {
/*  108 */           if (config_temp.propertiesMap == null)
/*      */           {
/*  110 */             config_temp.load();
/*      */           }
/*      */           
/*  113 */           return config_temp;
/*      */         }
/*      */       }
/*      */       
/*  117 */       return config;
/*      */     }
/*      */     finally {
/*  120 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public static ConfigurationManager getInstance(Map data) {
/*      */     try {
/*  126 */       class_mon.enter();
/*      */       
/*  128 */       if (config == null)
/*      */       {
/*  130 */         config = new ConfigurationManager(data);
/*      */       }
/*      */       
/*  133 */       return config;
/*      */     }
/*      */     finally {
/*  136 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private ConfigurationManager() {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private ConfigurationManager(Map data)
/*      */   {
/*  153 */     if (data.get("Logger.DebugFiles.Enabled") == null)
/*      */     {
/*  155 */       data.put("Logger.DebugFiles.Enabled", new Long(0L));
/*      */     }
/*      */     
/*  158 */     this.propertiesMap = new ConcurrentHashMapWrapper(data);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void initialise()
/*      */   {
/*  167 */     ConfigurationChecker.checkConfiguration();
/*      */     
/*  169 */     ConfigurationChecker.setSystemProperties();
/*      */     
/*  171 */     loadExportedParameters();
/*      */     
/*  173 */     org.gudy.azureus2.core3.util.AEDiagnostics.addEvidenceGenerator(this);
/*      */   }
/*      */   
/*      */   public void load(String filename)
/*      */   {
/*  178 */     Map data = FileUtil.readResilientConfigFile(filename, false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  183 */     if (this.propertiesMap == null)
/*      */     {
/*  185 */       ConcurrentHashMapWrapper<String, Object> c_map = new ConcurrentHashMapWrapper(data.size() + 256, 0.75F, 8);
/*      */       
/*  187 */       c_map.putAll(data);
/*      */       
/*  189 */       this.propertiesMap = c_map;
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
/*      */   public void load()
/*      */   {
/*  230 */     load("azureus.config");
/*      */     try
/*      */     {
/*  233 */       String[] keys = (String[])this.propertiesMap.keySet().toArray(new String[0]);
/*  234 */       for (String key : keys) {
/*  235 */         if (key != null)
/*      */         {
/*      */ 
/*  238 */           if ((key.startsWith("SideBar.Expanded.AutoOpen.")) || (key.startsWith("NameColumn.wrapText."))) {
/*  239 */             removeParameter(key);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Exception e) {
/*  245 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */   
/*      */   public void save(String filename)
/*      */   {
/*  251 */     if (this.propertiesMap == null)
/*      */     {
/*      */ 
/*      */ 
/*  255 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  264 */     TreeMap<String, Object> properties_clone = this.propertiesMap.toTreeMap();
/*      */     
/*      */ 
/*  267 */     if (!this.transient_properties.isEmpty()) {
/*  268 */       properties_clone.keySet().removeAll(this.transient_properties);
/*      */     }
/*      */     
/*  271 */     FileUtil.writeResilientConfigFile(filename, properties_clone);
/*      */     
/*      */     List<COConfigurationListener> listeners_copy;
/*      */     
/*  275 */     synchronized (this.listenerz)
/*      */     {
/*  277 */       listeners_copy = new ArrayList(this.listenerz);
/*      */     }
/*      */     
/*  280 */     for (int i = 0; i < listeners_copy.size(); i++)
/*      */     {
/*  282 */       COConfigurationListener l = (COConfigurationListener)listeners_copy.get(i);
/*      */       
/*  284 */       if (l != null) {
/*      */         try
/*      */         {
/*  287 */           l.configurationSaved();
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  291 */           Debug.printStackTrace(e);
/*      */         }
/*      */         
/*      */       } else {
/*  295 */         Debug.out("COConfigurationListener is null");
/*      */       }
/*      */     }
/*      */     
/*  299 */     if (this.exported_parameters_dirty)
/*      */     {
/*  301 */       exportParameters();
/*      */     }
/*      */   }
/*      */   
/*      */   public void save() {
/*  306 */     save("azureus.config");
/*      */   }
/*      */   
/*      */ 
/*      */   public void setDirty()
/*      */   {
/*  312 */     dirty_dispatcher.dispatch();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isNewInstall()
/*      */   {
/*  318 */     return ConfigurationChecker.isNewInstall();
/*      */   }
/*      */   
/*      */ 
/*      */   public Set<String> getDefinedParameters()
/*      */   {
/*  324 */     return new java.util.HashSet(this.propertiesMap.keySet());
/*      */   }
/*      */   
/*      */   public boolean getBooleanParameter(String parameter, boolean defaultValue) {
/*  328 */     int defaultInt = defaultValue ? 1 : 0;
/*  329 */     int result = getIntParameter(parameter, defaultInt);
/*  330 */     return result != 0;
/*      */   }
/*      */   
/*      */   public boolean getBooleanParameter(String parameter) {
/*  334 */     ConfigurationDefaults def = ConfigurationDefaults.getInstance();
/*      */     int result;
/*      */     try {
/*  337 */       result = getIntParameter(parameter, def.getIntParameter(parameter));
/*      */     } catch (ConfigurationParameterNotFoundException e) {
/*  339 */       result = getIntParameter(parameter, 0);
/*      */     }
/*  341 */     return result != 0;
/*      */   }
/*      */   
/*      */   public boolean setParameter(String parameter, boolean value) {
/*  345 */     return setParameter(parameter, value ? 1 : 0);
/*      */   }
/*      */   
/*      */   private Long getLongParameterRaw(String parameter) {
/*      */     try {
/*  350 */       return (Long)this.propertiesMap.get(parameter);
/*      */     } catch (Exception e) {
/*  352 */       Debug.out("Parameter '" + parameter + "' has incorrect type", e); }
/*  353 */     return null;
/*      */   }
/*      */   
/*      */   public int getIntParameter(String parameter, int defaultValue)
/*      */   {
/*  358 */     Long tempValue = getLongParameterRaw(parameter);
/*  359 */     return tempValue != null ? tempValue.intValue() : defaultValue;
/*      */   }
/*      */   
/*      */   public int getIntParameter(String parameter) {
/*  363 */     ConfigurationDefaults def = ConfigurationDefaults.getInstance();
/*      */     int result;
/*      */     try {
/*  366 */       result = getIntParameter(parameter, def.getIntParameter(parameter));
/*      */     } catch (ConfigurationParameterNotFoundException e) {
/*  368 */       result = getIntParameter(parameter, 0);
/*      */     }
/*  370 */     return result;
/*      */   }
/*      */   
/*      */   public long getLongParameter(String parameter, long defaultValue) {
/*  374 */     Long tempValue = getLongParameterRaw(parameter);
/*  375 */     return tempValue != null ? tempValue.longValue() : defaultValue;
/*      */   }
/*      */   
/*      */   public long getLongParameter(String parameter) {
/*  379 */     ConfigurationDefaults def = ConfigurationDefaults.getInstance();
/*      */     long result;
/*      */     try {
/*  382 */       result = getLongParameter(parameter, def.getLongParameter(parameter));
/*      */     } catch (ConfigurationParameterNotFoundException e) {
/*  384 */       result = getLongParameter(parameter, 0L);
/*      */     }
/*  386 */     return result;
/*      */   }
/*      */   
/*      */   private byte[] getByteParameterRaw(String parameter) {
/*  390 */     return (byte[])this.propertiesMap.get(parameter);
/*      */   }
/*      */   
/*      */   public byte[] getByteParameter(String parameter) {
/*  394 */     ConfigurationDefaults def = ConfigurationDefaults.getInstance();
/*      */     byte[] result;
/*      */     try {
/*  397 */       result = getByteParameter(parameter, def.getByteParameter(parameter));
/*      */     } catch (ConfigurationParameterNotFoundException e) {
/*  399 */       result = getByteParameter(parameter, ConfigurationDefaults.def_bytes);
/*      */     }
/*  401 */     return result;
/*      */   }
/*      */   
/*      */   public byte[] getByteParameter(String parameter, byte[] defaultValue) {
/*  405 */     byte[] tempValue = getByteParameterRaw(parameter);
/*  406 */     return tempValue != null ? tempValue : defaultValue;
/*      */   }
/*      */   
/*      */   private String getStringParameter(String parameter, byte[] defaultValue) {
/*  410 */     byte[] bp = getByteParameter(parameter, defaultValue);
/*  411 */     if (bp == null) {
/*  412 */       bp = getByteParameter(parameter, null);
/*      */     }
/*  414 */     if (bp == null)
/*  415 */       return null;
/*  416 */     return bytesToString(bp);
/*      */   }
/*      */   
/*      */   public String getStringParameter(String parameter, String defaultValue) {
/*  420 */     String tempValue = getStringParameter(parameter, (byte[])null);
/*  421 */     return tempValue != null ? tempValue : defaultValue;
/*      */   }
/*      */   
/*      */   public String getStringParameter(String parameter) {
/*  425 */     ConfigurationDefaults def = ConfigurationDefaults.getInstance();
/*      */     String result;
/*      */     try {
/*  428 */       result = getStringParameter(parameter, def.getStringParameter(parameter));
/*      */     } catch (ConfigurationParameterNotFoundException e) {
/*  430 */       result = getStringParameter(parameter, "");
/*      */     }
/*  432 */     return result;
/*      */   }
/*      */   
/*      */   public StringList getStringListParameter(String parameter) {
/*      */     try {
/*  437 */       List rawList = (List)this.propertiesMap.get(parameter);
/*  438 */       if (rawList == null)
/*  439 */         return new StringListImpl();
/*  440 */       return new StringListImpl(rawList);
/*      */     } catch (Exception e) {
/*  442 */       Debug.out("Parameter '" + parameter + "' has incorrect type", e); }
/*  443 */     return new StringListImpl();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean setParameter(String parameter, StringList value)
/*      */   {
/*      */     try
/*      */     {
/*  451 */       List encoded = new ArrayList();
/*      */       
/*  453 */       List l = ((StringListImpl)value).getList();
/*      */       
/*  455 */       for (int i = 0; i < l.size(); i++)
/*      */       {
/*  457 */         encoded.add(stringToBytes((String)l.get(i)));
/*      */       }
/*  459 */       this.propertiesMap.put(parameter, encoded);
/*  460 */       notifyParameterListeners(parameter);
/*      */     } catch (Exception e) {
/*  462 */       Debug.printStackTrace(e);
/*  463 */       return false;
/*      */     }
/*  465 */     return true;
/*      */   }
/*      */   
/*      */   public List getListParameter(String parameter, List def)
/*      */   {
/*      */     try
/*      */     {
/*  472 */       List rawList = (List)this.propertiesMap.get(parameter);
/*  473 */       if (rawList == null)
/*  474 */         return def;
/*  475 */       return rawList;
/*      */     } catch (Exception e) {
/*  477 */       Debug.out("Parameter '" + parameter + "' has incorrect type", e); }
/*  478 */     return def;
/*      */   }
/*      */   
/*      */   public boolean setParameter(String parameter, List value)
/*      */   {
/*      */     try {
/*  484 */       this.propertiesMap.put(parameter, value);
/*  485 */       notifyParameterListeners(parameter);
/*      */     } catch (Exception e) {
/*  487 */       Debug.printStackTrace(e);
/*  488 */       return false;
/*      */     }
/*  490 */     return true;
/*      */   }
/*      */   
/*      */   public Map getMapParameter(String parameter, Map def)
/*      */   {
/*      */     try
/*      */     {
/*  497 */       Map map = (Map)this.propertiesMap.get(parameter);
/*  498 */       if (map == null)
/*  499 */         return def;
/*  500 */       return map;
/*      */     } catch (Exception e) {
/*  502 */       Debug.out("Parameter '" + parameter + "' has incorrect type", e); }
/*  503 */     return def;
/*      */   }
/*      */   
/*      */   public boolean setParameter(String parameter, Map value)
/*      */   {
/*      */     try {
/*  509 */       this.propertiesMap.put(parameter, value);
/*  510 */       notifyParameterListeners(parameter);
/*      */     } catch (Exception e) {
/*  512 */       Debug.printStackTrace(e);
/*  513 */       return false;
/*      */     }
/*  515 */     return true;
/*      */   }
/*      */   
/*      */   public String getDirectoryParameter(String parameter) throws IOException
/*      */   {
/*  520 */     String dir = getStringParameter(parameter);
/*      */     
/*  522 */     if (dir.length() > 0) {
/*  523 */       File temp = new File(dir);
/*  524 */       if (!temp.exists()) {
/*  525 */         FileUtil.mkdirs(temp);
/*      */       }
/*  527 */       if (!temp.isDirectory()) {
/*  528 */         throw new IOException("Configuration error. This is not a directory: " + dir);
/*      */       }
/*      */     }
/*      */     
/*  532 */     return dir;
/*      */   }
/*      */   
/*      */   public float getFloatParameter(String parameter) {
/*  536 */     return getFloatParameter(parameter, 0.0F);
/*      */   }
/*      */   
/*      */   public float getFloatParameter(String parameter, float def_val) {
/*  540 */     ConfigurationDefaults def = ConfigurationDefaults.getInstance();
/*      */     try {
/*  542 */       Object o = this.propertiesMap.get(parameter);
/*  543 */       if ((o instanceof Number)) {
/*  544 */         return ((Number)o).floatValue();
/*      */       }
/*      */       
/*  547 */       String s = getStringParameter(parameter);
/*      */       
/*  549 */       if (!s.equals(""))
/*  550 */         return Float.parseFloat(s);
/*      */     } catch (Exception e) {
/*  552 */       Debug.out("Parameter '" + parameter + "' has incorrect type", e);
/*      */     }
/*      */     try
/*      */     {
/*  556 */       return def.getFloatParameter(parameter);
/*      */     } catch (Exception e2) {}
/*  558 */     return def_val;
/*      */   }
/*      */   
/*      */   public boolean setParameter(String parameter, float defaultValue)
/*      */   {
/*  563 */     String newValue = String.valueOf(defaultValue);
/*  564 */     return setParameter(parameter, stringToBytes(newValue));
/*      */   }
/*      */   
/*      */   public boolean setParameter(String parameter, int defaultValue) {
/*  568 */     Long newValue = new Long(defaultValue);
/*      */     try {
/*  570 */       Long oldValue = (Long)this.propertiesMap.put(parameter, newValue);
/*  571 */       return notifyParameterListenersIfChanged(parameter, newValue, oldValue);
/*      */     }
/*      */     catch (ClassCastException e)
/*      */     {
/*  575 */       notifyParameterListeners(parameter); }
/*  576 */     return true;
/*      */   }
/*      */   
/*      */   public boolean setParameter(String parameter, long defaultValue)
/*      */   {
/*  581 */     Long newValue = new Long(defaultValue);
/*      */     try {
/*  583 */       Long oldValue = (Long)this.propertiesMap.put(parameter, newValue);
/*  584 */       return notifyParameterListenersIfChanged(parameter, newValue, oldValue);
/*      */     }
/*      */     catch (ClassCastException e)
/*      */     {
/*  588 */       notifyParameterListeners(parameter); }
/*  589 */     return true;
/*      */   }
/*      */   
/*      */   public boolean setParameter(String parameter, byte[] defaultValue)
/*      */   {
/*      */     try {
/*  595 */       byte[] oldValue = (byte[])this.propertiesMap.put(parameter, defaultValue);
/*  596 */       return notifyParameterListenersIfChanged(parameter, defaultValue, oldValue);
/*      */ 
/*      */     }
/*      */     catch (ClassCastException e)
/*      */     {
/*  601 */       notifyParameterListeners(parameter); }
/*  602 */     return true;
/*      */   }
/*      */   
/*      */   public boolean setParameter(String parameter, String defaultValue)
/*      */   {
/*  607 */     return setParameter(parameter, stringToBytes(defaultValue));
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
/*      */   public boolean hasParameter(String key, boolean explicit)
/*      */   {
/*  620 */     if (this.propertiesMap.containsKey(key)) { return true;
/*      */     }
/*      */     
/*  623 */     if ((!explicit) && (ConfigurationDefaults.getInstance().hasParameter(key))) {
/*  624 */       return true;
/*      */     }
/*      */     
/*  627 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean verifyParameter(String parameter, String value)
/*      */   {
/*  635 */     List verifiers = ConfigurationDefaults.getInstance().getVerifiers(parameter);
/*      */     
/*  637 */     if (verifiers != null) {
/*      */       try {
/*  639 */         for (int i = 0; i < verifiers.size(); i++)
/*      */         {
/*  641 */           COConfigurationManager.ParameterVerifier verifier = (COConfigurationManager.ParameterVerifier)verifiers.get(i);
/*      */           
/*  643 */           if (verifier != null) {
/*      */             try
/*      */             {
/*  646 */               if (!verifier.verify(parameter, value))
/*      */               {
/*  648 */                 return false;
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/*  652 */               Debug.printStackTrace(e);
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  660 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  664 */     return true;
/*      */   }
/*      */   
/*      */   public boolean setRGBParameter(String parameter, int red, int green, int blue) {
/*  668 */     boolean bAnyChanged = false;
/*  669 */     bAnyChanged |= setParameter(parameter + ".red", red);
/*  670 */     bAnyChanged |= setParameter(parameter + ".green", green);
/*  671 */     bAnyChanged |= setParameter(parameter + ".blue", blue);
/*  672 */     if (bAnyChanged) {
/*  673 */       notifyParameterListeners(parameter);
/*      */     }
/*  675 */     return bAnyChanged;
/*      */   }
/*      */   
/*      */   public boolean setRGBParameter(String parameter, int[] rgb, boolean override) {
/*  679 */     boolean changed = false;
/*  680 */     if (rgb == null) {
/*  681 */       changed |= removeParameter(parameter + ".override");
/*  682 */       changed |= removeParameter(parameter + ".red");
/*  683 */       changed |= removeParameter(parameter + ".green");
/*  684 */       changed |= removeParameter(parameter + ".blue");
/*      */     }
/*      */     else {
/*  687 */       changed |= setParameter(parameter + ".override", override);
/*  688 */       changed |= setRGBParameter(parameter, rgb[0], rgb[1], rgb[2]);
/*      */     }
/*  690 */     if (changed) {
/*  691 */       notifyParameterListeners(parameter);
/*      */     }
/*  693 */     return changed;
/*      */   }
/*      */   
/*      */   public boolean setParameter(String parameter) throws ConfigurationParameterNotFoundException
/*      */   {
/*  698 */     ConfigurationDefaults def = ConfigurationDefaults.getInstance();
/*      */     try {
/*  700 */       return setParameter(parameter, def.getIntParameter(parameter));
/*      */     } catch (Exception e) {}
/*  702 */     return setParameter(parameter, def.getStringParameter(parameter));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object getParameter(String name)
/*      */   {
/*  710 */     Object value = this.propertiesMap.get(name);
/*      */     
/*  712 */     if (value == null)
/*      */     {
/*  714 */       value = ConfigurationDefaults.getInstance().getParameter(name);
/*      */     }
/*      */     
/*  717 */     return value;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setParameterRawNoNotify(String parameter, Object value)
/*      */   {
/*  729 */     this.propertiesMap.put(parameter, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void registerTransientParameter(String param)
/*      */   {
/*  739 */     this.transient_properties.add(param);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean removeParameter(String parameter)
/*      */   {
/*  748 */     boolean removed = this.propertiesMap.remove(parameter) != null;
/*  749 */     if (removed)
/*  750 */       notifyParameterListeners(parameter);
/*  751 */     return removed;
/*      */   }
/*      */   
/*      */   public boolean removeRGBParameter(String parameter) {
/*  755 */     boolean bAnyChanged = false;
/*  756 */     bAnyChanged |= removeParameter(parameter + ".red");
/*  757 */     bAnyChanged |= removeParameter(parameter + ".green");
/*  758 */     bAnyChanged |= removeParameter(parameter + ".blue");
/*  759 */     bAnyChanged |= removeParameter(parameter + ".override");
/*  760 */     if (bAnyChanged) {
/*  761 */       notifyParameterListeners(parameter);
/*      */     }
/*  763 */     return bAnyChanged;
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
/*      */   public boolean doesParameterNonDefaultExist(String parameter)
/*      */   {
/*  776 */     return this.propertiesMap.containsKey(parameter);
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean notifyParameterListenersIfChanged(String parameter, Long newValue, Long oldValue)
/*      */   {
/*  782 */     if ((oldValue == null) || (0 != newValue.compareTo(oldValue))) {
/*  783 */       notifyParameterListeners(parameter);
/*  784 */       return true;
/*      */     }
/*  786 */     return false;
/*      */   }
/*      */   
/*      */   private boolean notifyParameterListenersIfChanged(String parameter, byte[] newValue, byte[] oldValue) {
/*  790 */     if ((oldValue == null) || (!java.util.Arrays.equals(newValue, oldValue))) {
/*  791 */       notifyParameterListeners(parameter);
/*  792 */       return true;
/*      */     }
/*  794 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addResetToDefaultsListener(COConfigurationManager.ResetToDefaultsListener l)
/*      */   {
/*  801 */     synchronized (this.reset_to_def_listeners)
/*      */     {
/*  803 */       this.reset_to_def_listeners.add(l);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void registerExportedParameter(String name, String key)
/*      */   {
/*  812 */     synchronized (this.exported_parameters)
/*      */     {
/*  814 */       String[] entry = (String[])this.exported_parameters.get(key);
/*      */       
/*  816 */       if (entry == null)
/*      */       {
/*  818 */         entry = new String[] { name, (String)this.imported_parameters.remove(name) };
/*      */         
/*  820 */         this.exported_parameters.put(key, entry);
/*      */       }
/*      */     }
/*      */     
/*  824 */     addParameterListener(key, this.exportable_parameter_listener);
/*      */     
/*      */ 
/*      */ 
/*  828 */     updateExportableParameter(key);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void updateExportableParameter(String key)
/*      */   {
/*  836 */     Object o_value = getParameter(key);
/*      */     
/*      */     String value;
/*      */     String value;
/*  840 */     if (o_value == null)
/*      */     {
/*  842 */       value = null;
/*      */     }
/*  844 */     else if ((o_value instanceof byte[])) {
/*      */       try
/*      */       {
/*  847 */         value = new String((byte[])o_value, "UTF-8");
/*      */       }
/*      */       catch (UnsupportedEncodingException e)
/*      */       {
/*  851 */         String value = null;
/*      */       }
/*      */       
/*      */     } else {
/*  855 */       value = String.valueOf(o_value);
/*      */     }
/*      */     
/*  858 */     synchronized (this.exported_parameters)
/*      */     {
/*  860 */       String[] entry = (String[])this.exported_parameters.get(key);
/*      */       
/*  862 */       if (entry != null)
/*      */       {
/*  864 */         String existing = entry[1];
/*      */         
/*  866 */         if (existing != value)
/*      */         {
/*  868 */           if ((existing == null) || (value == null) || (!existing.equals(value)))
/*      */           {
/*  870 */             entry[1] = value;
/*      */             
/*  872 */             if (!this.exported_parameters_dirty)
/*      */             {
/*  874 */               this.exported_parameters_dirty = true;
/*      */               
/*  876 */               new org.gudy.azureus2.core3.util.DelayedEvent("epd", 5000L, new AERunnable()
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void runSupport()
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*  886 */                   ConfigurationManager.this.exportParameters();
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void exportParameters()
/*      */   {
/*  899 */     synchronized (this.exported_parameters)
/*      */     {
/*  901 */       if (!this.exported_parameters_dirty)
/*      */       {
/*  903 */         return;
/*      */       }
/*      */       
/*  906 */       this.exported_parameters_dirty = false;
/*      */       try
/*      */       {
/*  909 */         TreeMap<String, String> tm = new TreeMap();
/*      */         
/*  911 */         Set<String> exported_keys = new java.util.HashSet();
/*      */         
/*  913 */         for (String[] entry : this.exported_parameters.values())
/*      */         {
/*  915 */           String key = entry[0];
/*  916 */           String value = entry[1];
/*      */           
/*  918 */           exported_keys.add(key);
/*      */           
/*  920 */           if (value != null)
/*      */           {
/*  922 */             tm.put(key, value);
/*      */           }
/*      */         }
/*      */         
/*  926 */         for (Map.Entry<String, String> entry : this.imported_parameters.entrySet())
/*      */         {
/*  928 */           String key = (String)entry.getKey();
/*      */           
/*  930 */           if (!exported_keys.contains(key))
/*      */           {
/*  932 */             tm.put(key, entry.getValue());
/*      */           }
/*      */         }
/*      */         
/*  936 */         File parent_dir = new File(SystemProperties.getUserPath());
/*      */         
/*  938 */         File props = new File(parent_dir, "exported_params.properties");
/*      */         
/*  940 */         PrintWriter pw = new PrintWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(props), "UTF-8"));
/*      */         try
/*      */         {
/*  943 */           for (Map.Entry<String, String> entry : tm.entrySet())
/*      */           {
/*  945 */             pw.println((String)entry.getKey() + "=" + (String)entry.getValue());
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/*  950 */           pw.close();
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/*  954 */         e.printStackTrace();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void loadExportedParameters()
/*      */   {
/*  962 */     synchronized (this.exported_parameters)
/*      */     {
/*      */       try {
/*  965 */         File parent_dir = new File(SystemProperties.getUserPath());
/*      */         
/*  967 */         File props = new File(parent_dir, "exported_params.properties");
/*      */         
/*  969 */         if (props.exists())
/*      */         {
/*  971 */           LineNumberReader lnr = new LineNumberReader(new java.io.InputStreamReader(new java.io.FileInputStream(props), "UTF-8"));
/*      */           try
/*      */           {
/*      */             for (;;)
/*      */             {
/*  976 */               String line = lnr.readLine();
/*      */               
/*  978 */               if (line == null) {
/*      */                 break;
/*      */               }
/*      */               
/*      */ 
/*  983 */               String[] bits = line.split("=");
/*      */               
/*  985 */               if (bits.length == 2)
/*      */               {
/*  987 */                 String key = bits[0].trim();
/*  988 */                 String value = bits[1].trim();
/*      */                 
/*  990 */                 if ((key.length() > 0) && (value.length() > 0))
/*      */                 {
/*  992 */                   this.imported_parameters.put(key, value);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           finally {
/*  998 */             lnr.close();
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1003 */         e.printStackTrace();
/*      */       }
/*      */     }
/*      */     
/* 1007 */     COConfigurationManager.setIntDefault("instance.port", Constants.INSTANCE_PORT);
/*      */     
/* 1009 */     registerExportedParameter("instance.port", "instance.port");
/*      */   }
/*      */   
/*      */ 
/*      */   public void resetToDefaults()
/*      */   {
/* 1015 */     ConfigurationDefaults def = ConfigurationDefaults.getInstance();
/*      */     
/* 1017 */     List<String> def_names = new ArrayList(def.getAllowedParameters());
/*      */     
/* 1019 */     for (String s : def_names)
/*      */     {
/* 1021 */       if (this.propertiesMap.remove(s) != null)
/*      */       {
/* 1023 */         notifyParameterListeners(s);
/*      */       }
/*      */     }
/*      */     
/*      */     List<COConfigurationManager.ResetToDefaultsListener> listeners;
/*      */     
/* 1029 */     synchronized (this.reset_to_def_listeners)
/*      */     {
/* 1031 */       listeners = new ArrayList(this.reset_to_def_listeners);
/*      */     }
/*      */     
/* 1034 */     for (COConfigurationManager.ResetToDefaultsListener l : listeners) {
/*      */       try
/*      */       {
/* 1037 */         l.reset();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1041 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/* 1045 */     save();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void notifyParameterListeners(String parameter)
/*      */   {
/*      */     ParameterListener[] listeners;
/*      */     
/* 1054 */     synchronized (this.parameterListenerz)
/*      */     {
/* 1056 */       listeners = (ParameterListener[])this.parameterListenerz.get(parameter);
/*      */     }
/*      */     
/* 1059 */     if (listeners == null) {
/* 1060 */       return;
/*      */     }
/*      */     
/* 1063 */     for (ParameterListener listener : listeners)
/*      */     {
/* 1065 */       if (listener != null) {
/*      */         try
/*      */         {
/* 1068 */           listener.parameterChanged(parameter);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1072 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addParameterListener(String parameter, ParameterListener new_listener)
/*      */   {
/* 1083 */     if ((parameter == null) || (new_listener == null))
/*      */     {
/* 1085 */       return;
/*      */     }
/*      */     
/* 1088 */     synchronized (this.parameterListenerz)
/*      */     {
/* 1090 */       ParameterListener[] listeners = (ParameterListener[])this.parameterListenerz.get(parameter);
/*      */       
/* 1092 */       if (listeners == null)
/*      */       {
/* 1094 */         this.parameterListenerz.put(parameter, new ParameterListener[] { new_listener });
/*      */       }
/*      */       else
/*      */       {
/* 1098 */         ParameterListener[] new_listeners = new ParameterListener[listeners.length + 1];
/*      */         
/* 1100 */         if ((Constants.IS_CVS_VERSION) && (listeners.length > 100)) {
/* 1101 */           Debug.out(parameter);
/*      */         }
/*      */         
/*      */         int pos;
/*      */         int pos;
/* 1106 */         if ((new_listener instanceof org.gudy.azureus2.core3.config.PriorityParameterListener))
/*      */         {
/* 1108 */           new_listeners[0] = new_listener;
/*      */           
/* 1110 */           pos = 1;
/*      */         }
/*      */         else
/*      */         {
/* 1114 */           new_listeners[listeners.length] = new_listener;
/*      */           
/* 1116 */           pos = 0;
/*      */         }
/*      */         
/* 1119 */         for (int i = 0; i < listeners.length; i++)
/*      */         {
/* 1121 */           ParameterListener existing_listener = listeners[i];
/*      */           
/* 1123 */           if (existing_listener == new_listener)
/*      */           {
/* 1125 */             return;
/*      */           }
/*      */           
/* 1128 */           new_listeners[(pos++)] = existing_listener;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1136 */         this.parameterListenerz.put(parameter, new_listeners);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeParameterListener(String parameter, ParameterListener listener)
/*      */   {
/* 1143 */     if ((parameter == null) || (listener == null)) {
/* 1144 */       return;
/*      */     }
/*      */     
/* 1147 */     synchronized (this.parameterListenerz) {
/* 1148 */       ParameterListener[] listeners = (ParameterListener[])this.parameterListenerz.get(parameter);
/*      */       
/* 1150 */       if (listeners == null)
/*      */       {
/* 1152 */         return;
/*      */       }
/*      */       
/* 1155 */       if (listeners.length == 1)
/*      */       {
/* 1157 */         if (listeners[0] == listener)
/*      */         {
/* 1159 */           this.parameterListenerz.remove(parameter);
/*      */         }
/*      */       }
/*      */       else {
/* 1163 */         ParameterListener[] new_listeners = new ParameterListener[listeners.length - 1];
/*      */         
/* 1165 */         int pos = 0;
/*      */         
/* 1167 */         for (int i = 0; i < listeners.length; i++)
/*      */         {
/* 1169 */           ParameterListener existing_listener = listeners[i];
/*      */           
/* 1171 */           if (existing_listener != listener)
/*      */           {
/* 1173 */             if (pos == new_listeners.length)
/*      */             {
/* 1175 */               return;
/*      */             }
/*      */             
/* 1178 */             new_listeners[(pos++)] = existing_listener;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1187 */         this.parameterListenerz.put(parameter, new_listeners);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void addListener(COConfigurationListener listener) {
/* 1193 */     synchronized (this.listenerz)
/*      */     {
/* 1195 */       this.listenerz.add(listener);
/*      */     }
/*      */   }
/*      */   
/*      */   public void addAndFireListener(COConfigurationListener listener) {
/* 1200 */     synchronized (this.listenerz)
/*      */     {
/* 1202 */       this.listenerz.add(listener);
/*      */     }
/*      */     
/*      */     try
/*      */     {
/* 1207 */       listener.configurationSaved();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1211 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/* 1215 */   public void removeListener(COConfigurationListener listener) { synchronized (this.listenerz)
/*      */     {
/* 1217 */       this.listenerz.remove(listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean ignoreKeyForDump(String key)
/*      */   {
/* 1225 */     String lc_key = key.toLowerCase(Locale.US);
/*      */     
/* 1227 */     if ((key.startsWith("core.crypto.")) || (lc_key.equals("id")) || (lc_key.equals("azbuddy.dchat.optsmap")) || (lc_key.endsWith(".privx")) || (lc_key.endsWith(".user")) || (lc_key.contains("password")) || (lc_key.contains("username")) || (lc_key.contains("session key")))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1236 */       return true;
/*      */     }
/*      */     
/* 1239 */     Object value = this.propertiesMap.get(key);
/*      */     
/* 1241 */     if ((value instanceof byte[])) {
/*      */       try
/*      */       {
/* 1244 */         value = new String((byte[])value, "UTF-8");
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1251 */     if ((value instanceof String))
/*      */     {
/* 1253 */       if (((String)value).toLowerCase(Locale.US).endsWith(".b32.i2p"))
/*      */       {
/* 1255 */         return true;
/*      */       }
/*      */     }
/*      */     
/* 1259 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 1266 */     writer.println("Configuration Details");
/*      */     try
/*      */     {
/* 1269 */       writer.indent();
/*      */       
/* 1271 */       writer.println("version=5.7.6.0, subver=");
/*      */       
/* 1273 */       writer.println("System Properties");
/*      */       try
/*      */       {
/* 1276 */         writer.indent();
/*      */         
/* 1278 */         Properties props = System.getProperties();
/*      */         
/* 1280 */         Iterator it = new TreeSet(props.keySet()).iterator();
/*      */         
/* 1282 */         while (it.hasNext())
/*      */         {
/* 1284 */           String key = (String)it.next();
/*      */           
/* 1286 */           writer.println(key + "=" + props.get(key));
/*      */         }
/*      */       }
/*      */       finally {}
/*      */       
/*      */ 
/*      */ 
/* 1293 */       writer.println("Environment");
/*      */       try
/*      */       {
/* 1296 */         writer.indent();
/*      */         
/* 1298 */         Map<String, String> env = System.getenv();
/*      */         
/* 1300 */         if (env == null)
/*      */         {
/* 1302 */           writer.println("Not supported");
/*      */         }
/*      */         else
/*      */         {
/* 1306 */           Iterator it = new TreeSet(env.keySet()).iterator();
/*      */           
/* 1308 */           while (it.hasNext())
/*      */           {
/* 1310 */             String key = (String)it.next();
/*      */             
/* 1312 */             writer.println(key + "=" + (String)env.get(key));
/*      */           }
/*      */         }
/*      */       }
/*      */       finally {}
/*      */       
/*      */ 
/*      */ 
/* 1320 */       writer.println("Azureus Config");
/*      */       
/* 1322 */       ConfigurationDefaults defaults = ConfigurationDefaults.getInstance();
/*      */       try
/*      */       {
/* 1325 */         writer.indent();
/*      */         
/* 1327 */         Set<String> keys = new TreeSet(new Comparator()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           public int compare(String o1, String o2)
/*      */           {
/*      */ 
/*      */ 
/* 1336 */             return o1.compareToIgnoreCase(o2);
/*      */           }
/*      */           
/* 1339 */         });
/* 1340 */         keys.addAll(this.propertiesMap.keySet());
/*      */         
/* 1342 */         Iterator<String> it = keys.iterator();
/*      */         
/* 1344 */         while (it.hasNext())
/*      */         {
/* 1346 */           String key = (String)it.next();
/*      */           
/*      */ 
/*      */ 
/* 1350 */           if (!ignoreKeyForDump(key))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1355 */             Object value = this.propertiesMap.get(key);
/*      */             
/* 1357 */             boolean bParamExists = defaults.doesParameterDefaultExist(key.toString());
/*      */             
/* 1359 */             if (!bParamExists)
/*      */             {
/* 1361 */               key = "[NoDef] " + key;
/*      */             }
/*      */             else {
/* 1364 */               Object def = defaults.getParameter(key);
/*      */               
/* 1366 */               if ((def != null) && (value != null))
/*      */               {
/* 1368 */                 if (!BEncoder.objectsAreIdentical(def, value))
/*      */                 {
/* 1370 */                   key = "-> " + key;
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 1375 */             if ((value instanceof Long))
/*      */             {
/* 1377 */               writer.println(key + "=" + value);
/*      */             }
/* 1379 */             else if ((value instanceof List))
/*      */             {
/* 1381 */               writer.println(key + "=" + BDecoder.decodeStrings((List)BEncoder.clone(value)) + "[list]");
/*      */             }
/* 1383 */             else if ((value instanceof Map))
/*      */             {
/* 1385 */               writer.println(key + "=" + BDecoder.decodeStrings((Map)BEncoder.clone(value)) + "[map]");
/*      */             }
/* 1387 */             else if ((value instanceof byte[]))
/*      */             {
/* 1389 */               byte[] b = (byte[])value;
/*      */               
/* 1391 */               boolean hex = false;
/*      */               
/* 1393 */               for (int i = 0; i < b.length; i++)
/*      */               {
/* 1395 */                 char c = (char)b[i];
/*      */                 
/* 1397 */                 if ((!Character.isLetterOrDigit(c)) && ("\\ `Â¬\"Â£$%^&*()-_=+[{]};:'@#~,<.>/?'".indexOf(c) == -1))
/*      */                 {
/*      */ 
/* 1400 */                   hex = true;
/*      */                   
/* 1402 */                   break;
/*      */                 }
/*      */               }
/* 1405 */               writer.println(key + "=" + (hex ? ByteFormatter.nicePrint(b) : bytesToString((byte[])value)));
/*      */             }
/*      */             else
/*      */             {
/* 1409 */               writer.println(key + "=" + value + "[unknown]");
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       finally {}
/*      */     }
/*      */     finally
/*      */     {
/* 1418 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void dumpConfigChanges(IndentWriter writer)
/*      */   {
/* 1426 */     ConfigurationDefaults defaults = ConfigurationDefaults.getInstance();
/*      */     
/* 1428 */     Set<String> keys = new TreeSet(new Comparator()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public int compare(String o1, String o2)
/*      */       {
/*      */ 
/*      */ 
/* 1437 */         return o1.compareToIgnoreCase(o2);
/*      */       }
/*      */       
/* 1440 */     });
/* 1441 */     keys.addAll(this.propertiesMap.keySet());
/*      */     
/* 1443 */     Iterator<String> it = keys.iterator();
/*      */     
/* 1445 */     while (it.hasNext())
/*      */     {
/* 1447 */       String key = (String)it.next();
/*      */       
/*      */ 
/*      */ 
/* 1451 */       if (!ignoreKeyForDump(key))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1456 */         Object value = this.propertiesMap.get(key);
/*      */         
/* 1458 */         boolean bParamExists = defaults.doesParameterDefaultExist(key.toString());
/*      */         
/* 1460 */         if (bParamExists)
/*      */         {
/* 1462 */           Object def = defaults.getParameter(key);
/*      */           
/* 1464 */           if ((def != null) && (value != null))
/*      */           {
/* 1466 */             if (!BEncoder.objectsAreIdentical(def, value))
/*      */             {
/* 1468 */               if ((value instanceof Long))
/*      */               {
/* 1470 */                 writer.println(key + "=" + value);
/*      */               }
/* 1472 */               else if ((value instanceof List))
/*      */               {
/* 1474 */                 writer.println(key + "=" + BDecoder.decodeStrings((List)BEncoder.clone(value)) + "[list]");
/*      */               }
/* 1476 */               else if ((value instanceof Map))
/*      */               {
/* 1478 */                 writer.println(key + "=" + BDecoder.decodeStrings((Map)BEncoder.clone(value)) + "[map]");
/*      */               }
/* 1480 */               else if ((value instanceof byte[]))
/*      */               {
/* 1482 */                 byte[] b = (byte[])value;
/*      */                 
/* 1484 */                 boolean hex = false;
/*      */                 
/* 1486 */                 for (int i = 0; i < b.length; i++)
/*      */                 {
/* 1488 */                   char c = (char)b[i];
/*      */                   
/* 1490 */                   if ((!Character.isLetterOrDigit(c)) && ("\\ `Â¬\"Â£$%^&*()-_=+[{]};:'@#~,<.>/?'".indexOf(c) == -1))
/*      */                   {
/*      */ 
/* 1493 */                     hex = true;
/*      */                     
/* 1495 */                     break;
/*      */                   }
/*      */                 }
/* 1498 */                 writer.println(key + "=" + (hex ? ByteFormatter.nicePrint(b) : bytesToString((byte[])value)));
/*      */               }
/*      */               else
/*      */               {
/* 1502 */                 writer.println(key + "=" + value + "[unknown]");
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected static String bytesToString(byte[] bytes)
/*      */   {
/*      */     try
/*      */     {
/* 1515 */       return new String(bytes, "UTF8");
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 1519 */     return new String(bytes);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static byte[] stringToBytes(String str)
/*      */   {
/* 1527 */     if (str == null)
/*      */     {
/* 1529 */       return null;
/*      */     }
/*      */     try
/*      */     {
/* 1533 */       return str.getBytes("UTF8");
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 1537 */     return str.getBytes();
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/config/impl/ConfigurationManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */