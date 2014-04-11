var gulp = require('gulp');
var gutil = require('gulp-util');
var concat = require('gulp-concat');
var sass = require('gulp-ruby-sass');
var nodemon = require('gulp-nodemon');
var plumber = require('gulp-plumber');
var autoprefixer = require('gulp-autoprefixer');

var onError = function (err) {
  gutil.beep();
  console.log(err);
};

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

gulp.task('watch', function () {
  gulp.watch(['../client/public/css/*.scss'], ['sass']);
});

gulp.task('develop', function () {
  nodemon({
    script: 'server.js',
    verbose: true,
    ext: 'html js jade',
    env: { 'NODE_ENV': 'development', 'PORT': 3000 },
    ignore: ['Gulpfile.js', '.sass-cache/*'],
    watch: ['../../app']
  })
    .on('change', [])
    .on('restart', []);
});

gulp.task('default', ['sass', 'watch', 'develop']);