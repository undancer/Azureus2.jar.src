/*     */ package org.pf.file;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FilenameFilter;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import org.pf.text.StringPattern;
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
/*     */ public class ExtendedFileFilter
/*     */   implements FilenameFilter
/*     */ {
/*     */   protected static final int DIR_CHECK_NAME = 1;
/*     */   protected static final int DIR_INCLUDE = 2;
/*     */   protected static final int DIR_EXCLUDE = 3;
/*  46 */   private List stringPatterns = new Vector();
/*  47 */   protected List getStringPatterns() { return this.stringPatterns; }
/*  48 */   protected void setStringPatterns(List newValue) { this.stringPatterns = newValue; }
/*     */   
/*  50 */   private int dirHandling = 1;
/*  51 */   protected int getDirHandling() { return this.dirHandling; }
/*  52 */   protected void setDirHandling(int newValue) { this.dirHandling = newValue; }
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
/*     */   public void addPattern(String pattern)
/*     */   {
/*  81 */     StringPattern stringPattern = null;
/*     */     
/*  83 */     stringPattern = new StringPattern(pattern, false);
/*  84 */     getStringPatterns().add(stringPattern);
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
/*     */   public void addPattern(String pattern, char digitWildcard)
/*     */   {
/* 102 */     StringPattern stringPattern = null;
/*     */     
/* 104 */     stringPattern = new StringPattern(pattern, false, digitWildcard);
/* 105 */     getStringPatterns().add(stringPattern);
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
/*     */   public void addPattern(String pattern, boolean ignoreCase)
/*     */   {
/* 118 */     StringPattern stringPattern = null;
/*     */     
/* 120 */     stringPattern = new StringPattern(pattern, ignoreCase);
/* 121 */     getStringPatterns().add(stringPattern);
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
/*     */   public void addPattern(String pattern, boolean ignoreCase, char digitWildcard)
/*     */   {
/* 137 */     StringPattern stringPattern = null;
/*     */     
/* 139 */     stringPattern = new StringPattern(pattern, ignoreCase, digitWildcard);
/* 140 */     getStringPatterns().add(stringPattern);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkNameOfDirectories()
/*     */   {
/* 150 */     setDirHandling(1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void alwaysIncludeDirectories()
/*     */   {
/* 161 */     setDirHandling(2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void alwaysExcludeDirectories()
/*     */   {
/* 171 */     setDirHandling(3);
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
/*     */   public boolean accept(File dir, String name)
/*     */   {
/* 185 */     File fileOrDir = null;
/*     */     
/* 187 */     fileOrDir = new File(dir, name);
/* 188 */     if (fileOrDir.isDirectory())
/*     */     {
/* 190 */       if (mustIncludeDirectories())
/* 191 */         return true;
/* 192 */       if (mustExcludeDirectories()) {
/* 193 */         return false;
/*     */       }
/*     */     }
/* 196 */     return checkAgainstPatterns(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean checkAgainstPatterns(String name)
/*     */   {
/* 205 */     Iterator iterator = null;
/* 206 */     StringPattern pattern = null;
/*     */     
/* 208 */     iterator = getStringPatterns().iterator();
/* 209 */     while (iterator.hasNext())
/*     */     {
/* 211 */       pattern = (StringPattern)iterator.next();
/* 212 */       if (pattern.matches(name)) {
/* 213 */         return true;
/*     */       }
/*     */     }
/* 216 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean mustIncludeDirectories()
/*     */   {
/* 227 */     return getDirHandling() == 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean mustExcludeDirectories()
/*     */   {
/* 237 */     return getDirHandling() == 3;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/pf/file/ExtendedFileFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */