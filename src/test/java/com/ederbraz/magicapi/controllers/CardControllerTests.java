package com.ederbraz.magicapi.controllers;

import com.ederbraz.magicapi.dto.CardDTO;
import com.ederbraz.magicapi.exceptions.DatabaseException;
import com.ederbraz.magicapi.factory.Factory;
import com.ederbraz.magicapi.services.CardService;
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

@WebMvcTest(CardController.class)
class CardControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @Autowired
    private ObjectMapper objectMapper;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private String existingName;
    private String nonExistingName;
    private CardDTO dto;

    @BeforeEach
    void setUp() throws Exception {
        Long existingId = 1L;
        Long nonExistingId = 2L;
        Long dependentId = 3L;

        CardDTO cardDTO = Factory.createCardDTO();
        PageImpl<CardDTO> page = new PageImpl<>(List.of(cardDTO));

        when(cardService.findAll(any())).thenReturn(page);

        when(cardService.findById(existingId)).thenReturn(cardDTO);
        when(cardService.findById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        when(cardService.insert(any())).thenReturn(cardDTO);

        when(cardService.update(eq(existingId), any())).thenReturn(cardDTO);
        when(cardService.update(eq(nonExistingId), any())).thenThrow(EntityNotFoundException.class);

        doNothing().when(cardService).delete(existingId);
        doThrow(EntityNotFoundException.class).when(cardService).delete(nonExistingId);
        doThrow(DatabaseException.class).when(cardService).delete(dependentId);
    }

    @Test
    void findAllShouldReturnPage() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/cards").accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
    }

    @Test
    void findByIdShouldReturnCardWhenIdExists() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/cards/{id}", existingId).accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.name").exists());
        resultActions.andExpect(jsonPath("$.edition").exists());
        resultActions.andExpect(jsonPath("$.language").exists());
        resultActions.andExpect(jsonPath("$.foil").exists());
        resultActions.andExpect(jsonPath("$.price").exists());
        resultActions.andExpect(jsonPath("$.quantity").exists());
    }

    @Test
    void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/cards/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void findByNameShouldReturnCardWhenNameExists() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/cards/{name}", existingName).accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.name").exists());
        resultActions.andExpect(jsonPath("$.edition").exists());
        resultActions.andExpect(jsonPath("$.language").exists());
        resultActions.andExpect(jsonPath("$.foil").exists());
        resultActions.andExpect(jsonPath("$.price").exists());
        resultActions.andExpect(jsonPath("$.quantity").exists());
    }

    @Test
    void findByNameShouldReturnNotFoundWhenNameDoesNotExist() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/cards/{name}", nonExistingName).accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void insertShouldReturnCardDTOCreated() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions resultActions = mockMvc.perform(post("/cards").content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.name").exists());
        resultActions.andExpect(jsonPath("$.edition").exists());
        resultActions.andExpect(jsonPath("$.language").exists());
        resultActions.andExpect(jsonPath("$.foil").exists());
        resultActions.andExpect(jsonPath("$.price").exists());
        resultActions.andExpect(jsonPath("$.quantity").exists());
    }

    @Test
    void updateShouldReturnCardDTOWhenIdExists() throws Exception{
        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions resultActions = mockMvc.perform(put("/cards/{id}", existingId).content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.name").exists());
        resultActions.andExpect(jsonPath("$.edition").exists());
        resultActions.andExpect(jsonPath("$.language").exists());
        resultActions.andExpect(jsonPath("$.foil").exists());
        resultActions.andExpect(jsonPath("$.price").exists());
        resultActions.andExpect(jsonPath("$.quantity").exists());
    }

    @Test
    void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions resultActions = mockMvc.perform(put("/cards/{id}", nonExistingId).content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        ResultActions resultActions = mockMvc.perform(delete("/cards/{id}", existingId).accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions resultActions = mockMvc.perform(delete("/cards/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }
}
