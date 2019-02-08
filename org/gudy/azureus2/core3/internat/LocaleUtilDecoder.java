package org.gudy.azureus2.core3.internat;

import java.io.UnsupportedEncodingException;

public abstract interface LocaleUtilDecoder
{
  public abstract String getName();
  
  public abstract int getIndex();
  
  public abstract String tryDecode(byte[] paramArrayOfByte, boolean paramBoolean);
  
  public abstract String decodeString(byte[] paramArrayOfByte)
    throws UnsupportedEncodingException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/internat/LocaleUtilDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */