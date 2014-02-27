var express = require('express');
var app = express();

app.set('port', process.env.PORT || 3000);
app.set('views', ('../client/views'));
app.set('view engine', 'jade');
app.use(express.favicon());
app.use(express.json());
app.use(express.urlencoded());
app.use(express.methodOverride());
app.use(require('stylus').middleware('../client/public'));
app.use(express.static('../client/public'));
app.use(app.router); // put below static!

if ('development' == app.get('env')) {
	app.use(express.logger('dev'));
	app.use(express.errorHandler());
}

require('./routes')(app);
// console.log(app.routes);

app.listen(app.get('port'));
