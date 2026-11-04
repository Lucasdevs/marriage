package com.marriage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:test;MODE=PostgreSQL","ADMIN_PASSWORD=test-password"}) @AutoConfigureMockMvc
class GiftTest {
 @Autowired MockMvc mvc;
 @Test void reservationsArePersistentAndPrivate()throws Exception{
  mvc.perform(post("/api/gifts/1/reserve").contentType("application/json").content("{\"guest\":\"Ana Teste\",\"message\":\"Felicidades\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.reserved").value(true)).andExpect(jsonPath("$.guestName").doesNotExist());
  mvc.perform(post("/api/gifts/1/reserve").contentType("application/json").content("{\"guest\":\"Outra pessoa\"}")).andExpect(status().isConflict());
  mvc.perform(get("/api/gifts")).andExpect(status().isOk()).andExpect(jsonPath("$[0].reserved").value(true)).andExpect(jsonPath("$[0].message").doesNotExist());
 }
 @Test void validatesAndProtectsCreation()throws Exception{
  mvc.perform(post("/api/gifts/2/reserve").contentType("application/json").content("{\"guest\":\" \"}")).andExpect(status().isBadRequest());
  mvc.perform(post("/api/gifts").with(csrf()).contentType("application/json").content("{\"name\":\"Vaso\",\"description\":\"Para flores\",\"category\":\"Decoração\"}")).andExpect(status().isUnauthorized());
  mvc.perform(post("/api/gifts").with(httpBasic("admin","test-password")).with(csrf()).contentType("application/json").content("{\"name\":\"Vaso\",\"description\":\"Para flores\",\"category\":\"Decoração\"}")).andExpect(status().isCreated());
 }
}
