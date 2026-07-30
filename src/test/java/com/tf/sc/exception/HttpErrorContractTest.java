package com.tf.sc.exception;

import com.tf.sc.advice.ResultHttpStatusAdvice;
import com.tf.sc.common.Result;
import com.tf.sc.common.ResultCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class HttpErrorContractTest {
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new ContractController())
                .setControllerAdvice(new GlobalExceptionHandler(), new ResultHttpStatusAdvice())
                .build();
    }

    @Test
    void resultCodeControlsHttpStatus() throws Exception {
        mockMvc.perform(get("/contract/bad-request"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void missingRequestParameterIsBadRequest() throws Exception {
        mockMvc.perform(get("/contract/validation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void authenticationAndAuthorizationHaveDifferentStatuses() throws Exception {
        mockMvc.perform(get("/contract/unauthorized"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));

        mockMvc.perform(get("/contract/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void unexpectedFailureIsInternalServerError() throws Exception {
        mockMvc.perform(get("/contract/failure"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500));
    }

    @RestController
    @RequestMapping("/contract")
    static class ContractController {
        @GetMapping("/bad-request")
        Result<Void> badRequest() {
            return Result.error("bad request");
        }

        @GetMapping("/validation")
        Result<String> validation(@RequestParam String value) {
            return Result.success(value);
        }

        @GetMapping("/unauthorized")
        Result<Void> unauthorized() {
            throw new UnauthorizedException("unauthorized");
        }

        @GetMapping("/forbidden")
        Result<Void> forbidden() {
            throw new ForbiddenException("forbidden");
        }

        @GetMapping("/failure")
        Result<Void> failure() {
            throw new IllegalStateException("failure");
        }
    }
}
