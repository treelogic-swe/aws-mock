module.exports = function(grunt) {
    "use strict";
    grunt.initConfig({
        pkg: grunt.file.readJSON("package.json"),
        jshint: {
            all: {
                src: ["src/**/*.js", "example/**/*.js", "Gruntfile.js"],
                options: {
                    jshintrc: true
                }
            }
        }
    });
    grunt.loadNpmTasks('grunt-contrib-jshint');
};

