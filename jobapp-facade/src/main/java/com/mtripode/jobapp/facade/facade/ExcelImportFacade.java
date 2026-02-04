package com.mtripode.jobapp.facade.facade;

import java.io.InputStream;

public interface ExcelImportFacade {

    boolean processFile (String filePath);

    boolean processFile(InputStream inputStream);
    
}
