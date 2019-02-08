package org.gudy.bouncycastle.asn1;

import java.io.IOException;

public abstract interface ASN1TaggedObjectParser
  extends DEREncodable
{
  public abstract int getTagNo();
  
  public abstract DEREncodable getObjectParser(int paramInt, boolean paramBoolean)
    throws IOException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/ASN1TaggedObjectParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */