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

import java.util.Arrays;
import java.util.List;

/**
 * Created by sblindt on 7/29/2016.
 */
@Service
public class AwsSesServiceImpl implements AwsSesService {

    @Value("${aws.ses.region}")
    private String region;

    @Autowired
    private AwsConfigurationService configurationService;

    private AmazonSimpleEmailService client;
    private List<String> verifiedIdentities;

    // Create logger
    private final Logger logger = LogManager.getLogger(AwsSesServiceImpl.class);

    /**
     * Automatically called on bean creation.
     */
    public void init() {

        // Create an AWS SES client
        this.client = new AmazonSimpleEmailServiceClient(this.configurationService.getAwsCredentials());

        // Create and set the needed AWS region
        Region REGION = Region.getRegion(Regions.valueOf(this.region));
        this.client.setRegion(REGION);

        // Get the verified identities - Email addresses and domains allowed to use SES endpoint
        this.verifiedIdentities = this.client.listIdentities().getIdentities();
    }

    /**
     * Send email using AWS SES service
     *
     * @param fromAddress String of the address the email is coming from
     * @param toAddresses String array of addresses to send to
     * @param ccAddresses String array of cc addresses to send to
     * @param bccAddresses String array of bcc addresses to send to
     * @param subject the subject of the message
     * @param content the content of the message
     * @return true if the email was sent successfully
     */
    public boolean sendEmail(String fromAddress, String[] toAddresses, String[] ccAddresses, String[] bccAddresses, String subject, String content) {

        // Verify the from address is set
        if ((fromAddress != null) && (!fromAddress.equals(""))) {

            // Determine if the from address is verified
            if (this.fromAddressIsVerified(fromAddress)) {

                // Create the destination
                Destination destination = this.createEmailDestination(toAddresses, ccAddresses, bccAddresses);

                // Create the message
                Message message = this.createEmailMessage(subject, content);

                // Build the Email request
                SendEmailRequest request = new SendEmailRequest()
                        .withSource(fromAddress)
                        .withDestination(destination)
                        .withMessage(message);

                try {

                    // Send the email and get the result.
                    SendEmailResult result = this.client.sendEmail(request);

                    // Log the Send Result - MessageId
                    logger.error(result);

                    // Email request was successful
                    return true;

                } catch (Exception ex) {

                    // Log request exception
                    this.logger.error(ex.getMessage());

                    // Failed to send email request
                    return false;
                }
            }

            // Log failed address attempt
            this.logger.error("The given FROM ADDRESS [" + fromAddress + "] has not been verified with AWS SES. Email from addresses must be verified with SES before they can be used.");

        } else {

            // Log failed address attempt
            this.logger.error("The given FROM ADDRESS was not set, or was left empty, therefore the email service will not attempt to send emails.");
        }

        // Failed to send emails
        return false;
    }

    /**
     * Verify the given from address is verified with SES
     *
     * @param fromAddress the from address of the email
     * @return true if the address is verified with SES
     */
    public boolean fromAddressIsVerified(String fromAddress) {

        // Cycle through verified identities from AWS
        for (String identity : this.verifiedIdentities) {

            // Parse the main domain from the email address even if it includes subdomains
            String domain = fromAddress.substring(fromAddress.indexOf('@') + 1).replaceAll(".*\\.(?=.*\\.)", "");

            // Check if identity has been verified
            if ((identity.equals(domain)) || (identity.equals(fromAddress))) {
                return true;
            }
        }

        // Identity not verified
        return false;
    }

    /**
     * Create a destination for the email
     *
     * @param toAddresses String array of addresses to send to
     * @param ccAddresses String array of cc addresses to send to
     * @param bccAddresses String array of bcc addresses to send to
     * @return the destination of the email
     */
    private Destination createEmailDestination(String[] toAddresses, String[] ccAddresses, String[] bccAddresses) {

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

        // Return the message destination
        return destination;
    }

    /**
     * Create a message for the email
     *
     * @param subject the subject of the email
     * @param content the content of the email
     * @return the message of the email
     */
    private Message createEmailMessage(String subject, String content) {

        // Create the subject of the message
        Content subjectContent = new Content().withData(subject);

        // Create the body of the message
        Content bodyContent = new Content().withData(content);
        Body body = new Body().withText(bodyContent);

        // Create a message using the subject and body.
        return new Message().withSubject(subjectContent).withBody(body);
    }
}
