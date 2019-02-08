/*     */ package org.gudy.azureus2.ui.swt.config.generic;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Spinner;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
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
/*     */ public class GenericIntParameter
/*     */ {
/*  32 */   private static boolean DEBUG = false;
/*     */   
/*     */   private GenericParameterAdapter adapter;
/*     */   
/*  36 */   private int iMinValue = Integer.MIN_VALUE;
/*     */   
/*  38 */   private int iMaxValue = Integer.MAX_VALUE;
/*     */   
/*     */   private int iDefaultValue;
/*     */   
/*     */   private String sParamName;
/*     */   
/*  44 */   private boolean bGenerateIntermediateEvents = false;
/*     */   
/*     */ 
/*     */ 
/*  48 */   private boolean bTriggerOnFocusOut = Utils.isCarbon;
/*     */   
/*     */   private Spinner spinner;
/*     */   
/*  52 */   private TimerEvent timedSaveEvent = null;
/*     */   
/*     */   private TimerEventPerformer timerEventSave;
/*     */   
/*  56 */   private final boolean delayIntialSet = (Utils.isCarbon) && (System.getProperty("os.version", "").startsWith("10.6"));
/*     */   
/*  58 */   private boolean isZeroHidden = false;
/*     */   
/*  60 */   private boolean disableTimedSave = false;
/*     */   
/*     */   public GenericIntParameter(GenericParameterAdapter adapter, Composite composite, String name)
/*     */   {
/*  64 */     this.iDefaultValue = adapter.getIntValue(name);
/*  65 */     initialize(adapter, composite, name);
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*  71 */   public GenericIntParameter(GenericParameterAdapter adapter, Composite composite, String name, int defaultValue) { this.iDefaultValue = defaultValue;
/*  72 */     initialize(adapter, composite, name);
/*     */   }
/*     */   
/*     */   public GenericIntParameter(GenericParameterAdapter adapter, Composite composite, String name, int minValue, int maxValue)
/*     */   {
/*  77 */     this.iDefaultValue = adapter.getIntValue(name);
/*     */     
/*  79 */     if (maxValue < minValue) {
/*  80 */       Debug.out("max < min, not good");
/*     */       
/*     */ 
/*  83 */       maxValue = Integer.MAX_VALUE;
/*     */     }
/*     */     
/*  86 */     this.iMinValue = minValue;
/*  87 */     this.iMaxValue = maxValue;
/*  88 */     initialize(adapter, composite, name);
/*     */   }
/*     */   
/*     */   public void initialize(GenericParameterAdapter _adapter, Composite composite, String name)
/*     */   {
/*  93 */     this.adapter = _adapter;
/*  94 */     this.sParamName = name;
/*     */     
/*  96 */     this.timerEventSave = new TimerEventPerformer() {
/*     */       public void perform(TimerEvent event) {
/*  98 */         Utils.execSWTThread(new AERunnable() {
/*     */           public void runSupport() {
/* 100 */             if (GenericIntParameter.this.spinner.isDisposed()) {
/* 101 */               return;
/*     */             }
/* 103 */             if (GenericIntParameter.DEBUG) {
/* 104 */               GenericIntParameter.this.debug("setIntValue to " + GenericIntParameter.this.spinner.getSelection() + " via timeEventSave");
/*     */             }
/*     */             
/* 107 */             GenericIntParameter.this.adapter.setIntValue(GenericIntParameter.this.sParamName, GenericIntParameter.this.spinner.getSelection());
/*     */           }
/*     */           
/*     */         });
/*     */       }
/* 112 */     };
/* 113 */     int value = this.adapter.getIntValue(name, this.iDefaultValue);
/*     */     
/* 115 */     this.spinner = new Spinner(composite, 133120);
/* 116 */     setMinimumValue(this.iMinValue);
/* 117 */     setMaximumValue(this.iMaxValue);
/* 118 */     swt_setSpinnerValue(value);
/*     */     
/* 120 */     if (this.delayIntialSet) {
/* 121 */       Utils.execSWTThreadLater(0, new AERunnable() {
/*     */         public void runSupport() {
/* 123 */           GenericIntParameter.this.swt_setSpinnerValue(GenericIntParameter.this.adapter.getIntValue(GenericIntParameter.this.sParamName, GenericIntParameter.this.iDefaultValue));
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 128 */     this.spinner.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 130 */         if ((GenericIntParameter.this.bGenerateIntermediateEvents) || (!GenericIntParameter.this.spinner.isFocusControl())) {
/* 131 */           GenericIntParameter.this.adapter.setIntValue(GenericIntParameter.this.sParamName, GenericIntParameter.this.spinner.getSelection());
/*     */         } else {
/* 133 */           GenericIntParameter.this.bTriggerOnFocusOut = true;
/* 134 */           GenericIntParameter.this.cancelTimedSaveEvent();
/*     */           
/* 136 */           if (GenericIntParameter.DEBUG) {
/* 137 */             GenericIntParameter.this.debug("create timeSaveEvent (" + GenericIntParameter.this.spinner.getSelection() + ") ");
/*     */           }
/* 139 */           if (!GenericIntParameter.this.disableTimedSave) {
/* 140 */             GenericIntParameter.this.timedSaveEvent = SimpleTimer.addEvent("IntParam Saver", SystemTime.getOffsetTime(750L), GenericIntParameter.this.timerEventSave);
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 152 */     });
/* 153 */     this.spinner.addListener(12, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 155 */         if (GenericIntParameter.this.spinner.isFocusControl()) {
/* 156 */           if (GenericIntParameter.DEBUG) {
/* 157 */             GenericIntParameter.this.debug("next");
/*     */           }
/* 159 */           GenericIntParameter.this.spinner.traverse(16);
/*     */         }
/*     */         
/*     */       }
/* 163 */     });
/* 164 */     this.spinner.addListener(16, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 166 */         if (GenericIntParameter.this.bTriggerOnFocusOut) {
/* 167 */           if (GenericIntParameter.DEBUG) {
/* 168 */             GenericIntParameter.this.debug("focus out setIntValue(" + GenericIntParameter.this.spinner.getSelection() + "/trigger");
/*     */           }
/*     */           
/* 171 */           GenericIntParameter.this.cancelTimedSaveEvent();
/* 172 */           GenericIntParameter.this.adapter.setIntValue(GenericIntParameter.this.sParamName, GenericIntParameter.this.spinner.getSelection());
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void swt_setSpinnerValue(int value) {
/* 179 */     this.spinner.setSelection(value);
/* 180 */     if (this.isZeroHidden) {
/* 181 */       Display display = this.spinner.getDisplay();
/* 182 */       this.spinner.setBackground(value == 0 ? display.getSystemColor(29) : null);
/* 183 */       this.spinner.setForeground(value == 0 ? display.getSystemColor(28) : null);
/*     */     }
/*     */   }
/*     */   
/*     */   private void cancelTimedSaveEvent() {
/* 188 */     if ((this.timedSaveEvent != null) && ((!this.timedSaveEvent.hasRun()) || (!this.timedSaveEvent.isCancelled())))
/*     */     {
/* 190 */       if (DEBUG) {
/* 191 */         debug("cancel timeSaveEvent");
/*     */       }
/* 193 */       this.timedSaveEvent.cancel();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void debug(String string)
/*     */   {
/* 201 */     System.out.println("[GenericIntParameter:" + this.sParamName + "] " + string);
/*     */   }
/*     */   
/*     */   public void setMinimumValue(final int value) {
/* 205 */     this.iMinValue = value;
/* 206 */     if ((this.iMinValue != Integer.MIN_VALUE) && (getValue() < this.iMinValue)) {
/* 207 */       setValue(this.iMinValue);
/*     */     }
/* 209 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 211 */         GenericIntParameter.this.spinner.setMinimum(value);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setMaximumValue(final int value) {
/* 217 */     this.iMaxValue = value;
/* 218 */     if ((this.iMaxValue != Integer.MAX_VALUE) && (getValue() > this.iMaxValue)) {
/* 219 */       setValue(this.iMaxValue);
/*     */     }
/* 221 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 223 */         GenericIntParameter.this.spinner.setMaximum(value);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/* 229 */   public String getName() { return this.sParamName; }
/*     */   
/*     */   public void setValue(int value) {
/*     */     int newValue;
/*     */     int newValue;
/* 234 */     if ((this.iMaxValue != Integer.MAX_VALUE) && (value > this.iMaxValue)) {
/* 235 */       newValue = this.iMaxValue; } else { int newValue;
/* 236 */       if ((this.iMinValue != Integer.MIN_VALUE) && (value < this.iMinValue)) {
/* 237 */         newValue = this.iMinValue;
/*     */       } else {
/* 239 */         newValue = value;
/*     */       }
/*     */     }
/* 242 */     final int finalNewValue = newValue;
/*     */     
/* 244 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 246 */         if (!GenericIntParameter.this.spinner.isDisposed()) {
/* 247 */           if (GenericIntParameter.this.spinner.getSelection() != finalNewValue) {
/* 248 */             if (GenericIntParameter.DEBUG) {
/* 249 */               GenericIntParameter.this.debug("spinner.setSelection(" + finalNewValue + ")");
/*     */             }
/* 251 */             GenericIntParameter.this.swt_setSpinnerValue(finalNewValue);
/*     */           }
/* 253 */           if (GenericIntParameter.DEBUG) {
/* 254 */             GenericIntParameter.this.debug("setIntValue to " + GenericIntParameter.this.spinner.getSelection() + " via setValue(int)");
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */     
/* 260 */     if (finalNewValue != getValue()) {
/* 261 */       this.adapter.setIntValue(this.sParamName, finalNewValue);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setValue(final int value, boolean force_adapter_set) {
/* 266 */     if (force_adapter_set) {
/* 267 */       setValue(value);
/*     */     } else {
/* 269 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 271 */           if (GenericIntParameter.this.spinner.getSelection() != value) {
/* 272 */             GenericIntParameter.this.swt_setSpinnerValue(value);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   public int getValue() {
/* 280 */     return this.adapter.getIntValue(this.sParamName, this.iDefaultValue);
/*     */   }
/*     */   
/*     */   public void resetToDefault() {
/* 284 */     if (this.adapter.resetIntDefault(this.sParamName)) {
/* 285 */       setValue(this.adapter.getIntValue(this.sParamName));
/*     */     } else {
/* 287 */       setValue(getValue());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void refresh()
/*     */   {
/* 294 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 296 */         if (!GenericIntParameter.this.spinner.isDisposed())
/* 297 */           GenericIntParameter.this.spinner.setSelection(GenericIntParameter.this.adapter.getIntValue(GenericIntParameter.this.sParamName));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setLayoutData(Object layoutData) {
/* 303 */     Utils.adjustPXForDPI(layoutData);
/* 304 */     this.spinner.setLayoutData(layoutData);
/*     */   }
/*     */   
/*     */   public Control getControl() {
/* 308 */     return this.spinner;
/*     */   }
/*     */   
/*     */   public boolean isGeneratingIntermediateEvents() {
/* 312 */     return this.bGenerateIntermediateEvents;
/*     */   }
/*     */   
/*     */   public void setGenerateIntermediateEvents(boolean generateIntermediateEvents) {
/* 316 */     this.bGenerateIntermediateEvents = generateIntermediateEvents;
/*     */   }
/*     */   
/*     */ 
/*     */   public void disableTimedSave()
/*     */   {
/* 322 */     this.disableTimedSave = true;
/*     */   }
/*     */   
/*     */   public boolean isZeroHidden() {
/* 326 */     return this.isZeroHidden;
/*     */   }
/*     */   
/*     */   public void setZeroHidden(boolean isZeroHidden) {
/* 330 */     if (this.isZeroHidden == isZeroHidden) {
/* 331 */       return;
/*     */     }
/* 333 */     this.isZeroHidden = isZeroHidden;
/*     */     
/* 335 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 337 */         GenericIntParameter.this.swt_setSpinnerValue(GenericIntParameter.this.adapter.getIntValue(GenericIntParameter.this.sParamName, GenericIntParameter.this.iDefaultValue));
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/generic/GenericIntParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */