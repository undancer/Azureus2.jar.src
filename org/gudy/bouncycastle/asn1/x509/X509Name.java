/*      */ package org.gudy.bouncycastle.asn1.x509;
/*      */ 
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Vector;
/*      */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*      */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*      */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*      */ import org.gudy.bouncycastle.asn1.ASN1Set;
/*      */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*      */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*      */ import org.gudy.bouncycastle.asn1.DERObject;
/*      */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*      */ import org.gudy.bouncycastle.asn1.DERSequence;
/*      */ import org.gudy.bouncycastle.asn1.DERSet;
/*      */ import org.gudy.bouncycastle.asn1.DERString;
/*      */ import org.gudy.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
/*      */ import org.gudy.bouncycastle.util.Strings;
/*      */ import org.gudy.bouncycastle.util.encoders.Hex;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class X509Name
/*      */   extends ASN1Encodable
/*      */ {
/*   41 */   public static final DERObjectIdentifier C = new DERObjectIdentifier("2.5.4.6");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   46 */   public static final DERObjectIdentifier O = new DERObjectIdentifier("2.5.4.10");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   51 */   public static final DERObjectIdentifier OU = new DERObjectIdentifier("2.5.4.11");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   56 */   public static final DERObjectIdentifier T = new DERObjectIdentifier("2.5.4.12");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   61 */   public static final DERObjectIdentifier CN = new DERObjectIdentifier("2.5.4.3");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   66 */   public static final DERObjectIdentifier SN = new DERObjectIdentifier("2.5.4.5");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   71 */   public static final DERObjectIdentifier STREET = new DERObjectIdentifier("2.5.4.9");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   76 */   public static final DERObjectIdentifier SERIALNUMBER = SN;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   81 */   public static final DERObjectIdentifier L = new DERObjectIdentifier("2.5.4.7");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   86 */   public static final DERObjectIdentifier ST = new DERObjectIdentifier("2.5.4.8");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   91 */   public static final DERObjectIdentifier SURNAME = new DERObjectIdentifier("2.5.4.4");
/*   92 */   public static final DERObjectIdentifier GIVENNAME = new DERObjectIdentifier("2.5.4.42");
/*   93 */   public static final DERObjectIdentifier INITIALS = new DERObjectIdentifier("2.5.4.43");
/*   94 */   public static final DERObjectIdentifier GENERATION = new DERObjectIdentifier("2.5.4.44");
/*   95 */   public static final DERObjectIdentifier UNIQUE_IDENTIFIER = new DERObjectIdentifier("2.5.4.45");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  100 */   public static final DERObjectIdentifier BUSINESS_CATEGORY = new DERObjectIdentifier("2.5.4.15");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  106 */   public static final DERObjectIdentifier POSTAL_CODE = new DERObjectIdentifier("2.5.4.17");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  112 */   public static final DERObjectIdentifier DN_QUALIFIER = new DERObjectIdentifier("2.5.4.46");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  118 */   public static final DERObjectIdentifier PSEUDONYM = new DERObjectIdentifier("2.5.4.65");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  125 */   public static final DERObjectIdentifier DATE_OF_BIRTH = new DERObjectIdentifier("1.3.6.1.5.5.7.9.1");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  131 */   public static final DERObjectIdentifier PLACE_OF_BIRTH = new DERObjectIdentifier("1.3.6.1.5.5.7.9.2");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  137 */   public static final DERObjectIdentifier GENDER = new DERObjectIdentifier("1.3.6.1.5.5.7.9.3");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  144 */   public static final DERObjectIdentifier COUNTRY_OF_CITIZENSHIP = new DERObjectIdentifier("1.3.6.1.5.5.7.9.4");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  151 */   public static final DERObjectIdentifier COUNTRY_OF_RESIDENCE = new DERObjectIdentifier("1.3.6.1.5.5.7.9.5");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  158 */   public static final DERObjectIdentifier NAME_AT_BIRTH = new DERObjectIdentifier("1.3.36.8.3.14");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  164 */   public static final DERObjectIdentifier POSTAL_ADDRESS = new DERObjectIdentifier("2.5.4.16");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  171 */   public static final DERObjectIdentifier EmailAddress = PKCSObjectIdentifiers.pkcs_9_at_emailAddress;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  176 */   public static final DERObjectIdentifier UnstructuredName = PKCSObjectIdentifiers.pkcs_9_at_unstructuredName;
/*  177 */   public static final DERObjectIdentifier UnstructuredAddress = PKCSObjectIdentifiers.pkcs_9_at_unstructuredAddress;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  182 */   public static final DERObjectIdentifier E = EmailAddress;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  187 */   public static final DERObjectIdentifier DC = new DERObjectIdentifier("0.9.2342.19200300.100.1.25");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  192 */   public static final DERObjectIdentifier UID = new DERObjectIdentifier("0.9.2342.19200300.100.1.1");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  197 */   public static Hashtable OIDLookUp = new Hashtable();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  203 */   public static boolean DefaultReverse = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  209 */   public static Hashtable DefaultSymbols = OIDLookUp;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  215 */   public static Hashtable RFC2253Symbols = new Hashtable();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  221 */   public static Hashtable RFC1779Symbols = new Hashtable();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  227 */   public static Hashtable SymbolLookUp = new Hashtable();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  232 */   public static Hashtable DefaultLookUp = SymbolLookUp;
/*      */   
/*  234 */   private static final Boolean TRUE = Boolean.TRUE;
/*  235 */   private static final Boolean FALSE = Boolean.FALSE;
/*      */   
/*      */   static
/*      */   {
/*  239 */     DefaultSymbols.put(C, "C");
/*  240 */     DefaultSymbols.put(O, "O");
/*  241 */     DefaultSymbols.put(T, "T");
/*  242 */     DefaultSymbols.put(OU, "OU");
/*  243 */     DefaultSymbols.put(CN, "CN");
/*  244 */     DefaultSymbols.put(L, "L");
/*  245 */     DefaultSymbols.put(ST, "ST");
/*  246 */     DefaultSymbols.put(SN, "SERIALNUMBER");
/*  247 */     DefaultSymbols.put(EmailAddress, "E");
/*  248 */     DefaultSymbols.put(DC, "DC");
/*  249 */     DefaultSymbols.put(UID, "UID");
/*  250 */     DefaultSymbols.put(STREET, "STREET");
/*  251 */     DefaultSymbols.put(SURNAME, "SURNAME");
/*  252 */     DefaultSymbols.put(GIVENNAME, "GIVENNAME");
/*  253 */     DefaultSymbols.put(INITIALS, "INITIALS");
/*  254 */     DefaultSymbols.put(GENERATION, "GENERATION");
/*  255 */     DefaultSymbols.put(UnstructuredAddress, "unstructuredAddress");
/*  256 */     DefaultSymbols.put(UnstructuredName, "unstructuredName");
/*  257 */     DefaultSymbols.put(UNIQUE_IDENTIFIER, "UniqueIdentifier");
/*  258 */     DefaultSymbols.put(DN_QUALIFIER, "DN");
/*  259 */     DefaultSymbols.put(PSEUDONYM, "Pseudonym");
/*  260 */     DefaultSymbols.put(POSTAL_ADDRESS, "PostalAddress");
/*  261 */     DefaultSymbols.put(NAME_AT_BIRTH, "NameAtBirth");
/*  262 */     DefaultSymbols.put(COUNTRY_OF_CITIZENSHIP, "CountryOfCitizenship");
/*  263 */     DefaultSymbols.put(COUNTRY_OF_RESIDENCE, "CountryOfResidence");
/*  264 */     DefaultSymbols.put(GENDER, "Gender");
/*  265 */     DefaultSymbols.put(PLACE_OF_BIRTH, "PlaceOfBirth");
/*  266 */     DefaultSymbols.put(DATE_OF_BIRTH, "DateOfBirth");
/*  267 */     DefaultSymbols.put(POSTAL_CODE, "PostalCode");
/*  268 */     DefaultSymbols.put(BUSINESS_CATEGORY, "BusinessCategory");
/*      */     
/*  270 */     RFC2253Symbols.put(C, "C");
/*  271 */     RFC2253Symbols.put(O, "O");
/*  272 */     RFC2253Symbols.put(OU, "OU");
/*  273 */     RFC2253Symbols.put(CN, "CN");
/*  274 */     RFC2253Symbols.put(L, "L");
/*  275 */     RFC2253Symbols.put(ST, "ST");
/*  276 */     RFC2253Symbols.put(STREET, "STREET");
/*  277 */     RFC2253Symbols.put(DC, "DC");
/*  278 */     RFC2253Symbols.put(UID, "UID");
/*      */     
/*  280 */     RFC1779Symbols.put(C, "C");
/*  281 */     RFC1779Symbols.put(O, "O");
/*  282 */     RFC1779Symbols.put(OU, "OU");
/*  283 */     RFC1779Symbols.put(CN, "CN");
/*  284 */     RFC1779Symbols.put(L, "L");
/*  285 */     RFC1779Symbols.put(ST, "ST");
/*  286 */     RFC1779Symbols.put(STREET, "STREET");
/*      */     
/*  288 */     DefaultLookUp.put("c", C);
/*  289 */     DefaultLookUp.put("o", O);
/*  290 */     DefaultLookUp.put("t", T);
/*  291 */     DefaultLookUp.put("ou", OU);
/*  292 */     DefaultLookUp.put("cn", CN);
/*  293 */     DefaultLookUp.put("l", L);
/*  294 */     DefaultLookUp.put("st", ST);
/*  295 */     DefaultLookUp.put("sn", SN);
/*  296 */     DefaultLookUp.put("serialnumber", SN);
/*  297 */     DefaultLookUp.put("street", STREET);
/*  298 */     DefaultLookUp.put("emailaddress", E);
/*  299 */     DefaultLookUp.put("dc", DC);
/*  300 */     DefaultLookUp.put("e", E);
/*  301 */     DefaultLookUp.put("uid", UID);
/*  302 */     DefaultLookUp.put("surname", SURNAME);
/*  303 */     DefaultLookUp.put("givenname", GIVENNAME);
/*  304 */     DefaultLookUp.put("initials", INITIALS);
/*  305 */     DefaultLookUp.put("generation", GENERATION);
/*  306 */     DefaultLookUp.put("unstructuredaddress", UnstructuredAddress);
/*  307 */     DefaultLookUp.put("unstructuredname", UnstructuredName);
/*  308 */     DefaultLookUp.put("uniqueidentifier", UNIQUE_IDENTIFIER);
/*  309 */     DefaultLookUp.put("dn", DN_QUALIFIER);
/*  310 */     DefaultLookUp.put("pseudonym", PSEUDONYM);
/*  311 */     DefaultLookUp.put("postaladdress", POSTAL_ADDRESS);
/*  312 */     DefaultLookUp.put("nameofbirth", NAME_AT_BIRTH);
/*  313 */     DefaultLookUp.put("countryofcitizenship", COUNTRY_OF_CITIZENSHIP);
/*  314 */     DefaultLookUp.put("countryofresidence", COUNTRY_OF_RESIDENCE);
/*  315 */     DefaultLookUp.put("gender", GENDER);
/*  316 */     DefaultLookUp.put("placeofbirth", PLACE_OF_BIRTH);
/*  317 */     DefaultLookUp.put("dateofbirth", DATE_OF_BIRTH);
/*  318 */     DefaultLookUp.put("postalcode", POSTAL_CODE);
/*  319 */     DefaultLookUp.put("businesscategory", BUSINESS_CATEGORY);
/*      */   }
/*      */   
/*  322 */   private X509NameEntryConverter converter = null;
/*  323 */   private Vector ordering = new Vector();
/*  324 */   private Vector values = new Vector();
/*  325 */   private Vector added = new Vector();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private ASN1Sequence seq;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static X509Name getInstance(ASN1TaggedObject obj, boolean explicit)
/*      */   {
/*  340 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*      */   }
/*      */   
/*      */ 
/*      */   public static X509Name getInstance(Object obj)
/*      */   {
/*  346 */     if ((obj == null) || ((obj instanceof X509Name)))
/*      */     {
/*  348 */       return (X509Name)obj;
/*      */     }
/*  350 */     if ((obj instanceof ASN1Sequence))
/*      */     {
/*  352 */       return new X509Name((ASN1Sequence)obj);
/*      */     }
/*      */     
/*  355 */     throw new IllegalArgumentException("unknown object in factory \"" + obj.getClass().getName() + "\"");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X509Name(ASN1Sequence seq)
/*      */   {
/*  366 */     this.seq = seq;
/*      */     
/*  368 */     Enumeration e = seq.getObjects();
/*      */     
/*  370 */     while (e.hasMoreElements())
/*      */     {
/*  372 */       ASN1Set set = ASN1Set.getInstance(e.nextElement());
/*      */       
/*  374 */       for (int i = 0; i < set.size(); i++)
/*      */       {
/*  376 */         ASN1Sequence s = ASN1Sequence.getInstance(set.getObjectAt(i));
/*      */         
/*  378 */         if (s.size() != 2)
/*      */         {
/*  380 */           throw new IllegalArgumentException("badly sized pair");
/*      */         }
/*      */         
/*  383 */         this.ordering.addElement(DERObjectIdentifier.getInstance(s.getObjectAt(0)));
/*      */         
/*  385 */         DEREncodable value = s.getObjectAt(1);
/*  386 */         if ((value instanceof DERString))
/*      */         {
/*  388 */           this.values.addElement(((DERString)value).getString());
/*      */         }
/*      */         else
/*      */         {
/*  392 */           this.values.addElement("#" + bytesToString(Hex.encode(value.getDERObject().getDEREncoded())));
/*      */         }
/*  394 */         this.added.addElement(i != 0 ? TRUE : FALSE);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public X509Name(Hashtable attributes)
/*      */   {
/*  414 */     this(null, attributes);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X509Name(Vector ordering, Hashtable attributes)
/*      */   {
/*  429 */     this(ordering, attributes, new X509DefaultEntryConverter());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X509Name(Vector ordering, Hashtable attributes, X509NameEntryConverter converter)
/*      */   {
/*  448 */     this.converter = converter;
/*      */     
/*  450 */     if (ordering != null)
/*      */     {
/*  452 */       for (int i = 0; i != ordering.size(); i++)
/*      */       {
/*  454 */         this.ordering.addElement(ordering.elementAt(i));
/*  455 */         this.added.addElement(FALSE);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  460 */       Enumeration e = attributes.keys();
/*      */       
/*  462 */       while (e.hasMoreElements())
/*      */       {
/*  464 */         this.ordering.addElement(e.nextElement());
/*  465 */         this.added.addElement(FALSE);
/*      */       }
/*      */     }
/*      */     
/*  469 */     for (int i = 0; i != this.ordering.size(); i++)
/*      */     {
/*  471 */       DERObjectIdentifier oid = (DERObjectIdentifier)this.ordering.elementAt(i);
/*      */       
/*  473 */       if (attributes.get(oid) == null)
/*      */       {
/*  475 */         throw new IllegalArgumentException("No attribute for object id - " + oid.getId() + " - passed to distinguished name");
/*      */       }
/*      */       
/*  478 */       this.values.addElement(attributes.get(oid));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X509Name(Vector oids, Vector values)
/*      */   {
/*  489 */     this(oids, values, new X509DefaultEntryConverter());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X509Name(Vector oids, Vector values, X509NameEntryConverter converter)
/*      */   {
/*  503 */     this.converter = converter;
/*      */     
/*  505 */     if (oids.size() != values.size())
/*      */     {
/*  507 */       throw new IllegalArgumentException("oids vector must be same length as values.");
/*      */     }
/*      */     
/*  510 */     for (int i = 0; i < oids.size(); i++)
/*      */     {
/*  512 */       this.ordering.addElement(oids.elementAt(i));
/*  513 */       this.values.addElement(values.elementAt(i));
/*  514 */       this.added.addElement(FALSE);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X509Name(String dirName)
/*      */   {
/*  525 */     this(DefaultReverse, DefaultLookUp, dirName);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X509Name(String dirName, X509NameEntryConverter converter)
/*      */   {
/*  538 */     this(DefaultReverse, DefaultLookUp, dirName, converter);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X509Name(boolean reverse, String dirName)
/*      */   {
/*  551 */     this(reverse, DefaultLookUp, dirName);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X509Name(boolean reverse, String dirName, X509NameEntryConverter converter)
/*      */   {
/*  566 */     this(reverse, DefaultLookUp, dirName, converter);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X509Name(boolean reverse, Hashtable lookUp, String dirName)
/*      */   {
/*  587 */     this(reverse, lookUp, dirName, new X509DefaultEntryConverter());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private DERObjectIdentifier decodeOID(String name, Hashtable lookUp)
/*      */   {
/*  594 */     if (Strings.toUpperCase(name).startsWith("OID."))
/*      */     {
/*  596 */       return new DERObjectIdentifier(name.substring(4));
/*      */     }
/*  598 */     if ((name.charAt(0) >= '0') && (name.charAt(0) <= '9'))
/*      */     {
/*  600 */       return new DERObjectIdentifier(name);
/*      */     }
/*      */     
/*  603 */     DERObjectIdentifier oid = (DERObjectIdentifier)lookUp.get(Strings.toLowerCase(name));
/*  604 */     if (oid == null)
/*      */     {
/*  606 */       throw new IllegalArgumentException("Unknown object id - " + name + " - passed to distinguished name");
/*      */     }
/*      */     
/*  609 */     return oid;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X509Name(boolean reverse, Hashtable lookUp, String dirName, X509NameEntryConverter converter)
/*      */   {
/*  631 */     this.converter = converter;
/*  632 */     X509NameTokenizer nTok = new X509NameTokenizer(dirName);
/*      */     
/*  634 */     while (nTok.hasMoreTokens())
/*      */     {
/*  636 */       String token = nTok.nextToken();
/*  637 */       int index = token.indexOf('=');
/*      */       
/*  639 */       if (index == -1)
/*      */       {
/*  641 */         throw new IllegalArgumentException("badly formated directory string");
/*      */       }
/*      */       
/*  644 */       String name = token.substring(0, index);
/*  645 */       String value = token.substring(index + 1);
/*  646 */       DERObjectIdentifier oid = decodeOID(name, lookUp);
/*      */       
/*  648 */       if (value.indexOf('+') > 0)
/*      */       {
/*  650 */         X509NameTokenizer vTok = new X509NameTokenizer(value, '+');
/*      */         
/*  652 */         this.ordering.addElement(oid);
/*  653 */         this.values.addElement(vTok.nextToken());
/*  654 */         this.added.addElement(FALSE);
/*      */         
/*  656 */         while (vTok.hasMoreTokens())
/*      */         {
/*  658 */           String sv = vTok.nextToken();
/*  659 */           int ndx = sv.indexOf('=');
/*      */           
/*  661 */           String nm = sv.substring(0, ndx);
/*  662 */           String vl = sv.substring(ndx + 1);
/*  663 */           this.ordering.addElement(decodeOID(nm, lookUp));
/*  664 */           this.values.addElement(vl);
/*  665 */           this.added.addElement(TRUE);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  670 */         this.ordering.addElement(oid);
/*  671 */         this.values.addElement(value);
/*  672 */         this.added.addElement(FALSE);
/*      */       }
/*      */     }
/*      */     
/*  676 */     if (reverse)
/*      */     {
/*  678 */       Vector o = new Vector();
/*  679 */       Vector v = new Vector();
/*  680 */       Vector a = new Vector();
/*  681 */       int count = 1;
/*      */       
/*  683 */       for (int i = 0; i < this.ordering.size(); i++)
/*      */       {
/*  685 */         if (((Boolean)this.added.elementAt(i)).booleanValue())
/*      */         {
/*  687 */           o.insertElementAt(this.ordering.elementAt(i), count);
/*  688 */           v.insertElementAt(this.values.elementAt(i), count);
/*  689 */           a.insertElementAt(this.added.elementAt(i), count);
/*  690 */           count++;
/*      */         }
/*      */         else
/*      */         {
/*  694 */           o.insertElementAt(this.ordering.elementAt(i), 0);
/*  695 */           v.insertElementAt(this.values.elementAt(i), 0);
/*  696 */           a.insertElementAt(this.added.elementAt(i), 0);
/*  697 */           count = 1;
/*      */         }
/*      */       }
/*      */       
/*  701 */       this.ordering = o;
/*  702 */       this.values = v;
/*  703 */       this.added = a;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Vector getOIDs()
/*      */   {
/*  712 */     Vector v = new Vector();
/*      */     
/*  714 */     for (int i = 0; i != this.ordering.size(); i++)
/*      */     {
/*  716 */       v.addElement(this.ordering.elementAt(i));
/*      */     }
/*      */     
/*  719 */     return v;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Vector getValues()
/*      */   {
/*  728 */     Vector v = new Vector();
/*      */     
/*  730 */     for (int i = 0; i != this.values.size(); i++)
/*      */     {
/*  732 */       v.addElement(this.values.elementAt(i));
/*      */     }
/*      */     
/*  735 */     return v;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Vector getValues(DERObjectIdentifier oid)
/*      */   {
/*  745 */     Vector v = new Vector();
/*      */     
/*  747 */     for (int i = 0; i != this.values.size(); i++)
/*      */     {
/*  749 */       if (this.ordering.elementAt(i).equals(oid))
/*      */       {
/*  751 */         v.addElement(this.values.elementAt(i));
/*      */       }
/*      */     }
/*      */     
/*  755 */     return v;
/*      */   }
/*      */   
/*      */   public DERObject toASN1Object()
/*      */   {
/*  760 */     if (this.seq == null)
/*      */     {
/*  762 */       ASN1EncodableVector vec = new ASN1EncodableVector();
/*  763 */       ASN1EncodableVector sVec = new ASN1EncodableVector();
/*  764 */       DERObjectIdentifier lstOid = null;
/*      */       
/*  766 */       for (int i = 0; i != this.ordering.size(); i++)
/*      */       {
/*  768 */         ASN1EncodableVector v = new ASN1EncodableVector();
/*  769 */         DERObjectIdentifier oid = (DERObjectIdentifier)this.ordering.elementAt(i);
/*      */         
/*  771 */         v.add(oid);
/*      */         
/*  773 */         String str = (String)this.values.elementAt(i);
/*      */         
/*  775 */         v.add(this.converter.getConvertedValue(oid, str));
/*      */         
/*  777 */         if ((lstOid == null) || (((Boolean)this.added.elementAt(i)).booleanValue()))
/*      */         {
/*      */ 
/*  780 */           sVec.add(new DERSequence(v));
/*      */         }
/*      */         else
/*      */         {
/*  784 */           vec.add(new DERSet(sVec));
/*  785 */           sVec = new ASN1EncodableVector();
/*      */           
/*  787 */           sVec.add(new DERSequence(v));
/*      */         }
/*      */         
/*  790 */         lstOid = oid;
/*      */       }
/*      */       
/*  793 */       vec.add(new DERSet(sVec));
/*      */       
/*  795 */       this.seq = new DERSequence(vec);
/*      */     }
/*      */     
/*  798 */     return this.seq;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean equals(Object obj, boolean inOrder)
/*      */   {
/*  807 */     if (!inOrder)
/*      */     {
/*  809 */       return equals(obj);
/*      */     }
/*      */     
/*  812 */     if (obj == this)
/*      */     {
/*  814 */       return true;
/*      */     }
/*      */     
/*  817 */     if ((!(obj instanceof X509Name)) && (!(obj instanceof ASN1Sequence)))
/*      */     {
/*  819 */       return false;
/*      */     }
/*      */     
/*  822 */     DERObject derO = ((DEREncodable)obj).getDERObject();
/*      */     
/*  824 */     if (getDERObject().equals(derO))
/*      */     {
/*  826 */       return true;
/*      */     }
/*      */     
/*      */     X509Name other;
/*      */     
/*      */     try
/*      */     {
/*  833 */       other = getInstance(obj);
/*      */     }
/*      */     catch (IllegalArgumentException e)
/*      */     {
/*  837 */       return false;
/*      */     }
/*      */     
/*  840 */     int orderingSize = this.ordering.size();
/*      */     
/*  842 */     if (orderingSize != other.ordering.size())
/*      */     {
/*  844 */       return false;
/*      */     }
/*      */     
/*  847 */     for (int i = 0; i < orderingSize; i++)
/*      */     {
/*  849 */       DERObjectIdentifier oid = (DERObjectIdentifier)this.ordering.elementAt(i);
/*  850 */       DERObjectIdentifier oOid = (DERObjectIdentifier)other.ordering.elementAt(i);
/*      */       
/*  852 */       if (oid.equals(oOid))
/*      */       {
/*  854 */         String value = (String)this.values.elementAt(i);
/*  855 */         String oValue = (String)other.values.elementAt(i);
/*      */         
/*  857 */         if (!equivalentStrings(value, oValue))
/*      */         {
/*  859 */           return false;
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  864 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  868 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean equals(Object obj)
/*      */   {
/*  876 */     if (obj == this)
/*      */     {
/*  878 */       return true;
/*      */     }
/*      */     
/*  881 */     if ((!(obj instanceof X509Name)) && (!(obj instanceof ASN1Sequence)))
/*      */     {
/*  883 */       return false;
/*      */     }
/*      */     
/*  886 */     DERObject derO = ((DEREncodable)obj).getDERObject();
/*      */     
/*  888 */     if (getDERObject().equals(derO))
/*      */     {
/*  890 */       return true;
/*      */     }
/*      */     
/*      */     X509Name other;
/*      */     
/*      */     try
/*      */     {
/*  897 */       other = getInstance(obj);
/*      */     }
/*      */     catch (IllegalArgumentException e)
/*      */     {
/*  901 */       return false;
/*      */     }
/*      */     
/*  904 */     int orderingSize = this.ordering.size();
/*      */     
/*  906 */     if (orderingSize != other.ordering.size())
/*      */     {
/*  908 */       return false;
/*      */     }
/*      */     
/*  911 */     boolean[] indexes = new boolean[orderingSize];
/*      */     int delta;
/*      */     int start;
/*  914 */     int end; int delta; if (this.ordering.elementAt(0).equals(other.ordering.elementAt(0)))
/*      */     {
/*  916 */       int start = 0;
/*  917 */       int end = orderingSize;
/*  918 */       delta = 1;
/*      */     }
/*      */     else
/*      */     {
/*  922 */       start = orderingSize - 1;
/*  923 */       end = -1;
/*  924 */       delta = -1;
/*      */     }
/*      */     
/*  927 */     for (int i = start; i != end; i += delta)
/*      */     {
/*  929 */       boolean found = false;
/*  930 */       DERObjectIdentifier oid = (DERObjectIdentifier)this.ordering.elementAt(i);
/*  931 */       String value = (String)this.values.elementAt(i);
/*      */       
/*  933 */       for (int j = 0; j < orderingSize; j++)
/*      */       {
/*  935 */         if (indexes[j] == 0)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  940 */           DERObjectIdentifier oOid = (DERObjectIdentifier)other.ordering.elementAt(j);
/*      */           
/*  942 */           if (oid.equals(oOid))
/*      */           {
/*  944 */             String oValue = (String)other.values.elementAt(j);
/*      */             
/*  946 */             if (equivalentStrings(value, oValue))
/*      */             {
/*  948 */               indexes[j] = true;
/*  949 */               found = true;
/*  950 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  955 */       if (!found)
/*      */       {
/*  957 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  961 */     return true;
/*      */   }
/*      */   
/*      */   private boolean equivalentStrings(String s1, String s2)
/*      */   {
/*  966 */     String value = Strings.toLowerCase(s1.trim());
/*  967 */     String oValue = Strings.toLowerCase(s2.trim());
/*      */     
/*  969 */     if (!value.equals(oValue))
/*      */     {
/*  971 */       value = stripInternalSpaces(value);
/*  972 */       oValue = stripInternalSpaces(oValue);
/*      */       
/*  974 */       if (!value.equals(oValue))
/*      */       {
/*  976 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  980 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   private String stripInternalSpaces(String str)
/*      */   {
/*  986 */     StringBuilder res = new StringBuilder();
/*      */     
/*  988 */     if (str.length() != 0)
/*      */     {
/*  990 */       char c1 = str.charAt(0);
/*      */       
/*  992 */       res.append(c1);
/*      */       
/*  994 */       for (int k = 1; k < str.length(); k++)
/*      */       {
/*  996 */         char c2 = str.charAt(k);
/*  997 */         if ((c1 != ' ') || (c2 != ' '))
/*      */         {
/*  999 */           res.append(c2);
/*      */         }
/* 1001 */         c1 = c2;
/*      */       }
/*      */     }
/*      */     
/* 1005 */     return res.toString();
/*      */   }
/*      */   
/*      */   public int hashCode()
/*      */   {
/* 1010 */     ASN1Sequence seq = (ASN1Sequence)getDERObject();
/* 1011 */     Enumeration e = seq.getObjects();
/* 1012 */     int hashCode = 0;
/*      */     
/* 1014 */     while (e.hasMoreElements())
/*      */     {
/* 1016 */       hashCode ^= e.nextElement().hashCode();
/*      */     }
/*      */     
/* 1019 */     return hashCode;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void appendValue(StringBuffer buf, Hashtable oidSymbols, DERObjectIdentifier oid, String value)
/*      */   {
/* 1028 */     String sym = (String)oidSymbols.get(oid);
/*      */     
/* 1030 */     if (sym != null)
/*      */     {
/* 1032 */       buf.append(sym);
/*      */     }
/*      */     else
/*      */     {
/* 1036 */       buf.append(oid.getId());
/*      */     }
/*      */     
/* 1039 */     buf.append('=');
/*      */     
/* 1041 */     int index = buf.length();
/*      */     
/* 1043 */     buf.append(value);
/*      */     
/* 1045 */     int end = buf.length();
/*      */     
/* 1047 */     while (index != end)
/*      */     {
/* 1049 */       if ((buf.charAt(index) == ',') || (buf.charAt(index) == '"') || (buf.charAt(index) == '\\') || (buf.charAt(index) == '+') || (buf.charAt(index) == '<') || (buf.charAt(index) == '>') || (buf.charAt(index) == ';'))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1057 */         buf.insert(index, "\\");
/* 1058 */         index++;
/* 1059 */         end++;
/*      */       }
/*      */       
/* 1062 */       index++;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString(boolean reverse, Hashtable oidSymbols)
/*      */   {
/* 1082 */     StringBuilder buf = new StringBuilder();
/* 1083 */     Vector components = new Vector();
/* 1084 */     boolean first = true;
/*      */     
/* 1086 */     StringBuffer ava = null;
/*      */     
/* 1088 */     for (int i = 0; i < this.ordering.size(); i++)
/*      */     {
/* 1090 */       if (((Boolean)this.added.elementAt(i)).booleanValue())
/*      */       {
/* 1092 */         ava.append('+');
/* 1093 */         appendValue(ava, oidSymbols, (DERObjectIdentifier)this.ordering.elementAt(i), (String)this.values.elementAt(i));
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/* 1099 */         ava = new StringBuffer();
/* 1100 */         appendValue(ava, oidSymbols, (DERObjectIdentifier)this.ordering.elementAt(i), (String)this.values.elementAt(i));
/*      */         
/*      */ 
/* 1103 */         components.addElement(ava);
/*      */       }
/*      */     }
/*      */     
/* 1107 */     if (reverse)
/*      */     {
/* 1109 */       for (int i = components.size() - 1; i >= 0; i--)
/*      */       {
/* 1111 */         if (first)
/*      */         {
/* 1113 */           first = false;
/*      */         }
/*      */         else
/*      */         {
/* 1117 */           buf.append(',');
/*      */         }
/*      */         
/* 1120 */         buf.append(components.elementAt(i).toString());
/*      */       }
/*      */       
/*      */     }
/*      */     else {
/* 1125 */       for (int i = 0; i < components.size(); i++)
/*      */       {
/* 1127 */         if (first)
/*      */         {
/* 1129 */           first = false;
/*      */         }
/*      */         else
/*      */         {
/* 1133 */           buf.append(',');
/*      */         }
/*      */         
/* 1136 */         buf.append(components.elementAt(i).toString());
/*      */       }
/*      */     }
/*      */     
/* 1140 */     return buf.toString();
/*      */   }
/*      */   
/*      */ 
/*      */   private String bytesToString(byte[] data)
/*      */   {
/* 1146 */     char[] cs = new char[data.length];
/*      */     
/* 1148 */     for (int i = 0; i != cs.length; i++)
/*      */     {
/* 1150 */       cs[i] = ((char)(data[i] & 0xFF));
/*      */     }
/*      */     
/* 1153 */     return new String(cs);
/*      */   }
/*      */   
/*      */   public String toString()
/*      */   {
/* 1158 */     return toString(DefaultReverse, DefaultSymbols);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/X509Name.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */