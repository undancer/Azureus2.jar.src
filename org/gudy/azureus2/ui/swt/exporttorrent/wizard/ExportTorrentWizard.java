/*     */ package org.gudy.azureus2.ui.swt.exporttorrent.wizard;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.MessageBox;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.ui.swt.wizard.Wizard;
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
/*     */ 
/*     */ 
/*     */ public class ExportTorrentWizard
/*     */   extends Wizard
/*     */ {
/*  49 */   String torrent_file = "";
/*  50 */   String export_file = "";
/*     */   
/*     */ 
/*     */   public ExportTorrentWizard()
/*     */   {
/*  55 */     super("exportTorrentWizard.title");
/*     */     
/*  57 */     ExportTorrentWizardInputPanel input_panel = new ExportTorrentWizardInputPanel(this, null);
/*     */     
/*  59 */     setFirstPanel(input_panel);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExportTorrentWizard(Display display, DownloadManager dm)
/*     */   {
/*  67 */     super("exportTorrentWizard.title");
/*     */     
/*  69 */     setTorrentFile(dm.getTorrentFileName());
/*     */     
/*  71 */     ExportTorrentWizardOutputPanel output_panel = new ExportTorrentWizardOutputPanel(this, null);
/*     */     
/*  73 */     setFirstPanel(output_panel);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void onClose()
/*     */   {
/*  80 */     super.onClose();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setTorrentFile(String str)
/*     */   {
/*  87 */     this.torrent_file = str;
/*     */     
/*  89 */     this.export_file = (str + ".xml");
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getTorrentFile()
/*     */   {
/*  95 */     return this.torrent_file;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setExportFile(String str)
/*     */   {
/* 102 */     this.export_file = str;
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getExportFile()
/*     */   {
/* 108 */     return this.export_file;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean performExport()
/*     */   {
/*     */     File input_file;
/*     */     try
/*     */     {
/* 117 */       input_file = new File(getTorrentFile()).getCanonicalFile();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 121 */       MessageBox mb = new MessageBox(getWizardWindow(), 33);
/*     */       
/* 123 */       mb.setText(MessageText.getString("exportTorrentWizard.process.inputfilebad.title"));
/*     */       
/* 125 */       mb.setMessage(MessageText.getString("exportTorrentWizard.process.inputfilebad.message") + "\n" + e.toString());
/*     */       
/*     */ 
/* 128 */       mb.open();
/*     */       
/* 130 */       return false;
/*     */     }
/*     */     
/* 133 */     File output_file = new File(this.export_file);
/*     */     
/* 135 */     if (output_file.exists())
/*     */     {
/* 137 */       MessageBox mb = new MessageBox(getWizardWindow(), 196);
/*     */       
/* 139 */       mb.setText(MessageText.getString("exportTorrentWizard.process.outputfileexists.title"));
/*     */       
/* 141 */       mb.setMessage(MessageText.getString("exportTorrentWizard.process.outputfileexists.message"));
/*     */       
/* 143 */       int result = mb.open();
/*     */       
/* 145 */       if (result == 128)
/*     */       {
/* 147 */         return false;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     String error_title;
/*     */     
/*     */     String error_detail;
/*     */     
/*     */     try
/*     */     {
/*     */       try
/*     */       {
/* 160 */         TOTorrent torrent = TOTorrentFactory.deserialiseFromBEncodedFile(input_file);
/*     */         
/*     */         try
/*     */         {
/* 164 */           torrent.serialiseToXMLFile(output_file);
/*     */           
/* 166 */           return true;
/*     */         }
/*     */         catch (TOTorrentException e)
/*     */         {
/* 170 */           error_title = MessageText.getString("exportTorrentWizard.process.exportfail.title");
/*     */           
/* 172 */           error_detail = TorrentUtils.exceptionToText(e);
/*     */         }
/*     */       }
/*     */       catch (TOTorrentException e) {
/* 176 */         error_title = MessageText.getString("exportTorrentWizard.process.torrentfail.title");
/*     */         
/* 178 */         error_detail = TorrentUtils.exceptionToText(e);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 188 */       mb = new MessageBox(getWizardWindow(), 33);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 183 */       error_title = MessageText.getString("exportTorrentWizard.process.unknownfail.title");
/*     */       
/* 185 */       error_detail = e.toString();
/*     */     }
/*     */     
/*     */     MessageBox mb;
/*     */     
/* 190 */     mb.setText(error_title);
/*     */     
/* 192 */     mb.setMessage(error_detail);
/*     */     
/* 194 */     mb.open();
/*     */     
/* 196 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/exporttorrent/wizard/ExportTorrentWizard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */