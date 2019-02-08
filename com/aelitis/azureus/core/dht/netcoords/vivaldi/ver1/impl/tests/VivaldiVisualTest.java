/*     */ package com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.impl.tests;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.VivaldiPosition;
/*     */ import com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.impl.HeightCoordinatesImpl;
/*     */ import com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.impl.VivaldiPositionImpl;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.ui.swt.views.stats.VivaldiPanel;
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
/*     */ public class VivaldiVisualTest
/*     */ {
/*     */   private static final int MAX_HEIGHT = 20;
/*     */   private static final int ELEMENTS_X = 20;
/*     */   private static final int ELEMENTS_Y = 20;
/*     */   private static final int DISTANCE = 50;
/*     */   private static final int MAX_ITERATIONS = 10000;
/*     */   private static final int NB_CONTACTS = 5;
/*     */   
/*     */   public void start()
/*     */   {
/*  47 */     final Display display = new Display();
/*  48 */     Shell shell = new Shell(display);
/*  49 */     final VivaldiPanel panel = new VivaldiPanel(shell);
/*  50 */     shell.setLayout(new FillLayout());
/*  51 */     shell.setSize(800, 800);
/*  52 */     shell.setText("Vivaldi Simulator");
/*  53 */     shell.open();
/*     */     
/*  55 */     Thread runner = new Thread("Viviladi Simulator") {
/*     */       public void run() {
/*  57 */         VivaldiPosition[][] positions = new VivaldiPosition[20][20];
/*  58 */         final List<VivaldiPosition> lPos = new ArrayList(400);
/*  59 */         HeightCoordinatesImpl[][] realCoordinates = new HeightCoordinatesImpl[20][20];
/*     */         
/*  61 */         for (int i = 0; i < 20; i++) {
/*  62 */           for (int j = 0; j < 20; j++) {
/*  63 */             realCoordinates[i][j] = new HeightCoordinatesImpl(i * 50 - 500, j * 50 - 500, 20.0F);
/*  64 */             if (i >= 10) {}
/*     */             
/*     */ 
/*     */ 
/*  68 */             positions[i][j] = new VivaldiPositionImpl(new HeightCoordinatesImpl(1000 + 50 * i, 1000 + 50 * j, 20.0F));
/*     */             
/*     */ 
/*  71 */             lPos.add(positions[i][j]);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*  76 */         for (int iter = 0; iter < 10000; iter++) {
/*  77 */           if (iter % 100 == 0) System.out.println(iter);
/*  78 */           if (display.isDisposed()) return;
/*  79 */           display.syncExec(new Runnable() {
/*     */             public void run() {
/*  81 */               VivaldiVisualTest.1.this.val$panel.refresh(lPos);
/*     */             }
/*     */           });
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  91 */           for (int i = 0; i < 20; i++) {
/*  92 */             for (int j = 0; j < 20; j++) {
/*  93 */               VivaldiPosition position = positions[i][j];
/*     */               
/*  95 */               for (int k = 0; k < 5; k++) {
/*  96 */                 int i1 = (int)(Math.random() * 20.0D);
/*  97 */                 int j1 = (int)(Math.random() * 20.0D);
/*  98 */                 if ((i1 != i) || (j1 != j)) {
/*  99 */                   VivaldiPosition position1 = positions[i1][j1];
/* 100 */                   float rtt = realCoordinates[i1][j1].distance(realCoordinates[i][j]);
/*     */                   
/* 102 */                   position.update(rtt, position1.getCoordinates(), position1.getErrorEstimate());
/*     */                 }
/*     */                 
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 110 */     };
/* 111 */     runner.setDaemon(true);
/* 112 */     runner.start();
/*     */     
/* 114 */     while (!shell.isDisposed()) {
/* 115 */       if (!display.readAndDispatch()) display.sleep();
/*     */     }
/* 117 */     display.dispose();
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 121 */     new VivaldiVisualTest().start();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/netcoords/vivaldi/ver1/impl/tests/VivaldiVisualTest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */