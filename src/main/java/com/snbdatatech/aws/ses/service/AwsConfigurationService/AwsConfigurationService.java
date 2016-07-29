package com.snbdatatech.aws.ses.service.AwsConfigurationService;

import com.amazonaws.auth.BasicAWSCredentials;

/**
 * Created by sblindt on 7/29/2016.
 */
public interface AwsConfigurationService {
    BasicAWSCredentials getAwsCredentials();
}
