'use strict';

var args = process.argv;

if (args) {

    var AWS, ec2;
    AWS = require('aws-sdk');
    AWS.config.update({
        accessKeyId: 'foo',
        secretAccessKey: 'bar',
        region: 'baz'
    });
    ec2 = new AWS.EC2({
        endpoint: new AWS.Endpoint('http://localhost:9090/')
    });

    var action = args[2];
    var instanceIDs = args.slice(3, args.length);

    switch (action) {
    case 'runInstances':
        var runInstancesExample = require('./simple/RunInstancesExample.js');
        runInstancesExample.runInstances(ec2, function getResult(instances) {
            console.log("Created instances:");
            instances.forEach(function printInstance(inst) {
                console.log(inst.InstanceId, inst.State.Name);
            });
        });
        break;

    case 'describeInstances':
        var describeInstancesExample = require('./simple/DescribeInstancesExample.js');
        describeInstancesExample.describeInstances(instanceIDs, ec2, function getResult(instances) {
            instances.forEach(function printInstances(inst) {
                console.log(inst.InstanceId, inst.ImageId, inst.InstanceType, inst.State.Name);
            });
        });
        break;

    case 'stopInstances':
        var stopInstancesExample = require('./full/StopInstancesExample.js');
        stopInstancesExample.stopInstances(instanceIDs, ec2, function getResult(stoppingInstances) {
            if (stoppingInstances.length > 0) {
                console.log("Instance state changes:");
                stoppingInstances.forEach(function printInstance(inst) {
                    console.log(inst.InstanceId, inst.PreviousState.Name, '->', inst.CurrentState.Name);
                });
            } else {
                console.log('Nothing happened! Make sure you input the right instance IDs.');
            }
        });
        break;

    case 'startInstances':
        var startInstancesExample = require('./full/StartInstancesExample.js');
        startInstancesExample.startInstances(instanceIDs, ec2, function getResult(startingInstances) {
            if (startingInstances.length > 0) {
                console.log("Instance state changes:");

                startingInstances.forEach(function printInstance(inst) {
                    console.log(inst.InstanceId, inst.PreviousState.Name, '->', inst.CurrentState.Name);
                });
            } else {
                console.log('Nothing happened! Make sure you input the right instance IDs.');
            }
        });
        break;

    case 'terminateInstances':
        var terminateInstancesExample = require('./full/TerminateInstancesExample.js');
        terminateInstancesExample.terminateInstances(instanceIDs, ec2, function getResult(terminatingInstances) {
            if (terminatingInstances.length > 0) {
                console.log("Instance state changes:");

                terminatingInstances.forEach(function printInstance(inst) {
                    console.log(inst.InstanceId, inst.PreviousState.Name, '->', inst.CurrentState.Name);
                });
            } else {
                console.log('Nothing happened! Make sure you input the right instance IDs.');
            }
        });
        break;

    case 'describeImages':
        var describeImagesExample = require('./full/DescribeImagesExample.js');
        describeImagesExample.describeImages(ec2, function(images) {
            images.forEach(function printAmis(image) {
                console.log(image.ImageId);
            });
        });
        break;

    default:
        console.log('usage: node RunExamples <runInstances|describeInstances|stopInstances|startInstances|terminateInstances|describeImages> [instanceID-1] [instanceID-2] ...');
    }
}

