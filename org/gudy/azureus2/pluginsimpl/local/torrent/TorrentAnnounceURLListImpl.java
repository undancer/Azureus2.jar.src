/*     */ package org.gudy.azureus2.pluginsimpl.local.torrent;
/*     */ 
/*     */ import java.net.URL;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAnnounceURLList;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAnnounceURLListSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TorrentAnnounceURLListImpl
/*     */   implements TorrentAnnounceURLList
/*     */ {
/*     */   protected TorrentImpl torrent;
/*     */   
/*     */   protected TorrentAnnounceURLListImpl(TorrentImpl _torrent)
/*     */   {
/*  46 */     this.torrent = _torrent;
/*     */   }
/*     */   
/*     */ 
/*     */   public TorrentAnnounceURLListSet[] getSets()
/*     */   {
/*  52 */     TOTorrentAnnounceURLGroup group = this.torrent.getTorrent().getAnnounceURLGroup();
/*     */     
/*  54 */     TOTorrentAnnounceURLSet[] sets = group.getAnnounceURLSets();
/*     */     
/*  56 */     TorrentAnnounceURLListSet[] res = new TorrentAnnounceURLListSet[sets.length];
/*     */     
/*  58 */     for (int i = 0; i < res.length; i++)
/*     */     {
/*  60 */       res[i] = new TorrentAnnounceURLListSetImpl(this, sets[i]);
/*     */     }
/*     */     
/*  63 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSets(TorrentAnnounceURLListSet[] sets)
/*     */   {
/*  70 */     TOTorrentAnnounceURLGroup group = this.torrent.getTorrent().getAnnounceURLGroup();
/*     */     
/*  72 */     TOTorrentAnnounceURLSet[] res = new TOTorrentAnnounceURLSet[sets.length];
/*     */     
/*  74 */     for (int i = 0; i < res.length; i++)
/*     */     {
/*  76 */       res[i] = ((TorrentAnnounceURLListSetImpl)sets[i]).getSet();
/*     */     }
/*     */     
/*  79 */     group.setAnnounceURLSets(res);
/*     */     
/*  81 */     updated();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TorrentAnnounceURLListSet create(URL[] urls)
/*     */   {
/*  88 */     return new TorrentAnnounceURLListSetImpl(this, this.torrent.getTorrent().getAnnounceURLGroup().createAnnounceURLSet(urls));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addSet(URL[] urls)
/*     */   {
/*  95 */     if (setAlreadyExists(urls))
/*     */     {
/*  97 */       return;
/*     */     }
/*     */     
/* 100 */     TorrentUtils.announceGroupsInsertLast(this.torrent.getTorrent(), urls);
/*     */     
/* 102 */     updated();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void insertSetAtFront(URL[] urls)
/*     */   {
/* 109 */     if (setAlreadyExists(urls))
/*     */     {
/* 111 */       return;
/*     */     }
/*     */     
/* 114 */     TorrentUtils.announceGroupsInsertFirst(this.torrent.getTorrent(), urls);
/*     */     
/* 116 */     updated();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean setAlreadyExists(URL[] urls)
/*     */   {
/* 123 */     TOTorrentAnnounceURLGroup group = this.torrent.getTorrent().getAnnounceURLGroup();
/*     */     
/* 125 */     TOTorrentAnnounceURLSet[] sets = group.getAnnounceURLSets();
/*     */     
/* 127 */     for (int i = 0; i < sets.length; i++)
/*     */     {
/* 129 */       URL[] u = sets[i].getAnnounceURLs();
/*     */       
/* 131 */       if (u.length == urls.length)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 136 */         boolean all_found = true;
/*     */         
/* 138 */         for (int j = 0; j < urls.length; j++)
/*     */         {
/* 140 */           URL u1 = urls[j];
/*     */           
/* 142 */           boolean this_found = false;
/*     */           
/* 144 */           for (int k = 0; k < u.length; k++)
/*     */           {
/* 146 */             URL u2 = u[k];
/*     */             
/* 148 */             if (u1.toString().equals(u2.toString()))
/*     */             {
/* 150 */               this_found = true;
/*     */               
/* 152 */               break;
/*     */             }
/*     */           }
/*     */           
/* 156 */           if (!this_found)
/*     */           {
/* 158 */             all_found = false;
/*     */             
/* 160 */             break;
/*     */           }
/*     */         }
/*     */         
/* 164 */         if (all_found)
/*     */         {
/* 166 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 170 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void updated()
/*     */   {
/* 176 */     this.torrent.updated();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/torrent/TorrentAnnounceURLListImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */