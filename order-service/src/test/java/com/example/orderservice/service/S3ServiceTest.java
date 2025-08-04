package com.example.orderservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    private S3Service s3Service;

    private final String bucketName = "test-bucket";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        s3Service = new S3Service(s3Client, bucketName);
    }

    @Test
    public void testUploadFile() throws IOException {
        // Prepare test data
        String key = "invoices/123/invoice.pdf";
        MockMultipartFile file = new MockMultipartFile(
                "invoice",
                "invoice.pdf",
                "application/pdf",
                "PDF content".getBytes()
        );

        // Mock S3 client response
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // Call the service method
        String result = s3Service.uploadFile(key, file);

        // Verify the result
        String expectedUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
        assertEquals(expectedUrl, result);

        // Capture the request arguments
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
        verify(s3Client, times(1)).putObject(requestCaptor.capture(), bodyCaptor.capture());

        // Verify the request
        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertEquals(bucketName, capturedRequest.bucket());
        assertEquals(key, capturedRequest.key());
        assertEquals(file.getContentType(), capturedRequest.contentType());
    }

    @Test
    public void testDownloadFile() throws IOException {
        // Prepare test data
        String key = "invoices/123/invoice.pdf";
        byte[] fileContent = "PDF content".getBytes();

        // Create a mock response input stream
        ResponseInputStream<GetObjectResponse> responseStream = new ResponseInputStream<>(
                GetObjectResponse.builder()
                        .contentType("application/pdf")
                        .contentLength((long) fileContent.length)
                        .build(),
                new ByteArrayInputStream(fileContent)
        );

        // Mock S3 client response
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseStream);

        // Call the service method
        byte[] result = s3Service.downloadFile(key);

        // Verify the result
        assertArrayEquals(fileContent, result);

        // Capture the request argument
        ArgumentCaptor<GetObjectRequest> requestCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client, times(1)).getObject(requestCaptor.capture());

        // Verify the request
        GetObjectRequest capturedRequest = requestCaptor.getValue();
        assertEquals(bucketName, capturedRequest.bucket());
        assertEquals(key, capturedRequest.key());
    }
}