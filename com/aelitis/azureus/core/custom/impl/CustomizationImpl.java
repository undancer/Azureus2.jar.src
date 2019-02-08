/*     */ package com.aelitis.azureus.core.custom.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.custom.Customization;
/*     */ import com.aelitis.azureus.core.custom.CustomizationException;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipInputStream;
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
/*     */ 
/*     */ public class CustomizationImpl
/*     */   implements Customization
/*     */ {
/*     */   private final CustomizationManagerImpl manager;
/*     */   private final String name;
/*     */   private final String version;
/*     */   private final File contents;
/*     */   
/*     */   protected CustomizationImpl(CustomizationManagerImpl _manager, String _name, String _version, File _contents)
/*     */     throws CustomizationException
/*     */   {
/*  58 */     this.manager = _manager;
/*  59 */     this.name = _name;
/*  60 */     this.version = _version;
/*  61 */     this.contents = _contents;
/*     */     
/*  63 */     if (!this.contents.exists())
/*     */     {
/*  65 */       throw new CustomizationException("Content file '" + this.contents + " not found");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  72 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getVersion()
/*     */   {
/*  78 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */   protected File getContents()
/*     */   {
/*  84 */     return this.contents;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getProperty(String name)
/*     */   {
/*  91 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isActive()
/*     */   {
/*  97 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setActive(boolean active) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public InputStream getResource(String resource_name)
/*     */   {
/* 111 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InputStream[] getResources(String resource_name)
/*     */   {
/* 118 */     result = new ArrayList();
/*     */     
/* 120 */     ZipInputStream zis = null;
/*     */     try
/*     */     {
/* 123 */       zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(this.contents)));
/*     */       
/*     */ 
/*     */       for (;;)
/*     */       {
/* 128 */         ZipEntry entry = zis.getNextEntry();
/*     */         
/* 130 */         if (entry == null) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/* 135 */         String name = entry.getName();
/*     */         
/* 137 */         int pos = name.indexOf(resource_name + "/");
/*     */         
/* 139 */         if (pos != -1)
/*     */         {
/* 141 */           if (name.endsWith(".vuze"))
/*     */           {
/* 143 */             ByteArrayOutputStream baos = new ByteArrayOutputStream(16384);
/*     */             
/* 145 */             byte[] buffer = new byte['䀀'];
/*     */             
/*     */             for (;;)
/*     */             {
/* 149 */               int len = zis.read(buffer);
/*     */               
/* 151 */               if (len <= 0) {
/*     */                 break;
/*     */               }
/*     */               
/*     */ 
/* 156 */               baos.write(buffer, 0, len);
/*     */             }
/*     */             
/* 159 */             result.add(new ByteArrayInputStream(baos.toByteArray()));
/*     */           }
/*     */         }
/*     */       }
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
/* 179 */       return (InputStream[])result.toArray(new InputStream[result.size()]);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 165 */       Debug.out(e);
/*     */     }
/*     */     finally
/*     */     {
/* 169 */       if (zis != null) {
/*     */         try
/*     */         {
/* 172 */           zis.close();
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
/*     */ 
/*     */   public void exportToVuzeFile(File file)
/*     */     throws CustomizationException
/*     */   {
/* 188 */     this.manager.exportCustomization(this, file);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/custom/impl/CustomizationImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */