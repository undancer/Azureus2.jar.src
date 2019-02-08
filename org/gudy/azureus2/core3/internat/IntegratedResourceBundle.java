/*     */ package org.gudy.azureus2.core3.internat;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Properties;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import java.util.WeakHashMap;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.LightHashMap;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IntegratedResourceBundle
/*     */   extends ResourceBundle
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*  45 */   private static final Object NULL_OBJECT = new Object();
/*     */   
/*  47 */   private static final Map bundle_map = new WeakHashMap();
/*     */   private static TimerEventPeriodic compact_timer;
/*     */   protected static boolean upper_case_enabled;
/*     */   private final Locale locale;
/*     */   private final boolean is_message_bundle;
/*     */   
/*     */   static {
/*  54 */     COConfigurationManager.addAndFireParameterListener("label.lang.upper.case", new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/*     */ 
/*  62 */         IntegratedResourceBundle.upper_case_enabled = COConfigurationManager.getBooleanParameter(name, false);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected static void resetCompactTimer()
/*     */   {
/*  70 */     synchronized (bundle_map)
/*     */     {
/*  72 */       if ((compact_timer == null) && (System.getProperty("transitory.startup", "0").equals("0")))
/*     */       {
/*  74 */         compact_timer = SimpleTimer.addPeriodicEvent("IRB:compactor", 60000L, new TimerEventPerformer()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public void perform(TimerEvent event)
/*     */           {
/*     */ 
/*     */ 
/*  83 */             synchronized (IntegratedResourceBundle.bundle_map)
/*     */             {
/*  85 */               Iterator it = IntegratedResourceBundle.bundle_map.keySet().iterator();
/*     */               
/*  87 */               boolean did_something = false;
/*     */               
/*  89 */               while (it.hasNext())
/*     */               {
/*  91 */                 IntegratedResourceBundle rb = (IntegratedResourceBundle)it.next();
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  97 */                 if (rb.compact())
/*     */                 {
/*  99 */                   did_something = true;
/*     */                 }
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 106 */               if (!did_something)
/*     */               {
/* 108 */                 IntegratedResourceBundle.compact_timer.cancel();
/*     */                 
/* 110 */                 IntegratedResourceBundle.access$102(null);
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private Map messages;
/*     */   
/*     */   private Map used_messages;
/*     */   
/*     */   private List null_values;
/*     */   
/*     */   private boolean messages_dirty;
/* 127 */   private int clean_count = 0;
/*     */   
/*     */   private boolean one_off_discard_done;
/*     */   
/*     */   private File scratch_file_name;
/*     */   
/*     */   private InputStream scratch_file_is;
/*     */   
/*     */   private final int initCapacity;
/*     */   
/*     */   private Map<String, String> added_strings;
/*     */   
/*     */ 
/*     */   public IntegratedResourceBundle(ResourceBundle main, Map localizationPaths)
/*     */   {
/* 142 */     this(main, localizationPaths, null, 10);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IntegratedResourceBundle(ResourceBundle main, Map localizationPaths, int initCapacity)
/*     */   {
/* 151 */     this(main, localizationPaths, null, initCapacity);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IntegratedResourceBundle(ResourceBundle main, Map localizationPaths, Collection resource_bundles, int initCapacity)
/*     */   {
/* 161 */     this(main, localizationPaths, resource_bundles, initCapacity, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IntegratedResourceBundle(ResourceBundle main, Map localizationPaths, Collection resource_bundles, int initCapacity, boolean isMessageBundle)
/*     */   {
/* 172 */     this.initCapacity = initCapacity;
/* 173 */     this.is_message_bundle = isMessageBundle;
/*     */     
/* 175 */     this.messages = new LightHashMap(initCapacity);
/*     */     
/* 177 */     this.locale = main.getLocale();
/*     */     
/*     */ 
/*     */ 
/* 181 */     addResourceMessages(main, isMessageBundle);
/*     */     Iterator iter;
/* 183 */     synchronized (localizationPaths)
/*     */     {
/* 185 */       for (iter = localizationPaths.keySet().iterator(); iter.hasNext();) {
/* 186 */         String localizationPath = (String)iter.next();
/* 187 */         ClassLoader classLoader = (ClassLoader)localizationPaths.get(localizationPath);
/*     */         
/* 189 */         addPluginBundle(localizationPath, classLoader);
/*     */       }
/*     */     }
/*     */     
/* 193 */     if (resource_bundles != null) { Iterator itr;
/* 194 */       synchronized (resource_bundles)
/*     */       {
/* 196 */         for (itr = resource_bundles.iterator(); itr.hasNext();) {
/* 197 */           addResourceMessages((ResourceBundle)itr.next());
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 203 */     this.used_messages = new LightHashMap(this.messages.size());
/*     */     
/* 205 */     synchronized (bundle_map)
/*     */     {
/* 207 */       bundle_map.put(this, NULL_OBJECT);
/*     */       
/* 209 */       resetCompactTimer();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Locale getLocale()
/*     */   {
/* 216 */     return this.locale;
/*     */   }
/*     */   
/*     */ 
/*     */   private Map getMessages()
/*     */   {
/* 222 */     return loadMessages();
/*     */   }
/*     */   
/*     */ 
/*     */   public Enumeration getKeys()
/*     */   {
/* 228 */     new Exception("Don't call me, call getKeysLight").printStackTrace();
/*     */     
/* 230 */     Map m = loadMessages();
/*     */     
/* 232 */     return new Vector(m.keySet()).elements();
/*     */   }
/*     */   
/*     */ 
/*     */   protected Iterator getKeysLight()
/*     */   {
/* 238 */     Map m = new LightHashMap(loadMessages());
/*     */     
/* 240 */     return m.keySet().iterator();
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
/*     */   public String getString(String key, String def)
/*     */   {
/* 255 */     String s = (String)handleGetObject(key);
/* 256 */     if (s == null) {
/* 257 */       if (this.parent != null) {
/* 258 */         s = this.parent.getString(key);
/*     */       }
/* 260 */       if (s == null) {
/* 261 */         return def;
/*     */       }
/*     */     }
/* 264 */     return s;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected Object handleGetObject(String key)
/*     */   {
/*     */     Object res;
/*     */     
/*     */ 
/* 274 */     synchronized (bundle_map)
/*     */     {
/* 276 */       res = this.used_messages.get(key);
/*     */     }
/*     */     
/* 279 */     Integer keyHash = null;
/* 280 */     if (this.null_values != null) {
/* 281 */       keyHash = new Integer(key.hashCode());
/* 282 */       int index = Collections.binarySearch(this.null_values, keyHash);
/* 283 */       if (index >= 0) {
/* 284 */         return null;
/*     */       }
/*     */     }
/*     */     
/* 288 */     if (res == NULL_OBJECT)
/*     */     {
/* 290 */       return null;
/*     */     }
/*     */     
/* 293 */     if (res == null)
/*     */     {
/* 295 */       synchronized (bundle_map)
/*     */       {
/* 297 */         loadMessages();
/*     */         
/* 299 */         if (this.messages != null)
/*     */         {
/* 301 */           res = this.messages.get(key);
/*     */         }
/*     */         
/* 304 */         if ((res == null) && (this.null_values != null))
/*     */         {
/* 306 */           int index = Collections.binarySearch(this.null_values, keyHash);
/* 307 */           if (index < 0) {
/* 308 */             index = -1 * index - 1;
/*     */           }
/*     */           
/* 311 */           if (index > this.null_values.size()) {
/* 312 */             index = this.null_values.size();
/*     */           }
/*     */           
/* 315 */           this.null_values.add(index, keyHash);
/*     */         }
/*     */         else
/*     */         {
/* 319 */           this.used_messages.put(key, res == null ? NULL_OBJECT : res);
/*     */         }
/*     */         
/* 322 */         this.clean_count = 0;
/*     */         
/* 324 */         resetCompactTimer();
/*     */       }
/*     */     }
/*     */     
/* 328 */     return res;
/*     */   }
/*     */   
/*     */   public void addPluginBundle(String localizationPath, ClassLoader classLoader)
/*     */   {
/* 333 */     ResourceBundle newResourceBundle = null;
/*     */     try {
/* 335 */       if (classLoader != null) {
/* 336 */         newResourceBundle = ResourceBundle.getBundle(localizationPath, this.locale, classLoader);
/*     */       } else {
/* 338 */         newResourceBundle = ResourceBundle.getBundle(localizationPath, this.locale, IntegratedResourceBundle.class.getClassLoader());
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/*     */       try {
/* 343 */         if (classLoader != null) {
/* 344 */           newResourceBundle = ResourceBundle.getBundle(localizationPath, MessageText.LOCALE_DEFAULT, classLoader);
/*     */         } else
/* 346 */           newResourceBundle = ResourceBundle.getBundle(localizationPath, MessageText.LOCALE_DEFAULT, IntegratedResourceBundle.class.getClassLoader());
/*     */       } catch (Exception e2) {
/* 348 */         System.out.println(localizationPath + ": no default resource bundle");
/* 349 */         return;
/*     */       }
/*     */     }
/*     */     
/* 353 */     addResourceMessages(newResourceBundle, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addResourceMessages(ResourceBundle bundle)
/*     */   {
/* 362 */     addResourceMessages(bundle, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addResourceMessages(ResourceBundle bundle, boolean are_messages)
/*     */   {
/* 370 */     boolean upper_case = (upper_case_enabled) && ((this.is_message_bundle) || (are_messages));
/*     */     Enumeration enumeration;
/* 372 */     synchronized (bundle_map)
/*     */     {
/* 374 */       loadMessages();
/*     */       
/* 376 */       if (bundle != null)
/*     */       {
/* 378 */         this.messages_dirty = true;
/*     */         
/* 380 */         if ((bundle instanceof IntegratedResourceBundle))
/*     */         {
/* 382 */           Map<String, String> m = ((IntegratedResourceBundle)bundle).getMessages();
/*     */           
/* 384 */           if (upper_case)
/*     */           {
/* 386 */             for (Map.Entry<String, String> entry : m.entrySet())
/*     */             {
/* 388 */               String key = (String)entry.getKey();
/* 389 */               this.messages.put(key, toUpperCase((String)entry.getValue()));
/*     */             }
/*     */             
/*     */           } else {
/* 393 */             this.messages.putAll(m);
/*     */           }
/*     */           
/* 396 */           if (this.used_messages != null)
/*     */           {
/* 398 */             this.used_messages.keySet().removeAll(m.keySet());
/*     */           }
/*     */           
/* 401 */           if (this.null_values != null) {
/* 402 */             this.null_values.removeAll(m.keySet());
/*     */           }
/*     */         }
/*     */         else {
/* 406 */           for (enumeration = bundle.getKeys(); enumeration.hasMoreElements();)
/*     */           {
/* 408 */             String key = (String)enumeration.nextElement();
/*     */             
/* 410 */             if (upper_case)
/*     */             {
/* 412 */               this.messages.put(key, toUpperCase((String)bundle.getObject(key)));
/*     */             }
/*     */             else {
/* 415 */               this.messages.put(key, bundle.getObject(key));
/*     */             }
/*     */             
/* 418 */             if (this.used_messages != null) {
/* 419 */               this.used_messages.remove(key);
/*     */             }
/* 421 */             if (this.null_values != null) {
/* 422 */               this.null_values.remove(key);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String toUpperCase(String str)
/*     */   {
/* 436 */     int pos1 = str.indexOf('{');
/*     */     
/* 438 */     if (pos1 == -1)
/*     */     {
/* 440 */       return str.toUpperCase(this.locale);
/*     */     }
/*     */     
/* 443 */     int pos = 0;
/* 444 */     int len = str.length();
/*     */     
/* 446 */     StringBuilder result = new StringBuilder(len);
/*     */     
/* 448 */     while (pos < len)
/*     */     {
/* 450 */       if (pos1 > pos)
/*     */       {
/* 452 */         result.append(str.substring(pos, pos1).toUpperCase(this.locale));
/*     */       }
/*     */       
/* 455 */       if (pos1 == len)
/*     */       {
/* 457 */         return result.toString();
/*     */       }
/*     */       
/* 460 */       int pos2 = str.indexOf('}', pos1);
/*     */       
/* 462 */       if (pos2 == -1)
/*     */       {
/* 464 */         result.append(str.substring(pos1).toUpperCase(this.locale));
/*     */         
/* 466 */         return result.toString();
/*     */       }
/*     */       
/* 469 */       pos2++;
/*     */       
/* 471 */       result.append(str.substring(pos1, pos2));
/*     */       
/* 473 */       pos = pos2;
/*     */       
/* 475 */       pos1 = str.indexOf('{', pos);
/*     */       
/* 477 */       if (pos1 == -1)
/*     */       {
/* 479 */         pos1 = len;
/*     */       }
/*     */     }
/*     */     
/* 483 */     return result.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean compact()
/*     */   {
/* 491 */     this.clean_count += 1;
/*     */     
/* 493 */     if (this.clean_count == 1)
/*     */     {
/* 495 */       return true;
/*     */     }
/*     */     
/* 498 */     if ((this.scratch_file_is == null) || (this.messages_dirty))
/*     */     {
/* 500 */       File temp_file = null;
/*     */       
/* 502 */       FileOutputStream fos = null;
/*     */       
/*     */ 
/*     */ 
/* 506 */       if (this.scratch_file_is != null)
/*     */       {
/*     */         try
/*     */         {
/*     */ 
/* 511 */           this.scratch_file_is.close();
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 515 */           this.scratch_file_name = null;
/*     */         }
/*     */         finally
/*     */         {
/* 519 */           this.scratch_file_is = null;
/*     */         }
/*     */       }
/*     */       try
/*     */       {
/* 524 */         Properties props = new Properties();
/*     */         
/* 526 */         props.putAll(this.messages);
/*     */         
/* 528 */         if (this.scratch_file_name == null)
/*     */         {
/* 530 */           temp_file = AETemporaryFileHandler.createTempFile();
/*     */         }
/*     */         else
/*     */         {
/* 534 */           temp_file = this.scratch_file_name;
/*     */         }
/*     */         
/* 537 */         fos = new FileOutputStream(temp_file);
/*     */         
/* 539 */         props.store(fos, "message cache");
/*     */         
/* 541 */         fos.close();
/*     */         
/* 543 */         fos = null;
/*     */         
/*     */ 
/*     */ 
/* 547 */         this.scratch_file_name = temp_file;
/* 548 */         this.scratch_file_is = new FileInputStream(temp_file);
/*     */         
/* 550 */         this.messages_dirty = false;
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 554 */         if (fos != null) {
/*     */           try
/*     */           {
/* 557 */             fos.close();
/*     */           }
/*     */           catch (Throwable f) {}
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 564 */         if (temp_file != null)
/*     */         {
/* 566 */           temp_file.delete();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 571 */     if (this.scratch_file_is != null)
/*     */     {
/* 573 */       if (this.clean_count >= 2)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 584 */         this.messages = null;
/*     */       }
/*     */       
/* 587 */       if ((this.clean_count == 5) && (!this.one_off_discard_done))
/*     */       {
/*     */ 
/*     */ 
/* 591 */         this.one_off_discard_done = true;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 596 */         this.used_messages.clear();
/*     */       }
/*     */     }
/*     */     
/* 600 */     if (this.clean_count > 5)
/*     */     {
/* 602 */       Map compact_um = new LightHashMap(this.used_messages.size() + 16);
/*     */       
/* 604 */       compact_um.putAll(this.used_messages);
/*     */       
/* 606 */       this.used_messages = compact_um;
/*     */       
/* 608 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 612 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected Map loadMessages()
/*     */   {
/* 619 */     synchronized (bundle_map)
/*     */     {
/* 621 */       if (this.messages != null)
/*     */       {
/* 623 */         return this.messages;
/*     */       }
/*     */       
/*     */       Map result;
/*     */       Map result;
/* 628 */       if (this.scratch_file_is == null)
/*     */       {
/* 630 */         result = new LightHashMap();
/*     */ 
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 637 */         Properties p = new Properties();
/*     */         
/* 639 */         InputStream fis = this.scratch_file_is;
/*     */         
/*     */         try
/*     */         {
/* 643 */           p.load(fis);
/*     */           
/* 645 */           fis.close();
/*     */           
/* 647 */           this.scratch_file_is = new FileInputStream(this.scratch_file_name);
/*     */           
/* 649 */           this.messages = new LightHashMap();
/*     */           
/* 651 */           this.messages.putAll(p);
/*     */           
/* 653 */           result = this.messages;
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 657 */           if (fis != null) {
/*     */             try
/*     */             {
/* 660 */               fis.close();
/*     */             }
/*     */             catch (Throwable f) {}
/*     */           }
/*     */           
/*     */ 
/* 666 */           Debug.out("Failed to load message bundle scratch file", e);
/*     */           
/* 668 */           this.scratch_file_name.delete();
/*     */           
/* 670 */           this.scratch_file_is = null;
/*     */           
/* 672 */           result = new LightHashMap();
/*     */         }
/*     */       }
/*     */       
/* 676 */       if (this.added_strings != null)
/*     */       {
/* 678 */         result.putAll(this.added_strings);
/*     */       }
/*     */       
/* 681 */       return result;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getString()
/*     */   {
/* 688 */     return this.locale + ": use=" + this.used_messages.size() + ",map=" + (this.messages == null ? "" : String.valueOf(this.messages.size())) + (this.null_values == null ? "" : new StringBuilder().append(",null=").append(this.null_values.size()).toString()) + ",added=" + (this.added_strings == null ? "" : Integer.valueOf(this.added_strings.size()));
/*     */   }
/*     */   
/*     */ 
/*     */   public void addString(String key, String value)
/*     */   {
/* 694 */     synchronized (bundle_map) {
/* 695 */       if (this.added_strings == null)
/*     */       {
/* 697 */         this.added_strings = new HashMap();
/*     */       }
/*     */       
/* 700 */       this.added_strings.put(key, value);
/*     */       
/* 702 */       if (this.messages != null)
/*     */       {
/* 704 */         this.messages.put(key, value);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean getUseNullList() {
/* 710 */     return this.null_values != null;
/*     */   }
/*     */   
/*     */   public void setUseNullList(boolean useNullList) {
/* 714 */     if ((useNullList) && (this.null_values == null)) {
/* 715 */       this.null_values = new ArrayList(0);
/* 716 */     } else if ((!useNullList) && (this.null_values != null)) {
/* 717 */       this.null_values = null;
/*     */     }
/*     */   }
/*     */   
/*     */   public void clearUsedMessagesMap(int initialCapacity) {
/* 722 */     this.used_messages = new LightHashMap(initialCapacity);
/* 723 */     if (this.null_values != null) {
/* 724 */       this.null_values = new ArrayList(0);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/internat/IntegratedResourceBundle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */