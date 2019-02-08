/*     */ package org.gudy.azureus2.core3.ipfilter.impl;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.zip.GZIPInputStream;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipInputStream;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpRange;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderAdapter;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader.ResourceDownloaderFactoryImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IpFilterAutoLoaderImpl
/*     */ {
/*  55 */   private static final LogIDs LOGID = LogIDs.CORE;
/*     */   
/*     */ 
/*     */   public static final String CFG_AUTOLOAD_LAST = "Ip Filter Autoload Last Date";
/*     */   
/*     */   public static final String CFG_AUTOLOAD_FILE = "Ip Filter Autoload File";
/*     */   
/*  62 */   static final AEMonitor class_mon = new AEMonitor("IpFilterAutoLoaderImpl:class");
/*     */   
/*     */   private Object timerEventFilterReload;
/*     */   
/*     */   private final IpFilterImpl ipFilter;
/*     */   
/*     */   public IpFilterAutoLoaderImpl(IpFilterImpl ipFilter)
/*     */   {
/*  70 */     this.ipFilter = ipFilter;
/*  71 */     COConfigurationManager.setLongDefault("Ip Filter Autoload Last Date", 0L);
/*  72 */     COConfigurationManager.setStringDefault("Ip Filter Autoload File", "");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void loadDATFilters(InputStream fin)
/*     */   {
/*     */     try
/*     */     {
/*  84 */       class_mon.enter();
/*     */       
/*  86 */       List new_ipRanges = new ArrayList(1024);
/*     */       
/*  88 */       InputStreamReader streamReader = null;
/*  89 */       BufferedReader reader = null;
/*     */       try {
/*  91 */         Pattern pattern = Pattern.compile("^(.*):([0-9\\.]+)[^0-9]+([0-9\\.]+).*");
/*  92 */         int parseMode = -1;
/*     */         
/*     */ 
/*     */ 
/*  96 */         streamReader = new InputStreamReader(fin, "utf8");
/*  97 */         reader = new BufferedReader(streamReader);
/*     */         
/*  99 */         int numConsecutiveUnknowns = 0;
/*     */         
/* 101 */         while (numConsecutiveUnknowns < 1000) {
/* 102 */           String line = reader.readLine();
/*     */           
/* 104 */           if (line == null) {
/*     */             break;
/*     */           }
/*     */           
/* 108 */           line = line.trim();
/*     */           
/* 110 */           if ((!line.startsWith("#")) && (line.length() != 0))
/*     */           {
/*     */ 
/*     */ 
/* 114 */             String description = "";
/* 115 */             String startIp = null;
/* 116 */             String endIp = null;
/* 117 */             int level = 0;
/*     */             
/* 119 */             if ((parseMode <= 0) || (parseMode == 1)) {
/* 120 */               Matcher matcher = pattern.matcher(line);
/* 121 */               if (matcher.find()) {
/* 122 */                 if (parseMode != 1) {
/* 123 */                   parseMode = 1;
/*     */                 }
/* 125 */                 description = matcher.group(1);
/* 126 */                 startIp = matcher.group(2);
/* 127 */                 endIp = matcher.group(3);
/*     */               } else {
/* 129 */                 Logger.log(new LogEvent(LOGID, 1, "unrecognized line while reading ip filter: " + line));
/*     */               }
/*     */             }
/*     */             
/*     */ 
/* 134 */             if (parseMode != 1) {
/* 135 */               if (parseMode != 2) {
/* 136 */                 parseMode = 2;
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 144 */               String[] sections = line.split(" *[-,] *", 4);
/*     */               
/* 146 */               if ((sections.length >= 2) && (
/* 147 */                 (sections[0].indexOf('.') < 0) || (sections[1].indexOf('.') < 0) || (sections[0].length() > 15) || (sections[1].length() > 15) || (sections[0].length() < 7) || (sections[1].length() < 7)))
/*     */               {
/*     */ 
/* 150 */                 numConsecutiveUnknowns++;
/*     */ 
/*     */               }
/*     */               else
/*     */               {
/* 155 */                 if (sections.length >= 4)
/*     */                 {
/*     */ 
/* 158 */                   startIp = sections[0];
/* 159 */                   endIp = sections[1];
/* 160 */                   description = sections[3];
/*     */                   try {
/* 162 */                     level = Integer.parseInt(sections[2]);
/*     */                   } catch (NumberFormatException e) {
/* 164 */                     description = sections[2] + " " + description;
/*     */                   }
/* 166 */                   for (int i = 4; i < sections.length; i++) {
/* 167 */                     description = description + " " + sections[i];
/*     */                   }
/* 169 */                   numConsecutiveUnknowns = 0;
/* 170 */                 } else if (sections.length == 3) {
/* 171 */                   startIp = sections[0];
/* 172 */                   endIp = sections[1];
/* 173 */                   description = sections[2];
/* 174 */                   numConsecutiveUnknowns = 0;
/* 175 */                 } else if (sections.length == 2) {
/* 176 */                   startIp = sections[0];
/* 177 */                   endIp = sections[1];
/* 178 */                   numConsecutiveUnknowns = 0;
/*     */                 } else {
/* 180 */                   numConsecutiveUnknowns++;
/* 181 */                   continue;
/*     */                 }
/*     */                 
/* 184 */                 if (level >= 128) {}
/*     */               }
/*     */               
/*     */ 
/*     */             }
/* 189 */             else if ((startIp != null) && (endIp != null))
/*     */             {
/*     */ 
/*     */ 
/* 193 */               IpRangeImpl ipRange = new IpRangeImpl(description, startIp, endIp, true);
/*     */               
/*     */ 
/*     */ 
/* 197 */               ipRange.setAddedToRangeList(true);
/*     */               
/* 199 */               new_ipRanges.add(ipRange);
/*     */             }
/*     */           } } } catch (IOException e) { Iterator it;
/* 202 */         Debug.out(e);
/*     */       } finally {
/*     */         Iterator it;
/* 205 */         if (reader != null) {
/*     */           try {
/* 207 */             reader.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/* 211 */         if (streamReader != null) {
/*     */           try {
/* 213 */             streamReader.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */         
/* 218 */         Iterator it = new_ipRanges.iterator();
/*     */         
/* 220 */         while (it.hasNext())
/*     */         {
/* 222 */           ((IpRange)it.next()).checkValid();
/*     */         }
/*     */         
/* 225 */         this.ipFilter.markAsUpToDate();
/*     */       }
/*     */     }
/*     */     finally {
/* 229 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   private int getP2BFileVersion(InputStream is)
/*     */   {
/*     */     try {
/* 236 */       for (int i = 0; i < 4; i++) {
/* 237 */         int byteRead = is.read();
/* 238 */         if (byteRead != 255) {
/* 239 */           return -1;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 244 */       byte[] MAGIC = { 80, 50, 66 };
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 249 */       for (int i = 0; i < MAGIC.length; i++) {
/* 250 */         byte b = MAGIC[i];
/* 251 */         if (b != is.read()) {
/* 252 */           return -1;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 257 */       int p2bVersion = is.read();
/* 258 */       Logger.log(new LogEvent(LOGID, "Log Filter: loading p2b version " + p2bVersion));
/*     */       
/* 260 */       return p2bVersion;
/*     */     } catch (IOException e) {
/* 262 */       Debug.out(e);
/*     */     }
/*     */     
/* 265 */     return -1;
/*     */   }
/*     */   
/*     */   protected void loadOtherFilters(boolean allowAsyncDownloading, boolean loadOldWhileAsyncDownloading)
/*     */   {
/* 270 */     int p2bVersion = -1;
/*     */     try {
/* 272 */       class_mon.enter();
/*     */       
/* 274 */       List new_ipRanges = new ArrayList(1024);
/*     */       
/* 276 */       InputStream fin = null;
/* 277 */       BufferedInputStream bin = null;
/* 278 */       boolean isURL = false;
/*     */       try
/*     */       {
/* 281 */         String file = COConfigurationManager.getStringParameter("Ip Filter Autoload File");
/* 282 */         Logger.log(new LogEvent(LOGID, "IP Filter file: " + file));
/* 283 */         File filtersFile = new File(file);
/* 284 */         if (filtersFile.exists()) {
/* 285 */           isURL = false;
/*     */         } else {
/* 287 */           if (!UrlUtils.isURL(file))
/*     */           {
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
/*     */ 
/*     */ 
/*     */ 
/* 439 */             if (bin != null) {
/*     */               try {
/* 441 */                 bin.close();
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/* 445 */             if (fin != null) {
/*     */               try {
/* 447 */                 fin.close();
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */             
/* 452 */             Iterator it = new_ipRanges.iterator();
/*     */             
/* 454 */             while (it.hasNext())
/*     */             {
/* 456 */               ((IpRange)it.next()).checkValid();
/*     */             }
/*     */             
/* 459 */             this.ipFilter.markAsUpToDate();
/*     */             
/* 461 */             if (!isURL) {
/* 462 */               setFileReloadTimer();
/*     */             }
/*     */             return;
/*     */           }
/* 291 */           isURL = true;
/*     */           
/* 293 */           filtersFile = FileUtil.getUserFile("ipfilter.dl");
/* 294 */           if (filtersFile.exists()) {
/* 295 */             if (allowAsyncDownloading) {
/* 296 */               Logger.log(new LogEvent(LOGID, "Downloading " + file + "  async"));
/*     */               
/* 298 */               downloadFiltersAsync(new URL(file));
/*     */               
/* 300 */               if (!loadOldWhileAsyncDownloading)
/*     */               {
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
/* 439 */                 if (bin != null) {
/*     */                   try {
/* 441 */                     bin.close();
/*     */                   }
/*     */                   catch (Throwable e) {}
/*     */                 }
/* 445 */                 if (fin != null) {
/*     */                   try {
/* 447 */                     fin.close();
/*     */                   }
/*     */                   catch (Throwable e) {}
/*     */                 }
/*     */                 
/* 452 */                 Iterator it = new_ipRanges.iterator();
/*     */                 
/* 454 */                 while (it.hasNext())
/*     */                 {
/* 456 */                   ((IpRange)it.next()).checkValid();
/*     */                 }
/*     */                 
/* 459 */                 this.ipFilter.markAsUpToDate();
/*     */                 
/* 461 */                 if (!isURL) {
/* 462 */                   setFileReloadTimer();
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 306 */             Logger.log(new LogEvent(LOGID, "sync Downloading " + file));
/*     */             try {
/* 308 */               ResourceDownloader rd = ResourceDownloaderFactoryImpl.getSingleton().create(new URL(file));
/*     */               
/* 310 */               fin = rd.download();
/* 311 */               FileUtil.copyFile(fin, filtersFile);
/* 312 */               setNextAutoDownload(true);
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
/*     */             }
/*     */             catch (ResourceDownloaderException e)
/*     */             {
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
/* 439 */               if (bin != null) {
/*     */                 try {
/* 441 */                   bin.close();
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/* 445 */               if (fin != null) {
/*     */                 try {
/* 447 */                   fin.close();
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/*     */               
/* 452 */               Iterator it = new_ipRanges.iterator();
/*     */               
/* 454 */               while (it.hasNext())
/*     */               {
/* 456 */                 ((IpRange)it.next()).checkValid();
/*     */               }
/*     */               
/* 459 */               this.ipFilter.markAsUpToDate();
/*     */               
/* 461 */               if (!isURL) {
/* 462 */                 setFileReloadTimer();
/*     */               }
/*     */               return;
/*     */             }
/*     */           }
/*     */         }
/* 319 */         fin = new FileInputStream(filtersFile);
/* 320 */         bin = new BufferedInputStream(fin, 16384);
/*     */         
/*     */ 
/* 323 */         byte[] headerBytes = new byte[2];
/* 324 */         bin.mark(3);
/* 325 */         bin.read(headerBytes, 0, 2);
/* 326 */         bin.reset();
/*     */         
/* 328 */         if ((headerBytes[1] == -117) && (headerBytes[0] == 31)) {
/* 329 */           GZIPInputStream gzip = new GZIPInputStream(bin);
/*     */           
/* 331 */           filtersFile = FileUtil.getUserFile("ipfilter.ext");
/* 332 */           FileUtil.copyFile(gzip, filtersFile);
/* 333 */           fin = new FileInputStream(filtersFile);
/* 334 */           bin = new BufferedInputStream(fin, 16384);
/* 335 */         } else if ((headerBytes[0] == 80) && (headerBytes[1] == 75)) {
/* 336 */           ZipInputStream zip = new ZipInputStream(bin);
/*     */           
/* 338 */           ZipEntry zipEntry = zip.getNextEntry();
/*     */           
/* 340 */           while ((zipEntry != null) && (zipEntry.getSize() < 1048576L)) {
/* 341 */             zipEntry = zip.getNextEntry();
/*     */           }
/*     */           
/* 344 */           if (zipEntry == null)
/*     */           {
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
/* 439 */             if (bin != null) {
/*     */               try {
/* 441 */                 bin.close();
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/* 445 */             if (fin != null) {
/*     */               try {
/* 447 */                 fin.close();
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */             
/* 452 */             Iterator it = new_ipRanges.iterator();
/*     */             
/* 454 */             while (it.hasNext())
/*     */             {
/* 456 */               ((IpRange)it.next()).checkValid();
/*     */             }
/*     */             
/* 459 */             this.ipFilter.markAsUpToDate();
/*     */             
/* 461 */             if (!isURL) {
/* 462 */               setFileReloadTimer();
/*     */             }
/*     */             return;
/*     */           }
/* 347 */           filtersFile = FileUtil.getUserFile("ipfilter.ext");
/* 348 */           FileUtil.copyFile(zip, filtersFile);
/* 349 */           fin = new FileInputStream(filtersFile);
/* 350 */           bin = new BufferedInputStream(fin, 16384);
/*     */         }
/*     */         
/* 353 */         bin.mark(8);
/*     */         
/* 355 */         p2bVersion = getP2BFileVersion(bin);
/*     */         
/* 357 */         if ((p2bVersion < 1) || (p2bVersion > 3)) {
/* 358 */           bin.reset();
/* 359 */           loadDATFilters(bin);
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
/* 439 */           if (bin != null) {
/*     */             try {
/* 441 */               bin.close();
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/* 445 */           if (fin != null) {
/*     */             try {
/* 447 */               fin.close();
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */           
/* 452 */           Iterator it = new_ipRanges.iterator();
/*     */           
/* 454 */           while (it.hasNext())
/*     */           {
/* 456 */             ((IpRange)it.next()).checkValid();
/*     */           }
/*     */           
/* 459 */           this.ipFilter.markAsUpToDate();
/*     */           
/* 461 */           if (!isURL) {
/* 462 */             setFileReloadTimer();
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 363 */           byte[] descBytes = new byte['ÿ'];
/* 364 */           byte[] ipBytes = new byte[4];
/* 365 */           String encoding = p2bVersion == 1 ? "ISO-8859-1" : "UTF-8";
/*     */           
/* 367 */           if ((p2bVersion == 1) || (p2bVersion == 2)) {
/*     */             for (;;) {
/* 369 */               String description = readString(bin, descBytes, encoding);
/*     */               
/* 371 */               int read = bin.read(ipBytes);
/* 372 */               if (read < 4) {
/*     */                 break;
/*     */               }
/* 375 */               int startIp = ByteFormatter.byteArrayToInt(ipBytes);
/* 376 */               read = bin.read(ipBytes);
/* 377 */               if (read < 4) {
/*     */                 break;
/*     */               }
/* 380 */               int endIp = ByteFormatter.byteArrayToInt(ipBytes);
/*     */               
/* 382 */               IpRangeImpl ipRange = new IpRangeImpl(description, startIp, endIp, true);
/*     */               
/*     */ 
/* 385 */               ipRange.setAddedToRangeList(true);
/*     */               
/* 387 */               new_ipRanges.add(ipRange);
/*     */             }
/*     */           }
/* 390 */           int read = bin.read(ipBytes);
/* 391 */           if (read < 4)
/*     */           {
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
/* 439 */             if (bin != null) {
/*     */               try {
/* 441 */                 bin.close();
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/* 445 */             if (fin != null) {
/*     */               try {
/* 447 */                 fin.close();
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */             
/* 452 */             Iterator it = new_ipRanges.iterator();
/*     */             
/* 454 */             while (it.hasNext())
/*     */             {
/* 456 */               ((IpRange)it.next()).checkValid();
/*     */             }
/*     */             
/* 459 */             this.ipFilter.markAsUpToDate();
/*     */             
/* 461 */             if (!isURL) {
/* 462 */               setFileReloadTimer();
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 394 */             int numDescs = ByteFormatter.byteArrayToInt(ipBytes);
/* 395 */             String[] descs = new String[numDescs];
/* 396 */             for (int i = 0; i < numDescs; i++) {
/* 397 */               descs[i] = readString(bin, descBytes, encoding);
/*     */             }
/*     */             
/* 400 */             read = bin.read(ipBytes);
/* 401 */             if (read < 4)
/*     */             {
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
/* 439 */               if (bin != null) {
/*     */                 try {
/* 441 */                   bin.close();
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/* 445 */               if (fin != null) {
/*     */                 try {
/* 447 */                   fin.close();
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/*     */               
/* 452 */               Iterator it = new_ipRanges.iterator();
/*     */               
/* 454 */               while (it.hasNext())
/*     */               {
/* 456 */                 ((IpRange)it.next()).checkValid();
/*     */               }
/*     */               
/* 459 */               this.ipFilter.markAsUpToDate();
/*     */               
/* 461 */               if (!isURL) {
/* 462 */                 setFileReloadTimer();
/*     */               }
/*     */             }
/*     */             else
/*     */             {
/* 404 */               int numRanges = ByteFormatter.byteArrayToInt(ipBytes);
/* 405 */               for (int i = 0; i < numRanges; i++) {
/* 406 */                 read = bin.read(ipBytes);
/* 407 */                 if (read < 4)
/*     */                 {
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
/* 439 */                   if (bin != null) {
/*     */                     try {
/* 441 */                       bin.close();
/*     */                     }
/*     */                     catch (Throwable e) {}
/*     */                   }
/* 445 */                   if (fin != null) {
/*     */                     try {
/* 447 */                       fin.close();
/*     */                     }
/*     */                     catch (Throwable e) {}
/*     */                   }
/*     */                   
/* 452 */                   Iterator it = new_ipRanges.iterator();
/*     */                   
/* 454 */                   while (it.hasNext())
/*     */                   {
/* 456 */                     ((IpRange)it.next()).checkValid();
/*     */                   }
/*     */                   
/* 459 */                   this.ipFilter.markAsUpToDate();
/*     */                   
/* 461 */                   if (!isURL) {
/* 462 */                     setFileReloadTimer();
/*     */                   }
/*     */                   return;
/*     */                 }
/* 410 */                 int descIdx = ByteFormatter.byteArrayToInt(ipBytes);
/*     */                 
/* 412 */                 read = bin.read(ipBytes);
/* 413 */                 if (read < 4)
/*     */                 {
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
/* 439 */                   if (bin != null) {
/*     */                     try {
/* 441 */                       bin.close();
/*     */                     }
/*     */                     catch (Throwable e) {}
/*     */                   }
/* 445 */                   if (fin != null) {
/*     */                     try {
/* 447 */                       fin.close();
/*     */                     }
/*     */                     catch (Throwable e) {}
/*     */                   }
/*     */                   
/* 452 */                   Iterator it = new_ipRanges.iterator();
/*     */                   
/* 454 */                   while (it.hasNext())
/*     */                   {
/* 456 */                     ((IpRange)it.next()).checkValid();
/*     */                   }
/*     */                   
/* 459 */                   this.ipFilter.markAsUpToDate();
/*     */                   
/* 461 */                   if (!isURL) {
/* 462 */                     setFileReloadTimer();
/*     */                   }
/*     */                   return;
/*     */                 }
/* 416 */                 int startIp = ByteFormatter.byteArrayToInt(ipBytes);
/*     */                 
/* 418 */                 read = bin.read(ipBytes);
/* 419 */                 if (read < 4)
/*     */                 {
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
/* 439 */                   if (bin != null) {
/*     */                     try {
/* 441 */                       bin.close();
/*     */                     }
/*     */                     catch (Throwable e) {}
/*     */                   }
/* 445 */                   if (fin != null) {
/*     */                     try {
/* 447 */                       fin.close();
/*     */                     }
/*     */                     catch (Throwable e) {}
/*     */                   }
/*     */                   
/* 452 */                   Iterator it = new_ipRanges.iterator();
/*     */                   
/* 454 */                   while (it.hasNext())
/*     */                   {
/* 456 */                     ((IpRange)it.next()).checkValid();
/*     */                   }
/*     */                   
/* 459 */                   this.ipFilter.markAsUpToDate();
/*     */                   
/* 461 */                   if (!isURL) {
/* 462 */                     setFileReloadTimer();
/*     */                   }
/*     */                   return;
/*     */                 }
/* 422 */                 int endIp = ByteFormatter.byteArrayToInt(ipBytes);
/*     */                 
/* 424 */                 String description = (descIdx < descs.length) && (descIdx >= 0) ? descs[descIdx] : "";
/*     */                 
/*     */ 
/* 427 */                 IpRangeImpl ipRange = new IpRangeImpl(description, startIp, endIp, true);
/*     */                 
/*     */ 
/* 430 */                 ipRange.setAddedToRangeList(true);
/*     */                 
/* 432 */                 new_ipRanges.add(ipRange);
/*     */               }
/*     */             }
/*     */           } } } catch (IOException e) { Iterator it;
/* 436 */         Debug.out(e);
/*     */       } finally {
/*     */         Iterator it;
/* 439 */         if (bin != null) {
/*     */           try {
/* 441 */             bin.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/* 445 */         if (fin != null) {
/*     */           try {
/* 447 */             fin.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */         
/* 452 */         Iterator it = new_ipRanges.iterator();
/*     */         
/* 454 */         while (it.hasNext())
/*     */         {
/* 456 */           ((IpRange)it.next()).checkValid();
/*     */         }
/*     */         
/* 459 */         this.ipFilter.markAsUpToDate();
/*     */         
/* 461 */         if (!isURL) {
/* 462 */           setFileReloadTimer();
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 467 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setFileReloadTimer()
/*     */   {
/* 477 */     if ((this.timerEventFilterReload instanceof TimerEvent)) {
/* 478 */       ((TimerEvent)this.timerEventFilterReload).cancel();
/* 479 */     } else if ((this.timerEventFilterReload instanceof TimerEventPeriodic)) {
/* 480 */       ((TimerEventPeriodic)this.timerEventFilterReload).cancel();
/*     */     }
/* 482 */     this.timerEventFilterReload = SimpleTimer.addPeriodicEvent("IP Filter download", 60000L, new TimerEventPerformer()
/*     */     {
/*     */       long lastFileModified;
/*     */       
/*     */       public void perform(TimerEvent event) {
/* 487 */         event.cancel();
/*     */         
/* 489 */         String file = COConfigurationManager.getStringParameter("Ip Filter Autoload File");
/* 490 */         File filtersFile = new File(file);
/* 491 */         if (!filtersFile.exists()) {
/* 492 */           return;
/*     */         }
/* 494 */         long fileModified = filtersFile.lastModified();
/*     */         
/* 496 */         if (this.lastFileModified == 0L) {
/* 497 */           this.lastFileModified = fileModified;
/* 498 */         } else if (this.lastFileModified != fileModified) {
/*     */           try
/*     */           {
/* 501 */             IpFilterAutoLoaderImpl.this.ipFilter.reload();
/*     */           }
/*     */           catch (Exception e) {}
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void downloadFiltersAsync(URL url)
/*     */   {
/* 515 */     ResourceDownloader rd = ResourceDownloaderFactoryImpl.getSingleton().create(url);
/*     */     
/*     */ 
/* 518 */     rd.addListener(new ResourceDownloaderAdapter()
/*     */     {
/*     */       public void reportPercentComplete(ResourceDownloader downloader, int percentage) {}
/*     */       
/*     */       public boolean completed(ResourceDownloader downloader, InputStream data)
/*     */       {
/*     */         try
/*     */         {
/* 526 */           IpFilterAutoLoaderImpl.this.setNextAutoDownload(true);
/*     */           
/* 528 */           Logger.log(new LogEvent(IpFilterAutoLoaderImpl.LOGID, "downloaded..waiting"));
/*     */           
/*     */ 
/* 531 */           IpFilterAutoLoaderImpl.class_mon.enter();
/* 532 */           Logger.log(new LogEvent(IpFilterAutoLoaderImpl.LOGID, "downloaded.. copying"));
/*     */           try
/*     */           {
/* 535 */             FileUtil.copyFile(data, FileUtil.getUserFile("ipfilter.dl"));
/* 536 */             AEThread thread = new AEThread("reload ipfilters", true) {
/*     */               public void runSupport() {
/*     */                 try {
/* 539 */                   UIFunctions uif = UIFunctionsManager.getUIFunctions();
/* 540 */                   if (uif != null) {
/* 541 */                     uif.setStatusText("Reloading Filters..");
/*     */                   }
/* 543 */                   IpFilterAutoLoaderImpl.this.ipFilter.reload(false);
/* 544 */                   if (uif != null) {
/* 545 */                     uif.setStatusText(null);
/*     */                   }
/*     */                 } catch (Exception e) {
/* 548 */                   Debug.out(e);
/*     */                 }
/*     */               }
/* 551 */             };
/* 552 */             thread.setPriority(4);
/* 553 */             thread.start();
/*     */           } catch (Exception e) {
/* 555 */             Debug.out(e);
/*     */           }
/*     */         } finally {
/* 558 */           IpFilterAutoLoaderImpl.class_mon.exit();
/*     */         }
/*     */         
/* 561 */         return true;
/*     */       }
/* 563 */     });
/* 564 */     rd.asyncDownload();
/*     */   }
/*     */   
/*     */   public void setNextAutoDownload(boolean updateLastDownloadedDate) {
/* 568 */     long now = SystemTime.getCurrentTime();
/*     */     long lastDL;
/*     */     long lastDL;
/* 571 */     if (updateLastDownloadedDate) {
/* 572 */       COConfigurationManager.setParameter("Ip Filter Autoload Last Date", now);
/* 573 */       lastDL = now;
/*     */     } else {
/* 575 */       lastDL = COConfigurationManager.getLongParameter("Ip Filter Autoload Last Date");
/* 576 */       if (lastDL > now) {
/* 577 */         lastDL = now;
/* 578 */         COConfigurationManager.setParameter("Ip Filter Autoload Last Date", now);
/*     */       }
/*     */     }
/*     */     
/* 582 */     long nextDL = lastDL + 604800000L;
/*     */     
/* 584 */     if ((this.timerEventFilterReload instanceof TimerEvent)) {
/* 585 */       ((TimerEvent)this.timerEventFilterReload).cancel();
/* 586 */     } else if ((this.timerEventFilterReload instanceof TimerEventPeriodic)) {
/* 587 */       ((TimerEventPeriodic)this.timerEventFilterReload).cancel();
/*     */     }
/* 589 */     this.timerEventFilterReload = SimpleTimer.addEvent("IP Filter download", nextDL, new TimerEventPerformer()
/*     */     {
/*     */       public void perform(TimerEvent event) {
/* 592 */         String file = COConfigurationManager.getStringParameter("Ip Filter Autoload File");
/*     */         try {
/* 594 */           IpFilterAutoLoaderImpl.this.downloadFiltersAsync(new URL(file));
/*     */         }
/*     */         catch (MalformedURLException e) {}
/*     */       }
/*     */     });
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
/*     */   private String readString(BufferedInputStream bin, byte[] descBytes, String encoding)
/*     */   {
/* 611 */     int pos = 0;
/*     */     try {
/*     */       for (;;) {
/* 614 */         int byteRead = bin.read();
/* 615 */         if (byteRead < 0) {
/*     */           break;
/*     */         }
/* 618 */         if (pos < descBytes.length) {
/* 619 */           descBytes[pos] = ((byte)byteRead);
/* 620 */           pos++;
/*     */         }
/* 622 */         if (byteRead == 0) {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (IOException e) {}
/*     */     
/* 629 */     if (pos > 1) {
/*     */       try {
/* 631 */         return new String(descBytes, 0, pos - 1, encoding);
/*     */       }
/*     */       catch (UnsupportedEncodingException e) {}
/*     */     }
/*     */     
/* 636 */     return "";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/impl/IpFilterAutoLoaderImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */