package com.marriage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:pix;MODE=PostgreSQL","ADMIN_PASSWORD=test-password"}) @AutoConfigureMockMvc @Transactional
class PixIntegrationTest {
 @Autowired MockMvc mvc;
 @Test void servesPricesAndDoesNotReserveOrAcceptClientAmount()throws Exception{
  mvc.perform(get("/api/gifts")).andExpect(status().isOk()).andExpect(jsonPath("$[0].price").value(110.0)).andExpect(jsonPath("$[1].price").value(82.0));
  mvc.perform(get("/api/gifts/2/pix?amount=0.01")).andExpect(status().isOk()).andExpect(header().string("Cache-Control","no-store")).andExpect(jsonPath("$.amount").value(82.0)).andExpect(jsonPath("$.key").value("lucascostateotonio@hotmail.com"));
  mvc.perform(get("/api/gifts")).andExpect(jsonPath("$[1].reserved").value(false));
  mvc.perform(get("/api/gifts/99999/pix")).andExpect(status().isNotFound());
 }
 @Test void priceChangesRequireAdminAndCsrf()throws Exception{
  mvc.perform(patch("/api/gifts/2/price").with(csrf()).contentType("application/json").content("{\"price\":1}")).andExpect(status().isUnauthorized());
  mvc.perform(patch("/api/gifts/2/price").with(httpBasic("admin","test-password")).contentType("application/json").content("{\"price\":1}")).andExpect(status().isForbidden());
  mvc.perform(patch("/api/gifts/2/price").with(httpBasic("admin","test-password")).with(csrf()).contentType("application/json").content("{\"price\":-1}")).andExpect(status().isBadRequest());
  mvc.perform(patch("/api/gifts/2/price").with(httpBasic("admin","test-password")).with(csrf()).contentType("application/json").content("{\"price\":82.50}")).andExpect(status().isOk());
  mvc.perform(get("/api/gifts/2/pix")).andExpect(jsonPath("$.amount").value(82.5));
 }
}
