package moneybuddy.fr.moneybuddy.model;

import moneybuddy.fr.moneybuddy.model.enums.TransactionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour l'entité Transaction
 */
class TransactionTest {

    @Test
    void builder_shouldCreateValidTransaction() {
        // Given
        String accountId = "account123";
        String childId = "child123";
        String amount = "50.00";
        TransactionType type = TransactionType.CREDIT;
        String description = "Weekly allowance";

        // When
        Transaction transaction = Transaction.builder()
                .accountId(accountId)
                .childId(childId)
                .amount(amount)
                .type(type)
                .description(description)
                .build();

        // Then
        assertNotNull(transaction);
        assertEquals(accountId, transaction.getAccountId());
        assertEquals(childId, transaction.getChildId());
        assertEquals(amount, transaction.getAmount());
        assertEquals(type, transaction.getType());
        assertEquals(description, transaction.getDescription());
        assertNotNull(transaction.getCreatedAt());
    }

    @Test
    void defaultCreatedAt_shouldBeSetAutomatically() {
        // When
        Transaction transaction = Transaction.builder()
                .accountId("account123")
                .build();

        // Then
        assertNotNull(transaction.getCreatedAt());
        assertTrue(transaction.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(transaction.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        // Given
        Transaction transaction = new Transaction();
        String id = "trans123";
        String accountId = "account456";
        String parentId = "parent789";

        // When
        transaction.setId(id);
        transaction.setAccountId(accountId);
        transaction.setParentId(parentId);

        // Then
        assertEquals(id, transaction.getId());
        assertEquals(accountId, transaction.getAccountId());
        assertEquals(parentId, transaction.getParentId());
    }
}
