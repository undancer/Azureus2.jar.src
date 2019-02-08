/*     */ package org.gudy.azureus2.ui.swt.views.stats;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManager;
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerFactory;
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerStats;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.ProgressBar;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.graphics.SpeedGraphic;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
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
/*     */ public class CacheView
/*     */   implements UISWTViewCoreEventListener
/*     */ {
/*     */   public static final String MSGID_PREFIX = "CacheView";
/*     */   CacheFileManagerStats stats;
/*     */   Composite panel;
/*     */   Label lblInUse;
/*     */   Label lblSize;
/*     */   Label lblPercentUsed;
/*     */   ProgressBar pbInUse;
/*     */   Label lblReadsFromCache;
/*     */   Label lblNumberReadsFromCache;
/*     */   Label lblAvgSizeFromCache;
/*     */   Label lblReadsFromFile;
/*     */   Label lblNumberReadsFromFile;
/*     */   Label lblAvgSizeFromFile;
/*     */   Label lblPercentReads;
/*     */   ProgressBar pbReads;
/*     */   Label lblWritesToCache;
/*     */   Label lblNumberWritesToCache;
/*     */   Label lblAvgSizeToCache;
/*     */   Label lblWritesToFile;
/*     */   Label lblNumberWritesToFile;
/*     */   Label lblAvgSizeToFile;
/*     */   Label lblPercentWrites;
/*     */   ProgressBar pbWrites;
/*     */   Canvas readsFromFile;
/*     */   Canvas readsFromCache;
/*     */   Canvas writesToCache;
/*     */   Canvas writesToFile;
/*     */   SpeedGraphic rffGraph;
/*     */   SpeedGraphic rfcGraph;
/*     */   SpeedGraphic wtcGraph;
/*     */   SpeedGraphic wtfGraph;
/*     */   private UISWTView swtView;
/*     */   
/*     */   public CacheView()
/*     */   {
/*     */     try
/*     */     {
/*  75 */       this.stats = CacheFileManagerFactory.getSingleton().getStats();
/*  76 */       this.rfcGraph = SpeedGraphic.getInstance();
/*  77 */       this.wtcGraph = SpeedGraphic.getInstance();
/*  78 */       this.rffGraph = SpeedGraphic.getInstance();
/*  79 */       this.wtfGraph = SpeedGraphic.getInstance();
/*     */     } catch (Exception e) {
/*  81 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */   private void initialize(Composite composite) {
/*  86 */     this.panel = new Composite(composite, 0);
/*  87 */     this.panel.setLayout(new GridLayout());
/*     */     
/*  89 */     generateGeneralGroup();
/*  90 */     generateReadsGroup();
/*  91 */     generateWritesGroup();
/*  92 */     generateSpeedGroup();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void generateGeneralGroup()
/*     */   {
/* 101 */     Group gCacheGeneral = new Group(this.panel, 0);
/* 102 */     Messages.setLanguageText(gCacheGeneral, "CacheView.general.title");
/* 103 */     Utils.setLayoutData(gCacheGeneral, new GridData(768));
/*     */     
/* 105 */     GridLayout layoutGeneral = new GridLayout();
/* 106 */     layoutGeneral.numColumns = 4;
/* 107 */     gCacheGeneral.setLayout(layoutGeneral);
/*     */     
/*     */ 
/* 110 */     Label lbl = new Label(gCacheGeneral, 0);
/* 111 */     GridData gridData = new GridData();
/* 112 */     gridData.widthHint = 100;
/* 113 */     Utils.setLayoutData(lbl, gridData);
/* 114 */     Messages.setLanguageText(lbl, "CacheView.general.inUse");
/*     */     
/* 116 */     this.lblInUse = new Label(gCacheGeneral, 0);
/* 117 */     gridData = new GridData();
/* 118 */     gridData.widthHint = 100;
/* 119 */     Utils.setLayoutData(this.lblInUse, gridData);
/*     */     
/* 121 */     this.pbInUse = new ProgressBar(gCacheGeneral, 256);
/* 122 */     gridData = new GridData(768);
/* 123 */     gridData.verticalSpan = 2;
/* 124 */     Utils.setLayoutData(this.pbInUse, gridData);
/* 125 */     this.pbInUse.setMinimum(0);
/* 126 */     this.pbInUse.setMaximum(1000);
/*     */     
/* 128 */     this.lblPercentUsed = new Label(gCacheGeneral, 0);
/* 129 */     gridData = new GridData();
/* 130 */     gridData.verticalSpan = 2;
/* 131 */     gridData.widthHint = 100;
/* 132 */     Utils.setLayoutData(this.lblPercentUsed, gridData);
/*     */     
/* 134 */     lbl = new Label(gCacheGeneral, 0);
/* 135 */     gridData = new GridData();
/* 136 */     gridData.widthHint = 100;
/* 137 */     Utils.setLayoutData(lbl, gridData);
/* 138 */     Messages.setLanguageText(lbl, "CacheView.general.size");
/*     */     
/* 140 */     this.lblSize = new Label(gCacheGeneral, 0);
/* 141 */     gridData = new GridData();
/* 142 */     gridData.widthHint = 100;
/* 143 */     Utils.setLayoutData(this.lblSize, gridData);
/*     */   }
/*     */   
/*     */ 
/*     */   private void generateReadsGroup()
/*     */   {
/* 149 */     Group gCacheReads = new Group(this.panel, 0);
/* 150 */     Messages.setLanguageText(gCacheReads, "CacheView.reads.title");
/* 151 */     Utils.setLayoutData(gCacheReads, new GridData(768));
/*     */     
/* 153 */     GridLayout layoutGeneral = new GridLayout();
/* 154 */     layoutGeneral.numColumns = 6;
/* 155 */     gCacheReads.setLayout(layoutGeneral);
/*     */     
/*     */ 
/* 158 */     Label lbl = new Label(gCacheReads, 0);
/*     */     
/* 160 */     lbl = new Label(gCacheReads, 0);
/* 161 */     Messages.setLanguageText(lbl, "CacheView.reads.#");
/*     */     
/* 163 */     lbl = new Label(gCacheReads, 0);
/* 164 */     Messages.setLanguageText(lbl, "CacheView.reads.amount");
/*     */     
/* 166 */     lbl = new Label(gCacheReads, 0);
/* 167 */     Messages.setLanguageText(lbl, "CacheView.reads.avgsize");
/*     */     
/* 169 */     lbl = new Label(gCacheReads, 0);
/* 170 */     lbl = new Label(gCacheReads, 0);
/*     */     
/*     */ 
/* 173 */     lbl = new Label(gCacheReads, 0);
/* 174 */     GridData gridData = new GridData();
/* 175 */     gridData.widthHint = 100;
/* 176 */     Utils.setLayoutData(lbl, gridData);
/* 177 */     Messages.setLanguageText(lbl, "CacheView.reads.fromCache");
/*     */     
/* 179 */     this.lblNumberReadsFromCache = new Label(gCacheReads, 0);
/* 180 */     gridData = new GridData();
/* 181 */     gridData.widthHint = 100;
/* 182 */     Utils.setLayoutData(this.lblNumberReadsFromCache, gridData);
/*     */     
/* 184 */     this.lblReadsFromCache = new Label(gCacheReads, 0);
/* 185 */     gridData = new GridData();
/* 186 */     gridData.widthHint = 100;
/* 187 */     Utils.setLayoutData(this.lblReadsFromCache, gridData);
/*     */     
/* 189 */     this.lblAvgSizeFromCache = new Label(gCacheReads, 0);
/* 190 */     gridData = new GridData();
/* 191 */     gridData.widthHint = 100;
/* 192 */     Utils.setLayoutData(this.lblAvgSizeFromCache, gridData);
/*     */     
/* 194 */     this.pbReads = new ProgressBar(gCacheReads, 256);
/* 195 */     gridData = new GridData(768);
/* 196 */     gridData.verticalSpan = 2;
/* 197 */     Utils.setLayoutData(this.pbReads, gridData);
/* 198 */     this.pbReads.setMinimum(0);
/* 199 */     this.pbReads.setMaximum(1000);
/*     */     
/* 201 */     this.lblPercentReads = new Label(gCacheReads, 0);
/* 202 */     gridData = new GridData();
/* 203 */     gridData.verticalSpan = 2;
/* 204 */     gridData.widthHint = 100;
/* 205 */     Utils.setLayoutData(this.lblPercentReads, gridData);
/*     */     
/* 207 */     lbl = new Label(gCacheReads, 0);
/* 208 */     gridData = new GridData();
/* 209 */     gridData.widthHint = 100;
/* 210 */     Utils.setLayoutData(lbl, gridData);
/* 211 */     Messages.setLanguageText(lbl, "CacheView.reads.fromFile");
/*     */     
/* 213 */     this.lblNumberReadsFromFile = new Label(gCacheReads, 0);
/* 214 */     gridData = new GridData();
/* 215 */     gridData.widthHint = 100;
/* 216 */     Utils.setLayoutData(this.lblNumberReadsFromFile, gridData);
/*     */     
/* 218 */     this.lblReadsFromFile = new Label(gCacheReads, 0);
/* 219 */     gridData = new GridData();
/* 220 */     gridData.widthHint = 100;
/* 221 */     Utils.setLayoutData(this.lblReadsFromFile, gridData);
/*     */     
/* 223 */     this.lblAvgSizeFromFile = new Label(gCacheReads, 0);
/* 224 */     gridData = new GridData();
/* 225 */     gridData.widthHint = 100;
/* 226 */     Utils.setLayoutData(this.lblAvgSizeFromFile, gridData);
/*     */   }
/*     */   
/*     */ 
/*     */   private void generateSpeedGroup()
/*     */   {
/* 232 */     Group gCacheSpeeds = new Group(this.panel, 0);
/* 233 */     Messages.setLanguageText(gCacheSpeeds, "CacheView.speeds.title");
/* 234 */     Utils.setLayoutData(gCacheSpeeds, new GridData(1808));
/*     */     
/* 236 */     GridLayout layoutGeneral = new GridLayout();
/* 237 */     layoutGeneral.numColumns = 3;
/* 238 */     gCacheSpeeds.setLayout(layoutGeneral);
/*     */     
/*     */ 
/* 241 */     Label lbl = new Label(gCacheSpeeds, 0);
/*     */     
/* 243 */     lbl = new Label(gCacheSpeeds, 0);
/* 244 */     GridData gridData = new GridData(64);
/* 245 */     Utils.setLayoutData(lbl, gridData);
/* 246 */     Messages.setLanguageText(lbl, "CacheView.speeds.reads");
/*     */     
/* 248 */     lbl = new Label(gCacheSpeeds, 0);
/* 249 */     gridData = new GridData(64);
/* 250 */     Utils.setLayoutData(lbl, gridData);
/* 251 */     Messages.setLanguageText(lbl, "CacheView.speeds.writes");
/*     */     
/* 253 */     lbl = new Label(gCacheSpeeds, 0);
/* 254 */     Messages.setLanguageText(lbl, "CacheView.speeds.fromCache");
/*     */     
/* 256 */     this.readsFromCache = new Canvas(gCacheSpeeds, 262144);
/* 257 */     gridData = new GridData(1808);
/* 258 */     Utils.setLayoutData(this.readsFromCache, gridData);
/* 259 */     this.rfcGraph.initialize(this.readsFromCache);
/*     */     
/*     */ 
/* 262 */     this.writesToCache = new Canvas(gCacheSpeeds, 262144);
/* 263 */     gridData = new GridData(1808);
/* 264 */     Utils.setLayoutData(this.writesToCache, gridData);
/* 265 */     this.wtcGraph.initialize(this.writesToCache);
/*     */     
/* 267 */     lbl = new Label(gCacheSpeeds, 0);
/* 268 */     Messages.setLanguageText(lbl, "CacheView.speeds.fromFile");
/*     */     
/* 270 */     this.readsFromFile = new Canvas(gCacheSpeeds, 262144);
/* 271 */     gridData = new GridData(1808);
/* 272 */     Utils.setLayoutData(this.readsFromFile, gridData);
/* 273 */     this.rffGraph.initialize(this.readsFromFile);
/*     */     
/* 275 */     this.writesToFile = new Canvas(gCacheSpeeds, 262144);
/* 276 */     gridData = new GridData(1808);
/* 277 */     Utils.setLayoutData(this.writesToFile, gridData);
/* 278 */     this.wtfGraph.initialize(this.writesToFile);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void periodicUpdate()
/*     */   {
/* 285 */     this.rfcGraph.addIntValue((int)this.stats.getAverageBytesReadFromCache());
/* 286 */     this.rffGraph.addIntValue((int)this.stats.getAverageBytesReadFromFile());
/* 287 */     this.wtcGraph.addIntValue((int)this.stats.getAverageBytesWrittenToCache());
/* 288 */     this.wtfGraph.addIntValue((int)this.stats.getAverageBytesWrittenToFile());
/*     */   }
/*     */   
/*     */ 
/*     */   private void generateWritesGroup()
/*     */   {
/* 294 */     Group gCacheWrites = new Group(this.panel, 0);
/* 295 */     Messages.setLanguageText(gCacheWrites, "CacheView.writes.title");
/* 296 */     Utils.setLayoutData(gCacheWrites, new GridData(768));
/*     */     
/* 298 */     GridLayout layoutGeneral = new GridLayout();
/* 299 */     layoutGeneral.numColumns = 6;
/* 300 */     gCacheWrites.setLayout(layoutGeneral);
/*     */     
/*     */ 
/* 303 */     Label lbl = new Label(gCacheWrites, 0);
/*     */     
/* 305 */     lbl = new Label(gCacheWrites, 0);
/* 306 */     Messages.setLanguageText(lbl, "CacheView.reads.#");
/*     */     
/* 308 */     lbl = new Label(gCacheWrites, 0);
/* 309 */     Messages.setLanguageText(lbl, "CacheView.reads.amount");
/*     */     
/* 311 */     lbl = new Label(gCacheWrites, 0);
/* 312 */     Messages.setLanguageText(lbl, "CacheView.reads.avgsize");
/*     */     
/* 314 */     lbl = new Label(gCacheWrites, 0);
/* 315 */     lbl = new Label(gCacheWrites, 0);
/*     */     
/*     */ 
/* 318 */     lbl = new Label(gCacheWrites, 0);
/* 319 */     GridData gridData = new GridData();
/* 320 */     gridData.widthHint = 100;
/* 321 */     Utils.setLayoutData(lbl, gridData);
/* 322 */     Messages.setLanguageText(lbl, "CacheView.writes.toCache");
/*     */     
/* 324 */     this.lblNumberWritesToCache = new Label(gCacheWrites, 0);
/* 325 */     gridData = new GridData();
/* 326 */     gridData.widthHint = 100;
/* 327 */     Utils.setLayoutData(this.lblNumberWritesToCache, gridData);
/*     */     
/* 329 */     this.lblWritesToCache = new Label(gCacheWrites, 0);
/* 330 */     gridData = new GridData();
/* 331 */     gridData.widthHint = 100;
/* 332 */     Utils.setLayoutData(this.lblWritesToCache, gridData);
/*     */     
/* 334 */     this.lblAvgSizeToCache = new Label(gCacheWrites, 0);
/* 335 */     gridData = new GridData();
/* 336 */     gridData.widthHint = 100;
/* 337 */     Utils.setLayoutData(this.lblAvgSizeToCache, gridData);
/*     */     
/* 339 */     this.pbWrites = new ProgressBar(gCacheWrites, 256);
/* 340 */     gridData = new GridData(768);
/* 341 */     gridData.verticalSpan = 2;
/* 342 */     Utils.setLayoutData(this.pbWrites, gridData);
/* 343 */     this.pbWrites.setMinimum(0);
/* 344 */     this.pbWrites.setMaximum(1000);
/*     */     
/* 346 */     this.lblPercentWrites = new Label(gCacheWrites, 0);
/* 347 */     gridData = new GridData();
/* 348 */     gridData.verticalSpan = 2;
/* 349 */     gridData.widthHint = 100;
/* 350 */     Utils.setLayoutData(this.lblPercentWrites, gridData);
/*     */     
/* 352 */     lbl = new Label(gCacheWrites, 0);
/* 353 */     gridData = new GridData();
/* 354 */     gridData.widthHint = 100;
/* 355 */     Utils.setLayoutData(lbl, gridData);
/* 356 */     Messages.setLanguageText(lbl, "CacheView.writes.toFile");
/*     */     
/* 358 */     this.lblNumberWritesToFile = new Label(gCacheWrites, 0);
/* 359 */     gridData = new GridData();
/* 360 */     gridData.widthHint = 100;
/* 361 */     Utils.setLayoutData(this.lblNumberWritesToFile, gridData);
/*     */     
/* 363 */     this.lblWritesToFile = new Label(gCacheWrites, 0);
/* 364 */     gridData = new GridData();
/* 365 */     gridData.widthHint = 100;
/* 366 */     Utils.setLayoutData(this.lblWritesToFile, gridData);
/*     */     
/* 368 */     this.lblAvgSizeToFile = new Label(gCacheWrites, 0);
/* 369 */     gridData = new GridData();
/* 370 */     gridData.widthHint = 100;
/* 371 */     Utils.setLayoutData(this.lblAvgSizeToFile, gridData);
/*     */   }
/*     */   
/*     */   private void delete() {
/* 375 */     Utils.disposeComposite(this.panel);
/* 376 */     this.rfcGraph.dispose();
/* 377 */     this.rffGraph.dispose();
/* 378 */     this.wtcGraph.dispose();
/* 379 */     this.wtfGraph.dispose();
/*     */   }
/*     */   
/*     */   private Composite getComposite() {
/* 383 */     return this.panel;
/*     */   }
/*     */   
/*     */   private void refresh()
/*     */   {
/* 388 */     this.lblSize.setText(DisplayFormatters.formatByteCountToKiBEtc(this.stats.getSize()));
/* 389 */     this.lblInUse.setText(DisplayFormatters.formatByteCountToKiBEtc(this.stats.getUsedSize()));
/*     */     
/* 391 */     int perThousands = (int)(1000L * this.stats.getUsedSize() / this.stats.getSize());
/* 392 */     this.lblPercentUsed.setText(DisplayFormatters.formatPercentFromThousands(perThousands));
/* 393 */     this.pbInUse.setSelection(perThousands);
/*     */     
/*     */ 
/* 396 */     refrehReads();
/*     */     
/*     */ 
/* 399 */     refreshWrites();
/*     */     
/*     */ 
/* 402 */     this.rfcGraph.refresh(false);
/* 403 */     this.rffGraph.refresh(false);
/* 404 */     this.wtcGraph.refresh(false);
/* 405 */     this.wtfGraph.refresh(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void refrehReads()
/*     */   {
/* 413 */     long readsFromCache = this.stats.getBytesReadFromCache();
/* 414 */     long readsFromFile = this.stats.getBytesReadFromFile();
/* 415 */     long nbReadsFromCache = this.stats.getCacheReadCount();
/* 416 */     long nbReadsFromFile = this.stats.getFileReadCount();
/* 417 */     this.lblNumberReadsFromCache.setText("" + nbReadsFromCache);
/* 418 */     this.lblNumberReadsFromFile.setText("" + nbReadsFromFile);
/*     */     
/* 420 */     if (nbReadsFromCache != 0L) {
/* 421 */       long avgReadFromCache = readsFromCache / nbReadsFromCache;
/* 422 */       this.lblAvgSizeFromCache.setText(DisplayFormatters.formatByteCountToKiBEtc(avgReadFromCache));
/*     */     } else {
/* 424 */       this.lblAvgSizeFromCache.setText("--");
/*     */     }
/*     */     
/* 427 */     if (nbReadsFromFile != 0L) {
/* 428 */       long avgReadFromFile = readsFromFile / nbReadsFromFile;
/* 429 */       this.lblAvgSizeFromFile.setText(DisplayFormatters.formatByteCountToKiBEtc(avgReadFromFile));
/*     */     } else {
/* 431 */       this.lblAvgSizeFromFile.setText("--");
/*     */     }
/*     */     
/* 434 */     this.lblReadsFromCache.setText(DisplayFormatters.formatByteCountToKiBEtc(readsFromCache));
/* 435 */     this.lblReadsFromFile.setText(DisplayFormatters.formatByteCountToKiBEtc(readsFromFile));
/*     */     
/* 437 */     long totalRead = readsFromCache + readsFromFile;
/* 438 */     if (totalRead > 0L) {
/* 439 */       int perThousands = (int)(1000L * this.stats.getBytesReadFromCache() / totalRead);
/* 440 */       this.lblPercentReads.setText(DisplayFormatters.formatPercentFromThousands(perThousands) + " " + MessageText.getString("CacheView.reads.hits"));
/* 441 */       this.pbReads.setSelection(perThousands);
/*     */     }
/*     */   }
/*     */   
/*     */   private void refreshWrites()
/*     */   {
/* 447 */     long writesToCache = this.stats.getBytesWrittenToCache();
/* 448 */     long writesToFile = this.stats.getBytesWrittenToFile();
/* 449 */     long nbWritesToCache = this.stats.getCacheWriteCount();
/* 450 */     long nbWritesToFile = this.stats.getFileWriteCount();
/* 451 */     this.lblNumberWritesToCache.setText("" + nbWritesToCache);
/* 452 */     this.lblNumberWritesToFile.setText("" + nbWritesToFile);
/*     */     
/* 454 */     if (nbWritesToCache != 0L) {
/* 455 */       long avgReadToCache = writesToCache / nbWritesToCache;
/* 456 */       this.lblAvgSizeToCache.setText(DisplayFormatters.formatByteCountToKiBEtc(avgReadToCache));
/*     */     } else {
/* 458 */       this.lblAvgSizeToCache.setText("--");
/*     */     }
/*     */     
/* 461 */     if (nbWritesToFile != 0L) {
/* 462 */       long avgReadToFile = writesToFile / nbWritesToFile;
/* 463 */       this.lblAvgSizeToFile.setText(DisplayFormatters.formatByteCountToKiBEtc(avgReadToFile));
/*     */     } else {
/* 465 */       this.lblAvgSizeToFile.setText("--");
/*     */     }
/*     */     
/* 468 */     this.lblWritesToCache.setText(DisplayFormatters.formatByteCountToKiBEtc(writesToCache));
/* 469 */     this.lblWritesToFile.setText(DisplayFormatters.formatByteCountToKiBEtc(writesToFile));
/*     */     
/* 471 */     long totalNbWrites = nbWritesToCache + nbWritesToFile;
/* 472 */     if (totalNbWrites > 0L) {
/* 473 */       int perThousands = (int)(1000L * nbWritesToCache / totalNbWrites);
/* 474 */       this.lblPercentWrites.setText(DisplayFormatters.formatPercentFromThousands(perThousands) + " " + MessageText.getString("CacheView.writes.hits"));
/* 475 */       this.pbWrites.setSelection(perThousands);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 480 */     switch (event.getType()) {
/*     */     case 0: 
/* 482 */       this.swtView = ((UISWTView)event.getData());
/* 483 */       this.swtView.setTitle(MessageText.getString("CacheView.title.full"));
/* 484 */       break;
/*     */     
/*     */     case 7: 
/* 487 */       delete();
/* 488 */       break;
/*     */     
/*     */     case 2: 
/* 491 */       initialize((Composite)event.getData());
/* 492 */       break;
/*     */     
/*     */     case 6: 
/* 495 */       Messages.updateLanguageForControl(getComposite());
/* 496 */       break;
/*     */     
/*     */     case 1: 
/*     */       break;
/*     */     
/*     */     case 3: 
/*     */       break;
/*     */     
/*     */     case 5: 
/* 505 */       refresh();
/* 506 */       break;
/*     */     
/*     */     case 256: 
/* 509 */       periodicUpdate();
/*     */     }
/*     */     
/*     */     
/* 513 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/stats/CacheView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */