/*     */ package org.pf.file;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipFile;
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
/*     */ public class FileLocator
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*  51 */   private static final String FILE_PROTOCOL_INDICATOR = "file:" + File.separator;
/*  52 */   private static final String ARCHIVE_INDICATOR = "!" + File.separator;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  57 */   private FileLocator parent = null;
/*  58 */   protected FileLocator getParent() { return this.parent; }
/*  59 */   protected void setParent(FileLocator newValue) { this.parent = newValue; }
/*     */   
/*  61 */   private File file = null;
/*  62 */   protected File getFile() { return this.file; }
/*  63 */   protected void setFile(File newValue) { this.file = newValue; }
/*     */   
/*  65 */   private ZipFile zipFile = null;
/*  66 */   protected ZipFile getZipFile() { return this.zipFile; }
/*  67 */   protected void setZipFile(ZipFile newValue) { this.zipFile = newValue; }
/*     */   
/*  69 */   private boolean exists = true;
/*  70 */   protected boolean getExists() { return this.exists; }
/*  71 */   protected void setExists(boolean newValue) { this.exists = newValue; }
/*     */   
/*  73 */   private Exception exception = null;
/*  74 */   protected Exception getException() { return this.exception; }
/*  75 */   protected void setException(Exception newValue) { this.exception = newValue; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static FileLocator create(File file)
/*     */   {
/*  85 */     FileLocator locator = new FileLocator();
/*     */     
/*  87 */     return locator.createFrom(file);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static FileLocator create(String filename)
/*     */   {
/*  97 */     return create(new File(filename));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static FileLocator newWith(FileLocator aParent, String[] pathElements)
/*     */     throws Exception
/*     */   {
/* 105 */     FileLocator locator = new FileLocator();
/*     */     
/* 107 */     return locator.createFrom(aParent, pathElements);
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
/*     */   public File realFile()
/*     */   {
/*     */     File aFile;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 144 */       aFile = fileRef();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 148 */       aFile = null;
/*     */     }
/* 150 */     return aFile;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean exists()
/*     */   {
/* 160 */     return getExists();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isFile()
/*     */   {
/*     */     try
/*     */     {
/* 173 */       if (exists()) {
/* 174 */         return isFileElement(getFile());
/*     */       }
/* 176 */       return false;
/*     */     }
/*     */     catch (Exception e) {}
/*     */     
/* 180 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isDirectory()
/*     */   {
/*     */     try
/*     */     {
/* 194 */       if (exists()) {
/* 195 */         return !isFileElement(getFile());
/*     */       }
/* 197 */       return false;
/*     */     }
/*     */     catch (Exception e) {}
/*     */     
/* 201 */     return false;
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
/*     */   public long size()
/*     */   {
/*     */     try
/*     */     {
/* 216 */       if (isInArchive())
/*     */       {
/* 218 */         ZipEntry entry = archiveEntry();
/*     */         
/* 220 */         return entry.getSize();
/*     */       }
/*     */       
/*     */ 
/* 224 */       return getFile().length();
/*     */     }
/*     */     catch (Exception ex) {}
/*     */     
/*     */ 
/*     */ 
/* 230 */     return 0L;
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
/*     */   public long lastModified()
/*     */   {
/*     */     try
/*     */     {
/* 246 */       if (isInArchive())
/*     */       {
/* 248 */         ZipEntry entry = archiveEntry();
/* 249 */         return entry.getTime();
/*     */       }
/*     */       
/*     */ 
/* 253 */       return getFile().lastModified();
/*     */     }
/*     */     catch (Exception ex) {}
/*     */     
/*     */ 
/*     */ 
/* 259 */     return 0L;
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
/*     */   public InputStream getInputStream()
/*     */     throws Exception
/*     */   {
/* 273 */     if (isInArchive())
/*     */     {
/* 275 */       ZipEntry entry = archiveEntry();
/* 276 */       return container().getInputStream(entry);
/*     */     }
/*     */     
/*     */ 
/* 280 */     return new FileInputStream(getFile());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isInArchive()
/*     */   {
/* 292 */     return getParent() != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getPath()
/*     */   {
/* 302 */     return fullFilePath(false).getPath();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAbsolutePath()
/*     */   {
/* 312 */     return fullFilePath(true).getPath();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getStandardizedPath()
/*     */   {
/* 324 */     return fileUtil().standardize(getPath());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getStandardizedAbsolutePath()
/*     */   {
/* 336 */     return fileUtil().standardize(getAbsolutePath());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Exception exception()
/*     */   {
/* 347 */     return getException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public URL toURL()
/*     */     throws MalformedURLException
/*     */   {
/* 358 */     StringBuffer buffer = new StringBuffer(128);
/*     */     
/* 360 */     urlPath(buffer);
/* 361 */     return new URL(buffer.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected FileLocator createFrom(File filePath)
/*     */   {
/* 372 */     FileLocator locator = null;
/* 373 */     String[] parts = null;
/* 374 */     File path = filePath;
/*     */     
/* 376 */     if (path.getPath().startsWith(FILE_PROTOCOL_INDICATOR)) {
/* 377 */       path = convertFromURLSyntax(path);
/*     */     }
/* 379 */     parts = str().parts(path.getPath(), File.separator);
/*     */     try
/*     */     {
/* 382 */       locator = initFromPath(parts, path.getPath().startsWith(File.separator));
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 386 */       setException(ex);
/* 387 */       doesNotExist(path);
/* 388 */       locator = this;
/*     */     }
/* 390 */     return locator;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private FileLocator createFrom(FileLocator aParent, String[] pathElements)
/*     */     throws Exception
/*     */   {
/* 398 */     setParent(aParent);
/* 399 */     return initFromPath(pathElements, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected FileLocator initFromPath(String[] parts, boolean startsFromRoot)
/*     */     throws Exception
/*     */   {
/* 407 */     FileLocator locator = this;
/* 408 */     File pathElement = null;
/* 409 */     String[] rest = null;
/* 410 */     boolean elementExists = false;
/*     */     
/* 412 */     if (startsFromRoot) {
/* 413 */       pathElement = new File(File.separator);
/*     */     }
/* 415 */     for (int i = 0; i < parts.length; i++)
/*     */     {
/* 417 */       if (pathElement == null) {
/* 418 */         pathElement = new File(parts[i]);
/*     */       } else {
/* 420 */         pathElement = new File(pathElement, parts[i]);
/*     */       }
/* 422 */       elementExists = doesElementExist(pathElement);
/*     */       
/* 424 */       if (elementExists)
/*     */       {
/* 426 */         setFile(pathElement);
/* 427 */         if (isFileElement(pathElement))
/*     */         {
/*     */ 
/* 430 */           if (i >= parts.length - 1)
/*     */             break;
/* 432 */           rest = str().copyFrom(parts, i + 1);
/*     */           
/* 434 */           locator = newWith(this, rest); break;
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */       }
/* 441 */       else if (isInArchive())
/*     */       {
/* 443 */         if (i >= parts.length - 1)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 451 */           throw new Exception("\"" + pathElement.getPath() + "\" does not exist");
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 456 */         throw new Exception("\"" + pathElement.getPath() + "\" does not exist");
/*     */       }
/*     */     }
/*     */     
/* 460 */     return locator;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean doesElementExist(File element)
/*     */     throws Exception
/*     */   {
/* 468 */     if (isInArchive())
/*     */     {
/* 470 */       return doesElementExistInArchive(element.getPath());
/*     */     }
/*     */     
/*     */ 
/* 474 */     return element.exists();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean isFileElement(File element)
/*     */     throws Exception
/*     */   {
/* 483 */     if (isInArchive())
/*     */     {
/* 485 */       return isFileInArchive(element.getPath());
/*     */     }
/*     */     
/*     */ 
/* 489 */     return element.isFile();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean doesElementExistInArchive(String elementName)
/*     */     throws Exception
/*     */   {
/* 500 */     ZipEntry entry = entryFromArchive(elementName);
/*     */     
/* 502 */     return entry != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean isFileInArchive(String elementName)
/*     */     throws Exception
/*     */   {
/* 511 */     ZipEntry entry = entryFromArchive(elementName);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 518 */     return (entry != null) && (entry.getSize() > 0L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ZipEntry entryFromArchive(String elementName)
/*     */     throws Exception
/*     */   {
/* 530 */     String name = str().replaceAll(elementName, "\\", "/");
/* 531 */     ZipFile archive = container();
/* 532 */     ZipEntry entry = archive.getEntry(name);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 551 */     return entry;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected ZipEntry archiveEntry()
/*     */     throws Exception
/*     */   {
/* 559 */     return entryFromArchive(getFile().getPath());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void doesNotExist(File file)
/*     */   {
/* 566 */     setExists(false);
/* 567 */     setFile(file);
/*     */   }
/*     */   
/*     */ 
/*     */   protected File fullFilePath(boolean absolute)
/*     */   {
/*     */     File full;
/*     */     
/*     */     File full;
/* 576 */     if (isInArchive())
/*     */     {
/* 578 */       full = new File(getParent().fullFilePath(absolute), getFile().getPath());
/*     */     }
/*     */     else
/*     */     {
/*     */       File full;
/* 583 */       if (absolute) {
/* 584 */         full = getFile().getAbsoluteFile();
/*     */       } else {
/* 586 */         full = getFile();
/*     */       }
/*     */     }
/* 589 */     return full;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void urlPath(StringBuffer buffer)
/*     */   {
/* 596 */     if (isInArchive())
/*     */     {
/* 598 */       getParent().urlPath(buffer);
/* 599 */       buffer.append(ARCHIVE_INDICATOR);
/*     */     }
/*     */     else
/*     */     {
/* 603 */       buffer.append(FILE_PROTOCOL_INDICATOR);
/*     */     }
/* 605 */     buffer.append(getFile().getPath());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected File fileRef()
/*     */     throws Exception
/*     */   {
/* 618 */     if (isInArchive())
/*     */     {
/* 620 */       ZipEntry entry = archiveEntry();
/* 621 */       InputStream archiveStream = container().getInputStream(entry);
/* 622 */       File tempFile = File.createTempFile("FLOC_", ".xtr");
/* 623 */       tempFile.deleteOnExit();
/* 624 */       FileOutputStream fileStream = new FileOutputStream(tempFile);
/* 625 */       fileUtil().copyStream(archiveStream, fileStream);
/* 626 */       return tempFile;
/*     */     }
/*     */     
/*     */ 
/* 630 */     return getFile();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ZipFile archive()
/*     */     throws Exception
/*     */   {
/* 643 */     if (getZipFile() == null)
/*     */     {
/* 645 */       setZipFile(new ZipFile(fileRef()));
/*     */     }
/* 647 */     return getZipFile();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ZipFile container()
/*     */     throws Exception
/*     */   {
/* 659 */     if (isInArchive()) {
/* 660 */       return getParent().archive();
/*     */     }
/* 662 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected File convertFromURLSyntax(File file)
/*     */   {
/* 671 */     String newStr = file.getPath().substring(FILE_PROTOCOL_INDICATOR.length());
/* 672 */     newStr = str().replaceAll(newStr, ARCHIVE_INDICATOR, File.separator);
/*     */     
/* 674 */     return new File(newStr);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected StringUtil str()
/*     */   {
/* 681 */     return StringUtil.current();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected FileUtil fileUtil()
/*     */   {
/* 688 */     return FileUtil.current();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/pf/file/FileLocator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */