package org.gudy.bouncycastle.util.encoders;

public abstract interface Translator
{
  public abstract int getEncodedBlockSize();
  
  public abstract int encode(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3);
  
  public abstract int getDecodedBlockSize();
  
  public abstract int decode(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/util/encoders/Translator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */