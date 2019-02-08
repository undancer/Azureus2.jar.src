/*     */ package org.gudy.azureus2.plugins.download.savelocation;
/*     */ 
/*     */ import java.io.File;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SaveLocationChange
/*     */ {
/*  33 */   public File download_location = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  38 */   public String download_name = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  43 */   public File torrent_location = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  48 */   public String torrent_name = null;
/*     */   
/*     */ 
/*     */ 
/*     */   public final String toString()
/*     */   {
/*  54 */     StringBuilder res = new StringBuilder("SaveLocationChange: ");
/*  55 */     res.append("DL-LOC=");
/*  56 */     res.append(this.download_location);
/*  57 */     res.append(", DL-NAME=");
/*  58 */     res.append(this.download_name);
/*  59 */     res.append(", TOR-LOC=");
/*  60 */     res.append(this.torrent_location);
/*  61 */     res.append(", TOR-NAME=");
/*  62 */     res.append(this.torrent_name);
/*  63 */     return res.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final File normaliseTorrentLocation(File old_torrent_location)
/*     */   {
/*  71 */     return normaliseTorrentLocation(old_torrent_location.getParentFile(), old_torrent_location.getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final File normaliseTorrentLocation(File old_torrent_directory, String old_torrent_name)
/*     */   {
/*  79 */     return new File(this.torrent_location != null ? this.torrent_location : old_torrent_directory, this.torrent_name != null ? this.torrent_name : old_torrent_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final File normaliseDownloadLocation(File old_download_location)
/*     */   {
/*  90 */     return normaliseDownloadLocation(old_download_location.getParentFile(), old_download_location.getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final File normaliseDownloadLocation(File old_download_directory, String old_download_name)
/*     */   {
/*  98 */     return new File(this.download_location != null ? this.download_location : old_download_directory, this.download_name != null ? this.download_name : old_download_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean hasDownloadChange()
/*     */   {
/* 109 */     return (this.download_location != null) || (this.download_name != null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean hasTorrentChange()
/*     */   {
/* 117 */     return (this.torrent_location != null) || (this.torrent_name != null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean isDifferentDownloadLocation(File current_location)
/*     */   {
/* 126 */     if (!hasDownloadChange()) return false;
/* 127 */     return !current_location.equals(normaliseDownloadLocation(current_location));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean isDifferentTorrentLocation(File current_location)
/*     */   {
/* 136 */     if (!hasTorrentChange()) return false;
/* 137 */     return !current_location.equals(normaliseTorrentLocation(current_location));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/savelocation/SaveLocationChange.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */