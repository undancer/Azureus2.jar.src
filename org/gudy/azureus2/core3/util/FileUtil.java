/*      */ package org.gudy.azureus2.core3.util;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreOperationTask;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileFilter;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.SocketTimeoutException;
/*      */ import java.net.URI;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import java.util.zip.GZIPInputStream;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.platform.PlatformManager;
/*      */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*      */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*      */ import org.gudy.azureus2.plugins.platform.PlatformManagerException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class FileUtil
/*      */ {
/*   51 */   private static final LogIDs LOGID = LogIDs.CORE;
/*   52 */   public static final String DIR_SEP = System.getProperty("file.separator");
/*      */   
/*      */   private static final int RESERVED_FILE_HANDLE_COUNT = 4;
/*      */   
/*   56 */   private static boolean first_reservation = true;
/*   57 */   private static boolean is_my_lock_file = false;
/*   58 */   private static final List reserved_file_handles = new ArrayList();
/*   59 */   private static final AEMonitor class_mon = new AEMonitor("FileUtil:class");
/*      */   
/*      */   private static Method reflectOnUsableSpace;
/*      */   
/*   63 */   private static char[] char_conversion_mapping = null;
/*      */   private static boolean sce_checked;
/*      */   private static String script_encoding;
/*      */   
/*      */   static
/*      */   {
/*      */     try {
/*   70 */       reflectOnUsableSpace = File.class.getMethod("getUsableSpace", (Class[])null);
/*      */     }
/*      */     catch (Throwable e) {
/*   73 */       reflectOnUsableSpace = null;
/*      */     }
/*      */   }
/*      */   
/*      */   public static boolean isAncestorOf(File parent, File child) {
/*   78 */     parent = canonise(parent);
/*   79 */     child = canonise(child);
/*   80 */     if (parent.equals(child)) return true;
/*   81 */     String parent_s = parent.getPath();
/*   82 */     String child_s = child.getPath();
/*   83 */     if (parent_s.charAt(parent_s.length() - 1) != File.separatorChar) {
/*   84 */       parent_s = parent_s + File.separatorChar;
/*      */     }
/*   86 */     return child_s.startsWith(parent_s);
/*      */   }
/*      */   
/*      */   public static File canonise(File file) {
/*   90 */     try { return file.getCanonicalFile(); } catch (IOException ioe) {}
/*   91 */     return file;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getCanonicalFileName(String filename)
/*      */   {
/*   99 */     String canonicalFileName = filename;
/*      */     try {
/*  101 */       canonicalFileName = new File(filename).getCanonicalPath();
/*      */     }
/*      */     catch (IOException ignore) {}
/*  104 */     return canonicalFileName;
/*      */   }
/*      */   
/*      */   public static File getUserFile(String filename)
/*      */   {
/*  109 */     return new File(SystemProperties.getUserPath(), filename);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static File getApplicationFile(String filename)
/*      */   {
/*  124 */     String path = SystemProperties.getApplicationPath();
/*      */     
/*  126 */     if ((Constants.isOSX) && (!new File(path, "Azureus2.jar").exists()))
/*      */     {
/*      */ 
/*  129 */       path = path + SystemProperties.getApplicationName() + ".app/Contents/";
/*      */     }
/*      */     
/*  132 */     return new File(path, filename);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean recursiveDelete(File f)
/*      */   {
/*  141 */     String defSaveDir = COConfigurationManager.getStringParameter("Default save path");
/*  142 */     String moveToDir = COConfigurationManager.getStringParameter("Completed Files Directory", "");
/*      */     try
/*      */     {
/*  145 */       moveToDir = new File(moveToDir).getCanonicalPath();
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     try {
/*  149 */       defSaveDir = new File(defSaveDir).getCanonicalPath();
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */     try
/*      */     {
/*  155 */       if (f.getCanonicalPath().equals(moveToDir)) {
/*  156 */         System.out.println("FileUtil::recursiveDelete:: not allowed to delete the MoveTo dir !");
/*  157 */         return false;
/*      */       }
/*  159 */       if (f.getCanonicalPath().equals(defSaveDir)) {
/*  160 */         System.out.println("FileUtil::recursiveDelete:: not allowed to delete the default data dir !");
/*  161 */         return false;
/*      */       }
/*      */       
/*  164 */       if (f.isDirectory()) {
/*  165 */         File[] files = f.listFiles();
/*  166 */         for (int i = 0; i < files.length; i++) {
/*  167 */           if (!recursiveDelete(files[i]))
/*      */           {
/*  169 */             return false;
/*      */           }
/*      */         }
/*  172 */         if (!f.delete())
/*      */         {
/*  174 */           return false;
/*      */         }
/*      */         
/*      */       }
/*  178 */       else if (!f.delete())
/*      */       {
/*  180 */         return false;
/*      */       }
/*      */     }
/*      */     catch (Exception ignore) {}
/*      */     
/*  185 */     return true;
/*      */   }
/*      */   
/*      */   public static boolean recursiveDeleteNoCheck(File f) {
/*      */     try {
/*  190 */       if (f.isDirectory()) {
/*  191 */         File[] files = f.listFiles();
/*  192 */         for (int i = 0; i < files.length; i++) {
/*  193 */           if (!recursiveDeleteNoCheck(files[i]))
/*      */           {
/*  195 */             return false;
/*      */           }
/*      */         }
/*  198 */         if (!f.delete())
/*      */         {
/*  200 */           return false;
/*      */         }
/*      */         
/*      */       }
/*  204 */       else if (!f.delete())
/*      */       {
/*  206 */         return false;
/*      */       }
/*      */     }
/*      */     catch (Exception ignore) {}
/*      */     
/*  211 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static long getFileOrDirectorySize(File file)
/*      */   {
/*  218 */     if (file.isFile())
/*      */     {
/*  220 */       return file.length();
/*      */     }
/*      */     
/*      */ 
/*  224 */     long res = 0L;
/*      */     
/*  226 */     File[] files = file.listFiles();
/*      */     
/*  228 */     if (files != null)
/*      */     {
/*  230 */       for (int i = 0; i < files.length; i++)
/*      */       {
/*  232 */         res += getFileOrDirectorySize(files[i]);
/*      */       }
/*      */     }
/*      */     
/*  236 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void recursiveEmptyDirDelete(File f, Set ignore_set, boolean log_warnings)
/*      */   {
/*      */     try
/*      */     {
/*  247 */       String defSaveDir = COConfigurationManager.getStringParameter("Default save path");
/*  248 */       String moveToDir = COConfigurationManager.getStringParameter("Completed Files Directory", "");
/*      */       
/*  250 */       if (defSaveDir.trim().length() > 0)
/*      */       {
/*  252 */         defSaveDir = new File(defSaveDir).getCanonicalPath();
/*      */       }
/*      */       
/*  255 */       if (moveToDir.trim().length() > 0)
/*      */       {
/*  257 */         moveToDir = new File(moveToDir).getCanonicalPath();
/*      */       }
/*      */       
/*  260 */       if (f.isDirectory())
/*      */       {
/*  262 */         File[] files = f.listFiles();
/*      */         
/*  264 */         if (files == null)
/*      */         {
/*  266 */           if (log_warnings) {
/*  267 */             Debug.out("Empty folder delete:  failed to list contents of directory " + f);
/*      */           }
/*      */           
/*  270 */           return;
/*      */         }
/*      */         
/*  273 */         for (int i = 0; i < files.length; i++)
/*      */         {
/*  275 */           File x = files[i];
/*      */           
/*  277 */           if (x.isDirectory())
/*      */           {
/*  279 */             recursiveEmptyDirDelete(files[i], ignore_set, log_warnings);
/*      */ 
/*      */ 
/*      */           }
/*  283 */           else if (ignore_set.contains(x.getName().toLowerCase()))
/*      */           {
/*  285 */             if (!x.delete())
/*      */             {
/*  287 */               if (log_warnings) {
/*  288 */                 Debug.out("Empty folder delete: failed to delete file " + x);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  295 */         if (f.getCanonicalPath().equals(moveToDir))
/*      */         {
/*  297 */           if (log_warnings) {
/*  298 */             Debug.out("Empty folder delete:  not allowed to delete the MoveTo dir !");
/*      */           }
/*      */           
/*  301 */           return;
/*      */         }
/*      */         
/*  304 */         if (f.getCanonicalPath().equals(defSaveDir))
/*      */         {
/*  306 */           if (log_warnings) {
/*  307 */             Debug.out("Empty folder delete:  not allowed to delete the default data dir !");
/*      */           }
/*      */           
/*  310 */           return;
/*      */         }
/*      */         
/*  313 */         File[] files_inside = f.listFiles();
/*  314 */         if (files_inside.length == 0)
/*      */         {
/*  316 */           if (!f.delete())
/*      */           {
/*  318 */             if (log_warnings) {
/*  319 */               Debug.out("Empty folder delete:  failed to delete directory " + f);
/*      */             }
/*      */           }
/*      */         }
/*  323 */         else if (log_warnings) {
/*  324 */           Debug.out("Empty folder delete:  " + files_inside.length + " file(s)/folder(s) still in \"" + f + "\" - first listed item is \"" + files_inside[0].getName() + "\". Not removing.");
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Exception e) {
/*  329 */       Debug.out(e.toString());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String convertOSSpecificChars(String file_name_in, boolean is_folder)
/*      */   {
/*      */     char[] mapping;
/*      */     
/*  339 */     synchronized (FileUtil.class)
/*      */     {
/*  341 */       if (char_conversion_mapping == null)
/*      */       {
/*  343 */         COConfigurationManager.addAndFireListener(new COConfigurationListener()
/*      */         {
/*      */ 
/*      */           public void configurationSaved()
/*      */           {
/*  348 */             synchronized (FileUtil.class)
/*      */             {
/*  350 */               String map = COConfigurationManager.getStringParameter("File.Character.Conversions");
/*      */               
/*  352 */               String[] bits = map.split(",");
/*      */               
/*  354 */               List<Character> chars = new ArrayList();
/*      */               
/*  356 */               for (String bit : bits) {
/*  357 */                 bit = bit.trim();
/*  358 */                 if (bit.length() == 3) {
/*  359 */                   char from = bit.charAt(0);
/*  360 */                   char to = bit.charAt(2);
/*      */                   
/*  362 */                   chars.add(Character.valueOf(from));
/*  363 */                   chars.add(Character.valueOf(to));
/*      */                 }
/*      */               }
/*      */               
/*  367 */               char[] new_map = new char[chars.size()];
/*      */               
/*  369 */               for (int i = 0; i < new_map.length; i++)
/*      */               {
/*  371 */                 new_map[i] = ((Character)chars.get(i)).charValue();
/*      */               }
/*      */               
/*  374 */               FileUtil.access$002(new_map);
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*  380 */       mapping = char_conversion_mapping;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  385 */     char[] chars = file_name_in.toCharArray();
/*      */     
/*  387 */     if (mapping.length == 2)
/*      */     {
/*      */ 
/*      */ 
/*  391 */       char from = mapping[0];
/*  392 */       char to = mapping[1];
/*      */       
/*  394 */       for (int i = 0; i < chars.length; i++)
/*      */       {
/*  396 */         if (chars[i] == from)
/*      */         {
/*  398 */           chars[i] = to;
/*      */         }
/*      */       }
/*  401 */     } else if (mapping.length > 0)
/*      */     {
/*  403 */       for (int i = 0; i < chars.length; i++)
/*      */       {
/*  405 */         char c = chars[i];
/*      */         
/*  407 */         for (int j = 0; j < mapping.length; j += 2)
/*      */         {
/*  409 */           if (c == mapping[j])
/*      */           {
/*  411 */             chars[i] = mapping[(j + 1)];
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  417 */     if (!Constants.isOSX)
/*      */     {
/*  419 */       if (Constants.isWindows)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  425 */         String not_allowed = "\\/:?*<>|";
/*  426 */         for (int i = 0; i < chars.length; i++) {
/*  427 */           if (not_allowed.indexOf(chars[i]) != -1) {
/*  428 */             chars[i] = '_';
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  434 */         if (is_folder)
/*      */         {
/*  436 */           for (int i = chars.length - 1; (i >= 0) && ((chars[i] == '.') || (chars[i] == ' ')); i--) { chars[i] = '_';
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  443 */       for (int i = 0; i < chars.length; i++)
/*      */       {
/*  445 */         char c = chars[i];
/*      */         
/*  447 */         if ((c == '/') || (c == '\r') || (c == '\n'))
/*      */         {
/*  449 */           chars[i] = ' ';
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  454 */     String file_name_out = new String(chars);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  465 */       if (Constants.isWindows)
/*      */       {
/*  467 */         while (file_name_out.endsWith(" "))
/*      */         {
/*  469 */           file_name_out = file_name_out.substring(0, file_name_out.length() - 1);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  474 */       String str = new File(file_name_out).getCanonicalFile().toString();
/*      */       
/*  476 */       int p = str.lastIndexOf(File.separator);
/*      */       
/*  478 */       file_name_out = str.substring(p + 1);
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  488 */     return file_name_out;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void writeResilientConfigFile(String file_name, Map data)
/*      */   {
/*  496 */     File parent_dir = new File(SystemProperties.getUserPath());
/*      */     
/*  498 */     boolean use_backups = COConfigurationManager.getBooleanParameter("Use Config File Backups");
/*      */     
/*  500 */     writeResilientFile(parent_dir, file_name, data, use_backups);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void writeResilientFile(File file, Map data)
/*      */   {
/*  508 */     writeResilientFile(file.getParentFile(), file.getName(), data, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean writeResilientFileWithResult(File parent_dir, String file_name, Map data)
/*      */   {
/*  517 */     return writeResilientFile(parent_dir, file_name, data);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void writeResilientFile(File parent_dir, String file_name, Map data, boolean use_backup)
/*      */   {
/*  527 */     writeResilientFile(parent_dir, file_name, data, use_backup, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void writeResilientFile(File parent_dir, String file_name, Map data, boolean use_backup, boolean copy_to_backup)
/*      */   {
/*  538 */     if (use_backup)
/*      */     {
/*  540 */       File originator = new File(parent_dir, file_name);
/*      */       
/*  542 */       if (originator.exists())
/*      */       {
/*  544 */         backupFile(originator, copy_to_backup);
/*      */       }
/*      */     }
/*      */     
/*  548 */     writeResilientFile(parent_dir, file_name, data);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean resilientConfigFileExists(String name)
/*      */   {
/*  654 */     File parent_dir = new File(SystemProperties.getUserPath());
/*      */     
/*  656 */     boolean use_backups = COConfigurationManager.getBooleanParameter("Use Config File Backups");
/*      */     
/*  658 */     return (new File(parent_dir, name).exists()) || ((use_backups) && (new File(parent_dir, name + ".bak").exists()));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Map readResilientConfigFile(String file_name)
/*      */   {
/*  670 */     File parent_dir = new File(SystemProperties.getUserPath());
/*      */     
/*  672 */     boolean use_backups = COConfigurationManager.getBooleanParameter("Use Config File Backups");
/*      */     
/*  674 */     return readResilientFile(parent_dir, file_name, use_backups);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Map readResilientConfigFile(String file_name, boolean use_backups)
/*      */   {
/*  686 */     File parent_dir = new File(SystemProperties.getUserPath());
/*      */     
/*  688 */     if (!use_backups)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  694 */       if (new File(parent_dir, file_name + ".bak").exists())
/*      */       {
/*  696 */         use_backups = true;
/*      */       }
/*      */     }
/*      */     
/*  700 */     return readResilientFile(parent_dir, file_name, use_backups);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Map readResilientFile(File file)
/*      */   {
/*  711 */     return readResilientFile(file.getParentFile(), file.getName(), false, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Map readResilientFile(File parent_dir, String file_name, boolean use_backup)
/*      */   {
/*  724 */     return readResilientFile(parent_dir, file_name, use_backup, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Map readResilientFile(File parent_dir, String file_name, boolean use_backup, boolean intern_keys)
/*      */   {
/*  743 */     File backup_file = new File(parent_dir, file_name + ".bak");
/*      */     
/*  745 */     if (use_backup)
/*      */     {
/*  747 */       use_backup = backup_file.exists();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  753 */     Map res = readResilientFileSupport(parent_dir, file_name, !use_backup, intern_keys);
/*      */     
/*  755 */     if ((res == null) && (use_backup))
/*      */     {
/*      */ 
/*      */ 
/*  759 */       res = readResilientFileSupport(parent_dir, file_name + ".bak", false, intern_keys);
/*      */       
/*  761 */       if (res != null)
/*      */       {
/*  763 */         Debug.out("Backup file '" + backup_file + "' has been used for recovery purposes");
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  768 */         writeResilientFile(parent_dir, file_name, res, false);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*  774 */         res = readResilientFileSupport(parent_dir, file_name, true, true);
/*      */       }
/*      */     }
/*      */     
/*  778 */     if (res == null)
/*      */     {
/*  780 */       res = new HashMap();
/*      */     }
/*      */     
/*  783 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static Map readResilientFile(String original_file_name, File parent_dir, String file_name, int fail_count, boolean recovery_mode, boolean skip_key_intern)
/*      */   {
/*  852 */     boolean using_backup = file_name.endsWith(".saving");
/*      */     
/*  854 */     File file = new File(parent_dir, file_name);
/*      */     
/*      */ 
/*      */ 
/*  858 */     if ((!file.exists()) || (file.length() <= 1L))
/*      */     {
/*  860 */       if (using_backup)
/*      */       {
/*  862 */         if (!recovery_mode)
/*      */         {
/*  864 */           if (fail_count == 1)
/*      */           {
/*  866 */             Debug.out("Load of '" + original_file_name + "' fails, no usable file or backup");
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  879 */         return null;
/*      */       }
/*      */       
/*  882 */       if (!recovery_mode) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  891 */       return readResilientFile(original_file_name, parent_dir, file_name + ".saving", 0, recovery_mode, true);
/*      */     }
/*      */     
/*  894 */     BufferedInputStream bin = null;
/*      */     try
/*      */     {
/*  897 */       int retry_limit = 5;
/*      */       for (;;)
/*      */       {
/*      */         try
/*      */         {
/*  902 */           bin = new BufferedInputStream(new FileInputStream(file), 16384);
/*      */ 
/*      */         }
/*      */         catch (IOException e)
/*      */         {
/*      */ 
/*  908 */           retry_limit--; if (retry_limit == 0)
/*      */           {
/*  910 */             throw e;
/*      */           }
/*      */           
/*  913 */           if (Logger.isEnabled()) {
/*  914 */             Logger.log(new LogEvent(LOGID, "Failed to open '" + file.toString() + "', retrying", e));
/*      */           }
/*  916 */           Thread.sleep(500L);
/*      */         }
/*      */       }
/*      */       
/*  920 */       BDecoder decoder = new BDecoder();
/*      */       
/*  922 */       if (recovery_mode)
/*      */       {
/*  924 */         decoder.setRecoveryMode(true);
/*      */       }
/*      */       
/*  927 */       Map res = decoder.decodeStream(bin, !skip_key_intern);
/*      */       
/*  929 */       if ((using_backup) && (!recovery_mode))
/*      */       {
/*  931 */         Debug.out("Load of '" + original_file_name + "' had to revert to backup file");
/*      */       }
/*      */       
/*  934 */       return res;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  938 */       Debug.printStackTrace(e);
/*      */       try
/*      */       {
/*  941 */         if (bin != null)
/*      */         {
/*  943 */           bin.close();
/*      */           
/*  945 */           bin = null;
/*      */         }
/*      */       }
/*      */       catch (Exception x) {
/*  949 */         Debug.printStackTrace(x);
/*      */       }
/*      */       
/*      */       File bad;
/*      */       
/*  954 */       if (!recovery_mode)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  961 */         int bad_id = 0;
/*      */         
/*      */         for (;;)
/*      */         {
/*  965 */           File test = new File(parent_dir, file.getName() + ".bad" + (bad_id == 0 ? "" : new StringBuilder().append("").append(bad_id).toString()));
/*      */           
/*  967 */           if (!test.exists())
/*      */           {
/*  969 */             File bad = test;
/*      */             
/*  971 */             break;
/*      */           }
/*      */           
/*  974 */           bad_id++;
/*      */         }
/*      */         
/*  977 */         if (Logger.isEnabled()) {
/*  978 */           Logger.log(new LogEvent(LOGID, 1, "Read of '" + original_file_name + "' failed, decoding error. " + "Renaming to " + bad.getName()));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  984 */         copyFile(file, bad);
/*      */       }
/*      */       
/*  987 */       if (using_backup)
/*      */       {
/*  989 */         if (!recovery_mode)
/*      */         {
/*  991 */           Debug.out("Load of '" + original_file_name + "' fails, no usable file or backup");
/*      */         }
/*      */         
/*  994 */         return null;
/*      */       }
/*      */       
/*  997 */       return readResilientFile(original_file_name, parent_dir, file_name + ".saving", 1, recovery_mode, true);
/*      */     }
/*      */     finally
/*      */     {
/*      */       try
/*      */       {
/* 1003 */         if (bin != null)
/*      */         {
/* 1005 */           bin.close();
/*      */         }
/*      */       }
/*      */       catch (Exception e) {
/* 1009 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void deleteResilientFile(File file)
/*      */   {
/* 1018 */     file.delete();
/* 1019 */     new File(file.getParentFile(), file.getName() + ".bak").delete();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void deleteResilientConfigFile(String name)
/*      */   {
/* 1026 */     File parent_dir = new File(SystemProperties.getUserPath());
/*      */     
/* 1028 */     new File(parent_dir, name).delete();
/* 1029 */     new File(parent_dir, name + ".bak").delete();
/*      */   }
/*      */   
/*      */   private static void getReservedFileHandles()
/*      */   {
/*      */     try
/*      */     {
/* 1036 */       class_mon.enter();
/*      */       
/* 1038 */       while (reserved_file_handles.size() > 0)
/*      */       {
/*      */ 
/*      */ 
/* 1042 */         InputStream is = (InputStream)reserved_file_handles.remove(0);
/*      */         try
/*      */         {
/* 1045 */           is.close();
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1049 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1054 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static void releaseReservedFileHandles()
/*      */   {
/*      */     try
/*      */     {
/* 1063 */       class_mon.enter();
/*      */       
/* 1065 */       File lock_file = new File(SystemProperties.getUserPath() + ".lock");
/*      */       
/* 1067 */       if (first_reservation)
/*      */       {
/* 1069 */         first_reservation = false;
/*      */         
/* 1071 */         lock_file.delete();
/*      */         
/* 1073 */         is_my_lock_file = lock_file.createNewFile();
/*      */       }
/*      */       else
/*      */       {
/* 1077 */         lock_file.createNewFile();
/*      */       }
/*      */       
/* 1080 */       while (reserved_file_handles.size() < 4)
/*      */       {
/*      */ 
/*      */ 
/* 1084 */         InputStream is = new FileInputStream(lock_file);
/*      */         
/* 1086 */         reserved_file_handles.add(is);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1090 */       Debug.printStackTrace(e);
/*      */     }
/*      */     finally
/*      */     {
/* 1094 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean isMyFileLock()
/*      */   {
/* 1101 */     return is_my_lock_file;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void backupFile(String _filename, boolean _make_copy)
/*      */   {
/* 1111 */     backupFile(new File(_filename), _make_copy);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void backupFile(File _file, boolean _make_copy)
/*      */   {
/* 1121 */     if (_file.length() > 0L) {
/* 1122 */       File bakfile = new File(_file.getAbsolutePath() + ".bak");
/* 1123 */       if (bakfile.exists()) bakfile.delete();
/* 1124 */       if (_make_copy) {
/* 1125 */         copyFile(_file, bakfile);
/*      */       }
/*      */       else {
/* 1128 */         _file.renameTo(bakfile);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean copyFile(String _source_name, String _dest_name)
/*      */   {
/* 1142 */     return copyFile(new File(_source_name), new File(_dest_name));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean copyFile(File _source, File _dest)
/*      */   {
/*      */     try
/*      */     {
/* 1183 */       copyFile(new FileInputStream(_source), new FileOutputStream(_dest));
/* 1184 */       return true;
/*      */     }
/*      */     catch (Throwable e) {
/* 1187 */       Debug.printStackTrace(e); }
/* 1188 */     return false;
/*      */   }
/*      */   
/*      */   public static void copyFileWithException(File _source, File _dest) throws IOException
/*      */   {
/* 1193 */     copyFile(new FileInputStream(_source), new FileOutputStream(_dest));
/*      */   }
/*      */   
/*      */   public static boolean copyFile(File _source, OutputStream _dest, boolean closeInputStream) {
/*      */     try {
/* 1198 */       copyFile(new FileInputStream(_source), _dest, closeInputStream);
/* 1199 */       return true;
/*      */     }
/*      */     catch (Throwable e) {
/* 1202 */       Debug.printStackTrace(e); }
/* 1203 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void copyFile(InputStream _source, File _dest)
/*      */     throws IOException
/*      */   {
/* 1221 */     FileOutputStream dest = null;
/*      */     
/* 1223 */     boolean close_input = true;
/*      */     try
/*      */     {
/* 1226 */       dest = new FileOutputStream(_dest);
/*      */       
/*      */ 
/*      */ 
/* 1230 */       close_input = false;
/*      */       
/* 1232 */       copyFile(_source, dest, true);
/*      */     }
/*      */     finally
/*      */     {
/*      */       try {
/* 1237 */         if (close_input) {
/* 1238 */           _source.close();
/*      */         }
/*      */       }
/*      */       catch (IOException e) {}
/*      */       
/* 1243 */       if (dest != null)
/*      */       {
/* 1245 */         dest.close();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void copyFile(InputStream _source, File _dest, boolean _close_input_stream)
/*      */     throws IOException
/*      */   {
/* 1258 */     FileOutputStream dest = null;
/*      */     
/* 1260 */     boolean close_input = _close_input_stream;
/*      */     try
/*      */     {
/* 1263 */       dest = new FileOutputStream(_dest);
/*      */       
/* 1265 */       close_input = false;
/*      */       
/* 1267 */       copyFile(_source, dest, close_input);
/*      */     }
/*      */     finally
/*      */     {
/*      */       try {
/* 1272 */         if (close_input)
/*      */         {
/* 1274 */           _source.close();
/*      */         }
/*      */       }
/*      */       catch (IOException e) {}
/*      */       
/* 1279 */       if (dest != null)
/*      */       {
/* 1281 */         dest.close();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void copyFile(InputStream is, OutputStream os)
/*      */     throws IOException
/*      */   {
/* 1293 */     copyFile(is, os, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void copyFile(InputStream is, OutputStream os, boolean closeInputStream)
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/* 1306 */       if (!(is instanceof BufferedInputStream))
/*      */       {
/* 1308 */         is = new BufferedInputStream(is, 131072);
/*      */       }
/*      */       
/* 1311 */       byte[] buffer = new byte[131072];
/*      */       
/*      */       for (;;)
/*      */       {
/* 1315 */         int len = is.read(buffer);
/*      */         
/* 1317 */         if (len == -1) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/* 1322 */         os.write(buffer, 0, len);
/*      */       }
/*      */     } finally {
/*      */       try {
/* 1326 */         if (closeInputStream) {
/* 1327 */           is.close();
/*      */         }
/*      */       }
/*      */       catch (IOException e) {}
/*      */       
/*      */ 
/* 1333 */       os.close();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void copyFileOrDirectory(File from_file_or_dir, File to_parent_dir)
/*      */     throws IOException
/*      */   {
/* 1344 */     if (!from_file_or_dir.exists())
/*      */     {
/* 1346 */       throw new IOException("File '" + from_file_or_dir.toString() + "' doesn't exist");
/*      */     }
/*      */     
/* 1349 */     if (!to_parent_dir.exists())
/*      */     {
/* 1351 */       throw new IOException("File '" + to_parent_dir.toString() + "' doesn't exist");
/*      */     }
/*      */     
/* 1354 */     if (!to_parent_dir.isDirectory())
/*      */     {
/* 1356 */       throw new IOException("File '" + to_parent_dir.toString() + "' is not a directory");
/*      */     }
/*      */     
/* 1359 */     if (from_file_or_dir.isDirectory())
/*      */     {
/* 1361 */       File[] files = from_file_or_dir.listFiles();
/*      */       
/* 1363 */       File new_parent = new File(to_parent_dir, from_file_or_dir.getName());
/*      */       
/* 1365 */       mkdirs(new_parent);
/*      */       
/* 1367 */       for (int i = 0; i < files.length; i++)
/*      */       {
/* 1369 */         File from_file = files[i];
/*      */         
/* 1371 */         copyFileOrDirectory(from_file, new_parent);
/*      */       }
/*      */     }
/*      */     else {
/* 1375 */       File target = new File(to_parent_dir, from_file_or_dir.getName());
/*      */       
/* 1377 */       if (!copyFile(from_file_or_dir, target))
/*      */       {
/* 1379 */         throw new IOException("File copy from " + from_file_or_dir + " to " + target + " failed");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static File getFileOrBackup(String _filename)
/*      */   {
/*      */     try
/*      */     {
/* 1394 */       File file = new File(_filename);
/*      */       
/* 1396 */       if (file.length() <= 1L)
/*      */       {
/* 1398 */         File bakfile = new File(_filename + ".bak");
/* 1399 */         if (bakfile.length() <= 1L) {
/* 1400 */           return null;
/*      */         }
/* 1402 */         return bakfile;
/*      */       }
/* 1404 */       return file;
/*      */     }
/*      */     catch (Exception e) {
/* 1407 */       Debug.out(e); }
/* 1408 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static File getJarFileFromClass(Class cla)
/*      */   {
/*      */     try
/*      */     {
/* 1417 */       String str = cla.getName();
/*      */       
/* 1419 */       str = str.replace('.', '/') + ".class";
/*      */       
/* 1421 */       URL url = cla.getClassLoader().getResource(str);
/*      */       
/* 1423 */       if (url != null)
/*      */       {
/* 1425 */         String url_str = url.toExternalForm();
/*      */         
/* 1427 */         if (url_str.startsWith("jar:file:"))
/*      */         {
/* 1429 */           File jar_file = getJarFileFromURL(url_str);
/*      */           
/* 1431 */           if ((jar_file != null) && (jar_file.exists()))
/*      */           {
/* 1433 */             return jar_file;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1439 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/* 1442 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static File getJarFileFromURL(String url_str)
/*      */   {
/* 1449 */     if (url_str.startsWith("jar:file:"))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1458 */       url_str = url_str.replaceAll(" ", "%20");
/*      */       
/* 1460 */       if (!url_str.startsWith("jar:file:/"))
/*      */       {
/*      */ 
/* 1463 */         url_str = "jar:file:/".concat(url_str.substring(9));
/*      */       }
/*      */       
/*      */ 
/*      */       try
/*      */       {
/* 1469 */         int posPling = url_str.lastIndexOf('!');
/*      */         
/* 1471 */         String jarName = url_str.substring(4, posPling);
/*      */         
/*      */ 
/*      */         URI uri;
/*      */         
/*      */         try
/*      */         {
/* 1478 */           uri = URI.create(jarName);
/*      */           
/* 1480 */           if (!new File(uri).exists())
/*      */           {
/* 1482 */             throw new FileNotFoundException();
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/* 1486 */           jarName = "file:/" + UrlUtils.encode(jarName.substring(6));
/*      */           
/* 1488 */           uri = URI.create(jarName);
/*      */         }
/*      */         
/* 1491 */         return new File(uri);
/*      */ 
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*      */ 
/* 1497 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 1501 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean renameFile(File from_file, File to_file)
/*      */   {
/* 1509 */     return renameFile(from_file, to_file, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean renameFile(File from_file, File to_file, boolean fail_on_existing_directory)
/*      */   {
/* 1519 */     return renameFile(from_file, to_file, fail_on_existing_directory, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean renameFile(File from_file, File to_file, boolean fail_on_existing_directory, FileFilter file_filter)
/*      */   {
/* 1529 */     if (!from_file.exists())
/*      */     {
/* 1531 */       Debug.out("renameFile: source file '" + from_file + "' doesn't exist, failing");
/*      */       
/* 1533 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1539 */     if ((to_file.exists()) && ((fail_on_existing_directory) || (from_file.isFile()) || (to_file.isFile())))
/*      */     {
/* 1541 */       Debug.out("renameFile: target file '" + to_file + "' already exists, failing");
/*      */       
/* 1543 */       return false;
/*      */     }
/* 1545 */     File to_file_parent = to_file.getParentFile();
/* 1546 */     if (!to_file_parent.exists()) { mkdirs(to_file_parent);
/*      */     }
/* 1548 */     if (from_file.isDirectory())
/*      */     {
/* 1550 */       File[] files = null;
/* 1551 */       if (file_filter != null) files = from_file.listFiles(file_filter); else {
/* 1552 */         files = from_file.listFiles();
/*      */       }
/* 1554 */       if (files == null)
/*      */       {
/*      */ 
/*      */ 
/* 1558 */         return true;
/*      */       }
/*      */       
/* 1561 */       int last_ok = 0;
/*      */       
/* 1563 */       if (!to_file.exists()) { to_file.mkdir();
/*      */       }
/* 1565 */       for (int i = 0; i < files.length; i++)
/*      */       {
/* 1567 */         File ff = files[i];
/* 1568 */         File tf = new File(to_file, ff.getName());
/*      */         try
/*      */         {
/* 1571 */           if (renameFile(ff, tf, fail_on_existing_directory, file_filter))
/*      */           {
/* 1573 */             last_ok++;
/*      */           }
/*      */           else {
/*      */             break;
/*      */           }
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1581 */           Debug.out("renameFile: failed to rename file '" + ff.toString() + "' to '" + tf.toString() + "'", e);
/*      */           
/*      */ 
/* 1584 */           break;
/*      */         }
/*      */       }
/*      */       
/* 1588 */       if (last_ok == files.length)
/*      */       {
/* 1590 */         File[] remaining = from_file.listFiles();
/*      */         
/* 1592 */         if ((remaining != null) && (remaining.length > 0))
/*      */         {
/*      */ 
/* 1595 */           if (file_filter == null) {
/* 1596 */             Debug.out("renameFile: files remain in '" + from_file.toString() + "', not deleting");
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/* 1601 */             return true;
/*      */           }
/*      */           
/*      */ 
/*      */         }
/* 1606 */         else if (!from_file.delete()) {
/* 1607 */           Debug.out("renameFile: failed to delete '" + from_file.toString() + "'");
/*      */         }
/*      */         
/*      */ 
/* 1611 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1616 */       for (int i = 0; i < last_ok; i++)
/*      */       {
/* 1618 */         File ff = files[i];
/* 1619 */         File tf = new File(to_file, ff.getName());
/*      */         
/*      */         try
/*      */         {
/* 1623 */           if (!renameFile(tf, ff, false, null)) {
/* 1624 */             Debug.out("renameFile: recovery - failed to move file '" + tf.toString() + "' to '" + ff.toString() + "'");
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/* 1628 */           Debug.out("renameFile: recovery - failed to move file '" + tf.toString() + "' to '" + ff.toString() + "'", e);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1634 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 1638 */     boolean copy_and_delete = COConfigurationManager.getBooleanParameter("Copy And Delete Data Rather Than Move");
/*      */     
/* 1640 */     if (copy_and_delete)
/*      */     {
/* 1642 */       boolean move_if_same_drive = COConfigurationManager.getBooleanParameter("Move If On Same Drive");
/*      */       
/* 1644 */       if (move_if_same_drive)
/*      */       {
/*      */ 
/*      */ 
/* 1648 */         if (Constants.isWindows) {
/*      */           try
/*      */           {
/* 1651 */             String str1 = from_file.getCanonicalPath();
/* 1652 */             String str2 = to_file.getCanonicalPath();
/*      */             
/* 1654 */             char drive1 = ':';
/* 1655 */             char drive2 = ' ';
/*      */             
/* 1657 */             if ((str1.length() > 2) && (str1.charAt(1) == ':'))
/*      */             {
/* 1659 */               drive1 = Character.toLowerCase(str1.charAt(0));
/*      */             }
/* 1661 */             if ((str2.length() > 2) && (str2.charAt(1) == ':'))
/*      */             {
/* 1663 */               drive2 = Character.toLowerCase(str2.charAt(0));
/*      */             }
/*      */             
/* 1666 */             if (drive1 == drive2)
/*      */             {
/* 1668 */               copy_and_delete = false;
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1677 */     if ((!copy_and_delete) && (from_file.renameTo(to_file)))
/*      */     {
/*      */ 
/* 1680 */       return true;
/*      */     }
/*      */     
/* 1683 */     boolean success = false;
/*      */     
/*      */ 
/*      */ 
/* 1687 */     FileInputStream fis = null;
/*      */     
/* 1689 */     FileOutputStream fos = null;
/*      */     try
/*      */     {
/* 1692 */       fis = new FileInputStream(from_file);
/*      */       
/* 1694 */       fos = new FileOutputStream(to_file);
/*      */       
/* 1696 */       byte[] buffer = new byte[65536];
/*      */       
/*      */       for (;;)
/*      */       {
/* 1700 */         len = fis.read(buffer);
/*      */         
/* 1702 */         if (len <= 0) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/* 1707 */         fos.write(buffer, 0, len);
/*      */       }
/*      */       
/* 1710 */       fos.close();
/*      */       
/* 1712 */       fos = null;
/*      */       
/* 1714 */       fis.close();
/*      */       
/* 1716 */       fis = null;
/*      */       
/* 1718 */       if (!from_file.delete()) {
/* 1719 */         Debug.out("renameFile: failed to delete '" + from_file.toString() + "'");
/*      */         
/*      */ 
/* 1722 */         throw new Exception("Failed to delete '" + from_file.toString() + "'");
/*      */       }
/*      */       
/* 1725 */       success = true;
/*      */       
/* 1727 */       return 1;
/*      */     }
/*      */     catch (Throwable e) {
/*      */       int len;
/* 1731 */       Debug.out("renameFile: failed to rename '" + from_file.toString() + "' to '" + to_file.toString() + "'", e);
/*      */       
/*      */ 
/* 1734 */       return 0;
/*      */     }
/*      */     finally
/*      */     {
/* 1738 */       if (fis != null) {
/*      */         try
/*      */         {
/* 1741 */           fis.close();
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/* 1747 */       if (fos != null) {
/*      */         try
/*      */         {
/* 1750 */           fos.close();
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1758 */       if (!success)
/*      */       {
/* 1760 */         if (to_file.exists())
/*      */         {
/* 1762 */           to_file.delete();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean writeStringAsFile(File file, String text)
/*      */   {
/*      */     try
/*      */     {
/* 1776 */       return writeBytesAsFile2(file.getAbsolutePath(), text.getBytes("UTF-8"));
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1780 */       Debug.out(e);
/*      */     }
/* 1782 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void writeBytesAsFile(String filename, byte[] file_data)
/*      */   {
/* 1793 */     writeBytesAsFile2(filename, file_data);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean writeBytesAsFile2(String filename, byte[] file_data)
/*      */   {
/*      */     try
/*      */     {
/* 1802 */       File file = new File(filename);
/*      */       
/* 1804 */       if (!file.getParentFile().exists())
/*      */       {
/* 1806 */         file.getParentFile().mkdirs();
/*      */       }
/*      */       
/* 1809 */       FileOutputStream out = new FileOutputStream(file);
/*      */       try
/*      */       {
/* 1812 */         out.write(file_data);
/*      */       }
/*      */       finally
/*      */       {
/* 1816 */         out.close();
/*      */       }
/*      */       
/* 1819 */       return true;
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/* 1823 */       Debug.out("writeBytesAsFile:: error: ", t);
/*      */     }
/* 1825 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean deleteWithRecycle(File file, boolean force_no_recycle)
/*      */   {
/* 1834 */     if ((COConfigurationManager.getBooleanParameter("Move Deleted Data To Recycle Bin")) && (!force_no_recycle)) {
/*      */       try
/*      */       {
/* 1837 */         PlatformManager platform = PlatformManagerFactory.getPlatformManager();
/*      */         
/* 1839 */         if (platform.hasCapability(PlatformManagerCapabilities.RecoverableFileDelete))
/*      */         {
/* 1841 */           platform.performRecoverableFileDelete(file.getAbsolutePath());
/*      */           
/* 1843 */           return true;
/*      */         }
/*      */         
/*      */ 
/* 1847 */         return file.delete();
/*      */       }
/*      */       catch (PlatformManagerException e)
/*      */       {
/* 1851 */         return file.delete();
/*      */       }
/*      */     }
/*      */     
/* 1855 */     return file.delete();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String translateMoveFilePath(String old_root, String new_root, String file_to_move)
/*      */   {
/* 1867 */     if (!file_to_move.startsWith(old_root))
/*      */     {
/* 1869 */       return null;
/*      */     }
/*      */     
/* 1872 */     if (old_root.equals(new_root))
/*      */     {
/*      */ 
/*      */ 
/* 1876 */       return file_to_move;
/*      */     }
/*      */     
/* 1879 */     if (new_root.equals(file_to_move))
/*      */     {
/*      */ 
/*      */ 
/* 1883 */       return file_to_move;
/*      */     }
/*      */     
/* 1886 */     String file_suffix = file_to_move.substring(old_root.length());
/*      */     
/* 1888 */     if (file_suffix.startsWith(File.separator))
/*      */     {
/* 1890 */       file_suffix = file_suffix.substring(1);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/* 1898 */     else if (new_root.endsWith(File.separator))
/*      */     {
/* 1900 */       Debug.out("Hmm, this is not going to work out well... " + old_root + ", " + new_root + ", " + file_to_move);
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/* 1906 */       if (new_root.endsWith(file_suffix))
/*      */       {
/* 1908 */         return new_root;
/*      */       }
/*      */       
/* 1911 */       return new_root + file_suffix;
/*      */     }
/*      */     
/*      */ 
/* 1915 */     if (new_root.endsWith(File.separator))
/*      */     {
/* 1917 */       new_root = new_root.substring(0, new_root.length() - 1);
/*      */     }
/*      */     
/* 1920 */     return new_root + File.separator + file_suffix;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void runAsTask(AzureusCoreOperationTask task)
/*      */   {
/* 1927 */     AzureusCore core = AzureusCoreFactory.getSingleton();
/*      */     
/* 1929 */     core.createOperation(2, task);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean mkdirs(File f)
/*      */   {
/* 1938 */     if (Constants.isOSX) {
/* 1939 */       Pattern pat = Pattern.compile("^(/Volumes/[^/]+)");
/* 1940 */       Matcher matcher = pat.matcher(f.getParent());
/* 1941 */       if (matcher.find()) {
/* 1942 */         String sVolume = matcher.group();
/* 1943 */         File fVolume = new File(sVolume);
/* 1944 */         if (!fVolume.isDirectory()) {
/* 1945 */           Logger.log(new LogEvent(LOGID, 1, sVolume + " is not mounted or not available."));
/*      */           
/* 1947 */           return false;
/*      */         }
/*      */       }
/*      */     }
/* 1951 */     return f.mkdirs();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getExtension(String fName)
/*      */   {
/* 1961 */     int fileSepIndex = fName.lastIndexOf(File.separator);
/* 1962 */     int fileDotIndex = fName.lastIndexOf('.');
/* 1963 */     if ((fileSepIndex == fName.length() - 1) || (fileDotIndex == -1) || (fileSepIndex > fileDotIndex))
/*      */     {
/* 1965 */       return "";
/*      */     }
/*      */     
/* 1968 */     return fName.substring(fileDotIndex);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String readFileAsString(File file, int size_limit, String charset)
/*      */     throws IOException
/*      */   {
/* 1979 */     FileInputStream fis = new FileInputStream(file);
/*      */     try {
/* 1981 */       return readInputStreamAsString(fis, size_limit, charset);
/*      */     }
/*      */     finally {
/* 1984 */       fis.close();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String readFileAsString(File file, int size_limit)
/*      */     throws IOException
/*      */   {
/* 1995 */     FileInputStream fis = new FileInputStream(file);
/*      */     try {
/* 1997 */       return readInputStreamAsString(fis, size_limit);
/*      */     }
/*      */     finally {
/* 2000 */       fis.close();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String readGZippedFileAsString(File file, int size_limit)
/*      */     throws IOException
/*      */   {
/* 2011 */     FileInputStream fis = new FileInputStream(file);
/*      */     try
/*      */     {
/* 2014 */       GZIPInputStream zis = new GZIPInputStream(fis);
/*      */       
/* 2016 */       return readInputStreamAsString(zis, size_limit);
/*      */     }
/*      */     finally {
/* 2019 */       fis.close();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String readInputStreamAsString(InputStream is, int size_limit)
/*      */     throws IOException
/*      */   {
/* 2029 */     return readInputStreamAsString(is, size_limit, "ISO-8859-1");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String readInputStreamAsString(InputStream is, int size_limit, String charSet)
/*      */     throws IOException
/*      */   {
/* 2040 */     StringBuilder result = new StringBuilder(1024);
/*      */     
/* 2042 */     byte[] buffer = new byte[65536];
/*      */     
/*      */     for (;;)
/*      */     {
/* 2046 */       int len = is.read(buffer);
/*      */       
/* 2048 */       if (len <= 0) {
/*      */         break;
/*      */       }
/*      */       
/*      */ 
/* 2053 */       result.append(new String(buffer, 0, len, charSet));
/*      */       
/* 2055 */       if ((size_limit >= 0) && (result.length() > size_limit))
/*      */       {
/* 2057 */         result.setLength(size_limit);
/*      */         
/* 2059 */         break;
/*      */       }
/*      */     }
/*      */     
/* 2063 */     return result.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String readInputStreamAsStringWithTruncation(InputStream is, int size_limit)
/*      */     throws IOException
/*      */   {
/* 2073 */     StringBuilder result = new StringBuilder(1024);
/*      */     
/* 2075 */     byte[] buffer = new byte[65536];
/*      */     try
/*      */     {
/*      */       for (;;)
/*      */       {
/* 2080 */         int len = is.read(buffer);
/*      */         
/* 2082 */         if (len <= 0) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/* 2087 */         result.append(new String(buffer, 0, len, "ISO-8859-1"));
/*      */         
/* 2089 */         if ((size_limit >= 0) && (result.length() > size_limit))
/*      */         {
/* 2091 */           result.setLength(size_limit);
/*      */           
/* 2093 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (SocketTimeoutException e) {}
/*      */     
/* 2099 */     return result.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String readFileEndAsString(File file, int size_limit)
/*      */     throws IOException
/*      */   {
/* 2109 */     return readFileEndAsString(file, size_limit, "ISO-8859-1");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String readFileEndAsString(File file, int size_limit, String charset)
/*      */     throws IOException
/*      */   {
/* 2120 */     FileInputStream fis = new FileInputStream(file);
/*      */     try
/*      */     {
/* 2123 */       if (file.length() > size_limit)
/*      */       {
/*      */ 
/*      */ 
/* 2127 */         fis.skip(file.length() - size_limit);
/*      */       }
/*      */       
/* 2130 */       StringBuilder result = new StringBuilder(1024);
/*      */       
/* 2132 */       byte[] buffer = new byte[65536];
/*      */       int len;
/*      */       for (;;)
/*      */       {
/* 2136 */         len = fis.read(buffer);
/*      */         
/* 2138 */         if (len <= 0) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2145 */         result.append(new String(buffer, 0, len, charset));
/*      */         
/* 2147 */         if (result.length() > size_limit)
/*      */         {
/* 2149 */           result.setLength(size_limit);
/*      */           
/* 2151 */           break;
/*      */         }
/*      */       }
/*      */       
/* 2155 */       return result.toString();
/*      */     }
/*      */     finally
/*      */     {
/* 2159 */       fis.close();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static byte[] readInputStreamAsByteArray(InputStream is)
/*      */     throws IOException
/*      */   {
/* 2169 */     return readInputStreamAsByteArray(is, Integer.MAX_VALUE);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static byte[] readInputStreamAsByteArray(InputStream is, int size_limit)
/*      */     throws IOException
/*      */   {
/* 2179 */     ByteArrayOutputStream baos = new ByteArrayOutputStream(32768);
/*      */     
/* 2181 */     byte[] buffer = new byte[32768];
/*      */     
/*      */     for (;;)
/*      */     {
/* 2185 */       int len = is.read(buffer);
/*      */       
/* 2187 */       if (len <= 0) {
/*      */         break;
/*      */       }
/*      */       
/*      */ 
/* 2192 */       baos.write(buffer, 0, len);
/*      */       
/* 2194 */       if (baos.size() > size_limit)
/*      */       {
/* 2196 */         throw new IOException("size limit exceeded");
/*      */       }
/*      */     }
/*      */     
/* 2200 */     return baos.toByteArray();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static byte[] readFileAsByteArray(File file)
/*      */     throws IOException
/*      */   {
/* 2209 */     ByteArrayOutputStream baos = new ByteArrayOutputStream((int)file.length());
/*      */     
/* 2211 */     byte[] buffer = new byte[32768];
/*      */     
/* 2213 */     InputStream is = new FileInputStream(file);
/*      */     try
/*      */     {
/*      */       int len;
/*      */       for (;;) {
/* 2218 */         len = is.read(buffer);
/*      */         
/* 2220 */         if (len <= 0) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/* 2225 */         baos.write(buffer, 0, len);
/*      */       }
/*      */       
/* 2228 */       return baos.toByteArray();
/*      */     }
/*      */     finally
/*      */     {
/* 2232 */       is.close();
/*      */     }
/*      */   }
/*      */   
/*      */   public static final boolean getUsableSpaceSupported()
/*      */   {
/* 2238 */     return reflectOnUsableSpace != null;
/*      */   }
/*      */   
/*      */   public static final long getUsableSpace(File f)
/*      */   {
/*      */     try {
/* 2244 */       return ((Long)reflectOnUsableSpace.invoke(f, new Object[0])).longValue();
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 2248 */     return -1L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean canReallyWriteToAppDirectory()
/*      */   {
/* 2255 */     if (!getApplicationFile("bogus").getParentFile().canWrite())
/*      */     {
/* 2257 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2262 */     if (Constants.isWindowsVistaOrHigher) {
/*      */       try
/*      */       {
/* 2265 */         File write_test = getApplicationFile("_az_.dll");
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2270 */         FileOutputStream fos = new FileOutputStream(write_test);
/*      */         try
/*      */         {
/* 2273 */           fos.write(32);
/*      */         }
/*      */         finally
/*      */         {
/* 2277 */           fos.close();
/*      */         }
/*      */         
/* 2280 */         write_test.delete();
/*      */         
/*      */ 
/*      */ 
/* 2284 */         File rename_test = getApplicationFile("License.txt");
/*      */         
/* 2286 */         if (!rename_test.exists())
/*      */         {
/* 2288 */           rename_test = getApplicationFile("GPL.txt");
/*      */         }
/*      */         
/* 2291 */         if (!rename_test.exists())
/*      */         {
/* 2293 */           File[] files = write_test.getParentFile().listFiles();
/*      */           
/* 2295 */           if (files != null)
/*      */           {
/* 2297 */             for (File f : files)
/*      */             {
/* 2299 */               String name = f.getName();
/*      */               
/* 2301 */               if ((name.endsWith(".txt")) || (name.endsWith(".log")))
/*      */               {
/* 2303 */                 rename_test = f;
/*      */                 
/* 2305 */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 2311 */         if (rename_test.exists())
/*      */         {
/* 2313 */           File target = new File(rename_test.getParentFile(), rename_test.getName() + ".bak");
/*      */           
/* 2315 */           target.delete();
/*      */           
/* 2317 */           rename_test.renameTo(target);
/*      */           
/* 2319 */           if (rename_test.exists())
/*      */           {
/* 2321 */             return false;
/*      */           }
/*      */           
/* 2324 */           target.renameTo(rename_test);
/*      */         }
/*      */         else
/*      */         {
/* 2328 */           Debug.out("Failed to find a suitable file for the rename test");
/*      */           
/*      */ 
/*      */ 
/* 2332 */           return false;
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 2336 */         return false;
/*      */       }
/*      */     }
/*      */     
/* 2340 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean canWriteToDirectory(File dir)
/*      */   {
/* 2350 */     if (!dir.isDirectory())
/*      */     {
/* 2352 */       return false;
/*      */     }
/*      */     try
/*      */     {
/* 2356 */       File temp = AETemporaryFileHandler.createTempFileInDir(dir);
/*      */       
/* 2358 */       if (!temp.delete())
/*      */       {
/* 2360 */         temp.deleteOnExit();
/*      */       }
/*      */       
/* 2363 */       return true;
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 2367 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getScriptCharsetEncoding()
/*      */   {
/* 2384 */     synchronized (FileUtil.class)
/*      */     {
/* 2386 */       if (sce_checked)
/*      */       {
/* 2388 */         return script_encoding;
/*      */       }
/*      */       
/* 2391 */       sce_checked = true;
/*      */       
/* 2393 */       String file_encoding = System.getProperty("file.encoding", null);
/* 2394 */       String jvm_encoding = System.getProperty("sun.jnu.encoding", null);
/*      */       
/* 2396 */       if ((file_encoding == null) || (jvm_encoding == null) || (file_encoding.equals(jvm_encoding)))
/*      */       {
/* 2398 */         return null;
/*      */       }
/*      */       
/*      */       try
/*      */       {
/* 2403 */         String test_str = SystemProperties.getUserPath();
/*      */         
/* 2405 */         if (!new String(test_str.getBytes(file_encoding), file_encoding).equals(test_str))
/*      */         {
/* 2407 */           if (new String(test_str.getBytes(jvm_encoding), jvm_encoding).equals(test_str))
/*      */           {
/* 2409 */             Debug.out("Script encoding determined to be " + jvm_encoding + " instead of " + file_encoding);
/*      */             
/* 2411 */             script_encoding = jvm_encoding;
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/* 2417 */       return script_encoding;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static InternedFile internFileComponents(File file)
/*      */   {
/* 2425 */     if (file == null)
/*      */     {
/* 2427 */       return null;
/*      */     }
/*      */     
/* 2430 */     List<String> comps = new ArrayList(100);
/*      */     
/* 2432 */     File comp = file;
/*      */     
/* 2434 */     while (comp != null)
/*      */     {
/* 2436 */       String name = comp.getName();
/*      */       
/* 2438 */       if (name.length() > 0)
/*      */       {
/* 2440 */         comps.add(StringInterner.intern(name));
/*      */       }
/*      */       else
/*      */       {
/* 2444 */         String path = comp.getPath();
/*      */         
/* 2446 */         if (path.length() > 0)
/*      */         {
/* 2448 */           comps.add(StringInterner.intern(path));
/*      */         }
/*      */       }
/*      */       
/* 2452 */       comp = comp.getParentFile();
/*      */     }
/*      */     
/* 2455 */     InternedFile res = new InternedFile((String[])comps.toArray(new String[comps.size()]), null);
/*      */     
/* 2457 */     if (!res.getFile().equals(file))
/*      */     {
/* 2459 */       Debug.out("intern failed for " + file + " (" + res.getFile() + ")");
/*      */     }
/*      */     
/* 2462 */     return res;
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   private static boolean writeResilientFile(File parent_dir, String file_name, Map data)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: getstatic 953	org/gudy/azureus2/core3/util/FileUtil:class_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   3: invokevirtual 1071	org/gudy/azureus2/core3/util/AEMonitor:enter	()V
/*      */     //   6: invokestatic 1084	org/gudy/azureus2/core3/util/FileUtil:getReservedFileHandles	()V
/*      */     //   9: new 587	java/io/File
/*      */     //   12: dup
/*      */     //   13: aload_0
/*      */     //   14: new 604	java/lang/StringBuilder
/*      */     //   17: dup
/*      */     //   18: invokespecial 1041	java/lang/StringBuilder:<init>	()V
/*      */     //   21: aload_1
/*      */     //   22: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   25: ldc 35
/*      */     //   27: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   30: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   33: invokespecial 990	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
/*      */     //   36: astore_3
/*      */     //   37: aconst_null
/*      */     //   38: astore 4
/*      */     //   40: aload_2
/*      */     //   41: invokestatic 1078	org/gudy/azureus2/core3/util/BEncoder:encode	(Ljava/util/Map;)[B
/*      */     //   44: astore 5
/*      */     //   46: new 592	java/io/FileOutputStream
/*      */     //   49: dup
/*      */     //   50: aload_3
/*      */     //   51: iconst_0
/*      */     //   52: invokespecial 1003	java/io/FileOutputStream:<init>	(Ljava/io/File;Z)V
/*      */     //   55: astore 6
/*      */     //   57: new 585	java/io/BufferedOutputStream
/*      */     //   60: dup
/*      */     //   61: aload 6
/*      */     //   63: sipush 8192
/*      */     //   66: invokespecial 961	java/io/BufferedOutputStream:<init>	(Ljava/io/OutputStream;I)V
/*      */     //   69: astore 4
/*      */     //   71: aload 4
/*      */     //   73: aload 5
/*      */     //   75: invokevirtual 960	java/io/BufferedOutputStream:write	([B)V
/*      */     //   78: aload 4
/*      */     //   80: invokevirtual 959	java/io/BufferedOutputStream:flush	()V
/*      */     //   83: invokestatic 1079	org/gudy/azureus2/core3/util/Constants:isCVSVersion	()Z
/*      */     //   86: ifne +11 -> 97
/*      */     //   89: aload 6
/*      */     //   91: invokevirtual 1004	java/io/FileOutputStream:getFD	()Ljava/io/FileDescriptor;
/*      */     //   94: invokevirtual 992	java/io/FileDescriptor:sync	()V
/*      */     //   97: aload 4
/*      */     //   99: invokevirtual 958	java/io/BufferedOutputStream:close	()V
/*      */     //   102: aconst_null
/*      */     //   103: astore 4
/*      */     //   105: aload_3
/*      */     //   106: invokevirtual 966	java/io/File:length	()J
/*      */     //   109: lconst_1
/*      */     //   110: lcmp
/*      */     //   111: ifle +315 -> 426
/*      */     //   114: new 587	java/io/File
/*      */     //   117: dup
/*      */     //   118: aload_0
/*      */     //   119: aload_1
/*      */     //   120: invokespecial 990	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
/*      */     //   123: astore 7
/*      */     //   125: aload 7
/*      */     //   127: invokevirtual 971	java/io/File:exists	()Z
/*      */     //   130: ifeq +46 -> 176
/*      */     //   133: aload 7
/*      */     //   135: invokevirtual 970	java/io/File:delete	()Z
/*      */     //   138: ifne +38 -> 176
/*      */     //   141: new 604	java/lang/StringBuilder
/*      */     //   144: dup
/*      */     //   145: invokespecial 1041	java/lang/StringBuilder:<init>	()V
/*      */     //   148: ldc 59
/*      */     //   150: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   153: aload_1
/*      */     //   154: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   157: ldc 19
/*      */     //   159: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   162: aload 7
/*      */     //   164: invokevirtual 981	java/io/File:getAbsolutePath	()Ljava/lang/String;
/*      */     //   167: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   170: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   173: invokestatic 1080	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;)V
/*      */     //   176: aload 7
/*      */     //   178: invokevirtual 971	java/io/File:exists	()Z
/*      */     //   181: ifeq +26 -> 207
/*      */     //   184: new 604	java/lang/StringBuilder
/*      */     //   187: dup
/*      */     //   188: invokespecial 1041	java/lang/StringBuilder:<init>	()V
/*      */     //   191: aload 7
/*      */     //   193: invokevirtual 1047	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*      */     //   196: ldc 8
/*      */     //   198: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   201: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   204: invokestatic 1080	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;)V
/*      */     //   207: aload_3
/*      */     //   208: aload 7
/*      */     //   210: invokevirtual 979	java/io/File:renameTo	(Ljava/io/File;)Z
/*      */     //   213: ifeq +77 -> 290
/*      */     //   216: iconst_1
/*      */     //   217: istore 8
/*      */     //   219: aload 4
/*      */     //   221: ifnull +8 -> 229
/*      */     //   224: aload 4
/*      */     //   226: invokevirtual 958	java/io/BufferedOutputStream:close	()V
/*      */     //   229: goto +49 -> 278
/*      */     //   232: astore 9
/*      */     //   234: new 604	java/lang/StringBuilder
/*      */     //   237: dup
/*      */     //   238: invokespecial 1041	java/lang/StringBuilder:<init>	()V
/*      */     //   241: ldc 59
/*      */     //   243: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   246: aload_1
/*      */     //   247: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   250: ldc 18
/*      */     //   252: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   255: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   258: aload 9
/*      */     //   260: invokestatic 1083	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   263: iconst_0
/*      */     //   264: istore 10
/*      */     //   266: invokestatic 1085	org/gudy/azureus2/core3/util/FileUtil:releaseReservedFileHandles	()V
/*      */     //   269: getstatic 953	org/gudy/azureus2/core3/util/FileUtil:class_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   272: invokevirtual 1072	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   275: iload 10
/*      */     //   277: ireturn
/*      */     //   278: invokestatic 1085	org/gudy/azureus2/core3/util/FileUtil:releaseReservedFileHandles	()V
/*      */     //   281: getstatic 953	org/gudy/azureus2/core3/util/FileUtil:class_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   284: invokevirtual 1072	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   287: iload 8
/*      */     //   289: ireturn
/*      */     //   290: ldc2_w 554
/*      */     //   293: invokestatic 1051	java/lang/Thread:sleep	(J)V
/*      */     //   296: aload_3
/*      */     //   297: aload 7
/*      */     //   299: invokevirtual 979	java/io/File:renameTo	(Ljava/io/File;)Z
/*      */     //   302: ifeq +77 -> 379
/*      */     //   305: iconst_1
/*      */     //   306: istore 8
/*      */     //   308: aload 4
/*      */     //   310: ifnull +8 -> 318
/*      */     //   313: aload 4
/*      */     //   315: invokevirtual 958	java/io/BufferedOutputStream:close	()V
/*      */     //   318: goto +49 -> 367
/*      */     //   321: astore 9
/*      */     //   323: new 604	java/lang/StringBuilder
/*      */     //   326: dup
/*      */     //   327: invokespecial 1041	java/lang/StringBuilder:<init>	()V
/*      */     //   330: ldc 59
/*      */     //   332: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   335: aload_1
/*      */     //   336: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   339: ldc 18
/*      */     //   341: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   344: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   347: aload 9
/*      */     //   349: invokestatic 1083	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   352: iconst_0
/*      */     //   353: istore 10
/*      */     //   355: invokestatic 1085	org/gudy/azureus2/core3/util/FileUtil:releaseReservedFileHandles	()V
/*      */     //   358: getstatic 953	org/gudy/azureus2/core3/util/FileUtil:class_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   361: invokevirtual 1072	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   364: iload 10
/*      */     //   366: ireturn
/*      */     //   367: invokestatic 1085	org/gudy/azureus2/core3/util/FileUtil:releaseReservedFileHandles	()V
/*      */     //   370: getstatic 953	org/gudy/azureus2/core3/util/FileUtil:class_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   373: invokevirtual 1072	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   376: iload 8
/*      */     //   378: ireturn
/*      */     //   379: new 604	java/lang/StringBuilder
/*      */     //   382: dup
/*      */     //   383: invokespecial 1041	java/lang/StringBuilder:<init>	()V
/*      */     //   386: ldc 59
/*      */     //   388: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   391: aload_1
/*      */     //   392: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   395: ldc 20
/*      */     //   397: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   400: aload_3
/*      */     //   401: invokevirtual 981	java/io/File:getAbsolutePath	()Ljava/lang/String;
/*      */     //   404: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   407: ldc 9
/*      */     //   409: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   412: aload 7
/*      */     //   414: invokevirtual 981	java/io/File:getAbsolutePath	()Ljava/lang/String;
/*      */     //   417: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   420: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   423: invokestatic 1080	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;)V
/*      */     //   426: iconst_0
/*      */     //   427: istore 7
/*      */     //   429: aload 4
/*      */     //   431: ifnull +8 -> 439
/*      */     //   434: aload 4
/*      */     //   436: invokevirtual 958	java/io/BufferedOutputStream:close	()V
/*      */     //   439: goto +49 -> 488
/*      */     //   442: astore 8
/*      */     //   444: new 604	java/lang/StringBuilder
/*      */     //   447: dup
/*      */     //   448: invokespecial 1041	java/lang/StringBuilder:<init>	()V
/*      */     //   451: ldc 59
/*      */     //   453: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   456: aload_1
/*      */     //   457: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   460: ldc 18
/*      */     //   462: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   465: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   468: aload 8
/*      */     //   470: invokestatic 1083	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   473: iconst_0
/*      */     //   474: istore 9
/*      */     //   476: invokestatic 1085	org/gudy/azureus2/core3/util/FileUtil:releaseReservedFileHandles	()V
/*      */     //   479: getstatic 953	org/gudy/azureus2/core3/util/FileUtil:class_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   482: invokevirtual 1072	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   485: iload 9
/*      */     //   487: ireturn
/*      */     //   488: invokestatic 1085	org/gudy/azureus2/core3/util/FileUtil:releaseReservedFileHandles	()V
/*      */     //   491: getstatic 953	org/gudy/azureus2/core3/util/FileUtil:class_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   494: invokevirtual 1072	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   497: iload 7
/*      */     //   499: ireturn
/*      */     //   500: astore 5
/*      */     //   502: new 604	java/lang/StringBuilder
/*      */     //   505: dup
/*      */     //   506: invokespecial 1041	java/lang/StringBuilder:<init>	()V
/*      */     //   509: ldc 59
/*      */     //   511: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   514: aload_1
/*      */     //   515: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   518: ldc 18
/*      */     //   520: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   523: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   526: aload 5
/*      */     //   528: invokestatic 1083	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   531: iconst_0
/*      */     //   532: istore 6
/*      */     //   534: aload 4
/*      */     //   536: ifnull +8 -> 544
/*      */     //   539: aload 4
/*      */     //   541: invokevirtual 958	java/io/BufferedOutputStream:close	()V
/*      */     //   544: goto +49 -> 593
/*      */     //   547: astore 7
/*      */     //   549: new 604	java/lang/StringBuilder
/*      */     //   552: dup
/*      */     //   553: invokespecial 1041	java/lang/StringBuilder:<init>	()V
/*      */     //   556: ldc 59
/*      */     //   558: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   561: aload_1
/*      */     //   562: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   565: ldc 18
/*      */     //   567: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   570: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   573: aload 7
/*      */     //   575: invokestatic 1083	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   578: iconst_0
/*      */     //   579: istore 8
/*      */     //   581: invokestatic 1085	org/gudy/azureus2/core3/util/FileUtil:releaseReservedFileHandles	()V
/*      */     //   584: getstatic 953	org/gudy/azureus2/core3/util/FileUtil:class_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   587: invokevirtual 1072	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   590: iload 8
/*      */     //   592: ireturn
/*      */     //   593: invokestatic 1085	org/gudy/azureus2/core3/util/FileUtil:releaseReservedFileHandles	()V
/*      */     //   596: getstatic 953	org/gudy/azureus2/core3/util/FileUtil:class_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   599: invokevirtual 1072	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   602: iload 6
/*      */     //   604: ireturn
/*      */     //   605: astore 11
/*      */     //   607: aload 4
/*      */     //   609: ifnull +8 -> 617
/*      */     //   612: aload 4
/*      */     //   614: invokevirtual 958	java/io/BufferedOutputStream:close	()V
/*      */     //   617: goto +49 -> 666
/*      */     //   620: astore 12
/*      */     //   622: new 604	java/lang/StringBuilder
/*      */     //   625: dup
/*      */     //   626: invokespecial 1041	java/lang/StringBuilder:<init>	()V
/*      */     //   629: ldc 59
/*      */     //   631: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   634: aload_1
/*      */     //   635: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   638: ldc 18
/*      */     //   640: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   643: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   646: aload 12
/*      */     //   648: invokestatic 1083	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   651: iconst_0
/*      */     //   652: istore 13
/*      */     //   654: invokestatic 1085	org/gudy/azureus2/core3/util/FileUtil:releaseReservedFileHandles	()V
/*      */     //   657: getstatic 953	org/gudy/azureus2/core3/util/FileUtil:class_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   660: invokevirtual 1072	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   663: iload 13
/*      */     //   665: ireturn
/*      */     //   666: aload 11
/*      */     //   668: athrow
/*      */     //   669: astore 14
/*      */     //   671: invokestatic 1085	org/gudy/azureus2/core3/util/FileUtil:releaseReservedFileHandles	()V
/*      */     //   674: aload 14
/*      */     //   676: athrow
/*      */     //   677: astore 15
/*      */     //   679: getstatic 953	org/gudy/azureus2/core3/util/FileUtil:class_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   682: invokevirtual 1072	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   685: aload 15
/*      */     //   687: athrow
/*      */     // Line number table:
/*      */     //   Java source line #560	-> byte code offset #0
/*      */     //   Java source line #563	-> byte code offset #6
/*      */     //   Java source line #564	-> byte code offset #9
/*      */     //   Java source line #565	-> byte code offset #37
/*      */     //   Java source line #568	-> byte code offset #40
/*      */     //   Java source line #569	-> byte code offset #46
/*      */     //   Java source line #570	-> byte code offset #57
/*      */     //   Java source line #571	-> byte code offset #71
/*      */     //   Java source line #572	-> byte code offset #78
/*      */     //   Java source line #576	-> byte code offset #83
/*      */     //   Java source line #578	-> byte code offset #89
/*      */     //   Java source line #581	-> byte code offset #97
/*      */     //   Java source line #582	-> byte code offset #102
/*      */     //   Java source line #586	-> byte code offset #105
/*      */     //   Java source line #588	-> byte code offset #114
/*      */     //   Java source line #590	-> byte code offset #125
/*      */     //   Java source line #592	-> byte code offset #133
/*      */     //   Java source line #594	-> byte code offset #141
/*      */     //   Java source line #598	-> byte code offset #176
/*      */     //   Java source line #599	-> byte code offset #184
/*      */     //   Java source line #602	-> byte code offset #207
/*      */     //   Java source line #604	-> byte code offset #216
/*      */     //   Java source line #629	-> byte code offset #219
/*      */     //   Java source line #631	-> byte code offset #224
/*      */     //   Java source line #638	-> byte code offset #229
/*      */     //   Java source line #633	-> byte code offset #232
/*      */     //   Java source line #635	-> byte code offset #234
/*      */     //   Java source line #637	-> byte code offset #263
/*      */     //   Java source line #642	-> byte code offset #266
/*      */     //   Java source line #646	-> byte code offset #269
/*      */     //   Java source line #642	-> byte code offset #278
/*      */     //   Java source line #646	-> byte code offset #281
/*      */     //   Java source line #609	-> byte code offset #290
/*      */     //   Java source line #610	-> byte code offset #296
/*      */     //   Java source line #612	-> byte code offset #305
/*      */     //   Java source line #629	-> byte code offset #308
/*      */     //   Java source line #631	-> byte code offset #313
/*      */     //   Java source line #638	-> byte code offset #318
/*      */     //   Java source line #633	-> byte code offset #321
/*      */     //   Java source line #635	-> byte code offset #323
/*      */     //   Java source line #637	-> byte code offset #352
/*      */     //   Java source line #642	-> byte code offset #355
/*      */     //   Java source line #646	-> byte code offset #358
/*      */     //   Java source line #642	-> byte code offset #367
/*      */     //   Java source line #646	-> byte code offset #370
/*      */     //   Java source line #615	-> byte code offset #379
/*      */     //   Java source line #618	-> byte code offset #426
/*      */     //   Java source line #629	-> byte code offset #429
/*      */     //   Java source line #631	-> byte code offset #434
/*      */     //   Java source line #638	-> byte code offset #439
/*      */     //   Java source line #633	-> byte code offset #442
/*      */     //   Java source line #635	-> byte code offset #444
/*      */     //   Java source line #637	-> byte code offset #473
/*      */     //   Java source line #642	-> byte code offset #476
/*      */     //   Java source line #646	-> byte code offset #479
/*      */     //   Java source line #642	-> byte code offset #488
/*      */     //   Java source line #646	-> byte code offset #491
/*      */     //   Java source line #620	-> byte code offset #500
/*      */     //   Java source line #622	-> byte code offset #502
/*      */     //   Java source line #624	-> byte code offset #531
/*      */     //   Java source line #629	-> byte code offset #534
/*      */     //   Java source line #631	-> byte code offset #539
/*      */     //   Java source line #638	-> byte code offset #544
/*      */     //   Java source line #633	-> byte code offset #547
/*      */     //   Java source line #635	-> byte code offset #549
/*      */     //   Java source line #637	-> byte code offset #578
/*      */     //   Java source line #642	-> byte code offset #581
/*      */     //   Java source line #646	-> byte code offset #584
/*      */     //   Java source line #642	-> byte code offset #593
/*      */     //   Java source line #646	-> byte code offset #596
/*      */     //   Java source line #628	-> byte code offset #605
/*      */     //   Java source line #629	-> byte code offset #607
/*      */     //   Java source line #631	-> byte code offset #612
/*      */     //   Java source line #638	-> byte code offset #617
/*      */     //   Java source line #633	-> byte code offset #620
/*      */     //   Java source line #635	-> byte code offset #622
/*      */     //   Java source line #637	-> byte code offset #651
/*      */     //   Java source line #642	-> byte code offset #654
/*      */     //   Java source line #646	-> byte code offset #657
/*      */     //   Java source line #642	-> byte code offset #669
/*      */     //   Java source line #646	-> byte code offset #677
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	688	0	parent_dir	File
/*      */     //   0	688	1	file_name	String
/*      */     //   0	688	2	data	Map
/*      */     //   36	365	3	temp	File
/*      */     //   38	575	4	baos	java.io.BufferedOutputStream
/*      */     //   44	30	5	encoded_data	byte[]
/*      */     //   500	27	5	e	Throwable
/*      */     //   55	548	6	tempOS	FileOutputStream
/*      */     //   123	375	7	file	File
/*      */     //   547	27	7	e	Exception
/*      */     //   217	160	8	bool1	boolean
/*      */     //   442	149	8	e	Exception
/*      */     //   579	12	8	bool2	boolean
/*      */     //   232	27	9	e	Exception
/*      */     //   321	165	9	e	Exception
/*      */     //   264	101	10	bool3	boolean
/*      */     //   605	62	11	localObject1	Object
/*      */     //   620	27	12	e	Exception
/*      */     //   652	12	13	bool4	boolean
/*      */     //   669	6	14	localObject2	Object
/*      */     //   677	9	15	localObject3	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   219	229	232	java/lang/Exception
/*      */     //   308	318	321	java/lang/Exception
/*      */     //   429	439	442	java/lang/Exception
/*      */     //   40	219	500	java/lang/Throwable
/*      */     //   290	308	500	java/lang/Throwable
/*      */     //   379	429	500	java/lang/Throwable
/*      */     //   534	544	547	java/lang/Exception
/*      */     //   40	219	605	finally
/*      */     //   290	308	605	finally
/*      */     //   379	429	605	finally
/*      */     //   500	534	605	finally
/*      */     //   605	607	605	finally
/*      */     //   607	617	620	java/lang/Exception
/*      */     //   6	266	669	finally
/*      */     //   290	355	669	finally
/*      */     //   379	476	669	finally
/*      */     //   500	581	669	finally
/*      */     //   605	654	669	finally
/*      */     //   666	671	669	finally
/*      */     //   0	269	677	finally
/*      */     //   278	281	677	finally
/*      */     //   290	358	677	finally
/*      */     //   367	370	677	finally
/*      */     //   379	479	677	finally
/*      */     //   488	491	677	finally
/*      */     //   500	584	677	finally
/*      */     //   593	596	677	finally
/*      */     //   605	657	677	finally
/*      */     //   666	679	677	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   private static Map readResilientFileSupport(File parent_dir, String file_name, boolean attempt_recovery, boolean intern_keys)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: getstatic 953	org/gudy/azureus2/core3/util/FileUtil:class_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   3: invokevirtual 1071	org/gudy/azureus2/core3/util/AEMonitor:enter	()V
/*      */     //   6: invokestatic 1084	org/gudy/azureus2/core3/util/FileUtil:getReservedFileHandles	()V
/*      */     //   9: aconst_null
/*      */     //   10: astore 4
/*      */     //   12: aload_1
/*      */     //   13: aload_0
/*      */     //   14: aload_1
/*      */     //   15: iconst_0
/*      */     //   16: iconst_0
/*      */     //   17: iload_3
/*      */     //   18: invokestatic 1112	org/gudy/azureus2/core3/util/FileUtil:readResilientFile	(Ljava/lang/String;Ljava/io/File;Ljava/lang/String;IZZ)Ljava/util/Map;
/*      */     //   21: astore 4
/*      */     //   23: goto +5 -> 28
/*      */     //   26: astore 5
/*      */     //   28: aload 4
/*      */     //   30: ifnonnull +50 -> 80
/*      */     //   33: iload_2
/*      */     //   34: ifeq +46 -> 80
/*      */     //   37: aload_1
/*      */     //   38: aload_0
/*      */     //   39: aload_1
/*      */     //   40: iconst_0
/*      */     //   41: iconst_1
/*      */     //   42: iload_3
/*      */     //   43: invokestatic 1112	org/gudy/azureus2/core3/util/FileUtil:readResilientFile	(Ljava/lang/String;Ljava/io/File;Ljava/lang/String;IZZ)Ljava/util/Map;
/*      */     //   46: astore 4
/*      */     //   48: aload 4
/*      */     //   50: ifnull +30 -> 80
/*      */     //   53: new 604	java/lang/StringBuilder
/*      */     //   56: dup
/*      */     //   57: invokespecial 1041	java/lang/StringBuilder:<init>	()V
/*      */     //   60: ldc 49
/*      */     //   62: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   65: aload_1
/*      */     //   66: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   69: ldc 23
/*      */     //   71: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   74: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   77: invokestatic 1080	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;)V
/*      */     //   80: aload 4
/*      */     //   82: astore 5
/*      */     //   84: invokestatic 1085	org/gudy/azureus2/core3/util/FileUtil:releaseReservedFileHandles	()V
/*      */     //   87: getstatic 953	org/gudy/azureus2/core3/util/FileUtil:class_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   90: invokevirtual 1072	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   93: aload 5
/*      */     //   95: areturn
/*      */     //   96: astore 4
/*      */     //   98: aload 4
/*      */     //   100: invokestatic 1082	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*      */     //   103: aconst_null
/*      */     //   104: astore 5
/*      */     //   106: invokestatic 1085	org/gudy/azureus2/core3/util/FileUtil:releaseReservedFileHandles	()V
/*      */     //   109: getstatic 953	org/gudy/azureus2/core3/util/FileUtil:class_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   112: invokevirtual 1072	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   115: aload 5
/*      */     //   117: areturn
/*      */     //   118: astore 6
/*      */     //   120: invokestatic 1085	org/gudy/azureus2/core3/util/FileUtil:releaseReservedFileHandles	()V
/*      */     //   123: aload 6
/*      */     //   125: athrow
/*      */     //   126: astore 7
/*      */     //   128: getstatic 953	org/gudy/azureus2/core3/util/FileUtil:class_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   131: invokevirtual 1072	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   134: aload 7
/*      */     //   136: athrow
/*      */     // Line number table:
/*      */     //   Java source line #796	-> byte code offset #0
/*      */     //   Java source line #799	-> byte code offset #6
/*      */     //   Java source line #801	-> byte code offset #9
/*      */     //   Java source line #804	-> byte code offset #12
/*      */     //   Java source line #809	-> byte code offset #23
/*      */     //   Java source line #806	-> byte code offset #26
/*      */     //   Java source line #811	-> byte code offset #28
/*      */     //   Java source line #813	-> byte code offset #37
/*      */     //   Java source line #815	-> byte code offset #48
/*      */     //   Java source line #817	-> byte code offset #53
/*      */     //   Java source line #821	-> byte code offset #80
/*      */     //   Java source line #831	-> byte code offset #84
/*      */     //   Java source line #835	-> byte code offset #87
/*      */     //   Java source line #823	-> byte code offset #96
/*      */     //   Java source line #825	-> byte code offset #98
/*      */     //   Java source line #827	-> byte code offset #103
/*      */     //   Java source line #831	-> byte code offset #106
/*      */     //   Java source line #835	-> byte code offset #109
/*      */     //   Java source line #831	-> byte code offset #118
/*      */     //   Java source line #835	-> byte code offset #126
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	137	0	parent_dir	File
/*      */     //   0	137	1	file_name	String
/*      */     //   0	137	2	attempt_recovery	boolean
/*      */     //   0	137	3	intern_keys	boolean
/*      */     //   10	71	4	res	Map
/*      */     //   96	3	4	e	Throwable
/*      */     //   26	90	5	e	Throwable
/*      */     //   118	6	6	localObject1	Object
/*      */     //   126	9	7	localObject2	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	23	26	java/lang/Throwable
/*      */     //   6	84	96	java/lang/Throwable
/*      */     //   6	84	118	finally
/*      */     //   96	106	118	finally
/*      */     //   118	120	118	finally
/*      */     //   0	87	126	finally
/*      */     //   96	109	126	finally
/*      */     //   118	128	126	finally
/*      */   }
/*      */   
/*      */   public static class InternedFile
/*      */   {
/*      */     private final String[] comps;
/*      */     
/*      */     private InternedFile(String[] _comps)
/*      */     {
/* 2474 */       this.comps = _comps;
/*      */     }
/*      */     
/*      */ 
/*      */     public File getFile()
/*      */     {
/* 2480 */       if (this.comps.length == 0)
/*      */       {
/* 2482 */         return new File("");
/*      */       }
/* 2484 */       if (this.comps.length == 1)
/*      */       {
/* 2486 */         return new File(this.comps[0]);
/*      */       }
/*      */       
/*      */ 
/* 2490 */       StringBuilder b = new StringBuilder(256);
/*      */       
/* 2492 */       for (int i = this.comps.length - 1; i >= 0; i--)
/*      */       {
/* 2494 */         if (b.length() > 0)
/*      */         {
/* 2496 */           b.append(File.separatorChar);
/*      */         }
/*      */         
/* 2499 */         b.append(this.comps[i]);
/*      */       }
/*      */       
/* 2502 */       return new File(b.toString());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean equals(Object other)
/*      */     {
/* 2511 */       if ((other instanceof InternedFile))
/*      */       {
/* 2513 */         InternedFile o = (InternedFile)other;
/*      */         
/* 2515 */         if (this.comps.length != o.comps.length)
/*      */         {
/* 2517 */           return false;
/*      */         }
/*      */         
/* 2520 */         for (int i = this.comps.length - 1; i >= 0; i--)
/*      */         {
/* 2522 */           if (!this.comps[i].equals(o.comps[i]))
/*      */           {
/* 2524 */             return false;
/*      */           }
/*      */         }
/*      */         
/* 2528 */         return true;
/*      */       }
/* 2530 */       if ((other instanceof File))
/*      */       {
/* 2532 */         return getFile().equals(other);
/*      */       }
/*      */       
/*      */ 
/* 2536 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 2544 */       int h = 0;
/*      */       
/* 2546 */       for (String s : this.comps)
/*      */       {
/* 2548 */         h += s.hashCode();
/*      */       }
/*      */       
/* 2551 */       return h;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/FileUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */