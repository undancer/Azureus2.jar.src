package org.gudy.azureus2.plugins.utils;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;

public abstract interface Formatters
{
  public static final String BYTE_ENCODING = "ISO-8859-1";
  public static final String TEXT_ENCODING = "UTF8";
  
  public abstract String formatByteCountToKiBEtc(long paramLong);
  
  public abstract String formatByteCountToKiBEtcPerSec(long paramLong);
  
  public abstract String formatPercentFromThousands(long paramLong);
  
  public abstract String formatByteArray(byte[] paramArrayOfByte, boolean paramBoolean);
  
  public abstract String encodeBytesToString(byte[] paramArrayOfByte);
  
  public abstract byte[] decodeBytesFromString(String paramString);
  
  public abstract String formatDate(long paramLong);
  
  public abstract String formatTimeOnly(long paramLong);
  
  public abstract String formatTimeOnly(long paramLong, boolean paramBoolean);
  
  public abstract String formatDateOnly(long paramLong);
  
  public abstract String formatTimeFromSeconds(long paramLong);
  
  public abstract String formatETAFromSeconds(long paramLong);
  
  public abstract byte[] bEncode(Map paramMap)
    throws IOException;
  
  public abstract Map bDecode(byte[] paramArrayOfByte)
    throws IOException;
  
  public abstract String base32Encode(byte[] paramArrayOfByte);
  
  public abstract byte[] base32Decode(String paramString);
  
  public abstract Comparator getAlphanumericComparator(boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/Formatters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */