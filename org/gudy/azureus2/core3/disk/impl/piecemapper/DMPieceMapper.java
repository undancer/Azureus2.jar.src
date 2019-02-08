package org.gudy.azureus2.core3.disk.impl.piecemapper;

import java.io.UnsupportedEncodingException;
import org.gudy.azureus2.core3.internat.LocaleUtilDecoder;

public abstract interface DMPieceMapper
{
  public abstract void construct(LocaleUtilDecoder paramLocaleUtilDecoder, String paramString)
    throws UnsupportedEncodingException;
  
  public abstract DMPieceMap getPieceMap();
  
  public abstract DMPieceMapperFile[] getFiles();
  
  public abstract int getPieceLength();
  
  public abstract int getLastPieceLength();
  
  public abstract long getTotalLength();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/piecemapper/DMPieceMapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */