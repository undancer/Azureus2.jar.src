/*     */ package org.gudy.azureus2.ui.swt.speedtest;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctions.actionListener;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import java.util.HashMap;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCException;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
/*     */ import org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel;
/*     */ import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;
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
/*     */ public class SpeedTestSelector
/*     */   extends AbstractWizardPanel<SpeedTestWizard>
/*     */ {
/*  52 */   private boolean mlab_test = true;
/*     */   
/*     */   public SpeedTestSelector(SpeedTestWizard wizard, IWizardPanel previous) {
/*  55 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */   public void show() {
/*  59 */     ((SpeedTestWizard)this.wizard).setTitle(MessageText.getString("speedtest.wizard.select.title"));
/*  60 */     ((SpeedTestWizard)this.wizard).setCurrentInfo("");
/*  61 */     Composite rootPanel = ((SpeedTestWizard)this.wizard).getPanel();
/*  62 */     GridLayout layout = new GridLayout();
/*  63 */     layout.numColumns = 1;
/*  64 */     rootPanel.setLayout(layout);
/*     */     
/*  66 */     Composite panel = new Composite(rootPanel, 0);
/*  67 */     GridData gridData = new GridData(1808);
/*  68 */     panel.setLayoutData(gridData);
/*  69 */     layout = new GridLayout();
/*  70 */     layout.numColumns = 1;
/*  71 */     panel.setLayout(layout);
/*     */     
/*  73 */     Group gRadio = new Group(panel, 0);
/*  74 */     Messages.setLanguageText(gRadio, "speedtest.wizard.select.group");
/*  75 */     gRadio.setLayoutData(gridData);
/*  76 */     layout = new GridLayout();
/*  77 */     layout.numColumns = 1;
/*  78 */     gRadio.setLayout(layout);
/*  79 */     gridData = new GridData(768);
/*  80 */     gRadio.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  85 */     Button auto_button = new Button(gRadio, 16);
/*  86 */     Messages.setLanguageText(auto_button, "speedtest.wizard.select.general");
/*  87 */     auto_button.setSelection(true);
/*     */     
/*     */ 
/*     */ 
/*  91 */     final Button manual_button = new Button(gRadio, 16);
/*  92 */     Messages.setLanguageText(manual_button, "speedtest.wizard.select.bt");
/*     */     
/*  94 */     manual_button.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event arg0)
/*     */       {
/*     */ 
/*     */ 
/* 102 */         SpeedTestSelector.this.mlab_test = (!manual_button.getSelection());
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isNextEnabled()
/*     */   {
/* 112 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPreviousEnabled()
/*     */   {
/* 118 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public IWizardPanel getNextPanel()
/*     */   {
/* 124 */     if (this.mlab_test)
/*     */     {
/* 126 */       ((SpeedTestWizard)this.wizard).close();
/*     */       
/* 128 */       runMLABTest(null);
/*     */       
/*     */ 
/*     */ 
/* 132 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 136 */     return new SpeedTestPanel((SpeedTestWizard)this.wizard, null);
/*     */   }
/*     */   
/*     */   public static void runMLABTest(Runnable runWhenClosed)
/*     */   {
/* 141 */     CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/* 143 */         UIFunctionsManager.getUIFunctions().installPlugin("mlab", "dlg.install.mlab", new UIFunctions.actionListener()
/*     */         {
/*     */           public void actionComplete(Object result) {
/* 146 */             if ((result instanceof Boolean)) {
/* 147 */               SpeedTestSelector._runMLABTest(SpeedTestSelector.2.this.val$runWhenClosed);
/*     */             } else {
/*     */               try
/*     */               {
/* 151 */                 Throwable error = (Throwable)result;
/*     */                 
/* 153 */                 Debug.out(error);
/*     */               }
/*     */               finally {
/* 156 */                 if (SpeedTestSelector.2.this.val$runWhenClosed != null) {
/* 157 */                   SpeedTestSelector.2.this.val$runWhenClosed.run();
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private static void _runMLABTest(Runnable runWhenClosed) {
/* 168 */     PluginInterface pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("mlab");
/*     */     
/*     */ 
/* 171 */     if (pi == null) {
/* 172 */       Debug.out("mlab plugin not available");
/* 173 */       if (runWhenClosed != null) {
/* 174 */         runWhenClosed.run();
/*     */       }
/*     */     } else {
/*     */       try {
/* 178 */         HashMap<String, Object> map = new HashMap();
/*     */         
/* 180 */         pi.getIPC().invoke("runTest", new Object[] { map, new IPCInterface()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           public Object invoke(String methodName, Object[] params)
/*     */             throws IPCException
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 192 */             COConfigurationManager.setParameter("SpeedTest Completed", true);
/*     */             
/* 194 */             if (this.val$runWhenClosed != null) {
/* 195 */               this.val$runWhenClosed.run();
/*     */             }
/* 197 */             return null;
/*     */           }
/*     */           
/*     */ 
/* 201 */           public boolean canInvoke(String methodName, Object[] params) { return true; } }, Boolean.valueOf(true) });
/*     */ 
/*     */ 
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*     */ 
/*     */ 
/* 209 */         Debug.out(e);
/* 210 */         if (runWhenClosed != null) {
/* 211 */           runWhenClosed.run();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/speedtest/SpeedTestSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */