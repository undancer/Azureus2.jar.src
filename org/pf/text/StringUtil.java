/*      */ package org.pf.text;
/*      */ 
/*      */ import java.io.PrintWriter;
/*      */ import java.io.StringWriter;
/*      */ import java.text.StringCharacterIterator;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Hashtable;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.StringTokenizer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class StringUtil
/*      */ {
/*      */   public static final char CH_SPACE = ' ';
/*      */   public static final char CH_NEWLINE = '\n';
/*      */   public static final char CH_CR = '\r';
/*      */   public static final char CH_TAB = '\t';
/*      */   public static final String STR_SPACE = " ";
/*      */   public static final String STR_NEWLINE = "\n";
/*      */   public static final String STR_CR = "\r";
/*      */   public static final String STR_TAB = "\t";
/*      */   private static final String WORD_DELIM = " \t\n\r";
/*   77 */   private static StringUtil singleton = null;
/*   78 */   private static StringUtil getSingleton() { return singleton; }
/*   79 */   private static void setSingleton(StringUtil inst) { singleton = inst; }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static StringUtil current()
/*      */   {
/*   89 */     if (getSingleton() == null)
/*   90 */       setSingleton(new StringUtil());
/*   91 */     return getSingleton();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String replaceAll(String sourceStr, String oldSubStr, String newSubStr)
/*      */   {
/*  110 */     String part = null;
/*  111 */     String result = "";
/*  112 */     int index = -1;
/*  113 */     int subLen = 0;
/*      */     
/*  115 */     subLen = oldSubStr.length();
/*  116 */     part = sourceStr;
/*  117 */     while ((part.length() > 0) && (subLen > 0))
/*      */     {
/*  119 */       index = part.indexOf(oldSubStr);
/*  120 */       if (index >= 0)
/*      */       {
/*  122 */         result = result + part.substring(0, index) + newSubStr;
/*  123 */         part = part.substring(index + subLen);
/*      */       }
/*      */       else
/*      */       {
/*  127 */         result = result + part;
/*  128 */         part = "";
/*      */       }
/*      */     }
/*      */     
/*  132 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String repeat(char ch, int count)
/*      */   {
/*  146 */     StringBuffer buffer = null;
/*      */     
/*  148 */     buffer = new StringBuffer(count);
/*  149 */     for (int i = 1; i <= count; i++)
/*      */     {
/*  151 */       buffer.append(ch);
/*      */     }
/*      */     
/*  154 */     return buffer.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] words(String text)
/*      */   {
/*  170 */     return parts(text, " \t\n\r");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] parts(String text, String delimiters)
/*      */   {
/*  192 */     return parts(text, delimiters, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] allParts(String text, String delimiters)
/*      */   {
/*  221 */     return parts(text, delimiters, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] substrings(String text, String separator)
/*      */   {
/*  245 */     return substrings(text, separator, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] allSubstrings(String text, String separator)
/*      */   {
/*  269 */     return substrings(text, separator, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getDelimitedSubstring(String text, String startDelimiter, String endDelimiter)
/*      */   {
/*  293 */     String subStr = "";
/*      */     
/*  295 */     if ((text != null) && (startDelimiter != null) && (endDelimiter != null))
/*      */     {
/*      */ 
/*  298 */       int start = text.indexOf(startDelimiter);
/*  299 */       if (start >= 0)
/*      */       {
/*  301 */         int stop = text.indexOf(endDelimiter, start + 1);
/*  302 */         if (stop > start) {
/*  303 */           subStr = text.substring(start + 1, stop);
/*      */         }
/*      */       }
/*      */     }
/*  307 */     return subStr;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getDelimitedSubstring(String text, String delimiter)
/*      */   {
/*  326 */     return getDelimitedSubstring(text, delimiter, delimiter);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String stackTrace(Throwable throwable)
/*      */   {
/*  337 */     StringWriter sw = new StringWriter();
/*  338 */     PrintWriter pw = new PrintWriter(sw);
/*  339 */     throwable.printStackTrace(pw);
/*  340 */     pw.close();
/*  341 */     return sw.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String leftPadCh(String str, int len, char ch)
/*      */   {
/*  353 */     return padCh(str, len, ch, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String leftPad(String str, int len)
/*      */   {
/*  368 */     return leftPadCh(str, len, ' ');
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String leftPadCh(int value, int len, char fillChar)
/*      */   {
/*  380 */     return leftPadCh(Integer.toString(value), len, fillChar);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String leftPad(int value, int len)
/*      */   {
/*  392 */     return leftPadCh(value, len, '0');
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String rightPadCh(String str, int len, char ch)
/*      */   {
/*  404 */     return padCh(str, len, ch, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String rightPad(String str, int len)
/*      */   {
/*  416 */     return rightPadCh(str, len, ' ');
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String rightPadCh(int value, int len, char fillChar)
/*      */   {
/*  428 */     return rightPadCh(Integer.toString(value), len, fillChar);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String rightPad(int value, int len)
/*      */   {
/*  440 */     return rightPadCh(value, len, ' ');
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String centerCh(String str, int len, char ch)
/*      */   {
/*  453 */     String buffer = null;
/*  454 */     int missing = len - str.length();
/*  455 */     int half = 0;
/*      */     
/*  457 */     if (missing <= 0) {
/*  458 */       return str;
/*      */     }
/*  460 */     half = missing / 2;
/*  461 */     buffer = rightPadCh(str, len - half, ch);
/*  462 */     return leftPadCh(buffer, len, ch);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String center(String str, int len)
/*      */   {
/*  474 */     return centerCh(str, len, ' ');
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] append(String[] strings, String string)
/*      */   {
/*  485 */     String[] appStr = { string };
/*  486 */     return append(strings, appStr);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] append(String[] strings, String[] appendStrings)
/*      */   {
/*  501 */     String[] newStrings = null;
/*      */     
/*  503 */     if (strings == null) {
/*  504 */       return appendStrings;
/*      */     }
/*  506 */     if (appendStrings == null) {
/*  507 */       return strings;
/*      */     }
/*  509 */     newStrings = new String[strings.length + appendStrings.length];
/*  510 */     System.arraycopy(strings, 0, newStrings, 0, strings.length);
/*  511 */     System.arraycopy(appendStrings, 0, newStrings, strings.length, appendStrings.length);
/*      */     
/*  513 */     return newStrings;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] appendIfNotThere(String[] strings, String appendString)
/*      */   {
/*  525 */     if (contains(strings, appendString)) {
/*  526 */       return strings;
/*      */     }
/*  528 */     return append(strings, appendString);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] appendIfNotThere(String[] strings, String[] appendStrings)
/*      */   {
/*  540 */     String[] newStrings = strings;
/*      */     
/*  542 */     if (appendStrings == null) {
/*  543 */       return newStrings;
/*      */     }
/*  545 */     for (int i = 0; i < appendStrings.length; i++)
/*      */     {
/*  547 */       newStrings = appendIfNotThere(newStrings, appendStrings[i]);
/*      */     }
/*  549 */     return newStrings;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] remove(String[] strings, String[] removeStrings)
/*      */   {
/*  564 */     if ((strings == null) || (removeStrings == null) || (strings.length == 0) || (removeStrings.length == 0))
/*      */     {
/*      */ 
/*  567 */       return strings;
/*      */     }
/*      */     
/*  570 */     return removeFromStringArray(strings, removeStrings);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] remove(String[] strings, String removeString)
/*      */   {
/*  585 */     String[] removeStrings = { removeString };
/*      */     
/*  587 */     return remove(strings, removeStrings);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] removeNull(String[] strings)
/*      */   {
/*  601 */     if (strings == null) {
/*  602 */       return strings;
/*      */     }
/*  604 */     return removeFromStringArray(strings, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String asString(String[] strings, String separator)
/*      */   {
/*  619 */     StringBuffer buffer = null;
/*      */     
/*  621 */     buffer = new StringBuffer(strings.length * 20);
/*  622 */     if (strings.length > 0)
/*      */     {
/*  624 */       buffer.append(strings[0].toString());
/*  625 */       for (int i = 1; i < strings.length; i++)
/*      */       {
/*  627 */         buffer.append(separator);
/*  628 */         if (strings[i] != null)
/*  629 */           buffer.append(strings[i]);
/*      */       }
/*      */     }
/*  632 */     return buffer.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String asString(String[] strings)
/*      */   {
/*  646 */     return asString(strings, ",");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int indexOf(String[] strArray, StringPattern pattern)
/*      */   {
/*  662 */     if ((strArray == null) || (strArray.length == 0)) {
/*  663 */       return -1;
/*      */     }
/*  665 */     boolean found = false;
/*  666 */     for (int i = 0; i < strArray.length; i++)
/*      */     {
/*  668 */       if (strArray[i] == null)
/*      */       {
/*  670 */         if (pattern == null) {
/*  671 */           found = true;
/*      */         }
/*      */         
/*      */       }
/*  675 */       else if (pattern != null) {
/*  676 */         found = pattern.matches(strArray[i]);
/*      */       }
/*  678 */       if (found)
/*  679 */         return i;
/*      */     }
/*  681 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int indexOf(String[] strArray, String searchStr)
/*      */   {
/*  698 */     return indexOfString(strArray, searchStr, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int indexOfIgnoreCase(String[] strArray, String searchStr)
/*      */   {
/*  715 */     return indexOfString(strArray, searchStr, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean contains(String[] strArray, String searchStr, boolean ignoreCase)
/*      */   {
/*  731 */     if (ignoreCase) {
/*  732 */       return containsIgnoreCase(strArray, searchStr);
/*      */     }
/*  734 */     return contains(strArray, searchStr);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean contains(String[] strArray, StringPattern pattern)
/*      */   {
/*  749 */     return indexOf(strArray, pattern) >= 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean contains(String[] strArray, String searchStr)
/*      */   {
/*  765 */     return indexOf(strArray, searchStr) >= 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean containsIgnoreCase(String[] strArray, String searchStr)
/*      */   {
/*  781 */     return indexOfIgnoreCase(strArray, searchStr) >= 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] copyFrom(String[] from, int start)
/*      */   {
/*  797 */     if (from == null) {
/*  798 */       return null;
/*      */     }
/*  800 */     return copyFrom(from, start, from.length - 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] copyFrom(String[] from, int start, int end)
/*      */   {
/*  821 */     int stop = end;
/*      */     
/*  823 */     if (from == null) {
/*  824 */       return null;
/*      */     }
/*  826 */     if (stop > from.length - 1) {
/*  827 */       stop = from.length - 1;
/*      */     }
/*  829 */     int count = stop - start + 1;
/*      */     
/*  831 */     if (count < 1) {
/*  832 */       return new String[0];
/*      */     }
/*  834 */     String[] result = new String[count];
/*      */     
/*  836 */     System.arraycopy(from, start, result, 0, count);
/*      */     
/*  838 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String cutTail(String text, String separator)
/*      */   {
/*  868 */     if ((text == null) || (separator == null)) {
/*  869 */       return text;
/*      */     }
/*  871 */     int index = text.lastIndexOf(separator);
/*  872 */     if (index < 0) {
/*  873 */       return text;
/*      */     }
/*  875 */     return text.substring(0, index);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String cutHead(String text, String separator)
/*      */   {
/*  905 */     if ((text == null) || (separator == null)) {
/*  906 */       return text;
/*      */     }
/*  908 */     int index = text.lastIndexOf(separator);
/*  909 */     if (index < 0) {
/*  910 */       return text;
/*      */     }
/*  912 */     return text.substring(index + 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] splitNameValue(String str, String separator)
/*      */   {
/*  931 */     String[] result = { "", "" };
/*      */     
/*      */ 
/*  934 */     if (str != null)
/*      */     {
/*  936 */       int index = str.indexOf(separator);
/*  937 */       if (index > 0)
/*      */       {
/*  939 */         result[0] = str.substring(0, index);
/*  940 */         result[1] = str.substring(index + separator.length());
/*      */       }
/*      */       else
/*      */       {
/*  944 */         result[0] = str;
/*      */       }
/*      */     }
/*      */     
/*  948 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String prefix(String str, String separator)
/*      */   {
/*  975 */     return prefix(str, separator, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String suffix(String str, String separator)
/*      */   {
/* 1002 */     return suffix(str, separator, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String upTo(String str, String separator)
/*      */   {
/* 1032 */     return prefix(str, separator, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String startingFrom(String str, String separator)
/*      */   {
/* 1059 */     return suffix(str, separator, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String reverse(String str)
/*      */   {
/* 1070 */     if (str == null) {
/* 1071 */       return null;
/*      */     }
/* 1073 */     char[] newStr = new char[str.length()];
/* 1074 */     StringCharacterIterator iterator = new StringCharacterIterator(str);
/* 1075 */     int i = 0;
/*      */     
/* 1077 */     for (char ch = iterator.last(); ch != 65535; ch = iterator.previous())
/*      */     {
/* 1079 */       newStr[i] = ch;
/* 1080 */       i++;
/*      */     }
/* 1082 */     return new String(newStr);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map toMap(String str, String elementSeparator, String keyValueSeparator, Map map)
/*      */   {
/* 1118 */     if (str == null) {
/* 1119 */       return map;
/*      */     }
/* 1121 */     Map result = map == null ? new Hashtable() : map;
/* 1122 */     String elemSep = elementSeparator == null ? "," : elementSeparator;
/* 1123 */     String kvSep = keyValueSeparator == null ? "=" : keyValueSeparator;
/*      */     
/* 1125 */     String[] assignments = parts(str, elemSep);
/* 1126 */     for (int i = 0; i < assignments.length; i++)
/*      */     {
/* 1128 */       String[] nameValue = splitNameValue(assignments[i], kvSep);
/* 1129 */       nameValue[0] = nameValue[0].trim();
/* 1130 */       nameValue[1] = nameValue[1].trim();
/* 1131 */       if (nameValue[0].length() > 0) {
/* 1132 */         result.put(nameValue[0], nameValue[1]);
/*      */       }
/*      */     }
/* 1135 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map asMap(String str)
/*      */   {
/* 1157 */     return toMap(str, null, null, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map asMap(String str, String elementSeparator)
/*      */   {
/* 1176 */     return toMap(str, elementSeparator, null, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map asMap(String str, String elementSeparator, String keyValueSeparator)
/*      */   {
/* 1195 */     return toMap(str, elementSeparator, keyValueSeparator, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map toMap(String str, String elementSeparator, Map map)
/*      */   {
/* 1215 */     return toMap(str, elementSeparator, null, map);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map toMap(String str, Map map)
/*      */   {
/* 1234 */     return toMap(str, null, null, map);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Properties asProperties(String str)
/*      */   {
/* 1252 */     return toProperties(str, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Properties toProperties(String str, Properties properties)
/*      */   {
/* 1271 */     Properties props = properties == null ? new Properties() : properties;
/* 1272 */     return (Properties)toMap(str, null, null, props);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String trimSeparator(String text, String separator)
/*      */   {
/* 1285 */     int sepLen = separator.length();
/*      */     
/* 1287 */     while (text.startsWith(separator)) {
/* 1288 */       text = text.substring(separator.length());
/*      */     }
/* 1290 */     while (text.endsWith(separator)) {
/* 1291 */       text = text.substring(0, text.length() - sepLen);
/*      */     }
/* 1293 */     return text;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String[] parts(String text, String delimiters, boolean all)
/*      */   {
/* 1310 */     ArrayList result = null;
/* 1311 */     StringTokenizer tokenizer = null;
/*      */     
/* 1313 */     if (text == null) {
/* 1314 */       return null;
/*      */     }
/* 1316 */     if ((delimiters == null) || (delimiters.length() == 0))
/*      */     {
/* 1318 */       String[] resultArray = { text };
/* 1319 */       return resultArray;
/*      */     }
/*      */     
/* 1322 */     if (text.length() == 0)
/*      */     {
/* 1324 */       return new String[0];
/*      */     }
/*      */     
/*      */ 
/* 1328 */     result = new ArrayList();
/* 1329 */     tokenizer = new StringTokenizer(text, delimiters, all);
/*      */     
/* 1331 */     if (all) {
/* 1332 */       collectParts(result, tokenizer, delimiters);
/*      */     } else {
/* 1334 */       collectParts(result, tokenizer);
/*      */     }
/* 1336 */     return (String[])result.toArray(new String[0]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void collectParts(List list, StringTokenizer tokenizer)
/*      */   {
/* 1343 */     while (tokenizer.hasMoreTokens())
/*      */     {
/* 1345 */       list.add(tokenizer.nextToken());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void collectParts(List list, StringTokenizer tokenizer, String delimiter)
/*      */   {
/* 1354 */     boolean lastWasDelimiter = false;
/*      */     
/* 1356 */     while (tokenizer.hasMoreTokens())
/*      */     {
/* 1358 */       String token = tokenizer.nextToken();
/* 1359 */       if (delimiter.indexOf(token) >= 0)
/*      */       {
/* 1361 */         if (lastWasDelimiter)
/* 1362 */           list.add("");
/* 1363 */         lastWasDelimiter = true;
/*      */       }
/*      */       else
/*      */       {
/* 1367 */         list.add(token);
/* 1368 */         lastWasDelimiter = false;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String[] substrings(String text, String separator, boolean all)
/*      */   {
/* 1393 */     int index = 0;
/* 1394 */     int start = 0;
/* 1395 */     int sepLen = 0;
/* 1396 */     int strLen = 0;
/* 1397 */     String str = text;
/* 1398 */     ArrayList strings = new ArrayList();
/*      */     
/* 1400 */     if (text == null) {
/* 1401 */       return new String[0];
/*      */     }
/* 1403 */     if ((separator == null) || (separator.length() == 0))
/*      */     {
/* 1405 */       if (text.length() == 0) {
/* 1406 */         return new String[0];
/*      */       }
/* 1408 */       String[] resultArray = { text };
/* 1409 */       return resultArray;
/*      */     }
/*      */     
/* 1412 */     if (!all) {
/* 1413 */       str = trimSeparator(text, separator);
/*      */     }
/* 1415 */     strLen = str.length();
/* 1416 */     if (strLen > 0)
/*      */     {
/* 1418 */       sepLen = separator.length();
/*      */       
/* 1420 */       index = str.indexOf(separator, start);
/* 1421 */       while (index >= 0)
/*      */       {
/* 1423 */         if (all)
/*      */         {
/* 1425 */           if (index > 0)
/*      */           {
/* 1427 */             strings.add(str.substring(start, index));
/*      */           }
/*      */           
/*      */ 
/*      */         }
/* 1432 */         else if (index > start + sepLen) {
/* 1433 */           strings.add(str.substring(start, index));
/*      */         }
/* 1435 */         start = index + sepLen;
/* 1436 */         index = str.indexOf(separator, start);
/*      */       }
/*      */       
/* 1439 */       if (start < strLen)
/* 1440 */         strings.add(str.substring(start));
/*      */     }
/* 1442 */     return (String[])strings.toArray(new String[0]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String padCh(String str, int len, char ch, boolean left)
/*      */   {
/* 1449 */     StringBuffer buffer = null;
/* 1450 */     int missing = len - str.length();
/*      */     
/* 1452 */     if (missing <= 0) {
/* 1453 */       return str;
/*      */     }
/* 1455 */     buffer = new StringBuffer(len);
/* 1456 */     if (!left)
/* 1457 */       buffer.append(str);
/* 1458 */     for (int i = 1; i <= missing; i++)
/* 1459 */       buffer.append(ch);
/* 1460 */     if (left)
/* 1461 */       buffer.append(str);
/* 1462 */     return buffer.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected int indexOfString(String[] strArray, String searchStr, boolean ignoreCase)
/*      */   {
/* 1469 */     if ((strArray == null) || (strArray.length == 0)) {
/* 1470 */       return -1;
/*      */     }
/* 1472 */     boolean found = false;
/* 1473 */     for (int i = 0; i < strArray.length; i++)
/*      */     {
/* 1475 */       if (strArray[i] == null)
/*      */       {
/* 1477 */         if (searchStr == null) {
/* 1478 */           found = true;
/*      */         }
/*      */         
/*      */       }
/* 1482 */       else if (ignoreCase) {
/* 1483 */         found = strArray[i].equalsIgnoreCase(searchStr);
/*      */       } else {
/* 1485 */         found = strArray[i].equals(searchStr);
/*      */       }
/* 1487 */       if (found)
/* 1488 */         return i;
/*      */     }
/* 1490 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String prefix(String str, String separator, boolean returnNull)
/*      */   {
/* 1508 */     if (str == null) {
/* 1509 */       return null;
/*      */     }
/* 1511 */     if (separator == null) {
/* 1512 */       return returnNull ? null : str;
/*      */     }
/* 1514 */     int index = str.indexOf(separator);
/* 1515 */     if (index >= 0) {
/* 1516 */       return str.substring(0, index);
/*      */     }
/* 1518 */     return returnNull ? null : str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String suffix(String str, String separator, boolean returnNull)
/*      */   {
/* 1536 */     if (str == null) {
/* 1537 */       return null;
/*      */     }
/* 1539 */     if (separator == null) {
/* 1540 */       return returnNull ? null : str;
/*      */     }
/* 1542 */     int index = str.indexOf(separator);
/* 1543 */     if (index >= 0) {
/* 1544 */       return str.substring(index + separator.length());
/*      */     }
/* 1546 */     return returnNull ? null : str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String[] removeFromStringArray(String[] strings, String[] removeStrings)
/*      */   {
/* 1561 */     List list = new ArrayList(strings.length);
/* 1562 */     for (int i = 0; i < strings.length; i++) { boolean remains;
/*      */       boolean remains;
/* 1564 */       if (removeStrings == null)
/*      */       {
/* 1566 */         remains = strings[i] != null;
/*      */       }
/*      */       else
/*      */       {
/* 1570 */         remains = !contains(removeStrings, strings[i]);
/*      */       }
/* 1572 */       if (remains)
/*      */       {
/* 1574 */         list.add(strings[i]);
/*      */       }
/*      */     }
/* 1577 */     return (String[])list.toArray(new String[list.size()]);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/pf/text/StringUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */