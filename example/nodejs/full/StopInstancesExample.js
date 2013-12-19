'use strict';

exports.stopInstances = function(instanceIDs, ec2, fnCallback) {
    // stop running instances by given IDs
    ec2.stopInstances({
        InstanceIds: instanceIDs
    },
    function handleResponse(err, resp) {

        if (err) {
            console.log("Could not stop instances", err);
        } else {
            if (fnCallback) {
                fnCallback(resp.StoppingInstances);
            }
        }
    });
};

