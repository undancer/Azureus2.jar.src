/*     */ package org.gudy.azureus2.platform.unix;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Properties;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*     */ import org.gudy.azureus2.plugins.Plugin;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerListener;
/*     */ import org.gudy.azureus2.update.UpdaterUtils;
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
/*     */ public class PlatformManagerUnixPlugin
/*     */   implements Plugin
/*     */ {
/*     */   private PluginInterface plugin_interface;
/*     */   
/*     */   public void initialize(PluginInterface _plugin_interface)
/*     */     throws PluginException
/*     */   {
/*  57 */     this.plugin_interface = _plugin_interface;
/*     */     
/*  59 */     this.plugin_interface.getPluginProperties().setProperty("plugin.name", "Platform-Specific Support");
/*     */     
/*     */ 
/*  62 */     String version = "1.0";
/*     */     
/*  64 */     PlatformManager platform = PlatformManagerFactory.getPlatformManager();
/*     */     
/*  66 */     if (platform.hasCapability(PlatformManagerCapabilities.GetVersion)) {
/*     */       try
/*     */       {
/*  69 */         version = platform.getVersion();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*  73 */         Debug.printStackTrace(e);
/*     */       }
/*     */       
/*     */     }
/*     */     else {
/*  78 */       this.plugin_interface.getPluginProperties().setProperty("plugin.version.info", "Not required for this platform");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  83 */     this.plugin_interface.getPluginProperties().setProperty("plugin.version", version);
/*     */     
/*     */ 
/*  86 */     this.plugin_interface.getUIManager().addUIListener(new UIManagerListener() {
/*  87 */       boolean done = false;
/*     */       
/*     */       public void UIDetached(UIInstance instance) {}
/*     */       
/*     */       public void UIAttached(UIInstance instance)
/*     */       {
/*  93 */         if (!this.done)
/*     */         {
/*  95 */           this.done = true;
/*     */           
/*  97 */           if (Constants.compareVersions(UpdaterUtils.getUpdaterPluginVersion(), "1.8.5") >= 0)
/*     */           {
/*  99 */             PlatformManagerUnixPlugin.this.checkStartupScript();
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void checkStartupScript()
/*     */   {
/* 112 */     COConfigurationManager.setIntDefault("unix.script.lastaskversion", -1);
/* 113 */     int lastAskedVersion = COConfigurationManager.getIntParameter("unix.script.lastaskversion");
/*     */     
/* 115 */     String sVersion = System.getProperty("azureus.script.version", "0");
/* 116 */     int version = 0;
/*     */     try {
/* 118 */       version = Integer.parseInt(sVersion);
/*     */     }
/*     */     catch (Throwable t) {}
/*     */     
/* 122 */     Pattern pat = Pattern.compile("SCRIPT_VERSION=([0-9]+)", 2);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 127 */     String sScriptFile = System.getProperty("azureus.script", null);
/* 128 */     File oldFilePath; File oldFilePath; if ((sScriptFile != null) && (new File(sScriptFile).exists())) {
/* 129 */       oldFilePath = new File(sScriptFile);
/*     */     } else {
/* 131 */       oldFilePath = new File(SystemProperties.getApplicationPath(), "azureus");
/*     */       
/* 133 */       if (!oldFilePath.exists()) {
/* 134 */         return;
/*     */       }
/*     */     }
/*     */     
/* 138 */     String oldFilePathString = oldFilePath.getAbsolutePath();
/*     */     String oldStartupScript;
/*     */     try
/*     */     {
/* 142 */       oldStartupScript = FileUtil.readFileAsString(oldFilePath, 65535, "utf8");
/*     */     }
/*     */     catch (IOException e) {
/* 145 */       oldStartupScript = "";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 153 */     if (version == 0) {
/* 154 */       Matcher matcher = pat.matcher(oldStartupScript);
/* 155 */       if (matcher.find()) {
/* 156 */         String sScriptVersion = matcher.group(1);
/*     */         try {
/* 158 */           version = Integer.parseInt(sScriptVersion);
/*     */         }
/*     */         catch (Throwable t) {}
/*     */       }
/*     */     }
/*     */     
/* 164 */     if (version <= lastAskedVersion) {
/* 165 */       return;
/*     */     }
/*     */     
/* 168 */     InputStream stream = getClass().getResourceAsStream("startupScript");
/*     */     try {
/* 170 */       String startupScript = FileUtil.readInputStreamAsString(stream, 65535, "utf8");
/*     */       
/* 172 */       Matcher matcher = pat.matcher(startupScript);
/* 173 */       if (matcher.find()) {
/* 174 */         String sScriptVersion = matcher.group(1);
/* 175 */         int latestVersion = 0;
/*     */         try {
/* 177 */           latestVersion = Integer.parseInt(sScriptVersion);
/*     */         }
/*     */         catch (Throwable t) {}
/* 180 */         if (latestVersion > version) {
/* 181 */           boolean bNotChanged = (oldStartupScript.indexOf("SCRIPT_NOT_CHANGED=0") > 0) || (oldStartupScript.indexOf("AUTOUPDATE_SCRIPT=1") > 0);
/*     */           
/* 183 */           boolean bInformUserManual = true;
/*     */           
/* 185 */           if (bNotChanged) {
/* 186 */             if (version >= 1)
/*     */             {
/* 188 */               String newFilePath = new File(SystemProperties.getApplicationPath(), "azureus.new").getAbsolutePath();
/*     */               
/* 190 */               FileUtil.writeBytesAsFile(newFilePath, startupScript.getBytes());
/*     */               
/* 192 */               String s = "cp \"" + newFilePath + "\" \"" + oldFilePathString + "\"; chmod +x \"" + oldFilePathString + "\"; echo \"Script Update successful\"";
/*     */               
/*     */ 
/*     */ 
/* 196 */               ScriptAfterShutdown.addExtraCommand(s);
/* 197 */               ScriptAfterShutdown.setRequiresExit(true);
/*     */               
/* 199 */               bInformUserManual = false;
/*     */             }
/*     */             else {
/*     */               try {
/* 203 */                 FileUtil.writeBytesAsFile(oldFilePathString, startupScript.getBytes());
/*     */                 
/* 205 */                 Runtime.getRuntime().exec(new String[] { findCommand("chmod"), "+x", oldStartupScript });
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 211 */                 bInformUserManual = false;
/*     */               }
/*     */               catch (Throwable t) {}
/*     */             }
/*     */           }
/*     */           
/* 217 */           if (bInformUserManual) {
/* 218 */             String newFilePath = new File(SystemProperties.getApplicationPath(), "azureus.new").getAbsolutePath();
/*     */             
/* 220 */             FileUtil.writeBytesAsFile(newFilePath, startupScript.getBytes());
/* 221 */             showScriptManualUpdateDialog(newFilePath, oldFilePathString, latestVersion);
/*     */           }
/*     */           else {
/* 224 */             showScriptAutoUpdateDialog();
/*     */           }
/*     */         }
/*     */       }
/*     */       return;
/*     */     } catch (Throwable t) {
/* 230 */       t.printStackTrace();
/*     */     } finally {
/*     */       try {
/* 233 */         stream.close();
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String findCommand(String name)
/*     */   {
/* 243 */     String[] locations = { "/bin", "/usr/bin" };
/*     */     
/* 245 */     for (String s : locations)
/*     */     {
/* 247 */       File f = new File(s, name);
/*     */       
/* 249 */       if ((f.exists()) && (f.canRead()))
/*     */       {
/* 251 */         return f.getAbsolutePath();
/*     */       }
/*     */     }
/*     */     
/* 255 */     return name;
/*     */   }
/*     */   
/*     */   private void showScriptManualUpdateDialog(String newFilePath, String oldFilePath, final int version)
/*     */   {
/* 260 */     final UIFunctions uif = UIFunctionsManager.getUIFunctions();
/* 261 */     if (uif != null) {
/* 262 */       final String sCopyLine = "cp \"" + newFilePath + "\" \"" + oldFilePath + "\"";
/* 263 */       uif.promptUser(MessageText.getString("unix.script.new.title"), MessageText.getString("unix.script.new.text", new String[] { newFilePath, sCopyLine }), new String[] { MessageText.getString("unix.script.new.button.quit"), MessageText.getString("unix.script.new.button.continue"), MessageText.getString("unix.script.new.button.asknomore") }, 0, null, null, false, 0, new UserPrompterResultListener()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void prompterClosed(int answer)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 274 */           if (answer == 0) {
/* 275 */             System.out.println("The line you should run:\n" + sCopyLine);
/* 276 */             uif.dispose(false, false);
/* 277 */           } else if (answer == 2) {
/* 278 */             COConfigurationManager.setParameter("unix.script.lastaskversion", version);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     else {
/* 284 */       System.out.println("NO UIF");
/*     */     }
/*     */   }
/*     */   
/*     */   private void showScriptAutoUpdateDialog() {
/* 289 */     final UIFunctions uif = UIFunctionsManager.getUIFunctions();
/* 290 */     if (uif != null) {
/* 291 */       uif.promptUser(MessageText.getString("unix.script.new.auto.title"), MessageText.getString("unix.script.new.auto.text", new String[0]), new String[] { MessageText.getString("UpdateWindow.restart"), MessageText.getString("UpdateWindow.restartLater") }, 0, null, null, false, 0, new UserPrompterResultListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void prompterClosed(int answer)
/*     */         {
/*     */ 
/* 298 */           if (answer == 0) {
/* 299 */             uif.dispose(true, false);
/*     */           }
/*     */         }
/*     */       });
/*     */     } else {
/* 304 */       System.out.println("NO UIF");
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/unix/PlatformManagerUnixPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */