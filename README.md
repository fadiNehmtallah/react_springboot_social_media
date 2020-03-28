# react_springboot_social_media

CLIENT-SIDE:

1:install all the dependencies listed in the client/social-media-app/package.json file
(Go to cmd in social-media-app and write "install dependencies")

2.Open cmd in "social-media-app" and write "npm start" to start the client server


SERVER-SIDE:

1.Create a MongoDB database

2.In "src/main/resources/application.properties" change "mongodb.host,mongodb.port,mongodb.database" to your corresponding ones

3.Install "Web Server for Chrome"

4.choose folder to download user images in this folder

5.Go to "server\src\main\java\com\example\mongodb\controller\UserController.java"

In line 300 change the path to the path chosen for your "web server for chrome"
In line 311 change the port number in image url to the corresponding port in "web server for chrome"

6.finally insert "no-image.png"(located in server) in your chosen folder in "web server for chrome"

7.Open cmd in server folder and write "mvn spring-boot:run" to boot your backend server

Finally enjoy it!!!!!!!!!!!!
