'use strict';

exports.startInstances = function(instanceIDs, ec2, fnCallback) {
    // start existing powered-off instances by given IDs
    ec2.startInstances({
        InstanceIds: instanceIDs
    },
    function handleResponse(err, resp) {

        if (err) {
            console.log("Could not start instance", err);
        } else {
            if (fnCallback) {
                fnCallback(resp.StartingInstances);
            }
        }
    });
};

