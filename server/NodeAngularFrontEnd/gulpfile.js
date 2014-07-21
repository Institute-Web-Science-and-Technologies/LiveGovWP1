(function() {
  'use strict';

   var gulp    = require('gulp'),
       util    = require('gulp-util'),
       react   = require('gulp-react'),
       concat  = require('gulp-concat'),
       plumber = require('gulp-plumber'),
       nodemon = require('gulp-nodemon'),
       prefix  = require('gulp-autoprefixer'),
       sass    = require('gulp-ruby-sass');

  var onError = function(err) {
    util.beep();
    console.log(err);
  };

  gulp.task('react', function() {
    gulp.src('src/jsx/**/*.jsx')
      .pipe(plumber({errorHandler:onError}))
      .pipe(react({harmony:true, noCacheDir:false}))
      .pipe(concat('components.js'))
      .pipe(gulp.dest('public/js'));
  });

  gulp.task('sass', function() {
    gulp.src('src/scss/*.scss')
      .pipe(plumber({errorHandler:onError}))
      .pipe(sass({sourcemap:false, style:'compressed'}))
      .pipe(prefix(["last 1 version", "> 1%", "ie 8", "ie 7"], { cascade: true }))
      .pipe(gulp.dest('public/css'));
  });

  gulp.task('server', function() {
    nodemon({
      script: 'server.js',
      watch: 'server.js',
      nodeArgs: ['--harmony']
    })
    .on('change', []);
  });

  gulp.task('watch', function() {
    gulp.watch('src/jsx/**/*.jsx', ['react']);
    gulp.watch('src/scss/**/*.scss', ['sass']);
  });

  gulp.task('default', ['react', 'sass', 'server', 'watch']);
}());
