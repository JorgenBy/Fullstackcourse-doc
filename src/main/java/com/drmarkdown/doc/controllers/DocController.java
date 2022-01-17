package com.drmarkdown.doc.controllers;

import com.drmarkdown.doc.dtos.DocDTO;
import com.drmarkdown.doc.exceptions.FaultyInputException;
import com.drmarkdown.doc.exceptions.UserNotAllowedException;
import com.drmarkdown.doc.services.DocService;
import com.drmarkdown.doc.services.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/doc")
public class DocController {

    @Autowired
    DocService docService;

    @Autowired
    TokenService tokenService;


    //create documents
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public DocDTO createDocument(@RequestBody DocDTO docDTO) throws FaultyInputException {
        docService.createDocument(docDTO);
        return docDTO;
    }

    //fetch users own documents
    @GetMapping("/all/{userId}")
    @PreAuthorize("hasAnyRole('ANONYMOUS', 'USER', 'ADMIN')")
    public List<DocDTO> fetchUserDocs(@PathVariable String userId, HttpServletRequest httpServletRequest) {

        String jwtToken = getJwtTokenFromHeader(httpServletRequest);
        String callerUserId = tokenService.getUserId(jwtToken);

        return docService.fetchDocsForUserId(userId, callerUserId);
    }

    //- fetch a public document
    @GetMapping("/fetch/{docId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ANONYMOUS')")
    public DocDTO fetchDocument(@PathVariable String docId, HttpServletRequest httpServletRequest) {

        String jwtToken = getJwtTokenFromHeader(httpServletRequest);
        String userId = tokenService.getUserId(jwtToken);

        return docService.fetchDoc(docId, userId);
    }

    //fetch 10 most recent documents
    @GetMapping("/recentdocs")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ANONYMOUS')")
    public List<DocDTO> fetchRecentDocs() {
        return docService.fetchTopRecentDocs();
    }

    // modify users document
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public DocDTO updateDoc(@RequestBody DocDTO docDTO, HttpServletRequest httpServletRequest) throws UserNotAllowedException, FaultyInputException {

        String jwtToken = getJwtTokenFromHeader(httpServletRequest);

        String userId = tokenService.getUserId(jwtToken); //userId -> issuer on jwtToken

        docService.updateDoc(docDTO, userId);

        return docDTO;

    }

    private String getJwtTokenFromHeader(HttpServletRequest httpServletRequest) {

        try {
            String tokenHeader = httpServletRequest.getHeader(AUTHORIZATION);
            return StringUtils.removeStart(tokenHeader, "Bearer ").trim();
        } catch (NullPointerException e) {
            return StringUtils.EMPTY;
        }
    }


    // delete users own document
    // modify users document
    @DeleteMapping("/delete/{docId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void deleteDoc(@PathVariable String docId, HttpServletRequest httpServletRequest) throws UserNotAllowedException {

        String jwtToken = getJwtTokenFromHeader(httpServletRequest);

        String userId = tokenService.getUserId(jwtToken); //userId -> issuer on jwtToken

        docService.deleteDoc(docId, userId);
    }
}
