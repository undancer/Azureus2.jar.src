/*     */ package org.gudy.azureus2.pluginsimpl.local;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.plugins.PluginManagerArgumentHandler;
/*     */ import org.gudy.azureus2.plugins.PluginManagerDefaults;
/*     */ import org.gudy.azureus2.pluginsimpl.local.launch.PluginSingleInstanceHandler;
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
/*     */ public class PluginManagerDefaultsImpl
/*     */   implements PluginManagerDefaults
/*     */ {
/*  37 */   protected static PluginManagerDefaultsImpl singleton = new PluginManagerDefaultsImpl();
/*     */   
/*     */ 
/*     */   public static PluginManagerDefaults getSingleton()
/*     */   {
/*  42 */     return singleton;
/*     */   }
/*     */   
/*  45 */   protected List disabled = new ArrayList();
/*     */   
/*     */ 
/*     */   public String[] getDefaultPlugins()
/*     */   {
/*  50 */     return PLUGIN_IDS;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDefaultPluginEnabled(String plugin_id, boolean enabled)
/*     */   {
/*  58 */     if (enabled)
/*     */     {
/*  60 */       this.disabled.remove(plugin_id);
/*     */     }
/*  62 */     else if (!this.disabled.contains(plugin_id))
/*     */     {
/*  64 */       this.disabled.add(plugin_id);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isDefaultPluginEnabled(String plugin_id)
/*     */   {
/*  72 */     return !this.disabled.contains(plugin_id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setApplicationName(String name)
/*     */   {
/*  79 */     SystemProperties.setApplicationName(name);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getApplicationName()
/*     */   {
/*  85 */     return SystemProperties.getApplicationName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setApplicationIdentifier(String id)
/*     */   {
/*  92 */     SystemProperties.setApplicationIdentifier(id);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getApplicationIdentifier()
/*     */   {
/*  98 */     return SystemProperties.getApplicationIdentifier();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setApplicationEntryPoint(String ep)
/*     */   {
/* 105 */     SystemProperties.setApplicationEntryPoint(ep);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getApplicationEntryPoint()
/*     */   {
/* 111 */     return SystemProperties.getApplicationEntryPoint();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setSingleInstanceHandler(int single_instance_port, PluginManagerArgumentHandler handler)
/*     */   {
/* 119 */     PluginSingleInstanceHandler.initialise(single_instance_port, handler);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean setSingleInstanceHandlerAndProcess(int single_instance_port, PluginManagerArgumentHandler handler, String[] args)
/*     */   {
/* 128 */     PluginSingleInstanceHandler.initialise(single_instance_port, handler);
/*     */     
/* 130 */     return PluginSingleInstanceHandler.initialiseAndProcess(single_instance_port, handler, args);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/PluginManagerDefaultsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */