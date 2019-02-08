/*     */ package org.gudy.azureus2.ui.telnet;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.ui.common.UIConst;
/*     */ import org.gudy.azureus2.ui.console.UserProfile;
/*     */ import org.gudy.azureus2.ui.console.multiuser.UserManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class SocketServer
/*     */   implements Runnable
/*     */ {
/*     */   private final ServerSocket serverSocket;
/*     */   private final Set allowedHosts;
/*     */   private final int maxLoginAttempts;
/*     */   private final UserManager userManager;
/*     */   private final UI ui;
/*     */   
/*     */   public SocketServer(UI ui, int port, Set allowedHosts, UserManager userManager, int maxLoginAttempts)
/*     */     throws IOException
/*     */   {
/*  58 */     this.ui = ui;
/*  59 */     this.allowedHosts = allowedHosts;
/*  60 */     this.userManager = userManager;
/*  61 */     this.serverSocket = new ServerSocket(port);
/*  62 */     this.maxLoginAttempts = maxLoginAttempts;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void run()
/*     */   {
/*  72 */     int threadNum = 1;
/*  73 */     System.out.println("Telnet server started. Listening on port: " + this.serverSocket.getLocalPort());
/*     */     
/*  75 */     new AEThread2("AZCoreStartup", true)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*  80 */         UIConst.getAzureusCore();
/*     */       }
/*     */     }.start();
/*     */     try
/*     */     {
/*     */       for (;;) {
/*  86 */         Socket socket = this.serverSocket.accept();
/*     */         
/*  88 */         InetSocketAddress addr = (InetSocketAddress)socket.getRemoteSocketAddress();
/*     */         
/*  90 */         if ((addr.isUnresolved()) || (!isAllowed(addr))) {
/*  91 */           System.out.println("TelnetUI: rejecting connection from: " + addr + " as address is not allowed");
/*  92 */           socket.close();
/*     */         }
/*     */         else {
/*  95 */           System.out.println("TelnetUI: accepting connection from: " + addr);
/*  96 */           int loginAttempts = 0;
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */           for (;;)
/*     */           {
/* 103 */             UserProfile profile = login(socket.getInputStream(), socket.getOutputStream());
/*     */             
/*     */ 
/*     */ 
/* 107 */             if (profile != null)
/*     */             {
/*     */ 
/*     */ 
/* 111 */               this.ui.createNewConsoleInput("Telnet Console " + threadNum++, socket.getInputStream(), new PrintStream(socket.getOutputStream()), profile);
/* 112 */               break;
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 117 */             loginAttempts++;
/*     */             
/* 119 */             if (loginAttempts >= this.maxLoginAttempts) {
/* 120 */               System.out.println("TelnetUI: rejecting connection from: " + addr + " as number of failed connections > max login attempts (" + this.maxLoginAttempts + ")");
/* 121 */               socket.close();
/* 122 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       return;
/* 128 */     } catch (Throwable t) { t.printStackTrace();
/*     */     }
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
/*     */   private UserProfile login(InputStream in, OutputStream out)
/*     */     throws IOException
/*     */   {
/* 144 */     if (this.userManager == null) {
/* 145 */       return UserProfile.DEFAULT_USER_PROFILE;
/*     */     }
/* 147 */     PrintStream ps = new PrintStream(out);
/* 148 */     BufferedReader br = new BufferedReader(new InputStreamReader(in));
/* 149 */     ps.print("Username: ");
/* 150 */     String username = br.readLine();
/* 151 */     ps.print("Password: ");
/* 152 */     String password = br.readLine();
/* 153 */     UserProfile userProfile = this.userManager.authenticate(username, password);
/* 154 */     if (userProfile != null)
/*     */     {
/* 156 */       ps.println("Login successful");
/* 157 */       return userProfile;
/*     */     }
/* 159 */     ps.println("Login failed");
/* 160 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean isAllowed(InetSocketAddress addr)
/*     */   {
/* 169 */     InetAddress address = addr.getAddress();
/* 170 */     if (checkHost(address.getHostAddress()))
/* 171 */       return true;
/* 172 */     if (checkHost(address.getHostName())) {
/* 173 */       return true;
/*     */     }
/* 175 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean checkHost(String hostName)
/*     */   {
/* 184 */     if (hostName == null)
/* 185 */       return false;
/* 186 */     hostName = hostName.toLowerCase();
/*     */     
/* 188 */     for (Iterator iter = this.allowedHosts.iterator(); iter.hasNext();) {
/* 189 */       String allowedHost = (String)iter.next();
/* 190 */       if (hostName.equals(allowedHost))
/* 191 */         return true;
/*     */     }
/* 193 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/telnet/SocketServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */