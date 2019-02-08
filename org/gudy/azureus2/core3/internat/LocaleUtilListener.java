package org.gudy.azureus2.core3.internat;

public abstract interface LocaleUtilListener
{
  public abstract LocaleUtilDecoderCandidate selectDecoder(LocaleUtil paramLocaleUtil, Object paramObject, LocaleUtilDecoderCandidate[] paramArrayOfLocaleUtilDecoderCandidate)
    throws LocaleUtilEncodingException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/internat/LocaleUtilListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */