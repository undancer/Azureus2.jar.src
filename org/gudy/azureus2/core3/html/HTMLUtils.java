/*     */ package org.gudy.azureus2.core3.html;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.xml.util.XUXmlWriter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HTMLUtils
/*     */ {
/*     */   public static List convertHTMLToText(String indent, String text)
/*     */   {
/*  50 */     int pos = 0;
/*     */     
/*  52 */     text = text.replaceAll("<ol>", "");
/*  53 */     text = text.replaceAll("</ol>", "");
/*  54 */     text = text.replaceAll("<ul>", "");
/*  55 */     text = text.replaceAll("</ul>", "");
/*  56 */     text = text.replaceAll("</li>", "");
/*  57 */     text = text.replaceAll("<li>", "\n\t*");
/*     */     
/*  59 */     String lc_text = text.toLowerCase(MessageText.LOCALE_ENGLISH);
/*     */     
/*  61 */     List lines = new ArrayList();
/*     */     
/*     */ 
/*     */ 
/*     */     for (;;)
/*     */     {
/*  67 */       String[] tokens = { "<br>", "<p>" };
/*     */       
/*  69 */       String token = null;
/*  70 */       int p1 = -1;
/*     */       
/*  72 */       for (int i = 0; i < tokens.length; i++)
/*     */       {
/*  74 */         int x = lc_text.indexOf(tokens[i], pos);
/*     */         
/*  76 */         if ((x != -1) && (
/*  77 */           (p1 == -1) || (x < p1))) {
/*  78 */           token = tokens[i];
/*  79 */           p1 = x;
/*     */         }
/*     */       }
/*     */       String line;
/*     */       String line;
/*  84 */       if (p1 == -1)
/*     */       {
/*  86 */         line = text.substring(pos);
/*     */       }
/*     */       else
/*     */       {
/*  90 */         line = text.substring(pos, p1);
/*     */         
/*  92 */         pos = p1 + token.length();
/*     */       }
/*     */       
/*  95 */       lines.add(indent + line);
/*     */       
/*  97 */       if (p1 == -1) {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 103 */     return lines;
/*     */   }
/*     */   
/*     */   public static String convertListToString(List list)
/*     */   {
/* 108 */     StringBuilder result = new StringBuilder();
/* 109 */     String separator = "";
/* 110 */     Iterator iter = list.iterator();
/* 111 */     while (iter.hasNext()) {
/* 112 */       String line = iter.next().toString();
/* 113 */       result.append(separator);
/* 114 */       result.append(line);
/* 115 */       separator = "\n";
/*     */     }
/*     */     
/* 118 */     return result.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String convertHTMLToText2(String content)
/*     */   {
/* 125 */     int pos = 0;
/*     */     
/* 127 */     String res = "";
/*     */     
/* 129 */     content = removeTagPairs(content, "script");
/*     */     
/* 131 */     content = content.replaceAll("&nbsp;", " ");
/*     */     
/* 133 */     content = content.replaceAll("[\\s]+", " ");
/*     */     
/*     */     for (;;)
/*     */     {
/* 137 */       int p1 = content.indexOf("<", pos);
/*     */       
/* 139 */       if (p1 == -1)
/*     */       {
/* 141 */         res = res + content.substring(pos);
/*     */         
/* 143 */         break;
/*     */       }
/*     */       
/* 146 */       int p2 = content.indexOf(">", p1);
/*     */       
/* 148 */       if (p2 == -1)
/*     */       {
/* 150 */         res = res + content.substring(pos);
/*     */         
/* 152 */         break;
/*     */       }
/*     */       
/* 155 */       String tag = content.substring(p1 + 1, p2).toLowerCase(MessageText.LOCALE_ENGLISH);
/*     */       
/* 157 */       res = res + content.substring(pos, p1);
/*     */       
/* 159 */       if ((tag.equals("p")) || (tag.equals("br")))
/*     */       {
/* 161 */         if ((res.length() > 0) && (res.charAt(res.length() - 1) != '\n'))
/*     */         {
/* 163 */           res = res + "\n";
/*     */         }
/*     */       }
/*     */       
/* 167 */       pos = p2 + 1;
/*     */     }
/*     */     
/* 170 */     res = res.replaceAll("[ \\t\\x0B\\f\\r]+", " ");
/* 171 */     res = res.replaceAll("[ \\t\\x0B\\f\\r]+\\n", "\n");
/* 172 */     res = res.replaceAll("\\n[ \\t\\x0B\\f\\r]+", "\n");
/*     */     
/* 174 */     if ((res.length() > 0) && (Character.isWhitespace(res.charAt(0))))
/*     */     {
/* 176 */       res = res.substring(1);
/*     */     }
/*     */     
/* 179 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String splitWithLineLength(String str, int length)
/*     */   {
/* 187 */     String res = "";
/*     */     
/* 189 */     StringTokenizer tok = new StringTokenizer(str, "\n");
/*     */     
/* 191 */     while (tok.hasMoreTokens())
/*     */     {
/* 193 */       String line = tok.nextToken();
/*     */       
/* 195 */       while (line.length() > length)
/*     */       {
/* 197 */         if (res.length() > 0)
/*     */         {
/* 199 */           res = res + "\n";
/*     */         }
/*     */         
/* 202 */         boolean done = false;
/*     */         
/* 204 */         for (int i = length - 1; i >= 0; i--)
/*     */         {
/* 206 */           if (Character.isWhitespace(line.charAt(i)))
/*     */           {
/* 208 */             done = true;
/*     */             
/* 210 */             res = res + line.substring(0, i);
/*     */             
/* 212 */             line = line.substring(i + 1);
/*     */             
/* 214 */             break;
/*     */           }
/*     */         }
/*     */         
/* 218 */         if (!done)
/*     */         {
/* 220 */           res = res + line.substring(0, length);
/*     */           
/* 222 */           line = line.substring(length);
/*     */         }
/*     */       }
/*     */       
/* 226 */       if ((res.length() > 0) && (line.length() > 0))
/*     */       {
/* 228 */         res = res + "\n";
/*     */         
/* 230 */         res = res + line;
/*     */       }
/*     */     }
/*     */     
/* 234 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String removeTagPairs(String content, String tag_name)
/*     */   {
/* 242 */     tag_name = tag_name.toLowerCase(MessageText.LOCALE_ENGLISH);
/*     */     
/* 244 */     String lc_content = content.toLowerCase(MessageText.LOCALE_ENGLISH);
/*     */     
/* 246 */     int pos = 0;
/*     */     
/* 248 */     String res = "";
/*     */     
/* 250 */     int level = 0;
/* 251 */     int start_pos = -1;
/*     */     
/*     */     for (;;)
/*     */     {
/* 255 */       int start_tag_start = lc_content.indexOf("<" + tag_name, pos);
/* 256 */       int end_tag_start = lc_content.indexOf("</" + tag_name, pos);
/*     */       
/* 258 */       if (level == 0)
/*     */       {
/* 260 */         if (start_tag_start == -1)
/*     */         {
/* 262 */           res = res + content.substring(pos);
/*     */           
/* 264 */           break;
/*     */         }
/*     */         
/* 267 */         res = res + content.substring(pos, start_tag_start);
/*     */         
/* 269 */         start_pos = start_tag_start;
/*     */         
/* 271 */         level = 1;
/*     */         
/* 273 */         pos = start_pos + 1;
/*     */       }
/*     */       else
/*     */       {
/* 277 */         if (end_tag_start == -1)
/*     */         {
/* 279 */           res = res + content.substring(pos);
/*     */           
/* 281 */           break;
/*     */         }
/*     */         
/* 284 */         if ((start_tag_start == -1) || (end_tag_start < start_tag_start))
/*     */         {
/* 286 */           level--;
/*     */           
/* 288 */           int end_end = lc_content.indexOf('>', end_tag_start);
/*     */           
/* 290 */           if (end_end == -1) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/* 295 */           pos = end_end + 1;
/*     */         }
/*     */         else
/*     */         {
/* 299 */           level++;
/*     */           
/* 301 */           pos = start_tag_start + 1;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 306 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Object[] getLinks(String content_in)
/*     */   {
/* 313 */     int pos = 0;
/*     */     
/* 315 */     List urls = new ArrayList();
/*     */     
/* 317 */     String content_out = "";
/*     */     
/* 319 */     String current_url = null;
/* 320 */     int current_url_start = -1;
/*     */     
/*     */     for (;;)
/*     */     {
/* 324 */       int p1 = content_in.indexOf("<", pos);
/*     */       
/* 326 */       if (p1 == -1) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/* 331 */       p1++;
/*     */       
/* 333 */       int p2 = content_in.indexOf(">", p1);
/*     */       
/* 335 */       if (p2 == -1) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/* 340 */       if (p1 > pos)
/*     */       {
/* 342 */         content_out = content_out + content_in.substring(pos, p1 - 1);
/*     */       }
/*     */       
/* 345 */       pos = p2 + 1;
/*     */       
/* 347 */       String tag = content_in.substring(p1, p2).trim();
/*     */       
/* 349 */       String lc_tag = tag.toLowerCase(MessageText.LOCALE_ENGLISH);
/*     */       
/* 351 */       if (lc_tag.startsWith("a "))
/*     */       {
/* 353 */         int hr_start = lc_tag.indexOf("href");
/*     */         
/* 355 */         if (hr_start != -1)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 360 */           hr_start = lc_tag.indexOf("=", hr_start);
/*     */           
/* 362 */           if (hr_start != -1)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 367 */             hr_start++;
/*     */             
/* 369 */             while ((hr_start < lc_tag.length()) && (Character.isWhitespace(lc_tag.charAt(hr_start))))
/*     */             {
/*     */ 
/* 372 */               hr_start++;
/*     */             }
/*     */             
/* 375 */             int hr_end = lc_tag.length() - 1;
/*     */             
/* 377 */             while ((hr_end >= lc_tag.length()) && (Character.isWhitespace(lc_tag.charAt(hr_end))))
/*     */             {
/*     */ 
/* 380 */               hr_end--;
/*     */             }
/*     */             
/* 383 */             String href = tag.substring(hr_start, hr_end + 1).trim();
/*     */             
/* 385 */             if (href.startsWith("\""))
/*     */             {
/* 387 */               int endQuotePos = href.indexOf('"', 1);
/* 388 */               if (endQuotePos == -1) {
/* 389 */                 href = href.substring(1, href.length() - 1);
/*     */               } else {
/* 391 */                 href = href.substring(1, endQuotePos);
/*     */               }
/*     */             }
/*     */             
/* 395 */             current_url = href;
/*     */             
/* 397 */             current_url_start = content_out.length();
/*     */           }
/* 399 */         } } else if ((lc_tag.startsWith("/")) && (lc_tag.substring(1).trim().equals("a")))
/*     */       {
/* 401 */         if (current_url != null)
/*     */         {
/* 403 */           int len = content_out.length() - current_url_start;
/*     */           
/* 405 */           urls.add(new Object[] { current_url, { current_url_start, len } });
/*     */         }
/*     */         
/* 408 */         current_url = null;
/*     */       }
/*     */     }
/*     */     
/* 412 */     if (pos < content_in.length())
/*     */     {
/* 414 */       content_out = content_out + content_in.substring(pos);
/*     */     }
/*     */     
/* 417 */     return new Object[] { content_out, urls };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String expand(String str)
/*     */   {
/* 424 */     str = XUXmlWriter.unescapeXML(str);
/*     */     
/* 426 */     str = str.replaceAll("&nbsp;", " ");
/*     */     
/* 428 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 435 */     Object[] obj = getLinks("aaaaaaa <a href=\"http://here/parp  \">link< / a > prute <a href=\"http://here/pa\">klink</a>");
/*     */     
/* 437 */     System.out.println(obj[0]);
/*     */     
/* 439 */     List urls = (List)obj[1];
/*     */     
/* 441 */     for (int i = 0; i < urls.size(); i++)
/*     */     {
/* 443 */       Object[] entry = (Object[])urls.get(i);
/*     */       
/* 445 */       System.out.println("    " + entry[0] + ((int[])(int[])entry[1])[0] + "," + ((int[])(int[])entry[1])[1]);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/html/HTMLUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */