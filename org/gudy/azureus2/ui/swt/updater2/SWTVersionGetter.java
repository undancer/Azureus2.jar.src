/*     */ package org.gudy.azureus2.ui.swt.updater2;
/*     */ 
/*     */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEVerifier;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.plugins.update.UpdateChecker;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SWTVersionGetter
/*     */ {
/*  43 */   private static final LogIDs LOGID = LogIDs.GUI;
/*     */   
/*     */   private String platform;
/*     */   
/*     */   private int currentVersion;
/*     */   
/*     */   private int latestVersion;
/*     */   
/*     */   private UpdateChecker checker;
/*     */   
/*     */   private String[] mirrors;
/*     */   private String infoURL;
/*     */   
/*     */   public SWTVersionGetter(UpdateChecker _checker)
/*     */   {
/*  58 */     this.platform = SWT.getPlatform();
/*  59 */     this.currentVersion = SWT.getVersion();
/*     */     
/*     */ 
/*  62 */     this.latestVersion = 0;
/*  63 */     this.checker = _checker;
/*     */   }
/*     */   
/*     */   public boolean needsUpdate() {
/*     */     try {
/*  68 */       downloadLatestVersion();
/*     */       
/*  70 */       String msg = "SWT: current version = " + this.currentVersion + ", latest version = " + this.latestVersion;
/*     */       
/*  72 */       this.checker.reportProgress(msg);
/*     */       
/*  74 */       if (Logger.isEnabled()) {
/*  75 */         Logger.log(new LogEvent(LOGID, msg));
/*     */       }
/*  77 */       return this.latestVersion > this.currentVersion;
/*     */     } catch (Exception e) {
/*  79 */       e.printStackTrace(); }
/*  80 */     return false;
/*     */   }
/*     */   
/*     */   private void downloadLatestVersion()
/*     */   {
/*  85 */     if (Utils.isCarbon) {
/*  86 */       return;
/*     */     }
/*  88 */     if (Logger.isEnabled()) {
/*  89 */       Logger.log(new LogEvent(LOGID, "Requesting latest SWT version and url from version check client."));
/*     */     }
/*     */     
/*  92 */     Map reply = VersionCheckClient.getSingleton().getVersionCheckInfo("sw");
/*     */     
/*  94 */     String msg = "SWT version check received:";
/*     */     
/*  96 */     boolean done = false;
/*     */     
/*  98 */     if (Constants.isOSX_10_5_OrHigher)
/*     */     {
/* 100 */       byte[] version_bytes = (byte[])reply.get("swt_version_cocoa");
/*     */       
/* 102 */       if (version_bytes != null)
/*     */       {
/* 104 */         this.latestVersion = Integer.parseInt(new String(version_bytes));
/*     */         
/* 106 */         msg = msg + " version=" + this.latestVersion;
/*     */         
/* 108 */         byte[] url_bytes = (byte[])reply.get("swt_url_cocoa");
/*     */         
/* 110 */         if (url_bytes != null)
/*     */         {
/* 112 */           this.mirrors = new String[] { new String(url_bytes) };
/*     */           
/* 114 */           msg = msg + " url=" + this.mirrors[0];
/*     */         }
/*     */         
/* 117 */         done = true;
/*     */       }
/*     */     }
/*     */     
/* 121 */     if (!done)
/*     */     {
/* 123 */       byte[] version_bytes = (byte[])reply.get("swt_version");
/* 124 */       if (version_bytes != null) {
/* 125 */         this.latestVersion = Integer.parseInt(new String(version_bytes));
/* 126 */         msg = msg + " version=" + this.latestVersion;
/*     */       }
/*     */       
/* 129 */       byte[] url_bytes = (byte[])reply.get("swt_url");
/* 130 */       if (url_bytes != null) {
/* 131 */         this.mirrors = new String[] { new String(url_bytes) };
/* 132 */         msg = msg + " url=" + this.mirrors[0];
/*     */       }
/*     */     }
/*     */     
/* 136 */     byte[] info_bytes = (byte[])reply.get("swt_info_url");
/*     */     
/* 138 */     if (info_bytes != null)
/*     */     {
/* 140 */       byte[] sig = (byte[])reply.get("swt_info_sig");
/*     */       
/* 142 */       if (sig == null)
/*     */       {
/* 144 */         Logger.log(new LogEvent(LogIDs.LOGGER, "swt info signature check failed - missing signature"));
/*     */       }
/*     */       else {
/*     */         try
/*     */         {
/* 149 */           this.infoURL = new String(info_bytes);
/*     */           try
/*     */           {
/* 152 */             AEVerifier.verifyData(this.infoURL, sig);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 156 */             Logger.log(new LogEvent(LogIDs.LOGGER, "swt info signature check failed", e));
/*     */             
/* 158 */             this.infoURL = null;
/*     */           }
/*     */         }
/*     */         catch (Exception e) {
/* 162 */           Logger.log(new LogEvent(LOGID, "swt info_url", e));
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 167 */     if (Logger.isEnabled()) {
/* 168 */       Logger.log(new LogEvent(LOGID, msg));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getLatestVersion()
/*     */   {
/* 178 */     return this.latestVersion;
/*     */   }
/*     */   
/*     */   public int getCurrentVersion() {
/* 182 */     return this.currentVersion;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getPlatform()
/*     */   {
/* 188 */     return this.platform;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String[] getMirrors()
/*     */   {
/* 195 */     return this.mirrors;
/*     */   }
/*     */   
/*     */   public String getInfoURL() {
/* 199 */     return this.infoURL;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/updater2/SWTVersionGetter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */