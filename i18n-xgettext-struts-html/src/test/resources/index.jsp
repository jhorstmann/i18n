<%@ page pageEncoding="utf-8" contentType="text/html;charset=utf8"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<html>
    <head>
        <title><bean:message key="message.title"/></title>
    </head>
    <body>
        <html:img srcKey="image.test.src" altKey="image.test.alt" titleKey="image.test.title"/>
    </body>
</html>