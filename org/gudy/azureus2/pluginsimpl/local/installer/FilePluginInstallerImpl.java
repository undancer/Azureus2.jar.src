/*     */ package org.gudy.azureus2.pluginsimpl.local.installer;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.util.Properties;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipInputStream;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.Plugin;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.plugins.installer.FilePluginInstaller;
/*     */ import org.gudy.azureus2.plugins.update.UpdatableComponent;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*     */ import org.gudy.azureus2.plugins.update.UpdateChecker;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.pluginsimpl.update.PluginUpdatePlugin;
/*     */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetailsLoader;
/*     */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetailsLoaderFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FilePluginInstallerImpl
/*     */   extends InstallablePluginImpl
/*     */   implements FilePluginInstaller
/*     */ {
/*     */   protected PluginInstallerImpl installer;
/*     */   protected File file;
/*     */   protected String id;
/*     */   protected String version;
/*     */   protected String name;
/*     */   protected boolean is_jar;
/*     */   
/*     */   protected FilePluginInstallerImpl(PluginInstallerImpl _installer, File _file)
/*     */     throws PluginException
/*     */   {
/*  69 */     super(_installer);
/*     */     
/*  71 */     this.installer = _installer;
/*  72 */     this.file = _file;
/*     */     
/*  74 */     String name = this.file.getName();
/*     */     
/*  76 */     int pos = name.lastIndexOf(".");
/*     */     
/*  78 */     boolean ok = false;
/*     */     
/*  80 */     if (pos != -1)
/*     */     {
/*  82 */       String prefix = name.substring(0, pos);
/*  83 */       String suffix = name.substring(pos + 1);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  88 */       if (prefix.lastIndexOf("_src") != -1) {
/*  89 */         if (prefix.endsWith("_src")) {
/*  90 */           prefix = prefix.substring(0, prefix.length() - 4);
/*     */         }
/*     */         else {
/*  93 */           int src_bit_pos = prefix.lastIndexOf("_src");
/*  94 */           prefix = prefix.substring(0, src_bit_pos) + prefix.substring(src_bit_pos + 1);
/*     */         }
/*     */       }
/*     */       
/*  98 */       if ((suffix.toLowerCase(MessageText.LOCALE_ENGLISH).equals("jar")) || (suffix.toLowerCase(MessageText.LOCALE_ENGLISH).equals("zip")))
/*     */       {
/*     */ 
/* 101 */         this.is_jar = suffix.toLowerCase(MessageText.LOCALE_ENGLISH).equals("jar");
/*     */         
/*     */ 
/*     */ 
/* 105 */         Properties properties = null;
/*     */         
/* 107 */         ZipInputStream zis = null;
/*     */         try
/*     */         {
/* 110 */           zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(this.file)));
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 115 */           while (properties == null)
/*     */           {
/* 117 */             ZipEntry entry = zis.getNextEntry();
/*     */             
/* 119 */             if (entry == null) {
/*     */               break;
/*     */             }
/*     */             
/*     */ 
/* 124 */             String zip_name = entry.getName().toLowerCase(MessageText.LOCALE_ENGLISH);
/*     */             
/*     */ 
/*     */ 
/* 128 */             if ((zip_name.equals("plugin.properties")) || (zip_name.endsWith("/plugin.properties")))
/*     */             {
/* 130 */               properties = new Properties();
/*     */               
/* 132 */               properties.load(zis);
/*     */             }
/* 134 */             else if (zip_name.endsWith(".jar"))
/*     */             {
/* 136 */               ZipInputStream zis2 = new ZipInputStream(zis);
/*     */               
/* 138 */               while (properties == null)
/*     */               {
/* 140 */                 ZipEntry entry2 = zis2.getNextEntry();
/*     */                 
/* 142 */                 if (entry2 == null) {
/*     */                   break;
/*     */                 }
/*     */                 
/*     */ 
/* 147 */                 String zip_name2 = entry2.getName().toLowerCase(MessageText.LOCALE_ENGLISH);
/*     */                 
/*     */ 
/*     */ 
/* 151 */                 if (zip_name2.equals("plugin.properties"))
/*     */                 {
/* 153 */                   properties = new Properties();
/*     */                   
/* 155 */                   properties.load(zis2);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 167 */           if (zis != null) {
/*     */             try
/*     */             {
/* 170 */               zis.close();
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 174 */               Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 179 */           pos = prefix.lastIndexOf("_");
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 163 */           throw new PluginException("Failed to read plugin file", e);
/*     */         }
/*     */         finally
/*     */         {
/* 167 */           if (zis != null) {
/*     */             try
/*     */             {
/* 170 */               zis.close();
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 174 */               Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 180 */         String filename_id = null;String filename_version = null;
/* 181 */         if (pos != -1) {
/* 182 */           filename_id = prefix.substring(0, pos);
/* 183 */           filename_version = prefix.substring(pos + 1);
/*     */         }
/*     */         
/* 186 */         if (properties == null)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 191 */           if (filename_id != null)
/*     */           {
/* 193 */             this.id = filename_id;
/* 194 */             this.version = filename_version;
/*     */             
/* 196 */             PluginInterface pi = this.installer.getPluginManager().getPluginInterfaceByID(this.id);
/*     */             
/* 198 */             ok = (pi != null) && ((pi.getPluginDirectoryName() == null) || (pi.getPluginDirectoryName().length() == 0));
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 203 */           if (!ok)
/*     */           {
/* 205 */             throw new PluginException("Mandatory file 'plugin.properties' not found in plugin file");
/*     */           }
/*     */           
/*     */         }
/*     */         else
/*     */         {
/* 211 */           PluginInitializer.checkJDKVersion("", properties, false);
/* 212 */           PluginInitializer.checkAzureusVersion("", properties, false);
/*     */           
/* 214 */           this.id = properties.getProperty("plugin.id");
/* 215 */           this.version = properties.getProperty("plugin.version");
/*     */           
/*     */ 
/* 218 */           String prop_version = this.version;
/* 219 */           if ((prop_version != null) && (filename_version != null) && (!filename_version.equals(prop_version))) {
/* 220 */             throw new PluginException("inconsistent versions [file=" + filename_version + ", prop=" + prop_version + "]");
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 225 */         if (this.id == null)
/*     */         {
/*     */ 
/*     */ 
/* 229 */           String plugin_class = properties.getProperty("plugin.class");
/*     */           
/* 231 */           if (plugin_class == null)
/*     */           {
/* 233 */             String plugin_classes = properties.getProperty("plugin.classes");
/*     */             
/* 235 */             if (plugin_classes != null)
/*     */             {
/* 237 */               int semi_pos = plugin_classes.indexOf(";");
/*     */               
/* 239 */               if (semi_pos == -1)
/*     */               {
/* 241 */                 plugin_class = plugin_classes;
/*     */               }
/*     */               else
/*     */               {
/* 245 */                 plugin_class = plugin_classes.substring(0, semi_pos);
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 250 */           if (plugin_class != null) {
/*     */             try
/*     */             {
/* 253 */               PluginInterface pi = this.installer.getPluginManager().getPluginInterfaceByClass(plugin_class);
/*     */               
/* 255 */               if (pi != null)
/*     */               {
/* 257 */                 this.id = pi.getPluginID();
/*     */               }
/*     */             }
/*     */             catch (Throwable ignore) {}
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 265 */         pos = prefix.lastIndexOf("_");
/*     */         
/* 267 */         if (pos != -1)
/*     */         {
/* 269 */           this.id = (this.id == null ? prefix.substring(0, pos) : this.id);
/*     */           
/*     */ 
/*     */           try
/*     */           {
/* 274 */             SFPluginDetailsLoader loader = SFPluginDetailsLoaderFactory.getSingleton();
/*     */             
/* 276 */             String[] ids = loader.getPluginIDs();
/*     */             
/* 278 */             for (int i = 0; i < ids.length; i++)
/*     */             {
/* 280 */               if (ids[i].equalsIgnoreCase(this.id))
/*     */               {
/* 282 */                 this.id = ids[i];
/*     */                 
/* 284 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 289 */             Debug.printStackTrace(e);
/*     */           }
/*     */           
/* 292 */           this.version = (this.version == null ? prefix.substring(pos + 1) : this.version);
/*     */         }
/*     */         
/*     */ 
/* 296 */         this.name = this.id;
/*     */         
/* 298 */         if (properties != null)
/*     */         {
/* 300 */           String plugin_name = properties.getProperty("plugin.name");
/*     */           
/* 302 */           if (plugin_name != null)
/*     */           {
/* 304 */             this.name = plugin_name;
/*     */           }
/*     */         }
/*     */         
/* 308 */         ok = (this.id != null) && (this.version != null);
/*     */       }
/*     */     }
/*     */     
/* 312 */     if (!ok)
/*     */     {
/* 314 */       throw new PluginException("Invalid plugin file name: must be of form <pluginid>_<version>.[jar|zip]");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public File getFile()
/*     */   {
/* 321 */     return this.file;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getId()
/*     */   {
/* 327 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getVersion()
/*     */   {
/* 333 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 339 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 345 */     return this.file.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getRelativeURLBase()
/*     */   {
/* 351 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addUpdate(UpdateCheckInstance inst, final PluginUpdatePlugin plugin_update_plugin, Plugin plugin, final PluginInterface plugin_interface)
/*     */   {
/* 361 */     inst.addUpdatableComponent(new UpdatableComponent()
/*     */     {
/*     */ 
/*     */       public String getName()
/*     */       {
/*     */ 
/* 367 */         return FilePluginInstallerImpl.this.name;
/*     */       }
/*     */       
/*     */ 
/*     */       public int getMaximumCheckTime()
/*     */       {
/* 373 */         return 0;
/*     */       }
/*     */       
/*     */ 
/*     */       public void checkForUpdate(UpdateChecker checker)
/*     */       {
/*     */         try
/*     */         {
/* 381 */           ResourceDownloader rd = plugin_interface.getUtilities().getResourceDownloaderFactory().create(FilePluginInstallerImpl.this.file);
/*     */           
/*     */ 
/* 384 */           plugin_update_plugin.addUpdate(plugin_interface, checker, getName(), new String[] { "Installation from file: " + FilePluginInstallerImpl.this.file.toString() }, "", FilePluginInstallerImpl.this.version, rd, FilePluginInstallerImpl.this.is_jar, plugin_interface.getPluginState().isUnloadable() ? 1 : 2, false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*     */         finally
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 398 */           checker.completed(); } } }, false);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/installer/FilePluginInstallerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */