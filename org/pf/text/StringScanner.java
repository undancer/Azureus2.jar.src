/*     */ package org.pf.text;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StringScanner
/*     */ {
/*     */   public static final char END_REACHED = '￿';
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  33 */   protected int length = 0;
/*  34 */   protected int position = 0;
/*  35 */   protected int pos_marker = 0;
/*  36 */   protected char[] buffer = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean endReached(char character)
/*     */   {
/*  47 */     return character == 65535;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean endNotReached(char character)
/*     */   {
/*  58 */     return !endReached(character);
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
/*     */   public StringScanner(String stringToScan)
/*     */   {
/*  72 */     this.length = stringToScan.length();
/*  73 */     this.buffer = new char[this.length];
/*  74 */     stringToScan.getChars(0, this.length, this.buffer, 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/*  85 */     return new String(this.buffer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void skip(int count)
/*     */   {
/*  97 */     this.position += count;
/*  98 */     if (this.position < 0) {
/*  99 */       this.position = 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public char peek()
/*     */   {
/* 111 */     return this.position < length() ? this.buffer[this.position] : 65535;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public char nextChar()
/*     */   {
/* 122 */     char next = peek();
/* 123 */     if (endNotReached(next))
/* 124 */       skip(1);
/* 125 */     return next;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean atEnd()
/*     */   {
/* 136 */     return endReached(peek());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasNext()
/*     */   {
/* 146 */     return !atEnd();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public char nextNoneWhitespaceChar()
/*     */   {
/* 157 */     char next = nextChar();
/* 158 */     while ((endNotReached(next)) && (Character.isWhitespace(next)))
/*     */     {
/* 160 */       next = nextChar();
/*     */     }
/* 162 */     return next;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPosition()
/*     */   {
/* 172 */     return this.position;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void markPosition()
/*     */   {
/* 182 */     this.pos_marker = this.position;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void restorePosition()
/*     */   {
/* 192 */     setPosition(this.pos_marker);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int length()
/*     */   {
/* 202 */     return this.length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setPosition(int pos)
/*     */   {
/* 209 */     if ((pos >= 0) && (pos <= length())) {
/* 210 */       this.position = pos;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/pf/text/StringScanner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */