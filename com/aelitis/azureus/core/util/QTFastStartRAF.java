/*     */ package com.aelitis.azureus.core.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
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
/*     */ public class QTFastStartRAF
/*     */ {
/*  37 */   private static final Set<String> supported_extensions = new HashSet();
/*     */   
/*     */   static {
/*  40 */     supported_extensions.add("mov");
/*  41 */     supported_extensions.add("qt");
/*  42 */     supported_extensions.add("mp4");
/*     */   }
/*     */   
/*  45 */   private static final Set<String> tested = new HashSet();
/*     */   private static final String ATOM_FREE = "free";
/*     */   private static final String ATOM_JUNK = "junk";
/*     */   private static final String ATOM_MDAT = "mdat";
/*     */   
/*     */   public static boolean isSupportedExtension(String extension) {
/*  51 */     return supported_extensions.contains(extension.toLowerCase());
/*     */   }
/*     */   
/*     */ 
/*     */   private static final String ATOM_MOOV = "moov";
/*     */   
/*     */   private static final String ATOM_PNOT = "pnot";
/*     */   
/*     */   private static final String ATOM_SKIP = "skip";
/*     */   
/*     */   private static final String ATOM_WIDE = "wide";
/*     */   
/*     */   private static final String ATOM_PICT = "PICT";
/*     */   private static final String ATOM_FTYP = "ftyp";
/*     */   private static final String ATOM_CMOV = "cmov";
/*     */   private static final String ATOM_STCO = "stco";
/*     */   private static final String ATOM_CO64 = "co64";
/*  68 */   private static final String[] VALID_TOPLEVEL_ATOMS = { "free", "junk", "mdat", "moov", "pnot", "skip", "wide", "PICT", "ftyp" };
/*     */   
/*     */ 
/*     */   private final FileAccessor input;
/*     */   
/*     */ 
/*     */   private boolean transparent;
/*     */   
/*     */ 
/*     */   private byte[] header;
/*     */   
/*     */ 
/*     */   private long body_start;
/*     */   
/*     */   private long body_end;
/*     */   
/*     */   private long seek_position;
/*     */   
/*     */ 
/*     */   public QTFastStartRAF(File file, boolean enable)
/*     */     throws IOException
/*     */   {
/*  90 */     this(new RAFAccessor(file, null), enable);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public QTFastStartRAF(FileAccessor accessor, boolean enable)
/*     */     throws IOException
/*     */   {
/* 100 */     this.input = accessor;
/*     */     
/* 102 */     if (enable)
/*     */     {
/* 104 */       String name = accessor.getName();
/*     */       
/*     */ 
/* 107 */       String fail = null;
/*     */       boolean log;
/* 109 */       synchronized (tested)
/*     */       {
/* 111 */         log = !tested.contains(name);
/*     */         
/* 113 */         if (log)
/*     */         {
/* 115 */           tested.add(name);
/*     */         }
/*     */       }
/*     */       try
/*     */       {
/* 120 */         Atom ah = null;
/* 121 */         Atom ftypAtom = null;
/*     */         
/* 123 */         boolean gotFtyp = false;
/* 124 */         boolean gotMdat = false;
/* 125 */         boolean justCopy = false;
/*     */         
/* 127 */         while (this.input.getFilePointer() < this.input.length())
/*     */         {
/* 129 */           ah = new Atom(this.input);
/*     */           
/*     */ 
/*     */ 
/* 133 */           if (!isValidTopLevelAtom(ah)) {
/* 134 */             throw new IOException("Non top level QT atom found (" + ah.type + "). File invalid?");
/*     */           }
/*     */           
/* 137 */           if ((gotFtyp) && (!gotMdat) && (ah.type.equalsIgnoreCase("moov"))) {
/* 138 */             justCopy = true;
/* 139 */             break;
/*     */           }
/*     */           
/*     */ 
/* 143 */           if (ah.type.equalsIgnoreCase("ftyp")) {
/* 144 */             ftypAtom = ah;
/* 145 */             ftypAtom.fillBuffer(this.input);
/* 146 */             gotFtyp = true;
/* 147 */           } else if (ah.type.equalsIgnoreCase("mdat")) {
/* 148 */             gotMdat = true;
/* 149 */             this.input.skipBytes((int)ah.size);
/*     */           } else {
/* 151 */             this.input.skipBytes((int)ah.size);
/*     */           }
/*     */         }
/*     */         
/* 155 */         if (justCopy)
/*     */         {
/* 157 */           this.transparent = true;
/*     */           String message;
/*     */           String message;
/*     */           return;
/*     */         }
/* 162 */         if (ftypAtom == null)
/*     */         {
/* 164 */           throw new IOException("No FTYP atom found");
/*     */         }
/*     */         
/* 167 */         if ((ah == null) || (!ah.type.equalsIgnoreCase("moov")))
/*     */         {
/* 169 */           throw new IOException("Last QT atom was not the MOOV atom.");
/*     */         }
/*     */         
/* 172 */         this.input.seek(ah.offset);
/*     */         
/* 174 */         Atom moovAtom = ah;
/*     */         
/* 176 */         moovAtom.fillBuffer(this.input);
/*     */         
/* 178 */         if (isCompressedMoovAtom(moovAtom))
/*     */         {
/* 180 */           throw new IOException("Compressed MOOV qt atoms are not supported");
/*     */         }
/*     */         
/* 183 */         patchMoovAtom(moovAtom);
/*     */         
/* 185 */         this.body_start = (ftypAtom.offset + ftypAtom.size);
/* 186 */         this.body_end = moovAtom.offset;
/*     */         
/* 188 */         this.header = new byte[ftypAtom.buffer.length + moovAtom.buffer.length];
/*     */         
/* 190 */         System.arraycopy(ftypAtom.buffer, 0, this.header, 0, ftypAtom.buffer.length);
/* 191 */         System.arraycopy(moovAtom.buffer, 0, this.header, ftypAtom.buffer.length, moovAtom.buffer.length);
/*     */         
/* 193 */         if (accessor.length() != this.header.length + (this.body_end - this.body_start))
/*     */         {
/* 195 */           throw new IOException("Inconsistent: file size has changed");
/*     */         }
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*     */         String message;
/*     */         String message;
/* 202 */         fail = Debug.getNestedExceptionMessage(e);
/*     */         
/* 204 */         this.transparent = true;
/*     */       } finally {
/*     */         String message;
/*     */         String message;
/* 208 */         this.input.seek(0L);
/*     */         
/* 210 */         if (log)
/*     */         {
/*     */           String message;
/*     */           String message;
/* 214 */           if (fail == null)
/*     */           {
/* 216 */             message = this.transparent ? "Not required" : "Required";
/*     */           }
/*     */           else
/*     */           {
/* 220 */             message = "Failed - " + fail;
/*     */           }
/*     */           
/* 223 */           Debug.outNoStack("MOOV relocation for " + accessor.getName() + ": " + message);
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 228 */       this.transparent = true;
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean isCompressedMoovAtom(Atom moovAtom)
/*     */   {
/* 234 */     byte[] cmovBuffer = copyOfRange(moovAtom.buffer, 12, 15);
/*     */     
/* 236 */     if (new String(cmovBuffer).equalsIgnoreCase("cmov")) {
/* 237 */       return true;
/*     */     }
/*     */     
/* 240 */     return false;
/*     */   }
/*     */   
/*     */   private boolean isValidTopLevelAtom(Atom ah)
/*     */   {
/* 245 */     for (String validAtom : VALID_TOPLEVEL_ATOMS) {
/* 246 */       if (validAtom.equalsIgnoreCase(ah.type)) {
/* 247 */         return true;
/*     */       }
/*     */     }
/* 250 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   private void patchMoovAtom(Atom moovAtom)
/*     */   {
/* 256 */     int idx = 0;
/* 257 */     for (idx = 4; idx < moovAtom.size - 4L; idx++) {
/* 258 */       byte[] buffer = copyOfRange(moovAtom.buffer, idx, idx + 4);
/* 259 */       if (new String(buffer).equalsIgnoreCase("stco")) {
/* 260 */         int stcoSize = patchStcoAtom(moovAtom, idx);
/* 261 */         idx += stcoSize - 4;
/* 262 */       } else if (new String(buffer).equalsIgnoreCase("co64")) {
/* 263 */         int co64Size = patchCo64Atom(moovAtom, idx);
/* 264 */         idx += co64Size - 4;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private int patchStcoAtom(Atom ah, int idx)
/*     */   {
/* 271 */     int stcoSize = (int)bytesToLong(copyOfRange(ah.buffer, idx - 4, idx));
/*     */     
/* 273 */     int offsetCount = (int)bytesToLong(copyOfRange(ah.buffer, idx + 8, idx + 12));
/* 274 */     for (int j = 0; j < offsetCount; j++) {
/* 275 */       int currentOffset = (int)bytesToLong(copyOfRange(ah.buffer, idx + 12 + j * 4, idx + 12 + j * 4 + 4));
/* 276 */       currentOffset = (int)(currentOffset + ah.size);
/* 277 */       int offsetIdx = idx + 12 + j * 4;
/* 278 */       ah.buffer[(offsetIdx + 0)] = ((byte)(currentOffset >> 24 & 0xFF));
/* 279 */       ah.buffer[(offsetIdx + 1)] = ((byte)(currentOffset >> 16 & 0xFF));
/* 280 */       ah.buffer[(offsetIdx + 2)] = ((byte)(currentOffset >> 8 & 0xFF));
/* 281 */       ah.buffer[(offsetIdx + 3)] = ((byte)(currentOffset >> 0 & 0xFF));
/*     */     }
/*     */     
/* 284 */     return stcoSize;
/*     */   }
/*     */   
/*     */   private int patchCo64Atom(Atom ah, int idx) {
/* 288 */     int co64Size = (int)bytesToLong(copyOfRange(ah.buffer, idx - 4, idx));
/*     */     
/* 290 */     int offsetCount = (int)bytesToLong(copyOfRange(ah.buffer, idx + 8, idx + 12));
/* 291 */     for (int j = 0; j < offsetCount; j++) {
/* 292 */       long currentOffset = bytesToLong(copyOfRange(ah.buffer, idx + 12 + j * 8, idx + 12 + j * 8 + 8));
/* 293 */       currentOffset += ah.size;
/* 294 */       int offsetIdx = idx + 12 + j * 8;
/* 295 */       ah.buffer[(offsetIdx + 0)] = ((byte)(int)(currentOffset >> 56 & 0xFF));
/* 296 */       ah.buffer[(offsetIdx + 1)] = ((byte)(int)(currentOffset >> 48 & 0xFF));
/* 297 */       ah.buffer[(offsetIdx + 2)] = ((byte)(int)(currentOffset >> 40 & 0xFF));
/* 298 */       ah.buffer[(offsetIdx + 3)] = ((byte)(int)(currentOffset >> 32 & 0xFF));
/* 299 */       ah.buffer[(offsetIdx + 4)] = ((byte)(int)(currentOffset >> 24 & 0xFF));
/* 300 */       ah.buffer[(offsetIdx + 5)] = ((byte)(int)(currentOffset >> 16 & 0xFF));
/* 301 */       ah.buffer[(offsetIdx + 6)] = ((byte)(int)(currentOffset >> 8 & 0xFF));
/* 302 */       ah.buffer[(offsetIdx + 7)] = ((byte)(int)(currentOffset >> 0 & 0xFF));
/*     */     }
/*     */     
/* 305 */     return co64Size;
/*     */   }
/*     */   
/*     */   public static byte[] copyOfRange(byte[] original, int from, int to) {
/* 309 */     int newLength = to - from;
/* 310 */     if (newLength < 0)
/* 311 */       throw new IllegalArgumentException(from + " > " + to);
/* 312 */     byte[] copy = new byte[newLength];
/* 313 */     System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
/*     */     
/* 315 */     return copy;
/*     */   }
/*     */   
/*     */   private long bytesToLong(byte[] buffer)
/*     */   {
/* 320 */     long retVal = 0L;
/*     */     
/* 322 */     for (int i = 0; i < buffer.length; i++) {
/* 323 */       retVal += ((buffer[i] & 0xFF) << 8 * (buffer.length - i - 1));
/*     */     }
/*     */     
/* 326 */     return retVal;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void seek(long pos)
/*     */     throws IOException
/*     */   {
/* 336 */     if (this.transparent)
/*     */     {
/* 338 */       this.input.seek(pos);
/*     */     }
/*     */     else
/*     */     {
/* 342 */       this.seek_position = pos;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int read(byte[] buffer, int pos, int len)
/*     */     throws IOException
/*     */   {
/* 354 */     if (this.transparent)
/*     */     {
/* 356 */       return this.input.read(buffer, pos, len);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 362 */     long start_pos = this.seek_position;
/* 363 */     int start_len = len;
/*     */     
/* 365 */     if (this.seek_position < this.header.length)
/*     */     {
/* 367 */       int rem = (int)(this.header.length - this.seek_position);
/*     */       
/* 369 */       if (rem > len)
/*     */       {
/* 371 */         rem = len;
/*     */       }
/*     */       
/* 374 */       System.arraycopy(this.header, (int)this.seek_position, buffer, pos, rem);
/*     */       
/* 376 */       pos += rem;
/* 377 */       len -= rem;
/*     */       
/* 379 */       this.seek_position += rem;
/*     */     }
/*     */     
/* 382 */     if (len > 0)
/*     */     {
/* 384 */       long file_position = this.body_start + this.seek_position - this.header.length;
/*     */       
/* 386 */       long rem = this.body_end - file_position;
/*     */       
/* 388 */       if (len < rem)
/*     */       {
/* 390 */         rem = len;
/*     */       }
/*     */       
/* 393 */       this.input.seek(file_position);
/*     */       
/* 395 */       int temp = this.input.read(buffer, pos, (int)rem);
/*     */       
/* 397 */       pos += temp;
/* 398 */       len -= temp;
/*     */       
/* 400 */       this.seek_position += temp;
/*     */     }
/*     */     
/* 403 */     int read = start_len - len;
/*     */     
/* 405 */     this.seek_position = (start_pos + read);
/*     */     
/* 407 */     return read;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long length()
/*     */     throws IOException
/*     */   {
/* 415 */     return this.input.length();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 423 */     this.input.close();
/*     */   }
/*     */   
/*     */ 
/*     */   private static class Atom
/*     */   {
/*     */     public long offset;
/*     */     
/*     */     public long size;
/*     */     public String type;
/* 433 */     public byte[] buffer = null;
/*     */     
/*     */     public Atom(QTFastStartRAF.FileAccessor input) throws IOException {
/* 436 */       this.offset = input.getFilePointer();
/*     */       
/* 438 */       this.size = input.readInt();
/*     */       
/* 440 */       byte[] atomTypeFCC = new byte[4];
/* 441 */       input.readFully(atomTypeFCC);
/* 442 */       this.type = new String(atomTypeFCC);
/* 443 */       if (this.size == 1L)
/*     */       {
/* 445 */         this.size = input.readLong();
/*     */       }
/*     */       
/* 448 */       input.seek(this.offset);
/*     */     }
/*     */     
/*     */     public void fillBuffer(QTFastStartRAF.FileAccessor input) throws IOException {
/* 452 */       this.buffer = new byte[(int)this.size];
/* 453 */       input.readFully(this.buffer); } }
/*     */   
/*     */   public static abstract interface FileAccessor { public abstract String getName();
/*     */     
/*     */     public abstract long getFilePointer() throws IOException;
/*     */     
/*     */     public abstract void seek(long paramLong) throws IOException;
/*     */     
/*     */     public abstract void skipBytes(int paramInt) throws IOException;
/*     */     
/*     */     public abstract long length() throws IOException;
/*     */     
/*     */     public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException;
/*     */     public abstract int readInt() throws IOException;
/*     */     public abstract long readLong() throws IOException;
/*     */     public abstract void readFully(byte[] paramArrayOfByte) throws IOException;
/*     */     public abstract void close() throws IOException; }
/* 470 */   private static class RAFAccessor implements QTFastStartRAF.FileAccessor { private RAFAccessor(File _file) throws IOException { this.file = _file;
/* 471 */       this.raf = new RandomAccessFile(this.file, "r");
/*     */     }
/*     */     
/*     */ 
/*     */     public String getName()
/*     */     {
/* 477 */       return this.file.getAbsolutePath();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public long getFilePointer()
/*     */       throws IOException
/*     */     {
/* 485 */       return this.raf.getFilePointer();
/*     */     }
/*     */     
/*     */ 
/*     */     private final File file;
/*     */     
/*     */     public void seek(long pos)
/*     */       throws IOException
/*     */     {
/* 494 */       this.raf.seek(pos);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void skipBytes(int num)
/*     */       throws IOException
/*     */     {
/* 503 */       this.raf.skipBytes(num);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public long length()
/*     */       throws IOException
/*     */     {
/* 511 */       return this.raf.length();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private RandomAccessFile raf;
/*     */     
/*     */ 
/*     */     public int read(byte[] buffer, int pos, int len)
/*     */       throws IOException
/*     */     {
/* 522 */       return this.raf.read(buffer, pos, len);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int readInt()
/*     */       throws IOException
/*     */     {
/* 530 */       return this.raf.readInt();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public long readLong()
/*     */       throws IOException
/*     */     {
/* 538 */       return this.raf.readLong();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void readFully(byte[] buffer)
/*     */       throws IOException
/*     */     {
/* 547 */       this.raf.readFully(buffer);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 555 */       this.raf.close();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/QTFastStartRAF.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */