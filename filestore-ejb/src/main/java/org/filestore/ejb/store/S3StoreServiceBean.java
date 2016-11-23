package org.filestore.ejb.store;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import org.filestore.api.FileData;
import org.filestore.ejb.file.FileServiceBean;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alexandre on 23/11/2016.
 */
public class S3StoreServiceBean implements BigFileStoreService {

    private static AmazonS3Client client = new AmazonS3Client(new ProfileCredentialsProvider());
    private static String bucketName = "miage-sid-2016";
    private static final Logger LOGGER = Logger.getLogger(FileServiceBean.class.getName());

    @PostConstruct
    public void init() {
        LOGGER.log(Level.FINEST, "Initializing S3");
    }

    @Override
    public boolean exists(String key) throws BinaryStoreServiceException {
        return client.doesObjectExist(bucketName, key);
    }

    @Override
    public String put(InputStream is) throws BinaryStoreServiceException {
        throw  new BinaryStoreServiceException("Not implemented");
    }

    @Override
    public String put(FileData data) throws BinaryStoreServiceException {
        List<PartETag> partETags = new ArrayList<PartETag>();
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
                bucketName, id);
        InitiateMultipartUploadResult initResponse =
                client.initiateMultipartUpload(initRequest);

       // File file = new File(filePath);
        long partSize = 5 * 1024 * 1024; // Set part size to 5 MB.

        try {
            // Step 2: Upload parts.
            long filePosition = 0;
            for (int i = 1; filePosition < data.getSize(); i++) {
                // Last part can be less than 5 MB. Adjust part size.
                partSize = Math.min(partSize, (data.getSize() - filePosition));

                // Create request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName).withKey(id)
                        .withUploadId(initResponse.getUploadId()).withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withInputStream(data.getData().getInputStream())
                        .withPartSize(partSize);

                // Upload part and add response to our list.
                partETags.add(client.uploadPart(uploadRequest).getPartETag());

                filePosition += partSize;
            }

            // Step 3: Complete.
            CompleteMultipartUploadRequest compRequest = new
                    CompleteMultipartUploadRequest(bucketName,
                    id,
                    initResponse.getUploadId(),
                    partETags);

            client.completeMultipartUpload(compRequest);
        } catch (Exception e) {
            client.abortMultipartUpload(new AbortMultipartUploadRequest(
                    bucketName, id, initResponse.getUploadId()));
        }

        return id;
    }

    @Override
    public InputStream get(String key) throws BinaryStoreServiceException, BinaryStreamNotFoundException {
       return  client.getObject(bucketName, key).getObjectContent();


    }

    @Override
    public void delete(String key) throws BinaryStoreServiceException, BinaryStreamNotFoundException {
        client.deleteObject(bucketName, key);
    }
}
