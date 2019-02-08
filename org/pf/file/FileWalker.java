/*     */ package org.pf.file;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FilenameFilter;
/*     */ import org.pf.text.StringUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FileWalker
/*     */ {
/*     */   public static final char PATTERN_SEPARATOR_CHAR = ';';
/*     */   public static final String PATTERN_SEPARATOR = ";";
/*  47 */   private FileHandler fileHandler = null;
/*  48 */   protected FileHandler getFileHandler() { return this.fileHandler; }
/*  49 */   protected void setFileHandler(FileHandler newValue) { this.fileHandler = newValue; }
/*     */   
/*  51 */   private boolean goOn = true;
/*  52 */   protected boolean getGoOn() { return this.goOn; }
/*  53 */   protected void setGoOn(boolean newValue) { this.goOn = newValue; }
/*     */   
/*  55 */   private Character digitWildcard = null;
/*  56 */   protected Character getDigitWildcard() { return this.digitWildcard; }
/*  57 */   protected void setDigitWildcard(Character newValue) { this.digitWildcard = newValue; }
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
/*     */   public FileWalker(FileHandler handler)
/*     */   {
/*  70 */     setFileHandler(handler);
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
/*     */   public FileWalker(FileHandler handler, char digitWildcard)
/*     */   {
/*  84 */     this(handler);
/*  85 */     setDigitWildcardChar(digitWildcard);
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
/*     */ 
/*     */ 
/*     */   public long walkThrough(String dir, String pattern, boolean recursive)
/*     */   {
/* 111 */     ExtendedFileFilter filter = null;
/* 112 */     String[] patterns = null;
/*     */     
/*     */ 
/* 115 */     setGoOn(true);
/* 116 */     filter = new ExtendedFileFilter();
/*     */     
/* 118 */     patterns = extractPatterns(pattern);
/* 119 */     for (int i = 0; i < patterns.length; i++)
/*     */     {
/* 121 */       String strPattern = patterns[i];
/* 122 */       if (hasDigitWildcard())
/*     */       {
/* 124 */         filter.addPattern(strPattern, true, getDigitWildcardChar());
/*     */       }
/*     */       else
/*     */       {
/* 128 */         filter.addPattern(strPattern, true);
/*     */       }
/*     */     }
/*     */     
/* 132 */     if (recursive) {
/* 133 */       filter.alwaysIncludeDirectories();
/*     */     } else {
/* 135 */       filter.alwaysExcludeDirectories();
/*     */     }
/* 137 */     return walkThrough(dir, filter, recursive);
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
/*     */   public void setDigitWildcardChar(char digitWildcard)
/*     */   {
/* 150 */     if (digitWildcard <= 0)
/*     */     {
/* 152 */       setDigitWildcard(null);
/*     */     }
/*     */     else
/*     */     {
/* 156 */       setDigitWildcard(new Character(digitWildcard));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected long walkThrough(String dir, FilenameFilter filter, boolean recursive)
/*     */   {
/* 168 */     long counter = 0L;
/* 169 */     File directory = null;
/* 170 */     File file = null;
/* 171 */     File[] files = null;
/* 172 */     int index = 0;
/*     */     
/* 174 */     directory = new File(dir);
/* 175 */     files = directory.listFiles(filter);
/* 176 */     if (files == null) {
/* 177 */       return counter;
/*     */     }
/* 179 */     setGoOn(getFileHandler().directoryStart(directory, files.length));
/* 180 */     if (!getGoOn()) {
/* 181 */       return counter;
/*     */     }
/* 183 */     for (index = 0; index < files.length; index++)
/*     */     {
/* 185 */       file = files[index];
/*     */       
/* 187 */       if (file.isDirectory())
/*     */       {
/* 189 */         if (recursive)
/*     */         {
/* 191 */           counter += walkThrough(file.getPath(), filter, recursive);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 196 */         setGoOn(getFileHandler().handleFile(file));
/* 197 */         counter += 1L;
/*     */       }
/* 199 */       if (!getGoOn())
/*     */         break;
/*     */     }
/* 202 */     setGoOn(getFileHandler().directoryEnd(directory));
/*     */     
/* 204 */     return counter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String[] extractPatterns(String pattern)
/*     */   {
/* 211 */     return StringUtil.current().parts(pattern, ";");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected char getDigitWildcardChar()
/*     */   {
/* 218 */     if (hasDigitWildcard()) {
/* 219 */       return getDigitWildcard().charValue();
/*     */     }
/* 221 */     return '\000';
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean hasDigitWildcard()
/*     */   {
/* 228 */     return getDigitWildcard() != null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/pf/file/FileWalker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */