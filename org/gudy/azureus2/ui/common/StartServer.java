/*     */ package org.gudy.azureus2.ui.common;
/*     */ 
/*     */ import com.aelitis.azureus.core.impl.AzureusCoreSingleInstanceClient;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */   extends Thread
/*     */ {
/*     */   private ServerSocket socket;
/*     */   private int state;
/*     */   private boolean bContinue;
/*     */   public static final int STATE_FAULTY = 0;
/*     */   public static final int STATE_LISTENING = 1;
/*     */   
/*     */   public StartServer()
/*     */   {
/*  50 */     super("Start Server");
/*  51 */     int instance_port = Constants.INSTANCE_PORT;
/*     */     try {
/*  53 */       this.socket = new ServerSocket(instance_port, 50, InetAddress.getByName("127.0.0.1"));
/*  54 */       this.state = 1;
/*  55 */       Logger.getLogger("azureus2").info("StartServer: listening on 127.0.0.1:" + instance_port + " for passed torrent info");
/*     */     } catch (Exception e) {
/*  57 */       this.state = 0;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  62 */       System.out.println("StartServer ERROR: unable to bind to 127.0.0.1:" + instance_port + " for passed torrent info");
/*     */     }
/*     */   }
/*     */   
/*     */   public void run() {
/*  67 */     this.bContinue = true;
/*  68 */     for (;;) { if (this.bContinue) {
/*  69 */         BufferedReader br = null;
/*     */         try {
/*  71 */           Socket sck = this.socket.accept();
/*     */           
/*  73 */           AzureusCoreSingleInstanceClient.sendReply(sck);
/*     */           
/*  75 */           String address = sck.getInetAddress().getHostAddress();
/*  76 */           if ((address.equals("localhost")) || (address.equals("127.0.0.1"))) {
/*  77 */             br = new BufferedReader(new InputStreamReader(sck.getInputStream()));
/*  78 */             String line = br.readLine();
/*     */             
/*  80 */             if (line != null)
/*     */             {
/*  82 */               StringTokenizer st = new StringTokenizer(line, ";");
/*  83 */               List argsList = new ArrayList();
/*  84 */               while (st.hasMoreElements())
/*  85 */                 argsList.add(st.nextToken().replaceAll("&;", ";").replaceAll("&&", "&"));
/*  86 */               if (argsList.size() > 1)
/*     */               {
/*  88 */                 String checker = (String)argsList.remove(0);
/*  89 */                 if (checker.equals("Azureus Start Server Access")) {
/*  90 */                   if (argsList.get(0).equals("args")) {
/*  91 */                     argsList.remove(0);
/*  92 */                     String[] newargs = new String[argsList.size()];
/*  93 */                     argsList.toArray(newargs);
/*  94 */                     Main.processArgs(newargs, null, null);
/*     */                   } else {
/*  96 */                     Logger.getLogger("azureus2").error("Something strange was sent to the StartServer: " + line);
/*     */                   }
/*     */                 } else {
/*  99 */                   Logger.getLogger("azureus2").error("StartServer: Wrong access token.");
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 105 */           sck.close();
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           try
/*     */           {
/* 113 */             if (br != null) {
/* 114 */               br.close();
/*     */             }
/*     */           }
/*     */           catch (Exception e) {}
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 108 */           if (!(e instanceof SocketException))
/* 109 */             e.printStackTrace();
/* 110 */           this.bContinue = false;
/*     */         } finally {
/*     */           try {
/* 113 */             if (br != null)
/* 114 */               br.close();
/*     */           } catch (Exception e) {}
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void stopIt() {
/* 122 */     this.bContinue = false;
/*     */     try {
/* 124 */       this.socket.close();
/*     */     }
/*     */     catch (Exception e) {}
/*     */   }
/*     */   
/*     */ 
/*     */   public int getServerState()
/*     */   {
/* 132 */     return this.state;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/common/StartServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */