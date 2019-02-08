/*     */ package org.gudy.azureus2.ui.swt.help;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.eclipse.swt.custom.CLabel;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.TraverseEvent;
/*     */ import org.eclipse.swt.events.TraverseListener;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
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
/*     */ public class HealthHelpWindow
/*     */ {
/*     */   public static void show(Display display)
/*     */   {
/*  46 */     ArrayList<String> imagesToRelease = new ArrayList();
/*     */     
/*  48 */     Shell window = ShellFactory.createShell(display, 67680);
/*     */     
/*  50 */     Utils.setShellIcon(window);
/*  51 */     window.setText(MessageText.getString("MyTorrentsView.menu.health"));
/*     */     
/*  53 */     Map mapIDs = new LinkedHashMap();
/*  54 */     mapIDs.put("grey", "st_stopped");
/*  55 */     mapIDs.put("red", "st_ko");
/*  56 */     mapIDs.put("blue", "st_no_tracker");
/*  57 */     mapIDs.put("yellow", "st_no_remote");
/*  58 */     mapIDs.put("green", "st_ok");
/*  59 */     mapIDs.put("error", "st_error");
/*  60 */     mapIDs.put("share", "st_shared");
/*     */     
/*  62 */     GridLayout layout = new GridLayout();
/*  63 */     layout.marginHeight = 3;
/*  64 */     layout.marginWidth = 3;
/*     */     try {
/*  66 */       layout.verticalSpacing = 3;
/*     */     }
/*     */     catch (NoSuchFieldError e) {}
/*     */     
/*  70 */     window.setLayout(layout);
/*     */     
/*  72 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  73 */     for (Iterator iter = mapIDs.keySet().iterator(); iter.hasNext();) {
/*  74 */       String key = (String)iter.next();
/*  75 */       String value = (String)mapIDs.get(key);
/*     */       
/*     */ 
/*  78 */       Image img = imageLoader.getImage(value);
/*  79 */       imagesToRelease.add(value);
/*     */       
/*  81 */       CLabel lbl = new CLabel(window, 0);
/*  82 */       lbl.setImage(img);
/*  83 */       lbl.setText(MessageText.getString("health.explain." + key));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  88 */     Button btnOk = new Button(window, 8);
/*  89 */     btnOk.setText(MessageText.getString("Button.ok"));
/*  90 */     GridData gridData = new GridData(64);
/*  91 */     gridData.widthHint = 70;
/*  92 */     Utils.setLayoutData(btnOk, gridData);
/*     */     
/*  94 */     btnOk.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/*  96 */         this.val$window.dispose();
/*     */       }
/*     */       
/*  99 */     });
/* 100 */     window.addTraverseListener(new TraverseListener() {
/*     */       public void keyTraversed(TraverseEvent e) {
/* 102 */         if (e.detail == 2) {
/* 103 */           this.val$window.dispose();
/*     */         }
/*     */         
/*     */       }
/* 107 */     });
/* 108 */     window.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent arg0) {
/* 110 */         ImageLoader imageLoader = ImageLoader.getInstance();
/* 111 */         for (String id : this.val$imagesToRelease) {
/* 112 */           imageLoader.releaseImage(id);
/*     */         }
/*     */         
/*     */       }
/* 116 */     });
/* 117 */     window.pack();
/* 118 */     window.open();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/help/HealthHelpWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */