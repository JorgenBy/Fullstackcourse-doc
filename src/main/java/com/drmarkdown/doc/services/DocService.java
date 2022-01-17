package com.drmarkdown.doc.services;


import com.drmarkdown.doc.dtos.DocDTO;
import com.drmarkdown.doc.exceptions.FaultyInputException;
import com.drmarkdown.doc.exceptions.UserNotAllowedException;

import java.util.List;

public interface DocService {
    void createDocument(DocDTO docDTO) throws FaultyInputException;

    List<DocDTO> fetchDocsForUserId(String userId, String callerUserId);

    List<DocDTO> fetchTopRecentDocs();

    void updateDoc(DocDTO docDTO, String userId) throws UserNotAllowedException, FaultyInputException;

    void deleteDoc(String docId, String userId) throws UserNotAllowedException;

    DocDTO fetchDoc(String docId, String userId);
}
