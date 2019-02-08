/*     */ package org.json.simple.parser;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ 
/*     */ class Yylex
/*     */ {
/*     */   private static final int YY_BUFFER_SIZE = 512;
/*     */   private static final int YY_F = -1;
/*     */   private static final int YY_NO_STATE = -1;
/*     */   private static final int YY_NOT_ACCEPT = 0;
/*     */   private static final int YY_END = 2;
/*     */   private static final int YY_NO_ANCHOR = 4;
/*     */   private static final int YY_BOL = 65536;
/*     */   private static final int YY_EOF = 65537;
/*  15 */   private StringBuffer sb = new StringBuffer();
/*     */   private BufferedReader yy_reader;
/*     */   private int yy_buffer_index;
/*     */   private int yy_buffer_read;
/*     */   private int yy_buffer_start;
/*     */   private int yy_buffer_end;
/*     */   private char[] yy_buffer;
/*     */   private boolean yy_at_bol;
/*     */   private int yy_lexical_state;
/*     */   
/*     */   Yylex(java.io.Reader reader) {
/*  26 */     this();
/*  27 */     if (null == reader) {
/*  28 */       throw new Error("Error: Bad input stream initializer.");
/*     */     }
/*  30 */     this.yy_reader = new BufferedReader(reader);
/*     */   }
/*     */   
/*     */   Yylex(java.io.InputStream instream) {
/*  34 */     this();
/*  35 */     if (null == instream) {
/*  36 */       throw new Error("Error: Bad input stream initializer.");
/*     */     }
/*  38 */     this.yy_reader = new BufferedReader(new java.io.InputStreamReader(instream));
/*     */   }
/*     */   
/*     */   private Yylex() {
/*  42 */     this.yy_buffer = new char['Ȁ'];
/*  43 */     this.yy_buffer_read = 0;
/*  44 */     this.yy_buffer_index = 0;
/*  45 */     this.yy_buffer_start = 0;
/*  46 */     this.yy_buffer_end = 0;
/*  47 */     this.yy_at_bol = true;
/*  48 */     this.yy_lexical_state = 0;
/*     */   }
/*     */   
/*     */ 
/*     */   private static final int YYINITIAL = 0;
/*     */   private static final int STRING_BEGIN = 1;
/*  54 */   private static final int[] yy_state_dtrans = { 0, 39 };
/*     */   private static final int YY_E_INTERNAL = 0;
/*     */   
/*     */   private void yybegin(int state)
/*     */   {
/*  59 */     this.yy_lexical_state = state;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private int yy_advance()
/*     */     throws java.io.IOException
/*     */   {
/*  67 */     if (this.yy_buffer_index < this.yy_buffer_read) {
/*  68 */       return this.yy_buffer[(this.yy_buffer_index++)];
/*     */     }
/*     */     
/*  71 */     if (0 != this.yy_buffer_start) {
/*  72 */       int i = this.yy_buffer_start;
/*  73 */       int j = 0;
/*  74 */       while (i < this.yy_buffer_read) {
/*  75 */         this.yy_buffer[j] = this.yy_buffer[i];
/*  76 */         i++;
/*  77 */         j++;
/*     */       }
/*  79 */       this.yy_buffer_end -= this.yy_buffer_start;
/*  80 */       this.yy_buffer_start = 0;
/*  81 */       this.yy_buffer_read = j;
/*  82 */       this.yy_buffer_index = j;
/*  83 */       int next_read = this.yy_reader.read(this.yy_buffer, this.yy_buffer_read, this.yy_buffer.length - this.yy_buffer_read);
/*     */       
/*     */ 
/*  86 */       if (-1 == next_read) {
/*  87 */         return 65537;
/*     */       }
/*  89 */       this.yy_buffer_read += next_read;
/*     */     }
/*     */     
/*  92 */     while (this.yy_buffer_index >= this.yy_buffer_read) {
/*  93 */       if (this.yy_buffer_index >= this.yy_buffer.length) {
/*  94 */         this.yy_buffer = yy_double(this.yy_buffer);
/*     */       }
/*  96 */       int next_read = this.yy_reader.read(this.yy_buffer, this.yy_buffer_read, this.yy_buffer.length - this.yy_buffer_read);
/*     */       
/*     */ 
/*  99 */       if (-1 == next_read) {
/* 100 */         return 65537;
/*     */       }
/* 102 */       this.yy_buffer_read += next_read;
/*     */     }
/* 104 */     return this.yy_buffer[(this.yy_buffer_index++)];
/*     */   }
/*     */   
/* 107 */   private void yy_move_end() { if ((this.yy_buffer_end > this.yy_buffer_start) && ('\n' == this.yy_buffer[(this.yy_buffer_end - 1)]))
/*     */     {
/* 109 */       this.yy_buffer_end -= 1; }
/* 110 */     if ((this.yy_buffer_end > this.yy_buffer_start) && ('\r' == this.yy_buffer[(this.yy_buffer_end - 1)]))
/*     */     {
/* 112 */       this.yy_buffer_end -= 1; }
/*     */   }
/*     */   
/*     */   private void yy_mark_start() {
/* 116 */     this.yy_buffer_start = this.yy_buffer_index;
/*     */   }
/*     */   
/* 119 */   private void yy_mark_end() { this.yy_buffer_end = this.yy_buffer_index; }
/*     */   
/*     */   private void yy_to_mark() {
/* 122 */     this.yy_buffer_index = this.yy_buffer_end;
/* 123 */     this.yy_at_bol = ((this.yy_buffer_end > this.yy_buffer_start) && (('\r' == this.yy_buffer[(this.yy_buffer_end - 1)]) || ('\n' == this.yy_buffer[(this.yy_buffer_end - 1)]) || ('߬' == this.yy_buffer[(this.yy_buffer_end - 1)]) || ('߭' == this.yy_buffer[(this.yy_buffer_end - 1)])));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String yytext()
/*     */   {
/* 130 */     return new String(this.yy_buffer, this.yy_buffer_start, this.yy_buffer_end - this.yy_buffer_start);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private char[] yy_double(char[] buf)
/*     */   {
/* 140 */     char[] newbuf = new char[2 * buf.length];
/* 141 */     for (int i = 0; i < buf.length; i++) {
/* 142 */       newbuf[i] = buf[i];
/*     */     }
/* 144 */     return newbuf;
/*     */   }
/*     */   
/*     */ 
/* 148 */   private String[] yy_error_string = { "Error: Internal error.\n", "Error: Unmatched input.\n" };
/*     */   
/*     */ 
/*     */   private void yy_error(int code, boolean fatal)
/*     */   {
/* 153 */     System.out.print(this.yy_error_string[code]);
/* 154 */     System.out.flush();
/* 155 */     if (fatal)
/* 156 */       throw new Error("Fatal Error.\n");
/*     */   }
/*     */   
/*     */   private static int[][] unpackFromString(int size1, int size2, String st) {
/* 160 */     int colonIndex = -1;
/*     */     
/* 162 */     int sequenceLength = 0;
/* 163 */     int sequenceInteger = 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 168 */     int[][] res = new int[size1][size2];
/* 169 */     for (int i = 0; i < size1; i++)
/* 170 */       for (int j = 0; j < size2; j++)
/* 171 */         if (sequenceLength != 0) {
/* 172 */           res[i][j] = sequenceInteger;
/* 173 */           sequenceLength--;
/*     */         }
/*     */         else {
/* 176 */           int commaIndex = st.indexOf(',');
/* 177 */           String workString = commaIndex == -1 ? st : st.substring(0, commaIndex);
/*     */           
/* 179 */           st = st.substring(commaIndex + 1);
/* 180 */           colonIndex = workString.indexOf(':');
/* 181 */           if (colonIndex == -1) {
/* 182 */             res[i][j] = Integer.parseInt(workString);
/*     */           }
/*     */           else {
/* 185 */             String lengthString = workString.substring(colonIndex + 1);
/*     */             
/* 187 */             sequenceLength = Integer.parseInt(lengthString);
/* 188 */             workString = workString.substring(0, colonIndex);
/* 189 */             sequenceInteger = Integer.parseInt(workString);
/* 190 */             res[i][j] = sequenceInteger;
/* 191 */             sequenceLength--;
/*     */           }
/*     */         }
/* 194 */     return res; }
/*     */   
/* 196 */   private static final int[] yy_acpt = { 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 243 */   private static final int[] yy_cmap = unpackFromString(1, 65538, "11:8,27:2,28,11,27,28,11:18,27,11,2,11:8,16,25,12,14,3,13:10,26,11:6,10:4,15,10,11:20,23,1,24,11:3,18,4,10:2,17,5,11:5,19,11,6,11:3,7,20,8,9,11:5,21,11,22,11:65410,0:2")[0];
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 248 */   private static final int[] yy_rmap = unpackFromString(1, 45, "0,1:2,2,1:7,3,1:2,4,1:10,5,6,1,7,8,9,10,11,12,13,14,15,16,6,17,18,19,20,21,22")[0];
/*     */   
/*     */ 
/*     */ 
/* 252 */   private static final int[][] yy_nxt = unpackFromString(23, 29, "1,-1,2,-1:2,25,28,-1,29,-1:3,30,3,-1:7,4,5,6,7,8,9,10:2,-1:42,3,33,34,-1,34,-1:24,11,-1,34,-1,34,-1:12,16,17,18,19,20,21,22,23,40,-1:37,31,-1:23,26,-1:24,42,-1:26,32,-1:34,3,-1:34,35,-1:18,37,-1:32,11,-1:27,38,26,-1:2,38,-1:32,37,-1:27,12,-1:26,13,-1:11,1,14,15,27:25,-1:5,44:2,-1:4,44,-1:2,44,-1,44,-1,44:2,-1:14,24:2,-1:4,24,-1:2,24,-1,24,-1,24:2,-1:29,36,-1:13,41:2,-1:4,41,-1:2,41,-1,41,-1,41:2,-1:14,43:2,-1:4,43,-1:2,43,-1,43,-1,43:2,-1:10");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Yytoken yylex()
/*     */     throws java.io.IOException
/*     */   {
/* 263 */     int yy_anchor = 4;
/* 264 */     int yy_state = yy_state_dtrans[this.yy_lexical_state];
/* 265 */     int yy_next_state = -1;
/* 266 */     int yy_last_accept_state = -1;
/* 267 */     boolean yy_initial = true;
/*     */     
/*     */ 
/* 270 */     yy_mark_start();
/* 271 */     int yy_this_accept = yy_acpt[yy_state];
/* 272 */     if (0 != yy_this_accept) {
/* 273 */       yy_last_accept_state = yy_state;
/* 274 */       yy_mark_end(); }
/*     */     for (;;) { int yy_lookahead;
/*     */       int yy_lookahead;
/* 277 */       if ((yy_initial) && (this.yy_at_bol)) yy_lookahead = 65536; else
/* 278 */         yy_lookahead = yy_advance();
/* 279 */       yy_next_state = -1;
/* 280 */       yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
/* 281 */       if ((65537 == yy_lookahead) && (true == yy_initial)) {
/* 282 */         return null;
/*     */       }
/* 284 */       if (-1 != yy_next_state) {
/* 285 */         yy_state = yy_next_state;
/* 286 */         yy_initial = false;
/* 287 */         yy_this_accept = yy_acpt[yy_state];
/* 288 */         if (0 != yy_this_accept) {
/* 289 */           yy_last_accept_state = yy_state;
/* 290 */           yy_mark_end();
/*     */         }
/*     */       }
/*     */       else {
/* 294 */         if (-1 == yy_last_accept_state) {
/* 295 */           throw new Error("Lexical Error: Unmatched Input.");
/*     */         }
/*     */         
/* 298 */         yy_anchor = yy_acpt[yy_last_accept_state];
/* 299 */         if (0 != (0x2 & yy_anchor)) {
/* 300 */           yy_move_end();
/*     */         }
/* 302 */         yy_to_mark();
/* 303 */         switch (yy_last_accept_state)
/*     */         {
/*     */         case -2: 
/*     */         case 1: 
/*     */           break;
/*     */         case 2: 
/* 309 */           this.sb.delete(0, this.sb.length());yybegin(1);
/*     */         case -3: 
/*     */           break;
/*     */         case 3: 
/* 313 */           Long val = Long.valueOf(yytext());return new Yytoken(0, val);
/*     */         case -4: 
/*     */           break;
/*     */         case 4: 
/* 317 */           return new Yytoken(1, null);
/*     */         case -5: 
/*     */           break;
/*     */         case 5: 
/* 321 */           return new Yytoken(2, null);
/*     */         case -6: 
/*     */           break;
/*     */         case 6: 
/* 325 */           return new Yytoken(3, null);
/*     */         case -7: 
/*     */           break;
/*     */         case 7: 
/* 329 */           return new Yytoken(4, null);
/*     */         case -8: 
/*     */           break;
/*     */         case 8: 
/* 333 */           return new Yytoken(5, null);
/*     */         case -9: 
/*     */           break;
/*     */         case 9: 
/* 337 */           return new Yytoken(6, null);
/*     */         case -10: 
/*     */           break;
/*     */         case -11: 
/*     */         case 10: 
/*     */           break;
/*     */         
/*     */         case 11: 
/* 345 */           Double val = Double.valueOf(yytext());return new Yytoken(0, val);
/*     */         case -12: 
/*     */           break;
/*     */         case 12: 
/* 349 */           return new Yytoken(0, null);
/*     */         case -13: 
/*     */           break;
/*     */         case 13: 
/* 353 */           Boolean val = Boolean.valueOf(yytext());return new Yytoken(0, val);
/*     */         case -14: 
/*     */           break;
/*     */         case 14: 
/* 357 */           this.sb.append(yytext());
/*     */         case -15: 
/*     */           break;
/*     */         case 15: 
/* 361 */           yybegin(0);return new Yytoken(0, this.sb.toString());
/*     */         case -16: 
/*     */           break;
/*     */         case 16: 
/* 365 */           this.sb.append('\\');
/*     */         case -17: 
/*     */           break;
/*     */         case 17: 
/* 369 */           this.sb.append('"');
/*     */         case -18: 
/*     */           break;
/*     */         case 18: 
/* 373 */           this.sb.append('/');
/*     */         case -19: 
/*     */           break;
/*     */         case 19: 
/* 377 */           this.sb.append('\b');
/*     */         case -20: 
/*     */           break;
/*     */         case 20: 
/* 381 */           this.sb.append('\f');
/*     */         case -21: 
/*     */           break;
/*     */         case 21: 
/* 385 */           this.sb.append('\n');
/*     */         case -22: 
/*     */           break;
/*     */         case 22: 
/* 389 */           this.sb.append('\r');
/*     */         case -23: 
/*     */           break;
/*     */         case 23: 
/* 393 */           this.sb.append('\t');
/*     */         case -24: 
/*     */           break;
/*     */         case 24: 
/* 397 */           int ch = Integer.parseInt(yytext().substring(2), 16);
/* 398 */           this.sb.append((char)ch);
/*     */         case -25: 
/*     */           break;
/*     */         
/*     */         case 26: 
/* 403 */           Double val = Double.valueOf(yytext());return new Yytoken(0, val);
/*     */         case -26: 
/*     */           break;
/*     */         case 27: 
/* 407 */           this.sb.append(yytext());
/*     */         case -27: 
/*     */           break;
/*     */         case 0: case 25: default: 
/* 411 */           yy_error(0, false);
/*     */         }
/*     */         
/* 414 */         yy_initial = true;
/* 415 */         yy_state = yy_state_dtrans[this.yy_lexical_state];
/* 416 */         yy_next_state = -1;
/* 417 */         yy_last_accept_state = -1;
/* 418 */         yy_mark_start();
/* 419 */         yy_this_accept = yy_acpt[yy_state];
/* 420 */         if (0 != yy_this_accept) {
/* 421 */           yy_last_accept_state = yy_state;
/* 422 */           yy_mark_end();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/json/simple/parser/Yylex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */