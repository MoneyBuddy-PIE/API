package moneybuddy.fr.moneybuddy.dtos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ResponseDto
 */
class ResponseDtoTest {

    @Test
    void builder_shouldCreateValidResponseDto() {
        // Given
        String message = "Operation successful";

        // When
        ResponseDto response = ResponseDto.builder()
                .message(message)
                .build();

        // Then
        assertNotNull(response);
        assertEquals(message, response.getMessage());
    }

    @Test
    void constructor_shouldCreateValidResponseDto() {
        // Given
        String message = "Error occurred";

        // When
        ResponseDto response = new ResponseDto(message);

        // Then
        assertNotNull(response);
        assertEquals(message, response.getMessage());
    }

    @Test
    void setMessage_shouldUpdateMessage() {
        // Given
        ResponseDto response = new ResponseDto();
        String newMessage = "Updated message";

        // When
        response.setMessage(newMessage);

        // Then
        assertEquals(newMessage, response.getMessage());
    }
}
