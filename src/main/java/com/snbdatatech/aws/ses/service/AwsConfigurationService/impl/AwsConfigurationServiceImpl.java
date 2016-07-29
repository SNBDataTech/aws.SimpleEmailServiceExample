package com.snbdatatech.aws.ses.service.AwsConfigurationService.impl;

import com.amazonaws.auth.BasicAWSCredentials;
import com.snbdatatech.aws.ses.service.AwsConfigurationService.AwsConfigurationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by sblindt on 7/29/2016.
 */
@Service
public class AwsConfigurationServiceImpl implements AwsConfigurationService {

    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretAccessKey}")
    private String secretAccessKey;

    /**
     * Create an AWS credentials object
     * @return Aws Credentials
     */
    public BasicAWSCredentials getAwsCredentials () {
        return new BasicAWSCredentials(this.accessKeyId, this.secretAccessKey);
    }
}
