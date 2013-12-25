/*
This script runs a reverse proxy on http://localhost:9090/ to the actual backend http://localhost:8000/aws-mock/ec2-endpoint/
*/

'use strict';

var httpProxy = require('http-proxy'),
options = {
    router: {
        'localhost/': 'http://localhost:8000/aws-mock/ec2-endpoint/'
    }
},
proxyServer = httpProxy.createServer(options);

proxyServer.listen(9090);
console.log("Proxy running on http://localhost:9090/");

