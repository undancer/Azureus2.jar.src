/*     */ package org.pf.file;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class FileFinder
/*     */   implements FileHandler
/*     */ {
/*  37 */   private List collectedFiles = null;
/*  38 */   protected List getCollectedFiles() { return this.collectedFiles; }
/*  39 */   protected void setCollectedFiles(List newValue) { this.collectedFiles = newValue; }
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
/*     */   protected FileFinder()
/*     */   {
/*  54 */     setCollectedFiles(new ArrayList());
/*     */   }
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
/*     */   public static File findFileOnClasspath(String filename)
/*     */   {
/*  68 */     ClassLoader cl = null;
/*  69 */     File file = null;
/*  70 */     URL url = null;
/*     */     
/*     */     try
/*     */     {
/*  74 */       cl = FileFinder.class.getClassLoader();
/*  75 */       if (cl == null)
/*     */       {
/*     */ 
/*  78 */         return null;
/*     */       }
/*  80 */       url = cl.getResource(filename);
/*  81 */       if (url != null)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  87 */         file = new File(url.getFile());
/*     */         
/*  89 */         if (!fileExists(file)) {
/*  90 */           file = null;
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception ex) {}
/*     */     
/*     */ 
/*  97 */     return file;
/*     */   }
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
/*     */   public static File findFile(String filename)
/*     */   {
/* 111 */     File aFile = null;
/*     */     
/* 113 */     aFile = new File(filename);
/* 114 */     if (fileExists(aFile)) {
/* 115 */       return aFile;
/*     */     }
/* 117 */     aFile = findFileOnClasspath(filename);
/*     */     
/* 119 */     return aFile;
/*     */   }
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
/*     */   public static File[] findFiles(String dir, String pattern)
/*     */   {
/* 137 */     return findFiles(dir, pattern, true);
/*     */   }
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
/*     */   public static File[] findFiles(String dir, String pattern, boolean recursive)
/*     */   {
/* 156 */     return findFiles(dir, pattern, recursive, '\000');
/*     */   }
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
/*     */   public static File[] findFiles(String dir, String pattern, boolean recursive, char digitWildcard)
/*     */   {
/* 180 */     Character digitChar = null;
/*     */     
/* 182 */     if (dir == null) {
/* 183 */       throw new IllegalArgumentException("FileFinder.findFiles(): dir is null");
/*     */     }
/* 185 */     if (pattern == null) {
/* 186 */       throw new IllegalArgumentException("FileFinder.findFiles(): pattern is null");
/*     */     }
/* 188 */     if (digitWildcard > 0) {
/* 189 */       digitChar = new Character(digitWildcard);
/*     */     }
/* 191 */     FileFinder finder = new FileFinder();
/* 192 */     return finder.collectFiles(dir, pattern, recursive, digitChar);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean fileExists(File file)
/*     */   {
/* 203 */     boolean success = false;
/* 204 */     if (file != null)
/*     */     {
/*     */       try
/*     */       {
/* 208 */         FileLocator locator = FileLocator.create(file);
/* 209 */         success = locator.exists();
/*     */       }
/*     */       catch (Exception ex) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 216 */     return success;
/*     */   }
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
/*     */   public boolean handleFile(File file)
/*     */   {
/* 234 */     getCollectedFiles().add(file);
/* 235 */     return true;
/*     */   }
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
/*     */   public boolean handleException(Exception ex, File file)
/*     */   {
/* 254 */     return false;
/*     */   }
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
/*     */   public boolean directoryEnd(File dir)
/*     */   {
/* 269 */     return true;
/*     */   }
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
/*     */   public boolean directoryStart(File dir, int count)
/*     */   {
/* 285 */     return true;
/*     */   }
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
/*     */   protected File[] collectFiles(String dir, String pattern, boolean recursive, Character digitWildcard)
/*     */   {
/* 299 */     FileWalker fileWalker = new FileWalker(this);
/* 300 */     if (digitWildcard != null) {
/* 301 */       fileWalker.setDigitWildcardChar(digitWildcard.charValue());
/*     */     }
/* 303 */     fileWalker.walkThrough(dir, pattern, recursive);
/* 304 */     List list = getCollectedFiles();
/* 305 */     return (File[])list.toArray(new File[list.size()]);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/pf/file/FileFinder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */