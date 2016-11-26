package org.filestore.ejb.store;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import org.filestore.api.FileData;
import org.filestore.ejb.file.FileServiceBean;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alexandre on 23/11/2016.
 */
@Startup
@Singleton(name="s3store")
@LocalBean
public class S3StoreServiceBean implements S3StoreService {


    private static final Logger LOGGER = Logger.getLogger(FileServiceBean.class.getName());

    private static final AWSCredentials cred = new BasicAWSCredentials(System.getProperty("AWS_ACCESS_KEY_ID"),System.getProperty("AWS_SECRET_ACCESS_KEY"));

    private AmazonS3Client client ;
    private static final String bucketName = System.getProperty("BUCKET_NAME");


    public S3StoreServiceBean() {
                this.client = new AmazonS3Client(cred);
    }


    @PostConstruct
    public void init() {
        LOGGER.log(Level.FINEST, "Initializing S3");
    }

    @Override
    public boolean exists(String key) throws BinaryStoreServiceException {
        return client.doesObjectExist(bucketName, key);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String put(FileData data) throws BinaryStoreServiceException {
        LOGGER.log(Level.INFO, "BUCKET : " + bucketName);
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        long filePosition = 0;
        long partSize = 5 * 1024 * 1024; // Set part size to 5 MB.
        List<PartETag> partETags = new ArrayList<PartETag>();
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
                bucketName, id);
        InitiateMultipartUploadResult initResponse =
                client.initiateMultipartUpload(initRequest);
        try{
        for (int i = 1; filePosition < data.getSize(); i++) {
            // Last part can be less than 5 MB. Adjust part size.
            partSize = Math.min(partSize, (data.getSize() - filePosition));

            // Create request to upload a part.
            UploadPartRequest uploadRequest = null;
                uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName).withKey(id)
                        .withUploadId(initResponse.getUploadId()).withPartNumber(i)
                        .withInputStream(data.getData().getInputStream())
                        .withPartSize(partSize);

            // repeat the upload until it succeeds.
            boolean anotherPass;
            do {
                anotherPass = false;  // assume everythings ok
                try {
                    // Upload part and add response to our list.
                    partETags.add(client.uploadPart(uploadRequest).getPartETag());
                } catch (Exception e) {
                    anotherPass = true; // repeat
                }
            } while (anotherPass);

            filePosition += partSize;
            LOGGER.log(Level.INFO, "PART : " + i);
            LOGGER.log(Level.INFO, "FilePosition : " + filePosition);
        }

        // Step 3: complete.
        CompleteMultipartUploadRequest compRequest = new
                CompleteMultipartUploadRequest(
                bucketName,
                id,
                initResponse.getUploadId(),
                partETags);

        client.completeMultipartUpload(compRequest);
            return id;
    } catch (Exception e) {
        client.abortMultipartUpload(new AbortMultipartUploadRequest(
                bucketName, id, initResponse.getUploadId()));
        e.printStackTrace();
        return null;
    }

    }

    @Override
    public String get(String key) throws BinaryStoreServiceException, BinaryStreamNotFoundException {

        LOGGER.log(Level.INFO, client.getResourceUrl(bucketName, key) + " kikou");

        return  client.getResourceUrl(bucketName, key);


    }

    @Override
    public void delete(String key) throws BinaryStoreServiceException, BinaryStreamNotFoundException {
        client.deleteObject(bucketName, key);
    }
}
