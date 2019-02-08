/*     */ package org.gudy.azureus2.ui.swt.config.generic;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.IAdditionalActionPerformer;
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
/*     */ public class GenericBooleanParameter
/*     */ {
/*     */   protected static final boolean DEBUG = false;
/*     */   GenericParameterAdapter adapter;
/*     */   String name;
/*     */   Button checkBox;
/*     */   Boolean defaultValue;
/*  50 */   List performers = new ArrayList();
/*     */   
/*     */   public GenericBooleanParameter(GenericParameterAdapter adapter, Composite composite, String name)
/*     */   {
/*  54 */     this(adapter, composite, name, adapter.getBooleanValue(name), null, null);
/*     */   }
/*     */   
/*     */   public GenericBooleanParameter(GenericParameterAdapter adapter, Composite composite, String name, String textKey)
/*     */   {
/*  59 */     this(adapter, composite, name, adapter.getBooleanValue(name), textKey, null);
/*     */   }
/*     */   
/*     */ 
/*     */   public GenericBooleanParameter(GenericParameterAdapter adapter, Composite composite, String name, Boolean defaultValue, String textKey)
/*     */   {
/*  65 */     this(adapter, composite, name, defaultValue, textKey, null);
/*     */   }
/*     */   
/*     */   public GenericBooleanParameter(GenericParameterAdapter adapter, Composite composite, String name, Boolean defaultValue)
/*     */   {
/*  70 */     this(adapter, composite, name, defaultValue, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */   public GenericBooleanParameter(GenericParameterAdapter _adapter, Composite composite, String _name, Boolean _defaultValue, String textKey, IAdditionalActionPerformer actionPerformer)
/*     */   {
/*  76 */     this.adapter = _adapter;
/*  77 */     this.name = _name;
/*  78 */     this.defaultValue = _defaultValue;
/*  79 */     if (actionPerformer != null) {
/*  80 */       this.performers.add(actionPerformer);
/*     */     }
/*  82 */     Boolean value = this.adapter.getBooleanValue(this.name, this.defaultValue);
/*  83 */     this.checkBox = new Button(composite, 32);
/*  84 */     if (textKey != null)
/*  85 */       Messages.setLanguageText(this.checkBox, textKey);
/*  86 */     if (value != null) {
/*  87 */       this.checkBox.setGrayed(false);
/*  88 */       this.checkBox.setSelection(value.booleanValue());
/*     */     } else {
/*  90 */       this.checkBox.setGrayed(true);
/*  91 */       this.checkBox.setSelection(true);
/*     */     }
/*     */     
/*  94 */     this.checkBox.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/*  96 */         GenericBooleanParameter.this.setSelected(GenericBooleanParameter.this.checkBox.getSelection(), true);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setLayoutData(Object layoutData) {
/* 102 */     Utils.adjustPXForDPI(layoutData);
/* 103 */     this.checkBox.setLayoutData(layoutData);
/*     */   }
/*     */   
/*     */   public void setAdditionalActionPerformer(IAdditionalActionPerformer actionPerformer)
/*     */   {
/* 108 */     this.performers.add(actionPerformer);
/* 109 */     Boolean selected = isSelected();
/* 110 */     if (selected != null) {
/* 111 */       actionPerformer.setSelected(selected.booleanValue());
/*     */     }
/* 113 */     actionPerformer.performAction();
/*     */   }
/*     */   
/*     */   public Control getControl() {
/* 117 */     return this.checkBox;
/*     */   }
/*     */   
/*     */   public String getName() {
/* 121 */     return this.name;
/*     */   }
/*     */   
/*     */   public void setName(String newName) {
/* 125 */     this.name = newName;
/*     */   }
/*     */   
/*     */   public Boolean isSelected() {
/* 129 */     return this.adapter.getBooleanValue(this.name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setEnabled(final boolean enabled)
/*     */   {
/* 136 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 138 */         if (!GenericBooleanParameter.this.checkBox.isDisposed()) {
/* 139 */           GenericBooleanParameter.this.checkBox.setEnabled(enabled);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setSelected(final boolean selected) {
/* 146 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 148 */         if (!GenericBooleanParameter.this.checkBox.isDisposed()) {
/* 149 */           if (GenericBooleanParameter.this.checkBox.getSelection() != selected)
/*     */           {
/*     */ 
/*     */ 
/* 153 */             GenericBooleanParameter.this.checkBox.setSelection(selected);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 159 */           GenericBooleanParameter.this.adapter.setBooleanValue(GenericBooleanParameter.this.name, GenericBooleanParameter.this.checkBox.getSelection());
/*     */         } else {
/* 161 */           GenericBooleanParameter.this.adapter.setBooleanValue(GenericBooleanParameter.this.name, selected);
/*     */         }
/*     */         
/* 164 */         if (GenericBooleanParameter.this.performers.size() > 0)
/*     */         {
/* 166 */           for (int i = 0; i < GenericBooleanParameter.this.performers.size(); i++)
/*     */           {
/* 168 */             IAdditionalActionPerformer performer = (IAdditionalActionPerformer)GenericBooleanParameter.this.performers.get(i);
/*     */             
/* 170 */             performer.setSelected(selected);
/*     */             
/* 172 */             performer.performAction();
/*     */           }
/*     */         }
/*     */         
/* 176 */         GenericBooleanParameter.this.adapter.informChanged(false);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   protected void setSelected(final boolean selected, boolean force) {
/* 182 */     if (force) {
/* 183 */       setSelected(selected);
/*     */     } else {
/* 185 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 187 */           if (GenericBooleanParameter.this.checkBox.getSelection() != selected) {
/* 188 */             GenericBooleanParameter.this.checkBox.setSelection(selected);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   public void refresh() {
/* 196 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 198 */         Boolean selected = GenericBooleanParameter.this.isSelected();
/* 199 */         if (selected == null) {
/* 200 */           GenericBooleanParameter.this.checkBox.setGrayed(true);
/* 201 */           GenericBooleanParameter.this.checkBox.setSelection(true);
/*     */         } else {
/* 203 */           GenericBooleanParameter.this.checkBox.setGrayed(false);
/* 204 */           if (GenericBooleanParameter.this.checkBox.getSelection() != selected.booleanValue()) {
/* 205 */             GenericBooleanParameter.this.checkBox.setSelection(selected.booleanValue());
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void debug(String string) {
/* 213 */     System.out.println("[GenericBooleanParameter:" + this.name + "] " + string);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/generic/GenericBooleanParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */