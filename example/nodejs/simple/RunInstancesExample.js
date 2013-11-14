var AWS, ec2, params;

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

params = {
	ImageId : 'ami-00abcdef', // a pre-defined AMI in aws-mock
	InstanceType : 't1.micro',
	MinCount : 10,
	MaxCount : 10
};

// run 10 new instances
ec2.runInstances(params, function(err, resp) {

	if (err) {
		console.log("Could not create instance", err);
	} else {

		console.log("Created instances:")
		for ( var i = 0, len = resp.Instances.length; i < len; i++) {
			console.log(resp.Instances[i].InstanceId,
					resp.Instances[i].State.Name);
		}
	}

});