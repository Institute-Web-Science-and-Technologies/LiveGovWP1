Live+Gov Inspection Front End
=============================

Initial Setup
-------------

1. Go to app/server and run `npm install` to install required node modules. By default development modules are installed as well. If you want the front end to run in production mode, you can run `npm install --production` to prevent installing development dependencies.

2. Run `gulp` to start the server in development mode on port 4001 including some development services, e.g. watching SASS stylesheets for changes and recompiling them. Alternatively you can run `gulp production` to only start the server on port 3001.

3. Go to app/client and run `bower install` to install the front end dependencies.

4. The front end should be accessible on the appropiate port.

Directory structure
-------------------

- **app/server**: The Node.js back end. (The back end of the front end.) It handles database access and provides the API.

-- **server.js**: The Node.js/Express server.

-- **database.js**: Handling database requests.

-- **routes.js**: API configuration.

- **app/client**: The Angular front end. Bower components are installed in *lib* and Jade templates reside in *views*. The actual statically served files for the browser side are in *public*. Therein, *js* contains the Angular project files.

**NOTE** This structure is going to be simplified in near future.

**NOTE** Undocumented features are probably not ready, yet.



