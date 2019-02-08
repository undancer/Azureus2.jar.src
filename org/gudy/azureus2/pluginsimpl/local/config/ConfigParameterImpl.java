/*    */ package org.gudy.azureus2.pluginsimpl.local.config;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*    */ import org.gudy.azureus2.core3.config.ParameterListener;
/*    */ import org.gudy.azureus2.plugins.config.ConfigParameter;
/*    */ import org.gudy.azureus2.plugins.config.ConfigParameterListener;
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
/*    */ 
/*    */ public class ConfigParameterImpl
/*    */   implements ConfigParameter, ParameterListener
/*    */ {
/*    */   protected String key;
/* 39 */   protected List listeners = new ArrayList();
/*    */   
/*    */ 
/*    */ 
/*    */   public ConfigParameterImpl(String _key)
/*    */   {
/* 45 */     this.key = _key;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void parameterChanged(String parameterName)
/*    */   {
/* 52 */     for (int i = 0; i < this.listeners.size(); i++)
/*    */     {
/* 54 */       ((ConfigParameterListener)this.listeners.get(i)).configParameterChanged(this);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void addConfigParameterListener(ConfigParameterListener l)
/*    */   {
/* 62 */     this.listeners.add(l);
/*    */     
/* 64 */     if (this.listeners.size() == 1)
/*    */     {
/* 66 */       COConfigurationManager.addParameterListener(this.key, this);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void removeConfigParameterListener(ConfigParameterListener l)
/*    */   {
/* 74 */     this.listeners.remove(l);
/*    */     
/* 76 */     if (this.listeners.size() == 0)
/*    */     {
/* 78 */       COConfigurationManager.removeParameterListener(this.key, this);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/config/ConfigParameterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */