/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreComponent;
/*     */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*     */ import com.aelitis.azureus.core.impl.AzureusCoreSingleInstanceClient;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.update.UpdateInstaller;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManager;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
/*     */ import org.gudy.azureus2.ui.swt.sharing.ShareUtils;
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
/*     */ public class StartServer
/*     */ {
/*  50 */   private static final LogIDs LOGID = LogIDs.GUI;
/*     */   
/*     */   private ServerSocket socket;
/*     */   
/*     */   private int state;
/*     */   
/*     */   private boolean bContinue;
/*     */   public static final int STATE_FAULTY = 0;
/*     */   public static final int STATE_LISTENING = 1;
/*  59 */   protected List queued_torrents = new ArrayList();
/*  60 */   protected boolean core_started = false;
/*  61 */   protected AEMonitor this_mon = new AEMonitor("StartServer");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public StartServer()
/*     */   {
/*     */     try
/*     */     {
/*  70 */       this.socket = new ServerSocket(Constants.INSTANCE_PORT, 50, InetAddress.getByName("127.0.0.1"));
/*     */       
/*  72 */       this.state = 1;
/*     */       
/*  74 */       if (Logger.isEnabled()) {
/*  75 */         Logger.log(new LogEvent(LOGID, "StartServer: listening on 127.0.0.1:" + Constants.INSTANCE_PORT + " for passed torrent info"));
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */ 
/*  83 */       this.state = 0;
/*  84 */       String reason = t.getMessage() == null ? "<>" : t.getMessage();
/*     */       
/*  86 */       System.out.println("StartServer ERROR: unable to bind to 127.0.0.1:" + Constants.INSTANCE_PORT + " listening" + " for passed torrent info: " + reason);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void pollForConnections(final AzureusCore azureus_core)
/*     */   {
/*  96 */     azureus_core.addLifecycleListener(new AzureusCoreLifecycleAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void componentCreated(AzureusCore core, AzureusCoreComponent component)
/*     */       {
/*     */ 
/*     */ 
/* 104 */         if ((component instanceof UIFunctionsSWT))
/*     */         {
/* 106 */           StartServer.this.openQueuedTorrents();
/*     */         }
/*     */       }
/*     */     });
/*     */     
/* 111 */     if (this.socket != null)
/*     */     {
/* 113 */       Thread t = new AEThread("Start Server")
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/* 119 */           StartServer.this.pollForConnectionsSupport(azureus_core);
/*     */         }
/*     */         
/* 122 */       };
/* 123 */       t.setDaemon(true);
/*     */       
/* 125 */       t.start();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void pollForConnectionsSupport(AzureusCore core)
/*     */   {
/* 133 */     this.bContinue = true;
/* 134 */     for (;;) { if (this.bContinue) {
/* 135 */         BufferedReader br = null;
/*     */         try {
/* 137 */           Socket sck = this.socket.accept();
/*     */           
/* 139 */           AzureusCoreSingleInstanceClient.sendReply(sck);
/*     */           
/* 141 */           String address = sck.getInetAddress().getHostAddress();
/* 142 */           if ((address.equals("localhost")) || (address.equals("127.0.0.1"))) {
/* 143 */             br = new BufferedReader(new InputStreamReader(sck.getInputStream(), "UTF8"));
/* 144 */             String line = br.readLine();
/*     */             
/*     */ 
/* 147 */             if (Logger.isEnabled()) {
/* 148 */               Logger.log(new LogEvent(LOGID, "Main::startServer: received '" + line + "'"));
/*     */             }
/*     */             
/* 151 */             if (line != null) {
/* 152 */               String[] args = parseArgs(line);
/* 153 */               if ((args != null) && (args.length > 0)) {
/* 154 */                 String debug_str = args[0];
/* 155 */                 for (int i = 1; i < args.length; i++) {
/* 156 */                   debug_str = debug_str + " ; " + args[i];
/*     */                 }
/* 158 */                 Logger.log(new LogEvent(LOGID, "Main::startServer: decoded to '" + debug_str + "'"));
/* 159 */                 processArgs(core, args);
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 164 */           sck.close();
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           try
/*     */           {
/* 173 */             if (br != null) {
/* 174 */               br.close();
/*     */             }
/*     */           }
/*     */           catch (Exception e) {}
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 168 */           if (!(e instanceof SocketException)) {
/* 169 */             Debug.printStackTrace(e);
/*     */           }
/*     */         } finally {
/*     */           try {
/* 173 */             if (br != null)
/* 174 */               br.close();
/*     */           } catch (Exception e) {}
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static String[] parseArgs(String line) {
/* 182 */     if (!line.startsWith("Azureus Start Server Access;")) { return null;
/*     */     }
/*     */     
/*     */ 
/* 186 */     ArrayList parts = new ArrayList();
/* 187 */     StringBuilder buf = new StringBuilder();
/* 188 */     boolean escape_mode = false;
/*     */     
/* 190 */     for (int i = "Azureus Start Server Access".length() + 1; i < line.length(); i++) {
/* 191 */       char c = line.charAt(i);
/* 192 */       if (escape_mode) { buf.append(c);escape_mode = false;
/* 193 */       } else if (c == '&') { escape_mode = true;
/* 194 */       } else if (c == ';') { parts.add(buf.toString());buf.setLength(0);
/* 195 */       } else { buf.append(c);
/*     */       } }
/* 197 */     if (buf.length() > 0) parts.add(buf.toString());
/* 198 */     return (String[])parts.toArray(new String[parts.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void processArgs(AzureusCore core, String[] args)
/*     */   {
/* 207 */     if ((args.length < 1) || (!args[0].equals("args")))
/*     */     {
/* 209 */       return;
/*     */     }
/*     */     
/* 212 */     boolean showMainWindow = (args.length == 1) || (COConfigurationManager.getBooleanParameter("Activate Window On External Download"));
/*     */     
/* 214 */     boolean open = true;
/*     */     
/* 216 */     for (int i = 1; i < args.length; i++)
/*     */     {
/* 218 */       String arg = args[i];
/*     */       
/* 220 */       if (i == 1)
/*     */       {
/* 222 */         if ((arg.equalsIgnoreCase("--closedown")) || (arg.equalsIgnoreCase("--shutdown")))
/*     */         {
/*     */ 
/*     */           try
/*     */           {
/*     */ 
/* 228 */             UpdateManager um = core.getPluginManager().getDefaultPluginInterface().getUpdateManager();
/*     */             
/* 230 */             UpdateInstaller[] installers = um.getInstallers();
/*     */             
/* 232 */             for (UpdateInstaller installer : installers)
/*     */             {
/* 234 */               installer.destroy();
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           
/* 239 */           UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*     */           
/* 241 */           if (uiFunctions != null)
/*     */           {
/* 243 */             uiFunctions.dispose(false, false);
/*     */           }
/*     */           
/* 246 */           return;
/*     */         }
/* 248 */         if (arg.equalsIgnoreCase("--restart"))
/*     */         {
/* 250 */           UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*     */           
/* 252 */           if (uiFunctions != null)
/*     */           {
/* 254 */             uiFunctions.dispose(true, false);
/*     */           }
/*     */           
/* 257 */           return;
/*     */         }
/* 259 */         if (arg.equalsIgnoreCase("--open"))
/*     */         {
/* 261 */           showMainWindow = true;
/*     */           
/* 263 */           continue;
/*     */         }
/* 265 */         if (arg.equalsIgnoreCase("--share"))
/*     */         {
/* 267 */           showMainWindow = true;
/*     */           
/* 269 */           open = false;
/*     */           
/* 271 */           continue;
/*     */         }
/*     */       }
/*     */       
/* 275 */       String file_name = arg;
/*     */       
/* 277 */       File file = new File(file_name);
/*     */       
/* 279 */       if ((!file.exists()) && (!isURI(file_name)))
/*     */       {
/* 281 */         String magnet_uri = UrlUtils.normaliseMagnetURI(file_name);
/*     */         
/* 283 */         if (magnet_uri != null)
/*     */         {
/* 285 */           file_name = magnet_uri;
/*     */         }
/*     */       }
/*     */       
/* 289 */       if (isURI(file_name))
/*     */       {
/* 291 */         if (Logger.isEnabled()) {
/* 292 */           Logger.log(new LogEvent(LOGID, "StartServer: args[" + i + "] handling as a URI: " + file_name));
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       else {
/*     */         try
/*     */         {
/* 300 */           if (!file.exists())
/*     */           {
/* 302 */             throw new Exception("File not found");
/*     */           }
/*     */           
/* 305 */           file_name = file.getCanonicalPath();
/*     */           
/* 307 */           Logger.log(new LogEvent(LOGID, "StartServer: file = " + file_name));
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 311 */           Logger.log(new LogAlert(true, 3, "Failed to access torrent file '" + file_name + "'. Ensure sufficient temporary file space " + "available (check browser cache usage)."));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 318 */       boolean queued = false;
/*     */       try
/*     */       {
/* 321 */         this.this_mon.enter();
/*     */         
/* 323 */         if (!this.core_started)
/*     */         {
/* 325 */           this.queued_torrents.add(new Object[] { file_name, Boolean.valueOf(open) });
/*     */           
/* 327 */           queued = true;
/*     */         }
/*     */       }
/*     */       finally {
/* 331 */         this.this_mon.exit();
/*     */       }
/*     */       
/* 334 */       if (!queued)
/*     */       {
/* 336 */         handleFile(file_name, open);
/*     */       }
/*     */     }
/*     */     
/* 340 */     if (showMainWindow) {
/* 341 */       showMainWindow();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean isURI(String file_name)
/*     */   {
/* 349 */     String file_name_lower = file_name.toLowerCase();
/*     */     
/* 351 */     return (file_name_lower.startsWith("http:")) || (file_name_lower.startsWith("https:")) || (file_name_lower.startsWith("magnet:")) || (file_name_lower.startsWith("maggot:")) || (file_name_lower.startsWith("bc:")) || (file_name_lower.startsWith("bctp:")) || (file_name_lower.startsWith("dht:"));
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
/*     */   protected void handleFile(String file_name, boolean open)
/*     */   {
/*     */     try
/*     */     {
/* 367 */       if (open)
/*     */       {
/* 369 */         TorrentOpener.openTorrent(file_name);
/*     */       }
/*     */       else
/*     */       {
/* 373 */         File f = new File(file_name);
/*     */         
/* 375 */         if (f.isDirectory())
/*     */         {
/* 377 */           ShareUtils.shareDir(file_name);
/*     */         }
/*     */         else
/*     */         {
/* 381 */           ShareUtils.shareFile(file_name);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 386 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void openQueuedTorrents()
/*     */   {
/*     */     try
/*     */     {
/* 394 */       this.this_mon.enter();
/*     */       
/* 396 */       this.core_started = true;
/*     */     }
/*     */     finally
/*     */     {
/* 400 */       this.this_mon.exit();
/*     */     }
/*     */     
/* 403 */     for (int i = 0; i < this.queued_torrents.size(); i++)
/*     */     {
/* 405 */       Object[] entry = (Object[])this.queued_torrents.get(i);
/*     */       
/* 407 */       String file_name = (String)entry[0];
/* 408 */       boolean open = ((Boolean)entry[1]).booleanValue();
/*     */       
/* 410 */       handleFile(file_name, open);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void showMainWindow()
/*     */   {
/* 417 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 418 */     if (uiFunctions != null) {
/* 419 */       uiFunctions.bringToFront();
/*     */     }
/*     */   }
/*     */   
/*     */   public void stopIt() {
/* 424 */     this.bContinue = false;
/*     */     try {
/* 426 */       if (this.socket != null) {
/* 427 */         this.socket.close();
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */   }
/*     */   
/*     */ 
/*     */   public int getState()
/*     */   {
/* 436 */     return this.state;
/*     */   }
/*     */   
/*     */   public static void main(String[] args)
/*     */   {
/* 441 */     String[] input_tests = { "a;b;c", "test", "Azureus Start Server Access;b;c;d", "Azureus Start Server Access;b;c&;d;e", "Azureus Start Server Access;b;c&&;d;e", "Azureus Start Server Access;b;c&&&;d;e" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 450 */     String[][] output_results = { null, null, { "b", "c", "d" }, { "b", "c;d", "e" }, { "b", "c&", "d", "e" }, { "b", "c&;d", "e" } };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 459 */     for (int i = 0; i < input_tests.length; i++) {
/* 460 */       System.out.println("Testing: " + input_tests[i]);
/* 461 */       String[] result = parseArgs(input_tests[i]);
/* 462 */       if ((result != output_results[i]) && 
/* 463 */         (!Arrays.equals(result, output_results[i]))) {
/* 464 */         System.out.println("TEST FAILED");
/* 465 */         System.out.println("  Expected: " + Arrays.asList(output_results[i]));
/* 466 */         System.out.println("  Decoded : " + Arrays.asList(result));
/* 467 */         System.exit(1);
/*     */       }
/*     */     }
/* 470 */     System.out.println("Done.");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/StartServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */