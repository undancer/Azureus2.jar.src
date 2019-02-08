/*      */ package org.gudy.azureus2.core3.util;
/*      */ 
/*      */ import java.io.PrintStream;
/*      */ import java.text.DecimalFormat;
/*      */ import java.text.DecimalFormatSymbols;
/*      */ import java.text.NumberFormat;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.internat.MessageText.MessageTextListener;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DisplayFormatters
/*      */ {
/*      */   private static final boolean ROUND_NO = true;
/*      */   private static final boolean TRUNCZEROS_NO = false;
/*      */   private static final boolean TRUNCZEROS_YES = true;
/*      */   public static final int UNIT_B = 0;
/*      */   public static final int UNIT_KB = 1;
/*      */   public static final int UNIT_MB = 2;
/*      */   public static final int UNIT_GB = 3;
/*      */   public static final int UNIT_TB = 4;
/*   67 */   private static final int[] UNITS_PRECISION = { 0, 1, 2, 2, 3 };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   74 */   private static final NumberFormat[] cached_number_formats = new NumberFormat[20];
/*      */   
/*      */   private static NumberFormat percentage_format;
/*      */   
/*   78 */   private static final String[] all_units = new String[5];
/*      */   
/*      */   private static String[] units;
/*      */   private static String[] units_bits;
/*      */   private static String[] units_rate;
/*   83 */   private static int unitsStopAt = 4;
/*      */   
/*      */   private static String[] units_base10;
/*      */   
/*      */   private static String per_sec;
/*      */   
/*      */   private static boolean use_si_units;
/*      */   
/*      */   private static boolean force_si_values;
/*      */   private static boolean use_units_rate_bits;
/*      */   private static boolean not_use_GB_TB;
/*   94 */   private static int message_text_state = 0;
/*      */   
/*      */   private static boolean separate_prot_data_stats;
/*      */   
/*      */   private static boolean data_stats_only;
/*      */   private static char decimalSeparator;
/*  100 */   private static volatile Map<String, Formatter> format_map = new HashMap();
/*      */   private static String PeerManager_status_finished;
/*      */   
/*  103 */   static { COConfigurationManager.addAndFireParameterListeners(new String[] { "config.style.useSIUnits", "config.style.forceSIValues", "config.style.useUnitsRateBits", "config.style.doNotUseGB", "config.style.formatOverrides" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String x)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  117 */         DisplayFormatters.access$002(COConfigurationManager.getBooleanParameter("config.style.useSIUnits"));
/*  118 */         DisplayFormatters.access$102(COConfigurationManager.getBooleanParameter("config.style.forceSIValues"));
/*  119 */         DisplayFormatters.access$202(COConfigurationManager.getBooleanParameter("config.style.useUnitsRateBits"));
/*  120 */         DisplayFormatters.access$302(COConfigurationManager.getBooleanParameter("config.style.doNotUseGB"));
/*      */         
/*  122 */         DisplayFormatters.access$402(DisplayFormatters.not_use_GB_TB ? 2 : 4);
/*      */         
/*  124 */         DisplayFormatters.setUnits();
/*      */         
/*  126 */         DisplayFormatters.updateFormatOverrides(COConfigurationManager.getStringParameter("config.style.formatOverrides", ""));
/*      */       }
/*      */       
/*  129 */     });
/*  130 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "config.style.dataStatsOnly", "config.style.separateProtDataStats" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String x)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  141 */         DisplayFormatters.access$602(COConfigurationManager.getBooleanParameter("config.style.separateProtDataStats"));
/*  142 */         DisplayFormatters.access$702(COConfigurationManager.getBooleanParameter("config.style.dataStatsOnly"));
/*      */       }
/*      */       
/*  145 */     });
/*  146 */     MessageText.addAndFireListener(new MessageText.MessageTextListener() {
/*      */       public void localeChanged(Locale old_locale, Locale new_locale) {
/*  148 */         DisplayFormatters.setUnits();
/*  149 */         DisplayFormatters.loadMessages(); } }); }
/*      */   
/*      */   private static String PeerManager_status_finishedin;
/*      */   private static String Formats_units_alot;
/*      */   private static String discarded;
/*      */   private static String ManagerItem_waiting;
/*      */   private static String ManagerItem_initializing;
/*      */   private static String ManagerItem_allocating;
/*      */   private static String ManagerItem_checking;
/*      */   private static String ManagerItem_finishing;
/*      */   private static String ManagerItem_ready;
/*      */   private static String ManagerItem_downloading;
/*  161 */   public static void setUnits() { units = new String[unitsStopAt + 1];
/*  162 */     units_bits = new String[unitsStopAt + 1];
/*  163 */     units_rate = new String[unitsStopAt + 1];
/*      */     
/*  165 */     if (use_si_units) {
/*  166 */       all_units[4] = getUnit("TiB");
/*  167 */       all_units[3] = getUnit("GiB");
/*  168 */       all_units[2] = getUnit("MiB");
/*  169 */       all_units[1] = getUnit("KiB");
/*  170 */       all_units[0] = getUnit("B");
/*      */       
/*      */ 
/*      */ 
/*  174 */       switch (unitsStopAt) {
/*      */       case 4: 
/*  176 */         units[4] = all_units[4];
/*  177 */         units_bits[4] = getUnit("Tibit");
/*  178 */         units_rate[4] = (use_units_rate_bits ? getUnit("Tibit") : getUnit("TiB"));
/*      */       case 3: 
/*  180 */         units[3] = all_units[3];
/*  181 */         units_bits[3] = getUnit("Gibit");
/*  182 */         units_rate[3] = (use_units_rate_bits ? getUnit("Gibit") : getUnit("GiB"));
/*      */       case 2: 
/*  184 */         units[2] = all_units[2];
/*  185 */         units_bits[2] = getUnit("Mibit");
/*  186 */         units_rate[2] = (use_units_rate_bits ? getUnit("Mibit") : getUnit("MiB"));
/*      */       
/*      */       case 1: 
/*  189 */         units[1] = all_units[1];
/*  190 */         units_bits[1] = getUnit("Kibit");
/*      */         
/*      */ 
/*  193 */         units_rate[1] = (use_units_rate_bits ? getUnit("Kibit") : getUnit("KiB"));
/*      */       case 0: 
/*  195 */         units[0] = all_units[0];
/*  196 */         units_bits[0] = getUnit("bit");
/*  197 */         units_rate[0] = (use_units_rate_bits ? getUnit("bit") : getUnit("B"));
/*      */       }
/*      */     } else {
/*  200 */       all_units[4] = getUnit("TB");
/*  201 */       all_units[3] = getUnit("GB");
/*  202 */       all_units[2] = getUnit("MB");
/*  203 */       all_units[1] = getUnit("kB");
/*  204 */       all_units[0] = getUnit("B");
/*      */       
/*  206 */       switch (unitsStopAt) {
/*      */       case 4: 
/*  208 */         units[4] = all_units[4];
/*  209 */         units_bits[4] = getUnit("Tbit");
/*  210 */         units_rate[4] = (use_units_rate_bits ? getUnit("Tbit") : getUnit("TB"));
/*      */       case 3: 
/*  212 */         units[3] = all_units[3];
/*  213 */         units_bits[3] = getUnit("Gbit");
/*  214 */         units_rate[3] = (use_units_rate_bits ? getUnit("Gbit") : getUnit("GB"));
/*      */       case 2: 
/*  216 */         units[2] = all_units[2];
/*  217 */         units_bits[2] = getUnit("Mbit");
/*  218 */         units_rate[2] = (use_units_rate_bits ? getUnit("Mbit") : getUnit("MB"));
/*      */       
/*      */       case 1: 
/*  221 */         units[1] = all_units[1];
/*  222 */         units_bits[1] = getUnit("kbit");
/*  223 */         units_rate[1] = (use_units_rate_bits ? getUnit("kbit") : getUnit("kB"));
/*      */       case 0: 
/*  225 */         units[0] = all_units[0];
/*  226 */         units_bits[0] = getUnit("bit");
/*  227 */         units_rate[0] = (use_units_rate_bits ? getUnit("bit") : getUnit("B"));
/*      */       }
/*      */       
/*      */     }
/*      */     
/*  232 */     per_sec = getResourceString("Formats.units.persec", "/s");
/*      */     
/*  234 */     units_base10 = new String[] { getUnit(use_units_rate_bits ? "bit" : "B"), getUnit(use_units_rate_bits ? "kbit" : "KB"), getUnit(use_units_rate_bits ? "Mbit" : "MB"), getUnit(use_units_rate_bits ? "Gbit" : "GB"), getUnit(use_units_rate_bits ? "Tbit" : "TB") };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  242 */     for (int i = 0; i <= unitsStopAt; i++) {
/*  243 */       units[i] = units[i];
/*  244 */       units_rate[i] = (units_rate[i] + per_sec);
/*      */     }
/*      */     
/*  247 */     Arrays.fill(cached_number_formats, null);
/*      */     
/*  249 */     percentage_format = NumberFormat.getPercentInstance();
/*  250 */     percentage_format.setMinimumFractionDigits(1);
/*  251 */     percentage_format.setMaximumFractionDigits(1);
/*      */     
/*  253 */     decimalSeparator = new DecimalFormatSymbols().getDecimalSeparator();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static String getUnit(String key)
/*      */   {
/*  260 */     String res = " " + getResourceString(new StringBuilder().append("Formats.units.").append(key).toString(), key);
/*      */     
/*  262 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static String ManagerItem_swarmMerge;
/*      */   
/*      */   private static String ManagerItem_seeding;
/*      */   
/*      */   private static String ManagerItem_superseeding;
/*      */   
/*      */   private static String ManagerItem_stopping;
/*      */   
/*      */   private static String ManagerItem_stopped;
/*      */   
/*      */   private static String ManagerItem_paused;
/*      */   
/*      */   private static String ManagerItem_queued;
/*      */   
/*      */   private static String ManagerItem_error;
/*      */   
/*      */   private static String ManagerItem_forced;
/*      */   
/*      */   private static String ManagerItem_moving;
/*      */   
/*      */   private static String yes;
/*      */   
/*      */   private static String no;
/*      */   
/*      */   public static void loadMessages()
/*      */   {
/*  293 */     PeerManager_status_finished = getResourceString("PeerManager.status.finished", "Finished");
/*  294 */     PeerManager_status_finishedin = getResourceString("PeerManager.status.finishedin", "Finished in");
/*  295 */     Formats_units_alot = getResourceString("Formats.units.alot", "A lot");
/*  296 */     discarded = getResourceString("discarded", "discarded");
/*  297 */     ManagerItem_waiting = getResourceString("ManagerItem.waiting", "waiting");
/*  298 */     ManagerItem_initializing = getResourceString("ManagerItem.initializing", "initializing");
/*  299 */     ManagerItem_allocating = getResourceString("ManagerItem.allocating", "allocating");
/*  300 */     ManagerItem_checking = getResourceString("ManagerItem.checking", "checking");
/*  301 */     ManagerItem_finishing = getResourceString("ManagerItem.finishing", "finishing");
/*  302 */     ManagerItem_ready = getResourceString("ManagerItem.ready", "ready");
/*  303 */     ManagerItem_downloading = getResourceString("ManagerItem.downloading", "downloading");
/*  304 */     ManagerItem_swarmMerge = getResourceString("TableColumn.header.mergeddata", "swarm merge");
/*  305 */     ManagerItem_seeding = getResourceString("ManagerItem.seeding", "seeding");
/*  306 */     ManagerItem_superseeding = getResourceString("ManagerItem.superseeding", "superseeding");
/*  307 */     ManagerItem_stopping = getResourceString("ManagerItem.stopping", "stopping");
/*  308 */     ManagerItem_stopped = getResourceString("ManagerItem.stopped", "stopped");
/*  309 */     ManagerItem_paused = getResourceString("ManagerItem.paused", "paused");
/*  310 */     ManagerItem_queued = getResourceString("ManagerItem.queued", "queued");
/*  311 */     ManagerItem_error = getResourceString("ManagerItem.error", "error");
/*  312 */     ManagerItem_forced = getResourceString("ManagerItem.forced", "forced");
/*  313 */     ManagerItem_moving = getResourceString("ManagerItem.moving", "moving");
/*  314 */     yes = getResourceString("GeneralView.yes", "Yes");
/*  315 */     no = getResourceString("GeneralView.no", "No");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String getResourceString(String key, String def)
/*      */   {
/*  323 */     if (message_text_state == 0)
/*      */     {
/*      */ 
/*      */       try
/*      */       {
/*      */ 
/*  329 */         MessageText.class.getName();
/*      */         
/*  331 */         message_text_state = 1;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  335 */         message_text_state = 2;
/*      */       }
/*      */     }
/*      */     
/*  339 */     if (message_text_state == 1)
/*      */     {
/*  341 */       return MessageText.getString(key);
/*      */     }
/*      */     
/*      */ 
/*  345 */     return def;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getYesNo(boolean b)
/*      */   {
/*  353 */     return b ? yes : no;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String getRateUnit(int unit_size)
/*      */   {
/*  360 */     return units_rate[unit_size].substring(1, units_rate[unit_size].length());
/*      */   }
/*      */   
/*      */ 
/*      */   public static String getUnit(int unit_size)
/*      */   {
/*  366 */     return units[unit_size].substring(1, units[unit_size].length());
/*      */   }
/*      */   
/*      */   public static String getRateUnitBase10(int unit_size)
/*      */   {
/*  371 */     return units_base10[unit_size] + per_sec;
/*      */   }
/*      */   
/*      */   public static String getUnitBase10(int unit_size)
/*      */   {
/*  376 */     return units_base10[unit_size];
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean isRateUsingBits()
/*      */   {
/*  382 */     return use_units_rate_bits;
/*      */   }
/*      */   
/*      */ 
/*      */   public static String formatByteCountToKiBEtc(int n)
/*      */   {
/*  388 */     return formatByteCountToKiBEtc(n);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String formatByteCountToKiBEtc(long n)
/*      */   {
/*  395 */     return formatByteCountToKiBEtc(n, false, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String formatByteCountToKiBEtc(long n, boolean bTruncateZeros)
/*      */   {
/*  402 */     return formatByteCountToKiBEtc(n, false, bTruncateZeros);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String formatByteCountToKiBEtc(long n, boolean rate, boolean bTruncateZeros)
/*      */   {
/*  411 */     return formatByteCountToKiBEtc(n, rate, bTruncateZeros, -1);
/*      */   }
/*      */   
/*      */ 
/*      */   public static int getKinB()
/*      */   {
/*  417 */     return use_si_units ? 1024 : force_si_values ? 1024 : 1000;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String formatByteCountToKiBEtc(long n, boolean rate, boolean bTruncateZeros, int precision)
/*      */   {
/*  427 */     double dbl = (rate) && (use_units_rate_bits) ? n * 8L : n;
/*      */     
/*  429 */     int unitIndex = 0;
/*      */     
/*  431 */     long div = force_si_values ? 1024L : use_si_units ? 'Ѐ' : 'Ϩ';
/*      */     
/*  433 */     while ((dbl >= div) && (unitIndex < unitsStopAt))
/*      */     {
/*  435 */       dbl /= div;
/*  436 */       unitIndex++;
/*      */     }
/*      */     
/*  439 */     if (precision < 0) {
/*  440 */       precision = UNITS_PRECISION[unitIndex];
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  451 */     return formatDecimal(dbl, precision, bTruncateZeros, rate) + (rate ? units_rate[unitIndex] : units[unitIndex]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String formatByteCountToKiBEtc(long n, boolean rate, boolean bTruncateZeros, int precision, int minUnit)
/*      */   {
/*  463 */     double dbl = (rate) && (use_units_rate_bits) ? n * 8L : n;
/*      */     
/*  465 */     int unitIndex = 0;
/*      */     
/*  467 */     long div = force_si_values ? 1024L : use_si_units ? 'Ѐ' : 'Ϩ';
/*      */     
/*  469 */     while ((dbl >= div) && (unitIndex < unitsStopAt))
/*      */     {
/*  471 */       dbl /= div;
/*  472 */       unitIndex++;
/*      */     }
/*      */     
/*  475 */     while (unitIndex < minUnit) {
/*  476 */       dbl /= div;
/*  477 */       unitIndex++;
/*      */     }
/*  479 */     if (precision < 0) {
/*  480 */       precision = UNITS_PRECISION[unitIndex];
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  491 */     return formatDecimal(dbl, precision, bTruncateZeros, rate) + (rate ? units_rate[unitIndex] : units[unitIndex]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean isDataProtSeparate()
/*      */   {
/*  498 */     return separate_prot_data_stats;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String formatDataProtByteCountToKiBEtc(long data, long prot)
/*      */   {
/*  506 */     if (separate_prot_data_stats) {
/*  507 */       if ((data == 0L) && (prot == 0L))
/*  508 */         return formatByteCountToKiBEtc(0);
/*  509 */       if (data == 0L)
/*  510 */         return "(" + formatByteCountToKiBEtc(prot) + ")";
/*  511 */       if (prot == 0L) {
/*  512 */         return formatByteCountToKiBEtc(data);
/*      */       }
/*  514 */       return formatByteCountToKiBEtc(data) + " (" + formatByteCountToKiBEtc(prot) + ")";
/*      */     }
/*  516 */     if (data_stats_only) {
/*  517 */       return formatByteCountToKiBEtc(data);
/*      */     }
/*  519 */     return formatByteCountToKiBEtc(prot + data);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String formatDataProtByteCountToKiBEtcPerSec(long data, long prot)
/*      */   {
/*  528 */     if (separate_prot_data_stats) {
/*  529 */       if ((data == 0L) && (prot == 0L))
/*  530 */         return formatByteCountToKiBEtcPerSec(0L);
/*  531 */       if (data == 0L)
/*  532 */         return "(" + formatByteCountToKiBEtcPerSec(prot) + ")";
/*  533 */       if (prot == 0L) {
/*  534 */         return formatByteCountToKiBEtcPerSec(data);
/*      */       }
/*  536 */       return formatByteCountToKiBEtcPerSec(data) + " (" + formatByteCountToKiBEtcPerSec(prot) + ")";
/*      */     }
/*  538 */     if (data_stats_only) {
/*  539 */       return formatByteCountToKiBEtcPerSec(data);
/*      */     }
/*  541 */     return formatByteCountToKiBEtcPerSec(prot + data);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String formatByteCountToKiBEtcPerSec(long n)
/*      */   {
/*  549 */     return formatByteCountToKiBEtc(n, true, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String formatByteCountToKiBEtcPerSec(long n, boolean bTruncateZeros)
/*      */   {
/*  558 */     return formatByteCountToKiBEtc(n, true, bTruncateZeros);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String formatByteCountToBase10KBEtc(long n)
/*      */   {
/*  567 */     if (use_units_rate_bits) {
/*  568 */       n *= 8L;
/*      */     }
/*      */     
/*  571 */     if (n < 1000L)
/*      */     {
/*  573 */       return n + units_base10[0];
/*      */     }
/*  575 */     if (n < 1000000L)
/*      */     {
/*  577 */       return n / 1000L + "." + n % 1000L / 100L + units_base10[1];
/*      */     }
/*      */     
/*      */ 
/*  581 */     if ((n < 1000000000L) || (not_use_GB_TB))
/*      */     {
/*  583 */       return n / 1000000L + "." + n % 1000000L / 100000L + units_base10[2];
/*      */     }
/*      */     
/*      */ 
/*  587 */     if (n < 1000000000000L)
/*      */     {
/*  589 */       return n / 1000000000L + "." + n % 1000000000L / 100000000L + units_base10[3];
/*      */     }
/*      */     
/*      */ 
/*  593 */     if (n < 1000000000000000L)
/*      */     {
/*  595 */       return n / 1000000000000L + "." + n % 1000000000000L / 100000000000L + units_base10[4];
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  600 */     return Formats_units_alot;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String formatByteCountToBase10KBEtcPerSec(long n)
/*      */   {
/*  608 */     return formatByteCountToBase10KBEtc(n) + per_sec;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String formatByteCountToBitsPerSec(long n)
/*      */   {
/*  621 */     double dbl = n * 8L;
/*      */     
/*  623 */     int unitIndex = 0;
/*      */     
/*  625 */     long div = 1000L;
/*      */     
/*  627 */     while ((dbl >= div) && (unitIndex < unitsStopAt))
/*      */     {
/*  629 */       dbl /= div;
/*  630 */       unitIndex++;
/*      */     }
/*      */     
/*  633 */     int precision = UNITS_PRECISION[unitIndex];
/*      */     
/*  635 */     return formatDecimal(dbl, precision, true, true) + units_bits[unitIndex] + per_sec;
/*      */   }
/*      */   
/*      */ 
/*      */   public static String formatETA(long eta)
/*      */   {
/*  641 */     return formatETA(eta, false);
/*      */   }
/*      */   
/*  644 */   private static final SimpleDateFormat abs_df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
/*      */   
/*      */ 
/*      */   public static String formatETA(long eta, boolean abs)
/*      */   {
/*  649 */     if (eta == 0L) return PeerManager_status_finished;
/*  650 */     if (eta == -1L) return "";
/*  651 */     if (eta > 0L) {
/*  652 */       if ((abs) && (eta != 31536000L) && (eta < 1827387392L))
/*      */       {
/*  654 */         long now = SystemTime.getCurrentTime();
/*  655 */         long then = now + eta * 1000L;
/*      */         
/*  657 */         if (eta > 300L)
/*      */         {
/*  659 */           then = then / 60000L * 60000L;
/*      */         }
/*      */         
/*      */         String str1;
/*      */         
/*      */         String str2;
/*  665 */         synchronized (abs_df) {
/*  666 */           str1 = abs_df.format(new Date(now));
/*  667 */           str2 = abs_df.format(new Date(then));
/*      */         }
/*      */         
/*  670 */         int len = Math.min(str1.length(), str2.length()) - 2;
/*      */         
/*  672 */         int diff_at = len;
/*      */         
/*  674 */         for (int i = 0; i < len; i++)
/*      */         {
/*  676 */           char c1 = str1.charAt(i);
/*      */           
/*  678 */           if (c1 != str2.charAt(i))
/*      */           {
/*  680 */             diff_at = i;
/*      */             
/*  682 */             break;
/*      */           }
/*      */         }
/*      */         
/*      */         String res;
/*      */         String res;
/*  688 */         if (diff_at >= 11)
/*      */         {
/*  690 */           res = str2.substring(11);
/*      */         } else { String res;
/*  692 */           if (diff_at >= 5)
/*      */           {
/*  694 */             res = str2.substring(5);
/*      */           }
/*      */           else
/*      */           {
/*  698 */             res = str2;
/*      */           }
/*      */         }
/*  701 */         return res;
/*      */       }
/*      */       
/*  704 */       return TimeFormatter.format(eta);
/*      */     }
/*      */     
/*      */ 
/*  708 */     return PeerManager_status_finishedin + " " + TimeFormatter.format(eta * -1L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String formatDownloaded(DownloadManagerStats stats)
/*      */   {
/*  716 */     long total_discarded = stats.getDiscarded();
/*  717 */     long total_received = stats.getTotalGoodDataBytesReceived();
/*      */     
/*  719 */     if (total_discarded == 0L)
/*      */     {
/*  721 */       return formatByteCountToKiBEtc(total_received);
/*      */     }
/*      */     
/*      */ 
/*  725 */     return formatByteCountToKiBEtc(total_received) + " ( " + formatByteCountToKiBEtc(total_discarded) + " " + discarded + " )";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String formatHashFails(DownloadManager download_manager)
/*      */   {
/*  735 */     TOTorrent torrent = download_manager.getTorrent();
/*      */     
/*  737 */     if (torrent != null)
/*      */     {
/*  739 */       long bad = download_manager.getStats().getHashFailBytes();
/*      */       
/*      */ 
/*      */ 
/*  743 */       long count = bad / torrent.getPieceLength();
/*      */       
/*  745 */       String result = count + " ( " + formatByteCountToKiBEtc(bad) + " )";
/*      */       
/*  747 */       return result;
/*      */     }
/*      */     
/*  750 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String formatDownloadStatus(DownloadManager manager)
/*      */   {
/*  757 */     if (manager == null)
/*      */     {
/*  759 */       return ManagerItem_error + ": Download is null";
/*      */     }
/*      */     
/*  762 */     int state = manager.getState();
/*      */     
/*  764 */     String tmp = "";
/*      */     
/*  766 */     switch (state) {
/*      */     case 75: 
/*  768 */       tmp = ManagerItem_queued;
/*  769 */       break;
/*      */     
/*      */     case 50: 
/*  772 */       tmp = ManagerItem_downloading;
/*  773 */       if (manager.isSwarmMerging() != null) {
/*  774 */         tmp = tmp + " + " + ManagerItem_swarmMerge;
/*      */       }
/*      */       
/*      */ 
/*      */       break;
/*      */     case 60: 
/*  780 */       DiskManager diskManager = manager.getDiskManager();
/*      */       
/*  782 */       if (diskManager != null)
/*      */       {
/*  784 */         int mp = diskManager.getMoveProgress();
/*      */         
/*  786 */         if (mp != -1)
/*      */         {
/*  788 */           tmp = ManagerItem_moving + ": " + formatPercentFromThousands(mp);
/*      */         }
/*      */         else {
/*  791 */           int done = diskManager.getCompleteRecheckStatus();
/*      */           
/*  793 */           if (done != -1)
/*      */           {
/*  795 */             tmp = ManagerItem_seeding + " + " + ManagerItem_checking + ": " + formatPercentFromThousands(done);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  800 */       if (tmp == "")
/*      */       {
/*  802 */         if ((manager.getPeerManager() != null) && (manager.getPeerManager().isSuperSeedMode()))
/*      */         {
/*  804 */           tmp = ManagerItem_superseeding;
/*      */         }
/*      */         else
/*      */         {
/*  808 */           tmp = ManagerItem_seeding;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       break;
/*      */     case 70: 
/*  815 */       tmp = manager.isPaused() ? ManagerItem_paused : ManagerItem_stopped;
/*  816 */       break;
/*      */     
/*      */     case 100: 
/*  819 */       tmp = ManagerItem_error + ": " + manager.getErrorDetails();
/*  820 */       break;
/*      */     
/*      */     case 0: 
/*  823 */       tmp = ManagerItem_waiting;
/*  824 */       break;
/*      */     
/*      */     case 5: 
/*  827 */       tmp = ManagerItem_initializing;
/*  828 */       break;
/*      */     
/*      */     case 10: 
/*  831 */       tmp = ManagerItem_initializing;
/*  832 */       break;
/*      */     
/*      */     case 20: 
/*  835 */       tmp = ManagerItem_allocating;
/*  836 */       DiskManager diskManager = manager.getDiskManager();
/*  837 */       if (diskManager != null) {
/*  838 */         tmp = tmp + ": " + formatPercentFromThousands(diskManager.getPercentDone());
/*      */       }
/*      */       
/*      */       break;
/*      */     case 30: 
/*  843 */       tmp = ManagerItem_checking + ": " + formatPercentFromThousands(manager.getStats().getCompleted());
/*      */       
/*  845 */       break;
/*      */     
/*      */     case 55: 
/*  848 */       tmp = ManagerItem_finishing;
/*  849 */       break;
/*      */     
/*      */     case 40: 
/*  852 */       tmp = ManagerItem_ready;
/*  853 */       break;
/*      */     
/*      */     case 65: 
/*  856 */       tmp = ManagerItem_stopping;
/*  857 */       break;
/*      */     
/*      */     default: 
/*  860 */       tmp = String.valueOf(state);
/*      */     }
/*      */     
/*  863 */     if ((manager.isForceStart()) && ((state == 60) || (state == 50)))
/*      */     {
/*      */ 
/*  866 */       tmp = ManagerItem_forced + " " + tmp; }
/*  867 */     return tmp;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String formatDownloadStatusDefaultLocale(DownloadManager manager)
/*      */   {
/*  874 */     int state = manager.getState();
/*      */     
/*  876 */     String tmp = "";
/*      */     
/*  878 */     DiskManager dm = manager.getDiskManager();
/*      */     
/*  880 */     switch (state) {
/*      */     case 0: 
/*  882 */       tmp = MessageText.getDefaultLocaleString("ManagerItem.waiting");
/*  883 */       break;
/*      */     case 5: 
/*  885 */       tmp = MessageText.getDefaultLocaleString("ManagerItem.initializing");
/*  886 */       break;
/*      */     case 10: 
/*  888 */       tmp = MessageText.getDefaultLocaleString("ManagerItem.initializing");
/*  889 */       break;
/*      */     case 20: 
/*  891 */       tmp = MessageText.getDefaultLocaleString("ManagerItem.allocating");
/*  892 */       break;
/*      */     case 30: 
/*  894 */       tmp = MessageText.getDefaultLocaleString("ManagerItem.checking");
/*  895 */       break;
/*      */     case 55: 
/*  897 */       tmp = MessageText.getDefaultLocaleString("ManagerItem.finishing");
/*  898 */       break;
/*      */     case 40: 
/*  900 */       tmp = MessageText.getDefaultLocaleString("ManagerItem.ready");
/*  901 */       break;
/*      */     case 50: 
/*  903 */       tmp = MessageText.getDefaultLocaleString("ManagerItem.downloading");
/*  904 */       if (manager.isSwarmMerging() != null) {
/*  905 */         tmp = tmp + " + " + MessageText.getDefaultLocaleString("TableColumn.header.mergeddata");
/*      */       }
/*      */       break;
/*      */     case 60: 
/*  909 */       if ((dm != null) && (dm.getCompleteRecheckStatus() != -1)) {
/*  910 */         int done = dm.getCompleteRecheckStatus();
/*      */         
/*  912 */         if (done == -1) {
/*  913 */           done = 1000;
/*      */         }
/*      */         
/*  916 */         tmp = MessageText.getDefaultLocaleString("ManagerItem.seeding") + " + " + MessageText.getDefaultLocaleString("ManagerItem.checking") + ": " + formatPercentFromThousands(done);
/*      */ 
/*      */ 
/*      */       }
/*  920 */       else if ((manager.getPeerManager() != null) && (manager.getPeerManager().isSuperSeedMode()))
/*      */       {
/*  922 */         tmp = MessageText.getDefaultLocaleString("ManagerItem.superseeding");
/*      */       }
/*      */       else {
/*  925 */         tmp = MessageText.getDefaultLocaleString("ManagerItem.seeding");
/*      */       }
/*  927 */       break;
/*      */     case 65: 
/*  929 */       tmp = MessageText.getDefaultLocaleString("ManagerItem.stopping");
/*  930 */       break;
/*      */     case 70: 
/*  932 */       tmp = MessageText.getDefaultLocaleString(manager.isPaused() ? "ManagerItem.paused" : "ManagerItem.stopped");
/*  933 */       break;
/*      */     case 75: 
/*  935 */       tmp = MessageText.getDefaultLocaleString("ManagerItem.queued");
/*  936 */       break;
/*      */     case 100: 
/*  938 */       tmp = MessageText.getDefaultLocaleString("ManagerItem.error").concat(": ").concat(manager.getErrorDetails());
/*  939 */       break;
/*      */     default: 
/*  941 */       tmp = String.valueOf(state);
/*      */     }
/*      */     
/*  944 */     return tmp;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String trimDigits(String str, int num_digits)
/*      */   {
/*  952 */     char[] chars = str.toCharArray();
/*  953 */     String res = "";
/*  954 */     int digits = 0;
/*      */     
/*  956 */     for (int i = 0; i < chars.length; i++) {
/*  957 */       char c = chars[i];
/*  958 */       if (Character.isDigit(c)) {
/*  959 */         digits++;
/*  960 */         if (digits <= num_digits) {
/*  961 */           res = res + c;
/*      */         }
/*  963 */       } else if ((c != '.') || (digits < 3))
/*      */       {
/*      */ 
/*  966 */         res = res + c;
/*      */       }
/*      */     }
/*      */     
/*  970 */     return res;
/*      */   }
/*      */   
/*      */   public static String formatPercentFromThousands(int thousands)
/*      */   {
/*  975 */     return percentage_format.format(thousands / 1000.0D);
/*      */   }
/*      */   
/*      */   public static String formatTimeStamp(long time) {
/*  979 */     StringBuilder sb = new StringBuilder();
/*  980 */     Calendar calendar = Calendar.getInstance();
/*  981 */     calendar.setTimeInMillis(time);
/*  982 */     sb.append('[');
/*  983 */     sb.append(formatIntToTwoDigits(calendar.get(5)));
/*  984 */     sb.append('.');
/*  985 */     sb.append(formatIntToTwoDigits(calendar.get(2) + 1));
/*  986 */     sb.append('.');
/*  987 */     sb.append(calendar.get(1));
/*  988 */     sb.append(' ');
/*  989 */     sb.append(formatIntToTwoDigits(calendar.get(11)));
/*  990 */     sb.append(':');
/*  991 */     sb.append(formatIntToTwoDigits(calendar.get(12)));
/*  992 */     sb.append(':');
/*  993 */     sb.append(formatIntToTwoDigits(calendar.get(13)));
/*  994 */     sb.append(']');
/*  995 */     return sb.toString();
/*      */   }
/*      */   
/*      */   public static String formatIntToTwoDigits(int n) {
/*  999 */     return n < 10 ? "0".concat(String.valueOf(n)) : String.valueOf(n);
/*      */   }
/*      */   
/*      */   private static String formatDate(long date, String format) {
/* 1003 */     if (date == 0L) return "";
/* 1004 */     SimpleDateFormat temp = new SimpleDateFormat(format);
/* 1005 */     return temp.format(new Date(date));
/*      */   }
/*      */   
/*      */   public static String formatDate(long date) {
/* 1009 */     return formatDate(date, "dd-MMM-yyyy HH:mm:ss");
/*      */   }
/*      */   
/*      */   public static String formatDateShort(long date) {
/* 1013 */     return formatDate(date, "MMM dd, HH:mm");
/*      */   }
/*      */   
/*      */   public static String formatDateNum(long date) {
/* 1017 */     return formatDate(date, "yyyy-MM-dd HH:mm:ss");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String formatCustomDateOnly(long date)
/*      */   {
/* 1025 */     if (date == 0L) return "";
/* 1026 */     return formatDate(date, "dd-MMM-yyyy");
/*      */   }
/*      */   
/*      */   public static String formatCustomTimeOnly(long date) {
/* 1030 */     return formatCustomTimeOnly(date, true);
/*      */   }
/*      */   
/*      */   public static String formatCustomTimeOnly(long date, boolean with_secs) {
/* 1034 */     if (date == 0L) return "";
/* 1035 */     return formatDate(date, with_secs ? "HH:mm:ss" : "HH:mm");
/*      */   }
/*      */   
/*      */   public static String formatCustomDateTime(long date) {
/* 1039 */     if (date == 0L) return "";
/* 1040 */     return formatDate(date);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String formatTime(long time)
/*      */   {
/* 1051 */     return TimeFormatter.formatColon(time / 1000L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String formatDecimal(double value, int precision)
/*      */   {
/* 1067 */     return formatDecimal(value, precision, false, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String formatDecimal(double value, int precision, boolean bTruncateZeros, boolean bRound)
/*      */   {
/* 1088 */     if ((Double.isNaN(value)) || (Double.isInfinite(value))) {
/* 1089 */       return "∞";
/*      */     }
/*      */     double tValue;
/*      */     double tValue;
/* 1093 */     if (bRound) {
/* 1094 */       tValue = value;
/*      */     } else {
/*      */       double tValue;
/* 1097 */       if (precision == 0) {
/* 1098 */         tValue = value;
/*      */       } else {
/* 1100 */         double shift = Math.pow(10.0D, precision);
/* 1101 */         tValue = (value * shift) / shift;
/*      */       }
/*      */     }
/*      */     
/* 1105 */     int cache_index = (precision << 2) + ((bTruncateZeros ? 1 : 0) << 1) + (bRound ? 1 : 0);
/*      */     
/*      */ 
/* 1108 */     NumberFormat nf = null;
/*      */     
/* 1110 */     if (cache_index < cached_number_formats.length) {
/* 1111 */       nf = cached_number_formats[cache_index];
/*      */     }
/*      */     
/* 1114 */     if (nf == null) {
/* 1115 */       nf = NumberFormat.getNumberInstance();
/* 1116 */       nf.setGroupingUsed(false);
/* 1117 */       if (!bTruncateZeros) {
/* 1118 */         nf.setMinimumFractionDigits(precision);
/*      */       }
/* 1120 */       if (bRound) {
/* 1121 */         nf.setMaximumFractionDigits(precision);
/*      */       }
/*      */       
/* 1124 */       if (cache_index < cached_number_formats.length) {
/* 1125 */         cached_number_formats[cache_index] = nf;
/*      */       }
/*      */     }
/*      */     
/* 1129 */     return nf.format(tValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String truncateString(String str, int width)
/*      */   {
/* 1144 */     int excess = str.length() - width;
/*      */     
/* 1146 */     if (excess <= 0)
/*      */     {
/* 1148 */       return str;
/*      */     }
/*      */     
/* 1151 */     excess += 3;
/*      */     
/* 1153 */     int token_start = -1;
/* 1154 */     int max_len = 0;
/* 1155 */     int max_start = 0;
/*      */     
/* 1157 */     for (int i = 0; i < str.length(); i++)
/*      */     {
/* 1159 */       char c = str.charAt(i);
/*      */       
/* 1161 */       if ((Character.isLetterOrDigit(c)) || (c == '-') || (c == '~'))
/*      */       {
/* 1163 */         if (token_start == -1)
/*      */         {
/* 1165 */           token_start = i;
/*      */         }
/*      */         else
/*      */         {
/* 1169 */           int len = i - token_start;
/*      */           
/* 1171 */           if (len > max_len)
/*      */           {
/* 1173 */             max_len = len;
/* 1174 */             max_start = token_start;
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/* 1179 */         token_start = -1;
/*      */       }
/*      */     }
/*      */     
/* 1183 */     if (max_len >= excess)
/*      */     {
/* 1185 */       int trim_point = max_start + max_len;
/*      */       
/* 1187 */       return str.substring(0, trim_point - excess) + "..." + str.substring(trim_point);
/*      */     }
/*      */     
/* 1190 */     return str.substring(0, width - 3) + "...";
/*      */   }
/*      */   
/*      */ 
/*      */   public static char getDecimalSeparator()
/*      */   {
/* 1196 */     return decimalSeparator;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static void updateFormatOverrides(String formats)
/*      */   {
/* 1203 */     Map<String, Formatter> map = new HashMap();
/*      */     
/* 1205 */     String[] lines = formats.split("\n");
/*      */     
/* 1207 */     List<String> errors = new ArrayList();
/*      */     
/* 1209 */     for (String line : lines)
/*      */     {
/* 1211 */       String error = null;
/*      */       
/* 1213 */       line = line.trim();
/*      */       
/* 1215 */       if (line.length() != 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1220 */         String[] key_value = line.split(":", 2);
/*      */         
/* 1222 */         if (key_value.length != 2)
/*      */         {
/* 1224 */           error = "is missing ':'";
/*      */         }
/*      */         else
/*      */         {
/* 1228 */           String key = key_value[0].trim();
/* 1229 */           String value = key_value[1].trim();
/*      */           
/* 1231 */           Formatter formatter = new Formatter(null);
/*      */           
/* 1233 */           error = formatter.parse(value);
/*      */           
/* 1235 */           if (error == null)
/*      */           {
/* 1237 */             map.put(key, formatter);
/*      */           }
/*      */         }
/*      */         
/* 1241 */         if (error != null)
/*      */         {
/* 1243 */           errors.add("'" + line + "' " + error);
/*      */         }
/*      */       }
/*      */     }
/*      */     String status_msg;
/*      */     String status_msg;
/* 1249 */     if (errors.size() > 0)
/*      */     {
/* 1251 */       status_msg = "Format parsing failed: " + errors;
/*      */     }
/*      */     else
/*      */     {
/* 1255 */       status_msg = "";
/*      */     }
/*      */     
/* 1258 */     COConfigurationManager.setParameter("config.style.formatOverrides.status", status_msg);
/*      */     
/* 1260 */     format_map = map;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String formatCustomRate(String key, long value)
/*      */   {
/* 1268 */     Formatter formatter = (Formatter)format_map.get(key);
/*      */     
/* 1270 */     if (formatter != null)
/*      */     {
/* 1272 */       return formatter.format(value, true);
/*      */     }
/*      */     
/* 1275 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String formatCustomSize(String key, long value)
/*      */   {
/* 1283 */     Formatter formatter = (Formatter)format_map.get(key);
/*      */     
/* 1285 */     if (formatter != null)
/*      */     {
/* 1287 */       return formatter.format(value, false);
/*      */     }
/*      */     
/* 1290 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   private static class Formatter
/*      */   {
/*      */     private static final int FORMAT_UNIT_B = 1;
/*      */     
/*      */     private static final int FORMAT_UNIT_K = 2;
/*      */     
/*      */     private static final int FORMAT_UNIT_M = 4;
/*      */     private static final int FORMAT_UNIT_G = 8;
/*      */     private static final int FORMAT_UNIT_T = 16;
/*      */     private static final int FORMAT_UNIT_NONE = 0;
/*      */     private static final int FORMAT_UNIT_ALL = 65535;
/* 1305 */     private static final int[] tens = { 1, 10, 100, 1000, 10000, 100000, 1000000 };
/*      */     
/* 1307 */     private int unit_formats = 65535;
/* 1308 */     private boolean hide_units = false;
/* 1309 */     private boolean short_units = false;
/* 1310 */     private Boolean rate_units = null;
/*      */     
/* 1312 */     private NumberFormat number_format = null;
/* 1313 */     private long number_format_fact = 1L;
/*      */     
/* 1315 */     private int rounding = 6;
/*      */     
/*      */ 
/*      */     private String parse(String str)
/*      */     {
/*      */       try
/*      */       {
/* 1322 */         String[] args = str.split(",");
/*      */         
/* 1324 */         for (String arg : args)
/*      */         {
/* 1326 */           arg = arg.trim();
/*      */           
/* 1328 */           if (arg.length() != 0)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1333 */             String[] sub_args = arg.split(";");
/*      */             
/* 1335 */             if (sub_args.length == 0)
/*      */             {
/* 1337 */               return "invalid argument '" + arg + "'";
/*      */             }
/*      */             
/* 1340 */             String main_arg = null;
/*      */             
/* 1342 */             for (String sub_arg : sub_args)
/*      */             {
/* 1344 */               sub_arg = sub_arg.trim();
/*      */               
/* 1346 */               String[] bits = sub_arg.split("=");
/*      */               
/* 1348 */               if (bits.length != 2)
/*      */               {
/* 1350 */                 return "invalid argument '" + arg + "'";
/*      */               }
/*      */               
/* 1353 */               String arg_name = bits[0].trim().toLowerCase(Locale.US);
/* 1354 */               String arg_value = bits[1].trim();
/*      */               
/* 1356 */               if (main_arg == null)
/*      */               {
/* 1358 */                 main_arg = arg_name;
/*      */                 
/* 1360 */                 if (main_arg.equals("units"))
/*      */                 {
/* 1362 */                   int mask = arg_value.contains("-") ? 65535 : 0;
/*      */                   
/* 1364 */                   String[] units = arg_value.toLowerCase(Locale.US).split("&");
/*      */                   
/* 1366 */                   for (String unit : units)
/*      */                   {
/*      */                     boolean remove;
/*      */                     boolean remove;
/* 1370 */                     if (unit.startsWith("-"))
/*      */                     {
/* 1372 */                       unit = unit.substring(1);
/*      */                       
/* 1374 */                       remove = true;
/*      */                     }
/*      */                     else
/*      */                     {
/* 1378 */                       remove = false;
/*      */                     }
/*      */                     
/* 1381 */                     char c = unit.charAt(0);
/*      */                     
/*      */                     int m;
/*      */                     
/* 1385 */                     if (c == 'b')
/*      */                     {
/* 1387 */                       m = 1;
/*      */                     } else { int m;
/* 1389 */                       if (c == 'k')
/*      */                       {
/* 1391 */                         m = 2;
/*      */                       } else { int m;
/* 1393 */                         if (c == 'm')
/*      */                         {
/* 1395 */                           m = 4;
/*      */                         } else { int m;
/* 1397 */                           if (c == 'g')
/*      */                           {
/* 1399 */                             m = 8;
/*      */                           } else { int m;
/* 1401 */                             if (c == 't')
/*      */                             {
/* 1403 */                               m = 16;
/*      */                             }
/*      */                             else
/*      */                             {
/* 1407 */                               return "Invalid unit: " + unit; }
/*      */                           } } } }
/*      */                     int m;
/* 1410 */                     if (remove)
/*      */                     {
/* 1412 */                       mask &= (m ^ 0xFFFFFFFF);
/*      */                     }
/*      */                     else
/*      */                     {
/* 1416 */                       mask |= m;
/*      */                     }
/*      */                   }
/*      */                   
/* 1420 */                   this.unit_formats = mask;
/*      */                 }
/* 1422 */                 else if (main_arg.equals("format"))
/*      */                 {
/* 1424 */                   this.number_format = NumberFormat.getInstance();
/*      */                   
/* 1426 */                   if ((this.number_format instanceof DecimalFormat))
/*      */                   {
/* 1428 */                     ((DecimalFormat)this.number_format).applyPattern(arg_value);
/*      */                   }
/*      */                   else
/*      */                   {
/* 1432 */                     Debug.out("Number pattern isn't a DecimalFormat: " + this.number_format);
/*      */                   }
/*      */                   
/* 1435 */                   int max_fd = this.number_format.getMaximumFractionDigits();
/*      */                   
/* 1437 */                   if (max_fd < tens.length)
/*      */                   {
/* 1439 */                     this.number_format_fact = tens[max_fd];
/*      */                   }
/*      */                   else
/*      */                   {
/* 1443 */                     this.number_format_fact = 1L;
/*      */                     
/* 1445 */                     for (int i = 0; i < max_fd; i++)
/*      */                     {
/* 1447 */                       this.number_format_fact *= 10L;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 else {
/* 1452 */                   Debug.out("TODO: " + main_arg);
/*      */                 }
/*      */                 
/*      */               }
/* 1456 */               else if (main_arg.equals("units"))
/*      */               {
/* 1458 */                 if (arg_name.equals("hide"))
/*      */                 {
/* 1460 */                   this.hide_units = arg_value.toLowerCase(Locale.US).startsWith("y");
/*      */                 }
/* 1462 */                 else if (arg_name.equals("short"))
/*      */                 {
/* 1464 */                   this.short_units = arg_value.toLowerCase(Locale.US).startsWith("y");
/*      */                 }
/* 1466 */                 else if (arg_name.equals("rate"))
/*      */                 {
/* 1468 */                   this.rate_units = Boolean.valueOf(arg_value.toLowerCase(Locale.US).startsWith("y"));
/*      */                 }
/*      */                 else
/*      */                 {
/* 1472 */                   Debug.out("TODO: " + arg_name);
/*      */                 }
/* 1474 */               } else if (main_arg.equals("format"))
/*      */               {
/* 1476 */                 if (arg_name.equals("round"))
/*      */                 {
/* 1478 */                   String r = arg_value.toLowerCase(Locale.US);
/*      */                   
/* 1480 */                   if (r.equals("up"))
/*      */                   {
/* 1482 */                     this.rounding = 0;
/*      */                   }
/* 1484 */                   else if (r.equals("down"))
/*      */                   {
/* 1486 */                     this.rounding = 1;
/*      */                   }
/* 1488 */                   else if (r.equals("halfup"))
/*      */                   {
/* 1490 */                     this.rounding = 4;
/*      */                   }
/* 1492 */                   else if (r.equals("halfdown"))
/*      */                   {
/* 1494 */                     this.rounding = 5;
/*      */                   }
/*      */                   else
/*      */                   {
/* 1498 */                     return "Invald round mode: " + r;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               else {
/* 1503 */                 Debug.out("TODO: " + arg_name);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1509 */         return null;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1513 */         return Debug.getNestedExceptionMessage(e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private String format(long _value, boolean is_rate)
/*      */     {
/*      */       try
/*      */       {
/* 1523 */         double value = _value;
/*      */         
/* 1525 */         String unit_str = "";
/*      */         
/* 1527 */         if (this.unit_formats == 2)
/*      */         {
/* 1529 */           value /= 1024.0D;
/*      */           
/* 1531 */           unit_str = DisplayFormatters.all_units[1];
/*      */         }
/* 1533 */         else if (this.unit_formats == 4)
/*      */         {
/* 1535 */           value /= 1048576.0D;
/*      */           
/* 1537 */           unit_str = DisplayFormatters.all_units[2];
/*      */         }
/* 1539 */         else if (this.unit_formats == 8)
/*      */         {
/* 1541 */           value /= 1.073741824E9D;
/*      */           
/* 1543 */           unit_str = DisplayFormatters.all_units[3];
/*      */         }
/* 1545 */         else if (this.unit_formats == 16)
/*      */         {
/* 1547 */           value /= 1.099511627776E12D;
/*      */           
/* 1549 */           unit_str = DisplayFormatters.all_units[4];
/*      */         }
/*      */         
/*      */         String result;
/*      */         
/* 1554 */         if (this.number_format != null)
/*      */         {
/* 1556 */           if (this.rounding != 6)
/*      */           {
/*      */ 
/*      */ 
/* 1560 */             double l_value = value;
/*      */             
/* 1562 */             double fraction = value - l_value;
/*      */             
/* 1564 */             fraction *= this.number_format_fact;
/*      */             
/* 1566 */             double l_fraction = fraction;
/*      */             
/* 1568 */             double rem = fraction - l_fraction;
/*      */             
/* 1570 */             if (this.rounding != 1)
/*      */             {
/* 1572 */               if (this.rounding == 0)
/*      */               {
/* 1574 */                 if (rem > 0.0D)
/*      */                 {
/* 1576 */                   l_fraction += 1.0D;
/*      */                 }
/* 1578 */               } else if (this.rounding == 4)
/*      */               {
/* 1580 */                 if (rem >= 0.5D)
/*      */                 {
/* 1582 */                   l_fraction += 1.0D;
/*      */                 }
/* 1584 */               } else if (this.rounding == 5)
/*      */               {
/* 1586 */                 if (rem > 0.5D)
/*      */                 {
/* 1588 */                   l_fraction += 1.0D;
/*      */                 }
/*      */               }
/*      */             }
/* 1592 */             l_fraction /= this.number_format_fact;
/*      */             
/*      */ 
/*      */ 
/* 1596 */             value = l_value + l_fraction;
/*      */           }
/*      */           String result;
/* 1599 */           synchronized (this.number_format)
/*      */           {
/* 1601 */             result = this.number_format.format(value);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1606 */           result = String.valueOf(value);
/*      */         }
/*      */         
/* 1609 */         if (this.hide_units)
/*      */         {
/* 1611 */           return result;
/*      */         }
/*      */         
/* 1614 */         if (unit_str.length() > 0)
/*      */         {
/* 1616 */           if (this.short_units)
/*      */           {
/* 1618 */             result = result + " " + unit_str.charAt(1);
/*      */           }
/*      */           else
/*      */           {
/* 1622 */             result = result + unit_str;
/*      */           }
/*      */         }
/*      */         
/* 1626 */         if ((is_rate) && ((this.rate_units == null) || (this.rate_units.booleanValue()))) {}
/*      */         
/* 1628 */         return result + DisplayFormatters.per_sec;
/*      */ 
/*      */ 
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*      */ 
/* 1635 */         Debug.out(e);
/*      */       }
/* 1637 */       return String.valueOf(_value);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/* 1652 */     double d = 3.991630774821635E-6D;
/* 1653 */     NumberFormat nf = NumberFormat.getNumberInstance();
/* 1654 */     nf.setMaximumFractionDigits(6);
/* 1655 */     nf.setMinimumFractionDigits(6);
/* 1656 */     String s = nf.format(d);
/*      */     
/* 1658 */     System.out.println("Actual: " + d);
/* 1659 */     System.out.println("NF/6:   " + s);
/*      */     
/* 1661 */     System.out.println("DF:     " + formatDecimal(d, 6));
/*      */     
/* 1663 */     System.out.println("DF 0:   " + formatDecimal(d, 0));
/*      */     
/* 1665 */     System.out.println("0.000000:" + formatDecimal(0.0D, 6));
/*      */     
/* 1667 */     System.out.println("0.001:" + formatDecimal(0.001D, 6, true, true));
/*      */     
/* 1669 */     System.out.println("0:" + formatDecimal(0.0D, 0));
/*      */     
/* 1671 */     System.out.println("123456:" + formatDecimal(123456.0D, 0));
/*      */     
/* 1673 */     System.out.println("123456:" + formatDecimal(123456.999D, 0));
/* 1674 */     System.out.println(formatDecimal(NaN.0D, 3));
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/DisplayFormatters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */