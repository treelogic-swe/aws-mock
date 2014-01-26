/* jshint expr: true */
/* global describe: true, it: true */
'use strict';

var expect = require("chai").expect,
describeImagesExample = require('../full/DescribeImagesExample.js'),
runInstancesExample = require('../simple/RunInstancesExample.js'),
describeInstancesExample = require('../simple/DescribeInstancesExample.js'),
startInstancesExample = require('../full/StartInstancesExample.js'),
stopInstancesExample = require('../full/StopInstancesExample.js'),
terminateInstancesExample = require('../full/TerminateInstancesExample.js'),
AWS,
ec2,
maxBootSeconds = 30,
maxShutdownSeconds = 20,
exampleImageID = null,
instanceType = 'm1.small',
runCount = 10,
exampleInstanceIDs = [],
exampleInstanceID = null,
A_SECOND_IN_MILLSECOND = 1000,
FIVE_SECONDS_IN_MILLSECOND = 5000;

AWS = require('aws-sdk');
AWS.config.update({
    accessKeyId: 'foo',
    secretAccessKey: 'bar',
    region: 'baz'
});
ec2 = new AWS.EC2({
    endpoint: new AWS.Endpoint('http://localhost:9090/')
});

describe('Test Examples -> ', function() {

    describe('Describe Images test ->', function() {
        it('should return at least one pre-defined images', function(done) {
            describeImagesExample.describeImages(ec2, function getImages(images) {
                expect(images).to.have.length.above(0);
                // pick the first imageID for running the new instances
                exampleImageID = images[0].ImageId;
                done();
            });
        });
    });

    describe('Run Instances test -> ', function() {
        it('should start ' + runCount + ' new pending instances successfully', function(done) {
            runInstancesExample.runInstances(exampleImageID, instanceType, runCount, ec2, function getInstances(instances) {
                expect(instances).to.have.length(runCount);
                instances.forEach(function(inst) {
                    expect(inst.State.Name).to.equal('pending');
                    exampleInstanceIDs.push(inst.InstanceId);
                });
                // pick one of the instanceID for the subsequent tests (stop/start/terminate)
                exampleInstanceID = exampleInstanceIDs[exampleInstanceIDs.length - 1];
                done();
            });
        });

        var delayTime = getDelayTime(maxBootSeconds);
        it('should find all the ' + runCount + ' instances turned into running after ' + delayTime + ' millsecs', function(done) {
            // explicitly alter the timeout to maxBootSeconds+1 seconds to make sure the test can wait until instances turned into stopped
            this.timeout(delayTime + FIVE_SECONDS_IN_MILLSECOND);
            // wait maxBootSeconds*2 seconds for instances turning into running and describe and assert again
            setTimeout(function() {
                describeInstancesExample.describeInstances(exampleInstanceIDs, ec2, function getInstances(instances) {
                    expect(instances).to.have.length(runCount);
                    instances.forEach(function(inst) {
                        expect(inst.State.Name).to.equal('running');
                    });
                    done();
                });
            },
            delayTime);
        });
    });

    describe('Stop Instance Test -> ', function() {
        it('should stop one picked instance successfully', function(done) {
            stopInstancesExample.stopInstances([exampleInstanceID], ec2, function getInstances(instances) {
                expect(instances).to.have.length(1);
                instances.forEach(function(inst) {
                    expect(inst.PreviousState.Name).to.equal('running');
                    expect(inst.CurrentState.Name).to.equal('stopping');
                });
                done();
            });
        });

        var delayTime = getDelayTime(maxShutdownSeconds);
        it('should find that instance turned into stopped after ' + delayTime + ' millsecs', function(done) {
            // explicitly alter the timeout to maxShutdownSeconds*2+1 seconds to make sure the test can wait until instances turned into stopped
            this.timeout(delayTime + FIVE_SECONDS_IN_MILLSECOND);
            // wait maxShutdownSeconds*2 seconds for instances turning into stopped and describe and assert again
            setTimeout(function() {
                describeInstancesExample.describeInstances([exampleInstanceID], ec2, function getInstances(instances) {
                    expect(instances).to.have.length(1);
                    instances.forEach(function(inst) {
                        expect(inst.State.Name).to.equal('stopped');
                    });
                    done();
                });
            },
            delayTime);
        });
    });

    describe('Start Instance Test -> ', function() {
        it('should start that instance again successfully', function(done) {
            startInstancesExample.startInstances([exampleInstanceID], ec2, function getInstances(instances) {
                expect(instances).to.have.length(1);
                instances.forEach(function(inst) {
                    expect(inst.PreviousState.Name).to.equal('stopped');
                    expect(inst.CurrentState.Name).to.equal('pending');
                });
                done();
            });
        });

        var delayTime = getDelayTime(maxBootSeconds);
        it('should find that instance turned into running again after ' + delayTime + ' millsecs', function(done) {
            // explicitly alter the timeout to maxBootSeconds*2+1 seconds to make sure the test can wait until instances turned into running
            this.timeout(delayTime + FIVE_SECONDS_IN_MILLSECOND);
            // wait maxBootSeconds*2 seconds for instances turning into running and describe and assert again
            setTimeout(function() {
                describeInstancesExample.describeInstances([exampleInstanceID], ec2, function(instances) {
                    expect(instances).to.have.length(1);
                    instances.forEach(function(inst) {
                        expect(inst.State.Name).to.equal('running');
                    });
                    done();
                });
            },
            delayTime);
        });
    });

    describe('Terminate Instances Test -> ', function() {
        it('should terminate all the ' + runCount + ' instances successfully', function(done) {
            terminateInstancesExample.terminateInstances(exampleInstanceIDs, ec2, function getInstances(instances) {
                expect(instances).to.have.length(runCount);
                instances.forEach(function(inst) {
                    expect(inst.CurrentState.Name).to.equal('terminated');
                });
                done();
            });
        });
    });

    describe('Describe No Instances Test -> ', function() {
        it('should return nothing', function(done) {
            describeInstancesExample.describeInstances(null, ec2, function getInstances(instances) {
                expect(instances).to.have.length.of.at.least(runCount);
                done();
            });

        });
    });
});

/**
Return (seconds*1.5) in millseconds.
*/
function getDelayTime(seconds) {
    return seconds * 1.5 * A_SECOND_IN_MILLSECOND;
}

