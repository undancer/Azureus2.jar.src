/*     */ package org.gudy.azureus2.core3.util.protocol.magnet;
/*     */ 
/*     */ import com.aelitis.net.magneturi.MagnetURIHandler;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.Socket;
/*     */ import java.net.URL;
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
/*     */ public class MagnetConnection
/*     */   extends HttpURLConnection
/*     */ {
/*     */   private Socket socket;
/*     */   private static final String NL = "\r\n";
/*  46 */   private String status = "";
/*     */   
/*     */ 
/*     */ 
/*     */   protected MagnetConnection(URL _url)
/*     */   {
/*  52 */     super(_url);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void connect()
/*     */     throws IOException
/*     */   {
/*  60 */     this.socket = new Socket("127.0.0.1", MagnetURIHandler.getSingleton().getPort());
/*     */     
/*  62 */     String get = "GET /download/" + getURL().toString().substring(7) + " HTTP/1.0" + "\r\n" + "\r\n";
/*     */     
/*  64 */     this.socket.getOutputStream().write(get.getBytes());
/*     */     
/*  66 */     this.socket.getOutputStream().flush();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/*  74 */     InputStream is = this.socket.getInputStream();
/*     */     
/*  76 */     String line = "";
/*     */     
/*  78 */     byte[] buffer = new byte[1];
/*     */     
/*  80 */     byte[] line_bytes = new byte['ࠀ'];
/*  81 */     int line_bytes_pos = 0;
/*     */     
/*     */     for (;;)
/*     */     {
/*  85 */       int len = is.read(buffer);
/*     */       
/*  87 */       if (len == -1) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/*  92 */       line = line + (char)buffer[0];
/*     */       
/*  94 */       line_bytes[(line_bytes_pos++)] = buffer[0];
/*     */       
/*  96 */       if (line.endsWith("\r\n"))
/*     */       {
/*  98 */         line = line.trim();
/*     */         
/* 100 */         if (line.length() == 0) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/* 105 */         if (line.startsWith("X-Report:"))
/*     */         {
/* 107 */           line = new String(line_bytes, 0, line_bytes_pos, "UTF-8");
/*     */           
/* 109 */           line = line.substring(9);
/*     */           
/* 111 */           line = line.trim();
/*     */           
/* 113 */           this.status = (Character.toUpperCase(line.charAt(0)) + line.substring(1));
/*     */         }
/*     */         
/* 116 */         line = "";
/* 117 */         line_bytes_pos = 0;
/*     */       }
/*     */     }
/*     */     
/* 121 */     return is;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getResponseCode()
/*     */   {
/* 127 */     return 200;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getResponseMessage()
/*     */   {
/* 133 */     return this.status;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean usingProxy()
/*     */   {
/* 139 */     return false;
/*     */   }
/*     */   
/*     */   public void disconnect()
/*     */   {
/*     */     try
/*     */     {
/* 146 */       this.socket.close();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 150 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/protocol/magnet/MagnetConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */