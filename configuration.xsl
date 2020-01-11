<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://www.esei.uvigo.es/dai/hybridserver">
	<xsl:output method="html" indent="yes"/>
	
	<xsl:template match="/">
		<html>
			<head>
				<title>Configuration</title>
			</head>
			<body>
				<div id="container">
					<h1>Configuration</h1>
					
					<h2>Connections</h2>
					<div id="connections">
						<xsl:apply-templates select="c:configuration/c:connections"/>
					</div>
					
					<h2>Database</h2>
					<div id="database">
						<xsl:apply-templates select="c:configuration/c:database"/>
					</div>
					
					<h2>Servers</h2>
					<div id="servers">
						<xsl:apply-templates select="c:configuration/c:servers"/>
					</div>		
				</div>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="c:connections">
		<p>HTTP: <xsl:value-of select="c:http"/></p>
		<p>WebService: <xsl:value-of select="c:webservice"/></p>
		<p>Numero maximo conexiones: <xsl:value-of select="c:numClients"/></p>
	</xsl:template>
	
	<xsl:template match="c:database">
		<p>Usuario: <xsl:value-of select="c:user"/></p>
		<p>Contraseña: <xsl:value-of select="c:password"/></p>
		<p>URL: <xsl:value-of select="c:url"/></p>
	</xsl:template>
	
	<xsl:template match="c:servers">
		<xsl:for-each select="c:server">
			<h3>Server: <xsl:value-of select="@name"/>. </h3>
			<p>WSDL: <xsl:value-of select="@wsdl"/>. </p>
			<p>NameSpace: <xsl:value-of select="@namespace"/>. </p> 
			<p>Service <xsl:value-of select="@service"/>. </p>
			<p>httpAddress <xsl:value-of select="@httpAddress"/>.</p>	
		</xsl:for-each>
	</xsl:template>
	
</xsl:stylesheet>