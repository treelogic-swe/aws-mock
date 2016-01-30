'use strict';

exports.describeImages = function(ec2, fnCallback) {
    // describe all AMIs by passing no filter params
    ec2.describeImages({},
    function getImages(err, resp) {
        /*jshint unused:vars */
        if(err){
            console.log(err);
        }else{
            fnCallback(resp.Images);
        }
    });
};

