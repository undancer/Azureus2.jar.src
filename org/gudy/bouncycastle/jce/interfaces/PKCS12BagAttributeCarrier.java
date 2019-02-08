package org.gudy.bouncycastle.jce.interfaces;

import java.util.Enumeration;
import org.gudy.bouncycastle.asn1.DEREncodable;
import org.gudy.bouncycastle.asn1.DERObjectIdentifier;

public abstract interface PKCS12BagAttributeCarrier
{
  public abstract void setBagAttribute(DERObjectIdentifier paramDERObjectIdentifier, DEREncodable paramDEREncodable);
  
  public abstract DEREncodable getBagAttribute(DERObjectIdentifier paramDERObjectIdentifier);
  
  public abstract Enumeration getBagAttributeKeys();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/interfaces/PKCS12BagAttributeCarrier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */