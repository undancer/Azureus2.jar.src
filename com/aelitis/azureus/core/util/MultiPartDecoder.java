/*     */ package com.aelitis.azureus.core.util;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.LineNumberReader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MultiPartDecoder
/*     */ {
/*     */   public FormField[] decode(String boundary, InputStream is)
/*     */     throws IOException
/*     */   {
/*  54 */     byte[] header_end_bytes = "\r\n\r\n".getBytes("ISO-8859-1");
/*     */     
/*  56 */     byte[] boundary_bytes = ("\r\n--" + boundary).getBytes("ISO-8859-1");
/*     */     
/*  58 */     int boundary_len = boundary_bytes.length;
/*     */     
/*  60 */     byte[] buffer = new byte[65536];
/*  61 */     int buffer_pos = 0;
/*     */     
/*  63 */     boolean in_header = true;
/*     */     
/*  65 */     byte[] current_target = header_end_bytes;
/*  66 */     int current_target_length = 4;
/*     */     
/*  68 */     FormField current_field = null;
/*     */     
/*  70 */     List fields = new ArrayList();
/*     */     
/*     */     for (;;)
/*     */     {
/*  74 */       int buffer_pos_start = buffer_pos;
/*     */       
/*  76 */       int len = is.read(buffer, buffer_pos, buffer.length - buffer_pos);
/*     */       
/*  78 */       if (len < 0)
/*     */       {
/*  80 */         len = 0;
/*     */       }
/*     */       
/*  83 */       buffer_pos += len;
/*     */       
/*  85 */       boolean found_target = false;
/*     */       
/*  87 */       for (int i = 0; i <= buffer_pos - current_target_length; i++)
/*     */       {
/*  89 */         if (buffer[i] == current_target[0])
/*     */         {
/*  91 */           found_target = true;
/*     */           
/*  93 */           for (int j = 1; j < current_target_length; j++)
/*     */           {
/*  95 */             if (buffer[(i + j)] != current_target[j])
/*     */             {
/*  97 */               found_target = false;
/*     */               
/*  99 */               break;
/*     */             }
/*     */           }
/*     */           
/* 103 */           if (found_target)
/*     */           {
/* 105 */             if (in_header)
/*     */             {
/* 107 */               if (current_field != null)
/*     */               {
/* 109 */                 current_field.complete();
/*     */               }
/*     */               
/* 112 */               String header = new String(buffer, 0, i + 4);
/*     */               
/* 114 */               int cdl_pos = header.toLowerCase().indexOf("content-disposition");
/*     */               
/* 116 */               if (cdl_pos == -1)
/*     */               {
/* 118 */                 throw new IOException("invalid header '" + header + "'");
/*     */               }
/*     */               
/* 121 */               int cd_nl = header.indexOf("\r\n", cdl_pos);
/*     */               
/* 123 */               String cd_line = header.substring(cdl_pos, cd_nl);
/*     */               
/* 125 */               int cd_pos = 0;
/*     */               
/* 127 */               Map attributes = new HashMap();
/*     */               
/*     */               for (;;)
/*     */               {
/* 131 */                 int p1 = cd_line.indexOf(";", cd_pos);
/*     */                 
/*     */                 String bit;
/*     */                 String bit;
/* 135 */                 if (p1 == -1) {
/* 136 */                   bit = cd_line.substring(cd_pos);
/*     */                 } else {
/* 138 */                   bit = cd_line.substring(cd_pos, p1);
/* 139 */                   cd_pos = p1 + 1;
/*     */                 }
/*     */                 
/* 142 */                 int ep = bit.indexOf("=");
/*     */                 
/* 144 */                 if (ep != -1)
/*     */                 {
/* 146 */                   String lhs = bit.substring(0, ep).trim();
/* 147 */                   String rhs = bit.substring(ep + 1).trim();
/*     */                   
/* 149 */                   if (rhs.startsWith("\""))
/*     */                   {
/* 151 */                     rhs = rhs.substring(1);
/*     */                   }
/*     */                   
/* 154 */                   if (rhs.endsWith("\"")) {
/* 155 */                     rhs = rhs.substring(0, rhs.length() - 1);
/*     */                   }
/*     */                   
/* 158 */                   attributes.put(lhs.toLowerCase(), rhs);
/*     */                 }
/*     */                 
/* 161 */                 if (p1 == -1) {
/*     */                   break;
/*     */                 }
/*     */               }
/*     */               
/* 166 */               String field_name = (String)attributes.get("name");
/*     */               
/* 168 */               if (field_name == null)
/*     */               {
/* 170 */                 throw new IOException(cd_line + " missing 'name' attribute");
/*     */               }
/* 172 */               current_field = new FormField(field_name, attributes);
/*     */               
/* 174 */               fields.add(current_field);
/*     */             }
/*     */             else
/*     */             {
/* 178 */               current_field.write(buffer, 0, i);
/*     */             }
/*     */             
/* 181 */             int rem = buffer_pos - (i + current_target_length);
/*     */             
/* 183 */             if (rem > 0)
/*     */             {
/* 185 */               System.arraycopy(buffer, i + current_target_length, buffer, 0, rem);
/*     */             }
/*     */             
/* 188 */             buffer_pos = rem;
/*     */             
/* 190 */             if (in_header)
/*     */             {
/* 192 */               in_header = false;
/*     */               
/* 194 */               current_target = boundary_bytes;
/* 195 */               current_target_length = boundary_len; break;
/*     */             }
/*     */             
/*     */ 
/* 199 */             in_header = true;
/*     */             
/* 201 */             current_target = header_end_bytes;
/* 202 */             current_target_length = 4;
/*     */             
/*     */ 
/* 205 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 214 */       if ((!found_target) && (!in_header))
/*     */       {
/* 216 */         int rem = buffer_pos - current_target_length;
/*     */         
/* 218 */         if (rem > 0)
/*     */         {
/*     */ 
/*     */ 
/* 222 */           current_field.write(buffer, 0, rem);
/*     */           
/* 224 */           System.arraycopy(buffer, rem, buffer, 0, current_target_length);
/*     */           
/* 226 */           buffer_pos = current_target_length;
/*     */         }
/*     */       }
/*     */       
/* 230 */       if ((len == 0) && (buffer_pos == buffer_pos_start)) {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 240 */     if ((buffer_pos < 2) || (buffer[0] != 45) || (buffer[1] != 45))
/*     */     {
/* 242 */       throw new IOException("Incorrect termination of form upload data");
/*     */     }
/*     */     
/* 245 */     if (current_field != null)
/*     */     {
/* 247 */       current_field.complete();
/*     */     }
/*     */     
/* 250 */     FormField[] res = new FormField[fields.size()];
/*     */     
/* 252 */     fields.toArray(res);
/*     */     
/* 254 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public static class FormField
/*     */   {
/*     */     protected final String name;
/*     */     
/*     */     protected final Map attributes;
/*     */     
/*     */     protected long total_len;
/* 265 */     final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
/*     */     
/*     */ 
/*     */     File file;
/*     */     
/*     */     FileOutputStream fos;
/*     */     
/*     */     InputStream returned_stream;
/*     */     
/*     */ 
/*     */     protected FormField(String _name, Map _attributes)
/*     */     {
/* 277 */       this.name = _name;
/* 278 */       this.attributes = _attributes;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getName()
/*     */     {
/* 286 */       return this.name;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public String getAttribute(String attr_name)
/*     */     {
/* 293 */       return (String)this.attributes.get(attr_name.toLowerCase());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public InputStream getInputStream()
/*     */       throws IOException
/*     */     {
/* 301 */       if (this.file == null)
/*     */       {
/* 303 */         this.returned_stream = new ByteArrayInputStream(this.baos.toByteArray());
/*     */       }
/*     */       else
/*     */       {
/* 307 */         this.returned_stream = new FileInputStream(this.file);
/*     */       }
/*     */       
/* 310 */       return this.returned_stream;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public String getString()
/*     */       throws IOException
/*     */     {
/* 318 */       String str = new LineNumberReader(new InputStreamReader(getInputStream())).readLine();
/*     */       
/* 320 */       if (str == null)
/*     */       {
/* 322 */         str = "";
/*     */       }
/*     */       
/* 325 */       return str;
/*     */     }
/*     */     
/*     */ 
/*     */     public void destroy()
/*     */     {
/* 331 */       if (this.returned_stream != null) {
/*     */         try
/*     */         {
/* 334 */           this.returned_stream.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 341 */       if (this.file != null)
/*     */       {
/* 343 */         this.file.delete();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void write(byte[] buffer, int start, int len)
/*     */       throws IOException
/*     */     {
/* 355 */       this.total_len += len;
/*     */       
/* 357 */       if (this.fos != null)
/*     */       {
/* 359 */         this.fos.write(buffer, start, len);
/*     */ 
/*     */ 
/*     */       }
/* 363 */       else if (this.total_len > 1024L)
/*     */       {
/* 365 */         this.file = File.createTempFile("AZU", null);
/*     */         
/* 367 */         this.file.deleteOnExit();
/*     */         
/* 369 */         this.fos = new FileOutputStream(this.file);
/*     */         
/* 371 */         this.fos.write(this.baos.toByteArray());
/*     */         
/* 373 */         this.fos.write(buffer, start, len);
/*     */       }
/*     */       else
/*     */       {
/* 377 */         this.baos.write(buffer, start, len);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void complete()
/*     */       throws IOException
/*     */     {
/* 389 */       if (this.fos != null)
/*     */       {
/* 391 */         this.fos.close();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/MultiPartDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */