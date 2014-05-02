var gulp = require('gulp');
var gutil = require('gulp-util');
var sass = require('gulp-ruby-sass'); // "better" than gulp-sass
var nodemon = require('gulp-nodemon');
var plumber = require('gulp-plumber'); // error handling
var autoprefixer = require('gulp-autoprefixer');

var onError = function (err) {
  gutil.beep();
  console.log(err);
};

// recompile sass files (two short beeps = success, anything else: probably
gulp.task('sass', function () {
  gulp.src('../client/public/css/*.scss')
    .pipe(plumber({ errorHandler: onError }))
    .pipe(sass({
      sourcemap: true,
      style: process.env.NODE_ENV === 'develop' ? 'expanded' : 'compressed'
    }))
    .pipe(autoprefixer("last 1 version", "> 1%", "ie 8", "ie 7"))
    .pipe(gulp.dest('../client/public/css'));
});

// watch stuff for changes
gulp.task('watch', function () {
  gulp.watch(['../client/public/css/*.scss'], ['sass']);
});

// start the server using nodemon (so it restarts if neccessary)
gulp.task('develop', function () {
  nodemon({
    script: 'server.js',
    verbose: true,
    ext: 'html js jade',
    env: { 'NODE_ENV': 'development', 'PORT': 3000 },
    ignore: ['Gulpfile.js', '.sass-cache/*']
  })
    .on('change', [])
    .on('restart', []);
});

// development mode: compile sass files, watch them for changes and start the
// server on port 4001
gulp.task('default', ['sass', 'watch', 'develop']);

// production mode: start the server on port 3001
gulp.task('production', []);