package moneybuddy.fr.moneybuddy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests d'intégration pour l'application MoneyBuddy
 * Ces tests vérifient le bon fonctionnement de l'API avec tous les composants
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MoneybuddyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Teste que l'endpoint Swagger UI est accessible
     */
    @Test
    void swaggerUiShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Teste que l'endpoint API docs est accessible
     */
    @Test
    void apiDocsShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }
}
