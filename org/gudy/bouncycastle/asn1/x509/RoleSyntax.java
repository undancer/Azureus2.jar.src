/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.DERString;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RoleSyntax
/*     */   extends ASN1Encodable
/*     */ {
/*     */   private GeneralNames roleAuthority;
/*     */   private GeneralName roleName;
/*     */   
/*     */   public static RoleSyntax getInstance(Object obj)
/*     */   {
/*  46 */     if ((obj == null) || ((obj instanceof RoleSyntax)))
/*     */     {
/*  48 */       return (RoleSyntax)obj;
/*     */     }
/*  50 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  52 */       return new RoleSyntax((ASN1Sequence)obj);
/*     */     }
/*  54 */     throw new IllegalArgumentException("Unknown object in RoleSyntax factory.");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RoleSyntax(GeneralNames roleAuthority, GeneralName roleName)
/*     */   {
/*  66 */     if ((roleName == null) || (roleName.getTagNo() != 6) || (((DERString)roleName.getName()).getString().equals("")))
/*     */     {
/*     */ 
/*     */ 
/*  70 */       throw new IllegalArgumentException("the role name MUST be non empty and MUST use the URI option of GeneralName");
/*     */     }
/*     */     
/*  73 */     this.roleAuthority = roleAuthority;
/*  74 */     this.roleName = roleName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RoleSyntax(GeneralName roleName)
/*     */   {
/*  85 */     this(null, roleName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RoleSyntax(String roleName)
/*     */   {
/*  97 */     this(new GeneralName(6, roleName == null ? "" : roleName));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RoleSyntax(ASN1Sequence seq)
/*     */   {
/* 111 */     if ((seq.size() < 1) || (seq.size() > 2))
/*     */     {
/* 113 */       throw new IllegalArgumentException("Bad sequence size: " + seq.size());
/*     */     }
/*     */     
/*     */ 
/* 117 */     for (int i = 0; i != seq.size(); i++)
/*     */     {
/* 119 */       ASN1TaggedObject taggedObject = ASN1TaggedObject.getInstance(seq.getObjectAt(i));
/* 120 */       switch (taggedObject.getTagNo())
/*     */       {
/*     */       case 0: 
/* 123 */         this.roleAuthority = GeneralNames.getInstance(taggedObject, false);
/* 124 */         break;
/*     */       case 1: 
/* 126 */         this.roleName = GeneralName.getInstance(taggedObject, false);
/* 127 */         break;
/*     */       default: 
/* 129 */         throw new IllegalArgumentException("Unknown tag in RoleSyntax");
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public GeneralNames getRoleAuthority()
/*     */   {
/* 141 */     return this.roleAuthority;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public GeneralName getRoleName()
/*     */   {
/* 151 */     return this.roleName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getRoleNameAsString()
/*     */   {
/* 161 */     DERString str = (DERString)this.roleName.getName();
/*     */     
/* 163 */     return str.getString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String[] getRoleAuthorityAsString()
/*     */   {
/* 173 */     if (this.roleAuthority == null)
/*     */     {
/* 175 */       return new String[0];
/*     */     }
/*     */     
/* 178 */     GeneralName[] names = this.roleAuthority.getNames();
/* 179 */     String[] namesString = new String[names.length];
/* 180 */     for (int i = 0; i < names.length; i++)
/*     */     {
/* 182 */       DEREncodable value = names[i].getName();
/* 183 */       if ((value instanceof DERString))
/*     */       {
/* 185 */         namesString[i] = ((DERString)value).getString();
/*     */       }
/*     */       else
/*     */       {
/* 189 */         namesString[i] = value.toString();
/*     */       }
/*     */     }
/* 192 */     return namesString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERObject toASN1Object()
/*     */   {
/* 208 */     ASN1EncodableVector v = new ASN1EncodableVector();
/* 209 */     if (this.roleAuthority != null)
/*     */     {
/* 211 */       v.add(new DERTaggedObject(false, 0, this.roleAuthority));
/*     */     }
/* 213 */     v.add(new DERTaggedObject(false, 1, this.roleName));
/*     */     
/* 215 */     return new DERSequence(v);
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 220 */     StringBuilder buff = new StringBuilder("Name: " + getRoleNameAsString() + " - Auth: ");
/*     */     
/* 222 */     if ((this.roleAuthority == null) || (this.roleAuthority.getNames().length == 0))
/*     */     {
/* 224 */       buff.append("N/A");
/*     */     }
/*     */     else
/*     */     {
/* 228 */       String[] names = getRoleAuthorityAsString();
/* 229 */       buff.append('[').append(names[0]);
/* 230 */       for (int i = 1; i < names.length; i++)
/*     */       {
/* 232 */         buff.append(", ").append(names[i]);
/*     */       }
/* 234 */       buff.append(']');
/*     */     }
/* 236 */     return buff.toString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/RoleSyntax.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */