package org.filestore.ejb.store;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.filestore.api.FileData;
import org.filestore.ejb.file.FileServiceBean;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
        long partSize = 500 * 1024 * 1024; // Set part size to 5 MB.
        List<PartETag> partETags = new ArrayList<PartETag>();
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
                bucketName, id);
        initRequest.setCannedACL(CannedAccessControlList.PublicRead);
        InitiateMultipartUploadResult initResponse =
                client.initiateMultipartUpload(initRequest);
        try{
            InputStream is = data.getData().getInputStream();
            OutputStream os = new FileOutputStream(new File("./temp.file"));

// This will copy the file from the two streams
            IOUtils.copy(is, os);
            os.close();
            is.close();

            long length = data.getSize();
            File file = new File("./temp.file");
            LOGGER.log(Level.INFO, "SIZE : " + length);
        for (int i = 1; filePosition < length; i++) {
            // Last part can be less than 5 MB. Adjust part size.
            partSize = Math.min(partSize, (length - filePosition));
            boolean lastPart = filePosition+partSize == length;
            LOGGER.log(Level.INFO, "LAST PART : " + lastPart);
            LOGGER.log(Level.INFO, "PARTSIZE : " + partSize);
            // Create request to upload a part.
            UploadPartRequest uploadRequest = null;
                uploadRequest = new UploadPartRequest()
                    .withBucketName(bucketName).withKey(id)
                    .withUploadId(initResponse.getUploadId()).withPartNumber(i)
                    .withFile(file)
                    .withPartSize(partSize)
                        .withLastPart(lastPart);
            LOGGER.log(Level.INFO, "UPLOAD REQUEST : OK");
            partETags.add(client.uploadPart(uploadRequest).getPartETag());
            LOGGER.log(Level.INFO, "ETags  : " + partETags.get(i-1).getETag());
            filePosition += partSize;
            LOGGER.log(Level.INFO, "PART : " + i);
            LOGGER.log(Level.INFO, "FilePosition : " + filePosition);

        }
        file.delete();
            LOGGER.log(Level.INFO, "Transfert finish");
        // Step 3: complete.
        CompleteMultipartUploadRequest compRequest = new
                CompleteMultipartUploadRequest(
                bucketName,
                id,
                initResponse.getUploadId(),
                partETags);

        client.completeMultipartUpload(compRequest);
            LOGGER.log(Level.INFO, "Return  id : " + id);
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

        LOGGER.log(Level.INFO, "Return " + client.getResourceUrl(bucketName, key));

        return  client.getResourceUrl(bucketName, key);


    }

    @Override
    public void delete(String key) throws BinaryStoreServiceException, BinaryStreamNotFoundException {
        client.deleteObject(bucketName, key);
    }
}
