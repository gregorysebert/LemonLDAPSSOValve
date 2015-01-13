LemonLDAPSSOValve
======================

A LemonLDAP sso valve for exoplatform.

Build
-----

Build it with

    mvn clean package


Deploy to eXo Platform
----------------------

Install [eXo Platform 4.1 Tomcat bundle](http://learn.exoplatform.com/Download-eXo-Platform-Express-Edition-En.html) to some directory, e.g. `/opt/platform-tomcat`.

Copy the LemonLDAPSSOValve-1.0-SNAPSHOT.jar in the `/opt/platform-tomcat/lib`


Configuration
-------------

Register the LemonLDAPSSOValve in the server.xml:
<Valve className="org.lemonLDAPNG.SSOValve" userKey="AUTH-USER" roleKey="AUTH-ROLE" roleSeparator="," allows="127.0.0.1"/>

You can find information about available configuration option from LemonLDAP website:
http://lemonldap-ng.org/documentation/1.0/applications/tomcat

Create a lemonldap virtual host pointing to eXo Platform server:
http://lemonldap-ng.org/documentation/1.0/configvhost

Configure the httpheader and set AUTH-USER=$uid and map AUTH-ROLE to eXo platform roles (users, administrators ...)


Run Platform
------------

Switch to a folder with your Platform and start it.

    cd /opt/platform-tomcat
    ./start_eXo.sh
    










