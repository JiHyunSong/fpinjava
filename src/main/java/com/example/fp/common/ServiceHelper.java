package com.example.fp.common;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class ServiceHelper {
    @Getter
    private final AsyncRestTemplate asyncRestTemplate;

    @Autowired
    ServiceHelper(AsyncRestTemplate asyncRestTemplate) {
        this.asyncRestTemplate = asyncRestTemplate;
    }

    private <T> CompletableFuture<Optional<T>> apiCallAsOptional(String url, HttpMethod method, HttpEntity entity, ParameterizedTypeReference<T> type) {
        log.debug(String.format("api call as optional, trigger api : %s", url));
        return toCompletableFuture(asyncRestTemplate.exchange(url, method, entity, type)).thenApply(this::handleResponse).exceptionally(this::handleException);
    }

    private <T> CompletableFuture<Optional<T>> apiCallAsOptional(String url, HttpMethod method, HttpEntity entity, Class<T> type) {
        log.debug(String.format("api call as optional, trigger api : %s", url));
        return toCompletableFuture(asyncRestTemplate.exchange(url, method, entity, type)).thenApply(this::handleResponse).exceptionally(this::handleException);
    }

    private <T> CompletableFuture<Optional<T>> apiCallAsOptional(String url, HttpMethod method, HttpEntity entity, Class<T> type, Object... uriVariables) {
        log.debug(String.format("api call as optional, trigger api : %s", url));
        return toCompletableFuture(asyncRestTemplate.exchange(url, method, entity, type, uriVariables)).thenApply(this::handleResponse).exceptionally(this::handleException);
    }

    private <T> CompletableFuture<Optional<T>> apiCallAsOptionalWithException(String url, HttpMethod method, HttpEntity entity, Class<T> type, Object... uriVariables) {
        log.debug(String.format("api call as optional, trigger api : %s", url));
        return toCompletableFuture(asyncRestTemplate.exchange(url, method, entity, type, uriVariables)).thenApply(this::handleResponse);
    }

    private <T> CompletableFuture<Optional<T>> apiCallAsOptionalWithException(String url, HttpMethod method, HttpEntity entity, ParameterizedTypeReference<T> type, Object... uriVariables) {
        log.debug(String.format("api call as optional, trigger api : %s", url));
        return toCompletableFuture(asyncRestTemplate.exchange(url, method, entity, type, uriVariables)).thenApply(this::handleResponse);
    }

    public <T> CompletableFuture<Optional<T>> getApiCallAsOptional(String url, Class<T> type) {
        return apiCallAsOptional(url, HttpMethod.GET, buildNonBodyEntity(), type);
    }

    public <T> CompletableFuture<Optional<T>> getApiCallAsOptional(String url, Class<T> type, Object... urlVariables) {
        return apiCallAsOptional(url, HttpMethod.GET, buildNonBodyEntity(), type, urlVariables);
    }

    public <T> CompletableFuture<Optional<T>> getApiCallAsOptionalWithException(String url, Class<T> type, Object... urlVariables) {
        return apiCallAsOptionalWithException(url, HttpMethod.GET, buildNonBodyEntity(), type, urlVariables);
    }
    public <T> CompletableFuture<Optional<T>> getApiCallAsOptionalWithException(String url, ParameterizedTypeReference<T> type, Object... urlVariables) {
        return apiCallAsOptionalWithException(url, HttpMethod.GET, buildNonBodyEntity(), type, urlVariables);
    }

    public <T> CompletableFuture<Optional<T>> getApiCallAsOptional(String url, ParameterizedTypeReference<T> type) {
        return apiCallAsOptional(url, HttpMethod.GET, buildNonBodyEntity(), type);
    }

    public <T, U> CompletableFuture<Optional<T>> postApiCallAsOptional(String url, U data, Class<T> type) {
        return apiCallAsOptional(url, HttpMethod.POST, buildBodyEntity(data), type);
    }

    public <T, U> CompletableFuture<Optional<T>> postApiCallAsOptional(String url, U data, ParameterizedTypeReference<T> type) {
        return apiCallAsOptional(url, HttpMethod.POST, buildBodyEntity(data), type);
    }

    public <T, U> CompletableFuture<Optional<T>> postApiCallAsOptional(String url, U data, Class<T> type, Object... uriVariables) {
        return apiCallAsOptional(url, HttpMethod.POST, buildBodyEntity(data), type, uriVariables);
    }

    public <T, U> CompletableFuture<Optional<T>> putApiCallAsOptional(String url, U data, Class<T> type) {
        return apiCallAsOptional(url, HttpMethod.PUT, buildBodyEntity(data), type);
    }

    public <T, U> CompletableFuture<Optional<T>> putApiCallAsOptional(String url, U data, Class<T> type, Object... uriVariables) {
        return apiCallAsOptional(url, HttpMethod.PUT, buildBodyEntity(data), type, uriVariables);
    }

    public <T, U> CompletableFuture<Optional<T>> putApiCallAsOptional(String url, U data, ParameterizedTypeReference<T> type) {
        return apiCallAsOptional(url, HttpMethod.PUT, buildBodyEntity(data), type);
    }

    public <T> CompletableFuture<Optional<T>> deleteApiCallAsOptional(String url, Class<T> type) {
        return apiCallAsOptional(url, HttpMethod.DELETE, buildNonBodyEntity(), type);
    }

    public <T> CompletableFuture<Optional<T>> deleteApiCallAsOptional(String url, Class<T> type, Object... uriVariables) {
        return apiCallAsOptional(url, HttpMethod.DELETE, buildNonBodyEntity(), type, uriVariables);
    }

    public <T> CompletableFuture<Optional<T>> deleteApiCallAsOptionalWithException(String url, Class<T> type, Object... uriVariables) {
        return apiCallAsOptionalWithException(url, HttpMethod.DELETE, buildNonBodyEntity(), type, uriVariables);
    }

    public <T> CompletableFuture<Optional<T>> deleteApiCallAsOptional(String url, ParameterizedTypeReference<T> type) {
        return apiCallAsOptional(url, HttpMethod.DELETE, buildNonBodyEntity(), type);
    }

    private <T> HttpEntity<T> buildBodyEntity(T data) {
        if (data == null) {
            log.warn("body data is null.");
            return buildNonBodyEntity();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
        return new HttpEntity<>(data, headers);
    }

    private <T> HttpEntity<T> buildNonBodyEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
        return new HttpEntity<>(headers);
    }

    private <T> Optional<T> handleException(Throwable t) {
        if (t.getCause() instanceof HttpClientErrorException) {
            HttpStatus httpStatus = ((HttpClientErrorException)t.getCause()).getStatusCode();
             if (httpStatus.is5xxServerError()) {
                 log.error("Failed to call api, exception occurred, ", t);
             } else {
                 log.warn("Failed to call api, exception occurred, ", t);
             }
        } else {
            log.error("Failed to call api, exception occurred, ", t);
        }
        return Optional.empty();
    }

    private <T> Optional<T> handleResponse(final ResponseEntity<T> response) {
        if (response.getStatusCode() != HttpStatus.OK) {
            if (response.getStatusCode().is5xxServerError()) {
                log.error("Failed to call api, non OK status, {}", response);
            } else {
                log.warn("Failed to call api, non OK status, {}", response);
            }
            return Optional.empty();
        }
        return Optional.of(response.getBody());
    }

    private <T> CompletableFuture<T> toCompletableFuture(final ListenableFuture<T> listenableFuture) {
        //create an instance of CompletableFuture
        CompletableFuture<T> completable = new CompletableFuture<T>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                // propagate cancel to the listenable future
                boolean result = listenableFuture.cancel(mayInterruptIfRunning);
                super.cancel(mayInterruptIfRunning);
                return result;
            }
        };

        // add callback
        listenableFuture.addCallback(completable::complete, completable::completeExceptionally);
        return completable;
    }
}