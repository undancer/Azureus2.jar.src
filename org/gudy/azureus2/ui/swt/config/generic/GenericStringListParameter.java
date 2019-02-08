/*     */ package org.gudy.azureus2.ui.swt.config.generic;
/*     */ 
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.List;
/*     */ import org.eclipse.swt.widgets.Listener;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GenericStringListParameter
/*     */ {
/*     */   private GenericParameterAdapter adapter;
/*     */   private Control list;
/*     */   private final String name;
/*     */   private final String default_value;
/*     */   private final String[] values;
/*     */   private final boolean useCombo;
/*     */   
/*     */   public GenericStringListParameter(GenericParameterAdapter adapter, Composite composite, String _name, String[] labels, String[] values, boolean bUseCombo)
/*     */   {
/*  56 */     this(adapter, composite, _name, adapter.getStringListValue(_name), labels, values, bUseCombo);
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
/*     */   public GenericStringListParameter(GenericParameterAdapter adapter, Composite composite, String _name, String[] labels, String[] values)
/*     */   {
/*  69 */     this(adapter, composite, _name, adapter.getStringListValue(_name), labels, values, true);
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
/*     */   public GenericStringListParameter(GenericParameterAdapter adapter, Composite composite, String _name, String defaultValue, String[] labels, String[] values)
/*     */   {
/*  83 */     this(adapter, composite, _name, defaultValue, labels, values, true);
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
/*     */   public GenericStringListParameter(GenericParameterAdapter _adapter, Composite composite, String _name, String defaultValue, String[] labels, String[] values, final boolean bUseCombo)
/*     */   {
/*  98 */     this.adapter = _adapter;
/*  99 */     this.name = _name;
/* 100 */     this.default_value = defaultValue;
/* 101 */     this.values = values;
/* 102 */     this.useCombo = bUseCombo;
/*     */     
/* 104 */     if (labels.length != values.length) {
/* 105 */       return;
/*     */     }
/*     */     
/* 108 */     String value = this.adapter.getStringListValue(this.name, defaultValue);
/* 109 */     int index = findIndex(value, values);
/* 110 */     if (bUseCombo) {
/* 111 */       this.list = new Combo(composite, 12);
/*     */     } else {
/* 113 */       this.list = new List(composite, 2820)
/*     */       {
/*     */         public void checkSubclass() {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public Point computeSize(int wHint, int hHint, boolean changed)
/*     */         {
/* 122 */           if ((hHint == 0) && (!isVisible())) {
/* 123 */             return new Point(0, 0);
/*     */           }
/*     */           
/* 126 */           Point pt = super.computeSize(wHint, hHint, changed);
/*     */           
/* 128 */           if (hHint == -1) {
/* 129 */             Object ld = getLayoutData();
/* 130 */             if (((ld instanceof GridData)) && 
/* 131 */               (((GridData)ld).grabExcessVerticalSpace)) {
/* 132 */               pt.y = 20;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 137 */           return pt;
/*     */         }
/*     */       };
/*     */     }
/*     */     
/* 142 */     for (int i = 0; i < labels.length; i++) {
/* 143 */       if (bUseCombo) {
/* 144 */         ((Combo)this.list).add(labels[i]);
/*     */       } else {
/* 146 */         ((List)this.list).add(labels[i]);
/*     */       }
/*     */     }
/* 149 */     setIndex(index);
/*     */     
/* 151 */     this.list.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) { int index;
/*     */         int index;
/* 154 */         if (bUseCombo) {
/* 155 */           index = ((Combo)GenericStringListParameter.this.list).getSelectionIndex();
/*     */         } else
/* 157 */           index = ((List)GenericStringListParameter.this.list).getSelectionIndex();
/* 158 */         GenericStringListParameter.this.setIndex(index);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int findIndex(String value, String[] values)
/*     */   {
/* 172 */     for (int i = 0; i < values.length; i++) {
/* 173 */       if (values[i].equals(value))
/* 174 */         return i;
/*     */     }
/* 176 */     return -1;
/*     */   }
/*     */   
/*     */   protected void setIndex(final int index) {
/* 180 */     if (index < 0) {
/* 181 */       this.adapter.setStringListValue(this.name, null);
/*     */       
/* 183 */       String defValue = this.adapter.getStringListValue(this.name);
/* 184 */       int i = findIndex(defValue, this.values);
/* 185 */       if (i >= 0)
/*     */       {
/* 187 */         setIndex(i);
/*     */       } else {
/* 189 */         Utils.execSWTThread(new AERunnable() {
/*     */           public void runSupport() {
/* 191 */             if ((GenericStringListParameter.this.list == null) || (GenericStringListParameter.this.list.isDisposed())) {
/* 192 */               return;
/*     */             }
/*     */             
/* 195 */             if (GenericStringListParameter.this.useCombo) {
/* 196 */               ((Combo)GenericStringListParameter.this.list).deselectAll();
/*     */             } else {
/* 198 */               ((List)GenericStringListParameter.this.list).deselectAll();
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/* 203 */       return;
/*     */     }
/*     */     
/* 206 */     String selected_value = this.values[index];
/*     */     
/* 208 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 210 */         if ((GenericStringListParameter.this.list == null) || (GenericStringListParameter.this.list.isDisposed())) {
/* 211 */           return;
/*     */         }
/*     */         
/* 214 */         if (GenericStringListParameter.this.useCombo) {
/* 215 */           if (((Combo)GenericStringListParameter.this.list).getSelectionIndex() != index) {
/* 216 */             ((Combo)GenericStringListParameter.this.list).select(index);
/*     */           }
/*     */         }
/* 219 */         else if (((List)GenericStringListParameter.this.list).getSelectionIndex() != index) {
/* 220 */           ((List)GenericStringListParameter.this.list).select(index);
/*     */         }
/*     */       }
/*     */     });
/*     */     
/*     */ 
/* 226 */     if (!this.adapter.getStringListValue(this.name).equals(selected_value)) {
/* 227 */       this.adapter.setStringListValue(this.name, selected_value);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setLayoutData(Object layoutData) {
/* 232 */     Utils.adjustPXForDPI(layoutData);
/* 233 */     this.list.setLayoutData(layoutData);
/*     */   }
/*     */   
/*     */   public Control getControl() {
/* 237 */     return this.list;
/*     */   }
/*     */   
/*     */   public String getValue() {
/* 241 */     return this.adapter.getStringListValue(this.name, this.default_value);
/*     */   }
/*     */   
/*     */   public void setValue(Object value) {
/* 245 */     if ((value instanceof String)) {
/* 246 */       String s = (String)value;
/* 247 */       setIndex(findIndex(s, this.values));
/*     */     }
/*     */   }
/*     */   
/*     */   public Object getValueObject() {
/* 252 */     return getValue();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/generic/GenericStringListParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */