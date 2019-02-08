package org.gudy.bouncycastle.asn1;

import java.io.IOException;

public abstract interface ASN1SequenceParser
  extends DEREncodable
{
  public abstract DEREncodable readObject()
    throws IOException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/ASN1SequenceParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */