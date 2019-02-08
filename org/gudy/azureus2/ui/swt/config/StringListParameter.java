/*     */ package org.gudy.azureus2.ui.swt.config;
/*     */ 
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.GridData;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StringListParameter
/*     */   extends Parameter
/*     */ {
/*     */   Control list;
/*     */   final String name;
/*     */   final String default_value;
/*     */   private final String[] values;
/*     */   private final boolean useCombo;
/*     */   
/*     */   public StringListParameter(Composite composite, String _name, String[] labels, String[] values, boolean bUseCombo)
/*     */   {
/*  56 */     this(composite, _name, COConfigurationManager.getStringParameter(_name), labels, values, bUseCombo);
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
/*     */   public StringListParameter(Composite composite, String _name, String[] labels, String[] values)
/*     */   {
/*  69 */     this(composite, _name, COConfigurationManager.getStringParameter(_name), labels, values, true);
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
/*     */   public StringListParameter(Composite composite, String _name, String defaultValue, String[] labels, String[] values)
/*     */   {
/*  83 */     this(composite, _name, defaultValue, labels, values, true);
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
/*     */   public StringListParameter(Composite composite, String _name, String defaultValue, String[] labels, String[] values, final boolean bUseCombo)
/*     */   {
/*  98 */     super(_name);
/*  99 */     this.name = _name;
/* 100 */     this.default_value = defaultValue;
/* 101 */     this.values = values;
/* 102 */     this.useCombo = bUseCombo;
/*     */     
/* 104 */     if (labels.length != values.length) {
/* 105 */       return;
/*     */     }
/*     */     
/* 108 */     String value = COConfigurationManager.getStringParameter(this.name, defaultValue);
/* 109 */     int index = findIndex(value, values);
/* 110 */     if (bUseCombo) {
/* 111 */       this.list = new Combo(composite, 12);
/*     */     } else {
/* 113 */       this.list = new org.eclipse.swt.widgets.List(composite, 2820)
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
/* 146 */         ((org.eclipse.swt.widgets.List)this.list).add(labels[i]);
/*     */       }
/*     */     }
/* 149 */     setIndex(index);
/*     */     
/* 151 */     this.list.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) { int index;
/*     */         int index;
/* 154 */         if (bUseCombo) {
/* 155 */           index = ((Combo)StringListParameter.this.list).getSelectionIndex();
/*     */         } else
/* 157 */           index = ((org.eclipse.swt.widgets.List)StringListParameter.this.list).getSelectionIndex();
/* 158 */         StringListParameter.this.setIndex(index);
/*     */         
/* 160 */         if (StringListParameter.this.change_listeners != null) {
/* 161 */           for (int i = 0; i < StringListParameter.this.change_listeners.size(); i++) {
/* 162 */             ((ParameterChangeListener)StringListParameter.this.change_listeners.get(i)).parameterChanged(StringListParameter.this, false);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private int findIndex(String value, String[] values) {
/* 170 */     for (int i = 0; i < values.length; i++) {
/* 171 */       if (values[i].equals(value))
/* 172 */         return i;
/*     */     }
/* 174 */     return -1;
/*     */   }
/*     */   
/*     */   protected void setIndex(final int index) {
/* 178 */     if (index < 0) {
/* 179 */       COConfigurationManager.removeParameter(this.name);
/*     */       
/* 181 */       String defValue = COConfigurationManager.getStringParameter(this.name);
/* 182 */       int i = findIndex(defValue, this.values);
/* 183 */       if (i >= 0)
/*     */       {
/* 185 */         setIndex(i);
/*     */       } else {
/* 187 */         Utils.execSWTThread(new AERunnable() {
/*     */           public void runSupport() {
/* 189 */             if ((StringListParameter.this.list == null) || (StringListParameter.this.list.isDisposed())) {
/* 190 */               return;
/*     */             }
/*     */             
/* 193 */             if (StringListParameter.this.useCombo) {
/* 194 */               ((Combo)StringListParameter.this.list).deselectAll();
/*     */             } else {
/* 196 */               ((org.eclipse.swt.widgets.List)StringListParameter.this.list).deselectAll();
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/* 201 */       return;
/*     */     }
/*     */     
/* 204 */     String selected_value = this.values[index];
/*     */     
/* 206 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 208 */         if ((StringListParameter.this.list == null) || (StringListParameter.this.list.isDisposed())) {
/* 209 */           return;
/*     */         }
/*     */         
/* 212 */         if (StringListParameter.this.useCombo) {
/* 213 */           if (((Combo)StringListParameter.this.list).getSelectionIndex() != index) {
/* 214 */             ((Combo)StringListParameter.this.list).select(index);
/*     */           }
/*     */         }
/* 217 */         else if (((org.eclipse.swt.widgets.List)StringListParameter.this.list).getSelectionIndex() != index) {
/* 218 */           ((org.eclipse.swt.widgets.List)StringListParameter.this.list).select(index);
/*     */         }
/*     */       }
/*     */     });
/*     */     
/*     */ 
/* 224 */     if (!COConfigurationManager.getStringParameter(this.name).equals(selected_value)) {
/* 225 */       COConfigurationManager.setParameter(this.name, selected_value);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setLayoutData(Object layoutData) {
/* 230 */     Utils.adjustPXForDPI(layoutData);
/* 231 */     this.list.setLayoutData(layoutData);
/*     */   }
/*     */   
/*     */   public Control getControl() {
/* 235 */     return this.list;
/*     */   }
/*     */   
/*     */   public String getValue() {
/* 239 */     return COConfigurationManager.getStringParameter(this.name, this.default_value);
/*     */   }
/*     */   
/*     */   public void setValue(Object value) {
/* 243 */     if ((value instanceof String)) {
/* 244 */       String s = (String)value;
/* 245 */       setIndex(findIndex(s, this.values));
/*     */     }
/*     */   }
/*     */   
/*     */   public Object getValueObject() {
/* 250 */     return getValue();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/StringListParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */