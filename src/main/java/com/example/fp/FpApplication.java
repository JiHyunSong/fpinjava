package com.example.fp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@EnableAsync
@SpringBootApplication
public class FpApplication {
	private static final String FUNCTION_CALLER_FAILED_TO_FIND = "[FAILED TO FIND]";
	private static final String FUNCTION_CALLER_NOT_FONUD = "[NOT FOUND]";

	public static void main(String[] args) {
		SpringApplication.run(FpApplication.class, args);
	}

	public static <T> CompletableFuture<T> toCompletableFuture(final ListenableFuture<T> listenableFuture) {
		//create an instance of CompletableFuture
		final CompletableFuture<T> completable = new CompletableFuture<T>() {
			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				// propagate cancel to the listenable future
				boolean result = listenableFuture.cancel(mayInterruptIfRunning);
				super.cancel(mayInterruptIfRunning);
				return result;
			}
		};

		// add callback
		listenableFuture.addCallback(new ListenableFutureCallback<T>() {
			@Override
			public void onSuccess(T result) {
				completable.complete(result);
			}

			@Override
			public void onFailure(Throwable t) {
				completable.completeExceptionally(t);
			}
		});
		return completable;
	}

	public static String getStackTrace() {
		final List<String> stackTrace = Stream
				.of(Thread.currentThread().getStackTrace())
				.map(StackTraceElement::toString)
				.collect(Collectors.toList());
		return "\n" + String.join("\n\t", stackTrace);
	}

	public static String getCaller() {
		return getCaller(FUNCTION_CALLER_NOT_FONUD);
	}

	public static String getCaller(final String failed) {
		final List<String> whitelist = Collections.singletonList("com.fp.example");
		final List<String> blacklist = Arrays.asList("com.fp.example.FpApplication", "com.fp.example.monad");
		try {
			return Arrays
					.stream(Thread.currentThread().getStackTrace())
					.filter(Objects::nonNull)
					.map(StackTraceElement::toString)
					.filter(s -> whitelist.stream().anyMatch(s::startsWith))
					.filter(s -> blacklist.stream().noneMatch(s::startsWith))
					.findFirst()
					.orElse(failed);
		} catch (Exception e) {
			log.error("Failed to get stack trace, ", e);
			return FUNCTION_CALLER_FAILED_TO_FIND;
		}
	}
}