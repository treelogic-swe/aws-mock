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

// describe all instances by passing no filter params
ec2.describeInstances({}, function(err, resp) {

    if (err) {
        console.log("Could not describe instances", err);
    } else {
        
        resp.Reservations.forEach(function(rsv) {
            var inst = rsv.Instances[0];
            console.log(inst.InstanceId, inst.ImageId, inst.InstanceType, inst.State.Name);
        });
    }
});
