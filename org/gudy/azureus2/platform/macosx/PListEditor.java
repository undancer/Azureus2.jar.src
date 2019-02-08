/*     */ package org.gudy.azureus2.platform.macosx;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
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
/*     */ public class PListEditor
/*     */ {
/*     */   private String plistFile;
/*     */   private boolean found_bom;
/*     */   
/*     */   public PListEditor(String plistFile)
/*     */     throws IOException
/*     */   {
/*  45 */     this.plistFile = plistFile;
/*     */     
/*  47 */     File file = new File(plistFile);
/*     */     
/*  49 */     if (!file.exists())
/*     */     {
/*  51 */       throw new IOException("plist file '" + file + "' doesn't exist");
/*     */     }
/*     */     
/*  54 */     if (!file.canWrite())
/*     */     {
/*  56 */       throw new IOException("plist file '" + file + "' is read only");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setFileTypeExtensions(String[] extensions)
/*     */     throws IOException
/*     */   {
/*  66 */     StringBuilder value = new StringBuilder();
/*  67 */     StringBuilder find = new StringBuilder();
/*  68 */     find.append("(?s).*?<key>CFBundleDocumentTypes</key>\\s*<array>.*?<key>CFBundleTypeExtensions</key>\\s*<array>");
/*  69 */     for (int i = 0; i < extensions.length; i++) {
/*  70 */       value.append("\n\t\t\t\t<string>");
/*  71 */       value.append(extensions[i]);
/*  72 */       value.append("</string>");
/*     */       
/*  74 */       find.append(".*?");
/*  75 */       find.append(extensions[i]);
/*     */     }
/*  77 */     value.append("\n\t\t\t");
/*     */     
/*  79 */     find.append(".*?</array>.*");
/*  80 */     String match = "(?s)(<key>CFBundleDocumentTypes</key>\\s*<array>.*?<key>CFBundleTypeExtensions</key>\\s*<array>)(.*?)(</array>)";
/*     */     
/*  82 */     setValue(find.toString(), match, value.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setSimpleStringValue(String key, String value)
/*     */     throws IOException
/*     */   {
/*  92 */     String find = "(?s).*?<key>" + key + "</key>\\s*" + "<string>" + value + "</string>.*";
/*  93 */     String match = "(?s)(<key>" + key + "</key>\\s*" + "<string>)(.*?)(</string>)";
/*  94 */     setValue(find, match, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setArrayValues(String key, String valueType, String[] values)
/*     */     throws IOException
/*     */   {
/* 105 */     StringBuilder value = new StringBuilder();
/* 106 */     StringBuilder find = new StringBuilder();
/* 107 */     find.append("(?s).*?<key>").append(key).append("</key>\\s*").append("<array>");
/* 108 */     for (int i = 0; i < values.length; i++) {
/* 109 */       find.append("\\s*<").append(valueType).append(">").append(values[i]).append("</").append(valueType).append(">");
/* 110 */       value.append("\n\t\t\t\t<").append(valueType).append(">");
/* 111 */       value.append(values[i]);
/* 112 */       value.append("</").append(valueType).append(">");
/*     */     }
/* 114 */     find.append("\\s*</array>.*");
/* 115 */     value.append("\n\t\t\t");
/*     */     
/* 117 */     String match = "(?s)(<key>" + key + "</key>\\s*<array>)(.*?)(</array>)";
/*     */     
/* 119 */     setValue(find.toString(), match, value.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean isValuePresent(String fileContent, String match)
/*     */     throws IOException
/*     */   {
/* 130 */     return fileContent.matches(match);
/*     */   }
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
/*     */   private void setValue(String find, String match, String value)
/*     */     throws IOException
/*     */   {
/* 148 */     String fileContent = getFileContent();
/*     */     
/* 150 */     if (!isValuePresent(fileContent, find))
/*     */     {
/* 152 */       fileContent = fileContent.replaceFirst(match, "$1" + value + "$3");
/* 153 */       setFileContent(fileContent);
/* 154 */       touchFile();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private String getFileContent()
/*     */     throws IOException
/*     */   {
/* 162 */     InputStreamReader reader = null;
/*     */     try
/*     */     {
/* 165 */       byte[] file_bytes = FileUtil.readFileAsByteArray(new File(this.plistFile));
/*     */       
/*     */ 
/*     */ 
/* 169 */       if ((file_bytes.length > 3) && (file_bytes[0] == -17) && (file_bytes[1] == -69) && (file_bytes[2] == -65))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 174 */         this.found_bom = true;
/*     */         
/* 176 */         reader = new InputStreamReader(new ByteArrayInputStream(file_bytes, 3, file_bytes.length - 3));
/*     */       }
/*     */       else
/*     */       {
/* 180 */         this.found_bom = false;
/*     */         
/* 182 */         reader = new InputStreamReader(new ByteArrayInputStream(file_bytes));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 188 */       int length = 32768;
/*     */       
/* 190 */       char[] buffer = new char[length];
/*     */       
/* 192 */       int offset = 0;
/*     */       
/* 194 */       int len = 0;
/*     */       
/* 196 */       while ((len = reader.read(buffer, offset, length - offset)) > 0) {
/* 197 */         offset += len;
/*     */       }
/*     */       
/* 200 */       String result = new String(buffer, 0, offset);
/*     */       
/* 202 */       return result;
/*     */     }
/*     */     finally {
/* 205 */       if (reader != null) {
/* 206 */         reader.close();
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
/*     */   private void setFileContent(String fileContent)
/*     */     throws IOException
/*     */   {
/* 220 */     File file = new File(this.plistFile);
/*     */     
/* 222 */     File backup_file = new File(this.plistFile + ".bak");
/*     */     
/* 224 */     if (file.exists())
/*     */     {
/* 226 */       if (!FileUtil.copyFile(file, backup_file))
/*     */       {
/* 228 */         throw new IOException("Failed to backup plist file prior to modification");
/*     */       }
/*     */     }
/*     */     
/* 232 */     boolean ok = false;
/*     */     
/*     */     try
/*     */     {
/* 236 */       ByteArrayOutputStream baos = new ByteArrayOutputStream(fileContent.length() + 256);
/*     */       
/* 238 */       if (this.found_bom)
/*     */       {
/*     */ 
/*     */ 
/* 242 */         baos.write(new byte[] { -17, -69, -65 });
/*     */       }
/*     */       
/* 245 */       OutputStreamWriter osw = new OutputStreamWriter(baos);
/*     */       
/* 247 */       osw.write(fileContent);
/*     */       
/* 249 */       osw.close();
/*     */       
/* 251 */       FileOutputStream out = null;
/*     */       
/*     */       try
/*     */       {
/* 255 */         out = new FileOutputStream(this.plistFile);
/*     */         
/* 257 */         out.write(baos.toByteArray());
/*     */       }
/*     */       finally
/*     */       {
/* 261 */         if (out != null)
/*     */         {
/* 263 */           out.close();
/*     */           
/* 265 */           ok = true;
/*     */         }
/*     */       }
/*     */     } finally { File bork_file;
/* 269 */       if (ok)
/*     */       {
/* 271 */         backup_file.delete();
/*     */ 
/*     */ 
/*     */       }
/* 275 */       else if (backup_file.exists())
/*     */       {
/* 277 */         File bork_file = new File(this.plistFile + ".bad");
/*     */         
/* 279 */         file.renameTo(bork_file);
/*     */         
/* 281 */         file.delete();
/*     */         
/* 283 */         backup_file.renameTo(file);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void touchFile()
/*     */   {
/* 292 */     File file = new File(this.plistFile);
/* 293 */     for (int i = 0; i <= 2; i++) {
/* 294 */       if (file != null) {
/* 295 */         String[] command = { "touch", file.getAbsolutePath() };
/*     */         try
/*     */         {
/* 298 */           Runtime.getRuntime().exec(command);
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 302 */           e.printStackTrace();
/*     */         }
/*     */         
/* 305 */         file = file.getParentFile();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/*     */     try {
/* 312 */       PListEditor editor = new PListEditor("/Applications/Vuze.app/Contents/Info.plist");
/* 313 */       editor.setFileTypeExtensions(new String[] { "torrent", "tor", "vuze", "vuz" });
/* 314 */       editor.setSimpleStringValue("CFBundleName", "Vuze");
/* 315 */       editor.setSimpleStringValue("CFBundleTypeName", "Vuze Download");
/* 316 */       editor.setSimpleStringValue("CFBundleGetInfoString", "Vuze");
/* 317 */       editor.setArrayValues("CFBundleURLSchemes", "string", new String[] { "magnet", "dht" });
/*     */     }
/*     */     catch (Throwable e) {
/* 320 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/macosx/PListEditor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */