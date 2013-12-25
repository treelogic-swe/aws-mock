'use strict';

exports.describeImages = function(ec2, fnCallback) {
    // describe all AMIs by passing no filter params
    ec2.describeImages({},
    function getImages(err, resp) {

        if (err) {
            console.log("Could not describe images", err);
        } else {
            if (fnCallback) {
                fnCallback(resp.Images);
            }
        }
    });
};

