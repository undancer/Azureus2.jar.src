/*     */ package org.gudy.azureus2.pluginsimpl.remote.disk;
/*     */ 
/*     */ import java.io.File;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerChannel;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerListener;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerRandomReadRequest;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPException;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPObject;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPReply;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequest;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RPDiskManagerFileInfo
/*     */   extends RPObject
/*     */   implements DiskManagerFileInfo
/*     */ {
/*     */   protected transient DiskManagerFileInfo delegate;
/*     */   public int access_mode;
/*     */   public long downloaded;
/*     */   public long length;
/*     */   public File file;
/*     */   public int first_piece_number;
/*     */   public int num_pieces;
/*     */   public boolean is_priority;
/*     */   public boolean is_skipped;
/*     */   
/*     */   public static RPDiskManagerFileInfo create(DiskManagerFileInfo _delegate)
/*     */   {
/*  57 */     RPDiskManagerFileInfo res = (RPDiskManagerFileInfo)_lookupLocal(_delegate);
/*     */     
/*  59 */     if (res == null)
/*     */     {
/*  61 */       res = new RPDiskManagerFileInfo(_delegate);
/*     */     }
/*     */     
/*  64 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected RPDiskManagerFileInfo(DiskManagerFileInfo _delegate)
/*     */   {
/*  71 */     super(_delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void _setDelegate(Object _delegate)
/*     */   {
/*  78 */     this.delegate = ((DiskManagerFileInfo)_delegate);
/*     */     
/*  80 */     this.access_mode = this.delegate.getAccessMode();
/*  81 */     this.downloaded = this.delegate.getDownloaded();
/*  82 */     this.length = this.delegate.getLength();
/*  83 */     this.file = this.delegate.getFile();
/*  84 */     this.first_piece_number = this.delegate.getFirstPieceNumber();
/*  85 */     this.num_pieces = this.delegate.getNumPieces();
/*  86 */     this.is_priority = this.delegate.isPriority();
/*  87 */     this.is_skipped = this.delegate.isSkipped();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object _setLocal()
/*     */     throws RPException
/*     */   {
/*  95 */     return _fixupLocal();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public RPReply _process(RPRequest request)
/*     */   {
/* 102 */     String method = request.getMethod();
/*     */     
/* 104 */     throw new RPException("Unknown method: " + method);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPriority(boolean b)
/*     */   {
/* 113 */     notSupported();
/*     */   }
/*     */   
/*     */   public void setSkipped(boolean b)
/*     */   {
/* 118 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumericPriorty()
/*     */   {
/* 124 */     notSupported();
/*     */     
/* 126 */     return 0;
/*     */   }
/*     */   
/*     */   public int getNumericPriority() {
/* 130 */     notSupported();
/* 131 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setNumericPriority(int priority)
/*     */   {
/* 138 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public void setDeleted(boolean b)
/*     */   {
/* 144 */     notSupported();
/*     */   }
/*     */   
/*     */   public boolean isDeleted()
/*     */   {
/* 149 */     notSupported();
/*     */     
/* 151 */     return false;
/*     */   }
/*     */   
/*     */   public int getAccessMode()
/*     */   {
/* 156 */     return this.access_mode;
/*     */   }
/*     */   
/*     */   public long getDownloaded()
/*     */   {
/* 161 */     return this.downloaded;
/*     */   }
/*     */   
/*     */   public long getLength()
/*     */   {
/* 166 */     return this.length;
/*     */   }
/*     */   
/*     */   public File getFile()
/*     */   {
/* 171 */     return this.file;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public File getFile(boolean follow_link)
/*     */   {
/* 178 */     if (follow_link)
/*     */     {
/* 180 */       notSupported();
/*     */     }
/*     */     
/* 183 */     return this.file;
/*     */   }
/*     */   
/*     */   public int getFirstPieceNumber()
/*     */   {
/* 188 */     return this.first_piece_number;
/*     */   }
/*     */   
/*     */   public long getPieceSize()
/*     */   {
/* 193 */     notSupported();
/*     */     
/* 195 */     return -1L;
/*     */   }
/*     */   
/*     */   public int getNumPieces()
/*     */   {
/* 200 */     return this.num_pieces;
/*     */   }
/*     */   
/*     */   public boolean isPriority()
/*     */   {
/* 205 */     return this.is_priority;
/*     */   }
/*     */   
/*     */   public boolean isSkipped()
/*     */   {
/* 210 */     return this.is_skipped;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIndex()
/*     */   {
/* 216 */     notSupported();
/*     */     
/* 218 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLink(File link_destination)
/*     */   {
/* 225 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public File getLink()
/*     */   {
/* 231 */     notSupported();
/*     */     
/* 233 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getDownloadHash()
/*     */   {
/* 239 */     notSupported();
/*     */     
/* 241 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public Download getDownload()
/*     */     throws DownloadException
/*     */   {
/* 248 */     notSupported();
/*     */     
/* 250 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public DiskManagerChannel createChannel()
/*     */   {
/* 256 */     notSupported();
/*     */     
/* 258 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DiskManagerRandomReadRequest createRandomReadRequest(long file_offset, long length, boolean reverse_order, DiskManagerListener listener)
/*     */     throws DownloadException
/*     */   {
/* 270 */     notSupported();
/*     */     
/* 272 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/disk/RPDiskManagerFileInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */