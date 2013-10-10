WEBAPPS=/var/lib/tomcat7/webapps/

BRANCH="dev"
if [ -n "$1" ] 
then
    BRANCH="$1";
fi


SCRIPT_FOLDER=`dirname $0`
PROJECT_ROOT="$SCRIPT_FOLDER/../backend"
echo "Deploying $PROJECT_ROOT"
cd $PROJECT_ROOT
echo `pwd`

echo "Pulling branch $BRANCH from github.com"
git pull origin $BRANCH

echo "Running: mvn package war:war"
mvn package war:war > deploy.log

echo "Copying war files to tomcat"
cp target/*.war /var/lib/tomcat7/webapps/