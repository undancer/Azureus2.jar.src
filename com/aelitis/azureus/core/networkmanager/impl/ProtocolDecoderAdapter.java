package com.aelitis.azureus.core.networkmanager.impl;

import java.nio.ByteBuffer;

public abstract interface ProtocolDecoderAdapter
{
  public static final int MATCH_NONE = 1;
  public static final int MATCH_CRYPTO_NO_AUTO_FALLBACK = 2;
  public static final int MATCH_CRYPTO_AUTO_FALLBACK = 3;
  
  public abstract int getMaximumPlainHeaderLength();
  
  public abstract int matchPlainHeader(ByteBuffer paramByteBuffer);
  
  public abstract void gotSecret(byte[] paramArrayOfByte);
  
  public abstract void decodeComplete(ProtocolDecoder paramProtocolDecoder, ByteBuffer paramByteBuffer);
  
  public abstract void decodeFailed(ProtocolDecoder paramProtocolDecoder, Throwable paramThrowable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/ProtocolDecoderAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */