/*     */ package org.gudy.azureus2.core3.download.impl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.savelocation.DefaultSaveLocationManager;
/*     */ import org.gudy.azureus2.plugins.download.savelocation.SaveLocationChange;
/*     */ import org.gudy.azureus2.plugins.download.savelocation.SaveLocationManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DownloadManagerMoveHandler
/*     */   extends DownloadManagerMoveHandlerUtils
/*     */ {
/*  37 */   public static SaveLocationManager CURRENT_HANDLER = DownloadManagerDefaultPaths.DEFAULT_HANDLER;
/*     */   
/*     */   private static boolean isApplicableDownload(DownloadManager dm) {
/*  40 */     if (!dm.isPersistent()) {
/*  41 */       logInfo(describe(dm) + " is not persistent.", dm);
/*  42 */       return false;
/*     */     }
/*     */     
/*  45 */     if (dm.getDownloadState().getFlag(4L)) {
/*  46 */       logInfo(describe(dm) + " has exclusion flag set.", dm);
/*  47 */       return false;
/*     */     }
/*     */     
/*  50 */     return true;
/*     */   }
/*     */   
/*     */   public static SaveLocationChange onInitialisation(DownloadManager dm) {
/*  54 */     if (!isApplicableDownload(dm)) return null;
/*  55 */     try { return CURRENT_HANDLER.onInitialization(PluginCoreUtils.wrap(dm), true, true);
/*     */     } catch (Exception e) {
/*  57 */       logError("Error trying to determine initial download location.", dm, e); }
/*  58 */     return null;
/*     */   }
/*     */   
/*     */   public static SaveLocationChange onRemoval(DownloadManager dm)
/*     */   {
/*  63 */     if (!isApplicableDownload(dm)) return null;
/*  64 */     try { return CURRENT_HANDLER.onRemoval(PluginCoreUtils.wrap(dm), true, true);
/*     */     } catch (Exception e) {
/*  66 */       logError("Error trying to determine on-removal location.", dm, e); }
/*  67 */     return null;
/*     */   }
/*     */   
/*     */   public static SaveLocationChange onCompletion(DownloadManager dm, MoveCallback callback)
/*     */   {
/*  72 */     if (!isApplicableDownload(dm)) { return null;
/*     */     }
/*  74 */     if (dm.getDownloadState().getFlag(8L)) {
/*  75 */       logInfo("Completion flag already set on " + describe(dm) + ", skip move-on-completion behaviour.", dm);
/*  76 */       return null;
/*     */     }
/*     */     SaveLocationChange sc;
/*     */     try {
/*  80 */       sc = CURRENT_HANDLER.onCompletion(PluginCoreUtils.wrap(dm), true, true);
/*     */     } catch (Exception e) {
/*  82 */       logError("Error trying to determine on-completion location.", dm, e);
/*  83 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  89 */     if ((callback != null) && (sc != null))
/*     */     {
/*  91 */       callback.perform(sc);
/*     */     }
/*     */     
/*  94 */     logInfo("Setting completion flag on " + describe(dm) + ", may have been set before.", dm);
/*  95 */     dm.getDownloadState().setFlag(8L, true);
/*  96 */     return sc;
/*     */   }
/*     */   
/*     */   public static boolean canGoToCompleteDir(DownloadManager dm) {
/* 100 */     return (dm.isDownloadComplete(false)) && (isOnCompleteEnabled());
/*     */   }
/*     */   
/*     */   public static boolean isOnCompleteEnabled() {
/* 104 */     return COConfigurationManager.getBooleanParameter("Move Completed When Done");
/*     */   }
/*     */   
/*     */   public static boolean isOnRemovalEnabled() {
/* 108 */     return COConfigurationManager.getBooleanParameter("File.move.download.removed.enabled");
/*     */   }
/*     */   
/*     */   public static SaveLocationChange recalculatePath(DownloadManager dm) {
/* 112 */     Download download = PluginCoreUtils.wrap(dm);
/* 113 */     SaveLocationChange result = null;
/* 114 */     if (canGoToCompleteDir(dm)) {
/* 115 */       result = CURRENT_HANDLER.onCompletion(download, true, false);
/*     */     }
/* 117 */     if (result == null) {
/* 118 */       result = CURRENT_HANDLER.onInitialization(download, true, false);
/*     */     }
/* 120 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static File[] getRelatedDirs(DownloadManager dm)
/*     */   {
/* 128 */     ArrayList result = new ArrayList();
/* 129 */     Download d = PluginCoreUtils.wrap(dm);
/*     */     
/* 131 */     if (isOnCompleteEnabled()) {
/* 132 */       addFile(result, COConfigurationManager.getStringParameter("Completed Files Directory"));
/* 133 */       addFile(result, CURRENT_HANDLER.onCompletion(d, false, false));
/* 134 */       addFile(result, DownloadManagerDefaultPaths.DEFAULT_HANDLER.onCompletion(d, false, false));
/*     */     }
/* 136 */     if (isOnRemovalEnabled()) {
/* 137 */       addFile(result, COConfigurationManager.getStringParameter("File.move.download.removed.path"));
/* 138 */       addFile(result, CURRENT_HANDLER.onRemoval(d, false, false));
/* 139 */       addFile(result, DownloadManagerDefaultPaths.DEFAULT_HANDLER.onRemoval(d, false, false));
/*     */     }
/* 141 */     return (File[])result.toArray(new File[result.size()]);
/*     */   }
/*     */   
/*     */   private static void addFile(ArrayList l, SaveLocationChange slc) {
/* 145 */     if (slc != null) addFile(l, slc.download_location);
/*     */   }
/*     */   
/*     */   private static void addFile(ArrayList l, File f) {
/* 149 */     if ((f != null) && (!l.contains(f))) l.add(f);
/*     */   }
/*     */   
/*     */   private static void addFile(ArrayList l, String s) {
/* 153 */     if ((s != null) && (s.trim().length() != 0)) addFile(l, new File(s));
/*     */   }
/*     */   
/*     */   public static abstract interface MoveCallback
/*     */   {
/*     */     public abstract void perform(SaveLocationChange paramSaveLocationChange);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/impl/DownloadManagerMoveHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */