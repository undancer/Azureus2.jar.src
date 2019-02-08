/*    */ package com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.impl.tests;
/*    */ 
/*    */ import com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.Coordinates;
/*    */ import com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.VivaldiPosition;
/*    */ import com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.impl.HeightCoordinatesImpl;
/*    */ import com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.impl.VivaldiPositionImpl;
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class VivaldiTest
/*    */ {
/*    */   private static final int MAX_HEIGHT = 50;
/*    */   private static final int ELEMENTS_X = 20;
/*    */   private static final int ELEMENTS_Y = 20;
/*    */   private static final int DISTANCE = 10;
/*    */   private static final int MAX_ITERATIONS = 1000;
/*    */   private static final int NB_CONTACTS = 7;
/*    */   
/*    */   public VivaldiTest()
/*    */   {
/* 37 */     VivaldiPosition[][] positions = new VivaldiPosition[20][20];
/* 38 */     Coordinates[][] realCoordinates = new Coordinates[20][20];
/*    */     
/* 40 */     for (int i = 0; i < 20; i++) {
/* 41 */       for (int j = 0; j < 20; j++) {
/* 42 */         realCoordinates[i][j] = new HeightCoordinatesImpl(i * 10, j * 10, 50.0F);
/* 43 */         positions[i][j] = new VivaldiPositionImpl(new HeightCoordinatesImpl(0.0F, 0.0F, 0.0F));
/*    */       }
/*    */     }
/*    */     
/*    */ 
/* 48 */     for (int iter = 0; iter < 1000; iter++)
/*    */     {
/* 50 */       for (int i = 0; i < 20; i++) {
/* 51 */         for (int j = 0; j < 20; j++) {
/* 52 */           VivaldiPosition position = positions[i][j];
/*    */           
/* 54 */           for (int k = 0; k < 7; k++) {
/* 55 */             int i1 = (int)(Math.random() * 20.0D);
/* 56 */             int j1 = (int)(Math.random() * 20.0D);
/* 57 */             if ((i1 != i) || (j1 != j)) {
/* 58 */               VivaldiPosition position1 = positions[i1][j1];
/* 59 */               float rtt = realCoordinates[i1][j1].distance(realCoordinates[i][j]);
/* 60 */               position.update(rtt, position1.getCoordinates(), position1.getErrorEstimate());
/*    */             } }
/* 62 */           if (iter == 999) {
/* 63 */             System.out.println(iter + " (" + i + "," + j + ") : " + realCoordinates[i][j] + " , " + position);
/*    */           }
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public static void main(String[] args)
/*    */   {
/* 72 */     new VivaldiTest();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/netcoords/vivaldi/ver1/impl/tests/VivaldiTest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */