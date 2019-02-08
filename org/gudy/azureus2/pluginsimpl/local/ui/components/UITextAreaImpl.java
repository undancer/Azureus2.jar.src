/*     */ package org.gudy.azureus2.pluginsimpl.local.ui.components;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.FrequencyLimitedDispatcher;
/*     */ import org.gudy.azureus2.plugins.ui.components.UIPropertyChangeListener;
/*     */ import org.gudy.azureus2.plugins.ui.components.UITextArea;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UITextAreaImpl
/*     */   extends UIComponentImpl
/*     */   implements UITextArea
/*     */ {
/*  44 */   private final boolean enable_history = System.getProperty("az.logging.keep.ui.history", "true").equals("true");
/*     */   
/*  46 */   private int max_size = 60000;
/*  47 */   private int max_file_size = 20 * this.max_size;
/*     */   
/*     */   PoopWriter pw;
/*     */   int current_file_size;
/*     */   File poop_file;
/*  52 */   boolean useFile = true;
/*     */   
/*  54 */   AEMonitor file_mon = new AEMonitor("filemon");
/*     */   
/*  56 */   LinkedList<String> delay_text = new LinkedList();
/*  57 */   int delay_size = 0;
/*     */   
/*  59 */   FrequencyLimitedDispatcher dispatcher = new FrequencyLimitedDispatcher(new AERunnable()
/*     */   {
/*     */ 
/*     */ 
/*     */     public void runSupport()
/*     */     {
/*     */ 
/*  66 */       UITextAreaImpl.this.delayAppend();
/*     */     }
/*  59 */   }, 500);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public UITextAreaImpl()
/*     */   {
/*  74 */     setText("");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setText(String text)
/*     */   {
/*  81 */     if (!this.enable_history)
/*     */     {
/*  83 */       return;
/*     */     }
/*     */     
/*  86 */     if (this.useFile) {
/*     */       try
/*     */       {
/*  89 */         this.file_mon.enter();
/*     */         
/*  91 */         if (this.pw == null)
/*     */         {
/*  93 */           this.pw = new PoopWriter();
/*     */           
/*  95 */           this.pw.print(text);
/*     */           
/*  97 */           this.current_file_size = text.length(); return;
/*     */         }
/*     */         
/*     */       }
/*     */       finally
/*     */       {
/* 103 */         this.file_mon.exit();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 109 */     if (text.length() > this.max_size)
/*     */     {
/* 111 */       int size_to_show = this.max_size - 10000;
/*     */       
/* 113 */       if (size_to_show < 0)
/*     */       {
/* 115 */         size_to_show = this.max_size;
/*     */       }
/*     */       
/* 118 */       text = text.substring(text.length() - size_to_show);
/*     */     }
/*     */     
/* 121 */     setProperty("value", text);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void appendText(String text)
/*     */   {
/* 128 */     if (!this.enable_history)
/*     */     {
/* 130 */       return;
/*     */     }
/*     */     
/* 133 */     if ((this.useFile) && (this.pw != null)) {
/*     */       try
/*     */       {
/* 136 */         this.file_mon.enter();
/*     */         
/*     */ 
/*     */ 
/* 140 */         if (this.current_file_size > this.max_file_size)
/*     */         {
/* 142 */           this.current_file_size = getFileText().length();
/*     */         }
/*     */         
/* 145 */         this.pw.print(text);
/*     */         
/* 147 */         this.current_file_size += text.length(); return;
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/*     */ 
/* 153 */         this.file_mon.exit();
/*     */       }
/*     */     }
/*     */     
/* 157 */     synchronized (this)
/*     */     {
/* 159 */       this.delay_text.addLast(text);
/*     */       
/* 161 */       this.delay_size += text.length();
/*     */       
/* 163 */       while (this.delay_size > this.max_size)
/*     */       {
/* 165 */         if (this.delay_text.size() == 0) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/* 170 */         String s = (String)this.delay_text.removeFirst();
/*     */         
/* 172 */         this.delay_size -= s.length();
/*     */       }
/*     */     }
/*     */     
/* 176 */     this.dispatcher.dispatch();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void delayAppend()
/*     */   {
/* 182 */     String str = getText();
/*     */     
/*     */     String text;
/*     */     
/* 186 */     synchronized (this) {
/*     */       String text;
/* 188 */       if (this.delay_text.size() == 1)
/*     */       {
/* 190 */         text = (String)this.delay_text.get(0);
/*     */       }
/*     */       else
/*     */       {
/* 194 */         StringBuilder sb = new StringBuilder(this.delay_size);
/*     */         
/* 196 */         Iterator<String> it = this.delay_text.iterator();
/*     */         
/* 198 */         while (it.hasNext())
/*     */         {
/* 200 */           sb.append((String)it.next());
/*     */         }
/*     */         
/* 203 */         text = sb.toString();
/*     */       }
/*     */       
/* 206 */       this.delay_text.clear();
/* 207 */       this.delay_size = 0;
/*     */     }
/*     */     
/* 210 */     if (str == null)
/*     */     {
/* 212 */       setText(text);
/*     */     }
/*     */     else
/*     */     {
/* 216 */       setText(str + text);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getText()
/*     */   {
/* 223 */     if (!this.enable_history)
/*     */     {
/* 225 */       return "";
/*     */     }
/*     */     
/* 228 */     if ((this.useFile) && (this.pw != null))
/*     */     {
/* 230 */       return getFileText();
/*     */     }
/*     */     
/* 233 */     return (String)getProperty("value");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMaximumSize(int _max_size)
/*     */   {
/* 240 */     this.max_size = _max_size;
/*     */   }
/*     */   
/*     */   private String getFileText()
/*     */   {
/*     */     try
/*     */     {
/* 247 */       this.file_mon.enter();
/*     */       
/* 249 */       String text = null;
/*     */       
/* 251 */       if (this.pw != null)
/*     */       {
/* 253 */         this.pw.close();
/*     */         
/* 255 */         text = this.pw.getText();
/*     */       }
/*     */       
/* 258 */       if (text == null)
/*     */       {
/* 260 */         text = "";
/*     */       }
/*     */       
/* 263 */       this.pw = null;
/*     */       
/* 265 */       if (this.useFile)
/*     */       {
/* 267 */         this.pw = new PoopWriter();
/*     */         
/* 269 */         this.pw.print(text);
/*     */         
/* 271 */         this.current_file_size = text.length();
/*     */       }
/*     */       
/* 274 */       return text;
/*     */     }
/*     */     finally
/*     */     {
/* 278 */       this.file_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addPropertyChangeListener(UIPropertyChangeListener l)
/*     */   {
/* 286 */     if (this.useFile)
/*     */     {
/* 288 */       this.useFile = false;
/*     */       
/* 290 */       setText(getFileText());
/*     */     }
/*     */     
/* 293 */     super.addPropertyChangeListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */   protected class PoopWriter
/*     */   {
/* 299 */     private StringBuffer buffer = new StringBuffer(256);
/*     */     
/*     */     private PrintWriter pw;
/*     */     
/*     */     protected PoopWriter() {}
/*     */     
/*     */     private void print(String text)
/*     */     {
/* 307 */       if (this.pw == null)
/*     */       {
/* 309 */         this.buffer.append(text);
/*     */         
/* 311 */         if (this.buffer.length() > 8192)
/*     */         {
/* 313 */           if (UITextAreaImpl.this.poop_file == null) {
/*     */             try
/*     */             {
/* 316 */               UITextAreaImpl.this.poop_file = AETemporaryFileHandler.createTempFile();
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */           
/*     */ 
/* 322 */           if (UITextAreaImpl.this.poop_file != null) {
/*     */             try
/*     */             {
/* 325 */               this.pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(UITextAreaImpl.this.poop_file), "UTF-8"));
/*     */               
/* 327 */               this.pw.print(this.buffer.toString());
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */           
/*     */ 
/* 333 */           this.buffer.setLength(0);
/*     */         }
/*     */       }
/*     */       else {
/* 337 */         this.pw.print(text);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     private String getText()
/*     */     {
/* 344 */       if (UITextAreaImpl.this.poop_file == null)
/*     */       {
/* 346 */         return this.buffer.toString();
/*     */       }
/*     */       
/*     */       try
/*     */       {
/* 351 */         return FileUtil.readFileEndAsString(UITextAreaImpl.this.poop_file, UITextAreaImpl.this.max_size, "UTF-8");
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/* 355 */       return "";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private void close()
/*     */     {
/* 363 */       if (this.pw != null)
/*     */       {
/* 365 */         this.pw.close();
/*     */         
/* 367 */         this.pw = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/components/UITextAreaImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */