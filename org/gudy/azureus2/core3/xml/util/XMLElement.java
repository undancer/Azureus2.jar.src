/*     */ package org.gudy.azureus2.core3.xml.util;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class XMLElement
/*     */ {
/*     */   protected String text_content;
/*     */   protected Collection<XMLElement> contents;
/*     */   protected Map<String, String> attributes;
/*     */   protected final String tag_name;
/*     */   protected boolean auto_order;
/*     */   
/*     */   public XMLElement(String tag_name)
/*     */   {
/*  48 */     this(tag_name, false);
/*     */   }
/*     */   
/*     */   public XMLElement(String tag_name, boolean auto_order) {
/*  52 */     this.text_content = null;
/*  53 */     this.attributes = null;
/*  54 */     this.contents = null;
/*  55 */     this.tag_name = tag_name;
/*  56 */     this.auto_order = auto_order;
/*     */   }
/*     */   
/*     */   public String getTag() {
/*  60 */     return this.tag_name;
/*     */   }
/*     */   
/*     */   public String getAttribute(String key) {
/*  64 */     if (this.attributes == null) return null;
/*  65 */     return (String)this.attributes.get(key);
/*     */   }
/*     */   
/*     */   public void addAttribute(String key, String value) {
/*  69 */     if (this.attributes == null) {
/*  70 */       this.attributes = new TreeMap(ATTRIBUTE_COMPARATOR);
/*     */     }
/*  72 */     this.attributes.put(key, value);
/*     */   }
/*     */   
/*     */   public void addAttribute(String key, int value) {
/*  76 */     addAttribute(key, String.valueOf(value));
/*     */   }
/*     */   
/*     */   public void addAttribute(String key, boolean value) {
/*  80 */     addAttribute(key, value ? "yes" : "no");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addContent(String s)
/*     */   {
/*  88 */     if (s == null) {
/*  89 */       throw new NullPointerException();
/*     */     }
/*  91 */     if (this.contents != null) {
/*  92 */       throw new IllegalStateException("cannot add text content to an XMLElement when it contains child XMLElement objects");
/*     */     }
/*  94 */     if (this.text_content != null) {
/*  95 */       throw new IllegalStateException("text content is already set, you cannot set it again");
/*     */     }
/*  97 */     this.text_content = s;
/*     */   }
/*     */   
/*     */   public void addContent(XMLElement e) {
/* 101 */     if (e == null) {
/* 102 */       throw new NullPointerException();
/*     */     }
/* 104 */     if (this.text_content != null) {
/* 105 */       throw new IllegalStateException("cannot add child XMLElement when it contains text content");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 111 */     if (this.contents == null) {
/* 112 */       if (!this.auto_order) {
/* 113 */         this.contents = new ArrayList();
/*     */       }
/*     */       else {
/* 116 */         this.contents = new TreeSet(CONTENT_COMPARATOR);
/*     */       }
/*     */     }
/*     */     
/* 120 */     this.contents.add(e);
/*     */   }
/*     */   
/*     */   public void printTo(PrintWriter pw) {
/* 124 */     printTo(pw, 0, false);
/*     */   }
/*     */   
/*     */   public void printTo(PrintWriter pw, boolean spaced_out) {
/* 128 */     printTo(pw, 0, spaced_out);
/*     */   }
/*     */   
/*     */   public void printTo(PrintWriter pw, int indent) {
/* 132 */     printTo(pw, indent, false);
/*     */   }
/*     */   
/*     */   public void printTo(PrintWriter pw, int indent, boolean spaced_out)
/*     */   {
/* 137 */     for (int i = 0; i < indent; i++) { pw.print(" ");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 142 */     if ((this.attributes == null) && (this.contents == null) && (this.text_content == null)) {
/* 143 */       pw.print("<");
/* 144 */       pw.print(this.tag_name);
/* 145 */       pw.print(" />");
/* 146 */       return;
/*     */     }
/*     */     
/* 149 */     pw.print("<");
/* 150 */     pw.print(this.tag_name);
/*     */     
/*     */ 
/* 153 */     if (this.attributes != null) {
/* 154 */       Iterator<Map.Entry<String, String>> itr = this.attributes.entrySet().iterator();
/* 155 */       while (itr.hasNext()) {
/* 156 */         Map.Entry<String, String> entry = (Map.Entry)itr.next();
/* 157 */         pw.print(" ");
/* 158 */         pw.print((String)entry.getKey());
/* 159 */         pw.print("=\"");
/* 160 */         pw.print(quote((String)entry.getValue()));
/* 161 */         pw.print("\"");
/*     */       }
/*     */     }
/*     */     
/* 165 */     boolean needs_indented_close = this.contents != null;
/* 166 */     boolean needs_close_tag = (needs_indented_close) || (this.text_content != null);
/*     */     
/* 168 */     needs_indented_close = (needs_indented_close) || (spaced_out);
/* 169 */     needs_close_tag = (needs_close_tag) || (spaced_out);
/*     */     
/* 171 */     if (needs_indented_close) { pw.println(">");
/* 172 */     } else if (needs_close_tag) pw.print(">"); else {
/* 173 */       pw.print(" />");
/*     */     }
/*     */     
/* 176 */     if (this.text_content != null) {
/* 177 */       if (spaced_out) {
/* 178 */         for (int i = 0; i < indent + 2; i++) pw.print(" ");
/* 179 */         pw.print(quote(this.text_content));
/* 180 */         pw.println();
/*     */       }
/*     */       else {
/* 183 */         pw.print(quote(this.text_content));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 188 */     if (this.contents != null) {
/* 189 */       Iterator<XMLElement> itr = this.contents.iterator();
/* 190 */       while (itr.hasNext()) {
/* 191 */         XMLElement content_element = (XMLElement)itr.next();
/* 192 */         content_element.printTo(pw, indent + 2, spaced_out);
/*     */       }
/*     */     }
/*     */     
/* 196 */     if (needs_indented_close) {
/* 197 */       for (int i = 0; i < indent; i++) { pw.print(" ");
/*     */       }
/*     */     }
/* 200 */     if (needs_close_tag) {
/* 201 */       pw.print("</");
/* 202 */       pw.print(this.tag_name);
/* 203 */       pw.println(">");
/*     */     }
/*     */   }
/*     */   
/*     */   private String quote(String text) {
/* 208 */     text = text.replaceAll("&", "&amp;");
/* 209 */     text = text.replaceAll(">", "&gt;");
/* 210 */     text = text.replaceAll("<", "&lt;");
/* 211 */     text = text.replaceAll("\"", "&quot;");
/* 212 */     text = text.replaceAll("--", "&#45;&#45;");
/* 213 */     return text;
/*     */   }
/*     */   
/*     */   public XMLElement makeContent(String tag_name) {
/* 217 */     return makeContent(tag_name, false);
/*     */   }
/*     */   
/*     */   public XMLElement makeContent(String tag_name, boolean auto_order) {
/* 221 */     XMLElement content = new XMLElement(tag_name, auto_order);
/* 222 */     addContent(content);
/* 223 */     return content;
/*     */   }
/*     */   
/*     */   public void clear() {
/* 227 */     this.text_content = null;
/* 228 */     this.attributes = null;
/* 229 */     this.contents = null;
/*     */   }
/*     */   
/*     */   public void setAutoOrdering(boolean mode) {
/* 233 */     if (mode == this.auto_order) return;
/* 234 */     this.auto_order = mode;
/* 235 */     if (this.contents == null) return;
/* 236 */     Collection<XMLElement> previous_contents = this.contents;
/* 237 */     if (this.auto_order) {
/* 238 */       this.contents = new TreeSet(CONTENT_COMPARATOR);
/* 239 */       this.contents.addAll(previous_contents);
/*     */     }
/*     */     else {
/* 242 */       this.contents = new ArrayList(previous_contents);
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString() {
/* 247 */     return "XMLElement[" + this.tag_name + "]@" + Integer.toHexString(System.identityHashCode(this));
/*     */   }
/*     */   
/* 250 */   private static final Comparator<String> ATTRIBUTE_COMPARATOR = String.CASE_INSENSITIVE_ORDER;
/*     */   
/*     */   private static class ContentComparator implements Comparator<XMLElement> {
/*     */     public int compare(XMLElement xe1, XMLElement xe2) {
/* 254 */       if ((xe1 == null) || (xe2 == null)) { throw new NullPointerException();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 273 */       if (xe1 == xe2) { return 0;
/*     */       }
/*     */       
/* 276 */       int result = String.CASE_INSENSITIVE_ORDER.compare(xe1.getTag(), xe2.getTag());
/* 277 */       if (result != 0) { return result;
/*     */       }
/*     */       
/* 280 */       int xe1_index = 0;int xe2_index = 0;
/*     */       try {
/* 282 */         xe1_index = Integer.parseInt(xe1.getAttribute("index"));
/* 283 */         xe2_index = Integer.parseInt(xe2.getAttribute("index"));
/*     */       }
/*     */       catch (NullPointerException ne) {
/* 286 */         xe1_index = xe2_index = 0;
/*     */       }
/*     */       catch (NumberFormatException ne) {
/* 289 */         xe1_index = xe2_index = 0;
/*     */       }
/*     */       
/* 292 */       if (xe1_index != xe2_index) {
/* 293 */         return xe1_index - xe2_index;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 303 */       throw new IllegalArgumentException("Shouldn't be using sorting for contents if you have tags with same name and no index attribute! (tag: " + xe1.getTag() + ")");
/*     */     }
/*     */   }
/*     */   
/* 307 */   private static final Comparator<XMLElement> CONTENT_COMPARATOR = new ContentComparator(null);
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/xml/util/XMLElement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */