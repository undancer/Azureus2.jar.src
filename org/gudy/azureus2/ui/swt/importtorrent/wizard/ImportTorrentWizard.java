/*     */ package org.gudy.azureus2.ui.swt.importtorrent.wizard;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import org.eclipse.swt.widgets.MessageBox;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ 
/*     */ public class ImportTorrentWizard
/*     */   extends Wizard
/*     */ {
/*  49 */   String torrent_file = "";
/*  50 */   String import_file = "";
/*     */   
/*     */ 
/*     */   public ImportTorrentWizard()
/*     */   {
/*  55 */     super("importTorrentWizard.title");
/*     */     
/*  57 */     ImportTorrentWizardInputPanel input_panel = new ImportTorrentWizardInputPanel(this, null);
/*     */     
/*  59 */     setFirstPanel(input_panel);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void onClose()
/*     */   {
/*  66 */     super.onClose();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setTorrentFile(String str)
/*     */   {
/*  73 */     this.torrent_file = str;
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getTorrentFile()
/*     */   {
/*  79 */     return this.torrent_file;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setImportFile(String str)
/*     */   {
/*  86 */     this.import_file = str;
/*     */     
/*  88 */     this.torrent_file = (str + ".torrent");
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getImportFile()
/*     */   {
/*  94 */     return this.import_file;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean performImport()
/*     */   {
/*     */     File input_file;
/*     */     try
/*     */     {
/* 103 */       input_file = new File(getImportFile()).getCanonicalFile();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 107 */       MessageBox mb = new MessageBox(getWizardWindow(), 33);
/*     */       
/* 109 */       mb.setText(MessageText.getString("importTorrentWizard.process.inputfilebad.title"));
/*     */       
/* 111 */       mb.setMessage(MessageText.getString("importTorrentWizard.process.inputfilebad.message") + "\n" + e.toString());
/*     */       
/*     */ 
/* 114 */       mb.open();
/*     */       
/* 116 */       return false;
/*     */     }
/*     */     
/* 119 */     File output_file = new File(getTorrentFile());
/*     */     
/* 121 */     if (output_file.exists())
/*     */     {
/* 123 */       MessageBox mb = new MessageBox(getWizardWindow(), 196);
/*     */       
/* 125 */       mb.setText(MessageText.getString("importTorrentWizard.process.outputfileexists.title"));
/*     */       
/* 127 */       mb.setMessage(MessageText.getString("importTorrentWizard.process.outputfileexists.message"));
/*     */       
/* 129 */       int result = mb.open();
/*     */       
/* 131 */       if (result == 128)
/*     */       {
/* 133 */         return false;
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
/* 146 */         TOTorrent torrent = TOTorrentFactory.deserialiseFromXMLFile(input_file);
/*     */         
/*     */         try
/*     */         {
/* 150 */           torrent.serialiseToBEncodedFile(output_file);
/*     */           
/* 152 */           return true;
/*     */ 
/*     */         }
/*     */         catch (TOTorrentException e)
/*     */         {
/*     */ 
/* 158 */           error_title = MessageText.getString("importTorrentWizard.process.torrentfail.title");
/*     */           
/* 160 */           error_detail = TorrentUtils.exceptionToText(e);
/*     */         }
/*     */         
/*     */       }
/*     */       catch (TOTorrentException e)
/*     */       {
/* 166 */         error_title = MessageText.getString("importTorrentWizard.process.importfail.title");
/*     */         
/* 168 */         error_detail = TorrentUtils.exceptionToText(e);
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
/* 180 */       mb = new MessageBox(getWizardWindow(), 33);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 173 */       error_title = MessageText.getString("importTorrentWizard.process.unknownfail.title");
/*     */       
/* 175 */       Debug.printStackTrace(e);
/*     */       
/* 177 */       error_detail = e.toString();
/*     */     }
/*     */     
/*     */     MessageBox mb;
/*     */     
/* 182 */     mb.setText(error_title);
/*     */     
/* 184 */     mb.setMessage(error_detail);
/*     */     
/* 186 */     mb.open();
/*     */     
/* 188 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/importtorrent/wizard/ImportTorrentWizard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */