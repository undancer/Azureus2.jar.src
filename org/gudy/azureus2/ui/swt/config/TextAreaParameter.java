/*     */ package org.gudy.azureus2.ui.swt.config;
/*     */ 
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.events.KeyAdapter;
/*     */ import org.eclipse.swt.events.KeyEvent;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.ScrollBar;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.plugins.ui.components.UIPropertyChangeEvent;
/*     */ import org.gudy.azureus2.plugins.ui.components.UIPropertyChangeListener;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.UITextAreaImpl;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy.copyToClipProvider;
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
/*     */ public class TextAreaParameter
/*     */   extends Parameter
/*     */   implements UIPropertyChangeListener
/*     */ {
/*     */   private UITextAreaImpl ui_text_area;
/*     */   private StyledText text_area;
/*     */   
/*     */   public TextAreaParameter(Composite composite, UITextAreaImpl _ui_text_area)
/*     */   {
/*  49 */     super("");
/*     */     
/*  51 */     this.ui_text_area = _ui_text_area;
/*     */     
/*  53 */     this.text_area = new StyledText(composite, 2824);
/*     */     
/*  55 */     ClipboardCopy.addCopyToClipMenu(this.text_area, new ClipboardCopy.copyToClipProvider()
/*     */     {
/*     */ 
/*     */ 
/*     */       public String getText()
/*     */       {
/*     */ 
/*  62 */         return TextAreaParameter.this.text_area.getText().trim();
/*     */       }
/*     */       
/*  65 */     });
/*  66 */     this.text_area.addKeyListener(new KeyAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void keyPressed(KeyEvent event)
/*     */       {
/*     */ 
/*  73 */         int key = event.character;
/*     */         
/*  75 */         if ((key <= 26) && (key > 0))
/*     */         {
/*  77 */           key += 96;
/*     */         }
/*     */         
/*  80 */         if ((key == 97) && (event.stateMask == SWT.MOD1))
/*     */         {
/*  82 */           event.doit = false;
/*     */           
/*  84 */           TextAreaParameter.this.text_area.selectAll();
/*     */         }
/*     */         
/*     */       }
/*  88 */     });
/*  89 */     this.text_area.setText(this.ui_text_area.getText());
/*     */     
/*  91 */     this.ui_text_area.addPropertyChangeListener(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLayoutData(Object layoutData)
/*     */   {
/*  98 */     if ((layoutData instanceof GridData))
/*     */     {
/* 100 */       GridData gd = (GridData)layoutData;
/*     */       
/* 102 */       Integer hhint = (Integer)this.ui_text_area.getProperty("hhint");
/*     */       
/* 104 */       if (hhint != null)
/*     */       {
/* 106 */         gd.heightHint = hhint.intValue();
/*     */       }
/*     */     }
/*     */     
/* 110 */     Utils.adjustPXForDPI(layoutData);
/* 111 */     this.text_area.setLayoutData(layoutData);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Control getControl()
/*     */   {
/* 118 */     return this.text_area;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setValue(Object value) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void propertyChanged(final UIPropertyChangeEvent ev)
/*     */   {
/* 131 */     if ((this.text_area.isDisposed()) || (!this.ui_text_area.isVisible()))
/*     */     {
/* 133 */       this.ui_text_area.removePropertyChangeListener(this);
/*     */       
/* 135 */       return;
/*     */     }
/*     */     
/* 138 */     this.text_area.getDisplay().asyncExec(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 144 */         if ((TextAreaParameter.this.text_area.isDisposed()) || (!TextAreaParameter.this.ui_text_area.isVisible()))
/*     */         {
/* 146 */           TextAreaParameter.this.ui_text_area.removePropertyChangeListener(TextAreaParameter.this);
/*     */           
/* 148 */           return;
/*     */         }
/*     */         
/* 151 */         String old_value = (String)ev.getOldPropertyValue();
/* 152 */         String new_value = (String)ev.getNewPropertyValue();
/*     */         
/* 154 */         ScrollBar bar = TextAreaParameter.this.text_area.getVerticalBar();
/*     */         
/* 156 */         boolean max = bar.getSelection() == bar.getMaximum() - bar.getThumb();
/*     */         
/* 158 */         int lineOffset = TextAreaParameter.this.text_area.getLineCount() - TextAreaParameter.this.text_area.getTopIndex();
/*     */         
/* 160 */         if (new_value.startsWith(old_value))
/*     */         {
/* 162 */           String toAppend = new_value.substring(old_value.length());
/*     */           
/* 164 */           if (toAppend.length() == 0)
/*     */           {
/* 166 */             return;
/*     */           }
/*     */           
/* 169 */           StringBuilder builder = new StringBuilder(toAppend.length());
/*     */           
/* 171 */           String[] lines = toAppend.split("\n");
/*     */           
/*     */ 
/* 174 */           for (int i = 0; i < lines.length; i++)
/*     */           {
/* 176 */             String line = lines[i];
/*     */             
/* 178 */             builder.append("\n");
/* 179 */             builder.append(line);
/*     */           }
/*     */           
/* 182 */           TextAreaParameter.this.text_area.append(builder.toString());
/*     */         }
/*     */         else
/*     */         {
/* 186 */           StringBuilder builder = new StringBuilder(new_value.length());
/*     */           
/* 188 */           String[] lines = new_value.split("\n");
/*     */           
/* 190 */           for (int i = 0; i < lines.length; i++)
/*     */           {
/* 192 */             String line = lines[i];
/*     */             
/* 194 */             if (line != lines[0])
/*     */             {
/* 196 */               builder.append("\n");
/*     */             }
/*     */             
/* 199 */             builder.append(line);
/*     */           }
/*     */           
/* 202 */           TextAreaParameter.this.text_area.setText(builder.toString());
/*     */         }
/*     */         
/* 205 */         if (max)
/*     */         {
/* 207 */           bar.setSelection(bar.getMaximum() - bar.getThumb());
/*     */           
/* 209 */           TextAreaParameter.this.text_area.setTopIndex(TextAreaParameter.this.text_area.getLineCount() - lineOffset);
/*     */           
/* 211 */           TextAreaParameter.this.text_area.redraw();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/TextAreaParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */