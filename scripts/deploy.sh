WEBAPPS=/var/lib/tomcat7/webapps/
PROJECT_ROOT=`dirname $0`/..

echo "Deploying project $PROJECT_ROOT"
cd $PROJECT_ROOT/backend

echo "Running: mvn package war:war"
mvn package war:war > deploy.log

echo "Copying war files to tomcat"
cp target/*.war /var/lib/tomcat7/webapps/