package com.snbdatatech.aws.ses.service.AwsSesService;

/**
 * Created by sblindt on 7/29/2016.
 */
public interface AwsSesService {
    boolean sendEmail(String fromAddress, String[] toAddresses, String[] ccAddresses, String[] bccAddresses, String subject, String content);
}
