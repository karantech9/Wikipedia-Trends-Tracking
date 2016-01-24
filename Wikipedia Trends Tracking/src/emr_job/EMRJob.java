package emr_job;

import java.util.Arrays;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClient;
import com.amazonaws.services.elasticmapreduce.model.ActionOnFailure;
import com.amazonaws.services.elasticmapreduce.model.BootstrapActionConfig;
import com.amazonaws.services.elasticmapreduce.model.DescribeJobFlowsRequest;
import com.amazonaws.services.elasticmapreduce.model.DescribeJobFlowsResult;
import com.amazonaws.services.elasticmapreduce.model.DescribeStepRequest;
import com.amazonaws.services.elasticmapreduce.model.HadoopJarStepConfig;
import com.amazonaws.services.elasticmapreduce.model.JobFlowInstancesConfig;
import com.amazonaws.services.elasticmapreduce.model.RunJobFlowRequest;
import com.amazonaws.services.elasticmapreduce.model.RunJobFlowResult;
import com.amazonaws.services.elasticmapreduce.model.ScriptBootstrapActionConfig;
import com.amazonaws.services.elasticmapreduce.model.StepConfig;
import com.amazonaws.services.elasticmapreduce.util.StepFactory;

public class EMRJob {

	static String keypair = "caproject";
	public static void run() {
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
							"Please make sure that your credentials file is at the correct " +
							"location (C:\\Users\\Saurabh Gupta\\.aws\\credentials), and is in valid format.",
							e);
		}

		AmazonElasticMapReduceClient eMRClient = new AmazonElasticMapReduceClient(credentials);

		// Enable debugging
		StepFactory stepFactory = new StepFactory();

		StepConfig enabledebugging = new StepConfig()
		.withName("Enable debugging")
		.withActionOnFailure("TERMINATE_JOB_FLOW")
		.withHadoopJarStep(stepFactory.newEnableDebuggingStep());

		//Hadoop Job Step
		HadoopJarStepConfig hadoopStep = new HadoopJarStepConfig().withJar("s3://wikitrendstracking/jar/WikiTrends.jar")
				.withArgs("s3://wikitrendstracking/input", "s3://wikitrendstracking/output1_mid", "s3://wikitrendstracking/output1"); 
		
		StepConfig hadoopJob = new StepConfig()
		.withHadoopJarStep(hadoopStep)  
		.withName("HadoopJob")  
		.withActionOnFailure(ActionOnFailure.CONTINUE); 

		//Bootstrap Actions add elastic search
		String elasticSearchPath = "s3://support.elasticmapreduce/bootstrap-actions/other/elasticsearch_install.rb";
		ScriptBootstrapActionConfig bootstrapScriptConfig = new ScriptBootstrapActionConfig();
		bootstrapScriptConfig.setPath(elasticSearchPath);
		BootstrapActionConfig elasticSearchBootstrap = new BootstrapActionConfig();
		elasticSearchBootstrap.setName("Install ElasticSearch");
		elasticSearchBootstrap.setScriptBootstrapAction(bootstrapScriptConfig);

		//Bootstrap Actions add kibana resource
		String kibanaPath = "s3://support.elasticmapreduce/bootstrap-actions/other/kibananginx_install.rb";
		bootstrapScriptConfig = new ScriptBootstrapActionConfig();
		bootstrapScriptConfig.setPath(kibanaPath);
		BootstrapActionConfig kibanaBootstrap = new BootstrapActionConfig();
		kibanaBootstrap.setName("Installkibanaginx");
		kibanaBootstrap.setScriptBootstrapAction(bootstrapScriptConfig);

		//Bootstrap Actions add logstash resource
		String logstashPath = "s3://support.elasticmapreduce/bootstrap-actions/other/logstash_install.rb";
		bootstrapScriptConfig = new ScriptBootstrapActionConfig();
		bootstrapScriptConfig.setPath(logstashPath);
		BootstrapActionConfig logstashBootstrap = new BootstrapActionConfig();
		logstashBootstrap.setName("Installlogstash");
		logstashBootstrap.setScriptBootstrapAction(bootstrapScriptConfig);


		JobFlowInstancesConfig instanceConfig = new JobFlowInstancesConfig()
		.withEc2KeyName(keypair)
		.withInstanceCount(2)
		.withMasterInstanceType(InstanceType.M1Medium.toString())
		.withSlaveInstanceType(InstanceType.M1Medium.toString()) 
		.withKeepJobFlowAliveWhenNoSteps(true);

		RunJobFlowRequest runJobFlowRequest = new RunJobFlowRequest()
		.withAmiVersion("3.5.0")
		.withBootstrapActions(elasticSearchBootstrap,logstashBootstrap,kibanaBootstrap)
		.withName("caprojecttest")
		.withSteps(enabledebugging, hadoopJob)
		.withLogUri("s3://aws-logs-393991342318-us-east-1/elasticmapreduce/")
		.withJobFlowRole("EMR_EC2_DefaultRole")
		.withServiceRole("EMR_DefaultRole")
		.withInstances(instanceConfig);
		
		
		RunJobFlowResult runJobFlowResult = eMRClient.runJobFlow(runJobFlowRequest);
		String jobFlowId = runJobFlowResult.getJobFlowId();
	
		
		System.out.println(jobFlowId);
				
	}

}
