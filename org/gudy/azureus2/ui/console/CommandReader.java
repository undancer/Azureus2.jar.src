/*     */ package org.gudy.azureus2.ui.console;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CommandReader
/*     */   extends Reader
/*     */ {
/*     */   private static final int ENTER = 0;
/*     */   private static final int TAB = 1;
/*     */   private static final int QUOTE = 3;
/*     */   private static final int ESCAPE = 4;
/*     */   private static final int NONQUOTEDESCAPE = 5;
/*     */   private Reader in;
/*     */   
/*     */   public CommandReader(Reader _in)
/*     */   {
/*  42 */     this.in = _in;
/*     */   }
/*     */   
/*     */   private void ensureOpen() throws IOException {
/*  46 */     if (this.in == null)
/*  47 */       throw new IOException("Stream closed");
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/*  51 */     synchronized (this.lock) {
/*  52 */       if (this.in != null) {
/*  53 */         this.in.close();
/*  54 */         this.in = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public int read() throws IOException {
/*  60 */     synchronized (this.lock) {
/*  61 */       ensureOpen();
/*  62 */       return this.in.read();
/*     */     }
/*     */   }
/*     */   
/*     */   public int read(char[] cbuf, int off, int len) throws IOException {
/*  67 */     synchronized (this.lock) {
/*  68 */       ensureOpen();
/*  69 */       return this.in.read(cbuf, off, len);
/*     */     }
/*     */   }
/*     */   
/*     */   public String readLine() throws IOException {
/*  74 */     synchronized (this.lock) {
/*  75 */       ensureOpen();
/*  76 */       StringBuilder line = new StringBuilder();
/*     */       int ch;
/*  78 */       while ((char)(ch = this.in.read()) != '\n')
/*     */       {
/*  80 */         if (ch == -1)
/*     */         {
/*  82 */           throw new IOException("stream closed");
/*     */         }
/*  84 */         line.append((char)ch);
/*     */       }
/*  86 */       return line.toString().trim();
/*     */     }
/*     */   }
/*     */   
/*     */   public List parseCommandLine(String commandLine) {
/*  91 */     StringBuffer current = new StringBuffer();
/*  92 */     Vector args = new Vector();
/*  93 */     boolean allowEmpty = false;
/*  94 */     boolean bailout = commandLine.length() == 0;
/*  95 */     int index = 0;
/*  96 */     int state = 0;
/*     */     
/*  98 */     while (!bailout)
/*     */     {
/* 100 */       int ch = commandLine.charAt(index++);
/* 101 */       bailout = index == commandLine.length();
/* 102 */       char c = (char)ch;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 109 */       switch (state)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       case 0: 
/* 130 */         switch (c) {
/*     */         case '"': 
/* 132 */           state = 3;
/* 133 */           break;
/*     */         
/*     */ 
/*     */ 
/*     */         case '\\': 
/* 138 */           state = 5;
/* 139 */           break;
/*     */         
/*     */         case '\r': 
/*     */           break;
/*     */         
/*     */ 
/*     */         default: 
/* 146 */           current.append(c);
/*     */         }
/* 148 */         if ((state == 0) && ((c == ' ') || (bailout))) {
/* 149 */           String arg = current.toString().trim();
/* 150 */           if ((arg.length() > 0) || (allowEmpty))
/*     */           {
/* 152 */             args.addElement(arg);
/* 153 */             allowEmpty = false;
/*     */           }
/* 155 */           current = new StringBuffer(); }
/* 156 */         break;
/*     */       
/*     */ 
/*     */       case 3: 
/* 160 */         switch (c) {
/*     */         case '"': 
/* 162 */           allowEmpty = true;
/* 163 */           state = 0;
/* 164 */           break;
/*     */         case '\\': 
/* 166 */           state = 4;
/* 167 */           break;
/*     */         default: 
/* 169 */           current.append(c);
/*     */         }
/* 171 */         break;
/*     */       
/*     */       case 4: 
/* 174 */         switch (c) {
/* 175 */         case 'n':  c = '\n'; break;
/* 176 */         case 'r':  c = '\r'; break;
/* 177 */         case 't':  c = '\t'; break;
/* 178 */         case 'b':  c = '\b'; break;
/* 179 */         case 'f':  c = '\f'; break;
/* 180 */         default:  current.append('\\');
/*     */         }
/* 182 */         state = 3;
/* 183 */         current.append(c);
/* 184 */         break;
/*     */       case 5: 
/* 186 */         switch (c) {
/*     */         case ';': 
/* 188 */           state = 0;
/* 189 */           current.append(c);
/* 190 */           break;
/*     */         default: 
/* 192 */           state = 0;
/* 193 */           current.append('\\');
/* 194 */           current.append(c);
/*     */         }
/*     */         break;
/*     */       }
/*     */       
/*     */     }
/* 200 */     if ((state == 0) && ((current.toString().trim().length() > 0) || (allowEmpty)))
/*     */     {
/* 202 */       String arg = current.toString().trim();
/* 203 */       args.addElement(arg);
/*     */     }
/* 205 */     return args;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/CommandReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */