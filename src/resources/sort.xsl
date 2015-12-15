<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     version="1.0">

  <xsl:template match="/">
    <xsl:copy>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="/keys">
    <xsl:copy>
      <xsl:apply-templates>
         <xsl:sort select="key/@user"/>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="/hosts">
    <xsl:copy>
      <xsl:apply-templates>
         <xsl:sort select="host/@name"/>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="/hosts/host">
    <xsl:copy>
      <xsl:apply-templates>
         <xsl:sort select="user/@name"/>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="/hosts/host/user">
    <xsl:copy>
      <xsl:apply-templates>
         <xsl:sort select="ssh-key/@user"/>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="@* | node()">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
