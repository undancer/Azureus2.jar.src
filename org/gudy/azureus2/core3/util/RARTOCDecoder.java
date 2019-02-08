/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
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
/*     */ public class RARTOCDecoder
/*     */ {
/*     */   private final DataProvider provider;
/*     */   
/*     */   public RARTOCDecoder(DataProvider _provider)
/*     */   {
/*  35 */     this.provider = _provider;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void analyse(TOCResultHandler result_handler)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  45 */       analyseSupport(result_handler);
/*     */       
/*  47 */       result_handler.complete();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */       IOException ioe;
/*     */       IOException ioe;
/*  53 */       if ((e instanceof IOException))
/*     */       {
/*  55 */         ioe = (IOException)e;
/*     */       }
/*     */       else
/*     */       {
/*  59 */         ioe = new IOException("Analysis failed: " + Debug.getNestedExceptionMessage(e));
/*     */       }
/*     */       
/*  62 */       result_handler.failed(ioe);
/*     */       
/*  64 */       throw ioe;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void analyseSupport(TOCResultHandler result_handler)
/*     */     throws IOException
/*     */   {
/*  76 */     byte[] header_buffer = new byte[7];
/*     */     
/*  78 */     readFully(header_buffer);
/*     */     
/*  80 */     if (!new String(header_buffer).startsWith("Rar!"))
/*     */     {
/*  82 */       throw new IOException("Not a rar file");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  87 */     readFully(header_buffer);
/*     */     
/*  89 */     int archive_header_size = getShort(header_buffer, 5);
/*     */     
/*  91 */     if (archive_header_size > 1024)
/*     */     {
/*  93 */       throw new IOException("Invalid archive header");
/*     */     }
/*     */     
/*  96 */     this.provider.skip(archive_header_size - 7);
/*     */     
/*     */ 
/*     */ 
/*     */     for (;;)
/*     */     {
/* 102 */       int read = this.provider.read(header_buffer);
/*     */       
/* 104 */       if (read < 7) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 111 */       int block_type = header_buffer[2] & 0xFF;
/*     */       
/* 113 */       int entry_flags = getShort(header_buffer, 3);
/* 114 */       int header_size = getShort(header_buffer, 5);
/*     */       
/*     */ 
/*     */ 
/* 118 */       if ((block_type < 112) || (block_type > 144))
/*     */       {
/* 120 */         throw new IOException("invalid header, archive corrupted");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 125 */       if (block_type == 116)
/*     */       {
/* 127 */         boolean password = (entry_flags & 0x4) != 0;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 136 */         byte[] buffer = new byte[25];
/*     */         
/* 138 */         readFully(buffer);
/*     */         
/* 140 */         long comp_size = getInteger(buffer, 0);
/* 141 */         long act_size = getInteger(buffer, 4);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 150 */         int extended_length = 0;
/*     */         
/* 152 */         if ((entry_flags & 0x100) != 0)
/*     */         {
/*     */ 
/*     */ 
/* 156 */           extended_length = 8;
/*     */           
/* 158 */           byte[] extended_size_info = new byte[8];
/*     */           
/* 160 */           readFully(extended_size_info);
/*     */           
/* 162 */           comp_size |= getInteger(extended_size_info, 0) << 32;
/* 163 */           act_size |= getInteger(extended_size_info, 4) << 32;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 168 */         int name_length = getShort(buffer, 19);
/*     */         
/* 170 */         if (name_length > 32768)
/*     */         {
/*     */ 
/* 173 */           throw new IOException("name length too large: " + name_length);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 178 */         byte[] name = new byte[name_length];
/*     */         
/* 180 */         readFully(name);
/*     */         
/*     */         String decoded_name;
/*     */         
/* 184 */         if ((entry_flags & 0x200) != 0)
/*     */         {
/* 186 */           int zero_pos = -1;
/*     */           
/* 188 */           for (int i = 0; i < name.length; i++)
/*     */           {
/* 190 */             if (name[i] == 0)
/*     */             {
/* 192 */               zero_pos = i;
/*     */               
/* 194 */               break;
/*     */             }
/*     */           }
/*     */           String decoded_name;
/* 198 */           if (zero_pos == -1)
/*     */           {
/* 200 */             decoded_name = new String(name, "UTF-8");
/*     */           }
/*     */           else
/*     */           {
/* 204 */             String decoded_name = decodeName(name, zero_pos + 1);
/*     */             
/* 206 */             if (decoded_name == null)
/*     */             {
/* 208 */               decoded_name = new String(name, 0, zero_pos, "UTF-8");
/*     */             }
/*     */           }
/*     */         }
/*     */         else {
/* 213 */           decoded_name = new String(name, "UTF-8");
/*     */         }
/*     */         
/* 216 */         if ((entry_flags & 0xE0) != 224)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 222 */           result_handler.entryRead(decoded_name, act_size, password);
/*     */         }
/*     */         
/* 225 */         this.provider.skip(header_size - (32 + extended_length + name_length) + comp_size);
/*     */       } else {
/* 227 */         if (block_type == 123) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 235 */         this.provider.skip(archive_header_size - 7);
/*     */         
/* 237 */         if ((entry_flags & 0x8000) != 0)
/*     */         {
/* 239 */           this.provider.skip(4L);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String decodeName(byte[] b_data, int pos)
/*     */   {
/*     */     try
/*     */     {
/* 251 */       int Flags = 0;
/* 252 */       int FlagBits = 0;
/*     */       
/* 254 */       byte[] Name = b_data;
/* 255 */       byte[] EncName = b_data;
/* 256 */       int EncSize = b_data.length;
/* 257 */       int MaxDecSize = 4096;
/*     */       
/* 259 */       int[] NameW = new int[MaxDecSize];
/*     */       
/* 261 */       int EncPos = pos;
/* 262 */       int DecPos = 0;
/*     */       
/* 264 */       byte HighByte = EncName[(EncPos++)];
/*     */       
/* 266 */       while ((EncPos < EncSize) && (DecPos < MaxDecSize))
/*     */       {
/* 268 */         if (FlagBits == 0)
/*     */         {
/* 270 */           Flags = EncName[(EncPos++)];
/* 271 */           FlagBits = 8;
/*     */         }
/*     */         
/* 274 */         switch (Flags >> 6 & 0x3)
/*     */         {
/*     */         case 0: 
/* 277 */           EncName[(EncPos++)] &= 0xFF;
/*     */           
/* 279 */           break;
/*     */         
/*     */         case 1: 
/* 282 */           NameW[(DecPos++)] = ((EncName[(EncPos++)] & 0xFF) + (HighByte << 8 & 0xFF00));
/*     */           
/* 284 */           break;
/*     */         
/*     */         case 2: 
/* 287 */           NameW[(DecPos++)] = ((EncName[(EncPos++)] & 0xFF) + (EncName[(EncPos++)] << 8 & 0xFF00));
/*     */           
/* 289 */           break;
/*     */         
/*     */         case 3: 
/* 292 */           int Length = EncName[(EncPos++)] & 0xFF;
/*     */           
/* 294 */           if ((Length & 0x80) != 0)
/*     */           {
/* 296 */             byte Correction = EncName[(EncPos++)];
/*     */             
/* 298 */             for (Length = (Length & 0x7F) + 2; (Length > 0) && (DecPos < MaxDecSize); DecPos++)
/*     */             {
/* 300 */               NameW[DecPos] = ((Name[DecPos] + Correction & 0xFF) + (HighByte << 8 & 0xFF00));Length--;
/*     */             }
/*     */           } else {
/* 303 */             for (Length += 2; (Length > 0) && (DecPos < MaxDecSize); DecPos++)
/*     */             {
/* 305 */               Name[DecPos] &= 0xFF;Length--;
/*     */             }
/*     */           }
/*     */           
/*     */           break;
/*     */         }
/*     */         
/*     */         
/* 313 */         Flags <<= 2;
/* 314 */         FlagBits -= 2;
/*     */       }
/*     */       
/* 317 */       byte[] temp = new byte[DecPos * 2];
/*     */       
/* 319 */       for (int i = 0; i < DecPos; i++)
/*     */       {
/* 321 */         temp[(i * 2)] = ((byte)(NameW[i] >> 8 & 0xFF));
/* 322 */         temp[(i * 2 + 1)] = ((byte)(NameW[i] & 0xFF));
/*     */       }
/*     */       
/* 325 */       return new String(temp, "UTF-16BE");
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 329 */       Debug.outNoStack("Failed to decode name: " + ByteFormatter.encodeString(b_data) + " - " + Debug.getNestedExceptionMessage(e));
/*     */     }
/* 331 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void readFully(byte[] buffer)
/*     */     throws IOException
/*     */   {
/* 340 */     if (this.provider.read(buffer) != buffer.length)
/*     */     {
/* 342 */       throw new IOException("unexpected end-of-file");
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
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try
/*     */     {
/* 386 */       FileInputStream fis = new FileInputStream("C:\\temp\\mp.part6.rar");
/*     */       
/* 388 */       RARTOCDecoder decoder = new RARTOCDecoder(new DataProvider()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public int read(byte[] buffer)
/*     */           throws IOException
/*     */         {
/*     */ 
/*     */ 
/* 398 */           return this.val$fis.read(buffer);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void skip(long bytes)
/*     */           throws IOException
/*     */         {
/* 407 */           this.val$fis.skip(bytes);
/*     */         }
/*     */         
/* 410 */       });
/* 411 */       decoder.analyse(new TOCResultHandler()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public void entryRead(String name, long size, boolean password)
/*     */         {
/*     */ 
/*     */ 
/* 420 */           System.out.println(name + ": " + size + (password ? " protected" : ""));
/*     */         }
/*     */         
/*     */ 
/*     */         public void complete()
/*     */         {
/* 426 */           System.out.println("complete");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void failed(IOException error)
/*     */         {
/* 433 */           System.out.println("failed: " + Debug.getNestedExceptionMessage(error));
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 439 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int getShort(byte[] buffer, int pos)
/*     */   {
/* 448 */     return buffer[(pos + 1)] << 8 & 0xFF00 | buffer[pos] & 0xFF;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static long getInteger(byte[] buffer, int pos)
/*     */   {
/* 456 */     return (buffer[(pos + 3)] << 24 & 0xFF000000 | buffer[(pos + 2)] << 16 & 0xFF0000 | buffer[(pos + 1)] << 8 & 0xFF00 | buffer[pos] & 0xFF) & 0xFFFFFFFF;
/*     */   }
/*     */   
/*     */   public static abstract interface DataProvider
/*     */   {
/*     */     public abstract int read(byte[] paramArrayOfByte)
/*     */       throws IOException;
/*     */     
/*     */     public abstract void skip(long paramLong)
/*     */       throws IOException;
/*     */   }
/*     */   
/*     */   public static abstract interface TOCResultHandler
/*     */   {
/*     */     public abstract void entryRead(String paramString, long paramLong, boolean paramBoolean)
/*     */       throws IOException;
/*     */     
/*     */     public abstract void complete();
/*     */     
/*     */     public abstract void failed(IOException paramIOException);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/RARTOCDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */