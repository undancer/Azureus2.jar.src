/*     */ package org.gudy.azureus2.core3.config;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.LineNumberReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.URL;
/*     */ import java.security.AccessControlException;
/*     */ import java.security.Security;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationDefaults;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.core3.util.protocol.AzURLStreamHandlerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class COConfigurationManager
/*     */ {
/*     */   public static final int CONFIG_DEFAULT_MIN_MAX_UPLOAD_SPEED = 5;
/*     */   public static final int CONFIG_DEFAULT_MAX_DOWNLOAD_SPEED = 0;
/*     */   public static final int CONFIG_DEFAULT_MAX_CONNECTIONS_PER_TORRENT = 50;
/*     */   public static final int CONFIG_DEFAULT_MAX_CONNECTIONS_GLOBAL = 250;
/*     */   public static final int CONFIG_CACHE_SIZE_MAX_MB;
/*     */   public static final boolean ENABLE_MULTIPLE_UDP_PORTS = false;
/*     */   private static boolean pre_initialised;
/*     */   
/*     */   static
/*     */   {
/*  57 */     long max_mem_bytes = Runtime.getRuntime().maxMemory();
/*  58 */     long mb_1 = 1048576L;
/*  59 */     long mb_32 = 32L * mb_1;
/*  60 */     int size = (int)((max_mem_bytes - mb_32) / mb_1);
/*  61 */     if (size > 2000) size = 2000;
/*  62 */     if (size < 1) size = 1;
/*  63 */     CONFIG_CACHE_SIZE_MAX_MB = size;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized void preInitialise()
/*     */   {
/*  73 */     if (!pre_initialised)
/*     */     {
/*  75 */       pre_initialised = true;
/*     */       try
/*     */       {
/*  78 */         if (System.getProperty("azureus.portable.enable", "false").equalsIgnoreCase("true")) {
/*     */           try
/*     */           {
/*  81 */             if (File.separatorChar != '\\')
/*     */             {
/*  83 */               throw new Exception("Portable only supported on Windows");
/*     */             }
/*     */             
/*     */             File portable_root;
/*     */             try
/*     */             {
/*  89 */               portable_root = new File(".").getCanonicalFile();
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/*  93 */               portable_root = new File(".").getAbsoluteFile();
/*     */             }
/*     */             
/*  96 */             if (!portable_root.canWrite())
/*     */             {
/*  98 */               throw new Exception("can't write to " + portable_root);
/*     */             }
/*     */             
/* 101 */             File root_file = new File(portable_root, "portable.dat");
/*     */             
/* 103 */             String str = portable_root.getAbsolutePath();
/*     */             
/* 105 */             if ((str.length() < 2) || (str.charAt(1) != ':'))
/*     */             {
/* 107 */               throw new Exception("drive letter missing in '" + str + "'");
/*     */             }
/*     */             
/* 110 */             String root_relative = str.substring(2);
/*     */             
/* 112 */             boolean write_file = true;
/*     */             
/* 114 */             if (root_file.exists())
/*     */             {
/* 116 */               LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(root_file), "UTF-8"));
/*     */               try
/*     */               {
/* 119 */                 String line = lnr.readLine();
/*     */                 
/* 121 */                 if (line != null)
/*     */                 {
/* 123 */                   line = line.trim();
/*     */                   
/* 125 */                   if (line.equalsIgnoreCase(root_relative))
/*     */                   {
/* 127 */                     write_file = false;
/*     */                   }
/*     */                   else
/*     */                   {
/* 131 */                     throw new Exception("root changed - old='" + line + "', new='" + root_relative);
/*     */                   }
/*     */                 }
/*     */               }
/*     */               finally {
/* 136 */                 lnr.close();
/*     */               }
/*     */             }
/*     */             
/* 140 */             if (write_file)
/*     */             {
/* 142 */               PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(root_file), "UTF-8"));
/*     */               try
/*     */               {
/* 145 */                 pw.println(root_relative);
/*     */               }
/*     */               finally
/*     */               {
/* 149 */                 pw.close();
/*     */               }
/*     */             }
/*     */             
/* 153 */             System.setProperty("azureus.install.path", str);
/* 154 */             System.setProperty("azureus.config.path", str);
/*     */             
/* 156 */             System.setProperty("azureus.portable.root", str);
/*     */             
/* 158 */             System.out.println("Portable setup OK - root=" + root_relative + " (current=" + str + ")");
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 162 */             System.err.println("Portable setup failed: " + e.getMessage());
/*     */             
/* 164 */             System.setProperty("azureus.portable.enable", "false");
/*     */             
/* 166 */             System.setProperty("azureus.portable.root", "");
/*     */           }
/*     */           
/*     */         } else {
/* 170 */           System.setProperty("azureus.portable.root", "");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         try
/*     */         {
/* 201 */           Security.setProperty("crypto.policy", "unlimited");
/*     */         }
/*     */         catch (Throwable e) {}
/*     */         
/*     */ 
/* 206 */         System.setProperty("sun.net.maxDatagramSockets", "4096");
/*     */         
/* 208 */         URL.setURLStreamHandlerFactory(new AzURLStreamHandlerFactory());
/*     */         
/*     */ 
/*     */ 
/* 212 */         System.setProperty("sun.net.inetaddr.ttl", "60");
/* 213 */         System.setProperty("networkaddress.cache.ttl", "60");
/* 214 */         System.setProperty("sun.net.inetaddr.negative.ttl", "300");
/* 215 */         System.setProperty("networkaddress.cache.negative.ttl", "300");
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 225 */         System.setProperty("sun.net.client.defaultConnectTimeout", "120000");
/* 226 */         System.setProperty("sun.net.client.defaultReadTimeout", "60000");
/*     */         
/*     */ 
/*     */ 
/* 230 */         System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 235 */         if (Constants.isOSX)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 241 */           System.setProperty("java.nio.preferSelect", "true");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 248 */         System.setProperty("sun.net.spi.nameservice.provider.1", "dns,aednsproxy");
/*     */         
/*     */ 
/*     */ 
/* 252 */         SystemProperties.determineApplicationName();
/*     */ 
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*     */ 
/* 258 */         if (!(e instanceof AccessControlException))
/*     */         {
/*     */ 
/*     */ 
/* 262 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static ConfigurationManager initialise()
/*     */   {
/* 271 */     preInitialise();
/*     */     
/* 273 */     return ConfigurationManager.getInstance();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static ConfigurationManager initialiseFromMap(Map data)
/*     */   {
/* 280 */     preInitialise();
/*     */     
/* 282 */     return ConfigurationManager.getInstance(data);
/*     */   }
/*     */   
/*     */ 
/*     */   public static final boolean isNewInstall()
/*     */   {
/* 288 */     return ConfigurationManager.getInstance().isNewInstall();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getStringParameter(String _name)
/*     */   {
/* 295 */     return ConfigurationManager.getInstance().getStringParameter(_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getStringParameter(String _name, String _default)
/*     */   {
/* 303 */     return ConfigurationManager.getInstance().getStringParameter(_name, _default);
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean setParameter(String parameter, String value)
/*     */   {
/* 309 */     return ConfigurationManager.getInstance().setParameter(parameter, value);
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean verifyParameter(String parameter, String value)
/*     */   {
/* 315 */     return ConfigurationManager.getInstance().verifyParameter(parameter, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean getBooleanParameter(String _name)
/*     */   {
/* 322 */     return ConfigurationManager.getInstance().getBooleanParameter(_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static boolean getBooleanParameter(String _name, boolean _default)
/*     */   {
/* 333 */     return ConfigurationManager.getInstance().getBooleanParameter(_name, _default);
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean setParameter(String parameter, boolean value)
/*     */   {
/* 339 */     return ConfigurationManager.getInstance().setParameter(parameter, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static int getIntParameter(String _name)
/*     */   {
/* 346 */     return ConfigurationManager.getInstance().getIntParameter(_name);
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
/*     */   public static int getIntParameter(String _name, int _default)
/*     */   {
/* 363 */     return ConfigurationManager.getInstance().getIntParameter(_name, _default);
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean setParameter(String parameter, int value)
/*     */   {
/* 369 */     return ConfigurationManager.getInstance().setParameter(parameter, value);
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean setParameter(String parameter, long value)
/*     */   {
/* 375 */     return ConfigurationManager.getInstance().setParameter(parameter, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static long getLongParameter(String _name)
/*     */   {
/* 382 */     return ConfigurationManager.getInstance().getLongParameter(_name);
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
/*     */   public static long getLongParameter(String _name, long _def)
/*     */   {
/* 398 */     return ConfigurationManager.getInstance().getLongParameter(_name, _def);
/*     */   }
/*     */   
/*     */   public static byte[] getByteParameter(String _name) {
/* 402 */     return ConfigurationManager.getInstance().getByteParameter(_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] getByteParameter(String _name, byte[] _default)
/*     */   {
/* 410 */     return ConfigurationManager.getInstance().getByteParameter(_name, _default);
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean setParameter(String parameter, byte[] value)
/*     */   {
/* 416 */     return ConfigurationManager.getInstance().setParameter(parameter, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getDirectoryParameter(String _name)
/*     */     throws IOException
/*     */   {
/* 424 */     return ConfigurationManager.getInstance().getDirectoryParameter(_name);
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
/*     */   public static boolean setRGBParameter(String parameter, int red, int green, int blue)
/*     */   {
/* 446 */     return ConfigurationManager.getInstance().setRGBParameter(parameter, red, green, blue);
/*     */   }
/*     */   
/*     */   public static boolean setRGBParameter(String parameter, int[] rgb, boolean override) {
/* 450 */     return ConfigurationManager.getInstance().setRGBParameter(parameter, rgb, override);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static float getFloatParameter(String _name)
/*     */   {
/* 457 */     return ConfigurationManager.getInstance().getFloatParameter(_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static float getFloatParameter(String _name, float _def)
/*     */   {
/* 465 */     return ConfigurationManager.getInstance().getFloatParameter(_name, _def);
/*     */   }
/*     */   
/*     */   public static boolean setParameter(String parameter, float value)
/*     */   {
/* 470 */     return ConfigurationManager.getInstance().setParameter(parameter, value);
/*     */   }
/*     */   
/*     */   public static boolean setParameter(String parameter, StringList value)
/*     */   {
/* 475 */     return ConfigurationManager.getInstance().setParameter(parameter, value);
/*     */   }
/*     */   
/*     */ 
/*     */   public static StringList getStringListParameter(String parameter)
/*     */   {
/* 481 */     return ConfigurationManager.getInstance().getStringListParameter(parameter);
/*     */   }
/*     */   
/*     */   public static boolean setParameter(String parameter, List value)
/*     */   {
/* 486 */     return ConfigurationManager.getInstance().setParameter(parameter, value);
/*     */   }
/*     */   
/*     */ 
/*     */   public static List getListParameter(String parameter, List def)
/*     */   {
/* 492 */     return ConfigurationManager.getInstance().getListParameter(parameter, def);
/*     */   }
/*     */   
/*     */   public static boolean setParameter(String parameter, Map value)
/*     */   {
/* 497 */     return ConfigurationManager.getInstance().setParameter(parameter, value);
/*     */   }
/*     */   
/*     */ 
/*     */   public static Map getMapParameter(String parameter, Map def)
/*     */   {
/* 503 */     return ConfigurationManager.getInstance().getMapParameter(parameter, def);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean hasParameter(String parameter, boolean explicit)
/*     */   {
/* 514 */     return ConfigurationManager.getInstance().hasParameter(parameter, explicit);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void save()
/*     */   {
/* 520 */     ConfigurationManager.getInstance().save();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setDirty()
/*     */   {
/* 531 */     ConfigurationManager.getInstance().setDirty();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addListener(COConfigurationListener listener)
/*     */   {
/* 538 */     ConfigurationManager.getInstance().addListener(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addAndFireListener(COConfigurationListener listener)
/*     */   {
/* 545 */     ConfigurationManager.getInstance().addAndFireListener(listener);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void addParameterListener(String parameter, ParameterListener listener)
/*     */   {
/* 551 */     ConfigurationManager.getInstance().addParameterListener(parameter, listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addParameterListener(String[] ids, ParameterListener listener)
/*     */   {
/* 562 */     ConfigurationManager instance = ConfigurationManager.getInstance();
/* 563 */     for (int i = 0; i < ids.length; i++) {
/* 564 */       instance.addParameterListener(ids[i], listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void addAndFireParameterListener(String parameter, ParameterListener listener)
/*     */   {
/* 571 */     ConfigurationManager.getInstance().addParameterListener(parameter, listener);
/*     */     
/* 573 */     listener.parameterChanged(parameter);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void addAndFireParameterListeners(String[] parameters, ParameterListener listener)
/*     */   {
/* 579 */     for (int i = 0; i < parameters.length; i++) {
/* 580 */       ConfigurationManager.getInstance().addParameterListener(parameters[i], listener);
/*     */     }
/*     */     
/* 583 */     listener.parameterChanged(null);
/*     */   }
/*     */   
/*     */   public static void removeParameterListener(String parameter, ParameterListener listener)
/*     */   {
/* 588 */     ConfigurationManager.getInstance().removeParameterListener(parameter, listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void removeListener(COConfigurationListener listener)
/*     */   {
/* 595 */     ConfigurationManager.getInstance().removeListener(listener);
/*     */   }
/*     */   
/*     */ 
/*     */   public static Set<String> getAllowedParameters()
/*     */   {
/* 601 */     return ConfigurationDefaults.getInstance().getAllowedParameters();
/*     */   }
/*     */   
/*     */ 
/*     */   public static Set<String> getDefinedParameters()
/*     */   {
/* 607 */     return ConfigurationManager.getInstance().getDefinedParameters();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Object getParameter(String name)
/*     */   {
/* 619 */     return ConfigurationManager.getInstance().getParameter(name);
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
/*     */   public static boolean doesParameterDefaultExist(String parameter)
/*     */   {
/* 632 */     return ConfigurationDefaults.getInstance().doesParameterDefaultExist(parameter);
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
/*     */   public static boolean doesParameterNonDefaultExist(String parameter)
/*     */   {
/* 645 */     return ConfigurationManager.getInstance().doesParameterNonDefaultExist(parameter);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void registerExternalDefaults(Map addmap)
/*     */   {
/* 651 */     ConfigurationDefaults.getInstance().registerExternalDefaults(addmap);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setBooleanDefault(String parameter, boolean _default)
/*     */   {
/* 659 */     ConfigurationDefaults.getInstance().addParameter(parameter, _default);
/*     */   }
/*     */   
/*     */   public static void setFloatDefault(String parameter, float _default) {
/* 663 */     ConfigurationDefaults.getInstance().addParameter(parameter, _default);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setIntDefault(String parameter, int _default)
/*     */   {
/* 671 */     ConfigurationDefaults.getInstance().addParameter(parameter, _default);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setLongDefault(String parameter, long _default)
/*     */   {
/* 679 */     ConfigurationDefaults.getInstance().addParameter(parameter, _default);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setStringDefault(String parameter, String _default)
/*     */   {
/* 687 */     ConfigurationDefaults.getInstance().addParameter(parameter, _default);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setByteDefault(String parameter, byte[] _default)
/*     */   {
/* 695 */     ConfigurationDefaults.getInstance().addParameter(parameter, _default);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Object getDefault(String parameter)
/*     */   {
/* 702 */     return ConfigurationDefaults.getInstance().getParameter(parameter);
/*     */   }
/*     */   
/*     */   public static boolean removeParameter(String parameter) {
/* 706 */     return ConfigurationManager.getInstance().removeParameter(parameter);
/*     */   }
/*     */   
/*     */   public static boolean removeRGBParameter(String parameter) {
/* 710 */     return ConfigurationManager.getInstance().removeRGBParameter(parameter);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void registerExportedParameter(String name, String key)
/*     */   {
/* 718 */     ConfigurationManager.getInstance().registerExportedParameter(name, key);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void resetToDefaults()
/*     */   {
/* 724 */     ConfigurationManager.getInstance().resetToDefaults();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addResetToDefaultsListener(ResetToDefaultsListener l)
/*     */   {
/* 731 */     ConfigurationManager.getInstance().addResetToDefaultsListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void dumpConfigChanges(IndentWriter writer)
/*     */   {
/* 738 */     ConfigurationManager.getInstance().dumpConfigChanges(writer);
/*     */   }
/*     */   
/*     */   public static abstract interface ParameterVerifier
/*     */   {
/*     */     public abstract boolean verify(String paramString, Object paramObject);
/*     */   }
/*     */   
/*     */   public static abstract interface ResetToDefaultsListener
/*     */   {
/*     */     public abstract void reset();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/config/COConfigurationManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */