/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*    */ 
/*    */ import java.lang.ref.WeakReference;
/*    */ import org.gudy.azureus2.plugins.PluginInterface;
/*    */ import org.gudy.azureus2.plugins.ui.config.ConfigSection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConfigSectionHolder
/*    */   implements ConfigSection
/*    */ {
/*    */   private ConfigSection section;
/*    */   private WeakReference<PluginInterface> pi;
/*    */   
/*    */   protected ConfigSectionHolder(ConfigSection _section, PluginInterface _pi)
/*    */   {
/* 39 */     this.section = _section;
/*    */     
/* 41 */     if (_pi != null)
/*    */     {
/* 43 */       this.pi = new WeakReference(_pi);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public String configSectionGetParentSection()
/*    */   {
/* 50 */     return this.section.configSectionGetParentSection();
/*    */   }
/*    */   
/*    */ 
/*    */   public String configSectionGetName()
/*    */   {
/* 56 */     return this.section.configSectionGetName();
/*    */   }
/*    */   
/*    */ 
/*    */   public void configSectionSave()
/*    */   {
/* 62 */     this.section.configSectionSave();
/*    */   }
/*    */   
/*    */ 
/*    */   public void configSectionDelete()
/*    */   {
/* 68 */     this.section.configSectionDelete();
/*    */   }
/*    */   
/*    */ 
/*    */   public PluginInterface getPluginInterface()
/*    */   {
/* 74 */     return this.pi == null ? null : (PluginInterface)this.pi.get();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/ConfigSectionHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */