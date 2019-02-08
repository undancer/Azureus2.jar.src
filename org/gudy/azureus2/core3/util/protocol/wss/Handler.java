/*     */ package org.gudy.azureus2.core3.util.protocol.wss;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctions.actionListener;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.UIFunctionsUserPrompter;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.URLStreamHandler;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCException;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
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
/*     */ public class Handler
/*     */   extends URLStreamHandler
/*     */ {
/*  50 */   private static boolean install_prompted = false;
/*     */   
/*     */ 
/*     */ 
/*     */   public URLConnection openConnection(URL u)
/*     */     throws IOException
/*     */   {
/*  57 */     return getProxy(u).openConnection();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private URL getProxy(URL u)
/*     */     throws IOException
/*     */   {
/*  66 */     PluginInterface pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("azwebtorrent");
/*     */     
/*  68 */     if (pi == null)
/*     */     {
/*  70 */       installPlugin();
/*     */       
/*  72 */       throw new IOException("'WebTorrent Support Plugin' is required - go to 'Tools->Plugins->Installation Wizard' to install.");
/*     */     }
/*     */     
/*  75 */     IPCInterface ipc = pi.getIPC();
/*     */     try
/*     */     {
/*  78 */       return (URL)ipc.invoke("getProxyURL", new Object[] { u });
/*     */ 
/*     */     }
/*     */     catch (IPCException ipce)
/*     */     {
/*     */ 
/*  84 */       Throwable e = ipce;
/*     */       
/*  86 */       if (e.getCause() != null)
/*     */       {
/*  88 */         e = e.getCause();
/*     */       }
/*     */       
/*  91 */       throw new IOException("Communication error with WebTorrent Support Plugin: " + Debug.getNestedExceptionMessage(e));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static void installPlugin()
/*     */   {
/*  98 */     synchronized (Handler.class)
/*     */     {
/* 100 */       if (install_prompted)
/*     */       {
/* 102 */         return;
/*     */       }
/*     */       
/* 105 */       install_prompted = true;
/*     */     }
/*     */     
/* 108 */     new AEThread2("install::async")
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 113 */         boolean installing = false;
/*     */         try
/*     */         {
/* 116 */           UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */           
/* 118 */           if (uif == null) {
/*     */             return;
/*     */           }
/*     */           
/*     */ 
/* 123 */           String title = MessageText.getString("azwebtorrent.install");
/*     */           
/* 125 */           String text = MessageText.getString("azwebtorrent.install.text");
/*     */           
/* 127 */           UIFunctionsUserPrompter prompter = uif.getUserPrompter(title, text, new String[] { MessageText.getString("Button.yes"), MessageText.getString("Button.no") }, 0);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 132 */           String remember_id = "azwebtorrent.install.remember.id";
/*     */           
/* 134 */           if (remember_id != null)
/*     */           {
/* 136 */             prompter.setRemember(remember_id, false, MessageText.getString("MessageBoxWindow.nomoreprompting"));
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 142 */           prompter.setAutoCloseInMS(0);
/*     */           
/* 144 */           prompter.open(null);
/*     */           
/* 146 */           boolean install = prompter.waitUntilClosed() == 0;
/*     */           
/* 148 */           if (install)
/*     */           {
/* 150 */             if (!Constants.isJava7OrHigher)
/*     */             {
/* 152 */               title = MessageText.getString("azwebtorrent.install.fail.jver");
/*     */               
/* 154 */               text = MessageText.getString("azwebtorrent.install.fail.jver.text");
/*     */               
/* 156 */               prompter = uif.getUserPrompter(title, text, new String[] { MessageText.getString("Button.ok") }, 0);
/*     */               
/*     */ 
/*     */ 
/* 160 */               prompter.setAutoCloseInMS(0);
/*     */               
/* 162 */               prompter.open(null);
/*     */             }
/*     */             else
/*     */             {
/* 166 */               uif.installPlugin("azwebtorrent", "azwebtorrent.install", new UIFunctions.actionListener()
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                 public void actionComplete(Object result) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 177 */               });
/* 178 */               installing = true;
/*     */             }
/*     */             
/*     */           }
/*     */           
/*     */         }
/*     */         finally
/*     */         {
/* 186 */           if (installing) {}
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/protocol/wss/Handler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */