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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StringExaminer
/*     */   extends StringScanner
/*     */ {
/*  34 */   private boolean ignoreCase = false;
/*  35 */   protected boolean ignoreCase() { return this.ignoreCase; }
/*  36 */   protected void ignoreCase(boolean newValue) { this.ignoreCase = newValue; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public StringExaminer(String stringToExamine)
/*     */   {
/*  54 */     this(stringToExamine, false);
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
/*     */   public StringExaminer(String stringToExamine, boolean ignoreCase)
/*     */   {
/*  67 */     super(stringToExamine);
/*  68 */     ignoreCase(ignoreCase);
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
/*     */   public boolean skipAfter(String matchString)
/*     */   {
/*  88 */     char ch = '-';
/*  89 */     char matchChar = ' ';
/*  90 */     boolean found = false;
/*  91 */     int index = 0;
/*     */     
/*  93 */     if ((matchString == null) || (matchString.length() == 0)) {
/*  94 */       return false;
/*     */     }
/*  96 */     ch = nextChar();
/*  97 */     while ((endNotReached(ch)) && (!found))
/*     */     {
/*  99 */       matchChar = matchString.charAt(index);
/* 100 */       if (charsAreEqual(ch, matchChar))
/*     */       {
/* 102 */         index++;
/* 103 */         if (index >= matchString.length())
/*     */         {
/* 105 */           found = true;
/*     */         }
/*     */         else
/*     */         {
/* 109 */           ch = nextChar();
/*     */         }
/*     */         
/*     */ 
/*     */       }
/* 114 */       else if (index == 0)
/*     */       {
/* 116 */         ch = nextChar();
/*     */       }
/*     */       else
/*     */       {
/* 120 */         index = 0;
/*     */       }
/*     */     }
/*     */     
/* 124 */     return found;
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
/*     */   public boolean skipBefore(String matchString)
/*     */   {
/* 144 */     boolean found = skipAfter(matchString);
/* 145 */     if (found) {
/* 146 */       skip(0 - matchString.length());
/*     */     }
/* 148 */     return found;
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
/*     */   public String peekUpToEnd()
/*     */   {
/* 161 */     return upToEnd(true);
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
/*     */   public String upToEnd()
/*     */   {
/* 174 */     return upToEnd(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean charsAreEqual(char char1, char char2)
/*     */   {
/* 185 */     return Character.toUpperCase(char1) == Character.toUpperCase(char2);
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
/*     */   protected String upToEnd(boolean peek)
/*     */   {
/* 201 */     char result = '-';
/* 202 */     int lastPosition = 0;
/* 203 */     StringBuffer buffer = new StringBuffer(100);
/*     */     
/* 205 */     lastPosition = getPosition();
/* 206 */     result = nextChar();
/* 207 */     while (endNotReached(result))
/*     */     {
/* 209 */       buffer.append(result);
/* 210 */       result = nextChar();
/*     */     }
/* 212 */     if (peek) {
/* 213 */       setPosition(lastPosition);
/*     */     }
/* 215 */     return buffer.toString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/pf/text/StringExaminer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */