// jshint esnext:true

module.exports = function(routes) {
  routes
    .get('index',    '/',                    renderIndex)
    .get('template', '/templates/:template', renderTemplate);

  function *renderIndex(next) {
    try {
      yield this.render('index');
    } catch (err) {
      this.status = err.status || 404; // requested resource could not be found
      this.body = err.message;
      this.app.emit('error', err, this);
      return;
    }
  }

  function *renderTemplate(next) {
    try {
      yield this.render('templates/' + this.params.template);
    } catch (err) {
      this.status = err.status || 404; // requested resource could not be found
      this.body = err.message;
      this.app.emit('error', err, this);
    }
  }
};
