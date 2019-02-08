/*     */ package org.gudy.azureus2.update;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarInputStream;
/*     */ import java.util.jar.JarOutputStream;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UpdateJarPatcher
/*     */ {
/*  36 */   private static String MANIFEST_NAME = "META-INF/MANIFEST.MF";
/*     */   
/*  38 */   protected Map patch_entries = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected UpdateJarPatcher(InputStream input_file, InputStream patch_file, OutputStream output_file, LoggerChannel log)
/*     */     throws IOException
/*     */   {
/*  49 */     readPatchEntries(patch_file);
/*     */     
/*  51 */     JarInputStream jis = new JarInputStream(input_file);
/*     */     
/*  53 */     JarOutputStream jos = new JarOutputStream(output_file);
/*     */     
/*  55 */     boolean manifest_found = false;
/*     */     
/*     */     for (;;)
/*     */     {
/*  59 */       JarEntry is_entry = jis.getNextJarEntry();
/*     */       
/*  61 */       if (is_entry == null) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/*  66 */       if (!is_entry.isDirectory())
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*  71 */         String name = is_entry.getName();
/*     */         
/*  73 */         if (name.equalsIgnoreCase(MANIFEST_NAME))
/*     */         {
/*  75 */           manifest_found = true;
/*     */         }
/*     */         
/*  78 */         InputStream eis = getPatch(name);
/*     */         
/*  80 */         if (eis != null)
/*     */         {
/*  82 */           log.log("patch - replace: " + name);
/*     */         }
/*     */         else
/*     */         {
/*  86 */           eis = jis;
/*     */         }
/*     */         
/*  89 */         JarEntry os_entry = new JarEntry(name);
/*     */         
/*  91 */         writeEntry(jos, os_entry, eis);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  96 */     Iterator it = this.patch_entries.keySet().iterator();
/*     */     
/*  98 */     while (it.hasNext())
/*     */     {
/* 100 */       String name = (String)it.next();
/*     */       
/* 102 */       if (name.equalsIgnoreCase(MANIFEST_NAME))
/*     */       {
/* 104 */         manifest_found = true;
/*     */       }
/*     */       
/* 107 */       log.log("patch - add: " + name);
/*     */       
/* 109 */       InputStream eis = (InputStream)this.patch_entries.get(name);
/*     */       
/* 111 */       JarEntry os_entry = new JarEntry(name);
/*     */       
/* 113 */       writeEntry(jos, os_entry, eis);
/*     */     }
/*     */     
/* 116 */     if (!manifest_found)
/*     */     {
/* 118 */       JarEntry entry = new JarEntry(MANIFEST_NAME);
/*     */       
/* 120 */       ByteArrayInputStream bais = new ByteArrayInputStream("Manifest-Version: 1.0\r\n\r\n".getBytes());
/*     */       
/* 122 */       writeEntry(jos, entry, bais);
/*     */     }
/*     */     
/* 125 */     jos.finish();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void writeEntry(JarOutputStream jos, JarEntry entry, InputStream data)
/*     */     throws IOException
/*     */   {
/* 136 */     jos.putNextEntry(entry);
/*     */     
/* 138 */     byte[] newBytes = new byte['က'];
/*     */     
/* 140 */     int size = data.read(newBytes);
/*     */     
/* 142 */     while (size != -1)
/*     */     {
/* 144 */       jos.write(newBytes, 0, size);
/*     */       
/* 146 */       size = data.read(newBytes);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void readPatchEntries(InputStream is)
/*     */     throws IOException
/*     */   {
/* 156 */     JarInputStream jis = new JarInputStream(is);
/*     */     
/*     */     for (;;)
/*     */     {
/* 160 */       JarEntry ent = jis.getNextJarEntry();
/*     */       
/* 162 */       if (ent == null) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/* 167 */       if (!ent.isDirectory())
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 172 */         ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */         
/* 174 */         byte[] buffer = new byte[' '];
/*     */         
/*     */         for (;;)
/*     */         {
/* 178 */           int l = jis.read(buffer);
/*     */           
/* 180 */           if (l <= 0) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/* 185 */           baos.write(buffer, 0, l);
/*     */         }
/*     */         
/* 188 */         this.patch_entries.put(ent.getName(), new ByteArrayInputStream(baos.toByteArray()));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InputStream getPatch(String name)
/*     */   {
/* 197 */     return (InputStream)this.patch_entries.remove(name);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/update/UpdateJarPatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */