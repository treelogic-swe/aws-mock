var AWS, ec2, params;

AWS = require('aws-sdk');

AWS.config.update({
    accessKeyId : 'foo',
    secretAccessKey : 'bar',
    region : 'baz'
});

ec2 = new AWS.EC2({
    endpoint : new AWS.Endpoint('http://localhost:9090/')
});

params = {
    ImageId : 'ami-12345678', // a pre-defined AMI in aws-mock
    InstanceType : 't1.micro',
    MinCount : 10,
    MaxCount : 10
};

ec2.runInstances(params, function(err, resp) {

    if (err) {
        console.log("Could not create instances", err);
    } else {
        console.log("Created instances:")
        
        resp.Instances.forEach(function(inst) {
            console.log(inst.InstanceId, inst.State.Name);
        });
    }
});
