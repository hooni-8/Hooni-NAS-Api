package org.hooni.api.common.response;

import org.hooni.api.common.code.StatusCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void preservesNotFoundStatus() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleResponseStatus(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "폴더를 찾을 수 없습니다.")
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(StatusCode.NOT_FOUND.getCode(), response.getBody().getCode());
    }

    @Test
    void preservesOtherResponseStatuses() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleResponseStatus(
                new ResponseStatusException(HttpStatus.BAD_REQUEST)
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(StatusCode.ERROR.getCode(), response.getBody().getCode());
    }

    @Test
    void returnsNotFoundFromController() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new MissingFolderController())
                .setControllerAdvice(handler)
                .build();

        mockMvc.perform(get("/missing-folder"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND.getCode()));
    }

    @RestController
    static class MissingFolderController {
        @GetMapping("/missing-folder")
        void missingFolder() {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "폴더를 찾을 수 없습니다.");
        }
    }
}
