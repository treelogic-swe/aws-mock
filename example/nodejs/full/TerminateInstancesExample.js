'use strict';

exports.terminateInstances = function(instanceIDs, ec2, fnCallback) {
    // terminate instances by given IDs
    ec2.terminateInstances({
        InstanceIds: instanceIDs
    },
    function handleResponse(err, resp) {

        if (err) {
            console.log("Could not terminate instances", err);
        } else {
            if (fnCallback) {
                fnCallback(resp.TerminatingInstances);
            }
        }
    });

};

