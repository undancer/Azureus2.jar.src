/*     */ package org.gudy.azureus2.core3.torrent;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.torrent.impl.TOTorrentCreateImpl;
/*     */ import org.gudy.azureus2.core3.torrent.impl.TOTorrentCreatorImpl;
/*     */ import org.gudy.azureus2.core3.torrent.impl.TOTorrentDeserialiseImpl;
/*     */ import org.gudy.azureus2.core3.torrent.impl.TOTorrentXMLDeserialiser;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TOTorrentFactory
/*     */ {
/*     */   public static final long TO_DEFAULT_FIXED_PIECE_SIZE = 262144L;
/*     */   public static final long TO_DEFAULT_VARIABLE_PIECE_SIZE_MIN = 32768L;
/*     */   public static final long TO_DEFAULT_VARIABLE_PIECE_SIZE_MAX = 2097152L;
/*     */   public static final long TO_DEFAULT_VARIABLE_PIECE_NUM_LOWER = 1024L;
/*     */   public static final long TO_DEFAULT_VARIABLE_PIECE_NUM_UPPER = 2048L;
/*  45 */   public static final long[] STANDARD_PIECE_SIZES = { 32768L, 49152L, 65536L, 98304L, 131072L, 196608L, 262144L, 393216L, 524288L, 786432L, 1048576L, 1572864L, 2097152L, 3145728L, 4194304L, 8388608L, 16777216L, 33554432L };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TOTorrent deserialiseFromBEncodedFile(File file)
/*     */     throws TOTorrentException
/*     */   {
/*  61 */     return new TOTorrentDeserialiseImpl(file);
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
/*     */   public static TOTorrent deserialiseFromBEncodedInputStream(InputStream is)
/*     */     throws TOTorrentException
/*     */   {
/*  79 */     return new TOTorrentDeserialiseImpl(is);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TOTorrent deserialiseFromBEncodedByteArray(byte[] bytes)
/*     */     throws TOTorrentException
/*     */   {
/*  88 */     return new TOTorrentDeserialiseImpl(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TOTorrent deserialiseFromMap(Map data)
/*     */     throws TOTorrentException
/*     */   {
/*  97 */     return new TOTorrentDeserialiseImpl(data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TOTorrent deserialiseFromXMLFile(File file)
/*     */     throws TOTorrentException
/*     */   {
/* 106 */     return new TOTorrentXMLDeserialiser().deserialise(file);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TOTorrentCreator createFromFileOrDirWithFixedPieceLength(File file, URL announce_url)
/*     */     throws TOTorrentException
/*     */   {
/* 118 */     return createFromFileOrDirWithFixedPieceLength(file, announce_url, false, 262144L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TOTorrentCreator createFromFileOrDirWithFixedPieceLength(File file, URL announce_url, boolean add_hashes)
/*     */     throws TOTorrentException
/*     */   {
/* 129 */     return createFromFileOrDirWithFixedPieceLength(file, announce_url, add_hashes, 262144L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TOTorrentCreator createFromFileOrDirWithFixedPieceLength(File file, URL announce_url, long piece_length)
/*     */     throws TOTorrentException
/*     */   {
/* 140 */     return createFromFileOrDirWithFixedPieceLength(file, announce_url, false, piece_length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TOTorrentCreator createFromFileOrDirWithFixedPieceLength(File file, URL announce_url, boolean add_hashes, long piece_length)
/*     */     throws TOTorrentException
/*     */   {
/* 152 */     return new TOTorrentCreatorImpl(file, announce_url, add_hashes, piece_length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TOTorrentCreator createFromFileOrDirWithComputedPieceLength(File file, URL announce_url)
/*     */     throws TOTorrentException
/*     */   {
/* 164 */     return createFromFileOrDirWithComputedPieceLength(file, announce_url, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TOTorrentCreator createFromFileOrDirWithComputedPieceLength(File file, URL announce_url, boolean add_hashes)
/*     */     throws TOTorrentException
/*     */   {
/* 175 */     return createFromFileOrDirWithComputedPieceLength(file, announce_url, add_hashes, 32768L, 2097152L, 1024L, 2048L);
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
/*     */   public static TOTorrentCreator createFromFileOrDirWithComputedPieceLength(File file, URL announce_url, long piece_min_size, long piece_max_size, long piece_num_lower, long piece_num_upper)
/*     */     throws TOTorrentException
/*     */   {
/* 197 */     return createFromFileOrDirWithComputedPieceLength(file, announce_url, false, piece_min_size, piece_max_size, piece_num_lower, piece_num_upper);
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
/*     */   public static TOTorrentCreator createFromFileOrDirWithComputedPieceLength(File file, URL announce_url, boolean add_hashes, long piece_min_size, long piece_max_size, long piece_num_lower, long piece_num_upper)
/*     */     throws TOTorrentException
/*     */   {
/* 215 */     return new TOTorrentCreatorImpl(file, announce_url, add_hashes, piece_min_size, piece_max_size, piece_num_lower, piece_num_upper);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long getTorrentDataSizeFromFileOrDir(File file_or_dir_or_desc, boolean is_layout_descriptor)
/*     */     throws TOTorrentException
/*     */   {
/* 228 */     TOTorrentCreatorImpl creator = new TOTorrentCreatorImpl(file_or_dir_or_desc);
/*     */     
/* 230 */     creator.setFileIsLayoutDescriptor(is_layout_descriptor);
/*     */     
/* 232 */     return creator.getTorrentDataSizeFromFileOrDir();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static long getComputedPieceSize(long data_size)
/*     */   {
/* 239 */     return TOTorrentCreateImpl.getComputedPieceSize(data_size, 32768L, 2097152L, 1024L, 2048L);
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
/*     */   public static long getPieceCount(long total_size, long piece_size)
/*     */   {
/* 252 */     return TOTorrentCreateImpl.getPieceCount(total_size, piece_size);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/TOTorrentFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */