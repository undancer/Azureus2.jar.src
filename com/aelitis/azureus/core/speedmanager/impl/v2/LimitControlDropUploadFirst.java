/*     */ package com.aelitis.azureus.core.speedmanager.impl.v2;
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
/*     */ public class LimitControlDropUploadFirst
/*     */   implements LimitControl
/*     */ {
/*  24 */   private float valueUp = 0.5F;
/*     */   
/*     */   int upMax;
/*     */   int upCurr;
/*     */   int upMin;
/*     */   SaturatedMode upUsage;
/*  30 */   private float valueDown = 1.0F;
/*     */   
/*     */   int downMax;
/*     */   
/*     */   int downCurr;
/*     */   
/*     */   int downMin;
/*     */   SaturatedMode downUsage;
/*     */   TransferMode mode;
/*  39 */   float usedUpMaxDownloadMode = 0.6F;
/*     */   
/*  41 */   boolean isDownloadUnlimited = false;
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
/*     */   public void updateStatus(int currUpLimit, SaturatedMode uploadUsage, int currDownLimit, SaturatedMode downloadUsage, TransferMode transferMode)
/*     */   {
/*  56 */     this.upCurr = currUpLimit;
/*  57 */     this.upUsage = uploadUsage;
/*  58 */     this.downCurr = currDownLimit;
/*  59 */     this.downUsage = downloadUsage;
/*     */     
/*  61 */     this.mode = transferMode;
/*     */   }
/*     */   
/*     */   public void setDownloadUnlimitedMode(boolean isUnlimited) {
/*  65 */     this.isDownloadUnlimited = isUnlimited;
/*  66 */     if (isUnlimited) {
/*  67 */       this.valueDown = 1.0F;
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isDownloadUnlimitedMode() {
/*  72 */     return this.isDownloadUnlimited;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void updateLimits(int _upMax, int _upMin, int _downMax, int _downMin)
/*     */   {
/*  79 */     if (_upMax < 30720) {
/*  80 */       _upMax = 30720;
/*     */     }
/*  82 */     if (_downMax < 61440) {
/*  83 */       _downMax = 61440;
/*     */     }
/*     */     
/*  86 */     if (_downMax < _upMax) {
/*  87 */       _downMax = _upMax;
/*     */     }
/*     */     
/*  90 */     _upMin = SMConst.calculateMinUpload(_upMax);
/*  91 */     _downMin = SMConst.calculateMinDownload(_downMax);
/*     */     
/*     */ 
/*  94 */     this.upMax = _upMax;
/*  95 */     this.upMin = _upMin;
/*  96 */     this.downMax = _downMax;
/*  97 */     this.downMin = _downMin;
/*     */   }
/*     */   
/*     */ 
/*     */   private int usedUploadCapacity()
/*     */   {
/* 103 */     float usedUpMax = this.upMax;
/* 104 */     if (this.mode.getMode() == TransferMode.State.SEEDING) {
/* 105 */       usedUpMax = this.upMax;
/* 106 */     } else if (this.mode.getMode() == TransferMode.State.DOWNLOADING) {
/* 107 */       usedUpMax = this.upMax * this.usedUpMaxDownloadMode;
/* 108 */     } else if (this.mode.getMode() == TransferMode.State.DOWNLOAD_LIMIT_SEARCH) {
/* 109 */       usedUpMax = this.upMax * this.usedUpMaxDownloadMode;
/* 110 */     } else if (this.mode.getMode() == TransferMode.State.UPLOAD_LIMIT_SEARCH) {
/* 111 */       usedUpMax = this.upMax;
/*     */     }
/*     */     else {
/* 114 */       SpeedManagerLogger.trace("LimitControlDropUploadFirst -> unrecognized transfer mode. ");
/*     */     }
/*     */     
/* 117 */     return Math.round(usedUpMax);
/*     */   }
/*     */   
/*     */   public void updateSeedSettings(float downloadModeUsed)
/*     */   {
/* 122 */     if ((downloadModeUsed < 1.0F) && (downloadModeUsed > 0.1F)) {
/* 123 */       this.usedUpMaxDownloadMode = downloadModeUsed;
/* 124 */       SpeedManagerLogger.trace("LimitControlDropUploadFirst %used upload used while downloading: " + downloadModeUsed);
/*     */     }
/*     */   }
/*     */   
/*     */   public SMUpdate adjust(float amount)
/*     */   {
/* 130 */     boolean increase = true;
/* 131 */     if (amount < 0.0F) {
/* 132 */       increase = false;
/*     */     }
/*     */     
/* 135 */     float factor = amount / 10.0F;
/* 136 */     int usedUpMax = usedUploadCapacity();
/* 137 */     float gamma = usedUpMax / this.downMax;
/*     */     
/* 139 */     if (increase)
/*     */     {
/* 141 */       if (this.valueDown < 0.99F) {
/* 142 */         this.valueDown = calculateNewValue(this.valueDown, factor);
/*     */ 
/*     */       }
/* 145 */       else if (this.upUsage == SaturatedMode.AT_LIMIT) {
/* 146 */         this.valueUp = calculateNewValue(this.valueUp, gamma * 0.5F * factor);
/*     */       }
/*     */       
/*     */ 
/*     */     }
/* 151 */     else if (this.valueUp > 0.01F) {
/* 152 */       this.valueUp = calculateNewValue(this.valueUp, gamma * factor);
/*     */     } else {
/* 154 */       this.valueDown = calculateNewValue(this.valueDown, factor);
/*     */     }
/*     */     
/*     */ 
/* 158 */     return update();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private SMUpdate update()
/*     */   {
/* 165 */     int usedUpMax = usedUploadCapacity();
/*     */     
/*     */ 
/* 168 */     int upLimit = Math.round((usedUpMax - this.upMin) * this.valueUp + this.upMin);
/*     */     
/*     */ 
/* 171 */     if ((upLimit > this.upMax) || (Float.isNaN(this.valueUp))) {
/* 172 */       SpeedManagerLogger.trace("Limit - should upload have an unlimited condition? Setting to usedUpMax");
/* 173 */       upLimit = usedUpMax; }
/*     */     int downLimit;
/*     */     int downLimit;
/* 176 */     if (this.isDownloadUnlimited) {
/* 177 */       downLimit = 0;
/*     */     } else {
/* 179 */       downLimit = Math.round((this.downMax - this.downMin) * this.valueDown + this.downMin);
/*     */     }
/*     */     
/*     */ 
/* 183 */     if (this.valueDown == 1.0D) {
/* 184 */       downLimit = 0;
/*     */     }
/*     */     
/*     */ 
/* 188 */     StringBuilder msg = new StringBuilder(" create-update: valueUp=" + this.valueUp + ",upLimit=" + upLimit + ",valueDown=");
/* 189 */     if (this.valueDown == 1.0D) msg.append("_unlimited_"); else
/* 190 */       msg.append(this.valueDown);
/* 191 */     msg.append(",downLimit=").append(downLimit).append(",upMax=").append(this.upMax).append(",usedUpMax=").append(usedUpMax).append(",upMin=").append(this.upMin).append(",downMax=").append(this.downMax);
/*     */     
/* 193 */     msg.append(",downMin=").append(this.downMin).append(",transferMode=").append(this.mode.getString()).append(",isDownUnlimited=").append(this.isDownloadUnlimited);
/*     */     
/*     */ 
/* 196 */     SpeedManagerLogger.log(msg.toString());
/*     */     
/* 198 */     return new SMUpdate(upLimit, true, downLimit, true);
/*     */   }
/*     */   
/*     */   private float calculateNewValue(float curr, float amount)
/*     */   {
/* 203 */     if (Float.isNaN(curr)) {
/* 204 */       SpeedManagerLogger.trace("calculateNewValue - curr=NaN");
/*     */     }
/* 206 */     if (Float.isNaN(amount)) {
/* 207 */       SpeedManagerLogger.trace("calculateNewValue = amount=NaN");
/*     */     }
/*     */     
/* 210 */     curr += amount;
/* 211 */     if (curr > 1.0F) {
/* 212 */       curr = 1.0F;
/*     */     }
/* 214 */     if (curr < 0.0F) {
/* 215 */       curr = 0.0F;
/*     */     }
/*     */     
/* 218 */     if (Float.isNaN(curr)) {
/* 219 */       curr = 0.0F;
/*     */     }
/*     */     
/* 222 */     return curr;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/LimitControlDropUploadFirst.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */