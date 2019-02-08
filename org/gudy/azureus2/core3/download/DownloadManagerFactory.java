/*    */ package org.gudy.azureus2.core3.download;
/*    */ 
/*    */ import java.util.List;
/*    */ import org.gudy.azureus2.core3.download.impl.DownloadManagerAvailabilityImpl;
/*    */ import org.gudy.azureus2.core3.download.impl.DownloadManagerImpl;
/*    */ import org.gudy.azureus2.core3.global.GlobalManager;
/*    */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DownloadManagerFactory
/*    */ {
/*    */   public static DownloadManager create(GlobalManager gm, byte[] torrent_hash, String torrentFileName, String savePath, String saveFile, int initialState, boolean persistent, boolean for_seeding, List file_priorities, DownloadManagerInitialisationAdapter adapter)
/*    */   {
/* 54 */     return new DownloadManagerImpl(gm, torrent_hash, torrentFileName, savePath, saveFile, initialState, persistent, false, for_seeding, false, file_priorities, adapter);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static DownloadManager create(GlobalManager gm, byte[] torrent_hash, String torrentFileName, String torrent_save_dir, String torrent_save_file, int initialState, boolean persistent, boolean recovered, boolean has_ever_been_started, List file_priorities)
/*    */   {
/* 72 */     return new DownloadManagerImpl(gm, torrent_hash, torrentFileName, torrent_save_dir, torrent_save_file, initialState, persistent, recovered, false, has_ever_been_started, file_priorities, null);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static DownloadManagerAvailability getAvailability(TOTorrent torrent, List<List<String>> updated_trackers, String[] enabled_peer_sources, String[] enabled_networks)
/*    */   {
/* 82 */     return new DownloadManagerAvailabilityImpl(torrent, updated_trackers, enabled_peer_sources, enabled_networks);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static void addGlobalDownloadListener(DownloadManagerListener listener)
/*    */   {
/* 89 */     DownloadManagerImpl.addGlobalDownloadListener(listener);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static void removeGlobalDownloadListener(DownloadManagerListener listener)
/*    */   {
/* 96 */     DownloadManagerImpl.removeGlobalDownloadListener(listener);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/DownloadManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */