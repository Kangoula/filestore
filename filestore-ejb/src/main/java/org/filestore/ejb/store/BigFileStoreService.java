package org.filestore.ejb.store;

import org.filestore.api.FileData;

/**
 * Created by Alexandre on 23/11/2016.
 */
public interface BigFileStoreService extends BinaryStoreService {

    public String put(FileData is) throws BinaryStoreServiceException;
}
