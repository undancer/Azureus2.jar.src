/*     */ package com.aelitis.azureus.core.util;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.Socket;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HTTPUtils
/*     */ {
/*     */   public static final String NL = "\r\n";
/*     */   private static final String default_type = "application/octet-stream";
/*  40 */   private static final Map file_types = new HashMap();
/*  41 */   private static final Set compression = new HashSet();
/*     */   
/*     */   static {
/*  44 */     file_types.put("html", "text/html");
/*  45 */     file_types.put("htm", "text/html");
/*  46 */     file_types.put("css", "text/css");
/*  47 */     file_types.put("js", "text/javascript");
/*  48 */     file_types.put("xml", "text/xml");
/*  49 */     file_types.put("xsl", "text/xml");
/*  50 */     file_types.put("jpg", "image/jpeg");
/*  51 */     file_types.put("jpeg", "image/jpeg");
/*  52 */     file_types.put("gif", "image/gif");
/*  53 */     file_types.put("tiff", "image/tiff");
/*  54 */     file_types.put("bmp", "image/bmp");
/*  55 */     file_types.put("png", "image/png");
/*  56 */     file_types.put("torrent", "application/x-bittorrent");
/*  57 */     file_types.put("tor", "application/x-bittorrent");
/*  58 */     file_types.put("vuze", "application/x-vuze");
/*  59 */     file_types.put("vuz", "application/x-vuze");
/*  60 */     file_types.put("zip", "application/zip");
/*  61 */     file_types.put("txt", "text/plain");
/*  62 */     file_types.put("jar", "application/java-archive");
/*  63 */     file_types.put("jnlp", "application/x-java-jnlp-file");
/*  64 */     file_types.put("mp3", "audio/x-mpeg");
/*     */     
/*  66 */     file_types.put("flv", "video/x-flv");
/*  67 */     file_types.put("swf", "application/x-shockwave-flash");
/*  68 */     file_types.put("mkv", "video/x-matroska");
/*  69 */     file_types.put("mp4", "video/mp4");
/*  70 */     file_types.put("mov", "video/quicktime");
/*  71 */     file_types.put("avi", "video/avi");
/*     */     
/*  73 */     file_types.put("xap", "application/x-silverlight-app");
/*     */     
/*  75 */     compression.add("text/html");
/*  76 */     compression.add("text/css");
/*  77 */     compression.add("text/xml");
/*  78 */     compression.add("text/plain");
/*  79 */     compression.add("text/javascript");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String guessContentTypeFromFileType(String file_type)
/*     */   {
/*  87 */     if (file_type != null)
/*     */     {
/*  89 */       String type = (String)file_types.get(file_type.toLowerCase(Constants.LOCALE_ENGLISH));
/*     */       
/*  91 */       if (type != null)
/*     */       {
/*  93 */         return type;
/*     */       }
/*     */     }
/*     */     
/*  97 */     return "application/octet-stream";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean canGZIP(String accept_encoding)
/*     */   {
/* 104 */     boolean gzip_reply = false;
/*     */     
/* 106 */     if (accept_encoding != null)
/*     */     {
/* 108 */       accept_encoding = accept_encoding.toLowerCase(Constants.LOCALE_ENGLISH);
/*     */       
/* 110 */       int gzip_index = accept_encoding.indexOf("gzip");
/*     */       
/* 112 */       if (gzip_index != -1)
/*     */       {
/* 114 */         gzip_reply = true;
/*     */         
/* 116 */         if (accept_encoding.length() - gzip_index >= 8)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 121 */           char[] chars = accept_encoding.toCharArray();
/*     */           
/* 123 */           boolean q_value = false;
/*     */           
/* 125 */           for (int i = gzip_index + 4; i < chars.length; i++)
/*     */           {
/* 127 */             char c = chars[i];
/*     */             
/* 129 */             if (c == ',') {
/*     */               break;
/*     */             }
/*     */             
/* 133 */             if (c == '=')
/*     */             {
/* 135 */               q_value = true;
/* 136 */               gzip_reply = false;
/*     */ 
/*     */ 
/*     */             }
/* 140 */             else if (q_value)
/*     */             {
/* 142 */               if ((c != ' ') && (c != '0') && (c != '.'))
/*     */               {
/* 144 */                 gzip_reply = true;
/*     */                 
/* 146 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 155 */     return gzip_reply;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean useCompressionForFileType(String file_type)
/*     */   {
/* 163 */     return compression.contains(file_type);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static InputStream decodeChunkedEncoding(Socket socket)
/*     */     throws IOException
/*     */   {
/* 172 */     return decodeChunkedEncoding(socket, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static InputStream decodeChunkedEncoding(Socket socket, boolean ignoreStatusCode)
/*     */     throws IOException
/*     */   {
/* 183 */     InputStream is = socket.getInputStream();
/*     */     
/* 185 */     String reply_header = "";
/*     */     
/*     */     for (;;)
/*     */     {
/* 189 */       byte[] buffer = new byte[1];
/*     */       
/* 191 */       if (is.read(buffer) <= 0)
/*     */       {
/* 193 */         throw new IOException("Premature end of input stream");
/*     */       }
/*     */       
/* 196 */       reply_header = reply_header + (char)buffer[0];
/*     */       
/* 198 */       if (reply_header.endsWith("\r\n\r\n")) {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 204 */     int p1 = reply_header.indexOf("\r\n");
/*     */     
/* 206 */     String first_line = reply_header.substring(0, p1).trim();
/*     */     
/* 208 */     if ((!ignoreStatusCode) && (!first_line.contains("200")))
/*     */     {
/* 210 */       String info = null;
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 215 */         int timeout = socket.getSoTimeout();
/*     */         
/* 217 */         socket.setSoTimeout(500);
/*     */         
/* 219 */         info = FileUtil.readInputStreamAsStringWithTruncation(is, 512);
/*     */         
/* 221 */         socket.setSoTimeout(timeout);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */ 
/* 226 */       String error = "HTTP request failed: " + first_line;
/*     */       
/* 228 */       if (info != null)
/*     */       {
/* 230 */         error = error + " - " + info;
/*     */       }
/*     */       
/* 233 */       throw new IOException(error);
/*     */     }
/*     */     
/* 236 */     String lc_reply_header = reply_header.toLowerCase(Constants.LOCALE_ENGLISH);
/*     */     
/* 238 */     int te_pos = lc_reply_header.indexOf("transfer-encoding");
/*     */     
/* 240 */     if (te_pos != -1)
/*     */     {
/* 242 */       String property = lc_reply_header.substring(te_pos);
/*     */       
/* 244 */       property = property.substring(property.indexOf(':') + 1, property.indexOf("\r\n")).trim();
/*     */       
/*     */ 
/* 247 */       if (property.equals("chunked"))
/*     */       {
/* 249 */         ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */         
/* 251 */         String chunk = "";
/*     */         
/* 253 */         int total_length = 0;
/*     */         
/*     */         for (;;)
/*     */         {
/* 257 */           int x = is.read();
/*     */           
/* 259 */           if (x == -1) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/* 264 */           chunk = chunk + (char)x;
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 270 */           if ((chunk.endsWith("\r\n")) && (chunk.length() > 2))
/*     */           {
/* 272 */             int semi_pos = chunk.indexOf(';');
/*     */             
/* 274 */             if (semi_pos != -1)
/*     */             {
/* 276 */               chunk = chunk.substring(0, semi_pos);
/*     */             }
/*     */             
/* 279 */             chunk = chunk.trim();
/*     */             
/* 281 */             int chunk_length = Integer.parseInt(chunk, 16);
/*     */             
/* 283 */             if (chunk_length <= 0) {
/*     */               break;
/*     */             }
/*     */             
/*     */ 
/* 288 */             total_length += chunk_length;
/*     */             
/* 290 */             if (total_length > 1048576)
/*     */             {
/* 292 */               throw new IOException("Chunk size " + chunk_length + " too large");
/*     */             }
/*     */             
/*     */ 
/* 296 */             byte[] buffer = new byte[chunk_length];
/*     */             
/* 298 */             int buffer_pos = 0;
/* 299 */             int rem = chunk_length;
/*     */             
/* 301 */             while (rem > 0)
/*     */             {
/* 303 */               int len = is.read(buffer, buffer_pos, rem);
/*     */               
/* 305 */               if (len <= 0)
/*     */               {
/* 307 */                 throw new IOException("Premature end of stream");
/*     */               }
/*     */               
/*     */ 
/* 311 */               buffer_pos += len;
/* 312 */               rem -= len;
/*     */             }
/*     */             
/* 315 */             baos.write(buffer);
/*     */             
/* 317 */             chunk = "";
/*     */           }
/*     */         }
/*     */         
/* 321 */         return new ByteArrayInputStream(baos.toByteArray());
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 326 */       int cl_pos = lc_reply_header.indexOf("content-length");
/* 327 */       if (cl_pos == -1) {
/* 328 */         return is;
/*     */       }
/* 330 */       String property = lc_reply_header.substring(cl_pos);
/*     */       
/* 332 */       property = property.substring(property.indexOf(':') + 1, property.indexOf("\r\n")).trim();
/*     */       try
/*     */       {
/* 335 */         long length = Long.parseLong(property);
/*     */         
/*     */ 
/* 338 */         if (length > 65535L) {
/* 339 */           return is;
/*     */         }
/*     */         
/* 342 */         int remaining = (int)length;
/* 343 */         int pos = 0;
/* 344 */         byte[] buffer = new byte[remaining];
/* 345 */         while (remaining > 0) {
/* 346 */           int read = is.read(buffer, pos, remaining);
/* 347 */           if (read < 0) {
/*     */             break;
/*     */           }
/* 350 */           remaining -= read;
/* 351 */           pos += read;
/*     */         }
/* 353 */         return new ByteArrayInputStream(buffer);
/*     */       }
/*     */       catch (NumberFormatException ignoreError) {}
/*     */     }
/*     */     
/* 358 */     return is;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/HTTPUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */