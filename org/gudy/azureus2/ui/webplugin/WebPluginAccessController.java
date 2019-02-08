/*     */ package org.gudy.azureus2.ui.webplugin;
/*     */ 
/*     */ import org.gudy.azureus2.plugins.PluginConfig;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.config.ConfigParameter;
/*     */ import org.gudy.azureus2.plugins.config.ConfigParameterListener;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequest;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequestAccessController;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.rpexceptions.RPMethodAccessDeniedException;
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
/*     */ public class WebPluginAccessController
/*     */   implements RPRequestAccessController
/*     */ {
/*     */   protected boolean view_mode;
/*     */   
/*     */   public WebPluginAccessController(final PluginInterface pi)
/*     */   {
/*  43 */     ConfigParameter mode_parameter = pi.getPluginconfig().getPluginParameter("Mode");
/*     */     
/*  45 */     if (mode_parameter == null)
/*     */     {
/*  47 */       this.view_mode = true;
/*     */     }
/*     */     else
/*     */     {
/*  51 */       mode_parameter.addConfigParameterListener(new ConfigParameterListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void configParameterChanged(ConfigParameter param)
/*     */         {
/*     */ 
/*  58 */           WebPluginAccessController.this.setViewMode(pi);
/*     */         }
/*     */         
/*  61 */       });
/*  62 */       setViewMode(pi);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setViewMode(PluginInterface pi)
/*     */   {
/*  70 */     ((WebPlugin)pi.getPlugin());String mode_str = pi.getPluginconfig().getPluginStringParameter("Mode", "full");
/*     */     
/*  72 */     this.view_mode = (!mode_str.equalsIgnoreCase("full"));
/*     */   }
/*     */   
/*     */ 
/*     */   public void checkUploadAllowed()
/*     */   {
/*  78 */     if (this.view_mode) {
/*  79 */       throw new RPMethodAccessDeniedException();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkAccess(String name, RPRequest request)
/*     */   {
/*  88 */     String method = request.getMethod();
/*     */     
/*     */ 
/*     */ 
/*  92 */     if (this.view_mode)
/*     */     {
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
/* 105 */       boolean ok = false;
/*     */       
/* 107 */       if (name.equals("PluginInterface"))
/*     */       {
/* 109 */         ok = (method.equals("getPluginconfig")) || (method.equals("getDownloadManager")) || (method.equals("getIPFilter"));
/*     */ 
/*     */ 
/*     */       }
/* 113 */       else if (name.equals("DownloadManager"))
/*     */       {
/* 115 */         ok = method.equals("getDownloads");
/*     */       }
/* 117 */       else if (name.equals("PluginConfig"))
/*     */       {
/* 119 */         if ((method.startsWith("getPlugin")) || (method.equals("save")))
/*     */         {
/*     */ 
/* 122 */           ok = true;
/*     */         }
/* 124 */         else if (method.equals("setPluginParameter[String,int]"))
/*     */         {
/* 126 */           String param = (String)request.getParams()[0];
/*     */           
/* 128 */           ok = param.equals("MDConfigModel:refresh_period");
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 133 */       if (!ok) {
/* 134 */         throw new RPMethodAccessDeniedException();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/webplugin/WebPluginAccessController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */