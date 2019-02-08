/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.LineNumberReader;
/*     */ import java.io.PrintStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.security.AccessControlException;
/*     */ import java.util.Locale;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.TimeZone;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Constants
/*     */ {
/*     */   public static final String EMPTY_STRING = "";
/*     */   public static final String SF_WEB_SITE = "http://plugins.vuze.com/";
/*     */   public static final String AELITIS_TORRENTS = "http://cf1.vuze.com/torrent/torrents/";
/*     */   public static final String AELITIS_FILES = "http://cf1.vuze.com/torrent/files/";
/*     */   public static final String AZUREUS_WIKI = "http://wiki.vuze.com/w/";
/*     */   public static final String VERSION_SERVER_V4 = "version.vuze.com";
/*     */   public static final String VERSION_SERVER_V6 = "version6.vuze.com";
/*     */   public static final String DHT_SEED_ADDRESS_V4 = "dht.vuze.com";
/*     */   public static final String DHT_SEED_ADDRESS_V6 = "dht6.vuze.com";
/*     */   public static final String DHT_SEED_ADDRESS_V6_TUNNEL = "dht6tunnel.vuze.com";
/*     */   public static final String NAT_TEST_SERVER = "nettest.vuze.com";
/*     */   public static final String NAT_TEST_SERVER_HTTP = "http://nettest.vuze.com/";
/*     */   public static final String SPEED_TEST_SERVER = "speedtest.vuze.com";
/*     */   public static final String PAIRING_URL = "https://pair.vuze.com/pairing";
/*  60 */   public static final String[] AZUREUS_DOMAINS = { "azureusplatform.com", "azureus.com", "aelitis.com", "vuze.com" };
/*     */   public static final String DEFAULT_ENCODING = "UTF8";
/*     */   public static final String BYTE_ENCODING = "ISO-8859-1";
/*     */   public static final Charset BYTE_CHARSET;
/*     */   public static final Charset DEFAULT_CHARSET;
/*     */   public static final int DEFAULT_INSTANCE_PORT = 6880;
/*     */   public static final int INSTANCE_PORT;
/*     */   public static final Locale LOCALE_ENGLISH;
/*     */   public static final String INFINITY_STRING = "∞";
/*     */   public static final int CRAPPY_INFINITY_AS_INT = 31536000;
/*     */   public static final long CRAPPY_INFINITE_AS_LONG = 1827387392L; public static boolean DOWNLOAD_SOURCES_PRETEND_COMPLETE; public static final String APP_NAME; public static final String APP_PLUS_NAME; public static final String AZUREUS_NAME = "Azureus"; public static final String AZUREUS_PROTOCOL_NAME_PRE_4813 = "Azureus"; public static final String AZUREUS_PROTOCOL_NAME = "Vuze"; public static final String AZUREUS_VERSION = "5.7.6.0"; public static final String BUILD_VERSION = "@build.version@"; public static final String AZUREUS_SUBVER = ""; public static final byte[] VERSION_ID; private static final boolean FORCE_NON_CVS; public static final boolean IS_CVS_VERSION; public static final String OSName; public static final boolean isOSX; public static final boolean isLinux; public static final boolean isSolaris; public static final boolean isFreeBSD; public static final boolean isWindowsXP; public static final boolean isWindows95; public static final boolean isWindows98; public static final boolean isWindows2000; public static final boolean isWindowsME; public static final boolean isWindows9598ME; public static boolean isSafeMode; public static final boolean isWindows; public static final boolean isUnix; public static final boolean isWindowsVista; public static final boolean isWindowsVistaSP2OrHigher; public static final boolean isWindowsVistaOrHigher; public static final boolean isWindows7OrHigher; public static final boolean isWindows8OrHigher; public static final Pattern PAT_SPLIT_COMMAWORDS; public static final Pattern PAT_SPLIT_COMMA; public static final Pattern PAT_SPLIT_DOT;
/*  71 */   static { String ip_str = System.getProperty("azureus.instance.port", String.valueOf(6880));
/*     */     int ip;
/*     */     try
/*     */     {
/*  75 */       ip = Integer.parseInt(ip_str);
/*     */     } catch (Throwable e) {
/*  77 */       ip = 6880;
/*     */     }
/*     */     
/*  80 */     INSTANCE_PORT = ip;
/*     */     
/*  82 */     Charset bc = null;
/*  83 */     Charset dc = null;
/*     */     try
/*     */     {
/*  86 */       bc = Charset.forName("ISO-8859-1");
/*  87 */       dc = Charset.forName("UTF8");
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  91 */       e.printStackTrace();
/*     */     }
/*     */     
/*  94 */     BYTE_CHARSET = bc;
/*  95 */     DEFAULT_CHARSET = dc;
/*     */     
/*     */ 
/*  98 */     LOCALE_ENGLISH = new Locale("en", "");
/*     */     
/*     */     try
/*     */     {
/* 102 */       String timezone = System.getProperty("azureus.timezone", null);
/*     */       
/* 104 */       if (timezone != null)
/*     */       {
/* 106 */         TimeZone.setDefault(TimeZone.getTimeZone(timezone));
/*     */       }
/*     */       
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 112 */       if (!(e instanceof AccessControlException))
/*     */       {
/*     */ 
/*     */ 
/* 116 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 130 */     DOWNLOAD_SOURCES_PRETEND_COMPLETE = false;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 137 */     APP_NAME = System.getProperty("azureus.product.name", "Vuze");
/* 138 */     APP_PLUS_NAME = APP_NAME + " Plus";
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 146 */     VERSION_ID = ("-AZ" + "5.7.6.0".replace(".", "") + "-").getBytes();
/*     */     
/* 148 */     FORCE_NON_CVS = System.getProperty("az.force.noncvs", "0").equals("1");
/*     */     
/* 150 */     IS_CVS_VERSION = (isCVSVersion("5.7.6.0")) && (!FORCE_NON_CVS);
/*     */     
/* 152 */     OSName = System.getProperty("os.name");
/*     */     
/* 154 */     isOSX = OSName.toLowerCase().startsWith("mac os");
/* 155 */     isLinux = OSName.equalsIgnoreCase("Linux");
/* 156 */     isSolaris = OSName.equalsIgnoreCase("SunOS");
/* 157 */     isFreeBSD = OSName.equalsIgnoreCase("FreeBSD");
/* 158 */     isWindowsXP = OSName.equalsIgnoreCase("Windows XP");
/* 159 */     isWindows95 = OSName.equalsIgnoreCase("Windows 95");
/* 160 */     isWindows98 = OSName.equalsIgnoreCase("Windows 98");
/* 161 */     isWindows2000 = OSName.equalsIgnoreCase("Windows 2000");
/* 162 */     isWindowsME = OSName.equalsIgnoreCase("Windows ME");
/* 163 */     isWindows9598ME = (isWindows95) || (isWindows98) || (isWindowsME);
/*     */     
/* 165 */     isSafeMode = false;
/*     */     
/* 167 */     isWindows = OSName.toLowerCase().startsWith("windows");
/*     */     
/* 169 */     isUnix = (!isWindows) && (!isOSX);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 179 */     PAT_SPLIT_COMMAWORDS = Pattern.compile("\\s*,\\s*");
/* 180 */     PAT_SPLIT_COMMA = Pattern.compile(",");
/* 181 */     PAT_SPLIT_DOT = Pattern.compile("\\.");
/* 182 */     PAT_SPLIT_SPACE = Pattern.compile(" ");
/* 183 */     PAT_SPLIT_SLASH_N = Pattern.compile("\n");
/*     */     
/*     */ 
/*     */ 
/*     */     boolean _is64Bit;
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 193 */       _is64Bit = System.getProperty("os.arch").contains("64");
/*     */       
/* 195 */       if (!_is64Bit)
/*     */       {
/* 197 */         _is64Bit = System.getProperty("sun.arch.data.model").equals("64");
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 201 */       _is64Bit = false;
/*     */     }
/*     */     
/* 204 */     is64Bit = _is64Bit;
/*     */     
/* 206 */     boolean _isOS64Bit = is64Bit;
/*     */     
/* 208 */     if ((isWindows) && (!_isOS64Bit)) {
/*     */       try
/*     */       {
/* 211 */         String pa = System.getenv("PROCESSOR_ARCHITECTURE");
/* 212 */         String wow_pa = System.getenv("PROCESSOR_ARCHITEW6432");
/*     */         
/* 214 */         _isOS64Bit = ((pa != null) && (pa.endsWith("64"))) || ((wow_pa != null) && (wow_pa.endsWith("64")));
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/* 220 */     isOS64Bit = _isOS64Bit;
/*     */     boolean vista_sp2_or_higher;
/* 222 */     if (isWindows)
/*     */     {
/* 224 */       Float ver = null;
/*     */       try
/*     */       {
/* 227 */         ver = new Float(System.getProperty("os.version"));
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */ 
/* 232 */       vista_sp2_or_higher = false;
/*     */       
/* 234 */       if (ver == null)
/*     */       {
/* 236 */         isWindowsVista = false;
/* 237 */         isWindowsVistaOrHigher = false;
/* 238 */         isWindows7OrHigher = false;
/* 239 */         isWindows8OrHigher = false;
/*     */       }
/*     */       else {
/* 242 */         float f_ver = ver.floatValue();
/*     */         
/* 244 */         isWindowsVista = f_ver == 6.0F;
/* 245 */         isWindowsVistaOrHigher = f_ver >= 6.0F;
/* 246 */         isWindows7OrHigher = f_ver >= 6.1F;
/* 247 */         isWindows8OrHigher = f_ver >= 6.2F;
/*     */         
/* 249 */         if (isWindowsVista)
/*     */         {
/* 251 */           LineNumberReader lnr = null;
/*     */           try
/*     */           {
/* 254 */             Process p = Runtime.getRuntime().exec(new String[] { "reg", "query", "HKLM\\Software\\Microsoft\\Windows NT\\CurrentVersion", "/v", "CSDVersion" });
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 263 */             lnr = new LineNumberReader(new InputStreamReader(p.getInputStream()));
/*     */             
/*     */             for (;;)
/*     */             {
/* 267 */               String line = lnr.readLine();
/*     */               
/* 269 */               if (line == null) {
/*     */                 break;
/*     */               }
/*     */               
/*     */ 
/* 274 */               if (line.matches(".*CSDVersion.*"))
/*     */               {
/* 276 */                 vista_sp2_or_higher = line.matches(".*Service Pack [2-9]");
/*     */                 
/* 278 */                 break;
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 285 */             if (lnr != null) {
/*     */               try
/*     */               {
/* 288 */                 lnr.close();
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 297 */             isWindowsVistaSP2OrHigher = vista_sp2_or_higher;
/*     */           }
/*     */           catch (Throwable e) {}finally
/*     */           {
/* 285 */             if (lnr != null) {
/*     */               try
/*     */               {
/* 288 */                 lnr.close();
/*     */ 
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */             
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 300 */       isWindowsVista = false;
/* 301 */       isWindowsVistaSP2OrHigher = false;
/* 302 */       isWindowsVistaOrHigher = false;
/* 303 */       isWindows7OrHigher = false;
/* 304 */       isWindows8OrHigher = false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 314 */     if (isOSX)
/*     */     {
/* 316 */       int first_digit = 0;
/* 317 */       int second_digit = 0;
/*     */       try
/*     */       {
/* 320 */         String os_version = System.getProperty("os.version");
/*     */         
/* 322 */         String[] bits = os_version.split("\\.");
/*     */         
/* 324 */         first_digit = Integer.parseInt(bits[0]);
/*     */         
/* 326 */         if (bits.length > 1)
/*     */         {
/* 328 */           second_digit = Integer.parseInt(bits[1]);
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */ 
/* 334 */       isOSX_10_5_OrHigher = (first_digit > 10) || ((first_digit == 10) && (second_digit >= 5));
/* 335 */       isOSX_10_6_OrHigher = (first_digit > 10) || ((first_digit == 10) && (second_digit >= 6));
/* 336 */       isOSX_10_7_OrHigher = (first_digit > 10) || ((first_digit == 10) && (second_digit >= 7));
/* 337 */       isOSX_10_8_OrHigher = (first_digit > 10) || ((first_digit == 10) && (second_digit >= 8));
/*     */     }
/*     */     else
/*     */     {
/* 341 */       isOSX_10_5_OrHigher = false;
/* 342 */       isOSX_10_6_OrHigher = false;
/* 343 */       isOSX_10_7_OrHigher = false;
/* 344 */       isOSX_10_8_OrHigher = false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 351 */     String vm_name = System.getProperty("java.vm.name", "");
/*     */     
/* 353 */     isAndroid = vm_name.equalsIgnoreCase("Dalvik");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 362 */     String java_version = isAndroid ? "1.6" : System.getProperty("java.version");
/* 363 */     int api_level = 0;
/*     */     
/* 365 */     if (isAndroid)
/*     */     {
/* 367 */       String sdk_int = System.getProperty("android.os.build.version.sdk_int", "0");
/*     */       try
/*     */       {
/* 370 */         api_level = Integer.parseInt(sdk_int);
/*     */         
/* 372 */         if ((api_level > 0) && (api_level <= 8))
/*     */         {
/* 374 */           java_version = "1.5";
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/* 380 */     JAVA_VERSION = java_version;
/* 381 */     API_LEVEL = api_level;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     boolean _7plus;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     boolean _8plus;
/*     */     
/*     */ 
/*     */ 
/*     */     boolean _9plus;
/*     */     
/*     */ 
/*     */ 
/*     */     boolean _10plus;
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 405 */       int pos = JAVA_VERSION.indexOf('-');
/*     */       
/* 407 */       if (pos == -1)
/*     */       {
/* 409 */         pos = JAVA_VERSION.indexOf('+');
/*     */       }
/*     */       
/* 412 */       String version = pos == -1 ? JAVA_VERSION : JAVA_VERSION.substring(0, pos);
/*     */       
/* 414 */       String[] bits = version.split("\\.");
/*     */       
/* 416 */       int first = Integer.parseInt(bits[0]);
/* 417 */       int second = bits.length == 1 ? 0 : Integer.parseInt(bits[1]);
/*     */       
/* 419 */       _7plus = (first > 1) || (second >= 7);
/* 420 */       _8plus = (first > 1) || (second >= 8);
/* 421 */       _9plus = (first > 1) || (second >= 9);
/*     */       
/* 423 */       _10plus = first >= 10;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 427 */       System.err.println("Unparsable Java version: " + JAVA_VERSION);
/*     */       
/* 429 */       e.printStackTrace();
/*     */       
/* 431 */       _7plus = false;
/* 432 */       _8plus = false;
/* 433 */       _9plus = false;
/* 434 */       _10plus = false;
/*     */     }
/*     */     
/* 437 */     isJava7OrHigher = _7plus;
/* 438 */     isJava8OrHigher = _8plus;
/* 439 */     isJava9OrHigher = _9plus;
/* 440 */     isJava10OrHigher = _10plus; }
/*     */   
/*     */   public static final Pattern PAT_SPLIT_SPACE;
/* 443 */   public static final Pattern PAT_SPLIT_SLASH_N; public static final boolean is64Bit; public static final boolean isOS64Bit; public static final boolean isOSX_10_5_OrHigher; public static final boolean isOSX_10_6_OrHigher; public static final boolean isOSX_10_7_OrHigher; public static final boolean isOSX_10_8_OrHigher; public static final boolean isAndroid; public static final String JAVA_VERSION; public static final int API_LEVEL; public static final boolean isJava7OrHigher; public static final boolean isJava8OrHigher; public static final boolean isJava9OrHigher; public static final boolean isJava10OrHigher; public static final String FILE_WILDCARD = isWindows ? "*.*" : "*";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getCurrentVersion()
/*     */   {
/* 450 */     return "5.7.6.0";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getBaseVersion()
/*     */   {
/* 460 */     return getBaseVersion("5.7.6.0");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getBaseVersion(String version)
/*     */   {
/* 467 */     int p1 = version.indexOf("_");
/*     */     
/* 469 */     if (p1 == -1)
/*     */     {
/* 471 */       return version;
/*     */     }
/*     */     
/* 474 */     return version.substring(0, p1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isCVSVersion()
/*     */   {
/* 485 */     return IS_CVS_VERSION;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isCVSVersion(String version)
/*     */   {
/* 492 */     return version.contains("_");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int getIncrementalBuild()
/*     */   {
/* 504 */     return getIncrementalBuild("5.7.6.0");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static int getIncrementalBuild(String version)
/*     */   {
/* 511 */     if (!isCVSVersion(version))
/*     */     {
/* 513 */       return 0;
/*     */     }
/*     */     
/* 516 */     int p1 = version.indexOf("_B");
/*     */     
/* 518 */     if (p1 == -1)
/*     */     {
/* 520 */       return -1;
/*     */     }
/*     */     try
/*     */     {
/* 524 */       return Integer.parseInt(version.substring(p1 + 2));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 528 */       System.out.println("can't parse version");
/*     */     }
/* 530 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isCurrentVersionLT(String version)
/*     */   {
/* 538 */     return compareVersions("5.7.6.0", version) < 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isCurrentVersionGE(String version)
/*     */   {
/* 545 */     return compareVersions("5.7.6.0", version) >= 0;
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
/*     */   public static int compareVersions(String version_1, String version_2)
/*     */   {
/*     */     try
/*     */     {
/* 561 */       version_1 = version_1.replaceAll("_CVS", "_B100");
/* 562 */       version_2 = version_2.replaceAll("_CVS", "_B100");
/*     */       
/* 564 */       if (version_1.startsWith(".")) {
/* 565 */         version_1 = "0" + version_1;
/*     */       }
/* 567 */       if (version_2.startsWith(".")) {
/* 568 */         version_2 = "0" + version_2;
/*     */       }
/*     */       
/* 571 */       version_1 = version_1.replaceAll("[^0-9.]", ".");
/* 572 */       version_2 = version_2.replaceAll("[^0-9.]", ".");
/*     */       
/* 574 */       StringTokenizer tok1 = new StringTokenizer(version_1, ".");
/* 575 */       StringTokenizer tok2 = new StringTokenizer(version_2, ".");
/*     */       for (;;)
/*     */       {
/* 578 */         if ((tok1.hasMoreTokens()) && (tok2.hasMoreTokens()))
/*     */         {
/* 580 */           int i1 = Integer.parseInt(tok1.nextToken());
/* 581 */           int i2 = Integer.parseInt(tok2.nextToken());
/*     */           
/* 583 */           if (i1 != i2)
/*     */           {
/* 585 */             return i1 - i2;
/*     */           }
/* 587 */         } else if (tok1.hasMoreTokens())
/*     */         {
/* 589 */           int i1 = Integer.parseInt(tok1.nextToken());
/*     */           
/* 591 */           if (i1 != 0)
/*     */           {
/* 593 */             return 1; }
/*     */         } else {
/* 595 */           if (!tok2.hasMoreTokens())
/*     */             break;
/* 597 */           int i2 = Integer.parseInt(tok2.nextToken());
/*     */           
/* 599 */           if (i2 != 0)
/*     */           {
/* 601 */             return -1; }
/*     */         }
/*     */       }
/* 604 */       return 0;
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 609 */       e.printStackTrace();
/*     */     }
/* 611 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isValidVersionFormat(String version)
/*     */   {
/* 619 */     if ((version == null) || (version.length() == 0))
/*     */     {
/* 621 */       return false;
/*     */     }
/*     */     
/* 624 */     for (int i = 0; i < version.length(); i++)
/*     */     {
/* 626 */       char c = version.charAt(i);
/*     */       
/* 628 */       if ((!Character.isDigit(c)) && (c != '.'))
/*     */       {
/* 630 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 634 */     if ((version.startsWith(".")) || (version.endsWith(".")) || (version.contains("..")))
/*     */     {
/*     */ 
/* 637 */       return false;
/*     */     }
/*     */     
/* 640 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isAzureusDomain(String host)
/*     */   {
/* 647 */     host = host.toLowerCase();
/*     */     
/* 649 */     for (int i = 0; i < AZUREUS_DOMAINS.length; i++)
/*     */     {
/* 651 */       String domain = AZUREUS_DOMAINS[i];
/*     */       
/* 653 */       if (domain.equals(host))
/*     */       {
/* 655 */         return true;
/*     */       }
/*     */       
/* 658 */       if (host.endsWith("." + domain))
/*     */       {
/* 660 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 664 */     return false;
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 668 */     System.out.println(compareVersions("3.0.0.1", "3.0.0.0"));
/* 669 */     System.out.println(compareVersions("3.0.0.0_B1", "3.0.0.0"));
/* 670 */     System.out.println(compareVersions("3.0.0.0", "3.0.0.0_B1"));
/* 671 */     System.out.println(compareVersions("3.0.0.0_B1", "3.0.0.0_B4"));
/* 672 */     System.out.println(compareVersions("3.0.0.0..B1", "3.0.0.0_B4"));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/Constants.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */