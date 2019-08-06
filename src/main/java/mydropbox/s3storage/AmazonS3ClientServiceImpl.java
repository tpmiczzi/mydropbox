package mydropbox.s3storage;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class AmazonS3ClientServiceImpl implements AmazonS3ClientService {
    private String awsS3Bucket;
    private AmazonS3 amazonS3;
    private static final Logger logger = LoggerFactory.getLogger(AmazonS3ClientServiceImpl.class);

    @Autowired
    public AmazonS3ClientServiceImpl(Region awsRegion,
                                     String awsS3Bucket) {
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                                             .withCredentials(new InstanceProfileCredentialsProvider(false))
                                             .withRegion(awsRegion.getName()).build();
        this.awsS3Bucket = awsS3Bucket;
    }

    @Async
    public void uploadFileToS3Bucket(MultipartFile multipartFile, boolean enablePublicReadAccess) {
        String fileName = multipartFile.getOriginalFilename();

        try {
            //creating the file in the server (temporarily)
            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();

            PutObjectRequest putObjectRequest = new PutObjectRequest(this.awsS3Bucket, fileName, file);

            if (enablePublicReadAccess) {
                putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
            }
            this.amazonS3.putObject(putObjectRequest);
            //removing the file created in the server
            file.delete();
        } catch (IOException | AmazonServiceException ex) {
            logger.error("error [" + ex.getMessage() + "] occurred while uploading [" + fileName + "] ");
        }
    }

    @Async
    public void deleteFileFromS3Bucket(String fileName) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(awsS3Bucket, fileName));
        } catch (AmazonServiceException ex) {
            logger.error("error [" + ex.getMessage() + "] occurred while removing [" + fileName + "] ");
        }
    }

    @Async
    public List<S3ObjectSummary> getListFiles() {
        ObjectListing listing = amazonS3.listObjects(awsS3Bucket);
        List<S3ObjectSummary> summaries = listing.getObjectSummaries();

        while (listing.isTruncated()) {
            listing = amazonS3.listNextBatchOfObjects(listing);
            summaries.addAll(listing.getObjectSummaries());
        }

        return summaries;
    }

    @Async
    public S3Object getFile(String key) {
        return amazonS3.getObject(new GetObjectRequest(awsS3Bucket, key));
    }
}
