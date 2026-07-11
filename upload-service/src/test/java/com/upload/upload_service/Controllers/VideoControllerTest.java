package com.upload.upload_service.Controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.upload.upload_service.DTO.CreateVideoRequest;
import com.upload.upload_service.DTO.VideoDto;
import com.upload.upload_service.Exceptions.VideoNotFoundException;
import com.upload.upload_service.Services.VideoService;

@WebMvcTest(VideoController.class)
class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VideoService videoService;

    @Test
    void uploadReturnsCreatedAndLocation() throws Exception {
        VideoDto response = new VideoDto();
        response.setId(42L);
        response.setTitle("Demo");
        when(videoService.saveUploadedVideo(any(CreateVideoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/videos/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Demo",
                                  "originalFileName": "demo.mp4",
                                  "storagePath": "videos/demo",
                                  "contentType": "video/mp4",
                                  "sizeInBytes": 1024
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/videos/42"))
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void uploadRejectsInvalidRequest() throws Exception {
        mockMvc.perform(post("/videos/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors.originalFileName").exists())
                .andExpect(jsonPath("$.fieldErrors.storagePath").exists())
                .andExpect(jsonPath("$.fieldErrors.contentType").exists())
                .andExpect(jsonPath("$.fieldErrors.sizeInBytes").exists());
    }

    @Test
    void getUnknownVideoReturnsNotFound() throws Exception {
        when(videoService.getVideo(99L)).thenThrow(new VideoNotFoundException(99L));

        mockMvc.perform(get("/videos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Video with id 99 was not found"));
    }

    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/videos/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request body is missing or malformed"));
    }

    @Test
    void invalidPathVariableReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/videos/not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid value for parameter id"));
    }

    @Test
    void updateRejectsUnknownStatus() throws Exception {
        mockMvc.perform(put("/videos/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Demo",
                                  "status": "UNKNOWN"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.status").exists());
    }
}
