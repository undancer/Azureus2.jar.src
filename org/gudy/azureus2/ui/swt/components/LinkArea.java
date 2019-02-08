/*     */ package org.gudy.azureus2.ui.swt.components;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.custom.StyleRange;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.graphics.Cursor;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.html.HTMLUtils;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
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
/*     */ public class LinkArea
/*     */ {
/*     */   private StyledText styled_text;
/*  49 */   private ArrayList links = new ArrayList();
/*     */   
/*     */   private int ofs;
/*     */   
/*  53 */   private String relative_url_base = "";
/*     */   
/*     */ 
/*     */ 
/*     */   public LinkArea(Composite comp)
/*     */   {
/*  59 */     this.styled_text = new StyledText(comp, 2824);
/*  60 */     this.styled_text.setWordWrap(true);
/*     */     
/*  62 */     this.styled_text.addListener(4, new Listener() {
/*     */       public void handleEvent(Event event) {
/*  64 */         if (LinkArea.this.links.size() == 0) {
/*  65 */           return;
/*     */         }
/*     */         try {
/*  68 */           int ofs = LinkArea.this.styled_text.getOffsetAtLocation(new Point(event.x, event.y));
/*  69 */           for (int i = 0; i < LinkArea.this.links.size(); i++) {
/*  70 */             LinkArea.linkInfo linkInfo = (LinkArea.linkInfo)LinkArea.this.links.get(i);
/*  71 */             if ((ofs >= linkInfo.ofsStart) && (ofs <= linkInfo.ofsEnd)) {
/*  72 */               Utils.launch(linkInfo.url);
/*  73 */               break;
/*     */             }
/*     */             
/*     */           }
/*     */           
/*     */         }
/*     */         catch (Exception e) {}
/*     */       }
/*  81 */     });
/*  82 */     final Cursor handCursor = new Cursor(comp.getDisplay(), 21);
/*  83 */     this.styled_text.addListener(5, new Listener() {
/*  84 */       Cursor curCursor = null;
/*     */       
/*     */       public void handleEvent(Event event) {
/*  87 */         if (LinkArea.this.links.size() == 0) {
/*  88 */           return;
/*     */         }
/*  90 */         boolean onLink = false;
/*     */         try {
/*  92 */           int ofs = LinkArea.this.styled_text.getOffsetAtLocation(new Point(event.x, event.y));
/*  93 */           for (int i = 0; i < LinkArea.this.links.size(); i++) {
/*  94 */             LinkArea.linkInfo linkInfo = (LinkArea.linkInfo)LinkArea.this.links.get(i);
/*  95 */             if ((ofs >= linkInfo.ofsStart) && (ofs <= linkInfo.ofsEnd)) {
/*  96 */               onLink = true;
/*  97 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Exception e) {}
/*     */         
/*     */         try
/*     */         {
/* 105 */           Cursor cursor = onLink ? handCursor : null;
/* 106 */           if (this.curCursor != cursor) {
/* 107 */             LinkArea.this.styled_text.setCursor(cursor);
/* 108 */             this.curCursor = cursor;
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         catch (Exception e) {}
/*     */       }
/* 115 */     });
/* 116 */     this.styled_text.addListener(12, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 118 */         LinkArea.this.styled_text.setCursor(null);
/* 119 */         handCursor.dispose();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public Composite getComponent()
/*     */   {
/* 127 */     return this.styled_text;
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset()
/*     */   {
/* 133 */     if (this.styled_text.isDisposed()) {
/* 134 */       return;
/*     */     }
/*     */     
/* 137 */     this.ofs = 0;
/* 138 */     this.styled_text.setText("");
/*     */     
/* 140 */     this.links.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setRelativeURLBase(String str)
/*     */   {
/* 147 */     this.relative_url_base = str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addLine(String line)
/*     */   {
/* 154 */     if (this.styled_text.isDisposed()) {
/* 155 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 159 */       line = HTMLUtils.expand(line);
/*     */       
/* 161 */       Object[] url_details = HTMLUtils.getLinks(line);
/*     */       
/* 163 */       String modified_line = (String)url_details[0];
/*     */       
/* 165 */       this.styled_text.append(modified_line + "\n");
/*     */       
/* 167 */       List urls = (List)url_details[1];
/*     */       
/* 169 */       for (int i = 0; i < urls.size(); i++) {
/* 170 */         Object[] entry = (Object[])urls.get(i);
/*     */         
/* 172 */         String url = (String)entry[0];
/*     */         
/* 174 */         int[] det = (int[])entry[1];
/*     */         
/* 176 */         if ((!url.toLowerCase().startsWith("http")) && (this.relative_url_base.length() > 0))
/*     */         {
/* 178 */           url = this.relative_url_base + url;
/*     */         }
/*     */         
/* 181 */         linkInfo info = new linkInfo(this.ofs + det[0], this.ofs + det[0] + det[1], url);
/*     */         
/* 183 */         this.links.add(info);
/*     */         
/* 185 */         StyleRange sr = new StyleRange();
/* 186 */         sr.start = info.ofsStart;
/* 187 */         sr.length = (info.ofsEnd - info.ofsStart);
/* 188 */         sr.underline = true;
/* 189 */         sr.foreground = Colors.blue;
/*     */         
/* 191 */         this.styled_text.setStyleRange(sr);
/*     */       }
/*     */       
/* 194 */       this.ofs += modified_line.length() + 1;
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */ 
/* 200 */       Debug.printStackTrace(e);
/*     */       
/* 202 */       this.styled_text.append(line + "\n");
/*     */     }
/*     */   }
/*     */   
/*     */   public static class linkInfo {
/*     */     int ofsStart;
/*     */     int ofsEnd;
/*     */     String url;
/*     */     
/*     */     linkInfo(int s, int e, String url) {
/* 212 */       this.ofsStart = s;
/* 213 */       this.ofsEnd = e;
/* 214 */       this.url = url;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/LinkArea.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */