var express = require('express');
var path = require('path');
var app = express();

app.set('port', process.env.PORT || 3000);
app.set('views', path.join(__dirname, '/../client/views'));
app.set('view engine', 'jade');

app.use('/app', express.static(path.join(__dirname, '/../client/public')));
app.use('/lib', express.static(path.join(__dirname, '/../client/bower_components')));

// middleware
app.use(require('body-parser')()); // previously bodyParser, json and urlencoded
app.use(require('method-override')());
app.use(require('compression')());

if ('development' == app.get('env')) {
  app.use(require('morgan')()); // logger
  app.use(require('errorhandler')());
}

require('./routes')(app);
// console.log(app.routes);

app.listen(app.get('port'));
