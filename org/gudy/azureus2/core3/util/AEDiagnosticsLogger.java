/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.LinkedList;
/*     */ import java.util.TimeZone;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AEDiagnosticsLogger
/*     */ {
/*     */   private static final int MAX_PENDING = 8192;
/*     */   private final String name;
/*     */   private int max_size;
/*     */   private final File debug_dir;
/*  48 */   private boolean timestamp_enable = true;
/*     */   
/*     */   private boolean force;
/*  51 */   private boolean first_file = true;
/*  52 */   private boolean first_write = true;
/*     */   
/*     */   private PrintWriter current_writer;
/*     */   
/*     */   private LinkedList<StringBuilder> pending;
/*     */   private int pending_size;
/*     */   private boolean direct_writes;
/*     */   private static final boolean close_pws = false;
/*     */   private static final String start_date;
/*     */   private static final long timezone_offset;
/*     */   
/*     */   static
/*     */   {
/*  65 */     long now = System.currentTimeMillis();
/*     */     
/*  67 */     start_date = new SimpleDateFormat().format(new Date(now));
/*     */     
/*  69 */     timezone_offset = TimeZone.getDefault().getOffset(now);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected AEDiagnosticsLogger(File _debug_dir, String _name, int _max_size, boolean _direct_writes)
/*     */   {
/*  79 */     this.debug_dir = _debug_dir;
/*  80 */     this.name = _name;
/*  81 */     this.max_size = _max_size;
/*  82 */     this.direct_writes = _direct_writes;
/*     */     try
/*     */     {
/*  85 */       File f1 = getLogFile();
/*     */       
/*  87 */       this.first_file = false;
/*     */       
/*  89 */       File f2 = getLogFile();
/*     */       
/*  91 */       this.first_file = true;
/*     */       
/*     */ 
/*     */ 
/*  95 */       if ((f1.exists()) && (f2.exists()))
/*     */       {
/*  97 */         if (f1.lastModified() < f2.lastModified())
/*     */         {
/*  99 */           this.first_file = false;
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable ignore) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setForced(boolean _force)
/*     */   {
/* 111 */     if (System.getProperty("skip.loggers.setforced", "0").equals("0")) {
/* 112 */       this.force = _force;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isForced()
/*     */   {
/* 119 */     return this.force;
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getName()
/*     */   {
/* 125 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMaxFileSize(int _max_size)
/*     */   {
/* 132 */     this.max_size = _max_size;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void enableTimeStamp(boolean enable)
/*     */   {
/* 139 */     this.timestamp_enable = enable;
/*     */   }
/*     */   
/*     */ 
/*     */   public void log(Throwable e)
/*     */   {
/*     */     try
/*     */     {
/* 147 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */       
/* 149 */       PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos));
/*     */       
/* 151 */       e.printStackTrace(pw);
/*     */       
/* 153 */       pw.close();
/*     */       
/* 155 */       log(baos.toString());
/*     */     }
/*     */     catch (Throwable ignore) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logAndOut(String str)
/*     */   {
/* 166 */     logAndOut(str, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logAndOut(String str, boolean stderr)
/*     */   {
/* 174 */     if (stderr)
/*     */     {
/* 176 */       System.err.println(str);
/*     */       
/*     */ 
/* 179 */       if (Logger.getOldStdErr() == null) {
/* 180 */         log(str);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 185 */       System.out.println(str);
/* 186 */       log(str);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logAndOut(Throwable e)
/*     */   {
/* 195 */     e.printStackTrace();
/*     */     
/* 197 */     log(e);
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
/*     */   public static String getTimestamp()
/*     */   {
/* 216 */     long time = SystemTime.getCurrentTime();
/*     */     
/* 218 */     time += timezone_offset;
/*     */     
/* 220 */     time /= 1000L;
/*     */     
/* 222 */     int secs = (int)time % 60;
/* 223 */     int mins = (int)(time / 60L) % 60;
/* 224 */     int hours = (int)(time / 3600L) % 24;
/*     */     
/* 226 */     char[] chars = new char[11];
/*     */     
/* 228 */     chars[0] = '[';
/* 229 */     format(hours, chars, 1);
/* 230 */     chars[3] = ':';
/* 231 */     format(mins, chars, 4);
/* 232 */     chars[6] = ':';
/* 233 */     format(secs, chars, 7);
/* 234 */     chars[9] = ']';
/* 235 */     chars[10] = ' ';
/*     */     
/* 237 */     return new String(chars);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final void format(int num, char[] chars, int pos)
/*     */   {
/* 246 */     if (num < 10) {
/* 247 */       chars[pos] = '0';
/* 248 */       chars[(pos + 1)] = ((char)(48 + num));
/*     */     } else {
/* 250 */       chars[pos] = ((char)(48 + num / 10));
/* 251 */       chars[(pos + 1)] = ((char)(48 + num % 10));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void log(String _str)
/*     */   {
/* 259 */     if (!AEDiagnostics.loggers_enabled)
/*     */     {
/* 261 */       if (!this.force)
/*     */       {
/* 263 */         return;
/*     */       }
/*     */     }
/*     */     
/* 267 */     StringBuilder str = new StringBuilder(_str.length() + 20);
/*     */     
/*     */     String timeStamp;
/*     */     String timeStamp;
/* 271 */     if (this.timestamp_enable)
/*     */     {
/* 273 */       timeStamp = getTimestamp();
/*     */     }
/*     */     else
/*     */     {
/* 277 */       timeStamp = null;
/*     */     }
/*     */     
/* 280 */     synchronized (this)
/*     */     {
/* 282 */       if (this.first_write)
/*     */       {
/* 284 */         this.first_write = false;
/*     */         
/* 286 */         Calendar now = GregorianCalendar.getInstance();
/*     */         
/* 288 */         str.append("\r\n[");
/* 289 */         str.append(start_date);
/* 290 */         str.append("] Log File Opened for ");
/* 291 */         str.append(Constants.APP_NAME);
/* 292 */         str.append(" ");
/* 293 */         str.append("5.7.6.0");
/* 294 */         str.append("\r\n");
/*     */       }
/*     */       
/* 297 */       if (timeStamp != null)
/*     */       {
/* 299 */         str.append(timeStamp);
/*     */       }
/*     */       
/* 302 */       str.append(_str);
/*     */       
/* 304 */       if (!this.direct_writes)
/*     */       {
/* 306 */         if (this.pending == null)
/*     */         {
/* 308 */           this.pending = new LinkedList();
/*     */         }
/*     */         
/* 311 */         this.pending.add(str);
/*     */         
/* 313 */         this.pending_size += str.length();
/*     */         
/* 315 */         if (this.pending_size > 8192)
/*     */         {
/* 317 */           writePending();
/*     */         }
/*     */         
/* 320 */         return;
/*     */       }
/*     */       
/* 323 */       write(str);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void write(StringBuilder str)
/*     */   {
/*     */     try
/*     */     {
/* 332 */       File log_file = getLogFile();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 339 */       if (log_file.length() >= this.max_size)
/*     */       {
/* 341 */         if (this.current_writer != null)
/*     */         {
/* 343 */           this.current_writer.close();
/*     */           
/* 345 */           this.current_writer = null;
/*     */         }
/*     */         
/* 348 */         this.first_file = (!this.first_file);
/*     */         
/* 350 */         log_file = getLogFile();
/*     */         
/*     */ 
/*     */ 
/* 354 */         log_file.delete();
/*     */       }
/*     */       
/* 357 */       if (this.current_writer == null)
/*     */       {
/* 359 */         this.current_writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(log_file, true), "UTF-8"));
/*     */       }
/*     */       
/* 362 */       this.current_writer.println(str);
/*     */       
/* 364 */       this.current_writer.flush();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 370 */       if (this.current_writer == null) {} return; } catch (Throwable e) {}finally { if (this.current_writer == null) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writePending()
/*     */   {
/* 382 */     synchronized (this)
/*     */     {
/* 384 */       if (this.pending == null)
/*     */       {
/* 386 */         return;
/*     */       }
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 392 */         File log_file = getLogFile();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 399 */         if (log_file.length() >= this.max_size)
/*     */         {
/* 401 */           if (this.current_writer != null)
/*     */           {
/* 403 */             this.current_writer.close();
/*     */             
/* 405 */             this.current_writer = null;
/*     */           }
/*     */           
/* 408 */           this.first_file = (!this.first_file);
/*     */           
/* 410 */           log_file = getLogFile();
/*     */           
/*     */ 
/*     */ 
/* 414 */           log_file.delete();
/*     */         }
/*     */         
/* 417 */         if (this.current_writer == null)
/*     */         {
/* 419 */           this.current_writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(log_file, true), "UTF-8"));
/*     */         }
/*     */         
/* 422 */         for (StringBuilder str : this.pending)
/*     */         {
/* 424 */           this.current_writer.println(str);
/*     */         }
/*     */         
/* 427 */         this.current_writer.flush();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 433 */         this.direct_writes = true;
/* 434 */         this.pending = null;
/*     */         
/* 436 */         if (this.current_writer != null) {}
/*     */       }
/*     */       catch (Throwable e) {}finally
/*     */       {
/* 433 */         this.direct_writes = true;
/* 434 */         this.pending = null;
/*     */         
/* 436 */         if (this.current_writer == null) {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private File getLogFile()
/*     */   {
/* 449 */     return new File(this.debug_dir, getName() + "_" + (this.first_file ? "1" : "2") + ".log");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static String format(int n)
/*     */   {
/* 456 */     if (n < 10)
/*     */     {
/* 458 */       return "0" + n;
/*     */     }
/*     */     
/* 461 */     return String.valueOf(n);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AEDiagnosticsLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */