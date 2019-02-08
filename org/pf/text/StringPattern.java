/*     */ package org.pf.text;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StringPattern
/*     */   implements Serializable
/*     */ {
/*     */   protected static final String MULTI_WILDCARD = "*";
/*     */   protected static final char MULTICHAR_WILDCARD = '*';
/*     */   protected static final char SINGLECHAR_WILDCARD = '?';
/*  56 */   private boolean ignoreCase = false;
/*     */   
/*     */   public boolean getIgnoreCase()
/*     */   {
/*  60 */     return this.ignoreCase;
/*     */   }
/*     */   
/*     */ 
/*  64 */   public void setIgnoreCase(boolean newValue) { this.ignoreCase = newValue; }
/*     */   
/*  66 */   private String pattern = null;
/*     */   
/*     */   public String getPattern()
/*     */   {
/*  70 */     return this.pattern;
/*     */   }
/*     */   
/*     */   public void setPattern(String newValue) {
/*  74 */     this.pattern = newValue;
/*     */   }
/*     */   
/*     */ 
/*  78 */   private Character digitWildcard = null;
/*  79 */   protected Character digitWildcard() { return this.digitWildcard; }
/*  80 */   protected void digitWildcard(Character newValue) { this.digitWildcard = newValue; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean match(String probe, String pattern)
/*     */   {
/*  94 */     StringPattern stringPattern = new StringPattern(pattern, false);
/*  95 */     return stringPattern.matches(probe);
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
/*     */   public static boolean matchIgnoreCase(String probe, String pattern)
/*     */   {
/* 109 */     StringPattern stringPattern = new StringPattern(pattern, true);
/* 110 */     return stringPattern.matches(probe);
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
/*     */   public StringPattern(String pattern, boolean ignoreCase)
/*     */   {
/* 127 */     setPattern(pattern);
/* 128 */     setIgnoreCase(ignoreCase);
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
/*     */   public StringPattern(String pattern)
/*     */   {
/* 141 */     this(pattern, false);
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
/*     */   public StringPattern(String pattern, char digitWildcard)
/*     */   {
/* 156 */     this(pattern, false, digitWildcard);
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
/*     */   public StringPattern(String pattern, boolean ignoreCase, char digitWildcard)
/*     */   {
/* 172 */     setPattern(pattern);
/* 173 */     setIgnoreCase(ignoreCase);
/* 174 */     setDigitWildcardChar(digitWildcard);
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
/*     */   public boolean matches(String probe)
/*     */   {
/* 191 */     StringExaminer patternIterator = null;
/* 192 */     StringExaminer probeIterator = null;
/* 193 */     char patternCh = '-';
/* 194 */     char probeCh = '-';
/* 195 */     String newPattern = null;
/* 196 */     String subPattern = null;
/* 197 */     int charIndex = 0;
/*     */     
/* 199 */     if (probe == null) return false;
/* 200 */     if (probe.length() == 0) { return false;
/*     */     }
/* 202 */     patternIterator = newExaminer(getPattern());
/* 203 */     probeIterator = newExaminer(probe);
/*     */     
/* 205 */     probeCh = probeIterator.nextChar();
/* 206 */     patternCh = getPatternChar(patternIterator, probeCh);
/*     */     
/* 208 */     while ((endNotReached(patternCh)) && (endNotReached(probeCh)))
/*     */     {
/*     */ 
/*     */ 
/* 212 */       if (patternCh == '*')
/*     */       {
/* 214 */         patternCh = skipWildcards(patternIterator);
/* 215 */         if (endReached(patternCh))
/*     */         {
/* 217 */           return true;
/*     */         }
/*     */         
/*     */ 
/* 221 */         patternIterator.skip(-1);
/* 222 */         newPattern = upToEnd(patternIterator);
/* 223 */         charIndex = newPattern.indexOf('*');
/* 224 */         if (charIndex >= 0)
/*     */         {
/* 226 */           subPattern = newPattern.substring(0, charIndex);
/*     */           
/* 228 */           if (skipAfter(probeIterator, subPattern))
/*     */           {
/* 230 */             patternIterator = newExaminer(newPattern.substring(charIndex));
/* 231 */             patternCh = probeCh;
/*     */           }
/*     */           else
/*     */           {
/* 235 */             return false;
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 240 */           probeIterator.skip(-1);
/* 241 */           return matchReverse(newPattern, probeIterator);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 246 */       if (charsAreEqual(probeCh, patternCh))
/*     */       {
/* 248 */         if (endNotReached(patternCh))
/*     */         {
/* 250 */           probeCh = probeIterator.nextChar();
/* 251 */           patternCh = getPatternChar(patternIterator, probeCh);
/*     */         }
/*     */         
/*     */ 
/*     */       }
/* 256 */       else if (patternCh != '*') {
/* 257 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 261 */     return (endReached(patternCh)) && (endReached(probeCh));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 273 */     if (getPattern() == null) {
/* 274 */       return super.toString();
/*     */     }
/* 276 */     return getPattern();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasWildcard()
/*     */   {
/* 286 */     if (getPattern() == null) {
/* 287 */       return false;
/*     */     }
/* 289 */     if (hasDigitWildcard())
/*     */     {
/* 291 */       if (getPattern().indexOf(digitWildcardChar()) >= 0) {
/* 292 */         return true;
/*     */       }
/*     */     }
/* 295 */     return (getPattern().indexOf("*") >= 0) || (getPattern().indexOf('?') >= 0);
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
/*     */   public void setDigitWildcardChar(char digitWildcard)
/*     */   {
/* 309 */     if (digitWildcard <= 0)
/*     */     {
/* 311 */       digitWildcard(null);
/*     */     }
/*     */     else
/*     */     {
/* 315 */       digitWildcard(new Character(digitWildcard));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean hasDigitWildcard()
/*     */   {
/* 326 */     return digitWildcard() != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected char digitWildcardChar()
/*     */   {
/* 333 */     if (hasDigitWildcard()) {
/* 334 */       return digitWildcard().charValue();
/*     */     }
/* 336 */     return '\000';
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected char skipWildcards(StringExaminer iterator)
/*     */   {
/* 347 */     char result = '-';
/*     */     
/*     */     do
/*     */     {
/* 351 */       result = iterator.nextChar();
/*     */     }
/* 353 */     while ((result == '*') || (result == '?'));
/* 354 */     return result;
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
/*     */   protected boolean skipAfter(StringExaminer examiner, String matchString)
/*     */   {
/* 372 */     char ch = '-';
/* 373 */     char matchChar = ' ';
/* 374 */     boolean found = false;
/* 375 */     int index = 0;
/*     */     
/* 377 */     if ((matchString == null) || (matchString.length() == 0)) {
/* 378 */       return false;
/*     */     }
/* 380 */     ch = examiner.nextChar();
/* 381 */     while ((examiner.endNotReached(ch)) && (!found))
/*     */     {
/* 383 */       matchChar = matchString.charAt(index);
/* 384 */       if (charsAreEqual(ch, matchChar))
/*     */       {
/* 386 */         index++;
/* 387 */         if (index >= matchString.length())
/*     */         {
/* 389 */           found = true;
/*     */         }
/*     */         else
/*     */         {
/* 393 */           ch = examiner.nextChar();
/*     */         }
/*     */         
/*     */ 
/*     */       }
/* 398 */       else if (index == 0)
/*     */       {
/* 400 */         ch = examiner.nextChar();
/*     */       }
/*     */       else
/*     */       {
/* 404 */         index = 0;
/*     */       }
/*     */     }
/*     */     
/* 408 */     return found;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String upToEnd(StringExaminer iterator)
/*     */   {
/* 415 */     return iterator.upToEnd();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean matchReverse(String pattern, StringExaminer probeIterator)
/*     */   {
/* 427 */     String newPattern = "*" + pattern;
/* 428 */     String newProbe = upToEnd(probeIterator);
/* 429 */     newPattern = strUtil().reverse(newPattern);
/* 430 */     newProbe = strUtil().reverse(newProbe);
/* 431 */     StringPattern newMatcher = new StringPattern(newPattern, getIgnoreCase());
/* 432 */     if (hasDigitWildcard()) {
/* 433 */       newMatcher.setDigitWildcardChar(digitWildcardChar());
/*     */     }
/* 435 */     return newMatcher.matches(newProbe);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean charsAreEqual(char probeChar, char patternChar)
/*     */   {
/* 442 */     if (hasDigitWildcard())
/*     */     {
/* 444 */       if (patternChar == digitWildcardChar())
/*     */       {
/* 446 */         return Character.isDigit(probeChar);
/*     */       }
/*     */     }
/*     */     
/* 450 */     if (getIgnoreCase())
/*     */     {
/* 452 */       return Character.toUpperCase(probeChar) == Character.toUpperCase(patternChar);
/*     */     }
/*     */     
/*     */ 
/* 456 */     return probeChar == patternChar;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean endReached(char character)
/*     */   {
/* 464 */     return character == 65535;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean endNotReached(char character)
/*     */   {
/* 471 */     return !endReached(character);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected char getPatternChar(StringExaminer patternIterator, char probeCh)
/*     */   {
/* 480 */     char patternCh = patternIterator.nextChar();
/*     */     
/* 482 */     return patternCh == '?' ? probeCh : patternCh;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected StringExaminer newExaminer(String str)
/*     */   {
/* 489 */     return new StringExaminer(str, getIgnoreCase());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected StringUtil strUtil()
/*     */   {
/* 496 */     return StringUtil.current();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/pf/text/StringPattern.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */