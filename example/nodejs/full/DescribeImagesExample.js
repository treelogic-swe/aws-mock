var AWS, ec2;

AWS = require('aws-sdk');

AWS.config.update({
    accessKeyId : 'foo',
    secretAccessKey : 'bar',
    region : 'baz'
});

ec2 = new AWS.EC2({
    endpoint : new AWS.Endpoint('http://localhost:9090/')
});

// describe all AMIs by passing no filter params
ec2.describeImages({}, function(err, resp) {

    if (err) {
        console.log("Could not describe images", err);
    } else {
        
        resp.Images.forEach(function(image) {
            console.log(image.ImageId);
        });
    }
});
