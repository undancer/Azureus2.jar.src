/*     */ package org.gudy.azureus2.pluginsimpl.update.sf.impl2;
/*     */ 
/*     */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetails;
/*     */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetailsException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SFPluginDetailsImpl
/*     */   implements SFPluginDetails
/*     */ {
/*     */   private SFPluginDetailsLoaderImpl loader;
/*     */   private boolean fully_loaded;
/*     */   private String id;
/*     */   private String name;
/*     */   private String version;
/*     */   private String category;
/*     */   private String download_url;
/*     */   private String author;
/*     */   private String cvs_version;
/*     */   private String cvs_download_url;
/*     */   private String desc;
/*     */   private String comment;
/*     */   private String info_url;
/*     */   
/*     */   protected SFPluginDetailsImpl(SFPluginDetailsLoaderImpl _loader, String _id, String _version, String _cvs_version, String _name, String _category)
/*     */   {
/*  58 */     this.loader = _loader;
/*  59 */     this.id = _id;
/*  60 */     this.version = _version;
/*  61 */     this.cvs_version = _cvs_version;
/*  62 */     this.name = _name;
/*  63 */     this.category = _category;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setDetails(String _download_url, String _author, String _cvs_download_url, String _desc, String _comment, String _info_url)
/*     */   {
/*  75 */     this.fully_loaded = true;
/*     */     
/*  77 */     this.download_url = _download_url;
/*  78 */     this.author = _author;
/*  79 */     this.cvs_download_url = _cvs_download_url;
/*  80 */     this.desc = _desc;
/*  81 */     this.comment = _comment;
/*  82 */     this.info_url = _info_url;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isFullyLoaded()
/*     */   {
/*  88 */     return this.fully_loaded;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void checkLoaded()
/*     */     throws SFPluginDetailsException
/*     */   {
/*  96 */     if (!this.fully_loaded)
/*     */     {
/*  98 */       this.loader.loadPluginDetails(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getId()
/*     */   {
/* 105 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 111 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getCategory()
/*     */   {
/* 117 */     return this.category;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getVersion()
/*     */   {
/* 123 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getDownloadURL()
/*     */     throws SFPluginDetailsException
/*     */   {
/* 131 */     checkLoaded();
/*     */     
/* 133 */     return this.download_url;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getAuthor()
/*     */     throws SFPluginDetailsException
/*     */   {
/* 141 */     checkLoaded();
/*     */     
/* 143 */     return this.author;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getCVSVersion()
/*     */     throws SFPluginDetailsException
/*     */   {
/* 151 */     return this.cvs_version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getCVSDownloadURL()
/*     */     throws SFPluginDetailsException
/*     */   {
/* 159 */     checkLoaded();
/*     */     
/* 161 */     return this.cvs_download_url;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getDescription()
/*     */     throws SFPluginDetailsException
/*     */   {
/* 169 */     checkLoaded();
/*     */     
/* 171 */     return this.desc;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getComment()
/*     */     throws SFPluginDetailsException
/*     */   {
/* 179 */     checkLoaded();
/*     */     
/* 181 */     return this.comment;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getRelativeURLBase()
/*     */   {
/* 187 */     return this.loader.getRelativeURLBase();
/*     */   }
/*     */   
/*     */   public String getInfoURL() {
/* 191 */     return this.info_url;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/update/sf/impl2/SFPluginDetailsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */