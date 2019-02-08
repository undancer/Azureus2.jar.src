/*     */ package org.gudy.azureus2.ui.swt.components;
/*     */ 
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.MouseListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Cursor;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy.copyToClipProvider;
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
/*     */ public class BufferedLabel
/*     */   extends BufferedWidget
/*     */ {
/*     */   private Control label;
/*  52 */   private String value = "";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public BufferedLabel(Composite composite, int attrs)
/*     */   {
/*  59 */     super((attrs & 0x20000000) == 0 ? new Label(composite, attrs) : new DoubleBufferedLabel(composite, attrs));
/*     */     
/*  61 */     this.label = ((Control)getWidget());
/*     */     
/*  63 */     ClipboardCopy.addCopyToClipMenu(this.label, new ClipboardCopy.copyToClipProvider()
/*     */     {
/*     */ 
/*     */ 
/*     */       public String getText()
/*     */       {
/*     */ 
/*  70 */         return BufferedLabel.this.getText();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDisposed()
/*     */   {
/*  78 */     return this.label.isDisposed();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLayoutData(GridData gd)
/*     */   {
/*  85 */     this.label.setLayoutData(gd);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLayoutData(FormData gd)
/*     */   {
/*  92 */     this.label.setLayoutData(gd);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLayoutData(Object ld)
/*     */   {
/*  99 */     this.label.setLayoutData(ld);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setData(String key, Object value)
/*     */   {
/* 107 */     this.label.setData(key, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getData(String key)
/*     */   {
/* 114 */     return this.label.getData(key);
/*     */   }
/*     */   
/*     */ 
/*     */   public Control getControl()
/*     */   {
/* 120 */     return this.label;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setText(String new_value)
/*     */   {
/* 127 */     if (this.label.isDisposed()) {
/* 128 */       return;
/*     */     }
/*     */     
/* 131 */     if (new_value == this.value)
/*     */     {
/* 133 */       return;
/*     */     }
/*     */     
/* 136 */     if ((new_value != null) && (this.value != null) && (new_value.equals(this.value)))
/*     */     {
/*     */ 
/*     */ 
/* 140 */       return;
/*     */     }
/*     */     
/* 143 */     this.value = new_value;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 149 */     String fixed_value = this.value == null ? "" : this.value.replaceAll("&", "&&");
/*     */     
/* 151 */     if ((this.label instanceof Label))
/*     */     {
/* 153 */       ((Label)this.label).setText(fixed_value);
/*     */     }
/*     */     else {
/* 156 */       ((DoubleBufferedLabel)this.label).setText(fixed_value);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLink(String url)
/*     */   {
/* 164 */     Object[] existing = (Object[])this.label.getData();
/*     */     
/* 166 */     if ((existing == null) && (url == null))
/*     */     {
/* 168 */       return;
/*     */     }
/* 170 */     if ((existing != null) && (url != null))
/*     */     {
/* 172 */       if (((String[])(String[])existing)[0].equals(url))
/*     */       {
/* 174 */         return;
/*     */       }
/*     */     }
/*     */     
/* 178 */     if (url == null) {
/* 179 */       this.label.setData(null);
/* 180 */       this.label.setCursor(null);
/* 181 */       this.label.setForeground(null);
/* 182 */       this.label.setToolTipText(null);
/*     */     } else {
/* 184 */       final String[] data = { url };
/*     */       
/* 186 */       this.label.setData(data);
/*     */       
/* 188 */       this.label.setToolTipText(url);
/*     */       
/* 190 */       this.label.setCursor(this.label.getDisplay().getSystemCursor(21));
/* 191 */       this.label.setForeground(Colors.blue);
/* 192 */       this.label.addMouseListener(new MouseAdapter() {
/*     */         public void mouseDoubleClick(MouseEvent arg0) {
/* 194 */           showURL((Label)arg0.widget);
/*     */         }
/*     */         
/* 197 */         public void mouseUp(MouseEvent arg0) { showURL((Label)arg0.widget); }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         protected void showURL(Label label)
/*     */         {
/* 204 */           if (label.getData() == data)
/*     */           {
/* 206 */             Utils.launch(data[0]);
/*     */           }
/*     */           else
/*     */           {
/* 210 */             label.removeMouseListener(this);
/*     */           }
/*     */           
/*     */         }
/* 214 */       });
/* 215 */       ClipboardCopy.addCopyToClipMenu(this.label);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getText() {
/* 220 */     return this.value == null ? "" : this.value;
/*     */   }
/*     */   
/*     */   public void addMouseListener(MouseListener listener) {
/* 224 */     this.label.addMouseListener(listener);
/*     */   }
/*     */   
/*     */   public void setForeground(Color color) {
/* 228 */     this.label.setForeground(color);
/*     */   }
/*     */   
/*     */   public void setCursor(Cursor cursor) {
/* 232 */     this.label.setCursor(cursor);
/*     */   }
/*     */   
/*     */   public void setToolTipText(String toolTipText) {
/* 236 */     this.label.setToolTipText(toolTipText);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/BufferedLabel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */