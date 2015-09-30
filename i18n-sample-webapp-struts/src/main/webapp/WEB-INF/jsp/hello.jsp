<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<!DOCTYPE html>
<html>
    <head>
        <title><bean:message key="message.helloworld"/></title>
    </head>
    <body>
        <h1><bean:message key="message.helloworld"/></h1>
        <html:form action="/hello.do" method="post">
            <bean:message key="label.name"/>
            <html:text property="name"/>
            <html:errors property="name"/>
        </html:form>
    </body>
</html>
