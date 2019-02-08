/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IndentWriter
/*     */ {
/*     */   private static final String INDENT_STRING = "    ";
/*     */   private static final String INDENT_STRING_HTML = "&nbsp;&nbsp;&nbsp;&nbsp;";
/*     */   private final PrintWriter pw;
/*  31 */   private String indent = "";
/*     */   
/*     */ 
/*     */   private boolean html;
/*     */   
/*     */   private boolean force;
/*     */   
/*     */ 
/*     */   public IndentWriter(PrintWriter _pw)
/*     */   {
/*  41 */     this.pw = _pw;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setHTML(boolean _html)
/*     */   {
/*  48 */     this.html = _html;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void println(String str)
/*     */   {
/*  55 */     if (this.html)
/*     */     {
/*  57 */       this.pw.print(this.indent + str + "<br>");
/*     */     }
/*     */     else
/*     */     {
/*  61 */       this.pw.println(this.indent + str);
/*     */     }
/*     */     
/*  64 */     if (this.force)
/*     */     {
/*  66 */       this.pw.flush();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void indent()
/*     */   {
/*  73 */     this.indent += (this.html ? "&nbsp;&nbsp;&nbsp;&nbsp;" : "    ");
/*     */   }
/*     */   
/*     */ 
/*     */   public void exdent()
/*     */   {
/*  79 */     if (this.indent.length() > 0)
/*     */     {
/*  81 */       this.indent = this.indent.substring((this.html ? "&nbsp;&nbsp;&nbsp;&nbsp;" : "    ").length());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getTab()
/*     */   {
/*  88 */     return this.html ? "&nbsp;&nbsp;&nbsp;&nbsp;" : "    ";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setForce(boolean b)
/*     */   {
/*  95 */     this.force = b;
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/* 101 */     this.pw.close();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/IndentWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */