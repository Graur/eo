<?xml version="1.0" encoding="UTF-8"?>
<!--
The MIT License (MIT)

Copyright (c) 2016-2023 Objectionary.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:eo="https://www.eolang.org" xmlns:g="http://www.w3.org/2001/03/XPath/grammar" id="to-ebnf" version="2.0">
  <xsl:output method="text" encoding="UTF-8"/>
  <xsl:function name="eo:escape" as="xs:string">
    <xsl:param name="s" as="xs:string"/>
    <xsl:variable name="r1" select='replace(replace(replace(replace(replace(replace($s, "\\", "\\textbackslash{}"), "&amp;", "\\&amp;"), " ", "\\textvisiblespace{}"), "\^", "\\^{}"), "\$", "\\textdollar"), "#", "\\#")'/>
    <xsl:value-of select="replace($r1, '&quot;', '\\lq\\lq')"/>
  </xsl:function>
  <xsl:template match="g:grammar">
    <xsl:element name="ebnf">
      <xsl:text>% Use native-enbf LaTeX package to render this</xsl:text>
      <xsl:text>&#x0A;</xsl:text>
      <xsl:apply-templates select="g:production"/>
    </xsl:element>
  </xsl:template>
  <xsl:template match="g:production">
    <xsl:value-of select="@name"/>
    <xsl:text> ::= </xsl:text>
    <xsl:apply-templates select="g:*"/>
    <xsl:text>&#x0A;</xsl:text>
  </xsl:template>
  <xsl:template match="g:optional">
    <xsl:text> [</xsl:text>
    <xsl:apply-templates select="g:*"/>
    <xsl:text>] </xsl:text>
  </xsl:template>
  <xsl:template match="g:zeroOrMore">
    <xsl:text> {</xsl:text>
    <xsl:apply-templates select="g:*"/>
    <xsl:text>} </xsl:text>
  </xsl:template>
  <xsl:template match="g:oneOrMore">
    <xsl:text> (</xsl:text>
    <xsl:apply-templates select="g:*"/>
    <xsl:text>) {</xsl:text>
    <xsl:apply-templates select="g:*"/>
    <xsl:text>} </xsl:text>
  </xsl:template>
  <xsl:template match="g:sequence">
    <xsl:text> (</xsl:text>
    <xsl:apply-templates select="g:*"/>
    <xsl:text>) </xsl:text>
  </xsl:template>
  <xsl:template match="g:choice">
    <xsl:for-each select="g:*">
      <xsl:if test="position() &gt; 1">
        <xsl:text> | </xsl:text>
      </xsl:if>
      <xsl:apply-templates select="."/>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="g:string">
    <xsl:text> "</xsl:text>
    <xsl:value-of select="eo:escape(text())"/>
    <xsl:text>" </xsl:text>
  </xsl:template>
  <xsl:template match="g:complement">
    <xsl:apply-templates select="g:*"/>
  </xsl:template>
  <xsl:template match="g:charClass">
    <xsl:text>[</xsl:text>
    <xsl:apply-templates select="g:*"/>
    <xsl:text>]</xsl:text>
  </xsl:template>
  <xsl:template match="g:charRange">
    <xsl:value-of select='@minChar'/>
    <xsl:text>-</xsl:text>
    <xsl:value-of select='@maxChar'/>
  </xsl:template>
  <xsl:template match="g:charCode">
    <xsl:text>\textbackslash{}x</xsl:text>
    <xsl:value-of select='@value'/>
  </xsl:template>
  <xsl:template match="g:char">
    <xsl:value-of select='eo:escape(text())'/>
  </xsl:template>
  <xsl:template match="g:ref">
    <xsl:text> </xsl:text>
    <xsl:choose>
      <xsl:when test='matches(@name, "^[A-Z].*")'>
        <xsl:text>'</xsl:text>
        <xsl:value-of select="eo:escape(@name)"/>
        <xsl:text>'</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text> &lt;</xsl:text>
        <xsl:value-of select="eo:escape(@name)"/>
        <xsl:text>&gt; </xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text> </xsl:text>
  </xsl:template>
</xsl:stylesheet>
