package org.filestore.ejb.store;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import org.filestore.api.FileData;
import org.filestore.ejb.file.FileServiceBean;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
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

    public String put(FileData data) throws BinaryStoreServiceException {
        LOGGER.log(Level.INFO, "BUCKET : " + bucketName);
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
                bucketName, id);
        InitiateMultipartUploadResult initResponse =
                client.initiateMultipartUpload(initRequest);
        LOGGER.log(Level.INFO, "Starting upload");
        try {

            ObjectMetadata m = new ObjectMetadata();
            m.setContentDisposition("attachment; filename="+data.getName());
            TransferManager t = new TransferManager(cred);
            Upload fileUpload = t.upload(new PutObjectRequest(bucketName, id, data.getData(), m)
                             .withCannedAcl(CannedAccessControlList.PublicRead));
            LOGGER.log(Level.INFO, "Transfert finish");
            fileUpload.waitForCompletion();
            t.shutdownNow();
            LOGGER.log(Level.INFO, "End request");
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Abort upload " + e.getMessage());
            client.abortMultipartUpload(new AbortMultipartUploadRequest(
                    bucketName, id, initResponse.getUploadId()));

        }


        return id;
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
