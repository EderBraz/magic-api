package com.ederbraz.magicapi.controllers;

import com.ederbraz.magicapi.dto.PlayerDTO;
import com.ederbraz.magicapi.exceptions.DatabaseException;
import com.ederbraz.magicapi.factory.Factory;
import com.ederbraz.magicapi.services.PlayerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayerController.class)
class PlayerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    @Autowired
    private ObjectMapper objectMapper;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private String existingName;
    private String nonExistingName;
    private PlayerDTO playerDTO;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        existingName = "Test";
        nonExistingName = "invalid name";
        playerDTO = Factory.createPlayerDTO();
        PageImpl<PlayerDTO> page = new PageImpl<>(List.of(playerDTO));

        when(playerService.findAll(any())).thenReturn(page);

        when(playerService.findByName(existingName)).thenReturn(playerDTO);
        when(playerService.findByName(nonExistingName)).thenThrow(EntityNotFoundException.class);

        when(playerService.insert(any())).thenReturn(playerDTO);

        when(playerService.update(eq(existingId), any())).thenReturn(playerDTO);
        when(playerService.update(eq(nonExistingId), any())).thenThrow(EntityNotFoundException.class);

        doNothing().when(playerService).delete(existingId);
        doThrow(EntityNotFoundException.class).when(playerService).delete(nonExistingId);
        doThrow(DatabaseException.class).when(playerService).delete(dependentId);
    }

    @Test
    void findAllShouldReturnPage() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/players").accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
    }

    @Test
    void findByNameShouldReturnPlayerWhenNameExists() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/players/{name}", existingName).accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.name").exists());
    }

    @Test
    void findByNameShouldReturnNotFoundWhenNameDoesNotExist() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/players/{name}", nonExistingName).accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void insertShouldReturnPlayerDTOCreated() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(playerDTO);

        ResultActions resultActions = mockMvc.perform(post("/players")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isCreated());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.name").exists());
    }

    @Test
    void updateShouldReturnPlayerDTOWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(playerDTO);

        ResultActions resultActions = mockMvc.perform(put("/players/{id}", existingId).content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.name").exists());
    }

    @Test
    void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(playerDTO);

        ResultActions resultActions = mockMvc.perform(put("/players/{id}", nonExistingId).content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        ResultActions resultActions = mockMvc.perform(delete("/players/{id}", existingId).accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions resultActions = mockMvc.perform(delete("/players/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturnDatabaseErrorWhenIdIsDependent() throws Exception {
        ResultActions resultActions = mockMvc.perform(delete("/players/{id}", dependentId).accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isBadRequest());
    }
}
