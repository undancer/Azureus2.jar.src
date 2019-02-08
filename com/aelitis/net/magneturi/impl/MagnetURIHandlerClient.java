/*     */ package com.aelitis.net.magneturi.impl;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
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
/*     */ public class MagnetURIHandlerClient
/*     */ {
/*     */   protected static final String NL = "\r\n";
/*     */   
/*     */   private byte[] load(String url, int max_millis_to_wait)
/*     */   {
/*  37 */     long start = System.currentTimeMillis();
/*     */     label235:
/*     */     label359:
/*     */     label430:
/*     */     for (;;) {
/*  42 */       for (int i = 45100;; i++) { if (i > 45108)
/*     */           break label430;
/*  44 */         long now = System.currentTimeMillis();
/*     */         
/*  46 */         if (now < start)
/*     */         {
/*  48 */           start = now;
/*     */         }
/*     */         
/*  51 */         if (now - start > max_millis_to_wait)
/*     */         {
/*  53 */           return null;
/*     */         }
/*     */         
/*  56 */         Socket sock = null;
/*     */         try
/*     */         {
/*  59 */           sock = new Socket();
/*     */           
/*  61 */           sock.connect(new InetSocketAddress("127.0.0.1", i), 500);
/*     */           
/*  63 */           sock.setSoTimeout(5000);
/*     */           
/*  65 */           PrintWriter pw = new PrintWriter(sock.getOutputStream());
/*     */           
/*  67 */           pw.println("GET " + url + " HTTP/1.1" + "\r\n" + "\r\n");
/*     */           
/*  69 */           pw.flush();
/*     */           
/*  71 */           InputStream is = sock.getInputStream();
/*     */           
/*  73 */           String header = "";
/*     */           
/*  75 */           byte[] buffer = new byte[1];
/*     */           
/*     */           for (;;)
/*     */           {
/*  79 */             int len = is.read(buffer);
/*     */             
/*  81 */             if (len <= 0)
/*     */             {
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
/* 130 */               if (sock == null)
/*     */                 break;
/*     */               try {
/* 133 */                 sock.close();
/*     */               }
/*     */               catch (Throwable e) {}
/* 136 */               break;
/*     */             }
/*  86 */             header = header + new String(buffer, 0, len);
/*     */             
/*  88 */             if (header.endsWith("\r\n\r\n")) {
/*     */               break label235;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*  94 */           int pos = header.indexOf("\r\n");
/*     */           
/*  96 */           String first_line = header.substring(0, pos);
/*     */           
/*  98 */           if (!first_line.contains("200"))
/*     */           {
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
/* 130 */             if (sock != null) {
/*     */               try
/*     */               {
/* 133 */                 sock.close();
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 103 */             ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
/*     */             
/* 105 */             buffer = new byte['ࠀ'];
/*     */             int len;
/*     */             for (;;)
/*     */             {
/* 109 */               len = is.read(buffer);
/*     */               
/* 111 */               if (len <= 0) {
/*     */                 break label359;
/*     */               }
/*     */               
/*     */ 
/* 116 */               baos.write(buffer, 0, len);
/*     */               
/* 118 */               if (baos.size() > 524288)
/*     */               {
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
/* 130 */                 if (sock == null)
/*     */                   break;
/*     */                 try {
/* 133 */                   sock.close();
/*     */                 }
/*     */                 catch (Throwable e) {}
/* 136 */                 break;
/*     */               }
/*     */             }
/* 124 */             return baos.toByteArray();
/*     */           }
/*     */           
/*     */         }
/*     */         catch (Throwable e) {}finally
/*     */         {
/* 130 */           if (sock != null) {
/*     */             try
/*     */             {
/* 133 */               sock.close();
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean sendSetValue(String name, String value, int max_millis)
/*     */   {
/* 149 */     String msg = "/setinfo?name=" + name + "&value=" + value;
/*     */     
/* 151 */     byte[] response = load(msg, max_millis);
/*     */     
/* 153 */     if (response == null)
/*     */     {
/* 155 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 160 */     boolean success = response.length == 134;
/*     */     
/* 162 */     System.out.println(name + "=" + value + " -> " + success);
/*     */     
/* 164 */     return success;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 171 */     new MagnetURIHandlerClient().sendSetValue("AZMSG", "AZMSG;1;torrent;is-ready", 30000);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 176 */     new MagnetURIHandlerClient().sendSetValue("AZMSG", "AZMSG;1;torrent;load-torrent;{\"url\":\"http://www.vuze.com/download/VCCBRHY5GYNGFKPJSYQID4GB3XPTYGIG.torrent?referal=jws\",\"play-now\":true}", 30000);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/magneturi/impl/MagnetURIHandlerClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */