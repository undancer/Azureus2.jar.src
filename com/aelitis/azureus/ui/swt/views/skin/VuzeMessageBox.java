/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctionsUserPrompter;
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectImage;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectText;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinProperties;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.ResourceBundle;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore2;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class VuzeMessageBox
/*     */   implements UIFunctionsUserPrompter, SkinnedDialog.SkinnedDialogClosedListener
/*     */ {
/*     */   private String title;
/*     */   private String text;
/*  48 */   private int result = -1;
/*     */   
/*  50 */   private ArrayList<UserPrompterResultListener> resultListeners = new ArrayList(1);
/*     */   
/*     */   private VuzeMessageBoxListener vuzeMessageBoxListener;
/*     */   
/*     */   private SWTSkinObjectContainer soExtra;
/*     */   
/*     */   private SkinnedDialog dlg;
/*     */   
/*     */   private String iconResource;
/*     */   
/*     */   private String subtitle;
/*     */   
/*  62 */   private List<rbInfo> listRBs = new ArrayList();
/*     */   
/*     */   private SWTSkin skin;
/*     */   
/*     */   private String textIconResource;
/*     */   
/*     */   private boolean closed;
/*     */   
/*     */   private boolean opened;
/*     */   
/*     */   private StandardButtonsArea buttonsArea;
/*     */   
/*  74 */   private String dialogTempate = "skin3_dlg_generic";
/*     */   
/*     */   public VuzeMessageBox(String title, String text, String[] buttons, int defaultOption)
/*     */   {
/*  78 */     this.title = title;
/*  79 */     this.text = text;
/*  80 */     this.buttonsArea = new StandardButtonsArea()
/*     */     {
/*     */       protected void clicked(int buttonValue) {
/*  83 */         VuzeMessageBox.this.closeWithButtonVal(buttonValue);
/*     */       }
/*  85 */     };
/*  86 */     this.buttonsArea.setButtonIDs(buttons);
/*  87 */     this.buttonsArea.setDefaultButtonPos(defaultOption);
/*     */   }
/*     */   
/*     */   public void setButtonEnabled(int buttonVal, boolean enable) {
/*  91 */     this.buttonsArea.setButtonEnabled(buttonVal, enable);
/*     */   }
/*     */   
/*     */   public void setButtonVals(Integer[] buttonVals) {
/*  95 */     this.buttonsArea.setButtonVals(buttonVals);
/*     */   }
/*     */   
/*     */   public void setSubTitle(String s)
/*     */   {
/* 100 */     this.subtitle = s;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getAutoCloseInMS()
/*     */   {
/* 107 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getHtml()
/*     */   {
/* 114 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getRememberID()
/*     */   {
/* 121 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getRememberText()
/*     */   {
/* 128 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isAutoClosed()
/*     */   {
/* 135 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void open(final UserPrompterResultListener l)
/*     */   {
/* 142 */     this.opened = true;
/* 143 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport() {
/* 146 */         if (VuzeMessageBox.this.closed) {
/* 147 */           return;
/*     */         }
/* 149 */         synchronized (VuzeMessageBox.this) {
/* 150 */           VuzeMessageBox.this._open(l);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setSkinnedDialagTemplate(String dialogTempate) {
/* 157 */     this.dialogTempate = dialogTempate;
/*     */   }
/*     */   
/*     */   protected void _open(UserPrompterResultListener l) {
/* 161 */     if (l != null) {
/* 162 */       synchronized (this.resultListeners) {
/* 163 */         this.resultListeners.add(l);
/*     */       }
/*     */     }
/* 166 */     this.dlg = new SkinnedDialog(this.dialogTempate, "shell", 2144) {
/*     */       protected void setSkin(SWTSkin skin) {
/* 168 */         super.setSkin(skin);
/*     */         
/*     */ 
/*     */ 
/* 172 */         VuzeMessageBox.this.skin = skin;
/* 173 */         synchronized (VuzeMessageBox.this.listRBs) {
/* 174 */           for (VuzeMessageBox.rbInfo rb : VuzeMessageBox.this.listRBs) {
/* 175 */             VuzeMessageBox.this.addResourceBundle(rb.cla, rb.path, rb.name);
/*     */           }
/* 177 */           VuzeMessageBox.this.listRBs.clear();
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 182 */     };
/* 183 */     this.dlg.setTitle(this.title);
/* 184 */     this.dlg.addCloseListener(this);
/*     */     
/* 186 */     SWTSkinObjectText soTopTitle = (SWTSkinObjectText)this.skin.getSkinObject("top-title");
/* 187 */     if (soTopTitle != null) {
/* 188 */       soTopTitle.setText(this.subtitle == null ? this.title : this.subtitle);
/*     */     }
/*     */     
/* 191 */     SWTSkinObjectText soText = (SWTSkinObjectText)this.skin.getSkinObject("middle-title");
/* 192 */     if (soText != null) {
/* 193 */       soText.setText(this.text);
/*     */     }
/*     */     
/* 196 */     if (this.iconResource != null) {
/* 197 */       SWTSkinObjectImage soTopLogo = (SWTSkinObjectImage)this.dlg.getSkin().getSkinObject("top-logo");
/* 198 */       if (soTopLogo != null) {
/* 199 */         soTopLogo.setImageByID(this.iconResource, null);
/*     */       }
/*     */     }
/*     */     
/* 203 */     if (this.textIconResource != null) {
/* 204 */       SWTSkinObjectImage soIcon = (SWTSkinObjectImage)this.dlg.getSkin().getSkinObject("text-icon");
/* 205 */       if (soIcon != null) {
/* 206 */         soIcon.setImageByID(this.textIconResource, null);
/*     */       }
/*     */     }
/*     */     
/* 210 */     if ((this.iconResource == null) && (this.textIconResource == null) && (soTopTitle != null) && (soText != null)) {
/* 211 */       soTopTitle.setStyle(soText.getStyle() & 0xFEFDFFFF);
/*     */     }
/*     */     
/* 214 */     SWTSkinObjectContainer soBottomArea = (SWTSkinObjectContainer)this.skin.getSkinObject("bottom-area");
/* 215 */     if (soBottomArea != null) {
/* 216 */       if (this.buttonsArea.getButtonCount() == 0) {
/* 217 */         soBottomArea.setVisible(false);
/*     */       } else {
/* 219 */         this.buttonsArea.swt_createButtons(soBottomArea.getComposite());
/*     */       }
/*     */     }
/*     */     
/* 223 */     if (this.vuzeMessageBoxListener != null) {
/* 224 */       this.soExtra = ((SWTSkinObjectContainer)this.skin.getSkinObject("middle-extra"));
/*     */       try {
/* 226 */         this.vuzeMessageBoxListener.shellReady(this.dlg.getShell(), this.soExtra);
/*     */       } catch (Exception e) {
/* 228 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/* 232 */     if (this.closed) {
/* 233 */       return;
/*     */     }
/* 235 */     this.dlg.open();
/*     */   }
/*     */   
/*     */   public Button[] getButtons() {
/* 239 */     return this.buttonsArea.getButtons();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAutoCloseInMS(int autoCloseInMS) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setHtml(String html) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setIconResource(String resource)
/*     */   {
/* 258 */     this.iconResource = resource;
/* 259 */     if (this.dlg != null) {
/* 260 */       SWTSkinObjectImage soTopLogo = (SWTSkinObjectImage)this.dlg.getSkin().getSkinObject("top-logo");
/* 261 */       if (soTopLogo != null) {
/* 262 */         soTopLogo.setImageByID(this.iconResource, null);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRelatedObject(Object relatedObject) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRelatedObjects(Object[] relatedObjects) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRemember(String rememberID, boolean rememberByDefault, String rememberText) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRememberText(String rememberText) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRememberOnlyIfButton(int button) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setUrl(String url) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int waitUntilClosed()
/*     */   {
/* 306 */     if (this.opened) {
/* 307 */       final AESemaphore2 sem = new AESemaphore2("waitUntilClosed");
/* 308 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 310 */           if (VuzeMessageBox.this.dlg == null) {
/* 311 */             sem.release();
/* 312 */             return;
/*     */           }
/* 314 */           if (!VuzeMessageBox.this.opened) {
/* 315 */             VuzeMessageBox.this.dlg.open();
/*     */           }
/* 317 */           Shell shell = VuzeMessageBox.this.dlg.getShell();
/* 318 */           if ((shell == null) || (shell.isDisposed())) {
/* 319 */             sem.release();
/* 320 */             return;
/*     */           }
/*     */           
/* 323 */           shell.addDisposeListener(new DisposeListener() {
/*     */             public void widgetDisposed(DisposeEvent e) {
/* 325 */               VuzeMessageBox.4.this.val$sem.release();
/*     */             }
/*     */           });
/*     */         }
/*     */       });
/*     */       
/* 331 */       if (Utils.isThisThreadSWT())
/*     */       {
/* 333 */         if (this.dlg != null) {
/* 334 */           Shell shell = this.dlg.getShell();
/* 335 */           if (shell != null) {
/* 336 */             Display d = shell.getDisplay();
/* 337 */             while (!shell.isDisposed()) {
/* 338 */               if (!d.readAndDispatch()) {
/* 339 */                 d.sleep();
/*     */               }
/*     */             }
/*     */           }
/* 343 */           skinDialogClosed(this.dlg);
/* 344 */           return this.buttonsArea.getButtonVal(this.result);
/*     */         }
/*     */       }
/* 347 */       sem.reserve();
/*     */     }
/*     */     
/* 350 */     skinDialogClosed(this.dlg);
/* 351 */     return this.buttonsArea.getButtonVal(this.result);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void skinDialogClosed(SkinnedDialog dialog)
/*     */   {
/* 358 */     synchronized (this.resultListeners) {
/* 359 */       int realResult = this.buttonsArea.getButtonVal(this.result);
/* 360 */       for (UserPrompterResultListener l : this.resultListeners) {
/*     */         try {
/* 362 */           l.prompterClosed(realResult);
/*     */         } catch (Exception e) {
/* 364 */           Debug.out(e);
/*     */         }
/*     */       }
/* 367 */       this.resultListeners.clear();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setListener(VuzeMessageBoxListener l) {
/* 372 */     this.vuzeMessageBoxListener = l;
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public void close(int buttonNo) {
/* 379 */     synchronized (this) {
/* 380 */       this.closed = true;
/* 381 */       this.result = buttonNo;
/* 382 */       if (this.dlg != null) {
/* 383 */         this.dlg.close();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void closeWithButtonVal(int buttonVal) {
/* 389 */     synchronized (this) {
/* 390 */       this.closed = true;
/* 391 */       this.result = this.buttonsArea.getButtonPosFromVal(buttonVal);
/* 392 */       if (this.dlg != null) {
/* 393 */         this.dlg.close();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void addResourceBundle(Class<?> cla, String path, String name)
/*     */   {
/* 400 */     synchronized (this.listRBs) {
/* 401 */       if (this.skin == null) {
/* 402 */         this.listRBs.add(new rbInfo(cla, path, name));
/* 403 */         return;
/*     */       }
/*     */     }
/*     */     
/* 407 */     String sFile = path + name;
/* 408 */     ClassLoader loader = cla.getClassLoader();
/* 409 */     ResourceBundle subBundle = ResourceBundle.getBundle(sFile, Locale.getDefault(), loader);
/*     */     
/*     */ 
/*     */ 
/* 413 */     SWTSkinProperties skinProperties = this.skin.getSkinProperties();
/* 414 */     skinProperties.addResourceBundle(subBundle, path, loader);
/*     */   }
/*     */   
/*     */   public void setTextIconResource(String resource) {
/* 418 */     this.textIconResource = resource;
/* 419 */     if (this.dlg != null) {
/* 420 */       SWTSkinObjectImage soIcon = (SWTSkinObjectImage)this.dlg.getSkin().getSkinObject("text-icon");
/* 421 */       if (soIcon != null) {
/* 422 */         soIcon.setImageByID(this.textIconResource, null);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void addListener(UserPrompterResultListener l) {
/* 428 */     if (l == null) {
/* 429 */       return;
/*     */     }
/* 431 */     synchronized (this.resultListeners) {
/* 432 */       this.resultListeners.add(l);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setDefaultButtonByPos(int pos) {
/* 437 */     if (this.dlg == null)
/* 438 */       this.buttonsArea.setDefaultButtonPos(pos); }
/*     */   
/*     */   public void setOneInstanceOf(String instanceID) {}
/*     */   
/*     */   private static class rbInfo { Class<?> cla;
/*     */     String path;
/*     */     String name;
/*     */     
/* 446 */     public rbInfo(Class<?> cla, String path, String name) { this.cla = cla;
/* 447 */       this.path = path;
/* 448 */       this.name = name;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/VuzeMessageBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */