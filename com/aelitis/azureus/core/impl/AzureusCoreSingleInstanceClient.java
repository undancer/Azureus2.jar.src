/*     */ package com.aelitis.azureus.core.impl;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AzureusCoreSingleInstanceClient
/*     */ {
/*     */   public static final String ACCESS_STRING = "Azureus Start Server Access";
/*     */   private static final int CONNECT_TIMEOUT = 500;
/*     */   private static final int READ_TIMEOUT = 5000;
/*     */   
/*     */   public boolean sendArgs(String[] args, int max_millis_to_wait)
/*     */   {
/*  52 */     long start = System.currentTimeMillis();
/*     */     
/*     */     for (;;)
/*     */     {
/*  56 */       long connect_start = System.currentTimeMillis();
/*     */       
/*  58 */       if (connect_start < start)
/*     */       {
/*  60 */         start = connect_start;
/*     */       }
/*     */       
/*  63 */       if (connect_start - start > max_millis_to_wait)
/*     */       {
/*  65 */         return false;
/*     */       }
/*     */       
/*  68 */       Socket sock = null;
/*     */       try
/*     */       {
/*  71 */         sock = new Socket();
/*     */         
/*  73 */         sock.connect(new InetSocketAddress("127.0.0.1", Constants.INSTANCE_PORT), 500);
/*     */         
/*  75 */         sock.setSoTimeout(5000);
/*     */         
/*  77 */         PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
/*     */         
/*  79 */         StringBuilder buffer = new StringBuilder("Azureus Start Server Access;args;");
/*     */         
/*  81 */         for (int i = 0; i < args.length; i++)
/*     */         {
/*  83 */           String arg = args[i].replaceAll("&", "&&").replaceAll(";", "&;");
/*     */           
/*  85 */           buffer.append(arg);
/*     */           
/*  87 */           buffer.append(';');
/*     */         }
/*     */         
/*  90 */         pw.println(buffer.toString());
/*     */         
/*  92 */         pw.flush();
/*     */         
/*  94 */         if (!receiveReply(sock))
/*     */         {
/*  96 */           return 0;
/*     */         }
/*     */         
/*  99 */         return 1;
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 103 */         long connect_end = System.currentTimeMillis();
/*     */         
/* 105 */         long time_taken = connect_end - connect_start;
/*     */         
/* 107 */         if (time_taken < 500L) {
/*     */           try
/*     */           {
/* 110 */             Thread.sleep(500L - time_taken);
/*     */           }
/*     */           catch (Throwable f) {}
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/*     */         try {
/* 118 */           if (sock != null)
/*     */           {
/* 120 */             sock.close();
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean sendReply(Socket socket)
/*     */   {
/*     */     try
/*     */     {
/* 137 */       OutputStream os = socket.getOutputStream();
/*     */       
/* 139 */       os.write("Azureus Start Server Access;".getBytes("UTF-8"));
/*     */       
/* 141 */       os.flush();
/*     */       
/* 143 */       return true;
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/* 148 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean receiveReply(Socket socket)
/*     */   {
/*     */     try
/*     */     {
/* 156 */       InputStream is = socket.getInputStream();
/*     */       
/* 158 */       socket.setSoTimeout(15000);
/*     */       
/* 160 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */       
/*     */       for (;;)
/*     */       {
/* 164 */         int data = is.read();
/*     */         
/* 166 */         if (data == -1) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/* 171 */         byte b = (byte)data;
/*     */         
/* 173 */         if (b == 59)
/*     */         {
/* 175 */           String str = new String(baos.toByteArray(), "UTF-8");
/*     */           
/* 177 */           return str.equals("Azureus Start Server Access");
/*     */         }
/*     */         
/*     */ 
/* 181 */         baos.write(b);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/* 187 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 194 */     new AzureusCoreSingleInstanceClient().sendArgs(new String[] { "6C0B39D9897AF42F624AC2DE010CF33F55CB45EC" }, 30000);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/impl/AzureusCoreSingleInstanceClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */