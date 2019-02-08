/*     */ package org.gudy.azureus2.ui.swt.config;
/*     */ 
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
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
/*     */ public class IntListParameter
/*     */   extends Parameter
/*     */ {
/*     */   Combo list;
/*     */   private final int[] values;
/*     */   private final String name;
/*     */   
/*     */   public IntListParameter(Composite composite, String name, String[] labels, int[] values)
/*     */   {
/*  45 */     this(composite, name, COConfigurationManager.getIntParameter(name), labels, values);
/*     */   }
/*     */   
/*     */   public IntListParameter(Composite composite, String name, int defaultValue, String[] labels, int[] values)
/*     */   {
/*  50 */     super(name);
/*  51 */     this.name = name;
/*  52 */     this.values = values;
/*     */     
/*  54 */     if (labels.length != values.length)
/*  55 */       return;
/*  56 */     int value = COConfigurationManager.getIntParameter(name, defaultValue);
/*  57 */     int index = findIndex(value, values);
/*  58 */     this.list = new Combo(composite, 12);
/*  59 */     for (int i = 0; i < labels.length; i++) {
/*  60 */       this.list.add(labels[i]);
/*     */     }
/*     */     
/*  63 */     setIndex(index);
/*     */     
/*  65 */     this.list.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/*  67 */         IntListParameter.this.setIndex(IntListParameter.this.list.getSelectionIndex());
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setIndex(final int index)
/*     */   {
/*  77 */     int selected_value = this.values[index];
/*     */     
/*  79 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/*  81 */         if ((IntListParameter.this.list == null) || (IntListParameter.this.list.isDisposed())) {
/*  82 */           return;
/*     */         }
/*     */         
/*  85 */         if (IntListParameter.this.list.getSelectionIndex() != index) {
/*  86 */           IntListParameter.this.list.select(index);
/*     */         }
/*     */       }
/*     */     });
/*     */     
/*  91 */     if (COConfigurationManager.getIntParameter(this.name) != selected_value) {
/*  92 */       COConfigurationManager.setParameter(this.name, selected_value);
/*     */     }
/*     */   }
/*     */   
/*     */   private int findIndex(int value, int[] values) {
/*  97 */     for (int i = 0; i < values.length; i++) {
/*  98 */       if (values[i] == value)
/*  99 */         return i;
/*     */     }
/* 101 */     return 0;
/*     */   }
/*     */   
/*     */   public void setLayoutData(Object layoutData)
/*     */   {
/* 106 */     Utils.adjustPXForDPI(layoutData);
/* 107 */     this.list.setLayoutData(layoutData);
/*     */   }
/*     */   
/*     */   public Control getControl() {
/* 111 */     return this.list;
/*     */   }
/*     */   
/*     */   public void setValue(Object value) {
/* 115 */     if ((value instanceof Number)) {
/* 116 */       int i = ((Number)value).intValue();
/* 117 */       setIndex(findIndex(i, this.values));
/*     */     }
/*     */   }
/*     */   
/*     */   public Object getValueObject() {
/* 122 */     return new Integer(COConfigurationManager.getIntParameter(this.name));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/IntListParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */