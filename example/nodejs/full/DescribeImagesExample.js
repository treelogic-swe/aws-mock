'use strict';

exports.describeImages = function(ec2, fnCallback) {
    // describe all AMIs by passing no filter params
    ec2.describeImages({},
    function getImages(err, resp) {
        /*jshint unused:vars */
        fnCallback(resp.Images);
    });
};

