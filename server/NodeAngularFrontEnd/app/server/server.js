var express = require('express');
var path = require('path');
var app = express();

app.set('port', process.env.PORT || 3000);
app.set('views', path.join(__dirname, '/../client/views'));
app.set('view engine', 'jade');

app.use(express.favicon());
app.use(express.json());
app.use(express.urlencoded());
app.use(express.methodOverride());
app.use(express.compress());

app.use('/app', express.static(path.join(__dirname, '/../client/public')));
app.use('/lib', express.static(path.join(__dirname, '/../client/bower_components')));

app.use(app.router); // put below static!

if ('development' == app.get('env')) {
	app.use(express.logger('dev'));
	app.use(express.errorHandler());
}

require('./routes')(app);
// console.log(app.routes);

app.listen(app.get('port'));
