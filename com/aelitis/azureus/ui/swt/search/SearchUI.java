/*     */ package com.aelitis.azureus.ui.swt.search;
/*     */ 
/*     */ import com.aelitis.azureus.core.metasearch.MetaSearchManager;
/*     */ import com.aelitis.azureus.core.metasearch.MetaSearchManagerFactory;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerListener;
/*     */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.ParameterListener;
/*     */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*     */ import org.gudy.azureus2.plugins.utils.DelayedTask;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
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
/*     */ public class SearchUI
/*     */ {
/*     */   private static final String CONFIG_SECTION_ID = "Search";
/*     */   private UIManager ui_manager;
/*     */   
/*     */   public SearchUI()
/*     */   {
/*  54 */     final PluginInterface default_pi = PluginInitializer.getDefaultInterface();
/*     */     
/*  56 */     this.ui_manager = default_pi.getUIManager();
/*     */     
/*  58 */     this.ui_manager.addUIListener(new UIManagerListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void UIAttached(UIInstance instance)
/*     */       {
/*     */ 
/*  65 */         if (!(instance instanceof UISWTInstance)) {
/*  66 */           return;
/*     */         }
/*     */         
/*     */ 
/*  70 */         Utilities utilities = default_pi.getUtilities();
/*     */         
/*  72 */         DelayedTask dt = utilities.createDelayedTask(new Runnable()
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*  77 */             Utils.execSWTThread(new AERunnable()
/*     */             {
/*     */ 
/*     */               public void runSupport()
/*     */               {
/*  82 */                 SearchUI.this.delayedInit();
/*     */               }
/*     */               
/*     */             });
/*     */           }
/*  87 */         });
/*  88 */         dt.queue();
/*     */       }
/*     */       
/*     */ 
/*     */       public void UIDetached(UIInstance instance) {}
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private void delayedInit()
/*     */   {
/*  99 */     final MetaSearchManager manager = MetaSearchManagerFactory.getSingleton();
/*     */     
/* 101 */     if (manager == null)
/*     */     {
/* 103 */       return;
/*     */     }
/*     */     
/* 106 */     BasicPluginConfigModel configModel = this.ui_manager.createBasicPluginConfigModel("root", "Search");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 111 */     final BooleanParameter proxy_enable = configModel.addBooleanParameter2("search.proxy.enable", "search.proxy.enable", manager.getProxyRequestsEnabled());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 116 */     proxy_enable.addListener(new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(Parameter param)
/*     */       {
/*     */ 
/* 123 */         manager.setProxyRequestsEnabled(proxy_enable.getValue());
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/search/SearchUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */