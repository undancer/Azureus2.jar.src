/*     */ package com.aelitis.azureus.core.devices.impl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.net.Socket;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TranscodePipeFileSource
/*     */   extends TranscodePipe
/*     */ {
/*     */   private static final String NL = "\r\n";
/*     */   private File source_file;
/*     */   private RandomAccessFile raf;
/*     */   private int raf_count;
/*     */   
/*     */   protected TranscodePipeFileSource(File _source_file, TranscodePipe.errorListener _error_listener)
/*     */     throws IOException
/*     */   {
/*  49 */     super(_error_listener);
/*     */     
/*  51 */     this.source_file = _source_file;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void handleSocket(Socket socket)
/*     */   {
/*  59 */     synchronized (this)
/*     */     {
/*  61 */       if (this.destroyed)
/*     */       {
/*     */         try {
/*  64 */           socket.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */         
/*     */ 
/*  69 */         return;
/*     */       }
/*     */       
/*  72 */       this.sockets.add(socket);
/*     */     }
/*     */     try
/*     */     {
/*  76 */       String command = null;
/*  77 */       Map<String, String> headers = new HashMap();
/*     */       
/*  79 */       InputStream is = socket.getInputStream();
/*  80 */       OutputStream os = socket.getOutputStream();
/*     */       
/*     */       for (;;)
/*     */       {
/*  84 */         String line = "";
/*     */         
/*  86 */         while (!line.endsWith("\r\n"))
/*     */         {
/*  88 */           byte[] buffer = new byte[1];
/*     */           
/*  90 */           if (is.read(buffer) <= 0)
/*     */           {
/*  92 */             throw new IOException("unexpected end of stream");
/*     */           }
/*     */           
/*  95 */           line = line + new String(buffer);
/*     */         }
/*     */         
/*  98 */         line = line.trim();
/*     */         
/* 100 */         if (line.length() == 0) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/* 105 */         if (command == null)
/*     */         {
/* 107 */           command = line;
/*     */         }
/*     */         else
/*     */         {
/* 111 */           int pos = line.indexOf(':');
/*     */           
/* 113 */           if (pos == -1)
/*     */           {
/* 115 */             return;
/*     */           }
/*     */           
/* 118 */           String lhs = line.substring(0, pos).trim().toLowerCase();
/* 119 */           String rhs = line.substring(pos + 1).trim();
/*     */           
/* 121 */           headers.put(lhs, rhs);
/*     */         }
/*     */       }
/*     */       
/* 125 */       boolean head = false;
/*     */       
/*     */ 
/*     */ 
/* 129 */       if (command == null)
/*     */       {
/* 131 */         throw new IOException("no method supplied");
/*     */       }
/* 133 */       if (!command.startsWith("GET "))
/*     */       {
/* 135 */         if (command.startsWith("HEAD "))
/*     */         {
/* 137 */           head = true;
/*     */         }
/*     */         else
/*     */         {
/* 141 */           throw new IOException("unsupported method '" + command + "'");
/*     */         }
/*     */       }
/* 144 */       long file_length = this.source_file.length();
/*     */       
/* 146 */       if (head)
/*     */       {
/* 148 */         write(os, "HTTP/1.1 200 OK\r\n");
/* 149 */         write(os, "Server: Azureus Media Server 1.0\r\n");
/* 150 */         write(os, "Accept-Ranges: bytes\r\n");
/* 151 */         write(os, "Content-Length: " + file_length + "\r\n");
/* 152 */         write(os, "Content-Range: 0-" + (file_length - 1L) + "/" + file_length + "\r\n");
/*     */         
/* 154 */         os.flush();
/*     */       }
/*     */       else
/*     */       {
/* 158 */         String ranges = (String)headers.get("range");
/*     */         
/* 160 */         long request_start = 0L;
/* 161 */         long request_length = 0L;
/*     */         
/* 163 */         boolean request_ok = false;
/*     */         
/* 165 */         if (ranges == null)
/*     */         {
/* 167 */           write(os, "HTTP/1.1 200 OK\r\n");
/* 168 */           write(os, "Server: Azureus Media Server 1.0\r\n");
/* 169 */           write(os, "Connection: close\r\n");
/* 170 */           write(os, "Accept-Ranges: bytes\r\n");
/* 171 */           write(os, "Content-Range: 0-" + (file_length - 1L) + "/" + file_length + "\r\n");
/* 172 */           write(os, "Content-Length: " + file_length + "\r\n" + "\r\n");
/*     */           
/* 174 */           request_length = file_length;
/*     */           
/* 176 */           request_ok = true;
/*     */         }
/*     */         else
/*     */         {
/* 180 */           ranges = ranges.toLowerCase();
/*     */           
/* 182 */           if (!ranges.startsWith("bytes="))
/*     */           {
/* 184 */             throw new IOException("invalid range: " + ranges);
/*     */           }
/*     */           
/* 187 */           ranges = ranges.substring(6);
/*     */           
/* 189 */           StringTokenizer tok = new StringTokenizer(ranges, ",");
/*     */           
/* 191 */           if (tok.countTokens() != 1)
/*     */           {
/* 193 */             throw new IOException("invalid range - only single supported: " + ranges);
/*     */           }
/*     */           
/* 196 */           String range = tok.nextToken();
/*     */           
/* 198 */           int pos = range.indexOf('-');
/*     */           
/*     */           long end;
/*     */           
/*     */           long end;
/* 203 */           if (pos < range.length() - 1)
/*     */           {
/* 205 */             end = Long.parseLong(range.substring(pos + 1));
/*     */           }
/*     */           else
/*     */           {
/* 209 */             end = file_length - 1L; }
/*     */           long start;
/*     */           long start;
/* 212 */           if (pos > 0)
/*     */           {
/* 214 */             start = Long.parseLong(range.substring(0, pos));
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/* 219 */             start = file_length - end;
/* 220 */             end = file_length - 1L;
/*     */           }
/*     */           
/* 223 */           request_length = end - start + 1L;
/*     */           
/*     */ 
/*     */ 
/* 227 */           if (request_length < 0L)
/*     */           {
/* 229 */             write(os, "HTTP/1.1 416 Requested Range Not Satisfiable\r\n\r\n");
/*     */           }
/*     */           else
/*     */           {
/* 233 */             request_start = start;
/*     */             
/* 235 */             write(os, "HTTP/1.1 206 Partial content\r\n");
/*     */             
/*     */ 
/* 238 */             write(os, "Server: Azureus Media Server 1.0\r\n");
/* 239 */             write(os, "Connection: close\r\n");
/* 240 */             write(os, "Content-Range: bytes " + start + "-" + end + "/" + file_length + "\r\n");
/* 241 */             write(os, "Content-Length: " + request_length + "\r\n" + "\r\n");
/*     */             
/* 243 */             request_ok = true;
/*     */           }
/*     */         }
/*     */         
/* 247 */         os.flush();
/*     */         
/* 249 */         if (request_ok)
/*     */         {
/* 251 */           handleRAF(os, request_start, request_length);
/*     */         }
/*     */       }
/*     */       
/* 255 */       synchronized (this)
/*     */       {
/* 257 */         if (this.destroyed)
/*     */         {
/*     */           try {
/* 260 */             socket.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           
/*     */           try
/*     */           {
/* 266 */             is.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           
/*     */ 
/* 271 */           this.sockets.remove(socket);
/*     */           
/* 273 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/*     */       try {
/* 279 */         socket.close();
/*     */       }
/*     */       catch (Throwable f) {}
/*     */       
/*     */ 
/* 284 */       synchronized (this)
/*     */       {
/* 286 */         this.sockets.remove(socket);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void write(OutputStream os, String str)
/*     */     throws IOException
/*     */   {
/* 298 */     os.write(str.getBytes());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected RandomAccessFile reserveRAF()
/*     */     throws IOException
/*     */   {
/* 307 */     synchronized (this)
/*     */     {
/* 309 */       if (this.destroyed)
/*     */       {
/* 311 */         throw new IOException("destroyed");
/*     */       }
/*     */       
/* 314 */       if (this.raf == null)
/*     */       {
/* 316 */         this.raf = new RandomAccessFile(this.source_file, "r");
/*     */       }
/*     */       
/* 319 */       this.raf_count += 1;
/*     */       
/* 321 */       return this.raf;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void releaseRAF(RandomAccessFile _raf)
/*     */   {
/* 330 */     synchronized (this)
/*     */     {
/* 332 */       this.raf_count -= 1;
/*     */       
/* 334 */       if (this.raf_count == 0)
/*     */       {
/*     */         try {
/* 337 */           this.raf.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */         
/*     */ 
/* 342 */         this.raf = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean destroy()
/*     */   {
/* 350 */     if (super.destroy())
/*     */     {
/* 352 */       if (this.raf != null)
/*     */       {
/*     */         try {
/* 355 */           this.raf.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */         
/*     */ 
/* 360 */         this.raf = null;
/*     */       }
/*     */       
/* 363 */       return true;
/*     */     }
/*     */     
/* 366 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/TranscodePipeFileSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */