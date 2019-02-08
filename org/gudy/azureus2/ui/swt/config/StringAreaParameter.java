/*     */ package org.gudy.azureus2.ui.swt.config;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.eclipse.swt.events.KeyAdapter;
/*     */ import org.eclipse.swt.events.KeyEvent;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public class StringAreaParameter
/*     */   extends Parameter
/*     */ {
/*     */   private String name;
/*     */   private Text inputField;
/*     */   private String defaultValue;
/*     */   
/*     */   public StringAreaParameter(Composite composite, String name)
/*     */   {
/*  41 */     this(composite, name, COConfigurationManager.getStringParameter(name));
/*     */   }
/*     */   
/*     */   public StringAreaParameter(Composite composite, final String name, String defaultValue) {
/*  45 */     super(name);
/*  46 */     this.name = name;
/*  47 */     this.defaultValue = defaultValue;
/*  48 */     this.inputField = new Text(composite, 2626)
/*     */     {
/*     */       public void checkSubclass() {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public Point computeSize(int wHint, int hHint, boolean changed)
/*     */       {
/*  58 */         if ((hHint == 0) && (!isVisible()))
/*     */         {
/*  60 */           return new Point(0, 0);
/*     */         }
/*  62 */         Point pt = super.computeSize(wHint, hHint, changed);
/*     */         
/*  64 */         if (wHint == -1) {
/*  65 */           Object ld = getLayoutData();
/*  66 */           if (((ld instanceof GridData)) && 
/*  67 */             (((GridData)ld).grabExcessHorizontalSpace)) {
/*  68 */             pt.x = 10;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*  74 */         return pt;
/*     */       }
/*     */       
/*  77 */     };
/*  78 */     String value = COConfigurationManager.getStringParameter(name, defaultValue);
/*  79 */     this.inputField.setText(value);
/*  80 */     this.inputField.addListener(25, new Listener() {
/*     */       public void handleEvent(Event e) {
/*  82 */         e.doit = COConfigurationManager.verifyParameter(name, e.text);
/*     */       }
/*     */       
/*  85 */     });
/*  86 */     this.inputField.addListener(16, new Listener() {
/*     */       public void handleEvent(Event event) {
/*  88 */         StringAreaParameter.this.checkValue();
/*     */       }
/*     */       
/*  91 */     });
/*  92 */     this.inputField.addKeyListener(new KeyAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void keyPressed(KeyEvent event)
/*     */       {
/*     */ 
/*  99 */         int key = event.character;
/*     */         
/* 101 */         if ((key <= 26) && (key > 0))
/*     */         {
/* 103 */           key += 96;
/*     */         }
/*     */         
/* 106 */         if ((key == 97) && (event.stateMask == SWT.MOD1))
/*     */         {
/* 108 */           event.doit = false;
/*     */           
/* 110 */           StringAreaParameter.this.inputField.selectAll();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getPreferredHeight(int line_count)
/*     */   {
/* 120 */     return this.inputField.getLineHeight() * line_count;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void checkValue()
/*     */   {
/* 126 */     String old_value = COConfigurationManager.getStringParameter(this.name, this.defaultValue);
/* 127 */     String new_value = this.inputField.getText();
/*     */     
/* 129 */     if (!old_value.equals(new_value)) {
/* 130 */       COConfigurationManager.setParameter(this.name, new_value);
/*     */       
/* 132 */       if (this.change_listeners != null) {
/* 133 */         for (int i = 0; i < this.change_listeners.size(); i++) {
/* 134 */           ((ParameterChangeListener)this.change_listeners.get(i)).parameterChanged(this, false);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void setLayoutData(Object layoutData) {
/* 141 */     Utils.adjustPXForDPI(layoutData);
/* 142 */     this.inputField.setLayoutData(layoutData);
/*     */   }
/*     */   
/*     */   public void setValue(final String value) {
/* 146 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 148 */         if ((StringAreaParameter.this.inputField == null) || (StringAreaParameter.this.inputField.isDisposed()) || (StringAreaParameter.this.inputField.getText().equals(value)))
/*     */         {
/* 150 */           return;
/*     */         }
/* 152 */         StringAreaParameter.this.inputField.setText(value);
/*     */       }
/*     */     });
/*     */     
/* 156 */     if (!COConfigurationManager.getStringParameter(this.name).equals(value)) {
/* 157 */       COConfigurationManager.setParameter(this.name, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getValue() {
/* 162 */     return this.inputField.getText();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Control getControl()
/*     */   {
/* 169 */     return this.inputField;
/*     */   }
/*     */   
/*     */   public void setValue(Object value) {
/* 173 */     if ((value instanceof String)) {
/* 174 */       setValue((String)value);
/*     */     }
/*     */   }
/*     */   
/*     */   public Object getValueObject() {
/* 179 */     return COConfigurationManager.getStringParameter(this.name);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/StringAreaParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */