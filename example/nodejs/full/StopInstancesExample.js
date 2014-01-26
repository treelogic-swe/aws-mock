'use strict';

exports.stopInstances = function(instanceIDs, ec2, fnCallback) {
    // stop running instances by given IDs
    ec2.stopInstances({
        InstanceIds: instanceIDs
    },
    function getStoppingInstances(err, resp) {
        /*jshint unused:vars */
        fnCallback(resp.StoppingInstances);
    });
};

