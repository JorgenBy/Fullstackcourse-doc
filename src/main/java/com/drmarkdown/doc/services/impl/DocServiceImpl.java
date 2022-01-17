package com.drmarkdown.doc.services.impl;

import com.drmarkdown.doc.daos.DocDAO;
import com.drmarkdown.doc.dtos.DocDTO;
import com.drmarkdown.doc.exceptions.FaultyInputException;
import com.drmarkdown.doc.exceptions.UserNotAllowedException;
import com.drmarkdown.doc.models.DocModel;
import com.drmarkdown.doc.services.DocService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.isNull;


@Service
public class DocServiceImpl implements DocService {
    private static final Logger logger = LoggerFactory.getLogger(DocServiceImpl.class.getName());
    @Autowired
    DocDAO docDAO;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public void createDocument(DocDTO docDTO) throws FaultyInputException {
        logger.info("Creating document: " + docDTO.getId());
        checkNotNull(checkEmptyString(docDTO.getContent(), "Content"));
        checkNotNull(checkEmptyString(docDTO.getTitle(), "Title"));
        checkNotNull(checkEmptyString(docDTO.getUserId(), "UserID"));
        docDTO.setId(null);

        DocModel docModel = modelMapper.map(docDTO, DocModel.class);

        if (isNull(docModel.getAvailable())) {
            docModel.setAvailable(false);
        }

        docDAO.save(docModel);

        modelMapper.map(docModel, docDTO);
    }

    @Override
    public List<DocDTO> fetchDocsForUserId(String userId, String callerUserId) {
        final List<DocModel> allByUserId = docDAO.findAllByUserIdOrderByUpdatedAtDesc(userId);

        if (userId.equals(callerUserId)) {
            return allByUserId.stream()
                    .map(docModel -> modelMapper.map(docModel, DocDTO.class))
                    .collect(Collectors.toList());
        } else {
            return allByUserId.stream()
                    .filter(DocModel::getAvailable)
                    .map(docModel -> modelMapper.map(docModel, DocDTO.class))
                    .collect(Collectors.toList());
        }

    }

    @Override
    public List<DocDTO> fetchTopRecentDocs() {
        final Page<DocModel> recentDocs = docDAO.findByAvailable(true, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt")));

        return recentDocs.stream()
                .map(docModel -> modelMapper.map(docModel, DocDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateDoc(DocDTO docDTO, String userId) throws UserNotAllowedException, FaultyInputException {
        logger.info("Updating document: " + docDTO.getId());
        checkNotNull(checkEmptyString(docDTO.getContent(), "Content"));
        checkNotNull(checkEmptyString(docDTO.getTitle(), "Title"));
        checkNotNull(checkEmptyString(docDTO.getUserId(), "UserID"));

        final Optional<DocModel> optionalDocModel = docDAO.findById(docDTO.getId());
        if (optionalDocModel.isPresent()) {

            final DocModel docModel = optionalDocModel.get();
            if (docModel.getUserId().equals(userId)) {

                modelMapper.map(docDTO, docModel);

                docDAO.save(docModel);

                modelMapper.map(docModel, docDTO);
                return;
            } else {
                throw new UserNotAllowedException("You are not allowed to modify this document");
            }
        }

        throw new NoSuchElementException("No document with id " + docDTO.getId() + " was found");
    }

    private Object checkEmptyString(String content, String contentPart) throws FaultyInputException {
        if (content.isBlank()) {
            throw new FaultyInputException(contentPart + " is not allowed to be empty");
        }
        return content;
    }

    @Override
    public void deleteDoc(String docId, String userId) throws UserNotAllowedException {
        logger.info("Deleting document with id: " + docId);
        final Optional<DocModel> optionalDocModel = docDAO.findById(docId);
        if (optionalDocModel.isPresent()) {

            final DocModel docModel = optionalDocModel.get();
            if (docModel.getUserId().equals(userId)) {

                docDAO.delete(docModel);
                return;
            } else {
                throw new UserNotAllowedException("You are not allowed to delete this document");
            }
        }

        throw new NoSuchElementException("No document with id " + docId+ " was found");
    }

    @Override
    public DocDTO fetchDoc(String docId, String userId) {

        final Optional<DocModel> optionalDocModel = docDAO.findById(docId);
        if (optionalDocModel.isPresent()) {

            if (optionalDocModel.get().getUserId().equals(userId)) {
                return modelMapper.map(optionalDocModel.get(), DocDTO.class);
            } else {
                if (optionalDocModel.get().getAvailable()) {
                    return modelMapper.map(optionalDocModel.get(), DocDTO.class);
                }
            }

            return null;
        }

        return null;
    }


}
