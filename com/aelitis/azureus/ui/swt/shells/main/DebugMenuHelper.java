/*     */ package com.aelitis.azureus.ui.swt.shells.main;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.VuzeMessageBox;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.VuzeMessageBoxListener;
/*     */ import org.eclipse.swt.dnd.Clipboard;
/*     */ import org.eclipse.swt.dnd.ImageTransfer;
/*     */ import org.eclipse.swt.dnd.Transfer;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.debug.ObfusticateShell;
/*     */ import org.gudy.azureus2.ui.swt.donations.DonationWindow;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
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
/*     */ public class DebugMenuHelper
/*     */ {
/*     */   public static MenuItem createDebugMenuItem(Menu menuDebug)
/*     */   {
/*  66 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*  67 */     if (null == uiFunctions) {
/*  68 */       throw new IllegalStateException("UIFunctionsManagerSWT.getUIFunctionsSWT() is returning null");
/*     */     }
/*     */     
/*     */ 
/*  72 */     MenuItem item = new MenuItem(menuDebug, 64);
/*  73 */     item.setText("Run GC");
/*  74 */     item.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event) {}
/*     */ 
/*  79 */     });
/*  80 */     item = new MenuItem(menuDebug, 8);
/*  81 */     item.setText("&CoreReq");
/*  82 */     item.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/*  84 */         CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener() {
/*     */           public void azureusCoreRunning(AzureusCore core) {
/*  86 */             new MessageBoxShell(0, "Done", "Core Now Avail").open(null);
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
/*     */           }
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
/*     */         });
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
/*     */       }
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
/* 186 */     });
/* 187 */     item = new MenuItem(menuDebug, 64);
/* 188 */     item.setText("DW");
/* 189 */     Menu menuBrowserTB = new Menu(menuDebug.getParent(), 4);
/* 190 */     item.setMenu(menuBrowserTB);
/*     */     
/* 192 */     item = new MenuItem(menuBrowserTB, 0);
/* 193 */     item.setText("popup check");
/* 194 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 196 */         boolean oldDebug = DonationWindow.DEBUG;
/* 197 */         DonationWindow.DEBUG = true;
/* 198 */         DonationWindow.checkForDonationPopup();
/* 199 */         DonationWindow.DEBUG = oldDebug;
/*     */       }
/* 201 */     });
/* 202 */     item = new MenuItem(menuBrowserTB, 0);
/* 203 */     item.setText("show");
/* 204 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 206 */         boolean oldDebug = DonationWindow.DEBUG;
/* 207 */         DonationWindow.DEBUG = true;
/* 208 */         DonationWindow.open(true, "debug");
/* 209 */         DonationWindow.DEBUG = oldDebug;
/*     */       }
/*     */       
/* 212 */     });
/* 213 */     item = new MenuItem(menuDebug, 0);
/* 214 */     item.setText("Alerts");
/* 215 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 217 */         String text = "This is a  long message with lots of information and stuff you really should read.  Are you still reading? Good, because reading <a href=\"http://moo.com\">stimulates</a> the mind.\n\nYeah Baby.";
/*     */         
/*     */ 
/*     */ 
/* 221 */         LogAlert logAlert = new LogAlert(true, 0, "Simple");
/* 222 */         Logger.log(logAlert);
/* 223 */         logAlert = new LogAlert(true, 1, text);
/* 224 */         logAlert.details = ("Details: \n\n" + text);
/* 225 */         Logger.log(logAlert);
/* 226 */         logAlert = new LogAlert(true, 3, "ShortText");
/* 227 */         logAlert.details = "Details";
/* 228 */         Logger.log(logAlert);
/*     */       }
/*     */       
/* 231 */     });
/* 232 */     item = new MenuItem(menuDebug, 0);
/* 233 */     item.setText("MsgBox");
/* 234 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 236 */         VuzeMessageBox box = new VuzeMessageBox("Title", "Text", new String[] { "Ok", "Cancel" }, 0);
/* 237 */         box.setListener(new VuzeMessageBoxListener() {
/*     */           public void shellReady(Shell shell, SWTSkinObjectContainer soExtra) {
/* 239 */             SWTSkin skin = soExtra.getSkin();
/* 240 */             skin.createSkinObject("dlg.generic.test", "dlg.generic.test", soExtra);
/* 241 */             skin.layout(soExtra);
/* 242 */             shell.layout(true, true);
/*     */           }
/* 244 */         });
/* 245 */         box.open(null);
/*     */       }
/*     */       
/* 248 */     });
/* 249 */     item = new MenuItem(menuDebug, 64);
/* 250 */     item.setText("Size");
/* 251 */     Menu menuSize = new Menu(menuDebug.getParent(), 4);
/* 252 */     item.setMenu(menuSize);
/*     */     
/* 254 */     int[] sizes = { 640, 430, 800, 550, 1024, 718, 1280, 700, 1440, 850, 1600, 1050, 1920, 1150 };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 263 */     for (int i = 0; i < sizes.length; i += 2) {
/* 264 */       int x = sizes[i];
/* 265 */       final int y = sizes[(i + 1)];
/* 266 */       item = new MenuItem(menuSize, 0);
/* 267 */       item.setText("" + x + "," + y);
/* 268 */       item.addSelectionListener(new SelectionAdapter() {
/*     */         public void widgetSelected(SelectionEvent e) {
/* 270 */           UIFunctionsManagerSWT.getUIFunctionsSWT().getMainShell().setSize(this.val$x, y);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 275 */     item = new MenuItem(menuDebug, 0);
/* 276 */     item.setText("Obfuscated Shell Image");
/* 277 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent ev) {
/* 279 */         Display display = Display.getCurrent();
/* 280 */         Shell[] shells = display.getShells();
/* 281 */         for (int i = 0; i < shells.length; i++) {
/*     */           try {
/* 283 */             Shell shell = shells[i];
/* 284 */             Image image = null;
/*     */             
/* 286 */             if ((shell.isDisposed()) || (shell.isVisible()))
/*     */             {
/*     */ 
/*     */ 
/* 290 */               if ((shell.getData("class") instanceof ObfusticateShell)) {
/* 291 */                 ObfusticateShell shellClass = (ObfusticateShell)shell.getData("class");
/*     */                 try
/*     */                 {
/* 294 */                   image = shellClass.generateObfusticatedImage();
/*     */                 } catch (Exception e) {
/* 296 */                   Debug.out("Obfuscating shell " + shell, e);
/*     */                 }
/*     */               }
/*     */               else {
/* 300 */                 Rectangle clientArea = shell.getClientArea();
/* 301 */                 image = new Image(display, clientArea.width, clientArea.height);
/*     */                 
/* 303 */                 GC gc = new GC(shell);
/*     */                 try {
/* 305 */                   gc.copyArea(image, clientArea.x, clientArea.y);
/*     */                 } finally {
/* 307 */                   gc.dispose();
/*     */                 }
/*     */               }
/*     */               
/* 311 */               if (image != null) {
/* 312 */                 Shell shell2 = new Shell(display);
/* 313 */                 Rectangle bounds = image.getBounds();
/* 314 */                 Point size = shell2.computeSize(bounds.width, bounds.height);
/* 315 */                 shell2.setSize(size);
/* 316 */                 shell2.setBackgroundImage(image);
/* 317 */                 shell2.open();
/*     */                 
/* 319 */                 new Clipboard(display).setContents(new Object[] { image.getImageData() }, new Transfer[] { ImageTransfer.getInstance() });
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Exception e)
/*     */           {
/* 325 */             Logger.log(new LogEvent(LogIDs.GUI, "Creating Obfusticated Image", e));
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 333 */     });
/* 334 */     return item;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/main/DebugMenuHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */