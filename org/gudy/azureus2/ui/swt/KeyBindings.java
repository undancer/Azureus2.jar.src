/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Constants;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class KeyBindings
/*     */ {
/* 117 */   private static final Pattern FUNC_EXP = Pattern.compile("([fF]{1})([1-9]{1}[0-5]{0,1})");
/* 118 */   private static final Pattern SANCTIONED_EXP = Pattern.compile("([ a-zA-Z\\d/\\\\=\\-,\\.`]{1})");
/*     */   
/*     */ 
/* 121 */   private static final String[] SPECIAL_KEYS = { "Meta", "Ctrl", "Cmd", "Alt", "Opt", "Shift", "Ins", "Backspace", "Del", "Esc", "PgUp", "PgDn", "Left", "Up", "Right", "Down", "Home", "End", "Tab" };
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
/* 143 */   private static final int[] SPECIAL_VALUES = { SWT.MOD1, 262144, SWT.MOD1, 65536, 65536, 131072, 16777225, 8, 127, 27, 16777221, 16777222, 16384, 128, 131072, 1024, 16777223, 16777224, 9 };
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
/*     */   private static final String DELIM = "+";
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
/*     */   private static final String DELIM_EXP = "\\+";
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
/*     */   private static String getPlatformKeySuffix()
/*     */   {
/* 179 */     if (Constants.isLinux)
/* 180 */       return ".linux";
/* 181 */     if (Constants.isSolaris)
/* 182 */       return ".solaris";
/* 183 */     if (Constants.isUnix)
/* 184 */       return ".unix";
/* 185 */     if (Constants.isFreeBSD)
/* 186 */       return ".freebsd";
/* 187 */     if (Constants.isOSX)
/* 188 */       return ".mac";
/* 189 */     if (Constants.isWindows) {
/* 190 */       return ".windows";
/*     */     }
/* 192 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static KeyBindingInfo parseKeyBinding(String keyBindingValue)
/*     */   {
/* 202 */     if (keyBindingValue.length() < 1) {
/* 203 */       return new KeyBindingInfo(null, 0, null);
/*     */     }
/*     */     
/* 206 */     int swtAccelerator = 0;
/*     */     
/* 208 */     String[] tmpValues = keyBindingValue.split("\\+");
/* 209 */     boolean[] specVisited = new boolean[SPECIAL_KEYS.length];
/* 210 */     boolean funcVisited = false;
/*     */     
/* 212 */     StringBuilder displayValue = new StringBuilder(keyBindingValue.length() + 2);
/* 213 */     displayValue.append('\t');
/*     */     
/* 215 */     for (int i = 0; i < tmpValues.length; i++)
/*     */     {
/* 217 */       String value = tmpValues[i];
/* 218 */       boolean matched = false;
/*     */       
/*     */ 
/* 221 */       for (int j = 0; j < SPECIAL_KEYS.length; j++)
/*     */       {
/* 223 */         if ((specVisited[j] == 0) && (SPECIAL_KEYS[j].equalsIgnoreCase(value)))
/*     */         {
/* 225 */           swtAccelerator |= SPECIAL_VALUES[j];
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 231 */           if (SPECIAL_KEYS[j].equalsIgnoreCase("Meta")) {
/* 232 */             displayValue.append(Constants.isOSX ? "Cmd" : "Ctrl").append("+");
/*     */           } else {
/* 234 */             displayValue.append(SPECIAL_KEYS[j]).append("+");
/*     */           }
/*     */           
/* 237 */           specVisited[j] = true;
/* 238 */           matched = true;
/* 239 */           break;
/*     */         }
/*     */       }
/*     */       
/* 243 */       if (!matched)
/*     */       {
/*     */ 
/*     */ 
/* 247 */         if (!funcVisited)
/*     */         {
/* 249 */           Matcher funcMatcher = FUNC_EXP.matcher(value);
/* 250 */           if ((funcMatcher.find()) && (funcMatcher.start() == 0) && (funcMatcher.end() == value.length()))
/*     */           {
/* 252 */             int funcVal = Integer.parseInt(funcMatcher.group(2));
/*     */             
/*     */ 
/* 255 */             swtAccelerator |= 16777216 + (9 + funcVal);
/* 256 */             displayValue.append(funcMatcher.group(0)).append("+");
/*     */             
/* 258 */             funcVisited = true;
/* 259 */             matched = true;
/*     */           }
/*     */         }
/*     */         
/* 263 */         if (!matched)
/*     */         {
/*     */ 
/* 266 */           Matcher valMatcher = SANCTIONED_EXP.matcher(value);
/* 267 */           if ((valMatcher.find()) && (valMatcher.start() == 0))
/*     */           {
/* 269 */             char c = valMatcher.group().charAt(0);
/*     */             
/*     */ 
/* 272 */             int subStrIndex = displayValue.indexOf(c + "+");
/* 273 */             if ((subStrIndex != 1) && ((subStrIndex <= 1) || (!displayValue.substring(subStrIndex - 1, subStrIndex).equals("+"))))
/*     */             {
/*     */ 
/* 276 */               swtAccelerator |= c;
/* 277 */               displayValue.append(c).append("+");
/*     */             }
/*     */           }
/*     */         } } }
/* 281 */     if ((funcVisited) || (specVisited[0] != 0) || (specVisited[1] != 0) || (specVisited[2] != 0) || (specVisited[3] != 0) || (specVisited[4] != 0)) {
/* 282 */       return new KeyBindingInfo(displayValue.substring(0, displayValue.length() - 1), swtAccelerator, null);
/*     */     }
/* 284 */     return new KeyBindingInfo(null, 0, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void removeAccelerator(MenuItem menu, String localizationKey)
/*     */   {
/* 296 */     setAccelerator(menu, new KeyBindingInfo("", 0, null));
/* 297 */     Messages.setLanguageText(menu, localizationKey);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setAccelerator(MenuItem menu, String localizationKey)
/*     */   {
/* 319 */     localizationKey = localizationKey + ".keybinding";
/* 320 */     String platformSpecificKey = localizationKey + getPlatformKeySuffix();
/*     */     
/*     */ 
/* 323 */     if (MessageText.keyExists(platformSpecificKey))
/*     */     {
/* 325 */       setAccelerator(menu, parseKeyBinding(MessageText.getString(platformSpecificKey)));
/*     */     }
/* 327 */     else if (MessageText.keyExists(localizationKey))
/*     */     {
/* 329 */       setAccelerator(menu, parseKeyBinding(MessageText.getString(localizationKey)));
/*     */     }
/* 331 */     else if (!MessageText.isCurrentLocale(MessageText.LOCALE_DEFAULT))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 336 */       if (MessageText.keyExistsForDefaultLocale(platformSpecificKey))
/*     */       {
/* 338 */         setAccelerator(menu, parseKeyBinding(MessageText.getDefaultLocaleString(platformSpecificKey)));
/*     */       }
/* 340 */       else if (MessageText.keyExistsForDefaultLocale(localizationKey))
/*     */       {
/*     */ 
/* 343 */         setAccelerator(menu, parseKeyBinding(MessageText.getDefaultLocaleString(localizationKey)));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static KeyBindingInfo getKeyBindingInfo(String localizationKey)
/*     */   {
/* 350 */     localizationKey = localizationKey + ".keybinding";
/* 351 */     String platformSpecificKey = localizationKey + getPlatformKeySuffix();
/*     */     
/*     */ 
/* 354 */     if (MessageText.keyExists(platformSpecificKey))
/*     */     {
/* 356 */       return parseKeyBinding(MessageText.getString(platformSpecificKey));
/*     */     }
/* 358 */     if (MessageText.keyExists(localizationKey))
/*     */     {
/* 360 */       return parseKeyBinding(MessageText.getString(localizationKey));
/*     */     }
/* 362 */     if (!MessageText.isCurrentLocale(MessageText.LOCALE_DEFAULT))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 367 */       if (MessageText.keyExistsForDefaultLocale(platformSpecificKey))
/*     */       {
/* 369 */         return parseKeyBinding(MessageText.getDefaultLocaleString(platformSpecificKey));
/*     */       }
/* 371 */       if (MessageText.keyExistsForDefaultLocale(localizationKey))
/*     */       {
/*     */ 
/* 374 */         return parseKeyBinding(MessageText.getDefaultLocaleString(localizationKey));
/*     */       }
/*     */     }
/*     */     
/* 378 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void setAccelerator(MenuItem menu, KeyBindingInfo kbInfo)
/*     */   {
/* 388 */     if (menu.isDisposed()) {
/* 389 */       return;
/*     */     }
/* 391 */     if (kbInfo.accelerator != 0)
/*     */     {
/* 393 */       menu.setAccelerator(kbInfo.accelerator);
/*     */       
/*     */ 
/* 396 */       if ((!Constants.isOSX) && (!menu.getText().endsWith(kbInfo.name))) {
/* 397 */         menu.setText(menu.getText() + kbInfo.name);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 407 */     System.out.println(parseKeyBinding("Ctrl+1").name);
/* 408 */     System.out.println(parseKeyBinding("Ctrl+F12").name);
/* 409 */     System.out.println(parseKeyBinding("Ctrl+F4").name);
/*     */     
/* 411 */     System.out.println("Meta+Shift+O");
/* 412 */     System.out.println(parseKeyBinding("Ctrl+Shift+O").accelerator);
/* 413 */     System.out.println(parseKeyBinding("Shift+Ctrl+O").accelerator);
/* 414 */     System.out.println(SWT.MOD1 | 0x20000 | 0x4F);
/*     */     
/* 416 */     System.out.println("Meta+Shift+o");
/* 417 */     System.out.println(SWT.MOD1 | 0x20000 | 0x6F);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static class KeyBindingInfo
/*     */   {
/*     */     public final String name;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public final int accelerator;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private KeyBindingInfo(String name, int accelerator)
/*     */     {
/* 446 */       this.name = name;
/* 447 */       this.accelerator = accelerator;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/KeyBindings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */