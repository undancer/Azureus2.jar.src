/*     */ package org.gudy.azureus2.pluginsimpl.local.config;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.LightHashSet;
/*     */ import org.gudy.azureus2.plugins.PluginConfig;
/*     */ import org.gudy.azureus2.plugins.config.PluginConfigSource;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PluginConfigSourceImpl
/*     */   implements COConfigurationListener, ParameterListener, PluginConfigSource
/*     */ {
/*     */   private PluginConfig plugin_config;
/*     */   private File source_file;
/*     */   private boolean initialised;
/*     */   private Map data_map;
/*  42 */   private String key_prefix = null;
/*  43 */   private boolean dirty = false;
/*  44 */   private boolean migrate_settings = false;
/*     */   private LightHashSet params_monitored;
/*     */   
/*     */   public PluginConfigSourceImpl(PluginConfig plugin_config, String plugin_id) {
/*  48 */     this.plugin_config = plugin_config;
/*  49 */     this.key_prefix = null;
/*  50 */     this.initialised = false;
/*  51 */     this.params_monitored = new LightHashSet();
/*  52 */     setConfigFilename(plugin_id + ".config");
/*     */   }
/*     */   
/*     */   public void initialize() {
/*  56 */     shouldBeInitialised(false);
/*  57 */     this.initialised = true;
/*  58 */     this.data_map = FileUtil.readResilientFile(this.source_file.getParentFile(), this.source_file.getName(), true);
/*  59 */     this.key_prefix = this.plugin_config.getPluginConfigKeyPrefix();
/*     */     
/*     */ 
/*     */ 
/*  63 */     Iterator itr = this.data_map.entrySet().iterator();
/*     */     
/*  65 */     ConfigurationManager config = ConfigurationManager.getInstance();
/*  66 */     while (itr.hasNext()) {
/*  67 */       Map.Entry entry = (Map.Entry)itr.next();
/*  68 */       String key = this.key_prefix + (String)entry.getKey();
/*  69 */       this.params_monitored.add(key);
/*  70 */       config.registerTransientParameter(key);
/*  71 */       config.setParameterRawNoNotify(key, entry.getValue());
/*  72 */       config.addParameterListener(key, this);
/*     */     }
/*     */     
/*  75 */     config.addListener(this);
/*     */   }
/*     */   
/*     */   public File getConfigFile() {
/*  79 */     return this.source_file;
/*     */   }
/*     */   
/*     */   public void setConfigFilename(String filename) {
/*  83 */     shouldBeInitialised(false);
/*  84 */     this.source_file = this.plugin_config.getPluginUserFile(FileUtil.convertOSSpecificChars(filename, false));
/*     */   }
/*     */   
/*     */   public void save(boolean force) {
/*  88 */     shouldBeInitialised(true);
/*  89 */     if ((!force) && (!this.dirty)) return;
/*  90 */     FileUtil.writeResilientFile(this.source_file.getParentFile(), this.source_file.getName(), this.data_map, true);
/*  91 */     this.dirty = false;
/*     */   }
/*     */   
/*     */   public void configurationSaved() {
/*  95 */     save(false);
/*     */   }
/*     */   
/*     */   public void parameterChanged(String full_param) {
/*  99 */     shouldBeInitialised(true);
/* 100 */     String plugin_param = toPluginName(full_param);
/* 101 */     if (COConfigurationManager.hasParameter(full_param, true)) {
/* 102 */       Object val = ConfigurationManager.getInstance().getParameter(full_param);
/* 103 */       this.data_map.put(plugin_param, val);
/*     */     }
/*     */     else {
/* 106 */       this.data_map.remove(plugin_param);
/*     */     }
/* 108 */     this.dirty = true;
/*     */   }
/*     */   
/*     */   public void registerParameter(String full_param)
/*     */   {
/* 113 */     shouldBeInitialised(true);
/* 114 */     if (!this.params_monitored.add(full_param)) return;
/* 115 */     ConfigurationManager config = ConfigurationManager.getInstance();
/* 116 */     config.registerTransientParameter(full_param);
/* 117 */     config.addParameterListener(full_param, this);
/* 118 */     if ((this.migrate_settings) && (COConfigurationManager.hasParameter(full_param, true))) {
/* 119 */       parameterChanged(full_param);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getUsedKeyPrefix()
/*     */   {
/* 125 */     return this.key_prefix;
/*     */   }
/*     */   
/*     */ 
/*     */   private String toPluginName(String name)
/*     */   {
/* 131 */     if (!name.startsWith(this.key_prefix)) {
/* 132 */       throw new RuntimeException("mismatch key prefix: " + name + ", " + this.key_prefix);
/*     */     }
/*     */     
/* 135 */     return name.substring(this.key_prefix.length());
/*     */   }
/*     */   
/*     */   private void shouldBeInitialised(boolean yes) {
/* 139 */     if ((yes) && (!this.initialised)) {
/* 140 */       throw new RuntimeException("source not yet initialised");
/*     */     }
/* 142 */     if ((!yes) && (this.initialised)) {
/* 143 */       throw new RuntimeException("source already initialised");
/*     */     }
/*     */   }
/*     */   
/*     */   public void forceSettingsMigration() {
/* 148 */     shouldBeInitialised(false);
/* 149 */     this.migrate_settings = true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/config/PluginConfigSourceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */