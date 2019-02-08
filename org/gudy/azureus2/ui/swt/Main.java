/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreException;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.launcher.Launcher;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class Main
/*     */ {
/*  40 */   private static final LogIDs LOGID = LogIDs.GUI;
/*     */   
/*     */   public static final String PR_MULTI_INSTANCE = "MULTI_INSTANCE";
/*     */   
/*     */   StartServer startServer;
/*  45 */   public static long startTime = System.currentTimeMillis();
/*     */   
/*     */ 
/*     */   public Main(String[] args)
/*     */   {
/*     */     try
/*     */     {
/*  52 */       if (Launcher.checkAndLaunch(Main.class, args)) {
/*  53 */         return;
/*     */       }
/*     */       
/*     */ 
/*  57 */       COConfigurationManager.preInitialise();
/*     */       
/*  59 */       Constructor constructor = null;
/*     */       try {
/*  61 */         Class az3Class = Class.forName("com.aelitis.azureus.ui.swt.Initializer");
/*     */         
/*  63 */         constructor = az3Class.getConstructor(new Class[] { AzureusCore.class, StartServer.class, String[].class });
/*     */ 
/*     */       }
/*     */       catch (ClassNotFoundException cnfe)
/*     */       {
/*     */ 
/*  69 */         System.err.println(cnfe.toString() + "\nDid you include the azureus3 module?");
/*  70 */         return;
/*     */       }
/*     */       catch (Throwable t) {
/*  73 */         t.printStackTrace();
/*     */         
/*  75 */         return;
/*     */       }
/*     */       
/*     */ 
/*  79 */       String mi_str = System.getProperty("MULTI_INSTANCE");
/*     */       
/*  81 */       boolean mi = (mi_str != null) && (mi_str.equalsIgnoreCase("true"));
/*     */       
/*  83 */       this.startServer = new StartServer();
/*     */       
/*     */ 
/*     */ 
/*  87 */       boolean debugGUI = Boolean.getBoolean("debug");
/*     */       
/*  89 */       if ((mi) || (debugGUI))
/*     */       {
/*     */ 
/*     */ 
/*  93 */         AzureusCore core = AzureusCoreFactory.create();
/*     */         
/*  95 */         constructor.newInstance(new Object[] { core, this.startServer, args });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 101 */         return;
/*     */       }
/*     */       
/*     */ 
/* 105 */       if (processParams(args, this.startServer))
/*     */       {
/* 107 */         AzureusCore core = AzureusCoreFactory.create();
/*     */         
/* 109 */         this.startServer.pollForConnections(core);
/*     */         
/* 111 */         constructor.newInstance(new Object[] { core, this.startServer, args });
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (AzureusCoreException e)
/*     */     {
/*     */ 
/* 121 */       Logger.log(new LogEvent(LOGID, "Start failed", e));
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 125 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean processParams(String[] args, StartServer startServer)
/*     */   {
/* 137 */     boolean closedown = false;
/*     */     
/* 139 */     boolean another_instance = startServer.getState() != 1;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 145 */     if (another_instance) {
/* 146 */       System.setProperty("transitory.startup", "1");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 151 */     for (int i = 0; i < args.length; i++)
/*     */     {
/* 153 */       String arg = args[i];
/*     */       
/* 155 */       if ((arg.equalsIgnoreCase("--closedown")) || (arg.equalsIgnoreCase("--shutdown")) || (arg.equalsIgnoreCase("--restart")))
/*     */       {
/* 157 */         closedown = true;
/*     */         
/* 159 */         break;
/*     */       }
/* 161 */       if (!arg.equalsIgnoreCase("--open"))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 170 */         String filename = arg;
/*     */         
/* 172 */         if (filename.length() == 40)
/*     */         {
/* 174 */           byte[] hash = null;
/*     */           try
/*     */           {
/* 177 */             hash = ByteFormatter.decodeString(filename);
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           
/*     */ 
/* 182 */           if ((hash != null) && (hash.length == 20))
/*     */           {
/* 184 */             filename = "magnet:?xt=urn:btih:" + Base32.encode(hash);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 190 */         if (filename.length() == 32)
/*     */         {
/* 192 */           byte[] hash = null;
/*     */           try
/*     */           {
/* 195 */             hash = Base32.decode(filename);
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           
/*     */ 
/* 200 */           if ((hash != null) && (hash.length == 20))
/*     */           {
/* 202 */             filename = "magnet:?xt=urn:btih:" + filename;
/*     */           }
/*     */         }
/*     */         
/* 206 */         if ((filename.toUpperCase().startsWith("HTTP:")) || (filename.toUpperCase().startsWith("HTTPS:")) || (filename.toUpperCase().startsWith("MAGNET:")) || (filename.toUpperCase().startsWith("MAGGOT:")) || (filename.toUpperCase().startsWith("BC:")) || (filename.toUpperCase().startsWith("BCTP:")) || (filename.toUpperCase().startsWith("DHT:")))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 214 */           if (!another_instance)
/*     */           {
/* 216 */             Logger.log(new LogEvent(LOGID, "Main::main: args[" + i + "] handling as a URI: " + filename));
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         else {
/*     */           try
/*     */           {
/* 224 */             File file = new File(filename);
/*     */             
/* 226 */             if (!file.exists())
/*     */             {
/* 228 */               throw new Exception("File '" + file + "' not found");
/*     */             }
/*     */             
/* 231 */             args[i] = file.getCanonicalPath();
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 236 */             if ((!another_instance) && (Logger.isEnabled()))
/*     */             {
/* 238 */               Logger.log(new LogEvent(LOGID, "Main::main: args[" + i + "] exists = " + new File(filename).exists()));
/*     */             }
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 243 */             if (another_instance)
/*     */             {
/* 245 */               e.printStackTrace();
/*     */             }
/*     */             else
/*     */             {
/* 249 */               Logger.log(new LogAlert(true, 3, "Failed to access torrent file '" + filename + "'. Ensure sufficient temporary " + "file space available (check browser cache usage)."));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 258 */     if (another_instance)
/*     */     {
/*     */ 
/*     */ 
/* 262 */       args = CocoaMagic(args);
/*     */       
/* 264 */       StartSocket ss = new StartSocket(args);
/*     */       
/* 266 */       if (!ss.sendArgs())
/*     */       {
/*     */ 
/*     */ 
/* 270 */         another_instance = false;
/*     */         
/* 272 */         String msg = "There appears to be another process already listening on socket [127.0.0.1:" + Constants.INSTANCE_PORT + "].\n\nLocate and terminate the other program or change the control port - <a href=\"http://wiki.vuze.com/w/Commandline_options#Changing_the_Control_Port\">see the wiki for details</a>.\n\nIf you don't then bad things will happen!";
/*     */         
/* 274 */         System.err.println(msg);
/*     */         
/* 276 */         Logger.log(new LogAlert(true, 1, msg));
/*     */       }
/*     */     }
/*     */     
/* 280 */     if (!another_instance)
/*     */     {
/* 282 */       if (closedown)
/*     */       {
/* 284 */         return false;
/*     */       }
/*     */       
/* 287 */       return true;
/*     */     }
/* 289 */     return false;
/*     */   }
/*     */   
/*     */   private static String[] CocoaMagic(String[] args)
/*     */   {
/* 294 */     if (!Constants.isOSX) {
/* 295 */       return args;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 300 */       System.setProperty("osxaccess.light", "1");
/*     */       
/* 302 */       Class<?> claOSXAccess = Class.forName("org.gudy.azureus2.platform.macosx.access.jnilib.OSXAccess");
/* 303 */       if (claOSXAccess != null) {
/* 304 */         Method method = claOSXAccess.getMethod("runLight", new Class[] { String[].class });
/*     */         
/*     */ 
/* 307 */         Object invoke = method.invoke(null, new Object[] { args });
/*     */         
/*     */ 
/* 310 */         return (String[])invoke;
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 315 */       Debug.printStackTrace(e);
/*     */     }
/* 317 */     return args;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 324 */     if (Launcher.checkAndLaunch(Main.class, args)) {
/* 325 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 330 */     if (System.getProperty("ui.temp") == null) {
/* 331 */       System.setProperty("ui.temp", "az2");
/*     */     }
/*     */     
/* 334 */     new Main(args);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/Main.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */