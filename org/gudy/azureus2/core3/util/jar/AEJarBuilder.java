/*     */ package org.gudy.azureus2.core3.util.jar;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarOutputStream;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.security.SEKeyDetails;
/*     */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AEJarBuilder
/*     */ {
/*     */   public static long buildFromPackages(OutputStream os, ClassLoader class_loader, String[] package_names, Map package_map, String sign_alias)
/*     */     throws IOException
/*     */   {
/*  54 */     List resource_names = new ArrayList();
/*     */     
/*  56 */     for (int i = 0; i < package_names.length; i++)
/*     */     {
/*  58 */       List entries = (List)package_map.get(package_names[i]);
/*     */       
/*  60 */       if (entries == null)
/*     */       {
/*  62 */         Debug.out("package '" + package_names[i] + "' missing");
/*     */       }
/*     */       else
/*     */       {
/*  66 */         for (int j = 0; j < entries.size(); j++)
/*     */         {
/*  68 */           resource_names.add(package_names[i] + "/" + entries.get(j));
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*  73 */     String[] res = new String[resource_names.size()];
/*     */     
/*  75 */     resource_names.toArray(res);
/*     */     
/*  77 */     return buildFromResources2(os, class_loader, null, res, sign_alias);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void buildFromResources(OutputStream os, ClassLoader class_loader, String resource_prefix, String[] resource_names, String sign_alias)
/*     */     throws IOException
/*     */   {
/*  90 */     buildFromResources2(os, class_loader, resource_prefix, resource_names, sign_alias);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static long buildFromResources2(OutputStream os, ClassLoader class_loader, String resource_prefix, String[] resource_names, String sign_alias)
/*     */     throws IOException
/*     */   {
/* 103 */     if (sign_alias != null)
/*     */     {
/* 105 */       ByteArrayOutputStream baos = new ByteArrayOutputStream(65536);
/*     */       
/* 107 */       long tim = buildFromResourcesSupport(new JarOutputStream(baos), class_loader, resource_prefix, resource_names);
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 112 */         SEKeyDetails kd = SESecurityManager.getKeyDetails(sign_alias);
/*     */         
/* 114 */         if (kd == null) {
/* 115 */           Logger.log(new LogAlert(false, 3, "Certificate alias '" + sign_alias + "' not found, jar signing fails"));
/*     */           
/*     */ 
/*     */ 
/* 119 */           throw new Exception("Certificate alias '" + sign_alias + "' not found ");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 124 */         AEJarSigner2 signer = new AEJarSigner2(sign_alias, SESecurityManager.getKeystoreName(), SESecurityManager.getKeystorePassword());
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 130 */         signer.signJarStream(new ByteArrayInputStream(baos.toByteArray()), os);
/*     */         
/* 132 */         return tim;
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 136 */         Debug.printStackTrace(e);
/*     */         
/* 138 */         throw new IOException(e.getMessage());
/*     */       }
/*     */     }
/*     */     
/*     */     JarOutputStream jos;
/*     */     
/*     */     JarOutputStream jos;
/* 145 */     if ((os instanceof JarOutputStream))
/*     */     {
/* 147 */       jos = (JarOutputStream)os;
/*     */     }
/*     */     else
/*     */     {
/* 151 */       jos = new JarOutputStream(os);
/*     */     }
/*     */     
/* 154 */     return buildFromResourcesSupport(jos, class_loader, resource_prefix, resource_names);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long buildFromResourcesSupport(JarOutputStream jos, ClassLoader class_loader, String resource_prefix, String[] resource_names)
/*     */     throws IOException
/*     */   {
/* 167 */     long latest_time = 0L;
/* 168 */     long now = SystemTime.getCurrentTime();
/*     */     
/* 170 */     for (int i = 0; i < resource_names.length; i++)
/*     */     {
/* 172 */       String resource_name = resource_names[i];
/*     */       
/* 174 */       if (resource_prefix != null)
/*     */       {
/* 176 */         resource_name = resource_prefix + "/" + resource_name;
/*     */       }
/*     */       
/* 179 */       InputStream is = null;
/*     */       try
/*     */       {
/* 182 */         is = class_loader.getResourceAsStream(resource_name);
/*     */         
/* 184 */         if (is == null)
/*     */         {
/* 186 */           Debug.out("WUJarBuilder: failed to find resource '" + resource_name + "'");
/*     */         }
/*     */         else
/*     */         {
/* 190 */           URL url = class_loader.getResource(resource_name);
/*     */           try
/*     */           {
/* 193 */             File file = null;
/*     */             
/* 195 */             if (url != null)
/*     */             {
/* 197 */               String url_str = url.toString();
/*     */               
/* 199 */               if (url_str.startsWith("jar:file:"))
/*     */               {
/* 201 */                 file = FileUtil.getJarFileFromURL(url_str);
/*     */               }
/* 203 */               else if (url_str.startsWith("file:"))
/*     */               {
/* 205 */                 file = new File(URI.create(url_str));
/*     */               }
/*     */             }
/*     */             
/* 209 */             if (file == null)
/*     */             {
/* 211 */               latest_time = now;
/*     */             }
/*     */             else
/*     */             {
/* 215 */               long time = file.lastModified();
/*     */               
/* 217 */               if (time > latest_time)
/*     */               {
/* 219 */                 latest_time = time;
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 224 */             Debug.printStackTrace(e);
/*     */           }
/*     */           
/* 227 */           JarEntry entry = new JarEntry(resource_name);
/*     */           
/* 229 */           writeEntry(jos, entry, is);
/*     */         }
/*     */       } finally {
/* 232 */         if (is != null)
/*     */         {
/* 234 */           is.close();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 239 */     JarEntry entry = new JarEntry("META-INF/MANIFEST.MF");
/*     */     
/* 241 */     String manifest_lines = "Manifest-Version: 1.0\r\nPermissions: all-permissions\r\n\r\n";
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 246 */     ByteArrayInputStream bais = new ByteArrayInputStream(manifest_lines.getBytes("ISO-8859-1"));
/*     */     
/* 248 */     writeEntry(jos, entry, bais);
/*     */     
/* 250 */     jos.flush();
/*     */     
/* 252 */     jos.finish();
/*     */     
/* 254 */     return latest_time;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void writeEntry(JarOutputStream jos, JarEntry entry, InputStream data)
/*     */     throws IOException
/*     */   {
/* 267 */     jos.putNextEntry(entry);
/*     */     
/* 269 */     byte[] newBytes = new byte['က'];
/*     */     
/* 271 */     int size = data.read(newBytes);
/*     */     
/* 273 */     while (size != -1)
/*     */     {
/* 275 */       jos.write(newBytes, 0, size);
/*     */       
/* 277 */       size = data.read(newBytes);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/jar/AEJarBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */