/*     */ package org.gudy.azureus2.core3.internat;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MessageText
/*     */ {
/*  47 */   public static final Locale LOCALE_ENGLISH = Constants.LOCALE_ENGLISH;
/*     */   
/*  49 */   public static final Locale LOCALE_DEFAULT = new Locale("", "");
/*     */   
/*  51 */   private static Locale LOCALE_CURRENT = LOCALE_DEFAULT;
/*     */   
/*     */   private static final String BUNDLE_NAME;
/*     */   
/*  55 */   private static final Map<String, String> DEFAULT_EXPANSIONS = new HashMap();
/*     */   private static final Map pluginLocalizationPaths;
/*     */   private static final Collection pluginResourceBundles; private static IntegratedResourceBundle RESOURCE_BUNDLE; private static Set platform_specific_keys;
/*  58 */   static { BUNDLE_NAME = System.getProperty("az.factory.internat.bundle", "org.gudy.azureus2.internat.MessagesBundle");
/*     */     
/*  60 */     updateProductName();
/*     */     
/*     */ 
/*  63 */     pluginLocalizationPaths = new HashMap();
/*  64 */     pluginResourceBundles = new ArrayList();
/*     */     
/*  66 */     platform_specific_keys = new HashSet();
/*  67 */     PAT_PARAM_ALPHA = Pattern.compile("\\{([^0-9].+?)\\}");
/*     */     
/*     */ 
/*  70 */     bundle_fail_count = 0;
/*     */     
/*  72 */     listeners = new ArrayList();
/*     */     
/*     */ 
/*     */ 
/*  76 */     setResourceBundle(new IntegratedResourceBundle(getResourceBundle(BUNDLE_NAME, LOCALE_DEFAULT, MessageText.class.getClassLoader()), pluginLocalizationPaths, null, 4000, true)); }
/*     */   
/*     */   private static final Pattern PAT_PARAM_ALPHA;
/*     */   private static int bundle_fail_count;
/*     */   private static final List listeners;
/*  81 */   private static IntegratedResourceBundle DEFAULT_BUNDLE = RESOURCE_BUNDLE;
/*     */   
/*     */ 
/*     */   public static void updateProductName()
/*     */   {
/*  86 */     DEFAULT_EXPANSIONS.put("base.product.name", Constants.APP_NAME);
/*  87 */     DEFAULT_EXPANSIONS.put("base.plus.product.name", Constants.APP_PLUS_NAME);
/*     */   }
/*     */   
/*     */   public static void loadBundle() {
/*  91 */     loadBundle(false);
/*     */   }
/*     */   
/*     */   public static void loadBundle(boolean forceReload) {
/*  95 */     Locale old_locale = getCurrentLocale();
/*     */     
/*  97 */     String savedLocaleString = COConfigurationManager.getStringParameter("locale");
/*     */     
/*     */ 
/* 100 */     String[] savedLocaleStrings = savedLocaleString.split("_", 3);
/* 101 */     Locale savedLocale; Locale savedLocale; if ((savedLocaleStrings.length > 0) && (savedLocaleStrings[0].length() == 2)) { Locale savedLocale;
/* 102 */       if (savedLocaleStrings.length == 3) {
/* 103 */         savedLocale = new Locale(savedLocaleStrings[0], savedLocaleStrings[1], savedLocaleStrings[2]);
/*     */       } else { Locale savedLocale;
/* 105 */         if ((savedLocaleStrings.length == 2) && (savedLocaleStrings[1].length() == 2))
/*     */         {
/* 107 */           savedLocale = new Locale(savedLocaleStrings[0], savedLocaleStrings[1]);
/*     */         } else
/* 109 */           savedLocale = new Locale(savedLocaleStrings[0]);
/*     */       }
/*     */     } else { Locale savedLocale;
/* 112 */       if ((savedLocaleStrings.length == 3) && (savedLocaleStrings[0].length() == 0) && (savedLocaleStrings[2].length() > 0))
/*     */       {
/* 114 */         savedLocale = new Locale(savedLocaleStrings[0], savedLocaleStrings[1], savedLocaleStrings[2]);
/*     */       }
/*     */       else {
/* 117 */         savedLocale = Locale.getDefault();
/*     */       }
/*     */     }
/* 120 */     changeLocale(savedLocale, forceReload);
/*     */     
/* 122 */     COConfigurationManager.setParameter("locale.set.complete.count", COConfigurationManager.getIntParameter("locale.set.complete.count") + 1);
/*     */     
/*     */ 
/*     */ 
/* 126 */     Locale new_locale = getCurrentLocale();
/*     */     
/* 128 */     if ((!old_locale.equals(new_locale)) || (forceReload))
/*     */     {
/* 130 */       for (int i = 0; i < listeners.size(); i++) {
/*     */         try
/*     */         {
/* 133 */           ((MessageTextListener)listeners.get(i)).localeChanged(old_locale, new_locale);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 137 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addListener(MessageTextListener listener)
/*     */   {
/* 147 */     listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void addAndFireListener(MessageTextListener listener)
/*     */   {
/* 153 */     listeners.add(listener);
/*     */     
/* 155 */     listener.localeChanged(getCurrentLocale(), getCurrentLocale());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void removeListener(MessageTextListener listener)
/*     */   {
/* 162 */     listeners.remove(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static ResourceBundle getResourceBundle(String name, Locale loc, ClassLoader cl)
/*     */   {
/*     */     try
/*     */     {
/* 173 */       return ResourceBundle.getBundle(name, loc, cl);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 177 */       bundle_fail_count += 1;
/*     */       
/* 179 */       if (bundle_fail_count == 1)
/*     */       {
/* 181 */         e.printStackTrace();
/*     */         
/* 183 */         Logger.log(new LogAlert(true, 3, "Failed to load resource bundle. One possible cause is that you have installed " + Constants.APP_NAME + " into a directory " + "with a '!' in it. If so, please remove the '!'."));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 189 */     new ResourceBundle()
/*     */     {
/*     */ 
/*     */       public Locale getLocale()
/*     */       {
/*     */ 
/* 195 */         return MessageText.LOCALE_DEFAULT;
/*     */       }
/*     */       
/*     */ 
/*     */       protected Object handleGetObject(String key)
/*     */       {
/* 201 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */       public Enumeration getKeys()
/*     */       {
/* 207 */         return new Vector().elements();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void setResourceBundle(IntegratedResourceBundle bundle)
/*     */   {
/* 217 */     RESOURCE_BUNDLE = bundle;
/*     */     
/* 219 */     Iterator keys = RESOURCE_BUNDLE.getKeysLight();
/*     */     
/* 221 */     String ui_suffix = getUISuffix();
/*     */     
/* 223 */     String platform_suffix = getPlatformSuffix();
/*     */     
/* 225 */     Set platformKeys = new HashSet();
/*     */     
/* 227 */     while (keys.hasNext()) {
/* 228 */       String key = (String)keys.next();
/* 229 */       if (key.endsWith(platform_suffix)) {
/* 230 */         platformKeys.add(key);
/* 231 */       } else if (key.endsWith(ui_suffix)) {
/* 232 */         RESOURCE_BUNDLE.addString(key.substring(0, key.length() - ui_suffix.length()), RESOURCE_BUNDLE.getString(key));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 238 */     platform_specific_keys = platformKeys;
/*     */   }
/*     */   
/*     */   public static boolean keyExists(String key)
/*     */   {
/*     */     try {
/* 244 */       getResourceBundleString(key);
/* 245 */       return true;
/*     */     } catch (MissingResourceException e) {}
/* 247 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean keyExistsForDefaultLocale(String key)
/*     */   {
/*     */     try {
/* 253 */       DEFAULT_BUNDLE.getString(key);
/* 254 */       return true;
/*     */     } catch (MissingResourceException e) {}
/* 256 */     return false;
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
/*     */   public static String getString(String key, String sDefault)
/*     */   {
/* 270 */     if (key == null) {
/* 271 */       return "";
/*     */     }
/* 273 */     String target_key = key + getPlatformSuffix();
/*     */     
/* 275 */     if (!platform_specific_keys.contains(target_key))
/*     */     {
/* 277 */       target_key = key;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 282 */       return getResourceBundleString(target_key);
/*     */     }
/*     */     catch (MissingResourceException e) {}
/*     */     
/* 286 */     return getPlatformNeutralString(key, sDefault);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getString(String key)
/*     */   {
/* 294 */     if (key == null) {
/* 295 */       return "";
/*     */     }
/* 297 */     String target_key = key + getPlatformSuffix();
/*     */     
/* 299 */     if (!platform_specific_keys.contains(target_key))
/*     */     {
/* 301 */       target_key = key;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 306 */       return getResourceBundleString(target_key);
/*     */     }
/*     */     catch (MissingResourceException e) {}
/*     */     
/* 310 */     return getPlatformNeutralString(key);
/*     */   }
/*     */   
/*     */   public static String getPlatformNeutralString(String key)
/*     */   {
/*     */     try {
/* 316 */       return getResourceBundleString(key);
/*     */     }
/*     */     catch (MissingResourceException e) {
/* 319 */       if ((key.startsWith("!")) && (key.endsWith("!")))
/* 320 */         return key.substring(1, key.length() - 1);
/*     */     }
/* 322 */     return '!' + key + '!';
/*     */   }
/*     */   
/*     */   public static String getPlatformNeutralString(String key, String sDefault)
/*     */   {
/*     */     try {
/* 328 */       return getResourceBundleString(key);
/*     */     } catch (MissingResourceException e) {
/* 330 */       if ((key.startsWith("!")) && (key.endsWith("!")))
/* 331 */         return key.substring(1, key.length() - 1);
/*     */     }
/* 333 */     return sDefault;
/*     */   }
/*     */   
/*     */   private static String getResourceBundleString(String key)
/*     */   {
/* 338 */     if (key == null) {
/* 339 */       return "";
/*     */     }
/*     */     
/* 342 */     String value = RESOURCE_BUNDLE.getString(key);
/*     */     
/* 344 */     return expandValue(value);
/*     */   }
/*     */   
/*     */   public static String expandValue(String value)
/*     */   {
/* 349 */     if ((value != null) && (value.indexOf('}') > 0)) {
/* 350 */       Matcher matcher = PAT_PARAM_ALPHA.matcher(value);
/* 351 */       while (matcher.find()) {
/* 352 */         String key = matcher.group(1);
/*     */         try {
/* 354 */           String text = (String)DEFAULT_EXPANSIONS.get(key);
/*     */           
/* 356 */           if (text == null) {
/* 357 */             text = getResourceBundleString(key);
/*     */           }
/*     */           
/* 360 */           if (text != null) {
/* 361 */             value = value.replaceAll("\\Q{" + key + "}\\E", text);
/*     */           }
/*     */         }
/*     */         catch (MissingResourceException e) {}
/*     */       }
/*     */     }
/*     */     
/* 368 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String getPlatformSuffix()
/*     */   {
/* 377 */     if (Constants.isOSX)
/* 378 */       return "._mac";
/* 379 */     if (Constants.isLinux)
/* 380 */       return "._linux";
/* 381 */     if (Constants.isUnix)
/* 382 */       return "._unix";
/* 383 */     if (Constants.isFreeBSD)
/* 384 */       return "._freebsd";
/* 385 */     if (Constants.isSolaris)
/* 386 */       return "._solaris";
/* 387 */     if (Constants.isWindows) {
/* 388 */       return "._windows";
/*     */     }
/* 390 */     return "._unknown";
/*     */   }
/*     */   
/*     */   private static String getUISuffix() {
/* 394 */     return "az2".equalsIgnoreCase(COConfigurationManager.getStringParameter("ui")) ? "._classic" : "._vuze";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getStringForSentence(String sentence)
/*     */   {
/* 404 */     StringTokenizer st = new StringTokenizer(sentence, " ");
/* 405 */     StringBuilder result = new StringBuilder(sentence.length());
/* 406 */     String separator = "";
/* 407 */     while (st.hasMoreTokens())
/*     */     {
/* 409 */       result.append(separator);
/* 410 */       separator = " ";
/*     */       
/* 412 */       String word = st.nextToken();
/* 413 */       int length = word.length();
/* 414 */       int position = word.lastIndexOf(".");
/* 415 */       if ((position == -1) || (position + 1 == length)) {
/* 416 */         result.append(word);
/*     */       }
/*     */       else {
/* 419 */         String translated = getString(word);
/* 420 */         if (translated.equals("!" + word + "!")) {
/* 421 */           result.append(word);
/*     */         }
/*     */         else {
/* 424 */           result.append(translated);
/*     */         }
/*     */       }
/*     */     }
/* 428 */     return result.toString();
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
/*     */   public static String getString(String key, String[] params)
/*     */   {
/* 442 */     String res = getString(key);
/*     */     
/* 444 */     if (params == null) {
/* 445 */       return res;
/*     */     }
/*     */     
/* 448 */     for (int i = 0; i < params.length; i++)
/*     */     {
/* 450 */       String from_str = "%" + (i + 1);
/* 451 */       String to_str = params[i];
/*     */       
/* 453 */       res = replaceStrings(res, from_str, to_str);
/*     */     }
/*     */     
/* 456 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static String replaceStrings(String str, String f_s, String t_s)
/*     */   {
/* 465 */     int pos = 0;
/*     */     
/* 467 */     String res = "";
/*     */     
/* 469 */     while (pos < str.length())
/*     */     {
/* 471 */       int p1 = str.indexOf(f_s, pos);
/*     */       
/* 473 */       if (p1 == -1)
/*     */       {
/* 475 */         res = res + str.substring(pos);
/*     */         
/* 477 */         break;
/*     */       }
/*     */       
/* 480 */       res = res + str.substring(pos, p1) + t_s;
/*     */       
/* 482 */       pos = p1 + f_s.length();
/*     */     }
/*     */     
/* 485 */     return res;
/*     */   }
/*     */   
/*     */   public static String getDefaultLocaleString(String key)
/*     */   {
/*     */     try {
/* 491 */       return DEFAULT_BUNDLE.getString(key);
/*     */     }
/*     */     catch (MissingResourceException e) {
/* 494 */       if ((key.startsWith("!")) && (key.endsWith("!")))
/* 495 */         return key.substring(1, key.length() - 1);
/*     */     }
/* 497 */     return '!' + key + '!';
/*     */   }
/*     */   
/*     */   public static Locale getCurrentLocale()
/*     */   {
/* 502 */     return LOCALE_DEFAULT.equals(LOCALE_CURRENT) ? LOCALE_ENGLISH : LOCALE_CURRENT;
/*     */   }
/*     */   
/*     */   public static boolean isCurrentLocale(Locale locale) {
/* 506 */     return LOCALE_ENGLISH.equals(locale) ? LOCALE_CURRENT.equals(LOCALE_DEFAULT) : LOCALE_CURRENT.equals(locale);
/*     */   }
/*     */   
/*     */   public static Locale[] getLocales(boolean sort) {
/* 510 */     String bundleFolder = BUNDLE_NAME.replace('.', '/');
/* 511 */     String prefix = BUNDLE_NAME.substring(BUNDLE_NAME.lastIndexOf('.') + 1);
/* 512 */     String extension = ".properties";
/*     */     
/* 514 */     String urlString = MessageText.class.getClassLoader().getResource(bundleFolder.concat(".properties")).toExternalForm();
/*     */     
/* 516 */     String[] bundles = null;
/*     */     
/* 518 */     if (urlString.startsWith("jar:file:"))
/*     */     {
/* 520 */       File jar = FileUtil.getJarFileFromURL(urlString);
/*     */       
/* 522 */       if (jar != null) {
/*     */         try
/*     */         {
/* 525 */           JarFile jarFile = new JarFile(jar);
/* 526 */           Enumeration entries = jarFile.entries();
/* 527 */           ArrayList list = new ArrayList(250);
/* 528 */           while (entries.hasMoreElements()) {
/* 529 */             JarEntry jarEntry = (JarEntry)entries.nextElement();
/* 530 */             if ((jarEntry.getName().startsWith(bundleFolder)) && (jarEntry.getName().endsWith(".properties")))
/*     */             {
/*     */ 
/* 533 */               list.add(jarEntry.getName().substring(bundleFolder.length() - prefix.length()));
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 538 */           bundles = (String[])list.toArray(new String[list.size()]);
/*     */         } catch (Exception e) {
/* 540 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     } else {
/* 544 */       File bundleDirectory = new File(URI.create(urlString)).getParentFile();
/*     */       
/*     */ 
/*     */ 
/* 548 */       bundles = bundleDirectory.list(new FilenameFilter() {
/*     */         public boolean accept(File dir, String name) {
/* 550 */           return (name.startsWith(this.val$prefix)) && (name.endsWith(".properties"));
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 555 */     HashSet bundleSet = new HashSet();
/*     */     
/*     */ 
/* 558 */     File localDir = new File(SystemProperties.getUserPath());
/* 559 */     String[] localBundles = localDir.list(new FilenameFilter() {
/*     */       public boolean accept(File dir, String name) {
/* 561 */         return (name.startsWith(this.val$prefix)) && (name.endsWith(".properties"));
/*     */       }
/*     */     });
/*     */     
/*     */ 
/*     */ 
/* 567 */     if (localBundles != null)
/*     */     {
/* 569 */       bundleSet.addAll(Arrays.asList(localBundles));
/*     */     }
/*     */     
/*     */ 
/* 573 */     File appDir = new File(SystemProperties.getApplicationPath());
/* 574 */     String[] appBundles = appDir.list(new FilenameFilter() {
/*     */       public boolean accept(File dir, String name) {
/* 576 */         return (name.startsWith(this.val$prefix)) && (name.endsWith(".properties"));
/*     */       }
/*     */     });
/*     */     
/*     */ 
/*     */ 
/* 582 */     if (appBundles != null)
/*     */     {
/* 584 */       bundleSet.addAll(Arrays.asList(appBundles));
/*     */     }
/*     */     
/* 587 */     bundleSet.addAll(Arrays.asList(bundles));
/*     */     
/* 589 */     List foundLocalesList = new ArrayList(bundleSet.size());
/*     */     
/* 591 */     foundLocalesList.add(LOCALE_ENGLISH);
/*     */     
/* 593 */     Iterator val = bundleSet.iterator();
/* 594 */     while (val.hasNext()) {
/* 595 */       String sBundle = (String)val.next();
/*     */       
/*     */ 
/* 598 */       if (prefix.length() + 1 < sBundle.length() - ".properties".length()) {
/* 599 */         String locale = sBundle.substring(prefix.length() + 1, sBundle.length() - ".properties".length());
/*     */         
/* 601 */         String[] sLocalesSplit = locale.split("_", 3);
/* 602 */         if ((sLocalesSplit.length > 0) && (sLocalesSplit[0].length() == 2)) {
/* 603 */           if (sLocalesSplit.length == 3) {
/* 604 */             foundLocalesList.add(new Locale(sLocalesSplit[0], sLocalesSplit[1], sLocalesSplit[2]));
/* 605 */           } else if ((sLocalesSplit.length == 2) && (sLocalesSplit[1].length() == 2)) {
/* 606 */             foundLocalesList.add(new Locale(sLocalesSplit[0], sLocalesSplit[1]));
/*     */           } else {
/* 608 */             foundLocalesList.add(new Locale(sLocalesSplit[0]));
/*     */           }
/*     */         }
/* 611 */         else if ((sLocalesSplit.length == 3) && (sLocalesSplit[0].length() == 0) && (sLocalesSplit[2].length() > 0))
/*     */         {
/*     */ 
/* 614 */           foundLocalesList.add(new Locale(sLocalesSplit[0], sLocalesSplit[1], sLocalesSplit[2]));
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 620 */     Locale[] foundLocales = new Locale[foundLocalesList.size()];
/*     */     
/* 622 */     foundLocalesList.toArray(foundLocales);
/*     */     
/* 624 */     if (sort) {
/*     */       try {
/* 626 */         Arrays.sort(foundLocales, new Comparator() {
/*     */           public final int compare(Object a, Object b) {
/* 628 */             return ((Locale)a).getDisplayName((Locale)a).compareToIgnoreCase(((Locale)b).getDisplayName((Locale)b));
/*     */           }
/*     */           
/*     */         });
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 635 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/* 638 */     return foundLocales;
/*     */   }
/*     */   
/*     */   public static boolean changeLocale(Locale newLocale) {
/* 642 */     return changeLocale(newLocale, false);
/*     */   }
/*     */   
/*     */   private static boolean changeLocale(Locale newLocale, boolean force)
/*     */   {
/* 647 */     Locale.setDefault(newLocale);
/*     */     
/* 649 */     if ((!isCurrentLocale(newLocale)) || (force)) {
/* 650 */       Locale.setDefault(LOCALE_DEFAULT);
/* 651 */       ResourceBundle newResourceBundle = null;
/* 652 */       String bundleFolder = BUNDLE_NAME.replace('.', '/');
/* 653 */       String prefix = BUNDLE_NAME.substring(BUNDLE_NAME.lastIndexOf('.') + 1);
/* 654 */       String extension = ".properties";
/*     */       
/* 656 */       if (newLocale.equals(LOCALE_ENGLISH)) {
/* 657 */         newLocale = LOCALE_DEFAULT;
/*     */       }
/*     */       try {
/* 660 */         File userBundleFile = new File(SystemProperties.getUserPath());
/* 661 */         File appBundleFile = new File(SystemProperties.getApplicationPath());
/*     */         
/*     */ 
/*     */ 
/* 665 */         ClassLoader cl = MessageText.class.getClassLoader();
/*     */         
/* 667 */         URL u = cl.getResource(bundleFolder + ".properties");
/*     */         
/* 669 */         if (u == null)
/*     */         {
/*     */ 
/*     */ 
/* 673 */           return false;
/*     */         }
/* 675 */         String sJar = u.toString();
/* 676 */         sJar = sJar.substring(0, sJar.length() - prefix.length() - ".properties".length());
/* 677 */         URL jarURL = new URL(sJar);
/*     */         
/*     */ 
/* 680 */         URL[] urls = { userBundleFile.toURL(), appBundleFile.toURL(), jarURL };
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 705 */         newResourceBundle = getResourceBundle("MessagesBundle", newLocale, new URLClassLoader(urls));
/*     */         
/*     */ 
/*     */ 
/* 709 */         if ((!newResourceBundle.getLocale().getLanguage().equals(newLocale.getLanguage())) && (!newLocale.getCountry().equals("")))
/*     */         {
/*     */ 
/* 712 */           Locale foundLocale = newResourceBundle.getLocale();
/* 713 */           System.out.println("changeLocale: " + (foundLocale.toString().equals("") ? "*Default Language*" : foundLocale.getDisplayLanguage()) + " != " + newLocale.getDisplayName() + ". Searching without country..");
/*     */           
/*     */ 
/*     */ 
/* 717 */           Locale localeJustLang = new Locale(newLocale.getLanguage());
/* 718 */           newResourceBundle = getResourceBundle("MessagesBundle", localeJustLang, new URLClassLoader(urls));
/*     */           
/*     */ 
/* 721 */           if ((newResourceBundle == null) || (!newResourceBundle.getLocale().getLanguage().equals(localeJustLang.getLanguage())))
/*     */           {
/*     */ 
/* 724 */             System.out.println("changeLocale: Searching for language " + newLocale.getDisplayLanguage() + " in *any* country..");
/* 725 */             Locale[] locales = getLocales(false);
/* 726 */             for (int i = 0; i < locales.length; i++) {
/* 727 */               if (locales[i].getLanguage().equals(newLocale.getLanguage())) {
/* 728 */                 newResourceBundle = getResourceBundle("MessagesBundle", locales[i], new URLClassLoader(urls));
/*     */                 
/* 730 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       } catch (MissingResourceException e) {
/* 736 */         System.out.println("changeLocale: no resource bundle for " + newLocale);
/* 737 */         Debug.printStackTrace(e);
/* 738 */         return false;
/*     */       } catch (Exception e) {
/* 740 */         Debug.printStackTrace(e);
/*     */       }
/*     */       
/* 743 */       if (newResourceBundle != null)
/*     */       {
/* 745 */         if ((!newLocale.equals(LOCALE_DEFAULT)) && (!newResourceBundle.getLocale().equals(newLocale)))
/*     */         {
/* 747 */           String sNewLanguage = newResourceBundle.getLocale().getDisplayName();
/* 748 */           if ((sNewLanguage == null) || (sNewLanguage.trim().equals("")))
/* 749 */             sNewLanguage = "English (default)";
/* 750 */           System.out.println("changeLocale: no message properties for Locale '" + newLocale.getDisplayName() + "' (" + newLocale + "), using '" + sNewLanguage + "'");
/* 751 */           if (newResourceBundle.getLocale().equals(RESOURCE_BUNDLE.getLocale())) {
/* 752 */             return false;
/*     */           }
/*     */         }
/* 755 */         newLocale = newResourceBundle.getLocale();
/* 756 */         Locale.setDefault(newLocale.equals(LOCALE_DEFAULT) ? LOCALE_ENGLISH : newLocale);
/* 757 */         LOCALE_CURRENT = newLocale;
/* 758 */         setResourceBundle(new IntegratedResourceBundle(newResourceBundle, pluginLocalizationPaths, null, 4000, true));
/* 759 */         if (newLocale.equals(LOCALE_DEFAULT))
/* 760 */           DEFAULT_BUNDLE = RESOURCE_BUNDLE;
/* 761 */         return true;
/*     */       }
/* 763 */       return false;
/*     */     }
/* 765 */     return false;
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
/*     */   public static boolean integratePluginMessages(String localizationPath, ClassLoader classLoader)
/*     */   {
/* 779 */     boolean integratedSuccessfully = false;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 784 */     if ((localizationPath != null) && (localizationPath.length() != 0))
/*     */     {
/* 786 */       synchronized (pluginLocalizationPaths)
/*     */       {
/* 788 */         pluginLocalizationPaths.put(localizationPath, classLoader);
/*     */       }
/*     */       
/* 791 */       RESOURCE_BUNDLE.addPluginBundle(localizationPath, classLoader);
/* 792 */       setResourceBundle(RESOURCE_BUNDLE);
/*     */       
/* 794 */       integratedSuccessfully = true;
/*     */     }
/* 796 */     return integratedSuccessfully;
/*     */   }
/*     */   
/*     */   public static boolean integratePluginMessages(ResourceBundle bundle) {
/* 800 */     synchronized (pluginResourceBundles)
/*     */     {
/* 802 */       pluginResourceBundles.add(bundle);
/*     */     }
/*     */     
/* 805 */     RESOURCE_BUNDLE.addResourceMessages(bundle, true);
/* 806 */     setResourceBundle(RESOURCE_BUNDLE);
/*     */     
/* 808 */     return true;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String resolveLocalizationKey(String localizationKey)
/*     */   {
/* 861 */     if (null == localizationKey) {
/* 862 */       return null;
/*     */     }
/*     */     
/* 865 */     if ("az3".equalsIgnoreCase(COConfigurationManager.getStringParameter("ui"))) {
/* 866 */       String v3Key = null;
/* 867 */       if (!localizationKey.startsWith("v3.")) {
/* 868 */         v3Key = "v3." + localizationKey;
/*     */       } else {
/* 870 */         v3Key = localizationKey;
/*     */       }
/*     */       
/* 873 */       if (keyExists(v3Key)) {
/* 874 */         return v3Key;
/*     */       }
/*     */     }
/*     */     
/* 878 */     return localizationKey;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String resolveAcceleratorKey(String acceleratorKey)
/*     */   {
/* 889 */     if (null == acceleratorKey) {
/* 890 */       return null;
/*     */     }
/*     */     
/* 893 */     if ("az3".equalsIgnoreCase(COConfigurationManager.getStringParameter("ui"))) {
/* 894 */       String v3Key = null;
/* 895 */       if (!acceleratorKey.startsWith("v3.")) {
/* 896 */         v3Key = "v3." + acceleratorKey;
/*     */       } else {
/* 898 */         v3Key = acceleratorKey;
/*     */       }
/*     */       
/* 901 */       if (keyExists(v3Key + ".keybinding")) {
/* 902 */         return v3Key;
/*     */       }
/*     */     }
/*     */     
/* 906 */     return acceleratorKey;
/*     */   }
/*     */   
/*     */   public static abstract interface MessageTextListener
/*     */   {
/*     */     public abstract void localeChanged(Locale paramLocale1, Locale paramLocale2);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/internat/MessageText.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */