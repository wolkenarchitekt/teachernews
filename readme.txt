 Teachernews
 ===========

 With the help of the application Teachernews, students can subscribe to newsfeeds and 
 be automatically informed of important events concerning specific teachers or 
 university departments.

 This application was written as part of my Diploma Thesis, in which i evaluated 
 Scala for implementing web applications on the Java EE 6 Platform.
 The following technologies are used by Teachernews:
  - Scala 2.8
  - Java Platform, Enterprise Edition 6 (JSR 316)  
  - JavaServer Faces 2.0 (JSR 314), [Mojarra] 
  - Java Persistence 2.0 (JSR 317), [Hibernate] 
  - Contexts and Dependency Injection for Java (Web Beans 1.0)(JSR 299), [Weld]
  - JavaMail 1.4 (JSR 196)
  - Bean Validation 1.0 (JSR 303)
  - PrimeFaces JSF component library 
  - Enterprise JavaBeans 3.1 (JSR 318)
 
 
 System requirements
 ===================

 To run this project you need Java 5.0 (Java SDK 1.5) or greater, Ant 1.8.0 or greater 
 and Maven 2.2.1 or greater. Teachernews runs on Glassfish >=V3.0.1. 
 Furthermore, set the environment variable $M2_REPO to point to your local Maven
 repository, in my case this is:
   C:\Users\ingo\.m2\repository
     
 If you already installed Glassfish and want to use that instance, you have to provide an 
 environment variable named $GLASSFISH that points to your Glassfish directory.
 If you experience PermGen Space errors, add the following environment variable:
   MAVEN_OPTS=-Xmx1024m -XX:MaxPermSize=1024m
 
 Installing the application
 ==========================
 
 At first you have to configure Teachernews by editing 
   teachernews.properties
  
 Don't forget to provide your E-Mail-provider, otherwise messages cannot be send by mail.
 The configuration of Teachernews is managed by an Ant script, the compilation and deployment
 is managed by Maven.
 There are two ways how to install the application:
 
 1) Use Embedded Glassfish
 
 If you just want to try the application without downloading or installing Glassfish, 
 you can use Embedded Glassfish. Teachernews then uses an in-memory database (not.
 To configure Teachernews to use the Embedded Glassfish, change the properties file or run:
   ant -Dglassfish.embedded=true
  
 Then you can build the application and deploy it on Embedded Glassfish:
   mvn package embedded-glassfish:run
  
 The application should then be available at the URL:
   http://localhost:7070/teachernews
  
 If you want to change settings in Embedded Glassfish that are not inside the 
 teachernews.properties, you have to change the file 
   embedded-glassfish\domain-template.xml 
 
 and re-run the Ant script. If you change the file domain.xml, your settings will be overwritten 
 the next time the Ant script is run (It replaces placeholders in domain-template.xml with the 
 teachernews.properties, overwriting domain.xml with the result).  
 
 
 2) Use your local Glassfish installation
 If you already installed Glassfish, You can deploy Teachernews on that instance. 
 First, set the environment variable $GLASSFISH to point to your Glassfish directory, 
 in my case it is: 
   C:\dev\glassfish-3.0.1\glassfish
 
 Make sure that the directory contains the folder "config".
 Now you can configure the application from the command line by just running:
   ant
 
 The application uses the Derby Database, which is already provided by Glassfish and 
 automatically started within the Ant script. If you want to use MySQL, change the 
 properties file or launch:
   ant -Dmysql=true
  
 You can change the database by running the Ant-script again. You also have to 
 re-run the Ant-script if you alter the teachernews.properties. 
 
 After running Ant, compile and deploy the application by running
   mvn package glassfish:deploy
    
 If the Maven build finished successfully, the application will be running at the following URL:
   http://localhost:8080/teachernews
 
 Admin
 =====
 
 You can set an Admin user inside the teachernews.properties. This user can change the roles
 of newly registered users. The Admin user is created automatically when deploying the application.
  
 Importing the project into Eclipse
 ==================================

 To import into Eclipse, you first need to install the m2eclipse plugin. To get
 started, add the m2eclipse update site (http://m2eclipse.sonatype.org/update/)
 to Eclipse and install the M2eclipse plugin and required dependencies. 
 To use M2Eclipse, Eclipse has to be launched with the JDK as the JVM.
 You can create a link for Eclipse with the following parameters:
 
   eclipse.exe -vm $JAVA_HOME\bin\javaw.exe" 
 
 Furthermore, you need the Scala Plugin. You can get the latest snapshot by adding
 the following update site to Eclipse:
   http://download.scala-ide.org/update-current
  
 Once that is installed, you'll be ready to import the project into Eclipse.
 Select File > Import... and select "Import... > Maven Projects" and select
 the teachernews root directory. m2eclipse should take it from there.
 Once in the IDE, you can execute the Maven commands through the IDE controls
 to run the application on an embedded Servlet Container.
 If you're using Eclipse 3.5 and want to start (and debug) Glassfish from within Eclipse, 
 i'd recommend updating the Eclipse WTP by adding the following update site:
   http://download.eclipse.org/webtools/updates/
  
 There, under "Web, XML, and Java EE Development", you can choose all plugins you need.
 You'll need the GlassfishV3 Server Adapter, available at the following update site 
 (you MUST update WTP tools first):
   https://ajax.dev.java.net/eclipse/
  
 Maven / Ant command overview
 ============================
 When Glassfish is already configured and you are using Apache Derby as database, 
 you have to start the database before deploying and running Teachernews. Do that
 by running
 
   asadmin start-database
 
 (the asadmin executable has to be in your PATH environment)
 The following Maven commands apply only when you have installed Glassfish.
 You have to run the Ant script one time before running the Maven commands, otherwise
 Glassfish is not configured properly.
 
 Start server:
   mvn glassfish:start-domain
  
 Deploy application:
   mvn glassfish:deploy
  
 Undeploy application:
   mvn glassfish:undeploy
  
 Stop server
   mvn glassfish:stop-domain
  
 Stop database
   asadmin stop-database
  
