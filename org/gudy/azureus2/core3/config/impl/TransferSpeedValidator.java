/*     */ package org.gudy.azureus2.core3.config.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManager;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.PriorityParameterListener;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class TransferSpeedValidator
/*     */ {
/*     */   public static final String AUTO_UPLOAD_ENABLED_CONFIGKEY = "Auto Upload Speed Enabled";
/*     */   public static final String AUTO_UPLOAD_SEEDING_ENABLED_CONFIGKEY = "Auto Upload Speed Seeding Enabled";
/*     */   public static final String UPLOAD_CONFIGKEY = "Max Upload Speed KBs";
/*     */   public static final String UPLOAD_SEEDING_CONFIGKEY = "Max Upload Speed Seeding KBs";
/*     */   public static final String DOWNLOAD_CONFIGKEY = "Max Download Speed KBs";
/*     */   public static final String UPLOAD_SEEDING_ENABLED_CONFIGKEY = "enable.seedingonly.upload.rate";
/*  45 */   public static final String[] CONFIG_PARAMS = { "Auto Upload Speed Enabled", "Auto Upload Speed Seeding Enabled", "Max Upload Speed KBs", "Max Upload Speed Seeding KBs", "Max Download Speed KBs", "enable.seedingonly.upload.rate" };
/*     */   
/*     */ 
/*     */   private final String configKey;
/*     */   
/*     */ 
/*     */   private final Number configValue;
/*     */   
/*     */ 
/*     */   private static boolean auto_upload_enabled;
/*     */   
/*     */ 
/*     */   private static boolean auto_upload_seeding_enabled;
/*     */   
/*     */   private static boolean seeding_upload_enabled;
/*     */   
/*     */ 
/*     */   static
/*     */   {
/*  64 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "enable.seedingonly.upload.rate", "Auto Upload Speed Enabled", "Auto Upload Speed Seeding Enabled" }, new PriorityParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  76 */         if ((parameterName == null) || (parameterName.equals("enable.seedingonly.upload.rate"))) {
/*  77 */           TransferSpeedValidator.access$002(COConfigurationManager.getBooleanParameter("enable.seedingonly.upload.rate"));
/*     */         }
/*  79 */         if ((parameterName == null) || (parameterName.equals("Auto Upload Speed Enabled"))) {
/*  80 */           TransferSpeedValidator.access$102(COConfigurationManager.getBooleanParameter("Auto Upload Speed Enabled"));
/*     */         }
/*  82 */         if ((parameterName == null) || (parameterName.equals("Auto Upload Speed Seeding Enabled"))) {
/*  83 */           TransferSpeedValidator.access$202(COConfigurationManager.getBooleanParameter("Auto Upload Speed Seeding Enabled"));
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TransferSpeedValidator(String configKey, Number value)
/*     */   {
/*  96 */     this.configKey = configKey;
/*  97 */     this.configValue = value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Object validate(String configKey, Number value)
/*     */   {
/* 107 */     int newValue = value.intValue();
/*     */     
/* 109 */     if (newValue < 0)
/*     */     {
/* 111 */       newValue = 0;
/*     */     }
/*     */     
/* 114 */     if (configKey == "Max Upload Speed KBs")
/*     */     {
/* 116 */       int downValue = COConfigurationManager.getIntParameter("Max Download Speed KBs");
/*     */       
/* 118 */       if ((newValue != 0) && (newValue < 5) && ((downValue == 0) || (downValue > newValue * 2)))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 124 */         newValue = (downValue + 1) / 2;
/*     */       }
/*     */       
/*     */     }
/* 128 */     else if (configKey == "Max Download Speed KBs")
/*     */     {
/* 130 */       int upValue = COConfigurationManager.getIntParameter("Max Upload Speed KBs");
/*     */       
/* 132 */       if ((upValue != 0) && (upValue < 5))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 137 */         if (newValue > upValue * 2)
/*     */         {
/* 139 */           newValue = upValue * 2;
/*     */ 
/*     */         }
/* 142 */         else if (newValue == 0)
/*     */         {
/* 144 */           newValue = upValue * 2;
/*     */         }
/*     */       }
/*     */     }
/* 148 */     else if (configKey != "Max Upload Speed Seeding KBs")
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 154 */       throw new IllegalArgumentException("Invalid Configuation Key; use key for max upload and max download");
/*     */     }
/*     */     
/* 157 */     return new Integer(newValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object getValue()
/*     */   {
/* 167 */     return validate(this.configKey, this.configValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getActiveUploadParameter(GlobalManager gm)
/*     */   {
/* 174 */     if ((seeding_upload_enabled) && (gm.isSeedingOnly()))
/*     */     {
/* 176 */       return "Max Upload Speed Seeding KBs";
/*     */     }
/*     */     
/*     */ 
/* 180 */     return "Max Upload Speed KBs";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getDownloadParameter()
/*     */   {
/* 187 */     return "Max Download Speed KBs";
/*     */   }
/*     */   
/*     */ 
/*     */   public static int getGlobalDownloadRateLimitBytesPerSecond()
/*     */   {
/* 193 */     return COConfigurationManager.getIntParameter(getDownloadParameter()) * 1024;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void setGlobalDownloadRateLimitBytesPerSecond(int bytes_per_second)
/*     */   {
/* 200 */     COConfigurationManager.setParameter(getDownloadParameter(), (bytes_per_second + 1023) / 1024);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isAutoUploadAvailable(AzureusCore core)
/*     */   {
/* 207 */     if (core == null) {
/* 208 */       return false;
/*     */     }
/* 210 */     SpeedManager speedManager = core.getSpeedManager();
/* 211 */     return speedManager == null ? false : speedManager.isAvailable();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isAutoSpeedActive(GlobalManager gm)
/*     */   {
/* 221 */     if (auto_upload_enabled) {
/* 222 */       return auto_upload_enabled;
/*     */     }
/*     */     
/* 225 */     if (gm.isSeedingOnly())
/*     */     {
/* 227 */       return auto_upload_seeding_enabled;
/*     */     }
/*     */     
/*     */ 
/* 231 */     return auto_upload_enabled;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getActiveAutoUploadParameter(GlobalManager gm)
/*     */   {
/* 239 */     if ((!auto_upload_enabled) && (gm.isSeedingOnly())) {
/* 240 */       return "Auto Upload Speed Seeding Enabled";
/*     */     }
/* 242 */     return "Auto Upload Speed Enabled";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/config/impl/TransferSpeedValidator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */