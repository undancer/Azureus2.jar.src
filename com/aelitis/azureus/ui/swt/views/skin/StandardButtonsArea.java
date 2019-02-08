/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.events.ShellEvent;
/*     */ import org.eclipse.swt.events.ShellListener;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.RowData;
/*     */ import org.eclipse.swt.layout.RowLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Widget;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
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
/*     */ public abstract class StandardButtonsArea
/*     */ {
/*     */   private Button def_button;
/*     */   private Button[] buttons;
/*  41 */   private Map<Integer, Boolean> buttonsEnabled = new HashMap();
/*     */   
/*     */   private static final int BUTTON_PADDING = 2;
/*     */   
/*     */   private static final int MIN_BUTTON_WIDTH = 75;
/*     */   
/*     */   private String[] buttonIDs;
/*     */   
/*     */   private Integer[] buttonVals;
/*     */   private int defaultButtonPos;
/*     */   
/*     */   public void setButtonIDs(String[] buttons)
/*     */   {
/*  54 */     this.buttonIDs = (buttons == null ? new String[0] : buttons);
/*     */   }
/*     */   
/*     */   public void setButtonVals(Integer[] buttonVals) {
/*  58 */     this.buttonVals = buttonVals;
/*  59 */     int cancelPos = -1;
/*  60 */     for (int i = 0; i < buttonVals.length; i++) {
/*  61 */       Integer val = buttonVals[i];
/*  62 */       if (val.intValue() == 256) {
/*  63 */         cancelPos = i;
/*  64 */         break;
/*     */       }
/*     */     }
/*  67 */     if ((cancelPos >= 0) && 
/*  68 */       (Constants.isOSX) && (cancelPos != 0)) {
/*  69 */       String cancelButton = this.buttonIDs[cancelPos];
/*     */       
/*  71 */       for (int i = cancelPos; i > 0; i--) {
/*  72 */         if (this.defaultButtonPos == i) {
/*  73 */           this.defaultButtonPos = (i - 1);
/*     */         }
/*  75 */         this.buttonIDs[i] = this.buttonIDs[(i - 1)];
/*  76 */         this.buttonVals[i] = this.buttonVals[(i - 1)];
/*     */       }
/*  78 */       if (this.defaultButtonPos == 0) {
/*  79 */         this.defaultButtonPos = 1;
/*     */       }
/*  81 */       this.buttonIDs[0] = cancelButton;
/*  82 */       buttonVals[0] = Integer.valueOf(256);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setDefaultButtonPos(int defaultOption)
/*     */   {
/*  88 */     this.defaultButtonPos = defaultOption;
/*     */   }
/*     */   
/*     */   public int getButtonVal(int buttonPos) {
/*  92 */     if (this.buttonVals == null) {
/*  93 */       return buttonPos;
/*     */     }
/*  95 */     if ((buttonPos < 0) || (buttonPos >= this.buttonVals.length)) {
/*  96 */       return 256;
/*     */     }
/*  98 */     return this.buttonVals[buttonPos].intValue();
/*     */   }
/*     */   
/*     */   public int getButtonCount() {
/* 102 */     return this.buttonIDs.length;
/*     */   }
/*     */   
/*     */   public int getButtonPosFromVal(int buttonVal) {
/* 106 */     int pos = buttonVal;
/* 107 */     if (this.buttonVals != null) {
/* 108 */       for (int i = 0; i < this.buttonVals.length; i++) {
/* 109 */         int val = this.buttonVals[i].intValue();
/* 110 */         if (buttonVal == val) {
/* 111 */           pos = i;
/* 112 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 116 */     return pos;
/*     */   }
/*     */   
/*     */   public void swt_createButtons(Composite cBottomArea)
/*     */   {
/* 121 */     Composite cCenterH = new Composite(cBottomArea, 0);
/* 122 */     FormData fd = new FormData();
/* 123 */     fd.height = 1;
/* 124 */     fd.width = 1;
/* 125 */     fd.left = new FormAttachment(100);
/* 126 */     fd.right = new FormAttachment(0);
/* 127 */     cCenterH.setLayoutData(fd);
/*     */     
/* 129 */     Composite cCenterV = new Composite(cBottomArea, 0);
/* 130 */     fd = new FormData();
/* 131 */     fd.width = 1;
/* 132 */     fd.height = 1;
/* 133 */     fd.top = new FormAttachment(0);
/* 134 */     fd.bottom = new FormAttachment(100);
/* 135 */     cCenterV.setLayoutData(fd);
/*     */     
/*     */ 
/*     */ 
/* 139 */     Composite cButtonArea = new Composite(cBottomArea, 0);
/*     */     
/* 141 */     cButtonArea.setBackgroundMode(2);
/* 142 */     fd = new FormData();
/* 143 */     fd.top = new FormAttachment(cCenterV, 0, 16777216);
/* 144 */     fd.right = new FormAttachment(cCenterH, 0, 16384);
/* 145 */     cButtonArea.setLayoutData(fd);
/*     */     
/* 147 */     RowLayout rowLayout = new RowLayout(256);
/* 148 */     rowLayout.center = true;
/* 149 */     rowLayout.spacing = 8;
/* 150 */     rowLayout.pack = false;
/* 151 */     Utils.setLayout(cButtonArea, rowLayout);
/*     */     
/* 153 */     this.buttons = new Button[this.buttonIDs.length];
/* 154 */     for (int i = 0; i < this.buttonIDs.length; i++) {
/* 155 */       String buttonText = this.buttonIDs[i];
/* 156 */       if (buttonText != null)
/*     */       {
/*     */ 
/* 159 */         Button button = this.buttons[i] = new Button(cButtonArea, 8);
/* 160 */         int buttonVal = (this.buttonVals == null) || (i >= this.buttonVals.length) ? i : this.buttonVals[i].intValue();
/*     */         
/* 162 */         Boolean b = (Boolean)this.buttonsEnabled.get(Integer.valueOf(buttonVal));
/* 163 */         if (b == null) {
/* 164 */           b = Boolean.TRUE;
/*     */         }
/* 166 */         button.setEnabled(b.booleanValue());
/* 167 */         button.setText(buttonText);
/*     */         
/* 169 */         RowData rowData = new RowData();
/* 170 */         Point size = button.computeSize(-1, -1);
/* 171 */         size.x += 2;
/* 172 */         int minButtonWidth = Utils.adjustPXForDPI(75);
/* 173 */         if (size.x < minButtonWidth) {
/* 174 */           size.x = minButtonWidth;
/*     */         }
/* 176 */         rowData.width = size.x;
/* 177 */         Utils.setLayoutData(button, rowData);
/*     */         
/* 179 */         if (this.defaultButtonPos == i) {
/* 180 */           this.def_button = button;
/*     */         }
/* 182 */         button.setData("ButtonNo", new Integer(i));
/* 183 */         button.addListener(13, new Listener() {
/*     */           public void handleEvent(Event event) {
/* 185 */             int intValue = ((Number)event.widget.getData("ButtonNo")).intValue();
/* 186 */             StandardButtonsArea.this.clicked(StandardButtonsArea.this.getButtonVal(intValue));
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/* 191 */     cBottomArea.getParent().layout(true, true);
/*     */     
/* 193 */     cBottomArea.getShell().addShellListener(new ShellListener()
/*     */     {
/*     */       public void shellIconified(ShellEvent e) {}
/*     */       
/*     */ 
/*     */ 
/*     */       public void shellDeiconified(ShellEvent e) {}
/*     */       
/*     */ 
/*     */ 
/*     */       public void shellDeactivated(ShellEvent e) {}
/*     */       
/*     */ 
/*     */       public void shellClosed(ShellEvent e) {}
/*     */       
/*     */ 
/*     */       public void shellActivated(ShellEvent e)
/*     */       {
/* 211 */         if (StandardButtonsArea.this.def_button != null) {
/* 212 */           StandardButtonsArea.this.def_button.getShell().setDefaultButton(StandardButtonsArea.this.def_button);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   protected abstract void clicked(int paramInt);
/*     */   
/*     */   public void setButtonEnabled(final int buttonVal, final boolean enable) {
/* 221 */     this.buttonsEnabled.put(Integer.valueOf(buttonVal), Boolean.valueOf(enable));
/* 222 */     if (this.buttons == null) {
/* 223 */       return;
/*     */     }
/* 225 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 227 */         if (StandardButtonsArea.this.buttons == null) {
/* 228 */           return;
/*     */         }
/* 230 */         int pos = StandardButtonsArea.this.getButtonPosFromVal(buttonVal);
/* 231 */         if ((pos >= 0) && (pos < StandardButtonsArea.this.buttons.length)) {
/* 232 */           Button button = StandardButtonsArea.this.buttons[pos];
/* 233 */           if ((button != null) && (!button.isDisposed())) {
/* 234 */             button.setEnabled(enable);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public Button[] getButtons() {
/* 242 */     return this.buttons;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/StandardButtonsArea.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */