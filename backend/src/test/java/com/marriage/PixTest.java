package com.marriage;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PixTest {
 @Test void matchesCentralBankPublishedVector(){
  assertEquals("00020126580014br.gov.bcb.pix0136123e4567-e12b-12d1-a456-4266554400005204000053039865802BR5913Fulano de Tal6008BRASILIA62070503***63041D3D",PixService.encode("123e4567-e12b-12d1-a456-426655440000","Fulano de Tal","BRASILIA",null,"***"));
 }
 @Test void keepsExactAmountAndNormalizesAccents(){
  Gift g=new Gift();g.id=2L;g.price=new BigDecimal("82.00");
  var pix=new PixService("lucascostateotonio@hotmail.com","Lucas Costa Teotônio","Esperança").payment(g);
  assertTrue(pix.payload().contains("540582.00"));
  assertTrue(pix.payload().contains("0130lucascostateotonio@hotmail.com"));
  assertEquals("LUCAS COSTA TEOTONIO",pix.recipient());assertEquals("ESPERANCA",pix.city());
  assertEquals("PRESENTE2",pix.reference());assertFalse(g.reserved);
 }
 @Test void refusesMissingPricesOrConfiguration(){
  Gift g=new Gift();g.id=1L;
  var service=new PixService("key","Name","City");
  assertThrows(org.springframework.web.server.ResponseStatusException.class,()->service.payment(g));
  g.price=new BigDecimal("110.00");g.reserved=true;
  assertThrows(org.springframework.web.server.ResponseStatusException.class,()->service.payment(g));
  g.reserved=false;
  assertThrows(org.springframework.web.server.ResponseStatusException.class,()->new PixService("key","","").payment(g));
  assertThrows(ArithmeticException.class,()->PixService.encode("key","NAME","CITY",new BigDecimal("1.001"),"ABC"));
 }
}
