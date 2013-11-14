var AWS, ec2;

AWS = require('aws-sdk');

AWS.config.update({
	accessKeyId : 'foo',
	secretAccessKey : 'bar',
	region : 'baz'
});

/*-
 Endpoint for mock ec2 should be running on 'root context' as
 Amazon's node.js aws-sdk doesn't support endpoint in a directory. 
 As an example here we proxy-pass our http://localhost:8000/aws-mock/ec2-endpoint 
 to a front-end reverse proxy web server (such as Apache or Nginx) on http://localhost:9090 
 */
ec2 = new AWS.EC2({
	endpoint : new AWS.Endpoint('http://localhost:9090')
});

// describe all instances by passing no filter params
ec2.describeInstances({}, function(err, resp) {

	if (err) {
		console.log("Could not describe instances", err);
	} else {

		for ( var i = 0, len = resp.Reservations.length; i < len; i++) {
			var inst = resp.Reservations[i].Instances[0];
			console.log(inst.InstanceId, inst.ImageId, inst.InstanceType,
					inst.State.Name);
		}
	}
});
