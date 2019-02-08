/*     */ package org.gudy.azureus2.pluginsimpl.local.deprecate;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.StringList;
/*     */ import org.gudy.azureus2.core3.config.impl.StringListImpl;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.logging.Logger;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerListener;
/*     */ import org.gudy.azureus2.plugins.ui.components.UIProgressBar;
/*     */ import org.gudy.azureus2.plugins.ui.components.UITextArea;
/*     */ import org.gudy.azureus2.plugins.ui.components.UITextField;
/*     */ import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
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
/*     */ public class PluginDeprecation
/*     */ {
/*     */   private static final String CONFIG_KEY = "PluginDeprecationWarnings";
/*     */   private static final String FORUM_STABLE_LINK = "http://forum.vuze.com/forum.jspa?forumID=124";
/*     */   private static final String FORUM_BETA_LINK = "http://forum.vuze.com/forum.jspa?forumID=124";
/*     */   private static final int IGNORE = 0;
/*     */   private static final int NOTIFY_ONCE = 1;
/*     */   private static final int NOTIFY_EVERY = 2;
/*     */   private static final int NOTIFY_AND_DIE = 3;
/*     */   private static final int DIE = 4;
/*  58 */   private static BasicPluginViewModel model = null;
/*  59 */   private static LoggerChannel channel = null;
/*  60 */   private static Map behaviour_mapping = new HashMap();
/*  61 */   private static Set persistent_warnings = Collections.synchronizedSet(new HashSet());
/*  62 */   private static Set instance_warnings = Collections.synchronizedSet(new HashSet());
/*     */   
/*  64 */   private static void register(String identifier, int stable_behaviour, int beta_behaviour) { behaviour_mapping.put(identifier, new Integer(Constants.isCVSVersion() ? beta_behaviour : stable_behaviour)); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/*  74 */     register("property listener", 0, 2);
/*  75 */     register("openTorrentFile", 0, 2);
/*  76 */     register("openTorrentURL", 0, 2);
/*     */     
/*     */ 
/*  79 */     register("setDisabled", 0, 1);
/*  80 */     register("isDisabled", 0, 1);
/*  81 */     register("isBuiltIn", 0, 1);
/*  82 */     register("isMandatory", 0, 1);
/*  83 */     register("isOperational", 0, 1);
/*  84 */     register("isShared", 0, 1);
/*  85 */     register("unload", 0, 1);
/*  86 */     register("reload", 0, 1);
/*  87 */     register("uninstall", 0, 1);
/*  88 */     register("isUnloadable", 0, 1);
/*     */     
/*     */ 
/*  91 */     persistent_warnings.addAll(Arrays.asList(COConfigurationManager.getStringListParameter("PluginDeprecationWarnings").toArray()));
/*     */   }
/*     */   
/*     */   public static void call(String identifier, Object context)
/*     */   {
/*  96 */     call(identifier, context.getClass().getName());
/*     */   }
/*     */   
/*     */   public static void call(String identifier, String context) {
/* 100 */     Integer behaviour = (Integer)behaviour_mapping.get(identifier);
/* 101 */     if (behaviour == null) {
/* 102 */       throw new IllegalArgumentException("unknown deprecated call identifier: " + identifier);
/*     */     }
/*     */     
/* 105 */     int b = behaviour.intValue();
/* 106 */     if (b == 0) { return;
/*     */     }
/* 108 */     boolean persistent_notify = b == 1;
/* 109 */     boolean notify = b != 4;
/* 110 */     boolean raise_error = (b == 3) || (b == 4);
/*     */     
/* 112 */     String persistent_id = context + ":" + identifier;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 122 */     if ((notify) && (!instance_warnings.contains(context)) && ((!persistent_notify) || (!persistent_warnings.contains(persistent_id))))
/*     */     {
/*     */ 
/* 125 */       instance_warnings.add(context);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 130 */       if ((!persistent_notify) && (persistent_warnings.remove(persistent_id))) {
/* 131 */         COConfigurationManager.setParameter("PluginDeprecationWarnings", new StringListImpl(persistent_warnings));
/*     */       }
/*     */       
/*     */ 
/* 135 */       synchronized (PluginDeprecation.class) {
/* 136 */         if (model == null) {
/* 137 */           final PluginInterface pi = PluginInitializer.getDefaultInterface();
/* 138 */           model = pi.getUIManager().createBasicPluginViewModel(MessageText.getString("PluginDeprecation.view"));
/* 139 */           model.getStatus().setVisible(false);
/* 140 */           model.getProgress().setVisible(false);
/* 141 */           model.getActivity().setVisible(false);
/* 142 */           model.getLogArea().appendText(MessageText.getString("PluginDeprecation.log.start", new String[] { Constants.isCVSVersion() ? "http://forum.vuze.com/forum.jspa?forumID=124" : "http://forum.vuze.com/forum.jspa?forumID=124" }));
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 148 */           BasicPluginViewModel f_model = model;
/*     */           
/*     */ 
/* 151 */           UIManagerListener uiml = new UIManagerListener() {
/*     */             public void UIAttached(UIInstance inst) {
/* 153 */               if (inst.getUIType() == 1)
/*     */               {
/* 155 */                 inst.openView(this.val$f_model);
/*     */                 
/* 157 */                 pi.getUIManager().removeUIListener(this);
/*     */               }
/*     */             }
/*     */             
/* 161 */             public void UIDetached(UIInstance inst) {} };
/* 162 */           pi.getUIManager().addUIListener(uiml);
/*     */         }
/*     */         
/* 165 */         String log_details = MessageText.getString("PluginDeprecation.log.details", new String[] { identifier, context, Debug.getStackTrace(false, false) });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 170 */         model.getLogArea().appendText(log_details);
/*     */         
/* 172 */         if (channel == null) {
/* 173 */           channel = PluginInitializer.getDefaultInterface().getLogger().getChannel("PluginDeprecation");
/*     */         }
/*     */         
/*     */ 
/* 177 */         channel.logAlert(2, MessageText.getString("PluginDeprecation.alert"));
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 182 */         Debug.out(new PluginDeprecationException("Deprecated plugin call - " + persistent_id).fillInStackTrace());
/*     */       }
/*     */       
/* 185 */       if (persistent_notify) {
/* 186 */         persistent_warnings.add(persistent_id);
/* 187 */         COConfigurationManager.setParameter("PluginDeprecationWarnings", new StringListImpl(persistent_warnings));
/*     */       }
/*     */     }
/*     */     
/* 191 */     if (raise_error) {
/* 192 */       throw new PluginDeprecationException(persistent_id);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/deprecate/PluginDeprecation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */