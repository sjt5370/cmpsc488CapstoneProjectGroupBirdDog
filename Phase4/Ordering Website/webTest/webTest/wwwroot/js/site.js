// Please see documentation at https://docs.microsoft.com/aspnet/core/client-side/bundling-and-minification
// for details on configuring this project to bundle and minify static web assets.

// Write your JavaScript code.

var mysql = require('mysql');

var connection = mysql.createConnection({
    host: mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com,
    user: masterUser,
    password: master1234,
    port: 1433
});

connection.connect(function (err) {
    if (err) {
        console.error('Database connection failed: ' + err.stack);
        return;
    }

    console.log('Connected to database.');
});

connection.end();