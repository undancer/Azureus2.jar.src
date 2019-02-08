/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.TableColumn;
/*     */ import org.eclipse.swt.widgets.TableItem;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.LocaleTorrentUtil;
/*     */ import org.gudy.azureus2.core3.internat.LocaleUtil;
/*     */ import org.gudy.azureus2.core3.internat.LocaleUtilDecoder;
/*     */ import org.gudy.azureus2.core3.internat.LocaleUtilDecoderCandidate;
/*     */ import org.gudy.azureus2.core3.internat.LocaleUtilEncodingException;
/*     */ import org.gudy.azureus2.core3.internat.LocaleUtilListener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class LocaleUtilSWT
/*     */   implements LocaleUtilListener
/*     */ {
/*  49 */   protected static boolean rememberEncodingDecision = true;
/*  50 */   protected static LocaleUtilDecoder rememberedDecoder = null;
/*     */   
/*     */ 
/*     */   protected static Object remembered_on_behalf_of;
/*     */   
/*     */ 
/*     */   public LocaleUtilSWT(AzureusCore core)
/*     */   {
/*  58 */     LocaleTorrentUtil.addListener(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public LocaleUtilDecoderCandidate selectDecoder(LocaleUtil locale_util, Object decision_owner, LocaleUtilDecoderCandidate[] candidates)
/*     */     throws LocaleUtilEncodingException
/*     */   {
/*  70 */     if (decision_owner != remembered_on_behalf_of)
/*     */     {
/*  72 */       remembered_on_behalf_of = decision_owner;
/*  73 */       rememberedDecoder = null;
/*     */     }
/*     */     
/*  76 */     if ((rememberEncodingDecision) && (rememberedDecoder != null))
/*     */     {
/*  78 */       for (int i = 0; i < candidates.length; i++)
/*     */       {
/*  80 */         if ((candidates[i].getValue() != null) && (rememberedDecoder == candidates[i].getDecoder())) {
/*  81 */           return candidates[i];
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*  86 */     LocaleUtilDecoderCandidate default_candidate = candidates[0];
/*     */     
/*  88 */     String defaultString = candidates[0].getValue();
/*     */     
/*  90 */     Arrays.sort(candidates);
/*     */     
/*  92 */     boolean always_prompt = COConfigurationManager.getBooleanParameter("File.Decoder.Prompt", false);
/*     */     
/*  94 */     if (!always_prompt)
/*     */     {
/*  96 */       int minlength = candidates[0].getValue().length();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 101 */       if ((defaultString != null) && (defaultString.length() == minlength)) {
/* 102 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 107 */       String default_name = COConfigurationManager.getStringParameter("File.Decoder.Default", "");
/*     */       
/* 109 */       if (default_name.length() > 0) {
/* 110 */         for (int i = 0; i < candidates.length; i++) {
/* 111 */           if ((candidates[i].getValue() != null) && (candidates[i].getDecoder().getName().equals(default_name)))
/*     */           {
/* 113 */             return candidates[i];
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 119 */     ArrayList choosableCandidates = new ArrayList();
/*     */     
/*     */ 
/*     */ 
/* 123 */     if (defaultString != null)
/*     */     {
/* 125 */       choosableCandidates.add(default_candidate);
/*     */     }
/*     */     
/* 128 */     LocaleUtilDecoder[] general_decoders = locale_util.getGeneralDecoders();
/*     */     
/*     */ 
/*     */ 
/* 132 */     for (int j = 0; j < general_decoders.length; j++)
/*     */     {
/* 134 */       for (int i = 0; i < candidates.length; i++)
/*     */       {
/* 136 */         if ((candidates[i].getValue() != null) && (candidates[i].getDecoder() != null))
/*     */         {
/* 138 */           if ((general_decoders[j] != null) && (general_decoders[j].getName().equals(candidates[i].getDecoder().getName())))
/*     */           {
/*     */ 
/* 141 */             if (!choosableCandidates.contains(candidates[i]))
/*     */             {
/* 143 */               choosableCandidates.add(candidates[i]);
/*     */               
/* 145 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 153 */     for (int i = 0; i < candidates.length; i++)
/*     */     {
/* 155 */       if ((candidates[i].getValue() != null) && (candidates[i].getDecoder() != null))
/*     */       {
/* 157 */         if (!choosableCandidates.contains(candidates[i]))
/*     */         {
/* 159 */           choosableCandidates.add(candidates[i]);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 164 */     final LocaleUtilDecoderCandidate[] candidatesToChoose = (LocaleUtilDecoderCandidate[])choosableCandidates.toArray(new LocaleUtilDecoderCandidate[choosableCandidates.size()]);
/* 165 */     final LocaleUtilDecoderCandidate[] selected_candidate = { null };
/*     */     
/*     */ 
/* 168 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/*     */         try {
/* 171 */           LocaleUtilSWT.this.showChoosableEncodingWindow(Utils.findAnyShell(), candidatesToChoose, selected_candidate);
/*     */ 
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 176 */           Debug.printStackTrace(e); } } }, false);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 182 */     if (selected_candidate[0] == null)
/*     */     {
/* 184 */       throw new LocaleUtilEncodingException(true);
/*     */     }
/*     */     
/* 187 */     return selected_candidate[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void showChoosableEncodingWindow(Shell shell, final LocaleUtilDecoderCandidate[] candidates, final LocaleUtilDecoderCandidate[] selected_candidate)
/*     */   {
/* 197 */     final Shell s = ShellFactory.createShell(shell, 34928);
/*     */     
/* 199 */     Utils.setShellIcon(s);
/* 200 */     s.setText(MessageText.getString("LocaleUtil.title"));
/*     */     
/* 202 */     s.setLayout(new GridLayout(1, true));
/*     */     
/* 204 */     Label label = new Label(s, 16384);
/* 205 */     Messages.setLanguageText(label, "LocaleUtil.label.chooseencoding");
/*     */     
/* 207 */     final Table table = new Table(s, 68100);
/* 208 */     GridData gridData = new GridData(1808);
/* 209 */     Utils.setLayoutData(table, gridData);
/*     */     
/* 211 */     table.setLinesVisible(true);
/* 212 */     table.setHeaderVisible(true);
/*     */     
/* 214 */     String[] titlesPieces = { "encoding", "text" };
/* 215 */     for (int i = 0; i < titlesPieces.length; i++) {
/* 216 */       TableColumn column = new TableColumn(table, 16384);
/* 217 */       Messages.setLanguageText(column, "LocaleUtil.column." + titlesPieces[i]);
/*     */     }
/*     */     
/*     */ 
/* 221 */     for (int i = 0; i < candidates.length; i++) {
/* 222 */       TableItem item = new TableItem(table, 0);
/* 223 */       String name = candidates[i].getDecoder().getName();
/* 224 */       item.setText(0, name);
/* 225 */       item.setText(1, candidates[i].getValue());
/*     */     }
/* 227 */     int lastSelectedIndex = 0;
/* 228 */     for (int i = 1; i < candidates.length; i++) {
/* 229 */       if ((candidates[i].getValue() != null) && (candidates[i].getDecoder() == rememberedDecoder)) {
/* 230 */         lastSelectedIndex = i;
/* 231 */         break;
/*     */       }
/*     */     }
/* 234 */     table.select(lastSelectedIndex);
/*     */     
/*     */ 
/* 237 */     table.getColumn(0).pack();
/* 238 */     table.getColumn(1).pack();
/*     */     
/* 240 */     label = new Label(s, 16384);
/* 241 */     Messages.setLanguageText(label, "LocaleUtil.label.hint.doubleclick");
/*     */     
/* 243 */     Composite composite = new Composite(s, 0);
/* 244 */     gridData = new GridData(768);
/* 245 */     Utils.setLayoutData(composite, gridData);
/*     */     
/* 247 */     GridLayout subLayout = new GridLayout();
/* 248 */     subLayout.numColumns = 2;
/*     */     
/* 250 */     composite.setLayout(subLayout);
/*     */     
/* 252 */     final Button checkBox = new Button(composite, 32);
/* 253 */     Utils.setLayoutData(checkBox, new GridData(32));
/* 254 */     checkBox.setSelection(rememberEncodingDecision);
/* 255 */     Messages.setLanguageText(checkBox, "LocaleUtil.label.checkbox.rememberdecision");
/*     */     
/* 257 */     Button ok = new Button(composite, 8);
/* 258 */     ok.setText(" ".concat(MessageText.getString("Button.next")).concat(" "));
/* 259 */     gridData = new GridData(3);
/* 260 */     gridData.widthHint = 100;
/* 261 */     Utils.setLayoutData(ok, gridData);
/*     */     
/*     */ 
/*     */ 
/* 265 */     s.setSize(500, 500);
/* 266 */     s.layout();
/*     */     
/* 268 */     Utils.centreWindow(s);
/*     */     
/* 270 */     ok.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */       public void widgetSelected(SelectionEvent event) {
/* 273 */         LocaleUtilSWT.this.setSelectedIndex(s, table, checkBox, candidates, selected_candidate);
/* 274 */         s.dispose();
/*     */       }
/*     */       
/* 277 */     });
/* 278 */     table.addMouseListener(new MouseAdapter() {
/*     */       public void mouseDoubleClick(MouseEvent mEvent) {
/* 280 */         LocaleUtilSWT.this.setSelectedIndex(s, table, checkBox, candidates, selected_candidate);
/* 281 */         s.dispose();
/*     */       }
/*     */       
/* 284 */     });
/* 285 */     s.open();
/* 286 */     while (!s.isDisposed()) {
/* 287 */       if (!s.getDisplay().readAndDispatch()) {
/* 288 */         s.getDisplay().sleep();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setSelectedIndex(Shell s, Table table, Button checkBox, LocaleUtilDecoderCandidate[] candidates, LocaleUtilDecoderCandidate[] selected_candidate)
/*     */   {
/* 301 */     int selectedIndex = table.getSelectionIndex();
/*     */     
/* 303 */     if (-1 == selectedIndex) {
/* 304 */       return;
/*     */     }
/* 306 */     rememberEncodingDecision = checkBox.getSelection();
/*     */     
/* 308 */     selected_candidate[0] = candidates[selectedIndex];
/*     */     
/* 310 */     if (rememberEncodingDecision)
/*     */     {
/* 312 */       rememberedDecoder = selected_candidate[0].getDecoder();
/*     */     } else {
/* 314 */       rememberedDecoder = null;
/*     */     }
/*     */     
/* 317 */     s.dispose();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void abandonSelection(Shell s)
/*     */   {
/* 324 */     s.dispose();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/LocaleUtilSWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */