package org.gudy.bouncycastle.asn1;

import java.io.InputStream;

public abstract interface ASN1OctetStringParser
  extends DEREncodable
{
  public abstract InputStream getOctetStream();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/ASN1OctetStringParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */