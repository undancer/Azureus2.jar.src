/*     */ package com.aelitis.azureus.ui.swt.subscriptions;
/*     */ 
/*     */ import com.aelitis.azureus.core.subs.Subscription;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*     */ import com.aelitis.azureus.ui.selectedcontent.DownloadUrlInfo;
/*     */ import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
/*     */ import com.aelitis.azureus.ui.selectedcontent.ISelectedVuzeFileContent;
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentCreator;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SubscriptionSelectedContent
/*     */   implements ISelectedVuzeFileContent
/*     */ {
/*     */   private Subscription subs;
/*     */   private TOTorrent torrent;
/*     */   
/*     */   protected SubscriptionSelectedContent(Subscription _subs)
/*     */   {
/*  55 */     this.subs = _subs;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDisplayName()
/*     */   {
/*  61 */     return MessageText.getString("subscriptions.column.name") + ": " + this.subs.getName();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getHash()
/*     */   {
/*  67 */     return this.subs.getID();
/*     */   }
/*     */   
/*     */   public VuzeFile getVuzeFile()
/*     */   {
/*     */     try
/*     */     {
/*  74 */       return this.subs.getVuzeFile();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  78 */       Debug.out(e);
/*     */     }
/*     */     
/*  81 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public TOTorrent getTorrent()
/*     */   {
/*  87 */     synchronized (this)
/*     */     {
/*  89 */       if (this.torrent == null)
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/*     */ 
/*  96 */           VuzeFile vf = this.subs.getVuzeFile();
/*     */           
/*     */ 
/*     */ 
/* 100 */           if (vf != null)
/*     */           {
/* 102 */             File f1 = AETemporaryFileHandler.createTempFile();
/*     */             
/* 104 */             File f = new File(f1.getParent(), "Update Vuze to access this share_" + f1.getName());
/*     */             
/* 106 */             f1.delete();
/*     */             
/*     */             try
/*     */             {
/* 110 */               vf.write(f);
/*     */               
/* 112 */               TOTorrentCreator cr = TOTorrentFactory.createFromFileOrDirWithComputedPieceLength(f, new URL("dht://"));
/*     */               
/* 114 */               TOTorrent temp = cr.create();
/*     */               
/* 116 */               Map vuze_map = vf.exportToMap();
/* 117 */               Map torrent_map = temp.serialiseToMap();
/*     */               
/* 119 */               torrent_map.putAll(vuze_map);
/*     */               
/* 121 */               this.torrent = TOTorrentFactory.deserialiseFromMap(torrent_map);
/*     */             }
/*     */             finally
/*     */             {
/* 125 */               f.delete();
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 130 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 135 */     return this.torrent;
/*     */   }
/*     */   
/*     */   public void setHash(String hash) {}
/*     */   
/*     */   public DownloadManager getDownloadManager()
/*     */   {
/* 142 */     return null;
/*     */   }
/*     */   
/*     */   public int getFileIndex() {
/* 146 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setDownloadManager(DownloadManager dm) {}
/*     */   
/*     */ 
/*     */   public void setTorrent(TOTorrent torrent) {}
/*     */   
/*     */   public void setDisplayName(String displayName) {}
/*     */   
/*     */   public DownloadUrlInfo getDownloadInfo()
/*     */   {
/* 159 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDownloadInfo(DownloadUrlInfo downloadInfo) {}
/*     */   
/*     */ 
/*     */   public boolean sameAs(ISelectedContent _other)
/*     */   {
/* 169 */     if (_other == this)
/*     */     {
/* 171 */       return true;
/*     */     }
/*     */     
/* 174 */     if ((_other instanceof SubscriptionSelectedContent))
/*     */     {
/* 176 */       SubscriptionSelectedContent other = (SubscriptionSelectedContent)_other;
/*     */       
/* 178 */       return this.subs == other.subs;
/*     */     }
/*     */     
/* 181 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/subscriptions/SubscriptionSelectedContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */