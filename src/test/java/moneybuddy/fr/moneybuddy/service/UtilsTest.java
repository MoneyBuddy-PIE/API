package moneybuddy.fr.moneybuddy.service;

import moneybuddy.fr.moneybuddy.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe Utils
 */
class UtilsTest {

    private Utils utils;

    @BeforeEach
    void setUp() {
        utils = new Utils();
    }

    @Test
    void pagination_shouldCreatePageableWithAscendingSort() {
        // Given
        int page = 0;
        int size = 10;
        String sortBy = "createdAt";
        String sortDir = "asc";

        // When
        Pageable result = utils.pagination(page, size, sortBy, sortDir);

        // Then
        assertNotNull(result);
        assertEquals(page, result.getPageNumber());
        assertEquals(size, result.getPageSize());
        assertEquals(Sort.Direction.ASC, result.getSort().getOrderFor(sortBy).getDirection());
    }

    @Test
    void pagination_shouldCreatePageableWithDescendingSort() {
        // Given
        int page = 1;
        int size = 20;
        String sortBy = "updatedAt";
        String sortDir = "desc";

        // When
        Pageable result = utils.pagination(page, size, sortBy, sortDir);

        // Then
        assertNotNull(result);
        assertEquals(page, result.getPageNumber());
        assertEquals(size, result.getPageSize());
        assertEquals(Sort.Direction.DESC, result.getSort().getOrderFor(sortBy).getDirection());
    }

    @Test
    void pagination_shouldDefaultToAscendingWhenInvalidDirection() {
        // Given
        int page = 0;
        int size = 10;
        String sortBy = "name";
        String sortDir = "invalid";

        // When
        Pageable result = utils.pagination(page, size, sortBy, sortDir);

        // Then
        assertNotNull(result);
        assertEquals(Sort.Direction.ASC, result.getSort().getOrderFor(sortBy).getDirection());
    }
}
