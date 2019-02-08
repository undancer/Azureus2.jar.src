/*     */ package com.aelitis.azureus.core.diskmanager.file.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.file.FMFileManagerException;
/*     */ import com.aelitis.azureus.core.diskmanager.file.FMFileOwner;
/*     */ import java.io.File;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FMFileTestImpl
/*     */   extends FMFileUnlimited
/*     */ {
/*     */   protected long file_offset_in_torrent;
/*     */   
/*     */   protected FMFileTestImpl(FMFileOwner _owner, FMFileManagerImpl _manager, File _file, int _type)
/*     */     throws FMFileManagerException
/*     */   {
/*  51 */     super(_owner, _manager, _file, _type);
/*     */     
/*  53 */     TOTorrentFile torrent_file = getOwner().getTorrentFile();
/*     */     
/*  55 */     TOTorrent torrent = torrent_file.getTorrent();
/*     */     
/*  57 */     for (int i = 0; i < torrent.getFiles().length; i++)
/*     */     {
/*  59 */       TOTorrentFile f = torrent.getFiles()[i];
/*     */       
/*  61 */       if (f == torrent_file) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/*  66 */       this.file_offset_in_torrent += f.getLength();
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void readSupport(DirectByteBuffer buffer, long offset)
/*     */     throws FMFileManagerException
/*     */   {
/*  87 */     buffer.position((byte)4, buffer.limit((byte)4));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writeSupport(DirectByteBuffer[] buffers, long offset)
/*     */     throws FMFileManagerException
/*     */   {
/*  98 */     offset += this.file_offset_in_torrent;
/*     */     
/* 100 */     for (int i = 0; i < buffers.length; i++)
/*     */     {
/* 102 */       DirectByteBuffer buffer = buffers[i];
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 124 */       buffer.position((byte)4, buffer.limit((byte)4));
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/file/impl/FMFileTestImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */