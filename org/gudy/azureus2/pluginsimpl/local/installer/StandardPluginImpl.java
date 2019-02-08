/*     */ package org.gudy.azureus2.pluginsimpl.local.installer;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.html.HTMLUtils;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.Plugin;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.installer.StandardPlugin;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*     */ import org.gudy.azureus2.pluginsimpl.update.PluginUpdatePlugin;
/*     */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetails;
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
/*     */ public class StandardPluginImpl
/*     */   extends InstallablePluginImpl
/*     */   implements StandardPlugin
/*     */ {
/*     */   private SFPluginDetails details;
/*     */   private String version;
/*     */   
/*     */   protected StandardPluginImpl(PluginInstallerImpl _installer, SFPluginDetails _details, String _version)
/*     */   {
/*  51 */     super(_installer);
/*     */     
/*  53 */     this.details = _details;
/*  54 */     this.version = (_version == null ? "" : _version);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getId()
/*     */   {
/*  60 */     return this.details.getId();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getVersion()
/*     */   {
/*  66 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  72 */     return this.details.getName();
/*     */   }
/*     */   
/*     */   public String getDescription()
/*     */   {
/*     */     try
/*     */     {
/*  79 */       List lines = HTMLUtils.convertHTMLToText("", this.details.getDescription());
/*     */       
/*  81 */       String res = "";
/*     */       
/*  83 */       for (int i = 0; i < lines.size(); i++) {
/*  84 */         res = res + (i == 0 ? "" : "\n") + lines.get(i);
/*     */       }
/*     */       
/*  87 */       return res;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  91 */       return Debug.getNestedExceptionMessage(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getRelativeURLBase()
/*     */   {
/*  98 */     return this.details.getRelativeURLBase();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addUpdate(UpdateCheckInstance inst, PluginUpdatePlugin plugin_update_plugin, Plugin plugin, PluginInterface plugin_interface)
/*     */   {
/* 108 */     inst.addUpdatableComponent(plugin_update_plugin.getCustomUpdateableComponent(getId(), false), false);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/installer/StandardPluginImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */