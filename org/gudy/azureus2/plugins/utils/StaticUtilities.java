/*     */ package org.gudy.azureus2.plugins.utils;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerListener;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*     */ import org.gudy.azureus2.plugins.utils.resourceuploader.ResourceUploaderFactory;
/*     */ import org.gudy.azureus2.plugins.utils.xml.rss.RSSFeed;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentException;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader.ResourceDownloaderFactoryImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.resourceuploader.ResourceUploaderFactoryImpl;
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
/*     */ public class StaticUtilities
/*     */ {
/*     */   private static Formatters formatters;
/*     */   
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  47 */       Class c = Class.forName("org.gudy.azureus2.pluginsimpl.local.utils.FormattersImpl");
/*  48 */       formatters = (Formatters)c.newInstance();
/*     */     } catch (Exception e) {
/*  50 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Formatters getFormatters()
/*     */   {
/*  58 */     return formatters;
/*     */   }
/*     */   
/*     */   public static ResourceDownloaderFactory getResourceDownloaderFactory()
/*     */   {
/*  63 */     return ResourceDownloaderFactoryImpl.getSingleton();
/*     */   }
/*     */   
/*     */ 
/*     */   public static ResourceUploaderFactory getResourceUploaderFactory()
/*     */   {
/*  69 */     return ResourceUploaderFactoryImpl.getSingleton();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static RSSFeed getRSSFeed(URL source_url, InputStream is)
/*     */     throws SimpleXMLParserDocumentException
/*     */   {
/*  79 */     return PluginInitializer.getDefaultInterface().getUtilities().getRSSFeed(source_url, is);
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
/*     */ 
/*     */ 
/*     */   public static int promptUser(String title, String desc, String[] options, int default_option)
/*     */   {
/*  97 */     UIInstance[] instances = PluginInitializer.getDefaultInterface().getUIManager().getUIInstances();
/*     */     
/*  99 */     if (instances.length > 0)
/*     */     {
/* 101 */       return instances[0].promptUser(title, desc, options, default_option);
/*     */     }
/*     */     
/*     */ 
/* 105 */     Debug.out("No UIInstances to handle prompt: " + title + "/" + desc);
/*     */     
/* 107 */     return -1;
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
/*     */   public static UIManager getUIManager(long millis_to_wait_for_attach)
/*     */   {
/* 122 */     UIManager ui_manager = PluginInitializer.getDefaultInterface().getUIManager();
/*     */     
/* 124 */     if (ui_manager.getUIInstances().length == 0)
/*     */     {
/* 126 */       final AESemaphore sem = new AESemaphore("waitforui");
/*     */       
/* 128 */       ui_manager.addUIListener(new UIManagerListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void UIAttached(UIInstance instance)
/*     */         {
/*     */ 
/* 135 */           this.val$ui_manager.removeUIListener(this);
/*     */           
/* 137 */           sem.releaseForever();
/*     */         }
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
/*     */         public void UIDetached(UIInstance instance) {}
/* 150 */       });
/* 151 */       long time_to_go = millis_to_wait_for_attach;
/*     */       
/* 153 */       while (ui_manager.getUIInstances().length == 0)
/*     */       {
/* 155 */         if (!sem.reserve(1000L))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 160 */           time_to_go -= 1000L;
/*     */           
/* 162 */           if (time_to_go <= 0L)
/*     */           {
/* 164 */             Debug.out("Timeout waiting for UI to attach");
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 171 */     return ui_manager;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/StaticUtilities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */