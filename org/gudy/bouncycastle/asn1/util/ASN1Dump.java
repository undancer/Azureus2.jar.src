/*     */ package org.gudy.bouncycastle.asn1.util;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*     */ 
/*     */ public class ASN1Dump
/*     */ {
/*  10 */   private static String TAB = "    ";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String _dumpAsString(String indent, DERObject obj)
/*     */   {
/*  21 */     if ((obj instanceof org.gudy.bouncycastle.asn1.ASN1Sequence))
/*     */     {
/*  23 */       StringBuilder buf = new StringBuilder();
/*  24 */       Enumeration e = ((org.gudy.bouncycastle.asn1.ASN1Sequence)obj).getObjects();
/*  25 */       String tab = indent + TAB;
/*     */       
/*  27 */       buf.append(indent);
/*  28 */       if ((obj instanceof org.gudy.bouncycastle.asn1.BERConstructedSequence))
/*     */       {
/*  30 */         buf.append("BER ConstructedSequence");
/*     */       }
/*  32 */       else if ((obj instanceof org.gudy.bouncycastle.asn1.DERConstructedSequence))
/*     */       {
/*  34 */         buf.append("DER ConstructedSequence");
/*     */       }
/*  36 */       else if ((obj instanceof org.gudy.bouncycastle.asn1.DERSequence))
/*     */       {
/*  38 */         buf.append("DER Sequence");
/*     */       }
/*  40 */       else if ((obj instanceof org.gudy.bouncycastle.asn1.BERSequence))
/*     */       {
/*  42 */         buf.append("BER Sequence");
/*     */       }
/*     */       else
/*     */       {
/*  46 */         buf.append("Sequence");
/*     */       }
/*     */       
/*  49 */       buf.append(System.getProperty("line.separator"));
/*     */       
/*  51 */       while (e.hasMoreElements())
/*     */       {
/*  53 */         Object o = e.nextElement();
/*     */         
/*  55 */         if ((o == null) || (o.equals(new org.gudy.bouncycastle.asn1.DERNull())))
/*     */         {
/*  57 */           buf.append(tab);
/*  58 */           buf.append("NULL");
/*  59 */           buf.append(System.getProperty("line.separator"));
/*     */         }
/*  61 */         else if ((o instanceof DERObject))
/*     */         {
/*  63 */           buf.append(_dumpAsString(tab, (DERObject)o));
/*     */         }
/*     */         else
/*     */         {
/*  67 */           buf.append(_dumpAsString(tab, ((DEREncodable)o).getDERObject()));
/*     */         }
/*     */       }
/*  70 */       return buf.toString();
/*     */     }
/*  72 */     if ((obj instanceof DERTaggedObject))
/*     */     {
/*  74 */       StringBuilder buf = new StringBuilder();
/*  75 */       String tab = indent + TAB;
/*     */       
/*  77 */       buf.append(indent);
/*  78 */       if ((obj instanceof org.gudy.bouncycastle.asn1.BERTaggedObject))
/*     */       {
/*  80 */         buf.append("BER Tagged [");
/*     */       }
/*     */       else
/*     */       {
/*  84 */         buf.append("Tagged [");
/*     */       }
/*     */       
/*  87 */       DERTaggedObject o = (DERTaggedObject)obj;
/*     */       
/*  89 */       buf.append(Integer.toString(o.getTagNo()));
/*  90 */       buf.append("]");
/*     */       
/*  92 */       if (!o.isExplicit())
/*     */       {
/*  94 */         buf.append(" IMPLICIT ");
/*     */       }
/*     */       
/*  97 */       buf.append(System.getProperty("line.separator"));
/*     */       
/*  99 */       if (o.isEmpty())
/*     */       {
/* 101 */         buf.append(tab);
/* 102 */         buf.append("EMPTY");
/* 103 */         buf.append(System.getProperty("line.separator"));
/*     */       }
/*     */       else
/*     */       {
/* 107 */         buf.append(_dumpAsString(tab, o.getObject()));
/*     */       }
/*     */       
/* 110 */       return buf.toString();
/*     */     }
/* 112 */     if ((obj instanceof org.gudy.bouncycastle.asn1.DERConstructedSet))
/*     */     {
/* 114 */       StringBuilder buf = new StringBuilder();
/* 115 */       Enumeration e = ((org.gudy.bouncycastle.asn1.ASN1Set)obj).getObjects();
/* 116 */       String tab = indent + TAB;
/*     */       
/* 118 */       buf.append(indent);
/* 119 */       buf.append("ConstructedSet");
/* 120 */       buf.append(System.getProperty("line.separator"));
/*     */       
/* 122 */       while (e.hasMoreElements())
/*     */       {
/* 124 */         Object o = e.nextElement();
/*     */         
/* 126 */         if (o == null)
/*     */         {
/* 128 */           buf.append(tab);
/* 129 */           buf.append("NULL");
/* 130 */           buf.append(System.getProperty("line.separator"));
/*     */         }
/* 132 */         else if ((o instanceof DERObject))
/*     */         {
/* 134 */           buf.append(_dumpAsString(tab, (DERObject)o));
/*     */         }
/*     */         else
/*     */         {
/* 138 */           buf.append(_dumpAsString(tab, ((DEREncodable)o).getDERObject()));
/*     */         }
/*     */       }
/* 141 */       return buf.toString();
/*     */     }
/* 143 */     if ((obj instanceof org.gudy.bouncycastle.asn1.BERSet))
/*     */     {
/* 145 */       StringBuilder buf = new StringBuilder();
/* 146 */       Enumeration e = ((org.gudy.bouncycastle.asn1.ASN1Set)obj).getObjects();
/* 147 */       String tab = indent + TAB;
/*     */       
/* 149 */       buf.append(indent);
/* 150 */       buf.append("BER Set");
/* 151 */       buf.append(System.getProperty("line.separator"));
/*     */       
/* 153 */       while (e.hasMoreElements())
/*     */       {
/* 155 */         Object o = e.nextElement();
/*     */         
/* 157 */         if (o == null)
/*     */         {
/* 159 */           buf.append(tab);
/* 160 */           buf.append("NULL");
/* 161 */           buf.append(System.getProperty("line.separator"));
/*     */         }
/* 163 */         else if ((o instanceof DERObject))
/*     */         {
/* 165 */           buf.append(_dumpAsString(tab, (DERObject)o));
/*     */         }
/*     */         else
/*     */         {
/* 169 */           buf.append(_dumpAsString(tab, ((DEREncodable)o).getDERObject()));
/*     */         }
/*     */       }
/* 172 */       return buf.toString();
/*     */     }
/* 174 */     if ((obj instanceof org.gudy.bouncycastle.asn1.DERSet))
/*     */     {
/* 176 */       StringBuilder buf = new StringBuilder();
/* 177 */       Enumeration e = ((org.gudy.bouncycastle.asn1.ASN1Set)obj).getObjects();
/* 178 */       String tab = indent + TAB;
/*     */       
/* 180 */       buf.append(indent);
/* 181 */       buf.append("DER Set");
/* 182 */       buf.append(System.getProperty("line.separator"));
/*     */       
/* 184 */       while (e.hasMoreElements())
/*     */       {
/* 186 */         Object o = e.nextElement();
/*     */         
/* 188 */         if (o == null)
/*     */         {
/* 190 */           buf.append(tab);
/* 191 */           buf.append("NULL");
/* 192 */           buf.append(System.getProperty("line.separator"));
/*     */         }
/* 194 */         else if ((o instanceof DERObject))
/*     */         {
/* 196 */           buf.append(_dumpAsString(tab, (DERObject)o));
/*     */         }
/*     */         else
/*     */         {
/* 200 */           buf.append(_dumpAsString(tab, ((DEREncodable)o).getDERObject()));
/*     */         }
/*     */       }
/* 203 */       return buf.toString();
/*     */     }
/* 205 */     if ((obj instanceof org.gudy.bouncycastle.asn1.DERObjectIdentifier))
/*     */     {
/* 207 */       return indent + "ObjectIdentifier(" + ((org.gudy.bouncycastle.asn1.DERObjectIdentifier)obj).getId() + ")" + System.getProperty("line.separator");
/*     */     }
/* 209 */     if ((obj instanceof org.gudy.bouncycastle.asn1.DERBoolean))
/*     */     {
/* 211 */       return indent + "Boolean(" + ((org.gudy.bouncycastle.asn1.DERBoolean)obj).isTrue() + ")" + System.getProperty("line.separator");
/*     */     }
/* 213 */     if ((obj instanceof org.gudy.bouncycastle.asn1.DERInteger))
/*     */     {
/* 215 */       return indent + "Integer(" + ((org.gudy.bouncycastle.asn1.DERInteger)obj).getValue() + ")" + System.getProperty("line.separator");
/*     */     }
/* 217 */     if ((obj instanceof org.gudy.bouncycastle.asn1.DEROctetString))
/*     */     {
/* 219 */       return indent + obj.toString() + "[" + ((org.gudy.bouncycastle.asn1.ASN1OctetString)obj).getOctets().length + "] " + System.getProperty("line.separator");
/*     */     }
/* 221 */     if ((obj instanceof org.gudy.bouncycastle.asn1.DERIA5String))
/*     */     {
/* 223 */       return indent + "IA5String(" + ((org.gudy.bouncycastle.asn1.DERIA5String)obj).getString() + ") " + System.getProperty("line.separator");
/*     */     }
/* 225 */     if ((obj instanceof org.gudy.bouncycastle.asn1.DERPrintableString))
/*     */     {
/* 227 */       return indent + "PrintableString(" + ((org.gudy.bouncycastle.asn1.DERPrintableString)obj).getString() + ") " + System.getProperty("line.separator");
/*     */     }
/* 229 */     if ((obj instanceof org.gudy.bouncycastle.asn1.DERVisibleString))
/*     */     {
/* 231 */       return indent + "VisibleString(" + ((org.gudy.bouncycastle.asn1.DERVisibleString)obj).getString() + ") " + System.getProperty("line.separator");
/*     */     }
/* 233 */     if ((obj instanceof org.gudy.bouncycastle.asn1.DERBMPString))
/*     */     {
/* 235 */       return indent + "BMPString(" + ((org.gudy.bouncycastle.asn1.DERBMPString)obj).getString() + ") " + System.getProperty("line.separator");
/*     */     }
/* 237 */     if ((obj instanceof org.gudy.bouncycastle.asn1.DERT61String))
/*     */     {
/* 239 */       return indent + "T61String(" + ((org.gudy.bouncycastle.asn1.DERT61String)obj).getString() + ") " + System.getProperty("line.separator");
/*     */     }
/* 241 */     if ((obj instanceof org.gudy.bouncycastle.asn1.DERUTCTime))
/*     */     {
/* 243 */       return indent + "UTCTime(" + ((org.gudy.bouncycastle.asn1.DERUTCTime)obj).getTime() + ") " + System.getProperty("line.separator");
/*     */     }
/* 245 */     if ((obj instanceof org.gudy.bouncycastle.asn1.DERUnknownTag))
/*     */     {
/* 247 */       return indent + "Unknown " + Integer.toString(((org.gudy.bouncycastle.asn1.DERUnknownTag)obj).getTag(), 16) + " " + new String(org.gudy.bouncycastle.util.encoders.Hex.encode(((org.gudy.bouncycastle.asn1.DERUnknownTag)obj).getData())) + System.getProperty("line.separator");
/*     */     }
/*     */     
/*     */ 
/* 251 */     return indent + obj.toString() + System.getProperty("line.separator");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String dumpAsString(Object obj)
/*     */   {
/* 263 */     if ((obj instanceof DERObject))
/*     */     {
/* 265 */       return _dumpAsString("", (DERObject)obj);
/*     */     }
/* 267 */     if ((obj instanceof DEREncodable))
/*     */     {
/* 269 */       return _dumpAsString("", ((DEREncodable)obj).getDERObject());
/*     */     }
/*     */     
/* 272 */     return "unknown object type " + obj.toString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/util/ASN1Dump.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */