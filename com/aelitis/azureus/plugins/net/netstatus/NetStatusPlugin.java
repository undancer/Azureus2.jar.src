/*     */ package com.aelitis.azureus.plugins.net.netstatus;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Properties;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.plugins.Plugin;
/*     */ import org.gudy.azureus2.plugins.PluginConfig;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginListener;
/*     */ import org.gudy.azureus2.plugins.logging.Logger;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerListener;
/*     */ import org.gudy.azureus2.plugins.ui.config.ActionParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.StringParameter;
/*     */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*     */ import org.gudy.azureus2.plugins.utils.LocaleUtilities;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
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
/*     */ public class NetStatusPlugin
/*     */   implements Plugin
/*     */ {
/*     */   public static final String VIEW_ID = "aznetstatus";
/*     */   private PluginInterface plugin_interface;
/*     */   private LoggerChannel logger;
/*     */   private BooleanParameter logging_detailed;
/*     */   private ActionParameter test_button;
/*     */   private StringParameter test_address;
/*     */   private NetStatusProtocolTester protocol_tester;
/*  52 */   private AESemaphore protocol_tester_sem = new AESemaphore("ProtTestSem");
/*     */   
/*     */ 
/*     */ 
/*     */   public static void load(PluginInterface plugin_interface)
/*     */   {
/*  58 */     String name = plugin_interface.getUtilities().getLocaleUtilities().getLocalisedMessageText("Views.plugins.aznetstatus.title");
/*     */     
/*     */ 
/*  61 */     plugin_interface.getPluginProperties().setProperty("plugin.version", "1.0");
/*  62 */     plugin_interface.getPluginProperties().setProperty("plugin.name", name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void initialize(PluginInterface _plugin_interface)
/*     */   {
/*  70 */     this.plugin_interface = _plugin_interface;
/*     */     
/*  72 */     this.logger = this.plugin_interface.getLogger().getChannel("NetStatus");
/*     */     
/*  74 */     this.logger.setDiagnostic();
/*     */     
/*  76 */     BasicPluginConfigModel config = this.plugin_interface.getUIManager().createBasicPluginConfigModel("Views.plugins.aznetstatus.title");
/*     */     
/*  78 */     this.logging_detailed = config.addBooleanParameter2("plugin.aznetstatus.logfull", "plugin.aznetstatus.logfull", false);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 147 */     this.plugin_interface.getUIManager().addUIListener(new UIManagerListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void UIAttached(UIInstance instance)
/*     */       {
/*     */ 
/* 154 */         if (instance.getUIType() == 1) {
/*     */           try
/*     */           {
/* 157 */             Class.forName("com.aelitis.azureus.plugins.net.netstatus.swt.NetStatusPluginView").getConstructor(new Class[] { NetStatusPlugin.class, UIInstance.class, String.class }).newInstance(new Object[] { NetStatusPlugin.this, instance, "aznetstatus" });
/*     */ 
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/*     */ 
/* 163 */             e.printStackTrace();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void UIDetached(UIInstance instance) {}
/* 174 */     });
/* 175 */     this.plugin_interface.addListener(new PluginListener()
/*     */     {
/*     */ 
/*     */       public void initializationComplete()
/*     */       {
/*     */ 
/* 181 */         new AEThread2("NetstatusPlugin:init", true)
/*     */         {
/*     */           public void run()
/*     */           {
/*     */             try
/*     */             {
/* 187 */               NetStatusPlugin.this.protocol_tester = new NetStatusProtocolTester(NetStatusPlugin.this, NetStatusPlugin.this.plugin_interface);
/*     */               
/* 189 */               if (NetStatusPlugin.this.test_button != null)
/*     */               {
/* 191 */                 NetStatusPlugin.this.test_button.setEnabled(true);
/*     */               }
/*     */             }
/*     */             finally {
/* 195 */               NetStatusPlugin.this.protocol_tester_sem.releaseForever();
/*     */             }
/*     */           }
/*     */         }.start();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void closedownInitiated() {}
/*     */       
/*     */ 
/*     */ 
/*     */       public void closedownComplete() {}
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isDetailedLogging()
/*     */   {
/* 216 */     return this.logging_detailed.getValue();
/*     */   }
/*     */   
/*     */ 
/*     */   public NetStatusProtocolTester getProtocolTester()
/*     */   {
/* 222 */     this.protocol_tester_sem.reserve();
/*     */     
/* 224 */     return this.protocol_tester;
/*     */   }
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
/*     */   public void setBooleanParameter(String name, boolean value)
/*     */   {
/* 240 */     this.plugin_interface.getPluginconfig().setPluginParameter(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean getBooleanParameter(String name, boolean def)
/*     */   {
/* 248 */     return this.plugin_interface.getPluginconfig().getPluginBooleanParameter(name, def);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void log(String str)
/*     */   {
/* 255 */     this.logger.log(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void log(String str, Throwable e)
/*     */   {
/* 263 */     this.logger.log(str);
/* 264 */     this.logger.log(e);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/netstatus/NetStatusPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */