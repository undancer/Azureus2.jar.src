/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SWTSkinButtonUtility
/*     */ {
/*  39 */   ArrayList<ButtonListenerAdapter> listeners = new ArrayList();
/*     */   
/*     */   private final SWTSkinObject skinObject;
/*     */   
/*     */   private final String imageViewID;
/*     */   
/*     */ 
/*     */   public static class ButtonListenerAdapter
/*     */   {
/*     */     public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask) {}
/*     */     
/*     */ 
/*     */     public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int button, int stateMask)
/*     */     {
/*  53 */       pressed(buttonUtility, skinObject, stateMask);
/*     */     }
/*     */     
/*     */     public boolean held(SWTSkinButtonUtility buttonUtility) {
/*  57 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */     public void disabledStateChanged(SWTSkinButtonUtility buttonUtility, boolean disabled) {}
/*     */   }
/*     */   
/*     */   public SWTSkinButtonUtility(SWTSkinObject skinObject)
/*     */   {
/*  66 */     this(skinObject, null);
/*     */   }
/*     */   
/*     */   public SWTSkinButtonUtility(SWTSkinObject skinObject, String imageViewID) {
/*  70 */     this.skinObject = skinObject;
/*  71 */     this.imageViewID = imageViewID;
/*     */     
/*  73 */     if (skinObject == null) {
/*  74 */       Debug.out("Can't make button out of null skinObject");
/*  75 */       return;
/*     */     }
/*  77 */     if (skinObject.getControl() == null) {
/*  78 */       Debug.out("Can't make button out of null skinObject control");
/*  79 */       return;
/*     */     }
/*     */     
/*  82 */     if ((skinObject instanceof SWTSkinObjectButton)) {
/*  83 */       return;
/*     */     }
/*     */     
/*  86 */     Listener l = new Listener()
/*     */     {
/*     */       boolean bDownPressed;
/*     */       private TimerEvent timerEvent;
/*     */       
/*     */       public void handleEvent(Event event) {
/*  92 */         if (event.type == 3) {
/*  93 */           if (this.timerEvent == null) {
/*  94 */             this.timerEvent = SimpleTimer.addEvent("MouseHold", SystemTime.getOffsetTime(1000L), new TimerEventPerformer()
/*     */             {
/*     */               public void perform(TimerEvent event) {
/*  97 */                 SWTSkinButtonUtility.1.this.timerEvent = null;
/*     */                 
/*  99 */                 if (!SWTSkinButtonUtility.1.this.bDownPressed) {
/* 100 */                   return;
/*     */                 }
/* 102 */                 SWTSkinButtonUtility.1.this.bDownPressed = false;
/*     */                 
/* 104 */                 boolean stillPressed = true;
/* 105 */                 for (SWTSkinButtonUtility.ButtonListenerAdapter l : SWTSkinButtonUtility.this.listeners) {
/* 106 */                   stillPressed &= !l.held(SWTSkinButtonUtility.this);
/*     */                 }
/* 108 */                 SWTSkinButtonUtility.1.this.bDownPressed = stillPressed;
/*     */               }
/*     */             });
/*     */           }
/* 112 */           this.bDownPressed = true;
/* 113 */           return;
/*     */         }
/* 115 */         if (this.timerEvent != null) {
/* 116 */           this.timerEvent.cancel();
/* 117 */           this.timerEvent = null;
/*     */         }
/* 119 */         if (!this.bDownPressed) {
/* 120 */           return;
/*     */         }
/*     */         
/*     */ 
/* 124 */         this.bDownPressed = false;
/*     */         
/* 126 */         if (SWTSkinButtonUtility.this.isDisabled()) {
/* 127 */           return;
/*     */         }
/*     */         
/* 130 */         for (SWTSkinButtonUtility.ButtonListenerAdapter l : SWTSkinButtonUtility.this.listeners) {
/* 131 */           l.pressed(SWTSkinButtonUtility.this, SWTSkinButtonUtility.this.skinObject, event.button, event.stateMask);
/*     */         }
/*     */       }
/*     */     };
/*     */     
/* 136 */     if ((skinObject instanceof SWTSkinObjectContainer)) {
/* 137 */       Utils.addListenerAndChildren((Composite)skinObject.getControl(), 4, l);
/*     */       
/* 139 */       Utils.addListenerAndChildren((Composite)skinObject.getControl(), 3, l);
/*     */     }
/*     */     else {
/* 142 */       skinObject.getControl().addListener(4, l);
/* 143 */       skinObject.getControl().addListener(3, l);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isDisabled() {
/* 148 */     return this.skinObject == null ? true : this.skinObject.getSuffix().contains("-disabled");
/*     */   }
/*     */   
/*     */ 
/* 152 */   private boolean inSetDisabled = false;
/*     */   
/* 154 */   private boolean lastDisabledState = false;
/*     */   
/*     */   public void setDisabled(boolean disabled) {
/* 157 */     if ((this.inSetDisabled) || (this.skinObject == null)) {
/* 158 */       return;
/*     */     }
/* 160 */     this.inSetDisabled = true;
/*     */     try {
/* 162 */       if (disabled == isDisabled()) {
/*     */         return;
/*     */       }
/* 165 */       if ((this.skinObject instanceof SWTSkinObjectButton)) {
/* 166 */         this.lastDisabledState = disabled;
/* 167 */         Utils.execSWTThreadLater(100, new AERunnable() {
/*     */           public void runSupport() {
/* 169 */             ((SWTSkinObjectButton)SWTSkinButtonUtility.this.skinObject).getButton().setEnabled(!SWTSkinButtonUtility.this.lastDisabledState);
/*     */           }
/*     */         });
/*     */       }
/*     */       
/* 174 */       String suffix = disabled ? "-disabled" : "";
/* 175 */       this.skinObject.switchSuffix(suffix, 1, false);
/*     */       
/* 177 */       for (ButtonListenerAdapter l : this.listeners) {
/* 178 */         l.disabledStateChanged(this, disabled);
/*     */       }
/*     */     } finally {
/* 181 */       this.inSetDisabled = false;
/*     */     }
/*     */   }
/*     */   
/*     */   public void addSelectionListener(ButtonListenerAdapter listener) {
/* 186 */     if ((this.skinObject instanceof SWTSkinObjectButton)) {
/* 187 */       ((SWTSkinObjectButton)this.skinObject).addSelectionListener(listener);
/* 188 */       return;
/*     */     }
/*     */     
/* 191 */     if (this.listeners.contains(listener)) {
/* 192 */       return;
/*     */     }
/* 194 */     this.listeners.add(listener);
/*     */   }
/*     */   
/*     */   public SWTSkinObject getSkinObject() {
/* 198 */     return this.skinObject;
/*     */   }
/*     */   
/*     */   public void setTextID(final String id) {
/* 202 */     if (this.skinObject == null) {
/* 203 */       return;
/*     */     }
/* 205 */     if ((this.skinObject instanceof SWTSkinObjectButton)) {
/* 206 */       ((SWTSkinObjectButton)this.skinObject).setText(MessageText.getString(id));
/* 207 */       return;
/*     */     }
/* 209 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*     */       public void runSupport() {
/* 211 */         if ((SWTSkinButtonUtility.this.skinObject instanceof SWTSkinObjectText)) {
/* 212 */           SWTSkinObjectText skinTextObject = (SWTSkinObjectText)SWTSkinButtonUtility.this.skinObject;
/* 213 */           skinTextObject.setTextID(id);
/* 214 */         } else if ((SWTSkinButtonUtility.this.skinObject instanceof SWTSkinObjectContainer)) {
/* 215 */           SWTSkinObject[] children = ((SWTSkinObjectContainer)SWTSkinButtonUtility.this.skinObject).getChildren();
/* 216 */           if ((children.length > 0) && ((children[0] instanceof SWTSkinObjectText))) {
/* 217 */             SWTSkinObjectText skinTextObject = (SWTSkinObjectText)children[0];
/* 218 */             skinTextObject.setTextID(id);
/*     */           }
/*     */         }
/* 221 */         Utils.relayout(SWTSkinButtonUtility.this.skinObject.getControl());
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setImage(final String id) {
/* 227 */     if (this.skinObject == null) {
/* 228 */       return;
/*     */     }
/* 230 */     if ((this.skinObject instanceof SWTSkinObjectButton))
/*     */     {
/* 232 */       return;
/*     */     }
/* 234 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 236 */         if (SWTSkinButtonUtility.this.imageViewID != null) {
/* 237 */           SWTSkinObject skinImageObject = SWTSkinButtonUtility.this.skinObject.getSkin().getSkinObject(SWTSkinButtonUtility.this.imageViewID, SWTSkinButtonUtility.this.skinObject);
/*     */           
/* 239 */           if ((skinImageObject instanceof SWTSkinObjectImage)) {
/* 240 */             ((SWTSkinObjectImage)skinImageObject).setImageByID(id, null);
/* 241 */             return;
/*     */           }
/*     */         }
/* 244 */         if ((SWTSkinButtonUtility.this.skinObject instanceof SWTSkinObjectImage)) {
/* 245 */           SWTSkinObjectImage skinImageObject = (SWTSkinObjectImage)SWTSkinButtonUtility.this.skinObject;
/* 246 */           skinImageObject.setImageByID(id, null);
/* 247 */         } else if ((SWTSkinButtonUtility.this.skinObject instanceof SWTSkinObjectContainer)) {
/* 248 */           SWTSkinObject[] children = ((SWTSkinObjectContainer)SWTSkinButtonUtility.this.skinObject).getChildren();
/* 249 */           if ((children.length > 0) && ((children[0] instanceof SWTSkinObjectImage))) {
/* 250 */             SWTSkinObjectImage skinImageObject = (SWTSkinObjectImage)children[0];
/* 251 */             skinImageObject.setImageByID(id, null);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setTooltipID(final String id) {
/* 259 */     if (this.skinObject == null) {
/* 260 */       return;
/*     */     }
/* 262 */     if ((this.skinObject instanceof SWTSkinObjectButton))
/*     */     {
/* 264 */       return;
/*     */     }
/*     */     
/* 267 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 269 */         if (SWTSkinButtonUtility.this.imageViewID != null) {
/* 270 */           SWTSkinObject skinImageObject = SWTSkinButtonUtility.this.skinObject.getSkin().getSkinObject(SWTSkinButtonUtility.this.imageViewID, SWTSkinButtonUtility.this.skinObject);
/*     */           
/* 272 */           if ((skinImageObject instanceof SWTSkinObjectImage)) {
/* 273 */             ((SWTSkinObjectImage)skinImageObject).setTooltipID(id);
/* 274 */             return;
/*     */           }
/*     */         }
/* 277 */         if ((SWTSkinButtonUtility.this.skinObject instanceof SWTSkinObjectImage)) {
/* 278 */           SWTSkinObjectImage skinImageObject = (SWTSkinObjectImage)SWTSkinButtonUtility.this.skinObject;
/* 279 */           skinImageObject.setTooltipID(id);
/* 280 */         } else if ((SWTSkinButtonUtility.this.skinObject instanceof SWTSkinObjectContainer)) {
/* 281 */           SWTSkinObject[] children = ((SWTSkinObjectContainer)SWTSkinButtonUtility.this.skinObject).getChildren();
/* 282 */           if ((children.length > 0) && ((children[0] instanceof SWTSkinObjectImage))) {
/* 283 */             SWTSkinObjectImage skinImageObject = (SWTSkinObjectImage)children[0];
/* 284 */             skinImageObject.setTooltipID(id);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinButtonUtility.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */