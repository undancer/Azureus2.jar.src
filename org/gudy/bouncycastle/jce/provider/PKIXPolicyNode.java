/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import java.security.cert.PolicyNode;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
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
/*     */ public class PKIXPolicyNode
/*     */   implements PolicyNode
/*     */ {
/*     */   protected List children;
/*     */   protected int depth;
/*     */   protected Set expectedPolicies;
/*     */   protected PolicyNode parent;
/*     */   protected Set policyQualifiers;
/*     */   protected String validPolicy;
/*     */   protected boolean critical;
/*     */   
/*     */   public PKIXPolicyNode(List _children, int _depth, Set _expectedPolicies, PolicyNode _parent, Set _policyQualifiers, String _validPolicy, boolean _critical)
/*     */   {
/*  38 */     this.children = _children;
/*  39 */     this.depth = _depth;
/*  40 */     this.expectedPolicies = _expectedPolicies;
/*  41 */     this.parent = _parent;
/*  42 */     this.policyQualifiers = _policyQualifiers;
/*  43 */     this.validPolicy = _validPolicy;
/*  44 */     this.critical = _critical;
/*     */   }
/*     */   
/*     */ 
/*     */   public void addChild(PKIXPolicyNode _child)
/*     */   {
/*  50 */     this.children.add(_child);
/*  51 */     _child.setParent(this);
/*     */   }
/*     */   
/*     */   public Iterator getChildren()
/*     */   {
/*  56 */     return this.children.iterator();
/*     */   }
/*     */   
/*     */   public int getDepth()
/*     */   {
/*  61 */     return this.depth;
/*     */   }
/*     */   
/*     */   public Set getExpectedPolicies()
/*     */   {
/*  66 */     return this.expectedPolicies;
/*     */   }
/*     */   
/*     */   public PolicyNode getParent()
/*     */   {
/*  71 */     return this.parent;
/*     */   }
/*     */   
/*     */   public Set getPolicyQualifiers()
/*     */   {
/*  76 */     return this.policyQualifiers;
/*     */   }
/*     */   
/*     */   public String getValidPolicy()
/*     */   {
/*  81 */     return this.validPolicy;
/*     */   }
/*     */   
/*     */   public boolean hasChildren()
/*     */   {
/*  86 */     return !this.children.isEmpty();
/*     */   }
/*     */   
/*     */   public boolean isCritical()
/*     */   {
/*  91 */     return this.critical;
/*     */   }
/*     */   
/*     */   public void removeChild(PKIXPolicyNode _child)
/*     */   {
/*  96 */     this.children.remove(_child);
/*     */   }
/*     */   
/*     */   public void setCritical(boolean _critical)
/*     */   {
/* 101 */     this.critical = _critical;
/*     */   }
/*     */   
/*     */   public void setParent(PKIXPolicyNode _parent)
/*     */   {
/* 106 */     this.parent = _parent;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 111 */     return toString("");
/*     */   }
/*     */   
/*     */   public String toString(String _indent)
/*     */   {
/* 116 */     StringBuilder _buf = new StringBuilder();
/* 117 */     _buf.append(_indent);
/* 118 */     _buf.append(this.validPolicy);
/* 119 */     _buf.append(" {\n");
/*     */     
/* 121 */     for (int i = 0; i < this.children.size(); i++) {
/* 122 */       _buf.append(((PKIXPolicyNode)this.children.get(i)).toString(_indent + "    "));
/*     */     }
/*     */     
/* 125 */     _buf.append(_indent);
/* 126 */     _buf.append("}\n");
/* 127 */     return _buf.toString();
/*     */   }
/*     */   
/*     */   public Object clone()
/*     */   {
/* 132 */     return copy();
/*     */   }
/*     */   
/*     */   public PKIXPolicyNode copy()
/*     */   {
/* 137 */     HashSet _expectedPolicies = new HashSet();
/* 138 */     Iterator _iter = this.expectedPolicies.iterator();
/* 139 */     while (_iter.hasNext())
/*     */     {
/* 141 */       _expectedPolicies.add(new String((String)_iter.next()));
/*     */     }
/*     */     
/* 144 */     HashSet _policyQualifiers = new HashSet();
/* 145 */     _iter = this.policyQualifiers.iterator();
/* 146 */     while (_iter.hasNext())
/*     */     {
/* 148 */       _policyQualifiers.add(new String((String)_iter.next()));
/*     */     }
/*     */     
/* 151 */     PKIXPolicyNode _node = new PKIXPolicyNode(new ArrayList(), this.depth, _expectedPolicies, null, _policyQualifiers, new String(this.validPolicy), this.critical);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 159 */     _iter = this.children.iterator();
/* 160 */     while (_iter.hasNext())
/*     */     {
/* 162 */       PKIXPolicyNode _child = ((PKIXPolicyNode)_iter.next()).copy();
/* 163 */       _child.setParent(_node);
/* 164 */       _node.addChild(_child);
/*     */     }
/*     */     
/* 167 */     return _node;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/PKIXPolicyNode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */