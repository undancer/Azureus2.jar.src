/*      */ package org.gudy.azureus2.ui.swt.shells;
/*      */ 
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.eclipse.swt.events.KeyEvent;
/*      */ import org.eclipse.swt.events.KeyListener;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.Device;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.layout.RowData;
/*      */ import org.eclipse.swt.layout.RowLayout;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Canvas;
/*      */ import org.eclipse.swt.widgets.Combo;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.eclipse.swt.widgets.Spinner;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class GCStringPrinter
/*      */ {
/*      */   private static final boolean DEBUG = false;
/*      */   private static final String GOOD_STRING = "(/|,jI~`gy";
/*      */   public static final int FLAG_SKIPCLIP = 1;
/*      */   public static final int FLAG_FULLLINESONLY = 2;
/*      */   public static final int FLAG_NODRAW = 4;
/*      */   public static final int FLAG_KEEP_URL_INFO = 8;
/*   60 */   private static final Pattern patHREF = Pattern.compile("<\\s*?a\\s.*?href\\s*?=\\s*?\"(.+?)\".*?>(.*?)<\\s*?/a\\s*?>", 2);
/*      */   
/*      */ 
/*      */ 
/*   64 */   private static final Pattern patAHREF_TITLE = Pattern.compile("title=\\\"([^\\\"]+)", 2);
/*      */   
/*      */ 
/*   67 */   private static final Pattern patAHREF_TARGET = Pattern.compile("target=\\\"([^\\\"]+)", 2);
/*      */   
/*      */ 
/*      */   private static final int MAX_LINE_LEN = 4000;
/*      */   
/*      */ 
/*      */   private static final int MAX_WORD_LEN = 4000;
/*      */   
/*      */ 
/*      */   private boolean cutoff;
/*      */   
/*      */ 
/*      */   private boolean isWordCut;
/*      */   
/*      */ 
/*      */   private GC gc;
/*      */   
/*      */ 
/*      */   private String string;
/*      */   
/*      */ 
/*      */   private Rectangle printArea;
/*      */   
/*      */ 
/*      */   private int swtFlags;
/*      */   
/*      */   private int printFlags;
/*      */   
/*      */   private Point size;
/*      */   
/*      */   private Color urlColor;
/*      */   
/*      */   private List<URLInfo> listUrlInfo;
/*      */   
/*      */   private Image[] images;
/*      */   
/*      */   private float[] imageScales;
/*      */   
/*      */   private int iCurrentHeight;
/*      */   
/*      */   private boolean wrap;
/*      */   
/*      */ 
/*      */   public static class URLInfo
/*      */   {
/*      */     public String url;
/*      */     
/*      */     public String text;
/*      */     
/*      */     public Color urlColor;
/*      */     
/*      */     int relStartPos;
/*      */     
/*  120 */     public List<Rectangle> hitAreas = null;
/*      */     
/*      */     int titleLength;
/*      */     
/*      */     public String fullString;
/*      */     
/*      */     public String title;
/*      */     
/*      */     public String target;
/*      */     
/*      */     public boolean urlUnderline;
/*      */     
/*      */     public String toString()
/*      */     {
/*  134 */       return super.toString() + ": relStart=" + this.relStartPos + ";url=" + this.url + ";title=" + this.text + ";hit=" + (this.hitAreas == null ? 0 : this.hitAreas.size());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class LineInfo
/*      */   {
/*      */     String originalLine;
/*      */     
/*      */     String lineOutputed;
/*      */     
/*      */     int excessPos;
/*      */     
/*      */     public int relStartPos;
/*      */     
/*      */     public int[] imageIndexes;
/*      */     
/*  152 */     public Point outputLineExtent = new Point(0, 0);
/*      */     
/*      */     public LineInfo(String originalLine, int relStartPos) {
/*  155 */       this.originalLine = originalLine;
/*  156 */       this.relStartPos = relStartPos;
/*      */     }
/*      */     
/*      */     public String toString()
/*      */     {
/*  161 */       return super.toString() + ": relStart=" + this.relStartPos + ";xcess=" + this.excessPos + ";orig=" + this.originalLine + ";output=" + this.lineOutputed;
/*      */     }
/*      */   }
/*      */   
/*      */   public static boolean printString(GC gc, String string, Rectangle printArea)
/*      */   {
/*  167 */     return printString(gc, string, printArea, false, false);
/*      */   }
/*      */   
/*      */   public static boolean printString(GC gc, String string, Rectangle printArea, boolean skipClip, boolean fullLinesOnly)
/*      */   {
/*  172 */     return printString(gc, string, printArea, skipClip, fullLinesOnly, 192);
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
/*      */   public static boolean printString(GC gc, String string, Rectangle printArea, boolean skipClip, boolean fullLinesOnly, int swtFlags)
/*      */   {
/*      */     try
/*      */     {
/*  190 */       GCStringPrinter sp = new GCStringPrinter(gc, string, printArea, skipClip, fullLinesOnly, swtFlags);
/*      */       
/*  192 */       return sp.printString();
/*      */     } catch (Exception e) {
/*  194 */       e.printStackTrace();
/*      */     }
/*      */     
/*  197 */     return false;
/*      */   }
/*      */   
/*      */   private boolean _printString() {
/*  201 */     if (Constants.isWindows) {
/*  202 */       return swt_printString_NoAdvanced();
/*      */     }
/*  204 */     return swt_printString();
/*      */   }
/*      */   
/*      */   private boolean swt_printString_NoAdvanced() {
/*  208 */     boolean b = false;
/*      */     try {
/*  210 */       boolean wasAdvanced = this.gc.getAdvanced();
/*  211 */       Rectangle clipping = null;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  221 */       if ((this.gc.getAdvanced()) && (this.gc.getTextAntialias() == -1) && (this.gc.getAlpha() == 255))
/*      */       {
/*  223 */         clipping = this.gc.getClipping();
/*  224 */         this.gc.setAdvanced(false);
/*  225 */         Utils.setClipping(this.gc, clipping);
/*      */       }
/*  227 */       b = __printString();
/*  228 */       if (wasAdvanced) {
/*  229 */         this.gc.setAdvanced(true);
/*  230 */         Utils.setClipping(this.gc, clipping);
/*      */       }
/*      */     } catch (Throwable t) {
/*  233 */       Debug.out(t);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  240 */     return b;
/*      */   }
/*      */   
/*      */   private boolean swt_printString() {
/*  244 */     boolean b = false;
/*      */     try {
/*  246 */       b = __printString();
/*      */     } catch (Throwable t) {
/*  248 */       Debug.out(t);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  255 */     return b;
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
/*      */   private boolean __printString()
/*      */   {
/*  269 */     this.size = new Point(0, 0);
/*  270 */     this.isWordCut = false;
/*      */     
/*  272 */     if (this.string == null) {
/*  273 */       return false;
/*      */     }
/*      */     
/*  276 */     if ((this.printArea == null) || (this.printArea.isEmpty())) {
/*  277 */       return false;
/*      */     }
/*      */     
/*  280 */     ArrayList<LineInfo> lines = new ArrayList(1);
/*      */     
/*  282 */     while (this.string.indexOf('\t') >= 0) {
/*  283 */       this.string = this.string.replace('\t', ' ');
/*      */     }
/*      */     
/*  286 */     if (this.string.indexOf("  ") > 0) {
/*  287 */       this.string = this.string.replaceAll("  +", " ");
/*      */     }
/*      */     
/*  290 */     boolean hasSlashR = this.string.indexOf('\r') > 0;
/*      */     
/*  292 */     boolean fullLinesOnly = (this.printFlags & 0x2) != 0;
/*  293 */     boolean skipClip = (this.printFlags & 0x1) != 0;
/*  294 */     boolean noDraw = (this.printFlags & 0x4) != 0;
/*  295 */     this.wrap = ((this.swtFlags & 0x40) != 0);
/*      */     
/*  297 */     if ((this.swtFlags & 0x480) == 0)
/*      */     {
/*  299 */       fullLinesOnly = true;
/*  300 */       this.printFlags |= 0x2;
/*      */     }
/*      */     
/*  303 */     if (this.string.indexOf('<') >= 0) {
/*  304 */       if ((this.printFlags & 0x8) == 0) {
/*  305 */         Matcher htmlMatcher = patHREF.matcher(this.string);
/*  306 */         boolean hasURL = htmlMatcher.find();
/*  307 */         if (hasURL) {
/*  308 */           this.listUrlInfo = new ArrayList(1);
/*      */           
/*  310 */           while (hasURL) {
/*  311 */             URLInfo urlInfo = new URLInfo();
/*      */             
/*      */ 
/*      */ 
/*  315 */             urlInfo.fullString = htmlMatcher.group();
/*  316 */             urlInfo.relStartPos = htmlMatcher.start(0);
/*      */             
/*  318 */             urlInfo.url = this.string.substring(htmlMatcher.start(1), htmlMatcher.end(1));
/*      */             
/*  320 */             urlInfo.text = this.string.substring(htmlMatcher.start(2), htmlMatcher.end(2));
/*      */             
/*  322 */             urlInfo.titleLength = urlInfo.text.length();
/*      */             
/*  324 */             Matcher matcherTitle = patAHREF_TITLE.matcher(urlInfo.fullString);
/*  325 */             if (matcherTitle.find()) {
/*  326 */               urlInfo.title = this.string.substring(urlInfo.relStartPos + matcherTitle.start(1), urlInfo.relStartPos + matcherTitle.end(1));
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*  331 */             Matcher matcherTarget = patAHREF_TARGET.matcher(urlInfo.fullString);
/*  332 */             if (matcherTarget.find()) {
/*  333 */               urlInfo.target = this.string.substring(urlInfo.relStartPos + matcherTarget.start(1), urlInfo.relStartPos + matcherTarget.end(1));
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  343 */             this.string = htmlMatcher.replaceFirst(urlInfo.text.replaceAll("\\$", "\\\\\\$"));
/*      */             
/*      */ 
/*  346 */             this.listUrlInfo.add(urlInfo);
/*  347 */             htmlMatcher = patHREF.matcher(this.string);
/*  348 */             hasURL = htmlMatcher.find(urlInfo.relStartPos);
/*      */           }
/*      */         }
/*      */       } else {
/*  352 */         Matcher htmlMatcher = patHREF.matcher(this.string);
/*  353 */         this.string = htmlMatcher.replaceAll("$2");
/*      */       }
/*      */     }
/*      */     
/*  357 */     Rectangle rectDraw = new Rectangle(this.printArea.x, this.printArea.y, this.printArea.width, this.printArea.height);
/*      */     
/*      */ 
/*  360 */     Rectangle oldClipping = null;
/*      */     try {
/*  362 */       if ((!skipClip) && (!noDraw)) {
/*  363 */         oldClipping = this.gc.getClipping();
/*      */         
/*      */ 
/*  366 */         Utils.setClipping(this.gc, this.printArea);
/*      */       }
/*      */       
/*      */ 
/*  370 */       this.iCurrentHeight = 0;
/*  371 */       int currentCharPos = 0;
/*      */       
/*  373 */       int posNewLine = this.string.indexOf('\n');
/*  374 */       if (hasSlashR) {
/*  375 */         int posR = this.string.indexOf('\r');
/*  376 */         if (posR == -1) {
/*  377 */           posR = posNewLine;
/*      */         }
/*  379 */         posNewLine = Math.min(posNewLine, posR);
/*      */       }
/*  381 */       if (posNewLine < 0) {
/*  382 */         posNewLine = this.string.length();
/*      */       }
/*  384 */       int posLastNewLine = 0;
/*  385 */       while ((posNewLine >= 0) && (posLastNewLine < this.string.length())) {
/*  386 */         String sLine = this.string.substring(posLastNewLine, posNewLine);
/*      */         do
/*      */         {
/*  389 */           LineInfo lineInfo = new LineInfo(sLine, currentCharPos);
/*  390 */           lineInfo = processLine(this.gc, lineInfo, this.printArea, fullLinesOnly, false);
/*      */           
/*  392 */           String sProcessedLine = lineInfo.lineOutputed;
/*      */           
/*  394 */           if ((sProcessedLine != null) && (sProcessedLine.length() > 0)) {
/*  395 */             if ((lineInfo.outputLineExtent.x == 0) || (lineInfo.outputLineExtent.y == 0)) {
/*  396 */               lineInfo.outputLineExtent = this.gc.stringExtent(sProcessedLine);
/*      */             }
/*  398 */             this.iCurrentHeight += lineInfo.outputLineExtent.y;
/*  399 */             boolean isOverY = this.iCurrentHeight > this.printArea.height;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  408 */             if ((isOverY) && (!fullLinesOnly))
/*      */             {
/*  410 */               lines.add(lineInfo);
/*  411 */             } else { if ((isOverY) && (fullLinesOnly) && (lines.size() > 0))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  464 */                 this.cutoff = true;
/*      */                 
/*      */ 
/*      */ 
/*  468 */                 boolean bool1 = false;
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  512 */                 if (lines.size() > 0)
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  528 */                   for (LineInfo lineInfo : lines) {
/*  529 */                     this.size.x = Math.max(lineInfo.outputLineExtent.x, this.size.x);
/*  530 */                     this.size.y += lineInfo.outputLineExtent.y;
/*      */                   }
/*      */                   
/*  533 */                   if ((this.swtFlags & 0x400) != 0) {
/*  534 */                     rectDraw.y = (rectDraw.y + rectDraw.height - this.size.y);
/*  535 */                   } else if ((this.swtFlags & 0x80) == 0)
/*      */                   {
/*  537 */                     rectDraw.y += (rectDraw.height - this.size.y) / 2;
/*      */                   }
/*      */                   
/*  540 */                   if ((!noDraw) || (this.listUrlInfo != null)) {
/*  541 */                     for (LineInfo lineInfo : lines) {
/*      */                       try {
/*  543 */                         drawLine(this.gc, lineInfo, this.swtFlags, rectDraw, noDraw);
/*      */                       } catch (Throwable t) {
/*  545 */                         t.printStackTrace();
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 
/*  551 */                 if ((!skipClip) && (!noDraw))
/*  552 */                   Utils.setClipping(this.gc, oldClipping); return bool1;
/*      */               }
/*  470 */               lines.add(lineInfo);
/*      */             }
/*  472 */             sLine = (lineInfo.excessPos >= 0) && (this.wrap) ? sLine.substring(lineInfo.excessPos) : null;
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/*  478 */             this.iCurrentHeight += lineInfo.outputLineExtent.y;
/*  479 */             lines.add(lineInfo);
/*  480 */             currentCharPos++;
/*  481 */             break;
/*      */           }
/*      */           
/*      */ 
/*  485 */           currentCharPos += (lineInfo.excessPos >= 0 ? lineInfo.excessPos : lineInfo.lineOutputed.length());
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*  490 */         while (sLine != null);
/*      */         
/*  492 */         if ((this.string.length() > posNewLine) && (this.string.charAt(posNewLine) == '\r') && (this.string.charAt(posNewLine + 1) == '\n'))
/*      */         {
/*  494 */           posNewLine++;
/*      */         }
/*  496 */         posLastNewLine = posNewLine + 1;
/*  497 */         currentCharPos = posLastNewLine;
/*      */         
/*  499 */         posNewLine = this.string.indexOf('\n', posLastNewLine);
/*  500 */         if (hasSlashR) {
/*  501 */           int posR = this.string.indexOf('\r', posLastNewLine);
/*  502 */           if (posR == -1) {
/*  503 */             posR = posNewLine;
/*      */           }
/*  505 */           posNewLine = Math.min(posNewLine, posR);
/*      */         }
/*  507 */         if (posNewLine < 0) {
/*  508 */           posNewLine = this.string.length();
/*      */         }
/*      */       }
/*      */       
/*  512 */       if (lines.size() > 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  528 */         for (LineInfo lineInfo : lines) {
/*  529 */           this.size.x = Math.max(lineInfo.outputLineExtent.x, this.size.x);
/*  530 */           this.size.y += lineInfo.outputLineExtent.y;
/*      */         }
/*      */         
/*  533 */         if ((this.swtFlags & 0x400) != 0) {
/*  534 */           rectDraw.y = (rectDraw.y + rectDraw.height - this.size.y);
/*  535 */         } else if ((this.swtFlags & 0x80) == 0)
/*      */         {
/*  537 */           rectDraw.y += (rectDraw.height - this.size.y) / 2;
/*      */         }
/*      */         
/*  540 */         if ((!noDraw) || (this.listUrlInfo != null)) {
/*  541 */           for (LineInfo lineInfo : lines) {
/*      */             try {
/*  543 */               drawLine(this.gc, lineInfo, this.swtFlags, rectDraw, noDraw);
/*      */             } catch (Throwable t) {
/*  545 */               t.printStackTrace();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  551 */       if ((!skipClip) && (!noDraw)) {
/*  552 */         Utils.setClipping(this.gc, oldClipping);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  512 */       if (lines.size() > 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  528 */         for (LineInfo lineInfo : lines) {
/*  529 */           this.size.x = Math.max(lineInfo.outputLineExtent.x, this.size.x);
/*  530 */           this.size.y += lineInfo.outputLineExtent.y;
/*      */         }
/*      */         
/*  533 */         if ((this.swtFlags & 0x400) != 0) {
/*  534 */           rectDraw.y = (rectDraw.y + rectDraw.height - this.size.y);
/*  535 */         } else if ((this.swtFlags & 0x80) == 0)
/*      */         {
/*  537 */           rectDraw.y += (rectDraw.height - this.size.y) / 2;
/*      */         }
/*      */         
/*  540 */         if ((!noDraw) || (this.listUrlInfo != null)) {
/*  541 */           for (LineInfo lineInfo : lines) {
/*      */             try {
/*  543 */               drawLine(this.gc, lineInfo, this.swtFlags, rectDraw, noDraw);
/*      */             } catch (Throwable t) {
/*  545 */               t.printStackTrace();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  551 */       if ((!skipClip) && (!noDraw)) {
/*  552 */         Utils.setClipping(this.gc, oldClipping);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  557 */     this.cutoff |= this.size.y > this.printArea.height;
/*  558 */     return !this.cutoff;
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
/*      */   private LineInfo processLine(GC gc, LineInfo lineInfo, Rectangle printArea, boolean fullLinesOnly, boolean hasMoreElements)
/*      */   {
/*  571 */     if (lineInfo.originalLine.length() == 0) {
/*  572 */       lineInfo.lineOutputed = "";
/*  573 */       lineInfo.outputLineExtent = new Point(0, gc.stringExtent("(/|,jI~`gy").y);
/*  574 */       return lineInfo;
/*      */     }
/*      */     
/*  577 */     StringBuffer outputLine = null;
/*  578 */     int excessPos = -1;
/*      */     
/*  580 */     boolean b = (this.images != null) || (lineInfo.originalLine.length() > 4000);
/*  581 */     if (!b) {
/*  582 */       Point outputLineExtent = gc.stringExtent(lineInfo.originalLine);
/*  583 */       b = outputLineExtent.x > printArea.width;
/*  584 */       if (!b) {
/*  585 */         lineInfo.outputLineExtent = outputLineExtent;
/*      */       }
/*      */     }
/*      */     
/*  589 */     if (b) {
/*  590 */       outputLine = new StringBuffer();
/*      */       
/*      */ 
/*      */ 
/*  594 */       StringBuffer space = new StringBuffer(1);
/*      */       
/*  596 */       if ((!this.wrap) && (this.images == null))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  601 */         String sProcessedLine = lineInfo.originalLine.length() > 4000 ? lineInfo.originalLine.substring(0, 4000) : lineInfo.originalLine;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  608 */         excessPos = processWord(gc, lineInfo.originalLine, sProcessedLine, printArea, lineInfo, outputLine, space);
/*      */       }
/*      */       else {
/*  611 */         int posLastWordStart = 0;
/*  612 */         int posWordStart = lineInfo.originalLine.indexOf(' ');
/*  613 */         while (posWordStart == 0) {
/*  614 */           posWordStart = lineInfo.originalLine.indexOf(' ', posWordStart + 1);
/*      */         }
/*  616 */         if (posWordStart < 0) {
/*  617 */           posWordStart = lineInfo.originalLine.length();
/*      */         }
/*      */         
/*  620 */         int curPos = 0;
/*  621 */         while ((posWordStart >= 0) && (posLastWordStart < lineInfo.originalLine.length())) {
/*  622 */           String word = lineInfo.originalLine.substring(posLastWordStart, posWordStart);
/*  623 */           if (word.length() == 0) {
/*  624 */             excessPos = -1;
/*  625 */             outputLine.append(' ');
/*      */           }
/*      */           
/*  628 */           for (int i = 0; i < word.length(); i += 4000)
/*      */           {
/*  630 */             int endPos = i + 4000;
/*  631 */             String subWord; String subWord; if (endPos > word.length()) {
/*  632 */               subWord = word.substring(i);
/*      */             } else {
/*  634 */               subWord = word.substring(i, endPos);
/*      */             }
/*      */             
/*  637 */             excessPos = processWord(gc, lineInfo.originalLine, subWord, printArea, lineInfo, outputLine, space);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  644 */             if (excessPos >= 0) {
/*  645 */               excessPos += curPos;
/*  646 */               break;
/*      */             }
/*  648 */             if (endPos <= word.length()) {
/*  649 */               space.setLength(0);
/*      */             }
/*  651 */             curPos += subWord.length() + 1;
/*      */           }
/*  653 */           if (excessPos >= 0) {
/*      */             break;
/*      */           }
/*      */           
/*  657 */           posLastWordStart = posWordStart + 1;
/*  658 */           posWordStart = lineInfo.originalLine.indexOf(' ', posLastWordStart);
/*  659 */           if (posWordStart < 0) {
/*  660 */             posWordStart = lineInfo.originalLine.length();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  666 */     if ((!this.wrap) && (hasMoreElements) && (excessPos >= 0))
/*      */     {
/*      */ 
/*      */ 
/*  670 */       int len = outputLine.length();
/*  671 */       if (len > 2) {
/*  672 */         len -= 2;
/*      */       }
/*  674 */       outputLine.setLength(len);
/*  675 */       outputLine.append("…");
/*  676 */       this.cutoff = true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  685 */     lineInfo.excessPos = excessPos;
/*  686 */     lineInfo.lineOutputed = (outputLine == null ? lineInfo.originalLine : outputLine.toString());
/*  687 */     return lineInfo;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int processWord(GC gc, String sLine, String word, Rectangle printArea, LineInfo lineInfo, StringBuffer outputLine, StringBuffer space)
/*      */   {
/*  699 */     if (word.length() == 0) {
/*  700 */       space.append(' ');
/*  701 */       return -1;
/*      */     }
/*      */     
/*      */ 
/*  705 */     if ((this.images != null) && (word.length() >= 2) && (word.charAt(0) == '%')) {
/*  706 */       int imgIdx = word.charAt(1) - '0';
/*  707 */       if ((this.images.length > imgIdx) && (imgIdx >= 0) && (this.images[imgIdx] != null)) {
/*  708 */         Image img = this.images[imgIdx];
/*  709 */         Rectangle bounds = img.getBounds();
/*  710 */         if ((this.imageScales != null) && (this.imageScales.length > imgIdx)) {
/*  711 */           bounds.width = ((int)(bounds.width * this.imageScales[imgIdx]));
/*  712 */           bounds.height = ((int)(bounds.height * this.imageScales[imgIdx]));
/*      */         }
/*      */         
/*  715 */         Point spaceExtent = gc.stringExtent(space.toString());
/*  716 */         int newWidth = lineInfo.outputLineExtent.x + bounds.width + spaceExtent.x;
/*      */         
/*      */ 
/*  719 */         if ((newWidth > printArea.width) && (
/*  720 */           (bounds.width + spaceExtent.x < printArea.width) || (lineInfo.outputLineExtent.x > 0)))
/*      */         {
/*      */ 
/*      */ 
/*  724 */           return 0;
/*      */         }
/*      */         
/*      */ 
/*  728 */         if (lineInfo.imageIndexes == null) {
/*  729 */           lineInfo.imageIndexes = new int[] { imgIdx };
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  735 */         lineInfo.outputLineExtent = new Point(newWidth, Math.max(bounds.height, lineInfo.outputLineExtent.y));
/*      */         
/*  737 */         Point ptWordSize = gc.stringExtent(word.substring(2) + " ");
/*  738 */         if (lineInfo.outputLineExtent.x + ptWordSize.x > printArea.width) {
/*  739 */           outputLine.append(space);
/*  740 */           outputLine.append(word.substring(0, 2));
/*      */           
/*  742 */           return 2;
/*      */         }
/*      */         
/*  745 */         outputLine.append(space);
/*  746 */         space.setLength(0);
/*  747 */         outputLine.append(word.substring(0, 2));
/*  748 */         word = word.substring(2);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  760 */     Point ptLineAndWordSize = gc.stringExtent(outputLine + word + " ");
/*      */     
/*  762 */     if (ptLineAndWordSize.x > printArea.width)
/*      */     {
/*      */ 
/*  765 */       Point ptWordSize2 = gc.stringExtent(word + " ");
/*  766 */       boolean bWordLargerThanWidth = ptWordSize2.x > printArea.width;
/*      */       
/*  768 */       if (bWordLargerThanWidth) {
/*  769 */         this.isWordCut = true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  774 */       if ((bWordLargerThanWidth) && (lineInfo.outputLineExtent.x > 0))
/*      */       {
/*  776 */         return 0;
/*      */       }
/*      */       
/*  779 */       int endIndex = word.length();
/*  780 */       long diff = endIndex;
/*      */       
/*  782 */       while (ptLineAndWordSize.x != printArea.width) {
/*  783 */         diff = (diff >> 1) + diff % 2L;
/*      */         
/*  785 */         if (diff <= 0L) {
/*  786 */           diff = 1L;
/*      */         }
/*      */         
/*      */ 
/*  790 */         if (ptLineAndWordSize.x > printArea.width) {
/*  791 */           endIndex = (int)(endIndex - diff);
/*  792 */           if (endIndex < 1) {
/*  793 */             endIndex = 1;
/*      */           }
/*      */         } else {
/*  796 */           endIndex = (int)(endIndex + diff);
/*  797 */           if (endIndex > word.length()) {
/*  798 */             endIndex = word.length();
/*      */           }
/*      */         }
/*      */         
/*  802 */         ptLineAndWordSize = gc.stringExtent(outputLine + word.substring(0, endIndex) + " ");
/*      */         
/*  804 */         if (diff <= 1L) {
/*      */           break;
/*      */         }
/*      */       }
/*  808 */       boolean nothingFit = endIndex == 0;
/*  809 */       if (nothingFit) {
/*  810 */         endIndex = 1;
/*      */       }
/*  812 */       if ((ptLineAndWordSize.x > printArea.width) && (endIndex > 1)) {
/*  813 */         endIndex--;
/*  814 */         ptLineAndWordSize = gc.stringExtent(outputLine + word.substring(0, endIndex) + " ");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  822 */       if ((this.wrap) && ((this.printFlags & 0x2) != 0)) {
/*  823 */         int nextLineHeight = gc.stringExtent("(/|,jI~`gy").y;
/*  824 */         if (this.iCurrentHeight + ptLineAndWordSize.y + nextLineHeight > printArea.height)
/*      */         {
/*      */ 
/*      */ 
/*  828 */           this.wrap = false;
/*      */         }
/*      */       }
/*      */       
/*  832 */       if ((endIndex > 0) && (outputLine.length() > 0) && (!nothingFit)) {
/*  833 */         outputLine.append(space);
/*      */       }
/*      */       
/*      */ 
/*  837 */       if ((this.wrap) && (!nothingFit) && (!bWordLargerThanWidth))
/*      */       {
/*  839 */         return 0;
/*      */       }
/*      */       
/*  842 */       outputLine.append(word.substring(0, endIndex));
/*  843 */       if (!this.wrap) {
/*  844 */         int len = outputLine.length();
/*  845 */         if (len == 0) {
/*  846 */           if (word.length() > 0) {
/*  847 */             outputLine.append(word.charAt(0));
/*  848 */           } else if (sLine.length() > 0) {
/*  849 */             outputLine.append(sLine.charAt(0));
/*      */           }
/*      */         } else {
/*  852 */           if (len > 2) {
/*  853 */             len -= 2;
/*      */           }
/*  855 */           outputLine.setLength(len);
/*  856 */           outputLine.append("…");
/*  857 */           this.cutoff = true;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  868 */       return endIndex;
/*      */     }
/*      */     
/*  871 */     lineInfo.outputLineExtent.x = ptLineAndWordSize.x;
/*  872 */     if (lineInfo.outputLineExtent.x > printArea.width) {
/*  873 */       if (space.length() > 0) {
/*  874 */         space.delete(0, space.length());
/*      */       }
/*      */       
/*  877 */       if (!this.wrap) {
/*  878 */         int len = outputLine.length();
/*  879 */         if (len == 0) {
/*  880 */           if (word.length() > 0) {
/*  881 */             outputLine.append(word.charAt(0));
/*  882 */           } else if (sLine.length() > 0) {
/*  883 */             outputLine.append(sLine.charAt(0));
/*      */           }
/*      */         } else {
/*  886 */           if (len > 2) {
/*  887 */             len -= 2;
/*      */           }
/*  889 */           outputLine.setLength(len);
/*  890 */           outputLine.append("…");
/*  891 */           this.cutoff = true;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  897 */         return -1;
/*      */       }
/*      */       
/*  900 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  905 */     if (outputLine.length() > 0) {
/*  906 */       outputLine.append(space);
/*      */     }
/*  908 */     outputLine.append(word);
/*  909 */     if (space.length() > 0) {
/*  910 */       space.delete(0, space.length());
/*      */     }
/*  912 */     space.append(' ');
/*      */     
/*      */ 
/*  915 */     return -1;
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
/*      */   private void drawLine(GC gc, LineInfo lineInfo, int swtFlags, Rectangle printArea, boolean noDraw)
/*      */   {
/*  929 */     String text = lineInfo.lineOutputed;
/*      */     
/*  931 */     if ((lineInfo.outputLineExtent.x == 0) || (lineInfo.outputLineExtent.y == 0)) {
/*  932 */       lineInfo.outputLineExtent = gc.stringExtent(text);
/*      */     }
/*      */     int x0;
/*      */     int x0;
/*  936 */     if ((swtFlags & 0x20000) != 0) {
/*  937 */       x0 = printArea.x + printArea.width - lineInfo.outputLineExtent.x; } else { int x0;
/*  938 */       if ((swtFlags & 0x1000000) != 0) {
/*  939 */         x0 = printArea.x + (printArea.width - lineInfo.outputLineExtent.x + 1) / 2;
/*      */       } else {
/*  941 */         x0 = printArea.x;
/*      */       }
/*      */     }
/*  944 */     int y0 = printArea.y;
/*      */     
/*  946 */     int lineInfoRelEndPos = lineInfo.relStartPos + lineInfo.lineOutputed.length();
/*      */     
/*  948 */     int relStartPos = lineInfo.relStartPos;
/*  949 */     int lineStartPos = 0;
/*      */     
/*  951 */     URLInfo urlInfo = null;
/*  952 */     boolean drawURL = hasHitUrl();
/*      */     
/*  954 */     if (drawURL) {
/*  955 */       URLInfo[] hitUrlInfo = getHitUrlInfo();
/*  956 */       int nextHitUrlInfoPos = 0;
/*      */       
/*  958 */       while (drawURL) {
/*  959 */         drawURL = false;
/*  960 */         for (int i = nextHitUrlInfoPos; i < hitUrlInfo.length; i++) {
/*  961 */           urlInfo = hitUrlInfo[i];
/*      */           
/*  963 */           drawURL = (urlInfo.relStartPos < lineInfoRelEndPos) && (urlInfo.relStartPos + urlInfo.titleLength > relStartPos) && (relStartPos >= lineInfo.relStartPos) && (relStartPos < lineInfoRelEndPos);
/*      */           
/*      */ 
/*      */ 
/*  967 */           if (drawURL) {
/*  968 */             nextHitUrlInfoPos = i + 1;
/*  969 */             break;
/*      */           }
/*      */         }
/*      */         
/*  973 */         if (!drawURL) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  980 */         int i = lineStartPos + urlInfo.relStartPos - relStartPos;
/*      */         
/*  982 */         if ((i > 0) && (i > lineStartPos) && (i <= text.length())) {
/*  983 */           String s = text.substring(lineStartPos, i);
/*      */           
/*  985 */           x0 += drawText(gc, s, x0, y0, lineInfo.outputLineExtent.y, null, noDraw, true).x;
/*      */           
/*  987 */           relStartPos += i - lineStartPos;
/*  988 */           lineStartPos += i - lineStartPos;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  993 */         int end = i + urlInfo.titleLength;
/*  994 */         if (i < 0) {
/*  995 */           i = 0;
/*      */         }
/*      */         
/*  998 */         if (end > text.length()) {
/*  999 */           end = text.length();
/*      */         }
/* 1001 */         String s = text.substring(i, end);
/* 1002 */         relStartPos += end - i;
/* 1003 */         lineStartPos += end - i;
/* 1004 */         Point pt = null;
/*      */         
/* 1006 */         Color fgColor = null;
/* 1007 */         if (!noDraw) {
/* 1008 */           fgColor = gc.getForeground();
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1018 */           if (urlInfo.urlColor != null) {
/* 1019 */             gc.setForeground(urlInfo.urlColor);
/* 1020 */           } else if (this.urlColor != null) {
/* 1021 */             gc.setForeground(this.urlColor);
/*      */           }
/*      */         }
/* 1024 */         if (urlInfo.hitAreas == null) {
/* 1025 */           urlInfo.hitAreas = new ArrayList(1);
/*      */         }
/* 1027 */         pt = drawText(gc, s, x0, y0, lineInfo.outputLineExtent.y, urlInfo.hitAreas, noDraw, true);
/*      */         
/* 1029 */         if (!noDraw) {
/* 1030 */           if (urlInfo.urlUnderline) {
/* 1031 */             gc.drawLine(x0, y0 + pt.y - 1, x0 + pt.x - 1, y0 + pt.y - 1);
/*      */           }
/* 1033 */           gc.setForeground(fgColor);
/*      */         }
/*      */         
/* 1036 */         if (urlInfo.hitAreas == null) {
/* 1037 */           urlInfo.hitAreas = new ArrayList(1);
/*      */         }
/*      */         
/*      */ 
/* 1041 */         x0 += pt.x;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1046 */     if (lineStartPos < text.length()) {
/* 1047 */       String s = text.substring(lineStartPos);
/* 1048 */       if (!noDraw) {
/* 1049 */         drawText(gc, s, x0, y0, lineInfo.outputLineExtent.y, null, noDraw, false);
/*      */       }
/*      */     }
/* 1052 */     printArea.y += lineInfo.outputLineExtent.y;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private Point drawText(GC gc, String s, int x, int y, int height, List<Rectangle> hitAreas, boolean nodraw, boolean calcExtent)
/*      */   {
/* 1059 */     if (this.images != null) {
/* 1060 */       int pctPos = s.indexOf('%');
/* 1061 */       int lastPos = 0;
/* 1062 */       int w = 0;
/* 1063 */       int h = 0;
/* 1064 */       while (pctPos >= 0) {
/* 1065 */         if ((pctPos >= 0) && (s.length() > pctPos + 1)) {
/* 1066 */           int imgIdx = s.charAt(pctPos + 1) - '0';
/*      */           
/* 1068 */           if ((imgIdx >= this.images.length) || (imgIdx < 0) || (this.images[imgIdx] == null)) {
/* 1069 */             String sStart = s.substring(lastPos, pctPos + 1);
/* 1070 */             Point textExtent = gc.textExtent(sStart);
/* 1071 */             int centerY = y + (height / 2 - textExtent.y / 2);
/* 1072 */             if (hitAreas != null) {
/* 1073 */               hitAreas.add(new Rectangle(x, centerY, textExtent.x, textExtent.y));
/*      */             }
/* 1075 */             if (!nodraw) {
/* 1076 */               gc.drawText(sStart, x, centerY, true);
/*      */             }
/* 1078 */             x += textExtent.x;
/* 1079 */             w += textExtent.x;
/* 1080 */             h = Math.max(h, textExtent.y);
/*      */             
/* 1082 */             lastPos = pctPos + 1;
/* 1083 */             pctPos = s.indexOf('%', pctPos + 1);
/*      */           }
/*      */           else
/*      */           {
/* 1087 */             String sStart = s.substring(lastPos, pctPos);
/* 1088 */             Point textExtent = gc.textExtent(sStart);
/* 1089 */             int centerY = y + (height / 2 - textExtent.y / 2);
/* 1090 */             if (!nodraw) {
/* 1091 */               gc.drawText(sStart, x, centerY, true);
/*      */             }
/* 1093 */             x += textExtent.x;
/* 1094 */             w += textExtent.x;
/* 1095 */             h = Math.max(h, textExtent.y);
/* 1096 */             if (hitAreas != null) {
/* 1097 */               hitAreas.add(new Rectangle(x, centerY, textExtent.x, textExtent.y));
/*      */             }
/*      */             
/*      */ 
/* 1101 */             Rectangle imgBounds = this.images[imgIdx].getBounds();
/* 1102 */             float scale = 1.0F;
/* 1103 */             if ((this.imageScales != null) && (this.imageScales.length > imgIdx)) {
/* 1104 */               scale = this.imageScales[imgIdx];
/*      */             }
/* 1106 */             int scaleImageWidth = (int)(imgBounds.width * scale);
/* 1107 */             int scaleImageHeight = (int)(imgBounds.height * scale);
/*      */             
/*      */ 
/* 1110 */             centerY = y + (height / 2 - scaleImageHeight / 2);
/* 1111 */             if (hitAreas != null) {
/* 1112 */               hitAreas.add(new Rectangle(x, centerY, scaleImageWidth, scaleImageHeight));
/*      */             }
/* 1114 */             if (!nodraw)
/*      */             {
/* 1116 */               gc.drawImage(this.images[imgIdx], 0, 0, imgBounds.width, imgBounds.height, x, centerY, scaleImageWidth, scaleImageHeight);
/*      */             }
/*      */             
/* 1119 */             x += scaleImageWidth;
/* 1120 */             w += scaleImageWidth;
/*      */             
/* 1122 */             h = Math.max(h, scaleImageHeight);
/*      */           }
/* 1124 */         } else { lastPos = pctPos + 2;
/* 1125 */           pctPos = s.indexOf('%', lastPos);
/*      */         }
/*      */       }
/* 1128 */       if (s.length() >= lastPos) {
/* 1129 */         String sEnd = s.substring(lastPos);
/* 1130 */         Point textExtent = gc.textExtent(sEnd);
/* 1131 */         int centerY = y + (height / 2 - textExtent.y / 2);
/* 1132 */         if (hitAreas != null) {
/* 1133 */           hitAreas.add(new Rectangle(x, centerY, textExtent.x, textExtent.y));
/*      */         }
/* 1135 */         if (!nodraw) {
/* 1136 */           gc.drawText(sEnd, x, centerY, true);
/*      */         }
/*      */         
/* 1139 */         w += textExtent.x;
/* 1140 */         h = Math.max(h, textExtent.y);
/*      */       }
/* 1142 */       return new Point(w, h);
/*      */     }
/*      */     
/*      */ 
/* 1146 */     if (!nodraw) {
/* 1147 */       gc.drawText(s, x, y, true);
/*      */     }
/* 1149 */     if ((!calcExtent) && (hitAreas == null)) {
/* 1150 */       return null;
/*      */     }
/* 1152 */     Point textExtent = gc.textExtent(s);
/* 1153 */     if (hitAreas != null) {
/* 1154 */       hitAreas.add(new Rectangle(x, y, textExtent.x, textExtent.y));
/*      */     }
/* 1156 */     return textExtent;
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
/*      */   public static void main(String[] args)
/*      */   {
/* 1171 */     final Display display = Display.getDefault();
/* 1172 */     final Shell shell = new Shell(display, 1264);
/*      */     
/* 1174 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*      */     
/* 1176 */     final Image[] images = { imageLoader.getImage("azureus32"), imageLoader.getImage("azureus64"), imageLoader.getImage("azureus"), imageLoader.getImage("azureus128") };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1184 */     String text = "Apple <A HREF=\"aa\">Banana</a>, Cow <A HREF=\"ss\">Dug Ergo</a>, Flip Only. test of the string printer averlongwordthisisyesindeed ∞";
/*      */     
/*      */ 
/* 1187 */     shell.setSize(500, 500);
/*      */     
/* 1189 */     GridLayout gridLayout = new GridLayout(2, false);
/* 1190 */     shell.setLayout(gridLayout);
/*      */     
/* 1192 */     int initHeight = 67;
/* 1193 */     Composite cButtons = new Composite(shell, 0);
/* 1194 */     GridData gridData = new GridData(0, 4, false, true);
/* 1195 */     cButtons.setLayoutData(gridData);
/* 1196 */     Canvas cPaint = new Canvas(shell, 536870912);
/* 1197 */     gridData = new GridData(4, 0, true, false);
/* 1198 */     gridData.heightHint = initHeight;
/* 1199 */     cPaint.setLayoutData(gridData);
/*      */     
/* 1201 */     cButtons.setLayout(new RowLayout(512));
/*      */     
/* 1203 */     Listener l = new Listener() {
/*      */       public void handleEvent(Event event) {
/* 1205 */         this.val$cPaint.redraw();
/*      */       }
/*      */       
/* 1208 */     };
/* 1209 */     final Text txtText = new Text(cButtons, 2114);
/* 1210 */     txtText.setText("Apple <A HREF=\"aa\">Banana</a>, Cow <A HREF=\"ss\">Dug Ergo</a>, Flip Only. test of the string printer averlongwordthisisyesindeed ∞");
/* 1211 */     txtText.addListener(24, l);
/* 1212 */     txtText.setLayoutData(new RowData(100, 200));
/* 1213 */     txtText.addKeyListener(new KeyListener()
/*      */     {
/*      */       public void keyReleased(KeyEvent e) {}
/*      */       
/*      */       public void keyPressed(KeyEvent e) {
/* 1218 */         if ((e.keyCode == 97) && (e.stateMask == 262144)) {
/* 1219 */           this.val$txtText.selectAll();
/*      */         }
/*      */         
/*      */       }
/* 1223 */     });
/* 1224 */     final Button btnSkipClip = new Button(cButtons, 32);
/* 1225 */     btnSkipClip.setText("Skip Clip");
/* 1226 */     btnSkipClip.setSelection(true);
/* 1227 */     btnSkipClip.addListener(13, l);
/*      */     
/* 1229 */     final Button btnFullOnly = new Button(cButtons, 32);
/* 1230 */     btnFullOnly.setText("Full Lines Only");
/* 1231 */     btnFullOnly.setSelection(true);
/* 1232 */     btnFullOnly.addListener(13, l);
/*      */     
/* 1234 */     final Combo cboVAlign = new Combo(cButtons, 8);
/* 1235 */     cboVAlign.add("Top");
/* 1236 */     cboVAlign.add("Bottom");
/* 1237 */     cboVAlign.add("None");
/* 1238 */     cboVAlign.addListener(13, l);
/* 1239 */     cboVAlign.select(0);
/*      */     
/* 1241 */     final Combo cboHAlign = new Combo(cButtons, 8);
/* 1242 */     cboHAlign.add("Left");
/* 1243 */     cboHAlign.add("Center");
/* 1244 */     cboHAlign.add("Right");
/* 1245 */     cboHAlign.add("None");
/* 1246 */     cboHAlign.addListener(13, l);
/* 1247 */     cboHAlign.select(0);
/*      */     
/* 1249 */     final Button btnWrap = new Button(cButtons, 32);
/* 1250 */     btnWrap.setText("Wrap");
/* 1251 */     btnWrap.setSelection(true);
/* 1252 */     btnWrap.addListener(13, l);
/*      */     
/* 1254 */     final Button btnGCAdvanced = new Button(cButtons, 32);
/* 1255 */     btnGCAdvanced.setText("gc.Advanced");
/* 1256 */     btnGCAdvanced.setSelection(true);
/* 1257 */     btnGCAdvanced.addListener(13, l);
/*      */     
/* 1259 */     final Spinner spinnerHeight = new Spinner(cButtons, 2048);
/* 1260 */     spinnerHeight.setSelection(initHeight);
/* 1261 */     spinnerHeight.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/* 1263 */         GridData gridData = (GridData)this.val$cPaint.getLayoutData();
/* 1264 */         gridData.heightHint = spinnerHeight.getSelection();
/* 1265 */         this.val$cPaint.setLayoutData(gridData);
/* 1266 */         shell.layout();
/*      */       }
/*      */       
/* 1269 */     });
/* 1270 */     final Label lblInfo = new Label(shell, 64);
/* 1271 */     lblInfo.setText("Welcome");
/*      */     
/*      */ 
/* 1274 */     Listener l2 = new Listener() {
/* 1275 */       GCStringPrinter.URLInfo lastHitInfo = null;
/*      */       
/*      */       public void handleEvent(Event event) {
/* 1278 */         GC gc = event.gc;
/*      */         
/* 1280 */         boolean ourGC = gc == null;
/* 1281 */         if (ourGC) {
/* 1282 */           gc = new GC(this.val$cPaint);
/*      */         }
/*      */         try {
/* 1285 */           gc.setAdvanced(btnGCAdvanced.getSelection());
/* 1286 */           GCStringPrinter sp = buildSP(gc);
/* 1287 */           Color colorURL = gc.getDevice().getSystemColor(3);
/* 1288 */           Color colorURL2 = gc.getDevice().getSystemColor(12);
/*      */           
/*      */ 
/* 1291 */           if (event.type == 5) {
/* 1292 */             Point pt = this.val$cPaint.toControl(display.getCursorLocation());
/* 1293 */             GCStringPrinter.URLInfo hitUrl = sp.getHitUrl(pt.x, pt.y);
/* 1294 */             String url1 = (hitUrl == null) || (hitUrl.url == null) ? "" : hitUrl.url;
/*      */             
/* 1296 */             String url2 = (this.lastHitInfo == null) || (this.lastHitInfo.url == null) ? "" : this.lastHitInfo.url;
/*      */             
/*      */ 
/* 1299 */             if (url1.equals(url2)) {
/*      */               return;
/*      */             }
/* 1302 */             this.val$cPaint.redraw();
/* 1303 */             this.lastHitInfo = hitUrl;
/*      */           }
/*      */           else
/*      */           {
/* 1307 */             Rectangle bounds = this.val$cPaint.getClientArea();
/*      */             
/* 1309 */             Color colorBox = gc.getDevice().getSystemColor(7);
/* 1310 */             Color colorText = gc.getDevice().getSystemColor(2);
/*      */             
/* 1312 */             gc.setForeground(colorText);
/*      */             
/* 1314 */             Point pt = this.val$cPaint.toControl(display.getCursorLocation());
/* 1315 */             sp.setUrlColor(colorURL);
/* 1316 */             GCStringPrinter.URLInfo hitUrl = sp.getHitUrl(pt.x, pt.y);
/* 1317 */             if (hitUrl != null) {
/* 1318 */               shell.setCursor(shell.getDisplay().getSystemCursor(21));
/* 1319 */               hitUrl.urlColor = colorURL2;
/*      */             } else {
/* 1321 */               shell.setCursor(null);
/*      */             }
/* 1323 */             boolean fit = sp.printString();
/*      */             
/* 1325 */             lblInfo.setText(fit ? "fit" : "no fit");
/*      */             
/* 1327 */             bounds.width -= 1;
/* 1328 */             bounds.height -= 1;
/*      */             
/* 1330 */             gc.setForeground(colorBox);
/* 1331 */             gc.drawRectangle(bounds);
/*      */             
/* 1333 */             bounds.height -= 20;
/* 1334 */             bounds.y += 10;
/* 1335 */             gc.setLineStyle(3);
/* 1336 */             gc.drawRectangle(bounds);
/*      */           }
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/* 1341 */           t.printStackTrace();
/*      */         }
/*      */         finally {
/* 1344 */           if (ourGC) {
/* 1345 */             gc.dispose();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       private GCStringPrinter buildSP(GC gc)
/*      */       {
/* 1353 */         Rectangle bounds = this.val$cPaint.getClientArea();
/* 1354 */         bounds.y += 10;
/* 1355 */         bounds.height -= 20;
/*      */         
/*      */ 
/* 1358 */         int style = btnWrap.getSelection() ? 64 : 0;
/* 1359 */         if (cboVAlign.getSelectionIndex() == 0) {
/* 1360 */           style |= 0x80;
/* 1361 */         } else if (cboVAlign.getSelectionIndex() == 1) {
/* 1362 */           style |= 0x400;
/*      */         }
/*      */         
/* 1365 */         if (cboHAlign.getSelectionIndex() == 0) {
/* 1366 */           style |= 0x4000;
/* 1367 */         } else if (cboHAlign.getSelectionIndex() == 1) {
/* 1368 */           style |= 0x1000000;
/* 1369 */         } else if (cboHAlign.getSelectionIndex() == 2) {
/* 1370 */           style |= 0x20000;
/*      */         }
/*      */         
/* 1373 */         String text = txtText.getText();
/* 1374 */         text = text.replaceAll("\r\n", "\n");
/* 1375 */         GCStringPrinter sp = new GCStringPrinter(gc, text, bounds, btnSkipClip.getSelection(), btnFullOnly.getSelection(), style);
/*      */         
/* 1377 */         sp.setImages(images);
/* 1378 */         sp.calculateMetrics();
/*      */         
/* 1380 */         return sp;
/*      */       }
/* 1382 */     };
/* 1383 */     cPaint.addListener(9, l2);
/* 1384 */     cPaint.addListener(5, l2);
/*      */     
/* 1386 */     shell.open();
/*      */     
/* 1388 */     while (!shell.isDisposed()) {
/* 1389 */       if (!display.readAndDispatch()) {
/* 1390 */         display.sleep();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public GCStringPrinter(GC gc, String string, Rectangle printArea, boolean skipClip, boolean fullLinesOnly, int swtFlags)
/*      */   {
/* 1400 */     this.gc = gc;
/* 1401 */     this.string = string;
/* 1402 */     this.printArea = printArea;
/* 1403 */     this.swtFlags = swtFlags;
/*      */     
/* 1405 */     this.printFlags = 0;
/* 1406 */     if (skipClip) {
/* 1407 */       this.printFlags |= 0x1;
/*      */     }
/* 1409 */     if (fullLinesOnly) {
/* 1410 */       this.printFlags |= 0x2;
/*      */     }
/*      */   }
/*      */   
/*      */   public GCStringPrinter(GC gc, String string, Rectangle printArea, int printFlags, int swtFlags)
/*      */   {
/* 1416 */     this.gc = gc;
/* 1417 */     this.string = string;
/* 1418 */     this.printArea = printArea;
/* 1419 */     this.swtFlags = swtFlags;
/* 1420 */     this.printFlags = printFlags;
/*      */   }
/*      */   
/*      */   public boolean printString() {
/* 1424 */     return _printString();
/*      */   }
/*      */   
/*      */   public boolean printString(int _printFlags) {
/* 1428 */     int oldPrintFlags = this.printFlags;
/* 1429 */     this.printFlags |= _printFlags;
/* 1430 */     boolean b = _printString();
/* 1431 */     this.printFlags = oldPrintFlags;
/* 1432 */     return b;
/*      */   }
/*      */   
/*      */   public void calculateMetrics() {
/* 1436 */     int oldPrintFlags = this.printFlags;
/* 1437 */     this.printFlags |= 0x4;
/* 1438 */     _printString();
/* 1439 */     this.printFlags = oldPrintFlags;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void printString(GC gc, Rectangle rectangle, int swtFlags)
/*      */   {
/* 1446 */     printString2(gc, rectangle, swtFlags);
/*      */   }
/*      */   
/*      */   public boolean printString2(GC gc, Rectangle rectangle, int swtFlags) {
/* 1450 */     this.gc = gc;
/* 1451 */     int printFlags = this.printFlags;
/* 1452 */     if (this.printArea.width == rectangle.width) {
/* 1453 */       printFlags |= 0x8;
/*      */     }
/* 1455 */     this.printArea = rectangle;
/* 1456 */     this.swtFlags = swtFlags;
/* 1457 */     return printString(printFlags);
/*      */   }
/*      */   
/*      */   public Point getCalculatedSize() {
/* 1461 */     return this.size;
/*      */   }
/*      */   
/*      */   public Color getUrlColor() {
/* 1465 */     return this.urlColor;
/*      */   }
/*      */   
/*      */   public void setUrlColor(Color urlColor) {
/* 1469 */     this.urlColor = urlColor;
/*      */   }
/*      */   
/*      */   public URLInfo getHitUrl(int x, int y) {
/* 1473 */     if ((this.listUrlInfo == null) || (this.listUrlInfo.size() == 0)) {
/* 1474 */       return null;
/*      */     }
/* 1476 */     for (Iterator i$ = this.listUrlInfo.iterator(); i$.hasNext();) { urlInfo = (URLInfo)i$.next();
/* 1477 */       if (urlInfo.hitAreas != null) {
/* 1478 */         for (Rectangle r : urlInfo.hitAreas) {
/* 1479 */           if (r.contains(x, y))
/* 1480 */             return urlInfo;
/*      */         }
/*      */       }
/*      */     }
/*      */     URLInfo urlInfo;
/* 1485 */     return null;
/*      */   }
/*      */   
/*      */   public URLInfo[] getHitUrlInfo() {
/* 1489 */     if (this.listUrlInfo == null) {
/* 1490 */       return new URLInfo[0];
/*      */     }
/* 1492 */     return (URLInfo[])this.listUrlInfo.toArray(new URLInfo[0]);
/*      */   }
/*      */   
/*      */   public boolean hasHitUrl() {
/* 1496 */     return (this.listUrlInfo != null) && (this.listUrlInfo.size() > 0);
/*      */   }
/*      */   
/*      */   public boolean isCutoff() {
/* 1500 */     return this.cutoff;
/*      */   }
/*      */   
/*      */   public void setImages(Image[] images) {
/* 1504 */     this.images = images;
/*      */   }
/*      */   
/*      */   public float[] getImageScales() {
/* 1508 */     return this.imageScales;
/*      */   }
/*      */   
/*      */   public void setImageScales(float[] imageScales) {
/* 1512 */     this.imageScales = imageScales;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getText()
/*      */   {
/* 1521 */     return this.string;
/*      */   }
/*      */   
/*      */   public boolean isWordCut() {
/* 1525 */     return this.isWordCut;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/shells/GCStringPrinter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */