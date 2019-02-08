/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
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
/*     */ public class AETemporaryFileHandler
/*     */ {
/*  34 */   private static final boolean PORTABLE = System.getProperty("azureus.portable.root", "").length() > 0;
/*     */   
/*     */   private static final String PREFIX = "AZU";
/*     */   
/*     */   private static final String SUFFIX = ".tmp";
/*     */   
/*     */   private static boolean started_up;
/*     */   private static File tmp_dir;
/*     */   
/*     */   public static synchronized void startup()
/*     */   {
/*  45 */     if (started_up)
/*     */     {
/*  47 */       return;
/*     */     }
/*     */     
/*  50 */     started_up = true;
/*     */     try
/*     */     {
/*  53 */       tmp_dir = FileUtil.getUserFile("tmp");
/*     */       
/*  55 */       if (tmp_dir.exists())
/*     */       {
/*  57 */         File[] files = tmp_dir.listFiles();
/*     */         
/*  59 */         if (files != null)
/*     */         {
/*  61 */           for (int i = 0; i < files.length; i++)
/*     */           {
/*  63 */             File file = files[i];
/*     */             
/*  65 */             if ((file.getName().startsWith("AZU")) && (file.getName().endsWith(".tmp")))
/*     */             {
/*  67 */               if (file.isDirectory())
/*     */               {
/*  69 */                 FileUtil.recursiveDelete(file);
/*     */               }
/*     */               else
/*     */               {
/*  73 */                 file.delete();
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/*  80 */         tmp_dir.mkdir();
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */       try {
/*  86 */         tmp_dir = File.createTempFile("AZU", ".tmp").getParentFile();
/*     */       }
/*     */       catch (Throwable f)
/*     */       {
/*  90 */         tmp_dir = new File("");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*  95 */       if (!(e instanceof NoClassDefFoundError))
/*     */       {
/*  97 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static File getTempDirectory()
/*     */   {
/* 105 */     startup();
/*     */     
/* 107 */     return tmp_dir;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isTempFile(File file)
/*     */   {
/* 114 */     if (!file.exists())
/*     */     {
/* 116 */       return false;
/*     */     }
/*     */     try
/*     */     {
/* 120 */       String s1 = file.getParentFile().getCanonicalPath();
/* 121 */       String s2 = tmp_dir.getCanonicalPath();
/*     */       
/* 123 */       if (!s1.equals(s2))
/*     */       {
/* 125 */         return false;
/*     */       }
/*     */       
/* 128 */       String name = file.getName();
/*     */       
/* 130 */       if (!name.startsWith("AZU"))
/*     */       {
/* 132 */         return false;
/*     */       }
/*     */       
/* 135 */       if (!name.endsWith(".tmp"))
/*     */       {
/* 137 */         return false;
/*     */       }
/*     */       
/* 140 */       return true;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 144 */       Debug.out(e);
/*     */     }
/* 146 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static File createTempFile()
/*     */     throws IOException
/*     */   {
/* 155 */     startup();
/*     */     
/* 157 */     return File.createTempFile("AZU", ".tmp", tmp_dir);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static File createTempFileInDir(File parent_dir)
/*     */     throws IOException
/*     */   {
/* 166 */     startup();
/*     */     
/* 168 */     return File.createTempFile("AZU", ".tmp", parent_dir);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static File createSemiTempFile()
/*     */     throws IOException
/*     */   {
/* 176 */     if (PORTABLE) {
/*     */       try
/*     */       {
/* 179 */         File stmp_dir = FileUtil.getUserFile("tmp2");
/*     */         
/* 181 */         if (!stmp_dir.exists())
/*     */         {
/* 183 */           stmp_dir.mkdirs();
/*     */         }
/*     */         
/* 186 */         if (stmp_dir.canWrite())
/*     */         {
/* 188 */           return File.createTempFile("AZU", null, stmp_dir);
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 192 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/* 196 */     return File.createTempFile("AZU", null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static File createTempDir()
/*     */     throws IOException
/*     */   {
/*     */     
/*     */     
/* 206 */     for (int i = 0; i < 16; i++)
/*     */     {
/* 208 */       File f = File.createTempFile("AZU", ".tmp", tmp_dir);
/*     */       
/* 210 */       f.delete();
/*     */       
/* 212 */       if (f.mkdirs())
/*     */       {
/* 214 */         return f;
/*     */       }
/*     */     }
/*     */     
/* 218 */     throw new IOException("Failed to create temporary directory in " + tmp_dir);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AETemporaryFileHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */