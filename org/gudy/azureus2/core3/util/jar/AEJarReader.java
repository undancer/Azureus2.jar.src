/*     */ package org.gudy.azureus2.core3.util.jar;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarInputStream;
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
/*     */ public class AEJarReader
/*     */ {
/*  37 */   protected final Map entries = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */   public AEJarReader(String name)
/*     */   {
/*  43 */     InputStream is = null;
/*  44 */     JarInputStream jis = null;
/*     */     try
/*     */     {
/*  47 */       is = getClass().getClassLoader().getResourceAsStream(name);
/*     */       
/*  49 */       jis = new JarInputStream(is);
/*     */       
/*     */       for (;;)
/*     */       {
/*  53 */         JarEntry ent = jis.getNextJarEntry();
/*     */         
/*  55 */         if (ent == null) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/*  60 */         if (!ent.isDirectory())
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*  65 */           ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */           
/*  67 */           byte[] buffer = new byte[' '];
/*     */           
/*     */           for (;;)
/*     */           {
/*  71 */             int l = jis.read(buffer);
/*     */             
/*  73 */             if (l <= 0) {
/*     */               break;
/*     */             }
/*     */             
/*     */ 
/*  78 */             baos.write(buffer, 0, l);
/*     */           }
/*     */           
/*  81 */           this.entries.put(ent.getName(), new ByteArrayInputStream(baos.toByteArray()));
/*     */         }
/*     */       }
/*     */       return;
/*     */     } catch (Throwable e) {
/*  86 */       e.printStackTrace();
/*     */     }
/*     */     finally
/*     */     {
/*     */       try {
/*  91 */         if (jis != null)
/*     */         {
/*  93 */           jis.close();
/*     */         }
/*     */         
/*  96 */         if (is != null)
/*     */         {
/*  98 */           is.close();
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public InputStream getResource(String name)
/*     */   {
/* 110 */     return (InputStream)this.entries.get(name);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/jar/AEJarReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */