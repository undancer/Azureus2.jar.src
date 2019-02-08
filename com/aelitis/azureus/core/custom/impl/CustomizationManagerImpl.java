/*     */ package com.aelitis.azureus.core.custom.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.custom.Customization;
/*     */ import com.aelitis.azureus.core.custom.CustomizationException;
/*     */ import com.aelitis.azureus.core.custom.CustomizationManager;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileComponent;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileProcessor;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.utils.StaticUtilities;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CustomizationManagerImpl
/*     */   implements CustomizationManager
/*     */ {
/*  52 */   private static final CustomizationManagerImpl singleton = new CustomizationManagerImpl();
/*     */   private boolean initialised;
/*     */   
/*     */   public static CustomizationManager getSingleton()
/*     */   {
/*  57 */     return singleton;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  62 */   private final Map customization_file_map = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */   private String current_customization_name;
/*     */   
/*     */ 
/*     */   private CustomizationImpl current_customization;
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean preInitialize()
/*     */   {
/*  75 */     File user_dir = FileUtil.getUserFile("custom");
/*     */     
/*  77 */     File app_dir = FileUtil.getApplicationFile("custom");
/*     */     
/*  79 */     boolean changed = preInitialize(app_dir);
/*     */     
/*  81 */     if (!user_dir.equals(app_dir))
/*     */     {
/*  83 */       if (preInitialize(user_dir))
/*     */       {
/*  85 */         changed = true;
/*     */       }
/*     */     }
/*     */     
/*  89 */     return changed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean preInitialize(File dir)
/*     */   {
/*  96 */     boolean changed = false;
/*     */     
/*  98 */     if (dir.isDirectory())
/*     */     {
/* 100 */       File[] files = dir.listFiles();
/*     */       
/* 102 */       if (files != null)
/*     */       {
/* 104 */         for (int i = 0; i < files.length; i++)
/*     */         {
/* 106 */           File file = files[i];
/*     */           
/* 108 */           String name = file.getName();
/*     */           
/* 110 */           if (name.endsWith(".config"))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 115 */             FileInputStream fis = null;
/*     */             
/* 117 */             boolean ok = false;
/*     */             
/* 119 */             System.out.println("Processing config presets: " + file);
/*     */             try
/*     */             {
/* 122 */               fis = new FileInputStream(file);
/*     */               
/* 124 */               Properties props = new Properties();
/*     */               
/* 126 */               props.load(fis);
/*     */               
/* 128 */               List<String> errors = new ArrayList();
/*     */               
/* 130 */               for (Map.Entry<Object, Object> entry : props.entrySet())
/*     */               {
/* 132 */                 String config_name = (String)entry.getKey();
/* 133 */                 String config_value = (String)entry.getValue();
/*     */                 
/* 135 */                 System.out.println("\t" + config_name + " -> " + config_value);
/*     */                 try
/*     */                 {
/* 138 */                   int pos = config_value.indexOf(':');
/*     */                   
/* 140 */                   if (pos == -1)
/*     */                   {
/* 142 */                     throw new Exception("Value is invalid - missing type specification");
/*     */                   }
/*     */                   
/* 145 */                   String config_type = config_value.substring(0, pos).trim().toLowerCase();
/*     */                   
/* 147 */                   config_value = config_value.substring(pos + 1);
/*     */                   
/* 149 */                   if (config_type.equals("bool"))
/*     */                   {
/* 151 */                     config_value = config_value.trim().toLowerCase();
/*     */                     
/*     */                     boolean b;
/*     */                     
/* 155 */                     if (config_value.equals("true"))
/*     */                     {
/* 157 */                       b = true;
/*     */                     } else { boolean b;
/* 159 */                       if (config_value.equals("false"))
/*     */                       {
/* 161 */                         b = false;
/*     */                       }
/*     */                       else
/*     */                       {
/* 165 */                         throw new Exception("Invalid boolean value"); }
/*     */                     }
/*     */                     boolean b;
/* 168 */                     COConfigurationManager.setParameter(config_name, b);
/*     */                   }
/* 170 */                   else if (config_type.equals("long"))
/*     */                   {
/* 172 */                     long l = Long.parseLong(config_value.trim());
/*     */                     
/* 174 */                     COConfigurationManager.setParameter(config_name, l);
/*     */                   }
/* 176 */                   else if (config_type.equals("float"))
/*     */                   {
/* 178 */                     float f = Float.parseFloat(config_value.trim());
/*     */                     
/* 180 */                     COConfigurationManager.setParameter(config_name, f);
/*     */                   }
/* 182 */                   else if (config_type.equals("string"))
/*     */                   {
/* 184 */                     COConfigurationManager.setParameter(config_name, config_value);
/*     */                   }
/* 186 */                   else if (config_type.equals("byte[]"))
/*     */                   {
/* 188 */                     COConfigurationManager.setParameter(config_name, ByteFormatter.decodeString(config_value));
/*     */                   }
/* 190 */                   else if (config_type.equals("list"))
/*     */                   {
/* 192 */                     COConfigurationManager.setParameter(config_name, (List)BDecoder.decode(ByteFormatter.decodeString(config_value)));
/*     */                   }
/* 194 */                   else if (config_type.equals("map"))
/*     */                   {
/* 196 */                     COConfigurationManager.setParameter(config_name, BDecoder.decode(ByteFormatter.decodeString(config_value)));
/*     */                   }
/*     */                   else
/*     */                   {
/* 200 */                     throw new Exception("Value is invalid - unknown type specifier");
/*     */                   }
/*     */                   
/* 203 */                   changed = true;
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 207 */                   errors.add(e.getMessage() + ": " + config_name + "=" + entry.getValue());
/*     */                 }
/*     */               }
/*     */               
/* 211 */               if (errors.size() > 0)
/*     */               {
/* 213 */                 throw new Exception("Found " + errors.size() + " errors: " + errors.toString());
/*     */               }
/*     */               
/* 216 */               ok = true;
/*     */               
/* 218 */               System.out.println("Presets applied");
/*     */             }
/*     */             catch (Throwable e) {
/*     */               File rename_target;
/* 222 */               System.err.println("Failed to process custom .config file " + file);
/*     */               
/* 224 */               e.printStackTrace();
/*     */             }
/*     */             finally {
/*     */               File rename_target;
/* 228 */               if (fis != null) {
/*     */                 try
/*     */                 {
/* 231 */                   fis.close();
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 235 */                   e.printStackTrace();
/*     */                 }
/*     */               }
/*     */               
/* 239 */               File rename_target = new File(file.getAbsolutePath() + (ok ? ".applied" : ".bad"));
/*     */               
/* 241 */               rename_target.delete();
/*     */               
/* 243 */               file.renameTo(rename_target);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 249 */     return changed;
/*     */   }
/*     */   
/*     */ 
/*     */   public void initialize()
/*     */   {
/* 255 */     synchronized (this)
/*     */     {
/* 257 */       if (this.initialised)
/*     */       {
/* 259 */         return;
/*     */       }
/*     */       
/* 262 */       this.initialised = true;
/*     */     }
/*     */     
/* 265 */     VuzeFileHandler.getSingleton().addProcessor(new VuzeFileProcessor()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void process(VuzeFile[] files, int expected_types)
/*     */       {
/*     */ 
/*     */ 
/* 273 */         for (int i = 0; i < files.length; i++)
/*     */         {
/* 275 */           VuzeFile vf = files[i];
/*     */           
/* 277 */           VuzeFileComponent[] comps = vf.getComponents();
/*     */           
/* 279 */           for (int j = 0; j < comps.length; j++)
/*     */           {
/* 281 */             VuzeFileComponent comp = comps[j];
/*     */             
/* 283 */             if (comp.getType() == 64) {
/*     */               try
/*     */               {
/* 286 */                 Map map = comp.getContent();
/*     */                 
/* 288 */                 ((CustomizationManagerImpl)CustomizationManagerImpl.getSingleton()).importCustomization(map);
/*     */                 
/* 290 */                 comp.setProcessed();
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 294 */                 Debug.printStackTrace(e);
/*     */               }
/* 296 */             } else if (comp.getType() == 1024) {
/*     */               try
/*     */               {
/* 299 */                 Map map = comp.getContent();
/*     */                 
/* 301 */                 String name = new String((byte[])map.get("name"));
/*     */                 
/* 303 */                 UIManager ui_manager = StaticUtilities.getUIManager(120000L);
/*     */                 
/* 305 */                 String details = MessageText.getString("custom.settings.import", new String[] { name });
/*     */                 
/*     */ 
/*     */ 
/* 309 */                 long res = ui_manager.showMessageBox("custom.settings.import.title", "!" + details + "!", 12L);
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 314 */                 if (res == 4L)
/*     */                 {
/* 316 */                   Map<String, Object> config = (Map)map.get("settings");
/*     */                   
/* 318 */                   int num_set = 0;
/*     */                   
/* 320 */                   for (Map.Entry<String, Object> entry : config.entrySet())
/*     */                   {
/* 322 */                     String key = (String)entry.getKey();
/* 323 */                     Object value = entry.getValue();
/*     */                     
/* 325 */                     if ((value instanceof Long))
/*     */                     {
/* 327 */                       COConfigurationManager.setParameter(key, ((Long)value).longValue());
/*     */                     }
/* 329 */                     else if ((value instanceof byte[]))
/*     */                     {
/* 331 */                       COConfigurationManager.setParameter(key, (byte[])value);
/*     */                     }
/* 333 */                     else if ((value instanceof List))
/*     */                     {
/* 335 */                       COConfigurationManager.setParameter(key, (List)value);
/*     */                     }
/* 337 */                     else if ((value instanceof Map))
/*     */                     {
/* 339 */                       COConfigurationManager.setParameter(key, (Map)value);
/*     */                     }
/*     */                     else
/*     */                     {
/* 343 */                       Debug.out("Unsupported entry: " + key + "=" + value);
/*     */                     }
/*     */                     
/* 346 */                     num_set++;
/*     */                   }
/*     */                   
/*     */ 
/* 350 */                   Long l_restart = (Long)map.get("restart");
/*     */                   
/* 352 */                   boolean restart = (l_restart != null) && (l_restart.longValue() != 0L);
/*     */                   
/* 354 */                   String restart_text = "";
/*     */                   
/* 356 */                   if (restart)
/*     */                   {
/* 358 */                     restart_text = "\r\n\r\n" + MessageText.getString("ConfigView.section.security.restart.title");
/*     */                   }
/*     */                   
/* 361 */                   String res_details = MessageText.getString("custom.settings.import.res", new String[] { String.valueOf(num_set), restart_text });
/*     */                   
/*     */ 
/*     */ 
/* 365 */                   ui_manager.showMessageBox("custom.settings.import.res.title", "!" + res_details + "!", 1L);
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 371 */                 comp.setProcessed();
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 375 */                 Debug.printStackTrace(e);
/*     */               }
/*     */               
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 382 */     });
/* 383 */     File user_dir = FileUtil.getUserFile("custom");
/*     */     
/* 385 */     File app_dir = FileUtil.getApplicationFile("custom");
/*     */     
/* 387 */     loadCustomizations(app_dir);
/*     */     
/* 389 */     if (!user_dir.equals(app_dir))
/*     */     {
/* 391 */       loadCustomizations(user_dir);
/*     */     }
/*     */     
/* 394 */     String active = COConfigurationManager.getStringParameter("customization.active.name", "");
/*     */     
/* 396 */     if (this.customization_file_map.get(active) == null)
/*     */     {
/*     */ 
/*     */ 
/* 400 */       Iterator it = this.customization_file_map.keySet().iterator();
/*     */       
/* 402 */       while (it.hasNext())
/*     */       {
/* 404 */         String name = (String)it.next();
/*     */         
/* 406 */         String version_key = "customization.name." + name + ".version";
/*     */         
/* 408 */         String existing_version = COConfigurationManager.getStringParameter(version_key, "0");
/*     */         
/* 410 */         if (existing_version.equals("0"))
/*     */         {
/* 412 */           active = name;
/*     */           
/* 414 */           String version = ((String[])(String[])this.customization_file_map.get(name))[0];
/*     */           
/* 416 */           COConfigurationManager.setParameter("customization.active.name", active);
/*     */           
/* 418 */           COConfigurationManager.setParameter(version_key, version);
/*     */           
/* 420 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 425 */     synchronized (this)
/*     */     {
/* 427 */       this.current_customization_name = active;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void loadCustomizations(File dir)
/*     */   {
/* 435 */     if (dir.isDirectory())
/*     */     {
/* 437 */       File[] files = dir.listFiles();
/*     */       
/* 439 */       if (files != null)
/*     */       {
/* 441 */         for (int i = 0; i < files.length; i++)
/*     */         {
/* 443 */           File file = files[i];
/*     */           
/* 445 */           String name = file.getName();
/*     */           
/* 447 */           if (!name.endsWith(".zip"))
/*     */           {
/* 449 */             if (!name.contains(".config"))
/*     */             {
/* 451 */               logInvalid(file);
/*     */             }
/*     */             
/*     */           }
/*     */           else
/*     */           {
/* 457 */             String base = name.substring(0, name.length() - 4);
/*     */             
/* 459 */             int u_pos = base.lastIndexOf('_');
/*     */             
/* 461 */             if (u_pos == -1)
/*     */             {
/* 463 */               logInvalid(file);
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/* 468 */               String lhs = base.substring(0, u_pos).trim();
/* 469 */               String rhs = base.substring(u_pos + 1).trim();
/*     */               
/* 471 */               if ((lhs.length() == 0) || (!Constants.isValidVersionFormat(rhs)))
/*     */               {
/* 473 */                 logInvalid(file);
/*     */ 
/*     */               }
/*     */               else
/*     */               {
/* 478 */                 String[] details = (String[])this.customization_file_map.get(lhs);
/*     */                 
/* 480 */                 if (details == null)
/*     */                 {
/* 482 */                   this.customization_file_map.put(lhs, new String[] { rhs, file.getAbsolutePath() });
/*     */                 }
/*     */                 else
/*     */                 {
/* 486 */                   String old_version = details[0];
/*     */                   
/* 488 */                   if (Constants.compareVersions(old_version, rhs) < 0)
/*     */                   {
/* 490 */                     this.customization_file_map.put(lhs, new String[] { rhs, file.getAbsolutePath() });
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected void logInvalid(File file) {
/* 502 */     Debug.out("Invalid customization file name '" + file.getAbsolutePath() + "' - format must be <name>_<version>.zip where version is numeric and dot separated");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void importCustomization(Map map)
/*     */     throws CustomizationException
/*     */   {
/*     */     try
/*     */     {
/* 512 */       String name = new String((byte[])map.get("name"), "UTF-8");
/*     */       
/* 514 */       String version = new String((byte[])map.get("version"), "UTF-8");
/*     */       
/* 516 */       if (!Constants.isValidVersionFormat(version))
/*     */       {
/* 518 */         throw new CustomizationException("Invalid version specification: " + version);
/*     */       }
/*     */       
/* 521 */       byte[] data = (byte[])map.get("data");
/*     */       
/* 523 */       File user_dir = FileUtil.getUserFile("custom");
/*     */       
/* 525 */       if (!user_dir.exists())
/*     */       {
/* 527 */         user_dir.mkdirs();
/*     */       }
/*     */       
/* 530 */       File target = new File(user_dir, name + "_" + version + ".zip");
/*     */       
/* 532 */       if (!target.exists())
/*     */       {
/* 534 */         if (!FileUtil.writeBytesAsFile2(target.getAbsolutePath(), data))
/*     */         {
/* 536 */           throw new CustomizationException("Failed to save customization to " + target);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (CustomizationException e) {
/* 541 */       throw e;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 545 */       throw new CustomizationException("Failed to import customization", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void exportCustomization(CustomizationImpl cust, File to_file)
/*     */     throws CustomizationException
/*     */   {
/* 556 */     if (to_file.isDirectory())
/*     */     {
/* 558 */       to_file = new File(to_file, cust.getName() + "_" + cust.getVersion() + ".vuze");
/*     */     }
/*     */     
/* 561 */     if (!to_file.getName().endsWith(".vuze"))
/*     */     {
/* 563 */       to_file = new File(to_file.getParentFile(), to_file.getName() + ".vuze");
/*     */     }
/*     */     try
/*     */     {
/* 567 */       Map contents = new HashMap();
/*     */       
/* 569 */       byte[] data = FileUtil.readFileAsByteArray(cust.getContents());
/*     */       
/* 571 */       contents.put("name", cust.getName());
/* 572 */       contents.put("version", cust.getVersion());
/* 573 */       contents.put("data", data);
/*     */       
/* 575 */       VuzeFile vf = VuzeFileHandler.getSingleton().create();
/*     */       
/* 577 */       vf.addComponent(64, contents);
/*     */       
/*     */ 
/*     */ 
/* 581 */       vf.write(to_file);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 585 */       throw new CustomizationException("Failed to export customization", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Customization getActiveCustomization()
/*     */   {
/* 592 */     synchronized (this)
/*     */     {
/* 594 */       if (this.current_customization == null)
/*     */       {
/* 596 */         if (this.current_customization_name != null)
/*     */         {
/* 598 */           String[] entry = (String[])this.customization_file_map.get(this.current_customization_name);
/*     */           
/* 600 */           if (entry != null) {
/*     */             try
/*     */             {
/* 603 */               this.current_customization = new CustomizationImpl(this, this.current_customization_name, entry[0], new File(entry[1]));
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 610 */               SimpleTimer.addEvent("Custom:clear", SystemTime.getCurrentTime() + 120000L, new TimerEventPerformer()
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/*     */                 public void perform(TimerEvent event)
/*     */                 {
/*     */ 
/*     */ 
/* 619 */                   synchronized (CustomizationManagerImpl.this)
/*     */                   {
/* 621 */                     CustomizationManagerImpl.this.current_customization = null;
/*     */                   }
/*     */                 }
/*     */               });
/*     */             }
/*     */             catch (CustomizationException e)
/*     */             {
/* 628 */               e.printStackTrace();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 634 */       return this.current_customization;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Customization[] getCustomizations()
/*     */   {
/* 641 */     List result = new ArrayList();
/*     */     
/* 643 */     synchronized (this)
/*     */     {
/* 645 */       Iterator it = this.customization_file_map.entrySet().iterator();
/*     */       
/* 647 */       while (it.hasNext())
/*     */       {
/* 649 */         Map.Entry entry = (Map.Entry)it.next();
/*     */         
/* 651 */         String name = (String)entry.getKey();
/* 652 */         String[] bits = (String[])entry.getValue();
/*     */         
/* 654 */         String version = bits[0];
/* 655 */         File file = new File(bits[1]);
/*     */         
/*     */         try
/*     */         {
/* 659 */           CustomizationImpl cust = new CustomizationImpl(this, name, version, file);
/*     */           
/* 661 */           result.add(cust);
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 668 */     return (Customization[])result.toArray(new Customization[result.size()]);
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
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try
/*     */     {
/* 690 */       VuzeFile vf = VuzeFileHandler.getSingleton().create();
/*     */       
/* 692 */       Map config = new HashMap();
/*     */       
/* 694 */       List list = new ArrayList();
/*     */       
/* 696 */       list.add("trout");
/* 697 */       list.add(Integer.valueOf(45));
/*     */       
/* 699 */       config.put("test.a10", "Hello mum");
/* 700 */       config.put("test.a11", new Long(100L));
/* 701 */       config.put("test.a13", list);
/*     */       
/* 703 */       Map map = new HashMap();
/*     */       
/* 705 */       map.put("name", "My Proxy Settings");
/* 706 */       map.put("settings", config);
/* 707 */       map.put("restart", new Long(1L));
/*     */       
/* 709 */       vf.addComponent(1024, map);
/*     */       
/* 711 */       vf.write(new File("C:\\temp\\p_config.vuze"));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 715 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/custom/impl/CustomizationManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */