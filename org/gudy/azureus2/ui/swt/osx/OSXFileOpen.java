/*    */ package org.gudy.azureus2.ui.swt.osx;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*    */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*    */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*    */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*    */ import java.lang.reflect.Field;
/*    */ import org.eclipse.swt.SWT;
/*    */ import org.eclipse.swt.widgets.Display;
/*    */ import org.eclipse.swt.widgets.Event;
/*    */ import org.eclipse.swt.widgets.Listener;
/*    */ import org.gudy.azureus2.platform.macosx.access.jnilib.OSXAccess;
/*    */ import org.gudy.azureus2.ui.swt.Utils;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class OSXFileOpen
/*    */ {
/*    */   public static void fileOpen(String[] files)
/*    */   {
/* 40 */     for (String file : files) {
/* 41 */       fileOpen(file);
/*    */     }
/*    */   }
/*    */   
/*    */   public static void fileOpen(String file) {
/* 46 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*    */       public void azureusCoreRunning(AzureusCore core) {
/* 48 */         UIFunctionsManagerSWT.getUIFunctionsSWT().openTorrentOpenOptions(Utils.findAnyShell(), null, new String[] { this.val$file }, false, false);
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void initLight()
/*    */   {
/*    */     try
/*    */     {
/* 61 */       Display display = new Display();
/*    */       
/*    */       try
/*    */       {
/* 65 */         Field fldOpenDoc = SWT.class.getDeclaredField("OpenDocument");
/* 66 */         int SWT_OpenDocument = fldOpenDoc.getInt(null);
/*    */         
/* 68 */         display.addListener(SWT_OpenDocument, new Listener() {
/*    */           public void handleEvent(Event event) {
/*    */             try {
/* 71 */               OSXAccess.passParameter(event.text);
/*    */             }
/*    */             catch (Throwable e) {}
/*    */           }
/*    */         });
/*    */       }
/*    */       catch (Throwable t) {}
/*    */       
/*    */ 
/*    */ 
/* 81 */       for (int i = 0; i < 10; i++) {
/* 82 */         while (display.readAndDispatch()) {}
/*    */         
/* 84 */         Thread.sleep(30L);
/*    */       }
/*    */     } catch (Throwable t) {
/* 87 */       t.printStackTrace();
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/osx/OSXFileOpen.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */