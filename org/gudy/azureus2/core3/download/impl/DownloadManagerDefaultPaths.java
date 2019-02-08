/*     */ package org.gudy.azureus2.core3.download.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagFeatureFileLocation;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.savelocation.DefaultSaveLocationManager;
/*     */ import org.gudy.azureus2.plugins.download.savelocation.SaveLocationChange;
/*     */ import org.gudy.azureus2.pluginsimpl.local.download.DownloadImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DownloadManagerDefaultPaths
/*     */   extends DownloadManagerMoveHandlerUtils
/*     */ {
/*  46 */   public static final DefaultSaveLocationManager DEFAULT_HANDLER = new DefaultSaveLocationManager()
/*     */   {
/*     */ 
/*     */ 
/*     */ 
/*     */     public SaveLocationChange onInitialization(Download d, boolean for_move, boolean on_event)
/*     */     {
/*     */ 
/*     */ 
/*  55 */       if (on_event) { return null;
/*     */       }
/*  57 */       DownloadManager dm = ((DownloadImpl)d).getDownload();
/*  58 */       return DownloadManagerDefaultPaths.determinePaths(dm, DownloadManagerDefaultPaths.UPDATE_FOR_MOVE_DETAILS[1], for_move, false);
/*     */     }
/*     */     
/*  61 */     public SaveLocationChange onCompletion(Download d, boolean for_move, boolean on_event) { DownloadManager dm = ((DownloadImpl)d).getDownload();
/*  62 */       DownloadManagerDefaultPaths.MovementInformation mi = DownloadManagerDefaultPaths.getTagMovementInformation(dm, DownloadManagerDefaultPaths.COMPLETION_DETAILS);
/*  63 */       return DownloadManagerDefaultPaths.determinePaths(dm, mi, for_move, false);
/*     */     }
/*     */     
/*  66 */     public SaveLocationChange testOnCompletion(Download d, boolean for_move, boolean on_event) { DownloadManager dm = ((DownloadImpl)d).getDownload();
/*  67 */       DownloadManagerDefaultPaths.MovementInformation mi = DownloadManagerDefaultPaths.getTagMovementInformation(dm, DownloadManagerDefaultPaths.COMPLETION_DETAILS);
/*  68 */       return DownloadManagerDefaultPaths.determinePaths(dm, mi, for_move, true);
/*     */     }
/*     */     
/*  71 */     public SaveLocationChange onRemoval(Download d, boolean for_move, boolean on_event) { DownloadManager dm = ((DownloadImpl)d).getDownload();
/*  72 */       return DownloadManagerDefaultPaths.determinePaths(dm, DownloadManagerDefaultPaths.REMOVAL_DETAILS, for_move, false);
/*     */     }
/*     */     
/*  75 */     public boolean isInDefaultSaveDir(Download d) { DownloadManager dm = ((DownloadImpl)d).getDownload();
/*  76 */       return DownloadManagerDefaultPaths.isInDefaultDownloadDir(dm);
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final MovementInformation COMPLETION_DETAILS;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final MovementInformation REMOVAL_DETAILS;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final MovementInformation[] UPDATE_FOR_MOVE_DETAILS;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 102 */   private static final TargetSpecification[] DEFAULT_DIRS = new TargetSpecification[3];
/* 103 */   static { TargetSpecification dest = new TargetSpecification(null);
/* 104 */     dest.setBoolean("enabled", true);
/* 105 */     dest.setString("target", "Default save path");
/* 106 */     dest.setContext("default save dir");
/* 107 */     DEFAULT_DIRS[0] = dest;
/*     */     
/*     */ 
/* 110 */     SourceSpecification source = new SourceSpecification(null);
/* 111 */     source.setBoolean("default dir", "Move Only When In Default Save Dir");
/* 112 */     source.setBoolean("default subdir", "File.move.subdir_is_default");
/* 113 */     source.setBoolean("incomplete dl", false);
/*     */     
/* 115 */     dest = new TargetSpecification(null);
/* 116 */     dest.setBoolean("enabled", "Move Completed When Done");
/* 117 */     dest.setString("target", "Completed Files Directory");
/* 118 */     dest.setContext("completed files dir");
/* 119 */     dest.setBoolean("torrent", "Move Torrent When Done");
/* 120 */     dest.setString("torrent_path", "Move Torrent When Done Directory");
/*     */     
/* 122 */     TransferSpecification trans = new TransferSpecification(null);
/*     */     
/* 124 */     MovementInformation mi_1 = new MovementInformation(source, dest, trans, "Move on completion");
/* 125 */     COMPLETION_DETAILS = mi_1;
/* 126 */     DEFAULT_DIRS[1] = dest;
/*     */     
/*     */ 
/* 129 */     source = new SourceSpecification(null);
/* 130 */     source.setBoolean("default dir", "File.move.download.removed.only_in_default");
/* 131 */     source.setBoolean("default subdir", "File.move.subdir_is_default");
/* 132 */     source.setBoolean("incomplete dl", false);
/*     */     
/* 134 */     dest = new TargetSpecification(null);
/* 135 */     dest.setBoolean("enabled", "File.move.download.removed.enabled");
/* 136 */     dest.setString("target", "File.move.download.removed.path");
/* 137 */     dest.setContext("removed files dir");
/* 138 */     dest.setBoolean("torrent", "File.move.download.removed.move_torrent");
/* 139 */     dest.setString("torrent_path", "File.move.download.removed.move_torrent_path");
/*     */     
/* 141 */     trans = new TransferSpecification(null);
/*     */     
/* 143 */     mi_1 = new MovementInformation(source, dest, trans, "Move on removal");
/* 144 */     REMOVAL_DETAILS = mi_1;
/* 145 */     DEFAULT_DIRS[2] = dest;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 161 */     source = new SourceSpecification(null);
/* 162 */     source.updateSettings(COMPLETION_DETAILS.source.getSettings());
/* 163 */     source.setBoolean("default dir", true);
/*     */     
/* 165 */     mi_1 = new MovementInformation(source, COMPLETION_DETAILS.target, COMPLETION_DETAILS.transfer, "Update completed download");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 170 */     source = new SourceSpecification(null);
/* 171 */     source.setBoolean("default dir", true);
/* 172 */     source.setBoolean("default subdir", "File.move.subdir_is_default");
/* 173 */     source.setBoolean("incomplete dl", true);
/*     */     
/* 175 */     dest = new TargetSpecification(null);
/* 176 */     dest.setBoolean("enabled", true);
/* 177 */     dest.setString("target", "Default save path");
/* 178 */     dest.setBoolean("torrent", false);
/*     */     
/* 180 */     trans = new TransferSpecification(null);
/*     */     
/*     */ 
/* 183 */     MovementInformation mi_2 = new MovementInformation(source, dest, trans, "Update incomplete download");
/* 184 */     UPDATE_FOR_MOVE_DETAILS = new MovementInformation[] { mi_1, mi_2 };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static final String SUBDIR_PARAM = "File.move.subdir_is_default";
/*     */   
/*     */   private static MovementInformation getTagMovementInformation(DownloadManager dm, MovementInformation def_mi)
/*     */   {
/* 193 */     List<Tag> dm_tags = TagManagerFactory.getTagManager().getTagsForTaggable(dm);
/*     */     
/* 195 */     if ((dm_tags == null) || (dm_tags.size() == 0))
/*     */     {
/* 197 */       return def_mi;
/*     */     }
/*     */     
/* 200 */     List<Tag> applicable_tags = new ArrayList();
/*     */     
/* 202 */     for (Tag tag : dm_tags)
/*     */     {
/* 204 */       if (tag.getTagType().hasTagTypeFeature(16L))
/*     */       {
/* 206 */         TagFeatureFileLocation fl = (TagFeatureFileLocation)tag;
/*     */         
/* 208 */         if (fl.supportsTagMoveOnComplete())
/*     */         {
/* 210 */           File move_to = fl.getTagMoveOnCompleteFolder();
/*     */           
/* 212 */           if (move_to != null)
/*     */           {
/* 214 */             if (!move_to.exists())
/*     */             {
/* 216 */               move_to.mkdirs();
/*     */             }
/*     */             
/* 219 */             if ((move_to.isDirectory()) && (move_to.canWrite()))
/*     */             {
/* 221 */               applicable_tags.add(tag);
/*     */             }
/*     */             else
/*     */             {
/* 225 */               logInfo("Ignoring invalid tag move-to location: " + move_to, dm);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 232 */     if (applicable_tags.size() == 0)
/*     */     {
/* 234 */       return def_mi;
/*     */     }
/* 236 */     if (applicable_tags.size() > 1)
/*     */     {
/* 238 */       Collections.sort(applicable_tags, new Comparator()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public int compare(Tag o1, Tag o2)
/*     */         {
/*     */ 
/*     */ 
/* 247 */           return o1.getTagID() - o2.getTagID();
/*     */         }
/*     */         
/* 250 */       });
/* 251 */       String str = "";
/*     */       
/* 253 */       for (Tag tag : applicable_tags)
/*     */       {
/* 255 */         str = str + (str.length() == 0 ? "" : ", ") + tag.getTagName(true);
/*     */       }
/*     */       
/* 258 */       logInfo("Multiple applicable tags found: " + str + " - selecting first", dm);
/*     */     }
/*     */     
/* 261 */     Tag tag_target = (Tag)applicable_tags.get(0);
/*     */     
/* 263 */     TagFeatureFileLocation fl = (TagFeatureFileLocation)tag_target;
/*     */     
/* 265 */     File move_to = fl.getTagMoveOnCompleteFolder();
/*     */     
/* 267 */     if (move_to != null)
/*     */     {
/* 269 */       long options = fl.getTagMoveOnCompleteOptions();
/*     */       
/* 271 */       boolean move_data = (options & 1L) != 0L;
/* 272 */       boolean move_torrent = (options & 0x2) != 0L;
/*     */       
/* 274 */       SourceSpecification source = new SourceSpecification(null);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 279 */       if (def_mi.target.getBoolean("enabled", false)) {
/* 280 */         source.setBoolean("default dir", "Move Only When In Default Save Dir");
/* 281 */         source.setBoolean("default subdir", "File.move.subdir_is_default");
/*     */       } else {
/* 283 */         source.setBoolean("default dir", false);
/*     */       }
/*     */       
/* 286 */       source.setBoolean("incomplete dl", false);
/*     */       
/* 288 */       TargetSpecification dest = new TargetSpecification(null);
/*     */       
/* 290 */       if (move_data)
/*     */       {
/* 292 */         dest.setBoolean("enabled", true);
/* 293 */         dest.setString("target_raw", move_to.getAbsolutePath());
/*     */       }
/*     */       else
/*     */       {
/* 297 */         dest.setBoolean("enabled", def_mi.target.getBoolean("enabled", false));
/*     */       }
/*     */       
/* 300 */       dest.setContext("Tag '" + tag_target.getTagName(true) + "' move-on-complete directory");
/*     */       
/* 302 */       if (move_torrent)
/*     */       {
/* 304 */         dest.setBoolean("torrent", true);
/* 305 */         dest.setString("torrent_path_raw", move_to.getAbsolutePath());
/*     */       }
/*     */       else
/*     */       {
/* 309 */         dest.setBoolean("torrent", "Move Torrent When Done");
/* 310 */         dest.setString("torrent_path", "Move Torrent When Done Directory");
/*     */       }
/*     */       
/* 313 */       TransferSpecification trans = new TransferSpecification(null);
/*     */       
/* 315 */       MovementInformation tag_mi = new MovementInformation(source, dest, trans, "Tag Move on Completion");
/*     */       
/* 317 */       return tag_mi;
/*     */     }
/*     */     
/* 320 */     return def_mi;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String normaliseRelativePathPart(String name)
/*     */   {
/* 328 */     name = name.trim();
/* 329 */     if (name.length() == 0) return "";
/* 330 */     if ((name.equals(".")) || (name.equals(".."))) {
/* 331 */       return null;
/*     */     }
/* 333 */     return FileUtil.convertOSSpecificChars(name, false).trim();
/*     */   }
/*     */   
/*     */   public static File normaliseRelativePath(File path) {
/* 337 */     if (path.isAbsolute()) { return null;
/*     */     }
/* 339 */     File parent = path.getParentFile();
/* 340 */     String child_name = normaliseRelativePathPart(path.getName());
/* 341 */     if (child_name == null) {
/* 342 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 346 */     if (parent == null) {
/* 347 */       return new File(child_name);
/*     */     }
/*     */     
/* 350 */     ArrayList parts = new ArrayList();
/* 351 */     parts.add(child_name);
/*     */     
/* 353 */     String filepart = null;
/* 354 */     while (parent != null) {
/* 355 */       filepart = normaliseRelativePathPart(parent.getName());
/* 356 */       if (filepart == null) return null;
/* 357 */       if (filepart.length() != 0)
/* 358 */         parts.add(0, filepart);
/* 359 */       parent = parent.getParentFile();
/*     */     }
/*     */     
/* 362 */     StringBuilder sb = new StringBuilder((String)parts.get(0));
/* 363 */     for (int i = 1; i < parts.size(); i++) {
/* 364 */       sb.append(File.separatorChar);
/* 365 */       sb.append(parts.get(i));
/*     */     }
/*     */     
/* 368 */     return new File(sb.toString());
/*     */   }
/*     */   
/*     */   private static File[] getDefaultDirs() {
/* 372 */     List results = new ArrayList();
/* 373 */     File location = null;
/* 374 */     TargetSpecification ts = null;
/* 375 */     for (int i = 0; i < DEFAULT_DIRS.length; i++) {
/* 376 */       ts = DEFAULT_DIRS[i];
/* 377 */       File[] targets = ts.getTargets(null, ts);
/* 378 */       if (targets[0] != null) {
/* 379 */         results.add(targets[0]);
/*     */       }
/*     */     }
/* 382 */     return (File[])results.toArray(new File[results.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static SaveLocationChange determinePaths(DownloadManager dm, MovementInformation mi, boolean check_source, boolean is_test)
/*     */   {
/* 391 */     boolean proceed = (!check_source) || (mi.source.matchesDownload(dm, mi, is_test));
/* 392 */     if (!proceed) {
/* 393 */       logInfo("Cannot consider " + describe(dm, mi) + " - does not match source criteria.", dm);
/*     */       
/* 395 */       return null;
/*     */     }
/*     */     
/* 398 */     File[] target_paths = mi.target.getTargets(dm, mi);
/* 399 */     if ((target_paths[0] == null) && (target_paths[1] == null)) {
/* 400 */       logInfo("Unable to determine an appropriate target for " + describe(dm, mi) + ".", dm);
/*     */       
/* 402 */       return null;
/*     */     }
/*     */     
/* 405 */     logInfo("Determined path for " + describe(dm, mi) + ".", dm);
/* 406 */     return mi.transfer.getTransferDetails(dm, mi, target_paths);
/*     */   }
/*     */   
/*     */ 
/*     */   static boolean isInDefaultDownloadDir(DownloadManager dm)
/*     */   {
/* 412 */     SourceSpecification source = new SourceSpecification(null);
/* 413 */     source.setBoolean("default subdir", "File.move.subdir_is_default");
/* 414 */     return source.checkDefaultDir(dm.getSaveLocation().getParentFile(), getDefaultDirs());
/*     */   }
/*     */   
/*     */   private static abstract interface ContextDescriptor { public abstract String getContext();
/*     */   }
/*     */   
/*     */   private static class MovementInformation implements DownloadManagerDefaultPaths.ContextDescriptor { final DownloadManagerDefaultPaths.SourceSpecification source;
/*     */     final DownloadManagerDefaultPaths.TargetSpecification target;
/*     */     final DownloadManagerDefaultPaths.TransferSpecification transfer;
/*     */     final String title;
/*     */     
/* 425 */     MovementInformation(DownloadManagerDefaultPaths.SourceSpecification source, DownloadManagerDefaultPaths.TargetSpecification target, DownloadManagerDefaultPaths.TransferSpecification transfer, String title) { this.source = source;
/* 426 */       this.target = target;
/* 427 */       this.transfer = transfer;
/* 428 */       this.title = title;
/*     */     }
/*     */     
/* 431 */     public String getContext() { return this.title; }
/*     */   }
/*     */   
/*     */   private static abstract class ParameterHelper implements DownloadManagerDefaultPaths.ContextDescriptor {
/* 435 */     private final Map settings = new HashMap();
/* 436 */     private String context = null;
/*     */     
/*     */     protected boolean getBoolean(String key, boolean def) {
/* 439 */       Object result = this.settings.get(key);
/* 440 */       if (result == null) return def;
/* 441 */       if ((result instanceof Boolean)) return ((Boolean)result).booleanValue();
/* 442 */       return COConfigurationManager.getBooleanParameter((String)result);
/*     */     }
/*     */     
/*     */     protected void setBoolean(String key, boolean value) {
/* 446 */       this.settings.put(key, Boolean.valueOf(value));
/*     */     }
/*     */     
/*     */     protected void setBoolean(String key, String param) {
/* 450 */       this.settings.put(key, param);
/*     */     }
/*     */     
/*     */     protected void setString(String key, String param) {
/* 454 */       this.settings.put(key, param);
/*     */     }
/*     */     
/*     */     protected String getStringRaw(String key) {
/* 458 */       return (String)this.settings.get(key);
/*     */     }
/*     */     
/*     */     protected String getString(String key, String def) {
/* 462 */       String result = (String)this.settings.get(key);
/* 463 */       if (result == null) { return def;
/*     */       }
/* 465 */       return COConfigurationManager.getStringParameter(result);
/*     */     }
/*     */     
/* 468 */     public Map getSettings() { return this.settings; }
/* 469 */     public void updateSettings(Map settings) { this.settings.putAll(settings); }
/*     */     
/* 471 */     public String getContext() { return this.context; }
/* 472 */     public void setContext(String context) { this.context = context; }
/*     */   }
/*     */   
/* 475 */   private static class SourceSpecification extends DownloadManagerDefaultPaths.ParameterHelper { private SourceSpecification() { super(); }
/*     */     
/*     */     public boolean matchesDownload(DownloadManager dm, DownloadManagerDefaultPaths.ContextDescriptor context, boolean ignore_completeness) {
/* 478 */       if (getBoolean("default dir", false)) {
/* 479 */         DownloadManagerMoveHandlerUtils.logInfo("Checking if " + DownloadManagerDefaultPaths.describe(dm, context) + " is inside default dirs.", dm);
/* 480 */         File[] default_dirs = DownloadManagerDefaultPaths.access$900();
/* 481 */         File current_location = dm.getSaveLocation().getParentFile();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 487 */         if (current_location == null) {
/* 488 */           DownloadManagerMoveHandlerUtils.logWarn(DownloadManagerDefaultPaths.describe(dm, context) + " appears to have a malformed save directory, skipping.", dm);
/* 489 */           return false;
/*     */         }
/*     */         
/* 492 */         if (!checkDefaultDir(current_location, default_dirs)) {
/* 493 */           DownloadManagerMoveHandlerUtils.logWarn(DownloadManagerDefaultPaths.describe(dm, context) + " doesn't exist in any of the following default directories" + " (current dir: " + current_location + ", subdirectories checked: " + getBoolean("default subdir", false) + ") - " + Arrays.asList(default_dirs), dm);
/*     */           
/*     */ 
/*     */ 
/* 497 */           return false;
/*     */         }
/* 499 */         DownloadManagerMoveHandlerUtils.logInfo(DownloadManagerDefaultPaths.describe(dm, context) + " does exist inside default dirs.", dm);
/*     */       }
/*     */       
/*     */ 
/* 503 */       if (!dm.isDownloadComplete(false)) {
/* 504 */         boolean can_move = (ignore_completeness) || (getBoolean("incomplete dl", false));
/* 505 */         String log_message = DownloadManagerDefaultPaths.describe(dm, context) + " is incomplete which is " + (can_move ? "" : "not ") + "an appropriate state.";
/*     */         
/* 507 */         if (!can_move) {
/* 508 */           DownloadManagerMoveHandlerUtils.logInfo(log_message, dm);
/* 509 */           return false;
/*     */         }
/*     */       }
/*     */       
/* 513 */       return true;
/*     */     }
/*     */     
/*     */     public boolean checkDefaultDir(File location, File[] default_dirs) {
/* 517 */       location = FileUtil.canonise(location);
/* 518 */       boolean subdir = getBoolean("default subdir", false);
/* 519 */       for (int i = 0; i < default_dirs.length; i++) {
/* 520 */         if (subdir) {
/* 521 */           if (FileUtil.isAncestorOf(default_dirs[i], location)) { return true;
/*     */           }
/*     */         }
/* 524 */         else if (default_dirs[i].equals(location)) { return true;
/*     */         }
/*     */       }
/* 527 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class TargetSpecification extends DownloadManagerDefaultPaths.ParameterHelper {
/* 532 */     private TargetSpecification() { super(); }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public File[] getTargets(DownloadManager dm, DownloadManagerDefaultPaths.ContextDescriptor cd)
/*     */     {
/* 539 */       boolean data_enabled = getBoolean("enabled", false);
/*     */       
/* 541 */       if (!data_enabled)
/*     */       {
/* 543 */         DownloadManagerMoveHandlerUtils.logInfo("Data target for " + DownloadManagerDefaultPaths.describe(dm, cd) + " is not enabled.", dm);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 550 */       String location = getStringRaw("target_raw");
/* 551 */       if (location == null) {
/* 552 */         location = getString("target", null);
/* 553 */         if (location != null)
/* 554 */           location = location.trim(); }
/*     */       File data_target;
/*     */       File data_target;
/* 557 */       if ((location == null) || (location.length() == 0)) {
/* 558 */         DownloadManagerMoveHandlerUtils.logInfo("No explicit data target for " + DownloadManagerDefaultPaths.describe(dm, cd) + ".", dm);
/* 559 */         data_target = null;
/*     */       }
/*     */       else {
/* 562 */         data_target = new File(FileUtil.getCanonicalFileName(location));
/* 563 */         String relative_path = null;
/*     */         
/* 565 */         if ((dm != null) && (dm.getDownloadState() != null)) {
/* 566 */           relative_path = dm.getDownloadState().getRelativeSavePath();
/*     */         }
/*     */         
/* 569 */         if ((relative_path != null) && (relative_path.length() > 0)) {
/* 570 */           DownloadManagerMoveHandlerUtils.logInfo("Consider relative save path: " + relative_path, dm);
/*     */           
/*     */ 
/*     */ 
/* 574 */           data_target = new File(data_target.getPath() + File.separator + relative_path);
/*     */         }
/*     */       }
/*     */       
/* 578 */       boolean torrent_enabled = getBoolean("torrent", false);
/*     */       File torrent_target;
/* 580 */       File torrent_target; if (!torrent_enabled)
/*     */       {
/* 582 */         DownloadManagerMoveHandlerUtils.logInfo("Torrent target for " + DownloadManagerDefaultPaths.describe(dm, cd) + " is not enabled.", dm);
/*     */         
/* 584 */         torrent_target = null;
/*     */       }
/*     */       else
/*     */       {
/* 588 */         torrent_target = data_target;
/*     */         
/*     */ 
/*     */ 
/* 592 */         String torrent_path = getStringRaw("torrent_path_raw");
/*     */         
/* 594 */         if (torrent_path == null)
/*     */         {
/* 596 */           torrent_path = getString("torrent_path", null);
/*     */         }
/*     */         
/* 599 */         if ((torrent_path != null) && (torrent_path.trim().length() > 0))
/*     */         {
/* 601 */           File temp = new File(torrent_path);
/*     */           
/* 603 */           if (temp.isDirectory())
/*     */           {
/* 605 */             torrent_target = temp;
/*     */           }
/* 607 */           else if (!temp.exists())
/*     */           {
/* 609 */             if (temp.mkdirs())
/*     */             {
/* 611 */               torrent_target = temp;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 617 */       return new File[] { data_enabled ? data_target : null, torrent_enabled ? torrent_target : null };
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class TransferSpecification
/*     */   {
/*     */     public SaveLocationChange getTransferDetails(DownloadManager dm, DownloadManagerDefaultPaths.ContextDescriptor cd, File[] target_paths)
/*     */     {
/* 627 */       SaveLocationChange result = new SaveLocationChange();
/*     */       
/* 629 */       File data_target = target_paths[0];
/* 630 */       File torrent_target = target_paths[1];
/*     */       
/* 632 */       if (data_target != null)
/*     */       {
/* 634 */         result.download_location = data_target;
/*     */       }
/*     */       
/* 637 */       if (torrent_target != null)
/*     */       {
/* 639 */         result.torrent_location = torrent_target;
/*     */       }
/*     */       
/* 642 */       return result;
/*     */     }
/*     */   }
/*     */   
/*     */   static String describe(DownloadManager dm, ContextDescriptor cs) {
/* 647 */     if (cs == null) return describe(dm);
/* 648 */     if (dm == null) {
/* 649 */       return "\"" + cs.getContext() + "\"";
/*     */     }
/* 651 */     return "\"" + dm.getDisplayName() + "\" with regard to \"" + cs.getContext() + "\"";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/impl/DownloadManagerDefaultPaths.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */