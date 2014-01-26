'use strict';

exports.describeInstances = function(instanceIDs, ec2, fnCallback) {
    // describe specified instances by passing filter params, or all instances if no filter
    ec2.describeInstances(instanceIDs === null || instanceIDs.length === 0 ? {}: {
        InstanceIds: instanceIDs
    },
    function getReservations(err, resp) {
        /*jshint unused:vars */
        var instances = [];
        resp.Reservations.forEach(function printInstances(rsv) {
            instances.push(rsv.Instances[0]);
        });
        fnCallback(instances);
    });
};

