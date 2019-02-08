/*      */ package org.gudy.azureus2.pluginsimpl.local;
/*      */ 
/*      */ import com.aelitis.net.magneturi.MagnetURIHandler;
/*      */ import java.io.File;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.StringList;
/*      */ import org.gudy.azureus2.core3.config.impl.ConfigurationDefaults;
/*      */ import org.gudy.azureus2.core3.config.impl.StringListImpl;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.plugins.PluginConfig;
/*      */ import org.gudy.azureus2.plugins.PluginConfigListener;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginState;
/*      */ import org.gudy.azureus2.plugins.config.ConfigParameter;
/*      */ import org.gudy.azureus2.plugins.config.PluginConfigSource;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.pluginsimpl.local.config.ConfigParameterImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.config.PluginConfigSourceImpl;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class PluginConfigImpl
/*      */   implements PluginConfig
/*      */ {
/*   47 */   protected static Map<String, String> external_to_internal_key_map = new HashMap();
/*   48 */   private PluginConfigSourceImpl external_source = null;
/*      */   private static Map fake_values_when_disabled;
/*      */   private static int fake_values_ref_count;
/*      */   
/*   52 */   static { external_to_internal_key_map.put("Max Upload Speed KBs", "Max Upload Speed KBs");
/*   53 */     external_to_internal_key_map.put("Max Upload Speed When Only Seeding KBs", "Max Upload Speed Seeding KBs");
/*   54 */     external_to_internal_key_map.put("Max Download Speed KBs", "Max Download Speed KBs");
/*   55 */     external_to_internal_key_map.put("Max Connections Global", "Max.Peer.Connections.Total");
/*   56 */     external_to_internal_key_map.put("Max Connections Per Torrent", "Max.Peer.Connections.Per.Torrent");
/*   57 */     external_to_internal_key_map.put("Max Downloads", "max downloads");
/*   58 */     external_to_internal_key_map.put("Max Active Torrents", "max active torrents");
/*   59 */     external_to_internal_key_map.put("Max Active Torrents When Only Seeding", "StartStopManager_iMaxActiveTorrentsWhenSeeding");
/*   60 */     external_to_internal_key_map.put("Max Uploads", "Max Uploads");
/*   61 */     external_to_internal_key_map.put("Max Uploads Seeding", "Max Uploads Seeding");
/*   62 */     external_to_internal_key_map.put("Max Upload Speed When Only Seeding Enabled", "enable.seedingonly.upload.rate");
/*   63 */     external_to_internal_key_map.put("Max Active Torrents When Only Seeding Enabled", "StartStopManager_bMaxActiveTorrentsWhenSeedingEnabled");
/*   64 */     external_to_internal_key_map.put("Auto Upload Speed Enabled", "Auto Upload Speed Enabled");
/*   65 */     external_to_internal_key_map.put("Auto Upload Speed Seeding Enabled", "Auto Upload Speed Seeding Enabled");
/*   66 */     external_to_internal_key_map.put("SOCKS Proxy No Inward Connection", "Proxy.Data.SOCKS.inform");
/*   67 */     external_to_internal_key_map.put("Newly Seeding Torrents Get First Priority", "Newly Seeding Torrents Get First Priority");
/*   68 */     external_to_internal_key_map.put("CORE_PARAM_STRING_LOCAL_BIND_IP", "Bind IP");
/*   69 */     external_to_internal_key_map.put("CORE_PARAM_BOOLEAN_FRIENDLY_HASH_CHECKING", "diskmanager.friendly.hashchecking");
/*   70 */     external_to_internal_key_map.put("GUI_PARAM_INT_SWT_REFRESH_IN_MS", "GUI Refresh");
/*   71 */     external_to_internal_key_map.put("CORE_PARAM_BOOLEAN_NEW_TORRENTS_START_AS_STOPPED", "Default Start Torrents Stopped");
/*   72 */     external_to_internal_key_map.put("Incoming TCP Port", "TCP.Listen.Port");
/*   73 */     external_to_internal_key_map.put("Incoming UDP Port", "UDP.Listen.Port");
/*   74 */     external_to_internal_key_map.put("Default save path", "Default save path");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   81 */     String[] passthrough_params = { "Open MyTorrents", "IconBar.enabled", "Wizard Completed", "welcome.version.lastshown" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*   86 */     for (int i = 0; i < passthrough_params.length; i++) {
/*   87 */       external_to_internal_key_map.put(passthrough_params[i], passthrough_params[i]);
/*      */     }
/*      */   }
/*      */   
/*      */   public void checkValidCoreParam(String name) {
/*   92 */     if (!external_to_internal_key_map.containsKey(name)) {
/*   93 */       throw new IllegalArgumentException("invalid core parameter: " + name);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setEnablePluginCoreConfigChange(boolean enabled)
/*      */   {
/*  104 */     synchronized (PluginConfigImpl.class)
/*      */     {
/*  106 */       if (enabled)
/*      */       {
/*  108 */         fake_values_ref_count -= 1;
/*      */         
/*  110 */         if (fake_values_ref_count == 0)
/*      */         {
/*      */ 
/*      */ 
/*  114 */           fake_values_when_disabled = null;
/*      */         }
/*      */       }
/*      */       else {
/*  118 */         fake_values_ref_count += 1;
/*      */         
/*  120 */         if (fake_values_ref_count == 1)
/*      */         {
/*  122 */           fake_values_when_disabled = new HashMap();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static Object getFakeValueWhenDisabled(String key, String name)
/*      */   {
/*  133 */     if (name.startsWith(key)) {
/*  134 */       return null;
/*      */     }
/*      */     
/*  137 */     synchronized (PluginConfigImpl.class)
/*      */     {
/*  139 */       if (fake_values_when_disabled != null)
/*      */       {
/*  141 */         return fake_values_when_disabled.get(name);
/*      */       }
/*      */     }
/*      */     
/*  145 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static boolean setFakeValueWhenDisabled(String key, String name, Object value)
/*      */   {
/*  154 */     if (name.startsWith(key)) {
/*  155 */       return false;
/*      */     }
/*      */     
/*  158 */     synchronized (PluginConfigImpl.class)
/*      */     {
/*  160 */       if (fake_values_when_disabled != null)
/*      */       {
/*  162 */         fake_values_when_disabled.put(name, value);
/*      */         
/*  164 */         return true;
/*      */       }
/*      */     }
/*      */     
/*  168 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PluginConfigImpl(PluginInterface _plugin_interface, String _key)
/*      */   {
/*  180 */     this.plugin_interface = _plugin_interface;
/*      */     
/*  182 */     this.key = (_key + ".");
/*  183 */     this.allow_key_modification = true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isNewInstall()
/*      */   {
/*  189 */     return COConfigurationManager.isNewInstall();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getPluginConfigKeyPrefix()
/*      */   {
/*  195 */     return this.key;
/*      */   }
/*      */   
/*      */   public void setPluginConfigKeyPrefix(String _key) {
/*  199 */     if (!this.allow_key_modification) {
/*  200 */       throw new RuntimeException("cannot modify key prefix - already in use");
/*      */     }
/*      */     
/*  203 */     if ((_key.length() > 0) || (this.plugin_interface.getPluginState().isBuiltIn())) {
/*  204 */       this.key = _key;
/*      */     } else {
/*  206 */       throw new RuntimeException("Can't set Plugin Config Key Prefix to '" + _key + "'");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean getBooleanParameter(String name, boolean _default, boolean map_name, boolean set_default)
/*      */   {
/*  217 */     Object obj = getFakeValueWhenDisabled(this.key, name);
/*  218 */     if (obj != null) {
/*  219 */       return ((Boolean)obj).booleanValue();
/*      */     }
/*  221 */     if (map_name) { name = mapKeyName(name, false);
/*      */     }
/*  223 */     notifyParamExists(name);
/*  224 */     if (set_default) { COConfigurationManager.setBooleanDefault(name, _default);
/*  225 */     } else if (!hasParameter(name)) return _default;
/*  226 */     return COConfigurationManager.getBooleanParameter(name);
/*      */   }
/*      */   
/*      */   private int[] getColorParameter(String name, int[] _default, boolean map_name, boolean set_default) {
/*  230 */     Object obj = getFakeValueWhenDisabled(this.key, name);
/*  231 */     if (obj != null) { return (int[])obj;
/*      */     }
/*  233 */     if (map_name) name = mapKeyName(name, false);
/*  234 */     int[] result = getColorParameter0(name, _default, set_default);
/*      */     
/*      */ 
/*  237 */     if (result == null) return null;
/*  238 */     if (result.length == 3) {
/*  239 */       int[] result2 = new int[4];
/*  240 */       System.arraycopy(result, 0, result2, 0, 3);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  245 */       result2[3] = getIntParameter(name, 0, false, false);
/*  246 */       result = result2;
/*      */     }
/*      */     
/*  249 */     return result;
/*      */   }
/*      */   
/*      */   private int[] getColorParameter0(String name, int[] _default, boolean set_default) {
/*  253 */     Object obj = getFakeValueWhenDisabled(this.key, name);
/*  254 */     if (obj != null) {
/*  255 */       return (int[])obj;
/*      */     }
/*      */     
/*  258 */     notifyRGBParamExists(name);
/*  259 */     if (set_default)
/*      */     {
/*      */ 
/*      */ 
/*  263 */       if (_default != null) {
/*  264 */         COConfigurationManager.setIntDefault(name + ".red", _default[0]);
/*  265 */         COConfigurationManager.setIntDefault(name + ".green", _default[1]);
/*  266 */         COConfigurationManager.setIntDefault(name + ".blue", _default[2]);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  271 */         throw new RuntimeException("color parameter default is null");
/*      */       }
/*      */       
/*      */     }
/*  275 */     else if (!hasParameter(name + ".red")) { return _default;
/*      */     }
/*  277 */     return new int[] { COConfigurationManager.getIntParameter(name + ".red"), COConfigurationManager.getIntParameter(name + ".green"), COConfigurationManager.getIntParameter(name + ".blue"), COConfigurationManager.getIntParameter(name + ".override") };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] getByteParameter(String name, byte[] _default, boolean map_name, boolean set_default)
/*      */   {
/*  286 */     Object obj = getFakeValueWhenDisabled(this.key, name);
/*  287 */     if (obj != null) {
/*  288 */       return (byte[])obj;
/*      */     }
/*  290 */     if (map_name) { name = mapKeyName(name, false);
/*      */     }
/*  292 */     notifyParamExists(name);
/*  293 */     if (set_default) { COConfigurationManager.setByteDefault(name, _default);
/*  294 */     } else if (!hasParameter(name)) return _default;
/*  295 */     return COConfigurationManager.getByteParameter(name);
/*      */   }
/*      */   
/*      */   private float getFloatParameter(String name, float _default, boolean map_name, boolean set_default) {
/*  299 */     Object obj = getFakeValueWhenDisabled(this.key, name);
/*  300 */     if (obj != null) {
/*  301 */       return ((Float)obj).floatValue();
/*      */     }
/*  303 */     if (map_name) { name = mapKeyName(name, false);
/*      */     }
/*  305 */     notifyParamExists(name);
/*  306 */     if (set_default) { COConfigurationManager.setFloatDefault(name, _default);
/*  307 */     } else if (!hasParameter(name)) return _default;
/*  308 */     return COConfigurationManager.getFloatParameter(name);
/*      */   }
/*      */   
/*      */   private int getIntParameter(String name, int _default, boolean map_name, boolean set_default) {
/*  312 */     Object obj = getFakeValueWhenDisabled(this.key, name);
/*  313 */     if (obj != null) {
/*  314 */       return ((Long)obj).intValue();
/*      */     }
/*  316 */     if (map_name) { name = mapKeyName(name, false);
/*      */     }
/*  318 */     notifyParamExists(name);
/*  319 */     if (set_default) { COConfigurationManager.setIntDefault(name, _default);
/*  320 */     } else if (!hasParameter(name)) return _default;
/*  321 */     return COConfigurationManager.getIntParameter(name);
/*      */   }
/*      */   
/*      */   private long getLongParameter(String name, long _default, boolean map_name, boolean set_default) {
/*  325 */     Object obj = getFakeValueWhenDisabled(this.key, name);
/*  326 */     if (obj != null) {
/*  327 */       return ((Long)obj).longValue();
/*      */     }
/*  329 */     if (map_name) { name = mapKeyName(name, false);
/*      */     }
/*  331 */     notifyParamExists(name);
/*  332 */     if (set_default) { COConfigurationManager.setLongDefault(name, _default);
/*  333 */     } else if (!hasParameter(name)) return _default;
/*  334 */     return COConfigurationManager.getLongParameter(name);
/*      */   }
/*      */   
/*      */   private String getStringParameter(String name, String _default, boolean map_name, boolean set_default) {
/*  338 */     Object obj = getFakeValueWhenDisabled(this.key, name);
/*  339 */     if (obj != null) {
/*  340 */       return (String)obj;
/*      */     }
/*  342 */     if (map_name) { name = mapKeyName(name, false);
/*      */     }
/*  344 */     notifyParamExists(name);
/*  345 */     if (set_default) { COConfigurationManager.setStringDefault(name, _default);
/*  346 */     } else if (!hasParameter(name)) return _default;
/*  347 */     return COConfigurationManager.getStringParameter(name);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean getDefaultedBooleanParameter(String name, boolean map_name)
/*      */   {
/*  356 */     Object obj = getFakeValueWhenDisabled(this.key, name);
/*  357 */     if (obj != null) {
/*  358 */       return ((Boolean)obj).booleanValue();
/*      */     }
/*  360 */     return getBooleanParameter(name, false, map_name, false);
/*      */   }
/*      */   
/*      */   private byte[] getDefaultedByteParameter(String name, boolean map_name) {
/*  364 */     return getByteParameter(name, ConfigurationDefaults.def_bytes, map_name, false);
/*      */   }
/*      */   
/*      */   private int[] getDefaultedColorParameter(String name, boolean map_name) {
/*  368 */     int[] default_value = { 0, 0, 0, 1 };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  374 */     return getColorParameter(name, default_value, map_name, false);
/*      */   }
/*      */   
/*      */   private float getDefaultedFloatParameter(String name, boolean map_name) {
/*  378 */     return getFloatParameter(name, 0.0F, map_name, false);
/*      */   }
/*      */   
/*      */   private int getDefaultedIntParameter(String name, boolean map_name) {
/*  382 */     return getIntParameter(name, 0, map_name, false);
/*      */   }
/*      */   
/*      */   private long getDefaultedLongParameter(String name, boolean map_name) {
/*  386 */     return getLongParameter(name, 0L, map_name, false);
/*      */   }
/*      */   
/*      */   private String getDefaultedStringParameter(String name, boolean map_name) {
/*  390 */     return getStringParameter(name, "", map_name, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public boolean getBooleanParameter(String name)
/*      */   {
/*  405 */     return getDefaultedBooleanParameter(name, true);
/*      */   }
/*      */   
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public boolean getBooleanParameter(String name, boolean default_value) {
/*  412 */     return getBooleanParameter(name, default_value, true, false);
/*      */   }
/*      */   
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public byte[] getByteParameter(String name) {
/*  419 */     return getDefaultedByteParameter(name, true);
/*      */   }
/*      */   
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public byte[] getByteParameter(String name, byte[] default_value) {
/*  426 */     return getByteParameter(name, default_value, true, false);
/*      */   }
/*      */   
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public float getFloatParameter(String name) {
/*  433 */     return getDefaultedFloatParameter(name, true);
/*      */   }
/*      */   
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public float getFloatParameter(String name, float default_value) {
/*  440 */     return getFloatParameter(name, default_value, true, false);
/*      */   }
/*      */   
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public int getIntParameter(String name) {
/*  447 */     return getDefaultedIntParameter(name, true);
/*      */   }
/*      */   
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public int getIntParameter(String name, int default_value) {
/*  454 */     return getIntParameter(name, default_value, true, false);
/*      */   }
/*      */   
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public long getLongParameter(String name) {
/*  461 */     return getDefaultedLongParameter(name, true);
/*      */   }
/*      */   
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public long getLongParameter(String name, long default_value) {
/*  468 */     return getLongParameter(name, default_value, true, false);
/*      */   }
/*      */   
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public String getStringParameter(String name) {
/*  475 */     return getDefaultedStringParameter(name, true);
/*      */   }
/*      */   
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public String getStringParameter(String name, String default_value) {
/*  482 */     return getStringParameter(name, default_value, true, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getCoreBooleanParameter(String name)
/*      */   {
/*  492 */     checkValidCoreParam(name);
/*  493 */     return getDefaultedBooleanParameter(name, true);
/*      */   }
/*      */   
/*      */   public boolean getCoreBooleanParameter(String name, boolean default_value) {
/*  497 */     checkValidCoreParam(name);
/*  498 */     return getBooleanParameter(name, default_value, true, false);
/*      */   }
/*      */   
/*      */   public byte[] getCoreByteParameter(String name) {
/*  502 */     checkValidCoreParam(name);
/*  503 */     return getDefaultedByteParameter(name, true);
/*      */   }
/*      */   
/*      */   public byte[] getCoreByteParameter(String name, byte[] default_value) {
/*  507 */     checkValidCoreParam(name);
/*  508 */     return getByteParameter(name, default_value, true, false);
/*      */   }
/*      */   
/*      */   public int[] getCoreColorParameter(String name) {
/*  512 */     checkValidCoreParam(name);
/*  513 */     return getDefaultedColorParameter(name, true);
/*      */   }
/*      */   
/*      */   public int[] getCoreColorParameter(String name, int[] default_value) {
/*  517 */     checkValidCoreParam(name);
/*  518 */     return getColorParameter(name, default_value, true, false);
/*      */   }
/*      */   
/*      */   public float getCoreFloatParameter(String name) {
/*  522 */     checkValidCoreParam(name);
/*  523 */     return getDefaultedFloatParameter(name, true);
/*      */   }
/*      */   
/*      */   public float getCoreFloatParameter(String name, float default_value) {
/*  527 */     checkValidCoreParam(name);
/*  528 */     return getFloatParameter(name, default_value, true, false);
/*      */   }
/*      */   
/*      */   public int getCoreIntParameter(String name) {
/*  532 */     checkValidCoreParam(name);
/*  533 */     return getDefaultedIntParameter(name, true);
/*      */   }
/*      */   
/*      */   public int getCoreIntParameter(String name, int default_value) {
/*  537 */     checkValidCoreParam(name);
/*  538 */     return getIntParameter(name, default_value, true, false);
/*      */   }
/*      */   
/*      */   public long getCoreLongParameter(String name) {
/*  542 */     checkValidCoreParam(name);
/*  543 */     return getDefaultedLongParameter(name, true);
/*      */   }
/*      */   
/*      */   public long getCoreLongParameter(String name, long default_value) {
/*  547 */     checkValidCoreParam(name);
/*  548 */     return getLongParameter(name, default_value, true, false);
/*      */   }
/*      */   
/*      */   public String getCoreStringParameter(String name) {
/*  552 */     checkValidCoreParam(name);
/*  553 */     return getDefaultedStringParameter(name, true);
/*      */   }
/*      */   
/*      */   public String getCoreStringParameter(String name, String default_value) {
/*  557 */     checkValidCoreParam(name);
/*  558 */     return getStringParameter(name, default_value, true, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setBooleanParameter(String name, boolean value)
/*      */   {
/*  567 */     if (setFakeValueWhenDisabled(this.key, name, Boolean.valueOf(value))) {
/*  568 */       return;
/*      */     }
/*  570 */     COConfigurationManager.setParameter(mapKeyName(name, true), value);
/*      */   }
/*      */   
/*      */   public void setByteParameter(String name, byte[] value) {
/*  574 */     if (setFakeValueWhenDisabled(this.key, name, value)) {
/*  575 */       return;
/*      */     }
/*  577 */     COConfigurationManager.setParameter(mapKeyName(name, true), value);
/*      */   }
/*      */   
/*      */   public void setColorParameter(String name, int[] value) {
/*  581 */     setColorParameter(name, value, true);
/*      */   }
/*      */   
/*      */   public void setColorParameter(String name, int[] value, boolean override) {
/*  585 */     if (setFakeValueWhenDisabled(this.key, name, value)) {
/*  586 */       return;
/*      */     }
/*  588 */     COConfigurationManager.setRGBParameter(mapKeyName(name, true), value, override);
/*      */   }
/*      */   
/*      */   public void setFloatParameter(String name, float value) {
/*  592 */     if (setFakeValueWhenDisabled(this.key, name, new Float(value))) {
/*  593 */       return;
/*      */     }
/*  595 */     COConfigurationManager.setParameter(mapKeyName(name, true), value);
/*      */   }
/*      */   
/*      */   public void setIntParameter(String name, int value) {
/*  599 */     if (setFakeValueWhenDisabled(this.key, name, new Long(value))) {
/*  600 */       return;
/*      */     }
/*  602 */     COConfigurationManager.setParameter(mapKeyName(name, true), value);
/*      */   }
/*      */   
/*      */   public void setLongParameter(String name, long value) {
/*  606 */     if (setFakeValueWhenDisabled(this.key, name, new Long(value))) {
/*  607 */       return;
/*      */     }
/*  609 */     COConfigurationManager.setParameter(mapKeyName(name, true), value);
/*      */   }
/*      */   
/*      */   public void setStringParameter(String name, String value) {
/*  613 */     if (setFakeValueWhenDisabled(this.key, name, value)) {
/*  614 */       return;
/*      */     }
/*  616 */     COConfigurationManager.setParameter(mapKeyName(name, true), value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setCoreBooleanParameter(String name, boolean value)
/*      */   {
/*  625 */     checkValidCoreParam(name);
/*  626 */     if (setFakeValueWhenDisabled(this.key, name, Boolean.valueOf(value))) {
/*  627 */       return;
/*      */     }
/*  629 */     COConfigurationManager.setParameter(mapKeyName(name, true), value);
/*      */   }
/*      */   
/*      */   public void setCoreByteParameter(String name, byte[] value) {
/*  633 */     checkValidCoreParam(name);
/*  634 */     if (setFakeValueWhenDisabled(this.key, name, value)) {
/*  635 */       return;
/*      */     }
/*  637 */     COConfigurationManager.setParameter(mapKeyName(name, true), value);
/*      */   }
/*      */   
/*      */   public void setCoreColorParameter(String name, int[] value) {
/*  641 */     setCoreColorParameter(name, value, true);
/*      */   }
/*      */   
/*      */   public void setCoreColorParameter(String name, int[] value, boolean override) {
/*  645 */     checkValidCoreParam(name);
/*  646 */     if (setFakeValueWhenDisabled(this.key, name, value)) {
/*  647 */       return;
/*      */     }
/*  649 */     COConfigurationManager.setRGBParameter(mapKeyName(name, true), value, override);
/*      */   }
/*      */   
/*      */   public void setCoreFloatParameter(String name, float value) {
/*  653 */     checkValidCoreParam(name);
/*  654 */     if (setFakeValueWhenDisabled(this.key, name, new Float(value))) {
/*  655 */       return;
/*      */     }
/*  657 */     COConfigurationManager.setParameter(mapKeyName(name, true), value);
/*      */   }
/*      */   
/*      */   public void setCoreIntParameter(String name, int value) {
/*  661 */     checkValidCoreParam(name);
/*  662 */     if (setFakeValueWhenDisabled(this.key, name, new Long(value))) {
/*  663 */       return;
/*      */     }
/*  665 */     COConfigurationManager.setParameter(mapKeyName(name, true), value);
/*      */   }
/*      */   
/*      */   public void setCoreLongParameter(String name, long value) {
/*  669 */     checkValidCoreParam(name);
/*  670 */     if (setFakeValueWhenDisabled(this.key, name, new Long(value))) {
/*  671 */       return;
/*      */     }
/*  673 */     COConfigurationManager.setParameter(mapKeyName(name, true), value);
/*      */   }
/*      */   
/*      */   public void setCoreStringParameter(String name, String value) {
/*  677 */     checkValidCoreParam(name);
/*  678 */     if (setFakeValueWhenDisabled(this.key, name, value)) {
/*  679 */       return;
/*      */     }
/*  681 */     COConfigurationManager.setParameter(mapKeyName(name, true), value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getPluginBooleanParameter(String name)
/*      */   {
/*  691 */     return getDefaultedBooleanParameter(this.key + name, false);
/*      */   }
/*      */   
/*      */   public boolean getPluginBooleanParameter(String name, boolean default_value) {
/*  695 */     return getBooleanParameter(this.key + name, default_value, false, true);
/*      */   }
/*      */   
/*      */   public byte[] getPluginByteParameter(String name) {
/*  699 */     return getDefaultedByteParameter(this.key + name, false);
/*      */   }
/*      */   
/*      */   public byte[] getPluginByteParameter(String name, byte[] default_value) {
/*  703 */     return getByteParameter(this.key + name, default_value, false, true);
/*      */   }
/*      */   
/*      */   public int[] getPluginColorParameter(String name) {
/*  707 */     return getDefaultedColorParameter(this.key + name, false);
/*      */   }
/*      */   
/*      */   public int[] getPluginColorParameter(String name, int[] default_value) {
/*  711 */     return getColorParameter(this.key + name, default_value, false, true);
/*      */   }
/*      */   
/*      */   public float getPluginFloatParameter(String name) {
/*  715 */     return getDefaultedFloatParameter(this.key + name, false);
/*      */   }
/*      */   
/*      */   public float getPluginFloatParameter(String name, float default_value) {
/*  719 */     return getFloatParameter(this.key + name, default_value, false, true);
/*      */   }
/*      */   
/*      */   public int getPluginIntParameter(String name) {
/*  723 */     return getDefaultedIntParameter(this.key + name, false);
/*      */   }
/*      */   
/*      */   public int getPluginIntParameter(String name, int default_value) {
/*  727 */     return getIntParameter(this.key + name, default_value, false, true);
/*      */   }
/*      */   
/*      */   public long getPluginLongParameter(String name) {
/*  731 */     return getDefaultedLongParameter(this.key + name, false);
/*      */   }
/*      */   
/*      */   public long getPluginLongParameter(String name, long default_value) {
/*  735 */     return getLongParameter(this.key + name, default_value, false, true);
/*      */   }
/*      */   
/*      */   public String getPluginStringParameter(String name) {
/*  739 */     return getDefaultedStringParameter(this.key + name, false);
/*      */   }
/*      */   
/*      */   public String getPluginStringParameter(String name, String default_value) {
/*  743 */     return getStringParameter(this.key + name, default_value, false, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPluginParameter(String name, boolean value)
/*      */   {
/*  752 */     notifyParamExists(this.key + name);
/*  753 */     COConfigurationManager.setParameter(this.key + name, value);
/*      */   }
/*      */   
/*      */   public void setPluginParameter(String name, byte[] value) {
/*  757 */     notifyParamExists(this.key + name);
/*  758 */     COConfigurationManager.setParameter(this.key + name, value);
/*      */   }
/*      */   
/*      */   public void setPluginParameter(String name, float value) {
/*  762 */     notifyParamExists(this.key + name);
/*  763 */     COConfigurationManager.setParameter(this.key + name, value);
/*      */   }
/*      */   
/*      */   public void setPluginParameter(String name, int value) {
/*  767 */     notifyParamExists(this.key + name);
/*  768 */     COConfigurationManager.setParameter(this.key + name, value);
/*      */   }
/*      */   
/*      */   public void setPluginParameter(String name, long value) {
/*  772 */     notifyParamExists(this.key + name);
/*  773 */     COConfigurationManager.setParameter(this.key + name, value);
/*      */   }
/*      */   
/*      */   public void setPluginParameter(String name, String value) {
/*  777 */     notifyParamExists(this.key + name);
/*  778 */     COConfigurationManager.setParameter(this.key + name, value);
/*      */   }
/*      */   
/*      */   public void setPluginColorParameter(String name, int[] value) {
/*  782 */     setPluginColorParameter(name, value, true);
/*      */   }
/*      */   
/*      */   public void setPluginColorParameter(String name, int[] value, boolean override) {
/*  786 */     notifyParamExists(this.key + name);
/*  787 */     COConfigurationManager.setRGBParameter(this.key + name, value, override);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getUnsafeBooleanParameter(String name)
/*      */   {
/*  797 */     return getDefaultedBooleanParameter(name, false);
/*      */   }
/*      */   
/*      */   public boolean getUnsafeBooleanParameter(String name, boolean default_value) {
/*  801 */     return getBooleanParameter(name, default_value, false, false);
/*      */   }
/*      */   
/*      */   public byte[] getUnsafeByteParameter(String name) {
/*  805 */     return getDefaultedByteParameter(name, false);
/*      */   }
/*      */   
/*      */   public byte[] getUnsafeByteParameter(String name, byte[] default_value) {
/*  809 */     return getByteParameter(name, default_value, false, false);
/*      */   }
/*      */   
/*      */   public int[] getUnsafeColorParameter(String name) {
/*  813 */     return getDefaultedColorParameter(name, false);
/*      */   }
/*      */   
/*      */   public int[] getUnsafeColorParameter(String name, int[] default_value) {
/*  817 */     return getColorParameter(name, default_value, false, false);
/*      */   }
/*      */   
/*      */   public float getUnsafeFloatParameter(String name) {
/*  821 */     return getDefaultedFloatParameter(name, false);
/*      */   }
/*      */   
/*      */   public float getUnsafeFloatParameter(String name, float default_value) {
/*  825 */     return getFloatParameter(name, default_value, false, false);
/*      */   }
/*      */   
/*      */   public int getUnsafeIntParameter(String name) {
/*  829 */     return getDefaultedIntParameter(name, false);
/*      */   }
/*      */   
/*      */   public int getUnsafeIntParameter(String name, int default_value) {
/*  833 */     return getIntParameter(name, default_value, false, false);
/*      */   }
/*      */   
/*      */   public long getUnsafeLongParameter(String name) {
/*  837 */     return getDefaultedLongParameter(name, false);
/*      */   }
/*      */   
/*      */   public long getUnsafeLongParameter(String name, long default_value) {
/*  841 */     return getLongParameter(name, default_value, false, false);
/*      */   }
/*      */   
/*      */   public String getUnsafeStringParameter(String name) {
/*  845 */     return getDefaultedStringParameter(name, false);
/*      */   }
/*      */   
/*      */   public String getUnsafeStringParameter(String name, String default_value) {
/*  849 */     return getStringParameter(name, default_value, false, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setUnsafeBooleanParameter(String name, boolean value)
/*      */   {
/*  858 */     if (setFakeValueWhenDisabled(this.key, name, Boolean.valueOf(value))) {
/*  859 */       return;
/*      */     }
/*  861 */     notifyParamExists(name);
/*  862 */     COConfigurationManager.setParameter(name, value);
/*      */   }
/*      */   
/*      */   public void setUnsafeByteParameter(String name, byte[] value) {
/*  866 */     if (setFakeValueWhenDisabled(this.key, name, value)) {
/*  867 */       return;
/*      */     }
/*  869 */     notifyParamExists(name);
/*  870 */     COConfigurationManager.setParameter(name, value);
/*      */   }
/*      */   
/*      */   public void setUnsafeColorParameter(String name, int[] value) {
/*  874 */     setUnsafeColorParameter(name, value, true);
/*      */   }
/*      */   
/*      */   public void setUnsafeColorParameter(String name, int[] value, boolean override) {
/*  878 */     if (setFakeValueWhenDisabled(this.key, name, value)) {
/*  879 */       return;
/*      */     }
/*  881 */     notifyRGBParamExists(name);
/*  882 */     COConfigurationManager.setRGBParameter(name, value, override);
/*      */   }
/*      */   
/*      */   public void setUnsafeFloatParameter(String name, float value) {
/*  886 */     if (setFakeValueWhenDisabled(this.key, name, new Float(value))) {
/*  887 */       return;
/*      */     }
/*  889 */     notifyParamExists(name);
/*  890 */     COConfigurationManager.setParameter(name, value);
/*      */   }
/*      */   
/*      */   public void setUnsafeIntParameter(String name, int value) {
/*  894 */     if (setFakeValueWhenDisabled(this.key, name, new Long(value))) {
/*  895 */       return;
/*      */     }
/*  897 */     notifyParamExists(name);
/*  898 */     COConfigurationManager.setParameter(name, value);
/*      */   }
/*      */   
/*      */   public void setUnsafeLongParameter(String name, long value) {
/*  902 */     if (setFakeValueWhenDisabled(this.key, name, new Long(value))) {
/*  903 */       return;
/*      */     }
/*  905 */     notifyParamExists(name);
/*  906 */     COConfigurationManager.setParameter(name, value);
/*      */   }
/*      */   
/*      */   public void setUnsafeStringParameter(String name, String value) {
/*  910 */     if (setFakeValueWhenDisabled(this.key, name, value)) {
/*  911 */       return;
/*      */     }
/*  913 */     notifyParamExists(name);
/*  914 */     COConfigurationManager.setParameter(name, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] getPluginStringListParameter(String key)
/*      */   {
/*  924 */     notifyParamExists(this.key + key);
/*  925 */     return COConfigurationManager.getStringListParameter(this.key + key).toArray();
/*      */   }
/*      */   
/*      */   public void setPluginStringListParameter(String key, String[] value) {
/*  929 */     notifyParamExists(this.key + key);
/*  930 */     StringListImpl list_obj = new StringListImpl(Arrays.asList(value));
/*  931 */     COConfigurationManager.setParameter(this.key + key, list_obj);
/*      */   }
/*      */   
/*      */   public List getPluginListParameter(String key, List default_value) {
/*  935 */     notifyParamExists(this.key + key);
/*  936 */     return COConfigurationManager.getListParameter(this.key + key, default_value);
/*      */   }
/*      */   
/*      */   public void setPluginListParameter(String key, List value) {
/*  940 */     notifyParamExists(this.key + key);
/*  941 */     COConfigurationManager.setParameter(this.key + key, value);
/*      */   }
/*      */   
/*      */   public Map getPluginMapParameter(String key, Map default_value) {
/*  945 */     notifyParamExists(this.key + key);
/*  946 */     return COConfigurationManager.getMapParameter(this.key + key, default_value);
/*      */   }
/*      */   
/*      */   public void setPluginMapParameter(String key, Map value) {
/*  950 */     notifyParamExists(this.key + key);
/*  951 */     COConfigurationManager.setParameter(this.key + key, value);
/*      */   }
/*      */   
/*      */   public void setPluginParameter(String key, int value, boolean global) {
/*  955 */     notifyParamExists(this.key + key);
/*  956 */     COConfigurationManager.setParameter(this.key + key, value);
/*  957 */     if (global) {
/*  958 */       MagnetURIHandler.getSingleton().addInfo(this.key + key, value);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ConfigParameter getParameter(String key)
/*      */   {
/*  966 */     return new ConfigParameterImpl(mapKeyName(key, false));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ConfigParameter getPluginParameter(String key)
/*      */   {
/*  973 */     return new ConfigParameterImpl(this.key + key);
/*      */   }
/*      */   
/*      */   public boolean removePluginParameter(String key) {
/*  977 */     notifyParamExists(this.key + key);
/*  978 */     return COConfigurationManager.removeParameter(this.key + key);
/*      */   }
/*      */   
/*      */   public boolean removePluginColorParameter(String key) {
/*  982 */     notifyParamExists(this.key + key);
/*  983 */     return COConfigurationManager.removeRGBParameter(this.key + key);
/*      */   }
/*      */   
/*      */ 
/*      */   public Map getUnsafeParameterList()
/*      */   {
/*  989 */     Set params = COConfigurationManager.getAllowedParameters();
/*      */     
/*  991 */     Iterator it = params.iterator();
/*      */     
/*  993 */     Map result = new HashMap();
/*      */     
/*  995 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/*  998 */         String name = (String)it.next();
/*      */         
/* 1000 */         Object val = COConfigurationManager.getParameter(name);
/*      */         
/* 1002 */         if ((!(val instanceof String)) && (!(val instanceof Long)))
/*      */         {
/* 1004 */           if ((val instanceof byte[]))
/*      */           {
/* 1006 */             val = new String((byte[])val, "UTF-8");
/*      */           }
/* 1008 */           else if ((val instanceof Integer))
/*      */           {
/* 1010 */             val = new Long(((Integer)val).intValue());
/*      */           }
/* 1012 */           else if ((val instanceof List))
/*      */           {
/* 1014 */             val = null;
/*      */           }
/* 1016 */           else if ((val instanceof Map))
/*      */           {
/* 1018 */             val = null;
/*      */           }
/* 1020 */           else if ((val instanceof Boolean))
/*      */           {
/* 1022 */             val = new Long(((Boolean)val).booleanValue() ? 1L : 0L);
/*      */           }
/* 1024 */           else if (((val instanceof Float)) || ((val instanceof Double)))
/*      */           {
/* 1026 */             val = val.toString();
/*      */           }
/*      */         }
/* 1029 */         if (val != null)
/*      */         {
/* 1031 */           result.put(name, val);
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1035 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 1039 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private PluginInterface plugin_interface;
/*      */   
/*      */ 
/*      */ 
/*      */   private String key;
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean allow_key_modification;
/*      */   
/*      */ 
/*      */ 
/*      */   public File getPluginUserFile(String name)
/*      */   {
/* 1059 */     String dir = this.plugin_interface.getUtilities().getAzureusUserDir();
/*      */     
/* 1061 */     File file = new File(dir, "plugins");
/*      */     
/* 1063 */     String p_dir = this.plugin_interface.getPluginDirectoryName();
/*      */     
/* 1065 */     if (p_dir.length() != 0)
/*      */     {
/* 1067 */       int lp = p_dir.lastIndexOf(File.separatorChar);
/*      */       
/* 1069 */       if (lp != -1)
/*      */       {
/* 1071 */         p_dir = p_dir.substring(lp + 1);
/*      */       }
/*      */       
/* 1074 */       file = new File(file, p_dir);
/*      */     }
/*      */     else
/*      */     {
/* 1078 */       String id = this.plugin_interface.getPluginID();
/*      */       
/* 1080 */       if ((id.length() > 0) && (!id.equals("<internal>")))
/*      */       {
/* 1082 */         file = new File(file, id);
/*      */       }
/*      */       else
/*      */       {
/* 1086 */         throw new RuntimeException("Plugin was not loaded from a directory");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1091 */     FileUtil.mkdirs(file);
/*      */     
/* 1093 */     return new File(file, name);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(final PluginConfigListener l)
/*      */   {
/* 1100 */     COConfigurationManager.addListener(new COConfigurationListener()
/*      */     {
/*      */ 
/*      */       public void configurationSaved()
/*      */       {
/*      */ 
/* 1106 */         l.configSaved();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private String mapKeyName(String key, boolean for_set) {
/* 1112 */     String result = (String)external_to_internal_key_map.get(key);
/* 1113 */     if (result == null) {
/* 1114 */       if (for_set) {
/* 1115 */         throw new RuntimeException("No permission to set the value of core parameter: " + key);
/*      */       }
/*      */       
/* 1118 */       return key;
/*      */     }
/*      */     
/* 1121 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasParameter(String param_name)
/*      */   {
/* 1127 */     return COConfigurationManager.hasParameter(param_name, false);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasPluginParameter(String param_name)
/*      */   {
/* 1133 */     notifyParamExists(this.key + param_name);
/* 1134 */     return COConfigurationManager.hasParameter(this.key + param_name, true);
/*      */   }
/*      */   
/*      */   public void notifyRGBParamExists(String param) {
/* 1138 */     notifyParamExists(param + ".red");
/* 1139 */     notifyParamExists(param + ".blue");
/* 1140 */     notifyParamExists(param + ".green");
/* 1141 */     notifyParamExists(param + ".override");
/*      */   }
/*      */   
/*      */   public void notifyParamExists(String param)
/*      */   {
/* 1146 */     if ((this.allow_key_modification) && (param.startsWith(this.key))) {
/* 1147 */       this.allow_key_modification = false;
/*      */     }
/* 1149 */     if ((this.external_source != null) && (param.startsWith(this.key))) {
/* 1150 */       this.external_source.registerParameter(param);
/*      */     }
/*      */   }
/*      */   
/*      */   public PluginConfigSource enableExternalConfigSource() {
/* 1155 */     PluginConfigSourceImpl source = new PluginConfigSourceImpl(this, this.plugin_interface.getPluginID());
/* 1156 */     setPluginConfigSource(source);
/* 1157 */     return source;
/*      */   }
/*      */   
/*      */   public PluginConfigSource getPluginConfigSource() {
/* 1161 */     return this.external_source;
/*      */   }
/*      */   
/*      */   public void setPluginConfigSource(PluginConfigSource source) {
/* 1165 */     if (this.external_source != null) {
/* 1166 */       throw new RuntimeException("external config source already associated!");
/*      */     }
/*      */     
/*      */ 
/* 1170 */     PluginConfigSourceImpl source_impl = (PluginConfigSourceImpl)source;
/* 1171 */     String used_key = source_impl.getUsedKeyPrefix();
/* 1172 */     if ((used_key != null) && (!getPluginConfigKeyPrefix().startsWith(used_key))) {
/* 1173 */       throw new RuntimeException("cannot use this config source object - incompatible prefix keys: " + used_key + " / " + getPluginConfigKeyPrefix());
/*      */     }
/* 1175 */     this.external_source = ((PluginConfigSourceImpl)source);
/*      */   }
/*      */   
/*      */   public void save() {}
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/PluginConfigImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */