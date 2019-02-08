/*     */ package org.pf.file;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.StringWriter;
/*     */ import org.pf.text.StringUtil;
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
/*     */ public class FileUtil
/*     */ {
/*  35 */   public static final String LINE_SEPARATOR = System.getProperty("line.separator");
/*     */   
/*     */ 
/*     */   protected static final int DEFAULT_BUFFER_SIZE = 1024;
/*     */   
/*     */ 
/*  41 */   private static FileUtil current = new FileUtil();
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
/*     */   public static FileUtil current()
/*     */   {
/*  63 */     return current;
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
/*     */   public void copyStream(InputStream inStream, OutputStream outStream)
/*     */     throws IOException
/*     */   {
/*  78 */     copyStream(inStream, outStream, 1024);
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
/*     */   public void copyStream(InputStream inStream, OutputStream outStream, int bufSize)
/*     */     throws IOException
/*     */   {
/*  92 */     byte[] buffer = new byte[bufSize];
/*     */     
/*     */ 
/*     */     try
/*     */     {
/*  97 */       int count = inStream.read(buffer);
/*  98 */       while (count > -1)
/*     */       {
/* 100 */         outStream.write(buffer, 0, count);
/* 101 */         count = inStream.read(buffer);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 106 */       close(inStream);
/* 107 */       close(outStream);
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
/*     */   public String readTextFrom(InputStream inStream)
/*     */     throws IOException
/*     */   {
/* 127 */     StringWriter writer = new StringWriter(1024);
/* 128 */     copyText(inStream, writer);
/* 129 */     return writer.toString();
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
/*     */   public String readTextFrom(String filename)
/*     */     throws IOException
/*     */   {
/* 145 */     FileInputStream inStream = new FileInputStream(filename);
/* 146 */     return readTextFrom(inStream);
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
/*     */   public String readTextFrom(File file)
/*     */     throws IOException
/*     */   {
/* 160 */     FileInputStream inStream = new FileInputStream(file);
/* 161 */     return readTextFrom(inStream);
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
/*     */   public void copyText(Reader reader, final StringWriter writer)
/*     */     throws IOException
/*     */   {
/* 180 */     BufferedReader bufReader = new BufferedReader(reader);
/*     */     try
/*     */     {
/* 183 */       LineProcessor processor = new LineProcessor()
/*     */       {
/*     */         public boolean processLine(String line, int lineNo)
/*     */         {
/* 187 */           if (lineNo > 1) {
/* 188 */             writer.write(FileUtil.LINE_SEPARATOR);
/*     */           }
/* 190 */           writer.write(line);
/* 191 */           return true;
/*     */         }
/* 193 */       };
/* 194 */       processTextLines(bufReader, processor);
/*     */     }
/*     */     finally
/*     */     {
/* 198 */       bufReader.close();
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
/*     */   public void processTextLines(String filename, LineProcessor processor)
/*     */     throws IOException
/*     */   {
/* 218 */     if (filename == null) {
/* 219 */       throw new IllegalArgumentException("filename must not be null");
/*     */     }
/* 221 */     FileInputStream inStream = new FileInputStream(filename);
/*     */     try {
/* 223 */       processTextLines(inStream, processor);
/*     */     } finally {
/* 225 */       inStream.close();
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
/*     */   public void processTextLines(InputStream inStream, LineProcessor processor)
/*     */     throws IOException
/*     */   {
/* 247 */     if (inStream == null) {
/* 248 */       throw new IllegalArgumentException("inStream must not be null");
/*     */     }
/* 250 */     InputStreamReader reader = new InputStreamReader(inStream);
/* 251 */     processTextLines(reader, processor);
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
/*     */   public void processTextLines(Reader reader, LineProcessor processor)
/*     */     throws IOException
/*     */   {
/* 270 */     int counter = 0;
/* 271 */     boolean continue_reading = true;
/*     */     
/* 273 */     if (reader == null) {
/* 274 */       throw new IllegalArgumentException("reader must not be null");
/*     */     }
/* 276 */     if (processor == null) {
/* 277 */       throw new IllegalArgumentException("processor must not be null");
/*     */     }
/* 279 */     BufferedReader bufReader = new BufferedReader(reader);
/* 280 */     while ((continue_reading) && (bufReader.ready()))
/*     */     {
/* 282 */       String line = bufReader.readLine();
/* 283 */       if (line == null) {
/*     */         break;
/*     */       }
/* 286 */       counter++;
/* 287 */       continue_reading = processor.processLine(line, counter);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean close(InputStream stream)
/*     */   {
/* 299 */     if (stream == null)
/*     */     {
/* 301 */       return false;
/*     */     }
/*     */     try
/*     */     {
/* 305 */       stream.close();
/* 306 */       return true;
/*     */     }
/*     */     catch (IOException e) {}
/*     */     
/* 310 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean close(OutputStream stream)
/*     */   {
/* 322 */     if (stream == null)
/*     */     {
/* 324 */       return false;
/*     */     }
/*     */     try
/*     */     {
/* 328 */       stream.close();
/* 329 */       return true;
/*     */     }
/*     */     catch (IOException e) {}
/*     */     
/* 333 */     return false;
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
/*     */   public String standardize(String filename)
/*     */   {
/* 349 */     if (filename == null) {
/* 350 */       return null;
/*     */     }
/* 352 */     return standardizeFilename(filename);
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
/*     */   public String javaFilename(String filename)
/*     */   {
/* 366 */     if (filename == null) {
/* 367 */       return null;
/*     */     }
/* 369 */     return filename.replace('\\', '/');
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void copyText(InputStream inStream, StringWriter writer)
/*     */     throws IOException
/*     */   {
/* 380 */     copyText(new InputStreamReader(inStream), writer);
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
/*     */   protected String standardizeFilename(String filename)
/*     */   {
/* 393 */     filename = javaFilename(filename);
/* 394 */     boolean startedFromRoot = filename.startsWith("/");
/* 395 */     String[] nameElements = str().parts(filename, "/");
/* 396 */     if (nameElements.length > 0)
/*     */     {
/* 398 */       boolean hasDriveLetter = nameElements[0].endsWith(":");
/* 399 */       if (hasDriveLetter)
/*     */       {
/* 401 */         nameElements[0] = nameElements[0].toUpperCase();
/*     */ 
/*     */ 
/*     */       }
/* 405 */       else if (startedFromRoot)
/*     */       {
/* 407 */         nameElements = str().append(new String[] { "" }, nameElements);
/*     */       }
/*     */       
/* 410 */       boolean isAbsolute = (hasDriveLetter) || (startedFromRoot);
/* 411 */       for (int i = 0; i < nameElements.length; i++)
/*     */       {
/* 413 */         if (".".equals(nameElements[i]))
/*     */         {
/* 415 */           nameElements[i] = null;
/*     */ 
/*     */ 
/*     */         }
/* 419 */         else if ("..".equals(nameElements[i]))
/*     */         {
/* 421 */           int index = indexOfPreceedingNotNullElement(nameElements, i - 1);
/* 422 */           if (index >= 0)
/*     */           {
/* 424 */             if ((index > 0) || (!isAbsolute))
/*     */             {
/* 426 */               nameElements[i] = null;
/* 427 */               nameElements[index] = null;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 433 */       nameElements = str().removeNull(nameElements);
/* 434 */       return str().asString(nameElements, "/");
/*     */     }
/*     */     
/*     */ 
/* 438 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int indexOfPreceedingNotNullElement(String[] elements, int start)
/*     */   {
/* 446 */     for (int i = start; i >= 0; i--)
/*     */     {
/* 448 */       if (elements[i] != null)
/*     */       {
/* 450 */         if ("..".equals(elements[i]))
/*     */         {
/* 452 */           return -1;
/*     */         }
/*     */         
/*     */ 
/* 456 */         return i;
/*     */       }
/*     */     }
/*     */     
/* 460 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected StringUtil str()
/*     */   {
/* 467 */     return StringUtil.current();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/pf/file/FileUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */