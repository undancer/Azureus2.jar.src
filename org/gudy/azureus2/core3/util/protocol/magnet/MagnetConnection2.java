/*     */ package org.gudy.azureus2.core3.util.protocol.magnet;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MagnetConnection2
/*     */   extends HttpURLConnection
/*     */ {
/*     */   private static final String NL = "\r\n";
/*  49 */   static final LinkedList<MagnetOutputStream> active_os = new LinkedList();
/*     */   private static TimerEventPeriodic active_os_event;
/*     */   private final MagnetHandler handler;
/*     */   private OutputStream output_stream;
/*     */   private InputStream input_stream;
/*     */   
/*     */   private static void addActiveStream(MagnetOutputStream os) {
/*  56 */     synchronized (active_os)
/*     */     {
/*  58 */       active_os.add(os);
/*     */       
/*  60 */       if ((active_os.size() == 1) && (active_os_event == null))
/*     */       {
/*  62 */         active_os_event = SimpleTimer.addPeriodicEvent("mos:checker", 30000L, new TimerEventPerformer()
/*     */         {
/*     */           public void perform(TimerEvent event)
/*     */           {
/*     */             List<MagnetConnection2.MagnetOutputStream> active;
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  74 */             synchronized (MagnetConnection2.active_os)
/*     */             {
/*  76 */               active = new ArrayList(MagnetConnection2.active_os);
/*     */             }
/*     */             
/*  79 */             for (MagnetConnection2.MagnetOutputStream os : active)
/*     */             {
/*  81 */               MagnetConnection2.MagnetOutputStream.access$000(os);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void removeActiveStream(MagnetOutputStream os)
/*     */   {
/*  93 */     synchronized (active_os)
/*     */     {
/*  95 */       active_os.remove(os);
/*     */       
/*  97 */       if ((active_os.size() == 0) && (active_os_event != null))
/*     */       {
/*  99 */         active_os_event.cancel();
/*     */         
/* 101 */         active_os_event = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 110 */   private final LinkedList<String> status_list = new LinkedList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public MagnetConnection2(URL _url, MagnetHandler _handler)
/*     */   {
/* 117 */     super(_url);
/*     */     
/* 119 */     this.handler = _handler;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void connect()
/*     */     throws IOException
/*     */   {
/* 127 */     MagnetOutputStream mos = new MagnetOutputStream(null);
/* 128 */     MagnetInputStream mis = new MagnetInputStream(mos, null);
/*     */     
/* 130 */     this.input_stream = mis;
/* 131 */     this.output_stream = mos;
/*     */     
/* 133 */     this.handler.process(getURL(), mos);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/* 141 */     String line = "";
/*     */     
/* 143 */     byte[] buffer = new byte[1];
/*     */     
/* 145 */     byte[] line_bytes = new byte['ࠀ'];
/* 146 */     int line_bytes_pos = 0;
/*     */     
/*     */     for (;;)
/*     */     {
/* 150 */       int len = this.input_stream.read(buffer);
/*     */       
/* 152 */       if (len == -1) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/* 157 */       line = line + (char)buffer[0];
/*     */       
/* 159 */       line_bytes[(line_bytes_pos++)] = buffer[0];
/*     */       
/* 161 */       if (line.endsWith("\r\n"))
/*     */       {
/* 163 */         line = line.trim();
/*     */         
/* 165 */         if (line.length() == 0) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/* 170 */         if (line.startsWith("X-Report:"))
/*     */         {
/* 172 */           line = new String(line_bytes, 0, line_bytes_pos, "UTF-8");
/*     */           
/* 174 */           line = line.substring(9);
/*     */           
/* 176 */           line = line.trim();
/*     */           
/* 178 */           synchronized (this.status_list)
/*     */           {
/* 180 */             String str = Character.toUpperCase(line.charAt(0)) + line.substring(1);
/*     */             
/* 182 */             if (this.status_list.size() == 0)
/*     */             {
/* 184 */               this.status_list.addLast(str);
/*     */             }
/* 186 */             else if (!((String)this.status_list.getLast()).equals(str))
/*     */             {
/* 188 */               this.status_list.addLast(str);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 193 */         line = "";
/* 194 */         line_bytes_pos = 0;
/*     */       }
/*     */     }
/*     */     
/* 198 */     return this.input_stream;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getResponseCode()
/*     */   {
/* 204 */     return 200;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getResponseMessage()
/*     */   {
/* 210 */     synchronized (this.status_list)
/*     */     {
/* 212 */       if (this.status_list.size() == 0)
/*     */       {
/* 214 */         return "";
/*     */       }
/* 216 */       if (this.status_list.size() == 1)
/*     */       {
/* 218 */         return (String)this.status_list.get(0);
/*     */       }
/*     */       
/*     */ 
/* 222 */       return (String)this.status_list.removeFirst();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<String> getResponseMessages(boolean error_only)
/*     */   {
/* 231 */     synchronized (this.status_list)
/*     */     {
/* 233 */       if (error_only)
/*     */       {
/* 235 */         List<String> response = new ArrayList();
/*     */         
/* 237 */         for (String s : this.status_list)
/*     */         {
/* 239 */           if (s.toLowerCase().startsWith("error:"))
/*     */           {
/* 241 */             response.add(s);
/*     */           }
/*     */         }
/*     */         
/* 245 */         return response;
/*     */       }
/*     */       
/*     */ 
/* 249 */       return new ArrayList(this.status_list);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean usingProxy()
/*     */   {
/* 257 */     return false;
/*     */   }
/*     */   
/*     */   public void disconnect()
/*     */   {
/*     */     try
/*     */     {
/* 264 */       this.output_stream.close();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 268 */       Debug.printStackTrace(e);
/*     */     }
/*     */     try
/*     */     {
/* 272 */       this.input_stream.close();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 276 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private class MagnetOutputStream
/*     */     extends OutputStream
/*     */   {
/* 284 */     private final LinkedList<byte[]> buffers = new LinkedList();
/*     */     private int available;
/* 286 */     private final AESemaphore buffer_sem = new AESemaphore("mos:buffers");
/*     */     
/*     */     private boolean closed;
/* 289 */     private long last_read = SystemTime.getMonotonousTime();
/*     */     
/*     */     private int read_active;
/*     */     
/*     */     private MagnetOutputStream()
/*     */     {
/* 295 */       MagnetConnection2.addActiveStream(this);
/*     */     }
/*     */     
/*     */ 
/*     */     private void timerCheck()
/*     */     {
/* 301 */       synchronized (this.buffers)
/*     */       {
/* 303 */         if ((this.closed) || (this.read_active > 0) || (SystemTime.getMonotonousTime() - this.last_read < 60000L))
/*     */         {
/*     */ 
/*     */ 
/* 307 */           return;
/*     */         }
/*     */       }
/*     */       
/* 311 */       Debug.out("Abandoning magnet download for " + MagnetConnection2.this.getURL() + " as no active reader");
/*     */       try
/*     */       {
/* 314 */         close();
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void write(int b)
/*     */       throws IOException
/*     */     {
/* 327 */       synchronized (this.buffers)
/*     */       {
/* 329 */         if (this.closed)
/*     */         {
/* 331 */           throw new IOException("Connection closed");
/*     */         }
/*     */         
/* 334 */         this.buffers.addLast(new byte[] { (byte)b });
/*     */         
/* 336 */         this.available += 1;
/*     */         
/* 338 */         this.buffer_sem.release();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void write(byte[] b, int off, int len)
/*     */       throws IOException
/*     */     {
/* 350 */       synchronized (this.buffers)
/*     */       {
/* 352 */         if (this.closed)
/*     */         {
/* 354 */           throw new IOException("Connection closed");
/*     */         }
/*     */         
/* 357 */         if (len > 0)
/*     */         {
/* 359 */           byte[] new_b = new byte[len];
/*     */           
/* 361 */           System.arraycopy(b, off, new_b, 0, len);
/*     */           
/* 363 */           this.buffers.addLast(new_b);
/*     */           
/* 365 */           this.available += len;
/*     */           
/* 367 */           this.buffer_sem.release();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private int read()
/*     */       throws IOException
/*     */     {
/* 377 */       synchronized (this.buffers)
/*     */       {
/* 379 */         this.last_read = SystemTime.getMonotonousTime();
/*     */         
/* 381 */         this.read_active += 1;
/*     */       }
/*     */       try
/*     */       {
/* 385 */         this.buffer_sem.reserve();
/*     */       }
/*     */       finally
/*     */       {
/* 389 */         synchronized (this.buffers)
/*     */         {
/* 391 */           this.last_read = SystemTime.getMonotonousTime();
/*     */           
/* 393 */           this.read_active -= 1;
/*     */         }
/*     */       }
/*     */       
/* 397 */       synchronized (this.buffers)
/*     */       {
/* 399 */         if ((this.closed) && (this.buffers.size() == 0))
/*     */         {
/* 401 */           return -1;
/*     */         }
/*     */         
/* 404 */         byte[] b = (byte[])this.buffers.removeFirst();
/*     */         
/* 406 */         if (b.length > 1)
/*     */         {
/* 408 */           for (int i = b.length - 1; i > 0; i--)
/*     */           {
/* 410 */             this.buffers.addFirst(new byte[] { b[i] });
/*     */             
/* 412 */             this.buffer_sem.release();
/*     */           }
/*     */         }
/*     */         
/* 416 */         this.available -= 1;
/*     */         
/* 418 */         return b[0] & 0xFF;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private int read(byte[] buffer, int off, int len)
/*     */       throws IOException
/*     */     {
/* 430 */       synchronized (this.buffers)
/*     */       {
/* 432 */         this.last_read = SystemTime.getMonotonousTime();
/*     */         
/* 434 */         this.read_active += 1;
/*     */       }
/*     */       try
/*     */       {
/* 438 */         this.buffer_sem.reserve();
/*     */       }
/*     */       finally
/*     */       {
/* 442 */         synchronized (this.buffers)
/*     */         {
/* 444 */           this.last_read = SystemTime.getMonotonousTime();
/*     */           
/* 446 */           this.read_active -= 1;
/*     */         }
/*     */       }
/*     */       
/* 450 */       synchronized (this.buffers)
/*     */       {
/* 452 */         int read = 0;
/*     */         
/*     */         for (;;)
/*     */         {
/* 456 */           if ((this.closed) && (this.buffers.size() == 0))
/*     */           {
/* 458 */             return read == 0 ? -1 : read;
/*     */           }
/*     */           
/* 461 */           byte[] b = (byte[])this.buffers.removeFirst();
/*     */           
/* 463 */           int b_len = b.length;
/*     */           
/* 465 */           if (b_len >= len)
/*     */           {
/* 467 */             read += len;
/*     */             
/* 469 */             System.arraycopy(b, 0, buffer, off, len);
/*     */             
/* 471 */             if (b_len > len)
/*     */             {
/* 473 */               byte[] new_b = new byte[b_len - len];
/*     */               
/* 475 */               System.arraycopy(b, len, new_b, 0, new_b.length);
/*     */               
/* 477 */               this.buffers.addFirst(new_b);
/*     */               
/* 479 */               this.buffer_sem.release();
/*     */             }
/*     */             
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/* 486 */             read += b_len;
/*     */             
/* 488 */             System.arraycopy(b, 0, buffer, off, b_len);
/*     */             
/* 490 */             off += b_len;
/* 491 */             len -= b_len;
/*     */             
/*     */ 
/* 494 */             if (!this.buffer_sem.reserveIfAvailable()) {
/*     */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 500 */         this.available -= read;
/*     */         
/* 502 */         return read;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private int available()
/*     */       throws IOException
/*     */     {
/* 511 */       synchronized (this.buffers)
/*     */       {
/* 513 */         if (this.available > 0)
/*     */         {
/* 515 */           return this.available;
/*     */         }
/*     */         
/* 518 */         if (this.closed)
/*     */         {
/* 520 */           throw new IOException("Connection closed");
/*     */         }
/*     */         
/* 523 */         return 0;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 532 */       synchronized (this.buffers)
/*     */       {
/* 534 */         if (this.closed)
/*     */         {
/* 536 */           return;
/*     */         }
/*     */         
/* 539 */         this.closed = true;
/*     */         
/* 541 */         this.buffer_sem.releaseForever();
/*     */       }
/*     */       
/* 544 */       MagnetConnection2.removeActiveStream(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class MagnetInputStream
/*     */     extends InputStream
/*     */   {
/*     */     private final MagnetConnection2.MagnetOutputStream out;
/*     */     
/*     */ 
/*     */     private MagnetInputStream(MagnetConnection2.MagnetOutputStream _out)
/*     */     {
/* 558 */       this.out = _out;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int read()
/*     */       throws IOException
/*     */     {
/* 566 */       return MagnetConnection2.MagnetOutputStream.access$500(this.out);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public int read(byte[] b, int off, int len)
/*     */       throws IOException
/*     */     {
/* 577 */       return MagnetConnection2.MagnetOutputStream.access$600(this.out, b, off, len);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int available()
/*     */       throws IOException
/*     */     {
/* 585 */       return MagnetConnection2.MagnetOutputStream.access$700(this.out);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public long skip(long n)
/*     */       throws IOException
/*     */     {
/* 594 */       throw new IOException("Not supported");
/*     */     }
/*     */     
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 601 */       this.out.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface MagnetHandler
/*     */   {
/*     */     public abstract void process(URL paramURL, OutputStream paramOutputStream)
/*     */       throws IOException;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/protocol/magnet/MagnetConnection2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */