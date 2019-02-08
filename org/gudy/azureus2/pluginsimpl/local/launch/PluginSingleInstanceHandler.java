/*     */ package org.gudy.azureus2.pluginsimpl.local.launch;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.plugins.PluginManagerArgumentHandler;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannelListener;
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
/*     */ public class PluginSingleInstanceHandler
/*     */ {
/*     */   private static boolean active;
/*     */   private static int port;
/*     */   private static PluginManagerArgumentHandler handler;
/*     */   
/*     */   public static void initialise(int _port, PluginManagerArgumentHandler _handler)
/*     */   {
/*  52 */     port = _port;
/*  53 */     handler = _handler;
/*     */     
/*  55 */     String multi_instance = System.getProperty("MULTI_INSTANCE");
/*     */     
/*  57 */     if ((multi_instance != null) && (multi_instance.equalsIgnoreCase("true")))
/*     */     {
/*  59 */       return;
/*     */     }
/*     */     
/*  62 */     active = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean initialiseAndProcess(int _port, PluginManagerArgumentHandler _handler, String[] _args)
/*     */   {
/*  71 */     initialise(_port, _handler);
/*     */     
/*  73 */     return process(null, _args);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static boolean process(LoggerChannelListener log, String[] args)
/*     */   {
/*  81 */     if (active)
/*     */     {
/*  83 */       if (startListener(log))
/*     */       {
/*  85 */         return false;
/*     */       }
/*     */       
/*     */ 
/*  89 */       sendArguments(log, args);
/*     */       
/*  91 */       return true;
/*     */     }
/*     */     
/*     */ 
/*  95 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static boolean startListener(final LoggerChannelListener log)
/*     */   {
/*     */     try
/*     */     {
/* 105 */       final ServerSocket server_socket = new ServerSocket(port, 50, InetAddress.getByName("127.0.0.1"));
/*     */       
/* 107 */       if (log != null) {
/* 108 */         log.messageLogged(1, "SingleInstanceHandler: listening on 127.0.0.1:" + port + " for passed arguments");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 113 */       Thread t = new Thread("Single Instance Handler")
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/*     */ 
/*     */           for (;;)
/*     */           {
/* 121 */             Socket socket = null;
/* 122 */             ObjectInputStream ois = null;
/*     */             try
/*     */             {
/* 125 */               socket = server_socket.accept();
/*     */               
/* 127 */               String address = socket.getInetAddress().getHostAddress();
/*     */               
/* 129 */               if ((!address.equals("localhost")) && (!address.equals("127.0.0.1")))
/*     */               {
/* 131 */                 socket.close();
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
/* 200 */                 if (ois != null) {
/*     */                   try {
/* 202 */                     ois.close();
/*     */                   }
/*     */                   catch (Throwable e) {}
/*     */                 }
/*     */                 
/*     */ 
/* 208 */                 if (socket == null)
/*     */                   continue;
/* 210 */                 try { socket.close();
/*     */                 }
/*     */                 catch (Throwable e) {}
/* 213 */                 continue;
/*     */               }
/* 136 */               ois = new ObjectInputStream(socket.getInputStream());
/*     */               
/* 138 */               ois.readInt();
/*     */               
/* 140 */               String header = (String)ois.readObject();
/*     */               
/* 142 */               if (!header.equals(PluginSingleInstanceHandler.getHeader()))
/*     */               {
/* 144 */                 if (log != null) {
/* 145 */                   log.messageLogged(3, "SingleInstanceHandler: invalid header - " + header);
/*     */                 }
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
/* 200 */                 if (ois != null) {
/*     */                   try {
/* 202 */                     ois.close();
/*     */                   }
/*     */                   catch (Throwable e) {}
/*     */                 }
/*     */                 
/*     */ 
/* 208 */                 if (socket == null)
/*     */                   continue;
/* 210 */                 try { socket.close();
/*     */                 }
/*     */                 catch (Throwable e) {}
/* 213 */                 continue;
/*     */               }
/* 153 */               String[] args = (String[])ois.readObject();
/*     */               
/* 155 */               String config_dir = System.getProperty("azureus.config.path", null);
/*     */               
/* 157 */               if (config_dir != null)
/*     */               {
/*     */ 
/*     */ 
/* 161 */                 String config_path = (String)ois.readObject();
/* 162 */                 String file_name = (String)ois.readObject();
/*     */                 
/* 164 */                 if (!config_path.equals(config_dir))
/*     */                 {
/* 166 */                   throw new Exception("Called supplied incorrect config path: " + config_path);
/*     */                 }
/*     */                 
/* 169 */                 File cmd_file = new File(new File(config_dir, "tmp"), file_name).getCanonicalFile();
/*     */                 
/* 171 */                 if (!cmd_file.getParentFile().getParentFile().equals(new File(config_dir)))
/*     */                 {
/* 173 */                   throw new Exception("Called supplied invalid file name: " + file_name);
/*     */                 }
/*     */                 
/* 176 */                 ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(cmd_file));
/*     */                 
/*     */                 try
/*     */                 {
/* 180 */                   args = (String[])ois2.readObject();
/*     */                 }
/*     */                 finally
/*     */                 {
/* 184 */                   ois2.close();
/*     */                   
/* 186 */                   cmd_file.delete();
/*     */                 }
/*     */               }
/*     */               
/* 190 */               PluginSingleInstanceHandler.handler.processArguments(args);
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 200 */               if (ois != null) {
/*     */                 try {
/* 202 */                   ois.close();
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/*     */               
/*     */ 
/* 208 */               if (socket != null) {
/*     */                 try {
/* 210 */                   socket.close();
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 194 */               if (log != null) {
/* 195 */                 log.messageLogged("SingleInstanceHandler: receive error", e);
/*     */               }
/*     */             }
/*     */             finally
/*     */             {
/* 200 */               if (ois != null) {
/*     */                 try {
/* 202 */                   ois.close();
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/*     */               
/*     */ 
/* 208 */               if (socket != null) {
/*     */                 try {
/* 210 */                   socket.close();
/*     */ 
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/*     */               
/*     */             }
/*     */           }
/*     */         }
/* 219 */       };
/* 220 */       t.setDaemon(true);
/*     */       
/* 222 */       t.start();
/*     */       
/* 224 */       return true;
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 228 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static void sendArguments(LoggerChannelListener log, String[] args)
/*     */   {
/* 237 */     Socket socket = null;
/*     */     try
/*     */     {
/* 240 */       socket = new Socket("127.0.0.1", port);
/*     */       
/* 242 */       ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
/*     */       
/* 244 */       oos.writeInt(0);
/*     */       
/* 246 */       oos.writeObject(getHeader());
/*     */       
/* 248 */       oos.writeObject(args);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 253 */       String config_dir = System.getProperty("azureus.config.path", null);
/*     */       
/* 255 */       if (config_dir != null)
/*     */       {
/* 257 */         File file = new File(config_dir, "tmp");
/*     */         
/* 259 */         file.mkdirs();
/*     */         
/* 261 */         file = File.createTempFile("AZU" + RandomUtils.nextSecureAbsoluteLong(), ".tmp", file);
/*     */         
/* 263 */         ObjectOutputStream oos2 = new ObjectOutputStream(new FileOutputStream(file));
/*     */         try
/*     */         {
/* 266 */           oos2.writeObject(args);
/*     */         }
/*     */         finally
/*     */         {
/* 270 */           oos2.close();
/*     */         }
/*     */         
/* 273 */         oos.writeObject(config_dir);
/*     */         
/* 275 */         oos.writeObject(file.getName());
/*     */       }
/*     */       
/* 278 */       oos.flush();
/*     */       
/* 280 */       if (log != null)
/*     */       {
/* 282 */         log.messageLogged(1, "SingleInstanceHandler: arguments passed to existing process");
/*     */       }
/*     */       return;
/*     */     } catch (Throwable e) {
/* 286 */       if (log != null)
/*     */       {
/* 288 */         log.messageLogged("SingleInstanceHandler: send error", e);
/*     */       }
/*     */     }
/*     */     finally {
/* 292 */       if (socket != null) {
/*     */         try {
/* 294 */           socket.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static String getHeader()
/*     */   {
/* 305 */     return SystemProperties.getApplicationName() + " Single Instance Handler";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/launch/PluginSingleInstanceHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */