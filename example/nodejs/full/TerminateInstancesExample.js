'use strict';

exports.terminateInstances = function(instanceIDs, ec2, fnCallback) {
    // terminate instances by given IDs
    ec2.terminateInstances({
        InstanceIds: instanceIDs
    },
    function getTerminatingInstances(err, resp) {
        /*jshint unused:vars */
        if(err){
            console.log(err);
        }else{
            fnCallback(resp.TerminatingInstances);
        }
    });

};

