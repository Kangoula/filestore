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
import java.io.InputStream;
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
    private static String bucketName = "miage-sid-2016";


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
        List<PartETag> partETags = new ArrayList<PartETag>();
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
                bucketName, id);
        InitiateMultipartUploadResult initResponse =
                client.initiateMultipartUpload(initRequest);

       // File file = new File(filePath);
        long partSize = 5 * 1024 * 1024; // Set part size to 5 MB.
        LOGGER.log(Level.INFO, "Starting upload");
        try {

            ObjectMetadata m = new ObjectMetadata();
            m.setContentDisposition("attachment; filename="+data.getName());
          //  PutObjectRequest p = new PutObjectRequest(bucketName, id, data.getData().getInputStream(), m)
          //          .withCannedAcl(CannedAccessControlList.PublicRead);
          //  p.getMetadata().setContentLength(data.getSize());
            TransferManager t = new TransferManager(cred);
            //m.setContentLength(data.getSize());
            Upload caca = t.upload(bucketName, id, data.getData().getInputStream(),m);
            LOGGER.log(Level.INFO, "MAROT : Transfert finish");
            caca.waitForCompletion();
            t.shutdownNow();

           LOGGER.log(Level.INFO, "PROUT " + caca.getState().toString());

           /* // Step 2: Upload parts.
            long filePosition = 0;
            for (int i = 1; filePosition < data.getSize(); i++) {
                // Last part can be less than 5 MB. Adjust part size.
                partSize = Math.min(partSize, (data.getSize() - filePosition));
                LOGGER.log(Level.INFO, "PART " + i);
                // Create request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName).withKey(id)
                        .withUploadId(initResponse.getUploadId()).withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withInputStream(data.getData().getInputStream())
                        .withPartSize(partSize);

                // Upload part and add response to our list.
                partETags.add(client.uploadPart(uploadRequest).getPartETag());
                LOGGER.log(Level.INFO, "Upload request send");
                filePosition += partSize;
            }
            LOGGER.log(Level.INFO, "Boucle finie");*/
            // Step 3: Complete.
            CompleteMultipartUploadRequest compRequest = new
                    CompleteMultipartUploadRequest(bucketName,
                    id,
                    initResponse.getUploadId(),
                    partETags);

            client.completeMultipartUpload(compRequest);
            LOGGER.log(Level.INFO, "Requête finie");
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Abort upload " + e.getMessage());
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
