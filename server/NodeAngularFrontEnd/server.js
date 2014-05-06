var express = require('express');
var path = require('path');
var app = express();

app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use('/bower_components', express.static(path.join(__dirname, 'bower_components')));
app.use(express.static(path.join(__dirname, 'public')));

// middleware
app.use(require('body-parser')()); // previously bodyParser, json and urlencoded
app.use(require('method-override')());
app.use(require('compression')());

if (process.env.NODE_ENV == 'development') {
  app.set('port', process.env.PORT || 4001);
  app.use(require('errorhandler')({ dumpExceptions: true, showStack: true }));
  app.use(require('morgan')()); // logger
} else {
  app.set('port', process.env.PORT || 3001);
  app.use(require('errorhandler')());
}

require('./routes')(app);
// console.log(app.routes);

app.listen(app.get('port'));
