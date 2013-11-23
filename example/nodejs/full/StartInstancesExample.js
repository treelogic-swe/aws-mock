'use strict';

var AWS, ec2;

AWS = require('aws-sdk');

AWS.config.update({
    accessKeyId : 'foo',
    secretAccessKey : 'bar',
    region : 'baz'
});

ec2 = new AWS.EC2({
    endpoint : new AWS.Endpoint('http://localhost:9090/')
});

// start existing powered-off instances by given IDs passed from command line as arguments
ec2.startInstances({
    InstanceIds : process.argv
}, function handleResponse(err, resp) {

    if (err) {
        console.log("Could not start instance", err);
    } else {

        if (resp.StartingInstances.length > 0) {
            console.log("Instance state changes:")

            resp.StartingInstances.forEach(function printInstance(inst) {
                console.log(inst.InstanceId, inst.PreviousState.Name, '->', inst.CurrentState.Name);
            });
        } else {
            console.log('Nothing happened! Make sure you input the right instance IDs.');
            console.log('usage: node StartInstancesExample.js <instanceID-1> [instanceID-2] [instanceID-3] ...');
        }

    }
});
