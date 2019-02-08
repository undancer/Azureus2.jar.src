/*     */ package org.gudy.azureus2.pluginsimpl.local.ui.model;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Properties;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.plugins.PluginConfig;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.components.UITextArea;
/*     */ import org.gudy.azureus2.plugins.ui.config.ActionParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.ColorParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.DirectoryParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.HyperlinkParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.InfoParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.IntParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.LabelParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.ParameterGroup;
/*     */ import org.gudy.azureus2.plugins.ui.config.ParameterTabFolder;
/*     */ import org.gudy.azureus2.plugins.ui.config.PasswordParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.StringListParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.StringParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.UIParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.UIParameterContext;
/*     */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*     */ import org.gudy.azureus2.plugins.utils.LocaleUtilities;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginConfigImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.UIManagerImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ActionParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.BooleanParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ColorParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.DirectoryParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.HyperlinkParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.InfoParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.IntParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.LabelParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ParameterGroupImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ParameterTabFolderImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.PasswordParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.StringListParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.StringParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.UIParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.UITextAreaImpl;
/*     */ 
/*     */ public class BasicPluginConfigModelImpl implements BasicPluginConfigModel
/*     */ {
/*     */   private UIManagerImpl ui_manager;
/*     */   private String parent_section;
/*     */   private String section;
/*     */   private PluginInterface pi;
/*  53 */   private ArrayList<Parameter> parameters = new ArrayList();
/*     */   
/*     */ 
/*     */   private String key_prefix;
/*     */   
/*     */ 
/*     */   private PluginConfigImpl configobj;
/*     */   
/*     */ 
/*     */ 
/*     */   public BasicPluginConfigModelImpl(UIManagerImpl _ui_manager, String _parent_section, String _section)
/*     */   {
/*  65 */     this.ui_manager = _ui_manager;
/*  66 */     this.parent_section = _parent_section;
/*  67 */     this.section = _section;
/*     */     
/*  69 */     this.pi = this.ui_manager.getPluginInterface();
/*     */     
/*  71 */     this.key_prefix = this.pi.getPluginconfig().getPluginConfigKeyPrefix();
/*  72 */     this.configobj = ((PluginConfigImpl)this.pi.getPluginconfig());
/*     */     
/*  74 */     if ((this.parent_section != null) && (!this.parent_section.equals("root")))
/*     */     {
/*  76 */       String version = this.pi.getPluginVersion();
/*     */       
/*  78 */       addLabelParameter2("!" + MessageText.getString("ConfigView.pluginlist.column.version") + ": " + (version == null ? "<local>" : version) + "!");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getParentSection()
/*     */   {
/*  85 */     return this.parent_section;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getSection()
/*     */   {
/*  91 */     return this.section;
/*     */   }
/*     */   
/*     */ 
/*     */   public PluginInterface getPluginInterface()
/*     */   {
/*  97 */     return this.pi;
/*     */   }
/*     */   
/*     */ 
/*     */   public Parameter[] getParameters()
/*     */   {
/* 103 */     Parameter[] res = new Parameter[this.parameters.size()];
/*     */     
/* 105 */     this.parameters.toArray(res);
/*     */     
/* 107 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addBooleanParameter(String key, String resource_name, boolean defaultValue)
/*     */   {
/* 116 */     addBooleanParameter2(key, resource_name, defaultValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BooleanParameter addBooleanParameter2(String key, String resource_name, boolean defaultValue)
/*     */   {
/* 125 */     BooleanParameterImpl res = new BooleanParameterImpl(this.configobj, resolveKey(key), resource_name, defaultValue);
/*     */     
/* 127 */     this.parameters.add(res);
/*     */     
/* 129 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addStringParameter(String key, String resource_name, String defaultValue)
/*     */   {
/* 138 */     addStringParameter2(key, resource_name, defaultValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public StringParameter addStringParameter2(String key, String resource_name, String defaultValue)
/*     */   {
/* 147 */     StringParameterImpl res = new StringParameterImpl(this.configobj, resolveKey(key), resource_name, defaultValue);
/*     */     
/* 149 */     this.parameters.add(res);
/*     */     
/* 151 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public StringListParameter addStringListParameter2(String key, String resource_name, String[] values, String defaultValue)
/*     */   {
/* 161 */     StringListParameterImpl res = new StringListParameterImpl(this.configobj, resolveKey(key), resource_name, defaultValue, values, values);
/*     */     
/* 163 */     this.parameters.add(res);
/*     */     
/* 165 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public StringListParameter addStringListParameter2(String key, String resource_name, String[] values, String[] labels, String defaultValue)
/*     */   {
/* 176 */     StringListParameterImpl res = new StringListParameterImpl(this.configobj, resolveKey(key), resource_name, defaultValue, values, labels);
/*     */     
/*     */ 
/*     */ 
/* 180 */     this.parameters.add(res);
/*     */     
/* 182 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PasswordParameter addPasswordParameter2(String key, String resource_name, int encoding_type, byte[] defaultValue)
/*     */   {
/* 192 */     PasswordParameterImpl res = new PasswordParameterImpl(this.configobj, resolveKey(key), resource_name, encoding_type, defaultValue);
/*     */     
/* 194 */     this.parameters.add(res);
/*     */     
/* 196 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IntParameter addIntParameter2(String key, String resource_name, int defaultValue)
/*     */   {
/* 205 */     IntParameterImpl res = new IntParameterImpl(this.configobj, resolveKey(key), resource_name, defaultValue);
/*     */     
/* 207 */     this.parameters.add(res);
/*     */     
/* 209 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IntParameter addIntParameter2(String key, String resource_name, int defaultValue, int min_value, int max_value)
/*     */   {
/* 220 */     IntParameterImpl res = new IntParameterImpl(this.configobj, resolveKey(key), resource_name, defaultValue, min_value, max_value);
/* 221 */     this.parameters.add(res);
/* 222 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DirectoryParameter addDirectoryParameter2(String key, String resource_name, String defaultValue)
/*     */   {
/* 231 */     DirectoryParameterImpl res = new DirectoryParameterImpl(this.configobj, resolveKey(key), resource_name, defaultValue);
/*     */     
/* 233 */     this.parameters.add(res);
/*     */     
/* 235 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public org.gudy.azureus2.plugins.ui.config.FileParameter addFileParameter2(String key, String resource_name, String defaultValue)
/*     */   {
/* 243 */     return addFileParameter2(key, resource_name, defaultValue, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public org.gudy.azureus2.plugins.ui.config.FileParameter addFileParameter2(String key, String resource_name, String defaultValue, String[] file_extensions)
/*     */   {
/* 252 */     org.gudy.azureus2.pluginsimpl.local.ui.config.FileParameter res = new org.gudy.azureus2.pluginsimpl.local.ui.config.FileParameter(this.configobj, resolveKey(key), resource_name, defaultValue, file_extensions);
/* 253 */     this.parameters.add(res);
/* 254 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public LabelParameter addLabelParameter2(String resource_name)
/*     */   {
/* 262 */     LabelParameterImpl res = new LabelParameterImpl(this.configobj, this.key_prefix, resource_name);
/*     */     
/* 264 */     this.parameters.add(res);
/*     */     
/* 266 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public InfoParameter addInfoParameter2(String resource_name, String value)
/*     */   {
/* 274 */     InfoParameterImpl res = new InfoParameterImpl(this.configobj, resolveKey(resource_name), resource_name, value);
/*     */     
/* 276 */     this.parameters.add(res);
/*     */     
/* 278 */     return res;
/*     */   }
/*     */   
/*     */   public HyperlinkParameter addHyperlinkParameter2(String resource_name, String url_location)
/*     */   {
/* 283 */     HyperlinkParameterImpl res = new HyperlinkParameterImpl(this.configobj, this.key_prefix, resource_name, url_location);
/* 284 */     this.parameters.add(res);
/* 285 */     return res;
/*     */   }
/*     */   
/*     */   public ColorParameter addColorParameter2(String key, String resource_name, int r, int g, int b)
/*     */   {
/* 290 */     ColorParameterImpl res = new ColorParameterImpl(this.configobj, resolveKey(key), resource_name, r, g, b);
/* 291 */     this.parameters.add(res);
/* 292 */     return res;
/*     */   }
/*     */   
/*     */   public UIParameter addUIParameter2(UIParameterContext context, String resource_name)
/*     */   {
/* 297 */     UIParameterImpl res = new UIParameterImpl(this.configobj, context, this.key_prefix, resource_name);
/* 298 */     this.parameters.add(res);
/* 299 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ActionParameter addActionParameter2(String label_resource_name, String action_resource_name)
/*     */   {
/* 307 */     ActionParameterImpl res = new ActionParameterImpl(this.configobj, label_resource_name, action_resource_name);
/*     */     
/* 309 */     this.parameters.add(res);
/*     */     
/* 311 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public UITextArea addTextArea(String resource_name)
/*     */   {
/* 318 */     UITextAreaImpl res = new UITextAreaImpl(this.configobj, resource_name);
/*     */     
/* 320 */     this.parameters.add(res);
/*     */     
/* 322 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ParameterGroup createGroup(String _resource_name, Parameter[] _parameters)
/*     */   {
/* 330 */     ParameterGroupImpl pg = new ParameterGroupImpl(_resource_name, _parameters);
/*     */     
/* 332 */     return pg;
/*     */   }
/*     */   
/*     */ 
/*     */   public ParameterTabFolder createTabFolder()
/*     */   {
/* 338 */     return new ParameterTabFolderImpl();
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 344 */     this.ui_manager.destroy(this);
/*     */     
/* 346 */     for (int i = 0; i < this.parameters.size(); i++)
/*     */     {
/* 348 */       ((ParameterImpl)this.parameters.get(i)).destroy();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setLocalizedName(String name) {
/* 353 */     Properties props = new Properties();
/* 354 */     props.put("ConfigView.section." + this.section, name);
/* 355 */     this.pi.getUtilities().getLocaleUtilities().integrateLocalisedMessageBundle(props);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String resolveKey(String key)
/*     */   {
/* 362 */     if ((key.startsWith("!")) && (key.endsWith("!")))
/*     */     {
/* 364 */       return key.substring(1, key.length() - 1);
/*     */     }
/*     */     
/* 367 */     return this.key_prefix + key;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/model/BasicPluginConfigModelImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */