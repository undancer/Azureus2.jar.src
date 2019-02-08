/*     */ package org.gudy.azureus2.core3.disk.impl.piecemapper.impl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerFileInfoImpl;
/*     */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceList;
/*     */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMap;
/*     */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMapper;
/*     */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMapperFile;
/*     */ import org.gudy.azureus2.core3.internat.LocaleUtilDecoder;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.StringInterner;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PieceMapperImpl
/*     */   implements DMPieceMapper
/*     */ {
/*     */   private final TOTorrent torrent;
/*     */   private final int last_piece_length;
/*  51 */   protected final ArrayList<fileInfo> btFileList = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PieceMapperImpl(TOTorrent _torrent)
/*     */   {
/*  58 */     this.torrent = _torrent;
/*     */     
/*  60 */     int piece_length = (int)this.torrent.getPieceLength();
/*     */     
/*  62 */     int piece_count = this.torrent.getNumberOfPieces();
/*     */     
/*  64 */     long total_length = this.torrent.getSize();
/*     */     
/*  66 */     this.last_piece_length = ((int)(total_length - (piece_count - 1) * piece_length));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void construct(LocaleUtilDecoder _locale_decoder, String _save_name)
/*     */     throws UnsupportedEncodingException
/*     */   {
/*  78 */     TOTorrentFile[] torrent_files = this.torrent.getFiles();
/*     */     
/*  80 */     if (this.torrent.isSimpleTorrent())
/*     */     {
/*  82 */       buildFileLookupTables(torrent_files[0], _save_name);
/*     */     }
/*     */     else
/*     */     {
/*  86 */       buildFileLookupTables(torrent_files, _locale_decoder);
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
/*     */   private void buildFileLookupTables(TOTorrentFile torrent_file, String fileName)
/*     */   {
/* 100 */     this.btFileList.add(new fileInfo(torrent_file, "", fileName));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void buildFileLookupTables(TOTorrentFile[] torrent_files, LocaleUtilDecoder locale_decoder)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 110 */     char separator = File.separatorChar;
/*     */     
/*     */ 
/*     */ 
/* 114 */     for (int i = 0; i < torrent_files.length; i++)
/*     */     {
/* 116 */       buildFileLookupTable(torrent_files[i], locale_decoder, separator);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void buildFileLookupTable(TOTorrentFile torrent_file, LocaleUtilDecoder locale_decoder, char separator)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 141 */     byte[][] path_components = torrent_file.getPathComponents();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 147 */     StringBuilder pathBuffer = new StringBuilder(0);
/*     */     
/* 149 */     int lastIndex = path_components.length - 1;
/* 150 */     for (int j = 0; j < lastIndex; j++)
/*     */     {
/*     */ 
/* 153 */       String comp = locale_decoder.decodeString(path_components[j]);
/*     */       
/* 155 */       comp = FileUtil.convertOSSpecificChars(comp, true);
/*     */       
/* 157 */       pathBuffer.append(comp);
/* 158 */       pathBuffer.append(separator);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 164 */     String last_comp = locale_decoder.decodeString(path_components[lastIndex]);
/*     */     
/* 166 */     last_comp = FileUtil.convertOSSpecificChars(last_comp, false);
/*     */     
/* 168 */     this.btFileList.add(new fileInfo(torrent_file, pathBuffer.toString(), last_comp));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DMPieceMap getPieceMap()
/*     */   {
/* 180 */     if (this.btFileList.size() == 1)
/*     */     {
/*     */ 
/*     */ 
/* 184 */       return new DMPieceMapSimple(this.torrent, ((fileInfo)this.btFileList.get(0)).getFileInfo());
/*     */     }
/*     */     
/* 187 */     int piece_length = (int)this.torrent.getPieceLength();
/*     */     
/* 189 */     int piece_count = this.torrent.getNumberOfPieces();
/*     */     
/* 191 */     long total_length = this.torrent.getSize();
/*     */     
/* 193 */     DMPieceList[] pieceMap = new DMPieceList[piece_count];
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 205 */     int modified_piece_length = piece_length;
/*     */     
/* 207 */     if (total_length < modified_piece_length)
/*     */     {
/* 209 */       modified_piece_length = (int)total_length;
/*     */     }
/*     */     
/* 212 */     long fileOffset = 0L;
/* 213 */     int currentFile = 0;
/* 214 */     for (int i = 0; ((1 == piece_count) && (i < piece_count)) || (i < piece_count - 1); i++) {
/* 215 */       ArrayList<PieceMapEntryImpl> pieceToFileList = new ArrayList();
/* 216 */       int usedSpace = 0;
/* 217 */       while (modified_piece_length > usedSpace) {
/* 218 */         fileInfo tempFile = (fileInfo)this.btFileList.get(currentFile);
/* 219 */         long length = tempFile.getLength();
/*     */         
/*     */ 
/* 222 */         long availableSpace = length - fileOffset;
/*     */         
/* 224 */         PieceMapEntryImpl tempPieceEntry = null;
/*     */         
/*     */ 
/* 227 */         if (availableSpace <= modified_piece_length - usedSpace)
/*     */         {
/* 229 */           tempPieceEntry = new PieceMapEntryImpl(tempFile.getFileInfo(), fileOffset, (int)availableSpace);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 234 */           usedSpace = (int)(usedSpace + availableSpace);
/*     */           
/* 236 */           fileOffset = 0L;
/*     */           
/* 238 */           currentFile++;
/*     */         }
/*     */         else {
/* 241 */           tempPieceEntry = new PieceMapEntryImpl(tempFile.getFileInfo(), fileOffset, modified_piece_length - usedSpace);
/*     */           
/*     */ 
/* 244 */           fileOffset += modified_piece_length - usedSpace;
/*     */           
/* 246 */           usedSpace += modified_piece_length - usedSpace;
/*     */         }
/*     */         
/*     */ 
/* 250 */         pieceToFileList.add(tempPieceEntry);
/*     */       }
/*     */       
/*     */ 
/* 254 */       pieceMap[i] = PieceListImpl.convert(pieceToFileList);
/*     */     }
/*     */     
/*     */ 
/* 258 */     if (piece_count > 1) {
/* 259 */       pieceMap[(piece_count - 1)] = PieceListImpl.convert(buildLastPieceToFileList(this.btFileList, currentFile, fileOffset));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 268 */     return new DMPieceMapImpl(pieceMap);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private List<PieceMapEntryImpl> buildLastPieceToFileList(List<fileInfo> file_list, int current_file, long file_offset)
/*     */   {
/* 278 */     ArrayList<PieceMapEntryImpl> piece_to_file_list = new ArrayList();
/*     */     
/* 280 */     for (int i = current_file; i < file_list.size(); i++)
/*     */     {
/* 282 */       fileInfo file = (fileInfo)file_list.get(i);
/*     */       
/* 284 */       long space_in_file = file.getLength() - file_offset;
/*     */       
/* 286 */       PieceMapEntryImpl piece_entry = new PieceMapEntryImpl(file.getFileInfo(), file_offset, (int)space_in_file);
/*     */       
/* 288 */       piece_to_file_list.add(piece_entry);
/*     */       
/* 290 */       file_offset = 0L;
/*     */     }
/*     */     
/* 293 */     return piece_to_file_list;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalLength()
/*     */   {
/* 299 */     return this.torrent.getSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPieceLength()
/*     */   {
/* 305 */     return (int)this.torrent.getPieceLength();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLastPieceLength()
/*     */   {
/* 311 */     return this.last_piece_length;
/*     */   }
/*     */   
/*     */ 
/*     */   public DMPieceMapperFile[] getFiles()
/*     */   {
/* 317 */     DMPieceMapperFile[] res = new DMPieceMapperFile[this.btFileList.size()];
/*     */     
/* 319 */     this.btFileList.toArray(res);
/*     */     
/* 321 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   protected static class fileInfo
/*     */     implements DMPieceMapperFile
/*     */   {
/*     */     private DiskManagerFileInfoImpl file;
/*     */     
/*     */     private final TOTorrentFile torrent_file;
/*     */     
/*     */     private final String path;
/*     */     
/*     */     private final String name;
/*     */     
/*     */ 
/*     */     public fileInfo(TOTorrentFile _torrent_file, String _path, String _name)
/*     */     {
/* 339 */       this.torrent_file = _torrent_file;
/* 340 */       this.path = StringInterner.intern(_path);
/* 341 */       this.name = _name;
/*     */     }
/*     */     
/*     */     public long getLength() {
/* 345 */       return this.torrent_file.getLength();
/*     */     }
/*     */     
/*     */     public File getDataFile()
/*     */     {
/* 350 */       return new File(this.path, this.name);
/*     */     }
/*     */     
/*     */     public TOTorrentFile getTorrentFile()
/*     */     {
/* 355 */       return this.torrent_file;
/*     */     }
/*     */     
/* 358 */     public DiskManagerFileInfoImpl getFileInfo() { return this.file; }
/*     */     
/*     */     public void setFileInfo(DiskManagerFileInfoImpl _file) {
/* 361 */       this.file = _file;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/piecemapper/impl/PieceMapperImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */