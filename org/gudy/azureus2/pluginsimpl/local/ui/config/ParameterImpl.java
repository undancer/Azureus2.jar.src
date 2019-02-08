/*     */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.config.ConfigParameterListener;
/*     */ import org.gudy.azureus2.plugins.ui.config.EnablerParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginConfigImpl;
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
/*     */ public class ParameterImpl
/*     */   implements EnablerParameter, org.gudy.azureus2.core3.config.ParameterListener
/*     */ {
/*     */   protected PluginConfigImpl config;
/*     */   private String key;
/*     */   private String labelKey;
/*     */   private String label;
/*  50 */   private int mode = 0;
/*     */   
/*  52 */   private boolean enabled = true;
/*  53 */   private boolean visible = true;
/*  54 */   private boolean generate_intermediate_events = true;
/*     */   
/*     */ 
/*     */   private List<Parameter> toDisable;
/*     */   
/*     */   private List<Parameter> toEnable;
/*     */   
/*     */   private List listeners;
/*     */   
/*     */   private List<ParameterImplListener> impl_listeners;
/*     */   
/*     */   private ParameterGroupImpl parameter_group;
/*     */   
/*     */ 
/*     */   public ParameterImpl(PluginConfigImpl _config, String _key, String _labelKey)
/*     */   {
/*  70 */     this.config = _config;
/*  71 */     this.key = _key;
/*  72 */     this.labelKey = _labelKey;
/*  73 */     if ("_blank".equals(this.labelKey)) {
/*  74 */       this.labelKey = "!!";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getKey()
/*     */   {
/*  82 */     return this.key;
/*     */   }
/*     */   
/*     */   public void addDisabledOnSelection(Parameter parameter) {
/*  86 */     if (this.toDisable == null) {
/*  87 */       this.toDisable = new ArrayList(1);
/*     */     }
/*  89 */     if ((parameter instanceof ParameterGroupImpl)) {
/*  90 */       ParameterImpl[] parameters = ((ParameterGroupImpl)parameter).getParameters();
/*  91 */       Collections.addAll(this.toDisable, parameters);
/*  92 */       return;
/*     */     }
/*  94 */     this.toDisable.add(parameter);
/*     */   }
/*     */   
/*     */   public void addEnabledOnSelection(Parameter parameter) {
/*  98 */     if (this.toEnable == null) {
/*  99 */       this.toEnable = new ArrayList(1);
/*     */     }
/* 101 */     if ((parameter instanceof ParameterGroupImpl)) {
/* 102 */       ParameterImpl[] parameters = ((ParameterGroupImpl)parameter).getParameters();
/* 103 */       Collections.addAll(this.toEnable, parameters);
/* 104 */       return;
/*     */     }
/* 106 */     this.toEnable.add(parameter);
/*     */   }
/*     */   
/*     */   public List getDisabledOnSelectionParameters() {
/* 110 */     return this.toDisable == null ? Collections.EMPTY_LIST : this.toDisable;
/*     */   }
/*     */   
/*     */   public List getEnabledOnSelectionParameters() {
/* 114 */     return this.toEnable == null ? Collections.EMPTY_LIST : this.toEnable;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void parameterChanged(String key)
/*     */   {
/* 121 */     fireParameterChanged();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void fireParameterChanged()
/*     */   {
/* 127 */     if (this.listeners == null) {
/* 128 */       return;
/*     */     }
/*     */     
/* 131 */     Object[] listenerArray = this.listeners.toArray();
/* 132 */     for (int i = 0; i < listenerArray.length; i++) {
/*     */       try {
/* 134 */         Object o = listenerArray[i];
/* 135 */         if ((o instanceof org.gudy.azureus2.plugins.ui.config.ParameterListener))
/*     */         {
/* 137 */           ((org.gudy.azureus2.plugins.ui.config.ParameterListener)o).parameterChanged(this);
/*     */         }
/*     */         else
/*     */         {
/* 141 */           ((ConfigParameterListener)o).configParameterChanged(this);
/*     */         }
/*     */       }
/*     */       catch (Throwable f) {
/* 145 */         Debug.printStackTrace(f);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setEnabled(boolean e)
/*     */   {
/* 154 */     this.enabled = e;
/*     */     
/* 156 */     if (this.impl_listeners == null) {
/* 157 */       return;
/*     */     }
/*     */     
/* 160 */     Object[] listenersArray = this.impl_listeners.toArray();
/* 161 */     for (int i = 0; i < listenersArray.length; i++) {
/*     */       try {
/* 163 */         ParameterImplListener l = (ParameterImplListener)listenersArray[i];
/* 164 */         l.enabledChanged(this);
/*     */       }
/*     */       catch (Throwable f) {
/* 167 */         Debug.printStackTrace(f);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 175 */     return this.enabled;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMinimumRequiredUserMode()
/*     */   {
/* 181 */     return this.mode;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMinimumRequiredUserMode(int _mode)
/*     */   {
/* 188 */     this.mode = _mode;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setVisible(boolean _visible)
/*     */   {
/* 195 */     this.visible = _visible;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isVisible()
/*     */   {
/* 201 */     return this.visible;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setGenerateIntermediateEvents(boolean b)
/*     */   {
/* 208 */     this.generate_intermediate_events = b;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getGenerateIntermediateEvents()
/*     */   {
/* 214 */     return this.generate_intermediate_events;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setGroup(ParameterGroupImpl _group)
/*     */   {
/* 221 */     this.parameter_group = _group;
/*     */   }
/*     */   
/*     */ 
/*     */   public ParameterGroupImpl getGroup()
/*     */   {
/* 227 */     return this.parameter_group;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(org.gudy.azureus2.plugins.ui.config.ParameterListener l)
/*     */   {
/* 234 */     if (this.listeners == null) {
/* 235 */       this.listeners = new ArrayList(1);
/*     */     }
/* 237 */     this.listeners.add(l);
/*     */     
/* 239 */     if (this.listeners.size() == 1)
/*     */     {
/* 241 */       COConfigurationManager.addParameterListener(this.key, this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(org.gudy.azureus2.plugins.ui.config.ParameterListener l)
/*     */   {
/* 249 */     if (this.listeners == null) {
/* 250 */       return;
/*     */     }
/* 252 */     this.listeners.remove(l);
/*     */     
/* 254 */     if (this.listeners.size() == 0)
/*     */     {
/* 256 */       COConfigurationManager.removeParameterListener(this.key, this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addImplListener(ParameterImplListener l)
/*     */   {
/* 264 */     if (this.impl_listeners == null) {
/* 265 */       this.impl_listeners = new ArrayList(1);
/*     */     }
/* 267 */     this.impl_listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeImplListener(ParameterImplListener l)
/*     */   {
/* 274 */     if (this.impl_listeners == null) {
/* 275 */       return;
/*     */     }
/* 277 */     this.impl_listeners.remove(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addConfigParameterListener(ConfigParameterListener l)
/*     */   {
/* 284 */     if (this.listeners == null) {
/* 285 */       this.listeners = new ArrayList(1);
/*     */     }
/* 287 */     this.listeners.add(l);
/*     */     
/* 289 */     if (this.listeners.size() == 1)
/*     */     {
/* 291 */       COConfigurationManager.addParameterListener(this.key, this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeConfigParameterListener(ConfigParameterListener l)
/*     */   {
/* 299 */     if (this.listeners == null) {
/* 300 */       return;
/*     */     }
/* 302 */     this.listeners.remove(l);
/*     */     
/* 304 */     if (this.listeners.size() == 0)
/*     */     {
/* 306 */       COConfigurationManager.removeParameterListener(this.key, this);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getLabelText() {
/* 311 */     if (this.label == null) {
/* 312 */       this.label = MessageText.getString(this.labelKey);
/*     */     }
/* 314 */     return this.label;
/*     */   }
/*     */   
/*     */   public void setLabelText(String sText) {
/* 318 */     this.labelKey = null;
/* 319 */     this.label = sText;
/*     */     
/* 321 */     triggerLabelChanged(sText, false);
/*     */   }
/*     */   
/*     */   public String getLabelKey() {
/* 325 */     return this.labelKey;
/*     */   }
/*     */   
/*     */   public void setLabelKey(String sLabelKey) {
/* 329 */     this.labelKey = sLabelKey;
/* 330 */     this.label = null;
/*     */     
/* 332 */     triggerLabelChanged(this.labelKey, true);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getConfigKeyName()
/*     */   {
/* 338 */     return this.key;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasBeenSet()
/*     */   {
/* 344 */     return COConfigurationManager.doesParameterNonDefaultExist(this.key);
/*     */   }
/*     */   
/*     */   private void triggerLabelChanged(String text, boolean isKey) {
/* 348 */     if (this.impl_listeners == null) {
/* 349 */       return;
/*     */     }
/*     */     
/* 352 */     Object[] listenersArray = this.impl_listeners.toArray();
/* 353 */     for (int i = 0; i < listenersArray.length; i++) {
/*     */       try {
/* 355 */         ParameterImplListener l = (ParameterImplListener)listenersArray[i];
/* 356 */         l.labelChanged(this, text, isKey);
/*     */       }
/*     */       catch (Throwable f)
/*     */       {
/* 360 */         Debug.printStackTrace(f);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 368 */     this.listeners = null;
/* 369 */     this.impl_listeners = null;
/* 370 */     this.toDisable = null;
/* 371 */     this.toEnable = null;
/*     */     
/* 373 */     COConfigurationManager.removeParameterListener(this.key, this);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/ParameterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */