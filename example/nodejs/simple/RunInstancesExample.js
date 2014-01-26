'use strict';

exports.runInstances = function(imageID, type, count, ec2, fnCallback) {

    var params = {
        ImageId: imageID,
        InstanceType: type,
        MinCount: count,
        MaxCount: count
    };

    ec2.runInstances(params, function getInstances(err, resp) {
        /*jshint unused:vars */
        fnCallback(resp.Instances);
    });
};

