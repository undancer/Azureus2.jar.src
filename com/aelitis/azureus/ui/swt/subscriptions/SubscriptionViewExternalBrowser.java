/*     */ package com.aelitis.azureus.ui.swt.subscriptions;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.core.subs.Subscription;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImage;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import java.util.Locale;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*     */ import org.gudy.azureus2.ui.webplugin.WebPlugin;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SubscriptionViewExternalBrowser
/*     */   implements SubscriptionsViewBase
/*     */ {
/*     */   private Subscription subs;
/*     */   private Composite parent_composite;
/*     */   private Composite composite;
/*     */   private SubscriptionMDIEntry mdiInfo;
/*     */   private UISWTView swtView;
/*     */   
/*     */   public void refreshView() {}
/*     */   
/*     */   private void launchView()
/*     */   {
/*  75 */     PluginInterface xmweb_ui = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("xmwebui");
/*     */     
/*  77 */     if ((xmweb_ui == null) || (!xmweb_ui.getPluginState().isOperational()))
/*     */     {
/*  79 */       UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*     */       
/*  81 */       MessageBoxShell mb = new MessageBoxShell(33, MessageText.getString("external.browser.failed"), MessageText.getString("xmwebui.required"));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  86 */       mb.setParent(uiFunctions.getMainShell());
/*     */       
/*  88 */       mb.open(null);
/*     */     }
/*     */     else
/*     */     {
/*  92 */       WebPlugin wp = (WebPlugin)xmweb_ui.getPlugin();
/*     */       
/*  94 */       String remui = wp.getProtocol().toLowerCase(Locale.US) + "://127.0.0.1:" + wp.getPort() + "/";
/*     */       
/*  96 */       String test_url = ConstantsVuze.getDefaultContentNetwork().getServiceURL(2, new Object[] { "", Boolean.valueOf(false) });
/*     */       
/*  98 */       int pos = test_url.indexOf('?');
/*     */       
/* 100 */       String mode = xmweb_ui.getUtilities().getFeatureManager().isFeatureInstalled("core") ? "plus" : "trial";
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 105 */       String query = "Subscription: " + UrlUtils.encode(this.subs.getName()) + " (" + this.subs.getID() + ")";
/*     */       
/* 107 */       String search_url = test_url.substring(0, pos + 1) + "q=" + UrlUtils.encode(query) + "&" + "mode=" + mode + "&" + "search_source=" + UrlUtils.encode(remui);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 113 */       Utils.launch(search_url);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void initialize(Composite _parent_composite)
/*     */   {
/* 121 */     this.parent_composite = _parent_composite;
/*     */     
/* 123 */     this.composite = new Composite(this.parent_composite, 0);
/*     */     
/* 125 */     GridLayout layout = new GridLayout(3, false);
/*     */     
/* 127 */     layout.marginHeight = 32;
/* 128 */     layout.marginWidth = 32;
/*     */     
/* 130 */     this.composite.setLayout(layout);
/*     */     
/* 132 */     Label label = new Label(this.composite, 0);
/* 133 */     GridData gd = new GridData(768);
/* 134 */     gd.horizontalSpan = 3;
/* 135 */     label.setLayoutData(gd);
/* 136 */     Messages.setLanguageText(label, "subs.ext.view.info");
/*     */     
/* 138 */     label = new Label(this.composite, 0);
/* 139 */     Messages.setLanguageText(label, "subs.ext.view.launch.info");
/*     */     
/* 141 */     Button button = new Button(this.composite, 8);
/* 142 */     Messages.setLanguageText(button, "iconBar.run");
/*     */     
/* 144 */     button.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void widgetSelected(SelectionEvent e)
/*     */       {
/*     */ 
/* 151 */         SubscriptionViewExternalBrowser.this.launchView();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private Composite getComposite()
/*     */   {
/* 159 */     return this.composite;
/*     */   }
/*     */   
/*     */ 
/*     */   private String getFullTitle()
/*     */   {
/* 165 */     if (this.subs == null)
/*     */     {
/* 167 */       return "";
/*     */     }
/*     */     
/* 170 */     return this.subs.getName();
/*     */   }
/*     */   
/*     */ 
/*     */   private void viewActivated()
/*     */   {
/* 176 */     if ((this.subs != null) && (this.mdiInfo == null))
/*     */     {
/* 178 */       this.mdiInfo = ((SubscriptionMDIEntry)this.subs.getUserData(SubscriptionManagerUI.SUB_ENTRYINFO_KEY));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void viewDeactivated()
/*     */   {
/* 185 */     if ((this.mdiInfo != null) && (this.mdiInfo.spinnerImage != null))
/*     */     {
/* 187 */       this.mdiInfo.spinnerImage.setVisible(false);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void dataSourceChanged(Object data)
/*     */   {
/* 195 */     if ((data instanceof Subscription))
/*     */     {
/* 197 */       this.subs = ((Subscription)data);
/*     */       
/* 199 */       this.mdiInfo = ((SubscriptionMDIEntry)this.subs.getUserData(SubscriptionManagerUI.SUB_ENTRYINFO_KEY));
/*     */     }
/*     */     
/* 202 */     if ((this.subs != null) && (this.swtView != null))
/*     */     {
/* 204 */       this.swtView.setTitle(getFullTitle());
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 209 */     switch (event.getType()) {
/*     */     case 0: 
/* 211 */       this.swtView = ((UISWTView)event.getData());
/* 212 */       this.swtView.setTitle(getFullTitle());
/* 213 */       break;
/*     */     
/*     */     case 7: 
/*     */       break;
/*     */     
/*     */     case 2: 
/* 219 */       initialize((Composite)event.getData());
/* 220 */       break;
/*     */     
/*     */     case 6: 
/* 223 */       Messages.updateLanguageForControl(getComposite());
/* 224 */       this.swtView.setTitle(getFullTitle());
/* 225 */       break;
/*     */     
/*     */     case 1: 
/* 228 */       dataSourceChanged(event.getData());
/* 229 */       break;
/*     */     
/*     */     case 3: 
/* 232 */       viewActivated();
/* 233 */       break;
/*     */     
/*     */     case 4: 
/* 236 */       viewDeactivated();
/* 237 */       break;
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/* 243 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/subscriptions/SubscriptionViewExternalBrowser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */