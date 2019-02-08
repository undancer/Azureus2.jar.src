/*     */ package org.gudy.azureus2.ui.swt.config;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ 
/*     */ 
/*     */ public class StringParameter
/*     */   extends Parameter
/*     */ {
/*     */   private String name;
/*     */   private Text inputField;
/*     */   private String defaultValue;
/*     */   
/*     */   public StringParameter(Composite composite, String name)
/*     */   {
/*  41 */     this(composite, name, COConfigurationManager.getStringParameter(name));
/*     */   }
/*     */   
/*     */   public StringParameter(Composite composite, String name, boolean generateIntermediateEvents) {
/*  45 */     this(composite, name, COConfigurationManager.getStringParameter(name), generateIntermediateEvents);
/*     */   }
/*     */   
/*  48 */   public StringParameter(Composite composite, String name, String defaultValue) { this(composite, name, defaultValue, true); }
/*     */   
/*     */   public StringParameter(Composite composite, final String name, String defaultValue, boolean generateIntermediateEvents) {
/*  51 */     super(name);
/*  52 */     this.name = name;
/*  53 */     this.defaultValue = defaultValue;
/*  54 */     this.inputField = new Text(composite, 2048)
/*     */     {
/*     */       public void checkSubclass() {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public Point computeSize(int wHint, int hHint, boolean changed)
/*     */       {
/*  64 */         if ((hHint == 0) && (!isVisible()))
/*     */         {
/*  66 */           return new Point(0, 0);
/*     */         }
/*  68 */         Point pt = super.computeSize(wHint, hHint, changed);
/*     */         
/*  70 */         if (wHint == -1) {
/*  71 */           Object ld = getLayoutData();
/*  72 */           if (((ld instanceof GridData)) && 
/*  73 */             (((GridData)ld).grabExcessHorizontalSpace)) {
/*  74 */             pt.x = 10;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*  80 */         return pt;
/*     */       }
/*  82 */     };
/*  83 */     String value = COConfigurationManager.getStringParameter(name, defaultValue);
/*     */     try {
/*  85 */       this.inputField.setText(value);
/*     */     } catch (IllegalArgumentException e) {
/*  87 */       Debug.out("IllegalArgumentException for value of " + name);
/*     */     }
/*  89 */     this.inputField.addListener(25, new Listener() {
/*     */       public void handleEvent(Event e) {
/*  91 */         e.doit = COConfigurationManager.verifyParameter(name, e.text);
/*     */       }
/*     */     });
/*     */     
/*  95 */     if (generateIntermediateEvents) {
/*  96 */       this.inputField.addListener(24, new Listener() {
/*     */         public void handleEvent(Event event) {
/*  98 */           StringParameter.this.checkValue();
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 103 */     this.inputField.addListener(16, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 105 */         StringParameter.this.checkValue();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected void checkValue()
/*     */   {
/* 113 */     String old_value = COConfigurationManager.getStringParameter(this.name, this.defaultValue);
/* 114 */     String new_value = this.inputField.getText();
/*     */     
/* 116 */     if (!old_value.equals(new_value)) {
/* 117 */       COConfigurationManager.setParameter(this.name, new_value);
/*     */       
/* 119 */       if (this.change_listeners != null) {
/* 120 */         for (int i = 0; i < this.change_listeners.size(); i++) {
/* 121 */           ((ParameterChangeListener)this.change_listeners.get(i)).parameterChanged(this, false);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void setLayoutData(Object layoutData) {
/* 128 */     Utils.adjustPXForDPI(layoutData);
/* 129 */     this.inputField.setLayoutData(layoutData);
/*     */   }
/*     */   
/*     */   public void setValue(final String value) {
/* 133 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 135 */         if ((StringParameter.this.inputField == null) || (StringParameter.this.inputField.isDisposed()) || (StringParameter.this.inputField.getText().equals(value)))
/*     */         {
/* 137 */           return;
/*     */         }
/* 139 */         StringParameter.this.inputField.setText(value);
/*     */       }
/*     */     });
/*     */     
/* 143 */     if (!COConfigurationManager.getStringParameter(this.name).equals(value)) {
/* 144 */       COConfigurationManager.setParameter(this.name, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getValue() {
/* 149 */     return this.inputField.getText();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Control getControl()
/*     */   {
/* 156 */     return this.inputField;
/*     */   }
/*     */   
/*     */   public void setValue(Object value) {
/* 160 */     if ((value instanceof String)) {
/* 161 */       setValue((String)value);
/*     */     }
/*     */   }
/*     */   
/*     */   public Object getValueObject() {
/* 166 */     return COConfigurationManager.getStringParameter(this.name);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/StringParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */