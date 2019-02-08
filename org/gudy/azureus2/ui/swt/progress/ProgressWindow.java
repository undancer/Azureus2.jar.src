/*     */ package org.gudy.azureus2.ui.swt.progress;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreOperation;
/*     */ import com.aelitis.azureus.core.AzureusCoreOperationListener;
/*     */ import com.aelitis.azureus.core.AzureusCoreOperationTask;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
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
/*     */ public class ProgressWindow
/*     */ {
/*     */   private volatile Shell shell;
/*     */   private volatile boolean task_complete;
/*     */   private final String resource;
/*     */   private Image[] spinImages;
/*     */   
/*     */   public static void register(AzureusCore core)
/*     */   {
/*  54 */     core.addOperationListener(new AzureusCoreOperationListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public boolean operationCreated(AzureusCoreOperation operation)
/*     */       {
/*     */ 
/*  61 */         if (((operation.getOperationType() == 2) || (operation.getOperationType() == 3)) && (Utils.isThisThreadSWT()))
/*     */         {
/*     */ 
/*     */ 
/*  65 */           if (operation.getTask() != null)
/*     */           {
/*  67 */             new ProgressWindow(operation);
/*     */             
/*  69 */             return true;
/*     */           }
/*     */         }
/*     */         
/*  73 */         return false;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  83 */   protected int curSpinIndex = 0;
/*     */   
/*     */ 
/*     */ 
/*     */   protected ProgressWindow(final AzureusCoreOperation operation)
/*     */   {
/*  89 */     final RuntimeException[] error = { null };
/*     */     
/*  91 */     this.resource = (operation.getOperationType() == 2 ? "progress.window.msg.filemove" : "progress.window.msg.progress");
/*     */     
/*  93 */     new DelayedEvent("ProgWin", operation.getOperationType() == 2 ? 1000L : 10L, new AERunnable()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/*     */ 
/* 101 */         if (!ProgressWindow.this.task_complete)
/*     */         {
/* 103 */           Utils.execSWTThread(new Runnable()
/*     */           {
/*     */ 
/*     */             public void run()
/*     */             {
/*     */ 
/* 109 */               synchronized (ProgressWindow.this)
/*     */               {
/* 111 */                 if (!ProgressWindow.this.task_complete)
/*     */                 {
/* 113 */                   Shell shell = ShellFactory.createMainShell(2144);
/*     */                   
/*     */ 
/* 116 */                   ProgressWindow.this.showDialog(shell); } } } }, false);
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 125 */     });
/* 126 */     new AEThread2("ProgressWindow", true)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/* 134 */           AzureusCoreOperationTask task = operation.getTask();
/*     */           
/* 136 */           if (task == null)
/*     */           {
/* 138 */             throw new RuntimeException("Task not available");
/*     */           }
/*     */           
/* 141 */           task.run(operation);
/*     */         }
/*     */         catch (RuntimeException e)
/*     */         {
/* 145 */           error[0] = e;
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 149 */           error[0] = new RuntimeException(e);
/*     */         }
/*     */         finally
/*     */         {
/* 153 */           Utils.execSWTThread(new Runnable()
/*     */           {
/*     */ 
/*     */             public void run()
/*     */             {
/*     */ 
/* 159 */               ProgressWindow.this.destroy();
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */     }.start();
/*     */     try
/*     */     {
/* 167 */       Display display = SWTThread.getInstance().getDisplay();
/*     */       
/* 169 */       while ((!this.task_complete) && (!display.isDisposed()))
/*     */       {
/* 171 */         if (!display.readAndDispatch()) { display.sleep();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 177 */       synchronized (this)
/*     */       {
/* 179 */         this.task_complete = true;
/*     */       }
/*     */       try
/*     */       {
/* 183 */         if ((this.shell != null) && (!this.shell.isDisposed()))
/*     */         {
/* 185 */           this.shell.dispose();
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 189 */         Debug.printStackTrace(e);
/*     */       }
/*     */       
/*     */ 
/* 193 */       if (error[0] == null) {
/*     */         return;
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 177 */       synchronized (this)
/*     */       {
/* 179 */         this.task_complete = true;
/*     */       }
/*     */       try
/*     */       {
/* 183 */         if ((this.shell != null) && (!this.shell.isDisposed()))
/*     */         {
/* 185 */           this.shell.dispose();
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 189 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 195 */     throw error[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ProgressWindow(Shell _parent, String _resource, int _style, int _delay_millis)
/*     */   {
/* 206 */     this.resource = _resource;
/*     */     
/* 208 */     final Shell shell = new Shell(_parent, _style);
/*     */     
/* 210 */     if (_delay_millis <= 0)
/*     */     {
/* 212 */       showDialog(shell);
/*     */     }
/*     */     else
/*     */     {
/* 216 */       new DelayedEvent("ProgWin", _delay_millis, new AERunnable()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/*     */ 
/* 224 */           if (!ProgressWindow.this.task_complete)
/*     */           {
/* 226 */             Utils.execSWTThread(new Runnable()
/*     */             {
/*     */ 
/*     */               public void run()
/*     */               {
/*     */ 
/* 232 */                 synchronized (ProgressWindow.this)
/*     */                 {
/* 234 */                   if (!ProgressWindow.this.task_complete)
/*     */                   {
/* 236 */                     ProgressWindow.this.showDialog(ProgressWindow.4.this.val$shell); } } } }, false);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void showDialog(Shell _shell)
/*     */   {
/* 252 */     this.shell = _shell;
/*     */     
/* 254 */     this.shell.setText(MessageText.getString("progress.window.title"));
/*     */     
/* 256 */     Utils.setShellIcon(this.shell);
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
/* 272 */     GridLayout layout = new GridLayout();
/* 273 */     layout.numColumns = 2;
/* 274 */     this.shell.setLayout(layout);
/*     */     
/* 276 */     this.spinImages = ImageLoader.getInstance().getImages("working");
/*     */     
/* 278 */     if ((this.spinImages == null) || (this.spinImages.length == 0))
/*     */     {
/* 280 */       new Label(this.shell, 0);
/*     */     }
/*     */     else
/*     */     {
/* 284 */       final Rectangle spinBounds = this.spinImages[0].getBounds();
/* 285 */       final Canvas canvas = new Canvas(this.shell, 0)
/*     */       {
/*     */ 
/*     */         public Point computeSize(int wHint, int hHint, boolean changed)
/*     */         {
/* 290 */           return new Point(spinBounds.width, spinBounds.height);
/*     */         }
/*     */         
/* 293 */       };
/* 294 */       canvas.addPaintListener(new PaintListener() {
/*     */         public void paintControl(PaintEvent e) {
/* 296 */           e.gc.drawImage(ProgressWindow.this.spinImages[ProgressWindow.this.curSpinIndex], 0, 0);
/*     */         }
/*     */         
/* 299 */       });
/* 300 */       Utils.execSWTThreadLater(100, new AERunnable() {
/*     */         public void runSupport() {
/* 302 */           if ((canvas == null) || (canvas.isDisposed())) {
/* 303 */             return;
/*     */           }
/*     */           
/* 306 */           canvas.redraw();
/* 307 */           canvas.update();
/* 308 */           if (ProgressWindow.this.curSpinIndex == ProgressWindow.this.spinImages.length - 1) {
/* 309 */             ProgressWindow.this.curSpinIndex = 0;
/*     */           } else {
/* 311 */             ProgressWindow.this.curSpinIndex += 1;
/*     */           }
/* 313 */           Utils.execSWTThreadLater(100, this);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 320 */     Label label = new Label(this.shell, 0);
/*     */     
/* 322 */     label.setText(MessageText.getString(this.resource));
/* 323 */     GridData gridData = new GridData();
/* 324 */     label.setLayoutData(gridData);
/*     */     
/* 326 */     this.shell.pack();
/*     */     
/* 328 */     Composite parent = this.shell.getParent();
/*     */     
/* 330 */     if (parent != null)
/*     */     {
/* 332 */       Utils.centerWindowRelativeTo(this.shell, parent);
/*     */     }
/*     */     else
/*     */     {
/* 336 */       Utils.centreWindow(this.shell);
/*     */     }
/*     */     
/* 339 */     this.shell.open();
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 345 */     synchronized (this)
/*     */     {
/* 347 */       this.task_complete = true;
/*     */     }
/*     */     try
/*     */     {
/* 351 */       if ((this.shell != null) && (!this.shell.isDisposed()))
/*     */       {
/* 353 */         this.shell.dispose();
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 357 */       Debug.printStackTrace(e);
/*     */     }
/*     */     
/* 360 */     if (this.spinImages != null) {
/* 361 */       ImageLoader.getInstance().releaseImage("working");
/* 362 */       this.spinImages = null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/progress/ProgressWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */