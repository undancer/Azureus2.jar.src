/*     */ package org.gudy.azureus2.core3.xml.util;
/*     */ 
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.io.Writer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class XUXmlWriter
/*     */ {
/*     */   private static final int INDENT_AMOUNT = 4;
/*     */   private String current_indent_string;
/*     */   private PrintWriter writer;
/*     */   private boolean generic_simple;
/*     */   
/*     */   protected XUXmlWriter()
/*     */   {
/*  52 */     resetIndent();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected XUXmlWriter(OutputStream _output_stream)
/*     */   {
/*  59 */     setOutputStream(_output_stream);
/*     */     
/*  61 */     resetIndent();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setOutputStream(OutputStream _output_stream)
/*     */   {
/*     */     try
/*     */     {
/*  70 */       this.writer = new PrintWriter(new OutputStreamWriter(_output_stream, "UTF8"));
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/*  74 */       Debug.printStackTrace(e);
/*     */       
/*  76 */       this.writer = new PrintWriter(_output_stream);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setOutputWriter(Writer _writer)
/*     */   {
/*  84 */     if ((_writer instanceof PrintWriter))
/*     */     {
/*  86 */       this.writer = ((PrintWriter)_writer);
/*     */     }
/*     */     else
/*     */     {
/*  90 */       this.writer = new PrintWriter(_writer);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setGenericSimple(boolean simple)
/*     */   {
/*  98 */     this.generic_simple = simple;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writeTag(String tag, String content)
/*     */   {
/* 106 */     writeLineRaw("<" + tag + ">" + escapeXML(content) + "</" + tag + ">");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writeTag(String tag, long content)
/*     */   {
/* 114 */     writeLineRaw("<" + tag + ">" + content + "</" + tag + ">");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writeTag(String tag, boolean content)
/*     */   {
/* 122 */     writeLineRaw("<" + tag + ">" + (content ? "YES" : "NO") + "</" + tag + ">");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void writeLineRaw(String str)
/*     */   {
/* 129 */     this.writer.println(this.current_indent_string + str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void writeLineEscaped(String str)
/*     */   {
/* 136 */     this.writer.println(this.current_indent_string + escapeXML(str));
/*     */   }
/*     */   
/*     */ 
/*     */   protected void resetIndent()
/*     */   {
/* 142 */     this.current_indent_string = "";
/*     */   }
/*     */   
/*     */ 
/*     */   protected void indent()
/*     */   {
/* 148 */     for (int i = 0; i < 4; i++)
/*     */     {
/* 150 */       this.current_indent_string += " ";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void exdent()
/*     */   {
/* 157 */     if (this.current_indent_string.length() >= 4)
/*     */     {
/* 159 */       this.current_indent_string = this.current_indent_string.substring(0, this.current_indent_string.length() - 4);
/*     */     }
/*     */     else {
/* 162 */       this.current_indent_string = "";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String escapeXML(String str)
/*     */   {
/* 170 */     if (str == null)
/*     */     {
/* 172 */       return "";
/*     */     }
/*     */     
/* 175 */     str = str.replaceAll("&", "&amp;");
/* 176 */     str = str.replaceAll(">", "&gt;");
/* 177 */     str = str.replaceAll("<", "&lt;");
/* 178 */     str = str.replaceAll("\"", "&quot;");
/* 179 */     str = str.replaceAll("'", "&apos;");
/* 180 */     str = str.replaceAll("--", "&#45;&#45;");
/*     */     
/* 182 */     char[] chars = str.toCharArray();
/*     */     
/*     */ 
/*     */ 
/* 186 */     for (int i = 0; i < chars.length; i++)
/*     */     {
/* 188 */       int c = chars[i];
/*     */       
/* 190 */       if ((c <= 31) || ((c >= 127) && (c <= 159)) || (!Character.isDefined(c)))
/*     */       {
/*     */ 
/*     */ 
/* 194 */         chars[i] = '?';
/*     */       }
/*     */     }
/*     */     
/* 198 */     return new String(chars);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String unescapeXML(String str)
/*     */   {
/* 205 */     if (str == null)
/*     */     {
/* 207 */       return "";
/*     */     }
/*     */     
/* 210 */     str = str.replaceAll("&gt;", ">");
/* 211 */     str = str.replaceAll("&lt;", "<");
/* 212 */     str = str.replaceAll("&quot;", "\"");
/* 213 */     str = str.replaceAll("&apos;", "'");
/* 214 */     str = str.replaceAll("&#45;&#45;", "--");
/* 215 */     str = str.replaceAll("&amp;", "&");
/*     */     
/* 217 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String[] splitWithEscape(String str, char delim)
/*     */   {
/* 225 */     List<String> res = new ArrayList();
/*     */     
/* 227 */     String current = "";
/*     */     
/* 229 */     char[] chars = str.toCharArray();
/*     */     
/* 231 */     for (int i = 0; i < chars.length; i++)
/*     */     {
/* 233 */       char c = chars[i];
/*     */       
/* 235 */       if ((c == '\\') && (i + 1 < chars.length) && (chars[(i + 1)] == delim))
/*     */       {
/* 237 */         current = current + delim;
/*     */         
/* 239 */         i++;
/*     */       }
/* 241 */       else if (c == delim)
/*     */       {
/* 243 */         if (current.length() > 0)
/*     */         {
/* 245 */           res.add(current);
/*     */           
/* 247 */           current = "";
/*     */         }
/*     */       }
/*     */       else {
/* 251 */         current = current + c;
/*     */       }
/*     */     }
/*     */     
/* 255 */     if (current.length() > 0)
/*     */     {
/* 257 */       res.add(current);
/*     */     }
/*     */     
/* 260 */     return (String[])res.toArray(new String[res.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void flushOutputStream()
/*     */   {
/* 266 */     if (this.writer != null)
/*     */     {
/* 268 */       this.writer.flush();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void closeOutputStream()
/*     */   {
/* 275 */     if (this.writer != null)
/*     */     {
/* 277 */       this.writer.flush();
/*     */       
/* 279 */       this.writer.close();
/*     */       
/* 281 */       this.writer = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writeGenericMapEntry(String name, Object value)
/*     */   {
/* 292 */     if (this.generic_simple)
/*     */     {
/* 294 */       name = name.replace(' ', '_').toUpperCase();
/*     */       
/* 296 */       writeLineRaw("<" + name + ">");
/*     */       try
/*     */       {
/* 299 */         indent();
/*     */         
/* 301 */         writeGeneric(value);
/*     */       }
/*     */       finally {
/* 304 */         exdent();
/*     */       }
/*     */       
/* 307 */       writeLineRaw("</" + name + ">");
/*     */     }
/*     */     else {
/* 310 */       writeLineRaw("<KEY name=\"" + escapeXML(name) + "\">");
/*     */       try
/*     */       {
/* 313 */         indent();
/*     */         
/* 315 */         writeGeneric(value);
/*     */       }
/*     */       finally {
/* 318 */         exdent();
/*     */       }
/*     */       
/* 321 */       writeLineRaw("</KEY>");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void writeGeneric(Object obj)
/*     */   {
/* 329 */     if ((obj instanceof Map))
/*     */     {
/* 331 */       writeGeneric((Map)obj);
/*     */     }
/* 333 */     else if ((obj instanceof List))
/*     */     {
/* 335 */       writeGeneric((List)obj);
/*     */     }
/* 337 */     else if ((obj instanceof String))
/*     */     {
/* 339 */       writeGeneric((String)obj);
/*     */     }
/* 341 */     else if ((obj instanceof byte[]))
/*     */     {
/* 343 */       writeGeneric((byte[])obj);
/*     */     }
/*     */     else
/*     */     {
/* 347 */       writeGeneric((Long)obj);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void writeGeneric(Map map)
/*     */   {
/* 355 */     writeLineRaw("<MAP>");
/*     */     try
/*     */     {
/* 358 */       indent();
/*     */       
/* 360 */       Iterator it = map.keySet().iterator();
/*     */       
/* 362 */       while (it.hasNext())
/*     */       {
/* 364 */         String key = (String)it.next();
/*     */         
/* 366 */         writeGenericMapEntry(key, map.get(key));
/*     */       }
/*     */     }
/*     */     finally {
/* 370 */       exdent();
/*     */     }
/*     */     
/* 373 */     writeLineRaw("</MAP>");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void writeGeneric(List list)
/*     */   {
/* 380 */     writeLineRaw("<LIST>");
/*     */     try
/*     */     {
/* 383 */       indent();
/*     */       
/* 385 */       for (int i = 0; i < list.size(); i++)
/*     */       {
/* 387 */         writeGeneric(list.get(i));
/*     */       }
/*     */     }
/*     */     finally {
/* 391 */       exdent();
/*     */     }
/*     */     
/* 394 */     writeLineRaw("</LIST>");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void writeGeneric(byte[] bytes)
/*     */   {
/* 401 */     if (this.generic_simple) {
/*     */       try
/*     */       {
/* 404 */         writeLineRaw(escapeXML(new String(bytes, "UTF-8")));
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 408 */         e.printStackTrace();
/*     */       }
/*     */       
/*     */     } else {
/* 412 */       writeTag("BYTES", encodeBytes(bytes));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void writeGeneric(String str)
/*     */   {
/* 420 */     if (this.generic_simple) {
/*     */       try
/*     */       {
/* 423 */         writeLineRaw(escapeXML(str));
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 427 */         e.printStackTrace();
/*     */       }
/*     */       
/*     */     } else {
/* 431 */       writeTag("STRING", str);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void writeGeneric(Long l)
/*     */   {
/* 439 */     if (this.generic_simple)
/*     */     {
/* 441 */       writeLineRaw(l.toString());
/*     */     }
/*     */     else {
/* 444 */       writeTag("LONG", "" + l);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writeTag(String tag, byte[] content)
/*     */   {
/* 453 */     writeLineRaw("<" + tag + ">" + encodeBytes(content) + "</" + tag + ">");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writeLocalisableTag(String tag, byte[] content)
/*     */   {
/* 461 */     boolean use_bytes = true;
/*     */     
/* 463 */     String utf_string = null;
/*     */     try
/*     */     {
/* 466 */       utf_string = new String(content, "UTF8");
/*     */       
/* 468 */       if (Arrays.equals(content, utf_string.getBytes("UTF8")))
/*     */       {
/*     */ 
/*     */ 
/* 472 */         use_bytes = false;
/*     */       }
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {}
/*     */     
/* 477 */     writeLineRaw("<" + tag + " encoding=\"" + (use_bytes ? "bytes" : "utf8") + "\">" + (use_bytes ? encodeBytes(content) : escapeXML(utf_string)) + "</" + tag + ">");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String encodeBytes(byte[] bytes)
/*     */   {
/* 485 */     String data = ByteFormatter.nicePrint(bytes, true);
/*     */     
/* 487 */     return data;
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
/*     */   protected String getUTF(byte[] bytes)
/*     */   {
/*     */     try
/*     */     {
/* 507 */       return new String(bytes, "UTF8");
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/* 511 */       Debug.printStackTrace(e);
/*     */     }
/* 513 */     return "";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/xml/util/XUXmlWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */