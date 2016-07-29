package com.snbdatatech.aws.ses.service.AwsSesService.impl;

import com.amazonaws.services.simpleemail.*;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.regions.*;
import com.snbdatatech.aws.ses.service.AwsConfigurationService.AwsConfigurationService;
import com.snbdatatech.aws.ses.service.AwsSesService.AwsSesService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by sblindt on 7/29/2016.
 */
@Service
public class AwsSesServiceImpl implements AwsSesService {

    @Value("${aws.ses.fromAddress}")
    private String fromAddress;

    @Value("${aws.ses.region}")
    private String region;

    @Autowired
    private AwsConfigurationService configurationService;

    // Create logger
    private final Logger logger = LogManager.getLogger(AwsSesServiceImpl.class);

    /**
     * Send email using AWS SES service
     * @param toAddresses String array of addresses to send to
     * @param ccAddresses String array of cc addresses to send to
     * @param bccAddresses String array of bcc addresses to send to
     * @param subject the subject of the message
     * @param content the content of the message
     * @return true if the email was sent successfully
     */
    public boolean sendEmail(String[] toAddresses, String[] ccAddresses, String[] bccAddresses, String subject, String content) {

        // Create destination for recipients
        Destination destination = new Destination().withToAddresses(toAddresses);

        // Determine if cc addresses need to be added
        if ((ccAddresses != null) && (ccAddresses.length > 0)) {

            // Add cc addresses to destination
            destination.withCcAddresses(ccAddresses);
        }

        // Determine if bcc addresses need to be added
        if ((bccAddresses != null) && (bccAddresses.length > 0)) {

            // Add bcc addresses to destination
            destination.withBccAddresses(bccAddresses);
        }

        // Create the subject of the message
        Content subjectContent = new Content().withData(subject);

        // Create the body of the message
        Content bodyContent = new Content().withData(content);
        Body body = new Body().withText(bodyContent);

        // Create a message with the specified subject and body.
        Message message = new Message().withSubject(subjectContent).withBody(body);

        // Build the Email request
        SendEmailRequest request = new SendEmailRequest().withSource(this.fromAddress).withDestination(destination).withMessage(message);

        try
        {
            // Create an AWS SES client
            AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(this.configurationService.getAwsCredentials());

            // Create and set the needed AWS region
            Region REGION = Region.getRegion(Regions.valueOf(this.region));
            client.setRegion(REGION);

            // Send the email.
            client.sendEmail(request);

            // Email request was successful
            return true;
        }
        catch (Exception ex)
        {
            // Log request exception
            this.logger.error(ex.getMessage());

            // Failed to send email request
            return false;
        }
    }
}
